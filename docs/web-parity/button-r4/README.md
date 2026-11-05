# Button — 浏览器四象限复核（r4）

- 日期：2026-11-05
- 官方修订：`8a3536678a528cc012b9e62484290e79d0be410f`（ui.shadcn.com Base UI + base-nova）
- 本轮定位：页面/配方已有基础，仅做当前四象限浏览器复核、适用交互状态取证与真实差异修复；不重建页面。
- 构建：slot h / port 8778，Wasm SHA-256 `c0570519f21493ae932505507aeb4d8130a06b13be91215ebede3b58db1710cc`（最终构建后重新截图）。
- 证据：`browser/`（四象限 official/local + `meta.json` + contact sheets）、`browser/sections/`（逐配方官方与本地对照）、`browser/states/`、`tests/`。

## 配方核对

官方 15 个预览：button-demo、Size、Default、Outline、Secondary、Ghost、Destructive、Link、Icon、With Icon、Rounded、Spinner、Button Group、As Link、RTL；另 Cursor 为纯文案小节。
本地 15 配方顺序、标题一致（首个 Default 为 demo）；Cursor 文案存在。

## 本轮变更

- RTL 配方描述改为官方原文（原先为 Kotlin 专用说明）。
- 无其它页面改动。

## 验证

- `ButtonExamplesTest` 12 tests + `ButtonFollowupRecipesTest` 12 tests，全部通过。
- 四象限已复查；逐配方对照 14 组已复查。
- 交互：Button Group 配方 “⋯” 打开真实菜单（Mark as Read/Archive/Snooze/Add to Calendar/Add to List/Label As.../Trash，`states/group-menu-open.png`），Escape 关闭；As Link 配方 Login 链接 hover 已取证。

## 剩余差异（保持 [ ]）

- 本地额外保留 “Composition” 小节（官方 Button 页无）。
- Tabler/Lucide 图标以本地 Lucide 近似，图形细节不完全一致。
- 子菜单宽度、手机宽度碰撞、指针聚焦与 expanded/pressed 完整状态矩阵仍开放。

## 共享文件需求

无；Composition 小节取舍同 Badge。
