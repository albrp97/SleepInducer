package com.sleepinducer.app.breathing

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class BreathingProtocolTest {
    @Test
    fun alternatesFiveSecondPhases() {
        val session = BreathingProtocol(SessionDuration.DEFAULT).newSession()

        assertEquals(
            SessionState.Active(BreathingPhase.INHALE, 0L),
            session.advanceTo(0L).state,
        )
        assertEquals(
            SessionState.Active(BreathingPhase.INHALE, 4_999L),
            session.advanceTo(4_999L).state,
        )
        assertEquals(
            SessionState.Active(BreathingPhase.EXHALE, 5_000L),
            session.advanceTo(5_000L).state,
        )
        assertEquals(
            SessionState.Active(BreathingPhase.EXHALE, 9_999L),
            session.advanceTo(9_999L).state,
        )
        assertEquals(
            SessionState.Active(BreathingPhase.INHALE, 10_000L),
            session.advanceTo(10_000L).state,
        )
    }

    @Test
    fun completesExactlyOnceAtDuration() {
        val session = BreathingProtocol(SessionDuration.DEFAULT).newSession()

        val completed = session.advanceTo(SessionDuration.DEFAULT.totalMillis)

        assertEquals(SessionState.Completed, completed.state)
        assertSame(
            completed,
            completed.advanceTo(SessionDuration.DEFAULT.totalMillis + 1L),
        )
    }

    @Test
    fun stopsAndInterruptsWithoutCompletion() {
        val protocol = BreathingProtocol(SessionDuration.DEFAULT)
        val stopped = protocol.newSession().advanceTo(1_000L).stop()
        val interrupted = protocol.newSession().advanceTo(1_000L).interrupt()

        assertEquals(SessionState.Stopped, stopped.state)
        assertSame(stopped, stopped.advanceTo(SessionDuration.DEFAULT.totalMillis))
        assertEquals(SessionState.Interrupted, interrupted.state)
        assertSame(interrupted, interrupted.advanceTo(SessionDuration.DEFAULT.totalMillis))
    }

    @Test
    fun rejectsTimeRegression() {
        val session = BreathingProtocol(SessionDuration.DEFAULT)
            .newSession()
            .advanceTo(5_000L)

        try {
            session.advanceTo(4_999L)
            error("Expected a monotonic-time validation failure.")
        } catch (error: IllegalArgumentException) {
            assertEquals("Elapsed time must be monotonic.", error.message)
        }
    }

    @Test
    fun usesNaturalBreathingWithoutHold() {
        val protocol = BreathingProtocol(SessionDuration.DEFAULT)

        assertEquals(5_000L, protocol.contract.phaseDurationMillis)
        assertEquals(0L, protocol.contract.requiredHoldDurationMillis)
        assertEquals(BreathingDepth.NATURAL, protocol.contract.depthGuidance)
        assertEquals(
            setOf(BreathingPhase.INHALE, BreathingPhase.EXHALE),
            setOf(
                (protocol.newSession().advanceTo(0L).state as SessionState.Active).phase,
                (protocol.newSession().advanceTo(5_000L).state as SessionState.Active).phase,
            ),
        )
    }
}
