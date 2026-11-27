"""Copy validated screenshot candidates into a baseline repository checkout."""

import argparse
import json
from pathlib import Path

from PIL import Image

from report import copy_image, screenshot_path


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--plan", type=Path, required=True)
    parser.add_argument("--platform", required=True)
    parser.add_argument("--candidates", type=Path, required=True)
    parser.add_argument("--baselines", type=Path, required=True)
    args = parser.parse_args()

    plan = json.loads(args.plan.read_text())
    if plan["platforms"].get(args.platform) != "automated":
        parser.error(f"no automated capture for {args.platform}")
    pairs = {
        frozenset(Path(args.platform) / screenshot_path(plan, component, theme, args.platform)
                  for theme in ("light", "dark"))
        for component in plan["components"]
    }
    expected = set().union(*pairs)
    files = {path.relative_to(args.candidates) for path in args.candidates.rglob("*") if path.is_file()}
    if not files or not files <= expected or any(files & pair and not pair <= files for pair in pairs):
        parser.error("candidates must contain complete light/dark PNG pairs for the selected platform")

    root = args.baselines.resolve()
    for relative in sorted(files):
        source = args.candidates / relative
        if not source.resolve().is_relative_to(args.candidates.resolve()):
            parser.error(f"candidate resolves outside the artifact: {relative}")
        if not (root / relative).resolve().is_relative_to(root):
            parser.error(f"baseline resolves outside the checkout: {relative}")
        with Image.open(source, formats=["PNG"]) as image:
            image.verify()
    for relative in sorted(files):
        copy_image(args.candidates / relative, root / relative)
        print(relative.as_posix())


if __name__ == "__main__":
    main()
