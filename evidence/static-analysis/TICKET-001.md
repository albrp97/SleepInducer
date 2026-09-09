# Static Analysis Record - TICKET-001

**Run ID:** `TICKET-001-final-diff`
**Mode:** diff
**Scope:** Android build foundation and launch boundary
**Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
`all-validation`
**Date:** 2026-09-08

## Deterministic tools

| Category | Tool | Command | Result | Report |
|---|---|---|---|---|
| Android lint | Android Gradle Plugin lint | `./gradlew lintDebug --offline` | Passed; no errors or warnings | `app/build/reports/lint-results-debug.html` |
| Kotlin/compiler | Kotlin Gradle compilation | `./gradlew assembleDebug --offline` | Passed | `app/build/outputs/apk/debug/app-debug.apk` |
| Unit task | Gradle test task | `./gradlew test --offline` | Passed with concern; no unit source set yet | Gradle terminal output |
| Churn | `aidd churn` | `npx --no-install aidd churn --json --days 90 --top 20 --min-loc 50` | Skipped with reason; package unavailable locally and installation was not authorized | No report |

## Parity

**Status:** notApplicable
**Reason:** no Android pull-request workflow or provider pipeline is configured
or discoverable yet. The existing `.github/workflows/workflow-evals.yml` is
workflow-evaluation automation, not an Android build pipeline.

## Findings

- No introduced Android lint findings.
- No introduced compiler errors.
- No applicable formatter, dependency-cruiser, JavaScript/TypeScript type
  check, or repository-native security analyzer was present for this Kotlin
  Android change.
- Churn is optional and unavailable. This is a review-depth coverage gap, not
  functional evidence.

## Status

**Overall:** passedWithConcerns

The required local Android lint and build checks are terminal. The concern is
limited to the intentionally empty unit source set and unavailable optional
churn tool. Neither replaces the passing emulator-backed functionality tests.
