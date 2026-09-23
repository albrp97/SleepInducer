package com.sleepinducer.app.session

import com.sleepinducer.app.breathing.BreathingPhase
import com.sleepinducer.app.breathing.BreathingProtocolContract
import com.sleepinducer.app.breathing.SessionDuration
import com.sleepinducer.app.haptics.HapticCapability

data class SessionStartRequest(
    val duration: SessionDuration,
    val contract: BreathingProtocolContract = BreathingProtocolContract(),
)

enum class SessionFailure {
    INVALID_START,
    HAPTICS_UNAVAILABLE,
    FOREGROUND_START_FAILED,
    SCREEN_OFF_CONTINUITY_FAILED,
    CUE_DELIVERY_FAILED,
    SERVICE_INTERRUPTED,
}

data class SessionSnapshot(
    val lifecycle: ForegroundSessionState = ForegroundSessionState.IDLE,
    val duration: SessionDuration? = null,
    val contract: BreathingProtocolContract = BreathingProtocolContract(),
    val phase: BreathingPhase? = null,
    val elapsedMillis: Long = 0L,
    val hapticCapability: HapticCapability? = null,
    val failure: SessionFailure? = null,
)

data class StoredSessionState(
    val lifecycle: ForegroundSessionState,
    val duration: SessionDuration?,
)

interface SessionStateStore {
    fun read(): StoredSessionState?

    fun write(state: StoredSessionState)
}

object ForegroundSessionActions {
    const val ACTION_START = "com.sleepinducer.app.session.START"
    const val ACTION_STOP = "com.sleepinducer.app.session.STOP"
    const val EXTRA_DURATION = "com.sleepinducer.app.session.DURATION"
    const val EXTRA_INHALE_DURATION_MILLIS =
        "com.sleepinducer.app.session.INHALE_DURATION_MILLIS"
    const val EXTRA_EXHALE_DURATION_MILLIS =
        "com.sleepinducer.app.session.EXHALE_DURATION_MILLIS"
}

enum class SessionStartRejection {
    UNSUPPORTED_ACTION,
    MISSING_DURATION,
    UNSUPPORTED_DURATION,
    UNSUPPORTED_TIMING,
}

sealed interface SessionStartResult {
    data class Accepted(
        val request: SessionStartRequest,
    ) : SessionStartResult

    data class Rejected(
        val reason: SessionStartRejection,
    ) : SessionStartResult
}

object ForegroundSessionCommandParser {
    fun parse(
        action: String?,
        durationName: String?,
        inhaleDurationMillis: Long? = null,
        exhaleDurationMillis: Long? = null,
    ): SessionStartResult {
        if (action != ForegroundSessionActions.ACTION_START) {
            return SessionStartResult.Rejected(
                SessionStartRejection.UNSUPPORTED_ACTION,
            )
        }

        if (durationName == null) {
            return SessionStartResult.Rejected(
                SessionStartRejection.MISSING_DURATION,
            )
        }

        val duration = SessionDuration.fromSerialized(durationName)
            ?: return SessionStartResult.Rejected(
            SessionStartRejection.UNSUPPORTED_DURATION,
        )

        val contract = try {
            BreathingProtocolContract(
                inhaleDurationMillis =
                    inhaleDurationMillis
                        ?: BreathingProtocolContract.DEFAULT_PHASE_DURATION_MILLIS,
                exhaleDurationMillis =
                    exhaleDurationMillis
                        ?: BreathingProtocolContract.DEFAULT_PHASE_DURATION_MILLIS,
            )
        } catch (_: IllegalArgumentException) {
            return SessionStartResult.Rejected(
                SessionStartRejection.UNSUPPORTED_TIMING,
            )
        }

        return SessionStartResult.Accepted(
            SessionStartRequest(
                duration = duration,
                contract = contract,
            ),
        )
    }
}

enum class ForegroundSessionState {
    IDLE,
    ACTIVE,
    STOPPED,
    FAILED,
    INTERRUPTED,
    COMPLETED,
}

class ForegroundSessionStateMachine {
    @Volatile
    var state: ForegroundSessionState = ForegroundSessionState.IDLE
        private set

    @Volatile
    var activeDuration: SessionDuration? = null
        private set

    fun activate(duration: SessionDuration) {
        if (state == ForegroundSessionState.ACTIVE) {
            return
        }

        state = ForegroundSessionState.ACTIVE
        activeDuration = duration
    }

    fun complete() {
        state = ForegroundSessionState.COMPLETED
        activeDuration = null
    }

    fun fail() {
        state = ForegroundSessionState.FAILED
        activeDuration = null
    }

    fun stop() {
        state = ForegroundSessionState.STOPPED
        activeDuration = null
    }

    fun interrupt() {
        if (state == ForegroundSessionState.ACTIVE) {
            state = ForegroundSessionState.INTERRUPTED
            activeDuration = null
        }
    }

    fun restore(
        restoredState: ForegroundSessionState,
        duration: SessionDuration?,
    ) {
        state = restoredState
        activeDuration = if (restoredState == ForegroundSessionState.ACTIVE) {
            duration
        } else {
            null
        }
    }
}
