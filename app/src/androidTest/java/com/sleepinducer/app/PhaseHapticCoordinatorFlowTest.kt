package com.sleepinducer.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sleepinducer.app.breathing.BreathingPhase
import com.sleepinducer.app.breathing.BreathingProtocol
import com.sleepinducer.app.breathing.BreathingSessionRunner
import com.sleepinducer.app.breathing.SessionSchedule
import com.sleepinducer.app.breathing.SessionScheduler
import com.sleepinducer.app.breathing.SessionState
import com.sleepinducer.app.breathing.PhaseHapticCoordinator
import com.sleepinducer.app.haptics.HapticAdapter
import com.sleepinducer.app.haptics.HapticCue
import com.sleepinducer.app.haptics.HapticDelivery
import com.sleepinducer.app.haptics.HapticLimitation
import com.sleepinducer.app.haptics.VibratorGateway
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PhaseHapticCoordinatorFlowTest {
    private val appContext = InstrumentationRegistry
        .getInstrumentation()
        .targetContext

    @Test
    fun mapsInhaleAndExhaleToDistinctCues() {
        val gateway = RecordingVibratorGateway()
        val coordinator = PhaseHapticCoordinator(HapticAdapter(gateway))

        assertEquals(
            HapticDelivery.Delivered,
            coordinator.onSessionStateChanged(
                SessionState.Active(BreathingPhase.INHALE, 0L),
            ),
        )
        assertEquals(
            HapticDelivery.Delivered,
            coordinator.onSessionStateChanged(
                SessionState.Active(BreathingPhase.EXHALE, 6_000L),
            ),
        )

        assertEquals("com.sleepinducer.app", appContext.packageName)
        assertEquals(2, gateway.emittedCues.size)
        assertNotEquals(gateway.emittedCues[0], gateway.emittedCues[1])
    }

    @Test
    fun deliversOneCuePerPhaseTransition() {
        val gateway = RecordingVibratorGateway()
        val coordinator = PhaseHapticCoordinator(HapticAdapter(gateway))

        listOf(
            SessionState.Active(BreathingPhase.INHALE, 0L),
            SessionState.Active(BreathingPhase.INHALE, 100L),
            SessionState.Active(BreathingPhase.EXHALE, 6_000L),
            SessionState.Active(BreathingPhase.EXHALE, 5_100L),
        ).forEach(coordinator::onSessionStateChanged)

        assertEquals(2, gateway.emittedCues.size)
    }

    @Test
    fun reportsUnavailableHapticsWithoutSuccess() {
        val gateway = RecordingVibratorGateway(hasVibrator = false)
        val coordinator = PhaseHapticCoordinator(HapticAdapter(gateway))

        assertEquals(
            HapticDelivery.Unavailable(HapticLimitation.NO_VIBRATOR),
            coordinator.onSessionStateChanged(
                SessionState.Active(BreathingPhase.INHALE, 0L),
            ),
        )
        assertTrue(gateway.emittedCues.isEmpty())
    }

    @Test
    fun cancelsAndRejectsStaleTerminalCues() {
        val gateway = RecordingVibratorGateway()
        val coordinator = PhaseHapticCoordinator(HapticAdapter(gateway))
        val scheduler = FakeSessionScheduler()
        val runner = BreathingSessionRunner(
            protocol = BreathingProtocol(
                duration = com.sleepinducer.app.breathing.SessionDuration.SHORT,
            ),
            scheduler = scheduler,
            onStateChanged = coordinator::onSessionStateChanged,
        )

        runner.start()
        scheduler.advanceTo(6_000L)
        assertEquals(1, gateway.cancellationCount)
        runner.stop()
        scheduler.runPending()

        assertEquals(2, gateway.cancellationCount)
        assertEquals(2, gateway.emittedCues.size)
        assertTrue(gateway.activeCue == null)
    }

    private class FakeSessionScheduler : SessionScheduler {
        private var nowMillis = 0L
        private var nextId = 0
        private val scheduled = mutableListOf<ScheduledAction>()

        override fun nowMillis(): Long = nowMillis

        override fun scheduleAt(
            targetMillis: Long,
            action: () -> Unit,
        ): SessionSchedule {
            val scheduledAction = ScheduledAction(nextId++, targetMillis, action)
            scheduled += scheduledAction
            return SessionSchedule {
                scheduledAction.cancelled = true
            }
        }

        fun advanceTo(targetMillis: Long) {
            nowMillis = targetMillis
            runPending()
        }

        fun runPending() {
            val pending = scheduled
                .filter { !it.cancelled && it.targetMillis <= nowMillis }
                .sortedBy { it.targetMillis }
            pending.forEach { action ->
                action.cancelled = true
                action.action()
            }
        }

        private data class ScheduledAction(
            val id: Int,
            val targetMillis: Long,
            val action: () -> Unit,
            var cancelled: Boolean = false,
        )
    }

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
