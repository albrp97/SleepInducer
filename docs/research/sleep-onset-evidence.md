# Sleep-Onset and Haptic Breathing Evidence Baseline

**Research status:** Baseline completed for bootstrap
**Date:** 2026-09-08
**Project:** Sleep Inducer
**Scope:** Adult wellness use, slow-paced breathing, screen-off haptic guidance

## Executive conclusion

The strongest evidence-based treatment for chronic insomnia is multicomponent
CBT-I, not a breathing timer. The first release must therefore be presented as
a relaxation and breathing-guidance tool rather than an insomnia treatment.

Slow-paced breathing is a reasonable low-risk prototype direction because:

- physiological studies consistently associate slow voluntary breathing with
  autonomic changes related to relaxation;
- a small insomnia study directly used 0.1 Hz breathing, about six breaths per
  minute, for twenty minutes before sleep and reported improved sleep
  measures;
- a recent systematic review found promising subjective sleep findings but
  inconclusive objective sleep results; and
- a tactile breath-pacer pilot supports acceptability and feasibility for
  screen-light and audio-free guidance, but it was uncontrolled and cannot
  establish efficacy.

There is no strong evidence that a phone's vibration motor, by itself, makes
people fall asleep faster. Haptic delivery is a product and usability
hypothesis that must be tested separately from the breathing protocol.

## Research question

For adults trying to wind down before sleep, what non-drug approaches have the
best evidence for reducing time to fall asleep, and what protocol is
reasonable for a quiet Android app that guides breathing through gentle
vibration while the display is off?

The question has two separate outcomes:

1. **Clinical sleep efficacy:** whether an intervention changes sleep-onset
   latency or other sleep outcomes.
2. **Product feasibility:** whether a user can comfortably and reliably follow
   a screen-off haptic rhythm without added stimulation.

These outcomes must not be conflated.

## Evidence hierarchy

### CBT-I is the benchmark for chronic insomnia

The 2021 American Academy of Sleep Medicine guideline gives a strong
recommendation for multicomponent CBT-I in adults with chronic insomnia. It
gives conditional recommendations for stimulus control, sleep restriction, and
relaxation as single components, and recommends against sleep hygiene as a
standalone treatment.

Trauer et al. conducted a 2015 systematic review and meta-analysis of 20
randomized studies involving 1,162 participants. Post-treatment CBT-I effects
included approximately nineteen minutes less sleep-onset latency, twenty-six
minutes less wake after sleep onset, about 9.91 percentage points higher sleep
efficiency, and approximately 7.61 minutes more total sleep time.

Implication: Sleep Inducer must not imply equivalence to CBT-I. More intensive
CBT-I-compatible features would be a separate product scope and should not be
introduced as incidental additions to a breathing pacer.

Sources:

- Edinger et al. 2021, AASM guideline:
  <https://doi.org/10.5664/jcsm.8986>
- Trauer et al. 2015:
  <https://doi.org/10.7326/M14-2841>
- World Sleep Society endorsement summary:
  <https://doi.org/10.1016/j.sleep.2023.07.001>

### Slow-paced breathing has plausible physiology but limited sleep evidence

Zaccaro et al. 2018 systematically reviewed slow breathing techniques below
ten breaths per minute in healthy subjects. Across fifteen eligible studies,
slow breathing was associated with autonomic and central nervous system
changes, including increased heart-rate variability and respiratory sinus
arrhythmia, together with reports of comfort and reduced arousal. This is
evidence for a plausible relaxation mechanism, not proof of faster sleep onset.

Eide, Hernes, and Gronli published a 2026 systematic review of slow breathing
before bedtime. Nine studies with 457 participants were included. The
protocols used ten or fewer breaths per minute. Seven studies that reported
self-rated sleep found improvements in sleep duration or quality, while
objective actigraphy and polysomnography results were inconclusive. Six
studies measured HRV and found autonomic effects.

Implication: A slow-breathing prototype is justified, but the product should
measure and communicate uncertainty rather than promise a fixed sleep benefit.

Sources:

- Zaccaro et al. 2018:
  <https://doi.org/10.3389/fnhum.2018.00353>
- Eide, Hernes, and Gronli 2026:
  <https://doi.org/10.1016/j.smrv.2026.102284>

### Direct paced-breathing insomnia study

Tsai, Kuo, and Yang 2015 studied fourteen self-reported insomniacs and
fourteen good sleepers. Participants were assessed during controlled
respiration at 0.1 Hz and 0.2 Hz, with nighttime polysomnography. The slow
condition was about six breaths per minute. The insomnia group practiced slow
paced breathing for twenty minutes before sleep and showed lower sleep-onset
latency, fewer awakenings, less waking time, and higher sleep efficiency.

This is highly relevant to the proposed default, but the sample is small and
the study does not establish that every user or every haptic implementation
will obtain the same result.

Source:

- Tsai et al. 2015:
  <https://doi.org/10.1111/psyp.12333>

### Tactile guidance feasibility

Vermeylen et al. 2022 evaluated a tactile breath pacer and companion app in a
one-month mixed-method pilot. Thirty-nine adults with self-reported sleep
problems used the device in naturalistic settings. Participants generally
found it easy to use, relaxing, and useful for focusing on breathing. The
standalone function was valued because it reduced dependence on a smartphone
screen at night.

Exploratory pre/post scores improved, including a PSQI total score change from
approximately 11.28 to 8.27 and an improvement in the PSQI sleep-latency
component of approximately 0.67 points. The design was uncontrolled and
self-reported, so the study supports acceptability and feasibility more
strongly than causal efficacy.

The study also reported practical design risks: device size, difficulty
perceiving the rhythm for some users, communication issues, unwanted lights,
and the need for a reliable standalone mode.

Source:

- Vermeylen et al. 2022:
  <https://doi.org/10.3389/fdgth.2022.908159>

### Other non-drug approaches

These approaches are relevant for comparison but remain outside the first
release:

- **Mindfulness:** a 2016 meta-analysis of six randomized trials and 330
  participants found improvements in some sleep outcomes, but the overall
  analysis did not consistently improve sleep-onset latency. A 2020
  meta-analysis of mindfulness-based stress reduction found improved sleep
  quality and mental-health outcomes, but it is a broader multi-session
  intervention rather than a short haptic cue.
- **Exercise:** a 2021 meta-analysis of 22 randomized trials found improved
  subjective sleep quality and insomnia scores, while objective sleep changes
  were limited. Exercise is useful general guidance but not an immediate
  in-bed session replacement.
- **Music:** a 2018 network meta-analysis of 20 trials and 1,339 patients
  found small sleep-onset-latency advantages for listening to music and
  music-associated relaxation. Audio is deliberately excluded from this
  product because the intended experience is screen-off and quiet.

Sources:

- Gong et al. 2016:
  <https://doi.org/10.1016/j.jpsychores.2016.07.016>
- Chen et al. 2020:
  <https://doi.org/10.1016/j.jpsychores.2020.110144>
- Xie et al. 2021:
  <https://doi.org/10.3389/fpsyt.2021.664499>
- Feng et al. 2018:
  <https://doi.org/10.1016/j.ijnurstu.2017.10.011>

## Recommended initial protocol

The first implementation should use the smallest protocol that is directly
supported by the evidence and easy to stop:

- **Rate:** six breaths per minute, one ten-second cycle.
- **Timing:** approximately five seconds inhaling and five seconds exhaling.
- **Holds:** none.
- **Depth:** comfortable and natural; never require maximal breaths.
- **Durations:** ten-minute default, with shorter and twenty-minute presets.
- **Cueing:** short phase-transition haptics, not continuous vibration.
- **User control:** stop at any time and do not penalize early stopping.

An optional longer-exhale pattern, such as four seconds in and six seconds
out, can be evaluated later as a separate product experiment. It should not be
described as proven superior for sleep onset. Mandatory holds and rapid
breathing patterns are not appropriate defaults.

## Safety and user wording

The user should be told to stop immediately for dizziness, shortness of breath,
air hunger, chest discomfort, pain, panic, or any feeling that the rhythm is
uncomfortable. The app should not ask users to breathe as deeply as possible,
hold their breath, or continue through distress.

The American Lung Association describes pursed-lip breathing as a way to slow
breathing and recommends a longer, gentle exhale for people managing
shortness of breath, while also directing users to urgent care for continuing
breathing difficulty. Cleveland Clinic describes diaphragmatic breathing as a
relaxation technique but notes that it should complement, not replace, care
from a healthcare provider.

Sleep Inducer should therefore use neutral wellness language and include a
short limitation statement. Persistent, severe, or worsening sleep problems
should be directed toward a qualified clinician. The app should not infer a
diagnosis from session use.

Sources:

- American Lung Association breathing exercises:
  <https://www.lung.org/lung-health-diseases/wellness/breathing-exercises>
- Cleveland Clinic diaphragmatic breathing:
  <https://my.clevelandclinic.org/health/articles/9445-diaphragmatic-breathing>

## Android screen-off haptic feasibility

Android provides `Vibrator`, `VibrationEffect`, and, on newer APIs,
`VibratorManager` for device vibration. The application needs the normal
`android.permission.VIBRATE` permission. `VibrationEffect` supports short
one-shot effects and waveforms, including amplitude values when the hardware
supports them. The app must check amplitude capability and use a conservative
fallback rather than assume all devices behave the same.

Android documentation states that vibration stops if the process exits and
that vibration should be initiated while the app is in the foreground. The
session should therefore be started from the visible activity and promoted to
an Android foreground service before the display is turned off. The service
must cancel vibration on stop, completion, interruption, and destruction.

Apps targeting Android 12 or higher face restrictions on starting foreground
services from the background. Apps targeting Android 14 or higher must
declare the service type and required permissions. Android 13 and higher also
have a notification runtime permission, although users can still see
foreground-service notices in the system Task Manager when notifications are
denied.

The exact foreground-service type for a timed haptic breathing session is an
implementation and distribution-policy gate. `shortService` is unsuitable
for normal ten- or twenty-minute sessions because its documented timeout is
about three minutes. A candidate type such as `specialUse` must be verified
against the target API and Play policy rather than assumed.

Sources:

- `Vibrator`:
  <https://developer.android.com/reference/android/os/Vibrator>
- `VibrationEffect`:
  <https://developer.android.com/reference/android/os/VibrationEffect>
- `VibratorManager`:
  <https://developer.android.com/reference/android/os/VibratorManager>
- Foreground services:
  <https://developer.android.com/develop/background-work/services/fgs>
- Foreground-service launch:
  <https://developer.android.com/develop/background-work/services/fgs/launch>
- Foreground-service declaration:
  <https://developer.android.com/develop/background-work/services/fgs/declare>
- Foreground-service types:
  <https://developer.android.com/develop/background-work/services/fgs/service-types>
- Android 13 notification permission:
  <https://developer.android.com/develop/ui/compose/notifications/notification-permission>

## Product and engineering risks

- A phone's motor may be too strong, too quiet, or inconsistent when placed
  on clothing or a mattress.
- OEM battery policies may suspend or terminate the service while the display
  is off.
- The user may interpret any vibration as a notification or find frequent
  cues stimulating rather than calming.
- Android policy may require a service declaration or distribution treatment
  that changes the packaging plan.
- A phone should not be required to be under a pillow, under body weight, or
  covered by bedding. The setup should prioritize comfort and ventilation.

## Validation plan

### Technical validation

- Unit-test the pure breathing state machine with exact phase boundaries,
  duration boundaries, cancellation, completion, and interruption.
- Instrument the Android service and haptic adapter with fake clocks and fake
  vibrators where possible.
- Run an emulator functionality test that starts a session, turns the display
  off, observes phase state and completion, stops a session, and confirms no
  future cues.
- Test a device or emulator with vibration disabled or unavailable and confirm
  explicit failure behavior.
- Test Android process/service interruption and Task Manager stop behavior.

### Physical-device validation

- Measure cue timing on at least one physical Android device with the display
  off.
- Record API level, OEM, battery mode, haptic capability, and whether the
  device was locked.
- Evaluate several amplitude/duration settings with the phone on clothing or a
  bedside surface. Do not require unsafe placement.
- Confirm that stop and completion cancel all future cues.

### Outcome research

The first release should not claim efficacy from technical tests. If product
research is later added, use a local, optional sleep-onset estimate with clear
limitations and compare a baseline period with a breathing-session period.
Avoid collecting health data by default and do not infer sleep from the phone's
vibration or screen state.

## Evidence limitations

- The direct insomnia breathing study was small.
- The 2026 slow-breathing review found subjective improvements more often than
  objective improvements.
- The tactile-device pilot was uncontrolled and self-reported.
- Breathing protocols, ratios, durations, and populations vary across studies.
- The evidence does not establish a universal ideal rate or prove that a phone
  motor is equivalent to a dedicated tactile device.
- Android screen-off behavior is a platform and hardware reliability question,
  not a clinical efficacy result.
