#!/usr/bin/env bash
set -euo pipefail

platform="${1:?Pass the platform name}"
output="${2:?Pass an absolute artifact directory}"
shift 2
passes=(actual)
if [[ "${1:-}" == --update-baselines ]]; then
  passes+=(repeat)
  shift
fi
swiftui=shadcn/iosApp
plan=shadcn/validation/plan.json
components=()
while IFS= read -r component; do
  components+=("$component")
done < <(python3 -c 'import json, sys; print(*(sys.argv[2:] or json.load(open(sys.argv[1]))["components"]), sep="\n")' "$plan" "$@")

mkdir -p "$output"
work="$(mktemp -d)"
trap 'rm -rf "$work"' EXIT

case "$platform" in
  cmp-web)
    node --version
    printf 'Playwright %s\n' "${PLAYWRIGHT_MODULE:?Set PLAYWRIGHT_MODULE to the installed Playwright entry point}"
    ;;
  cmp-desktop)
    if [[ "${components[*]}" != popover ]]; then
      echo "CMP Desktop capture currently supports Popover. Select --component popover." >&2
      exit 2
    fi
    ;;
  cmp-android)
    android_device="${ANDROID_SERIAL:?Set ANDROID_SERIAL to the running CMP simulator}"
    adb -s "$android_device" shell getprop ro.build.fingerprint
    python3 shadcn/registry/build.py --output "$work/registry"
    ;;
  swiftui-ios)
    xcodebuild -version
    sw_vers
    python3 shadcn/registry/build.py --output "$work/registry"
    registry="$work/registry/swiftui/registry.json"
    ;;
  *) echo "No capture adapter for $platform" >&2; exit 2 ;;
esac

if [[ "$platform" == swiftui-ios ]]; then
  app="$work/ComposeFoundationAudit.app"
  bundle=com.toutiaoyan.compose.foundation.audit
  sdk="$(xcrun --sdk iphonesimulator --show-sdk-path)"
  mkdir "$app"
  cp "$swiftui/validation/IOSAuditInfo.plist" "$app/Info.plist"
  cp "$registry" "$app/registry.json"
  xcrun --sdk iphonesimulator swiftc -sdk "$sdk" -parse-as-library -swift-version 6 \
    -target "$(uname -m)-apple-ios14.0-simulator" \
    "$swiftui"/Sources/ComposeFoundationSwiftUI/*.swift \
    "$swiftui"/validation/{AuditFixtures,IOSAuditApp}.swift -o "$app/ComposeFoundationAudit"
  runtime='com.apple.CoreSimulator.SimRuntime.iOS-26-2'
  type='com.apple.CoreSimulator.SimDeviceType.iPhone-17-Pro'
  device="$(xcrun simctl create 'Compose Foundation screenshots' "$type" "$runtime")"
  trap 'xcrun simctl delete "$device" || true; rm -rf "$work"' EXIT
  printf '%s %s\n' "$runtime" "$type"
  xcrun simctl boot "$device"
  xcrun simctl bootstatus "$device" -b
  xcrun simctl status_bar "$device" override --time '9:41' --dataNetwork wifi \
    --wifiMode active --wifiBars 3 --cellularMode active --cellularBars 4 \
    --batteryState discharging --batteryLevel 100
  xcrun simctl spawn "$device" defaults write com.apple.Accessibility ReduceMotionEnabled -bool YES
  xcrun simctl install "$device" "$app"
  container="$(xcrun simctl get_app_container "$device" "$bundle" data)"
fi

capture_status=0
for pass in "${passes[@]}"; do
  mkdir -p "$work/$pass"
  case "$platform" in
    cmp-web)
      for component in "${components[@]}"; do
        title="$(python3 -c 'import json, sys; print(json.load(open(sys.argv[1]))["components"][sys.argv[2]]["title"])' "$plan" "$component")"
        node scripts/verify_component_browser.mjs --slug "$component" --title "$title" \
          --output "$output/browser/$pass/$component" || capture_status=1
      done
      ;;
    cmp-android)
      python3 shadcn/androidApp/validation/verify_android_catalog.py \
        --registry "$work/registry/registry.json" --device "$android_device" \
        --output "$output/device/$pass" --only "${components[@]}" || capture_status=1
      ;;
    cmp-desktop)
      COMPONENT_PARITY_SCREENSHOT_DIR="$work/$pass" \
        JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS:-} -Droborazzi.test.record=true" \
        xvfb-run -a -s '-screen 0 1024x768x24' ./gradlew :shadcn:core:jvmTest \
          --tests '*PopoverNovaScreenshotTest' --rerun --console=plain || capture_status=1
      ;;
    swiftui-ios)
      mobile_output="$container/Documents/ParityAudit"
      rm -rf "$mobile_output"
      xcrun simctl launch --terminate-running-process "$device" "$bundle" \
        -AppleLanguages '(en)' -AppleLocale en_US
      for ((attempt = 0; attempt < 180; attempt++)); do
        [[ ! -s "$mobile_output/result.txt" ]] || break
        sleep 1
      done
      grep '^PASS ' "$mobile_output/result.txt"
      cp "$mobile_output/result.txt" "$output/$pass-render.txt"
      cp "$mobile_output"/*.png "$work/$pass/"
      ;;
  esac
  for component in "${components[@]}"; do
    destination="$output/$pass/$component/initial"
    mkdir -p "$destination"
    for theme in light dark; do
      case "$platform" in
        cmp-web) source="$output/browser/$pass/$component/desktop-$theme.png" ;;
        cmp-android) source="$output/device/$pass/$theme/$component.png" ;;
        cmp-desktop) source="$work/$pass/popover_nova_basic_$theme.png" ;;
        swiftui-ios) source="$work/$pass/$component-$theme.png" ;;
      esac
      if [[ "$platform" == cmp-desktop ]]; then
        python3 - "$source" "$destination/$theme.png" <<'PY' || capture_status=1
import sys
from PIL import Image

with Image.open(sys.argv[1]) as image:
    bounds = image.convert("RGBA").getchannel("A").getbbox()
    if bounds is None:
        raise ValueError("The desktop screenshot contains no visible pixels")
    image.crop(bounds).save(sys.argv[2])
PY
      else
        cp "$source" "$destination/$theme.png" || capture_status=1
      fi
    done
  done
done
exit "$capture_status"
