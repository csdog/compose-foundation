# scroll-fade 页面 r2（2026-11-05）

对照官方 [scroll-fade 工具页](https://ui.shadcn.com/docs/utils/scroll-fade)（注意：capture.py 默认
`/docs/components/base/<slug>` 会 404，本页需 `--official-path /docs/utils/scroll-fade`）与上游固定
版本 `8a3536678a528cc012b9e62484290e79d0be410f`。来源与哈希见 [sources.json](sources.json)。

## 官方结构

Installation（npm `shadcn` 包 + 全局 CSS import）、Usage（class 表 + 滚动感知行为说明）、
No Overflow, No Fade、Horizontal Scrolling、Edge Fades、Fade Size、Disabling the Fade、Fallback、
RTL；共 7 个预览。

## 本轮改动

- **修复本地页面在 Web 上完全空白的问题**：`No Overflow, No Fade` 配方原先在无固定高度的容器上
  使用 `verticalScroll`，在文档列中被赋予无限最大高度，Wasm 运行时抛
  `IllegalStateException: Vertically scrollable component was measured with an infinity maximum height`；
  现改为无溢出时只应用遮罩、不挂 `verticalScroll`，JVM 测试相应断言该节点不再有滚动轴语义。
- `isKotlinExtension = true`：页面顶部显示 "Kotlin extension" 标签（不再显示 Base UI 下划线）。
- 移除多余 Examples 标题；为 7 个官方配方补官方说明（No Overflow / Horizontal / Edge Fades /
  Fade Size / Disabling / RTL）；新增 Usage 规则列表（边缘/尺寸/`enabled`/`revealDistance`）与
  Fallback 指南；"Rows (Kotlin)" 改为 `Kotlin extension: Rows`。
- 预览使用 Rhea 源主题（官方该页 `styleName="base-rhea"`），12%/40dp/96dp 深度与 reveal、
  单边/尺寸/禁用、RTL 语言切换均已实现。

## 验证

- 聚焦测试 **32 项通过**（4 种视口/主题）：默认 12 个 tile 与两端滚动、无溢出无滚动范围、横向
  12 个 tag、四边独立滚动/标注、`4`/`24` 深度例、开启/关闭等价、RTL 阿拉伯语与位置保持、
  官方顺序 + Kotlin extension 前缀 + 扩展页标记；XML 见 [tests/](tests/)。
- 最终 Wasm 构建 wasm sha256
  `52e9c17c2bd84f125bfefce884246b92aacd162fd2646a931fcefc243c3368c0`；页面在浏览器可正常加载
  （修复前 `/scroll-fade` 为空白 canvas）。
- 四象限（1440/390 × 浅/深，official 使用 utils 路径）：[browser/](browser/)。
- 实际浏览器状态：[browser/states/](browser/states/)：默认列表滚动到中部（底部 tile 渐隐）、
  横向滚动到后段、RTL 阿拉伯语横向滚动。

## 剩余差异（均已解释）

1. **Installation 内容**：官方为 npm 包与全局 CSS `@import`；本地共享 Installation 块展示
   Gradle 依赖（全站统一平台差异）。
2. **Usage class 表**：官方 13 行 class 表在本地以规则列表 + 说明段落呈现（文档模型无表格），
   位于 Composition 之后而非官方 Usage 位置。
3. **章节顺序**：本地指南 `Kotlin extension: Scroll fade rules` 与 `Kotlin extension: Rows` 是
   Kotlin 专有内容；Fallback 位于 RTL 之后（官方 Fallback 在 RTL 之前），ToC 因此多两项。
4. **预览样式**：官方该页预览 `styleName="base-rhea"`，本地预览用 Rhea 源主题与 Nova 目录外壳，
   圆角/间距按 rhea token 近似。
5. **平台差异**：CSS `animation-timeline: scroll(...)` 的浏览器原生行为改为由 `ScrollState`
   驱动遮罩深度；无滚动监听，RTL 起止边由 `LocalLayoutDirection` 决定。
