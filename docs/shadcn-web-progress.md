# Web 对齐进度

更新：2026-10-30。计划见 [shadcn-web-plan.md](shadcn-web-plan.md)。

前八批基础实现及第九批六个组件的 Web 剩余实现已完成；本批详细范围与验证见文末。
第九批相关回归、像素与 JVM/JS/Wasm 验证见文末；第八批及之前的证据保留。
整站逐组件、逐状态验收仍未完成。清单中的未勾选项需区分“尚未开始核对”和“基础已验证、完整矩阵待关闭”；历史失配另见各批基准对照记录。

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

## 第二批：导航、正文结构与 Badge（2026-10-28）

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

### 第二批新增源码证据

- 上游固定版本 `96da9cc7f1aa10580a3741664e2a77c721206c00` 的
  [command-menu.tsx](https://github.com/shadcn-ui/ui/blob/96da9cc7f1aa10580a3741664e2a77c721206c00/apps/v4/components/command-menu.tsx)：
  站点搜索为自定义 Command 组合，512px 宽、15% 顶部定位、无可见遮罩、36px 结果行和 40px Footer。
- 通用 Overlay 新增可选颜色（默认值不变），搜索使用透明颜色维持外部点击关闭。
- Badge 的行内图标默认值由 Button、Badge 两个真实组件共享；显式 Icon variant/style 可覆盖，
  没有新增泛化的数据槽位系统。

## 尚未关闭的页面差异

- 顶栏仅显示本项目真实存在的导航；官方其他产品页面没有伪造本地入口。
- 侧栏与官方仍有栏目内容、渐隐边界、选中项宽度和细微尺寸差异；当前项目扩展单独列出。
- 手机 Sheet 沿用组件默认 320px 宽，官方导航尺寸及关闭按钮位置需进一步视觉核对。
- API 参数表已补 Alert/Alert Dialog/Button/Badge/Card/Code Block/Trace/Input/Field/Label/Textarea/Checkbox/Radio Group/Switch/Toggle/Toggle Group/Slider/Input Group/Button Group/Input OTP/Select/Combobox；其余组件需逐项从 Kotlin 源码填写。
- 目录已支持可读 URL fragment、刷新定位、浏览器前进/后退和重复点击同一章节；桌面与手机已验证 Alert。标题自身的 permalink 入口已在 Alert 验证；其他组件未据此视为复核完成，复杂重排后的滚动锚定仍待逐项核对。
- Button 尚需补齐官方 Rounded/Button Group/As Link/RTL 等示例和 hover/focus/pressed 验收；
  含图标的非对称 padding 也需进一步检查。
- Secondary hover 对其他有色 palette 的 OKLCH color-mix 精确性需单独校验。
- Badge 尚需完整对照所有官方示例及交互状态，不能仅凭新增截图标为完成。
- Card 的 Nova 标题/描述、Action 右置、Footer 满宽底色/分隔线已修复；Image、完整 RTL 和表单组件尺寸待验收。
- 通用弹层的 Nova 遮罩颜色/透明度及模糊仍需对照官方样式；本轮搜索使用透明重载。
- Code Block / Trace 已有公开组件与目录页；官方扩展演示、部分复合交互及完整状态矩阵尚未关闭。

## 逐组件清单

`[ ]` 表示尚未完成全部适用状态的视觉及行为验收，不能据此判断是否已经做过基础对齐。
“基础已验证”表示已有本轮实现、定向测试和浏览器证据，后半句列出真实剩余范围；
“尚未开始本轮页面与完整状态对照”表示该组件页还未进入本轮逐页核对。历史对齐结果不自动继承。

- [ ] Accordion (`accordion`)：2026-10-30 已复查官方/本地桌面及手机、浅深色首屏；修正 FAQ 首屏、384dp 宽度、Nova 紧凑间距和末项边框。六组官方示例已补、hover/描边图标已修正、RTL 键盘回归通过；Borders/Card/语言选择器四象限已验证；保留内容 Tab/状态和动态末项分隔线回归通过；Basic/Multiple 真实内容及四象限已补验、200ms 动画反转回归通过；Disabled 四象限、受控拒绝及 forceMount 状态已验证；预览高度策略和扩展 API 全量矩阵待关闭，详见文末。
- [ ] Alert (`alert`)：九组示例四象限、Composition/四个 Kotlin API 小节、说明/源码导出及新目录跳转已验证；共享 focus-visible、说明弹层焦点、URL/标题锚点、代码框/内联背景已验证；操作区默认定位及多按钮调用者布局四象限已补验；全文法、超高操作/任意复杂槽位、窗口重排、辅助技术及细微排版仍待关闭。
- [ ] Alert Dialog (`alert-dialog`)：官方七组预览、Composition/Kotlin API 已补；Media 桌面两列/手机与 Small 居中、24dp 图标、三语两尺寸切换及 Settings 链接已验证；56 项定向测试与六张 Nova 像素基准通过，桌面/手机浅深矩阵已检查。共用深色 input/border 令牌已在共享主题复核中修正；正文换行/字重及阿拉伯语回退字体、无障碍映射与四张旧 Vega 基准失配仍待关闭。
- [x] Aspect Ratio (`aspect-ratio`)：Default/Square/Portrait/三语 RTL 组件复核完成；固定 caption 20dp 行框并修正 Nova Select 真实 Arabic 字体选项高度。三种宽度、浅深色、语言切换及展开代码保留状态已验证；53 项功能/布局测试、四张像素基准通过。色域/整数测量适配及共享页面剩余差异见文末第二轮记录，不代表全站 P1/P3 或 Select 已完成。
- [ ] Attachment (`attachment`)：六组示例、Nova布局、Group吸附/键盘/渐隐/约束、整卡焦点和onClick hover，以及Features/Accessibility指南、目录/导出已补。第七轮按官网当前SVG修正FileSearch；实际标题14px/500字重和约148px排版字宽已验证，未调粗字体。26项目录回归、JS/Wasm构建通过；旧Vega、字形像素观感、自定义overlay hover、颜色过渡与原生辅助技术仍待关闭。按用户要求暂停逐轮细化，剩余项合并到后续统一处理；第八轮 hover 动画只有定向测试通过，尚未完成浏览器验收。证据见attachment-r2至attachment-r8。
- [ ] Avatar (`avatar`)：2026-10-30 首轮主要差异已补：真实图片、九组官方配方与额外 Fallback、Composition/六个 Kotlin API 小节；修正 fallback 字号、外圈、图标徽标、RTL 徽标及绿色令牌，补齐菜单键盘导航。9 项测试、JS/Wasm distribution 和四象限浏览器检查通过。混合尺寸分组计数、图片混合边框、菜单完整焦点/像素矩阵等保留明确后续项，见文末及 avatar-r1。
- [ ] Badge (`badge`)：基础已验证（第二批）；Nova 尺寸/字号/颜色与浏览器检查已完成，完整官方示例和交互矩阵待关闭。
- [ ] Breadcrumb (`breadcrumb`)：2026-10-30 七组官方配方、Composition/七个 API 小节已补；修正 6dp 换行列表、4dp item 间距、14dp 镜像描边分隔符、20/16dp 省略号及链接 hover。9 项组件/目录定向测试、四象限七组浏览器检查、JS/Wasm distribution 通过；连同 Avatar 共 31 项联合验证和 13 张像素基准通过。共享手机宽度导致的换行差异、链接/导航原生语义和焦点细节仍明确保留，详见 breadcrumb-r1。
- [ ] Bubble (`bubble`)：尚未开始本轮页面与完整状态对照。
- [ ] Button (`button`)：基础已验证（第一、二批）；默认示例、Nova 样式和形状反馈已修正，非对称图标内边距及 Rounded/As Link/RTL 等完整示例待关闭。
- [ ] Button Group (`button-group`)：基础已验证（第六批）；连体接缝、嵌套图标按钮、默认菜单已修正，任意混合子项、纵向/尺寸矩阵及菜单完整图标待关闭。
- [ ] Calendar (`calendar`)：尚未开始本轮页面与完整状态对照。
- [ ] Card (`card`)：Nova 标题、描述、Action 与 Footer 已修复；Create 财务配方已补响应式摘要和真实铺满页脚，字体实际行高及 Image/RTL 等完整验收待完成。
- [ ] Carousel (`carousel`)：尚未开始本轮页面与完整状态对照。
- [ ] Code Block (`code-block`)：已补公开组合组件、编号/差异行/换行/复制与目录页；扩展示例及完整状态矩阵待验收。
- [ ] Trace (`trace`)：已补公开状态行、折叠详情、键盘/受控回归与目录页；官方复杂工具调用示例待补。
- [ ] Chart (`chart`)：Create 独立 Chart Color、六柱数据/几何和部分预览配色已修正并回归；Chart 本页及核心组件完整样式、示例、tooltip/键盘/辅助技术等状态对照仍未完成。
- [ ] Checkbox (`checkbox`)：Nova 4dp 圆角、input 底色/边框、错误 ring、标签点击和控件焦点已修正；复杂组合与完整状态矩阵待关闭。
- [ ] Chip (`chip`)：项目扩展，检查一致性。
- [x] Collapsible (`collapsible`)：第九批完成 Web 剩余项：外部收起回收内容焦点、keepMounted 状态保持/隐藏语义、动画中断反转。
- [ ] Combobox (`combobox`)：基础已验证（第七批）；内联编辑、筛选/键盘提交/清空和默认示例已完成，多选 chips、完整辅助技术映射及复杂状态矩阵待关闭。
- [x] Command (`command`)：第九批完成 Web 剩余项：长列表 Home/End 滚动跟随、不因初始高亮滚动页面、受控 value 拒绝未确认导航、IME 组合期间不提交。
- [x] Context Menu (`context-menu`)：第九批完成 Web 剩余项：触发区域窗口坐标/边缘翻转、RTL 子菜单键、字母定位、触摸长按取消、Escape 后焦点恢复及非模态 host。
- [x] Date Picker (`date-picker`)：第九批完成 Web 剩余项：本地时区、可配置 locale/周起始日/读屏日期、连续范围、多月换行、月界限、隐藏日期语义和真实键盘焦点；弹层显式初始焦点。
- [x] Dialog (`dialog`)：第九批完成 Web 剩余项：4dp 背景模糊、初始焦点/Tab 闭环/恢复触发器、非模态背景交互与外部关闭；须在窗口根接入 OverlayHost。
- [x] Drawer (`drawer`)：第九批完成 Web 剩余项：背景模糊和嵌套捕获、初始/恢复焦点、受控 snap 切换、真实顺序吸附、手势与外部关闭策略解耦；真机覆盖另列。
- [ ] Dropdown Menu (`dropdown-menu`)：尚未开始本轮页面与完整状态对照。
- [ ] Empty (`empty`)：尚未开始本轮页面与完整状态对照。
- [ ] Field (`field`)：Nova 间距/字号、448 响应式阈值、嵌套 disabled、支付表单示例已补；choice-card、嵌套组及完整 RTL 待核对。
- [ ] Hover Card (`hover-card`)：尚未开始本轮页面与完整状态对照。
- [ ] Input (`input`)：32 高、14/16 响应式字号、dark/disabled/invalid/focus 已修正；复杂组合与全状态矩阵待关闭。
- [ ] Input Group (`input-group`)：基础已验证（第六、七批）；32dp 布局、附件聚焦、禁用/错误和单行按钮附件已修正，button/kbd 负 margin、复杂多控件与完整焦点矩阵待关闭。
- [ ] Input OTP (`input-otp`)：基础已验证（第七批）；连体格、分组、编辑/粘贴/密码语义和默认示例已完成，光标闪烁、invalid API、完整 IME/自动填码矩阵待关闭。
- [ ] Item (`item`)：尚未开始本轮页面与完整状态对照。
- [ ] Kbd (`kbd`)：尚未开始本轮页面与完整状态对照。
- [ ] Label (`label`)：基础已验证（第四、五批）；14 Medium、disabled 透明度和可点击标签已修正，Checkbox 基础依赖已补齐，复杂 Field 组合与完整状态矩阵待关闭。
- [ ] Marker (`marker`)：尚未开始本轮页面与完整状态对照。
- [ ] Menubar (`menubar`)：尚未开始本轮页面与完整状态对照。
- [ ] Message (`message`)：尚未开始本轮页面与完整状态对照。
- [ ] Message Scroller (`message-scroller`)：尚未开始本轮页面与完整状态对照。
- [ ] Navigation Menu (`navigation-menu`)：尚未开始本轮页面与完整状态对照。
- [ ] Pagination (`pagination`)：尚未开始本轮页面与完整状态对照。
- [ ] Popover (`popover`)：尚未开始本轮页面与完整状态对照。
- [ ] Progress (`progress`)：尚未开始本轮页面与完整状态对照。
- [ ] Questionnaire (`questionnaire`)：尚未开始本轮页面与完整状态对照。
- [ ] Radio Group (`radio-group`)：Nova 间距/错误状态、方向键循环、跳过 disabled、RTL/Home/End 已补；Tab 单入口、动态重排和完整矩阵待关闭。
- [ ] Resizable (`resizable`)：尚未开始本轮页面与完整状态对照。
- [ ] Scroll Area (`scroll-area`)：尚未开始本轮页面与完整状态对照。
- [ ] Scroll Fade (`scroll-fade`)：尚未开始本轮页面与完整状态对照。
- [ ] Select (`select`)：基础已验证（第七批）；默认水果示例、选中行定位、键盘/焦点和错误状态已修正，已按官网固定弹层宽度补 Nova 标签不收缩/不换行及 6dp 间距，长标签与希伯来语回归通过；复杂碰撞/长列表滚动、键入搜索、动态重排与完整 RTL 待关闭。
- [ ] Separator (`separator`)：尚未开始本轮页面与完整状态对照。
- [ ] Sheet (`sheet`)：尚未开始本轮页面与完整状态对照。
- [ ] Shimmer (`shimmer`)：尚未开始本轮页面与完整状态对照。
- [ ] Sidebar (`sidebar`)：尚未开始本轮页面与完整状态对照。
- [ ] Skeleton (`skeleton`)：尚未开始本轮页面与完整状态对照。
- [ ] Slider (`slider`)：基础已验证（第六批）；Create Payout 静态 Progress 已换为真实 50..10000/step50 Slider，金额和键盘 Home/End 已回归；12dp Nova 滑块、填充/尺寸、键盘步进和 commit 已修正，多滑块标签、动态焦点与完整竖向/反转/RTL 手势矩阵待关闭。
- [ ] Spinner (`spinner`)：已补内容色与行内尺寸继承；Attachment 第二轮按官网 LoaderCircle SVG 修正弧线半径、笔画及 sweep，验证实际绘制尺寸和 Style 覆盖，并显式验证浅深像素基准。Spinner 页面完整示例与交互复核仍待关闭。
- [ ] Stepper (`stepper`)：项目扩展，检查一致性。
- [ ] Switch (`switch`)：Nova 轨道/滑块尺寸、暗色滑块、150ms 位移、错误 ring 和标签焦点已修正；复杂组合及完整状态矩阵待关闭。
- [ ] Table (`table`)：尚未开始本轮页面与完整状态对照。
- [ ] Tabs (`tabs`)：尚未开始本轮页面与完整状态对照。
- [ ] Text (`text`)：项目扩展，检查一致性。
- [ ] Textarea (`textarea`)：64 最小高、上下 8 内边距、顶部对齐、内容自增高已补；完整组合矩阵待关闭。
- [ ] Toast (`toast`)：尚未开始本轮页面与完整状态对照。
- [ ] Toggle (`toggle`)：Nova 字重/行内图标/间距/选中/小号圆角已修正，新增兼容 invalid 重载；非对称图标内边距、完整 hover/pressed 矩阵待关闭。
- [ ] Toggle Group (`toggle-group`)：基础已验证（第六批）；连体样式、单选取消和方向键焦点已修正，Tab 单入口、动态重排与完整纵向矩阵待关闭。
- [ ] Tooltip (`tooltip`)：尚未开始本轮页面与完整状态对照。

## 旧 Badge 基准排查

旧 Vega Badge 的 4 张截图在显式 verify 下仍失败（字号/尺寸差异）。
临时恢复改动前的 Badge.kt、用相同周边代码及工具链渲染，再自动恢复当前源码，
四张 actual 图片与本轮图片 **SHA-256 全部相同**。
因此这四处是改动前实现已存在的基准差异，不是本轮 Nova Badge 引入的差异。
[机器可读比较证据](web-parity/badge-legacy-baseline-comparison.json)。
旧基准保持原样；后续应结合当前 Compose 字体继承规则单独审查，不能称整库截图全部通过。

## 第二批最终验证（2026-10-28）

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

## 本轮页面截图

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

## 第一批定向验证（2026-10-28）

- 14 项测试通过：CatalogPreview 2、TextColorInheritance 1、ButtonNovaParity 6、RippleShape 5。
- 显式启用 `-Proborazzi.test.verify=true`：Nova 2 张与 RippleShape 5 张全部通过。
- JVM / JS / Wasm 编译通过；最终 Wasm development distribution 构建通过。
- 新增 Nova 基准已检查；原有 RippleShape 基准没有重录。
- 当前已有 AGP 实验功能、Text 内部 API suppression、Webpack 和 Gradle 兼容性警告；
  本轮没有扩大到构建工具升级。
- 尚未执行全库全部 screenshot suite；其他依赖 Nova tokens/字体的旧截图需要逐项检查，
  当前定向通过不能替代整库验收。

## 验证记录

- 2026-10-28：首轮 JVM/Wasm 编译发现 Shadow 不支持 copy；已定位为 API 使用问题，修正后重试。
- 尚未执行线上发布、Git 提交或推送。

## 第三批实现（2026-10-29）

- Card：Nova 标题 16/22 Medium、小尺寸 14/19.25；描述 14；Header 间距 4；Action 置于标题右侧；Footer 铺满卡片底部，带 muted/50 底色、分隔线和 16/12 内边距。原有公共签名保留，Vega 路径保留。
- Card 默认示例换为官方登录内容，忘记密码放回 Password 标签同行；补公开参数表。未把 Input/Field 本身尚存的字号和间距差异算作 Card 完成。
- Code Block：新增公开 Root/Header/Title/Actions/CopyButton/Content/Line/Footer；两种尺寸、destructive、编号起始值、空行保留、差异/高亮、横向滚动或换行、选择、焦点 ring、复制原文及两秒成功反馈；复制被拒绝时不显示成功。富文本行显式指定 lineNumber。
- Code Block 默认展示同一段 13 行 route.ts 代码，并可切换换行。下载、Height/Show More/Max Height 和 Tabs/Trace/Message 组合仍待补齐。
- Trace：新增 Line/Icon/Title/Description 与 Collapsible/Trigger/Content；running shimmer、error、pending，200ms 展开及箭头旋转，复用已有受控/非受控 Collapsible；补 API 表。完整官方工具调用示例仍待补。
- 展示应用新增同版本 Geist Mono 资源，修复 Wasm 的平台 Monospace 回退；公共 CodeBlockContent 保留 textStyle 供应用提供字体。默认底色使用主题 background，避免原生 dropShadow 的内侧透色；非默认底色容器可通过 style 覆盖。
- 元数据生成器改为读取当前 RouteKey 注册格式和组件子目录，恢复真实 variants；每张预览记录 Vega/Nova，避免把 Nova 截图标作 Vega。只生成本轮相关的新增预览，不刷新旧 Badge 基准。

## 第三批自动验证（2026-10-29）

- 59 项定向回归通过：core 41、app 18。新增三套组件测试共 19 项（Card 5、Code Block 8、Trace 6）。
- 新增 8 张快照显式像素 verify 通过：Card 2、Code Block 4（含浅深色焦点）、Trace 2；此前的 11 张相关快照也通过。
- 覆盖 Footer 边界与 Action 位置、嵌套小 Card 字号隔离、水平滚动容器的无界测量；编号/空行、窄宽换行、复制原始空白、两秒反馈与权限拒绝；Trace 受控/非受控、Enter 展开及 disabled。
- 已修复实际失败：0.dp 等同 Dp.Hairline 导致黑细边；Infinity 减法破坏 Constraints 哨兵；Lucide 图标名应为 TextWrap。未修改测试断言规避问题。
- JVM/JS/Wasm 与 Web distribution 已通过；追加 Geist Mono 后又运行 app 18 项回归、JS 编译及 Wasm distribution，全部通过。
- 保留旧 Badge 4 张失配基准。没有宣称整库所有截图通过；没有发布、提交或推送。

## 第三批浏览器检查（2026-10-29）

- 本地当前工作树：Card / Code Block / Trace，1440×1000、390×844，浅深主题截图；另存 1095×687 浅色检查。
- 实测 Code Block 切换自动换行，长行在手机宽度内折行；Trace 点击展开、详情导线和箭头；运行中切换浅深主题。
- 确认 Geist Mono 修复在 Wasm 中生效，代码区和下方 Kotlin 代码不再使用普通字体回退。
- 官方同尺寸浅色参考与本地截图均存于 `docs/web-parity/*phase3*`。此处是基础布局/交互通过，不代表所有适用状态矩阵已关闭。
- 剩余差异：官方默认 Trace 含完整工具调用/来源示例，本地仍为简化演示；Code Block 尚少下载、受限高度与复合演示；Card 表单依赖 Input/Field/Label 的剩余尺寸核对。浏览器页签仍显示统一 Components 标题，逐路由标题待补。

本批截图：

- [Card：桌面浅色](web-parity/compose-card-phase3-light.png)、[深色](web-parity/compose-card-phase3-dark.png)、[手机浅色](web-parity/compose-card-phase3-mobile-light.png)、[手机深色](web-parity/compose-card-phase3-mobile-dark.png)、[官方参考](web-parity/shadcn-card-phase3-reference.png)。
- [Code Block：桌面浅色](web-parity/compose-code-block-phase3-light.png)、[换行](web-parity/compose-code-block-phase3-wrap.png)、[深色](web-parity/compose-code-block-phase3-dark.png)、[手机浅色](web-parity/compose-code-block-phase3-mobile-light.png)、[手机深色](web-parity/compose-code-block-phase3-mobile-dark.png)、[官方参考](web-parity/shadcn-code-block-phase3-reference.png)。
- [Trace：桌面浅色](web-parity/compose-trace-phase3-light.png)、[展开详情](web-parity/compose-trace-phase3-expanded.png)、[深色](web-parity/compose-trace-phase3-dark.png)、[手机浅色](web-parity/compose-trace-phase3-mobile-light.png)、[手机深色](web-parity/compose-trace-phase3-mobile-dark.png)、[官方参考](web-parity/shadcn-trace-phase3-reference.png)。

## 第四批实现（2026-10-29）

- Input：Nova 的 input 边框、dark input/30 底色、disabled input/50（dark /80）底色、invalid 红色边框及 3dp ring（浅 /20、深 /40）。原生 dropShadow 内侧由主题底色覆盖，避免焦点状态把整个输入区染灰/染红。
- 输入文本与 placeholder 使用同一份明确字体/字号；桌面 14/20，窗口小于 768dp 时 16/24，仍保持 32 高。官方手机页面 computed style 已确认 16px/32px。
- Textarea：Nova 上下 8、左右 10；占位与内容顶部对齐，最小 64，随多行输入增长。旧预设保留原有垂直对齐。
- Label：Nova 14 Medium、leading-none、letterSpacing 0；disabled 使用整体 /50。FieldLabel 单独使用 leading-snug 19.25，描述和错误为 14/20。
- FieldSet / FieldGroup / Field / FieldContent 的 Nova gap 分别为 16 / 20 / 8 / 2；Responsive 的 container md 阈值为 448，区别于页面媒体断点 768。嵌套 FieldSet 不再解除父级 disabled。
- Input/Label/Textarea 默认展示换为官方 API Key、可切换的 Accept terms、纯 Textarea；Field 新增 Payment Method/Billing Address/Comments 表单及月年选择、Cancel 重置。补四页 Kotlin API 参数。
- 修复目录中 Autofocus 演示在页面加载时抢焦点并自动滚向底部的问题。演示改为点击 Try autofocus 后挂载，库组件的 autoFocus 行为不变；新增两项目录回归。

### 第四批剩余范围

- Field 的 choice-card / nested group gap-4、相邻 Legend/Description 的负 margin、横向内容顶对齐、Separator 和完整 RTL/错误组示例仍待逐项核对；本批不勾选整组件完成。
- 默认 Label 中 Checkbox 仍依赖下一批 Checkbox 对齐；Select 的完整 Nova 状态亦未在本批关闭。
- 旧表单截图显式 verify 有 25 张失配。用改动前五个表单源码复现，并保留本轮其余环境，最终 25 张实际渲染 SHA-256 全部与改动前一致；未重录旧基准。
  证据：[forms-legacy-baseline-comparison.json](web-parity/forms-legacy-baseline-comparison.json)，以 finalIdentical 字段为最终比较。
- 初次比较中 6 张曾因多行顶部对齐影响 Vega；已将该行为限制为 Nova，复查后全部恢复改动前渲染。没有将原有失配称为全部通过。

### 第四批验证与截图

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

## 第五批实现（2026-10-29）

- Checkbox：Nova 4dp 圆角、16dp 本体、14dp 勾选图形；input 边框及 dark input/30 底色；错误 ring 为 3dp、浅 /20 深 /40，选中错误状态保留 primary 边框。勾选图形使用 Lucide Check 的同一条路径和等比线宽。
- Radio：Nova 纵向 gap 8、input 边框/底色、选中 primary、8dp 圆点和错误状态；增加方向键切换、循环、跳过禁用项、Home/End 与 RTL。通过实际官网确认默认竖排也响应右方向键。
- Switch：Nova 默认 32×18.4、Sm 24×14；本体扣除 border 后，未选中滑块从 0 开始、选中移动到 14/10；深色未选中滑块为 foreground，选中为 primaryForeground；位移动画 150ms。
- Toggle：Nova 14 Medium（Sm 12.8）、图标 16/14、Row gap 4，选中背景 muted，Outline 边框 input，小号圆角 min(md,12)。新增 required invalid 参数的组合内容重载，原公开签名保留；测试原 Toggle(true) 仍表示 pressed，避免重载改变原位置参数语义。
- Checkbox/Switch 文本重载共用正确的本地/受控状态；点击标签切换并把焦点转给真实控件，标签不再额外占据 Tab 焦点。Radio 标签同样请求真实 radio 焦点。描述版使用 FieldLabel 与 14/20 描述。
- 四页默认演示与官方一致：Accept terms、Comfortable 选中的三项密度、Airplane Mode、Small Outline Bookmark（选中后图标填充），并补 Kotlin API 参数表。
- Nova 默认启用焦点 ring；Ring 的旧默认值及其他预设保持原状。显式 ring.enabled=false 仍可关闭，已覆盖运行中切换。

### 第五批已知残差

- Radio Group 尚需补 Tab 单入口、动态重排及完整焦点矩阵；本批键盘能力不等于完整 Base UI 行为验收。
- Toggle 的 inline-start/end 非对称内边距仍待与 Button 一并实现；完整 hover/pressed、RTL 以及选择控件的 choice-card/group/table 组合未关闭。
- 原生 dropShadow 的内侧需要主题底色覆盖；特殊有色容器的透明底色组合仍需单独检查。
- 旧 Checkbox/Radio/Switch 的 14 项快照通过。旧 Toggle/ToggleGroup 的 8 张基准失配，用改动前实现复现后实际渲染 SHA-256 全部一致；保留原基准。
  [机器证据](web-parity/toggle-legacy-baseline-comparison.json)。为了编译新增回归，基线仅额外保留未参与旧测试的 invalid API 转发桥；测试的旧实现与样式均来自改动前源码。

### 第五批验证与截图

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


## 第六批实现（2026-10-29）

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

### 第六批已知残差

- Toggle Group 尚未完成 Tab 单入口、按可视顺序动态重排、完整纵向 stretch/焦点矩阵。Arrow/Home/End 能力不表示全部 Base UI 复合焦点规则均已关闭。
- Input Group 的 button/kbd 负 margin、全部组合下的禁用-opacity 与 focus-visible 输入来源区分、复杂多控件布局，仍待单独验收。特殊有色容器仍有前批记录的透明底色组合限制。
- Button Group 混合任意原始 Button/Input/Select 还需显式 item/shape 组合；默认菜单条目图标与完整 Label As 内容未完全复刻。纵向混合布局及全部尺寸矩阵仍待核对。
- Slider 的多滑块完整无障碍标签与焦点顺序、动态增删时的焦点保留、垂直/反转/RTL 的完整手势矩阵仍待补齐。本批没有声称所有状态像素一致。
- 旧 ButtonGroup 6、InputGroup 12、Slider 4 共 22 张快照失配，用原组件源码复现后实际渲染 SHA-256 全部一致，未重录旧基准。
  [机器证据](web-parity/groups-legacy-baseline-comparison.json)。对照中保留此前主题/Text/Input 修复；InputGroupLayoutState 仅作编译桥，原根组件不提供它。
- 本地正确入口使用一级路径（如 /toggle-group），不是 /components/toggle-group；错误的多级路径会让相对脚本地址返回 404。此前启动回复中的链接已在本批更正。

### 第六批验证与截图

- 最终 77 项相关检查通过：GroupsNovaParity 23、FormNovaParity 14、SelectionNovaParity 19、目录应用 21。
- 新增 10 张 Nova 快照显式 verify 通过（状态矩阵浅深色 2、Slider/Input Group/Toggle Group/Button Group 焦点浅深色 8）；同时重验此前表单与选择控件快照。未改动任何旧跟踪基准。
- JVM / JS / Wasm 编译及 Wasm Web distribution 通过。既有 AGP 实验、Text 内部 API、Gradle 兼容性及 Webpack 警告保留。
- 浏览器最终版本保存桌面 1440×1000 与手机 390×844 浅深主题、1095×687 浅色。实测主题切换、搜索附件聚焦输入、Slider ArrowRight、Toggle Group 焦点移动/Space，以及组合按钮菜单和 Escape。
- [Toggle Group：默认](web-parity/compose-toggle-group-phase6-light.png)、[键盘](web-parity/compose-toggle-group-phase6-keyboard.png)、[手机深色](web-parity/compose-toggle-group-phase6-mobile-dark.png)、[官方](web-parity/shadcn-toggle-group-phase6-reference.png)。
- [Slider：默认](web-parity/compose-slider-phase6-light.png)、[键盘](web-parity/compose-slider-phase6-keyboard.png)、[手机深色](web-parity/compose-slider-phase6-mobile-dark.png)、[官方](web-parity/shadcn-slider-phase6-reference.png)。
- [Input Group：默认](web-parity/compose-input-group-phase6-light.png)、[附件聚焦后编辑](web-parity/compose-input-group-phase6-edited.png)、[手机深色](web-parity/compose-input-group-phase6-mobile-dark.png)、[官方](web-parity/shadcn-input-group-phase6-reference.png)。
- [Button Group：默认](web-parity/compose-button-group-phase6-light.png)、[菜单](web-parity/compose-button-group-phase6-menu.png)、[手机深色](web-parity/compose-button-group-phase6-mobile-dark.png)、[官方](web-parity/shadcn-button-group-phase6-reference.png)。
- 共 28 张浏览器截图，尺寸与底色检查见 [phase6-evidence.json](web-parity/phase6-evidence.json)。10 张新快照已生成文档预览，并在四项组件元数据中标明 Nova。未提交、推送或发布。


## 第七批实现（2026-10-29）

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

### 第七批已知残差

- OTP 的假光标目前静态，尚未加入 blink；invalid API/分组错误 ring、完整 IME/自动填码、复杂选择/粘贴与手机键盘矩阵尚未关闭。Native 字体栅格化及接缝的半像素居中差异仍需更细的比较。
- Select 的复杂碰撞、长列表选中项初始滚动、键入搜索、动态重排和完整 RTL/辅助技术矩阵仍待核对。当前对齐受窗口边缘约束，不能把默认示例通过称为所有定位行为完成。
- Combobox 的多选 chip/换行、完整键盘焦点和无障碍 active-descendant 映射、任意类型/null 选项、组合内容的所有禁用项及主题变化矩阵仍待补齐。旧按钮配方不等同于本批新增的内联编辑入口。
- 浮层仍沿用项目的原生阴影与不透明背景；官网 translucent/backdrop 效果、叠层阴影和环形边线尚有差异，不将其标为像素完全一致。
- 本批开始先运行旧截图作为基线，结束后比较实际 PNG：Select 7 + Combobox 10 共 17 张既有失配的 SHA-256 全部一致，未重录旧基准；Input OTP 的旧 7 项显式验证通过。
  [机器证据](web-parity/choice-legacy-baseline-comparison.json)。

### 第七批验证与截图

- 最终 92 项相关检查通过：ChoiceNovaParity 27、GroupsNovaParity 23、FormNovaParity 14、旧 InputOTP 7、目录应用 21。
- 12 张新 Nova 快照显式 verify 通过：矩阵浅深色 2，OTP/Select/Combobox 焦点浅深色 6，Select/Combobox 展开浅深色 4。新快照生成文档预览并按 Nova 记录元数据。
- JVM / JS / Wasm 编译与 Wasm Web distribution 通过；保留现有 AGP、Text 内部 API、Gradle 和 Webpack 警告。
- 浏览器：桌面、手机浅深色及中等宽度；OTP 点选替换、Combobox 筛选/键盘提交、Select 行对齐与 Enter 选择后保持关闭。最终截图和尺寸清单见 [phase7-evidence.json](web-parity/phase7-evidence.json)。
- [Input OTP：默认](web-parity/compose-input-otp-phase7-light.png)、[编辑](web-parity/compose-input-otp-phase7-edited.png)、[手机深色](web-parity/compose-input-otp-phase7-mobile-dark.png)、[官方](web-parity/shadcn-input-otp-phase7-reference.png)。
- [Select：默认](web-parity/compose-select-phase7-light.png)、[展开](web-parity/compose-select-phase7-open.png)、[选中](web-parity/compose-select-phase7-selected.png)、[手机深色](web-parity/compose-select-phase7-mobile-dark.png)、[官方展开](web-parity/shadcn-select-phase7-open-reference.png)。
- [Combobox：默认](web-parity/compose-combobox-phase7-light.png)、[筛选](web-parity/compose-combobox-phase7-filtered.png)、[选中](web-parity/compose-combobox-phase7-selected.png)、[手机深色](web-parity/compose-combobox-phase7-mobile-dark.png)、[官方](web-parity/shadcn-combobox-phase7-reference.png)。
- 共 24 张浏览器截图，尺寸与主题底色已核对；代码及测试证据保存在 phase7-evidence.json。未提交、推送或发布。本地服务继续保留在 127.0.0.1:8765。

## 第八批：六个展开与弹层组件（2026-10-29）

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

## 第九批：关闭上述六个组件的 Web 剩余差异（2026-10-29）

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


### 第九批后续修正：bottom Drawer 高度（2026-10-29）

用户指出 bottom 高度仍不对。复核官方纵向 Drawer 的 content-sized / `calc(100dvh - 6rem)` 规则后，发现默认配送示例额外把手机列表限制为 `360.dp`，导致有足够空间时仍提前滚动，抽屉被压到约 520px。移除此限制；列表保留 `weight(fill = false)`，自然高度随内容增长，只有超出视口减 96dp 时才收缩并滚动，固定 header/footer 不参与滚动。可复制代码同步修改，未改动 snap point 的显式高度。

- 浏览器：390×844 下抽屉约 566px，五项和按钮完整可见；390×600 下约 504px（顶部保留 96px），滚动后最后一项与按钮均可见。
- 新增 `DrawerSizingTest` 两项：短内容增高时面板随之增长；长内容达到视口上限且滚动区不覆盖 footer。2/2 通过，目录应用 23 项通过，JS/Wasm 构建通过。
- 截图：`web-parity/drawer-bottom-natural-height-390x844.png`、`drawer-bottom-viewport-cap-390x600.png`、`drawer-bottom-viewport-cap-scrolled-390x600.png`；索引见 `phase9-evidence.json`。本次截图取代前面配送示例的手机高度证据。


### 第九批后续修正：Drawer snap points（2026-10-29）

用户指出 snap 示例仍不对。重新读取固定上游版本 `apps/v4/examples/base/drawer-snap-points.tsx` 后，发现目录示例还停留在 API 文档中的 `[148px, 1]` 片段，实际官方示例为 `[31rem, 1]`。本地已改为 `[496.dp, Fraction(1f)]`，补上示例占位内容和 Close 操作，可复制代码同步更新。

底层 `DrawerSnapPanel` 此前按最大 snap 点直接确定面板高度，绕过纵向 96dp 最大高度留白。Nova 现以视口减 96dp 确定 snap 面板高度，绝对/比例档位仍按原始视口换算并通过平移露出，超出面板的档位钳到最大展开位置；旧预设的尺寸路径保留。

- 新增三项几何回归：148dp 精确露出、0.5 比例按原始视口、1.0 档保留 96dp。连同两项普通 bottom 高度测试，`DrawerSizingTest` 5/5 通过。
- 定向验证合计 **69 项通过**（Sizing 5、Completion 29、Drawer 数学 12、目录应用 23），显式像素 verify，JS/Wasm 构建通过，没有重录基准。
- 浏览器实际鼠标拖动：1440×1000 初始顶边约 504px、展开约 96px；展开后回落 496dp 档，再向下拖动关闭；关闭重开后改变为 390×844，初始顶边约 348px，向上拖动展开仍保留 96px。截图记录于 `web-parity/drawer-snap-corrected-*`，索引见 `phase9-evidence.json`。浏览器鼠标验证不替代原生设备触摸矩阵。


### 第九批后续修正：侧 Drawer 与窗口间距（2026-10-29）

用户指出官网左侧 Drawer 是浮动面板。实测当前官网 1440×1000 的 Position 示例，左侧面板为 x=8、y=8、width=384、height=984，四角 24px。此前本地侧面板仍使用固定旧 Nova 样式的贴边规则；按用户指定的当前官网预览，Nova 左右侧面板现保留左/右、上、下各 8dp 间距并使用完整 24dp 圆角。面板自身宽度不含外边距，桌面 384dp，窄屏为原窗口宽度的 75%。

定位由弹层对齐完成，未在面板外增加拦截点击的透明 padding。浏览器已确认点击左侧 x=3 的间隙可关闭；桌面左右和手机左侧截图已保存。纵向 bottom/snap 几何与旧预设规则未改动。

新增左右各一项尺寸回归，`DrawerSizingTest` 共 7 项；最终定向验证范围为 Sizing 7、Completion 29、Surfaces Nova 23、Drawer 数学 12、目录应用 23（合计 94）。仅两张嵌套背景基准因父侧面板位置与圆角变化而更新，人工检查浅深 diff 后单独记录，其他基准未重录。结果与证据见 `web-parity/phase9-evidence.json` 的 `floating_side_followup`。


### 第九批后续修正：大内容 bottom 示例（2026-10-29）

按用户要求把 bottom 示例换成更大的内容。参考当前官网 Custom Sizes 中 `flex-1 overflow-y-auto p-4` 的结构：固定 DrawerHeader、可收缩滚动主体、固定 DrawerFooter。`Default swipe direction (down)` 与 `Scrollable content` 保留原入口，共用新的 `DrawerLargeContentExample`，包含 24 条活动记录及 Done 按钮；复制代码同步展示完整 recipe。旧的 160dp 滚动区限制移除，抽屉高度由真实内容和视口减 96dp 的上限决定。

目录应用 23 项测试通过，JS/Wasm 构建通过。本次只修改目录示例，没有重录核心像素基准。桌面 1440×1000 和手机 390×844 中均已验证顶部留白、列表滚到 Day 24、固定标题和底部按钮；截图索引见 `phase9-evidence.json` 的 `large_content_followup`。


### 第九批后续修正：Nested Drawer 开关动画（2026-10-29）

用户指出嵌套动画不对。此前父层只在 `nestedOpenCount > 0` 时立即套用静态 scale/translate，关闭时直接移除变换；中心缩放也未补偿缩放后的露边位置。现保留同一 GraphicsLayer，以连续 depth 动画计算缩放及位移，按抽屉贴靠边设置 TransformOrigin，补偿尺寸收缩并维持每层 16dp peek。Drawer 开关及父层变换采用 450ms、`cubic-bezier(0.22, 1, 0.36, 1)`；被覆盖的内容在 300ms 内淡出，关闭后淡入；Sheet 的独立动画未修改。

多层计数传播到所有祖先。对于同方向纵向嵌套，父层高度随最前面的抽屉动画变化，关闭后恢复已记录的自然高度；临时堆叠高度不回写为自然高度，避免高度反馈。异方向嵌套不强行套用前层高度。

目录 Nested drawers 改为四层同方向示例，桌面 right、手机 down，包含完整主体占位、下一层和 Close 操作；可复制代码同步。浏览器已核对桌面与手机二/四层打开，以及逐层关闭回到首层后的尺寸恢复。

新增 `DrawerMotionTest` 2 项（开关中间态与中途反向；前层高度同步与自然高度恢复），Drawer 数学/状态测试新增祖先计数。最终 **97 项通过**：Motion 2、Sizing 7、Drawer 13、Completion 29、Surfaces Nova 23、目录应用 23；显式像素 verify，JS/Wasm 构建通过。人工核对差异后，仅更新两张嵌套背景浅深基准及对应预览。截图索引见 `phase9-evidence.json` 的 `nested_motion_followup`。本项证据覆盖开关/反向过渡，不宣称完成原生手势真机矩阵。


### 预览显示旧 snap 代码的核对（2026-10-29）

用户贴出了旧的 148dp/仅 Header 片段。源码搜索与新打开的 8765 页面均没有该片段；实际展开 Snap points 的 View Code 后，已确认显示 496dp、320dp 主体及 Close footer，证据为 `web-parity/drawer-snap-full-code-current.png`。本地临时预览服务增加 `Cache-Control: no-store, max-age=0`，并补齐 SPA HEAD 路由。原 Chrome 标签页交互超时，尚未确认该页旧内容来自未刷新的实例还是另一预览地址，未将缓存作为已证实的根因。已提供带版本参数的当前预览入口并请求原页面地址。本次不修改组件源码或重录像素基准。


### 第九批后续修正：bottom / top 同样浮动留白（2026-10-29）

用户指出 bottom 左右也有间距。实测官网手机 bottom Drawer 的 margin 为 8px、四角 24px；此前仅对左右侧面板应用该规则。Nova 四个方向现统一使用 8dp 外边距和 24dp 完整圆角：纵向面板宽度为原视口宽度减 16dp，bottom 底部留 8dp，top 顶部留 8dp；普通和 snap 面板均覆盖。左右侧面板的 384dp / 75% 宽度不变，旧预设规则不变。

纵向高度上限仍是视口高度减 96dp，snap 的绝对/比例露出高度仍按原视口解析。需区分“高度上限扣除 96dp”与“顶部实际留白”：bottom 最大展开档因底部另有 8dp 留白，顶边在 88dp；例如 844dp 高视口，面板高 748dp、y=88、底边 836。先前截图中的贴边纵向布局和最大档 y=96 由本次证据取代。

新增 bottom/top 外边距两项回归，Sizing 共 9 项。手机普通 bottom 已验证左右/底部留白及侧边间隙关闭，snap 从 496dp 档拖到最大档并在手机尺寸保留留白。当前用户 IAB 预览已实际 reload。六张 Drawer 像素基准经人工 diff 核对后单独更新（Nova drawer、backdrop、nested backdrop 各浅深两张），相应预览同步。最终验证结果见 `phase9-evidence.json` 的 `vertical_floating_followup`。

本次最终 **99 项通过**（Sizing 9、Motion 2、Drawer 13、Completion 29、Surfaces Nova 23、目录应用 23），显式像素 verify；JS/Wasm 构建通过。


### 第九批后续修正：嵌套 Drawer 连续点空白关闭（2026-10-29）

用户指出上一层退场未结束时，继续点空白不能关闭下一层，表现为一层一层卡顿。浏览器先复现四层、约 80ms 间隔点背景四次，最终仍停在 Third Drawer：关闭中的原生 Popup 继续覆盖整窗，但 scrim 的关闭事件已禁用。

DrawerPortal 现提供仅限自身的退出关闭链：退场遮罩收到后续点击时，转给最近仍打开的 Drawer；每层读取最新 open、外部关闭开关及两个 prevention 回调。每次点击只请求关闭一层，父层禁止外部关闭或 preventDefault 时立即停止，不跳过该层。该能力在 Teleport 内容边界重置，普通 Dialog 不继承 Drawer 的退场转交。另修正入场中途关闭的生命周期：必须 current=false、target=false 且 isIdle 才卸载，避免反向退场被直接剪掉。

新增 `DrawerDismissTest` 5 项：冻结动画时连续点背景逐层关闭；父层 pointer prevention；父层 disablePointerDismissal；普通 Dialog 隔离；入场未完成时关闭仍保持退场内容。最终 **104 项通过**（Dismiss 5、Motion 2、Sizing 9、Drawer 13、Completion 29、Surfaces Nova 23、目录应用 23），显式像素 verify，JS/Wasm 构建通过，没有重录基准。

浏览器桌面四次快速点击后全部关闭；手机稳定视口内一次触摸从 Fourth 回到 Third，重开第四层后以约 80ms 间隔触摸背景四次可全部关闭。手机证据为 Chromium CDP 触摸，不替代原生设备实测。截图索引见 `phase9-evidence.json` 的 `rapid_outside_dismiss_followup`。


### Web favicon：shadcn 标记与 Kotlin 紫色（2026-10-29）

按用户要求，新增 `app/webApp/src/webMain/resources/favicon.svg` / `favicon.ico` 并在 `index.html` 中声明。采用用户提供的 shadcn-components favicon 原始图形，保留黑色圆角背景，内部三条线统一改为 Kotlin 紫色 `#7F52FF`；优先使用 SVG，ICO 含 16/32/48/64/256 五种尺寸。图标 URL 加版本参数，绝对路径支持 `/drawer` 等组件路由。

Web distribution 构建通过；实际页面 favicon 链接及 SVG/ICO 请求均已验证。图形源为用户提供的 `/Users/cscs/WebstormProjects/web-foundation/shadcn-components/apps/components/public/favicon.svg`，紫色来源 `https://kotlinlang.org/assets/images/favicon.svg`；预览 `web-parity/favicon-kotlin-purple.png`。仅涉及 Web 图标与入口资源，未修改组件行为或重录像素基准。


### Web favicon：Display P3 紫色（2026-10-29）

按用户最终选择使用 P3，不启用 HDR。SVG 内部三条线通过 `@supports` 使用 `color(display-p3 0.498 0.322 1)`，保留 `#7F52FF` presentation attribute 作为不支持时的回退；黑色背景不变。ICO 继续使用 SDR 兼容版本，现有 PNG 预览也仅展示 SDR。URL 版本更新为 `kotlin-purple-p3`。Web distribution 构建通过，浏览器实测三条线 computed stroke 均为 P3，背景为 `rgb(0, 0, 0)`；该验证不等于测量标签栏的实际色彩输出。


### Web favicon：恢复 sRGB 并提亮紫色（2026-10-29）

按用户最终要求移除 P3 样式，使用更浅亮的普通 sRGB 紫色 `#A78BFA`，保留黑色背景与三条线原形。按 sRGB 相对亮度计算，与纯黑的对比度由 `#7F52FF` 的 4.55:1 提高至 7.72:1。SVG、五种尺寸 ICO 和 SDR 预览同步更新，URL 版本改为 `light-purple`。浏览器渲染核对三条线均为 `rgb(167, 139, 250)`。


### Web favicon：加强圆角可见度（2026-10-29）

原始背景半径为 16/256，在 16px 标签栏图标上约为 1px，用户反馈看起来像直角。背景及裁剪统一使用 `rx=48`，小图标下约为 3px；浅紫色 `#A78BFA` 不变。SVG、ICO 和预览同步更新，验证四角透明、边缘中点不透明，缓存版本更新为 `light-purple-rounded`。


### Web favicon：Kotlin 红底（2026-10-29）

按用户反馈改为 Kotlin 官方 favicon 渐变中的红色 `#EF4857` 作为纯色背景，内部三条线改为白色，保留 `rx=48` 圆角。SVG/ICO 同步，预览为 `web-parity/favicon-kotlin-red.png`，URL 版本更新为 `kotlin-red`。


### Web favicon：Kotlin 官方渐变背景（2026-10-29）

按用户要求，背景采用 Kotlin 官方 favicon 的径向渐变：`#EF4857` → `#D211EC` → `#7F52FF`，原始渐变中心与半径按 64→256 等比例放大。白色三条线与 `rx=48` 圆角保持不变。SVG、五种尺寸 ICO 同步，预览为 `web-parity/favicon-kotlin-gradient.png`，URL 版本更新为 `kotlin-gradient`。


### Web favicon：仅保留 SVG（2026-10-30）

移除 ICO 文件及 HTML 引用，只使用 Kotlin 渐变背景、白色图形的 SVG favicon，避免维护两份资源。


### Drawer：可选整页 Indent effect（2026-10-30）

参考 Base UI Drawer 的根层 indent 模式（https://base-ui.com/react/components/drawer），新增 `DrawerIndentHost`。应用按 `OverlayHost { DrawerIndentHost { AppContent() } }` 放置，Drawer 使用 `indentBackground = true` 选择启用，默认 false。背景使用黑色，页面等比缩放，使较短轴两侧各留 12dp，圆角过渡至 24dp；复用现有 450ms easing。Portal 保持在变换容器外，Drawer 本身不跟随缩小。该实现是 Compose 可选效果，不宣称与官网 CSS 参数逐像素一致。

每个启用的 Drawer 以独立 key 注册，最后一个关闭或移除时恢复，避免多个 Drawer 相互提前还原。初版驱动为开关状态动画；后续已补齐手势进度跟随，见下节。目录根层已接入，Drawer 页面增加 “Indent effect” 示例和使用代码。

验证：`DrawerIndentTest` 3 项、`DrawerMotionTest` 2 项、`DrawerDismissTest` 5 项，共 10 项通过；Web Wasm distribution 构建通过。保留既有 Text.kt 编译警告及 webpack 警告，无新增编译错误。浏览器桌面确认整页背景内缩、面板保持原尺寸，Escape 关闭后恢复，证据为 `web-parity/drawer-indent-open.png`、`web-parity/drawer-indent-restored.png`。未重录像素基准。


### Drawer：Indent 跟随拖动与回弹（2026-10-30）

补齐上一节的手势缺口：普通面板以 anchored offset/Closed anchor 归一化，支持四个方向；snap 面板在最低可见档以上维持内缩，仅在最低档至关闭之间逐步还原。每个面板通过只读进度函数共享手势状态，Host 用 snapshotFlow 观察，手势和面板回弹帧直接更新背景，不叠加第二次缓动；普通开关仍使用 450ms 过渡，关闭从当前缩进继续。多个启用面板取最大展开比例。

新增真实触摸测试，验证手指仍按住时页面已还原、低速且低于阈值松手后回弹。Indent 4、Motion 2、Dismiss 5，共 11 项通过；Web Wasm distribution 构建通过。浏览器桌面鼠标按住下拖，确认面板下移时背景边距和圆角同步减小，证据为 `web-parity/drawer-indent-drag-held.png`。测试未将触摸 cancel 等同于正常松手：取消路径的面板停留行为不属于本次已验证回弹结论。


### 全量复核重启：Accordion 首轮（2026-10-30，尚未关闭）

继续完整清单，包含原先已勾选组件；editor 按要求排除。本次是实际代码及浏览器证据推进，不把本轮少量修改视为全站完成。

来源：官方 https://ui.shadcn.com/docs/components/base/accordion；源码 `apps/v4/registry/bases/base/ui/accordion.tsx`、`apps/v4/registry/styles/style-nova.css`，读取于 2026-10-30。浏览器测量官方标题为 14px/500/20px、上下 padding 10px、透明 1px 边框，首屏 Accordion 宽 384px，末项无分隔线。

修正：本地 Default 改成物流 FAQ 并限制为 384dp；Nova 标题上下 padding 10dp、显式 14/20 字号行高、透明边框和 42dp 最小高度；正文 14/20 Normal、底部 10dp；末项不绘制分隔线，多行标题图标顶对齐。保留其他 preset 的间距和分隔线。新增 `AccordionNovaTest` 验证 42dp 标题及展开/收起。最初尺寸测试测得 36dp，已通过显式 Style 行高与最小高度修正后重新验证。

验证：AccordionNovaTest 1 项 + AccordionScreenshotTest 6 项共 7 项通过；JVM/Wasm 编译和 Web distribution 通过。此命令未设置 Roborazzi 像素 verify，不将截图测试执行等同于基准逐像素一致；未重录基准。浏览器桌面 1095×687、手机 390×844 的浅深主题均查看；手机点击首项收起，再 ArrowDown/Enter 打开第二项，焦点与展开内容可见。截图 `web-parity/accordion-{official,local}-{desktop,phone}-{light,dark}.png`；键盘证据 `web-parity/accordion-local-keyboard-phone-dark.png`。

真实未关闭项：官网 Basic/Multiple/Disabled/Borders/Card/RTL 示例尚未逐一迁移；本地额外 API 示例暂保留。首屏后两项回答尚非官网全文；hover 下划线、focus ring 细节、chevron 图形与主题切换字重观感需继续比较。手机预览区水平 padding 为 40dp，官网更小，导致本地换行更早；标题/描述及代码区高度仍有页面骨架差异。键盘方向/RTL/动态排序及 keepMounted 隐藏焦点需要专门回归。故清单仍未勾选。


### Accordion 第二轮：完整示例源码、hover 与 RTL 键盘（2026-10-30，尚未关闭）

从官方当前仓库 `apps/v4/examples/base/accordion-{demo,basic,multiple,disabled,borders,card,rtl}.tsx` 读取七个文件。补齐默认 FAQ 后两项的真实回答，新增 Basic/Multiple/Disabled/Borders/Card 示例，RTL 示例采用官网英文、阿拉伯语、希伯来语内容，按钮循环切换语言和 LayoutDirection；目前语言控件为本地按钮，不与官网选择器外观等同。既有扩展 API 示例仍保留。

组件修正：默认实心箭头改成 16dp 圆端描边 chevron；hoverable 与真实 InteractionSource 驱动 LocalTextDecoration，浏览器已看到标题下划线。方向键根据 orientation 选择纵轴/横轴，横向 RTL 以 Left 前进、Right 后退，保留 Home/End。新增跳过 disabled + Home/End 和水平 RTL 两项回归。

验证：AccordionNovaTest 3 + AccordionScreenshotTest 6，共 9 项通过；JVM/Wasm 与 Web distribution 构建通过。未开启像素 verify，不重录基准。浏览器查看 Default hover、Disabled、Card 桌面浅色，并切换 RTL 三语言；Arabic 在手机深色无横向溢出，图标随方向移动到左侧。证据：`accordion-hover-desktop-light.png`、`accordion-card-desktop-light.png`、`accordion-rtl-{ar,he,en}-desktop-light.png`、`accordion-rtl-ar-phone-dark.png`（均位于 web-parity）。

继续保留未勾选：每个新增示例的完整桌面/手机、浅深四象限还未验完；手机 Card 上方局部截图出现较重的子项底色，需单独对照并定位。Borders 示例目前 item 的 16dp padding 同时缩进分隔线，官网分隔线应横跨外框内部，仍需修正。语言选择器样式、共享预览容器手机 40dp 留白、focus ring、动态顺序及 keepMounted 隐藏内容焦点仍待核对。不把新增示例源码或 9 项测试当作 Accordion 全面验收完成。


### Accordion 第三轮：焦点、重排、Borders/Card（2026-10-30，尚未关闭）

新增外部收起焦点恢复：内容 focusGroup 跟踪 hasFocus，关闭时请求对应标题焦点；keepMounted/forceMount 隐藏壳清除语义并拒绝进入焦点，保留内部组合状态。键盘导航用实际布局位置排序，修复 keyed children 重排后仍按首次注册顺序移动的问题；位置只在键盘事件中读取，未用每帧位置触发组合。

Borders 示例将 16dp 水平 inset 移到 Trigger/Content，分隔线横跨内框。Nova 标题底色改透明以融入 Card；浏览器复查还发现主题切换时 Card 内文字可能保留黑色，因此给 Nova 标题及字符串正文显式指定主题 foreground，新增实际 TextLayoutResult 颜色断言。仅修复背景而未修复文字时的截图已被最终构建截图替代。

验证：AccordionNovaTest 6 + AccordionScreenshotTest 6，共 12 项通过；包括外部关闭焦点回收/隐藏语义、keyed 重排方向键和 Home/End、深色 Card 文本颜色，以及此前尺寸/disabled/RTL 回归。Web distribution 通过，无像素基准重录或逐像素一致声明。Borders 与 Card 的桌面 1095×687 / 手机 390×844、浅深主题均人工检查，分隔线、透明背景、深色文字与换行修正可见。证据 `web-parity/accordion-{borders,card}-fixed-{desktop,phone}-{light,dark}.png`。

待完成：Basic/Multiple/Disabled/RTL 的全部组合状态与四象限仍需补齐；语言切换仍为按钮而非官网选择器；动态重排的末项 divider 仍基于注册次序，需要独立修正；keepMounted 还需验证 Tab 跳过与重新打开后的输入状态保持；共享预览容器的手机 40dp 留白、focus ring 精细参数和页面描述待对齐。上述差异仍保留，因此不勾选整组件或全站完成。


### Accordion 第四轮：隐藏 Tab、输入保持、末项顺序和语言下拉（2026-10-30，尚未关闭）

新增测试确认 keepMounted 输入框外部关闭后 Tab 到下一个可见按钮，重新打开仍保留输入。Nova 纵向根布局在测量直接子项前从 parent data 取得当前 keyed 顺序，分隔线按当前末项测量为 0 高；不把顺序写回 snapshot state，因此无需布局→组合循环。新增 42/43dp item 高度回归，覆盖末项随 keyed 重排变化。其他 preset 保留原布局。

RTL 从循环按钮改成真实 Select，下拉含 English/Arabic/Hebrew，选择后切换内容与 LayoutDirection；页面描述替换成官方文案。Nova focus 圆角使用 baseRadius（当前目录为 10dp），与本次官方 DOM 实测 10px 对照。新增可选 minPreviewHeight，Accordion 默认/Basic/Disabled/Borders=300dp、Multiple=450dp、Card=435dp，RTL 保留 288dp。默认其他组件仍 288dp；使用最小高度以免大字体/多行内容被强行裁剪，与官网固定高度的溢出策略仍存在差异，保留为明确待复核项。

**纠正此前截图推断**：2026-10-30 直接测量已加载官方 DOM，手机 390px 下预览容器 padding=40px、Accordion 宽260px，本地 40dp 并非已证实差异；撤回前几节“官网更小”的结论，不修改共享 padding。首轮截图不能独立证明 hydration 后的真实尺寸。官方当前预览高度同样由实际 DOM 确认。

验证：AccordionNovaTest 8 + AccordionScreenshotTest 6 =14 项、CatalogNavigationTest 6 项均通过；JVM/Wasm 与 Web distribution 通过。未重录基准或宣称逐像素一致。浏览器打开三项语言菜单并选 English，LTR 文本/图标方向实际改变；语言选择器桌面/手机浅深均查看，证据为 `accordion-language-select-{english,desktop-dark,phone-dark,phone-light}.png`。

尚未关闭：Basic/Multiple/Disabled 等新增示例的全部组合状态尚未逐一验完；forceMount 单独焦点矩阵、hover/disabled/controlled/动画反转需补齐。语言 Select 的尺寸和官网选择器还需详细比对；完整动画参数和预览固定高度溢出策略仍未验收。逐组件清单继续保持未完成，全站目标不缩减。


### Accordion 第五轮：真实数组示例、200ms 动画与透明 focus ring（2026-10-30，尚未关闭）

本轮纠正前两轮漏检：Basic/Multiple 的官网源码使用 items 数组，原迁移脚本仅抽取 JSX 文本，错误地把 `{item.trigger}` / `{item.content}` 留成字面量。已改为读取各自三条真实数据并保持首项 key 一致；Disabled 内容中的 HTML 实体已解码。新增 app/shared 的 AccordionExamplesTest，直接执行目录中实际 preview 并断言问题/回答，以及 Multiple 同时展开两项，防止“编译通过即内容正确”的误判。此前有关完整示例的表述由本节证据取代。

官方内容面板浏览器实测 animation=`0.2s ease-out`；Nova 改为只做高度 expand/shrink 的 200ms cubic-bezier(0,0,.58,1)，保留其他 preset 原动画与调用者自定义 enter/exit。新增冻结时钟的中途反向回归。多项展开的浅色截图暴露 focus shadow 从透明背景透进内部的灰填充：Nova focus ring 改为外侧圆角描边，仍保留主题色/3dp 外圈；新增真实白底内部像素断言，浏览器复核透明内部。指针也会显示 focused ring 与官网 focus-visible 的条件差异尚未关闭，透明边框的 focused 色精细对齐也仍待验收。

验证：最终源码已通过 core AccordionNovaTest 10 项、既有 AccordionScreenshotTest 6 项（上一全量命令）、app AccordionExamplesTest 2 项，合计 18 项不同测试通过；新增像素测试后只重跑 Nova 定向 10 项。JVM/Wasm 与 Web distribution 通过；不重录基准，不宣称全站逐像素一致。Basic 和 Multiple 的真实内容在桌面/手机浅深四象限已人工查看，Multiple 第二项打开时首项仍展开。证据 `web-parity/accordion-{basic,multiple}-real-{desktop,phone}-{light,dark}.png`；透明 focus 证据 `accordion-focus-transparent-fixed.png`。

继续未关闭：Disabled 完整四象限与外部受控拒绝变更、forceMount 单独行为、所有扩展 API 示例完整状态矩阵，以及固定高度溢出与 focus-visible 条件差异。上轮所说 Basic/Multiple 已迁移的源码本轮已真实修正；不以逐项增加测试数量替代最终浏览器矩阵验收。全量组件目标保持 active。


### Accordion 第六轮：Disabled、受控拒绝与 forceMount（2026-10-30，仍有说明的差异）

新增受控拒绝变更回归：onValueChange 只记录请求、不更新 value 时，原展开内容保持不变；disabled item 不发起请求。新增 forceMount 独立回归：外部收起恢复标题焦点、隐藏语义，重新打开保留输入。Nova focus ring 仅在 InputMode.Keyboard 下绘制，并补 1dp 内边框；鼠标点击不再显示外圈。Web 实测首次方向键未自动切换输入模式，因此在标题真实 KeyDown 事件中显式 requestInputMode(Keyboard)，再次用 ArrowDown+Enter 确认焦点外圈和下一项展开均可见。

验证：当前 core AccordionNovaTest 12 项通过；此前同批完整命令的 ScreenshotTest 6 项与 app AccordionExamplesTest 2 项通过，20 个不同测试在该批验证序列通过。最后输入模式修正后重跑 Nova 12 项和 Web distribution。未重录基准，未宣称像素完全一致。浏览器 Disabled 的桌面1095×687/手机390×844、浅深主题均检查，点击 disabled 无展开，第三项有真实回答。证据 `accordion-disabled-real-{desktop,phone}-{light,dark}.png`；`accordion-focus-mouse-no-ring.png` 和 `accordion-focus-keyboard-ring.png` 分别证明鼠标/键盘条件。

官方七组示例均已有浅深/桌面手机的首屏或展开检查记录；尚未据此宣称所有扩展 API 示例完整验收。继续保留：[1] 当前示例高度使用 minPreviewHeight，长内容会自适应增长，官网为固定高度和溢出策略；[2] 语言 Select 的具体尺寸、站点代码框与安装区布局还需逐像素对照；[3] 项目扩展的 horizontal/no-loop 等示例尚未完成全部组合状态。下一步开始 Alert 逐页核对，同时保留 Accordion 这些未关闭项，不把全站目标缩小到已处理部分。


### Alert 开始复核（2026-10-30）

官方页面 https://ui.shadcn.com/docs/components/base/alert 已实际打开，默认是 Payment successful 与 New feature available 两条带图标提示，源码为 `apps/v4/examples/base/alert-demo.tsx`。本地仍是 Success/no-icon 旧示例。官方 Nova CSS root 为 px-2.5/py-2、gap-0.5、图标16px和8px横向gap，Default 为 bg-card/text-card-foreground；本地 root 使用 twSpacing.lg/md（更大），Default 使用 colors.background。此处仅记录证实的缺口，未修改组件、未标记完成。官方首屏证据 `web-parity/alert-official-initial-desktop-light.png`；接下来修正组件/默认示例并完成浅深桌面手机与交互/无障碍检查。


### Alert 第一轮：Nova 组件布局与默认示例（2026-10-30，尚未关闭）

基线：官方 `apps/v4/examples/base/alert-demo.tsx` 及 style-nova.css。浏览器测得默认宽448px、每条80px、padding8px/10px、正文14/20、行间gap2px、图标16px+8px横向间距、圆角10px。默认底色/文字为 card/cardForeground，destructive 正文为 destructive/90。

修正：Nova 使用 Layout 将直接图标、正文和 Action 分别测量、放置，右侧按官网 pr-18 预留操作空间，按钮定位 root top/right 8dp；去掉 Nova 中依赖组合顺序捕获 actionHolder 的布局方式，其他 preset 保留旧实现。默认及 destructive 使用 card 底色，字体显式14/20、title Medium、正文 Normal；LineHeightStyle.Trim.None 保留完整20dp行盒，修正初版矮4dp。Assertive liveRegion 保留。目录首屏改为 Payment/New feature 两条真实提示、圆形图标、max448、gap16。

响应式容器：本次直接测量 Alert 官网使用 p-6（24px），手机 `h-auto`、桌面 h-72；并非所有组件统一40px。新增每个 ComponentExample 可选 previewPadding/mobileMinPreviewHeight，默认仍40dp和既有最小高度以保留其他组件；Alert 默认设padding24dp、手机最小高0dp。官方手机Alert宽292px、首条高100px、预览高244px，本地最终截图相符。此事实与 Accordion 的 p-10 不矛盾，后续按组件实测，不继续把全站容器视为统一参数。

验证：AlertNovaTest3（操作区避让及重组后按钮保留、destructive文字实际颜色/14sp/20sp、单行组合60dp及gap2）、AlertScreenshotTest4（旧 preset 浅深/action）、CatalogNavigationTest6，共13项通过；JVM/Wasm 和Web distribution通过，未重录基准，未声明全量像素verify。官方与本地默认示例的1095×687/390×844、浅深四象限均查看；本地深色 Action 桌面/手机确认长标题和正文未覆盖 Enable 按钮。证据 `alert-{official,local}-{desktop,phone}-{light,dark}.png`、`alert-action-{desktop,phone}-dark.png`。

仍待关闭：官网 Basic/Destructive/Action/RTL/标题-only 等完整示例迁移及状态矩阵；当前本地额外 API 示例不等于官网全部演示。更多尺寸、长操作按钮与复杂槽位组合，RTL 的图标/操作位置、读屏实际输出仍需核对。组件根布局在无界宽度父容器的固有宽度策略尚未验收；当前证据均为有界窗口/布局。Alert 不勾选完成，全站复核继续。


### Drawer：手势内缩方向与 snap 回归补充（2026-10-30）

确认当前 `DrawerIndentHost` 已消费面板的真实 anchored offset：手指按住拖动时页面缩放和圆角随进度变化，未达到关闭阈值松手时随面板回弹。此次补充 Nova 左、右、上三个方向及 snap 最低档至关闭区间的真实触摸回归，保留原下拉用例；新增多个 Drawer 取最大展开比例的状态验证。snap 在可见底部区域发起触摸，避免落到尚未露出的内容区域。

验证结果：`DrawerIndentTest` 9 项、`DrawerMotionTest` 2 项、`DrawerDismissTest` 5 项，共 **16 项通过**。此次新增验证与文档修订，未改动现有运行时代码；未新增浏览器或真机验证，不扩大此前截图证据的范围。


### Alert 第二轮：官网示例、RTL 与动态颜色（2026-10-30，尚未关闭）

对照官网 Base Alert 页面和 `apps/v4/examples/base/alert-{basic,destructive,action,colors,rtl}.tsx`：补入 Basic、Destructive、Action、Custom Colors、三语言 RTL，保留 Default 及项目扩展槽位示例。官方六组示例的预览统一使用 24dp padding、手机最小高度 0dp、桌面最小高度 288dp；最大组件宽度 448dp。Action 使用真实 Enable 按钮（官网没有业务回调，本地也不新增演示业务）。RTL 默认阿拉伯语，含英语和希伯来语，真实 Select 切换内容和布局方向；可复制代码同步。

Nova AlertTitle 移除写死的 TextStyle 颜色，让调用者的祖先 Style.contentColor 生效。根 Layout 为无界宽度补充固有宽度策略，新增水平滚动父容器与 RTL 图标/操作区避让测试。

实际 Custom Colors 示例的主题切换测试进一步复现 Text 桥接缺陷：只在 measure 读取祖先 Style 颜色，颜色变化仅触发 draw 时文字仍用旧颜色。Text 的既有兼容桥接现覆盖 draw 和 snapshot observation；draw 缓存读取不覆盖 measure 建立的订阅，避免空读取清空观察。继承颜色移除时恢复 Unspecified，让 LocalContentColor 回退重新生效。此桥接仍依赖现有内部 TextStyleProviderNode，并可能通过状态刷新额外调度文字更新；没有宣称解决 Compose 上游继承机制或完成性能基准。

验证：AlertNovaTest **6**（含 caller 自定义颜色、无界宽度、RTL 操作区）、AlertScreenshotTest **4**、TextScreenshotTest **4**、TextColorInheritanceTest **1**（主题不变时祖先颜色替换/移除、显式颜色优先级）、AlertExamplesTest **2**（实际 Custom Colors 浅到深主题切换、真实 RTL Select 切换内容），共 **17 项通过**。Web Wasm distribution 构建通过；保留既有 INVISIBLE_REFERENCE 与 webpack 警告。截图测试作为渲染回归执行，未重录基准，未声称通过全量像素 verify。

浏览器证据：Custom Colors 1095×687 和 390×844 浅深四象限均查看，并实际点击主题切换确认文字、背景及边框更新；手机内容宽约 292px、高约 120px，换行与官网对照。RTL 官网深色桌面、浅色手机，以及本地手机阿拉伯语浅深/切换 English 后的内容和方向已查看。证据：`alert-colors-{official,local}-{desktop,phone}-{light,dark}.png`、`alert-rtl-official-desktop-dark.png`、`alert-rtl-official-phone-light.png`、`alert-rtl-local-phone-english.png`。

仍待关闭的已知差异：官网 RTL 的语言选择器位于独立工具栏（约 144×28px，含说明按钮），本地目前在预览内容中（180×32dp）；Custom Colors 和 RTL 章节说明段落缺失；手机章节标题字号与官网不一致；代码框及安装/API 区布局尚未逐项完成；本地额外 Slots/Title/Description 示例的完整浅深/桌面手机矩阵、长操作区及读屏实际输出仍待核对。RTL 手机鼠标选择后 Select 出现 focus ring，focus-visible 条件还需单独复核。Alert 保持未完成，以上差异不因定向测试通过而视为关闭。


### Alert 第三轮：工具栏、章节排版、真实扩展示例与绘制颜色（2026-10-30，尚未关闭）

按官网 Base Alert 的 DOM 实测，RTL 工具栏改为独立 64dp 区域，语言 Select 为 144×28dp；预览主体在其下，桌面 288dp 最小高、手机自适应，主体 padding 24dp。右侧按钮提供真实三语言翻译说明 Popover（224dp 宽、10dp 留白、12/16sp 文本和分隔线），显式设置初始焦点，Escape 可关闭。提取目录专用 `RtlPreview`，语言状态仍由示例持有；目录控件与 Alert 组件本体分开，可复制 RTL 代码展示默认阿拉伯语组件结构。

共享 ComponentExample/PreviewCodeSection 增加可空 description slot，未提供时不产生额外区域。Custom Colors、RTL 增加说明，改为 Kotlin Style / LocalLayoutDirection 的说明而非照抄 React className API，RTL 配置链接保留到官网。示例章节标题采用本次 DOM 的桌面 18.75/26.25sp、手机 23.625/33.075sp 和 SemiBold；该共享标题排版也应用到其他目录示例，但并不代表其他组件已经复核。

修复扩展示例名实不符：Title 现在仅显示 AlertTitle，Description 仅显示 AlertDescription，不再都重复完整 Alert；Slots 保留组合示例。三组统一最大宽 448dp、padding24dp、手机自适应高度，代码与预览同步。

进一步替换上轮 Text 兼容桥接：使用 BasicText ColorProducer 在绘制时读取最近祖先自身明确设置的 contentColor，保留原有 Style/Text 测量边界；不再从 measure/draw 写 snapshot State 回组合，也不再使用聚合继承缓存。浏览器完整目录场景曾复现深色 Action 标题变黑，单独 JVM 场景未复现；直接读取每层 Style 自身已解析颜色后，最终桌面和手机深色截图均确认恢复。颜色替换、移除、显式 TextStyle 优先级、浅深往返及 Action 重绘改用实际绘制像素断言，GetTextLayoutResult 不作为 ColorProducer 最终绘制颜色的证据。当前仍依赖 Foundation 内部 StyleOuterNode/resolveAnimatedStyleFor，升级 Compose 时须以这些测试和 Web 场景重新验证；未声称完成上游缓存缺陷修复或原生辅助技术验收。

验证：最终相关用例 **88 项通过**：Accordion12、AlertNova6、AlertScreenshot4、Badge6、Button7、Card5、Form14、Selection19、TextColorInheritance1、TextScreenshot4、实际 Alert 示例4、目录导航6。TextScreenshot/AlertScreenshot 的 **8 张**显式像素验证通过，Web distribution 通过。首次显式 verify 发现 4 张旧基准与当前 Card 16dp 间距/Button Sm 28dp 高度不同；临时恢复修改前 Text 实现后仍出现同样 4 处，浅深对比图逐一查看后，仅用**旧 Text 实现**生成的结果更新这四张，再验证当前实现像素相同。隔离及验证记录见 `web-parity/alert-text-color-verification.json`，其余基准未修改。

浏览器最终矩阵：1095×687 桌面浅/深各12张连续覆盖，390×844 手机浅/深各9张，覆盖 Default、Basic、Destructive、Action、Custom Colors、RTL、Slots、Title、Description。原始文件清单见 `web-parity/alert-r3-final-matrix.json` 与 `alert-r3-final-{desktop,phone}-{light,dark}/`；中间的黑字问题保留为 `alert-r3-action-before-{desktop,phone}-dark.png`，不作为修复后证据。另已实际打开说明并按 Escape 关闭；语言 English 的真实切换前轮已验证，本轮 JVM 保留验证。截图覆盖证明上述视觉状态已查看，不等同于所有键盘、读屏及原生手势场景通过。

真实剩余差异（保持未勾选）：[1] 官网 Composition、四个 API 小节及相应目录锚点尚缺，Usage 仍是简化便捷重载；[2] Basic/Destructive/Action 的官网说明段落尚缺，本地多出 Examples 总标题；[3] Copy Page 尚未导出新 description slot 的说明内容，链接打开和代码展开/复制需在新结构下复核；[4] Select/说明按钮在鼠标返回焦点后仍可能显示 focus ring，通用 focus-visible 需继续处理；[5] 安装区采用本库真实 Gradle 依赖，不能改成实际无效的 shadcn npm 命令，其布局仍需继续对齐；[6] 长操作按钮/复杂槽位和实际读屏输出尚未完成对照。全站目标和已标记完成组件的复查范围不变，editor 继续跳过。


### Alert 第四轮：Composition/API、说明导出和目录（2026-10-30，尚未关闭）

补入官网的 Composition 层级及 Alert、AlertTitle、AlertDescription、AlertAction 四个 API 小节。API 说明和属性表对应当前 Kotlin 重载、Modifier、Style 及槽位参数，不把 React className 写成不存在的 Kotlin 参数。Usage 改为完整图标/标题/正文/操作按钮组合；代码片段改用库公开 Icon，而不是目录专用 DocIcon。Composition 使用纯文本代码块，不再显示错误的 Kotlin 语言标签；CodeBlock 新增可空 language，其他调用默认不变。

ComponentExample 的说明改为单份 descriptionMarkdown，页面渲染内联代码/链接，Copy Page 同时导出原始 Markdown。补齐 Basic、Destructive、Action 的说明段落；Custom Colors 和 RTL 继续采用真实 Kotlin Style/LocalLayoutDirection 用法。ComponentDoc 新增可选 compositionCode、apiSections 和可空 examplesHeading；Alert 不再多出 Examples 总标题，其他页面默认行为保持。共享章节二/三级标题按官网桌面和手机的已测值绘制，新增 heading 语义；这些共享变更不计为其他组件复核完成。

提取 componentPageMarkdown，导出顺序与页面一致：默认示例在安装说明之前，Usage 后为 Composition、其余示例、分组 API；包含说明、链接和属性表。新目录含 Composition、四个嵌套 API 项，点击可定位真实标题。目录滚动目前未同步 URL hash，这是仍待处理的共享导航差异。

验证 **13 项通过**：ComponentDocumentationTest3（完整 Markdown 内容/顺序、实际 Copy Page 按钮输出与 Composition/API 目录滚动、独立纯文本代码块复制载荷及反馈）、AlertExamplesTest4、CatalogNavigationTest6。最终 Web distribution 通过；本轮未改 core 组件、未重录截图基准，也未据此重复宣称上一轮 88 项均重新执行。

浏览器检查了 1440×900 桌面和 390×844 手机的浅深色 Composition/API；实际点击 Copy Page 看见 Copied，展开默认示例源码，点击 Composition/APIAction 目录项，点击 RTL 链接并确认新标签为 https://ui.shadcn.com/docs/rtl 后关闭测试标签。手机 API 表保留横向滚动，以容纳真实 Kotlin 类型，已实际滚到 Default 列。独立代码块复制在同步实际手机绘制帧后也观察到 Copied；对照期间的单节点标签改动已撤回，保留原 AnimatedContent。证据清单为 `web-parity/alert-r4/evidence.json`，截图均在同目录。

仍未关闭：[1] 共享 FocusRing.kt 的 focused 样式未区分输入方式，Select、说明/目录按钮在鼠标焦点返回后会显示 ring；需要实现并验证键盘/鼠标转换，而不是只隐藏本页 ring。[2] 安装区使用本库 Gradle 依赖，API 表使用 Kotlin 类型和横向滚动，均为明确的平台适配；安装/代码框的边框、留白、语法和内联代码样式尚未完成逐像素对照。[3] 目录滚动的 URL hash 同步未接入。[4] 超宽操作区、复杂槽位及实际读屏输出仍未完成验收；当前固定操作区留白适用于紧凑按钮，已在 API 中注明。Alert 保持未勾选，全站及已完成组件复查范围不变，editor 继续跳过。


### Drawer：绘制阶段直接跟随内缩手势（2026-10-30）

页面 graphicsLayer 直接读取当前 Drawer anchored 手势比例，缩放与圆角不再等待 snapshotFlow 将数值转存到 Animatable。保留 snapshotFlow 同步动画起点，普通开关和最后一个 Drawer 关闭时仍从当前值过渡；手势接近完全展开时也不提前切换为额外缓动。四向、snap 最低档到关闭的归一化规则以及多 Drawer 取最大展开比例保持现有行为。

新增冻结动画时钟的渲染测试，在 0.75、0.25、0.6 三个往返比例下检查实际页面宽度立即对应手势值，移除最后注册项后恢复原宽度。DrawerIndentTest 10 项、DrawerMotionTest 2 项、DrawerDismissTest 5 项，共 **17 项通过**；Web Wasm distribution 构建通过。仍有既有 Text.kt INVISIBLE_REFERENCE 与 webpack 警告。

浏览器桌面实际按住下拖并松手，确认页面边距/圆角随面板位移减小，低阈值松手后共同回弹；截图为 `web-parity/drawer-indent-direct-open.png`、`web-parity/drawer-indent-direct-held.png`、`web-parity/drawer-indent-direct-settled.png`。本轮未新增手机浏览器或真机验证，未更新其他组件进度或像素基准。


### Alert 第五轮：共享 focus-visible 与输入方式切换（2026-10-30，尚未关闭）

共享 focusRing 现在仅在真实焦点且 InputMode.Keyboard 时绘制非文本控件的边框/外圈；Destructive Button 的专用颜色遵守同一条件及 ring.enabled。文本输入继续使用 focusRingAlways，鼠标聚焦后保留输入提示。补入内部 keyboardInputMode 修饰器，在控件原有 click/navigation 消费按键之前切换输入模式，不消费按键或改变焦点。Button、Select、Checkbox、Switch、RadioButton、Toggle、Tabs、Slider、Badge、Chip、NavigationMenu、Sidebar、MessageScroller 和 Dialog/Sheet/Drawer 关闭按钮接入；复用 Button 的控件自动继承。该输入模式策略是 Compose 对应实现，不把程序化焦点/读屏/所有平台焦点来源等同于浏览器完整 CSS 启发式。

FocusVisibleTest 五项验证 Button、Select、Destructive 的指针→真实方向键→指针切换，保持真实焦点并断言实际外圈像素；Checkbox 用 Space 真正切换状态并显示键盘焦点圈，切回指针后保持选中及焦点；Input 在指针模式保留实际 ring 边框。snapshotFocused 显式进入键盘模式，避免键盘焦点截图依赖测试环境初始输入模式。

最终定向行为/渲染检查通过：FocusVisible、ButtonNova、SelectionNova、ChoiceNova、FormNova、Tabs、Slider、Sidebar、NavigationMenu、Chip、BadgeNova、OverlayEscape、DrawerDismiss，以及 AlertExamples、ComponentDocumentation、CatalogNavigation。Web Wasm distribution 通过；SelectFocus 浅深色两项显式 Roborazzi verify 通过。上述普通 jvmTest 通过不等于其他像素基准全部通过。

扩大像素检查发现 Chip 4、Tabs 3、Slider 4、Sidebar 14，共 25 项基准差异。临时禁用本轮 keyboard bridge、恢复无输入方式限制的旧焦点圈和旧 snapshotFocused 行为后，25 项仍然失败且实际输出图片逐项相同；隔离证据见 `web-parity/focus-baseline-isolation.json`。已恢复最终源码并重跑通过行为测试，没有重录这些基准；这些历史视觉差异仍需在各组件完整复核时逐项处理，不以更新图片掩盖。保留既有 Text.kt、AGP 实验选项、Gradle deprecated features 和 webpack 警告。

浏览器：官网 RTL 说明按钮确认键盘显示 focus-visible、指针操作后不显示。实际本地 Copy Page 在 1440×900 桌面与 390×844 手机、浅深色四象限分别验证指针无圈→方向键有圈→指针无圈，保留 Copied 反馈。截图 `web-parity/alert-focus-{desktop,phone}-{light,dark}-{pointer,keyboard,pointer-restored}.png`。手机浅色最初恢复截图仍处于过渡帧，等待末帧后重新捕获最终无圈状态。实际 RTL Select 鼠标选择 Hebrew 后无圈，方向键打开菜单后 Escape 关闭显示键盘焦点圈，证据 `alert-focus-select-{pointer,keyboard}.png`。本轮未完成说明 Popover 返回焦点、触摸真机或读屏验收。

仍待关闭：说明弹层 Escape 返回焦点的完整链路；安装/代码框细节；目录 URL hash；超宽操作按钮及复杂槽位；实际辅助技术输出。Alert 仍不勾选完成，全站复核范围不变，editor 继续跳过。


### Alert 第六轮：说明弹层焦点返回与章节 URL（2026-10-30，尚未关闭）

Popover 增加实例级焦点返回记录：触发器在指针初始事件或真实焦点进入时记录目标，优先返回调用者提供的 Button，纯文本触发器回退至外层 clickable。modal 内容的实际挂载/移除决定返回时机，退出动画结束后再请求焦点；重开会取消待执行请求。Popover 内容统一观察键盘输入并处理 Escape，RTL 目录说明移除局部 Escape 补丁，使用共享实现。该回补针对 modal Popover；非 modal 不自动抢回其他控件的焦点，本轮未扩展其完整交互验收。

新增 PopoverFocusTest 两项，真实鼠标点击第二个触发器，覆盖文本触发器和内嵌 Button。测试区分 Popup 与主窗口独立的 focus owner，冻结时钟检查退出中间帧尚未执行返回，退出完成后确实回到第二个触发器，并切为键盘输入模式。AlertExamplesTest 的真实 RTL 示例先聚焦语言选择器，再鼠标打开说明，Escape 后断言说明按钮获得焦点。

共享目录接入可选 DocNavigation 浏览器桥，原生目录保留本地滚动。对外 fragment 使用章节标题生成的可读 slug，例如 `#rtl`、`#composition`、`#custom-colors`、`#alertaction`；重复标题生成唯一后缀，内部稳定测试 tag 保留。Web pushState 保留 query，通过 popstate/hashchange 接回章节状态；刷新和初次进入等候真实标题布局后定位，目录点击仍平滑滚动，同一 hash 再次点击也能重新定位。浏览器跨组件路由变更清理旧 fragment；未知章节不移动当前页面。这里只实现按 fragment 定位，未声称持久化任意手动滚动位置。

ComponentDocumentationTest 新增真实页面深链接/目录输出/重复点击/空 hash 回到顶部/未知 hash 和其他路由隔离测试，以及重复标题 slug 唯一性测试。初版鼠标点击因 JVM 实际窗口只有 1024px、目录位于 1440dp 布局右侧而失败，改为同已有目录测试一致的 OnClick 语义操作；浏览器另外执行真实物理点击，不把测试窗口外的模拟点击当产品故障。

最终验证 **23 项通过**：PopoverFocus2、PopoverScreenshot4、OverlayEscape2、AlertExamples4、ComponentDocumentation5、CatalogNavigation6。Web Wasm distribution 通过；本轮未重录像素基准，也未执行全量显式像素 verify。保留既有 Text.kt INVISIBLE_REFERENCE、AGP 实验选项、Gradle deprecated features 与 webpack 警告。

官网对照：等待弹层实际移除后，触发器获得焦点且匹配 :focus-visible，最终 DOM 证据为 `web-parity/alert-r6-official-focus.json`；不以退出动画尚未结束时的 DOM 状态作为关闭结果。

浏览器：1440×900 桌面、390×844 手机的浅深色说明弹层均实际打开并按 Escape 关闭，确认键盘圈回到说明按钮、语言选择器不接走焦点；手机浅深色和桌面浅色另验证鼠标再开关后没有键盘圈。截图 `alert-r6-{desktop,phone}-{light,dark}-info-*.png`；手机完整加载 `#rtl` 时标题和说明位于首屏。桌面目录真实点击 Composition/AlertAction、浏览器 back/forward、带 hash 刷新、滚至页首后再次点击同一 hash，均使对应章节可见并保留 query，URL 与截图清单见 `web-parity/alert-r6-history.json`。API 最后小节受页面末尾最大滚动范围限制，不保证标题总在屏幕顶部；验证范围为真实章节可见。

剩余范围：安装/代码框的边框、留白、语法和内联代码细节；标题本身的 permalink 入口；窗口改尺寸或复杂内容重排后的滚动锚定；超宽操作区及复杂槽位；实际读屏输出。前轮 25 项历史截图基准差异仍待各组件复核处理。Alert 继续未勾选；共享修复不代表其他组件页完成对照，editor 继续跳过。


### Alert 第七轮：代码框、安装区与 Usage（2026-10-30，尚未关闭）

对照官网 DOM 实测修正目录共用 CodeBlock：无额外 Kotlin/Copy 文字栏；独立代码框圆角 18dp、无描边；纯代码水平/垂直留白 16/14dp；字号 14sp，普通行高 24.5sp，带行号行按官网 24.5px 加上下 1px 使用 26.5sp；行号 gutter 64dp、右侧留白 24dp。代码区使用官网实际渲染的浅色 #F8F8F8 / 深色 #161616，避免混用 muted 背景；Collapsed preview 渐隐同步使用该背景。内嵌示例代码传 RectangleShape，由预览外框处理底部圆角，分隔线处不再叠加独立代码框圆角。

复制改为右上角 28dp 图标按钮，距上/右 12/8dp，图标 16dp；成功切为勾号，提供 Copy/Copied 可访问名称与 polite live region。原 Copy Page 的文字动画仍保留。横向滚动只移动代码，行号和复制按钮不动；展开示例的纵向滚动放在代码内容内部，复制按钮留在代码外框右上。高度仍为实测的 288dp，未按 Tailwind 类名 max-h-96 误推为 384dp。滚动至代码边界后余量可继续传给页面，按钮随整个代码框移动，但不随代码内容移出。

安装区仍展示项目 Gradle 坐标与 commonMain 配置，用 build.gradle.kts 文件标题行区分上下文，没有伪造 npm/pnpm/yarn/bun 安装命令或不可用的包管理切换。Alert Usage 分为可复制的导入和调用两块；新增可空 usageImportsCode，Copy Page 同时导出两块，其他组件未提供时不产生空块。安装/Usage/Composition 内的标题到代码、连续代码间距按桌面和手机实测统一为 17.5dp；未据此修改尚未量取的 API 子节间距。

高亮的关键词/字符串/数字/注释等颜色向官网 github-light-default / vesper 语义主题靠齐；Vesper 注释保留上游 #8b8b8b94 的透明度。上游核对来源为 [github-light-default](https://raw.githubusercontent.com/shikijs/textmate-grammars-themes/main/packages/tm-themes/themes/github-light-default.json)、[vesper](https://raw.githubusercontent.com/shikijs/textmate-grammars-themes/main/packages/tm-themes/themes/vesper.json)。当前仍是轻量 Kotlin regex tokenizer，未声称完整移植 Shiki/TextMate 语法。内联代码字体改用项目实际打包的 Geist Mono，字号改为段落的 85%，对应官网桌面 12.75sp / 手机 16.065sp；圆角背景和精确内边距仍待后续处理。

新增 DocCodeBlockTest 4 项：实际 gutter/复制按钮位置与尺寸、水平滚动时 gutter 和复制保持不动、垂直滚动时复制保持可见、浅→深→浅切换下背景和字符串高亮的实际像素。ComponentDocumentationTest 更新为图标按钮语义复制，验证纯文本 Composition 的实际载荷与 Copied 反馈，并检查 Copy Page 包含拆分后的完整导入和调用。最终 **19 项通过**：DocCodeBlock4、ComponentDocumentation5、AlertExamples4、CatalogNavigation6；Web Wasm distribution 通过。未改 core 组件、未重录像素基准或执行全量像素 verify；最终构建仍有既有 webpack 警告。

浏览器最终检查：1440×900 桌面与 390×844 手机、浅深色的安装/Usage/Composition，保留 `alert-r7-{desktop,phone}-{light,dark}-{code,composition}.png`。真实点击安装代码复制后见勾号（desktop/phone-copy-feedback），手机实际水平滚动代码后行号保持固定（phone-code-horizontal-scroll）；桌面展开默认源码并纵向滚动，按滚动后的按钮位置实际复制见勾号（expanded-before/scrolled/copy-feedback）。原始样式数据为 `alert-r7-code-official-{light,dark}.json`、`alert-r7-code-spacing.json`、`alert-r7-inline-official.json`。浅深色比较是实际主题切换，不把静态渲染等同于键盘或读屏验收。

剩余：内联代码的圆角背景/左右及上下留白；轻量 Kotlin 高亮对完整语法的不足；复制图标目前复用目录 Lucide，官网为 Tabler，路径未逐点一致；代码/安装/API 的其余细微排版仍需复核；标题 permalink、复杂内容重排后的滚动锚定、超宽操作区/复杂槽位及实际读屏输出。前轮历史像素基准差异继续保留。Alert 仍未勾选完成，其他组件未因这些共用目录修改被视为复核完成，editor 继续跳过。


### Alert 第八轮：内联代码、标题 permalink 与复制图标（2026-10-30，尚未关闭）

补入内联代码背景：沿用上一轮实测的段落 85% 字号、Geist Mono；水平/垂直 padding 为代码字号的 0.3/0.125 倍，圆角为 0.35 倍，背景为 muted。用保留原始文字的 inline placeholders 提供字距/换行空间，在实际文字绘制阶段聚合同一代码片段、同一视觉行的背景。遵循官网实际 box-decoration-break:slice，只给整段起止端圆角。空格和连字符可换行；长标识符以文字布局提供的边界拆分，避免超宽占位整体消失。段落提供 SelectionContainer；链接仍为原始 LinkAnnotation.Url，原文不加入隐藏空格或额外符号。

像素测试和手机浏览器检查发现并修复两项实际问题：按字符 offset 合并背景会把不同视觉行合成一个大矩形，改按实际 placeholder 顶部聚合；占位宽度和边距的独立像素舍入会让最后字符被裁掉，改用同一像素边距计算宽度且片段自身不二次换行。TextLayoutResult 状态只在 drawBehind 读取，不传回 composition 或用于控制测量尺寸；未把此结构检查称为性能基准。

章节标题复用现有章节/URL 导航桥：Installation、Usage、Composition、示例标题、API Reference 及四个 API 子节具有对应可读 fragment；标题文本是可键盘访问的链接，hover 时下划线并显示相距 8dp 的 # 标记，键盘焦点也显示标记。# 本身可用指针点击，但从语义树清除且不建立第二个键盘停靠点。实际标题点击从 #basic 更新为 #destructive，并滚到该章节；不伪造不存在的锚点。标题下划线使用 Compose 原生 TextDecoration，尚未声明与 CSS underline-offset:4px 逐像素一致。

代码复制/成功图标改用官网 copy-button 的 Tabler outline SVG 路径，不再使用 Lucide 近似；仍以现有 16dp 图标/28dp 按钮呈现，无新增图标库运行时依赖。上游 [MIT 许可](https://github.com/tabler/tabler-icons/blob/main/LICENSE) 保存在 app/shared/src/commonMain/composeResources/files/licenses/tabler-icons.txt；已检查 Web distribution 中相应文件实际存在。目录原有其他 Lucide 示例图标未因此替换。

验证 **24 项通过**：DocParagraph3（保留原始文字/跨行及圆角像素、长标识符不消失、真实链接回调和浅深往返背景）、DocHeading2（可读 URL 元数据/指针点击/Tab+Enter、# 不重复朗读的语义结构）、DocCodeBlock4、AlertExamples4、ComponentDocumentation5、CatalogNavigation6。真实页面测试补查标题点击确实进入 DocNavigation 并更新请求版本。最终 Web Wasm distribution 通过；未改 core 组件、未重录像素基准、未执行全量显式像素 verify。最终构建仍有既有 webpack 警告。

浏览器最终矩阵：1440×900 桌面与 390×844 手机浅深色，检查 Destructive 跨行代码、Action 单段代码、Custom Colors 多个代码片段、RTL 代码/链接段落，见 `alert-r8-{desktop,phone}-{light,dark}-{inline,colors-inline}.png`。`alert-r8-heading-hover.png` 记录悬停下划线和 #，点击后真实 URL 为 `/alert?preview=r8-heading-inspect#destructive`；`alert-r8-{copy,copied}-glyph.png` 记录实际复制反馈。新页面在第一次 Canvas 渲染后等待章节定位完成，再捕获最终帧，未把加载首帧误作深链接失败。

剩余范围：轻量 Kotlin 高亮的完整语法、代码/安装/API 的其余细微排版、原生文字下划线定位与 DOM 的细节差异、窗口/复杂内容重排后的锚定、超宽操作区与复杂槽位、实际读屏输出。inline placeholder 的分段选择不等同 DOM 任意单字符选择，仍需跨平台人工验收；本轮保留原文和语义测试不替代该验收。历史像素基准差异继续保留。Alert 继续未勾选，全站及已完成组件复核范围不变，editor 继续跳过。


### Alert 第九轮：操作区约束、定位与多按钮示例（2026-10-30，尚未关闭）

读取当前 Base Alert 源码及官网真实 Nova 样式，确认 AlertAction 是绝对定位，根容器在有 action 时固定 pr-18（72px），并不按操作内容宽度自动扩大留白。对现有官网 Action 临时克隆、设置 300px 根宽、分别放入 56×24 与 136×24 的操作块后立即移除：正文宽均为 216px，操作距外边界 top/right 均为 9px；紧凑操作与正文相隔 8px，宽操作与正文重叠 72px。原始结果见 `web-parity/alert-r9-official-action-geometry.json`。这是上游固定留白约束，未把它误改为自动撑宽/换行。

发现本地 Nova 把 top/right 8px 按外边界处理，忽略了绝对定位包含块内的 1px 边框。按项目同数值 dp/px 约定，将布局定位从横向补偿 3dp、纵向 -1dp 修正为 2dp、0dp，使默认边框下距外框为 9dp；不改变正文默认 62dp 额外留白、无操作区像素或公开签名。KDoc/API 说明明确固定留白、多个控件放在同一 action slot、宽内容由调用者安排正文空间。

新增明确标为 Compose extension 的 Multiple actions (Compose) 示例：136dp Row 含两枚 64dp 按钮、间距 8dp；正文 Column 增加 80dp end padding（136 - 紧凑操作 56），继续保持 8dp 避让。Save/Undo 真实更新消息，源码、预览和 Copy Page 一致。此处演示调用者布局，不把项目扩展示例冒充官网已有演示，也未增加布局策略开关或新组件 API。Alert 示例总数从九组增至十组。

RTL 保留现有逻辑 end 定位并新增重组/点击几何回归；官网 RTL 示例的实际 root 使用 text-start/pe-18 逻辑规则。官网 RTL 演示未放 Action，因此本轮不声称完成其 Action 端到端逐像素对照；普通 RTL 图标/正文的既有验证范围也未扩大。

验证 **39 项通过（去重计数）**：AlertNova8、AlertExamples5、ComponentDocumentation5、CatalogNavigation6、DocParagraph3、DocHeading2、DocCodeBlock4，加 NovaActionScreenshot2 与既有 AlertScreenshot4。新测试检查 9dp 外框距离、216dp 正文宽与上游固定留白行为；292dp 组件下多操作正文避让、LTR→RTL 切换和重组后两枚按钮仍可用；实际目录示例也验证 Save/Undo 消息与避让。Web Wasm distribution 通过。

新增并逐张查看 `shadcn/core/src/jvmTest/snapshots/alert_nova_actions_{light,dark}.png`，覆盖紧凑操作、多按钮和 RTL 逻辑 end。两张新基准及四张旧 Alert 基准的显式 Roborazzi verify 均通过；未更新其他像素基准，未执行全项目像素验证。保留既有 INVISIBLE_REFERENCE、AGP 实验选项、Gradle deprecated features 和 webpack 警告。

浏览器检查了 1440×900 桌面、390×844 手机浅深色的普通 Action 和 Multiple actions (Compose)。实际点击 Save/Undo，消息分别变成 Save selected./Undo selected.，正文没有覆盖按钮，主题切换后状态保留。证据 `alert-r9-{desktop,phone}-{light,dark}-compact.png`、`alert-r9-{desktop,phone}-{light,dark}-actions.png`、`alert-r9-{desktop,phone}-light-{saved,undo}.png`。多按钮示例在手机消息变短时高度随内容自然缩小；截图未把这视作按钮丢失或重叠。

仍待关闭：任意超高操作/复杂嵌套槽位、RTL Action 的真实官网组合对照、全文法高亮与其余代码/API 细节、窗口和内容重排后的锚定、分段文字选择和实际辅助技术输出。未把固定 72px 上游约束继续列成待修 bug，也不把一个已验证的多按钮配方等同于所有自定义操作都已验收。Alert 保持未勾选，全站范围不变，editor 继续跳过。


### Alert Dialog 第一轮：默认弹层与响应式布局（2026-10-30，尚未关闭）

对照当前 [Base 源码](https://raw.githubusercontent.com/shadcn-ui/ui/main/apps/v4/registry/bases/base/ui/alert-dialog.tsx) 和官网实际 DOM/CSS，保存 `web-parity/alert-dialog-r1-official.json`。默认面板官网桌面 384px、手机 320px；Header gap 6px，标题 16px/24px；默认手机正文居中，桌面左对齐，Small 两种视口均居中。官网默认 Continue 是普通主按钮，完整说明以 “from our servers.” 结尾，本地默认预览、导出代码和 Usage 已同步修正。

AlertDialog 不再完全照搬 Dialog Header/Title：Nova 实现 6dp 间距、16sp/24sp 标题、紧凑 Header 和说明居中；Content 透传当前 size，受控 Default→Sm→Default 时对齐随之更新。默认手机面板设为 320dp，保留 DialogContent 原有视口边距和调用者样式覆盖。Nova 使用 1dp 外圈并移除占布局的边框；实测发现此版本 Style API 将 0.dp 识别为 Hairline，依据安装的 ResolvedStyle 源码改用 Dp.Unspecified 清除。没有修改 Dialog 的共用样式传递规则或公开函数签名，其他 preset 继续原有路径。

Nova 页脚在手机默认尺寸下按反向顺序纵排，操作按钮撑满可用宽度；Small 固定两列等宽，多于两项按行继续排列，保持 8dp 间距和逻辑方向。保留 RowScope 内容 API。真实手机截图发现只扩外层点击区域不会扩 Button 背景，因此在紧凑页脚通过局部样式给 Action/Cancel 内层 chrome 设置比例宽度；新增实际背景像素断言，避免以 semantics 宽度代替可见背景宽度。

最终普通定向测试 **37 项通过**：AlertDialogNova4、AlertDialogScreenshot4、SurfacesCompletion29。新测试覆盖标题真实文字行高、6dp 间距、大小切换及居中、Small 动态三项仍为两列、手机按钮反向顺序、实际背景像素、两个关闭回调及手机→桌面布局切换。新增并查看 Nova 默认弹层浅深两张基准，显式 Roborazzi verify 通过。旧 Vega AlertDialogScreenshot 的 4 张显式像素 verify 失配，已查看其中浅色普通、深色带 X 对照图，涉及尺寸/色彩等差异；未重录或声称原因已隔离，原始比较图复制为 `alert-dialog-r1-legacy-*.png`。本轮不把普通测试通过等同旧像素全部通过。

较早版本的 Web production distribution 构建通过；最终修复后重新构建了实际用于 8765 的 development distribution。浏览器首次检查发现服务目录仍是旧 development 产物，随后明确执行 wasmJsBrowserDevelopmentExecutableDistribution，并以更新后的页面说明确认正在查看新产物。最终 1440×900 与 390×844 浅深色默认弹层均已截图查看，见 `alert-dialog-r1-local-{desktop,phone}-{light,dark}.png`；官网对应四象限另存 official 文件。实际点击手机 Cancel/Continue、桌面 Cancel 均关闭；桌面外部点击仍保持，Escape 关闭，截图见 phone-confirmed、desktop-outside、desktop-escaped。加载/重绘期间出现的临时不完整字形未作为最终截图；最终帧确认 Continue 完整可见。汇总见 `web-parity/alert-dialog-r1-evidence.json`。

真实剩余：目录仍保留原有扩展示例，尚未替换成官网 Basic、Small、Media、Small with Media、Destructive、RTL 的完整集合，也缺 Composition/API 小节；Media Header 的桌面两列规则未完成；CSS text-balance/text-pretty 与本地自然换行不同，字重和面板总高度等细节继续核对。官网 role=alertdialog，aria-labelledby/aria-describedby 指向标题/说明，初始焦点为 Cancel；本轮只读取官网依据，尚未完成原生与 Web 对应语义及真实读屏输出的端到端验证。旧 Vega 四张基准失配保持未关闭，未更新其他组件基准，未执行全项目像素验证。既有 INVISIBLE_REFERENCE、AGP 实验选项、Gradle deprecated features 与 webpack 警告保留。Alert Dialog 继续未勾选，editor 继续跳过。


### Alert Dialog 第二轮：官方示例、Media 与三语预览（2026-10-30，尚未关闭）

从官方仓库树定位 `apps/v4/examples/base/alert-dialog-*.tsx`，读取 demo、basic、small、media、small-media、destructive、rtl 七份源码；保留在 `web-parity/alert-dialog-r2-source/`，来源与校验值见 `alert-dialog-r2-evidence.json`。目录原来的重复 API 演示收敛为官网七组 Default、Basic、Small、Media、Small with Media、Destructive、RTL；公开 API 没有移除，Compose 的 modal=false、可选关闭按钮、outside-dismiss 回调及 Lg 等扩展在 API 文字中明确标注。Basic 与 Default 的说明文字按各自源码保留差别，没有混用。

补上 Composition 和 Kotlin API 小节，Usage 导入/调用分开复制；Media 用 CircleFadingPlus，Small with Media 用 Bluetooth，Destructive 用 Trash2，与上游图标一致，core 没有增加图标集依赖。RTL 使用英文、阿拉伯文、希伯来文原始译文，并同时提供普通与带 Media 的 Small 弹层；目录语言工具栏沿用共用 RtlPreview，导出代码展示作用域独立的阿拉伯语两弹层配方，文字中说明目录另提供三语切换。

Media Header 不再始终纵排：Nova 默认桌面为起始侧媒体列及正文列，间隔 16dp；40dp Media 带 8dp 底部留白，标题/正文间隔 6dp。手机及 Sm 上下居中，Media 到标题合计 14dp。使用内部 layoutId 标识媒体，无新增公开开关/签名；逻辑起始侧随 LayoutDirection 变化。移除媒体后直接恢复普通 Header，不保留旧列宽；无界宽度按自然内容测量。Media 默认 inline Icon 为 24dp，Style 覆盖仍可用于 destructive 底色。

Destructive 正文用 Header 内的 BasicText 组合保留 Settings 真链接及下划线；默认继承 mutedForeground，悬停才变前景色。上游 href="#" 是当前页面占位链接，本地目录桥接到空 fragment，没有伪造设置页面或触发 Delete。JVM 实际点击链接验证导航请求且弹层仍显示；浏览器手机真实点击也从 `?preview=r2-settings-link#destructive` 变为同 query 的 `#`，背景回到页首，确认框保持打开且 Cancel 仍可关闭，见 settings-link.json 及两张对应截图。

**去重 56 项通过**：AlertDialogNova4、Media3、MediaScreenshot4、AlertDialogExamples5、ComponentDocumentation5、CatalogNavigation6、SurfacesCompletion29。覆盖媒体 40dp/inline 24dp、16dp 列间距、LTR→RTL→手机→移除媒体、Small 在桌面居中、无界测量；六组普通示例分别实际取消/确认；阿拉伯语、英语、希伯来语各自打开两种尺寸；Settings 点击、导出结构和弹层保持打开时 destructive 媒体背景浅→深→浅实际像素。新增四张 Media/Small Media 浅深基准并逐张查看；四张新基准与第一轮两张 Nova 默认基准的显式 Roborazzi verify 均通过。旧 Vega 四张失配继续保留，没有重录或宣布解决；SurfacesCompletion 普通测试通过不等于其全部旧图已显式像素验证。最终 8765 实际服务的 development distribution 构建通过，既有 AGP、Gradle deprecated features、webpack 等警告保留。

浏览器：官网与本地各 28 张新弹层图，1440×900 桌面、390×844 手机、浅深色，覆盖 Basic、Small、Media、Small with Media、Destructive、RTL 普通和 RTL Small；Default 沿用第一轮矩阵并保留 Nova 像素回归。原始全屏文件为 `alert-dialog-r2-{official,local}-{desktop,phone}-{light,dark}-*.png`，四张 compare 文件将中心面板裁切并排，已逐项查看，不能当作像素 diff。桌面/手机本地都实际打开并使用 Escape 关闭，在同一预览切换浅深色；官网的触发器有唯一来源元素。一次官网长距离自动滚动发生 CDP 输入超时，确认终止状态后改用章节链接直接定位，后续全部成功。手机 RTL 普通弹层最初与鼠标位置重叠产生 action hover，移开鼠标重截浅深两张，未将悬停灰色误记为基础色缺陷。截图尺寸和完整清单已检查写入 evidence.json。

仍待关闭：并排图显示 CSS text-balance/text-pretty 与自然换行、部分字重/行高及面板总高度差异，阿拉伯语回退字体度量也不同；不能据几何测试宣称字体完全一致。深色 Outline 底色/边框有共用色板差异：官网实测 --input 为 15% 白、--border 为 10% 白（`alert-dialog-r2-official-dark-tokens.json`），本地 BaseColor.baseDark 仍令 input=border（不透明深灰）；后续应从主题角色修正，不能仅给 Alert Dialog 按钮硬编码。alertdialog 角色、标题/说明关联、完整键盘闭环及实际读屏输出仍需端到端核对；自定义/嵌套 Header 超出官方 Media→Title→Description 组合的完整矩阵也未宣称完成。旧 Vega 四张失配未隔离解决。Alert Dialog 保持未勾选，全站和已完成组件复核范围不变，editor 继续跳过。


### Drawer：内缩手势完整区间同步（2026-10-30）

补齐真实手势进度到达 100% 时的边界：之前 graphicsLayer 在比例等于 1 时切回 Animatable，往返拖动后可能短暂使用旧比例。现在以手势进度是否就绪区分驱动方式，真实 anchored offset 在完整 0..1 区间直接驱动页面缩放及圆角；尚未建立手势锚点或没有面板的场景保留开关动画。未就绪以 null 表示，不再与完全展开的 1 混用。四向、snap 最低档以下的关闭区间、多 Drawer 取最大比例保持原规则。

扩展冻结动画时钟的现有渲染测试，依次验证 0.75、0.25、0.6、1、0、1，直接测量页面宽度。修复前在 1.0 复现失败：预期 376，实际 385.6；修复后通过。DrawerIndentTest 10 项、DrawerMotionTest 2 项、DrawerDismissTest 5 项，共 **17 项通过**；Web Wasm development distribution 构建通过。本次未新增浏览器手势或真机验证，未重录像素基准。仍有既有 Text.kt INVISIBLE_REFERENCE 与 webpack 警告。


### 共享主题复核：七套基础色、透明控件与外环（2026-10-30，组件继续未勾选）

从官方 `apps/v4/public/r/colors/<base>.json` 读取 Neutral、Stone、Zinc、Mauve、Olive、Mist、Taupe，并读取 `style-nova.css` 与 `themes.ts`；原文和重新联网校验的来源清单位于 `web-parity/theme-dark-r1-source/`。独立 TSV 固定了 31 个上游颜色角色 × 七套基础色 × 浅深两种模式，共 **434 个值**，不从被测 BaseColor 反推期望。`BaseColorSourceTest` 两项逐个精确比较 OKLCH 解析后的 Color（含 alpha）。

修正所有基础色的深色 border/sidebarBorder 为 10% 白、input 为 15% 白；card/popover/sidebar 使用各自 900 色阶，ring 使用 500；浅色 sidebar 使用 50 色阶，浅深 chart1..5 使用各自 300/500/600/700/800，所有深色 sidebarPrimary 使用官方 blue700。没有只修 Alert Dialog 的按钮，也没有把 Neutral 与 Zinc 的近似色当作精确一致。主题文档 `shadcn-parity.md` 的颜色表已重写，移除“不透明深灰足够接近”的旧结论；旧 LightColors/DarkColors 常量不代表当前 Theme 使用的 BaseColor。

Nova Input/Textarea、Checkbox、Radio、Select、InputGroup 恢复真实浅色透明填充、深色 input/30，Select hover 为 input/50；未选中 Switch 使用深色 input/80。移除提前 compositeOver 全局 background 的处理，使控件在 Popover 或有色父容器上按实际底色混合。保留禁用填充及透明度规则。ThemeRoleRenderingTest 四项覆盖七套基础色浅→深→浅、真实 popover 底色、浅色有色父容器透出、禁用填充和 Nova/Vega + Base/Blue 切换中的 input/border 角色；不据此宣称 Accent 已对齐。

透明背景暴露出 native Style dropShadow 的内部实心投影会透过控件填充：焦点 Input 变灰，错误态整块变红。内部 cssStyleable 保留 Style 的真实交互状态和最终 caller shape，使用 Compose 原生 DropShadowPainter、Shape.createOutline 与 ClipOp.Difference 仅绘制轮廓外部；没有重新手绘各圆角几何或增加公共 API。DrawModifierNode 在仅阴影颜色变更时主动失效，避免冻结动画时钟后阴影不刷新。三个测试覆盖浅色焦点/错误态内部不染色、调用者三角形 shape、冻结时钟下 red→blue 阴影更新。AGENTS.md 和 FocusRing KDoc 已记录透明表面的原因与约束。高级阴影动画、externalPadding 等完整覆盖仍未完成。

此前本轮最终定向验证 **43 项通过**：BaseColorSource2、ThemeRoleRendering4、CssStyleable3、FocusVisible5、ButtonNova7、FormNova14、AlertDialogNova4、AlertDialogMediaScreenshot4，显式开启 Roborazzi verify；其中 **14 张受影响/相关基准通过像素验证**，逐张检查后只更新 10 张，其余四张保留原文件。前后 hash 和记录/验证命令分别保存为 theme-dark-r1-baselines-{before,after}.json、theme-dark-r1-validation.json。此前 ChoiceNova27、ButtonGroupScreenshot6、AlertDialogExamples5 普通测试亦通过，未据普通运行声称它们的旧基准已通过像素比较。Web development distribution 构建通过；后续 Drawer 修改已另外运行其 17 项测试和 Web 构建，本节没有把它们混成同一轮 43 项。

浏览器：查看七个实际控件页面 Button/Input/Checkbox/Radio Group/Switch/Select/Input Group 的 28 张默认画面，覆盖 1440×900 桌面、390×844 手机和浅深色；六张 contact 图仅做裁切并排阅读，原始截图完整保留。另实际输入 demo（密码字段显示四个掩码）、切换主题并重新聚焦，逐张查看 Input 四张焦点画面：填充不受外环染色，文字和输入值保留。打开 Select 并 Escape 关闭，查看四张弹层图；打开默认 Alert Dialog 并 Escape 关闭，查看四张图，桌面行式和手机反向纵排页脚保留，深色 Outline 的填充与边框已分开。文件前缀 theme-dark-r1，索引见 browser/interactions/alert-browser 三份 JSON 与 evidence.json。此次未重跑所有官网示例，参考第二轮官方四象限截图及本轮重新校验的官方原始令牌/CSS；这些共享回归不等于每个控件完整验收。

独立 Accent 覆盖的 17 色矩阵与官方 themes.ts 角色映射已在下一节补齐。仍待关闭：其余组件受共享色板影响的像素基准、现有 Chip/Tabs/Slider/Sidebar 的 25 项历史失配及四张旧 Vega AlertDialog 失配；Alert Dialog 字体/换行、辅助技术、复杂自定义槽位继续保留。未执行全项目像素验证，未把旧差异自动重录为新基准。保留既有 Text.kt INVISIBLE_REFERENCE、AGP 实验选项、Gradle deprecation 与 webpack 警告。清单所有未完成项及已完成组件复查范围不变，editor 继续跳过。


### 共享强调色复核：官方角色覆盖与实时切换（2026-10-30）

重新下载当前官方 `apps/v4/registry/themes.ts`，校验与上一轮参考一致，保存原文及来源/hash 于 `web-parity/accent-r1/`。现有 Accent 的手调十六进制值与当前官方值不符，并错误地把 ring 改为主色、漏掉 secondary/chart 覆盖；其旧 KDoc 还声称只改 primary，与实际代码也不一致。已替换为精确匹配官方 OKLCH 的命名 TwColors，保留 Accent 枚举名、label 和 applyTo 的公开使用方式。

现在覆盖 primary/primaryForeground、secondary/secondaryForeground、chart1..5、sidebarPrimary/sidebarPrimaryForeground；其他角色继续保留基础色，包括焦点 ring。普通主色浅色 700/深色 800，Lime/Yellow 为 400/500 且文字用 900；图表为 300/500/600/700/800，Sidebar 主色为 600/500，前景色按各自源码单独处理。明确保留上游所有强调色使用 Zinc secondary 的行为，以及 Rose 深色额外 sidebar=Zinc900；没有为了统一规律而删掉这些特例。Accent.Base 直接返回原始 base 对象。AGENTS.md 和 shadcn-parity.md 的“手调色不应迁移”旧结论已修正。

独立 fixture 为 **375 条上游覆盖值**（17 色 × 浅深通常各 11 条，Rose 深色多一条 sidebar）。AccentSourceTest 在 238 种强调色/基础色/模式组合上比较全部 31 个上游角色，既验证明确覆盖，也验证未列出的角色不变，并保留本地额外 destructiveForeground；另验证 14 种 Base 身份不变。修复前有效夹具复现 Amber 浅深色差异，修复后通过。AccentRenderingTest 验证实际按钮背景/内容在 Blue、Lime、Fuchsia、Rose、Amber、Base 的往返切换，含 Neutral/Mauve、浅→深→浅、不改尺寸；恢复 Base 时实际绘制恢复，ring 始终保留基础色。

最终 **52 项定向测试通过**：AccentSource3、AccentRendering3、BaseColorSource2、ThemeRoleRendering4、ColorContrast3、CssStyleable3、FocusVisible5、ButtonNova7、FormNova14、AlertDialogNova4、MediaScreenshot4。两张新的强调色矩阵已查看，包含 Base 与 17 色、真实 Primary/Secondary Button 以及 Sidebar/Chart 色样；首次测试图表头/次按钮文字换行已通过加宽测试列修正，未改生产组件适配测试。显式 Roborazzi verify 共 **16 张基准通过**（两张新图及上轮 14 张旧图），未重录 14 张旧图。色样不等于完整 Sidebar/Chart 组件验收。Web development distribution 构建通过；既有 AGP/Gradle/Text.kt/webpack 警告保留。

浏览器实际选择本地和官网 Blue、Lime，查看桌面浅深色，另查看 Lime 手机浅深色；本地在手机切换主题和横向滚动底部控制区。官方读取到 Lime 主色、文字和 ring 的最终计算样式，核对异步 iframe 更新后再记录，未将短暂仍为 Neutral 的帧视为结果。首次官方浅色 Lime 图仍处在按钮颜色过渡中，查看后改为等待实际 backgroundColor 到达期望值，重截并查看最终帧。官方 Theme 菜单选择后仍会保留打开，初次头部开关点击被遮挡，重新观察并 Escape 关闭菜单后完成切换，未改用脚本直接写样式。截图和清单位于 `web-parity/accent-r1/`。此轮只确认强调色角色及相关切换；手机与桌面的完整 Create 布局、字体、图标及交互未宣称对齐。

新确认的剩余差异：Create 本地 Contribution History 五根柱体等高且直接使用 primary，官方六根不同高度，独立 Chart Color=Neutral 时仍保留灰色图表；本地 Chart Color 状态在 Customizer 内，尚未传入 PreviewCanvas，换 Theme 会错误地连带改变图表。Payout 金额与标签在本地桌面挤压换行，卡片宽度/页脚与手机控制区也存在差异。以上作为后续图表/目录预览工作保留，不能因 token 测试通过而关闭。其余组件清单、历史 25+4 张像素失配、字体/复杂状态/辅助技术等缺口仍在；editor 继续跳过。


### Create / Chart：独立 Chart Color 与真实六柱（2026-10-30，Chart 未关闭）

读取官方 chart-color-picker、theme-picker、base-color-picker、picker、design-system-provider、search-params 和 registry/config.ts，确认 buildRegistryTheme 仅以 Chart Color 覆盖 chart1..5，Theme 主色不应随之变化。选项是“当前基础色 + 17 强调色”，不是全部七种基础色同时可选；Base Color 更换时仅将不再有效的旧基础色图表选择回退，新基础色仍保留有效强调色。源文与官网真实六柱颜色/几何保存在 `web-parity/chart-color-r1/`。

将 chartColor 从 Customizer 局部提升到 CreatePage，由显式值/回调同时传给选择器和 PreviewCanvas；改变桌面/手机布局分支不再丢失选择。预览内部只通过局部 chart 角色作用域传递图表颜色，不覆盖按钮和其他控件的 Theme。所有 Shuffle 入口生成当前新基础色下有效的图表选项，Reset 同步恢复 Neutral。Base、Theme、Chart Color 的触发器色样改为官方固定深色来源、16dp，删除上游没有的选项色点；基础色用 mutedForeground，强调色用 primary，不再用 chart1 冒充所选色样。

Contribution History 改为官网 Dec..May 六个月与 800/1100/900/1300/750/1400 数据，使用 chart2、最高 40dp 宽和 Nova 6dp 顶部圆角。原来每个 Column 的唯一弹性子项使用 weight(value)，使每根柱体都填满剩余高度；改为独立绘图区内按值比例填充。保留官方 200dp 总高、8dp 顶部、30dp XAxis 空间，最高柱实际为 162dp，与官网读取的 162px 同数值；真实文本标签为 12sp/18sp。Lyra/Sera 保留源码直角规则。第二预览的私有柱图也修正唯一 weight 子项导致的等高问题；其现有柱图/圆形饼图占位改读 chart 角色。Analytics 折线按官方 chart1、Dividend 迷你柱图按 chart2 消费独立色板，避免首次预览正确而其他真实绘图区仍跟随 Theme。

**15 项定向测试通过**：CreateChartColor4、CatalogPreview5、CatalogNavigation6。独立 fixture 比较全部 24 种图表选择的浅深 chart1..5（240 值）及固定深色色样（24 值）；实际点击 Chart Color=Amber 后改 Theme=Lime、切换 1000→390→1000、浅→深，柱体仍保持 Amber；Base 变化保留强调色、无效基础色回退及菜单 Reset 均验证。几何测试直接量六根柱体高度比例、共同底边、40dp 宽上限及 162dp 最大高；Analytics/Dividend 实际像素验证 Blue→Amber 绑定。测试最初在 Mauve 和 Reset 上误匹配其他已显示文字，随后用基础色标签排除条件和菜单快捷键限定真正选项，未修改组件以迎合测试。Web development distribution 最终通过。未重录像素基准，未运行全项目像素验证；保留既有 AGP/Gradle/webpack 警告。

浏览器选择官网与本地 Theme=Lime / Chart Color=Blue，逐张查看 1440×900 和 390×844 浅深色画面，按钮仍为 Lime，柱图为 Blue；本地另验证换 Theme 后图表仍为 Neutral 的中间状态。官网 DOM 六柱的宽、高、底边和计算 fill 已保存，浅深色均 chart2=Blue500，按钮分别 Lime400/Lime500。一次组合任务因测试选项歧义失败，Webpack 已执行但最终 distribution 尚未同步，浏览器因此仍显示旧菜单色点；测试修正后明确重新分发，再以菜单色点已移除、16dp 色样和最终截图确认新产物。早期错误菜单/旧产物帧仅作为中间诊断，不作为最终证据。最终源码、构建、测试和截图对应关系见 evidence.json。

仍待关闭：Contribution 卡片宽度、摘要原文/手机纵排、footer 撑满及整体排版与官网仍有差异，Payout 金额拥挤仍保留；图表悬停 tooltip、键盘/读屏、桌面菜单 hover/焦点临时预览及锁定参数后 Shuffle 的官方语义尚未补齐。第二预览柱图的真实多系列/数据、饼图扇区，Stock Performance 等文字/图形占位仍需重做，独立色板绑定不能代替完整图表实现。未把目录私有图形修复等同于 core Chart 本页或全部组件验收；所有清单范围不变，editor 继续跳过。


### Create 财务预览：响应式卡片、真实 Payout 与输入保留（2026-10-30，Card/Field/Slider 继续未关闭）

读取官方 preview-02/index.tsx、Preview iframe 容器、Card、Contribution/Payout 源码并测量真实 DOM/CSS。官网 Nova 财务网格在 iframe 宽度小于 768 时为 2400px/16px gap，普通桌面为 3000px/40px gap；3xl CSSOM 明确为 min-width:1600px，此时 gap48。七列扣除两侧 padding 及六个 gap 后计算列宽，每列另有 4px 内边距；实际桌面 iframe1176 下卡片宽 374.8516px。Lyra/Mira 的 md 分支为 2600px/gap24。原本每列固定320dp、全局24dp内边距，导致 Payout 主体拥挤；现在按来源计算财务网格，双列区域及内部子列同步计算，第二套预览保留原布局路径。

官网 iframe 文档高度2322/client806，垂直滚动作用于整个网格。本地财务列改为共享纵向滚动；横向与纵向使用两个原生布局节点，避免把两个 scroll modifier 叠在同一节点后纵向 ScrollBy 语义动作被遮蔽。真实滑动和 ScrollBy 都验证各列同步。响应式摘要使用预览容器自己的宽度，模拟网页 iframe 内的 md，而非外层窗口宽度。暗色财务画布也从 muted/30 修正为源码 dark:bg-background，浅→深→浅像素检查通过。

Contribution 改用 CardContent/CardFooter 结构，摘要原文为 Upcoming / May 25, 2024 / $1,000 scheduled 与 Auto-Save Plan / Accelerated / Recurring weekly，手机纵排、桌面两列，间隔12dp。背景 muted/50、圆角10dp、内边距横12/纵10及显式字体大小/行高取自官网测量；不是据声明值宣称实际字体绘制完全一致。View Full Report 的背景真实铺满页脚可用宽度，测试同时检查几何与左端内部像素，避免只扩点击区。

Payout 修正更多功能差异：货币由固定值/no-op 回调改为真实本地选择状态；静态 Progress 改为 Slider，50..10000、step50、默认2500，动态显示两位小数、24sp 半粗 tabular 数字，label 加权、金额保持单行并按基线排布；Notes 最小外框100dp且保留输入。Header 使用独立 Title/Description/Action，X 使用原来源的 muted Ghost 图标按钮，Save Threshold 在真实 CardFooter 内撑满。官网 X、Save Threshold、View Full Report 均没有业务 handler，本地同样没有伪造保存/关闭结果。

浏览器又发现桌面→手机分支重建了 PreviewCanvas，虽然 chartColor 已提升到 CreatePage，Payout 内部普通 remember 状态仍会重置。现在 CreatePage 以记住的 movableContentOf 在两个布局分支移动同一预览内容，rememberUpdatedState 保持图表参数更新，卡片自己的局部状态仍由卡片持有。新增真实页面回归，先选 EUR、设10000、填备注，再切 1000→390→1000，三个值均保留；现有 Chart Color 在同一移动内容上继续更新并通过。

**最终21项通过**：FinancePreview6、CreateChartColor4、CatalogPreview5、CatalogNavigation6；Web development distribution 通过。新增测试涵盖 768/1600 边界及七列总宽、摘要往返断点、muted/50 与全宽按钮实际像素、EUR/Notes 状态、Slider SetProgress/方向键/Home/End、最大金额单行不重叠、100dp 外框完整 layout 尺寸、共享滑动及滚动语义、暗色画布、页面跨分支移动的输入保留。BasicTextField 的内部编辑语义边界仅82dp，实际 layout 含边框/内边距为100dp，未拿内部高度误判绘制外框。未修改 core 公共 API，未重录像素基准、未执行全项目像素验证；保留既有 AGP/Gradle/webpack 警告。

浏览器对官网和本地均实际选 EUR、用 End 调到10000、输入 Finance preview note，并切浅深色；官网通过实际 input[type=range] 键盘目标操作，未用脚本改金额值。检查1440×900桌面、390×844手机的 Contribution，并横向移动到 Payout 查看两种主题；本地先出现输入回默认值的失败观察，修复移动内容后重新截图，以手机 Payout 仍显示 EUR/$10000.00/备注及返回桌面同值为最终证据。来源、精确测试结果、最终构建和截图 hash 见 web-parity/finance-r1/evidence.json，中间 pan/旧状态图不列为最终证据。

剩余：外层 Create 顶栏高度、预览框起点与控制区排版仍不同；实际文本行高/字体度量及摘要整体高度尚未隔离（本地 Contribution 总高度仍略短），不能把显式18/28等声明当作最终像素相等。摘要仍是目录私有布局，待 Item 本体复查时核对其透明 border/完整布局规则和可复用组成。手机 Notes 的原生右下手动 resize、完整标签关联及实际读屏/IME尚未验收；Compose Canvas 与官网 DOM 的 wheel 位移在本次驱动下也不同，待独立隔离。仅默认 Nova 做了完整浏览器矩阵，其他 preset 不据纯计算测试宣称已验收。其余财务卡片仍含占位/简化内容，图表 tooltip、多系列与饼图扇区、菜单临时预览和参数锁定等前述缺口保留。全站及已完成组件复核范围未缩减，editor 继续跳过。


### Drawer：往返拖动内缩回归（2026-10-30）

继续核对当前直接读取 anchored 手势比例的实现，没有新增第二层缓动。补强四向及 snap drawer 的真实触摸测试：按住下继续沿关闭方向拖动，再反向移回原位置，分别断言页面宽度和面板内部内容的位置同步恢复，最后低速松手回弹。面板位置必须测量 offset 内的子内容；调用方的 testTag 在 offset 之前，测量它会得到未平移的位置，不能作为实际面板移动证据。

DrawerIndentTest 10、DrawerMotionTest 2、DrawerDismissTest 5，共 **17 项通过**；Web Wasm development distribution 构建通过。浏览器本地桌面复核实际按住下拖、反向拖回、松手回弹，证据为 `web-parity/drawer-indent-reverse-open.png`、`drawer-indent-reverse-held.png`、`drawer-indent-reverse-returning.png`、`drawer-indent-reverse-settled.png`。本轮补强测试和验证记录，未修改已接通的运行时代码，未新增手机或真机验收，未更新像素基准。


### Aspect Ratio：真实图片、响应式配方与共享 Select 标签（2026-10-30，尚未关闭）

重新读取当前 Base wrapper、四份 examples、文档及 Select 源码，并用官网真实 DOM/CSS 校验。当前 Base AspectRatio 是 CSS aspect-ratio 包装，不再是旧 KDoc 所述的 Radix 包装；Kotlin 保留既有 ratio=1f 默认值，并在 API 中说明与网页 ratio 必填的区别。复现 Style { width(160.dp) } 在 400dp 父容器下仍测为 400dp，调整为先应用 Style 约束、再测比例，签名不变。新增回归覆盖父宽及比例实时改变、16:9/1:1/9:16、子内容撑满和 Kotlin 默认值。

目录原来的固定宽度灰色占位替换为官网 Default、Square、Portrait、RTL。三种最大宽度为384/192/160dp；普通预览288dp高，Portrait和RTL主体384dp高，图片 Crop、10dp圆角、grayscale与深色20%亮度均按官网处理。图源实际为 avatar.vercel.sh/shadcn1；原始 PNG、curl 优化 PNG、浏览器实际协商图片的压缩纹理不同，最后从官网已加载 img 解码120×120原图（未应用CSS滤镜），作为本地资源打包，灰度和亮度仍在绘制时应用。文件来源与中间版本均保存，不新增网络图片加载依赖。KDoc及AGENTS中的旧占位/依赖说明已更新。

预览边框此前只绘制、不占布局，手机图片为262dp，官网为260px。共享 PreviewCodeSection 为1dp边框预留布局，新增完整预览框测试验证390宽、页面两侧24、边框1、预览左右40后的真实260×146dp图片。该共享更改只通过本轮目录回归，不能据此关闭其他组件。

RTL提供Arabic/English/Hebrew的真实caption及LocalLayoutDirection。浏览器发现共享Select会把希伯来语标签折为两行；重新实测官网弹层宽144、高92，三项均28高，确认需要保留anchor宽度，使用源码ItemText的shrink-0/whitespace-nowrap行为。Nova标签保留行内弹性分配，但内层按固有宽度单行测量，由滚动视口裁切过长文本；行间gap从8改为6dp。新增希伯来语/长标签行数、28dp行高、144dp弹层及方向键/Enter提交回归。其他preset保持原测量路径，Select完整验收仍未关闭。

最终 **49项功能/布局回归通过**：AspectRatioLayout3、AspectRatioScreenshot2、SelectLabelLayout2、ChoiceNova27、AspectRatioExamples4、CatalogPreview5、CatalogNavigation6。另单独显式Roborazzi verify验证两张既有AspectRatio基准unchanged，未重录；普通ChoiceNova运行不代表其旧像素基准已通过。Web development distribution通过。保留既有Text.kt INVISIBLE_REFERENCE、AGP实验选项、Gradle deprecation和webpack警告。原始测试XML、精确用例和Gradle收据见web-parity/aspect-ratio-r1/。

浏览器查看官网1440×900桌面/390×844手机浅深色整页，以及本地四个示例全部16张四象限画面；四张contact仅为原始截图裁切并排。手机深色实际打开语言菜单，通过方向键/Enter依次选English、Hebrew，caption正确改变，三条标签保持单行。首次自动截图因整页导航重置临时主题而取到浅色，已改为页内导航/滚动后重截并逐张确认最终浅深色；早期错色/未加载帧不列为最终证据。官网原生弹层曾因目标离开视口及文本子节点被遮挡导致驱动点击失败，本轮只以真实DOM尺寸与源码核对官网菜单，不宣称已完成官网语言选择的键盘验收。证据清单为web-parity/aspect-ratio-r1/evidence.json。

**真实剩余差异**：Arabic与English/Hebrew下，caption实际行框使图片垂直位置不同；官网Arabic figure为216+8+20=244px，而本地显式14/20sp声明尚不能证明实际行框一致。需继续隔离字体回退/行高，不能用固定声明或语义文本存在关闭此项。页面骨架起点、说明换行、代码/安装区排版等共享差异也保留；Kotlin API和离线图片是明确适配。Aspect Ratio保持未勾选，其他未完成及已完成组件的全站复查范围不变，editor继续跳过。


### Drawer：真实拖动不依赖动画时钟（2026-10-30）

保留现有绘制阶段直接读取面板 anchored offset 的实现；补充 API 说明，明确页面缩放、圆角随拖动与回弹变化。加强四向普通面板和 snap 面板的五个真实触摸用例：按住拖动后冻结动画时钟，再继续移动及反向移动，检查面板与页面都立即响应；恢复时钟后低速松手，检查共同回弹。这样同时覆盖实际手势接线与 Host 渲染，避免仅通过模拟进度验证。

继续核验时确认上述接线已在当前代码生效，无需再次修改运行时动画；复跑三个测试类仍为 17 项、0 失败。同步更新 Indent effect 的实际预览和展示代码说明，提示拖动会连续还原页面缩放与圆角，松手共同回弹。此处 snap 规则是最低档以上保持内缩，仅最低档至关闭区间逐步还原。

本次 DrawerIndentTest 10、DrawerMotionTest 2、DrawerDismissTest 5，共 **17 项通过**；Web Wasm development distribution 构建通过，可在 `/drawer` 的 Indent effect 示例查看。保留既有 Text.kt INVISIBLE_REFERENCE 和 webpack 警告。未新增浏览器截图或重录像素基准；浏览器证据仍以上述已记录截图为准。

#### 补充：越过阈值松手关闭（2026-10-30）

当前运行时已直接读取面板位移驱动页面缩放和圆角，本次未重复改动接线。新增真实触摸回归 `dismissingDragRestoresPageDuringSettleAndAfterRemoval`：冻结动画时钟，向下拖过关闭阈值，确认按住时页面已还原；松手推进 64ms 后确认页面继续还原，最终检查关闭回调及页面恢复至完整 400dp 宽度。结合既有四方向、snap、反向拖动和回弹测试，DrawerIndentTest 11、DrawerMotionTest 2、DrawerDismissTest 5，共 **18 项通过**；Web Wasm development distribution 构建通过。未新增浏览器或真机验证，未重录像素基准。


### Aspect Ratio 第二轮：固定行框、真实字体与三宽复核（2026-10-30）

关闭第一轮遗留的 RTL caption 行框问题。实际 Compose/Skia 排版不能仅凭 fontSize=14sp、lineHeight=20sp 保证最终20dp：Geist/回退字体会给出21dp或更高的自然文本布局。当前依赖的 Skia 源码也没有消费 LineHeightStyle 的 mode，故没有继续叠加无效参数或修改共享 Text。RTL 使用真实20dp语义容器，内层自然测量并居中绘制；字形允许超出行框，与 CSS 行框行为一致，不裁切 Arabic 上下笔画。新增实际 catalog 字体、四组宽度/主题往返语言回归，检查图片位置不变、caption语义高度20以及整页实际字形与参考绘制一致。

共享 Nova Select 的字符串标签也使用20sp行框，内层保留固有宽度单行及完整字形。真实 Noto Sans Arabic 测试先复现选项高度38dp，再验证修正为28dp；点击行继续持有名称、选中及点击语义。自定义内容槽与其他preset沿用原路径，公共签名未改。新增字体及OFL许可仅位于JVM测试资源，不进入应用/发布库；两个新的浅深Arabic菜单像素基准已人工查看。

最终53项功能/布局回归通过：AspectRatioLayout3、AspectRatioScreenshot2、SelectLabelLayout5、ChoiceNova27、AspectRatioExamples5、CatalogPreview5、CatalogNavigation6。单独显式Roborazzi verify四张基准unchanged（两张既有Aspect Ratio、两张新Arabic选项）；没有重录旧基准，也不据普通ChoiceNova成功宣称其历史像素全部通过。JS编译和Web Wasm development distribution通过。保留既有Text.kt INVISIBLE_REFERENCE、AGP实验选项、Gradle deprecation及webpack警告。结果XML、compact Gradle ledger、像素收据和源码hash均在 [aspect-ratio-r2/evidence.json](web-parity/aspect-ratio-r2/evidence.json)。

浏览器补齐1440×1000、1095×687、390×844，浅深色及Arabic/English/Hebrew切换，共18张官网RTL与18张本地RTL；另含六张本地语言菜单、六张紧凑宽静态示例、两张官网紧凑宽整页和两张手机展开源码前后截图，共52张。八张contact来自原图裁切。官网语言选择由真实键盘操作完成并关闭菜单；本地实际选择Hebrew后展开代码，caption与选择状态保持。复核发现原本标为desktop/light/ar的本地图实际上停在Default，已用真实RTL选择重新捕获并重建对照图；最终12组“来源×宽度×主题”均验证三语图片top一致。其他静态示例的桌面/手机证据延续第一轮，紧凑宽本轮补验。

剩余差异已说明：同一原图像素在sRGB canvas灰度为204、Display P3为210，与本地/官网截图采样相符，说明本例滤镜工作色域造成浅色明度差；不通过换图、烘焙P3灰图或改全局主题掩盖。该结论只限本次同源像素实验，不推断所有组件色彩管线。手机官网图片CSS高146.25px，截图146/147，Compose整数布局146；caption均为20。字形和亚像素抗锯齿不宣称字节一致。RTL章节定位后图片top在官网/本地桌面及紧凑宽为306/285、手机389/366，该共享章节/toolbar/滚动定位差异继续归P1；正文、安装和代码区排版也仍待全站关闭。Kotlin ratio默认1f及离线图片是明确适配。Aspect Ratio本体和四份示例复核勾选，Select、全站P1/P3、已完成组件的复查范围继续保留，editor跳过。


### Attachment 首轮：状态继承、card 颜色与实际扫光（2026-10-30，尚未关闭）

读取固定上游96da9cc的Base Attachment、Nova CSS、MDX和六个配方。官网MDX固定styleName=base-rhea，实际默认卡片圆角18px，不能直接作为本库Nova圆角目标；本库继续按Nova样式，通用结构/状态按Base逻辑。来源与hash见 [attachment-r1/sources.json](web-parity/attachment-r1/sources.json)。本地原来的首屏是三个状态横排，官网首屏是图片上传托盘；本次没有把首屏视为已对齐。

修正根背景background→card，显式提供cardForeground。根state通过私有、每个实例独立的CompositionLocal传入Title、Description、Media：Uploading/Processing默认启动标题扫光，Error默认显示destructive/10媒体底色、destructive媒体内容色及destructive/80描述。显式Title state、Description isError仍能覆盖；嵌套Attachment以自己的根状态为准，单独使用这些子组件仍默认Done。参数类型和槽位结构未改；目录说明改为当前官网描述。

标题原先用透明度脉冲近似shimmer，现在使用已有Modifier.shimmer的字形扫光，保留完整文本和颜色。测试发现无限动画必须在composition前冻结时钟，否则Compose测试策略可能取消动画；修正测试时钟后，Style版rememberShimmerStyle在当前文本路径仍没有实际像素变化，而同一时钟条件下绘制版Modifier产生变化。故采用已有绘制版，不修改共享Text或Shimmer。该Style路径问题留待Shimmer本体复查，不据此推断所有Style内容都无效。

新增AttachmentStateTest四项：浅→深→浅及Error→Done实际背景/媒体/描述像素；显式中性描述和嵌套根状态独立；字符串Uploading标题、lambda Processing标题随时间改变，显式Done保持静态，根改Done后停止。加上既有AttachmentScreenshot6、ShimmerScreenshot2，最终 **12项通过**。JS编译和Web Wasm development distribution通过。没有开启Roborazzi verify/record，没有重录旧基准；保留既有Text.kt、AGP实验选项、Gradle deprecation及webpack警告。测试XML和compact ledger存于attachment-r1。

浏览器实看官网States与本地当前三状态首屏，在1440×1000、390×844及浅深色四象限检查；手机在本地真实横向滚动到错误附件，再切主题并滚回。确认深色卡片底色、浅深错误媒体底色及现有描述显示。原始截图、官网手机DOM/CSS测量、验证范围和hash见 [attachment-r1/evidence.json](web-parity/attachment-r1/evidence.json)。这些证据不等于完整五态示例或整页已对齐。

明确剩余：迁移官网Default/Image/五States/Sizes/Group/Trigger示例和真实图片；当前图片仍是emoji，占位与源码导入需替换。根Style覆盖、宽度约束、内容弹性及动作区需要逐项修正，三种size的间距/字号/媒体尺寸、vertical布局及image variant尚未对齐。Trigger全卡预览与动作独立点击/键盘/标签、焦点/hover/disabled，Group吸附、边缘渐隐及键盘滚动仍未验收。本地“Default error”实际内容为Done/isError=false，待按真实示例结构重组。Attachment继续未勾选，全站及已完成组件复查目标保持，editor跳过。

### Attachment 第二轮：六组示例、真实布局、Trigger 与渐隐（2026-10-30，尚未关闭）

迁移 Default、Image、五 States、Sizes、Group、Trigger 六份实际配方；用与官网同源的三张照片替换 emoji，作为 catalog 离线资源保存，未给 core 增加图片或图标依赖。每份示例展示完整 Kotlin 代码，补充 Composition 和 API 说明。预览采用实际测得的外层 p40 加配方 py48，浅色底 #F8F8F8；修复此前把这两层留白合并导致示例高度不足的问题。图片 URL、下载参数、hash 及来源见 image-sources.json。

Nova 根布局改为实际单一 Style 容器和内部 Layout：水平媒体 40/32/28dp，间距 8/10/6dp；动作先占空间，长文件名在剩余宽度单行省略，不再挤出动作。根 Style 宽度与底色覆盖实际卡片；纵向含文字卡片默认宽120dp、纯媒体96dp，媒体随可用宽度保持方形，动作覆盖在顶端而不占正文高度。文字使用已有继承路径，Default14sp、Sm/Xs12sp，标题 medium；图标和 Spinner 通过既有行内图标尺寸继承，操作按钮保留 icon-xs 的12dp。新增 Image media 重载，上传/处理中/错误时透明度60%，Idle/Done100%；最终 Style shape 能裁切图片，保留原 icon 重载。

Trigger 使用实际可点击根节点和真实 Dialog，设置448dp最大内容宽度；独立 Copy/Remove 操作不冒泡打开预览，保持上游示例的空操作。JVM 验证 Enter 打开、Escape 关闭和焦点返回原卡片，图片 URI 使用注入 UriHandler 验证，移除按钮不打开图片。浏览器桌面/手机浅深四组均实际打开弹窗、Escape 关闭，并查看动作后、打开及关闭截图；Canvas 的 DOM activeElement 不能证明 Compose 焦点恢复，该结论由实际焦点测试支持。本轮没有据 JVM URI 测试宣称浏览器图片新标签已验收。

AttachmentGroup 接入现有 ScrollFade 绘制路径的内部扩展：边缘最大 min(12%视口,40dp)，按96dp滚动范围及 CSS ease-in-out 逐渐显露，RTL 交换物理左右边缘，无溢出不渐隐。公共 ScrollFade 原签名、默认24dp和即时边缘规则保持；其横向 RTL 同步修正。新增实际像素测试验证起点、中间渐增、终点、无溢出及 RTL，未以仅计算 helper 的测试代替组件接线。手机浏览器实际水平滚动后确认内容可达、边缘渐隐生效；官网会吸附到后续卡片，本地仍自由滚动，这一差异继续保留。

同时根据真实 Spinner 源码的 Lucide LoaderCircle，修正此前全画布弧线过粗、绘制超出标称尺寸：24单位 viewBox、半径9、笔画2、288°弧段。Sm12/Default16/Lg24 和显式 Style20 覆盖均检查实际捕获尺寸；八张浏览器原生 SVG 固定角度参考图支持浅深色轮廓、覆盖面积和空白边界断言。不同渲染器的边缘采样并非逐像素相同，测试允许明确的亚像素覆盖带。只重录经过 before/after/compare 检查的两张既有 Spinner 基准；64×64对照图变化仅在[22,22,41,41]字形区，背景与布局未变。新 Attachment 两张基准单独记录，共享 ScrollFade 旧基准未重录。

最终定向回归71项通过（core49、catalog22，含更强的 Trigger 焦点返回断言），JS编译和 Wasm development distribution通过。随后显式 Roborazzi verify 六张均 unchanged：Attachment Nova2、Spinner2、共享 ScrollFade2；无 blanket record。保留 Text.kt INVISIBLE_REFERENCE、AGP实验选项、Gradle deprecation 和 webpack 既有警告。XML、compact ledger、像素结果、SVG参考、源文件及资源 hash 见 [attachment-r2/evidence.json](web-parity/attachment-r2/evidence.json)。

浏览器最终矩阵为官网24张＋本地24张：1440×1000/390×844、浅深色、六组示例；本地在渐隐构建后重新捕获。六张示例 contact 全部查看，DOM/图像测量的预览高度差均在2px以内。另查看四组官网/本地弹窗对照及本地独立动作和关闭状态，手机 Group 起点与滚动后截图。弹窗截图捕获早于最后新增 Group 渐隐，Trigger 实现未再变化；这些截图不证明拖动、原生读屏等未测场景。

条件与剩余差异明确保留：官网 MDX 固定 base-rhea，本库继续 base-nova，Rhea 卡片18px圆角和 Dialog 的更大圆角、p6、较深遮罩不作为 Nova 的目标；Nova 的 p4、radius-xl、black/10 与固定源码一致。官网手机 native scrollbar 占15px，client375/本地390，预览325/340、水平卡片245/260，影响截断，属于共享 P1。深色标题字重、文件名截断仍有可见差异；已确认 Geist medium500 字体存在，不能把原因归为缺少中等字重。Lucide FileSearch 图形版本也有差异。Nova xs 媒体目前8dp圆角、目标6dp，纵向多动作目前0dp间距、目标4dp；Group mandatory snap、scroll-px-1、键盘滚动/overscroll、复杂槽位/极小约束、hover/focus-within、独立 AttachmentTrigger 及完整辅助技术映射仍未关闭。共享正文、源码与章节定位差异继续归 P1。Attachment、Spinner 和全站目标均不标为完成，editor继续跳过。

### Attachment 第三轮：Group 吸附、真实键盘与输入尺度校准（2026-10-30，尚未关闭）

保留现有 Row/RowScope 布局，通过实际 Attachment 的 Modifier.Node 边界提供 SnapLayoutInfoProvider。停下时读取当前几何，4dp 逻辑滚动留白随 RTL 镜像；不同宽度的卡片、末项边界以及大于 snapport 的卡片都参与正确的停留区间。嵌套卡片/嵌套 Group 不作为外层的额外停点，非 Attachment 内容不产生停点；没有把组件换成固定页宽的 Pager，也没有新建位置 CompositionLocal 注册表。公共函数签名保持不变，现有根 Style 和调用方 Modifier 继续生效。

Group 增加容器焦点和未被子控件处理的左右键导航；超宽卡片内按40dp逐步滚动，普通卡片跳至相邻停点。真实 Tab/Shift+Tab 到达屏外操作，并验证操作按钮完整位于容器范围内；Enter 只激活当前操作。嵌套滚动只隔离横向余量和横向剩余速度。滚轮结束时将交给最终吸附的速度归零，触摸拖动继续使用原生惯性；多事件滚轮用例覆盖实际 UI 路径。Group 现在有额外容器焦点；调用方应通过 modifier 提供语义标签，该 Compose 适配与完整读屏映射仍需后续复核。

新增 AttachmentGroupSnapTest 共9项：不同宽度及左右方向的真实低速拖动/4dp停点/末项；超宽卡片保留按住位置；动作上的方向键；横向边缘不带动父容器；超宽卡片逐步键盘移动；非附件节点不生成停点；原实例动态切换 RTL；Tab/Shift+Tab/Enter 的焦点与点击；多事件鼠标滚轮停下后不再追加触摸惯性。测试使用实际 Group 原点测量，修正了把测试场景24dp外边距误当作滚动偏差的断言；滚轮单位按当前 Foundation 的平台转换选取，不再假定旧文档中的64dp换算。

最终80项相关回归全部通过（core58、catalog22），JS编译和干净 Wasm development distribution通过。随后显式 Roborazzi verify 六张均 unchanged：Attachment Nova2、Spinner2、共享 ScrollFade2。本轮没有新增或重录像素基准。临时数值诊断 println 已移除；保留既有 Text.kt INVISIBLE_REFERENCE、AGP实验选项、Gradle deprecation 和 webpack 警告。XML、compact ledger、源码与构建 hash 见 [attachment-r3/evidence.json](web-parity/attachment-r3/evidence.json)。

浏览器检查发现本机 Retina 主机的 DPR 仿真差异：在同源、无框架的原生滚动容器里，DPR=1 时单个 CDP deltaX=330 让原生容器滚动330，但 DOM WheelEvent 报告660；DPR=2 时 DOM和原生滚动均为330。原生 CSS snap 容器在两种模式都停264。这解释了早期把 DPR=1 的本地末项截图当作单纯吸附惯性问题的不充分判断；不向组件加入除以2的补丁。校准 HTML/JSON、数值轨迹及早期截图仅作诊断证据，不作为最终输入等价验收；临时页面已从 Web 构建目录移除。

最终采用原生 DPR=2，记录实际收到的单个330px DOM事件；官网/本地分别16张，共32张，覆盖1440×1000与390×844、浅深色，以及起点、滚轮后、末项、键盘返回四种状态。四张最终 contact 全部查看：桌面滚轮后均显示第二张 workspace、末项 renderer 可达、向左键均返回 customers；手机同样能滚动到第二张及末项。截图工具输出仍为 CSS 分辨率，实际 DPR 和事件数据以 official-browser.json/local-browser.json 为准，不能从 PNG 宽度反推 DPR。截图等待目标滚动位置或卡片边界稳定，不以整页其他动画的 PNG 哈希作为滚动完成条件。

此外补齐第二轮尚缺的真实图片链接验收：手机浅色中点击 workspace 图片卡片，实际打开正确 Unsplash URL 的新标签，查看了原图；点击同卡片的移除按钮不打开链接。已关闭本次创建的图片标签，任务空间恢复两页。该结果见 photo-interactions.json 与两张实际截图，不扩大为所有浏览器/原生 UriHandler 的结论。

仍保留可见差异：官网手机 Group 容器245px、四卡各245px，本地容器260dp、示例卡各256dp，来源包含 native scrollbar 的15px共享视口差异；更窄 Group 内根 Attachment 的 max-w-full 默认上限尚未实现/验收。官网手机末项向左键可先在其末项停留区间内移4px，本地当前宽度条件下进入前一张，未据桌面通过宣称手机轨迹完全一致。浅深标题字重、图标版本、Nova xs 媒体6dp圆角及纵向多动作4dp间距仍未修正；不通过示例手动样式覆盖来隐藏这些缺口。复杂槽位/极小约束、hover/focus-within、独立 AttachmentTrigger、完整读屏语义及原生设备手势仍保留第二轮范围。Attachment 和全站目标继续未完成，editor跳过。


### Attachment 第四轮：约束边界与显式部件作用域（2026-10-30，尚未关闭）

根 Attachment 在最近的 Group 视口内落实 max-w-full：普通 Modifier.width/Style.width 的期望宽度受视口上限约束，Nova 水平 min-w-40 仍保留，显式 requiredWidth 继续允许可滚动溢出。视口变化时在测量阶段使真实卡片重新测量，无新增布局到 composition 的 Snapshot 回写。四项真实布局测试覆盖 245→300→200→245 往返、LTR/RTL、独立及嵌套 Group、100dp 极窄容器和 requiredWidth 超宽末端可达。既有超宽 snap 用例改用 requiredWidth，明确区别普通期望尺寸与强制溢出。

新增 AttachmentScope，将根 size/orientation 显式传给 Media/Actions；未添加密度 CompositionLocal。xs 媒体使用 rounded-md，其余 Nova 媒体 rounded-lg；纵向动作 gap 使用 xs token（默认4dp），水平0，RTL 下纵向动作保持源码 right-3 的物理右侧。五项作用域测试检查实时尺寸切换的实际圆角像素、嵌套隔离、提取的扩展 helper、方向/布局往返、物理右侧及新旧 JVM 函数符号。

**历史数值更正**：第二、三轮记录及其历史 evidence 中的“xs 当前8dp、目标6dp”误用了 Tailwind 默认刻度。当前 catalog 基础半径为10dp，主题映射 rounded-lg=10dp、rounded-md=8dp、rounded-xl=14dp；本轮使用主题 token，未硬编码6dp。历史带 hash 的证据不改写，以本节更正为准。

公共 content/actions 槽位类型改为 @Composable AttachmentScope.() -> Unit，版本升至 **0.3.0 开发版**。普通尾随 lambda 可直接使用，存储的旧无接收者槽位需改类型或以 { oldContent() } 包装；提取含 Media/Actions 的 helper 应作为 AttachmentScope 扩展才能继承根变体。保留 HIDDEN 旧签名桥，测试证明新旧 JVM 符号存在，不据此宣称已完成外部预编译客户端验收。README 明确该版本是当前 checkout，未发布 Maven 包。

功能/布局测试 **89项通过**（core67、catalog22），JS 编译与 Wasm development distribution通过。显式 Roborazzi verify 正确的六张 Attachment Nova/Spinner/共享 ScrollFade 基准均 unchanged，未重录。额外检查旧 Vega Attachment 六张基准时全部出现差异，保留 compare/actual 和失败 XML；已查看的 vertical/light 差异集中于根圆角，尚未逐张归因，不能把这六张算作通过或直接覆盖。收据见 attachment-r4/test-results.json、pixel-verification.json 和 legacy-pixel-differences/。既有 AGP 实验项、Gradle deprecation 等警告保留。


浏览器本轮 Group 官网16张、本地24张：1440×1000、390×844 浅深色各四状态（起点/单次330px滚轮/末项/向左键），另加本地375×844浅深色共8张用于对齐官网 clientWidth375 下的245px内容视口。实际 DPR=2，DOM输入校验通过；常规390px本地内容仍260dp，不能混称两者同一窗口条件下轨迹相同。对齐探针下卡片上限245dp生效，末项向左键保留 renderer，原始像素局部平移搜索在浅深色均得到准确+4px、误差0；官网 scrollLeft771→767。标准390px本地则仍跳至 customers，其视口差异归共享P1。八张 Group contact 已逐张查看，完整记录及局部取样区域见 local-browser.json、official-browser.json、aligned-shift-probe.json。

Sizes 官网/本地各四张，桌面手机浅深色全部查看；官网真实 DOM 确认媒体宽40/32/28px、圆角10/10/8px，手机测量见 official-sizes-measurement.json。本地手机 Sizes 标题定位低于官网，但三卡完整可见，不能把章节位置视为已对齐；字重/图标、根圆角（官网Rhea、本库Nova）、动作焦点外观及共享页面骨架差异继续保留。本次浏览器采集辅助脚本曾因375px截图比例/变量引用错误中止，修正后完整重跑；最终JSON均包含完整矩阵，未保留失败帧作最终证据。

本轮关闭 max-w-full、xs主题圆角及纵向多动作间距三个明确缺口。Attachment仍未勾选：六张旧Vega像素差异待归因；复杂槽位与极小约束尚非穷尽验证；hover/focus-within、独立AttachmentTrigger、完整辅助技术映射和原生设备手势仍待复核，字体/图标及全站P1/P3不变。editor继续跳过。完整收据、源码hash和浏览器证据见 [attachment-r4/evidence.json](web-parity/attachment-r4/evidence.json)。


### Attachment 第五轮：整卡焦点环、悬停与独立 Trigger（2026-10-30，尚未关闭）

固定源码的 Nova 根要求 focus-within:ring-1/ring-ring/50，Base 根在有直属按钮/链接时 hover:bg-muted/50。当前 onClick 适配原先只可点击，没有这两种整卡视觉反馈。本轮 Nova 根通过真实后代焦点事件驱动1dp/50%外环，包含指针聚焦及非交互卡片内的动作聚焦，不套用按钮的 focus-visible 规则，也不改原边框颜色/厚度。根继续由 Style 管理形状和交互，采用已有 cssStyleable 外阴影 knockout，让焦点环不会渗入半透明 hover 背景；调用方末尾 Style 仍能覆盖。onClick 根使用同一个 clickable InteractionSource 的 hovered 状态，静态卡片不增加 hover 色。

独立 BoxScope.AttachmentTrigger 原文档写 z-10 而实现缺少 zIndex，本轮补10f。三项新交互测试覆盖：浅→深→浅、Touch/Keyboard模式下后代焦点的实际外环像素、布局不变及移出最后子控件后环消失；Trigger 与父边界相同、无论插入顺序都盖住z0内容、z20动作仍独立点击；Enter/Space以及enabled=false/disabled=true实时切换不会误触。覆盖层的测试动作明确置于兄弟z20，不能推断任意嵌套Box会自动修复层级。未新增公共参数或再升级版本。API说明补充名称、可见文件名/状态、禁用和自定义覆盖层的责任。

最终 **92项通过**（core70、catalog22），测试 XML 包含加强的离开后代焦点断言；六张既有 Attachment Nova/Spinner/共享ScrollFade显式verify均unchanged，没有重录。JS编译、Wasm development distribution通过。保留既有Text.kt INVISIBLE_REFERENCE、AGP实验项、Gradle deprecation及webpack警告。旧Vega六张像素问题延续第四轮，未借本轮92项普通测试掩盖像素失败。


浏览器实际捕获官网16张＋本地16张，在1440×1000、390×844浅深色中分别检查无焦点、hover、Remove动作键盘焦点、整卡预览焦点。四张最终contact均已查看。官网DOM记录确认root的hover为muted/50、后代focus-within环为1px；官方MDX使用Rhea，其实际环透明度为30%，本库按Nova源码50%，以及Rhea18px/Nova14dp圆角、动作圆角差异，均保留条件说明，不据截图直接改成Rhea。浅色本地内部背景实测255→247、深色23→24，退出hover后恢复；JVM直接验证1dp外环及完全失焦后消失。

第一批官方手机卡片底部被焦点定位滚出视口，本地手机深色也保留了上一次整卡焦点；最终脚本先把焦点移到卡片外，再重新定位章节，源码端检查卡片完整在视口内，重新捕获并覆盖初版帧。Canvas焦点不能由DOM activeElement证明；本地焦点结论来自实际键盘输入、已查看的环/动作外观和JVM焦点断言。官网原生Tab顺序为Copy→Remove→Preview，本地onClick适配为Preview→Copy→Remove；本轮没有硬改或宣称二者Tab顺序完全相同。

仍有真实缺口：根hover的结束颜色已接通，但Base的transition-colors渐变时序尚未实现；独立Box overlay仍不会自动登记到旁边Attachment的hover条件，只有onClick根的hover接线已关闭。完整原生读屏映射、复杂自定义槽位、字重/图标、旧Vega六张像素问题及共享P1/P3继续保留；静态卡片子动作的整体焦点反馈和独立Trigger的基础输入/层级已验证，不再笼统记为完全未验收。Attachment仍未勾选，其他组件及已完成项的复查目标不变，editor跳过。证据为 [attachment-r5/evidence.json](web-parity/attachment-r5/evidence.json)。

补充未关闭项：本次浏览器相同位置的浅色hover灰度取样为官网246、本地247，深色均24，尚不宣称像素相同；官网独立 Features/Accessibility 章节及其导航仍未完整迁入，本轮 API 标签说明不能代替这些章节。


### Attachment 第六轮：Features/Accessibility 正文、目录与 Copy Page（2026-10-30，尚未关闭）

关闭第五轮新增的章节缺口。Features放在Composition之后、Image之前；Accessibility放在Trigger之后、API之前，含Label icon-only actions、Label the trigger、Keyboard scrolling、Meaning beyond color四个三级标题。内容按真实Kotlin API改写：label/contentDescription、LocalUriHandler、默认可聚焦Group与描述性错误状态，不伪称Compose渲染原生HTML按钮。图标代码显式按LocalContentColor着色，URI使用目录已有的真实图片地址。去掉官网相应位置没有的额外Keyboard/Error代码块；Root API恢复简短容器说明，避免新指南与API重复堆叠。

目录模型新增beforeExamples/afterExamples正文列表，块类型只有本轮需要的段落、项目列表和代码。页面渲染、目录锚点和Copy Page消费同一份数据；现有文档默认空列表，未改组件发布API或再提升版本。新增导航使用features/accessibility/label-icon-only-actions/label-the-trigger/keyboard-scrolling/meaning-beyond-color可读片段，四项子标题在目录缩进。

按官网DOM实测修正新正文排版：桌面/手机列表缩进22.5/28.35px加项目内边距6/7.56px，项目间隔7.5/9.45px，列表前18.75/23.625px；段落在标题后15/18.9px，代码前17.5px。项目符号使用主题mutedForeground，小圆点对齐首行，文本独立换行，并给列表提供CollectionInfo。测量源见official-list-styles.json及official-browser.json。其余共享章节外边距、标题定位/页面骨架、字体与代码高亮差异仍归P1/P3，未宣称整页逐像素一致。

新增两项实际目录测试：导出和目录顺序、六个片段、代码/指导内容；以及真实Attachment页面的首次片段加载、Copy Page、全部六个目录链接、标题链接和屏幕内可见性。首次失败来自JVM默认场景尺寸小于requiredSize，产生负屏幕坐标；改为调整真实测试scene到1440×900，并加强横纵边界断言，不放宽到“节点存在”。最终ComponentDocumentation7、CatalogNavigation6、AttachmentExamples6、CatalogPreview5，共 **24项通过**；JS编译和Wasm development distribution通过。编译保留既有AGP/Gradle/Text.kt/webpack警告。本轮未修改core运行时或重录像素基准，不能把第五轮六张verify说成本轮重跑。


最终浏览器矩阵为官网24张＋本地24张，六个新章节在1440×1000/390×844及浅深色逐节打开；12张并排contact均查看，最终精简Root API后重捕本地矩阵，并复看受影响的API邻接画面。另实际点击桌面Features、Accessibility、Keyboard scrolling三个目录，检查URL更新和滚动稳定后截图，三张均查看；JVM补足全部六项目录及标题链接、Copy Page。正文Kotlin名称、代码行数和说明长度与React版本的差异是明确适配，不通过硬编码章节高度掩盖。尚未关闭原生读屏行为，增加Accessibility说明不等于组件辅助技术已验收。

Shared P1/P3仍包含章节定位、正文/代码高亮、API表三列与官网四列的结构差异，以及手机长类型的视口裁切/横向可达性规则待复核。Attachment其他运行时缺口与旧Vega六张像素问题延续第五轮；本轮仅关闭Features/Accessibility正文缺失及其导航/导出，Attachment未勾选。全部组件复查、已完成项复查及editor跳过范围不变。证据与hash见 [attachment-r6/evidence.json](web-parity/attachment-r6/evidence.json)。


### Attachment 第七轮：当前 FileSearch 路径与真实标题排版（2026-10-30，尚未关闭）

读取官网Trigger媒体里的实际SVG：放大镜位于纸张左侧，当前icons-lucide-cmp2.2.1的FileSearch仍是旧版右侧设计。目录使用同一24单位viewBox、stroke2、round cap/join路径构建新ImageVector（circle转为等价弧段），只替换此例，不给core引入图标依赖或更换整套图标包。Trigger展示代码同时包含实际矢量构造，并从同一组路径字符串生成，补足相关imports，不再用旧Lucide.FileSearch调用伪装示例代码。上游ISC/MIT许可位于composeResources/files/licenses/lucide-icons.txt；原始SVG和来源测量在attachment-r7。

新增实际Trigger媒体像素测试，使用官网DOM里的SVG在原生浏览器DPR2下绘制16/24px，16px浅深参考用于测试；24px只作诊断资产。测试渲染真实目录例子，并以安装的旧图标作反例，同一6%平均RGB误差阈值接受新图标、拒绝旧图标。浅色新/旧误差为2.875%/13.941%，深色为1.987%/12.120%；不宣称不同渲染器AA逐像素相同。首次取样失败含夹具错误：clickable语义截图为382×56内容框，不含外边框，正确图标偏移为20而非21px；已修正取样位置，未放宽容差。临时诊断PNG写入与println已移除，实际图和收据保留。

标题没有随观感任意改为600。官网实测14px、fontWeight500、lineHeight17.5px，使用Geist1.800变量字体；本地400/500/600/700文件同为1.800，浏览器确实加载Medium，OS/2字重标记正确。初始把hmtx字宽总和152.04px当作排版宽度的断言不成立：官网真实Canvas含字偶距宽148.008163px、DOM Range148.015625px，本地真实TextLayoutResult为147.99402px，在0.1px容差内，字重/字号断言也通过。hmtx原值仍作为未排版诊断保留，已加说明。

字体文件存在hint表差异（本地有fpgm/prep/cvt，官网无），轮廓指令结构也不能直接按原始hash判等；该证据不等于已经隔离字形观感差异的唯一原因。此例的字重、字号、排版字宽不再记为已确认不匹配；残余字形像素观感仍留全站P3，不改公共字体资源。完整原始字体文件仅在临时目录分析，仓库保存来源/hash/元数据与实际排版结果。

最终AttachmentExamples8、ComponentDocumentation7、CatalogNavigation6、CatalogPreview5，共 **26项通过**；JS编译和Wasm development distribution通过。既有AGP实验项、Gradle deprecation、Text.kt与webpack警告保留。未改core运行时，未重录或重跑其六张像素基准；第五轮基准证据延续，旧Vega六张差异也未关闭。新的两张16px参考是原生SVG测试输入，不是Roborazzi基准重录。


浏览器最终官网16张＋本地16张，覆盖1440×1000/390×844、浅深色及rest/hover/动作焦点/整卡焦点，四张最终contact均查看。对照图改为整数原图裁切：之前直接用含.5/.25px的DOM矩形做裁切会额外插值，使官网字形看起来更糊；历史第五轮contact不改写，本轮不以这类二次插值作为字重证据。原始截图保留实际页面自身的亚像素布局与AA。

另分别测量官网CSS颜色和本地浏览器实际墨色：标题/媒体在浅色均为RGB10，深色均250；媒体背景均245/38。原生SVG几何参考显式归一到JVM默认fixture色（浅色stroke23），与实际目录主题的10区别记录在validation.json，不能把几何参考当作官网整张配色截图。五个Geist字体资源在最终浏览器实际加载，来源与Wasm资源记录见browser-build-resources.json。

本轮关闭FileSearch轮廓缺口，并确认Trigger标题字重/字号/实际排版宽度及墨色与实测相符；残余字形抗锯齿、原生亚像素布局仍归共享P3。Nova/Rhea根圆角与焦点环、15px共享视口差异等条件延续前轮；独立overlay hover、颜色过渡、复杂槽位/原生辅助技术和旧Vega差异仍开放。Attachment未勾选，全站和已完成组件复查范围不变，editor继续跳过。完整证据为 [attachment-r7/evidence.json](web-parity/attachment-r7/evidence.json)。


### Attachment 暂缓项（2026-10-30，按用户要求转入 Avatar）

不再阻塞后续组件。待统一处理：旧 Vega 六张像素基准差异、字形栅格观感、自定义 overlay 与外层 hover 联动、完整 transition-colors（含中断反转时序）、复杂槽位及极端约束、原生辅助技术。共享页面 P1/P3 差异沿用已有记录。

第八轮已有代码将交互卡 hover 背景改为 150ms FastOutSlowInEasing；浅深色真实指针进入/退出/反转定向测试已通过，但完整颜色属性、浏览器四象限及 JS/Wasm 构建尚未验收，不能视为本组件完成。保留改动供后续一起复核，证据见 `web-parity/attachment-r8`。


### Avatar 首轮（2026-10-30）

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


### Breadcrumb 首轮及本地提交检查点（2026-10-30）

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
