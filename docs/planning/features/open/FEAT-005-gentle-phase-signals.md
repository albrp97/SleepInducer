# Feature 005 - Signaling phase changes gently and reliably

**Feature ID:** `FEAT-005`
**Status:** confirmed
**Parent phase:** `PHASE-002`
**Parent objective:** `OBJ-001`
**Parent scope:** `SCOPE-001`
**Capabilities:** `CAP-003`, `CAP-004`
**Owner:** Android platform implementation
**Source paths:** `docs/specs/project-scope.md`,
`docs/specs/capability-map.md`,
`docs/planning/phases/open/PHASE-002-guided-breathing-sessions.md`,
`docs/planning/features/open/FEAT-003-capability-aware-haptic-guidance.md`
**Development mode:** automatic
**Last updated:** 2026-09-08

## Outcome

Translate domain phase transitions into short, conservative haptic cues with
stable meaning, capability detection, and reliable cancellation.

## Included

- Phase-to-cue mapping.
- Gentle waveform and amplitude defaults.
- Adapter cancellation when a session becomes terminal.
- Unsupported-hardware reporting at the session boundary.
- Tests that prove no stale cue survives stop or completion.

## Non-goals

- Continuous vibration, audio, visual animation during screen-off operation,
  or wearable-specific protocols.
- Claiming that haptics independently improve sleep onset.

## Acceptance outcomes

- Given an available vibrator, each phase transition produces only its mapped
  short cue.
- Given unsupported or missing haptics, the session exposes a clear
  limitation and does not claim cue delivery.
- Given stop, completion, or interruption, all scheduled and active vibration
  is cancelled.
- Given a cue configuration change, the phase meaning remains documented and
  unambiguous.

## Dependencies and risks

- **Dependencies:** `FEAT-003`, `FEAT-004`, and Android vibration APIs.
- **Risks:** excessive stimulation, amplitude variation, or cancellation races.
- **Mitigation:** conservative defaults, capability checks, fake adapter tests,
  and later physical-device validation.

## Evidence plan

- Unit tests with fake haptic adapter.
- Android adapter tests for capability and cancellation.
- Cue mapping review and raw timing evidence.
- Evidence path: `evidence/FEAT-005/`.

## Traceability

- Requirements: `FR-002`, `FR-003`, `FR-004`, `FR-005`.
- Protected behaviors: gentle cues and no stale vibration.
- Parent exit gate: `PHASE-002`.
