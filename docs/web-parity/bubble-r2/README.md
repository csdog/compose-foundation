# Bubble — 浏览器四象限复核（r2）

- 日期：2026-11-05
- 官方修订：`8a3536678a528cc012b9e62484290e79d0be410f`（ui.shadcn.com Base UI + base-nova）
- 本轮定位：页面/配方已有基础，仅做当前四象限浏览器复核、适用交互状态取证与真实差异修复；不重建页面。
- 构建：slot h / port 8778，Wasm SHA-256 `c0570519f21493ae932505507aeb4d8130a06b13be91215ebede3b58db1710cc`（最终构建后重新截图）。
- 证据：`browser/`（四象限 official/local + `meta.json` + contact sheets）、`browser/sections/`（逐配方官方与本地对照）、`browser/states/`、`tests/`。

## 配方核对

官方 9 个预览：bubble-demo、Variants、Alignment、Bubble Group、Links and Buttons、Reactions、Show More / Collapsible、Tooltip、Popover；Composition、Features 与 4 个 API 小节。
本地 9 配方顺序、标题一致。

## 本轮变更

- 本轮未修改页面/组件代码；仅复核与取证。

## 验证

- `BubbleExamplesTest`：8 tests，全部通过。
- 四象限已复查；逐配方对照 8 组已复查。
- 交互：Links and Buttons 配方可点击气泡 hover 后显示焦点环，点击 “I need help with my subscription” 出现 toast “You clicked help with subscription”（`states/toast-after-click.png`）。

## 剩余差异（保持 [ ]）

- Variants 预览中 ghost 长文案在本地预览高度内被截断，官方预览显示完整段落；预览高度参数仍需微调（未在本轮改，避免与 80% 宽/自然高规则冲突）。
- Links and Buttons 描述为 Kotlin 适配文案（官方描述讲 render prop），属平台 API 差异。
- Popover/Tooltip/Reactions 的完整键盘与 aria 路径未逐项验证；按钮反应自动 padding 已知残留。

## 共享文件需求

无。
