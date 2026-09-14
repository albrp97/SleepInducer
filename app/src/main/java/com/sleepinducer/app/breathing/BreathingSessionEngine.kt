package com.sleepinducer.app.breathing

import com.sleepinducer.app.haptics.HapticAdapter
import com.sleepinducer.app.haptics.HapticDelivery

class BreathingSessionEngine(
    duration: SessionDuration,
    scheduler: SessionScheduler,
    adapter: HapticAdapter,
    private val onStateChanged: (SessionState, HapticDelivery?) -> Unit,
) {
    private val coordinator = PhaseHapticCoordinator(adapter)
    private val runner = BreathingSessionRunner(
        protocol = BreathingProtocol(duration),
        scheduler = scheduler,
    ) { state ->
        onStateChanged(
            state,
            coordinator.onSessionStateChanged(state),
        )
    }

    val state: SessionState
        get() = runner.state

    fun start() {
        runner.start()
    }

    fun stop() {
        runner.stop()
    }

    fun interrupt() {
        runner.interrupt()
    }
}
