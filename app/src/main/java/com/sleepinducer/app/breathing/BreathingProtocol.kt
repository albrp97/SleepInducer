package com.sleepinducer.app.breathing

enum class BreathingPhase {
    INHALE,
    EXHALE,
}

enum class BreathingDepth {
    NATURAL,
}

enum class SessionDuration(val totalMillis: Long) {
    SHORT(5 * 60 * 1000L),
    DEFAULT(10 * 60 * 1000L),
    EXTENDED(20 * 60 * 1000L),
}

data class BreathingProtocolContract(
    val phaseDurationMillis: Long = 5_000L,
    val requiredHoldDurationMillis: Long = 0L,
    val depthGuidance: BreathingDepth = BreathingDepth.NATURAL,
)

sealed interface SessionState {
    data object Ready : SessionState

    data class Active(
        val phase: BreathingPhase,
        val elapsedMillis: Long,
    ) : SessionState

    data object Completed : SessionState
    data object Stopped : SessionState
    data object Interrupted : SessionState
}

class BreathingProtocol(
    val duration: SessionDuration,
    val contract: BreathingProtocolContract = BreathingProtocolContract(),
) {
    init {
        require(contract.phaseDurationMillis > 0L) {
            "Phase duration must be positive."
        }
        require(contract.requiredHoldDurationMillis == 0L) {
            "Mandatory breath holds are not supported."
        }
        require(contract.depthGuidance == BreathingDepth.NATURAL) {
            "Only natural breathing guidance is supported."
        }
    }

    fun newSession(): BreathingSession = BreathingSession(this, SessionState.Ready)

    internal fun stateAt(elapsedMillis: Long): SessionState {
        require(elapsedMillis >= 0L) { "Elapsed time cannot be negative." }

        if (elapsedMillis >= duration.totalMillis) {
            return SessionState.Completed
        }

        val cycleDurationMillis = contract.phaseDurationMillis * 2
        val phase = if (elapsedMillis % cycleDurationMillis < contract.phaseDurationMillis) {
            BreathingPhase.INHALE
        } else {
            BreathingPhase.EXHALE
        }

        return SessionState.Active(phase, elapsedMillis)
    }
}

class BreathingSession internal constructor(
    private val protocol: BreathingProtocol,
    val state: SessionState,
) {
    fun advanceTo(elapsedMillis: Long): BreathingSession {
        require(elapsedMillis >= 0L) { "Elapsed time cannot be negative." }

        if (state.isTerminal) {
            return this
        }

        val currentElapsedMillis = (state as? SessionState.Active)?.elapsedMillis ?: 0L
        require(elapsedMillis >= currentElapsedMillis) {
            "Elapsed time must be monotonic."
        }

        return BreathingSession(protocol, protocol.stateAt(elapsedMillis))
    }

    fun stop(): BreathingSession {
        return if (state.isTerminal) this else BreathingSession(protocol, SessionState.Stopped)
    }

    fun interrupt(): BreathingSession {
        return if (state.isTerminal) this else BreathingSession(protocol, SessionState.Interrupted)
    }
}

private val SessionState.isTerminal: Boolean
    get() = this is SessionState.Completed ||
        this is SessionState.Stopped ||
        this is SessionState.Interrupted
