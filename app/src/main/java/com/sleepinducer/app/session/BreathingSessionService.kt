package com.sleepinducer.app.session

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import com.sleepinducer.app.MainActivity
import com.sleepinducer.app.R
import com.sleepinducer.app.breathing.BreathingSessionEngine
import com.sleepinducer.app.breathing.BreathingPhase
import com.sleepinducer.app.breathing.SessionState
import com.sleepinducer.app.haptics.AndroidVibratorGateway
import com.sleepinducer.app.haptics.HapticAdapter
import com.sleepinducer.app.haptics.HapticCapability
import com.sleepinducer.app.haptics.HapticDelivery

class BreathingSessionService : Service() {
    companion object {
        const val ACTION_START = ForegroundSessionActions.ACTION_START
        const val ACTION_STOP = ForegroundSessionActions.ACTION_STOP
        const val EXTRA_DURATION = ForegroundSessionActions.EXTRA_DURATION
        const val NOTIFICATION_CHANNEL_ID = "breathing_session"
        private const val NOTIFICATION_ID = 1001
        private const val NOTIFICATION_STOP_REQUEST_CODE = 1002
        private const val NOTIFICATION_OPEN_REQUEST_CODE = 1003
        private const val PROGRESS_UPDATE_INTERVAL_MILLIS = 1_000L
    }

    private val stateMachine = ForegroundSessionStateMachine()
    private val binder = LocalBinder()
    private val mainHandler = Handler(Looper.getMainLooper())
    private val observers = mutableSetOf<(SessionSnapshot) -> Unit>()

    private lateinit var stateStore: SessionStateStore
    private lateinit var hapticAdapter: HapticAdapter
    private var scheduler: AndroidSessionScheduler? = null
    private var engine: BreathingSessionEngine? = null
    private var sessionStartedAtMillis: Long? = null
    private var progressTicker: Runnable? = null
    private var failureInProgress = false

    @Volatile
    private var currentSnapshot = SessionSnapshot()

    @Volatile
    var isForegroundOwned: Boolean = false
        private set

    val snapshot: SessionSnapshot
        get() = currentSnapshot

    val lifecycleState: ForegroundSessionState
        get() = stateMachine.state

    val activeDuration
        get() = stateMachine.activeDuration

    override fun onCreate() {
        super.onCreate()
        stateStore = SharedPreferencesSessionStateStore(this)
        hapticAdapter = HapticAdapter(AndroidVibratorGateway(this))
        currentSnapshot = currentSnapshot.copy(
            hapticCapability = hapticAdapter.capability(),
        )
        restorePersistedState()
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        when (intent?.action) {
            ACTION_START -> handleStart(intent)
            ACTION_STOP -> stopSession()
            else -> failSession(SessionFailure.INVALID_START)
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        if (lifecycleState == ForegroundSessionState.ACTIVE) {
            interruptForDestruction()
        }
        releaseForeground()
        clearEngine()
        super.onDestroy()
    }

    fun addSnapshotListener(listener: (SessionSnapshot) -> Unit) {
        synchronized(observers) {
            observers += listener
        }
        listener(currentSnapshot)
    }

    fun removeSnapshotListener(listener: (SessionSnapshot) -> Unit) {
        synchronized(observers) {
            observers -= listener
        }
    }

    inner class LocalBinder : android.os.Binder() {
        fun service(): BreathingSessionService = this@BreathingSessionService
    }

    private fun restorePersistedState() {
        val stored = stateStore.read() ?: return
        if (stored.lifecycle == ForegroundSessionState.ACTIVE) {
            stateMachine.restore(ForegroundSessionState.INTERRUPTED, stored.duration)
            stateStore.write(
                StoredSessionState(
                    lifecycle = ForegroundSessionState.INTERRUPTED,
                    duration = stored.duration,
                ),
            )
            currentSnapshot = currentSnapshot.copy(
                lifecycle = ForegroundSessionState.INTERRUPTED,
                duration = stored.duration,
                failure = SessionFailure.SERVICE_INTERRUPTED,
            )
        } else {
            stateMachine.restore(stored.lifecycle, stored.duration)
            currentSnapshot = currentSnapshot.copy(
                lifecycle = stored.lifecycle,
                duration = stored.duration,
            )
        }
    }

    private fun handleStart(intent: Intent) {
        if (lifecycleState == ForegroundSessionState.ACTIVE) {
            return
        }

        when (
            val result = ForegroundSessionCommandParser.parse(
                action = intent.action,
                durationName = intent.getStringExtra(EXTRA_DURATION),
            )
        ) {
            is SessionStartResult.Accepted -> activateSession(result.request)
            is SessionStartResult.Rejected -> failSession(SessionFailure.INVALID_START)
        }
    }

    private fun activateSession(request: SessionStartRequest) {
        val capability = hapticAdapter.capability()
        publish(
            currentSnapshot.copy(
                duration = request.duration,
                phase = null,
                elapsedMillis = 0L,
                hapticCapability = capability,
                failure = null,
            ),
        )

        if (capability !is HapticCapability.Usable) {
            failSession(
                failure = SessionFailure.HAPTICS_UNAVAILABLE,
                duration = request.duration,
            )
            return
        }

        try {
            createNotificationChannel()
            startForegroundCompat()
            stateMachine.activate(request.duration)
            sessionStartedAtMillis = SystemClock.elapsedRealtime()
            stateStore.write(
                StoredSessionState(
                    lifecycle = ForegroundSessionState.ACTIVE,
                    duration = request.duration,
                ),
            )

            val newScheduler = AndroidSessionScheduler(mainHandler)
            val newEngine = BreathingSessionEngine(
                duration = request.duration,
                scheduler = newScheduler,
                adapter = hapticAdapter,
                onStateChanged = ::onEngineStateChanged,
            )
            scheduler = newScheduler
            engine = newEngine
            publish(
                currentSnapshot.copy(
                    lifecycle = ForegroundSessionState.ACTIVE,
                    duration = request.duration,
                    failure = null,
                ),
            )
            startProgressTicker()
            newEngine.start()
        } catch (_: SecurityException) {
            failSession(
                failure = SessionFailure.FOREGROUND_START_FAILED,
                duration = request.duration,
            )
        } catch (_: IllegalArgumentException) {
            failSession(
                failure = SessionFailure.FOREGROUND_START_FAILED,
                duration = request.duration,
            )
        } catch (_: IllegalStateException) {
            failSession(
                failure = SessionFailure.FOREGROUND_START_FAILED,
                duration = request.duration,
            )
        }
    }

    private fun onEngineStateChanged(
        state: SessionState,
        delivery: HapticDelivery?,
    ) {
        if (failureInProgress) {
            return
        }

        if (delivery is HapticDelivery.Failed) {
            failSession(
                failure = SessionFailure.CUE_DELIVERY_FAILED,
                duration = currentSnapshot.duration,
            )
            return
        }

        if (state is SessionState.Active) {
            if (delivery is HapticDelivery.Unavailable ||
                delivery is HapticDelivery.Rejected
            ) {
                failSession(
                    failure = SessionFailure.CUE_DELIVERY_FAILED,
                    duration = currentSnapshot.duration,
                )
                return
            }

            publish(
                currentSnapshot.copy(
                    lifecycle = ForegroundSessionState.ACTIVE,
                    phase = state.phase,
                    elapsedMillis = state.elapsedMillis,
                    failure = null,
                ),
            )
            updateForegroundNotification()
            return
        }

        when (state) {
            SessionState.Completed -> finishCompleted()
            SessionState.Stopped -> finishStopped()
            SessionState.Interrupted -> finishInterrupted()
            SessionState.Ready -> Unit
            is SessionState.Active -> Unit
        }
    }

    private fun stopSession() {
        val activeEngine = engine
        if (lifecycleState == ForegroundSessionState.ACTIVE &&
            activeEngine != null
        ) {
            activeEngine.stop()
        } else {
            finishStopped()
        }
    }

    private fun finishCompleted() {
        val duration = currentSnapshot.duration
        stateMachine.complete()
        stateStore.write(
            StoredSessionState(
                lifecycle = ForegroundSessionState.COMPLETED,
                duration = duration,
            ),
        )
        publish(
            currentSnapshot.copy(
                lifecycle = ForegroundSessionState.COMPLETED,
                phase = null,
                elapsedMillis = duration?.totalMillis ?: currentSnapshot.elapsedMillis,
                failure = null,
            ),
        )
        releaseForeground()
        clearEngine()
        stopSelf()
    }

    private fun finishStopped() {
        val duration = currentSnapshot.duration
        stateMachine.stop()
        stateStore.write(
            StoredSessionState(
                lifecycle = ForegroundSessionState.STOPPED,
                duration = duration,
            ),
        )
        publish(
            currentSnapshot.copy(
                lifecycle = ForegroundSessionState.STOPPED,
                phase = null,
                failure = null,
            ),
        )
        releaseForeground()
        clearEngine()
        stopSelf()
    }

    private fun finishInterrupted() {
        val duration = currentSnapshot.duration
        stateMachine.interrupt()
        stateStore.write(
            StoredSessionState(
                lifecycle = ForegroundSessionState.INTERRUPTED,
                duration = duration,
            ),
        )
        publish(
            currentSnapshot.copy(
                lifecycle = ForegroundSessionState.INTERRUPTED,
                phase = null,
                failure = SessionFailure.SERVICE_INTERRUPTED,
            ),
        )
        releaseForeground()
        clearEngine()
        stopSelf()
    }

    private fun interruptForDestruction() {
        val duration = currentSnapshot.duration
        failureInProgress = true
        try {
            engine?.interrupt()
        } finally {
            failureInProgress = false
        }
        stateMachine.interrupt()
        stateStore.write(
            StoredSessionState(
                lifecycle = ForegroundSessionState.INTERRUPTED,
                duration = duration,
            ),
        )
        publish(
            currentSnapshot.copy(
                lifecycle = ForegroundSessionState.INTERRUPTED,
                phase = null,
                failure = SessionFailure.SERVICE_INTERRUPTED,
            ),
        )
    }

    private fun failSession(
        failure: SessionFailure,
        duration: com.sleepinducer.app.breathing.SessionDuration? =
            currentSnapshot.duration,
    ) {
        if (failureInProgress) {
            return
        }

        failureInProgress = true
        try {
            engine?.interrupt()
        } finally {
            failureInProgress = false
        }
        stateMachine.fail()
        stateStore.write(
            StoredSessionState(
                lifecycle = ForegroundSessionState.FAILED,
                duration = duration,
            ),
        )
        publish(
            currentSnapshot.copy(
                lifecycle = ForegroundSessionState.FAILED,
                duration = duration,
                phase = null,
                elapsedMillis = 0L,
                failure = failure,
            ),
        )
        releaseForeground()
        clearEngine()
        stopSelf()
    }

    private fun clearEngine() {
        progressTicker?.let(mainHandler::removeCallbacks)
        progressTicker = null
        sessionStartedAtMillis = null
        scheduler?.close()
        scheduler = null
        engine = null
    }

    private fun startProgressTicker() {
        val ticker = object : Runnable {
            override fun run() {
                if (lifecycleState != ForegroundSessionState.ACTIVE) {
                    return
                }

                val startedAt = sessionStartedAtMillis ?: return
                val duration = currentSnapshot.duration ?: return
                val elapsedMillis = (
                    SystemClock.elapsedRealtime() - startedAt
                    ).coerceIn(0L, duration.totalMillis)
                publish(
                    currentSnapshot.copy(elapsedMillis = elapsedMillis),
                )
                mainHandler.postDelayed(this, PROGRESS_UPDATE_INTERVAL_MILLIS)
            }
        }
        progressTicker = ticker
        mainHandler.postDelayed(ticker, PROGRESS_UPDATE_INTERVAL_MILLIS)
    }

    private fun publish(snapshot: SessionSnapshot) {
        currentSnapshot = snapshot
        val currentObservers = synchronized(observers) {
            observers.toList()
        }
        currentObservers.forEach { observer ->
            observer(snapshot)
        }
    }

    private fun startForegroundCompat() {
        val notification = buildNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
        isForegroundOwned = true
    }

    private fun updateForegroundNotification() {
        if (!isForegroundOwned) {
            return
        }
        getSystemService(NotificationManager::class.java)
            .notify(NOTIFICATION_ID, buildNotification())
    }

    private fun buildNotification(): Notification {
        val stopIntent = Intent(this, BreathingSessionService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            NOTIFICATION_STOP_REQUEST_CODE,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or
                Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openPendingIntent = PendingIntent.getActivity(
            this,
            NOTIFICATION_OPEN_REQUEST_CODE,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        return Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.service_notification_title))
            .setContentText(notificationText())
            .setSmallIcon(R.drawable.ic_sleep_inducer)
            .setContentIntent(openPendingIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .setOnlyAlertOnce(true)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .addAction(
                Notification.Action.Builder(
                    Icon.createWithResource(this, R.drawable.ic_sleep_inducer),
                    getString(R.string.service_notification_stop),
                    stopPendingIntent,
                ).build(),
            )
            .build()
    }

    private fun notificationText(): String =
        when (currentSnapshot.phase) {
            BreathingPhase.INHALE -> getString(R.string.service_notification_inhale)
            BreathingPhase.EXHALE -> getString(R.string.service_notification_exhale)
            null -> getString(R.string.service_notification_starting)
        }

    private fun createNotificationChannel() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.service_notification_channel),
                NotificationManager.IMPORTANCE_LOW,
            ),
        )
    }

    private fun releaseForeground() {
        if (!isForegroundOwned) {
            return
        }

        stopForeground(STOP_FOREGROUND_REMOVE)
        isForegroundOwned = false
    }

}
