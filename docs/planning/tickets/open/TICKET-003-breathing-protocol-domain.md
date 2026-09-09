# Ticket 003 - Implementing the breathing protocol domain

**Ticket ID:** `TICKET-003`
**Status:** verifying
**Parent feature:** `FEAT-002`
**Parent phase:** `PHASE-001`
**Parent objective:** `OBJ-001`
**Parent scope:** `SCOPE-001`
**Capabilities:** `CAP-001`, `CAP-006`
**Owner:** Domain implementation
**Source paths:** `vision.md`, `docs/specs/project-scope.md`,
`docs/specs/capability-map.md`,
`docs/research/sleep-onset-evidence.md`,
`docs/planning/features/open/FEAT-002-comfortable-breathing-protocol.md`
**Last updated:** 2026-09-09

## Outcome

Create a pure Kotlin breathing protocol boundary that produces predictable
inhale and exhale phases for supported durations, completes exactly once, and
stops without future transitions when cancelled or interrupted.

## Scope

- Typed phase, duration, and session-state models.
- Five-second inhale and five-second exhale defaults with no mandatory hold.
- Ten-minute default, shorter supported option, and twenty-minute option.
- Monotonic-time transition calculation independent of Android classes.
- Explicit stopped, interrupted, and completed terminal states.
- Unit tests and an Android instrumentation acceptance flow for the public
  protocol boundary.

## Non-goals

- Compose setup or session controls.
- Vibration hardware, waveform selection, or foreground services.
- Persistence, health data, heart-rate feedback, or adaptive medical
  personalization.
- Breath-hold training, forced breath depth, hyperventilation, or performance
  scoring.

## Affected surfaces

- `app/src/main/java/com/sleepinducer/app/breathing/`.
- `app/src/test/java/com/sleepinducer/app/breathing/`.
- `app/src/androidTest/java/com/sleepinducer/app/ProtocolFlowTest.kt`.
- Gradle test and Android functionality evidence.

## Dependencies and risks

- **Dependencies:** completed `TICKET-001` and `TICKET-002`, monotonic clock
  contract, and the research-informed defaults in `SCOPE-001`.
- **Risks:** timer drift, ambiguous phase ownership, duplicate completion, or
  accidental introduction of holds.
- **Mitigation:** keep the domain pure, derive phase from elapsed monotonic
  time, define terminal transitions explicitly, and assert every boundary.

## Acceptance criteria and automated functionality tests

1. **Stable phase cadence:** Given a supported duration and elapsed monotonic
   time, the protocol returns alternating five-second inhale and exhale phases
   without hidden hold phases.
   - **Unit test:** `BreathingProtocolTest#alternatesFiveSecondPhases`.
   - **Functionality test:** `ProtocolFlowTest#exposesTheConfiguredPhaseContract`
     installs the APK, invokes the public protocol contract at representative
     elapsed times, and asserts the visible phase sequence returned by the
     application boundary.
   - **Expected state:** inhale at the start, exhale after five seconds, and
     the pattern repeats at ten-second cycle boundaries.
2. **Exact completion:** Given a supported duration reaches its terminal
   elapsed time, the protocol reports `Completed` exactly once and emits no
   later breathing phase.
   - **Unit test:** `BreathingProtocolTest#completesExactlyOnceAtDuration`.
   - **Functionality test:** `ProtocolFlowTest#reportsOneTerminalCompletion`
     drives the public protocol boundary through the terminal timestamp and
     asserts one completion result with no subsequent phase.
   - **Expected state:** completion is terminal and not duplicated.
3. **Safe cancellation and interruption:** Given stop or interruption occurs,
   the protocol reports the corresponding terminal state and never reports
   successful completion afterward.
   - **Unit test:** `BreathingProtocolTest#stopsAndInterruptsWithoutCompletion`.
   - **Functionality test:** `ProtocolFlowTest#stopsFutureProtocolTransitions`
     exercises the public stop/interruption boundary and asserts no later
     phase or success result is produced.
   - **Expected state:** stopped and interrupted sessions remain non-success
     terminal states.
4. **Comfort contract:** Given the default protocol is created, it contains no
   mandatory hold or forced-depth instruction.
   - **Unit test:** `BreathingProtocolTest#usesNaturalBreathingWithoutHold`.
   - **Functionality test:** `ProtocolFlowTest#exposesTheNoHoldDefault`
     reads the public protocol contract and asserts the default has only inhale
     and exhale phases and carries the natural-breathing guidance.
   - **Expected state:** users are never required to hold or maximize a breath.

## Baseline and evidence

- **Protected baseline:** Android build, setup boundary, and five emulator
  functionality tests pass before domain implementation.
- **Commands:** `./gradlew test --offline`, `./gradlew lintDebug --offline`,
  `./gradlew assembleDebug --offline`, and
  `./gradlew connectedDebugAndroidTest --offline`.
- **Evidence path:** `evidence/TICKET-003/`.
- **Classification:** `automaticValidation` only, using the configured
  `rubber-duck` / `gpt-5.6-luna` / high / `all-validation` profile.

## User-validation plan

Automatic mode requires the agent to execute the same protocol functionality
charter and record `automaticValidation` without waiting. An optional human
smoke check is to open the debug app, inspect the protocol preview when it is
exposed by the implementation, and confirm the first phase is inhale, the
second phase is exhale, and no hold instruction appears.

## Definition of done

- Pure domain models and timing logic are independent of Android classes.
- Supported durations and five-second inhale/exhale cadence are covered by
  unit tests.
- Stop, interruption, and exactly-once completion are covered by unit and
  application-boundary functionality tests.
- No mandatory holds, forced depth, or medical personalization are encoded.
- Build, lint, unit, functionality, automatic-validation, and review evidence
  is appended under `evidence/TICKET-003/`.
