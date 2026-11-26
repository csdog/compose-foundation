"""Exercise the SwiftUI Toast queue on an iOS simulator."""

import argparse
import json
import subprocess
import time


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--companion", required=True)
    parser.add_argument("--idb", default="idb")
    args = parser.parse_args()
    client = [args.idb, "--companion", args.companion]

    def read_elements():
        result = subprocess.check_output([*client, "ui", "describe-all", "--api", "ax"], timeout=10)
        return [row for row in json.loads(result) if row.get("AXLabel")]

    def expect(*present, absent=()):
        deadline = time.monotonic() + 4
        while True:
            rows = read_elements()
            labels = {row["AXLabel"] for row in rows}
            if set(present) <= labels and not labels.intersection(absent):
                return rows
            if time.monotonic() >= deadline:
                raise AssertionError(f"Expected {present}, absent {absent}, got {sorted(labels)}")
            time.sleep(0.12)

    def tap_row(row):
        frame = row["frame"]
        x = round(frame["x"] + frame["width"] / 2)
        y = round(frame["y"] + frame["height"] / 2)
        subprocess.run([*client, "ui", "tap", str(x), str(y)], check=True, timeout=10)

    def tap(label):
        tap_row(next(row for row in expect(label) if row["AXLabel"] == label))

    tap("Add four notifications")
    expect("Second notice", "Third notice", "Saved", "Undo", absent=("First notice",))
    tap("Undo")
    expect("Undo count: 1", "First notice", "Second notice", "Third notice", absent=("Saved", "Undo"))
    tap("Dismiss First notice")
    expect("Second notice", "Third notice", absent=("First notice",))

    tap("Add timed notification")
    expect("Second notice", "Third notice", "Timed notice")
    expect("Second notice", "Third notice", absent=("Timed notice",))

    tap("Clear notifications")
    expect("Undo count: 1", absent=("First notice", "Second notice", "Third notice", "Timed notice"))
    print("PASS: visible limit, promotion, individual dismissal, Undo, timeout, and clear")


if __name__ == "__main__":
    main()
