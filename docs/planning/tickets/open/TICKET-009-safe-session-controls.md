# Ticket 009 - Providing safe session controls

**Ticket ID:** `TICKET-009`
**Status:** pending
**Parent feature:** `FEAT-007`
**Parent phase:** `PHASE-003`
**Parent objective:** `OBJ-001`
**Parent scope:** `SCOPE-001`
**Capabilities:** `CAP-004`, `CAP-005`, `CAP-006`
**Owner:** Android UI and domain integration
**Source paths:** `docs/planning/features/open/FEAT-007-safe-session-controls.md`,
`docs/specs/project-scope.md`, `vision.md`
**Development mode:** automatic
**Approval state:** bootstrap-authorized automatic child planning
**Last updated:** 2026-09-23

## Outcome

Provide a clear, low-distraction Compose flow for choosing a preset or custom
duration, starting and stopping a session, following the active phase, and
seeing honest safety, haptic, notification, and interruption messaging.

## Scope

- Show five-, ten-, and twenty-minute presets plus a custom slider from zero to
  twenty minutes. Require at least one minute before starting a custom session.
- Default to six-second inhale and exhale phases, with independent sliders from
  two to ten seconds in half-second steps.
- Start a user-selected session through the foreground service.
- Show the active inhale/exhale phase and approximate remaining time.
- Keep a prominent stop control visible while active.
- Show completion, stopped, interrupted, haptic, notification, and service
  failure states.
- Explain the six-second default inhale/exhale technique, zero-second hold,
  adjustable phase timing, research basis, phase cues, discomfort stop guidance, phone
  positioning, and offline operation.
- Follow the device light or dark theme.

## Non-goals

- Sleep tracking, health sensing, accounts, network access, audio, or medical
  claims.
- Persisting user-entered content or session history.
- Replacing the foreground service or embedding timing policy in Compose.

## Acceptance criteria and automated functionality tests

1. Given the app is open, preset and custom duration choices and a start action
   are visible and selectable.
   - **Functionality test:** `SessionControlsFlowTest#showsDurationChoicesAndStartAction`.
2. Given a selected duration and usable device, the visible flow starts the
   service and reports the selected duration and active phase.
   - **Functionality test:** `SessionControlsFlowTest#selectedDurationIsUsedByTheActiveSession`.
3. Given a valid custom duration, the visible flow starts the service with that
   exact duration.
   - **Functionality test:** `SessionControlsFlowTest#startsWithSliderCustomDuration`.
4. Given the user selects inhale and exhale timings, the visible active state
   reports those exact phase durations.
   - **Functionality test:** `SessionControlsFlowTest#startsWithConfiguredBreathingTiming`.
5. Given the setup screen is open, the technique and usage instructions are
   visible before starting.
   - **Functionality test:** `SetupFlowTest#explainsTheTechniqueAndUsage`.
6. Given an active session, the user can stop immediately and sees that future
   cues were cancelled.
   - **Functionality test:** `SessionControlsFlowTest#startsAndStopsFromVisibleControls`.
7. Given the device has no usable haptics or notification permission, the UI
   communicates the limitation without claiming successful delivery.
   - **Functionality coverage:** `ForegroundSessionServiceFlowTest` and
     capability-aware adapter flows.

## Protected behaviors

- The UI does not add mandatory holds, forced breath depth, or performance
  pressure.
- Phase timing remains within the supported two-to-ten-second range and uses
  only whole or half-second values.
- The stop path remains visible before display-off operation.
- Safety and wellness limitations remain visible and honest.
- Notification permission is requested for visibility but does not block the
  haptic session when the user declines it.

## Verification and evidence

- `./gradlew test --offline`
- `./gradlew lintDebug --offline`
- `./gradlew connectedDebugAndroidTest --offline`
- Evidence path: `evidence/TICKET-009/`.
- Automatic validation uses `rubber-duck` / `gpt-5.6-luna` / high /
  `all-validation`.

## Definition of done

- The Compose flow is connected to the bound service and renders all terminal
  and failure states in scope.
- Automated functionality tests cover selection, start, active status, and
  stop behavior.
- README/setup copy no longer describes the controls as unfinished.
- Evidence, review, commit, and lifecycle closeout are terminal.
