# Project Vision

> This document is the source of truth for project direction.
> Revisit when goals, users, or constraints change.

**Objective ID:** OBJ-001
**Status:** Confirmed
**Last updated:** 2026-09-08

## Overview

Sleep Inducer is an Android-first, offline breathing pacer for adults who want
a quiet aid for winding down before sleep. It guides a comfortable,
slow-paced breathing rhythm with gentle haptic cues so the user can start a
session, turn the display off, and follow the exercise without watching a
screen or listening to audio.

The app is a wellness and relaxation tool. It is not a diagnostic device, a
medical treatment, or a replacement for clinical insomnia care.

## Goals

- Provide a simple breathing session with a research-informed default near six
  breaths per minute.
- Keep the session usable with the display off through reliable, gentle,
  phase-specific haptic cues.
- Let the user choose a duration, start from a visible screen, stop at any
  time, and receive a clear completion cue.
- Keep the first release offline-first and minimize stored data.
- Validate timing, interruption behavior, haptic delivery, and user-visible
  operation on an Android emulator and physical devices before making release
  claims.
- Keep the breathing domain logic independently testable from Android services,
  vibration hardware, and Compose presentation.

## Non-Goals (Out of Scope)

- Diagnosing, preventing, or treating insomnia or any other medical condition.
- Replacing cognitive behavioral therapy for insomnia (CBT-I), sleep
  restriction, stimulus control, or professional medical advice.
- Mandatory breath holds, hyperventilation, competitive breathing, or
  performance scoring.
- Sleep tracking, sleep staging, heart-rate or HRV sensing, wearable
  integration, or medical measurements in the first release.
- A sleep diary, education library, audio library, meditation catalog, or
  broader relaxation toolkit in the first release.
- Cloud accounts, advertising, analytics, remote content, or transmission of
  breathing or sleep data.

## Key Constraints

- The primary target is adults seeking a general wellness aid. Children and
  clinical populations are not the evidence or safety target for the first
  release.
- The initial protocol uses six-second inhale and exhale defaults without a
  mandatory hold. Each phase can be adjusted independently from two to ten
  seconds in half-second steps. The timing and haptic pattern are product
  settings that must remain comfortable and interruptible.
- The app must avoid claims that a breathing session reliably shortens sleep
  onset. Current evidence supports feasibility and a plausible relaxation
  mechanism, but the direct sleep evidence is small, heterogeneous, and
  inconclusive for objective outcomes.
- Any dizziness, shortness of breath, air hunger, pain, panic, or discomfort
  must be treated as a reason to stop immediately.
- Haptic behavior must degrade clearly when a device has no vibrator or does
  not expose amplitude control. The app must not pretend that a cue was
  delivered when it could not be delivered.
- The phone must not be required to remain face-up or illuminated during a
  session. Physical-device testing must cover screen-off and interruption
  behavior.
- Session data is local-only and minimal. Do not store typed answers, health
  data, recordings, private notes, or unnecessary user-provided information.
- The project targets the existing Android development environment of JDK 17
  and Android SDK 35. Exact minimum supported Android API, application ID,
  release signing, and license remain implementation decisions.

## Delivery / Operational Readiness

- The repository must build a debug APK with the repository-native Gradle
  wrapper once the Android project is present.
- Technical checks are agent-owned. Every acceptance outcome must have an
  executable automated functionality test that crosses the Android
  application boundary. Unit tests do not replace functionality tests.
- Automatic validation is recorded as `automaticValidation`, never as human
  `userValidation`, and must use the exact configured Rubber Duck profile.
- Evidence must separate raw command output from the validation classification
  and must record the emulator/device, Android version, screen state, haptic
  availability, and any interruption conditions.
- A release candidate must be tested on an emulator and at least one physical
  Android device with the display off. OEM battery management and notification
  behavior must be documented when they affect the session.
- The app must expose a reliable stop path before the display is turned off
  and must stop future haptic cues when the user stops the session or the
  system terminates the foreground service.

## Development Mode

- **Selected mode:** `automatic`
- **Canonical configuration:** `.github/aidd-config.yml`,
  `delivery.development.mode`
- **Guided:** retain configured approval gates and the functionality-only user
  validation handoff.
- **Automatic:** after the approved and verified project bootstrap, continue
  through mapping, planning, implementation, technical verification,
  automated functionality validation, review/remediation, delivery, and
  remaining phases without asking or waiting for another user response.
- **Automatic validation profile:** `rubber-duck`, model `gpt-5.6-luna`,
  reasoning effort `high`, scope `all-validation`
- **Profile authority:** `.github/aidd-config.yml`,
  `delivery.development.automatic_validation`
- **Mode-policy authority:** `.github/aidd-config.yml`,
  `delivery.mode_overrides.<mode>`; automatic execution additionally requires
  an available matching validator capability from the runtime.

## Architectural Decisions

| Decision | Rationale |
|---|---|
| Keep breathing timing as a pure domain boundary | Phase transitions, duration handling, stop behavior, and completion can be tested without Android hardware or lifecycle state. |
| Isolate haptic delivery behind an Android adapter | `Vibrator`/`VibratorManager`, amplitude support, and OEM behavior vary by API level and device. |
| Use an Android foreground service for an active screen-off session | The session is user-visible work that must continue while the activity is no longer visible; the API 35 APK declares `specialUse` with an explicit subtype because the timed haptic session does not fit the roughly three-minute `shortService` limit. Google Play approval remains a release gate. |
| Keep the first release local-only and offline | A breathing pacer does not need an account, network, cloud storage, or sensitive telemetry. |
| Treat the six-breaths-per-minute rhythm as an evidence-informed default, not a medical prescription | The strongest direct breathing study is small, and the broader sleep evidence does not justify a universal clinical claim. |

## User Experience Principles

- Quiet: the app should reduce stimulation instead of adding content, scores,
  alerts, or bright visuals.
- Predictable: every cue has a stable meaning, timing is easy to understand,
  and the session can be stopped immediately.
- Comfortable: users follow a natural breath and are never pressured to
  increase depth, hold their breath, or keep going through discomfort.
- Screen-independent: the setup is clear before the screen turns off, and the
  session does not require visual attention afterward.
- Honest: language distinguishes an evidence-informed relaxation aid from a
  proven insomnia treatment.
- Private: no account or remote collection is needed for the core experience.

## Success Criteria

- A user can select a supported duration and start a session from the visible
  app without an account or network connection.
- With the display off, the session emits the intended phase cues within the
  repository's defined timing tolerance until completion or stop.
- Stopping a session prevents all future cues and releases the active service
  cleanly.
- A device without usable haptic hardware produces a clear, non-success-shaped
  explanation and does not claim that haptics were delivered.
- Automated functionality tests prove the visible start/stop flow, screen-off
  session lifecycle, completion behavior, and relevant local state.
- The final review contains no unresolved introduced safety, privacy,
  reliability, or scope findings and documents the remaining evidence limits.

## Evidence Basis

The initial research baseline is maintained in
[`docs/research/sleep-onset-evidence.md`](docs/research/sleep-onset-evidence.md).
It distinguishes clinical insomnia treatment evidence from the narrower
feasibility question of a quiet, haptic breathing pacer.
