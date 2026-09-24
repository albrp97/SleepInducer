# Static Analysis - TICKET-011 Signed Release

**Run date:** 2026-09-24
**Run mode:** diff
**Scope:** release signing configuration, tag-triggered APK publishing,
version `0.1.1`, release documentation, and delivery evidence
**Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
`all-validation`
**Status:** passedWithConcerns
**Local-to-PR parity:** notApplicable; the repository has no Android
pull-request build or test pipeline

## Tool results

| Category | Tool | Command | Exit | Status | Findings |
|---|---|---|---:|---|---|
| Formatting hygiene | Git 2.55.0 | `git diff --check` | 0 | passed | None |
| Build and type correctness | Gradle 8.9 / Kotlin 2.0.21 | `./gradlew test lintDebug assembleRelease --offline --no-daemon --console=plain` | 0 | passed | Release and debug Kotlin compilation completed |
| Android lint | Android Gradle Plugin Lint 8.7.3 | `./gradlew test lintDebug assembleRelease --offline --no-daemon --console=plain` | 0 | passedWithConcerns | Four existing warnings, all in unchanged `strings.xml` |
| Release workflow syntax | PyYAML 6.0.3 / Bash 5.3.15 | Parse workflow YAML and run `bash -n` on each embedded script | 0 | passed | YAML parsed and all six scripts passed syntax validation |
| Release version contract | Android SDK Build Tools 35.0.0 | Compare `apkanalyzer manifest version-name` with the `v`-stripped release tag | 0 | passed | `v0.1.1` matches APK version `0.1.1` |
| Hotspot analysis | `aidd@3.1.0` | `npx --no-install aidd churn --json` | 1 | skippedWithReason | Optional tool unavailable locally; no installation performed |

## Lint findings

| Rule | Severity | Location | Message | Baseline |
|---|---|---|---|---|
| `UnusedResources` | Warning | `app/src/main/res/values/strings.xml:8` | `R.string.setup_next_step_title` appears unused | Existing |
| `UnusedResources` | Warning | `app/src/main/res/values/strings.xml:9` | `R.string.setup_next_step_body` appears unused | Existing |
| `TypographyEllipsis` | Warning | `app/src/main/res/values/strings.xml:38` | Replace `...` with the ellipsis character | Existing |
| `UnusedResources` | Warning | `app/src/main/res/values/strings.xml:71` | `R.string.service_notification_active` appears unused | Existing |

All four findings are present in the prior TICKET-009 lint review and their
source file is unchanged by this diff. They are retained in
`evidence/static-analysis/baseline.json`, not suppressed or removed.

## Review classification

- No introduced Android lint finding remains.
- The optional churn coverage gap does not block this analysis.
- The signed APK's version metadata matches the selected `v0.1.1` release.
- No Android pull-request pipeline was found, so parity is not applicable.

## Artifacts

- `evidence/static-analysis/baseline.json`
- `evidence/static-analysis/TICKET-011-signed-release.json`
- `evidence/static-analysis/TICKET-011-signed-release.sarif`
- `app/build/reports/lint-results-debug.xml`
- `app/build/reports/lint-results-debug.html`
