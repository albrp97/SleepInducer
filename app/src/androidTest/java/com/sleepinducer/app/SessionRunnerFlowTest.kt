package com.sleepinducer.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sleepinducer.app.breathing.BreathingPhase
import com.sleepinducer.app.breathing.BreathingProtocol
import com.sleepinducer.app.breathing.BreathingSessionRunner
import com.sleepinducer.app.breathing.SessionDuration
import com.sleepinducer.app.breathing.SessionSchedule
import com.sleepinducer.app.breathing.SessionScheduler
import com.sleepinducer.app.breathing.SessionState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SessionRunnerFlowTest {
    @Test
    fun startsWithInhaleAndAdvancesPhase() {
        val scheduler = FakeSessionScheduler()
        val states = mutableListOf<SessionState>()
        val runner = createRunner(scheduler, states)

        runner.start()
        scheduler.runLastAt(5_000L)

        assertEquals(
            listOf(
                SessionState.Active(BreathingPhase.INHALE, 0L),
                SessionState.Active(BreathingPhase.EXHALE, 5_000L),
            ),
            states,
        )
    }

    @Test
    fun reconcilesDelayedBoundary() {
        val scheduler = FakeSessionScheduler()
        val states = mutableListOf<SessionState>()
        val runner = createRunner(scheduler, states)

        runner.start()
        scheduler.runLastAt(5_250L)

        assertEquals(
            SessionState.Active(BreathingPhase.EXHALE, 5_250L),
            states.last(),
        )
        assertEquals(10_000L, scheduler.schedules.last().targetMillis)
    }

    @Test
    fun cancelsFutureTransitionsAfterStopAndInterruption() {
        val stoppedScheduler = FakeSessionScheduler()
        val stoppedStates = mutableListOf<SessionState>()
        val stoppedRunner = createRunner(stoppedScheduler, stoppedStates)
        stoppedRunner.start()
        val stoppedSchedule = stoppedScheduler.schedules.single()
        stoppedRunner.stop()
        stoppedScheduler.runAt(stoppedSchedule, 5_000L)

        val interruptedScheduler = FakeSessionScheduler()
        val interruptedStates = mutableListOf<SessionState>()
        val interruptedRunner = createRunner(interruptedScheduler, interruptedStates)
        interruptedRunner.start()
        val interruptedSchedule = interruptedScheduler.schedules.single()
        interruptedRunner.interrupt()
        interruptedScheduler.runAt(interruptedSchedule, 5_000L)

        assertEquals(SessionState.Stopped, stoppedStates.last())
        assertEquals(SessionState.Interrupted, interruptedStates.last())
        assertEquals(2, stoppedStates.size)
        assertEquals(2, interruptedStates.size)
        assertTrue(stoppedSchedule.cancelled)
        assertTrue(interruptedSchedule.cancelled)
    }

    @Test
    fun completesOnceAtDuration() {
        val scheduler = FakeSessionScheduler()
        val states = mutableListOf<SessionState>()
        val runner = createRunner(scheduler, states)

        runner.start()
        val firstSchedule = scheduler.schedules.single()
        scheduler.runAt(firstSchedule, SessionDuration.SHORT.totalMillis)
        val stateCountAtCompletion = states.size
        scheduler.runLastAt(SessionDuration.SHORT.totalMillis + 5_000L)

        assertEquals(SessionState.Completed, states.last())
        assertEquals(stateCountAtCompletion, states.size)
        assertTrue(firstSchedule.cancelled)
    }

    private fun createRunner(
        scheduler: FakeSessionScheduler,
        states: MutableList<SessionState>,
    ): BreathingSessionRunner =
        BreathingSessionRunner(
            protocol = BreathingProtocol(SessionDuration.SHORT),
            scheduler = scheduler,
            onStateChanged = states::add,
        )

    private class FakeSessionScheduler(
        private var currentTimeMillis: Long = 0L,
    ) : SessionScheduler {
        val schedules = mutableListOf<FakeSessionSchedule>()

        override fun nowMillis(): Long = currentTimeMillis

        override fun scheduleAt(
            targetMillis: Long,
            action: () -> Unit,
        ): SessionSchedule {
            return FakeSessionSchedule(targetMillis, action).also(schedules::add)
        }

        fun runLastAt(timeMillis: Long) {
            runAt(schedules.last(), timeMillis)
        }

        fun runAt(schedule: FakeSessionSchedule, timeMillis: Long) {
            currentTimeMillis = timeMillis
            schedule.action()
        }
    }

    private class FakeSessionSchedule(
        val targetMillis: Long,
        val action: () -> Unit,
    ) : SessionSchedule {
        var cancelled = false
            private set

        override fun cancel() {
            cancelled = true
        }
    }
}
