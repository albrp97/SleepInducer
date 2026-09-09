# Phase 001 - Establishing the safe breathing foundation

**Phase ID:** `PHASE-001`
**Status:** confirmed
**Parent objective:** `OBJ-001`
**Parent scope:** `SCOPE-001`
**Capabilities:** `CAP-001`, `CAP-003`, `CAP-005`, `CAP-006`
**Owner:** Delivery planning and Android implementation
**Source paths:** `vision.md`, `docs/specs/project-scope.md`,
`docs/specs/capability-map.md`, `docs/research/sleep-onset-evidence.md`,
`docs/planning/repo-map.md`
**Development mode:** automatic
**Last updated:** 2026-09-08

## Outcome

Establish a buildable Android foundation and explicit, evidence-backed
contracts for breathing timing, gentle haptics, safety language, local-only
operation, and the unresolved platform choices that could otherwise block
screen-off delivery.

## Boundaries

Included:

- Android project identity, minimum API decision, and clean debug build
  baseline.
- Pure breathing-session contract and supported duration model.
- Haptic capability abstraction and conservative cue contract.
- Safety, privacy, and wellness wording required by the approved scope.
- Foreground-service feasibility decision and required Android permissions.

Deferred:

- Full screen-off session orchestration.
- Final emulator and physical-device functionality evidence.
- Release signing, remote publication, and APK release packaging.

## Entry conditions

- `OBJ-001`, `SCOPE-001`, `MAP-001`, and `CAPMAP-001` are confirmed.
- The research baseline and breathing-only boundary are available.
- JDK 17 and Android SDK 35 are available in the target environment.

## Exit conditions

- The project builds from a clean checkout with a repository-native Gradle
  command.
- The breathing protocol, state model, duration options, and timing tolerance
  are documented and covered by focused tests.
- Haptic delivery is isolated behind a capability-aware boundary with explicit
  unavailable-hardware behavior.
- Safety and privacy copy is present without clinical treatment claims.
- Minimum API, application ID, haptic waveform direction, and foreground
  service policy are recorded with evidence or an explicit blocker.

## Dependencies and risks

- **Dependencies:** Android Gradle tooling, target API behavior, vibration API
  availability, and the approved research baseline.
- **Risks:** choosing an unsupported service type, over-constraining breathing,
  or allowing missing haptics to appear successful.
- **Mitigation:** keep domain code pure, detect platform capabilities, preserve
  no-hold defaults, and stop on unresolved policy conflicts.

## Validation focus

- Unit tests for protocol and haptic contracts.
- Clean-clone debug build.
- Manifest and dependency inspection.
- Static review of safety, privacy, and offline boundaries.

## Evidence and traceability

- Requirements: `FR-001`, `FR-002`, `FR-005`, `FR-007`, `FR-008`.
- Protected behaviors: comfortable breathing, no mandatory holds, honest
  wellness claims, local-only state.
- Evidence path: `evidence/PHASE-001/`.
- Automatic validation must use the configured
  `rubber-duck` / `gpt-5.6-luna` / high / `all-validation` profile.

## Approval and status history

This phase is confirmed through the automatic bootstrap handoff. It remains in
the configured `open` directory until all exit evidence and delivery gates are
terminal.
