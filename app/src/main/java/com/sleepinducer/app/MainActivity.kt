package com.sleepinducer.app

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sleepinducer.app.breathing.BreathingPhase
import com.sleepinducer.app.breathing.SessionDuration
import com.sleepinducer.app.haptics.HapticCapability
import com.sleepinducer.app.haptics.HapticLimitation
import com.sleepinducer.app.session.BreathingSessionService
import com.sleepinducer.app.session.ForegroundSessionState
import com.sleepinducer.app.session.SessionFailure
import com.sleepinducer.app.session.SessionSnapshot

class MainActivity : ComponentActivity() {
    private val sessionSnapshot = mutableStateOf(SessionSnapshot())
    private val actionMessage = mutableStateOf<String?>(null)
    private var service: BreathingSessionService? = null
    private var isBound = false
    private var pendingDuration: SessionDuration? = null

    private val snapshotListener: (SessionSnapshot) -> Unit = { snapshot ->
        runOnUiThread {
            sessionSnapshot.value = snapshot
        }
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        val duration = pendingDuration
        pendingDuration = null
        if (granted && duration != null) {
            startServiceSession(duration)
        } else if (!granted) {
            actionMessage.value = getString(
                R.string.session_notification_permission_required,
            )
            if (duration != null) {
                startServiceSession(duration)
            }
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            name: ComponentName?,
            binder: IBinder?,
        ) {
            service = (binder as BreathingSessionService.LocalBinder).service()
                .also { boundService ->
                    boundService.addSnapshotListener(snapshotListener)
                }
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SleepInducerTheme {
                SessionScreen(
                    snapshot = sessionSnapshot.value,
                    actionMessage = actionMessage.value,
                    onStart = ::requestStart,
                    onStop = ::stopSession,
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        bindService(
            Intent(this, BreathingSessionService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE,
        )
    }

    override fun onStop() {
        service?.removeSnapshotListener(snapshotListener)
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
        service = null
        super.onStop()
    }

    private fun requestStart(duration: SessionDuration) {
        actionMessage.value = null
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            pendingDuration = duration
            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS,
            )
            return
        }

        startServiceSession(duration)
    }

    private fun startServiceSession(duration: SessionDuration) {
        val intent = Intent(this, BreathingSessionService::class.java).apply {
            action = BreathingSessionService.ACTION_START
            putExtra(BreathingSessionService.EXTRA_DURATION, duration.name)
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        } catch (_: SecurityException) {
            actionMessage.value = getString(
                R.string.session_start_failed,
            )
        } catch (_: IllegalStateException) {
            actionMessage.value = getString(
                R.string.session_start_failed,
            )
        }
    }

    private fun stopSession() {
        val intent = Intent(this, BreathingSessionService::class.java).apply {
            action = BreathingSessionService.ACTION_STOP
        }
        startService(intent)
    }
}

@Composable
private fun SleepInducerTheme(content: @Composable () -> Unit) {
    MaterialTheme(content = content)
}

@Composable
private fun SessionScreen(
    snapshot: SessionSnapshot,
    actionMessage: String?,
    onStart: (SessionDuration) -> Unit,
    onStop: () -> Unit,
) {
    var selectedDurationName by rememberSaveable {
        mutableStateOf(SessionDuration.DEFAULT.name)
    }
    val selectedDuration = SessionDuration.entries.first {
        it.name == selectedDurationName
    }
    val isActive = snapshot.lifecycle == ForegroundSessionState.ACTIVE

    Scaffold { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    modifier = Modifier.semantics { heading() },
                    style = MaterialTheme.typography.headlineLarge,
                )
                Text(
                    text = stringResource(R.string.setup_purpose),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = stringResource(R.string.setup_wellness_boundary),
                    style = MaterialTheme.typography.bodyMedium,
                )
                HorizontalDivider()

                if (isActive) {
                    ActiveSessionCard(snapshot = snapshot, onStop = onStop)
                } else {
                    DurationSelector(
                        selectedDuration = selectedDuration,
                        onSelected = { selectedDurationName = it.name },
                    )
                    Button(
                        onClick = { onStart(selectedDuration) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(R.string.session_start))
                    }
                }

                TerminalStateCard(snapshot = snapshot)
                FailureCard(snapshot = snapshot)
                HapticStatus(snapshot = snapshot)
                ActionMessage(actionMessage)
                SafetyCard()
                Text(
                    text = stringResource(R.string.setup_offline),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun DurationSelector(
    selectedDuration: SessionDuration,
    onSelected: (SessionDuration) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = stringResource(R.string.session_duration_title),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = stringResource(R.string.session_duration_body),
            style = MaterialTheme.typography.bodyMedium,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SessionDuration.entries.forEach { duration ->
                FilterChip(
                    selected = duration == selectedDuration,
                    onClick = { onSelected(duration) },
                    label = {
                        Text(duration.shortDisplayLabelComposable())
                    },
                )
            }
        }
    }
}

@Composable
private fun ActiveSessionCard(
    snapshot: SessionSnapshot,
    onStop: () -> Unit,
) {
    val phaseText = when (snapshot.phase) {
        BreathingPhase.INHALE -> stringResource(R.string.session_inhale)
        BreathingPhase.EXHALE -> stringResource(R.string.session_exhale)
        null -> stringResource(R.string.session_preparing)
    }
    Card(modifier = Modifier.fillMaxWidth()) {
        val durationLabel = snapshot.duration?.displayLabelComposable().orEmpty()
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.session_active_title),
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = phaseText,
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = stringResource(
                    R.string.session_active_body,
                    durationLabel,
                ),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = stringResource(
                    R.string.session_remaining,
                    snapshot.remainingLabel(),
                ),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = stringResource(R.string.session_screen_off_guidance),
                style = MaterialTheme.typography.bodyMedium,
            )
            Button(
                onClick = onStop,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.session_stop))
            }
        }
    }
}

@Composable
private fun FailureCard(snapshot: SessionSnapshot) {
    val message = when (snapshot.failure) {
        SessionFailure.INVALID_START ->
            stringResource(R.string.session_invalid_start)
        SessionFailure.HAPTICS_UNAVAILABLE ->
            stringResource(R.string.session_haptics_unavailable)
        SessionFailure.FOREGROUND_START_FAILED ->
            stringResource(R.string.session_start_failed)
        SessionFailure.CUE_DELIVERY_FAILED ->
            stringResource(R.string.session_cue_delivery_failed)
        SessionFailure.SERVICE_INTERRUPTED ->
            stringResource(R.string.session_service_interrupted)
        null -> null
    } ?: return

    Card(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = message,
            modifier = Modifier.padding(20.dp),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun TerminalStateCard(snapshot: SessionSnapshot) {
    val message = when (snapshot.lifecycle) {
        ForegroundSessionState.COMPLETED ->
            stringResource(R.string.session_completed)
        ForegroundSessionState.STOPPED ->
            stringResource(R.string.session_stopped)
        ForegroundSessionState.INTERRUPTED ->
            stringResource(R.string.session_interrupted)
        else -> null
    } ?: return

    Card(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = message,
            modifier = Modifier.padding(20.dp),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun HapticStatus(snapshot: SessionSnapshot) {
    val limitation = (snapshot.hapticCapability as? HapticCapability.Unavailable)
        ?.reason
        ?: return
    val message = when (limitation) {
        HapticLimitation.NO_VIBRATOR ->
            stringResource(R.string.session_no_vibrator)
        HapticLimitation.AMPLITUDE_CONTROL_UNAVAILABLE ->
            stringResource(R.string.session_no_amplitude_control)
    }
    Card(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = message,
            modifier = Modifier.padding(20.dp),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun ActionMessage(message: String?) {
    if (message == null) {
        return
    }
    Card(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = message,
            modifier = Modifier.padding(20.dp),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun SafetyCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.setup_safety_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(R.string.setup_stop_guidance),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = stringResource(R.string.setup_phone_guidance),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun SessionDuration.displayLabelComposable(): String =
    when (this) {
        SessionDuration.SHORT -> stringResource(R.string.duration_five_minutes)
        SessionDuration.DEFAULT -> stringResource(R.string.duration_ten_minutes)
        SessionDuration.EXTENDED -> stringResource(R.string.duration_twenty_minutes)
    }

@Composable
private fun SessionDuration.shortDisplayLabelComposable(): String =
    when (this) {
        SessionDuration.SHORT -> stringResource(R.string.duration_five_minutes_short)
        SessionDuration.DEFAULT -> stringResource(R.string.duration_ten_minutes_short)
        SessionDuration.EXTENDED -> stringResource(R.string.duration_twenty_minutes_short)
    }

private fun SessionSnapshot.remainingLabel(): String {
    val remainingMillis = (
        (duration?.totalMillis ?: 0L) - elapsedMillis
        ).coerceAtLeast(0L)
    val totalSeconds = remainingMillis / 1_000L
    return "%d:%02d".format(totalSeconds / 60L, totalSeconds % 60L)
}
