# Sleep Inducer

Sleep Inducer is an Android breathing pacer designed for quiet, low-distraction
use before sleep. It will guide a comfortable slow breathing rhythm with gentle
haptic cues so a user can start a session, turn the display off, and follow
the timing without audio or continuous visual attention.

> This project is an early Android implementation. It provides a timed
> breathing session with gentle phase cues, a visible stop control, and a
> foreground service for screen-off continuity.

## Intended first release

- Offline Android breathing sessions.
- A research-informed slow-breathing default with six-second inhale and
  exhale phases.
- Configurable session duration.
- Gentle phase-transition vibration with the screen off.
- Immediate stop and clear completion behavior.
- No account, cloud sync, sleep tracking, or health-data collection.

The app supports five-, ten-, and twenty-minute presets, plus a custom session
length from 0 to 20 minutes. A custom session must be set to at least one
minute before it can start. Choose a duration while the screen is visible,
adjust the separate inhale and exhale sliders if needed, allow notifications so
the active session remains visible, start the session, and then turn the
display off. Inhale and exhale begin with distinct short cues. The notification
and the in-app control both provide a stop action.

The app is intended as a wellness and relaxation aid. It is not a medical
device, a diagnosis, or a treatment for insomnia. Stop if breathing becomes
uncomfortable, causes dizziness, or increases anxiety. Persistent or severe
sleep problems should be discussed with a qualified clinician.

## How it works

Sleep Inducer uses paced breathing with six-second inhale and exhale phases by
default, with zero seconds of breath holding. Use the inhale and exhale sliders
to choose each phase from two to ten seconds in half-second steps. There are no
mandatory holds and no required breath depth: breathe naturally and
comfortably.

The short haptic cues mark the beginning of each phase. Breathe in during the
inhale phase and out during the exhale phase. The vibration is a timing aid,
not a measurement of breathing quality or a guarantee of faster sleep.

The protocol is based on the research baseline in
[`docs/research/sleep-onset-evidence.md`](docs/research/sleep-onset-evidence.md).
Slow breathing below ten breaths per minute is associated with
relaxation-related changes, and a small pre-sleep study used approximately six
breaths per minute for twenty minutes. Broader evidence remains inconclusive,
so this is not presented as the fastest or guaranteed way to fall asleep.
For persistent insomnia, cognitive behavioral therapy for insomnia has
stronger evidence than a breathing timer.

To use the app:

1. Choose a session length and adjust the inhale and exhale sliders if needed.
   Each phase supports whole or half seconds.
2. Start the session while the screen is visible and keep the phone uncovered
   in a comfortable position.
3. Turn the screen off and follow the inhale and exhale cues.
4. Stop whenever you need to, or let the session finish naturally.

The app follows the device's system light or dark theme automatically. While a
session is active, the foreground service holds a partial CPU wake lock so the
phase callbacks can continue when the display is locked. It is
released as soon as the session stops, completes, is interrupted, or fails.

## Evidence and limitations

The research baseline is in
[`docs/research/sleep-onset-evidence.md`](docs/research/sleep-onset-evidence.md).
Existing evidence supports slow breathing and tactile guidance as plausible,
low-distraction relaxation approaches, but it does not prove that this exact
phone implementation will make every user fall asleep faster.

## Repository context

- [`vision.md`](vision.md) defines the durable product direction.
- [`AGENTS.md`](AGENTS.md) defines project-specific engineering and safety
  rules.
- [`docs/specs/project-scope.md`](docs/specs/project-scope.md) is the current
  delivery handoff.
- [`docs/planning/repo-map.md`](docs/planning/repo-map.md) records observed and
  planned repository surfaces.

## Build and test

Use JDK 17 and Android SDK 35, then run:

```bash
./gradlew assembleDebug
./gradlew test
./gradlew lintDebug
./gradlew connectedDebugAndroidTest
```

The debug APK is expected at
`app/build/outputs/apk/debug/app-debug.apk`.

The current application ID is `com.sleepinducer.app`. The Android
functionality suite uses an API 35 emulator or connected device.

The debug build requests vibration, foreground-service, notification, and
screen-off CPU continuity permissions only. It does not request internet
access.

## Signed release APKs

GitHub releases publish `sleep-inducer-release.apk` only after the APK passes
signature verification and installs and launches on an API 35 emulator. Release
signing uses a dedicated key that must remain outside the repository. Configure
these GitHub Actions repository secrets before publishing or updating a
release:

- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

Add the secrets in
[repository Actions settings](https://github.com/albrp97/SleepInducer/settings/secrets/actions).
Keep a secure backup of the release keystore: future APK updates must use the
same key.

The release build fails rather than producing a downloadable unsigned APK when
any signing secret is missing. The previously published `v0.1.0` asset is
unsigned and cannot be installed. To preserve the existing tag, the corrected
workflow publishes a new signed `v0.1.1` release without moving `v0.1.0`.
Publication completes only after the signed APK passes the API 35 install and
launch checks. The old `v0.1.0` release remains unchanged and should not be
installed. CI pins the signing certificate to SHA-256
`f983cd5963b821bff295cb349cc43ef074f3ee76aa467900722f06eabdef5566`.

## License

Licensing is not selected yet.
