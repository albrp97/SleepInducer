# Feature 004 - Advancing through predictable breathing phases

**Feature ID:** `FEAT-004`
**Status:** confirmed
**Parent phase:** `PHASE-002`
**Parent objective:** `OBJ-001`
**Parent scope:** `SCOPE-001`
**Capabilities:** `CAP-001`, `CAP-004`
**Owner:** Domain implementation
**Source paths:** `docs/specs/project-scope.md`,
`docs/specs/capability-map.md`,
`docs/planning/phases/open/PHASE-002-guided-breathing-sessions.md`,
`docs/planning/features/open/FEAT-002-comfortable-breathing-protocol.md`
**Development mode:** automatic
**Last updated:** 2026-09-08

## Outcome

Run a timed breathing session whose phase transitions, duration, cancellation,
completion, and interruption behavior are deterministic and observable.

## Included

- Session state machine and monotonic scheduling.
- Explicit inhale, exhale, active, stopped, completed, and interrupted states.
- Supported duration handling and terminal transition guards.
- Tests for timing tolerance and cancellation races.

## Non-goals

- Android service lifecycle, notifications, or device-specific haptic output.
- Adaptive breathing based on health sensors or heart-rate data.

## Acceptance outcomes

- Given a selected duration, the session enters active state and advances
  through inhale and exhale phases at the configured cadence.
- Given stop, cancellation, or interruption, no later phase transition occurs.
- Given natural duration completion, completion is emitted once and the session
  cannot restart itself.
- Given timing jitter within the defined tolerance, phase ordering remains
  correct and observable.

## Dependencies and risks

- **Dependencies:** `FEAT-002` protocol contract and monotonic clock.
- **Risks:** race conditions around terminal boundaries and duplicate events.
- **Mitigation:** explicit transition table, idempotent cancellation, and
  deterministic fake-clock tests.

## Evidence plan

- Domain unit tests for every transition.
- Regression tests for stop/completion races and interruption.
- Evidence path: `evidence/FEAT-004/`.

## Traceability

- Requirements: `FR-001`, `FR-002`, `FR-003`, `FR-004`, `FR-006`.
- Protected behaviors: predictable timing and no future work after stop.
- Parent exit gate: `PHASE-002`.

## Planned tickets

- `TICKET-005` - Creating the cancellable breathing session runner
  (`docs/planning/tickets/open/TICKET-005-cancellable-session-runner.md`)
