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


def labels():
    output = subprocess.check_output([*client, "describe-all", "--api", "ax"], timeout=25)
    return {item["AXLabel"] for item in json.loads(output) if item.get("AXLabel")}


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


assert wait_for("Selected: 2025-01-31", timeout=90), labels()
capture("before.png")
key(79)  # Right
for _ in range(4):
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
