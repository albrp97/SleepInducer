# Ticket 010 - Proving the complete application flow

**Ticket ID:** `TICKET-010`
**Status:** pending
**Parent feature:** `FEAT-008`
**Parent phase:** `PHASE-004`
**Parent objective:** `OBJ-001`
**Parent scope:** `SCOPE-001`
**Capabilities:** `CAP-001`, `CAP-002`, `CAP-003`, `CAP-004`, `CAP-005`,
`CAP-006`
**Owner:** Quality and functionality validation
**Source paths:** `docs/planning/features/open/FEAT-008-complete-session-evidence.md`,
`docs/specs/project-scope.md`
**Development mode:** automatic
**Approval state:** bootstrap-authorized automatic child planning
**Last updated:** 2026-09-14

## Outcome

Make every first-release acceptance outcome executable through the Android
test boundary and record reproducible evidence for the service, UI, haptics,
privacy, and safety behavior.

## Scope

- Package-level engine completion and cue-cancellation coverage.
- Foreground-service active phase, stop, interruption, and display-off coverage.
- Compose duration, start, stop, and terminal-state coverage.
- Manifest and offline permission assertions.
- Evidence records for commands, emulator API, display state, and haptic
  capability.

## Non-goals

- Clinical efficacy measurement.
- Physical-device comfort claims before a real device is available.
- Treating unit tests or source inspection as functionality evidence.

## Acceptance criteria and automated functionality tests

1. `FR-001` through `FR-008` each map to an executable test or an explicit
   external blocker.
   - **Functionality tests:** `SessionControlsFlowTest`,
     `ForegroundSessionServiceFlowTest`, `SessionEngineFlowTest`,
     `SessionRunnerFlowTest`, and `LaunchFlowTest`.
2. Completion and terminal cancellation are asserted through a packaged test
   boundary.
   - **Functionality test:** `SessionEngineFlowTest#completesAndCancelsFutureCuesThroughPackagedEngine`.
3. Display-off continuity is asserted on the supported API 35 emulator.
   - **Functionality test:** `ForegroundSessionServiceFlowTest#continuesPhaseTimingWithTheDisplayOff`.
4. Results and remaining gaps are recorded append-only in evidence artifacts.
   - **Acceptance check:** evidence records under `evidence/TICKET-010/`.

## Protected behaviors

- No required network, account, analytics, health data, or audio path is
  introduced.
- Unavailable haptics and service interruption remain explicit failures.
- Automatic validation uses only the configured Rubber Duck profile.

## Verification and evidence

- `./gradlew test --offline`
- `./gradlew lintDebug --offline`
- `./gradlew assembleDebug --offline`
- `./gradlew connectedDebugAndroidTest --offline`
- Evidence path: `evidence/TICKET-010/`.
- Automatic validation uses `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.

## Definition of done

- All applicable local technical checks and packaged functionality tests are
  terminal.
- Static-analysis and review findings are recorded and remediated or blocked
  explicitly.
- Evidence identifies the exact remaining physical-device and distribution
  gates.
