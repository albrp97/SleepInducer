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
**Last updated:** 2026-09-24

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
4. Given a GitHub APK release, it should contain a signed APK that passes
   signature verification and installs and launches on the supported API 35
   emulator.
   - **Functionality test:** the release workflow verifies the signature,
     installs the release APK, and launches `com.sleepinducer.app`.
   - **Release check:** `v0.1.1` contains `sleep-inducer-release.apk` with
     version metadata matching the tag and no unsigned asset. The existing
     unsigned `v0.1.0` release remains unchanged and is documented as
     superseded.

## Protected behaviors

- No credentials, private keys, or sensitive device data enter the repository.
- The API 35 `specialUse` service remains an explicit policy gate.
- Debug APK delivery is not represented as a signed production release.

## Verification and evidence

- `./gradlew assembleDebug --offline`
- `./gradlew test lintDebug assembleRelease --offline` with release-signing
  environment values configured.
- `./gradlew connectedDebugAndroidTest --offline`
- Release APK signature verification, API 35 emulator install, and offline
  launch smoke.
- Evidence path: `evidence/TICKET-011/`.
- Automatic validation uses `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.

## Definition of done

- The documented APK artifact is reproducible and locally installable.
- Emulator evidence and screenshots are retained.
- GitHub's `v0.1.1` release contains the signed `sleep-inducer-release.apk`,
  and the older unsigned `v0.1.0` release is documented as superseded.
- Physical-device, signing, provider, and Play policy gaps are explicit
  blockers rather than implied passes.

## Current release-signing status

- A dedicated PKCS#12 release key has been generated and stored outside the
  repository with owner-only file permissions.
- The release build requires signing inputs and fails closed when any are
  missing. The GitHub Actions workflow verifies the signature, installs and
  launches the APK on API 35, and verifies the pinned signing certificate.
  The signed `v0.1.1` release will be published without moving the existing
  `v0.1.0` tag.
- GitHub Actions successfully built and signature-verified the `v0.1.1` APK
  using the configured secrets. The first hosted API 35 launch assertion
  failed after installation. The corrected launch check passed locally; the
  hosted rerun and release publication remain pending. The existing public
  `v0.1.0` asset remains unsigned.
