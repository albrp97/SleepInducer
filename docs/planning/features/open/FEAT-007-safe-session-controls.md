# Feature 007 - Starting and stopping with clear safety boundaries

**Feature ID:** `FEAT-007`
**Status:** confirmed
**Parent phase:** `PHASE-003`
**Parent objective:** `OBJ-001`
**Parent scope:** `SCOPE-001`
**Capabilities:** `CAP-004`, `CAP-005`, `CAP-006`
**Owner:** Android UI and domain integration
**Source paths:** `vision.md`, `docs/specs/project-scope.md`,
`docs/specs/capability-map.md`,
`docs/planning/phases/open/PHASE-003-screen-off-relaxation.md`,
`docs/planning/features/open/FEAT-001-buildable-offline-foundation.md`
**Development mode:** automatic
**Last updated:** 2026-09-08

## Outcome

Give users a low-distraction setup and control flow that makes duration,
haptic limitations, comfort guidance, stop behavior, and local-only operation
clear before the display is turned off.

## Included

- Duration selection and visible start action.
- Visible stop action and terminal-state rendering.
- Safety guidance for comfort, positioning, and symptoms.
- Missing-hardware and service-start failure messaging.
- Minimal local operational state without accounts or network calls.

## Non-goals

- Sleep tracking, health metrics, journaling, personalization based on medical
  data, or claims of guaranteed sleep.
- Hidden data collection or a user flow that requires watching the display.

## Acceptance outcomes

- Given the app is open, the user can choose a supported duration and start or
  stop a session without an account or network.
- Given haptics or service startup are unavailable, the user sees a clear
  limitation rather than a successful active state.
- Given discomfort, the user can stop immediately and sees guidance not to
  continue through symptoms.
- Given a normal session, only minimal operational state is stored locally.

## Dependencies and risks

- **Dependencies:** `FEAT-001`, `FEAT-004`, `FEAT-006`, and reviewed safety copy.
- **Risks:** unclear controls, stale UI state, or users placing the phone in an
  unsafe position.
- **Mitigation:** visible pre-start instructions, prominent stop action,
  explicit terminal states, and no under-pillow placement guidance.

## Evidence plan

- Start/stop/completion functionality tests.
- Missing hardware and service failure functionality tests.
- Offline storage/log review.
- Evidence path: `evidence/FEAT-007/`.

## Traceability

- Requirements: `FR-001`, `FR-003`, `FR-004`, `FR-005`, `FR-007`, `FR-008`.
- Protected behaviors: immediate stop, surfaced failures, privacy, and safety.
- Parent exit gate: `PHASE-003`.
