# Message 复核（2026-11-05）

按固定上游 `8a3536678a528cc012b9e62484290e79d0be410f` 重读 Message MDX、`message.tsx`、demo/avatar/group/header-footer/actions/attachment 六个配方与 `style-nova.css`，并实时对照 [Base UI Message](https://ui.shadcn.com/docs/components/base/message)。来源见 [sources.json](sources.json)。

## 发现与修改

- 官方顺序：demo、Features、Avatar、Group、Header and Footer、Actions、Attachment、Accessibility（含 Label icon-only actions / Status updates 两个 h3）、API Reference。本地六配方与 API 小节已存在，但缺 Features/Accessibility 章节，且额外配方没有 `Kotlin extension:` 前缀。
- 本轮补 `beforeExamples = Features`（五条对齐/头像/头脚/组规则）与 `afterExamples = Accessibility`（presentational 说明 + 两个 level-3 子节与 Kotlin 代码），非官方配方统一加前缀。
- 官方 demo 使用 `base-rhea` + `theme-blue`；本地按项目目标继续 Nova，并用 `Accent.Blue` 复现蓝色消息气泡（实测气泡 RGB 35,70,221），组件未改。

## 浏览器验证

`browser/`：Group（两条消息对齐）、Actions（Copy/Like/Dislike 图标 + Failed to send + Retry）、Attachment（图片 + PDF 卡片）实际画面；四象限（1440/390 × 浅深）已复查，含 Features/Accessibility 章节与 TOC 顺序。

## 自动验证

`tests/`：MessageExamplesTest **7 项通过**（六配方 + 六 API 小节、extension 前缀、Features/Accessibility 导出顺序、默认会话/反应/LiveRegion、头像两侧长文、Group 对齐、Actions 可访问名、附件内容）。构建成功；最终应用 `e1ab1f17e73949ec9320.wasm`。

## 未关闭的差异

- 官网使用 base-rhea（更大圆角/不同气泡与头像细节）与 `theme-blue`；本地用 Nova + Accent.Blue 近似，圆角/间距仍按 Nova。
- Canvas 无 DOM：Message 是展示型布局，原生读屏/ARIA 语义取决于内部内容；完整键盘/读屏与动态主题矩阵未重验。
- 共享页面标签（单 Base UI）、无广告、手机 375 对 390、Kotlin 代码面板；MessageScroller 未在本轮与本页联调。
