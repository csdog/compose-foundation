"""Verify Dialog and Sheet focus using hardware keys on the iOS simulator."""

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

    def labels():
        rows = json.loads(
            subprocess.check_output([*client, "ui", "describe-all", "--api", "ax"], timeout=10)
        )
        return {row["AXLabel"] for row in rows if row.get("AXLabel")}

    def expect(*wanted):
        actual = labels()
        assert set(wanted) <= actual, f"Expected {wanted}, got {actual}"

    def key(code):
        subprocess.run([*client, "ui", "key", str(code), "--duration", "0.14"], check=True, timeout=10)
        time.sleep(0.65)

    subprocess.run(
        ["xcrun", "simctl", "launch", "--terminate-running-process", args.device, args.bundle,
         "--overlay-focus", "-AppleLanguages", "(en)", "-AppleLocale", "en_US"],
        check=True,
        timeout=15,
    )
    time.sleep(1)
    expect("Open dialog", "Open sheet")

    for _ in range(2):
        key(43)  # Tab
        key(44)  # Space
        if "Audit dialog" in labels():
            break
    expect("Audit dialog", "Close")
    key(40)  # Return activates the initially focused close button.
    expect("Dialog dismissals: 1", "Open dialog")
    key(44)  # Reopen from restored trigger focus.
    expect("Audit dialog")
    key(41)  # Escape
    expect("Dialog dismissals: 2")

    key(43)
    key(44)
    expect("Audit sheet", "Done")
    key(40)
    expect("Sheet dismissals: 1", "Open sheet")
    key(44)
    expect("Audit sheet")
    key(41)
    expect("Sheet dismissals: 2")
    print("PASS: Dialog and Sheet initial close focus, native Escape, and trigger focus restoration")


if __name__ == "__main__":
    main()
