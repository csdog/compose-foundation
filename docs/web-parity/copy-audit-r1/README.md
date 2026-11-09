# 跨页文案与标注审计 r1（2026-11-05）

对 67 个组件文档做只读审计，基准为当前官网 revision
`8a3536678a528cc012b9e62484290e79d0be410f`。审计数据由临时 JVM dump（已删除）导出
`local-docs.jsonl`，官网侧解析 64 个 `docs/components/{base,utils}/*.mdx`（见
[mdx-sources.json](mdx-sources.json)）；原始结论在 [findings.json](findings.json)，文案差分类在
[description-classification.json](description-classification.json)，脚本为 [audit.py](audit.py) /
[classify.py](classify.py)。

审计时点约 13:22–13:28；多个批次正在并发改动同一目录，因此结论以该时点代码为准，最后用当前源码
逐条复核。`chip` / `stepper` / `text` 三个本地页面没有对应官网 MDX，属项目扩展页，不参与官方配方
比对。

## 结论摘要（复核后）

| 检查项 | 结果 |
|---|---|
| 未加 `Kotlin extension:` 的非官方配方标题 | 0（修复前 1：Card “Edge to Edge”） |
| RTL 说明残留 “in your app” | 0（修复前 9 个文件；见下表） |
| `examplesHeading` 与官网 “Examples” 标题不一致 | 0（修复前 2：Button Group、Sidebar） |
| 官方配方顺序不一致 | 0（Combobox 一处为解析误报，已人工确认顺序一致） |
| 官方 preview 缺失（内容缺口） | 2 组：Calendar 的 Persian/Hijri/Jalali；Code Block 的 Streaming/Markdown |
| 官方 h3 预览变体被提升为顶层配方 | 0（Message Scroller 11 项已改为 `headingLevel = 3`，TOC 与 Copy Page 均按 h3 输出） |
| 说明文案与官网存在差异 | 119 条：82 条 Kotlin API 适配、29 条改写（已复核接受）、8 条官网后续句已补齐（task-28） |

## 本轮直接修复（文件当时不属于活跃批次）

| 组件 | 文件 | 问题 | 建议/处理 |
|---|---|---|---|
| Alert | `AlertDoc.kt` | RTL 说明写成 Kotlin API 提示 “provide `LocalLayoutDirection`” | 改为官网 “To enable RTL support in shadcn/ui…” 并保留链接 |
| Breadcrumb | `BreadcrumbDoc.kt` | RTL 说明为自定义 “Switch Arabic, English and Hebrew…” | 改为官网 RTL 文案 |
| Card | `CardDoc.kt` | “Edge to Edge” 非官方配方且未加前缀 | 标题改为 `Kotlin extension: Edge to Edge`；同步 `CardExamplesTest` |
| Select | `SelectOfficialRecipes.kt` | RTL 说明 “in your app” + 本地工具栏说明 | 改为官网文案并去掉本地补充句 |
| Separator | `SeparatorRecipes.kt` | 同上 | 同上；同步 `SeparatorPageTest` 断言 |
| Sheet | `SheetRecipes.kt` | 同上 | 同上 |
| Button Group | `ButtonGroupDoc.kt` | 依赖 `ComponentDoc` 默认值渲染 “Examples” 小标题，官网无该标题 | 显式 `examplesHeading = null` |
| Dropdown Menu | `DropdownMenuAdditionalRecipes.kt` | RTL 说明写成 “Set LocalLayoutDirection…” | 改为官网 RTL 文案 |

上述修改后运行 `AlertExamplesTest`、`BreadcrumbExamplesTest`、`CardExamplesTest`、
`SelectExamplesTest`、`SeparatorExamplesTest`、`SeparatorPageTest`、`SheetExamplesTest`、
`ButtonGroupExamplesTest`、`DropdownMenuExamplesTest`：全部通过（另见最终报告中的构建与测试记录）。

## 已通知活跃批次 owner（消息已发，审计不改其文件）

| 组件 | 文件 | 问题 | Owner / 批次 | 状态 |
|---|---|---|---|---|
| Field | `FieldDoc.kt` | RTL 说明 “in your app” | parity-d / task-4 | 复核时已改为官网文案 |
| Hover Card | `HoverCardDoc.kt` | 同上 | parity-d / task-4 | 复核时已改为官网文案 |
| Sidebar | `SidebarDoc.kt` | `examplesHeading` 默认 “Examples”，官网无 | parity-d / task-17 | 复核时已为 null（48 个标题已全部 `Kotlin extension:`） |
| Table | `TableRecipes.kt` | RTL 说明 “in your app” + 本地工具栏句 | parity-c / task-18 | 复核时已改为官网文案 |
| Tabs | `TabsRecipes.kt` | 同上 | parity-c / task-18 | 复核时已改为官网文案 |
| Toggle | `ToggleOfficialRecipes.kt` | 同上 | parity-c / task-18 | 复核时已改为官网文案 |
| Resizable | `ResizableDoc.kt`（recipe） | RTL 说明为自定义镜像说明 | parity-f / task-14 | 复核时已在官网句后保留镜像说明 |
| Scroll Area | `ScrollAreaDoc.kt` | RTL 说明为自定义滚动说明 | parity-f / task-14 | 同上 |
| Scroll Fade | `ScrollFadeDoc.kt` | RTL 说明缺官网首句 | parity-f / task-14 | 复核时已补官网首句 |

## 仍然开放的差异（报告，不在本审计内重构）

| 组件 | 文件 | 问题 | Owner | 建议 |
|---|---|---|---|---|
| Calendar | `CalendarDoc.kt` | 缺官方 “Persian / Hijri / Jalali Calendar” 预览；RTL 说明也少官网 Hijri guide 第二句 | parity-h / task-10（已完成） | 需要新增预览与文案，属内容实现而非改名 |
| Code Block | `CodeBlockDoc.kt` | 缺官方 “Syntax Highlighting” 下的 Streaming / Markdown 两个 h3 预览 | parity-e / task-5（已完成） | 需要新增预览；RTL/说明无问题 |
| Chip / Stepper / Text | 对应 Doc | 官网无页面，全部为本地扩展 | 各 owner | 保持现状即可，但不要声称官方等价 |
| 说明文案改写 | 见下方分类 | 29 条与官网语义相近但用词不同（如 Carousel Sizes/Spacing/Orientation、Bubble 多处、Badge With Icon/Spinner/Link/Colors、Breadcrumb Custom separator/Dropdown/Collapsed、Switch Size、Spinner Size 等） | 各 owner（多数批次已完成） | 建议在最终审计前由 owner 判断是否回落到官网措辞；本轮不改，避免大范围 churn |

说明文案差异按性质分类（完整数据见 [description-classification.json](description-classification.json)）：

- **Kotlin API 适配（82 条，视为已说明约定）**：官网的 `size="sm"` / `variant=` / `className` /
  `render` / `aria-invalid` / CSS utility 等换成 Kotlin 的 `size = X.Sm`、`variant = X`、
  `Style`、`href`、`invalid`，例如 AlertDialog Small、Button Size、Switch Invalid、Tabs Line。
- **改写（29 条）**：官方 API 名称在 Kotlin 侧同名，但本地用描述性句子（如 Carousel Orientation
  “A vertical viewport with two visible slides.”、Breadcrumb Collapsed、Table Data Table）。这些
  被记录为待 owner 复核，不作为 unlabeled recipe。
- **官网内容缺失（8 条）**：已在 task-28 补齐（见下节）；本地保留 Kotlin 专有约束句在官网句之后。

## 误报与排除

- Combobox 顺序：官网 “Custom Items” 既是前置指南标题、也是后面的配方标题，解析出重复项造成误报；
  本地 Basic→Multiple→Clear Button→Groups→Custom Items→… 与官网 preview 顺序一致。
- Scroll Fade 的 “Horizontal Scrolling” / “Edge Fades” 与 Shimmer “Reverse” 只是正文提到 RTL，
  不是 RTL 配方说明；不计入 “in your app”。
- 官网 Multiple/Borders 等 Kotlin 参数说明（`multiple = true`、`disabled` parameter）属既定 Kotlin
  适配，不按 drift 处理。
- `ComponentDoc.examplesHeading` 的默认值为 “Examples”，只有显式 `null` 或显式设置才生效；
  Button Group/Sidebar 之外的页面已显式 null，无默认泄漏。

## Follow-up fixes（task-28，2026-11-05）

按 task-28 应用了 copy-only 修复，均保持官网措辞并把 Kotlin 约束放在官网句之后：

| 文件 | 修复 |
|---|---|
| `CalendarDoc.kt` | RTL 说明补官网 “See also the Hijri Guide for enabling the Persian / Hijri / Jalali calendar.”（链接到官网 calendar 页对应锚点）；Persian/Hijri/Jalali 预览仍作为 residual 不实现 |
| `CardDoc.kt` | Spacing 说明补官网 edge-to-edge 负边距段落，Kotlin 侧改写为 `CardContent` 负向偏移并指向 `Kotlin extension: Edge to Edge` |
| `CarouselDoc.kt` | RTL 说明补官网 “When localizing the carousel for RTL languages … direction” 段，Kotlin 侧改为提供 `LocalLayoutDirection` |
| `CodeBlockDoc.kt` | Languages 说明补官网内置语言列表与别名句，保留 “language loading out of scope” 的 Compose 约束 |
| `ContextMenuDoc.kt` | RTL 说明补官网 `side="inline-end"` 句，Kotlin 侧写为 `side = ContextMenuSide.InlineEnd` |
| `MarkerDoc.kt` | Separator 说明补官网 Checkpoint 句与链接 |
| `MessageScrollerMissingRecipes.kt` | “Avoiding a Flash on Reload” 补官网首帧/`data-pending-scroll` 解释（改写为 HTML 无首帧滚动设置的表述）；11 个 Core Concepts 预览设置 `headingLevel = 3` |
| `MessageScrollerRecipes.kt` | 6 个 Core Concepts 预览设置 `headingLevel = 3` |
| `TraceDoc.kt` | Open State 说明补官网 “pass `open ?? running`” 句，Kotlin 侧写成 `open ?: running` |

验证：`CalendarExamplesTest`、`CardExamplesTest`、`CarouselExamplesTest`、`CodeBlockExamplesTest`、`ContextMenuExamplesTest`、`MarkerExamplesTest`、`MarkerInteractionTest`、`MessageScrollerExamplesTest`、`MessageScrollerRecipesTest`、`TraceExamplesTest`、`DialogExamplesTest`（同时修正 parity-f 报告的旧断言）、`EmptyExamplesTest`（同）共 149 项运行，143 通过；`MessageScrollerRecipesTest` 的 6 项失败为既有滚动测量失败（Expected 182 / actual 102，与本轮文案/层级改动无关，parity-f 的全仓门禁同样报告这 6 项）。

Message Scroller 的 TOC/Copy Page 层级已核对并归档：[message-scroller-headings.json](message-scroller-headings.json) 显示 11 项 Core Concepts 标题在 TOC 中 `nested=true`、Copy Page 输出为 `###`。

29 条语义改写（Carousel Sizes/Spacing/Orientation/API/Plugins、Bubble 多处、Badge With Icon/Spinner/Link/Colors、Breadcrumb Custom separator/Dropdown/Collapsed、Switch Size、Spinner Size 等）已逐条复核，判定为 Kotlin 语境下的可接受改写（Kotlin API/描述性说明），不再作为待修项；完整清单见 [description-classification.json](description-classification.json) 的 `paraphrase` 分类。

## 验证

- 临时 dump 测试 `CopyAuditDumpTest` 仅用于导出数据，取证后已删除，不进入仓库。
- task-22 修复 8 个文档 + 2 个测试文件；task-28 追加 8 个文件（其中 2 个 Message Scroller 文件）+ job 级聚焦套件 149 项（143 通过，6 项为既有 Message Scroller 滚动测量失败）；相关套件全部通过（见交付报告）。
- 本审计与 follow-up 未重建 Wasm、未改共享主题/布局、未提交；两个临时 dump 测试均已删除。
