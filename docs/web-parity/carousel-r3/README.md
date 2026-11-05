# Carousel — 浏览器四象限复核（r3）

- 日期：2026-11-05
- 官方修订：`8a3536678a528cc012b9e62484290e79d0be410f`（ui.shadcn.com Base UI + base-nova）
- 本轮定位：页面/配方已有基础，仅做当前四象限浏览器复核、适用交互状态取证与真实差异修复；不重建页面。
- 构建：slot h / port 8778，Wasm SHA-256 `c0570519f21493ae932505507aeb4d8130a06b13be91215ebede3b58db1710cc`（最终构建后重新截图）。
- 证据：`browser/`（四象限 official/local + `meta.json` + contact sheets）、`browser/sections/`（逐配方官方与本地对照）、`browser/states/`、`tests/`。

## 配方核对

官方 7 个预览：carousel-demo、Sizes、Spacing、Orientation、API、Plugins、RTL；About/Usage/Composition/Options/Events 文案。
本地 7 官方配方 + 1 个 Kotlin extension（With dot indicators），顺序一致。

## 本轮变更

- RTL 配方描述改为官方原文。
- 未改组件。

## 验证

- `CarouselExamplesTest`：7 tests，全部通过。
- 四象限已复查；逐配方对照 6 组已复查。
- 交互：Sizes 下一箭头前进到 2/3/4（`states/sizes-next2.png`）、鼠标拖拽后保持手势结果（`states/sizes-dragged2.png`）、Plugins 自动播放 2 秒后切换（`states/plugins-autoplay2.png`）。

## 剩余差异（保持 [ ]）

- 页面小节顺序：官方 About 在 Installation 之前、Options/Events 与预览交错；本地由 ComponentDetailScreen 统一在 Usage 之后插入 beforeExamples、在全部配方之后插入 afterExamples，因此 About 在 Usage 后、Options/Events 在配方之后。需要共享页面骨架支持 MDX 式交错顺序。
- 页面 description 为 Kotlin 适配（Compose Pager 替代 Embla），平台差异。
- Embla 任意 options/plugins、loop、动态主题、完整辅助技术仍未对齐；点指示器为 Kotlin extension。
- 拖动仅做视觉取证，未断言 Embla 等价惯性参数。

## 共享文件需求

需要页面骨架支持：`app/shared/src/commonMain/kotlin/com/toutiaoyan/compose/ui/catalog/ComponentDetailScreen.kt` 目前固定 preview→Installation→Usage→Composition→beforeExamples→examples→afterExamples；官方 Carousel MDX 的 About 在 Installation 前、Options/Events 与预览交错，无法用现有字段表达。
