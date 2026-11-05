# Card — 浏览器四象限复核（r3）

- 日期：2026-11-05
- 官方修订：`8a3536678a528cc012b9e62484290e79d0be410f`（ui.shadcn.com Base UI + base-nova）
- 本轮定位：页面/配方已有基础，仅做当前四象限浏览器复核、适用交互状态取证与真实差异修复；不重建页面。
- 构建：slot h / port 8778，Wasm SHA-256 `c0570519f21493ae932505507aeb4d8130a06b13be91215ebede3b58db1710cc`（最终构建后重新截图）。
- 证据：`browser/`（四象限 official/local + `meta.json` + contact sheets）、`browser/sections/`（逐配方官方与本地对照）、`browser/states/`、`tests/`。

## 配方核对

官方 6 个预览：card-demo、Size、Spacing、Edge to Edge、Image、RTL；Composition 与 7 个 API 小节、Changelog。
本地 6 配方顺序、标题一致（Edge to Edge 复用 Spacing 节说明）。

## 本轮变更

- RTL 描述按 Lead 统一要求改为官方原文 “To enable RTL support in shadcn/ui, see the RTL configuration guide.”（原为 “in your app”）。
- 未改组件。

## 验证

- `CardExamplesTest`：20 tests，全部通过。
- 四象限已复查；逐配方对照 4 组（Size、Spacing+Edge to Edge、Image、RTL）已复查。
- 静态配方，无额外交互状态。

## 剩余差异（保持 [ ]）

- 390 宽度下卡片 Password 行 “Password” 与 “Forgot password?” 间距极小（官方同样紧绷，但本地更接近贴合）；换行/字重细节仍有差异。
- 原生滚动条、1px 边框占位、图像占位色差仍开放。
- 本地额外提供 CardImage（Kotlin extension）API 小节与 Composition 内容（官方 Card 有 Composition，一致）。

## 共享文件需求

无。
