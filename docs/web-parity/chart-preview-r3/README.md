# Chart preview r3 — fixed steps and responsive inset

官网五个First Chart步骤的预览区均为320px；桌面Steps将内容缩进48px，390窄屏取消缩进。实测桌面preview589×320、padding40、SVG509×286；窄屏325×320、SVG245×200。SVG在固定预览内居中，桌面自然高度286超过扣除padding后的240，不能简单把图表限制到240。见[官网几何](browser/official-geometry.json)，来源沿用[Chart r1固定源码](../chart-r1/sources.json)。

新增目录元数据`previewHeight`（默认null）与`desktopPreviewInsetStart`（默认0），由页面第一例和后续例统一传给PreviewCodeSection。仅五个Chart步骤设置320/48；标题仍保持主列对齐，card和其代码面板缩进，640以下取消缩进。固定预览通过unbounded wrapContentHeight保留图表自然比例并居中，绘制限制在预览内。其他预览保留原最小高度行为。

验证完成本轮范围：8项新Frame测试已通过，覆盖1440/390×浅深中的320高度、48/0缩进、自然图表510×286与260×200居中，以及展开代码后remember点击计数保留。既有ChartExamples32、DocCodeBlock4、ComponentDocumentation7及shared ktlint均通过，本轮共51项独立执行。4张新原生图已目视复查；44张原配方图字节不变。

[最终验证](verification/summary.json)：Wasm开发distribution22.520s通过（1项webpack warning），8项输入SHA一致。实际加载新`2bca779a33ae63e2c8c7.wasm`后，五步骤四象限20图已通过8张原像素拼图全部目视；另实际展开Legend代码面板，图表高度保持不变且源码可见，见[浏览器记录](browser/review.json)。生成脚本再次运行幂等。

本轮不改变核心Chart渲染，r2核心证据继续保留；51项是本轮app定向测试，不与上轮64重复累计。原44图与final60一致，复用其目视结果。

Chart整页尚未完成。Add Grid/Axis/Tooltip/Legend当前仍为h2而源为h3，完整编号Steps/说明尚未接入；主图header、日期刻度、字体及文档章节、动效/辅助技术继续开放。源与本地全局内容宽度也仍相差桌面1px、手机15px，不能称像素相同。未运行或刷新旧golden，未执行全仓提交门禁或新增commit。
