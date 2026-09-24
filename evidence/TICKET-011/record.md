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
**Readiness:** The user-authorized v0.1.4 signed release is published. Hosted
run `36039522821` passed build, signature, package/version, API 35 install,
and launch checks; the running process and resumed `MainActivity` were
observed. The GitHub asset was downloaded and its SHA-256 matches the release
asset digest. Commit `da1c876` is authored by `albrp97`, pushed to
`ticket/safe-session-controls`, and tagged `v0.1.4`. Physical-device and
distribution gates remain pending.

## Planning chain

`OBJ-001` -> `SCOPE-001` -> `CAP-002`, `CAP-003`, `CAP-004`, `CAP-006` ->
`PHASE-004` -> `FEAT-010` -> `TICKET-011`

## Acceptance coverage

1. Reproducible debug APK installs and launches on the supported emulator.
2. API 35 emulator display-off behavior is captured with environment details.
3. Physical-device, signing, provider, remote, and distribution gates are
   evidenced or recorded as exact blockers.
4. GitHub's approved release target contains a signed APK that verifies,
   installs, and launches on API 35; existing release tags remain unchanged.

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

## Baseline blockers recorded in EVID-003

These were observed at evidence initialization; later entries record
subsequent changes and resolutions.

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

## EVID-025 - Emulator launch remediation commit

- **Timestamp:** `2026-09-24`
- **Category:** commit
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Commit:** `56cca0f98d9d043ef35bc2296ccb907b29362ac0`
- **Subject:** `fix(release): retry emulator launch check`
- **Source branch:** `ticket/safe-session-controls`
- **Intended upstream:** `origin/ticket/safe-session-controls` at
  `dc2a4de14c4a0ec0cdf7bc79d3280c373788053b`.
- **Committed paths:** `.github/workflows/release-apk.yml`, `README.md`,
  `docs/planning/tickets/open/TICKET-011-device-release-readiness.md`, and
  `evidence/TICKET-011/record.md`.
- **Readiness references:** EVID-021 through EVID-024; hosted failure
  preserved, corrected local API 35 test passed, and exact-profile review
  found no blocker in the remediation.
- **Observed:** local commit created with the configured `albrp97` identity;
  branch is one commit ahead of its upstream.
- **Status:** passed as a local fix commit only; hosted rerun remains pending.

## EVID-026 - Emulator launch remediation pushed

- **Timestamp:** `2026-09-24`
- **Category:** push
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Commit:** `56cca0f98d9d043ef35bc2296ccb907b29362ac0`
- **Source branch:** `ticket/safe-session-controls`
- **Remote:** `origin/ticket/safe-session-controls`
- **Observed:** push succeeded without force; the remote branch now points to
  the remediation commit.
- **Status:** passed for branch publication. No hosted release rerun has
  started.

## EVID-027 - Same-tag hosted rerun blocked by provider permissions

- **Timestamp:** `2026-09-24`
- **Category:** providerAction
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Requested workflow:** `.github/workflows/release-apk.yml`
- **Requested ref:** `v0.1.1`
- **Observed:** GitHub rejected the `workflow_dispatch` API request with
  `403 Must have admin rights to Repository`; no workflow run was created.
- **Status:** blocked pending an authorized repository administrator action
  or approval to publish under a new version tag.
- **Risk:** the existing `v0.1.1` tag cannot be moved under the configured
  no-force policy. The ticket still requires a signed APK release and
  hosted API 35 validation.

## EVID-028 - Approved v0.1.2 release-target change

- **Timestamp:** `2026-09-24`
- **Category:** planning
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Parent links:** `OBJ-001` / `SCOPE-001` / `PHASE-004` / `FEAT-010`.
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Change ID:** `CHG-001`.
- **Decision:** change the current signed-release target from `v0.1.1` to
  `v0.1.2` with APK version name `0.1.2` and version code `3`.
- **Approval:** user selected `authorize-v012`.
- **Rationale:** the `v0.1.1` tag is immutable under `allow_force: false`;
  its hosted API 35 launch check failed, and GitHub rejected the same-tag
  workflow-dispatch request with HTTP 403.
- **Impact:** update the active ticket's current release acceptance, README
  release instructions, Android version metadata, and append-only evidence.
  No product behavior, parent artifact, ticket status, or scope changes.
- **Preserved:** the `v0.1.0` and `v0.1.1` tags and their historical evidence.
- **Status:** approved replanning; local and hosted v0.1.2 verification remain
  pending.

## EVID-029 - Initial pre-change baseline attempt blocked by shell environment

- **Timestamp:** `2026-09-24`
- **Category:** baseline
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Command:** `./gradlew test lintDebug --offline --no-daemon`.
- **Expected:** start Gradle and run the pre-change unit-test and lint
  baseline.
- **Observed:** Gradle could not start because this shell did not have
  `JAVA_HOME` configured and no `java` executable on `PATH`.
- **Fix:** use the repository environment's JDK 17 at
  `/home/ghiki/.local/jdks/jdk17` and Android SDK 35 at
  `/home/ghiki/.local/android-sdk`; no project file was changed for the
  environment correction.
- **Status:** blocked for this invocation; the configured-environment retry
  is recorded in EVID-030.

## EVID-030 - Pre-change Android unit-test and lint baseline

- **Timestamp:** `2026-09-24`
- **Category:** baseline
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Command:** `./gradlew test lintDebug --offline --no-daemon`, with
  `JAVA_HOME`, `ANDROID_HOME`, and `ANDROID_SDK_ROOT` set to the repository
  JDK 17 and Android SDK 35 paths.
- **Expected:** existing unit-test and lint checks finish successfully before
  the version metadata change.
- **Observed:** `BUILD SUCCESSFUL in 4s`; 55 tasks were actionable, one
  executed and 54 were up-to-date.
- **Status:** passed as the pre-change raw technical baseline.

## EVID-031 - Building the signed v0.1.2 APK

- **Timestamp:** `2026-09-24`
- **Category:** qualityGate
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Command:** `./gradlew test lintDebug assembleRelease --offline --no-daemon`
  with repository JDK 17, Android SDK 35, and protected local signing inputs.
- **Expected:** unit tests, Android lint, and signed release assembly complete
  successfully for the approved v0.1.2 metadata.
- **Observed:** `BUILD SUCCESSFUL in 16s`; 85 tasks were actionable, 26
  executed and 59 were up-to-date. Lint reported four warnings in the
  unchanged `strings.xml` baseline.
- **Failure and fix:** an initial shell invocation exited before Gradle
  startup while loading protected signing inputs. The retry handled the
  secret-file EOF safely, checked only that values were present, and printed
  no secret values.
- **Status:** passedWithConcerns due only to the four existing lint warnings.
- **Artifacts:** `app/build/outputs/apk/release/app-release.apk`,
  `app/build/reports/lint-results-debug.xml`, and
  `app/build/reports/lint-results-debug.html`.

## EVID-032 - API 35 Android functionality suite

- **Timestamp:** `2026-09-24`
- **Category:** automatedFunctionality
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Test ID:** `ticket-011-api35-android-functionality`.
- **Command:** `./gradlew connectedDebugAndroidTest --offline --no-daemon`.
- **System boundary:** Android app and foreground service on a clean API 35
  Google APIs x86_64 emulator.
- **Assertions:** all instrumentation flows pass, including setup launch,
  screen-off phase continuity, configured timing, stop/completion,
  interruption, and haptic capability behavior.
- **Expected:** all instrumentation tests pass with no failures or skips.
- **Observed:** 42 tests completed on API 35; zero failures and zero skips.
- **Status:** passed.
- **Artifacts:** `app/build/reports/androidTests/connected/debug/index.html`,
  `app/build/outputs/androidTest-results/connected/debug/test-result.textproto`.

## EVID-033 - Signed v0.1.2 install and launch

- **Timestamp:** `2026-09-24`
- **Category:** automatedFunctionality
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Test ID:** `release-apk-api35-install-launch-v0.1.2`.
- **System boundary:** signed release APK installed on the local API 35
  `sleep-inducer-api35` emulator.
- **Steps:** verify package/version metadata and signer; install
  `app/build/outputs/apk/release/app-release.apk`; start
  `com.sleepinducer.app/.MainActivity` with `adb shell am start -W`; assert
  `Status: ok` and a running app process.
- **Expected:** package `com.sleepinducer.app`, version name `0.1.2`, version
  code `3`, pinned signer, successful install and activity launch.
- **Observed:** all assertions passed; API level was `35`, `am start -W`
  returned `Status: ok`, and the app process was present.
- **Signer SHA-256:** `f983cd5963b821bff295cb349cc43ef074f3ee76aa467900722f06eabdef5566`.
- **APK SHA-256:** `06970177d7d62a9839d427115bf6ca1213ece3ffa7da26af1e71a1226d60d627`.
- **Downloads artifact:** `/home/ghiki/Downloads/sleep-inducer-release.apk`;
  its SHA-256 matched the release build byte-for-byte.
- **Failure and fix:** after instrumentation tests, a redundant uninstall
  returned `DELETE_FAILED_INTERNAL_ERROR`; package inspection showed the test
  runner had already removed the debug package. Installing the signed APK
  directly then passed.
- **Status:** passed for local functionality only; hosted release validation
  remains pending.
- **Artifacts:** `app/build/outputs/apk/release/app-release.apk`,
  `/home/ghiki/Downloads/sleep-inducer-release.apk`.

## EVID-034 - v0.1.2 static analysis and workflow contract

- **Timestamp:** `2026-09-24`
- **Category:** staticAnalysis
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Checks:** `git diff --check`; Android Gradle build/test/lint; YAML parse
  and `bash -n` for all six release-workflow scripts; APK package/version
  contract; and pinned-certificate verification.
- **Expected:** changed files are clean, workflow syntax is valid, and the
  signed APK metadata matches `v0.1.2`.
- **Observed:** diff hygiene passed, YAML parsed, all six scripts passed
  `bash -n`, APK metadata matched `0.1.2`/`3`, and signature verification
  passed.
- **Existing findings:** four Android lint warnings remain in the approved
  baseline and are not introduced by this diff.
- **Optional coverage gap:** `npx --no-install aidd churn --json` returned
  unavailable because `aidd@3.1.0` is not installed. No installation was
  attempted; churn is optional.
- **Parity:** no Android build/lint/test PR job is configured. The separate
  `workflow-evals.yml` job references missing
  `tools/eval_workflows.py` and `ai-evals/workflow-contracts.json`, and does
  not validate Android changes. No PR is required for this delivery.
- **Failure and fix:** the first local tag-contract helper exited before the
  assertion because its `tag` variable was unset. The corrected helper set
  `v0.1.2` explicitly and passed the version/tag comparison.
- **Status:** passedWithConcerns; hosted tag workflow and release asset are
  separate pending remote gates.
- **Artifacts:** `evidence/static-analysis/TICKET-011-v0.1.2-release.md`,
  `evidence/static-analysis/TICKET-011-v0.1.2-release.json`,
  `evidence/static-analysis/TICKET-011-v0.1.2-release.sarif`,
  `app/build/reports/lint-results-debug.xml`.

## EVID-035 - Interim exact-profile review

- **Timestamp:** `2026-09-24`
- **Category:** review
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Review scope:** CHG-001, current diff, local build and functionality
  results, static analysis, planning links, and remote release gates.
- **Observed:** the initial review accepted the local technical results with
  concerns but found the final v0.1.2 evidence and static-analysis artifacts
  had not yet been appended. EVID-031 through EVID-034 and the v0.1.2
  analysis artifacts now address that evidence gap. The hosted v0.1.2 workflow
  and release asset remain pending and are not claimed as passed.
- **Status:** blocked at the time of this interim review; a final local
  commit-readiness review is required after evidence reconciliation.

## EVID-036 - Release workflow version-code assertion

- **Timestamp:** `2026-09-24`
- **Category:** regression
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Test ID:** `release-workflow-pins-v0.1.2-version-code`.
- **Red check:** parse `.github/workflows/release-apk.yml` and assert that the
  signature/package verification step reads `apkanalyzer manifest
  version-code` and asserts version code `3`.
- **Expected before fix:** the contract check fails because the release
  workflow validates only package ID and version name.
- **Observed before fix:** assertion failed with
  `release workflow does not verify APK versionCode 3`.
- **Fix:** the release workflow now extracts APK version code and asserts it
  is `3`; TICKET-011 change-control scope records the affected workflow.
- **Post-fix verification:** the focused contract check passed against the
  workflow and signed APK; YAML parsing and all six embedded Bash syntax
  checks passed.
- **Status:** passed.

## EVID-037 - Final local release quality checks

- **Timestamp:** `2026-09-24`
- **Category:** staticAnalysis
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Checks:** final `git diff --check`; release-workflow YAML parse and
  `bash -n` for all six shell blocks; assertion that the workflow enforces
  version code `3`; APK package, version name/code and signature; API 35
  signed-APK install and launch.
- **Observed:** all focused checks passed. The signed APK contract is
  `com.sleepinducer.app`, version name `0.1.2`, version code `3`, with the
  pinned certificate. API 35 launch returned `Status: ok` and the process
  remained present.
- **Existing findings:** four lint warnings remain in the approved baseline.
- **Optional coverage gap:** churn analysis remains unavailable; no package
  was installed.
- **Parity:** no Android build/lint/test PR job is configured. The separate
  workflow-evaluation workflow does not validate Android changes, and its
  referenced files are absent. No PR is required for this release.
- **Status:** passedWithConcerns; hosted workflow and release asset remain
  separate pending remote gates.
- **Artifacts:** `evidence/static-analysis/TICKET-011-v0.1.2-release-final.md`,
  `evidence/static-analysis/TICKET-011-v0.1.2-release-final.json`,
  `evidence/static-analysis/TICKET-011-v0.1.2-release-final.sarif`,
  `app/build/reports/lint-results-debug.xml`, and
  `app/build/reports/androidTests/connected/debug/index.html`.

## EVID-038 - Automatic validation of v0.1.2 release functionality

- **Timestamp:** `2026-09-24`
- **Category:** automaticValidation
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Test ID:** `release-apk-api35-install-launch-v0.1.2`.
- **Automated:** yes.
- **System boundary:** signed release APK on an API 35 Google APIs x86_64
  emulator.
- **Assertions:** package, version name `0.1.2`, version code `3`, pinned
  signer, successful installation, `Status: ok` from activity startup, and a
  running app process.
- **Expected:** the supported emulator accepts and launches the signed
  v0.1.2 package with metadata matching the approved release target.
- **Observed:** all local assertions passed; the exact-profile validator
  returned terminal `PASS`.
- **Status:** passed for local release functionality only.
- **Artifacts:** `app/build/outputs/apk/release/app-release.apk`,
  `/home/ghiki/Downloads/sleep-inducer-release.apk`,
  `app/build/reports/androidTests/connected/debug/index.html`.
- **Boundary:** the hosted v0.1.2 workflow and GitHub release asset are not
  covered by this result and remain pending remote verification.

## EVID-039 - Current v0.1.2 readiness snapshot

- **Timestamp:** `2026-09-24`
- **Category:** gate
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Local terminal evidence:** EVID-031 through EVID-038; build, API 35
  functionality, APK metadata/signature, workflow version-code enforcement,
  and automatic validation passed, with four accepted existing lint warnings
  and optional churn unavailable.
- **Approval:** CHG-001 authorizes the new v0.1.2 target; v0.1.0 and v0.1.1
  tags remain unchanged.
- **Current blockers:** the v0.1.2 change is staged but not yet committed or
  tagged; no hosted v0.1.2 workflow run or GitHub release asset exists yet.
  Physical-device haptic comfort and Google Play `specialUse` approval also
  remain external gates.
- **Resolved since baseline:** remote origin and upstream are configured,
  signing inputs are available, and local automatic validation is terminal.
- **Status:** local commit gate may proceed after final staged review; remote
  delivery and ticket completion remain blocked until the authorized tag
  workflow and release asset are verified.

## EVID-040 - Final staged v0.1.2 review

- **Timestamp:** `2026-09-24`
- **Category:** review
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Reviewed scope:** 11 staged paths covering release metadata, the
  version-code workflow assertion, README/ticket updates, and initial/final
  v0.1.2 static-analysis and delivery evidence.
- **Observed:** the staged diff passed `git diff --cached --check`; planning
  ancestry and CHG-001 approval are consistent; EVID-038 provides terminal
  local automatic validation; no introduced blocking finding remains.
- **Accepted concerns:** four existing Android lint warnings and unavailable
  optional churn analysis.
- **Remote boundary:** no hosted v0.1.2 workflow run or GitHub release asset
  exists yet. Physical-device and Google Play `specialUse` gates remain
  external. These block delivery completion and ticket closure, not the
  local commit.
- **Status:** passedWithConcerns for the local commit gate only.

## EVID-041 - Local v0.1.2 release commit

- **Timestamp:** `2026-09-24`
- **Category:** commit
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Commit:** `3e5fc8e0b34ee7612f6813bff762c4adda9d2ce3`.
- **Subject:** `fix(release): validate v0.1.2 APK`.
- **Source branch:** `ticket/safe-session-controls`.
- **Intended upstream:** `origin/ticket/safe-session-controls` at
  `56cca0f98d9d043ef35bc2296ccb907b29362ac0`.
- **Committed paths:** `.github/workflows/release-apk.yml`, `README.md`,
  `app/build.gradle.kts`,
  `docs/planning/tickets/open/TICKET-011-device-release-readiness.md`,
  `evidence/TICKET-011/record.md`, and six v0.1.2 static-analysis report
  artifacts.
- **Readiness references:** EVID-031 through EVID-040; exact-profile local
  automatic validation passed, static analysis passedWithConcerns, and no
  introduced blocking review finding remains.
- **Accepted warnings:** four existing lint warnings; optional churn analysis
  unavailable.
- **Observed:** commit created with author `albrp97`; local branch is one
  commit ahead of upstream. Hosted release verification remains pending.
- **Status:** passed as a local commit only; no delivery-complete claim.

## EVID-042 - v0.1.2 release commit pushed

- **Timestamp:** `2026-09-24`
- **Category:** push
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Source branch:** `ticket/safe-session-controls`.
- **Remote:** `origin/ticket/safe-session-controls`.
- **Commit:** `3e5fc8e0b34ee7612f6813bff762c4adda9d2ce3`.
- **Command:** `git push origin ticket/safe-session-controls`.
- **Observed:** push succeeded without force; a corrected `git ls-remote`
  check confirmed the remote branch points to the commit. The first combined
  push/check shell invocation had a quoting typo after the push; the separate
  remote verification passed.
- **Status:** passed for branch publication. The v0.1.2 release tag and
  hosted workflow remain pending.

## EVID-043 - v0.1.2 release tag published

- **Timestamp:** `2026-09-24`
- **Category:** push
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Tag:** annotated `v0.1.2`.
- **Target commit:** `3e5fc8e0b34ee7612f6813bff762c4adda9d2ce3`.
- **Command:** `git push origin refs/tags/v0.1.2`.
- **Observed:** tag push succeeded without force; remote peeled tag resolves
  to the intended commit. Tag-triggered workflow run `35996308723` started
  and is in progress.
- **Status:** passed for tag publication; hosted build, install/launch, and
  release asset remain pending.

## EVID-044 - Hosted v0.1.2 launch assertion failure

- **Timestamp:** `2026-09-24`
- **Category:** deployment
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Workflow run:** `35996308723`
  (`https://github.com/albrp97/SleepInducer/actions/runs/35996308723`).
- **Ref/commit:** immutable tag `v0.1.2` at
  `3e5fc8e0b34ee7612f6813bff762c4adda9d2ce3`.
- **Expected:** hosted build, signature/version checks, API 35 install and
  launch, then signed release publication.
- **Observed:** build/test/lint and signature/package/version checks passed.
  The hosted emulator booted on API 35 and APK install returned `Success`.
  `adb shell am start -W` returned exit code `0` but empty output. The strict
  `grep -Fx 'Status: ok'` then exited `1`, terminating the script before its
  process-retry loop or diagnostic logcat. The workflow job failed and the
  release publication step was skipped.
- **What remains unknown:** because the script exited before checking
  `pidof`, resumed activity, or logcat, this run does not establish whether
  the app process or main activity actually started.
- **Status:** failed; no GitHub `v0.1.2` release or APK asset was created.
- **Fix direction:** treat absent `Status: ok` text as inconclusive, then
  require an independently observed running process and resumed main
  activity; emit logcat and activity diagnostics when those assertions fail.
- **Tag policy:** preserve the failed `v0.1.2` tag. A future version-tag
  publication requires explicit authorization and must use the corrected
  workflow.

## EVID-045 - Corrected release launch contract checks

- **Timestamp:** `2026-09-24`
- **Category:** contract
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Requirement:** TICKET-011 acceptance criterion 4; a release APK must
  install and launch on API 35 before publication.
- **Command:** parsed `.github/workflows/release-apk.yml` with PyYAML 6.0.3,
  extracted the `Install and launch release APK` shell script, and executed
  it through an inline Python harness with mocked `adb` and `sleep`.
- **Assertions:** empty `am start -W` output is inconclusive when both the
  package process and resumed main activity are present; `Status: ok` also
  passes with those observations; a missing process/activity and a nonzero
  launch command fail and emit diagnostics. All six embedded workflow Bash
  blocks pass `bash -n`.
- **Expected:** tolerate missing status text only when independent activity
  and process checks prove the app is active; reject failed launches with
  diagnostics.
- **Observed:** both healthy cases passed. Both negative cases failed with
  the expected diagnostic output. YAML parsing and all six shell syntax
  checks passed; `git diff --check` passed.
- **Baseline:** EVID-044 records the hosted false negative caused by making
  `Status: ok` mandatory before the process/activity checks.
- **Artifacts:** `evidence/static-analysis/TICKET-011-v0.1.2-launch-workflow-fix-final.md`,
  `.json`, and `.sarif`.
- **Status:** passed for the corrected workflow contract; optional analyzer
  availability is recorded separately in EVID-047.

## EVID-046 - Local API 35 release launch verification

- **Timestamp:** `2026-09-24`
- **Category:** deployment
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Requirement:** TICKET-011 acceptance criterion 4; verify the release
  install-and-launch gate against the supported Android boundary.
- **Command:** execute the exact `Install and launch release APK` script
  extracted from `.github/workflows/release-apk.yml` against the locally
  signed v0.1.2 release APK on an API 35 emulator (emulator 37.1.11.0).
- **Expected:** APK installs, the launch command succeeds, the app process
  exists, and `.MainActivity` is the resumed activity.
- **Observed:** install returned `Success`; `am start -W` reported
  `Status: ok`; the process was observed and `topResumedActivity` identified
  `com.sleepinducer.app/.MainActivity`.
- **Boundary:** local API 35 emulator only. This does not claim the failed
  immutable v0.1.2 tag passed hosted CI or has a GitHub release asset.
- **Cleanup:** stopped the emulator and ADB daemon and removed only the
  temporary `sleep-inducer-api35` AVD created for this check.
- **Artifacts:** local release APK at
  `app/build/outputs/apk/release/app-release.apk`; workflow analysis reports
  are recorded with EVID-047.
- **Status:** passed for the local API 35 launch flow; hosted publication
  remains blocked on a newly authorized version tag.

## EVID-047 - Initial exact-profile review evidence blocker

- **Timestamp:** `2026-09-24`
- **Category:** review
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Scope:** workflow/docs correction only; no app source or version change.
- **Expected:** final workflow correction and supporting analysis evidence
  should be internally auditable before commit readiness is classified.
- **Observed:** the reviewer found no launch-logic defect and confirmed the
  configured validator profile was available. It blocked commit readiness
  because the final static-analysis Markdown/JSON/SARIF artifacts and terminal
  automatic-validation record had not yet been written.
- **Fix:** created the three configured static-analysis reports and added
  EVID-048 and EVID-049; a follow-up exact-profile validation will verify
  those artifacts and classify the terminal automatic-validation gate.
- **Status:** blocked at this review snapshot; superseded by the follow-up
  validation after the evidence artifacts are verified.

## EVID-048 - Final launch-workflow static analysis

- **Timestamp:** `2026-09-24`
- **Category:** staticAnalysis
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Run:** `TICKET-011-v0.1.2-launch-workflow-fix-2026-09-24`, diff mode.
- **Command and results:** `git diff --check` passed; PyYAML 6.0.3 parsed the
  workflow; GNU Bash 5.3.15 passed `bash -n` on all six embedded blocks; the
  mocked launch contract and real API 35 script execution passed as recorded
  in EVID-045 and EVID-046.
- **Findings:** none introduced.
- **Parity:** `notApplicable`; no PR static-analysis job is configured.
- **Availability gaps:** optional actionlint, ShellCheck, yamllint, and aidd
  churn analyzers are unavailable. The separate PR workflow-evaluation job
  references missing `tools/eval_workflows.py` and
  `ai-evals/workflow-contracts.json`; it was not represented as passing.
- **Artifacts:** `evidence/static-analysis/TICKET-011-v0.1.2-launch-workflow-fix-final.md`,
  `.json`, and `.sarif`.
- **Status:** passedWithConcerns; coverage limitations are explicit and no
  introduced finding remains.

## EVID-049 - Automated API 35 release launch functionality

- **Timestamp:** `2026-09-24`
- **Category:** automatedFunctionality
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Test ID:** `release-apk-launch-api35`.
- **Command:** parse `.github/workflows/release-apk.yml`, extract the exact
  `Install and launch release APK` script, then execute it against
  `app/build/outputs/apk/release/app-release.apk` on a local API 35 emulator.
- **System boundary:** signed APK install, Android Activity Manager launch,
  package process lookup, and resumed main-activity inspection on API 35.
- **Assertions:** install succeeds; a nonzero launch command fails;
  `Status: ok` is optional; success requires both the package process and
  resumed `com.sleepinducer.app/.MainActivity`.
- **Expected:** the corrected release launch gate passes when the app is
  active and emits diagnostics when launch or activity assertions fail.
- **Observed:** APK installation and launch passed. The package process was
  present and `topResumedActivity` identified the main activity. Mocked
  negative cases also failed with diagnostics as required.
- **External effects:** no GitHub release was created; the test used only a
  temporary local emulator, which was stopped and removed afterward.
- **Artifacts:** EVID-045, EVID-046, and the EVID-048 static-analysis reports.
- **Status:** passed for the local API 35 functionality boundary; hosted
  release publication remains a separate blocker.

## EVID-050 - Automatic validation of corrected release launch flow

- **Timestamp:** `2026-09-24`
- **Category:** automaticValidation
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Test ID:** `release-apk-launch-api35`.
- **Requirement:** TICKET-011 acceptance criterion 4; the release workflow
  must distinguish missing launch-command output from a failed app launch
  and must prove the APK is active before publication.
- **Command:** inspect the deterministic YAML, Bash syntax, mocked launch
  contract, and local API 35 emulator results in EVID-045, EVID-046,
  EVID-048, and EVID-049 with the exact configured Rubber Duck profile.
- **System boundary:** API 35 APK installation, Activity Manager launch,
  package-process detection, and resumed main-activity verification.
- **Assertions:** empty output with an active process and resumed activity
  passes; missing status alone does not pass; missing process/activity and
  nonzero launch fail with diagnostics.
- **Expected:** successful local functionality evidence for the corrected
  launch gate, with hosted release status kept separate.
- **Observed:** the exact extracted script installed and launched the signed
  v0.1.2 APK locally and observed the main activity. Mocked negative paths
  failed as required. The reviewer classified local functionality as PASS
  and the overall review as `passedWithConcerns`.
- **Concerns:** optional workflow analyzers and churn are unavailable; the
  PR evaluator/spec are absent; four existing Android lint warnings remain;
  hosted publication has not passed.
- **Artifacts:** `evidence/static-analysis/TICKET-011-v0.1.2-launch-workflow-fix-final.md`,
  `.json`, and `.sarif`.
- **Status:** passedWithConcerns for the local functionality boundary only.
  No hosted-release or user-validation claim is made.

## EVID-051 - Final local workflow-fix review

- **Timestamp:** `2026-09-24`
- **Category:** review
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Scope:** corrected API 35 release launch workflow, current README/ticket
  status, and final evidence artifacts.
- **Observed:** the exact-profile reviewer verified that EVID-045 through
  EVID-049 and all referenced analysis reports are present and consistent.
  It found no blocking defect in the workflow/docs correction and confirmed
  the earlier EVID-047 evidence gap is closed.
- **Accepted concerns:** unavailable optional analyzers/churn, missing
  workflow-evaluation source/spec, and four existing Android lint warnings.
- **Readiness:** ready for a local commit of the reviewed workflow/docs fix
  only. This is not ticket completion or release readiness.
- **Release blocker:** immutable tag `v0.1.2` failed before publication and
  has no GitHub release asset. A new version tag requires explicit user
  authorization and must pass hosted checks.
- **Status:** passedWithConcerns for the local commit gate; hosted delivery
  remains blocked.

## EVID-052 - Local workflow-fix commit

- **Timestamp:** `2026-09-24`
- **Category:** commit
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Commit:** `4a0634d17ef671ca2dfd1326380a326e8812ef36`.
- **Subject:** `fix(release): verify API 35 activity`.
- **Author:** `albrp97`.
- **Source branch:** `ticket/safe-session-controls`.
- **Intended upstream:** `origin/ticket/safe-session-controls` at
  `3e5fc8e0b34ee7612f6813bff762c4adda9d2ce3`.
- **Committed paths:** `.github/workflows/release-apk.yml`, `README.md`,
  `docs/planning/tickets/open/TICKET-011-device-release-readiness.md`,
  `evidence/TICKET-011/record.md`, and the final launch-workflow static
  analysis `.md`, `.json`, and `.sarif` artifacts.
- **Readiness references:** EVID-045 through EVID-051; exact-profile local
  functionality and review passedWithConcerns. Four existing lint warnings
  and optional analyzer gaps remain accepted.
- **Observed:** commit created with the requested author; branch is one
  commit ahead of its configured upstream. The worktree contains only the
  post-commit delivery-evidence append. Push is the next configured action.
- **Status:** passed as a local commit only; hosted release publication is
  not claimed.

## EVID-053 - Workflow-fix commit pushed to ticket branch

- **Timestamp:** `2026-09-24`
- **Category:** push
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Remote:** `origin` (`github-personal:albrp97/SleepInducer.git`).
- **Source branch:** `ticket/safe-session-controls`.
- **Commit:** `4a0634d17ef671ca2dfd1326380a326e8812ef36`.
- **Command:** `git push origin ticket/safe-session-controls`.
- **Observed:** push succeeded without force. `git ls-remote` confirmed the
  remote branch points to the exact local commit. The annotated `v0.1.2` tag
  still peels to `3e5fc8e0b34ee7612f6813bff762c4adda9d2ce3`; it was not moved.
- **Worktree:** only this post-commit evidence append remains uncommitted;
  push policy allows a dirty worktree and no other path was altered.
- **Status:** passed for ticket-branch publication. The v0.1.2 GitHub release
  remains absent, and publishing a new version tag requires user
  authorization.

## EVID-054 - CHG-002 approval for v0.1.3

- **Timestamp:** `2026-09-24`
- **Category:** planning
- **Owner:** user
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Change ID:** `CHG-002`.
- **Decision:** approved target `v0.1.3`, version name `0.1.3`, version code
  `4`; preserve immutable tags `v0.1.0`, `v0.1.1`, and `v0.1.2`.
- **Approval source:** the user selected `authorize-v0.1.3` in response to
  the explicit change-control approval request after the hosted v0.1.2 run
  failed and same-tag rerun was unavailable.
- **Affected surfaces:** app release metadata, exact workflow version-code
  assertion, release documentation, TICKET-011, and this evidence record.
- **Unaffected behavior:** breathing protocol, safety messaging, data
  collection, and application behavior.
- **Status:** approved; v0.1.3 implementation and delivery evidence pending.

## EVID-055 - Pre-change version contract baseline

- **Timestamp:** `2026-09-24`
- **Category:** baseline
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Requirement:** CHG-002; the release APK must report version name `0.1.3`
  and version code `4`.
- **Command:** `apkanalyzer manifest version-name
  app/build/outputs/apk/release/app-release.apk` and
  `apkanalyzer manifest version-code
  app/build/outputs/apk/release/app-release.apk`.
- **Environment:** JDK 17 and Android SDK 35.
- **Expected baseline:** the existing pre-change v0.1.2 APK does not satisfy
  the newly approved v0.1.3/4 contract.
- **Observed:** version name `0.1.2`, version code `3`; RED confirmed for the
  new target.
- **Status:** passed as a protected pre-change baseline; no release metadata
  had yet been modified.

## EVID-056 - Signed v0.1.3 build and unit tests

- **Timestamp:** `2026-09-24`
- **Category:** qualityGate
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Command:** `./gradlew test lintDebug assembleRelease --offline --no-daemon`.
  A Python subprocess loaded the required protected signing inputs into the
  Gradle process environment without printing or persisting their values.
- **Environment:** JDK 17, Gradle Wrapper 8.9, Android SDK 35.
- **Expected:** unit tests pass and a signed release APK is assembled for the
  approved version.
- **Observed:** `BUILD SUCCESSFUL`; 85 Gradle tasks completed or were
  up-to-date. Unit-test tasks were up-to-date because application source and
  tests are unchanged from the previously tested code.
- **Artifact:** `app/build/outputs/apk/release/app-release.apk`.
- **Status:** passed for build, unit-test, and release-assembly tasks. Lint
  warnings are recorded separately in EVID-057.

## EVID-057 - Android lint for v0.1.3

- **Timestamp:** `2026-09-24`
- **Category:** qualityGate
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Command:** `./gradlew test lintDebug assembleRelease --offline --no-daemon`.
- **Expected:** Android lint completes without new release-blocking findings.
- **Observed:** lint completed successfully with four existing warnings in
  unchanged `app/src/main/res/values/strings.xml` (three `UnusedResources`,
  one `TypographyEllipsis`), matching the prior approved baseline.
- **Artifact:** `app/build/reports/lint-results-debug.xml`.
- **Status:** passedWithConcerns; warnings remain visible and were not
  suppressed.

## EVID-058 - v0.1.3 signature and APK metadata

- **Timestamp:** `2026-09-24`
- **Category:** contract
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Command:** `apksigner verify --verbose`,
  `apksigner verify --print-certs`, and `apkanalyzer` package/version
  queries against `app/build/outputs/apk/release/app-release.apk`; the exact
  `Verify release signature and package` workflow block also ran with
  `RELEASE_TAG=v0.1.3`.
- **Assertions:** pinned signer certificate matches; package is
  `com.sleepinducer.app`; version name is `0.1.3`; version code is `4`;
  workflow version name matches the tag.
- **Observed:** all assertions passed.
- **APK SHA-256:** `af2bd2a84ed500798ab76d3056051fd1326ffaa59751ef449393ef61c916b84c`.
- **Status:** passed for local APK signature and metadata; hosted release
  checks remain pending.

## EVID-059 - API 35 instrumentation suite for v0.1.3

- **Timestamp:** `2026-09-24`
- **Category:** automatedFunctionality
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Test ID:** `connected-debug-android-test-api35`.
- **Command:** `./gradlew connectedDebugAndroidTest --offline --no-daemon`.
- **System boundary:** debug application installed and exercised through
  AndroidJUnitRunner on the API 35 x86_64 emulator.
- **Assertions:** the repository instrumentation suite completes without
  failures or skips.
- **Observed:** 42 tests completed, zero failed, zero skipped.
- **Artifact:** `app/build/reports/androidTests/connected/debug/index.html`.
- **Status:** passed.

## EVID-060 - Local v0.1.3 release workflow functionality

- **Timestamp:** `2026-09-24`
- **Category:** automatedFunctionality
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Test ID:** `release-apk-launch-api35`.
- **Command:** parse `.github/workflows/release-apk.yml`, run its exact
  `Verify release signature and package` block for `RELEASE_TAG=v0.1.3`,
  then execute its exact `Install and launch release APK` script against
  `app/build/outputs/apk/release/app-release.apk`.
- **System boundary:** signed release APK installation, Activity Manager,
  application process lookup, and resumed MainActivity on API 35.
- **Assertions:** pinned signature/package/version checks pass; APK installs;
  launch command succeeds; package process exists; resumed activity is
  `com.sleepinducer.app/.MainActivity`.
- **Observed:** both workflow blocks passed. Install returned `Success`;
  `am start -W` returned `Status: ok`; process PID `4471` and
  `topResumedActivity` for the main activity were observed. EVID-045 retains
  the mocked empty-output and negative-path regression results.
- **Boundary:** local emulator only; no GitHub tag or release was created.
- **Status:** passed for the local release functionality boundary.

## EVID-061 - v0.1.3 APK copied to Downloads

- **Timestamp:** `2026-09-24`
- **Category:** deployment
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Command:** `install -m 644
  app/build/outputs/apk/release/app-release.apk
  /home/ghiki/Downloads/sleep-inducer-release.apk`; verify with `cmp -s`,
  `sha256sum`, and `stat`.
- **Expected:** Downloads contains the same signed v0.1.3 release APK as the
  verified build output.
- **Observed:** byte comparison passed; file size is `6561012` bytes and
  SHA-256 matches EVID-058.
- **Artifact:** `/home/ghiki/Downloads/sleep-inducer-release.apk`.
- **Status:** passed for the local Downloads artifact; GitHub publication is
  still pending.

## EVID-062 - Final v0.1.3 static analysis

- **Timestamp:** `2026-09-24`
- **Category:** staticAnalysis
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Run:** `TICKET-011-v0.1.3-release-final-2026-09-24`, diff mode.
- **Scope:** approved v0.1.3/version code 4 metadata, release workflow
  assertion, README, ticket, and evidence.
- **Command/results:** `git diff --check` passed; PyYAML 6.0.3 parsed the
  workflow; GNU Bash 5.3.15 passed `bash -n` on all six embedded blocks;
  Gradle build/unit/lint/release assembly passed; 42 API 35 instrumentation
  tests passed; signature/version and exact workflow install/launch checks
  passed.
- **Findings:** no introduced finding. Four existing Android lint warnings
  remain in unchanged `strings.xml` and are included in the JSON/SARIF.
- **Parity:** `notApplicable`; no PR static-analysis job exists. The separate
  PR workflow-evaluation job is unavailable because its evaluator/spec files
  are absent.
- **Availability gaps:** optional actionlint, ShellCheck, yamllint, and aidd
  churn tools are unavailable; no installation was attempted.
- **Artifacts:** `evidence/static-analysis/TICKET-011-v0.1.3-release-final.md`,
  `.json`, and `.sarif`.
- **Status:** passedWithConcerns; hosted tag/release checks remain pending.

## EVID-063 - Automatic validation of v0.1.3 local functionality

- **Timestamp:** `2026-09-24`
- **Category:** automaticValidation
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Test ID:** `release-apk-launch-api35`.
- **Requirement:** CHG-002; the authorized v0.1.3 APK must satisfy the
  signed-install-launch boundary before its tag is published.
- **Command:** inspect the deterministic build, signature, metadata,
  instrumentation, exact-workflow, and Downloads results in EVID-056 through
  EVID-061 with the configured Rubber Duck profile.
- **System boundary:** signed APK install, Activity Manager launch, app
  process detection, and resumed MainActivity on API 35.
- **Assertions:** package/version/signature match the approved contract;
  launch succeeds; process and resumed activity are present; failure paths
  are checked by the mock regression in EVID-045.
- **Observed:** the exact local release scripts passed. The APK installed,
  launched, and resumed `com.sleepinducer.app/.MainActivity`; the configured
  reviewer classified local functionality as PASS.
- **Concerns:** four existing lint warnings; optional analyzers are
  unavailable; the PR workflow evaluator/spec are missing; hosted release
  validation is not yet available.
- **External effects:** no release tag or GitHub asset was created.
- **Artifacts:** EVID-045, EVID-056 through EVID-062, and the v0.1.3
  static-analysis Markdown/JSON/SARIF reports.
- **Status:** passedWithConcerns for the local functionality boundary only;
  no hosted-release claim is made.

## EVID-064 - Initial v0.1.3 review evidence blocker

- **Timestamp:** `2026-09-24`
- **Category:** review
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Observed:** the exact-profile reviewer confirmed the local functionality
  results and found no introduced code defect, but commit readiness was
  blocked because the terminal `automaticValidation` entry had not yet been
  recorded.
- **Fix:** EVID-063 now records the terminal local functionality result with
  the exact configured profile. A narrow follow-up will verify the evidence
  closure and commit-only gate.
- **Status:** blocked at this review snapshot; superseded by the final
  evidence-closure review.

## EVID-065 - Final local v0.1.3 candidate review

- **Timestamp:** `2026-09-24`
- **Category:** review
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Scope:** approved CHG-002 version metadata, exact release-workflow
  assertion, README/ticket updates, and final local evidence.
- **Observed:** the exact-profile reviewer verified EVID-054 through
  EVID-064, the terminal automaticValidation entry, and the final static
  reports. It confirmed the local candidate is ready for a local commit.
- **Accepted concerns:** four existing lint warnings, unavailable optional
  analyzers/churn, unavailable PR workflow-evaluation inputs, and no hosted
  v0.1.3 run or asset yet.
- **Readiness:** passedWithConcerns for the local commit gate only.
- **Release boundary:** no v0.1.3 tag or GitHub release is claimed. Hosted
  validation and asset publication remain pending.

## EVID-066 - v0.1.3 release-target commit

- **Timestamp:** `2026-09-24`
- **Category:** commit
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Commit:** `63c9f9e789623bd9ba4eee16ac222c8bc422bf6a`.
- **Subject:** `fix(release): target v0.1.3 APK`.
- **Author:** `albrp97`.
- **Source branch:** `ticket/safe-session-controls`.
- **Intended upstream:** `origin/ticket/safe-session-controls` at
  `4a0634d17ef671ca2dfd1326380a326e8812ef36`.
- **Committed paths:** `.github/workflows/release-apk.yml`,
  `app/build.gradle.kts`, `README.md`,
  `docs/planning/tickets/open/TICKET-011-device-release-readiness.md`,
  `evidence/TICKET-011/record.md`, and the v0.1.3 static-analysis
  `.md`, `.json`, and `.sarif` reports.
- **Readiness references:** EVID-054 through EVID-065; CHG-002 is approved,
  local build/signature/version/API 35 functionality passedWithConcerns, and
  exact-profile automaticValidation and review are terminal.
- **Observed:** commit created as `albrp97`; branch is one commit ahead of
  upstream. The APK is not committed. Push is the next configured action.
- **Status:** passed as a local commit only; no tag or GitHub release is
  claimed.

## EVID-067 - v0.1.3 release-target commit pushed

- **Timestamp:** `2026-09-24`
- **Category:** push
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Remote:** `origin` (`github-personal:albrp97/SleepInducer.git`).
- **Source branch:** `ticket/safe-session-controls`.
- **Commit:** `63c9f9e789623bd9ba4eee16ac222c8bc422bf6a`.
- **Command:** `git push origin ticket/safe-session-controls`.
- **Observed:** push succeeded without force. GitHub's branch commit listing
  and commit lookup both confirmed that the remote branch head is
  `63c9f9e789623bd9ba4eee16ac222c8bc422bf6a` and the author is `albrp97`.
  The local `git ls-remote` read-back returned an SSH public-key permission
  error; the provider API supplied the independent remote verification.
- **Tag verification:** GitHub confirms `v0.1.2` still targets
  `3e5fc8e0b34ee7612f6813bff762c4adda9d2ce3`; `v0.1.3` did not exist before
  the approved tag operation.
- **Status:** passed for branch publication and provider-API verification;
  release-tag publication is next.

## EVID-068 - Initial v0.1.3 tag push authentication blocker

- **Timestamp:** `2026-09-24`
- **Category:** push
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Tag:** local annotated `v0.1.3` targeting
  `63c9f9e789623bd9ba4eee16ac222c8bc422bf6a`.
- **Command:** `git push origin refs/tags/v0.1.3`.
- **Observed:** push failed with SSH `Permission denied (publickey)`.
  GitHub's tag lookup still reports no remote `v0.1.3` reference; no remote
  tag or release was created or moved.
- **Fix direction:** retry through the configured GitHub SSH identity without
  exposing credentials; preserve all existing tags.
- **Status:** blocked pending successful authenticated tag publication.

## EVID-069 - v0.1.3 tag publication authentication blocker

- **Timestamp:** `2026-09-24`
- **Category:** push
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Local tag:** annotated `v0.1.3` still targets
  `63c9f9e789623bd9ba4eee16ac222c8bc422bf6a`; it has not been moved.
- **Recovery attempts:** verified the branch commit through GitHub's API;
  retried tag authentication with the configured SSH identity; checked for an
  HTTPS Git credential helper, GitHub CLI, and runtime GitHub token without
  displaying credential values.
- **Observed:** SSH still returned `Permission denied (publickey)`. No
  authenticated HTTPS helper or GitHub CLI is available in this environment.
  GitHub API lookups return 404 for remote tag `v0.1.3` and its release.
  Workflow-run listing contains no v0.1.3 run.
- **Blocker:** the current shell lacks an accepted GitHub write credential
  for publishing the approved tag.
- **Fix direction:** restore GitHub write authentication through the normal
  secure environment, then push the existing local annotated tag. Do not
  paste private keys or tokens into chat.
- **Status:** blocked; branch commit and local APK are ready, but the hosted
  workflow and GitHub release cannot start until tag publication succeeds.

## EVID-070 - v0.1.3 tag published and hosted workflow started

- **Timestamp:** `2026-09-24`
- **Category:** push
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Tag:** annotated `v0.1.3`, tag object
  `56d7013af486b1b8b1adac6f34fd8f73765d86c7`, target commit
  `63c9f9e789623bd9ba4eee16ac222c8bc422bf6a`.
- **Command:** `git push origin refs/tags/v0.1.3`.
- **Observed:** a retry succeeded without force. `git ls-remote` and GitHub's
  tag API confirm the remote annotated tag peels to the approved commit and
  is tagged by `albrp97`. Existing tags remain unchanged.
- **Hosted run:** `36015108549`
  (`https://github.com/albrp97/SleepInducer/actions/runs/36015108549`),
  triggered by the v0.1.3 tag.
- **Release status:** the build, signature, metadata, and install steps
  passed; the launch step failed before the app started because
  `android-emulator-runner@v2` splits multiline scripts into individual shell
  commands. Publication was skipped and no GitHub release or APK asset exists.
- **History:** EVID-068 and EVID-069 retain the initial SSH authentication
  failures; this entry records the successful retry and supersedes their
  blocked delivery state.
- **Status:** passed for remote tag publication; the hosted attempt failed
  before app launch and publication.

## EVID-071 - Hosted v0.1.3 launch workflow failure

- **Timestamp:** `2026-09-24`
- **Category:** automatedFunctionality
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Test ID:** `release-apk-launch-api35-v0.1.3`.
- **Requirement:** acceptance criterion 4; the signed APK must install and
  launch on the API 35 emulator before publication.
- **Command:** hosted GitHub Actions run
  `36015108549`, job `107685496420`, step `Install and launch release APK`.
- **System boundary:** signed release APK, Android API 35 emulator, Activity
  Manager, and GitHub release publication gate.
- **Assertions:** the APK installs, the launch command executes, the package
  process and resumed main activity are observed, and only then may the
  workflow publish the release.
- **Expected:** installation and launch both pass; the workflow publishes
  `sleep-inducer-release.apk`.
- **Observed:** build, signer/package/version checks, and `adb install`
  succeeded. The emulator action passed the launch script one line at a time;
  `/usr/bin/sh -c if launch_result=...; then` failed with
  `Syntax error: end of file unexpected (expecting "fi")` before `am start`
  ran. Launch failed and publication was skipped. The upstream action parser
  is documented at
  `https://github.com/ReactiveCircus/android-emulator-runner/blob/v2/lib/script-parser.js`.
- **Artifacts:** `https://github.com/albrp97/SleepInducer/actions/runs/36015108549`,
  job `107685496420`.
- **Status:** failed; no app launch or GitHub release asset was produced.
- **Fix direction:** run the launch logic from one Bash script command and
  validate it with a new, explicitly approved release version. Do not move or
  reuse `v0.1.3`.

## EVID-072 - Release-launch regression and workflow checks

- **Timestamp:** `2026-09-24`
- **Category:** regression
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Requirement:** acceptance criterion 4; the emulator action must receive a
  single command, and the launch assertion must retain its success and failure
  behavior.
- **Command:** `bash .github/scripts/test-release-apk-install.sh`;
  `bash -n .github/scripts/verify-release-apk-install.sh`;
  `bash -n .github/scripts/test-release-apk-install.sh`; parse
  `.github/workflows/release-apk.yml` with PyYAML 6 and assert the emulator
  `script` input is exactly
  `bash .github/scripts/verify-release-apk-install.sh`.
- **Automated:** true
- **Assertions:** empty `am start -W` output succeeds only with a process and
  resumed main activity; `Status: ok` remains accepted; nonzero launch and
  missing process/activity fail with diagnostics; install failure stops
  execution before the launch command; the workflow passes one command to the
  emulator action.
- **Expected:** all mocked cases and static workflow assertions pass.
- **Observed:** five mocked success/failure cases passed, both Bash files
  parsed, YAML parsed, and the workflow script input was exactly one line.
- **Status:** passed for local mocked regression and syntax checks.
- **Coverage gap:** no local `adb` or Android SDK is available in this shell,
  so a real API 35 rerun must occur in the next approved hosted workflow.

## EVID-073 - Local API 35 rerun unavailable

- **Timestamp:** `2026-09-24`
- **Category:** automatedFunctionality
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Test ID:** `release-apk-launch-api35`.
- **Command:** `adb devices -l`; inspect the configured SDK paths and run the
  release-launch script against the API 35 emulator.
- **System boundary:** signed release APK and API 35 Android emulator.
- **Assertions:** install succeeds, app process runs, and
  `com.sleepinducer.app/.MainActivity` is resumed.
- **Expected:** the corrected script passes on a real local API 35 emulator.
- **Observed:** `adb` is not installed or on `PATH`, neither
  `ANDROID_HOME` nor `ANDROID_SDK_ROOT` is configured, and no emulator was
  available. The hosted v0.1.3 attempt remains a failure because its script
  syntax prevented app launch.
- **Status:** blocked.
- **Blocker:** real API 35 functionality must be rerun by a new hosted
  workflow; the immutable failed v0.1.3 tag cannot execute the correction.
- **Next decision:** approve a new version/tag before another hosted attempt.

## EVID-076 - Hosted v0.1.4 release install and launch

- **Timestamp:** `2026-09-24`
- **Category:** automatedFunctionality
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Test ID:** `release-apk-launch-api35-v0.1.4`.
- **Requirement:** acceptance criterion 4; the signed APK must verify, install,
  launch, and be published only after the API 35 functionality check passes.
- **Command:** hosted GitHub Actions run `36039522821`, job `107767922615`;
  build command `./gradlew test lintDebug assembleRelease --no-daemon`;
  exact workflow step `Install and launch release APK`.
- **System boundary:** signed v0.1.4 APK, API 35 emulator, Activity Manager,
  package process, resumed main activity, and GitHub release publication.
- **Assertions:** signer fingerprint, package name, version name `0.1.4`,
  version code `5`, successful install, active application process, resumed
  `com.sleepinducer.app/.MainActivity`, and publication after these checks.
- **Expected:** the signed APK installs and its main activity becomes
  resumed; GitHub publishes only the verified APK.
- **Observed:** build, signature, package/version, and install steps passed.
  `am start -W` returned exit code `0` with `Status: timeout`, so the script
  correctly treated the status as inconclusive and verified process PID
  `2035` plus `topResumedActivity=...com.sleepinducer.app/.MainActivity`.
  The step passed and GitHub published the release.
- **Artifacts:** `https://github.com/albrp97/SleepInducer/actions/runs/36039522821`,
  `https://github.com/albrp97/SleepInducer/releases/tag/v0.1.4`.
- **Status:** passed.

## EVID-077 - Downloaded and verified the GitHub release APK

- **Timestamp:** `2026-09-24`
- **Category:** smoke
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Command:** download the v0.1.4 release asset to a temporary Downloads
  path; compare `sha256sum` with GitHub's asset digest; run `unzip -tq`; move
  the verified file to
  `/home/ghiki/Downloads/sleep-inducer-release.apk`.
- **Expected:** the downloaded APK matches GitHub's uploaded asset and is a
  readable APK archive.
- **Observed:** GitHub reports asset size `6,561,012` bytes and digest
  `sha256:67642061a75e7d47727165049bb680951d18e1ef9a5bb59327440d2dfdead2d5`.
  The local download matches that digest and archive integrity passed.
- **Artifacts:** `/home/ghiki/Downloads/sleep-inducer-release.apk`.
- **Status:** passed.

## EVID-078 - v0.1.4 release commit

- **Timestamp:** `2026-09-24`
- **Category:** commit
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Command:** `git commit -m "fix(release): target v0.1.4 APK"`.
- **Commit:** `da1c876260217e05e6de11efd12a7100cc86784b`.
- **Author:** `albrp97`.
- **Branch:** `ticket/safe-session-controls`.
- **Paths:** the release workflow, two release launch scripts, app version
  metadata, README, TICKET-011, and its evidence record.
- **Observed:** commit created without rewriting history.
- **Status:** passed.

## EVID-079 - v0.1.4 branch and tag published

- **Timestamp:** `2026-09-24`
- **Category:** push
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Remote:** `origin` (`github-personal:albrp97/SleepInducer.git`).
- **Branch:** `ticket/safe-session-controls`.
- **Commit:** `da1c876260217e05e6de11efd12a7100cc86784b`.
- **Tag:** annotated `v0.1.4`, target commit
  `da1c876260217e05e6de11efd12a7100cc86784b`.
- **Commands:** `git push origin ticket/safe-session-controls`;
  `git push origin refs/tags/v0.1.4`.
- **Observed:** both pushes succeeded without force. GitHub Actions run
  `36039522821` completed successfully and created the v0.1.4 release asset.
- **Status:** passed.

## EVID-074 - User approval for v0.1.4

- **Timestamp:** `2026-09-24`
- **Category:** gate
- **Owner:** user
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Change:** `CHG-003`, moving the immutable release target from failed
  `v0.1.3` to signed `v0.1.4` / version code `5`.
- **Approval source:** the user selected `authorize-v0.1.4` in response to
  the explicit new-tag approval request after hosted run `36015108549` failed
  before app launch.
- **Scope:** update app version metadata and exact workflow assertion,
  preserve all existing tags, run the corrected signature/install/launch
  workflow, and publish only after all hosted checks pass.
- **Observed:** approval was recorded before version changes, commit, tag, or
  publication.
- **Status:** approved; EVID-076 through EVID-079 record completed release
  implementation and delivery.

## EVID-075 - Approved v0.1.4 release contract checks

- **Timestamp:** `2026-09-24`
- **Category:** regression
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-011`
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.
- **Requirement:** CHG-003; the APK and workflow must target version name
  `0.1.4` and version code `5`, and the emulator action must invoke the
  corrected launch check as one command.
- **Command:** `git diff --check`;
  `bash .github/scripts/test-release-apk-install.sh`;
  `bash -n .github/scripts/verify-release-apk-install.sh`;
  `bash -n .github/scripts/test-release-apk-install.sh`; PyYAML 6.0.3
  parsing and assertions against the release workflow and
  `app/build.gradle.kts`.
- **Automated:** true
- **Assertions:** five mocked install/launch success and failure cases pass;
  the action script input is one command; Gradle metadata and workflow
  version-code assertion agree on 0.1.4/code 5.
- **Expected:** all focused checks pass without changing the previously
  approved tag.
- **Observed:** regression cases passed, both Bash files parsed, YAML parsed,
  version metadata matched, and `git diff --check` passed. The actual API 35
  install/launch result is recorded in EVID-076.
- **Status:** passed for focused local checks.
