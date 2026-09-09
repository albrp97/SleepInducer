---
name: aidd-project-bootstrap
description: Bootstrap a repository's durable project context by combining intent discovery, vision, agent guidance, repository mapping, scope, and delivery configuration. Use when starting a project or when its foundational context is missing or inconsistent.
---

# aidd-project-bootstrap

Create a trustworthy project context before capability, phase, feature, or
ticket planning. This skill orchestrates the existing vision, discovery,
repository-map, and planning-bootstrap skills; it does not replace their
contracts or silently generate downstream delivery work.

Apply [../development-mode.md](../development-mode.md) when selecting and
persisting the project's development mode.

## Contract

```sudolang
ProjectBootstrapContract {
  mode: draft | status | write
  developmentMode: guided | automatic
  automaticValidationProfile
  inputs[]
  proposedArtifacts[]
  filesRead[]
  filesWritten[]
  writeResults[]
  approvalConditions[]
  stopConditions[]
  handoff[]
  bootstrapAuthorized
  blockers[]
  mayImplement = false
  mayCommit = false
  mayPush = false
  mayMerge = false
}
```

## Use when

- A new repository needs durable project context before implementation
  planning.
- `vision.md`, `AGENTS.md`, the repository map, scope record, or delivery
  configuration is absent, stale, or contradictory.
- The user can describe the project intent but the repository does not yet
  contain a reliable handoff for future agents.
- Several foundational files need to be drafted together without duplicating
  interviews or creating conflicting sources of truth.

For an existing project with trustworthy context, use the narrower planning
skill that matches the requested layer instead.

## Inputs

- The repository root and observed repository files, structure, manifests,
  CI, contribution guidance, and existing planning artifacts.
- `.github/aidd-config.yml` when present. Resolve
  `delivery.artifacts.agent_instructions` or default to `AGENTS.md`, and
  `delivery.artifacts.project_readme` or default to `README.md`.
- The user's description of what the project is, who it serves, the problem it
  solves, and the intended outcome.
- The required development-mode choice: `guided` preserves interactive
  approvals and user functionality validation; `automatic` continues through
  the configured workflow after bootstrap without asking or waiting for the
  user.
- Existing `vision.md`, `AGENTS.md`, `README.md`, repository maps, scope
  records, and configuration. Treat unknown or inferred values as unresolved.

## Outputs

In `draft` or `status` mode, the skill produces a report only. In `write` mode,
or after the default flow receives explicit approval, it must persist the
approved artifacts with repository file operations and verify each resulting
path. A file body printed in the response is never evidence that an artifact
was created.

The skill may create or update the following durable context:

- **Vision:** `vision.md`, using the `create-vision` contract, for project
  purpose, users, goals, non-goals, durable constraints, principles, and
  success criteria.
- **Agent guidance:** `AGENTS.md` or the configured equivalent, for
  repository-specific setup, commands, architecture boundaries, validation,
  security rules, and agent operating conventions. It must complement rather
  than duplicate `.github/copilot-instructions.md`.
- **Repository map:** the configured map path, using
  `aidd-create-repository-map`, for source, test, documentation, automation,
  infrastructure, ownership, tooling, and unknown surfaces.
- **Scope handoff:** the configured scope artifact, using the
  `aidd-product-manager` discovery contract, for the current horizon,
  exclusions, dependencies, risks, protected behavior, and verification
  intent.
- **Delivery configuration:** an authorized, secret-free update to
  `.github/aidd-config.yml` when project-specific commands, provider, branch,
  artifact settings, or `delivery.development.mode` are confirmed. The
  machine-readable mode is canonical.
- **README:** the configured project README only when it is missing or the
  user explicitly requests it. A README is human-facing onboarding, not a
  substitute for vision or agent guidance.

The selected mode must also be mirrored in `vision.md` and project-specific
`AGENTS.md` (or the configured equivalent), with an explicit reference to
`.github/aidd-config.yml` and `delivery.development.mode` as the source of
truth. When automatic mode is selected, mirror the exact
`delivery.development.automatic_validation` profile:
`rubber-duck`, `gpt-5.6-luna`, high reasoning, and `all-validation`.

The result reports the actual `created`, `updated`, `unchanged`, `skipped`,
`unresolved`, and `blocked` artifacts, including their repository-relative
paths and post-write verification status. It does not create capability,
phase, feature, or ticket children as a side effect.

```sudolang
ArtifactWriteResult {
  path
  operation: created | updated | unchanged | skipped | blocked
  verified
  sourceBasis[]
  reason
}
```

## Process

```sudolang
projectBootstrap(request) => BootstrapReport {
  1. inspect repository and existing sources of truth
  2. resolve configured artifact paths and identify conflicts or missing context
  3. reuse existing facts and ask only the unanswered intent, delivery, and
     development-mode questions
  4. draft every proposed file and show inferred values, TBDs, selected mode,
     and overwrite risks
  5. wait for explicit approval of the complete foundational draft
  6. enter write mode and use repository file operations for every approved artifact
  7. re-read every written path and record an ArtifactWriteResult; stop if verification fails
  8. resolve and persist the mode in configuration, vision, and AGENTS before
     reporting bootstrap completion
  9. run planning-bootstrap reconciliation and report the next planning layer
  10. if the selected mode is automatic, immediately hand off to the
      orchestrator's automatic development loop; do not ask or wait for another
      user response
}
```

### Inspect and reconcile

Read the repository root, README, AGENTS guidance, manifests, source layout,
tests, CI, contribution rules, `vision.md`, `.github/aidd-config.yml`, and
configured planning artifacts. Preserve an existing authoritative issue
tracker, project board, `plan/`, or `docs/` layout instead of creating a
parallel source of truth.

Classify each fact as observed, user-confirmed, inferred, stale, conflicting,
or unknown. Never convert a filename, dependency, or convention guess into a
project rule without evidence or confirmation.

### Interview

Ask one focused question at a time and reuse answers across all artifacts.
Cover, as applicable:

1. Who is the primary user and what problem or opportunity does the project
   address?
2. What outcome should the project create, and how will success be measured?
3. What is included in the current horizon, and what must remain out of scope?
4. What must the project never become or do?
5. Which technical, legal, organizational, environment, security, or support
   constraints are durable?
6. Which setup, quality, security, deployment, rollback, observability, and
   evidence expectations must agents preserve?
7. Which repository commands, architecture boundaries, naming rules, and
   contribution practices are confirmed rather than inferred?
8. Which development mode should govern work after this bootstrap?
   - `guided`: retain approval gates and the functionality-only user
     validation handoff.
   - `automatic`: after the foundational bootstrap is approved and verified,
     continue through mapping, planning, implementation, verification,
     review, delivery, and remaining phases without asking or waiting.

Do not ask questions whose answers are already supported by repository evidence
unless the fact is consequential and needs user confirmation.

### Draft and review

Present a consolidated draft before writing. For every proposed artifact,
show its path, purpose, source basis, changed sections, confidence, unresolved
TBDs, and any existing content that would be preserved or replaced.

The `AGENTS.md` draft should contain only project-specific agent guidance:

- links to `vision.md`, the repository map, and authoritative planning sources;
- the selected development mode, with
  `.github/aidd-config.yml:delivery.development.mode` as the canonical source;
- the automatic validation profile, with
  `.github/aidd-config.yml:delivery.development.automatic_validation` as the
  canonical source when automatic mode is selected;
- the automatic-mode rule that no user questions or validation waits occur
  after the verified bootstrap handoff, when automatic mode is selected;
- confirmed setup, format, lint, type-check, build, test, and validation
  commands;
- repository structure and layer boundaries grounded in the map;
- required evidence, security, data-handling, and contribution rules;
- instructions for handling uncertainty, scope, and existing sources of truth.

Do not copy the entire generic workflow into `AGENTS.md`, and do not put
temporary ticket branches, individual reviewers, credentials, tokens, or
one-off delivery choices into durable files.

### Write after approval

After explicit approval, enter `write` mode and use the repository's file
creation/editing tool for every approved path. Do not replace this step with
printing the proposed contents in the response. Create missing parent
directories when needed, preserve unrelated existing content, and write only
the approved files in this order:

1. `vision.md` through the `create-vision` rules.
2. Repository map through `aidd-create-repository-map`.
3. Scope and discovery handoff through `aidd-product-manager`.
4. `AGENTS.md` or the configured agent-instructions path.
5. Authorized configuration updates and optional README changes.

If an existing file is present, preserve its history and content not covered by
the approved change. If the requested change would replace or merge
contradictory instructions, stop and report the exact conflict.

After each write, re-read the path (or use the repository's equivalent
post-write inspection) and verify that the approved content is present. Record
the operation and verification in `writeResults[]`. If a file operation fails,
the artifact is `blocked`; do not report it as created or updated.
For every `created` or `updated` result, the execution must include a call to
the host's file create/edit tool. A code block, patch shown in the response, or
natural-language claim is not a file operation. Creating a missing parent
directory is part of the approved write and must also be reported and verified.

### Draft, status, and write modes

- `draft` and `status` are read-only and must not create directories or files.
- The default bootstrap flow drafts first and waits for explicit approval.
- The development-mode choice is part of this bootstrap approval boundary.
- Selecting `automatic` authorizes routine downstream planning, implementation,
  evidence, and delivery continuation after the foundational writes verify;
  it does not bypass security, branch, provider, credential, remote-check,
  merge, or unavailable-tool blockers.
- Explicit approval authorizes only the listed artifacts and approved sections.
- `write` persists the previously approved draft; it is not a request to print
  the draft again.
- A `write` request without a recoverable approved draft is blocked; do not
  reconstruct unapproved contents and write them.
- A response containing Markdown, YAML, or SudoLang content without a file
  operation is still a draft and must be labeled `not written`.

### Handoff

After foundational context is approved and every authorized write has a
verified result, load `aidd-planning-bootstrap` to reconcile statuses, paths,
and planning depth. If any write is blocked or unverified, keep the bootstrap
blocked instead. In guided mode, recommend
`aidd-create-capability-map` for cross-cutting or full-depth work and preserve
the next approval gate. In automatic mode, invoke the orchestrator's loop,
which creates and reviews each downstream layer in order without waiting for
user approval.

## Failure and blocker behavior

Missing user intent, contradictory existing guidance, unavailable repository
evidence, unresolved overwrite decisions, or missing approval is a blocker.
Report the exact artifact and decision required; do not fill gaps with
plausible defaults. A draft or rejected approval never writes files. After a
verified automatic handoff, a downstream uncertainty is handled from
repository evidence or reported as a blocker; it must not become a follow-up
question.

## Constraints

```sudolang
Constraints {
  Read existing sources before proposing replacements
  Ask one question at a time and reuse confirmed answers
  Show a complete file-by-file draft before any write
  Require explicit approval before creating or updating foundational artifacts
  Treat response-only artifact content as not written
  Use a repository file-writing tool after approval; do not stop at a response draft
  Re-read every written path and report its verified write result
  Stop and report a blocker when a requested file operation fails
  Never overwrite vision, AGENTS, README, configuration, or planning sources silently
  Never create capabilities, phases, features, tickets, branches, commits, pushes, or merges as a side effect
  Ask for the development mode during bootstrap and persist it in all required
    project-context artifacts
  In automatic mode, never ask or wait after the verified foundational
    bootstrap; route internally until completion or a real blocker
  In automatic mode, require the exact Rubber Duck `gpt-5.6-luna`
    high-reasoning profile for every validation decision
  Never record automatic agent validation as user confirmation
  Never store credentials, tokens, private keys, cookies, or sensitive data
  Preserve project-specific guidance separately from generic .github policy
  Record observed versus confirmed versus inferred facts and explicit TBD values
  Route material scope or architecture changes to a controlled planning review
}
```

## Commands

```sudolang
Commands {
  /aidd-project-bootstrap [request]
  - inspect, interview, draft, approve, and write foundational project context

  /aidd-project-bootstrap draft [request]
  - produce the complete file-by-file draft without writing

  /aidd-project-bootstrap status
  - report existing foundational artifacts, conflicts, and the next handoff

  /aidd-project-bootstrap write [request]
  - persist the previously approved foundational artifacts and verify each path
}
```
