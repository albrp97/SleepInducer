# Repository Map

**Map ID:** MAP-001
**Status:** Confirmed after bootstrap inspection
**Last updated:** 2026-09-08
**Repository root:** `/home/ghiki/code/sleep-inducer`

This map distinguishes observed scaffolding from planned or unknown surfaces.
It is not an implementation plan and does not create child planning records.

## Objective and scope ancestry

- Objective: `OBJ-001` - help adult users settle before sleep with a
  comfortable, screen-off guided breathing session.
- Scope: `SCOPE-001` - first release is a breathing-only Android pacer.
- Development mode: `automatic`, authoritative at
  `.github/aidd-config.yml:delivery.development.mode`.

## Observed repository surfaces

| Path | Responsibility | Status | Evidence |
|---|---|---|---|
| `.github/aidd-config.yml` | Artifact paths, planning policy, commands, gates, and mode profile | Present | Repository inspection |
| `.github/copilot-instructions.md` | Repository-neutral workflow and delivery rules | Present | Repository inspection |
| `.github/README.md` | Harmonic Coding workflow overview | Present | Repository inspection |
| `.github/skills/` | Lifecycle, planning, validation, and domain skill contracts | Present | Repository inspection |
| `.github/prompts/` | Workflow command entry points | Present | Repository inspection |
| `.github/instructions/` | Planning, implementation, review, security, and testing instructions | Present | Repository inspection |
| `.github/workflows/workflow-evals.yml` | Existing workflow evaluation automation | Present | Repository inspection |
| `.github/workflow-templates/` | Reusable workflow templates | Present | Repository inspection |

## Foundational project surfaces

| Path | Responsibility | Status | Owner |
|---|---|---|---|
| `vision.md` | Durable project direction and success criteria | Present | Project workflow |
| `AGENTS.md` | Project-specific engineering, safety, and validation guidance | Present | Project workflow |
| `README.md` | Human-facing repository onboarding | Present | Project workflow |
| `docs/specs/project-scope.md` | Approved objective-to-delivery scope handoff | Created | Product planning |
| `docs/research/sleep-onset-evidence.md` | Scientific and Android feasibility baseline | Created | Research baseline |

## Implemented and planned implementation surfaces

| Path | Intended responsibility | Required boundary |
|---|---|---|
| `settings.gradle.kts`, Gradle build files, and `gradlew` | Android project and dependency configuration | Keep build configuration explicit and reproducible |
| `app/src/main/java/.../domain/` | Breathing timing and session state | No Android framework dependency in core timing logic |
| `app/src/main/java/.../service/` | Screen-off session lifecycle | Surface start, interruption, stop, and completion failures |
| `app/src/main/java/.../haptics/` | Vibration adapter and capability detection | Cancel reliably and avoid success-shaped fallback |
| `app/src/main/java/.../ui/` | Session setup and status presentation | No embedded session policy |
| `app/src/test/` | Domain and repository correctness tests | Unit tests do not replace functionality tests |
| `app/src/androidTest/` | Android boundary functionality flows | Must cover screen-off lifecycle and observable state |
| `.github/workflows/android.yml` | Android build and test parity | Must match local commands before release |
| `evidence/` | Baseline, test, review, and delivery evidence | Redact sensitive values |

## Commands and environment

- Confirmed environment guidance: JDK 17 and Android SDK 35.
- Build: `./gradlew assembleDebug`.
- Unit test: `./gradlew test`.
- Lint: `./gradlew lintDebug`.
- Android functionality test: `./gradlew connectedDebugAndroidTest`.
- Current application ID: `com.sleepinducer.app`.
- Configured command arrays are empty by design until the Android project and
  repository-native commands are promoted into delivery configuration.

## Delivery surfaces

- APK output: `app/build/outputs/apk/debug/app-debug.apk`.
- Evidence: `evidence/`.
- Pull-request and release policy: configured in `.github/aidd-config.yml`;
  no remote provider or branch is currently configured.
- The repository root currently has no Git metadata or configured remote.

## Known gaps and unknowns

- The initial Android source, Gradle wrapper, manifest, tests, and debug APK
  now exist. Android CI is not configured yet.
- Minimum API 26 and application ID `com.sleepinducer.app` are selected for the
  initial implementation.
- The correct Android foreground-service type for a screen-off haptic session
  must be verified against the target API and distribution policy.
- Physical-device haptic timing, amplitude consistency, lock-screen behavior,
  Doze/OEM battery policies, and user-stop behavior require validation.
- Release signing, license, remote repository, branch policy, and release
  channel are unresolved.
