#!/usr/bin/env python3
"""Co-locate each component (+ styles + tests) under components/<name>/."""

from __future__ import annotations

import re
import shutil
from pathlib import Path

ROOT = Path("/Users/cscs/IdeaProjects/angryecho")
SHADCN = ROOT / "shadcn-compose"
MAIN = SHADCN / "shadcn/shared/src/commonMain/kotlin/com/toutiaoyan/compose/ui"
JVM_TEST = SHADCN / "shadcn/shared/src/jvmTest/kotlin/com/toutiaoyan/compose/ui"
COMMON_TEST = SHADCN / "shadcn/shared/src/commonTest/kotlin/com/toutiaoyan/compose/ui"
COMP = MAIN / "components"
STYLES = MAIN / "styles"
PKG = "com.toutiaoyan.compose.ui.components"

# component file stem -> subpackage dir name
COMPONENTS: dict[str, str] = {
    "Accordion": "accordion",
    "AlertDialog": "alertdialog",
    "AspectRatio": "aspectratio",
    "Attachment": "attachment",
    "Avatar": "avatar",
    "Badge": "badge",
    "Breadcrumb": "breadcrumb",
    "Bubble": "bubble",
    "Button": "button",
    "ButtonGroup": "buttongroup",
    "Calendar": "calendar",
    "Card": "card",
    "Carousel": "carousel",
    "Chart": "chart",
    "Checkbox": "checkbox",
    "Chip": "chip",
    "Collapsible": "collapsible",
    "Combobox": "combobox",
    "Command": "command",
    "ContextMenu": "contextmenu",
    "Dialog": "dialog",
    "DropdownMenu": "dropdownmenu",
    "EmojiText": "emojitext",
    "Empty": "empty",
    "Field": "field",
    "GroupCorners": "buttongroup",  # shared helper lives with ButtonGroup
    "HoverCard": "hovercard",
    "Icon": "icon",
    "InputGroup": "inputgroup",
    "InputOTP": "inputotp",
    "Item": "item",
    "Kbd": "kbd",
    "Label": "label",
    "Marker": "marker",
    "Menubar": "menubar",
    "Message": "message",
    "MessageScroller": "messagescroller",
    "NavigationMenu": "navigationmenu",
    "Pagination": "pagination",
    "Popover": "popover",
    "Progress": "progress",
    "RadioButton": "radiobutton",
    "Resizable": "resizable",
    "ScrollArea": "scrollarea",
    "Select": "select",
    "Separator": "separator",
    "Sheet": "sheet",
    "Sidebar": "sidebar",
    "Skeleton": "skeleton",
    "Slider": "slider",
    "Spinner": "spinner",
    "Stepper": "stepper",
    "Switch": "switch",
    "Table": "table",
    "Tabs": "tabs",
    "Text": "text",
    "TextField": "textfield",
    "Textarea": "textarea",
    "Toast": "toast",
    "Toggle": "toggle",
    "ToggleGroup": "togglegroup",
    "Tooltip": "tooltip",
}

# Whole styles file -> subpackage
WHOLE_STYLES: dict[str, str] = {
    "ButtonStyles.kt": "button",
    "BadgeStyles.kt": "badge",
    "AvatarStyles.kt": "avatar",
    "CardStyles.kt": "card",
    "ChipStyles.kt": "chip",
    "CheckboxStyles.kt": "checkbox",
    "RadioButtonStyles.kt": "radiobutton",
    "SwitchStyles.kt": "switch",
    "ToggleStyles.kt": "toggle",
    "SliderStyles.kt": "slider",
    "SelectStyles.kt": "select",
    "TextFieldStyles.kt": "textfield",
    "DialogStyles.kt": "dialog",
    "SeparatorStyles.kt": "separator",
    "KbdStyles.kt": "kbd",
    "ItemStyles.kt": "item",
}

# Multi-file splits: (source, [(start_fn_name, end_fn_name_exclusive_or_None, dest_pkg, dest_filename)])
# We'll split by parsing top-level @Composable fun / enum / sealed

SPLIT_OWNERS: dict[str, str] = {
    # MenuCollapsiblePaginationStyles
    "rememberMenuPanelStyle": "styles",  # stay shared
    "rememberMenuItemStyle": "styles",
    "rememberMenuLabelStyle": "styles",
    "rememberCollapsibleContentStyle": "collapsible",
    "rememberPaginationButtonStyle": "pagination",
    "rememberCommandPanelStyle": "command",
    "rememberMenubarStyle": "menubar",
    "rememberMenubarTriggerStyle": "menubar",
    "rememberButtonGroupOutlineStyle": "buttongroup",
    "rememberButtonGroupTextStyle": "buttongroup",
    # SidebarCalendarMiscStyles
    "rememberSidebarRailStyle": "sidebar",
    "rememberSidebarContentStyle": "sidebar",
    "rememberSidebarInsetStyle": "sidebar",
    "rememberSidebarTriggerStyle": "sidebar",
    "rememberSidebarMenuItemStyle": "sidebar",
    "rememberCalendarRootStyle": "calendar",
    "rememberCalendarNavButtonStyle": "calendar",
    "rememberCalendarDayCellChromeStyle": "calendar",
    "rememberScrollThumbStyle": "scrollarea",
    "rememberAttachmentMediaStyle": "attachment",
    "rememberResizableHandleStyle": "resizable",
    "rememberResizableGripStyle": "resizable",
    "rememberCarouselDotStyle": "carousel",
    "rememberChartLegendSwatchStyle": "chart",
    "rememberFieldSeparatorLineStyle": "field",
    # ComboboxNavMessageStepperStyles
    "rememberComboboxPanelStyle": "combobox",
    "rememberComboboxTriggerStyle": "combobox",
    "rememberNavMenuItemStyle": "navigationmenu",
    "rememberNavMenuPanelStyle": "navigationmenu",
    "rememberMessageAvatarStyle": "message",
    "rememberStepCircleStyle": "stepper",
    "rememberStepConnectorStyle": "stepper",
    # SkeletonProgressEmptyStyles
    "rememberSkeletonStyle": "skeleton",
    "rememberProgressTrackStyle": "progress",
    "rememberEmptyStyle": "empty",
    "rememberEmptyMediaStyle": "empty",
    # OverlayNavTableStyles
    "rememberHoverCardContentStyle": "hovercard",
    "rememberPopoverContentStyle": "popover",
    "rememberTabsListStyle": "tabs",
    "rememberTabItemStyle": "tabs",
    "rememberTabsIndicatorStyle": "tabs",
    "rememberToggleGroupOutlineStyle": "togglegroup",
    "rememberTableRowStyle": "table",
    "rememberToastCardStyle": "toast",
    # TooltipSheetAccordionStyles
    "rememberTooltipContentStyle": "tooltip",
    "rememberSheetPanelStyle": "sheet",
    "rememberAccordionTriggerStyle": "accordion",
    "rememberSpinnerStyle": "spinner",
    "MarkerVariant.rememberStyle": "marker",
    "MarkerVariant": "marker",
}

MULTI_STYLE_FILES = [
    "MenuCollapsiblePaginationStyles.kt",
    "SidebarCalendarMiscStyles.kt",
    "ComboboxNavMessageStepperStyles.kt",
    "SkeletonProgressEmptyStyles.kt",
    "OverlayNavTableStyles.kt",
    "TooltipSheetAccordionStyles.kt",
]

JVM_TEST_MAP: dict[str, str] = {
    "AccordionScreenshotTest.kt": "accordion",
    "AlertDialogScreenshotTest.kt": "alertdialog",
    "AspectRatioScreenshotTest.kt": "aspectratio",
    "AttachmentScreenshotTest.kt": "attachment",
    "AvatarScreenshotTest.kt": "avatar",
    "BadgeScreenshotTest.kt": "badge",
    "BreadcrumbScreenshotTest.kt": "breadcrumb",
    "BubbleScreenshotTest.kt": "bubble",
    "ButtonGroupScreenshotTest.kt": "buttongroup",
    "ButtonScreenshotTest.kt": "button",
    "CalendarScreenshotTest.kt": "calendar",
    "CardScreenshotTest.kt": "card",
    "CarouselScreenshotTest.kt": "carousel",
    "ChartScreenshotTest.kt": "chart",
    "CheckboxScreenshotTest.kt": "checkbox",
    "ChipScreenshotTest.kt": "chip",
    "CollapsibleScreenshotTest.kt": "collapsible",
    "ComboboxScreenshotTest.kt": "combobox",
    "CommandScreenshotTest.kt": "command",
    "ContextMenuScreenshotTest.kt": "contextmenu",
    "DatePickerScreenshotTest.kt": "calendar",
    "DialogScreenshotTest.kt": "dialog",
    "DropdownMenuScreenshotTest.kt": "dropdownmenu",
    "EmptyScreenshotTest.kt": "empty",
    "FieldScreenshotTest.kt": "field",
    "HoverCardScreenshotTest.kt": "hovercard",
    "InputGroupScreenshotTest.kt": "inputgroup",
    "InputOTPScreenshotTest.kt": "inputotp",
    "ItemScreenshotTest.kt": "item",
    "KbdScreenshotTest.kt": "kbd",
    "LabelScreenshotTest.kt": "label",
    "MarkerScreenshotTest.kt": "marker",
    "MenubarScreenshotTest.kt": "menubar",
    "MessageScreenshotTest.kt": "message",
    "MessageScrollerScreenshotTest.kt": "messagescroller",
    "NavigationMenuScreenshotTest.kt": "navigationmenu",
    "PaginationScreenshotTest.kt": "pagination",
    "PopoverScreenshotTest.kt": "popover",
    "ProgressScreenshotTest.kt": "progress",
    "RadioButtonScreenshotTest.kt": "radiobutton",
    "ResizableScreenshotTest.kt": "resizable",
    "ScrollAreaScreenshotTest.kt": "scrollarea",
    "SelectScreenshotTest.kt": "select",
    "SeparatorScreenshotTest.kt": "separator",
    "SheetScreenshotTest.kt": "sheet",
    "SidebarScreenshotTest.kt": "sidebar",
    "SkeletonScreenshotTest.kt": "skeleton",
    "SliderScreenshotTest.kt": "slider",
    "SpinnerScreenshotTest.kt": "spinner",
    "StepperScreenshotTest.kt": "stepper",
    "SwitchScreenshotTest.kt": "switch",
    "TableScreenshotTest.kt": "table",
    "TabsScreenshotTest.kt": "tabs",
    "TextFieldScreenshotTest.kt": "textfield",
    "TextScreenshotTest.kt": "text",
    "TextareaScreenshotTest.kt": "textarea",
    "ToastScreenshotTest.kt": "toast",
    "ToggleGroupScreenshotTest.kt": "togglegroup",
    "ToggleScreenshotTest.kt": "toggle",
    "TooltipScreenshotTest.kt": "tooltip",
    # helpers stay at components root test or move to styles tests — leave in place under styles pkg later
}

COMMON_TEST_MAP: dict[str, str] = {
    "MessageScrollerTest.kt": "messagescroller",
    "ResizablePanelGroupTest.kt": "resizable",
    "ScrollThumbMathTest.kt": "scrollarea",
}

STYLE_HELPER_IMPORTS = [
    "com.toutiaoyan.compose.ui.styles.border",
    "com.toutiaoyan.compose.ui.styles.disabledDim",
    "com.toutiaoyan.compose.ui.styles.focusRing",
    "com.toutiaoyan.compose.ui.styles.focusRingAlways",
]


def ensure_dir(p: Path) -> None:
    p.mkdir(parents=True, exist_ok=True)


def set_package(text: str, new_pkg: str) -> str:
    return re.sub(r"^package\s+[\w.]+\s*$", f"package {new_pkg}", text, count=1, flags=re.M)


def add_imports(text: str, imports: list[str]) -> str:
    existing = set(re.findall(r"^import\s+([\w.]+)", text, flags=re.M))
    to_add = [i for i in imports if i not in existing]
    if not to_add:
        return text
    lines = text.splitlines(keepends=True)
    last_import = max((i for i, l in enumerate(lines) if l.startswith("import ")), default=-1)
    block = "".join(f"import {i}\n" for i in to_add)
    if last_import >= 0:
        lines.insert(last_import + 1, block)
    else:
        # after package
        for i, l in enumerate(lines):
            if l.startswith("package "):
                insert_at = i + 1
                if insert_at < len(lines) and lines[insert_at].strip() == "":
                    insert_at += 1
                lines.insert(insert_at, "\n" + block)
                break
    return "".join(lines)


def needs_helper(text: str, name: str) -> bool:
    return bool(re.search(rf"\b{name}\s*\(", text))


def inject_style_helpers(text: str) -> str:
    imports = []
    for full in STYLE_HELPER_IMPORTS:
        simple = full.rsplit(".", 1)[-1]
        if needs_helper(text, simple):
            imports.append(full)
    return add_imports(text, imports)


def move_file(src: Path, dest: Path, new_pkg: str | None) -> None:
    ensure_dir(dest.parent)
    text = src.read_text()
    if new_pkg:
        text = set_package(text, new_pkg)
    if new_pkg and new_pkg.startswith(PKG + ".") and "Styles" in dest.name:
        text = inject_style_helpers(text)
    dest.write_text(text)
    src.unlink()


def split_multi_style(filename: str) -> None:
    src = STYLES / filename
    text = src.read_text()
    lines = text.splitlines(keepends=True)

    # header = everything until first top-level declaration after imports
    decl_pat = re.compile(
        r"^(?:@\w+(?:\([^)]*\))?\s*)*(?:fun |sealed |enum |class |interface |@Composable)",
        re.M,
    )

    # Find top-level declaration starts (column 0)
    starts: list[tuple[int, str]] = []
    i = 0
    while i < len(lines):
        line = lines[i]
        if line.startswith("@file:"):
            i += 1
            continue
        # accumulate annotations
        if line.startswith("@") or line.startswith("fun ") or line.startswith("sealed ") or line.startswith("enum ") or line.startswith("class ") or line.startswith("interface "):
            # find fun/enum name
            chunk_start = i
            while i < len(lines) and (lines[i].startswith("@") or lines[i].strip() == ""):
                # blank between annotations rare; stop on blank before fun? keep going on @ only
                if lines[i].strip() == "" and i > chunk_start:
                    break
                if lines[i].startswith("@"):
                    i += 1
                    continue
                break
            if i >= len(lines):
                break
            m = re.match(
                r"^(?:fun\s+(?:[\w.]+\.)?(\w+)|enum\s+class\s+(\w+)|sealed\s+(?:interface|class)\s+(\w+)|(?:data\s+)?class\s+(\w+)|interface\s+(\w+))",
                lines[i],
            )
            if not m and lines[i].startswith("@Composable"):
                i += 1
                continue
            if m:
                name = next(g for g in m.groups() if g)
                # For `fun MarkerVariant.rememberStyle` capture as MarkerVariant.rememberStyle
                ext = re.match(r"^fun\s+([\w.]+)\.(\w+)", lines[i])
                key = f"{ext.group(1)}.{ext.group(2)}" if ext else name
                starts.append((chunk_start, key))
                i += 1
                continue
        i += 1

    if not starts:
        raise SystemExit(f"no decls in {filename}")

    # header: lines before first decl
    header = "".join(lines[: starts[0][0]])
    # strip package/imports from header; rebuild per dest
    file_optin = ""
    for l in lines:
        if l.startswith("@file:"):
            file_optin += l
        else:
            break

    # collect common imports from original
    imports = [l for l in lines if l.startswith("import ")]
    import_block = "".join(imports)

    # body slices
    chunks: dict[str, list[str]] = {}
    for idx, (start, key) in enumerate(starts):
        end = starts[idx + 1][0] if idx + 1 < len(starts) else len(lines)
        owner = SPLIT_OWNERS.get(key)
        if owner is None:
            # try bare function name
            bare = key.split(".")[-1]
            owner = SPLIT_OWNERS.get(bare)
        if owner is None:
            raise SystemExit(f"No owner for {key} in {filename}")
        chunks.setdefault(owner, []).append("".join(lines[start:end]))

    # write outs
    for owner, bodies in chunks.items():
        body = "".join(bodies)
        if owner == "styles":
            out_pkg = "com.toutiaoyan.compose.ui.styles"
            # accumulate into MenuStyles.kt
            out_path = STYLES / "MenuStyles.kt"
            if out_path.exists():
                existing = out_path.read_text()
                # append bodies if not present
                if bodies[0][:40] not in existing:
                    out_path.write_text(existing.rstrip() + "\n\n" + body)
                continue
            content = (
                file_optin
                + f"package {out_pkg}\n\n"
                + import_block
                + ("\n" if not import_block.endswith("\n") else "")
                + body
            )
            # ensure rememberTheme import
            if "rememberTheme" in body and "utils.rememberTheme" not in content:
                content = add_imports(content, ["com.toutiaoyan.compose.ui.utils.rememberTheme"])
            out_path.write_text(content)
        else:
            out_pkg = f"{PKG}.{owner}"
            out_name = {
                "collapsible": "CollapsibleStyles.kt",
                "pagination": "PaginationStyles.kt",
                "command": "CommandStyles.kt",
                "menubar": "MenubarStyles.kt",
                "buttongroup": "ButtonGroupStyles.kt",
                "sidebar": "SidebarStyles.kt",
                "calendar": "CalendarStyles.kt",
                "scrollarea": "ScrollAreaStyles.kt",
                "attachment": "AttachmentStyles.kt",
                "resizable": "ResizableStyles.kt",
                "carousel": "CarouselStyles.kt",
                "chart": "ChartStyles.kt",
                "field": "FieldStyles.kt",
                "combobox": "ComboboxStyles.kt",
                "navigationmenu": "NavigationMenuStyles.kt",
                "message": "MessageStyles.kt",
                "stepper": "StepperStyles.kt",
                "skeleton": "SkeletonStyles.kt",
                "progress": "ProgressStyles.kt",
                "empty": "EmptyStyles.kt",
                "hovercard": "HoverCardStyles.kt",
                "popover": "PopoverStyles.kt",
                "tabs": "TabsStyles.kt",
                "togglegroup": "ToggleGroupStyles.kt",
                "table": "TableStyles.kt",
                "toast": "ToastStyles.kt",
                "tooltip": "TooltipStyles.kt",
                "sheet": "SheetStyles.kt",
                "accordion": "AccordionStyles.kt",
                "spinner": "SpinnerStyles.kt",
                "marker": "MarkerStyles.kt",
            }[owner]
            out_dir = COMP / owner
            ensure_dir(out_dir)
            out_path = out_dir / out_name
            content = (
                file_optin
                + f"package {out_pkg}\n\n"
                + import_block
            )
            if "rememberTheme" in body:
                content = add_imports(content if "package" in content else content, [])
                # rebuild properly
                content = (
                    file_optin
                    + f"package {out_pkg}\n\n"
                    + import_block
                )
                if "utils.rememberTheme" not in content:
                    content += "import com.toutiaoyan.compose.ui.utils.rememberTheme\n"
            content += "\n" + body
            content = inject_style_helpers(content)
            if out_path.exists():
                prev = out_path.read_text()
                # merge bodies
                # drop duplicate package/imports — append body only
                out_path.write_text(prev.rstrip() + "\n\n" + body)
            else:
                out_path.write_text(content)

    src.unlink()


def rewrite_imports_in_tree() -> None:
    """Rewrite known import paths across the monorepo."""
    # Build rename list: old_full -> new_full for importable symbols
    # Order longer first
    renames: list[tuple[str, str]] = []

    # components.Foo -> components.pkg.Foo for each component stem
    # Also nested types often imported as components.ButtonVariant from styles previously
    for stem, pkg in sorted(COMPONENTS.items(), key=lambda x: -len(x[0])):
        if stem == "GroupCorners":
            renames.append((f"{PKG}.GroupCorners", f"{PKG}.buttongroup.GroupCorners"))
            continue
        renames.append((f"{PKG}.{stem}", f"{PKG}.{pkg}.{stem}"))

    # styles symbols that moved — common ones
    style_symbol_moves: list[tuple[str, str]] = [
        ("ButtonVariant", "button"),
        ("ButtonAnimation", "button"),
        ("ButtonSize", "button"),
        ("BadgeVariant", "badge"),
        ("AvatarSize", "avatar"),
        ("CardVariant", "card"),
        ("CardSize", "card"),
        ("ChipVariant", "chip"),
        ("ToggleVariant", "toggle"),
        ("SelectVariant", "select"),
        ("TextFieldVariant", "textfield"),
        ("SeparatorOrientation", "separator"),
        ("ItemVariant", "item"),
        ("MarkerVariant", "marker"),
        ("rememberCheckboxStyle", "checkbox"),
        ("rememberRadioButtonStyle", "radiobutton"),
        ("rememberSwitchTrackStyle", "switch"),
        ("rememberSliderTrackStyle", "slider"),
        ("rememberSliderRangeStyle", "slider"),
        ("rememberSliderThumbStyle", "slider"),
        ("rememberSelectItemStyle", "select"),
        ("rememberDialogPanelStyle", "dialog"),
        ("rememberKbdStyle", "kbd"),
        ("rememberAvatarFallbackStyle", "avatar"),
        ("rememberMenuPanelStyle", "styles"),  # styles.MenuStyles — package stays styles
        ("rememberMenuItemStyle", "styles"),
        ("rememberMenuLabelStyle", "styles"),
        ("rememberCollapsibleContentStyle", "collapsible"),
        ("rememberPaginationButtonStyle", "pagination"),
        ("rememberCommandPanelStyle", "command"),
        ("rememberMenubarStyle", "menubar"),
        ("rememberMenubarTriggerStyle", "menubar"),
        ("rememberButtonGroupOutlineStyle", "buttongroup"),
        ("rememberButtonGroupTextStyle", "buttongroup"),
        ("rememberSidebarRailStyle", "sidebar"),
        ("rememberSidebarContentStyle", "sidebar"),
        ("rememberSidebarInsetStyle", "sidebar"),
        ("rememberSidebarTriggerStyle", "sidebar"),
        ("rememberSidebarMenuItemStyle", "sidebar"),
        ("rememberCalendarRootStyle", "calendar"),
        ("rememberCalendarNavButtonStyle", "calendar"),
        ("rememberCalendarDayCellChromeStyle", "calendar"),
        ("rememberScrollThumbStyle", "scrollarea"),
        ("rememberAttachmentMediaStyle", "attachment"),
        ("rememberResizableHandleStyle", "resizable"),
        ("rememberResizableGripStyle", "resizable"),
        ("rememberCarouselDotStyle", "carousel"),
        ("rememberChartLegendSwatchStyle", "chart"),
        ("rememberFieldSeparatorLineStyle", "field"),
        ("rememberComboboxPanelStyle", "combobox"),
        ("rememberComboboxTriggerStyle", "combobox"),
        ("rememberNavMenuItemStyle", "navigationmenu"),
        ("rememberNavMenuPanelStyle", "navigationmenu"),
        ("rememberMessageAvatarStyle", "message"),
        ("rememberStepCircleStyle", "stepper"),
        ("rememberStepConnectorStyle", "stepper"),
        ("rememberSkeletonStyle", "skeleton"),
        ("rememberProgressTrackStyle", "progress"),
        ("rememberEmptyStyle", "empty"),
        ("rememberEmptyMediaStyle", "empty"),
        ("rememberHoverCardContentStyle", "hovercard"),
        ("rememberPopoverContentStyle", "popover"),
        ("rememberTabsListStyle", "tabs"),
        ("rememberTabItemStyle", "tabs"),
        ("rememberTabsIndicatorStyle", "tabs"),
        ("rememberToggleGroupOutlineStyle", "togglegroup"),
        ("rememberTableRowStyle", "table"),
        ("rememberToastCardStyle", "toast"),
        ("rememberTooltipContentStyle", "tooltip"),
        ("rememberSheetPanelStyle", "sheet"),
        ("rememberAccordionTriggerStyle", "accordion"),
        ("rememberSpinnerStyle", "spinner"),
    ]

    styles_pkg = "com.toutiaoyan.compose.ui.styles"
    for sym, owner in style_symbol_moves:
        old = f"{styles_pkg}.{sym}"
        if owner == "styles":
            # stays in styles (MenuStyles)
            continue
        new = f"{PKG}.{owner}.{sym}"
        renames.append((old, new))

    # Also components.styles.rememberStyle imports that pointed at extensions — now in component pkgs
    # import com.toutiaoyan.compose.ui.styles.rememberStyle — used for ButtonVariant.rememberStyle etc.
    # Those extensions move with variants; importers that only needed extension should import from new pkg.
    # Leave styles.rememberStyle if any top-level remains — there isn't; it's rememberTheme now.
    # Many files: import styles.rememberStyle for Xxx.rememberStyle() — rewrite per usage is hard.
    # We'll handle via compile errors: change to import components.button.rememberStyle etc.
    # For now, drop/replace common pattern: files importing styles.rememberStyle alongside ButtonVariant
    # -> import button.rememberStyle

    renames.sort(key=lambda x: -len(x[0]))

    kt_files = list(ROOT.rglob("*.kt"))
    # skip build dirs
    kt_files = [p for p in kt_files if "build/" not in str(p) and "/.git/" not in str(p)]

    for path in kt_files:
        text = path.read_text()
        orig = text
        for old, new in renames:
            text = text.replace(old, new)
        if text != orig:
            path.write_text(text)


def main() -> None:
    # 1) Move component sources
    for stem, pkg in COMPONENTS.items():
        src = COMP / f"{stem}.kt"
        if not src.exists():
            print(f"skip missing component {stem}")
            continue
        dest = COMP / pkg / f"{stem}.kt"
        move_file(src, dest, f"{PKG}.{pkg}")

    # 2) Move whole styles
    for fname, pkg in WHOLE_STYLES.items():
        src = STYLES / fname
        if not src.exists():
            print(f"skip missing style {fname}")
            continue
        dest = COMP / pkg / fname
        move_file(src, dest, f"{PKG}.{pkg}")

    # 3) Split multi styles
    for fname in MULTI_STYLE_FILES:
        if (STYLES / fname).exists():
            print(f"split {fname}")
            split_multi_style(fname)

    # 4) Move jvm tests
    jvm_comp = JVM_TEST / "components"
    for fname, pkg in JVM_TEST_MAP.items():
        src = jvm_comp / fname
        if not src.exists():
            print(f"skip missing test {fname}")
            continue
        dest = jvm_comp / pkg / fname
        move_file(src, dest, f"{PKG}.{pkg}")

    # 5) Move common tests
    common_comp = COMMON_TEST / "components"
    for fname, pkg in COMMON_TEST_MAP.items():
        src = common_comp / fname
        if not src.exists():
            continue
        dest = common_comp / pkg / fname
        move_file(src, dest, f"{PKG}.{pkg}")

    # 6) Rewrite imports
    rewrite_imports_in_tree()
    print("done moves + import rewrite")


if __name__ == "__main__":
    main()
