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
   - **Release check:** `v0.1.4` contains `sleep-inducer-release.apk` with
     version name `0.1.4`, version code `5`, and no unsigned asset. Existing
     `v0.1.0`, `v0.1.1`, `v0.1.2`, and `v0.1.3` tags remain unchanged.

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
- GitHub's `v0.1.4` release contains the signed `sleep-inducer-release.apk`,
  and the older unsigned `v0.1.0` release is documented as superseded.
- Physical-device, signing, provider, and Play policy gaps are explicit
  blockers rather than implied passes.

## Current release-signing status

- A dedicated PKCS#12 release key has been generated and stored outside the
  repository with owner-only file permissions.
- The release build requires signing inputs and fails closed when any are
  missing. The GitHub Actions workflow verifies the signature, installs and
  launches the APK on API 35, and verifies the pinned signing certificate.
- GitHub Actions successfully built and signature-verified the `v0.1.1` APK
  using the configured secrets, but its first hosted API 35 launch assertion
  failed after installation. The corrected launch check passed locally. A
  same-tag workflow dispatch was denied for lack of repository admin rights,
  so the `v0.1.1` tag remains unchanged and was not released. Change-control
  record `CHG-001` authorized `v0.1.2`, but that immutable tag also failed
  before publication and has no GitHub release asset. The public `v0.1.0`
  asset remains unsigned. Local v0.1.2 build, functionality,
  signature, and version checks passed, but tag-triggered hosted run
  `35996308723` failed after installation because `am start -W` returned an
  empty response and the workflow treated the missing `Status: ok` line as
  fatal before checking the process or resumed activity. No v0.1.2 GitHub
  release or APK asset was published. The corrected launch check now passes
  its mocked failure/success cases and a local API 35 emulator run; commit
  `4a0634d` is pushed to `ticket/safe-session-controls`. The immutable
  v0.1.2 tag cannot use the corrected workflow. Change-control record
  `CHG-002` now authorizes v0.1.3/version code 4 as the next release target.
  The v0.1.3 signed build, package/version/signature checks, 42 API 35
  instrumentation tests, and exact local release-workflow install/launch
  checks passed. The APK is copied to `/home/ghiki/Downloads/`. Its tag,
  hosted workflow, and GitHub release asset are still pending. The first tag
  push attempt failed because SSH authentication was rejected, but an
  immediate retry published the annotated tag without force. GitHub confirms
  `v0.1.3` points to the approved commit. Hosted run `36015108549` built,
  signature-checked, and installed the APK, but failed before launch because
  the emulator action executes each line of its multiline script separately.
  The publication step was skipped and no release asset exists. The worktree
  now uses a one-command Bash script for the launch check, and mocked
  regression cases pass. The user approved CHG-003 for signed `v0.1.4` with
  version code `5`. Hosted run `36039522821` passed build, signature,
  metadata, and API 35 install/launch checks, then published the single
  signed APK asset. The downloaded GitHub asset is at
  `/home/ghiki/Downloads/sleep-inducer-release.apk` and matches the published
  SHA-256. Existing tags, including failed v0.1.3, remain unchanged.

## Change control

### CHG-001 - Moving the release target to v0.1.2

- **Status:** approved; the v0.1.2 target failed before publication and is
  superseded by CHG-002.
- **Approval:** the user selected `authorize-v012` after the same-tag dispatch
  was rejected with HTTP 403.
- **Previous target:** signed `v0.1.1`; its immutable tag points to a commit
  whose hosted API 35 launch assertion failed.
- **Approved target:** signed `v0.1.2`, with APK version name `0.1.2` and
  version code `3`.
- **Rationale:** retrying `v0.1.1` requires an administrator-authorized
  workflow dispatch. A new patch tag can run the corrected workflow without
  moving or rewriting existing tags.
- **Affected artifacts:** this ticket's release acceptance and status text,
  `README.md`, `app/build.gradle.kts`,
  `.github/workflows/release-apk.yml` (including an exact version-code
  assertion), and appended evidence in `evidence/TICKET-011/record.md`.
- **Unaffected ancestry and scope:** `OBJ-001`, `SCOPE-001`, `PHASE-004`,
  `FEAT-010`, breathing behavior, safety boundaries, and ticket status.
- **Non-goals:** moving `v0.1.0` or `v0.1.1`, changing app behavior, or
  claiming a release before the hosted checks pass.
- **Evidence:** EVID-021 and EVID-027 document the hosted failure and
  dispatch blocker; EVID-028 records this approval.

### CHG-002 - Moving the release target to v0.1.3

- **Status:** approved; delivery evidence pending.
- **Approval:** the user selected `authorize-v0.1.3` after the hosted
  v0.1.2 launch check failed and the tag could not be rerun with the corrected
  workflow.
- **Previous target:** immutable `v0.1.2`, version name `0.1.2`, version code
  `3`; hosted run `35996308723` failed before publication and no release
  asset exists.
- **Approved target:** signed `v0.1.3`, version name `0.1.3`, version code
  `4`.
- **Rationale:** the corrected API 35 launch assertion is committed and
  pushed as `4a0634d`. Existing tags are immutable and must remain unchanged,
  so the corrected workflow needs a new version tag.
- **Affected artifacts:** `app/build.gradle.kts`,
  `.github/workflows/release-apk.yml` (exact version-code assertion),
  `README.md`, this ticket's release acceptance and status text, and
  `evidence/TICKET-011/record.md`.
- **Unaffected ancestry and scope:** `OBJ-001`, `SCOPE-001`, `PHASE-004`,
  `FEAT-010`, breathing behavior, safety boundaries, and ticket status.
- **Non-goals:** moving or reusing `v0.1.0`, `v0.1.1`, or `v0.1.2`; changing
  app behavior; claiming release publication before all hosted checks pass.
- **Evidence:** EVID-044 records the immutable v0.1.2 hosted failure;
  EVID-045 through EVID-053 record the corrected workflow, exact-profile
  review, commit, and branch push. The user authorized this target after the
  explicit approval request in this session.

### CHG-003 - Moving the release target to v0.1.4

- **Status:** approved; signed v0.1.4 release published and API 35 validation
  passed.
- **Approval:** the user selected `authorize-v0.1.4` after hosted run
  `36015108549` failed before app launch and publication.
- **Previous target:** immutable `v0.1.3`, version name `0.1.3`, version code
  `4`; its hosted workflow installed the APK but failed while parsing the
  multiline emulator script. No GitHub release asset was created.
- **Approved target:** signed `v0.1.4`, version name `0.1.4`, version code
  `5`.
- **Rationale:** the emulator action executes each line of its `script`
  input separately. The fix moves the launch checks into a Bash file invoked
  by one command. The immutable v0.1.3 tag cannot include that correction.
- **Affected artifacts:** `app/build.gradle.kts`,
  `.github/workflows/release-apk.yml`, `.github/scripts/`,
  `README.md`, this ticket's release target and status, and appended
  evidence in `evidence/TICKET-011/record.md`.
- **Unaffected ancestry and scope:** `OBJ-001`, `SCOPE-001`, `PHASE-004`,
  `FEAT-010`, breathing behavior, safety boundaries, and ticket status.
- **Non-goals:** moving or reusing `v0.1.0`, `v0.1.1`, `v0.1.2`, or
  `v0.1.3`; changing app behavior; publishing before the signed artifact
  passes API 35 install and launch.
- **Evidence:** EVID-071 records the hosted failure; EVID-072 records the
  corrected launch-script regression checks; EVID-074 records the user's
  approval of the new immutable release target; EVID-076 through EVID-079
  record the hosted pass, downloaded asset, commit, branch push, and tag.
