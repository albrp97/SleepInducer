# Ticket 007 - Establishing the foreground session service

**Ticket ID:** `TICKET-007`
**Status:** inProgress
**Parent feature:** `FEAT-006`
**Parent phase:** `PHASE-003`
**Parent objective:** `OBJ-001`
**Parent scope:** `SCOPE-001`
**Capabilities:** `CAP-002`, `CAP-004`, `CAP-005`
**Owner:** Android platform implementation
**Source paths:** `vision.md`, `AGENTS.md`,
`docs/specs/project-scope.md`, `docs/specs/capability-map.md`,
`docs/research/sleep-onset-evidence.md`,
`docs/planning/phases/open/PHASE-003-screen-off-relaxation.md`,
`docs/planning/features/open/FEAT-006-screen-off-session-continuity.md`,
`docs/planning/features/closed/FEAT-005-gentle-phase-signals.md`,
`docs/planning/tickets/closed/TICKET-006-phase-haptic-coordinator.md`
**Development mode:** automatic
**Approval state:** bootstrap-authorized automatic child planning
**Last updated:** 2026-09-10

## Outcome

Establish a user-started Android foreground-service boundary for the active
breathing session, with an explicit target-API service declaration, visible
notification, deterministic start/stop lifecycle, and no hidden background
execution.

## Scope

- Resolve the target API 35 service declaration as `specialUse`, because the
  timed haptic breathing session does not match the documented short-service
  limit or another standard service category.
- Declare `FOREGROUND_SERVICE` and
  `FOREGROUND_SERVICE_SPECIAL_USE`, plus the required manifest subtype
  explanation for the user-started timed haptic session.
- Add an `exported=false` `BreathingSessionService` with explicit start and
  stop actions and a local binder for deterministic instrumentation access.
- Start foreground execution only from the explicit start action, create a low
  importance notification channel, and expose an active service state.
- Reject missing or invalid session-start configuration without a
  success-shaped active state.
- Stop foreground execution and release service-owned lifecycle state on the
  explicit stop action and service destruction.
- Add unit coverage for command parsing and lifecycle-state cleanup, plus
  packaged Android functionality coverage for manifest declaration, start,
  active state, stop, and cleanup.

## Non-goals

- Wiring the Compose screen to service controls; that belongs to `FEAT-007`.
- Full runner/haptic session orchestration and display-off timing evidence;
  those belong to follow-up tickets in `FEAT-006`.
- Auto-start, sticky restart, indefinite background operation, or hidden
  execution.
- Selecting a final Google Play distribution approval; the `specialUse`
  declaration remains subject to Play review and is an explicit release gate.
- Health sensing, audio, persistence, accounts, network access, or medical
  claims.

## Affected surfaces

- `app/src/main/AndroidManifest.xml`.
- `app/src/main/java/com/sleepinducer/app/session/`.
- `app/src/test/java/com/sleepinducer/app/session/`.
- `app/src/androidTest/java/com/sleepinducer/app/ForegroundSessionServiceFlowTest.kt`.
- `app/build.gradle.kts` if the service-test boundary requires an existing
  AndroidX test rule.
- `evidence/TICKET-007/`.

## Dependencies and risks

- **Dependencies:** target API 35, Android foreground-service rules,
  notification channels, and completed phase runner/haptic contracts.
- **Risks:** policy rejection of `specialUse`, service start restrictions,
  notification permission behavior, process destruction, and accidental
  success-shaped failure handling.
- **Mitigation:** start only from visible user flow, declare the documented
  subtype, keep the service non-exported and non-sticky, expose explicit
  lifecycle state, and test failure/cleanup paths through the Android
  boundary.

## Acceptance criteria and automated functionality tests

1. **Policy-compliant declaration:** Given target API 35, the manifest declares
   a non-exported `specialUse` foreground service, both required foreground
   service permissions, and a clear subtype explanation for the timed haptic
   breathing use case.
   - **Unit coverage:** `ForegroundSessionServiceTest` verifies that only the
     explicit start action is accepted; installed manifest policy is covered at
     the Android application boundary.
   - **Functionality test:** `ForegroundSessionServiceFlowTest#declaresSpecialUseServiceAndPermissions`
     inspects the installed package manifest and service metadata.
   - **Expected state:** the app does not rely on an undeclared or
     short-service fallback.
2. **Explicit user-started activation:** Given a valid session-start action,
   the service enters foreground mode with a visible low-importance
   notification and exposes `ACTIVE` state.
   - **Unit test:** `ForegroundSessionServiceTest#startsOnlyFromExplicitAction`.
   - **Functionality test:** `ForegroundSessionServiceFlowTest#startsForegroundServiceFromExplicitAction`
     starts and binds the packaged service, then asserts active state and a
     foreground notification.
   - **Expected state:** service work is user-started and visible to the
     operating system.
3. **Invalid-start honesty:** Given a missing or unsupported duration, the
   service reports `FAILED` and does not expose an active session.
   - **Unit test:** `ForegroundSessionServiceTest#rejectsInvalidStartConfiguration`.
   - **Functionality test:** `ForegroundSessionServiceFlowTest#rejectsInvalidStartWithoutActiveState`
     sends invalid start intents and asserts a non-success state.
   - **Expected state:** no service success or hidden fallback is reported.
4. **Stop and destruction cleanup:** Given an active service, explicit stop or destruction reaches a non-active state, removes foreground
ownership, and releases all service-owned lifecycle state.
   - **Unit tests:**
     `ForegroundSessionServiceTest#stopsAndCleansUpIdempotently` and
     `ForegroundSessionServiceTest#interruptsActiveSessionWithoutLeavingDuration`.
   - **Functionality test:** `ForegroundSessionServiceFlowTest#stopsAndCleansUpService`
     stops the packaged service, asserts inactive state, and verifies no
     foreground ownership remains.
   - **Expected state:** no future service work survives stop or destruction.

## Baseline and evidence

- **Protected baseline:** terminal `PHASE-002`, 23 packaged Android
  functionality tests, unit tests, lint, and debug APK build remain passing
  before service changes.
- **Commands:** `./gradlew test --offline`, `./gradlew lintDebug --offline`,
  `./gradlew assembleDebug --offline`, and
  `./gradlew connectedDebugAndroidTest --offline`.
- **Evidence path:** `evidence/TICKET-007/`.
- **Classification:** `automaticValidation` only, using the configured
  `rubber-duck` / `gpt-5.6-luna` / high / `all-validation` profile.

## User-validation plan

Automatic mode requires the agent to execute the service functionality charter
and record `automaticValidation` without waiting. Physical display-off,
lock-screen, notification, and haptic timing validation remains a later
`FEAT-006` acceptance boundary after the UI and full service orchestration
exist.

## Definition of done

- The foreground-service policy and target API declarations are explicit.
- The service is non-exported, user-started, visible, non-sticky, and
  capability/failure honest.
- Start, invalid start, stop, destruction, manifest, notification, and
  cleanup paths have unit and packaged Android functionality tests.
- No hidden background fallback, auto-start, health data, network, or medical
  behavior is added.
- Review, static analysis, automatic validation, and lifecycle evidence are
  terminal.
