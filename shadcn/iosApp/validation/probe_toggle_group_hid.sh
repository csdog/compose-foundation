#!/usr/bin/env bash
set -euo pipefail
work="$(mktemp -d)"
device=''
server=''
cleanup() {
    if [[ -n "$server" ]]; then kill "$server" || true; fi
    if [[ -s "$work/companion.log" ]]; then tail -n 12 "$work/companion.log"; fi
    if [[ -n "$device" ]]; then xcrun simctl delete "$device" || true; fi
    rm -rf "$work"
}
trap cleanup EXIT
swiftui=shadcn/iosApp
app="$work/ComposeFoundationAudit.app"
bundle=com.toutiaoyan.compose.foundation.audit
mkdir -p "$app" "$work/companion"
cp "$swiftui/validation/IOSAuditInfo.plist" "$app/Info.plist"
curl --retry 3 -fsSL https://github.com/facebook/idb/releases/download/v1.7.8/idb-companion.macos-arm64.tar.gz -o "$work/companion.tar.gz"
printf '%s  %s\n' '44369a2df0b62e3a1764de61bd09280999c7e9b27ea8f20bff1ea3a9db0302a2' "$work/companion.tar.gz" | shasum -a 256 -c
tar -xzf "$work/companion.tar.gz" -C "$work/companion"
python3 -m venv "$work/venv"
"$work/venv/bin/pip" install 'https://github.com/facebook/idb/releases/download/v1.7.8/fb_idb-1.7.8-py3-none-any.whl'

device="$(xcrun simctl create 'Toggle Group USB HID audit' com.apple.CoreSimulator.SimDeviceType.iPad-mini-A17-Pro com.apple.CoreSimulator.SimRuntime.iOS-26-2)"
xcrun simctl boot "$device"
sdk="$(xcrun --sdk iphonesimulator --show-sdk-path)"
xcrun --sdk iphonesimulator swiftc -sdk "$sdk" -parse-as-library -swift-version 6 \
    -target "$(uname -m)-apple-ios14.0-simulator" \
    "$swiftui"/Sources/ComposeFoundationSwiftUI/Foundation{Theme,Toggle,ToggleGroup,TabsNavigation}.swift \
    "$swiftui"/validation/ToggleGroupHIDAudit.swift -o "$app/ComposeFoundationAudit"
xcrun simctl bootstatus "$device" -b
open -a Simulator --args -CurrentDeviceUDID "$device"
xcrun simctl install "$device" "$app"
"$work/companion/idb_companion" --udid "$device" --grpc-port 10882 > "$work/companion.log" 2>&1 &
server=$!
for _ in {1..30}; do
    if nc -z 127.0.0.1 10882; then break; fi
    kill -0 "$server"
    sleep 1
done
"$work/venv/bin/python" "$swiftui/validation/probe_toggle_group_hid.py" "$work/venv/bin/idb" "$device"
