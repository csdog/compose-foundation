"""Render the cross-platform report as a single image."""

from PIL import Image, ImageDraw, ImageFont


STATUS_LABELS = {
    "passed": ("PASS", "#047857"),
    "review": ("NEEDS REVIEW", "#92400e"),
    "failed": ("FAIL", "#b91c1c"),
    "capture-failed": ("CAPTURE FAILED", "#b91c1c"),
    "checks-failed": ("CHECKS FAILED", "#b91c1c"),
    "pending": ("NOT RUN", "#475569"),
}


def draw_overview(root, rows, *, shadcn_review=False):
    padding, gutter = 20, 12
    label_width, tile_width, row_height = 216, 260, 141
    capture_header = "CANDIDATE / ACTUAL" if shadcn_review else "ACTUAL"
    headers = ("BASELINE / REPEAT", capture_header, "DIFF", "SHADCN REFERENCE")
    panel_order = (1, 3, 0, 2) if shadcn_review else (0, 1, 2, 3)
    width = padding * 2 + label_width + len(headers) * (tile_width + gutter)
    canvas = Image.new("RGB", (width, 100 + row_height * len(rows)), "#f1f5f9")
    draw = ImageDraw.Draw(canvas)
    heading = ImageFont.load_default(size=24)
    text = ImageFont.load_default(size=13)
    draw.text((padding, 18), "Component screenshot overview", fill="#0f172a", font=heading)
    draw.text((padding, 50), "Magenta marks changes. The diff thumbnail zooms into small changed regions.",
              fill="#475569", font=text)
    for index, panel in enumerate(panel_order):
        x = padding + label_width + gutter + index * (tile_width + gutter)
        draw.text((x, 76), headers[panel], fill="#475569", font=text)

    for index, (platform, case, theme, status, against, baseline_source, paths) in enumerate(rows):
        y = 96 + row_height * index
        if status == "failed" and "差异图" not in paths:
            status = "checks-failed" if "本次截图" in paths else "capture-failed"
        label, color = STATUS_LABELS[status]
        draw.text((padding, y + 8), platform, fill="#0f172a", font=text)
        draw.text((padding, y + 28), f"{case} / {theme}", fill="#334155", font=text)
        draw.text((padding, y + 52), label, fill=color, font=text)
        if against == "repeat":
            baseline = paths.get("重拍")
            baseline_label = "Second capture"
        else:
            baseline = paths.get("基准图") or paths.get("待审核候选图")
            local_candidate = baseline_source == "candidate" or "基准图" not in paths
            baseline_label = "Local candidate" if local_candidate else "Baseline"
        diff_label = {
            "repeat": "Actual vs second capture",
            "candidate": "Actual vs local candidate",
            "baseline": "Actual vs baseline",
        }.get(against, "Diff")
        candidate = paths.get("待审核候选图") if shadcn_review else None
        panels = (
            (baseline, baseline_label, "No baseline"),
            (candidate or paths.get("本次截图"), "Candidate" if candidate else "Actual", "No capture"),
            (paths.get("差异图"), diff_label, "No change" if status == "passed" else "No diff"),
            (paths.get("上游截图"), "Visual review only", "No reference"),
        )
        for offset, panel in enumerate(panel_order):
            path, caption, empty = panels[panel]
            x = padding + label_width + gutter + offset * (tile_width + gutter)
            draw.rounded_rectangle((x, y, x + tile_width, y + row_height - 8),
                                   radius=8, fill="white", outline="#cbd5e1")
            if path is None:
                draw.text((x + 12, y + 50), empty, fill="#64748b", font=text)
                continue
            try:
                with Image.open(root / path) as source:
                    preview = source.convert("RGBA")
            except OSError:
                draw.text((x + 12, y + 50), "Image unreadable", fill="#991b1b", font=text)
                continue
            sampling = Image.Resampling.LANCZOS
            if panel == 2:
                sampling = Image.Resampling.NEAREST
                if bounds := preview.getchannel("A").getbbox():
                    if bounds[2] - bounds[0] < 24 and bounds[3] - bounds[1] < 24:
                        preview = preview.crop(bounds)
                        preview = preview.resize((preview.width * 4, preview.height * 4), sampling)
                        caption += " (4x detail)"
            preview.thumbnail((tile_width - 16, row_height - 39), sampling)
            canvas.paste(preview, (x + (tile_width - preview.width) // 2, y + 6), preview)
            draw.text((x + 10, y + row_height - 29), caption, fill="#475569", font=text)
    output = root / "overview.png"
    canvas.save(output)
    return output
