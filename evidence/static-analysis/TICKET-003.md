# Static Analysis Record - TICKET-003

**Run ID:** `TICKET-003-final-diff`
**Mode:** diff
**Scope:** pure Kotlin breathing protocol, unit tests, instrumentation
  acceptance tests, and Gradle test dependency
**Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
`all-validation`
**Date:** 2026-09-09

## Deterministic tools

| Category | Tool | Command | Result | Report |
|---|---|---|---|---|
| Android lint | Android Gradle Plugin lint | `./gradlew lintDebug --offline` | Passed; no errors or warnings | `app/build/reports/lint-results-debug.html` |
| Kotlin/compiler | Kotlin Gradle compilation | `./gradlew assembleDebug --offline` | Passed | `app/build/outputs/apk/debug/app-debug.apk` |
| Unit tests | Gradle test task | `./gradlew test --offline` | Passed; protocol unit tests execute | `app/build/test-results/testDebugUnitTest/` |
| Android functionality | Connected instrumentation | `./gradlew connectedDebugAndroidTest --offline` | Passed; 9 tests, 0 failures, 0 skips | `app/build/reports/androidTests/connected/debug/` |
| Churn | `aidd churn` | `npx --no-install aidd churn --json --days 90 --top 20 --min-loc 50` | Skipped with reason; package unavailable locally and installation was not authorized | No report |

## Review scope

- `app/src/main/java/com/sleepinducer/app/breathing/BreathingProtocol.kt`
- `app/src/test/java/com/sleepinducer/app/breathing/BreathingProtocolTest.kt`
- `app/src/androidTest/java/com/sleepinducer/app/ProtocolFlowTest.kt`
- `app/build.gradle.kts`

## Findings

- No Android lint findings were introduced.
- No Kotlin compilation errors or warnings remain.
- The protocol is independent of Android classes and validates the no-hold,
  natural-depth contract at construction time.
- The unit and instrumentation tests cover phase boundaries, terminal-state
  idempotence, supported duration behavior, and the default comfort contract.
- Churn is optional and unavailable. This is a review-depth coverage gap, not
  functional evidence.

## Parity

**Status:** notApplicable

**Reason:** no Android pull-request workflow or provider pipeline is configured
or discoverable yet. The existing `.github/workflows/workflow-evals.yml` is
workflow-evaluation automation, not an Android build pipeline.

## Status

**Overall:** passedWithConcerns

The required local Android build, lint, unit, and emulator functionality checks
are terminal. The only accepted concern is unavailable optional churn
analysis.
