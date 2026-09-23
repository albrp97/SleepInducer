# Static Analysis - TICKET-009 Safe Session Controls

**Run date:** 2026-09-14
**Run mode:** diff
**Scope:** screen-off service continuity, bounded wake-lock ownership, system
light/dark theme resources, 0–20 minute slider controls, updated setup guide,
tests, README, and planning/evidence synchronization
**Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
`all-validation`
**Status:** blocked
**Local-to-PR parity:** notApplicable; no Android pull-request build/test
pipeline is configured

## Tool results

| Category | Tool | Command | Exit | Status | Findings |
|---|---|---|---:|---|---|
| Formatting hygiene | Git | `git diff --check` | 0 | passed | None |
| Unit tests | Gradle wrapper | `./gradlew --offline test --quiet` | 0 | passed | Unit test suite completed successfully |
| Android lint | Android Gradle Plugin lint | `./gradlew --offline lintDebug --quiet` | 0 | passedWithConcerns | Zero errors; four existing warnings for unused resources and typography |
| Debug APK | Gradle Android build | `./gradlew --offline assembleDebug --quiet` | 0 | passed | Debug APK assembled |
| Android test APK | Gradle Android build | `./gradlew --offline assembleDebugAndroidTest --quiet` | 0 | passed | Functionality tests compiled |
| Android functionality | Android instrumentation runner | `./gradlew --offline connectedDebugAndroidTest --quiet` | 1 | blocked | No connected device and no configured AVD |

## Review classification

- The service wake lock is bounded to the selected session duration plus a
  one-minute grace period and is released through shared terminal cleanup.
- The custom duration range is represented consistently by the domain model,
  Compose slider, service command parser, persistence, and functionality test.
- The light/dark platform navigation-bar attributes are version-qualified for
  the API 26 minimum SDK.
- No new lint error or whitespace finding was introduced.
- The required packaged functionality gate is blocked by unavailable Android
  infrastructure and is not converted into a pass.

## Raw artifacts

- `app/build/outputs/apk/debug/app-debug.apk`
- `app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk`
- `app/build/reports/lint-results-debug.html`
