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

    @Test
    fun reportsSystemDeliveryFailuresWithoutThrowing() {
        val gateway = RecordingVibratorGateway(
            hasVibrator = true,
            hasAmplitudeControl = true,
            failure = HapticDeliveryFailure.SYSTEM_REJECTED,
        )

        assertEquals(
            HapticDelivery.Failed(HapticDeliveryFailure.SYSTEM_REJECTED),
            HapticAdapter(gateway).deliver(HapticCue.DefaultPhaseTransition),
        )
        assertTrue(gateway.emittedCues.isEmpty())
    }

    @Test
    fun reportsCancellationFailuresWithoutThrowing() {
        val gateway = RecordingVibratorGateway(
            hasVibrator = true,
            hasAmplitudeControl = true,
            cancellationFailure = HapticDeliveryFailure.SECURITY_RESTRICTION,
        )
        val adapter = HapticAdapter(gateway)

        assertEquals(
            HapticDelivery.Delivered,
            adapter.deliver(HapticCue.DefaultPhaseTransition),
        )
        assertEquals(
            HapticDelivery.Failed(HapticDeliveryFailure.SECURITY_RESTRICTION),
            adapter.cancel(),
        )
    }

    private class RecordingVibratorGateway(
        override val hasVibrator: Boolean,
        override val hasAmplitudeControl: Boolean,
        private val failure: HapticDeliveryFailure? = null,
        private val cancellationFailure: HapticDeliveryFailure? = null,
    ) : VibratorGateway {
        val emittedCues = mutableListOf<HapticCue>()
        var cancellationCount = 0
            private set
        var activeCue: HapticCue? = null
            private set

        override fun vibrate(cue: HapticCue) {
            if (failure != null) {
                throw when (failure) {
                    HapticDeliveryFailure.SECURITY_RESTRICTION ->
                        SecurityException("test")
                    HapticDeliveryFailure.SYSTEM_REJECTED ->
                        IllegalStateException("test")
                }
            }
            emittedCues += cue
            activeCue = cue
        }

        override fun cancel() {
            cancellationCount += 1
            if (cancellationFailure != null) {
                throw when (cancellationFailure) {
                    HapticDeliveryFailure.SECURITY_RESTRICTION ->
                        SecurityException("test")
                    HapticDeliveryFailure.SYSTEM_REJECTED ->
                        IllegalStateException("test")
                }
            }
            activeCue = null
        }
    }
}
