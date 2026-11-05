# Web 对齐进度

更新：2026-11-05。计划见 [shadcn-web-plan.md](shadcn-web-plan.md)。

前八批基础实现及第九批六个组件的 Web 剩余实现已完成；本批详细范围与验证见下方倒序进度记录。
第九批相关回归、像素与 JVM/JS/Wasm 验证见倒序记录；第八批及之前的证据保留。
整站逐组件、逐状态验收仍未完成。清单中的未勾选项需区分“尚未开始核对”和“基础已验证、完整矩阵待关闭”；历史失配另见各批基准对照记录。

质量验证口径更正（Message Scroller 第二轮发现）：历史记录中的“所属模块四项质量检查通过”指任务退出成功；默认 `:shadcn:core:detekt` 与 `:app:shared:detekt` 实际 sourceCount=0，并未分析公共源码。真正覆盖本轮改动的是两个 `detektMetadataCommonMain` 任务；本轮实际执行已发现大量规则报告，详情和范围见文末。既有格式/JVM/截图/Web结果仍按各自证据成立，不能据默认detekt成功推断完整静态分析通过。

## 本轮基线

- 直接浏览官方和现有线上 `/button` 页面，使用同一浏览器视口 1095×687。
- 官方截图：[shadcn-button-before.png](web-parity/shadcn-button-before.png)。
- 线上旧版截图：[compose-button-before.png](web-parity/compose-button-before.png)。
- 源码依据：Base UI `button.tsx`、`style-nova.css`、`component-preview-tabs.tsx`，读取于 2026-10-28。
- 仓库已有 Button shape/波纹修复和其他未提交文档修改；本轮保留，不算作新增完成项。

## 初始差异（修复状态见交付检查）

- 默认预览为 Vega + Zinc + 4 基础圆角，与项目声明的 base-nova 目标不符。
- 旧页面顶栏 56 高、正文铺满、页面标题 24、额外 Breadcrumb/Preview & Code 标题。
- 预览左上对齐；代码与预览互相替换；官方预览居中且代码在下方展开。
- 默认 Button 示例内容和变体不同（官方为 Outline Button + Submit 图标按钮），本轮已按该结构修改。
- Button Destructive 的底色、内容色、hover、focus 色与 Nova 不符；
  Primary hover、Secondary hover、Outline 暗色背景和小尺寸间距也需调整。
- 窄屏抽屉存在，但没有打开入口；Components 导航停留当前页面，未真正跳转。
- Web HTML title 仍为 KotlinProject。

## 初查发现的组件缺口（第三批已补公开实现）

- `code-block`：官方是公开的组合组件（Header/Title/Actions/CopyButton/Content/Line/Footer、
  destructive、编号、增删及高亮行）。初查时本项目只有目录应用内部的简单 CodeBlock；第三批已新增 `shadcn/core` 公开组合 API。
- `trace`：官方有 Line/Icon/Title/Description 和 Collapsible 组合，含 running/error/pending 状态；
  第三批已新增组件、目录示例及回归，完整官方示例仍待补齐。

## 阶段进度

- [x] 浏览线上及官方页面并保留初始截图。
- [x] 建立独立计划、逐组件清单和验收规则。
- [ ] P0：比较条件固定与修改后本地 Web 基线。
- [ ] P1：页面骨架基础实现已完成，剩余视觉差异、深链接及完整验收待关闭。
- [ ] P2：逐组件视觉和行为核对（前八批基础已验证，各组件剩余范围见下方）。
- [ ] P3：跨尺寸、深浅主题、截图和交互整体验收。

## 首批交付检查

- [x] Nova / Neutral / 10 基础圆角默认预览。
- [x] Geist 资源与许可、Theme → Text 字体传递。
- [x] 64 高顶栏、可用的窄屏导航入口和 Components 跳转。
- [x] 正文限宽、标题和描述、Copy Page、前后组件导航。
- [x] 居中预览、代码底栏、行号、展开时保留示例状态。
- [x] Button 首屏示例改成官方 Outline + Submit 组合。
- [x] Nova Button 颜色、小尺寸、文字样式修正及定向回归。
- [x] 主题切换文字颜色缓存修复及 Vega/Nova 回归。
- [x] Web 手动浅深主题持久化、系统主题回退及存储不可用时降级；[验证](web-parity/theme-persistence-r1/README.md)。
- [ ] 逐组件/逐状态完整验收（见下方清单）。

## 本轮改动与验证

本轮已实现：Nova/Neutral/10 默认主题、Geist 字体；64 高顶栏；正文限宽、标题、Copy Page 和前后页；
居中预览和下方 View Code；手机导航入口；Button 的 Nova 视觉差异；Web 页面标题。

已通过：JVM/JS/Wasm 编译、Web development distribution 构建；预览展开状态保持和窄屏导航入口测试；
Button 主题切换像素与字号/字重/字体回归；现有 Text 内容色继承回归。
已在本机浏览器查看 1095×687 浅/深主题、390×844 窄屏与导航抽屉。

额外修复：Theme.fontFamily 先前没有进入 Text 的最终排版；
Nova Button 默认 slot 文本也不能依赖当前 Compose 版本未开启的 Style 字体继承。
现已显式传递默认文字样式，并保留调用者 textStyle 的覆盖优先级。
Nova 特有的按钮颜色/边框/字号按 preset 限定，保留其他预设行为。

截图：新增两张 Nova 按钮矩阵；这两张与原有五张 RippleShape 基准均已通过像素校验。
没有批量重录旧图。

浏览器补查发现文字颜色缓存可能在只换主题、不触发尺寸变化时保留旧值；
本轮补充按主题/ambient content color 失效的缓存与 Vega/Nova 两种预设的实时文字颜色回归。
此处不将编译成功或少量浏览器抽查视为所有组件视觉验收完成。

## 尚未关闭的页面差异

- Progress r2发现的滚动条宽度变更导致Canvas初载缩放/居中偏移已修复：显示/隐藏滚动条实际改变clientWidth时通知Compose重测，29组浏览器几何/导航/高密度验证通过。历史图保留，逐帧绘制时序及其余组件状态不据此自动完成，见[viewport-scroll-r1](web-parity/viewport-scroll-r1/README.md)。

- 顶栏仅显示本项目真实存在的导航；官方其他产品页面没有伪造本地入口。
- 侧栏与官方仍有栏目内容、渐隐边界、选中项宽度和细微尺寸差异；当前项目扩展单独列出。
- 组件描述已按官网 80% 限宽、移动 1.05rem 与 `LineBreak.Heading` 调整；Compose 无 `text-wrap: balance`，逐行均衡仍与官网不同，见共享记录。
- 手机 Sheet 沿用组件默认 320px 宽，官方导航尺寸及关闭按钮位置需进一步视觉核对。
- API 参数表已补 Alert/Alert Dialog/Button/Badge/Card/Code Block/Trace/Input/Field/Label/Textarea/Checkbox/Radio Group/Switch/Toggle/Toggle Group/Slider/Input Group/Button Group/Input OTP/Select/Combobox；其余组件需逐项从 Kotlin 源码填写。
- 目录已支持可读 URL fragment、刷新定位、浏览器前进/后退和重复点击同一章节；桌面与手机已验证 Alert。标题自身的 permalink 入口已在 Alert 验证；其他组件未据此视为复核完成，复杂重排后的滚动锚定仍待逐项核对。
- Button 十五组官方示例已基础移植；共享菜单宽度/碰撞/指针焦点、Tabler图形和 expanded/hover/pressed/完整辅助技术仍待验收，见 Button r2；
  含图标的非对称 padding 也需进一步检查。
- Secondary hover 对其他有色 palette 的 OKLCH color-mix 精确性需单独校验。
- Badge 尚需完整对照所有官方示例及交互状态，不能仅凭新增截图标为完成。
- Card 六组官方示例、Image、三语 RTL、间距切换和表单保留已补验；条款原生滚动条、边框占位、字体字重/换行及共享页面垂直节奏仍未对齐，见 [Card r1](web-parity/card-r1/README.md)。
- 通用弹层的 Nova 遮罩颜色/透明度及模糊仍需对照官方样式；本轮搜索使用透明重载。
- Code Block / Trace 已有公开组件与目录页；官方扩展演示、部分复合交互及完整状态矩阵尚未关闭。

## 逐组件清单

`[ ]` 表示尚未完成全部适用状态的视觉及行为验收，不能据此判断是否已经做过基础对齐。
“基础已验证”表示已有本轮实现、定向测试和浏览器证据，后半句列出真实剩余范围；
“尚未开始本轮页面与完整状态对照”表示该组件页还未进入本轮逐页核对。历史对齐结果不自动继承。

- [ ] Accordion (`accordion`)：官网七组配方已逐轮对照；已修固定预览、Horizontal竖排与动态导航、RTL工具栏/Noto字体及20px行框，并补五组行为说明。r11常规六例48张新四象限浏览器图与Multiple全展开8图已复查，37项目录验证通过；共享Web主题持久化、七宽度水平布局已关闭，原生滚动基础桥接已验证。r12把过期342dp窄宽夹具校正到官网实际327dp/245dp内宽，补`multiple = true`文案、Kotlin扩展标题及Base UI API Reference，并完成99项聚焦回归；浏览器四象限仍未在新空间重跑。仍留字形/描述换行/垂直间距、代码面板细节、原生Hebrew标签，以及扩展API/Style/动态slot/辅助技术/触摸矩阵；见[页面布局新记录](web-parity/page-layout-r1/README.md)、[r12记录](web-parity/accordion-r12/README.md)、[组件记录](web-parity/accordion-r11/README.md)及r7–r10。
- [ ] Alert (`alert`)：九组示例四象限、Composition/四个 Kotlin API 小节、说明/源码导出及新目录跳转已验证；共享 focus-visible、说明弹层焦点、URL/标题锚点、代码框/内联背景已验证；操作区默认定位及多按钮调用者布局四象限已补验。r10用当前官网重新确认官方六配方顺序，四条Kotlin专有配方明确加前缀，并修复Copy Page遗漏显式examplesHeading；28项聚焦测试和Wasm构建通过，浏览器仍待新空间复核。全文法、超高操作/任意复杂槽位、窗口重排、辅助技术及细微排版仍待关闭，见[Alert r10](web-parity/alert-r10/README.md)。
- [ ] Alert Dialog (`alert-dialog`)：官方七组预览、Composition/Kotlin API 已补；Media 桌面两列/手机与 Small 居中、24dp 图标、三语两尺寸切换及 Settings 链接已验证；56 项定向测试与六张 Nova 像素基准通过，桌面/手机浅深矩阵已检查。r3重新读取当前官网，校正Basic、Small with Media和Destructive三段说明并使用当前Kotlin API名称；28项聚焦回归及Wasm构建通过，浏览器仍待新空间复核。共用深色 input/border 令牌已在共享主题复核中修正；正文换行/字重及阿拉伯语回退字体、无障碍映射与四张旧 Vega 基准失配仍待关闭，见[Alert Dialog r3](web-parity/alert-dialog-r3/README.md)。
- [ ] Aspect Ratio (`aspect-ratio`)：Default/Square/Portrait/三语 RTL 组件已做基础复核；固定 caption 20dp 行框并修正 Nova Select 真实 Arabic 字体选项高度。r3再次确认当前官网仍为四配方，补精确顺序/文案及`ratio`默认值说明，11项聚焦测试和Wasm构建通过。旧勾选撤回：色域/整数测量、共享页面差异及共享布局修复后的新浏览器矩阵仍未关闭，见[aspect-ratio-r3](web-parity/aspect-ratio-r3/README.md)。
- [ ] Attachment (`attachment`)：六组示例、Nova布局、Group吸附/键盘/渐隐/约束、整卡焦点和onClick hover，以及Features/Accessibility指南、目录/导出已补。第七轮按官网当前SVG修正FileSearch；实际标题14px/500字重和约148px排版字宽已验证，未调粗字体。26项目录回归、JS/Wasm构建通过；旧Vega、字形像素观感、自定义overlay hover、颜色过渡与原生辅助技术仍待关闭。按用户要求暂停逐轮细化，剩余项合并到后续统一处理；第八轮 hover 动画只有定向测试通过，尚未完成浏览器验收。证据见attachment-r2至attachment-r8。
- [ ] Avatar (`avatar`)：2026-10-30 首轮主要差异已补：真实图片、九组官方配方与额外 Fallback、Composition/六个 Kotlin API 小节；修正 fallback 字号、外圈、图标徽标、RTL 徽标及绿色令牌，补齐菜单键盘导航。9 项测试、JS/Wasm distribution 和四象限浏览器检查通过。r2按当前官网确认十组官方顺序，给额外Fallback加Kotlin extension前缀，并把Dropdown前三个条目放入DropdownMenuGroup；25项聚焦测试及Wasm构建通过，浏览器仍待新空间复核。混合尺寸分组计数、图片混合边框、菜单完整焦点/像素矩阵等保留明确后续项，见[avatar-r2](web-parity/avatar-r2/README.md)及avatar-r1。
- [ ] Badge (`badge`)：七个官方页面示例已补齐；桌面/手机浅深28组双方截图及英语/希伯来语8组双方截图已复查；补 href/invalid/逻辑图标边距与 Spinner 固有尺寸修复，23项定向测试通过。r2再次确认当前七配方及自定义色阶，补齐Usage代码缺失的`BadgeVariant`导入并对齐Link文案；30项聚焦测试及Wasm构建通过，浏览器仍待新空间复核。原生链接语义、完整交互/辅助技术和共享页面差异仍开放，见[badge-r2](web-parity/badge-r2/README.md)及badge-r1。
- [ ] Breadcrumb (`breadcrumb`)：2026-10-30 七组官方配方、Composition/七个 API 小节已补；修正 6dp 换行列表、4dp item 间距、14dp 镜像描边分隔符、20/16dp 省略号及链接 hover。r2再次确认当前七配方，三组菜单路径补`DropdownMenuGroup`，额外Disabled改为Kotlin extension标题；23项聚焦测试及Wasm构建通过，浏览器仍待新空间复核。共享手机宽度导致的换行差异、链接/导航原生语义和焦点细节仍明确保留，见[breadcrumb-r2](web-parity/breadcrumb-r2/README.md)及breadcrumb-r1。
- [ ] Bubble (`bubble`)：2026-10-30 九组官方配方、Composition/四个 API 及可访问性指南已补；修正自然宽度/80% 上限、Ghost 全宽、字号行框、Muted/Tinted 颜色和反应行悬浮定位。17 项定向测试、16 张像素基准、JS/Wasm distribution、四象限首屏对照及静态检查通过。任意复杂槽位裁剪/多 Content 布局、按钮反应自动 padding、完整焦点及原生辅助技术矩阵仍待关闭，见 bubble-r1。
- [ ] Button (`button`)：十五组实际配方已补齐，新增公开 buttonStyle/invalid、As Link、完整默认Group菜单；修正Nova背景裁剪、阴影alpha与嵌套Escape。r3重新确认当前官网配方与As Link说明，移除API Reference中重复的根Button属性表，保留命名Button与buttonStyle小节；63项聚焦测试及Wasm构建通过，浏览器仍待新空间复核。子菜单宽度/手机碰撞/指针聚焦、Tabler图形、expanded/pressed及完整状态仍开放，见[button-r3](web-parity/button-r3/README.md)。[Card依赖](web-parity/card-r1/README.md)、r1、r2。
- [ ] Button Group (`button-group`)：基础已验证（第六批）；默认配方、完整菜单、图标、单选状态与响应式Go Back已验证。r2读取当前官网，页面已从旧13参数示例切换为Default、Orientation、Size、Nested、Separator、Split、Input、Input Group、Dropdown Menu、Select、Popover、RTL十二组实际配方；`ButtonGroupItem`新增`size`参数，42项聚焦测试及Wasm构建通过。共享菜单宽度/碰撞、混合子项形状、role=group语义、浏览器与完整状态矩阵仍待关闭，见[button-group-r2](web-parity/button-group-r2/README.md)。
- [ ] Calendar (`calendar`)：2026-10-30 完成首轮公历主要对照：15 组实现、Composition/四组 API、预设/时间/已订日期/价格格/周数/RTL 已补。r2再次读取当前官网，官方十配方顺序一致，额外Multiple/Disabled/周起始/隐藏外部日期/月份范围已明确加`Kotlin extension:`前缀；35项聚焦测试及Wasm构建通过，浏览器仍待新空间复核。波斯历、原生时间输入行为、caption 细节及完整辅助技术/交互矩阵仍保留，见[calendar-r2](web-parity/calendar-r2/README.md)及calendar-r1。
- [ ] Card (`card`)：六组官方示例与完整导出代码已补；Nova垂直根padding、分区水平inset、满宽Footer及公开CardImage已修正，调用方Style优先。93项联合回归、11图像素校验、JS/Wasm通过；官网/本地各56张四象限静态/交互图已复查。原生滚动条、1px边框占位、字体字重/换行、共享Select浏览器键盘及全状态仍开放。[证据](web-parity/card-r1/README.md)。
- [ ] Carousel (`carousel`)：当前官网仍为Default、Sizes、Spacing、Orientation、API、Plugins、RTL七配方；r2确认组件、MDX与七示例相对首轮固定版本字节一致。修复Plugins悬停/拖动结束后不恢复自动播放、各配方未使用公开`CarouselItem`的问题，并把点指示器明确标为Kotlin extension；24项聚焦测试、全仓lint/detekt及JS/Wasm通过，浏览器仍待新空间复核。Embla任意options/plugins、loop、浏览器拖动/动态主题和完整辅助技术仍开放，见[carousel-r2](web-parity/carousel-r2/README.md)及首轮记录。
- [ ] Code Block (`code-block`)：已补公开组合组件、编号/差异行/换行/复制与目录页；扩展示例及完整状态矩阵待验收。
- [ ] Trace (`trace`)：已补公开状态行、折叠详情、键盘/受控回归与目录页。r2按当前官网加入Description、Collapsible、Open State、Group，移除非官方Expanded别名；Open State同时覆盖defaultOpen、受控状态和外部Button。11项聚焦测试、全仓lint/detekt及JS/Wasm通过；Link/Dialog/Sheet/Sources/Chain of Thought/Commit/Whole Turn/Reasoning/Tool及浏览器/辅助技术矩阵仍开放，见[trace-r2](web-parity/trace-r2/README.md)。
- [ ] Chart (`chart`)：本页八组官方配方已迁移，补真实tooltip/键盘选点、系列切换、主题/图标、响应尺寸、数值域与日期轴修复；320高、桌面48/手机0缩进及缩放后阿语轴重叠已处理。r4把Your First Chart保留为h2，Add a Grid/Axis/Tooltip/Legend改为嵌套h3并补Compose步骤说明；页面/文档/预览56项、核心交互/布局/截图40项共96项通过，全仓lint/detekt与JS/Wasm通过。浏览器仍待新空间复核，共享标题/代码细节、窄屏日期刻度和动效/完整交互继续开放，见[chart-page-r4](web-parity/chart-page-r4/README.md)、chart-r1、chart-axis-r2及chart-preview-r3。
- [ ] Checkbox (`checkbox`)：官网八配方、Group/Table/三语RTL及Field布局已补。r2确认当前组件、MDX/Field/Nova与八示例相对首轮固定版本字节一致；修复默认误用Button缩放按压、改为官网150ms颜色过渡，调用方仍可显式选择动画。25项聚焦功能测试、全仓lint/detekt及JS/Wasm通过，浅色截图verify通过；两张深色失配与历史HEAD逐字节一致且未刷新。浏览器仍待新空间复核，原生表单/label关联、CSS命中区、动态主题和完整辅助技术继续开放，见[checkbox-r2](web-parity/checkbox-r2/README.md)及首轮记录。
- [ ] Chip (`chip`)：项目扩展，检查一致性。
- [x] Collapsible (`collapsible`)：已按当前官网 `8a353667` 重建 Default/Basic/Settings Panel/File Tree/RTL 五配方，补 Controlled State 文案与代码、Composition、Base UI API Reference 及 288dp/576dp 预览几何；四个 Kotlin 专有示例保留为 `Kotlin extension:`。8 项聚焦测试、Wasm 构建、1440/390×浅深四象限、四个分节对照及 Default/Basic/Settings/File Tree/RTL 语言切换交互已验证；仅余 Base UI 单变体、sr-only FieldLabel、字体栅格化等已说明差异，见 [collapsible-r1](web-parity/collapsible-r1/README.md)。
- [ ] Combobox (`combobox`)：基础已验证（第七批）；内联编辑、筛选/键盘提交/清空和默认示例已完成，多选 chips、完整辅助技术映射及复杂状态矩阵待关闭。
- [x] Command (`command`)：r11按当前官网8a3536678a重建页面：About、Composition与6个官方配方（demo、Basic、Shortcuts、Groups、Scrollable、RTL，含官网标题/描述/预览内容）按官网顺序实现；删除旧的54项参数矩阵，仅保留4个Kotlin扩展（受控value、自定义filter、长列表Home/End、keywords）。6项新目录测试通过，Wasm构建后四象限与Basic/Shortcuts对话框打开+Escape画面已在浏览器复核。残留：About位于Composition之后（本地渲染器顺序）、CommandDialog居中而官网top-1/3、API表为本地增补、无Radix/Aria标签页与广告，详见[round record](web-parity/command-r11/README.md)。
- [ ] Context Menu (`context-menu`)：保留第九批窗口坐标/边缘翻转、RTL/字母定位、触摸取消与非模态host结果；本轮共享依赖复查发现Escape被根层提前消费，已补两层逐层关闭/恢复焦点，模态与非模态回归通过。面板宽度/ring/阴影、完整页面与四象限需重新验收，历史勾选撤回。见[Button r2](web-parity/button-r2/README.md)。
- [x] Date Picker (`date-picker`)：r10按当前官网8a3536678a重建页面：官方8个配方（demo、Basic、Range Picker、Date of Birth、Input、Time Picker、Natural Language Picker、RTL）按官网顺序与文案实现，含Composition；删除旧38项Calendar参数矩阵，仅保留4个Kotlin扩展（中文本地化、多选、禁用周末、最小/最大月份）。8项新目录测试通过，Wasm构建后四象限与demo弹层打开画面已在浏览器复核。残留：Time Picker用24h文本字段替代原生input[type=time]；Natural Language用Kotlin小解析器替代chrono-node；RTL使用内置阿拉伯日历locale而非date-fns/DayPicker bundle；本地增加API表，详见[round record](web-parity/date-picker-r10/README.md)。
- [x] Dialog (`dialog`)：r1 按当前官网重建页面为官方六配方（Edit profile、Custom Close Button、No Close Button、Sticky Footer、Scrollable Content、RTL），其余 Compose-only 配方统一加 `Kotlin extension:` 前缀后置，并补 Composition 树、Usage 导入块与七个 API 小节；Custom Close 的 448dp、RTL 三语、初始焦点/Escape 焦点恢复、非模态、Sticky/Scrollable 滚动均已在真实浏览器验证，四象限 1440/390×浅深已复查，DialogExamplesTest 10 项与 Wasm 构建通过。Canvas 无原生 inert/aria-modal 语义、无内容弹窗的 Escape 仅有浏览器证据、Base UI `trap-focus`/`initialFocus`/`onOpenChangeComplete` 未映射，见[Dialog r1](web-parity/dialog-r1/README.md)。
- [x] Drawer (`drawer`)：r1 按当前官网重建为官方七配方（Delivery time、Position、Swipe Handle、Nested、Non Modal、Snap Points、Responsive），Compose-only 配方加 `Kotlin extension:` 后置，并补 Custom Sizes/Styling/Migrating from Vaul 章节、Composition 树与七个 API 小节；右侧/左侧/内容自适应/四层嵌套/非模态/496dp peek→全高吸附/桌面 Dialog 响应式均在真实浏览器验证，四象限已复查，DrawerExamplesTest 10 项与 Wasm 构建通过。demo 后 Callout 无放置位、迁移步骤以 Bullets 呈现、预览内 Toast 位置、真机手势与 data-* 属性语义仍见[Drawer r1](web-parity/drawer-r1/README.md)。
- [ ] Dropdown Menu (`dropdown-menu`)：r4按当前官网8a3536678a逐条复核13个配方（demo/Basic/Submenu/Shortcuts/Icons/Checkboxes/Checkboxes Icons/Radio Group/Radio Icons/Destructive/Avatar/Complex/RTL）顺序、标题与文案一致，宽度与官网一致。核心修复：指针打开不再聚焦首行（原缺陷）、键盘ArrowDown/ArrowUp/Enter/Space分别聚焦首/末行、Nova默认内容宽度改为max(anchor,128dp)、子菜单min-w-96、Nova面板以内描边改为官网ring-1 foreground/10（不再吃掉2dp行宽）。18项目录测试通过，2张Nova基准按预期重录并verify通过，Wasm构建后四象限与浏览器指针/键盘/Escape/手机状态已复核。剩余：Compose Web语义树无法等价ARIA role=menu/menuitem，anchor宽度对“触发器宽于显式宽度”的用例仍会取宽，子菜单无显式宽度时不做内容自适应，详见[round record](web-parity/dropdown-menu-r4/README.md)。
- [ ] Empty (`empty`)：r3 按当前官网校正六段配方 copy（Outline/Background/Avatar/Avatar Group/InputGroup/RTL）并给 Usage 补 EmptyMedia，Outline 自然高度、RTL 阿拉伯语、Avatar Group、InputGroup 重新取证；四象限 1440/390×浅深已复查，EmptyExamplesTest 7、EmptyBrowserParityTest 12、core EmptyParityTest 7 共 26 项通过，Wasm 构建通过。平衡/pretty 换行、字重与图形/阴影细节、共享页面排版、原生 HTML 链接与完整键盘/读屏、任意 Style/shape 与大内容仍开放；历史旧 Vega 截图矩阵未重验，见[Empty r3](web-parity/empty-r3/README.md)。
- [ ] Field (`field`)：官方13配方（Demo、Input、Textarea、Select、Slider、Fieldset、Checkbox、Radio、Switch、Choice Card、Field Group、RTL、Responsive Layout）顺序/标题/文案与Composition三小节、Anatomy、Form、Validation/Errors、Accessibility、10项API已对齐；Demo 448宽、分隔线、正常字重复选框、Select全宽、滑块区间说明已修；Nova FieldSet legend→description负边距、dense组12间距、横向*:w-auto、FieldSeparator -8边距与Choice Card 10dp半径已实现；9项目录测试+14项FormNova+4项FieldScreenshot通过，1440/390浅深四象限及Select/Radio/Choice/Switch/RTL三语/Responsive手机实测。仍待：label无htmlFor聚焦、FieldDescription nth-last-2 -4、RadioGroup触发的FieldSet 12间距、官方bullets无悬挂缩进、Textarea原生拖拽柄、多错误列表语义及完整辅助技术；[证据](web-parity/field-r1/README.md)。
- [ ] Hover Card (`hover-card`)：四官方配方桌面/手机浅深已保存关闭和Default/Basic展开证据，三个方向单例已确认，连续切换/重载与本地三语完整Web矩阵仍待补；补实际Trigger锚点、键盘失焦立即关闭、Nova 5dp避让/手机垂直fallback、RTL 320正文/Noto完整行框及Sides亚像素换行。44项回归、4张显式像素verify、lint和JS/Wasm通过；仅两张Nova基准按5px平移更新。safe polygon、多Trigger/嵌套宿主、全局Escape、实际翻转动画及完整辅助技术仍开放。[证据](web-parity/hover-card-r2/README.md)。
- [ ] Input (`input`)：32 高、14/16 响应式字号、dark/disabled/invalid/focus 已修正；复杂组合与全状态矩阵待关闭。
- [ ] Input Group (`input-group`)：基础已验证（第六、七批）；32dp 布局、附件聚焦、禁用/错误和单行按钮附件已修正，button/kbd 负 margin、复杂多控件与完整焦点矩阵待关闭。
- [ ] Input OTP (`input-otp`)：基础已验证（第七批）；连体格、分组、编辑/粘贴/密码语义和默认示例已完成，光标闪烁、invalid API、完整 IME/自动填码矩阵待关闭。
- [ ] Item (`item`)：11配方与10个API部分已基础移植；r2修默认标题自然换行/显式maxLines、Nova Xs菜单上下文padding及单一RTL正文。Item/Spinner共51项常规测试和6张像素verify通过；四组合浏览器证据见[item-r2](web-parity/item-r2/README.md)。窄屏Header的min-content溢出、Web同页链接/target、菜单初始焦点、共享排版与完整动态/辅助技术仍未关闭。
- [ ] Kbd (`kbd`)：六官网配方已完成桌面/窄屏浅深静态、Tooltip/输入及三语对照；修正RTL单一288dp正文/完整语言标签、Button尾部键帽位置和遗漏TooltipProvider造成的600ms延迟。24项常规测试、lint/JS/Wasm通过；显式旧图4通过、2张Tooltip失配保留。附件专用圆角/负margin、ButtonGroup嵌套边框、字形及完整动态/辅助技术仍开放，见[kbd-r2](web-parity/kbd-r2/README.md)。
- [ ] Label (`label`)：基础14 Medium、disabled和点击聚焦已修正；Card依赖复查补Nova等字号/行高的外层行框，Arabic单行14dp/两行28dp，Vega两张既有基准未变；桌面/窄屏标签点击实际聚焦已验。任意字体/emoji/分数行高、复杂Field及辅助技术矩阵仍待关闭，见[Card r1](web-parity/card-r1/README.md)。
- [ ] Marker (`marker`)：八官网配方已完成桌面/窄屏浅深对照，修复自然预览高度、本页链接及文字/图标hover，补说明与完整复制代码；30项常规测试通过，旧图6/7通过且906像素历史差异未变。共享Toast位置、焦点/原生链接能力、表格/代码栏及完整动态/辅助技术仍开放，见[Marker r2](web-parity/marker-r2/README.md)。
- [ ] Menubar (`menubar`)：已读取官方六配方并开始修复；Nova栏样式、菜单上下文/滚动/键盘、选择语义和六组示例已补齐；27项定向验证通过，浏览器及完整状态仍待核验。
- [ ] Message (`message`)：六配方、头像/Footer/ghost布局、元信息排版和API补齐；34项定向验证通过，浏览器四象限及完整状态仍待核验。
- [ ] Message Scroller (`message-scroller`)：真实滚动原语及官网6/11配方已迁移；本轮新增Previous Context、Load History、Visibility，核心78项独立验证、目录31项通过。InputGroupAddon Style与Button调用方尺寸绘制问题已修复，公共版本0.3.14。其余5配方、浏览器四象限、窄屏目录触发器裁剪及原语遗留仍保留；真实公共源码Detekt未通过，详见文末。
- [ ] Navigation Menu (`navigation-menu`)：官网Default/三语RTL的内容、真实导航、图标及768/1024媒体查询已移植，75条文案逐字核对；本轮修复面板链接悬停关闭/重开及Popup导航依赖作用域，公共版本0.3.16。核心22项独立验证、目录18项及两个ktlint通过；共享视口/Root align、浏览器及完整状态仍开放，详见文末。
- [ ] Pagination (`pagination`)：官网Default/Simple/Icons Only/三语RTL已移植；Nova32dp/2dp间隔、响应式文字、RTL箭头、禁用及省略号语义已补，新增内容slot/size/style并修正Select当前值换行，版本0.3.17。核心24、目录16及两个ktlint通过，JS/Wasm成功；旧Vega2图预存失配，浏览器及完整状态仍待关闭，详见文末。
- [ ] Popover (`popover`)：本轮已核对官网来源，修正 Nova 面板间距、圆角、阴影细环与完整行高；原生9项、两项ktlint与JS/Wasm已验证。五组官网配方已迁移，原生24项示例交互已过；默认非模态与焦点行为、动画及浏览器完整状态矩阵仍待完成。
- [ ] Progress (`progress`)：四官网配方桌面/窄屏浅深静态、三语和受控键盘已对照；移除十二重复预览，补Composition/说明/真实API，RTL统一288正文/40inset与Noto字体。41常规项、Nova四图显式verify、JS/Wasm通过；旧Vega四图actual与r1一致。共享初载缩放根因已在[viewport-scroll-r1](web-parity/viewport-scroll-r1/README.md)修复；绘制时序/排版/代码顺序、原生语义和完整交互仍开放；官网轨道点击未生效有可信事件诊断，不计通过。[证据](web-parity/progress-r2/README.md)。
- [ ] Questionnaire (`questionnaire`)：本轮修正 Nova 选项图形/间距/字号、导航与验证、单选输入互斥、提交顺序和受控更新；官网 Default 三题流程已迁移。原生38项通过；其余13组官网配方及浏览器/完整状态矩阵待完成。
- [ ] Radio Group (`radio-group`)：Nova 间距/错误状态、方向键循环、跳过 disabled、RTL/Home/End 已补；Tab 单入口、动态重排和完整矩阵待关闭。
- [ ] Resizable (`resizable`)：官网四配方与三语 RTL 已迁移；Nova 分隔线/grip、0..100%范围、真实鼠标/触摸命中、键盘调节及嵌套比例已补。37项定向验证和两个ktlint通过；浏览器四象限、多面板/resize光标及完整状态仍待完成。
- [ ] Scroll Area (`scroll-area`)：已迁移官网 Tags/三幅照片横向/三语RTL配方；Nova轨道、thumb端点、轨道点击及滚轮、键盘、双轴corner和Lazy拖动已补，49项定向验证与两个ktlint通过，JS/Wasm退出0但仍有诊断。浏览器、任意Root形状继承及完整状态仍开放。
- [ ] Scroll Fade (`scroll-fade`)：七组官网配方、12%/40dp/96dp规则、逐边/尺寸/none、RTL停点及MessageScroller视口遮罩顺序已补；92项定向验证和两个ktlint通过。浏览器四象限、CSS fallback与完整状态仍开放，详见文末。
- [ ] Select (`select`)：基础已验证（第七批）；默认水果示例、选中行定位、键盘/焦点和错误状态已修正，已按官网固定弹层宽度补 Nova 标签不收缩/不换行及 6dp 间距，长标签与希伯来语菜单项回归通过，本轮另补SelectValue当前值单行/省略、窄触发器不再换行裁剪；复杂碰撞/长列表滚动、键入搜索、动态重排与完整 RTL 待关闭。Card浏览器复查观察到指针打开语言菜单后Home/End滚动页面，键盘初始焦点仍需专项核对。
- [ ] Separator (`separator`)：官网五配方桌面/窄屏浅深、三语与767/768断点已对照；移除十个重复旧示例，补说明/API和公共代码导出，RTL改Noto/单一288正文/40inset/20行框，Menu接CSS视口桥接。44项常规测试、lint/JS/Wasm通过；显式6图5通过，旧暗色20像素actual与r1一致。共享排版/字形、平台role及完整动态/辅助技术仍开放，见[separator-r2](web-parity/separator-r2/README.md)。
- [ ] Sheet (`sheet`)：官网四配方及三语RTL已迁移；响应式宽度、动画/焦点、Nova单侧border与shadow、Header/Footer/正文权重和RTL关闭按钮已补。55项定向验证及core/shared两个ktlint通过；浏览器四象限、旧基准审计和完整状态矩阵仍开放，详见文末。
- [ ] Shimmer (`shimmer`)：已修复Style绘制/重组、实际currentColor、浅深高光alpha/OKLCH及九组官网配方；78项定向验证和core/shared两个ktlint通过。字体spread、自动减弱动态、色域/采样误差及浏览器完整矩阵仍开放，详见文末。
- [ ] Sidebar (`sidebar`)：已核对官网来源并补桌面/移动端独立状态、响应式切换、Offcanvas内容保留/200ms动画、移动面板与内侧边框；内容滚动及Header/Footer/Group间距也已补齐，34项定向验证及两个ktlint通过。官网配方、浏览器四象限及完整状态矩阵仍待完成，详见 sidebar-r1/r2。
- [ ] Skeleton (`skeleton`)：七组官网配方及三语RTL已迁移，Shimmer扩展复制代码已统一；脉冲Style逐帧重组49次问题已修复。50项定向测试、两项旧截图验证及core/shared ktlint通过，40张原生截图已检查。浏览器四象限及完整状态验收仍开放，见 skeleton-r1。
- [ ] Slider (`slider`)：基础已验证（第六批）；Create Payout 静态 Progress 已换为真实 50..10000/step50 Slider，金额和键盘 Home/End 已回归；12dp Nova 滑块、填充/尺寸、键盘步进和 commit 已修正，多滑块标签、动态焦点与完整竖向/反转/RTL 手势矩阵待关闭。
- [ ] Spinner (`spinner`)：八官方配方、自定义图形slot及旋转读取阶段已修正；22项测试、既有两图verify、lint与JS/Wasm通过。四种显示组合及三语/演示按钮已对照；共享排版、Empty换行、Item字体、完整动态及读屏矩阵仍待关闭，见Spinner第一轮。
- [ ] Stepper (`stepper`)：项目扩展，检查一致性。
- [ ] Switch (`switch`)：Nova 轨道/滑块尺寸、暗色滑块、150ms 位移、错误 ring 和标签焦点已修正；复杂组合及完整状态矩阵待关闭。
- [ ] Table (`table`)：已补caption-bottom、Header40dp、单元格14/20与p8、行hover/selected、Footer半透明顶线、内容槽和横向溢出；四组官网数据/交互配方及三语RTL已加入。44项定向测试和core/shared ktlint通过，24张原生图已检查；共享自动列宽/跨列、希伯来字体及浏览器完整矩阵仍开放，详见 table-r1。
- [ ] Tabs (`tabs`)：六组官网配方、四面板与三语RTL已迁移；默认Manual、单Tab入口/RTL键盘/动态顺序、隐藏面板状态与焦点、选中chrome及透明focus阴影已修正。58项定向测试、core/shared ktlint通过，44张原生图已检查；150ms颜色过渡、辅助技术及浏览器完整矩阵仍开放，详见 tabs-r1。
- [ ] Text (`text`)：项目扩展，检查一致性。
- [ ] Textarea (`textarea`)：64 最小高、上下 8 内边距、顶部对齐、内容自增高已补；完整组合矩阵待关闭。
- [ ] Toast (`toast`)：本轮进行中，已对照固定上游 Base UI Toast 源码，补 Default/Types/Promise 配方及管理器、图标和卡片样式；43项定向测试通过，堆叠/手势/动效和浏览器四象限仍待验。见 Toast 本轮记录。 Marker r2浏览器补查确认共享Toaster仍位于预览内部，与官网窗口顶部位置不同，需在Toast专轮修正。
- [ ] Toggle (`toggle`)：Nova 字重/行内图标/间距/选中/小号圆角已修正，新增兼容 invalid 重载；非对称图标内边距、完整 hover/pressed 矩阵待关闭。
- [ ] Toggle Group (`toggle-group`)：基础已验证（第六批）；连体样式、单选取消和方向键焦点已修正，Tab 单入口、动态重排与完整纵向矩阵待关闭。
- [ ] Tooltip (`tooltip`)：本轮进行中，已对照八份固定上游源码，接入五组官方配方并修计时、Escape、排版及箭头占位；30项定向测试及core/shared ktlint通过，32张原生图已检查；动效/剩余交互与浏览器矩阵待验。见 Tooltip 本轮记录。

## 进度记录（按创建时间倒序）

按记录创建日期倒序；同日按原追加顺序倒序。旧记录未注明时分秒，不补造精确时间；后续修改不改变创建顺序。

### 共享 Web：描述 80% 限宽、移动字号与断行策略（2026-11-05）

<!-- progress-created: 2026-11-05; order: 161 -->

当前官网组件页描述使用 `text-[1.05rem] text-muted-foreground sm:text-base sm:text-balance md:max-w-[80%]`：小于 640px 为 16.8px/25.2px，640px 起 16px/24px，768px 起再限制为正文宽度的 80%。本地此前是整宽 16sp/24sp，导致所有组件页描述换行与官方不同（Accordion 等页一条长行）。

现按官方规则修改共享文档头：`catalogViewportWidth() >= 768.dp` 时描述 `fillMaxWidth(0.8f)`，`< 640.dp` 时 16.8sp/25.2sp，其余 16sp/24sp，并尝试 Compose 的 `LineBreak.Heading`。Accordion 描述现在折成两行，手机字号与官方一致。CatalogDocumentLayoutTest 14 项、ComponentDocumentationTest 7 项、CatalogNavigationTest 3 项及文档/导航相关 10 项均通过。

仍保留：Compose 没有 CSS `text-wrap: balance`，`LineBreak.Heading` 只改变断行策略、不做逐行均衡；因此具体断点仍与官网不同（Accordion 在 "that" 前后分布不同）。这是字体/断行残差，归入已说明差异，不据此宣称逐像素一致。

### Field：官方十三配方重构与Nova FieldSet/横向布局修正（2026-11-05）

<!-- progress-created: 2026-11-05; order: 160 -->

读取当前官网（`8a353667`）Field MDX、13份示例、`field.tsx` 与 nova 样式，并核对实时 DOM 尺寸：Demo 内容 `max-w-md`=448、legend 16/24 + 6 下边距、description 紧跟 legend 时 -6、FieldSet gap 16、FieldGroup gap 20（`data-slot=checkbox-group` 为 12）、separator 高 20 且 `-my-2`、Input 32 高、choice-card 10dp 圆角 + 10 内边距。

**页面重构：** 旧页是 26 条自造配方并带 `Examples` H2；现改为官方 13 条按序排列，`examplesHeading = null`。`beforeExamples` 为 Composition（H2）+ Field/FieldGroup/FieldSet（H3）、Anatomy（段落/代码/三条要点）、Form；`afterExamples` 为 Validation and Errors、Accessibility；API Reference 覆盖 FieldSet…FieldError 十项；Usage 采用官方 Profile 示例的 Kotlin 版。所有配方使用官方文案与内容：支付 Demo、Input（Username/Password）、Textarea（rows=4）、Select（Department 九项）、Slider（Price Range 200–800）、Fieldset（Address Information）、Checkbox（桌面显示四项 + Sync 描述）、Radio（Subscription Plan）、Switch（MFA 行内）、Choice Card（Kubernetes/VM）、Field Group（Responses/Tasks 含链接与禁用复选框）、RTL（英/阿/希支付表单，复用 RTL 工具栏与 Noto 字体）、Responsive Layout（448 容器断点）。

**组件修正：** 新增 `FieldSetLayout`（Nova）：直接子项 parent-data 标记 legend/description/group，legend→description 负 6dp 边距，`FieldGroup(dense = true)` 映射 `data-slot=checkbox-group` 并使 FieldSet gap 12；横向 `FieldHorizontalLayout` 按 CSS `*:w-auto` 先量控制项自然宽度、再让 `FieldContent`（flex-1）占满剩余，修复 Responsive 输入把标签挤成逐字换行；`FieldSeparator` Nova 改为 20dp 高、`-8/-8` 边距、居中横线 + 背景遮罩标签（`px-2`、14/20 muted）；`FieldTitle` 禁用时 50% 透明度；choice-card `FieldLabel` 圆角改用 `twRadius.lg`（10dp）而非硬编码 8dp。另修正 `FormNovaParityTest` 中 FieldDescription 行高期望（官方 `leading-normal` = 21，非 20）。

**验证：** 9 项 `FieldExamplesTest`（配方顺序/文案/API、legend→description 40dp、radio/choice 选中、checkbox 标签切换、响应式 448 断点、开关标签切换、RTL 阿拉伯标签）通过；core `FormNovaParityTest` 14 项、`FieldScreenshotTest` 4 项通过。Wasm `b3b5e53c…`，四象限（1440×1000/390×844 浅深）与分区对照已保存；浏览器实测 Select 打开/选择、Radio 切换、Choice Card 选中、Switch 开关、RTL 阿拉伯↔希伯来切换、手机 Responsive 垂直堆叠、深色 Select 面板与 Input 聚焦输入。

**未关闭差异：** ①Compose 无 `htmlFor`，普通字段点击 label 不会聚焦/激活控件（复选框/单选/开关配方已显式接线）；②`FieldDescription` 的 `nth-last-2:-mt-1`（在 Field 中为倒数第二项时 -4dp）未实现，影响官方 Input 配方 Password 一处 4dp；③FieldSet 的 `has-[>[data-slot=radio-group]]:gap-3` 无法从独立 RadioGroup 组件标记，Radio/Choice Card 的 legend→description 比官方多 4dp；④Responsive Layout 官方为三条 bullet 列表，目录 description 槽仅渲染段落，现用「• 」前缀行，无悬挂缩进；⑤Textarea 无浏览器原生拖拽手柄；⑥`FieldError` 多错误用行内「• 」而非 `<ul class=list-disc>`（本页无多错误配方）；⑦无 React Aria/Radix UI 标签页与 pnpm/npm 包管理器切换，代码面板为 Kotlin，字体光栅化不同。

### Date Picker：r10 按当前官网重建八配方（2026-11-05）

<!-- progress-created: 2026-11-05; order: 159 -->

Date Picker 以当前官网修订 `8a3536678a…` 重建页面（源码见 [sources.json](web-parity/date-picker-r10/sources.json)）。

- 官方页8个配方：无名 demo、Basic、Range Picker、Date of Birth、Input、Time Picker、Natural Language Picker、RTL；本地旧页为38个 Calendar 参数矩阵，缺少上述配方，原 `[x]` 的页面依据不成立。
- 已按官网顺序与文案重建8个配方及其 Composition，删除旧参数矩阵，仅保留4个 Kotlin 扩展（中文本地化、多选、禁用周末、最小/最大月份）；demo 复用既有 `DatePickerParityExample`，其余配方共用带 `PopoverScope.toggle` 的触发器/日历辅助组件（顺带修复了新触发器最初未接 toggle、无法打开弹层的问题）。
- 验证：`DatePickerExamplesTest` 8/8通过（顺序/文案、demo 选中日期、Range 默认 Jan20–Feb9、Date of Birth dropdown caption、Input 保留官方格式化值、Time 两个字段、自然语言解析、RTL 阿拉伯占位与标签）；Wasm `wasm=7f45661c…` 后四象限重新采集，浏览器打开 demo 弹层（November 2026，今天高亮）并 Escape 关闭。
- 残留（均已在代码注释与本记录说明）：Compose 无原生 `input[type=time]`，Time 用24h文本字段；`chrono-node` 仅JS可用，Natural Language 改为支持 In N days/tomorrow/yesterday/next week 的Kotlin小解析器；RTL 使用内置阿拉伯 `CalendarLocale` 与 `LayoutDirection.Rtl`，不用 date-fns/DayPicker bundle；本地页面额外保留 API 表。证据 [date-picker-r10](web-parity/date-picker-r10/README.md)。

### Command：r11 按当前官网重建六配方与 About/Composition（2026-11-05）

<!-- progress-created: 2026-11-05; order: 158 -->

Command 以当前官网修订 `8a3536678a…` 重建页面（源码见 [sources.json](web-parity/command-r11/sources.json)）。

- 官方页只有6个示例：无名 demo、Basic、Shortcuts、Groups、Scrollable、RTL，另有 About 与 Composition；本地旧页仍是54个参数的矩阵式示例且没有 RTL/About/Composition，原 `[x]` 不准确。
- 已删除旧参数矩阵（Lead同意），按官网顺序与标题/描述重建：`CommandDoc.kt` 提供 demo、About、官方 Composition 与4个精选 Kotlin 扩展（受控 value、自定义 filter、长列表 Home/End、keywords）；新增 `CommandOfficialRecipes.kt` 数据驱动生成 Basic/Shortcuts/Groups/Scrollable/RTL 的预览与导出代码，保证预览与代码同步。
- 验证：新增 `CommandExamplesTest` 6/6通过（配方顺序/文案、官方 demo 内容、Basic 对话框打开、Scrollable 滚到最后分组、RTL 阿拉伯标题、80项列表 Home/End 滚到首末项）；Wasm `wasm=7f45661c…` 后四象限重新采集，浏览器实测 Basic 对话框打开（搜索框+Suggestions行）与 Escape 关闭、Shortcuts 打开。
- 残留：About 位于 Composition 之后（本地渲染器顺序，官网在 Installation 前）；CommandDialog 居中且使用库 Dialog 宽度（官网 top-1/3）；API Reference 为本地 Kotlin 表（官网指向 cmdk 文档）；无 React Aria/Radix 标签页与广告卡片。证据 [command-r11](web-parity/command-r11/README.md)。

### Dropdown Menu：r4 指针聚焦、默认宽度与 ring 对齐（2026-11-05）

<!-- progress-created: 2026-11-05; order: 157 -->

本轮以当前官网固定修订 `8a3536678a528cc012b9e62484290e79d0be410f`（Base UI + base-nova）逐条复核 Dropdown Menu 页与组件，源码与校验值见 [sources.json](web-parity/dropdown-menu-r4/sources.json)。

- 页面：官方首个预览为无名 `dropdown-menu-demo`，其后为 Installation、Usage、Composition、Basic…RTL、API Reference 共15个H2；本地13个官方配方顺序、标题、预览内容与显式宽度（160/128/192/224/176/144）均已与官网源一致；8个原本标题与官方配方并列的Kotlin参数示例改为 `Kotlin extension: …`，排在官方配方之后。r4 未改配方宽度。
- 组件修复：① 指针打开不再自动聚焦/高亮首行，改用 `DropdownMenuInitialFocus { None, First, Last }` 意图：触发器的 ArrowDown/ArrowUp/Enter/Space 记录键盘意图，指针点击保持 None；None 时聚焦面板本体，使后续 ArrowDown 仍能进入菜单。② Nova 根内容宽度改为 `max(anchor, 128dp)`，子菜单默认 `min-w-[96px]`（官方 `w-(--anchor-width)` + `min-w-32`、子菜单 `w-auto min-w-[96px]`）。③ 官方 `ring-1 ring-foreground/10` 是 box-shadow；本地原用 inner border，会吃掉每行2dp宽度，现 Nova 改用 `dropShadow(ringShadow(...))` 外描边，几何与官网一致，Vega 保持原样。④ 折叠/翻转/可用高度上限沿用既有 `PopupPositionProvider`，本轮复测未发现新差异。`MenuStyles.rememberMenuPanelStyle` 仅新增默认保留的可选参数（Lead 已批准），ContextMenu/Menubar 调用不变。
- 验证：`DropdownMenuExamplesTest` 18/18通过（新增指针不聚焦首行、ArrowDown/ArrowUp/Enter 键盘打开聚焦首/末行、默认宽度遵循触发器且不小于128dp、Kotlin扩展标题）；`dropdown_menu_nova_{light,dark}` 因指针打开不再高亮首行而按预期重录，`verifyRoborazziJvm` 仅这两张变化，Vega `dropdown_menu_states_*` 未变；四个界面模块编译通过；Wasm 构建 `wasm=7f45661c…` 后重新采集四象限，并在浏览器复核指针打开（无高亮行）、ArrowDown（首行高亮）、Escape 关闭、390px 指针打开，与官网同浏览器实测的宽度/行高/ring 对照。
- 仍保留：Compose Web Canvas 的语义树无法等价输出 ARIA `role=menu`/`role=menuitem`（当前为 Button/Checkbox/RadioButton + aria-checked/selected）；显式宽度小于超宽触发器的调用会被拉宽到 anchor 宽度；无显式宽度的子菜单只保证96dp最小宽度，不做内容自适应；指针打开时焦点所有者为面板而非触发器（视觉状态与官网一致）。因此保持未勾选，证据见 [dropdown-menu-r4](web-parity/dropdown-menu-r4/README.md)。

### Empty：测试矩阵补记与最终证据（2026-11-05）

<!-- progress-created: 2026-11-05; order: 156 -->

补记测试矩阵（同轮）：按固定上游 `8a3536678a528cc012b9e62484290e79d0be410f` 复核后，除 EmptyExamplesTest 7 项外，又跑通 EmptyBrowserParityTest 12 项（四种显示组合、搜索/清空、三语、Contact support/Learn More 导航）与 core EmptyParityTest 7 项，共 **26 项通过**，三份 JUnit XML 均归档在 [empty-r3/tests](web-parity/empty-r3/tests)。页面 copy 修正（六段说明与 Usage 的 `EmptyMedia`）、Outline 自然高度、RTL 阿拉伯语与四象限浏览器证据不变；未重验的是历史旧 Vega 截图矩阵（12 changed / 4 missing），不能据本轮通过声称旧基准已通过。最终应用仍为 `17aa61fed5cd3f56a86f.wasm`，SHA256 `7f45661cba22c83438633b3dd71bca5bfadb43c773ab974a905c067eaeed9fb0`。

### Empty：官方配方 copy、Usage 与四象限复核（2026-11-05）

<!-- progress-created: 2026-11-05; order: 155 -->

按固定上游 `8a3536678a528cc012b9e62484290e79d0be410f` 重读 Empty MDX、`empty.tsx`、七个配方与 `style-nova.css`，并实时读取当前页面段落。配方顺序仍为 demo、Outline、Background、Avatar、Avatar Group、InputGroup、RTL；每节有一句说明，Usage 的 EmptyHeader 含 `EmptyMedia variant="icon"`。

本轮校正页面 copy：Outline → `EmptyVariant.Outline`（border utility）；Background → `background` style（bg-*/bg-gradient-*）；Avatar/Avatar Group → `Use the EmptyMedia component …`；InputGroup → `You can add an InputGroup component to the EmptyContent component.`；RTL → RTL 配置指南链接 + 三语工具栏说明。Usage 代码补 `EmptyMedia(variant = EmptyMediaVariant.Icon)`。组件未改：Outline 仍由预览承担 384dp 与 40/24dp inset、Empty 自然高度；RTL 64dp 工具栏 + 288dp 正文 + 40dp inset + 打包 Noto；Avatar 48dp 灰度、Avatar Group 40dp 叠放、InputGroup 搜索/清空/支持链接保持 r2 状态。

验证：EmptyExamplesTest **7 项通过**（七配方顺序、六段官方 copy、Usage EmptyMedia、Learn More 导航、48dp/灰度头像、Avatar Group 40dp 步进、InputGroup 编辑/清空/链接、RTL 三语顺序）；浏览器重拍 Outline/Background/Avatar Group/InputGroup/RTL 阿拉伯语，四象限 1440/390×浅深已复查；Empty 页无 toast，共享 toast 位置问题不适用于本页。Wasm 构建成功，最终应用 `17aa61fed5cd3f56a86f.wasm`，SHA256 `7f45661cba22c83438633b3dd71bca5bfadb43c773ab974a905c067eaeed9fb0`。

保留差异：text-balance/text-pretty 未实现导致多处换行不同；深色字重/图形/阴影/圆角细节、头像缓存/缩放采样差异；Composition 前引导句与共享页面标题/节奏/代码底栏/API 表排版；原生 HTML 链接语义、读屏与完整键盘/hover/focus、任意 Style/shape、极端约束与大内容；历史 Vega 截图矩阵未重验，r2 的 25 项完整矩阵本轮未重跑。

### Drawer：官方七配方、章节与手势/snap 复核（2026-11-05）

<!-- progress-created: 2026-11-05; order: 154 -->

按固定上游 `8a3536678a528cc012b9e62484290e79d0be410f` 重读 Drawer MDX、`drawer.tsx`、七个官方配方与 `style-nova.css`，并实时对照当前页面。官方顺序为 demo（delivery time）、Custom Sizes、Styling、Position、Swipe Handle、Nested、Non Modal、Snap Points、Responsive、Migrating from Vaul、API Reference，demo 后还有一条 Base UI 迁移 Callout。本地旧页缺这些章节与 Composition/API Reference，官方七配方顺序/标题错位，缺 Responsive，Nested 触发文案不同。

本轮重建示例为官方七配方 + `Kotlin extension:` 后置：Delivery time（公共 ShadcnToast 确认提示）、Position（左抽屉 Move Goal）、Swipe Handle（内容自适应 + 拖动条）、Nested（四层，父层保持挂载）、Non Modal（无遮罩、页面可交互且不因外部点击关闭）、Snap Points（496dp/全高，手柄实拖吸附）、Responsive（桌面 Dialog/窄屏 Drawer）。补 Custom Sizes、Styling 前置章节与 Migrating from Vaul 后置章节、Composition 树、Usage 导入块和七个 API 小节；官方配方 footer 的 Close 改为 `fillMaxWidth()` 以匹配 flex-col 拉伸；demo 根布局改用居中的 Box 避免共享 Toaster 撑开预览。

验证：DrawerExamplesTest **10 项通过**；浏览器验证 desktop demo、左抽屉、Swipe Handle、四层嵌套逐层打开、非模态点击页面仍打开、Snap peek→拖动吸附全高、Responsive 桌面 Dialog，四象限 1440/390×浅深已复查。Wasm 构建成功，最终应用 `17aa61fed5cd3f56a86f.wasm`，SHA256 `7f45661cba22c83438633b3dd71bca5bfadb43c773ab974a905c067eaeed9fb0`。

保留差异：demo 后 Callout 无页面壳槽位；Migrating 的 7 个 Steps 用 Bullets+代码近似（标题层级不同）；demo toast 渲染在预览右下角而非 sonner 的视口固定位置（共享 toast 位置问题）；data-* 属性/`--drawer-*` 变量无可直接对应物、改用 Compose 参数；四向拖动/欠阻尼/真机触摸与 iOS body 定位要求未在本轮重验；官网 drawer 用 base-rhea、本地按项目目标继续 base-nova。

### Dialog：官方六配方、Composition/API 与状态复核（2026-11-05）

<!-- progress-created: 2026-11-05; order: 153 -->

按固定上游 `8a3536678a528cc012b9e62484290e79d0be410f` 重读 Dialog MDX、`dialog.tsx`、六个官方配方与 `style-nova.css`，并实时对照当前页面。官方顺序为 demo（Edit profile）、Custom Close Button、No Close Button、Sticky Footer、Scrollable Content、RTL，正文无 Examples 分组；本地旧页把 33 个 API 示例放在 Examples 下，缺 RTL，缺 Composition/API Reference，且 Custom Close Button/No Close Button 的标题、copy、预览内容均非官网内容。

本轮把页面重建为官方六配方 + `Kotlin extension:` 后置（`examplesHeading = null`）：Edit profile、Share link（只读链接、`sm:max-w-md` 用 `Style { width(448.dp) }` 覆盖）、No Close Button、Sticky Footer（10 段 lorem + 固定 footer）、Scrollable Content、RTL（阿拉伯语默认、工具栏可切英语/希伯来语）。补 Usage 导入块、Composition 树和 Dialog/DialogTrigger/DialogContent/Header-Title-Description/Footer/Close/Overlay-Portal 七个 API 小节，区分 Compose 扩展与 Base UI 属性。组件未改：Nova 面板、4dp 背景模糊、footer `bg-muted/50` + 顶边框、关闭按钮位置与源码一致。

验证：DialogExamplesTest **10 项通过**（官方顺序、extension 前缀、各配方开关、Escape 焦点恢复、禁用关闭、RTL 三语、导出顺序）；Wasm development distribution 成功，最终应用 `17aa61fed5cd3f56a86f.wasm`，SHA256 `7f45661cba22c83438633b3dd71bca5bfadb43c773ab974a905c067eaeed9fb0`。浏览器保存打开/关闭、Escape 焦点恢复、非模态页面交互、Sticky/Scrollable 滚动、RTL 浅深、448dp Custom Close 与四象限拼图；无内容弹窗的 Escape 只有浏览器证据，因 Canvas 无焦点节点时 JVM 键注入不进入 Popup。

保留差异：Canvas 无原生 inert/aria-modal/aria-labelledby 与浏览器焦点陷阱；Base UI `modal="trap-focus"`、`initialFocus`/`finalFocus`、`onOpenChangeComplete` 未映射；448px 用的是 Style 覆盖而 `DialogSize` 仍只有 320/384/512；共享页面（单 Base UI 标签、无广告、Geist 光栅化、手机 375 对 390、Kotlin 代码面板、RTL 回退字体行框）未变。

### Collapsible：当前五配方、RTL 与浏览器四象限（2026-11-05）

<!-- progress-created: 2026-11-05; order: 152 -->

按“已标记完成组件也要复查”重新读取当前官网（`8a353667`）。官方页现为 Default、Basic、Settings Panel、File Tree、RTL 五组预览，另有 Usage、Composition、Controlled State 与 Base UI API Reference；`collapsible.tsx` 只是 Base UI 薄包装、没有组件级样式。本地页仍是 10 月 31 日的 Kotlin 参数矩阵（Always-visible siblings、Default open、Disabled、Keep mounted、Force mount、Uncontrolled、Trigger、Content），与当前页面结构不一致。

页面已按官网重建：Default 为 350dp 订单卡，Basic 为 Card 内 Product details/Learn More，Settings Panel 使用 Card+Field+Input 与图标触发器，File Tree 为 256dp Card、Explorer/Outline Tabs 与递归嵌套 Collapsible，RTL 复用官网语言工具栏与阿/英/希顺序卡文案；预览几何按官网 `h-72`（288dp）+40dp 内边距，Default/Basic/RTL 顶部对齐，File Tree 固定 576dp。补 Controlled State 文案/代码与 Base UI API Reference；`examplesHeading = null`，四个 Kotlin 专有示例移到官方配方之后并加 `Kotlin extension:` 前缀。新增 `CollapsibleExamplesTest` 8 项，锁定配方顺序/标题、Controlled State、Copy Page 标题顺序、Default/Basic/Settings/File Tree 打开行为与阿拉伯语 RTL 文案。

8/8 聚焦测试通过；Wasm development distribution 重新构建（应用 Wasm `ee99559a…`）。浏览器在 1440×1000 与 390×844 的浅/深四象限逐张复查，Basic/Settings Panel/File Tree/RTL 另有分节对照；实际点击验证 Default 展开/收起、Basic 展开、Settings 显示额外输入、File Tree 展开 `components`→`ui`→文件行，以及 RTL 工具栏阿拉伯语切换英语（LTR）后的方向与文案。证据见 [collapsible-r1](web-parity/collapsible-r1/README.md)。

仍保留并说明：官网预览含 React Aria/Radix 变体，本地按项目范围只实现 Base UI；四个 Kotlin API 示例属专有扩展；Settings Panel 的官网 sr-only `FieldLabel` 未移植，原生读屏标签仍缺口；Geist/阿拉伯字体栅格化与单像素抗锯齿差异。Base UI 单变体、原生辅助技术与字体采样因此不计为未说明差异。Editor 排除、Attachment 后置。

### Trace：Description、Collapsible、Open State 与 Group（2026-11-05）

<!-- progress-created: 2026-11-05; order: 151 -->

当前官网Trace的页面结构仍由Trace/Line/Icon/Title/Description与Collapsible组合，官方MDX列出十五个示例。本地原页只有Default、Status及非官方Expanded，缺少Description、真实终端输出、受控打开和Group。

现以当前`8a353667`的Description、Collapsible、Open State、Group四份example source为准补入页面；Expanded改为官方Collapsible，折叠内容使用`CodeBlock`显示真实`node --version`输出。Open State同时包含`defaultOpen`、`open/onOpenChange`和外部Button触发。新增页面测试验证配方顺序、计数/搜索结果、展开/收起、受控外部触发与Group三条内容。

页面5项、核心Trace行为/截图6项共11项通过；全仓ktlint/detekt通过。Wasm开发distribution16.103s退出0并有1条Webpack warning；JS开发distribution33.818s退出0但保留既有`os`/`path`诊断。来源、XML与构建摘要见[trace-r2](web-parity/trace-r2/README.md)。

当前Ego仍无compose-foundation TaskSpace，只有用户空间与无关Product Hunt委派空间；未创建替代空间或接管标签，因此没有新的浅深桌面/手机浏览器验证。Trace保持未勾选；Link、Dialog、Sheet、Sources、Chain of Thought、Commit、Whole Turn、Reasoning、Tool及完整指针/触摸/键盘/减弱动态/辅助技术继续开放。Editor排除、Attachment后置。

### Checkbox：颜色过渡与当前源码复核（2026-11-05）

<!-- progress-created: 2026-11-05; order: 150 -->

当前官网仍为Default、Invalid State、Basic、Description、Disabled、Group、Table、RTL八组配方；当前shadcn/ui `main`为`8a353667`，Checkbox MDX、组件、Field、Nova样式与八份示例均与首轮`96da9cc7`归档字节一致。

官方`cn-checkbox`使用`transition-colors`，没有位移或缩放反馈；本地默认复用了Button的`ButtonAnimation.Scale`。现新增`CheckboxDefaults.Animation=None`与150ms颜色过渡，只动画背景和边框，调用方仍可通过原有`animation`参数显式选择Scale/Bounce/MoveDown。页面API说明同步区分官方默认与Kotlin可选扩展。

核心parity6项、Checkbox截图4项、页面3项、Viewport12项共25项功能测试通过；全仓ktlint/detekt通过。Wasm开发distribution22.680s退出0并有1条Webpack warning；JS开发distribution27.894s退出0但保留既有`os`/`path`诊断。显式像素verify中浅色两图通过；深色focused/states两图失败，其actual与compare哈希同首轮current及HEAD逐字节一致，已归档且未刷新。

当前Ego仍无compose-foundation TaskSpace，只有用户空间与无关Product Hunt委派空间；未创建替代空间或接管标签，因此没有新的浅深桌面/手机浏览器验证。Checkbox保持未勾选；深色旧基准、CSS命中区、HTML name/value/required、原生label关联、触摸、动态主题/shape和完整辅助技术继续开放。Editor排除、Attachment后置。

### Chart：First Chart 层级与 Compose 步骤说明（2026-11-05）

<!-- progress-created: 2026-11-05; order: 149 -->

当前官网Chart仍为Default、Your First Chart、Add a Grid、Add an Axis、Add Tooltip、Add Legend、Tooltip、RTL八组页面预览；当前shadcn/ui `main`为`8a353667`，Chart MDX、组件、八份示例、Nova样式与Recharts数值来源均与首轮`96da9cc7`归档字节一致。

发现页面结构仍把所有增量步骤渲染为同级h2，且四步没有Compose说明。现为`ComponentExample`增加通用`headingLevel`，同时用于页面标题、TOC嵌套和Copy Page Markdown；Your First Chart保持h2，Grid/Axis/Tooltip/Legend改为h3。四步分别补充数据/配置、`showGrid`、`showXAxis`、tooltip slot和legend content的Kotlin用法，不把Compose实现误写成Recharts运行时。

页面与交互36项、Preview Frame8项、文档8项、代码块4项、核心Chart axis/interaction/numeric/presentation/scale/screenshot共40项，合计96项通过；全仓ktlint/detekt通过。Wasm开发distribution23.254s退出0并有1条Webpack warning；JS开发distribution28.466s退出0但保留既有`os`/`path`诊断。来源、XML与构建摘要见[chart-page-r4](web-parity/chart-page-r4/README.md)。

当前Ego仍无compose-foundation TaskSpace，只有用户空间与无关Product Hunt委派空间；未创建替代空间或接管标签，因此没有新的浅深桌面/手机浏览器验证。Chart保持未勾选；Recharts精确几何/动画、复杂config/payload、locale rounding、窄屏日期刻度和完整指针/触摸/键盘/辅助技术矩阵继续开放。Editor排除、Attachment后置。

### Carousel：自动播放恢复、CarouselItem 与当前页面边界（2026-11-05）

<!-- progress-created: 2026-11-05; order: 148 -->

当前官网仍为Default、Sizes、Spacing、Orientation、API、Plugins、RTL七组官方配方，点指示器是本地Kotlin扩展。当前shadcn/ui `main`为`8a353667`；组件、MDX与七份示例的SHA-256均与首轮`96da9cc7`归档一致，没有新增官方页面结构。

Plugins配方原先遇到hover或拖动即永久停止自动播放，与官网`onMouseEnter=stop`、`onMouseLeave=reset`不一致；现改为hover与drag都结束后恢复，并补充纯状态生命周期回归。渲染及导出代码现统一使用公开`CarouselItem`包裹页面，不再直接放裸`Box`；Compose无法直接表达`aria-roledescription`，API文档要求通过slide级`contentDescription`补充位置语义。额外点分页标题改为`Kotlin extension: With dot indicators`。

页面7项、核心parity5项、截图12项共24项通过；全仓ktlint/detekt通过。Wasm开发distribution21.609s退出0并有1条Webpack warning；JS开发distribution48.496s退出0但保留既有`os`/`path`解析诊断。来源、XML与构建摘要见[carousel-r2](web-parity/carousel-r2/README.md)。

当前Ego仍无compose-foundation TaskSpace，只有用户空间与无关Product Hunt委派空间；未创建替代空间或接管标签，因此没有新的浅深桌面/手机浏览器验证。Carousel保持未勾选；Embla完整options/plugins对象、loop、浏览器拖动/hover、动态主题字体、任意宿主尺寸、完整焦点/辅助技术与状态组合继续开放。Editor排除、Attachment后置。

### Card：当前源码复核与 Kotlin 扩展边界（2026-11-05）

<!-- progress-created: 2026-11-05; order: 147 -->

当前官网仍为Default、Size、Spacing（含Edge to Edge）、Image、RTL六组；API Reference仍为七个官方组件，不包含`CardImage`。shadcn/ui当前`main`已从Card首轮固定的`4aaea19d`前进到`8a353667`，但两次提交之间只改动`apps/v4/registry/directory.json`，九份Card、MDX、示例、图片与Nova来源均保持字节一致，因此未新增源码或配方。

页面API文案已按当前上游校正`CardHeader`、`CardDescription`、`CardAction`和`CardContent`；本地`CardImage`小节明确标记为`Kotlin extension`并说明它是Kotlin-only图像区域。页面导出测试新增Action逻辑末端语义及`CardImage`边界断言。页面20项、核心Nova parity5项、Spacing contract5项、Card截图7项共37项通过；全仓ktlint/detekt通过，Wasm开发distribution14.686s退出0并保留1条既有Webpack warning。来源、XML与构建摘要见[card-r2](web-parity/card-r2/README.md)。

当前Ego仍无compose-foundation TaskSpace，只有用户空间与无关Product Hunt委派空间；未创建替代空间或接管标签，因此没有新的浅深桌面/手机浏览器验证。Card保持未勾选，原生HTML滚动条/边框占位、字体与换行、共享页几何、Select键盘初始焦点、完整链接/焦点/禁用/触摸/辅助技术矩阵继续开放。Editor排除、Attachment后置。

### Calendar：官方十配方与 Kotlin 扩展标识（2026-11-05）

<!-- progress-created: 2026-11-05; order: 146 -->

当前官网Calendar为Default、Basic、Range Calendar、Month and Year Selector、Presets、Date and Time Picker、Booked dates、Custom Cell Size、Week Numbers、RTL十组。本地前十年保持原顺序，随后Multiple、Disabled、Week starts on Monday、Hide outside days、Month bounds五组现明确加`Kotlin extension:`前缀；底层预览与生成代码仍使用原始配方名，不改交互逻辑。

页面/交互6项、文档8项、核心Calendar parity9项、Calendar截图10项、DatePicker截图2项共35项通过；Wasm开发distribution16.701s退出0并保留1条Webpack warning，全仓ktlint/detekt通过。来源、XML与构建摘要见[calendar-r2](web-parity/calendar-r2/README.md)。

当前Ego仍无compose-foundation TaskSpace，只有用户空间与无关Product Hunt委派空间；未创建替代空间或接管标签。因此没有新的浅深桌面/手机浏览器验证，Calendar保持未勾选；波斯/Jalali转换、原生时间输入、caption细节和完整辅助技术/触摸继续开放。Editor排除、Attachment后置。

### Button Group：当前十二配方与 Item 尺寸 API（2026-11-05）

<!-- progress-created: 2026-11-05; order: 145 -->

当前官网Button Group已扩展为Default、Orientation、Size、Nested、Separator、Split、Input、Input Group、Dropdown Menu、Select、Popover、RTL。旧本地页面仍是Outline、Spacing、Disabled等13个参数化示例，现已整体替换为当前页面结构。

公共`ButtonGroupItem`两个重载新增兼容的`size: ButtonSize = Md`参数，用于官网按单个Button控制尺寸的Size配方。新页面加入真实尺寸、嵌套、分隔、Split、搜索输入、InputGroup、Dropdown、Select、Popover及三语RTL预览。页面16项、Followup12项、文档8项、核心截图6项共42项通过；Wasm开发distribution23.937s退出0并保留1条Webpack warning，全仓ktlint/detekt通过。摘要与XML见[button-group-r2](web-parity/button-group-r2/README.md)。

当前Ego仍无compose-foundation TaskSpace，只有用户空间和无关Product Hunt委派空间；未创建替代空间或接管标签。因此没有新的浅深桌面/手机浏览器验证，Button Group保持未勾选；共享菜单自然宽/碰撞、Input混合子项圆角、平台group语义、完整焦点/触摸与辅助技术继续开放。Editor排除、Attachment后置。

### Button：API Reference 去重与当前页面复核（2026-11-05）

<!-- progress-created: 2026-11-05; order: 144 -->

重新读取当前官网，十五组配方与本地顺序一致，Cursor与As Link的“链接不要使用Button角色”说明仍匹配。发现ComponentDoc同时设置根`apiProperties`和命名`Button` section，导致页面与Copy Page重复整张Button属性表；现删除根表，只保留详细Button和buttonStyle两节。

新增Markdown导出回归要求`onClick` API行只出现一次。Button页面与Followup 24项、文档8项、核心CallerSize/Icon/Invalid/NoWrap/Nova/截图共31项，合计63项通过；Wasm开发distribution14.878s退出0并保留1条Webpack warning，全仓ktlint/detekt通过。摘要与XML见[button-r3](web-parity/button-r3/README.md)。

当前Ego仍无compose-foundation TaskSpace，只有用户空间和无关Product Hunt委派空间；未创建替代空间或接管标签。因此没有新的浅深桌面/手机浏览器验证，Button保持未勾选；菜单自然宽/碰撞、指针焦点、expanded/pressed、图形与完整辅助技术继续开放。Editor排除、Attachment后置。

### Breadcrumb：当前七配方、菜单分组与扩展标题（2026-11-05）

<!-- progress-created: 2026-11-05; order: 143 -->

当前官网仍为Default、Basic、Custom separator、Dropdown、Collapsed、Link component、RTL七配方。本地顺序一致，最后额外Disabled改为`Kotlin extension: Disabled`；Default、Dropdown、RTL的Documentation/Themes/GitHub现统一放入`DropdownMenuGroup`，复制代码与实际预览一致。

新增官方标题顺序、扩展标题及三处菜单源码结构回归。Breadcrumb页面5项、文档8项、核心Parity5项、截图5项共23项通过；Wasm开发distribution19.626s退出0并保留1条Webpack warning，全仓ktlint/detekt通过。来源、XML与构建摘要见[breadcrumb-r2](web-parity/breadcrumb-r2/README.md)。

当前Ego仍无compose-foundation TaskSpace，只有用户空间和无关Product Hunt委派空间；未创建替代空间或接管标签。因此没有新的浅深桌面/手机浏览器验证，Breadcrumb保持未勾选；共享手机宽度换行、链接/导航原生语义与焦点细节继续开放。Editor排除、Attachment后置。

### Badge：可复制 Usage 导入与 Link 文案（2026-11-05）

<!-- progress-created: 2026-11-05; order: 142 -->

当前官网仍为Default、Variants、With Icon、With Spinner、Link、Custom Colors、RTL七配方，本地顺序、图标位置、Spinner、链接及自定义浅深色阶一致。发现Usage片段使用`BadgeVariant.Default`却只导入`Badge`，现已补`BadgeVariant`导入；Link说明改为官网“render a link as a badge”语义，同时保留本地`href`/`onClick`导航说明。

页面8项、文档8项、核心Badge截图4项、扩展API4项、Nova parity6项共30项通过；Wasm开发distribution13.721s退出0并保留1条Webpack warning，全仓ktlint/detekt通过。来源、XML与构建摘要见[badge-r2](web-parity/badge-r2/README.md)。

当前Ego仍无compose-foundation TaskSpace，只有用户空间与无关Product Hunt委派空间；未创建替代空间或接管标签。因此没有新的浅深桌面/手机浏览器验证，Badge保持未勾选；原生链接语义、完整交互/辅助技术和共享页面差异继续开放。Editor排除、Attachment后置。

### Avatar：当前十配方、Dropdown Group 与扩展标题（2026-11-05）

<!-- progress-created: 2026-11-05; order: 141 -->

对照当前UI官网与Base源码，官方顺序为Default、Basic、Badge、Badge with Icon、Avatar Group、Avatar Group Count、Avatar Group with Icon、Sizes、Dropdown、RTL。本地十组顺序已一致，最后额外Fallback改为`Kotlin extension: Fallback`，不再与官网配方混排。

官方Dropdown把Profile/Billing/Settings放在`DropdownMenuGroup`，Log out在Separator之后；本地复制代码和实际预览现同步该结构。Sizes说明改为官网的`size` prop措辞。回归测试锁定十组官方顺序、Fallback扩展标题、Dropdown Group及文案。

Avatar页面4项、文档8项、核心Parity5项、截图8项共25项通过；Wasm开发distribution17.073s退出0并保留1条Webpack warning，全仓ktlint/detekt通过。来源、XML与构建摘要见[avatar-r2](web-parity/avatar-r2/README.md)。

当前Ego仍只有用户空间和无关Product Hunt委派空间，没有compose-foundation TaskSpace；未创建替代空间或接管标签。因此没有新的浅深桌面/手机浏览器验证，Avatar保持未勾选；混合尺寸分组、图片混合边框、菜单完整焦点/像素与原生辅助语义继续开放。Editor排除、Attachment后置。

### Aspect Ratio：完成状态复查与当前四配方（2026-11-05）

<!-- progress-created: 2026-11-05; order: 140 -->

按“已完成组件也要复查”重新读取当前UI官网及Base源码，页面仍为Default、Square、Portrait、RTL四配方，本地顺序与说明一致。保留的唯一API差异是官网要求`ratio`、Kotlin保留`1f`默认值；该差异已在API说明中明确，且改变签名属于破坏性变更，本轮不静默修改。

新增精确回归锁定四配方、Square/Portrait文案、未设置总Examples标题及`ratio`默认值说明。AspectRatio页面6项、布局3项、截图2项共11项通过；Wasm开发distribution为up-to-date，全仓ktlint/detekt通过。来源与XML见[aspect-ratio-r3](web-parity/aspect-ratio-r3/README.md)。

由于当前Ego仍无compose-foundation TaskSpace，且共享布局修复后没有新的浅深桌面/手机浏览器矩阵，历史上已记录的色域/整数测量及共享页面差异也仍开放，本轮撤回Aspect Ratio勾选。未创建替代空间或接管用户标签；Editor排除、Attachment后置。

### Alert Dialog：当前七配方说明与页面顺序（2026-11-05）

<!-- progress-created: 2026-11-05; order: 139 -->

重新读取当前UI官网、Base组件和七个官方配方，配方顺序仍为Default、Basic、Small、Media、Small with Media、Destructive、RTL，本地结构一致。Basic、Small with Media和Destructive三段说明已经落后：现分别按官网补标题/描述/按钮结构、`DialogSize.Sm`与`AlertDialogMedia`组合，以及`AlertDialogAction`破坏性操作语义；Settings当前页占位说明保留。

页面顺序与说明映射新增精确断言。AlertDialog页面、Media、核心Nova、旧截图及文档导出共28项聚焦测试通过；Wasm开发distribution15.947s退出0并保留1条Webpack warning，全仓ktlint/detekt通过。来源、测试XML和构建摘要见[alert-dialog-r3](web-parity/alert-dialog-r3/README.md)。

当前Ego仍无compose-foundation TaskSpace，只有用户空间和无关Product Hunt委派空间；未创建替代空间或接管标签。因此没有新的浅深桌面/手机浏览器验证，Alert Dialog保持未勾选；正文换行、Arabic回退字体、辅助技术及四张旧Vega差异继续开放。Editor排除、Attachment后置。

### Alert：官方配方边界与 Copy Page 标题导出（2026-11-05）

<!-- progress-created: 2026-11-05; order: 138 -->

重新读取当前UI官网与Base UI示例，官方Alert顺序为Default、Basic、Destructive、Action、Custom Colors、RTL。本地前六项本已一致，但Slots、Title、Description、Multiple actions直接追加，未说明是Kotlin扩展；现分别改为`Kotlin extension:`标题，保留官方示例原名与顺序。

回归测试暴露共享`componentPageMarkdown`遗漏`examplesHeading`：页面会渲染显式Examples标题，Copy Page却丢失。现按页面语义导出该标题，并新增配置标题回归。同时纠正上一轮Accordion把`Kotlin extensions`误设为总Examples标题的问题，恢复官方Basic/Multiple等原名，只让已有`Kotlin extension:`标题表示额外配方。

Alert六页面/导出测试、八核心Nova、四旧Alert截图、两Nova Action截图共28项通过；Wasm开发distribution16.822s退出0并保留1条Webpack warning。全仓ktlint/detekt通过。证据与测试XML见[alert-r10](web-parity/alert-r10/README.md)。

当前Ego仍只有用户空间和无关Product Hunt委派空间，没有compose-foundation TaskSpace；未创建替代空间或接管用户标签。因此没有新的浅深桌面/手机浏览器四象限，Alert保持未勾选；完整语法、复杂槽位、超高Action、重排和辅助技术继续开放。Editor排除、Attachment后置。

### Accordion：当前327dp文档宽度、页面文案与API Reference（2026-11-05）

<!-- progress-created: 2026-11-05; order: 137 -->

先用page-layout-r1的当前官网几何纠正r7–r11遗留的窄宽验证：390 CSS视口扣除15px经典滚动条后正文为327dp，而非342dp；RTL和扩展预览内部因此为245dp。两个预览测试新增固定宽度断言，避免共享布局修复后Accordion夹具继续测旧宽度。

页面源码对照官方MDX补三处：Multiple说明改用公开`multiple = true`写法；额外Kotlin示例继续使用已有`Kotlin extension:`标题而不误设总Examples标题；官方末尾补Base UI API Reference段落及链接。新增文案/导出回归。七套聚焦测试共99项通过：预览24、扩展32、RTL4、页面示例3、文档几何14、核心Nova15、文档导出7；Wasm开发distribution37.315s通过并保留1条Webpack warning。当前窄宽图、测试XML与构建摘要见[accordion-r12](web-parity/accordion-r12/README.md)。

本轮没有浏览器四象限：当前Ego仅返回用户空间和无关的Product Hunt委派空间，没有compose-foundation TaskSpace；未创建替代空间或接管用户标签。因此Accordion保持未勾选；Geist精确换行、代码面板字形、Hebrew省略、辅助技术/触摸及新构建浏览器矩阵继续开放。Editor排除、Attachment后置。

### 文档：拆分计划与执行进度（2026-11-05）

<!-- progress-created: 2026-11-05; order: 136 -->

将 [shadcn-web-plan.md](shadcn-web-plan.md) 收缩为目标、执行顺序与验收标准，删除计划中的批次完成摘要、共享页面修复状态和六个日期化组件增量。组件实施细节继续保存在本文件的 Badge、Button、Card、Empty、Hover Card、Checkbox 及共享 Web 记录中，不改变任何组件的完成状态或剩余差异。

本次只调整文档职责，没有修改组件代码，也没有重新执行组件测试或浏览器验收。历史记录继续按创建日期倒序，同日按原追加顺序倒序。

### Item：标题自然换行、菜单上下文与RTL页面（2026-11-04）

<!-- progress-created: 2026-11-04; order: 135 -->

对照固定上游与真实DOM：普通ItemTitle的flex使类名line-clamp-1不生效，改为默认自然换行，公开maxLines保留Image配方显式单行；Nova Xs在DropdownMenuContent内默认padding0，修三个人员条目换行，保留调用者Style覆盖。RTL重构为单一288dp正文/40dp inset、完整三语及同源公开代码，版本0.3.38。

标题一项、菜单四组合四项先RED后GREEN；Item36+Spinner依赖15共51项常规测试通过，六张既有像素显式verify均通过，未刷新golden；core/shared ktlint和JS/Wasm通过，11输入摘要一致。官网和最终本地四组合静态及交互图与采样诊断见[item-r2](web-parity/item-r2/README.md)。窄屏Header的CSS min-content溢出、同页链接/target、本地菜单初始焦点及共享排版/动态/辅助技术仍开放；错误语言坐标与空白采样帧隔离，不计通过。Item不勾选；Editor排除、Attachment后置，整站继续。

### Web画布：滚动条宽度切换触发精确重测（2026-11-04）

<!-- progress-created: 2026-11-04; order: 134 -->

Progress r2发现的初载缩放已定位到实际Compose 1.12.2依赖源码：DefaultWindowState只监听窗口resize/像素密度变化，文档host显示经典滚动条改变了可用宽度却没有触发重测，bitmap1440×1000被CSS显示为1425×989.578并居中。BrowserDocumentScrollHost现比较overflow修改前后clientWidth，仅实际变化时发送resize通知，文档与Create两方向均处理；未修改内部DOM/CSS、公共组件或版本。

Web ktlint、JS/Wasm及既有滚动绑定/缓存测试1项通过。真实新产物浏览器19个初载/锚点/七宽度重排状态、四组合Create/Back8状态及DPR2两尺寸共29项尺寸验证通过，全部图像已通过8拼图和2张高密度原图复查；返回精确恢复缓存scrollY。见[viewport-scroll-r1](web-parity/viewport-scroll-r1/README.md)。验证覆盖Canvas bitmap/CSS/root原点与尺寸，不扩张为逐帧性能、所有字体排版或全部组件完成；历史浏览器图不自动升级为最新产物验收。继续优先缺少本轮完整页面对照的组件，Editor排除、Attachment后置。

### Progress：四配方页面、RTL字体与真实浏览器交互（2026-11-04）

<!-- progress-created: 2026-11-04; order: 133 -->

继续优先缺少本轮完整页面浏览器对照的组件。删除四官方配方后十二个重复参数预览，扩展能力合并到Kotlin指南/API；补Composition、Label/Controlled/RTL说明及公共完整复制代码。RTL改为单一288dp正文/40dp inset和Noto Arabic/Hebrew，保留完整语言标签、方向及数字格式，不改变公共组件或版本。

核心13+目录28共41项常规测试、目录ktlint及JS/Wasm通过；Nova四图显式verify通过，旧Vega四图仍失配且actual与r1逐字节相同，未刷新。官网/最终本地各16静态+28交互图，经12张最终拼图复查，受控Home/End/Left和三语切换已实测。本地点击约四分之一轨道填充约24%；官网轨道点击未变化，可信指针事件/命中记录保留，不计对齐通过。发现共享初载/resize缩放与空白偏移、一次RTL空白和250ms中间过渡帧，诊断图隔离，延长等待后重拍但不声称相关路径已修复。详见[progress-r2](web-parity/progress-r2/README.md)。共享排版/Composition顺序、原生语义、完整手势/动态/辅助技术及旧图差异仍开放，Progress不勾选；Editor排除、Attachment后置。

### Marker：自然预览高度、本页链接与悬停反馈（2026-11-04）

<!-- progress-created: 2026-11-04; order: 132 -->

按优先尚无本轮完整页面浏览器对照推进Marker。八官网配方改为随内容自然展开，补Features、六组说明与Accessibility；链接由外部官网改为本页#links-and-buttons，文字URL annotation拥有键盘语义、整行指针区域扩展到图标/空白，避免重复激活。链接/按钮hover同时更新文字和图标颜色，按钮触发真实Toast并去除演示波纹。公开复制代码同源生成，不改变公共Marker API或版本。

核心13+既有目录5+新增四组合交互12共30项常规测试通过；显式旧图7张中6张通过，暗色separator906像素actual与r1逐字节相同，未刷新。目录ktlint和JS/Wasm通过。官网/最终本地各32静态+20交互图，经16张拼图复查；四种组合真实滚轮后点击均更新锚点，早期history设置的诊断图隔离。详见[marker-r2](web-parity/marker-r2/README.md)。共享Toaster仍在预览内而非官网窗口顶部，焦点外观/完整原生链接、Variants表格/代码面板及动态/辅助技术仍待关闭；Marker保持未勾选，Editor排除、Attachment后置。

### Separator：五配方页面、RTL字体与CSS视口断点（2026-11-04）

<!-- progress-created: 2026-11-04; order: 131 -->

继续优先尚无完整本轮浏览器页面对照的组件。删除五官网配方后追加的十个重复旧示例，补Vertical/Menu/List说明、Composition与真实Kotlin API，竖线约束/语义扩展集中说明。RTL采用Noto Arabic/Hebrew和Tight20dp行框，正文统一288dp/40dp inset；导出包含公共字体资源及调用入口，不依赖目录helper/测试标签。Menu使用CSS完整视口的768dp阈值，修复滚动条画布宽度导致的断点偏移。

核心14+目录30共44项常规测试通过；显式六张截图五通过，旧Vega暗色20像素actual与r1记录逐字节一致，未刷新。目录ktlint与JS/Wasm通过。官网/最终本地各20静态、8三语及4断点图，错误语言坐标与热态旧边界空白帧隔离说明，详见[separator-r2](web-parity/separator-r2/README.md)。共享排版/换行/字形、平台role和完整动态/辅助技术仍开放，Separator保持未勾选；Editor排除、Attachment后置。

### Kbd：页面四种显示组合、RTL正文与提示延迟（2026-11-04）

<!-- progress-created: 2026-11-04; order: 130 -->

按优先尚无本轮完整页面浏览器对照推进Kbd。复核六组官网配方及固定上游源码，RTL去除共享RtlPreview叠加的高度/padding，改成单一288dp正文与40dp inset，语言标签补完整。Button预览/导出声明尾部图标位置，保留键帽2dp偏移；补五组行为说明。浏览器500ms采样发现示例遗漏TooltipProvider，沿用了未配置时600ms延迟；补官网默认0ms Provider及32ms语义可见回归，早期未展开采样隔离保留。

核心13+目录11共24项常规测试通过，app ktlint与JS/Wasm构建通过。旧图显式verify六张中四张通过，Tooltip浅深各2468像素差异，核心/原测试/配置与起始HEAD一致，actual/compare及输入摘要保留且不刷新。官网与最终本地各24静态、12提示/输入和8语言补充图，见[kbd-r2](web-parity/kbd-r2/README.md)。InputGroup附件直接kbd的圆角/负margin、Tooltip内侧组合边框/特殊padding、字形/换行、共享页面和完整动态/辅助技术仍开放，Kbd保持未勾选；Editor排除、Attachment后置。

### Spinner：八官方配方、自定义视觉与旋转重组（2026-11-04）

<!-- progress-created: 2026-11-04; order: 129 -->

固定上游文档、组件及八配方并对照实时页面，替换旧六个重复基础示例。首屏恢复320dp付款Item，另补八射线Customization、12/16/24/32尺寸、禁用Sm按钮、行内Badge、禁用输入但仍启用Send、Empty/Cancel和三语RTL。Send/Cancel按官网保持无业务回调；导出为完整公共Kotlin组合。浏览器发现自定义图形未指定内容色时不可见，已补主题前景回退及真实墨迹断言。

公共Spinner新增兼容的content重载，尺寸、角度及加载说明由根统一负责；TwAnimate新增状态形式，角度读取延后到graphicsLayer，修复256ms内调用方多重组16次。原Float API、1秒线性旋转、LoaderCircle几何和尺寸覆盖保持。版本0.3.37。

核心7+目录15共22项通过，两张既有Spinner基准显式verify零变化，未刷新。三模块ktlint与JS/Wasm构建通过。官网/本地各32静态、8三语补充、8演示按钮动作对照及剩余差异详见[spinner-r1](web-parity/spinner-r1/README.md)。页面排版/说明缺口、Empty换行、Item字体、完整动态及辅助技术仍开放，Spinner保持未勾选；Editor排除、Attachment后置。

### Checkbox：八官方配方、Field依赖与四种显示组合（2026-11-04）

<!-- progress-created: 2026-11-04; order: 128 -->

按优先未做本轮完整页面对照的组件推进Checkbox。读取固定上游组件/Field/Nova源码和八配方，并核对实时DOM。目录恢复Default四独立控件、Invalid State、Basic、Description、Disabled、Group、Table、RTL及真实公共代码导出；表格使用官网人物/初始Sarah选择，全选与逐行切换时部分选中的表头保持未选中。

公共Field测量阶段识别FieldContent/Checkbox，Nova说明顶对齐与1dp控制偏移，普通横向保持居中，Vega原Row路径保留。DOM纠正最初2dp推断，FieldDescription为14/21、FieldTitle14/20，legend完整行框由18恢复20。新增FieldLabel整卡slot、FieldGroup spacing及FieldSet label legend重载：自然宽居中，legend不纳入正文flex间隔。整卡内边距/内部控制各只触发一次、聚焦真实控制，键盘外环与hover覆盖选中底色按CSS实现。版本0.3.36。

核心5+目录3+四种显示组合12共20项通过，所属lint与JS/Wasm通过。官网/本地各61张主对照（32静态、20交互、8三语补充、1整卡键盘），另保留手机Space8张诊断过程；16张对照拼图与原图已复查。手机初次Space批次截到off状态，已按逐步实际状态替换，错误帧隔离，不按文件名认定通过。旧Vega八图仅两浅色通过，六项失配通过临时HEAD旧实现完整复现，12张actual/compare逐字节一致，未刷新基准。列宽/原生滚动条、字重/换行/焦点绘制、CSS伪元素命中区、原生表单/读屏与完整动态状态仍保留，详见[checkbox-r1](web-parity/checkbox-r1/README.md)。Checkbox保持未勾选，Editor排除、Attachment后置。

### Hover Card：浏览器跟进、触发器锚点与窄屏避让（2026-11-04）

<!-- progress-created: 2026-11-04; order: 127 -->

用户授权恢复专用验收空间后，对照官网四官方配方Default/Basic/Sides/RTL，读取实时DOM并补读Base UI 1.6.0定位源码。Root与Trigger坐标分开记录且只在Popup测量阶段消费；修正额外Root内容推开弹层，以及键盘失焦沿用hover closeDelay。Nova补5dp窗口边距、空间不足时垂直fallback、RTL逻辑交叉轴。RTL正文320dp、40dp inset、完整语言标签/Noto字体与20dp行框；Sides使用字形亚像素advance决定换行，解决245dp内宽因逐项ceil为246px导致手机3+1排列，保留完整按钮绘制和点击区域。

核心27+目录17共44项通过，四张既有像素verify通过；只更新两张Nova基准/发布预览，平移对齐后面板内容RGBA一致，Vega未改。core/shared lint、JS/Wasm通过，最终构建、XML、浏览器对照与剩余差异见[hover-card-r2](web-parity/hover-card-r2/README.md)。公共版本0.3.35。浏览器仅接受16张关闭、8张Default/Basic展开和3张单独方向图；连续批量切换异常、重载几何偏移及本地三语完整Web矩阵未通过，异常图隔离保留。safe polygon/选字保护、多Trigger和任意嵌套宿主、Root外Escape、完整滚动裁切与实际翻转动画原点仍保留，不将静态位置修正视为完整组件完成。Hover Card保持未勾选；Editor排除、Attachment后置。

### Empty：浏览器四象限、Outline高度及RTL（2026-11-04）

<!-- progress-created: 2026-11-04; order: 126 -->

浏览器恢复授权已生效，沿用专用空间1完成七组官网/本地桌面1440/窄屏390浅深各28静态图，另各8搜索与12三语图，24拼图已逐组复查。[实现、来源、测试与明确缺口](web-parity/empty-r2/README.md)。

Outline不再把384dp最小高压到虚线框本身；RTL改64工具栏+288正文/40inset和Noto字体，Nova标题/描述Tight完整行框并移除额外heading。LearnMore保留官网真实a[href=#]的button角色及URL annotation，一个焦点，文字/内边距/Enter单次激活；14dp图标、4dp gap和逻辑end inset按实时CSS修正。Contact support接目录#导航，四象限均实测返回页首。版本0.3.34。

核心7+目录18共25项通过，两张既有Nova基准显式verify，core/shared ktlint与JS/Wasm构建通过，无基准刷新。头像当前URL新下载与本地文件逐字节一致，不凭目视差异改裁切。平衡/pretty换行、字重与头像缓存/缩放采样、页面说明/排版、原生HTML链接能力和辅助技术/完整状态仍保留；历史旧Vega十二changed/四missing矩阵本轮未重验。Empty保持未勾选，Editor排除，Attachment后置。

### Card：六组官方配方、分区布局与长单行文字（2026-11-04）

<!-- progress-created: 2026-11-04; order: 125 -->

六组Default/Size/Spacing/Edge/Image/三语RTL已替换旧目录混合示例，补Composition与八个Kotlin API、完整可复制代码及官网原图。Nova改为仅根垂直padding、Header/Content各自水平inset；Footer保留真实测量/语义范围，首个公开CardImage移除默认顶部inset，显式Style优先，Vega保留旧逻辑。版本0.3.33。[来源、实现及证据](web-parity/card-r1/README.md)。

Card标题/描述/正文补完整CSS行框，Label补等字号行高的外层14dp/两行28dp行框；定位Skia仍会保留内部paragraph leading，未声称内部段落已变成14dp。窄屏Arabic Google文字被单行折行截掉，现按自然宽度不换行，仅反馈层受shape裁剪。最终93项联合回归、11图显式像素校验、core/shared ktlint及JS/Wasm通过。两张Card基准随完整行框变高；两张Button仅各2像素变化；既有圆环基准失配通过HEAD Button隔离确认后校准，其他Ripple/Vega Label基准未变。

官网/本地各56张截图覆盖六配方四象限、四档Spacing、三语切换和条款末尾，另验桌面/窄屏标签聚焦及Forgot链接回到页首。验收使用1440/390×1000鼠标视口，非触摸真机。浏览器发现并保留原生滚动条、边框占位、字重/换行及共享页面节奏差异；语言菜单Home/End实际滚动页面，单独记为待查，不能用原生通过替代。Card/Label/Button仍未全量关闭，Editor排除、Attachment后置。

进度记录现按创建日期与原追加顺序倒序，历史125条正文保留，新记录置顶；脚本持久化同日顺序且重复运行不改变结果。旧记录无时分秒时不补造时间。

### Button：链接、错误绘制及嵌套菜单第二轮（2026-11-04）

<!-- progress-created: 2026-11-04; order: 124 -->

已补 As Link 和公开 Modifier.buttonStyle、Button.invalid，版本0.3.32；完整默认Group菜单在Button/Group两页共用。修正Nova bg-clip-padding及CSS阴影alpha，保留两代旧JVM入口。原生51项定向回归、两张有意更新Nova基准显式verify、core/shared ktlint与JS/Wasm构建通过。[来源、RED/GREEN、基准统计与完整缺口](web-parity/button-r2/README.md)。

官网/本地两个重点配方桌面1440/手机390浅深各28张已目视复查；链接文字/填充区均能导航。菜单单选保持展开，Escape逐层关闭/焦点恢复已修复；两层Dropdown和模态/非模态Context有专门测试。浏览器仍见192dp子菜单宽于官网101.5px，手机夹边覆盖父菜单、指针双重高亮与根焦点环外观差异。按实际证据保留未完成，并撤回Context Menu历史勾选。Button expanded/pressed等完整状态、链接原生HTML能力、Group/Context完整页面及共享排版仍开放；Editor排除、Attachment后置。

此前累积对齐与桌面图标/托盘设置已集中提交为491a06ee；本轮继续独立整理，不把已记录缺口重复标为完成。默认detekt公共源码覆盖限制仍存在。

### Button：逻辑图标边距与十四组配方（2026-11-04）

<!-- progress-created: 2026-11-04; order: 123 -->

官网当前十五组实际docs示例已归档固定版本。本地整理十四组，新增Size四组文字/图标配对、Rounded、Spinner与三语RTL，补Cursor/Composition/API；公开ButtonIconPlacement Start/End/Both遵守Xs8/6、Sm10/6、Md/Lg10/8的普通/图标边距，正确映射RTL，保留两种旧JVM入口及Style最后覆盖。Nova改默认光标，Arabic文字行框30→20dp，按钮高度维持32dp，版本0.3.31。[来源、证据和剩余差异](web-parity/button-r1/README.md)。

回归和浏览器逐步暴露三项真实问题：手机Spinner FlowRow错分两行（官网约250px单行略超245px内区）、Arabic字体leading撑高、桌面/手机RTL自然宽度与CSS不一致。现在非RTL示例保留自然单行，RTL以真实子项测量求自然总宽并限于可用宽后换行、逻辑定位；不依赖不足的Button固有尺寸查询。新增手机root245dp/各行右对齐和桌面五按钮同行断言。17项独立用例通过；两张既有ButtonNova图显式verify通过，未更新Button基准。core/shared ktlint、最终JS和Wasm distribution通过；官网/本地七组各28张桌面/手机浅深图已复查，最终app7c866，见validation与输入哈希。

仍不勾选：As Link缺失（需真实链接语义，不能冒充Button）、Group More Options与完整菜单、Tabler图形、invalid/expanded/pressed/动态主题形状与辅助技术全矩阵、任意固有布局及共享排版差异均明确保留。Editor排除、Attachment后置。

提交路径已接通：.git-hooks/pre-commit增加可选compact Gradle wrapper入口，保留原始ktlintFormat/ktlintCheck/detekt任务、默认开发者流程及git add-u。本批将之前累积的组件复核、共享Web布局、Badge/Button和已授权桌面示例改动集中整理为checkpoint；默认detekt公共源码覆盖限制仍存在，不将默认任务退出0当作完整静态分析。

### Badge：七个官网示例与 Spinner 固有尺寸（2026-11-04）

<!-- progress-created: 2026-11-04; order: 122 -->

按官网实际 MDX 的 Default、Variants、With Icon、With Spinner、Link、Custom Colors、RTL 重写目录及配方，补 Composition/API。组件增加 href、invalid 和逻辑图标边 API，保留旧 JVM 入口；修正 RTL 图标边距及 Arabic/Hebrew 16dp 行高。颜色示例采用官网浅深调色板，RTL 三语言切换方向与字体。安装文档版本同步0.3.30。[完整证据与剩余项](web-parity/badge-r1/README.md)。

真实浏览器发现 Spinner 示例两个 Badge 错分两行：Canvas 未提供固有宽度，FlowRow 的 fit-content 因而少算 Spinner。现在在公开 StyleScope 层记录声明尺寸并补固有查询，保持真实 Style/Modifier 测量优先级。先验证回归失败，再修复并验证动态尺寸和 Modifier 覆盖；最终桌面/手机浅深四种均同行。

最终23项独立测试通过；BadgeNova2图、Spinner2图显式像素 verify，core/shared ktlint、JS编译、Wasm distribution 通过。全仓 ktlintCheck/detekt 退出0，但默认 detekt 公共源码覆盖限制仍在。官网/最终本地各28张，另各8张英语/希伯来语对照均已查看，最终 app147f，哈希/输入/原图/XML见证据目录。仅刷新经过旧实现隔离、与当前结果字节一致且符合官网white10%边框的 Nova 暗色1张基准；浅色和旧Vega4张未变，未宣称旧Vega全部通过。

Badge仍不勾选：原生HTML链接语义、完整hover/pressed/辅助技术、长内容与共享页面/暗色字体/代码差异未关闭。未新增commit，未绕过或修改pre-commit；累积工作树及hook的Gradle wrapper接入待整理。Editor排除、Attachment后置。

### 共享 Web：水平布局、文档滚动与 Create 导航回归（2026-11-04）

<!-- progress-created: 2026-11-04; order: 121 -->

获准新建专用空间后恢复官网/本地验收，后续复用 TaskSpace 1。正文改为居中最大640dp，壳左右8dp，侧栏/目录各288dp，并按CSS视口1024/1280断点显示；390px正文由342变为官网327px，七宽度浅深14项几何测试通过。Web原生滚动条与Compose文档滚动双向同步，指南复用路由缓存，Create保留内部滚动；首轮同步安装及跨路由返回/空锚点处理均已补验证。[详细记录与证据](web-parity/page-layout-r1/README.md)。

完整路由回归发现Create真实intrinsic崩溃，隔离至KitchenSliderRow所在FlowRow查询Slider固有宽高；仅示例提供尺寸回退后修复。最终28项独立测试、shared/webApp ktlint、JS编译及Wasm开发distribution通过；官网/本地八张桌面/手机浅深截图全部目视复查，DPR2在桥接构建已验，最终5f6构建另验Create往返稳定保留800px（同一timeOrigin）。仅等body高度会误读初始Introduction过渡页，现先目视确认目标页后操作，早期瞬态未计通过。垂直节奏、描述均衡换行、顶部/目录/代码细节、完整弹层滚动锁及触摸/辅助技术继续开放，不以本轮关闭全站P1/P3或Accordion。未运行旧golden、全仓提交门禁，未新增commit；Editor排除，Attachment后置。

### 共享Web：手动主题持久化（2026-11-02）

<!-- progress-created: 2026-11-02; order: 120 -->

关闭此前Accordion r7–r11的共享主题重载缺口。App接收启动override和显式变化回调，Web入口读取/保存独立localStorage键；未设置或无效值随系统，手动选择优先，恢复/重组不写入，读写异常不影响当前UI切换。原生宿主保留原默认内存行为。[实现和证据](web-parity/theme-persistence-r1/README.md)。

新应用重建测试及目录导航/预览共12项独立通过，shared/webApp ktlint通过；JS编译、Wasm开发distribution通过（18.809s），保留既有warning。5输入SHA一致。新app42ea70…实际加载，1440/390浅深四象限、系统深色→手动浅色、系统浅色→手动深色、整页刷新和Accordion/Chart跨页导航均复验；各次timeOrigin不同，非仅同页hash。官网持久化也通过真实整页刷新核对。

另临时模拟p2存储SecurityError，验证系统回退、内存切换和重载降级；移除hook后原dark记录未变且恢复。共10张浏览器截图已全部目视复查，未删除存储键。JS仅编译、未单独运行；390不是触摸真机。工作流已结束和清理日志，旧golden/全仓提交门禁未执行、未新增commit。Accordion及其他组件剩余矩阵保持原范围，Editor排除、Attachment后置。

### Accordion 第十一轮：常规六例Web回归与说明（2026-11-02）

<!-- progress-created: 2026-11-02; order: 119 -->

补齐r10行框修改后的官网/本地Default、Basic、Multiple、Disabled、Borders、Card各24状态，另补Multiple全展开各4状态，56图全部通过12张原像素对照拼图复查。发现五组行为说明缺失，现已补入现有描述字段/导出路径，Kotlin API名按实际接口适配。[记录与画面](web-parity/accordion-r11/README.md)。

官网预览300/450/435px保持不变，Multiple全展开桌面自然358px、窄屏478px；双方450px固定外框均裁剪超出正文。本地已实际同时展开三项，桌面缩窄后状态保留。窄屏342/327px列宽导致换行/截断点仍不同，Canvas内部root未测自然高度，不用推断值冒称完全一致。

早期深色误标来自Default整页重载丢失手动主题，已排除；最终56图逐张按实际背景像素确认。24次本地导航记录中4次整页重载，故重拍改为导航后再检查/设置主题。手动主题持久化仍待修。

37项独立目录测试、shared ktlint通过，Wasm开发14.128s通过并保留1条webpack warning；新app289f86…实际加载，17输入SHA一致。新60原生图与r10目视版字节相同；core15未重跑，不累计。工作流已结束清理，旧golden和全仓提交门禁未执行、未新增commit。其余字形/代码面板/原生标签/扩展API/Style/动态slot/辅助技术/触摸继续开放，Accordion未勾选。Editor排除，Attachment后置。

### Accordion 第十轮：官方RTL字体与20px行框（2026-11-02）

<!-- progress-created: 2026-11-02; order: 118 -->

官网computed样式证明阿语/希伯来语正文分别使用Noto Sans Arabic/Hebrew，工具栏仍用Geist；原本只切方向/翻译导致Web系统字体回退。加入固定Google Fonts提交的400/500静态资源，完整OFL许可证和生成脚本保留，四资源重复生成逐字节一致（合计484644bytes）。只替换Accordion RTL正文的字体，其余主题参数继承。

换入真实Noto后单行标题42px回归四象限全部RED（实际52px）。Nova Accordion三条string文字路径统一Center/None/Tight20px行框，原断言转绿。最终核心15+RTL4+Frame24+旧例2+Breadcrumb4=49项独立通过，两个ktlint通过；Wasm开发21.372s通过、1条webpack warning，16输入SHA一致，工作流已清理。见[r10证据](web-parity/accordion-r10/README.md)。

新app a8c35a…及两种Noto各两字重实际加载；12个三语四象限本地图已复查。窄屏阿语第三项浅深均恢复可见，实际点击展开完整支付回答；官网稳定对应状态另存。官网12静态状态复用r9基准并重新核对字体；未声称本轮重拍。原生RTL12+六例Frame48全部已目视复查；后者48图较r7都变化，不能说全局行框未影响旧例。Basic/Borders/Card固定预览仍存在长内容边界裁剪，需要对其余六例补本次变更后的浏览器矩阵。

全局列宽/换行、深色字形观感、原生希伯来语标签省略及扩展/Style/动态slot/辅助技术/触摸和手动主题重载持久化继续开放。Accordion保持未勾选，旧golden/全仓提交门禁未运行，未新增commit；Editor排除、Attachment后置。

### Accordion 第九轮：RTL工具栏与自然高度（2026-11-02）

<!-- progress-created: 2026-11-02; order: 117 -->

官网三语×桌面/窄屏×浅深12状态确认：工具栏64px、Select144×28、正文固定288px/padding40/顶部对齐、Accordion最大448px。目录共用工具栏原本已存在，现抽出原有控制区域供Accordion复用，其他RTL配方正文布局保留。新增RTL指南段落，复制代码移除目录选择器。

初步图像复查抓到固定高度压缩后续条目；新增第三个阿语标题自然高度断言在390浅深均RED。正文改为固定外框内自然高度测量，RED两项转绿。最终RTL4+固定预览24+旧示例2+共享Breadcrumb4=34项独立通过，shared ktlint通过；Wasm开发distribution15.135s通过、1条webpack warning，6输入SHA一致，工作流已正常结束。12原生图已复查，窄屏6图变化。见[本轮证据](web-parity/accordion-r9/README.md)。

官网/本地各12张三语四象限浏览器图均已复查，新app401ff6…实际加载。早期菜单焦点/坐标未就绪导致切换未成功的截图已排除，不计通过。桌面翻译说明弹层可用。Web阿语字体/行高仍不同，窄屏第三项仍被代码边界遮住，不能用原生通过掩盖；全局窄屏342/327px、深色字重/字形、原生希伯来语选中标签省略等继续记录。Accordion仍未勾选；扩展/Style/动态slot、完整辅助技术及触摸矩阵待补，未运行旧golden或全仓提交门禁，未新增commit。Editor排除、Attachment后置。

### Accordion 第八轮：动态顺序与锚点结论更正（2026-11-02）

<!-- progress-created: 2026-11-02; order: 116 -->

新增LTR/RTL反转c,b,a的两项真实UI回归均RED；额外不同宽度RTL回归在Home断言RED，证明不能只给x排序补并列规则。根布局已按官网恢复竖排，现导航统一按实际y顺序，orientation只决定按键轴。最终Nova15+Extension32=47项独立通过，core/shared ktlint通过；8张原生扩展图字节不变。Wasm开发distribution21.084s通过（保留webpack warning），10项输入SHA一致，工作流已清理。见[验证](web-parity/accordion-r8/verification/summary.json)。

撤回此前“缩放后未自动重新定位是本地缺陷”的推断：官网Card标题在390时85.516px，切1440后同hash变成-350.953px；再次点相同目录后回87.547px。本地相同目录点击亦正常，BrowserDocNavigation原有force/revision已处理；未加入与官网不同的强制缩放重定位。新app模块实际加载后，桌面/窄屏浅深四张实际Right/Enter场景已确认Styled?焦点及完整正文，补上r7未成功的桌面验收。[实测/画面](web-parity/accordion-r8/README.md)。

仍留RTL工具栏/Select、其余扩展/Style/动态slot及辅助技术完整矩阵、换行/字形与全局页面细节；全页重载丢失手动主题仍是真实共享缺口，未因锚点更正撤回。Accordion继续未勾选；未运行旧golden或全仓提交门禁，本轮未新增commit。

### Accordion 第七轮：固定预览与orientation故障（2026-11-02）

<!-- progress-created: 2026-11-02; order: 115 -->

复核九份固定上游与当前DOM：Default/Basic/Disabled/Borders300、Multiple450、Card435，align=start、padding40。六组从minHeight改为固定height+顶部对齐；共享固定预览按官网overflow-visible交给外层圆角卡片裁剪，Chart居中与尺寸不变。RTL工具栏在官方preview外，本地仍内部Select，暂未生搬288固定高。官方例标题统一，其他例标Kotlin extension，补Composition树和Usage导入。

新矩阵实际抓到Horizontal故障：焦点移动正确，但第二项正文被零宽挤掉，8项断言失败。官方Root始终flex-col，orientation只影响键盘轴；移除本地两处误用的横Row，恢复默认竖排并保留左右键/RTL导航。8个失败保持正文可见性断言并转绿，补竖排几何断言。[验证](web-parity/accordion-r7/verification/summary.json)：最终Frame24+Extension32+旧目录2+ChartFrame8+core Nova12合计78项独立通过，core/shared ktlint通过；Wasm开发26.837s通过、1条webpack warning，10输入SHA一致。56张原生图已逐张通过12张原像素拼图复查，额外4ChartFrame字节不变。

官网/本地六例×宽窄×浅深各24张，最终48图已复查；首批深色误标被发现并替换，最终24本地图每张按实际背景像素核对。手机浅深真实Right/Enter后Styled?正文可见。桌面API尝试受缩放后同fragment未重新定位影响，截图落在Vertical，未算通过。另确认手动主题在App.kt仅remember、整页重载丢失，保留共享缺口。原图、SHA、几何、排除的诊断及限制见[本轮说明](web-parity/accordion-r7/README.md)。

仍留RTL外置工具栏/精确Select、Horizontal同列动态重排、其余扩展API/Style/辅助技术/触摸矩阵、Card正文换行与深色字形、列宽/scrollbar和代码面板1px统计。Multiple全展开478px/固定450px的溢出已在官网实测，本地三项同时展开浏览器状态仍待补。Accordion保持未勾选；旧golden未验/未刷新，全仓提交门禁未运行、未新增commit。Editor继续排除，Attachment后置。

### Chart 五步骤预览尺寸（2026-11-02）

<!-- progress-created: 2026-11-02; order: 114 -->

官网实测五个步骤的preview均320px高，desktop内容缩进48px、手机0。桌面SVG509×286，虽然扣除40px上下padding只剩240px，仍按自然比例居中伸入padding区；本地此前全宽且随内容长高。新增目录`previewHeight`/`desktopPreviewInsetStart`元数据，仅五个Chart步骤使用320/48，并在固定预览内允许自然高度居中。标题留在主列，预览卡片及源码一起缩进，其他例继续原最小高度策略。

新8项Frame测试及既有ChartExamples32、DocCodeBlock4、ComponentDocumentation7合计51项本轮独立通过，shared ktlint通过；4张新原生图已查看，原44配方图字节未变。Wasm开发distribution22.520s通过（1项webpack warning），8输入SHA一致且脚本生成幂等。新Wasm实际加载后，五步骤四象限20图及实际展开Legend源码画面均已复查；桌面本地图表510×286、手机260×200，剩余宽差来自全局列宽/scrollbar。见[本轮记录](web-parity/chart-preview-r3/README.md)。

仍保留Add Grid/Axis/Tooltip/Legend的h2→h3层级、完整编号Steps和正文/ChartConfig等章节、主图标题/header与刻度及完整交互差异；Chart继续未勾选。上一轮64项核心/目录证据不与本轮51重复累计，未刷新旧golden、未运行全仓提交门禁或新增commit。清单已无明确尚未开始本轮页审的组件；下一轮回到清单前部，优先复核Accordion尚存预览高度策略与扩展API矩阵。Editor继续排除，Attachment剩余差异继续后置。

### Chart 响应轴补测与修正（2026-11-02）

<!-- progress-created: 2026-11-02; order: 113 -->

追查前轮阿语末两月重叠，确认首次窄屏和单独主题切换正常，但1440→390并切到RTL可复现且稳定帧仍错。JVM新增两项真实RTL间距/glyph与两项缩放后对照全新布局的像素测试，原实现8项轴测试均过，不能声称JVM RED。轴改为测量实际Text children，用同一placeable宽度筛选与放置，移除独立记忆宽度；浏览器新Wasm实际加载后原重现路径已消失，三语×浅深×宽窄12图及主图4图均已复查。[证据与边界](web-parity/chart-axis-r2/README.md)。

最终32核心+32目录=64项独立通过、两ktlint通过，44原生图与final60字节一致。Wasm开发distribution32.657s通过并保留1项webpack warning，18输入SHA一致，工作流已正常结束和清理。官网键盘此前读取太早；保留SVG focus并逐键等待可见Tooltip状态，已确认Apr2/Page Views/180。实际窄屏日期刻度仍是本地Apr1/8/15/22/30对官网Apr7/14/22/30，图宽/字体度量及标题/Steps/文档章节等差异继续开放。未验/刷新旧golden、未运行全仓门禁、未新增commit，Chart保持未勾选。

### Chart 最终60项与四象限浏览器记录（2026-11-02）

<!-- progress-created: 2026-11-02; order: 112 -->

数字裁切补三项固定宽度回归，修复前全部失败；Tooltip数值先测量并保持单行，名称列允许收缩，修复后包含真实RTL全部通过。Default与Tooltip解剖按MDX隐藏代码面板，其他示例默认行为保留。最终28核心+32目录合计60项独立通过、两个ktlint通过；JS编译12.912s、Wasm开发distribution27.840s通过并保留1项webpack warning，不能扩大为最终production验收。18输入SHA再次一致，生成脚本重跑幂等；44原生图已全部复查。[最终证据](web-parity/chart-r1/verification/summary.json)。

已使用获授权的空间4，官网与本地八示例各32张图（1440/390×浅深）全部保存并目视复查。新开发dist显示0.3.29，旧0.3.17产物明确排除。Mobile切换后的悬停值官网/本地均为Apr1/150，本地真实Tab/Home/Right显示Apr2/180；官网focus/arrow尝试未可靠观察到数字变化，保留键盘对照待办。390为桌面Chromium窄视口，不等同真实手机触摸。原图、拼图、局部裁切位置和矩阵见[浏览器记录](web-parity/chart-r1/browser/review.json)。

仍未对齐：桌面header约108px对官网99px；手机标题15px对继承约18.9px；官网Steps缩进48及固定320px预览、本地全宽随内容高度；页面Chart Config/Theming等章节和层级缺失；窄屏阿语末两个月份拥挤；Tooltip解剖色板/名称换行、深色文字字重与刻度对比、精确柱宽/阴影、完整动效与辅助技术/触摸状态。具体清单见[Chart说明](web-parity/chart-r1/README.md)。Chart继续未勾选；旧golden未验/未刷新，全仓门禁未运行、未新增commit，Attachment继续后置。

### Chart 本页：八配方与浏览器恢复（2026-11-02，进行中）

<!-- progress-created: 2026-11-02; order: 111 -->

[固定上游11来源](web-parity/chart-r1/sources.json)覆盖Default、First Chart/Grid/Axis/Tooltip/Legend、Tooltip解剖与RTL八组。已移植六月、原30天数据与7324/7250汇总、真正切换系列、三语翻译与反向轴，代码从编译区域生成。组件补Bar/Line指针命中、键盘选点与浮动提示，defaultIndex仅初始化、initialDimension不再冻结测量尺寸；新增series主题色/图标、正确hideIcon fallback、tooltip独立格式化数值和空数据隐藏/formatter/indicator。

原presentation RED4全失败，改后core17+目录32=49通过；恢复审计确认中断前最终语言切换修正已成功执行但未归档，恢复run复用缓存并逐XML确认，不算49项新执行。用户已明确允许新建专用浏览器验收空间，现canonical空间4、官网p1、本地p2，见[session记录](web-parity/browser-session.json)。原空间18的历史丢失记录保留，不再作为当前授权障碍。

官网真实四象限截图与DOM几何已保存，确认首柱222在220px绘图区为81.4px，对应0..600域；旧Canvas用最大值454撑满错误。按[Recharts v3.3主源码与MIT许可](web-parity/chart-r1/recharts-scale-sources.json)移植five-tick域（Double替代Decimal），并修24dp卡片内容gap。明确官网Card本身border0，外框来自preview，去除重复边框。该检查点core21+目录32=53及两ktlint通过，44原生图中40变化待复查。随后发现日期轴被窄band截成Ap/A，已改实际文字测量与preserveEnd/minTickGap、统一plotPadding；最终57项正在验证。

目前本地预览发现旧dist仍是0.3.17，未用它验收当前源码；webpack成功不等于dist同步，恢复owner正准备最终开发distribution供浏览器。Chart保持未勾选。剩余完整交互/动效、继承手机标题字号、刻度与formatter边界、多层阴影、rich config/payload别名及最终浏览器对照均需继续；旧黄金未验/未刷新，全仓门禁与新增commit未执行。

### Tooltip：五配方、计时与箭头布局（2026-10-31，进行中）

<!-- progress-created: 2026-10-31; order: 110 -->

按官网Base UI页面与固定上游96da9cc对照，[八份源码与SHA](web-parity/tooltip-r1/sources.json)已校验。五组配方Default、Side、With Keyboard Shortcut、Disabled Button、RTL已编译接入，复制代码从实际预览区域生成。Hover改Outline；四方向按Left/Top/Bottom/Right顺序；Save图标+S键帽、禁用按钮包装层、AR/EN/HE真实语言切换与六方向均已接入。物理Start/End沿用共享PopupPlacement语义，logical sides在配方显式映射。旧例标记Kotlin extension，修正默认skip400和hoverable说明。

原RED5有4失败/1通过：挂载通知、Escape不关闭、受控关闭不通知、11sp字体失败。原键盘焦点测试被初始化skip窗口掩盖，最终将skip设0强化后验证。已修首次hover延迟、focus立即打开、Escape单次关闭请求并保持焦点、当前hover抑制至离开重入；Provider只在真正打开/关闭后更新skip信息，不再挂载即写关闭时间。Nova chip改12/16 Normal、12/6内边距及320最大宽；箭头改overlay，Top侧anchor到chip实测4dp，不再因箭头额外占10dp。最终30项测试通过（Behavior8、Chrome2、目录5方法×1440/390×浅深=20），core/shared ktlint通过。32张原生图已通过4张仅裁空白、不缩放的拼图全部目视；组内互斥修复前后32张逐字节一致。原图与裁切元数据、XML和来源SHA均归档。详细范围与结果见 [tooltip-r1](web-parity/tooltip-r1/README.md)。

明确剩余：click-to-close、hover打开但键盘焦点在trigger外时的全局Escape、safe-polygon hover转移/closeDelay、嵌套/替换Provider边界、多trigger/动态移除、碰撞后箭头最终方向/边缘对齐、准确zoom/slide动效、自动Kbd选择器样式与原生无障碍关联及完整扩展矩阵。原浏览器空间18重查仍不存在，依ego-browser指导不得擅自新建替代空间，浅深×桌面/手机浏览器验收未完成；保留未勾选，原生模拟不能替代浏览器验收。


Tooltip目录验证还复现了390宽度语言切换后hover与focus同时打开两个同组提示。未弱化为first匹配；核对Base UI FloatingDelayGroup主源码（独立master来源与SHA已归档），新增hover→focus及focus→hover两项回归，修复前均失败。Provider现在在新owner打开时调用旧owner关闭，保留原焦点并抑制旧交互重开；原混合输入语言切换测试继续保留且全部通过。32张图展示真实键盘focus ring；390约束的RTL popup仍可能离开预览框，因为原生实际窗口1024宽，不能据此声称手机collision已验收。


Tooltip最终结果与输入SHA归档于[verification](web-parity/tooltip-r1/verification/summary.json)：30定向测试及两模块ktlint通过，JS39.482s退出0仍有os/path诊断，Wasm19.050s退出0有1项webpack warning；最终9输入哈希一致，32图与已目视版本完全一致。工作流结束并清理日志。动态trigger移除与content hover卸载清理仍需专项状态验证；旧黄金未验证/未刷新，全仓门禁未运行，本轮未新增commit。浏览器空间仍不可用，Tooltip保持未勾选。清单已无“尚未开始本轮页面与完整状态对照”的具体组件标记；下一轮优先处理尚缺本页完整对照的Chart，再回到清单前部关闭已记录差异。Attachment继续遵守用户要求留待统一处理。

### Toast：Base UI 对照与管理器补齐（2026-10-31，进行中）

<!-- progress-created: 2026-10-31; order: 109 -->

本轮纠正对照目标：官网当前 Toast 页面使用 Base UI Toast，此前本地的 Sonner 描述不能作为完成依据。固定上游 `96da9cc7f1aa10580a3741664e2a77c721206c00` 的页面、组件、Default/Types/Promise 和 Nova 样式共六份源码已保存并校验 SHA256，见 [来源](web-parity/toast-r1/sources.json)。

已实现默认5000ms、0常驻、默认关闭控件、action由回调显式决定关闭；同ID更新重启超时，超出显示数量的消息仍计时，替换manager不复用旧ID计时。补caller-owned suspend promise/loading，成功/失败更新原消息，取消清理并继续传播，已关闭消息不因完成而重建。卡片按popover/foreground、384dp宽度上限、16dp圆角、16dp内边距、14/20排版调整；状态点换为有许可证及来源归档的Lucide图标，Error使用destructive，Loading使用Spinner。目录官方三组配方接窗口级host，窗口边距16dp；代码从编译配方生成。旧位置/主题/取消等扩展保留，不等同官网完整能力。

[验证结果](web-parity/toast-r1/verification/final/test-summary.json)：43项独立测试通过（计时/操作8、异步管理器5、浅深卡片6、模拟1440/390×浅深配方24），core/shared ktlint通过。最初五项行为测试在原实现上4失败/1通过，首轮修改后5通过。40张原生图已逐张通过8张原像素拼图目视检查，覆盖Default、五Types、Promise加载/成功、更新后的action及完整host位置；这些是原生桌面中的模拟宽度，不是浏览器/真实手机验收。六份上游和五个Lucide源文件哈希通过，生成片段二次运行幂等。旧扩展示例统一标注Kotlin extension，修正默认5000ms、显示3项、关闭按钮开启、gap12和Close toast说明，最后文档调整后24项配方与shared ktlint复验通过，40张图与已目视版本逐字节一致。旧Toast黄金图本轮未验证或刷新。详见 [本轮说明](web-parity/toast-r1/README.md)。

明确剩余：源码折叠堆叠、peek/scale和可变高度展开，swipe，hover/focus/window失焦暂停，关闭保留与500/150/250ms动效，F6/Escape及焦点恢复、portal层级与RTL、准确CSS多层阴影和完整扩展矩阵。当前host仍是竖向Column，立即dismiss不能声称已有完整退出生命周期。原浏览器TaskSpace18丢失且替代空间未获授权，浅深×桌面/手机浏览器验收仍未完成；未勾选Toast。下一项优先Tooltip。


Toast本轮最终构建记录：JS 24.801s退出0但仍报既有os/path模块诊断，Wasm 18.861s退出0且有1项webpack warning。Web编译对应runtime与官方三配方检查点；晚期仅旧扩展ToastDoc默认值/标题修正，最终字节经JVM配方及shared ktlint复验，不能扩大为最终Web文档验收。[最终摘要与输入哈希](web-parity/toast-r1/verification/summary.json)和[compact ledger](web-parity/toast-r1/verification/gradle-ledger.json)已归档，工作流清理，无基准更新。全仓提交门禁未运行，本轮未新增commit；Toast保持未勾选，继续优先Tooltip。

### Tabs 六配方、键盘与隐藏面板复核（2026-10-31）

<!-- progress-created: 2026-10-31; order: 108 -->

归档9份固定Nova来源并参照Base UI行为文档。Root默认改Manual（方向键只移动焦点，Enter/Space激活），补单Tab入口、RTL方向反转、disabled跳过、Home/End、非循环边界与重排顺序；稳定setter读取最新回调并尊重受控拒绝。有效默认值等待注册就绪，不再初始闪空。默认面板即时切换，显式动画扩展保留；keepMounted保留输入但不占间距、不参与隐藏焦点或辅助技术。

默认滑动装饰改为trigger自身选中chrome；32dp横轨、14/20 medium、default gap0/Line gap4、root gap8、等分背景/垂直等宽和图标6dp间距已修正。Line下划线改源码外侧位置。原生focus阴影曾把透明Line内部染灰，浅深像素RED复现后使用已有cssStyleable外侧绘制修复。六组官网配方含四个真实Card面板及三语内容，预览和复制代码同源生成；旧Default activation扩展的代码/预览不再误写显式Automatic。

最终core22与app36合计58项独立验证及两个ktlint通过，分别来自未改runtime的实际54项运行和最后app36新运行，不声称单次58全量。44图均已检查；焦点修正影响的8图已复查，其余36字节相同，最终13份源码/配置/测试及44图hash复验一致。旧7张基准变化，精确原始源码隔离4过3变，确认旧Vega3项原已失配，未刷新任何基准；原始源码的一帧面板测试明确RED，当前GREEN。

最后JS退出0仍有os/path错误，Wasm退出0带warning。来源、同步说明、尺寸/截图边界与后续项见 [tabs-r1](web-parity/tabs-r1/README.md)。150ms颜色/下划线透明度过渡、ARIA/面板Tab停点、动态焦点移除和完整自定义/RTL矩阵仍待完成；共享语言栏Hebrew文字省略已记为原生观察。原浏览器TaskSpace18仍丢失，未新建替代空间，Tabs保持未勾选。下一项优先Toast；全仓提交门禁未运行，未新增commit。

### Table 第一轮结构、样式与配方（2026-10-31）

<!-- progress-created: 2026-10-31; order: 107 -->

固定上游7份来源并核对明确的base-nova目标。修正Caption始终底置居中与16dp间距、Header40dp、body14/20和四边8dp、透明行/hover/selected语义与选中hover优先级、Footer独立半透明底色和顶线。新增具名content槽并保持原有尾随String lambda；测试曾抓到重载吞掉字符串，已调整签名修复。横向viewport内置，ApiReference调用方移除额外嵌套滚动。

Default/三行Footer/真实Actions菜单/三语RTL的官方数据与交互已加入，预览与复制代码同源生成。但列宽仍是显式100dp首末列与中间权重，不等同HTML跨行自动列宽，footer也不是通用colSpan，此缺口保持开放。原有12组Kotlin扩展示例和多行thead、checkbox/复杂槽、expanded行色、过渡及完整辅助技术仍待完成；希伯来原生高度381px、英阿372px的字体度量差异已记录。

原始10项全失败，最终core16/app28合计44项与core/shared ktlint通过；强化RTL位置断言后的app28重新运行通过，恢复检查core16使用已验证缓存，非第二次独立44全量运行。24张浅深/1440与390原生图已检查，源码与图片hash复验一致。旧2张截图均变化；精确原始源码隔离为浅色通过/深色仅两条976px分隔线既有RGB38→34失配，新增总高度16px来自本轮。未刷新基准。JS退出0仍有os/path错误，Wasm退出0带warning。

证据、测试范围和计划见 [table-r1](web-parity/table-r1/README.md)。浏览器原TaskSpace18仍不存在，未自行新建恢复空间。Table保持未勾选；按尚未开始优先进入Tabs，后续再统一关闭已记录差异。全仓提交门禁仍未建立，本轮未新增commit。

### Skeleton 七配方与脉冲绘制复核（2026-10-31）

<!-- progress-created: 2026-10-31; order: 106 -->

Default补48dp圆头像与两行16dp骨架；Avatar改为40dp圆头像和两行短骨架，Card/Text/Form/Table/三语RTL按固定Nova来源迁移。七组官网配方及Shimmer项目扩展的预览/复制代码由同一份编译源码生成；Shimmer原先复制一行但预览三行的差异已消除。

脉冲原实现正确完成两秒1→0.5→1循环，但在测试帧内使调用方重组49次；现保留State并由Style观察，动画不再逐帧重组调用方。原始8项中6过2失败，最终10项核心和40项目录状态合计50项、core/shared ktlint通过。浅深旧截图2项验证通过且未刷新基准。40张原生浅深/1440和390视口夹具已检查，11份源码/测试/生成器/配置及40图hash复验一致。JS退出0仍有os/path错误，Wasm退出0带warning，不声称干净Web构建。

来源、样式策略、截图尺寸与脉冲相位限制见 [skeleton-r1/README.md](web-parity/skeleton-r1/README.md)。原生截图不替代浏览器四象限验收，原TaskSpace18仍丢失，保持未勾选。下一项优先Table；未运行或绕过全仓提交门禁，未新增commit。

### Skeleton 来源与缺口定位（2026-10-31）

<!-- progress-created: 2026-10-31; order: 105 -->

已核对官网页面，归档Base组件、Nova CSS、MDX及七组配方共10份来源并验证hash。默认chrome源码相符；本地Default缺头像且行高不符，Avatar预览是方块且缺文字骨架，其余五组配方缺失。后续迁移全部七配方、验证脉冲/裁剪/尺寸及复制代码一致性；此处仅为来源审计，未声称实现或浏览器通过。见 [skeleton-r1](web-parity/skeleton-r1/README.md)。

### Sidebar 第二轮内容区与分区布局（2026-10-31）

<!-- progress-created: 2026-10-31; order: 104 -->

修正全局padding/gap错放、SidebarContent没有滚动及Footer未固定在底部的问题。Header/Footer为8dp padding与gap，Group为8dp padding，内容无额外gap；Icon收缩时隐藏溢出。文档Header/Footer预览和复制代码均使用SidebarContent。新增8项RED→GREEN，包括真实鼠标滚轮；与旧26项合计34项、core/shared ktlint、shared JVM编译通过。8张布局截图已检查且最终版本字节相同。JS退出0仍有os/path错误，Wasm退出0带warning。来源、尺寸限制、未关闭项及证据见 [sidebar-r2](web-parity/sidebar-r2/README.md)。保持未勾选，接下来优先开始Skeleton来源及配方复核。

### Sidebar 第一轮状态与移动面板复核（2026-10-31）

<!-- progress-created: 2026-10-31; order: 103 -->

固定上游19份来源、修正与证据见 [sidebar-r1/README.md](web-parity/sidebar-r1/README.md)。26项定向测试和core/shared ktlint通过；JS退出0仍有os/path错误，Wasm退出0带一项warning。旧17项截图1过16变，精确前后源码隔离确认十项仅本轮内侧border变化、五项完全相同、两项手机面板背景/间距变化；其余大面积色彩及scrim差异本轮前已存在，未更新旧基准。原生8张为几何夹具，不代表页面视觉验收。SidebarContent已有公开函数但尚无滚动，接下来修正内容区及Header/Footer布局。浏览器TaskSpace18仍丢失，未新建恢复空间；完整矩阵保持未完成。

### Shimmer 第二轮：currentColor、官网配色与九组配方（2026-10-31）

<!-- progress-created: 2026-10-31; order: 102 -->

继续使用[11份固定上游来源](web-parity/shimmer-r1/sources.json)，另核对W3C颜色及渐变插值规则，引用见[本轮说明](web-parity/shimmer-r2/README.md)。四项RED像素测试确认旧Style默认底色不跟随红字、浅色黑字峰值约0.478而非20% alpha、暗色峰值约0.455未按OKLCH提亮、半透明蓝色高光被合成为不透明紫色；修复后四项均通过。最初暗色测试错误地取了包含背景的最小通道，已先改为亮字形最大值再建立有效RED，未把无效oracle算作依据。

Text新增内部currentColor画刷协议，在绘制边界解析显式TextStyle、LocalContentColor、祖先Style颜色和Style自带颜色；显式baseColor参数仍可覆盖。保留Foundation清空contentBrush前的同节点颜色，支持Style.shimmer及Text自身原始then链；更近的显式TextStyle仍优先。祖先/自身Style颜色在冻结动画时钟下变化也能重绘，调用方组合计数不增加。

Style默认浅色高光保持当前RGB并将alpha乘0.2；暗色按max(0.8,L+0.4)及alpha+0.4提亮并截到1，L端点映射黑/白。显式高光保留alpha，中间停点按预乘OKLCH混合、最短色相路径与灰色借色相处理。Oklab渐变由257个sRGB样本近似，最多缓存四种底色，避免动画逐帧重算颜色。此近似没有证明全色域浏览器误差界；色域映射/P3、极端饱和色、量化与近无彩色阈值仍开放。旧Modifier白色SrcAtop叠加作为扩展保留，未改Attachment，也不再把该叠加称为CSS文字填充等价实现。

官网Default、With Marker、Color、Duration、Spread、Angle、Play Once/Replay、Disabling the Shimmer、RTL九组配方已放到目录最前；旧Modifier示例改名Overlay (Kotlin)，主Usage使用Style。文字14/20、标签12/16 Mono；比较组最大512dp、24dp gap、640dp起两列，渐变使用整个段落宽度而非仅字形宽度。加入blue500/60与#378ADD、16/96dp spread、1100ms once及keyed Replay、768dp起关闭动画、英文/阿拉伯语RTL与两个Marker状态。复制代码从编译预览源码和共享函数生成。

[最终验证](web-parity/shimmer-r2/verification/summary.json)：核心38（CSS颜色4、数值8、currentColor8、既有绘制/阶段12、TextColor1、AttachmentState5）＋目录40，共78项独立通过，core/shared两个ktlint通过。目录测试独立比较Marker每段文字像素（不借Spinner动画冒充扫光）、Replay结束后重启、桌面None静态/窄屏动态、颜色/速度/宽度/角度差异、两列/竖排及RTL文案。4张核心图与40张目录原生图均已查看，[4张拼接检查图](web-parity/shimmer-r2/review-sheets/layout.json)无缩放且原图40张已核实全不透明。1440/390是JVM窗口信息覆盖，不能替代浏览器或手机系统验收。

旧Shimmer/Text六张verify仍为4PASS、2CHANGED；两张actual与上一轮精确修改前隔离结果逐字节相同，本轮没有额外旧图变化，未录制基准。JS35.755s退出0仍有既存os/path诊断；Wasm18.911s退出0并一项webpack警告。20份源码/配置/生成器/测试、44张原生图哈希已重验，工作流结束并清理完整临时日志。

Shimmer保持未勾选。自动减弱动态、实际字体3ch+40px及百分比spread、浏览器四象限、色域/采样精度与完整时间/角度/RTL/字体/富文本矩阵仍待完成；直接在外部Foundation祖先节点设置同节点颜色和画刷而未用Style.shimmer包装仍受其清色规则限制，不能由Text自身then链测试外推为已支持。完整剩余范围已写入[本轮说明](web-parity/shimmer-r2/README.md)。浏览器空间18恢复授权仍未回复，未创建替代空间。版本0.3.28，Editor排除、Attachment遗留暂缓；下一项优先尚未开始本轮对照的Sidebar。

### Shimmer 第一轮：修复 Style 扫光与绘制阶段读值（2026-10-31，进行中）

<!-- progress-created: 2026-10-31; order: 101 -->

已归档固定上游96da9cc的MDX、九组Base配方及shadcn/tailwind.css，共[11份官方来源](web-parity/shimmer-r1/sources.json)。官网文本抓取工具访问live页面失败，改用官方仓库MDX/CSS作来源；不将其视为浏览器现场验收。来源表明确官网currentColor与OKLCH高光、字体3ch+40px spread、once/reverse、自动减弱动态及九组配方，本轮先处理已记录的Style路径根因。

五项RED像素测试全部复现失败：Style扫光不改变像素、显式文字颜色遮住画刷、富文本与祖先画刷未绘制、半透明效果不正确。检查Compose1.12.2源码确认同节点contentColor会清空contentBrush；本地Text把显式TextStyle纯色追加在同一Style节点上，还缺少实际画刷绘制桥。现将显式纯色作为更近的独立节点，保持原纯色优先级，同时由原生绘制节点读取最近画刷，在隔离字形层以SrcIn绘制，避免污染空白/背景/兄弟节点；画刷存在时原纯色仅作不透明字形遮罩，避免把其alpha再乘一次。普通文本仍走直接绘制路径。五项测试随后独立通过。

另加动画阶段测试发现Style调用方组合次数在采样间隔从4增至64；Modifier调用方原本已稳定，未把它误记为同类回归。动画辅助改返回State，Modifier在draw读值，remember后的Style在自身绘制观察作用域读值，每个绘制帧新建画刷避免ShaderBrush按尺寸缓存旧着色器。Style和Modifier均以真实像素变化加调用方组合次数不增加验证。补测浅/深、once结束后稳定、禁用后恢复红色并停止动画、SelectionContainer画刷、显式TextStyle纯色仍优先于普通Style纯色与祖先颜色动态更新；Attachment五项既有状态回归也通过。文档标题/描述对照官网，并修正复制文本和KDoc里不存在的TextStyles.Muted为实际TextColors.Muted。

定向验证共18项（Shimmer新12＋TextColor旧1＋AttachmentState旧5）及core/shared两个ktlint通过，6张原生文字图已逐张查看。旧Shimmer/Text共6张verify：4PASS、2CHANGED，没有录制基准。为归因，临时安装本轮修改前的精确Text/Shimmer源码后仅verify两项变化；两张actual均与本轮当前actual逐字节相同，证明本轮前已存在，未归因于本次画刷/状态修复。Shimmer浅色644个字形像素差异、Text继承暗色背景/边框差异的历史来源仍未分配。已精确恢复当前green源码，仅改KDoc用词；恢复后的18项检查复用了相同编译产物的已通过结果，不声称一次新的独立执行。

Shimmer保持未勾选；[完整计划与真实缺口](web-parity/shimmer-r1/README.md)列明仍待修复的currentColor/OKLCH及alpha混合、字体spread与任意百分比、自动减弱动态、九组官网示例和Replay/响应式None、RTL方向与完整字形/密度/字体矩阵。现有白色叠加高光不是官网颜色等价实现，未因此宣称页面已对齐。浏览器原空间18恢复授权仍未回复，未创建替代空间；Editor排除、Attachment遗留暂缓。版本0.3.27，没有更新截图基准或运行全仓提交门禁。

[本轮最终证据](web-parity/shimmer-r1/verification/summary.json)：JS45.731s退出0，仍有既存os/path诊断；Wasm24.917s退出0并一项webpack警告，未称为干净构建。11份源码/配置/测试与6张已查看原生图哈希匹配，RED/GREEN、重组计数及旧基准隔离证据已归档。工作流结束，完整临时日志已清理。后续先补Shimmer官网颜色/减少动态与完整配方，再按清单进入尚未开始的Sidebar。

### Sheet 第二轮：面板、Footer权重与四组官网配方（2026-10-31）

<!-- progress-created: 2026-10-31; order: 100 -->

补齐Nova面板popover底色、仅朝内一侧1dp边框、shadow-lg双阴影并移除根内边距；Header改16dp padding/2dp gap，标题16/24 medium、描述14/20 muted并保留完整行盒。Footer改ColumnScope、16dp padding、8dp gap，操作按钮默认拉伸；自动顶边距只消费正文权重分配后剩下的空间，不再用额外weight Spacer抢正文高度。新内部SheetColumn保留weight/fill和align/alignBy父数据，支持竖向面板内容自适应；完整intrinsic/动态布局矩阵仍开放。

关闭按钮改为Ghost IconSm28dp、top/end12dp。最初仅看Nova原始right-3会把RTL按钮留在右侧，原生图发现它与右对齐标题重叠；进一步归档固定上游RTL转换器与registry builder，确认构建先展开Nova样式，再执行right-*→end-*。因此已按官网生成逻辑修正为RTL左上角，并更新定位断言。面板Left/Right仍是物理方向，与逻辑Start/End扩展分开。

官网Default、Side、No Close Button、RTL四组配方已置于目录最前，旧Kotlin示例保留并改重复标题。默认表单精确文案、Pedro Duarte/@peduarte、标签点击聚焦与输入、Save不自动关闭、Close关闭均已接入；关闭卸载后重新打开恢复初始值。Side加入十段完整正文、14/22.75行高、每段8dp底边距、四方向、top/bottom 50%视口高度及滚动，Save保留面板而Cancel关闭。RTL沿用FieldGroup/Field结构及三语文本，Username为peduarte（无@），Arabic/Hebrew物理Left、English物理Right，关闭图标的默认辅助名称保留官网Close。复制代码由编译中的配方和共享辅助函数生成，避免预览与文本分离。

[验证证据](web-parity/sheet-r2/verification/summary.json)：核心15旧行为＋12新布局/像素＋目录28，共55项独立通过；core/shared两个ktlint通过。测试覆盖Header字号行盒/间距、Footer纵向拉伸/贴底、正文weight不被Footer平分、fill=false、四边框像素、Top/Bottom自动高度、RTL关闭按钮位置、四方向长正文滚到底部后关闭、输入/标签焦点/重开重置与Save不关闭。最初Side测试在冻结时钟下调用循环performScrollTo，测试进程83.636s后失败；XML只含6项完成记录，未把它算通过。检查Compose1.12.2源码后改成单次ScrollBy加显式推进时钟，独立Side四模式以及完整28项随后通过。该测试夹具诊断不推断产品滚动故障或进程退出具体原因。

已查看4张核心图和[12张原始像素拼接检查图](web-parity/sheet-r2/review-sheets/layout.json)，覆盖36张目录图；全部是JVM原生证据。390/1440为覆盖WindowInfo，真实Popup场景仍为1024×768；Top/Bottom原生图宽度仍是1024，不能声称手机浏览器布局已验收。JS38.178s退出0仍有既存os/path解析诊断；Wasm19.397s退出0并保留一项webpack警告。13份源码/配置/生成器与40张原生图哈希已重验；工作流已结束并删除完整临时日志。

Sheet仍未勾选。浏览器桌面/手机浅深、触摸与软键盘初始焦点、无触发器恢复、嵌套/非模态/中断动画、完整密度/字体/Style/RTL样式矩阵、动态权重及alignBy/intrinsic、辅助技术与语言工具栏交互仍开放；没有重录或验证旧截图基准、没有创建新截图基准，也未运行全仓提交门禁。[剩余差异与验证范围](web-parity/sheet-r2/README.md)已记录。浏览器原空间18恢复申请仍未回复，未创建替代空间。版本0.3.26，Editor排除、Attachment遗留暂缓，接下来按优先级做尚未开始的Shimmer，包含先前记录的Style路径无像素动画问题。

### Sheet 第一轮：尺寸、方向、动画与焦点基础（2026-10-31，进行中）

<!-- progress-created: 2026-10-31; order: 99 -->

已重新读取[官网 Sheet 页面](https://ui.shadcn.com/docs/components/base/sheet)，归档7份固定上游96da9cc的MDX、组件、Nova样式和四组配方；本轮重验全部来源哈希。发现本地横向面板固定320dp、进入/退出整块位移且500ms，Start/End动画方向没有随RTL翻转；默认逻辑End也不等于官网物理right。Header/Footer、面板根padding与border、标题/描述字号、关闭按钮和示例内容另有差异，完整待办见[Sheet逐项计划](web-parity/sheet-r1/README.md)。

本轮先修复基础行为：横向默认75%视口宽度，640dp起上限384dp；尺寸转为可覆盖的Style默认值，已有Sidebar的336dp调用方宽度可生效。新增物理Left/Right并以Right为默认，保留逻辑Start/End扩展并按布局方向解析。SheetContent以密度换算40dp位移，面板200ms ease-in-out淡入淡出；遮罩调整为黑色10%与150ms，复用Nova已有OverlayHost模糊。根层接入共享焦点所有者；字符串触发器改为直接Button，避免外层clickable叠加，并在关闭完成后恢复键盘触发器焦点。

原生15项定向测试及core ktlint已通过：390/639/640/1440宽度与断点、336dp显式覆盖、RTL物理Right及逻辑End、受控关闭拒绝、关闭按钮/Escape/无关闭按钮时空白关闭、外部关闭拦截、首个按钮与触发器焦点、动画中途位移和像素透明度及200ms后移除。4张浅深面板图已逐张查看；它们只是1024×768 JVM Popup场景中覆盖WindowInfo后的面板宽度证据，不是手机浏览器截图，也没有作为视觉一致性基准录入。Header/Footer及面板chrome仍明显保留旧实现。

Sheet保持未勾选；下一步继续Header/Footer布局（包括body权重与Footer自动顶边距）、面板单侧border与shadow、28dp关闭按钮、官网Default/Side/NoClose/三语RTL配方，然后补完整状态矩阵。浏览器原空间18丢失的恢复申请仍未回复，未创建替代空间。Editor不做，Attachment遗留暂缓。版本0.3.25；本轮没有重录旧Sheet截图，也没有绕过现有提交hook。

[本轮验证证据](web-parity/sheet-r1/verification/summary.json)：目录shared JVM调用方编译通过；JS27.077s退出0但仍有既存os/path解析诊断，Wasm18.453s退出0且一项webpack警告，不称干净构建。6份源码/配置及4张已查看原生图哈希一致。定向Gradle工作流已结束、完整临时日志已删除；没有运行全仓提交门禁或录制旧截图。

### Separator 第一轮：官网五配方与语义默认（2026-10-31）

<!-- progress-created: 2026-10-31; order: 98 -->

[计划](web-parity/separator-r1/plan.md)、[9份固定来源](web-parity/separator-r1/sources.json)、[矩阵](web-parity/separator-r1/parity-matrix.json)已归档。固定shadcn96da9cc/Base UI1.6.0，五组Default、Vertical、Menu、List、RTL均为base-nova。默认1dp线、border颜色、无内建margin及调用方Style覆盖原实现正确，未改绘制尺寸。

Base UI固定role=separator及aria-orientation，默认有语义；本地原先沿用Radix的decorative=true，现改false，native提供Separator描述与Horizontal/Vertical状态，并验证调用方可用semantics覆盖本地化描述。Compose1.12.2无Role.Separator，以上是平台fallback，不作为HTML角色完全等价证明。原decorative=true装饰线扩展继续可用；迁移时需要静默线的调用方显式true。

五配方已迁移，文本、三语翻译和复制代码从编译源码保持一致；旧复制字符串中的无效TextStyles.Muted/TextTypography名称已修正。Default384dp最大宽、14/20字号、14sp leading-none标题、6/16dp间距；Vertical20dp行；List三对Item/Value、两条线和8dp间距；Menu20+4+16=40dp行，低于768dp隐藏Help及第二竖线、间隔16→8dp。CSS竖线self-stretch对应的Compose父Row需有界高度，内容自适应时用IntrinsicSize.Min，示例和说明已补。

验证：核心8项涵盖默认/装饰/本地化语义、方向更新、显式尺寸/红色覆盖、竖向有界/内在行高及零隐含margin；目录20项覆盖五配方×1440/390×浅深。新Nova4图和28张原生目录图已查看，[四张无缩放联系表](web-parity/separator-r1/review-sheets/layout.json)保留全部状态。这是1024×768桌面场景里的mockWindowInfo，不能代替浏览器/手机验收。

旧Vega2图浅色通过、深色CHANGED，差异只有竖线20像素RGB38→34，[逐像素记录](web-parity/separator-r1/legacy-pixel-diff.json)可查。将Separator.kt替换为本轮前精确字节后，得到相同失败及完全一致actual哈希，证明此色差早于本轮（非纯HEAD隔离结论）；未刷新旧基线，已恢复当前源码。

[最终证据](web-parity/separator-r1/verification/final-summary.json)：核心8＋新Nova4＋目录20共32项独立通过，core/shared两个ktlint通过。JS27.607s、Wasm19.961s均退出0；JS仍有webpack os/path解析诊断，Wasm一条webpack警告，未称为干净构建。11份源码/配置/生成器、4张新基准、28张目录图哈希匹配，最终目录图与已查看版本逐字节一致；当前源码在精确恢复后仅修正一处KDoc措辞，运行时代码未改变。工作流已结束、原始日志已清理。累计修改未commit：此前全仓pre-commit门禁因自动审批额度拒绝，未绕过hook，本轮模块检查不能代替全仓门禁。

组件保持未勾选。浏览器桌面/手机浅深、真实辅助技术与role适配、dl/dt/dd语义、极端/无界父级、动态主题/字体/密度/Style及768边界、RTL Menu/List完整矩阵仍开放。浏览器原空间18丢失，既有恢复申请尚未回复，未创建替代空间。版本0.3.24；Editor排除，Attachment遗留暂缓，下一项优先Sheet。

### scroll-fade 第一轮：默认规则、完整配方与真实遮罩（2026-10-31）

<!-- progress-created: 2026-10-31; order: 97 -->

[计划](web-parity/scroll-fade-r1/plan.md)、[12份固定来源](web-parity/scroll-fade-r1/sources.json)、[差异矩阵](web-parity/scroll-fade-r1/parity-matrix.json)已保存。官网实际路径为/docs/utils/scroll-fade；七个配方为Default、No Overflow、Horizontal、Edge Fades、Fade Size、None、RTL。MDX前六指定Rhea、RTL指定Nova；这些普通Tailwind div的圆角仍使用site globals中的--radius公式，不能套用本地Rhea组件18/26dp人格半径，现默认按lg10/2xl18dp渲染。

核心默认从24dp瞬时mask改为min(容器12%,40dp)，随96dp滚动距离以CSS ease-in-out渐入/出。新增ScrollFadeEdge与ScrollFadeSize.Length/Fraction、非对称leading/trailing、revealDistance及enabled=false。固定尺寸/任意百分比不受默认40dp cap；不再把所有大小强制截到半个容器，而按CSS单一渐变的停点fixup绘制。RTL对逻辑渐变方向反转而保留停点顺序，避免大尺寸重叠时错误地选择相反的黑色停点；新增像素回归已先失败再通过，[规则与例子](web-parity/scroll-fade-r1/mask-oracle.json)明确区分源码/规范及原生证据与浏览器验收。

仍使用Offscreen+DstIn真正降低内容alpha，不叠固定底色；draw/cache阶段读取滚动状态，真实触摸测试确认调用方没有额外重组。红内容/蓝背景像素验证起点、中点、终点、半程reveal、无溢出/收缩、none、逻辑/物理边和RTL。Attachment原五参40dp/.12/96dp配置保持可用并有独立像素测试；未重新展开Attachment遗留。

七配方已移植：320dp最大宽、40dp行、6dp间隔/内边距、正确视口高度、精确条目与三语翻译；七段复制代码从编译源码生成。公开说明已强调scrollFade须放在verticalScroll/horizontalScroll之前，让mask覆盖视口而非长内容；旧复制代码中不存在的shadcnScrollFade方法名已移除。页面及侧栏标题改为官网scroll-fade。

关联修复：MessageScrollerViewport原来把mask放在verticalScroll内部，现改为视口层。旧两张Nova图仅Message4的文字alpha变化，浅色446/深色457像素、范围[25,220,95,232]，其他行、按钮与尺寸不变，[像素对照](web-parity/scroll-fade-r1/message-scroller-pixel-diff.json)可查。旧ScrollFade两张Vega测试同样mask顺序错误，且未等测量就读取maxValue；旧基线显示终点。现等待布局、断言实际中点后截图。上述四张旧图均在查看before/actual、确认源码意图后定向更新并独立verify；没有批量录旧基线。

验证结果：数学13、真实像素/触摸14、新Nova6、修正旧ScrollFade2、MessageScroller依赖29、目录28，共92项独立通过，core/shared两个ktlint通过。64张原生目录图与六张新图已查看，[16张无缩放联系表](web-parity/scroll-fade-r1/review-sheets/layout.json)保留全部状态。最终RTL修正后64张目录图哈希与已查看版本逐字节一致。原生fixture仅在1024×768桌面场景提供1440/390 mockWindowInfo，不能替代真实手机、浏览器或全页验收。

[最终验证证据](web-parity/scroll-fade-r1/verification/final-summary.json)：JS21.569s、Wasm18.178s均退出0；JS仍有webpack os/path解析诊断，Wasm一条webpack警告，未称为干净构建。最终页面说明已在编译JS中核对，14份源码、6张新Nova/4张定向修正旧基准、64张目录图哈希全部匹配，工作流已结束并清理原始日志。累计修改暂未commit：此前全仓pre-commit门禁遭自动审批因额度拒绝，未绕过hook；本轮所属模块检查不能代替全仓门禁。

组件仍未勾选：官网/本地浏览器桌面/手机浅深、物理x时间线在RTL下的精确语义、unsupported-browser静态fallback、自定义spacing/rem、动态主题/字体/密度/方向/配置与enabled切换、复合mask和完整读屏/键盘矩阵仍保留。Mono参数允许调用方注入字体，目录用GeistMono，复制函数默认平台Monospace。浏览器原空间18丢失，既有重建申请尚未回复，未另建替代空间。公共版本0.3.23；Editor排除、Attachment遗留暂缓，下一项优先尚未开始的Separator。

### Scroll Area 第一轮：官网配方、真实滚动与焦点（2026-10-31）

<!-- progress-created: 2026-10-31; order: 96 -->

按优先级进入尚未开始的 Scroll Area。[计划](web-parity/scroll-area-r1/plan.md)、[固定13份来源](web-parity/scroll-area-r1/sources.json)、[图片来源](web-parity/scroll-area-r1/artwork-sources.json)和[剩余矩阵](web-parity/scroll-area-r1/parity-matrix.json)已保存。官方Base Nova为Default、Horizontal、RTL三配方；锁文件实际解析Base UI1.6.0，已按该tag归档Root/Viewport/Scrollbar/Thumb/Corner和constants，不将本地Radix扩展策略作为Base UI默认行为。

三组配方已迁移：Default为192×288dp框、16dp内边距、50个原始版本号、14sp leading-none标题及20sp版本行；Horizontal为384dp宽框，三幅原图以300×400dp裁剪显示、16dp间隔及12/16sp作者说明；RTL阿拉伯语初始，英文/希伯来语切换保留滚动位置并移动纵向轨道。三张图片按官网原URL下载为本地资源，哈希已保存，复制横向示例需同时引入这三个drawable。复制代码从编译配方生成并校验幂等。

核心修复：Nova轨道10dp，1dp透明边框和padding对应7dp可见thumb，上游最小长度为16px。修正最小thumb下仍用原始内容比例定位导致越界的问题，改为按实际travel×当前滚动比例计算，超长内容在末端仍留在轨道内。非挂起指针节点实现thumb拖动及轨道按下居中/继续拖动，横向RTL按物理方向转换；双轴滚动条预留corner。轨道是Viewport的兄弟节点，需独立处理滚轮，现通过Compose原生scrollable和平台reverseDirection处理，不用猜测wheel单位。

Viewport在溢出时可聚焦，方向/Home/End/Page/Space与Shift+Space可滚动；只处理Viewport自身焦点，不抢子文本框的Home/End。截图复核发现普通styleable的阴影把透明Viewport内部染灰，已改用CSS外阴影绘制并补1dp outline。新增viewportStyle供调用方设置形状，官网配方与Root一致；任意Root shape自动继承和焦点环外缘被原生Root形状裁剪的问题仍保留，未声称完全一致。默认ScrollArea.type由Hover改为Auto（溢出时显示）；需要旧600ms行为的调用方显式传ScrollType.Hover()，LazyScrollArea扩展默认保持Hover。

验证：[最终证据](web-parity/scroll-area-r1/verification/final-summary.json)已归档。11项真实交互＋12项几何数学＋4项可见性策略＋4项Lazy metrics＋6项新Nova基准独立verify＋12项目录，共49项通过；core/shared两个ktlint通过。真实交互覆盖鼠标/触摸/Viewport与track滚轮、RTL、首尾端点、轨道按下、键盘、双轴及Lazy列表。首次Lazy底部点击测试因Compose click()默认把位置移回中心而失败，改为明确press/release，末端index>80阈值未降低。28张目录原生图与6张新图已查看，[八张无缩放联系表](web-parity/scroll-area-r1/review-sheets/layout.json)可复查。它们是桌面1024×768场景里的1440/390 mockWindowInfo，不能代替真实手机/浏览器。

旧4图：ScrollArea浅色通过；深色thumb540像素颜色变化（主体RGB38→34，含抗锯齿），几何/文字未改变；Lazy浅深两图缺少原始基线，分别保存actual和XML。[像素差异](web-parity/scroll-area-r1/legacy-pixel-diff.json)未作前版隔离归因，未重录任何旧基准。

最终构建：JS 34.428s、Wasm 19.890s均退出0；JS仍有webpack os/path解析诊断，Wasm一条webpack警告，不称为干净构建。11份编译源码、6张新基准、28张目录图哈希均匹配，工作流已结束、原始日志已清理。累计修改暂未commit：此前全仓pre-commit门禁被自动审批因额度拒绝，未绕过hook；所属模块检查不能代替该门禁。

组件保持未勾选。完整官网/本地浏览器浅深桌面/手机、自动Root形状继承与focus ring裁剪、OS高精度touchpad/ctrl-wheel/按键重复、辅助技术、动态内容/主题/样式/方向改变、嵌套边界及Lazy可变高度/padding/reverse layout全矩阵仍开放。原浏览器空间18丢失，既有重建申请尚未回复，未创建替代空间。公共版本0.3.22；Editor排除，Attachment遗留暂缓，下一项优先Scroll Fade。

### Resizable 第一轮：配方、真实拖动和键盘（2026-10-31）

<!-- progress-created: 2026-10-31; order: 95 -->

按优先级进入本轮尚未开始的 Resizable。[计划](web-parity/resizable-r1/plan.md)、[固定来源与 SHA-256](web-parity/resizable-r1/sources.json)、[差异矩阵](web-parity/resizable-r1/parity-matrix.json)已保存。固定 shadcn 96da9cc；锁文件实际解析 react-resizable-panels 4.5.8，因此键盘和命中规则按该 tag 的实现核对，未混用旧版默认约束。

Nova 改为 1dp 分隔线、4×24dp 圆角 grip（纵向组为24×4）、键盘焦点1dp环。默认范围从15..85%改为0..100%；真实可分配尺寸排除边框及分隔线。端点零宽高不会触发非法 weight；无效 delta/未测量尺寸不改变比例，自定义范围与禁用状态有独立验证。方向键按5%调节、Home/End到范围端点，支持 SetProgress；RTL 水平拖动按逻辑首面板位于右侧计算。

首次鼠标命中测试发现，Compose 的 touchBoundsExpansion 实际只覆盖 stylus/eraser，无法扩展普通鼠标/触摸命中。已改为组级非挂起指针路由：鼠标10dp、触摸20dp，视觉仍保持1dp分隔线。真实鼠标偏移4dp、触摸偏移9dp、RTL/竖向/变更方向取消、边框和自定义5dp分隔线的尺寸计算均通过原生交互测试；没有使用只验证数学的测试代替拖动。

官网 Default、Vertical、Handle、RTL 四组配方已移植：嵌套50/50与25/75、单线/有grip、框架尺寸和内边距、英/阿/希三语；变更语言保留布局比例。复制代码从编译配方生成并校验幂等，旧 Vertical/Handle 扩展示例改名避免冲突。字号14/20、半粗体；小面板文本按CSS居中逻辑保留字形，外层面板裁剪避免内容溢出。

验证：核心真实交互10项＋数学5项＋新Nova基准6项＋目录16项，共37项独立通过；core/shared两个ktlint通过。目录第一次运行4项因旧示例标题重复失败，修复标题后16项全部通过。[四张无缩放联系表](web-parity/resizable-r1/review-sheets/layout.json)包含36个原生配方状态图，已逐张查看；新六张浅深基准也已查看。它们是桌面原生场景内的1440/390 mockWindowInfo，不代表真实手机或浏览器验收。补查旧8图：5项通过，3项深色图 CHANGED。逐像素比较仅分隔线从RGB(38,38,38)变为(34,34,34)，分别120/120/160像素，位置尺寸与文字无变化；[差异证据](web-parity/resizable-r1/legacy-pixel-diff.json)已保存。未作前版隔离，暂不归因为预存或本轮引入，未重录旧基线。

[最终验证证据](web-parity/resizable-r1/verification/final-summary.json)：JS 26.356s、Wasm 18.278s均退出0；JS仍有webpack os/path解析诊断，Wasm一条webpack警告，不能称为干净构建。编译源码、6张新基准及36张目录图哈希均匹配，工作流已结束并清理原始日志。累计改动尚未commit：既有pre-commit要求全仓ktlintFormat/ktlintCheck/detekt，此前该门禁遭自动审批额度拒绝，未绕过hook。

组件继续未勾选：resize光标、多于两面板、collapse/Enter、ref/受控布局/持久化、跨分隔线F6与交点命中、独立Handle范围外命中、完整读屏/Style/字体缩放，以及浏览器桌面/手机浅深均保留。原空间18丢失，[既有浏览器恢复记录](web-parity/popover-r2/browser-status.json)与重建申请仍待答复，未另建替代空间。公共版本0.3.21。按用户优先级下一项 Scroll Area；Editor排除，Attachment遗留暂缓。

### Questionnaire 第一轮：基础状态与官网默认三题流程（2026-10-31，仍未完成）

<!-- progress-created: 2026-10-31; order: 94 -->

已读取[官网 Base Questionnaire](https://ui.shadcn.com/docs/components/base/questionnaire)，固定96da9cc归档14组配方、组件、MDX、Nova CSS及headless组件/类型/四个hook，共[24份来源](web-parity/questionnaire-r1/sources.json)。[计划](web-parity/questionnaire-r1/plan.md)、[剩余矩阵](web-parity/questionnaire-r1/parity-matrix.json)与[源码校验](web-parity/questionnaire-r1/source-check.json)包含Default集合34个字符串的逐字核对、复制代码生成幂等性。版本0.3.20。只迁移了官网Default，其余13组仍待做，原有Kotlin同名示例不算已对应官网。

Choice现在自己提供16dp radio/checkbox图形，选中radio为primary底/8dp反色圆点，checkbox为14dp勾；外部行保留点击与checked语义，装饰图形/快捷键隐藏于读屏。Nova行采用12×10dp padding、10dp间距、lg圆角、输入边框、muted选中底/primary40%边框、hover/focus与invalid样式；CSS边框+padding+indicator+gap的标签起点39dp已由布局测试确认，未重复加入边框留白。进度12/16sp且tabular数字、标题16/22sp、说明和错误14/20sp、选项14/19.25sp，快捷键20dp/10sp；错误额外top8dp。小于640dp时按钮与输入最小44dp。原手动ChoiceInput/ChoiceShortcut调用需移除，目录和旧parts fixture已迁移；requiredMessage现在可空，缺省错误按required选文案。

导航修正：首题Previous、必填题Skip隐藏；optional末题Skip也会提交。未作答optional不能靠Next隐式跳过，必须答题或明确Skip；提交会检查全部启用题并回到首个invalid，受控父级拒绝切换时不强推item。单选固定项与自由输入互斥，切换固定项时保留输入草稿但不提交它；Skip也保留草稿而清除其提交选择。多选payload按集合顺序排序，不随点击顺序改变。输入快捷键可聚焦freeform、选项快捷键选中并聚焦；Enter只确认已选choice或有内容的输入，Ctrl/Meta修饰键按源规则处理。全部键盘/IME行为尚未完成。

最终截图核对发现Default首次打开没有A/B/C，而简单非集合示例有：控制器externalValue/shortcuts/animate/disabled原为SideEffect后写入的普通变量，渲染读不到变化。改为snapshot state后，初始与动态快捷键、接受后的受控item以及外部item回切均有UI测试证明，未用额外点击掩盖首次渲染差异。

Default已按官网三题（direction/signals/timing）、letters、448dp最大宽与原文案实现；提交显示Agent plan saved及真实答案Toast。`generate_questionnaire_recipe_code.py`从实际编译区域生成独立导入/集合/辅助函数和配方，未维护第二套手写预览代码。提交map按Kotlin接口返回，原生Toast摆在窗口边缘；其灰色圆点、360dp卡片及排版仍与Sonner存在差异，留待Toast组件复核，当前只证明payload与操作结果。

[最终XML、源码哈希与ledger](web-parity/questionnaire-r1/verification)：38项针对性验证通过（控制器10、UI8、新Nova图4、目录16=4方法×模拟1440/390×浅深），两项owning ktlint通过；JS命令exit0（25.620s，保留既有webpack os/path诊断），Wasm命令exit0（18.505s，有1项webpack warning）。新4图record后实际查看并独立verify，最终[36张目录原生图的四张拼图](web-parity/questionnaire-r1/review-sheets)保留原像素无缩放裁切。捕获包含初始、必填错误、多选、末题、自由输入/草稿和三种提交Toast，保留真实鼠标hover/输入focus状态；它们是1024×768原生桌面场景内的模拟宽度与主题，不是浏览器或真实手机页面验收。工作流已清理。

旧30张图本轮只选了default/choice_parts浅深4张独立verify。临时恢复改动前核心/样式及原fixture（仅为未选中新增测试加两处不影响旧fixture的编译桥接）后，仍4项CHANGED，但actual哈希全部不同：[隔离证据](web-parity/questionnaire-r1/legacy-differences/isolation-result.json)说明预存基准差异和本轮有意修改混合，不能把当前差异全称为预存。已逐字恢复当前源码；旧基准未重录，另26张旧图未在本轮执行。

Questionnaire保持未勾选。其余13配方、浏览器桌面/手机浅深、IME/重复按键、完整方向键与读屏关联、清空已校验答案后的即时重新验证、HTML输入约束验证、动态集合与受控答案/恢复/持久化、AnimatedVisibility常量true的进入时机与减弱动态效果、无Description标题的额外下间距、任意slot/Style覆盖和字体缩放等仍待核对。浏览器原空间18丢失，[恢复记录](web-parity/popover-r2/browser-status.json)及重建申请仍待答复，未创建替代空间。按用户优先级下一步进入尚未开始的Resizable；Editor排除，Attachment遗留暂缓。

### Progress 第一轮：自动轨道、真实数值与四组官网配方（2026-10-31，仍未完成）

<!-- progress-created: 2026-10-31; order: 93 -->

对照[官网 Base Progress](https://ui.shadcn.com/docs/components/base/progress)，按固定96da9cc归档组件、MDX、Default/Label/Controlled/RTL四配方、Nova CSS，并补充Base UI1.6的ProgressValue源文件，共[8份可校验来源](web-parity/progress-r1/sources.json)。[计划与迁移说明](web-parity/progress-r1/plan.md)、[剩余矩阵](web-parity/progress-r1/parity-matrix.json)。库及安装说明版本0.3.19。

根组件现在始终追加轨道，content只放标签等可变内容；改为gap12的横向换行布局，ProgressValue向逻辑末端自动留白，RTL同步翻转。原有content的ColumnScope已移除，根style包含标签和轨道；目录与旧截图fixture中手动ProgressTrack调用已迁移，避免双轨道。自定义轨道可单独用ProgressTrack/显式value的ProgressIndicator。Nova标签/数值14/20sp（保留完整行高），标签medium、数值muted；轨道4dp，填充末端改为直角，仅外轨道圆角裁剪。库max默认1保持Kotlin fraction约定，官网0..100配方显式max100，不隐瞒这项接口适配。

进度语义和显示值读取逻辑目标，不再逐帧读动画中间值；宽度在测量阶段读取独立动画状态，按[文档中的Tailwind transition-all默认值](https://tailwindcss.com/docs/transition-property)使用150ms与0.4/0/0.2/1曲线，实际浏览器computed timing尚未验证。不确定进度使用Indeterminate范围且默认数值留空；Base UI Value的aria-hidden:true对应为hideFromAccessibility。getValueLabel改为stateDescription保留标签；NaN、负数、越界和无效max不会生成非法范围。以上原生语义属性测试不代替真实读屏验收，尤其标签关联与重复朗读仍需核对。

Default现在13→66、500ms、60%宽；Label为56%和384dp最大宽；Controlled初始50、0..100/step1滑块；RTL保留三条源文案及Arabic“٥٦%”，目录语言工具栏可切换English/Hebrew。复制代码由实际编译配方生成，独立导入、不依赖目录RtlPreview，重复生成不变；Usage与官网33及说明文字同步，原Kotlin扩展示例保留。

[最终验证记录、XML和源码哈希](web-parity/progress-r1/verification)已归档。JS命令exit0（最终26.683s），仍有既有os/path诊断；Wasm命令exit0（18.670s），有一项webpack warning。最后的Usage33和说明改动由最终JS及Wasm编译字符串证据确认纳入，未用较早构建冒充最终产物。工作流已清理。最终针对性测试29项通过：核心9项布局/填充像素/RTL/目标值与动画解耦/不确定/数值边界/描述/窄宽换行/Value隐藏语义，4张新Nova浅深与RTL基准独立verify；目录16项（4方法×模拟1440/390×浅深）验证500ms前后的13/66、尺寸和标签、方向键/Home/End、真实鼠标点击slider并比对值，以及实际三语言切换。[28张目录原生图的审阅拼图](web-parity/progress-r1/review-sheets)与单独查看的4张新基准保留原像素，无缩放裁切。模拟窗口仍是1024×768桌面原生场景，组件截图不证明真实手机和完整页面；浏览器空间18丢失的恢复记录与未答复申请继续保留，未擅自创建替代空间。

旧4张图独立verify为CHANGED。恢复本轮前的完整核心备份和原手动Track fixture后仍4项CHANGED，但四张actual哈希均与当前版本不同：[隔离记录](web-parity/progress-r1/legacy-differences/isolation-result.json)说明既有基准差异与本轮端点/布局变化混合，不能将当前四项全部宣称为预存。已恢复本轮源码及迁移后的fixture，没有重录旧基准。

Progress保持未勾选。完整浏览器浅深桌面/手机、读屏、Controlled触摸拖动、动画中间帧/反向/颜色尺寸过渡、字体缩放与长文本、全部Style覆盖及Root显式高度的flex伸展行为仍待完成；Track自定义shape目前仍受标准圆角裁剪，完整上游min/null/format/render接口也未逐项映射。下一步优先进入尚未开始本轮核对的Questionnaire；Editor排除，Attachment遗留暂缓。

### Popover 第二轮：官网五组配方与真实输入（2026-10-31，仍未完成）

<!-- progress-created: 2026-10-31; order: 92 -->

复用[上一轮固定官网来源](web-parity/popover-r1/sources.json)，新增实际编译的 `PopoverRecipes.kt`：Default 为 320dp 四项尺寸网格（100%、300px、25px、none），输入跨两列；Basic 为 288dp/alignStart 的标题说明；Align 用 Sm 按钮、24dp 间距和三个 160dp 面板；With Form 为 256dp/alignStart、两项水平 Field、半宽标签与 16dp 组间距；RTL 包含四物理边与两逻辑边，三语言共24条文案[逐字校验](web-parity/popover-r2/source-check.json)。物理左右按语言方向映射到现有逻辑 PopupPlacement，保留 Popup 内文方向；完整翻转/碰撞矩阵尚待实测。五组置于目录首位，原有 Kotlin 扩展示例保留；Usage 与官网 Header/Title/Description 组合及导入同步。`generate_popover_recipe_code.py` 从实际配方区域生成独立可复制代码，包含辅助函数和导入、不依赖目录 RtlPreview，重复执行无变化。

24项目录测试（6方法×1440/390模拟窗口×浅深）通过：真实鼠标打开、Escape 关闭；Default 四个初值、标签聚焦与修改、关闭卸载后恢复默认值；With Form 两项输入实际修改、水平排列；三个 Align 触发器与面板；Arabic 六边触发及面板文案，实际语言菜单切换 English/Hebrew 并打开逻辑边面板。所有预期保持真实状态与官网初值，没有增加业务提交按钮。宽高断言包括 Default320×230dp、Basic288×62dp、Form256×152dp、Align160dp 与28dp按钮。此前核心9项回归和两项 owning ktlint 通过。[最终XML与验证记录](web-parity/popover-r2/verification)已归档，JS命令exit0（20.329s），Wasm命令exit0（13.344s）；仍有既有webpack os/path诊断和Wasm warning，不声称无诊断构建。工作流已清理。

测试发现两处需要精确区分：Input 编辑语义节点高度22dp不是32dp外框，改为测量已存在的行根节点、未添加强制尺寸包装；冻结时钟下输入后显式推进一帧周期，未降低输入值断言。新增高度断言又暴露 Default 的 leading-none 标题被原生字形指标撑到16dp，导致整体232dp；仅为该官网 h4增加14sp行框测量、字形居中允许外溢绘制，不裁字形，保持源配方 leading-none 并使原230dp期望通过。标准 PopoverTitle 的20sp行框不受此配方调整影响。

[36张原生面板截图的四张审阅拼图](web-parity/popover-r2/review-sheets)已逐张查看，保留原像素、无缩放裁切；包含编辑后的表单、三个 Align 与三语言面板。截图只覆盖面板，不证明完整页面、外部阴影或锚点位置。模拟390和1440的面板渲染相同，原生 Popup 仍使用实际1024×768桌面场景自己的 WindowInfo，因此不能把这些图作为手机 CSS/响应式输入字号验证。本轮再次只读确认浏览器空间18仍不存在，[恢复记录](web-parity/popover-r2/browser-status.json)保留指南要求与已提交但尚未答复的重建申请；没有创建替代空间。

组件保持未勾选。[剩余矩阵](web-parity/popover-r2/parity-matrix.json)保留默认非模态、初始/最终焦点、受控拒绝/嵌套/快速切换、100ms方向动画、完整键盘/读屏与官网桌面/手机浅深对照。核心未改，库版本保持0.3.18；上一轮4张旧图预存差异记录继续有效，本轮没有重录旧图。按用户优先级，下一步先进入尚未开始本轮核对的 Progress，Popover 的剩余交互与浏览器项目继续保留。Editor 排除，Attachment 遗留暂缓。

### Popover 第一轮：Nova 面板与完整文本行高（2026-10-31，仍未完成）

<!-- progress-created: 2026-10-31; order: 91 -->

已读取[官网 Base Popover](https://ui.shadcn.com/docs/components/base/popover)，按固定上游 `96da9cc7f1aa10580a3741664e2a77c721206c00` 归档组件、MDX、Default/Basic/Align/With Form/RTL 五个配方及 Nova CSS，[8 份来源与校验](web-parity/popover-r1/sources.json)。[计划](web-parity/popover-r1/plan.md)与[差异矩阵](web-parity/popover-r1/parity-matrix.json)。本轮版本 `0.3.18`。

Nova 核心面板保持默认 288dp，改为 10dp 内边距、10dp 子项间距、lg 圆角、foreground/10 ring 与双层 shadow-md；Header 间距由 4dp 改为 2dp，标题与说明 14/20sp，标题 medium，说明 muted。默认正文继承 14/20sp，调用方 Style 仍可覆盖宽度。其他预设保留旧样式；本轮没有修改先前已有的 panelFocus 实现。

布局测试最初暴露标题区只有 34dp：原生单行首尾行高被裁减。为 Nova 文本显式设置 LineHeightStyle Center/Trim.None 后，原断言 42dp 通过，没有降低期望。最终独立验证共 9 项通过：5 项布局（浅/深 288×70dp 的双 20dp 内容、10dp 内边距/间距、42dp Header、自定义 320dp 宽、正文20dp完整行高），2 张新 Nova 浅/深截图，2 项既有关闭返回焦点测试。两项 owning ktlintCheck 通过。[最终验证记录与 XML](web-parity/popover-r1/verification)已归档，JS 构建命令 exit0（47.292s），Wasm 构建命令 exit0（24.485s）；仍有既有 webpack os/path 诊断及 Wasm warning，不声称无诊断构建。工作流已清理托管日志。新增两图先 record、实际查看后独立 verify；首次截图容器过窄导致弹层贴场景边缘，已改为 360×180dp 内容面板、Outline trigger、alignStart 后重新查看与 verify。[原生截图说明](web-parity/popover-r1/native-captures.json)明确不是浏览器截图，也没有模拟手机验收。

旧 Vega 四张图单独 verify 均 CHANGED。临时撤回本轮 Nova 修改、保留此前未提交的 panelFocus 行为后，四张 actual 的 SHA256 与本轮源码完全一致；[隔离证据](web-parity/popover-r1/legacy-differences/isolation-result.json)证明差异早于本轮，不能推断为纯 HEAD 通过。本轮源码已按备份逐字恢复；没有重录旧基准。

尚未关闭的差异包括五组官网配方（当前默认表单只有两项，官网 Default 为四项/320dp）、三语 RTL 的物理边/逻辑边、受控拒绝与嵌套、碰撞定位、焦点与读屏、官网 100ms 动画及完整浏览器桌面/手机浅深矩阵。[Base UI 文档](https://base-ui.com/react/components/popover)的 Root.modal 默认 false，而本地仍 true，初始焦点与关闭恢复也需一并核对，不能仅改默认值后宣称功能等价。原浏览器空间 18 已丢失，替换空间申请仍待答复，未擅自重建。Popover 保持未勾选；Editor 排除，Attachment 遗留暂缓。下一步继续 Popover 配方与交互，之后才进入尚未开始的 Progress。

### Pagination 第一轮：四组官网配方、Nova 与当前值裁剪（2026-10-31，仍未完成）

<!-- progress-created: 2026-10-31; order: 90 -->

已按[官网页面](https://ui.shadcn.com/docs/components/base/pagination)及固定96da9cc的[8份组件/示例/Nova/Select来源](web-parity/pagination-r1/sources.json)开始复查。[计划](web-parity/pagination-r1/plan.md)、[完整矩阵](web-parity/pagination-r1/parity-matrix.json)。本地原为36dp图标分页，默认有10页/受控状态；官网Nova为32dp，Default只含1、固定选中的2、3、省略号、前后链接，Simple有1–5，Icons Only还含行数选择器。“Icons Only”指没有页码，不是桌面隐藏Previous/Next文字。

- Nova页码复用已存在的Button渲染路径，保留32dp视觉尺寸、Outline/Ghost、14/20 Medium、禁用透明度及调用方约束；48dp触摸目标不会放大32dp绘制区。内容间隔改2dp，根层按可用宽度居中，caller Style最后覆盖。省略号为32dp容器/16dp Lucide并隐藏装饰语义，旧填充字形在非Nova保留。
- Previous/Next默认文字从640dp起显示，以下隐藏；16dp线条箭头按RTL镜像，6/10dp逻辑内边距、6dp图文间隔。隐藏文字时实测34dp宽、32dp高，显示后按钮宽度比文字多40dp，均有回归。默认英语ARIA描述与可翻译可见文案分开，可由ariaLabel覆盖。
- PaginationLink补必选content槽位重载及size/style，阿拉伯数字不再硬编码成Int字符串；Nova使用实际可见文字作名称。原数值重载、受控page/pageCount/windowing与First/Last保留为Kotlin扩展，非Nova默认排版保留。新增签名/版本及安装示例同步0.3.17，已有编译产物需重新编译。

四个真实官网示例已排在Kotlin扩展前：Default固定active=2、Simple五页、Icons Only使用横向Field与80dp Select，初值25，可实际选择10/25/50/100；标签点击也会打开选择器。RTL初始Arabic，保留6条翻译、阿拉伯数字转换及真实EN/HE切换。官网占位href="#"不引入业务翻页状态，点击3/5后仍选中2；可操作分页另标Controlled (Kotlin)。四份可复制代码从实际编译区域生成，包含导入与辅助函数，不依赖私有RtlPreview。Composition、API和导航回调说明已补齐。

逐图检查补出了Select依赖遗漏：144dp、Sm28dp语言触发器的Hebrew当前值会换两行并裁掉；以前的“语言标签不换行”测试只覆盖SelectItem菜单项，不覆盖SelectValue。新增测试先复现lineCount=2，按固定Select TSX的nowrap/line-clamp-1对Nova当前值设置单行/Ellipsis，保留非Nova原逻辑。实际Hebrew及超长值均有逻辑/尺寸回归；超长样例的GetTextLayoutResult.isLineEllipsized返回false，但[实际PNG](web-parity/pagination-r1/select-value-fix/long-value-diagnostic.png)明确绘制省略号，故用两张查看后的浅深golden验证实际视觉，没有按错误假设修改产品或只删掉视觉要求。最终四张Hebrew工具栏图已复看，其余24张与先前已看图逐字节一致。

[最终XML与ledger](web-parity/pagination-r1/verification)：核心独立24项通过（Pagination11行为+4新Nova图，SelectLabelLayout7项含2张既有Arabic图，SelectValueOverflow2新图）；目录四配方×模拟1440/390及浅深共16项通过。两项所属ktlint通过；最终JS/Wasm在Select修复后分别单worker、非并行构建成功，耗时133.971s/94.374s，最终验证后生产源码未改。中间JS139.598s和因新增修复主动中断的Wasm41.015s不冒充最终结果。既有AGP/Text内部API/Gradle警告保留，实际commonMain Detekt未重复同一失败，不声称完整分析通过。

[6张新增基准及hash](web-parity/pagination-r1/new-goldens.json)严格限定4张Pagination与2张Select省略；均先查看再独立verify，并按原规则生成不裁剪、不透明底色/24px边距文档预览、更新元数据。原Pagination两张Vega基准仍changed：用本轮之前的精确Pagination/Styles体、仅加入不被旧fixture使用的签名编译shim复验，两张actual逐字节相同，[隔离结果](web-parity/pagination-r1/legacy-differences/isolation-result.json)确认本轮未引入；当前源码已逐字节恢复，旧基准没有重录，不能把它们计绿。

[28张原生画面](web-parity/pagination-r1/native-captures.json)已检查，覆盖固定页码点击后的hover、五页、25/50行及菜单展开、Arabic/Hebrew。LocalWindowInfo模拟1440/390dp，但实际测试场景仍1024×768、内容约束640/390dp；不是官网/本地浏览器四象限，也不能证明真实手机碰撞、字体/换行和读屏。

仍保留HTML nav/ul/li/anchor、aria-current与原生Role.Button/selected的差别；href/render/target及当前页hash导航没有映射，导航由Kotlin回调/可控状态承担。完整指针/键盘/读屏、RTL逻辑padding的浏览器像素、动态主题/大字号/长内容/Style与触摸尺寸矩阵，Select独立页面及Kotlin额外分页窗口的边界仍待关闭。组件保持未勾选。

[浏览器状态](web-parity/pagination-r1/browser-status.json)：原空间18仍缺失，本轮将先前未答复的新建专用验收空间询问重新呈现，尚未收到答复；未另开空间或接管用户标签。下一项按优先级推进未开始的Popover。Editor排除，Attachment遗留暂缓；此前全仓提交门禁遭自动审批因额度拒绝，未绕过hook，累计修改尚未commit。

### Navigation Menu 第二轮：官网 Default / RTL 配方与面板链接（2026-10-31，仍未完成）

<!-- progress-created: 2026-10-31; order: 89 -->

官网两组实际内容已置于Kotlin扩展示例之前，使用上一轮固定revision/依赖的[10份来源](web-parity/navigation-menu-r1/sources.json)。Default恢复Getting started三链接、Components六链接、With Icon三图标/占位链接及Docs；Components仅在窗口达到768dp时组合显示，双列8dp间隔，内容宽500dp、1024dp后600dp。Getting started内容宽384dp、Icon内容宽200dp；标题14/14 Medium，说明14/20并限制两行。三语RTL保留初始Arabic、真实语言选择器及全部[75条逐字文案](web-parity/navigation-menu-r2/translation-verification.json)，实际切换English/Hebrew同时更新内容和方向。[完整矩阵](web-parity/navigation-menu-r2/parity-matrix.json)。

可复制代码从实际编译的`NavigationMenuRecipes.kt`区域生成，包含导入、数据和辅助函数；RTL导出提供language参数/初始Arabic，不依赖目录私有RtlPreview。两组均沿用示例已有Lucide图标包，未给core增加图标集依赖。旧Kotlin示例通过构造后不可变copy追加官网两组，避免仅为新增两组重缩进六百余行旧代码。

本轮补真实示例后发现两个依赖问题：

- 面板内NavigationMenuLink沿用了顶层普通链接“悬停关闭菜单”的逻辑。最初只断言最终可见的测试通过，但受控回调追踪记录了`[null, first]`，证明关闭后又被内容悬停重开。现按面板上下文区分，只允许顶层普通链接在悬停时关闭；每个新Root重置上下文。新增两个回归同时验证面板悬停不请求null、选择后执行一次动作，以及顶层Docs悬停仍关闭；没有把最终可见当成从未关闭的证据。
- Popup内读取的LocalUriHandler没有使用外层覆盖。直接调用实际Typography按钮的OnClick会关闭面板但URI记录为空，外侧Docs控制则记录正确；将导航依赖在示例根、Popup之前捕获，并显式传回调给面板辅助函数后，真实鼠标点击才全部验证通过。此处记录已观察的依赖作用域差别，不猜测框架内部根因。版本及安装示例同步0.3.16，公共函数签名未再变化。

[最终XML](web-parity/navigation-menu-r2/verification)：目录18项通过（Default/图标/Components/三语切换各四种模拟宽度主题，共16项，加顺序/导出依赖和767/768/1023/1024实时断点两项）；真实鼠标逐一选择九条说明链接及Docs并验证URI和关闭，图标占位项只关闭、不虚构业务状态。核心独立verify22项通过（15行为+既有4张Nova与3张Vega图），本轮未record任何基准。初始应用测试14失败；128ms短于实际250ms弹层动画，改400ms后画面稳定但URI仍失败，显式鼠标移动也未解决；仅在导航回调前移后全部实际交互通过。直接OnClick诊断四例及打印已从最终测试中移除，诊断XML留档，没有用语义调用替代真实指针验收。

[26张原生图与hash](web-parity/navigation-menu-r2/native-captures.json)已逐张查看，涵盖Default闭合/三种面板、Arabic闭合/展开及Hebrew展开，已增加四张实际配方的文档预览并更新元数据。边界必须区分：LocalWindowInfo模拟1440/390dp，而场景仍是1024×768、内容约束720/390dp；面板截图只是元素裁剪，不能证明Root对齐、碰撞、外部ring/shadow或真实手机最大宽度。Getting started面板测得392dp（384内容+两侧4），其大于390的模拟宽度也说明不能拿这些图当作手机布局已通过。原生字形/换行与浏览器的精确对照继续保留。

两个所属ktlintCheck通过；JS/Wasm分别单worker非并行构建成功，耗时145.955s/94.324s，[最终ledger](web-parity/navigation-menu-r2/verification/ledger.json)已归档；最终验证后无生产源码改动。既有AGP实验选项、Text内部API和Gradle废弃警告继续保留。实际commonMain Detekt仍承接前述未通过记录，本轮没有重复同一失败或用默认零源码任务冒充静态分析通过。

组件仍未完成：root共享视口、Root align（包括RTL end）、跨面板宽高/位置/方向过渡还未实现，当前逐项AnchoredPopup/透传Positioner不等价；完整键盘、受控拒绝、500ms边界、安全指针通道、快速切换、嵌套/重排/卸载、主题/字号/Style/触摸尺寸与读屏矩阵均待关闭。图标项保留无业务状态并关闭菜单，但官网`href="#"`的当前页hash导航没有映射；普通链接用原生Button语义和显式URI回调，不能当作HTML anchor/render/target完全等价。Components通过条件组合隐藏，而官网用CSS保留挂载，跨断点的状态/关闭行为仍需核对。

[浏览器状态](web-parity/navigation-menu-r2/browser-status.json)：原Ego空间18复查仍不存在，既有重建询问未回复，未新建空间、接管用户标签或导航。按照用户优先尚未开始本轮完整对照的要求，下一项Pagination；以上Navigation Menu缺口保留，继续未勾选。Editor排除、Attachment遗留暂缓。此前全仓提交门禁遭自动审批因额度拒绝，未绕过hook，累计修改仍未commit。

### Navigation Menu 第一轮：Nova 基础样式与真实开关事件（2026-10-31，仍未完成）

<!-- progress-created: 2026-10-31; order: 88 -->

按用户优先级开始本轮此前尚未核对的Navigation Menu。[官网文档](https://ui.shadcn.com/docs/components/base/navigation-menu)与固定上游96da9cc的组件/Default/RTL/Nova样式、实际依赖Base UI 1.6.0原语及常量共[10份来源和hash](web-parity/navigation-menu-r1/sources.json)；[完整计划](web-parity/navigation-menu-r1/plan.md)、[剩余矩阵](web-parity/navigation-menu-r1/parity-matrix.json)。官网首屏并不是原来的Home/Getting started/Docs三项原生示例，旧首例已明确标为Native overview (Kotlin)；完整Default和三语RTL仍需移植，不拿旧示例当作配方完成。

- Nova列表间隔改为0；Trigger为36dp高、10×6dp内边距、14/20 Medium，补12dp装饰箭头、4dp间距/1dp偏移和300ms旋转，打开态muted/50、hover/focus为muted。Link为8dp内边距、14/20常规字号，面板内md圆角、外侧lg圆角；面板内抑制额外focus ring。Panel取消强制320dp，改为内容固有宽度、4dp内边距、popover前景色继承、外描ring和shadow。既有非Nova样式保留，旧3图独立verify通过。
- 固定依赖源码确认打开/关闭默认均为50ms，补closeDelayDuration；旧skipDelayDuration作为Kotlin扩展保留。延迟任务改为读取最新controller/回调，禁用或卸载清理悬停状态，旧关闭任务只关闭仍为自己ID的当前项。测试中的旧回调old:first及禁用后仍请求first均已复现并修复。
- 复验曾在203秒后通过wrapper中断，不能计为通过。后续真实状态记录确认持续悬停时重复first/null循环；单看全局输入模式无效，因为桌面可能默认Keyboard。现在由稳定item scope记录实际打开方式：悬停/触摸不抢焦点，键盘打开在下一帧聚焦实际面板。Escape关闭后只在关闭被接受、没有其他项打开时恢复仍挂载的触发器；控件ID不再覆盖可访问名称，使用真实可见文案。
- 真实触摸第二次点击曾记录`first → null → first`：外部关闭先在按下时发生，松开时已重组的触发器又按新状态打开。现在保存按下时expanded快照并据此切换，键盘清空指针快照；修复后实际双次触摸可关闭。官网500ms悬停点击粘滞窗口来自同版本常量，已按指针类型和开启动作接入；精确边界/浏览器行为仍待专门验收。
- Link默认请求关闭并提供closeOnClick=false，API、Composition与安装版本同步0.3.15。新增参数涉及已发布函数签名，现有编译产物需要重新编译；常见尾随lambda/具名调用保持可用。

[测试XML](web-parity/navigation-menu-r1/verification)：13项行为+4张新增Nova图+3张旧图，独立verify共20项通过；覆盖几何、半muted像素、零间隔、最新回调、禁用取消、计时器换项、持续悬停、链接关闭/保留、真实触摸、键盘Enter/Escape及恢复焦点、调用方Style尺寸和固有面板宽度。初始RED中的旧leave测试在新项赋值之前就因帧未推进失败，部分click测试也混入滞留鼠标位置，不能将它们算作特定产品原因的证据；已改为真实移动离开、组合前冻结时钟及事件后定向推进，再核对状态和画面。触摸竞态和悬停循环另有直接状态记录，未用测试环境覆盖输入模式掩盖问题。

四张新原图先查看后独立verify，[范围与hash](web-parity/navigation-menu-r1/new-goldens.json)。它们是样式fixture，含三条Getting started链接，不是官网完整Default/RTL，更不是浏览器/手机矩阵。Popup图保留1024×768测试场景的透明外围；已按现有不裁剪/不缩放、24px边距和不透明主题底色规则派生四张文档预览并更新元数据，旧基准未重录。两个所属ktlintCheck通过；JS/Wasm分别单worker、非并行构建成功，耗时134.757s/94.455s，[最终ledger](web-parity/navigation-menu-r1/verification/ledger.json)已归档。生产源码在最终验证后未改动，保留既有AGP实验选项、Text内部API及Gradle废弃警告。

尚未关闭：官网完整Default/RTL及响应式Components隐藏/双列/宽度、真实链接和图标；root共享视口及align、跨面板位置/宽高与方向过渡；完整方向键/Tab/焦点移出、受控拒绝、安全指针通道、快速换项、嵌套/重排/卸载、读屏及HTML导航/链接语义。当前仍是逐项AnchoredPopup与透传Positioner，不能以本轮样式修复销项。自定义child/Style、最小触摸尺寸、动态主题/大字号/长文案及全部状态矩阵继续开放。下一步补官网两组完整配方，再处理共享视口和完整状态。

[浏览器状态](web-parity/navigation-menu-r1/browser-status.json)：原空间18只读复查仍不存在，既有重建询问未收到答复，未另建空间或接管用户标签。真实公共源码Detekt仍承接上轮未通过状态，本轮未重复相同失败，不能声称完整静态分析通过。Navigation Menu保持未勾选；Editor排除、Attachment遗留暂缓。此前全仓pre-commit遭自动审批因额度拒绝，未绕过hook，累计修改尚未commit。

### Message Scroller 第三轮：前文、历史与目录配方（2026-10-31，仍未完成）

<!-- progress-created: 2026-10-31; order: 87 -->

本轮按固定上游 revision 96da9cc 补齐 Previous Context、Load History、Visibility，累计6/11个官网配方；[42份来源及hash](web-parity/message-scroller-r1/sources.json)、[本轮源码事实](web-parity/message-scroller-r3/source-notes.json)、[剩余矩阵](web-parity/message-scroller-r3/parity-matrix.json)。导出代码继续从实际编译的源码区域生成，六个示例各含独立导入与辅助函数。实际 Default/chat 仍未迁移，临时首屏现为 Keeping Context Visible，不能把它视为官网默认示例完成。

- Keeping Context Visible：保留原8条消息，`get(2)`是最初两条消息而非两轮对话；64–128dp前文滑块、24dp margin、固定下一条用户文本、两行省略、Plus菜单、发送与重置均按源码接入。读取实际helper确认先等待1000ms，再按词及尾随空白每35ms输出；忙碌时禁用控件，连续三次发送后显示耗尽提示。协程由keyed子树拥有，重置/卸载取消；没有独立游离任务。
- Loading Earlier Messages：先显示原10条中的后5条，一次加载全部，按钮随后为禁用的History Loaded，并显示真实toast；匿名End of Conversation标记不注册锚点。前插保留读者原段落位置，Reset只重建Root并保留Provider。此前计划写的“多次批量加载”不符合官网实际配方，已更正。目录使用窗口toast host；独立导出示例可传ToastState，缺省局部Toaster的位置差别保留说明。
- Tracking the Reader’s Position：原4个用户ID、12dp margin、36dp外侧触发器、256dp左侧面板及当前行样式，接入真实锚点查询、焦点/悬停和顺滑跳转。官网外侧几何在原生窄布局中导致触发器大部分被裁掉，尚未擅自改到卡片内部；需恢复浏览器后对照实际溢出/碰撞与触摸打开行为。selected语义也不等同完整aria-current=location映射。

配方发现并修复两个真实组件依赖问题：

- InputGroupAddon两个重载增加`style`，默认逻辑内边距缓存为Style，调用方按边覆盖而非累加；将点击/焦点修饰符放在styleable外层，使padding也可点击聚焦输入框。新增5项几何、RTL、String重载与真实点击回归；API及安装示例同步0.3.14。修正旧focused测试为实际编辑框tag后，用精确的本轮之前组件体加无行为Style签名shim隔离，旧12张默认图逐字节一致；[隔离证据](web-parity/message-scroller-r3/legacy-differences/focused-isolation-result.json)。这项隔离在Button修复之前进行，不能扩大为最终Button版本已复验全部旧图；旧基准未重录。
- Button原先`fillMaxWidth`只扩大可点击外层，绘制层仍包住文字，History底部按钮没有铺满。改为单子节点Layout把原调用方约束传到绘制层，再单独扩展最小触摸区域；全宽、指定高度实际着色，并保留48dp触摸区域内28dp图标按钮尺寸。尺寸测试由3失败/1通过变为4通过，另7项既有Nova回归通过。

[最终XML与ledger](web-parity/message-scroller-r3/verification)：核心独立verify共78项（Scroller29行为+15纯逻辑+14旧图，Tabs4，Addon5，Button尺寸4与Nova7），目录31项通过；两个所属ktlintCheck通过。JS/Wasm分别以单worker非并行方式构建成功，耗时147.935s/94.733s。最后新增的测试仅核对已保存PNG色值，未更改产品代码。本轮未录制新旧黄金基准。

[20张原生画面及hash](web-parity/message-scroller-r3/native-captures.json)已逐张查看，覆盖三组初始态、Context发送耗尽和目录面板，在432/360dp内容约束及浅深色下采集；实际卡片宽384/312dp，底层测试场景仍为1024×768，不能当成手机浏览器viewport或弹层碰撞证据。已添加六张文档预览。曾观察手机深色目录当前行疑似白色，但异常原图未单独归档，未能复现；新增保存PNG的色值断言在四例单独运行和完整31例顺序中均通过（深色#262626、浅色#f5f5f5），最终图已复看。本轮未修改主题代码，也未宣称已找到异常根因。

浏览器仍未验收：原Ego工作空间18的只读检查返回不存在，既有重建询问未收到答复，未接管用户标签或另开空间。[浏览器状态](web-parity/message-scroller-r3/browser-status.json)。上轮真正覆盖公共源码的Detekt仍失败（core10731/app26557条）；本轮未重复相同失败，也不能拿默认任务零源码成功当作静态分析通过。现有警告与Tabs/原语遗留继续保留。

仍余Default、Anchoring、Group Chat、Streaming、Animation五配方；六个已迁移配方也需要浏览器四象限与完整状态验收。text-balance、tabular-nums、完整辅助技术及原语差异见矩阵。此前“必须先做完Scroller再转Navigation Menu”是代理拟定的顺序，现按用户“优先尚未开始本轮页面与完整状态对照”的要求调整：下一项Navigation Menu；以上缺口保持开放，Message Scroller不勾选。Editor排除，Attachment遗留继续暂缓。累计修改未提交：此前全仓pre-commit遭自动审批因额度拒绝，未绕过hook。

### Message Scroller 第二轮：三个官网配方及其真实依赖（2026-10-30，仍未完成）

<!-- progress-created: 2026-10-30; order: 86 -->

按上一轮[计划](web-parity/message-scroller-r1/plan.md)继续，新增 Opening Saved Threads、Jumping to Messages、Reading Scroll State 三个官网配方；没有用原生 Provider controls 替代这三个示例。仍固定96da9cc，本轮额外读取 MessageAnimated、动画预设、Base Tabs、Nova Card/Tabs 选择器，来源归档累计33文件，见[来源](web-parity/message-scroller-r1/sources.json)。官网配方默认Rhea（MessageAnimated实际导入radix-rhea），本库依然对齐Nova目标；例如常规内容间距24dp、Card间距16dp为Nova，不误用Rhea数值。

新增和修正：

- **Opening Saved Threads**：保留原四条消息和open-* IDs、560dp高/最大384dp宽卡片、三个等分Tabs。初次测量完成后显式跳open-3并保留64dp前文；start/end/last-anchor切换及重复点击已实现。不把一次性LastAnchor的“短回合回退末端”替代官网显式跳转，也不声称具有浏览器before-paint的同样时机。
- **Jumping to Messages**：原八条消息、四个用户锚点及文案；Jump to…下拉、Conversations组、256dp菜单、超过42字符取前39字符加省略号、选择后关闭菜单并Smooth/Start跳转。无Footer卡片保留外侧16dp底部留白，Content独立16dp内边距，宽度缩小时CardAction不会遮住描述。
- **Reading Scroll State**：保留12条checkpoint消息、16dp行间距及官网四种完整页脚文案，状态来自真实messageScrollerScrollable查询。用户消息按固定源码以10dp/260ms/[.16,1,.3,1]入场，助手初始不动；内容段落间距8dp。动画中间帧、系统减少动态效果联动仍未做浏览器验收。
- **组件依赖Tabs**：Nova trigger原来16×4内边距，改为源码6×2和1dp透明边框；水平25dp高来自32dp列表扣6dp内边距再扣源码calc的1dp。选中indicator改跟踪真正的外层trigger，修复等分Tab底色只包文字；字符串标签采用单行intrinsic测量，修复手机last-anchor换行后被裁切。深色选中底色/边框使用input/30与input，避免黑色底。Vega的间距/配色保持原值；Tabs独立页面/完整状态并未据此宣称完成。
- **真正的手机反馈循环**：初次运行在手机底部页脚断言处持续占用CPU，已由测试线程栈和运行状态确认后，经wrapper停止该次运行。页脚在“middle一行”和“bottom两行”切换，缩小视口后旧末端位置变成离末端20dp，导致状态反复翻转。新增RED复现缩小200→160后仍停400而非440；修复仅在内容不变、无anchor/spacer且原本在末端的视口缩小时保持末端，不启用autoScroll=false后的内容跟随。自由阅读保持不动，后续追加也不被拉走。没有固定页脚高度掩盖问题；该逻辑是KMP原生布局修正，不声称React中有同样显式代码，浏览器原生锚定效果仍待对照。
- 导出代码由[scripts/generate_message_scroller_recipe_code.py](../scripts/generate_message_scroller_recipe_code.py)直接从编译预览的imports/shared/各配方段生成，包含原始数据和辅助函数；生成幂等，不独立维护另一份示例实现。三个配方置于旧补充示例之前；实际官网Default完整聊天首屏还没迁移，当前首屏临时为Opening Saved Threads，保持明确缺口。

验证及未关闭项：

- 核心独立verify62/62：Scroller29（27行为+2新Nova图）、既有纯函数15、旧Scroller图14、Tabs Nova4（2行为+2新图）。目录15/15：三个配方×桌面/手机宽度×浅/深共12组完整交互，加原有Provider controls3项。手机footer循环修复后完整执行结束，初次定位/重复Tabs/下拉前后跳转/Home/PageDown/End和页脚状态均验证通过。
- 生成并逐张查看了12张**原生**画面：432/360dp容器，24dp外侧留白，对应384/312dp卡片；这些不是浏览器截图。截图曾揭示测试未发现的手机标签裁切，已修复并重新查看最终画面。[原生图片及hash](web-parity/message-scroller-r2/native-captures.json)、[XML与中断诊断](web-parity/message-scroller-r2/verification/)、[浅色Opening](previews/message_scroller_opening_light.png)、[深色Opening](previews/message_scroller_opening_dark.png)。
- 只定向record两张**新**Nova Tabs图，查看后独立verify通过；[新图hash](web-parity/message-scroller-r2/new-goldens.json)。旧Vega5图中2通过、3存在底部条带差异：states_light、states_dark、item_focused_light。分别恢复Tabs.kt/TabsStyles.kt到HEAD的隔离实验仍得到三个逐字节相同actual；当前源码已逐字节恢复，旧基线未刷新。保留[对比图/隔离结论](web-parity/message-scroller-r2/legacy-differences/isolation.json)，不把这三项报成绿色或本轮引入的回归。
- core/shared格式检查通过；默认两个detekt任务其实sourceCount=0/NO-SOURCE。只读task元数据确认真正覆盖本轮公共源码的是detektMetadataCommonMain，分别覆盖221/131文件，detektJvmMain反而只有2/3个平台文件。实际运行两个正确task **未通过**：core10731项，app26557项。当前涉及文件也有报告，没有把全体违规未经隔离就归为既有；不在UI配方迁移中清理全模块规则或改构建配置。[源码覆盖元数据](web-parity/message-scroller-r2/verification/detekt-source-metadata.json)、[core摘要](web-parity/message-scroller-r2/verification/core-detekt-summary.json)、[app摘要](web-parity/message-scroller-r2/verification/app-detekt-summary.json)、[完整压缩XML与hash](web-parity/message-scroller-r2/verification/detekt-reports.json)已保留，文档开头已更正历史验证口径。
- 最终JS132.5s、Wasm94.3s分别通过，见[本轮矩阵](web-parity/message-scroller-r2/parity-matrix.json)与[Gradle ledger](web-parity/message-scroller-r2/verification/gradle-ledger.json)。两目标分别单worker运行，未改项目堆/并行配置。
- 新鲜只读重查仍返回原Ego任务空间18不存在；没有另建空间或接管用户标签，重建请求仍待答复。本轮没有浏览器桌面/手机×浅深验收，见[browser-status](web-parity/message-scroller-r2/browser-status.json)。

仍余8配方：Default、Anchoring、Group Chat、Previous Context、Streaming、Load History、Animation、Visibility；下一轮按计划继续Visibility/Load History/Previous Context。已迁移的三个也仍需官网浏览器完整状态复核，Opening/Commands脚注的CSS text-balance目前仍为原生普通居中换行；Tabs的active shadow、hover/focus/文字透明度、variants/RTL/辅助技术、自定义Style等完整矩阵仍待独立页面核对。上一轮滚动原语遗留继续开放，其中官网仅在itemCount===0时排队未知ID，不能扩大为任意未知ID都排队。Message Scroller继续未勾选，Editor排除，Attachment暂缓；旧截图差异、Rhea/Nova目标区别、首屏和浏览器缺口均记录在[矩阵](web-parity/message-scroller-r2/parity-matrix.json)。此前全仓提交门禁遭自动审批因额度拒绝，未绕过hook，累计改动仍未commit。

### Message Scroller 第一轮：真实滚动控制器与 Nova 样式（2026-10-30，未完成）

<!-- progress-created: 2026-10-30; order: 85 -->

本轮没有把旧有的被动包装器或原生示例视为官网完整实现。固定上游 `96da9cc7f1aa10580a3741664e2a77c721206c00`，读取 Base 包装器、MDX、11个配方、`packages/react/src/message-scroller` 的实际控制器/命令/几何/状态与测试，以及 Nova/Rhea 独立 CSS。28个来源的 hash 已逐一验证，见[来源](web-parity/message-scroller-r1/sources.json)。页面使用 Rhea，而本库仍以 Nova 为主：Nova Content `gap-6`=24dp，Rhea `gap-8`=32dp；Root 的样式来自 TSX，不能因 CSS 没有 Root selector 推断其无样式。

本轮发现并修改：

- Provider 原来并未提供官网完整滚动能力。新增显式 State、一次性 Start/End/LastAnchor 开场、8dp边缘阈值、64dp前文预览；默认 `autoScroll=false`、开场 End。自动跟随在实际回到末端时重新启用，内容增长本身不被当作用户滚动。
- Provider 内 Root 改为 BoxScope 框架，Viewport 真正拥有原生滚动，Content/Item 可独立组合，浮动 Button 显式作为 Viewport 的兄弟。保留无 Provider 的旧 Root 自动 Column/按钮与像素阈值适配。新增真实 messageId/scrollAnchor 注册、单新回合顶端锚定和尾部占位；流式回复消耗占位，仅占位从正数降至0时转交末端跟随。
- suspend Start/End/Message 命令支持 Auto/Smooth、Start/Center/End/Nearest 与 Dp margin。未知/未挂载目标返回 false，目前还没有官网的待挂载ID队列。补上可滚动方向、当前回合和可见消息查询，排除前文 peek 和匿名ID；历史前插及上方行高度变化保持阅读位置，可关闭保留策略。
- Content/Item 测量回调移至调用方 padding 外侧，修复目标位置少算 padding。布局回调只写普通几何缓存，通过合并通道在下一帧协调状态。默认 Content 间距32→24dp；Nova浮动按钮28dp，使用16dp Lucide描边箭头；退场期间立即不可点击、不可聚焦并从可访问树隐藏，保留动态Style/主题缓存。
- 新增取消回归，先 RED 复现两处真实缺陷：UserInput 抢占布局补偿会取消整个 Root 控制器；等待 spacer 生效的跳转在指针按下后仍从0跳至536。修复后每次布局修正是结构化子任务，每条命令有调用方生命周期内的嵌套 job，按下/滚轮/键盘意图会取消待执行跳转。新命令覆盖旧命令时，旧取消清理不能清掉新命令的跟随状态。原生正在执行的 smooth-scroll 指针中断本来就通过，没有把它误报成修复项。继续复现初次 End 定位被 UserInput 中断后 `pendingScroll=true` 的问题（Viewport 因此持续 alpha0）；初次定位在 finally 清除此标记，中断后显示当前阅读位置，回归通过。
- 公共API变化升为0.3.13。迁移时 Root 的 ColumnScope→BoxScope；Provider 内必须使用 Viewport，并显式放置 MessageScrollerButton；需要旧/Rhea间距请显式传32.dp。目录主用法、七个API段落和一个可操作的 `Provider controls (Kotlin)` 示例已更新；原生示例保留为补充，不冒充官网配方。

验证与证据：

- 核心独立 verify56/56：25项实际行为、2张新Nova浅深截图、15项既有纯函数、14张旧截图。目录示例3/3，验证跳转、加载历史保持位置、自由阅读不被追加消息拉走和显式末端恢复。所属core/shared四项ktlint/detekt通过。[矩阵](web-parity/message-scroller-r1/parity-matrix.json)与[测试证据](web-parity/message-scroller-r1/verification/)分别记录覆盖范围，不能据此声称官网页面验收完成。
- 两张新Provider图片逐张查看，箭头纠正为描边后独立verify通过；[浅色预览](previews/message_scroller_provider_nova_light.png)、[深色预览](previews/message_scroller_provider_nova_dark.png)，新图片hash见[new-goldens](web-parity/message-scroller-r1/new-goldens.json)。
- 旧图最初5项不匹配均逐张查看：4张Content/Viewport是本次公开默认间距32→24dp的预期变化；另1张dark start-button边框在只恢复HEAD按钮函数的隔离实验中逐字节相同，是已存在差异。隔离后恢复当前源文件，仅对这5张进行定向record，然后14张旧图独立verify全部通过；保留[隔离方法/结果](web-parity/message-scroller-r1/legacy-differences/isolation.json)、原始actual/compare与XML，没有批量刷新其他组件截图。
- 本轮最终JS（134.7s）与Wasm（92.8s）分别通过，开场取消修复后core56/56、app3/3和所属四项质量检查均再次通过，见[验证矩阵](web-parity/message-scroller-r1/parity-matrix.json)与[Gradle ledger](web-parity/message-scroller-r1/verification/gradle-ledger.json)。两目标分开执行 `--no-parallel --max-workers=1`，没有修改项目堆或并行配置。
- 原浏览器任务空间18仍不存在：默认沙箱启动失败，正常桌面权限只读重查返回 `task space not found: 18`。未创建替代空间或接管用户标签，重建请求仍待答复；本轮没有桌面/手机×浅深浏览器验收，详见[browser-status](web-parity/message-scroller-r1/browser-status.json)。

剩余差异和下一步：官网 Default、Anchoring、Group chat、Previous context、Streaming、Opening position、Load history、Animation、Commands、Visibility、Scrollable 共11个配方的真实聊天数据、布局及交互还未替换到目录页，[后续逐配方计划](web-parity/message-scroller-r1/plan.md)列出迁移顺序和验收门槛；下一轮继续 Message Scroller，不能先当作已完成后转向 Navigation Menu。滚动原语仍缺未挂载ID命令队列、完整region/log/additions/busy辅助技术映射、细滚动条和stable gutter/overscroll/content-visibility、任意wrapper/多viewport/变换坐标/选区与重排矩阵、完整Button render/variant/size转发；其余键盘/RTL/主题/Style/字号及手势过渡验证见矩阵。Message Scroller保持未勾选，Editor排除，Attachment暂缓项保持。此前全仓pre-commit门禁遭自动审批因额度拒绝，未绕过；累计改动仍待可运行最终门禁后提交。

### Message 六配方、头像与元信息布局（2026-10-30）

<!-- progress-created: 2026-10-30; order: 84 -->

固定上游96da9cc的Base组件、MDX、六配方及Nova/Rhea Message规则已归档，[10份来源](web-parity/message-r1/sources.json)。两套Message CSS逐字相同；官网页面使用Rhea/theme-blue，本地六组配方使用Nova+Blue，并保留目录当前浅深色、基础色、圆角与字体。嵌套Bubble/Attachment/Button保持库内Nova样式，不伪称整个页面等同Rhea。

- Default、Avatar、Group、Header and Footer、Actions、Attachment六组按官网正文、消息顺序、分组与状态重建，保留7个Kotlin扩展示例。三张头像使用上游原始02/03/10 PNG；Workspace复用已有同URL、同SHA的打包资源。[4项素材来源](web-parity/message-r1/assets.json)。Attachment仅组合已有API，没有修改或重新展开用户暂缓的Attachment内部差异。
- Nova消息行/分组8dp间距、内容列10dp间距，普通文本14/20；avatar可省略，省略时不产生空节点或8dp空隙。MessageAvatar改为最小宽32dp、随内容测高并按最终shape裁剪，空头像仅占32dp宽、0高度；48dp头像可完整展示，字符串便利重载仍提供32dp头像。
- Footer存在时头像按源码固定向上平移32dp，多个Footer不重复累加；移除全部Footer还原。Header/Footer为12/16 Medium、muted色、水平12dp留白；同消息Ghost Bubble存在时清零留白。部件通过DisposableEffect注册/注销，未在composition或measurement中回写Snapshot状态；头像偏移在placement读取，ghost样式缓存包含部件与主题状态。动态ghost移除、footer从2→1→0、兄弟消息隔离和运行中浅深色测试通过。
- MessageContent新增可独立使用的modifier/style重载，同时保留旧RowScope扩展。Message root的Nova全宽走默认Style，调用方Style仍可覆盖宽度；内部列由Message持有。公共版本及安装文档同步0.3.12；补六部分API、正确导入与组合树。

上游存在矛盾：MDX明确Header无论align均靠start；CSS的content下直接data-slot子项self-end选择器也会选中Header。本轮Nova Header按MDX靠start，Footer按消息侧对齐；该项作为已说明差异留待浏览器自定义End Header核实，没有冒称CSS与文档完全一致。

当前[测试XML](web-parity/message-r1/test-results)：Message10项行为（含单独补跑的旧重载兼容项）、4张新增浅深/ghost图独立verify、7张旧Message图verify、Bubble7项回归及目录六项配方测试通过，共34项，来自独立定向运行。覆盖无头像间距、内在头像/空占位、动态Footer平移、ghost留白恢复与隔离、元信息排版与动态颜色、End Header/Footer位置、modifier/style宽度、RTL镜像、最终shape裁剪、原图与正文/反应/liveRegion、空头像分组文本对齐及操作按钮标签。旧RowScope额外兼容测试确认，新增无receiver重载没有吞掉原MessageContent尾lambda调用；240dp Row中尾部32dp元素仍完整占位，内容列保持weight分配。

四张新原图已目视检查后独立verify，[录图范围与hash](web-parity/message-r1/new-goldens.json)。本轮未重录旧Message/MessageScroller基准；它们在此前轮次已有未提交变化。已生成四张24dp边距/不透明背景的文档预览并更新元数据。[最终ledger](web-parity/message-r1/verification-ledger.json)：所属core/shared四个质量检查通过；JS和Wasm分别以`--no-parallel --max-workers=1`成功构建，保持原堆大小及项目配置，未出现上一轮并行内存失败。之后仅清理代码示例空白行尾空格并追加JVM兼容测试，最终shared JVM编译及四项质量检查仍通过，未无理由重跑截图/Web。保留已有AGP/Text内部API/Gradle警告，未因本轮没有os/path指纹就宣称历史诊断已修复。

[剩余矩阵](web-parity/message-r1/parity-matrix.json)保留：浏览器桌面/手机浅深对照、初次挂载/取消/重排的部件注册过渡帧、嵌套Message/portal/任意包裹层与CSS后代选择器差异、长连续文本与字体缩放、自定义style与footer/布局复杂状态、完整键盘/触摸/读屏。原生HTML title工具提示未重建。Copy/Like/Dislike/Retry/Download上游配方没有业务handler，本地同样展示有可访问名称的按钮，不声称实现剪贴板、反馈服务或PDF文件下载。Bubble及Attachment此前遗留保持原记录。

[浏览器状态](web-parity/message-r1/browser-status.json)本轮重新只读确认：原空间18不存在，只有用户持有的0/1/3空间；正常桌面权限重试也相同，没有新建、接管或导航。重建授权仍待答复。Message保持未勾选；Editor仍排除。此前全仓门禁的自动审批额度拒绝未绕过，累计改动暂未commit。

Message继续未勾选；下一项优先尚未开始完整对照的Message Scroller。

### Menubar 六配方、Nova 菜单与键盘导航（2026-10-30）

<!-- progress-created: 2026-10-30; order: 83 -->

读取固定上游96da9cc的Base组件、MDX、六个配方及Nova规则，[9份来源与校验](web-parity/menubar-r1/sources.json)。官网默认有File/Edit/View/Profiles四个菜单，本地原先缺Profiles与完整内容；本轮按Default、Checkbox、Radio、Submenu、With Icons、RTL六组重建并置于首位，保留39个Kotlin扩展示例。默认文件/编辑子菜单、禁用项、快捷键提示、View勾选与Profiles单选内容均按源配方补齐；上游未受控的勾选项及Radio/RTL真实setter示例使用本地remember，关闭重开后保留；literal checked=true及Default固定benoit/no setter保持只读，没有擅自变成可编辑。新增真实点击测试验证只读项不会变更。图标使用现有Lucide库，HelpCircle对应已安装库的CircleQuestionMark命名。

- Nova栏高32dp，圆角lg，边框内留白3dp，触发器之间2dp；触发器14/20 Medium，水平/垂直内边距6/2dp、实际高24dp，hover/展开为muted。测试实测根边缘到首触发器4dp（含1dp边框），没有凭观感放松尺寸。
- 每个MenubarMenu复用真实DropdownMenu root/content，保留同一anchor；菜单项获得Nova上下文、视口高度限制、滚动、方向键/Home/End/typeahead及子菜单行为。顶层左右方向键按LTR/RTL方向切换、跳过禁用项并循环，关闭态只移动焦点；菜单打开时横向切换另一个菜单。子菜单打开箭头先于顶层切换消费事件。Down打开首项、Up打开末项；Escape关闭并恢复原trigger焦点。
- MenubarItem补自由内容slot用于图标，MenubarContent补align，RTL示例按方向指定Start/End；语言选择器切换官网EN/AR/HE文案与方向，导出代码包含自己的translations而不依赖私有RtlPreview。新增16个API部分、usage导入和composition。公共API与安装文档同步0.3.11。
- Menubar选择标记与Dropdown不同：Menubar Checkbox/Radio用左侧16dp标记、6dp间隔、文字起点28dp；通过内部上下文只作用于Menubar两个别名，普通Dropdown继续右侧标记。Menubar行独立补md圆角，Label补14/20 Medium（普通Dropdown标签仍12/16）。同时修正共享Checkbox/Radio缺少标准toggleableState/selected语义的问题，保留原回调和文字描述；indeterminate也有标准状态。没有把测试改成只检查描述字符串。

[测试 XML](web-parity/menubar-r1/test-results)：核心9项行为+6张Nova浅深图独立verify+旧Menubar两图verify，合计17/17；目录最终7项配方测试+普通Dropdown三项选择状态回归，10项通过，总计27项（来自独立定向运行）。除尺寸/字体外覆盖方向键循环和禁用跳过、RTL左右方向、Home/End、Up/Down初始焦点、Escape返回、开菜单切换、子菜单按键优先级、内容slot、Label、28dp选择文字起点与indeterminate语义。回归测试验证普通Dropdown checkbox持久化、带图标通知独立勾选和付款方式单选。源码内置上下文默认关闭、仅Menubar别名启用，普通Dropdown保留原右侧分支；本轮没有另做Dropdown选择标记像素位置验收。

本次只录6张新Nova图，先检查原图后独立verify；菜单两图因md圆角修正做了定向更新，栏两图不重复录制。已有menubar_idle_dark在本轮开始前已是修改状态，本轮没有重录旧图。[新图范围与hash](web-parity/menubar-r1/new-goldens.json)。已生成六张不透明背景/24px边距的文档预览并更新元数据；弹层原图包含透明的1024×768测试画布，这些不是浏览器布局验收。

[最终ledger](web-parity/menubar-r1/verification-ledger.json)：core/shared四个质量门禁通过，最后只读配方修正后的shared两项检查与目录7项测试通过。最终并行Web复验曾出现JS GC overhead OOM及Wasm在未改动ToastTheme处的后端异常；改为分别以`--no-parallel --max-workers=1`构建后，Wasm及JS均exit0，未修改Toast、堆大小或项目配置。[构建诊断](web-parity/menubar-r1/build-diagnosis.json)保留并行内存压力限制，不声称默认并行已稳定或旧webpack os/path诊断已消失。[剩余矩阵](web-parity/menubar-r1/parity-matrix.json)仍记录：单Tab停靠/roving焦点、root disabled/orientation/loopFocus可配置项、动态重排与重复ID处理、快速切换关闭/指针焦点策略、共享192dp固定面板与源码min-width/内容宽度、完整100ms动效/变换原点/退出/透明表面、selection别名部分inset/leadingContent等API、原生menubar/menuitem角色与完整自定义slot/Style/触摸/读屏矩阵。继承的press motion仍需与源码无额外按压缩放的状态比较。[浏览器当前状态](web-parity/menubar-r1/browser-status.json)重新确认原空间18仍不存在，其他空间均由用户持有。桌面/手机浅深对照仍待重建授权，没有接管用户标签，Menubar保持未勾选。

Editor排除，Attachment遗留暂缓。下一项优先尚未开始完整对照的Message；此前全仓门禁被自动审批额度拒绝，未绕过依赖相同门禁的commit hook，本轮累计修改暂未提交。

### Marker 八配方与 Nova 基础样式（2026-10-30）

<!-- progress-created: 2026-10-30; order: 82 -->

官方 MDX 指定 base-rhea，但固定上游96da9cc的Nova和Rhea Marker规则逐字相同；本轮没有借此切换整个目录主题。[12份来源与校验](web-parity/marker-r1/sources.json)包括组件、MDX、八个配方、两套样式摘录。

- 本地原来以Today和重复Pinned messages演示为主，现按Default、Variants、Status、Shimmer、Separator、Border、With Icon、Links and Buttons八组重建。Default恢复分支、Thinking、压缩分隔、文件搜索四行；Border恢复三条实际文案与Lucide图标，core没有添加图标库依赖。示例导出包含独立导入和正文，不依赖私有辅助函数。
- Nova root补足全宽、最小高16dp、14/20字号/行高和muted前景；图标16×16dp并清除装饰子树语义。分隔线在原8dp间隔外补各4dp边距；底边1dp线内移半像素，完整落在组件范围内；Border底部8dp留白。旧非Nova排版与边框路径保留，调用方modifier与Style仍位于原根层并可覆盖尺寸。
- 状态通过polite liveRegion接入；Spinner在装饰icon槽中不会重复宣读Loading。Shimmer接入已有文本brush效果。链接实际调用UriHandler，按钮实际触发toast。竖排图标示例通过调用方Column组合；root仍为Row，不声称支持CSS任意render/flex-direction。

[测试 XML](web-parity/marker-r1/test-results)：6项核心行为、2张新增Nova浅深截图独立verify、5项配方测试通过。覆盖14/20排版、8dp图标间距、20dp实际行高/16dp空行最小高、Style尺寸覆盖、装饰语义、动态浅深颜色、完整1px底边与8dp底部留白，以及1px居中分隔线/12dp标签留白；实际链接跳转和按钮toast通过。两张新增图已目视检查并补文档预览，原有图未重录。

旧5张截图最终4通过、1张separator_dark changed。只临时恢复HEAD的Marker.kt与MarkerStyles.kt进行隔离：separator_dark在HEAD仍失败且actual PNG哈希逐字一致，确认为预存差异。border_light在HEAD通过，定位为本次对旧图标无条件居中引入回归，已改为仅Nova Center、其他预设保持TopStart；恢复当前源码后的独立旧图verify确认border_light通过。[隔离过程](web-parity/marker-r1/legacy-differences/head-isolation.json)明确排除了成功用例遗留的旧actual文件，未拿陈旧文件冒充复现。

[最终ledger](web-parity/marker-r1/verification-ledger.json)：core/shared四个ktlintCheck/detekt检查通过；JS/Wasm构建命令exit0，仍保留已有webpack os/path诊断，不声称无诊断构建。[剩余矩阵](web-parity/marker-r1/parity-matrix.json)明确保留浏览器桌面/手机浅深、完整键盘/触摸/读屏、hover/focus视觉、长文本与separator溢出、Style图标颜色继承及shimmer动画/减弱动态设置。链接使用官网绝对URL而非目录页内锚点，Compose原生没有Role.Link；上述适配不等同于HTML anchor语义。Marker保持未勾选。Editor仍排除，Attachment遗留仍暂缓。

Marker保持未勾选，下一项优先尚未开始完整对照的Menubar。浏览器工作空间重建仍等待答复；最终全仓门禁和依赖它的commit hook未绕过，累计修改暂未提交。

### Kbd 六配方、Nova 排版与 Tooltip 内容对比度（2026-10-30）

<!-- progress-created: 2026-10-30; order: 81 -->

按固定上游 96da9cc 对照 Base Kbd、Nova 样式、六个官方配方及 Tailwind preflight；[来源与校验](web-parity/kbd-r1/sources.json)。Default、Group、Button、Tooltip、Input Group、RTL 顺序与内容已补齐：实际内联段落中的 Ctrl+B/Ctrl+K、可输入搜索框、Save/Print 真实 Tooltip，以及 EN/AR/HE 方向切换。键帽仅展示快捷键，不安装全局快捷键处理器，与上游一致。

- Nova 键帽高/最小宽20dp，水平内边距与组间距4dp，字号/行高12/16、Medium，默认内联图标12dp；普通组分隔文字采用等宽字体，键帽使用主题 sans。颜色与样式缓存包含当前主题和 Tooltip 上下文，调用方 Style 仍最后覆盖。Nova 禁止文字选取，父按钮点击可穿透到实际处理器。
- Tooltip 只在实际内容区提供内部上下文：浅色键帽 background/20、深色 background/10，文字使用 background。覆盖真实 Tooltip 打开状态及运行中浅深切换，没有把 trigger 标记为 Tooltip 内容。
- 新增明确命名的 `Kbd(content = { ... })` 内容重载，保留旧 `Kbd { text }` String lambda。测试发现把新 slot 放最后会吞掉旧 String lambda、显示空键帽，已改为 content 必选首参并验证 Ctrl→Alt 更新。公共版本与安装文档同步0.3.10。

[验证 ledger](web-parity/kbd-r1/verification-ledger.json)、[测试 XML](web-parity/kbd-r1/test-results)：7项核心行为、4张新增 Nova 浅深/Tooltip 黄金图独立 verify、2张旧 Kbd 黄金图 verify、6项配方测试，共19项通过。行为覆盖几何、字体、图标、旧lambda、父按钮回调、调用方覆盖与真实Tooltip动态主题；单字符居中容许0.5像素Row整数取整，长Ctrl左右4dp内边距仍严格断言。四个所属模块ktlint/detekt通过；JS/Wasm命令exit0，但保留已有webpack os/path诊断，不声称无诊断构建。新增4张原图已目视检查，生成24px边距、不透明背景的文档预览并更新元数据。

旧 Tooltip 六张黄金图均 changed，未刷新。只临时回退此次 content provider 一行重跑六项，其实际 PNG 与当前实现六张 SHA256 全部一致；已恢复 provider。[隔离证据](web-parity/kbd-r1/tooltip-legacy-differences/provider-isolation.json)。因此这些差异不由本次 provider 包裹造成，留待 Tooltip 专项复核，不能声称整套 Tooltip 截图通过。

[剩余矩阵](web-parity/kbd-r1/parity-matrix.json)：浏览器桌面/手机浅深对照、内联段落响应换行/基线、平台等宽字体与官网 GeistMono 度量、完整选取/读屏/自定义交互slot/触摸状态仍待核验。浏览器工作空间重建仍等待答复，未接管用户标签。Kbd保持未勾选。下一项进入Marker；Editor排除、Attachment遗留暂缓。最终全仓门禁及依赖它的commit hook仍受此前自动审批额度拒绝限制，未绕过。

### Item 本轮源码、配方与布局复核（2026-10-30）

<!-- progress-created: 2026-10-30; order: 80 -->

固定上游Base组件、Nova CSS、MDX及11组配方共14份文件已保存[来源及校验值](web-parity/item-r1/sources.json)。未依据旧的“本地示例更丰富”说明直接判断等价；实际Default、Variant、Size、Icon、Avatar、Image、Group、Header、Link、Dropdown、RTL与旧目录重复参数示例差别明显。

修改：

- Nova三variant均保留1dp边框空间，Default/Muted为透明边框，Muted改为50%填色，圆角lg；Default/Sm为12/10dp内边距、10dp间距，Xs为10/8dp、8dp间距。链接行支持100ms hover背景过渡和keyboard focus ring。
- Nova根改用FlowRow且保留RowScope调用约定，Header/Footer可占完整独立行；非Nova保留原Row。ItemContent的4dp间距在Xs归零，新增fill=false表示不可伸展的尾列。标题14/19.25 Medium、单行ellipsis，描述14/21或Xs12/18、两行ellipsis；补AnnotatedString/textStyle支持，实际音乐标题可保留专辑的muted span。
- Icon媒体不再强制32dp底色徽章；Image随Default/Sm/Xs为40/32/24dp。描述按真实组合注册/移除，媒体在有描述时置顶并平移2dp，移除后恢复居中。组间距按成员尺寸自动16/10/8dp，暴露原生collection信息；Separator增加8dp上下间距。
- 新增href导航适配和显式onClick覆盖；Compose1.12.2没有公开Role.Link，href行保留通用clickable语义，不伪造内部Role值，也不宣称已等价HTML anchor。普通onClick仍为Button。API变更同步版本及安装文档0.3.9。
- 官网11配方、10个组成部分API、Composition、Item vs Field说明与Kotlin导出代码已更新；保留真实按钮、Dropdown复合Item、三语切换和3列图片Header。既有头像及[6张远程原图资源](web-parity/item-r1/assets.json)已打包，预览不依赖实时下载。音乐渐变接口实际返回PNG，按响应格式保存；模型图片为原JPEG。

验证与纠正：

- [最终ledger](web-parity/item-r1/verification-ledger.json)、[三份测试XML](web-parity/item-r1/test-results)。9项核心行为、2张新Nova独立verify、4张旧截图独立verify、8项配方共23项通过。覆盖完整行宽/换行、透明border布局、图片尺寸、描述动态媒体位移、Xs字号/间距、组尺寸间距与初始列表索引、固定时长列、导航/禁用、实际crop配置；配方覆盖真实资源、Header图片在正文上方、音乐行导航、Dropdown选择关闭及三语action顺序。
- 媒体第一次测得0dp，是testTag放在offset外层；改测实际子节点后2dp断言通过，未把期望改为0。文本getter第一次返回false，实际width124/maxLines1/Ellipsis/hasVisualOverflow均正确；源码证明JVM SkiaParagraph.isLineEllipsized固定返回false。[getter证据](web-parity/item-r1/ellipsis-getter-evidence.json)。新增窄标题/两行描述Nova截图，浅深原图人工查看，真实ellipsis可见，随后独立verify通过。
- 旧两张浅色基准直接通过，两张深色仅边框RGB38→34。保存旧图/实际/对比后，仅暂换Item.kt与ItemStyles.kt为HEAD，并暂移新API测试以允许旧源码编译；两张actual SHA与当前实现完全相同。[HEAD隔离复现](web-parity/item-r1/legacy-differences/head-reproduction.json)。当前源码/测试已恢复；依据已落地BaseColor.kt的dark White/10令牌，人工查看后仅定向刷新这两张深色基准，再独立verify全部六图。未刷新两张浅色旧图。
- 配方首次只有Invite选择器匹配两个合法按钮造成失败，已断言两者且分别检查enabled，未修改产品文案。四个所属模块ktlint/detekt通过；JS/Wasm distribution命令exit0，已有webpack os/path诊断继续留存。文档派生预览按既有24px边距/主题底色规则生成，元数据已更新；这些JVM图不等于浏览器整页验收。

保留差异：[矩阵](web-parity/item-r1/parity-matrix.json)。全部11组的浏览器桌面/手机浅深四象限仍未验证，尤其窄屏Header/头像响应式需真实画面；href通过平台UriHandler导航，Web默认_blank与官网同页/新页target及rel属性尚未完全等价，原生整行Link角色亦未解决；相邻内容的CSS自动flex-none在Kotlin需显式fill=false，Xs在Dropdown中自动p-0尚未实现，当前配方显式8dp覆盖。平衡/pretty换行、Next图片优化像素、嵌套组选择器、keyed重排的collection索引、完整hover/focus/pressed/disabled/动态主题和任意Style约束继续待验。

Item保持未勾选，浏览器工作空间重建仍等待用户答复；没有接管用户标签或新建替代空间。下一项优先尚未开始本轮核对的Kbd。Editor仍排除，Attachment遗留仍按用户要求暂缓。累计修改尚未提交：此前最终全仓门禁被自动审批因额度拒绝，实际pre-commit仍要求同一组全仓检查，未绕过。

### Hover Card 本轮验证结果（2026-10-30）

<!-- progress-created: 2026-10-30; order: 79 -->

最终[ledger](web-parity/hover-card-r1/verification-ledger.json)及[三份XML](web-parity/hover-card-r1/test-results)已保存。12项核心行为全部通过，覆盖600/300默认定时、trigger优先级、延迟期间最新callback、受控拒绝不反复回调、hover排除root其他子区域、触摸/disabled、keyboard focus与Escape不抢焦点、进入面板取消关闭计时、离开后关闭，以及Nova256×40/14×20和RTL物理/逻辑side映射。跨面板测试第一次在挂载前断言，补50ms挂载帧后保留500ms进入/350ms离开断言，通过后再全量复验。

- 两张新增Nova浅深图定向记录并人工查看，随后独立verify通过；旧两张trigger基准也独立verify通过，没有刷新旧基准。截图夹具使用官网semibold标题及12/16 muted日期。原始Popup截图含透明大画布且靠窗口左边界，不代表整页或最终浏览器对齐；文档派生图只按既有规则铺主题底色、增加24px边距，不裁剪/缩放。
- 截图检查时发现样式适配层会用最后一次dropShadow替换整个列表，原连续调用只留下ring；已改为单次vararg传入全部阴影。原生PNG底边外采样alpha逐渐24→18→9→4→0，证明阴影确实绘制；不把此探针等同于官网像素匹配。[探针](web-parity/hover-card-r1/shadow-alpha-probes.json)。
- 5项目录配方测试通过，涵盖真实@nextjs文案、四个物理side的独立内容、三语商品/价格及两个逻辑side、受控预览状态、官方章节/API/导出独立性。四个所属模块ktlint/detekt通过；JS/Wasm distribution命令exit0，但仍捕获已有webpack os/path诊断，未声称无诊断构建。

仍未完成：[矩阵](web-parity/hover-card-r1/parity-matrix.json)记录安全多边形指针通道/文本选取、多个trigger/handle、非触发器焦点的全局Escape、root额外布局anchor、RTL非居中align/碰撞、按实际翻转side选择动画原点、focus blur独立关闭时机及完整读屏/动态主题/自定义Style矩阵。本地focus blur当前沿用hover closeDelay，而Base UI有独立立即关闭路径。浏览器任务空间重建仍等待用户答复，本轮没有桌面/手机浅深视觉验收。Hover Card保持未勾选；下一项优先尚未开始本轮复核的Item。Editor仍排除，Attachment遗留仍暂缓。

提交状态保持：实际pre-commit会运行全仓ktlintFormat/ktlintCheck/detekt；此前该最终全仓操作已被自动审批因额度拒绝，本轮只复核hook内容，未绕过、未触发被拒操作，累计修改尚未提交。

### Hover Card 本轮复核计划与发现（2026-10-30，进行中）

<!-- progress-created: 2026-10-30; order: 78 -->

固定上游Base/Nova和官网配方见[来源](web-parity/hover-card-r1/sources.json)。实际依赖为Base UI1.6.0，已读取并保存该版本Trigger/Root/Popup及focus、dismiss实现；没有将master分支的实现作为固定版本依据。

本轮计划：先修核心trigger定时/焦点/样式，补Default、Basic、Sides、RTL预览及导出代码，再运行定向行为与浅深Nova截图，并在浏览器恢复后检查桌面/手机四象限。

发现与修改：

- Base UI默认delay/closeDelay为600/300ms；官网Default/Basic显式10/100，Sides100/100，RTL10/100。本地旧默认0/100且作用在整个root，已将hover监听移到真实trigger，新增trigger级参数优先于root/provider别名。
- 增加keyboard模式聚焦打开和触发器焦点下Escape关闭；延迟回调通过rememberUpdatedState读取最新值。初始defaultOpen/受控open不再因初始idle分支自动关闭。默认外部点击关闭改为Base UI useDismiss的true。
- Nova面板为256dp、10dp内边距、lg圆角、外描foreground/10 ring和分层shadow；没有占据布局的1dp边框。明确内容14/20和popover前景色，并补100ms淡入/缩放/按side滑入。物理Left/Top/Bottom/Right及随布局方向变化的InlineStart/InlineEnd经HoverCardSide暴露，旧PopupPlacement重载保留。公共API版本及安装文档更新0.3.8。
- 官网默认示例实际没有头像，是Hover Here链接样式按钮和@nextjs文本预览；已改正原本额外头像及错误文案。四组官网配方、Trigger Delays/Positioning说明、Composition和三个官方API部分已补，保留三组独立Kotlin扩展示例。
- 首次行为测试存在输入夹具问题：enter需同时发moveTo，触摸用例需显式Touch模式；修正后触摸通过，但鼠标仍全部不触发。进一步定位到hover委托只在SideEffect读取，组合未订阅状态；已改为组合阶段读取Boolean，当前正复验。保存[RED测试](web-parity/hover-card-r1/test-results)，不因错误归因降低断言。

明确待验/保留：安全多边形指针通道与文本选取保持打开、多个trigger/handle、非触发器焦点时的全局Escape、root额外布局内容下的实际anchor、RTL非居中align及碰撞边界、实际翻转side对应的动画原点、完整触摸/读屏/任意Style与动态主题矩阵。浏览器空间恢复仍等待用户答复，尚无本轮四象限视觉验收；Hover Card保持未勾选。

### Empty 七配方、链接与媒体补齐（2026-10-30）

<!-- progress-created: 2026-10-30; order: 77 -->

- Avatar、Avatar Group、InputGroup、RTL 四组预览和导出代码补齐，前七组顺序与官网相同。头像使用已有打包资源，48dp并去色；组通过真实AvatarGroup提供-8dp重叠和外描2dp背景环，没有用内边框缩小照片。InputGroup保留本地输入状态，说明通过AnnotatedString呈现可点击的Contact support链接。RTL通过目录语言选择器切换官网英/阿/希文案和布局方向，导出代码不依赖私有RtlPreview。
- EmptyDescription新增AnnotatedString重载，与普通文字共享排版；公共API版本与安装文档同步0.3.7。补齐六个组成部分的API表、导入代码和Composition。
- 首三组及RTL的媒体替换为Tabler SVG原始路径，不再使用Lucide近似图案；使用v3.34.1（官网依赖声明^3.31.0范围内）并保留来源校验与MIT许可，core没有增加图标库。Learn More接入UriHandler和muted色；图标继承按钮/附加区的前景色，修正主按钮内图标仍为普通foreground的问题。Outline预览按768dp断点采用桌面40dp、手机24dp外部留白。这些修改是源码及测试层面完成，尚无本轮浏览器视觉验收。

验证：[最终ledger](web-parity/empty-r1/r2-verification-ledger.json)、[测试XML](web-parity/empty-r1/test-results)。6项配方测试覆盖七组顺序、48dp实际头像及灰度像素、头像组40dp步进、编辑/清空搜索、支持链接点击、Learn More跳转和三语方向/顺序；6项核心测试（包括Nova浅深两图独立verify）通过。本轮未重录任何基准。四个所属模块质量门禁通过；最后一次仅改app后，重跑了app六项测试、shared质量门禁及JS/Wasm构建，没有无理由重复core。Web构建exit0，仍捕获已有webpack os/path诊断，不声称无诊断构建。

保留差异：[矩阵](web-parity/empty-r1/parity-matrix.json)已更新。全部七组的浏览器桌面/手机浅深矩阵、平衡/pretty换行、完整键盘焦点/读屏、任意Style/shape覆盖及大内容布局仍未关闭；标题heading和Learn More按钮语义与官网div/anchor有差别。旧16项截图仍保留上一轮HEAD复现结论（12changed、4missing），未自动刷新。浏览器空间重建问题仍等待用户答复，没有接管用户标签或另开空间。Empty继续未勾选。累计工作未提交：此前全仓最终门禁因自动审批额度被拒，未绕过commit hook。

### Empty 本轮源码与前三组配方（2026-10-30，进行中）

<!-- progress-created: 2026-10-30; order: 76 -->

读取固定上游 `96da9cc7f1aa10580a3741664e2a77c721206c00` 的 Base Empty、Nova CSS、MDX 与七组配方；十份源文件保存校验值，[来源](web-parity/empty-r1/sources.json)。实际官网配方为 Default、Outline、Background、Avatar、Avatar Group、InputGroup、RTL；本地旧列表存在大量仅重复默认参数的示例，不能算已覆盖官网。

已修改：

- Nova 默认不画可见边框；Outline 增加虚线外框和1dp边框空间，非Nova旧边框策略保持。第一轮新增截图暴露虚线在内边距以内穿过标题，已将Nova绘制顺序移到Style之前，最终浅深截图已查看。
- Nova Header/Content 默认 max-width 384dp，Content填满可用宽度；用缓存Style实现，调用方Style仍可覆盖。显式根高度中内容垂直居中。标题由实际12px改为14/20，tracking从错误的-0.025sp改为-0.35sp；描述使用14/22.75并保留完整行盒。
- 目录首三组改为官网实际项目空态、Cloud Storage Empty虚线轮廓及通知背景配方，预览和导出代码同步，增加Composition。其余四组暂未移植，不能把删除重复参数示例理解为全页完成。[配方矩阵](web-parity/empty-r1/parity-matrix.json)。

验证：四项行为测试已通过，覆盖384dp上限、调用方覆盖为480dp、标题及描述排版、固定高度垂直居中。新增Nova浅深两张基准已定向记录并查看；独立verify和所属模块质量门禁结果待追加。初次编译的Text调用、预览参数名、无效文档字段及rememberTheme receiver用法错误均已修正，未因这些错误放宽断言。

浏览器本轮未执行成功：原Ego工作空间18已不存在，listTaskSpaces仅返回用户所有的新标签页。按ego-browser运行指南禁止通过新建空间绕过丢失状态的规定，已请求用户允许重建，等待答复；未接管用户标签页，也未把历史图算作本轮证据。

明确保留：四组剩余配方、Tabler媒体图案（当前前三组暂用Lucide近似）、Learn More链接行为与muted色、Outline桌面40/手机24dp预览inset、平衡换行、完整Style/radius覆盖，以及全部浏览器四象限和交互检查。Empty保持未勾选；Editor继续排除，Attachment继续按用户要求暂缓。

最终验证补充：6项EmptyParityTest通过（4项行为、2张Nova独立verify），四个所属模块ktlint/detekt及shared JVM编译通过。[ledger](web-parity/empty-r1/verification-ledger.json)、[当前实现XML](web-parity/empty-r1/test-results)。旧EmptyScreenshotTest的16项未通过：12changed、4missing；修正本轮意外影响的nonNova字号/行盒后仍相同。仅临时替换Empty.kt为HEAD原实现复现，16张实际PNG的SHA全部与当前实现相同；恢复当前源码后保留该缺口，未重录旧图。[HEAD隔离复现](web-parity/empty-r1/legacy-differences/head-reproduction.json)。因此不能声称Empty整套截图通过。新增两张Nova文档预览按现有规则补24px边距，元数据已生成。

### Dropdown Menu 七组图标与复杂配方（2026-10-30）

<!-- progress-created: 2026-10-30; order: 75 -->

在前六组基础配方上补齐 Icons、Checkboxes Icons、Radio Icons、Destructive、Avatar、Complex、RTL；页面前13组按官网顺序排列，保留八组有独立行为价值的 Kotlin 扩展示例。描述、Composition、导入代码与 API 表已整理；RTL 导出示例不依赖目录私有预览包装器，头像使用已有打包资源。

- CheckboxItem、RadioItem、SubTrigger 新增可空 leadingContent 槽位，core 不引入图标集；有图标的行仍由真实菜单项承担状态、点击与键盘行为。公共 API 版本及安装示例同步 0.3.6。
- 根菜单及子菜单增加可用窗口高度限制和内部滚动。Complex 覆盖多层最近项目、主题单选、通知复选及共享 Sidebar/Email 状态；RTL 提供英/阿/希三语配方及方向切换。三语完整浏览器交互仍待验，不因配方存在而标记通过。
- [最终测试与质量 ledger](web-parity/dropdown-menu-r3/verification-ledger.json)：13项目录配方测试、Dropdown/Context Menu/Menubar 的8项截图独立 verify 全部通过，本轮未重录基准；四个所属模块 ktlint/detekt 通过。JS/Wasm distribution exit 0，已有 webpack os/path 诊断继续保留。[四份测试 XML](web-parity/dropdown-menu-r3/test-results)。
- 官网七组配方四象限28张展开截图已留存；本地同范围28次展开和 Escape 关闭检查全部通过，真实画面关闭后与各自基准像素差均为0。[官网记录](web-parity/dropdown-menu-r3/official-browser.json)、[本地记录](web-parity/dropdown-menu-r3/local-browser.json)。检查了桌面 Avatar、Complex、Radio Icons、RTL 和手机 Complex/RTL 等代表性原图；不声称其余截图已逐张人工复核。Avatar 的 Notifications 未发生换行。
- 手机 Complex 的整屏高菜单初始看不到 Sign Out，菜单内滚动后可见，截图见 dropdown-menu-r3/local-phone-light-complex-scrolled.png。页面自身与官网的标题/段落滚动位置不同，导致本地菜单可用空间较小；不能把此画面说成官网等高或全部内容初始可见。

Dropdown Menu 继续未勾选。剩余包括默认 anchor/min-width 和子菜单固有宽度、指针打开首行额外聚焦高亮、ring/阴影几何、完整键盘及受控拒绝矩阵、短窗口/真实触摸、读屏状态与 Web 关闭后可访问树残留。此前全仓最终门禁被自动审批系统因额度拒绝，本轮未绕过或跳过 commit hook，累计修改尚未提交。Ego Lite 提示可更新，未擅自升级。

手机 RTL 滚动已单独复验，底部“تسجيل الخروج”可见。首次误点滚动了页面本身，已排除；重定位并查看触发器画面后再执行菜单内滚动，详见 [长菜单记录](web-parity/dropdown-menu-r3/long-menu-scroll.json)。下一项优先尚未开始本轮复核的 Empty；Dropdown 遗留保留，Attachment 仍按用户要求暂缓。

### Dropdown Menu 浏览器判断修正与禁用色（2026-10-30）

<!-- progress-created: 2026-10-30; order: 74 -->

本轮纠正了上一轮“Web 无法关闭”的判断。临时回调日志显示外部关闭确实将受控 open 写为 false，动画状态也落定为 `(false, false, true)`；但 Canvas 的旧可访问节点仍保留在 DOM，原来等待节点删除的条件不成立。上一轮手机 Profile 选择后的截图本身已经显示菜单关闭，不能把等待条件超时解释为组件关闭失败。[原始回调记录](web-parity/dropdown-menu-r1/r2-callback-trace.json)、[动画观测](web-parity/dropdown-menu-r1/r2-transition-state.json)。临时日志已全部移除并重建干净 Web 预览，没有为了这个误判修改 Popup 动画或受控状态实现。

- 六个基础配方各完成桌面 1440×1000、手机 390×844、浅深色共 24 个稳定展开与 Escape 关闭检查；先把鼠标移到触发器、等待悬浮样式稳定，再记录关闭基准，避免把代码按钮的悬浮变化算成菜单关闭差异。菜单区域关闭后与基准像素差全部为 0。已逐张查看四张组件对照拼图；手机小标题比桌面高 8px，裁剪坐标已据实修正。[24 项记录](web-parity/dropdown-menu-r1/r2-six-browser.json)。
- 默认菜单另验证四象限的外部点击及 Profile 选择关闭，八次关闭画面差也全部为 0；同时每次 DOM 仍有一个旧 Profile 节点，直接证明原有存在性判断不可靠。[关闭交互记录](web-parity/dropdown-menu-r1/r2-dismiss-browser.json)。这些检查证明真实 Canvas 关闭行为，不代表可访问树/读屏同步问题已修复。
- 对照图发现 Nova 禁用 API 文字先变为 muted，再经行 opacity 变淡，形成重复变淡。新增用例先 RED（实际基础色 #737373，对照正常项 #0A0A0A），再修正 Nova 普通菜单项保持基础前景色，仅经既有行 opacity 变淡。非 Nova 的原行为保留。浅深两张 Nova 基准定向记录、人工检查，Default/Basic 的八个受影响浏览器场景重新采集并复验关闭；其余 16 个未受影响场景沿用前一轮证据。[RED XML](web-parity/dropdown-menu-r1/test-results/r2-disabled-color-red.xml)、[八项复验](web-parity/dropdown-menu-r1/r2-disabled-color-browser.json)。
- 最终 6 项配方测试、Dropdown/Context Menu/Menubar 共 8 项截图独立 verify 通过，四个所属模块 ktlint/detekt 检查通过；JS/Wasm distribution exit 0，仍记录已有 webpack os/path 字样。[最终验证 ledger](web-parity/dropdown-menu-r1/r2-verification-ledger.json)。

仍未完成的是另外七个官网配方、默认宽度/可用高度、复杂嵌套和完整键盘/受控状态、指针开启时首行额外聚焦高亮、阴影/ring 几何及 Web 可访问树残留同步。下一步补 Icons、Checkboxes Icons、Radio Icons、Destructive、Avatar、Complex、RTL；Dropdown Menu 继续保持未勾选，Editor 仍排除，Attachment 遗留仍按用户要求后续集中处理。

### Dropdown Menu 首轮基础配方（2026-10-30，进行中）

<!-- progress-created: 2026-10-30; order: 73 -->

已读取固定上游 `96da9cc7f1aa10580a3741664e2a77c721206c00` 的 Base UI 组件、Nova CSS、MDX 和官网全部 13 个配方，保存[源码与校验值](web-parity/dropdown-menu-r1/sources.json)。本次先处理 Default、Basic、Submenu、Shortcuts、Checkboxes、Radio Group；其余 7 个不是已完成项，见[配方矩阵](web-parity/dropdown-menu-r1/parity-matrix.json)。

已修改：

- 首屏改为官网完整账户菜单：快捷键、Invite users 子菜单、GitHub/Support、禁用 API 与普通 Log out；触发器改为 Outline。新增 Basic，并同步六个配方的预览与导出代码、分组、宽度和初始状态。Submenu 包含 More options 下的第二层；Checkboxes 包含禁用 Activity Bar、可切换 Panel；Radio 默认 Bottom。
- 原鼠标两层子菜单测试持续无法 idle。两份线程样本证明在重复创建/绘制 Popup，未把 FontMgr 栈误判为永久原生阻塞。修复 hover 打开的子弹层抢焦点造成的开关循环：hover 保持非聚焦，ArrowRight 再提升为键盘聚焦；打开的后代通过稳定 token 向祖先登记，避免跨入第三层时祖先提前关闭。保留原鼠标测试，并增加完整两层键盘进入和选取测试，两条路径均通过。
- Nova 分隔线补齐官网 my-1/-mx-1 的 4dp 竖向间距和横向出血；Context Menu 别名去掉原来的额外间距，避免重复叠加。Nova 菜单字体使用不裁剪的 20px/16px 行盒，基础行实测恢复 28dp。快捷键从误用 CommandShortcut 改为官网原字符的 12/16 文本与 tracking，保留 ⌘B、⌘+T 的文本语义。

验证与证据：

- 5 项配方测试通过：禁用项/选择关闭、28dp 行高、复选状态跨关闭保留、鼠标两层选择关闭整树、键盘两层选择关闭整树。Label 的文字语义边界是 16dp，并不包含上下 padding；已据此修正测试，没有为了 24dp 断言放大文字。
- Dropdown Menu 4 张、Context Menu 2 张、Menubar 2 张共 8 张截图独立 verify 通过；其中新增 Nova 浅/深两张，已人工查看。三个旧 Vega 基准先失败，保留旧图/实际图/对比图后，临时仅将三份菜单源码换回 HEAD 复现，实际 PNG SHA 全部与当前修改相同；恢复当前代码后才定向刷新这三张旧基准。[HEAD 复现](web-parity/dropdown-menu-r1/legacy-differences/head-reproduction.json)。
- 四个所属模块 ktlint/detekt 门禁及 JS/Wasm distribution 命令 exit 0；仍有已有 webpack os/path resolve 字样，未声称无诊断构建。[验证 ledger](web-parity/dropdown-menu-r1/verification-ledger.json)，[测试 XML](web-parity/dropdown-menu-r1/test-results)。
- 官网和本地六个基础配方均已采集桌面/手机、浅/深色展开态。此前用残留的 Web 可访问节点是否存在来判定关闭，得出了错误的“无法关闭”结论；本轮已纠正。临时回调和动画状态观测确认控制状态更新、动画落定；关闭后的真实画面已核实。正式干净构建下完成 24 个展开/Escape 视觉检查，以及默认菜单四象限的外部点击、Profile 选择关闭检查，关闭区域与原基准像素差均为 0。[六配方记录](web-parity/dropdown-menu-r1/r2-six-browser.json)，[外部/选择记录](web-parity/dropdown-menu-r1/r2-dismiss-browser.json)。可访问树仍保留旧 Profile 节点，读屏同步问题保留为待查。

继续保留并优先处理：

1. Icons、Checkboxes Icons、Radio Icons、Destructive、Avatar、Complex、RTL 七个官网配方尚未移植。本地旧参数示例很多，页面章节/顺序、Composition/API 与官网还未完整整理。
2. 已确认的 Web 基础关闭路径见上述记录；两层鼠标/键盘、焦点返回、hover 转键盘、Enter/Space 开启、后退键、受控状态拒绝和外部主动打开等路径仍需完整覆盖。Web 关闭后的可访问树残留节点及读屏同步仍待排查。
3. 菜单默认 anchor-width/min-width、子菜单默认最小宽、可用高度/滚动、读屏 menu/menuitem 与 checkbox/radio 状态语义仍待核对。本轮六个配方显式设置官网宽度，不代表核心默认宽度已对齐。
4. Nova border 与官网外描 ring 的布局差别、阴影强度、指针打开时首行被自动聚焦/高亮（官网未高亮）及完整 hover/disabled/focus/RTL 图标状态仍需进一步核对；全站 P1/P3 的位置、滚动条、字体和代码展示差异继续保留。

Dropdown Menu 保持未勾选。本轮未绕过此前被自动审批拒绝的最终全仓门禁和 commit hook，因此累计修改尚未 commit。

### Carousel 首轮页面、配方与行为对照（2026-10-30）

<!-- progress-created: 2026-10-30; order: 72 -->

按固定上游 `96da9cc7f1aa10580a3741664e2a77c721206c00` 的 Base UI / Nova 读取组件、MDX 与 Default、Sizes、Spacing、Orientation、API、Plugins、RTL 七个示例；另读取 Embla Autoplay v8.6.0 的 stop/reset 实现。[来源和校验值](web-parity/carousel-r1/sources.json)。

已修改：

- 新增 `CarouselRoot` 为内容及外置控件提供统一 state、orientation、Carousel 语义和键盘处理；`CarouselContent` 可继承状态。新增原生 Pager 的 PageSize、pageSpacing、userScrollEnabled 参数；公共 API 版本同步升至 0.3.5。
- 上/下一页按实际 canScrollBackward/Forward 禁用，修复一屏多卡到末尾仍可继续点击的问题；快速导航以 targetPage 为基准。Nova 控件改为 28dp 圆形 Chevron，继承竖向/RTL 方向，其余 preset 保留原尺寸。
- 七个官网配方与导出 Kotlin 示例同步，增加响应式尺寸、间距、多卡、竖向、页码、两秒自动播放和 RTL；保留额外 dots 配方。API 使用 settledPage 显示稳定页码。自动播放在 hover/drag 后停止；官网示例的 stop 后 reset 不会重新激活，已按真实源码处理。
- 为卡片显式应用官网 py-4 与内容方形布局，使数字在完整卡片中居中；修正 RTL 工具栏占位挤压展示区的问题。Card 的全局 padding 差异留给 Card 专项，不扩大本次修改范围。

验证与证据：

- 官网七个配方及本地八个配方，分别采集 1440×1000 桌面、390×844 手机，浅/深色，DPR 2；已逐张检查四组对照拼图，另检查最终 Nova 默认浅色/竖向深色原始基准。[浏览器记录](web-parity/carousel-r1/local-browser.json)。拼图用于比对组件，裁剪区域和段落长度不同，不代表整页像素一致。
- 浏览器实测 Next 从 1 到 2、ArrowRight 从 2 到 3；鼠标拖动后仍为 3，未通过。Compose foundation 1.12.2 `AbstractScrollableNode.CanDragCalculation` 明确过滤 PointerType.Mouse，已定位原生 Pager 的限制。桌面页面注入 touch 也没有翻页，不能据此声称手机真实触摸通过。[交互记录](web-parity/carousel-r1/browser-interaction.json)。
- 5 项核心行为、6 项配方、12 项截图共 23 项定向测试通过，截图最后以 verify 模式独立验证；四个所属模块 ktlint/detekt 检查通过。[测试 XML](web-parity/carousel-r1/test-results)，[实现 ledger](web-parity/carousel-r1/implementation-ledger.json)，[质量 ledger](web-parity/carousel-r1/quality-ledger.json)。JS 与 Wasm distribution 构建任务 exit 0，但 ledger 仍捕获已有 webpack path resolve 字样，不能把 exit 0 解释为无诊断构建。
- 四张旧 Vega 基准出现差异：只将导航函数临时替换为 HEAD 原函数后重跑，四张实际 PNG 与当前实现的 SHA 均相同，证明这部分导航差异已存在；保留旧图、对比图与复现记录后定向刷新。八张新增 Nova 基准单独记录，全部独立验证通过。[旧基准复现](web-parity/carousel-r1/legacy-differences/head-reproduction.json)。

明确保留：

1. 鼠标拖动尚不等价于 Embla；真实触摸、拖动暂停自动播放、快速/反向手势与滚轮边界仍需完整矩阵。
2. Embla loop、align、containScroll、opts 和完整 plugins API 尚未移植；PageSize/spacing 只覆盖本轮配方。原生 Pager 需要有界 viewport，动态高度、任意内容/Style 覆盖仍待核对。
3. native pageCount 是 item 数，Embla scrollSnapList 可能小于 item 数；API 当前在落定时更新，与官网 select 时机不同。完整事件接口尚未等价。
4. RTL 已测 Arabic 导航方向并修正布局；三语切换、全部键盘焦点/读屏、语义和用户拒绝受控回调等完整状态未验。后台页面暂停、reduced motion 等 autoplay 生命周期也未完整验证。
5. 全站 P1/P3 的横向偏移、375/390 client width、标题/段落/代码区域、字体栅格等差异仍保留；旧两张仅浅色 Vega 特例也未补深色。

Carousel 保持未勾选，下一项优先 Dropdown Menu。最终全仓门禁此前被自动审批额度拒绝，本次未绕过，也未跳过 commit hook，因此累计修改尚未提交。

### Calendar 首轮公历对照（2026-10-30）

<!-- progress-created: 2026-10-30; order: 71 -->

按“尚未开始本轮页面与完整状态对照”进入 Calendar；Attachment 暂缓项未重新展开。官方页面本轮明确指定 base-nova，不沿用 Bubble 页的 Rhea 例外。[来源与固定 revision](web-parity/calendar-r1/sources.json)、MDX、组件、12 个官方示例源文件和 Nova Calendar 样式已归档。

已修改：

- 将原来重复展示默认参数的目录页改为 15 组有意义的配方：Default、Basic、Range Calendar、Month and Year Selector、Presets、Date and Time Picker、Booked dates、Custom Cell Size、Week Numbers、RTL，加 Multiple、Disabled、Monday、Hide outside days、Month bounds。补 Composition、四组 API、时区说明及键盘指南；复杂配方的状态、价格格、已订日期、预设和时间 footer 代码已实际导出，未保留“见目录实现”的占位源码。
- Default 使用系统时区的当前公历日期和 dropdown caption；Basic 无初始选择，Range 为当年 1 月 12 日至 2 月 11 日并显示两月。日期预设更新选择及可见月份；已订日期为当年 1 月 12–26 日，禁用、灰色并带删除线；价格格使用工作日 $100、周末 $120。保留单选点击取消的目录行为。
- Nova 默认 28dp 日格，星期 12.8sp / 22.4sp、标题与表格间距 16dp、每周上方/周间 8dp；默认改为可变周数，显式 fixedWeeks 仍可固定六周。自定义 cellSize 作用于日期、导航、表头和周数栏；周数显示两位数。多月在小于 768dp 的窗口纵向排列。
- 移除仅因“已选中”就显示的初始 focus ring，跟随实际焦点；日期键盘处理限定在日按钮，避免拦截月份按钮或 caption 的 Enter。原生日按钮可跳过禁用日期、跨月保留真实焦点；月份边界也约束键盘移动。dropdown 默认年份范围从前后十年改为锚定年份前 100 年至该年，默认年底禁止继续向后导航。
- Calendar 按自然内容宽度布局，窄父容器不再将两位日期折成两行；保留官网大格在窄 Card 中被裁切的现象。Card 使用已测量的自然宽度：时间 220dp、已订 212dp、周数 240dp、大格 7×cellSize+16dp，预设最大 300dp；修复目录 Card 误撑到 500dp。预设按钮使用现有 Button 的 fillWidth Style 绘制等宽背景。
- 核心增加 cellSize/showWeekNumber、CalendarGrid 自定义 dayStyle/dayContent 及 CalendarDayButton modifier/style/content，版本及安装示例同步 0.3.4。保持 core 无新日期库、Material 或图标集依赖。

验证：

- 最终 15 项功能/目录测试通过：核心 9、目录 6；覆盖尺寸/可变周、窄屏日期不换行、周数跨年、月份 Enter、禁用跳过/跨月焦点、日期槽位、百年范围与边界、未传 today 时范围保持锚定、真实预设、独立时间编辑、已订禁用、价格范围选择和配方导出。此前现有 Chinese/RTL、月份范围和 Nova 选择/尺寸回归也已定向通过，未将其扩大为全仓测试通过。
- 只记录本轮影响的 Calendar 10 张及 DatePicker 两张基准，旧图保存在 baselines-before；检查对照图后独立 verify 共 12 张通过。周数补零后只重录对应两张并再 verify 全部 12 张。同步派生 docs/previews 和 metadata；也补齐上轮 Bubble 及直接依赖的派生预览，共 28 张，未重录其他组件。
- 官方十组公历示例 × 桌面/手机 × 浅深色共 40 张，本地十五组共 60 张首屏截图，四张对照图已检查；本地主题以背景像素逐张验证。视口为 1440×1000、390×844，DPR 2。另存浅色桌面/手机 Range/Presets/Time/Custom 下半段八张，最后预设按钮背景已单独补验浅深色桌面/手机；实际月份切换至 November、鼠标选日后 ArrowRight+Enter 选中 16 日、月份菜单打开/Esc 关闭已检查。首屏和浅色补验不替代全部交互四象限。
- JS/Wasm development distribution 成功；core/catalog 所属模块 ktlintCheck/detekt 均通过，最后预设绘制调整后 catalog 两项再次通过。保留原有 Text 内部 API、AGP/Gradle 及可选 os/path Webpack 警告。汇总见 [evidence.json](web-parity/calendar-r1/evidence.json)。

明确保留：

1. 波斯历/Jalali 需要独立历法引擎，当前 CalendarDate 为公历；已在页面和文档明确解释，未用翻译月份冒充实现。官网该示例为自带 32px 日期格的独立组件，仍需后续移植与日期换算验证。
2. 时间字段目前为两个独立可编辑文本，不具备 HTML time 的分段、原生键盘操作及格式/范围校验；日期与时区转换已沿用既有 app helper，不代表所有历法/时区边界验收。
3. Caption 目前为 DropdownMenu，不是官网透明 native select/combobox；字号/精确内边距、完整键盘、焦点返回及选项滚动待验。官网 Intl 默认浏览器语言显示“10月/十二月”，Kotlin 明确 English locale 显示 Oct/Dec，这个环境相关格式差异尚未统一。
4. Arabic 星期已按官网真实标签修正；三语切换、Hebrew 的全部月份/标签、读屏日期长名称、网格/星期的原生语义尚未完整验证。周数目前按周起点、包含 1 月 1 日的周计算，ISO/自定义 firstWeekContainsDate 尚无完整支持矩阵。
5. 不同 variants/disabled/hover/focus 的全部浅深状态、无选中/外部月份切换后的焦点、被拒绝的受控回调、复杂范围/外部日期、自定义槽位与超窄布局仍需完整复查。共享页面 P1/P3 的 15px 滚动条、内容/标题位置、字体栅格、代码及 API 表格差异仍沿用全站记录；首屏截取没有覆盖所有展开状态和深色长内容尾部。

Calendar 保持未勾选。下一项优先进入尚未开始本轮完整对照的 Carousel。此前最终全仓检查遭自动审批额度拒绝的限制未绕过，本轮仅执行新增实现必要的所属模块检查；因此累计改动暂未 commit，待最终门禁可执行后提交。

### 桌面应用图标和托盘设置（2026-10-30，用户新增）

<!-- progress-created: 2026-10-30; order: 70 -->

- 复用 Web Kotlin 渐变 favicon，生成带透明圆角的 1024px PNG；窗口、Dock/taskbar、托盘共用该图案，安装包配置 macOS ICNS、Windows ICO 和 Linux PNG。
- 原生“设置”菜单新增“显示托盘图标”“关闭窗口时最小化到托盘”，默认启用并持久化；禁用托盘或系统不支持时关闭窗口直接退出，不留下无法恢复的隐藏窗口。托盘菜单提供打开、关闭策略和退出。Dock 重开、托盘打开及深链恢复原窗口，不重新创建导航状态。
- 设置持久化测试及 macOS createDistributable 成功；ICNS 经 AppKit 解码验证，生成 app 已实际启动，无 Java/Compose 启动异常。完整手动托盘交互及 Windows/Linux 运行检查仍待验证，不据此声称跨平台运行验收完成。
- 使用入口及验证边界见 [desktop README](../app/desktopApp/README.md)。完成该增量后继续 Calendar；Attachment 暂缓项保持原记录。


#### 本轮最终门禁状态

桌面入口拆分及显式导入后，`:app:desktopApp:ktlintCheck`、`:app:desktopApp:detekt`、`:app:desktopApp:test`、`:app:desktopApp:createDistributable` 联合通过。此前全仓 ktlintCheck/detekt 已通过；追加桌面改动后的最终全仓复查被自动审批系统因额度不足拒绝，未实际启动。未绕过该拒绝，也未执行依赖同一全仓检查的 commit hook，因此本轮改动尚未提交。证据见 bubble-r1/quality-validation.json；该状态不影响已经通过的桌面定向验证。

### Bubble 首轮（2026-10-30）

<!-- progress-created: 2026-10-30; order: 69 -->

按清单推进到 Bubble；Attachment 的未关闭项继续暂缓，不再逐轮打磨。本轮读取并保存官方 Base UI 源码、九组配方和 Nova/Rhea 样式，[来源](web-parity/bubble-r1/sources.json)。

- 补齐 Default、Variants、Alignment、Bubble Group、Links and Buttons、Reactions、Show More / Collapsible、Tooltip、Popover。示例正文、真实点击通知、交互反应、折叠内容及错误详情均已接入；通知挂在窗口根层。补 Composition、四个 Kotlin API 小节及三组可访问性指南。
- 气泡按内容测宽，非 Ghost 最宽 80%，Ghost 可用整行；继承 Message 的 End 对齐，RTL 镜像。默认 14sp / 22.75sp 行框显式保留 leading，Nova padding 12/8dp 加 1dp 边框。根 variant 向内容传递；Muted 使用 foreground，Tinted 按 OKLCH 相对色计算而非透明色叠加。
- 反应行不参与父气泡测量，按自身高度偏移 75%、内联边缘 12dp；真实鼠标可点击超出气泡范围的按钮。互动内容提供 hover、键盘激活和 focus-visible。共享 Popover 增加无可聚焦子项时的焦点回退，使纯文本错误面板可 Esc 关闭，已有输入框自动焦点回归通过。公开签名调整，开发版本与安装示例同步 0.3.3。
- 17 项定向测试通过（Bubble 核心 7、目录 8、Popover 焦点 2）。仅重录本轮影响的 12 张 Bubble 与 Message/MessageScroller 各两张基准，保留旧图；已检查 Nova 浅深色、聊天依赖图并独立 verify 共 16 张。JS/Wasm development distribution、ktlintCheck/detekt 通过。现有 Text 内部 API、AGP/Gradle 与可选 os/path Webpack 警告仍保留。
- 官方/本地九组示例各 36 张首屏截图及四张对照图已检查，覆盖 1440×1000 和 390×844、DPR 2、浅深色；本地主题逐张按像素校验。桌面长内容尾部、实际展开状态及错误 Popover 过渡帧已另存截图；Popover 打开/关闭终态截图仍需补验，不将过渡帧计为终态验收。这不代表全部浏览器交互和超长内容四象限验收完毕。

明确剩余范围：

1. 官网此页 live preview 明确使用 Rhea/theme-blue；项目按 Nova 保留 8dp 纵向 padding、xl 圆角和 50% focus ring，不复制 Rhea 的 10px/22px/30%。手机官方滚动条使 clientWidth=375，本地 Canvas=390；共享页面内容偏移、标题/描述/代码框、字体和 emoji 栅格差异继续保留。
2. 任意自定义槽位的 overflow-hidden、同一气泡多个 BubbleContent 的纵向 gap 布局尚未移植；当前 BoxScope 保持兼容。旧调用只在 Content 指定 Ghost 不会解除根层 80% 上限，新配方在根指定 variant。
3. 按钮反应行需显式 Style padding=0，尚无 CSS has-button 自动检测；复杂 RTL 反应行、所有 variants 的 hover/focus/动画反转及原生辅助技术未全覆盖。
4. 全部长内容尾部和 Tooltip/Popover/通知交互的浏览器四象限仍需补验；本轮功能测试通过不能替代该矩阵。

Bubble 保持未勾选；下一项尚未开始完整对照的是 Calendar。用户新增桌面应用图标和托盘设置需求，先完成该增量，再继续清单。

### Breadcrumb 首轮及本地提交检查点（2026-10-30）

<!-- progress-created: 2026-10-30; order: 68 -->

按“尚未开始本轮页面与完整状态对照”推进到 Breadcrumb。官方 Base UI 源码、Nova 样式和七组配方已读取并归档，[来源](web-parity/breadcrumb-r1/sources.json)。本轮不新增 Responsive 示例：当前官网 Breadcrumb 页面实际包含 Default、Basic、Custom separator、Dropdown、Collapsed、Link component、RTL。

已修改：

- Breadcrumb/List 使用 FlowRow，列表横纵 gap 均为 6dp；BreadcrumbItem 从透传改为真实 Row，内部 gap 4dp，复杂单项整体换行。保留现有 RowScope 调用方式，并新增 list/item 的 modifier/style。开发版本及安装示例同步至 0.3.2。
- 默认分隔符从实心手绘箭头改为内部 Lucide 形状的 14dp 描边 chevron，随 RTL 镜像；省略号为 20dp 容器、16dp 三圆描边图标。装饰节点隐藏于辅助语义，current page 选中、禁用且不可点击。
- 链接移除额外的按压位移，接入真实 hover 状态及 150ms 原生 Style 内容颜色过渡；默认 muted、hover foreground。保留 disabled 行为和调用者 Style 覆盖。
- 默认配方补齐可点击的省略号菜单、Components 及 current page；菜单按钮中的省略号保持居中。Custom separator/Dropdown 使用 Dot，Dropdown 为 Components + 下箭头。Collapsed 保持纯展示，Link component 使用当前页片段导航。补齐三语言真实标签及菜单内容、RTL 布局/菜单对齐，保留额外 Disabled 示例。
- Composition、Usage 和七个 Kotlin API 小节来自同一 ComponentDoc；示例路由采用调用者 DocNavigation/UriHandler，不再全部为空回调。

验证：

- Breadcrumb 9 项功能/布局测试通过：换行与间距、RTL 项顺序、装饰隐藏/current page 语义、浅深色真实 hover 墨色及点击、默认菜单选择/Esc、片段路由、真实 URI 目标、三语言菜单。
- 官方和本地各 28 张截图覆盖七组示例 × 桌面/手机 × 浅深色；视口 1440×1000、390×844、DPR 2。四张对照图已检查，逐张以实际背景像素核验本地主题。首屏菜单四象限打开/ArrowDown/Esc 八张截图已检查，源码菜单项目 Documentation/Themes/GitHub 也已实际打开核对。
- 用户新增“适当 commit”要求后，按仓库截图规则只录制本轮涉及的 Avatar/Breadcrumb。视觉检查发现 Avatar fallback 背景缺少圆形设置，已显式补 CircleShape；历史六张 Avatar Vega 基准的待验收项在此关闭，新增 Avatar/Breadcrumb Nova 浅深色基准。共 13 张图片已检查并独立 verify；没有重录其他组件。旧图保存在 baselines-before，便于追溯。
- 提交前联合验证共 31 项通过：core 23 项（10 项功能 + 13 张像素），目录 8 项。最终 JS/Wasm development distribution 成功。现有 AGP 实验、Text 内部 API、Gradle 与可选 os/path Webpack 警告仍保留；没有用这些定向结果宣称全仓测试通过。
- 汇总：[evidence.json](web-parity/breadcrumb-r1/evidence.json)。

明确保留的差异：

1. 共享页面 P1/P3 尚未完成。官网手机 clientWidth 为 375、Canvas 为 390，同样预览内边距下，Basic/Custom separator/Link component 在官网换行、本地可能仍一行。列表本身换行已测试，不用组件特例内边距掩盖共享宽度差异。标题/描述高度、代码和字体栅格差异也保留。
2. KMP 回调链接目前仍是 Button 语义；DOM nav/ol/li/link、aria-current 等映射和原生辅助技术尚未完整对齐。浏览器默认链接焦点 outline、动画中断反转、任意长文本/复杂动态槽位及原生路由仍需复查。
3. Dropdown 的行高/阴影、焦点返回及完整翻译后 RTL 弹层矩阵继续交由 Dropdown 专项，不以 Avatar/Breadcrumb 的基本菜单通过代替。

本次计划提交为整段未提交 Web 对齐工作的检查点，包含此前页面/组件/文档/证据；未完成项仍按清单保留，不代表全站验收或发布。接下来优先进入尚未开始本轮对照的 Bubble。


#### 提交门禁后续验证（2026-10-30）

首次提交被仓库 pre-commit 拦住，未生成 commit。hook 格式化了此前累计代码；已修复其后暴露的长行/缩进、浮点区间 `.35f...65f` 解析问题（改为等值 `0.35f..0.65f`）。没有关闭格式规则或更换 lint 引擎。

当前 ktlint1.0.1 对复杂 raw-string 插值产生了重复文本：AlertDialog 的 header、Attachment 图标和 Trigger 配方受影响。已改为预计算简单变量配合 trimMargin，并保留原 trimIndent 行为；新增配方回归检查，确保源码中仅有一个 vector builder、根/内层缩进正确且不泄漏生成器表达式。只是修复提交门禁引入的代码示例问题，Attachment 其余暂缓项不变。

最终 `ktlintCheck` / `detekt` 全部通过；格式化后的 53 项联合回归及 13 张像素基准通过。随后追加 16 项定向回归（Attachment9、文档7，包含新增配方检查），JS/Wasm development distribution 均通过，再次通过静态门禁。相关 XML 和压缩 Gradle 记录见 [commit-validation.json](web-parity/breadcrumb-r1/commit-validation.json)。浏览器截图对应相同组件 UI；后续变更为格式和配方生成器修复，不把提交门禁通过扩大为全站组件验收。

### Avatar 首轮（2026-10-30）

<!-- progress-created: 2026-10-30; order: 67 -->

Attachment 按用户要求暂缓，已转入清单下一个尚未开始对照的 Avatar。官方依据为 Base UI Avatar 页面、`avatar.tsx`、Nova CSS 和十个示例文件，固定源码版本与头像来源见 [sources.json](web-parity/avatar-r1/sources.json)。官网当前预览的共享外观可能为 Rhea；组件尺寸按固定 Nova CSS 检查，不能将页面整体截图视为 Nova 逐像素基准。

已修改：

- 目录补齐 Default、Basic、Badge、Badge with Icon、Avatar Group、Avatar Group Count、Avatar Group with Icon、Sizes、Dropdown、RTL；保留额外 Fallback。四张官网头像打包为本地资源，替换旧 `model = null` 的图片假示例。增加 Composition、六个 Kotlin API 小节、描述及可复制配方。
- 默认/大号 fallback 为 14px、普通字重，小号 12px，移除原先 LabelSmall 11px/Medium 和字距。徽标增加图标槽位；小号隐藏图标，默认/大号图标 8dp；默认位置为 bottom-end，RTL 镜像到左下角。
- 徽标、分组头像和计数的 2dp ring 使用已有 Style/原生 shadow 工具绘制于外部，避免使用内部 border 代替外圈；Style 仍接受最终形状覆盖。徽标默认定位先于调用者 modifier，允许显式位置覆盖。绿色使用 TwColors.green600/green800 的实际 OKLCH 令牌。
- 新增 Painter AvatarImage 重载，成功加载后隐藏 fallback、移除后恢复；图像保持圆形裁剪。分组计数自定义图标提供 muted 前景色。
- Avatar 下拉菜单暴露出根 DropdownMenuContent 未接入既有菜单导航的问题，已复用 ContextMenuNavigation 接入首项焦点、方向键和 Esc。保留四个菜单项、128dp 宽度及 destructive Log out。徽标 API 调整涉及接收者与槽位，开发版本及安装代码同步至 0.3.1。

验证结果：

- 9 项定向测试通过：core 5 项（真实 Painter 状态/移除、loading 延迟及 Error、字号、RTL 徽标、分组镜像）；目录 4 项（真实图片、菜单选择/方向键/Esc、完整配方/API、阿拉伯语/英语/希伯来语选择与计数/实际镜像位置）。
- 最终 JS/Wasm development distribution 构建成功。已有 AGP 实验项、Text 内部 API、Gradle 兼容性及 Webpack 可选 os/path 警告保留。
- 官方/本地各 24 张代表性示例截图：Default、图标徽标、分组图标、Sizes、Dropdown、RTL × 桌面/手机 × 浅/深色；1440×1000 与 390×844，DPR 2。四张对照图已检查；本地浅深色按真实背景像素核对。Canvas 正文为空，未以 DOM body text 或文件名代替主题验证。
- 菜单四象限实际 pointer 打开、ArrowDown、Esc 已操作，关闭态等待退出动画结束再采集；八张截图及 contact-menu 已检查。最初坐标误点和主题被 goto 重置的截图已替换，不计入验证。
- 原生截图基准未重录，不能把 jvmTest 通过当成像素对比通过。证据汇总：[evidence.json](web-parity/avatar-r1/evidence.json)。

后续统一处理（Avatar 继续保持未勾选）：

1. 官网 Avatar 的 1px `::after` 边框使用 lighten/darken 混合模式，当前尚未移植；任意形状覆盖和通用槽位字体继承还需完整矩阵。
2. 官网 GroupCount 会按组内 Sm/Lg 成员自动调整尺寸；当前 Kotlin API 需显式传 size，混合尺寸、图标自适应仍有差异。
3. 远程加载重试/竞态及原生辅助技术仍待检查。本轮真实图片为本地 Painter，不等同于网络加载全流程验收。
4. Dropdown 的完整焦点返回、嵌套菜单、pointer/keyboard 初始高亮和共享面板行高/阴影差异转入 Dropdown 专项；本轮只验证 Avatar 配方的基本行为。
5. 六张旧 Vega Avatar 像素基准未验收或重录。共享页面 P1/P3 的内容偏移、滚动条、标题位置、代码格式和 API 表格差异沿用已有记录。

推进顺序：不在 Avatar 继续逐轮打磨上述次要差异；按“尚未开始本轮页面与完整状态对照”优先进入 Breadcrumb，Badge 已有基础检查仍保留后续完整复查。

### Attachment 暂缓项（2026-10-30，按用户要求转入 Avatar）

<!-- progress-created: 2026-10-30; order: 66 -->

不再阻塞后续组件。待统一处理：旧 Vega 六张像素基准差异、字形栅格观感、自定义 overlay 与外层 hover 联动、完整 transition-colors（含中断反转时序）、复杂槽位及极端约束、原生辅助技术。共享页面 P1/P3 差异沿用已有记录。

第八轮已有代码将交互卡 hover 背景改为 150ms FastOutSlowInEasing；浅深色真实指针进入/退出/反转定向测试已通过，但完整颜色属性、浏览器四象限及 JS/Wasm 构建尚未验收，不能视为本组件完成。保留改动供后续一起复核，证据见 `web-parity/attachment-r8`。

### Attachment 第七轮：当前 FileSearch 路径与真实标题排版（2026-10-30，尚未关闭）

<!-- progress-created: 2026-10-30; order: 65 -->

读取官网Trigger媒体里的实际SVG：放大镜位于纸张左侧，当前icons-lucide-cmp2.2.1的FileSearch仍是旧版右侧设计。目录使用同一24单位viewBox、stroke2、round cap/join路径构建新ImageVector（circle转为等价弧段），只替换此例，不给core引入图标依赖或更换整套图标包。Trigger展示代码同时包含实际矢量构造，并从同一组路径字符串生成，补足相关imports，不再用旧Lucide.FileSearch调用伪装示例代码。上游ISC/MIT许可位于composeResources/files/licenses/lucide-icons.txt；原始SVG和来源测量在attachment-r7。

新增实际Trigger媒体像素测试，使用官网DOM里的SVG在原生浏览器DPR2下绘制16/24px，16px浅深参考用于测试；24px只作诊断资产。测试渲染真实目录例子，并以安装的旧图标作反例，同一6%平均RGB误差阈值接受新图标、拒绝旧图标。浅色新/旧误差为2.875%/13.941%，深色为1.987%/12.120%；不宣称不同渲染器AA逐像素相同。首次取样失败含夹具错误：clickable语义截图为382×56内容框，不含外边框，正确图标偏移为20而非21px；已修正取样位置，未放宽容差。临时诊断PNG写入与println已移除，实际图和收据保留。

标题没有随观感任意改为600。官网实测14px、fontWeight500、lineHeight17.5px，使用Geist1.800变量字体；本地400/500/600/700文件同为1.800，浏览器确实加载Medium，OS/2字重标记正确。初始把hmtx字宽总和152.04px当作排版宽度的断言不成立：官网真实Canvas含字偶距宽148.008163px、DOM Range148.015625px，本地真实TextLayoutResult为147.99402px，在0.1px容差内，字重/字号断言也通过。hmtx原值仍作为未排版诊断保留，已加说明。

字体文件存在hint表差异（本地有fpgm/prep/cvt，官网无），轮廓指令结构也不能直接按原始hash判等；该证据不等于已经隔离字形观感差异的唯一原因。此例的字重、字号、排版字宽不再记为已确认不匹配；残余字形像素观感仍留全站P3，不改公共字体资源。完整原始字体文件仅在临时目录分析，仓库保存来源/hash/元数据与实际排版结果。

最终AttachmentExamples8、ComponentDocumentation7、CatalogNavigation6、CatalogPreview5，共 **26项通过**；JS编译和Wasm development distribution通过。既有AGP实验项、Gradle deprecation、Text.kt与webpack警告保留。未改core运行时，未重录或重跑其六张像素基准；第五轮基准证据延续，旧Vega六张差异也未关闭。新的两张16px参考是原生SVG测试输入，不是Roborazzi基准重录。


浏览器最终官网16张＋本地16张，覆盖1440×1000/390×844、浅深色及rest/hover/动作焦点/整卡焦点，四张最终contact均查看。对照图改为整数原图裁切：之前直接用含.5/.25px的DOM矩形做裁切会额外插值，使官网字形看起来更糊；历史第五轮contact不改写，本轮不以这类二次插值作为字重证据。原始截图保留实际页面自身的亚像素布局与AA。

另分别测量官网CSS颜色和本地浏览器实际墨色：标题/媒体在浅色均为RGB10，深色均250；媒体背景均245/38。原生SVG几何参考显式归一到JVM默认fixture色（浅色stroke23），与实际目录主题的10区别记录在validation.json，不能把几何参考当作官网整张配色截图。五个Geist字体资源在最终浏览器实际加载，来源与Wasm资源记录见browser-build-resources.json。

本轮关闭FileSearch轮廓缺口，并确认Trigger标题字重/字号/实际排版宽度及墨色与实测相符；残余字形抗锯齿、原生亚像素布局仍归共享P3。Nova/Rhea根圆角与焦点环、15px共享视口差异等条件延续前轮；独立overlay hover、颜色过渡、复杂槽位/原生辅助技术和旧Vega差异仍开放。Attachment未勾选，全站和已完成组件复查范围不变，editor继续跳过。完整证据为 [attachment-r7/evidence.json](web-parity/attachment-r7/evidence.json)。

### Attachment 第六轮：Features/Accessibility 正文、目录与 Copy Page（2026-10-30，尚未关闭）

<!-- progress-created: 2026-10-30; order: 64 -->

关闭第五轮新增的章节缺口。Features放在Composition之后、Image之前；Accessibility放在Trigger之后、API之前，含Label icon-only actions、Label the trigger、Keyboard scrolling、Meaning beyond color四个三级标题。内容按真实Kotlin API改写：label/contentDescription、LocalUriHandler、默认可聚焦Group与描述性错误状态，不伪称Compose渲染原生HTML按钮。图标代码显式按LocalContentColor着色，URI使用目录已有的真实图片地址。去掉官网相应位置没有的额外Keyboard/Error代码块；Root API恢复简短容器说明，避免新指南与API重复堆叠。

目录模型新增beforeExamples/afterExamples正文列表，块类型只有本轮需要的段落、项目列表和代码。页面渲染、目录锚点和Copy Page消费同一份数据；现有文档默认空列表，未改组件发布API或再提升版本。新增导航使用features/accessibility/label-icon-only-actions/label-the-trigger/keyboard-scrolling/meaning-beyond-color可读片段，四项子标题在目录缩进。

按官网DOM实测修正新正文排版：桌面/手机列表缩进22.5/28.35px加项目内边距6/7.56px，项目间隔7.5/9.45px，列表前18.75/23.625px；段落在标题后15/18.9px，代码前17.5px。项目符号使用主题mutedForeground，小圆点对齐首行，文本独立换行，并给列表提供CollectionInfo。测量源见official-list-styles.json及official-browser.json。其余共享章节外边距、标题定位/页面骨架、字体与代码高亮差异仍归P1/P3，未宣称整页逐像素一致。

新增两项实际目录测试：导出和目录顺序、六个片段、代码/指导内容；以及真实Attachment页面的首次片段加载、Copy Page、全部六个目录链接、标题链接和屏幕内可见性。首次失败来自JVM默认场景尺寸小于requiredSize，产生负屏幕坐标；改为调整真实测试scene到1440×900，并加强横纵边界断言，不放宽到“节点存在”。最终ComponentDocumentation7、CatalogNavigation6、AttachmentExamples6、CatalogPreview5，共 **24项通过**；JS编译和Wasm development distribution通过。编译保留既有AGP/Gradle/Text.kt/webpack警告。本轮未修改core运行时或重录像素基准，不能把第五轮六张verify说成本轮重跑。


最终浏览器矩阵为官网24张＋本地24张，六个新章节在1440×1000/390×844及浅深色逐节打开；12张并排contact均查看，最终精简Root API后重捕本地矩阵，并复看受影响的API邻接画面。另实际点击桌面Features、Accessibility、Keyboard scrolling三个目录，检查URL更新和滚动稳定后截图，三张均查看；JVM补足全部六项目录及标题链接、Copy Page。正文Kotlin名称、代码行数和说明长度与React版本的差异是明确适配，不通过硬编码章节高度掩盖。尚未关闭原生读屏行为，增加Accessibility说明不等于组件辅助技术已验收。

Shared P1/P3仍包含章节定位、正文/代码高亮、API表三列与官网四列的结构差异，以及手机长类型的视口裁切/横向可达性规则待复核。Attachment其他运行时缺口与旧Vega六张像素问题延续第五轮；本轮仅关闭Features/Accessibility正文缺失及其导航/导出，Attachment未勾选。全部组件复查、已完成项复查及editor跳过范围不变。证据与hash见 [attachment-r6/evidence.json](web-parity/attachment-r6/evidence.json)。

### Attachment 第五轮：整卡焦点环、悬停与独立 Trigger（2026-10-30，尚未关闭）

<!-- progress-created: 2026-10-30; order: 63 -->

固定源码的 Nova 根要求 focus-within:ring-1/ring-ring/50，Base 根在有直属按钮/链接时 hover:bg-muted/50。当前 onClick 适配原先只可点击，没有这两种整卡视觉反馈。本轮 Nova 根通过真实后代焦点事件驱动1dp/50%外环，包含指针聚焦及非交互卡片内的动作聚焦，不套用按钮的 focus-visible 规则，也不改原边框颜色/厚度。根继续由 Style 管理形状和交互，采用已有 cssStyleable 外阴影 knockout，让焦点环不会渗入半透明 hover 背景；调用方末尾 Style 仍能覆盖。onClick 根使用同一个 clickable InteractionSource 的 hovered 状态，静态卡片不增加 hover 色。

独立 BoxScope.AttachmentTrigger 原文档写 z-10 而实现缺少 zIndex，本轮补10f。三项新交互测试覆盖：浅→深→浅、Touch/Keyboard模式下后代焦点的实际外环像素、布局不变及移出最后子控件后环消失；Trigger 与父边界相同、无论插入顺序都盖住z0内容、z20动作仍独立点击；Enter/Space以及enabled=false/disabled=true实时切换不会误触。覆盖层的测试动作明确置于兄弟z20，不能推断任意嵌套Box会自动修复层级。未新增公共参数或再升级版本。API说明补充名称、可见文件名/状态、禁用和自定义覆盖层的责任。

最终 **92项通过**（core70、catalog22），测试 XML 包含加强的离开后代焦点断言；六张既有 Attachment Nova/Spinner/共享ScrollFade显式verify均unchanged，没有重录。JS编译、Wasm development distribution通过。保留既有Text.kt INVISIBLE_REFERENCE、AGP实验项、Gradle deprecation及webpack警告。旧Vega六张像素问题延续第四轮，未借本轮92项普通测试掩盖像素失败。


浏览器实际捕获官网16张＋本地16张，在1440×1000、390×844浅深色中分别检查无焦点、hover、Remove动作键盘焦点、整卡预览焦点。四张最终contact均已查看。官网DOM记录确认root的hover为muted/50、后代focus-within环为1px；官方MDX使用Rhea，其实际环透明度为30%，本库按Nova源码50%，以及Rhea18px/Nova14dp圆角、动作圆角差异，均保留条件说明，不据截图直接改成Rhea。浅色本地内部背景实测255→247、深色23→24，退出hover后恢复；JVM直接验证1dp外环及完全失焦后消失。

第一批官方手机卡片底部被焦点定位滚出视口，本地手机深色也保留了上一次整卡焦点；最终脚本先把焦点移到卡片外，再重新定位章节，源码端检查卡片完整在视口内，重新捕获并覆盖初版帧。Canvas焦点不能由DOM activeElement证明；本地焦点结论来自实际键盘输入、已查看的环/动作外观和JVM焦点断言。官网原生Tab顺序为Copy→Remove→Preview，本地onClick适配为Preview→Copy→Remove；本轮没有硬改或宣称二者Tab顺序完全相同。

仍有真实缺口：根hover的结束颜色已接通，但Base的transition-colors渐变时序尚未实现；独立Box overlay仍不会自动登记到旁边Attachment的hover条件，只有onClick根的hover接线已关闭。完整原生读屏映射、复杂自定义槽位、字重/图标、旧Vega六张像素问题及共享P1/P3继续保留；静态卡片子动作的整体焦点反馈和独立Trigger的基础输入/层级已验证，不再笼统记为完全未验收。Attachment仍未勾选，其他组件及已完成项的复查目标不变，editor跳过。证据为 [attachment-r5/evidence.json](web-parity/attachment-r5/evidence.json)。

补充未关闭项：本次浏览器相同位置的浅色hover灰度取样为官网246、本地247，深色均24，尚不宣称像素相同；官网独立 Features/Accessibility 章节及其导航仍未完整迁入，本轮 API 标签说明不能代替这些章节。

### Attachment 第四轮：约束边界与显式部件作用域（2026-10-30，尚未关闭）

<!-- progress-created: 2026-10-30; order: 62 -->

根 Attachment 在最近的 Group 视口内落实 max-w-full：普通 Modifier.width/Style.width 的期望宽度受视口上限约束，Nova 水平 min-w-40 仍保留，显式 requiredWidth 继续允许可滚动溢出。视口变化时在测量阶段使真实卡片重新测量，无新增布局到 composition 的 Snapshot 回写。四项真实布局测试覆盖 245→300→200→245 往返、LTR/RTL、独立及嵌套 Group、100dp 极窄容器和 requiredWidth 超宽末端可达。既有超宽 snap 用例改用 requiredWidth，明确区别普通期望尺寸与强制溢出。

新增 AttachmentScope，将根 size/orientation 显式传给 Media/Actions；未添加密度 CompositionLocal。xs 媒体使用 rounded-md，其余 Nova 媒体 rounded-lg；纵向动作 gap 使用 xs token（默认4dp），水平0，RTL 下纵向动作保持源码 right-3 的物理右侧。五项作用域测试检查实时尺寸切换的实际圆角像素、嵌套隔离、提取的扩展 helper、方向/布局往返、物理右侧及新旧 JVM 函数符号。

**历史数值更正**：第二、三轮记录及其历史 evidence 中的“xs 当前8dp、目标6dp”误用了 Tailwind 默认刻度。当前 catalog 基础半径为10dp，主题映射 rounded-lg=10dp、rounded-md=8dp、rounded-xl=14dp；本轮使用主题 token，未硬编码6dp。历史带 hash 的证据不改写，以本节更正为准。

公共 content/actions 槽位类型改为 @Composable AttachmentScope.() -> Unit，版本升至 **0.3.0 开发版**。普通尾随 lambda 可直接使用，存储的旧无接收者槽位需改类型或以 { oldContent() } 包装；提取含 Media/Actions 的 helper 应作为 AttachmentScope 扩展才能继承根变体。保留 HIDDEN 旧签名桥，测试证明新旧 JVM 符号存在，不据此宣称已完成外部预编译客户端验收。README 明确该版本是当前 checkout，未发布 Maven 包。

功能/布局测试 **89项通过**（core67、catalog22），JS 编译与 Wasm development distribution通过。显式 Roborazzi verify 正确的六张 Attachment Nova/Spinner/共享 ScrollFade 基准均 unchanged，未重录。额外检查旧 Vega Attachment 六张基准时全部出现差异，保留 compare/actual 和失败 XML；已查看的 vertical/light 差异集中于根圆角，尚未逐张归因，不能把这六张算作通过或直接覆盖。收据见 attachment-r4/test-results.json、pixel-verification.json 和 legacy-pixel-differences/。既有 AGP 实验项、Gradle deprecation 等警告保留。


浏览器本轮 Group 官网16张、本地24张：1440×1000、390×844 浅深色各四状态（起点/单次330px滚轮/末项/向左键），另加本地375×844浅深色共8张用于对齐官网 clientWidth375 下的245px内容视口。实际 DPR=2，DOM输入校验通过；常规390px本地内容仍260dp，不能混称两者同一窗口条件下轨迹相同。对齐探针下卡片上限245dp生效，末项向左键保留 renderer，原始像素局部平移搜索在浅深色均得到准确+4px、误差0；官网 scrollLeft771→767。标准390px本地则仍跳至 customers，其视口差异归共享P1。八张 Group contact 已逐张查看，完整记录及局部取样区域见 local-browser.json、official-browser.json、aligned-shift-probe.json。

Sizes 官网/本地各四张，桌面手机浅深色全部查看；官网真实 DOM 确认媒体宽40/32/28px、圆角10/10/8px，手机测量见 official-sizes-measurement.json。本地手机 Sizes 标题定位低于官网，但三卡完整可见，不能把章节位置视为已对齐；字重/图标、根圆角（官网Rhea、本库Nova）、动作焦点外观及共享页面骨架差异继续保留。本次浏览器采集辅助脚本曾因375px截图比例/变量引用错误中止，修正后完整重跑；最终JSON均包含完整矩阵，未保留失败帧作最终证据。

本轮关闭 max-w-full、xs主题圆角及纵向多动作间距三个明确缺口。Attachment仍未勾选：六张旧Vega像素差异待归因；复杂槽位与极小约束尚非穷尽验证；hover/focus-within、独立AttachmentTrigger、完整辅助技术映射和原生设备手势仍待复核，字体/图标及全站P1/P3不变。editor继续跳过。完整收据、源码hash和浏览器证据见 [attachment-r4/evidence.json](web-parity/attachment-r4/evidence.json)。

### Attachment 第三轮：Group 吸附、真实键盘与输入尺度校准（2026-10-30，尚未关闭）

<!-- progress-created: 2026-10-30; order: 61 -->

保留现有 Row/RowScope 布局，通过实际 Attachment 的 Modifier.Node 边界提供 SnapLayoutInfoProvider。停下时读取当前几何，4dp 逻辑滚动留白随 RTL 镜像；不同宽度的卡片、末项边界以及大于 snapport 的卡片都参与正确的停留区间。嵌套卡片/嵌套 Group 不作为外层的额外停点，非 Attachment 内容不产生停点；没有把组件换成固定页宽的 Pager，也没有新建位置 CompositionLocal 注册表。公共函数签名保持不变，现有根 Style 和调用方 Modifier 继续生效。

Group 增加容器焦点和未被子控件处理的左右键导航；超宽卡片内按40dp逐步滚动，普通卡片跳至相邻停点。真实 Tab/Shift+Tab 到达屏外操作，并验证操作按钮完整位于容器范围内；Enter 只激活当前操作。嵌套滚动只隔离横向余量和横向剩余速度。滚轮结束时将交给最终吸附的速度归零，触摸拖动继续使用原生惯性；多事件滚轮用例覆盖实际 UI 路径。Group 现在有额外容器焦点；调用方应通过 modifier 提供语义标签，该 Compose 适配与完整读屏映射仍需后续复核。

新增 AttachmentGroupSnapTest 共9项：不同宽度及左右方向的真实低速拖动/4dp停点/末项；超宽卡片保留按住位置；动作上的方向键；横向边缘不带动父容器；超宽卡片逐步键盘移动；非附件节点不生成停点；原实例动态切换 RTL；Tab/Shift+Tab/Enter 的焦点与点击；多事件鼠标滚轮停下后不再追加触摸惯性。测试使用实际 Group 原点测量，修正了把测试场景24dp外边距误当作滚动偏差的断言；滚轮单位按当前 Foundation 的平台转换选取，不再假定旧文档中的64dp换算。

最终80项相关回归全部通过（core58、catalog22），JS编译和干净 Wasm development distribution通过。随后显式 Roborazzi verify 六张均 unchanged：Attachment Nova2、Spinner2、共享 ScrollFade2。本轮没有新增或重录像素基准。临时数值诊断 println 已移除；保留既有 Text.kt INVISIBLE_REFERENCE、AGP实验选项、Gradle deprecation 和 webpack 警告。XML、compact ledger、源码与构建 hash 见 [attachment-r3/evidence.json](web-parity/attachment-r3/evidence.json)。

浏览器检查发现本机 Retina 主机的 DPR 仿真差异：在同源、无框架的原生滚动容器里，DPR=1 时单个 CDP deltaX=330 让原生容器滚动330，但 DOM WheelEvent 报告660；DPR=2 时 DOM和原生滚动均为330。原生 CSS snap 容器在两种模式都停264。这解释了早期把 DPR=1 的本地末项截图当作单纯吸附惯性问题的不充分判断；不向组件加入除以2的补丁。校准 HTML/JSON、数值轨迹及早期截图仅作诊断证据，不作为最终输入等价验收；临时页面已从 Web 构建目录移除。

最终采用原生 DPR=2，记录实际收到的单个330px DOM事件；官网/本地分别16张，共32张，覆盖1440×1000与390×844、浅深色，以及起点、滚轮后、末项、键盘返回四种状态。四张最终 contact 全部查看：桌面滚轮后均显示第二张 workspace、末项 renderer 可达、向左键均返回 customers；手机同样能滚动到第二张及末项。截图工具输出仍为 CSS 分辨率，实际 DPR 和事件数据以 official-browser.json/local-browser.json 为准，不能从 PNG 宽度反推 DPR。截图等待目标滚动位置或卡片边界稳定，不以整页其他动画的 PNG 哈希作为滚动完成条件。

此外补齐第二轮尚缺的真实图片链接验收：手机浅色中点击 workspace 图片卡片，实际打开正确 Unsplash URL 的新标签，查看了原图；点击同卡片的移除按钮不打开链接。已关闭本次创建的图片标签，任务空间恢复两页。该结果见 photo-interactions.json 与两张实际截图，不扩大为所有浏览器/原生 UriHandler 的结论。

仍保留可见差异：官网手机 Group 容器245px、四卡各245px，本地容器260dp、示例卡各256dp，来源包含 native scrollbar 的15px共享视口差异；更窄 Group 内根 Attachment 的 max-w-full 默认上限尚未实现/验收。官网手机末项向左键可先在其末项停留区间内移4px，本地当前宽度条件下进入前一张，未据桌面通过宣称手机轨迹完全一致。浅深标题字重、图标版本、Nova xs 媒体6dp圆角及纵向多动作4dp间距仍未修正；不通过示例手动样式覆盖来隐藏这些缺口。复杂槽位/极小约束、hover/focus-within、独立 AttachmentTrigger、完整读屏语义及原生设备手势仍保留第二轮范围。Attachment 和全站目标继续未完成，editor跳过。

### Attachment 第二轮：六组示例、真实布局、Trigger 与渐隐（2026-10-30，尚未关闭）

<!-- progress-created: 2026-10-30; order: 60 -->

迁移 Default、Image、五 States、Sizes、Group、Trigger 六份实际配方；用与官网同源的三张照片替换 emoji，作为 catalog 离线资源保存，未给 core 增加图片或图标依赖。每份示例展示完整 Kotlin 代码，补充 Composition 和 API 说明。预览采用实际测得的外层 p40 加配方 py48，浅色底 #F8F8F8；修复此前把这两层留白合并导致示例高度不足的问题。图片 URL、下载参数、hash 及来源见 image-sources.json。

Nova 根布局改为实际单一 Style 容器和内部 Layout：水平媒体 40/32/28dp，间距 8/10/6dp；动作先占空间，长文件名在剩余宽度单行省略，不再挤出动作。根 Style 宽度与底色覆盖实际卡片；纵向含文字卡片默认宽120dp、纯媒体96dp，媒体随可用宽度保持方形，动作覆盖在顶端而不占正文高度。文字使用已有继承路径，Default14sp、Sm/Xs12sp，标题 medium；图标和 Spinner 通过既有行内图标尺寸继承，操作按钮保留 icon-xs 的12dp。新增 Image media 重载，上传/处理中/错误时透明度60%，Idle/Done100%；最终 Style shape 能裁切图片，保留原 icon 重载。

Trigger 使用实际可点击根节点和真实 Dialog，设置448dp最大内容宽度；独立 Copy/Remove 操作不冒泡打开预览，保持上游示例的空操作。JVM 验证 Enter 打开、Escape 关闭和焦点返回原卡片，图片 URI 使用注入 UriHandler 验证，移除按钮不打开图片。浏览器桌面/手机浅深四组均实际打开弹窗、Escape 关闭，并查看动作后、打开及关闭截图；Canvas 的 DOM activeElement 不能证明 Compose 焦点恢复，该结论由实际焦点测试支持。本轮没有据 JVM URI 测试宣称浏览器图片新标签已验收。

AttachmentGroup 接入现有 ScrollFade 绘制路径的内部扩展：边缘最大 min(12%视口,40dp)，按96dp滚动范围及 CSS ease-in-out 逐渐显露，RTL 交换物理左右边缘，无溢出不渐隐。公共 ScrollFade 原签名、默认24dp和即时边缘规则保持；其横向 RTL 同步修正。新增实际像素测试验证起点、中间渐增、终点、无溢出及 RTL，未以仅计算 helper 的测试代替组件接线。手机浏览器实际水平滚动后确认内容可达、边缘渐隐生效；官网会吸附到后续卡片，本地仍自由滚动，这一差异继续保留。

同时根据真实 Spinner 源码的 Lucide LoaderCircle，修正此前全画布弧线过粗、绘制超出标称尺寸：24单位 viewBox、半径9、笔画2、288°弧段。Sm12/Default16/Lg24 和显式 Style20 覆盖均检查实际捕获尺寸；八张浏览器原生 SVG 固定角度参考图支持浅深色轮廓、覆盖面积和空白边界断言。不同渲染器的边缘采样并非逐像素相同，测试允许明确的亚像素覆盖带。只重录经过 before/after/compare 检查的两张既有 Spinner 基准；64×64对照图变化仅在[22,22,41,41]字形区，背景与布局未变。新 Attachment 两张基准单独记录，共享 ScrollFade 旧基准未重录。

最终定向回归71项通过（core49、catalog22，含更强的 Trigger 焦点返回断言），JS编译和 Wasm development distribution通过。随后显式 Roborazzi verify 六张均 unchanged：Attachment Nova2、Spinner2、共享 ScrollFade2；无 blanket record。保留 Text.kt INVISIBLE_REFERENCE、AGP实验选项、Gradle deprecation 和 webpack 既有警告。XML、compact ledger、像素结果、SVG参考、源文件及资源 hash 见 [attachment-r2/evidence.json](web-parity/attachment-r2/evidence.json)。

浏览器最终矩阵为官网24张＋本地24张：1440×1000/390×844、浅深色、六组示例；本地在渐隐构建后重新捕获。六张示例 contact 全部查看，DOM/图像测量的预览高度差均在2px以内。另查看四组官网/本地弹窗对照及本地独立动作和关闭状态，手机 Group 起点与滚动后截图。弹窗截图捕获早于最后新增 Group 渐隐，Trigger 实现未再变化；这些截图不证明拖动、原生读屏等未测场景。

条件与剩余差异明确保留：官网 MDX 固定 base-rhea，本库继续 base-nova，Rhea 卡片18px圆角和 Dialog 的更大圆角、p6、较深遮罩不作为 Nova 的目标；Nova 的 p4、radius-xl、black/10 与固定源码一致。官网手机 native scrollbar 占15px，client375/本地390，预览325/340、水平卡片245/260，影响截断，属于共享 P1。深色标题字重、文件名截断仍有可见差异；已确认 Geist medium500 字体存在，不能把原因归为缺少中等字重。Lucide FileSearch 图形版本也有差异。Nova xs 媒体目前8dp圆角、目标6dp，纵向多动作目前0dp间距、目标4dp；Group mandatory snap、scroll-px-1、键盘滚动/overscroll、复杂槽位/极小约束、hover/focus-within、独立 AttachmentTrigger 及完整辅助技术映射仍未关闭。共享正文、源码与章节定位差异继续归 P1。Attachment、Spinner 和全站目标均不标为完成，editor继续跳过。

### Attachment 首轮：状态继承、card 颜色与实际扫光（2026-10-30，尚未关闭）

<!-- progress-created: 2026-10-30; order: 59 -->

读取固定上游96da9cc的Base Attachment、Nova CSS、MDX和六个配方。官网MDX固定styleName=base-rhea，实际默认卡片圆角18px，不能直接作为本库Nova圆角目标；本库继续按Nova样式，通用结构/状态按Base逻辑。来源与hash见 [attachment-r1/sources.json](web-parity/attachment-r1/sources.json)。本地原来的首屏是三个状态横排，官网首屏是图片上传托盘；本次没有把首屏视为已对齐。

修正根背景background→card，显式提供cardForeground。根state通过私有、每个实例独立的CompositionLocal传入Title、Description、Media：Uploading/Processing默认启动标题扫光，Error默认显示destructive/10媒体底色、destructive媒体内容色及destructive/80描述。显式Title state、Description isError仍能覆盖；嵌套Attachment以自己的根状态为准，单独使用这些子组件仍默认Done。参数类型和槽位结构未改；目录说明改为当前官网描述。

标题原先用透明度脉冲近似shimmer，现在使用已有Modifier.shimmer的字形扫光，保留完整文本和颜色。测试发现无限动画必须在composition前冻结时钟，否则Compose测试策略可能取消动画；修正测试时钟后，Style版rememberShimmerStyle在当前文本路径仍没有实际像素变化，而同一时钟条件下绘制版Modifier产生变化。故采用已有绘制版，不修改共享Text或Shimmer。该Style路径问题留待Shimmer本体复查，不据此推断所有Style内容都无效。

新增AttachmentStateTest四项：浅→深→浅及Error→Done实际背景/媒体/描述像素；显式中性描述和嵌套根状态独立；字符串Uploading标题、lambda Processing标题随时间改变，显式Done保持静态，根改Done后停止。加上既有AttachmentScreenshot6、ShimmerScreenshot2，最终 **12项通过**。JS编译和Web Wasm development distribution通过。没有开启Roborazzi verify/record，没有重录旧基准；保留既有Text.kt、AGP实验选项、Gradle deprecation及webpack警告。测试XML和compact ledger存于attachment-r1。

浏览器实看官网States与本地当前三状态首屏，在1440×1000、390×844及浅深色四象限检查；手机在本地真实横向滚动到错误附件，再切主题并滚回。确认深色卡片底色、浅深错误媒体底色及现有描述显示。原始截图、官网手机DOM/CSS测量、验证范围和hash见 [attachment-r1/evidence.json](web-parity/attachment-r1/evidence.json)。这些证据不等于完整五态示例或整页已对齐。

明确剩余：迁移官网Default/Image/五States/Sizes/Group/Trigger示例和真实图片；当前图片仍是emoji，占位与源码导入需替换。根Style覆盖、宽度约束、内容弹性及动作区需要逐项修正，三种size的间距/字号/媒体尺寸、vertical布局及image variant尚未对齐。Trigger全卡预览与动作独立点击/键盘/标签、焦点/hover/disabled，Group吸附、边缘渐隐及键盘滚动仍未验收。本地“Default error”实际内容为Done/isError=false，待按真实示例结构重组。Attachment继续未勾选，全站及已完成组件复查目标保持，editor跳过。

### Aspect Ratio 第二轮：固定行框、真实字体与三宽复核（2026-10-30）

<!-- progress-created: 2026-10-30; order: 58 -->

关闭第一轮遗留的 RTL caption 行框问题。实际 Compose/Skia 排版不能仅凭 fontSize=14sp、lineHeight=20sp 保证最终20dp：Geist/回退字体会给出21dp或更高的自然文本布局。当前依赖的 Skia 源码也没有消费 LineHeightStyle 的 mode，故没有继续叠加无效参数或修改共享 Text。RTL 使用真实20dp语义容器，内层自然测量并居中绘制；字形允许超出行框，与 CSS 行框行为一致，不裁切 Arabic 上下笔画。新增实际 catalog 字体、四组宽度/主题往返语言回归，检查图片位置不变、caption语义高度20以及整页实际字形与参考绘制一致。

共享 Nova Select 的字符串标签也使用20sp行框，内层保留固有宽度单行及完整字形。真实 Noto Sans Arabic 测试先复现选项高度38dp，再验证修正为28dp；点击行继续持有名称、选中及点击语义。自定义内容槽与其他preset沿用原路径，公共签名未改。新增字体及OFL许可仅位于JVM测试资源，不进入应用/发布库；两个新的浅深Arabic菜单像素基准已人工查看。

最终53项功能/布局回归通过：AspectRatioLayout3、AspectRatioScreenshot2、SelectLabelLayout5、ChoiceNova27、AspectRatioExamples5、CatalogPreview5、CatalogNavigation6。单独显式Roborazzi verify四张基准unchanged（两张既有Aspect Ratio、两张新Arabic选项）；没有重录旧基准，也不据普通ChoiceNova成功宣称其历史像素全部通过。JS编译和Web Wasm development distribution通过。保留既有Text.kt INVISIBLE_REFERENCE、AGP实验选项、Gradle deprecation及webpack警告。结果XML、compact Gradle ledger、像素收据和源码hash均在 [aspect-ratio-r2/evidence.json](web-parity/aspect-ratio-r2/evidence.json)。

浏览器补齐1440×1000、1095×687、390×844，浅深色及Arabic/English/Hebrew切换，共18张官网RTL与18张本地RTL；另含六张本地语言菜单、六张紧凑宽静态示例、两张官网紧凑宽整页和两张手机展开源码前后截图，共52张。八张contact来自原图裁切。官网语言选择由真实键盘操作完成并关闭菜单；本地实际选择Hebrew后展开代码，caption与选择状态保持。复核发现原本标为desktop/light/ar的本地图实际上停在Default，已用真实RTL选择重新捕获并重建对照图；最终12组“来源×宽度×主题”均验证三语图片top一致。其他静态示例的桌面/手机证据延续第一轮，紧凑宽本轮补验。

剩余差异已说明：同一原图像素在sRGB canvas灰度为204、Display P3为210，与本地/官网截图采样相符，说明本例滤镜工作色域造成浅色明度差；不通过换图、烘焙P3灰图或改全局主题掩盖。该结论只限本次同源像素实验，不推断所有组件色彩管线。手机官网图片CSS高146.25px，截图146/147，Compose整数布局146；caption均为20。字形和亚像素抗锯齿不宣称字节一致。RTL章节定位后图片top在官网/本地桌面及紧凑宽为306/285、手机389/366，该共享章节/toolbar/滚动定位差异继续归P1；正文、安装和代码区排版也仍待全站关闭。Kotlin ratio默认1f及离线图片是明确适配。Aspect Ratio本体和四份示例复核勾选，Select、全站P1/P3、已完成组件的复查范围继续保留，editor跳过。

### Drawer：真实拖动不依赖动画时钟（2026-10-30）

<!-- progress-created: 2026-10-30; order: 57 -->

保留现有绘制阶段直接读取面板 anchored offset 的实现；补充 API 说明，明确页面缩放、圆角随拖动与回弹变化。加强四向普通面板和 snap 面板的五个真实触摸用例：按住拖动后冻结动画时钟，再继续移动及反向移动，检查面板与页面都立即响应；恢复时钟后低速松手，检查共同回弹。这样同时覆盖实际手势接线与 Host 渲染，避免仅通过模拟进度验证。

继续核验时确认上述接线已在当前代码生效，无需再次修改运行时动画；复跑三个测试类仍为 17 项、0 失败。同步更新 Indent effect 的实际预览和展示代码说明，提示拖动会连续还原页面缩放与圆角，松手共同回弹。此处 snap 规则是最低档以上保持内缩，仅最低档至关闭区间逐步还原。

本次 DrawerIndentTest 10、DrawerMotionTest 2、DrawerDismissTest 5，共 **17 项通过**；Web Wasm development distribution 构建通过，可在 `/drawer` 的 Indent effect 示例查看。保留既有 Text.kt INVISIBLE_REFERENCE 和 webpack 警告。未新增浏览器截图或重录像素基准；浏览器证据仍以上述已记录截图为准。

#### 补充：越过阈值松手关闭（2026-10-30）

当前运行时已直接读取面板位移驱动页面缩放和圆角，本次未重复改动接线。新增真实触摸回归 `dismissingDragRestoresPageDuringSettleAndAfterRemoval`：冻结动画时钟，向下拖过关闭阈值，确认按住时页面已还原；松手推进 64ms 后确认页面继续还原，最终检查关闭回调及页面恢复至完整 400dp 宽度。结合既有四方向、snap、反向拖动和回弹测试，DrawerIndentTest 11、DrawerMotionTest 2、DrawerDismissTest 5，共 **18 项通过**；Web Wasm development distribution 构建通过。未新增浏览器或真机验证，未重录像素基准。

### Aspect Ratio：真实图片、响应式配方与共享 Select 标签（2026-10-30，尚未关闭）

<!-- progress-created: 2026-10-30; order: 56 -->

重新读取当前 Base wrapper、四份 examples、文档及 Select 源码，并用官网真实 DOM/CSS 校验。当前 Base AspectRatio 是 CSS aspect-ratio 包装，不再是旧 KDoc 所述的 Radix 包装；Kotlin 保留既有 ratio=1f 默认值，并在 API 中说明与网页 ratio 必填的区别。复现 Style { width(160.dp) } 在 400dp 父容器下仍测为 400dp，调整为先应用 Style 约束、再测比例，签名不变。新增回归覆盖父宽及比例实时改变、16:9/1:1/9:16、子内容撑满和 Kotlin 默认值。

目录原来的固定宽度灰色占位替换为官网 Default、Square、Portrait、RTL。三种最大宽度为384/192/160dp；普通预览288dp高，Portrait和RTL主体384dp高，图片 Crop、10dp圆角、grayscale与深色20%亮度均按官网处理。图源实际为 avatar.vercel.sh/shadcn1；原始 PNG、curl 优化 PNG、浏览器实际协商图片的压缩纹理不同，最后从官网已加载 img 解码120×120原图（未应用CSS滤镜），作为本地资源打包，灰度和亮度仍在绘制时应用。文件来源与中间版本均保存，不新增网络图片加载依赖。KDoc及AGENTS中的旧占位/依赖说明已更新。

预览边框此前只绘制、不占布局，手机图片为262dp，官网为260px。共享 PreviewCodeSection 为1dp边框预留布局，新增完整预览框测试验证390宽、页面两侧24、边框1、预览左右40后的真实260×146dp图片。该共享更改只通过本轮目录回归，不能据此关闭其他组件。

RTL提供Arabic/English/Hebrew的真实caption及LocalLayoutDirection。浏览器发现共享Select会把希伯来语标签折为两行；重新实测官网弹层宽144、高92，三项均28高，确认需要保留anchor宽度，使用源码ItemText的shrink-0/whitespace-nowrap行为。Nova标签保留行内弹性分配，但内层按固有宽度单行测量，由滚动视口裁切过长文本；行间gap从8改为6dp。新增希伯来语/长标签行数、28dp行高、144dp弹层及方向键/Enter提交回归。其他preset保持原测量路径，Select完整验收仍未关闭。

最终 **49项功能/布局回归通过**：AspectRatioLayout3、AspectRatioScreenshot2、SelectLabelLayout2、ChoiceNova27、AspectRatioExamples4、CatalogPreview5、CatalogNavigation6。另单独显式Roborazzi verify验证两张既有AspectRatio基准unchanged，未重录；普通ChoiceNova运行不代表其旧像素基准已通过。Web development distribution通过。保留既有Text.kt INVISIBLE_REFERENCE、AGP实验选项、Gradle deprecation和webpack警告。原始测试XML、精确用例和Gradle收据见web-parity/aspect-ratio-r1/。

浏览器查看官网1440×900桌面/390×844手机浅深色整页，以及本地四个示例全部16张四象限画面；四张contact仅为原始截图裁切并排。手机深色实际打开语言菜单，通过方向键/Enter依次选English、Hebrew，caption正确改变，三条标签保持单行。首次自动截图因整页导航重置临时主题而取到浅色，已改为页内导航/滚动后重截并逐张确认最终浅深色；早期错色/未加载帧不列为最终证据。官网原生弹层曾因目标离开视口及文本子节点被遮挡导致驱动点击失败，本轮只以真实DOM尺寸与源码核对官网菜单，不宣称已完成官网语言选择的键盘验收。证据清单为web-parity/aspect-ratio-r1/evidence.json。

**真实剩余差异**：Arabic与English/Hebrew下，caption实际行框使图片垂直位置不同；官网Arabic figure为216+8+20=244px，而本地显式14/20sp声明尚不能证明实际行框一致。需继续隔离字体回退/行高，不能用固定声明或语义文本存在关闭此项。页面骨架起点、说明换行、代码/安装区排版等共享差异也保留；Kotlin API和离线图片是明确适配。Aspect Ratio保持未勾选，其他未完成及已完成组件的全站复查范围不变，editor继续跳过。

### Drawer：往返拖动内缩回归（2026-10-30）

<!-- progress-created: 2026-10-30; order: 55 -->

继续核对当前直接读取 anchored 手势比例的实现，没有新增第二层缓动。补强四向及 snap drawer 的真实触摸测试：按住下继续沿关闭方向拖动，再反向移回原位置，分别断言页面宽度和面板内部内容的位置同步恢复，最后低速松手回弹。面板位置必须测量 offset 内的子内容；调用方的 testTag 在 offset 之前，测量它会得到未平移的位置，不能作为实际面板移动证据。

DrawerIndentTest 10、DrawerMotionTest 2、DrawerDismissTest 5，共 **17 项通过**；Web Wasm development distribution 构建通过。浏览器本地桌面复核实际按住下拖、反向拖回、松手回弹，证据为 `web-parity/drawer-indent-reverse-open.png`、`drawer-indent-reverse-held.png`、`drawer-indent-reverse-returning.png`、`drawer-indent-reverse-settled.png`。本轮补强测试和验证记录，未修改已接通的运行时代码，未新增手机或真机验收，未更新像素基准。

### Create 财务预览：响应式卡片、真实 Payout 与输入保留（2026-10-30，Card/Field/Slider 继续未关闭）

<!-- progress-created: 2026-10-30; order: 54 -->

读取官方 preview-02/index.tsx、Preview iframe 容器、Card、Contribution/Payout 源码并测量真实 DOM/CSS。官网 Nova 财务网格在 iframe 宽度小于 768 时为 2400px/16px gap，普通桌面为 3000px/40px gap；3xl CSSOM 明确为 min-width:1600px，此时 gap48。七列扣除两侧 padding 及六个 gap 后计算列宽，每列另有 4px 内边距；实际桌面 iframe1176 下卡片宽 374.8516px。Lyra/Mira 的 md 分支为 2600px/gap24。原本每列固定320dp、全局24dp内边距，导致 Payout 主体拥挤；现在按来源计算财务网格，双列区域及内部子列同步计算，第二套预览保留原布局路径。

官网 iframe 文档高度2322/client806，垂直滚动作用于整个网格。本地财务列改为共享纵向滚动；横向与纵向使用两个原生布局节点，避免把两个 scroll modifier 叠在同一节点后纵向 ScrollBy 语义动作被遮蔽。真实滑动和 ScrollBy 都验证各列同步。响应式摘要使用预览容器自己的宽度，模拟网页 iframe 内的 md，而非外层窗口宽度。暗色财务画布也从 muted/30 修正为源码 dark:bg-background，浅→深→浅像素检查通过。

Contribution 改用 CardContent/CardFooter 结构，摘要原文为 Upcoming / May 25, 2024 / $1,000 scheduled 与 Auto-Save Plan / Accelerated / Recurring weekly，手机纵排、桌面两列，间隔12dp。背景 muted/50、圆角10dp、内边距横12/纵10及显式字体大小/行高取自官网测量；不是据声明值宣称实际字体绘制完全一致。View Full Report 的背景真实铺满页脚可用宽度，测试同时检查几何与左端内部像素，避免只扩点击区。

Payout 修正更多功能差异：货币由固定值/no-op 回调改为真实本地选择状态；静态 Progress 改为 Slider，50..10000、step50、默认2500，动态显示两位小数、24sp 半粗 tabular 数字，label 加权、金额保持单行并按基线排布；Notes 最小外框100dp且保留输入。Header 使用独立 Title/Description/Action，X 使用原来源的 muted Ghost 图标按钮，Save Threshold 在真实 CardFooter 内撑满。官网 X、Save Threshold、View Full Report 均没有业务 handler，本地同样没有伪造保存/关闭结果。

浏览器又发现桌面→手机分支重建了 PreviewCanvas，虽然 chartColor 已提升到 CreatePage，Payout 内部普通 remember 状态仍会重置。现在 CreatePage 以记住的 movableContentOf 在两个布局分支移动同一预览内容，rememberUpdatedState 保持图表参数更新，卡片自己的局部状态仍由卡片持有。新增真实页面回归，先选 EUR、设10000、填备注，再切 1000→390→1000，三个值均保留；现有 Chart Color 在同一移动内容上继续更新并通过。

**最终21项通过**：FinancePreview6、CreateChartColor4、CatalogPreview5、CatalogNavigation6；Web development distribution 通过。新增测试涵盖 768/1600 边界及七列总宽、摘要往返断点、muted/50 与全宽按钮实际像素、EUR/Notes 状态、Slider SetProgress/方向键/Home/End、最大金额单行不重叠、100dp 外框完整 layout 尺寸、共享滑动及滚动语义、暗色画布、页面跨分支移动的输入保留。BasicTextField 的内部编辑语义边界仅82dp，实际 layout 含边框/内边距为100dp，未拿内部高度误判绘制外框。未修改 core 公共 API，未重录像素基准、未执行全项目像素验证；保留既有 AGP/Gradle/webpack 警告。

浏览器对官网和本地均实际选 EUR、用 End 调到10000、输入 Finance preview note，并切浅深色；官网通过实际 input[type=range] 键盘目标操作，未用脚本改金额值。检查1440×900桌面、390×844手机的 Contribution，并横向移动到 Payout 查看两种主题；本地先出现输入回默认值的失败观察，修复移动内容后重新截图，以手机 Payout 仍显示 EUR/$10000.00/备注及返回桌面同值为最终证据。来源、精确测试结果、最终构建和截图 hash 见 web-parity/finance-r1/evidence.json，中间 pan/旧状态图不列为最终证据。

剩余：外层 Create 顶栏高度、预览框起点与控制区排版仍不同；实际文本行高/字体度量及摘要整体高度尚未隔离（本地 Contribution 总高度仍略短），不能把显式18/28等声明当作最终像素相等。摘要仍是目录私有布局，待 Item 本体复查时核对其透明 border/完整布局规则和可复用组成。手机 Notes 的原生右下手动 resize、完整标签关联及实际读屏/IME尚未验收；Compose Canvas 与官网 DOM 的 wheel 位移在本次驱动下也不同，待独立隔离。仅默认 Nova 做了完整浏览器矩阵，其他 preset 不据纯计算测试宣称已验收。其余财务卡片仍含占位/简化内容，图表 tooltip、多系列与饼图扇区、菜单临时预览和参数锁定等前述缺口保留。全站及已完成组件复核范围未缩减，editor 继续跳过。

### Create / Chart：独立 Chart Color 与真实六柱（2026-10-30，Chart 未关闭）

<!-- progress-created: 2026-10-30; order: 53 -->

读取官方 chart-color-picker、theme-picker、base-color-picker、picker、design-system-provider、search-params 和 registry/config.ts，确认 buildRegistryTheme 仅以 Chart Color 覆盖 chart1..5，Theme 主色不应随之变化。选项是“当前基础色 + 17 强调色”，不是全部七种基础色同时可选；Base Color 更换时仅将不再有效的旧基础色图表选择回退，新基础色仍保留有效强调色。源文与官网真实六柱颜色/几何保存在 `web-parity/chart-color-r1/`。

将 chartColor 从 Customizer 局部提升到 CreatePage，由显式值/回调同时传给选择器和 PreviewCanvas；改变桌面/手机布局分支不再丢失选择。预览内部只通过局部 chart 角色作用域传递图表颜色，不覆盖按钮和其他控件的 Theme。所有 Shuffle 入口生成当前新基础色下有效的图表选项，Reset 同步恢复 Neutral。Base、Theme、Chart Color 的触发器色样改为官方固定深色来源、16dp，删除上游没有的选项色点；基础色用 mutedForeground，强调色用 primary，不再用 chart1 冒充所选色样。

Contribution History 改为官网 Dec..May 六个月与 800/1100/900/1300/750/1400 数据，使用 chart2、最高 40dp 宽和 Nova 6dp 顶部圆角。原来每个 Column 的唯一弹性子项使用 weight(value)，使每根柱体都填满剩余高度；改为独立绘图区内按值比例填充。保留官方 200dp 总高、8dp 顶部、30dp XAxis 空间，最高柱实际为 162dp，与官网读取的 162px 同数值；真实文本标签为 12sp/18sp。Lyra/Sera 保留源码直角规则。第二预览的私有柱图也修正唯一 weight 子项导致的等高问题；其现有柱图/圆形饼图占位改读 chart 角色。Analytics 折线按官方 chart1、Dividend 迷你柱图按 chart2 消费独立色板，避免首次预览正确而其他真实绘图区仍跟随 Theme。

**15 项定向测试通过**：CreateChartColor4、CatalogPreview5、CatalogNavigation6。独立 fixture 比较全部 24 种图表选择的浅深 chart1..5（240 值）及固定深色色样（24 值）；实际点击 Chart Color=Amber 后改 Theme=Lime、切换 1000→390→1000、浅→深，柱体仍保持 Amber；Base 变化保留强调色、无效基础色回退及菜单 Reset 均验证。几何测试直接量六根柱体高度比例、共同底边、40dp 宽上限及 162dp 最大高；Analytics/Dividend 实际像素验证 Blue→Amber 绑定。测试最初在 Mauve 和 Reset 上误匹配其他已显示文字，随后用基础色标签排除条件和菜单快捷键限定真正选项，未修改组件以迎合测试。Web development distribution 最终通过。未重录像素基准，未运行全项目像素验证；保留既有 AGP/Gradle/webpack 警告。

浏览器选择官网与本地 Theme=Lime / Chart Color=Blue，逐张查看 1440×900 和 390×844 浅深色画面，按钮仍为 Lime，柱图为 Blue；本地另验证换 Theme 后图表仍为 Neutral 的中间状态。官网 DOM 六柱的宽、高、底边和计算 fill 已保存，浅深色均 chart2=Blue500，按钮分别 Lime400/Lime500。一次组合任务因测试选项歧义失败，Webpack 已执行但最终 distribution 尚未同步，浏览器因此仍显示旧菜单色点；测试修正后明确重新分发，再以菜单色点已移除、16dp 色样和最终截图确认新产物。早期错误菜单/旧产物帧仅作为中间诊断，不作为最终证据。最终源码、构建、测试和截图对应关系见 evidence.json。

仍待关闭：Contribution 卡片宽度、摘要原文/手机纵排、footer 撑满及整体排版与官网仍有差异，Payout 金额拥挤仍保留；图表悬停 tooltip、键盘/读屏、桌面菜单 hover/焦点临时预览及锁定参数后 Shuffle 的官方语义尚未补齐。第二预览柱图的真实多系列/数据、饼图扇区，Stock Performance 等文字/图形占位仍需重做，独立色板绑定不能代替完整图表实现。未把目录私有图形修复等同于 core Chart 本页或全部组件验收；所有清单范围不变，editor 继续跳过。

### 共享强调色复核：官方角色覆盖与实时切换（2026-10-30）

<!-- progress-created: 2026-10-30; order: 52 -->

重新下载当前官方 `apps/v4/registry/themes.ts`，校验与上一轮参考一致，保存原文及来源/hash 于 `web-parity/accent-r1/`。现有 Accent 的手调十六进制值与当前官方值不符，并错误地把 ring 改为主色、漏掉 secondary/chart 覆盖；其旧 KDoc 还声称只改 primary，与实际代码也不一致。已替换为精确匹配官方 OKLCH 的命名 TwColors，保留 Accent 枚举名、label 和 applyTo 的公开使用方式。

现在覆盖 primary/primaryForeground、secondary/secondaryForeground、chart1..5、sidebarPrimary/sidebarPrimaryForeground；其他角色继续保留基础色，包括焦点 ring。普通主色浅色 700/深色 800，Lime/Yellow 为 400/500 且文字用 900；图表为 300/500/600/700/800，Sidebar 主色为 600/500，前景色按各自源码单独处理。明确保留上游所有强调色使用 Zinc secondary 的行为，以及 Rose 深色额外 sidebar=Zinc900；没有为了统一规律而删掉这些特例。Accent.Base 直接返回原始 base 对象。AGENTS.md 和 shadcn-parity.md 的“手调色不应迁移”旧结论已修正。

独立 fixture 为 **375 条上游覆盖值**（17 色 × 浅深通常各 11 条，Rose 深色多一条 sidebar）。AccentSourceTest 在 238 种强调色/基础色/模式组合上比较全部 31 个上游角色，既验证明确覆盖，也验证未列出的角色不变，并保留本地额外 destructiveForeground；另验证 14 种 Base 身份不变。修复前有效夹具复现 Amber 浅深色差异，修复后通过。AccentRenderingTest 验证实际按钮背景/内容在 Blue、Lime、Fuchsia、Rose、Amber、Base 的往返切换，含 Neutral/Mauve、浅→深→浅、不改尺寸；恢复 Base 时实际绘制恢复，ring 始终保留基础色。

最终 **52 项定向测试通过**：AccentSource3、AccentRendering3、BaseColorSource2、ThemeRoleRendering4、ColorContrast3、CssStyleable3、FocusVisible5、ButtonNova7、FormNova14、AlertDialogNova4、MediaScreenshot4。两张新的强调色矩阵已查看，包含 Base 与 17 色、真实 Primary/Secondary Button 以及 Sidebar/Chart 色样；首次测试图表头/次按钮文字换行已通过加宽测试列修正，未改生产组件适配测试。显式 Roborazzi verify 共 **16 张基准通过**（两张新图及上轮 14 张旧图），未重录 14 张旧图。色样不等于完整 Sidebar/Chart 组件验收。Web development distribution 构建通过；既有 AGP/Gradle/Text.kt/webpack 警告保留。

浏览器实际选择本地和官网 Blue、Lime，查看桌面浅深色，另查看 Lime 手机浅深色；本地在手机切换主题和横向滚动底部控制区。官方读取到 Lime 主色、文字和 ring 的最终计算样式，核对异步 iframe 更新后再记录，未将短暂仍为 Neutral 的帧视为结果。首次官方浅色 Lime 图仍处在按钮颜色过渡中，查看后改为等待实际 backgroundColor 到达期望值，重截并查看最终帧。官方 Theme 菜单选择后仍会保留打开，初次头部开关点击被遮挡，重新观察并 Escape 关闭菜单后完成切换，未改用脚本直接写样式。截图和清单位于 `web-parity/accent-r1/`。此轮只确认强调色角色及相关切换；手机与桌面的完整 Create 布局、字体、图标及交互未宣称对齐。

新确认的剩余差异：Create 本地 Contribution History 五根柱体等高且直接使用 primary，官方六根不同高度，独立 Chart Color=Neutral 时仍保留灰色图表；本地 Chart Color 状态在 Customizer 内，尚未传入 PreviewCanvas，换 Theme 会错误地连带改变图表。Payout 金额与标签在本地桌面挤压换行，卡片宽度/页脚与手机控制区也存在差异。以上作为后续图表/目录预览工作保留，不能因 token 测试通过而关闭。其余组件清单、历史 25+4 张像素失配、字体/复杂状态/辅助技术等缺口仍在；editor 继续跳过。

### 共享主题复核：七套基础色、透明控件与外环（2026-10-30，组件继续未勾选）

<!-- progress-created: 2026-10-30; order: 51 -->

从官方 `apps/v4/public/r/colors/<base>.json` 读取 Neutral、Stone、Zinc、Mauve、Olive、Mist、Taupe，并读取 `style-nova.css` 与 `themes.ts`；原文和重新联网校验的来源清单位于 `web-parity/theme-dark-r1-source/`。独立 TSV 固定了 31 个上游颜色角色 × 七套基础色 × 浅深两种模式，共 **434 个值**，不从被测 BaseColor 反推期望。`BaseColorSourceTest` 两项逐个精确比较 OKLCH 解析后的 Color（含 alpha）。

修正所有基础色的深色 border/sidebarBorder 为 10% 白、input 为 15% 白；card/popover/sidebar 使用各自 900 色阶，ring 使用 500；浅色 sidebar 使用 50 色阶，浅深 chart1..5 使用各自 300/500/600/700/800，所有深色 sidebarPrimary 使用官方 blue700。没有只修 Alert Dialog 的按钮，也没有把 Neutral 与 Zinc 的近似色当作精确一致。主题文档 `shadcn-parity.md` 的颜色表已重写，移除“不透明深灰足够接近”的旧结论；旧 LightColors/DarkColors 常量不代表当前 Theme 使用的 BaseColor。

Nova Input/Textarea、Checkbox、Radio、Select、InputGroup 恢复真实浅色透明填充、深色 input/30，Select hover 为 input/50；未选中 Switch 使用深色 input/80。移除提前 compositeOver 全局 background 的处理，使控件在 Popover 或有色父容器上按实际底色混合。保留禁用填充及透明度规则。ThemeRoleRenderingTest 四项覆盖七套基础色浅→深→浅、真实 popover 底色、浅色有色父容器透出、禁用填充和 Nova/Vega + Base/Blue 切换中的 input/border 角色；不据此宣称 Accent 已对齐。

透明背景暴露出 native Style dropShadow 的内部实心投影会透过控件填充：焦点 Input 变灰，错误态整块变红。内部 cssStyleable 保留 Style 的真实交互状态和最终 caller shape，使用 Compose 原生 DropShadowPainter、Shape.createOutline 与 ClipOp.Difference 仅绘制轮廓外部；没有重新手绘各圆角几何或增加公共 API。DrawModifierNode 在仅阴影颜色变更时主动失效，避免冻结动画时钟后阴影不刷新。三个测试覆盖浅色焦点/错误态内部不染色、调用者三角形 shape、冻结时钟下 red→blue 阴影更新。AGENTS.md 和 FocusRing KDoc 已记录透明表面的原因与约束。高级阴影动画、externalPadding 等完整覆盖仍未完成。

此前本轮最终定向验证 **43 项通过**：BaseColorSource2、ThemeRoleRendering4、CssStyleable3、FocusVisible5、ButtonNova7、FormNova14、AlertDialogNova4、AlertDialogMediaScreenshot4，显式开启 Roborazzi verify；其中 **14 张受影响/相关基准通过像素验证**，逐张检查后只更新 10 张，其余四张保留原文件。前后 hash 和记录/验证命令分别保存为 theme-dark-r1-baselines-{before,after}.json、theme-dark-r1-validation.json。此前 ChoiceNova27、ButtonGroupScreenshot6、AlertDialogExamples5 普通测试亦通过，未据普通运行声称它们的旧基准已通过像素比较。Web development distribution 构建通过；后续 Drawer 修改已另外运行其 17 项测试和 Web 构建，本节没有把它们混成同一轮 43 项。

浏览器：查看七个实际控件页面 Button/Input/Checkbox/Radio Group/Switch/Select/Input Group 的 28 张默认画面，覆盖 1440×900 桌面、390×844 手机和浅深色；六张 contact 图仅做裁切并排阅读，原始截图完整保留。另实际输入 demo（密码字段显示四个掩码）、切换主题并重新聚焦，逐张查看 Input 四张焦点画面：填充不受外环染色，文字和输入值保留。打开 Select 并 Escape 关闭，查看四张弹层图；打开默认 Alert Dialog 并 Escape 关闭，查看四张图，桌面行式和手机反向纵排页脚保留，深色 Outline 的填充与边框已分开。文件前缀 theme-dark-r1，索引见 browser/interactions/alert-browser 三份 JSON 与 evidence.json。此次未重跑所有官网示例，参考第二轮官方四象限截图及本轮重新校验的官方原始令牌/CSS；这些共享回归不等于每个控件完整验收。

独立 Accent 覆盖的 17 色矩阵与官方 themes.ts 角色映射已在下一节补齐。仍待关闭：其余组件受共享色板影响的像素基准、现有 Chip/Tabs/Slider/Sidebar 的 25 项历史失配及四张旧 Vega AlertDialog 失配；Alert Dialog 字体/换行、辅助技术、复杂自定义槽位继续保留。未执行全项目像素验证，未把旧差异自动重录为新基准。保留既有 Text.kt INVISIBLE_REFERENCE、AGP 实验选项、Gradle deprecation 与 webpack 警告。清单所有未完成项及已完成组件复查范围不变，editor 继续跳过。

### Drawer：内缩手势完整区间同步（2026-10-30）

<!-- progress-created: 2026-10-30; order: 50 -->

补齐真实手势进度到达 100% 时的边界：之前 graphicsLayer 在比例等于 1 时切回 Animatable，往返拖动后可能短暂使用旧比例。现在以手势进度是否就绪区分驱动方式，真实 anchored offset 在完整 0..1 区间直接驱动页面缩放及圆角；尚未建立手势锚点或没有面板的场景保留开关动画。未就绪以 null 表示，不再与完全展开的 1 混用。四向、snap 最低档以下的关闭区间、多 Drawer 取最大比例保持原规则。

扩展冻结动画时钟的现有渲染测试，依次验证 0.75、0.25、0.6、1、0、1，直接测量页面宽度。修复前在 1.0 复现失败：预期 376，实际 385.6；修复后通过。DrawerIndentTest 10 项、DrawerMotionTest 2 项、DrawerDismissTest 5 项，共 **17 项通过**；Web Wasm development distribution 构建通过。本次未新增浏览器手势或真机验证，未重录像素基准。仍有既有 Text.kt INVISIBLE_REFERENCE 与 webpack 警告。

### Alert Dialog 第二轮：官方示例、Media 与三语预览（2026-10-30，尚未关闭）

<!-- progress-created: 2026-10-30; order: 49 -->

从官方仓库树定位 `apps/v4/examples/base/alert-dialog-*.tsx`，读取 demo、basic、small、media、small-media、destructive、rtl 七份源码；保留在 `web-parity/alert-dialog-r2-source/`，来源与校验值见 `alert-dialog-r2-evidence.json`。目录原来的重复 API 演示收敛为官网七组 Default、Basic、Small、Media、Small with Media、Destructive、RTL；公开 API 没有移除，Compose 的 modal=false、可选关闭按钮、outside-dismiss 回调及 Lg 等扩展在 API 文字中明确标注。Basic 与 Default 的说明文字按各自源码保留差别，没有混用。

补上 Composition 和 Kotlin API 小节，Usage 导入/调用分开复制；Media 用 CircleFadingPlus，Small with Media 用 Bluetooth，Destructive 用 Trash2，与上游图标一致，core 没有增加图标集依赖。RTL 使用英文、阿拉伯文、希伯来文原始译文，并同时提供普通与带 Media 的 Small 弹层；目录语言工具栏沿用共用 RtlPreview，导出代码展示作用域独立的阿拉伯语两弹层配方，文字中说明目录另提供三语切换。

Media Header 不再始终纵排：Nova 默认桌面为起始侧媒体列及正文列，间隔 16dp；40dp Media 带 8dp 底部留白，标题/正文间隔 6dp。手机及 Sm 上下居中，Media 到标题合计 14dp。使用内部 layoutId 标识媒体，无新增公开开关/签名；逻辑起始侧随 LayoutDirection 变化。移除媒体后直接恢复普通 Header，不保留旧列宽；无界宽度按自然内容测量。Media 默认 inline Icon 为 24dp，Style 覆盖仍可用于 destructive 底色。

Destructive 正文用 Header 内的 BasicText 组合保留 Settings 真链接及下划线；默认继承 mutedForeground，悬停才变前景色。上游 href="#" 是当前页面占位链接，本地目录桥接到空 fragment，没有伪造设置页面或触发 Delete。JVM 实际点击链接验证导航请求且弹层仍显示；浏览器手机真实点击也从 `?preview=r2-settings-link#destructive` 变为同 query 的 `#`，背景回到页首，确认框保持打开且 Cancel 仍可关闭，见 settings-link.json 及两张对应截图。

**去重 56 项通过**：AlertDialogNova4、Media3、MediaScreenshot4、AlertDialogExamples5、ComponentDocumentation5、CatalogNavigation6、SurfacesCompletion29。覆盖媒体 40dp/inline 24dp、16dp 列间距、LTR→RTL→手机→移除媒体、Small 在桌面居中、无界测量；六组普通示例分别实际取消/确认；阿拉伯语、英语、希伯来语各自打开两种尺寸；Settings 点击、导出结构和弹层保持打开时 destructive 媒体背景浅→深→浅实际像素。新增四张 Media/Small Media 浅深基准并逐张查看；四张新基准与第一轮两张 Nova 默认基准的显式 Roborazzi verify 均通过。旧 Vega 四张失配继续保留，没有重录或宣布解决；SurfacesCompletion 普通测试通过不等于其全部旧图已显式像素验证。最终 8765 实际服务的 development distribution 构建通过，既有 AGP、Gradle deprecated features、webpack 等警告保留。

浏览器：官网与本地各 28 张新弹层图，1440×900 桌面、390×844 手机、浅深色，覆盖 Basic、Small、Media、Small with Media、Destructive、RTL 普通和 RTL Small；Default 沿用第一轮矩阵并保留 Nova 像素回归。原始全屏文件为 `alert-dialog-r2-{official,local}-{desktop,phone}-{light,dark}-*.png`，四张 compare 文件将中心面板裁切并排，已逐项查看，不能当作像素 diff。桌面/手机本地都实际打开并使用 Escape 关闭，在同一预览切换浅深色；官网的触发器有唯一来源元素。一次官网长距离自动滚动发生 CDP 输入超时，确认终止状态后改用章节链接直接定位，后续全部成功。手机 RTL 普通弹层最初与鼠标位置重叠产生 action hover，移开鼠标重截浅深两张，未将悬停灰色误记为基础色缺陷。截图尺寸和完整清单已检查写入 evidence.json。

仍待关闭：并排图显示 CSS text-balance/text-pretty 与自然换行、部分字重/行高及面板总高度差异，阿拉伯语回退字体度量也不同；不能据几何测试宣称字体完全一致。深色 Outline 底色/边框有共用色板差异：官网实测 --input 为 15% 白、--border 为 10% 白（`alert-dialog-r2-official-dark-tokens.json`），本地 BaseColor.baseDark 仍令 input=border（不透明深灰）；后续应从主题角色修正，不能仅给 Alert Dialog 按钮硬编码。alertdialog 角色、标题/说明关联、完整键盘闭环及实际读屏输出仍需端到端核对；自定义/嵌套 Header 超出官方 Media→Title→Description 组合的完整矩阵也未宣称完成。旧 Vega 四张失配未隔离解决。Alert Dialog 保持未勾选，全站和已完成组件复核范围不变，editor 继续跳过。

### Alert Dialog 第一轮：默认弹层与响应式布局（2026-10-30，尚未关闭）

<!-- progress-created: 2026-10-30; order: 48 -->

对照当前 [Base 源码](https://raw.githubusercontent.com/shadcn-ui/ui/main/apps/v4/registry/bases/base/ui/alert-dialog.tsx) 和官网实际 DOM/CSS，保存 `web-parity/alert-dialog-r1-official.json`。默认面板官网桌面 384px、手机 320px；Header gap 6px，标题 16px/24px；默认手机正文居中，桌面左对齐，Small 两种视口均居中。官网默认 Continue 是普通主按钮，完整说明以 “from our servers.” 结尾，本地默认预览、导出代码和 Usage 已同步修正。

AlertDialog 不再完全照搬 Dialog Header/Title：Nova 实现 6dp 间距、16sp/24sp 标题、紧凑 Header 和说明居中；Content 透传当前 size，受控 Default→Sm→Default 时对齐随之更新。默认手机面板设为 320dp，保留 DialogContent 原有视口边距和调用者样式覆盖。Nova 使用 1dp 外圈并移除占布局的边框；实测发现此版本 Style API 将 0.dp 识别为 Hairline，依据安装的 ResolvedStyle 源码改用 Dp.Unspecified 清除。没有修改 Dialog 的共用样式传递规则或公开函数签名，其他 preset 继续原有路径。

Nova 页脚在手机默认尺寸下按反向顺序纵排，操作按钮撑满可用宽度；Small 固定两列等宽，多于两项按行继续排列，保持 8dp 间距和逻辑方向。保留 RowScope 内容 API。真实手机截图发现只扩外层点击区域不会扩 Button 背景，因此在紧凑页脚通过局部样式给 Action/Cancel 内层 chrome 设置比例宽度；新增实际背景像素断言，避免以 semantics 宽度代替可见背景宽度。

最终普通定向测试 **37 项通过**：AlertDialogNova4、AlertDialogScreenshot4、SurfacesCompletion29。新测试覆盖标题真实文字行高、6dp 间距、大小切换及居中、Small 动态三项仍为两列、手机按钮反向顺序、实际背景像素、两个关闭回调及手机→桌面布局切换。新增并查看 Nova 默认弹层浅深两张基准，显式 Roborazzi verify 通过。旧 Vega AlertDialogScreenshot 的 4 张显式像素 verify 失配，已查看其中浅色普通、深色带 X 对照图，涉及尺寸/色彩等差异；未重录或声称原因已隔离，原始比较图复制为 `alert-dialog-r1-legacy-*.png`。本轮不把普通测试通过等同旧像素全部通过。

较早版本的 Web production distribution 构建通过；最终修复后重新构建了实际用于 8765 的 development distribution。浏览器首次检查发现服务目录仍是旧 development 产物，随后明确执行 wasmJsBrowserDevelopmentExecutableDistribution，并以更新后的页面说明确认正在查看新产物。最终 1440×900 与 390×844 浅深色默认弹层均已截图查看，见 `alert-dialog-r1-local-{desktop,phone}-{light,dark}.png`；官网对应四象限另存 official 文件。实际点击手机 Cancel/Continue、桌面 Cancel 均关闭；桌面外部点击仍保持，Escape 关闭，截图见 phone-confirmed、desktop-outside、desktop-escaped。加载/重绘期间出现的临时不完整字形未作为最终截图；最终帧确认 Continue 完整可见。汇总见 `web-parity/alert-dialog-r1-evidence.json`。

真实剩余：目录仍保留原有扩展示例，尚未替换成官网 Basic、Small、Media、Small with Media、Destructive、RTL 的完整集合，也缺 Composition/API 小节；Media Header 的桌面两列规则未完成；CSS text-balance/text-pretty 与本地自然换行不同，字重和面板总高度等细节继续核对。官网 role=alertdialog，aria-labelledby/aria-describedby 指向标题/说明，初始焦点为 Cancel；本轮只读取官网依据，尚未完成原生与 Web 对应语义及真实读屏输出的端到端验证。旧 Vega 四张基准失配保持未关闭，未更新其他组件基准，未执行全项目像素验证。既有 INVISIBLE_REFERENCE、AGP 实验选项、Gradle deprecated features 与 webpack 警告保留。Alert Dialog 继续未勾选，editor 继续跳过。

### Alert 第九轮：操作区约束、定位与多按钮示例（2026-10-30，尚未关闭）

<!-- progress-created: 2026-10-30; order: 47 -->

读取当前 Base Alert 源码及官网真实 Nova 样式，确认 AlertAction 是绝对定位，根容器在有 action 时固定 pr-18（72px），并不按操作内容宽度自动扩大留白。对现有官网 Action 临时克隆、设置 300px 根宽、分别放入 56×24 与 136×24 的操作块后立即移除：正文宽均为 216px，操作距外边界 top/right 均为 9px；紧凑操作与正文相隔 8px，宽操作与正文重叠 72px。原始结果见 `web-parity/alert-r9-official-action-geometry.json`。这是上游固定留白约束，未把它误改为自动撑宽/换行。

发现本地 Nova 把 top/right 8px 按外边界处理，忽略了绝对定位包含块内的 1px 边框。按项目同数值 dp/px 约定，将布局定位从横向补偿 3dp、纵向 -1dp 修正为 2dp、0dp，使默认边框下距外框为 9dp；不改变正文默认 62dp 额外留白、无操作区像素或公开签名。KDoc/API 说明明确固定留白、多个控件放在同一 action slot、宽内容由调用者安排正文空间。

新增明确标为 Compose extension 的 Multiple actions (Compose) 示例：136dp Row 含两枚 64dp 按钮、间距 8dp；正文 Column 增加 80dp end padding（136 - 紧凑操作 56），继续保持 8dp 避让。Save/Undo 真实更新消息，源码、预览和 Copy Page 一致。此处演示调用者布局，不把项目扩展示例冒充官网已有演示，也未增加布局策略开关或新组件 API。Alert 示例总数从九组增至十组。

RTL 保留现有逻辑 end 定位并新增重组/点击几何回归；官网 RTL 示例的实际 root 使用 text-start/pe-18 逻辑规则。官网 RTL 演示未放 Action，因此本轮不声称完成其 Action 端到端逐像素对照；普通 RTL 图标/正文的既有验证范围也未扩大。

验证 **39 项通过（去重计数）**：AlertNova8、AlertExamples5、ComponentDocumentation5、CatalogNavigation6、DocParagraph3、DocHeading2、DocCodeBlock4，加 NovaActionScreenshot2 与既有 AlertScreenshot4。新测试检查 9dp 外框距离、216dp 正文宽与上游固定留白行为；292dp 组件下多操作正文避让、LTR→RTL 切换和重组后两枚按钮仍可用；实际目录示例也验证 Save/Undo 消息与避让。Web Wasm distribution 通过。

新增并逐张查看 `shadcn/core/src/jvmTest/snapshots/alert_nova_actions_{light,dark}.png`，覆盖紧凑操作、多按钮和 RTL 逻辑 end。两张新基准及四张旧 Alert 基准的显式 Roborazzi verify 均通过；未更新其他像素基准，未执行全项目像素验证。保留既有 INVISIBLE_REFERENCE、AGP 实验选项、Gradle deprecated features 和 webpack 警告。

浏览器检查了 1440×900 桌面、390×844 手机浅深色的普通 Action 和 Multiple actions (Compose)。实际点击 Save/Undo，消息分别变成 Save selected./Undo selected.，正文没有覆盖按钮，主题切换后状态保留。证据 `alert-r9-{desktop,phone}-{light,dark}-compact.png`、`alert-r9-{desktop,phone}-{light,dark}-actions.png`、`alert-r9-{desktop,phone}-light-{saved,undo}.png`。多按钮示例在手机消息变短时高度随内容自然缩小；截图未把这视作按钮丢失或重叠。

仍待关闭：任意超高操作/复杂嵌套槽位、RTL Action 的真实官网组合对照、全文法高亮与其余代码/API 细节、窗口和内容重排后的锚定、分段文字选择和实际辅助技术输出。未把固定 72px 上游约束继续列成待修 bug，也不把一个已验证的多按钮配方等同于所有自定义操作都已验收。Alert 保持未勾选，全站范围不变，editor 继续跳过。

### Alert 第八轮：内联代码、标题 permalink 与复制图标（2026-10-30，尚未关闭）

<!-- progress-created: 2026-10-30; order: 46 -->

补入内联代码背景：沿用上一轮实测的段落 85% 字号、Geist Mono；水平/垂直 padding 为代码字号的 0.3/0.125 倍，圆角为 0.35 倍，背景为 muted。用保留原始文字的 inline placeholders 提供字距/换行空间，在实际文字绘制阶段聚合同一代码片段、同一视觉行的背景。遵循官网实际 box-decoration-break:slice，只给整段起止端圆角。空格和连字符可换行；长标识符以文字布局提供的边界拆分，避免超宽占位整体消失。段落提供 SelectionContainer；链接仍为原始 LinkAnnotation.Url，原文不加入隐藏空格或额外符号。

像素测试和手机浏览器检查发现并修复两项实际问题：按字符 offset 合并背景会把不同视觉行合成一个大矩形，改按实际 placeholder 顶部聚合；占位宽度和边距的独立像素舍入会让最后字符被裁掉，改用同一像素边距计算宽度且片段自身不二次换行。TextLayoutResult 状态只在 drawBehind 读取，不传回 composition 或用于控制测量尺寸；未把此结构检查称为性能基准。

章节标题复用现有章节/URL 导航桥：Installation、Usage、Composition、示例标题、API Reference 及四个 API 子节具有对应可读 fragment；标题文本是可键盘访问的链接，hover 时下划线并显示相距 8dp 的 # 标记，键盘焦点也显示标记。# 本身可用指针点击，但从语义树清除且不建立第二个键盘停靠点。实际标题点击从 #basic 更新为 #destructive，并滚到该章节；不伪造不存在的锚点。标题下划线使用 Compose 原生 TextDecoration，尚未声明与 CSS underline-offset:4px 逐像素一致。

代码复制/成功图标改用官网 copy-button 的 Tabler outline SVG 路径，不再使用 Lucide 近似；仍以现有 16dp 图标/28dp 按钮呈现，无新增图标库运行时依赖。上游 [MIT 许可](https://github.com/tabler/tabler-icons/blob/main/LICENSE) 保存在 app/shared/src/commonMain/composeResources/files/licenses/tabler-icons.txt；已检查 Web distribution 中相应文件实际存在。目录原有其他 Lucide 示例图标未因此替换。

验证 **24 项通过**：DocParagraph3（保留原始文字/跨行及圆角像素、长标识符不消失、真实链接回调和浅深往返背景）、DocHeading2（可读 URL 元数据/指针点击/Tab+Enter、# 不重复朗读的语义结构）、DocCodeBlock4、AlertExamples4、ComponentDocumentation5、CatalogNavigation6。真实页面测试补查标题点击确实进入 DocNavigation 并更新请求版本。最终 Web Wasm distribution 通过；未改 core 组件、未重录像素基准、未执行全量显式像素 verify。最终构建仍有既有 webpack 警告。

浏览器最终矩阵：1440×900 桌面与 390×844 手机浅深色，检查 Destructive 跨行代码、Action 单段代码、Custom Colors 多个代码片段、RTL 代码/链接段落，见 `alert-r8-{desktop,phone}-{light,dark}-{inline,colors-inline}.png`。`alert-r8-heading-hover.png` 记录悬停下划线和 #，点击后真实 URL 为 `/alert?preview=r8-heading-inspect#destructive`；`alert-r8-{copy,copied}-glyph.png` 记录实际复制反馈。新页面在第一次 Canvas 渲染后等待章节定位完成，再捕获最终帧，未把加载首帧误作深链接失败。

剩余范围：轻量 Kotlin 高亮的完整语法、代码/安装/API 的其余细微排版、原生文字下划线定位与 DOM 的细节差异、窗口/复杂内容重排后的锚定、超宽操作区与复杂槽位、实际读屏输出。inline placeholder 的分段选择不等同 DOM 任意单字符选择，仍需跨平台人工验收；本轮保留原文和语义测试不替代该验收。历史像素基准差异继续保留。Alert 继续未勾选，全站及已完成组件复核范围不变，editor 继续跳过。

### Alert 第七轮：代码框、安装区与 Usage（2026-10-30，尚未关闭）

<!-- progress-created: 2026-10-30; order: 45 -->

对照官网 DOM 实测修正目录共用 CodeBlock：无额外 Kotlin/Copy 文字栏；独立代码框圆角 18dp、无描边；纯代码水平/垂直留白 16/14dp；字号 14sp，普通行高 24.5sp，带行号行按官网 24.5px 加上下 1px 使用 26.5sp；行号 gutter 64dp、右侧留白 24dp。代码区使用官网实际渲染的浅色 #F8F8F8 / 深色 #161616，避免混用 muted 背景；Collapsed preview 渐隐同步使用该背景。内嵌示例代码传 RectangleShape，由预览外框处理底部圆角，分隔线处不再叠加独立代码框圆角。

复制改为右上角 28dp 图标按钮，距上/右 12/8dp，图标 16dp；成功切为勾号，提供 Copy/Copied 可访问名称与 polite live region。原 Copy Page 的文字动画仍保留。横向滚动只移动代码，行号和复制按钮不动；展开示例的纵向滚动放在代码内容内部，复制按钮留在代码外框右上。高度仍为实测的 288dp，未按 Tailwind 类名 max-h-96 误推为 384dp。滚动至代码边界后余量可继续传给页面，按钮随整个代码框移动，但不随代码内容移出。

安装区仍展示项目 Gradle 坐标与 commonMain 配置，用 build.gradle.kts 文件标题行区分上下文，没有伪造 npm/pnpm/yarn/bun 安装命令或不可用的包管理切换。Alert Usage 分为可复制的导入和调用两块；新增可空 usageImportsCode，Copy Page 同时导出两块，其他组件未提供时不产生空块。安装/Usage/Composition 内的标题到代码、连续代码间距按桌面和手机实测统一为 17.5dp；未据此修改尚未量取的 API 子节间距。

高亮的关键词/字符串/数字/注释等颜色向官网 github-light-default / vesper 语义主题靠齐；Vesper 注释保留上游 #8b8b8b94 的透明度。上游核对来源为 [github-light-default](https://raw.githubusercontent.com/shikijs/textmate-grammars-themes/main/packages/tm-themes/themes/github-light-default.json)、[vesper](https://raw.githubusercontent.com/shikijs/textmate-grammars-themes/main/packages/tm-themes/themes/vesper.json)。当前仍是轻量 Kotlin regex tokenizer，未声称完整移植 Shiki/TextMate 语法。内联代码字体改用项目实际打包的 Geist Mono，字号改为段落的 85%，对应官网桌面 12.75sp / 手机 16.065sp；圆角背景和精确内边距仍待后续处理。

新增 DocCodeBlockTest 4 项：实际 gutter/复制按钮位置与尺寸、水平滚动时 gutter 和复制保持不动、垂直滚动时复制保持可见、浅→深→浅切换下背景和字符串高亮的实际像素。ComponentDocumentationTest 更新为图标按钮语义复制，验证纯文本 Composition 的实际载荷与 Copied 反馈，并检查 Copy Page 包含拆分后的完整导入和调用。最终 **19 项通过**：DocCodeBlock4、ComponentDocumentation5、AlertExamples4、CatalogNavigation6；Web Wasm distribution 通过。未改 core 组件、未重录像素基准或执行全量像素 verify；最终构建仍有既有 webpack 警告。

浏览器最终检查：1440×900 桌面与 390×844 手机、浅深色的安装/Usage/Composition，保留 `alert-r7-{desktop,phone}-{light,dark}-{code,composition}.png`。真实点击安装代码复制后见勾号（desktop/phone-copy-feedback），手机实际水平滚动代码后行号保持固定（phone-code-horizontal-scroll）；桌面展开默认源码并纵向滚动，按滚动后的按钮位置实际复制见勾号（expanded-before/scrolled/copy-feedback）。原始样式数据为 `alert-r7-code-official-{light,dark}.json`、`alert-r7-code-spacing.json`、`alert-r7-inline-official.json`。浅深色比较是实际主题切换，不把静态渲染等同于键盘或读屏验收。

剩余：内联代码的圆角背景/左右及上下留白；轻量 Kotlin 高亮对完整语法的不足；复制图标目前复用目录 Lucide，官网为 Tabler，路径未逐点一致；代码/安装/API 的其余细微排版仍需复核；标题 permalink、复杂内容重排后的滚动锚定、超宽操作区/复杂槽位及实际读屏输出。前轮历史像素基准差异继续保留。Alert 仍未勾选完成，其他组件未因这些共用目录修改被视为复核完成，editor 继续跳过。

### Alert 第六轮：说明弹层焦点返回与章节 URL（2026-10-30，尚未关闭）

<!-- progress-created: 2026-10-30; order: 44 -->

Popover 增加实例级焦点返回记录：触发器在指针初始事件或真实焦点进入时记录目标，优先返回调用者提供的 Button，纯文本触发器回退至外层 clickable。modal 内容的实际挂载/移除决定返回时机，退出动画结束后再请求焦点；重开会取消待执行请求。Popover 内容统一观察键盘输入并处理 Escape，RTL 目录说明移除局部 Escape 补丁，使用共享实现。该回补针对 modal Popover；非 modal 不自动抢回其他控件的焦点，本轮未扩展其完整交互验收。

新增 PopoverFocusTest 两项，真实鼠标点击第二个触发器，覆盖文本触发器和内嵌 Button。测试区分 Popup 与主窗口独立的 focus owner，冻结时钟检查退出中间帧尚未执行返回，退出完成后确实回到第二个触发器，并切为键盘输入模式。AlertExamplesTest 的真实 RTL 示例先聚焦语言选择器，再鼠标打开说明，Escape 后断言说明按钮获得焦点。

共享目录接入可选 DocNavigation 浏览器桥，原生目录保留本地滚动。对外 fragment 使用章节标题生成的可读 slug，例如 `#rtl`、`#composition`、`#custom-colors`、`#alertaction`；重复标题生成唯一后缀，内部稳定测试 tag 保留。Web pushState 保留 query，通过 popstate/hashchange 接回章节状态；刷新和初次进入等候真实标题布局后定位，目录点击仍平滑滚动，同一 hash 再次点击也能重新定位。浏览器跨组件路由变更清理旧 fragment；未知章节不移动当前页面。这里只实现按 fragment 定位，未声称持久化任意手动滚动位置。

ComponentDocumentationTest 新增真实页面深链接/目录输出/重复点击/空 hash 回到顶部/未知 hash 和其他路由隔离测试，以及重复标题 slug 唯一性测试。初版鼠标点击因 JVM 实际窗口只有 1024px、目录位于 1440dp 布局右侧而失败，改为同已有目录测试一致的 OnClick 语义操作；浏览器另外执行真实物理点击，不把测试窗口外的模拟点击当产品故障。

最终验证 **23 项通过**：PopoverFocus2、PopoverScreenshot4、OverlayEscape2、AlertExamples4、ComponentDocumentation5、CatalogNavigation6。Web Wasm distribution 通过；本轮未重录像素基准，也未执行全量显式像素 verify。保留既有 Text.kt INVISIBLE_REFERENCE、AGP 实验选项、Gradle deprecated features 与 webpack 警告。

官网对照：等待弹层实际移除后，触发器获得焦点且匹配 :focus-visible，最终 DOM 证据为 `web-parity/alert-r6-official-focus.json`；不以退出动画尚未结束时的 DOM 状态作为关闭结果。

浏览器：1440×900 桌面、390×844 手机的浅深色说明弹层均实际打开并按 Escape 关闭，确认键盘圈回到说明按钮、语言选择器不接走焦点；手机浅深色和桌面浅色另验证鼠标再开关后没有键盘圈。截图 `alert-r6-{desktop,phone}-{light,dark}-info-*.png`；手机完整加载 `#rtl` 时标题和说明位于首屏。桌面目录真实点击 Composition/AlertAction、浏览器 back/forward、带 hash 刷新、滚至页首后再次点击同一 hash，均使对应章节可见并保留 query，URL 与截图清单见 `web-parity/alert-r6-history.json`。API 最后小节受页面末尾最大滚动范围限制，不保证标题总在屏幕顶部；验证范围为真实章节可见。

剩余范围：安装/代码框的边框、留白、语法和内联代码细节；标题本身的 permalink 入口；窗口改尺寸或复杂内容重排后的滚动锚定；超宽操作区及复杂槽位；实际读屏输出。前轮 25 项历史截图基准差异仍待各组件复核处理。Alert 继续未勾选；共享修复不代表其他组件页完成对照，editor 继续跳过。

### Alert 第五轮：共享 focus-visible 与输入方式切换（2026-10-30，尚未关闭）

<!-- progress-created: 2026-10-30; order: 43 -->

共享 focusRing 现在仅在真实焦点且 InputMode.Keyboard 时绘制非文本控件的边框/外圈；Destructive Button 的专用颜色遵守同一条件及 ring.enabled。文本输入继续使用 focusRingAlways，鼠标聚焦后保留输入提示。补入内部 keyboardInputMode 修饰器，在控件原有 click/navigation 消费按键之前切换输入模式，不消费按键或改变焦点。Button、Select、Checkbox、Switch、RadioButton、Toggle、Tabs、Slider、Badge、Chip、NavigationMenu、Sidebar、MessageScroller 和 Dialog/Sheet/Drawer 关闭按钮接入；复用 Button 的控件自动继承。该输入模式策略是 Compose 对应实现，不把程序化焦点/读屏/所有平台焦点来源等同于浏览器完整 CSS 启发式。

FocusVisibleTest 五项验证 Button、Select、Destructive 的指针→真实方向键→指针切换，保持真实焦点并断言实际外圈像素；Checkbox 用 Space 真正切换状态并显示键盘焦点圈，切回指针后保持选中及焦点；Input 在指针模式保留实际 ring 边框。snapshotFocused 显式进入键盘模式，避免键盘焦点截图依赖测试环境初始输入模式。

最终定向行为/渲染检查通过：FocusVisible、ButtonNova、SelectionNova、ChoiceNova、FormNova、Tabs、Slider、Sidebar、NavigationMenu、Chip、BadgeNova、OverlayEscape、DrawerDismiss，以及 AlertExamples、ComponentDocumentation、CatalogNavigation。Web Wasm distribution 通过；SelectFocus 浅深色两项显式 Roborazzi verify 通过。上述普通 jvmTest 通过不等于其他像素基准全部通过。

扩大像素检查发现 Chip 4、Tabs 3、Slider 4、Sidebar 14，共 25 项基准差异。临时禁用本轮 keyboard bridge、恢复无输入方式限制的旧焦点圈和旧 snapshotFocused 行为后，25 项仍然失败且实际输出图片逐项相同；隔离证据见 `web-parity/focus-baseline-isolation.json`。已恢复最终源码并重跑通过行为测试，没有重录这些基准；这些历史视觉差异仍需在各组件完整复核时逐项处理，不以更新图片掩盖。保留既有 Text.kt、AGP 实验选项、Gradle deprecated features 和 webpack 警告。

浏览器：官网 RTL 说明按钮确认键盘显示 focus-visible、指针操作后不显示。实际本地 Copy Page 在 1440×900 桌面与 390×844 手机、浅深色四象限分别验证指针无圈→方向键有圈→指针无圈，保留 Copied 反馈。截图 `web-parity/alert-focus-{desktop,phone}-{light,dark}-{pointer,keyboard,pointer-restored}.png`。手机浅色最初恢复截图仍处于过渡帧，等待末帧后重新捕获最终无圈状态。实际 RTL Select 鼠标选择 Hebrew 后无圈，方向键打开菜单后 Escape 关闭显示键盘焦点圈，证据 `alert-focus-select-{pointer,keyboard}.png`。本轮未完成说明 Popover 返回焦点、触摸真机或读屏验收。

仍待关闭：说明弹层 Escape 返回焦点的完整链路；安装/代码框细节；目录 URL hash；超宽操作按钮及复杂槽位；实际辅助技术输出。Alert 仍不勾选完成，全站复核范围不变，editor 继续跳过。

### Drawer：绘制阶段直接跟随内缩手势（2026-10-30）

<!-- progress-created: 2026-10-30; order: 42 -->

页面 graphicsLayer 直接读取当前 Drawer anchored 手势比例，缩放与圆角不再等待 snapshotFlow 将数值转存到 Animatable。保留 snapshotFlow 同步动画起点，普通开关和最后一个 Drawer 关闭时仍从当前值过渡；手势接近完全展开时也不提前切换为额外缓动。四向、snap 最低档到关闭的归一化规则以及多 Drawer 取最大展开比例保持现有行为。

新增冻结动画时钟的渲染测试，在 0.75、0.25、0.6 三个往返比例下检查实际页面宽度立即对应手势值，移除最后注册项后恢复原宽度。DrawerIndentTest 10 项、DrawerMotionTest 2 项、DrawerDismissTest 5 项，共 **17 项通过**；Web Wasm distribution 构建通过。仍有既有 Text.kt INVISIBLE_REFERENCE 与 webpack 警告。

浏览器桌面实际按住下拖并松手，确认页面边距/圆角随面板位移减小，低阈值松手后共同回弹；截图为 `web-parity/drawer-indent-direct-open.png`、`web-parity/drawer-indent-direct-held.png`、`web-parity/drawer-indent-direct-settled.png`。本轮未新增手机浏览器或真机验证，未更新其他组件进度或像素基准。

### Alert 第四轮：Composition/API、说明导出和目录（2026-10-30，尚未关闭）

<!-- progress-created: 2026-10-30; order: 41 -->

补入官网的 Composition 层级及 Alert、AlertTitle、AlertDescription、AlertAction 四个 API 小节。API 说明和属性表对应当前 Kotlin 重载、Modifier、Style 及槽位参数，不把 React className 写成不存在的 Kotlin 参数。Usage 改为完整图标/标题/正文/操作按钮组合；代码片段改用库公开 Icon，而不是目录专用 DocIcon。Composition 使用纯文本代码块，不再显示错误的 Kotlin 语言标签；CodeBlock 新增可空 language，其他调用默认不变。

ComponentExample 的说明改为单份 descriptionMarkdown，页面渲染内联代码/链接，Copy Page 同时导出原始 Markdown。补齐 Basic、Destructive、Action 的说明段落；Custom Colors 和 RTL 继续采用真实 Kotlin Style/LocalLayoutDirection 用法。ComponentDoc 新增可选 compositionCode、apiSections 和可空 examplesHeading；Alert 不再多出 Examples 总标题，其他页面默认行为保持。共享章节二/三级标题按官网桌面和手机的已测值绘制，新增 heading 语义；这些共享变更不计为其他组件复核完成。

提取 componentPageMarkdown，导出顺序与页面一致：默认示例在安装说明之前，Usage 后为 Composition、其余示例、分组 API；包含说明、链接和属性表。新目录含 Composition、四个嵌套 API 项，点击可定位真实标题。目录滚动目前未同步 URL hash，这是仍待处理的共享导航差异。

验证 **13 项通过**：ComponentDocumentationTest3（完整 Markdown 内容/顺序、实际 Copy Page 按钮输出与 Composition/API 目录滚动、独立纯文本代码块复制载荷及反馈）、AlertExamplesTest4、CatalogNavigationTest6。最终 Web distribution 通过；本轮未改 core 组件、未重录截图基准，也未据此重复宣称上一轮 88 项均重新执行。

浏览器检查了 1440×900 桌面和 390×844 手机的浅深色 Composition/API；实际点击 Copy Page 看见 Copied，展开默认示例源码，点击 Composition/APIAction 目录项，点击 RTL 链接并确认新标签为 https://ui.shadcn.com/docs/rtl 后关闭测试标签。手机 API 表保留横向滚动，以容纳真实 Kotlin 类型，已实际滚到 Default 列。独立代码块复制在同步实际手机绘制帧后也观察到 Copied；对照期间的单节点标签改动已撤回，保留原 AnimatedContent。证据清单为 `web-parity/alert-r4/evidence.json`，截图均在同目录。

仍未关闭：[1] 共享 FocusRing.kt 的 focused 样式未区分输入方式，Select、说明/目录按钮在鼠标焦点返回后会显示 ring；需要实现并验证键盘/鼠标转换，而不是只隐藏本页 ring。[2] 安装区使用本库 Gradle 依赖，API 表使用 Kotlin 类型和横向滚动，均为明确的平台适配；安装/代码框的边框、留白、语法和内联代码样式尚未完成逐像素对照。[3] 目录滚动的 URL hash 同步未接入。[4] 超宽操作区、复杂槽位及实际读屏输出仍未完成验收；当前固定操作区留白适用于紧凑按钮，已在 API 中注明。Alert 保持未勾选，全站及已完成组件复查范围不变，editor 继续跳过。

### Alert 第三轮：工具栏、章节排版、真实扩展示例与绘制颜色（2026-10-30，尚未关闭）

<!-- progress-created: 2026-10-30; order: 40 -->

按官网 Base Alert 的 DOM 实测，RTL 工具栏改为独立 64dp 区域，语言 Select 为 144×28dp；预览主体在其下，桌面 288dp 最小高、手机自适应，主体 padding 24dp。右侧按钮提供真实三语言翻译说明 Popover（224dp 宽、10dp 留白、12/16sp 文本和分隔线），显式设置初始焦点，Escape 可关闭。提取目录专用 `RtlPreview`，语言状态仍由示例持有；目录控件与 Alert 组件本体分开，可复制 RTL 代码展示默认阿拉伯语组件结构。

共享 ComponentExample/PreviewCodeSection 增加可空 description slot，未提供时不产生额外区域。Custom Colors、RTL 增加说明，改为 Kotlin Style / LocalLayoutDirection 的说明而非照抄 React className API，RTL 配置链接保留到官网。示例章节标题采用本次 DOM 的桌面 18.75/26.25sp、手机 23.625/33.075sp 和 SemiBold；该共享标题排版也应用到其他目录示例，但并不代表其他组件已经复核。

修复扩展示例名实不符：Title 现在仅显示 AlertTitle，Description 仅显示 AlertDescription，不再都重复完整 Alert；Slots 保留组合示例。三组统一最大宽 448dp、padding24dp、手机自适应高度，代码与预览同步。

进一步替换上轮 Text 兼容桥接：使用 BasicText ColorProducer 在绘制时读取最近祖先自身明确设置的 contentColor，保留原有 Style/Text 测量边界；不再从 measure/draw 写 snapshot State 回组合，也不再使用聚合继承缓存。浏览器完整目录场景曾复现深色 Action 标题变黑，单独 JVM 场景未复现；直接读取每层 Style 自身已解析颜色后，最终桌面和手机深色截图均确认恢复。颜色替换、移除、显式 TextStyle 优先级、浅深往返及 Action 重绘改用实际绘制像素断言，GetTextLayoutResult 不作为 ColorProducer 最终绘制颜色的证据。当前仍依赖 Foundation 内部 StyleOuterNode/resolveAnimatedStyleFor，升级 Compose 时须以这些测试和 Web 场景重新验证；未声称完成上游缓存缺陷修复或原生辅助技术验收。

验证：最终相关用例 **88 项通过**：Accordion12、AlertNova6、AlertScreenshot4、Badge6、Button7、Card5、Form14、Selection19、TextColorInheritance1、TextScreenshot4、实际 Alert 示例4、目录导航6。TextScreenshot/AlertScreenshot 的 **8 张**显式像素验证通过，Web distribution 通过。首次显式 verify 发现 4 张旧基准与当前 Card 16dp 间距/Button Sm 28dp 高度不同；临时恢复修改前 Text 实现后仍出现同样 4 处，浅深对比图逐一查看后，仅用**旧 Text 实现**生成的结果更新这四张，再验证当前实现像素相同。隔离及验证记录见 `web-parity/alert-text-color-verification.json`，其余基准未修改。

浏览器最终矩阵：1095×687 桌面浅/深各12张连续覆盖，390×844 手机浅/深各9张，覆盖 Default、Basic、Destructive、Action、Custom Colors、RTL、Slots、Title、Description。原始文件清单见 `web-parity/alert-r3-final-matrix.json` 与 `alert-r3-final-{desktop,phone}-{light,dark}/`；中间的黑字问题保留为 `alert-r3-action-before-{desktop,phone}-dark.png`，不作为修复后证据。另已实际打开说明并按 Escape 关闭；语言 English 的真实切换前轮已验证，本轮 JVM 保留验证。截图覆盖证明上述视觉状态已查看，不等同于所有键盘、读屏及原生手势场景通过。

真实剩余差异（保持未勾选）：[1] 官网 Composition、四个 API 小节及相应目录锚点尚缺，Usage 仍是简化便捷重载；[2] Basic/Destructive/Action 的官网说明段落尚缺，本地多出 Examples 总标题；[3] Copy Page 尚未导出新 description slot 的说明内容，链接打开和代码展开/复制需在新结构下复核；[4] Select/说明按钮在鼠标返回焦点后仍可能显示 focus ring，通用 focus-visible 需继续处理；[5] 安装区采用本库真实 Gradle 依赖，不能改成实际无效的 shadcn npm 命令，其布局仍需继续对齐；[6] 长操作按钮/复杂槽位和实际读屏输出尚未完成对照。全站目标和已标记完成组件的复查范围不变，editor 继续跳过。

### Alert 第二轮：官网示例、RTL 与动态颜色（2026-10-30，尚未关闭）

<!-- progress-created: 2026-10-30; order: 39 -->

对照官网 Base Alert 页面和 `apps/v4/examples/base/alert-{basic,destructive,action,colors,rtl}.tsx`：补入 Basic、Destructive、Action、Custom Colors、三语言 RTL，保留 Default 及项目扩展槽位示例。官方六组示例的预览统一使用 24dp padding、手机最小高度 0dp、桌面最小高度 288dp；最大组件宽度 448dp。Action 使用真实 Enable 按钮（官网没有业务回调，本地也不新增演示业务）。RTL 默认阿拉伯语，含英语和希伯来语，真实 Select 切换内容和布局方向；可复制代码同步。

Nova AlertTitle 移除写死的 TextStyle 颜色，让调用者的祖先 Style.contentColor 生效。根 Layout 为无界宽度补充固有宽度策略，新增水平滚动父容器与 RTL 图标/操作区避让测试。

实际 Custom Colors 示例的主题切换测试进一步复现 Text 桥接缺陷：只在 measure 读取祖先 Style 颜色，颜色变化仅触发 draw 时文字仍用旧颜色。Text 的既有兼容桥接现覆盖 draw 和 snapshot observation；draw 缓存读取不覆盖 measure 建立的订阅，避免空读取清空观察。继承颜色移除时恢复 Unspecified，让 LocalContentColor 回退重新生效。此桥接仍依赖现有内部 TextStyleProviderNode，并可能通过状态刷新额外调度文字更新；没有宣称解决 Compose 上游继承机制或完成性能基准。

验证：AlertNovaTest **6**（含 caller 自定义颜色、无界宽度、RTL 操作区）、AlertScreenshotTest **4**、TextScreenshotTest **4**、TextColorInheritanceTest **1**（主题不变时祖先颜色替换/移除、显式颜色优先级）、AlertExamplesTest **2**（实际 Custom Colors 浅到深主题切换、真实 RTL Select 切换内容），共 **17 项通过**。Web Wasm distribution 构建通过；保留既有 INVISIBLE_REFERENCE 与 webpack 警告。截图测试作为渲染回归执行，未重录基准，未声称通过全量像素 verify。

浏览器证据：Custom Colors 1095×687 和 390×844 浅深四象限均查看，并实际点击主题切换确认文字、背景及边框更新；手机内容宽约 292px、高约 120px，换行与官网对照。RTL 官网深色桌面、浅色手机，以及本地手机阿拉伯语浅深/切换 English 后的内容和方向已查看。证据：`alert-colors-{official,local}-{desktop,phone}-{light,dark}.png`、`alert-rtl-official-desktop-dark.png`、`alert-rtl-official-phone-light.png`、`alert-rtl-local-phone-english.png`。

仍待关闭的已知差异：官网 RTL 的语言选择器位于独立工具栏（约 144×28px，含说明按钮），本地目前在预览内容中（180×32dp）；Custom Colors 和 RTL 章节说明段落缺失；手机章节标题字号与官网不一致；代码框及安装/API 区布局尚未逐项完成；本地额外 Slots/Title/Description 示例的完整浅深/桌面手机矩阵、长操作区及读屏实际输出仍待核对。RTL 手机鼠标选择后 Select 出现 focus ring，focus-visible 条件还需单独复核。Alert 保持未完成，以上差异不因定向测试通过而视为关闭。

### Drawer：手势内缩方向与 snap 回归补充（2026-10-30）

<!-- progress-created: 2026-10-30; order: 38 -->

确认当前 `DrawerIndentHost` 已消费面板的真实 anchored offset：手指按住拖动时页面缩放和圆角随进度变化，未达到关闭阈值松手时随面板回弹。此次补充 Nova 左、右、上三个方向及 snap 最低档至关闭区间的真实触摸回归，保留原下拉用例；新增多个 Drawer 取最大展开比例的状态验证。snap 在可见底部区域发起触摸，避免落到尚未露出的内容区域。

验证结果：`DrawerIndentTest` 9 项、`DrawerMotionTest` 2 项、`DrawerDismissTest` 5 项，共 **16 项通过**。此次新增验证与文档修订，未改动现有运行时代码；未新增浏览器或真机验证，不扩大此前截图证据的范围。

### Alert 第一轮：Nova 组件布局与默认示例（2026-10-30，尚未关闭）

<!-- progress-created: 2026-10-30; order: 37 -->

基线：官方 `apps/v4/examples/base/alert-demo.tsx` 及 style-nova.css。浏览器测得默认宽448px、每条80px、padding8px/10px、正文14/20、行间gap2px、图标16px+8px横向间距、圆角10px。默认底色/文字为 card/cardForeground，destructive 正文为 destructive/90。

修正：Nova 使用 Layout 将直接图标、正文和 Action 分别测量、放置，右侧按官网 pr-18 预留操作空间，按钮定位 root top/right 8dp；去掉 Nova 中依赖组合顺序捕获 actionHolder 的布局方式，其他 preset 保留旧实现。默认及 destructive 使用 card 底色，字体显式14/20、title Medium、正文 Normal；LineHeightStyle.Trim.None 保留完整20dp行盒，修正初版矮4dp。Assertive liveRegion 保留。目录首屏改为 Payment/New feature 两条真实提示、圆形图标、max448、gap16。

响应式容器：本次直接测量 Alert 官网使用 p-6（24px），手机 `h-auto`、桌面 h-72；并非所有组件统一40px。新增每个 ComponentExample 可选 previewPadding/mobileMinPreviewHeight，默认仍40dp和既有最小高度以保留其他组件；Alert 默认设padding24dp、手机最小高0dp。官方手机Alert宽292px、首条高100px、预览高244px，本地最终截图相符。此事实与 Accordion 的 p-10 不矛盾，后续按组件实测，不继续把全站容器视为统一参数。

验证：AlertNovaTest3（操作区避让及重组后按钮保留、destructive文字实际颜色/14sp/20sp、单行组合60dp及gap2）、AlertScreenshotTest4（旧 preset 浅深/action）、CatalogNavigationTest6，共13项通过；JVM/Wasm 和Web distribution通过，未重录基准，未声明全量像素verify。官方与本地默认示例的1095×687/390×844、浅深四象限均查看；本地深色 Action 桌面/手机确认长标题和正文未覆盖 Enable 按钮。证据 `alert-{official,local}-{desktop,phone}-{light,dark}.png`、`alert-action-{desktop,phone}-dark.png`。

仍待关闭：官网 Basic/Destructive/Action/RTL/标题-only 等完整示例迁移及状态矩阵；当前本地额外 API 示例不等于官网全部演示。更多尺寸、长操作按钮与复杂槽位组合，RTL 的图标/操作位置、读屏实际输出仍需核对。组件根布局在无界宽度父容器的固有宽度策略尚未验收；当前证据均为有界窗口/布局。Alert 不勾选完成，全站复核继续。

### Alert 开始复核（2026-10-30）

<!-- progress-created: 2026-10-30; order: 36 -->

官方页面 https://ui.shadcn.com/docs/components/base/alert 已实际打开，默认是 Payment successful 与 New feature available 两条带图标提示，源码为 `apps/v4/examples/base/alert-demo.tsx`。本地仍是 Success/no-icon 旧示例。官方 Nova CSS root 为 px-2.5/py-2、gap-0.5、图标16px和8px横向gap，Default 为 bg-card/text-card-foreground；本地 root 使用 twSpacing.lg/md（更大），Default 使用 colors.background。此处仅记录证实的缺口，未修改组件、未标记完成。官方首屏证据 `web-parity/alert-official-initial-desktop-light.png`；接下来修正组件/默认示例并完成浅深桌面手机与交互/无障碍检查。

### Accordion 第六轮：Disabled、受控拒绝与 forceMount（2026-10-30，仍有说明的差异）

<!-- progress-created: 2026-10-30; order: 35 -->

新增受控拒绝变更回归：onValueChange 只记录请求、不更新 value 时，原展开内容保持不变；disabled item 不发起请求。新增 forceMount 独立回归：外部收起恢复标题焦点、隐藏语义，重新打开保留输入。Nova focus ring 仅在 InputMode.Keyboard 下绘制，并补 1dp 内边框；鼠标点击不再显示外圈。Web 实测首次方向键未自动切换输入模式，因此在标题真实 KeyDown 事件中显式 requestInputMode(Keyboard)，再次用 ArrowDown+Enter 确认焦点外圈和下一项展开均可见。

验证：当前 core AccordionNovaTest 12 项通过；此前同批完整命令的 ScreenshotTest 6 项与 app AccordionExamplesTest 2 项通过，20 个不同测试在该批验证序列通过。最后输入模式修正后重跑 Nova 12 项和 Web distribution。未重录基准，未宣称像素完全一致。浏览器 Disabled 的桌面1095×687/手机390×844、浅深主题均检查，点击 disabled 无展开，第三项有真实回答。证据 `accordion-disabled-real-{desktop,phone}-{light,dark}.png`；`accordion-focus-mouse-no-ring.png` 和 `accordion-focus-keyboard-ring.png` 分别证明鼠标/键盘条件。

官方七组示例均已有浅深/桌面手机的首屏或展开检查记录；尚未据此宣称所有扩展 API 示例完整验收。继续保留：[1] 当前示例高度使用 minPreviewHeight，长内容会自适应增长，官网为固定高度和溢出策略；[2] 语言 Select 的具体尺寸、站点代码框与安装区布局还需逐像素对照；[3] 项目扩展的 horizontal/no-loop 等示例尚未完成全部组合状态。下一步开始 Alert 逐页核对，同时保留 Accordion 这些未关闭项，不把全站目标缩小到已处理部分。

### Accordion 第五轮：真实数组示例、200ms 动画与透明 focus ring（2026-10-30，尚未关闭）

<!-- progress-created: 2026-10-30; order: 34 -->

本轮纠正前两轮漏检：Basic/Multiple 的官网源码使用 items 数组，原迁移脚本仅抽取 JSX 文本，错误地把 `{item.trigger}` / `{item.content}` 留成字面量。已改为读取各自三条真实数据并保持首项 key 一致；Disabled 内容中的 HTML 实体已解码。新增 app/shared 的 AccordionExamplesTest，直接执行目录中实际 preview 并断言问题/回答，以及 Multiple 同时展开两项，防止“编译通过即内容正确”的误判。此前有关完整示例的表述由本节证据取代。

官方内容面板浏览器实测 animation=`0.2s ease-out`；Nova 改为只做高度 expand/shrink 的 200ms cubic-bezier(0,0,.58,1)，保留其他 preset 原动画与调用者自定义 enter/exit。新增冻结时钟的中途反向回归。多项展开的浅色截图暴露 focus shadow 从透明背景透进内部的灰填充：Nova focus ring 改为外侧圆角描边，仍保留主题色/3dp 外圈；新增真实白底内部像素断言，浏览器复核透明内部。指针也会显示 focused ring 与官网 focus-visible 的条件差异尚未关闭，透明边框的 focused 色精细对齐也仍待验收。

验证：最终源码已通过 core AccordionNovaTest 10 项、既有 AccordionScreenshotTest 6 项（上一全量命令）、app AccordionExamplesTest 2 项，合计 18 项不同测试通过；新增像素测试后只重跑 Nova 定向 10 项。JVM/Wasm 与 Web distribution 通过；不重录基准，不宣称全站逐像素一致。Basic 和 Multiple 的真实内容在桌面/手机浅深四象限已人工查看，Multiple 第二项打开时首项仍展开。证据 `web-parity/accordion-{basic,multiple}-real-{desktop,phone}-{light,dark}.png`；透明 focus 证据 `accordion-focus-transparent-fixed.png`。

继续未关闭：Disabled 完整四象限与外部受控拒绝变更、forceMount 单独行为、所有扩展 API 示例完整状态矩阵，以及固定高度溢出与 focus-visible 条件差异。上轮所说 Basic/Multiple 已迁移的源码本轮已真实修正；不以逐项增加测试数量替代最终浏览器矩阵验收。全量组件目标保持 active。

### Accordion 第四轮：隐藏 Tab、输入保持、末项顺序和语言下拉（2026-10-30，尚未关闭）

<!-- progress-created: 2026-10-30; order: 33 -->

新增测试确认 keepMounted 输入框外部关闭后 Tab 到下一个可见按钮，重新打开仍保留输入。Nova 纵向根布局在测量直接子项前从 parent data 取得当前 keyed 顺序，分隔线按当前末项测量为 0 高；不把顺序写回 snapshot state，因此无需布局→组合循环。新增 42/43dp item 高度回归，覆盖末项随 keyed 重排变化。其他 preset 保留原布局。

RTL 从循环按钮改成真实 Select，下拉含 English/Arabic/Hebrew，选择后切换内容与 LayoutDirection；页面描述替换成官方文案。Nova focus 圆角使用 baseRadius（当前目录为 10dp），与本次官方 DOM 实测 10px 对照。新增可选 minPreviewHeight，Accordion 默认/Basic/Disabled/Borders=300dp、Multiple=450dp、Card=435dp，RTL 保留 288dp。默认其他组件仍 288dp；使用最小高度以免大字体/多行内容被强行裁剪，与官网固定高度的溢出策略仍存在差异，保留为明确待复核项。

**纠正此前截图推断**：2026-10-30 直接测量已加载官方 DOM，手机 390px 下预览容器 padding=40px、Accordion 宽260px，本地 40dp 并非已证实差异；撤回前几节“官网更小”的结论，不修改共享 padding。首轮截图不能独立证明 hydration 后的真实尺寸。官方当前预览高度同样由实际 DOM 确认。

验证：AccordionNovaTest 8 + AccordionScreenshotTest 6 =14 项、CatalogNavigationTest 6 项均通过；JVM/Wasm 与 Web distribution 通过。未重录基准或宣称逐像素一致。浏览器打开三项语言菜单并选 English，LTR 文本/图标方向实际改变；语言选择器桌面/手机浅深均查看，证据为 `accordion-language-select-{english,desktop-dark,phone-dark,phone-light}.png`。

尚未关闭：Basic/Multiple/Disabled 等新增示例的全部组合状态尚未逐一验完；forceMount 单独焦点矩阵、hover/disabled/controlled/动画反转需补齐。语言 Select 的尺寸和官网选择器还需详细比对；完整动画参数和预览固定高度溢出策略仍未验收。逐组件清单继续保持未完成，全站目标不缩减。

### Accordion 第三轮：焦点、重排、Borders/Card（2026-10-30，尚未关闭）

<!-- progress-created: 2026-10-30; order: 32 -->

新增外部收起焦点恢复：内容 focusGroup 跟踪 hasFocus，关闭时请求对应标题焦点；keepMounted/forceMount 隐藏壳清除语义并拒绝进入焦点，保留内部组合状态。键盘导航用实际布局位置排序，修复 keyed children 重排后仍按首次注册顺序移动的问题；位置只在键盘事件中读取，未用每帧位置触发组合。

Borders 示例将 16dp 水平 inset 移到 Trigger/Content，分隔线横跨内框。Nova 标题底色改透明以融入 Card；浏览器复查还发现主题切换时 Card 内文字可能保留黑色，因此给 Nova 标题及字符串正文显式指定主题 foreground，新增实际 TextLayoutResult 颜色断言。仅修复背景而未修复文字时的截图已被最终构建截图替代。

验证：AccordionNovaTest 6 + AccordionScreenshotTest 6，共 12 项通过；包括外部关闭焦点回收/隐藏语义、keyed 重排方向键和 Home/End、深色 Card 文本颜色，以及此前尺寸/disabled/RTL 回归。Web distribution 通过，无像素基准重录或逐像素一致声明。Borders 与 Card 的桌面 1095×687 / 手机 390×844、浅深主题均人工检查，分隔线、透明背景、深色文字与换行修正可见。证据 `web-parity/accordion-{borders,card}-fixed-{desktop,phone}-{light,dark}.png`。

待完成：Basic/Multiple/Disabled/RTL 的全部组合状态与四象限仍需补齐；语言切换仍为按钮而非官网选择器；动态重排的末项 divider 仍基于注册次序，需要独立修正；keepMounted 还需验证 Tab 跳过与重新打开后的输入状态保持；共享预览容器的手机 40dp 留白、focus ring 精细参数和页面描述待对齐。上述差异仍保留，因此不勾选整组件或全站完成。

### Accordion 第二轮：完整示例源码、hover 与 RTL 键盘（2026-10-30，尚未关闭）

<!-- progress-created: 2026-10-30; order: 31 -->

从官方当前仓库 `apps/v4/examples/base/accordion-{demo,basic,multiple,disabled,borders,card,rtl}.tsx` 读取七个文件。补齐默认 FAQ 后两项的真实回答，新增 Basic/Multiple/Disabled/Borders/Card 示例，RTL 示例采用官网英文、阿拉伯语、希伯来语内容，按钮循环切换语言和 LayoutDirection；目前语言控件为本地按钮，不与官网选择器外观等同。既有扩展 API 示例仍保留。

组件修正：默认实心箭头改成 16dp 圆端描边 chevron；hoverable 与真实 InteractionSource 驱动 LocalTextDecoration，浏览器已看到标题下划线。方向键根据 orientation 选择纵轴/横轴，横向 RTL 以 Left 前进、Right 后退，保留 Home/End。新增跳过 disabled + Home/End 和水平 RTL 两项回归。

验证：AccordionNovaTest 3 + AccordionScreenshotTest 6，共 9 项通过；JVM/Wasm 与 Web distribution 构建通过。未开启像素 verify，不重录基准。浏览器查看 Default hover、Disabled、Card 桌面浅色，并切换 RTL 三语言；Arabic 在手机深色无横向溢出，图标随方向移动到左侧。证据：`accordion-hover-desktop-light.png`、`accordion-card-desktop-light.png`、`accordion-rtl-{ar,he,en}-desktop-light.png`、`accordion-rtl-ar-phone-dark.png`（均位于 web-parity）。

继续保留未勾选：每个新增示例的完整桌面/手机、浅深四象限还未验完；手机 Card 上方局部截图出现较重的子项底色，需单独对照并定位。Borders 示例目前 item 的 16dp padding 同时缩进分隔线，官网分隔线应横跨外框内部，仍需修正。语言选择器样式、共享预览容器手机 40dp 留白、focus ring、动态顺序及 keepMounted 隐藏内容焦点仍待核对。不把新增示例源码或 9 项测试当作 Accordion 全面验收完成。

### 全量复核重启：Accordion 首轮（2026-10-30，尚未关闭）

<!-- progress-created: 2026-10-30; order: 30 -->

继续完整清单，包含原先已勾选组件；editor 按要求排除。本次是实际代码及浏览器证据推进，不把本轮少量修改视为全站完成。

来源：官方 https://ui.shadcn.com/docs/components/base/accordion；源码 `apps/v4/registry/bases/base/ui/accordion.tsx`、`apps/v4/registry/styles/style-nova.css`，读取于 2026-10-30。浏览器测量官方标题为 14px/500/20px、上下 padding 10px、透明 1px 边框，首屏 Accordion 宽 384px，末项无分隔线。

修正：本地 Default 改成物流 FAQ 并限制为 384dp；Nova 标题上下 padding 10dp、显式 14/20 字号行高、透明边框和 42dp 最小高度；正文 14/20 Normal、底部 10dp；末项不绘制分隔线，多行标题图标顶对齐。保留其他 preset 的间距和分隔线。新增 `AccordionNovaTest` 验证 42dp 标题及展开/收起。最初尺寸测试测得 36dp，已通过显式 Style 行高与最小高度修正后重新验证。

验证：AccordionNovaTest 1 项 + AccordionScreenshotTest 6 项共 7 项通过；JVM/Wasm 编译和 Web distribution 通过。此命令未设置 Roborazzi 像素 verify，不将截图测试执行等同于基准逐像素一致；未重录基准。浏览器桌面 1095×687、手机 390×844 的浅深主题均查看；手机点击首项收起，再 ArrowDown/Enter 打开第二项，焦点与展开内容可见。截图 `web-parity/accordion-{official,local}-{desktop,phone}-{light,dark}.png`；键盘证据 `web-parity/accordion-local-keyboard-phone-dark.png`。

真实未关闭项：官网 Basic/Multiple/Disabled/Borders/Card/RTL 示例尚未逐一迁移；本地额外 API 示例暂保留。首屏后两项回答尚非官网全文；hover 下划线、focus ring 细节、chevron 图形与主题切换字重观感需继续比较。手机预览区水平 padding 为 40dp，官网更小，导致本地换行更早；标题/描述及代码区高度仍有页面骨架差异。键盘方向/RTL/动态排序及 keepMounted 隐藏焦点需要专门回归。故清单仍未勾选。

### Drawer：Indent 跟随拖动与回弹（2026-10-30）

<!-- progress-created: 2026-10-30; order: 29 -->

补齐上一节的手势缺口：普通面板以 anchored offset/Closed anchor 归一化，支持四个方向；snap 面板在最低可见档以上维持内缩，仅在最低档至关闭之间逐步还原。每个面板通过只读进度函数共享手势状态，Host 用 snapshotFlow 观察，手势和面板回弹帧直接更新背景，不叠加第二次缓动；普通开关仍使用 450ms 过渡，关闭从当前缩进继续。多个启用面板取最大展开比例。

新增真实触摸测试，验证手指仍按住时页面已还原、低速且低于阈值松手后回弹。Indent 4、Motion 2、Dismiss 5，共 11 项通过；Web Wasm distribution 构建通过。浏览器桌面鼠标按住下拖，确认面板下移时背景边距和圆角同步减小，证据为 `web-parity/drawer-indent-drag-held.png`。测试未将触摸 cancel 等同于正常松手：取消路径的面板停留行为不属于本次已验证回弹结论。

### Drawer：可选整页 Indent effect（2026-10-30）

<!-- progress-created: 2026-10-30; order: 28 -->

参考 Base UI Drawer 的根层 indent 模式（https://base-ui.com/react/components/drawer），新增 `DrawerIndentHost`。应用按 `OverlayHost { DrawerIndentHost { AppContent() } }` 放置，Drawer 使用 `indentBackground = true` 选择启用，默认 false。背景使用黑色，页面等比缩放，使较短轴两侧各留 12dp，圆角过渡至 24dp；复用现有 450ms easing。Portal 保持在变换容器外，Drawer 本身不跟随缩小。该实现是 Compose 可选效果，不宣称与官网 CSS 参数逐像素一致。

每个启用的 Drawer 以独立 key 注册，最后一个关闭或移除时恢复，避免多个 Drawer 相互提前还原。初版驱动为开关状态动画；后续已补齐手势进度跟随，见下节。目录根层已接入，Drawer 页面增加 “Indent effect” 示例和使用代码。

验证：`DrawerIndentTest` 3 项、`DrawerMotionTest` 2 项、`DrawerDismissTest` 5 项，共 10 项通过；Web Wasm distribution 构建通过。保留既有 Text.kt 编译警告及 webpack 警告，无新增编译错误。浏览器桌面确认整页背景内缩、面板保持原尺寸，Escape 关闭后恢复，证据为 `web-parity/drawer-indent-open.png`、`web-parity/drawer-indent-restored.png`。未重录像素基准。

### Web favicon：仅保留 SVG（2026-10-30）

<!-- progress-created: 2026-10-30; order: 27 -->

移除 ICO 文件及 HTML 引用，只使用 Kotlin 渐变背景、白色图形的 SVG favicon，避免维护两份资源。

### Web favicon：Kotlin 官方渐变背景（2026-10-29）

<!-- progress-created: 2026-10-29; order: 26 -->

按用户要求，背景采用 Kotlin 官方 favicon 的径向渐变：`#EF4857` → `#D211EC` → `#7F52FF`，原始渐变中心与半径按 64→256 等比例放大。白色三条线与 `rx=48` 圆角保持不变。SVG、五种尺寸 ICO 同步，预览为 `web-parity/favicon-kotlin-gradient.png`，URL 版本更新为 `kotlin-gradient`。

### Web favicon：Kotlin 红底（2026-10-29）

<!-- progress-created: 2026-10-29; order: 25 -->

按用户反馈改为 Kotlin 官方 favicon 渐变中的红色 `#EF4857` 作为纯色背景，内部三条线改为白色，保留 `rx=48` 圆角。SVG/ICO 同步，预览为 `web-parity/favicon-kotlin-red.png`，URL 版本更新为 `kotlin-red`。

### Web favicon：加强圆角可见度（2026-10-29）

<!-- progress-created: 2026-10-29; order: 24 -->

原始背景半径为 16/256，在 16px 标签栏图标上约为 1px，用户反馈看起来像直角。背景及裁剪统一使用 `rx=48`，小图标下约为 3px；浅紫色 `#A78BFA` 不变。SVG、ICO 和预览同步更新，验证四角透明、边缘中点不透明，缓存版本更新为 `light-purple-rounded`。

### Web favicon：恢复 sRGB 并提亮紫色（2026-10-29）

<!-- progress-created: 2026-10-29; order: 23 -->

按用户最终要求移除 P3 样式，使用更浅亮的普通 sRGB 紫色 `#A78BFA`，保留黑色背景与三条线原形。按 sRGB 相对亮度计算，与纯黑的对比度由 `#7F52FF` 的 4.55:1 提高至 7.72:1。SVG、五种尺寸 ICO 和 SDR 预览同步更新，URL 版本改为 `light-purple`。浏览器渲染核对三条线均为 `rgb(167, 139, 250)`。

### Web favicon：Display P3 紫色（2026-10-29）

<!-- progress-created: 2026-10-29; order: 22 -->

按用户最终选择使用 P3，不启用 HDR。SVG 内部三条线通过 `@supports` 使用 `color(display-p3 0.498 0.322 1)`，保留 `#7F52FF` presentation attribute 作为不支持时的回退；黑色背景不变。ICO 继续使用 SDR 兼容版本，现有 PNG 预览也仅展示 SDR。URL 版本更新为 `kotlin-purple-p3`。Web distribution 构建通过，浏览器实测三条线 computed stroke 均为 P3，背景为 `rgb(0, 0, 0)`；该验证不等于测量标签栏的实际色彩输出。

### Web favicon：shadcn 标记与 Kotlin 紫色（2026-10-29）

<!-- progress-created: 2026-10-29; order: 21 -->

按用户要求，新增 `app/webApp/src/webMain/resources/favicon.svg` / `favicon.ico` 并在 `index.html` 中声明。采用用户提供的 shadcn-components favicon 原始图形，保留黑色圆角背景，内部三条线统一改为 Kotlin 紫色 `#7F52FF`；优先使用 SVG，ICO 含 16/32/48/64/256 五种尺寸。图标 URL 加版本参数，绝对路径支持 `/drawer` 等组件路由。

Web distribution 构建通过；实际页面 favicon 链接及 SVG/ICO 请求均已验证。图形源为用户提供的 `/Users/cscs/WebstormProjects/web-foundation/shadcn-components/apps/components/public/favicon.svg`，紫色来源 `https://kotlinlang.org/assets/images/favicon.svg`；预览 `web-parity/favicon-kotlin-purple.png`。仅涉及 Web 图标与入口资源，未修改组件行为或重录像素基准。

### 第九批后续修正：嵌套 Drawer 连续点空白关闭（2026-10-29）

<!-- progress-created: 2026-10-29; order: 20 -->

用户指出上一层退场未结束时，继续点空白不能关闭下一层，表现为一层一层卡顿。浏览器先复现四层、约 80ms 间隔点背景四次，最终仍停在 Third Drawer：关闭中的原生 Popup 继续覆盖整窗，但 scrim 的关闭事件已禁用。

DrawerPortal 现提供仅限自身的退出关闭链：退场遮罩收到后续点击时，转给最近仍打开的 Drawer；每层读取最新 open、外部关闭开关及两个 prevention 回调。每次点击只请求关闭一层，父层禁止外部关闭或 preventDefault 时立即停止，不跳过该层。该能力在 Teleport 内容边界重置，普通 Dialog 不继承 Drawer 的退场转交。另修正入场中途关闭的生命周期：必须 current=false、target=false 且 isIdle 才卸载，避免反向退场被直接剪掉。

新增 `DrawerDismissTest` 5 项：冻结动画时连续点背景逐层关闭；父层 pointer prevention；父层 disablePointerDismissal；普通 Dialog 隔离；入场未完成时关闭仍保持退场内容。最终 **104 项通过**（Dismiss 5、Motion 2、Sizing 9、Drawer 13、Completion 29、Surfaces Nova 23、目录应用 23），显式像素 verify，JS/Wasm 构建通过，没有重录基准。

浏览器桌面四次快速点击后全部关闭；手机稳定视口内一次触摸从 Fourth 回到 Third，重开第四层后以约 80ms 间隔触摸背景四次可全部关闭。手机证据为 Chromium CDP 触摸，不替代原生设备实测。截图索引见 `phase9-evidence.json` 的 `rapid_outside_dismiss_followup`。

### 第九批后续修正：bottom / top 同样浮动留白（2026-10-29）

<!-- progress-created: 2026-10-29; order: 19 -->

用户指出 bottom 左右也有间距。实测官网手机 bottom Drawer 的 margin 为 8px、四角 24px；此前仅对左右侧面板应用该规则。Nova 四个方向现统一使用 8dp 外边距和 24dp 完整圆角：纵向面板宽度为原视口宽度减 16dp，bottom 底部留 8dp，top 顶部留 8dp；普通和 snap 面板均覆盖。左右侧面板的 384dp / 75% 宽度不变，旧预设规则不变。

纵向高度上限仍是视口高度减 96dp，snap 的绝对/比例露出高度仍按原视口解析。需区分“高度上限扣除 96dp”与“顶部实际留白”：bottom 最大展开档因底部另有 8dp 留白，顶边在 88dp；例如 844dp 高视口，面板高 748dp、y=88、底边 836。先前截图中的贴边纵向布局和最大档 y=96 由本次证据取代。

新增 bottom/top 外边距两项回归，Sizing 共 9 项。手机普通 bottom 已验证左右/底部留白及侧边间隙关闭，snap 从 496dp 档拖到最大档并在手机尺寸保留留白。当前用户 IAB 预览已实际 reload。六张 Drawer 像素基准经人工 diff 核对后单独更新（Nova drawer、backdrop、nested backdrop 各浅深两张），相应预览同步。最终验证结果见 `phase9-evidence.json` 的 `vertical_floating_followup`。

本次最终 **99 项通过**（Sizing 9、Motion 2、Drawer 13、Completion 29、Surfaces Nova 23、目录应用 23），显式像素 verify；JS/Wasm 构建通过。

### 预览显示旧 snap 代码的核对（2026-10-29）

<!-- progress-created: 2026-10-29; order: 18 -->

用户贴出了旧的 148dp/仅 Header 片段。源码搜索与新打开的 8765 页面均没有该片段；实际展开 Snap points 的 View Code 后，已确认显示 496dp、320dp 主体及 Close footer，证据为 `web-parity/drawer-snap-full-code-current.png`。本地临时预览服务增加 `Cache-Control: no-store, max-age=0`，并补齐 SPA HEAD 路由。原 Chrome 标签页交互超时，尚未确认该页旧内容来自未刷新的实例还是另一预览地址，未将缓存作为已证实的根因。已提供带版本参数的当前预览入口并请求原页面地址。本次不修改组件源码或重录像素基准。

### 第九批后续修正：Nested Drawer 开关动画（2026-10-29）

<!-- progress-created: 2026-10-29; order: 17 -->

用户指出嵌套动画不对。此前父层只在 `nestedOpenCount > 0` 时立即套用静态 scale/translate，关闭时直接移除变换；中心缩放也未补偿缩放后的露边位置。现保留同一 GraphicsLayer，以连续 depth 动画计算缩放及位移，按抽屉贴靠边设置 TransformOrigin，补偿尺寸收缩并维持每层 16dp peek。Drawer 开关及父层变换采用 450ms、`cubic-bezier(0.22, 1, 0.36, 1)`；被覆盖的内容在 300ms 内淡出，关闭后淡入；Sheet 的独立动画未修改。

多层计数传播到所有祖先。对于同方向纵向嵌套，父层高度随最前面的抽屉动画变化，关闭后恢复已记录的自然高度；临时堆叠高度不回写为自然高度，避免高度反馈。异方向嵌套不强行套用前层高度。

目录 Nested drawers 改为四层同方向示例，桌面 right、手机 down，包含完整主体占位、下一层和 Close 操作；可复制代码同步。浏览器已核对桌面与手机二/四层打开，以及逐层关闭回到首层后的尺寸恢复。

新增 `DrawerMotionTest` 2 项（开关中间态与中途反向；前层高度同步与自然高度恢复），Drawer 数学/状态测试新增祖先计数。最终 **97 项通过**：Motion 2、Sizing 7、Drawer 13、Completion 29、Surfaces Nova 23、目录应用 23；显式像素 verify，JS/Wasm 构建通过。人工核对差异后，仅更新两张嵌套背景浅深基准及对应预览。截图索引见 `phase9-evidence.json` 的 `nested_motion_followup`。本项证据覆盖开关/反向过渡，不宣称完成原生手势真机矩阵。

### 第九批后续修正：大内容 bottom 示例（2026-10-29）

<!-- progress-created: 2026-10-29; order: 16 -->

按用户要求把 bottom 示例换成更大的内容。参考当前官网 Custom Sizes 中 `flex-1 overflow-y-auto p-4` 的结构：固定 DrawerHeader、可收缩滚动主体、固定 DrawerFooter。`Default swipe direction (down)` 与 `Scrollable content` 保留原入口，共用新的 `DrawerLargeContentExample`，包含 24 条活动记录及 Done 按钮；复制代码同步展示完整 recipe。旧的 160dp 滚动区限制移除，抽屉高度由真实内容和视口减 96dp 的上限决定。

目录应用 23 项测试通过，JS/Wasm 构建通过。本次只修改目录示例，没有重录核心像素基准。桌面 1440×1000 和手机 390×844 中均已验证顶部留白、列表滚到 Day 24、固定标题和底部按钮；截图索引见 `phase9-evidence.json` 的 `large_content_followup`。

### 第九批后续修正：侧 Drawer 与窗口间距（2026-10-29）

<!-- progress-created: 2026-10-29; order: 15 -->

用户指出官网左侧 Drawer 是浮动面板。实测当前官网 1440×1000 的 Position 示例，左侧面板为 x=8、y=8、width=384、height=984，四角 24px。此前本地侧面板仍使用固定旧 Nova 样式的贴边规则；按用户指定的当前官网预览，Nova 左右侧面板现保留左/右、上、下各 8dp 间距并使用完整 24dp 圆角。面板自身宽度不含外边距，桌面 384dp，窄屏为原窗口宽度的 75%。

定位由弹层对齐完成，未在面板外增加拦截点击的透明 padding。浏览器已确认点击左侧 x=3 的间隙可关闭；桌面左右和手机左侧截图已保存。纵向 bottom/snap 几何与旧预设规则未改动。

新增左右各一项尺寸回归，`DrawerSizingTest` 共 7 项；最终定向验证范围为 Sizing 7、Completion 29、Surfaces Nova 23、Drawer 数学 12、目录应用 23（合计 94）。仅两张嵌套背景基准因父侧面板位置与圆角变化而更新，人工检查浅深 diff 后单独记录，其他基准未重录。结果与证据见 `web-parity/phase9-evidence.json` 的 `floating_side_followup`。

### 第九批后续修正：Drawer snap points（2026-10-29）

<!-- progress-created: 2026-10-29; order: 14 -->

用户指出 snap 示例仍不对。重新读取固定上游版本 `apps/v4/examples/base/drawer-snap-points.tsx` 后，发现目录示例还停留在 API 文档中的 `[148px, 1]` 片段，实际官方示例为 `[31rem, 1]`。本地已改为 `[496.dp, Fraction(1f)]`，补上示例占位内容和 Close 操作，可复制代码同步更新。

底层 `DrawerSnapPanel` 此前按最大 snap 点直接确定面板高度，绕过纵向 96dp 最大高度留白。Nova 现以视口减 96dp 确定 snap 面板高度，绝对/比例档位仍按原始视口换算并通过平移露出，超出面板的档位钳到最大展开位置；旧预设的尺寸路径保留。

- 新增三项几何回归：148dp 精确露出、0.5 比例按原始视口、1.0 档保留 96dp。连同两项普通 bottom 高度测试，`DrawerSizingTest` 5/5 通过。
- 定向验证合计 **69 项通过**（Sizing 5、Completion 29、Drawer 数学 12、目录应用 23），显式像素 verify，JS/Wasm 构建通过，没有重录基准。
- 浏览器实际鼠标拖动：1440×1000 初始顶边约 504px、展开约 96px；展开后回落 496dp 档，再向下拖动关闭；关闭重开后改变为 390×844，初始顶边约 348px，向上拖动展开仍保留 96px。截图记录于 `web-parity/drawer-snap-corrected-*`，索引见 `phase9-evidence.json`。浏览器鼠标验证不替代原生设备触摸矩阵。

### 第九批后续修正：bottom Drawer 高度（2026-10-29）

<!-- progress-created: 2026-10-29; order: 13 -->

用户指出 bottom 高度仍不对。复核官方纵向 Drawer 的 content-sized / `calc(100dvh - 6rem)` 规则后，发现默认配送示例额外把手机列表限制为 `360.dp`，导致有足够空间时仍提前滚动，抽屉被压到约 520px。移除此限制；列表保留 `weight(fill = false)`，自然高度随内容增长，只有超出视口减 96dp 时才收缩并滚动，固定 header/footer 不参与滚动。可复制代码同步修改，未改动 snap point 的显式高度。

- 浏览器：390×844 下抽屉约 566px，五项和按钮完整可见；390×600 下约 504px（顶部保留 96px），滚动后最后一项与按钮均可见。
- 新增 `DrawerSizingTest` 两项：短内容增高时面板随之增长；长内容达到视口上限且滚动区不覆盖 footer。2/2 通过，目录应用 23 项通过，JS/Wasm 构建通过。
- 截图：`web-parity/drawer-bottom-natural-height-390x844.png`、`drawer-bottom-viewport-cap-390x600.png`、`drawer-bottom-viewport-cap-scrolled-390x600.png`；索引见 `phase9-evidence.json`。本次截图取代前面配送示例的手机高度证据。

### 第九批：关闭上述六个组件的 Web 剩余差异（2026-10-29）

<!-- progress-created: 2026-10-29; order: 12 -->

本批已实现并定向验证上次列出的 Web 缺口，六项 Web 清单改为已勾选。该勾选不代表整站或原生平台发布验收。

- **共用弹层**：新增 `OverlayHost`（在窗口根、Theme 内包裹应用）。记录背景 GraphicsLayer，Nova scrim 使用 4dp BlurEffect；每层 portal 单独记录背景，使嵌套 Drawer 模糊父层而不是把父层盖掉。无 Host 的调用继续使用 Popup 和纯 scrim；非模态完整交互应接入 Host。
- **Dialog / Drawer 焦点**：按声明顺序进入首个可操作元素；无操作项仍可键盘关闭；Tab 留在模态窗口内；关闭并完成退场后回到实际触发器。Nova 字符串 DialogTrigger 移除嵌套 button；嵌套 Drawer 关闭后返回父层入口。
- **非模态**：使用 Host 中的 portal，不创建阻塞背景的全屏原生窗口；外部按下监听放在共同祖先，不消费输入，支持背景点击、失焦关闭与 preventDefault。菜单的非模态模式也保留键盘导航和 Escape。子 portal 的外部点击按所属分支处理。
- **Command**：高亮项 BringIntoView 只由用户导航触发；初始首项高亮不会把目录页滚到离屏示例。Home/End 可到长列表首尾且输入框保留焦点。受控 value 不在调用者确认前改变；保留 TextFieldValue 的 composition/selection，IME 组合期间 Enter/方向键不会提前提交。
- **Context Menu**：使用触发器窗口坐标，处理偏移触发器及窗口边缘翻转；RTL 左开右关和箭头方向；字母前缀定位；按 Compose 长按手势取消规则处理触屏移动/取消；关闭后恢复触发器焦点。
- **Collapsible**：外部受控收起时也会将已有内容焦点还给触发器；保留内容状态并移出隐藏语义/焦点路径；回归中途反转的 200ms 高度动画。
- **Date Picker / Calendar**：目录 recipe 使用系统时区（kotlinx-datetime 仅为 app 依赖，core 不增加日期库）。`CalendarLocale` 可注入月份、星期、caption、显示日期、读屏日期与周起始日，提供英语/中文；连续范围填补日期格之间的背景；多月在窄容器换行；start/endMonth 按整月解释并限制导航；隐藏 outside days 不进入语义树。键盘光标与真实日按钮焦点一致；`LocalCalendarAutoFocus` 仅在弹层中启用，内联示例不抢焦点。
- **中文示例**：Wasm 的 Geist 不含中文字形，已为该例打包 19,588 bytes 的静态日期字形子集（Noto Sans SC 派生并重命名，OFL 随资源保留）；任意中文业务内容仍应由调用者提供完整字体。新增 Long list / Localized date picker 可运行示例。Drawer 默认窄屏示例为滚动列表保留 footer 空间并显式裁剪，固定操作区补 popover 背景，避免列表透到按钮后面。

**验证范围**：本批 `SurfacesCompletionTest` 覆盖初始/恢复/Tab 焦点、非模态背景点击、非模态菜单键盘、长列表滚动、受控值、IME 协议、RTL/字母定位、菜单坐标、keepMounted、动画反转、月边界、本地化、隐藏日期、嵌套和 snap；Drawer 纯数学测试补顺序吸附物理顺序及取消短拖动。目录应用新增跨午夜/跨年时区回归。

新增 8 张 `surfaces_completion_*` 浅深基准（本地化范围、Dialog/Drawer 背景、嵌套背景）。上一批 Dialog/Drawer 的 4 张 Nova 基准在人工检查 diff 后更新：差异为正确的初始焦点 ring；没有批量录制旧预设。其他 Form/Groups/Choice 像素基准保留。

浏览器证据索引：[phase9-evidence.json](web-parity/phase9-evidence.json)。额外检查真实非模态背景导航、长列表 End 可见末项、中文日期字形、手机与桌面背景模糊、菜单长按/取消及关闭恢复。

**发布前平台验证（实现已具备，不混作 Web 未实现项）**：Android/iOS 原生 GPU blur、VoiceOver/TalkBack、系统候选窗与真实多语言 IME、设备触控/系统中断仍需对应设备实测。当前证据为 JVM UI/像素与 Chromium Web；没有声称已完成原生真机矩阵。


第九批验证汇总：**151 项通过**（Completion 29、上一批 Surfaces 23、Choice 27、Groups 23、Form 14、Drawer 数学 12、目录应用 23），使用显式像素 verify，JVM/JS/Wasm 构建通过。
旧预设单独复核 21 项：6 项通过、15 项失配。包含原有 13 项历史失配，以及共用初始焦点修复带来的 2 项 Drawer 焦点外观变化；历史 actual 中 11/13 与上批相同，CommandDialog 另外 2 张因首项聚焦而变化。没有重录旧预设，详见 [本批旧基准对照](web-parity/surfaces-phase9-legacy-comparison.json)。

### 第八批：六个展开与弹层组件（2026-10-29）

<!-- progress-created: 2026-10-29; order: 11 -->

对照当前官方 Base UI 页面及固定 registry commit `96da9cc7f1aa10580a3741664e2a77c721206c00` 的 UI/CSS。
本批六项均完成默认页面和主要行为修正；清单继续保留未勾选，表示以下剩余矩阵尚未全部关闭。

| 组件 | 本批交付 | 自动验证 |
| --- | --- | --- |
| Collapsible | 官方订单示例；Nova Content 无内建间距、200ms 高度动画；禁用根传递到按钮、FieldSet 禁用继承；隐藏 keepMounted 内容移出语义并阻止焦点进入 | 受控只回传意图、非受控展开/收起、根禁用 |
| Command | 官方六条带图标菜单；InputGroup 搜索框、分组/空状态/滚动上限；默认首个可用项高亮；修复自定义 value、重组回调导致高亮丢失、旧高亮残留；Home/End、禁用跳过 | 自定义 value 点击、Home/End/Enter、过滤空分组、重组保留高亮、默认首项选择 |
| Context Menu | 官方虚线触发区域与完整菜单；Nova 192px 面板/阴影/14px 文字/尾部勾选/快捷键；右键、长按、Shift+F10/Menu；上下/Home/End 与子菜单右开左关 | Shift+F10 打开/禁用跳过、复选更新且保持打开、父子菜单导航独立 |
| Date Picker | 保持 Popover + Calendar recipe；212px 触发器、尾部 Chevron、零 padding 弹层、28px 日期格；选中日期回填，默认按当前 UTC 日期初始化月份；保持官方选择后仍打开的行为 | 日期格尺寸与选择回传；Calendar 随 Nova 显示今日底色 |
| Dialog | 官方 Edit profile；16/16 Medium 标题、14/20 描述、28px X；黑色 10% 遮罩、手机左右 16px 留白；横向铺满的 Footer 背景/边框；手机默认动作逆序堆叠 | Escape 关闭；浅深色像素基准 |
| Drawer | 官方配送时间示例、选项卡 16px 内边距/8px 间隔/选中底色；桌面右侧、手机底部；修复桌面面板错误占 75% 宽，>=640px 为 384px；方向拖动柄为 96×4；新增 `DrawerFooterColumn`，保留旧 RowScope API；确认后回显选择 | 桌面 384px 尺寸；浅深色像素基准 |

验证结果：`SurfacesNovaParityTest` 23 项、Choice 27 项、Groups 23 项、Form 14 项、目录应用 21 项，共 **108 项通过**。
新增 **8 张 Nova 基准**（inline / context / dialog / drawer × 浅深），已使用显式 `roborazzi.test.verify=true` 校验；预览和 metadata 已同步。
JVM、JS、Wasm 编译及 Web development distribution 构建通过。最终配送卡间距调整后再次通过目录应用测试和 JS/Wasm 构建。

旧主题六组共 21 项中 8 项通过、13 项为修改前已有的像素失配；13 张修改前/后 actual 输出 SHA-256 全部相同，见
[surfaces-legacy-baseline-comparison.json](web-parity/surfaces-legacy-baseline-comparison.json)。未覆盖或重录旧 golden。

浏览器证据见 [phase8-evidence.json](web-parity/phase8-evidence.json)：1440×1000、1095×687、390×844，六页浅深主题；
打开状态与操作结果另存 `local-*-phase8-open-*` 和 `local-*-phase8-interaction-*`。

明确剩余：Dialog/Drawer 背景模糊（当前仅黑色 10% scrim）；完整初始/恢复焦点和非模态矩阵；Command 长列表高亮滚动跟随、IME；
Context Menu RTL/碰撞/长按取消；Date Picker 多月、连续范围、本地化和时区；Collapsible 动画中断与 Drawer snap/嵌套/手势取消完整矩阵。
已有这些能力的实现或历史测试不等于本批完成了官方逐状态验收。

### 第七批实现（2026-10-29）

<!-- progress-created: 2026-10-29; order: 10 -->

- Input OTP：Nova 32dp 连体格、lg 两端圆角、单条边框接缝、14/20 数字；6 格总宽 192dp。根按 Row 排列多个 Group，两组 3 格 + Minus 总宽 224dp；新增根作用域 InputOTPSeparator，原 Group 作用域签名保留。
- Input OTP：BasicTextField 使用 TextFieldValue 记录实际选择范围；点击格子后替换相应字符，支持筛选/限长粘贴、退格与受控清空后的光标范围。密码模式增加真正的 PasswordVisualTransformation 和密码语义，禁用继承 FieldSet 并按 Nova 整体降透明度。
- Input OTP：空活动格显示 1×16dp 假光标；默认演示为官网的 123456，分隔符示例改成两个独立 Group。
- Select：Nova 32/28dp 触发器，10/8dp 左右内边距、6dp gap、14/20 文本、16dp 轮廓箭头、暗色 input/30、hover input/50、禁用和持久错误 ring；根 style/公开 API 保留。
- Select：Nova 面板采用 lg 圆角，选项行 28dp、标签盒 24dp，滚动箭头只在可滚动方向显示；测量触发器宽度及当前选项实际位置，默认 alignItemWithTrigger 使当前行覆盖触发器，并在打开后固定该偏移以免滚动时追随。
- Select：方向键打开、跳过禁用项、Home/End、Enter/Space 选择、Escape 及关闭后恢复触发器焦点；修复只提供 onOpenChange 时无受控 open 却打不开的问题；键盘选择尊重 onSelect 的阻止逻辑。
- Select：浏览器发现 Enter 选择后，释放 Enter 会因焦点已回触发器而重新打开。已在弹层/触发器消费该次释放事件，并补“提交后保持关闭”的回归；真实 Web 已复测 Apple 选择后关闭。
- Select 默认演示为 192dp 的水果选择器，含 Fruits 标签和可清除的占位项。占位项显式通过 onSelect 清空调用方的 nullable 状态，保留现有 onValueChange: (T) -> Unit 的公开签名。
- Combobox：新增 required editable 参数的 ComboboxInput 重载，原搜索输入重载及按钮配方继续可用。可编辑组合使用 InputGroup，输入框保留焦点、输入即打开筛选列表、方向键高亮、Enter 提交、Escape 还原选择；支持 showTrigger / showClear，继承禁用和错误样式。
- Combobox：内联编辑时弹层不夺焦点，默认面板按锚宽 + 28dp 取宽，28dp 选项、右侧勾选、居中的空结果；autoHighlight 可直接 Enter 提交，键盘选择也尊重 onSelect。关闭清理查询，选中值回显。
- Input Group：修复带 24dp 按钮的 inline addon 把分组撑高的问题；只要中间控制项为单行高度，Nova 总高度仍为 32dp，附件居中溢出布局，与固定 h-8 对齐。
- 小型控制图形使用内部 outline 路径（Chevron / Check / X / Minus），不引入图标库依赖，也不替换旧公开图标。三页描述和 API 表已同步，默认内容与官网一致。

#### 第七批已知残差

- OTP 的假光标目前静态，尚未加入 blink；invalid API/分组错误 ring、完整 IME/自动填码、复杂选择/粘贴与手机键盘矩阵尚未关闭。Native 字体栅格化及接缝的半像素居中差异仍需更细的比较。
- Select 的复杂碰撞、长列表选中项初始滚动、键入搜索、动态重排和完整 RTL/辅助技术矩阵仍待核对。当前对齐受窗口边缘约束，不能把默认示例通过称为所有定位行为完成。
- Combobox 的多选 chip/换行、完整键盘焦点和无障碍 active-descendant 映射、任意类型/null 选项、组合内容的所有禁用项及主题变化矩阵仍待补齐。旧按钮配方不等同于本批新增的内联编辑入口。
- 浮层仍沿用项目的原生阴影与不透明背景；官网 translucent/backdrop 效果、叠层阴影和环形边线尚有差异，不将其标为像素完全一致。
- 本批开始先运行旧截图作为基线，结束后比较实际 PNG：Select 7 + Combobox 10 共 17 张既有失配的 SHA-256 全部一致，未重录旧基准；Input OTP 的旧 7 项显式验证通过。
  [机器证据](web-parity/choice-legacy-baseline-comparison.json)。

#### 第七批验证与截图

- 最终 92 项相关检查通过：ChoiceNovaParity 27、GroupsNovaParity 23、FormNovaParity 14、旧 InputOTP 7、目录应用 21。
- 12 张新 Nova 快照显式 verify 通过：矩阵浅深色 2，OTP/Select/Combobox 焦点浅深色 6，Select/Combobox 展开浅深色 4。新快照生成文档预览并按 Nova 记录元数据。
- JVM / JS / Wasm 编译与 Wasm Web distribution 通过；保留现有 AGP、Text 内部 API、Gradle 和 Webpack 警告。
- 浏览器：桌面、手机浅深色及中等宽度；OTP 点选替换、Combobox 筛选/键盘提交、Select 行对齐与 Enter 选择后保持关闭。最终截图和尺寸清单见 [phase7-evidence.json](web-parity/phase7-evidence.json)。
- [Input OTP：默认](web-parity/compose-input-otp-phase7-light.png)、[编辑](web-parity/compose-input-otp-phase7-edited.png)、[手机深色](web-parity/compose-input-otp-phase7-mobile-dark.png)、[官方](web-parity/shadcn-input-otp-phase7-reference.png)。
- [Select：默认](web-parity/compose-select-phase7-light.png)、[展开](web-parity/compose-select-phase7-open.png)、[选中](web-parity/compose-select-phase7-selected.png)、[手机深色](web-parity/compose-select-phase7-mobile-dark.png)、[官方展开](web-parity/shadcn-select-phase7-open-reference.png)。
- [Combobox：默认](web-parity/compose-combobox-phase7-light.png)、[筛选](web-parity/compose-combobox-phase7-filtered.png)、[选中](web-parity/compose-combobox-phase7-selected.png)、[手机深色](web-parity/compose-combobox-phase7-mobile-dark.png)、[官方](web-parity/shadcn-combobox-phase7-reference.png)。
- 共 24 张浏览器截图，尺寸与主题底色已核对；代码及测试证据保存在 phase7-evidence.json。未提交、推送或发布。本地服务继续保留在 127.0.0.1:8765。

### 第六批实现（2026-10-29）

<!-- progress-created: 2026-10-29; order: 9 -->

- Toggle Group：Nova 连体项使用 lg 外侧圆角、8dp 横向内边距，Outline 项按 1dp 重叠排布，避免 shared border + hairline 形成额外接缝。非连体组保留默认 8dp 间距；根 style 在非 Outline 模式也生效。
- Toggle Group：Single 可再次取消选择；方向键仅移动焦点、循环并跳过禁用项，Space 由 Toggle 切换；支持 Home/End、RTL 与纵向方向键。继承 FieldSet 禁用。原公开重载和默认 Multiple 行为保留。
- Slider：Nova 12dp 白色滑块、4dp 轨道、3dp hover/active/focus ring；多滑块填充准确连接中心。新增 NovaThumbSize，保留原 ThumbSize=16dp。修复 minTouchTarget=null 时根高度为零，纵向最少 160dp。
- Slider：方向键、Home/End、PageUp/PageDown、Shift+方向键、RTL/反转及每个滑块 SetProgress；按 step 吸附并限制相邻滑块间距。键盘 commit 使用请求后的值，避免同步回调拿到旧受控值。指针按下聚焦相应滑块，并在结束/取消时清理 pressed 状态。
- Input Group：Nova input 边框、dark input/30 与禁用底色、持久 destructive ring；内部 Input/Textarea 的 invalid / enabled 汇总到根，并在移除时解除注册。禁用传递和 FieldSet 继承覆盖真实输入。
- Input Group：Inline 附件外侧 8dp，输入侧 6dp；Block 附件水平 10dp、外侧 8dp。内部输入去除重复边框，整体 inline 高度为 32dp，block 输入允许按内容增长。点击附件聚焦可用输入，附件按钮保持自己的点击处理。
- Input Group：addon / text 使用 14/20，内置 Icon 为 16dp；组内按钮按 xs / sm / icon-xs / icon-sm 设置实际尺寸与圆角。旧预设字体和布局保留。
- Button Group：Nova lg 外角、Outline 负 margin 同时回收布局占位，避免第三项开始留下额外宽度；Text 使用 border、10dp 水平内边距与居中对齐。
- Button Group：Nova 横排先测量真实子项，再拉伸分隔线，解决 intrinsic measurement 将嵌套图标按钮压至图形高度的问题。旧预设继续使用原布局。
- 四页默认示例对齐官方：未选中的 Outline 格式图标、75/100 Slider、Search + 12 results、返回/Archive/Report/Snooze/More Options 的嵌套按钮组。按钮组菜单可展开并用 Escape 关闭，Label As 使用现有子菜单 API；手机隐藏返回按钮。补四页 API 表，描述文字使用官方当前文案。

#### 第六批已知残差

- Toggle Group 尚未完成 Tab 单入口、按可视顺序动态重排、完整纵向 stretch/焦点矩阵。Arrow/Home/End 能力不表示全部 Base UI 复合焦点规则均已关闭。
- Input Group 的 button/kbd 负 margin、全部组合下的禁用-opacity 与 focus-visible 输入来源区分、复杂多控件布局，仍待单独验收。特殊有色容器仍有前批记录的透明底色组合限制。
- Button Group 混合任意原始 Button/Input/Select 还需显式 item/shape 组合。默认菜单条目图标与完整 Label As 内容已在 Button r2 补齐；共享菜单尺寸/碰撞、纵向混合布局及全部尺寸矩阵仍待核对。
- Slider 的多滑块完整无障碍标签与焦点顺序、动态增删时的焦点保留、垂直/反转/RTL 的完整手势矩阵仍待补齐。本批没有声称所有状态像素一致。
- 旧 ButtonGroup 6、InputGroup 12、Slider 4 共 22 张快照失配，用原组件源码复现后实际渲染 SHA-256 全部一致，未重录旧基准。
  [机器证据](web-parity/groups-legacy-baseline-comparison.json)。对照中保留此前主题/Text/Input 修复；InputGroupLayoutState 仅作编译桥，原根组件不提供它。
- 本地正确入口使用一级路径（如 /toggle-group），不是 /components/toggle-group；错误的多级路径会让相对脚本地址返回 404。此前启动回复中的链接已在本批更正。

#### 第六批验证与截图

- 最终 77 项相关检查通过：GroupsNovaParity 23、FormNovaParity 14、SelectionNovaParity 19、目录应用 21。
- 新增 10 张 Nova 快照显式 verify 通过（状态矩阵浅深色 2、Slider/Input Group/Toggle Group/Button Group 焦点浅深色 8）；同时重验此前表单与选择控件快照。未改动任何旧跟踪基准。
- JVM / JS / Wasm 编译及 Wasm Web distribution 通过。既有 AGP 实验、Text 内部 API、Gradle 兼容性及 Webpack 警告保留。
- 浏览器最终版本保存桌面 1440×1000 与手机 390×844 浅深主题、1095×687 浅色。实测主题切换、搜索附件聚焦输入、Slider ArrowRight、Toggle Group 焦点移动/Space，以及组合按钮菜单和 Escape。
- [Toggle Group：默认](web-parity/compose-toggle-group-phase6-light.png)、[键盘](web-parity/compose-toggle-group-phase6-keyboard.png)、[手机深色](web-parity/compose-toggle-group-phase6-mobile-dark.png)、[官方](web-parity/shadcn-toggle-group-phase6-reference.png)。
- [Slider：默认](web-parity/compose-slider-phase6-light.png)、[键盘](web-parity/compose-slider-phase6-keyboard.png)、[手机深色](web-parity/compose-slider-phase6-mobile-dark.png)、[官方](web-parity/shadcn-slider-phase6-reference.png)。
- [Input Group：默认](web-parity/compose-input-group-phase6-light.png)、[附件聚焦后编辑](web-parity/compose-input-group-phase6-edited.png)、[手机深色](web-parity/compose-input-group-phase6-mobile-dark.png)、[官方](web-parity/shadcn-input-group-phase6-reference.png)。
- [Button Group：默认](web-parity/compose-button-group-phase6-light.png)、[菜单](web-parity/compose-button-group-phase6-menu.png)、[手机深色](web-parity/compose-button-group-phase6-mobile-dark.png)、[官方](web-parity/shadcn-button-group-phase6-reference.png)。
- 共 28 张浏览器截图，尺寸与底色检查见 [phase6-evidence.json](web-parity/phase6-evidence.json)。10 张新快照已生成文档预览，并在四项组件元数据中标明 Nova。未提交、推送或发布。

### 第五批实现（2026-10-29）

<!-- progress-created: 2026-10-29; order: 8 -->

- Checkbox：Nova 4dp 圆角、16dp 本体、14dp 勾选图形；input 边框及 dark input/30 底色；错误 ring 为 3dp、浅 /20 深 /40，选中错误状态保留 primary 边框。勾选图形使用 Lucide Check 的同一条路径和等比线宽。
- Radio：Nova 纵向 gap 8、input 边框/底色、选中 primary、8dp 圆点和错误状态；增加方向键切换、循环、跳过禁用项、Home/End 与 RTL。通过实际官网确认默认竖排也响应右方向键。
- Switch：Nova 默认 32×18.4、Sm 24×14；本体扣除 border 后，未选中滑块从 0 开始、选中移动到 14/10；深色未选中滑块为 foreground，选中为 primaryForeground；位移动画 150ms。
- Toggle：Nova 14 Medium（Sm 12.8）、图标 16/14、Row gap 4，选中背景 muted，Outline 边框 input，小号圆角 min(md,12)。新增 required invalid 参数的组合内容重载，原公开签名保留；测试原 Toggle(true) 仍表示 pressed，避免重载改变原位置参数语义。
- Checkbox/Switch 文本重载共用正确的本地/受控状态；点击标签切换并把焦点转给真实控件，标签不再额外占据 Tab 焦点。Radio 标签同样请求真实 radio 焦点。描述版使用 FieldLabel 与 14/20 描述。
- 四页默认演示与官方一致：Accept terms、Comfortable 选中的三项密度、Airplane Mode、Small Outline Bookmark（选中后图标填充），并补 Kotlin API 参数表。
- Nova 默认启用焦点 ring；Ring 的旧默认值及其他预设保持原状。显式 ring.enabled=false 仍可关闭，已覆盖运行中切换。

#### 第五批已知残差

- Radio Group 尚需补 Tab 单入口、动态重排及完整焦点矩阵；本批键盘能力不等于完整 Base UI 行为验收。
- Toggle 的 inline-start/end 非对称内边距仍待与 Button 一并实现；完整 hover/pressed、RTL 以及选择控件的 choice-card/group/table 组合未关闭。
- 原生 dropShadow 的内侧需要主题底色覆盖；特殊有色容器的透明底色组合仍需单独检查。
- 旧 Checkbox/Radio/Switch 的 14 项快照通过。旧 Toggle/ToggleGroup 的 8 张基准失配，用改动前实现复现后实际渲染 SHA-256 全部一致；保留原基准。
  [机器证据](web-parity/toggle-legacy-baseline-comparison.json)。为了编译新增回归，基线仅额外保留未参与旧测试的 invalid API 转发桥；测试的旧实现与样式均来自改动前源码。

#### 第五批验证与截图

- 本批最终 54 项检查通过：SelectionNovaParity 19、旧 Checkbox/Radio/Switch 14、目录应用 21。此前 55 项 Nova/组件相关回归也已重跑通过；本轮相关检查共 109 项分批通过。
- 新增 10 张快照显式像素 verify 通过：状态矩阵浅深色 2、四种控件焦点浅深色 8；没有重录旧 Toggle 的 8 张失配基准。
- 覆盖本体尺寸、图标尺寸/字重/间距、标签点击与真实控件焦点、三态 Space、FieldSet 禁用继承、Radio 循环/禁用跳过/RTL/Home/End、受控状态回调、Nova ring 显式关闭及实时切换、旧 Toggle 位置参数兼容。
- JVM / JS / Wasm 编译及 Wasm Web distribution 通过；保留现有 AGP、Text 内部 API、Gradle 和 Webpack 警告。
- 浏览器实测标签切换、Switch Space、Radio 标签聚焦后 ArrowRight 选中 Compact、Bookmark 按下/图标填充、运行中主题切换。保存 1440×1000 / 390×844 浅深主题，另存 1095×687 浅色。
- [Checkbox：默认](web-parity/compose-checkbox-phase5-light.png)、[选中](web-parity/compose-checkbox-phase5-checked.png)、[手机深色](web-parity/compose-checkbox-phase5-mobile-dark.png)、[官方](web-parity/shadcn-checkbox-phase5-reference.png)。
- [Radio Group：默认](web-parity/compose-radio-group-phase5-light.png)、[键盘选择](web-parity/compose-radio-group-phase5-keyboard.png)、[手机深色](web-parity/compose-radio-group-phase5-mobile-dark.png)、[官方](web-parity/shadcn-radio-group-phase5-reference.png)。
- [Switch：默认](web-parity/compose-switch-phase5-light.png)、[标签开启](web-parity/compose-switch-phase5-checked.png)、[Space 关闭](web-parity/compose-switch-phase5-keyboard.png)、[手机深色](web-parity/compose-switch-phase5-mobile-dark.png)、[官方](web-parity/shadcn-switch-phase5-reference.png)。
- [Toggle：默认](web-parity/compose-toggle-phase5-light.png)、[按下](web-parity/compose-toggle-phase5-pressed.png)、[手机深色](web-parity/compose-toggle-phase5-mobile-dark.png)、[官方](web-parity/shadcn-toggle-phase5-reference.png)。
- 截图清单及尺寸检查见 [phase5-evidence.json](web-parity/phase5-evidence.json)。未提交、推送或发布。

### 第四批实现（2026-10-29）

<!-- progress-created: 2026-10-29; order: 7 -->

- Input：Nova 的 input 边框、dark input/30 底色、disabled input/50（dark /80）底色、invalid 红色边框及 3dp ring（浅 /20、深 /40）。原生 dropShadow 内侧由主题底色覆盖，避免焦点状态把整个输入区染灰/染红。
- 输入文本与 placeholder 使用同一份明确字体/字号；桌面 14/20，窗口小于 768dp 时 16/24，仍保持 32 高。官方手机页面 computed style 已确认 16px/32px。
- Textarea：Nova 上下 8、左右 10；占位与内容顶部对齐，最小 64，随多行输入增长。旧预设保留原有垂直对齐。
- Label：Nova 14 Medium、leading-none、letterSpacing 0；disabled 使用整体 /50。FieldLabel 单独使用 leading-snug 19.25，描述和错误为 14/20。
- FieldSet / FieldGroup / Field / FieldContent 的 Nova gap 分别为 16 / 20 / 8 / 2；Responsive 的 container md 阈值为 448，区别于页面媒体断点 768。嵌套 FieldSet 不再解除父级 disabled。
- Input/Label/Textarea 默认展示换为官方 API Key、可切换的 Accept terms、纯 Textarea；Field 新增 Payment Method/Billing Address/Comments 表单及月年选择、Cancel 重置。补四页 Kotlin API 参数。
- 修复目录中 Autofocus 演示在页面加载时抢焦点并自动滚向底部的问题。演示改为点击 Try autofocus 后挂载，库组件的 autoFocus 行为不变；新增两项目录回归。

#### 第四批剩余范围

- Field 的 choice-card / nested group gap-4、相邻 Legend/Description 的负 margin、横向内容顶对齐、Separator 和完整 RTL/错误组示例仍待逐项核对；本批不勾选整组件完成。
- 默认 Label 中 Checkbox 仍依赖下一批 Checkbox 对齐；Select 的完整 Nova 状态亦未在本批关闭。
- 旧表单截图显式 verify 有 25 张失配。用改动前五个表单源码复现，并保留本轮其余环境，最终 25 张实际渲染 SHA-256 全部与改动前一致；未重录旧基准。
  证据：[forms-legacy-baseline-comparison.json](web-parity/forms-legacy-baseline-comparison.json)，以 finalIdentical 字段为最终比较。
- 初次比较中 6 张曾因多行顶部对齐影响 Vega；已将该行为限制为 Nova，复查后全部恢复改动前渲染。没有将原有失配称为全部通过。

#### 第四批验证与截图

- 本批 35 项定向检查通过：FormNovaParity 14、目录应用 21（含新增 Autofocus 2 与 API Key 密码语义 1）；此前 41 项基础/第三批 core 回归也已重跑通过，共 76 项相关检查分批通过。
- 新增 6 张快照显式像素验证通过：表单矩阵浅深色、Input 焦点浅深色、Textarea 焦点浅深色。没有刷新失配的旧 25 张表单截图或旧 Badge 4 张。
- JVM / JS / Wasm 编译与 Wasm Web distribution 通过。已有 AGP 实验项、Text 内部 API、Gradle 兼容性和 Webpack 警告仍单独保留。
- 浏览器：1440×1000 和 390×844 浅深主题，另存 1095×687 浅色；实测默认 API Key 编辑及密码遮挡、Textarea 三行输入与高度增长、点击 Label 切换复选框、Payment 表单编辑/Cancel 重置。
- 官方默认 API Key 实为 password；最后已同步此行为并添加密码语义回归。手机官方 Input computed style 为 16px/32px。
- Autofocus 演示经修复后，加载页面停留在默认示例；显式 Try 后才请求焦点。浏览器初次尝试曾因该问题跳页，最终截图已替换为修复后状态。
- [Input 桌面](web-parity/compose-input-phase4-light.png)、[密码及焦点](web-parity/compose-input-phase4-focus.png)、[手机深色](web-parity/compose-input-phase4-mobile-dark.png)、[官方](web-parity/shadcn-input-phase4-reference.png)。
- [Textarea 桌面](web-parity/compose-textarea-phase4-light.png)、[三行输入](web-parity/compose-textarea-phase4-edited.png)、[手机深色](web-parity/compose-textarea-phase4-mobile-dark.png)、[官方](web-parity/shadcn-textarea-phase4-reference.png)。
- [Label 桌面](web-parity/compose-label-phase4-light.png)、[点击后选中](web-parity/compose-label-phase4-checked.png)、[手机深色](web-parity/compose-label-phase4-mobile-dark.png)、[官方](web-parity/shadcn-label-phase4-reference.png)。
- [Field 桌面](web-parity/compose-field-phase4-light.png)、[手机深色](web-parity/compose-field-phase4-mobile-dark.png)、[官方](web-parity/shadcn-field-phase4-reference.png)。
- 新增截图清单、尺寸及底色检查见 [phase4-evidence.json](web-parity/phase4-evidence.json)。未提交、推送或发布。

### 第三批浏览器检查（2026-10-29）

<!-- progress-created: 2026-10-29; order: 6 -->

- 本地当前工作树：Card / Code Block / Trace，1440×1000、390×844，浅深主题截图；另存 1095×687 浅色检查。
- 实测 Code Block 切换自动换行，长行在手机宽度内折行；Trace 点击展开、详情导线和箭头；运行中切换浅深主题。
- 确认 Geist Mono 修复在 Wasm 中生效，代码区和下方 Kotlin 代码不再使用普通字体回退。
- 官方同尺寸浅色参考与本地截图均存于 `docs/web-parity/*phase3*`。此处是基础布局/交互通过，不代表所有适用状态矩阵已关闭。
- 剩余差异：官方默认 Trace 含完整工具调用/来源示例，本地仍为简化演示；Code Block 尚少下载、受限高度与复合演示；Card 表单依赖 Input/Field/Label 的剩余尺寸核对。浏览器页签仍显示统一 Components 标题，逐路由标题待补。

本批截图：

- [Card：桌面浅色](web-parity/compose-card-phase3-light.png)、[深色](web-parity/compose-card-phase3-dark.png)、[手机浅色](web-parity/compose-card-phase3-mobile-light.png)、[手机深色](web-parity/compose-card-phase3-mobile-dark.png)、[官方参考](web-parity/shadcn-card-phase3-reference.png)。
- [Code Block：桌面浅色](web-parity/compose-code-block-phase3-light.png)、[换行](web-parity/compose-code-block-phase3-wrap.png)、[深色](web-parity/compose-code-block-phase3-dark.png)、[手机浅色](web-parity/compose-code-block-phase3-mobile-light.png)、[手机深色](web-parity/compose-code-block-phase3-mobile-dark.png)、[官方参考](web-parity/shadcn-code-block-phase3-reference.png)。
- [Trace：桌面浅色](web-parity/compose-trace-phase3-light.png)、[展开详情](web-parity/compose-trace-phase3-expanded.png)、[深色](web-parity/compose-trace-phase3-dark.png)、[手机浅色](web-parity/compose-trace-phase3-mobile-light.png)、[手机深色](web-parity/compose-trace-phase3-mobile-dark.png)、[官方参考](web-parity/shadcn-trace-phase3-reference.png)。

### 第三批自动验证（2026-10-29）

<!-- progress-created: 2026-10-29; order: 5 -->

- 59 项定向回归通过：core 41、app 18。新增三套组件测试共 19 项（Card 5、Code Block 8、Trace 6）。
- 新增 8 张快照显式像素 verify 通过：Card 2、Code Block 4（含浅深色焦点）、Trace 2；此前的 11 张相关快照也通过。
- 覆盖 Footer 边界与 Action 位置、嵌套小 Card 字号隔离、水平滚动容器的无界测量；编号/空行、窄宽换行、复制原始空白、两秒反馈与权限拒绝；Trace 受控/非受控、Enter 展开及 disabled。
- 已修复实际失败：0.dp 等同 Dp.Hairline 导致黑细边；Infinity 减法破坏 Constraints 哨兵；Lucide 图标名应为 TextWrap。未修改测试断言规避问题。
- JVM/JS/Wasm 与 Web distribution 已通过；追加 Geist Mono 后又运行 app 18 项回归、JS 编译及 Wasm distribution，全部通过。
- 保留旧 Badge 4 张失配基准。没有宣称整库所有截图通过；没有发布、提交或推送。

### 第三批实现（2026-10-29）

<!-- progress-created: 2026-10-29; order: 4 -->

- Card：Nova 标题 16/22 Medium、小尺寸 14/19.25；描述 14；Header 间距 4；Action 置于标题右侧；Footer 铺满卡片底部，带 muted/50 底色、分隔线和 16/12 内边距。原有公共签名保留，Vega 路径保留。
- Card 默认示例换为官方登录内容，忘记密码放回 Password 标签同行；补公开参数表。未把 Input/Field 本身尚存的字号和间距差异算作 Card 完成。
- Code Block：新增公开 Root/Header/Title/Actions/CopyButton/Content/Line/Footer；两种尺寸、destructive、编号起始值、空行保留、差异/高亮、横向滚动或换行、选择、焦点 ring、复制原文及两秒成功反馈；复制被拒绝时不显示成功。富文本行显式指定 lineNumber。
- Code Block 默认展示同一段 13 行 route.ts 代码，并可切换换行。下载、Height/Show More/Max Height 和 Tabs/Trace/Message 组合仍待补齐。
- Trace：新增 Line/Icon/Title/Description 与 Collapsible/Trigger/Content；running shimmer、error、pending，200ms 展开及箭头旋转，复用已有受控/非受控 Collapsible；补 API 表。完整官方工具调用示例仍待补。
- 展示应用新增同版本 Geist Mono 资源，修复 Wasm 的平台 Monospace 回退；公共 CodeBlockContent 保留 textStyle 供应用提供字体。默认底色使用主题 background，避免原生 dropShadow 的内侧透色；非默认底色容器可通过 style 覆盖。
- 元数据生成器改为读取当前 RouteKey 注册格式和组件子目录，恢复真实 variants；每张预览记录 Vega/Nova，避免把 Nova 截图标作 Vega。只生成本轮相关的新增预览，不刷新旧 Badge 基准。

### 第一批定向验证（2026-10-28）

<!-- progress-created: 2026-10-28; order: 3 -->

- 14 项测试通过：CatalogPreview 2、TextColorInheritance 1、ButtonNovaParity 6、RippleShape 5。
- 显式启用 `-Proborazzi.test.verify=true`：Nova 2 张与 RippleShape 5 张全部通过。
- JVM / JS / Wasm 编译通过；最终 Wasm development distribution 构建通过。
- 新增 Nova 基准已检查；原有 RippleShape 基准没有重录。
- 当前已有 AGP 实验功能、Text 内部 API suppression、Webpack 和 Gradle 兼容性警告；
  本轮没有扩大到构建工具升级。
- 尚未执行全库全部 screenshot suite；其他依赖 Nova tokens/字体的旧截图需要逐项检查，
  当前定向通过不能替代整库验收。

#### 验证记录

- 2026-10-28：首轮 JVM/Wasm 编译发现 Shadow 不支持 copy；已定位为 API 使用问题，修正后重试。
- 尚未执行线上发布、Git 提交或推送。

### 第二批最终验证（2026-10-28）

<!-- progress-created: 2026-10-28; order: 2 -->

- 31 项定向测试通过：目录应用 9 项、组件库 22 项。
- 显式像素验证 11 张通过：Nova Badge 2、Nova Button 2、RippleShape 5、Spinner 2。
- 只新增 Nova Badge 两张基准；没有刷新 4 张已知失配的旧 Badge 基准。
- JVM / JS / Wasm 编译与 Wasm Web 构建通过。
- 浏览器确认：无需先点画布即可 Cmd/Ctrl+K；输入 Badge 后方向键/Enter 跳转；
  点击 Usage 目录项滚动；切页再后退恢复到相同位置。
- Usage 跳转后的截图与浏览器后退恢复截图 SHA-256 相同，说明本次测试位置完整恢复。
- 390px 浏览器已实测手机导航打开、Escape 关闭、全局搜索打开；实际截图已保存。
- 搜索外部点击关闭后，在入口上按 Enter 可再次打开，已通过实际截图确认焦点恢复。
- Overlay 保留原有两参数入口，通过新增带颜色的重载支持透明遮罩，没有替换旧调用签名。

#### 本轮页面截图

- [第二批：1440px 目录页面](web-parity/compose-button-wide-phase2.png)
- [参考：1440px 官方页面](web-parity/shadcn-button-wide-reference.png)
- [第二批：搜索外部点击关闭](web-parity/compose-search-dismissed-phase2.png)
- [第二批：焦点恢复后 Enter 再打开](web-parity/compose-search-focus-restored-phase2.png)
- [第二批：全局搜索](web-parity/compose-search-phase2.png)
- [参考：官方搜索](web-parity/shadcn-search-reference.png)
- [第二批：Badge 页面](web-parity/compose-badge-phase2-light.png)
- [第二批：Usage 锚点](web-parity/compose-usage-anchor-phase2.png)
- [第二批：浏览器后退恢复](web-parity/compose-back-restored-phase2.png)
- [第二批：手机导航 Sheet](web-parity/compose-mobile-sheet-phase2.png)
- [第二批：手机搜索](web-parity/compose-mobile-search-phase2.png)
- [修改前：线上页面](web-parity/compose-button-before.png)
- [参考：官方页面](web-parity/shadcn-button-before.png)
- [修改后：本地浅色](web-parity/compose-button-after-light.png)
- [修改后：本地深色](web-parity/compose-button-after-dark.png)
- [修改后：390px 窄屏](web-parity/compose-button-after-mobile.png)

### 旧 Badge 基准排查（2026-10-28）

<!-- progress-created: 2026-10-28; order: 1 -->

旧 Vega Badge 的 4 张截图在显式 verify 下仍失败（字号/尺寸差异）。
临时恢复改动前的 Badge.kt、用相同周边代码及工具链渲染，再自动恢复当前源码，
四张 actual 图片与本轮图片 **SHA-256 全部相同**。
因此这四处是改动前实现已存在的基准差异，不是本轮 Nova Badge 引入的差异。
[机器可读比较证据](web-parity/badge-legacy-baseline-comparison.json)。
旧基准保持原样；后续应结合当前 Compose 字体继承规则单独审查，不能称整库截图全部通过。

### 第二批：导航、正文结构与 Badge（2026-10-28）

<!-- progress-created: 2026-10-28; order: 0 -->

- [x] 顶栏全局搜索入口，支持组件、指南、工具与扩展；筛选后隐藏空组。
- [x] 搜索自动聚焦、方向键/Enter 选择、Escape 关闭的定向回归。
- [x] 侧栏组件改为字母顺序；Getting Started、Utils、Extensions 分开；当前项超出视野时自动滚回。
- [x] 手机导航使用实际 Sheet，带关闭按钮、遮罩与键盘关闭；390px 浏览器已实测。
- [x] 1440px 宽屏章节目录；点击章节/示例滚动到实际布局位置，当前章节随滚动更新。
- [x] 页面 Installation 区块及 Button API 参数表；修正旧安装指南中的包名和版本。
- [x] 页面切换时在目录应用生命周期内保留每个组件的滚动位置，支持浏览器前后退复用。
- [x] 共用 TeleportPrimitive 补 Escape 处理，Dialog/Sheet 已有键盘回归。
- [x] Cmd/Ctrl+K 浏览器级入口已验证：未点击 Compose 控件也能打开搜索；事件监听随页面组合生命周期移除。
- [x] Nova Badge 的 20px 高度、12px Medium 字体、Ghost/Destructive、4px slot 间距及 12px 默认图标已通过功能/像素断言。
- [x] Spinner 跟随 Badge/Button 的内容色与默认行内图标尺寸；显式尺寸仍优先。

#### 第二批新增源码证据

- 上游固定版本 `96da9cc7f1aa10580a3741664e2a77c721206c00` 的
  [command-menu.tsx](https://github.com/shadcn-ui/ui/blob/96da9cc7f1aa10580a3741664e2a77c721206c00/apps/v4/components/command-menu.tsx)：
  站点搜索为自定义 Command 组合，512px 宽、15% 顶部定位、无可见遮罩、36px 结果行和 40px Footer。
- 通用 Overlay 新增可选颜色（默认值不变），搜索使用透明颜色维持外部点击关闭。
- Badge 的行内图标默认值由 Button、Badge 两个真实组件共享；显式 Icon variant/style 可覆盖，
  没有新增泛化的数据槽位系统。
