"""Exercise multi-month SwiftUI Calendar navigation on an iOS simulator."""

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
            rows = subprocess.check_output([*client, "describe-all", "--api", "ax"], timeout=10)
            elements = {row["AXLabel"]: row for row in json.loads(rows) if row.get("AXLabel")}
            if set(present) <= elements.keys() and not elements.keys() & absent:
                return elements
            if time.monotonic() >= deadline:
                raise AssertionError(f"Expected {present} and absent {absent}, got {tuple(elements)}")
            time.sleep(0.12)

    def tap(label):
        frame = expect(label)[label]["frame"]
        x = round(frame["x"] + frame["width"] / 2)
        y = round(frame["y"] + frame["height"] / 2)
        subprocess.run([*client, "tap", str(x), str(y)], check=True, timeout=10)

    rows = expect("Month for January 2025", "Month for February 2025", "January 19, 2025", "Previous month")
    assert rows["Month for January 2025"]["frame"]["y"] < rows["Month for February 2025"]["frame"]["y"]
    assert not rows["January 19, 2025"]["enabled"]
    assert not rows["Previous month"]["enabled"]
    tap("January 30, 2025")
    expect("Range: 2025-01-30 to none")
    tap("February 3, 2025")
    rows = expect("Range: 2025-01-30 to 2025-02-03", "January 30, 2025", "February 1, 2025", "February 3, 2025")
    for label in ("January 30, 2025", "February 1, 2025", "February 3, 2025"):
        assert "Selected" in rows[label]["traits"], rows[label]

    tap("Month for January 2025")
    expect("Jan", "Feb", "Dec")
    tap("Feb")
    expect("Month for February 2025", "Month for March 2025")
    tap("Previous month")
    expect("Month for January 2025", "Month for February 2025")
    tap("Year for January 2025")
    expect("2025", "2026", absent=("2024", "2027"))
    tap("2026")
    expect("Month for January 2026", "Month for February 2026")
    tap("Month for January 2026")
    expect("Jan", "Feb", "Mar", absent=("Apr",))
    tap("Mar")
    rows = expect("Month for February 2026", "Month for March 2026", "March 11, 2026", "Next month")
    assert not rows["Next month"]["enabled"]
    assert not rows["March 11, 2026"]["enabled"]
    print("PASS: stacked months, cross-month range, month and year menus, and bounds")


if __name__ == "__main__":
    main()
