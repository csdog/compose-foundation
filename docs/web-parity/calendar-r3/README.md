# Calendar — 浏览器四象限复核（r3）

- 日期：2026-11-05
- 官方修订：`8a3536678a528cc012b9e62484290e79d0be410f`（ui.shadcn.com Base UI + base-nova）
- 本轮定位：页面/配方已有基础，仅做当前四象限浏览器复核、适用交互状态取证与真实差异修复；不重建页面。
- 构建：slot h / port 8778，Wasm SHA-256 `c0570519f21493ae932505507aeb4d8130a06b13be91215ebede3b58db1710cc`（最终构建后重新截图）。
- 证据：`browser/`（四象限 official/local + `meta.json` + contact sheets）、`browser/sections/`（逐配方官方与本地对照）、`browser/states/`、`tests/`。

## 配方核对

官方 11 个预览：calendar-demo、Persian/Hijri/Jalali、Basic、Range Calendar、Month and Year Selector、Presets、Date and Time Picker、Booked dates、Custom Cell Size、Week Numbers、RTL；另 About/Date Picker/Selected Date 文案与 Changelog。
本地官方配方 10 组（无 Hijri 预览），顺序一致；另有 5 个 Kotlin extension 配方（Multiple/Disabled/Week starts on Monday/Hide outside days/Month bounds）。

## 本轮变更

- 补齐官方配方描述：Basic、Range Calendar、Month and Year Selector、Week Numbers、RTL（原先本地无描述）。
- RTL 描述采用官方原文。
- 未改组件。

## 验证

- `CalendarExamplesTest`：6 tests，全部通过。
- 四象限已复查；逐配方对照 9 组已复查。
- 交互：Month 菜单打开（Jan–Dec，`states/month-menu-open2.png`）、Year 菜单打开（1926–2026，`states/year-menu-open2.png`）、日期选择（`states/date-selected2.png`）、Range 初始区间与点击选择已取证、RTL 阿拉伯语日历已取证（`states/rtl-arabic.png`）。

## 剩余差异（保持 [ ]）

- 官方 Persian/Hijri/Jalali 预览未实现：CalendarDate 仅公历，需日历系统适配器（页面 About 文案已说明）。
- Month/Year caption 下拉为带边框胶囊按钮 + Compose DropdownMenu；官方为无边框文本 + 原生 select 覆盖层，且月份文案随浏览器 locale（官方显示 “11月”，本地英文 “Nov”）。这是组件层差异（`shadcn/core/.../calendar/CalendarStyles.kt`、`Calendar.kt`），不在本批写入范围，需共享修复排期。
- Custom Cell Size 的 $ 价格密度、Booked dates 具体日期与官方示例不完全相同；Range 二次选择交互仅取证未做断言。
- 官方 Changelog 小节本地未复刻。

## 共享文件需求

需要组件所有者处理：`shadcn/core/src/commonMain/kotlin/com/toutiaoyan/compose/ui/components/calendar/CalendarStyles.kt`（rememberCalendarCaptionDropdownStyle）去掉下拉触发按钮边框/背景，并评估月份标签使用 locale 长月份（官方 caption 为文本覆盖原生 select）。
