# Feature 009 - Producing a reviewable offline APK

**Feature ID:** `FEAT-009`
**Status:** confirmed
**Parent phase:** `PHASE-004`
**Parent objective:** `OBJ-001`
**Parent scope:** `SCOPE-001`
**Capabilities:** `CAP-005`, `CAP-006`
**Owner:** Build, static analysis, and review workflow
**Source paths:** `README.md`, `AGENTS.md`,
`docs/specs/project-scope.md`, `docs/specs/capability-map.md`,
`docs/planning/phases/open/PHASE-004-quality-and-apk-delivery.md`
**Development mode:** automatic
**Last updated:** 2026-09-08

## Outcome

Create a reproducible debug APK and reviewable repository state that can be
built from a clean clone without hidden services, sensitive data, or
undocumented commands.

## Included

- Clean-clone build and expected APK path verification.
- Static analysis, structure/churn review, and remediation.
- README and setup command accuracy.
- APK metadata, size, install, and offline smoke checks.

## Non-goals

- Release signing or public distribution before provider and credential
  requirements are resolved.
- Adding telemetry or remote delivery dependencies.

## Acceptance outcomes

- Given a clean checkout and JDK 17/SDK 35, `./gradlew assembleDebug`
  produces the documented APK.
- Given repository review, no introduced safety, privacy, or reliability
  finding remains unresolved without an explicit blocker.
- Given an offline device, the built APK opens and runs the core setup flow.
- Given the README, a new contributor can clone, build, and understand the
  wellness limitations.

## Dependencies and risks

- **Dependencies:** all prior phases, Gradle tooling, static-analysis
  configuration, and clean checkout.
- **Risks:** local-only behavior drifting from documentation or build success
  depending on undeclared state.
- **Mitigation:** repeatable commands, clean-clone execution, and final diff
  review.

## Evidence plan

- `./gradlew assembleDebug`
- Repository-native static analysis and review output.
- APK path and install smoke evidence.
- Evidence path: `evidence/FEAT-009/`.

## Traceability

- Requirements: `FR-001`, `FR-008`.
- Protected behaviors: offline operation, privacy, and honest claims.
- Parent exit gate: `PHASE-004`.
