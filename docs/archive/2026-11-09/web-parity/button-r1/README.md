# Button：图标边距、配方与 RTL（2026-11-04）

对照 [官方 Base UI Button](https://ui.shadcn.com/docs/components/base/button)，固定版本 `4aaea19d9cff3e0908e12a8c1d504b8c5ec34610`。MDX、十五个实际 docs 示例、Button 和 Nova 样式原文见 `source/` 与 [sources.json](sources.json)。本轮继续逐组件复查，Editor 排除、Attachment 后置；Button 尚未全部验收。

## 实现

- 补公开 `ButtonIconPlacement.Start/End/Both`，支持两种重载；保留两个旧 JVM 方法入口。四种文字尺寸：Xs普通边8/图标边6、Sm普通边10/图标边6、Md/Lg普通边10/图标边8；边框另加1。逻辑边按 LTR/RTL 映射一次。纯图标尺寸不受此提示影响，调用者 Style 最后覆盖，原有最小触摸区和shape反馈路径保留。
- Nova 默认箭头光标，调用者可通过 Modifier 显式覆盖；旧 Vega 手形行为保留。字符串便利重载单行，对应官网 whitespace-nowrap。
- Nova 16/20dp 行框采用 Tight，修正 Noto Arabic 将按钮文字行框撑到30dp的问题，实际阿语Text 20dp和按钮32dp均有回归。
- 页面整理为14组实际预览：首屏、Size、Default、Outline、Secondary、Ghost、Destructive、Link、Icon、With Icon、Rounded、Spinner、Button Group、RTL，补 Cursor、Composition 和公开参数说明。Size 四组文字/图标配对，640dp 以下竖排；Rounded 使用调用者 Style.shape(CircleShape)，三语言 RTL 同步字体和方向，箭头镜像。
- 官网 Spinner 两按钮宽114.10+8+127.67=249.77px，在手机245px预览内区仍保持单行、略超出内边距。本地之前 FlowRow 折行，现非RTL示例使用保留自然宽度的 Row；不是压小控件或字体。RTL 配方按子项真实测量的自然总宽限于可用宽度，再换行并逻辑定位；手机保持245dp可用宽度、各行靠右，桌面保持自然单行。
- 安装文档和 VERSION_NAME 同步0.3.31。

## 验证

根目录 `tests/*.xml`：**17项独立用例通过**（ButtonIconPlacement2、ButtonNovaParity7、ButtonExamples8）。新回归覆盖32组逻辑边/四种尺寸组合、Style覆盖、纯图标尺寸和Vega回退；目录测试覆盖两尺寸浅深、Size断点、加载同行及禁用、三语切换、RTL行框/换行对齐。`tests/red/` 和 `red-arabic-line/` 保留旧失败，不计为通过。ButtonNova 两张既有基准显式verify通过，本轮未改任何Button基准图。二进制入口见 [binary-entry-points.txt](binary-entry-points.txt)。构建及浏览器最终结果见 [validation.json](validation.json)。

## 仍需继续

- 官网十五组中的 As Link 尚未补入：必须使用真实链接语义和按钮样式组合，不能把带 Role.Button 的 Button 当链接。完整填充区命中、URI处理和浏览器原生链接能力需一起验证。
- Button Group 当前复用既有公开目录示例；仍有 More Options 图标、菜单图标/Other条目/单选状态与官网不同，不能视作完整移植。后续需连同 Button Group 本页统一修正与验收。
- With Icon 官网用 Tabler GitBranch/GitFork，本地暂用 Lucide，对应图形仍有差异。
- 固有尺寸查询与实际 Style 测量仍有差异：新增浏览器回归发现强制 IntrinsicSize.Max 会把桌面 RTL 错分两行，现配方走真实自然测量并补同行断言。任意调用者的固有布局组合仍待核验。
- aria-invalid 错误边框/ring、展开菜单的 aria-expanded 样式及完整变体 hover/pressed/focus-visible、实时 theme/shape、自定义slot与minTouchTarget、辅助技术/触摸尚未关闭；本轮定向测试不等同于全矩阵。
- 共享页面垂直节奏、描述文字、代码面板和暗色字形观感仍保留。七组重点预览截图不代替其余预览的完整浏览器验收，也不关闭全站 P1/P3。


## 最终浏览器构建

最终 `7c866eaec21232f3b9a4.wasm`（43,015,469 bytes，SHA256 `e3dfbcab91caa1ddbd1b1b67799e5e14b276e3bcff13807543fec5fbf61265e1`）。七组重点配方：首屏、Size、With Icon、Rounded、Spinner、Button Group、RTL；1440×1000和390×1000 CSS px、DPR1、浅/深各轮，官网/本地各28张均已查看，原图/位置/加载app记录在 `browser/*-matrix.json`，查看拼图在 `browser/contacts/`。错误RTL旧图仅在 `browser/diagnostics/`。默认Arabic的四象限已浏览器验收；英语/希伯来语切换由8项目录测试覆盖，尚未声称其浏览器完整矩阵通过。DOM光标抽取没有找到canvas，不接受其为空作为通过证据（`local-cursor.json`已标注）；默认光标变更目前仅有源码依据。

`.git-hooks/pre-commit` 已接入可选已安装的 compact Gradle wrapper 路径与有效 workflow ID，保持原有三项检查及默认开发流程。hook直接Gradle与验证wrapper的衔接问题已解决；后续集中checkpoint的门禁结果以实际Git提交和wrapper摘要为准。默认detekt未覆盖commonMain的既有限制仍然保留。
