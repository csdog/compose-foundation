"""Compare platform screenshots against a locked baseline or prepare updates."""

import argparse
from array import array
import itertools
import json
import os
from pathlib import Path
import re
from shutil import copyfile
import subprocess
import sys
from urllib.parse import quote

from PIL import Image, ImageChops, ImageMath
from overview import draw_overview


DIFF_COMPARISONS = {
    "repeat": "本次截图 vs 重拍图",
    "candidate": "本次截图 vs 本地候选基准",
    "baseline": "本次截图 vs 已锁定的原生基准",
}


def differences(expected, actual, output, *, reference_viewport=()):
    with Image.open(expected) as first, Image.open(actual) as second:
        if second.size == tuple(reference_viewport) and first.width >= second.width and first.height >= second.height:
            first = first.crop((0, 0, *second.size))
        if first.size != second.size:
            if output:
                comparison = Image.new("RGB", (first.width + second.width + 4, max(first.height, second.height)), "red")
                comparison.paste(first, (0, 0))
                comparison.paste(second, (first.width + 4, 0))
                comparison.save(output)
            return f"尺寸变化 {first.size} → {second.size}"
        first_rgba, second_rgba = first.convert("RGBA"), second.convert("RGBA")
        channels = ImageChops.difference(first_rgba, second_rgba).split()
        mask = channels[0]
        for channel in channels[1:]:
            mask = ImageChops.lighter(mask, channel)
        nonzero = [0] + [255] * 255
        visible = ImageChops.lighter(first_rgba.getchannel("A"), second_rgba.getchannel("A")).point(nonzero)
        changed = ImageChops.multiply(mask.point(nonzero), visible)
        count = changed.histogram()[255]
        if count:
            if output:
                # Compose Preview uses the sum of the four channel deltas to shade magenta.
                magnitude = ImageMath.lambda_eval(
                    lambda channel: channel["r"] + channel["g"] + channel["b"] + channel["a"],
                    **dict(zip("rgba", channels)),
                )
                shades = [int(200 * (1 - delta / 1020)) for delta in range(1021)]
                green = Image.frombytes("L", second.size, bytes(map(shades.__getitem__, array("i", magnitude.tobytes()))))
                full = Image.new("L", second.size, 255)
                Image.merge("RGBA", (full, Image.composite(green, full, changed), full, changed)).save(output)
            return f"{count} 个像素不同"
        return ""


def browser_failures(output, component, theme, passes):
    for capture in passes:
        summary = output / "browser" / capture / component / "summary.json"
        if not summary.is_file():
            return f"浏览器没有产出 {capture}/summary.json"
        for failure in json.loads(summary.read_text())["failures"]:
            name = failure["name"]
            view = name.rsplit(":", 1)[-1]
            if view not in {"desktop-light", "desktop-dark", "phone-light", "phone-dark"} or view.endswith(theme):
                return f"浏览器检查失败（{capture}: {name}）"
    return ""


def screenshot_path(plan, component, theme, platform):
    case = plan["components"][component]
    status = case.get("platformStatus", {}).get(platform, case["status"])
    return Path(component, f"{component}-{case['variant']}-{status}-{theme}.png")


def baseline_path(plan, platform):
    return Path(plan["baseline"].get("paths", {}).get(platform, platform))


def copy_image(source, target):
    target.parent.mkdir(parents=True, exist_ok=True)
    copyfile(source, target)


def include_shadcn_reference(reference_root, output, screenshot):
    relative = Path("shadcn") / screenshot
    reference = reference_root / relative
    target = output / relative
    capture_provenance = target.with_suffix(".provenance.json")
    same_path = reference.resolve() == target.resolve()
    if not same_path:
        target.unlink(missing_ok=True)
        capture_provenance.unlink(missing_ok=True)
    if not reference.is_file():
        return "缺参考图"
    try:
        with Image.open(reference) as image:
            image.verify()
        if not same_path:
            copy_image(reference, target)
        provenance = reference.with_name("provenance.json")
        output_provenance = target.with_name("provenance.json")
        if provenance.is_file() and provenance.resolve() != output_provenance.resolve():
            copy_image(provenance, output_provenance)
        coverage = reference.with_name("coverage.json")
        if coverage.is_file():
            manifest = json.loads(coverage.read_text(encoding="utf-8"))
            record = next(
                (item for item in manifest["captures"] if reference.name in item["images"]), None
            )
            if record is not None:
                provenance = {key: value for key, value in manifest.items() if key != "captures"}
                capture_provenance.write_text(
                    json.dumps({**provenance, "example": record}, ensure_ascii=False, indent=2) + "\n",
                    encoding="utf-8",
                )
        return "有参考图"
    except (OSError, SyntaxError, ValueError):
        return "参考图无法读取"


def capture_results(args, plan):
    output = args.output / args.platform
    output.mkdir(parents=True, exist_ok=True)
    passes = ("actual", "repeat") if args.update_baselines else ("actual",)
    results = {}
    for component, theme in itertools.product(plan["components"], ("light", "dark")):
        captured = Path(component, "initial", f"{theme}.png")
        first = output / "actual" / captured
        second = output / "repeat" / captured
        screenshot = screenshot_path(plan, component, theme, args.platform)
        location = Path(args.platform) / screenshot
        native_path = Path(args.platform) if args.try_candidate else baseline_path(plan, args.platform)
        baseline = args.baselines / native_path / screenshot
        approved = output / "expected" / location
        candidate = output / "candidate" / location
        reference_path = Path("shadcn") / screenshot
        diff = output / "diff" / location
        diff.parent.mkdir(parents=True, exist_ok=True)
        for previous in (approved, candidate, diff):
            previous.unlink(missing_ok=True)
        diff_against = None
        baseline_source = None
        reference_status = "缺参考图"
        if args.shadcn_references:
            reference_status = include_shadcn_reference(args.shadcn_references, output, screenshot)
        elif args.baseline_revision or args.try_candidate:
            reference_status = include_shadcn_reference(args.baselines, output, screenshot)
        else:
            (output / reference_path).unlink(missing_ok=True)
        try:
            if (args.baseline_revision or args.try_candidate) and baseline.is_file():
                copy_image(baseline, approved)
                baseline_source = "candidate" if args.try_candidate else "baseline"
            if not first.is_file():
                result = ("failed", "缺少本次截图")
            elif args.update_baselines and not second.is_file():
                result = ("failed", "缺少更新基准所需的重拍图")
            elif args.platform == "cmp-web" and (error := browser_failures(output, component, theme, passes)):
                result = ("failed", error)
            elif args.update_baselines and (instability := differences(first, second, diff)):
                result = ("failed", f"重复采集不稳定（{instability}）")
                diff_against = "repeat"
            elif args.update_baselines:
                change = differences(approved, first, diff) if approved.is_file() else ""
                diff_against = baseline_source if change else None
                copy_image(first, candidate)
                detail = "截图可复现，等待审核并录入原生基准"
                result = ("review", f"{detail}（与原基准相比：{change}）" if change else detail)
            elif not approved.is_file():
                if args.try_candidate:
                    result = ("failed", f"缺少要复跑的本地候选图（{location}）")
                elif args.require_baselines:
                    result = ("failed", f"缺少原生基准（{native_path / screenshot}）")
                else:
                    result = ("pending", "缺少原生基准，已跳过像素比对")
            else:
                change = differences(approved, first, diff)
                match = "与本地候选图一致，待审核" if args.try_candidate else "与已锁定基准一致"
                result = ("failed", change) if change else ("passed", match)
                diff_against = baseline_source if change else None
        except (OSError, ValueError, KeyError) as error:
            result = ("failed", str(error))
        results[screenshot.as_posix()] = {
            "status": result[0], "detail": result[1], "reference": reference_status,
            "diff_against": diff_against, "baseline_source": baseline_source,
        }
    (output / "result.json").write_text(json.dumps(results, ensure_ascii=False, indent=2) + "\n")
    return any(result["status"] == "failed" for result in results.values())


def report_images(root, platform, component, theme, screenshot):
    captured = Path(component, "initial", f"{theme}.png")
    location = Path(platform) / screenshot
    platform_dir = root / platform
    baseline = platform_dir / "expected" / location
    files = {
        "本次截图": platform_dir / "actual" / captured,
        "重拍": platform_dir / "repeat" / captured,
        "基准图": baseline,
        "差异图": platform_dir / "diff" / location,
        "上游截图": platform_dir / "shadcn" / screenshot,
    }
    if files["上游截图"].is_file():
        reference = files["上游截图"]
        provenance = reference.with_suffix(".provenance.json")
        files["上游截图来源"] = provenance if provenance.is_file() else reference.with_name("provenance.json")
    return {label: path.relative_to(root) for label, path in files.items() if path.is_file()}


def shadcn_difference(root, artifact, screenshot, images, viewport):
    output = artifact / "diff-shadcn" / screenshot
    output.unlink(missing_ok=True)
    actual = images.get("本次截图")
    reference = images.get("上游截图")
    if not actual or not reference:
        return "—"
    output.parent.mkdir(parents=True, exist_ok=True)
    try:
        change = differences(root / reference, root / actual, output, reference_viewport=viewport)
    except (OSError, ValueError):
        return "无法比对截图"
    if output.is_file():
        images["shadcn 差异图"] = output.relative_to(root)
    return change or "像素一致"


def summarize(args, plan):
    baseline = "本地候选图试跑，待审核" if args.try_candidate else args.baseline_revision or "未取得基准提交"
    header = [
        "## 跨平台组件验收总报告", "",
        f"shadcn {plan['primitive']} / {plan['style']} `{plan['upstreamRevision']}`",
        f"截图基准 `{baseline}`",
    ]
    lines = [*header, "",
        "| 平台 | 用例 | 主题 | 结论 | shadcn 截图对照 |",
        "| --- | --- | --- | --- | --- |",
    ]
    rows = []
    overview_rows = []
    targets = set()
    labels = {"failed": "❌ 失败", "review": "🟡 待审核", "passed": "✅ 通过", "pending": "⏸️ 待验"}
    for platform, mode in plan["platforms"].items():
        artifact = args.output / platform
        result = artifact / "result.json"
        results = json.loads(result.read_text()) if result.is_file() else None
        for component, theme in itertools.product(plan["components"], ("light", "dark")):
            case = plan["components"][component]
            screenshot = screenshot_path(plan, component, theme, platform)
            key = screenshot.as_posix()
            case_status = case.get("platformStatus", {}).get(platform, case["status"])
            reference = "本次未采集"
            against = None
            baseline_source = None
            if results is None:
                status = "failed" if mode == "automated" else "pending"
                detail = "运行未产出报告" if status == "failed" else "该平台未在本次 CI 采集"
            else:
                item = results.get(key)
                if item:
                    status, detail, reference = item["status"], item["detail"], item["reference"]
                    against = item.get("diff_against")
                    baseline_source = item.get("baseline_source")
                    if args.report_only and args.shadcn_references:
                        reference = include_shadcn_reference(args.shadcn_references, artifact, screenshot)
                else:
                    status, detail = "failed", "报告中缺少本用例"
            if args.require_baselines and status == "review":
                status = "failed"
                detail = f"要求已审核基准。{detail}"
            safe_detail = detail.replace("|", "\\|").replace("\n", " ")
            columns = [platform, f"{component}/{case['variant']}/{case_status}", theme,
                       f"{labels[status]}（{safe_detail}）", reference]
            images = report_images(args.output, platform, component, theme, screenshot) if results else {}
            viewport = case.get("shadcnComparisonViewport", {}).get(platform, ())
            upstream_change = shadcn_difference(args.output, artifact, screenshot, images, viewport)
            overview_status = status if results is not None else "pending"
            overview_rows.append((platform, columns[1], theme, overview_status, against, baseline_source, images))
            paths = images.copy()
            log = artifact / "capture.log"
            if status == "failed" and not against and log.is_file():
                paths["采集日志"] = log.relative_to(args.output)
            browser_summary = artifact / "browser" / "actual" / component / "summary.json"
            if status == "failed" and browser_summary.is_file():
                paths["浏览器明细"] = browser_summary.relative_to(args.output)
            targets.update(paths.values())
            links = {label: f"[{label}]({quote(path.as_posix())})" for label, path in paths.items()}
            rows.append((columns, links, against, baseline_source, upstream_change, status == "failed"))
    details = ["", "| 组件 | 首版上游参考与视觉审核 | 平台交互待验 | 无障碍待验 | 平台动画待验 |",
               "| --- | --- | --- | --- | --- |"]
    for component, case in plan["components"].items():
        link = f"https://github.com/shadcn-ui/ui/blob/{plan['upstreamRevision']}/{case['reference']}"
        details.append(f"| {component} | [源码示例]({link})，{case['visual']} | "
                       f"{case['behavior']} | {case['accessibility']} | {case['motion']} |")
    details.extend(["", "差异图的透明区域表示像素一致，洋红越深表示差异越大。尺寸不同时，比较图将参考图与实拍图从左到右并排展示。设有共同视口的用例在该区域做像素比较。shadcn 比对结果供视觉审核。",
                    "各平台的设备交互、无障碍和动画仍需独立验收。", ""])
    table = [
        "| 平台 | 用例 | 主题 | 结论 | 本次截图 | 已发布基准 | 差异图（本次截图与基准图） | shadcn 截图对照 | 差异图（本次截图与 shadcn 截图对照） |",
        "| --- | --- | --- | --- | --- | --- | --- | --- | --- |",
    ]
    linked_rows = []
    failure_rows = []
    for columns, links, against, baseline_source, upstream_change, failed in rows:
        summary_columns = columns.copy()
        if comparison := DIFF_COMPARISONS.get(against):
            summary_columns[3] += f" · {comparison}"
        lines.append("| " + " | ".join(summary_columns) + " |")
        conclusion = columns[3]
        diagnostics = [links[label] for label in ("采集日志", "浏览器明细") if label in links]
        if against == "repeat" and "重拍" in links:
            diagnostics.append(links["重拍"])
        if baseline_source == "candidate" and "基准图" in links:
            diagnostics.append(links["基准图"].replace("[基准图]", "[本地候选基准]"))
        if against in {"repeat", "candidate"} and "差异图" in links:
            diagnostics.append(links["差异图"].replace("[差异图]", f"[{comparison}]"))
        if diagnostics:
            conclusion += " · " + " · ".join(diagnostics)
        baseline_link = links.get("基准图") if baseline_source == "baseline" else None
        if baseline_link:
            baseline_link = baseline_link.replace("[基准图]", "[已发布基准]")
        upstream = " · ".join(links[label] for label in ("上游截图", "上游截图来源") if label in links)
        upstream_diff = links.get("shadcn 差异图")
        if upstream_diff:
            upstream_change = f"{upstream_diff}（{upstream_change}）"
        row = "| " + " | ".join([
            *columns[:3], conclusion, links.get("本次截图", "缺截图"), baseline_link or "—",
            links.get("差异图", "—") if against == "baseline" else "—",
            upstream or columns[-1], upstream_change,
        ]) + " |"
        linked_rows.append(row)
        if failed:
            failure_rows.append(row)
    linked = [*header, "", f"[失败报告（{len(failure_rows)} 项）](failures.md) · "
              "[查看全平台截图与差异总览](overview.png)", "", *table, *linked_rows]
    failures = ["## 跨平台组件验收失败报告", "",
                "[返回总报告](report.md) · [查看全平台截图与差异总览](overview.png)", "",
                *([f"❌ {len(failure_rows)} 项截图验收失败。", "", *table, *failure_rows]
                  if failure_rows else ["✅ 本次没有截图验收失败项。"]), ""]
    reports = {"report.md": "\n".join(linked + details), "failures.md": "\n".join(failures)}
    args.output.mkdir(parents=True, exist_ok=True)
    overview = draw_overview(args.output, overview_rows, shadcn_review=args.update_baselines)
    targets.add(overview.relative_to(args.output))
    for name, report in reports.items():
        (args.output / name).write_text(report)
    if args.bundle:
        args.bundle.mkdir(parents=True, exist_ok=True)
        for name, report in reports.items():
            (args.bundle / name).write_text(report)
        for relative in sorted(targets):
            copy_image(args.output / relative, args.bundle / relative)
    print(args.output / "report.md")
    print(args.output / "failures.md")
    if summary := os.environ.get("GITHUB_STEP_SUMMARY"):
        if args.bundle:
            lines.extend(["", f"在本次运行的 Artifacts 下载 **{args.bundle.name}**，解压后打开 "
                          "`overview.png` 查看截图对照，或打开 `report.md` 查看原图和来源。"])
        with Path(summary).open("a") as stream:
            stream.write("\n".join(lines + details))
    return bool(failure_rows)


def run_capture(args, plan):
    destination = args.output / args.platform
    if any((destination / phase).exists() for phase in ("actual", "repeat", "browser", "device")):
        sys.exit(f"截图输出目录已使用，请指定新的运行目录。{destination}")
    destination.mkdir(parents=True, exist_ok=True)
    command = ["bash", "shadcn/validation/capture.sh", args.platform, str(destination.resolve())]
    if args.update_baselines:
        command.append("--update-baselines")
    command.extend(plan["components"])
    with (destination / "capture.log").open("w") as log:
        capture = subprocess.run(command, stdout=log, stderr=subprocess.STDOUT)
    compared = capture_results(args, plan)
    plan["platforms"] = {args.platform: plan["platforms"][args.platform]}
    reported = summarize(args, plan)
    return bool(capture.returncode or compared or reported)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("output", type=Path)
    parser.add_argument("--platform")
    parser.add_argument("--baselines", type=Path)
    parser.add_argument("--baseline-revision", help="Commit SHA of the baseline checkout used by this run.")
    parser.add_argument("--component", action="append", help="Limit the run to this component. Repeat to select more.")
    parser.add_argument("--run", action="store_true", help="Capture screenshots, compare and produce the report.")
    parser.add_argument("--update-baselines", action="store_true", help="Capture twice and prepare reproducible baseline candidates for review.")
    parser.add_argument("--try-candidate", action="store_true", help="Compare against local candidate images for a trial.")
    parser.add_argument("--require-baselines", action="store_true")
    parser.add_argument("--bundle", type=Path, help="Copy the report and images it links to into this directory.")
    parser.add_argument("--report-only", action="store_true", help="Regenerate a report from images and results already on disk.")
    parser.add_argument("--shadcn-references", type=Path, help="Use upstream reference images from this separate directory.")
    args = parser.parse_args()
    if args.update_baselines and (not args.platform or args.report_only or args.try_candidate or args.require_baselines):
        parser.error("--update-baselines requires --platform and cannot be combined with --report-only, --try-candidate or --require-baselines")
    plan = json.loads(Path(__file__).with_name("plan.json").read_text())
    if not args.baseline_revision:
        args.baseline_revision = plan["baseline"].get("revision")
    if not args.try_candidate and args.baselines and (args.baselines / ".git").exists():
        checkout_revision = subprocess.check_output(
            ["git", "-C", args.baselines, "rev-parse", "HEAD"], text=True
        ).strip()
        if args.baseline_revision and args.baseline_revision != checkout_revision:
            parser.error("baseline checkout does not match the selected revision")
        args.baseline_revision = checkout_revision
    if args.baseline_revision and not re.fullmatch(r"[0-9a-f]{40}", args.baseline_revision):
        parser.error("baseline revision must be a full commit SHA")
    if args.component:
        unknown = set(args.component) - plan["components"].keys()
        if unknown:
            parser.error(f"unknown component: {', '.join(sorted(unknown))}")
        plan["components"] = {component: plan["components"][component] for component in args.component}
    if (args.run or args.try_candidate) and not args.platform:
        parser.error("--run and --try-candidate require --platform")
    if args.run and args.report_only:
        parser.error("--run and --report-only cannot be combined")
    if args.report_only:
        if args.platform:
            if args.platform not in plan["platforms"]:
                parser.error("unknown platform")
            plan["platforms"] = {args.platform: plan["platforms"][args.platform]}
        return summarize(args, plan)
    if args.platform:
        if args.platform not in plan["platforms"] or args.baselines is None:
            parser.error("--platform requires a known platform and --baselines")
        if args.run:
            return run_capture(args, plan)
        return capture_results(args, plan)
    return summarize(args, plan)


if __name__ == "__main__":
    raise SystemExit(main())
