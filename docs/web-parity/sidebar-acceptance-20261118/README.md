# Sidebar Web 验收

2026-11-18。锁定 shadcn/ui 968d8cb9dc42ae5d52e8fd356cf84596ec832a26，核对 Base UI 与 Nova 的官方主示例、受控示例、RTL 预览和 API 指南。保留原有 48 个 Kotlin 扩展。[官方文档](https://ui.shadcn.com/docs/components/base/sidebar) · [固定来源](sources.json)。

- 桌面侧栏从 256px 折叠至 48px，两套运行时的深浅主题都保留了实际中间帧。Rail 采用 16px 全高叠放位置，点击后切换同一 provider，并保留旧有的嵌套和无面板用法，配对时主内容空间不扣除 Rail 宽度。PanelLeft 触发器随方向镜像，子菜单边线位于逻辑起始侧。真实 Tab 导航的焦点环逐像素测得 3px。
- 手机根据视口打开 Sheet。390px 下按 75% 计算。浏览器实测面板填充为 292px，边框占 1px，官方盒宽为 292.5px。显式手机模式沿用 288dp，调用方仍可用 Style 覆盖宽度。英语、阿拉伯语、希伯来语的语言选择器及面板随逻辑方向移动。
- 项目操作在桌面行悬停、键盘焦点进入或菜单打开时绘制，在手机持续可见。默认行为沿用于既有调用方。主示例覆盖团队菜单、项目操作和可展开的 Playground。子菜单提供展开状态提示。
- 139 项 JVM（含共享 Sheet 回归）、9 项所属 lint、4 份独立 Kotlin 片段通过。17 张截图逐张核对旧参考、改动前输出和改动后输出，两次候选图逐字节一致。已有 16 张历史参考差异。两张强制手机图的实际像素在本轮改动前后完全相同。Sheet 焦点接入后又核对了两张 Inset 外层背景和阴影。最终 Roborazzi 复验的 17 张 Sidebar 与 6 张 Sheet 实际图均匹配参考。[逐图记录](golden-references.json)。
- 49 个 JS、45 个 Wasm 产物文件与 Desktop 编译通过。当前 15,355 个工作树源文件在测试、构建和浏览器之间逐一核对。8 个桌面／手机、浅／深、JS／Wasm 场景，12 个语言场景和 4 组路由通过。核对 290 个实际响应体，8 个上下文加载应用 Wasm。三段官方示例在两端共 6 次精确复制，2 次整页复制包含 Usage、结构及 RTL 顺序。
- 触摸打开后共享 Sheet 的焦点所有权。普通打开及从嵌套团队菜单完成选择后，Escape 均可关闭面板。常规打开流程还验证了焦点归还与 Enter 重开。
- 浏览器快捷键在当前 provider 焦点范围内生效。持久化通过调用方注入 onPersistOpen。Rail 使用 Compose 的浏览器光标和独立的 2px 悬停边线。整页目录预览将示例嵌入文档卡片。

[验收凭证](../../../.agent-runs/sidebar/goal-20261118/final-evidence.json) · [当前浏览器批次](../../../.agent-runs/sidebar/goal-20261118/mobile-focus-browser/batch-status.json) · [独立审查](independent-review.md)。
