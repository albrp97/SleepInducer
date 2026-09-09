package com.sleepinducer.app.haptics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HapticAdapterTest {
    @Test
    fun reportsSupportedCapability() {
        val adapter = HapticAdapter(
            RecordingVibratorGateway(
                hasVibrator = true,
                hasAmplitudeControl = true,
            ),
        )

        assertEquals(HapticCapability.Usable, adapter.capability())
    }

    @Test
    fun deliversOneConfiguredCue() {
        val gateway = RecordingVibratorGateway(
            hasVibrator = true,
            hasAmplitudeControl = true,
        )
        val adapter = HapticAdapter(gateway)
        val cue = HapticCue.DefaultPhaseTransition

        assertEquals(HapticDelivery.Delivered, adapter.deliver(cue))
        assertEquals(listOf(cue), gateway.emittedCues)
        assertTrue(cue.durationMillis <= HapticCue.MAX_DURATION_MILLIS)
        assertTrue(cue.amplitude <= HapticCue.MAX_AMPLITUDE)
    }

    @Test
    fun rejectsUnavailableCapabilities() {
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
    fun cancelsIdempotently() {
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
