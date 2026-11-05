# Attachment — 最终页面复核（r9）

- 日期：2026-11-05
- 官方修订：`8a3536678a528cc012b9e62484290e79d0be410f`（ui.shadcn.com Base UI，页面使用 base-rhea 风格）
- 本轮定位：Attachment 是最后一个未完成页面；此前 r2–r8 已有六配方、Nova 布局、Group 吸附/键盘/渐隐/约束、整卡焦点与 Features/Accessibility 指南。本轮按当前官网复核配方/文案，关闭可修复的残余缺口，并补齐四象限、逐配方与交互浏览器证据。
- 构建：slot h / port 8778，最终 Wasm SHA-256 `02d85afc1cd47cd566e3c5d532c5ff93b15a82698e079e7405a8d7e21735fa49`（最终构建后重新截图）。
- 证据：`browser/`（四象限 official/local + `meta.json` + contact sheets）、`browser/sections/`（5 个官方配方的官方/本地对照）、`browser/states/`（hover/离开/焦点/弹窗/滚动/扫光帧）、`tests/`。

## 配方与页面结构核对

官方预览（base-rhea）：attachment-demo（顶部）、Image、States、Sizes、Group、Trigger；正文小节：Installation、Usage、Composition、Features（6 条 bullet）、Image、States、Sizes、Group、Trigger、Accessibility（Label icon-only actions / Label the trigger / Keyboard scrolling / Meaning beyond color）、API Reference（Attachment、AttachmentMedia、AttachmentContent、AttachmentTitle、AttachmentDescription、AttachmentActions、AttachmentAction、AttachmentTrigger、AttachmentGroup）。

本地 6 配方顺序与标题一致：Default（demo）、Image、States、Sizes、Group、Trigger；Features 与四个 Accessibility 小节、9 个官方 API 小节齐全。本地 API Reference 额外含 `AttachmentScope`（Kotlin-only 组合作用域），TOC 中显示为额外小节。

## 本轮修复/关闭的缺口

1. **FileSearch SVG**：r7 已用官网 Trigger 媒体中的真实路径（放大镜在纸张左侧）构建 `AttachmentPreviewIcon.kt` 的本地 ImageVector，而非旧版 `Lucide.FileSearch`。本轮在浏览器 Trigger 配方中放大对照，图标轮廓与官网一致（`browser/sections/local-trigger-desktop-light.png`）。
2. **Trigger 配方文案**：由 “Make the whole card open a preview…” 改为官网语义：“Add an `AttachmentTrigger` to make the whole card open a link or dialog. It fills the card behind the actions, so the actions stay independently clickable.”（导出 code 未受影响）。
3. **overlay hover（此前记为开放）**：实测鼠标进入覆盖整卡的 Trigger（overlay）区域时，外层卡片背景变为 `muted/50`（白 255 → 247，整卡 bbox 变化），移出后恢复；非交互卡片（Default demo 的 uploading/file 行）hover 无背景变化，与官网 `has-[>a,>button]:hover:bg-muted/50` 语义一致。证据：`states/trigger-rest.png`、`states/trigger-hover.png`、`states/demo-rest.png`、`states/demo-hover.png`。
4. **color transition**：官网 motion 实测 `transition-colors 0.15s cubic-bezier(0.4,0,0.2,1)`（r8 `official-motion.json`）；本地 hover 背景使用 150ms `FastOutSlowInEasing` tween（`Attachment.kt`），进入/离开均生效，离开后完全回到 `card` 背景。受截图延迟限制无法截取 150ms 中间帧；中断反转时序由 core 定向测试覆盖（`AttachmentInteractionTest`）。状态属性变化时的逐属性颜色动画（border/text）在示例中无可变状态，未单独验证。
5. **native a11y**：Canvas 无 DOM，但 Compose Web 语义层进入 Chrome Accessibility 树。实测 AX tree 暴露真实 `button` 角色与名称：`Preview research-summary.pdf`、`Copy link`、`Remove research-summary.pdf`、`Remove sales-dashboard.pdf`、`Remove briefing-notes.pdf` 等（`Accessibility.getFullAXTree`）。未覆盖真实屏幕阅读器播报/分组角色等完整矩阵。

## 验证

- 四象限：1440×1000 / 390×844 × light/dark，official/local 各 4 张，contact sheets 已查看（浅深色主题均校验）。
- 逐配方对照 5 组（Image、States、Sizes、Group、Trigger）已查看；FileSearch 图标、错误态图标/文案、Shimmer、尺寸层级一致。
- 交互状态（`browser/states/`）：
  - Trigger：rest → hover（背景 muted/50）→ 离开恢复 → 点击打开预览 Dialog → Escape 关闭 → 点击 Copy 动作只聚焦卡片（focus-within 1px ring），不触发 Dialog（动作独立可点）。
  - Image：卡片 hover（含 Trigger overlay）状态取证。
  - Group：pointer 停留在 Group 上，横向 wheel 240 后卡片位移并出现边缘渐隐（diff 限定在 group 行内）。
  - States：uploading/processing 标题 shimmer 连续动画（两帧不同，diff 位于标题区域）。
  - Default demo：非交互卡片 hover 无背景变化（仅 uploading 标题 shimmer）。
- 测试（全部通过，XML 在 `tests/`）：
  - `AttachmentExamplesTest` 9/9
  - `AttachmentGroupConstraintsTest` 4/4、`AttachmentGroupFadeTest` 2/2、`AttachmentGroupSnapTest` 9/9、`AttachmentInteractionTest` 5/5、`AttachmentLayoutTest` 9/9、`AttachmentScopeTest` 5/5、`AttachmentScreenshotTest` 6/6（jvmTest 下仅捕获不比对 golden）、`AttachmentStateTest` 5/5
  - 合计 54 tests，0 failures / 0 errors。
- 标题排版：r7 已实测官网 14px / font-weight 500 / line-height 17.5px，本地 `AttachmentTitle`/`AttachmentContent` 保持 BodyMedium + Medium（行高 fontSize×1.25）；本轮未改组件，浏览器观感一致，未再改字重。

## 剩余差异（已解释）

- **demo 后的官方介绍段落缺失**：官网在首屏预览之后有 “The `Attachment` component displays a file or image attachment, its media, name, and metadata, with optional actions and upload state. Use it for files and images in chat composers, message threads, and upload lists.” 本地 `ComponentDoc`/`ComponentDetailScreen` 没有“预览后介绍段落”字段（只有 `description` 标题下副标题与 beforeExamples/afterExamples），无法在正确位置渲染；属共享页面骨架缺口，需要 `ComponentDoc.introMarkdown` + `ComponentDetailScreen` 支持。
- **风格上下文**：官网 Attachment 预览使用 `base-rhea`（previewClassName 带 `theme-blue bg-surface`），本地 catalog 固定 Nova 预设与本地预览底色；为既有平台/风格上下文差异（自 r1 记录），不伪造 Rhea。
- **完整 transition-colors / 中断时序**：hover 背景 150ms 已验证；浏览器无法捕获中间帧，逐属性（border/text）状态切换动画未实现（示例无动态状态）。中断反转由 core 定向测试覆盖，未做浏览器帧级取证。
- **原生辅助技术矩阵**：可用 AX button 角色/名称已取证；真实屏幕阅读器播报、Group 分组角色、完整 Tab 顺序未验证（Canvas 语义层限制）。
- **旧 Vega 六张像素基准**：r5/r7 记录保留，未重录/未重跑；本轮用原生浏览器截图作为对照，不以 Roborazzi golden 作像素结论。
- 其他历史条件延续：复杂自定义槽位/极端约束、字形抗锯齿与亚像素观感（全站 P3）、本地预览底色与官网 `bg-surface` 的色差。

## 共享文件需求

1. `app/shared/src/commonMain/kotlin/com/toutiaoyan/compose/ui/catalog/docs/ComponentDoc.kt` + `.../ComponentDetailScreen.kt`：增加“预览之后、Installation 之前”的介绍段落字段，供 Attachment 等页面复刻官网 intro 文案（本批 write scope 未包含这两个文件，故未改）。
2. 官方 Attachment 预览使用 base-rhea；若后续统一 Rhea 预览上下文，需要共享预览容器支持 per-page 风格，而不是伪造颜色。

## 结论

页面 6 配方顺序/标题/主要文案与官方一致；四象限、逐配方、hover/focus/overlay/dialog/group-scroll/shimmer 状态均已浏览器验证；54 项聚焦测试通过。剩余均为已解释的共享骨架/平台/像素级差异，附件页面可进入最终集成检查。
