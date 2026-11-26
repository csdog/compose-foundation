"""Verify Drawer dismissal and focus with Full Keyboard Access enabled on an iOS simulator."""

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
            payload = subprocess.check_output([*client, "describe-all", "--api", "ax"], timeout=10)
            rows = {row["AXLabel"]: row for row in json.loads(payload) if row.get("AXLabel")}
            if set(present) <= rows.keys() and not rows.keys() & absent:
                return rows
            if time.monotonic() >= deadline:
                raise AssertionError(f"Expected {present} and absent {absent}, got {tuple(rows)}")
            time.sleep(0.12)

    def send(*command):
        subprocess.run([*client, *map(str, command)], check=True, timeout=10)
        time.sleep(0.35)

    def center(row):
        frame = row["frame"]
        return round(frame["x"] + frame["width"] / 2), round(frame["y"] + frame["height"] / 2)

    def shown():
        return expect("Audit drawer", "Close", "Drawer content", absent=("Open drawer", "Background action"))

    def closed(count):
        focus = ("Trigger focused",) if count else ()
        return expect("Open drawer", f"Drawer dismissals: {count}", "Background activations: 0", *focus,
                      absent=("Audit drawer",))

    def reopen():
        send("key", 7, "--duration", 0.14)  # D is handled only by the focused fixture trigger.
        return shown()

    rows = closed(0)
    background = center(rows["Background action"])
    send("tap", *center(rows["Open drawer"]))
    shown()
    send("key", 40, "--duration", 0.14)  # Return
    closed(1)
    reopen()
    send("key", 41, "--duration", 0.14)  # Escape
    closed(2)
    reopen()
    send("tap", *background)  # Scrim absorbs the tap instead of activating the background.
    closed(3)

    rows = reopen()
    heading = rows["Audit drawer"]["frame"]
    container = next(row["frame"] for row in rows.values() if row["role"] == "AXApplication")
    x = round(container["width"] / 2)
    y = round(heading["y"] - 24)
    end = round(container["height"] - 10)
    send("swipe", x, y, x, end, "--duration", 0.35, "--delta", 4)
    closed(4)
    reopen()
    send("key", 41, "--duration", 0.14)
    closed(5)
    print("PASS: Drawer Return, Escape, scrim, drag, and trigger focus after every dismissal")


if __name__ == "__main__":
    main()
