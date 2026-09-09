---
agent: agent
description: "AIDD /commit — create a conventional commit only after staged scope, evidence, and configured delivery readiness checks pass."
---

# 💾 Commit

Act as a senior software engineer using
[aidd-commit](../skills/aidd-commit/SKILL.md), the repository's commit policy,
and [aidd-evidence](../skills/aidd-evidence/SKILL.md).
Resolve `delivery.development.mode` from `.github/aidd-config.yml`. Guided
commit readiness requires `userValidation`; automatic readiness requires
`automaticValidation`, the exact Rubber Duck `gpt-5.6-luna` high-reasoning
`all-validation` profile on every validation entry, and uses verified bootstrap
authorization
for the routine commit operation.

Commit message format:
`"$type${[(scope)]}{[!]}: $description"`

Types: `fix|feat|chore|docs|refactor|test|perf|build|ci|style|revert`

Process {
  1. Read `.github/aidd-config.yml`, the active ticket, evidence summary, and
     repository commit policy.
  2. Verify the dedicated ticket branch and intended base.
  3. Inspect the staged file list and staged diff; stage nothing implicitly.
  4. Check scope, secrets, debug artifacts, unrelated files, generated files,
     migrations, and required documentation.
  5. Confirm applicable agent-owned baseline, smoke, unit, regression, fixture,
     acquisition, contract, integration, migration, security, static-analysis,
     deployment, quality, and automated functionality checks have terminal
     evidence. Confirm that guided user-validation evidence records
     functionality only, or that automatic mode has terminal
     `automaticValidation` with the exact Rubber Duck profile, and that review
     prerequisites are terminal. Keep
     remote-only checks explicitly pending until the branch is published.
  6. Use repository-configured trailers, signing, author, and branch policy.
  7. Create a conventional commit with a first line no longer than 50
     characters and no CHANGELOG.md edits unless policy explicitly requires it.
  8. Record the commit ID, branch, committed paths, and next delivery action in
     the evidence record.
  9. End with exactly one `Next step`, `Skill`, and `Why` handoff. Recommend
     `/push` when the commit is unpublished or ahead of its upstream; otherwise
     recommend `/aidd-pr` when a published branch requires a PR, or configured
     closeout when neither push nor PR is required.
}

Constraints {
  Use non-interactive mode only.
  Do not commit unstaged or unrelated files.
  Do not commit when a required gate or evidence result is missing.
  Do not commit without current terminal automated functionality evidence for
    every acceptance outcome.
  Do not treat guided user validation as evidence that a technical check ran,
    and do not ask the user to run technical scripts.
  Do not commit while the mode-appropriate validation is pending, failed, or
    blocked.
  Do not commit automatic-mode work when any validation result lacks the exact
    Rubber Duck `gpt-5.6-luna` high-reasoning profile.
  Do not store credentials in commit messages or artifacts.
  Do not stage files implicitly.
  Do not push or create a PR as a side effect of committing.
  Respect repository-specific commit policy over generic defaults.
}
