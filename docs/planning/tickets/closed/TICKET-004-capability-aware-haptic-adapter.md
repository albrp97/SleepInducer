# Ticket 004 - Creating the capability-aware haptic adapter

**Ticket ID:** `TICKET-004`
**Status:** complete
**Parent feature:** `FEAT-003`
**Parent phase:** `PHASE-001`
**Parent objective:** `OBJ-001`
**Parent scope:** `SCOPE-001`
**Capabilities:** `CAP-003`, `CAP-005`
**Owner:** Android platform implementation
**Source paths:** `vision.md`, `AGENTS.md`,
`docs/specs/project-scope.md`, `docs/specs/capability-map.md`,
`docs/planning/features/open/FEAT-003-capability-aware-haptic-guidance.md`,
`docs/research/sleep-onset-evidence.md`
**Development mode:** automatic
**Approval state:** bootstrap-authorized automatic child planning
**Last updated:** 2026-09-09

## Outcome

Create a testable Android haptic boundary that detects whether conservative
phase cues can be delivered, emits a caller-provided short cue when the
required capability is present, and reports an explicit non-success result
when hardware or amplitude control is unavailable.

## Scope

- Add the `android.permission.VIBRATE` manifest capability.
- Define a platform-independent haptic adapter contract, capability result, cue
  value, and delivery result in the Android application layer.
- Resolve `VibratorManager` on API 31 and newer and the compatible legacy
  `Vibrator` service on API 26 through 30 behind one production adapter.
- Detect a usable vibrator and amplitude-control support before delivery.
- Use bounded, short one-shot cues with conservative defaults and no
  continuous vibration.
- Cancel an active cue safely and idempotently before any terminal caller state
  can leave stale vibration active.
- Expose a stable limitation reason for the future UI/session boundary without
  adding session controls or Compose rendering in this ticket.
- Add fake-gateway unit coverage and packaged Android functionality coverage
  for supported and unsupported capability paths.

## Non-goals

- Mapping inhale/exhale semantics to product cues; that belongs to `FEAT-005`.
- Foreground services, screen-off continuity, notifications, or session
  lifecycle ownership; those belong to `FEAT-006` and `FEAT-007`.
- Final comfort calibration across OEM devices; release-device evidence
  belongs to `FEAT-010`.
- Audio, continuous vibration, wearable integration, health sensing, or
  performance feedback.

## Affected surfaces

- `app/src/main/AndroidManifest.xml`.
- `app/src/main/java/com/sleepinducer/app/haptics/`.
- `app/src/test/java/com/sleepinducer/app/haptics/`.
- `app/src/androidTest/java/com/sleepinducer/app/HapticAdapterFlowTest.kt`.
- `evidence/TICKET-004/`.

## Dependencies and risks

- **Dependencies:** completed `TICKET-003`, Android API 26 through 35,
  `android.permission.VIBRATE`, and the Android device capability APIs.
- **Risks:** OEM amplitude differences, API compatibility mistakes,
  overlapping one-shot cues, and success-shaped behavior when hardware is
  unavailable.
- **Mitigation:** isolate platform calls behind a gateway, validate capability
  before delivery, bound cue duration and amplitude, cancel idempotently, and
  preserve explicit unavailable results.

## Acceptance criteria and automated functionality tests

1. **Supported capability detection:** Given a vibrator with amplitude control,
   the adapter reports a usable capability with no limitation reason.
   - **Unit test:** `HapticAdapterTest#reportsSupportedCapability`.
   - **Functionality test:** `HapticAdapterFlowTest#reportsSupportedCapability`
     installs the APK, uses the packaged adapter boundary with a supported
     gateway, and asserts the capability result is usable.
   - **Expected state:** capability is usable and ready for a cue.
2. **Conservative cue delivery:** Given a usable capability and a valid cue,
   the adapter emits exactly one bounded one-shot vibration and returns a
   delivered result.
   - **Unit test:** `HapticAdapterTest#deliversOneConfiguredCue`.
   - **Functionality test:** `HapticAdapterFlowTest#deliversAndCancelsCue`
     records the packaged adapter's gateway calls and asserts one cue with
     bounded duration and amplitude followed by cancellation.
   - **Expected state:** delivery is reported only after the platform gateway
     accepts the cue.
3. **Unsupported hardware honesty:** Given no vibrator or no amplitude-control
   support, the adapter returns an explicit unavailable result with a stable
   limitation reason and does not invoke vibration delivery.
   - **Unit test:** `HapticAdapterTest#rejectsUnavailableCapabilities`.
   - **Functionality test:** `HapticAdapterFlowTest#reportsUnavailableHardware`
     runs both unsupported gateway variants through the packaged boundary and
     asserts no success-shaped delivery result or vibration call.
   - **Expected state:** callers can show a clear limitation and cannot claim
     that haptics were delivered.
4. **Cancellation safety:** Given a delivered cue is active, cancellation is
   idempotent and leaves no active gateway vibration for later session wiring.
   - **Unit test:** `HapticAdapterTest#cancelsIdempotently`.
   - **Functionality test:** `HapticAdapterFlowTest#cancelsWithoutStaleDelivery`
     emits a cue, cancels it twice, and asserts the gateway has no active cue
     and receives no later delivery.
   - **Expected state:** stop/terminal callers have a reliable cancellation
     boundary with no stale cue.

## Baseline and evidence

- **Protected baseline:** committed Android foundation, offline setup,
  breathing protocol, unit tests, lint, debug APK build, and nine emulator
  functionality tests remain passing before adapter changes.
- **Commands:** `./gradlew test --offline`, `./gradlew lintDebug --offline`,
  `./gradlew assembleDebug --offline`, and
  `./gradlew connectedDebugAndroidTest --offline`.
- **Evidence path:** `evidence/TICKET-004/`.
- **Classification:** `automaticValidation` only, using the configured
  `rubber-duck` / `gpt-5.6-luna` / high / `all-validation` profile.

## User-validation plan

Automatic mode requires the agent to execute the same adapter functionality
charter and record `automaticValidation` without waiting. A physical-device
comfort and screen-off smoke run is intentionally deferred to `FEAT-010`,
because this ticket does not yet expose session controls or a foreground
service. When that later boundary exists, the validation must record device
model/API, amplitude capability, screen state, cue meaning, and any
discomfort or interruption.

## Definition of done

- The adapter has one Android-compatible production boundary for API 26-35.
- Supported and unsupported hardware paths are explicit and testable.
- Valid cues are short, bounded, conservative, and never continuous.
- Cancellation is idempotent and prevents stale delivery.
- Manifest permission, unit tests, packaged functionality tests, build, lint,
  review, static analysis, and automatic-validation evidence are terminal.
- No session lifecycle, UI, service, or medical claim is added outside this
  ticket.
