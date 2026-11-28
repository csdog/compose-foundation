#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/../.."
platform="${1:?Pass cmp-ios or swiftui-ios}"
output="${2:?Pass the output root}"
work="$(mktemp -d)"
device=
recording=
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
xcrun simctl io "$device" recordVideo --codec=h264 "$work/sheet.mov" &
recording=$!
sleep 1
xcrun simctl launch "$device" "$bundle" --sheet-animation
sleep 5
kill -INT "$recording"
wait "$recording" || test "$?" -eq 130
recording=
python3 shadcn/validation/animation_baseline.py "$platform" sheet-open --capture-only \
  --video "$work/sheet.mov" --trigger-marker 24 216 --crop-top 246 --output "$output"
