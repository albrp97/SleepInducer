# Ticket 006 - Creating the phase haptic coordinator

**Ticket ID:** `TICKET-006`
**Status:** complete
**Parent feature:** `FEAT-005`
**Parent phase:** `PHASE-002`
**Parent objective:** `OBJ-001`
**Parent scope:** `SCOPE-001`
**Capabilities:** `CAP-003`, `CAP-004`
**Owner:** Android platform implementation
**Source paths:** `vision.md`, `AGENTS.md`,
`docs/specs/project-scope.md`, `docs/specs/capability-map.md`,
`docs/planning/phases/open/PHASE-002-guided-breathing-sessions.md`,
`docs/planning/features/open/FEAT-003-capability-aware-haptic-guidance.md`,
`docs/planning/features/open/FEAT-004-predictable-breathing-phases.md`,
`docs/planning/features/closed/FEAT-005-gentle-phase-signals.md`,
`docs/planning/tickets/closed/TICKET-004-capability-aware-haptic-adapter.md`,
`docs/planning/tickets/closed/TICKET-005-cancellable-session-runner.md`
**Development mode:** automatic
**Approval state:** bootstrap-authorized automatic child planning
**Last updated:** 2026-09-10

## Outcome

Connect the deterministic breathing session runner to the capability-aware
haptic adapter through an Android-independent coordinator that gives each
breathing phase a documented short cue and cancels all active delivery when the
session becomes terminal.

## Scope

- Define distinct conservative cue meanings for inhale and exhale.
- Map `BreathingPhase` values to bounded `HapticCue` values without changing
  the breathing protocol cadence or adding mandatory holds.
- Coordinate runner state events with `HapticAdapter` delivery.
- Deliver at most one cue for each newly observed active phase, including the
  initial inhale, and ignore repeated state notifications for the same phase.
- Expose explicit unsupported-hardware or rejected-cue results to the caller
  without treating them as successful delivery.
- Cancel the adapter on stop, interruption, and natural completion, including
  when a terminal state arrives after a delayed scheduler callback.
- Add fake-adapter unit coverage and packaged Android functionality coverage
  for mapping, deduplication, unavailable hardware, and terminal cancellation.

## Non-goals

- Foreground services, notifications, screen-off continuity, or lifecycle
  ownership; those belong to `FEAT-006` and `FEAT-007`.
- Compose controls, persistence, accounts, network access, health sensing, or
  adaptive timing.
- Continuous vibration, audio, wearable protocols, or performance feedback.
- Physical-device comfort calibration or release claims; those belong to
  `FEAT-010`.
- Changing the five-second inhale/five-second exhale protocol or introducing
  mandatory breath holds.

## Affected surfaces

- `app/src/main/java/com/sleepinducer/app/breathing/`.
- `app/src/main/java/com/sleepinducer/app/haptics/`.
- `app/src/test/java/com/sleepinducer/app/breathing/`.
- `app/src/test/java/com/sleepinducer/app/haptics/`.
- `app/src/androidTest/java/com/sleepinducer/app/PhaseHapticCoordinatorFlowTest.kt`.
- `evidence/TICKET-006/`.

## Dependencies and risks

- **Dependencies:** completed `TICKET-004`, completed `TICKET-005`, the
  `BreathingPhase` protocol, and the `HapticAdapter` capability contract.
- **Risks:** duplicate cues from repeated state events, ambiguous phase
  meaning, stale vibration after terminal transitions, and success-shaped
  behavior when haptics are unavailable.
- **Mitigation:** immutable phase-to-cue mapping, phase deduplication,
  explicit delivery results, terminal cancellation, fake adapters, and
  packaged Android boundary tests.

## Acceptance criteria and automated functionality tests

1. **Documented phase mapping:** Given an active inhale or exhale state, the
   coordinator selects a distinct bounded cue whose meaning is documented and
   stable.
   - **Unit test:** `PhaseHapticCoordinatorTest#mapsEachPhaseToDistinctCue`.
   - **Functionality test:** `PhaseHapticCoordinatorFlowTest#mapsInhaleAndExhaleToDistinctCues`
     drives the packaged coordinator with a fake adapter and asserts the
     expected phase/cue pairs.
   - **Expected state:** inhale and exhale each have one deterministic cue
     within the adapter bounds.
2. **Single cue per phase transition:** Given runner state notifications,
   the coordinator delivers the initial inhale and each newly observed phase
   once, while repeated notifications for the same phase do not emit another
   cue.
   - **Unit test:** `PhaseHapticCoordinatorTest#deduplicatesRepeatedPhaseStates`.
   - **Functionality test:** `PhaseHapticCoordinatorFlowTest#deliversOneCuePerPhaseTransition`
     sends repeated packaged active states and asserts exactly one delivery for
     each phase transition.
   - **Expected state:** no duplicate or overlapping cue is created by a
     repeated state callback.
3. **Honest capability boundary:** Given unavailable hardware or a rejected
   cue, the coordinator reports the adapter result and does not claim that a
   phase cue was delivered.
   - **Unit test:** `PhaseHapticCoordinatorTest#reportsUnavailableDelivery`.
   - **Functionality test:** `PhaseHapticCoordinatorFlowTest#reportsUnavailableHapticsWithoutSuccess`
     runs the packaged coordinator against unavailable and rejecting fakes and
     asserts an explicit non-success result with no successful-delivery state.
   - **Expected state:** callers can surface the limitation and the next phase
     remains logically observable without fabricated haptic success.
4. **Terminal cancellation:** Given stop, interruption, or completion, the
   coordinator cancels active haptics exactly once and emits no later cue from
   stale runner notifications.
   - **Unit test:** `PhaseHapticCoordinatorTest#cancelsOnEveryTerminalState`.
   - **Functionality test:** `PhaseHapticCoordinatorFlowTest#cancelsAndRejectsStaleTerminalCues`
     drives all terminal states through the packaged boundary and asserts
     cancellation plus no post-terminal delivery.
   - **Expected state:** no active or future cue survives a terminal session.

## Baseline and evidence

- **Protected baseline:** committed Android foundation, setup boundary,
  breathing protocol, haptic adapter, session runner, unit tests, lint, debug
  APK build, and 19 emulator functionality tests remain passing before
  coordinator changes.
- **Commands:** `./gradlew test --offline`, `./gradlew lintDebug --offline`,
  `./gradlew assembleDebug --offline`, and
  `./gradlew connectedDebugAndroidTest --offline`.
- **Evidence path:** `evidence/TICKET-006/`.
- **Classification:** `automaticValidation` only, using the configured
  `rubber-duck` / `gpt-5.6-luna` / high / `all-validation` profile.

## User-validation plan

Automatic mode requires the agent to execute the same coordinator
functionality charter and record `automaticValidation` without waiting. A
physical-device comfort smoke run remains deferred to `FEAT-010`, because this
ticket does not yet own the foreground service, screen-off lifecycle, or user
controls. When that later boundary exists, validation must record device
model/API, haptic capability, cue meaning, screen state, and any discomfort or
interruption.

## Definition of done

- Inhale and exhale have distinct, documented, bounded cue mappings.
- The coordinator delivers only one cue for each newly observed active phase.
- Unsupported or rejected delivery is explicit and never success-shaped.
- Stop, interruption, and completion cancel active cues and prevent stale
  delivery.
- Unit tests, packaged functionality tests, lint, APK build, review, static
  analysis, and automatic-validation evidence are terminal.
- No service, screen-off, UI, persistence, network, or medical behavior is
  added outside this ticket.
