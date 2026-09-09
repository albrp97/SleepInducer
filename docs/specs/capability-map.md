# Capability Map

**Map ID:** CAPMAP-001
**Status:** Confirmed after automatic bootstrap authorization
**Objective:** `OBJ-001` in [`vision.md`](../../vision.md)
**Scope:** `SCOPE-001` in [`project-scope.md`](project-scope.md)
**Repository map:** [`repo-map.md`](../planning/repo-map.md)
**Development mode:** Automatic
**Source revision:** 2026-09-08

## Coverage model

The capabilities below describe product or operational abilities rather than
implementation folders. Each capability links to the approved objective and
scope, identifies dependencies and affected surfaces, and names the evidence
needed before it can be considered complete.

The capability map does not create phases, features, or tickets. Those layers
must be derived only after this map has been written and verified.

## Capabilities

### CAP-001 - Guiding a configurable breathing protocol

- **Outcome:** Deliver a comfortable, timed breathing rhythm near six breaths
  per minute with supported durations and no mandatory breath holds.
- **Ancestry:** `OBJ-001` -> `SCOPE-001`
- **Affected surfaces:** breathing domain, session setup UI, local preferences,
  unit tests, Android functionality tests.
- **Dependencies:** approved protocol defaults, monotonic timing source,
  supported duration list.
- **Risks:** timer drift, forced breathing depth, unclear phase semantics,
  discomfort from an overly rigid cadence.
- **Protected behavior:** natural comfortable breathing remains the default;
  no hyperventilation or competitive breath-holding behavior.
- **Verification:** deterministic state-machine tests for phase boundaries,
  duration, stop, completion, interruption, and timing tolerance; observable
  UI test for selecting and starting a duration.
- **Requirements covered:** `FR-001`, `FR-002`, `FR-004`, `FR-007`
- **Evidence:** research baseline, unit output, functionality output, and
  automatic-validation record.

### CAP-002 - Continuing a session with the display off

- **Outcome:** Keep the active breathing session alive after the user turns
  off the display, while exposing a clear active, stopped, completed, or
  interrupted state.
- **Ancestry:** `OBJ-001` -> `SCOPE-001`
- **Affected surfaces:** Android service lifecycle, activity/service boundary,
  notification, screen-off behavior, instrumentation tests.
- **Dependencies:** service start from visible UI, target Android API policy,
  foreground-service declaration and permissions, monotonic clock.
- **Risks:** process death, OEM battery restrictions, Doze behavior, lock-screen
  transitions, foreground-service policy incompatibility.
- **Protected behavior:** screen-off operation is a first-class acceptance
  boundary and service failures are not treated as successful sessions.
- **Verification:** emulator and physical-device functionality flows with the
  display off, interruption tests, service-stop tests, and timing evidence.
- **Requirements covered:** `FR-002`, `FR-003`, `FR-004`, `FR-006`
- **Evidence:** Android API research, manifest/configuration review, device
  matrix output, and automatic-validation record.

### CAP-003 - Delivering gentle, capability-aware haptics

- **Outcome:** Emit short, phase-specific vibration cues using conservative
  settings and detect when the device cannot deliver usable haptics.
- **Ancestry:** `OBJ-001` -> `SCOPE-001`
- **Affected surfaces:** haptic adapter, `Vibrator`/`VibratorManager` API
  compatibility, amplitude detection, hardware tests, user-facing errors.
- **Dependencies:** `android.permission.VIBRATE`, target API support,
  selected cue waveform, cancellation path.
- **Risks:** device-to-device amplitude variation, cues that are too strong or
  too weak, unsupported amplitude control, stale vibration after stop.
- **Protected behavior:** no success-shaped fallback when haptics are
  unavailable; no continuous or unnecessarily intense vibration.
- **Verification:** fake-vibrator unit tests, hardware capability tests,
  screen-off physical-device tests, cancellation tests, and usability review
  of cue intensity.
- **Requirements covered:** `FR-002`, `FR-003`, `FR-005`, `FR-004`
- **Evidence:** Android API documentation, hardware capability logs, raw cue
  timing measurements, and automatic-validation record.

### CAP-004 - Stopping, completing, and recovering safely

- **Outcome:** Let the user stop immediately, end naturally, and recover from
  service or operating-system interruption without future cues or misleading
  success state.
- **Ancestry:** `OBJ-001` -> `SCOPE-001`
- **Affected surfaces:** domain state machine, UI controls, service teardown,
  notification actions, interruption recovery, functionality tests.
- **Dependencies:** explicit state model, cancellable scheduler, service
  lifecycle callbacks, persisted or transient operational state.
- **Risks:** race conditions at phase/duration boundaries, duplicate completion
  cues, service destruction without cancellation, stale UI state.
- **Protected behavior:** every stop path cancels future vibration and
  communicates the resulting state.
- **Verification:** unit tests for cancellation races and terminal states,
  functionality tests for user stop and natural completion, and interruption
  recovery evidence.
- **Requirements covered:** `FR-003`, `FR-004`, `FR-006`, `FR-007`
- **Evidence:** state-transition test output, Android lifecycle output, and
  automatic-validation record.

### CAP-005 - Operating offline with minimal local state

- **Outcome:** Provide the core session without an account, network, cloud
  service, advertising, analytics, or unnecessary collection of sleep or
  health information.
- **Ancestry:** `OBJ-001` -> `SCOPE-001`
- **Affected surfaces:** app configuration, storage boundary, manifest,
  dependencies, logs, privacy documentation, tests.
- **Dependencies:** local defaults and optional local preferences only.
- **Risks:** accidental network dependency, sensitive logging, hidden
  telemetry, persistence failure masked as success.
- **Protected behavior:** no user-entered health or sleep data is transmitted;
  local failures are surfaced explicitly.
- **Verification:** offline build/install/run, manifest/dependency inspection,
  storage and log review, and functionality test with network unavailable.
- **Requirements covered:** `FR-001`, `FR-008`
- **Evidence:** dependency/configuration review, offline functionality output,
  static analysis, and automatic-validation record.

### CAP-006 - Preserving evidence-backed safety and honest claims

- **Outcome:** Keep protocol defaults, user wording, safety guidance, and
  release claims aligned with the strength and limits of the available
  evidence.
- **Ancestry:** `OBJ-001` -> `SCOPE-001`
- **Affected surfaces:** research documentation, scope, UI copy, README,
  release notes, review evidence.
- **Dependencies:** maintained research baseline, reviewed safety copy,
  explicit distinction between clinical insomnia care and wellness use.
- **Risks:** overclaiming efficacy, silently adding holds, confusing breathing
  guidance with treatment, omitted stop guidance.
- **Protected behavior:** the app never claims to diagnose or treat insomnia
  and never presents preliminary haptic evidence as clinical proof.
- **Verification:** documentation review, UI copy functionality assertions,
  safety-path tests, static analysis, and final scope/review audit.
- **Requirements covered:** `FR-005`, `FR-007`, `FR-008`
- **Evidence:** [`sleep-onset-evidence.md`](../research/sleep-onset-evidence.md),
  review report, functionality output, and automatic-validation record.

## Requirement coverage

| Requirement | Covered by capabilities | Required evidence |
|---|---|---|
| `FR-001` | `CAP-001`, `CAP-005` | Start-flow functionality test and offline run |
| `FR-002` | `CAP-001`, `CAP-002`, `CAP-003` | Screen-off timing and haptic evidence |
| `FR-003` | `CAP-002`, `CAP-003`, `CAP-004` | Stop functionality test and cancellation evidence |
| `FR-004` | `CAP-001`, `CAP-002`, `CAP-003`, `CAP-004` | Completion functionality test and service teardown evidence |
| `FR-005` | `CAP-003`, `CAP-006` | Missing-hardware behavior and honest UI copy |
| `FR-006` | `CAP-002`, `CAP-004` | Service interruption functionality test |
| `FR-007` | `CAP-001`, `CAP-004`, `CAP-006` | Immediate stop path and safety-copy assertions |
| `FR-008` | `CAP-005`, `CAP-006` | Offline run, storage/log review, and privacy evidence |

## Cross-cutting dependencies

- Android foreground-service type and distribution-policy decision.
- Minimum supported Android API and application ID.
- Haptic waveform and amplitude policy after physical-device testing.
- Test device or emulator with controllable screen and lock state.
- Runtime availability of the exact automatic validation profile:
  `rubber-duck`, `gpt-5.6-luna`, high reasoning, `all-validation`.

## Coverage gaps and blockers

- No Android project or executable test boundary exists yet, so no capability
  is implementation-complete.
- The correct foreground-service type for ten- to twenty-minute haptic
  sessions remains unresolved.
- Physical-device haptic timing and comfort are unverified.
- Release signing, minimum API, license, remote provider, and delivery channel
  remain unresolved.
- The configuration declares the required automatic validator profile; runtime
  capability availability must be independently verified before any automatic
  readiness or validation pass is claimed.

## Handoff

After this map is written and verified, the next planning layer is ordered
phase creation. Phase planning must preserve the breathing-only scope, keep
platform feasibility before release packaging, and attach explicit entry and
exit evidence to each phase.
