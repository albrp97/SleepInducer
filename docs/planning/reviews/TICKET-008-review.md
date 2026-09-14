# Review - TICKET-008

**Ticket:** `TICKET-008` - Running the breathing session in the foreground service
**Feature:** `FEAT-006` - Continuing sessions with the display off
**Phase:** `PHASE-003` - Delivering screen-off relaxation use
**Objective:** `OBJ-001`
**Scope:** `SCOPE-001`
**Source branch:** `ticket/running-session-service`
**Base revision:** `ed5255f`
**Status:** reviewed with concerns
**Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
`all-validation`
**Reviewed:** 2026-09-14

## Planning coverage

- The ticket remains in the configured `open` directory and links to its
  confirmed feature, phase, scope, capabilities, and objective.
- The service implementation, runner integration, haptic coordination,
  interruption recovery, and notification stop path are within the approved
  TICKET-008 scope.
- TICKET-009 and TICKET-010 remain open sibling tickets for the integrated
  control and evidence surfaces. TICKET-011 remains open and blocked for
  physical-device and release readiness.

## Technical verification

- `./gradlew test lintDebug assembleDebug --offline` passed with exit code `0`.
- The current reports contain 27 debug and 27 release unit tests with zero
  failures or errors.
- Android lint passed without findings and the debug APK was rebuilt at
  `app/build/outputs/apk/debug/app-debug.apk`.
- `git diff --check` passed.
- The earlier complete packaged run passed all 35 Android instrumentation
  tests with zero failures and zero skips on `emulator-5554` API 35 before the
  latest haptic-cancellation remediation.
- The post-remediation targeted instrumentation attempt was blocked before
  test execution by an emulator startup ANR. Logcat reported
  `ANR in com.sleepinducer.app` and `failed to complete startup`, with no
  application-specific fatal exception.

## Automated functionality

`ForegroundSessionServiceFlowTest` covers explicit activation, active phase
advancement, display-off continuity, interruption recovery, invalid starts,
notification stop, and terminal cleanup. `SessionEngineFlowTest` and
`SessionRunnerFlowTest` cover packaged completion and cancellation of future
work. The prior complete API 35 run is retained as the terminal result for
unchanged normal service flows. The fresh targeted rerun is recorded as an
infrastructure blocker rather than an application failure because the test
process did not complete startup.

## Static analysis and parity

The deterministic analysis record is
[`evidence/static-analysis/TICKET-008.md`](../../../evidence/static-analysis/TICKET-008.md).
The final diff passes Git whitespace checks, unit tests, Android lint, and
debug APK assembly. The optional `aidd@3.1.0` churn analyzer is unavailable
locally. No Android pull-request build/test pipeline is configured, so
Android local-to-PR parity is `notApplicable`. The discovered workflow
evaluation job references missing `tools/eval_workflows.py` and
`ai-evals/workflow-contracts.json`; this is pre-existing and outside the
ticket's implementation scope.

## Architecture and scope

- `BreathingSessionEngine` composes the Android-independent runner with the
  phase haptic coordinator.
- `BreathingSessionService` owns Android lifecycle continuity, notification
  updates, persistence, failure routing, and cleanup.
- `HapticAdapter` is the only vibrator adapter and now converts cancellation
  security/system failures into explicit results at lines 114-129.
- `PhaseHapticCoordinator` propagates terminal cancellation failures at
  lines 51-84, and the service routes them to an explicit failed session at
  lines 226-271.
- Absolute monotonic scheduling, short bounded cues, a non-exported
  non-sticky service, and the breathing-only boundary remain intact.

## Findings

| Severity | Finding | Disposition |
|---|---|---|
| Warning | Fresh targeted instrumentation is blocked by emulator startup ANR | Accepted for local commit; retained as explicit evidence and does not replace the prior terminal full run |
| Warning | Optional churn analyzer is unavailable | Accepted with coverage note; no package was installed |
| Warning | No Android PR pipeline is configured and the workflow-evaluation assets are missing | Accepted as a pre-existing delivery/configuration gap |
| Warning | Physical-device comfort and Google Play `specialUse` approval remain unresolved | Follow-up in `TICKET-011`; not represented as local delivery evidence |

No introduced correctness, safety, privacy, security, scope, or architecture
defect remains within the TICKET-008 boundary. The previously identified
haptic cancellation concern was remediated and covered by unit and engine
tests.

## Readiness decision

**Result:** Ready for the configured local commit checkpoint with the warnings
above explicitly recorded. This is not a production-release readiness decision.

**Evidence:** `evidence/TICKET-008/record.md`,
`evidence/static-analysis/TICKET-008.md`
