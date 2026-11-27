"""Refresh native screenshot indexes and list the Markdown paths that changed."""

import argparse
import json
import os
from pathlib import Path
import re
from urllib.parse import quote

from report import baseline_path


THEMES = {"light": "浅色", "dark": "深色"}
STATES = {"initial": "初始态", "open": "展开态", "closed": "关闭态"}
PLATFORM_TITLES = {
    "cmp-web": "Compose Web",
    "cmp-android": "Compose Multiplatform Android",
    "swiftui-ios": "SwiftUI iOS",
    "cmp-desktop": "Compose Desktop",
}
TABLE = "| 组件 | 本端截图 | shadcn 视觉基准 |\n| --- | --- | --- |"
INTRO = "shadcn 是默认视觉基准。本端截图发布后与同状态的 shadcn 示例并列。"
FIRST_CELL = re.compile(r"^\| (?:\[([^\]]+)\]\([^)]*\)|([^|]+?)) \|")


def link(page, target, label, embed=False):
    if not target.is_file():
        raise FileNotFoundError(target)
    path = quote(Path(os.path.relpath(target, page.parent)).as_posix(), safe="/")
    return f"{'!' if embed else ''}[{label}]({path})"


def write_page(root, path, content):
    relative = path.relative_to(root)
    if not path.resolve().is_relative_to(root):
        raise ValueError(f"Index resolves outside the baseline checkout: {relative}")
    if path.is_file() and path.read_text(encoding="utf-8") == content:
        return
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content, encoding="utf-8")
    print(relative.as_posix())


def screenshots(directory, component):
    pattern = re.compile(rf"{re.escape(component)}-([a-z0-9-]+)-({'|'.join(THEMES)})\.png")
    cases = {}
    for path in sorted(directory.glob("*.png")):
        if match := pattern.fullmatch(path.name):
            cases.setdefault(match[1], {})[match[2]] = path
    return cases


def reference_status(reference_page, reference_cases):
    if reference_page.is_file() or reference_cases:
        return "无同变体和状态的参考图"
    return "无直接对应示例"


def case_label(case, planned_variant, label_variant=False):
    if case.startswith(planned_variant + "-"):
        state = case[len(planned_variant) + 1:]
        if not label_variant:
            return STATES.get(state, state)
    return case


def update_component(root, index, platform_title, component, title, cases, variant):
    page = index.parent / component / "Index.md"
    reference_page = root / "shadcn" / component / "Index.md"
    reference_cases = screenshots(reference_page.parent, component)
    missing = reference_status(reference_page, reference_cases)
    detail_rows, local_links, upstream_links = [], [], []
    multiple_cases = len(cases) > 1
    for case, images in cases.items():
        upstream = {
            theme: path for theme, path in reference_cases.get(case, {}).items() if theme in images
        }
        state = case_label(case, variant, multiple_cases)
        prefix = f"{state} " if multiple_cases else ""
        local_links.append(prefix + " · ".join(
            link(index, images[theme], label) for theme, label in THEMES.items() if theme in images
        ))
        if upstream:
            upstream_links.append(prefix + " · ".join(
                link(index, upstream[theme], label) for theme, label in THEMES.items()
                if theme in upstream
            ))
        else:
            upstream_links.append(prefix + missing)
        for theme, label in THEMES.items():
            if theme in images:
                local_image = link(page, images[theme], f"{title} {label}", embed=True)
                upstream_image = (
                    link(page, upstream[theme], f"shadcn {label}", embed=True)
                    if theme in upstream else missing
                )
                detail_rows.append(f"| {prefix}{label} | {local_image} | {upstream_image} |")

    first_case = next(iter(cases))
    intro = "按截图变体、状态与主题对照。" if multiple_cases else f"对照状态为{case_label(first_case, variant)}。"
    if reference_page.is_file():
        intro += " " + link(page, reference_page, "shadcn 示例和采集记录")
    content = "\n".join([
        f"# {title}", "", intro, "",
        f"| {'变体、状态与主题' if multiple_cases else '主题'} | {platform_title} | shadcn 视觉基准 |",
        "| --- | --- | --- |", *detail_rows, "",
    ])
    write_page(root, page, content)
    title_link = f"[{title}](./{component}/Index.md)"
    return f"| {title_link} | {'<br>'.join(local_links)} | {'<br>'.join(upstream_links)} |"


def update_platform(root, plan, platform):
    directory = root / baseline_path(plan, platform)
    if not directory.resolve().is_relative_to(root):
        raise ValueError(f"Platform directory resolves outside the baseline checkout: {directory}")
    index = directory / "Index.md"
    platform_title = PLATFORM_TITLES.get(platform, platform)
    shadcn_index = root / "shadcn/Index.md"
    if index.is_file():
        before, separator, after = index.read_text(encoding="utf-8").partition(TABLE)
        if not separator:
            raise ValueError(f"Missing screenshot table in {index.relative_to(root)}")
    else:
        intro = INTRO
        if shadcn_index.is_file():
            intro += " " + link(index, shadcn_index, "上游组件索引")
        before, after = f"# {platform_title}\n\n{intro}\n\n", "\n"

    rows = []
    remaining = after.removeprefix("\n").split("\n")
    while remaining and remaining[0].startswith("| "):
        rows.append(remaining.pop(0))

    for component, item in plan["components"].items():
        cases = screenshots(directory / component, component)
        if not cases:
            continue
        title = item["title"]
        key = title.casefold()
        row = update_component(root, index, platform_title, component, title, cases, item["variant"])
        for position, existing in enumerate(rows):
            match = FIRST_CELL.match(existing)
            if not match:
                continue
            existing_title = (match[1] or match[2]).casefold()
            if existing_title == key or f"](./{component}/Index.md)" in existing:
                rows[position] = row
                break
        else:
            rows.append(row)

    start = before + TABLE + "\n"
    write_page(root, index, start + "\n".join([*rows, *remaining]))


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--plan", type=Path, required=True)
    parser.add_argument("--platform", required=True)
    parser.add_argument("--baselines", type=Path, required=True)
    args = parser.parse_args()
    update_platform(args.baselines.resolve(), json.loads(args.plan.read_text()), args.platform)


if __name__ == "__main__":
    main()
