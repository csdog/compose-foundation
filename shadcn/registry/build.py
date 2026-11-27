#!/usr/bin/env python3
"""Build the static shadcn registries from the checked-out component sources."""

import argparse
from collections import defaultdict
import json
from pathlib import Path
import re


ROOT = Path(__file__).resolve().parents[2]
HOMEPAGE = "https://compose-foundation.devops.cn.toutiaoyan.com"
PACKAGE_PREFIX = "com.toutiaoyan.compose."
COMPONENT_PREFIX = PACKAGE_PREFIX + "ui.components."
SWIFT_PACKAGE = Path("shadcn/iosApp")
SWIFT_SOURCES = SWIFT_PACKAGE / "Sources/ComposeFoundationSwiftUI"
SWIFT_FILENAMES = {
    "button": "FoundationButtonStyle.swift",
    "switch": "FoundationSwitchStyle.swift",
}
SWIFT_SUPPORT_FILES = {
    "alert-dialog": ("FoundationButtonStyle.swift",),
    "button-group": ("FoundationButtonStyle.swift",),
    "calendar": ("FoundationCalendarSelection.swift", "FoundationCalendarNavigation.swift"),
    "dialog": ("FoundationPresentationHeader.swift",),
    "drawer": ("FoundationPresentationHeader.swift", "FoundationButtonStyle.swift"),
    "field": ("FoundationLabel.swift",),
    "hover-card": ("FoundationPopover.swift",),
    "menubar": ("FoundationMenuGroups.swift", "FoundationPopover.swift"),
    "navigation-menu": ("FoundationMenuGroups.swift", "FoundationPopover.swift"),
    "pagination": ("FoundationButtonStyle.swift",),
    "sheet": ("FoundationPresentationHeader.swift",),
    "tabs": ("FoundationTabsNavigation.swift",),
    "toast": ("FoundationButtonStyle.swift",),
    "toggle-group": ("FoundationToggle.swift",),
}
PACKAGE = re.compile(r"^package ([\w.]+)", re.MULTILINE)
IMPORT = re.compile(r"^import (com\.toutiaoyan\.compose\.[\w.]+)", re.MULTILINE)
QUALIFIED_NAME = re.compile(r"\bcom\.toutiaoyan\.compose\.[\w.]+")
COMMENTS = re.compile(r"/\*.*?\*/|//[^\n]*", re.DOTALL)
EXPECT_DECLARATION = re.compile(r"^(?:\w+[ \t]+)*expect[ \t]+", re.MULTILINE)
TOP_LEVEL_DECLARATION = re.compile(
    r"^(?:\w+[ \t]+)*(?:fun|class|interface|object|typealias|val|var)[ \t]+",
    re.MULTILINE,
)
ACTUAL_MODIFIER = re.compile(r"\bactual[ \t]+(?=(?:fun|class|val|var|constructor|typealias)\b)")
COMPONENT_NAMES = {
    "alertdialog": "alert-dialog",
    "aspectratio": "aspect-ratio",
    "buttongroup": "button-group",
    "codeblock": "code-block",
    "contextmenu": "context-menu",
    "dropdownmenu": "dropdown-menu",
    "emojitext": "emoji-text",
    "hovercard": "hover-card",
    "inputgroup": "input-group",
    "inputotp": "input-otp",
    "messagescroller": "message-scroller",
    "navigationmenu": "navigation-menu",
    "radiobutton": "radio-group",
    "scrollarea": "scroll-area",
    "togglegroup": "toggle-group",
}


def load_sources():
    packages = defaultdict(dict)
    for module in ("shadcn/shared", "tailwind/core"):
        for path in sorted((ROOT / module / "src").glob("*Main/kotlin/**/*.kt")):
            content = path.read_bytes().decode("utf-8")
            package = PACKAGE.search(content)
            if not package or not package[1].startswith(PACKAGE_PREFIX):
                raise ValueError(f"Unexpected package in {path.relative_to(ROOT)}")
            packages[package[1]][path.relative_to(ROOT)] = content
    return packages


def package_dependencies(packages):
    dependencies = {}
    for package, files in packages.items():
        references = set()
        for content in files.values():
            names = set(IMPORT.findall(content))
            names.update(QUALIFIED_NAME.findall(COMMENTS.sub("", content)))
            for name in names:
                while name.startswith(PACKAGE_PREFIX):
                    if name in packages:
                        references.add(name)
                        break
                    name = name.rpartition(".")[0]
        dependencies[package] = references - {package}
    return dependencies


def collect_files(package, packages, dependencies):
    # Internal declarations and expect/actual implementations stay in the same
    # consumer module. Follow package dependencies, including cycles, once.
    remaining = [package]
    visited = set()
    files = {}
    while remaining:
        current = remaining.pop()
        if current in visited:
            continue
        visited.add(current)
        files.update(packages[current])
        remaining.extend(dependencies[current] - visited)
    return files


def registry_file(path, target, content):
    return {
        "path": path.as_posix(),
        "type": "registry:file",
        "target": target,
        "content": content,
    }


def source_file(path, target):
    return registry_file(path, target, (ROOT / path).read_bytes().decode("utf-8"))


def license_file():
    return source_file(Path("LICENSE"), "~/licenses/compose-foundation.txt")


def android_sources(packages):
    packages_for_android = defaultdict(dict)
    for package, files in packages.items():
        for path, content in files.items():
            source_set = path.parts[3]
            if source_set == "commonMain":
                if EXPECT_DECLARATION.search(content):
                    declarations = TOP_LEVEL_DECLARATION.findall(COMMENTS.sub("", content))
                    if any(not EXPECT_DECLARATION.match(declaration) for declaration in declarations):
                        raise ValueError(f"Keep expect declarations in a dedicated file for Android export: {path}")
                    continue
            elif source_set == "androidMain":
                content = ACTUAL_MODIFIER.sub("", content)
            else:
                continue
            packages_for_android[package][path] = content
    return packages_for_android


def android_target(path):
    # Keep Android implementations distinct from shared files with the same stem.
    if path.parts[3] == "androidMain" and not path.name.endswith(".android.kt"):
        path = path.with_name(f"{path.stem}.android.kt")
    return "~/src/main/" + Path(*path.parts[4:]).as_posix()


def multiplatform_target(path):
    return "~/" + Path(*path.parts[2:]).as_posix()


def registry_item(name, description, sources, target_for):
    files = [
        registry_file(path, target_for(path), content)
        for path, content in sorted(sources.items())
    ]
    files.append(license_file())
    if any("/icons/emoji/" in file["path"] for file in files):
        path = Path("shadcn/registry/twemoji.txt")
        files.append(registry_file(
            path, "~/licenses/compose-foundation-twemoji.txt",
            (ROOT / path).read_text(encoding="utf-8"),
        ))
    if len({file["target"] for file in files}) != len(files):
        raise ValueError(f"Duplicate install target in {name}")
    return {
        "name": name,
        "type": "registry:item",
        "title": name.replace("-", " ").title(),
        "description": description,
        "files": files,
    }


def kotlin_entries(packages):
    entries = {
        "theme": (PACKAGE_PREFIX + "ui.theme", "Compose Foundation theme and design tokens."),
        "shimmer": (PACKAGE_PREFIX + "ui.styles", "Compose Foundation shimmer modifier."),
        "scroll-fade": (PACKAGE_PREFIX + "ui.styles", "Compose Foundation scroll-fade modifier."),
    }
    for package, files in sorted(packages.items()):
        if not package.startswith(COMPONENT_PREFIX):
            continue
        component = package.removeprefix(COMPONENT_PREFIX)
        if component == "overlay" or "." in component:
            continue
        if not any(path.parts[3] == "commonMain" for path in files):
            continue
        name = COMPONENT_NAMES.get(component, component)
        if name in entries:
            raise ValueError(f"Duplicate registry item {name}")
        entries[name] = (package, f"Compose Foundation {name} component and its source dependencies.")

    return entries


def kotlin_items(packages, entries, target_for):
    dependencies = package_dependencies(packages)
    return [
        registry_item(name, description, collect_files(package, packages, dependencies), target_for)
        for name, (package, description) in sorted(entries.items())
    ]


def build_registry(output):
    packages = load_sources()
    entries = kotlin_entries(packages)
    write_registry(output, "compose-foundation", kotlin_items(packages, entries, multiplatform_target))
    write_registry(
        output / "android", "compose-foundation-android",
        kotlin_items(android_sources(packages), entries, android_target),
    )
    write_registry(output / "swiftui", "compose-foundation-swiftui", swiftui_items(entries))


def swiftui_items(entries):
    foundation = [
        source_file(SWIFT_PACKAGE / "Package.swift", "~/Package.swift"),
        source_file(SWIFT_SOURCES / "FoundationTheme.swift",
                    "~/Sources/ComposeFoundationSwiftUI/FoundationTheme.swift"),
        license_file(),
    ]
    items = [{
        "name": "theme",
        "type": "registry:item",
        "title": "SwiftUI Theme",
        "description": "Compose Foundation light and dark palettes for SwiftUI.",
        "files": foundation,
    }]
    for name in sorted(entries.keys() - {"theme"}):
        type_name = "".join(part.title() for part in name.split("-"))
        filename = SWIFT_FILENAMES.get(name, f"Foundation{type_name}.swift")
        if not (ROOT / SWIFT_SOURCES / filename).is_file():
            raise ValueError(f"Missing SwiftUI implementation for {name}: {filename}")
        files = [
            source_file(SWIFT_SOURCES / file, f"~/Sources/ComposeFoundationSwiftUI/{file}")
            for file in (filename, *SWIFT_SUPPORT_FILES.get(name, ()))
        ]
        items.append({
            "name": name,
            "type": "registry:item",
            "title": f"SwiftUI {name.title()}",
            "description": f"Compose Foundation {name} for SwiftUI.",
            "files": [*foundation, *files],
        })
    return items


def write_registry(output, registry_name, items):
    output.mkdir(parents=True, exist_ok=True)
    catalog = []
    for item in sorted(items, key=lambda item: item["name"]):
        write_json(output / f"{item['name']}.json", {
            "$schema": "https://ui.shadcn.com/schema/registry-item.json",
            **item,
        })
        catalog.append({
            **item,
            "files": [
                {key: value for key, value in file.items() if key != "content"}
                for file in item["files"]
            ],
        })
    write_json(output / "registry.json", {
        "$schema": "https://ui.shadcn.com/schema/registry.json",
        "name": registry_name,
        "homepage": HOMEPAGE,
        "items": catalog,
    })


def write_json(path, payload):
    path.write_text(json.dumps(payload, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--output", type=Path, default=ROOT / "build/shadcn-registry")
    build_registry(parser.parse_args().output)
