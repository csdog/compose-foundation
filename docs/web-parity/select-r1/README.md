# Select 复核（2026-11-05）

按固定上游 `8a3536678a528cc012b9e62484290e79d0be410f` 读取 Select MDX、`select.tsx`、7 个官方配方与 `style-nova.css`，并实时对照 [Base UI Select](https://ui.shadcn.com/docs/components/base/select)。来源见 [sources.json](sources.json)。

## 发现与修改

- 官方页面顺序：demo（隐藏标题）、Composition、Align Item With Trigger、Groups、Scrollable、Disabled、Invalid、RTL、API Reference；本地此前把 30 个 API 式示例放在 `Examples` 分组里，没有官方配方标题、Composition 与 RTL。
- 本轮重建：`examplesHeading = null`，新增 Composition 树；官方七段按顺序接线（demo 复用原 Default 水果选择；新增 Align Item With Trigger 的 Switch+`alignItemWithTrigger`、Groups 双分组+Separator、Scrollable 长时区列表、Disabled 整表禁用+禁用项、Invalid `Field(invalid)`+`Select(invalid)`、RTL 三语工具栏+阿拉伯语）；其余示例统一加 `Kotlin extension:` 前缀，原有 `SelectExamplesTest` 新增 7 项页面/交互回归。

## 浏览器验证

`browser/`（1440×1000 浅色）：`closed-light.png`、`demo-open-light.png`（Fruits 分组、选中项勾选）、`scrollable-open-light.png` / `scrollable-scrolled-light.png`（长列表弹出后滚动到 South America，验证长列表滚动）、`rtl-section-light.png` / `rtl-open-light.png`（阿拉伯语占位与弹出）、`align-item-section-light.png`；四象限 1440/390×浅深在最终构建上复查（官方主题像素已确认，不是跨源串色）。

## 自动验证

`tests/`：SelectExamplesTest **7 项通过**（官方顺序/前缀/Composition/description、demo 选择、Groups、Scrollable 滚动定位、Disabled 不可打开、Invalid、RTL 三语）。构建成功；最终应用 `8b31cf6051ad0ed3c7dd.wasm`，SHA256 `af0897ba8dcd1ca0ba6ea8ace92d531d0a7a68b6aa02253393db7e8c3dc60285`。

## 未关闭的差异

- 浏览器只覆盖了长列表滚动；typeahead 输入、边缘碰撞/翻转、动态重排与 prevent-item-select 仍靠 `Kotlin extension:` 示例与既有核心测试，未逐一在浏览器取证。
- Canvas 无原生 `role=listbox/option`、`aria-activedescendant` 与原生弹层语义；RTL 弹层方向/位置只验证了阿拉伯语触发与弹出画面。
- 共享页面标签（单 Base UI）、无广告、手机 375 对 390、Kotlin 代码面板与字体光栅化差异不变。
