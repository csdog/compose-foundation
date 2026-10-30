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
[**component catalog**](docs/components.md) for the full list with keywords, and
[Registry parity](#registry-parity) for how closely each tracks real shadcn/ui.

## Installation

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

Every component in real shadcn/ui's `base/*` registry is implemented here, with a small set of
deliberate, documented exceptions — `native-select`, `direction`, `data-table`, and `toast`
(deprecated upstream in favor of `sonner`, implemented here as `ShadcnToast`). Full reasoning in
[`.claude/AGENTS.md`](.claude/AGENTS.md#registry-parity-status); token/component-level verification
against real source in [`docs/shadcn-parity.md`](docs/shadcn-parity.md).
Ongoing alignment work is logged in [
`docs/shadcn-align-progress.md`](docs/shadcn-align-progress.md).

Web visual alignment: [implementation plan](docs/shadcn-web-plan.md) ·
[current progress and component checklist](docs/shadcn-web-progress.md).

## Project structure

- [`/shadcn/core`](./shadcn/core/src) — the published library (`commonMain` has every `Shadcn*`
  component).
- [`/tailwind/core`](./tailwind/core/src) — `TwColors` / Modifier utilities (
  `com.toutiaoyan.compose:tailwind`).
- [`/core`](./core/src) — shared utilities used across the catalog app's targets.
- [`/app/shared`](./app/shared/src) — the catalog/documentation app's shared UI.
- [`/app/androidApp`](./app/androidApp), [`/app/desktopApp`](./app/desktopApp), [
  `/app/webApp`](./app/webApp) — catalog app platform entry points.
- [`/app/iosApp`](./app/iosApp/iosApp) — the iOS entry point; open in Xcode.

## Development

Requires JDK 21 (matches CI). Android SDK and Xcode only needed for those specific targets.

## Running the apps

- build everything `./gradlew build  `                        
- run the catalog app (desktop) `./gradlew :app:desktopApp:run`            #
- desktop with Compose Hot Reload and navigate to drawer `./gradlew :app:desktopApp:hotRun --autoReload --args="shadcncompose://app/drawer"`  
- library tests + Roborazzi screenshots (JVM)`./gradlew :shadcn:core:jvmTest`           
- library tests, all platforms`./gradlew :shadcn:core:allTests  `        
- code quality (also run in CI)`./gradlew ktlintCheck detekt lint `

Hot Reload uses
the [Compose Hot Reload](https://kotlinlang.org/docs/multiplatform/compose-hot-reload.html)
plugin (`org.jetbrains.compose.hot-reload`). Cursor MCP is wired in `.cursor/mcp.json`
(`:app:desktopApp:hotMcpServer`).

See [`docs/visual-testing.md`](docs/visual-testing.md) for the screenshot-testing workflow
(`recordRoborazziJvm`/`verifyRoborazziJvm`) and [`docs/shadcn-parity.md`](docs/shadcn-parity.md) for
how components are checked against real shadcn/ui source.

### Android Preview 截图测试

使用 AGP 原生测试套件，通过 Layoutlib 在本机渲染，无需模拟器或真机。
在仓库根目录运行：

```bash
# 校验当前 UI 与已提交的基准图是否一致
./gradlew :app:androidApp:testDebugScreenshotTestDefaultTestSuite

# 首次生成或确认外观变化符合预期后更新基准；检查 PNG 差异后提交
./gradlew :app:androidApp:updateDebugScreenshotTestDefaultTestSuite
```

- 测试源码：`app/androidApp/src/screenshotTest/kotlin/`
- 基准图片：`app/androidApp/src/screenshotTestDefaultDebug/reference/`
- 差异报告：`app/androidApp/build/reports/tests/testDebugScreenshotTestDefaultTestSuite/index.html`

当前使用 AGP `9.5.0-rc02`、Gradle `9.6.0` 和截图引擎 `0.0.1-alpha16`，
测试套件仍为实验功能。上述任务名按当前 AGP 实际生成的名称填写，顺序与官方文档示例不同。
覆盖浅色、深色、平板宽度和大字体；交互行为继续由 JVM 测试验证。

详见 [项目截图测试说明](docs/visual-testing.md) 和
[Android 官方：使用测试套件进行截图测试](https://developer.android.com/studio/preview/compose-screenshot-testing-with-testsuites?hl=zh-cn)。

## License

[Apache License 2.0](LICENSE).

The curated emoji reaction set used by `EmojiText`
(`shadcn/core/src/commonMain/.../icons/emoji/`) is derived from
[Twemoji](https://github.com/jdecked/twemoji), licensed under
[CC-BY 4.0](https://creativecommons.org/licenses/by/4.0/). Copyright 2020 Twitter, Inc and other
contributors, graphics licensed under CC-BY 4.0.

---

Learn more
about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
and the source of design truth, [shadcn/ui](https://ui.shadcn.com).
