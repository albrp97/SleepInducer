# Feature 008 - Proving the complete user-facing session

**Feature ID:** `FEAT-008`
**Status:** confirmed
**Parent phase:** `PHASE-004`
**Parent objective:** `OBJ-001`
**Parent scope:** `SCOPE-001`
**Capabilities:** `CAP-001`, `CAP-002`, `CAP-003`, `CAP-004`, `CAP-005`,
`CAP-006`
**Owner:** Quality and functionality validation
**Source paths:** `docs/specs/project-scope.md`,
`docs/specs/capability-map.md`,
`docs/planning/phases/open/PHASE-004-quality-and-apk-delivery.md`,
`docs/planning/features/open/FEAT-006-screen-off-session-continuity.md`,
`docs/planning/features/open/FEAT-007-safe-session-controls.md`
**Development mode:** automatic
**Last updated:** 2026-09-08

## Outcome

Demonstrate through executable tests that a user can configure, start, follow,
stop, complete, and recover a breathing session across the supported Android
application boundary.

## Included

- Unit and regression coverage for domain and adapters.
- Android functionality tests for start, screen-off continuity, stop,
  completion, missing hardware, service interruption, and offline operation.
- Evidence records separating raw command output from automatic classification.

## Non-goals

- Clinical efficacy measurement or replacement of controlled sleep research.
- User validation recorded as human confirmation in automatic mode.

## Acceptance outcomes

- Every scope requirement `FR-001` through `FR-008` has an executable
  automated functionality test or an explicit external blocker.
- Tests observe both visible behavior and relevant local/service state.
- Terminal states prove that future haptic work is cancelled.
- Test evidence records API level, screen state, haptic capability, and
  interruption conditions.

## Dependencies and risks

- **Dependencies:** all user-facing features and available emulator/device
  infrastructure.
- **Risks:** tests that cover only units, flaky screen-off behavior, or
  success-shaped results after interruption.
- **Mitigation:** functionality tests cross the app boundary and terminal
  states are asserted explicitly.

## Evidence plan

- `./gradlew test`
- `./gradlew connectedDebugAndroidTest`
- Evidence path: `evidence/FEAT-008/`.

## Planned tickets

- `TICKET-010` - Proving the complete application flow
  (`docs/planning/tickets/open/TICKET-010-complete-functionality-evidence.md`)

## Traceability

- Requirements: all `FR-001` through `FR-008`.
- Protected behaviors: all scope-protected behavior statements.
- Parent exit gate: `PHASE-004`.
