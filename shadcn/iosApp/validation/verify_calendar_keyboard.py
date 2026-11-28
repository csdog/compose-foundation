"""Exercise Calendar with USB keyboard events on an iPad simulator."""

import argparse
import json
from pathlib import Path
import subprocess
import time


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--device", required=True)
    parser.add_argument("--companion", required=True)
    parser.add_argument("--bundle", default="com.toutiaoyan.compose.foundation.audit")
    parser.add_argument("--idb", default="idb")
    parser.add_argument("--screenshots", type=Path)
    args = parser.parse_args()
    client = [args.idb, "--companion", args.companion, "ui"]

    def labels():
        output = subprocess.check_output([*client, "describe-all", "--api", "ax"], timeout=25)
        return {element["AXLabel"] for element in json.loads(output) if element.get("AXLabel")}

    def wait_for(label, timeout):
        deadline = time.monotonic() + timeout
        while time.monotonic() < deadline:
            try:
                if label in labels():
                    return
            except (subprocess.CalledProcessError, subprocess.TimeoutExpired):
                pass
            time.sleep(0.35)
        raise AssertionError(f"Missing {label!r}")

    def key(code):
        subprocess.run([*client, "key", str(code), "--duration", "0.14"], check=True, timeout=15)
        time.sleep(0.35)

    def capture(name):
        if args.screenshots is not None:
            args.screenshots.mkdir(parents=True, exist_ok=True)
            subprocess.run(["xcrun", "simctl", "io", args.device, "screenshot", str(args.screenshots / name)],
                           check=True, timeout=20)

    subprocess.run(["xcrun", "simctl", "launch", "--terminate-running-process", args.device, args.bundle,
                    "--calendar-navigation", "--calendar-keys", "-AppleLanguages", "(en)", "-AppleLocale", "en_US"],
                   check=True, timeout=15)
    wait_for("Selected: 2025-01-31", timeout=90)
    capture("before.png")
    key(79)  # Right
    for _ in range(4):
        if "February 1, 2025" in labels():
            break
        key(43)  # Tab enters the date grid if it does not have initial keyboard focus.
        key(79)
    assert "February 1, 2025" in labels(), "Date focus did not cross into February"
    assert "Selected: 2025-01-31" in labels(), "An arrow changed the selection"
    capture("after-arrow.png")
    key(40)  # Return
    wait_for("Selected: 2025-02-01", timeout=6)
    key(81)  # Down
    assert "Selected: 2025-02-01" in labels(), "Down changed the selection"
    key(44)  # Space
    wait_for("Selected: 2025-02-08", timeout=6)
    print("PASS: iPad USB keyboard crosses months and commits on Return or Space")


if __name__ == "__main__":
    main()
