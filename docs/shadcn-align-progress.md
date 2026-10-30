# shadcn 对齐进度

独立跟踪「把 `compose-foundation` 各组件对齐 [shadcn/ui](https://ui.shadcn.com) 实现」的任务进度。
Token / 已验证过的视觉对照仍以 `[shadcn-parity.md](shadcn-parity.md)` 为准；本文件只记**还在推进的工作**和每轮循环做了什么。

**对齐目标（2026-08-21 起）：Base UI** — 源码
`apps/v4/registry/bases/base/ui/*.tsx`，文档 `ui.shadcn.com/docs/components/base/*`，
默认风格组合 **base-nova**。`new-york-v4`（Radix/Vaul）仅作历史对照，与 Base 分歧时以 Base 为准。
详见 `vendor/README.md` / `AGENTS.md` / `shadcn-parity.md`。

以下 Tick 是此前对齐循环的历史日志，不表示当前有运行中的定时任务。

## Web 页面专项（2026-10-28）

本轮要求尽量复现 ui.shadcn.com 的实际页面与组件展示；
执行计划见 [shadcn-web-plan.md](shadcn-web-plan.md)，
逐组件状态与本轮验证见 [shadcn-web-progress.md](shadcn-web-progress.md)。
旧 Tick 不作为本轮视觉验收通过的依据。

### Web 第二批（2026-10-28）

已补顶栏全局搜索与浏览器 Cmd/Ctrl+K、字母顺序侧栏、手机 Sheet、宽屏章节目录、
Installation 与 Button/Badge API、组件页滚动恢复；Nova Badge、默认行内图标尺寸及 Spinner 内容色已修正。
共用弹层已补 Escape 处理。31 项定向测试、11 张显式像素校验通过。
旧 Badge 的 4 张基准差异经改动前源码对照，actual 图 SHA-256 全部相同；旧基准未更新。
完整浏览器证据、剩余差异和比较 JSON 见 [Web 进度](shadcn-web-progress.md)。

## 当前状态（本地核对：2026-10-28）

本节依据当前工作树核对，包含尚未提交的按钮波纹修复；本次没有重新抓取上游
registry，因此不将旧日志中的对齐结论视为对最新版上游的重新认证。

- 对齐目标仍是 **Base UI / base-nova**；已有对齐记录见下方 Tick 1–99，
  组件差异矩阵见 [shadcn-parity.md](shadcn-parity.md)。
- `Questionnaire` 已实现，含 Root `item` / `onItemChange`、Item `onStatusChange`；
  不再列为缺失组件或待补 API。
- `LocalGroupCorners` 已从源码删除。
- Chip 已使用 `rememberTheme` 缓存，并为标签提供当前 `LocalContentColor`；
  旧主题缓存与内容色问题不再作为待修复缺陷。
- Sidebar 已支持 Icon 折叠、Provider 内 Cmd/Ctrl+B、floating/inset、移动端 Sheet；
  默认宽度 256.dp、图标栏 48.dp、移动端 288.dp。持久化由 `onPersistOpen` 回调交给调用方。
- `SheetDefaults.ShowCloseButton = true`；ToggleGroup 默认 `spacing = 8.dp`。
  旧文档中“为保 golden 保留旧默认值”的说法已失效。
- Empty 默认虚线边框已实现。Chart 使用 Canvas 而非 Recharts 是平台实现差异，
  不作为缺少绘图组件的待办。
- 当前工作树已修复 Button 的 shape 波纹裁剪，新增 5 项像素回归与截图基准，
  覆盖圆形、非对称圆角、自定义路径及轮廓外阴影；本轮测试和跨平台编译已通过。

### 已核对的源码入口

- [ChipStyles.kt](../shadcn/core/src/commonMain/kotlin/com/toutiaoyan/compose/ui/components/chip/ChipStyles.kt)、
  [Chip.kt](../shadcn/core/src/commonMain/kotlin/com/toutiaoyan/compose/ui/components/chip/Chip.kt)、
  [RememberTheme.kt](../shadcn/core/src/commonMain/kotlin/com/toutiaoyan/compose/ui/utils/RememberTheme.kt)
- [Sidebar.kt](../shadcn/core/src/commonMain/kotlin/com/toutiaoyan/compose/ui/components/sidebar/Sidebar.kt)、
  [Sheet.kt](../shadcn/core/src/commonMain/kotlin/com/toutiaoyan/compose/ui/components/sheet/Sheet.kt)、
  [ToggleGroup.kt](../shadcn/core/src/commonMain/kotlin/com/toutiaoyan/compose/ui/components/togglegroup/ToggleGroup.kt)
- [Questionnaire.kt](../shadcn/core/src/commonMain/kotlin/com/toutiaoyan/compose/ui/components/questionnaire/Questionnaire.kt)、
  [Empty.kt](../shadcn/core/src/commonMain/kotlin/com/toutiaoyan/compose/ui/components/empty/Empty.kt)

### 刻意不做（不是缺口）

- `native-select` — 原生 HTML `<select>` 不在当前 Compose 绘制组件范围内。
- `direction` — 使用 Compose 的 `LocalLayoutDirection`。
- `form` — 并入 Field，没有引入 react-hook-form。
- `data-table` — 当前按 Table 组合使用，不作为单独的组件移植任务。
- `toast` — 现有实现走 sonner 对应的 Toast，不另建旧版同名组件。

## 待办（按优先级，仅保留未完成项）

1. **CI 显式执行截图校验。**
   [.github/workflows/ci.yml](../.github/workflows/ci.yml) 的 Desktop job 当前只运行
   `jvmTest --continue`；应显式接入 `:shadcn:core:verifyRoborazziJvm`，
   让像素差异成为 CI 失败条件。详见 [visual-testing.md](visual-testing.md)。
2. **核对旧截图基准与当前源码。**
   波纹修复验证期间发现部分现有按钮基准尺寸与当前渲染不同；应逐项确认原因后
   再更新，不以批量录制替代检查。本次没有改动这些旧基准。
3. **补充动态交互回归。**
   本轮已有静态 shape 下的按压像素覆盖；仍需补运行时主题/shape 切换、
   跨组件 hover 状态和弹层关闭后的焦点恢复等过程测试。
4. **建立公共 API / ABI 兼容性基准。**
   当前构建未接入 `apiCheck` / `apiDump` 或等效兼容性校验；正式发布前应明确
   导出 API 基准与破坏性变更检查流程。

---

## 循环日志（历史记录，非当前待办）

以下描述保留各 Tick 当时的状态；已完成事项以顶部的本地核对结果为准。

先前对话（[~228 ticks](890186cb-3c26-4cd4-b5c4-7d4a399f30b0)）没有单独进度文档，摘要：

- 组合 API：Dialog / Sheet / Drawer / Accordion / Popover / HoverCard / Tooltip / DropdownMenu / ContextMenu / Combobox / Select / Command / Menubar / NavigationMenu / Sidebar / Carousel / Calendar 等拆成官方槽位。
- 行为：`open` / `onOpenChange` / `defaultOpen`、hover 打开、focus Tooltip、Calendar range / 键盘漫游、Tabs `activationMode` 等。
- Catalog：大量官方槽位示例，默认预览从关闭/收起开始，避免漂像素。



### Tick 1 — 2026-08-20 06:12

- 新建本进度文档。
- 新增 `Questionnaire`（+ Progress / Item / Title / Description / Choices / Choice / Input / Error / Actions / Previous / Skip / Next / Submit；`questionnaireState()`）。
- Catalog 页（Default / Multiple / Freeform / Skip）+ `questionnaire_default_{light,dark}` 截图。
- `components.md` / CatalogRegistry / AGENTS / `component-metadata.json` 已同步。
- `compileKotlinJvm` 通过。



### Tick 2–4 — 2026-08-20 06:32（三次唤醒叠成一轮）

- **Questionnaire** `shortcuts`：`None` / `Letters` / `Numbers`；选项右侧显示 `Kbd`；按键选中但**不**自动下一题；输入框聚焦时不抢键。
- **QuestionnaireItem** `disabled`：条件题跳过，不计入 Progress。
- Catalog：Shortcuts、Conditional item；截图 `questionnaire_shortcuts_{light,dark}`。默认截图像素不变。
- `shadcn-parity.md` §4 补上 Questionnaire 行。



### Tick 5–6 — 2026-08-20 06:41（两次唤醒叠成一轮）

对照当前 `new-york-v4/ui` 源码扩 §4，并补两处默认不改像素的缺口：

- **DialogFooter** `showCloseButton`：官方新 prop，默认 `false`；为 `true` 时 Footer 末尾加 Outline「Close」。Catalog「Footer close button」。
- **ScrollAreaViewport**：官方 `focus-visible:ring-[3px]`；获焦才画 ring，未聚焦截图不变。
- **§4 新行**：Separator ✅、Dialog ✅、Sheet ⚠️（官方 Content 关闭按钮默认 `true`，我们仍是 `false`）、ScrollArea ✅、Calendar ⚠️（还没有 `captionLayout = dropdown`）。
- `LocalGroupCorners` 已不存在，从待办划掉。



### Tick 7 — 2026-08-20 06:45

- **Questionnaire resume**：Root `defaultItem`（`defaultValue` 的官方别名）；Choice `defaultChecked`；Input `defaultValue`。只 apply 一次。Catalog「Resume」+ `questionnaire_resume_{light,dark}`。
- **Carousel**：ArrowLeft / ArrowRight 翻页（`onPreviewKeyEvent`，不加 `focusable()`，避免画 ring）。未重录 carousel golden：工作区里 Button/icon 已有相对 HEAD 的像素差，与本次键盘无关。
- **§4**：Carousel ✅（`CarouselDots` 是我们多出来的）；Sidebar ⚠️（无 icon 折叠 / cookie / Cmd+B / 移动端 Sheet；宽度 240 vs 官方 256）。



### Tick 8–9 — 2026-08-20 06:53（两次唤醒叠成一轮）

- **ButtonGroup / ToggleGroup** `focus-visible:z-10`：组内 Button/Toggle 仅在获焦时 `zIndex(10)`，邻项不再裁 ring。未聚焦 modifier 链不变。
- `QuestionnaireChoiceDescription`：官方同名槽位；string Choice 的 `description` 仍转调它，默认截图应不变。
- **§4 Chart**：Container / Style / Legend / Tooltip 对齐；Bar/Line 是 Canvas 自绘（无 Recharts）。Legend `verticalAlign` 默认省略以保像素。



### Tick 10 — 2026-08-20 06:58

- **Calendar** `captionLayout`：默认 `Label`（现有截图不变）；`Dropdown` / `DropdownMonths` / `DropdownYears` 用短月名 + 年份菜单。Catalog「Dropdown caption」+ `calendar_caption_dropdown_{light,dark}`。



### Tick 11 — 2026-08-20 07:04

- **Sidebar** `collapsible = Icon`：收起时保留 48dp 窄轨（官方 `3rem`），仅 Icon 模式 `clipToBounds()`，隐藏 GroupLabel / Badge / Sub / GroupAction，菜单项自动用 label 做 tooltip。默认仍是 Offcanvas。Catalog「Icon collapse」+ `sidebar_icon_collapsed_{light,dark}`。Offcanvas collapsed golden 已 verify 通过。



### Tick 12 — 2026-08-20 07:10

- **Sidebar Cmd/Ctrl+B**：`SidebarProvider` 默认 `shortcutEnabled = true`、`shortcutKey = Key.B`。焦点在 Provider 子树内时切换开合（官方是 `window` 监听，Compose 做不到全局）。`collapsible = None` 不拦截。Catalog「Keyboard shortcut」。未新增 golden（不改默认像素）。



### Tick 13 — 2026-08-20 07:13

- **Sidebar** `variant`：默认 `Sidebar`（现有截图不变）；`Floating` 为 `p-2` + `rounded-lg` + border + `shadow-sm`；`Inset` 为 Provider `bg-sidebar`、内容区 `m-2`/`rounded-xl`/`shadow-sm`（展开时贴着侧栏一侧无间距）。Catalog「Floating」「Inset」+ `sidebar_floating_`* / `sidebar_inset_*`。



### Tick 14–15 — 2026-08-20 07:19（两次唤醒叠成一轮）

- **Questionnaire** `invalid` **/** `validate`：官方 Item `invalid` 立即显示 Error；`validate` 在 Next/Submit 时对答案返回错误文案。Catalog「Custom validation」+ `questionnaire_custom_validation_`*。
- **Sidebar** `mobile`：默认 `false`；为 `true` 时侧栏改为 Sheet  overlay（`collapsible = None` 除外）。Catalog「Mobile sheet」+ `sidebar_mobile_`*。无 `useIsMobile` 断点、无 cookie。



### Tick 16–17 — 2026-08-20 07:28（两次唤醒叠成一轮）

- **Questionnaire** `animate`：默认 `false`（现有截图不变）。为 `true` 时切题 `fade-in` + `slide-in-from-bottom-2`（300ms），对齐官方 `data-active` class。Catalog「Animated items」+ `questionnaire_animate_`*。
- **SidebarDefaults**：`Width` 256.dp（`16rem`）、`IconWidth` 48.dp、`MobileWidth` 288.dp、`CookieName` / 7 天。默认宽度仍是 240.dp。`onPersistOpen` 是 cookie 钩子（无 `document.cookie`）。Catalog「Official width」+ `sidebar_official_width_`*。



### Tick 18 — 2026-08-20 07:33

- **SheetContent** `width`：Start/End 默认仍是 320.dp（现有 Sheet golden 不变）。Sidebar `mobile` 传入 `SidebarDefaults.MobileWidth`（官方 `18rem` / 288.dp），重录 `sidebar_mobile_`*。



### Tick 19 — 2026-08-20 07:38

- **Questionnaire** `status` **/** `onStatusChange`：`Unanswered` / `Answered` / `Skip` 后为 `Skipped`；首次 Unanswered 不回调（对齐官方）。Root `item` / `onItemChange` 作为 `value` / `onValueChange` 别名。Catalog「Item status」+ `questionnaire_status_`*。默认截图不变。



### Tick 20 — 2026-08-20 07:43

- **QuestionnaireProgress 自定义文案**：官方 `render` / `current`/`total`/`first`/`last`。默认仍是 `Question X of Y`；传入 `text` 可改成 `Checkpoint 1 of 2`。语义标成 progressbar。Error 可选 `text` 覆盖默认必填文案（未 invalid 时仍隐藏）。Catalog「Custom progress」+ `questionnaire_custom_progress_`*。默认截图不变。



### Tick 21 — 2026-08-20 07:48

- **Choice** `checked` **/** `onCheckedChange`：对齐官方 Conditional 的受控 radio/`onChange`。未传 `checked` 时行为不变。Catalog「Conditional item」改为真正随选项禁用下一题。
- **Navigation state**：用 `questionnaireState().status` 关掉未作答时的 Next。Catalog「Navigation state」+ `questionnaire_navigation_state_`*。默认截图不变。



### Tick 22 — 2026-08-20 07:53

- **Card 组合**：对齐官方 Card 示例。Title / Description 放进 `CardHeader`，Progress + Item 放进 `CardContent`，Next/Submit 放进 `CardFooter`（Footer 已是 `RowScope`，导航按钮无需改 API）。Catalog「Card」+ `questionnaire_card_`*。默认截图不变。



### Tick 23 — 2026-08-20 07:59

- **Dialog 组合**：对齐官方「dismissal host-owned」。Catalog「Dialog」默认关闭（Trigger「Open clarification」）；打开后 Header 是宿主文案，问卷在 Content 里，Next/Submit 走 `DialogFooter`。截图 `questionnaire_dialog_`*（overlay 会拍到整窗，与现有 Dialog golden 相同）。默认问卷截图不变。



### Tick 24 — 2026-08-20 08:03

- **Root** `items`：官方同一份 collection 驱动顺序、进度、快捷键。默认 `emptyList()`，现有截图不变。Catalog「Items collection」+ `questionnaire_items_`*。



### Tick 25 — 2026-08-20 08:08

- **ChoiceInput / ChoiceLabel**：官方 unstyled 槽位。String Choice 仍不画原生 radio（默认截图不变），Label 抽成同名槽位。Catalog「Choice parts」+ `questionnaire_choice_parts_`*。



### Tick 26 — 2026-08-20 08:13

- **Reset**：对齐官方 Resume 的 `type="reset"`。`questionnaireState().reset` 恢复 `defaultItem` / `defaultChecked` / `defaultValue`；`QuestionnaireReset` 默认文案 `"Reset changes"`（Outline）。Catalog「Reset」+ `questionnaire_reset_`*。默认截图不变。



### Tick 27 — 2026-08-20 08:18

- **导航脱离 Actions**：官方 anatomy 把 Previous/Skip/Next/Submit 放在 Root 下，`QuestionnaireActions` 只是布局助手。补上无 `RowScope` 的重载；Actions 里仍用带 `weight` 的 Row 版。Catalog「Without Actions」+ `questionnaire_without_actions_`*。默认截图不变。



### Tick 28 — 2026-08-20 08:23

- **Root** `disabled`：官方 `disabled`。通过 `LocalFieldSetDisabled` 禁用选项 / 自由输入 / 导航按钮，不清除答案；快捷键忽略。Catalog「Disabled」+ `questionnaire_disabled_`*。
- **Input** `contentDescription`：官方 `aria-label`（placeholder 不算 accessible name）。Freeform 示例带上，像素不变。



### Tick 29 — 2026-08-20 08:28

- **键盘导航**（官方 Keyboard）：ArrowLeft 上一题；ArrowRight 仅在已答/已跳过时继续；Ctrl/Cmd+Enter 随时校验并继续/提交；输入框有内容时 Enter 同样继续。选项聚焦时不抢方向键。
- **Progress** `contentDescription` 默认 `"Questionnaire progress"`（官方 `aria-label`）。
- **aria-invalid**：Item / Choice 在有错误时 `semantics.error`。默认截图像素不变。



### Tick 30 — 2026-08-20 08:35

- **ArrowUp / ArrowDown**：官方在选项间漫游；单选会一并选中。空的自由输入也参与。
- **QuestionnaireInput** `type`：官方 `type`（默认 Text）；Catalog「Custom validation」用 `InputType.Email`。默认截图像素不变。



### Tick 31 — 2026-08-20 08:43

- **QuestionnaireChoices** `selectableGroup()`：单选题对齐官方 radio group（与 RadioGroup / ToggleGroup Single 相同）。
- **Choice** `selected` **语义**：暴露 aria-checked。
- **ButtonGroup** `contentDescription`：官方 `aria-label`；Catalog Default 用 `"Button group"`。默认截图像素不变。



### Tick 32 — 2026-08-20 08:47

- **ToggleGroupDefaults.Spacing = 8.dp**：官方 2026-05-17 起 `spacing` 默认 `2`。Composable 默认仍是 `0.dp` 以保 golden。Catalog「Spacing」改用该常量。
- **ToggleGroup / RadioGroup** `contentDescription`：官方 `aria-label`。RadioGroup Catalog Default 用 `"Plan"`。默认截图像素不变。



### Tick 33 — 2026-08-20 08:50

- **TabsList** `variant`：官方 `variant="line"`。`TabsListVariant.Default`（默认，muted 轨道 + 滑动胶囊，现有 golden 不变）/ `Line`（透明、无圆角、`gap-1`，指示器改成 2.dp `foreground` 底边/右边下划线）。
- Catalog「Line」+ 截图 `tabs_line_{light,dark}`。官方伪元素画在 trigger 外 5px；Compose 把线放在 tab bounds 底边内侧，避免被裁切。



### Tick 34 — 2026-08-20 08:56

- **Combobox** `multiple`：新增 `values` / `onValuesChange` 重载。选中项 toggle，弹层保持打开。默认单选 API 不变。
- **ComboboxChips / ComboboxChip / ComboboxChipsInput**：官方 chips 槽位。Chip `showRemove` 默认 `true`。**ComboboxCollection** 是 name-parity 透传。
- Catalog「Multiple」+ 截图 `combobox_chips_{light,dark}`（关闭态）。



### Tick 35–36 — 2026-08-20 09:04（两次唤醒叠成一轮）

- **ComboboxClear** + Trigger `showClear`（官方 ComboboxInput `showClear`，默认 false）。单选要传 Root `onClear` 才能清成 null；多选 Clear 直接清空 `values`。无选中时不画 X。Catalog「Clear button」+ `combobox_clear_{light,dark}`。
- **InputGroup** `contentDescription`（官方 `aria-label`）。Compose 没有 `Role.Group`。默认 null，现有 golden 不变。Catalog inline-start 用 `"Amount"`。



### Tick 37 — 2026-08-20 09:09

- **Badge** `variant="link"`：官方新变体（primary 文字，hover 下划线）。`BadgeVariant.Link` 默认关闭。Ghost 仍是 muted 底（官方 ghost 在 `<a>` 上才透明+hover）。Catalog「Link」。`badge_variants_`* 重录以纳入 Link；Default 单枚截图不变。



### Tick 38 — 2026-08-20 09:13

- **Card** `spacing`：官方 `--card-spacing`。默认 `null`，Default/Sm 像素不变（Sidebar 里的 `CardSize.Sm` 也不动）。传入后 gap 和 inset 共用同一 token。
- `CardDefaults.Spacing` **= 16.dp**（`--spacing(4)`）、`SmSpacing` **= 12.dp**（官方 `size="sm"`）。`CardSize.Sm` 仍只把区块间距从 8dp 收到 4dp。
- Catalog「Spacing」「Small spacing」+ 截图 `card_spacing_{light,dark}`。§4 补上 Card 行。



### Tick 39 — 2026-08-20 09:21

- **Item** `size="xs"`：官方文档已有、registry 源码还只有 default/sm。`ItemSize.Xs` 为 12×8 dp 内边距、8 dp 间距。Default/Sm 像素不变。Catalog「Extra small」+ `item_xs_{light,dark}`。
- **TabsList** `contentDescription`：官方 tablist `aria-label`。默认 null。Catalog Default 用 `"Account"`。



### Tick 40 — 2026-08-20 09:28

- **AlertAction**：官方右上角操作槽位。默认不传则 Default/Destructive 像素不变。Catalog「Action」+ `alert_action_{light,dark}`。§4 补上 Alert 行。



### Tick 41–42 — 2026-08-20 09:33（两次唤醒叠成一轮）

- **ComboboxTrigger** `showTrigger`：官方 ComboboxInput `showTrigger`，默认 `true`（chevron 仍在）。为 `false` 时不画触发图标。Catalog「Without trigger」+ `combobox_no_trigger_{light,dark}`。
- `itemToStringValue`：官方对象项的 label 函数，默认等于现有 `label`。Catalog「Custom items」。



### Tick 43 — 2026-08-20 09:38

- **AvatarGroupCount**：官方 `AvatarGroupCount`（muted `size-8` 圆 + `ring-2 ring-background`，文案如 `+3`）。默认不传则现有 Avatar 截图不变。Group 仍不自动 `-space-x-2` 重叠。
- Catalog「Group count」+ 截图 `avatar_group_count_{light,dark}`。§4 补上 Avatar 行。



### Tick 44 — 2026-08-20 09:43

- **AvatarGroup** `spacing`：官方 `-space-x-2`。默认 `0.dp`，现有 Group / GroupCount 截图不变。传入 `AvatarGroupDefaults.Overlap`（`-8.dp`）时重叠，并为成员 Avatar 加上 `ring-2 ring-background`。
- Catalog「Overlap」+ 截图 `avatar_group_overlap_{light,dark}`。



### Tick 45 — 2026-08-20 09:48

- **Combobox** `autoHighlight`：官方过滤后高亮第一项。默认 `false`，现有关闭态截图不变。查询非空时第一项用 hovered muted 底。未做完整键盘漫游。
- Catalog「Auto highlight」+ 截图 `combobox_auto_highlight_{light,dark}`（`defaultOpen` 打开弹层，harness 会拍整窗）。



### Tick 46 — 2026-08-20 09:55

- **EmptyVariant.Outline**：官方文档 Outline（加 `border`）。默认 Default 走原来的 `rememberEmptyStyle() then style`，不额外 `then` 空 Style。Compose 没有 dash primitive，描边是 1dp solid `border` token。
- Catalog「Outline」改用该变体；截图 `empty_outline_{light,dark}`。§4 补上 Empty 行。`empty_states` 相对 committed golden 已有高度差（更早未提交的 Title heading 等），不 record。



### Tick 47 — 2026-08-20 10:01

- **ProgressLabel / ProgressValue / ProgressTrack**：官方 Aria 文档槽位。不传 content lambda 时仍是原来的 8dp 轨道，`progress_states` 应不变。
- Catalog「Label and value」+ 截图 `progress_label_{light,dark}`。§4 补上 Progress 行。



### Tick 48 — 2026-08-20 10:07

- **Empty** `spacing`：官方根 `gap-6`（24dp）。默认 `null` 仍是 16dp `gap-4`，现有 `empty_outline` 像素不变。`EmptyDefaults.Spacing` = 24.dp。
- **EmptyMedia** `size`：官方 icon `size-10`（40dp）。默认 32dp `size-8`。`EmptyMediaDefaults.OfficialIconSize` = 40.dp。
- Table 已有 Caption / Footer，本轮不改。
- Catalog「Official spacing」+ 截图 `empty_spacing_{light,dark}`。§4 补上 Empty 行（Tick 46 漏写）。`empty_states` 相对 committed golden 已有高度差，不 record。



### Tick 49–50 — 2026-08-20 10:15（两次唤醒叠成一轮）

- **BubbleVariant.Tinted**：官方 `tinted`（浅色 primary wash + foreground 字）。默认仍是 Default，不改 `bubble_variants`。Compose 没有 `oklch(from --primary)`，用 primary `alpha` 0.12 / 暗色 0.3 近似。Catalog「Tinted」+ 截图 `bubble_tinted_{light,dark}`。§4 补上 Bubble 行。
- **EmptyContent** `spacing`：官方 `gap-4`（16dp）。默认仍是 ~10dp `gap-2.5`。`EmptyContentDefaults.Spacing` = 16.dp。Catalog「Content spacing」+ 截图 `empty_content_spacing_{light,dark}`。



### Tick 51–52 — 2026-08-20 10:27（两次唤醒叠成一轮）

- **EmptyTitle** `variant` **/** `EmptyTitleDefaults.Official`：官方 `text-lg`（18sp / TitleSmall）。默认仍是 BodyMedium `text-sm`。Catalog「Official title」+ 截图 `empty_title_lg_{light,dark}`。
- **MessageScrollerContent / MessageScrollerItem**：官方组合槽位。Content `spacing` 默认 `null`（0dp），`MessageScrollerDefaults.ContentSpacing` = 32.dp（`gap-8`）。Item `scrollAnchor` 接了 API，切题锚定尚未移植。现有 scroller 截图不变。Catalog「Content items」+ 截图 `message_scroller_content_items_{light,dark}`。§4 补上 MessageScroller 行。



### Tick 53 — 2026-08-20 10:32

- **BubbleReactions** `side` **/** `align`：官方 `side` top/bottom（默认 Bottom）、`align` start/end（默认 End）。默认仍是 BottomEnd + 向下偏移，`conversation_with_reactions` 不变。`side = Top` 时 pill 从气泡上沿探出。Catalog「Reactions top」+ 截图 `bubble_reactions_top_{light,dark}`。



### Tick 54 — 2026-08-20 10:35

- **SkeletonVariant.Accent**：官方 `bg-accent`。默认仍是 `bg-muted`，`skeleton_states` 不变。Catalog「Accent」+ 截图 `skeleton_accent_{light,dark}`。§4 补上 Skeleton 行。



### Tick 55 — 2026-08-20 10:38

- **Empty** `padding`：官方 `md:p-12`（48dp）。默认 `null` 仍是 Style 里的 `p-6`（24dp）。`EmptyDefaults.MdPadding` = 48.dp。不要在默认路径上 `then` 空 Style。Catalog「Large padding」+ 截图 `empty_md_padding_{light,dark}`。



### Tick 56 — 2026-08-20 10:43

- **MessageScrollerButton** `direction = Start`：官方回到顶部按钮（箭头旋转 180°）。默认仍是 End。Root 的 `visible` 仍按「是否靠近底部」计算，Start 由调用方自己传 `visible`。Catalog「Start button」+ 截图 `message_scroller_button_start_{light,dark}`。



### Tick 57 — 2026-08-20 10:48

- **Tooltip** `showArrow`：官方 Content 里始终有 Radix `Arrow`（`size-2.5 rotate-45` 的 foreground 菱形）。默认 `false`，`tooltip_trigger` 不变。`TooltipDefaults.ShowArrow` = true。Catalog「Arrow」+ 截图 `tooltip_arrow_{light,dark}`（`open = true` + Bottom，会拍到整窗，与 Popover golden 相同）。§4 补上 Tooltip 行。



### Tick 58 — 2026-08-20 10:53

- **Empty** `radius`：官方根 `rounded-lg`。默认 `null` 仍是 Style 里的 `rounded-xl`。`EmptyDefaults.Radius` 读主题 `twRadius.lg`。Catalog「Official radius」用 Outline 把圆角差画出来 + 截图 `empty_rounded_lg_{light,dark}`。`empty_outline` 默认路径不变。



### Tick 59 — 2026-08-20 10:58

- **ResizableHandle** `withHandle`：官方缺省为假（只有 1px 分割线）。默认仍是 `true`（带 grip），现有 `resizable_states` 不变。`ResizableDefaults.WithHandle` = false。Catalog「Without handle」改用该常量 + 截图 `resizable_without_handle_{light,dark}`。§4 补上 Resizable 行。



### Tick 60 — 2026-08-20 11:03

- **ChartLegend** `verticalAlign`：官方默认 `bottom`（`pt-3` = 12dp）。默认仍省略以免改 `chart_bar_states`。`ChartLegendDefaults.VerticalAlign` = Bottom。Catalog「Legend bottom」改用该常量 + 截图 `chart_legend_bottom_{light,dark}`。



### Tick 61 — 2026-08-20 11:08

- **SheetContent** `showCloseButton`：官方默认 `true`。我们默认仍是 `false`，`sheet_states` 不变。`SheetDefaults.ShowCloseButton` = true。Catalog「Close button」改用该常量 + 截图 `sheet_close_button_{light,dark}`（overlay 会拍到整窗）。



### Tick 62 — 2026-08-20 11:13

- **Tooltip** `sideOffset`：当前官方 registry 默认 `0`。我们默认仍是 `4.dp`。`TooltipDefaults.SideOffset` = 0.dp。Catalog「Official side offset」+ 截图 `tooltip_side_offset_0_{light,dark}`（`open = true` + Bottom）。



### Tick 63 — 2026-08-20 11:18

- **SelectContent** `position`：官方默认 `item-aligned`（选中项叠在 Trigger 上）。我们默认仍是 Popper（贴边 + 4.dp）。`SelectContentDefaults.Position` = ItemAligned：offset 0、无 fade；选中项与 Trigger 重叠尚未移植。Catalog「Align item with trigger」+ 截图 `select_item_aligned_{light,dark}`（`open = true`，会拍到整窗）。`select_closed_light` 相对 golden 变宽是既有漂移（Trigger 内 `weight(1f)`），未 re-record。



### Tick 64 — 2026-08-20 11:21

- **EmptyMedia** `variant="default"`：官方默认透明槽。我们默认仍是 Icon 徽章。`EmptyMediaDefaults.Variant` = Default。Catalog「Media variant」改用该常量 + 截图 `empty_media_default_{light,dark}`。`empty_states` 相对 golden 仍有高度差（Tick 46 起已知），未 re-record。默认根改为 `styleable(composed, style)`，不再 `then` 空 Style；`empty_outline` / `empty_rounded_lg` verify 通过。



### Tick 65–66 — 2026-08-20 11:29（两次唤醒叠成一轮）

- **ChartContainer** `initialDimension`：官方 Recharts `ResponsiveContainer` 默认 `{ width: 320, height: 200 }`。我们默认仍是 `fillMaxWidth` + 16:9。`ChartContainerDefaults.InitialDimension` = 320×200。Catalog「Official size」+ 截图 `chart_initial_dimension_{light,dark}`。
- **DrawerContent** `showHandle`：Bottom 默认仍画 handle。`DrawerDefaults.ShowHandle` = false。Catalog「Without handle」改用该常量 + 截图 `drawer_without_handle_{light,dark}`（overlay 会拍到整窗）。



### Tick 67 — 2026-08-20 11:36

- **MessageScrollerProvider / MessageScrollerViewport**：官方组合树。Provider 是透传；Viewport 是 Root 滚动列里的 landmark 槽，`contentDescription` 默认空，`MessageScrollerDefaults.ViewportLabel` = `"Messages"`。`preserveScrollOnPrepend` 接了 API，前置稳定尚未移植。滚动仍在 Root。Catalog「Official tree」+ 截图 `message_scroller_viewport_{light,dark}`。



### Tick 68 — 2026-08-20 11:40

- **AlertDialogContent** `showCloseButton`：官方 AlertDialog 没有 X。我们默认仍是 `false`。`AlertDialogDefaults.ShowCloseButton` = true（Dialog Content 官方关闭按钮）。Catalog「Close button」改用该常量 + 截图 `alert_dialog_close_button_{light,dark}`（overlay 会拍到整窗）。



### Tick 69 — 2026-08-20 11:43

- **CommandDialog** `showCloseButton`：官方 palette 默认没有 X。我们默认仍是 `false`。`CommandDefaults.ShowCloseButton` = true（Dialog Content 官方关闭按钮）。Catalog「Close button」改用该常量 + 截图 `command_dialog_close_button_{light,dark}`（overlay 会拍到整窗）。



### Tick 70 — 2026-08-20 11:48

- **DrawerContent** `showCloseButton`：官方 Drawer 默认没有 X。我们默认仍是 `false`。`DrawerDefaults.ShowCloseButton` = true。Catalog「Close button」改用该常量 + 截图 `drawer_close_button_{light,dark}`（overlay 会拍到整窗）。

### Tick 71 — 2026-08-20 16:38

去掉「为保 golden 不改默认像素」这条自定约束。官方默认接到 composable 默认值上，并重录对应截图：

- 删除 `SkeletonVariant`（没有官方 variant）；Skeleton 默认 `bg-accent`
- Sheet `showCloseButton`、Tooltip `showArrow` / `sideOffset`、ToggleGroup `spacing`、Resizable `withHandle`、ChartLegend `verticalAlign`、Empty（gap / radius / Media / Title / Content）、Card `--card-spacing`、AvatarGroup overlap、Select `item-aligned`、Sidebar `16rem`、MessageScroller Content `gap-8` / Viewport label
- AlertDialog / Command / Drawer 的关闭按钮仍默认 `false`（官方就没有 X）
- ChartContainer `320×200`、Empty `md:p-12` 仍是 opt-in（分别是 Recharts 测量提示和断点样式）

### Tick 72 — 2026-08-20 17:30

Overlay 公共 API 对齐官方 `new-york-v4` exports（dialog / sheet / drawer / popover / dropdown-menu / select）：

- **Sheet**：`SheetPortal` / `SheetOverlay` → internal；去掉公开 `direction` / `width`（仅 `side`；Start/End 内置 ~320.dp，可用 `modifier` 覆盖）
- **Drawer**：去掉公开 `DrawerHandle` / `DrawerIndicator` / `showHandle` / `showCloseButton` / `DrawerDefaults`；Bottom Content 始终画 handle；去掉 Root `side` 别名
- **Popover**：去掉公开 `PopoverClose`；`PopoverPortal` → internal；Content 增加 `align` 默认 Center
- **DropdownMenu**：`DropdownMenuItemIndicator` → internal（Checkbox/Radio 内联）
- **Select**：`SelectPortal` / `Viewport` / `Icon` / `ItemText` / `ItemIndicator` → internal；Trigger 始终 chevron；Content 组合 Portal+Viewport+ScrollButtons，并加 `align` Center
- Catalog / `components.md` / `shadcn-parity.md` §4 已同步；去掉非官方槽位示例与 Drawer hide-handle / close 截图用例

### Tick 73 — 2026-08-21

**Drawer 对齐目标从 Vaul/Radix 切到 Base UI / base-nova**（Compose 仍用 children，不用 `render=`）：

- Root：`direction` → `swipeDirection`（`Up`/`Right`/`Down`/`Left`，默认 `Down`）；新增 `showSwipeHandle`（默认 `false`）
- 公开 `DrawerSwipeHandle`；Content 仅在 `showSwipeHandle` 时绘制
- Trigger/Close：文档与用法改为 `DrawerTrigger { Button(..., onClick = toggle) }` / `DrawerClose { Button(..., onClick = close) }`；另保留 `DrawerCloseIcon` 与字符串便捷重载
- `components.md` / `shadcn-parity.md` / Catalog `DrawerDoc` 已同步

### Tick 74 — 2026-08-21

**Drawer 剩余 Base / base-nova 对齐**：

- Root：`snapPoints` / `snapPoint` / `onSnapPointChange` / `snapToSequentialPoints`；`disablePointerDismissal`；去掉未用的 `dismissOnSwipeDown` / `dismissOnSwipeEnd`
- 垂直 snap 面板（多 anchor + 关闭态）；嵌套 open 时父级 scale/peek（`--stack-step` / `--peek` 近似）
- 尺寸：垂直 `maxH − 6rem`；侧栏 `min(75%, 24rem)`
- Catalog：Disable pointer dismissal / Snap points / Nested；unit tests for snap resolve
- 未做（base-nova 也未 export）：`Drawer.Provider` / Indent / SwipeArea / VirtualKeyboardProvider

### Tick 75 — 2026-08-21

**Drawer 对齐目标确认 = Base UI / base-nova（不是 Radix/Vaul）**；Compose 继续用 children 组合：

- 补回 Tick 74 Root 缺口：`DrawerSnapPoint.kt` + `Drawer` 的 snap / nest / `disablePointerDismissal`（此前 Content 已写、Root 曾回退）
- **Trigger/Close**：children 改为 **passthrough host slot**（不再外包一层 `clickable`，避免 Button 套 Button）；字符串重载直接渲染 `Button` + `toggle`/`close`
- 推荐写法：`DrawerTrigger { Button(..., onClick = toggle) }` / `DrawerClose { Button(..., onClick = close) }`（对应 Web `render={<Button/>}`）
- `components.md` / `shadcn-parity.md` 已同步

### Tick 76 — 2026-08-21

**Drawer 面板贴边 + 点外部关闭**：

- 根因：`DrawerContentImpl` 的 `BoxWithConstraints(fillMaxSize)` 把 `TeleportPrimitive.Content` 的吞点击层撑满整窗，挡住 Overlay
- 去掉该 `fillMaxSize`（与 Sheet 一样只把面板放进 Content）；贴边靠 Content 的 `contentAlignment`；`constraints.max*` 仍来自外层全窗 Box
- Roborazzi `DrawerScreenshotTest` 已 re-record

### Tick 75 — 2026-08-21

**全局对齐目标改为 Base UI**（不再以 `new-york-v4` / Radix 为主）：

- `AGENTS.md`：对齐目标写明 `bases/base/ui` + base-nova；registry / fetch URL 改指向 Base
- `shadcn-parity.md`：Sources of truth 改为 Base；`new-york-v4` 标为 legacy
- `shadcn-align-progress.md`：当前状态与待办同步；`vendor/README.md` 此前已写明
- 组件实现：仅 **Drawer** 已按 Base 重对齐；其余仍待对照 `bases/base/ui` 扫差异

### Tick 76 — 2026-08-21

**Overlay 族第一批 Base UI 差异修复**（对照 `bases/base/ui`）：

- **Tooltip**：`sideOffset` 默认 **4.dp**（Base，非旧 Radix 0）；`TooltipPortal` / `TooltipArrow` → internal；去掉 Catalog Portal 示例；截图 `tooltip_side_offset_4`
- **Popover**：`PopoverAnchor` → internal（Base 不 export）；去掉 Catalog Anchor 示例
- **Select**：`alignItemWithTrigger` API + `AlignItemWithTrigger` 枚举；对齐时仍保留 `sideOffset` 4（Base）；`ItemAligned` deprecated
- **DropdownMenu**：`SubContent` `sideOffset` 默认 **0.dp**（Base）
- Dialog / Sheet 导出面与 Base 已基本一致（本轮未改代码）
- Docs：`components.md` / `shadcn-parity.md` 已同步

### Tick 77 — 2026-08-21

- **AlertDialogMedia**：Base export 槽位落地（`size-10` `bg-muted` `rounded-md`）
- 删除过时 golden `tooltip_side_offset_0_*`
- 5min 循环已启动（`baseui-align`）

### Tick 78 — 2026-08-21

**Sheet / HoverCard / Combobox → Base UI**：

- **Sheet**：导出面已与 Base 一致（Portal/Overlay internal；`showCloseButton` true）— 仅文档确认
- **HoverCard**：Content 增加 `align` 默认 Center；`HoverCardPortal` → internal；去掉 Catalog Portal 示例
- **Combobox**：Content `sideOffset` **6.dp** + `align` Start；Portal / Icon / Clear / ItemIndicator → internal；Catalog 同步
- compile OK（`--no-build-cache`）

### Tick 79 — 2026-08-21（loop tick）

**Menubar / ContextMenu / Command → Base UI**：

- **MenubarContent** `sideOffset` 默认 **8.dp**；**MenubarSubContent** 默认 **0.dp**；`MenubarItemIndicator` → internal
- **ContextMenuSubContent** 默认 **0.dp**；`ContextMenuItemIndicator` → internal
- **Command**：导出面核对 — Footer/Loading/GroupHeading 为 Compose 辅助（Base 未 export）；Dialog `showCloseButton` 默认 false 已对齐
- Catalog / parity / components 已同步

### Tick 80 — 2026-08-21（loop tick 2）

**NavigationMenu / Accordion / Collapsible / Tabs → Base UI**：

- **NavigationMenu**：Content `sideOffset` **8.dp**；公开 `NavigationMenuPositioner`（`Viewport` deprecated）
- **Accordion**：`AccordionHeader` / `AccordionItemIndicator` → internal
- **Collapsible**：`CollapsibleHeader` → internal
- **Tabs**：`TabsIndicator` → internal（List 默认仍绘制）
- Catalog / parity / components 已同步；compile OK

### Tick 81 — 2026-08-21（loop tick 3）

**表单控件导出面 → Base UI**：

- **Checkbox**：`CheckboxIndicator` → internal（Base 只 export `Checkbox`）
- **Switch**：`SwitchThumb` → internal
- **RadioGroup**：`RadioGroupIndicator` → internal（仍 export `RadioGroup` / `RadioGroupItem`）
- **Slider**：`SliderTrack` / `SliderRange` / `SliderThumb` → internal（Base 只 export `Slider`）
- **Progress / Toggle / ToggleGroup**：导出面与 Base 已一致（本轮未改）
- Catalog 去掉 Indicator / Track-Range-Thumb 示例；`components.md` / `shadcn-parity.md` 已同步

### Tick 82 — 2026-08-21（loop tick 4）

**ScrollArea / Pagination / Calendar 核对 → Base UI**：

- **ScrollArea**：`ScrollAreaViewport` / `ScrollAreaCorner` → internal（Base 只 export `ScrollArea` + `ScrollBar`）；去掉 Catalog Viewport 示例
- **Pagination**：核心导出与 Base 一致；`PaginationFirst` / `Last` 标为 Compose helpers（`showEdges`）
- **Calendar**：Base 另 export `CalendarDayButton` — Compose 仍为 private（Grid 内使用）；parity 记 ⚠️
- Avatar / Empty / Field / Card / InputGroup 导出面已基本对齐（本轮未改）
- Docs：`components.md` / `shadcn-parity.md` 已同步

### Tick 83 — 2026-08-21（loop tick 5）

**Calendar DayButton + 扫尾核对**：

- **Calendar**：`CalendarCell` / `CalendarDayButton` → **public**（对齐 Base export）
- **Carousel**：`CarouselDots` 仍为 Compose helper（Catalog / parity 已注明；Base 无 dots）
- **Dialog / AlertDialog / Sheet / Chart / Table / InputOTP**：导出面与 Base 核对 — 基本一致（Dialog/AlertDialog 保留 Portal/Overlay；Sheet 不 export）
- **已知未移植（本轮不实现）**：`NativeSelect`（web `<select>`）、`DirectionProvider`（Base re-export）、`Sonner` 已由 `Toaster`/`ShadcnToast` 覆盖
- Docs：`components.md` / `shadcn-parity.md` / progress 已同步

### Tick 84 — 2026-08-21（loop tick 6）

**Attachment → Base UI 补齐导出**：

- 新增 **`AttachmentAction`**（默认 Ghost + `IconXs`，对齐 Base）
- 新增 **`AttachmentTrigger`**（`BoxScope` + `matchParentSize`）；`Attachment(onClick=)` 内部走 Trigger；Actions 画在 Trigger 之上以免挡点击
- Catalog 移除按钮改用 `AttachmentAction`
- `components.md` 已同步

### Tick 85 — 2026-08-21（loop tick 7–8）

**HoverCard `alignOffset` → Base UI**：

- Base Content 默认 `alignOffset = 4`；Compose `HoverCardContent` 增加同名参数默认 **4.dp**
- `AnchoredPopup` / `PopupPositionProvider` 支持 `alignOffset`（其它 overlay 默认 0，行为不变）
- Header/Title/Description 标为 Compose helpers（Base 只 export Root/Trigger/Content）
- Docs：`components.md` / `shadcn-parity.md` 已同步

### Tick 86 — 2026-08-21（loop tick 9）

**Overlay 族 `alignOffset` → Base UI**：

- **Tooltip**：`align` Center + `alignOffset` 0.dp（此前未传 align，默认成了 Start）
- **Popover / Combobox / Select / NavigationMenu**：`alignOffset` 默认 0.dp
- **DropdownMenuContent** `alignOffset` 0；**SubContent** 默认 **(-3).dp**（Base）
- **MenubarContent** `alignOffset` 默认 **(-4).dp**（Base）
- Docs：`shadcn-parity.md` 已同步

### Tick 87 — 2026-08-21（loop tick 10）

**ContextMenu / Menubar Sub → Base `alignOffset`**：

- **ContextMenuContent**：`sideOffset` 0 + `alignOffset` **4.dp**（相对点击点的十字轴偏移，对齐 Base Positioner）
- **ContextMenuSubContent**：显式 `alignOffset` **4.dp**（Base 复用 Content，不是 Dropdown Sub 的 -3）
- **MenubarSubContent**：显式转发 `alignOffset` **(-3).dp**（与 Dropdown Sub 一致）
- Docs：`shadcn-parity.md` 已同步

### Tick 88 — 2026-08-21（loop tick 11）

**Base 对齐收口（文档 / 状态）**：

- 更新 `shadcn-align-progress.md` 当前状态：Tick 75–87 主路径已完成
- **Select / NavigationMenu** parity 补 `alignOffset` 0
- **Questionnaire**：`Reset` / `ChoiceInput` / `ChoiceLabel` / `ChoiceShortcut` 标为 Compose helpers（Base 未 export）
- 待扫改为可选平台差异项（Empty dash / Chart / NativeSelect）

### Tick 89 — 2026-08-21（loop tick 12）

**Empty → base-nova**：

- Root 默认对齐 `.cn-empty`：`gap-4`（16.dp）、`rounded-xl`、`border-dashed`（`PathEffect`）、`p-6`
- Media Icon `size-8`（32.dp）；Title `text-sm`；Content `gap-2.5`（10.dp）
- Outline 仍为 solid 1dp（覆盖虚线）
- Docs / parity / screenshot 名：`empty_title_sm` / `empty_rounded_xl`

### Tick 90 — 2026-08-22（loop tick 13）

**Accordion 触发图标 → Base UI**：

- 默认 indicator：折叠 **ChevronDown** / 展开 **ChevronUp**（双图标显隐，对齐 Base `group-aria-expanded`）
- 新增 `ChevronDown` / `ChevronUp` ImageVector（由既有 ChevronRight 旋转变换）
- 不再用 ChevronRight + `animateFloatAsState` 旋转

### Tick 91 — 2026-08-22（loop tick 14）

**Skeleton + Attachment Idle → base-nova**：

- **Skeleton** 默认填充 `bg-muted`（`.cn-skeleton`），不再用 `bg-accent`
- 共享 `Modifier.dashedBorder`（`styles/Border.kt`）；Empty 的 `emptyDashedBorder` 改为别名
- **Attachment** Idle 状态使用虚线边框（Base `data-[state=idle]:border-dashed`）

### Tick 92 — 2026-08-22（loop tick 15）

**Progress → base-nova**：

- Track 高度 **`h-1`（4.dp）**，背景 **`bg-muted`**（不再是 `h-2` + `primary/20`）
- `ProgressDefaults.TrackHeight`；Label 默认 `font-medium`

### Tick 93 — 2026-08-22（loop tick 16）

**Slider → base-nova**：

- Thumb **`size-3`（12.dp）**；Track **`h-1`（4.dp）**
- Thumb 边框 `border-ring`（不再用 primary）
- `SliderDefaults.ThumbSize` / `TrackHeight`

### Tick 94 — 2026-08-22（loop tick 17）

**Input → base-nova**：

- 高度 **`h-8`（32.dp）**（不再是 `h-9` / 36.dp）
- 圆角 **`rounded-lg`**；内边距 **`px-2.5 py-1`**
- `InputDefaults.Height`

### Tick 95 — 2026-08-22（loop tick 18）

**SelectTrigger → base-nova**：

- Default 高度 **`h-8`（32.dp）**；Sm **`h-7`（28.dp）**（不再是 36 / 32）
- 圆角 **`rounded-lg`**；`SelectDefaults.TriggerHeight` / `TriggerHeightSm`

### Tick 96 — 2026-08-22（loop tick 19）

**Button / Toggle → base-nova 尺寸**：

- **Button**：Md `h-8`、Sm `h-7`、Lg `h-9`；Icon `size-8` / IconSm `size-7` / IconLg `size-9`；默认圆角 ring `rounded-lg`
- **Toggle**：Md `h-8`、Sm `h-7`、Lg `h-9`（原 36/32/40）


### Tick 97 — 2026-08-22（loop tick 20）

**TabsList / InputGroup / ComboboxChips → base-nova**：

- **TabsList**（Default + 横向）：高度 **`h-8`（32.dp）**
- **InputGroup**：`minHeight` **`h-8`**、圆角 **`rounded-lg`**（block addon 仍可长高）
- **ComboboxChips**：`min-h-8`、`rounded-lg`、`py-1`

### Tick 98 — 2026-08-22（loop tick 21）

**Dialog / AlertDialog panel → base-nova**：

- 圆角 **`rounded-xl`**；内边距 **`p-4`**（不再是 `lg` / `p-6`）
- 背景 **`bg-popover`**；Default 宽度 **384.dp**（`sm:max-w-sm`）

### Tick 99 — 2026-08-22

**SidebarMenu → Base UI `sidebar.tsx` / base-nova**：

- `SidebarMenu` 对齐官方 `ul`：`flex w-full min-w-0 flex-col`，nova **`gap-0`**（不再 `spacedBy(xs2)`）
- `SidebarMenuItem` 是 `relative` 容器；Button / Action / Badge / Sub 为兄弟槽位（Action/Badge `absolute right-1`）
- 选中态在 **`SidebarMenuButton.isActive`**（官方）；`SidebarMenu(activeId, onItemClick)` 仍可作为 `(value, label)` 行的便利 overload
- MenuButton：**`h-8` / `h-7` / `h-12`**，hover/active `sidebar-accent`，默认字色 `sidebar-foreground`（不再 muted），icon-collapsed tooltip 仅在收起轨时显示
- Sub：`border-l` + `mx-3.5` / `px-2.5` / `gap-1`；SubButton **`h-7`**


### 2026-10-29 — Web 对齐第三批

Card Nova 标题/描述、右侧 Action、满宽 Footer 已修复；新增 Code Block 与 Trace 公开组件、目录和参数表。
59 项定向回归、19 张相关像素快照通过（本批新增 8 张），JVM/JS/Wasm 与 Web distribution 通过。
Wasm 代码区追加 Geist Mono 后又通过目录回归与 Web 构建。三个页面已检查桌面/手机浅深主题和基础交互。
完整状态矩阵、官方扩展示例和 Input/Field 等后续批次仍未关闭。
最新范围与截图见 [Web 进度](shadcn-web-progress.md)，不把历史 Tick 当作本轮验收证据。


### 2026-10-29 — Web 对齐第四批

Input / Field / Label / Textarea 的 Nova 基础字号、尺寸、间距、底色、错误/禁用状态及默认演示已更新；补支付表单及 API 表。
修复演示 autoFocus 加载时抢焦点/跳页，API Key 默认密码遮挡与官方一致。
本批 35 项定向检查、6 张新增像素快照通过，JVM/JS/Wasm 及 Web distribution 通过；此前相关回归一并重跑。
旧表单 25 张失配已用改动前源码复现，最终实际渲染哈希全部一致，旧基准未刷新。
复杂 Field 布局和完整状态矩阵仍未关闭，详见 [Web 进度](shadcn-web-progress.md)。


### 2026-10-29 — Web 对齐第五批

Checkbox / Radio Group / Switch / Toggle 的 Nova 基础样式、错误 ring、默认示例和 API 表已更新。
标签点击会聚焦真实控件；Radio 补方向键循环、禁用跳过、RTL/Home/End；Toggle 新增兼容 invalid 重载。
Nova 默认开启焦点 ring，显式关闭仍有效。本批 54 项定向检查、10 张新快照通过，跨 JVM/JS/Wasm 构建通过。
旧 Toggle 8 张失配已用改动前实现复现且哈希相同，基准未刷新；Tab 单入口及复杂组合仍未关闭。
最新范围及截图见 [Web 进度](shadcn-web-progress.md)。


## 2026-10-29 · Web 第六批

Toggle Group / Slider / Input Group / Button Group 的 Nova 基础样式、默认示例与键盘/布局修正已落地。Slider 在关闭触控最小尺寸时保持可见，Input Group 按 border-box 为 32dp，嵌套图标按钮按实际高度测量。详细范围、尚未关闭的矩阵和历史快照对照见 [Web 进度](shadcn-web-progress.md#第六批实现2026-10-29)。原组件源码复现了 22 张旧基准差异，未批量刷新。


## 2026-10-29 · Web 第七批

Input OTP / Select / Combobox 的基础 Nova 对齐已落地：OTP 连体格及选择范围、Select 当前行对齐和键盘链路、可编辑 Combobox 新入口。修复 Web 中 Enter 释放使 Select 重新打开，以及 inline addon 按钮把 Input Group 撑高的问题。92 项相关检查、12 张新快照、JS/Wasm 构建通过。旧 Select/Combobox 17 张历史失配与改动前实际 PNG 一致，旧 OTP 7 项验证通过。完整边界与截图见 [Web 进度](shadcn-web-progress.md#第七批实现2026-10-29)。


### 2026-10-29：Web 第八批六个组件

- Collapsible、Command、Context Menu、Date Picker recipe、Dialog、Drawer 的默认示例和主要 Nova 样式已按官方页面更新。
- 修复 Command 自定义 value/重组高亮、Collapsible 根禁用、Drawer 桌面宽度约束；增加 Context Menu 键盘/长按入口与子菜单导航。
- Drawer 新增 `DrawerFooterColumn`，原 `DrawerFooter` RowScope 签名保留；Dialog 默认 recipe 补手机逆序纵向动作。
- 108 项相关检查、8 张新 Nova 像素基准及 JVM/JS/Wasm 构建通过；旧主题 13 张历史失配的修改前后 SHA-256 完全相同。
- 三种宽度、浅深主题及交互截图索引：`web-parity/phase8-evidence.json`。完整范围与真实残差见 `shadcn-web-progress.md` 第八批，未将本批基础完成等同于整站验收完成。


### 2026-10-29：Web 第九批，六组件剩余实现

- Collapsible / Command / Context Menu / Date Picker / Dialog / Drawer 的上批 Web 缺口已补齐；清单按 Web 对照范围关闭。
- 新增窗口根 `OverlayHost`：4dp 背景模糊、嵌套背景捕获、非模态 portal 与不消费输入的外部点击监听。
- 修复初始/恢复/Tab 焦点、隐藏内容焦点、长列表滚动、受控值、IME 组合保护、RTL/字母定位/窗口坐标、顺序吸附实际手势和日期时区/locale/范围/月界限。
- 151 项相关检查与显式像素 verify 通过，8 张新基准；4 张 Nova Dialog/Drawer 图仅按已检查的初始焦点变化更新。
- 旧预设 21 项有 15 项失配（13 项历史问题 + 2 项 Drawer 焦点变化），保持旧 golden；本批没有用批量重录掩盖历史差异。
- 浏览器证据 `web-parity/phase9-evidence.json`；Android/iOS 原生 blur、读屏和真实系统输入法仍列设备实测，未宣称全原生平台发布验收完成。
