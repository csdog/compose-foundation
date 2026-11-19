#!/usr/bin/env python3
"""Measure Compose Foundation catalog packages and write a Markdown report."""

import argparse
from collections import Counter
from datetime import datetime, timezone
from filecmp import cmp
from pathlib import Path
import platform
import plistlib
import subprocess
import sys
import zlib
from zipfile import BadZipFile, ZipFile

ROOT = Path(__file__).resolve().parents[1]
ARTIFACTS = {
    "android-apk": ("Android Release APK", "app/androidApp/build/outputs/apk/release/*.apk", "android"),
    "android-aab": ("Android Release AAB", "app/androidApp/build/outputs/bundle/release/*.aab", "android"),
    "android-debug": ("Android Debug APK", "app/androidApp/build/outputs/apk/debug/*.apk", "android"),
    "mac-app": ("macOS 应用镜像", "app/desktopApp/build/compose/binaries/main/app/*.app", "desktop"),
    "mac-dmg": ("macOS DMG", "app/desktopApp/build/compose/binaries/main/dmg/*.dmg", "file"),
    "windows-msi": ("Windows MSI", "app/desktopApp/build/compose/binaries/main/msi/*.msi", "file"),
    "linux-deb": ("Linux DEB", "app/desktopApp/build/compose/binaries/main/deb/*.deb", "file"),
    "ios-app": ("iOS 真机 Release App", "app/iosApp/build/package-size/Release-iphoneos/*.app", "ios-app"),
    "ios-ipa": ("iOS IPA", "app/iosApp/build/package-size/*.ipa", "file"),
    "ios-framework": ("iOS 真机 Release 静态 Framework", "app/shared/build/bin/iosArm64/releaseFramework/Shared.framework", "framework"),
    "ios-simulator": ("iOS 模拟器 Release 静态 Framework", "app/shared/build/bin/iosSimulatorArm64/releaseFramework/Shared.framework", "framework"),
    "web-js": ("Web JS Production", "app/webApp/build/dist/js/productionExecutable", "web"),
    "web-wasm": ("Web Wasm Production", "app/webApp/build/dist/wasmJs/productionExecutable", "web"),
    "web-compat": ("Web Compatibility Production", "app/webApp/build/dist/composeWebCompatibility/productionExecutable", "web"),
}


def size(value):
    return f"{value / 1024**2:.2f} MiB ({value:,} B)"


def files_under(directory):
    return sorted(path for path in directory.rglob("*") if path.is_file() and not path.is_symlink())


def gzip_size(path):
    compressor = zlib.compressobj(level=6, wbits=31)
    with path.open("rb") as stream:
        compressed = sum(len(compressor.compress(chunk)) for chunk in iter(lambda: stream.read(1 << 20), b""))
    return compressed + len(compressor.flush())


def android_breakdown(path):
    groups = Counter()
    with ZipFile(path) as archive:
        for entry in archive.infolist():
            if entry.is_dir():
                continue
            parts = entry.filename.split("/")
            # AAB entries have a module prefix. APK entries start with their category.
            if path.suffix == ".aab" and len(parts) > 1 and parts[1] in {"lib", "dex", "assets", "res", "resources.pb"}:
                parts = parts[1:]
            top = parts[0]
            if top == "lib" and len(parts) > 2:
                group = f"原生库 {parts[1]}"
            elif top == "dex" or top.endswith(".dex"):
                group = "DEX"
            elif top in {"res", "resources.arsc", "resources.pb"}:
                group = "Android 资源"
            elif top == "assets":
                group = "Assets"
            else:
                group = "其他条目"
            groups[group] += entry.compress_size
    return groups


def web_group(path):
    if path.suffix == ".map":
        return "Source maps"
    if path.suffix == ".wasm":
        return "Wasm"
    if path.suffix in {".js", ".mjs"}:
        return "JavaScript"
    if path.suffix in {".ttf", ".otf", ".woff", ".woff2"}:
        return "字体"
    return "其他资源"


def measure(path, kind):
    if kind == "framework" and not (path / path.stem).is_file():
        raise ValueError(f"Framework 缺少已链接的二进制：{path}")
    files = files_under(path) if path.is_dir() else [path]
    if kind == "ios-app":
        with (path / "Info.plist").open("rb") as stream:
            executable = plistlib.load(stream).get("CFBundleExecutable")
        if not executable or not (path / executable).is_file():
            raise ValueError(f"iOS App 缺少可执行文件：{path}")
        if not any(item.is_relative_to(path / "compose-resources") for item in files):
            raise ValueError(f"iOS App 缺少 Compose 资源：{path}")
    total = sum(item.stat().st_size for item in files)
    if not total:
        raise ValueError(f"产物为空：{path}")

    if kind == "android":
        groups = android_breakdown(path)
        details = [(name, count, None) for name, count in groups.most_common()]
        details.append(("ZIP 容器开销", total - groups.total(), None))
    elif kind == "web":
        if not (path / "index.html").is_file() or not any(item.suffix == ".js" for item in files):
            raise ValueError(f"Web 分发目录缺少 HTML 入口或 JS：{path}")
        raw = Counter()
        compressed = Counter()
        for item in files:
            group = web_group(item)
            raw[group] += item.stat().st_size
            if group != "Source maps":
                compressed[group] += gzip_size(item)
        details = [
            (group, count, compressed.get(group))
            for group, count in raw.most_common()
        ]
        details.append(("除 Source maps 外合计", total - raw["Source maps"], compressed.total()))
    elif path.is_dir():
        groups = Counter()
        for item in files:
            parts = item.relative_to(path).parts
            group = "/".join(parts[:2]) if kind == "desktop" else parts[0]
            groups[group] += item.stat().st_size
        details = [(name, count, None) for name, count in groups.most_common(8)]
    else:
        details = []
    updated = datetime.fromtimestamp(max(item.stat().st_mtime for item in files), timezone.utc)
    return total, details, updated.strftime("%Y-%m-%d %H:%M UTC")


def revision(root):
    def git(*args):
        return subprocess.run(["git", "-C", str(root), *args], capture_output=True, text=True, check=True).stdout.strip()

    try:
        return git("rev-parse", "--short", "HEAD") + ("（工作区有改动）" if git("status", "--porcelain") else "")
    except (OSError, subprocess.CalledProcessError):
        return "未知"


def report(root, overrides, build_note):
    rows = []
    sections = []
    found = set()
    observations = []
    for key, (label, pattern, kind) in ARTIFACTS.items():
        paths = [overrides[key]] if key in overrides else sorted(root.glob(pattern))
        if key == "android-debug" and not paths and key not in overrides:
            paths = sorted(root.glob("app/androidApp/build/intermediates/apk/debug/*.apk"))
        for path in paths:
            try:
                if kind in {"desktop", "ios-app", "framework", "web"} and not path.is_dir():
                    raise ValueError("需要产物目录")
                if kind in {"file", "android"} and not path.is_file():
                    raise ValueError("需要包文件")
                total, details, updated = measure(path, kind)
            except (OSError, ValueError, BadZipFile, plistlib.InvalidFileException) as error:
                print(f"{key}: {error}", file=sys.stderr)
                continue
            found.add(key)
            name = path.relative_to(root) if path.is_relative_to(root) else path
            rows.append(f"| {label} | `{name}` | {size(total)} | {updated} |")
            if kind == "android":
                group, count, _ = max(details, key=lambda item: item[1])
                observations.append(f"{label} 最大的分项是 {group}，占 {count / total:.1%}，合计 {size(count)}。")
            if kind == "desktop":
                runtime = next((count for group, count, _ in details if group == "Contents/runtime"), 0)
                if runtime:
                    observations.append(f"{label}内置 JRE 占 {runtime / total:.1%}，合计 {size(runtime)}。")
            if kind == "ios-app":
                group, count, _ = details[0]
                observations.append(f"{label} 最大的分项是 `{group}`，占 {count / total:.1%}，合计 {size(count)}。")
            if kind == "web":
                payload, compressed = details[-1][1:]
                observations.append(
                    f"{label} 去掉 source maps 后是 {size(payload)}，逐文件 gzip 估算为 {size(compressed)}。"
                )
                skiko = path / "skiko.wasm"
                if skiko.is_file():
                    for candidate in sorted(path.glob("*.wasm")):
                        if candidate != skiko and candidate.is_file() and cmp(candidate, skiko, shallow=False):
                            observations.append(
                                f"{label} 的 `{candidate.name}` 和 `skiko.wasm` 字节相同，"
                                f"各占 {size(skiko.stat().st_size)}，发布目录计入两份。"
                            )
            if details:
                sections += [
                    f"### {label} · {path.name}", "",
                    "| 内容 | 字节数 | 逐文件 gzip 估算 |",
                    "|---|---:|---:|",
                    *(f"| `{name}` | {size(count)} | {size(gzip) if gzip is not None else '—'} |"
                      for name, count, gzip in details),
                    "",
                ]
        if key not in found:
            rows.append(f"| {label} | — | 未测得 | — |")

    lines = [
        "# Compose Foundation 各端包体积", "",
        f"- 测量环境为 {platform.system()} {platform.machine()}，工作区版本为 `{revision(root)}`。",
        f"- {build_note}",
        "- MiB 按 1,048,576 字节换算。文件取实际字节数，目录累加普通文件的逻辑大小并跳过符号链接。",
        "- 测量对象为 Catalog 应用。它包含示例和应用依赖，数值不能当作单独接入组件库的增量。",
        "", "## 实测汇总", "",
        "| 产物 | 路径 | 大小 | 最近文件修改时间 |",
        "|---|---|---:|---|",
        *rows, "",
        *(["## 体积分布", "", *(f"- {item}" for item in observations), ""] if observations else []),
        "## 分项与口径", "",
        "- APK 和 AAB 按 ZIP 条目压缩后大小分组。AAB 是上传包，设备实际下载量还取决于分包。",
        "- macOS 应用镜像包含 JRE 与依赖。DMG、MSI 和 DEB 是安装包。",
        "- iOS 静态 Framework 是链接输入，Release App 是应用目录，IPA 是压缩后的分发文件。",
        "- Web 逐文件以 gzip level 6 估算，合计排除 source maps。Compatibility 同时部署 JS 和 Wasm，实际首屏流量取决于浏览器选择的路径及请求资源。",
        "", *sections,
        "## 复测", "",
        "在项目根目录先构建需要的产物，再用 Python 3.10 或更新版本运行测量脚本。", "",
        "```sh",
        "./gradlew :app:androidApp:assembleRelease :app:androidApp:bundleRelease",
        "./gradlew :app:webApp:composeCompatibilityBrowserDistribution :app:desktopApp:packageDmg \\",
        "  --no-parallel --max-workers=2 '-Dorg.gradle.jvmargs=-Xmx12g -Dfile.encoding=UTF-8' '-Pkotlin.daemon.jvmargs=-Xmx10g'",
        "./gradlew :app:shared:linkReleaseFrameworkIosArm64 --no-parallel --max-workers=2 \\",
        "  '-Dorg.gradle.jvmargs=-Xmx12g -Dfile.encoding=UTF-8' '-Pkotlin.daemon.jvmargs=-Xmx10g'",
        "python3 scripts/measure_package_sizes.py --output docs/package-size-report.md",
        "python3 -m unittest discover -s scripts -p test_measure_package_sizes.py",
        "```", "",
        "生成 iOS 真机 Framework 后，在 macOS 上用 Xcode 链接未签名 Release App，并同步 Compose 资源。", "",
        "```sh",
        'ios_products="$PWD/app/iosApp/build/package-size/Release-iphoneos"',
        'ios_frameworks="$PWD/app/shared/build/bin/iosArm64/releaseFramework"',
        "xcodebuild -project app/iosApp/iosApp.xcodeproj -scheme iosApp -configuration Release \\",
        "  -destination 'generic/platform=iOS' -derivedDataPath app/iosApp/build/xcode-size \\",
        '  CONFIGURATION_BUILD_DIR="$ios_products" FRAMEWORK_SEARCH_PATHS="$ios_frameworks" \\',
        "  OTHER_LDFLAGS='-framework Shared' CODE_SIGNING_ALLOWED=NO OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED=YES build",
        'BUILT_PRODUCTS_DIR="$ios_products" UNLOCALIZED_RESOURCES_FOLDER_PATH=ShadcnComposeCatalog.app \\',
        "  ENABLE_USER_SCRIPT_SANDBOXING=NO \\",
        "  ./gradlew :app:shared:syncComposeResourcesForIos \\",
        "  -Pcompose.ios.resources.platform=iphoneos -Pcompose.ios.resources.archs=arm64",
        "```", "",
        "在对应的 Windows 或 Linux 主机上可分别运行 `:app:desktopApp:packageMsi`、`:app:desktopApp:packageDeb`。"
        "Xcode 生成的真机 `.app` 或导出的 `.ipa` 可通过 `--artifact ios-app=路径` 或 `--artifact ios-ipa=路径` 传入。",
        "自动化验证时可重复传入 `--require`，例如 `--require android-apk --require web-compat`，缺少指定产物时返回非零退出码。",
        "",
    ]
    return "\n".join(lines), found


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--root", type=Path, default=ROOT)
    parser.add_argument("--output", type=Path)
    parser.add_argument("--require", choices=ARTIFACTS, action="append", default=[])
    parser.add_argument("--artifact", metavar="KEY=PATH", action="append", default=[])
    parser.add_argument("--build-note", default="读取本地现有构建产物。产物时间用于识别快照，工作区版本不代表产物的构建版本。")
    args = parser.parse_args()
    overrides = {}
    for spec in args.artifact:
        key, _, path = spec.partition("=")
        if key not in ARTIFACTS or not path:
            parser.error(f"无效产物参数 {spec!r}，可用名称为 {', '.join(ARTIFACTS)}")
        overrides[key] = Path(path).resolve()
    markdown, found = report(args.root.resolve(), overrides, args.build_note)
    missing = set(args.require) - found
    if missing:
        print(f"缺少必需产物 {', '.join(sorted(missing))}", file=sys.stderr)
        return 1
    if args.output:
        args.output.write_text(markdown, encoding="utf-8")
    else:
        print(markdown, end="")
    return 0


if __name__ == "__main__":
    sys.exit(main())
