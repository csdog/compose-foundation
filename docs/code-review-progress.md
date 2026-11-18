# Compose Foundation 代码审查进度

[审查计划与验收标准](code-review-plan.md)

审查基线为 `95c932b6`。两轮审查已结束，组件修复已分批提交。目录导航和 Detekt 构建配置各有一项延后事项。审查开始前已有的代码改动和暂存内容均已保留。

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
| `app/webApp/src/webMain/kotlin/com/toutiaoyan/compose/ui/main.kt:106`、`:122` | 路由绑定直接写入和清空 `window.onpopstate`，覆盖宿主已有处理器。 | 暂缓，文件有用户的导航改动 | Chromium 在 JS/Wasm 中均复现。可正常执行锚点导航、后退和前进，但浏览器派发了 `popstate`，预先注册的宿主处理器调用为 0。整合导航改动后改用成对注册与移除。[导航复现记录](../.agent-runs/code-review-95c932b6/catalog-history.json) |
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
- `app/webApp` 的历史监听器修复仍与本地导航改动重叠。DropdownMenu 也有重叠修改，待整合后复审。保留上述本地改动和已有暂存内容。

## Chromium 实测

- Select 在最终的 JS/Wasm 构建中验证 DPR 2 下的弹层高度、外部点击和滚轮锁定。Toast 在两种运行时验证桌面和触屏交互，覆盖队列、计时与焦点。
- Slider 验证键盘、触屏和滑块值语义。Sheet 验证鼠标与触屏打开时的初始焦点及 Tab 后编辑。Toggle 验证键盘、触屏取消、RTL 和 `aria-pressed`。目录测试验证锚点与前进后退，并复现上表的宿主监听器问题。
- 22 个浏览器配置的断言全部通过，其中 2 个用于复现已登记的目录缺陷。服务响应与最终分发产物哈希一致，Wasm 用例实际加载了应用 Wasm。[浏览器结果](../.agent-runs/code-review-95c932b6/browser-summary.json) · [产物响应核对](../.agent-runs/code-review-95c932b6/browser-provenance.json)
