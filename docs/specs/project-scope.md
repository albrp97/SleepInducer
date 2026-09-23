# Project Scope Handoff

**Scope ID:** SCOPE-001
**Objective ID:** OBJ-001
**Status:** Confirmed
**Planning depth:** Full
**Development mode:** Automatic
**Last updated:** 2026-09-08

## Objective

Help adult users settle before sleep with a comfortable, low-distraction,
screen-off guided breathing session that does not require audio, an account, or
network access.

## Problem

Some users benefit from an external breathing pace when trying to wind down,
but many phone-based approaches require looking at a bright screen, listening
to audio, or interacting with a feature-heavy sleep product. The user wants a
simple session that can be configured while awake and then followed through
gentle vibration with the display off.

## Desired outcome

A user can start a timed breathing session, place the phone in a comfortable
position, turn the display off, and receive predictable phase cues until the
session ends or is stopped. The product must be honest that this is a
wellness/relaxation aid supported by preliminary evidence, not a guaranteed
sleep-onset treatment.

## User story

As an adult trying to wind down before sleep, I want a simple breathing
session that continues through gentle haptic cues after I turn the screen off,
so that I can follow the rhythm without stimulating myself with a bright
screen, audio, or complex interaction.

## Scope horizon

### Included

- Android application shell and offline operation.
- Visible setup screen with five-, ten-, and twenty-minute presets plus a
  custom slider from zero to twenty minutes. A custom session starts only when
  at least one minute is selected.
- Default breathing rhythm uses six-second inhale and exhale phases, with
  independently adjustable whole- or half-second timing.
- No mandatory breath hold in the initial protocol.
- Gentle phase-transition haptic cues.
- Screen-off session continuity.
- System light and dark theme support.
- Immediate stop, natural completion, and interrupted-session handling.
- Hardware capability detection and explicit haptic-unavailable messaging.
- Local-only preferences or operational state when required for reliable
  behavior.
- Scientific and technical evidence records for protocol and platform choices.

### Explicitly excluded

- Sleep education, CBT-I content, sleep diary, sleep tracking, or sleep-stage
  inference.
- Audio guidance, music, ambient noise, or spoken instructions.
- Heart-rate, HRV, respiration sensing, wearable integration, or adaptive
  biofeedback.
- Medical diagnosis, treatment claims, clinical decision support, or
  personalized medical advice.
- Mandatory holds, hyperventilation, performance scores, streaks, or
  gamification.
- Accounts, cloud storage, analytics, advertising, remote content, or
  unnecessary telemetry.
- Children as a primary target population.

## Candidate capability coverage for the next planning layer

These are handoff themes, not child records created by bootstrap:

- `CAP-001` - Configurable breathing session timing.
- `CAP-002` - Screen-off session lifecycle continuity.
- `CAP-003` - Gentle, capability-aware haptic delivery.
- `CAP-004` - Safe stop, completion, and interruption handling.
- `CAP-005` - Offline, minimal-data operation.
- `CAP-006` - Evidence-backed protocol and honest safety messaging.

## Research-informed initial defaults

- Pace: a slow, comfortable breathing rhythm with six-second inhale and
  exhale defaults.
- Timing: each phase can be set independently from two to ten seconds in
  half-second steps.
- Holds: none required in the first release.
- Duration: ten minutes by default, with a twenty-minute preset aligned with
  the direct insomnia breathing study and a shorter option for comfort testing.
- Breath depth: comfortable and natural; do not instruct users to force maximal
  inhalation or exhalation.
- Haptics: short, gentle transition cues with a distinct meaning for each
  phase; exact waveform and amplitude remain a device-validation decision.

## Observable functional requirements

- **FR-001:** Given the app is open and the device is usable, the user should
  be able to select a supported duration and start a session without an
  account or network connection.
- **FR-002:** Given a session is active, the system should emit the configured
  phase cues at the configured cadence while the display is off, within the
  defined timing tolerance.
- **FR-003:** Given the user stops an active session, the system should cancel
  all future cues, stop the active service, and show a stopped state.
- **FR-004:** Given the session reaches its duration, the system should stop
  future cues, release the active service, and show a completed state.
- **FR-005:** Given the device has no usable vibrator or amplitude support, the
  system should communicate the limitation clearly and must not claim that
  haptic cues were delivered.
- **FR-006:** Given the operating system interrupts or terminates the session,
  the system should surface the interruption when possible and must not leave
  a success-shaped active state.
- **FR-007:** Given the user experiences dizziness, shortness of breath,
  air hunger, pain, panic, or discomfort, the product should make stopping
  immediate and should not encourage the user to continue.
- **FR-008:** Given a normal session, the system should keep operational state
  local and should not transmit health, sleep, or user-entered data.

## Protected behaviors

- Comfortable, non-competitive breathing remains the default.
- No mandatory breath holds or hyperventilation patterns are introduced without
  a separately reviewed safety and evidence change.
- Screen-off operation remains a first-class acceptance boundary.
- Stop and interruption paths must cancel future vibration.
- Failure to start a service, persist required state, or deliver haptics must
  be surfaced rather than silently treated as success.
- The app remains a wellness aid and does not claim equivalence to CBT-I.

## Dependencies and constraints

- Android vibration APIs and device hardware.
- Android foreground-service and notification behavior on the target API.
- A connected emulator or physical device for functionality evidence.
- JDK 17 and Android SDK 35 for the intended build environment.
- No external API or network service is required for the core experience.

## Risks

| Risk | Impact | Mitigation |
|---|---|---|
| Haptic timing changes across devices or OEM power policies | User cannot follow the exercise reliably | Use a foreground service, test screen-off behavior on emulator and physical devices, record timing evidence, and expose interruption states. |
| Vibration is too strong or annoying on a phone placed near the body | Increased arousal or discomfort | Use short low-intensity cues, detect amplitude capability, provide a stop path, and validate on real hardware. |
| Slow breathing causes discomfort or air hunger for some users | Safety and trust failure | No holds or forced depth, clear stop guidance, and no performance framing. |
| Users interpret the app as an insomnia treatment | Misleading health claim | Keep wellness language and limitations visible; point persistent/severe problems toward clinical care. |
| Android foreground-service policy changes or blocks the chosen type | Screen-off session cannot run or cannot ship | Treat the service type and distribution policy as an explicit implementation gate. |
| A phone placed under bedding overheats or becomes uncomfortable | Physical safety and usability issue | Instruct users to keep the device uncovered and positioned comfortably; do not require placing it under a pillow or body weight. |

## Verification and evidence plan

- Record a protected baseline before implementation.
- Unit-test phase timing, duration, cancellation, completion, and interruption
  state transitions.
- Run Android functionality tests through the visible app boundary for start,
  stop, completion, screen-off continuity, and unavailable hardware.
- Validate on an emulator and at least one physical device with the display
  off, capturing API level, screen state, haptic capability, and interruptions.
- Run the configured static-analysis and review workflow against the final
  diff.
- Record all results in configured evidence paths and use
  `automaticValidation` for automatic-mode classification.

## Open decisions

- Minimum supported Android API: 26 for the initial implementation.
- Application ID: `com.sleepinducer.app`; display name: `Sleep Inducer`.
- Exact haptic waveform and phase encoding after hardware testing.
- For the API 35 APK, use the documented `specialUse` foreground-service type
  with an explicit timed-haptic subtype; Google Play approval remains an
  unresolved release-policy gate.
- Whether local session preferences are needed beyond transient service state.
- Release signing, license, remote repository, and release-channel policy.

## Definition of done for this scope

- The Android project builds from a clean clone with repository-native
  commands.
- Every acceptance outcome has an executable automated functionality test.
- Screen-off haptics are verified on the supported emulator/device matrix.
- Stop, completion, missing hardware, service interruption, and persistence
  failure paths are explicit and tested.
- Safety/privacy language and evidence limitations are present.
- Review, automatic validation, and configured delivery gates are terminal, or
  an exact external blocker is recorded.
