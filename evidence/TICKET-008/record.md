# Evidence Record - TICKET-008

**Repository:** `/home/ghiki/code/sleep-inducer`
**Branch:** `ticket/android-build-foundation`
**Base revision:** `ed5255f`
**Phase:** `PHASE-003`
**Feature:** `FEAT-006`
**Ticket:** `TICKET-008`
**Development mode:** automatic
**Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
`all-validation`
**Evidence path:** `evidence/TICKET-008/`
**Readiness:** implementation complete; review and delivery gates pending

## Planning chain

`OBJ-001` -> `SCOPE-001` -> `CAP-001`, `CAP-002`, `CAP-003`, `CAP-004` ->
`PHASE-003` -> `FEAT-006` -> `TICKET-008`

## Acceptance coverage

1. Live runner and haptic ownership through the foreground service.
2. Natural completion and future-cue cancellation.
3. Interruption recovery after service/process destruction.
4. Notification stop action and terminal cleanup.

## Protected flows

- Five-second inhale and five-second exhale remain unchanged.
- Haptic delivery remains short, bounded, capability-aware, and cancellable.
- The service remains non-exported, non-sticky, and explicitly user-started.
- No health data, network, account, audio, analytics, or medical behavior is
  introduced.

## EVID-001 - Protected baseline

- **Category:** baseline
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-008`
- **Source references:** `docs/planning/tickets/closed/TICKET-007-foreground-session-service.md`,
  `docs/planning/features/open/FEAT-006-screen-off-session-continuity.md`,
  `docs/planning/phases/open/PHASE-003-screen-off-relaxation.md`
- **Command:** `./gradlew test lintDebug assembleDebug connectedDebugAndroidTest --offline`
- **Automated:** true
- **Assertions:** the completed service declaration and all prior protocol,
  runner, haptic, coordinator, and setup functionality remain green before
  live-session integration.
- **Expected:** current unit, lint, APK, and emulator functionality checks
  pass.
- **Status:** pending

## Open blockers

- Physical-device screen-off timing and haptic comfort remain release evidence
  work in `TICKET-011`.
- Google Play approval for the API 35 `specialUse` declaration remains an
  external release-policy gate.

## EVID-002 - Protected baseline rerun

- **Category:** baseline
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-008`
- **Source references:** `docs/planning/tickets/closed/TICKET-007-foreground-session-service.md`,
  `docs/planning/features/open/FEAT-006-screen-off-session-continuity.md`,
  `app/src/main/java/com/sleepinducer/app/session/ForegroundSessionContract.kt`
- **Command:** `./gradlew test lintDebug assembleDebug connectedDebugAndroidTest --offline`
- **Automated:** true
- **Assertions:** the protected foreground-service boundary, breathing protocol,
  runner, haptic adapter, coordinator, setup, and existing functionality flows
  remain green before and after live-session integration.
- **Expected:** unit tests, lint, APK assembly, and packaged Android
  functionality pass without regressions.
- **Observed:** the final rerun completed successfully with 24 debug unit
  tests, 24 release unit tests, 35 Android instrumentation tests on
  `emulator-5554` API 35, clean Android lint, and a rebuilt debug APK.
- **Status:** passed
- **Artifacts:** `app/build/outputs/apk/debug/app-debug.apk`,
  `app/build/reports/lint-results-debug.html`,
  `app/build/reports/androidTests/connected/debug/index.html`
- **Supersedes:** the pending `EVID-001` baseline entry.

## EVID-003 - Live-session implementation

- **Category:** implementation
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-008`
- **Source references:** `app/src/main/java/com/sleepinducer/app/breathing/BreathingSessionEngine.kt`,
  `app/src/main/java/com/sleepinducer/app/session/AndroidSessionScheduler.kt`,
  `app/src/main/java/com/sleepinducer/app/session/SharedPreferencesSessionStateStore.kt`,
  `app/src/main/java/com/sleepinducer/app/session/BreathingSessionService.kt`,
  `app/src/main/java/com/sleepinducer/app/haptics/HapticAdapter.kt`
- **Command:** implementation followed by the final repository verification
  command recorded in `EVID-004`.
- **Automated:** false
- **Assertions:** the service owns the live engine, absolute monotonic
  scheduling, phase cues, immutable snapshots, progress updates, notification
  controls, minimal operational persistence, interruption recovery, and
  explicit haptic-delivery failures.
- **Expected:** every terminal path cancels future work and reports an honest
  state without adding network, account, health, audio, or medical behavior.
- **Observed:** the service and UI are connected through the local binder,
  active phase changes are reflected in snapshots and notifications, terminal
  paths clear the scheduler and engine, persisted active state restores as
  interrupted, and haptic gateway security/system failures become explicit
  delivery failures instead of escaping from the session callback.
- **Status:** passed
- **Artifacts:** `app/src/main/java/com/sleepinducer/app/session/BreathingSessionService.kt`,
  `app/src/main/java/com/sleepinducer/app/breathing/BreathingSessionEngine.kt`,
  `app/src/main/java/com/sleepinducer/app/haptics/HapticAdapter.kt`

## EVID-004 - Final technical verification

- **Category:** qualityGate
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-008`
- **Source references:** `app/src/main/java/com/sleepinducer/app/session/`,
  `app/src/main/java/com/sleepinducer/app/breathing/`,
  `app/src/main/java/com/sleepinducer/app/haptics/`,
  `app/src/test/java/com/sleepinducer/app/`,
  `app/src/androidTest/java/com/sleepinducer/app/`
- **Command:** `./gradlew test lintDebug assembleDebug connectedDebugAndroidTest --offline`
- **Automated:** true
- **Assertions:** debug and release unit tests pass, Android lint is clean,
  the debug APK is assembled, and all packaged functionality tests pass.
- **Expected:** no regression in timing, haptics, service lifecycle, setup,
  controls, offline boundary, interruption, or cleanup.
- **Observed:** exit code `0`; 24 debug unit tests, 24 release unit tests, and
  35 Android instrumentation tests passed on `emulator-5554` API 35; lint
  passed without findings; `app/build/outputs/apk/debug/app-debug.apk` was
  rebuilt.
- **Status:** passed
- **Artifacts:** `app/build/outputs/apk/debug/app-debug.apk`,
  `app/build/reports/lint-results-debug.html`,
  `app/build/reports/androidTests/connected/debug/index.html`
- **Accepted warning:** the connected-device task also observed the unrelated
  `emulator-5556` test device as `Unknown API Level` and skipped it, while the
  configured API 35 device completed all tests. The optional emulator-console
  startup warning was non-blocking.

## EVID-005 - Packaged Android functionality

- **Category:** automatedFunctionality
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-008`
- **Source references:** `app/src/androidTest/java/com/sleepinducer/app/ForegroundSessionServiceFlowTest.kt`,
  `app/src/androidTest/java/com/sleepinducer/app/SessionEngineFlowTest.kt`,
  `app/src/androidTest/java/com/sleepinducer/app/SessionRunnerFlowTest.kt`
- **Command:** `./gradlew connectedDebugAndroidTest --offline`
- **Automated:** true
- **Test IDs:** `ForegroundSessionServiceFlowTest#startsForegroundServiceFromExplicitAction`,
  `ForegroundSessionServiceFlowTest#advancesActivePhaseThroughTheBoundService`,
  `ForegroundSessionServiceFlowTest#continuesPhaseTimingWithTheDisplayOff`,
  `ForegroundSessionServiceFlowTest#restoresInterruptedSessionAfterServiceDestruction`,
  `ForegroundSessionServiceFlowTest#notificationStopActionStopsTheLiveSession`,
  `ForegroundSessionServiceFlowTest#rejectsInvalidStartWithoutActiveState`,
  `SessionEngineFlowTest#completesAndCancelsFutureCuesThroughPackagedEngine`,
  `SessionRunnerFlowTest#completesOnceAtDuration`
- **System boundary:** installed debug APK, Android foreground-service
  lifecycle, local binder snapshots, notification stop action, display state,
  persisted operational state, packaged session engine, and haptic adapter.
- **Assertions:** valid starts become active with a phase and selected
  duration; phase timing continues with the display off; natural completion
  and explicit stop cancel future cues; service destruction restores an
  interrupted state; invalid starts never claim active execution; and the
  notification stop action reaches terminal cleanup.
- **Expected:** all TICKET-008 acceptance outcomes are observable through the
  packaged application boundary with no success-shaped interruption or
  unavailable-haptic state.
- **Observed:** all 35 Android instrumentation tests passed with zero
  failures and zero skips on `emulator-5554` API 35. The emulator exposes a
  vibrator with amplitude-control capability through `vibrator_manager`.
- **Status:** passed
- **Artifacts:** `app/build/reports/androidTests/connected/debug/index.html`,
  `evidence/screenshots/setup-final.png`,
  `evidence/screenshots/active-final.png`,
  `evidence/screenshots/stopped-final.png`

## EVID-006 - Automatic functionality validation

- **Category:** automaticValidation
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-008`
- **Source references:** `evidence/TICKET-008/record.md`,
  `app/src/androidTest/java/com/sleepinducer/app/ForegroundSessionServiceFlowTest.kt`,
  `app/src/androidTest/java/com/sleepinducer/app/SessionEngineFlowTest.kt`
- **Command:** `./gradlew connectedDebugAndroidTest --offline`
- **Automated:** true
- **Test IDs:** the complete test set listed in `EVID-005`.
- **System boundary:** packaged Android application and operating-system
  service, notification, display, persistence, and haptic contracts.
- **Assertions:** every TICKET-008 acceptance outcome has terminal packaged
  functionality evidence for live execution, completion/cancellation,
  interruption recovery, and notification stop cleanup.
- **Expected:** the configured automatic validator can classify the complete
  functionality charter as terminal without treating it as human confirmation.
- **Observed:** the complete API 35 functionality charter passed with no
  unasserted TICKET-008 outcome.
- **Status:** passed
- **Artifacts:** `app/build/reports/androidTests/connected/debug/index.html`

## EVID-007 - Final static analysis

- **Category:** staticAnalysis
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-008`
- **Source references:** `.github/aidd-config.yml`,
  `.github/workflows/workflow-evals.yml`,
  `evidence/static-analysis/TICKET-008.md`
- **Command:** `git diff --check`; `./gradlew test lintDebug assembleDebug connectedDebugAndroidTest --offline`;
  `npx --no-install aidd churn --json`
- **Automated:** true
- **Expected:** applicable deterministic checks pass, optional unavailable
  analysis is classified explicitly, and local-to-PR parity is not overstated.
- **Observed:** `git diff --check` and the complete Gradle suite exited `0`.
  Android lint reported no findings. The configured optional churn analyzer
  was unavailable because `aidd@3.1.0` is not installed locally. The only
  discovered CI workflow is the pre-existing workflow-evaluation job and no
  Android pull-request build/test pipeline exists, so Android local-to-PR
  parity is `notApplicable`.
- **Status:** passedWithConcerns
- **Artifacts:** `evidence/static-analysis/TICKET-008.md`,
  `app/build/reports/lint-results-debug.html`,
  `app/build/outputs/apk/debug/app-debug.apk`,
  `app/build/reports/androidTests/connected/debug/index.html`
- **Accepted warning:** optional churn coverage remains unavailable and the
  repository CI workflow references missing `tools/eval_workflows.py` and
  `ai-evals/workflow-contracts.json`; those pre-existing workflow assets were
  not changed by TICKET-008.

## EVID-008 - Dedicated branch and post-remediation local verification

- **Category:** qualityGate
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-008`
- **Source references:** `app/src/main/java/com/sleepinducer/app/haptics/HapticAdapter.kt`,
  `app/src/main/java/com/sleepinducer/app/breathing/PhaseHapticCoordinator.kt`,
  `app/src/main/java/com/sleepinducer/app/session/BreathingSessionService.kt`,
  `app/src/test/java/com/sleepinducer/app/haptics/HapticAdapterTest.kt`,
  `app/src/test/java/com/sleepinducer/app/breathing/BreathingSessionEngineTest.kt`
- **Command:** `./gradlew test lintDebug assembleDebug --offline`
- **Automated:** true
- **Assertions:** cancellation failures are converted into explicit haptic
  delivery failures, terminal service cleanup can surface that failure, unit
  tests pass, lint is clean, and the debug APK is rebuilt on the dedicated
  ticket branch.
- **Expected:** no exception escapes from haptic cancellation and no local
  regression is introduced.
- **Observed:** exit code `0`; the report XML contains 27 debug and 27 release
  unit tests with zero failures or errors; Android lint passed; and
  `app/build/outputs/apk/debug/app-debug.apk` was rebuilt on
  `ticket/running-session-service`.
- **Status:** passed
- **Artifacts:** `app/build/outputs/apk/debug/app-debug.apk`,
  `app/build/reports/lint-results-debug.html`
- **Fix:** `HapticAdapter.cancel()` now returns explicit delivery failures,
  `PhaseHapticCoordinator` propagates terminal cancellation failures, and the
  service converts them to a terminal session failure.

## EVID-009 - Targeted instrumentation harness blocker

- **Category:** qualityGate
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-008`
- **Source references:** `app/src/androidTest/java/com/sleepinducer/app/SessionControlsFlowTest.kt`,
  `evidence/TICKET-008/record.md`
- **Command:** `adb -s emulator-5554 shell am instrument -w -e class
  com.sleepinducer.app.SessionControlsFlowTest#showsDurationChoicesAndStartAction
  com.sleepinducer.app.test/androidx.test.runner.AndroidJUnitRunner`
- **Automated:** true
- **Test IDs:** `SessionControlsFlowTest#showsDurationChoicesAndStartAction`
- **System boundary:** API 35 emulator instrumentation startup.
- **Assertions:** the targeted packaged test can start and execute after the
  haptic remediation.
- **Expected:** the test runner starts and reports the test result.
- **Observed:** instrumentation returned `shortMsg=Process crashed` with code
  `0`; emulator logcat reported `ANR in com.sleepinducer.app`,
  `failed to complete startup`, and an instrumentation crash after the
  process was killed. No application-specific fatal exception was reported.
- **Status:** blocked
- **Blocker:** emulator/system startup ANR prevents the test body from
  executing. The earlier complete 35-test API 35 run remains the terminal
  packaged-functionality result for unchanged normal flows, while this
  attempted rerun is retained as an infrastructure warning.
