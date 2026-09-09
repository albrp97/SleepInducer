# Ticket 005 - Creating the cancellable breathing session runner

**Ticket ID:** `TICKET-005`
**Status:** complete
**Parent feature:** `FEAT-004`
**Parent phase:** `PHASE-002`
**Parent objective:** `OBJ-001`
**Parent scope:** `SCOPE-001`
**Capabilities:** `CAP-001`, `CAP-004`
**Owner:** Domain implementation
**Source paths:** `vision.md`, `AGENTS.md`,
`docs/specs/project-scope.md`, `docs/specs/capability-map.md`,
`docs/planning/features/open/FEAT-004-predictable-breathing-phases.md`,
`docs/planning/tickets/closed/TICKET-003-breathing-protocol-domain.md`
**Development mode:** automatic
**Approval state:** bootstrap-authorized automatic child planning
**Last updated:** 2026-09-09

## Outcome

Provide a pure Kotlin session runner that starts the completed breathing
protocol, schedules monotonic phase boundaries through an injected clock and
scheduler, and reaches stopped, interrupted, or completed terminal states
without stale callbacks or duplicate terminal events.

## Scope

- Define a scheduler and cancellation-handle contract independent of Android.
- Start a `BreathingProtocol` session at a monotonic timestamp and emit the
  initial inhale state.
- Schedule phase-boundary callbacks from absolute elapsed protocol time rather
  than accumulating callback delay.
- Reconcile scheduler jitter through the existing protocol state calculation so
  phase ordering remains correct.
- Expose explicit start, stop, interruption, and natural-completion
  transitions to an injected state listener.
- Cancel the current scheduled task on every terminal transition and make
  terminal operations idempotent.
- Add fake-clock/fake-scheduler unit coverage and packaged Android
  functionality coverage for start, jitter, stop, interruption, and completion.

## Non-goals

- Android foreground services, notifications, display-off continuity, or
  lifecycle ownership; those belong to `FEAT-006`.
- Vibration delivery or phase-to-cue mapping; those belong to `FEAT-005`.
- Compose controls, persistence, health sensing, adaptive timing, or
  performance scoring.
- Introducing mandatory breath holds or changing the completed protocol
  contract.

## Affected surfaces

- `app/src/main/java/com/sleepinducer/app/breathing/`.
- `app/src/test/java/com/sleepinducer/app/breathing/`.
- `app/src/androidTest/java/com/sleepinducer/app/SessionRunnerFlowTest.kt`.
- `evidence/TICKET-005/`.

## Dependencies and risks

- **Dependencies:** completed `TICKET-003`, the monotonic protocol contract,
  and an injectable scheduling boundary.
- **Risks:** scheduler drift, callbacks after cancellation, race conditions at
  duration boundaries, duplicate completion, and incorrect phase recovery
  after delayed callbacks.
- **Mitigation:** absolute elapsed targets, immutable protocol transitions,
  explicit terminal guards, fake scheduler control, and application-boundary
  regression tests.

## Acceptance criteria and automated functionality tests

1. **Start and phase scheduling:** Given a ready runner and a supported
   duration, starting emits inhale at elapsed zero and schedules the next
   protocol boundary using the configured phase duration.
   - **Unit test:** `BreathingSessionRunnerTest#startsWithInhaleAndSchedulesNextBoundary`.
   - **Functionality test:** `SessionRunnerFlowTest#startsWithInhaleAndAdvancesPhase`
     drives the packaged runner with a fake monotonic scheduler and asserts
     the initial inhale and five-second exhale states.
   - **Expected state:** one active inhale state is emitted immediately, then
     the next active phase is emitted at the configured boundary.
2. **Jitter-tolerant ordering:** Given a scheduler callback arrives late but
   with a monotonic timestamp, the runner derives the current protocol phase
   from elapsed time without accumulating drift or emitting a reversed phase.
   - **Unit test:** `BreathingSessionRunnerTest#reconcilesLateCallbacksWithoutPhaseRegression`.
   - **Functionality test:** `SessionRunnerFlowTest#reconcilesDelayedBoundary`
     advances the fake clock past one boundary and asserts the emitted state
     reflects the current monotonic phase and elapsed time.
   - **Expected state:** phase ordering remains inhale/exhale-correct and the
     next scheduled target remains based on protocol time.
3. **Safe terminal cancellation:** Given stop or interruption, the runner
   emits the corresponding non-success terminal state once, cancels its
   scheduled work, and ignores later callbacks.
   - **Unit test:** `BreathingSessionRunnerTest#stopsAndInterruptsWithoutStaleCallbacks`.
   - **Functionality test:** `SessionRunnerFlowTest#cancelsFutureTransitionsAfterStopAndInterruption`
     invokes both terminal paths through the packaged runner and asserts no
     later active or completed state is delivered.
   - **Expected state:** terminal state is stable and the scheduler has no
     active task.
4. **Exactly-once completion:** Given elapsed time reaches the selected
   duration, the runner emits completion once, cancels future scheduling, and
   cannot restart itself through a later callback.
   - **Unit test:** `BreathingSessionRunnerTest#completesExactlyOnceAtDuration`.
   - **Functionality test:** `SessionRunnerFlowTest#completesOnceAtDuration`
     drives the packaged runner to the terminal timestamp and asserts one
     completion with no later phase or duplicate terminal event.
   - **Expected state:** completion is terminal, observable, and idempotent.

## Baseline and evidence

- **Protected baseline:** completed Android foundation, offline setup,
  breathing protocol, capability-aware haptic adapter, unit tests, lint, debug
  APK build, and 15 emulator functionality tests remain passing before runner
  changes.
- **Commands:** `./gradlew test --offline`, `./gradlew lintDebug --offline`,
  `./gradlew assembleDebug --offline`, and
  `./gradlew connectedDebugAndroidTest --offline`.
- **Evidence path:** `evidence/TICKET-005/`.
- **Classification:** `automaticValidation` only, using the configured
  `rubber-duck` / `gpt-5.6-luna` / high / `all-validation` profile.

## User-validation plan

Automatic mode requires the agent to execute the same session-runner
functionality charter and record `automaticValidation` without waiting. This
ticket has no new visible UI or physical-device behavior, so human validation
is not applicable at this boundary. Screen-off and device timing validation
must wait for the later service and release-readiness tickets.

## Definition of done

- The runner and scheduling contracts are pure Kotlin and Android-independent.
- Start, phase progression, jitter reconciliation, stop, interruption, and
  exactly-once completion are independently tested.
- Every acceptance outcome has an executable packaged functionality test.
- Terminal transitions cancel scheduled work and reject stale callbacks.
- Existing protocol, setup, haptic, offline, and manifest behavior remains
  passing.
- Build, lint, unit, functionality, automatic-validation, review, and static
  analysis evidence is terminal.
