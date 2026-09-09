package com.sleepinducer.app

import android.content.pm.PackageManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sleepinducer.app.haptics.AndroidVibratorGateway
import com.sleepinducer.app.haptics.HapticAdapter
import com.sleepinducer.app.haptics.HapticCapability
import com.sleepinducer.app.haptics.HapticCue
import com.sleepinducer.app.haptics.HapticDelivery
import com.sleepinducer.app.haptics.HapticLimitation
import com.sleepinducer.app.haptics.VibratorGateway
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HapticAdapterFlowTest {
    private val appContext = InstrumentationRegistry
        .getInstrumentation()
        .targetContext

    @Test
    fun reportsSupportedCapability() {
        val adapter = HapticAdapter(
            RecordingVibratorGateway(
                hasVibrator = true,
                hasAmplitudeControl = true,
            ),
        )

        assertEquals("com.sleepinducer.app", appContext.packageName)
        assertEquals(HapticCapability.Usable, adapter.capability())
    }

    @Test
    fun deliversAndCancelsCue() {
        val gateway = RecordingVibratorGateway(
            hasVibrator = true,
            hasAmplitudeControl = true,
        )
        val adapter = HapticAdapter(gateway)

        assertEquals(
            HapticDelivery.Delivered,
            adapter.deliver(HapticCue.DefaultPhaseTransition),
        )
        adapter.cancel()

        assertEquals(1, gateway.emittedCues.size)
        assertEquals(1, gateway.cancellationCount)
        assertTrue(gateway.activeCue == null)
    }

    @Test
    fun reportsUnavailableHardware() {
        val noVibratorGateway = RecordingVibratorGateway(
            hasVibrator = false,
            hasAmplitudeControl = true,
        )
        val noAmplitudeGateway = RecordingVibratorGateway(
            hasVibrator = true,
            hasAmplitudeControl = false,
        )

        assertEquals(
            HapticDelivery.Unavailable(HapticLimitation.NO_VIBRATOR),
            HapticAdapter(noVibratorGateway).deliver(HapticCue.DefaultPhaseTransition),
        )
        assertEquals(
            HapticDelivery.Unavailable(HapticLimitation.AMPLITUDE_CONTROL_UNAVAILABLE),
            HapticAdapter(noAmplitudeGateway).deliver(HapticCue.DefaultPhaseTransition),
        )
        assertTrue(noVibratorGateway.emittedCues.isEmpty())
        assertTrue(noAmplitudeGateway.emittedCues.isEmpty())
    }

    @Test
    fun cancelsWithoutStaleDelivery() {
        val gateway = RecordingVibratorGateway(
            hasVibrator = true,
            hasAmplitudeControl = true,
        )
        val adapter = HapticAdapter(gateway)

        adapter.deliver(HapticCue.DefaultPhaseTransition)
        adapter.cancel()
        adapter.cancel()

        assertEquals(1, gateway.cancellationCount)
        assertTrue(gateway.activeCue == null)
        assertEquals(1, gateway.emittedCues.size)
    }

    @Test
    fun reportsRealDeviceCapabilityWithoutClaimingUnsupportedDelivery() {
        val adapter = HapticAdapter(AndroidVibratorGateway(appContext))

        when (val capability = adapter.capability()) {
            HapticCapability.Usable -> {
                assertEquals(
                    HapticDelivery.Delivered,
                    adapter.deliver(HapticCue.DefaultPhaseTransition),
                )
                adapter.cancel()
            }

            is HapticCapability.Unavailable -> {
                assertEquals(
                    HapticDelivery.Unavailable(capability.reason),
                    adapter.deliver(HapticCue.DefaultPhaseTransition),
                )
            }
        }
    }

    @Test
    fun manifestDeclaresVibratePermission() {
        @Suppress("DEPRECATION")
        val permissions = appContext.packageManager
            .getPackageInfo(appContext.packageName, PackageManager.GET_PERMISSIONS)
            .requestedPermissions
            .orEmpty()

        assertTrue("android.permission.VIBRATE" in permissions.toSet())
    }

    private class RecordingVibratorGateway(
        override val hasVibrator: Boolean,
        override val hasAmplitudeControl: Boolean,
    ) : VibratorGateway {
        val emittedCues = mutableListOf<HapticCue>()
        var cancellationCount = 0
            private set
        var activeCue: HapticCue? = null
            private set

        override fun vibrate(cue: HapticCue) {
            emittedCues += cue
            activeCue = cue
        }

        override fun cancel() {
            cancellationCount += 1
            activeCue = null
        }
    }
}
