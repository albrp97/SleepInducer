# Ticket 008 - Running the breathing session in the foreground service

**Ticket ID:** `TICKET-008`
**Status:** pending
**Parent feature:** `FEAT-006`
**Parent phase:** `PHASE-003`
**Parent objective:** `OBJ-001`
**Parent scope:** `SCOPE-001`
**Capabilities:** `CAP-001`, `CAP-002`, `CAP-003`, `CAP-004`
**Owner:** Android session implementation
**Source paths:** `vision.md`, `AGENTS.md`,
`docs/specs/project-scope.md`, `docs/planning/phases/open/PHASE-003-screen-off-relaxation.md`,
`docs/planning/features/open/FEAT-006-screen-off-session-continuity.md`,
`docs/planning/tickets/closed/TICKET-007-foreground-session-service.md`
**Development mode:** automatic
**Approval state:** bootstrap-authorized automatic child planning
**Last updated:** 2026-09-14

## Outcome

Make the foreground service own a real timed breathing session, including
phase transitions, gentle haptic delivery, completion, interruption, and
minimal local operational recovery.

## Scope

- Add an Android scheduler backed by the existing monotonic session contract.
- Connect `BreathingSessionRunner` and `PhaseHapticCoordinator` to the service.
- Expose immutable session snapshots to a bound activity through the local
  binder.
- Surface haptic capability and delivery failures without success-shaped state.
- Update the ongoing notification with the current phase and provide an
  explicit stop action.
- Stop and release service work on completion, explicit stop, interruption, or
  destruction.
- Persist only the active/terminal operational state needed to surface an
  interrupted session after process recreation.
- Add unit and packaged Android functionality coverage for live phase changes,
  completion through the session engine, interruption recovery, and cleanup.

## Non-goals

- Compose duration/start/stop controls; those belong to `TICKET-009`.
- Physical-device comfort validation; that belongs to `TICKET-011`.
- Accounts, network access, health sensing, audio, analytics, or medical
  behavior.

## Acceptance criteria and automated functionality tests

1. **Live session ownership:** Given a valid start action and usable haptics,
   the service starts the configured runner, reports the active phase, and
   emits the mapped cue at each phase boundary.
   - **Unit test:** the session engine advances through phase callbacks using
     a fake scheduler.
   - **Functionality test:** `ForegroundSessionServiceFlowTest#advancesActivePhase`
     starts the installed service and observes a phase change through the
     binder snapshot.
2. **Completion and cancellation:** Given natural completion, the session
   reaches `COMPLETED`, cancels future cues, releases foreground ownership,
   and does not restart.
   - **Unit test:** the session engine completes once at the configured
     duration.
   - **Functionality test:** `SessionRunnerFlowTest#completesOnceAtDuration`
     exercises the packaged session engine boundary with a deterministic
     scheduler.
3. **Interruption recovery:** Given process/service destruction during an
   active session, the next bound service reports `INTERRUPTED` and no active
   duration remains.
   - **Unit test:** the local state store and state machine restore an active
     record as interrupted.
   - **Functionality test:** `ForegroundSessionServiceFlowTest#restoresInterruptedSession`
     destroys and rebinds the packaged service, then asserts interrupted state.
4. **Notification stop:** Given the active notification stop action, the
   service reaches `STOPPED`, removes foreground ownership, and cancels future
   cues.
   - **Functionality test:** `ForegroundSessionServiceFlowTest#stopsFromNotificationAction`
     sends the explicit notification action through the installed service
     boundary and asserts terminal cleanup.

## Protected behaviors

- Five-second inhale and five-second exhale remain the only phases.
- No mandatory hold, forced depth, continuous vibration, or hidden restart is
  introduced.
- Unsupported haptics, service failures, and interruptions remain explicit.
- The service remains non-exported and non-sticky.

## Verification and evidence

- `./gradlew test --offline`
- `./gradlew lintDebug --offline`
- `./gradlew assembleDebug --offline`
- `./gradlew connectedDebugAndroidTest --offline`
- Evidence path: `evidence/TICKET-008/`.
- Automatic validation uses `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.

## Definition of done

- The service owns live runner and haptic work with deterministic cleanup.
- Session snapshots are observable from a bound activity.
- Completion, interruption, unsupported haptics, and notification stop are
  covered by executable tests.
- Evidence, static analysis, review, commit, and lifecycle closeout are
  terminal.
