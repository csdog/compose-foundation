# Dropdown Menu Web 对齐验收

官方来源固定在 `shadcn-ui/ui@dd41d19e2f80ff6891951f7ced74c2c9a650b065`，Base UI / base-nova / Neutral。18 份上游文件及 SHA-256 见 [sources.json](sources.json)。浏览器矩阵覆盖 13 个官方配方。旧 [r4 记录](../dropdown-menu-r4/README.md) 保留历史证据。

## 当前源码和构建

Nova 默认弹层使用 100ms CSS ease。退出中重新打开时，根菜单和子菜单重启进入过渡并保留内容，重新建立本次打开所需的焦点和定位。显式及混合过渡 API 由当前 JVM 用例覆盖。

- 当前核心 JVM 289 项、目录应用 JVM 258 项、Roborazzi 黄金图校验 8 项和 owning lint 5 项均通过。[测试凭证](../../../.agent-runs/dropdown-menu/implement-20/20261112-font-nova-repair/supervisor-current-30a-passing-handoff.json)
- 当前工作树的 JS/Wasm 开发分发分别含 49 / 45 个文件，Desktop 编译和生成的 Wasm 模块语法检查通过。浏览器保留实际响应字节、响应头、服务器记录和分发文件的哈希对应关系。[构建凭证](../../../.agent-runs/dropdown-menu/implement-20/20261112-font-nova-repair/supervisor-current-30b-browser-handoff.json)

## 浏览器验证

- JS/Wasm × 桌面/窄屏 × 浅色/深色共 104 个配方视图、104 组状态和 1,232 项状态检查。52 组可见 View Code 内容与归档输入核对。另测嵌套导航、真实指针退出中重开、焦点恢复、RTL 碰撞、锚点移动、路由及历史。[浏览器交接](../../../.agent-runs/dropdown-menu/implement-20/20261112-font-nova-repair/supervisor-current-30c-final-audit-input.json)
- 两种运行时均验证实际字母输入、前缀与循环查找、禁用项跳过、普通和头像触发器的单个 Tab 停靠、方向键初始高亮、Escape 恢复、已打开时 Enter/Space 单次关闭及模态和非模态的外部点击差异。头像首次键盘进入从另一个实际控件出发，分别验证 Tab 与 Shift+Tab、可见圆形焦点环和 Enter 激活。[优先交互](../../../.agent-runs/dropdown-menu/implement-20/20261112-font-nova-repair/nova-reversal-current-30c-priority-input-supplement/report.md) · [头像首次进入](../../../.agent-runs/dropdown-menu/implement-20/20261112-font-nova-repair/nova-reversal-current-30c-avatar-first-entry/report.md)

## 已审查的平台差异

当前阿拉伯语和希伯来语菜单行 29px，英语 28px。JS 的匹配段落高度为 21px 和 20px，行上下内边距相同。JS/Wasm 的实际本地化像素均保留字形净空；证据未识别单个字形的实际选用字体。Chromium 的原生 AX 焦点落在 Compose 宿主，逐行高亮、可读标签与状态、键盘操作和恢复由实际页面证据覆盖。[平台差异审查](../../../.agent-runs/dropdown-menu/implement-20/20261112-font-nova-repair/nova-reversal-current-30d/report.md)

## 独立终审

本地 Prime Agent 的 `openai-codex/gpt-6.1-sol` 原生子代理审查通过。终审复算了优先交互补验中的 509 条本地响应，核对当前源码和分发文件，并关闭三处验证缺口。[终审报告](../../../.agent-runs/dropdown-menu/implement-20/20261112-font-nova-repair/nova-reversal-current-30d-priority-final/report.md) · [终审凭证](../../../.agent-runs/dropdown-menu/implement-20/20261112-font-nova-repair/nova-reversal-current-30d-priority-final/terminal-receipt.json) · [完整验收凭证](../../../.agent-runs/dropdown-menu/implement-20/20261112-font-nova-repair/nova-reversal-current-40/30-verify-terminal-receipt.json) · [交接核验](../../../.agent-runs/dropdown-menu/implement-20/20261112-font-nova-repair/nova-reversal-current-40/report.md)
