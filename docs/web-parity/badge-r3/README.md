# Badge — 浏览器四象限复核（r3）

- 日期：2026-11-05
- 官方修订：`8a3536678a528cc012b9e62484290e79d0be410f`（ui.shadcn.com Base UI + base-nova）
- 本轮定位：页面/配方已有基础，仅做当前四象限浏览器复核、适用交互状态取证与真实差异修复；不重建页面。
- 构建：slot h / port 8778，Wasm SHA-256 `c0570519f21493ae932505507aeb4d8130a06b13be91215ebede3b58db1710cc`（最终构建后重新截图）。
- 证据：`browser/`（四象限 official/local + `meta.json` + contact sheets）、`browser/sections/`（逐配方官方与本地对照）、`browser/states/`、`tests/`。

## 配方核对

官方 7 个预览：badge-demo、Variants、With Icon、With Spinner、Link、Custom Colors、RTL。
本地 7 配方顺序、标题一致（Default、Variants、With Icon、With Spinner、Link、Custom Colors、RTL）；描述、Usage 与 API Reference 与官方对应。

## 本轮变更

- RTL 配方描述改为官方原文：“To enable RTL support in shadcn/ui, see the RTL configuration guide.”（原先为 Kotlin 专用说明）。
- 无其它页面改动（页面与组件在前几轮已完成）。

## 验证

- `BadgeExamplesTest`：8 tests，全部通过（`tests/TEST-...BadgeExamplesTest.xml`）。
- 四象限 official/local 各 4 张已复查（desktop 1440×1000、phone 390×844、light/dark），首屏骨架、预览几何、字体、颜色一致。
- 逐配方对照 6 组（Variants、With Icon、With Spinner、Link、Custom Colors、RTL）已复查。
- 交互：Link 配方徽章 hover 变色，点击后 URL hash 变为 `#link`（`states/hover-link.png`、`states/click-link.png`）。

## 剩余差异（保持 [ ]）

- 本地页面额外保留 “Composition” 小节（官方 Badge 页无此节），属于本地页面结构扩展；如需严格对齐应移除（影响 3 个页面的统一决策，未在本轮单方面删除）。
- 徽章内部键盘焦点态、原生链接语义（无 DOM、Canvas 渲染）未逐项验证；hover/click 已取证。
- 官方 React Aria/Radix 变体标签页不适用本 Canvas 应用（平台差异）。

## 共享文件需求

无共享文件修改；Composition 额外小节的取舍属于页面骨架决策，留给 Lead 统一处理。
