# Evidence Record - TICKET-009

**Repository:** `/home/ghiki/code/sleep-inducer`
**Branch:** `ticket/android-build-foundation`
**Base revision:** `ed5255f`
**Phase:** `PHASE-003`
**Feature:** `FEAT-007`
**Ticket:** `TICKET-009`
**Development mode:** automatic
**Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
`all-validation`
**Evidence path:** `evidence/TICKET-009/`
**Readiness:** implementation complete; review and delivery gates pending

## Planning chain

`OBJ-001` -> `SCOPE-001` -> `CAP-004`, `CAP-005`, `CAP-006` -> `PHASE-003` ->
`FEAT-007` -> `TICKET-009`

## Acceptance coverage

1. Visible duration choices and start action.
2. Selected duration reaches the active service session.
3. Prominent stop control stops the session and cancels future cues.
4. Honest haptic, notification, service, interruption, safety, and offline
   messaging.

## Protected flows

- The UI does not own timing policy or replace the foreground service.
- A stop action is visible before screen-off operation.
- Missing haptics and service failures remain explicit.
- No account, network, health data, session history, or medical claim is
  introduced.

## EVID-001 - Implementation and baseline

- **Category:** implementation
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-009`
- **Source references:** `app/src/main/java/com/sleepinducer/app/MainActivity.kt`,
  `app/src/main/res/values/strings.xml`,
  `app/src/androidTest/java/com/sleepinducer/app/SessionControlsFlowTest.kt`
- **Command:** `./gradlew test lintDebug assembleDebug connectedDebugAndroidTest --offline`
- **Automated:** true
- **Assertions:** Compose controls are bound to the service snapshot and render
  active and terminal states without embedding breathing timing.
- **Expected:** supported durations, start, active phase, remaining time, stop,
  safety copy, offline copy, and failure messages are observable.
- **Observed:** the final command passed with the UI functionality tests
  listed below, clean lint, and a rebuilt APK.
- **Status:** passed
- **Artifacts:** `app/build/outputs/apk/debug/app-debug.apk`,
  `evidence/screenshots/setup-final.png`,
  `evidence/screenshots/active-final.png`,
  `evidence/screenshots/stopped-final.png`

## EVID-002 - Packaged Android functionality

- **Category:** automatedFunctionality
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-009`
- **Source references:** `app/src/androidTest/java/com/sleepinducer/app/SessionControlsFlowTest.kt`,
  `app/src/androidTest/java/com/sleepinducer/app/ForegroundSessionServiceFlowTest.kt`,
  `app/src/androidTest/java/com/sleepinducer/app/SetupFlowTest.kt`,
  `app/src/androidTest/java/com/sleepinducer/app/LaunchFlowTest.kt`
- **Command:** `./gradlew connectedDebugAndroidTest --offline`
- **Automated:** true
- **Test IDs:** `SessionControlsFlowTest#showsDurationChoicesAndStartAction`,
  `SessionControlsFlowTest#selectedDurationIsUsedByTheActiveSession`,
  `SessionControlsFlowTest#startsAndStopsFromVisibleControls`,
  `ForegroundSessionServiceFlowTest#rejectsInvalidStartWithoutActiveState`,
  `SetupFlowTest#showsStopIfUncomfortableGuidance`,
  `SetupFlowTest#opensWithoutAccountOrNetwork`,
  `LaunchFlowTest#manifestHasNoNetworkPermission`
- **System boundary:** installed APK Compose UI, bound foreground service,
  Android permission/manifest boundary, and visible terminal state.
- **Assertions:** duration choices and start are displayed, the selected
  twenty-minute duration reaches the active UI, visible stop reaches the
  stopped state, safety/offline copy is displayed, invalid start is not active,
  and no Internet permission is requested.
- **Expected:** every TICKET-009 acceptance outcome is observable through the
  packaged app boundary.
- **Observed:** all 35 instrumentation tests passed with zero failures and zero
  skips on the API 35 emulator.
- **Status:** passed
- **Artifacts:** `app/build/reports/androidTests/connected/debug/index.html`

## EVID-003 - Automatic functionality validation

- **Category:** automaticValidation
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-009`
- **Source references:** `evidence/TICKET-009/record.md`,
  `app/src/androidTest/java/com/sleepinducer/app/SessionControlsFlowTest.kt`
- **Command:** `./gradlew connectedDebugAndroidTest --offline`
- **Automated:** true
- **Test IDs:** the complete TICKET-009 functionality set listed in `EVID-002`.
- **System boundary:** packaged Compose/application/service flow.
- **Assertions:** each control, selected-duration, safety, offline, failure, and
  terminal-state outcome is asserted by an executable Android test.
- **Expected:** automatic validation is terminal without being represented as
  human confirmation.
- **Observed:** the API 35 functionality charter passed with no unasserted
  TICKET-009 outcome.
- **Status:** passed
- **Artifacts:** `app/build/reports/androidTests/connected/debug/index.html`

## Open blockers

- Final review, commit, and lifecycle closeout are shared with the current
  implementation checkpoint.
- Physical-device comfort and release distribution remain in `TICKET-011`.

## EVID-004 - Dedicated branch and post-remediation local verification

- **Category:** qualityGate
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-009`
- **Source references:** `app/src/main/java/com/sleepinducer/app/MainActivity.kt`,
  `app/src/main/java/com/sleepinducer/app/session/BreathingSessionService.kt`,
  `app/src/main/java/com/sleepinducer/app/haptics/HapticAdapter.kt`
- **Command:** `./gradlew test lintDebug assembleDebug --offline`
- **Automated:** true
- **Assertions:** the Compose/service integration remains compilable after
  haptic cancellation failure handling was tightened.
- **Expected:** unit tests, lint, and APK assembly pass without changing the
  visible start, active, or stop flow.
- **Observed:** exit code `0`; 27 debug and 27 release unit tests passed with
  zero failures or errors, lint passed, and the debug APK was rebuilt on
  `ticket/running-session-service`.
- **Status:** passed
- **Artifacts:** `app/build/outputs/apk/debug/app-debug.apk`,
  `app/build/reports/lint-results-debug.html`

## EVID-005 - Targeted instrumentation harness blocker

- **Category:** qualityGate
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-009`
- **Source references:** `app/src/androidTest/java/com/sleepinducer/app/SessionControlsFlowTest.kt`,
  `evidence/TICKET-009/record.md`
- **Command:** `adb -s emulator-5554 shell am instrument -w -e class
  com.sleepinducer.app.SessionControlsFlowTest#showsDurationChoicesAndStartAction
  com.sleepinducer.app.test/androidx.test.runner.AndroidJUnitRunner`
- **Automated:** true
- **Test IDs:** `SessionControlsFlowTest#showsDurationChoicesAndStartAction`
- **System boundary:** API 35 emulator instrumentation startup.
- **Assertions:** the packaged Compose control test can start after the
  remediation.
- **Expected:** the test runner starts and executes the selected test.
- **Observed:** the runner reported `shortMsg=Process crashed`; logcat
  identified an `ANR in com.sleepinducer.app` because the process failed to
  complete startup, followed by instrumentation cleanup. No app-specific
  fatal exception was observed.
- **Status:** blocked
- **Blocker:** emulator/system startup instability prevented test execution.
  The prior complete 35-test API 35 pass remains the terminal result for the
  unchanged normal control flow.

## EVID-006 - Implementation commit

- **Category:** commit
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-009`
- **Source references:** `docs/planning/reviews/TICKET-009-review.md`,
  `evidence/static-analysis/TICKET-008.md`
- **Command:** `git commit -m "feat(android): add live session"`
- **Automated:** false
- **Expected:** the reviewed Compose control integration and its linked
  implementation, tests, planning, evidence, and documentation are captured
  in one scoped local commit.
- **Observed:** commit
  `f597bce98393671fc5c43ccf9e2f09a1397cb66e` was created on
  `ticket/running-session-service` with the required Copilot co-author trailer
  and 39 intended paths. No upstream is configured.
- **Status:** passed
- **Artifacts:** commit
  `f597bce98393671fc5c43ccf9e2f09a1397cb66e`
- **Next action:** configure an approved remote/upstream and run the configured
  push workflow.
