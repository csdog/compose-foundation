# 证据完整性审计（2026-11-05）

审计快照：2026-11-05 13:28（+08:00）。只读审计，未修改任何组件/证据文件。审计期间其他 agent 仍在并行写入（快照间 `[x]` 从 16 变为 17，`skeleton-r2`/`slider-r1` 目录在写），本表为单一时间点结果。

## 口径

- 组件：`docs/shadcn-web-progress.md`「逐组件清单」共 **67** 行，`[x]` **17** 行。
- 记录：解析 **220** 条；order 唯一 **是**、标题唯一 **是**、日期倒序 **是**、进度文档内悬空链接 **0** 条。
- 证据目录：`docs/web-parity/` 共 **165** 个目录；组件 claimed **67** 个，未被组件行引用的 **98** 个（共享页面/主题/历史轮次/并行临时目录）。
- 检查项：claimed 目录存在、`README.md`、`sources.json`（或等价来源哈希）、`meta.json`、完整四象限（8 张 official/local×desktop/phone×light/dark）或 `sheet-all.png`、≥1 个 tests XML 与 testcase 数。**项目扩展组件**（README 明确声明官方无页面）按“本地截图 + README 说明 + tests”判定，official 象限/官方来源哈希不适用。
- 状态：**OK** = 必需项齐全；**STALE** = 目录/浏览器证据存在但缺 sources/meta/标准四象限或与记录不一致；**MISSING** = 目录/README/tests 等硬缺口。

## 总览

- 组件状态：**OK 63 / STALE 1 / MISSING 3**。
- `[x]` **17** 个：**0** 个未达证据门槛（全部通过）。
- 项目扩展组件（README 显式声明官方无页面，按扩展口径判定）：Chip, Stepper, Text。
- 审计时仍在写入（15 分钟内修改）的非 OK 目录：Skeleton(skeleton-r2), Slider(slider-r1)——并行移动目标，gate 前需复查。

## 组件表

| 组件 | [x] | claimed 目录 | 证据 | tests | 缺失项 | 状态 / 备注 |
|---|---|---|---|---|---|---|
| Accordion |  | accordion-r13 | 四象限+meta | 7xml/100 | — | OK；记录=Accordion：r13 浏览器四象限与交互复… |
| Alert |  | alert-r11 | 四象限+meta | 5xml/28 | — | OK；记录=Alert：r11 浏览器四象限、说明文案与操作… |
| Alert Dialog |  | alert-dialog-r4 | 四象限+meta | 6xml/28 | — | OK；记录=Alert Dialog：r4 浏览器四象限与打… |
| Aspect Ratio |  | aspect-ratio-r4 | 四象限+meta | 5xml/33 | — | OK；记录=Aspect Ratio：r4 浏览器四象限与 … |
| Attachment | 是 | attachment-r9 | 四象限+meta | 9xml/54 | — | OK；记录=Attachment 第九轮：最终页面复核、ov… |
| Avatar |  | avatar-r3 | 四象限+meta | 4xml/26 | — | OK；记录=Avatar：r3 浏览器四象限、Badge 彩… |
| Badge |  | badge-r3 | 四象限+meta | 1xml/8 | — | OK；记录=Badge：浏览器四象限与 Link 状态复核 |
| Breadcrumb |  | breadcrumb-r3 | 四象限+meta | 1xml/5 | — | OK；记录=Breadcrumb：浏览器四象限与 Dropd… |
| Bubble |  | bubble-r2 | 四象限+meta | 1xml/8 | — | OK；记录=Bubble：浏览器四象限与可点击气泡状态复核 |
| Button |  | button-r4 | 四象限+meta | 2xml/24 | — | OK；记录=Button：浏览器四象限与菜单/As Link… |
| Button Group |  | button-group-r3 | 四象限+meta | 1xml/16 | — | OK；记录=Button Group：配方内容对齐官方并完成… |
| Calendar |  | calendar-r3 | 四象限+meta | 1xml/6 | — | OK；记录=Calendar：补齐官方描述并复核下拉/选择/… |
| Card |  | card-r3 | 四象限+meta | 1xml/20 | — | OK；记录=Card：修正 RTL 描述并完成四象限复核 |
| Carousel |  | carousel-r3 | 四象限+meta | 1xml/7 | — | OK；记录=Carousel：四象限与箭头/拖拽/自动播放状… |
| Code Block | 是 | code-block-r2 | 四象限+meta | 2xml/26 | — | OK；记录=Code Block：官方 23 配方、页面文案… |
| Trace | 是 | trace-r3 | 四象限+meta | 2xml/30 | — | OK；记录=Trace：最终构建复验（导入清理后 89721… |
| Chart |  | chart-page-r4 | 无 | 10xml/96 | meta.json, quadrants(0 files) | MISSING；记录=Chart：First Chart 层级与 Co… |
| Checkbox |  | checkbox-r3 | 四象限+meta | 2xml/15 | — | OK；记录=Checkbox：RTL 文案、新空间四象限与 … |
| Chip |  | chip-r1 | 四象限+meta | 1xml/16 | — | OK（扩展页，本地证据）；记录=Chip：扩展页一致性、Kotlin exten… |
| Collapsible | 是 | collapsible-r1 | 四象限+meta | 1xml/8 | — | OK；记录=Collapsible：当前五配方、RTL 与浏… |
| Combobox |  | combobox-r1 | 四象限+meta | 1xml/12 | — | OK；记录=Combobox：当前官方页十二配方、可编辑筛选… |
| Command | 是 | command-r11 | 四象限+meta | 1xml/6 | — | OK；记录=Command：r11 按当前官网重建六配方与 … |
| Context Menu |  | context-menu-r1 | 四象限+meta | 4xml/28 | — | OK；记录=Context Menu：当前官方页十一配方、s… |
| Date Picker | 是 | date-picker-r10 | 四象限+meta | 1xml/8 | — | OK；记录=Date Picker：r10 按当前官网重建八… |
| Dialog | 是 | dialog-r1 | 四象限+meta | 1xml/10 | — | OK；记录=Dialog：官方六配方、Composition… |
| Drawer | 是 | drawer-r1 | 四象限+meta | 1xml/10 | — | OK；记录=Drawer：官方七配方、章节与手势/snap … |
| Dropdown Menu |  | dropdown-menu-r4 | 四象限+meta | 1xml/18 | — | OK；记录=Dropdown Menu：r4 指针聚焦、默认… |
| Empty |  | empty-r3 | 四象限+meta | 3xml/26 | — | OK；记录=Empty：测试矩阵补记与最终证据 |
| Field |  | field-r1 | 四象限+meta | 3xml/27 | — | OK；记录=Field：官方十三配方重构与Nova Fiel… |
| Hover Card |  | hover-card-r3 | 四象限+meta | 2xml/17 | — | OK；记录=Hover Card：Composition文案… |
| Input |  | input-r1 | 四象限+meta | 3xml/26 | — | OK；记录=Input：官方十五配方重建、RTL文案与状态矩… |
| Input Group |  | input-group-r1 | 四象限+meta | 3xml/49 | — | OK；记录=Input Group：当前官网 14 配方、A… |
| Input OTP |  | input-otp-r1 | 四象限+meta | 2xml/27 | — | OK；记录=Input OTP：当前官网 10 配方、inv… |
| Item |  | item-r3 | 四象限+meta | 3xml/25 | — | OK；记录=Item：官网原文文案、Item vs Fiel… |
| Kbd |  | kbd-r3 | 四象限+meta | 1xml/7 | — | OK；记录=Kbd：r3 官方文案与 Tooltip/RTL… |
| Label |  | label-r1 | 四象限+meta | 1xml/6 | — | OK；记录=Label：r1 按当前官网重建 Label i… |
| Marker |  | marker-r3 | 四象限+meta | 1xml/5 | — | OK；记录=Marker：r3 官方文案、hover 与链接… |
| Menubar |  | menubar-r1 | 四象限+meta | 5xml/35 | — | OK；记录=Menubar：官方六配方前缀、API Refe… |
| Message |  | message-r1 | 四象限+meta | 6xml/41 | — | OK；记录=Message：Features/Accessi… |
| Message Scroller | 是 | message-scroller-r4 | 四象限+meta | 1xml/4 | — | OK；记录=Message Scroller：官方 11 配… |
| Navigation Menu |  | navigation-menu-r3 | 四象限+meta | 2xml/18 | — | OK；记录=Navigation Menu：r3 官方文案/… |
| Pagination |  | pagination-r2 | 四象限+meta | 1xml/20 | — | OK；记录=Pagination：r2 官方描述、扩展收敛与… |
| Popover |  | popover-r3 | 四象限+meta | 1xml/24 | — | OK；记录=Popover：r3 官方描述/Composit… |
| Progress |  | progress-r3 | 四象限+meta | 2xml/28 | — | OK；记录=Progress：当前官网四配方、Composi… |
| Questionnaire |  | questionnaire-r2 | 四象限+meta | 2xml/22 | — | OK；记录=Questionnaire：官方 14 预览、校… |
| Radio Group |  | radio-group-r1 | 四象限+meta | 1xml/12 | — | OK；记录=Radio Group：官方 7 配方与 Nov… |
| Resizable | 是 | resizable-r2 | 四象限+meta | 1xml/20 | — | OK；记录=Resizable：官方四配方、Changelo… |
| Scroll Area | 是 | scroll-area-r2 | 四象限+meta | 1xml/16 | — | OK；记录=Scroll Area：官方三配方、Horizo… |
| Scroll Fade | 是 | scroll-fade-r2 | 四象限+meta | 1xml/32 | — | OK |
| Select |  | select-r1 | 四象限+meta | 1xml/7 | — | OK；记录=Select：官网配方、Composition … |
| Separator |  | separator-r1 | 四象限+meta | 8xml/72 | — | OK；记录=Separator：官网配方、Compositi… |
| Sheet |  | sheet-r1 | 四象限+meta | 2xml/47 | — | OK；记录=Sheet：官网配方、Composition 与… |
| Shimmer |  | shimmer-r3 | 四象限+meta | 1xml/44 | — | OK；记录=Shimmer：官方小节文案、Reverse与四… |
| Sidebar |  | sidebar-r2 | 截图(26) | 7xml/50 | sources, meta.json, quadrants(0 files) | STALE；记录=Sidebar 第二轮内容区与分区布局 |
| Skeleton |  | skeleton-r2 | 四象限+meta | 0xml/0 | README, tests | MISSING |
| Slider |  | slider-r1 | 四象限+meta | 0xml/0 | README, tests | MISSING |
| Spinner | 是 | spinner-r2 | 四象限+meta | 2xml/15 | — | OK；记录=Spinner：八配方、三枚徽标与分节复查 |
| Stepper | 是 | stepper-r1 | 部分图(4)+meta | 1xml/3 | — | OK（扩展页，本地证据）；记录=Stepper：项目扩展标注、API 表与浏览器… |
| Switch | 是 | switch-r1 | 四象限+meta | 1xml/8 | — | OK；记录=Switch：当前七配方、Choice Card… |
| Table |  | table-r1 | 四象限+meta | 15xml/244 | — | OK；记录=Table：官网配方、组件默认与状态复核 |
| Tabs |  | tabs-r1 | 四象限+meta | 32xml/392 | — | OK；记录=Tabs：官网配方、组件默认与状态复核 |
| Text | 是 | text-r1 | 部分图(4)+meta | 1xml/5 | — | OK（扩展页，本地证据）；记录=Text：项目扩展 API 一致性与 Kotli… |
| Textarea | 是 | textarea-r1 | 四象限+meta | 1xml/7 | — | OK；记录=Textarea：当前六配方、扩展裁剪与分节对照 |
| Toast |  | toast-r2 | 四象限+meta | 1xml/24 | — | OK；记录=Toast：r2 官方文案、viewport-f… |
| Toggle |  | toggle-r1 | 四象限+meta | 1xml/5 | — | OK；记录=Toggle：官网配方、组件默认与状态复核 |
| Toggle Group |  | toggle-group-r1 | 四象限+meta | 1xml/5 | — | OK；记录=Toggle Group：r1 按当前官网重建八… |
| Tooltip |  | tooltip-r2 | 四象限+meta | 1xml/20 | — | OK；记录=Tooltip：r2 官方 Compositio… |

## [x] 证据门槛

- 17 个 `[x]` 全部达到门槛：README、来源（扩展页为“官方无页面”声明）、meta、完整四象限或 `sheet-all.png`、tests XML 均存在；最新记录日期均为 2026-11-05（`scroll-fade` 的记录标题为 `scroll-fade：…（r2）`，已按 slug 匹配）。

## 最高影响缺口

1. **Chart（`chart-page-r4`，未勾选）**：README/sources/tests 齐全，但 claimed 目录没有 `meta.json` 与四象限；图表截图证据分散在 `chart-preview-r3`/`chart-axis-r2`/`chart-r1` 等目录，单目录不能自证。gate 前应把当轮四象限复制进 chart-page-r4 或在 README 写明唯一证据入口。
2. **Sidebar（`sidebar-r2`，未勾选）**：无 `sources.json`/`meta.json`/标准四象限；但 `verification/green|final/` 下有 native 截图与 tests XML（SidebarContentLayoutParityTest），属“非标准路径证据”。应补 README 的固定 revision/来源与 meta，或把 verification 纳入标准结构。
3. **Skeleton（`skeleton-r2`）/ Slider（`slider-r1`，均未勾选）**：审计时刻目录仍在被并行写入（15 分钟内修改），当时分别缺 README/tests。属移动目标，gate 前复查即可。

## 说明与限制

- 本表为 2026-11-05 13:28 快照；提交前建议按同口径重跑（checklist/记录解析 → claimed `web-parity/<dir>` → 目录内容检查 → tests XML 汇总）。
- 未被组件行引用的 98 个目录不逐项解释：多为共享页面/主题/历史轮次证据（`theme-dark-r1-*`、`viewport-scroll-r1`、`page-layout-r1`、`chart-*`、各组件 `-r1..rN`）与并行临时目录。
- 判定为粗粒度只读解析：四象限按文件名规则识别，tests 统计目录内全部 XML 的 `tests` 属性之和；记录中的批次级数字（如“107 app + 62 core”）不作为单组件缺失。
- 未修改任何组件文件/证据；本目录仅包含本 README。
