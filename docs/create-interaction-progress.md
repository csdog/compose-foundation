# Compose Foundation Create 页面与组件交互进度

[目标、范围与完成条件](create-interaction-plan.md)

## 对照基线

| 项目 | 记录 |
| --- | --- |
| 上游参考 | [shadcn Create](https://ui.shadcn.com/create) |
| 对照日期、上游源码版本与本地提交 | 2026-11-19 · 上游 [968d8cb9](https://github.com/shadcn-ui/ui/tree/968d8cb9dc42ae5d52e8fd356cf84596ec832a26) · 本地 `7b7d30bce475bef64b0bc0afd0955acfa6a9acd9` |
| 视口、主题、设备、浏览器与运行时 | 目标视口 1440×1000、1095×687、390×844，另跨 768 px 断点及手机横屏。JS/Wasm 分别验收。 |

## Create 源码对照

| 范围 | 上游预期 | 本地基线 | 验证场景 |
| --- | --- | --- | --- |
| 布局 | `page.tsx` 在 `md` 切换桌面左栏和底部配置区，`customizer.tsx` 分别纵向和横向滚动。 | `CreatePage.kt` 在 900 dp 切换并重建 Customizer，导致局部字体、圆角等状态丢失。已统一断点为 768 dp，并让同一个 Customizer 跨布局移动。 | 从窄屏操作配置后越过断点往返，检查配置、预览与滚动。 |
| 预览 | `preview.tsx` 共享配置与预览选择。 | `CreatePreviewCanvas.kt` 的选择是画布局部状态，切换器可用，命令面板无法选中它。 | 分别由切换器和命令选择预览，检查选中项及内容。 |
| 配置 | 样式、颜色、圆角、字体、图标由当前预设驱动。 | `CreateCustomizer.kt` 的字体、图标与圆角没有接入画布。 | 逐类修改并比对浅深主题。 |
| 欢迎 | `welcome-dialog.tsx` 提供说明和开始按钮。 | `CreateWelcomeDialog.kt` 已有视觉结构及关闭操作，生命周期待比较。 | 打开页面、关闭、重新进入。 |
| 操作及导出 | `action-menu.tsx` 执行选中动作，`open-preset.tsx` 解析代码后应用配置。 | `CreatePage.kt` 的命令回调只关闭面板，`CreateCustomizer.kt` 的短码由哈希生成，导入只判定非空。 | Shuffle、Reset、Get Code，复制后打开预设，以及无效输入。 |

## 实施记录

Create 按用户流程登记，组件交互按输入方式和平台登记。状态使用待验证、待修复、待复测、已验收、保留或受阻。每次完成对照、复现、修复或验证后更新对应记录。

| 编号 | 页面或组件 | 场景与预期 | 发现或复现 | 状态 | 验证证据与提交 |
| --- | --- | --- | --- | --- | --- |
| CREATE-LAYOUT | `/create` | 在桌面选择 Radius None 和 Preview 02，缩至手机、转横屏后回到桌面，保留选项和预览。 | 既有 JS 开发产物在 Chrome 中复现，1440×1000 → 390×844 → 844×390 → 1440×1000，后两次均失去 Radius None。当前源码已让配置区和预览跨断点保留组成。 | 已验收 | 当前 JS 与 Wasm 开发分发在 Chrome 157.0.8103.79 浅色和深色下均保留 Radius None 与 Preview 02，已保存桌面、手机和横屏截图。共享 JVM 回归 1,513 项、ktlintCheck 和 Detekt 通过。提交 `b42e3e68`。 |
| CREATE-01 | `/create` 命令面板 | 选择 `Preview 02` 后切换预览 | `CreatePage.kt` 的处理器只关闭命令面板。Chrome 157 在既有 JS 产物的 1440×1500 视口实测，选择 Preview 02 后面板关闭，Payout Threshold 仍显示。 | 已验收 | 命令与画布选择器使用同一预览索引。Chrome JS/Wasm 的 Preview 01/02 均通过。 |
| CREATE-02 | `/create` 配置 | 样式、底色、主题色、图表、字体、圆角、图标即时影响预览。 | 配置已统一，Preview 02 色样使用实际主题和图表颜色，字体及图标选择已接入。 | 已验收 | Chrome JS/Wasm 浅深、桌面及手机的样式即时更新和导出值已核对，图表颜色及完整配置往返由共享 JVM 覆盖。 |
| CREATE-03 | `/create` 操作 | Shuffle、Reset、Get Code、Open Preset 均可从页面或命令面板触发。 | 移除了伪短码和空导入，增加版本化配置、完整往返测试和无效输入提示。 | 已验收 | Chrome JS/Wasm 各四种主题与视口组合通过往返，桌面命令验证 Reset、Shuffle 与 Get Code。 |
| INPUT-02 | Slider | 从横向滑块内纵向滑动时页面可滚动，值不改变。 | Chrome 157，390×844 触屏模拟，在既有 JS 产物的 `/slider` 上纵向滑动 140 px，页面位置不变，Volume 75 → 50。横向滑动 80 px 可调整至 82。修复先等待触屏方向判定，横滑、点按才进入 Slider，取消后清除按压并跳过提交。 | 待复测 | 25 项定向 JVM 测试通过。修复后 JS/Wasm 模拟通过，纵滑页面滚动、数值保持 75，横滑仍调整到 82，阈值前取消保持 75。核心静态检查及真机待复核。 |
| INPUT-01 | Carousel | 从轮播区域纵向滑动时，页面仍能滚动 | JS，Chrome 157，390×844 触屏模拟。纵向滑动 145 px 后页面滚动 258 px，仍在首张。横向滑动 150 px 后已翻页，页面没有纵向位移。 | 待复测 | JS 和 Wasm 模拟通过。RTL 与真机待复核 |
| INPUT-03 | Sheet、DropdownMenu | 鼠标按下移出再抬起不打开，触屏点按能打开，Escape 关闭后键盘可再次打开。 | JS/Wasm 的现有示例均通过。Sheet 使用 Tab，DropdownMenu 使用方向键。 | 已验收 | Chrome 157，1440×1000，CDP 切换触屏。Sheet 的滑动关闭由专用 Drawer 提供。 |
| INPUT-04 | Hover Card | 鼠标移入、移出及触屏长按后切回鼠标均无残留。 | JS/Wasm 均通过。长按不打开悬浮预览，返回鼠标后仍可正常打开。 | 已验收 | Chrome 157，1440×1000，CDP 切换触屏。 |
| INPUT-05 | Tooltip | 触屏长按不遗留提示，键盘聚焦仍显示。 | JS/Wasm 基线长按后仍显示 Add to library。已将聚焦触发限定在键盘输入模式，并新增输入模式切换回归。 | 待复测 | 待用更新后的 JS/Wasm 检查。 |
| INPUT-06 | NavigationMenu | 鼠标悬停、触屏点按开关及切回鼠标行为。 | JS 现有示例通过。 | 待复测 | Chrome 157，1440×1000。Wasm 待重跑。 |
| INPUT-07 | ScrollArea | 容器内滚动、边界让渡、方向键和 RTL。 | 核心 JVM 已有鼠标拖拽、滚轮、触屏滚动、边界、键盘和 RTL 用例。 | 待复测 | Chrome JS/Wasm 中内层标签移动 67 px，页面维持原位。内层到达起始边界后，外层页面从 80 px 回到 0 px。 |

行为缺陷附修复前后的复现步骤和实际结果。视觉差异附截图及视口和主题。滑动或取消类问题可附浏览器脚本、事件记录或真机录像。

## 保留差异与阻塞

- 核心全量 JVM 运行 1,810 项，唯一失败 `ChoiceNovaParityTest.editableComboAndInlineButtonGroupStay32High`。InputGroup 实际 38 dp、预期 32 dp。切回 `b42e3e68` 中的组件源码和原有测试集单独运行，复现同一失败。
- 本机 `adb devices -l` 未列出 Android，`devicectl list devices` 显示已配对的 iPhone 16 Pro 为 unavailable。真实触屏验收待设备上线，浏览器触屏模拟结果单独记录。
- Gradle 默认沙箱无法写入用户缓存。授权运行后，联网解析了离线缓存缺少的依赖。布局初轮发现一处 ktlint 换行问题，已修正，当前布局的 ktlint 与 Detekt 报告无发现。JS 开发分发已构建。组合运行中 JVM 测试编译及 Wasm 链接失败，诊断含堆内存不足和后端错误，扩大 Kotlin daemon 堆后 Wasm 开发分发构建通过。JVM 改用 10 GB Gradle 进程内编译后，共享 JVM 回归通过。

## 布局证据

- 本轮 JS 入口 SHA-256 为 `0b73359540d4841db1985df7642468f0e0228e4b81fdf79894098b89717286cb`。
- 截图已保存于本会话产物的 `create-layout-js` 与 `create-layout-wasm` 目录，分别包含浅深主题的 desktop、phone、landscape。Chrome 截图对应验证视口 1440×1000、390×844 和 844×390。

## 阶段收尾

每个阶段结束时记录完成项、剩余项、通过的检查和提交。视觉验收与交互验收分别注明覆盖范围。

- Create 布局批次已提交为 `b42e3e68`。提交时按路径限定范围，并在验证后跳过会自动暂存其他工作区改动的提交钩子。

- 配置实现采用 Compose Foundation 本地往返格式 `cf1`，可读取裸代码或 `--preset` 前缀。上游 CLI 短码和旧版不可解码的本地随机短码会显示验证反馈。
- 配置批次的 Inter 来自 Google Fonts `5bd87dfc`，替代图标来自 Iconify `81ad501f` 的 Hugeicons、Tabler、Phosphor 和 Remix Icon 集合。各许可证随资源保存。

- 配置批次已通过 1,516 项共享 JVM 测试、ktlintCheck、Detekt 及双 Web 开发分发。Chrome 157.0.8103.79 中 JS/Wasm、1440×1000 和 390×844、浅深主题共八个预设往返场景通过，截图和脚本保存在本会话的 `create-actions` 产物目录。JS 入口 SHA-256 为 `f0fe5b944326ecf15c80bb1da93aa11799e79c7ec7a67eee598ed70d94590ee6`，Wasm 入口 SHA-256 为 `3843b106a934b0e2c3fafe185b73e87ea0bb7e8bab30c718192a90800d3f3e16`。
