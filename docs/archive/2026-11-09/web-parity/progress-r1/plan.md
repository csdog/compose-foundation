# Progress 第一轮（2026-10-31）

固定官网 Base/Nova 上游96da9cc，8份归档来源（含Base UI1.6Value的aria-hidden语义）；Tailwind transition-all默认150ms/0.4,0,0.2,1参考官方文档。

- [x] 根组件自动轨道；gap12横向标签与数值、Value逻辑auto-margin、窄宽换行。
- [x] Nova14/20sp、medium标签与muted数值；track4dp，填充末端直角、外轨道裁剪。
- [x] 逻辑目标值语义/文案与动画解耦，indeterminate语义不伪造百分比，150ms宽度过渡。
- [x] Default13→66/500ms、Label56、Controlled50/整数slider、RTL三语和Arabic数字四配方；源码生成复制代码。
- [x] 本轮核心9+新Nova4+目录16共29项通过；两项ktlint、JS/Wasm完成；原生32图已查看。旧4图混合差异保留，完整交互仍见矩阵。
- [ ] 浏览器desktop/mobile×light/dark；原空间18丢失、重建申请待回复，未另建空间。
- [ ] 完整可访问性、快速反向/中间动画像素、字体缩放/长文本、CSS颜色尺寸过渡和全部Style override等矩阵。

0.3.19迁移：content不再带ColumnScope，根组件始终追加Track，既有content中的手动ProgressTrack需移除。style改为根层应用；自定义轨道可独立使用ProgressTrack { ProgressIndicator(value=...) }。库仍以max默认1的fraction为Kotlin接口，官网0..100示例显式max100；indeterminate bool对应未知进度，不声明与上游全部原始Props逐项等价。

保留剩余差异，组件不勾选。Editor排除，Attachment遗留暂缓。
