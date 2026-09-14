package com.sleepinducer.app.breathing

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BreathingSessionRunnerTest {
    @Test
    fun startsWithInhaleAndSchedulesNextBoundary() {
        val scheduler = FakeSessionScheduler()
        val states = mutableListOf<SessionState>()
        val runner = BreathingSessionRunner(
            protocol = BreathingProtocol(SessionDuration.SHORT),
            scheduler = scheduler,
            onStateChanged = states::add,
        )

        runner.start()

        assertEquals(
            listOf(SessionState.Active(BreathingPhase.INHALE, 0L)),
            states,
        )
        assertEquals(5_000L, scheduler.schedules.single().targetMillis)

        scheduler.runLastAt(5_000L)

        assertEquals(
            SessionState.Active(BreathingPhase.EXHALE, 5_000L),
            states.last(),
        )
    }

    @Test
    fun reconcilesLateCallbacksWithoutPhaseRegression() {
        val scheduler = FakeSessionScheduler()
        val states = mutableListOf<SessionState>()
        val runner = BreathingSessionRunner(
            protocol = BreathingProtocol(SessionDuration.SHORT),
            scheduler = scheduler,
            onStateChanged = states::add,
        )

        runner.start()
        scheduler.runLastAt(5_250L)

        assertEquals(
            SessionState.Active(BreathingPhase.EXHALE, 5_250L),
            states.last(),
        )
        assertEquals(10_000L, scheduler.schedules.last().targetMillis)
    }

    @Test
    fun stopsAndInterruptsWithoutStaleCallbacks() {
        val stoppedScheduler = FakeSessionScheduler()
        val stoppedStates = mutableListOf<SessionState>()
        val stoppedRunner = BreathingSessionRunner(
            protocol = BreathingProtocol(SessionDuration.SHORT),
            scheduler = stoppedScheduler,
            onStateChanged = stoppedStates::add,
        )
        stoppedRunner.start()
        val stoppedSchedule = stoppedScheduler.schedules.single()
        stoppedRunner.stop()
        stoppedScheduler.runAt(stoppedSchedule, 5_000L)

        val interruptedScheduler = FakeSessionScheduler()
        val interruptedStates = mutableListOf<SessionState>()
        val interruptedRunner = BreathingSessionRunner(
            protocol = BreathingProtocol(SessionDuration.SHORT),
            scheduler = interruptedScheduler,
            onStateChanged = interruptedStates::add,
        )
        interruptedRunner.start()
        val interruptedSchedule = interruptedScheduler.schedules.single()
        interruptedRunner.interrupt()
        interruptedScheduler.runAt(interruptedSchedule, 5_000L)

        assertEquals(
            listOf(
                SessionState.Active(BreathingPhase.INHALE, 0L),
                SessionState.Stopped,
            ),
            stoppedStates,
        )
        assertEquals(
            listOf(
                SessionState.Active(BreathingPhase.INHALE, 0L),
                SessionState.Interrupted,
            ),
            interruptedStates,
        )
        assertTrue(stoppedSchedule.cancelled)
        assertTrue(interruptedSchedule.cancelled)
    }

    @Test
    fun completesExactlyOnceAtDuration() {
        val scheduler = FakeSessionScheduler()
        val states = mutableListOf<SessionState>()
        val runner = BreathingSessionRunner(
            protocol = BreathingProtocol(SessionDuration.SHORT),
            scheduler = scheduler,
            onStateChanged = states::add,
        )

        runner.start()
        val firstSchedule = scheduler.schedules.single()
        scheduler.runAt(firstSchedule, SessionDuration.SHORT.totalMillis)
        val stateCountAtCompletion = states.size
        scheduler.runLastAt(SessionDuration.SHORT.totalMillis + 5_000L)

        assertEquals(SessionState.Completed, states.last())
        assertEquals(stateCountAtCompletion, states.size)
        assertTrue(firstSchedule.cancelled)
    }

    @Test
    fun doesNotScheduleAfterStartCallbackInterruptsSession() {
        val scheduler = FakeSessionScheduler()
        val states = mutableListOf<SessionState>()
        lateinit var runner: BreathingSessionRunner
        runner = BreathingSessionRunner(
            protocol = BreathingProtocol(SessionDuration.SHORT),
            scheduler = scheduler,
        ) { state ->
            states += state
            if (state is SessionState.Active) {
                runner.interrupt()
            }
        }

        runner.start()

        assertEquals(
            listOf(
                SessionState.Active(BreathingPhase.INHALE, 0L),
                SessionState.Interrupted,
            ),
            states,
        )
        assertTrue(scheduler.schedules.isEmpty())
    }

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
