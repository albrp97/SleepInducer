package com.sleepinducer.app.breathing

fun interface SessionSchedule {
    fun cancel()
}

interface SessionScheduler {
    fun nowMillis(): Long

    fun scheduleAt(
        targetMillis: Long,
        action: () -> Unit,
    ): SessionSchedule
}

class BreathingSessionRunner(
    private val protocol: BreathingProtocol,
    private val scheduler: SessionScheduler,
    private val onStateChanged: (SessionState) -> Unit,
) {
    private var session = protocol.newSession()
    private var startedAtMillis: Long? = null
    private var nextBoundaryElapsedMillis: Long? = null
    private var nextBoundaryPhase: BreathingPhase? = null
    private var scheduledBoundary: SessionSchedule? = null

    val state: SessionState
        get() = session.state

    fun start() {
        if (state != SessionState.Ready) {
            return
        }

        startedAtMillis = scheduler.nowMillis()
        session = session.advanceTo(0L)
        onStateChanged(session.state)
        if (state.isTerminal) {
            return
        }
        nextBoundaryElapsedMillis = protocol.contract.inhaleDurationMillis
        nextBoundaryPhase = BreathingPhase.EXHALE
        scheduleNextBoundary()
    }

    fun stop() {
        transitionTerminal { it.stop() }
    }

    fun interrupt() {
        transitionTerminal { it.interrupt() }
    }

    private fun transitionTerminal(
        transition: (BreathingSession) -> BreathingSession,
    ) {
        if (state.isTerminal) {
            return
        }

        cancelScheduledBoundary()
        session = transition(session)
        onStateChanged(session.state)
    }

    private fun scheduleNextBoundary() {
        val startedAt = checkNotNull(startedAtMillis)
        val nextBoundaryElapsed = checkNotNull(nextBoundaryElapsedMillis)

        scheduledBoundary = scheduler.scheduleAt(
            targetMillis = startedAt + nextBoundaryElapsed,
            action = ::handleScheduledBoundary,
        )
    }

    private fun handleScheduledBoundary() {
        val currentBoundary = scheduledBoundary
        scheduledBoundary = null

        if (state.isTerminal) {
            return
        }

        val startedAt = checkNotNull(startedAtMillis)
        val elapsedMillis = scheduler.nowMillis() - startedAt
        session = session.advanceTo(elapsedMillis)
        onStateChanged(session.state)

        if (state.isTerminal) {
            currentBoundary?.cancel()
            return
        }

        var nextBoundaryElapsed = checkNotNull(nextBoundaryElapsedMillis)
        while (nextBoundaryElapsed <= elapsedMillis) {
            val boundaryPhase = checkNotNull(nextBoundaryPhase)
            nextBoundaryElapsed += protocol.contract.phaseDurationMillis(
                boundaryPhase,
            )
            nextBoundaryPhase = when (boundaryPhase) {
                BreathingPhase.INHALE -> BreathingPhase.EXHALE
                BreathingPhase.EXHALE -> BreathingPhase.INHALE
            }
        }
        nextBoundaryElapsedMillis = nextBoundaryElapsed
        scheduleNextBoundary()
    }

    private fun cancelScheduledBoundary() {
        scheduledBoundary?.cancel()
        scheduledBoundary = null
    }
}

private val SessionState.isTerminal: Boolean
    get() = this is SessionState.Completed ||
        this is SessionState.Stopped ||
        this is SessionState.Interrupted
