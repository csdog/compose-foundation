#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/../.."
platform="${1:?Pass cmp-android or jetpack-compose}"
output="${2:?Pass the output root}/$platform/sheet-open/actual"
test_class=com.toutiaoyan.compose.ui.components.sheet.SheetAndroidAnimationTest
adb shell settings put system screen_off_timeout 2147483647
adb shell svc power stayon true
adb shell settings put global animator_duration_scale 1
adb shell settings put global sysui_demo_allowed 1
adb shell am broadcast -a com.android.systemui.demo -e command enter
adb shell am broadcast -a com.android.systemui.demo -e command clock -e hhmm 0941
adb shell am broadcast -a com.android.systemui.demo -e command battery -e level 100 -e plugged false
adb shell am broadcast -a com.android.systemui.demo -e command notifications -e visible false
case "$platform" in
  cmp-android)
    ./gradlew :shadcn:core:packageAndroidDeviceTest
    test_package=com.toutiaoyan.compose.ui.test
    package="$test_package"
    test_apk=shadcn/shared/build/outputs/apk/androidTest/core-androidTest.apk
    ;;
  jetpack-compose)
    project="$PWD/build/shadcn-audit/jetpack-sheet"
    python3 shadcn/validation/prepare_jetpack_sheet.py "$project"
    ./gradlew -p "$project" assembleDebug assembleDebugAndroidTest
    package=com.toutiaoyan.compose.ui.animation
    test_package="$package.test"
    adb install -r "$project/build/outputs/apk/debug/jetpack-sheet-animation-debug.apk"
    test_apk="$project/build/outputs/apk/androidTest/debug/jetpack-sheet-animation-debug-androidTest.apk"
    ;;
  *) echo "Unsupported Android platform $platform" >&2; exit 2 ;;
esac
adb install -r "$test_apk"
adb shell pm clear "$package"
adb shell input keyevent KEYCODE_WAKEUP
adb shell wm dismiss-keyguard
adb shell cmd statusbar collapse
result="$(adb shell am instrument -w -e class "$test_class" "$test_package/androidx.test.runner.AndroidJUnitRunner")"
printf '%s\n' "$result"
if ! grep -Fq 'OK (1 test)' <<<"$result"; then
  adb shell dumpsys window | grep -E 'mCurrentFocus|mFocusedApp' || true
  exit 1
fi
mkdir -p "$output"
adb pull "/sdcard/Android/data/$package/files/sheet-animation/." "$output"
