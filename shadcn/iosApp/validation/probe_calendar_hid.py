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


def launch(priority=False):
    mode = "--calendar-priority-probe" if priority else "--calendar-navigation"
    subprocess.run(["xcrun", "simctl", "launch", "--terminate-running-process", device,
                    "com.toutiaoyan.compose.foundation.audit", mode, "--calendar-keys",
                    "-AppleLanguages", "(en)", "-AppleLocale", "en_US"],
                   check=True, timeout=50)
    assert wait_for("Selected: 2025-01-31", timeout=90), labels()


def test_priority(label):
    launch(priority=True)
    key(79)  # Right
    for _ in range(4):
        if "Priority probe: Right" in labels():
            break
        key(43)
        key(79)
    received = "Priority probe: Right" in labels()
    print(f"Priority command, {label}: delivered={received}", flush=True)
    capture(f"priority-{label}.png")


test_priority("normal")
enable_full_keyboard_access()
test_priority("full-access")
for tabs in range(8):
    launch()
    for _ in range(tabs):
        key(43)  # Tab
    capture(f"native-tab-{tabs}.png")
    key(44)  # Space
    if "February 1, 2025" in labels():
        assert "Selected: 2025-01-31" in labels(), labels()
        print(f"Native Tab and Space navigated from January to February after {tabs} Tab events", flush=True)
        capture("native-next-month.png")
        break
else:
    raise AssertionError(f"Tab and Space did not activate Next month: {sorted(labels())}")
for _ in range(8):
    key(43)
    key(44)
    result = next((value for value in labels() if value.startswith("Selected: 2025-02-")), None)
    if result is not None:
        print(f"Native Tab and Space committed {result}", flush=True)
        capture("native-date-selected.png")
        break
else:
    raise AssertionError(f"Tab and Space did not select a February date: {sorted(labels())}")
