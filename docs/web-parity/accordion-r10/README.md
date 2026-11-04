# Accordion r10 — RTL fonts and CSS line boxes

后续：[r11](../accordion-r11/README.md)已补本轮行框变更后其余六例的新浏览器矩阵和Multiple全展开；本目录保留r10当时的边界说明。

上一轮保留了Web阿语第三项被代码面板遮住的问题。本轮追查官网实际computed style：RTL正文按语言使用Noto Sans Arabic或Noto Sans Hebrew，工具栏仍用Geist。此前本地只替换文字/方向，使用Geist对缺字的系统回退。见[官网字体与CSS声明](official-fonts.json)，配方与语言控件源仍固定于上游96da9cc7…。

已从[固定Google Fonts提交](source/font-sources.json)加入Noto Sans Arabic2.012、Noto Sans Hebrew3.001的400/500静态资源，保留完整SIL OFL许可证。生成脚本冻结width100与weight，完整字符集保留；[资源SHA](font-assets.json)与重复生成字节一致。只在Accordion RTL正文内用公共Theme API替换fontFamily，继承preset、颜色、accent、深浅色、radius、ring、spacing与触控策略，工具栏和其他配方不变。

换入Noto后新增实际单行标题42px回归：四象限均RED，实际52px。Compose默认Fixed模式会为高字形增加首尾留白，CSS20px行框则不增加。Nova Accordion三条string标题/正文路径统一显式使用Center/Trim.None/Mode.Tight；保留原14sp、20sp及400/500字重。原断言不变，四项转绿。原生与Web仍需查看字形是否完整，不能只以几何通过判定。

[最终验证](verification/summary.json)：核心Nova15+目录RTL4/Frame24/旧例2/Breadcrumb4共49项独立通过，core/shared ktlint通过（12.799s）；Wasm开发distribution21.372s通过，保留webpack1 warning。16项输入SHA一致，工作流正常结束并清理原始日志。初稿使用internal Theme.LocalTheme造成的编译失败已通过公共Theme API修正，不算运行时RED。

实际加载新app`a8c35a345ac9b9d12cfa.wasm`及Arabic/Hebrew各两字重资源后，12个三语×宽窄×浅深浏览器状态全部查看。窄屏阿语第三项在浅深色均可见，深色实际点击后首项关闭，支付回答完整显示；官网对应状态在动画结束后另存。[浏览器记录](browser/review.json)。官网静态12状态沿用r9同源基准，本轮额外核对computed字体与新的第三项交互，不冒称重拍了全部官网静态图。

原生RTL12图与常规六例初始/切换Frame48图全部通过12张原像素拼图复查。Mode.Tight使Frame48全部相较r7发生变化，已归档原图、SHA及比较表；不能声称其余配方像素未变。既有Basic/Borders/Card长内容在固定预览边界的裁剪仍可观察；后续须对其余六例补新浏览器几何/溢出矩阵，原生检查不等于Web验收。

仍留全局窄屏342/327px与换行、深色字形/字重观感、原生希伯来语工具栏标签省略、全部扩展/Style/动态slot及辅助技术/触摸和手动主题重载持久化。未刷新旧golden或运行全仓提交门禁，未新增commit。Editor排除、Attachment后置。Accordion整项仍保持未完成，扩展/Style/辅助技术/触摸等完整矩阵未关闭。
