# Compose Foundation 代码审查进度

[审查计划与验收标准](code-review-plan.md)

审查基线为 `95c932b6`。两轮审查已结束，组件修复已分批提交。Detekt 构建配置、目录 JS 启动修复和导航监听器修复均已提交。其余导航改动及已有暂存内容保留。

## 第一轮

- [x] 盘点本地改动和基线验证结果。
- [x] 审查 Select 的弹层几何、滚动和键盘状态。
- [x] 审查 Toast 的队列、计时和焦点恢复。
- [x] 审查共享弹层与 Web 事件适配。

## 第二轮

- [x] 审查 Slider 的输入、焦点和平台语义。
- [x] 审查 Sheet、Toggle 的触摸焦点和平台适配。
- [x] 审查目录应用的浏览器滚动和导航，登记有重叠改动的待处理项。
- [x] 核对两轮的发现、验证结果和提交。

## 审查发现

每条记录写明文件与行号、影响、处理状态，以及对应的验证结果和提交。验收通过后勾选上方条目。

| 位置 | 发现及影响 | 处理状态 | 验证与提交 |
| --- | --- | --- | --- |
| `SelectPopupGeometry.kt:14`、`SelectPopupGrowth.kt:17`、`Select.kt:198` | 测量、增长和面板最大高度重复表达同一个窗口边距，容易在更新对齐参数时发生分歧。搜索超时和最小宽度也各有多个使用点。 | 已收拢规则 | `3effb8f4`。114 个 Select JVM 用例通过，包含分数密度、空间上限与输入模式。 |
| `Toast.kt:253` | 错误格式化回调抛异常时，原始加载提示永久驻留。三处默认超时也各自定义。 | 已修复 | `d1616329`。修复前测试实际得到 2 条消息，预期为 1 条。修复后两条新增回归均通过，覆盖原异常、无关消息和回调改写的新版本。 |
| `MenuPopup.kt:30`、`PopupPositionMetadataTest.kt:10` | 两条 Nova 路径重复定义过渡时间与滑动距离。弹层元数据测试也有既有 ktlint 格式问题。 | 已收拢参数并清理测试格式 | `1a2f20d6`。16 个共享弹层测试通过，保留各路径的挂载、取消和显式转换处理。 |
| `NovaToastViewport.kt:116` | 可见项先按消息排序，再逐项扫描视口条目查找相同 ID。 | 已复用有序列表 | `d1616329`。32 个 Toast 测试通过，覆盖可见上限、离场顺序、计时和焦点恢复。 |
| `Slider.kt:81`、`:104` | 控制器和根节点无障碍动作分别定义滑块间最小距离。键盘大步进使用了几个语义不同的数字。 | 已共享间距规则并命名步长 | `50f33ddb`。23 个 Slider 测试通过，保留 Web 绝对大步进及其他平台的步幅倍数。 |
| `TouchCanvasFocus.kt:15`、`Sheet.kt:252`、`Toggle.kt:147` | 两处都在指针释放的 Main 阶段向点击处理器暂存触摸信息，且各自负责取消清理。 | 已共享触摸释放生命周期 | `cd15c448`。37 个 Sheet、15 个 Toggle 测试通过。Sheet 保留原生触摸识别，Toggle 仅在捕获到恢复动作时恢复焦点。Slider 拖动与 Popover 非模态交互保留独立路径。 |
| `app/webApp/src/webMain/kotlin/com/toutiaoyan/compose/ui/main.kt` 的 `bindAppRouterToBrowser` | 路由绑定直接写入和清空 `window.onpopstate`，覆盖宿主已有处理器。 | `704a785b` 改为成对注册与移除，保留路由回调顺序。 | 以仅含正则修复的父提交为对照。JS/Wasm Chromium 均复现宿主原回调丢失，最终提交保留原回调并在前进后退时调用。对照与最终版的滚动恢复位置一致。[修改前](../.agent-runs/cursor-review-goal-20261118/delivery/browser-parent-79145449.json) · [修改后](../.agent-runs/cursor-review-goal-20261118/delivery/browser-final-704a785b.json) |
| `app/shared/src/commonMain/kotlin/com/toutiaoyan/compose/ui/catalog/docs/InlineDocCode.kt:216` | 字符类的右方括号在 JS Unicode 正则中需要转义，旧版目录启动时报错。 | `79145449` 提交已有的单行转义修复。 | 原始 JS 产物报 `SyntaxError`。父提交及最终版的 JS 目录均能挂载并执行历史回归。[启动复现](../.agent-runs/cursor-review-goal-20261118/clean-candidate.json) · [父提交浏览器结果](../.agent-runs/cursor-review-goal-20261118/delivery/browser-parent-79145449.json) |
| `build.gradle.kts` 的 Detekt 配置 | 核心模块原先以 `NO-SOURCE` 跳过，读到 0 个输入文件。 | 已配置 Gradle 扫描 KMP 源码，并为存量问题生成模块 baseline | 检查和 baseline 生成使用同一份源码范围。详见下方 Gradle Detekt 记录。 |

## 基线检查

- 首轮工作区基线为 160 个 JVM 测试通过。Select 114 个，Toast 30 个，共享弹层 16 个。
- 全模块 ktlint 基线报出 `PopupPositionMetadataTest.kt` 已有 9 项格式告警。Detekt 首次离线运行缺少 `detekt-formatting:1.23.7` 依赖。
- 目录测试首次编译遇到 JVM 堆不足，改用隔离输出与更大的堆后通过。最终 294 个 JVM 测试通过，包含 237 个组件及 57 个目录相关用例。核心模块 ktlint、目录共享源码与 JVM 测试 ktlint、Web 源码 ktlint 通过。JS 与 Wasm 分别生成 49 和 45 个产物文件。
- Detekt 1.23.7 CLI 使用仓库配置和同版本格式插件。以 `95c932b6` 对比本轮所改的 12 个文件，告警从 604 条降到 573 条，无新增的按文件和规则统计项。全模块报告仍有既有告警，包括 Select、Toast 的长函数。保留现有组合生命周期与测试覆盖，避免仅为降低行数而拆分状态所有者。[本地 Detekt 记录](../.agent-runs/code-review-95c932b6/detekt-summary.json)
- [本地 JVM、ktlint 和构建证据](../.agent-runs/code-review-95c932b6/native-and-distributions.json)

## Gradle Detekt 跟进

- Cursor 修改 Gradle 配置后，检查和 baseline 生成共用各模块的 `src/**/*.kt`，排除生成目录。移除 Detekt 格式插件，缩进、换行、修饰符顺序和导入格式由 ktlint 配置负责。
- 以 `0989319b` 的已提交源码生成 baseline。7 个模块共扫描 939 个 Kotlin 文件，5 个模块记录了 5,408 个存量问题 ID。CI 沿用 `ktlintCheck detekt --continue`，`maxIssues: 0` 会拦截 baseline 之外的 Detekt 问题。用 `detektBaseline` 生成初始清单，日常 CI 只读取该清单。[源码覆盖](../.agent-runs/code-review-95c932b6/detekt-gradle/baseline-coverage.json) · [存量问题统计](../.agent-runs/code-review-95c932b6/detekt-gradle/baseline-summary.json)
- 隔离副本运行 CI 同款命令通过。临时在 `iosMain` 中引入一条 `MagicNumber`，Gradle Detekt 报出该告警并失败。放在 `generated` 中的另一条告警被排除。两个临时文件均已移除。[新增告警试验](../.agent-runs/code-review-95c932b6/detekt-gradle/probe.xml)
- 清理临时文件后，核心 Detekt 重新扫描 553 个文件并通过。相关 JVM 测试共 418 个通过，包括 236 个组件测试及 182 个目录测试。[Detekt 复测](../.agent-runs/code-review-95c932b6/detekt-gradle/final-coverage.json) · [JVM 结果](../.agent-runs/code-review-95c932b6/detekt-gradle/jvm-scoped.json)
- 核心模块全量 JVM 运行 1,698 个测试，Select 焦点恢复、Checkbox 动画中间帧以及 Switch 浅色和深色动画反向各有一项失败。用原构建配置单独重跑这四项，复现相同断言失败。[全量结果](../.agent-runs/code-review-95c932b6/detekt-gradle/jvm-full.json) · [改动前对照](../.agent-runs/code-review-95c932b6/detekt-gradle/jvm-control.json)
- 已有的本地源码改动额外引入 33 个 Detekt 问题 ID，仍会触发检查。上述 baseline 仅依据已提交源码生成。

## Detekt 三批存量清理

| 提交 | 文件与影响 | 处理结果 |
| --- | --- | --- |
| `807706d8` | `SelectPopupGeometry.kt:34`、`Select.kt:201`、`SelectScrollTarget.kt:6` | 分离对齐和浮动两条计算路径，共用滚动箭头预留高度，明确连续滚动间隔。 |
| `807706d8` | `MenuPopup.kt:33`、`Toast.kt:64`、`NovaToastViewport.kt:76` | 共用弹层缩放计算，保留原浮点运算与缓动曲线。给 Toast 关闭等待时间命名，符合已有常量规范。整批移除 8 个 ID。[第一批 Detekt 对照](../.agent-runs/code-review-95c932b6/detekt-cleanup/batch-1.json) |
| `807706d8` | `ChoiceNovaParityTest.kt:215` | 原测试在提交选项前错误地断言焦点跳过禁用项。新用例覆盖 Nova 约定：禁用项可获焦，Enter 无效，选中启用项后焦点回到触发器。原配置的失败和新用例的通过均有 JVM 记录。 |
| `25dcd3b4` | `Sheet.kt:101`、`Slider.kt:82` | 明确面板宽度、断点和动效单位，保留 Slider 整控件禁用透明度。Toggle 的参数和交互实现保留，单纯拆分欠缺收益。移除 6 个 ID。[第二批 Detekt 对照](../.agent-runs/code-review-95c932b6/detekt-cleanup/batch-2.json) |
| `e4cb3b27` | `Calendar.kt:97`、`CalendarLocale.kt:36`、`Questionnaire.kt:563`、`Sidebar.kt:322` | 统一日历周索引和边界，删除无引用的私有 Gregorian 月长计算。问卷快捷键上限取自实际键表，Sidebar 动画时长有明确单位。保留公开参数和日历算法查找表。移除 22 个 ID。[第三批 Detekt 对照](../.agent-runs/code-review-95c932b6/detekt-cleanup/batch-3.json) |

- 核心模块扫描 553 个 Kotlin 文件，问题 ID 从 4,583 降至 4,547，全仓 baseline 从 5,408 降至 5,372。新增 ID 为零。未采用会搬移复杂度告警或改变浮点边界判断的候选改写。[Detekt 前后对照](../.agent-runs/code-review-95c932b6/detekt-cleanup/baseline-delta.json)
- 核心 `detekt` 与 `ktlintCheck` 通过。相关 JVM 全部通过，共 394 个组件用例和 182 个目录用例。产品交互与视觉值保持原语义，本次验证使用现有 JVM 和快照用例。Checkbox 中间帧和 Switch 两项动画反向仍是已记录的全量 JVM 基线问题。[Gradle 结果](../.agent-runs/code-review-95c932b6/detekt-cleanup/gradle.json) · [JVM 统计](../.agent-runs/code-review-95c932b6/detekt-cleanup/jvm.json)
- DropdownMenu 独立清理已提交为 `0eb6a4f1`。在 `DropdownMenu.kt` 的复选项与单选项之间共用标签渲染，保留各自的语义、指示器和点击行为。子菜单悬停关闭延迟仍为 100 ms，由具名常量明确单位。子菜单与锚点的 -3 dp 对齐偏移保留现有 Base UI 默认值，公开参数保留现有调用契约。
- 相同 553 个 Kotlin 文件的 Detekt 对照确认移除复选项 `LongMethod` 与悬停延迟 `MagicNumber` 两个存量 ID，零新增。核心 baseline 降至 4,545，全仓降至 5,370。核心 `detekt`、`ktlintCheck` 和 44 项菜单 JVM 测试通过，包含 8 项现有快照。[清理与验证结果](../.agent-runs/code-review-95c932b6/detekt-cleanup/dropdown-menu.json)

## DropdownMenu 复审

- 核查 `DropdownMenu.kt` 中字符串触发器的键盘事件消费、非模态 Escape 的关闭顺序、Nova 弹层在退出中重开的焦点处理，以及 Menubar 与 ContextMenu 共用的弹层和样式。当前源码的本地复审未确认新回归。
- 从与当前工作区 1,650 个输入一致的隔离副本运行。DropdownMenu 和共享弹层 57 项、目录示例 22 项、Menubar 和 ContextMenu 45 项 JVM 测试通过。`:shadcn:core:ktlintCheck` 通过。前次完整浏览器与截图验收后，部分源码哈希已有变化。[前次验收](web-parity/dropdown-menu-acceptance-20261113/README.md)
- JS 与 Wasm 目录应用构建通过。Chromium 四组用例覆盖两个运行时的普通按钮和头像按钮。两种按钮均可用鼠标打开，按 Escape 关闭后可用 Enter 和 Space 从原按钮重新打开。普通按钮另验证了 ArrowDown 和 ArrowUp。Wasm 用例加载了应用 Wasm，四组均无页面异常。[浏览器结果](../.agent-runs/code-review-95c932b6/dropdown-menu-chromium.json)
- 提交 `0eb6a4f1` 后，复扫与工作区一致的隔离副本，核心 Detekt 仍报告 25 条 baseline 之外的告警。DropdownMenu 实现和两份测试占 11 条，另有 Menubar、ContextMenu、Empty 和 Text 的告警。保留当前 baseline 与规则阈值，逐条位置见报告。[Detekt 结果](../.agent-runs/code-review-95c932b6/dropdown-menu-detekt.json)

## Empty、Text 和 Menubar 的工作区告警

- `EmptyDescriptionWrap.kt` 把两次二分搜索集中到同一边界查找，分离缓存与候选选择，明确原有长度、行数和测量次数限制。`EmptyBorder.kt` 用可空描边颜色表达是否描边，保留绘制层次。`EmptyDescriptionWrapTest.kt` 将布置和断言分开。
- `Text.kt` 把段落修饰符与 BasicText 绘制分开，保留纯字符串走引擎继承排版的条件。原本因函数级注解变化而失配的两个 ID 恢复匹配。`Menubar.kt` 简化悬停切换条件，Web 的 `MenubarPointerSurface.kt` 保留最近的有效 Canvas。
- 与同范围的 573 份 Kotlin 源码对照，核心工作区的 baseline 外告警从 25 降到 12，零新增，正式 baseline 未修改。余下 8 条在 DropdownMenu 实现，4 条在 DropdownMenu 和 ContextMenu 测试。7 个文件原有改动仍在工作区，清理随相应组件改动保留。[Detekt 前后对照](../.agent-runs/code-review-95c932b6/core-wip-detekt-cleanup.json)
- 核心 `ktlintCheck`、76 个相关 JVM 用例（其中 26 个快照用例）与两种 Web 构建通过。Chromium 在 JS 和 Wasm 中均通过了 Menubar 打开后的悬停切换和 Escape 关闭。Empty 的默认与 RTL 示例在两个运行时分别取得相同的文本边界和截图哈希。[浏览器结果](../.agent-runs/code-review-95c932b6/core-wip-chromium.json)

## 目录交付复核

- 最终提交 `704a785b` 的 1,614 个输入与验证副本一致。JS/Wasm 双构建、两个目录模块的 ktlint 和 Detekt，以及 27 个目录 JVM 测试通过。两模块 Detekt 报告均为零。[输入核对](../.agent-runs/cursor-review-goal-20261118/delivery/committed-inputs.json) · [验证记录](../.agent-runs/cursor-review-goal-20261118/delivery/verification.json)
- 本地 `app/webApp/build.gradle.kts` 的字体生成任务改为先注册再配置。ktlint 通过，重新生成的 Kotlin 文件逐字节一致。该格式修正随原本的字体配置改动留在工作区。[局部补丁](../.agent-runs/cursor-review-goal-20261118/delivery/local-web-build-format.patch)

## 当前目录导航复核

- `app/webApp/src/webMain/kotlin/com/toutiaoyan/compose/ui/BrowserDocHistory.kt:109` 在普通 HTTP 主机上调用仅在安全上下文可用的 `crypto.randomUUID()`，导致目录初始化失败。现根据运行时能力选择 UUID 或四个随机 32 位值，历史条目仍使用不透明字符串。兼容修复与历史条目及滚动的直接依赖已经提交为 `c3760ac6`。
- Chromium 使用同一套路由与断言对照。原产物的 JS 和 Wasm 在 `http://catalog.test` 都报 `TypeError`。修复后两个产物在普通 HTTP 和 localhost 共四组都成功挂载。每组均确认片段条目 ID 与原条目不同，后退和前进分别恢复对应的 ID。另在 localhost 复核了锚点滚动与宿主 `popstate` 处理器。对应 JS/Wasm 构建、Web 和共享目录的 `ktlintCheck` 均通过。[修复前](../.agent-runs/code-review-95c932b6/catalog-http-before.json) · [修复后](../.agent-runs/code-review-95c932b6/catalog-http-after.json) · [滚动和监听器](../.agent-runs/code-review-95c932b6/catalog-current-history.json)
- `BrowserDocHistory.kt` 合并历史条目的空值与所有权检查，仅在直接调用内嵌 JS 的函数上标注 Detekt 的 `UnusedParameter` 误报。8 个参数均在 JS 表达式中使用。`main.kt` 单独判断页面位置变化。`BrowserDocumentScrollHost.kt` 分离 Canvas 选择与原生事件监听。`DocumentScrollBridge.kt` 明确半个 Compose 像素的容差，原顺序决定是否保留恢复位置。
- 原工作区的 Web 目录模块 11 条、共享目录模块 2 条告警均已消除；两模块的 Detekt 与 ktlint 通过，baseline 未改动。24 项导航和滚动 JVM 测试、最终 JS/Wasm 构建和上述 Chromium 回归通过。构建与工作区的 1,648 个输入逐文件一致。目录导航清理已归入 `c3760ac6`。Empty 链接的原生 Space 适配随已有 Empty 改动保留。[原告警](../.agent-runs/code-review-95c932b6/catalog-local-detekt.json) · [复测结果](../.agent-runs/code-review-95c932b6/catalog-cleanup-verification.json)

## 菜单清理与最终验证

- `DropdownMenu.kt` 集中处理首项、末项与指针打开的焦点意图，把根面板和子面板的关闭按键与布局分开。保留菜单退出中重开时的状态观察位置、焦点返回和最深层优先关闭顺序。两个公开面板签名及默认表达式与本轮修改前逐字一致。
- `DropdownMenuLiveStyleTest.kt`、`DropdownMenuArabicLayoutTest.kt` 和 `ContextMenuPanelTest.kt` 分开测试布置、测量和断言，保留原断言及诊断输出。剩余 12 条告警中，10 条通过代码清理消除。另两条公开参数告警仅配对更新原 baseline ID，对应现有默认过渡引用变化。相同源码范围内零新增原始问题 ID。
- 确认 6 个旧 baseline ID 在当前改动中已解决，核心 baseline 从 4,545 降到 4,539，全仓从 5,370 降到 5,364。仓库级 `ktlintCheck` 和 `detekt` 通过。强制重跑确认七个含源码的任务均实际执行，共 969 个 Kotlin 文件，核心模块覆盖 9 个源集和 573 个文件。根目录下两个源代码为空的聚合项目按预期跳过。[ID 对照与 JVM 结果](../.agent-runs/code-review-95c932b6/menu-final-detekt-and-jvm.json) · [扫描覆盖](../.agent-runs/code-review-95c932b6/final-detekt-coverage.json)
- 与当前工作区逐文件一致的 1,648 个输入通过 444 项核心 JVM 回归、1,513 项目录共享模块全量 JVM 测试，以及 JS/Wasm 双构建。Chromium 在两种运行时均验证了 DropdownMenu 关闭后键盘重开、Menubar 悬停切换和 Shift+Tab 返回子菜单触发器、Escape 按层关闭，以及普通 HTTP 下目录启动和历史恢复。[DropdownMenu](../.agent-runs/code-review-95c932b6/final-dropdown-keyboard.json) · [Menubar](../.agent-runs/code-review-95c932b6/final-menubar-nested.json) · [目录 HTTP](../.agent-runs/code-review-95c932b6/final-catalog-http.json)
- 验证以当时的工作区为准。下方交付验收记录随后拆出的独立提交，以及与既有组件变更一起保留的结构清理。

## Chromium 实测

- Select 在最终的 JS/Wasm 构建中验证 DPR 2 下的弹层高度、外部点击和滚轮锁定。Toast 在两种运行时验证桌面和触屏交互，覆盖队列、计时与焦点。
- Slider 验证键盘、触屏和滑块值语义。Sheet 验证鼠标与触屏打开时的初始焦点及 Tab 后编辑。Toggle 验证键盘、触屏取消、RTL 和 `aria-pressed`。目录测试验证锚点与前进后退，并复现上表的宿主监听器问题。
- 22 个浏览器配置的断言全部通过，其中 2 个用于复现已登记的目录缺陷。服务响应与最终分发产物哈希一致，Wasm 用例实际加载了应用 Wasm。[浏览器结果](../.agent-runs/code-review-95c932b6/browser-summary.json) · [产物响应核对](../.agent-runs/code-review-95c932b6/browser-provenance.json)

## 交付验收

- `c3760ac6` 将 HTTP 修复、浏览器历史、滚动和直接依赖作为 14 个文件的独立目录提交。普通 HTTP 的失败与恢复证据见当前目录导航复核。拆分后再次验证 JS/Wasm、普通 HTTP 启动、历史条目 ID、锚点滚动位置和宿主 `popstate` 回调。24 项目录 JVM 测试通过。首次合并构建的 JVM 编译耗尽堆空间，单 worker 重试通过。[普通 HTTP](../.agent-runs/code-review-95c932b6/committed-catalog-http.json) · [滚动与监听](../.agent-runs/code-review-95c932b6/committed-catalog-history.json)
- `ac979ed1` 独立提交 DropdownMenu 焦点意图的状态归属，移除 1 个已提交 baseline ID。44 项菜单 JVM 用例包含 8 项快照，全部通过。连同先前组件提交，已提交 baseline 为 5,369 个 ID。整合工作区仍为 5,364 个 ID，差额来自随原组件改动保留的结构清理。两个提交使用独立索引，原文件字节和原暂存差异均已核对。[提交与 JVM 结果](../.agent-runs/code-review-95c932b6/scoped-commits.json)
- 两轮规定范围均已审查。已确认的 Toast 悬挂提示、JS 启动、宿主监听覆盖、普通 HTTP 启动与目录滚动问题都有组件提交和前后证据。干净源码的 1,615 个相关输入与提交逐文件一致，仓库级 Detekt 和 ktlint 均通过。保留的工作区原始版本仍与通过 1,957 项 JVM 用例及双 Web 构建的 1,648 个输入一致。[干净提交的 Detekt 覆盖](../.agent-runs/code-review-95c932b6/committed-detekt-coverage.json)
- 用户已有的菜单视觉、Empty 排版和 Space 捕获改动及其结构清理保留在工作区，随各自组件变更整合。正式 CI 仍只拦 baseline 外的告警，ktlint 负责格式。历史全量核心 JVM 的 Checkbox 中间帧和两项 Switch 反向动画失败记录在基线检查。本轮记录的 Gradle Windows 包装脚本 CRLF 空白来自已有的包装器更新，审查提交的差异检查通过。

## 全组件扩展进度

清单取自 `shadcn/core/src/commonMain/kotlin/com/toutiaoyan/compose/ui/components` 的直接子目录。平台实现及跨组件测试按对应组件归档。人工审查和 Detekt 清理均验收后才算完成。已归档的旧批次证据可复用，存在重叠改动时记录具体文件及验证范围。

| 组件源码包 | 人工审查 | Detekt 清理 | 证据与待办 |
| --- | --- | --- | --- |
| `accordion` | 已验收 | 已验收 | 方向键跳过已卸载的触发器，`a8c5a079`。 |
| `alert` | 已验收 | 已验收 | 旧主题操作按钮随插槽生命周期挂载，`fb4b0d85`。 |
| `alertdialog` | 已验收 | 已验收 | 修复 Nova 紧凑页脚的无界测量，`7c6d03b8`。 |
| `aspectratio` | 已验收 | 已验收 | 尺寸级联、比例输入和目录示例已复核。 |
| `attachment` | 已验收 | 已验收 | 未溢出的附件组跳过 Tab 停靠，`7ed5c664`。 |
| `avatar` | 已验收 | 已验收 | 图片卸载只通知当前观察者，`98edb377`。 |
| `badge` | 已验收 | 已验收 | 共用旧主题变体几何，分离交互与图标样式，`22737976`。 |
| `breadcrumb` | 已验收 | 已验收 | 复用换行布局及悬停样式，`dcfa5f72`。 |
| `bubble` | 待审 | 待审 |  |
| `button` | 待审 | 待审 |  |
| `buttongroup` | 已验收 | 已验收 | 按实际落位更新连边及边框重叠，`63b97dd0`。 |
| `calendar` | 待审 | 局部清理 | 已处理日历周索引和算法私有代码。 |
| `card` | 已验收 | 已验收 | 提取 Nova 页头测量并复用间距，`4e638668`。 |
| `carousel` | 待审 | 待审 |  |
| `chart` | 待审 | 待审 |  |
| `checkbox` | 待审 | 待审 |  |
| `chip` | 已验收 | 已验收 | 为复选角色补充勾选状态，`37daeefb`。 |
| `codeblock` | 待审 | 待审 |  |
| `collapsible` | 已验收 | 已验收 | 关闭内容时回到仍挂载的触发器，`b9182baf`。 |
| `combobox` | 待审 | 待审 |  |
| `command` | 待审 | 待审 |  |
| `contextmenu` | 局部审查 | 局部清理 | 已有共享菜单和子菜单测试证据。 |
| `dialog` | 待审 | 待审 |  |
| `drawer` | 已验收 | 已验收 | `c52276e4` 修复留白，`f97109ce` 清理状态与布局。 |
| `dropdownmenu` | 已验收 | 已验收 | 菜单复审、独立提交及 Chromium 记录。 |
| `emojitext` | 已验收 | 已验收 | 保留扩展 Emoji 序列及环境文字颜色，`e8353560`。 |
| `empty` | 局部审查 | 局部清理 | 已有排版搜索和边框的 JVM 与 Web 证据。 |
| `field` | 待审 | 待审 |  |
| `hovercard` | 待审 | 待审 |  |
| `icon` | 已验收 | 已验收 | 共享尺寸样式并核对显式染色，`2334e48a`。 |
| `input` | 已验收 | 已验收 | 保留字数上限时未被编辑的文本，`f19db43a`。 |
| `inputgroup` | 待审 | 待审 |  |
| `inputotp` | 已验收 | 已验收 | 仅在值变化时回调，移除槽位时清理命中坐标，`1a1a0fa2`。 |
| `item` | 待审 | 待审 |  |
| `kbd` | 已验收 | 已验收 | 核对键帽、组合、Tooltip 与触发器的语义。 |
| `label` | 已验收 | 已验收 | `2efa8fbd` 修复零值与相对行高。 |
| `marker` | 已验收 | 已验收 | 统一 Nova、Rhea 的布局选择，`6da4551e`。 |
| `menubar` | 局部审查 | 局部清理 | 已有悬停、焦点与 Canvas 适配证据。 |
| `message` | 待审 | 待审 |  |
| `messagescroller` | 待审 | 待审 |  |
| `navigationmenu` | 待审 | 待审 |  |
| `overlay` | 已验收 | 已验收 | 前两轮及第一批记录。 |
| `pagination` | 待审 | 待审 |  |
| `popover` | 待审 | 待审 |  |
| `progress` | 已验收 | 已验收 | 独立轨道在无界容器中安全测量，`326ce39b`。 |
| `questionnaire` | 待审 | 局部清理 | 已处理快捷键上限。 |
| `radiobutton` | 待审 | 待审 |  |
| `resizable` | 待审 | 待审 |  |
| `scrollarea` | 待审 | 待审 |  |
| `select` | 已验收 | 已验收 | 前两轮及第一批记录。 |
| `separator` | 已验收 | 已验收 | 方向、装饰语义和样式覆盖已复核。 |
| `sheet` | 已验收 | 已验收 | 第二轮及第二批记录。 |
| `sidebar` | 待审 | 局部清理 | 已处理动画时长。 |
| `skeleton` | 已验收 | 已验收 | 替换样式、脉冲和目录布局已复核。 |
| `slider` | 已验收 | 已验收 | 第二轮及第二批记录。 |
| `spinner` | 已验收 | 已验收 | `036a3657` 统一尺寸来源并拆清几何测试。 |
| `stepper` | 待审 | 待审 |  |
| `switch` | 待审 | 待审 |  |
| `table` | 待审 | 待审 |  |
| `tabs` | 已验收 | 已验收 | 退出动画开始后旧面板释放焦点，`0a0763d9`。 |
| `text` | 待审 | 待审 |  |
| `textarea` | 已验收 | 已验收 | 加上限时替换多行内容的回归覆盖，`ae14d1d2`。 |
| `toast` | 已验收 | 已验收 | 前两轮及第一批记录。 |
| `toggle` | 已验收 | 已验收 | 第二轮及第二批记录。 |
| `togglegroup` | 待审 | 待审 |  |
| `tooltip` | 待审 | 待审 |  |
| `trace` | 已验收 | 已验收 | 按键释放时打开一次链接，`a8521ad7`。 |

### Drawer 外边距回归

- `DrawerContent.kt:96` 与 `DrawerSizingTest.kt:57`。Nova 右侧 Drawer 改为贴边后失去原有留白。提交 `c52276e4` 恢复右、上、下三边的 8 dp，以及与之对应的面板高度。左、上、下方向的现有定位和其他主题保持原行为。
- JVM 修复前报告 `Top gap: 0.0`。修复后 56 项核心 Drawer 与 13 项目录 Drawer JVM 测试通过。核心与共享目录的 Detekt、ktlint 通过。Chromium 在 JS 和 Wasm 上分别测得修复前 0 px、修复后 8 px。[浏览器前后对照](../.agent-runs/code-review-95c932b6/drawer-right-inset.json)

### Drawer、AspectRatio、Separator 和 Skeleton 验收

- Drawer 核对受控开关、关闭优先级、Portal 的最新回调、缩进/嵌套注册的释放、锚点裁切和手势系数。右侧外边距缺陷的前后验证见上一节。Kotlin 提交 `f97109ce` 将 `Drawer.kt` 的注册生命周期、`DrawerContent.kt` 的尺寸计算和 `DrawerSnapPoint.kt` 的顺序吸附选择拆开。删除三个无引用的内部方向函数与只写不读的内部指针配置。动画时间、覆盖层透明度与禁用透明度具名且数值沿用。
- Drawer 同范围扫描移除 9 个 ID，另将一条内部构造器规则记录对应到删去未用字段后的签名，规则阈值保持。剩余 23 个 ID 已核对。公开参数和默认值属于调用契约，两个命名命中来自相邻摆放的尺寸或方向声明，曲线控制点、半轴中心和行高来自当前动效及排版定义。面板的长函数仍共同拥有锚点、动画和释放顺序。手势测试的时序留在一个用例内以保留按帧断言。完整 ID 与验证见报告。[Drawer 前后对照](../.agent-runs/code-review-95c932b6/component-review-drawer-batch.json)
- `AspectRatio.kt:36` 在入口拒绝零、负数和非有限比例，样式尺寸先参与测量，内容保留各自的裁切与无障碍所有权。核心 5 项、目录 6 项 JVM 通过，相关源码和测试无存量 ID。[既有 Web 验收](web-parity/aspect-ratio-r5/README.md)
- `Separator.kt:42` 的水平/垂直尺寸、装饰态与可覆盖语义已核对。核心 14 项、目录 38 项 JVM 通过。保留 `SeparatorStyles.kt` 中与样式共置的公开方向枚举及其一条文件命名规则记录。分隔线原生 ARIA role 的平台差异沿用现有记录。[既有 Web 验收](web-parity/separator-r3/README.md)
- `Skeleton.kt:32` 的调用方替换样式和 `SkeletonStyles.kt:37` 的主题圆角、背景与脉冲已核对。核心 12 项、目录 48 项 JVM 通过，相关源码和测试无存量 ID。脉冲逐帧重组的回归测试通过。[既有 Web 验收](web-parity/skeleton-r2/README.md)
- 本批 192 项 JVM 测试通过，目录模块 Detekt、ktlint 通过。初轮核心格式检查报出三处长行。修正后核心 Detekt、ktlint 与最终 56 项 Drawer JVM 回归通过。本批现有浏览器证据覆盖视觉和交互，本次 Drawer 结构清理沿用相同的阶段、阈值和数值。

### Label、Kbd 和 Spinner 验收

- `Label.kt` 的 Nova 行盒将公开 `FieldLabel` 的相对行高交给只接受 `sp` 的转换，且零行高会使行数计算得到 `NaN`。两条新 JVM 用例先分别复现 `Only Sp can convert to Px` 和 `Cannot round NaN value`，再经 `2efa8fbd` 通过。自定义行盒仅处理正值 `sp`，其余交由原段落测量。沿用 Nova 禁用透明度，移除 1 个存量 ID。保留 `LabelImpl` 一条参数数目记录，参数直接对应普通 Label 与提供 Field 排版覆盖的调用者。两种 Web 构建在 Chromium 通过可见标签激活复选框、Space 切换以及点击四个付款字段标签后编辑的验证。
- `Kbd.kt:68` 与 `KbdStyles.kt:15` 的键帽尺寸、Tooltip 色彩、组合字体和非交互语义已核对。尺寸来自 Nova 的键帽规格。已有 JVM 用例验证组内文字及键帽的字体、内联图标和父按钮的点击。相关源码与测试无 Detekt 存量 ID。[既有 Web 验收](web-parity/kbd-r2)
- `Spinner.kt` 与 `SpinnerStyles.kt` 的默认样式和固有尺寸现在共用同一 16 dp 来源。`036a3657` 保留 SVG 比例数值，分离尺寸样本、像素轮廓及覆盖率断言。规范尺寸枚举文件能独立检索，保留类型名和枚举值。核对了动画绘制、调用方样式覆盖、固有尺寸约束和单一加载语义。6 个存量 ID 已移除。[既有 Web 验收](web-parity/spinner-r2/README.md)
- 本批 33 项核心及 38 项目录 JVM 测试通过，两模块 Detekt、ktlint 通过。7 个存量 ID 移除，零新增。最终 Spinner 默认值归并后在独立副本复跑核心 JVM 与 lint，通过。[JVM、Detekt 与 Chromium 证据](../.agent-runs/code-review-95c932b6/component-review-label-kbd-spinner.json)

### Progress、Alert 和 Avatar 验收

- `Progress.kt:269,295`。横向滚动中的独立确定型轨道把无界约束当作宽度，修复前计算出 1,073,741,824 px 并抛出 `IllegalArgumentException`。修复后使用有限宽度或约束最小值，确定型和不确定型的无界测试通过。有界宽度动画、RTL 方向与无障碍数值仍由已有用例覆盖。四条存量 ID 已移除。公开参数及与样式定义共置的 `ProgressDefaults` 保留。
- `Alert.kt:148,320`。旧主题根组件重组时会清空操作插槽，改变宽度后按钮从语义树消失。修复后，Lyra 的按钮在尺寸更新后可见，移除插槽时随即消失。Nova 测量规则独立，Alert 两个变体共用相同的背景、边框和圆角构造。两条存量 ID 已移除。公开便捷重载、共置的变体类型及 Nova CSS 留位尺寸保留。
- `AvatarImage.kt:74,134,155,188`。在更换观察者后移除图片，旧观察者原来收到 `Success, Idle`。修复后旧观察者仅收到 `Success`，当前观察者收到 `Idle`，并显示回退。异步和矢量路径复用卸载逻辑。分组尺寸优先级、负间距、镜像和徽标叠放已有 JVM 覆盖。一条存量 ID 已移除。三个公开图片重载、共置尺寸定义和官方 `-space-x-2` 规格保留。
- 两模块 Detekt 和 ktlint 通过。核心干净源码扫描覆盖 554 个 Kotlin 文件，已提交 baseline 从 4,528 条减至 4,521 条，零新增。100 项 JVM 用例中 96 项通过。四项 Alert 快照在相同环境的修复前源码上也失败，前后实际 PNG 的 SHA-256 逐一相同，保留仓库基准。Chromium 在 JS/Wasm 均确认 Progress 的 `56%` 及可访问范围、Alert 操作点击和 Avatar 首字母回退。[回归、像素比对与浏览器证据](../.agent-runs/code-review-95c932b6/component-review-progress-alert-avatar.json)

### AlertDialog、Badge 和 Breadcrumb 验收

- `AlertDialogLayout.kt:57` 与 `AlertDialogMediaTest.kt:22`。Nova 紧凑页脚原来把无界宽度哨兵 2,147,483,647 作为按钮的实际宽度，导致横向滚动容器中的页脚抛出 `IllegalArgumentException`。`7c6d03b8` 按按钮固有宽度测量无界页脚。新增用例实测两个操作等宽、保持手机端反序且间隔 8 dp。核心还覆盖小尺寸双列、RTL 媒体排布、Tab 限制与 Escape 焦点恢复。清理四条存量 ID，保留公开重载、旧 getter 和原主题规格。
- `BadgeStyles.kt:105` 与 `BadgeExtendedApiTest.kt:77`。`22737976` 合并旧主题变体相同的边框预留、内边距、字号和焦点圈。保留 Ghost 原字重及各变体配色。Nova 的交互、图标边距和文字摆放分离。断言继续覆盖双方向和全部图标位置组合，以及回调优先级、键盘激活、无效态和既有 JVM 签名。清理四条存量 ID，公开参数数目及共置类型的记录保留。
- `BreadcrumbStyles.kt:28,41`。`dcfa5f72` 复用根和列表的换行规则，并具名标注悬停时间、禁用透明度和 RTL 旋转角度。当前页可访问名称和分隔元素的装饰语义已有覆盖，标签可点击性、Tab 顺序和菜单交互通过回归。清理三条存量 ID。
- 核心扫描覆盖 556 个 Kotlin 文件，提交的 Detekt baseline 从 4,521 条减至 4,510 条，零新增。核心及目录模块的 Detekt、ktlint 通过。含像素校验的 67 项 JVM 用例中 53 项通过，另外 14 项在修复前同样失败，实测 PNG 的 SHA-256 前后逐一相同。最终 46 项行为回归单独复跑通过。两套 Web 构建在 Chromium 均验证手机 AlertDialog 按钮反序和 Escape 关闭、Badge 跳转 `#link`、Breadcrumb 当前页名称与折叠菜单。[回归、像素比对及浏览器证据](../.agent-runs/code-review-95c932b6/component-review-alertdialog-badge-breadcrumb.json)

### Accordion、Collapsible 和 Tabs 验收

- `Accordion.kt:160,829`。当项仍挂载而触发器已移除时，旧实现仍把该项排入方向键目标，按 Down 后焦点留在原触发器。修复后只访问仍注册且启用的触发器，移动成功后才消费按键。保留测量后的视觉顺序、横排键盘方向、RTL 和循环边界。清理三条存量 ID，保留公开重载、动画曲线及图标路径比例。
- `Collapsible.kt:75,102,377`。原触发器卸载后关闭内容，焦点原来未移交给剩余触发器。修复后优先恢复到最后聚焦的有效触发器，其次选择仍挂载且可聚焦的触发器。无可用目标时清除正在关闭的内容焦点。动画时间具名，清理一条存量 ID。公开重载和保留内容的状态契约继续由既有用例覆盖。
- `Tabs.kt:637,694`。受控切换带退出动画的面板后，原面板的操作在动画期间仍持有焦点。修复后在旧面板仍挂载时释放焦点，并拦截重新进入。保留按键自动与手动激活、视觉顺序、RTL 和默认即时切换。清理五条存量 ID，公开参数和聚合触发器语义、绘制及交互修饰顺序保留。
- 159 项最终 JVM 行为回归、两模块 Detekt 与 ktlint 通过。176 项含像素检查的 JVM 用例中，17 项在修复前同样失败，实际 PNG 前后逐一相同。干净源码扫描 557 个 Kotlin 文件，已提交 baseline 从 4,510 条减至 4,501 条。四条公开重载记录随默认表达式的常量名更新，规则与参数数量一致。Chromium 用隔离验证页在 JS/Wasm 执行三个焦点场景，均通过。修复前证据来自 JVM。[验证证据](../.agent-runs/code-review-95c932b6/component-review-accordion-collapsible-tabs.json)

### ButtonGroup 和 Card 验收

- `GroupCorners.kt:57` 与 `ButtonGroupScreenshotTest.kt:25`。带稳定 key 的 Nova Outline 按钮换位后，旧注册顺序仍让新首项向外偏 1 dp。JVM 修复前测得组起点 24 dp、新首项 23 dp。修复后按实际落位计算逻辑顺序，新首项贴齐组起点，两个按钮仍重叠 1 dp。未落位时沿用注册顺序，按 RTL、横向或纵向解析边缘。共用注册器的 ToggleGroup 继续按原顺序读取。具名焦点叠放层级，移除一条存量 ID。公开插槽、间距和边角参数保留。
- `CardLayout.kt:138`。把 Nova 页头测量与 `CardAction` 的父数据放在布局文件内。标题和描述仍占据起始列，操作放在逻辑尾侧。间距只解析一次，保留旧主题布局、无界测量、跟随调用方间距的内边距与页脚贴边规则。移除一条存量 ID，保留公开重载及其组装入口。
- 119 项最终 JVM 行为回归通过，包含共用注册器的 ToggleGroup 用例。含显式像素比对的 136 项中 123 项通过，13 项修复前也失败，前后实际 PNG 的 SHA-256 逐一相同。两模块 Detekt、ktlint 和常规 JS/Wasm 目录构建通过。Detekt 干净源码扫描 557 个文件，baseline 从 4,501 条减至 4,499 条，零新增。Chromium 在两种运行时验证换位前后组边缘、相邻按钮重叠和 Card 页头内容顺序。[回归、像素比对与浏览器证据](../.agent-runs/code-review-95c932b6/component-review-buttongroup-card.json)

### Icon、Chip 和 EmojiText 验收

- `Icon.kt:47` 与 `IconStyles.kt:34`。统一默认和小号变体的尺寸声明。默认尺寸仍优先读取内联控件的主题尺寸，小号仍用主题小号规格，调用方 Style 仍最后合并。明确染色仍优先于环境颜色，Chip、Badge 和 Button 消费方回归及浏览器像素覆盖。保留一条公开参数数量记录。
- `Chip.kt:96`。原复选角色只提供 `selected`，JVM 无 `ToggleableState`。修复后公布勾选状态，现有选中语义与回调保持。三种变体共用焦点圈、排版和内边距，保留各自背景和反馈。保留公开参数数量与样式文件中共置的变体类型两条记录。
- `EmojiText.kt:25,53,83`。原实现会把肤色、ZWJ 及文本呈现选择序列的一部分替换成独立图，且插图路径的文字使用前景默认色。JVM 修复前混合样例产生 5 个占位符，修复后只有受支持的完整序列产生 1 个，原文本逐字保留。改用现有富文本入口，周围正文继承颜色，图标占位跟随实际字号。相关源码和测试无存量 ID。
- 最终 63 项 JVM 检查及两模块 Detekt 和 ktlint 通过。含显式像素校验的 67 项中，四项 Chip 快照在修复前也失败，实际 PNG 前后逐一相同。干净源码扫描覆盖 558 个文件，baseline 共 4,499 条，零新增。Chromium 的 JS、Wasm 隔离验证页均读到 Chip 的 `aria-checked` 由 false 切换为 true，并核对 Emoji 原始文本、环境红色和显式图标绿色。[回归、像素比对与浏览器证据](../.agent-runs/code-review-95c932b6/component-review-icon-chip-emojitext.json)

### Marker、Attachment 和 Trace 验收

- `MarkerStyles.kt:12` 与 `Marker.kt:58`。将 Nova、Rhea 共用的布局选择收敛到同一处，边线两个端点共用按主题计算的落位高度。既有 JVM 核对标题水平居中、RTL、间距、装饰图标语义和主题实时切换。相关源码与测试无存量 ID。
- `Attachment.kt:691` 与 `AttachmentGroupScroll.kt:75`。未溢出的附件组原先暴露 `RequestFocus`，形成多余的 Tab 停靠点。修复后有溢出才加入键盘顺序，布局从溢出恢复为可容纳时移除停靠点。单独处理滚轮、拖动惯性、嵌套滚动与方向键，保留滚动吸附边界、RTL 和独立删除操作。净移除八条存量 ID。保留公开重载、布局分支及上传托盘的完整样式验证。
- `Trace.kt:291`。旧链接在 Enter 按下时打开，并额外手动发出焦点交互。修复后沿用共享点击组件，按键释放才打开一次，保留悬停、可访问名称和 URI 回调。说明展开时长、箭头角度和待处理透明度，移除四条存量 ID。公开的归组插槽和由调用方控制的展开参数保留。
- 最终 127 项 JVM 检查及两模块 Detekt、ktlint 通过。显式像素比对共 133 项，六项 Attachment 快照在修复前同样失败，实际 PNG 前后逐一相同。干净源码扫描 559 个 Kotlin 文件，已提交 baseline 从 4,499 条减至 4,487 条，零新增。Chromium 在 JS 和 Wasm 上验证溢出前后的真实 Tab 顺序、Trace 的 Enter 释放与 Marker 居中。最终两种常规目录构建通过。[JVM、像素比对与浏览器证据](../.agent-runs/code-review-95c932b6/component-review-marker-attachment-trace.json)

### Input、Textarea 和 InputOTP 验收

- `Input.kt:243` 原先截断整个更新后的值，达到上限时在中间插入字符会误删尾部。`InputEdit.kt:10` 缩减插入范围并重映射光标和输入法组合区，同时避免拆开 UTF-16 代理对。提交 `f19db43a`。
- `Textarea.kt:67` 沿用 Input 的编辑逻辑，新增选区跨行回归，保留已有的最小高度。提交 `ae14d1d2`。
- `InputOTP.kt:293` 只在接受的值实际变化时回调。槽位在 `InputOTP.kt:392` 随组合退出释放命中坐标。在原槽位的位置输入时，更新当前存在的最近槽位。提交 `1a1a0fa2`。
- 保留公开签名、主题颜色及动效定义。输入框的可用态、焦点、分组适配和正文装饰共用一个受控编辑入口。OTP 槽位样式维持原有组环与层叠顺序。保留 Nova 的禁用背景标准透明度和 CSS ease-out 控制点。
- 核心的同配置 Detekt 扫描从 4,487 减至 4,479 个存量 ID，零新增。核心和目录 Detekt、ktlint 与 100 项最终 JVM 验证通过。Textarea 四张快照在原实现上同样失败，实际 PNG 的 SHA-256 逐一相同。两种常规 Web 构建通过，Chromium 在 JS/Wasm 上实测选区替换、OTP 回调和动态槽位的指针命中。[验证证据](code-review-evidence/input-textarea-inputotp.json)
