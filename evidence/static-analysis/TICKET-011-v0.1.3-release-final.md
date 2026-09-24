# Static Analysis - TICKET-011 v0.1.3 Release Candidate

- **Run date:** 2026-09-24
- **Run mode:** diff
- **Scope:** Approved v0.1.3/versionCode 4 target, exact release workflow version assertion, release documentation, and TICKET-011 change-control/evidence updates.
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high / `all-validation`
- **Profile availability:** available
- **Status:** passedWithConcerns
- **Static-analysis parity:** notApplicable; no PR static-analysis job is configured. The separate PR workflow-evaluation job references absent files.

## Tool results

| Category | Tool | Command or check | Result | Status |
|---|---|---|---|---|
| Formatting hygiene | Git 2.55.0 | `git diff --check` | Exit 0 | passed |
| Build and unit tests | Gradle Wrapper 8.9 | `./gradlew test lintDebug assembleRelease --offline --no-daemon` | Build successful; unit-test tasks were up-to-date | passed |
| Android lint | Android Gradle Plugin Lint 8.7.3 | Same Gradle command | Four existing warnings in unchanged `strings.xml` | passedWithConcerns |
| Android functionality suite | AndroidJUnitRunner | `./gradlew connectedDebugAndroidTest --offline --no-daemon` | API 35; 42 tests passed, zero failures/skips | passed |
| Workflow contract | PyYAML 6.0.3 / GNU Bash 5.3.15 | Parse YAML; assert version name/code; `bash -n` all embedded blocks | Six Bash blocks valid; v0.1.3/code 4 contract matches | passed |
| APK signature and metadata | Android SDK Build Tools 35.0.0 | Exact release workflow verification block | Pinned signature, package, version name and code passed | passed |
| Release install/launch | Android Emulator 37.1.11.0 / API 35 | Exact release workflow install/launch block | Install succeeded; process and resumed MainActivity observed | passed |
| Optional analyzers | actionlint, ShellCheck, yamllint | Availability check | Unavailable; no installation attempted | skippedWithReason |
| Optional hotspot analysis | aidd CLI | Availability check | CLI unavailable; churn is configured optional | skippedWithReason |
| PR workflow evaluation | `workflow-evals.yml` | Discovered evaluator command | `tools/eval_workflows.py` and `ai-evals/workflow-contracts.json` are absent | skippedWithReason |

## Existing Android lint findings

| Rule | Severity | Location | Message | Baseline |
|---|---|---|---|---|
| `UnusedResources` | Warning | `app/src/main/res/values/strings.xml:8` | `R.string.setup_next_step_title` appears unused | Existing |
| `UnusedResources` | Warning | `app/src/main/res/values/strings.xml:9` | `R.string.setup_next_step_body` appears unused | Existing |
| `TypographyEllipsis` | Warning | `app/src/main/res/values/strings.xml:38` | Replace three periods with the ellipsis character | Existing |
| `UnusedResources` | Warning | `app/src/main/res/values/strings.xml:71` | `R.string.service_notification_active` appears unused | Existing |

No introduced lint finding was observed. All four warnings remain visible and
the resource file is unchanged.

## Review classification

- The APK reports package `com.sleepinducer.app`, version `0.1.3`, and version
  code `4`, and verifies against the pinned release certificate.
- The corrected launch step installs the signed APK on API 35 and verifies
  both a running process and resumed main activity.
- The local Downloads APK is byte-identical to the verified release build.
- The immutable v0.1.2 tag remains unchanged. No v0.1.3 tag or GitHub release
  has been created yet; local checks do not substitute for hosted validation.

## Artifacts

- `evidence/static-analysis/TICKET-011-v0.1.3-release-final.json`
- `evidence/static-analysis/TICKET-011-v0.1.3-release-final.sarif`
- `evidence/static-analysis/TICKET-011-v0.1.3-release-final.md`
- `app/build/reports/lint-results-debug.xml`
- `app/build/reports/androidTests/connected/debug/index.html`
- `app/build/outputs/apk/release/app-release.apk`
- `/home/ghiki/Downloads/sleep-inducer-release.apk`
