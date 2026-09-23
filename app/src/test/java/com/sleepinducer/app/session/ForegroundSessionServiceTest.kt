package com.sleepinducer.app.session

import com.sleepinducer.app.breathing.SessionDuration
import com.sleepinducer.app.breathing.BreathingProtocolContract
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
    fun acceptsSerializedCustomDuration() {
        val customDuration = SessionDuration.Custom(15)

        assertEquals(
            SessionStartResult.Accepted(SessionStartRequest(customDuration)),
            ForegroundSessionCommandParser.parse(
                action = BreathingSessionService.ACTION_START,
                durationName = customDuration.name,
            ),
        )
    }

    @Test
    fun acceptsConfiguredPhaseDurations() {
        val result = ForegroundSessionCommandParser.parse(
            action = BreathingSessionService.ACTION_START,
            durationName = SessionDuration.DEFAULT.name,
            inhaleDurationMillis = 7_500L,
            exhaleDurationMillis = 4_500L,
        )

        assertEquals(
            SessionStartResult.Accepted(
                SessionStartRequest(
                    duration = SessionDuration.DEFAULT,
                    contract = BreathingProtocolContract(
                        inhaleDurationMillis = 7_500L,
                        exhaleDurationMillis = 4_500L,
                    ),
                ),
            ),
            result,
        )
    }

    @Test
    fun rejectsUnsupportedPhaseDurations() {
        assertEquals(
            SessionStartResult.Rejected(
                SessionStartRejection.UNSUPPORTED_TIMING,
            ),
            ForegroundSessionCommandParser.parse(
                action = BreathingSessionService.ACTION_START,
                durationName = SessionDuration.DEFAULT.name,
                inhaleDurationMillis = 7_250L,
                exhaleDurationMillis = 4_500L,
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
