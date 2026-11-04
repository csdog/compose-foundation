# Badge：官方七示例复查（2026-11-04）

对照 [官网 Base UI Badge](https://ui.shadcn.com/docs/components/base/badge) 与本地 `http://127.0.0.1:8765/badge`。上游固定 `4aaea19d9cff3e0908e12a8c1d504b8c5ec34610`，完整路径/URL 见 [sources.json](sources.json)，原文在 `source/`。实际 MDX 引用的七个 docs examples 为准；registry gallery 的 `badge-example.tsx` 仅作旁证，不将其额外内容误算成官网页面示例。

## 已实现

- Default、Variants、With Icon、With Spinner、Link、Custom Colors、RTL 七个示例和对应 Kotlin 配方、Composition/API 文档。Variants 五种；默认四种。自定义五色使用浅色50/700、深色950/300，对应 Tailwind v4 OKLCH 调色板。
- Badge 公开 `href`、`invalid`、`BadgeIconPlacement.Start/End/Both`。常规边距8dp，图标逻辑边6dp，slot gap4dp，图标12dp；保留原两个 JVM 方法描述符，见 [binary-entry-points.txt](binary-entry-points.txt)。显式回调优先于 href；无回调通过 LocalUriHandler 打开。
- 修复 Foundation scalar padding 的物理定位与逻辑 RTL 边的映射。错误边框及键盘焦点 destructive ring 按浅/深 .2/.4；错误语义与内容合并。Nova 文字行高16dp，Noto Arabic/Hebrew 不再撑高徽章。
- 预览 body288dp、内边40dp、RTL toolbar64dp。默认/RTL 居中满宽换行，其他行按固有宽度收缩；手机内宽245dp。语言默认 Arabic，English/Arabic/Hebrew 可切换，方向和字体同步。
- 浏览器发现 Spinner 的 Canvas 固有宽度为0，导致两个徽章错分两行。Spinner 现在在公开 StyleScope 层记录声明的尺寸，补充四种固有查询，真实测量仍交给 Style/Modifier。覆盖默认/Sm/Lg、Style 动态尺寸及显式 Modifier 优先级。百分比无确定可用宽度时采用自然尺寸回退；未声称覆盖所有百分比和动画固有测量组合。
- 安装版本与 VERSION_NAME 同步为0.3.30。

## 验证与证据

[validation.json](validation.json) 汇总 **23 项独立测试、0失败**：BadgeExtendedApi4、BadgeNovaParity6、BadgeExamples8、SpinnerIntrinsicSize2、SpinnerGeometry1、SpinnerScreenshot2。根目录 `tests/*.xml` 为最终绿色结果；`tests/red*` 保留有意义的修复前失败，不计入通过数。明确启用像素 verify 的是 BadgeNova 的2图与 SpinnerScreenshot 的2图。core/shared ktlint、JS 编译、Wasm distribution 通过；全仓 `ktlintCheck detekt` 退出0，但默认 detekt 的 commonMain sourceCount=0 限制仍存在，不能推断所有公共源码静态分析通过。

最终本地 app：`147fdb141f2a48920953.wasm`，42,913,062 bytes，SHA256 `5b1c7a958ddac8860120771e3df9dd9b27dcc03ab3899e62cf5a6b1a4befce88`。10份实现/测试输入哈希见 [inputs.json](inputs.json)。

获准恢复后复用 TaskSpace1：1440×1000与390×1000 CSS px，DPR1，浅/深各轮。`browser/official-matrix.json` 与 `local-matrix.json` 各28张；七示例共56张均已目视查看。另有英语/希伯来语各四种组合，双方共16张，也全部查看；默认阿拉伯语在28张矩阵内。`browser/contacts/` 为查看用拼图，原图保留。最终 local matrix 全部来自上述147f构建；错误的 Spinner 双行旧图仅保留在 `browser/diagnostics/before-spinner-fix/`。Canvas 菜单动画没有 DOM 可等待状态，语言交互在 popup 打开/选择后等待220ms；早期菜单尚未可操作时的空画布截图已由最终有效截图替换，不计通过。

## 单张 Nova 暗色基准的处理

旧 Nova 暗色图有150个 Outline 边框像素从38变为34。临时恢复 HEAD `df92e50d` 的旧 Badge 实现后仍得到同样差异，旧实现和新实现 actual 的 SHA 完全一致，且官网暗色边框为 white10%。因此只更新 `badge_nova_parity_dark.png`；浅色图和四张旧 Vega 基准未变，未宣称 Vega 验证通过。原图、旧实现隔离结果、新实现差异分别保留在 `baselines-before/`、`control-before/`、`current-before-golden-update/`，见 [legacy-nova-comparison.json](legacy-nova-comparison.json)。

## 仍开放

- Canvas href 不是原生 HTML anchor；Compose1.12 没有 Role.Link，目前不伪装成 Button。浏览器新标签、链接菜单、读屏 link 语义和完整跨平台辅助技术待补。
- 全部变体 hover/pressed/键盘焦点的浏览器动态矩阵与官网150ms过渡仍待关闭。测试中的错误焦点像素不代替该矩阵。
- 长文本、自定义slot、极窄宽度与百分比/动画尺寸组合继续待验。
- 页头/章节的垂直节奏、说明文字换行、暗色文字观感、代码区等共享差异仍可见；本轮不关闭全站 P1/P3，也不将 Badge 勾成完整完成。
- 本轮尚未新增 commit：工作树仍有之前批次累积修改。仓库 pre-commit 直接运行 Gradle 格式化/检查并暂存所有 tracked 修改；当前 Gradle 验证走 compact wrapper，两者的接入尚未整理。本轮未绕过 hook，也未修改 hook。

Editor 排除，Attachment 后置，均未在本轮推进。
