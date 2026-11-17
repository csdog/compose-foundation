# Progress Web 对齐验收

固定 shadcn/ui 修订 `968d8cb9dc42ae5d52e8fd356cf84596ec832a26` 及其 Base UI 1.6.0 依赖。目录覆盖 Default、Label、Controlled、RTL 四个官方配方。Composition 内保留 With label and value，Kotlin 扩展置于 API Reference 之后。[来源](sources.json)

- Chromium 的 JS/Wasm 提供具名 `progressbar`。可见标签为进度条提供无障碍名称，显式 `max = 100f` 暴露 0–100 数值，通过 `aria-valuetext` 暴露百分比。Chromium 辅助功能树没有重复的视觉百分比。三语切换验证名字、数字显示及进度填充方向。
- Controlled 的滑块通过 End、Home、ArrowRight、鼠标和触屏更新进度。点击展示用进度轨道不改变数值。桌面和手机都录到填充的动画中间帧。验证过程中切换浅深主题，进度位置保持一致。
- 52 项 JVM 测试、八张截图基准和所属 lint 通过。八张更新的旧图匹配两次当前采样。四张旧图的轨道由 8px 改为既有的 4px，四张 Nova 差异限于百分数字形。证据保留旧图、逐像素差异和当前采样。
- JS/Wasm 使用当前工作树产物，覆盖 32 个配方位置、20 项路由检查、配方与整页复制、桌面目录及手机导航。Desktop 验证到编译。

[证据汇总](../../../.agent-runs/progress/goal-20261118/acceptance-evidence.json) · [构建](../../../.agent-runs/progress/goal-20261118/final-build/artifact-handoff.json) · [独立终审](independent-review.md)

## API 与平台边界

Kotlin 默认 `max = 1f`，`getValueLabel` 仍接收归一化比例。未知进度用 `indeterminate = true` 表达，浏览器保留进度条角色并省略数值属性。Base UI 的任意最小值、空值和渲染属性由各自的原生 API 表达。辅助功能取证范围为 Chromium。
