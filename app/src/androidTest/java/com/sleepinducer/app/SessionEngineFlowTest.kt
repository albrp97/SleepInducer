package com.sleepinducer.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sleepinducer.app.breathing.BreathingPhase
import com.sleepinducer.app.breathing.BreathingSessionEngine
import com.sleepinducer.app.breathing.SessionDuration
import com.sleepinducer.app.breathing.SessionSchedule
import com.sleepinducer.app.breathing.SessionScheduler
import com.sleepinducer.app.breathing.SessionState
import com.sleepinducer.app.haptics.HapticAdapter
import com.sleepinducer.app.haptics.HapticCue
import com.sleepinducer.app.haptics.HapticDelivery
import com.sleepinducer.app.haptics.VibratorGateway
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SessionEngineFlowTest {
    @Test
    fun completesAndCancelsFutureCuesThroughPackagedEngine() {
        val scheduler = FakeSessionScheduler()
        val gateway = RecordingVibratorGateway()
        val states = mutableListOf<SessionState>()
        val deliveries = mutableListOf<HapticDelivery?>()
        val engine = BreathingSessionEngine(
            duration = SessionDuration.SHORT,
            scheduler = scheduler,
            adapter = HapticAdapter(gateway),
        ) { state, delivery ->
            states += state
            deliveries += delivery
        }

        engine.start()
        scheduler.runAt(5_000L)
        scheduler.runAt(SessionDuration.SHORT.totalMillis)

        assertEquals(
            SessionState.Active(BreathingPhase.EXHALE, 5_000L),
            states[1],
        )
        assertEquals(SessionState.Completed, states.last())
        assertEquals(
            listOf(
                HapticDelivery.Delivered,
                HapticDelivery.Delivered,
                null,
            ),
            deliveries,
        )
        assertEquals(2, gateway.emittedCues.size)
        assertTrue(gateway.cancellationCount >= 1)
    }

    private class FakeSessionScheduler : SessionScheduler {
        private var currentTimeMillis = 0L
        private val scheduled = mutableListOf<ScheduledAction>()

        override fun nowMillis(): Long = currentTimeMillis

        override fun scheduleAt(
            targetMillis: Long,
            action: () -> Unit,
        ): SessionSchedule {
            val scheduledAction = ScheduledAction(targetMillis, action)
            scheduled += scheduledAction
            return SessionSchedule {
                scheduledAction.cancelled = true
            }
        }

        fun runAt(timeMillis: Long) {
            currentTimeMillis = timeMillis
            val next = scheduled
                .filter { !it.cancelled && it.targetMillis <= timeMillis }
                .minByOrNull { it.targetMillis }
                ?: error("No scheduled session boundary is ready.")
            next.action()
        }

        private data class ScheduledAction(
            val targetMillis: Long,
            val action: () -> Unit,
            var cancelled: Boolean = false,
        )
    }

    private class RecordingVibratorGateway : VibratorGateway {
        override val hasVibrator: Boolean = true
        override val hasAmplitudeControl: Boolean = true
        val emittedCues = mutableListOf<HapticCue>()
        var cancellationCount = 0
            private set

        override fun vibrate(cue: HapticCue) {
            emittedCues += cue
        }

        override fun cancel() {
            cancellationCount += 1
        }
    }
}
