# Static Analysis Record - TICKET-005

**Run ID:** `TICKET-005-final-diff`
**Mode:** diff
**Scope:** pure Kotlin session runner, scheduler contract, unit tests,
packaged functionality tests, planning, and evidence
**Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
`all-validation`
**Date:** 2026-09-09

## Deterministic tools

| Category | Tool | Command | Result | Report |
|---|---|---|---|---|
| Android lint | Android Gradle Plugin lint | `./gradlew lintDebug --offline` | Passed; no errors or warnings | `app/build/reports/lint-results-debug.html` |
| Kotlin/compiler | Kotlin Gradle compilation | `./gradlew assembleDebug --offline` | Passed | `app/build/outputs/apk/debug/app-debug.apk` |
| Unit tests | Gradle test task | `./gradlew test --offline` | Passed; 13 tests in each debug/release unit variant | `app/build/test-results/` |
| Android functionality | Connected instrumentation | `./gradlew connectedDebugAndroidTest --offline` | Passed; 19 tests, 0 failures, 0 skips | `app/build/reports/androidTests/connected/debug/` |
| Churn | `aidd churn` | `npx --no-install aidd churn --json --days 90 --top 20 --min-loc 50` | Skipped with reason; `aidd@3.1.0` unavailable locally and installation was not authorized | No report |

## Findings

- The runner remains pure Kotlin and depends only on injected scheduler and
  listener contracts.
- Scheduling targets are derived from the monotonic session start and protocol
  elapsed boundaries, avoiding accumulated callback drift.
- Terminal transitions cancel scheduled work and stale callbacks cannot emit
  new states after stop, interruption, or completion.
- No Android service, vibration, persistence, network, health-data, or medical
  behavior was added.
- Churn is optional and unavailable. This is a review-depth coverage gap, not
  functional evidence.

## Parity

**Status:** notApplicable

**Reason:** no Android pull-request workflow or provider pipeline is configured
or discoverable yet. The existing workflow-evaluation automation is not an
Android build pipeline.

## Status

**Overall:** passedWithConcerns

Required local Android verification and packaged runner functionality are
terminal. The only accepted concern is unavailable optional churn analysis.
