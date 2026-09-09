# Feature 001 - Establishing the buildable offline foundation

**Feature ID:** `FEAT-001`
**Status:** complete
**Parent phase:** `PHASE-001`
**Parent objective:** `OBJ-001`
**Parent scope:** `SCOPE-001`
**Capabilities:** `CAP-005`, `CAP-006`
**Owner:** Android implementation
**Source paths:** `vision.md`, `AGENTS.md`, `docs/specs/project-scope.md`,
`docs/specs/capability-map.md`, `docs/planning/phases/open/PHASE-001-safe-breathing-foundation.md`
**Development mode:** automatic
**Last updated:** 2026-09-09

## Outcome

Provide a clean, offline Android foundation with a clear app identity, honest
wellness boundary, minimal local-data policy, and a reproducible debug build.

## Included

- Android project and Gradle wrapper using the confirmed JDK 17/SDK 35
  environment.
- Application identity, minimum supported API decision, manifest baseline, and
  offline dependency boundary.
- Initial setup copy that explains the app's purpose, limitations, and stop
  guidance without clinical claims.
- Build and test command documentation.

## Non-goals

- Cloud services, accounts, analytics, advertising, sleep diaries, audio,
  health sensors, or clinical treatment flows.
- Final release signing or remote publication.

## Acceptance outcomes

- Given a clean checkout, the project builds a debug APK with the documented
  repository-native command.
- Given no network connection, the app can open and present the setup boundary.
- Given the user reads the initial guidance, the app identifies itself as a
  wellness aid and explains when to stop.
- Given a repository inspection, no unnecessary network or health-data
  collection path is present.

## Dependencies and risks

- **Dependencies:** Android Gradle tooling, JDK 17, SDK 35, and the approved
  privacy/safety scope.
- **Risks:** accidental network dependency, unsupported API choice, or
  misleading health language.
- **Mitigation:** keep dependencies minimal, document the selected API, and
  test the app offline.

## Evidence plan

- Clean-clone `assembleDebug` output.
- Offline launch functionality test.
- Manifest/dependency inspection and safety-copy assertion.
- Evidence path: `evidence/FEAT-001/`.

## Traceability

- Requirements: `FR-001`, `FR-008`.
- Protected behaviors: offline/local-only operation and wellness-only claims.
- Parent exit gate: `PHASE-001`.
