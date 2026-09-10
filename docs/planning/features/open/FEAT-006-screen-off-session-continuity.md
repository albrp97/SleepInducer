# Feature 006 - Continuing sessions with the display off

**Feature ID:** `FEAT-006`
**Status:** confirmed
**Parent phase:** `PHASE-003`
**Parent objective:** `OBJ-001`
**Parent scope:** `SCOPE-001`
**Capabilities:** `CAP-002`, `CAP-004`
**Owner:** Android implementation
**Source paths:** `docs/specs/project-scope.md`,
`docs/specs/capability-map.md`,
`docs/planning/phases/open/PHASE-003-screen-off-relaxation.md`,
`docs/planning/features/open/FEAT-004-predictable-breathing-phases.md`
**Development mode:** automatic
**Last updated:** 2026-09-08

## Outcome

Keep a user-started breathing session alive and predictable after the display
is turned off, using a user-visible Android service and explicit interruption
state.

## Included

- Foreground-service ownership of active session work.
- Start from visible UI before display-off operation.
- Notification and service lifecycle contract.
- Display-off continuity and interruption reporting.
- Stop and completion teardown.

## Non-goals

- Background auto-start, hidden execution, or indefinite service operation.
- Support for every OEM battery policy without documented device evidence.

## Acceptance outcomes

- Given a user starts a supported session while the app is visible, the active
  session continues when the display turns off.
- Given the service is stopped by the user or operating system, future cues are
  cancelled and the UI does not show success.
- Given natural completion, the service stops and the completion state is
  observable after the display is restored.
- Given the configured Android policy disallows the chosen service approach,
  the project records a blocker instead of shipping a misleading fallback.

## Dependencies and risks

- **Dependencies:** `FEAT-004`, `FEAT-005`, target API service policy,
  notification behavior, emulator or physical device.
- **Risks:** process death, Doze, OEM battery limits, lock-screen behavior, and
  foreground-service type restrictions.
- **Mitigation:** start from visible UI, test interruption paths, and keep
  service type decision explicit.

## Evidence plan

- Android functionality test with display off.
- Service start/stop/completion/interruption logs.
- Emulator and physical-device timing evidence.
- Evidence path: `evidence/FEAT-006/`.

## Traceability

- Requirements: `FR-002`, `FR-003`, `FR-004`, `FR-006`.
- Protected behaviors: screen-off continuity and no success-shaped interruption.
- Parent exit gate: `PHASE-003`.

## Planned tickets

- `TICKET-007` - Establishing the foreground session service
  (`docs/planning/tickets/closed/TICKET-007-foreground-session-service.md`)
