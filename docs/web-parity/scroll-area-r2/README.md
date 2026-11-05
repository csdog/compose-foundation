# Scroll Area 页面 r2（2026-11-05）

对照官方 [Scroll Area 页面](https://ui.shadcn.com/docs/components/base/scroll-area) 与上游固定版本
`8a3536678a528cc012b9e62484290e79d0be410f`（MDX、3 个示例 TSX、`scroll-area.tsx`、`style-nova.css`）。
来源与哈希见 [sources.json](sources.json)。

## 官方结构

Installation、Usage、Composition（`ScrollArea └── ScrollBar`）、Horizontal、RTL、API Reference
（链接 Base UI）。顶部 Tags demo 使用 `h-96`（预览内高 384），Horizontal 预览内容高 458 但被
288 高的预览裁切，RTL 预览内高 368。

## 本轮改动

- 补 `Composition`、`Usage` 导入代码、Horizontal/RTL 官方说明与 API Reference 链接，移除多余
  Examples 标题并修正 Usage 代码块重复导入。
- 官方三配方保持 Default / Horizontal / RTL 顺序；Default 预览内高 304（`h-96` 总高 384）、
  Horizontal 用 288 高预览复现官方裁切、RTL 保持自动高度。
- Horizontal 图片沿用 r1 归档的三张本地灰度艺术图（官方为 Unsplash 同图），宽度/文件名/语义
  与官方一致。
- Kotlin 专有配方裁剪为四条并统一 `Kotlin extension: ` 前缀：Hover-only scrollbar、Always-visible
  scrollbar、Custom hide delay、Lazy list；其余重复变体不再渲染。
- 组件（`shadcn/core/.../scrollarea`）已有 Nova 轨道/滑块端点、轨道点击、滚轮/键盘、双轴角落与
  Lazy 拖拽，本轮未改组件行为。

## 验证

- 聚焦测试 **16 项通过**（4 种视口/主题）：默认 192×288 框、50 个 Tag、键盘到达末项、Horizontal
  三张 300×400 图片与水平轨道高度、RTL 轨道左端点与三语切换、结构/顺序/扩展前缀；XML 见
  [tests/](tests/)。
- 最终 Wasm 构建 wasm sha256
  `52e9c17c2bd84f125bfefce884246b92aacd162fd2646a931fcefc243c3368c0`。
- 四象限（1440/390 × 浅/深）：[browser/](browser/)。
- 实际浏览器状态：[browser/states/](browser/states/)：Tags 滚动后滑块移动；Horizontal 滚轮横向
  滚动到后两张照片；RTL 阿拉伯语列表滚动且滑块在左侧。

## 剩余差异（均已解释）

1. **Horizontal 预览裁切**：官方在 288 高预览内裁切 458 高的 ScrollArea；本地以
   `previewHeight = 288` 复现，垂直居中方式可能比官方多露出顶部少量像素。
2. **图片来源**：官方 Unsplash 网络图；本地使用 r1 归档的同款灰度图片资源（离线、稳定）。
3. **扩展配方**：官方仅有三个预览；本地保留四条 `Kotlin extension:` API 演示（hover/always/
   hide delay/lazy），已与官方配方分离。
4. **平台差异**：Canvas 无原生滚动条 DOM；滚动条为 Compose 绘制，指针/键盘/滚轮交互已实测。
