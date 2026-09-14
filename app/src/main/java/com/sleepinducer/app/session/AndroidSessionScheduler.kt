package com.sleepinducer.app.session

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import com.sleepinducer.app.breathing.SessionSchedule
import com.sleepinducer.app.breathing.SessionScheduler

class AndroidSessionScheduler(
    private val handler: Handler = Handler(Looper.getMainLooper()),
    private val clock: () -> Long = SystemClock::elapsedRealtime,
) : SessionScheduler {
    private val schedules = mutableSetOf<AndroidSessionSchedule>()

    override fun nowMillis(): Long = clock()

    override fun scheduleAt(
        targetMillis: Long,
        action: () -> Unit,
    ): SessionSchedule {
        val schedule = AndroidSessionSchedule(targetMillis, action)
        schedules += schedule
        val delayMillis = (targetMillis - clock()).coerceAtLeast(0L)
        handler.postDelayed(schedule.runnable, delayMillis)
        return schedule
    }

    fun close() {
        schedules.toList().forEach(AndroidSessionSchedule::cancel)
    }

    private inner class AndroidSessionSchedule(
        private val targetMillis: Long,
        private val action: () -> Unit,
    ) : SessionSchedule {
        val runnable = Runnable {
            if (!cancelled) {
                schedules.remove(this)
                action()
            }
        }
        var cancelled: Boolean = false
            private set

        override fun cancel() {
            if (cancelled) {
                return
            }

            cancelled = true
            schedules.remove(this)
            handler.removeCallbacks(runnable)
        }
    }
}
