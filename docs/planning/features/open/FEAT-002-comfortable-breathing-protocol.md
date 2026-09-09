# Feature 002 - Defining the comfortable breathing protocol

**Feature ID:** `FEAT-002`
**Status:** confirmed
**Parent phase:** `PHASE-001`
**Parent objective:** `OBJ-001`
**Parent scope:** `SCOPE-001`
**Capabilities:** `CAP-001`, `CAP-006`
**Owner:** Domain implementation
**Source paths:** `docs/specs/project-scope.md`,
`docs/specs/capability-map.md`, `docs/research/sleep-onset-evidence.md`,
`docs/planning/phases/open/PHASE-001-safe-breathing-foundation.md`
**Development mode:** automatic
**Last updated:** 2026-09-08

## Outcome

Define a transparent, comfortable breathing contract that uses approximately
six breaths per minute by default, keeps inhalation and exhalation explicit,
avoids mandatory holds, and supports a small set of user-selectable durations.

## Included

- Typed phase and session model.
- Default five-second inhale and five-second exhale cadence.
- Ten-minute default, shorter option, and twenty-minute option.
- Natural-breathing and stop-if-uncomfortable guidance.
- Deterministic timing and terminal-state contract for later Android wiring.

## Non-goals

- Breath-hold training, forced maximal breaths, hyperventilation, or a
  clinical prescription.
- Adaptive medical personalization or heart-rate feedback.

## Acceptance outcomes

- Given a supported duration, the protocol produces a stable sequence of
  inhale and exhale phases at the configured cadence.
- Given a session reaches its duration, the protocol reaches completion exactly
  once.
- Given cancellation or interruption, the protocol stops future transitions and
  does not report successful completion.
- Given user discomfort, the product guidance makes stopping immediate and does
  not encourage pushing through symptoms.

## Dependencies and risks

- **Dependencies:** monotonic timing source and the research baseline.
- **Risks:** timer drift, ambiguous phase ownership, or wording that pressures
  users to breathe unnaturally.
- **Mitigation:** pure domain tests, explicit phase semantics, and no-hold
  defaults.

## Evidence plan

- Protocol/state-machine unit tests.
- Timing tolerance and terminal-state regression tests.
- Safety-copy functionality assertion.
- Evidence path: `evidence/FEAT-002/`.

## Traceability

- Requirements: `FR-001`, `FR-002`, `FR-004`, `FR-007`.
- Protected behaviors: comfortable natural breathing and no mandatory holds.
- Parent exit gate: `PHASE-001`.
