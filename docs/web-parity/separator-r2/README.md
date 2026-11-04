# Separator 页面与浏览器第二轮

2026-11-04，对照[官网Separator](https://ui.shadcn.com/docs/components/base/separator)。固定上游 `96da9cc7f1aa10580a3741664e2a77c721206c00` 的文档、组件及五配方见[source](source)，URL/摘要见[sources.json](sources.json)；当前DOM的尺寸、role和RTL字体见[live-dom.json](live-dom.json)。

## 本轮修正

- 页面只保留Default、Vertical、Menu、List、RTL五个官网配方，移除后面十个重复旧示例。使用/API及Compose竖线约束、decorative/语义差异集中说明，公开组件和版本不变。
- 补Vertical/Menu/List说明；复制代码从实际编译配方生成，包含完整公共字体资源和调用入口，不含目录内部helper导入及测试标签。
- RTL使用官网Noto Arabic/Hebrew字体，继承现有主题设置和单位间距，保持真实三语文案与布局方向。采用单一288dp正文/40dp inset，取代旧共享RTL预览在窄屏按自然高度和24dp inset的路径；文本Tight模式保证声明的20dp完整行框。
- Menu按真实CSS视口768dp阈值切换，不再按减去滚动条的画布宽度。预览使用既有catalog视口桥接；导出函数允许调用方传入自己的视口宽度。767/768原生定向测试与浏览器两主题边界均保留。

## 验证

[常规测试](tests)44项通过：核心14、目录既有20、页面/RTL字体与行框8、CSS视口断点2。包括1dp分隔线、20dp竖行/40dp菜单、List间隔、三语文案、语义/decorative及尺寸/颜色Style覆盖。

显式六张旧截图verify中五张通过，旧Vega states_dark仍有20像素变化；actual与第一轮保留图逐字节一致。[legacy-differences](legacy-differences)保存XML、actual/compare与比较摘要，未刷新任何基准。普通测试退出成功与显式像素结果分开记录。

目录ktlint与JS/Wasm development构建通过。最终主应用 `14bb8406988c01fe3e72.wasm`，43,553,221字节，SHA256 `0b505f372a6762477c86ed364ff70b7e69f5e129ffcf8e33890f6dad75cf8662`，验证/输入摘要见[validation.json](validation.json)。既有AGP/Gradle/Text及webpack os/path诊断保留，默认detekt不覆盖commonMain的限制没有据任务成功而关闭。

## 浏览器

沿用授权专用空间1，1440/390×1000、DPR1、mouse、mobile:false。官网和最终本地各20静态、8三语补充、4断点图；390是窄屏鼠标模拟。修改前20张本地主要配方及4张边界图在[before](before)；其中边界旧帧出现热态画布顶端空白，作为诊断保留，不当作正常页面像素通过证据。记录显示旧768px画面仍只有Settings/Account，官网已显示Help。

最初三语批次沿用别页选择器坐标；移除旧配方后RTL接近页尾，滚动被最大位置限制，实际工具栏不再位于原坐标，导致错误语言帧/主题等待超时。它们在[diagnostics-language-coordinates](diagnostics-language-coordinates)，不当作语言成功证据。最终每组重新载入阿拉伯语初态，在桌面页首切换主题后再切换宽度；手机菜单需按中心位置并等待定位稳定，已重做四张手机帧。最终八张补充图均已查看实际选中语言和正文。[browser](browser)保留最终原图及实际应用资源，最终14张拼图均已目视复查，见[review-sheets](review-sheets)。

## 尚未关闭

- 两平台的字重/字形边缘、英文与RTL说明换行、默认标题leading-none绘制、共享章节位置、代码栏/API排版及RTL说明链接不同；五配方存在不等于整页像素一致。
- 真实HTML role=separator/aria-orientation与dl/dt/dd没有原生Compose完全等价映射；contentDescription/stateDescription是明确的平台适配。读屏、真机触屏及任意密度/字体/Style动态矩阵仍需验证。
- 任意无界父级的CSS self-stretch未声称支持；竖向分隔线需要调用方给出有界高度或IntrinsicSize.Min。旧Vega暗色20像素差异保留。

Separator保持未勾选。Editor排除、Attachment后置；整站目标继续。
