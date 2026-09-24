#!/usr/bin/env bash
set -euo pipefail

application_id="com.sleepinducer.app"
activity="${application_id}/.MainActivity"
apk="app/build/outputs/apk/release/app-release.apk"

adb install -r "$apk"
if launch_result="$(adb shell am start -W -n "$activity" 2>&1)"; then
  launch_status=0
else
  launch_status=$?
fi

printf 'Activity launch exit code: %s\n' "$launch_status"
printf '%s\n' "$launch_result"
if [[ "$launch_status" -ne 0 ]]; then
  printf 'Activity launch command failed; collecting diagnostics.\n' >&2
  adb shell dumpsys activity activities |
    grep -E 'mResumedActivity|topResumedActivity|mFocusedApp' || true
  adb logcat -d -t 200 -s AndroidRuntime:E ActivityTaskManager:E ActivityManager:E ||
    printf 'Could not retrieve logcat diagnostics.\n' >&2
  exit 1
fi

if grep -Fqx 'Status: ok' <<<"$(printf '%s\n' "$launch_result" | tr -d '\r')"; then
  printf 'Activity manager reported Status: ok.\n'
else
  printf 'No Status: ok output; verifying resumed activity and process.\n'
fi

for attempt in 1 2 3 4 5; do
  process_id="$(adb shell pidof "$application_id" | tr -d '\r' || true)"
  resumed_activity="$(
    adb shell dumpsys activity activities |
      tr -d '\r' |
      grep -E 'mResumedActivity|topResumedActivity' |
      grep -F "$activity" || true
  )"
  if [[ -n "$process_id" && -n "$resumed_activity" ]]; then
    printf 'Running app process: %s\n' "$process_id"
    printf '%s\n' "$resumed_activity"
    exit 0
  fi
  sleep 2
done

printf 'App did not become active; collecting diagnostics.\n' >&2
adb shell dumpsys activity activities |
  grep -E 'mResumedActivity|topResumedActivity|mFocusedApp' || true
adb shell pidof "$application_id" || true
adb logcat -d -t 200 -s AndroidRuntime:E ActivityTaskManager:E ActivityManager:E ||
  printf 'Could not retrieve logcat diagnostics.\n' >&2
exit 1
