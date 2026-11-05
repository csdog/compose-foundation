# Drawer 页面复核（2026-11-05）

固定上游 `8a3536678a528cc012b9e62484290e79d0be410f` 的 Base UI Drawer MDX、`drawer.tsx`、七个官方配方与 `style-nova.css`，并实时对照 [ui.shadcn.com Base UI Drawer](https://ui.shadcn.com/docs/components/base/drawer)。来源与 SHA-256 见 [sources.json](sources.json)。

## 发现

- 官方顺序：demo（delivery time）、Custom Sizes、Styling、Position、Swipe Handle、Nested、Non Modal、Snap Points、Responsive、Migrating from Vaul、API Reference；demo 后有一条 Base UI 迁移 Callout。
- 本地此前无章节说明、无 Composition/API Reference，示例标题多为 API 名；官方七个配方中 Position 只演示左抽屉（Move Goal）、Swipe Handle/Non Modal/Snap Points 的标题与说明不同，Responsive 缺失，Nested 触发文案不同。
- 组件本身与 nova 源码一致：垂直抽屉 `calc(100dvh - 6rem)` 上限、侧抽屉 75% / 384dp 上限、8dp 浮动 inset、24dp 圆角、`bg-popover`、遮罩 `bg-black/10` + 4dp 模糊、snap 点与嵌套栈。

## 修改

- 重建示例列表（`examplesHeading = null`）：先放官方七个配方（官方标题/说明/预览；Position 左抽屉 Move Goal、Swipe Handle 320dp 内容块、Nested 四层、Non Modal `modal=false + disablePointerDismissal + swipeDirection=Right`、Snap Points `496dp/1f` + swipe handle、Responsive 桌面 Dialog/窄屏 Drawer），其余 Compose-only 配方加 `Kotlin extension: ` 前缀后置，Filter 掉重复的旧 demo/滚动/非模态/Modal/Swipe handle/Nested/Snap 条目。
- 新增 `Custom Sizes`、`Styling` 前置章节与 `Migrating from Vaul` 后置章节（Kotlin API 对应说明），补 Composition 树、Usage 导入块与七个 API 小节。
- 修正官方配方 footer：`DrawerClose { Button("Close", …) }` 全部 `fillMaxWidth()`，匹配官方 `flex-col` footer 的拉伸行为。
- demo 预览用公共 `ShadcnToast`：确认后显示 “Delivery time confirmed / <label>”；触发按钮回到预览居中（共享 Toaster 不再撑开根布局）。

## 浏览器验证

`browser/`（1440×1000）：

- 桌面 demo 右侧抽屉打开/关闭；Position 左抽屉（贴左、仅右侧 24dp 圆角）；Swipe Handle 内容自适应高度 + 4dp 拖动条；Nested 连续打开到第四层（`nested-level1..4-light.png`，父层保持挂载）；Non Modal 右侧抽屉无遮罩、点击页面后仍保持打开；Snap Points 初始 496dp peek，按住 swipe handle 上拖后吸附到全高（`snap-points-peek/expanded-light.png`）；Responsive 桌面渲染 Dialog（`responsive-dialog-open-light.png`）。
- 四象限（1440/390 × 浅深）由 capture.py 生成并逐张目视复查，`sheet-all.png` 为拼图。

## 自动验证

`tests/`：DrawerExamplesTest **10 项通过**（官方顺序 + extension 前缀、demo 打开/取消/确认 toast、Position、Swipe Handle、四层嵌套逐层关闭、Non Modal、Snap Points、Responsive 桌面/窄屏、导出章节顺序）。构建成功，最终应用 `17aa61fed5cd3f56a86f.wasm`，SHA256 `7f45661cba22c83438633b3dd71bca5bfadb43c773ab974a905c067eaeed9fb0`。

## 未关闭的差异

- demo 后的 Base UI 迁移 Callout 没有页面位置可放（页面壳没有首个预览与 Installation 之间的 callout 槽位）。
- `Migrating from Vaul` 官网是 7 个 Steps（h3），本地用等价 Bullets + 一段代码表示，标题层级不同；官方 CSS 变量/`data-*` 属性表没有逐项表格，改用 Bullets 映射到 Compose 参数。
- demo toast 由预览内部 Toaster 渲染（预览右下角），官网 sonner 固定视口右下角；这是共享 toast 位置问题，未在本页单独改布局。
- 手势：浏览器只验证了 snap handle 上拖吸附；四向拖动/欠阻尼/惯性与真机触摸、iOS 定位 body 要求仍属平台差异，未在本轮重验。
- Canvas 无 DOM：Base UI 的 `data-swipe-direction`/`data-expanded`/`data-swipe-axis` 等属性选择器无法直接表达，也没有原生 inert/aria 语义。
- 共享页面：无 React Aria/Radix UI 标签页、无广告、手机 clientWidth 375 vs 390、代码面板为 Kotlin；官网 drawer 使用 base-rhea，本地按项目目标继续 base-nova。
