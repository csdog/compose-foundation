# Shimmer Web 验收

2026-11-18。固定 shadcn/ui 提交 968d8cb9dc42ae5d52e8fd356cf84596ec832a26，核对官方 Rhea 示例、Shimmer CSS 工具和 Kotlin API。目录保留 Reverse 示例与四个 Kotlin 扩展。[官方文档](https://ui.shadcn.com/docs/utils/shimmer) · [固定来源](sources.json)。

## 修复与回归

- JS 和 Wasm 现在都跟随 prefers-reduced-motion。初始启用减少动态效果以及运行中切换时，Style 文字与 Modifier 遮罩会恢复静态渲染。关闭偏好后两种渲染方式都重新产生动画。改动前，两种运行时在相同设置下都继续绘制动画中间帧。
- Rhea Marker 使用 8dp 间距、16dp 图标和 14sp / 20sp 文本。装饰性 Spinner 不再单独出现在语义树。Replay 使用 Rhea Outline Small，实测 28px 高、1px 按压位移与 3px 键盘焦点环。该 Rhea Outline 样式也用于 Combobox。
- 预览以当前字体的零字形宽度换算 CSS 的 3ch + 40px。官方四组视口与主题实测每侧 67.846px，Compose 约 67px。Replay 盒宽为 73px，官方为 70.921875px。Compose 保留 Dp 接口、字体栅格化和画刷渲染方式。百分比及其他 CSS 长度表达式由 Kotlin 调用方换算。

## 验证

- 295 项 JVM、9 项所属 lint、15 份独立 Kotlin 片段编译通过。Shimmer 与 Marker 的截图检查通过。Shimmer 浅色参考的 644 个历史像素差异集中在字形，已从三份一致的实际图复核后更新。Vega Button 与 Combobox 的 22 张截图仍存在历史参考差异。用原有 Button 源码复跑，前后实际图逐字节一致。
- 49 个 JS 文件、45 个 Wasm 文件及 Desktop 编译通过。15,347 个当前工作树文件在测试、构建与浏览器验证间逐一匹配。四个浏览器批次校验 190 个实际响应，8 个上下文装载应用 Wasm。
- 1440×1000 和 390×844、浅色与深色、两套运行时合计 80 个预览场景。核对 136 组逐帧 RGB 证据、RTL 文案、两列和堆叠布局、响应式禁用、一次播放与真实 Replay 交互。浏览器实测包括鼠标悬停与按压、可信键盘事件和对应焦点像素。
- 桌面与手机分别核对深链接、刷新、前进、后退、240px 滚动恢复和现场主题切换。在 JS 和 Wasm 中逐字节比对 28 段剪贴板文本，2 次 Copy Page 均含 Usage、全部示例和正确的章节顺序。

[验收凭证](../../../.agent-runs/shimmer/goal-20261118/final-evidence.json) · [当前浏览器批次](../../../.agent-runs/shimmer/goal-20261118/review-final-browser/batch-status.json) · [Vega 对照](../../../.agent-runs/shimmer/goal-20261118/review-vega-regression.json) · [独立审查](independent-review.md)。
