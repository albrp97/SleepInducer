# Sleep Inducer Agent Guidance

This file contains project-specific guidance. The repository-wide workflow
remains in [`.github/copilot-instructions.md`](.github/copilot-instructions.md).

## Canonical Context

- Vision: [`vision.md`](vision.md)
- Repository map: [`docs/planning/repo-map.md`](docs/planning/repo-map.md)
- Scope handoff: [`docs/specs/project-scope.md`](docs/specs/project-scope.md)
- Research baseline: [`docs/research/sleep-onset-evidence.md`](docs/research/sleep-onset-evidence.md)
- Delivery configuration: [`.github/aidd-config.yml`](.github/aidd-config.yml)
- Workflow overview: [`.github/README.md`](.github/README.md)

The machine-readable development-mode source is
`.github/aidd-config.yml:delivery.development.mode`. The selected mode is
`automatic`.

## Development Mode

After the verified bootstrap handoff, automatic mode continues through
approved planning, implementation, technical verification, automated
functionality testing, review/remediation, and configured delivery without
asking or waiting for another routine user response. It must still stop for
real blockers such as missing parents, contradictory scope, unavailable
required tools, credentials, provider restrictions, branch protection, or
remote checks.

Every automatic validation decision uses the exact configured profile:

- Validator: `rubber-duck`
- Model: `gpt-5.6-luna`
- Reasoning effort: `high`
- Scope: `all-validation`

The profile is authoritative at
`.github/aidd-config.yml:delivery.development.automatic_validation`.

## Environment and Commands

- Use JDK 17 and Android SDK 35.
- Build the debug APK with `./gradlew assembleDebug`.
- Run repository unit tests with `./gradlew test`.
- Run Android lint with `./gradlew lintDebug`.
- Run Android functionality tests with
  `./gradlew connectedDebugAndroidTest` on a connected emulator or device.
- The expected debug APK path is
  `app/build/outputs/apk/debug/app-debug.apk`.
- The current application ID is `com.sleepinducer.app`, with minimum API 26
  and compile/target API 35.
- The repository wrapper is the source of truth for Gradle execution. Empty
  command arrays in the configuration mean discovery is required, not that
  checks may be skipped.

## Architecture Boundaries

- The breathing domain owns phase timing, duration, stop, completion, and
  state transitions. It must remain testable without Android classes.
- The Android session service owns lifecycle continuity while the display is
  off and must surface interruption or start failures.
- The haptic adapter owns `Vibrator`/`VibratorManager` calls, amplitude
  capability detection, cancellation, and API compatibility.
- The UI owns setup, duration selection, start/stop controls, status, and
  honest hardware/error messaging. It must not embed timing policy.
- Keep research and safety claims in the research and scope artifacts rather
  than duplicating unsupported claims in UI code.

## Safety and Privacy

- Treat the app as a wellness/relaxation aid, never as a diagnosis or
  treatment for insomnia.
- Do not add mandatory breath holds, hyperventilation, or performance
  challenges to the initial protocol.
- Provide a visible stop action before screen-off operation and stop all future
  cues when the session ends or is interrupted.
- Tell users to stop for dizziness, shortness of breath, air hunger, pain,
  panic, or discomfort.
- Do not store or transmit health data, sleep scores, recordings, typed
  responses, private notes, or unnecessary session content.
- Do not log session duration, haptic settings, or user-provided information
  unless the log is required for a local failure and is appropriately
  redacted.
- Avoid continuous or intense vibration. Validate cues on real devices with
  the phone in the intended position and stop if the hardware is
  uncomfortable.

## Evidence and Validation

- Preserve a protected baseline before changing implementation behavior.
- Every acceptance outcome needs an executable automated functionality test
  through the Android application boundary, separate from unit tests.
- Test screen-on and screen-off operation, lock/interruption behavior, stop,
  completion, missing vibrator hardware, and persistence or service failures.
- Record raw command output, environment, device/API level, haptic capability,
  and the automatic validation classification separately.
- Do not use success-shaped fallbacks for unavailable haptics, service start
  failures, or persistence failures. Surface the failure clearly.

## Scope and Delivery

- Preserve the breathing-only first-release boundary in `SCOPE-001`.
- Route material changes to protocol, safety, data collection, or platform
  behavior through the configured change-control workflow.
- Do not create branches, commits, pushes, pull requests, or releases as a
  side effect of project bootstrap or planning.
- Keep planning records in the configured open/closed layout and preserve
  stable IDs and index synchronization.
