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
    adb -s "$android_device" shell dumpsys window displays > "$work/android-displays.txt"
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
  cp app/shared/src/commonMain/composeResources/font/geist_{medium,regular}.ttf "$app/"
  cp "$plan" "$app/parity-plan.json"
  xcrun --sdk iphonesimulator swiftc -sdk "$sdk" -parse-as-library -swift-version 6 \
    -target "$(uname -m)-apple-ios14.0-simulator" \
    "$swiftui"/Sources/ComposeFoundationSwiftUI/*.swift \
    "$swiftui"/validation/{AuditFixtures,IOSAuditApp}.swift -o "$app/ComposeFoundationAudit"
fi

capture_ios_group() {
  local type="com.apple.CoreSimulator.SimDeviceType.$1"
  shift
  local runtime='com.apple.CoreSimulator.SimRuntime.iOS-26-2'
  local -a audit_components=()
  local component pass container mobile_output attempt
  for component in "$@"; do
    audit_components+=("--audit-component=$component")
  done
  device="$(xcrun simctl create 'Compose Foundation screenshots' "$type" "$runtime")"
  printf '%s %s\n' "$runtime" "$type"
  xcrun simctl boot "$device"
  xcrun simctl bootstatus "$device" -b
  xcrun simctl status_bar "$device" override --time '9:41' --dataNetwork wifi \
    --wifiMode active --wifiBars 3 --cellularMode active --cellularBars 4 \
    --batteryState discharging --batteryLevel 100
  xcrun simctl spawn "$device" defaults write com.apple.Accessibility ReduceMotionEnabled -bool YES
  xcrun simctl install "$device" "$app"
  container="$(xcrun simctl get_app_container "$device" "$bundle" data)"
  mobile_output="$container/Documents/ParityAudit"
  for pass in "${passes[@]}"; do
    mkdir -p "$work/$pass"
    rm -rf "$mobile_output"
    xcrun simctl launch --terminate-running-process "$device" "$bundle" \
      -AppleLanguages '(en)' -AppleLocale en_US "${audit_components[@]}"
    for ((attempt = 0; attempt < 180; attempt++)); do
      [[ ! -s "$mobile_output/result.txt" ]] || break
      sleep 1
    done
    if ! grep '^PASS ' "$mobile_output/result.txt"; then
      if [[ -f "$mobile_output/result.txt" ]]; then
        cat "$mobile_output/result.txt" >&2
      else
        echo "iOS audit did not finish before its deadline" >&2
      fi
      return 1
    fi
    printf '%s\n' "$(cat "$mobile_output/result.txt")" >> "$output/$pass-render.txt"
    cp "$mobile_output"/*.png "$work/$pass/"
  done
  xcrun simctl shutdown "$device"
  xcrun simctl delete "$device"
  device=''
}

if [[ "$platform" == swiftui-ios ]]; then
  device=''
  trap 'if [[ -n "$device" ]]; then xcrun simctl shutdown "$device" || true; xcrun simctl delete "$device" || true; fi; rm -rf "$work"' EXIT
  groups="$(python3 - "$plan" "${components[@]}" <<'PYTHON'
import json
import sys

components = json.load(open(sys.argv[1]))["components"]
groups = {}
for component in sys.argv[2:]:
    device = components[component].get("swiftuiDevice", "iPhone-17-Pro")
    groups.setdefault(device, []).append(component)
for device, slugs in groups.items():
    print(device, *slugs)
PYTHON
)"
  for pass in "${passes[@]}"; do
    : > "$output/$pass-render.txt"
  done
  while read -r device_type group; do
    read -ra slugs <<< "$group"
    capture_ios_group "$device_type" "${slugs[@]}"
  done <<< "$groups"
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
        --output "$output/device/$pass" --retry-failed --only "${components[@]}" || capture_status=1
      ;;
    cmp-desktop)
      COMPONENT_PARITY_SCREENSHOT_DIR="$work/$pass" \
        JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS:-} -Droborazzi.test.record=true" \
        xvfb-run -a -s '-screen 0 1024x768x24' ./gradlew :shadcn:core:jvmTest \
          --tests '*PopoverNovaScreenshotTest' --rerun --console=plain || capture_status=1
      ;;
    swiftui-ios)
      python3 - "$work/$pass" <<'PYTHON' || capture_status=1
from pathlib import Path
import sys

from PIL import Image

directory = Path(sys.argv[1])
for theme in ("light", "dark"):
    initial = directory / f"popover-default-initial-{theme}.png"
    if not initial.is_file():
        continue
    with Image.open(initial) as image:
        expected_background = image.convert("RGB").getpixel((image.width - 1, image.height - 1))
    for capture in directory.glob(f"popover-*-{theme}.png"):
        with Image.open(capture) as image:
            background = image.convert("RGB").getpixel((image.width - 1, image.height - 1))
        if background != expected_background:
            raise ValueError(f"{capture.name}: drawing the overlay changed the empty viewport background")
PYTHON
      ;;
  esac
  for component in "${components[@]}"; do
    if [[ "$platform" == swiftui-ios && "$component" == popover ]]; then
      while IFS= read -r key; do
        for theme in light dark; do
          destination="$output/$pass/$component/${key%%:*}/${key##*:}"
          mkdir -p "$destination"
          cp "$work/$pass/$component-${key/:/-}-$theme.png" "$destination/$theme.png" || capture_status=1
        done
      done < <(python3 -c 'import json, sys; print(*(c["variant"] + ":" + c["status"] for c in json.load(open(sys.argv[1]))["components"]["popover"]["platformCases"]["swiftui-ios"]), sep="\n")' "$plan")
      continue
    fi
    destination="$output/$pass/$component/initial"
    mkdir -p "$destination"
    for theme in light dark; do
      case "$platform" in
        cmp-web) source="$output/browser/$pass/$component/desktop-$theme.png" ;;
        cmp-android) source="$output/device/$pass/$theme/$component.png" ;;
        cmp-desktop) source="$work/$pass/popover_nova_basic_$theme.png" ;;
        swiftui-ios) source="$work/$pass/$component-$theme.png" ;;
      esac
      if [[ "$platform" == cmp-android ]]; then
        [[ -f "$source" ]] || { capture_status=1; continue; }
        python3 - "$source" "$destination/$theme.png" "$work/android-displays.txt" <<'PY' || capture_status=1
from pathlib import Path
import re
import sys
from PIL import Image

frames = re.findall(
    r"\b(?:mType|type)=statusBars\s+(?:mFrame|frame)=\[0,0\]\[(\d+),(\d+)\]",
    Path(sys.argv[3]).read_text(),
)
with Image.open(sys.argv[1]) as image:
    heights = [int(height) for width, height in frames if int(width) == image.width]
    top = max(heights, default=0)
    if not 0 < top < image.height:
        raise ValueError("Could not find the Android status bar inset for this screenshot")
    image.crop((0, top, image.width, image.height)).save(sys.argv[2])
PY
      elif [[ "$platform" == cmp-desktop ]]; then
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
