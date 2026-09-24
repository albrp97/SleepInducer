# Static Analysis - TICKET-011 v0.1.2 Release (Final Local Diff)

**Run date:** 2026-09-24
**Run mode:** diff
**Scope:** v0.1.2 metadata, release-workflow version-code assertion, release
documentation, ticket change-control record, and delivery evidence
**Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
`all-validation`
**Status:** passedWithConcerns
**Android local-to-PR parity:** notApplicable; no Android build, lint, or test
job is configured for PRs. The separate `workflow-evals.yml` workflow
references missing evaluation files and does not validate Android changes.

## Tool results

| Category | Tool | Command | Exit | Status | Findings |
|---|---|---|---:|---|---|
| Formatting hygiene | Git 2.55.0 | `git diff --check` | 0 | passed | None |
| Build and unit tests | Gradle 8.9 / Kotlin 2.0.21 | `./gradlew test lintDebug assembleRelease --offline --no-daemon` | 0 | passed | Signed debug/release build completed |
| Android lint | Android Gradle Plugin Lint 8.7.3 | `./gradlew test lintDebug assembleRelease --offline --no-daemon` | 0 | passedWithConcerns | Four existing warnings in unchanged `strings.xml` |
| Android functionality | Android instrumentation runner | `./gradlew connectedDebugAndroidTest --offline --no-daemon` | 0 | passed | 42 tests passed on API 35 |
| Release workflow contract | PyYAML 6.0.3 / Bash 5.3.15 | Parse release YAML, assert version-code check, run `bash -n` on embedded scripts | 0 | passed | Version code `3` is asserted; six scripts passed |
| APK contract and signature | Android SDK Build Tools 35.0.0 | `apkanalyzer` package/version checks and `apksigner verify` | 0 | passed | Package, `0.1.2`, code `3`, pinned signer |
| Hotspot analysis | `aidd@3.1.0` | `npx --no-install aidd churn --json` | 1 | skippedWithReason | Optional package unavailable; no installation attempted |

## Lint findings

| Rule | Severity | Location | Message | Baseline |
|---|---|---|---|---|
| `UnusedResources` | Warning | `app/src/main/res/values/strings.xml:8` | `R.string.setup_next_step_title` appears unused | Existing |
| `UnusedResources` | Warning | `app/src/main/res/values/strings.xml:9` | `R.string.setup_next_step_body` appears unused | Existing |
| `TypographyEllipsis` | Warning | `app/src/main/res/values/strings.xml:38` | Replace `...` with the ellipsis character | Existing |
| `UnusedResources` | Warning | `app/src/main/res/values/strings.xml:71` | `R.string.service_notification_active` appears unused | Existing |

All four warnings are present in the approved baseline and the source file is
unchanged. They remain visible in
`evidence/static-analysis/baseline.json`; none was suppressed or removed.

## Review classification

- No introduced Android lint finding remains in the final local diff.
- The release workflow now verifies the required version code in addition
  to package ID and tag-derived version name.
- The optional churn coverage gap is accepted under the configured optional
  policy; it is not represented as a clean churn result.
- The API 35 hosted workflow and release asset are not covered by local
  analysis and remain pending remote verification.

## Artifacts

- `evidence/static-analysis/baseline.json`
- `evidence/static-analysis/TICKET-011-v0.1.2-release-final.json`
- `evidence/static-analysis/TICKET-011-v0.1.2-release-final.sarif`
- `app/build/reports/lint-results-debug.xml`
- `app/build/reports/lint-results-debug.html`
- `app/build/reports/androidTests/connected/debug/index.html`
