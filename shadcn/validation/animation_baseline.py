"""Compare animation keyframes and render a synchronized review GIF."""

import argparse
from collections import Counter
import json
from pathlib import Path
import subprocess
from tempfile import TemporaryDirectory

from PIL import Image, ImageChops, ImageDraw

from report import differences


PLAN = json.loads(Path(__file__).with_name("animation-plan.json").read_text())


def find_trigger(video, step, marker):
    x, y = marker
    probe = subprocess.run(
        [
            "ffmpeg", "-nostdin", "-v", "error", "-i", str(video),
            "-vf", f"fps=1000/{step}:start_time=0:round=up,format=rgb24,crop=1:1:{x}:{y}",
            "-f", "rawvideo", "-pix_fmt", "rgb24", "pipe:1",
        ],
        capture_output=True,
        check=True,
    ).stdout
    armed = False
    pixels = list(zip(probe[::3], probe[1::3], probe[2::3]))
    for index, (red, green, blue) in enumerate(pixels):
        if red > 180 and max(green, blue) < 80:
            armed = True
        if armed and green > 180 and max(red, blue) < 80:
            return index * step
    colors = Counter(pixels).most_common(6)
    raise ValueError(f"No red-to-green trigger at ({x}, {y}). Observed RGB colors: {colors}")


def extract_frames(video, trigger_ms, step, names, output, crop_top=0):
    # The first sample precedes the trigger by one interval.
    with TemporaryDirectory() as scratch:
        scratch = Path(scratch)
        subprocess.run(
            [
                "ffmpeg", "-nostdin", "-v", "error",
                "-ss", f"{(trigger_ms - step) / 1000:.3f}", "-i", str(video),
                "-vf", f"fps=1000/{step}:start_time=0:round=up,crop=iw:ih-{crop_top}:0:{crop_top}",
                "-frames:v", str(len(names)), "-start_number", "0", str(scratch / "%03d.png"),
            ],
            check=True,
        )
        sources = sorted(scratch.glob("*.png"))
        if len(sources) != len(names):
            raise ValueError(f"Recording has {len(sources)} of {len(names)} required frames")
        output.mkdir(parents=True, exist_ok=True)
        for source, name in zip(sources, names):
            (output / f"{name}.png").write_bytes(source.read_bytes())


def load_frames(directory, names, crop_top=0):
    missing = [name for name in names if not (directory / f"{name}.png").is_file()]
    if missing:
        raise ValueError(f"{directory} is missing PNG frames: {', '.join(missing)}")
    frames = []
    for name in names:
        with Image.open(directory / f"{name}.png") as image:
            frame = image.convert("RGBA")
            if crop_top:
                frame = frame.crop((0, crop_top, frame.width, frame.height))
            frames.append(frame)
    return frames


def changed_pixels(expected, actual, threshold=0):
    if expected.size != actual.size:
        return max(expected.width * expected.height, actual.width * actual.height)
    channels = ImageChops.difference(expected, actual).split()
    mask = channels[0]
    for channel in channels[1:]:
        mask = ImageChops.lighter(mask, channel)
    return expected.width * expected.height - sum(mask.histogram()[: threshold + 1])


def settled_at(frames, times, threshold, budget):
    # Find the first sample after the last change, including changes near the end.
    for time, frame in reversed(list(zip(times[1:], frames[1:-1]))):
        if changed_pixels(frame, frames[-1], threshold) > budget:
            return time
    return 0


def render_gif(expected, actual, scores, times, output):
    width = max(image.width for image in expected + actual)
    height = max(image.height for image in expected + actual)
    greatest = max(range(len(scores)), key=scores.__getitem__)
    review = []
    for index, (baseline, candidate) in enumerate(zip(expected, actual)):
        canvas = Image.new("RGB", (2 * width + 8, height + 42), "#20252b")
        draw = ImageDraw.Draw(canvas)
        draw.text((8, 5), "Baseline", fill="white")
        draw.text((width + 16, 5), "Candidate", fill="white")
        time = "Before click" if index == 0 else f"{times[index - 1]} ms"
        peak = " (largest difference)" if index == greatest and scores[index] else ""
        draw.text((8, 22), f"{time} | {scores[index]} changed pixels{peak}", fill="white")
        canvas.paste(baseline, (0, 42), baseline)
        canvas.paste(candidate, (width + 8, 42), candidate)
        review.append(canvas)

    # GIF delays are in centiseconds. Round cumulative time to keep both sides in sync.
    durations = [350] + [
        10 * (round(end / 10) - round(start / 10)) for start, end in zip(times, times[1:])
    ] + [650]
    review[0].save(output, save_all=True, append_images=review[1:], duration=durations, loop=0)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("platform", choices=PLAN["platforms"])
    parser.add_argument("case", choices=PLAN["cases"])
    parser.add_argument("--baselines", type=Path, help="Root of platform baseline directories")
    parser.add_argument("--output", type=Path, required=True, help="Root of platform capture directories")
    parser.add_argument("--video", type=Path, help="Import a live recording for visual review")
    parser.add_argument("--trigger-ms", type=int, help="Timestamp of the trigger in the recording")
    parser.add_argument("--trigger-marker", type=int, nargs=2, metavar=("X", "Y"))
    parser.add_argument("--crop-top", type=int, default=0, help="Pixels to crop from the recorded status area")
    parser.add_argument("--capture-only", action="store_true", help="Import frames without comparing")
    parser.add_argument("--strict", action="store_true", help="Apply the platform comparison limits")
    parser.add_argument("--gif", action="store_true", help="Also write the review GIF on a match")
    args = parser.parse_args()
    if not args.capture_only and args.baselines is None:
        parser.error("Comparison requires --baselines")
    case = PLAN["cases"][args.case]
    recorded = PLAN["platforms"][args.platform] == "recording"
    if recorded:
        case = {**case, **case["recording"]}
    step = case["stepMs"]
    times = list(range(0, case["endMs"] + 1, step))
    names = ["before", *(f"{time:03}" for time in times)]
    keyframes = {"before", *(f"{time:03}" for time in case["keyframes"])}
    lane = Path(args.platform) / args.case
    output = args.output / lane
    actual_dir = output / "actual"
    if args.video:
        if (args.trigger_ms is None) == (args.trigger_marker is None):
            parser.error("--video requires either --trigger-ms or --trigger-marker")
        trigger_ms = args.trigger_ms if args.trigger_marker is None else find_trigger(args.video, step, args.trigger_marker)
        if trigger_ms < step or args.crop_top < 0:
            parser.error(f"Trigger must be at least {step} ms into the recording and crop must be nonnegative")
        if args.strict:
            parser.error("Live recordings are for visual review. Use test-clock PNGs with --strict.")
        extract_frames(args.video, trigger_ms, step, names, actual_dir, args.crop_top)
    elif args.trigger_ms is not None or args.trigger_marker or args.crop_top or args.capture_only:
        parser.error("Recording options require --video")
    if args.capture_only:
        print(f"Imported {len(names)} frames into {actual_dir}")
        return 0

    baseline = args.baselines / lane
    crop_top = case.get("cropTopByPlatform", {}).get(args.platform, 0)
    expected = load_frames(baseline, names, crop_top)
    actual = load_frames(actual_dir, names, crop_top)
    threshold = case.get("channelTolerance", 0)
    budget = int(expected[-1].width * expected[-1].height * case.get("changedPixelFraction", 0))
    timing_tolerance = case.get("timingToleranceMs", 0)
    scores = [changed_pixels(first, second, threshold) for first, second in zip(expected, actual)]
    expected_end = settled_at(expected, times, threshold, budget)
    actual_end = settled_at(actual, times, threshold, budget)
    failures = []

    for index, (name, score) in enumerate(zip(names, scores)):
        # A simulator recording may sample a neighboring display refresh.
        start = max(1, index - timing_tolerance // step)
        end = min(len(names) - 1, index + timing_tolerance // step + 1)
        if recorded and 1 < index < len(names) - 1 and name in keyframes and score > budget:
            score = min(changed_pixels(expected[index], frame, threshold) for frame in actual[start:end])
        if name in keyframes and score > budget:
            delta = differences(
                baseline / f"{name}.png",
                actual_dir / f"{name}.png",
                output / f"{name}-diff.png",
            )
            if delta:
                failures.append(f"{name}: {delta}")
    if abs(expected_end - actual_end) > timing_tolerance:
        failures.append(f"Settled at {actual_end} ms, baseline {expected_end} ms")
    if actual_end in (0, times[-1]) or changed_pixels(actual[0], actual[-1], threshold) <= budget:
        failures.append("Capture must show an opening animation followed by a settled frame")

    if failures or args.gif:
        render_gif(expected, actual, scores, times, output / "comparison.gif")
    if failures:
        print("\n".join(failures))
        return int(args.strict)

    print(f"Matched {len(keyframes)} animation keyframes and settled at {actual_end} ms")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (OSError, ValueError, subprocess.CalledProcessError) as error:
        raise SystemExit(str(error)) from error
