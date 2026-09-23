package com.sleepinducer.app.breathing

import com.sleepinducer.app.haptics.HapticAdapter
import com.sleepinducer.app.haptics.HapticCue
import com.sleepinducer.app.haptics.HapticDelivery
import com.sleepinducer.app.haptics.VibratorGateway
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PhaseHapticCoordinatorTest {
    @Test
    fun mapsEachPhaseToDistinctCue() {
        val gateway = RecordingVibratorGateway()
        val coordinator = PhaseHapticCoordinator(HapticAdapter(gateway))

        assertEquals(
            HapticDelivery.Delivered,
            coordinator.onSessionStateChanged(active(BreathingPhase.INHALE)),
        )
        assertEquals(
            HapticDelivery.Delivered,
            coordinator.onSessionStateChanged(active(BreathingPhase.EXHALE)),
        )

        assertEquals(2, gateway.emittedCues.size)
        assertNotEquals(gateway.emittedCues[0], gateway.emittedCues[1])
    }

    @Test
    fun deduplicatesRepeatedPhaseStates() {
        val gateway = RecordingVibratorGateway()
        val coordinator = PhaseHapticCoordinator(HapticAdapter(gateway))

        coordinator.onSessionStateChanged(active(BreathingPhase.INHALE))
        coordinator.onSessionStateChanged(active(BreathingPhase.INHALE, 100L))
        coordinator.onSessionStateChanged(active(BreathingPhase.EXHALE, 6_000L))
        coordinator.onSessionStateChanged(active(BreathingPhase.EXHALE, 5_100L))

        assertEquals(2, gateway.emittedCues.size)
    }

    @Test
    fun reportsUnavailableDelivery() {
        val gateway = RecordingVibratorGateway(hasVibrator = false)
        val coordinator = PhaseHapticCoordinator(HapticAdapter(gateway))

        assertEquals(
            HapticDelivery.Unavailable(
                com.sleepinducer.app.haptics.HapticLimitation.NO_VIBRATOR,
            ),
            coordinator.onSessionStateChanged(active(BreathingPhase.INHALE)),
        )
        assertTrue(gateway.emittedCues.isEmpty())
    }

    @Test
    fun reportsRejectedDelivery() {
        val gateway = RecordingVibratorGateway()
        val coordinator = PhaseHapticCoordinator(
            adapter = HapticAdapter(gateway),
            cueFor = { HapticCue(durationMillis = 0L, amplitude = 1) },
        )

        assertEquals(
            HapticDelivery.Rejected(
                com.sleepinducer.app.haptics.HapticCueRejection.DURATION_OUT_OF_RANGE,
            ),
            coordinator.onSessionStateChanged(active(BreathingPhase.INHALE)),
        )
        assertTrue(gateway.emittedCues.isEmpty())
    }

    @Test
    fun cancelsOnEveryTerminalStateAndRejectsStaleCues() {
        listOf(
            SessionState.Stopped,
            SessionState.Interrupted,
            SessionState.Completed,
        ).forEach { terminalState ->
            val gateway = RecordingVibratorGateway()
            val coordinator = PhaseHapticCoordinator(HapticAdapter(gateway))

            coordinator.onSessionStateChanged(active(BreathingPhase.INHALE))
            coordinator.onSessionStateChanged(terminalState)
            coordinator.onSessionStateChanged(terminalState)
            coordinator.onSessionStateChanged(active(BreathingPhase.EXHALE, 6_000L))

            assertEquals(1, gateway.cancellationCount)
            assertEquals(1, gateway.emittedCues.size)
            assertTrue(gateway.activeCue == null)
        }
    }

    private fun active(
        phase: BreathingPhase,
        elapsedMillis: Long = 0L,
    ): SessionState.Active =
        SessionState.Active(phase = phase, elapsedMillis = elapsedMillis)

    private class RecordingVibratorGateway(
        override val hasVibrator: Boolean = true,
        override val hasAmplitudeControl: Boolean = true,
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
