"""Build a standalone test project from the Android Sheet registry item."""

import json
from pathlib import Path
import shutil
import subprocess
import sys


ROOT = Path(__file__).resolve().parents[2]


def prepare(project):
    project.mkdir(parents=True, exist_ok=True)
    for source in Path(__file__).with_name("jetpack-sheet").glob("*.gradle.kts"):
        shutil.copyfile(source, project / source.name)

    registry = project / "registry"
    subprocess.run([sys.executable, "shadcn/registry/build.py", "--output", str(registry)], cwd=ROOT, check=True)
    item = json.loads((registry / "android/sheet.json").read_text())
    for source in item["files"]:
        target = project / source["target"].removeprefix("~/")
        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_text(source["content"])

    android_test = ROOT / "shadcn/shared/src/androidDeviceTest"
    shutil.copyfile(android_test / "AndroidManifest.xml", project / "src/main/AndroidManifest.xml")
    shutil.copytree(android_test / "kotlin", project / "src/androidTest/kotlin", dirs_exist_ok=True)


if __name__ == "__main__":
    prepare(Path(sys.argv[1]).resolve())
