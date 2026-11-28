# SwiftUI 与 Compose 组件逐项核对

核对基准是本仓库 `/r/registry.json` 的 69 个条目，以及 `shadcn/shared` 中对应的 Compose 实现。三份 registry 使用相同的条目名。下表记录 SwiftUI 源码具有的行为，以及与 Compose 版有实质区别的能力。Apple 原生控件的样式、键盘行为和系统弹层适配由运行系统决定。

本轮使用 shadcn CLI 4.23.0 从本地 HTTP 安装三个目录各自的全部条目，并核对各目录的名称和安装文件。已安装的 Swift Package 通过 macOS 构建，并逐项通过 iOS 14 模拟器目标类型检查。在 macOS 和 iOS 26.2 的 iPhone 17 Pro 模拟器上，各渲染 69 个条目的深浅两种外观，共 276 张非空截图。

在 Pixel 模拟器上打开成品 APK 的安装页，确认 Jetpack Compose 和 SwiftUI 命令可见。验证了状态栏在深浅色下的前景与背景，以及键盘打开时文档搜索输入框仍保持可见和焦点。

Web 兼容分发在 Chromium 的 Wasm 路径和 JS 回退路径均显示原生安装选项。实际点击复制按钮核对了两端命令。Date Picker 的 Copy Page 同时包含 Calendar 和 Popover。两项 JVM 文档回归测试通过。

## shadcn 视觉对齐范围

SwiftUI registry 当前有 69 个条目。基准仓库为其中 63 个条目提供 shadcn 截图。chip、emoji-text、icon、stepper、text、theme 缺少直接参考截图，现有原生样例继续用各自功能验收。各条目的视觉审核按变体、状态和主题记录。[shadcn 基准索引](https://github.com/csdog/compose-foundation-baselines/blob/b19fcbf62f182422a5f26d7b8ce3dd8006695858/shadcn/Index.md)

### Tabs 和 Button 初始态

- Tabs 使用 `tabs-disabled.tsx`。Home 为选中项，Disabled 保持禁用，方向键仍跳过禁用项。Web 的 Disabled 焦点环示例与这一规则不同，后续键盘视觉对照使用可聚焦项。
- Button 使用现有 `button-variants-initial` 参考，覆盖描边文字按钮和向上箭头图标按钮。两个控件均保留 44 pt 高的点按范围，图标按钮的宽度也至少为 44 pt。
- 比对使用 402 × 520 共用画布，每个用例分别采集浅色和深色。

iPhone 17 Pro（iOS 26.2）上两轮各四张原图逐文件一致。复验确认默认深色 Tabs 的选中填充为 `#2f2f2f`。shadcn 和 SwiftUI 在文字抗锯齿及轮廓边缘仍有像素差异。macOS Swift Package 构建通过。[验证运行](https://github.com/csdog/compose-foundation/actions/runs/45993072773) · [四张原图及差异](https://github.com/csdog/compose-foundation/actions/runs/45993072773/artifacts/15194918019)

### Badge 和 Separator 初始态

Badge 对照官方 Nova 示例的 Default、Secondary、Destructive、Outline 整行。Separator 对照标题、分隔线和下方两行正文。需要逻辑像素对照时，从上游原图分别以 x=159、x=6 裁取 402 × 520 区域，完整保留所有可见内容。

iPhone 17 Pro（iOS 26.2）上浅色和深色各拍两遍。四张原图逐文件一致。差异主要集中在文字边缘、圆角抗锯齿及正文基线。macOS Swift Package 构建通过。[验证运行](https://github.com/csdog/compose-foundation/actions/runs/45994534289) · [四张实拍及 shadcn 对照](https://github.com/csdog/compose-foundation/actions/runs/45994534289/artifacts/15196185208)

### Kbd、Skeleton 和 Spinner 初始态

Kbd 覆盖四个修饰键与 Ctrl + B 两行。Skeleton 覆盖头像和两条文本占位。Spinner 覆盖静止帧以及完整的支付状态行。各用例使用 402 × 520 的共用画布，浅色和深色均以 iPhone 17 Pro（iOS 26.2）复拍。六张新图逐张一致，集成分支此前的 32 张图也保持逐字节一致。macOS Swift Package 构建通过。符号字体与抗锯齿仍有平台差异，Spinner 动画需单独验收。[Review 报告](https://github.com/csdog/compose-foundation/actions/runs/45998642997/artifacts/15197407696)

### Progress 默认示例

官方示例在 500 ms 后从 13% 更新到 66%，归档参考图为更新后的状态。SwiftUI Nova 对照完整的 403.2 pt 宽进度条，在 iPhone 17 Pro Max（iOS 26.2）用 440 × 520 共用画布采集。深浅色各拍两遍，PNG 逐字节一致。原有 38 张 iPhone 17 Pro 截图与上一轮逐字节一致。圆角边缘及轨道配色有像素差异。macOS Swift Package 构建通过。进度变化过程及 RTL 待单独验收。[Review 报告](https://github.com/csdog/compose-foundation/actions/runs/46000170133/artifacts/15198731356)

### Switch 关闭、开启和禁用态

对照 Nova 的 `switch-demo.tsx` 和 `switch-disabled.tsx`，每张图保留完整轨道、滑块及标签。最终在 iPhone 17 Pro（iOS 26.2）以 402 × 520 画布采集，浅色和深色各三个用例，六张 PNG 与各自复拍逐字节一致。禁用轨道采样点与上游相差一个灰阶，滑块中心采样值一致。字体光栅化与控件轮廓仍有细微像素差异。[最终 Switch Review](https://github.com/csdog/compose-foundation/actions/runs/46006892307/artifacts/15202264774)

整批 Review 中，原有的 40 张实拍与上一轮逐字节一致。修订禁用态后的四张开启和关闭截图也与整批 Review 一致。最终 macOS Swift Package 构建和 Tabs 导航回归通过。实际触控、硬件键盘、VoiceOver、RTL 和动效仍待单独验收。[整批 Review](https://github.com/csdog/compose-foundation/actions/runs/46005110796) · [最终验证](https://github.com/csdog/compose-foundation/actions/runs/46006892307)

### Checkbox 默认、勾选、基础和禁用态

Nova 的完整四行示例保留两段说明和通知卡片。首项勾选前后只有该选框区域改变。另拍单独的基础和禁用示例。默认示例裁切 shadcn 参考图时以 x=12 开始，独立示例以 x=159 开始，均完整保留内容。

iPhone 17 Pro（iOS 26.2）上以 402 × 520 画布复拍深浅色八张 PNG，逐字节一致。原有 46 张图也与上一轮一致。两端文字光栅化和控件边缘仍有细微差异。macOS Swift Package 构建和 Tabs 导航回归通过。Checkbox 的三态转换断言通过。部分选中态视觉、真实触控、硬件键盘和 VoiceOver 仍待验收。[完整 Review 报告](https://github.com/csdog/compose-foundation/actions/runs/46009522214/artifacts/15202868116)

### Radio Group 选中和禁用态

对照 Nova 的 `radio-group-demo.tsx` 和 `radio-group-disabled.tsx`。三组完整用例覆盖 Comfortable 选中、Compact 选中及首项禁用。iOS 默认样例的选项中心相隔 46 pt，上游为 24 CSS px。该差异来自 44 pt 最小行高与 2 pt 行间距。禁用示例的标签间距也有差异。

iPhone 17 Pro（iOS 26.2）上深浅色共六张截图，复拍逐字节一致。切换 Comfortable 和 Compact 的预设选择只改变第二、第三项的圆形控件区域。原有 54 张截图与上一轮逐字节一致。macOS Swift Package 构建和 Tabs 导航回归通过。方向键导航、实际触控和 VoiceOver 待验收。[完整 Review 报告](https://github.com/csdog/compose-foundation/actions/runs/46011436975/artifacts/15204561793)

#### Radio Group 方向键与焦点

iOS 17 和 macOS 14 起，Radio Group 默认只保留当前有效选择的 Tab 停靠点。无有效选择时焦点进入首个可用选项，绑定仍保持原值，按 Space 才选择该项。方向键移动焦点并提交新选项，跳过禁用项，首尾循环。左右键跟随界面方向，上下键始终按项目顺序移动。Nova 在当前聚焦的圆形控件外绘制焦点环。较早系统继续使用原有按钮交互。[shadcn 禁用示例](https://ui.shadcn.com/examples/base/radio-group-disabled) · [Base UI Radio Group](https://base-ui.com/react/components/radio-group)

实测 shadcn 的禁用示例从第二项按上键，跳过禁用的第一项并循环到第三项。官方 RTL 预览的视觉方向为 RTL，左右键实测仍沿 LTR 索引。SwiftUI 使用 `layoutDirection` 映射左右方向，与 Base UI 的方向上下文约定一致。[shadcn RTL 示例](https://ui.shadcn.com/examples/base/radio-group-rtl)

iPhone 17 Pro（iOS 26.2）复拍默认、Compact 选中和首项禁用的六张深浅色截图，逐字节一致，静态画面与上轮像素一致。[截图与 shadcn 对照](https://github.com/csdog/compose-foundation/actions/runs/46119665206/artifacts/15253064945)

macOS 实际窗口的 LTR、RTL 回归覆盖初始无选择、Space 选择、方向键跳过禁用项、循环、Tab 离开、反向 Tab 返回及整组禁用。测试核对每次有效选择只写入绑定一次。[macOS 按键运行](https://github.com/csdog/compose-foundation/actions/runs/46120905530/job/140905026114)

iPad mini（A17 Pro，iOS 26.2）通过 LTR、RTL 的 USB 键盘和触控回归。方向键跳过禁用项并循环，点按禁用项保留选择，点按有效项更新选择。实拍显示焦点环环绕单选控件，RTL 下随控件移至文字右侧。[iPad 键盘和触控日志](https://github.com/csdog/compose-foundation/actions/runs/46121823678/job/140907836094) · [焦点截图](https://github.com/csdog/compose-foundation/actions/runs/46121823678/artifacts/15253544269)

### Input 和 Textarea 初始态

Input 覆盖完整的 API Key 字段和独立输入框。API Key 示例使用系统 SecureField。Textarea 覆盖独立编辑器和含标题说明的编辑器。iPad mini（A17 Pro，iPadOS 26.2）使用 720 × 520 画布，完整保留 672 pt 宽的输入框及边框。输入与占位文字为 16 pt，外部标签和说明为 14 pt。

深浅色共八张新图，复拍逐字节一致。原有 60 张 iPhone 图也与上一轮一致。浅色占位文字的可见边界与参考图最多差 1 px。Textarea 使用原生滚动编辑器，上游网页有拖拽调整尺寸的手柄。macOS Swift Package 构建和 Tabs 导航回归通过。实际输入、安全遮蔽、错误和焦点态、VoiceOver 及窄屏布局仍待验收。[完整 Review 报告](https://github.com/csdog/compose-foundation/actions/runs/46016882459/artifacts/15207916190)

### Input OTP 初始与禁用态

Nova 示例覆盖六位填满、四位空白及三加三禁用。每张图保留完整格组、数字和适用的分隔符。单个验证码绑定负责原生输入，同组使用 32 pt 高的连续格。使用 `groupBreaks: [3]` 在第四格前加入分隔符。本地 Swift 断言通过，覆盖重复和越界位置、分组排序、非数字过滤、Unicode 数字归一化和长度截断。

iPhone 17 Pro（iOS 26.2）使用 402 × 520 画布采集深浅色六张新图，复拍逐字节一致，原有 68 张图与上一轮逐字节一致。整体可见边界与参考最多相差 2 px。macOS Swift Package 构建和 Tabs 导航回归通过。实际输入、粘贴、系统自动填充、焦点态和 VoiceOver 待单独验收。[完整 Review 报告](https://github.com/csdog/compose-foundation/actions/runs/46019689625/artifacts/15209185259)

### Slider 默认、步进、禁用和 RTL

Nova 单值 Slider 的完整 320 pt 轨道对照 75、76、禁用 50 和 RTL 75。轨道高 4 pt，滑块直径 12 pt，控件交互区域高 44 pt。shadcn 参考图从 x=159 裁成 402 × 520，完整保留轨道和滑块。

iPhone 17 Pro（iOS 26.2）深浅色共八张新图，复拍逐字节一致。原有 74 张图与上一轮逐字节一致。75 到 76 的滑块移动实测为 3 pt。RTL 轨道从右向左填充。上游 RTL 参考的滑块中心位于填充起点左侧约 12 CSS px，SwiftUI 的滑块中心与填充起点重合。[完整 Review 报告](https://github.com/csdog/compose-foundation/actions/runs/46022866497/artifacts/15212334824)

macOS 窗口通过 AppKit 事件验证两种方向的点击、按住拖动、四个方向键、Home/End 和禁用阻止绑定写入。数值断言覆盖范围夹取、步长取整和极小步长。iOS 实际触控、硬件键盘和 VoiceOver 待单独验收。[macOS 回归日志](https://github.com/csdog/compose-foundation/actions/runs/46022866497/job/140588878349)

### Toggle 书签和 Toggle Group 格式按钮

Toggle 对照完整描边书签的关闭和选中态。Toggle Group 对照 Bold、Italic、Underline 三个描边按钮，覆盖无选中和 Bold 选中。选中 Bold 后，像素变化局限于该按钮。Nova 的这两组控件保留至少 44 × 44 pt 的操作区域。

Toggle Group 的纵向截图对应 [shadcn Base UI Nova 纵向示例](https://github.com/shadcn-ui/ui/blob/0e5b2e621a9ee4cf6a604bbe6c5c0d5b70561eb4/apps/v4/examples/base/toggle-group-vertical.tsx)，初始按下 Bold 和 Italic。浏览器参考使用 402 × 520 视口，三个按钮均为 36 × 32 CSS px，纵向间距为 4 CSS px。SwiftUI 保留 44 pt 的最小触控高度。横向初始和 Bold 选中的四张 iPhone 截图与 #17 审核实拍逐字节一致。iPhone 17 Pro 的横向初始、Bold 选中和纵向初始共六张图，均与复拍逐字节一致。用此次运行锁定的 shadcn 子模块提交补齐了六个用例的上游对照。[横向截图](https://github.com/csdog/compose-foundation/actions/runs/46125826564/artifacts/15255799024) · [六张 iPhone 截图及复拍](https://github.com/csdog/compose-foundation/actions/runs/46127910706/artifacts/15257996626)

macOS 和 iPad mini（iOS 26.2，普通键盘模式）验证横向 LTR/RTL 和纵向键盘。方向键跳过禁用项、循环移动焦点，Space 切换按下状态，Tab 进入一次并离开。按下状态不会随方向键移动。macOS 还验证 Home、End、Return、Shift+Tab 和鼠标；iPad 触控验证禁用项不动作、可用项能切换。焦点截图中 Bold 仍按下，系统焦点环已移到 Underline。通过 Component parity 的手动 Review 选择 `component=toggle-group` 和 `platform=swiftui-ios` 复跑 iPad HID 用例。VoiceOver 待验。[macOS 日志](https://github.com/csdog/compose-foundation/actions/runs/46126047434/job/140920837857) · [iPad 焦点截图](https://github.com/csdog/compose-foundation/actions/runs/46126047434/artifacts/15255904282)

iPhone 17 Pro（iOS 26.2）深浅色共八张新图，与复拍逐字节一致。原有 82 张图也与上一轮一致，外边界与 shadcn 参考相差最多 2 px。macOS 窗口通过 AppKit 鼠标事件验证单按钮切换、单选替换、多选保留、最后一项必选及单项和整组禁用。Toggle 单按钮的 iOS 触控及硬件键盘、实际 VoiceOver 仍待验收。Toggle Group 的后续 iPad 测试见上文。[完整 Review 报告](https://github.com/csdog/compose-foundation/actions/runs/46028364880/artifacts/15214950117) · [macOS 回归日志](https://github.com/csdog/compose-foundation/actions/runs/46028364880/job/140605804826)

### Button Group 三种尺寸和纵向布局

对照 Nova 的 `button-group-size.tsx` 和 `button-group-orientation.tsx`。横向图保留 Small、Default、Large 三排共十二个按钮，纵向图保留完整的加减组。相邻按钮共用一道接缝，只有整组的外角保留圆角。SwiftUI 的组内图标按钮宽度至少 44 pt，纵向组每项高 44 pt。上游纵向示例每项为 32 CSS px。

iPhone 17 Pro（iOS 26.2）以 402 × 520 画布采集四张深浅色图，复拍逐字节一致。包含独立 Button 在内的原有 90 张图与上一轮逐字节一致。macOS 窗口回归分别点按横向和纵向接缝两侧，验证只触发对应按钮，且禁用 Next 后 Back 仍可操作。iOS 实际触控、键盘、VoiceOver、RTL 和混合子控件的布局仍待验收。[完整 Review 报告](https://github.com/csdog/compose-foundation/actions/runs/46031332393/artifacts/15214938905) · [macOS 回归日志](https://github.com/csdog/compose-foundation/actions/runs/46031332393/job/140615958625)

### Label 复选框和 Field 输入示例

对照 Nova 的 `label-demo.tsx`、`field-input.tsx` 和 `field-textarea.tsx`。Label 用例保留复选框和完整条款文字。Field 用例保留用户名、密码及两条说明，以及独立的反馈编辑器。密码说明在控件上方，其他说明在控件下方。用户名和密码组的尾部在浅色图中比参考低约 20 pt，输入控件保留 44 pt 的原生操作高度。

iPhone 17 Pro（iOS 26.2）的六张深浅色截图与复拍逐字节一致，原有 94 张图也与上一轮逐字节一致。macOS 实际窗口通过 AppKit 鼠标事件验证点击标签和复选框会更新同一绑定，禁用时点击不写入。iOS 触控、键盘、VoiceOver 和独立标签的焦点关联待验收。[完整 Review 报告](https://github.com/csdog/compose-foundation/actions/runs/46034351670/artifacts/15218125719) · [macOS 回归日志](https://github.com/csdog/compose-foundation/actions/runs/46034351670/job/140625952242)

### Alert 成功与错误、Card 登录与小尺寸

对照 shadcn Base UI / Nova 的 `alert-basic.tsx`、`alert-destructive.tsx`、`card-demo.tsx` 和 `card-small.tsx`。Alert 保留完整标题、说明及状态图标。可见外边界与参考相差最多 1 pt。两行说明的换行位置和原生 SF Symbols 图标仍有差异。Card 保留完整登录表单和小尺寸报告卡的操作区。以浅暗两种主题抽样，输入框、底部描边按钮和底部背景与 shadcn 的六处填色一致。登录卡和小尺寸卡的底部仍分别低 45–46 pt 和 32–33 pt，原生输入控件和按钮保留 44 pt 操作高度。

Alert 在 iPad mini（iPadOS 26.2）以 720 × 520 画布容纳 448 pt 宽的参考布局。Card 在 iPhone 17 Pro（iOS 26.2）以 402 × 520 画布采集。两种主题共八张新图，复拍逐字节一致，原有 100 张图也与上一轮逐字节一致。macOS 实际窗口通过 AppKit 鼠标事件验证卡片正文和底部的控件可以独立操作，且禁用底部按钮后正文仍能响应。Alert 操作区和 RTL、Card 边缘布局、iOS 触控、硬件键盘及 VoiceOver 待单独验收。[完整 Review 报告](https://github.com/csdog/compose-foundation/actions/runs/46039710034/artifacts/15220152607) · [macOS 回归日志](https://github.com/csdog/compose-foundation/actions/runs/46039710034/job/140644135876)

### Accordion 默认、禁用、多开与 RTL

对照 shadcn Base UI / Nova 默认示例的配送及退货展开状态，以及 `accordion-disabled.tsx`、`accordion-multiple.tsx` 和 `accordion-rtl.tsx`。Nova 的整行标题可以操作，使用 14 pt 字号、末尾方向箭头和项目之间的分隔线。标题提供按钮和标题语义，并报告已展开或已收起。保留原有 `expanded` 绑定和 `allowsMultiple` 参数。

iPad mini（iPadOS 26.2）以 720 × 520 画布采集十张新截图，每张都与复拍逐字节一致，上一轮的 108 张也未变化。英文示例保留 44 pt 操作区，末行比参考低 6–8 pt。阿拉伯文参考图仍使用左侧文字和右侧箭头，SwiftUI 会依照系统布局方向把文字排在右侧、箭头排在左侧。[全量截图报告](https://github.com/csdog/compose-foundation/actions/runs/46042561688/artifacts/15221309670)

移除重复的禁用淡化后，最终 iPad Review 十张复拍逐字节一致。与全量截图相比，两种主题只有禁用标题行的像素发生变化。灰阶字形取样与 shadcn 相差 4–5 个灰阶值，修正前相差 57–62。[最终 Accordion Review](https://github.com/csdog/compose-foundation/actions/runs/46045617662/artifacts/15222572073)

macOS 实际窗口通过 AppKit 点击验证整行命中、单开时替换原面板、多开时独立关闭、单项与整组禁用，以及每次有效操作只写一次绑定。iOS 实际触控、键盘、VoiceOver 和动态过程待单独验收。[macOS 回归日志](https://github.com/csdog/compose-foundation/actions/runs/46045617662/job/140664093584)

日常 iOS 回归仍报告 `popover-default-initial-dark.png` 与已发布原生基准有 3,107 个像素不同。父 PR #20 的同一用例也是 3,107，Accordion 新用例因等待发布基准而标为待验。[日常 iOS 回归](https://github.com/csdog/compose-foundation/actions/runs/46042899384/job/140654967723) · [父 PR 的回归](https://github.com/csdog/compose-foundation/actions/runs/46042184550/job/140652502470)

### Collapsible 订单详情

对照 shadcn Base UI / Nova 的 `collapsible-demo.tsx`。标题右侧的图标按钮独立切换展开状态，订单状态始终可见，配送地址和商品信息只在展开时装入布局。`FoundationCollapsibleTrigger(isExpanded: $expanded)` 接收自定义标签，可用 `buttonStyle` 设置外观，图标按钮需要提供可访问名称。`FoundationCollapsibleContent(isExpanded: expanded)` 放在详情应出现的位置。原有带字符串标题的 `FoundationCollapsible` 继续使用系统 DisclosureGroup。

iPhone 17 Pro（iOS 26.2）以 402 × 520 画布采集收起、展开的四张深浅色图，复拍逐字节一致。两端画布背景颜色一致。原生图保留 44 pt 的触控区域，两种状态的内容下沿均比参考低 13–14 pt。shadcn 展开图在触发按钮上保留了鼠标悬停底色，iOS 截图为静止触屏状态。[Collapsible 截图报告](https://github.com/csdog/compose-foundation/actions/runs/46046834807/artifacts/15222433755)

macOS 实际窗口回归已验证开关各写入绑定一次、关闭的面板无法接收点击、展开始终可由外部状态控制，以及禁用触发按钮时已展开的内容仍可操作。iOS 触控、键盘、VoiceOver、RTL 和其余布局待单独验收。[macOS 回归日志](https://github.com/csdog/compose-foundation/actions/runs/46046834807/job/140668144649)

### Empty 默认与虚线边框

对照 shadcn Base UI / Nova 的 `empty-demo.tsx` 和 `empty-outline.tsx`。默认示例保留新建、导入和帮助入口，边框示例保留上传操作。Nova 使用 32 pt 的装饰图标区域、14 pt 的标题与说明，以及独立的操作区。标题有原生无障碍 heading 语义，图标和虚线边框从无障碍树隐藏。传入 `variant: .outline` 可选择边框，原有带或不带 `actions` 的初始化方式仍可使用。

iPad mini（iPadOS 26.2）的四张深浅色图在 720 × 520 画布上复拍逐字节一致。图标底色取样与 shadcn 一致，浅色为 `#f5f5f5`，深色为 `#262626`。虚线边框的左右边界与参考相差最多 1 pt。SwiftUI 说明使用自然换行，保留原生 SF Symbols 和 44 pt 按钮操作高度。默认样例末行低 12–13 pt，边框样例下沿低 4–5 pt。[最终 Review 报告](https://github.com/csdog/compose-foundation/actions/runs/46048600511/artifacts/15222394857)

macOS Swift Package 构建和 AppKit 点击回归通过。虚线边框内两个按钮可分别操作，禁用其中一个时另一个仍能响应。iOS 触控、硬件键盘、VoiceOver、RTL 和动态字体待单独验收。[macOS 回归日志](https://github.com/csdog/compose-foundation/actions/runs/46048600511/job/140674068424)

### Breadcrumb 省略菜单与分隔符

对照 shadcn Base UI / Nova 的 `breadcrumb-demo.tsx`、`breadcrumb-ellipsis.tsx` 和 `breadcrumb-separator.tsx`。保留完整 Home、Components 和当前页路径。使用 `Entry.ellipsis(id:title:items:)` 可放入原生菜单，传入空列表时显示静态省略标记。`separatorSymbol` 和 `separatorSize` 可替换分隔符图形与字号。分隔符不进入无障碍树，末项保留静态文本并报告当前页。原有 `FoundationBreadcrumb([Entry])` 调用仍可用。

iPhone 17 Pro（iOS 26.2）的六张深浅色图以 402 × 520 画布复拍，逐字节一致。三组布局的完整路径均可见。Nova 链接和菜单触发器保留至少 44 pt 的操作区域。默认样例的末项右沿比 shadcn 远 24–25 pt，静态省略与圆点分隔样例的末项右沿远 8–9 pt。两端的圆点和箭头形状仍有差异。[Breadcrumb 截图报告](https://github.com/csdog/compose-foundation/actions/runs/46049871562/artifacts/15224985937)

macOS Swift Package 构建与 AppKit 点击回归通过。Home 和 Components 可独立触发，点分隔符不触发动作，末项即使带有回调也保持静态。菜单打开和选择、iOS 实际触控、硬件键盘、VoiceOver 及 RTL 待单独验收。[macOS 回归日志](https://github.com/csdog/compose-foundation/actions/runs/46049871562/job/140678364299)

### Avatar 首字母、尺寸、徽标与头像组

对照 shadcn Base UI / Nova 的 `avatar-demo.tsx`、`avatar-size.tsx`、`avatar-badge-icon.tsx` 和 `avatar-group-count.tsx`。归档参考图呈现图片未加载时的首字母，模拟器使用相同内容。头像尺寸为 24、32 和 40 pt，带状态徽标的头像可为辅助技术提供对应状态文字。头像组按 8 pt 重叠，并提供 `+N` 计数。调用者可自定义回退文字，原有 `FoundationAvatar(_:image:)` 继续支持本地 `Image` 和自动首字母。

iPhone 17 Pro（iOS 26.2）以 402 × 520 画布复拍八张深浅色图，全部逐字节一致。四个示例的外边界与参考图相差最多 2 pt。绿色状态徽标中心的 sRGB 取样在两种主题均一致。字号光栅化、加号和环形描边边缘仍有平台差异。[最终截图报告](https://github.com/csdog/compose-foundation/actions/runs/46051515412/artifacts/15224872953)

异步 URL 初始化器使用系统 `AsyncImage`，适用于 iOS 15 和 macOS 12 起的版本。在 macOS 窗口分别验证本地图像、环回 HTTP 200 图像和 HTTP 404 后保留回退。iOS 实际远程加载、VoiceOver、RTL 和动态字体仍待单独验收。[macOS 验证](https://github.com/csdog/compose-foundation/actions/runs/46051515412/job/140683808277)

### Item 默认、表面样式与尺寸

对照 shadcn Base UI / Nova 的 `item-demo.tsx`、`item-variant.tsx` 和 `item-size.tsx`。默认示例保留单独操作以及整行可操作的验证提示，样式示例覆盖透明、描边和弱化背景。尺寸示例覆盖常规、小号和超小号。常规与小号的内边距在上游 Nova 中相同，超小号缩小内边距和说明字号。保留原有标题、说明、媒体和操作初始化器，显式 `variant` 可选择新表面样式。

iPad mini（iPadOS 26.2）以 720 × 520 画布复拍六张深浅色图，逐字节一致。校准文字行高后，三组示例的外边界与参考最多相差 2 pt。弱化背景的取样颜色一致，浅色为 `#fafafa`，深色为 `#181818`。原生 SF Symbols 和字体光栅化仍有平台差异。[最终截图报告](https://github.com/csdog/compose-foundation/actions/runs/46053822322/artifacts/15225853235)

macOS Swift Package 构建和 AppKit 点击回归通过。同一行的两个操作分别触发，切换 RTL 后命中区域随布局镜像，禁用一个操作时另一个仍可点击。iOS 实际触控、键盘、VoiceOver、动态字体和其他媒体组合待单独验收。[macOS 回归日志](https://github.com/csdog/compose-foundation/actions/runs/46053822322/job/140691488790)

### Aspect Ratio 横向、方形和竖向裁剪

对照 shadcn Base UI / Nova 的 `aspect-ratio-demo.tsx`、`aspect-ratio-square.tsx` 和 `aspect-ratio-portrait.tsx`。示例以 16:9、1:1、9:16 布局分别裁剪居中的正方形内容，使用本地生成的渐变复现几何和亮度。`FoundationAspectRatio` 保留原有初始化器、父容器尺寸提议和无效比例回退。超出可见矩形的子视图不能接收点击。

iPhone 17 Pro Max（iOS 26.2）在 440 × 520 画布上复拍六张深浅色图，逐字节一致。三种比例的可见边界与参考最多相差 2 pt，中央颜色取样最多相差 1 个 RGB 通道值。程序生成的渐变在亮度分布和圆角抗锯齿上仍有像素差异。[截图报告](https://github.com/csdog/compose-foundation/actions/runs/46055187678/artifacts/15226197760)

macOS 原生渲染测得 160 × 90、160 × 160 和 90 × 160 三种尺寸。零、负数、无穷大和 NaN 都回退到 80 × 80。实际窗口通过 AppKit 点击验证，可见区内的超尺寸按钮仍可操作，横向与纵向裁剪区外的点击交给下层按钮。iOS 实际触控及 RTL 参考示例待单独验收。[macOS 回归日志](https://github.com/csdog/compose-foundation/actions/runs/46055187678/job/140695915025)

### Scroll Area 纵向列表、横向照片与 RTL

对照 shadcn Base UI / Nova 的 `scroll-area-demo.tsx`、`scroll-area-horizontal-demo.tsx` 和 `scroll-area-rtl.tsx`。英文和阿拉伯文标签列表使用 192 × 288 pt 视口，横向照片使用 384 × 458 pt 视口。照片沿用仓库内已有的三张资源。`showsBorder: true` 启用主题边框、圆角裁剪和 1 pt 视口内边距。滚动轴、指示器和无障碍操作继续由原生 ScrollView 提供。

iPhone 17 Pro Max（iOS 26.2）以 440 × 520 画布复拍六张深浅色图，逐字节一致。三组视口可见边缘与 shadcn 参考最多相差 1 pt。RTL 标题和标签在右侧对齐，横向视口裁剪第二张照片。首张照片内部的平均 RGB 通道差约为 4/255。Web 默认示例的初始图可见滚动条，iOS 静止截图由系统隐藏指示器。[截图报告](https://github.com/csdog/compose-foundation/actions/runs/46056329426/artifacts/15227071393)

macOS 通过原生 NSScrollView 接收滚轮事件，验证纵向和横向分别沿指定轴滚动，正交轴偏移不变。iOS 实际拖动、硬件键盘、VoiceOver 和滚动指示器的拖动待单独验收。[macOS 回归日志](https://github.com/csdog/compose-foundation/actions/runs/46056329426/job/140699684536)

### Pagination 文字按钮、纯页码和 RTL

对照 shadcn Base UI / Nova 的 `pagination-demo.tsx`、`pagination-simple.tsx` 和 `pagination-rtl.tsx`。原有初始化器继续提供图标控制和包含首末页的标准窗口。新增控制可显示自定义前后页文字或仅显示页码。紧凑窗口显示连续的最多三个页码，在有隐藏页的一侧显示省略号。Nova 当前页使用描边和选中语义，前后控制在翻页边界禁用，阿拉伯文示例使用本地化数字与 RTL 方向图标。

iPhone 17 Pro（iOS 26.2）以 402 × 520 画布复拍六张深浅色图，逐字节一致。RTL 使用 `ar@numbers=arab`，数字、按钮顺序和箭头方向与示例一致。原生按钮保留 44 pt 的操作区域，默认、纯页码和 RTL 的可见内容分别比参考宽 28–30、48–50 和 22–24 pt。[最终截图报告](https://github.com/csdog/compose-foundation/actions/runs/46058083580/artifacts/15227653258)

macOS 原生窗口的点击回归验证首页和末页的禁用按钮不写入绑定、直接选页、逐页翻页及 RTL 镜像。iOS 实际触控、硬件键盘、VoiceOver 和窄屏滚动仍待验收。[macOS 回归日志](https://github.com/csdog/compose-foundation/actions/runs/46058083580/job/140705454124)

### Hover Card 收起、展开与指针切换

对照 shadcn Base UI / Nova 的 `hover-card-demo.tsx`，包含完整的触发器、账号、两行简介和加入时间。Nova 复用锚定弹层，使用 256 pt 默认宽度、4 pt 间距和对齐偏移。指针悬停打开时保留原窗口焦点，移入卡片会取消延迟关闭。长按入口和非 Nova 原生系统弹层仍可用。新增 `isPresented` 绑定用于调用方控制展开。

iPhone 17 Pro（iOS 26.2）使用 402 × 520 画布复拍收起、展开的四张深浅色图，逐字节一致。展开面板取样颜色与参考一致。SwiftUI 布局保留 44 pt 的原生触发区域，面板顶缘比参考低约 6 pt。Web 展开图带有鼠标悬停下划线，SwiftUI 图通过受控状态打开。[最终截图报告](https://github.com/csdog/compose-foundation/actions/runs/46060257085/artifacts/15228428014)

macOS 在实际窗口内调用组件悬停回调，验证过早离开会取消打开、触发器到卡片的切换会取消关闭、离开卡片后关闭，以及父窗口一直保持为 key window。系统指针轨迹在 CI 未得到验证。既有 Popover 的 Escape 关闭与触发器焦点回归通过。共用弹层改动后的 24 张 iOS Popover 截图与改动前逐字节一致。iOS 实际长按、VoiceOver 和 RTL 待另行验收。[macOS 回归日志](https://github.com/csdog/compose-foundation/actions/runs/46060257085/job/140712584204) · [Popover 截图回归](https://github.com/csdog/compose-foundation/actions/runs/46059382583/artifacts/15227918198)

### Input Group 行内与上下附加区域

对照 shadcn Base UI / Nova 的 `input-group-demo.tsx`、`input-group-block-start.tsx` 和 `input-group-block-end.tsx`。示例保留搜索图标和结果计数、单行输入、带标题的代码编辑器及带计数与发布按钮的评论编辑器。原有横向初始化器继续可用，`axis: .vertical` 将首尾附加区域放在编辑器上下。组级 `isFocused` 和 `isInvalid` 可由调用方的原生焦点或校验状态驱动，输入和附加按钮保留各自的原生控件语义。

iPhone 17 Pro Max（iOS 26.2）以 440 × 520 画布复拍六张深浅色图，逐字节一致。横向组边界与参考最多相差 2 pt，填色取样在两种主题一致，搜索占位文字的灰阶取样与参考相差 2–4。保留 44 pt 操作区后，上附加区域和下附加区域示例的底部分别低 36–37 pt 和 28–29 pt。[最终截图报告](https://github.com/csdog/compose-foundation/actions/runs/46062487554/artifacts/15229552092)

macOS 实际窗口回归以 AppKit 点击和按键验证编辑器获得焦点、组边框外出现焦点环、按键更新原生输入绑定、附加按钮独立运行以及移出焦点后焦点环消失。iOS 实际输入、键盘、VoiceOver、RTL 和错误样式仍待进一步验收。[macOS 回归日志](https://github.com/csdog/compose-foundation/actions/runs/46062487554/job/140719888964)

### Table 发票、页脚和窄窗口

对照 shadcn Base UI / Nova 的 `table-demo.tsx` 和 `table-footer.tsx`。完整示例包含七行发票，紧凑示例展示前三行，两张表都带合计和说明文字。Nova 使用无外框表头、行分隔线、浅色填充页脚和底部居中说明。原初始化器与列定义仍可用。新增的可选页脚复用列的值提取逻辑，始终位于排序后的数据行下方。

iPad mini（A17 Pro，iOS 26.2）以 720 × 520 画布拍摄完整发票和紧凑页脚的深浅色图。四张图复拍逐字节一致。表头、行间分隔线和合计栏的纵向位置与参考一致，两种主题的页面及页脚内部填色取样一致。差异集中在系统字体、分隔线灰阶和抗锯齿。[截图与 shadcn 差异报告](https://github.com/csdog/compose-foundation/actions/runs/46064358988/artifacts/15230840455)

macOS 真实窗口回归验证点击表头分别按升序和降序排列、合计栏固定在数据行后，以及窄窗口可以横向滚动。SwiftUI 包构建与 Tabs 导航回归通过。iOS 的 VoiceOver 和实际触控滚动仍待验收。[macOS 回归日志](https://github.com/csdog/compose-foundation/actions/runs/46064358988/job/140726083322)

### Table RTL 与行操作

另外对照 `table-rtl.tsx` 和 `table-actions.tsx`。阿拉伯语发票通过 SwiftUI 的布局方向镜像列次序，金额沿参考图的物理右侧对齐。商品表示例使用原生行菜单，含 Edit、Duplicate 和 Delete。新增 `Column` 的 `cell` 渲染闭包，调用方可传入菜单、按钮或格式化视图。正文行将操作控件保留为独立的无障碍子元素，页脚使用列的文本值。

iPad mini（A17 Pro，iOS 26.2）复拍四个变体的八张深浅色图，全部逐字节一致。完整发票与紧凑页脚的四张图也与上一轮逐字节一致。RTL 首行金额和发票编号的文字右缘与参考相差 1 pt，商品行菜单图标向左约 6 pt，对应原生 44 pt 触控区域。[截图与差异报告](https://github.com/csdog/compose-foundation/actions/runs/46065612262/artifacts/15231470214)

macOS 真实窗口中，排序前后点击同一位置的按钮，分别执行 Zulu 和 Alpha 的操作。升降序、页脚位置、横向滚动及 Tabs 回归通过。iOS 菜单展开、选择和 VoiceOver 待实机交互验收。[macOS 回归日志](https://github.com/csdog/compose-foundation/actions/runs/46065612262/job/140730127707)

### Tooltip 样式、悬停与关闭

对照 shadcn Base UI / Nova 的 `tooltip-demo.tsx`，保留 Hover 按钮和 Add to library 提示。Nova 使用反色圆角气泡、指向触发器的箭头和可配置的悬停延迟。原初始化器继续可用。iOS 保留长按和原生无障碍提示，macOS 的非 Nova 原有调用沿用系统帮助。调用方也可以绑定显示状态，例如在已有键盘焦点状态变化时显示提示。

iPhone 17 Pro（iOS 26.2）使用 402 × 520 共同比较区域，收起与展开的四张深浅色截图复拍逐字节一致。SwiftUI 从按钮的 44 pt 操作区下缘放置气泡，气泡比参考低约 6 pt。Web 展开图带鼠标悬停填色，iOS 图通过绑定打开并保留按钮的静态外观。[截图与差异报告](https://github.com/csdog/compose-foundation/actions/runs/46067416555/artifacts/15232095750)

macOS 实际窗口调用悬停回调，验证配置的延迟、提前移出取消、已打开后移出关闭和禁用关闭。气泡显示在按钮下方，窗口保持 key 状态。SwiftUI 包构建与 Tabs 导航回归通过。系统指针路径、iOS 实际长按、Escape 和 VoiceOver 待单独验收。[macOS 回归日志](https://github.com/csdog/compose-foundation/actions/runs/46067416555/job/140736147725)

### Alert Dialog 居中面板与确认操作

对照 shadcn Base UI / Nova 的 `alert-dialog-demo.tsx`，保留 Show Dialog、标题、说明及 Cancel、Continue。通过新增 `size:` 初始化器，调用方可以在填满呈现区域的容器上使用带遮罩和页脚的面板。常规尺寸随可用宽度切换横排和叠放操作，紧凑尺寸采用并排操作。原初始化器继续呈现系统确认框。

iPad mini（A17 Pro，iOS 26.2）以 720 × 520 画布拍摄关闭和打开的四张深浅色图，复拍逐字节一致。两种主题的面板和页脚内部填色取样与参考一致，遮罩背景每通道相差 1，面板边缘相差约 1–3 pt。背景触发器使用模糊，页脚保留 44 pt 原生操作区域。[截图与差异报告](https://github.com/csdog/compose-foundation/actions/runs/46069335630/artifacts/15232243007)

macOS 实际窗口验证从触发器打开、点击遮罩不透传、初始 Return 执行 Cancel、Escape 只取消一次，重新打开后点击 Continue 只执行一次确认。SwiftUI 包构建与 Tabs 导航回归通过。关闭后的触发器焦点恢复、iOS 实际触控和 VoiceOver 待验收。[macOS 回归日志](https://github.com/csdog/compose-foundation/actions/runs/46069335630/job/140742295708)

### Alert Dialog 紧凑面板与媒体

对照 `alert-dialog-small.tsx`、`alert-dialog-media.tsx` 和 `alert-dialog-small-media.tsx`。媒体插槽采用 40 pt 方形背景。宽版面板把媒体放在文字前，紧凑版放在标题上方，保留并排等宽的取消和确认操作。现有系统确认框和无媒体面板初始化器继续可用。

三个新增案例的 shadcn 展开参考通过官方托管示例采集，六张图复拍逐字节一致。参考截图、索引和采集记录在基准仓库单独审核。[上游参考 PR](https://github.com/csdog/compose-foundation-baselines/pull/9)

iPad mini（A17 Pro，iOS 26.2）采集四个案例在收起和展开时的 16 张深浅色图，复拍逐字节一致。默认案例的四张图也与上一轮逐字节一致。三个新增面板的边缘与参考相差 1–4 pt，按钮布局一致。SwiftUI 的 Share Project 媒体使用系统圆形加号，shadcn 参考使用渐隐圆形加号。使用参考 PR 的图本地重生成了 16 个用例的 shadcn 差异图。[iOS 截图附件](https://github.com/csdog/compose-foundation/actions/runs/46070366785/artifacts/15233256919)

macOS 实际窗口复跑遮罩点击、初始 Return 取消、Escape 和单次确认。RTL、iOS 触控、触发器焦点恢复和 VoiceOver 待验收。[macOS 回归日志](https://github.com/csdog/compose-foundation/actions/runs/46070366785/job/140745676384)

### Alert Dialog 阿拉伯语和关闭后的焦点

对照 `alert-dialog-rtl.tsx`。两个触发器右对齐，原生 RTL 布局镜像常规面板的文字和操作顺序，紧凑面板使用居中蓝牙图标及并排操作。对应的四张 shadcn 展开参考通过官方示例采集，复拍逐字节一致。[上游参考 PR](https://github.com/csdog/compose-foundation-baselines/pull/10)

面板的 `.onDismiss { ... }` 在关闭并重新启用背景后回调，调用方可以把键盘焦点归还给实际触发器。确认和取消仍各自使用原回调，外部绑定关闭只报告 dismiss。

iPad mini（A17 Pro，iOS 26.2）以 720 × 520 画布采集 22 张深浅色图，复拍逐字节一致。此前 16 张图也与上一轮逐字节一致。RTL 两种展开图的确认按钮都在取消按钮左侧。紧凑版阿拉伯语说明在 SwiftUI 折成两行，参考为一行，面板上下边缘各相差约 11–13 pt。使用参考 PR 的图生成了所有 22 个用例的 shadcn 差异图。[iOS 截图附件](https://github.com/csdog/compose-foundation/actions/runs/46071762772/artifacts/15232789578)

macOS 实际窗口验证初始 Return 执行取消、Escape 关闭、确认以及外部绑定关闭。每次关闭后，触发器重新收到用于打开面板的键盘事件。回归还通过了遮罩拦截及确认和取消回调次数检查。iOS 触控、iOS 键盘焦点恢复和 VoiceOver 待验收。[macOS 回归日志](https://github.com/csdog/compose-foundation/actions/runs/46071762772/job/140750197594)

### Drawer 底部面板与固定页脚

对照 shadcn 的 `drawer-demo.tsx`。该发布示例使用 Base UI / Rhea，配送卡片保留五个时段、默认选择、确认和取消。新增带说明文字的初始化器将关闭操作放在固定页脚，内容超出时单独滚动。Rhea 使用内缩圆角面板，Nova 使用贴底面板。原初始化器继续提供原有布局。两种布局复用关闭焦点、向下拖动阈值和减少动态效果设置。

iPad mini（A17 Pro，iOS 26.2）以 720 × 520 画布拍摄开合的四张深浅色图，复拍逐字节一致。Rhea 面板纵向边界为 y=88–511 pt，与参考一致。浅色遮罩取样相同，深色每通道相差 1。原生页脚保留 44 pt 操作区，第三行的说明需要滚动后完整显示。[截图与差异报告](https://github.com/csdog/compose-foundation/actions/runs/46073531801/artifacts/15233608259)

macOS 实际窗口复跑原版 Drawer、Nova Drawer、Rhea Drawer、Dialog 和 Sheet。五种面板均通过初始 Return、Escape、单次回调和触发器焦点归还。Drawer 回调在关闭及背景更新后执行，触发器可再次从键盘打开面板。新版布局的触控拖动和 VoiceOver 待单独验收。[macOS 回归日志](https://github.com/csdog/compose-foundation/actions/runs/46073531801/job/140755923260)

### Dialog 居中面板、表单与固定页脚

对照 shadcn Base UI / Nova 的 `dialog-demo.tsx`。编辑资料示例使用 `FoundationField` 和 `FoundationInput`，保留已有 Name、Username、Cancel 和 Save changes 内容。`presentation: .centered` 在填满呈现区域的容器上启用遮罩、居中面板和可选固定页脚。内容超出时单独滚动，原初始化器继续走系统 sheet。居中面板初始关闭焦点复用已有的呈现组件逻辑。

iPad mini（A17 Pro，iOS 26.2）以 720 × 520 画布拍摄开合的四张深浅色图，复拍逐字节一致。两种主题的面板和页脚填色取样与参考一致。SwiftUI 的两个输入框和页脚按钮保留 44 pt 操作区，面板比参考高约 37–39 pt。Web 展开图显示 Name 输入框焦点环，SwiftUI 初始焦点放在关闭按钮。[截图与差异报告](https://github.com/csdog/compose-foundation/actions/runs/46075322227/artifacts/15235260515)

macOS 实际窗口验证原生表单输入、遮罩关闭、Return 和 Escape，并检查每次关闭后触发器恢复焦点。关闭回调在面板退出和输入控件释放焦点后执行。原系统 Dialog、三种 Drawer、Sheet 和 Tabs 的回归通过。iOS 触控、键盘焦点归还及 VoiceOver 待独立验收。[macOS 回归日志](https://github.com/csdog/compose-foundation/actions/runs/46075322227/job/140762633510)

### Sheet 方位、表单与固定页脚

对照 shadcn Base UI / Nova 的 `sheet-demo.tsx`。编辑资料示例保留 Name、Username、Save changes 和 Close。新增 `side:` 入口在占满呈现区域的容器上沿逻辑边显示面板，支持 `top`、`bottom`、`leading`、`trailing`。侧边内容可以滚动，页脚固定。原有初始化器继续使用系统 sheet。新面板复用呈现组件的关闭按钮焦点逻辑。

iPad mini（A17 Pro，iOS 26.2）以 720 × 520 画布采集开合的四张深浅色图，复拍逐字节一致。侧边面板的分界线在 x=336 pt，两种主题的面板填色取样与参考一致，遮罩背景每通道相差 1。SwiftUI 保留 44 pt 输入和按钮操作区。Web 展开图显示 Name 焦点环，SwiftUI 初始焦点位于关闭按钮。[截图与差异报告](https://github.com/csdog/compose-foundation/actions/runs/46078074006/artifacts/15234969638)

macOS 实际窗口验证四个逻辑边的初始关闭焦点、Return、Escape、单次回调和触发器焦点归还。右侧面板另验收输入表单后点击遮罩，先释放 macOS 文本编辑器焦点，再在关闭后归还焦点。系统 Sheet、居中和系统 Dialog、Drawer 及 Tabs 回归通过。[macOS 回归日志](https://github.com/csdog/compose-foundation/actions/runs/46078074006/job/140771605885)

### Sheet 上、下、左面板与 RTL

对照 shadcn 的 `sheet-side.tsx` 和 `sheet-rtl.tsx`。上、下示例将面板最高设为半屏。新增 `.maximumPanelHeight(...)` 可限制边缘面板高度，超出的正文单独滚动，操作保持在页脚。阿拉伯语示例保留左侧触发器，面板由末尾逻辑边在 RTL 环境中映射到物理左侧。

四张 shadcn 下方与左侧展开参考通过官方示例采集，复拍逐字节一致。[参考图 PR](https://github.com/csdog/compose-foundation-baselines/pull/11)

iPad mini（A17 Pro，iOS 26.2）以 720 × 520 画布采集 16 张深浅色图，复拍逐字节一致。四张默认参考用例也与上一轮逐字节一致。上、下示例面板高度取样为 259–260 pt，与对应 shadcn 图一致。RTL 的面板和字段标签镜像，英文输入值保持原生靠左对齐，Web 参考靠右。用待审核参考图重生成全部 16 项 shadcn 差异。[iOS 截图附件](https://github.com/csdog/compose-foundation/actions/runs/46080011895/artifacts/15236292196)

macOS 实际窗口验证 RTL 与四个逻辑边的 Return、Escape、单次关闭和焦点归还。右侧 Sheet 与居中 Dialog 在输入文字后点击遮罩，均释放了 macOS 原生字段编辑器并归还焦点。原版 Sheet、Drawer 和 Tabs 的回归通过。iOS 实际触控和 VoiceOver 待验收。[macOS 回归日志](https://github.com/csdog/compose-foundation/actions/runs/46080011895/job/140776652762)

完整回归复现了右侧与 RTL Sheet 在编辑后点遮罩关闭时的焦点丢失。macOS 14 起用退场动画的实际完成事件执行点遮罩后的关闭回调。重新打开时取消尚未执行的回调。复验从原触发器发键重新打开，再按 Escape 关闭。完整 macOS 回归通过，覆盖四个逻辑边、RTL、居中 Dialog 和 Drawer。iPad 上 16 张深浅色截图复拍逐字节一致，也与修复前逐字节一致。[失败日志](https://github.com/csdog/compose-foundation/actions/runs/46101050028/job/140843386330) · [完整 macOS 回归](https://github.com/csdog/compose-foundation/actions/runs/46101896143/job/140846025466) · [iOS 截图回归](https://github.com/csdog/compose-foundation/actions/runs/46101725059/artifacts/15245871546)

### Input 阿拉伯语布局和光标编辑

Nova 的 RTL 单行输入在 iOS 26 和 macOS 26 上使用系统布局对齐策略，英文值可沿界面的起始边放置。TextField、SecureField、文本绑定和无障碍标签继续由 SwiftUI 提供。旧 SDK 与早期系统沿用原有对齐策略，部署目标仍为 iOS 14、macOS 11。[Apple 对齐策略文档](https://developer.apple.com/documentation/swiftui/view/multilinetextalignment(strategy:))

iPad mini（A17 Pro，iOS 26.2）的 16 张 Sheet 图复拍逐字节一致。两张 RTL 展开图的英文值从 x=29 移到字段右侧，字形右缘同为 x=355；其余 14 张与上一轮逐字节一致。[Sheet 截图与差异报告](https://github.com/csdog/compose-foundation/actions/runs/46080927172/artifacts/15237217758)

独立 Input 用例的六张深浅色图复拍逐字节一致，四张原英文图与此前一致。阿拉伯语密码占位文字与参考图的横向边界最多相差 1 pt。SwiftUI 将占位串显示为 `sk-...`，浏览器显示为 `...-sk`，两端短横线和省略号采用各自的双向排版结果。[Input 截图与差异报告](https://github.com/csdog/compose-foundation/actions/runs/46081746716/artifacts/15237403782)

macOS 实际窗口中点击 RTL Sheet 的 `FoundationInput`，从 `Pedro` 末尾向左移动光标后输入 `a`，绑定值为 `Pedrao`。点击遮罩关闭后焦点归还，其他呈现组件的回归通过。该 macOS runner 使用系统原有对齐策略，iOS 截图用于验收新策略。[macOS 回归日志](https://github.com/csdog/compose-foundation/actions/runs/46080927172/job/140779538561)

### Calendar Nova 单月、当天和月份年份标题

对照 shadcn Base UI / Nova 的 `calendar-demo.tsx`、`calendar-basic.tsx` 和 `calendar-caption.tsx`。截图固定为参考中的 2026 年 11 月 27 日。Nova 使用日历边框、英文短星期名、选中日填色和当天标记，显示首尾周的相邻月份日期。邻月补位只用于提示当前网格的日期排列。现有选区绑定、月份年份原生菜单和翻页边界继续使用原有逻辑。

iPad mini（A17 Pro，iOS 26.2）以 720 × 520 采集默认选中、基础月历和月份年份标题的深浅色截图。6 张候选图复拍逐字节一致。日历卡片的逻辑尺寸约为 324 × 320 pt，行高保留 44 pt 触控区。shadcn 参考卡片约为 214 × 261 CSS px。实际图和参考图的画布底色一致，浅色为 `#FFFFFF`，深色为 `#0A0A0A`。审计应用以 iOS 14 模拟器部署目标编译。[iOS 截图和对照报告](https://github.com/csdog/compose-foundation/actions/runs/46083621121/artifacts/15238467407)

macOS 实际窗口中点击禁用日期，绑定保持原值。有效日期更新绑定，翻到有效月份后可继续选日期，到达日期上限所在月份后停止翻页。状态回归同时覆盖跨月选区、月份年份导航、周起始日和首尾周。完整 SwiftUI 包在 macOS 构建通过。[macOS 回归日志](https://github.com/csdog/compose-foundation/actions/runs/46083621121/job/140788138648)

#### Calendar 月份和年份菜单

Nova 的月份、年份触发器采用 6 pt 间距和末尾指示图标，iOS 保留至少 44 pt 高的触控区。选项由原生单选菜单呈现，当前月份和年份对应各菜单的选择值。切换菜单只移动可见月份，日期区间绑定及边界沿用原有规则。样式以基准仓库的官方预览截图为准。固定源码的 Nova CSS 声明了触发器边框，已采集的官方预览使用无边框外观。[固定源码](https://github.com/shadcn-ui/ui/blob/0e5b2e621a9ee4cf6a604bbe6c5c0d5b70561eb4/apps/v4/examples/base/calendar-caption.tsx) · [官方示例](https://ui.shadcn.com/examples/base/calendar-caption)

iPad mini（A17 Pro，iOS 26.2）复拍五个用例的 10 张深浅色图，均逐字节一致。基础月历和双月区间的四张图与上轮像素相同，其余六张的变化仅位于月份、年份标题。macOS Swift Package 编译和 LTR/RTL 日期导航回归通过。[截图与 shadcn 差异](https://github.com/csdog/compose-foundation/actions/runs/46111415465/artifacts/15249971385) · [macOS 回归](https://github.com/csdog/compose-foundation/actions/runs/46111415465/job/140875608206)

iPhone 17 Pro（iOS 26.2）的 XCUITest 验证了触发器名称及当前值、菜单当前项的选中语义、月份和年份的触控切换、日期区间绑定保留，以及边界外选项不可用。两个展开态保留 iOS 系统菜单样式。实际 VoiceOver 朗读仍待验收。[iOS 交互回归](https://github.com/csdog/compose-foundation/actions/runs/46111945303/job/140877342187) · [月份和年份展开截图](https://github.com/csdog/compose-foundation/actions/runs/46111945303/artifacts/15250770536)

### Calendar Nova 双月区间与阿拉伯语 RTL

对照 `calendar-range.tsx` 和 `calendar-rtl.tsx`。双月区间固定为 2026 年 1 月 12 日到 2 月 11 日。`FoundationCalendar(range:initialMonth:numberOfMonths:)` 可以指定首个月份，原有绑定和范围初始化方式仍可使用。Nova 区间端点在本周本月内连接到相邻的已选日期。未完成区间、同日区间与周月交界处的端点独立显示。

RTL 用例使用阿拉伯语标题和星期名，保留参考图中的西文日期数字。SwiftUI 由布局方向控制月份按钮和星期列的排列。macOS 实际窗口在 LTR 和 RTL 下验证了禁用日期、选择、跨月翻页与月份上限，区间端点与周边界状态测试也已通过。[macOS 回归日志](https://github.com/csdog/compose-foundation/actions/runs/46084778506/job/140792048023)

iPad mini（A17 Pro，iOS 26.2）的 10 张深浅色候选图复拍逐字节一致。原有三种单月用例的 6 张图与上一轮像素一致。双月图使用 720 × 680 画布，容纳两个完整月份和 44 pt 日期触控行。shadcn 双月参考为 720 × 532。SwiftUI 的前后箭头分布于首末月份标题，区间色带的换行边缘为直角。[iOS 截图和对照报告](https://github.com/csdog/compose-foundation/actions/runs/46084778506/artifacts/15239270143)

### Menubar Nova 默认和阿拉伯语 RTL

对照 shadcn Base UI / Nova 的 `menubar-demo.tsx`、RTL 示例及同版本样式。Nova 菜单栏使用 3 pt 水平内边距、2 pt 触发器间距和可缩放的 14 pt Medium 字号。展开及指针悬停使用次要表面色，键盘焦点由现有焦点环显示。iOS 触发器保留至少 44 pt 高的点击区域，macOS 菜单栏至少高 32 pt。

macOS 实际窗口的 Menubar LTR、Menubar RTL 和 Navigation Menu LTR 均通过完整键盘回归。三个启用组与一个禁用组验证了正确的移动方向、禁用组及禁用项跳过、Return 只执行目标操作、Escape 归还焦点、整组禁用后关闭。[macOS 键盘日志](https://github.com/csdog/compose-foundation/actions/runs/46085948252/job/140795596981)

iPad mini（A17 Pro，iOS 26.2）的 4 张 720 × 520 候选图复拍逐字节一致。LTR 与阿拉伯语 RTL 审核用例的菜单栏宽 288 pt，高 44 pt，shadcn 对照为 288 × 32 CSS px。两边浅色画布为 `#FFFFFF`，深色为 `#0A0A0A`。截图应用以 iOS 14 模拟器部署目标编译。[iOS 截图与参考图](https://github.com/csdog/compose-foundation/actions/runs/46085948252/artifacts/15239227471)

### Navigation Menu Nova 默认和阿拉伯语 RTL

对照 shadcn Base UI / Nova 的 `navigation-menu-demo.tsx`、RTL 示例和同版本样式。Nova 横排使用可缩放的 14 pt Medium 字号、零列间距和 10 pt 触发器水平内边距。分组箭头位于逻辑末端，展开时翻转，辅助技术以按钮的展开值读取状态。iOS 触发器至少高 44 pt，macOS 至少高 36 pt。截图用例混排两个分组按钮和一个原生 `Link`。

macOS 实际窗口通过了 Navigation Menu 和 Menubar 各自的 LTR、RTL 按键回归。测试覆盖按布局方向移动、跳过禁用组和禁用项、用 Return 执行、Escape 收起并归还焦点，以及停用整栏时关闭当前面板。[macOS 键盘日志](https://github.com/csdog/compose-foundation/actions/runs/46087089785/job/140799174351)

iPad mini（A17 Pro，iOS 26.2）的 4 张 720 × 520 候选图复拍逐字节一致。浅色画布与参考同为 `#FFFFFF`，深色同为 `#0A0A0A`。iOS 使用系统字形和 44 pt 触控行，RTL 内容在该截图中比参考右移约 9 pt。审计应用以 iOS 14 模拟器部署目标编译。[iOS 截图与 shadcn 对照](https://github.com/csdog/compose-foundation/actions/runs/46087089785/artifacts/15239234065)

### Combobox Nova 描边、占位和分组

对照 shadcn Base UI / Nova 的 `combobox-demo.tsx`、`combobox-disabled.tsx` 和 `combobox-groups.tsx`。触发器使用 32 pt 输入描边、可缩放的 16 pt 文字、弱化占位文案及末端向下箭头。点按区域至少高 44 pt。分组标题保留标题语义，Nova 在筛选后可见的组之间绘制分隔线。macOS 的选项行至少高 28 pt，iOS 至少高 44 pt。

macOS 实际窗口的 Nova 分组用例通过了点击展开、方向键跨空组并跳过禁用项、Return 更新绑定、Escape 保留已选值，以及重新打开后用上键跳到最后一项。[macOS 交互日志](https://github.com/csdog/compose-foundation/actions/runs/46089020495/job/140805392257)

iPad mini（A17 Pro，iOS 26.2）在默认、分组和禁用初始态采集 6 张深浅色图，复拍逐字节一致。触发器在 720 × 520 画布中的宽度为 672 pt。禁用态中心底色与参考一致，浅色为 `#F8F8F8`，深色为 `#181818`。默认和分组图与调整禁用色前的候选图像素一致。审计应用以 iOS 14 模拟器部署目标编译。[iOS 截图与 shadcn 对照](https://github.com/csdog/compose-foundation/actions/runs/46089020495/artifacts/15239799106)

### Select Nova 默认、分组、禁用和阿拉伯语 RTL

对照 shadcn Base UI / Nova 的 `select-demo.tsx`、分组、禁用和 RTL 示例。Nova 使用可缩放的 14 pt 文字、32 pt 描边和至少 44 pt 的点按区。占位文字和箭头使用弱化色，箭头处于逻辑末端。分组映射到原生菜单分区，空分组在构造时过滤，选中项保留勾选图标及选择语义。

macOS 实际窗口验证了外部绑定切换蔬菜与水果时的触发器重绘、未知值回退到占位文字和整控件禁用时的外观。托管运行器的合成事件未能触发原生菜单项，本轮仍待复验菜单内点选。[macOS 窗口日志](https://github.com/csdog/compose-foundation/actions/runs/46091495270/job/140813282875)

iPad mini（A17 Pro，iOS 26.2）的 8 张深浅色截图复拍逐字节一致。默认、分组与禁用触发器宽 192 pt，阿拉伯语 RTL 宽 128 pt。与 shadcn 对照，浅色中心底面均为 `#FFFFFF`，深色启用为 `#141414`，深色禁用为 `#0F0F0F`。禁用调整前后的 6 张启用用例像素一致。截图程序以 iOS 14 模拟器部署目标编译。[iOS 截图和对照报告](https://github.com/csdog/compose-foundation/actions/runs/46091495270/artifacts/15241042954)

### Toast Nova 卡片、状态图标与队列

对照 shadcn Base UI / Nova 的 `toast-demo.tsx`、类型示例及 Sonner 的卡片样式。Nova 使用 16 pt 圆角、16 pt 起始内边距和可缩放的 14 pt 文案。Undo 使用紧凑描边按钮，Undo 与关闭操作保留至少 44 pt 高的点击区域。成功图标保留圆形标记，错误图标改用八角形并使用主题错误色。

macOS 实际窗口点击 Undo 后回调恰好执行一次，对应通知退出队列，最早的排队通知补位。关闭按钮只移除当前通知。另验证单条到期和清空队列后的计时器取消。[macOS 队列日志](https://github.com/csdog/compose-foundation/actions/runs/46092913983/job/140817763434)

iPad mini（A17 Pro，iOS 26.2）在 720 × 520 画布上采集默认触发和展开、类型触发、成功及错误共 10 张深浅色图。复拍逐字节一致。通知卡片的取样底色与 shadcn 一致，浅色为 `#FFFFFF`，深色为 `#171717`。Nova 的阴影应用于卡片轮廓。审计应用以 iOS 14 模拟器部署目标编译。[iOS 截图及 shadcn 对照](https://github.com/csdog/compose-foundation/actions/runs/46092913983/artifacts/15241424116)

### Carousel Nova 双侧翻页与 RTL

对照 shadcn Base UI / Nova 的 `carousel-demo.tsx` 和 RTL 示例。新增 `navigation: .sides` 可把前后按钮放在视口两侧，原有初始化器使用底部控件。Nova 使用 28 pt 圆形描边按钮及 44 pt 点按区。RTL 镜像按钮位置和箭头方向，视口继续提供带页码的无障碍调节操作。

macOS 实际窗口在 LTR 与 RTL 下均验证拖动过程中相邻页已进入视口，绑定等到松手吸附后才更新。侧边按钮可以翻页，向首屏边界外拖动停在第一页。[macOS 拖动日志](https://github.com/csdog/compose-foundation/actions/runs/46094533306/job/140822790736)

iPad mini（A17 Pro，iOS 26.2）的默认和 RTL 共 4 张深浅色截图复拍逐字节一致。RTL 使用阿拉伯文页码。卡片取样底色与 shadcn 一致，浅色 `#FFFFFF`，深色 `#171717`。左侧按钮在截图中随上游示例画布裁切。SwiftUI 审计应用以 iOS 14 模拟器部署目标编译。[iOS 截图与差异报告](https://github.com/csdog/compose-foundation/actions/runs/46094533306/artifacts/15242433307)

### Theme 预设输入框高度与切换

在同一 SwiftUI 表单中切换 Vega、Nova、Maia、Lyra、Mira、Luma、Sera、Rhea。常规字号的可见输入框高度分别为 36、32、36、32、28、36、40、32 pt，来自对应的 `style-*.css` 输入框高度。Sera 使用底部线条，常规文本编辑区域至少 44 pt 高，字号放大时可见框随之增高。[上游预设样式表](https://github.com/shadcn-ui/ui/tree/0e5b2e621a9ee4cf6a604bbe6c5c0d5b70561eb4/apps/v4/registry/styles)

macOS 在实际窗口用原生按键写入文本并点击保存，然后切换 16 种预设及深浅色组合。每种组合产生独立的画面，输入内容和保存次数保持。[macOS 预设验收日志](https://github.com/csdog/compose-foundation/actions/runs/46095932842/job/140828070077)

iPad mini（A17 Pro，iOS 26.2）的 16 张 720 × 520 截图复拍逐字节一致。按中心空白列测量上下描边，七套有描边的预设在深浅色下均得到对应的 28、32 或 36 pt 高度。Sera 的取样只出现底线。Theme 表单以预设样式表为上游依据，报告中的独立 shadcn 截图标为缺参考图。审计应用以 iOS 14 模拟器部署目标编译。[iOS 预设图与报告](https://github.com/csdog/compose-foundation/actions/runs/46095932842/artifacts/15243740986)

### Textarea Nova 焦点、禁用、错误与 RTL

对照 shadcn Base UI / Nova 的默认、Field、禁用、错误和 RTL 示例。Nova 保持至少 64 pt 高的编辑区，使用主题色区分焦点、禁用底色和错误环。焦点覆盖从 iOS 15、macOS 12 启用。新 SDK 的 RTL 编辑采用逻辑方向对齐，阿拉伯语用例的占位文字、标题和说明也随布局方向排列。

macOS 实际窗口点击编辑器后出现焦点环。逐键输入字符及回车写入两行绑定值。点击另一字段后焦点环消失，切换到错误态时仍显示错误环并保留两行文字。[macOS 多行编辑日志](https://github.com/csdog/compose-foundation/actions/runs/46097044679/job/140830735599)

iPad mini（A17 Pro，iOS 26.2）的 10 张 720 × 520 截图复拍逐字节一致。禁用编辑区的取样色与 shadcn 一致，浅色 `#F8F8F8`，深色 `#181818`。错误环取样每通道最多相差 2 个色阶。SwiftUI 编辑器外观保留原生滚动区域，浏览器拖动调节尺寸的手柄待对齐。审计应用以 iOS 14 模拟器部署目标编译。[iOS 截图与对照报告](https://github.com/csdog/compose-foundation/actions/runs/46097044679/artifacts/15243936930)

### Dropdown Menu Nova 收起态与 RTL

对照 shadcn Base UI / Nova 的 `dropdown-menu-demo.tsx` 和 `dropdown-menu-rtl.tsx`。Nova 触发器使用 32 pt 可见描边、随字号调整的文字和至少 44 × 44 pt 的点按区域。深色描边底色跟随页面或卡片表面。调用方仍可提供触发器内容和字体。分组、子菜单和禁用项通过既有 SwiftUI `Menu` 内容闭包传入。

macOS 实际窗口验证调用方更改标签后触发器即时更新，禁用后重新启用可恢复原外观。[macOS 验证日志](https://github.com/csdog/compose-foundation/actions/runs/46097918360/job/140833511445)

iPad mini（A17 Pro，iOS 26.2）在 720 × 520 画布上采集默认与阿拉伯语 RTL 共四张深浅色截图，复拍逐字节一致。深色触发器底色取样与 shadcn 同为 `#141414`。浅色阿拉伯语触发器因字形和字距差异比参考窄 8 px。审计应用以 iOS 14 模拟器部署目标编译。[iOS 截图与差异报告](https://github.com/csdog/compose-foundation/actions/runs/46097918360/artifacts/15244128043)

iPhone 17 Pro（iOS 26.2）的两项 XCUITest 验证实际点按。菜单展开后可查询分组标题和禁用项，点击禁用项不执行操作且保留展开。点击普通项或子菜单动作会更新结果并关闭菜单，点击外部会关闭菜单并保留结果。默认展开与子菜单的系统圆角、阴影和定位仍有别于 shadcn。iOS 测试记录了 UIKit 的 `_UIReparentingView` 层级警告，两项测试均通过。方向键遍历、Escape 后焦点归还和 VoiceOver 仍待验收。手动运行 Component parity 时选择 `update=review`、`platform=swiftui-ios`、`component=dropdown-menu` 可复验。[触控验证日志](https://github.com/csdog/compose-foundation/actions/runs/46098555210/job/140835528786) · [展开截图](https://github.com/csdog/compose-foundation/actions/runs/46098555210/artifacts/15244991195)

在相同 iPhone 模拟器上，XCUITest 的按键方式未改变菜单高亮，也未关闭菜单。使用 idb USB HID 事件复测后，Escape 关闭菜单且不执行动作。触控重开后输入两次 Down 和 Return，跳过禁用的 API 并选中 Settings。触控打开、Escape 关闭后，Space 未重新打开，键盘打开后的焦点归还仍待验收。可用已安装审计应用的模拟器 UDID 和 idb companion 地址运行 `python3 shadcn/iosApp/validation/verify_dropdown_menu_keyboard.py --device "$device" --companion "$companion"`，复验已确认的两条键盘路径。macOS 托管 runner 缺少系统输入注入权限，键盘探针未打开原生菜单。[iOS HID 探针日志](https://github.com/csdog/compose-foundation/actions/runs/46101756296/job/140845596818) · [XCUITest 探针日志](https://github.com/csdog/compose-foundation/actions/runs/46099999639/job/140840079576) · [macOS 探针日志](https://github.com/csdog/compose-foundation/actions/runs/46100117185/job/140840481645)

### Context Menu Nova 虚线触发区与 RTL

对照 shadcn Base UI / Nova 的 `context-menu-demo.tsx` 和 `context-menu-rtl.tsx`。新增可选的 `triggerStyle: .outline`，提供 16:9、宽度上限 320 pt 的完整触发区，沿用主题圆角及描边色。原有初始化器保持调用方提供的内容和布局。iOS 通过原生长按打开，macOS 使用原生右键菜单。菜单内容仍由调用方提供。

iPad mini（A17 Pro，iOS 26.2）采集默认、阿拉伯语 RTL 和深浅色的四张收起态截图，复拍逐字节一致。以相同 720 × 520 画布比较，触发区各边与 shadcn 相差至多 2 px。虚线取样差异集中在描边抗锯齿和虚线长度及间隔，阿拉伯文字形由各平台的备用字体绘制。截图采用参考的右键文案，实际触控用例使用长按文案。[iOS 截图与差异报告](https://github.com/csdog/compose-foundation/actions/runs/46102840957/artifacts/15246767791)

macOS 实际窗口向虚线框内的空白处发送右键事件。系统菜单的 Forward 标记为禁用。再发送两次 Down 和 Return，越过 Forward 并执行一次 Reload。[macOS 回归日志](https://github.com/csdog/compose-foundation/actions/runs/46103051907/job/140849669837)

iPhone 17 Pro（iOS 26.2）的两项 XCUITest 验证点按空白区不开菜单，长按同一位置会打开。禁用项不执行，Reload 和子菜单动作更新结果，点外部关闭并保留结果。展开时系统显示触发区预览及遮罩，与 shadcn Web 浮层的展开布局有差异。测试记录 UIKit 的 `_UIGravityWellEffectAnchorView` 和 `_UIReparentingView` 层级警告，两项测试通过。[iOS 触控日志](https://github.com/csdog/compose-foundation/actions/runs/46102840957/job/140848985022) · [展开截图](https://github.com/csdog/compose-foundation/actions/runs/46102840957/artifacts/15246013498)

### Command Nova 搜索、分组与空状态

对照 shadcn Base UI / Nova 的 `command-demo.tsx`。Nova 采用紧凑搜索栏、分组标题、组间分隔线和高亮行。调用方可提供查询绑定、搜索提示、空态文案和结果行的图标或快捷键提示。原有初始化器和按标题过滤保持兼容。快捷键提示是视觉标签。

macOS 实际窗口验证搜索框聚焦后，两次 Down 跳过禁用项并移到下一组。Return 执行高亮项一次。原生文本输入更新绑定并过滤结果，空状态不执行动作，Up 可从起点循环至最后一项。方向键增强适用于 iOS 17、macOS 14 起的系统，较早版本继续使用 SwiftUI 文本框和 Return 选择。[macOS 回归日志](https://github.com/csdog/compose-foundation/actions/runs/46106250389/job/140859625033)

iOS 的 Command 复用 Combobox 的 UIKit 搜索文本框处理硬件按键。两者保留各自的样式和自动聚焦规则。Combobox 的六张 iPad 截图与上一轮及本轮复拍均逐字节一致，macOS 分组键盘回归通过。[Combobox 回归](https://github.com/csdog/compose-foundation/actions/runs/46106240821) · [截图](https://github.com/csdog/compose-foundation/actions/runs/46106240821/artifacts/15247587438)

iPhone 17 Pro（iOS 26.2）的两项 XCUITest 验证实际文本输入、空状态、禁用项和点按执行。聚焦搜索框后发送两次硬件方向键，再点屏幕键盘的 Search，从第一组跳过禁用项并执行下一组的 Profile。硬件 Return 在 macOS 已验证，iOS 的实体 Return 与 VoiceOver 仍待验收。[iPhone 回归](https://github.com/csdog/compose-foundation/actions/runs/46106217615/job/140859515396)

iPad mini（A17 Pro，iOS 26.2）采集初始、过滤和空状态的六张深浅色截图，均与复拍逐字节一致。搜索栏取样色为浅色 `#F7F7F7`、深色 `#212121`，高亮行为浅色 `#F5F5F5`、深色 `#262626`，均与 shadcn 一致。过滤面板比参考高约 13 pt，iOS 行保留 44 pt 的点按高度。上游初始图同时显示空态文字和结果，SwiftUI 只在无匹配项时显示空态。独立空状态尚无对应的上游截图。[iOS 截图和差异报告](https://github.com/csdog/compose-foundation/actions/runs/46106250389/artifacts/15248145701)

### Command Dialog 搜索焦点与关闭

`FoundationCommandDialog` 接受呈现绑定和已有的 Command 分组。iOS 使用居中 Dialog，macOS 使用窗体的系统 Sheet。调用方可以在触发器注册 `⌘J`。iOS 17、macOS 14 起默认把键盘焦点放在搜索框。开启 VoiceOver 时保留对话框标题的焦点策略。执行结果、按 Escape 或更新呈现绑定均可关闭面板，关闭回调可将焦点归还给调用方指定的触发器。重新打开时搜索查询和行高亮复位。

shadcn 官方 `command-dialog` 的收起态参考图可用。当前官方预览收到 `⌘J` 后报 `Cannot read properties of undefined (reading 'subscribe')`，展开态未生成参考图。原生展开态使用可见关闭按钮。[官方示例](https://ui.shadcn.com/examples/base/command-dialog) · [固定源码](https://github.com/shadcn-ui/ui/blob/0e5b2e621a9ee4cf6a604bbe6c5c0d5b70561eb4/apps/v4/examples/base/command-dialog.tsx)

macOS 实际窗口验证 `⌘J` 打开、搜索自动聚焦、键入过滤后用 Return 执行动作。选择结果、Escape 和外部绑定关闭均归还触发器焦点，选择和 Escape 后也通过键盘重新打开复验。普通 Dialog 的四张 iPad 图与上一轮逐字节一致。共享呈现组件的 macOS 回归通过。[Command Dialog 回归](https://github.com/csdog/compose-foundation/actions/runs/46108085674/job/140865317244) · [普通 Dialog 回归](https://github.com/csdog/compose-foundation/actions/runs/46107173404)

iPhone 17 Pro（iOS 26.2）触控打开后直接向搜索框输入，验证查询过滤、点按结果关闭、重新打开后查询清空，以及 Close 按钮关闭。交互附件包含模拟器首次显示键盘时的系统教学提示。iOS 硬件 Escape、关闭后的触发器焦点与实际 VoiceOver 仍待验收。[iPhone 回归](https://github.com/csdog/compose-foundation/actions/runs/46108085674/job/140865317325) · [交互截图](https://github.com/csdog/compose-foundation/actions/runs/46108085674/artifacts/15248970456)

iPad mini（A17 Pro，iOS 26.2）在 720 × 520 画布重拍 Command 的十张深浅色图，全部与复拍逐字节一致。六张原有内联图也与上一轮逐字节一致。新增收起态触发器与 shadcn 参考的边界相差最多 3 px，展开面板完整落在画布内，结果区可滚动。[iPad 截图与差异报告](https://github.com/csdog/compose-foundation/actions/runs/46108707115/artifacts/15249145767)

### Command 加载状态、固定页脚与快捷键

内联 `FoundationCommand` 和 `FoundationCommandDialog` 都支持 `.loading(_:)` 与 `.footer {}`。加载时仍可执行当前匹配且可用的结果。加载指示存在时暂停显示空态提示，开启减少动态效果时使用静态图标。页脚位于滚动区域下方，在零匹配结果时也可使用。

shadcn 的 `command-shortcuts` 在 Settings 分组的每行显示快捷键提示。SwiftUI 保留 `shortcutLabel`，并可通过 `ActionAppearance` 的 `shortcut` 参数绑定原生按键。该绑定仅适用于当前匹配且启用的行，包括滚动区域外的行。官方快捷键示例的深浅色展开态各独立采集两次，结果逐字节一致。加载与页脚为原生扩展，视觉审核使用 SwiftUI 截图。[shadcn 快捷键示例](https://ui.shadcn.com/examples/base/command-shortcuts) · [参考图 PR](https://github.com/csdog/compose-foundation-baselines/pull/12)

macOS 实际窗口验证了过滤后快捷键失效、视口外的结果仍可触发、禁用行和禁用面板拦截按键、加载中执行已有匹配项，以及加载中没有匹配项时仍可通过键盘执行页脚操作。Command Dialog 的搜索自动聚焦和关闭后焦点归还也通过回归。[macOS 运行记录](https://github.com/csdog/compose-foundation/actions/runs/46109901238/job/140870935386)

iPhone 17 Pro（iOS 26.2）通过 XCUITest 验证了搜索框聚焦期间用 `⌘P` 执行动作、加载状态期间点按页脚、完成加载后恢复空态提示，以及零匹配时页脚仍可交互。原有搜索、方向键与 Command Dialog 触控测试同样通过。[iOS 运行记录](https://github.com/csdog/compose-foundation/actions/runs/46109899451/job/140870954906)

iPad mini（A17 Pro，iOS 26.2）的 16 张深浅色截图均与复拍逐字节一致，之前的 10 张保持像素和文件一致。新增快捷键展开态使用三项相同内容与 384 pt 宽的面板。SwiftUI 面板保留标题和关闭按钮，iOS 行高至少为 44 pt。上游展开图显示首项高亮，原生静态采集未激活搜索键盘，初始行高亮仍有差异。[iPad 截图及 shadcn 差异](https://github.com/csdog/compose-foundation/actions/runs/46109901238/artifacts/15249263011)

快捷键展开图约 80% 的差异像素来自遮罩下的一档灰度差。浅色背景为 SwiftUI `#E6E6E6`、shadcn `#E5E5E5`，深色为 `#090909`、`#080808`。面板与搜索框取样色在两端一致。

## 各条目

| 条目 | SwiftUI 实现与差异 |
| --- | --- |
| accordion | 支持单开或多开、禁用项和绑定展开集合。Nova 使用全宽按钮、标题语义、展开状态和末尾方向箭头。横向布局与独立组装的触发器尚待对齐。 |
| alert | 支持标题、说明、图标、操作和 destructive。Nova 使用紧凑的图标与文字排版，错误说明采用主题 destructive 色。操作区位置及 RTL 尚待对照。 |
| alert-dialog | 支持系统确认框及可选的居中面板。常规、紧凑、媒体、紧凑媒体和阿拉伯语 RTL 示例覆盖深浅色。调用方可以在 dismiss 后归还触发器焦点。iOS 触控、iOS 焦点恢复和 VoiceOver 待验收。 |
| aspect-ratio | 固定宽高比，裁剪溢出内容与点击范围，无效比例回退到 1:1。无界布局和 RTL 样例待进一步对照。 |
| attachment | 有上传进度、处理、失败和完成状态，并支持移除。缺少 Compose 的尺寸、纵向布局、附件组与预览触发器。 |
| avatar | 支持本地 `Image`、三种尺寸、首字母或自定义回退、状态徽标、头像组和计数。iOS 15、macOS 12 起支持原生异步 URL 图片。 |
| badge | 支持 primary、secondary、outline、destructive。缺少 Compose 的 ghost/link 样式与图标位置参数。 |
| breadcrumb | 支持水平路径、链接、当前页、自定义分隔符以及静态与菜单式省略项。自定义页面 slot 和其余示例待对齐。 |
| bubble | 支持 incoming、outgoing、muted 内容气泡。缺少气泡组及定位在气泡边缘的 reactions。 |
| button | 六种颜色变体、禁用、按压反馈和内容 slot。Compose 另有尺寸、图标位置、焦点环和多种按压动画。 |
| button-group | 支持纵向和横向布局。iOS 18、macOS 15 起，Nova 的 FoundationButtonStyle 子项合并相邻圆角和边框。分隔符、文本项及子项焦点层级仍待对齐。 |
| calendar | 支持本地化单月和连续多月、单选、区间、多选及月份年份菜单。Nova 已验收单月、双月区间、RTL 截图，以及 iPhone 上菜单当前值、选中项和触控切换。实际 VoiceOver 朗读待验收。 |
| card | 支持主题背景、边框、常规与紧凑尺寸，以及独立的底部操作区。头部、图片与关联操作可通过内容构建器组合。边缘贴图和 RTL 尚待对照。 |
| carousel | 当前页和相邻页随横向拖动移动，松手后吸附。支持 RTL、底部或侧边翻页、页码无障碍调节和减少动态效果。Nova 已接入默认与 RTL 初始态截图。纵向、部分页宽及分页圆点待对齐。 |
| chart | 支持柱形、折线、面积、多序列和选点摘要。Compose 的独立图例、工具提示、序列主题配置和 indicator 选项未移植。 |
| checkbox | 支持未选中、已选中和部分选中。部分选中使用减号、独立无障碍值和点按转全选。Nova 使用 16 pt 标记、深色未选中底色和首行文字对齐。Bool ToggleStyle 和 SwiftUI 混合状态均受支持。 |
| chip | 可切换选中状态，支持内容标签和选中语义。缺少 Compose 的独立 chip 样式变体。 |
| code-block | 支持横向滚动、语言标签和行号。缺少复制按钮、行状态、页脚和操作栏。 |
| collapsible | 保留原生 DisclosureGroup，支持可单独摆放的按钮和内容 slots、共享展开状态和自定义标签。展开动画由调用方决定。 |
| combobox | 支持搜索、空态、禁用选项、分组、单选和可移除的多选 chips。Nova 已接入默认、分组和禁用初始态对照。此前已验证分组导航、隐藏空组、多选、软键盘提交和 VoiceOver 朗读。Nova 展开面板的 iOS 截图和定位待验收。 |
| command | 支持 Nova 搜索、分组、空态、禁用项、方向键高亮、加载状态、固定页脚和原生逐项快捷键。Command Dialog 支持绑定呈现和搜索焦点，macOS 已验证关闭后归还焦点。实际 VoiceOver 仍待验收。 |
| context-menu | Nova 可选虚线触发区，iOS 原生长按和 macOS 右键已验。普通项、禁用项、子菜单及外点关闭已有实测，macOS 已验证方向键跳过禁用项。展开样式、iOS 键盘及 VoiceOver 待对齐或验收。 |
| dialog | 支持系统 sheet 及带遮罩的可选居中面板、滚动内容和固定页脚。默认编辑资料示例覆盖深浅色。其他变体、iOS 居中版触控、键盘焦点和 VoiceOver 待验收。 |
| drawer | 支持原有底部面板，以及 Nova、Rhea 的说明文字、可滚动内容和固定页脚。原布局已验收关闭焦点、Return、Escape、向下拖动和焦点归还。侧边面板、吸附点、嵌套面板和无模态示例待对齐。 |
| dropdown-menu | Nova 触发器覆盖默认与阿拉伯语 RTL 收起态。iOS 已验证分组、禁用项、子菜单、外点关闭、方向键选择和 Escape 关闭。展开面板保留系统样式，键盘焦点归还和 VoiceOver 待验收。 |
| emoji-text | Apple 系统彩色 emoji，可保留 ZWJ 等 Unicode 序列。Compose 使用自己的 emoji 图形与替换逻辑。 |
| empty | 支持符号、标题、说明和操作 slot，以及标准和虚线边框样式。Nova 对齐基本排版。自定义头像等媒体及其余示例布局待对照。 |
| field | 纵向显示标题、说明、错误及任意输入。Nova 使用 14 pt 支持文字与 8 pt 间距，说明可放在控件前或后。FieldSet、Legend、横向及响应式排列待补齐。 |
| hover-card | 支持延迟开关、触发器与卡片间的指针切换、长按和受控展开。Nova 使用锚定弹层，悬停保持原窗口焦点。共享 provider、位置变体及触控回归待对齐。 |
| icon | 支持 SF Symbols、动态字号和可选无障碍标签。Compose 可传入任意 ImageVector 并选择图标变体。 |
| input | 原生单行输入支持独立占位文字、SecureField、错误和禁用状态。Nova 边框视觉高 32 pt，字段布局至少高 44 pt。iOS 26 与 macOS 26 的 Nova RTL 字段按界面起始边对齐。混合脚本输入、iOS 光标及 VoiceOver 待单独验收。 |
| input-group | 支持横向和纵向附加区域、共用边框与由调用方控制的焦点/错误外观。输入和操作可通过原生 SwiftUI 控件组合。 |
| input-otp | 使用一个定长数字绑定和原生编辑字段，iOS 字段标记为一次性验证码。Nova 使用连续的 32 pt 格，可通过 `groupBreaks` 加入分隔符。字母数字模式及逐槽选区控制仍待补齐。 |
| item | 支持媒体、标题、说明、操作、三种表面样式和三种尺寸。群组、图片媒体尺寸和独立 header/footer 待对齐。 |
| kbd | 显示单个键符及 themed 键帽。Compose 另有组合键分组 slot。 |
| label | Nova 标签使用 14 pt Medium，支持禁用淡化。作为原生 Toggle 的内容时，文字和复选框使用同一绑定。独立表单标签的焦点关联待补齐。 |
| marker | plain、separator、border 三种文本标记。缺少 Compose 的独立图标和内容 slots。 |
| menubar | 分组入口支持共享打开状态、键盘移动、跳过禁用项、Return 执行和 Escape 关闭。Nova 菜单栏覆盖默认和阿拉伯语 RTL。展开面板对照、子菜单、复选和单选项待补齐。 |
| message | 收发对齐、作者、时间和内容。没有 Compose 的头像、消息组、独立 header/footer slots。 |
| message-scroller | 内容更新时跟随末尾、触摸拖动暂停、恢复跟随和到达顶部回调。缺少 Compose 的任意锚点寻址、可见 ID，以及对桌面滚轮的完整跟随判定。 |
| navigation-menu | 支持横向内容 slot、分组触发器和关联弹层。Nova 初始态覆盖默认和阿拉伯语 RTL。展开面板对照、共享 viewport 和 indicator，以及分组与独立链接间的方向键导航待补齐。 |
| pagination | 支持前后页图标或文字、纯页码布局、标准和紧凑窗口、省略及当前页绑定。独立的首末按钮和自定义内容 slots 待对齐。 |
| popover | iOS 和 macOS 使用锚定的无箭头圆角面板，默认居中显示在触发器下方，间距 4 pt，并根据可用空间翻转或调整位置。`arrowEdge` 指定首选方向，`onDismiss` 在窗口关闭后调用。iOS 模拟器已验证面板内键盘焦点、Return、Escape、触控关闭和跨窗口还原触发器焦点。 |
| progress | 支持范围归一化、动画、减少动态效果与百分比无障碍语义。Compose 可插入 label、value、track 和 indicator。 |
| questionnaire | 单选、多选、文本题、验证、回退、跳过及提交可用。缺少 Compose 的快捷键、题定义 slots、重置与部分进度状态 API。 |
| radio-group | 支持纵向单选和禁用。Nova 使用 16 pt 圆形控件、8 pt 选中圆点和深色未选中底色。iOS 每项至少保留 44 × 44 pt 点按区域。macOS 与 iPad 已验收 LTR、RTL 的方向键，macOS 已验收组内单一 Tab 停靠点，iPad 已验收触控。横向布局与实际 VoiceOver 仍待验收。 |
| resizable | 支持双窗格纵向/横向比例、拖动限制、RTL 和无障碍增减。拖动读取全局坐标，避免手柄移动改变手势参考点。Compose 另有专门的状态与面板 slots。 |
| scroll-area | 使用原生 ScrollView，支持滚动轴、系统指示器与可选主题边框和圆角裁剪。自定义滚动条及其显隐和拖动配置待对齐。 |
| scroll-fade | 按所选边缘和固定距离加渐变遮罩。和 Compose 的静态 overload 对应，缺少按可滚动方向和偏移量驱动的 overload。 |
| select | 原生 Menu 支持单选、占位、选中图标、禁用项和原生分区标题。Nova 已接入默认、分组、禁用及阿拉伯语 RTL 初始态截图。展开样式、尺寸和面板位置待对齐。 |
| separator | 横纵向装饰性分隔线可用。边缘颜色和屏幕像素由 SwiftUI/theme 决定。 |
| sheet | 支持系统 sheet 及四个逻辑边的面板。可设最大高度，正文可滚动，页脚固定。四个方向和阿拉伯语 RTL 示例覆盖深浅色。iOS 实际触控、滚动及 VoiceOver 待验收。 |
| shimmer | 支持开关、穿过内容的光带、减少动态效果。Compose 另有文本遮罩及可调颜色、宽度、方向、时序。 |
| sidebar | 可开关侧栏，宽容器并排、窄容器遮罩。Compose 另有侧栏方位、collapsible/rail 策略、菜单 slots 和键盘快捷键。 |
| skeleton | 支持静态主题占位圆角。Compose 默认包含 pulse 动画。 |
| slider | 支持单值范围和步长。iOS 17、macOS 14 起，Nova 使用 4 pt 轨道、12 pt 滑块与 RTL 方向，系统提供可调整无障碍语义。较早系统继续用原生 Slider。纵向和双滑块范围仍待补齐。 |
| spinner | 系统不定进度控件及无障碍标签。Compose 提供原有旋转外观和 size 参数。 |
| stepper | 支持当前、已完成状态和点击回调。Compose 的横向排列、连接线与描述 slots 未移植。 |
| switch | Nova 使用 32 × 18.4 pt 轨道、16 pt 滑块和至少 44 pt 高的触控区域。iOS 15、macOS 12 起使用系统 Toggle 提供无障碍语义，较早系统提供按钮及开关状态。其余预设使用原生 SwiftUI Switch。小尺寸和组合字段仍待对照。 |
| table | 支持列标题、数据行、水平滚动、排序、说明文字、页脚和自定义正文单元格。Nova 的完整发票、紧凑页脚、阿拉伯语 RTL 和商品操作示例覆盖深浅色。跨列合并、iOS 实际菜单操作和 VoiceOver 待验收。 |
| tabs | 支持选择绑定、禁用、横向 tabs 与内容切换。iOS 17 / macOS 14 起提供左右键、Home / End、禁用项跳过，以及自动或手动激活。iOS 26.2 模拟器已验证 LTR、RTL、跨组 Tab、长列表滚动、自动激活及手动模式的独立焦点与提交。macOS 15.6.1 已验证自动模式跳过禁用项、跨组 Tab 和手动激活。Compose 另有纵向、list variant 和独立焦点移动 API。 |
| text | 支持标题、headline、正文、caption 和 muted 文本。Compose 有更多主题字体角色及自定义内容和样式参数。 |
| textarea | 原生 TextEditor 支持多行绑定、独立占位文字、禁用和错误环。Nova 最小高 64 pt，iOS 16 和 macOS 13 起隐藏系统滚动背景。自动增高、RTL 实际键入和焦点态 iOS 截图待验收。 |
| theme | SwiftUI 环境支持自定义深浅色、圆角及八种预设。按钮、输入框、卡片和 Popover 使用预设尺寸。各预设的完整配色、图表色和逐组件状态样式仍待对齐。 |
| toast | 支持单条通知和 `FoundationToastState` 队列，默认显示最新三条，支持操作、逐条关闭、清空及独立计时。Nova 已接入默认、成功和错误样式的截图。堆叠展开、滑动关闭、优先级播报和多操作待对齐。 |
| toggle | 可绑定选中、禁用及自定义标签。Nova 支持普通、描边和三个尺寸。描边书签的状态已截图。 |
| toggle-group | 支持单选、多选、是否允许空选择、禁用、自定义图标标签和纵向布局。macOS 与 iPad 已验收横向 LTR/RTL、纵向键盘和独立的按下状态。iPad 触控及原有横向截图已验收。纵向深浅色截图与 VoiceOver 待验。 |
| tooltip | Nova 支持反色气泡、方向配置、悬停延迟和绑定显示。iOS 继续提供长按提示及无障碍 hint。共享 provider、边界自动换边、滚动容器裁剪、自动键盘焦点触发和 RTL 上屏验收待补。 |
| trace | 支持状态、标题和可展开内容。Compose 的多行跟踪、独立图标、描述和链接 slots 未移植。 |

## Button 逐帧对比

将 shadcn/ui 官方 Base UI Button 的首个 Outline 示例与 iPhone 17 Pro 模拟器上的 `FoundationButtonStyle(.outline)` 对照。双方均用 Nova 浅色和相同的 “Button” 标签。Web 连续采集 25 张截图。旧版 SwiftUI 采集 34 帧，按住时文字和边框随整个按钮一起淡化。Web 在约 0.75 秒的触屏按压结束后测到 1 CSS px 的下移再复位。以 Web 的 `pointerup` 和 SwiftUI 点击计数首次变化的帧为各自的时间零点。[官方示例](https://ui.shadcn.com/docs/components/base/button)

新版使用 SwiftUI `ButtonStyle` 的原生按压状态，以 150 ms 曲线平移 1 pt。开启减少动态效果后直接切换位置。新版模拟器复拍 39 帧，按住时文字下移约 3 个物理像素且亮度保持稳定，松手后回到起始位置。无障碍树确认触控区域仍高 44 pt，实际按钮动作只执行一次。iOS 14 模拟器目标编译、macOS 11 部署目标的 Swift Package 构建及 69 项 SwiftUI registry 重新生成均通过。当前对比覆盖触屏输入，Web 和 SwiftUI 下移动作的时机仍有差异。

## Popover 收起态视觉验收

验收范围为 Nova 默认变体的收起态，覆盖浅色和深色。SwiftUI 审计应用用 `FoundationButtonStyle(.outline)` 和 `FoundationPopover` 组装可打开的触发器。审计应用复用仓库内的 Geist Medium 字体。按钮外观与点击区域分别高 32 pt 和 44 pt。

iPhone 17 Pro（iOS 26.2、Xcode 26.2）每个主题各渲染两次。每对原生 PNG 完全一致。将上游 720 × 520 画布裁到同一 402 × 520 区域，按 1 pt 对 1 CSS px 比较，保留全部可见控件。浅色和深色的按钮均位于左上角 24 pt，文字位置和背景一致。像素差异限于按钮区域，涉及边缘及字形抗锯齿和深色填充的 1 级灰度差。展开态和键盘激活态有独立的参考图，本次审核覆盖收起态。[shadcn 参考与来源](https://github.com/csdog/compose-foundation-baselines/blob/df707eeb146db4b3bb2b23ba8306444039f6e541/shadcn/popover/Index.md) · [浅色与深色审核附件](https://github.com/csdog/compose-foundation/actions/runs/45975773617/artifacts/15187598129)

### 扩展 Popover 截图验收

SwiftUI iOS 的计划包含默认态的收起、展开、键盘聚焦和键盘激活，以及 Basic、Alignments、Form 和 RTL 的收起与展开。每个用例采集浅色和深色，合计 24 张。Alignments 的参考图展开 Start，RTL 的参考图展开「يسار」。两个示例里的其他触发器仍可使用。布局使用 `FoundationPopover` 的 start、center、end 对齐与限宽，常规调用仍以安全区约束弹层。

采集程序将触发器和弹层窗口绘制到同一张 PNG。它按 1 pt 对 1 CSS px 对照参考图。RTL 按计划内的原点裁切 Web 画布，完整保留两排触发器和弹层。键盘截图使用预设状态和原生输入框焦点，实际发送硬件按键的行为由下文焦点脚本独立验收。[shadcn Popover 截图及来源](https://github.com/csdog/compose-foundation-baselines/tree/main/shadcn/popover)

在 iPhone 17 Pro（iOS 26.2）上，两轮各 24 张 PNG 逐文件一致。展开图同时保留触发器、背景和完整面板。默认 Nova 深色按钮中部实测为 `#141414`。Web 和 iOS 的文字光栅化、圆角与阴影仍有像素差异，逐项差异图收录在审核附件。macOS Swift Package 构建通过。[验证运行](https://github.com/csdog/compose-foundation/actions/runs/45988767609) · [24 张实拍与 shadcn 对照](https://github.com/csdog/compose-foundation/actions/runs/45988767609/artifacts/15192533031)

## 复现

```sh
python3 shadcn/registry/build.py
xcrun swiftc -parse-as-library -target arm64-apple-macos11.0 -swift-version 6 \
  shadcn/iosApp/Sources/ComposeFoundationSwiftUI/*.swift \
  shadcn/iosApp/validation/AuditFixtures.swift \
  shadcn/iosApp/validation/RenderAudit.swift -o /tmp/compose-foundation-render-audit
/tmp/compose-foundation-render-audit build/shadcn-registry/swiftui/registry.json build/shadcn-audit/swiftui/macos
```

渲染程序要求 fixture 和 registry 条目一一对应，在 macOS AppKit 容器中输出浅色与深色图。iOS 使用同一套 fixture，由 `IOSAuditApp.swift` 和 `IOSAuditInfo.plist` 组成独立模拟器应用，构建时将 SwiftUI 的 `registry.json` 放入应用 bundle。渲染结果保存到应用容器的 `Documents/ParityAudit`。

iOS 审计应用以 iOS 14.0 为部署目标。用 iOS 26.2 SDK 构建后，二进制记录的最低版本为 14.0。在 iPhone 17 Pro（iOS 26.2）上，该构建成功写出全部 69 个条目的 138 张深浅色截图及 `result.txt`。

菜单、popover 和系统对话框的 fixture 使用收起状态。源码差异来自组件实现核对。截图用于查看 SwiftUI 样例。Compose 像素匹配以及手势、键盘和 VoiceOver 需要独立的交互回归测试。

启动 iOS 审计应用时传入 `--combobox-interaction` 可打开单选和多选测试页。macOS 可构建同一测试页。

```sh
xcrun swiftc -parse-as-library -target arm64-apple-macos11.0 -swift-version 6 \
  shadcn/iosApp/Sources/ComposeFoundationSwiftUI/*.swift \
  shadcn/iosApp/validation/AuditFixtures.swift \
  shadcn/iosApp/validation/MacComboboxAudit.swift -o /tmp/compose-foundation-combobox-audit
/tmp/compose-foundation-combobox-audit
```

在测试页打开单选框，等待搜索框获得焦点。按两次 ↓ 再按回车会跳过禁用的 Restricted 并选中 Advanced。重新打开后按 ↑ 再按回车会选中 Network。按 Escape 应关闭列表并保留已选值。多选框可用来复验选项的选中状态和 chip 的移除标签。

iOS 26.2 的 iPhone 17 Pro 模拟器运行了实际 VoiceOver。使用 VoiceOver 左右导航键移动焦点，并从 VoiceOver 字幕面板核对朗读。多选触发器会读出当前值，搜索框朗读一次 “Search” 和文本栏角色，General 会读出已选中，Restricted 会读出不可用。VoiceOver 运行期间，通过无障碍按下操作选中 Advanced、关闭列表并移除 “Remove Advanced” chip。字幕面板确认了 Advanced 的已选状态、移除按钮名称，以及移除后触发器只读出 General。

iOS 模拟器的自动回归也核对了触发器当前值、移除按钮名称和选项的已选与禁用状态。列表展开后的自动描述审计另外报告了三个缺少描述的系统键盘预测词单元格。

iOS 模拟器的 `ComboboxUITests.swift` 覆盖方向键高亮、软键盘提交、触控和无障碍语义，可加入以 iOS 审计应用为目标的 UI 测试 target。硬件 Return 和 Escape 在 iOS 26.2 上另用 idb USB HID 事件验收。打开单选框后依次发送键码 81、81、40，应关闭列表、选中 Advanced。重新打开后发送 82、41，应关闭列表并保留 Advanced。

启动 iOS 审计应用时传入 `--choice-groups-interaction`，再运行 `validation/verify_choice_groups.py`。测试覆盖 Combobox 分组的标题语义、跨组键盘导航、搜索空态、多选和移除 chips，以及 Select 分区标题、禁用项和选择结果。两个组件的独立 SwiftUI registry 条目均通过 iOS 14 和 macOS 11 类型检查。

macOS 15.6.1 的审计应用也接受 `--choice-groups-interaction`。在实际窗口打开 Grouped Combobox，等 Search 获取焦点后按两次 Down 和 Return，从 Fruit 跨到 Vegetables，跳过 Apricot 并选中 Carrot。再次打开并输入 `cel` 后，树中只剩 Vegetables 和 Celery，Escape 保留 Carrot。多选框从两个组选中 Apple 与 Carrot，点 Done 后移除 Apple，触发器只显示 Carrot。Grouped Select 的原生菜单显示 Fruit、Vegetables 和禁用的 Apricot，选择 Carrot 后核对了菜单关闭及触发器无障碍值。

启动审计应用时传入 `--tabs-interaction` 或 `--tabs-manual-interaction` 可打开 Tabs 测试页。在 iOS 26.2 模拟器开启完全键盘控制后，自动模式下按右键会从 Overview 跳过 Restricted 选中 Activity，Home 和 End 会跳到首项和末项，末项再按右键回到首项。手动模式下，同一次右键只将焦点移到 Activity，按 Space 后才切换面板，从 Activity 按右键再按 Return 会选中 Settings。

同一测试页接受 `--tabs-rtl` 和 `--tabs-overflow`。在模拟器的完全键盘控制服务运行时，`validation/verify_tabs_keyboard.py` 通过 idb USB HID 验证 RTL 方向键、跨组 Tab、手动 Return，以及长列表最后一个标签可完全滚入可视区域。脚本新增 `--tabs-disabled` 场景，核对方向键、End、Space 和 Return 不改变两个已禁用的 tab strip 的选择。启用系统的完全键盘控制时，系统接管部分方向键和 Space 行为，自动模式会在组内焦点移动时同步选择。组件的自定义方向键处理采用 iOS 17、macOS 14 起提供的 SwiftUI API。包仍支持 iOS 14、macOS 11，较早系统走原生 Button 的选择路径。

本轮以 shadcn `0e5b2e621a9ee4cf6a604bbe6c5c0d5b70561eb4` 的 Base UI / Nova 为参考。Tabs 整组禁用后退出键盘焦点路径，方向键和激活键不写入选择绑定。Menubar 与 Navigation Menu 共用同一规则，整组禁用时关闭已打开的菜单。iOS 上如果原触发器在关闭时已移除或禁用，焦点回到首个可用触发器。Swift 6 语法检查与 Calendar 的连续月、月份选择和年份边界状态回归已在 Linux 通过。本轮 Tabs 和菜单的新增用例仍需在 Apple 系统上运行。[Tabs 禁用示例](https://github.com/shadcn-ui/ui/blob/0e5b2e621a9ee4cf6a604bbe6c5c0d5b70561eb4/apps/v4/examples/base/tabs-disabled.tsx) · [Nova 样式](https://github.com/shadcn-ui/ui/blob/0e5b2e621a9ee4cf6a604bbe6c5c0d5b70561eb4/apps/v4/registry/styles/style-nova.css)

Tabs 的焦点目标和选择写回规则现在可用纯 Swift 独立复验。18 条断言覆盖焦点入口、LTR/RTL、方向键循环、首末项、失效焦点、自动与手动激活，以及重复事件的去重。SwiftUI registry 的 Tabs 安装项包含导航源码。

```sh
mkdir -p build/shadcn-audit/swiftui-linux
swiftc -swift-version 6 -parse-as-library \
  shadcn/iosApp/Sources/ComposeFoundationSwiftUI/FoundationTabsNavigation.swift \
  shadcn/iosApp/validation/TabsNavigationAudit.swift \
  -o build/shadcn-audit/swiftui-linux/tabs-navigation-audit
build/shadcn-audit/swiftui-linux/tabs-navigation-audit
```

在 Linux 上用 isim 0.36.0（兼容 iOS 18）、Swift 6.2.4 和 LLVM 21 编译并运行了独立探针。Tabs 实测 LTR/RTL 跳过禁用项、自动切换、手动 Return/Space 提交、Home/End、首项向后循环与整组禁用。Menubar 通过 Down 跳过禁用项、Return 执行 Save、Right 切组及 Escape 关闭后用 Down 重开原菜单。Navigation Menu 通过 Right 切组和 Return 执行 User guide。Dialog 用 Return 关闭，Sheet 用 Escape 关闭，Drawer 用两键分别关闭。三个弹层均核对了关闭回调计数和焦点归还。[isim 的 SwiftUI 焦点和键盘覆盖](https://github.com/kolabs-dev/isim/blob/v0.36.0/docs/frameworks/SwiftUI.md#focus--keyboard)

isim 上还验证了 Combobox 分组标题与触控选择、Calendar 双月显示及月份和年份切换、Toast 队列显示上限与 Undo、Carousel 拖动松手后切页与外部绑定，以及 Theme 的八个预设菜单项、切换 Lyra 和深色后保留保存计数。探针在临时源码中适配了 isim 的 `defaultFocus` 签名，弹层和其余独立探针用 Swift 5 语言模式编译。主题探针省去了依赖 `TextField` 旧初始化器的输入框。

后续在同一版本 isim 上完成了三项补验。Popover 探针在临时适配中把 `UIWindow.makeKey()` 接到该运行时的 `makeKeyWindow()`，观测到打开后焦点进入面板、Return 关闭、按 P 从原触发器重开、Escape 关闭，每次关闭只回调一次并归还焦点。Select 探针使用等价的组索引范围及 `Section` 构造方式，观察到两个分区的选项、禁用 Apricot 后选择值保持为空，以及选择 Carrot 后关闭并写回。Carousel 的临时探针记录了 LTR 和 RTL 手势期间连续变化的布局位移，绑定在松手前保持第 1 页，松手后切到第 2 页。Apple 模拟器和 macOS 仍是原生焦点验收环境。探针源码、界面树与断言结果位于 `build/shadcn-audit/swiftui-linux/isim/`。

Linux 回归继续使用 isim。agent-sim 试跑已结束，安装停在磁盘检查，未产生 SwiftUI 界面验收结果。当前 Linux 实例可运行 Swift 状态回归，无法运行 Xcode 的 iOS Simulator 或 macOS AppKit 回归。下文的 Apple 实测记录对应此前的 iOS 26.2 和 macOS 15.6.1 构建，本轮 Tabs 和菜单变更仍需原生复验。

macOS Tabs 回归在活动图形会话中分别验证 LTR、RTL 及两种选择模式。用例覆盖跳过禁用项、Home/End、双向循环、选择绑定的写入次数、手动模式的 Space/Return，以及整组禁用后的键盘输入。

```sh
mkdir -p build/shadcn-audit
audit_dir="$(mktemp -d "$PWD/build/shadcn-audit/macos-tabs.XXXXXX")"
app_bundle="$audit_dir/TabsKeyboardAudit.app"
mkdir -p "$app_bundle/Contents/MacOS"
plutil -create xml1 "$app_bundle/Contents/Info.plist"
plutil -insert CFBundleExecutable -string TabsKeyboardAudit "$app_bundle/Contents/Info.plist"
plutil -insert CFBundleIdentifier -string com.toutiaoyan.compose.foundation.macos-tabs-audit "$app_bundle/Contents/Info.plist"
plutil -insert CFBundlePackageType -string APPL "$app_bundle/Contents/Info.plist"
xcrun swiftc -whole-module-optimization -parse-as-library -target "$(uname -m)-apple-macos14.0" -swift-version 6 \
  shadcn/iosApp/Sources/ComposeFoundationSwiftUI/*.swift shadcn/iosApp/validation/MacTabsKeyboardAudit.swift \
  -o "$app_bundle/Contents/MacOS/TabsKeyboardAudit"
for direction in ltr rtl; do
  for activation in automatic manual; do
    set --
    if [ "$direction" = rtl ]; then set -- "$@" --rtl; fi
    if [ "$activation" = manual ]; then set -- "$@" --manual; fi
    log="$audit_dir/$direction-$activation.log"
    open -F -n -W --stdout "$log" "$app_bundle" --args "$@"
    grep '^PASS: Tabs' "$log" || exit 1
  done
done
```

启动审计应用时传入 `--menus-interaction` 可打开 Menubar 和 Navigation Menu 测试页。上述 iOS 14 部署目标构建在 iPhone 17 Pro（iOS 26.2）上通过 `validation/verify_menu_keyboard.py`。实测 Menubar 触控打开后 Down 跳过禁用项，Return 执行选项，右键跳过禁用分组后切至 Edit，Escape 关闭后保留原操作结果。Navigation Menu 的右键从 Project 切至 Resources，Return 执行 User guide。

iPhone 紧凑布局在原生 sheet 的关闭回调里归还焦点。模拟器回归用硬件 Down 验证两类菜单的焦点归还。Menubar 从 File 切到 Edit 后按 Escape，再按 Down 会重开 Edit。Navigation Menu 从 Project 切到 Resources 后，Escape 或 Return 关闭，再按 Down 都会重开 Resources。每次关闭也会检查菜单项已经退出无障碍树。完全键盘控制服务开启时，系统可能接管横向键。

macOS 15.6.1 上，触发器栏和打开的菜单面板各持有自己的键盘焦点。两者记录方向键选定的活动项，按该项绘制焦点环并处理 Return。Menubar 和 Navigation Menu 都通过了 AppKit 完整回归。Down 跳过禁用项并执行 Save。Right 跳过禁用组切到 Edit，Escape 关闭后 Down 重开 Edit 并执行 Copy。在面板中切回 File 后可直接按 Return 执行 New。

使用 `validation/MacMenuKeyboardAudit.swift` 可独立复验。下例编译 macOS 14 及以上运行的审计应用，完整用例也检查整组禁用时关闭已打开的菜单，以及禁用期间的方向键和 Return。传入 `--actions-only` 可单独运行菜单内的禁用项跳过用例。

```sh
mkdir -p build/shadcn-audit
audit_dir="$(mktemp -d "$PWD/build/shadcn-audit/macos-menu.XXXXXX")"
app_bundle="$audit_dir/MenuKeyboardAudit.app"
mkdir -p "$app_bundle/Contents/MacOS"
plutil -create xml1 "$app_bundle/Contents/Info.plist"
plutil -insert CFBundleExecutable -string MenuKeyboardAudit "$app_bundle/Contents/Info.plist"
plutil -insert CFBundleIdentifier -string com.toutiaoyan.compose.foundation.macos-menu-audit "$app_bundle/Contents/Info.plist"
plutil -insert CFBundlePackageType -string APPL "$app_bundle/Contents/Info.plist"
xcrun swiftc -whole-module-optimization -parse-as-library -target "$(uname -m)-apple-macos14.0" -swift-version 6 \
  shadcn/iosApp/Sources/ComposeFoundationSwiftUI/*.swift shadcn/iosApp/validation/MacMenuKeyboardAudit.swift \
  -o "$app_bundle/Contents/MacOS/MenuKeyboardAudit"
open -F -n -W --stdout "$audit_dir/menubar.log" "$app_bundle"
open -F -n -W --stdout "$audit_dir/menubar-rtl.log" "$app_bundle" --args --rtl
open -F -n -W --stdout "$audit_dir/navigation.log" "$app_bundle" --args --navigation
open -F -n -W --stdout "$audit_dir/navigation-rtl.log" "$app_bundle" --args --navigation --rtl
for mode in menubar menubar-rtl navigation navigation-rtl; do
  grep '^PASS' "$audit_dir/$mode.log" || exit 1
done
```

启动审计应用时传入 `--overlay-focus` 可打开 Dialog 和 Sheet 测试页。在 iOS 完全键盘控制服务运行时，通过 `validation/verify_overlay_focus.py` 复验初始关闭焦点、Return 和 Escape 两种关闭方式，以及关闭后按 Space 从同一个触发器重新打开。用重新打开的结果判断系统焦点，关闭次数用于核对每次关闭只触发一次回调。

启动审计应用时传入 `--drawer-focus`，再运行 `validation/verify_drawer_focus.py`。在 iPhone 17 Pro 模拟器开启完全键盘控制后，Return、Escape、点击遮罩和拖动把手均能关闭 Drawer，每次关闭只触发一次回调。测试页通过只在触发器有焦点时处理的 D 键确认焦点已归还。打开时后台动作从无障碍树隐藏，点遮罩时后台动作计数保持为零。

macOS 15.6.1 上，共用关闭按钮接受普通键盘焦点后，Drawer、Dialog 和 Sheet 均通过初始 Return、Escape、单次关闭回调及触发器焦点归还回归。Drawer 的无障碍窗口树直接确认初始焦点位于 Close。窗口鼠标检查确认向上拖动保持打开，向下拖过阈值关闭一次，后台动作计数保持为零。一次短距离快拖也触发了关闭。慢速短拖的回弹和鼠标关闭后的触发器键盘焦点仍待复验。

下例运行 macOS 三类弹层的键盘回归。

```sh
mkdir -p build/shadcn-audit
audit_dir="$(mktemp -d "$PWD/build/shadcn-audit/macos-presentation.XXXXXX")"
app_bundle="$audit_dir/PresentationFocusAudit.app"
mkdir -p "$app_bundle/Contents/MacOS"
plutil -create xml1 "$app_bundle/Contents/Info.plist"
plutil -insert CFBundleExecutable -string PresentationFocusAudit "$app_bundle/Contents/Info.plist"
plutil -insert CFBundleIdentifier -string com.toutiaoyan.compose.foundation.presentation-focus-audit "$app_bundle/Contents/Info.plist"
plutil -insert CFBundlePackageType -string APPL "$app_bundle/Contents/Info.plist"
xcrun swiftc -whole-module-optimization -parse-as-library -target "$(uname -m)-apple-macos14.0" -swift-version 6 \
  shadcn/iosApp/Sources/ComposeFoundationSwiftUI/*.swift shadcn/iosApp/validation/MacPresentationFocusAudit.swift \
  -o "$app_bundle/Contents/MacOS/PresentationFocusAudit"
for kind in drawer dialog sheet; do
  open -F -n -W --stdout "$audit_dir/$kind.log" "$app_bundle" --args "$kind"
  test "$(grep -c '^PASS:' "$audit_dir/$kind.log")" -eq 2 || break
done
```

启动 iOS 审计应用时传入 `--popover-interaction` 可打开 Popover 测试页。在 iPhone 17 Pro 模拟器上，已确认无箭头面板显示在触发器下方，无障碍树包含内容和关闭按钮。点击关闭按钮、点击面板外部或发送硬件 Escape 后，触发器重新出现在无障碍树中。macOS 实现已通过类型检查。

启动 iOS 审计应用时传入 `--popover-focus`，再运行 `validation/verify_popover_focus.py`。在开启完全键盘控制的 iPhone 17 Pro 模拟器上，面板内的 `FocusState` 获取初始焦点，Return、Escape、点外侧和点关闭按钮均各触发一次 `onDismiss`。测试页用仅由已聚焦触发器处理的 P 键复验重新打开。macOS 15.6.1 修复了子面板显示后仍未取得键盘焦点的问题。独立 AppKit 测试通过实际窗口派发按键事件，验证了面板和内容取得初始焦点、Escape 和 Return 各触发一次关闭回调、父窗口及原触发器恢复焦点，以及按 P 重新打开。macOS VoiceOver 播报待验。

上面构建的 macOS 审计程序也接受 `--drawer-focus` 和 `--popover-focus`，可打开同一套焦点测试页。两种模式均已通过 macOS 11 部署目标构建，实际按键和焦点行为可在 macOS 14 及以上验证。

在活动的 macOS 图形会话中运行 Popover 焦点回归。两次运行都在日志中输出 `PASS`。

```sh
audit_dir=$(mktemp -d)
app_bundle="$audit_dir/PopoverFocusAudit.app"
mkdir -p "$app_bundle/Contents/MacOS"
plutil -create xml1 "$app_bundle/Contents/Info.plist"
plutil -insert CFBundleExecutable -string PopoverFocusAudit "$app_bundle/Contents/Info.plist"
plutil -insert CFBundleIdentifier -string com.toutiaoyan.compose.foundation.macos-popover-audit "$app_bundle/Contents/Info.plist"
plutil -insert CFBundlePackageType -string APPL "$app_bundle/Contents/Info.plist"
xcrun swiftc -whole-module-optimization -parse-as-library -target arm64-apple-macos14.0 -swift-version 6 \
  shadcn/iosApp/Sources/ComposeFoundationSwiftUI/*.swift shadcn/iosApp/validation/MacPopoverFocusAudit.swift \
  -o "$app_bundle/Contents/MacOS/PopoverFocusAudit"
open -F -n -W --stdout "$audit_dir/escape.log" "$app_bundle"
open -F -n -W --stdout "$audit_dir/return.log" "$app_bundle" --args --return-only
grep '^PASS' "$audit_dir/escape.log" && grep '^PASS' "$audit_dir/return.log"
```

启动 iOS 审计应用时传入 `--toast-interaction`，再使用 `validation/verify_toast_queue.py` 验证显示上限、关闭后补位、仅关闭指定通知、Undo 回调、单条超时和全部清空。测试通过无障碍标签定位并实际点击按钮。iPhone 17 Pro 模拟器也检查了深浅色外观。macOS 另外运行了队列状态测试，验证隐藏通知可以到期，手动关闭和清空后的旧定时器不会影响新通知。

Calendar 和 Checkbox 的状态回归可单独运行。测试覆盖禁止选择范围外日期、反向区间、跨夏令时区间、完成后的重选、同日多选去除和三态激活。
两项新状态已在 iPhone 17 Pro 模拟器和 macOS 的深浅色样例中复验。单独安装 Calendar 或 Checkbox 后，均可通过 iOS 14 模拟器目标类型检查。

启动 iOS 审计应用时传入 `--calendar-navigation`，再运行 `validation/verify_calendar_navigation.py`。已通过 iPhone 17 Pro 模拟器触控验证纵向双月、跨月选区、月份和年份菜单、边界外日期及翻页禁用。macOS 上运行了导航状态回归，并在 740 pt 容器中检查了浅色和深色的并排双月渲染。

启动 iOS 审计应用时传入 `--carousel-interaction`，再运行 `validation/verify_carousel.py`。加入 `--carousel-rtl` 启动参数并给脚本传入 `--rtl` 可验收 RTL。iPhone 17 Pro 模拟器上，两种方向均确认页面在绑定改变前就随手势移动，短拖和到达边界时不翻页，按钮和外部绑定可翻页，未选中的页面不进入无障碍树，纵向滑动仍可滚动父视图。

macOS 审计应用也接受 `--carousel-interaction`，可附加 `--carousel-rtl` 打开相同的 RTL 样例。macOS 15.6.1 的 LTR 窗口实测确认鼠标左拖从第 1 页到第 2 页，右拖返回第 1 页。首尾越界拖动保持原页，外部绑定可以跳至第 3 页。滚轮能滚动父视图，同时保持页码。拖动中页面按钮未误触，稳定后无障碍树只提供当前页的操作。macOS 也运行了页高与越界绑定回归。鼠标按住期间的逐帧位移及 macOS RTL 鼠标路径仍待采样。

启动 iOS 审计应用时传入 `--theme-presets`，再运行 `validation/verify_theme_presets.py` 验证深浅色下切换八套预设、按钮尺寸和触控、切换前后保存状态。macOS 渲染了 16 组预设与深浅色的组合，并检查了自定义配色、圆角覆盖和旧初始化器的默认值。打包检查确认全部 69 项 SwiftUI registry 都包含同一份主题源码。

```sh
xcrun swiftc -parse-as-library -target arm64-apple-macos11.0 -swift-version 6 \
  shadcn/iosApp/Sources/ComposeFoundationSwiftUI/FoundationTheme.swift \
  shadcn/iosApp/Sources/ComposeFoundationSwiftUI/FoundationCalendarNavigation.swift \
  shadcn/iosApp/Sources/ComposeFoundationSwiftUI/FoundationCalendarSelection.swift \
  shadcn/iosApp/Sources/ComposeFoundationSwiftUI/FoundationCheckbox.swift \
  shadcn/iosApp/validation/SelectionAudit.swift -o /tmp/compose-foundation-selection-audit
/tmp/compose-foundation-selection-audit
```
