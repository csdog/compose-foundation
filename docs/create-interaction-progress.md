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
| CREATE-LAYOUT | `/create` | 在桌面选择 Radius None 和 Preview 02，缩至手机、转横屏后回到桌面，保留选项和预览。 | 既有 JS 开发产物在 Chrome 中复现，1440×1000 → 390×844 → 844×390 → 1440×1000，后两次均失去 Radius None。当前源码已让配置区和预览跨断点保留组成。 | 已验收 | 当前 JS 与 Wasm 开发分发在 Chrome 157.0.8103.79 浅色和深色下均保留 Radius None 与 Preview 02，已保存桌面、手机和横屏截图。共享 JVM 回归、ktlintCheck 和 Detekt 通过。 |
| CREATE-01 | `/create` 命令面板 | 选择 `Preview 02` 后切换预览 | `CreatePage.kt` 的处理器只关闭命令面板。Chrome 157 在既有 JS 产物的 1440×1500 视口实测，选择 Preview 02 后面板关闭，Payout Threshold 仍显示。 | 待修复 | 待补修复后结果 |
| INPUT-01 | Carousel | 从轮播区域纵向滑动时，页面仍能滚动 | 待在真机核对横向拖动与纵向滚动的竞争。 | 待验证 | 待记录设备、浏览器、运行时与操作结果 |

行为缺陷附修复前后的复现步骤和实际结果。视觉差异附截图及视口和主题。滑动或取消类问题可附浏览器脚本、事件记录或真机录像。

## 保留差异与阻塞

- 本机 `adb devices -l` 未列出设备，`devicectl list devices` 超时。真实触屏验收仍待连接设备，浏览器的触屏模拟结果需单独记录。
- Gradle 默认沙箱无法写入用户缓存。授权运行后，联网解析了离线缓存缺少的依赖。布局初轮发现一处 ktlint 换行问题，已修正，当前布局的 ktlint 与 Detekt 报告无发现。JS 开发分发已构建。组合运行中 JVM 测试编译及 Wasm 链接失败，诊断含堆内存不足和后端错误，扩大 Kotlin daemon 堆后 Wasm 开发分发构建通过。JVM 改用 10 GB Gradle 进程内编译后，共享 JVM 回归通过。

## 布局证据

- 本轮 JS 入口 SHA-256 为 `0b73359540d4841db1985df7642468f0e0228e4b81fdf79894098b89717286cb`。
- 截图已保存于本会话产物的 `create-layout-js` 与 `create-layout-wasm` 目录，分别包含浅深主题的 desktop、phone、landscape。Chrome 截图对应验证视口 1440×1000、390×844 和 844×390。

## 阶段收尾

每个阶段结束时记录完成项、剩余项、通过的检查和提交。视觉验收与交互验收分别注明覆盖范围。

- Create 布局批次验证通过。实施提交标题为 `fix(create): preserve configuration across responsive layouts`。
