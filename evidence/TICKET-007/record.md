# Evidence Record - TICKET-007

**Repository:** `/home/ghiki/code/sleep-inducer`
**Branch:** `ticket/android-build-foundation`
**Base revision:** `6ebb783b68411b95300257bf574512835fc40cfa`
**Phase:** `PHASE-003`
**Feature:** `FEAT-006`
**Ticket:** `TICKET-007`
**Development mode:** automatic
**Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
`all-validation`
**Evidence path:** `evidence/TICKET-007/`
**Readiness:** reviewed with accepted warnings; commit pending

## Planning chain

`OBJ-001` -> `SCOPE-001` -> `CAP-002`, `CAP-004`, `CAP-005` -> `PHASE-003` ->
`FEAT-006` -> `TICKET-007`

## Acceptance coverage

1. Explicit API 35 `specialUse` service declaration and permissions.
2. User-started foreground activation with notification and active state.
3. Invalid start rejection without success-shaped activation.
4. Explicit stop/destruction cleanup with no foreground ownership.

## Protected flows

- The breathing-only scope and no-account/no-network boundary remain unchanged.
- The service is not auto-started, sticky, exported, or hidden.
- The screen-off session remains user-started from a visible app boundary.
- Service policy failures remain explicit and do not become fake active sessions.
- Existing protocol, runner, haptic mapping, setup, and functionality tests
  remain passing.

## EVID-001 - Protected baseline

- **Category:** baseline
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-007`
- **Source references:** `docs/planning/tickets/open/TICKET-007-foreground-session-service.md`,
  `docs/planning/features/open/FEAT-006-screen-off-session-continuity.md`,
  `docs/planning/phases/open/PHASE-003-screen-off-relaxation.md`
- **Command:** `./gradlew test lintDebug assembleDebug connectedDebugAndroidTest --offline`
- **Automated:** true
- **Assertions:** terminal PHASE-002 behavior and all existing Android
  functionality flows remain green before service changes.
- **Expected:** current unit, lint, APK, and emulator functionality checks
  pass.
- **Observed:** 18 debug unit tests passed, 18 release unit tests passed,
  `lintDebug` passed with no errors or warnings, `assembleDebug` passed, and
  23 Android instrumentation tests passed with zero failures and zero skips.
- **Status:** passed
- **Artifacts:** `app/build/outputs/apk/debug/app-debug.apk`,
  `app/build/reports/lint-results-debug.html`,
  `app/build/reports/androidTests/connected/debug/index.html`

## EVID-002 - Test-first red state

- **Category:** unit
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-007`
- **Source references:** `app/src/test/java/com/sleepinducer/app/session/ForegroundSessionServiceTest.kt`,
  `app/src/androidTest/java/com/sleepinducer/app/ForegroundSessionServiceFlowTest.kt`
- **Command:** `./gradlew testDebugUnitTest --tests com.sleepinducer.app.session.ForegroundSessionServiceTest --offline`
- **Automated:** true
- **Assertions:** service command parsing and lifecycle-state tests compile
  against the planned foreground service boundary.
- **Expected:** tests fail before production service and contract
  implementation because the new symbols do not yet exist.
- **Observed:** compilation failed with unresolved service command, state, and
  binder references.
- **Status:** failed
- **Failure:** expected TDD red state before implementation.
- **Fix:** implement the explicit foreground-service contract and Android
  service host, then rerun focused and packaged tests.
- **Accepted warning:** emulator console may report failure to start its
  optional console while connected tests still complete.

## Open blockers

- Google Play distribution approval for the `specialUse` service remains a
  later release-policy gate and is not silently treated as resolved by this
  ticket.

## EVID-003 - Final technical verification

- **Category:** qualityGate
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-007`
- **Source references:** `app/src/main/java/com/sleepinducer/app/session/`,
  `app/src/test/java/com/sleepinducer/app/session/`,
  `app/src/androidTest/java/com/sleepinducer/app/ForegroundSessionServiceFlowTest.kt`,
  `app/src/main/AndroidManifest.xml`
- **Command:** `./gradlew test lintDebug assembleDebug connectedDebugAndroidTest --offline`
- **Automated:** true
- **Assertions:** existing protocol, runner, haptic, setup, and service
  boundaries remain green; debug and release unit tests pass; lint is clean;
  the debug APK is assembled; packaged Android functionality tests pass.
- **Expected:** no regression and no lint finding.
- **Observed:** 22 debug unit tests passed, 22 release unit tests passed,
  `lintDebug` passed with no errors or warnings, `assembleDebug` produced the
  debug APK, and 27 Android instrumentation tests passed on
  `emulator-5554` API 35. The emulator reported only its known optional
  console-start warning.
- **Status:** passed
- **Artifacts:** `app/build/outputs/apk/debug/app-debug.apk`,
  `app/build/reports/lint-results-debug.html`,
  `app/build/reports/androidTests/connected/debug/index.html`
- **Accepted warning:** `[EmulatorConsole]: Failed to start Emulator console
  for 5554`; the connected test run completed successfully.

## EVID-004 - Packaged Android functionality

- **Category:** automatedFunctionality
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-007`
- **Source references:** `app/src/androidTest/java/com/sleepinducer/app/ForegroundSessionServiceFlowTest.kt`
- **Command:** `./gradlew connectedDebugAndroidTest --offline`
- **Automated:** true
- **Test IDs:** `ForegroundSessionServiceFlowTest#declaresSpecialUseServiceAndPermissions`,
  `ForegroundSessionServiceFlowTest#startsForegroundServiceFromExplicitAction`,
  `ForegroundSessionServiceFlowTest#rejectsInvalidStartWithoutActiveState`,
  `ForegroundSessionServiceFlowTest#stopsAndCleansUpService`
- **System boundary:** installed Android APK manifest, foreground-service
  lifecycle, notification channel, binder state, and explicit service actions.
- **Assertions:** the installed manifest contains the required permissions,
  non-exported `specialUse` metadata and subtype; valid starts reach `ACTIVE`
  with foreground ownership and a low-importance channel; invalid starts reach
  `FAILED` without foreground ownership; explicit stop reaches `STOPPED` and
  clears the active duration and foreground ownership.
- **Expected:** all four TICKET-007 acceptance outcomes are observable through
  the packaged application boundary.
- **Observed:** all 27 instrumentation tests passed with zero failures and
  zero skips on API 35.
- **Status:** passed
- **Artifacts:** `app/build/reports/androidTests/connected/debug/index.html`

## EVID-005 - Automatic functionality validation

- **Category:** automaticValidation
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-007`
- **Source references:** `evidence/TICKET-007/record.md`,
  `app/src/androidTest/java/com/sleepinducer/app/ForegroundSessionServiceFlowTest.kt`
- **Command:** `./gradlew connectedDebugAndroidTest --offline`
- **Automated:** true
- **Test IDs:** `ForegroundSessionServiceFlowTest#declaresSpecialUseServiceAndPermissions`,
  `ForegroundSessionServiceFlowTest#startsForegroundServiceFromExplicitAction`,
  `ForegroundSessionServiceFlowTest#rejectsInvalidStartWithoutActiveState`,
  `ForegroundSessionServiceFlowTest#stopsAndCleansUpService`
- **System boundary:** packaged Android application and operating-system
  foreground-service contract.
- **Assertions:** the service declaration is policy-explicit, valid user-started
  activation is visible and active, invalid input cannot become active, and
  stop cleanup removes service-owned lifecycle state.
- **Expected:** the service boundary satisfies the ticket's observable
  acceptance outcomes without hidden execution or success-shaped fallback.
- **Observed:** the complete packaged functionality charter passed on the API 35
  emulator; no acceptance outcome failed or remained unasserted.
- **Status:** passed
- **Artifacts:** `app/build/reports/androidTests/connected/debug/index.html`

## EVID-006 - Static analysis

- **Category:** staticAnalysis
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-007`
- **Source references:** `evidence/static-analysis/TICKET-007.md`,
  `.github/workflows/workflow-evals.yml`
- **Command:** `git diff --check`; `./gradlew test lintDebug assembleDebug connectedDebugAndroidTest --offline`;
  `npx --no-install aidd churn --json`
- **Automated:** true
- **Expected:** deterministic checks pass and all unavailable optional tools
  are recorded explicitly.
- **Observed:** Git diff hygiene, Gradle tests, Android lint, APK assembly, and
  Android functionality passed. Optional churn analysis was unavailable
  because `aidd@3.1.0` is not installed locally. No Android pull-request
  pipeline is configured, so parity is `notApplicable`.
- **Status:** passedWithConcerns
- **Artifacts:** `evidence/static-analysis/TICKET-007.md`,
  `app/build/reports/lint-results-debug.html`,
  `app/build/outputs/apk/debug/app-debug.apk`,
  `app/build/reports/androidTests/connected/debug/index.html`
- **Accepted warning:** optional churn coverage is unavailable and no package
  was installed.

## EVID-007 - Post-review-cleanup verification

- **Category:** qualityGate
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-007`
- **Source references:** `app/src/main/java/com/sleepinducer/app/session/ForegroundSessionContract.kt`,
  `app/src/main/java/com/sleepinducer/app/session/BreathingSessionService.kt`
- **Command:** `git diff --check`; `./gradlew test lintDebug assembleDebug connectedDebugAndroidTest --offline`
- **Automated:** true
- **Assertions:** the Android-independent command contract compiles after
  separating action constants from the service implementation, and all
  existing technical and packaged functionality checks remain green.
- **Expected:** no regression from the architecture cleanup.
- **Observed:** diff hygiene passed; 22 debug unit tests, 22 release unit tests,
  and 27 Android instrumentation tests passed; lint remained clean and the
  debug APK was rebuilt.
- **Status:** passed
- **Artifacts:** `app/build/outputs/apk/debug/app-debug.apk`,
  `app/build/reports/lint-results-debug.html`,
  `app/build/reports/androidTests/connected/debug/index.html`

## EVID-008 - Final packaged Android functionality

- **Category:** automatedFunctionality
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-007`
- **Source references:** `app/src/androidTest/java/com/sleepinducer/app/ForegroundSessionServiceFlowTest.kt`
- **Command:** `./gradlew connectedDebugAndroidTest --offline`
- **Automated:** true
- **Test IDs:** `ForegroundSessionServiceFlowTest#declaresSpecialUseServiceAndPermissions`,
  `ForegroundSessionServiceFlowTest#startsForegroundServiceFromExplicitAction`,
  `ForegroundSessionServiceFlowTest#rejectsInvalidStartWithoutActiveState`,
  `ForegroundSessionServiceFlowTest#stopsAndCleansUpService`
- **System boundary:** installed Android APK manifest, foreground-service
  lifecycle, notification channel, binder state, and explicit service actions.
- **Assertions:** the installed manifest contains both foreground-service
  permissions, a non-exported `specialUse` service, and its subtype; valid
  starts reach `ACTIVE` with foreground ownership and a low-importance channel;
  invalid starts reach `FAILED` without foreground ownership; explicit stop
  reaches `STOPPED` and clears active duration and foreground ownership.
- **Expected:** all four acceptance outcomes remain observable after the
  contract-boundary cleanup.
- **Observed:** all 27 instrumentation tests passed with zero failures and
  zero skips on the API 35 emulator.
- **Status:** passed
- **Artifacts:** `app/build/reports/androidTests/connected/debug/index.html`
- **Supersedes:** `EVID-004`

## EVID-009 - Final automatic functionality validation

- **Category:** automaticValidation
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-007`
- **Source references:** `evidence/TICKET-007/record.md`,
  `app/src/androidTest/java/com/sleepinducer/app/ForegroundSessionServiceFlowTest.kt`
- **Command:** `./gradlew connectedDebugAndroidTest --offline`
- **Automated:** true
- **Test IDs:** `ForegroundSessionServiceFlowTest#declaresSpecialUseServiceAndPermissions`,
  `ForegroundSessionServiceFlowTest#startsForegroundServiceFromExplicitAction`,
  `ForegroundSessionServiceFlowTest#rejectsInvalidStartWithoutActiveState`,
  `ForegroundSessionServiceFlowTest#stopsAndCleansUpService`
- **System boundary:** packaged Android application and operating-system
  foreground-service contract.
- **Assertions:** policy-explicit declaration, visible user-started activation,
  honest invalid-start failure, and complete explicit-stop cleanup.
- **Expected:** every acceptance outcome has terminal automatic functionality
  evidence using the configured profile.
- **Observed:** the complete functionality charter passed on API 35; no
  acceptance outcome failed or remained unasserted.
- **Status:** passed
- **Artifacts:** `app/build/reports/androidTests/connected/debug/index.html`
- **Supersedes:** `EVID-005`

## EVID-010 - Final static analysis

- **Category:** staticAnalysis
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-007`
- **Source references:** `evidence/static-analysis/TICKET-007.md`,
  `.github/workflows/workflow-evals.yml`
- **Command:** `git diff --check`; `./gradlew test lintDebug assembleDebug connectedDebugAndroidTest --offline`;
  `npx --no-install aidd churn --json`
- **Automated:** true
- **Expected:** final deterministic checks pass and unavailable optional tools
  remain explicitly classified.
- **Observed:** diff hygiene, Gradle tests, lint, APK assembly, and Android
  functionality passed. Churn remained unavailable because `aidd@3.1.0` is
  not installed. The only active CI workflow evaluates workflow contracts,
  so Android local-to-PR parity is `notApplicable`.
- **Status:** passedWithConcerns
- **Artifacts:** `evidence/static-analysis/TICKET-007.md`,
  `app/build/reports/lint-results-debug.html`,
  `app/build/outputs/apk/debug/app-debug.apk`,
  `app/build/reports/androidTests/connected/debug/index.html`
- **Accepted warning:** optional churn coverage is unavailable and no package
  was installed.
- **Supersedes:** `EVID-006`

## EVID-011 - Review decision

- **Category:** review
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-007`
- **Source references:** `docs/planning/reviews/TICKET-007-review.md`,
  `evidence/static-analysis/TICKET-007.md`,
  `docs/planning/tickets/open/TICKET-007-foreground-session-service.md`
- **Command:** final-diff review against the configured ticket, phase, feature,
  evidence, manifest, Android service boundary, and validation artifacts.
- **Automated:** false
- **Expected:** no introduced correctness, safety, privacy, security, scope,
  or architecture defect; all configured local gates are terminal.
- **Observed:** the service boundary is explicit, non-exported, non-sticky,
  user-started, failure-honest, and covered by packaged functionality tests.
  The Android-independent command contract no longer depends on the service
  implementation. Google Play `specialUse` approval and optional churn
  analysis remain documented non-blocking warnings for this local ticket.
- **Status:** passedWithConcerns
- **Artifacts:** `docs/planning/reviews/TICKET-007-review.md`,
  `evidence/static-analysis/TICKET-007.md`
