# Static Analysis - TICKET-008

**Run date:** 2026-09-14
**Run mode:** diff
**Scope:** live session engine, Android scheduler, foreground service
integration, Compose controls, haptic failure handling, tests, manifest,
resources, README, planning, and evidence changes
**Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
`all-validation`
**Status:** passedWithConcerns
**Local-to-PR parity:** notApplicable for Android application checks

## Tool results

| Category | Tool | Command | Exit | Status | Findings |
|---|---|---|---:|---|---|
| Formatting hygiene | Git | `git diff --check` | 0 | passed | None |
| Kotlin compilation and unit tests | Gradle wrapper | `./gradlew test --offline` | 0 | passed | 24 debug and 24 release unit tests passed |
| Android lint | Android Gradle Plugin lint | `./gradlew lintDebug --offline` | 0 | passed | No errors or warnings |
| APK build | Gradle Android build | `./gradlew assembleDebug --offline` | 0 | passed | `app/build/outputs/apk/debug/app-debug.apk` |
| Android functionality | Android instrumentation runner | `./gradlew connectedDebugAndroidTest --offline` | 0 | passed | 35 tests passed, 0 failures, 0 skips on `emulator-5554` API 35 |
| Hotspot analysis | `aidd@3.1.0` | `npx --no-install aidd churn --json` | 1 | skippedWithReason | Package unavailable locally; no installation performed |
| Workflow contract CI | Repository workflow | `python tools/eval_workflows.py --spec ai-evals/workflow-contracts.json --threshold 1.0` | 2 | blocked | Referenced script and spec are absent from the repository |

## Review classification

- No introduced Android lint, compilation, test, privacy, or scope finding
  was observed in the changed implementation.
- Haptic gateway security and system delivery exceptions are converted into an
  explicit failed delivery result, and the service stops rather than claiming a
  successful cue.
- The optional churn analyzer remains a coverage warning, not a functional or
  security pass.
- No Android pull-request build/test pipeline is configured. The discovered
  workflow-evaluation pipeline is not an Android parity target and currently
  references missing repository assets, so it remains a pre-existing delivery
  blocker outside this ticket's implementation scope.

## Raw artifacts

- `app/build/reports/lint-results-debug.html`
- `app/build/outputs/apk/debug/app-debug.apk`
- `app/build/reports/androidTests/connected/debug/index.html`

## EVID-008 - Post-remediation local analysis

- **Category:** staticAnalysis
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Command:** `git diff --check`; `./gradlew test lintDebug assembleDebug
  --offline`
- **Scope:** haptic cancellation remediation and the complete current
  implementation diff on `ticket/running-session-service`.
- **Observed:** `git diff --check` passed. The Gradle command exited `0`; 27
  debug and 27 release unit tests passed with zero failures or errors, Android
  lint reported no findings, and the debug APK was rebuilt.
- **Status:** passed
- **Artifacts:** `app/build/outputs/apk/debug/app-debug.apk`,
  `app/build/reports/lint-results-debug.html`

## EVID-009 - Targeted instrumentation availability

- **Category:** staticAnalysis
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Command:** `adb -s emulator-5554 shell am instrument -w -e class
  com.sleepinducer.app.SessionControlsFlowTest#showsDurationChoicesAndStartAction
  com.sleepinducer.app.test/androidx.test.runner.AndroidJUnitRunner`
- **Scope:** post-remediation API 35 packaged test startup.
- **Observed:** instrumentation reported `shortMsg=Process crashed` with code
  `0`; logcat identified an `ANR in com.sleepinducer.app` because the process
  failed to complete startup, followed by instrumentation cleanup. No
  application-specific fatal exception was reported.
- **Status:** blocked
- **Blocker:** emulator/system startup instability prevented execution of the
  selected test. The earlier complete 35-test API 35 run remains the terminal
  functionality result for the unchanged normal flows.
