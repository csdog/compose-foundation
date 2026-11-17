# Navigation Menu 独立终审

**PASS** · 2026-11-18

- 独立复算 14 份固定来源、10 个本次修改文件、构建前后源清单、49 个 JS 文件和 45 个 Wasm 文件的哈希。Desktop 编译及两份 Web 分发产物均由当前工作树重新构建。
- 复核 52 项 JVM、7 张截图基准和所属 lint。复算 72 张浏览器截图的哈希，逐条核对 323 个实际 HTTP 正文及对应分发文件。
- 40 个示例位置、20 项路由操作、10 个交互场景通过。两种运行时均验证悬停 Escape、外部点击、短视口翻转及共享弹层从 392px 到 608px 的中间帧。
- 记录 Canvas 的按钮与 URI 回调语义、调用者自定义弹层路径。浏览器范围为 Chromium。两个独立菜单同时处于打开状态时的 Escape 优先级未覆盖。

[证据清单](../../../.agent-runs/navigation-menu/goal-20261118/acceptance-evidence.json) · [官网交互取证](../../../.agent-runs/navigation-menu/goal-20261118/official-live/official.json)
