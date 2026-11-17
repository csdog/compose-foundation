# Popover Web 对齐验收

固定 shadcn/ui 修订 `968d8cb9dc42ae5d52e8fd356cf84596ec832a26` 和该修订使用的 Base UI 1.6.0。目录覆盖 Default、Basic、Align、With Form、RTL 五个官方示例，以及 Non-modal、Placement、Uncontrolled 三个 Kotlin 扩展。[来源](sources.json)

- Nova 弹层使用 100ms 淡入淡出和 95% 缩放。入场从碰撞计算所得的一侧偏移。默认居中、离触发器 4dp，碰撞保留 5dp 边距。Chromium 的 JS/Wasm 录到正常放置及短视口翻转的动画中间帧。
- 官方示例允许外部点击触发目标控件并关闭弹层。慢速再次点击原触发器会关闭。键盘打开后进入 Width，触屏打开后焦点落在面板。Escape 恢复触发器，Shift+Tab 可回到触发器并保留面板。Tab 遍历表单后离开并关闭。
- 48 项聚焦 JVM 测试、六张截图基准和所属 lint 通过。五张基准与未改动源码的两次采样逐字节一致，更新内容包括既有的触发器尺寸、轮廓与表单初始焦点。验收证据保留旧图、差异区域和实际采样。
- 当前 JS/Wasm 产物覆盖 64 个示例位置、20 项路由操作、桌面及手机交互，以及阿拉伯语、希伯来语和英语的打开状态。Desktop 验证到编译。

[证据汇总](../../../.agent-runs/popover/goal-20261118/acceptance-evidence.json) · [构建](../../../.agent-runs/popover/goal-20261118/acceptance-build/artifact-handoff.json) · [独立终审](independent-review.md)

## API 与平台边界

Kotlin 的 `modal` 默认值沿用 `true`。五个官方示例显式选择 `false`，允许外部指针交互，并保留弹层内的键盘导航。Compose Canvas 用具名按钮、展开状态、标题和表单输入提供操作语义。浏览器验证覆盖 Chromium，HTML 的 `aria-expanded` 属性和读屏映射取决于平台。
