import gzip
from pathlib import Path
import plistlib
import subprocess
import sys
import tempfile
import unittest
from zipfile import ZIP_DEFLATED, ZipFile

import measure_package_sizes as sizes


class PackageSizeTest(unittest.TestCase):
    def setUp(self):
        temporary = tempfile.TemporaryDirectory()
        self.addCleanup(temporary.cleanup)
        self.root = Path(temporary.name)

    def write(self, name, contents):
        path = self.root / name
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_bytes(contents)
        return path

    def test_android_archive_uses_stored_sizes_and_separates_abis(self):
        for key, prefix, dex, resources in [
            ("android-apk", "", "classes.dex", "resources.arsc"),
            ("android-aab", "base/", "dex/classes.dex", "resources.pb"),
        ]:
            with self.subTest(key=key):
                archive_path = self.root / ("fixture" + (".apk" if key == "android-apk" else ".aab"))
                entries = {
                    prefix + dex: "DEX",
                    prefix + "lib/arm64-v8a/libskiko.so": "原生库 arm64-v8a",
                    prefix + "lib/x86_64/libskiko.so": "原生库 x86_64",
                    prefix + "assets/font.ttf": "Assets",
                    prefix + resources: "Android 资源",
                }
                with ZipFile(archive_path, "w", compression=ZIP_DEFLATED) as archive:
                    for name in entries:
                        archive.writestr(name, b"x" * 4096)
                    archive.writestr(prefix + "assets/", b"")
                total, details, _ = sizes.measure(archive_path, "android")
                with ZipFile(archive_path) as archive:
                    expected = {group: archive.getinfo(name).compress_size for name, group in entries.items()}
                expected["ZIP 容器开销"] = total - sum(expected.values())
                self.assertEqual(total, archive_path.stat().st_size)
                self.assertEqual({group: count for group, count, _ in details}, expected)

    def test_web_gzip_is_per_file_and_excludes_source_maps(self):
        served = [b"<html>", b"let app = true;" * 50, b"wasm" * 100]
        source_map = b"source map" * 100
        for name, data in zip(["index.html", "main.js", "module.wasm", "main.js.map"], served + [source_map]):
            self.write(name, data)
        total, details, _ = sizes.measure(self.root, "web")
        groups = {group: (count, gz) for group, count, gz in details}
        self.assertEqual(total, sum(map(len, served)) + len(source_map))
        self.assertEqual(groups["Source maps"], (len(source_map), None))
        self.assertEqual(groups["除 Source maps 外合计"], (
            sum(map(len, served)), sum(len(gzip.compress(data, compresslevel=6)) for data in served),
        ))

    def test_report_only_marks_identical_wasm_as_duplicate(self):
        self.write("index.html", b"<html>")
        self.write("main.js", b"let app = true;")
        self.write("skiko.wasm", b"runtime")
        self.write("hashed.wasm", b"runtime")
        self.write("other.wasm", b"another")
        markdown, found = sizes.report(self.root, {"web-js": self.root}, "fixture")
        self.assertEqual(found, {"web-js"})
        self.assertIn("`hashed.wasm` 和 `skiko.wasm` 字节相同", markdown)
        self.assertNotIn("`other.wasm` 和 `skiko.wasm` 字节相同", markdown)

    def test_directory_ignores_symlinks(self):
        binary = self.write("Shared", b"library")
        (self.root / "Alias").symlink_to(binary)
        total, details, _ = sizes.measure(self.root, "tree")
        self.assertEqual(total, len(b"library"))
        self.assertEqual(details, [("Shared", total, None)])

    def test_framework_requires_linked_binary(self):
        self.write("Shared.framework/Headers/Shared.h", b"header")
        framework = self.root / "Shared.framework"
        with self.assertRaisesRegex(ValueError, "缺少已链接的二进制"):
            sizes.measure(framework, "framework")
        self.write("Shared.framework/Shared", b"binary")
        self.assertEqual(sizes.measure(framework, "framework")[0], 12)

    def test_ios_app_requires_bundle_executable_and_resources(self):
        info = plistlib.dumps({"CFBundleExecutable": "Catalog"})
        self.write("Catalog.app/Info.plist", info)
        app = self.root / "Catalog.app"
        with self.assertRaisesRegex(ValueError, "缺少可执行文件"):
            sizes.measure(app, "ios-app")
        self.write("Catalog.app/Catalog", b"binary")
        with self.assertRaisesRegex(ValueError, "缺少 Compose 资源"):
            sizes.measure(app, "ios-app")
        self.write("Catalog.app/compose-resources/composeResources/font.ttf", b"font")
        self.assertEqual(sizes.measure(app, "ios-app")[0], len(info) + len(b"binaryfont"))

    def test_required_output_and_explicit_path(self):
        installer = self.write("example.dmg", b"installer")
        existing_report = self.write("report.md", b"previous measurement")
        command = [sys.executable, str(Path(sizes.__file__).resolve()), "--root", str(self.root), "--require", "mac-dmg"]
        missing = subprocess.run(command + ["--output", str(existing_report)], capture_output=True, text=True)
        self.assertEqual(missing.returncode, 1)
        self.assertIn("mac-dmg", missing.stderr)
        self.assertEqual(existing_report.read_bytes(), b"previous measurement")
        measured = subprocess.run(command + ["--artifact", f"mac-dmg={installer}"], capture_output=True, text=True)
        self.assertEqual(measured.returncode, 0, measured.stderr)
        self.assertIn("example.dmg", measured.stdout)
        self.assertIn("(9 B)", measured.stdout)


if __name__ == "__main__":
    unittest.main()
