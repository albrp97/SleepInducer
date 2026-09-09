# Feature 010 - Validating device behavior and release readiness

**Feature ID:** `FEAT-010`
**Status:** confirmed
**Parent phase:** `PHASE-004`
**Parent objective:** `OBJ-001`
**Parent scope:** `SCOPE-001`
**Capabilities:** `CAP-002`, `CAP-003`, `CAP-004`, `CAP-006`
**Owner:** Device validation and delivery workflow
**Source paths:** `docs/specs/project-scope.md`,
`docs/specs/capability-map.md`,
`docs/research/sleep-onset-evidence.md`,
`docs/planning/phases/open/PHASE-004-quality-and-apk-delivery.md`
**Development mode:** automatic
**Last updated:** 2026-09-08

## Outcome

Establish the supported device behavior and release decision for screen-off
haptics, service interruption, safety messaging, signing, and configured
delivery operations.

## Included

- Emulator and physical-device display-off validation.
- Haptic intensity, phase meaning, interruption, and battery-policy notes.
- Release signing, license, provider, branch, and channel decision records.
- Final evidence, review, and delivery-gate status.

## Non-goals

- Claiming universal OEM compatibility or clinical effectiveness.
- Publishing credentials, private keys, or sensitive device data.

## Acceptance outcomes

- Given the supported emulator and at least one physical device, screen-off
  cues and terminal states are documented with API and hardware details.
- Given a service or haptic limitation, the release notes and UI describe the
  limitation without a success-shaped claim.
- Given delivery configuration, release signing and provider requirements are
  either satisfied or recorded as exact blockers.
- Given the final diff, review evidence is terminal or identifies the precise
  unresolved external gate.

## Dependencies and risks

- **Dependencies:** `FEAT-008`, `FEAT-009`, physical device, release policy,
  and the automatic validator capability.
- **Risks:** OEM power management, physical haptic discomfort, missing signing
  credentials, or unavailable remote checks.
- **Mitigation:** preserve raw device evidence, stop on provider/credential
  blockers, and never claim delivery without terminal gates.

## Evidence plan

- Emulator functionality output.
- Physical-device display-off evidence.
- Review, static-analysis, and delivery-gate records.
- Evidence path: `evidence/FEAT-010/`.

## Traceability

- Requirements: `FR-002`, `FR-003`, `FR-004`, `FR-005`, `FR-006`, `FR-007`.
- Protected behaviors: screen-off reliability, gentle haptics, stop, and
  honest safety boundaries.
- Parent exit gate: `PHASE-004`.
