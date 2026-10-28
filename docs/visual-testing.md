# Visual regression testing

`:shadcn:core` has a Roborazzi screenshot-test suite under `shadcn/core/src/jvmTest/`, running
Robolectric-less on the JVM/Desktop target (`roborazzi-compose-desktop`) -- no Android
or Robolectric dependency anywhere in this module.

## Why this exists

Live-browser spot checks (toggling dark mode, tabbing to a button to see its focus
ring) are useful but not reproducible or reviewable -- nothing is committed, so a
regression only surfaces if someone happens to look at the same screen again. This
suite makes every component's visual states a committed, diffable artifact.

## What's covered

- **Per-component state matrices** (`shadcn/core/src/jvmTest/kotlin/.../components/*ScreenshotTest.kt`):
  every component's variants/enabled/disabled/checked-selected states, each captured in
  both light and dark, plus a real keyboard-`requestFocus()` capture for interactive
  leaf controls (proves the focus ring actually renders, not just that the modifier
  compiles).
- **Style preset matrix** (`StylePresetMatrixTest.kt`): all 8 `StylePreset` values
  (Vega/Nova/Maia/Lyra/Mira/Luma/Sera/Rhea) side by side in one composite image
  (`style_matrix_ring_swatch_<light|dark>.png`), plus a real focused `Button` per
  preset (`style_matrix_button_focused_<preset>_<light|dark>.png`) -- this is the
  durable proof that picking a style preset actually changes the ring/shape tokens end
  to end (`StylePreset` -> `Theme.light/dark(ring = ...)` -> `Button`
  -> `shadcnFocusRing`), not just in the two presets that happened to get eyeballed live.

Not covered yet: hover state (no reliable hover-simulation API is used here -- only
prop-driven states and real focus/click are exercised), and per-side border-stripping
inside `ToggleGroup`/`ButtonGroup` (see `docs/shadcn-parity.md`).

**A real blind spot, found the hard way:** every golden here composes once, under one
fixed theme, and never recomposes -- so a `Style { }` block that captures a stale
CompositionLocal snapshot (see `.claude/AGENTS.md`'s notes on this anti-pattern) renders
*correctly* in a static test, because the one theme it was composed under happens to be
the one it's frozen at. This exact bug shipped undetected in `InputGroup`/
`Input`'s error state until a live dark-mode toggle in the running catalog app
revealed it. This suite catches wrong colors; it cannot catch colors that are right once
and then never update. Since a screenshot diff structurally can't catch this, the fix
does not replace it. `currentTheme` inside `Style { }` is a compiler error
(`@Composable` getter). The remaining risk is capturing a theme color in
composition and stuffing it into Style -- that compiles and a static screenshot
will not catch a live theme change.

## Running it

```bash
# Record goldens (first run, or after an intentional visual change)
./gradlew :shadcn:core:recordRoborazziJvm

# Verify against committed goldens (what CI runs, via :shadcn:core:jvmTest)
./gradlew :shadcn:core:verifyRoborazziJvm
```

Goldens live in `shadcn/core/src/jvmTest/snapshots/` and are committed to git. On a
mismatch, Roborazzi writes a `*_compare.png` beside the golden with
expected/actual/diff side by side; CI uploads these as the `screenshot-diffs` artifact
on failure.

## Android Preview 截图（Layoutlib）

Android Preview 截图与 JVM 交互测试互补：前者检查 Android 渲染、宽度、主题和字体缩放，
后者继续验证点击、焦点和动画过程。两套基准分开维护，不能互相替代或证明其他平台像素一致。

- 工具链：AGP `9.5.0-rc02`、Gradle `9.6.0`、截图引擎 `0.0.1-alpha16`。
  AGP 9.5 当前仍是预发布版本，截图测试套件也是实验性 API；升级时需重新核对 DSL。
- 测试宿主：`:app:androidApp`，套件名 `screenshotTest`，目标变体 `debug`。
- 用例目录：`app/androidApp/src/screenshotTest/kotlin/`。
- 基准目录：`app/androidApp/src/screenshotTestDefaultDebug/reference/`，生成后需随代码提交。
- 报告目录：`app/androidApp/build/reports/tests/testDebugScreenshotTestDefaultTestSuite/index.html`。
- 每个用例包含 360dp 浅色、360dp 深色、600dp 浅色、360dp / 1.5 倍字体四种配置，
  固定 API 37；使用固定数据，不访问网络、系统时间或登录状态。

在仓库根目录运行：

```bash
# 首次生成或有意修改外观时更新；必须检查 PNG 差异。
./gradlew :app:androidApp:updateDebugScreenshotTestDefaultTestSuite

# 日常验证与 CI：只比较，不更新基准。
./gradlew :app:androidApp:testDebugScreenshotTestDefaultTestSuite
```

以上直接使用 AGP 原生任务。当前 AGP `9.5.0-rc02` 生成的任务名顺序与官方文档示例不同，
请以当前版本实际生成的任务为准。验证任务只比较图片，不更新基准。需要 JDK 17+、Android SDK platform 37 和 Build Tools 36.0.0；
无需模拟器或真机。基准更新与验证应使用一致的渲染环境。

官方说明：[使用 AGP 测试套件进行 Compose 预览截图测试](https://developer.android.com/studio/preview/compose-screenshot-testing-with-testsuites)。

首批 Android 用例：`ButtonShapesPreview` 和 `FormStatesPreview`，覆盖按钮变体、禁用状态、
圆形/非对称 shape、输入框与复选框。JVM 的按压中间态截图仍由 Roborazzi 负责。
