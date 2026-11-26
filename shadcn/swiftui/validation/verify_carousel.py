"""Exercise continuous SwiftUI Carousel paging on an iOS simulator."""

import argparse
import json
import subprocess
import time


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--companion", required=True)
    parser.add_argument("--idb", default="idb")
    parser.add_argument("--rtl", action="store_true")
    parser.add_argument("--capture-drag")
    args = parser.parse_args()
    client = [args.idb, "--companion", args.companion]

    def elements():
        rows = subprocess.check_output([*client, "ui", "describe-all", "--api", "ax"], timeout=10)
        return {row["AXLabel"]: row for row in json.loads(rows) if row.get("AXLabel")}

    def expect(*present, absent=()):
        deadline = time.monotonic() + 4
        while True:
            rows = elements()
            if set(present) <= rows.keys() and not rows.keys() & absent:
                return rows
            if time.monotonic() >= deadline:
                raise AssertionError(f"Expected {present} and absent {absent}, got {tuple(rows)}")
            time.sleep(0.12)

    def center(row, axis):
        frame = row["frame"]
        size = "width" if axis == "x" else "height"
        return round(frame[axis] + frame[size] / 2)

    def tap(label):
        row = expect(label)[label]
        subprocess.run([*client, "ui", "tap", str(center(row, "x")), str(center(row, "y"))], check=True, timeout=10)

    def swipe(x, y, end_x, end_y, duration):
        points = map(str, (x, y, end_x, end_y))
        return subprocess.Popen([*client, "ui", "swipe", *points, "--duration", str(duration), "--delta", "4"])

    def finish(process):
        assert process.wait(timeout=10) == 0
        time.sleep(0.35)

    rows = expect("Slide 1", "Bound selection: 1", "Previous", "Next", "Page 1 of 3", absent=("Slide 2", "Slide 3"))
    assert not rows["Previous"]["enabled"]
    assert "Adjustable" in rows["Page 1 of 3"]["traits"]
    arrows = center(rows["Next"], "x") - center(rows["Previous"], "x")
    assert (arrows < 0) == args.rtl
    original_x = center(rows["Slide 1"], "x")
    y = center(rows["Slide 1"], "y")
    original_next_y = center(rows["Next"], "y")
    direction = 1 if args.rtl else -1

    finish(swipe(original_x, y, original_x - direction * 125, y, 0.65))  # Beyond the first page.
    expect("Slide 1", "Bound selection: 1")
    finish(swipe(original_x, y, original_x + direction * 28, y, 0.60))  # Too short to change pages.
    expect("Slide 1", "Bound selection: 1")

    process = swipe(original_x - direction * 95, y, original_x + direction * 130, y, 2)
    moved_during_drag = False
    deadline = time.monotonic() + 5
    while process.poll() is None and time.monotonic() < deadline:
        rows = elements()
        if "Bound selection: 1" in rows and "Slide 1" in rows:
            displacement = (center(rows["Slide 1"], "x") - original_x) * direction
            if 42 < displacement < 130 and process.poll() is None:
                moved_during_drag = True
                if args.capture_drag:
                    subprocess.run([*client, "screenshot", args.capture_drag], check=True, timeout=10)
                break
        time.sleep(0.08)
    finish(process)
    assert moved_during_drag, "The current slide did not move before the binding changed"
    rows = expect("Slide 2", "Bound selection: 2", "Next", absent=("Slide 1", "Slide 3"))
    assert abs(center(rows["Next"], "y") - original_next_y - 52) <= 2, "The viewport must fit the current page"
    tap("Activate slide 2")
    expect("Activated slide: 2")
    tap("Previous")
    expect("Slide 1", "Bound selection: 1")
    tap("Next")
    expect("Slide 2", "Bound selection: 2")
    tap("Jump to last slide")
    rows = expect("Slide 3", "Bound selection: 3", "Next", absent=("Slide 1", "Slide 2"))
    assert not rows["Next"]["enabled"]

    next_y = center(rows["Next"], "y")
    x = center(rows["Slide 3"], "x")
    y = center(rows["Slide 3"], "y") + 35
    finish(swipe(x, y, x, y - 110, 0.65))
    rows = expect("Slide 3", "Next", absent=("Slide 1", "Slide 2"))
    assert center(rows["Next"], "y") < next_y - 60, "A vertical gesture must still scroll the parent"
    print(f"PASS: continuous drag, size, snap, bounds, taps, vertical scroll ({'RTL' if args.rtl else 'LTR'})")


if __name__ == "__main__":
    main()
