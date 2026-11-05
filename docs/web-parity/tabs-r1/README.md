# Tabs 复核（2026-11-05）

按固定上游 `8a3536678a528cc012b9e62484290e79d0be410f` 读取 Tabs MDX、`tabs.tsx`、六个官方配方与 `style-nova.css`，并实时对照 [Base UI Tabs](https://ui.shadcn.com/docs/components/base/tabs)。来源见 [sources.json](sources.json)。

## 发现与修改

- 官方顺序：demo（四个面板）、Composition、Line、Vertical、Disabled、Icons、RTL、API Reference。本地六段已前置，但缺 Composition/API 小节，仍是 `Examples` 分组，原生示例无前缀，Line/Vertical/RTL 无官网说明。
- 组件级发现：`Tabs` 的 `activationMode` 默认是 `Manual`，而 Base UI/Radix 的 `Tabs.Root` 默认是 **Automatic**（方向键同时移动焦点并选中面板）。本轮把默认改为 `Automatic` 并补文档注释说明如何显式选 Manual；显式 Manual/Automatic 的扩展示例保留，核心与页面测试同步更新（原先把 Manual 当默认的断言改为对 Automatic 的断言）。
- 页面：补 Composition 树与三个 API 小节，`examplesHeading = null`，Line/Vertical/RTL 补官方 copy，原生示例统一 `Kotlin extension:` 前缀。

## 浏览器验证

`browser/`：`demo-light.png`（Overview/Analytics/Reports/Settings 四面板）、`analytics-selected-light.png`（点击切换）、`keyboard-automatic-light.png`（深色，点击后 ArrowRight 直接选中并显示 Reports 面板，验证 Automatic 默认）、`line-section-light.png`、`vertical-section-light.png`、`rtl-section-light.png`；四象限 1440/390×浅深在最终构建复查。

## 自动验证

`tests/`：TabsExamplesTest **44 项通过**（11 测试×4 视口/主题，含官方顺序/Composition/前缀、四面板按钮、自动激活、Manual/Automatic 扩展示例、RTL 键盘方向）；核心 TabsBehaviorParityTest **12 项通过**（含默认 Automatic、RTL 反向、单一 Tab 入口、Home/End/非循环边界）。最终应用 `879c014d3060edbcf720.wasm`，SHA256 `97fa0dc37ba6ef519ac835a9de585e4f3cf0c3f35ce14a80d232502370a46931`。

## 未关闭的差异

- 150ms 选中态过渡未抓中间帧；hidden panel 的焦点回收/keepMounted 状态只由 JVM 测试覆盖，未逐帧截图；RTL 方向键的浏览器实测仍以 JVM 为主。
- Canvas 无原生 tablist/tab/aria-selected 与动态顺序（DOM 重排）语义；共享页面标签、手机 375 对 390、编码面板差异不变。
- 全量核心测试运行时观察到 5 个与本批无关的 Menubar/Menu Escape 层失败（属其他 batch 的进行中改动），Tabs 相关 29 项全绿。
