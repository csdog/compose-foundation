# compose-foundation

[![CI](https://github.com/ronjunevaldoz/shadcn-compose/actions/workflows/ci.yml/badge.svg)](https://github.com/ronjunevaldoz/shadcn-compose/actions/workflows/ci.yml)
[![Kotlin](https://img.shields.io/badge/kotlin-2.4.0-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.11.1-blue.svg)](https://www.jetbrains.com/lp/compose-multiplatform/)
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

Published to Maven Central.

```toml
# gradle/libs.versions.toml
[versions]
ty-compose-ui = "0.2.4"

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
- `./scripts/check_style_block_theme_reads.sh`

Hot Reload uses
the [Compose Hot Reload](https://kotlinlang.org/docs/multiplatform/compose-hot-reload.html)
plugin (`org.jetbrains.compose.hot-reload`). Cursor MCP is wired in `.cursor/mcp.json`
(`:app:desktopApp:hotMcpServer`).

See [`docs/visual-testing.md`](docs/visual-testing.md) for the screenshot-testing workflow
(`recordRoborazziJvm`/`verifyRoborazziJvm`) and [`docs/shadcn-parity.md`](docs/shadcn-parity.md) for
how components are checked against real shadcn/ui source.

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
