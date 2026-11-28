#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/../.."
platform="${1:?Pass cmp-ios or swiftui-ios}"
output="${2:?Pass the output root}"
work="$(mktemp -d)"
device=
recording=
diagnostics="$output/$platform/sheet-open/recording.mov"
cleanup() {
  if [[ -n "$recording" ]]; then kill -INT "$recording" 2>/dev/null || true; fi
  if [[ -n "$device" ]]; then xcrun simctl delete "$device" || true; fi
  rm -rf "$work"
}
trap cleanup EXIT

case "$platform" in
  swiftui-ios)
    app="$work/ComposeFoundationAudit.app"
    bundle=com.toutiaoyan.compose.foundation.audit
    swiftui=shadcn/iosApp
    sdk="$(xcrun --sdk iphonesimulator --show-sdk-path)"
    mkdir "$app"
    cp "$swiftui/validation/IOSAuditInfo.plist" "$app/Info.plist"
    xcrun --sdk iphonesimulator swiftc -sdk "$sdk" -parse-as-library -swift-version 6 \
      -target "$(uname -m)-apple-ios14.0-simulator" \
      "$swiftui"/Sources/ComposeFoundationSwiftUI/*.swift \
      "$swiftui"/validation/{AuditFixtures,IOSAuditApp}.swift -o "$app/ComposeFoundationAudit"
    ;;
  cmp-ios)
    trigger_url=shadcncompose://animation/sheet-open
    xcodebuild -project app/iosApp/iosApp.xcodeproj -scheme iosApp -configuration Debug \
      -destination 'generic/platform=iOS Simulator' -derivedDataPath "$work/derived" CODE_SIGNING_ALLOWED=NO build
    app="$work/derived/Build/Products/Debug-iphonesimulator/ShadcnComposeCatalog.app"
    bundle="$(/usr/libexec/PlistBuddy -c 'Print CFBundleIdentifier' "$app/Info.plist")"
    ;;
  *) echo "Unsupported iOS platform $platform" >&2; exit 2 ;;
esac
device="$(xcrun simctl create 'Sheet animation' com.apple.CoreSimulator.SimDeviceType.iPhone-17-Pro \
  com.apple.CoreSimulator.SimRuntime.iOS-26-2)"
xcrun simctl boot "$device"
xcrun simctl bootstatus "$device" -b
xcrun simctl ui "$device" appearance light
xcrun simctl status_bar "$device" override --time '9:41' --dataNetwork wifi \
  --wifiMode active --wifiBars 3 --cellularMode active --cellularBars 4 \
  --batteryState discharging --batteryLevel 100
xcrun simctl install "$device" "$app"
xcrun simctl launch "$device" "$bundle" --sheet-animation
ready=false
for ((attempt = 0; attempt < 30; attempt++)); do
  xcrun simctl io "$device" screenshot "$work/ready.png" >/dev/null 2>&1
  if python3 - "$work/ready.png" <<'PY'
from PIL import Image
import sys

with Image.open(sys.argv[1]) as screenshot:
    red, green, blue = screenshot.convert("RGB").getpixel((24, 216))
sys.exit(0 if red > 180 and max(green, blue) < 80 else 1)
PY
  then
    ready=true
    break
  fi
  sleep 1
done
if [[ "$ready" != true ]]; then
  echo "The Sheet fixture did not render its readiness marker" >&2
  exit 1
fi
xcrun simctl io "$device" recordVideo --codec=h264 "$work/sheet.mov" >"$work/recording.log" 2>&1 &
recording=$!
for ((attempt = 0; attempt < 60; attempt++)); do
  if grep -q 'Recording started' "$work/recording.log"; then break; fi
  kill -0 "$recording"
  sleep 1
done
if ! grep -q 'Recording started' "$work/recording.log"; then
  cat "$work/recording.log"
  exit 1
fi
if [[ "$platform" == cmp-ios ]]; then
  sleep 1
  xcrun simctl openurl "$device" "$trigger_url"
  sleep 3
else
  # SwiftUI schedules the trigger in-process to avoid SpringBoard URL presentation.
  sleep 15
fi
kill -INT "$recording"
wait "$recording" || test "$?" -eq 130
recording=
cat "$work/recording.log"
mkdir -p "$(dirname "$diagnostics")"
cp "$work/sheet.mov" "$diagnostics"
ffprobe -v error -select_streams v:0 -show_entries stream=width,height,avg_frame_rate,duration "$diagnostics"
python3 shadcn/validation/animation_baseline.py "$platform" sheet-open --capture-only \
  --video "$diagnostics" --trigger-marker 24 216 --crop-top 246 --output "$output"
