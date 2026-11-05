# Sheet 复核（2026-11-05）

按固定上游 `8a3536678a528cc012b9e62484290e79d0be410f` 读取 Sheet MDX、`sheet.tsx`、四个官方配方与 `style-nova.css`，并实时对照 [Base UI Sheet](https://ui.shadcn.com/docs/components/base/sheet)。来源见 [sources.json](sources.json)。

## 发现与修改

- 官方顺序：demo（Edit profile 表单）、Composition、Side、No Close Button、RTL、API Reference；本地 `sheetOfficialExamples` 已有四段并按官方顺序前置，但缺 Composition/API Reference，Side/No Close/RTL 无官网说明，页面仍显示 `Examples` 分组且 Compose-only 示例无前缀。
- 本轮补 Composition 树与三个 API 小节（Sheet / SheetContent·Header·Footer / Trigger·Close，含 Base UI Dialog 链接）；`examplesHeading = null`；给 Side（`side` 取值）、No Close Button（`showCloseButton = false`）、RTL（RTL 指南）补官方 copy；原生 27 个 Compose-only 示例统一加 `Kotlin extension:` 前缀，并过滤掉与官方 demo 重复的旧 “Edit profile” 条目。
- 组件未改：Nova 侧边面板、单侧 border、overlay 模糊与三语 RTL 均沿用既有实现。

## 浏览器验证

`browser/`（1440×1000 浅色）：`demo-open-light.png`（右侧 Edit profile 表单 + Save/Close）、`top-side-open-light.png`（顶部 50vh 面板）、`left-side-open-light.png`、`top-side`/`side-section` 画面、`no-close-open-light.png`（无右上角关闭）、`rtl-open-light.png`（RTL 镜像到左侧、阿拉伯语标题/表单/按钮）。四象限 1440/390×浅深在最终构建上复查。

## 自动验证

`tests/`：SheetExamplesTest **32 项通过**（4 视口/主题参数化，含页面顺序/前缀/Composition/API 与四侧边与 RTL 几何断言）。最终应用 `8b31cf6051ad0ed3c7dd.wasm`，SHA256 `af0897ba8dcd1ca0ba6ea8ace92d531d0a7a68b6aa02253393db7e8c3dc60285`。

## 未关闭的差异

- 四侧边中 bottom 侧本轮没有单独截图（top/right/left + demo 已取证）；侧边开启动画中间帧与焦点陷阱/恢复仍依赖既有组件测试，未新增截图。
- Nova 单侧 border/shadow 与 Header/Footer 字重按现有主题实现，未逐像素对比；Canvas 无原生 `role=dialog`/inert 语义；共享页面标签、手机 375 对 390 与代码面板差异不变。
