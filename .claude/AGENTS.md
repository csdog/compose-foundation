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
- Match shadcn's component semantics, including state transitions, token roles and formulas, focus, keyboard behavior, and accessibility. Preserve logical start/end layout and platform input conventions. Keep Material dependencies out of the Compose component library.
- Do not hardcode a color, alpha, size, or platform-specific override solely to match pixels in a shadcn screenshot. Keep the upstream token formula responsive to theme and caller overrides. When equivalent semantics render differently, verify the cause and record the example, measured values, and decision in the alignment plan. Platform pixel regression still compares against the approved baseline for that platform.
- For SwiftUI, track scope, acceptance criteria, and rendering exceptions in [the alignment plan](../docs/swiftui-parity-plan.md). Update [the progress record](../docs/swiftui-parity-progress.md) when implementation, verification, or publication status changes. Link the exact commit, test run, or report that supports the update.
- Document composed slots, controlled state, focus, dismissal, and gestures where applicable. Include a working catalog example. `ComponentDetailScreen` already renders the first example in Preview & Code.
- Keep platform integrations in their source sets and use shared logic for behavior common to multiple targets. For standalone Android export, keep `expect` declarations in dedicated files and supply the matching Android actuals.
- Run the owning regression tests. Record and verify Roborazzi baselines after Compose visual changes, then review the image diffs before staging. Put run evidence in `build/shadcn-audit/` and durable findings in the corresponding audit record.

## Screenshot review and baseline publication

`shadcn/validation/plan.json` defines the capture cases, platform paths, and upstream source revision. [compose-foundation-baselines](https://github.com/csdog/compose-foundation-baselines) stores both the shadcn visual references under `shadcn/` and the platform PNGs used for pixel regression. Match upstream references by component, variant, state, and light or dark theme. Each Actions run resolves one baseline commit for all platforms. Use `baseline.revision` to pin a specific commit, or leave it unset to resolve the baseline repository's `main`.

Use the **Component parity** workflow in `compose-foundation` (`.github/workflows/component-parity.yml`).

- **Daily regression (`update=no`)** captures each case once and compares it against the approved PNG for the same platform. Failed capture jobs upload any generated diff PNGs. Missing native baselines are pending unless `require_baselines` is enabled.
- **Review (`update=review`)** captures each case twice. Download reproducible candidates from `component-parity-candidates-*`. In `component-parity-shadcn-review-*`, open `overview.png` to compare candidate and upstream images side by side. `report.md` links the originals and provenance and marks missing references. The report also records any comparison against the previous native baseline. When the platform index changes, `component-parity-index-preview-*` contains the Markdown patch. Review artifacts are retained for seven days.
- **Publish (`update=publish`)** is manually triggered by the maintainer from the source repository's `main` after review and merge. Select the same component scope. The workflow recaptures the candidates and uses `COMPONENT_BASELINES_WRITE_TOKEN` to commit validated PNGs and updated platform `Index.md` files to the baseline repository. The Actions summary records the new baseline commit. Use the PR and review flow for proposed changes.

Use the Actions UI to pick the branch and component, or dispatch a review with `gh workflow run component-parity.yml --ref "$review_ref" -f update=review -f component=popover`. Set `review_ref` to the PR branch. Omit `component` to run every case currently in the plan. Find the run with `gh run list --workflow component-parity.yml`, then use `gh run watch` and `gh run download` with its run ID.

## Local screenshots and diffs

- **SwiftUI iOS** uses macOS, Xcode, `xcrun swiftc`, and `xcrun simctl`. The capture script builds the audit app and manages a temporary iPhone 17 Pro simulator with the iOS 26.2 runtime. Check the active Xcode and installed runtimes with `xcodebuild -version` and `xcrun simctl list runtimes`. For manual keyboard, focus, and dismissal checks, use the Simulator app and the recipes in `docs/swiftui-parity-audit.md`. From Linux, use the workflow's macOS runner for iOS Simulator coverage.
- **Compose Web** uses Node.js, Playwright, and Chromium. The capture adapter takes the installed Playwright entry point via `PLAYWRIGHT_MODULE`. **Compose Android** uses the catalog APK, an Android emulator, and `adb` with `ANDROID_SERIAL` set to the target device. Follow the matrix setup in the workflow for reproducible runs.

Use `report.py` to capture, compare, and build reports. Install `Pillow==11.3.0` in your Python environment. This example runs a SwiftUI iOS regression on macOS against a baseline checkout.

```sh
mkdir -p build/shadcn-audit
baseline_checkout="$(mktemp -d "$PWD/build/shadcn-audit/baselines.XXXXXX")"
git clone --recurse-submodules https://github.com/csdog/compose-foundation-baselines.git "$baseline_checkout"
audit="$(mktemp -d "$PWD/build/shadcn-audit/ios-popover.XXXXXX")"
python3 shadcn/validation/report.py "$audit" --run --platform swiftui-ios \
  --component popover --baselines "$baseline_checkout"
```

Open `report.md`, `failures.md`, or `overview.png` in the output directory. Native pixel differences are under the platform's `diff/` directory. Add `--update-baselines` with a fresh output directory to capture a local candidate pair. Use `--report-only --shadcn-references "$baseline_checkout"` with the same platform and component to refresh a saved report's upstream references without recapturing. See `docs/shadcn-component-sop.md` for candidate replay and upstream capture commands.

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
