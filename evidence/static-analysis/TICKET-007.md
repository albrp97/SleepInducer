# Static Analysis - TICKET-007

**Run mode:** diff
**Scope:** foreground-service production and test sources, manifest/resource
changes, and related planning/evidence records
**Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
`all-validation`
**Status:** passedWithConcerns
**Local-to-PR parity:** notApplicable

## Tool results

| Category | Tool | Command | Exit | Status | Findings |
|---|---|---|---:|---|---|
| Formatting hygiene | Git | `git diff --check` | 0 | passed | None |
| Kotlin compilation and unit tests | Gradle 8.9 wrapper | `./gradlew test lintDebug assembleDebug connectedDebugAndroidTest --offline` | 0 | passed | 22 debug and 22 release unit tests passed |
| Android lint | Android Gradle Plugin lint | `./gradlew test lintDebug assembleDebug connectedDebugAndroidTest --offline` | 0 | passed | No errors or warnings |
| APK build | Gradle Android build | `./gradlew test lintDebug assembleDebug connectedDebugAndroidTest --offline` | 0 | passed | `app/build/outputs/apk/debug/app-debug.apk` |
| Android functionality | Android instrumentation runner | `./gradlew test lintDebug assembleDebug connectedDebugAndroidTest --offline` | 0 | passed | 27 tests passed, 0 failures, 0 skips on `emulator-5554` API 35 |
| Hotspot analysis | `aidd@3.1.0` | `npx --no-install aidd churn --json` | 1 | skippedWithReason | Package unavailable locally; no installation performed |

## Review classification

- No introduced correctness, safety, privacy, security, scope, or dependency
  boundary finding was observed in the changed Kotlin, manifest, or resource
  surfaces.
- The service is non-exported, non-sticky, explicitly user-started, and
  rejects invalid starts without exposing an active state.
- The optional churn analyzer is unavailable and remains an accepted coverage
  warning rather than a functional or readiness pass.
- The repository has an active workflow-evaluation workflow but no Android
  pull-request build/test pipeline, so local-to-PR parity is
  `notApplicable`.

## Raw artifacts

- `app/build/reports/lint-results-debug.html`
- `app/build/outputs/apk/debug/app-debug.apk`
- `app/build/reports/androidTests/connected/debug/index.html`

## Final rerun after review cleanup

The command-boundary cleanup moved service action constants into the
Android-independent contract and was followed by a complete rerun:

- `git diff --check` exited `0`.
- `./gradlew test lintDebug assembleDebug connectedDebugAndroidTest --offline`
  exited `0`, with 22 debug unit tests, 22 release unit tests, 27 Android
  instrumentation tests, clean lint, and a rebuilt debug APK.
- `npx --no-install aidd churn --json` remained unavailable because the
  configured optional `aidd@3.1.0` package is not installed locally.
