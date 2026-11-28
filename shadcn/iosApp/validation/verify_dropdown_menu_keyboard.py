"""Exercise native Dropdown Menu keyboard dismissal and selection with iOS simulator HID."""

import argparse
import json
import subprocess
import time


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--device", required=True)
    parser.add_argument("--bundle", default="com.toutiaoyan.compose.foundation.audit")
    parser.add_argument("--companion", required=True)
    parser.add_argument("--idb", default="idb")
    args = parser.parse_args()
    client = [args.idb, "--companion", args.companion, "ui"]

    def expect(*labels, absent=(), timeout=10):
        deadline = time.monotonic() + timeout
        elements = {}
        last_error = None
        while time.monotonic() < deadline:
            try:
                output = subprocess.check_output([*client, "describe-all", "--api", "ax"], timeout=28)
                elements = {row["AXLabel"]: row for row in json.loads(output) if row.get("AXLabel")}
                if set(labels) <= elements.keys() and set(absent).isdisjoint(elements):
                    return elements
            except (subprocess.CalledProcessError, subprocess.TimeoutExpired) as error:
                last_error = error
            time.sleep(0.4)
        raise AssertionError(f"Expected {labels}, absent {absent}, got {tuple(elements)}") from last_error

    def send(*event):
        subprocess.run([*client, *event], check=True, timeout=15)
        time.sleep(0.55)

    def open_menu():
        frame = expect("Open actions")["Open actions"]["frame"]
        x = round(frame["x"] + frame["width"] / 2)
        y = round(frame["y"] + frame["height"] / 2)
        send("tap", str(x), str(y))
        rows = expect("Profile", "API", "Settings")
        assert not rows["API"]["enabled"], rows["API"]

    subprocess.run(
        ["xcrun", "simctl", "launch", "--terminate-running-process", args.device, args.bundle,
         "--dropdown-menu-interaction", "-AppleLanguages", "(en)", "-AppleLocale", "en_US"],
        check=True,
        timeout=180,
    )
    expect("Open actions", "No action", timeout=90)

    open_menu()
    send("key", "41", "--duration", "0.14")  # Escape
    expect("Open actions", "No action", absent=("Profile", "API", "Settings"))

    open_menu()
    send("key", "81", "--duration", "0.14")  # Down to Profile
    send("key", "81", "--duration", "0.14")  # Skip API and highlight Settings
    send("key", "40", "--duration", "0.14")  # Return
    expect("Open actions", "Settings selected", absent=("Profile", "API", "Settings"))
    print("PASS: native Dropdown Menu dismisses on Escape and skips the disabled action")


if __name__ == "__main__":
    main()
