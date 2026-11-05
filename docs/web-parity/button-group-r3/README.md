# Button Group — 浏览器四象限复核（r3）

- 日期：2026-11-05
- 官方修订：`8a3536678a528cc012b9e62484290e79d0be410f`（ui.shadcn.com Base UI + base-nova）
- 本轮定位：页面/配方已有基础，仅做当前四象限浏览器复核、适用交互状态取证与真实差异修复；不重建页面。
- 构建：slot h / port 8778，Wasm SHA-256 `c0570519f21493ae932505507aeb4d8130a06b13be91215ebede3b58db1710cc`（最终构建后重新截图）。
- 证据：`browser/`（四象限 official/local + `meta.json` + contact sheets）、`browser/sections/`（逐配方官方与本地对照）、`browser/states/`、`tests/`。

## 配方核对

官方 12 个预览：button-group-demo、Orientation、Size、Nested、Separator、Split、Input、Input Group、Dropdown Menu、Select、Popover、RTL；Composition、Accessibility、ButtonGroup vs ToggleGroup 文案。
本地 12 配方顺序、标题一致。

## 本轮变更

- Orientation：改为官方单个竖向 ButtonGroup（Plus、Minus）。
- Size：改为官方三行四按钮（Small/Default/Large × label+Button+Group+Plus，行距 32dp）。
- Nested：改为官方嵌套结构（内层 Plus 图标组 + InputGroup “Send a message...” + 尾部 Voice Mode 音频按钮）。
- Separator：改用官方 Secondary + Sm 两个按钮。
- Split：改用官方 Secondary 变体。
- Dropdown Menu：触发按钮改为 Follow，菜单换成官方 7 项（Mute/Mark/Report/Block/Share/Copy + Delete destructive），补 DropdownMenuGroup/Separator。
- Select：改为官方 [货币 Select + 10.00 Input][发送图标按钮] 组合。
- Input Group：改为官方嵌套结构（Plus 组 + “Send a message...” InputGroup + Voice Mode 按钮）。
- RTL 描述改为官方原文。
- 导出 code 字符串同步更新；`ButtonGroupExamplesTest` 菜单断言同步更新。

## 验证

- `ButtonGroupExamplesTest`：16 tests，全部通过。
- 四象限已复查；逐配方对照 11 组已复查（Orientation/Size/Nested/Separator/Split/Input/Input Group/Dropdown/Select/Popover/RTL）。
- 交互：Dropdown Menu 菜单打开取证（`states/dropdown-open3.png`）；Select 弹出 $/€/£ 菜单（`states/select-open.png`）；Popover 打开 Copilot 面板（`states/popover-open2.png`）。

## 剩余差异（保持 [ ]）

- Popover 内容仍为简化版（官方为 PopoverHeader/标题/描述 + Field + Textarea + FieldDescription）；未逐字对齐。
- Input 配方本地保留 outline 统一 style 与分隔符（视觉已接近，内部结构与官方略有差异）。
- role=group 语义、混合子项形状、焦点环在分组缝合处的完整矩阵未验证。
- 本地额外保留 Kotlin 扩展 API 小节（ButtonGroupSeparator/Text 图标组合等）。

## 共享文件需求

无；Popover 内容如需 1:1 对齐会牵涉 Popover/Field 组合，可在后续 Button Group 专项处理。
