# Phase 002 - Delivering reliable guided breathing sessions

**Phase ID:** `PHASE-002`
**Status:** complete
**Parent objective:** `OBJ-001`
**Parent scope:** `SCOPE-001`
**Capabilities:** `CAP-001`, `CAP-003`, `CAP-004`
**Owner:** Android implementation
**Source paths:** `docs/specs/project-scope.md`,
`docs/specs/capability-map.md`, `docs/research/sleep-onset-evidence.md`,
`docs/planning/phases/open/PHASE-001-safe-breathing-foundation.md`
**Development mode:** automatic
**Last updated:** 2026-09-10

## Outcome

Deliver the core breathing session behavior as a deterministic, cancellable
experience that gives gentle phase cues and reaches explicit stopped,
completed, or interrupted terminal states.

## Boundaries

Included:

- Breathing state machine and phase scheduling.
- Start, stop, completion, cancellation, and interruption behavior.
- Haptic phase-transition cues and cancellation.
- Unit and domain-level regression coverage.

Deferred:

- Android foreground-service lifecycle and display-off continuity.
- Physical-device comfort tuning and release packaging.

## Entry conditions

- `PHASE-001` has a buildable Android baseline and stable protocol contracts.
- Haptic availability and unsupported-device behavior are explicit.
- Safety copy and no-hold breathing defaults are preserved.

## Exit conditions

- A normal session advances through the configured phases within the defined
  timing tolerance.
- Stop cancels all future cues and reaches a non-success stopped state.
- Natural duration completion cancels future work and reaches completed state.
- Interruption cannot leave a success-shaped active state.
- Every acceptance outcome has focused automated test coverage that can feed an
  Android functionality boundary.

## Dependencies and risks

- **Dependencies:** `CAP-001`, `CAP-003`, and the phase-001 timing/haptic
  contracts.
- **Risks:** timer races, duplicate terminal events, stale callbacks, and
  device-specific vibration behavior.
- **Mitigation:** monotonic timing, idempotent cancellation, explicit state
  transitions, and fake-adapter tests.

## Validation focus

- Unit tests for phase boundaries, duration, stop, completion, and
  interruption.
- Haptic adapter tests for capability detection and cancellation.
- Regression test for no future cue after any terminal transition.

## Evidence and traceability

- Requirements: `FR-001`, `FR-002`, `FR-003`, `FR-004`, `FR-005`, `FR-006`.
- Protected behaviors: immediate stop, no stale haptics, comfortable cadence.
- Evidence path: `evidence/PHASE-002/`.
- Automatic validation must use the configured
  `rubber-duck` / `gpt-5.6-luna` / high / `all-validation` profile.

## Approval and status history

This phase was confirmed through the automatic bootstrap handoff and is now
complete because its planned features and ticket evidence are terminal.

## Planned features

- `FEAT-004` - Advancing through predictable breathing phases
  (`docs/planning/features/closed/FEAT-004-predictable-breathing-phases.md`)
- `FEAT-005` - Signaling phase changes gently and reliably
  (`docs/planning/features/closed/FEAT-005-gentle-phase-signals.md`)
