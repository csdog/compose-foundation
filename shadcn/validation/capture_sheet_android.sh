#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/../.."
output="${1:?Pass the output root}/cmp-android/sheet-open/actual"
test_class=com.toutiaoyan.compose.ui.components.sheet.SheetAndroidAnimationTest
adb shell settings put global animator_duration_scale 1
adb shell settings put global sysui_demo_allowed 1
adb shell am broadcast -a com.android.systemui.demo -e command enter
adb shell am broadcast -a com.android.systemui.demo -e command clock -e hhmm 0941
adb shell am broadcast -a com.android.systemui.demo -e command battery -e level 100 -e plugged false
adb shell am broadcast -a com.android.systemui.demo -e command notifications -e visible false
./gradlew :shadcn:core:connectedAndroidDeviceTest "-Pandroid.testInstrumentationRunnerArguments.class=$test_class" --rerun
package="$(adb shell pm list instrumentation | tr -d '\r' |
  awk '/^instrumentation:com\.toutiaoyan\.compose\.ui\./ { sub(/^\(target=/, "", $2); sub(/\)$/, "", $2); print $2 }' |
  sort -u)"
[[ "$package" =~ ^[a-zA-Z0-9_.]+$ ]]
mkdir -p "$output"
adb pull "/sdcard/Android/data/$package/files/sheet-animation/." "$output"
