# Web 对齐验收状态

更新日期：2026-11-11。当前保存状态为 **40 / 67 项已勾选，27 项未完成**。
本轮仅继续 Calendar 验收，未启动全队列；历史运行信息见[批次记录](../.agent-runs/batches/latest.json)。
文档整理本身不构成验收；勾选只在组件证据与当前源码校验通过后更新。

## 状态口径

- `[x]` 保留此前清单的关闭记录；共享源码后续变化仍需按输入哈希重新验证。
- `[ ]` 表示完整验收仍未关闭，不能等同于未实现或完全没有测试。
- 阶段完成、测试通过和交接结束均不自动等于组件验收完成。
- 下方阻塞描述来自最近一次已停止的执行记录，不是本次重新复现的结果。

## 工作入口

- [验收计划与规则](shadcn-web-plan.md)
- [批处理与断点操作](../scripts/BATCH_EXECUTION.md)
- [停止时的运行状态](../.agent-runs/batch-state.json)
- [完整历史进度](archive/2026-11-09/shadcn-web-progress.md)
- [按组件查阅历史验收范围与证据](archive/2026-11-09/component-status-details.md)

## 逐组件清单

- [x] Accordion (`accordion`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#accordion) · [交接](../.agent-runs/accordion/progress.md)。
- [x] Alert (`alert`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#alert)。
- [x] Alert Dialog (`alert-dialog`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#alert-dialog) · [交接](../.agent-runs/alert-dialog/progress.md)。
- [x] Aspect Ratio (`aspect-ratio`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#aspect-ratio) · [交接](../.agent-runs/aspect-ratio/progress.md)。
- [x] Attachment (`attachment`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#attachment)。
- [x] Avatar (`avatar`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#avatar) · [交接](../.agent-runs/avatar/progress.md)。
- [x] Badge (`badge`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#badge)。
- [x] Breadcrumb (`breadcrumb`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#breadcrumb) · [交接](../.agent-runs/breadcrumb/progress.md)。
- [x] Bubble (`bubble`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#bubble) · [交接](../.agent-runs/bubble/progress.md)。
- [x] Button (`button`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#button) · [交接](../.agent-runs/button/progress.md)。
- [x] Button Group (`button-group`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#button-group) · [交接](../.agent-runs/button-group/progress.md)。
- [x] Calendar (`calendar`)：2026-11-10 验收通过：65 tests、10 unchanged goldens、5 lint、JS/Wasm、四象限与菜单/范围/主题/导航浏览器实测及独立审查通过。已修复目录滚动、选中悬停与按压动画；Wasm DOM/ARIA 平台限制按 I16 明确记录。[当前证据](../.agent-runs/calendar/acceptance-20261110/evidence.md) · [独立审查](../.agent-runs/calendar/acceptance-20261110/independent-audit.md) · [交接](../.agent-runs/calendar/progress.md)。
- [x] Card (`card`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#card) · [交接](../.agent-runs/card/progress.md)。
- [x] Carousel (`carousel`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#carousel) · [交接](../.agent-runs/carousel/progress.md)。
- [x] Code Block (`code-block`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#code-block)。
- [x] Trace (`trace`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#trace)。
- [ ] Chart (`chart`)：停止时阻塞：无 Tooltip 折线图的选中值辅助语义缺失。[范围与证据](archive/2026-11-09/component-status-details.md#chart) · [交接](../.agent-runs/chart/progress.md)。
- [ ] Checkbox (`checkbox`)：停止时阻塞：动画中间帧验证未解决。[范围与证据](archive/2026-11-09/component-status-details.md#checkbox) · [交接](../.agent-runs/checkbox/progress.md)。
- [x] Chip (`chip`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#chip) · [交接](../.agent-runs/chip/progress.md)。
- [x] Collapsible (`collapsible`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#collapsible)。
- [x] Combobox (`combobox`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#combobox) · [交接](../.agent-runs/combobox/progress.md)。
- [x] Command (`command`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#command)。
- [ ] Context Menu (`context-menu`)：停止时阻塞：键盘打开与焦点验证未完成。[范围与证据](archive/2026-11-09/component-status-details.md#context-menu) · [交接](../.agent-runs/context-menu/progress.md)。
- [x] Date Picker (`date-picker`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#date-picker)。
- [x] Dialog (`dialog`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#dialog)。
- [x] Drawer (`drawer`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#drawer)。
- [ ] Dropdown Menu (`dropdown-menu`)：停止时阻塞：焦点和 Escape 交互待诊断。[范围与证据](archive/2026-11-09/component-status-details.md#dropdown-menu) · [交接](../.agent-runs/dropdown-menu/progress.md)。
- [ ] Empty (`empty`)：停止时阻塞：语言弹层关闭后的辅助功能树恢复异常。[范围与证据](archive/2026-11-09/component-status-details.md#empty) · [交接](../.agent-runs/empty/progress.md)。
- [ ] Field (`field`)：停止时阻塞：页面标题未随组件路由更新。[范围与证据](archive/2026-11-09/component-status-details.md#field) · [交接](../.agent-runs/field/progress.md)。
- [ ] Hover Card (`hover-card`)：停止时阻塞：浏览器关闭行为待诊断。[范围与证据](archive/2026-11-09/component-status-details.md#hover-card) · [交接](../.agent-runs/hover-card/progress.md)。
- [ ] Input (`input`)：停止时阻塞：动态形状、过渡帧及文件按钮专项证据缺失。[范围与证据](archive/2026-11-09/component-status-details.md#input) · [交接](../.agent-runs/input/progress.md)。
- [ ] Input Group (`input-group`)：停止时阻塞：More / Search In 下拉按钮回调未实现。[范围与证据](archive/2026-11-09/component-status-details.md#input-group) · [交接](../.agent-runs/input-group/progress.md)。
- [ ] Input OTP (`input-otp`)：停止时阻塞：手机 Form 布局裁切。[范围与证据](archive/2026-11-09/component-status-details.md#input-otp) · [交接](../.agent-runs/input-otp/progress.md)。
- [ ] Item (`item`)：已有验证进展；会话预算结束，保留续跑断点。[范围与证据](archive/2026-11-09/component-status-details.md#item) · [交接](../.agent-runs/item/progress.md)。
- [x] Kbd (`kbd`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#kbd) · [交接](../.agent-runs/kbd/progress.md)。
- [ ] Label (`label`)：用户主动停止于 30-verify，保留断点。[范围与证据](archive/2026-11-09/component-status-details.md#label) · [交接](../.agent-runs/label/progress.md)。
- [ ] Marker (`marker`)：尚未关闭完整验收；后续从已保存范围继续。[范围与证据](archive/2026-11-09/component-status-details.md#marker) · [交接](../.agent-runs/marker/progress.md)。
- [ ] Menubar (`menubar`)：尚未关闭完整验收；后续从已保存范围继续。[范围与证据](archive/2026-11-09/component-status-details.md#menubar) · [交接](../.agent-runs/menubar/progress.md)。
- [ ] Message (`message`)：尚未关闭完整验收；后续从已保存范围继续。[范围与证据](archive/2026-11-09/component-status-details.md#message) · [交接](../.agent-runs/message/progress.md)。
- [x] Message Scroller (`message-scroller`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#message-scroller)。
- [ ] Navigation Menu (`navigation-menu`)：尚未关闭完整验收；后续从已保存范围继续。[范围与证据](archive/2026-11-09/component-status-details.md#navigation-menu) · [交接](../.agent-runs/navigation-menu/progress.md)。
- [x] Pagination (`pagination`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#pagination) · [交接](../.agent-runs/pagination/progress.md)。
- [ ] Popover (`popover`)：尚未关闭完整验收；后续从已保存范围继续。[范围与证据](archive/2026-11-09/component-status-details.md#popover) · [交接](../.agent-runs/popover/progress.md)。
- [ ] Progress (`progress`)：尚未关闭完整验收；后续从已保存范围继续。[范围与证据](archive/2026-11-09/component-status-details.md#progress) · [交接](../.agent-runs/progress/progress.md)。
- [ ] Questionnaire (`questionnaire`)：尚未关闭完整验收；后续从已保存范围继续。[范围与证据](archive/2026-11-09/component-status-details.md#questionnaire) · [交接](../.agent-runs/questionnaire/progress.md)。
- [ ] Radio Group (`radio-group`)：尚未关闭完整验收；后续从已保存范围继续。[范围与证据](archive/2026-11-09/component-status-details.md#radio-group) · [交接](../.agent-runs/radio-group/progress.md)。
- [x] Resizable (`resizable`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#resizable)。
- [x] Scroll Area (`scroll-area`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#scroll-area)。
- [x] Scroll Fade (`scroll-fade`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#scroll-fade)。
- [ ] Select (`select`)：尚未关闭完整验收；后续从已保存范围继续。[范围与证据](archive/2026-11-09/component-status-details.md#select) · [交接](../.agent-runs/select/progress.md)。
- [x] Separator (`separator`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#separator)。
- [ ] Sheet (`sheet`)：尚未关闭完整验收；后续从已保存范围继续。[范围与证据](archive/2026-11-09/component-status-details.md#sheet) · [交接](../.agent-runs/sheet/progress.md)。
- [ ] Shimmer (`shimmer`)：尚未关闭完整验收；后续从已保存范围继续。[范围与证据](archive/2026-11-09/component-status-details.md#shimmer) · [交接](../.agent-runs/shimmer/progress.md)。
- [ ] Sidebar (`sidebar`)：尚未关闭完整验收；后续从已保存范围继续。[范围与证据](archive/2026-11-09/component-status-details.md#sidebar) · [交接](../.agent-runs/sidebar/progress.md)。
- [x] Skeleton (`skeleton`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#skeleton) · [交接](../.agent-runs/skeleton/progress.md)。
- [ ] Slider (`slider`)：尚未关闭完整验收；后续从已保存范围继续。[范围与证据](archive/2026-11-09/component-status-details.md#slider) · [交接](../.agent-runs/slider/progress.md)。
- [x] Spinner (`spinner`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#spinner)。
- [x] Stepper (`stepper`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#stepper)。
- [x] Switch (`switch`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#switch)。
- [x] Table (`table`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#table) · [交接](../.agent-runs/table/progress.md)。
- [x] Tabs (`tabs`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#tabs) · [交接](../.agent-runs/tabs/progress.md)。
- [x] Text (`text`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#text)。
- [x] Textarea (`textarea`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#textarea)。
- [ ] Toast (`toast`)：尚未关闭完整验收；后续从已保存范围继续。[范围与证据](archive/2026-11-09/component-status-details.md#toast) · [交接](../.agent-runs/toast/progress.md)。
- [ ] Toggle (`toggle`)：尚未关闭完整验收；后续从已保存范围继续。[范围与证据](archive/2026-11-09/component-status-details.md#toggle) · [交接](../.agent-runs/toggle/progress.md)。
- [x] Toggle Group (`toggle-group`)：2026-11-11 验收通过。补齐 Changelog、Custom 卡片样式与 View Code、Single 键盘操作和 Connected Outline 的验收证据。74 项聚焦测试、5 项 lint、JVM/JS/Wasm 构建、544 项浏览器断言、机械审计和独立审查通过。I7/I16 验收范围见审计。[验收凭证](../.agent-runs/toggle-group/acceptance.json) · [审计](../.agent-runs/toggle-group/verify/acceptance-provenance/audit/report.md) · [历史范围](archive/2026-11-09/component-status-details.md#toggle-group) · [交接](../.agent-runs/toggle-group/progress.md)。
- [x] Tooltip (`tooltip`)：已有关闭记录。[范围与证据](archive/2026-11-09/component-status-details.md#tooltip) · [交接](../.agent-runs/tooltip/progress.md)。

## 历史进度

按日/批次的长篇记录已移至[2026-11-09 归档](archive/2026-11-09/README.md)。
此页保留执行入口、明确状态和组件勾选；新增长篇取证记录写入对应组件证据目录。
