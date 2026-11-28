"""Verify native iPad Radio Group selection over USB keyboard events."""

import json
import os
from pathlib import Path
import subprocess
import sys
import time

client = [sys.argv[1], "--companion", "127.0.0.1:10882", "ui"]
device = sys.argv[2]
screenshots = Path(os.environ["RUNNER_TEMP"]) / "radio-hid"
screenshots.mkdir(exist_ok=True)


def labels():
    output = subprocess.check_output([*client, "describe-all", "--api", "ax"], timeout=25)
    return {item["AXLabel"] for item in json.loads(output) if item.get("AXLabel")}


def expect(label, timeout=8):
    end = time.monotonic() + timeout
    while time.monotonic() < end:
        try:
            if label in labels():
                return
        except (subprocess.CalledProcessError, subprocess.TimeoutExpired):
            pass
        time.sleep(0.4)
    raise AssertionError(f"Expected {label!r}, got {sorted(labels())}")


def key(code):
    subprocess.run([*client, "key", str(code), "--duration", "0.14"], check=True, timeout=15)
    time.sleep(0.35)


for direction in ("ltr", "rtl"):
    subprocess.run(["xcrun", "simctl", "launch", "--terminate-running-process", device,
                    "com.toutiaoyan.compose.foundation.audit", f"--{direction}"], check=True, timeout=15)
    expect("Selected: none", timeout=90)
    key(44)  # Space
    for _ in range(4):
        if "Selected: regular" in labels():
            break
        key(43)  # Tab enters the group if it doesn't already have focus.
        key(44)
    expect("Selected: regular")
    key(79 if direction == "ltr" else 80)
    expect("Selected: compact")
    subprocess.run(["xcrun", "simctl", "io", device, "screenshot", str(screenshots / f"{direction}.png")],
                   check=True, timeout=20)
    key(79 if direction == "ltr" else 80)
    expect("Selected: regular")
    key(81)
    expect("Selected: compact")
    print(f"PASS: {direction} Radio Group selected by arrow, skipped disabled, and wrapped", flush=True)
