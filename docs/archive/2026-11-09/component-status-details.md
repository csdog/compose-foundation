# 逐组件验收范围与证据：整理前快照

记录于 2026-11-09。保留原清单中的范围、限制与证据；当前勾选状态见[验收清单](../../shadcn-web-progress.md)。

## accordion

- [x] Accordion (`accordion`)：r14 将当前官方 `main` 固定为 `42f3bb63…`，MDX、wrapper 和七个示例的 9 份来源哈希与 r13 一致，并在 r14 新增 Nova CSS 固定；页面官方顺序（hero、Installation、Usage、Composition、Basic、Multiple、Disabled、Borders、Card、RTL、API Reference）已补完整回归断言。目录 63/63、core 21/21 通过；当前 Wasm `1355c1d5…` 下完成四象限、48 张六分节和 17 个交互取证，10 份来源及全部图片哈希由 [validation.json](../../web-parity/accordion-r14/validation.json) 校验。共享文档壳/代码面板、Geist 与阿拉伯/希伯来字形栅格、Canvas 原生辅助技术角色以及扩展/真机触摸矩阵作为明确平台差异，见 [accordion-r14](../../web-parity/accordion-r14/README.md)。

## alert

- [x] Alert (`alert`)：r12 将当前官方 `main` 固定为 `42f3bb63…`，核对 MDX、`alert.tsx`、六个示例和 Nova CSS 与 r11 的 `8a353667…` 文件字节一致；14 项 core、6 项目录测试通过，并在当前 Wasm 构建上复查 1440/390×浅深官方/本地四象限。HTML role、任意富文本子节点、共享文档壳及字形栅格化作为明确平台/API 差异记录，见 [alert-r12](../../web-parity/alert-r12/README.md)。

## alert-dialog

- [x] Alert Dialog (`alert-dialog`)：r5 将当前官方 `main` 固定为 `42f3bb63…`，MDX、wrapper、七个示例和 Nova CSS 与 r4 字节一致。修复共享 modal 初始焦点：Tab 现在在 Cancel -> Continue -> Cancel 内循环，Escape 关闭并恢复触发器焦点。目录 5/5、Alert Dialog 与 modal-focus 17/17、受影响 Dialog/Sheet/Drawer/surfaces 回归 150/150 通过；Wasm `1355c1d5…` 下完成四象限、48 张六分节和 22 个官方/本地弹窗交互取证。共享文档壳/字体栅格与 Canvas 原生 alert-dialog 语义作为明确平台差异，见 [alert-dialog-r5](../../web-parity/alert-dialog-r5/README.md)。

## aspect-ratio

- [x] Aspect Ratio (`aspect-ratio`)：r5 将当前官方 `main` 固定为 `42f3bb63…`，MDX、wrapper、四个示例和 Nova CSS 与 r4 字节一致。`ratio` 改为官方一致的必填参数，并在组件入口拒绝零、负数、NaN 与无穷值；API 表同步标为 `Required`。6 项目录与 5 项 core 测试通过，当前 Wasm `dfb4b1c1…` 下完成 1440/390×浅深四象限、24 张三个官方示例分节截图，以及 RTL 阿拉伯语/英语/希伯来语与弹层切换。独立 Reviewer 无 finding；内置 PNG/`next/image`、阿希字体栅格和共享文档壳差异作为明确平台差异，见 [aspect-ratio-r5](../../web-parity/aspect-ratio-r5/README.md)。

## attachment

- [x] Attachment (`attachment`)：官方六配方（demo、Image、States、Sizes、Group、Trigger）顺序/标题一致，Features/四个 Accessibility 小节与 9 个官方 API 小节齐全；slot h 最终构建（wasm 02d85afc）后重拍 1440×1000/390×844 浅深四象限、5 组逐配方对照，并取证 Trigger 整卡 overlay hover（背景 muted/50）与离开恢复、点击打开预览 Dialog/Escape 关闭、Copy 动作独立聚焦不触发 Dialog、Image hover、Group 横向滚动渐隐、States shimmer 两帧及 Default 非交互卡片无 hover 变化；Chrome AX 树暴露真实 button 角色与名称（Preview research-summary.pdf / Copy link / Remove …）；FileSearch 自定义矢量与官方一致，标题 14px/500/17.5 行高沿用 r7 实测。54 项聚焦测试通过（AttachmentExamples 9 + core 45）。仍保留的已解释差异：demo 后官方介绍段落需共享页面骨架新增“预览后介绍”字段、base-rhea 预览上下文、完整 transition 中断帧级取证与屏幕阅读器全矩阵、旧 Vega golden；见 [attachment-r9](../../web-parity/attachment-r9/README.md)。

## avatar

- [x] Avatar (`avatar`)：r4 将当前官方 `main` 固定为 `42f3bb63…`，12 份 MDX/示例/wrapper 与 Nova CSS 均与 r3 字节一致。AvatarGroup 现在为所有成员无条件提供背景色 ring；Count 省略 `size` 时按官方 CSS 级联跟随组尺寸（有 Sm 则 Sm，否则有 Lg 则 Lg，否则 Default），显式尺寸可覆盖。Dropdown 键盘回归补齐 ArrowUp/Home/End/循环/Escape 焦点恢复。目录 5 项、core parity 7 项、截图 10 项通过；当前 Wasm `ba10ad5d…` 下完成四象限、72 张九分节和 12 个 Dropdown/RTL 交互取证。修复后独立 Reviewer 无 finding；共享壳、字形栅格、`next/image` 与 Canvas 原生语义作为明确平台差异，见 [avatar-r4](../../web-parity/avatar-r4/README.md)。

## badge

- [x] Badge (`badge`)：r4 固定当前官方 `main` 为 `42f3bb63…`，保存 MDX、`badge.tsx`、七个示例与 Nova CSS，移除官网不存在的本地 Composition 小节；新增 Space/Enter `onClick`/`href` 激活、可选受控 `interactionSource` 与四变体 hover 测试。core 17/17、catalog 8/8 通过，当前 Wasm 构建完成 1440/390×浅深官方/本地四象限。golden 字体漂移已由 HEAD-only 控制运行证明为仓库环境问题；调用方图标尺寸、非 Nova preset 与 CSS transition 作为明确平台/API 差异记录，见 [badge-r4](../../web-parity/badge-r4/README.md)。

## breadcrumb

- [x] Breadcrumb (`breadcrumb`)：r4 将当前官方 `main` 固定为 `42f3bb63…`，MDX/七示例与 r3 字节一致，并归档当前 Base wrapper 与 Nova CSS。BreadcrumbLink 增加主题 focus ring，补齐 Tab、Enter/Space 与 disabled 回归；当前页改为不可点击的 BasicText，并以 `"<text>, current page"` 暴露可访问名称。目录 5 项、core parity 6 项、截图 7 项通过；当前 Wasm `7a06664d…` 下完成四象限、48 张六分节及 11 个 hover/Dropdown/RTL 交互取证。Compose desktop 仍会把该可访问当前页节点标为 focused，作为明确语义差异记录；浏览器键盘 focus 为 JVM/golden-only，见 [breadcrumb-r4](../../web-parity/breadcrumb-r4/README.md)。

## bubble

- [x] Bubble (`bubble`)：r3 将当前官方 `main` 固定为 `42f3bb63…`，MDX/九个示例与 r2 字节一致，并归档当前 wrapper 与 Nova CSS。Variants 三段 ghost 文案在可滚动 600dp 视口与当前浏览器构建中完整显示；BubbleContent 覆盖 disabled、指针、Enter、Space，Tooltip 覆盖 focus/Enter/Escape/焦点恢复，Popover 覆盖 Enter/Space/Escape，Reaction 覆盖指针与键盘。目录 9 项、core parity 7 项、截图 14 项通过；当前 Wasm `7a06664d…` 下完成四象限、64 张八分节和 10 个 ghost/链接/Reaction/Collapsible/Popover 状态。Tooltip 因 Ego 合成 hover 无法触发 Canvas 而明确限定为 JVM 覆盖；共享壳/字体与 Kotlin 路由文案作为既有差异，见 [bubble-r3](../../web-parity/bubble-r3/README.md)。

## button

- [x] Button (`button`)：r5 将当前官方 `main` 固定为 `42f3bb63…`，MDX、wrapper、15 个示例与 Nova CSS 均按 SHA-256 归档。删除官网不存在的 Composition 小节，把 `buttonStyle` 标为 `Kotlin extension`；Label As 子菜单改为官方 96dp 宽度，桌面右开、手机左翻且选项保持单行，Work 选择在关闭重开后保持。目录 24/24、精确 core 42/42 通过；Wasm `a4730c39…` 下完成四象限、120 张十五分节和 22 个官方/本地菜单、Link、RTL 交互状态取证。仍需共享文档壳/字体栅格、Canvas 原生链接语义和键盘-only 浏览器遍历作为明确平台差异，见 [button-r5](../../web-parity/button-r5/README.md)。

## button-group

- [x] Button Group (`button-group`)：r4 将当前官方 `main` 固定为 `42f3bb63…`，归档 MDX、12 个示例、组件、Base UI wrapper 与 Nova CSS。`ButtonGroup` 现提供 traversal-group 语义及可选 accessibility label，页面补齐官方 Popover/Input Group 配方、Composition、Accessibility 与 ButtonGroup-vs-ToggleGroup 指南。目录与 core 聚焦共 34/34 通过，`ktlintCheck` 与当前工作树 Wasm distribution 通过；1440×1000、390×844 浅深四象限和 hover、键盘焦点、Dropdown、滚动取证完成。Canvas 无直接 HTML `role=group`、指针点击无持续视觉状态及共享文档壳作为明确平台差异保留，见 [button-group-r4](../../web-parity/button-group-r4/sources.json)。

## calendar

- [ ] Calendar (`calendar`)：恢复验证 32 tests、owning lint、Wasm 与四象限 browser 32/32 均通过且输入/产物 hash 当前匹配；30d audit 确认仍缺真正 Persian/Jalali 引擎与官方交互预览、Kotlin 等价 Changelog/RTL Support 指南，不能作为平台差异豁免。需 implementation/fixer 后独立 review 与受影响 gate 复验，不再重复未修改的验证。原生菜单和 locale 已按本地等价 API 分类。见 [audit](../../../.agent-runs/calendar/audit-recovery/summary.json)、[handoff](../../../.agent-runs/calendar/progress.md)。

2026-11-10 续验关闭：历史 CAL-A1/CAL-A2 已由真实 Jalali 引擎、交互预览及迁移指南解决；目录滚动、悬停和按压动画修复后，65 tests、10 unchanged goldens、5 lint、JS/Wasm、浏览器实测及独立审查通过。原段落保留为当时记录，当前状态以规范清单为准；已知 Wasm DOM/ARIA 恢复限制按 I16 单列，不宣称读屏验收通过。见[当前验收证据](../../../.agent-runs/calendar/acceptance-20261110/evidence.md)。

## card

- [x] Card (`card`)：r4 固定当前官方 `main` 为 `42f3bb63…`，归档 MDX、六个示例、Base UI wrapper 与 Nova CSS。将官网嵌入 Spacing 的 Edge to Edge 配方从错误的 `Kotlin extension:` 标题纠正，并移除本地额外说明；390dp Password/Forgot 2px 间距与官方一致并新增回归。共享文档模型新增 API 后章节槽位，为 Card 补 Kotlin 等价 Changelog 与三步 Spacing Variable 迁移，步骤不再污染 TOC。目录 20/20、共享文档 8/8、core 17/17 通过；当前 Wasm `c575ab2d…` 下完成四象限、64 张六配方/API/Changelog 分节和 6 个 spacing/RTL 交互取证。共享文档壳、Geist/阿希字形、代码面板、原生滚动条与 Canvas 语义作为明确平台差异，见 [card-r4](../../web-parity/card-r4/README.md)。

## carousel

- [x] Carousel (`carousel`)：r4 固定当前官方 `main` 为 `42f3bb63…`，归档 MDX、七个示例、Base UI wrapper 与 Nova CSS。共享文档模型新增首屏后介绍与指定示例前指南槽位，页面恢复 About → Installation、Options → API、Events → Plugins，并把键盘/无障碍说明标为 Kotlin extension；About/Options/Events 保留官网 Embla 文案与链接。目录 8/8、共享文档 8/8、core 17/17 通过；当前 Wasm `a0632a78…` 下完成四象限、88 张七配方/About/Options/Events/API 分节和 7 个导航/拖拽/自动播放/RTL 交互取证。Embla 任意 options/plugins 惯性参数、共享壳与字体/Canvas 语义作为明确差异，见 [carousel-r4](../../web-parity/carousel-r4/README.md)。

## code-block

- [x] Code Block (`code-block`)：按当前官网重建 23 个配方（demo + Basic…Terminal Output，Max Height/Languages/Terminal Output 为嵌套 h3），补 Features/Accessibility/Composition/Usage 与九个 API 小节；组件新增 maxHeight/scrollable 截断及静态底部渐隐、横向滚动渐隐、内容强制 LTR、行号对读屏隐藏。26 项聚焦测试、Wasm 构建通过；1440/390 浅深四象限及复制确认、换行、悬浮按钮、键盘滚动、Show More 展开/收起、Tabs、RTL 阿语/英语切换均已复核。仍余 rhea→Nova 样式上下文、Shiki/ANSI/Streamdown 服务端/流式/Markdown、Typeset 近似、下载按钮无效、无原生滚动条、共享页面缺少 demo 后导语槽位及指针聚焦 ring 与 focus-visible 差异，见 [Code Block r2](../../web-parity/code-block-r2/README.md)。

## trace

- [x] Trace (`trace`)：按固定版本 8a35366 补齐官方 16 个预览（顶部 demo + Trace Line/Status/Description/Collapsible/Open State/Link/Dialog/Sheet/Group/Sources/Chain of Thought/Commit/Whole Turn/Reasoning/Tool），顺序/标题/文案、Built with Trace/Features/Accessibility 与 9 个 API 小节（TraceLink 为 Kotlin extension）已对齐；组件补 `keepMounted`、标题后图标 4dp、`TraceDescription(mono=)` 与 `TraceLink`。30 项聚焦测试（页面19+核心11含RTL/keepMounted/链接语义）、Wasm 89721bca、四象限与新空间实际浏览器展开/受控/Dialog/Sheet/嵌套diff/Reasoning/Tool/Replay 状态通过。残余（已解释）：官方简介段/Built with 表格/Status 表受本地文档模型限制置于 Composition 后或改为散文，官方预览后补充 TSX 片段未收录，Reasoning/Tool 非流式、Replay 恢复静态转录，Dialog/Sheet 触发行为无 hover 变色，官方预览为 base-rhea 而本地 Nova（对话框448vs384）且 API 表少 Description 列，见[trace-r3](../../web-parity/trace-r3/README.md)。

## chart

- [ ] Chart (`chart`)：Stage 20 Fixer attempt 3 已修复 Legend 位置说明及 Column 示例，明确组合顺序决定上下位置、verticalAlign 仅控制朝向图表的间距；保留前轮 Using Colors、Tooltip Props 层级和 formatter 修复。当前 ChartPageTest 5/5 通过（相同命令实际升级重试，--no-daemon），独立只读复审 PASS，无待修 finding。当前 lint/build/browser/audit 尚未覆盖新增输入，不能沿用旧通过作为验收。证据与后续动作见 `.agent-runs/chart/progress.md`、`.agent-runs/chart/evidence/20-fixer3/`；Canvas/Recharts API 差异不豁免视觉和交互验证。

## checkbox

- [ ] Checkbox (`checkbox`)：20-implement 恢复：已有 hit-area40×32、Table16/32 inset 与 API 链接修复保留；新增当前 JVM live shape/theme 像素回归通过，但 checked 颜色64ms中间帧与终态无法区分，聚焦2项为1通过/1失败，尚未确定组件动画还是测试时钟原因。历史两张深色 golden 仍待裁决；Canvas input/form/label-for 为明确平台/API差异，triStateToggleable/真实焦点/Space 为本地等价物，外部辅助技术矩阵未验证。旧 browser 证据早于 hit-area/inset 修复，不作当前验收。见 [恢复证据](../../../.agent-runs/checkbox/recovery-20) 与 [progress](../../../.agent-runs/checkbox/progress.md)。

## chip

- [x] Chip (`chip`)：40-handoff 完成。项目 Kotlin 扩展页，固定 revision `8a3536678a528cc012b9e62484290e79d0be410f` 下 `chip.mdx` 为预期 404，不声称官方配方；七组实际状态、`isKotlinExtension` 标记与 Chip/ChipVariant API 表保留。聚焦 JVM 20/20、当前工作树 Wasm distribution 与 1440×1000/390×844 浅深四象限浏览器检查通过，无源码改动；Compose Web 将 `Role.Checkbox` 暴露为 `role=button` 且省略 `aria-pressed`，记录为明确平台/API 差异。见 [`.agent-runs/chip/progress.md`](../../../.agent-runs/chip/progress.md) 与 [chip-r1](../../web-parity/chip-r1/README.md)。

## collapsible

- [x] Collapsible (`collapsible`)：已按当前官网 `8a353667` 重建 Default/Basic/Settings Panel/File Tree/RTL 五配方，补 Controlled State 文案与代码、Composition、Base UI API Reference 及 288dp/576dp 预览几何；四个 Kotlin 专有示例保留为 `Kotlin extension:`。8 项聚焦测试、Wasm 构建、1440/390×浅深四象限、四个分节对照及 Default/Basic/Settings/File Tree/RTL 语言切换交互已验证；仅余 Base UI 单变体、sr-only FieldLabel、字体栅格化等已说明差异，见 [collapsible-r1](../../web-parity/collapsible-r1/README.md)。

## combobox

- [x] Combobox (`combobox`)：r1 已按当前官网（8a35366）重建页面：demo + Basic/Multiple/Clear Button/Groups/Custom Items/Invalid/Disabled/Auto Highlight/Popup/Input Group/RTL 十二配方及官方顺序/文案，Composition 三棵组合树、Custom Items/Multiple Selection 指南与 Base UI API 链接已补；删除 48 条 Base UI API dump 配方。组件修复可编辑输入按 anchor 宽度出面板、chips 宽度跟踪与点击即开、空态/筛选项自动隐藏、InputGroup 前导 addon 与 panelWidth 覆盖；56 项聚焦测试绿（本组件 12 项），四象限与 closed/open/filtered/selected/multi/custom/invalid/disabled/auto/popup/input-group/RTL 状态截图已复查。仍留：Composition 三个 h3 受单 compositionCode 模型限制合为一块；可编辑输入固定 216dp（官网 Input Group 约 229px）；单选清空需 onClear 回调；RTL 语言工具栏为目录页共享 chrome 且希伯来标签走共享字体回退。见 [combobox-r1](../../web-parity/combobox-r1/README.md)。

## command

- [x] Command (`command`)：r11按当前官网8a3536678a重建页面：About、Composition与6个官方配方（demo、Basic、Shortcuts、Groups、Scrollable、RTL，含官网标题/描述/预览内容）按官网顺序实现；删除旧的54项参数矩阵，仅保留4个Kotlin扩展（受控value、自定义filter、长列表Home/End、keywords）。6项新目录测试通过，Wasm构建后四象限与Basic/Shortcuts对话框打开+Escape画面已在浏览器复核。残留：About位于Composition之后（本地渲染器顺序）、CommandDialog居中而官网top-1/3、API表为本地增补、无Radix/Aria标签页与广告，详见[round record](../../web-parity/command-r11/README.md)。

## context-menu

- [ ] Context Menu (`context-menu`)：r1 已按当前官网（8a35366）重建为 demo + Basic/Submenu/Shortcuts/Groups/Icons/Checkboxes/Radio/Destructive/Sides/RTL 十一配方，官方顺序/文案/Composition/API 链接齐备，删除 39 条 API dump 配方；虚线触发器统一。组件补齐 `side`（Top/Right/Bottom/Left/InlineStart/InlineEnd）/`align` 与边缘翻转（新 provider 8 项数学测试）、面板 `panelWidth`（默认 144，demo/RTL 192，checkboxes 188，submenu 144/176）、外置 ring（消费共享 novaRing）、`ContextMenuItem` slot 重载与 SubContent width/style。此前窗口坐标/RTL/触摸取消/两层 Escape 回归保持。28 项 contextmenu 相关测试与 16 项目录测试绿，四象限及 open/submenu+嵌套/双层 Escape/checkbox 切换保持展开/sides=top/RTL 菜单与语言菜单截图已复查。仍留：Compose 菜单行需显式宽度（官网 min-w-36 随内容 187.7 等按实测宽度传入）；官网 pointer-fine/coarse 双文案在目录页合并为一句（触摸长按仍实现）；API Reference 仅 Base UI 链接；RTL 语言名走共享 RTL 工具栏字体回退。见 [context-menu-r1](../../web-parity/context-menu-r1/README.md)。

## date-picker

- [x] Date Picker (`date-picker`)：r10按当前官网8a3536678a重建页面：官方8个配方（demo、Basic、Range Picker、Date of Birth、Input、Time Picker、Natural Language Picker、RTL）按官网顺序与文案实现，含Composition；删除旧38项Calendar参数矩阵，仅保留4个Kotlin扩展（中文本地化、多选、禁用周末、最小/最大月份）。8项新目录测试通过，Wasm构建后四象限与demo弹层打开画面已在浏览器复核。残留：Time Picker用24h文本字段替代原生input[type=time]；Natural Language用Kotlin小解析器替代chrono-node；RTL使用内置阿拉伯日历locale而非date-fns/DayPicker bundle；本地增加API表，详见[round record](../../web-parity/date-picker-r10/README.md)。

## dialog

- [x] Dialog (`dialog`)：r1 按当前官网重建页面为官方六配方（Edit profile、Custom Close Button、No Close Button、Sticky Footer、Scrollable Content、RTL），其余 Compose-only 配方统一加 `Kotlin extension:` 前缀后置，并补 Composition 树、Usage 导入块与七个 API 小节；Custom Close 的 448dp、RTL 三语、初始焦点/Escape 焦点恢复、非模态、Sticky/Scrollable 滚动均已在真实浏览器验证，四象限 1440/390×浅深已复查，DialogExamplesTest 10 项与 Wasm 构建通过。Canvas 无原生 inert/aria-modal 语义、无内容弹窗的 Escape 仅有浏览器证据、Base UI `trap-focus`/`initialFocus`/`onOpenChangeComplete` 未映射，见[Dialog r1](../../web-parity/dialog-r1/README.md)。

## drawer

- [x] Drawer (`drawer`)：r1 按当前官网重建为官方七配方（Delivery time、Position、Swipe Handle、Nested、Non Modal、Snap Points、Responsive），Compose-only 配方加 `Kotlin extension:` 后置，并补 Custom Sizes/Styling/Migrating from Vaul 章节、Composition 树与七个 API 小节；右侧/左侧/内容自适应/四层嵌套/非模态/496dp peek→全高吸附/桌面 Dialog 响应式均在真实浏览器验证，四象限已复查，DrawerExamplesTest 10 项与 Wasm 构建通过。demo 后 Callout 无放置位、迁移步骤以 Bullets 呈现、预览内 Toast 位置、真机手势与 data-* 属性语义仍见[Drawer r1](../../web-parity/drawer-r1/README.md)。

## dropdown-menu

- [ ] Dropdown Menu (`dropdown-menu`)：r4按当前官网8a3536678a逐条复核13个配方（demo/Basic/Submenu/Shortcuts/Icons/Checkboxes/Checkboxes Icons/Radio Group/Radio Icons/Destructive/Avatar/Complex/RTL）顺序、标题与文案一致，宽度与官网一致。核心修复：指针打开不再聚焦首行（原缺陷）、键盘ArrowDown/ArrowUp/Enter/Space分别聚焦首/末行、Nova默认内容宽度改为max(anchor,128dp)、子菜单min-w-96、Nova面板以内描边改为官网ring-1 foreground/10（不再吃掉2dp行宽）。18项目录测试通过，2张Nova基准按预期重录并verify通过，Wasm构建后四象限与浏览器指针/键盘/Escape/手机状态已复核。剩余：Compose Web语义树无法等价ARIA role=menu/menuitem，anchor宽度对“触发器宽于显式宽度”的用例仍会取宽，子菜单无显式宽度时不做内容自适应，详见[round record](../../web-parity/dropdown-menu-r4/README.md)。

## empty

- [ ] Empty (`empty`)：r3 按当前官网校正六段配方 copy（Outline/Background/Avatar/Avatar Group/InputGroup/RTL）并给 Usage 补 EmptyMedia，Outline 自然高度、RTL 阿拉伯语、Avatar Group、InputGroup 重新取证；四象限 1440/390×浅深已复查，EmptyExamplesTest 7、EmptyBrowserParityTest 12、core EmptyParityTest 7 共 26 项通过，Wasm 构建通过。平衡/pretty 换行、字重与图形/阴影细节、共享页面排版、原生 HTML 链接与完整键盘/读屏、任意 Style/shape 与大内容仍开放；历史旧 Vega 截图矩阵未重验，见[Empty r3](../../web-parity/empty-r3/README.md)。

## field

- [ ] Field (`field`)：官方13配方（Demo、Input、Textarea、Select、Slider、Fieldset、Checkbox、Radio、Switch、Choice Card、Field Group、RTL、Responsive Layout）顺序/标题/文案与Composition三小节、Anatomy、Form、Validation/Errors、Accessibility、10项API已对齐；Demo 448宽、分隔线、正常字重复选框、Select全宽、滑块区间说明已修；Nova FieldSet legend→description负边距、dense组12间距、横向*:w-auto、FieldSeparator -8边距与Choice Card 10dp半径已实现；9项目录测试+14项FormNova+4项FieldScreenshot通过，1440/390浅深四象限及Select/Radio/Choice/Switch/RTL三语/Responsive手机实测。仍待：label无htmlFor聚焦、FieldDescription nth-last-2 -4、RadioGroup触发的FieldSet 12间距、官方bullets无悬挂缩进、Textarea原生拖拽柄、多错误列表语义及完整辅助技术；[证据](../../web-parity/field-r1/README.md)。

## hover-card

- [ ] Hover Card (`hover-card`)：官方顺序（Demo、Installation、Usage、Composition、Trigger Delays、Positioning、Basic、Sides、RTL、API Reference）与文案已对齐，Composition 补官方说明句，三条 Kotlin 配方改名 `Kotlin extension: …`；r2 的 Trigger 锚点、失焦立即关闭、Nova 5dp 避让与 RTL 行框修复保留。2026-11-05 r3 新构建 `6f7a5fe4…` 下 5项目录+12项定位测试通过；1440/390 浅深关闭态与 Demo 开/关、Sides 四物理方向打开、手机开态已实测并存图。safe polygon、多 Trigger/嵌套宿主、全局 Escape、按实际翻转侧的动画、完整辅助技术与连续切换/重载矩阵仍开放；[证据](../../web-parity/hover-card-r3/README.md)。

## input

- [ ] Input (`input`)：Stage30 当前输入 hash 全匹配，41/41 聚焦测试、owning lint、Wasm、四象限 browser 68/68 通过；18 份 pinned source 已恢复并逐一匹配 SHA。旧残留已裁决：label 通过 FocusRequester/命名语义恢复聚焦，Web 原生 file chooser bridge 已实现并验证选择；显式宽度、目录 Lucide、官方无 API 表为明确 API/平台差异。仍缺 Nova transition-colors 中间帧、Input live-shape 切换及非 forced 文件按钮浏览器操作证据，不以平台差异豁免；保持未勾选。见 [当前审计](../../../.agent-runs/input/verify-30d-current/audit.json)、[残留裁决](../../web-parity/input-r1/README.md)。

## input-group

- [ ] Input Group (`input-group`)：r1 已按当前官网（8a35366）重建：demo + Align（h2，官方 Callout）+ inline-start/inline-end/block-start/block-end（嵌套 h3）+ Icon/Text/Button/Kbd/Dropdown/Spinner/Textarea/Custom Input/RTL 共 14 配方，官方顺序/文案/Composition 与五节 API Reference 齐备，删除原 API dump 配方；“Align”通过 beforeExamples h2 + headingLevel=3 复刻官网层级（目录与官网一致）。组件为配方补齐 InputGroupInput readOnly/type 与 InputGroupTextarea readOnly/rows/minLines/maxLines 透传。32 项目录测试 + 17 项 core 测试通过，四象限与 inline-start/block-end/button/dropdown open/RTL(阿→英→希) 状态图已复查。仍留：Custom Input 无 Compose 版 TextareaAutosize，改用内置 Textarea 显式行高；Button 配方复制状态为本地图标切换、popover 正文简化；原生 htmlFor/className 覆盖为 Kotlin semantics/Modifier 等价物；RTL 语言名走共享工具栏字体回退。见 [input-group-r1](../../web-parity/input-group-r1/README.md)。

## input-otp

- [ ] Input OTP (`input-otp`)：r1 已按当前官网（8a35366）重建为 demo + Pattern/Separator/Disabled/Controlled/Invalid/Four Digits/Alphanumeric/Form/RTL 十配方及官方 About/API 链接，删除原 API dump（Masked/Read only/Max length/Slot/Group 等）。组件新增 root/slot `invalid=`（destructive 边框+ring，等价 aria-invalid）与 root contentDescription/disabled/error 语义；10 预览、controlled 输入、digits pattern 过滤、无效/禁用语义与 RTL 均有 20 项目录测试 + 7 项 core 截图测试覆盖，四象限与 controlled/disabled/invalid/four-digits/form/RTL 状态图已复查。仍留：About 在目录模型中只能位于 Composition 之后（官网紧跟 demo）；Form 配方槽位使用共享 32dp 尺寸而非官网 h-12/w-11；逐槽读屏/IME 组合/平台自动填码矩阵未完成；RTL 语言名走共享字体回退。见 [input-otp-r1](../../web-parity/input-otp-r1/README.md)。

## item

- [ ] Item (`item`)：r3 在当前官网（8a35366）下确认 11 配方顺序与 10 个 API 小节不变，并把全部配方说明改为官网原文（variant/size/icon/avatar/image/group/header/Link 的 render→Kotlin href 对照/RTL），补回官网 Item vs Field 两段文字与 demo 后总述段；ItemExamplesTest 新增文案断言，9 项目录测试 + 16 项 core 测试通过，四象限与 dropdown 展开、RTL 状态图复查。仍留：总述段因首个示例描述槽位显示在预览上方（官网在代码块之后）；390 宽 Header 三卡仍是浏览器 flex min-content 溢出 vs Compose 换行模型差异，不以硬编码 padding/截断掩盖；Link 的 native target/rel/修饰键与同页锚点、Dropdown 指针打开的初始高亮（共享 DropdownMenu）、共享排版/text-pretty/图片裁切及完整动态/辅助技术矩阵仍开放。见 [item-r3](../../web-parity/item-r3/README.md)（承接 item-r2）。

## kbd

- [x] Kbd (`kbd`)：40-handoff 完成。沿用固定归档 `kbd-r2` 与现有页面/组件实现，六个官方配方顺序与文案、Input Group 键帽、Button/Tooltip 几何和三语 RTL 均有聚焦及浏览器证据；core/catalog 24/24 测试、当前工作树 Wasm 构建、1440×1000/390×844 浅深四象限 242/242 浏览器检查和确定性审计通过。原生 `kbd`/辅助技术/触屏、共享字体栅格及当前 `main` 在线 revision 刷新受网络阻塞作为明确平台/环境差异记录，见 [`.agent-runs/kbd/progress.md`](../../../.agent-runs/kbd/progress.md) 与 [kbd-r2](../../web-parity/kbd-r2)。

## label

- [ ] Label (`label`)：r1按当前官网8a3536678a重建页面：官方描述与用法，Default（复选框+Label，点击标签切换选中）、Label in Field（官方field-demo付款表单：FieldLabel/FieldDescription/FieldSet/FieldGroup/FieldSeparator）、RTL（阿拉伯语标签+语言工具栏）按官网顺序实现，另保留2个Kotlin扩展（required、disabled）。新增LabelExamplesTest 6项通过；Wasm构建60af0664…后四象限与标签点击选中已浏览器验证。仍开放：Compose无htmlFor隐式关联（付款表单FieldLabel点击不聚焦输入框）、官网demo后的Field提示Callout未在本地骨架渲染、任意字体/emoji/多行阿拉伯行高与完整辅助技术，故保持未勾选，详见[label-r1](../../web-parity/label-r1/README.md)。

## marker

- [ ] Marker (`marker`)：r3按当前官网8a3536678a复核八个配方（demo、Variants、Status、Shimmer、Separator、Border、With Icon、Links and Buttons）顺序与内容，Variants/Status/Shimmer/Separator/Border/With Icon/Links and Buttons说明改为官网原文；确认自然预览高度、本页链接、文字/图标hover与按钮Toast仍正常。MarkerExamplesTest 5、MarkerInteractionTest 12、MarkerParityTest 8、MarkerScreenshotTest 5通过；Wasm构建60af0664…后四象限已复核（含共享说明80%宽度），浏览器已验链接hover、Revert Toast与Accessibility/API区。仍开放：共享Toaster位置/堆叠、原生anchor/button与辅助技术能力、官网demo后导语与base-rhea预览主题、Variants表格/节奏差异及旧暗色分隔线基准，故保持未勾选，详见[marker-r3](../../web-parity/marker-r3/README.md)。

## menubar

- [ ] Menubar (`menubar`)：r1 按当前官网确认六配方顺序（demo/Checkbox/Radio/Submenu/With Icons/RTL），给 30 个 Compose-only 配方统一加 `Kotlin extension:` 前缀，补 API Reference 三个小节与 Base UI 链接；浏览器实测 File/View/Profiles 菜单开关、ArrowDown/ArrowRight 键盘导航、Escape 关闭与 RTL 阿拉伯语栏，四象限 1440/390×浅深已复查，MenubarExamplesTest 8 项与 Wasm 构建通过。Canvas 无原生 menu/menuitem 语义、悬停子菜单交互帧、指针碰撞/滚动与真机触摸、共享页面标签/手机宽度仍开放，见[Menubar r1](../../web-parity/menubar-r1/README.md)。

## message

- [ ] Message (`message`)：r1 按当前官网确认六配方顺序（demo/Avatar/Group/Header and Footer/Actions/Attachment），补 Features 与 Accessibility（Label icon-only actions / Status updates）章节，并把额外配方统一加 `Kotlin extension:` 前缀；官方 base-rhea 的蓝色气泡用 Nova + Accent.Blue 复现（实测 RGB 35,70,221），浏览器复查 Group/Actions/Attachment 与四象限，MessageExamplesTest 7 项与 Wasm 构建通过。base-rhea 圆角/间距、原生 ARIA/读屏、完整键盘与动态主题、共享页面标签/手机宽度仍开放，见[Message r1](../../web-parity/message-r1/README.md)。

## message-scroller

- [x] Message Scroller (`message-scroller`)：r4 按当前官网对齐：description 原文、`examplesHeading = null`、官方顺序 12 节（New Chat demo、Anchoring Turns、Group Chat、Keeping Context Visible、Following the Live Edge、Opening Saved Threads、Avoiding a Flash on Reload、Loading Earlier Messages、Animating New Messages、Jumping to Messages、Tracking the Reader's Position、Reading Scroll State）+ 8 段官方指南，Compose-only 示例统一 `Kotlin extension:` 前缀。新增 5 个官方配方实现（demo、anchoring、group chat、streaming follow、animation presets），复用既有 6 个；浏览器实测 demo 流式跟随、锚点切换（user/assistant + 64dp peek）、Group Chat 两步推进与 Marker 锚点、streaming 跟随、animation 预设发送，四象限 1440/390×浅深复查，MessageScrollerExamplesTest 4 项与 Wasm 构建通过（`ef04de09cbf8457ebdc6.wasm`）。官方指南位置（Installation 前）、scroll-release 仅 JVM、rhea/theme-blue 近似、DOM aria 语义与虚拟化/unstyled 无对应物等差异见[Message Scroller r4](../../web-parity/message-scroller-r4/README.md)。

## navigation-menu

- [ ] Navigation Menu (`navigation-menu`)：r3按当前官网8a3536678a复核Default/三语RTL顺序与内容；补官网RTL文案、Composition中的NavigationMenuIndicator和Link Component章节（render prop说明为Kotlin onClick），隐藏通用Examples标题；仅保留3个Kotlin扩展（Vertical/Delay/Controlled），其余参数矩阵不再渲染。修复390px预览被裁切：官方在sm以下缩小预览内边距，本地改为16dp预览边距+640dp以下压缩触发器水平内边距（桌面保持官网10dp）。16项配方+2项响应式测试通过；Wasm c0570519…构建后四象限（官方主题已重新断言并逐张校验背景像素）与hover/点击/Escape/1024面板已浏览器验证。仍开放：共享viewport/Root align、弹层绘制在预览卡外而非h-96内裁剪、原生anchor/辅助技术与完整延迟矩阵，故保持未勾选，见[navigation-menu-r3](../../web-parity/navigation-menu-r3/README.md)。

## pagination

- [x] Pagination (`pagination`)：40-handoff 完成。沿用固定官网 `8a353667…` 与 r2 页面结果，当前工作树 Wasm 构建在 1440×1000/390×844 浅深四象限通过；core 15/15、catalog 20/20，ktlint 仅命中无关 catalog 既有行长错误，`git diff --check` 与 handoff validator 通过。当前页无原生 HTML anchor、共享文档壳及旧 Vega 基准作为明确平台/历史差异记录，见 [pagination-r2](../../web-parity/pagination-r2/README.md) 与 `.agent-runs/pagination/progress.md`。

## popover

- [ ] Popover (`popover`)：r3按当前官网8a3536678a复核五配方Default/Basic/Align/With Form/RTL；补官方描述、Composition区块并隐藏通用Examples标题，仅保留3个Kotlin扩展（Non-modal/Placement/Uncontrolled），其余参数矩阵不再渲染。24项目录参数化测试通过（1440/390×浅深：四输入Dimensions、Basic header、三对齐面板、表单、六向RTL、扩展），既有核心PopoverFocusTest 2、NovaLayout 5、NovaScreenshot 2继续作为焦点/布局覆盖；Wasm c0570519…构建后四象限（官方主题逐张校验）与默认打开/外部点击关闭/Escape关闭已浏览器验证。仍开放：默认modal=true与Base UI差异（非模态示例已文档化）、完整初始焦点/焦点恢复与动画矩阵、side/align物理属性矩阵未按1:1配方展开，故保持未勾选，见[popover-r3](../../web-parity/popover-r3/README.md)。

## progress

- [ ] Progress (`progress`)：按当前官网 8a353667 复核四配方（demo/Label/Controlled/RTL）；Composition 改为官方结构树并补嵌套 “With label and value” 说明/代码，RTL 改官网引导链接，API 首节补 Base UI 链接；28 项聚焦测试、Wasm 构建通过；1440/390 浅深四象限、受控键盘 End/Home、轨道点击无效（与官网一致）及三语切换已复核。仍余共享 Composition 内容顺序、Kotlin API 表/扩展小节、完整指针与原生语义、旧 Vega 基准及共享初载/排版差异，见 [Progress r3](../../web-parity/progress-r3/README.md)。

## questionnaire

- [ ] Questionnaire (`questionnaire`)：按当前官网重建 14 个预览（demo + 13 配方）的顺序与文案，补 Server Rendering/Accessibility/Unstyled/API Reference；Custom Validation 真实返回无效项、Controlled/Resume/Card/Dialog 对齐 CardAction 与默认答案；旧参数演示收敛为 4 个 Kotlin extension。22 项聚焦测试、Wasm 构建通过；1440/390 浅深四象限及多选、校验返回、Resume 重置、Dialog 打开已复核。仍余自定义 Progress render、NativeSelect、onReset、render-prop 组合、共享 Composition 段落/底对齐、浏览器完整键盘/AT 与 toast，见 [Questionnaire r2](../../web-parity/questionnaire-r2/README.md)。

## radio-group

- [ ] Radio Group (`radio-group`)：按当前官网重建 7 个预览（demo/Description/Choice Card/Fieldset/Disabled/Invalid/RTL）及 Usage/Composition 与 API 小节，Horizontal/Required 保留为 Kotlin extension；12 项聚焦测试、Wasm 构建通过；1440/390 浅深四象限及卡片选中、disabled 无效、Invalid、RTL 阿语/英语切换已复核。仍余 Kotlin API 表与额外 TOC、FieldLabel 字重/原生 label 关联、完整 AT/动态重排与真机触屏，见 [Radio Group r1](../../web-parity/radio-group-r1/README.md)。

## resizable

- [x] Resizable (`resizable`)：按固定版本 8a35366 对齐官方四配方（Default/Vertical/Handle/RTL）顺序与文案，补 Composition/Usage 导入/About/API Reference/Changelog（v3→v4 八条），移除 Examples 标题并把 13 个重复变体裁剪为四条 `Kotlin extension:` 配方；20 项聚焦测试（拖拽/键盘/触摸/RTL/嵌套比例）、Wasm 52e9c17c、1440/390 浅深四象限与浏览器拖拽+RTL 反向拖拽状态通过。残余（已解释）：About 受文档模型限制在 Composition 后（官方在 Installation 前），Changelog 官方两表改为列表，Canvas 无原生 separator DOM 语义，见[resizable-r2](../../web-parity/resizable-r2/README.md)。

## scroll-area

- [x] Scroll Area (`scroll-area`)：按固定版本 8a35366 对齐官方三配方（Default/Horizontal/RTL），补 Composition/Usage 导入/Horizontal/RTL 说明与 API Reference，移除 Examples 标题并把重复变体裁剪为四条 `Kotlin extension:` 配方（hover/always/hide delay/lazy）；16 项聚焦测试（50 Tag 滚动/键盘/三图横向/RTL 镜像）、Wasm 52e9c17c、四象限与浏览器 Tags 滚动/Horizontal 横向滚动/RTL 滚动状态通过。残余（已解释）：Horizontal 用 288 高预览复现官方裁切、图片为本地归档同款灰度图、扩展配方为 Kotlin 专有、Canvas 无原生滚动条 DOM，见[scroll-area-r2](../../web-parity/scroll-area-r2/README.md)。

## scroll-fade

- [x] Scroll Fade (`scroll-fade`)：修复该页在 Web 上整页空白（No Overflow 配方无固定高度的 verticalScroll 触发无限约束异常），改为无溢出时不挂滚动；isKotlinExtension 标记显示 "Kotlin extension"；官方七配方顺序/标题/说明、Usage 规则列表与 Fallback 指南补齐，Rows 改为 Kotlin extension；32 项聚焦测试、Wasm 52e9c17c、四象限与浏览器默认中部滚动/横向滚动/RTL 横向滚动状态通过。残余（已解释）：Installation 为全站 Gradle 块而非 npm/CSS、class 表改为规则列表、Fallback 在 RTL 之后且扩展项增多、rhea 预览近似、CSS scroll-timeline 由 ScrollState 驱动，见[scroll-fade-r2](../../web-parity/scroll-fade-r2/README.md)。

## select

- [ ] Select (`select`)：r1 按当前官网重建页面：官方七配方顺序（demo、Align Item With Trigger、Groups、Scrollable、Disabled、Invalid、RTL）+ Compose-only 示例统一 `Kotlin extension:` 前缀、`examplesHeading` 移除并补 Composition 树；新增 Align Item（Switch+alignItemWithTrigger）、Groups（双分组+Separator）、Scrollable（长时区列表）、Disabled（整表禁用）、Invalid（Field+Select invalid）、RTL（三语工具栏/Arabic）并复用原 demo。浏览器验证 demo 打开/选择、长列表弹出滚动、RTL 阿拉伯语、四象限 1440/390×浅深，SelectExamplesTest 7 项与 Wasm 构建通过；typeahead/碰撞翻转/动态重排未在浏览器取证，原生 listbox 语义缺失，见[Select r1](../../web-parity/select-r1/README.md)。

## separator

- [x] Separator (`separator`)：r3 将当前官方 `main` 固定为 `42f3bb63…`，核对 MDX、`separator.tsx`、五个示例和 Nova CSS 与 r1/r2 的历史固定文件字节一致；10 项 core、2 项断点、16 项配方和 20 项目录页测试合计 48 项通过，并在当前 Wasm 构建上复查 1440/390×浅深官方/本地四象限。原生 HTML separator role 作为明确的 Compose 平台差异记录，见 [separator-r3](../../web-parity/separator-r3/README.md)。

## sheet

- [ ] Sheet (`sheet`)：r1 按当前官网补 Composition 树与三个 API 小节，移除 `Examples` 分组并给原生示例加 `Kotlin extension:` 前缀；Side/No Close Button/RTL 三节补官方 copy 与说明，四官方配方顺序（demo、Side、No Close Button、RTL）已确认。浏览器验证右侧 demo、顶部/左侧面板、无关闭按钮、RTL 镜像，四象限 1440/390×浅深复查，SheetExamplesTest 32 项与 Wasm 构建通过。bottom 侧未单独截图、动画中间帧/焦点恢复与 Nova 单侧 border 逐像素未验，原生 dialog/inert 语义缺失，见[Sheet r1](../../web-parity/sheet-r1/README.md)。

## shimmer

- [ ] Shimmer (`shimmer`)：官方九配方（Default、With Marker、Color、Duration、Spread、Angle、Play Once、Disabling、RTL）顺序/标题/文案已对齐并移除 `Examples` H2，新增 Reverse 配方（官方仅代码）、`Kotlin extension: CSS class mapping`、Fallback/Reduced Motion 说明；四条旧 Kotlin 示例改名 `Kotlin extension: …`；补齐 reverse 参数并重新生成可复制代码。11×4=44 项目录测试通过，Wasm `97fa0dc3…` 下 1440/390 浅深四象限及 demo/Reverse/Play Once/Disabled/RTL 双帧动画、手机与深色浏览器证据已保存。仍待：官方 rhea 预览调色板、`3ch+40px` 精确 spread、自动减弱动态、Fallback/Reduced Motion 位置、Oklab/色域/采样与完整辅助技术；[证据](../../web-parity/shimmer-r3/README.md)。

## sidebar

- [ ] Sidebar (`sidebar`)：已核对官网来源并补桌面/移动端独立状态、响应式切换、Offcanvas内容保留/200ms动画、移动面板与内侧边框；内容滚动及Header/Footer/Group间距也已补齐，34项定向验证及两个ktlint通过。官网配方、浏览器四象限及完整状态矩阵仍待完成，详见 sidebar-r1/r2。

## skeleton

- [x] Skeleton (`skeleton`)：官方顺序（Demo、Avatar、Card、Text、Form、Table、RTL）与文案已对齐，移除 `Examples` H2，RTL 补官方 “in shadcn/ui” 说明，Kotlin 扩展改名 `Kotlin extension: Shimmer`；r1 的七配方尺寸/间距/比例、RTL 三语与脉冲逐帧重组修复保留。core 10/10、目录 40/40 通过，当前 Wasm 构建下 1440/390 浅深四象限、主题、DOM/AX、RTL、Shimmer 和 View Code 交互已复核。减弱动态因官方 `animate-pulse` 无 reduced-motion hook 而保留为明确差异；手机 314dp 头像行沿用官方溢出裁切、390dp 以下容器未覆盖，Canvas 原生语义与共享字体栅格作为平台差异记录，见 [skeleton-r2](../../web-parity/skeleton-r2/README.md)。

## slider

- [ ] Slider (`slider`)：按当前官网重建 7 个官方预览（demo/Range/Multiple Thumbs/Vertical/Controlled/Disabled/RTL）与 Usage/API 说明，Step/Inverted/Commit/Min steps 保留为 Kotlin extension；修复 RTL 方向镜像。7 项聚焦测试、Wasm 构建通过；1440/390 浅深四象限及 range/multiple/vertical/controlled/disabled/RTL 状态已复核。仍余 Kotlin API 表与官方单链接差异、ego 指针无法取得 Compose 焦点致浏览器键盘/拖动证据缺失（键盘由 JVM 测试覆盖）、完整辅助技术/多 thumb 焦点/真机手势，见 [Slider r1](../../web-parity/slider-r1/README.md)。

## spinner

- [x] Spinner (`spinner`)：按当前官网复查八个配方（demo、Customization、Size、Button、Badge、Input Group、Empty、RTL）、描述与 API 表，页面/组件无需改动；SpinnerExamplesTest 7 项与 SpinnerViewportTest 8 项通过，Wasm 构建、1440/390×浅深四象限及七个分节对照通过，Badge 三枚 12dp spinner 单行、Input Group 与 Empty 布局与官网一致。仅余 Canvas 无原生 progressbar 语义与共享页面 chrome/字体差异，见 [spinner-r2](../../web-parity/spinner-r2/README.md)。

## stepper

- [x] Stepper (`stepper`)：项目扩展一致性自查完成：补 Kotlin extension 说明、Usage 导入块、Stepper 与 StepperItem 两个 API 表，并接入共享 `isKotlinExtension` 标识；保留 12 组既有配方；新增 StepperExamplesTest 3 项，Wasm 构建与 1440/390×浅深浏览器复查通过。无官方对照对象，仅余共享页面 chrome 与字体栅格化差异，见 [stepper-r1](../../web-parity/stepper-r1/README.md)。

## switch

- [x] Switch (`switch`)：按当前官网重建为 demo、Description、Choice Card、Disabled、Invalid、Size、RTL 七配方，删除参数矩阵并补官方描述/文案；SwitchExamplesTest 8 项、Wasm 构建、1440/390×浅深四象限与 Description/Choice Card/Disabled/Invalid/Size/RTL 分节对照通过。Canvas 无原生 role=switch/label-for，Choice Card 由目录配方绘制边框/选中底色，共享页面 chrome 与字体栅格化属既有差异，见 [switch-r1](../../web-parity/switch-r1/README.md)。

## table

- [x] Table (`table`)：40-handoff 完成。固定当前官方 `main` 为 `8ab27715521cbd4f87ae6886cd989cbb678f6de7`；官方顺序/文案、Composition/API、colSpan 与共享行权重、行 hover/selected、水平溢出、RTL/Hebrew 和四象限均有源码、48 项聚焦测试、当前 Wasm 构建及 35 张浏览器证据。ktlint 仅命中既有无关文件；平台/文档壳与 Kotlin 扩展残余已明确记录。见 [Table r1](../../web-parity/table-r1/verify-30c/summary.json) 与 [`.agent-runs/table/progress.md`](../../../.agent-runs/table/progress.md)。

## tabs

- [x] Tabs (`tabs`)：r2 固定当前官方 `main` 为 `42f3bb63…`，确认 demo/Line/Vertical/Disabled/Icons/RTL 顺序与文案、Composition/API 和 Automatic 激活语义；将 indicator 过渡修正为 150ms 并新增中间帧扫描。Tabs 聚焦 75/75、当前 Wasm 构建、1440/390×浅深 56/56 浏览器断言及 11 张必需截图通过，覆盖键盘激活、隐藏面板焦点回收、动态/视觉顺序、垂直导航与 RTL。Canvas 无原生 HTML tab/selected/disabled 属性作为明确平台差异，见 [`tabs` handoff](../../../.agent-runs/tabs/progress.md) 与 [Tabs r1](../../web-parity/tabs-r1/README.md)。

## text

- [x] Text (`text`)：项目扩展一致性自查完成：Usage 与所有示例代码中已不存在的 `TextTypography`/`TextStyles` 改为当前 `TextVariant`/`TextColors`；新增 Variants（全部类型刻度）、Muted、Kotlin extension: Style font-size 示例与 Text API 表；共享 ComponentDoc 增加 `isKotlinExtension`，扩展页头部改为 `Kotlin extension` 而非 Base UI 页签；TextExamplesTest 5 项与 1440/390×浅深浏览器复查通过。无官方对照对象，仅余共享页面 chrome 与字体栅格化差异，见 [text-r1](../../web-parity/text-r1/README.md)。

## textarea

- [x] Textarea (`textarea`)：按当前官网重建为 demo、Field、Disabled、Invalid、Button、RTL 六配方，补官网描述与 Usage；删除 22 项重复参数矩阵，只保留 Rows、Min/Max lines、Max length、Autofocus、Read only 五个 `Kotlin extension:` 配方；新增 TextareaExamplesTest 7 项，Wasm 构建、1440/390×浅深四象限与 Field/Disabled/Invalid/Button/RTL 分节对照通过。仍余 Canvas 无原生 resize grip、无 htmlFor 式 label→control 焦点关联及共享页面 chrome/字体差异，见 [textarea-r1](../../web-parity/textarea-r1/README.md)。

## toast

- [ ] Toast (`toast`)：r2按当前官网8a3536678a复核Base UI Toast页面：Default/Types/Promise三配方与官方标题/描述一致，补Types/Promise官方文案并隐藏通用Examples标题；仅保留Position/Duration/Close button三个Kotlin扩展，其余20+参数矩阵不再渲染。24项参数化测试通过（1440/390×浅深，含源消息/Undo id、五种type播报、Promise切换与位置扩展）；Wasm 97fa0dc3…构建后四象限（官方背景像素逐张校验）与浏览器默认toast已验：共享Toaster在窗口右下固定位置出现、5秒后自动消失，非预览内绘制，上一轮“预览内Toaster”证据已作废。仍开放：Action说明章节省略（Default配方已展示Undo）、Base UI完整管理器选项/堆叠手势/精确动效未按页面配方展开，故保持未勾选，详见[toast-r2](../../web-parity/toast-r2/README.md)。

## toggle

- [ ] Toggle (`toggle`)：r1 按当前官网重建为 demo/Outline/With Text/Size/Disabled/RTL 六段（Sizes→Size、With text→With Text，新增三语 RTL Bookmark 配方），Outline/Size/RTL 补官方 copy、其余示例统一 `Kotlin extension:` 前缀并移除 `Examples` 分组；组件未改。浏览器验证 Bookmark 按下态、Size 段、RTL 阿拉伯语按下态，四象限 1440/390×浅深复查，ToggleExamplesTest 5 项与 Wasm 构建通过。非对称图标 padding、hover/pressed 过渡与 invalid overload 矩阵未逐帧截图，见[Toggle r1](../../web-parity/toggle-r1/README.md)。

## toggle-group

- [ ] Toggle Group (`toggle-group`)：r1按当前官网8a3536678a重建页面：官方八配方Default/Outline/Size/Spacing/Vertical/Disabled/Custom/RTL按官网顺序、标题、描述与内容实现（含官方Composition与API表），删除旧18项参数矩阵，仅保留Single select与Connected两个Kotlin扩展。新增ToggleGroupExamplesTest 5/5（顺序文案、Outline字面all选中、单选取消、纵向堆叠、禁用不切换）；Wasm 97fa0dc3…构建后四象限（官方背景像素逐张校验）与浏览器Italic选中、ArrowRight焦点、Tab移出已验。仍开放：Changelog章节省略、自定义64dp卡片用Modifier.size而非CSS、全部方向键/Tab矩阵由组件测试而非页面配方覆盖，故保持未勾选，详见[toggle-group-r1](../../web-parity/toggle-group-r1/README.md)。

## tooltip

- [x] Tooltip (`tooltip`)：40-handoff 完成。固定当前官方 `main` 为 `42f3bb63b46a69e52a24bd005cdc9ec15ba7a7b9`，五组官方配方顺序/文案、嵌套 Provider 延迟与 skip、侧向箭头偏移、焦点/Escape、兄弟关闭所有权和三语 RTL 均有源码、聚焦或浏览器证据；36/36 聚焦测试与当前工作树 Wasm distribution 通过。1440×1000/390×844 浅深四象限、hover、Tab、Escape 与 RTL 通过；官方原生 button 可访问名称在 Compose Web Canvas AX 树中缺失，作为已解释平台差异记录。见 [tooltip-r3](../../web-parity/tooltip-r3/audit.json) 与 [`.agent-runs/tooltip/progress.md`](../../../.agent-runs/tooltip/progress.md)。

