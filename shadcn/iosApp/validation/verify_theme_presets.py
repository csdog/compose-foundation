"""Exercise all eight SwiftUI style presets on an iOS simulator."""

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
    styles = ("Vega", "Nova", "Maia", "Lyra", "Mira", "Luma", "Sera", "Rhea")

    def expect(*labels, preset=None):
        deadline = time.monotonic() + 4
        expected = {label.casefold() for label in labels}
        while True:
            rows = subprocess.check_output([*client, "describe-all", "--api", "ax"], timeout=10)
            elements = {row["AXLabel"].casefold(): row for row in json.loads(rows) if row.get("AXLabel")}
            shown = elements.get("style preset", {}).get("AXValue")
            if expected <= elements.keys() and (preset is None or shown == preset):
                return elements
            if time.monotonic() >= deadline:
                raise AssertionError(f"Expected {labels}, preset {preset}; got {tuple(elements)}, preset {shown}")
            time.sleep(0.12)

    def tap(label):
        frame = expect(label)[label.casefold()]["frame"]
        x = round(frame["x"] + frame["width"] / 2)
        y = round(frame["y"] + frame["height"] / 2)
        subprocess.run([*client, "tap", str(x), str(y)], check=True, timeout=10)

    expect("Style preset", "Use dark appearance", "Saves: 0", preset="Nova")
    saves = 0
    for appearance in ("light", "dark"):
        if appearance == "dark":
            tap("Use dark appearance")
            expect("Use light appearance")
        widths = {}
        for style in styles:
            tap("Style preset")
            expect(*styles)
            tap(style)
            rows = expect("Account", "Save changes", f"Saves: {saves}", preset=style)
            frame = rows["save changes"]["frame"]
            assert frame["height"] >= 43.5, (style, frame)
            widths[style] = frame["width"]
            tap("Save changes")
            saves += 1
            expect(f"Saves: {saves}", preset=style)
        assert widths["Sera"] > widths["Nova"] + 20, widths
        assert widths["Mira"] < widths["Nova"], widths
        print(f"PASS: 8 {appearance} presets, density, control activation, and preserved state")


if __name__ == "__main__":
    main()
