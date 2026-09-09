# aidd-create-capability-map

Connect desired outcomes to evidenced product or operational capabilities.
The approved map is persisted to the configured capability-map path and
verified; a map printed only in the response remains a draft. `write` without
a recoverable approved map is blocked.

## Use when

- The planning layer needs to be created, reconciled, or reviewed.
- Parent links, status, approval, coverage, or evidence must be made explicit.

## Contract

Inputs and outputs are defined in [SKILL.md](SKILL.md). The skill is bounded to its named planning layer, preserves stable IDs and traceability, and stops on missing prerequisites. It does not implement, commit, push, merge, or silently generate downstream artifacts.
