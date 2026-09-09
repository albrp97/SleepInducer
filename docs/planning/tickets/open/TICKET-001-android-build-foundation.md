# Ticket 001 - Creating the Android build foundation

**Ticket ID:** `TICKET-001`
**Status:** inProgress
**Parent feature:** `FEAT-001`
**Parent phase:** `PHASE-001`
**Parent objective:** `OBJ-001`
**Parent scope:** `SCOPE-001`
**Capabilities:** `CAP-005`, `CAP-006`
**Owner:** Android implementation
**Source paths:** `AGENTS.md`, `README.md`,
`docs/specs/project-scope.md`,
`docs/planning/features/open/FEAT-001-buildable-offline-foundation.md`
**Last updated:** 2026-09-08

## Outcome

Create a minimal, clean-clone Android project that builds a debug APK and
launches an offline app boundary without introducing network, account, health
data, or sensitive logging dependencies.

## Scope

- Gradle wrapper and Android application module.
- JDK 17/SDK 35-compatible build configuration.
- Stable application ID and display name decision.
- Manifest baseline with only required permissions.
- Minimal launchable activity and a placeholder setup boundary.
- Repository-native build and instrumentation test commands.

## Non-goals

- Breathing timing, haptic delivery, foreground service, or final session UI.
- Release signing, remote publication, analytics, or cloud services.

## Affected surfaces

- `settings.gradle.kts`, root and app Gradle configuration.
- `app/src/main/AndroidManifest.xml`.
- `app/src/main/java/` or `app/src/main/kotlin/`.
- `app/src/main/res/`.
- `app/src/androidTest/`.
- `README.md` and project-specific build guidance.

## Dependencies and risks

- **Dependencies:** JDK 17, Android SDK 35, Gradle distribution availability.
- **Risks:** undeclared local tool dependencies, accidental network permission,
  or an application identity that conflicts with the intended package.
- **Mitigation:** use the repository-native wrapper, keep the manifest minimal,
  and verify from a clean build path.

## Acceptance criteria and automated functionality tests

1. **Buildable APK:** Given a clean checkout with JDK 17 and SDK 35,
   `./gradlew assembleDebug` produces the expected debug APK.
   - **Functionality test:** `./gradlew connectedDebugAndroidTest --tests
     com.sleepinducer.app.LaunchFlowTest#launchesTheOfflineSetupBoundary`
     installs the built app, launches the real activity, and asserts the
     visible app identity and setup boundary.
   - **Expected state:** the activity is displayed and no crash or network
     dependency is required.
2. **Minimal offline boundary:** Given the installed debug APK and no network
   access, the app opens the initial screen and exposes no account or remote
   setup requirement.
   - **Functionality test:** the same application-boundary test runs with the
     test device network unavailable and asserts setup content is visible
     without a loading or error state.
   - **Expected state:** the setup screen is usable offline.
3. **No unnecessary permission:** Given a manifest inspection, the app declares
   only permissions required by the current behavior.
   - **Functionality test:** `LaunchFlowTest#manifestHasNoNetworkPermission`
     queries the installed package metadata and asserts no internet permission
     is present before network behavior is added.
   - **Expected state:** the assertion passes and the app remains local-only.

## Baseline and evidence

- **Protected baseline:** repository contains no Android source, manifest,
  Gradle wrapper, tests, or APK.
- **Commands:** `./gradlew assembleDebug`,
  `./gradlew connectedDebugAndroidTest`.
- **Evidence path:** `evidence/TICKET-001/`.
- **Raw evidence:** command output, APK path/size, device API level, and test
  assertions.
- **Classification:** `automaticValidation` only, using the configured
  `rubber-duck` / `gpt-5.6-luna` / high / `all-validation` profile.

## User-validation plan

This repository is in automatic mode, so the agent must execute the same
functionality charter and record `automaticValidation` rather than waiting for
human confirmation. If a human performs an optional smoke check, they should
install the debug APK, open the app with network disabled, confirm the setup
screen appears, and report the visible result and device API level.

## Definition of done

- The Android project and wrapper are present.
- The debug APK builds from the repository-native command.
- The launch-flow functionality test is executable and passes.
- The manifest has no unnecessary network permission.
- Build/setup documentation matches the actual commands.
- Evidence is appended under `evidence/TICKET-001/`.
