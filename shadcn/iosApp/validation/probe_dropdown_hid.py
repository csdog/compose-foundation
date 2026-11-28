import json
import subprocess
import sys
import time

client = [sys.argv[1], "--companion", "127.0.0.1:10882", "ui"]


def rows():
    output = subprocess.check_output([*client, "describe-all", "--api", "ax"], timeout=28)
    return {item["AXLabel"]: item for item in json.loads(output) if item.get("AXLabel")}


def require(label):
    deadline = time.monotonic() + 90
    found = {}
    while time.monotonic() < deadline:
        try:
            found = rows()
        except (subprocess.CalledProcessError, subprocess.TimeoutExpired) as error:
            print(f"TRACE: simulator accessibility is still starting ({type(error).__name__})", flush=True)
        if label in found:
            return found[label]
        time.sleep(1)
    raise RuntimeError(f"Expected {label!r}, got {list(found)}")


def send(*event):
    subprocess.run([*client, *event], check=True, timeout=15)
    time.sleep(0.55)


def tap(label):
    frame = require(label)["frame"]
    send("tap", str(round(frame["x"] + frame["width"] / 2)), str(round(frame["y"] + frame["height"] / 2)))


def key(value):
    send("key", str(value), "--duration", "0.14")


tap("Open actions")
opened = require("API")
print("TRACE: disabled menu item", opened.get("enabled"), flush=True)
key(41)  # Escape
escaped = "Profile" not in rows()
print("TRACE: Escape closed the menu", escaped, flush=True)

if escaped:
    key(44)  # Space
    reopened = "Profile" in rows()
    print("TRACE: Space reopened on the restored trigger", reopened, flush=True)
    if not reopened:
        tap("Open actions")

require("Settings")
key(81)  # Down
key(81)  # Down
key(40)  # Return
final = rows()
selected = "Settings selected" in final and "Profile" not in final
print("TRACE: Down, Down, Return selected Settings", selected, list(final), flush=True)
if "Profile" in final:
    tap("Settings")
    require("Settings selected")
    print("TRACE: touch control selected Settings", flush=True)
if not (escaped and reopened and selected):
    sys.exit(1)
