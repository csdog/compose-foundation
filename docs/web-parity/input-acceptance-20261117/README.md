# Input Web 对齐验收

验收使用 shadcn/ui 的 Base UI、Nova、Neutral 和 10 px 圆角。官方文档、组件、样式和全部 15 个预览配方固定在修订 `968d8cb9dc42ae5d52e8fd356cf84596ec832a26`。18 份源文件与上次归档的 SHA-256 相同。[来源](sources.json)

- Nova 默认 Input 以 150 ms 过渡背景、前景和边框颜色，外圈随焦点即时绘制。JVM 验证聚焦与失焦的中间色，以及同一个聚焦输入框在主题和半径变化时重绘并保值。Chromium 的八个视图使用可控时钟捕获边框中间色和即时外圈。正常速度的采样确认了外圈，错过了多数中间帧。
- File 配方的单一输入外壳高 32 px，桌面宽 320 px，手机宽 245 px。指针、Enter 和 Space 可驱动原生选择器。四组浏览器检查覆盖选择、取消、重新选择、文件名保留和临时元素清理。
- 21 项核心 JVM 测试、18 项目录 JVM 测试、截图校验及所属源码集 lint 通过。七张过时截图以旧版和新版 Input 在同一工作树分别重渲，两次结果逐字节一致，再更新基准。
- 同一当前工作树生成的 JS 和 Wasm 分别包含 49 和 45 个文件。双视口、双主题共通过 120 个配方视图。另验证真实浏览器文本输入、主题切换后保值、三语菜单选择后的实际占位文本方向。冷启动深链接、刷新及前进后退通过 20 项检查。五组浏览器验证共绑定 384 条实际响应，响应体均与构建产物一致。

[构建](../../../.agent-runs/input/goal-20261117/acceptance-build/artifact-handoff.json) · [JVM 和 lint](../../../.agent-runs/input/goal-20261117/checks) · [基准图对照](../../../.agent-runs/input/goal-20261117/golden-refresh.json) · [配方矩阵](../../../.agent-runs/input/goal-20261117/acceptance-matrix/input-matrix.json) · [文件交互](../../../.agent-runs/input/goal-20261117/acceptance-file/file-selection.json) · [动画帧](../../../.agent-runs/input/goal-20261117/acceptance-motion-clock/motion.json) · [文字输入和语言切换](../../../.agent-runs/input/goal-20261117/acceptance-editing-rtl/editing.json) · [占位文本像素](../../../.agent-runs/input/goal-20261117/acceptance-editing-rtl/rtl-placeholder-pixels.json) · [路由](../../../.agent-runs/input/goal-20261117/acceptance-routing/routing.json)

[独立终审](independent-review.md)

## 平台边界

Canvas 上的语义与可点击标签用于键盘和辅助技术。File 用可聚焦的外壳驱动浏览器原生选择器，文件名记录在语义状态中。JS 和 Wasm 都验证了这一交互和清理。原生 HTML 文件控件暴露的 DOM API、表单提交和校验、系统自动填充及所有辅助技术组合的等价性未做扩展声明。复杂配方使用显式 Kotlin 宽度表达 CSS 的收缩规则。Input Group 目录示例使用 Lucide。官方 Input 指南未提供独立的 API 表。Compose 和 CSS 的透明色插值方式有差异，验收覆盖过渡时长、即时外圈、可见中间帧和终态。严格的非作用域空白检查还会读到已有的 Gradle Windows 脚本改动，按 CRLF 规则运行的工作树检查和 Input 提交检查均通过。
