# Evidence Record - TICKET-011

**Repository:** `/home/ghiki/code/sleep-inducer`
**Branch:** `ticket/safe-session-controls`
**Base revision:** `fc4cc43`
**Phase:** `PHASE-004`
**Feature:** `FEAT-010`
**Ticket:** `TICKET-011`
**Development mode:** automatic
**Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
`all-validation`
**Evidence path:** `evidence/TICKET-011/`
**Readiness:** blocked by GitHub signing-secret setup, required automatic
validation classification, and external device/distribution gates

## Planning chain

`OBJ-001` -> `SCOPE-001` -> `CAP-002`, `CAP-003`, `CAP-004`, `CAP-006` ->
`PHASE-004` -> `FEAT-010` -> `TICKET-011`

## Acceptance coverage

1. Reproducible debug APK installs and launches on the supported emulator.
2. API 35 emulator display-off behavior is captured with environment details.
3. Physical-device, signing, provider, remote, and distribution gates are
   evidenced or recorded as exact blockers.
4. GitHub's `v0.1.0` release contains a signed APK that verifies, installs, and
   launches on the API 35 emulator; the unsigned asset is removed.

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

## EVID-006 - Implementation checkpoint commit

- **Category:** commit
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Source references:** `docs/planning/reviews/TICKET-008-review.md`,
  `evidence/static-analysis/TICKET-008.md`
- **Command:** `git commit -m "feat(android): add live session"`
- **Automated:** false
- **Expected:** the implementation checkpoint and its explicit release
  blockers are recorded without claiming production readiness.
- **Observed:** commit
  `f597bce98393671fc5c43ccf9e2f09a1397cb66e` was created on
  `ticket/running-session-service` with the required Copilot co-author trailer
  and 39 intended paths. No upstream is configured.
- **Status:** passedWithConcerns
- **Artifacts:** commit
  `f597bce98393671fc5c43ccf9e2f09a1397cb66e`
- **Accepted warning:** physical-device screen-off/haptic comfort evidence,
  release signing, provider/remote checks, and Google Play `specialUse`
  approval remain blocked.

## EVID-007 - Reproducing the public release install failure

- **Timestamp:** `2026-09-24`
- **Category:** baseline
- **Owner:** agent
- **Source:** GitHub release `v0.1.0`,
  `sleep-inducer-release-unsigned.apk`
- **Command:** download the published APK and run
  `apksigner verify --verbose`
- **Expected:** a release APK accepted by Android signature verification.
- **Observed:** verification failed with `DOES NOT VERIFY` and
  `Missing META-INF/MANIFEST.MF`; GitHub reported SHA-256
  `81db3287c9aeea956837193f5d9cf8b1466f844892f02b5ae0e36b9329cce028`.
- **Status:** failure reproduced

## EVID-008 - Building and installing the signed release APK

- **Timestamp:** `2026-09-24`
- **Category:** automatedFunctionality
- **Owner:** agent
- **Command:** `./gradlew test lintDebug assembleRelease --offline --no-daemon`
  with the protected local release-signing environment values.
- **Signature check:** `apksigner verify --verbose` passed with one RSA signer.
- **Package assertion:** `com.sleepinducer.app`, min API 26, target API 35.
- **Functionality command:** `adb install -r
  app/build/outputs/apk/release/app-release.apk`,
  `adb shell am start -W -n com.sleepinducer.app/.MainActivity`, and
  `adb shell pidof com.sleepinducer.app`.
- **System boundary:** signed APK installed and launched on the API 35
  `sleep-inducer-api35` emulator.
- **Expected:** Android accepts the signed package, starts the main activity,
  and keeps the application process running.
- **Observed:** Gradle build succeeded; emulator install returned `Success`,
  launch returned `Status: ok`, and the application process was present.
- **APK SHA-256:** `b63fb675f46bc4940127beb5f5385c1306fa50dfbbd96cdc5194432f4b9994dc`.
- **Local artifact:** `/home/ghiki/Downloads/sleep-inducer-release.apk`,
  verified byte-for-byte against the build output.
- **Status:** passed for the local signed artifact; the GitHub release has not
  yet been updated.

## EVID-009 - Rejecting release builds without signing inputs

- **Timestamp:** `2026-09-24`
- **Category:** regression
- **Owner:** agent
- **Command:** `./gradlew assembleRelease --offline --no-daemon` without
  release-signing environment values.
- **Expected:** fail before producing an unsigned release APK and name the
  missing signing inputs.
- **Observed:** Gradle failed with `Release APKs must be signed` and listed
  the four required environment-variable names.
- **Status:** passed; the unsigned-release regression is blocked.

## Current release blockers

- Configure repository Actions secrets
  `ANDROID_KEYSTORE_BASE64`, `ANDROID_KEYSTORE_PASSWORD`,
  `ANDROID_KEY_ALIAS`, and `ANDROID_KEY_PASSWORD` from the locally protected
  signing files in `/home/ghiki/.config/sleep-inducer/release-signing/`.
- The local tests establish raw results, but the required configured
  `rubber-duck` / `gpt-5.6-luna` / high / `all-validation` classification has
  not been run.
- Physical-device haptic comfort and Google Play `specialUse` approval remain
  outside this APK installation fix.
- The current `origin` remote and branch tracking configuration supersede the
  historical no-remote observation in EVID-003; publication of this release
  fix is still pending.

## EVID-010 - Local signed release fix and publication gate

- **Timestamp:** `2026-09-24`
- **Category:** gate
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Required automatic validation profile (not executed):**
  `rubber-duck` / `gpt-5.6-luna` / high / `all-validation`.
- **Raw technical commands:** `./gradlew clean --offline --no-daemon`, then
  `./gradlew test lintDebug assembleRelease --offline --no-daemon` with
  protected local signing environment values;
  `apksigner verify --verbose --print-certs`;
  `apkanalyzer manifest application-id`; API 35 emulator install and activity
  launch; YAML parse and `bash -n` for workflow scripts.
- **Raw observed results:** clean Gradle build succeeded, unit tests and lint
  completed successfully, the APK passed Android signature verification with
  one RSA signer, package ID is `com.sleepinducer.app`, the API 35 emulator
  accepted and launched the APK, and workflow YAML and embedded shell syntax
  checks passed.
- **Signer certificate SHA-256:** `f983cd5963b821bff295cb349cc43ef074f3ee76aa467900722f06eabdef5566`.
- **Signed APK SHA-256:** `089fdbdcebaefd6164b2fae17b4b798bd70ed059c075e5b89c8fb6e69812b25c`.
- **Supersedes:** the earlier APK hash in EVID-008 was produced before the
  final stable keystore was selected and must not be used. This entry records
  the final pinned signer and artifact hash.
- **Local artifact:** `/home/ghiki/Downloads/sleep-inducer-release.apk`.
- **Expected:** the repository release workflow publishes this stable-key
  signed APK and removes the old unsigned asset.
- **Observed:** the release changes remain local and the public `v0.1.0`
  release is still unsigned because its four Actions secrets have not been
  configured. The optional churn analyzer is unavailable because
  `aidd@3.1.0` is not installed; no tooling was installed.
- **Status:** blocked
- **Blockers:** GitHub Actions signing secrets are not set, and the required
  Rubber Duck validation profile has not been invoked. No claim is made that
  the remote release is fixed yet.

## EVID-011 - Signed v0.1.1 APK install and launch

- **Timestamp:** `2026-09-24`
- **Category:** automatedFunctionality
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Requirement:** acceptance criterion 4, signed release APK install and
  launch.
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Test ID:** `release-apk-api35-install-launch`
- **Automated:** yes
- **System boundary:** signed release APK installed on the API 35 Google APIs
  emulator (`sdk_gphone64_x86_64`).
- **Command:**
  `adb install -r app/build/outputs/apk/release/app-release.apk`,
  `adb shell am start -W -n com.sleepinducer.app/.MainActivity`, and
  `adb shell pidof com.sleepinducer.app`.
- **Assertions:** Android accepts the APK, the main activity reports
  `Status: ok`, and the application process remains running.
- **Expected:** the signed APK installs and launches on API 35.
- **Observed:** install returned `Success`, launch returned `Status: ok`, and
  `pidof` returned a running process.
- **APK metadata:** package `com.sleepinducer.app`, version name `0.1.1`,
  version code `2`.
- **Signer certificate SHA-256:**
  `f983cd5963b821bff295cb349cc43ef074f3ee76aa467900722f06eabdef5566`.
- **APK SHA-256:**
  `c3defc2807f58b0c43331d0eca98a503bb27fb5bbb5be56d1c31664f8a95d2fb`.
- **Artifacts:** `app/build/outputs/apk/release/app-release.apk`,
  `/home/ghiki/Downloads/sleep-inducer-release.apk`.
- **Status:** passed for the local signed v0.1.1 APK. This does not establish
  that the public GitHub release has been updated.

## EVID-012 - Final diff static analysis

- **Timestamp:** `2026-09-24`
- **Category:** staticAnalysis
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Mode and scope:** diff analysis of release signing, tag-triggered
  publishing, version `0.1.1`, documentation, and delivery evidence.
- **Commands:** `git diff --check` and
  `./gradlew test lintDebug assembleRelease --offline --no-daemon
  --console=plain`.
- **Observed:** both commands succeeded. Android Gradle Plugin Lint 8.7.3
  reported four warnings in unchanged `app/src/main/res/values/strings.xml`.
  The configured `evidence/static-analysis/baseline.json` records those
  pre-existing warnings; none is introduced by this diff. The optional
  `npx --no-install aidd churn --json` check remains unavailable because
  `aidd@3.1.0` is not installed.
- **Local-to-PR parity:** `notApplicable`; the repository has no Android
  pull-request build/test pipeline.
- **Artifacts:** `evidence/static-analysis/baseline.json`,
  `evidence/static-analysis/TICKET-011-signed-release.md`,
  `evidence/static-analysis/TICKET-011-signed-release.json`,
  `evidence/static-analysis/TICKET-011-signed-release.sarif`, and
  `app/build/reports/lint-results-debug.xml`.
- **Status:** `passedWithConcerns`; optional churn coverage is unavailable and
  the four existing lint warnings remain visible.

## EVID-013 - GitHub release remains blocked

- **Timestamp:** `2026-09-24`
- **Category:** gate
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Check:** queried the GitHub `v0.1.0` release and release workflow runs.
- **Expected:** the downloadable release asset is signed and corresponds to
  the corrected release.
- **Observed:** the public `v0.1.0` release still contains only
  `sleep-inducer-release-unsigned.apk` (SHA-256
  `81db3287c9aeea956837193f5d9cf8b1466f844892f02b5ae0e36b9329cce028`).
  Signing secrets are not configured, so no signed remote release has run.
- **Decision:** publish a new `v0.1.1` release rather than moving the existing
  `v0.1.0` tag; this preserves the configured `allow_force: false` policy.
- **Status:** blocked.
- **Blocker:** the four repository Actions signing secrets must be configured
  before the signed workflow can run. The available GitHub integration has no
  Actions-secret write operation, so no secret values were transmitted.

## EVID-014 - Superseding the release acceptance target

- **Timestamp:** `2026-09-24`
- **Category:** gate
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Supersedes:** the initial acceptance coverage at the start of this record
  that described replacing the v0.1.0 asset.
- **Current acceptance target:** publish and verify signed `v0.1.1` with APK
  versionName `0.1.1` and versionCode `2`. Preserve the existing `v0.1.0` tag
  and its unsigned asset; do not claim it was fixed.
- **Rationale:** the selected new version avoids force-moving a tag, consistent
  with the configured `allow_force: false` policy.
- **Status:** blocked until repository signing secrets are configured and the
  v0.1.1 workflow completes successfully.

## EVID-015 - Release workflow syntax and version contract

- **Timestamp:** `2026-09-24`
- **Category:** qualityGate
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Commands:** parsed `.github/workflows/release-apk.yml` with PyYAML 6.0.3
  and ran `bash -n` on all six embedded shell blocks; checked APK versionName
  with Android SDK Build Tools 35.0.0 against tag `v0.1.1`.
- **Expected:** workflow shell syntax is valid and APK versionName matches
  the version tag.
- **Observed:** YAML parsed, all six scripts passed `bash -n`, and APK version
  `0.1.1` matched tag `v0.1.1`.
- **Status:** passed.
- **Artifacts:** `.github/workflows/release-apk.yml`,
  `app/build/outputs/apk/release/app-release.apk`.

## EVID-016 - Automatic validation of local signed APK functionality

- **Timestamp:** `2026-09-24`
- **Category:** automaticValidation
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Test ID:** `release-apk-api35-install-launch`
- **Automated:** yes
- **System boundary:** local signed v0.1.1 APK on the API 35 Google APIs
  emulator.
- **Executable steps:** install
  `app/build/outputs/apk/release/app-release.apk`, launch
  `com.sleepinducer.app/.MainActivity` with `adb shell am start -W`, then
  verify `adb shell pidof com.sleepinducer.app` returns a process.
- **Assertions:** APK installation succeeds, activity launch reports
  `Status: ok`, and the app process remains running.
- **Expected:** Android accepts and launches the signed release APK.
- **Observed:** all three assertions passed. The final exact-profile review
  classified this local artifact functionality outcome as passed.
- **Status:** passed for local signed APK functionality only.
- **Limitation:** this does not validate or publish a GitHub release.
  `v0.1.0` remains unsigned and no remote `v0.1.1` release exists. See
  EVID-013 for the blocked remote gate.
- **Artifacts:** EVID-011 raw test result,
  `app/build/outputs/apk/release/app-release.apk`,
  `/home/ghiki/Downloads/sleep-inducer-release.apk`.

## EVID-017 - Corrected Gradle verification environment

- **Timestamp:** `2026-09-24`
- **Category:** qualityGate
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Commands:** `./gradlew clean --offline --no-daemon --console=plain`, then
  `./gradlew test lintDebug assembleRelease --offline --no-daemon
  --console=plain`, with JDK 17, Android SDK 35, and protected local signing
  inputs in the process environment.
- **Initial failure:** the first test/build attempt omitted
  `ANDROID_HOME`/`ANDROID_SDK_ROOT` and failed with `SDK location not found`.
- **Fix:** set both SDK environment variables to the installed Android SDK
  path and rerun the build.
- **Observed:** clean succeeded, then unit tests, lint, release compilation,
  and signed APK assembly all completed with exit code 0.
- **Status:** passed after environment correction.

## EVID-018 - Local commit

- **Timestamp:** `2026-09-24`
- **Category:** commit
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Commit:** `dc2a4de14c4a0ec0cdf7bc79d3280c373788053b`
- **Subject:** `fix(release): sign v0.1.1 APK`
- **Source branch:** `ticket/safe-session-controls`
- **Intended base/upstream:** `origin/ticket/safe-session-controls` at
  `fc4cc43dcc65b13c0ffc400fff547b096a9d4c23`.
- **Committed paths:** `.github/workflows/release-apk.yml`, `README.md`,
  `app/build.gradle.kts`,
  `docs/planning/tickets/open/TICKET-011-device-release-readiness.md`,
  `evidence/TICKET-011/record.md`,
  `evidence/static-analysis/baseline.json`,
  `evidence/static-analysis/TICKET-011-signed-release.md`,
  `evidence/static-analysis/TICKET-011-signed-release.json`, and
  `evidence/static-analysis/TICKET-011-signed-release.sarif`.
- **Readiness references:** EVID-011 through EVID-017; exact-profile review
  found no introduced defect and passed local APK functionality.
- **Observed:** local commit created with the configured `albrp97` Git
  identity. The branch is one commit ahead of its upstream; push and remote
  workflow checks are pending.
- **Status:** passed as a local commit only; remote release is not yet
  delivered.

## EVID-019 - Ticket branch publication

- **Timestamp:** `2026-09-24`
- **Category:** push
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Remote:** `origin` (`albrp97/SleepInducer`).
- **Source branch:** `ticket/safe-session-controls`.
- **Published commit:** `dc2a4de14c4a0ec0cdf7bc79d3280c373788053b`.
- **Command:** `git push origin ticket/safe-session-controls`.
- **Observed:** push succeeded without force. GitHub branch ref was verified
  to resolve to the published commit.
- **Status:** passed.
- **PR state:** no PR exists for this branch; repository branch rules reported
  no required pull-request rules.

## EVID-020 - v0.1.1 release tag publication

- **Timestamp:** `2026-09-24`
- **Category:** push
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Tag:** annotated `v0.1.1`, targeting
  `dc2a4de14c4a0ec0cdf7bc79d3280c373788053b`.
- **Command:** `git tag -a v0.1.1 HEAD -m 'Sleep Inducer v0.1.1'`, then
  `git push origin refs/tags/v0.1.1`.
- **Observed:** new tag pushed without moving or rewriting `v0.1.0`; remote
  tag object `67b60c8f2dc280ddc135aef143758c7793bf840b` was verified.
- **Status:** passed for tag publication. GitHub Actions run
  `35977783546` is still in progress; no release asset is claimed yet.

## EVID-021 - Hosted API 35 launch assertion failure

- **Timestamp:** `2026-09-24`
- **Category:** automatedFunctionality
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Requirement:** acceptance criterion 4, install and launch the signed
  release APK on API 35.
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Test ID:** `release-apk-api35-install-launch`
- **System boundary:** GitHub Actions API 35 emulator running the signed
  `v0.1.1` APK.
- **Command:** release workflow step ran `adb install -r ...`,
  `adb shell am start -n com.sleepinducer.app/.MainActivity`, waited three
  seconds, then ran `adb shell pidof com.sleepinducer.app`.
- **Expected:** installation succeeds, the activity starts, and the app
  process remains running.
- **Observed:** build, signature/package verification, version-tag assertion,
  and APK installation succeeded. The process check returned exit code 1, so
  the emulator step failed and release publication was skipped.
- **Artifact:** GitHub Actions run
  `https://github.com/albrp97/SleepInducer/actions/runs/35977783546`.
- **Status:** failed; the current emulator launch assertion does not reliably
  observe a running app process.
- **Fix direction:** wait for activity startup and retry the process check,
  reporting filtered Android runtime/activity errors if the process remains
  absent. Preserve the API 35 installation and process assertions.

## EVID-022 - Corrected API 35 launch retry test

- **Timestamp:** `2026-09-24`
- **Category:** automatedFunctionality
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Requirement:** acceptance criterion 4, signed release APK install and
  launch.
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Test ID:** `release-apk-api35-install-launch`
- **Automated:** yes
- **System boundary:** signed local `v0.1.1` APK installed on a clean API 35
  Google APIs emulator.
- **Command:** `adb install -r app/build/outputs/apk/release/app-release.apk`,
  `adb shell am start -W -n com.sleepinducer.app/.MainActivity`, then retry
  `adb shell pidof com.sleepinducer.app` up to five times with two-second
  waits.
- **Assertions:** installation succeeds, activity launch reports
  `Status: ok`, and the process is found within the retry window.
- **Expected:** the signed APK installs and its main activity remains
  running on API 35.
- **Observed:** install returned `Success`, launch returned `Status: ok`, and
  the retry check observed a running process.
- **Status:** passed locally. Hosted workflow confirmation is still required.
- **Artifacts:** `app/build/outputs/apk/release/app-release.apk`,
  `/home/ghiki/Downloads/sleep-inducer-release.apk`.

## EVID-023 - Safe same-tag workflow rerun

- **Timestamp:** `2026-09-24`
- **Category:** qualityGate
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Change:** added manual dispatch with a tag-only job guard. The workflow
  still checks out `github.ref`, derives `RELEASE_TAG` from that same ref, and
  asserts APK versionName matches the tag. This permits retrying v0.1.1
  without moving or rewriting its tag.
- **Check:** parsed the workflow YAML and ran `bash -n` on all six embedded
  shell blocks; `git diff --check` passed.
- **Observed:** the job guard requires `refs/tags/v...`; all syntax checks
  passed.
- **Status:** passed locally. Manual hosted rerun remains pending.

## EVID-024 - Automatic validation of corrected launch check

- **Timestamp:** `2026-09-24`
- **Category:** automaticValidation
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Test ID:** `release-apk-api35-install-launch`
- **Automated:** yes
- **System boundary:** signed local `v0.1.1` APK on a clean API 35 Google APIs
  emulator.
- **Executable steps:** install the signed APK, launch the main activity with
  `adb shell am start -W`, assert `Status: ok`, then retry the process check
  up to five times with two-second waits.
- **Assertions:** successful install, successful activity startup, and
  running app process.
- **Expected:** the corrected launch check recognizes the app within its
  retry window.
- **Observed:** all assertions passed. The exact-profile review classified
  the local remediation as passed.
- **Status:** passed for local functionality only.
- **Limitation:** the hosted v0.1.1 launch check still must pass before a
  release asset can be published. See EVID-021 for the first hosted failure.
- **Artifacts:** EVID-022 raw local test result and
  `app/build/outputs/apk/release/app-release.apk`.
