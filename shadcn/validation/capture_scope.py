"""Select screenshot components and runners for Component parity."""

import json
import os
from pathlib import Path

RUNNERS = {
    "cmp-web": "ubuntu-24.04",
    "cmp-android": "ubuntu-24.04",
    "swiftui-ios": "macos-15",
}


def capture_matrix(plan, component="", labels=None):
    components = plan["components"]
    platforms = {name: RUNNERS[name] for name, mode in plan["platforms"].items() if mode == "automated"}
    if labels is None:
        if component and component not in components:
            raise ValueError(f"Unknown component {component!r}. Enter the slug, for example popover.")
        selected = {platform: {component} if component else set(components) for platform in platforms}
    else:
        selected = {platform: set() for platform in platforms}
        for label in labels:
            if not label.startswith("publish:"):
                continue
            target = label.removeprefix("publish:")
            if target.startswith("swiftui:"):
                slug = target.removeprefix("swiftui:")
                destinations = ("swiftui-ios",)
            else:
                slug = target
                destinations = platforms
            if slug not in components:
                raise ValueError(f"Unknown baseline publication label: {label!r}")
            for platform in destinations:
                selected[platform].add(slug)

    return {"include": [
        {"platform": platform, "runner": runner, "components": [slug for slug in components if slug in selected[platform]]}
        for platform, runner in platforms.items()
        if selected[platform]
    ]}


if __name__ == "__main__":
    plan = json.loads(Path(__file__).with_name("plan.json").read_text())
    labels = None
    if os.environ["GITHUB_EVENT_NAME"] == "pull_request_target":
        event = json.loads(Path(os.environ["GITHUB_EVENT_PATH"]).read_text())
        labels = [item["name"] for item in event["pull_request"]["labels"]]
    matrix = capture_matrix(plan, os.environ.get("COMPONENT", ""), labels)
    print(f"matrix={json.dumps(matrix, separators=(',', ':'))}")
