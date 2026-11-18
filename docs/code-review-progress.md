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

- `app/webApp/src/webMain/kotlin/com/toutiaoyan/compose/ui/BrowserDocHistory.kt:109` 在普通 HTTP 主机上调用仅在安全上下文可用的 `crypto.randomUUID()`，导致目录初始化失败。现根据运行时能力选择 UUID 或四个随机 32 位值，历史条目仍使用不透明字符串。该文件和依赖它的滚动改动都在当前工作区中，兼容修复随原改动保留，待整合时一并提交。
- Chromium 使用同一套路由与断言对照。原产物的 JS 和 Wasm 在 `http://catalog.test` 都报 `TypeError`。修复后两个产物在普通 HTTP 和 localhost 共四组都成功挂载。每组均确认片段条目 ID 与原条目不同，后退和前进分别恢复对应的 ID。另在 localhost 复核了锚点滚动与宿主 `popstate` 处理器。对应 JS/Wasm 构建、Web 和共享目录的 `ktlintCheck` 均通过。[修复前](../.agent-runs/code-review-95c932b6/catalog-http-before.json) · [修复后](../.agent-runs/code-review-95c932b6/catalog-http-after.json) · [滚动和监听器](../.agent-runs/code-review-95c932b6/catalog-current-history.json)
- `BrowserDocHistory.kt` 合并历史条目的空值与所有权检查，仅在直接调用内嵌 JS 的函数上标注 Detekt 的 `UnusedParameter` 误报。8 个参数均在 JS 表达式中使用。`main.kt` 单独判断页面位置变化。`BrowserDocumentScrollHost.kt` 分离 Canvas 选择与原生事件监听。`DocumentScrollBridge.kt` 明确半个 Compose 像素的容差，原顺序决定是否保留恢复位置。
- 原工作区的 Web 目录模块 11 条、共享目录模块 2 条告警均已消除；两模块的 Detekt 与 ktlint 通过，baseline 未改动。24 项导航和滚动 JVM 测试、最终 JS/Wasm 构建和上述 Chromium 回归通过。构建与工作区的 1,648 个输入逐文件一致。清理涉及未提交的目录导航文件，随该批改动留在工作区。[原告警](../.agent-runs/code-review-95c932b6/catalog-local-detekt.json) · [复测结果](../.agent-runs/code-review-95c932b6/catalog-cleanup-verification.json)

## 菜单清理与最终验证

- `DropdownMenu.kt` 集中处理首项、末项与指针打开的焦点意图，把根面板和子面板的关闭按键与布局分开。保留菜单退出中重开时的状态观察位置、焦点返回和最深层优先关闭顺序。两个公开面板签名及默认表达式与本轮修改前逐字一致。
- `DropdownMenuLiveStyleTest.kt`、`DropdownMenuArabicLayoutTest.kt` 和 `ContextMenuPanelTest.kt` 分开测试布置、测量和断言，保留原断言及诊断输出。剩余 12 条告警中，10 条通过代码清理消除。另两条公开参数告警仅配对更新原 baseline ID，对应现有默认过渡引用变化。相同源码范围内零新增原始问题 ID。
- 确认 6 个旧 baseline ID 在当前改动中已解决，核心 baseline 从 4,545 降到 4,539，全仓从 5,370 降到 5,364。仓库级 `ktlintCheck` 和 `detekt` 通过。强制重跑确认七个含源码的任务均实际执行，共 969 个 Kotlin 文件，核心模块覆盖 9 个源集和 573 个文件。根目录下两个源代码为空的聚合项目按预期跳过。[ID 对照与 JVM 结果](../.agent-runs/code-review-95c932b6/menu-final-detekt-and-jvm.json) · [扫描覆盖](../.agent-runs/code-review-95c932b6/final-detekt-coverage.json)
- 与当前工作区逐文件一致的 1,648 个输入通过 444 项核心 JVM 回归、1,513 项目录共享模块全量 JVM 测试，以及 JS/Wasm 双构建。Chromium 在两种运行时均验证了 DropdownMenu 关闭后键盘重开、Menubar 悬停切换和 Shift+Tab 返回子菜单触发器、Escape 按层关闭，以及普通 HTTP 下目录启动和历史恢复。[DropdownMenu](../.agent-runs/code-review-95c932b6/final-dropdown-keyboard.json) · [Menubar](../.agent-runs/code-review-95c932b6/final-menubar-nested.json) · [目录 HTTP](../.agent-runs/code-review-95c932b6/final-catalog-http.json)
- 两轮各组件的审查发现及可独立提交的修复见上表。清理和验证均已落到当前工作区。菜单收尾与 baseline 更新依赖尚未提交的组件改动，普通 HTTP 修复依赖尚未提交的 `BrowserDocHistory.kt` 及目录导航改动。对应组件整合时一起提交。历史全量核心 JVM 的 Checkbox 中间帧和 Switch 动画反向基线失败仍由前文记录。

## Chromium 实测

- Select 在最终的 JS/Wasm 构建中验证 DPR 2 下的弹层高度、外部点击和滚轮锁定。Toast 在两种运行时验证桌面和触屏交互，覆盖队列、计时与焦点。
- Slider 验证键盘、触屏和滑块值语义。Sheet 验证鼠标与触屏打开时的初始焦点及 Tab 后编辑。Toggle 验证键盘、触屏取消、RTL 和 `aria-pressed`。目录测试验证锚点与前进后退，并复现上表的宿主监听器问题。
- 22 个浏览器配置的断言全部通过，其中 2 个用于复现已登记的目录缺陷。服务响应与最终分发产物哈希一致，Wasm 用例实际加载了应用 Wasm。[浏览器结果](../.agent-runs/code-review-95c932b6/browser-summary.json) · [产物响应核对](../.agent-runs/code-review-95c932b6/browser-provenance.json)
