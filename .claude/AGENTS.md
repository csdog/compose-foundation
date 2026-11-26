# AGENTS.md — Compose Foundation

Compose Foundation implements shadcn-style components in Compose Multiplatform and native SwiftUI. Read `docs/shadcn-component-sop.md` before adding a component or changing its API, appearance, motion, or input behavior. Paths in these instructions are relative to the repository root.

## Where to work

| Directory | Responsibility |
| --- | --- |
| `shadcn/shared/` | Published Compose Multiplatform library. Kotlin components live in `src/commonMain`. Android, iOS, desktop, and web integrations use their corresponding source sets. |
| `shadcn/androidApp/` | Android catalog host and Android screenshot, input, and TalkBack validation. Catalog screens and examples are shared with the other Compose hosts. |
| `shadcn/iosApp/` | Standalone Swift Package, SwiftUI audit app, macOS renderer, and platform interaction checks. The package supports iOS 14 and macOS 11. |
| `shadcn/registry/` | Source registry generator for Compose Multiplatform, standalone Jetpack Compose, and SwiftUI. |
| `app/shared/` | Shared Compose catalog, routes, component docs, and executable examples. |
| `app/iosApp/`, `app/desktopApp/`, `app/webApp/` | Compose catalog entry points for iOS, desktop, and web. |
| `tailwind/core/` | Color, spacing, layout, and styling utilities used by the Compose library. |

The Gradle project paths `:shadcn:core` and `:app:androidApp` point to the relocated directories. Keep the published artifacts `com.toutiaoyan.compose:ui`, `com.toutiaoyan.compose:tailwind`, the `ComposeFoundationSwiftUI` Swift product, and existing registry item names and URLs stable. Check existing callers and platform minimums when editing an API. A public signature change for a `Shadcn*` component or a public sealed style variant requires the appropriate version bump.

## Component conventions

- Use Base UI with Nova as the default parity reference. Pin the shadcn commit and read both the component wrapper and its theme CSS. Record approved Figma frame/version references for product-specific overrides.
- Apply shadcn sizing tokens numerically in logical units. A reference value of 16 CSS px maps to 16 dp in Compose and 16 pt in SwiftUI. Normalize screen scale when comparing images.
- Use existing theme and style tokens. Prefer matching `TwColors` values, then the existing OKLCH conversion. Preserve container-specific color roles and live theme switching. Follow neighboring components for variant resolution, `rememberTheme`, and caller style overrides.
- Preserve logical start/end layout, accessibility semantics, and platform input conventions. Keep Material dependencies out of the Compose component library.
- Document composed slots, controlled state, focus, dismissal, and gestures where applicable. Include a working catalog example. `ComponentDetailScreen` already renders the first example in Preview & Code.
- Keep platform integrations in their source sets and use shared logic for behavior common to multiple targets. For standalone Android export, keep `expect` declarations in dedicated files and supply the matching Android actuals.
- Run the owning regression tests. Record and verify Roborazzi baselines after Compose visual changes, then review the image diffs before staging. Put run evidence in `build/shadcn-audit/` and durable findings in the corresponding audit record.

## Commands

Run from the repository root. Use the available Gradle runner skill for agent-initiated Gradle checks.

| Check | Command |
| --- | --- |
| Compose JVM regressions | `./gradlew :shadcn:core:jvmTest` |
| Compose pixel baselines | `./gradlew :shadcn:core:recordRoborazziJvm`, then `./gradlew :shadcn:core:verifyRoborazziJvm` |
| Android catalog APK | `./gradlew :app:androidApp:assembleDebug` |
| SwiftUI package | `swift build --package-path shadcn/iosApp` |
| Source registries | `python3 shadcn/registry/build.py` |

Use `docs/android-registry-audit.md` and `docs/swiftui-parity-audit.md` for current evidence and remaining cases. See `vendor/README.md` for obtaining the upstream source and Git history for earlier decisions.
