package com.sleepinducer.app.breathing

import com.sleepinducer.app.haptics.HapticAdapter
import com.sleepinducer.app.haptics.HapticCue
import com.sleepinducer.app.haptics.HapticDelivery

enum class HapticPhaseSignal {
    INHALE_START,
    EXHALE_START,
}

data class MappedPhaseHapticCue(
    val signal: HapticPhaseSignal,
    val cue: HapticCue,
)

object PhaseHapticCueMapping {
    val inhaleStart = MappedPhaseHapticCue(
        signal = HapticPhaseSignal.INHALE_START,
        cue = HapticCue(
            durationMillis = 80L,
            amplitude = 64,
        ),
    )

    val exhaleStart = MappedPhaseHapticCue(
        signal = HapticPhaseSignal.EXHALE_START,
        cue = HapticCue(
            durationMillis = 120L,
            amplitude = 48,
        ),
    )

    fun forPhase(phase: BreathingPhase): MappedPhaseHapticCue =
        when (phase) {
            BreathingPhase.INHALE -> inhaleStart
            BreathingPhase.EXHALE -> exhaleStart
        }

    fun cueFor(phase: BreathingPhase): HapticCue = forPhase(phase).cue
}

class PhaseHapticCoordinator(
    private val adapter: HapticAdapter,
    private val cueFor: (BreathingPhase) -> HapticCue =
        PhaseHapticCueMapping::cueFor,
) {
    private var lastPhase: BreathingPhase? = null
    private var terminalCancellationIssued = false

    fun onSessionStateChanged(state: SessionState): HapticDelivery? =
        when (state) {
            SessionState.Ready -> {
                lastPhase = null
                terminalCancellationIssued = false
                adapter.cancel()
            }

            is SessionState.Active -> deliverForNewPhase(state.phase)

            SessionState.Completed,
            SessionState.Stopped,
            SessionState.Interrupted,
            -> {
                if (!terminalCancellationIssued) {
                    terminalCancellationIssued = true
                    adapter.cancel()
                } else {
                    null
                }
            }
        }

    private fun deliverForNewPhase(phase: BreathingPhase): HapticDelivery? {
        if (terminalCancellationIssued || lastPhase == phase) {
            return null
        }

        lastPhase = phase
        val delivery = adapter.deliver(cueFor(phase))
        if (delivery != HapticDelivery.Delivered) {
            return adapter.cancel() ?: delivery
        }
        return delivery
    }
}
