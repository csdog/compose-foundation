# 独立复审 · 2026-11-09

范围：当前工作区全部组件。历史完成标记仅作输入，不视为本轮验收。保留所有既有修改；不提交、不推送、不重置。

验收维度：公开 API 与行为、示例与文案、有效回归测试、适用主题/RTL/尺寸、构建与浏览器证据时效。通用浏览器冒烟通过不等同于组件交互或视觉一致。

启动基线见 baseline.json（HEAD、已跟踪改动哈希及原阶段状态）。

|组件|历史标记|本轮复审|
|---|---|---|
|Accordion (`accordion`)|已勾选|已修复独立禁用 Trigger 键盘跳转；RED→GREEN、22 核心测试通过；浏览器/金图待验|
|Alert (`alert`)|已勾选|实际源码/示例/测试/历史图证复审未发现缺陷；历史 20 项通过；当前共享壳层待验|
|Alert Dialog (`alert-dialog`)|已勾选|实际实现/依赖/示例/图证复审未发现缺陷；历史 21 项通过；当前构建/浏览器待验|
|Aspect Ratio (`aspect-ratio`)|已勾选|实际源码/示例/测试/图证复审未发现缺陷；历史 11 项通过；共享页间距待验|
|Attachment (`attachment`)|已勾选|已修复简介、边框状态过渡及示例导入；28 定向测试通过；共享改动/浏览器待总验|
|Avatar (`avatar`)|已勾选|已修复混合边框及彩色示例代码；9 行为+5 示例+10 截图通过；浏览器待验|
|Badge (`badge`)|已勾选|已修复 interactionSource API 兼容缺陷；18 核心+8 示例测试通过；浏览器待验|
|Breadcrumb (`breadcrumb`)|已勾选|已修复折叠/自定义示例目标链接；RED→GREEN、6 示例测试通过；浏览器待验|
|Bubble (`bubble`)|已勾选|已补固定来源的两段简介；9 示例测试通过；实时主题/RTL 及浏览器待验|
|Button (`button`)|已勾选|实际源码/15 示例/测试/图证复审未发现缺陷；历史 66 项通过；当前总验待补|
|Button Group (`button-group`)|已勾选|已修复 API 文档、Voice 状态及完整 RTL 示例；40 示例测试/自身源码及测试 lint 通过；遗漏已补齐，浏览器待验|
|Calendar (`calendar`)|未勾选|已修复多月下拉及错误去边框；28 定向测试含 10 截图验证通过；浏览器待验|
|Card (`card`)|已勾选|已修复引导段落位置及复制代码间距/对齐；24 示例测试通过；浏览器待验|
|Carousel (`carousel`)|已勾选|实际源码/示例/测试复审未见行为缺陷；已修复自身 lint；旧交互图证不足，当前交互待验|
|Code Block (`code-block`)|已勾选|已修复布局/颜色/RTL/代码/下载；74 测试、JS/Wasm、所属 lint 通过；一处旧暗色焦点金图及浏览器待验|
|Trace (`trace`)|已勾选|已修复流式/回放、悬停/错误箭头及文档结构；45 测试/6 金图通过；最终示例重验已通过，浏览器待验|
|Chart (`chart`)|未勾选|静态复审未发现运行时缺陷；历史证据已核实；当前验收待补|
|Checkbox (`checkbox`)|未勾选|已修复点击热区、响应式示例边距及 API 链接；31 测试/自身 lint 通过；2 旧暗色金图残差|
|Chip (`chip`)|已勾选|实际源码复审未见运行缺陷；纠正 Badge/disabled 文案；当前交互待验|
|Collapsible (`collapsible`)|已勾选|已补 4 个输入无障碍标签及组合说明；9 示例测试/自身 lint 通过；浏览器待验|
|Combobox (`combobox`)|未勾选|已修复 4 个键盘/disabled 缺陷及组合说明；4 核心+12 示例通过，所属 lint 清洁；浏览器待验|
|Command (`command`)|已勾选|已修复查询后键盘顺序、复制代码/内容及 RTL；10 示例+4 核心通过，所属 lint 清洁；浏览器待验|
|Context Menu (`context-menu`)|未勾选|已修复 RTL 状态、长菜单滚动及暗色危险项；91 共享测试通过；浏览器待验|
|Date Picker (`date-picker`)|已勾选|已修复输入/键盘、精确日期/宽度及源码示例；16 测试/所属 lint 通过；浏览器待验|
|Dialog (`dialog`)|已勾选|已修复手机页脚、RTL 编辑/关闭及滚动等 6 处；19 定向测试通过；文档 lint 重验已清洁，浏览器待补|
|Drawer (`drawer`)|已勾选|已修复 RTL、不可关闭崩溃及扩展示例；49 核心/最终 16 定向通过；Nova 外观/滑动/嵌套已修复，62+最终 21 检查及 4 金图通过；浏览器待验|
|Dropdown Menu (`dropdown-menu`)|未勾选|已修复四类行 textValue 搜索；RED→GREEN；共享 85 测试通过；浏览器待验|
|Empty (`empty`)|未勾选|独立实现复审通过；修正文案等价性声明；浏览器/构建等残留待验|
|Field (`field`)|未勾选|已修复 ColumnScope 布局合同及错误声明；32 测试/核心 lint 通过；404 旧源证据已更正留档|
|Hover Card (`hover-card`)|未勾选|已修复触发器卸载/过期回调及指南；47 测试/自身 lint 通过；碰撞动画待集成、全局 Escape 有平台限制|
|Input (`input`)|未勾选|已修复标签焦点/文件选择/Autofocus 及复制代码；18 测试、JS/Wasm、所属 lint 通过；浏览器待验|
|Input Group (`input-group`)|未勾选|已修复复制/布局/RTL/焦点环等；87 定向/所属 lint 通过；固定源码样式已补，浏览器待验|
|Input OTP (`input-otp`)|未勾选|已修复表单尺寸/字体、无效组环及文档；稳定联合 78 测试/自身 lint 通过；手机表单浏览器待验|
|Item (`item`)|未勾选|已修复头尾 8dp 间距及文档顺序/内容；39 测试/所属 lint 通过；浏览器待验|
|Kbd (`kbd`)|已勾选|已补组合说明/RTL 链接；8 示例测试、所属 lint 通过；Tooltip 上下文问题已转交|
|Label (`label`)|未勾选|已修复 RTL/希伯来语/激活及示例说明；9 测试、所属 lint 通过；浏览器待验|
|Marker (`marker`)|未勾选|已补简介/表格/无障碍结构并纠正 HTML 指引；18 测试、所属 lint 通过；浏览器待验|
|Menubar (`menubar`)|未勾选|已修复键盘聚焦、嵌套 Escape、Nova 面板宽度；共享 85 测试及定向截图验证通过；浏览器待验|
|Message (`message`)|未勾选|已修复 Rhea 字体/元数据/对齐及文档；38 测试、所属 lint 通过；浏览器待验|
|Message Scroller (`message-scroller`)|已勾选|已修复坐标反馈竞态、重置/回放及文档；94 测试、所属 lint 通过；浏览器待验|
|Navigation Menu (`navigation-menu`)|未勾选|已修复方向键/首项焦点、Delay 及复制代码；25 核心+22 示例、所属 lint 通过；浏览器待验|
|Pagination (`pagination`)|已勾选|已补组合/Next.js 适用背景/RTL 变更指引；24 示例、所属 lint 通过；浏览器待验|
|Popover (`popover`)|未勾选|已修复 Escape/嵌套/初始焦点及组合说明；44 测试、所属 lint 通过；动作动画待集成|
|Progress (`progress`)|未勾选|已修复组合顺序、等宽数字及复制代码导入；50 测试/最终 16 重验、所属 lint 通过；浏览器待验|
|Questionnaire (`questionnaire`)|未勾选|已修复状态/默认值/焦点/反馈/验证及 Dialog；96+受影响 21/1 测试、2 金图及所属 lint 通过；预览对齐待集成|
|Radio Group (`radio-group`)|未勾选|已修复单 Tab 停靠/重排导航及示例；38 测试、所属 lint 已通过；浏览器待验|
|Resizable (`resizable`)|已勾选|已修复拖动越界回退数学及说明/表格；21 测试、5 所属 lint 清洁；实际拖动浏览器待验|
|Scroll Area (`scroll-area`)|已勾选|已纠正水平 API/RTL 链接/组合说明；16 示例、所属 lint 通过；浏览器待验|
|Scroll Fade (`scroll-fade`)|已勾选|已修复 7 个复制示例越界滚动崩溃及使用/回退说明；34 示例、所属 lint 通过；浏览器待验|
|Select (`select`)|未勾选|已修复开/关闭键入搜索、焦点颜色/顺序/组间距及示例；20 核心+最终 8/10 重验、所属 lint 通过；箭头/外环待集成|
|Separator (`separator`)|已勾选|已补官方 API 链接；38 示例、所属 lint 通过；浏览器待验|
|Sheet (`sheet`)|未勾选|已修复正文颜色继承/实时主题及 API 声明，纠正固定源码令牌指引；67 测试、所属 lint 通过；浏览器待验|
|Shimmer (`shimmer`)|未勾选|已按固定 CSS 修复 RTL+reverse 方向、指南及实际减弱动画说明；88 测试、所属 lint 通过；浏览器待验|
|Sidebar (`sidebar`)|未勾选|已修复 2 处布局缺陷；53 项定向测试通过；浏览器待验|
|Skeleton (`skeleton`)|已勾选|已实测并修复 390/375 手机线宽钳制；8 新几何+40 示例、所属 lint 通过；Wasm 浏览器待确认|
|Slider (`slider`)|未勾选|已修复根/拇指离散无障碍步数；19 测试、所属 lint 通过；浏览器/平台待验|
|Spinner (`spinner`)|已勾选|已补按钮/徽章图标位置说明及 RTL 链接；16 示例、所属 lint 通过；浏览器待验|
|Stepper (`stepper`)|已勾选|已修复独立步骤重组/增删及空操作；7 当前行为/示例、所属 lint 通过；最终截图/浏览器待验|
|Switch (`switch`)|已勾选|待复审|
|Table (`table`)|已勾选|已修复 span 权重优先级及 RTL 物理对齐；20 核心+28 示例测试通过；浏览器待验|
|Tabs (`tabs`)|已勾选|待复审|
|Text (`text`)|已勾选|待复审|
|Textarea (`textarea`)|已勾选|待复审|
|Toast (`toast`)|未勾选|待复审|
|Toggle (`toggle`)|未勾选|已修复 API 兼容、Nova 透明/按压及阴影缺陷；18 项定向测试通过；浏览器/截图金图待验|
|Toggle Group (`toggle-group`)|未勾选|待复审|
|Tooltip (`tooltip`)|已勾选|待复审|

## 首批进展

- Sidebar：修复 ParentData 返回错误接收者、普通兄弟节点与多个 Inset 宽度分配。两个几何回归先失败后通过，53 项 Sidebar 测试通过；尚未作浏览器验收。
- 通用浏览器验证：移除个人依赖路径，补 TOC fragment 检查、源码/构建产物指纹与准确覆盖边界；6 项 Node 测试通过，尚未运行浏览器。
- 证据完整性初筛：检查最新编号目录内 614 对显式路径/哈希，420 对直接匹配，132 份官方源经归档 basename + SHA-256 匹配。60 份 upstream 引用尚未解析，不直接判为丢失。另有 Chip 历史构建产物一个路径已不存在、一个已变化，反映构建目录可变，不能据此称历史取证伪造。该扫描仅确认字节完整性，不证明功能、截图内容或源码与产物对应关系。
- `startup-findings.md` 记录阶段文字/状态对账问题。

Chart 独立复审：93 个历史 wildcard 测试结果中 89 个为 Chart、4 个为 Create charts；12 份官方来源与四张截图哈希匹配。r6 45/45 为来源+页面冒烟检查，不能替代交互/视觉验收；旧交接 source pin 与实际 manifest 不一致。详见 `.agent-runs/review-chart/progress.md`。

Empty 已获得实际独立实现复审：修正把 Compose 换行策略说成 CSS 等价行为的声明，聚焦验证通过，保留套件含 27 项；新增冗余 forwarding 测试已移除。完整浏览器/构建、换行视觉等价性和公共签名发布处理仍未关闭，详见 `.agent-runs/review-empty/progress.md`。

Empty 兼容性复核：恢复五个原公开签名，新 lineBreak 重载要求显式参数；28 项定向测试通过。见 `.agent-runs/review-empty-compatibility/progress.md`。CSS 换行视觉等价性仍未证明。
