package com.sleepinducer.app.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

data class HapticCue(
    val durationMillis: Long,
    val amplitude: Int,
) {
    companion object {
        const val MAX_DURATION_MILLIS = 250L
        const val MAX_AMPLITUDE = 128
        val DefaultPhaseTransition = HapticCue(
            durationMillis = 80L,
            amplitude = 64,
        )
    }
}

enum class HapticLimitation {
    NO_VIBRATOR,
    AMPLITUDE_CONTROL_UNAVAILABLE,
}

enum class HapticCueRejection {
    DURATION_OUT_OF_RANGE,
    AMPLITUDE_OUT_OF_RANGE,
}

sealed interface HapticCapability {
    data object Usable : HapticCapability

    data class Unavailable(
        val reason: HapticLimitation,
    ) : HapticCapability
}

sealed interface HapticDelivery {
    data object Delivered : HapticDelivery

    data class Unavailable(
        val reason: HapticLimitation,
    ) : HapticDelivery

    data class Rejected(
        val reason: HapticCueRejection,
    ) : HapticDelivery
}

interface VibratorGateway {
    val hasVibrator: Boolean
    val hasAmplitudeControl: Boolean

    fun vibrate(cue: HapticCue)

    fun cancel()
}

class HapticAdapter(
    private val gateway: VibratorGateway,
) {
    private var cueActive = false

    fun capability(): HapticCapability =
        when {
            !gateway.hasVibrator -> HapticCapability.Unavailable(
                HapticLimitation.NO_VIBRATOR,
            )

            !gateway.hasAmplitudeControl -> HapticCapability.Unavailable(
                HapticLimitation.AMPLITUDE_CONTROL_UNAVAILABLE,
            )

            else -> HapticCapability.Usable
        }

    fun deliver(cue: HapticCue): HapticDelivery {
        when (val capability = capability()) {
            HapticCapability.Usable -> Unit
            is HapticCapability.Unavailable -> {
                cancel()
                return HapticDelivery.Unavailable(capability.reason)
            }
        }

        val rejection = cue.rejection()
        if (rejection != null) {
            return HapticDelivery.Rejected(rejection)
        }

        cancel()
        gateway.vibrate(cue)
        cueActive = true
        return HapticDelivery.Delivered
    }

    fun cancel() {
        if (cueActive) {
            gateway.cancel()
            cueActive = false
        }
    }

    private fun HapticCue.rejection(): HapticCueRejection? =
        when {
            durationMillis !in 1..HapticCue.MAX_DURATION_MILLIS ->
                HapticCueRejection.DURATION_OUT_OF_RANGE

            amplitude !in 1..HapticCue.MAX_AMPLITUDE ->
                HapticCueRejection.AMPLITUDE_OUT_OF_RANGE

            else -> null
        }
}

class AndroidVibratorGateway(
    context: Context,
) : VibratorGateway {
    private val vibrator = resolveVibrator(context)

    override val hasVibrator: Boolean
        get() = vibrator?.hasVibrator() == true

    override val hasAmplitudeControl: Boolean
        get() = vibrator?.hasAmplitudeControl() == true

    override fun vibrate(cue: HapticCue) {
        val availableVibrator = checkNotNull(vibrator) {
            "A usable vibrator is required before delivering a haptic cue."
        }
        availableVibrator.vibrate(
            VibrationEffect.createOneShot(cue.durationMillis, cue.amplitude),
        )
    }

    override fun cancel() {
        vibrator?.cancel()
    }

    private fun resolveVibrator(context: Context): Vibrator? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(VibratorManager::class.java)?.defaultVibrator
        } else {
            resolveLegacyVibrator(context)
        }

    @Suppress("DEPRECATION")
    private fun resolveLegacyVibrator(context: Context): Vibrator? =
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
}
