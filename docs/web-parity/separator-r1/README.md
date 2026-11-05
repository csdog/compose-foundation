# Separator 复核（2026-11-05）

按固定上游 `8a3536678a528cc012b9e62484290e79d0be410f` 重读 Separator MDX、`separator.tsx`、五个官方配方与 `style-nova.css`，并实时对照 [Base UI Separator](https://ui.shadcn.com/docs/components/base/separator)。来源见 [sources.json](sources.json)。

## 发现与修改

- 官方顺序：demo、Vertical、Menu、List、RTL、API Reference；本地 `separatorOfficialExamples` 已是这五段且 `examplesHeading = null`，Vertical/Menu/List 的说明与官网一致，RTL 缺官网引导句。
- 本轮给 RTL 段补 `descriptionMarkdown`（RTL 配置指南链接 + 工具栏三语说明），并把页面里 Compose 专有的 “Compose layout and semantics” 指南标题改为 `Kotlin extension: Compose layout and semantics`；组件与五段预览未改。
- Vertical 的 `orientation`、Menu 的竖直分隔、List 的水平分隔以及 RTL 的 Arabic/Hebrew 文案均沿用既有实现（r1/r2 已核对）。

## 浏览器验证

`browser/`：`menu-section-light.png`（菜单内竖直分隔）、`rtl-section-light.png`（阿拉伯语 RTL 工具栏与正文）；四象限 1440/390×浅深在最终构建上复查，官方主题像素校验通过。

## 自动验证

`tests/`：SeparatorPageTest **12 项通过**（4 视口×浅深，含 RTL 文案、20px 行框与 767/768 断点）＋ SeparatorExamplesTest **20 项通过**（5 配方×4 视口/主题）。最终应用 `8b31cf6051ad0ed3c7dd.wasm`，SHA256 `af0897ba8dcd1ca0ba6ea8ace92d531d0a7a68b6aa02253393db7e8c3dc60285`。

## 未关闭的差异

- Canvas 不提供原生 HTML `role="separator"` 语义，`decorative` 为 Compose 近似；共享页面标签/手机宽度/代码面板差异不变。
- 767/768 的 CSS 断点桥接已由测试覆盖，但真机/浏览器缩放跨断点、菜单内 separator 的指针/键盘聚焦矩阵本轮未重验。
