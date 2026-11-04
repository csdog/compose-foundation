# Spinner 第一轮页面对齐

2026-11-04，对照[官方 Base UI Spinner](https://ui.shadcn.com/docs/components/base/spinner)。上游固定到 `96da9cc7f1aa10580a3741664e2a77c721206c00`，成功获取的组件、文档及八配方保存在 [source](source)，URL/摘要见 [sources.json](sources.json)。两个推测文件名404只列入 discoveryOnly，不当作来源。

## 本轮修改

- 目录由六个基础/重复示例替换为 Default、Customization、Size、Button、Badge、Input Group、Empty、RTL 八组实际官网配方。首屏是320dp muted付款Item，包含“Processing payment...”和$100.00；Usage才是单独的Spinner。首次实现误读首屏，已根据真实截图及spinner-demo.tsx修正并加回归。
- 增加保留原签名的 `Spinner { content() }` 重载，统一尺寸、旋转、加载说明和liveRegion，自定义八射线图形使用同一公开组件。图形继承内容色，未指定时回退主题前景；浏览器发现缺少此回退导致自定义图形不可见，已修复并增加实际墨迹断言。
- 尺寸12/16/24/32dp；三个禁用Sm按钮、三个带12dp图标的Badge、两组禁用输入及仍启用的Send、带Cancel的Empty均使用公共组件组合。Badge窄屏保持官网单行，不靠压窄最后一个Badge适配。
- 官网Send与Cancel都没有业务回调，点击保持加载演示；本地没有增加虚构的发送或取消完成状态。InputGroup根不整体禁用，避免连带禁用Send；只禁用两个输入控件，既有InputGroup根据后代状态同步半透明外观。
- RTL沿用真实三语文案、Noto Arabic/Hebrew、逻辑顺序及16dp基础圆角。导出代码包含公共组件与字体资源，不依赖目录私有语言工具栏。
- `TwAnimate.rememberSpinState()`提供状态形式，`animateSpin`在graphicsLayer读取角度，保留原Float形式API。1秒线性旋转不变；RED记录显示256ms内调用方多重组16次，修正后保持绘制变化且调用方不随帧重组。
- 公共版本与安装说明更新为0.3.37；原LoaderCircle圆弧几何和Style/Modifier尺寸优先级保留。

## 验证

[测试XML](tests)共22项通过：核心7项（几何1、尺寸/自定义slot3、真实动画1、既有截图2），目录Examples7和四种显示组合Viewport8。涵盖八配方顺序、四尺寸、禁用按钮、输入禁用但Send启用、Cancel演示行为、首屏付款卡片、自定义图形可见墨迹、Badge三图标单行、三语切换与实际左右镜像。初始RED在tests/red。禁用输入不暴露SetText action，测试按EditableText语义定位后检查disabled，没有为了测试增加编辑动作。

两张既有Spinner基准又显式verify，均0像素变化，没有重录。core/shared/tailwind-core ktlint及JS/Wasm development构建通过。最终应用 `e3cb8a2d32ed55f173f7.wasm`，43,569,043字节，SHA256 `74a7883439c42f17ba097e25a7b40aed99313c7a99be5f78356f707690206621`。源码摘要见[source-inputs.json](source-inputs.json)，验证汇总见[validation.json](validation.json)。既有Text INVISIBLE_REFERENCE、AGP/Gradle及webpack os/path诊断保留；默认detekt不覆盖commonMain的问题没有因任务成功而视为解决。

## 浏览器范围

沿用用户授权恢复的专用空间1。1440/390×1000，DPR1，mouse，mobile:false；390为窄屏鼠标模拟，不是真机触摸。官网及最终本地八配方各32静态图，另保存英/希两语各8图、Send/Cancel点击后各8图；原图及状态记录在[browser](browser)，16张对照拼图在[review-sheets](review-sheets)，均已目视复查。本地resource记录对应最终构建。旋转未暂停，截图角度随采样时刻变化，几何由独立SVG参考和既有像素测试验证，不能按随机角度宣称逐像素相同。

## 明确剩余差异

- 共享页面的标题、正文宽度、预览/代码底栏高度与章节节奏不同；Button/Badge的解释段落、RTL说明链接及Customization下额外源码面板尚未完整对齐。
- Item字重、标题省略位置及阿拉伯金额字形/排版仍有差异；窄屏三语方向已核对，不能据此宣称所有字体行框一致。
- Empty描述的实际换行宽度及字重仍不同。即使公共Style声明maxWidth384dp，Compose与CSS的固有测量/换行结果仍需继续在共享Empty/Text组件核对，不用示例硬编码行分隔掩盖。
- 实拍Send获得焦点后，本地InputGroup把后代按钮焦点计入整组focus，出现官网未出现的组边框变化；需在InputGroup专轮收窄到输入控件focus-visible，仍开放。
- 输入组、按钮、Badge的完整hover/pressed/focus/动态shape-theme过程尚未逐项关闭；本轮验证加载、禁用、现有演示动作和三语布局。
- Canvas contentDescription/liveRegion不等于原生HTML `role=status` 的DOM与读屏行为；辅助技术、系统减弱动画及实际触屏仍需专门验证。没有为了截图关闭无限动画或更改官方演示行为。

Spinner保持未勾选。Editor排除、Attachment后置，整站验收继续按清单推进。
