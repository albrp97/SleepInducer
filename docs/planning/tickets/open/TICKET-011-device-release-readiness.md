# Ticket 011 - Establishing device and release readiness

**Ticket ID:** `TICKET-011`
**Status:** pending
**Parent feature:** `FEAT-010`
**Parent phase:** `PHASE-004`
**Parent objective:** `OBJ-001`
**Parent scope:** `SCOPE-001`
**Capabilities:** `CAP-002`, `CAP-003`, `CAP-004`, `CAP-006`
**Owner:** Device validation and delivery workflow
**Source paths:** `docs/planning/features/open/FEAT-010-device-release-readiness.md`,
`docs/specs/project-scope.md`, `vision.md`
**Development mode:** automatic
**Approval state:** bootstrap-authorized automatic child planning
**Last updated:** 2026-09-14

## Outcome

Document the remaining device, signing, provider, and distribution decisions
without claiming release readiness that cannot be proven locally.

## Scope

- Capture API 35 emulator display-off and notification behavior.
- Capture physical-device haptic comfort and screen-off behavior when a device
  is available.
- Verify APK metadata, path, install, and offline launch.
- Record release signing, foreground-service policy, remote repository, and
  distribution blockers.

## Non-goals

- Publishing a release without signing credentials and provider approval.
- Claiming universal OEM compatibility or clinical effectiveness.

## Acceptance criteria and automated functionality tests

1. The debug APK is reproducible and installs on the supported emulator.
   - **Functionality check:** APK install and `LaunchFlowTest`.
2. Emulator display-off behavior is recorded with API and haptic details.
   - **Functionality test:** `ForegroundSessionServiceFlowTest#continuesPhaseTimingWithTheDisplayOff`.
3. Physical-device and distribution gates are either evidenced or listed as
   exact blockers.
   - **Acceptance check:** `evidence/TICKET-011/`.

## Protected behaviors

- No credentials, private keys, or sensitive device data enter the repository.
- The API 35 `specialUse` service remains an explicit policy gate.
- Debug APK delivery is not represented as a signed production release.

## Verification and evidence

- `./gradlew assembleDebug --offline`
- `./gradlew connectedDebugAndroidTest --offline`
- APK install and offline launch smoke.
- Evidence path: `evidence/TICKET-011/`.
- Automatic validation uses `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.

## Definition of done

- The documented APK artifact is reproducible and locally installable.
- Emulator evidence and screenshots are retained.
- Physical-device, signing, provider, and Play policy gaps are explicit
  blockers rather than implied passes.
