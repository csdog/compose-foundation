# Breadcrumb — 浏览器四象限复核（r3）

- 日期：2026-11-05
- 官方修订：`8a3536678a528cc012b9e62484290e79d0be410f`（ui.shadcn.com Base UI + base-nova）
- 本轮定位：页面/配方已有基础，仅做当前四象限浏览器复核、适用交互状态取证与真实差异修复；不重建页面。
- 构建：slot h / port 8778，Wasm SHA-256 `c0570519f21493ae932505507aeb4d8130a06b13be91215ebede3b58db1710cc`（最终构建后重新截图）。
- 证据：`browser/`（四象限 official/local + `meta.json` + contact sheets）、`browser/sections/`（逐配方官方与本地对照）、`browser/states/`、`tests/`。

## 配方核对

官方 7 个预览：breadcrumb-demo、Basic、Custom separator、Dropdown、Collapsed、Link component、RTL；另有 Composition 与 7 个 API 小节。
本地 7 配方顺序、标题一致；Composition 与 API 小节一致。

## 本轮变更

- 本轮未修改页面/组件代码；仅复核与取证。

## 验证

- `BreadcrumbExamplesTest`：5 tests，全部通过。
- 四象限已复查；逐配方对照 6 组已复查（Basic、Custom separator、Dropdown、Collapsed、Link component、RTL）。
- 交互：Dropdown 食谱点击 “Components ⌄” 打开菜单（Documentation / Themes / GitHub，`states/dropdown-open.png`），Escape/点击外部关闭；Collapsed 食谱省略号展示已取证（`states/before-collapsed.png`）；Basic 链接 hover 已取证。

## 剩余差异（保持 [ ]）

- 本地额外提供 Kotlin extension: Disabled 配方与 RTL 语言选择器（官方为静态 direction 预览），属于本地扩展。
- 原生导航链接语义、键盘焦点移动细节未验证（Canvas 无 DOM 语义树）。
- 手机宽度下长路径换行/截断与官方仍有细微差异（前几轮已记录）。

## 共享文件需求

无。
