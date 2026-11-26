"""Exercise Android catalog interactions on a running emulator."""

import argparse
from calendar import monthrange
from datetime import date, timedelta
from pathlib import Path
import re
import time
import xml.etree.ElementTree as ET

from verify_android_catalog import AndroidCatalog, PACKAGE


def expect_popup(catalog, visible):
    deadline = time.monotonic() + 3
    while True:
        windows = re.split(r"(?m)^\s+Window #\d+ ", catalog.shell("dumpsys", "window", "windows"))
        is_open = any(
            "Pop-Up Window" in window.splitlines()[0] and f"package={PACKAGE}" in window
            and "isVisible=true" in window
            for window in windows if window
        )
        if is_open == visible:
            return
        if time.monotonic() >= deadline:
            raise AssertionError(f"Expected the catalog popup to be {'open' if visible else 'closed'}")
        time.sleep(0.1)


def verify_popover(catalog, output):
    catalog.open("popover")
    catalog.expect(("Popover", "Open popover"))
    expect_popup(catalog, False)
    catalog.tap("Open popover")
    expect_popup(catalog, True)
    if output:
        time.sleep(0.2)
        catalog.screenshot(output / "popover-open.png")
    catalog.shell("input", "keyevent", "KEYCODE_ESCAPE")
    expect_popup(catalog, False)

    x, y = catalog.tap("Open popover")
    expect_popup(catalog, True)
    catalog.shell("input", "tap", str(max(1, x // 16)), str(y // 2))
    expect_popup(catalog, False)
    print("PASS: Popover touch open, hardware Escape, outside touch", flush=True)


def verify_modals(catalog):
    for route, page, trigger, heading, actions, cancel in (
        ("alert-dialog", "Alert Dialog", "Show Dialog", "Are you absolutely sure?",
         ("Continue", "Cancel"), "Cancel"),
        ("dialog", "Dialog", "Open Dialog", "Edit profile", ("Save changes", "Cancel"), "Cancel"),
        ("drawer", "Drawer", "Open Drawer", "Pick a delivery time",
         ("Confirm Delivery Time", "Cancel"), "Cancel"),
        ("sheet", "Sheet", "Open", "Edit profile", ("Save changes", "Close"), "Close"),
    ):
        catalog.open(route)
        catalog.expect((page, trigger), absent=(heading,))
        catalog.tap(trigger)
        catalog.expect((heading, *actions), absent=(trigger,))
        catalog.tap(cancel)
        catalog.expect((page, trigger), absent=(heading,))
        catalog.tap(trigger)
        catalog.expect((heading, *actions), absent=(trigger,))
        catalog.shell("input", "keyevent", "KEYCODE_ESCAPE")
        catalog.expect((page, trigger), absent=(heading,))
        print(f"PASS: {page} opens by touch and closes by {cancel} or hardware Escape", flush=True)


def verify_menus(catalog):
    for route, page, trigger, chosen, other in (
        ("dropdown-menu", "Dropdown Menu", "Open", "Profile", "Billing"),
        ("menubar", "Menubar", "File", "New Tab", "New Window"),
    ):
        catalog.open(route)
        catalog.expect((page, trigger), absent=(chosen, other))
        catalog.tap(trigger)
        catalog.expect((chosen, other))
        catalog.tap(chosen)
        catalog.expect((page, trigger), absent=(chosen, other))
        catalog.tap(trigger)
        catalog.expect((chosen, other))
        catalog.shell("input", "keyevent", "KEYCODE_ESCAPE")
        catalog.expect((page, trigger), absent=(chosen, other))
        print(f"PASS: {page} opens by touch and closes on item selection or hardware Escape", flush=True)


def verify_button_group(catalog):
    catalog.open("button-group")
    trigger = "More Options"
    actions = ("Mark as Read", "Add to Calendar")
    catalog.expect(("Button Group", "Message actions", trigger), absent=actions)
    catalog.tap(trigger)
    catalog.expect(actions)
    catalog.shell("input", "keyevent", "KEYCODE_ESCAPE")
    catalog.expect(("Button Group", trigger), absent=actions)
    print("PASS: Button Group opens the overflow menu by touch and closes it with Escape", flush=True)


def verify_tabs(catalog):
    catalog.open("tabs")
    overview = "You have 12 active projects and 3 pending tasks."
    analytics = "Page views are up 25% compared to last month."
    catalog.expect(("Tabs", overview))
    catalog.tap("Analytics")
    catalog.expect((analytics,), absent=(overview,))
    print("PASS: Tabs touch selection swaps the visible panel", flush=True)


def focused_labels(catalog, payload=None, *, clickable=False):
    return {
        value for node in ET.fromstring(payload if payload is not None else catalog.hierarchy()).iter("node")
        if node.get("focused") == "true" and (not clickable or node.get("clickable") == "true")
        for descendant in node.iter("node") for key in ("text", "content-desc")
        if (value := descendant.get(key))
    }


def tab_to(catalog, label, *, clickable=False):
    for _ in range(15):
        catalog.shell("input", "keyevent", "KEYCODE_TAB")
        try:
            labels = focused_labels(catalog, clickable=clickable)
        except RuntimeError:
            continue
        if label in labels:
            return
    raise AssertionError(f"Tab did not focus {label!r}")


def verify_keyboard_menu(catalog, route, page, trigger, first, second):
    catalog.open(route)
    catalog.expect((page, trigger), absent=(first,))
    tab_to(catalog, trigger)
    for closing_key in ("KEYCODE_ESCAPE", "KEYCODE_ENTER"):
        catalog.shell("input", "keyevent", "KEYCODE_ENTER")
        opened = catalog.expect((first, second))
        assert first in focused_labels(catalog, opened), f"{page} did not initially focus {first!r}"
        catalog.shell("input", "keyevent", "KEYCODE_DPAD_DOWN")
        assert second in focused_labels(catalog), f"{page} Down did not focus {second!r}"
        catalog.shell("input", "keyevent", closing_key)
        closed = catalog.expect((page, trigger), absent=(first,))
        assert trigger in focused_labels(catalog, closed), f"{page} did not restore focus to {trigger!r}"
    print(f"PASS: {page} keyboard open, Down navigation, selection, Escape and trigger focus restoration", flush=True)


def verify_breadcrumb(catalog):
    catalog.open("breadcrumb")
    trigger, choice = "Toggle menu", "Themes"
    catalog.tap(trigger, catalog.expect(("Breadcrumb", trigger), absent=(choice,)))
    catalog.tap(choice, catalog.expect(("Documentation", choice)))
    catalog.expect(("Breadcrumb", trigger), absent=(choice,))
    print("PASS: Breadcrumb ellipsis opens on touch and selecting Themes closes it", flush=True)


def verify_navigation_menu(catalog):
    catalog.open("navigation-menu")
    trigger = "Getting started"
    x, y = catalog.tap(trigger)
    expect_popup(catalog, True)
    catalog.shell("input", "tap", str(max(1, x // 16)), str(y // 2))
    expect_popup(catalog, False)

    for _ in range(13):
        catalog.shell("input", "keyevent", "KEYCODE_TAB")
        if focused_labels(catalog) == {trigger}:
            break
    else:
        raise AssertionError("Tab did not reach the Navigation Menu trigger")
    catalog.shell("input", "keyevent", "KEYCODE_DPAD_DOWN")
    catalog.expect(("Introduction",))
    assert "Introduction" in focused_labels(catalog), "Down did not focus the first menu link"
    catalog.shell("input", "keyevent", "KEYCODE_ESCAPE")
    expect_popup(catalog, False)
    assert focused_labels(catalog) == {trigger}, "Escape did not restore the Navigation Menu trigger focus"
    print("PASS: Navigation Menu touch open, outside touch, Down entry and Escape focus restoration", flush=True)


def verify_dialog_focus(catalog):
    trigger = "Open Dialog"
    heading = "Edit profile"
    catalog.open("dialog")
    catalog.expect(("Dialog", trigger), absent=(heading,))
    for _ in range(12):
        catalog.shell("input", "keyevent", "KEYCODE_TAB")
        if focused_labels(catalog) == {trigger}:
            break
    else:
        raise AssertionError("Tab did not reach the Dialog trigger")
    catalog.shell("input", "keyevent", "KEYCODE_ENTER")
    catalog.expect((heading, "Cancel"))
    assert focused_labels(catalog) == {"Pedro Duarte"}, "Dialog did not initially focus the Name field"
    catalog.shell("input", "keyevent", "KEYCODE_TAB")
    assert focused_labels(catalog) == {"@peduarte"}, "Tab did not focus the Username field"
    catalog.shell("input", "keyevent", "KEYCODE_ESCAPE")
    catalog.expect(("Dialog", trigger), absent=(heading,))
    assert focused_labels(catalog) == {trigger}, "Dialog did not restore trigger focus"
    catalog.shell("input", "keyevent", "KEYCODE_ENTER")
    catalog.expect((heading, "Cancel"))
    print("PASS: Dialog sets initial field focus, moves focus with Tab, and restores the trigger after Escape",
          flush=True)


def verify_tabs_keyboard(catalog):
    catalog.shell("am", "force-stop", PACKAGE)
    catalog.open("tabs")
    catalog.expect(("Tabs", "You have 12 active projects and 3 pending tasks."))
    for _ in range(13):
        catalog.shell("input", "keyevent", "KEYCODE_TAB")
        if focused_labels(catalog) == {"Overview"}:
            break
    else:
        raise AssertionError("Tab did not reach the Overview tab")
    catalog.shell("input", "keyevent", "KEYCODE_DPAD_RIGHT")
    catalog.expect(("Page views are up 25% compared to last month.",))
    print("PASS: Tabs keyboard focus and Right-arrow selection", flush=True)


def checked_nodes(payload, label=None):
    return [
        node.attrib["checked"] == "true" for node in ET.fromstring(payload).iter("node")
        if node.get("package") == PACKAGE and node.get("checkable") == "true"
        and (label is None or any(label in (child.get("text"), child.get("content-desc"))
                                  for child in node.iter("node")))
    ]


def verify_form_controls(catalog):
    terms = "Accept terms and conditions"
    catalog.open("checkbox")
    assert checked_nodes(catalog.expect(("Checkbox", terms)), terms) == [False, True]
    catalog.tap(terms)
    assert checked_nodes(catalog.expect((terms,)), terms) == [True, True]
    print("PASS: Checkbox touch updates the first option's checked state", flush=True)

    catalog.open("radio-group")
    before = catalog.expect(("Radio Group", "Comfortable", "Compact"))
    assert checked_nodes(before, "Comfortable") == [True]
    catalog.tap("Compact")
    after = catalog.expect(("Comfortable", "Compact"))
    assert checked_nodes(after, "Comfortable") == [False]
    assert checked_nodes(after, "Compact") == [True]
    print("PASS: Radio Group touch changes the single checked option", flush=True)

    catalog.open("switch")
    mode = "Airplane Mode"
    payload = catalog.expect(("Switch", mode))
    assert checked_nodes(payload, mode) == [False]
    visible_label = next(node for node in ET.fromstring(payload).iter("node") if node.get("text") == mode)
    catalog.tap(mode, ET.tostring(visible_label))
    assert checked_nodes(catalog.expect((mode,)), mode) == [True]
    print("PASS: Switch touch on the associated label updates checked state", flush=True)


def verify_label(catalog):
    catalog.open("label")
    label = "Accept terms and conditions"
    payload = catalog.expect(("Label", label))
    assert checked_nodes(payload, label) == [False]
    visible_label = next(node for node in ET.fromstring(payload).iter("node") if node.get("text") == label)
    catalog.tap(label, ET.tostring(visible_label))
    assert checked_nodes(catalog.expect(("Label", label)), label) == [True]
    print("PASS: tapping the Label text changes its associated checkbox", flush=True)


def verify_field(catalog):
    catalog.open("field")
    labels = ("Name on Card", "Card Number")
    placeholder = "Evil Rabbit"
    value = "RegistryTester"
    payload = catalog.expect(("Field", placeholder, *labels))
    catalog.tap(placeholder, payload)
    catalog.shell("input", "text", value)
    payload = catalog.expect((value, *labels))
    assert any(
        node.get("class") == "android.widget.EditText" and node.get("text") == value
        for node in ET.fromstring(payload).iter("node")
    ), "Field did not expose the name as its editable value"
    print("PASS: Field accepts text and retains the grouped form labels", flush=True)


DISCLOSURES = {
    "accordion": ("Accordion", "What is your return policy?", (
        "Returns accepted within 30 days. Items must be unused and in original packaging. "
        "Refunds processed within 5-7 business days.",
    )),
    "collapsible": ("Collapsible", "Toggle details", ("Shipping address", "Items")),
    "trace": ("Trace", "Thought for 5s", (
        "CI installs with a frozen lockfile, so the install stops when package.json and pnpm-lock.yaml disagree. "
        "I should check what the last commit changed, then reproduce the install.",
    )),
}


def verify_disclosure(catalog, route, *, keyboard=False):
    heading, trigger, details = DISCLOSURES[route]
    catalog.open(route)
    before = catalog.expect((heading, trigger), absent=details)
    if keyboard:
        tab_to(catalog, trigger)
        catalog.shell("input", "keyevent", "KEYCODE_ENTER")
    else:
        catalog.tap(trigger, before)
    expanded = catalog.expect((trigger, *details))
    if keyboard:
        assert trigger in focused_labels(catalog, expanded), f"{heading} lost trigger focus after expanding"
        catalog.shell("input", "keyevent", "KEYCODE_SPACE")
    else:
        catalog.tap(trigger, expanded)
    catalog.expect((trigger,), absent=details)
    interaction = "Enter and Space" if keyboard else "touch"
    print(f"PASS: {heading} expands and collapses with {interaction}", flush=True)


def verify_disclosures(catalog):
    for route in ("accordion", "collapsible"):
        verify_disclosure(catalog, route)


def verify_keyboard_toggle(catalog, route, heading, label, *, other_checked=(), entry=None):
    catalog.open(route)
    off = [False, *other_checked]
    on = [True, *other_checked]
    assert checked_nodes(catalog.expect((heading, label)), label) == off
    tab_to(catalog, entry or label)
    if entry:
        catalog.shell("input", "keyevent", "KEYCODE_DPAD_RIGHT")
        assert label in focused_labels(catalog), f"Right did not focus {label!r}"
    catalog.shell("input", "keyevent", "KEYCODE_SPACE")
    assert checked_nodes(catalog.expect((label,)), label) == on
    catalog.shell("input", "keyevent", "KEYCODE_ENTER")
    assert checked_nodes(catalog.expect((label,)), label) == off
    print(f"PASS: {heading} changes the checked state with Space and Enter", flush=True)


def verify_radio_keyboard(catalog):
    catalog.open("radio-group")
    comfortable, compact = "Comfortable", "Compact"
    labels = (comfortable, compact)
    assert checked_nodes(catalog.expect(("Radio Group", *labels)), comfortable) == [True]
    tab_to(catalog, comfortable)
    for key, selected, previous in (("DOWN", compact, comfortable), ("UP", comfortable, compact)):
        catalog.shell("input", "keyevent", f"KEYCODE_DPAD_{key}")
        payload = catalog.expect(labels)
        assert focused_labels(catalog, payload) == {selected}
        assert checked_nodes(payload, selected) == [True]
        assert checked_nodes(payload, previous) == [False]
    print("PASS: Radio Group arrows move focus and select the focused option", flush=True)


def verify_questionnaire(catalog):
    catalog.open("questionnaire")
    first = "Question 1 of 3"
    second = "Question 2 of 3"
    choice = "Tool call timeline"
    payload = catalog.expect((first, choice, "Next"), absent=(second,))
    catalog.tap(choice, payload)
    catalog.tap("Next")
    payload = catalog.expect((second, "What should every progress update include?", "Previous"), absent=(first,))
    catalog.tap("Previous", payload)
    payload = catalog.expect((first, choice, "Next"), absent=(second,))
    catalog.tap("Next", payload)
    catalog.expect((second,), absent=(first,))
    print("PASS: Questionnaire advances, goes back and retains the selected answer", flush=True)


def verify_questionnaire_keyboard(catalog):
    catalog.open("questionnaire")
    first, second = "Question 1 of 3", "Question 2 of 3"
    choice = "Tool call timeline"
    initial = catalog.expect((first, choice), absent=(second,))
    assert checked_nodes(initial, choice) == [False]
    tab_to(catalog, choice, clickable=True)
    catalog.shell("input", "keyevent", "KEYCODE_ENTER")
    catalog.expect((first,), absent=(second,))
    catalog.shell("input", "keyevent", "KEYCODE_SPACE")
    assert checked_nodes(catalog.expect((choice,)), choice) == [True]
    catalog.shell("input", "keyevent", "KEYCODE_ENTER")
    catalog.expect((second, "Previous"), absent=(first,))
    tab_to(catalog, "Previous", clickable=True)
    catalog.shell("input", "keyevent", "KEYCODE_ENTER")
    returned = catalog.expect((first, choice), absent=(second,))
    assert checked_nodes(returned, choice) == [True]
    print("PASS: Questionnaire requires an answer before Enter can advance, Space selects it and Previous retains it", flush=True)


def verify_calendar(catalog):
    catalog.open("calendar")
    year, month = map(int, catalog.shell("date", "+%Y-%m").split("-"))
    next_year, next_month = (year + 1, 1) if month == 12 else (year, month + 1)
    current_date = f"{year}-{month}-15"
    next_date = f"{next_year}-{next_month}-15"
    catalog.expect(("Calendar", current_date), absent=(next_date,))
    catalog.tap("Next month")
    payload = catalog.expect((next_date,), absent=(current_date,))
    assert checked_nodes(payload, next_date) == [False]
    catalog.tap(next_date, payload)
    assert checked_nodes(catalog.expect((next_date,)), next_date) == [True]
    catalog.tap("Previous month")
    catalog.expect((current_date,), absent=(next_date,))
    print("PASS: Calendar touch moves between months and selects a date", flush=True)


def verify_calendar_keyboard(catalog):
    def label(value):
        return f"{value.year}-{value.month}-{value.day}"

    catalog.open("calendar")
    today = date.fromisoformat(catalog.shell("date", "+%Y-%m-%d"))
    tomorrow = today + timedelta(days=1)
    original, next_day = label(today), label(tomorrow)
    assert checked_nodes(catalog.expect(("Calendar", original)), original) == [True]
    tab_to(catalog, original)
    catalog.shell("input", "keyevent", "KEYCODE_DPAD_RIGHT")
    pending = catalog.expect((next_day,))
    assert next_day in focused_labels(catalog, pending)
    assert checked_nodes(pending, next_day) == [False]
    catalog.shell("input", "keyevent", "KEYCODE_ENTER")
    assert checked_nodes(catalog.expect((next_day,)), next_day) == [True]

    year, month = (tomorrow.year + 1, 1) if tomorrow.month == 12 else (tomorrow.year, tomorrow.month + 1)
    next_month = date(year, month, min(tomorrow.day, monthrange(year, month)[1]))
    catalog.shell("input", "keyevent", "KEYCODE_PAGE_DOWN")
    displayed = catalog.expect((label(next_month),))
    assert label(next_month) in focused_labels(catalog, displayed)
    assert checked_nodes(displayed, label(next_month)) == [False]
    print("PASS: Calendar arrows move day focus, Enter selects and Page Down moves by a month", flush=True)


def verify_context_menu(catalog):
    catalog.open("context-menu")
    trigger = "Right click here"
    x, y = map(str, catalog.point(trigger))
    catalog.shell("input", "swipe", x, y, x, y, "750")
    catalog.expect(("Back", "Reload", "Show Bookmarks"), absent=(trigger,))
    catalog.shell("input", "keyevent", "KEYCODE_ESCAPE")
    catalog.expect(("Context Menu", trigger), absent=("Show Bookmarks",))
    print("PASS: Context Menu opens on touch hold and closes with hardware Escape", flush=True)


def verify_context_menu_keys(catalog):
    catalog.open("context-menu")
    trigger = "Right click here"
    catalog.expect(("Context Menu", trigger), absent=("Show Bookmarks",))
    tab_to(catalog, trigger)
    catalog.shell("input", "keycombination", "KEYCODE_SHIFT_LEFT", "KEYCODE_F10")
    catalog.expect(("Back", "Reload", "Show Bookmarks"))
    catalog.shell("input", "keyevent", "KEYCODE_ESCAPE")
    closed = catalog.expect(("Context Menu", trigger), absent=("Show Bookmarks",))
    assert trigger in focused_labels(catalog, closed), "Escape did not restore Context Menu trigger focus"
    print("PASS: Context Menu Shift+F10 opens and Escape restores focus", flush=True)


def verify_keyboard_popup(catalog, route, trigger):
    catalog.open(route)
    catalog.expect((trigger,))
    expect_popup(catalog, False)
    tab_to(catalog, trigger, clickable=True)
    expect_popup(catalog, True)
    catalog.shell("input", "keyevent", "KEYCODE_ESCAPE")
    expect_popup(catalog, False)
    print(f"PASS: {route} opens on Tab focus and closes with Escape", flush=True)


def verify_toast(catalog, *, keyboard=False):
    catalog.open("toast")
    trigger = "Show Toast"
    title = "Event created"
    catalog.expect(("Toast", trigger), absent=(title,))
    if keyboard:
        tab_to(catalog, trigger)
        catalog.shell("input", "keyevent", "KEYCODE_ENTER")
    else:
        catalog.tap(trigger)
    catalog.expect((title, "Undo", "Close toast"))
    catalog.expect(("Toast", trigger), absent=(title,))
    interaction = "Enter" if keyboard else "touch"
    print(f"PASS: Toast {interaction} trigger shows the notification and it expires", flush=True)


def verify_select(catalog):
    prompt = "Select a fruit"
    catalog.open("select")
    catalog.expect(("Select", prompt))
    catalog.tap(prompt)
    catalog.expect(("Apple", "Banana"))
    catalog.shell("input", "keyevent", "KEYCODE_DPAD_DOWN")
    assert focused_labels(catalog) == {"Apple"}
    catalog.shell("input", "keyevent", "KEYCODE_DPAD_DOWN")
    assert focused_labels(catalog) == {"Banana"}
    catalog.shell("input", "keyevent", "KEYCODE_ENTER")
    catalog.expect(("Select", "Banana"), absent=("Apple", prompt))
    print("PASS: Select Down moves focus and Enter commits the choice", flush=True)

    catalog.tap("Banana")
    catalog.expect(("Apple", "Banana"))
    catalog.tap("Apple")
    catalog.expect(("Select", "Apple"), absent=("Banana", prompt))
    print("PASS: Select closes the menu and shows the fruit chosen by touch", flush=True)


def verify_carousel(catalog):
    catalog.open("carousel")
    catalog.expect(("Carousel", "Slide 1 of 5", "Next slide"))
    catalog.tap("Next slide")
    catalog.expect(("Slide 2 of 5", "Previous slide"))
    catalog.tap("Previous slide")
    payload = catalog.expect(("Slide 1 of 5", "Next slide"))
    slide = next(node for node in ET.fromstring(payload).iter("node") if node.get("content-desc") == "Slide 1 of 5")
    left, top, right, bottom = map(int, re.findall(r"\d+", slide.attrib["bounds"]))
    inset = (right - left) // 8
    y = (top + bottom) // 2
    catalog.shell("input", "swipe", str(right - inset), str(y), str(left + inset), str(y), "350")
    catalog.expect(("Slide 2 of 5",))
    print("PASS: Carousel touch controls and horizontal swipe change slides", flush=True)


def verify_carousel_keyboard(catalog):
    def button_has(payload, label, attribute, value):
        return any(
            node.get("clickable") == "true" and node.get(attribute) == value
            and any(child.get("content-desc") == label for child in node.iter("node"))
            for node in ET.fromstring(payload).iter("node")
        )

    catalog.open("carousel")
    previous, following = "Previous slide", "Next slide"
    buttons = (previous, following)
    payload = catalog.expect(("Carousel", "Slide 1 of 5", *buttons))
    assert button_has(payload, previous, "enabled", "false"), "Previous should be disabled on the first slide"
    tab_to(catalog, following)
    for index in range(2, 6):
        key = "KEYCODE_SPACE" if index == 2 else "KEYCODE_ENTER"
        catalog.shell("input", "keyevent", key)
        payload = catalog.expect((f"Slide {index} of 5", *buttons), absent=(f"Slide {index - 1} of 5",))
    assert button_has(payload, following, "enabled", "false"), "Next should be disabled on the last slide"
    assert button_has(payload, previous, "focused", "true"), "Focus should move to Previous on the last slide"

    for index in range(4, 0, -1):
        catalog.shell("input", "keyevent", "KEYCODE_ENTER")
        payload = catalog.expect((f"Slide {index} of 5", *buttons), absent=(f"Slide {index + 1} of 5",))
    assert button_has(payload, previous, "enabled", "false"), "Previous should be disabled after returning to slide 1"
    assert button_has(payload, following, "focused", "true"), "Focus should move to Next on the first slide"
    catalog.shell("input", "keyevent", "KEYCODE_SPACE")
    catalog.expect(("Slide 2 of 5",), absent=("Slide 1 of 5",))
    print("PASS: Carousel transfers keyboard focus and reverses at both ends without an extra Tab", flush=True)


def scrollable_bounds(payload, text):
    tree = ET.fromstring(payload)
    parents = {child: node for node in tree.iter("node") for child in node}
    container = next(node for node in tree.iter("node") if node.get("text") == text)
    while container in parents and container.get("scrollable") != "true":
        container = parents[container]
    assert container.get("scrollable") == "true", f"{text!r} does not have a scrollable container"
    return tuple(map(int, re.findall(r"\d+", container.attrib["bounds"])))


def verify_scroll_area(catalog):
    catalog.open("scroll-area")
    first, later = "v1.2.0-beta.50", "v1.2.0-beta.43"
    payload = catalog.expect(("Scroll Area", "Tags", first), absent=(later,))
    left, top, right, bottom = scrollable_bounds(payload, first)
    x = str((left + right) // 2)
    lower = str(top + (bottom - top) * 4 // 5)
    upper = str(top + (bottom - top) // 5)
    catalog.shell("input", "swipe", x, lower, x, upper, "420")
    catalog.expect(("Scroll Area", later), absent=(first,))
    catalog.shell("input", "swipe", x, upper, x, lower, "420")
    catalog.expect(("Scroll Area", first), absent=(later,))
    print("PASS: Scroll Area reveals later tags with a touch swipe and returns to the top", flush=True)


def verify_scroll_fade(catalog):
    catalog.open("scroll-fade")
    title, first, later = "scroll-fade", "Item 1", "Item 9"
    payload = catalog.expect((title, first), absent=(later,))
    left, top, right, bottom = scrollable_bounds(payload, first)
    x = str((left + right) // 2)
    inset = (bottom - top) // 6
    high, low = str(top + inset), str(bottom - inset)
    catalog.shell("input", "swipe", x, low, x, high, "390")
    catalog.expect((title, later), absent=(first,))
    catalog.shell("input", "swipe", x, high, x, low, "390")
    catalog.expect((title, first))
    print("PASS: Scroll Fade inner viewport reveals later rows and returns to Item 1", flush=True)


def verify_message_scroller(catalog):
    empty = "Morning, shadcn!"
    reply_start = "The important part: it only auto-scrolls while the reader is already at the bottom."
    reply_end = "their position is preserved."

    def expect_complete_reply():
        for _ in range(5):
            payload = catalog.expect(("Messages",), absent=(empty,))
            paragraphs = (node.get("text", "") for node in ET.fromstring(payload).iter("node"))
            if any(text.startswith(reply_start) and text.endswith(reply_end) for text in paragraphs):
                return
        raise AssertionError("Message Scroller did not expose the completed reply in the transcript")

    catalog.open("message-scroller")
    catalog.expect((empty, "Send", "Reset conversation"))
    catalog.tap("Send")
    expect_complete_reply()
    catalog.tap("Reset conversation")
    catalog.expect((empty, "Send"))
    tab_to(catalog, "Send", clickable=True)
    catalog.shell("input", "keyevent", "KEYCODE_ENTER")
    expect_complete_reply()
    print("PASS: Message Scroller sends by touch or Enter and resets the transcript", flush=True)


def verify_table_scroll(catalog):
    catalog.open("table")
    initial = catalog.expect(("Table", "Invoice", "INV001"), absent=("Amount", "$250.00"))
    viewport = next(
        node for node in ET.fromstring(initial).iter("node")
        if node.get("class") == "android.widget.HorizontalScrollView" and node.get("scrollable") == "true"
        and any(child.get("text") == "Invoice" for child in node.iter("node"))
    )
    left, _, right, _ = map(int, re.findall(r"\d+", viewport.attrib["bounds"]))
    inset = (right - left) // 8
    start, end = str(right - inset), str(left + inset)
    _, y = catalog.point("INV001", initial)
    catalog.shell("input", "swipe", start, str(y), end, str(y), "360")
    catalog.expect(("Table", "Amount", "$250.00"))
    catalog.shell("input", "swipe", end, str(y), start, str(y), "360")
    catalog.expect(("Table", "Invoice", "INV001"), absent=("Amount", "$250.00"))
    print("PASS: Table horizontal touch scrolling reveals Amount and returns to Invoice", flush=True)


def verify_table_keyboard(catalog):
    catalog.open("table")
    catalog.expect(("Table", "Invoice"), absent=("Amount",))
    tab_to(catalog, "Invoice")
    for _ in range(8):
        catalog.shell("input", "keyevent", "KEYCODE_DPAD_RIGHT")
        root = ET.fromstring(catalog.hierarchy())
        viewport = next(
            (node for node in root.iter("node") if node.get("focused") == "true"
             and node.get("class") == "android.widget.HorizontalScrollView"),
            None,
        )
        assert viewport is not None, "The Table viewport lost focus during horizontal key scrolling"
        if any(node.get("text") == "Amount" for node in viewport.iter("node")):
            break
    else:
        raise AssertionError("Right did not reveal the Amount column")
    catalog.shell("input", "keyevent", "KEYCODE_MOVE_END")
    catalog.expect(("Amount", "$250.00"))
    catalog.shell("input", "keyevent", "KEYCODE_MOVE_HOME")
    catalog.expect(("Table", "Invoice"), absent=("Amount",))
    print("PASS: Table receives Tab, scrolls with Right, and supports End and Home", flush=True)


def verify_chart(catalog):
    title = "Bar Chart - Interactive"
    desktop, mobile = "Desktop", "Mobile"

    def expect_series(active):
        payload = catalog.expect((title, desktop, mobile))
        for series in (desktop, mobile):
            assert checked_nodes(payload, series) == [series == active], f"Unexpected {series} series selection"

    catalog.open("chart")
    expect_series(desktop)
    catalog.tap(mobile)
    expect_series(mobile)
    catalog.tap(desktop)
    expect_series(desktop)

    catalog.shell("am", "force-stop", PACKAGE)
    catalog.open("chart")
    expect_series(desktop)
    tab_to(catalog, mobile, clickable=True)
    catalog.shell("input", "keyevent", "KEYCODE_ENTER")
    expect_series(mobile)
    catalog.shell("input", "keycombination", "KEYCODE_SHIFT_LEFT", "KEYCODE_TAB")
    assert desktop in focused_labels(catalog, clickable=True), "Shift+Tab did not focus Desktop"
    catalog.shell("input", "keyevent", "KEYCODE_SPACE")
    expect_series(desktop)
    print("PASS: Chart switches Mobile and Desktop with touch or hardware keyboard", flush=True)


def verify_resizable(catalog):
    catalog.open("resizable")
    handle = "Resize panels horizontally"
    payload = catalog.expect(("Resizable", "One", handle))
    x, y = catalog.point(handle, payload)
    before = catalog.point("One", payload)[0]
    catalog.shell("input", "swipe", str(x), str(y), str(x + 150), str(y), "380")
    payload = catalog.expect(("Resizable", "One", handle))
    after_drag = catalog.point("One", payload)[0]
    assert after_drag > before + 30, "Dragging the divider did not expand the left pane"
    assert handle in focused_labels(catalog), "The drag did not focus the divider"
    catalog.shell("input", "keyevent", "KEYCODE_DPAD_LEFT")
    assert catalog.point("One")[0] < after_drag, "Left did not reduce the left pane"
    print("PASS: Resizable expands by touch drag and shrinks with Left on its focused divider", flush=True)


def verify_sidebar(catalog):
    catalog.open("sidebar")
    trigger = "Toggle Sidebar"
    navigation = ("Playground", "Models")
    catalog.expect(("Sidebar", trigger), absent=navigation)
    catalog.tap(trigger)
    catalog.expect(navigation)
    catalog.shell("input", "keyevent", "KEYCODE_BACK")
    catalog.expect(("Sidebar", trigger), absent=navigation)

    tab_to(catalog, trigger, clickable=True)
    catalog.shell("input", "keyevent", "KEYCODE_ENTER")
    catalog.expect(navigation)
    catalog.shell("input", "keyevent", "KEYCODE_ESCAPE")
    catalog.expect(("Sidebar", trigger), absent=navigation)
    print("PASS: Sidebar opens with touch or Enter and closes with Android Back or Escape", flush=True)


def verify_command_filter(catalog):
    field = "Type a command or search..."
    catalog.open("command")
    catalog.expect(("Command", field, "Calendar", "Calculator"))
    catalog.tap(field)
    catalog.shell("input", "text", "calc")
    payload = catalog.expect(("calc", "Calculator"), absent=("Calendar", "Search Emoji", "Profile"))
    assert any(node.get("class") == "android.widget.EditText" and node.get("text") == "calc"
               for node in ET.fromstring(payload).iter("node")), "Command did not expose its entered query"
    print("PASS: Command hardware input filters suggestions to the matching item", flush=True)


def verify_combobox(catalog):
    catalog.open("combobox")
    catalog.expect(("Combobox", "Select a framework", "Show suggestions"))
    catalog.tap("Show suggestions")
    expect_popup(catalog, True)
    catalog.shell("input", "text", "Svel")
    catalog.expect(("Svel",))
    catalog.shell("input", "keyevent", "KEYCODE_DPAD_DOWN", "KEYCODE_ENTER")
    expect_popup(catalog, False)
    catalog.expect(("Combobox", "SvelteKit"), absent=("Svel", "Select a framework"))
    print("PASS: Combobox hardware input filters and Down plus Enter select the suggestion", flush=True)


def verify_text_entry(catalog):
    for route, title, placeholder, value in (
        ("input-group", "Input Group", "Search...", "registrysearch"),
        ("textarea", "Textarea", "Type your message here.", "registrymessage"),
    ):
        catalog.open(route)
        catalog.expect((title, placeholder))
        catalog.tap(placeholder)
        catalog.shell("input", "text", value)
        payload = catalog.expect((title, value))
        assert any(
            node.get("class") == "android.widget.EditText" and node.get("text") == value
            for node in ET.fromstring(payload).iter("node")
        ), f"{title} did not expose the text as an editable value"
        print(f"PASS: {title} exposes the entered text as its editable value", flush=True)


def verify_card(catalog):
    placeholder = "m@example.com"
    for keyboard, value in ((False, "cardtouch"), (True, "cardkeys")):
        catalog.shell("am", "force-stop", PACKAGE)
        catalog.open("card")
        catalog.expect(("Card", "Login to your account", placeholder))
        if keyboard:
            tab_to(catalog, placeholder, clickable=True)
        else:
            catalog.tap(placeholder)
        catalog.shell("input", "text", value)
        tree = ET.fromstring(catalog.expect((value,)))
        assert any(
            node.get("class") == "android.widget.EditText" and node.get("text") == value and node.get("focused") == "true"
            for node in tree.iter("node")
        ), "The focused Card email field did not expose the entered value"
    print("PASS: Card email accepts text after touch or Tab focus", flush=True)


def verify_password(catalog):
    catalog.open("input")
    catalog.expect(("Input", "API Key", "sk-..."))
    catalog.tap("sk-...")
    value = "registry42"
    catalog.shell("input", "text", value)
    tree = ET.fromstring(catalog.expect(("Input", "API Key"), absent=("sk-...",)))
    assert any(
        node.get("class") == "android.widget.EditText" and node.get("password") == "true"
        and len(node.get("text", "")) == len(value) and node.get("text") != value
        for node in tree.iter("node")
    ), "Input did not retain the masked password value"
    print("PASS: Input accepts text in the password field and masks it in the accessibility tree", flush=True)


def verify_otp(catalog):
    catalog.open("input-otp")
    catalog.expect(("Input OTP", "123456"))
    catalog.tap("123456")
    catalog.shell("input", "keyevent", "KEYCODE_MOVE_END", "KEYCODE_DEL")
    catalog.expect(("12345",), absent=("123456",))
    catalog.shell("input", "text", "98")
    catalog.expect(("123459",), absent=("1234598",))
    print("PASS: Input OTP supports hardware backspace and caps input at six digits", flush=True)


def verify_toggles(catalog):
    for route, heading, target, controls in (
        ("toggle", "Toggle", "Toggle bookmark", ("Toggle bookmark",)),
        ("toggle-group", "Toggle Group", "Toggle italic", ("Toggle bold", "Toggle italic", "Toggle strikethrough")),
    ):
        catalog.open(route)
        before = catalog.expect((heading, *controls))
        assert all(checked_nodes(before, label) == [False] for label in controls)
        catalog.tap(target)
        after = catalog.expect((heading, *controls))
        assert all(checked_nodes(after, label) == [label == target] for label in controls)
        catalog.tap(target)
        cleared = catalog.expect((heading, *controls))
        assert all(checked_nodes(cleared, label) == [False] for label in controls)
        print(f"PASS: {heading} toggles the targeted control on and off", flush=True)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--device", required=True)
    parser.add_argument("--adb", default="adb")
    parser.add_argument("--output", type=Path)
    cases = (
        "popover", "modals", "menus", "dialog-focus", "tabs", "forms", "disclosures", "select", "text", "card",
        "password", "otp", "toggles", "carousel", "command", "combobox", "calendar", "context-menu", "toast",
        "navigation-menu", "scroll-area", "scroll-fade", "message-scroller", "table", "chart", "resizable", "sidebar", "trace", "questionnaire", "label", "field", "button-group", "breadcrumb",
        "accordion-keys", "collapsible-keys", "trace-keys", "switch-keys", "checkbox-keys", "radio-keys",
        "toggle-keys", "toggle-group-keys",
        "dropdown-menu-keys", "menubar-keys", "button-group-keys", "breadcrumb-keys", "carousel-keys", "calendar-keys",
        "label-keys", "toast-keys", "questionnaire-keys", "context-menu-keys", "tooltip-keys", "hover-card-keys", "table-keys",
    )
    parser.add_argument("--only", choices=cases, nargs="+")
    args = parser.parse_args()
    if args.output:
        args.output.mkdir(parents=True, exist_ok=True)
    catalog = AndroidCatalog(args.adb, args.device)
    checks = {
        "popover": lambda: verify_popover(catalog, args.output),
        "modals": lambda: verify_modals(catalog),
        "menus": lambda: verify_menus(catalog),
        "dialog-focus": lambda: verify_dialog_focus(catalog),
        "tabs": lambda: (verify_tabs(catalog), verify_tabs_keyboard(catalog)),
        "forms": lambda: verify_form_controls(catalog),
        "disclosures": lambda: verify_disclosures(catalog),
        "select": lambda: verify_select(catalog),
        "text": lambda: verify_text_entry(catalog),
        "card": lambda: verify_card(catalog),
        "password": lambda: verify_password(catalog),
        "otp": lambda: verify_otp(catalog),
        "toggles": lambda: verify_toggles(catalog),
        "carousel": lambda: verify_carousel(catalog),
        "command": lambda: verify_command_filter(catalog),
        "combobox": lambda: verify_combobox(catalog),
        "calendar": lambda: verify_calendar(catalog),
        "context-menu": lambda: verify_context_menu(catalog),
        "context-menu-keys": lambda: verify_context_menu_keys(catalog),
        "tooltip-keys": lambda: verify_keyboard_popup(catalog, "tooltip", "Hover"),
        "hover-card-keys": lambda: verify_keyboard_popup(catalog, "hover-card", "Hover Here"),
        "toast": lambda: verify_toast(catalog),
        "navigation-menu": lambda: verify_navigation_menu(catalog),
        "scroll-area": lambda: verify_scroll_area(catalog),
        "scroll-fade": lambda: verify_scroll_fade(catalog),
        "message-scroller": lambda: verify_message_scroller(catalog),
        "table": lambda: verify_table_scroll(catalog),
        "table-keys": lambda: verify_table_keyboard(catalog),
        "chart": lambda: verify_chart(catalog),
        "resizable": lambda: verify_resizable(catalog),
        "sidebar": lambda: verify_sidebar(catalog),
        "trace": lambda: verify_disclosure(catalog, "trace"),
        "questionnaire": lambda: verify_questionnaire(catalog),
        "label": lambda: verify_label(catalog),
        "field": lambda: verify_field(catalog),
        "button-group": lambda: verify_button_group(catalog),
        "breadcrumb": lambda: verify_breadcrumb(catalog),
        "accordion-keys": lambda: verify_disclosure(catalog, "accordion", keyboard=True),
        "collapsible-keys": lambda: verify_disclosure(catalog, "collapsible", keyboard=True),
        "trace-keys": lambda: verify_disclosure(catalog, "trace", keyboard=True),
        "switch-keys": lambda: verify_keyboard_toggle(catalog, "switch", "Switch", "Airplane Mode"),
        "checkbox-keys": lambda: verify_keyboard_toggle(
            catalog, "checkbox", "Checkbox", "Accept terms and conditions", other_checked=(True,),
        ),
        "radio-keys": lambda: verify_radio_keyboard(catalog),
        "toggle-keys": lambda: verify_keyboard_toggle(catalog, "toggle", "Toggle", "Toggle bookmark"),
        "toggle-group-keys": lambda: verify_keyboard_toggle(
            catalog, "toggle-group", "Toggle Group", "Toggle italic", entry="Toggle bold",
        ),
        "dropdown-menu-keys": lambda: verify_keyboard_menu(
            catalog, "dropdown-menu", "Dropdown Menu", "Open", "Profile", "Billing",
        ),
        "menubar-keys": lambda: verify_keyboard_menu(
            catalog, "menubar", "Menubar", "File", "New Tab", "New Window",
        ),
        "button-group-keys": lambda: verify_keyboard_menu(
            catalog, "button-group", "Button Group", "More Options", "Mark as Read", "Archive",
        ),
        "breadcrumb-keys": lambda: verify_keyboard_menu(
            catalog, "breadcrumb", "Breadcrumb", "Toggle menu", "Documentation", "Themes",
        ),
        "carousel-keys": lambda: verify_carousel_keyboard(catalog),
        "calendar-keys": lambda: verify_calendar_keyboard(catalog),
        "label-keys": lambda: verify_keyboard_toggle(catalog, "label", "Label", "Accept terms and conditions"),
        "toast-keys": lambda: verify_toast(catalog, keyboard=True),
        "questionnaire-keys": lambda: verify_questionnaire_keyboard(catalog),
    }
    for name in args.only or checks:
        catalog.shell("am", "force-stop", PACKAGE)
        checks[name]()


if __name__ == "__main__":
    main()
