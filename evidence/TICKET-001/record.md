# Evidence Record - TICKET-001

**Context:** Creating the Android build foundation
**Planning chain:** `OBJ-001` -> `SCOPE-001` -> `CAP-005`, `CAP-006` ->
`PHASE-001` -> `FEAT-001` -> `TICKET-001`
**Repository:** `/home/ghiki/code/sleep-inducer`
**Base revision:** unavailable; the directory was not a Git repository at
baseline
**Development mode:** automatic
**Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
`all-validation`
**Readiness:** committed; ticket lifecycle terminal

## Acceptance coverage

- Clean-clone Android build and debug APK.
- Offline launch boundary.
- Minimal manifest permissions.
- Automated functionality test crossing the Android application boundary.

## Entries

### EVID-001 - Protected implementation baseline

- **Phase:** `PHASE-001`
- **Feature:** `FEAT-001`
- **Ticket:** `TICKET-001`
- **Requirement/flow:** protected baseline before Android implementation
- **Category:** baseline
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-001`
- **Source references:** `AGENTS.md`, `README.md`,
  `docs/planning/repo-map.md`
- **Command or steps:**
  - `find . -maxdepth 2 -type f -print | sort`
  - `find . -type f \( -name '*.gradle' -o -name '*.gradle.kts' -o -name 'AndroidManifest.xml' -o -name 'settings.gradle*' -o -name 'gradlew' \) -print | sort`
  - `git status --short`
  - `java -version`
  - `gradle --version`
  - `adb version`
  - inspected the session toolchain for JDK, Gradle, Android SDK 35, and
    platform tools
- **Automated:** true
- **Test ID:** `baseline-project-surface`
- **System boundary:** repository filesystem and build-tool availability
- **Assertions:**
  - The repository contains only foundational documentation and `.github`
    workflow scaffolding.
  - No Android source, Gradle wrapper, manifest, tests, or APK exists.
  - No Git metadata or base revision is available.
  - JDK 17, Gradle 8.9, Android SDK platform 35, build tools 35.0.0, and
    platform tools are available in the session toolchain, but are not on the
    default shell `PATH`.
- **Expected:** implementation starts from an empty Android source/build
  boundary and must use the confirmed JDK 17/SDK 35 toolchain.
- **Observed:** all assertions matched the repository and environment.
- **Status:** passedWithConcerns
- **Artifacts:** `docs/planning/repo-map.md`,
  `/home/ghiki/.copilot/session-state/f1af4c00-cd3d-41b5-b8b5-483ada2650b6/files/toolchain`
- **Accepted warning:** Git initialization and PATH-independent toolchain
  setup remain delivery decisions; no source files were modified by this
  baseline capture.

### EVID-002 - Debug APK build

- **Phase:** `PHASE-001`
- **Feature:** `FEAT-001`
- **Ticket:** `TICKET-001`
- **Requirement/flow:** clean-clone Android build and debug APK
- **Category:** qualityGate
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-001`
- **Source references:** `settings.gradle.kts`, `build.gradle.kts`,
  `app/build.gradle.kts`, `AGENTS.md`
- **Command or steps:**
  `JAVA_HOME=<session-toolchain>/jdk ANDROID_HOME=<session-toolchain>/android-sdk
  ./gradlew assembleDebug --offline`
- **Automated:** true
- **Test ID:** `debug-apk-build`
- **System boundary:** Gradle Android application build
- **Assertions:** the build completes successfully and produces
  `app/build/outputs/apk/debug/app-debug.apk`.
- **Expected:** a reproducible debug APK is created with JDK 17 and Android
  SDK 35.
- **Observed:** build succeeded; APK size was `9,864,528` bytes.
- **Status:** passed
- **Artifacts:** `app/build/outputs/apk/debug/app-debug.apk`,
  `app/build/outputs/apk/debug/output-metadata.json`

### EVID-003 - Android launch functionality

- **Phase:** `PHASE-001`
- **Feature:** `FEAT-001`
- **Ticket:** `TICKET-001`
- **Requirement/flow:** offline setup boundary and minimal manifest
- **Category:** automatedFunctionality
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-001`
- **Source references:** `app/src/androidTest/java/com/sleepinducer/app/LaunchFlowTest.kt`,
  `app/src/main/java/com/sleepinducer/app/MainActivity.kt`,
  `app/src/main/AndroidManifest.xml`
- **Command or steps:**
  `JAVA_HOME=<session-toolchain>/jdk ANDROID_HOME=<session-toolchain>/android-sdk
  ./gradlew connectedDebugAndroidTest`
- **Automated:** true
- **Test ID:** `LaunchFlowTest`
- **System boundary:** installed Android APK, launched `MainActivity`, and
  package metadata on emulator `emulator-5554`
- **Assertions:**
  - `launchesTheOfflineSetupBoundary` finds and displays `Sleep Inducer`.
  - The setup purpose text is visible.
  - `manifestHasNoNetworkPermission` confirms the installed package does not
    request `android.permission.INTERNET`.
- **Expected:** both tests pass on API 35 without a network or account flow.
- **Observed:** 2 tests passed, 0 failed, 0 skipped on
  `sdk_gphone64_x86_64`, API 35; report success rate was 100%.
- **Status:** passed
- **Artifacts:** `app/build/reports/androidTests/connected/debug/com.sleepinducer.app.LaunchFlowTest.html`,
  `app/build/outputs/androidTest-results/connected/debug/TEST-emulator-5554 - 15-_app-.xml`

### EVID-004 - Automatic validation of ticket functionality

- **Phase:** `PHASE-001`
- **Feature:** `FEAT-001`
- **Ticket:** `TICKET-001`
- **Requirement/flow:** all TICKET-001 acceptance outcomes
- **Category:** automaticValidation
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-001`
- **Source references:** `docs/planning/tickets/open/TICKET-001-android-build-foundation.md`,
  `app/build/reports/androidTests/connected/debug/com.sleepinducer.app.LaunchFlowTest.html`
- **Command or steps:** inspected the terminal Gradle build result and the
  emulator-backed `LaunchFlowTest` result against each acceptance outcome.
- **Automated:** true
- **Test ID:** `TICKET-001-automatic-validation`
- **System boundary:** Gradle build output and installed Android app on API 35
  emulator
- **Assertions:** APK exists, the app launches into the offline setup boundary,
  and the manifest has no network permission.
- **Expected:** every acceptance outcome has terminal executable evidence.
- **Observed:** all three acceptance outcomes are covered by the build and the
  two passing functionality tests.
- **Status:** passed
- **Artifacts:** `app/build/outputs/apk/debug/app-debug.apk`,
  `app/build/reports/androidTests/connected/debug/com.sleepinducer.app.LaunchFlowTest.html`

### EVID-005 - Unit-test command

- **Phase:** `PHASE-001`
- **Feature:** `FEAT-001`
- **Ticket:** `TICKET-001`
- **Requirement/flow:** repository unit-test gate
- **Category:** unit
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-001`
- **Command or steps:** `./gradlew test --offline`
- **Automated:** true
- **Test ID:** `gradle-unit-tests`
- **System boundary:** Gradle unit-test task
- **Assertions:** the task completes without failures; no unit source set
  exists yet, so the task reports `NO-SOURCE`.
- **Expected:** no unit-test regression is introduced by the foundation.
- **Observed:** `BUILD SUCCESSFUL`; debug and release unit-test tasks reported
  `NO-SOURCE`.
- **Status:** passedWithConcerns
- **Accepted warning:** domain unit tests are intentionally deferred to
  `FEAT-002` and later tickets; this does not replace functionality evidence.

### EVID-006 - Android lint

- **Phase:** `PHASE-001`
- **Feature:** `FEAT-001`
- **Ticket:** `TICKET-001`
- **Requirement/flow:** repository quality gate
- **Category:** staticAnalysis
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-001`
- **Command or steps:** `./gradlew lintDebug --offline`
- **Automated:** true
- **Test ID:** `android-lint-debug`
- **System boundary:** Android lint over the app and debug test sources
- **Assertions:** the lint report contains no errors or warnings.
- **Expected:** the foundation has no introduced lint findings.
- **Observed:** report title was `Lint Report: No errors or warnings`.
- **Status:** passed
- **Artifacts:** `app/build/reports/lint-results-debug.html`

### EVID-007 - Final functionality rerun

- **Phase:** `PHASE-001`
- **Feature:** `FEAT-001`
- **Ticket:** `TICKET-001`
- **Requirement/flow:** final Android functionality gate
- **Category:** automatedFunctionality
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-001`
- **Command or steps:** `./gradlew connectedDebugAndroidTest --offline`
- **Automated:** true
- **Test ID:** `LaunchFlowTest-final`
- **System boundary:** installed APK and Android API 35 emulator
  `emulator-5554`
- **Assertions:** both launch-flow tests pass; the app shows the setup boundary
  and the installed package does not request Internet permission.
- **Expected:** 2 tests pass with no failures or skips.
- **Observed:** 2 tests passed, 0 failed, 0 skipped on
  `sdk_gphone64_x86_64`, API 35.
- **Status:** passed
- **Artifacts:** `app/build/reports/androidTests/connected/debug/com.sleepinducer.app.LaunchFlowTest.html`,
  `app/build/outputs/androidTest-results/connected/debug/TEST-emulator-5554 - 15-_app-.xml`

## Open blockers

- None for starting implementation after configuring commands to use the
  available session toolchain.

### EVID-008 - Foundation commit

- **Phase:** `PHASE-001`
- **Feature:** `FEAT-001`
- **Ticket:** `TICKET-001`
- **Requirement/flow:** reviewed Android build foundation delivery
- **Category:** commit
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-001`
- **Source references:** `docs/planning/reviews/TICKET-001-review.md`,
  `docs/planning/tickets/closed/TICKET-001-android-build-foundation.md`
- **Commit:** `8480d9b18caa705252a996e4d132fb779ac29bdb`
- **Subject:** `feat(android): add build foundation`
- **Source branch:** `ticket/android-build-foundation`
- **Intended base:** repository default branch, not configured
- **Committed paths:** all staged project-context, Android foundation,
  planning, evidence, and workflow files in the scoped root commit
- **Readiness references:** `EVID-002` through `EVID-007`,
  `docs/planning/reviews/TICKET-001-review.md`
- **Accepted warnings:** empty unit source set and unavailable optional churn
  analyzer remain documented concerns; neither blocks this foundation commit.
- **Upstream:** no remote or upstream branch is configured
- **Next action:** implement `TICKET-002` under the automatic delivery loop
- **Status:** passed
