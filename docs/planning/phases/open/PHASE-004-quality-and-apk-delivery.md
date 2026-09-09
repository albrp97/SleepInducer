# Phase 004 - Proving and packaging the offline app

**Phase ID:** `PHASE-004`
**Status:** confirmed
**Parent objective:** `OBJ-001`
**Parent scope:** `SCOPE-001`
**Capabilities:** `CAP-001`, `CAP-002`, `CAP-003`, `CAP-004`, `CAP-005`,
`CAP-006`
**Owner:** Quality, release, and delivery workflow
**Source paths:** `vision.md`, `docs/specs/project-scope.md`,
`docs/specs/capability-map.md`, `docs/planning/phases/open/PHASE-003-screen-off-relaxation.md`
**Development mode:** automatic
**Last updated:** 2026-09-08

## Outcome

Produce a reproducible, reviewable APK candidate whose core breathing behavior,
screen-off haptics, safety boundary, privacy behavior, and device limitations
are supported by terminal evidence.

## Boundaries

Included:

- Full unit and Android functionality test execution.
- Static analysis, structure/churn review, and remediation.
- Clean-clone build and APK artifact verification.
- Emulator and physical-device evidence with display-off conditions.
- Release documentation and configured local/provider delivery gates.

Deferred:

- Clinical efficacy claims or clinical validation.
- Accounts, cloud sync, analytics, sleep tracking, audio, or sensor
  integrations.

## Entry conditions

- `PHASE-003` user-facing behavior is implemented and testable.
- All known external decisions required for the target APK are resolved or
  explicitly blocked.
- The repository-native build, test, and functionality commands are known.

## Exit conditions

- The debug APK builds from a clean clone at the expected path.
- Unit tests and automated functionality tests are terminal and recorded.
- Static analysis and final review contain no unresolved introduced findings.
- Display-off behavior is documented for the supported emulator and at least
  one physical device.
- Safety, privacy, evidence limitations, and known device constraints are
  reflected in repository documentation.
- Configured delivery gates are satisfied, or an exact external blocker is
  recorded without claiming completion.

## Dependencies and risks

- **Dependencies:** all prior phases, Android SDK/JDK toolchain, emulator or
  device, and configured validator capability.
- **Risks:** clean-clone drift, OEM-specific failures, unavailable runtime
  validator, signing or remote-provider restrictions.
- **Mitigation:** run commands from a clean checkout, preserve raw evidence,
  separate deterministic results from validation classification, and stop at
  external blockers.

## Validation focus

- `./gradlew test`
- `./gradlew connectedDebugAndroidTest`
- `./gradlew assembleDebug`
- Configured static-analysis and review workflow.
- Automatic validation using the exact configured profile.

## Evidence and traceability

- Requirements: all `FR-001` through `FR-008`.
- Protected behaviors: all protected behavior statements in `SCOPE-001`.
- Evidence path: `evidence/PHASE-004/`.
- Automatic validation must use the configured
  `rubber-duck` / `gpt-5.6-luna` / high / `all-validation` profile.

## Approval and status history

This phase is confirmed through the automatic bootstrap handoff. It remains in
the configured `open` directory until delivery policy and evidence are
terminal.
