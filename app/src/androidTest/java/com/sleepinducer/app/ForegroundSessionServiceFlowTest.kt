package com.sleepinducer.app

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sleepinducer.app.breathing.SessionDuration
import com.sleepinducer.app.session.BreathingSessionService
import com.sleepinducer.app.session.ForegroundSessionState
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class ForegroundSessionServiceFlowTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val appContext = InstrumentationRegistry
        .getInstrumentation()
        .targetContext
    private var activeBinding: ServiceBinding? = null

    @After
    fun tearDown() {
        activeBinding?.let { binding ->
            appContext.startService(
                Intent(appContext, BreathingSessionService::class.java).apply {
                    action = BreathingSessionService.ACTION_STOP
                },
            )
            appContext.unbindService(binding.connection)
        }
        appContext.stopService(
            Intent(appContext, BreathingSessionService::class.java),
        )
        activeBinding = null
    }

    @Test
    fun declaresSpecialUseServiceAndPermissions() {
        @Suppress("DEPRECATION")
        val permissions = appContext.packageManager
            .getPackageInfo(appContext.packageName, PackageManager.GET_PERMISSIONS)
            .requestedPermissions
            .orEmpty()
            .toSet()
        val serviceInfo = appContext.packageManager.getServiceInfo(
            ComponentName(appContext, BreathingSessionService::class.java),
            PackageManager.GET_META_DATA,
        )

        assertTrue("android.permission.FOREGROUND_SERVICE" in permissions)
        assertTrue("android.permission.FOREGROUND_SERVICE_SPECIAL_USE" in permissions)
        assertEquals(
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
            serviceInfo.foregroundServiceType,
        )
        assertFalse(serviceInfo.exported)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val property = appContext.packageManager.getProperty(
                "android.app.PROPERTY_SPECIAL_USE_FGS_SUBTYPE",
                ComponentName(appContext, BreathingSessionService::class.java),
            )
            assertTrue(property.getString()?.isNotBlank() == true)
        }
    }

    @Test
    fun startsForegroundServiceFromExplicitAction() {
        val binding = bindService()

        appContext.startForegroundService(startIntent(SessionDuration.DEFAULT))
        awaitState(binding.service, ForegroundSessionState.ACTIVE)

        assertTrue(binding.service.isForegroundOwned)
        val channel = appContext
            .getSystemService(android.app.NotificationManager::class.java)
            .getNotificationChannel(BreathingSessionService.NOTIFICATION_CHANNEL_ID)
        assertNotNull(channel)
        assertEquals(
            android.app.NotificationManager.IMPORTANCE_LOW,
            channel?.importance,
        )
    }

    @Test
    fun rejectsInvalidStartWithoutActiveState() {
        val binding = bindService()
        appContext.startForegroundService(
            Intent(appContext, BreathingSessionService::class.java).apply {
                action = BreathingSessionService.ACTION_START
            },
        )

        awaitState(binding.service, ForegroundSessionState.FAILED)

        assertFalse(binding.service.isForegroundOwned)
    }

    @Test
    fun stopsAndCleansUpService() {
        val binding = bindService()
        appContext.startForegroundService(startIntent(SessionDuration.DEFAULT))
        awaitState(binding.service, ForegroundSessionState.ACTIVE)

        appContext.startService(
            Intent(appContext, BreathingSessionService::class.java).apply {
                action = BreathingSessionService.ACTION_STOP
            },
        )
        awaitState(binding.service, ForegroundSessionState.STOPPED)

        assertFalse(binding.service.isForegroundOwned)
        assertEquals(null, binding.service.activeDuration)
    }

    private fun startIntent(duration: SessionDuration): Intent =
        Intent(appContext, BreathingSessionService::class.java).apply {
            action = BreathingSessionService.ACTION_START
            putExtra(BreathingSessionService.EXTRA_DURATION, duration.name)
        }

    private fun bindService(): ServiceBinding {
        val connected = CountDownLatch(1)
        lateinit var connection: ServiceConnection
        var service: BreathingSessionService? = null
        connection = object : ServiceConnection {
            override fun onServiceConnected(
                name: ComponentName?,
                binder: IBinder?,
            ) {
                service = (binder as BreathingSessionService.LocalBinder).service()
                connected.countDown()
            }

            override fun onServiceDisconnected(name: ComponentName?) = Unit
        }

        assertTrue(
            appContext.bindService(
                Intent(appContext, BreathingSessionService::class.java),
                connection,
                Context.BIND_AUTO_CREATE,
            ),
        )
        assertTrue(connected.await(5, TimeUnit.SECONDS))
        val binding = ServiceBinding(
            service = checkNotNull(service),
            connection = connection,
        )
        activeBinding = binding
        return binding
    }

    private fun awaitState(
        service: BreathingSessionService,
        expected: ForegroundSessionState,
    ) {
        val deadline = SystemClock.uptimeMillis() + 5_000L
        while (SystemClock.uptimeMillis() < deadline) {
            if (service.lifecycleState == expected) {
                return
            }
            Thread.sleep(50L)
        }
        assertEquals(expected, service.lifecycleState)
    }

    private data class ServiceBinding(
        val service: BreathingSessionService,
        val connection: ServiceConnection,
    )
}
