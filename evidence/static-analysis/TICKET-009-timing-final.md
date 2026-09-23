# Static Analysis - TICKET-009 Timing Controls

**Run date:** 2026-09-23
**Run mode:** diff
**Scope:** six-second inhale and exhale defaults, independent half-second
sliders, service propagation, session runner timing, functionality tests, and
related documentation
**Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
`all-validation`
**Status:** passedWithConcerns
**Local-to-PR parity:** notApplicable; repository discovery found no Android
pull-request build or test pipeline

## Tool results

| Category | Tool | Command | Exit | Status | Findings |
|---|---|---|---:|---|---|
| Formatting hygiene | Git | `git diff --check` | 0 | passed | None |
| Unit tests, lint, and APK | Gradle wrapper | `./gradlew test lintDebug assembleDebug --offline` | 0 | passed | 32 debug and 32 release unit tests passed; lint reported zero issues; debug APK assembled |
| Android functionality | Gradle wrapper and API 35 emulator | `./gradlew connectedDebugAndroidTest --offline` | 0 | passed | 42 tests passed with zero failures or skips on `emulator-5554 - 15` |
| Hotspot analysis | `aidd@3.1.0` | `npx --no-install aidd churn --json` | 1 | skippedWithReason | Package unavailable locally; no installation performed |
| Android pull-request parity | Repository CI discovery | Repository workflow/manifests | — | notApplicable | No Android pull-request build/test pipeline is configured |

## Review classification

- No introduced static-analysis finding remains after replacing the new
  primitive Compose timing state with `mutableLongStateOf`.
- The optional churn coverage gap does not block the configured review because
  `delivery.gates.churn` is optional.
- The Android application boundary is covered by the terminal API 35
  functionality run rather than by a unit-only proxy.

## Raw artifacts

- `app/build/outputs/apk/debug/app-debug.apk`
- `app/build/reports/lint-results-debug.html`
- `app/build/reports/androidTests/connected/debug/index.html`
