# Evidence Record - TICKET-011

**Repository:** `/home/ghiki/code/sleep-inducer`
**Branch:** `ticket/android-build-foundation`
**Base revision:** `ed5255f`
**Phase:** `PHASE-004`
**Feature:** `FEAT-010`
**Ticket:** `TICKET-011`
**Development mode:** automatic
**Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
`all-validation`
**Evidence path:** `evidence/TICKET-011/`
**Readiness:** blocked by external device and delivery gates

## Planning chain

`OBJ-001` -> `SCOPE-001` -> `CAP-002`, `CAP-003`, `CAP-004`, `CAP-006` ->
`PHASE-004` -> `FEAT-010` -> `TICKET-011`

## Acceptance coverage

1. Reproducible debug APK installs and launches on the supported emulator.
2. API 35 emulator display-off behavior is captured with environment details.
3. Physical-device, signing, provider, remote, and distribution gates are
   evidenced or recorded as exact blockers.

## EVID-001 - Emulator APK installation and offline launch

- **Category:** smoke
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Source references:** `README.md`,
  `app/src/androidTest/java/com/sleepinducer/app/LaunchFlowTest.kt`,
  `app/src/main/AndroidManifest.xml`
- **Command:** install `app/build/outputs/apk/debug/app-debug.apk` with
  `adb -s emulator-5554 install -r`, launch
  `com.sleepinducer.app`, capture `evidence/screenshots/setup-final.png`,
  and inspect package metadata.
- **Automated:** true
- **Assertions:** package `com.sleepinducer.app` targets API 35 with minimum
  API 26, launches without network permission, and exposes only the documented
  vibration, notification, and foreground-service permissions.
- **Expected:** the debug APK is reproducible, installable, and starts the
  offline setup boundary.
- **Observed:** install and launch completed on `emulator-5554`; package
  metadata reported `versionCode=1`, `minSdk=26`, `targetSdk=35`; no
  `android.permission.INTERNET` was requested; the setup screenshot was
  captured.
- **Status:** passed
- **Artifacts:** `app/build/outputs/apk/debug/app-debug.apk`,
  `evidence/screenshots/setup-final.png`

## EVID-002 - Emulator display-off and haptic evidence

- **Category:** automatedFunctionality
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Source references:** `app/src/androidTest/java/com/sleepinducer/app/ForegroundSessionServiceFlowTest.kt`,
  `evidence/TICKET-008/record.md`
- **Command:** `./gradlew connectedDebugAndroidTest --offline`; inspect
  `adb -s emulator-5554 shell dumpsys vibrator_manager` and capture
  `evidence/screenshots/active-final.png`.
- **Automated:** true
- **Test IDs:** `ForegroundSessionServiceFlowTest#continuesPhaseTimingWithTheDisplayOff`.
- **System boundary:** API 35 emulator foreground service, display state,
  monotonic phase scheduler, and vibrator manager.
- **Assertions:** the installed service continues from inhale to exhale with
  the display off, then restores the display; the emulator exposes amplitude
  control.
- **Expected:** emulator behavior is documented without implying physical
  device comfort or universal OEM behavior.
- **Observed:** the packaged test passed. `vibrator_manager` reported one
  vibrator with `AMPLITUDE_CONTROL`; the manual capture shows the active
  session before display-off. Physical-device comfort remains untested.
- **Status:** passedWithConcerns
- **Artifacts:** `app/build/reports/androidTests/connected/debug/index.html`,
  `evidence/screenshots/active-final.png`
- **Accepted warning:** emulator haptic output is not evidence of physical
  device comfort.

## EVID-003 - External delivery blockers

- **Category:** deployment
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Source references:** `.github/aidd-config.yml`,
  `docs/planning/tickets/open/TICKET-011-device-release-readiness.md`,
  `vision.md`
- **Command:** inspect branch/upstream/provider state with
  `git status --short --branch` and `git remote -v`; inspect the Android
  manifest's API 35 `specialUse` declaration.
- **Automated:** false
- **Assertions:** no release readiness claim is made without physical-device
  evidence, release signing, configured remote/provider checks, and Google
  Play approval for the selected service type.
- **Expected:** missing external capabilities remain explicit blockers.
- **Observed:** the branch has no configured upstream or remote; only a debug
  APK signing configuration is present; no physical Android device has been
  validated; Google Play `specialUse` approval is unresolved; and the
  repository's discovered workflow references missing CI assets.
- **Status:** blocked
- **Blocker:** physical-device screen-off/haptic comfort validation, release
  signing credentials, provider/remote configuration, remote checks, and
  Google Play `specialUse` approval are unavailable.

## Open blockers

- Physical-device screen-off timing and haptic comfort.
- Release signing and production artifact policy.
- Remote repository/upstream and remote-check configuration.
- Google Play approval for `specialUse`.
- Referenced workflow-evaluation assets are absent from the repository.

## EVID-004 - Post-remediation local artifact verification

- **Category:** smoke
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Source references:** `README.md`, `app/build/outputs/apk/debug/app-debug.apk`,
  `app/src/main/AndroidManifest.xml`
- **Command:** `./gradlew test lintDebug assembleDebug --offline`
- **Automated:** true
- **Assertions:** the current dedicated branch still produces the debug APK
  and passes local unit/lint checks after haptic cleanup remediation.
- **Expected:** local artifact generation succeeds without changing release
  blocker classification.
- **Observed:** exit code `0`; 27 debug and 27 release unit tests passed with
  zero failures or errors, lint passed, and the debug APK was rebuilt.
- **Status:** passed
- **Artifacts:** `app/build/outputs/apk/debug/app-debug.apk`,
  `app/build/reports/lint-results-debug.html`

## EVID-005 - Fresh instrumentation attempt blocked by emulator startup

- **Category:** smoke
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Source references:** `evidence/TICKET-008/record.md`,
  `app/src/androidTest/java/com/sleepinducer/app/SessionControlsFlowTest.kt`
- **Command:** `adb -s emulator-5554 shell am instrument -w -e class
  com.sleepinducer.app.SessionControlsFlowTest#showsDurationChoicesAndStartAction
  com.sleepinducer.app.test/androidx.test.runner.AndroidJUnitRunner`
- **Automated:** true
- **Assertions:** the API 35 instrumentation harness starts after the
  remediation.
- **Expected:** test execution begins before result collection.
- **Observed:** the runner reported `shortMsg=Process crashed` with code `0`;
  logcat reported `ANR in com.sleepinducer.app`,
  `failed to complete startup`, and cleanup before the test body ran.
- **Status:** blocked
- **Blocker:** emulator/system startup instability. This does not replace the
  prior complete 35-test API 35 pass, but it prevents a fresh terminal run.
