# Evidence Record - TICKET-005

**Ticket:** `TICKET-005` - Creating the cancellable breathing session runner
**Feature:** `FEAT-004` - Advancing through predictable breathing phases
**Phase:** `PHASE-002` - Delivering reliable guided breathing sessions
**Objective:** `OBJ-001`
**Scope:** `SCOPE-001`
**Status:** complete
**Readiness:** committed; ticket lifecycle terminal
**Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
`all-validation`

## EVID-001 - Protected baseline

- **Category:** baseline
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-005`
- **Baseline commit:** `e9c5fd6ec67c65747b9b34316a16ce06c4803b06`
- **Command or steps:** `./gradlew test --offline`, `./gradlew lintDebug
  --offline`, `./gradlew assembleDebug --offline`, and
  `./gradlew connectedDebugAndroidTest --offline`
- **Environment:** JDK 17, Android SDK 35, API 35 emulator
  `emulator-5554`
- **Expected:** existing protocol, haptic, setup, foundation, manifest, and
  offline behavior remains buildable, lint-clean, and functionality-green
  before runner work.
- **Observed:** unit tests, lint, debug APK build, and all 15 connected Android
  tests passed with zero failures and zero skips.
- **Status:** passed
- **Artifacts:** `app/build/outputs/apk/debug/app-debug.apk`,
  `app/build/reports/lint-results-debug.html`,
  `app/build/reports/androidTests/connected/debug/`

## EVID-002 - Test-first red state

- **Category:** unit
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-005`
- **Command or steps:** `./gradlew testDebugUnitTest --offline` after adding
  session-runner unit tests and before adding runner production classes.
- **Expected:** the new tests fail because the planned runner and scheduler
  boundary does not exist yet.
- **Observed:** compilation failed with unresolved references for
  `BreathingSessionRunner`, `SessionScheduler`, and `SessionSchedule`.
- **Status:** expectedFailure
- **Artifacts:** Gradle compiler output from the red TDD run.

## EVID-003 - Session runner implementation and unit verification

- **Category:** unit
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-005`
- **Command or steps:** `./gradlew test --offline`
- **Test ID:** `BreathingSessionRunnerTest`
- **System boundary:** pure Kotlin runner, monotonic scheduler contract, fake
  schedule handles, and immutable breathing protocol.
- **Assertions:** immediate inhale, phase scheduling, delayed callback
  reconciliation, terminal cancellation, stale callback rejection, and
  exactly-once completion.
- **Expected:** all protocol, haptic, and session-runner unit tests pass in
  debug and release variants.
- **Observed:** 13 tests passed in each unit-test variant, with 0 failures and
  0 errors.
- **Status:** passed
- **Artifacts:** `app/build/test-results/testDebugUnitTest/`,
  `app/build/test-results/testReleaseUnitTest/`

## EVID-004 - Packaged session-runner functionality

- **Category:** automatedFunctionality
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-005`
- **Command or steps:** `./gradlew connectedDebugAndroidTest --offline`
- **Test ID:** `SessionRunnerFlowTest`
- **System boundary:** packaged APK and Android instrumentation runtime on API
  35 emulator `emulator-5554`.
- **Assertions:**
  - start emits inhale and advances to exhale at the configured boundary;
  - delayed callbacks preserve monotonic phase ordering and schedule targets;
  - stop and interruption cancel scheduled work and ignore stale callbacks;
  - natural completion is emitted once and cannot be restarted by a callback.
- **Expected:** all runner outcomes and all protected foundation, setup,
  protocol, and haptic flows pass through the application artifact.
- **Observed:** 19 instrumentation tests passed, 0 failed, 0 skipped.
- **Status:** passed
- **Artifacts:** `app/build/reports/androidTests/connected/debug/`,
  `app/build/outputs/androidTest-results/connected/debug/`

## EVID-005 - Session runner automatic validation

- **Category:** automaticValidation
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-005`
- **Source references:** `docs/planning/tickets/open/TICKET-005-cancellable-session-runner.md`,
  `evidence/static-analysis/TICKET-005.md`
- **Command or steps:** inspected the runner state transitions, scheduling
  targets, terminal cancellation, unit output, lint output, APK output, and
  packaged functionality output against each acceptance outcome.
- **Test ID:** `TICKET-005-automatic-validation`
- **Assertions:** no terminal state can emit later active phases; delayed
  callbacks use monotonic elapsed time; completion is terminal and idempotent.
- **Expected:** all four outcomes are terminally covered without Android
  service, haptic, persistence, or safety-scope expansion.
- **Observed:** all four outcomes passed; all protected flows remained green;
  service and physical-device behavior remain deferred as planned.
- **Status:** passed
- **Artifacts:** `app/build/outputs/apk/debug/app-debug.apk`,
  `app/build/reports/androidTests/connected/debug/`

## EVID-006 - Deterministic static analysis

- **Category:** staticAnalysis
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-005`
- **Command or steps:** `./gradlew test lintDebug assembleDebug
  connectedDebugAndroidTest --offline`; optional
  `npx --no-install aidd churn --json --days 90 --top 20 --min-loc 50`
- **Expected:** compiler, unit, lint, APK, and functionality checks pass, and
  optional analysis is classified honestly.
- **Observed:** required Gradle checks passed; churn was unavailable because
  `aidd@3.1.0` is not installed and installation was not authorized.
- **Status:** passedWithConcerns
- **Accepted warning:** optional churn coverage is unavailable.
- **Parity:** `notApplicable`; no Android pull-request pipeline is configured
  or discoverable.
- **Artifacts:** `evidence/static-analysis/TICKET-005.md`,
  `app/build/reports/lint-results-debug.html`

## EVID-007 - Review readiness

- **Category:** review
- **Owner:** agent
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-005`
- **Source references:** `docs/planning/reviews/TICKET-005-review.md`,
  `evidence/static-analysis/TICKET-005.md`
- **Command or steps:** reviewed the final runner diff, planning ancestry,
  state transitions, scheduler contract, protected flows, evidence entries,
  and static-analysis result.
- **Test ID:** `TICKET-005-review`
- **Assertions:** no introduced correctness, safety, privacy, security, scope,
  or architecture defect remains within the ticket boundary.
- **Expected:** review is ready for commit with only documented optional
  coverage concerns.
- **Observed:** no introduced defects; optional churn remains unavailable.
- **Status:** passedWithConcerns
- **Accepted warning:** optional churn coverage is unavailable.
- **Artifacts:** `docs/planning/reviews/TICKET-005-review.md`,
  `evidence/static-analysis/TICKET-005.md`

## Open blockers

- No blockers for implementation. Screen-off service policy and physical-device
  timing remain outside this ticket.

## EVID-008 - Session runner commit

- **Category:** commit
- **Owner:** agent
- **Planning layer:** ticket
- **Parent artifact:** `TICKET-005`
- **Source references:** `docs/planning/reviews/TICKET-005-review.md`,
  `docs/planning/tickets/closed/TICKET-005-cancellable-session-runner.md`
- **Commit:** `8c3f164ddfd6e825860a0960b415ecbb104b1e27`
- **Subject:** `feat(core): add session runner`
- **Source branch:** `ticket/android-build-foundation`
- **Intended base:** repository default branch, not configured
- **Committed paths:** pure session runner, unit tests, packaged
  functionality tests, planning, review, and evidence artifacts
- **Readiness references:** `EVID-001` through `EVID-007`,
  `docs/planning/reviews/TICKET-005-review.md`
- **Accepted warnings:** optional churn analysis is unavailable.
- **Upstream:** no remote or upstream branch is configured
- **Next action:** implement phase-to-cue mapping under `FEAT-005`
- **Status:** passed
