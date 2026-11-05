# Table 复核（2026-11-05）

按固定上游 `8a3536678a528cc012b9e62484290e79d0be410f` 读取 Table MDX、`table.tsx`、四个官方配方与 `style-nova.css`，并实时对照 [Base UI Table](https://ui.shadcn.com/docs/components/base/table)。来源见 [sources.json](sources.json)。

## 发现与修改

- 官方顺序：demo（7 行发票 + Total footer + caption-bottom）、Composition、Footer、Actions、Data Table（说明）、RTL。本地 `tableOfficialExamples` 已是 demo/Footer/Actions/RTL 四段且前置，但缺 Composition 与 API 小节，页面仍是 `Examples` 分组，原生 12 个组件分项示例没有 `Kotlin extension:` 前缀，Footer/Actions/RTL 缺官网说明，Data Table 段缺失。
- 本轮补 Composition 树（Caption/Header/Body/Footer）与三个 API 小节；`examplesHeading = null`；插入 Data Table 说明段（用只读表格呈现 Compose 无 TanStack 对应物的取舍）；给 Footer/Actions/RTL 补官方 copy；原生示例统一加前缀。
- 组件未改；Header 40dp、cell 14sp/20sp + p-2、footer 半透明顶线、caption-bottom 均沿用既有实现。

## 浏览器验证

`browser/`：`actions-section-light.png` + `actions-menu-open-light.png`（行内 DropdownMenu 打开 Edit/Duplicate/Delete）、`rtl-section-light.png`（阿拉伯语/+希伯来语工具栏）；四象限 1440/390×浅深在最终构建（`879c014d3060edbcf720.wasm`）复查，demo 表格与官方 7 行发票 + Total + caption 一致。

## 自动验证

`tests/`：TableExamplesTest **28 项通过**（7 测试×4 视口/主题参数化，含官方顺序/Composition/Data Table/前缀与 7 行发票/import 代码断言）。最终应用 `879c014d3060edbcf720.wasm`，SHA256 `97fa0dc37ba6ef519ac835a9de585e4f3cf0c3f35ce14a80d232502370a46931`。

## 未关闭的差异

- 共享列宽/`colSpan` 行为：Compose 端靠 `Modifier.weight`，没有 HTML 自动列宽与 colspan 语义；数据表分页/排序无对应物（已在 Data Table 段说明）。
- 行 hover/selected 状态与水平溢出滚动本轮只复查静态四象限与既有测试，未新增交互帧；希伯来语回退字体的行框仍有差异。
- 共享页面标签、手机宽度 375 对 390 与代码面板差异不变。
