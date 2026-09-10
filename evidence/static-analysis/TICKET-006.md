# Static Analysis - TICKET-006

**Run mode:** diff
**Scope:** changed Kotlin production/test sources and related planning/evidence
**Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
`all-validation`
**Status:** passedWithConcerns
**Local-to-PR parity:** notApplicable

## Tool results

| Category | Tool | Command | Exit | Status | Findings |
|---|---|---|---:|---|---|
| Formatting hygiene | Git | `git diff --check` | 0 | passed | None |
| Kotlin compilation and unit tests | Gradle 8.9 wrapper | `./gradlew test --offline` | 0 | passed | 18 debug and 18 release unit tests passed |
| Android lint | Android Gradle Plugin lint | `./gradlew lintDebug --offline` | 0 | passed | No errors or warnings |
| APK build | Gradle Android build | `./gradlew assembleDebug --offline` | 0 | passed | `app/build/outputs/apk/debug/app-debug.apk` |
| Android functionality | Android instrumentation runner | `./gradlew connectedDebugAndroidTest --offline` | 0 | passed | 23 tests passed, 0 failures, 0 skips |
| Hotspot analysis | `aidd@3.1.0` | Repository-configured optional churn analysis | N/A | skippedWithReason | Package unavailable locally; no installation performed |

## Review classification

- No introduced correctness, safety, privacy, security, scope, or dependency
  boundary finding was observed in the changed Kotlin surfaces.
- The coordinator remains Android-independent and depends on the existing
  capability-aware adapter contract.
- The absence of optional churn analysis is an accepted coverage warning, not a
  functional or readiness pass.
- No Android pull-request pipeline is configured or discoverable, so parity is
  `notApplicable`.

## Raw artifacts

- `app/build/reports/lint-results-debug.html`
- `app/build/outputs/apk/debug/app-debug.apk`
- `app/build/reports/androidTests/connected/debug/index.html`
