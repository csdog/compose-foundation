# Menubar 复核（2026-11-05）

按固定上游 `8a3536678a528cc012b9e62484290e79d0be410f` 重读 Menubar MDX、`menubar.tsx`、demo/checkbox/radio/submenu/icons/rtl 六个配方与 `style-nova.css`，并实时对照 [Base UI Menubar](https://ui.shadcn.com/docs/components/base/menubar)。来源见 [sources.json](sources.json)。

## 发现与修改

- 官方顺序为 demo（隐藏标题）、Checkbox、Radio、Submenu、With Icons、RTL、API Reference；本地六配方顺序正确，但其余 30 个 Compose-only 配方没有统一 `Kotlin extension:` 前缀，页面也没有 API Reference 小节。
- 本轮给非官方配方统一加前缀（`examples.map(::menubarPageExample)`，官方标题集合固定），补 API Reference 的三个小节（Menubar / Menu·Trigger·Content / Items·Groups·Separators，含 Base UI 链接），组件未改。
- 浏览器对照确认 File 菜单换行（New Incognito Window 两行）、菜单宽度与官方一致；没有发现需要改组件的视觉差异。

## 浏览器验证

`browser/`：关闭态、File 菜单打开（含 ⌘T/⌘N、禁用项、Share 子菜单入口、Print…）、键盘 ArrowDown 高亮、ArrowRight 切到 Edit 菜单、Escape 关闭、View 菜单（复选框 Bookmarks Bar/Full URLs、禁用 Reload/Force Reload）、Profiles 单选菜单、RTL 阿拉伯语栏；官方 File 菜单截图同目录对照。四象限（1440/390 × 浅深）已复查。

## 自动验证

`tests/`：MenubarExamplesTest **8 项通过**（六配方顺序、extension 前缀、API/章节导出、默认菜单内容、选择语义、关闭重开保持、RTL code）。构建成功；最终应用 `e1ab1f17e73949ec9320.wasm`。

## 未关闭的差异

- Canvas 没有原生 DOM 菜单语义（role=menu/menuitem、aria-expanded、原生焦点陷阱与屏幕阅读器树），键盘与焦点由 Compose 实现；悬停打开子菜单只用 JVM 测试覆盖，未单独截交互帧。
- 共享页面：只有 Base UI 标签（无 React Aria/Radix UI）、无广告、手机 clientWidth 375 对 390、代码面板为 Kotlin；菜单指针碰撞/滚动与真机触摸未重验。
