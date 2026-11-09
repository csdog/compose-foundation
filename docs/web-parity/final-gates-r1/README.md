# 最终门禁演练 r1（2026-11-05）

在共享工作区（`main`，还有 52 个未提交修改、多个 parity 批次仍在编辑）上按 task-23 执行的
全量门禁演练。所有 Gradle 都包在 `python3 /tmp/cf-parity/locked.py` 内；未修改他人文件、未改动
黄金基准、未删除测试、未提交 git。本记录是**快照**：剩余批次仍在持续修改工作区。

## 汇总表

| # | 运行 | 结果 | 最后一次耗时 | 说明 |
| - | ---- | ---- | ------------ | ---- |
| 1 | `:app:shared:jvmTest`（第三轮） | **FAIL-real** | 2m49s | 1188 项，6 失败，全部在 `MessageScrollerRecipesTest`（既有失败，见下） |
| 1a | 同上前两次尝试 | FAIL-in-flight → 修复 | 3s / 2m49s | 第 1 次 `SliderDoc.kt` 缺 `CompositionLocalProvider` import（parity-e 在飞）；随后 Dialog/Empty/Input 断言失败由 owner 修复，第三轮已转绿 |
| 2 | `:shadcn:core:jvmTest`（第二轮） | **FAIL-real** | 42s | 1333 项，5 失败：`MenuEscapeLayersTest` 3 + `MenubarParityTest` 2（隔离复现，既有） |
| 3 | `ktlintCheck`（4 模块） | **FAIL-real** | 15s | 仅 `:app:shared:ktlintJvmTestSourceSetCheck` 失败：**86 条 / 28 个 jvmTest 文件**；其余源集 UP-TO-DATE/通过 |
| 4 | `detektMetadataCommonMain`（core+shared） | **FAIL-real** | 48s | 两个任务均失败：**36,446 条**（core 11,734 + shared 24,712）——仓库既有系统性问题 |
| 5 | `:app:webApp:compileKotlinJs` + `wasmJsBrowserDevelopmentExecutableDistribution` | **PASS** | 17s | 退出 0；最终 wasm sha256 见下；仅有既有 AGP 实验开关与 `Text.kt` INVISIBLE_REFERENCE warning |

最终构建 sha256（`app/webApp/build/dist/wasmJs/developmentExecutable/1b185feccfd64aeafa06.wasm`）：

```
b61208f6c945a73688f79695b40f3e39fd55cb29ee004f2fbdcae87dc7823294
```

## 真实失败明细

### 1. `MessageScrollerRecipesTest`（6，既有/无活跃 owner）

- `openingTabsRepositionTheSameSavedTranscriptAndCanRepeat[width=432,dark=false]`
- `openingTabsRepositionTheSameSavedTranscriptAndCanRepeat[width=432,dark=true]`
  - `AssertionError: Expected <182.0> with absolute tolerance <1.0>, actual <102.0>`
- `openingTabsRepositionTheSameSavedTranscriptAndCanRepeat[width=360,dark=false]`
- `openingTabsRepositionTheSameSavedTranscriptAndCanRepeat[width=360,dark=true]`
  - `Expected <182.0> ... actual <0.0>`
- `footerTracksTopMiddleAndBottomWithRealKeyboardScrolling[width=360,dark=false]`
- `footerTracksTopMiddleAndBottomWithRealKeyboardScrolling[width=360,dark=true]`
  - `Failed to perform isDisplayed check ... could not find any node ... "You are at the bottom. You can only scroll up."`

parity-g 在独立运行中复现同样 6 项，确认与 copy/headingLevel 修改无关；MessageScroller 页面当前
没有活跃批次。判定为既有真实失败，需 owner 修复滚动测量/底部状态断言。

### 2. `MenuEscapeLayersTest` / `MenubarParityTest`（5，既有/无活跃 owner）

- `MenuEscapeLayersTest[dropdown] > escapeClosesOnlyTheDeepestOpenLayerAndRestoresEachTrigger`
- `MenuEscapeLayersTest[context-modal] > …`
- `MenuEscapeLayersTest[context-nonmodal] > …`
  - `Failed to inject key input ... could not find any node ... contains 'Leaf'`
- `MenubarParityTest > horizontalKeyInAnOpenMenuSwitchesMenuAndFocusesItsFirstItem`
- `MenubarParityTest > downOpensWithMenuNavigationAndEscapeRestoresTriggerFocus`
  - `Failed to assert (Focused = 'true')` on `Undo` / `New Tab`

用 `--tests` 隔离复现（18 项 5 失败，3s），相关 component/test 文件在工作区干净（最后提交
`491a06ee`），判定为 HEAD 上的既有真实失败。

### 3. ktlint：`app:shared` jvmTest 源集 86 条 / 28 文件

其余三模块及其它 source set 均为通过/UP-TO-DATE，只有 `app:shared:ktlintJvmTestSourceSetCheck`
失败。规则分布：`Argument should be on a separate line` 22、`A multiline expression should start on
a new line` 18、`Missing newline …` 20、`Exceeded max line length` 11、其它少量。文件分布（按条数）：

```
11 CollapsibleExamplesTest    9 TraceExamplesTest       9 TextExamplesTest
 7 InputGroupExamplesTest     7 ContextMenuExamplesTest 6 DatePickerExamplesTest
 5 QuestionnairePageTest      3 SidebarExamplesTest     3 PaginationExamplesTest
 3 InputExamplesTest          3 FieldExamplesTest       2 SelectExamplesTest
 2 MessageScrollerExamplesTest 2 CopyAuditHeadingDumpTest 1 Toggle/Textarea/Tabs/Table/
 1 Switch/SliderPage/Sheet/SeparatorPage/Message/Menubar/Label/Drawer/Dialog/ChartPage
```

其中 `TraceExamplesTest` 属于本 agent 之前提交的文件，其余多为各批次已提交测试；不是本轮
in-flight 语法错误。报告文件：`app/shared/build/reports/ktlint/ktlintJvmTestSourceSetCheck/`。

### 4. detekt：两个 metadata 任务均失败

- `:shadcn:core:detektMetadataCommonMain`：11,734 条，报告 `shadcn/core/build/reports/detekt/commonMain.txt`
- `:app:shared:detektMetadataCommonMain`：24,712 条，报告 `app/shared/build/reports/detekt/commonMain.txt`

规则以 `Indentation`（26,897）、`MagicNumber`（7,422）、`WildcardImport`/`NoWildcardImports`（696）、
`LongParameterList`（306）、`ImportOrdering`（212）为主，覆盖 icons、文档页与生成资源；属于仓库
既有的系统性 detekt 基线（进度文档早有同口径说明），不是本轮引入。

## 处理与重试记录

- Gate 1 第 1 次：`SliderDoc.kt` 缺 import（parity-e 活跃）→ FAIL-in-flight；等待后第 2 次编译通过。
- Gate 1 第 2 次：9 失败。其中 Dialog/Empty 的 RTL 文案断言（parity-g copy audit）与
  `CatalogPreviewTest` Input 标题（parity-d）已由 owner 修复；第 3 次降到 6 个既有失败。
- 全程没有编辑其他 agent 的文件；仅本 agent 自己页面文件在演练前按 copy-audit 建议更新了 3 处
  RTL 文案（Resizable/ScrollArea/ScrollFade）。
- 未改动 `MessageScroller`/`Menubar`/`DropdownMenu` 相关文件、未重录黄金基准、未删测试。

## 结论

- 构建链路（JS 编译 + Wasm distribution）PASS；Wasm sha `b61208f6…`。
- 仍需在收尾前解决的真实门禁问题：6 个 MessageScroller 断言失败、5 个 Menu/Menubar 焦点断言失败、
  app:shared jvmTest 的 86 条 ktlint、以及两个 detekt metadata 任务的 36,446 条报告。
- 后两项（ktlint/detekt）是仓库级既有基线，需 Lead 决定是否作为本批门禁修复范围；两项测试失败
  需要对应页面 owner 处理（当前无活跃写入方）。

## 归档

- 失败套件 XML：[tests/](tests/)（MessageScrollerRecipesTest、MenuEscapeLayersTest、MenubarParityTest）。
- ktlint 报告：[reports/ktlintJvmTestSourceSetCheck.txt](reports/ktlintJvmTestSourceSetCheck.txt)。
- detekt 报告：[reports/detekt-core-commonMain.txt](reports/detekt-core-commonMain.txt)、
  [reports/detekt-shared-commonMain.txt](reports/detekt-shared-commonMain.txt)（完整 36,446 条）。
- 运行日志：[logs/](logs/)（shared/core 测试、ktlint、Web 构建；detekt 完整日志因与报告重复未另存）。
