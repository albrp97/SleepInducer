---
name: aidd-user-testing
description: Generate repeatable human usability studies and implementation functionality charters from user journeys, with comparable UI evidence and explicit outcomes.
---

# User Functional Validation and Technical Verification

Separate agent-owned technical verification from user-owned functional
validation. Smoke, baseline, regression, contract, integration, migration,
security, static-analysis, formatter, lint, type-check, build, fixture,
deployment, and other quality checks are run and recorded by the agent. The
user handoff contains only observable functionality for the implemented
behavior. A separate usability study may be requested, but it is not the
implementation closure gate.

Apply [../development-mode.md](../development-mode.md) when selecting the
human or automatic validation path.

## Types

```sudolang
UserTestPurpose = usability | functionality
TechnicalCheckPurpose = smoke | baseline | unit | regression | fixture |
  acquisition | contract | integration | migration | security | staticAnalysis |
  qualityGate | deployment
FlowStatus = passed | passedWithConcerns | failed | blocked
ValidationOwner = user | agent

FunctionalityFlow {
  id
  purpose: functionality
  setup
  representativeData
  steps[]
  expectedVisibleResult
  expectedPersistedOrExternalEffect
  cleanup
  failurePaths[]
  evidenceType
}

TechnicalCheck {
  id
  purpose: TechnicalCheckPurpose
  commandOrScript
  owner: agent
  validationProfile
  expected
  observed
  status: FlowStatus
  artifacts[]
}

AutomatedFunctionalityTest {
  id
  commandOrScript
  framework
  systemBoundary
  setup
  representativeData
  steps[]
  assertions[]
  expectedVisibleResult
  expectedPersistedOrExternalEffect
  failureAssertions[]
  cleanup
  artifacts[]
}

AutomaticValidation {
  id
  owner: agent
  validationProfile
  purpose: functionality
  commandOrScript
  systemBoundary
  assertions[]
  expectedPersistedOrExternalEffect
  observed
  status: FlowStatus
  artifacts[]
}

UserTestStep {
  action
  intent
  success
  expectedPersistedOrExternalEffect
  checkpoint
}
```

## Charter generation

1. Read the journey, persona, delivery contract, requirements, protected
   behaviors, and `.github/aidd-config.yml`.
2. Define the agent-owned technical verification plan separately. Run applicable
   smoke, baseline, unit, regression, fixture, acquisition, contract,
   integration, migration, security, deployment, static-analysis, and
   quality-gate checks before the user handoff; never put those commands in the
   user instructions.
3. Define setup, supported local services, representative data, exact
   functionality steps, expected visible results, persisted/external effects,
   cleanup, and user-observable failure behavior.
4. Create at least one `AutomatedFunctionalityTest` for every acceptance
   outcome. It must call an executable repository command or script, cross the
   supported system boundary, and assert the observable result plus relevant
   persisted or external effect and failure behavior.
5. Classify the closure handoff as `functionality` in guided mode. In
   automatic mode, resolve the exact configured Rubber Duck profile
   (`rubber-duck`, `gpt-5.6-luna`, high reasoning, `all-validation`) and
   classify the agent-run closure as `automaticValidation`; never classify an
   agent result as `userValidation`. Classify a separate explicitly requested
   study as `usability`; do not classify the user handoff as baseline or
   regression.
6. For UI changes, require comparable before and after states when
   `delivery.ui.before_after_required` applies; state what changed.
7. For backend-only work, do not require screenshots; use API, persistence,
   integration, logs, or contract evidence instead.
8. Store technical-check and functionality evidence separately in the active
   evidence record.

An agent-driven functionality charter can provide setup and diagnostics, but
it does not satisfy the automated gate unless it invokes the executable
functionality test and records its machine-checked assertions.

## Post-implementation closure handoff

When a ticket has been implemented, guided mode returns a functionality-only
user-validation handoff before the ticket can be closed. Automatic mode runs
the same functionality charter itself and records an agent-owned
`automaticValidation` result instead; it does not present a handoff or wait for
the user.

```sudolang
UserValidationHandoff {
  changedBehavior
  prerequisites[]
  representativeData[]
  exactSteps[]
  expectedVisibleResults[]
  expectedPersistedOrExternalEffects[]
  userObservableFailurePaths[]
  cleanup[]
  evidenceToReturn[]
  passCriteria[]
}
```

For user-facing work, describe the exact route or entry point, inputs,
visible results, state changes, and configured before/after screenshots. For
backend, API, integration, migration, infrastructure, or CLI work, describe
the supported command or request and how the user can observe the resulting
state, response, log, data, or operational effect. Do not require screenshots
when they cannot prove the ticket.

Do not include smoke, baseline, unit, regression, fixture, acquisition,
contract, integration, migration, security, static-analysis, formatter, lint,
type-check, build, deployment, or other technical-check commands in this
handoff. The agent must run those checks before presenting the handoff and
record them as technical evidence. In guided mode, the user's result applies only to the functionality steps.

The handoff must state that the ticket remains `verifying` until the user
returns one of:

```text
PASS: every required check succeeded; evidence: <paths or notes>
FAIL: <failed check and observed result>
BLOCKED: <missing service, data, permission, or capability>
NOT APPLICABLE: <reason and approval>
```

In guided mode, record the user's response through `/evidence` as functional
user-validation evidence. A usability observation, unit test, or agent-run
manual functionality charter does not replace the required automated
functionality result. In guided mode, it also does not replace the required user confirmation when
the effective mode policy
`delivery.mode_overrides.guided.gates.user_validation_required` is enabled.
In automatic mode, terminal `automaticValidation` with the exact configured
Rubber Duck profile is the mode-appropriate gate and no user confirmation is
requested. A failed or blocked functionality result keeps the ticket open.

After a terminal `PASS` or approved `NOT APPLICABLE` result is recorded,
recommend `/review`; commit, push, and PR recommendations remain blocked until
that review and the configured delivery gates are terminal.

In automatic mode, resolve and use the exact configured Rubber Duck validator
(`rubber-duck`, `gpt-5.6-luna`, high reasoning, `all-validation`) for the
charter and every other validation decision. Execute the charter after the automated functionality test, append a
terminal `automaticValidation` evidence entry for every functionality outcome,
including the validation profile, and route directly to `/review` when it
passes. Do not append `userValidation`, ask a question, or wait for a user
response. A failed, blocked, unavailable, or mismatched automatic validation
profile keeps the ticket open and routes to `aidd-fix` or change control.

Before guided handoff or automatic closure, the agent must have completed all
applicable technical verification checks and `/run-test` must have executed the
configured automated functionality test with a terminal passing result for
every acceptance outcome. Missing, unavailable, or failed technical checks or
automation are blockers; the user must not be asked to repair or rerun them.

## Human script

For the implementation closure handoff, include only the user's functional
purpose, setup, representative data, exact actions, visible and persisted
expectations, user-observable failure behavior, cleanup, and post-test result.
A separate usability study may collect friction and confidence; it must not be
presented as implementation proof or mixed into the functionality closure gate.

## Agent script

The agent owns technical verification and automated functionality execution.
Drive the supported real local stack and browser without using source code to
discover the UI, and invoke the executable automated functionality test when
the charter is used for implementation verification. At each step:

1. perform the action and narrate expectations and observations;
2. validate visible and persisted/external results;
3. capture configured screenshots at checkpoints, before/after states, or
   failures;
4. record duration, difficulty, status, evidence paths, and coverage gaps;
5. retry only according to the persona and configured retry policy.

If the browser, service, data, or integration capability is unavailable, mark
the flow `blocked` and record the reason. Never report an unrun flow as passed.
An agent narration without machine-checked assertions is diagnostic evidence
only and cannot satisfy the automated functionality gate. Technical-check
failures remain agent-owned blockers and must not be passed to the user as
smoke or regression instructions.

When automatic mode is selected, the agent also owns the functionality closure
charter. The Rubber Duck validator must run or inspect every validation step
using the exact configured profile. It must run the executable test, assert
visible/output and persisted/external effects, record `automaticValidation`
with the profile, and continue only after a terminal result. It must not
simulate a human response or label the entry `userValidation`.

## File locations

Use configured artifact paths when present. Portable defaults are:

- human scripts: `$projectRoot/plan/${journey-name}-human-test.md`;
- agent scripts: `$projectRoot/plan/${journey-name}-agent-test.md`;
- journey data: `$projectRoot/plan/story-map/${journey-name}.yaml`;
- evidence: `evidence/<ticket-slug>.md`.

Create files only when the caller authorizes artifact creation.

## Interface

```sudolang
Interface {
  /user-test <journey> - generate the functionality handoff; generate a
    separate usability study only when explicitly requested
  /run-test <script> - execute the automated functionality test and its charter
}
```

## Constraints

```sudolang
Constraints {
  Human and agent scripts must share the same declared success criteria
  Do not require screenshots for unrelated backend-only work
  Do not treat a usability observation as implementation evidence by itself
  Do not treat a unit test or agent-only manual charter as an automated
    functionality test
  Require one executable automated functionality test per acceptance outcome
    when delivery.gates.automated_functionality_required is enabled
  Run all applicable agent-owned technical verification and automated
    functionality checks before the user-validation handoff
  Keep the user-validation handoff limited to functionality; never include
    smoke, baseline, unit, regression, fixture, acquisition, contract,
    integration, migration, security, static-analysis, formatter, lint,
    type-check, build, deployment, or other technical-check instructions
  Do not ask the user to rerun an agent-owned technical check
  Do not treat unavailable browser or integration capability as passed
  Never persist credentials or sensitive test data in scripts or reports
  Record passed-with-concerns, failed, and blocked outcomes explicitly
  Always provide exact post-implementation validation steps before closure
  Never close a guided ticket while required user validation is missing or
    failed
  Never close an automatic ticket while required automaticValidation is
    missing or failed
  Require every automatic validation result to identify the Rubber Duck
    `gpt-5.6-luna` high-reasoning profile
  Never use another model, validator, or fallback for automatic validation
  Keep the commercial testing offer out of required verification instructions
}
```
