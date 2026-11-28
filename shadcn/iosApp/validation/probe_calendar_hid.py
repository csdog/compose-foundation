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


def rows():
    output = subprocess.check_output([*client, "describe-all", "--api", "ax"], timeout=25)
    return json.loads(output)


def labels():
    return {item["AXLabel"] for item in rows() if item.get("AXLabel")}


def choose(names, scroll=False, switch=False):
    for _ in range(7):
        elements = rows()
        for row in elements:
            if row.get("AXLabel") not in names or not row.get("frame"):
                continue
            if switch and "switch" not in str(row.get("type", "")).lower():
                continue
            frame = row["frame"]
            if frame["width"] > 0 and frame["height"] > 0:
                return row
        if scroll:
            app = next(row["frame"] for row in elements if row.get("type") == "Application")
            x = round(app["width"] * 0.72)
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
    subprocess.run(["xcrun", "simctl", "launch", device, "com.apple.Preferences"], check=True, timeout=15)
    assert wait_for("Accessibility", timeout=90), labels()
    tap(choose({"Accessibility"}))
    tap(choose({"Keyboards & Typing", "Keyboards"}, scroll=True))
    tap(choose({"Full Keyboard Access"}))
    toggle = choose({"Full Keyboard Access"}, switch=True)
    if str(toggle.get("AXValue")).lower() not in {"1", "on", "true"}:
        tap(toggle)
    enabled = choose({"Full Keyboard Access"}, switch=True)
    assert str(enabled.get("AXValue")).lower() in {"1", "on", "true"}, enabled
    print("Enabled Full Keyboard Access in iPad Settings", flush=True)


def wait_for(label, timeout=6):
    deadline = time.monotonic() + timeout
    while time.monotonic() < deadline:
        try:
            if label in labels():
                return True
        except (subprocess.CalledProcessError, subprocess.TimeoutExpired):
            pass
        time.sleep(0.35)
    return False


def key(code):
    subprocess.run([*client, "key", str(code), "--duration", "0.14"], check=True, timeout=15)
    time.sleep(0.35)


def capture(name):
    subprocess.run(["xcrun", "simctl", "io", device, "screenshot", str(screenshots / name)], check=True, timeout=20)


enable_full_keyboard_access()
subprocess.run(["xcrun", "simctl", "launch", device, "com.toutiaoyan.compose.foundation.audit",
                "--calendar-navigation", "--calendar-keys", "-AppleLanguages", "(en)", "-AppleLocale", "en_US"],
               check=True, timeout=15)
assert wait_for("Selected: 2025-01-31", timeout=90), labels()
capture("before.png")
key(79)  # Right
for _ in range(5):
    if "February 1, 2025" in labels():
        break
    key(43)  # Tab
    key(79)
else:
    capture("focus-entry.png")
    raise AssertionError(f"Date focus did not move into February: {sorted(labels())}")
assert "Selected: 2025-01-31" in labels(), labels()
capture("after-arrow.png")
key(40)  # Return
assert wait_for("Selected: 2025-02-01"), labels()
key(81)  # Down
assert "Selected: 2025-02-01" in labels(), labels()
key(44)  # Space
assert wait_for("Selected: 2025-02-08"), labels()
print("PASS: iPad USB keyboard moves focus across months and commits only on Return or Space")
