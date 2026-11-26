"""Verify TalkBack focus, activation, and text editing on an Android emulator."""

import argparse
from pathlib import Path
import re
import shlex
import subprocess
import tempfile
import time
import zipfile

from verify_android_catalog import AndroidCatalog, PACKAGE


PROBE = "com.toutiaoyan.compose.talkbackaudit"
SOURCES = Path(__file__).resolve().parent / "talkback"


def build_probe(sdk, platform, build_tools, output):
    tools = sdk / "build-tools" / build_tools
    android_jar = sdk / "platforms" / platform / "android.jar"
    classes, dex = output / "classes", output / "dex"
    classes.mkdir()
    dex.mkdir()
    unsigned, aligned, signed = (output / name for name in ("unsigned.apk", "aligned.apk", "audit.apk"))
    key = output / "audit.jks"
    password = "local-emulator-audit"

    subprocess.run(["javac", "--release", "11", "-cp", android_jar, "-d", classes,
                    SOURCES / "TalkBackAudit.java"], check=True, timeout=30)
    subprocess.run([tools / "d8", "--lib", android_jar, "--min-api", "28", "--output", dex,
                    *classes.rglob("*.class")], check=True, timeout=30)
    subprocess.run([tools / "aapt2", "link", "-I", android_jar, "--manifest",
                    SOURCES / "AndroidManifest.xml", "-o", unsigned], check=True, timeout=30)
    with zipfile.ZipFile(unsigned, "a", compression=zipfile.ZIP_DEFLATED) as apk:
        apk.write(dex / "classes.dex", "classes.dex")
    subprocess.run([tools / "zipalign", "-f", "4", unsigned, aligned], check=True, timeout=30)
    subprocess.run(["keytool", "-genkeypair", "-keystore", key, "-storepass", password,
                    "-keypass", password, "-alias", "audit", "-dname", "CN=Local Emulator Audit",
                    "-keyalg", "RSA", "-validity", "7"], check=True, timeout=30)
    subprocess.run([tools / "apksigner", "sign", "--ks", key, "--ks-pass", f"pass:{password}",
                    "--key-pass", f"pass:{password}", "--out", signed, aligned], check=True, timeout=30)
    return signed


def touchscreen_tap(catalog, x, y, duration):
    assert b"OK" in catalog.run("emu", "event", "mouse", x, y, "0", "1")
    try:
        time.sleep(duration)
    finally:
        assert b"OK" in catalog.run("emu", "event", "mouse", x, y, "0", "0")


def touchscreen_swipe(catalog, start, end):
    try:
        for step in range(9):
            point = tuple(str(round(a + (b - a) * step / 9)) for a, b in zip(start, end))
            assert b"OK" in catalog.run("emu", "event", "mouse", *point, "0", "1")
            time.sleep(0.013)
    finally:
        assert b"OK" in catalog.run("emu", "event", "mouse", *map(str, end), "0", "0")


def verify(catalog, args):
    if not args.current_screen:
        catalog.shell("am", "force-stop", PACKAGE)
        catalog.open(args.route)
    time.sleep(1.4)  # Let TalkBack finish choosing its initial focus.
    command = ["am", "instrument", "-w", "-r", "-e", "label", args.label]
    if args.role:
        command.extend(("-e", "class", args.role))
    if args.shown_after:
        command.extend(("-e", "shownAfter", args.shown_after))
    if args.hidden_after:
        command.extend(("-e", "hiddenAfter", args.hidden_after))
    if args.disabled_after:
        command.extend(("-e", "disabledAfter", args.disabled_after))
    if args.state_after:
        label, state = args.state_after
        command.extend(("-e", "stateLabel", label, "-e", "stateAfter", state))
        if args.state_unchanged:
            command.extend(("-e", "stateUnchanged", "true"))
    if args.focus_only:
        command.extend(("-e", "focusOnly", "true"))
    if args.adjust_range:
        command.extend(("-e", "adjustRange", "true"))
    if args.long_press:
        command.extend(("-e", "longPress", "true"))
    if args.type_text is not None:
        expected = args.type_text if args.expected_edit_text is None else args.expected_edit_text
        command.extend(("-e", "expectedEditText", expected))
    if args.masked:
        command.extend(("-e", "masked", "true"))
    if args.require_checkable or args.focused_checked is not None:
        command.extend(("-e", "requireCheckable", "true"))
    if args.focused_checked is not None:
        command.extend(("-e", "focusedChecked", args.focused_checked))
    if args.require_clickable:
        command.extend(("-e", "requireClickable", "true"))
    if args.closes_option_list:
        command.extend(("-e", "closesOptionList", "true"))
    if args.exact_focus:
        command.extend(("-e", "exactFocus", "true"))
    if args.position:
        row, column = map(str, args.position)
        command.extend(("-e", "rowIndex", row, "-e", "columnIndex", column))
    command.append(f"{PROBE}/.TalkBackAudit")
    passed = False
    with subprocess.Popen([*catalog.command, "shell", shlex.join(command)],
                          stdout=subprocess.PIPE, text=True) as observer:
        for line in observer.stdout:
            print(line, end="", flush=True)
            if line.strip() == "INSTRUMENTATION_RESULT: status=PASS":
                passed = True
            snapshot = re.fullmatch(
                r"INSTRUMENTATION_STATUS: snapshot=(explored|focused|activated|increased|decreased|edited)\s*", line,
            )
            if snapshot:
                if args.output:
                    catalog.screenshot(args.output / f"{snapshot[1]}.png")
                continue
            if line.strip() == "INSTRUMENTATION_STATUS: edit=replace":
                catalog.shell("input", "keycombination", "KEYCODE_CTRL_LEFT", "KEYCODE_A")
                catalog.shell("input", "text", args.type_text)
                continue
            match = re.fullmatch(r"INSTRUMENTATION_STATUS: (explore|activate|hold|next|adjust)=(\d+) (\d+)(?: (\d+))?\s*", line)
            if not match:
                continue
            action, x, y, end = match.groups()
            if action == "explore":
                touchscreen_tap(catalog, x, y, 0.6)
            elif action == "next":
                touchscreen_swipe(catalog, (int(x), int(y)), (int(end), int(y)))
            elif action == "adjust":
                touchscreen_swipe(catalog, (int(x), int(y)), (int(x), int(end)))
            else:
                touchscreen_tap(catalog, x, y, 0.055)
                time.sleep(0.07)
                touchscreen_tap(catalog, x, y, 0.85 if action == "hold" else 0.055)
        if observer.wait(timeout=5) != 0 or not passed:
            raise AssertionError(f"TalkBack did not verify {args.label!r} on {args.route}")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--sdk", required=True, type=Path)
    parser.add_argument("--device", required=True)
    parser.add_argument("--platform", default="android-37.0")
    parser.add_argument("--build-tools", default="36.0.0")
    parser.add_argument("--route", default="checkbox")
    parser.add_argument("--current-screen", action="store_true", help="Use the catalog example already on screen")
    parser.add_argument("--label", default="Accept terms and conditions")
    parser.add_argument("--role", default="android.widget.CheckBox")
    parser.add_argument("--shown-after")
    parser.add_argument("--hidden-after", help="Label that must disappear after activation")
    parser.add_argument("--disabled-after", help="Label of a control that must be disabled after activation")
    parser.add_argument("--state-after", nargs=2, metavar=("LABEL", "STATE"),
                        help="Accessibility state description that must appear on the named node after activation")
    parser.add_argument("--state-unchanged", action="store_true",
                        help="Require --state-after to match before activation as well")
    action_group = parser.add_mutually_exclusive_group()
    action_group.add_argument("--focus-only", action="store_true", help="Skip activation when checking static content")
    action_group.add_argument("--adjust-range", action="store_true", help="Check range changes after TalkBack swipes up and down")
    action_group.add_argument("--long-press", action="store_true", help="Double tap and hold the second tap with TalkBack")
    action_group.add_argument("--type-text", help="Open the editor with a TalkBack double tap, then replace its value")
    parser.add_argument("--expected-edit-text", help="Expected result when the editor transforms or limits input")
    parser.add_argument("--masked", action="store_true", help="Require an edited password value of the expected length to be masked")
    parser.add_argument("--require-checkable", action="store_true", help="Require the focused node itself to be checkable")
    parser.add_argument("--focused-checked", choices=("true", "false"), help="Expected checked state on arrival at the target")
    parser.add_argument("--require-clickable", action="store_true", help="Require the focused node itself to be clickable")
    parser.add_argument("--closes-option-list", action="store_true",
                        help="Require activation to remove the checkable option from the open list")
    parser.add_argument("--exact-focus", action="store_true", help="Match the label and role on the focused node itself")
    parser.add_argument("--position", nargs=2, type=int, metavar=("ROW", "COLUMN"), help="Expected zero-based position")
    parser.add_argument("--output", type=Path)
    args = parser.parse_args()
    if args.long_press and not args.shown_after:
        parser.error("--long-press requires --shown-after to confirm the result")
    if args.state_unchanged and not args.state_after:
        parser.error("--state-unchanged requires --state-after")
    if args.type_text is None and (args.masked or args.expected_edit_text is not None):
        parser.error("--masked and --expected-edit-text require --type-text")
    if args.type_text is not None and not re.fullmatch(r"[A-Za-z0-9]+", args.type_text):
        parser.error("--type-text accepts alphanumeric fixture text")
    if (args.focus_only or args.adjust_range or args.type_text is not None) and (
        args.shown_after or args.hidden_after or args.disabled_after or args.closes_option_list or args.state_after
    ):
        parser.error("Post-activation checks require the default activation mode")
    if not re.fullmatch(r"emulator-\d+", args.device):
        parser.error("--device must name a dedicated Android emulator")
    if args.output:
        args.output.mkdir(parents=True, exist_ok=True)
    catalog = AndroidCatalog(str(args.sdk / "platform-tools" / "adb"), args.device)
    with tempfile.TemporaryDirectory(prefix="compose-talkback-") as directory:
        apk = build_probe(args.sdk, args.platform, args.build_tools, Path(directory))
        if catalog.run("install", "--no-streaming", "-t", str(apk)).strip().splitlines()[-1] != b"Success":
            raise RuntimeError("The TalkBack observer could not be installed")
        try:
            verify(catalog, args)
        finally:
            catalog.run("uninstall", PROBE)


if __name__ == "__main__":
    main()
