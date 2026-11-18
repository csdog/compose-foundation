# Select Web 验收

验收范围为官方 Base UI / Nova、七个主配方以及 Kotlin 扩展。固定 shadcn/ui 提交 `968d8cb9dc42ae5d52e8fd356cf84596ec832a26`，对应 Base UI 1.6.0。主配方依次为 Default、Align Item With Trigger、Groups、Scrollable、Disabled、Invalid、RTL。41 份预览代码和 Usage 来自实际编译的 Kotlin 函数。[固定来源](sources.json)。

- Nova 对齐使用触发器值和选项文字的实际边界，覆盖 LTR 与 RTL、碰撞回退和 DPR。滚动增长、两端箭头、方向键、文字搜索和无选中值都使用当前选项状态。
- 默认显隐保留 Nova 的节奏，显式传入的 enter/exit 按调用方配置运行。受控 Select 禁用时，退出动画可以继续绘制，选项随禁用状态立即停止接收焦点和选择。
- Nova 初始焦点可以落在已选的禁用选项。对齐模式没有选择时，首行可以接收焦点并保持禁用。受控调用方拒绝 Tab 关闭后，下一次 Escape 关闭会把焦点还给触发器。
- 非模态弹层仅在实际项目对齐时锁住 Web 页面滚动。触摸触发与普通定位继续遵循非模态行为。固定 Base UI 参考的滚动矩阵在本地浏览器实测。触摸打开的长列表使用完整滚动视口，列表底部的条目可直接点选。
- Vega 默认关闭焦点光环。旧 Vega 焦点图与 idle 图相同，Nova 键盘焦点由行为证据覆盖。更新后的九张参考来自两次字节一致的当前截图，保留原图、哈希及逐图解释。
- Compose 使用自身菜单、选项和焦点语义。required 在 Kotlin API 中提供元数据，HTML 表单验证、原生 DOM 属性及平台读屏属于平台边界。当前 Compose 触发器使用点击释放打开，Base UI 的按下、拖动、释放选中手势未纳入本轮配方验证。

## 验证

- 221 项 JVM 用例通过，覆盖 Select 113 项、Dropdown Menu 41 项、共享弹层 16 项和目录 51 项。八项新增回归均有修复前失败记录。113 项截图校验、九项 lint、41 份配方与 Usage 共 42 份独立代码编译通过。
- JS 与 Wasm 产物分别包含 49 和 45 个文件，Desktop 编译通过。构建与测试使用相同的 15,335 个源文件哈希。十个浏览器批次核对了 629 条实际响应和 26 个应用 Wasm 上下文。
- JS 和 Wasm 各在宽窄视口及浅深主题验证七个主配方，共 56 个配方位置。八组 LTR 文字横向差距为 0–1 CSS px，纵向中心重合。八组阿拉伯语文字的右缘和纵向中心重合。
- 真实触摸点击同一时区条目，旧产物在 JS 和 Wasm 均滚动 48 CSS px，新产物两者均选中条目。八组模态、对齐非模态、默认关闭和触摸回退用例验证真实滚轮、外部点击与关闭后的滚动恢复。
- 验证了碰撞上下两侧的动画中间帧、滚动增长和六次箭头点击。82 份剪贴板代码和两份整页内容逐字节匹配。动态主题和英语、希伯来语、阿拉伯语也已复验。

[当前验收凭证](../../../.agent-runs/select/goal-20261118/acceptance-evidence.json) · [浏览器批次](../../../.agent-runs/select/goal-20261118/review-browser-final/receipt.json) · [JVM、lint 与代码编译](../../../.agent-runs/select/goal-20261118/review-native-receipt.json) · [独立审查](independent-review.md)。
