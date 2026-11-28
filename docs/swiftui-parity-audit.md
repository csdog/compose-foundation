# SwiftUI 与 Compose 组件逐项核对

核对基准是本仓库 `/r/registry.json` 的 69 个条目，以及 `shadcn/shared` 中对应的 Compose 实现。三份 registry 使用相同的条目名。下表记录 SwiftUI 源码具有的行为，以及与 Compose 版有实质区别的能力。Apple 原生控件的样式、键盘行为和系统弹层适配由运行系统决定。

本轮使用 shadcn CLI 4.23.0 从本地 HTTP 安装三个目录各自的全部条目，并核对各目录的名称和安装文件。已安装的 Swift Package 通过 macOS 构建，并逐项通过 iOS 14 模拟器目标类型检查。在 macOS 和 iOS 26.2 的 iPhone 17 Pro 模拟器上，各渲染 69 个条目的深浅两种外观，共 276 张非空截图。

在 Pixel 模拟器上打开成品 APK 的安装页，确认 Jetpack Compose 和 SwiftUI 命令可见。验证了状态栏在深浅色下的前景与背景，以及键盘打开时文档搜索输入框仍保持可见和焦点。

Web 兼容分发在 Chromium 的 Wasm 路径和 JS 回退路径均显示原生安装选项。实际点击复制按钮核对了两端命令。Date Picker 的 Copy Page 同时包含 Calendar 和 Popover。两项 JVM 文档回归测试通过。

## shadcn 视觉对齐范围

SwiftUI registry 当前有 69 个条目。基准仓库为其中 63 个条目提供 shadcn 截图。chip、emoji-text、icon、stepper、text、theme 缺少直接参考截图，现有原生样例继续用各自功能验收。各条目的视觉审核按变体、状态和主题记录。[shadcn 基准索引](https://github.com/csdog/compose-foundation-baselines/blob/b19fcbf62f182422a5f26d7b8ce3dd8006695858/shadcn/Index.md)

### Tabs 和 Button 初始态

Button 的字号由 `Size` 决定，省略尺寸时走主题字体，Sera 使用 caption / semibold。Nova Tabs 保留组件字号，祖先的 `.font` 不会改变标签样式。截图用例使用组件实际的 SwiftUI 字体，Web 侧保留 Geist。


- Tabs 使用 `tabs-disabled.tsx`。Home 为选中项，Disabled 保持禁用，方向键仍跳过禁用项。Web 的 Disabled 焦点环示例与这一规则不同，后续键盘视觉对照使用可聚焦项。
- Button 使用现有 `button-variants-initial` 参考，覆盖描边文字按钮和向上箭头图标按钮。两个控件均保留 44 pt 高的点按范围，图标按钮的宽度也至少为 44 pt。
- 比对使用 402 × 520 共用画布，每个用例分别采集浅色和深色。

iPhone 17 Pro（iOS 26.2）上两轮各四张原图逐文件一致。复验确认默认深色 Tabs 的选中填充为 `#2f2f2f`。shadcn 和 SwiftUI 在文字抗锯齿及轮廓边缘仍有像素差异。macOS Swift Package 构建通过。[验证运行](https://github.com/csdog/compose-foundation/actions/runs/45993072773) · [四张原图及差异](https://github.com/csdog/compose-foundation/actions/runs/45993072773/artifacts/15194918019)

### Badge 和 Separator 初始态

Badge 对照官方 Nova 示例的 Default、Secondary、Destructive、Outline 整行。Separator 对照标题、分隔线和下方两行正文。需要逻辑像素对照时，从上游原图分别以 x=159、x=6 裁取 402 × 520 区域，完整保留所有可见内容。

iPhone 17 Pro（iOS 26.2）上浅色和深色各拍两遍。四张原图逐文件一致。差异主要集中在文字边缘、圆角抗锯齿及正文基线。macOS Swift Package 构建通过。[验证运行](https://github.com/csdog/compose-foundation/actions/runs/45994534289) · [四张实拍及 shadcn 对照](https://github.com/csdog/compose-foundation/actions/runs/45994534289/artifacts/15196185208)

## 各条目

| 条目 | SwiftUI 实现与差异 |
| --- | --- |
| accordion | 支持单开或多开、禁用项、绑定展开集合。Compose 另有横向布局和独立组装的触发器、面板。 |
| alert | 标题、说明、图标、操作和 destructive 样式齐备。Compose 可用多个独立 slot 自定义排布。 |
| alert-dialog | 原生确认框支持取消、确认和 destructive 操作。Compose 的自定义面板、媒体、页脚和叠层由系统确认框替代。 |
| aspect-ratio | 固定宽高比、裁剪，非法比例回退到 1:1。窄容器和无界布局的精确测量规则需另行比较。 |
| attachment | 有上传进度、处理、失败和完成状态，并支持移除。缺少 Compose 的尺寸、纵向布局、附件组与预览触发器。 |
| avatar | 支持本地 `Image` 和姓名首字母回退。缺少内建异步 URL 加载、错误状态、徽标和头像组。 |
| badge | 支持 primary、secondary、outline、destructive。缺少 Compose 的 ghost/link 样式与图标位置参数。 |
| breadcrumb | 支持水平路径、链接、当前页和分隔符。缺少可配置分隔符、ellipsis 与自定义页面 slot。 |
| bubble | 支持 incoming、outgoing、muted 内容气泡。缺少气泡组及定位在气泡边缘的 reactions。 |
| button | 六种颜色变体、禁用、按压反馈和内容 slot。Compose 另有尺寸、图标位置、焦点环和多种按压动画。 |
| button-group | 支持纵向和横向布局。Compose 会合并邻接圆角、协调子项焦点层级，并提供分隔符与文本项。 |
| calendar | 支持本地化单月和连续多月，窄布局纵向排列，宽布局并排。支持日期单选、区间、多选、月份和年份菜单。日期上下限约束菜单和翻页，区间跨月时保留中间日期的选中样式。Compose 另有独立网格 slots。 |
| card | 支持主题背景、边框和内容。Compose 的 header/footer/image/action slots、尺寸与卡片变体需由 SwiftUI 调用方组装。 |
| carousel | 当前页和相邻页随横向拖动连续移动，松手后吸附到当前或相邻页。支持 RTL、前后按钮、页码无障碍调节和减少动态效果。视口高度由当前页决定。Compose 另有纵向模式、部分页宽及 dots API。 |
| chart | 支持柱形、折线、面积、多序列和选点摘要。Compose 的独立图例、工具提示、序列主题配置和 indicator 选项未移植。 |
| checkbox | 支持未选中、已选中和部分选中。部分选中使用减号、独立无障碍值和点按转全选。保留原有 Bool ToggleStyle API，支持 SwiftUI 提供的混合状态。 |
| chip | 可切换选中状态，支持内容标签和选中语义。缺少 Compose 的独立 chip 样式变体。 |
| code-block | 支持横向滚动、语言标签和行号。缺少复制按钮、行状态、页脚和操作栏。 |
| collapsible | 原生 DisclosureGroup 与展开绑定。Compose 的自定义 trigger slot、状态联动与展开动画参数未移植。 |
| combobox | 支持搜索、空态、禁用选项、分组、单选和可移除的多选 chips。iOS 模拟器和 macOS 已验证方向键跨组、跳过禁用项、Return 选择、Escape 关闭、多选、移除 chips 和搜索隐藏空分组。iOS 平铺模式此前还验证了软键盘提交和 VoiceOver 朗读。 |
| command | 支持分组过滤、空态、禁用项和回车选择首个可用结果。缺少对话框、加载状态、footer 和逐项快捷键。 |
| context-menu | 使用系统右键或长按菜单。调用方可传入系统菜单视图，Compose 的位置、portal、子菜单和条目 DSL 有独立 API。 |
| dialog | 原生 sheet 显示标题、说明、内容和关闭按钮。iOS 模拟器与 macOS 已验证 Return、Escape 和关闭后的触发器焦点。Compose 另有遮罩、portal、尺寸与页脚控制。 |
| drawer | 支持底部面板、遮罩点击、拖动关闭、减少动态效果和 `onDismiss`。iOS 模拟器与 macOS 已验证初始关闭焦点、Return、Escape 和键盘关闭后归还焦点。macOS 已验证向下拖动关闭和向上拖动边界。Compose 另有多方向、吸附点和背景缩进。 |
| dropdown-menu | 自定义外部触发器外观，菜单内容走 SwiftUI Menu。Compose 可控制菜单锚点、portal、焦点和自定义条目状态。 |
| emoji-text | Apple 系统彩色 emoji，可保留 ZWJ 等 Unicode 序列。Compose 使用自己的 emoji 图形与替换逻辑。 |
| empty | 支持符号、标题、说明和操作 slot。缺少 Compose 的默认/虚线框变体和说明换行策略。 |
| field | 纵向显示标题、说明、错误及任意输入。缺少 FieldSet/Legend、横向/响应式排列及 Compose 的分组语义。 |
| hover-card | 触发器和卡片有延迟打开、指针离开后延迟关闭，并支持触摸长按。Compose 另有共享 provider、位置和更完整的指针过渡控制。 |
| icon | 支持 SF Symbols、动态字号和可选无障碍标签。Compose 可传入任意 ImageVector 并选择图标变体。 |
| input | 原生单行输入，支持错误、禁用和编辑时的边框反馈。缺少密码/数字等内建类型和 Compose 样式变体。 |
| input-group | 横向 leading/content/trailing slots。Compose 还支持上下 addon、组合焦点及明确的 input/textarea/button slots。 |
| input-otp | 数字规整、定长槽位、iOS 自动填码。缺少 Compose 的字母数字输入类型、分组分隔符和逐槽状态控制。 |
| item | 支持媒体、标题、说明、操作。Compose 有尺寸、变体、群组和独立 header/footer。 |
| kbd | 显示单个键符及 themed 键帽。Compose 另有组合键分组 slot。 |
| label | 主题文本，支持禁用降权。Compose 的可点击、自定义内容和关联输入行为需要用原生可访问性 API 或外部容器提供。 |
| marker | plain、separator、border 三种文本标记。缺少 Compose 的独立图标和内容 slots。 |
| menubar | 分组入口支持共享打开状态、键盘移动、跳过禁用项、Return 执行和 Escape 关闭。iOS 26.2 模拟器已验证打开后的项目导航及跨菜单切换。Compose 另有子菜单和独立 radio/checkbox 条目。 |
| message | 收发对齐、作者、时间和内容。没有 Compose 的头像、消息组、独立 header/footer slots。 |
| message-scroller | 内容更新时跟随末尾、触摸拖动暂停、恢复跟随和到达顶部回调。缺少 Compose 的任意锚点寻址、可见 ID，以及对桌面滚轮的完整跟随判定。 |
| navigation-menu | 支持横向内容 slot、分组触发器和关联弹层。iOS 26.2 模拟器已验证打开后通过方向键切换导航组和以 Return 执行操作。Compose 另有独立 viewport 和 indicator API。 |
| pagination | 前后按钮、页码窗口、省略和当前页绑定。Compose 另有专门的首末按钮和内容 slots。 |
| popover | iOS 和 macOS 使用锚定的无箭头圆角面板，默认居中显示在触发器下方，间距 4 pt，并根据可用空间翻转或调整位置。`arrowEdge` 指定首选方向，`onDismiss` 在窗口关闭后调用。iOS 模拟器已验证面板内键盘焦点、Return、Escape、触控关闭和跨窗口还原触发器焦点。 |
| progress | 支持范围归一化、动画、减少动态效果与百分比无障碍语义。Compose 可插入 label、value、track 和 indicator。 |
| questionnaire | 单选、多选、文本题、验证、回退、跳过及提交可用。缺少 Compose 的快捷键、题定义 slots、重置与部分进度状态 API。 |
| radio-group | 纵向单选与禁用状态。Compose 另有横向布局和独立 RadioButton。 |
| resizable | 支持双窗格纵向/横向比例、拖动限制、RTL 和无障碍增减。拖动读取全局坐标，避免手柄移动改变手势参考点。Compose 另有专门的状态与面板 slots。 |
| scroll-area | 使用原生 ScrollView，支持滚动轴和指示器。缺少 Compose 的独立滚动条、显隐策略、lazy API 和拖动 thumb 配置。 |
| scroll-fade | 按所选边缘和固定距离加渐变遮罩。和 Compose 的静态 overload 对应，缺少按可滚动方向和偏移量驱动的 overload。 |
| select | 原生 Menu 支持单选、占位、选中图标、禁用项和原生分区标题。iOS 模拟器及 macOS 已验证分区、禁用状态、指针选择及无障碍值。Compose 另有尺寸、变体及不同面板位置。 |
| separator | 横纵向装饰性分隔线可用。边缘颜色和屏幕像素由 SwiftUI/theme 决定。 |
| sheet | 原生 sheet 支持标题、内容、Done、Escape 和 `onDismiss`。iOS 模拟器与 macOS 已验证初始关闭焦点、Return、Escape 和关闭后的触发器焦点。Compose 具备 top/end/bottom/start 方位与独立 header/footer/close slots。 |
| shimmer | 支持开关、穿过内容的光带、减少动态效果。Compose 另有文本遮罩及可调颜色、宽度、方向、时序。 |
| sidebar | 可开关侧栏，宽容器并排、窄容器遮罩。Compose 另有侧栏方位、collapsible/rail 策略、菜单 slots 和键盘快捷键。 |
| skeleton | 支持静态主题占位圆角。Compose 默认包含 pulse 动画。 |
| slider | 包装系统 Slider，支持范围和步长。Compose 的轨道、滑块外观、纵向布局和更多手势参数未移植。 |
| spinner | 系统不定进度控件及无障碍标签。Compose 提供原有旋转外观和 size 参数。 |
| stepper | 支持当前、已完成状态和点击回调。Compose 的横向排列、连接线与描述 slots 未移植。 |
| switch | 原生 SwiftUI 开关，使用主题 tint；尺寸和交互由平台决定。Compose 提供专有 size 和轨道样式。 |
| table | 支持标题、数据行、列宽、排序与水平滚动。Compose 有任意内容单元格、caption/footer 和独立表结构 slots。 |
| tabs | 支持选择绑定、禁用、横向 tabs 与内容切换。iOS 17 / macOS 14 起提供左右键、Home / End、禁用项跳过，以及自动或手动激活。iOS 26.2 模拟器已验证 LTR、RTL、跨组 Tab、长列表滚动、自动激活及手动模式的独立焦点与提交。macOS 15.6.1 已验证自动模式跳过禁用项、跨组 Tab 和手动激活。Compose 另有纵向、list variant 和独立焦点移动 API。 |
| text | 支持标题、headline、正文、caption 和 muted 文本。Compose 有更多主题字体角色及自定义内容和样式参数。 |
| textarea | 原生 TextEditor，多行绑定与错误边框齐备。Compose 的变体、placeholder/尺寸参数及自定义焦点样式未对齐。 |
| theme | SwiftUI 环境支持自定义深浅色、圆角及 Vega、Nova、Maia、Lyra、Mira、Luma、Sera、Rhea 八种样式。预设为通用圆角、按钮尺寸、卡片和 Popover 间距、部分字体和动效提供默认值。Compose 的配色集合、图表色及各组件状态样式仍有差别。 |
| toast | 支持原有单条通知和 `FoundationToastState` 队列。队列默认显示最新三条，支持深浅色、变体图标、可选操作、逐条关闭、清空和独立计时。面板目前纵向展开。堆叠展开、滑动手势、优先级播报和多操作仍待对齐。 |
| toggle | 可绑定选中、禁用及 slot 内容。Compose 还提供多种样式、尺寸和图标位置。 |
| toggle-group | 支持单选、多选、是否允许空选择、禁用。Compose 另有方向、附加状态与样式变体。 |
| tooltip | macOS 使用系统 help，iOS 使用长按提示和无障碍 hint。Compose 支持 provider、延迟、位置与更广的触发策略。 |
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

在 iPhone 17 Pro（iOS 26.2）上，两轮各 24 张 PNG 逐文件一致。展开图同时保留触发器、背景和完整面板。当时的候选图使用固定深色填充。当前描边触发器使用 `input.opacity(0.3)`，与 shadcn 的 `input/30` 配方一致。Web 和 iOS 的文字光栅化、圆角与阴影仍有像素差异，逐项差异图收录在审核附件。macOS Swift Package 构建通过。[验证运行](https://github.com/csdog/compose-foundation/actions/runs/45988767609) · [24 张实拍与 shadcn 对照](https://github.com/csdog/compose-foundation/actions/runs/45988767609/artifacts/15192533031)

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
open -F -n -W --stdout "$audit_dir/navigation.log" "$app_bundle" --args --navigation
grep '^PASS' "$audit_dir/menubar.log" && grep '^PASS' "$audit_dir/navigation.log"
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
