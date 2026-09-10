# Delivery Phases

> Objective: `OBJ-001`
> Scope: `SCOPE-001`
> Capability map: `CAPMAP-001`
> Development mode: automatic

| ID | Title | Status | Path | Capabilities | Entry gate | Exit gate |
|---|---|---|---|---|---|---|
| `PHASE-001` | Establishing the safe breathing foundation | confirmed | `docs/planning/phases/open/PHASE-001-safe-breathing-foundation.md` | `CAP-001`, `CAP-003`, `CAP-005`, `CAP-006` | Objective, scope, repository map, research baseline, and capability map are confirmed | Android build baseline, protocol contract, safety copy, app identity, and platform decisions are recorded and testable |
| `PHASE-002` | Delivering reliable guided breathing sessions | complete | `docs/planning/phases/closed/PHASE-002-guided-breathing-sessions.md` | `CAP-001`, `CAP-003`, `CAP-004` | Phase 001 contracts and Android baseline are available | Domain timing, gentle haptic adapter, stop, completion, and interruption behavior pass focused tests |
| `PHASE-003` | Delivering screen-off relaxation use | confirmed | `docs/planning/phases/open/PHASE-003-screen-off-relaxation.md` | `CAP-002`, `CAP-004`, `CAP-005`, `CAP-006` | Guided session behavior is stable and service policy is resolved | Visible setup, foreground service, notification, screen-off continuity, safety messaging, and local state pass functionality tests |
| `PHASE-004` | Proving and packaging the offline app | confirmed | `docs/planning/phases/open/PHASE-004-quality-and-apk-delivery.md` | `CAP-001`, `CAP-002`, `CAP-003`, `CAP-004`, `CAP-005`, `CAP-006` | User-facing behavior is implemented and testable | Clean-clone APK build, unit and functionality evidence, static analysis, review, device validation, and configured delivery gates are terminal |

## Sequencing rationale

The project first establishes the evidence-backed protocol, safety boundary,
Android identity, and platform constraints so implementation does not encode
unresolved assumptions. The breathing domain and haptic adapter then become
testable independently of the activity lifecycle. Screen-off continuity is
implemented only after those contracts are stable, because service and OEM
behavior are the highest-risk user-facing boundary. Quality and APK delivery
remain last so the release artifact contains the complete validated behavior.

## Planning decision

The phases were created under the automatic development authorization recorded
by the verified project bootstrap. Feature generation must remain inside one
phase at a time and preserve the breathing-only scope.
