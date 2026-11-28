"""Copy validated screenshot candidates into a baseline repository checkout."""

import argparse
from collections import defaultdict
import json
from pathlib import Path

from PIL import Image

from report import baseline_path, copy_image, screenshot_cases


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
    pairs = defaultdict(set)
    for slug, case, _, screenshot, _ in screenshot_cases(plan, args.platform):
        pairs[(slug, case["variant"], case["status"])].add(Path(args.platform) / screenshot)
    expected = set().union(*pairs.values())
    files = {path.relative_to(args.candidates) for path in args.candidates.rglob("*") if path.is_file()}
    if not files or not files <= expected or any(files & pair and not pair <= files for pair in pairs.values()):
        parser.error("candidates must contain complete light/dark PNG pairs for the selected platform")

    root = args.baselines.resolve()
    platform_dir = root / baseline_path(plan, args.platform)
    for relative in sorted(files):
        source = args.candidates / relative
        if not source.resolve().is_relative_to(args.candidates.resolve()):
            parser.error(f"candidate resolves outside the artifact: {relative}")
        target = platform_dir / relative.relative_to(args.platform)
        if not target.resolve().is_relative_to(root):
            parser.error(f"baseline resolves outside the checkout: {relative}")
        with Image.open(source, formats=["PNG"]) as image:
            image.verify()
    for relative in sorted(files):
        target = platform_dir / relative.relative_to(args.platform)
        copy_image(args.candidates / relative, target)
        print(target.relative_to(root).as_posix())


if __name__ == "__main__":
    main()
