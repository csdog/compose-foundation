"""Exercise Menubar and Navigation Menu with USB HID events on an iOS simulator."""

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
        rows = json.loads(
            subprocess.check_output([*client, "ui", "describe-all", "--api", "ax"], timeout=10)
        )
        return {row["AXLabel"]: row for row in rows if row.get("AXLabel")}

    def expect(*labels, absent=()):
        for _ in range(12):
            elements = read_elements()
            if set(labels) <= elements.keys() and set(absent).isdisjoint(elements):
                return elements
            time.sleep(0.2)
        raise AssertionError(f"Expected {labels} with {absent} absent, got {tuple(elements)}")

    def tap(label):
        frame = expect(label)[label]["frame"]
        x = round(frame["x"] + frame["width"] / 2)
        y = round(frame["y"] + frame["height"] / 2)
        subprocess.run([*client, "ui", "tap", str(x), str(y)], check=True, timeout=10)
        time.sleep(0.45)

    def press(key):
        subprocess.run([*client, "ui", "key", str(key), "--duration", "0.14"], check=True, timeout=10)
        time.sleep(0.3)

    subprocess.run(
        ["xcrun", "simctl", "launch", "--terminate-running-process", args.device, args.bundle,
         "--menus-interaction", "-AppleLanguages", "(en)", "-AppleLocale", "en_US"],
        check=True,
        timeout=15,
    )
    expect("File", "Result: No action")
    tap("File")
    assert not expect("New file", "Locked file")["Locked file"]["enabled"]
    press(81)  # Down
    press(40)  # Return
    expect("Result: Save file")

    tap("File")
    expect("New file")
    press(79)  # Right
    expect("Copy", "Paste")
    press(41)  # Escape
    expect("Result: Save file", "Edit", absent=("Copy", "Paste"))
    press(81)  # Down must reopen Edit from its restored trigger.
    expect("Copy", "Paste", absent=("New file",))
    press(40)
    expect("Result: Copy", "Edit", absent=("Copy", "Paste"))

    tap("Project")
    expect("Overview", "Settings")
    press(79)
    expect("User guide")
    press(41)
    expect("Result: Copy", "Resources", absent=("User guide",))
    press(81)  # Down must reopen Resources from its restored trigger.
    expect("User guide", absent=("Overview",))
    press(40)
    expect("Result: User guide", "Resources", absent=("User guide",))
    press(81)  # Selection must also return focus to Resources.
    expect("User guide", absent=("Overview",))
    press(41)
    expect("Result: User guide", absent=("User guide",))
    print("PASS: disabled items, group switching, action execution, and focus restoration after Escape and selection")


if __name__ == "__main__":
    main()
