"""Compare two captures per platform and summarize the cross-platform pilot."""

import argparse
import itertools
import json
import os
from pathlib import Path

from PIL import Image, ImageChops


def differences(expected, actual, output):
    with Image.open(expected) as first, Image.open(actual) as second:
        if first.size != second.size:
            return f"尺寸变化 {first.size} → {second.size}"
        channels = ImageChops.difference(first.convert("RGBA"), second.convert("RGBA")).split()
        mask = channels[0]
        for channel in channels[1:]:
            mask = ImageChops.lighter(mask, channel)
        count = mask.width * mask.height - mask.histogram()[0]
        if count:
            if output:
                mask.point([0] + [255] * 255).save(output)
            return f"{count} 个像素不同"
        return ""


def browser_failures(output, component, theme):
    for capture in ("actual", "repeat"):
        summary = output / "browser" / capture / component / "summary.json"
        if not summary.is_file():
            return f"浏览器没有产出 {capture}/summary.json"
        for failure in json.loads(summary.read_text())["failures"]:
            name = failure["name"]
            view = name.rsplit(":", 1)[-1]
            if view not in {"desktop-light", "desktop-dark", "phone-light", "phone-dark"} or view.endswith(theme):
                return f"浏览器检查失败（{capture}: {name}）"
    return ""


def capture_results(args, plan):
    output = args.output / args.platform
    output.mkdir(parents=True, exist_ok=True)
    results = {}
    for component, theme in itertools.product(plan["components"], ("light", "dark")):
        key = f"{component}/initial/{theme}"
        first = output / "actual" / f"{key}.png"
        second = output / "repeat" / f"{key}.png"
        baseline = args.baselines / args.platform / f"{key}.png"
        diff = output / "diff" / f"{key}.png"
        diff.parent.mkdir(parents=True, exist_ok=True)
        try:
            if not first.is_file() or not second.is_file():
                result = ("failed", "两次采集的截图不完整")
            elif args.platform == "cmp-web" and (error := browser_failures(output, component, theme)):
                result = ("failed", error)
            elif instability := differences(first, second, diff):
                result = ("failed", f"重复采集不稳定（{instability}）")
            elif not plan["baseline"]["revision"] or not baseline.is_file():
                result = ("review", "截图可复现，等待按上游参考审核并录入原生基准")
            else:
                approved = output / "expected" / f"{key}.png"
                approved.parent.mkdir(parents=True, exist_ok=True)
                approved.write_bytes(baseline.read_bytes())
                change = differences(approved, first, diff)
                result = ("failed", change) if change else ("passed", "与已锁定基准一致")
        except (OSError, ValueError, KeyError) as error:
            result = ("failed", str(error))
        results[key] = result
    (output / "result.json").write_text(json.dumps(results, ensure_ascii=False, indent=2) + "\n")
    return any(status == "failed" for status, _ in results.values())


def summarize(args, plan):
    lines = [
        "## 跨平台组件验收", "",
        f"shadcn Base UI / Nova `{plan['upstreamRevision']}`",
        f"截图基准 `{plan['baseline']['revision'] or '尚未锁定'}`", "",
        "| 平台 | 用例 | 主题 | 视觉 |",
        "| --- | --- | --- | --- |",
    ]
    failed = False
    labels = {"failed": "失败", "review": "待审核", "passed": "通过"}
    for platform, mode in plan["platforms"].items():
        artifact = args.output / platform
        result = artifact / "result.json"
        results = json.loads(result.read_text()) if result.is_file() else None
        for component, theme in itertools.product(plan["components"], ("light", "dark")):
            key = f"{component}/initial/{theme}"
            if results is None:
                status = "failed" if mode == "automated" else "pending"
                detail = "运行未产出报告" if status == "failed" else "该平台未在本次 CI 采集"
            else:
                status, detail = results.get(key, ("failed", "报告中缺少本用例"))
            failed |= status == "failed" or (args.require_baselines and status == "review")
            safe_detail = detail.replace("|", "\\|").replace("\n", " ")
            lines.append(f"| {platform} | {component}/initial | {theme} | "
                         f"{labels.get(status, '待验')}（{safe_detail}） |")
    lines.extend(["", "| 组件 | 首版上游参考与视觉审核 | 平台交互待验 | 无障碍待验 | 平台动画待验 |",
                  "| --- | --- | --- | --- | --- |"])
    for component, case in plan["components"].items():
        link = f"https://github.com/shadcn-ui/ui/blob/{plan['upstreamRevision']}/{case['reference']}"
        lines.append(f"| {component} | [源码示例]({link})，{case['visual']} | "
                     f"{case['behavior']} | {case['accessibility']} | {case['motion']} |")
    lines.extend(["", "打开本次运行的 Artifacts，查看各平台的实际图、重复采集图、"
                  "已锁定基准和差异图。下方的逻辑及 JVM 结果提供额外证据，"
                  "各平台的设备交互、无障碍和动画仍需独立验收。", ""])
    report = "\n".join(lines)
    args.output.mkdir(parents=True, exist_ok=True)
    (args.output / "report.md").write_text(report)
    if summary := os.environ.get("GITHUB_STEP_SUMMARY"):
        with Path(summary).open("a") as stream:
            stream.write(report)
    return failed


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("output", type=Path)
    parser.add_argument("--platform")
    parser.add_argument("--baselines", type=Path)
    parser.add_argument("--require-baselines", action="store_true")
    args = parser.parse_args()
    plan = json.loads(Path(__file__).with_name("plan.json").read_text())
    if args.platform:
        if args.platform not in plan["platforms"] or args.baselines is None:
            parser.error("--platform requires a known platform and --baselines")
        return capture_results(args, plan)
    return summarize(args, plan)


if __name__ == "__main__":
    raise SystemExit(main())
