# Accordion r7 — fixed previews and keyboard orientation

2026-11-02后续更正：[r8实测](../accordion-r8/README.md)确认官网缩放后同样不会自动重新定位hash；本地再次点击目录原已正常。因此下文旧记录中的“锚点问题”不能作为本地与官网的差异。动态重排已由r8三项RED回归与47项最终验证修复，桌面键盘画面亦已补验。

[九份固定上游源码](sources.json)与[官网当前字号](browser/official-typography.json)已复核。官网Trigger为Geist14/20、500字重、42px高，正文14/20、400字重。六个预览固定为Default/Basic/Disabled/Borders300、Multiple450、Card435；均`align="start"`，p40。RTL另有外置语言工具栏，暂不把本地现有内部Select强塞进固定288高。

目录接入`previewHeight`及新的`previewVerticalAlignment=Alignment.Top`，初始与切换后保持顶部40px位置及固定外框。共享固定预览原先独立clipToBounds不符合官网overflow-visible；现由外层圆角卡片裁剪，与源码一致，Chart默认居中和既有尺寸保持。官网Multiple三项展开时Accordion478高、preview仍450，尾部进入源码区后被覆盖/裁剪，见[实测](browser/official-all-open-geometry.json)。本地三项同时展开的浏览器对照还未确认，不能从单项切换测试扩大结论。

官方例改回Basic/Multiple/Disabled/Borders/Card/RTL名称；后续扩展标记Kotlin extension，消除重复Multiple等标题。补Composition树并修Usage导入缩进和AccordionType漏导入。其余指南/准确API章节仍需整理。

本轮发现核心故障：Horizontal的方向键焦点能到第二项，但正文存在却不可见。根Row及Item内部Row让首项fillMaxWidth耗尽空间。上游Root无论orientation都为flex-col；orientation控制键盘轴。现恢复默认竖排结构，保留Horizontal左右键/RTL方向，KDoc说明明确。不是给横排均分宽度另造样式。

最初新56项中Frame24全部通过，Extension32有8项正文not displayed失败。修复后32全部通过，并增加第二Trigger在第一下方的源契约断言及8张原生图；断言未弱化。最终78项独立验证=Frame24+Extension32+旧AccordionExamples2+ChartPreviewFrame8+AccordionNova12，core/shared ktlint通过。Wasm开发distribution26.837s通过（1条webpack warning）；10项输入SHA一致。见[摘要](verification/summary.json)。本轮没有production或旧golden验证/刷新。

48张Frame原生初始/切换图通过8张原像素拼图目视，8张LTR/RTL扩展图通过4张仅加主题底色的原像素拼图目视；后者捕获边界截断外侧focus ring，不能证明完整焦点阴影像素。4张ChartFrame与r3字节相同。[原生拼图](native-review-sheets/layout.json)、[扩展拼图](extension-review-sheets/layout.json)。

真实浏览器：官网与本地六例×1440/390×浅深各24张，最终48张全部通过8张配对拼图复查。首批本地深色误标已替换；确认App.kt手动主题仅remember，整页导航会重置，最终截图在导航后设置主题并逐张校验背景像素。源/本地滚动位置不同，拼图不是整页像素相同声明。手机浅深实际Right/Enter让Styled?获得焦点并完整显示回答；桌面尝试遇到缩放后相同fragment锚点偏移，截图显示Vertical，不算通过。见[浏览器记录](browser/review.json)。658820...是Skiko运行库，4519defb...才是当前app Wasm，不能把前者认作旧app模块。

剩余：RTL工具栏/Select精确布局、完整扩展API与Style/读屏/触摸矩阵、源码与本地Card文字换行、全局列宽/scrollbar及代码面板1px边框统计、深色字形观感。当前Horizontal键盘排序主要按x而新默认呈竖排，动态重排的同列次序需要新回归，不能用静态两项矩阵宣称完成。目录相同fragment在宽窄变化后未重新对齐、全页重载的手动主题丢失已明确列为共享问题。Accordion继续未勾选；全仓提交门禁未运行，本轮未新增commit。Editor排除、Attachment遗留后置。
