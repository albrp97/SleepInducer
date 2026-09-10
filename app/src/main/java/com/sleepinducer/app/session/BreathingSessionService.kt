package com.sleepinducer.app.session

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import com.sleepinducer.app.R

class BreathingSessionService : Service() {
    companion object {
        const val ACTION_START = ForegroundSessionActions.ACTION_START
        const val ACTION_STOP = ForegroundSessionActions.ACTION_STOP
        const val EXTRA_DURATION = ForegroundSessionActions.EXTRA_DURATION
        const val NOTIFICATION_CHANNEL_ID = "breathing_session"
        private const val NOTIFICATION_ID = 1001
    }

    private val stateMachine = ForegroundSessionStateMachine()
    private val binder = LocalBinder()

    @Volatile
    var isForegroundOwned: Boolean = false
        private set

    val lifecycleState: ForegroundSessionState
        get() = stateMachine.state

    val activeDuration
        get() = stateMachine.activeDuration

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        when (intent?.action) {
            ACTION_START -> handleStart(intent)
            ACTION_STOP -> stopSession()
            else -> failSession()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        stateMachine.interrupt()
        releaseForeground()
        super.onDestroy()
    }

    inner class LocalBinder : android.os.Binder() {
        fun service(): BreathingSessionService = this@BreathingSessionService
    }

    private fun handleStart(intent: Intent) {
        when (
            val result = ForegroundSessionCommandParser.parse(
                action = intent.action,
                durationName = intent.getStringExtra(EXTRA_DURATION),
            )
        ) {
            is SessionStartResult.Accepted -> activateSession(result.request)
            is SessionStartResult.Rejected -> failSession()
        }
    }

    private fun activateSession(request: SessionStartRequest) {
        if (lifecycleState == ForegroundSessionState.ACTIVE) {
            return
        }

        try {
            createNotificationChannel()
            startForegroundCompat()
            stateMachine.activate(request.duration)
        } catch (_: SecurityException) {
            failSession()
        } catch (_: IllegalArgumentException) {
            failSession()
        }
    }

    private fun stopSession() {
        stateMachine.stop()
        releaseForeground()
        stopSelf()
    }

    private fun failSession() {
        stateMachine.fail()
        releaseForeground()
        stopSelf()
    }

    private fun startForegroundCompat() {
        val notification = Notification.Builder(
            this,
            NOTIFICATION_CHANNEL_ID,
        )
            .setContentTitle(getString(R.string.service_notification_title))
            .setContentText(getString(R.string.service_notification_active))
            .setSmallIcon(R.drawable.ic_sleep_inducer)
            .setOngoing(true)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
        isForegroundOwned = true
    }

    private fun createNotificationChannel() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.service_notification_channel),
                NotificationManager.IMPORTANCE_LOW,
            ),
        )
    }

    private fun releaseForeground() {
        if (!isForegroundOwned) {
            return
        }

        stopForeground(STOP_FOREGROUND_REMOVE)
        isForegroundOwned = false
    }
}
