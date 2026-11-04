# Scroll Area 第一轮（2026-10-31）

已归档来源并开始实现；49项独立定向验证和两个ktlint已通过，JS/Wasm退出0但仍有诊断，详情如下。官网 Base Nova MDX 实际有 Default、Horizontal、RTL 三组配方；来源固定96da9cc，13份上游源码/锁文件摘录含Nova CSS已归档并校验哈希，另有三份图片来源清单。官网页面读取只用于文档/示例清单，不作为浏览器验收。

- [x] 来源、配方列表及Nova样式读取；另查询Base UI1.6.0官方文档中的Root/Viewport/Scrollbar/Thumb/Corner组合。
- [x] 锁文件确认为Base UI1.6.0，归档Root/Viewport/Scrollbar/Thumb/Corner及constants；最小thumb16px，默认overflow时显示。
- [x] 已实现并验证 Default：192×288dp、边框/圆角、16dp内边距；50个版本号、Tags标题与Separator间距。
- [x] 已实现并验证 Horizontal：384dp宽框、三幅300×400照片、16dp间隔及内边距、作者caption与横向Scrollbar；真实素材/加载/窄屏。
- [x] 已实现并验证 RTL：阿拉伯语初始、英文/希伯来语；文本与滚动条方向、滚动位置保留。
- [x] Nova10dp轨道、7dp thumb、16dp最小长度及端点限制；CSS outer shadow修复透明Viewport染灰，增加outline；自定义Root shape需显式viewportStyle。外缘裁剪仍未关闭。
- [x] 定向验证真实viewport/track wheel、touch、thumb拖动与轨道按下、方向/Home/End/Page/Space、双轴Corner、RTL、子文本框不夺键、真实Lazy拖动和轨道跳转。
- [ ] 完整OS trackpad、嵌套边界、动态溢出/方向/样式及读屏矩阵。
- [ ] 浏览器桌面/手机浅深对照及原生必要测试；浏览器空间重建仍待既有询问答复。

不把目前Hover/Always等扩展示例视为官网配方已迁移。Editor排除，Attachment遗留暂缓。

本轮变更：默认ScrollArea.type由Hover改Auto（需要旧600ms行为的调用方显式传Hover）；Nova轨道10dp，padding/border占3dp使可见thumb7dp，最小16dp，按maxValue/实际travel定位。新增轨道按下居中并可继续拖动、RTL水平反向、双轴预留corner、溢出时Viewport聚焦及键盘Home/End/方向/Page/Space；LazyScrollArea仍保留扩展Hover默认，复用thumb修正。

待明确的差异：Viewport继承任意Root shape、focus outline/ring外缘裁剪、原生滚动与浏览器OS键盘步进/平滑过渡、平台touchpad/完整辅助技术/动态样式与Lazy矩阵，不能仅靠定向测试关闭。

验证记录：11个真实交互、12个几何数学、4个可见性策略、4个Lazy metrics、6张新Nova图独立verify、12个目录场景共49通过；两模块ktlint通过。28张目录图和6张新图均已查看。旧4图只有ScrollArea浅色通过，深色thumb540像素颜色差异未归因，Lazy浅深缺少原始基线；未重录旧图。

版本0.3.22。浏览器验收和差异矩阵全部关闭前不勾选组件。下一项优先Scroll Fade。

最终构建：JS34.428s退出0但保留os/path解析诊断；Wasm19.890s退出0，一条webpack警告。源码/新图/28张原生图哈希均匹配，工作流已结束并清理原始日志，最终证据已归档verification。未声称全仓门禁通过或所有目标已验收。
