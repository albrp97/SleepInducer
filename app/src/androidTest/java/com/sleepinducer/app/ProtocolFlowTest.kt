package com.sleepinducer.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sleepinducer.app.breathing.BreathingDepth
import com.sleepinducer.app.breathing.BreathingPhase
import com.sleepinducer.app.breathing.BreathingProtocol
import com.sleepinducer.app.breathing.SessionDuration
import com.sleepinducer.app.breathing.SessionState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProtocolFlowTest {
    private val appContext = InstrumentationRegistry
        .getInstrumentation()
        .targetContext

    @Test
    fun exposesTheConfiguredPhaseContract() {
        val session = BreathingProtocol(SessionDuration.DEFAULT).newSession()

        assertEquals("com.sleepinducer.app", appContext.packageName)
        assertEquals(
            SessionState.Active(BreathingPhase.INHALE, 0L),
            session.advanceTo(0L).state,
        )
        assertEquals(
            SessionState.Active(BreathingPhase.EXHALE, 5_000L),
            session.advanceTo(5_000L).state,
        )
    }

    @Test
    fun reportsOneTerminalCompletion() {
        val session = BreathingProtocol(SessionDuration.SHORT).newSession()
        val completed = session.advanceTo(SessionDuration.SHORT.totalMillis)

        assertEquals(SessionState.Completed, completed.state)
        assertSame(
            completed,
            completed.advanceTo(SessionDuration.SHORT.totalMillis + 5_000L),
        )
    }

    @Test
    fun stopsFutureProtocolTransitions() {
        val session = BreathingProtocol(SessionDuration.DEFAULT)
            .newSession()
            .advanceTo(1_000L)
        val stopped = session.stop()
        val interrupted = session.interrupt()

        assertEquals(SessionState.Stopped, stopped.state)
        assertEquals(SessionState.Interrupted, interrupted.state)
        assertSame(stopped, stopped.advanceTo(SessionDuration.DEFAULT.totalMillis))
        assertSame(interrupted, interrupted.advanceTo(SessionDuration.DEFAULT.totalMillis))
    }

    @Test
    fun exposesTheNoHoldDefault() {
        val protocol = BreathingProtocol(SessionDuration.DEFAULT)

        assertEquals(0L, protocol.contract.requiredHoldDurationMillis)
        assertEquals(BreathingDepth.NATURAL, protocol.contract.depthGuidance)
        assertEquals(5_000L, protocol.contract.phaseDurationMillis)
    }
}
