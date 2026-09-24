#!/usr/bin/env bash
set -euo pipefail

repository_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
workflow_file="${repository_root}/.github/workflows/release-apk.yml"
launch_script="${repository_root}/.github/scripts/verify-release-apk-install.sh"
test_directory="$(mktemp -d)"
trap 'rm -rf "$test_directory"' EXIT

grep -Fqx \
  '          script: bash .github/scripts/verify-release-apk-install.sh' \
  "$workflow_file"

mkdir -p "${test_directory}/bin"
cat >"${test_directory}/bin/adb" <<'ADB'
#!/usr/bin/env bash
set -u

printf '%s\n' "$*" >>"$MOCK_ADB_LOG"

case "$*" in
  "install -r "*)
    if [[ "$MOCK_ADB_MODE" == install-failure ]]; then
      printf 'Mock install failure\n' >&2
      exit 1
    fi
    exit 0
    ;;
  "shell am start "*)
    case "$MOCK_ADB_MODE" in
      empty-output)
        exit 0
        ;;
      status-output)
        printf 'Status: ok\n'
        exit 0
        ;;
      launch-failure)
        printf 'Unable to start activity\n' >&2
        exit 1
        ;;
      inactive)
        printf 'Starting activity\n'
        exit 0
        ;;
    esac
    ;;
  "shell pidof com.sleepinducer.app")
    if [[ "$MOCK_ADB_MODE" == empty-output || "$MOCK_ADB_MODE" == status-output ]]; then
      printf '1234\n'
    fi
    exit 0
    ;;
  "shell dumpsys activity activities")
    if [[ "$MOCK_ADB_MODE" == empty-output || "$MOCK_ADB_MODE" == status-output ]]; then
      printf 'mResumedActivity: ActivityRecord{abc com.sleepinducer.app/.MainActivity t1}\n'
    fi
    exit 0
    ;;
  "shell logcat "*)
    printf 'Mock Android error log\n'
    exit 0
    ;;
  *)
    printf 'Unexpected adb command: %s\n' "$*" >&2
    exit 2
    ;;
esac
ADB

cat >"${test_directory}/bin/sleep" <<'SLEEP'
#!/usr/bin/env bash
exit 0
SLEEP
chmod +x "${test_directory}/bin/adb" "${test_directory}/bin/sleep"

run_success_case() {
  local mode="$1"
  local expected_output="$2"
  local output

  : >"${test_directory}/adb.log"
  if ! output="$(
    PATH="${test_directory}/bin:${PATH}" \
      MOCK_ADB_LOG="${test_directory}/adb.log" \
      MOCK_ADB_MODE="$mode" \
      bash "$launch_script" 2>&1
  )"; then
    printf 'Expected mode %s to pass, but it failed:\n%s\n' "$mode" "$output" >&2
    return 1
  fi

  if ! grep -Fq "$expected_output" <<<"$output"; then
    printf 'Expected output for mode %s was missing:\n%s\n' "$mode" "$output" >&2
    return 1
  fi
}

run_failure_case() {
  local mode="$1"
  local expected_output="$2"
  local output

  : >"${test_directory}/adb.log"
  if output="$(
    PATH="${test_directory}/bin:${PATH}" \
      MOCK_ADB_LOG="${test_directory}/adb.log" \
      MOCK_ADB_MODE="$mode" \
      bash "$launch_script" 2>&1
  )"; then
    printf 'Expected mode %s to fail, but it passed:\n%s\n' "$mode" "$output" >&2
    return 1
  fi

  if ! grep -Fq "$expected_output" <<<"$output"; then
    printf 'Expected failure diagnostics for mode %s were missing:\n%s\n' "$mode" "$output" >&2
    return 1
  fi

  if [[ "$mode" == install-failure ]] &&
    grep -Fq 'shell am start' "${test_directory}/adb.log"; then
    printf 'The launch command ran after installation failed.\n' >&2
    return 1
  fi
}

run_success_case empty-output 'Running app process: 1234'
run_success_case status-output 'Activity manager reported Status: ok.'
run_failure_case install-failure 'Mock install failure'
run_failure_case launch-failure 'Activity launch command failed; collecting diagnostics.'
run_failure_case inactive 'App did not become active; collecting diagnostics.'

if ! grep -Fq 'install -r app/build/outputs/apk/release/app-release.apk' \
  "${test_directory}/adb.log"; then
  printf 'The release APK was not passed to adb install.\n' >&2
  exit 1
fi

printf 'Release APK install/launch regression cases passed.\n'
