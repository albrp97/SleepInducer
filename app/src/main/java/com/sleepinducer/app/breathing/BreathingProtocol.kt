package com.sleepinducer.app.breathing

enum class BreathingPhase {
    INHALE,
    EXHALE,
}

enum class BreathingDepth {
    NATURAL,
}

sealed interface SessionDuration {
    val name: String
    val minutes: Int
    val totalMillis: Long

    data object SHORT : SessionDuration {
        override val name: String = "SHORT"
        override val minutes: Int = 5
        override val totalMillis: Long = minutes * MILLIS_PER_MINUTE
    }

    data object DEFAULT : SessionDuration {
        override val name: String = "DEFAULT"
        override val minutes: Int = 10
        override val totalMillis: Long = minutes * MILLIS_PER_MINUTE
    }

    data object EXTENDED : SessionDuration {
        override val name: String = "EXTENDED"
        override val minutes: Int = 20
        override val totalMillis: Long = minutes * MILLIS_PER_MINUTE
    }

    data class Custom(
        override val minutes: Int,
    ) : SessionDuration {
        init {
            require(minutes in MIN_CUSTOM_MINUTES..MAX_CUSTOM_MINUTES) {
                "Custom duration must be between $MIN_CUSTOM_MINUTES and " +
                    "$MAX_CUSTOM_MINUTES minutes."
            }
        }

        override val name: String
            get() = "$CUSTOM_PREFIX$minutes"

        override val totalMillis: Long
            get() = minutes * MILLIS_PER_MINUTE
    }

    companion object {
        const val MIN_CUSTOM_MINUTES = 1
        const val MAX_CUSTOM_MINUTES = 20
        const val CUSTOM_OPTION_NAME = "CUSTOM"

        val entries: List<SessionDuration> = listOf(
            SHORT,
            DEFAULT,
            EXTENDED,
        )

        fun customOrNull(minutes: Int): Custom? {
            return if (minutes in MIN_CUSTOM_MINUTES..MAX_CUSTOM_MINUTES) {
                Custom(minutes)
            } else {
                null
            }
        }

        fun fromSerialized(serializedName: String): SessionDuration? {
            entries.firstOrNull { it.name == serializedName }?.let {
                return it
            }

            return serializedName
                .takeIf { it.startsWith(CUSTOM_PREFIX) }
                ?.removePrefix(CUSTOM_PREFIX)
                ?.toIntOrNull()
                ?.let(::customOrNull)
        }
    }
}

private const val MILLIS_PER_MINUTE = 60_000L
private const val CUSTOM_PREFIX = "CUSTOM:"

data class BreathingProtocolContract(
    val inhaleDurationMillis: Long = DEFAULT_PHASE_DURATION_MILLIS,
    val exhaleDurationMillis: Long = DEFAULT_PHASE_DURATION_MILLIS,
    val requiredHoldDurationMillis: Long = 0L,
    val depthGuidance: BreathingDepth = BreathingDepth.NATURAL,
) {
    init {
        require(isSupportedPhaseDuration(inhaleDurationMillis)) {
            "Inhale duration must be between 2 and 10 seconds in half-second steps."
        }
        require(isSupportedPhaseDuration(exhaleDurationMillis)) {
            "Exhale duration must be between 2 and 10 seconds in half-second steps."
        }
    }

    fun phaseDurationMillis(phase: BreathingPhase): Long =
        when (phase) {
            BreathingPhase.INHALE -> inhaleDurationMillis
            BreathingPhase.EXHALE -> exhaleDurationMillis
        }

    companion object {
        const val MIN_PHASE_DURATION_MILLIS = 2_000L
        const val MAX_PHASE_DURATION_MILLIS = 10_000L
        const val PHASE_DURATION_STEP_MILLIS = 500L
        const val DEFAULT_PHASE_DURATION_MILLIS = 6_000L

        fun isSupportedPhaseDuration(durationMillis: Long): Boolean {
            return durationMillis in
                MIN_PHASE_DURATION_MILLIS..MAX_PHASE_DURATION_MILLIS &&
                durationMillis % PHASE_DURATION_STEP_MILLIS == 0L
        }
    }
}

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

        val cycleDurationMillis =
            contract.inhaleDurationMillis + contract.exhaleDurationMillis
        val cyclePositionMillis = elapsedMillis % cycleDurationMillis
        val phase = if (cyclePositionMillis < contract.inhaleDurationMillis) {
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
