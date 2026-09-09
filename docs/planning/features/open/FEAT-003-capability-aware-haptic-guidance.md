# Feature 003 - Making haptic guidance capability-aware

**Feature ID:** `FEAT-003`
**Status:** confirmed
**Parent phase:** `PHASE-001`
**Parent objective:** `OBJ-001`
**Parent scope:** `SCOPE-001`
**Capabilities:** `CAP-003`, `CAP-005`
**Owner:** Android platform implementation
**Source paths:** `docs/specs/project-scope.md`,
`docs/specs/capability-map.md`, `docs/research/sleep-onset-evidence.md`,
`docs/planning/phases/open/PHASE-001-safe-breathing-foundation.md`
**Development mode:** automatic
**Last updated:** 2026-09-08

## Outcome

Define and expose a conservative haptic boundary that can deliver distinct
phase-transition cues when supported and reports unavailable or limited
hardware without pretending that cues were delivered.

## Included

- Android haptic adapter contract.
- Capability and amplitude support detection.
- Short, gentle phase-transition cue direction.
- Cancellation and no-stale-vibration behavior.
- User-visible unsupported-hardware state.

## Non-goals

- Continuous vibration, strong stimulation, audio cues, or wearable-specific
  integrations.
- Final comfort calibration across every OEM device.

## Acceptance outcomes

- Given a usable vibrator, the adapter emits the configured phase cue and can
  cancel it.
- Given no usable vibrator or unsupported amplitude control, the adapter
  reports the limitation clearly and does not claim successful delivery.
- Given a stop or terminal session state, no future vibration remains active.
- Given a physical-device validation run, cue intensity and phase meaning are
  documented without adding performance pressure.

## Dependencies and risks

- **Dependencies:** Android vibration APIs, `android.permission.VIBRATE`, and
  selected target API.
- **Risks:** OEM amplitude differences, unavailable amplitude control, and
  haptics that increase arousal.
- **Mitigation:** capability detection, conservative defaults, cancellation
  tests, and physical-device validation.

## Evidence plan

- Fake-adapter unit tests.
- Emulator and physical-device capability checks.
- Stop/cancellation regression test.
- Evidence path: `evidence/FEAT-003/`.

## Traceability

- Requirements: `FR-002`, `FR-003`, `FR-005`.
- Protected behaviors: gentle cues and honest unsupported-device behavior.
- Parent exit gate: `PHASE-001`.
