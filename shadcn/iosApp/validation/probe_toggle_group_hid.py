"""Exercise Toggle Group with iPad hardware-keyboard HID and touch events."""

import json
import os
from pathlib import Path
import subprocess
import sys
import time

client = [sys.argv[1], "--companion", "127.0.0.1:10882", "ui"]
device = sys.argv[2]
bundle = "com.toutiaoyan.compose.foundation.audit"
screenshots = Path(os.environ["RUNNER_TEMP"]) / "toggle-hid"
screenshots.mkdir(exist_ok=True)


def rows():
    output = subprocess.check_output([*client, "describe-all", "--api", "ax"], timeout=25)
    return {item["AXLabel"]: item for item in json.loads(output) if item.get("AXLabel")}


def tap(label):
    frame = rows()[label]["frame"]
    x = round(frame["x"] + frame["width"] / 2)
    y = round(frame["y"] + frame["height"] / 2)
    subprocess.run([*client, "tap", str(x), str(y)], check=True, timeout=15)
    time.sleep(0.4)


def expect(label, timeout=8):
    end = time.monotonic() + timeout
    observed = []
    while time.monotonic() < end:
        try:
            observed = sorted(rows())
            if label in observed:
                return
        except (subprocess.CalledProcessError, subprocess.TimeoutExpired):
            pass
        time.sleep(0.4)
    raise AssertionError(f"Expected {label!r}, got {observed}")


def press(code):
    subprocess.run([*client, "key", str(code), "--duration", "0.14"], check=True, timeout=15)
    time.sleep(0.35)


for mode, forward in (("ltr", 79), ("rtl", 80), ("vertical", 81)):
    if mode != "ltr":
        subprocess.run(["xcrun", "simctl", "terminate", device, bundle], check=True, timeout=35)
    subprocess.run(["xcrun", "simctl", "launch", device, bundle, f"--{mode}"], check=True, timeout=60)
    expect("Selected: none", timeout=90)
    press(44)  # Space; keyboard focus may already be in the group.
    for _ in range(4):
        if "Selected: bold" in rows():
            break
        press(43)  # Tab
        press(44)
    expect("Selected: bold")
    press(forward)
    expect("Selected: bold")  # Arrows move focus without writing pressed state.
    subprocess.run(["xcrun", "simctl", "io", device, "screenshot", str(screenshots / f"{mode}.png")],
                   check=True, timeout=20)
    press(44)
    expect("Selected: bold, underline")
    press(forward)
    press(44)
    expect("Selected: underline")  # Wraps to Bold and unpresses it.
    before = next(label for label in rows() if label.startswith("After group: "))
    press(43)
    press(44)
    expect(f"After group: {int(before.split(': ')[1]) + 1}")
    expect("Selected: underline")
    assert not rows()["Italic"]["enabled"], rows()["Italic"]
    tap("Italic")
    expect("Selected: underline")
    tap("Bold")
    expect("Selected: bold, underline")
    print(f"PASS: {mode} Toggle Group roves without changing selection, skips disabled, wraps, exits, and responds to touch",
          flush=True)
