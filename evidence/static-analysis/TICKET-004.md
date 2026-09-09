# Static Analysis Record - TICKET-004

**Run ID:** `TICKET-004-final-diff`
**Mode:** diff
**Scope:** Android haptic adapter, manifest permission, unit tests, packaged
functionality tests, planning, and evidence
**Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
`all-validation`
**Date:** 2026-09-09

## Deterministic tools

| Category | Tool | Command | Result | Report |
|---|---|---|---|---|
| Android lint | Android Gradle Plugin lint | `./gradlew lintDebug --offline` | Passed; no errors or warnings | `app/build/reports/lint-results-debug.html` |
| Kotlin/compiler | Kotlin Gradle compilation | `./gradlew assembleDebug --offline` | Passed | `app/build/outputs/apk/debug/app-debug.apk` |
| Unit tests | Gradle test task | `./gradlew test --offline` | Passed; 9 tests in each debug/release unit variant | `app/build/test-results/` |
| Android functionality | Connected instrumentation | `./gradlew connectedDebugAndroidTest --offline` | Passed; 15 tests, 0 failures, 0 skips | `app/build/reports/androidTests/connected/debug/` |
| Manifest | Packaged manifest inspection | packaged debug manifest | Passed; `android.permission.VIBRATE` present | `app/build/intermediates/packaged_manifests/debug/processDebugManifestForPackage/AndroidManifest.xml` |
| Churn | `aidd churn` | `npx --no-install aidd churn --json --days 90 --top 20 --min-loc 50` | Skipped with reason; `aidd@3.1.0` unavailable locally and installation was not authorized | No report |

## Findings

- The haptic adapter has no Android dependency in its gateway contract, so
  supported, unavailable, and cancellation paths are unit-testable.
- `AndroidVibratorGateway` isolates API 31 `VibratorManager` resolution from
  the API 26-30 legacy service path.
- Delivery requires both a vibrator and amplitude control, and rejects
  out-of-policy cue values before platform delivery.
- The manifest contains the required vibration permission and no network
  permission was added.
- Churn is optional and unavailable. This is a review-depth coverage gap, not
  functional evidence.

## Parity

**Status:** notApplicable

**Reason:** no Android pull-request workflow or provider pipeline is configured
or discoverable yet. The existing workflow-evaluation automation is not an
Android build pipeline.

## Status

**Overall:** passedWithConcerns

Required local Android verification and packaged haptic functionality are
terminal. The accepted concerns are unavailable optional churn analysis and
physical-device comfort validation deferred to release readiness.
