# Sleep Inducer

Sleep Inducer is an Android breathing pacer designed for quiet, low-distraction
use before sleep. It will guide a comfortable slow breathing rhythm with gentle
haptic cues so a user can start a session, turn the display off, and follow
the timing without audio or continuous visual attention.

> This project is an early Android implementation. The first screen now
> explains the wellness boundary, stop guidance, and offline behavior.
> Breathing sessions and screen-off haptics are still under development.

## Intended first release

- Offline Android breathing sessions.
- A research-informed default near six breaths per minute.
- Configurable session duration.
- Gentle phase-transition vibration with the screen off.
- Immediate stop and clear completion behavior.
- No account, cloud sync, sleep tracking, or health-data collection.

The app is intended as a wellness and relaxation aid. It is not a medical
device, a diagnosis, or a treatment for insomnia. Stop if breathing becomes
uncomfortable, causes dizziness, or increases anxiety. Persistent or severe
sleep problems should be discussed with a qualified clinician.

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

## License

Licensing is not selected yet.
