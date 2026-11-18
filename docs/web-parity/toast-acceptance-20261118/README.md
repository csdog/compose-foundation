# Toast Web 验收

2026-11-18。锁定 shadcn/ui `968d8cb9dc42ae5d52e8fd356cf84596ec832a26` 及其 Base UI 1.6.0 依赖 `b34551d644f2e58ebf8fc1050d949f6654ceca6c`。15 个固定来源。Default、Types 和 Promise 是官网三种预览，页面补齐 Installation 中的根级 Toaster、Usage、Action 与 API Reference。Position、Duration 和 Close button 是 Kotlin 扩展。[官方文档](https://ui.shadcn.com/docs/components/base/toast) · [固定来源](sources.json)。

- 默认 Nova 通知在窗口底部保留 16px 间距。层叠停驻时后两张卡片各露出 12px，前几层按 100%、90%、80% 缩放。扩展按各卡片实测高度和间距放置，新增与移除时保留前后层的过渡帧。右滑超过 40dp 关闭当前卡片，同时保留其他卡片和各自身份。
- 悬停、键盘焦点及手指按住时暂停计时。验证了未发生位移的触摸释放，以及划走后在视口外进行可信触摸，随后通知继续计时。Promise 在同一条通知上由加载进入成功。退出中的旧卡片保留绘制并释放控件输入。
- 有通知时，F6 将焦点移入通知区，Tab 进入当前卡片，浏览器测得焦点环向外延伸 3px。Escape、Close 和当前 Undo 完成后将焦点交给相邻消息。Nova 的内容渐变与位移统一为 500ms，官方内容透明度采用独立的 250ms 过渡。Compose 使用 Canvas 和浏览器辅助语义树。Kotlin 管理器、原有自定义过渡及 Vega 路径继续保留。
- 108 项 JVM 含 Toast、Button 和共享目录回归，9 项所属 lint 与 9 份独立 Kotlin 源码通过。两张历史 Vega 参考均在改动前失败。逐张核对了旧参考和三次实际输出，新实际图在实现前后字节一致，并通过最终 Roborazzi 复验。[逐图记录](golden-references.json)。
- 15,373 个当前工作树文件逐一核对。49 个 JS、45 个 Wasm 产物和 Desktop 编译通过。8 个设备与主题组合、2 个键盘上下文、18 次逐字节片段复制、2 次整页复制均通过。核对 266 条实际浏览器响应，6 个上下文加载应用 Wasm。手机 Wasm 深色组从当前产物的 Promise 深链接重跑，初次滚动位置保留在验收凭证中。

[当前验收凭证](../../../.agent-runs/toast/goal-20261118/final-evidence.json) · [官方浏览器对照](../../../.agent-runs/toast/goal-20261118/official-browser-complete/official-toast.json) · [独立审查](independent-review.md)。
