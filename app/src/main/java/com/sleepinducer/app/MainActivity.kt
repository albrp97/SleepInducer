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
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.sleepinducer.app.breathing.BreathingPhase
import com.sleepinducer.app.breathing.BreathingProtocolContract
import com.sleepinducer.app.breathing.SessionDuration
import com.sleepinducer.app.haptics.HapticCapability
import com.sleepinducer.app.haptics.HapticLimitation
import com.sleepinducer.app.session.BreathingSessionService
import com.sleepinducer.app.session.ForegroundSessionState
import com.sleepinducer.app.session.SessionFailure
import com.sleepinducer.app.session.SessionSnapshot
import kotlin.math.roundToInt

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4D6078),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD3E4FF),
    onPrimaryContainer = Color(0xFF071C30),
    secondary = Color(0xFF56606E),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD9E3F2),
    onSecondaryContainer = Color(0xFF131C27),
    background = Color(0xFFF8F9FC),
    onBackground = Color(0xFF191C20),
    surface = Color(0xFFF8F9FC),
    onSurface = Color(0xFF191C20),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFB2C8E8),
    onPrimary = Color(0xFF19324A),
    primaryContainer = Color(0xFF354A62),
    onPrimaryContainer = Color(0xFFD3E4FF),
    secondary = Color(0xFFBDC7D5),
    onSecondary = Color(0xFF27313D),
    secondaryContainer = Color(0xFF3D4855),
    onSecondaryContainer = Color(0xFFD9E3F2),
    background = Color(0xFF111418),
    onBackground = Color(0xFFE1E2E6),
    surface = Color(0xFF111418),
    onSurface = Color(0xFFE1E2E6),
)

private data class SessionSelection(
    val duration: SessionDuration,
    val contract: BreathingProtocolContract,
)

class MainActivity : ComponentActivity() {
    private val sessionSnapshot = mutableStateOf(SessionSnapshot())
    private val actionMessage = mutableStateOf<String?>(null)
    private var service: BreathingSessionService? = null
    private var isBound = false
    private var pendingSelection: SessionSelection? = null

    private val snapshotListener: (SessionSnapshot) -> Unit = { snapshot ->
        runOnUiThread {
            sessionSnapshot.value = snapshot
        }
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        val selection = pendingSelection
        pendingSelection = null
        if (granted && selection != null) {
            startServiceSession(selection)
        } else if (!granted) {
            actionMessage.value = getString(
                R.string.session_notification_permission_required,
            )
            if (selection != null) {
                startServiceSession(selection)
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

    private fun requestStart(
        duration: SessionDuration,
        contract: BreathingProtocolContract,
    ) {
        actionMessage.value = null
        val selection = SessionSelection(duration, contract)
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            pendingSelection = selection
            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS,
            )
            return
        }

        startServiceSession(selection)
    }

    private fun startServiceSession(selection: SessionSelection) {
        val intent = Intent(this, BreathingSessionService::class.java).apply {
            action = BreathingSessionService.ACTION_START
            putExtra(
                BreathingSessionService.EXTRA_DURATION,
                selection.duration.name,
            )
            putExtra(
                BreathingSessionService.EXTRA_INHALE_DURATION_MILLIS,
                selection.contract.inhaleDurationMillis,
            )
            putExtra(
                BreathingSessionService.EXTRA_EXHALE_DURATION_MILLIS,
                selection.contract.exhaleDurationMillis,
            )
        }
        try {
            startForegroundService(intent)
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
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) {
            DarkColorScheme
        } else {
            LightColorScheme
        },
        content = content,
    )
}

@Composable
private fun SessionScreen(
    snapshot: SessionSnapshot,
    actionMessage: String?,
    onStart: (SessionDuration, BreathingProtocolContract) -> Unit,
    onStop: () -> Unit,
) {
    var selectedDurationName by rememberSaveable {
        mutableStateOf(SessionDuration.DEFAULT.name)
    }
    var customMinutesText by rememberSaveable {
        mutableStateOf("0")
    }
    var inhaleDurationMillis by rememberSaveable {
        mutableLongStateOf(BreathingProtocolContract.DEFAULT_PHASE_DURATION_MILLIS)
    }
    var exhaleDurationMillis by rememberSaveable {
        mutableLongStateOf(BreathingProtocolContract.DEFAULT_PHASE_DURATION_MILLIS)
    }
    val customMinutes = customMinutesText.toIntOrNull() ?: 0
    val selectedDuration = if (
        selectedDurationName == SessionDuration.CUSTOM_OPTION_NAME
    ) {
        SessionDuration.customOrNull(customMinutes)
    } else {
        SessionDuration.entries.first {
            it.name == selectedDurationName
        }
    }
    val selectedContract = BreathingProtocolContract(
        inhaleDurationMillis = inhaleDurationMillis,
        exhaleDurationMillis = exhaleDurationMillis,
    )
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
                        selectedDurationName = selectedDurationName,
                        customMinutesText = customMinutesText,
                        onPresetSelected = {
                            selectedDurationName = it.name
                        },
                        onCustomSelected = {
                            selectedDurationName =
                                SessionDuration.CUSTOM_OPTION_NAME
                        },
                        onCustomMinutesChanged = {
                            customMinutesText = it.toString()
                        },
                    )
                    BreathingTimingSelector(
                        inhaleDurationMillis = inhaleDurationMillis,
                        exhaleDurationMillis = exhaleDurationMillis,
                        onInhaleDurationChanged = {
                            inhaleDurationMillis = it
                        },
                        onExhaleDurationChanged = {
                            exhaleDurationMillis = it
                        },
                    )
                    Button(
                        onClick = {
                            selectedDuration?.let { duration ->
                                onStart(duration, selectedContract)
                            }
                        },
                        enabled = selectedDuration != null,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(R.string.session_start))
                    }
                    HowItWorksCard()
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
    selectedDurationName: String,
    customMinutesText: String,
    onPresetSelected: (SessionDuration) -> Unit,
    onCustomSelected: () -> Unit,
    onCustomMinutesChanged: (Int) -> Unit,
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
                    selected = duration.name == selectedDurationName,
                    onClick = { onPresetSelected(duration) },
                    label = {
                        Text(duration.shortDisplayLabelComposable())
                    },
                )
            }
        }
        FilterChip(
            selected = selectedDurationName == SessionDuration.CUSTOM_OPTION_NAME,
            onClick = onCustomSelected,
            label = {
                Text(stringResource(R.string.duration_custom_option))
            },
        )
        if (selectedDurationName == SessionDuration.CUSTOM_OPTION_NAME) {
            val customMinutes = customMinutesText.toIntOrNull() ?: 0
            Text(
                text = stringResource(R.string.duration_custom_label),
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = pluralStringResource(
                    R.plurals.duration_custom_minutes,
                    customMinutes,
                    customMinutes,
                ),
                style = MaterialTheme.typography.titleMedium,
            )
            Slider(
                value = customMinutes.toFloat(),
                onValueChange = { value ->
                    onCustomMinutesChanged(
                        value.roundToInt().coerceIn(
                            0,
                            SessionDuration.MAX_CUSTOM_MINUTES,
                        ),
                    )
                },
                valueRange = 0f..SessionDuration.MAX_CUSTOM_MINUTES.toFloat(),
                steps = SessionDuration.MAX_CUSTOM_MINUTES - 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("custom-minutes-slider"),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.duration_custom_zero),
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = stringResource(
                        R.string.duration_custom_maximum,
                        SessionDuration.MAX_CUSTOM_MINUTES,
                    ),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Text(
                text = stringResource(
                    if (customMinutes == 0) {
                        R.string.duration_custom_error
                    } else {
                        R.string.duration_custom_supporting
                    },
                ),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun BreathingTimingSelector(
    inhaleDurationMillis: Long,
    exhaleDurationMillis: Long,
    onInhaleDurationChanged: (Long) -> Unit,
    onExhaleDurationChanged: (Long) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = stringResource(R.string.breathing_timing_title),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = stringResource(R.string.breathing_timing_body),
            style = MaterialTheme.typography.bodyMedium,
        )
        PhaseDurationSlider(
            label = stringResource(R.string.breathing_inhale_label),
            durationMillis = inhaleDurationMillis,
            onDurationChanged = onInhaleDurationChanged,
            testTag = "inhale-seconds-slider",
        )
        PhaseDurationSlider(
            label = stringResource(R.string.breathing_exhale_label),
            durationMillis = exhaleDurationMillis,
            onDurationChanged = onExhaleDurationChanged,
            testTag = "exhale-seconds-slider",
        )
    }
}

@Composable
private fun PhaseDurationSlider(
    label: String,
    durationMillis: Long,
    onDurationChanged: (Long) -> Unit,
    testTag: String,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = stringResource(
                R.string.breathing_phase_value,
                label,
                phaseDurationSecondsLabel(durationMillis),
            ),
            style = MaterialTheme.typography.titleSmall,
        )
        Slider(
            value = durationMillis / 1_000f,
            onValueChange = { value ->
                onDurationChanged(
                    (value * 1_000f)
                        .roundToInt()
                        .toLong()
                        .coerceIn(
                            BreathingProtocolContract.MIN_PHASE_DURATION_MILLIS,
                            BreathingProtocolContract.MAX_PHASE_DURATION_MILLIS,
                        ),
                )
            },
            valueRange =
                BreathingProtocolContract.MIN_PHASE_DURATION_MILLIS / 1_000f..
                    BreathingProtocolContract.MAX_PHASE_DURATION_MILLIS / 1_000f,
            steps =
                (
                    (
                        BreathingProtocolContract.MAX_PHASE_DURATION_MILLIS -
                            BreathingProtocolContract.MIN_PHASE_DURATION_MILLIS
                        ) / BreathingProtocolContract.PHASE_DURATION_STEP_MILLIS
                    ).toInt() - 1,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(
                    R.string.breathing_phase_minimum,
                    phaseDurationSecondsLabel(
                        BreathingProtocolContract.MIN_PHASE_DURATION_MILLIS,
                    ),
                ),
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = stringResource(
                    R.string.breathing_phase_maximum,
                    phaseDurationSecondsLabel(
                        BreathingProtocolContract.MAX_PHASE_DURATION_MILLIS,
                    ),
                ),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun HowItWorksCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = stringResource(R.string.setup_how_it_works_title),
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = stringResource(R.string.setup_technique_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(R.string.setup_technique_body),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = stringResource(R.string.setup_timing_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(R.string.setup_timing_body),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = stringResource(R.string.setup_cues_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(R.string.setup_cues_body),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = stringResource(R.string.setup_usage_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(R.string.setup_usage_step_one),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = stringResource(R.string.setup_usage_step_two),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = stringResource(R.string.setup_usage_step_three),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = stringResource(R.string.setup_usage_step_four),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = stringResource(R.string.setup_research_basis_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(R.string.setup_research_basis_body),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = stringResource(R.string.setup_evidence_note),
                style = MaterialTheme.typography.bodyMedium,
            )
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
                    R.string.session_active_timing,
                    phaseDurationSecondsLabel(
                        snapshot.contract.inhaleDurationMillis,
                    ),
                    phaseDurationSecondsLabel(
                        snapshot.contract.exhaleDurationMillis,
                    ),
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
        SessionFailure.SCREEN_OFF_CONTINUITY_FAILED ->
            stringResource(R.string.session_screen_off_continuity_failed)
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
        is SessionDuration.Custom -> pluralStringResource(
            R.plurals.duration_custom_minutes,
            minutes,
            minutes,
        )
    }

@Composable
private fun SessionDuration.shortDisplayLabelComposable(): String =
    when (this) {
        SessionDuration.SHORT -> stringResource(R.string.duration_five_minutes_short)
        SessionDuration.DEFAULT -> stringResource(R.string.duration_ten_minutes_short)
        SessionDuration.EXTENDED -> stringResource(R.string.duration_twenty_minutes_short)
        is SessionDuration.Custom -> pluralStringResource(
            R.plurals.duration_custom_minutes_short,
            minutes,
            minutes,
        )
    }

private fun phaseDurationSecondsLabel(durationMillis: Long): String {
    val halfSeconds = durationMillis / 500L
    return if (halfSeconds % 2L == 0L) {
        (halfSeconds / 2L).toString()
    } else {
        "${halfSeconds / 2L}.5"
    }
}

private fun SessionSnapshot.remainingLabel(): String {
    val remainingMillis = (
        (duration?.totalMillis ?: 0L) - elapsedMillis
        ).coerceAtLeast(0L)
    val totalSeconds = remainingMillis / 1_000L
    return "%d:%02d".format(totalSeconds / 60L, totalSeconds % 60L)
}
