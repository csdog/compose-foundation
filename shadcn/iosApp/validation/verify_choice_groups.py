"""Exercise grouped SwiftUI pickers on an iOS simulator."""

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

    def expect(*present, absent=()):
        deadline = time.monotonic() + 4
        while True:
            rows = subprocess.check_output([*client, "ui", "describe-all", "--api", "ax"], timeout=10)
            elements = {row["AXLabel"]: row for row in json.loads(rows) if row.get("AXLabel")}
            if set(present) <= elements.keys() and not elements.keys() & absent:
                return elements
            if time.monotonic() >= deadline:
                raise AssertionError(f"Expected {present}, absent {absent}, got {tuple(elements)}")
            time.sleep(0.12)

    def send(*command):
        subprocess.run([*client, "ui", *command], check=True, timeout=10)
        time.sleep(0.18)

    def tap(label):
        frame = expect(label)[label]["frame"]
        x = round(frame["x"] + frame["width"] / 2)
        y = round(frame["y"] + frame["height"] / 2)
        send("tap", str(x), str(y))

    tap("Grouped Combobox")
    rows = expect("Fruit", "Vegetables", "Apple", "Apricot", "Carrot", absent=("Empty group",))
    assert "Header" in rows["Fruit"]["traits"], rows["Fruit"]
    assert not rows["Apricot"]["enabled"], rows["Apricot"]
    send("key", "81")  # Apple
    send("key", "81")  # Skip disabled Apricot and the group headings to reach Carrot.
    send("key", "40")  # Return
    expect("Combobox: Carrot")

    tap("Grouped Combobox")
    expect("Fruit", "Vegetables")
    send("text", "Cel")
    expect("Vegetables", "Celery", absent=("Fruit", "Apple", "Apricot", "Carrot"))
    send("key", "40")
    expect("Combobox: Celery")
    tap("Grouped Combobox")
    expect("Fruit", "Vegetables")
    send("text", "ZZZZ")
    expect("No results", absent=("Fruit", "Vegetables"))
    send("key", "41")  # Escape
    expect("Combobox: Celery")

    tap("Grouped multi-select")
    expect("Fruit", "Vegetables", "Done")
    tap("Apple")
    tap("Carrot")
    tap("Done")
    expect("Remove Apple", "Remove Carrot")
    tap("Remove Apple")
    expect("Remove Carrot", absent=("Remove Apple",))

    tap("Grouped Select")
    rows = expect("Fruit", "Vegetables", "Apple", "Apricot", "Carrot", absent=("Empty group",))
    assert not rows["Apricot"]["enabled"], rows["Apricot"]
    tap("Carrot")
    rows = expect("Grouped Select", "Select: Carrot")
    assert rows["Grouped Select"]["AXValue"] == "Carrot", rows["Grouped Select"]
    print("PASS: named groups, filtering, cross-group keys, multi-select, and native Select")


if __name__ == "__main__":
    main()
