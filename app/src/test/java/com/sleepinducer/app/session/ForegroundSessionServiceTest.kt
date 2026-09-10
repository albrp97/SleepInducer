package com.sleepinducer.app.session

import com.sleepinducer.app.breathing.SessionDuration
import org.junit.Assert.assertEquals
import org.junit.Test

class ForegroundSessionServiceTest {
    @Test
    fun startsOnlyFromExplicitAction() {
        assertEquals(
            SessionStartResult.Accepted(
                SessionStartRequest(SessionDuration.DEFAULT),
            ),
            ForegroundSessionCommandParser.parse(
                action = BreathingSessionService.ACTION_START,
                durationName = SessionDuration.DEFAULT.name,
            ),
        )
        assertEquals(
            SessionStartResult.Rejected(
                SessionStartRejection.UNSUPPORTED_ACTION,
            ),
            ForegroundSessionCommandParser.parse(
                action = "unexpected",
                durationName = SessionDuration.DEFAULT.name,
            ),
        )
    }

    @Test
    fun rejectsInvalidStartConfiguration() {
        assertEquals(
            SessionStartResult.Rejected(
                SessionStartRejection.MISSING_DURATION,
            ),
            ForegroundSessionCommandParser.parse(
                action = BreathingSessionService.ACTION_START,
                durationName = null,
            ),
        )
        assertEquals(
            SessionStartResult.Rejected(
                SessionStartRejection.UNSUPPORTED_DURATION,
            ),
            ForegroundSessionCommandParser.parse(
                action = BreathingSessionService.ACTION_START,
                durationName = "UNKNOWN",
            ),
        )
    }

    @Test
    fun stopsAndCleansUpIdempotently() {
        val stateMachine = ForegroundSessionStateMachine()

        stateMachine.activate(SessionDuration.DEFAULT)
        stateMachine.stop()
        stateMachine.stop()

        assertEquals(ForegroundSessionState.STOPPED, stateMachine.state)
        assertEquals(null, stateMachine.activeDuration)
    }

    @Test
    fun interruptsActiveSessionWithoutLeavingDuration() {
        val stateMachine = ForegroundSessionStateMachine()

        stateMachine.activate(SessionDuration.DEFAULT)
        stateMachine.interrupt()

        assertEquals(ForegroundSessionState.INTERRUPTED, stateMachine.state)
        assertEquals(null, stateMachine.activeDuration)
    }
}
