# compose-foundation

[![CI](https://github.com/ronjunevaldoz/shadcn-compose/actions/workflows/ci.yml/badge.svg)](https://github.com/ronjunevaldoz/shadcn-compose/actions/workflows/ci.yml)
[![Kotlin](https://img.shields.io/badge/kotlin-2.4.0-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.12.2-blue.svg)](https://www.jetbrains.com/lp/compose-multiplatform/)
![Platforms](https://img.shields.io/badge/platform-Android%20%7C%20iOS%20%7C%20Desktop%20%7C%20Web-blue.svg)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)

A [shadcn/ui](https://ui.shadcn.com)-inspired component library for **Kotlin Multiplatform / Compose
Multiplatform** — Android, iOS, Desktop, and Web from one `commonMain` source set. Token-based
theming and sealed variant systems, built on the experimental **Compose Styles API**. No Material
dependency, no icon-library dependency — every component is drawn from this library's own tokens.

70+ components across primitives, forms, overlays, data display, and AI Elements (chat UI) — see the
[**component catalog**](../archive/compose-foundation/2026-11-19/docs/components.md) for the component/source index, and
[Registry parity](#registry-parity) for how closely each tracks real shadcn/ui.

## Installation

Use the shadcn registry to copy editable sources for
[Compose Multiplatform or Android-only Jetpack Compose](shadcn/registry/README.md)
and [native SwiftUI](shadcn/iosApp/README.md). All three catalogs use the same
component names.

The coordinates below use this checkout's **0.3.0 development version**. A Maven
release is a separate publishing step.

In 0.3.0, `Attachment` content and actions use `AttachmentScope` to pass the root's
size and orientation to its parts. Trailing lambdas keep the same syntax. Change
stored slot types to `@Composable AttachmentScope.() -> Unit`; extract reusable
parts as `AttachmentScope` extension composables. A stored unscoped lambda can be
wrapped in `{ oldContent() }`. The previous JVM entry point remains as a binary bridge.

```toml
# gradle/libs.versions.toml
[versions]
ty-compose-ui = "0.3.0"

[libraries]
ty-compose-ui = { module = "com.toutiaoyan.compose:ui", version.ref = "ty-compose-ui" }
```

```kotlin
// build.gradle.kts
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.ty.compose.ui)
            // TwColors / Modifier utilities arrive transitively via
            // com.toutiaoyan.compose:tailwind (api dependency of :ui).
        }
    }
}
```

Library and catalog modules (`:shadcn:core`, `:tailwind-core`, `:app:shared`) opt in
at the Gradle module level via `-opt-in=…ExperimentalFoundationStyleApi`, so source
files there do not need `@file:OptIn`. Downstream apps that call Style APIs still
need either the same compiler flag or:

```kotlin
@file:OptIn(ExperimentalFoundationStyleApi::class)
```

## Registry parity

The Compose Multiplatform, Jetpack Compose, and SwiftUI registries publish the same
item slugs. See the [component SOP](docs/shadcn-component-sop.md) for the reference
and test workflow, and the [Android](docs/android-registry-audit.md) and
[SwiftUI](docs/swiftui-parity-audit.md) audits for current coverage and open cases.

## Project structure

- [`/shadcn/shared`](./shadcn/shared/src) — the published library (`commonMain` has every `Shadcn*`
  component).
- [`/tailwind/core`](./tailwind/core/src) — `TwColors` / Modifier utilities (
  `com.toutiaoyan.compose:tailwind`).
- [`/core`](./core/src) — shared utilities used across the catalog app's targets.
- [`/app/shared`](./app/shared/src) — the catalog/documentation app's shared UI.
- [`/shadcn/androidApp`](./shadcn/androidApp) — Android catalog host and platform validation.
- [`/shadcn/iosApp`](./shadcn/iosApp) — native SwiftUI package and capture fixtures.
- [`/shadcn/registry`](./shadcn/registry) — source registry generator.
- [`/app/desktopApp`](./app/desktopApp), [`/app/webApp`](./app/webApp), and [`/app/iosApp`](./app/iosApp/iosApp) — the other Compose catalog hosts.

## Development

Requires JDK 21 (matches CI). Android SDK and Xcode only needed for those specific targets.

## Running the apps

- build everything `./gradlew build  `                        
- run the catalog app (desktop) `./gradlew :app:desktopApp:run`            #
- desktop with Compose Hot Reload and navigate to drawer `./gradlew :app:desktopApp:hotRun --autoReload --args="shadcncompose://app/drawer"`  
- library tests (JVM) `./gradlew :shadcn:core:jvmTest`
- pixel comparison (Roborazzi) `./gradlew :shadcn:core:verifyRoborazziJvm`
- library tests, all platforms`./gradlew :shadcn:core:allTests  `        
- code quality (also run in CI)`./gradlew ktlintCheck detekt lint `

Hot Reload uses
the [Compose Hot Reload](https://kotlinlang.org/docs/multiplatform/compose-hot-reload.html)
plugin (`org.jetbrains.compose.hot-reload`). Cursor MCP is wired in `.cursor/mcp.json`
(`:app:desktopApp:hotMcpServer`).

See the [component SOP](docs/shadcn-component-sop.md) for pixel, animation, and gesture checks.

### Android Preview 截图测试

使用 AGP 原生测试套件，通过 Layoutlib 在本机渲染，无需模拟器或真机。
在仓库根目录运行：

```bash
# 校验当前 UI 与已提交的基准图是否一致
./gradlew :app:androidApp:testScreenshotTestDefaultDebugTestSuite

# 首次生成或确认外观变化符合预期后更新基准；检查 PNG 差异后提交
./gradlew :app:androidApp:updateScreenshotTestDefaultDebugTestSuite
```

- 测试源码：`shadcn/androidApp/src/screenshotTest/kotlin/`
- 基准图片：`shadcn/androidApp/src/screenshotTestDefaultDebug/reference/`
- 差异报告：`shadcn/androidApp/build/reports/tests/testScreenshotTestDefaultDebugTestSuite/index.html`

当前使用 AGP `9.4.0`、Gradle `9.8.2` 和截图引擎 `0.0.1-alpha16`，
测试套件仍为实验功能。上述任务名按当前 AGP 实际生成的名称填写，顺序与官方文档示例不同。
覆盖浅色、深色、平板宽度和大字体；交互行为继续由 JVM 测试验证。

详见 [组件开发与验收 SOP](docs/shadcn-component-sop.md)。

## License

[Apache License 2.0](LICENSE).

The curated emoji reaction set used by `EmojiText`
(`shadcn/shared/src/commonMain/.../icons/emoji/`) is derived from
[Twemoji](https://github.com/jdecked/twemoji), licensed under
[CC-BY 4.0](https://creativecommons.org/licenses/by/4.0/). Copyright 2020 Twitter, Inc and other
contributors, graphics licensed under CC-BY 4.0.

---

Learn more
about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
and the source of design truth, [shadcn/ui](https://ui.shadcn.com).
