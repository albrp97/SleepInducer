# Ticket 002 - Presenting the offline wellness setup boundary

**Ticket ID:** `TICKET-002`
**Status:** complete
**Parent feature:** `FEAT-001`
**Parent phase:** `PHASE-001`
**Parent objective:** `OBJ-001`
**Parent scope:** `SCOPE-001`
**Capabilities:** `CAP-005`, `CAP-006`
**Owner:** Android UI implementation
**Source paths:** `vision.md`, `docs/specs/project-scope.md`,
`docs/specs/capability-map.md`,
`docs/planning/features/closed/FEAT-001-buildable-offline-foundation.md`,
`docs/planning/tickets/closed/TICKET-001-android-build-foundation.md`
**Last updated:** 2026-09-09

## Outcome

Present a quiet first screen that explains the breathing-pacer purpose,
wellness limitations, immediate stop guidance, and local-only behavior before a
session can be started.

## Scope

- Setup copy and accessible labels.
- Explicit statement that the app is not medical treatment.
- Stop-if-uncomfortable guidance.
- Offline/local-only wording.
- A testable boundary for the future duration/start controls.

## Non-goals

- Implementing the breathing engine or haptic service.
- Collecting health data, names, sleep history, or typed notes.

## Affected surfaces

- Initial activity/composable layout.
- String resources and accessibility semantics.
- Android functionality tests and README screenshots or wording if needed.

## Dependencies and risks

- **Dependencies:** `TICKET-001` Android build foundation and reviewed scope
  language.
- **Risks:** clinical overclaiming, missing stop guidance, or a UI that implies
  the user must continue through discomfort.
- **Mitigation:** copy assertions in functionality tests and reuse the approved
  wording from `vision.md` and `project-scope.md`.

## Acceptance criteria and automated functionality tests

1. **Honest purpose:** Given the app opens, the first screen identifies the
   feature as a breathing and relaxation aid rather than insomnia treatment.
   - **Functionality test:** `SetupFlowTest#showsWellnessPurposeAndLimitations`
     launches the activity and asserts the visible purpose and limitation text.
   - **Expected state:** both strings are visible and readable.
2. **Immediate stop guidance:** Given the first screen is open, the user sees
   guidance to stop for dizziness, shortness of breath, pain, panic, or
   discomfort.
   - **Functionality test:** `SetupFlowTest#showsStopIfUncomfortableGuidance`
     asserts the safety copy through the rendered UI semantics tree.
   - **Expected state:** all required symptom categories are exposed.
3. **Local-only operation:** Given the first screen is open without network,
   the UI does not request an account or remote permission.
   - **Functionality test:** `SetupFlowTest#opensWithoutAccountOrNetwork`
     launches with network disabled and asserts setup content is stable with no
     account or network error state.
   - **Expected state:** the setup boundary remains usable offline.

## Baseline and evidence

- **Protected baseline:** foundational scope already limits the app to
  breathing-only, offline, minimal local state.
- **Commands:** `./gradlew connectedDebugAndroidTest --offline`; the Android
  connected-test task runs the complete instrumentation suite because it does
  not support Gradle's `--tests` option.
- **Evidence path:** `evidence/TICKET-002/`.
- **Classification:** `automaticValidation` only, using the configured
  `rubber-duck` / `gpt-5.6-luna` / high / `all-validation` profile.

## User-validation plan

Automatic mode requires the agent to execute and record the functionality
charter without waiting. An optional human smoke check is to open the app with
network disabled, read the purpose and limitation text, confirm the stop
guidance is visible before starting anything, and report the device API level
and visible result.

## Definition of done

- Setup copy is stored in resources and is accessible.
- All three functionality tests pass.
- No account, network, or health-data collection path is introduced.
- Evidence is appended under `evidence/TICKET-002/`.
