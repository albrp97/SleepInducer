# Phase 003 - Delivering screen-off relaxation use

**Phase ID:** `PHASE-003`
**Status:** confirmed
**Parent objective:** `OBJ-001`
**Parent scope:** `SCOPE-001`
**Capabilities:** `CAP-002`, `CAP-004`, `CAP-005`, `CAP-006`
**Owner:** Android implementation and functionality validation
**Source paths:** `docs/specs/project-scope.md`,
`docs/specs/capability-map.md`, `docs/research/sleep-onset-evidence.md`,
`docs/planning/phases/open/PHASE-002-guided-breathing-sessions.md`
**Development mode:** automatic
**Last updated:** 2026-09-08

## Outcome

Allow a user to configure and start a session while the app is visible, turn
off the display, and follow gentle haptic cues until completion or immediate
stop without network access or unnecessary data collection.

## Boundaries

Included:

- Foreground-service session ownership.
- Notification and visible start/stop controls.
- Display-off continuity and service interruption handling.
- Local operational state and explicit failure reporting.
- Safety guidance for positioning, discomfort, and stopping.

Deferred:

- Release-channel publication and signing.
- Broader sleep features such as sleep diaries, audio, health sensors, or
  clinical treatment workflows.

## Entry conditions

- `PHASE-002` terminal state behavior is stable.
- Foreground-service type, required permissions, and target API policy are
  resolved or recorded as an external blocker.
- The app can run offline with the core session boundary.

## Exit conditions

- The visible start flow launches a user-visible session without an account or
  network connection.
- The session continues with the display off on the supported emulator/device
  matrix.
- Stop and completion release the service and prevent future cues.
- System interruption is surfaced when possible and never reported as success.
- Missing haptic hardware is clearly communicated.
- Automated functionality tests cover start, screen-off continuity, stop,
  completion, missing hardware, and interruption.

## Dependencies and risks

- **Dependencies:** Android service policy, notification permission behavior,
  OEM power management, and a connected emulator or physical device.
- **Risks:** process death, Doze restrictions, notification/task-manager stop,
  lock-screen behavior, or foreground-service policy changes.
- **Mitigation:** start from visible UI, use a user-visible service, document
  unsupported environments, and test interruption paths.

## Validation focus

- Android functionality tests crossing the application boundary.
- Emulator screen-off session test.
- Physical-device screen-off haptic test.
- Offline run and local-state inspection.

## Evidence and traceability

- Requirements: `FR-001`, `FR-002`, `FR-003`, `FR-004`, `FR-005`, `FR-006`,
  `FR-007`, `FR-008`.
- Protected behaviors: screen-off operation, immediate stop, honest failures,
  no unnecessary data transmission.
- Evidence path: `evidence/PHASE-003/`.
- Automatic validation must use the configured
  `rubber-duck` / `gpt-5.6-luna` / high / `all-validation` profile.

## Approval and status history

This phase is confirmed through the automatic bootstrap handoff. It remains in
the configured `open` directory until device and functionality evidence are
terminal.
