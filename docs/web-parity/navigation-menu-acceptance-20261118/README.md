# Navigation Menu Web 对齐验收

以 shadcn/ui 修订 `968d8cb9dc42ae5d52e8fd356cf84596ec832a26` 和 Base UI v1.6.0 为基准。目录覆盖 Default、RTL 两个官方配方，以及 Vertical、Delay、Controlled 三个 Kotlin 扩展。[来源](sources.json)

- Nova 默认面板共用一个弹层。官网桌面从 Getting started 切到 Components 时，弹层宽度由 392px 变到 608px。当前 JS 和 Wasm 都在诊断用慢时钟下录到中间宽度与水平位移。JVM 测试验证居中定位、8dp 偏移和弹层打开时的主题色与圆角更新。
- 顶层项目可逐个 Tab。ArrowDown 进入面板，Shift+Tab 回到触发器，最后一个面板链接按 Tab 到达下一个顶层项目。Chromium 验证键盘操作、悬停后页面持有焦点时的 Escape、触屏切换、短视口翻转、外部点击及阿拉伯语、希伯来语和英语的方向。
- 52 项聚焦 JVM 测试、7 张截图基准和所属 lint 通过。当前工作树生成的 JS/Wasm 产物覆盖 40 个示例位置与 20 项路由检查。宽度 390px 验证触屏，768px 和 1024px 验证 Components 双栏。Desktop 验证到编译。

[证据汇总](../../../.agent-runs/navigation-menu/goal-20261118/acceptance-evidence.json) · [构建](../../../.agent-runs/navigation-menu/goal-20261118/final-build/artifact-handoff.json) · [独立终审](independent-review.md)

## API 与平台边界

NavigationMenuLink 保留 Kotlin 回调和现有 `closeOnClick = true` 默认值。目录的 Docs 链接由 URI 处理器打开官网地址，Chromium 验证了目标 URL。Compose Canvas 用有名称且可聚焦的按钮语义提供键盘激活。Canvas 的按钮语义没有 HTML 锚点的原生上下文菜单、修饰键点击及链接角色。调用者指定面板样式或动画时沿用单项弹层路径。
