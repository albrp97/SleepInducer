package com.sleepinducer.app.breathing

import com.sleepinducer.app.haptics.HapticAdapter
import com.sleepinducer.app.haptics.HapticCue
import com.sleepinducer.app.haptics.HapticDelivery
import com.sleepinducer.app.haptics.HapticDeliveryFailure
import com.sleepinducer.app.haptics.VibratorGateway
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BreathingSessionEngineTest {
    @Test
    fun advancesPhasesAndCompletesWithTerminalCueCancellation() {
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
        scheduler.runAt(6_000L)

        scheduler.runAt(SessionDuration.SHORT.totalMillis)

        assertEquals(BreathingPhase.EXHALE, (states[1] as SessionState.Active).phase)
        assertEquals(SessionState.Completed, states.last())
        assertEquals(
            listOf(
                HapticDelivery.Delivered,
                HapticDelivery.Delivered,
                null,
            ),
            deliveries.take(3),
        )
        assertEquals(2, gateway.emittedCues.size)
        assertTrue(gateway.cancellationCount >= 1)
    }

    @Test
    fun reportsTerminalCancellationFailure() {
        val scheduler = FakeSessionScheduler()
        val gateway = RecordingVibratorGateway(
            cancellationFailure = HapticDeliveryFailure.SYSTEM_REJECTED,
        )
        val deliveries = mutableListOf<HapticDelivery?>()
        val engine = BreathingSessionEngine(
            duration = SessionDuration.SHORT,
            scheduler = scheduler,
            adapter = HapticAdapter(gateway),
        ) { _, delivery ->
            deliveries += delivery
        }

        engine.start()
        engine.stop()

        assertEquals(
            listOf(
                HapticDelivery.Delivered,
                HapticDelivery.Failed(HapticDeliveryFailure.SYSTEM_REJECTED),
            ),
            deliveries,
        )
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
            return SessionSchedule { scheduledAction.cancelled = true }
        }

        fun runAt(timeMillis: Long) {
            currentTimeMillis = timeMillis
            val next = scheduled
                .filter { !it.cancelled && it.targetMillis <= timeMillis }
                .minByOrNull { it.targetMillis }
                ?: error("No scheduled session boundary is ready.")
            next.cancelled = true
            next.action()
        }

        private data class ScheduledAction(
            val targetMillis: Long,
            val action: () -> Unit,
            var cancelled: Boolean = false,
        )
    }

    private class RecordingVibratorGateway(
        private val cancellationFailure: HapticDeliveryFailure? = null,
    ) : VibratorGateway {
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
            if (cancellationFailure != null) {
                throw when (cancellationFailure) {
                    HapticDeliveryFailure.SECURITY_RESTRICTION ->
                        SecurityException("test")
                    HapticDeliveryFailure.SYSTEM_REJECTED ->
                        IllegalStateException("test")
                }
            }
        }
    }
}
