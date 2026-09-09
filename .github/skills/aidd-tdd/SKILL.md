---
name: aidd-tdd
description: Discover and apply the strongest repository-appropriate test and verification process for an approved ticket, with baseline and evidence discipline.
---

Apply [../development-mode.md](../development-mode.md) when selecting the
post-implementation validation and continuation behavior.

# TDD and Verification

Use test-first development for new code behavior, while choosing the strongest
appropriate evidence method for documentation, configuration, migration,
infrastructure, and exploratory tickets.

## Test and delivery context

Read `.github/aidd-config.yml`, the active ticket contract, manifests, CI,
contribution guidance, and relevant domain skills before selecting commands.
Repository-specific commands and conventions override generic examples.

```sudolang
TestPlan {
  framework
  commands
  technicalVerification[]
  functionalityCommand
  automatedFunctionalityTests[]
  phase
  feature
  ticket
  protectedFlows[]
  requirements[]
  evidencePath
  realSystemRequired
  validationProfile
  coverageGaps[]
}

TechnicalVerification {
  id
  category: smoke | baseline | unit | regression | fixture | acquisition |
    contract | integration | migration | security | staticAnalysis |
    qualityGate | deployment
  commandOrScript
  owner: agent
  validationProfile
  expected
  observed
  status
  artifacts[]
}

AutomatedFunctionalityTest {
  id
  commandOrScript
  framework
  entryPoint
  setup
  representativeData
  steps[]
  assertions[]
  expectedPersistedOrExternalEffect
  cleanup
  artifacts[]
}

TicketMethod = codeTdd | configurationVerification | migrationVerification |
  infrastructureVerification | exploratoryEvidence
```

## Discover the test plan

```sudolang
discoverTestPlan(ticket) => TestPlan {
  1. verify the ticket belongs to an approved active phase and feature
  2. inspect configured commands and repository manifests/scripts
  3. inspect CI and contribution guidance for required gates
  4. identify the test framework and supported local stack
  5. classify the ticket method
  6. identify protected existing flows and at least one automated functionality
     test for every acceptance outcome
  7. identify applicable agent-owned smoke, baseline, unit, regression, fixture,
     acquisition, contract, integration, migration, security, static-analysis,
     deployment, and quality-gate checks; never assign these technical checks
     to the user handoff
  8. in automatic mode, resolve the exact Rubber Duck validation profile
     (`rubber-duck`, `gpt-5.6-luna`, high reasoning, `all-validation`) before
     running or classifying any check
  9. verify that the functionality test crosses a supported system boundary
     and has executable assertions for observable results and relevant state
  10. record missing tooling or unavailable services as blockers when the
     required automated functionality gate cannot run
}
```

Do not assume `npm`, Vitest, Riteway, Playwright, pytest, or any other tool
without repository evidence. If a command is not configured, discover it or
record why it is unavailable.

## Baseline and red-green verification

For code behavior:

1. Record the ticket requirements, protected flows, and expected evidence.
2. Define at least one automated functionality test for every acceptance
   outcome before changing code. It must exercise a supported public boundary
   such as an API, CLI, browser flow, worker, job, or integration and assert
   the observable result plus relevant persisted or external state.
3. Run the protected automated flows and applicable technical verification
   checks before changing code.
4. Record baseline passes, failures, environment health, and known defects with
   `/evidence`; do not hide pre-existing failures.
5. Write a failing, isolated unit/regression test for one requirement and add
   the executable automated functionality test before implementation when the
   repository harness supports test-first execution.
6. Run the narrowest applicable test and confirm it fails for the intended
   reason.
7. Implement only the minimum behavior needed to pass.
8. Rerun the focused test, then the affected regression flows.
9. Run configured smoke, regression, fixture, acquisition, contract,
   integration, migration, security, static-analysis, deployment, and local
   quality checks applicable to the ticket. These are agent-owned technical
   verification; do not ask the user to run them. In automatic mode, the
   Rubber Duck validator must orchestrate or inspect each result using the
   exact configured profile.
10. Run the automated functionality test suite as the final acceptance-level
    automated check before the guided user handoff or automatic validation.
    A missing, unavailable, failed, or unasserted functionality test is a
    blocker when `delivery.gates.automated_functionality_required` is enabled.
11. Record each technical check separately with the automatic validation
    profile when automatic mode is active, then record the functionality
    command, test identifier, boundary exercised, assertions, expected/observed
    result, persisted or external effect, and artifacts.

For documentation, configuration, migration, infrastructure, and exploratory
tickets, still create an executable automated functionality or acceptance
check for the artifact's observable outcome (for example, a parser, command
smoke test, migration verification, API contract, or deployment check). Do not
substitute a unit test, source inspection, or prose-only review. If the
required harness or service is unavailable, stop with a blocker and record the
coverage gap.

## Mandatory automated functionality result

Before presenting the post-implementation user-validation handoff, the
technical result must include one terminal `automatedFunctionality` evidence
entry for the active ticket. The result must come from an executable repository
command or script, not from an agent narrating manual steps. It must exercise a
supported entry point and assert the acceptance outcome, relevant state or
external effect, and failure behavior. `/run-test` is the standard execution
entry point for the configured functionality command or script.

## Post-implementation validation

After implementation and technical verification, do not report the ticket as
done or move it to `closed` yet. In guided mode, produce a copy/paste-ready
handoff that tells the user exactly what to validate against the ticket
contract. In automatic mode, execute the same functionality charter through
the exact Rubber Duck validator (`gpt-5.6-luna`, high reasoning), record
`automaticValidation` with that profile, and do not ask or wait for the user.

```sudolang
UserValidationHandoff {
  ticket
  implementationSummary
  purpose: functionality | notApplicable
  prerequisites[]
  representativeData[]
  steps[] // action, expectedVisibleResult, expectedPersistedOrExternalEffect
  userObservableFailurePaths[]
  cleanup[]
  evidenceToReturn[]
  passCriteria[]
}
```

The handoff must:

1. Translate every applicable acceptance criterion into an observable action
   and expected result; never tell the user only to "test the change".
2. Include setup, services, permissions, representative data, exact
   functionality steps, visible results, persisted or external effects,
   cleanup, and only user-observable failure behavior.
3. Require comparable before/after UI evidence when configured. For backend,
   integration, migration, infrastructure, or CLI work, specify the supported
   API, command, service, log, data, or operational observation instead; do
   not require screenshots when they cannot prove the change.
4. Exclude smoke, baseline, unit, regression, fixture, acquisition, contract,
   integration, migration, security, static-analysis, formatter, lint,
   type-check, build, and other technical-check commands from the user
   handoff. The agent must run and record those checks before presenting it.
5. State what the user must return: `PASS`, `FAIL`, `BLOCKED`, or approved
   `NOT APPLICABLE` for the functionality flow only, with notes and safe
   evidence paths.
6. Keep the ticket in `verifying` while the handoff is awaiting a result.
   A failed or blocked user check keeps the ticket open and requires a fix,
   follow-up, or explicit change-control decision.

Use `/run-test` to execute the required automated functionality test and
record agent-owned technical checks through `/evidence`. Use `/user-test` only
to generate the functionality handoff (or an explicitly requested separate
usability study). A human script or an agent-only manual charter cannot
satisfy the automated functionality gate. In guided mode, agent-run checks do
not replace the required user confirmation when the effective guided policy
`delivery.mode_overrides.guided.gates.user_validation_required` is enabled; in
automatic mode, terminal `automaticValidation` through the exact Rubber Duck
profile is the mode-appropriate gate. The user is never responsible for
rerunning technical checks.

After the guided user returns a terminal validation result, or after automatic
validation is appended with the required Rubber Duck profile to evidence,
recommend `/review`. Do not recommend
`/commit` until the review is terminal and the intended changes are staged.

## Assertions and isolation

```sudolang
assert({ given, should, actual, expected }) {
  given and should describe observable acceptance behavior
  actual exercises the named unit or real integration; automated functionality
    actual must cross the supported system boundary
  expected expresses the required result
  test answers unit, behavior, actual result, expected result, and bug discovery
}
```

Tests must be readable, local, independent, and explicit. Use factories for
repeated data rather than shared mutable fixtures. Prefer real integration
where technically and economically feasible. A mock is justified only for an
irrecoverable side effect, unavailable physical infrastructure, or
non-viable per-run cost; document the reason and the resulting coverage gap.

## Evidence

Record commands, framework, environment/service health, expected and observed
results, artifacts, failures, fixes, skipped checks, and coverage gaps through
`/evidence`. A unit-test exit code is evidence for that command only; it does
not replace the required automated functionality result or remote-gate
evidence. Technical verification evidence belongs to the agent; user-validation
evidence records only the user's functional observation and result. In
automatic mode, every validation result must include the exact Rubber Duck
profile; deterministic tools remain authoritative for their raw output.

## Constraints

```sudolang
Constraints {
  Never implement code behavior before its failing test when code TDD applies
  Never claim a baseline or regression passed without running and recording it
  Never close or gate a ticket without a terminal automated functionality test
    when delivery.gates.automated_functionality_required is enabled
  Never treat a unit test, source inspection, human script, or agent narration
    as an automated functionality test
  Never treat unavailable browser, service, or integration capability as passed
  Never assume a test framework or command
  Never share mutable test state
  Never add tests for type shape alone when type checking covers it
  Stop and report when a required test or service cannot run
  Always perform the mode-appropriate validation after all applicable
    agent-owned technical checks and automated functionality checks are
    terminal
  Never ask the user to run smoke, baseline, unit, regression, fixture,
    acquisition, contract, integration, migration, security, static-analysis,
    formatter, lint, type-check, build, deployment, or other technical checks
  Never report a guided ticket done or move it to closed before user validation
    evidence is recorded, unless an approved not-applicable decision is recorded
  Never report an automatic ticket done or move it to closed before
    automaticValidation evidence is recorded
  Never accept automatic validation without the exact Rubber Duck
    `gpt-5.6-luna` high-reasoning profile
  Obtain the configured approval before moving to the next requirement in
    guided mode; automatic mode uses verified bootstrap authorization
}
```
