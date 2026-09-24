# Static Analysis - TICKET-011 v0.1.2 Launch Workflow Correction

- **Run date:** 2026-09-24
- **Run mode:** diff
- **Scope:** Corrected API 35 release launch validation and accurate release-status documentation; no Android application source or version metadata changed.
- **Validation profile:** `rubber-duck` / `gpt-5.6-luna` / high / `all-validation`
- **Profile availability:** available
- **Status:** passedWithConcerns
- **Static-analysis parity:** notApplicable; no PR static-analysis job is configured. The separate PR workflow-evaluation job references files that are absent from the worktree.

## Tool results

| Category | Tool | Command or check | Result | Status |
|---|---|---|---|---|
| Formatting hygiene | Git 2.55.0 | `git diff --check` | Exit 0 | passed |
| Workflow syntax and regression | PyYAML 6.0.3 / GNU Bash 5.3.15 | Parse workflow YAML, test the extracted launch script with mock `adb` responses, and run `bash -n` on all six embedded scripts | YAML and all six shell blocks valid; healthy empty-output and `Status: ok` cases pass; both negative cases fail with diagnostics | passed |
| API 35 functionality | Android Emulator 37.1.11.0 / API 35 | Run the exact extracted launch script against the local signed v0.1.2 APK | Install succeeded; `Status: ok`, package process, and resumed `MainActivity` observed | passed |
| Optional static analyzers | actionlint, ShellCheck, yamllint | Availability check | Not installed; no installation attempted | skippedWithReason |
| Optional hotspot analysis | aidd CLI | Availability check | CLI unavailable; churn is configured optional | skippedWithReason |
| PR workflow evaluation | `workflow-evals.yml` | Discovered command: `python tools/eval_workflows.py --spec ai-evals/workflow-contracts.json --threshold 1.0` | `tools/eval_workflows.py` and `ai-evals/workflow-contracts.json` are absent; not run | skippedWithReason |

## Review classification

- No introduced static-analysis finding was observed in the corrected launch
  logic or release-status documentation.
- Empty `am start -W` output is now treated as inconclusive. Success still
  requires both a running package process and a resumed or top-resumed
  `com.sleepinducer.app/.MainActivity`.
- A nonzero launch command or failure to observe both signals remains fatal
  and emits activity/logcat diagnostics.
- The four existing Android lint warnings in unchanged
  `app/src/main/res/values/strings.xml` remain visible in EVID-031 through
  EVID-040.
- The optional analyzer gaps and unavailable PR workflow-evaluation files are
  coverage concerns, not passes.

## Delivery boundary

The immutable `v0.1.2` tag's hosted workflow failed before publication, and no
GitHub release or APK asset exists. This local analysis does not satisfy the
hosted release gate. A new version tag requires explicit authorization.

## Artifacts

- `evidence/static-analysis/TICKET-011-v0.1.2-launch-workflow-fix-final.json`
- `evidence/static-analysis/TICKET-011-v0.1.2-launch-workflow-fix-final.sarif`
- `evidence/static-analysis/TICKET-011-v0.1.2-launch-workflow-fix-final.md`
