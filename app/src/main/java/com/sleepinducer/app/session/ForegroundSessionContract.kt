package com.sleepinducer.app.session

import com.sleepinducer.app.breathing.SessionDuration

data class SessionStartRequest(
    val duration: SessionDuration,
)

object ForegroundSessionActions {
    const val ACTION_START = "com.sleepinducer.app.session.START"
    const val ACTION_STOP = "com.sleepinducer.app.session.STOP"
    const val EXTRA_DURATION = "com.sleepinducer.app.session.DURATION"
}

enum class SessionStartRejection {
    UNSUPPORTED_ACTION,
    MISSING_DURATION,
    UNSUPPORTED_DURATION,
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

        val duration = SessionDuration.entries.firstOrNull {
            it.name == durationName
        } ?: return SessionStartResult.Rejected(
            SessionStartRejection.UNSUPPORTED_DURATION,
        )

        return SessionStartResult.Accepted(SessionStartRequest(duration))
    }
}

enum class ForegroundSessionState {
    IDLE,
    ACTIVE,
    STOPPED,
    FAILED,
    INTERRUPTED,
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
}
