# Message Scroller 页面与状态复核（2026-11-05，完成）

按固定上游 `8a3536678a528cc012b9e62484290e79d0be410f` 读取 Message Scroller MDX、`message-scroller.tsx`、11 个官方配方与 `style-nova.css`，并实时对照 [Base UI Message Scroller](https://ui.shadcn.com/docs/components/base/message-scroller)。来源与 SHA-256 见 [sources.json](sources.json)。

## 官网结构（本轮对齐目标）

- 指南：What Makes a Great Streaming Chat Experience、MessageScroller、Core Concepts；Performance、Virtualization、Accessibility、Unstyled。
- Core Concepts 的 11 个配方顺序：Anchoring Turns、Group Chat、Keeping Context Visible、Following the Live Edge、Opening Saved Threads、Avoiding a Flash on Reload、Loading Earlier Messages、Animating New Messages、Jumping to Messages、Tracking the Reader's Position、Reading Scroll State；首个预览是 `message-scroller-demo`（New Chat 面板）。
- 页 description 按官网原文。

## 本轮修改

- 页面 description 改为官网原文；`examplesHeading = null`；示例改为官方顺序：demo（New Chat）、Anchoring Turns、Group Chat、Keeping Context Visible、Following the Live Edge、Opening Saved Threads、Avoiding a Flash on Reload、Loading Earlier Messages、Animating New Messages、Jumping to Messages、Tracking the Reader's Position、Reading Scroll State，之后才是 Compose-only 示例并统一加 `Kotlin extension:` 前缀。
- 新增 5 个官方配方实现（`MessageScrollerMissingRecipes.kt`）：demo（New Chat 空态 + 脚本化流式回复 + autoScroll）、Anchoring Turns（User/Assistant 锚点切换、重置、空态）、Group Chat（Grace/Mary/Rocky + Marker 作为 turn 锚点、两步推进）、Following the Live Edge（autoScroll 流式跟随、脚本化多轮）、Animating New Messages（Fade/Pop/Slide 预设、transform+opacity 入场）。Keeping Context Visible / Opening Saved Threads / Loading Earlier Messages / Jumping to Messages / Tracking the Reader's Position / Reading Scroll State 复用既有实现并按官方顺序接线。
- Avoiding a Flash on Reload 在官网是无预览的说明节；页面壳只能渲染 recipe（标题+预览+代码），因此用一段说明文字 + 无代码的提示卡承担该节，文案取官网要点。
- 补 8 段官方指南：What Makes a Great Streaming Chat Experience（含 15 条要点与否决句）、MessageScroller、Core Concepts 作为前置章节；Performance、Virtualization、Accessibility、Unstyled 作为后置章节。原 “Provider and standalone usage” 保留为 `Kotlin extension:` 章节。
- 复用并公开了 `MessageScrollerRecipes.kt` 的 frame/transcript helper（private→internal），未改滚动组件的运行时代码。

## 浏览器验证

`browser/`（1440×1000，浅色；深色见四象限）：

- `demo-closed-light.png`：New Chat 空态、输入区与预置 prompt；`demo-streaming-light.png` / `demo-followed-light.png`：发送后流式回复逐字增长并自动跟随到最新文本。
- `anchoring-section-light.png`、`anchoring-user-anchor-light.png`、`anchoring-assistant-anchor-light.png`：User 锚点时新 prompt 停在顶部附近、回复在其下；切到 Assistant 后回复成为锚点；之前一条消息保留 64dp peek。
- `group-chat-initial-light.png`、`group-chat-marker-light.png`、`group-chat-rocky-light.png`：先添加 “Rocky has joined the chat” Marker（作为锚点滚动到顶部），再发送 Rocky 回复。
- `streaming-initial/following-light.png`：Following the Live Edge 的流式跟随（发送后视图跟到最新回复）。
- `animation-initial/sent-light.png`：Fade 预设下发送新用户消息并流式回复，选择器在 footer。
- 四象限（1440/390 × 浅深，最终构建）已逐张复查：hero demo、TOC 顺序、指南章节、扩展前缀。手机端卡片按 375 clientWidth 收窄，与官网 390 的共享宽度差异一致。

## 自动验证

`tests/`：MessageScrollerExamplesTest **4 项通过**（页面顺序 + 指南顺序 + 前缀 + 官网 description + 原有 Provider 状态回归）。构建 `:app:webApp:wasmJsBrowserDevelopmentExecutableDistribution` 成功；最终应用 `ef04de09cbf8457ebdc6.wasm`，SHA256 `9b8f50bf1288b50c1c7cbc59d22f28dacbcb0a18b94a84583bc4662197c38ca8`。

## 未关闭的差异

- 官方把 What Makes… / MessageScroller 放在 Installation 之前，页面壳只在 Composition 之后渲染 beforeExamples，所以它们出现在 Composition 之后。
- 流式/动画用本地协程模拟（无 AI SDK / useChat）；内容为官网脚本摘要，不是逐字全文；`MessageAnimated` 的 iMessage 弹簧参数只按预设近似（Fade/Pop/Slide）。
- 浏览器“滚动离开后释放跟随”未能在 Canvas 内用滚轮复现（滚轮事件落到页面而不是内部 viewport）；该状态由 `releasedReaderStaysPutAndExplicitButtonResumesFollowing` JVM 测试覆盖，未用截图证明。
- rhea + theme-blue 的 demo 外观用 Nova 近似；官网圆角/配色/卡片内距不同。
- Canvas 无 DOM 的 `role=log`、`aria-live`、`aria-busy`、`data-scrollable`/`data-pending-scroll` 属性；Virtualization 的 TanStack 方案与 Unstyled 包无 Compose 对应物，以文案说明。
- 真实触摸/键盘滚动、可见性订阅的长时间矩阵、任意大 transcript 性能未重验。
