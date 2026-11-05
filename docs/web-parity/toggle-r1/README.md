# Toggle 复核（2026-11-05）

按固定上游 `8a3536678a528cc012b9e62484290e79d0be410f` 读取 Toggle MDX、`toggle.tsx`、六个官方配方与 `style-nova.css`，并实时对照 [Base UI Toggle](https://ui.shadcn.com/docs/components/base/toggle)。来源见 [sources.json](sources.json)。

## 发现与修改

- 官方顺序：demo（Bookmark 图标+文字、outline/sm）、Outline、With Text、Size、Disabled、RTL、API Reference。本地缺 RTL 配方，示例列表还是 API 式标题（Default variant/Sizes/With text 等）且没有 `Examples` 分组控制。
- 本轮重建示例列表：官方六段按官网标题/顺序（demo 复用现有 Bookmark toggle、Sizes→Size、With text→With Text），新增 RTL（三语工具栏 + 阿拉伯语/希伯来语 Bookmark 标签、RtlPreview + 主题字体）；Outline/Size/RTL 补官方 copy；其余示例统一 `Kotlin extension:` 前缀；`examplesHeading = null`，API 参数表沿用既有 apiProperties。
- 组件未改；Nova 字重/内联图标/间距/选中态/小圆角沿用现有实现。

## 浏览器验证

`browser/`：`demo-light.png`（Bookmark toggle）、`pressed-light.png`（按下选中态）、`size-section-light.png`、`rtl-section-light.png` / `rtl-pressed-light.png`（阿拉伯语 RTL 按下）。四象限 1440/390×浅深在最终构建复查。

## 自动验证

`tests/`：ToggleExamplesTest **5 项通过**（官方顺序/API/前缀、demo 按下切换、Outline 按下、Disabled 不可用、RTL 三语）。最终应用 `879c014d3060edbcf720.wasm`，SHA256 `97fa0dc37ba6ef519ac835a9de585e4f3cf0c3f35ce14a80d232502370a46931`。

## 未关闭的差异

- 非对称图标 padding、hover/pressed 过渡与 `invalid` overload 的完整矩阵未逐帧截图，只保留既有测试与静态四象限；组件 API 未变。
- Canvas 无原生 `aria-pressed`/button 语义与焦点可见性差异；共享页面标签、手机 375 对 390、代码面板差异不变。
