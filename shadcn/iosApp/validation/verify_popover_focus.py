"""Exercise Popover focus across windows on the iOS simulator."""

import argparse
import json
import subprocess
import time


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--companion", required=True)
    parser.add_argument("--idb", default="idb")
    args = parser.parse_args()
    client = [args.idb, "--companion", args.companion, "ui"]

    def expect(*present, absent=()):
        deadline = time.monotonic() + 4
        while True:
            result = subprocess.check_output([*client, "describe-all", "--api", "ax"], timeout=10)
            rows = {row["AXLabel"]: row for row in json.loads(result) if row.get("AXLabel")}
            if set(present) <= rows.keys() and not rows.keys() & absent:
                return rows
            if time.monotonic() >= deadline:
                raise AssertionError(f"Expected {present} and absent {absent}, got {tuple(rows)}")
            time.sleep(0.12)

    def send(*command):
        subprocess.run([*client, *map(str, command)], check=True, timeout=10)
        time.sleep(0.30)

    def tap(label):
        frame = expect(label)[label]["frame"]
        x = round(frame["x"] + frame["width"] / 2)
        y = round(frame["y"] + frame["height"] / 2)
        send("tap", x, y)

    def key(code):
        send("key", code, "--duration", 0.14)

    def opened():
        expect("Popover content", "Close popover", "Panel focused", absent=("Open popover",))

    def closed(count):
        expect("Open popover", f"Popover dismissals: {count}", "Trigger focused", absent=("Popover content",))

    def reopen():
        key(19)  # P is handled only by the focused fixture trigger.
        opened()

    expect("Open popover", "Popover dismissals: 0")
    tap("Open popover")
    opened()
    key(40)  # Return reaches the focused control in the overlay window.
    closed(1)
    reopen()
    key(41)  # Escape
    closed(2)
    reopen()
    send("tap", 12, 650)  # Outside the anchored panel.
    closed(3)
    reopen()
    tap("Close popover")
    closed(4)
    print("PASS: panel focus, Return, Escape, outside touch, button touch, and trigger focus")


if __name__ == "__main__":
    main()
