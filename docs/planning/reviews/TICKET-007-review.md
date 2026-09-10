# Review - TICKET-007

**Ticket:** `TICKET-007` - Establishing the foreground session service
**Feature:** `FEAT-006` - Continuing sessions with the display off
**Phase:** `PHASE-003` - Delivering screen-off relaxation use
**Objective:** `OBJ-001`
**Scope:** `SCOPE-001`
**Status:** reviewed with concerns
**Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
`all-validation`
**Reviewed:** 2026-09-10

## Planning coverage

- The ticket remains in the configured `open` directory and links to the
  confirmed feature, phase, capabilities, scope, and objective.
- `FEAT-006` now lists `TICKET-007` as its planned child.
- The ticket's four acceptance outcomes each have packaged Android
  functionality coverage and terminal automatic-validation evidence.
- The `specialUse` choice is recorded in the vision, scope, and research
  artifacts without treating Google Play approval as complete.

## Technical verification

- `./gradlew test --offline` passed 22 tests in each debug and release unit
  variant.
- `./gradlew lintDebug --offline` passed with no errors or warnings.
- `./gradlew assembleDebug --offline` passed and produced
  `app/build/outputs/apk/debug/app-debug.apk`.
- `./gradlew connectedDebugAndroidTest --offline` passed all 27 tests with zero
  failures and zero skips on `emulator-5554` API 35.
- `git diff --check` passed.

## Automated functionality

`ForegroundSessionServiceFlowTest` inspected the installed manifest and
exercised the packaged service boundary. It verified the required
`specialUse` permissions and subtype, explicit valid activation with a
low-importance notification channel, invalid-start failure without active
ownership, and explicit stop cleanup. The remaining application functionality
tests stayed green.

The service intentionally does not yet wire the breathing runner or haptic
coordinator. Display-off timing, lock-screen behavior, process interruption,
completion, and physical-device comfort remain follow-up acceptance work in
`FEAT-006`, as declared by the ticket non-goals.

## Static analysis and parity

The deterministic analysis record is
[`evidence/static-analysis/TICKET-007.md`](../../../evidence/static-analysis/TICKET-007.md).
The final diff is clean, the Gradle verification suite and Android lint pass,
and the optional `aidd@3.1.0` churn analyzer is unavailable locally. No
Android pull-request build/test pipeline is configured; the active workflow
only evaluates workflow contracts, so local-to-PR parity is
`notApplicable`.

## Architecture and scope

- `ForegroundSessionContract.kt` owns pure command and lifecycle state types;
  action constants no longer require loading the Android service
  implementation.
- `BreathingSessionService` owns Android lifecycle continuity, notification
  channel creation, foreground ownership, explicit actions, and cleanup.
- The service is non-exported, non-sticky, and only activates from a
  user-started explicit action.
- Invalid commands and foreground activation failures become explicit failed
  state, with no success-shaped fallback.
- No mandatory holds, forced breathing, health sensing, medical claim,
  network, account, analytics, persistence, or hidden background behavior was
  added.

## Findings

| Severity | Finding | Disposition |
|---|---|---|
| Warning | Optional churn analyzer is unavailable | Accepted with coverage note; no package was installed |
| Warning | Google Play review of the `specialUse` service remains unresolved | Accepted as a later release-policy gate; local APK/service validation is not represented as Play approval |

No introduced correctness, safety, privacy, security, scope, or architecture
defect was found within the ticket boundary.

## Readiness decision

**Result:** Ready to proceed to configured delivery handling for this ticket.

The ticket has terminal baseline, implementation, unit, lint, APK,
application-boundary functionality, automatic-validation, static-analysis, and
review evidence. The remaining warnings are explicitly recorded and do not
block this local commit.
