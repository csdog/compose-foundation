"""Exercise Calendar on iPad using simulated USB keyboard events."""

import json
import os
from pathlib import Path
import subprocess
import sys
import time

client = [sys.argv[1], "--companion", "127.0.0.1:10882", "ui"]
device = sys.argv[2]
screenshots = Path(os.environ["RUNNER_TEMP"]) / "calendar-hid"
screenshots.mkdir(exist_ok=True)


def rows(attempts=3):
    for attempt in range(attempts):
        try:
            output = subprocess.check_output([*client, "describe-all", "--api", "ax"], timeout=25)
            return json.loads(output)
        except (subprocess.CalledProcessError, subprocess.TimeoutExpired):
            if attempt + 1 == attempts:
                raise
            print(f"Retrying simulator accessibility read ({attempt + 2}/{attempts})", flush=True)
            time.sleep(1)


def labels(attempts=3):
    return {item["AXLabel"] for item in rows(attempts) if item.get("AXLabel")}


def choose(names, scroll=False, switch=False, x_fraction=0.72):
    for _ in range(7):
        elements = rows()
        for row in elements:
            if row.get("AXLabel") not in names or not row.get("frame"):
                continue
            if switch and str(row.get("type", "")).lower() not in {"switch", "checkbox"}:
                continue
            frame = row["frame"]
            if frame["width"] > 0 and frame["height"] > 0:
                return row
        if scroll:
            app = next(row["frame"] for row in elements if row.get("type") == "Application")
            x = round(app["width"] * x_fraction)
            subprocess.run([*client, "swipe", str(x), str(round(app["height"] * 0.74)),
                            str(x), str(round(app["height"] * 0.31)), "--duration", "0.45"],
                           check=True, timeout=15)
        time.sleep(0.7)
    raise AssertionError(f"Could not locate {names}: {[(x.get('AXLabel'), x.get('type'), x.get('AXValue')) for x in rows()]}")


def tap(row):
    frame = row["frame"]
    x = round(frame["x"] + frame["width"] / 2)
    y = round(frame["y"] + frame["height"] / 2)
    subprocess.run([*client, "tap", str(x), str(y)], check=True, timeout=15)
    time.sleep(0.6)


def enable_full_keyboard_access():
    subprocess.run(["xcrun", "simctl", "launch", device, "com.apple.Preferences"], check=True, timeout=50)
    assert wait_for("Sidebar", timeout=90) or "Accessibility" in labels(), labels()
    capture("settings-initial.png")
    if "Accessibility" in labels():
        tap(choose({"Accessibility"}))
    else:
        # The simulator AX tree exposes the detail pane but omits the visible sidebar rows.
        app = next(row["frame"] for row in rows() if row.get("type") == "Application")
        x = round(app["width"] * 0.16)
        # The first-run Apple Intelligence row shifts the sidebar entry below General.
        for fraction in (0.364, 0.314):
            y = round(app["height"] * fraction)
            subprocess.run([*client, "tap", str(x), str(y), "--duration", "0.15"], check=True, timeout=15)
            if wait_for("Accessibility", timeout=3):
                break
    if not wait_for("Accessibility", timeout=8):
        capture("settings-accessibility.png")
        raise AssertionError(f"Settings sidebar did not open Accessibility: {sorted(labels())}")
    tap(choose({"Keyboards & Typing", "Keyboards"}, scroll=True))
    tap(choose({"Full Keyboard Access", "Full Keyboard Access, Off", "Full Keyboard Access, On"}))
    capture("settings-full-keyboard-access.png")
    options = {"Full Keyboard Access", "Full Keyboard Access, Off", "Full Keyboard Access, On"}
    time.sleep(1.6)
    for offset in (32, 26, 36):
        toggle = choose(options, switch=True)
        if str(toggle.get("AXValue")).lower() in {"1", "on", "true"}:
            break
        frame = toggle["frame"]
        x = round(frame["x"] + frame["width"] - offset)
        y = round(frame["y"] + frame["height"] / 2)
        subprocess.run([*client, "tap", str(x), str(y), "--duration", "0.12"], check=True, timeout=15)
        time.sleep(2)
    enabled = choose(options, switch=True)
    capture("settings-after-toggle.png")
    assert str(enabled.get("AXValue")).lower() in {"1", "on", "true"}, enabled
    capture("settings-enabled.png")
    print("Enabled Full Keyboard Access in iPad Settings", flush=True)


def wait_for(label, timeout=6):
    deadline = time.monotonic() + timeout
    while time.monotonic() < deadline:
        try:
            if label in labels(attempts=1):
                return True
        except (subprocess.CalledProcessError, subprocess.TimeoutExpired):
            pass
        time.sleep(0.35)
    return False


def key(code):
    subprocess.run([*client, "key", str(code), "--duration", "0.14"], check=True, timeout=30)
    time.sleep(0.35)


def capture(name):
    subprocess.run(["xcrun", "simctl", "io", device, "screenshot", str(screenshots / name)], check=True, timeout=35)


def launch(label, *arguments):
    subprocess.run(["xcrun", "simctl", "launch", "--terminate-running-process", device,
                    "com.toutiaoyan.compose.foundation.audit", *arguments,
                    "-AppleLanguages", "(en)", "-AppleLocale", "en_US"],
                   check=True, timeout=50)
    assert wait_for(label, timeout=90), labels()


def probe(name, initial, accepted, *arguments):
    launch(initial, *arguments)
    capture(f"{name}-initial.png")
    for tabs in range(5):
        key(44)  # Space performs the current Full Keyboard Access action.
        current = labels()
        destination = accepted(current)
        if destination is not None:
            print(f"INPUT {name}: activated after {tabs} Tab events: {destination}", flush=True)
            capture(f"{name}-activated.png")
            return True
        key(43)  # Tab moves Full Keyboard Access focus.
    print(f"INPUT {name}: no activation after five attempts; labels={sorted(current)}", flush=True)
    capture(f"{name}-last.png")
    return False


def selected(prefix, current):
    return next((label for label in current if label.startswith(prefix) and label != f"{prefix}none"), None)


enable_full_keyboard_access()
assert probe("uikit", "UIKit result: none", lambda current: selected("UIKit result: ", current),
             "--fka-native-control"), "The UIKit control must receive focus and activation"
for name, arguments in (
    ("swiftui-plain", ("--fka-swiftui-control",)),
    ("swiftui-scroll", ("--fka-swiftui-control", "--fka-scroll")),
):
    probe(name, "SwiftUI result: none", lambda current: selected("SwiftUI result: ", current), *arguments)
for name, flags in (
    ("calendar-toggle-date", ()),
    ("calendar-native-grid", ("--fka-calendar-native-grid",)),
    ("calendar-explicit-ax", ("--fka-calendar-ax-interactive",)),
):
    probe(name, "Selected: 2025-01-31",
          lambda current: next((label for label in current if label == "February 2025" or
                                label.startswith("Selected: ") and label != "Selected: 2025-01-31"), None),
          "--fka-calendar-toggle-date", *flags)
