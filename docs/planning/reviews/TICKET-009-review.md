# Review - TICKET-009

**Ticket:** `TICKET-009` - Providing safe session controls
**Feature:** `FEAT-007` - Operating safe session controls
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
- The Compose flow, selected-duration handoff, visible stop action, safety
  copy, offline copy, and failure messaging are within the approved scope.
- TICKET-008 owns the service implementation and TICKET-010 owns complete
  functionality evidence. TICKET-011 remains open and blocked for release
  readiness.

## Technical verification

- `./gradlew test lintDebug assembleDebug --offline` passed with exit code `0`.
- The current reports contain 27 debug and 27 release unit tests with zero
  failures or errors.
- Android lint passed without findings and the debug APK was rebuilt at
  `app/build/outputs/apk/debug/app-debug.apk`.
- `git diff --check` passed.
- The earlier complete packaged run passed all 35 Android instrumentation
  tests with zero failures and zero skips on `emulator-5554` API 35.
- A post-remediation targeted control test was blocked before execution by an
  emulator startup ANR. Logcat reported `ANR in com.sleepinducer.app` and
  `failed to complete startup`, with no application-specific fatal exception.

## Automated functionality

`SessionControlsFlowTest` covers the duration choices, start action, selected
duration in the active session, and visible stop behavior. Supporting packaged
tests cover setup, offline/manifest boundaries, invalid starts, and service
cleanup. The earlier complete API 35 run remains the terminal result for the
unchanged normal control flow. The targeted rerun is retained as an explicit
emulator infrastructure blocker because its test body did not execute.

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

- Compose owns duration selection, start/stop actions, status rendering, and
  safety messaging.
- The service owns timing, haptics, foreground continuity, persistence, and
  terminal state.
- The UI does not add timing policy, health sensing, accounts, network
  access, audio, or medical claims.
- Notification permission is requested for visibility but does not turn into
  a false guarantee of in-app control while the display is off.

## Findings

| Severity | Finding | Disposition |
|---|---|---|
| Warning | Fresh targeted instrumentation is blocked by emulator startup ANR | Accepted for local commit; retained as explicit evidence and does not replace the prior terminal full run |
| Warning | Optional churn analyzer is unavailable | Accepted with coverage note; no package was installed |
| Warning | No Android PR pipeline is configured and the workflow-evaluation assets are missing | Accepted as a pre-existing delivery/configuration gap |
| Warning | Physical-device comfort and Google Play `specialUse` approval remain unresolved | Follow-up in `TICKET-011`; not represented as local delivery evidence |

No introduced correctness, safety, privacy, security, scope, or architecture
defect remains within the TICKET-009 boundary.

## Readiness decision

**Result:** Ready for the configured local commit checkpoint with the warnings
above explicitly recorded. This is not a production-release readiness decision.

**Evidence:** `evidence/TICKET-009/record.md`,
`evidence/static-analysis/TICKET-008.md`
