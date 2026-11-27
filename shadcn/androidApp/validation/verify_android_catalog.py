"""Capture the Android catalog route for each generated registry item."""

import argparse
import json
from pathlib import Path
import re
import subprocess
import time
import xml.etree.ElementTree as ET


PACKAGE = "com.toutiaoyan.compose.ui"
REPO = Path(__file__).resolve().parents[3]
DOCS = REPO / "app/shared/src/commonMain/kotlin/com/toutiaoyan/compose/ui/catalog/docs"
DOC_TITLE = re.compile(
    r'ComponentDoc\(\s*id\s*=\s*"([^"]+)"\s*,\s*(?:isKotlinExtension\s*=\s*true\s*,\s*)?title\s*=\s*"([^"]+)"'
)
EXAMPLE_ROUTES = {
    "emoji-text": ("bubble", ("Bubble", "Reaction: thumbs up")),
    "icon": ("input-group", ("Input Group", "12 results")),
    "theme": ("theming", ("Theming",)),
}


class AndroidCatalog:
    def __init__(self, adb, device):
        self.command = [adb, "-s", device]

    def run(self, *args, timeout=25):
        return subprocess.check_output([*self.command, *args], timeout=timeout)

    def shell(self, *args):
        return self.run("shell", *args).decode("utf-8").strip()

    def open(self, route):
        result = self.shell("am", "start", "-W", "-a", "android.intent.action.VIEW", "-d",
                            f"shadcncompose://app/{route}", "-n", f"{PACKAGE}/.MainActivity")
        if "Status: ok" not in result:
            raise RuntimeError(result)

    def hierarchy(self):
        payload = self.run("exec-out", "uiautomator", "dump", "/dev/tty")
        start = payload.find(b"<?xml")
        end = payload.find(b"</hierarchy>")
        if start == -1 or end == -1:
            raise RuntimeError(payload.decode("utf-8", errors="replace").strip())
        return payload[start:end + len(b"</hierarchy>")]

    def expect(self, expected, *, absent=()):
        last = ()
        problem = None
        for _ in range(3):
            try:
                payload = self.hierarchy()
            except (RuntimeError, subprocess.TimeoutExpired) as error:
                problem = error
                time.sleep(0.2)
                continue
            nodes = [node for node in ET.fromstring(payload).iter("node") if node.get("package") == PACKAGE]
            last = tuple(
                value for node in nodes for attr in ("text", "content-desc")
                if (value := node.get(attr, "").strip())
            )
            if set(expected) <= set(last) and not set(absent).intersection(last):
                return payload
            time.sleep(0.15)
        raise AssertionError(f"Expected {expected} and absent {absent}; saw {last[:12]}; dump error: {problem}")

    def point(self, label, payload=None):
        payload = payload if payload is not None else self.expect((label,))
        for node in ET.fromstring(payload).iter("node"):
            if node.get("package") == PACKAGE and label in (node.get("text"), node.get("content-desc")):
                left, top, right, bottom = map(int, re.findall(r"\d+", node.attrib["bounds"]))
                if left < right and top < bottom:
                    return (left + right) // 2, (top + bottom) // 2
        raise AssertionError(f"{label!r} disappeared")

    def tap(self, label, payload=None):
        point = self.point(label, payload)
        self.shell("input", "tap", *map(str, point))
        return point

    def screenshot(self, output):
        data = self.run("exec-out", "screencap", "-p")
        if not data.startswith(b"\x89PNG\r\n\x1a\n"):
            raise RuntimeError("screencap produced no PNG")
        output.write_bytes(data)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--registry", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--device", required=True)
    parser.add_argument("--adb", default="adb")
    parser.add_argument("--only", nargs="+")
    parser.add_argument("--retry-failed", action="store_true", help="Relaunch once if a catalog capture fails.")
    args = parser.parse_args()
    items = [item["name"] for item in json.loads(args.registry.read_text())["items"]]
    if len(items) != len(set(items)) or any(not re.fullmatch(r"[a-z0-9]+(?:-[a-z0-9]+)*", item) for item in items):
        parser.error("registry item names must be unique slugs")
    if args.only and set(args.only) - set(items):
        parser.error("--only contains a slug absent from the generated registry")
    titles = {}
    for path in DOCS.glob("*.kt"):
        titles.update(DOC_TITLE.findall(path.read_text()))
    catalog = AndroidCatalog(args.adb, args.device)
    initial_mode = catalog.shell("cmd", "uimode", "night").removeprefix("Night mode: ")
    if initial_mode not in {"yes", "no", "auto", "custom_schedule", "custom_bedtime"}:
        raise RuntimeError(f"Unexpected night mode: {initial_mode}")

    failed = []
    try:
        for appearance, mode in (("light", "no"), ("dark", "yes")):
            catalog.shell("cmd", "uimode", "night", mode)
            if catalog.shell("cmd", "uimode", "night") != f"Night mode: {mode}":
                raise RuntimeError(f"Could not select {appearance} mode")
            directory = args.output / appearance
            directory.mkdir(parents=True, exist_ok=True)
            for index, item in enumerate(args.only or items):
                if index % 12 == 0:
                    catalog.shell("am", "force-stop", PACKAGE)
                route, expected = EXAMPLE_ROUTES.get(item, (item, (titles.get(item, ""),)))
                result = {"item": item, "route": route, "appearance": appearance, "expected": expected}
                for suffix in (".png", ".xml"):
                    (directory / f"{item}{suffix}").unlink(missing_ok=True)
                try:
                    if not all(expected):
                        raise ValueError(f"Missing catalog title for {item}")
                    capture_case(catalog, route, expected, directory / item, args.retry_failed)
                    result["status"] = "route verified"
                except (subprocess.SubprocessError, RuntimeError, ValueError, AssertionError, ET.ParseError) as error:
                    result["status"] = "failed"
                    result["error"] = str(error)
                    failed.append(f"{appearance}/{item}")
                    print(f"{appearance}/{item}: {error}", flush=True)
                (directory / f"{item}.json").write_text(json.dumps(result, ensure_ascii=False) + "\n")
                print(f"{appearance}/{item}: {result['status']}", flush=True)
    finally:
        catalog.shell("cmd", "uimode", "night", initial_mode)
    if failed:
        raise SystemExit("Failed: " + ", ".join(failed))


def capture_case(catalog, route, expected, output, retry):
    try:
        catalog.open(route)
        hierarchy = catalog.expect(expected)
        output.with_suffix(".xml").write_bytes(hierarchy)
        catalog.screenshot(output.with_suffix(".png"))
    except (subprocess.SubprocessError, RuntimeError, AssertionError, ET.ParseError) as error:
        if not retry:
            raise
        print(f"{route}: capture failed, relaunching once: {error}", flush=True)
        catalog.shell("am", "force-stop", PACKAGE)
        time.sleep(1)
        capture_case(catalog, route, expected, output, retry=False)


if __name__ == "__main__":
    main()
