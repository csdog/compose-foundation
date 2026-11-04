# Accordion r9 — RTL preview chrome and natural content height

后续：[r10](../accordion-r10/README.md)接入官方Noto字体与CSS紧密行框，已解决这里保留的Web窄屏阿语第三项裁剪；本目录继续保留当时原始证据。

本轮沿用获授权的验收空间4，官网p1与本地p2；对照Base UI页面的RTL示例。上游配方仍为[r7固定来源](../accordion-r7/sources.json)中的`accordion-rtl.tsx`，实际官网DOM另存[三语四象限几何](browser/official-matrix.json)。

官网工具栏高64px（含底边），选择器144×28px；正文固定288px、padding40、顶部对齐，Accordion最大448px。三语×1440/390×浅深共12个官网状态确认预览恒高288，切换语言保留首项展开。390为桌面Chromium窄视口，尚非手机触摸真机。

本地移除正文内180px选择器，复用现有语言/翻译说明工具栏，并保留其余RTL示例既有正文布局。Accordion使用独立固定预览、448px最大宽度，补RTL配置指南正文；复制代码不包含目录工具栏。

初步34项测试通过后，原生图和浏览器发现固定高度约束压缩后续条目，第三项只剩图标。新增第三个阿语标题自然高度断言，390浅深两项RED、桌面两项通过。正文改为在固定外框内按自然高度测量、允许伸入padding区，匹配官网overflow-visible；外层卡片仍裁剪。此前仅因答案子串同时匹配代码面板产生的4次测试失败单独记录，不冒称组件回归RED。

[最终验证](verification/summary.json)：34项独立JVM测试（RTL4、原固定预览24、原示例2、共享Breadcrumb4）及shared ktlint通过，19.814s。Wasm开发distribution15.135s通过，保留webpack1 warning；6项输入SHA一致，工作流正常finish并清理原始日志。最终app为`401ff6ac3a7049a264f1.wasm`，实际浏览器加载已核对；`658820…`是未变化的Skiko模块。

12张最终原生图已逐张通过4张原像素拼图复查，窄屏6张相较初版变化、桌面6张不变。官网/本地各12张三语四象限图全部目视复查；[浏览器记录](browser/review.json)列出实际操作、原图SHA及被排除的早期无效键盘尝试。桌面翻译说明弹层实际打开，JVM亦验证说明文字。动态语言切换保留首项展开。

明确仍存在的差异：Web阿语使用的字形/行高与官网不同，窄屏第三项仍落在固定预览/代码边界下，不能将原生零高修复称为Web第三项可见性完全对齐；本地窄屏外框342px对官网327px，来自全局列宽/滚动条；深色字重/字体外观仍偏离。原生希伯来语选中标签省略，而Web选中标签完整。其余扩展、动态slot/Style及辅助技术/触摸矩阵未完成。未运行旧golden或全仓提交门禁，未新增commit。Accordion整体仍待其他扩展API/Style/动态slot、完整辅助技术和触摸矩阵，以及字体/换行与全局页面细节，未标记整项完成。Editor排除，Attachment继续后置。
