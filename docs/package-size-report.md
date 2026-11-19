# Compose Foundation 各端包体积

- 测量环境为 Darwin arm64，工作区版本为 `4e2d6b2c（工作区有改动）`。
- macOS 15.6.1 arm64，工作区构建配置为 Gradle 9.8.2、Kotlin 2.4.21、Compose Multiplatform 1.12.2、AGP 9.4.0。Android Release、macOS、iOS 真机和三个 Web 产物为本次重建。iOS App 使用 Xcode 26.2 构建，已同步 Compose 资源，未签名，因此未导出 IPA。Linux DEB 使用项目 Ubuntu 24.04 容器原生构建，架构为 arm64。Android Debug APK 使用已有快照。Windows 虚拟机处于挂起状态，MSI 未测。iOS 模拟器 Framework 首轮链接遇到 JVM 堆内存不足，本次重试针对真机。
- MiB 按 1,048,576 字节换算。文件取实际字节数，目录累加普通文件的逻辑大小并跳过符号链接。
- 测量对象为 Catalog 应用。它包含示例和应用依赖，数值不能当作单独接入组件库的增量。

## 实测汇总

| 产物 | 路径 | 大小 | 最近文件修改时间 |
|---|---|---:|---|
| Android Release APK | `app/androidApp/build/outputs/apk/release/androidApp-release-unsigned.apk` | 14.84 MiB (15,560,639 B) | 2026-11-11 09:42 UTC |
| Android Release AAB | `app/androidApp/build/outputs/bundle/release/androidApp-release.aab` | 14.70 MiB (15,416,487 B) | 2026-11-11 09:42 UTC |
| Android Debug APK | `app/androidApp/build/intermediates/apk/debug/androidApp-debug.apk` | 22.91 MiB (24,023,442 B) | 2026-11-11 04:40 UTC |
| macOS 应用镜像 | `app/desktopApp/build/compose/binaries/main/app/com.toutiaoyan.compose.ui.app` | 150.68 MiB (157,995,496 B) | 2026-11-11 09:53 UTC |
| macOS DMG | `app/desktopApp/build/compose/binaries/main/dmg/com.toutiaoyan.compose.ui-1.0.0.dmg` | 86.75 MiB (90,964,163 B) | 2026-11-11 09:55 UTC |
| Windows MSI | — | 未测得 | — |
| Linux DEB | `app/desktopApp/build/compose/binaries/main/deb/com.toutiaoyan.compose.ui_1.0.0_arm64.deb` | 69.91 MiB (73,300,772 B) | 2026-11-11 09:51 UTC |
| iOS 真机 Release App | `app/iosApp/build/package-size/Release-iphoneos/ShadcnComposeCatalog.app` | 84.11 MiB (88,199,684 B) | 2026-11-11 09:50 UTC |
| iOS IPA | — | 未测得 | — |
| iOS 真机 Release 静态 Framework | `app/shared/build/bin/iosArm64/releaseFramework/Shared.framework` | 213.29 MiB (223,654,827 B) | 2026-11-11 09:49 UTC |
| iOS 模拟器 Release 静态 Framework | — | 未测得 | — |
| Web JS Production | `app/webApp/build/dist/js/productionExecutable` | 52.93 MiB (55,501,000 B) | 2026-11-11 09:53 UTC |
| Web Wasm Production | `app/webApp/build/dist/wasmJs/productionExecutable` | 26.51 MiB (27,793,003 B) | 2026-11-11 09:52 UTC |
| Web Compatibility Production | `app/webApp/build/dist/composeWebCompatibility/productionExecutable` | 68.77 MiB (72,108,794 B) | 2026-11-11 09:53 UTC |

## 体积分布

- Android Release APK 最大的分项是 DEX，占 84.9%，合计 12.60 MiB (13,214,884 B)。
- Android Release AAB 最大的分项是 DEX，占 85.7%，合计 12.60 MiB (13,214,884 B)。
- Android Debug APK 最大的分项是 DEX，占 89.0%，合计 20.40 MiB (21,389,641 B)。
- macOS 应用镜像内置 JRE 占 51.4%，合计 77.38 MiB (81,140,752 B)。
- iOS 真机 Release App 最大的分项是 `ShadcnComposeCatalog`，占 97.0%，合计 81.62 MiB (85,584,312 B)。
- Web JS Production 去掉 source maps 后是 31.78 MiB (33,326,193 B)，逐文件 gzip 估算为 11.18 MiB (11,719,949 B)。
- Web JS Production 的 `658820f72b4617efb380.wasm` 和 `skiko.wasm` 字节相同，各占 8.25 MiB (8,648,883 B)，发布目录计入两份。
- Web Wasm Production 去掉 source maps 后是 25.06 MiB (26,281,204 B)，逐文件 gzip 估算为 8.33 MiB (8,737,684 B)。
- Web Compatibility Production 去掉 source maps 后是 46.18 MiB (48,422,188 B)，逐文件 gzip 估算为 14.51 MiB (15,215,396 B)。
- Web Compatibility Production 的 `658820f72b4617efb380.wasm` 和 `skiko.wasm` 字节相同，各占 8.25 MiB (8,648,883 B)，发布目录计入两份。

## 分项与口径

- APK 和 AAB 按 ZIP 条目压缩后大小分组。AAB 是上传包，设备实际下载量还取决于分包。
- macOS 应用镜像包含 JRE 与依赖。DMG、MSI 和 DEB 是安装包。
- iOS 静态 Framework 是链接输入，Release App 是应用目录，IPA 是压缩后的分发文件。
- Web 逐文件以 gzip level 6 估算，合计排除 source maps。Compatibility 同时部署 JS 和 Wasm，实际首屏流量取决于浏览器选择的路径及请求资源。

### Android Release APK · androidApp-release-unsigned.apk

| 内容 | 字节数 | 逐文件 gzip 估算 |
|---|---:|---:|
| `DEX` | 12.60 MiB (13,214,884 B) | — |
| `Assets` | 1.88 MiB (1,974,857 B) | — |
| `Android 资源` | 0.23 MiB (237,111 B) | — |
| `其他条目` | 0.03 MiB (34,253 B) | — |
| `原生库 x86_64` | 0.01 MiB (10,760 B) | — |
| `原生库 arm64-v8a` | 0.01 MiB (10,096 B) | — |
| `原生库 x86` | 0.01 MiB (9,284 B) | — |
| `原生库 armeabi-v7a` | 0.01 MiB (7,252 B) | — |
| `ZIP 容器开销` | 0.06 MiB (62,142 B) | — |

### Android Release AAB · androidApp-release.aab

| 内容 | 字节数 | 逐文件 gzip 估算 |
|---|---:|---:|
| `DEX` | 12.60 MiB (13,214,884 B) | — |
| `Assets` | 1.86 MiB (1,946,241 B) | — |
| `Android 资源` | 0.13 MiB (132,861 B) | — |
| `其他条目` | 0.06 MiB (58,889 B) | — |
| `原生库 x86` | 0.00 MiB (4,782 B) | — |
| `原生库 x86_64` | 0.00 MiB (4,614 B) | — |
| `原生库 arm64-v8a` | 0.00 MiB (4,386 B) | — |
| `原生库 armeabi-v7a` | 0.00 MiB (4,064 B) | — |
| `ZIP 容器开销` | 0.04 MiB (45,766 B) | — |

### Android Debug APK · androidApp-debug.apk

| 内容 | 字节数 | 逐文件 gzip 估算 |
|---|---:|---:|
| `DEX` | 20.40 MiB (21,389,641 B) | — |
| `Assets` | 1.92 MiB (2,009,143 B) | — |
| `Android 资源` | 0.50 MiB (520,368 B) | — |
| `其他条目` | 0.04 MiB (37,828 B) | — |
| `原生库 arm64-v8a` | 0.01 MiB (10,096 B) | — |
| `ZIP 容器开销` | 0.05 MiB (56,366 B) | — |

### macOS 应用镜像 · com.toutiaoyan.compose.ui.app

| 内容 | 字节数 | 逐文件 gzip 估算 |
|---|---:|---:|
| `Contents/runtime` | 77.38 MiB (81,140,752 B) | — |
| `Contents/app` | 72.16 MiB (75,668,627 B) | — |
| `Contents/Resources` | 0.90 MiB (944,555 B) | — |
| `Contents/MacOS` | 0.19 MiB (203,200 B) | — |
| `Contents/_CodeSignature` | 0.04 MiB (36,761 B) | — |
| `Contents/Info.plist` | 0.00 MiB (1,593 B) | — |
| `Contents/PkgInfo` | 0.00 MiB (8 B) | — |

#### macOS 深入拆分

| 分项 | 大小 | 组成 |
|---|---:|---|
| 内置 Java 21 运行时 | 77.38 MiB (81,140,752 B) | `lib/modules` 51.66 MiB，`libjvm.dylib` 17.36 MiB，其余 8.37 MiB |
| 外置 Skiko arm64 动态库 | 20.47 MiB (21,463,408 B) | `Contents/app/libskiko-macos-arm64.dylib` |
| Skiko runtime JAR | 8.94 MiB (9,369,365 B) | 包含一个 x86_64 动态库，解压后 21.18 MiB |
| 其他应用 JAR 和配置 | 42.76 MiB (44,835,854 B) | 主要是 Catalog 共享代码、Lucide 图标、项目组件和 Compose 依赖 |
| 应用图标、启动器、签名和元数据 | 1.13 MiB (1,186,117 B) | 其中应用图标 0.90 MiB |
| **.app 合计** | **150.68 MiB (157,995,496 B)** | 目录内普通文件的逻辑大小 |
| **DMG** | **86.75 MiB (90,964,163 B)** | 分发文件大小 |

其他 JAR 中最大的几项是 `shared-jvm` 6.26 MiB、`icons-lucide-cmp-jvm` 5.52 MiB、`foundation-desktop` 4.55 MiB、项目 `core-jvm` 4.11 MiB 和 `ui-desktop` 3.57 MiB。

`lipo -archs` 确认启动器、JVM 和外置 Skiko 都是 arm64。Gradle 缓存中的原始 Skiko runtime JAR 含 arm64 和 x86_64 两份动态库。打包中间产物已抽出 arm64，成品里名字带 `macos-arm64` 的 runtime JAR 仍保留 x86_64。仅按字节减去该 JAR 后，目录约为 141.74 MiB。此值尚未通过启动、签名或重打包验证，也没有从 DMG 内部复核文件组成。

### iOS 真机 Release App · ShadcnComposeCatalog.app

| 内容 | 字节数 | 逐文件 gzip 估算 |
|---|---:|---:|
| `ShadcnComposeCatalog` | 81.62 MiB (85,584,312 B) | — |
| `compose-resources` | 2.42 MiB (2,533,666 B) | — |
| `Assets.car` | 0.07 MiB (70,200 B) | — |
| `AppIcon76x76@2x~ipad.png` | 0.01 MiB (5,434 B) | — |
| `AppIcon60x60@2x.png` | 0.00 MiB (4,402 B) | — |
| `Info.plist` | 0.00 MiB (1,662 B) | — |
| `PkgInfo` | 0.00 MiB (8 B) | — |

### iOS 真机 Release 静态 Framework · Shared.framework

| 内容 | 字节数 | 逐文件 gzip 估算 |
|---|---:|---:|
| `Shared` | 213.09 MiB (223,437,344 B) | — |
| `Headers` | 0.21 MiB (216,364 B) | — |
| `Info.plist` | 0.00 MiB (998 B) | — |
| `Modules` | 0.00 MiB (121 B) | — |

### Web JS Production · productionExecutable

| 内容 | 字节数 | 逐文件 gzip 估算 |
|---|---:|---:|
| `Source maps` | 21.15 MiB (22,174,807 B) | — |
| `Wasm` | 16.50 MiB (17,297,766 B) | 6.36 MiB (6,673,598 B) |
| `JavaScript` | 12.87 MiB (13,490,887 B) | 2.99 MiB (3,140,360 B) |
| `其他资源` | 1.31 MiB (1,373,620 B) | 1.29 MiB (1,347,725 B) |
| `字体` | 1.11 MiB (1,163,920 B) | 0.53 MiB (558,266 B) |
| `除 Source maps 外合计` | 31.78 MiB (33,326,193 B) | 11.18 MiB (11,719,949 B) |

### Web Wasm Production · productionExecutable

| 内容 | 字节数 | 逐文件 gzip 估算 |
|---|---:|---:|
| `Wasm` | 22.13 MiB (23,204,021 B) | 6.42 MiB (6,730,018 B) |
| `Source maps` | 1.44 MiB (1,511,799 B) | — |
| `其他资源` | 1.31 MiB (1,373,620 B) | 1.29 MiB (1,347,725 B) |
| `字体` | 1.11 MiB (1,163,920 B) | 0.53 MiB (558,266 B) |
| `JavaScript` | 0.51 MiB (539,643 B) | 0.10 MiB (101,675 B) |
| `除 Source maps 外合计` | 25.06 MiB (26,281,204 B) | 8.33 MiB (8,737,684 B) |

### Web Compatibility Production · productionExecutable

| 内容 | 字节数 | 逐文件 gzip 估算 |
|---|---:|---:|
| `Wasm` | 30.38 MiB (31,852,904 B) | 9.60 MiB (10,066,817 B) |
| `Source maps` | 22.59 MiB (23,686,606 B) | — |
| `JavaScript` | 13.38 MiB (14,031,744 B) | 3.09 MiB (3,242,588 B) |
| `其他资源` | 1.31 MiB (1,373,620 B) | 1.29 MiB (1,347,725 B) |
| `字体` | 1.11 MiB (1,163,920 B) | 0.53 MiB (558,266 B) |
| `除 Source maps 外合计` | 46.18 MiB (48,422,188 B) | 14.51 MiB (15,215,396 B) |

## 复测

在项目根目录先构建需要的产物，再用 Python 3.10 或更新版本运行测量脚本。

```sh
./gradlew :app:androidApp:assembleRelease :app:androidApp:bundleRelease
./gradlew :app:webApp:composeCompatibilityBrowserDistribution :app:desktopApp:packageDmg \
  --no-parallel --max-workers=2 '-Dorg.gradle.jvmargs=-Xmx12g -Dfile.encoding=UTF-8' '-Pkotlin.daemon.jvmargs=-Xmx10g'
./gradlew :app:shared:linkReleaseFrameworkIosArm64 --no-parallel --max-workers=2 \
  '-Dorg.gradle.jvmargs=-Xmx12g -Dfile.encoding=UTF-8' '-Pkotlin.daemon.jvmargs=-Xmx10g'
python3 scripts/measure_package_sizes.py --output "${TMPDIR:-/tmp}/compose-foundation-package-size-report.md"
python3 -m unittest discover -s scripts -p test_measure_package_sizes.py
```

以下命令在 macOS 上复核现有产物的分项字节和架构。生成的新测量报告放在临时目录，便于和本文的实测快照比对。

```sh
mac_products=app/desktopApp/build/compose/binaries/main
mac_app="$mac_products/app/com.toutiaoyan.compose.ui.app"
mac_dmg="$mac_products/dmg/com.toutiaoyan.compose.ui-1.0.0.dmg"

python3 - "$mac_app" "$mac_dmg" <<'PY'
from pathlib import Path
import subprocess
import sys
import tempfile
import zipfile

app, dmg = map(Path, sys.argv[1:])
contents = app / "Contents"
payload = contents / "app"
jre = contents / "runtime" / "Contents" / "Home"
skiko = payload / "libskiko-macos-arm64.dylib"
skiko_jar, = payload.glob("skiko-awt-runtime-macos-arm64-*.jar")

def size(path):
    if path.is_dir():
        return sum(p.stat().st_size for p in path.rglob("*")
                   if p.is_file() and not p.is_symlink())
    return path.stat().st_size

def report(label, count):
    print(f"{label:34} {count:>12,} B  {count / 2**20:>7.2f} MiB")

for label, path in [
    ("App", app), ("DMG", dmg), ("Java runtime", contents / "runtime"),
    ("Java modules", jre / "lib/modules"), ("JVM", jre / "lib/server/libjvm.dylib"),
    ("Skiko dylib", skiko), ("Skiko runtime JAR", skiko_jar),
]:
    report(label, size(path))
report("Other app JARs and config", size(payload) - size(skiko) - size(skiko_jar))
report("Other bundle files", size(app) - size(payload) - size(contents / "runtime"))

for jar in sorted(payload.glob("*.jar"), key=size, reverse=True)[:10]:
    report(jar.name, size(jar))

for binary in [contents / "MacOS/com.toutiaoyan.compose.ui",
               jre / "lib/server/libjvm.dylib", skiko]:
    arch = subprocess.check_output(["lipo", "-archs", binary], text=True).strip()
    print(f"{binary.name}: {arch}")

with zipfile.ZipFile(skiko_jar) as archive, tempfile.TemporaryDirectory() as tmp:
    for entry in archive.infolist():
        if entry.filename.endswith(".dylib"):
            binary = Path(tmp) / Path(entry.filename).name
            binary.write_bytes(archive.read(entry))
            arch = subprocess.check_output(["lipo", "-archs", binary], text=True).strip()
            report(f"{binary.name} ({arch})", entry.compress_size)
            report("Uncompressed dylib", entry.file_size)
PY
```

生成 iOS 真机 Framework 后，在 macOS 上用 Xcode 链接未签名 Release App，并同步 Compose 资源。

```sh
ios_products="$PWD/app/iosApp/build/package-size/Release-iphoneos"
ios_frameworks="$PWD/app/shared/build/bin/iosArm64/releaseFramework"
xcodebuild -project app/iosApp/iosApp.xcodeproj -scheme iosApp -configuration Release \
  -destination 'generic/platform=iOS' -derivedDataPath app/iosApp/build/xcode-size \
  CONFIGURATION_BUILD_DIR="$ios_products" FRAMEWORK_SEARCH_PATHS="$ios_frameworks" \
  OTHER_LDFLAGS='-framework Shared' CODE_SIGNING_ALLOWED=NO OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED=YES build
BUILT_PRODUCTS_DIR="$ios_products" UNLOCALIZED_RESOURCES_FOLDER_PATH=ShadcnComposeCatalog.app \
  ENABLE_USER_SCRIPT_SANDBOXING=NO \
  ./gradlew :app:shared:syncComposeResourcesForIos \
  -Pcompose.ios.resources.platform=iphoneos -Pcompose.ios.resources.archs=arm64
```

在对应的 Windows 或 Linux 主机上可分别运行 `:app:desktopApp:packageMsi`、`:app:desktopApp:packageDeb`。Xcode 生成的真机 `.app` 或导出的 `.ipa` 可通过 `--artifact ios-app=路径` 或 `--artifact ios-ipa=路径` 传入。
自动化验证时可重复传入 `--require`，例如 `--require android-apk --require web-compat`，缺少指定产物时返回非零退出码。
