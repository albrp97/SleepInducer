# Static Analysis - TICKET-011 v0.1.2 Release

**Run date:** 2026-09-24
**Run mode:** diff
**Scope:** v0.1.2 metadata, release documentation, ticket change-control
record, and delivery evidence
**Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high /
`all-validation`
**Status:** passedWithConcerns
**Android local-to-PR parity:** notApplicable; the repository has no Android
build, lint, or test PR job. The separate `workflow-evals.yml` job references
missing evaluation files and does not validate Android changes.

## Tool results

| Category | Tool | Command | Exit | Status | Findings |
|---|---|---|---:|---|---|
| Formatting hygiene | Git 2.55.0 | `git diff --check` | 0 | passed | None |
| Build and unit tests | Gradle 8.9 / Kotlin 2.0.21 | `./gradlew test lintDebug assembleRelease --offline --no-daemon` | 0 | passed | Release and debug builds succeeded |
| Android lint | Android Gradle Plugin Lint 8.7.3 | `./gradlew test lintDebug assembleRelease --offline --no-daemon` | 0 | passedWithConcerns | Four existing warnings in unchanged `strings.xml` |
| Android functionality | Android instrumentation runner | `./gradlew connectedDebugAndroidTest --offline --no-daemon` | 0 | passed | 42 tests passed on API 35 |
| Workflow syntax | PyYAML 6.0.3 / Bash 5.3.15 | Parse `.github/workflows/release-apk.yml`; run `bash -n` on each embedded script | 0 | passed | YAML parsed and six scripts passed |
| Release contract | Android SDK Build Tools 35.0.0 | Verify APK package, version name/code, signature, and v0.1.2 contract | 0 | passed | `com.sleepinducer.app`, `0.1.2`, code `3`, pinned signer |
| Hotspot analysis | `aidd@3.1.0` | `npx --no-install aidd churn --json` | 1 | skippedWithReason | Optional package unavailable; no installation attempted |

## Lint findings

| Rule | Severity | Location | Message | Baseline |
|---|---|---|---|---|
| `UnusedResources` | Warning | `app/src/main/res/values/strings.xml:8` | `R.string.setup_next_step_title` appears unused | Existing |
| `UnusedResources` | Warning | `app/src/main/res/values/strings.xml:9` | `R.string.setup_next_step_body` appears unused | Existing |
| `TypographyEllipsis` | Warning | `app/src/main/res/values/strings.xml:38` | Replace `...` with the ellipsis character | Existing |
| `UnusedResources` | Warning | `app/src/main/res/values/strings.xml:71` | `R.string.service_notification_active` appears unused | Existing |

All four warnings are present in the approved baseline and the source file is
unchanged by this diff. They are retained in
`evidence/static-analysis/baseline.json`; none was suppressed or removed.

## Review classification

- No introduced Android lint finding remains in the v0.1.2 diff.
- The optional churn coverage gap is accepted under the configured optional
  policy and is not represented as a clean churn result.
- Workflow syntax and the tag-derived APK version contract passed.
- The tag-triggered hosted v0.1.2 workflow and release asset are not covered
  by this local static-analysis run and remain pending separate remote
  verification.

## Artifacts

- `evidence/static-analysis/baseline.json`
- `evidence/static-analysis/TICKET-011-v0.1.2-release.json`
- `evidence/static-analysis/TICKET-011-v0.1.2-release.sarif`
- `app/build/reports/lint-results-debug.xml`
- `app/build/reports/lint-results-debug.html`
