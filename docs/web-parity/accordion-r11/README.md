# Accordion r11 — six-example browser regression and explanations

后续：[共享主题持久化](../theme-persistence-r1/README.md)已修复本轮记录的整页重载丢失手动主题；旧截图/导航记录仍是修复前证据。

本轮补r10共享Nova行框变更后的常规六例浏览器矩阵。官网Base UI配方仍按[r7固定来源](../accordion-r7/sources.json)；官网与本地Default/Basic/Multiple/Disabled/Borders/Card各24状态（1440/390×浅深）全部新采集、目视复查，未拿旧r7截图代替新构建。另补Multiple全部三项展开的官网/本地各4状态，总56张图见[浏览器记录](browser/review.json)。390是桌面Chromium窄视口，不等于触摸真机。

发现本地缺少Basic、Multiple、Disabled、Borders、Card五组说明段落，已补入ComponentExample.descriptionMarkdown，复用既有段落排版及Copy Page导出。Multiple使用实际Kotlin的AccordionType.Multiple，disabled描述为参数，Borders按Kotlin边框/分隔线语义表达，不复制无效CSS类。

官网固定预览：四例300px、Multiple450px、Card435px；Multiple全部展开自然高度桌面358px、窄屏478px，外框保持450px。实际先开Billing再开Privacy，官网/本地均保留首项并同时展开三项；本地桌面→窄屏仍保留全部展开。窄屏最后回答在代码面板边界被裁剪是官网也有的overflow策略；本地外框342px对官网327px，文字换行和裁剪位置仍不同。Canvas内完整root自然高度未直接测量，不能冒称本地也是478px。

首批本地深色图实际为浅色：在Default导航整页重载之前设置主题，导致override丢失。已全部移入diagnostics排除；最终24本地状态逐个导航后读取实际背景像素、必要时再点主题，最终56图每张均核对浅深像素。导航记录的performance.timeOrigin显示24次中4次整页重载，其他同页hash导航保留状态。主题持久化仍是独立未修问题。

[最终验证](verification/summary.json)：37项独立测试（Examples2/Frame24/RTL4/Documentation7）与shared ktlint通过，14.762s。Wasm开发distribution14.128s通过、保留1条webpack warning，新app289f86cde9386ed91695.wasm实际加载。17项输入SHA一致；60张新原生图与r10已目视版本逐字节相同，复用其目视，不重复累计coreNova15为本轮执行。工作流正常finish并清理日志。未验旧golden、未运行全仓提交门禁、未新增commit。

仍留全局列宽/列位置、字体/深色字形/精确换行、代码面板View Code垂直位置与字形、原生希伯来语标签省略、手动主题持久化、扩展API/Style/动态slot和辅助技术/触摸矩阵。Accordion整体未勾选；Editor排除、Attachment后置。
