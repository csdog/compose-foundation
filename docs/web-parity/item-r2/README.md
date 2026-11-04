# Item 第二轮：标题换行、菜单富内容与 RTL 页面

2026-11-04。对照 [官网 Item](https://ui.shadcn.com/docs/components/base/item)，固定上游 `96da9cc7f1aa10580a3741664e2a77c721206c00` 的组件、MDX、11配方及Nova样式，文件与SHA见 [sources.json](sources.json)、[source](source)。本轮保持 Item 未勾选；Editor排除、Attachment后置。

## 修改与依据

- ItemTitle 默认自然换行，新增 String/AnnotatedString 公共 `maxLines` 参数。无限行使用 Clip，显式有限行使用 Ellipsis。官网普通标题虽有 `line-clamp-1` 类，实际 `display:flex` 不产生该截断；390视口的验证标题为两行。Image配方显式类覆盖后为 flow-root，仍单行，因此该配方和复制代码显式 `maxLines=1`。真实计算样式、尺寸见 [official-titles.json](browser/official-titles.json)。不能仅按类名推断浏览器行为。
- Nova Xs Item 在真实 DropdownMenuContent 内默认padding为0，上下文进入主题缓存key。公开调用者Style仍可覆盖，其他尺寸/预设不变。移除目录手写8dp padding，使三个人员的头像与文字恢复同排。官网MenuItem padding4px6px、内层Item padding0，测量见 [official-menu-layout.json](browser/official-menu-layout.json)。对应CSS上下文选择器优先于配方p-2；本项目使用组合上下文表达默认值。
- RTL配方采用单一288dp正文与40dp inset，保留继承主题、Noto Arabic/Hebrew、完整三语标签及两行内容。示例和公开复制代码同源生成；导出不依赖目录helper或测试标签。API表补maxLines。
- 公共行为和参数变化使版本 `0.3.37 → 0.3.38`。原lambda重载保留；没有改变非Nova默认标题行为。

## 验证

基线23项。标题自然换行断言先RED一项；菜单三头像横向位置断言四种组合先RED四项，修复后转绿。最终常规验证51项：核心ItemParity12、ItemScreenshot4、目录ItemExamples8、ItemPageLayout12，以及实际依赖ItemTitle的SpinnerExamples7、SpinnerViewport8。XML与摘要见 [tests](tests)、[test-summary.json](test-summary.json)。覆盖普通标题窄屏两行/桌面一行、显式单行、RTL正文尺寸/语言/方向/导出以及菜单同排布局。

六张既有像素基准（Nova2、Vega4）显式verify均通过，未刷新golden，见 [pixel-verify](pixel-verify)。旧Nova长标题截图配方改为显式maxLines1，继续验证截断外观；默认自然换行由新的行为断言验证，不借刷新基准隐藏默认变化。core/shared ktlint通过。默认detekt公共源码sourceCount=0的限制继续适用，不能据任务退出0宣称公共源码完整静态分析通过。

JS/Wasm development distribution构建退出0，保留既有Text INVISIBLE_REFERENCE、webpack os/path诊断与warning。构建前后11项输入摘要一致，见 [input-before](input-before.json)、[input-after](input-after.json)。主Wasm为 `43f9620e3dea62e316fb.wasm`，43,479,040字节，SHA256 `1ec9fa100cd8dc414b705efa19073f6e1c65214ea5411228c2acc80312e2a856`；辅助Wasm与摘要见 [wasm-artifacts](wasm-artifacts.json)。

## 浏览器证据口径

复用用户授权的专用TaskSpace1，官网p1、本地p2；1440/390×1000、浅/深、DPR1，mouse/mobile:false，窄屏不等于真实触屏。11配方四组合各44张静态；官网另24张交互（菜单展开/方向键/Enter关闭及三语言各四组合），本地另24张交互（菜单12张、三语12张）。官网与最终本地各68张，20张最终拼图已全部目视复查；摘要见 [images.json](browser/images.json)、[review.json](review-sheets/review.json)。修改前44张在 [before](before)，最终图及资源/导航记录在 [browser](browser)，拼图在 [review-sheets](review-sheets)。

原先强制空hash再跳章节的五张空白帧单独留在 [diagnostics-empty-frames](diagnostics-empty-frames)。最终改为连续不同锚点重新采集44张，均非纯色空帧；未将采样变化宣称为绘制时序根因修复。菜单旧8dp结果单列 [before-menu-padding](before-menu-padding)。旧语言坐标采集与真实标签不一致的12张单列 [diagnostics-language-coordinates](diagnostics-language-coordinates)，不计通过；旧local-actions.json已移入诊断目录，最终语言以local-languages.json为准、菜单以local-menus.json为准。Canvas可访问层在弹层关闭后仍可能保留菜单文字，且同名Select会命中侧栏，故不能仅据snapshot出现语言名称判定切换成功。最终按真实画面位置操作并复查标签与正文。另发现暗色菜单等待1000ms仍截到淡入中间帧，保留menu-dark-intermediate.png；等待2000ms并移动指针后补拍稳定展开图，不将此采样策略称为动画/绘制时序修复。

## 未关闭差异

- Header窄屏：官网三卡宽71px、内padding10px12px，剩余45px；标题实际min-content宽约66.09/58.69/60.98px，前两项单行并溢出约束区。原生受约束后换行；这是CSS flex自动最小宽度与溢出模型差异，不仅是字体问题，尚未用硬编码padding/截断掩盖。
- 链接行为：官网 `href="#"` 在本页导航，原生Web的Compose 1.12.2 UriHandler调用window.open默认_blank。真实点击本地打开p3，父页不跳转，验后关闭p3，见 [link-navigation.json](browser/link-navigation.json)。HTML链接role、target/rel、修饰键及同页导航仍待补齐。
- Dropdown指针打开后，官网不高亮首项，首个ArrowDown到shadcn；本地立即聚焦首项，ArrowDown到maxleiter。两者Enter均关闭；不能据关闭成功宣称初始焦点对齐，移交Dropdown Menu专轮。菜单上下文跨嵌套portal、动态换主题及完整尺寸/预设矩阵未穷尽。
- 正文字重/抗锯齿、换行/text-pretty、图片裁切、官方默认示例后的说明、共享代码与Link面板仍有差异；复杂slot重排、任意Style覆盖、完整动态/焦点/触屏/辅助技术未完成。Item仍未通过整项验收。
