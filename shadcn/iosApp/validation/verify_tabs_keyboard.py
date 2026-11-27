"""Exercise the Tabs fixture with USB HID events on an iOS simulator."""

import argparse
import json
import subprocess
import time


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--device", required=True)
    parser.add_argument("--bundle", required=True)
    parser.add_argument("--companion", required=True)
    parser.add_argument("--idb", default="idb")
    args = parser.parse_args()
    client = [args.idb, "--companion", args.companion]

    def read_elements():
        return json.loads(
            subprocess.check_output([*client, "ui", "describe-all", "--api", "ax"], timeout=10)
        )

    def expect(*labels):
        elements = read_elements()
        actual = {row.get("AXLabel") for row in elements}
        assert set(labels) <= actual, f"Expected {labels}, got {actual}"
        return elements

    def press(key):
        subprocess.run([*client, "ui", "key", str(key), "--duration", "0.14"], check=True, timeout=10)
        time.sleep(0.35)

    def launch(option):
        subprocess.run(
            ["xcrun", "simctl", "launch", "--terminate-running-process", args.device, args.bundle,
             "--tabs-interaction", option, "-AppleLanguages", "(en)", "-AppleLocale", "en_US"],
            check=True,
            timeout=15,
        )
        time.sleep(1)
        expect("Automatic panel: Overview", "Manual panel: Overview")

    launch("--tabs-rtl")
    press(79)
    if all(row.get("AXLabel") != "Automatic panel: Activity" for row in read_elements()):
        press(79)
    expect("Automatic panel: Activity", "Manual panel: Overview")
    press(79)
    expect("Automatic panel: Overview", "Manual panel: Overview")
    press(80)
    expect("Automatic panel: Activity", "Manual panel: Overview")
    press(43)
    press(80)
    expect("Automatic panel: Activity", "Manual panel: Overview")
    press(40)
    expect("Automatic panel: Activity", "Manual panel: Settings")

    launch("--tabs-overflow")
    for _ in range(15):
        press(79)
        elements = read_elements()
        if any(row.get("AXLabel") == "Automatic panel: Extra 8" for row in elements):
            break
    else:
        raise AssertionError("Keyboard navigation did not reach the final tab")

    selected = next(
        row for row in elements
        if row.get("AXLabel") == "Extra section 8" and "Selected" in row["traits"]
    )
    viewport = next(row["frame"] for row in elements if row["type"] == "Application")
    frame = selected["frame"]
    assert viewport["x"] <= frame["x"], frame
    assert frame["x"] + frame["width"] <= viewport["x"] + viewport["width"], frame

    launch("--tabs-disabled")
    for key in (79, 77, 44, 40):  # Right, End, Space, Return
        press(key)
        expect("Automatic panel: Overview", "Manual panel: Overview")
    print("PASS: RTL movement, cross-group Tab, manual Return, overflow scrolling, disabled strips")


if __name__ == "__main__":
    main()
