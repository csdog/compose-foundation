"""Select screenshot components and runners for Component parity."""

import json
import os
from pathlib import Path

RUNNERS = {
    "cmp-web": "ubuntu-24.04",
    "cmp-android": "ubuntu-24.04",
    "swiftui-ios": "macos-15",
}


def capture_matrix(plan, component="", labels=None, platform=""):
    components = plan["components"]
    platforms = {name: RUNNERS[name] for name, mode in plan["platforms"].items() if mode == "automated"}
    supported = {
        slug: set(platforms).intersection(config.get("platforms", platforms))
        for slug, config in components.items()
    }
    selected = {name: set() for name in platforms}
    if labels is None:
        if component and component not in components:
            raise ValueError(f"Unknown component {component!r}. Enter the slug, for example popover.")
        if platform not in ("", "all") and platform not in platforms:
            raise ValueError(f"No automated capture for platform {platform!r}")
        for slug in [component] if component else components:
            for name in supported[slug]:
                if platform in ("", "all", name):
                    selected[name].add(slug)
    else:
        for label in labels:
            if not label.startswith("publish:"):
                continue
            target = label.removeprefix("publish:")
            swiftui_only = target.startswith("swiftui:")
            slug = target.removeprefix("swiftui:")
            if slug not in supported:
                raise ValueError(f"Unknown baseline publication label: {label!r}")
            destinations = supported[slug] & {"swiftui-ios"} if swiftui_only else supported[slug]
            if not destinations:
                raise ValueError(f"No automated capture for publication label {label!r}")
            for name in destinations:
                selected[name].add(slug)

    include = [
        {"platform": name, "runner": runner, "components": [slug for slug in components if slug in selected[name]]}
        for name, runner in platforms.items()
        if selected[name]
    ]
    if labels is None and not include:
        raise ValueError(f"No automated cases for {component or 'the selected components'} on {platform or 'any platform'}")
    return {"include": include}


if __name__ == "__main__":
    plan = json.loads(Path(__file__).with_name("plan.json").read_text())
    labels = json.loads(os.environ["PUBLISH_LABELS"]) if os.environ["GITHUB_EVENT_NAME"] == "push" else None
    matrix = capture_matrix(plan, os.environ.get("COMPONENT", ""), labels, os.environ.get("CAPTURE_PLATFORM", ""))
    print(f"matrix={json.dumps(matrix, separators=(',', ':'))}")
    print(f"has_scope={str(bool(matrix['include'])).lower()}")
