# Sheet Web 验收

2026-11-18。固定 shadcn/ui 提交 `968d8cb9dc42ae5d52e8fd356cf84596ec832a26`，对应 Base UI 1.6.0。核对 Default、Side、No Close Button、RTL 四个官方配方和 Kotlin API。保留原有 Kotlin 扩展。[官方文档](https://ui.shadcn.com/docs/components/base/sheet) · [固定来源](sources.json)。

## 修复与回归

- 触摸打开时先聚焦面板，避免激活文本输入。Web 使用同一触摸事件的 Canvas 恢复键盘派发。Tab 进入第一个编辑框。鼠标、键盘与程序化打开继续采用默认焦点逻辑。
- 修复前 JS 和 Wasm 触摸打开会聚焦 Name，输入 `z` 会修改姓名。第一轮修复中，真实 Tab 因浏览器焦点停在页面主体而未进入面板。最终在同一实验中，输入 `z` 保持原值，Tab 到 Name 后输入 `k` 生效。鼠标打开直接聚焦 Name。受控调用方拒绝关闭后，触摸焦点状态保留到实际关闭。
- Usage 使用官方的非受控示例与相同文案，并补齐可独立编译的导入。官方 Dialog API 链接位于 RTL 后，Kotlin 参数表保留在扩展部分。
- 六张历史参考经过两次一致的当前输出核对。旧遮罩使用约 50% 前景色，当前是源码规定的 10% 黑色。深色面板、标题区及底部操作的布局也依据固定来源复核。旧参考与提交前哈希已保存。

## 验证

- 133 项 JVM 通过，含 37 项 Sheet、64 项相关弹层和 32 项目录用例。37 项截图校验、六张参考和所属 lint 通过。四份官方配方与 Usage 共五份实际 Kotlin 复制代码独立编译。
- 49 个 JS 文件和 45 个 Wasm 文件逐一核对，Desktop 编译通过。当前构建源清单 15,343 个文件。七个浏览器批次验证当前产物，13 个上下文装载应用 Wasm。
- 48 个 JS/Wasm、宽窄视口和浅深主题场景覆盖 Default、四个物理边及 No Close Button。各边面板与单侧边框做了像素核对。无关闭按钮时，点击外部可关闭。
- 英语、阿拉伯语、希伯来语切换后可实际编辑字段。RTL 侧边与逻辑末尾的关闭按钮位置已复核。Tab、Shift+Tab、Escape 和关闭后从触发器重开均有真实键盘证据。1095×687 视口捕捉了入场与退出中间帧。本地手动时钟用例核对 200 毫秒运动。
- 深链接、页面刷新、前进后退和滚动位置恢复均在当前产物复核。两套运行时合计八段官方配方剪贴板内容逐字节匹配，整页复制包含 Usage 与 API 顺序，探针恢复先前剪贴板文本。

## 适用范围

Compose 提供可操作的触发器、标题、编辑控件和弹层焦点。HTML 原生 dialog/inert、DOM 属性和系统读屏未纳入等价声明。触摸输入以浏览器可信事件和真实代理焦点验证，OS 虚拟键盘及原生移动端运行未执行。

[验收凭证](../../../.agent-runs/sheet/goal-20261118/final-evidence.json) · [当前浏览器批次](../../../.agent-runs/sheet/goal-20261118/controlled-final-browser/batch-status.json) · [独立审查](independent-review.md)。
