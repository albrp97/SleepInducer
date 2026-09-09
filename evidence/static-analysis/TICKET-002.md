# Static Analysis Record - TICKET-002

**Run ID:** `TICKET-002-final-diff`
**Mode:** diff
**Scope:** Compose setup boundary, resource copy, and instrumentation tests
**Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
`all-validation`
**Date:** 2026-09-09

## Deterministic tools

| Category | Tool | Command | Result | Report |
|---|---|---|---|---|
| Android lint | Android Gradle Plugin lint | `./gradlew lintDebug --offline` | Passed; no errors or warnings | `app/build/reports/lint-results-debug.html` |
| Kotlin/compiler | Kotlin Gradle compilation | `./gradlew assembleDebug --offline` | Passed | `app/build/outputs/apk/debug/app-debug.apk` |
| Unit task | Gradle test task | `./gradlew test --offline` | Passed with concern; no unit source set yet | Gradle terminal output |
| Churn | `aidd churn` | `npx --no-install aidd churn --json --days 90 --top 20 --min-loc 50` | Skipped with reason; package unavailable locally and installation was not authorized | No report |

## Review scope

- `app/src/main/java/com/sleepinducer/app/MainActivity.kt`
- `app/src/main/res/values/strings.xml`
- `app/src/androidTest/java/com/sleepinducer/app/SetupFlowTest.kt`
- Related ticket and README documentation

## Findings

- No Android lint findings were introduced.
- No Kotlin compilation errors were introduced.
- The Compose layout keeps setup presentation in the activity boundary and
  leaves breathing timing and service policy outside this ticket.
- The safety and offline copy is resource-backed and visible through the
  instrumentation semantics tree.
- Churn is optional and unavailable. This is a review-depth coverage gap, not
  functional evidence.

## Parity

**Status:** notApplicable

**Reason:** no Android pull-request workflow or provider pipeline is configured
or discoverable yet. The existing `.github/workflows/workflow-evals.yml` is
workflow-evaluation automation, not an Android build pipeline.

## Status

**Overall:** passedWithConcerns

The required local Android build, lint, and emulator functionality checks are
terminal. The accepted concerns are the intentionally empty unit source set
and unavailable optional churn tool.
