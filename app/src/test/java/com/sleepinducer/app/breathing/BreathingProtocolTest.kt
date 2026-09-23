package com.sleepinducer.app.breathing

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class BreathingProtocolTest {
    @Test
    fun alternatesSixSecondDefaultPhases() {
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
            SessionState.Active(BreathingPhase.INHALE, 5_999L),
            session.advanceTo(5_999L).state,
        )
        assertEquals(
            SessionState.Active(BreathingPhase.EXHALE, 6_000L),
            session.advanceTo(6_000L).state,
        )
        assertEquals(
            SessionState.Active(BreathingPhase.EXHALE, 11_999L),
            session.advanceTo(11_999L).state,
        )
        assertEquals(
            SessionState.Active(BreathingPhase.INHALE, 12_000L),
            session.advanceTo(12_000L).state,
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
            .advanceTo(6_000L)

        try {
            session.advanceTo(5_999L)
            error("Expected a monotonic-time validation failure.")
        } catch (error: IllegalArgumentException) {
            assertEquals("Elapsed time must be monotonic.", error.message)
        }
    }

    @Test
    fun usesNaturalBreathingWithoutHold() {
        val protocol = BreathingProtocol(SessionDuration.DEFAULT)

        assertEquals(6_000L, protocol.contract.inhaleDurationMillis)
        assertEquals(6_000L, protocol.contract.exhaleDurationMillis)
        assertEquals(0L, protocol.contract.requiredHoldDurationMillis)
        assertEquals(BreathingDepth.NATURAL, protocol.contract.depthGuidance)
        assertEquals(
            setOf(BreathingPhase.INHALE, BreathingPhase.EXHALE),
            setOf(
                (protocol.newSession().advanceTo(0L).state as SessionState.Active).phase,
                (protocol.newSession().advanceTo(6_000L).state as SessionState.Active).phase,
            ),
        )
    }

    @Test
    fun supportsIndependentHalfSecondPhaseDurations() {
        val protocol = BreathingProtocol(
            duration = SessionDuration.DEFAULT,
            contract = BreathingProtocolContract(
                inhaleDurationMillis = 7_500L,
                exhaleDurationMillis = 4_500L,
            ),
        )

        assertEquals(
            SessionState.Active(BreathingPhase.INHALE, 7_499L),
            protocol.newSession().advanceTo(7_499L).state,
        )
        assertEquals(
            SessionState.Active(BreathingPhase.EXHALE, 7_500L),
            protocol.newSession().advanceTo(7_500L).state,
        )
        assertEquals(
            SessionState.Active(BreathingPhase.INHALE, 12_000L),
            protocol.newSession().advanceTo(12_000L).state,
        )
    }

    @Test
    fun supportsValidatedCustomDurations() {
        val duration = SessionDuration.customOrNull(15)

        assertEquals(15, duration?.minutes)
        assertEquals(15 * 60_000L, duration?.totalMillis)
        assertEquals("CUSTOM:15", duration?.name)
        assertEquals(duration, SessionDuration.fromSerialized("CUSTOM:15"))
        assertNull(SessionDuration.customOrNull(0))
        assertEquals(20, SessionDuration.customOrNull(20)?.minutes)
        assertNull(SessionDuration.customOrNull(21))
        assertNull(SessionDuration.fromSerialized("CUSTOM:21"))
    }
}
