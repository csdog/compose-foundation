# Accordion r8 — reorder and repeat-anchor review

本轮先检验r7恢复竖排后Horizontal键盘顺序的动态重排，随后重查此前记录的缩放锚点问题。

确认更正：官网在390宽Card标题处位置85.516px，切1440后同一个`#card`标题变为-350.953px；它并不因缩放自动重新定位URL标题。再次点击右侧同一Card链接后，标题回到87.547px，hash未变。[官网实测](browser/official-repeat-anchor.json)。本地再次点击右侧相同Horizontal目录项也能回到该标题，[前](browser/local-repeat-anchor-before.png)/[后](browser/local-repeat-anchor-after.png)。BrowserDocNavigation原有`sync(force=true)`递增revision，已经处理同hash重复点击。

因此撤回r7将“缩放后未自动定位”认作本地导航缺陷的结论；不应加一个与官网不同的强制重定位效果。上轮桌面键盘截图未命中目标，不作为通过证据；本轮已通过实际目录点击重新进入并完成补验。

重排回归确认了实际缺陷：原Horizontal用x排序，同列会退回旧注册次序；不同宽度RTL项目的x也会误导Home/End。新增LTR/RTL反转为c,b,a的两项测试先失败，额外不同宽度RTL一项在Home处失败。现两种键盘轴统一按实际y位置排序，orientation仅决定Left/Right或Up/Down映射，不再参与视觉顺序。

[最终验证](verification/summary.json)：核心Nova15+实际扩展例32合计47项独立通过，core/shared ktlint通过；Wasm开发distribution21.084s通过、保留webpack warning。10项输入SHA一致，8张扩展原生图与R7逐字节相同，复用其目视。R7其他78项不能重复累计为本轮新增；旧golden与全仓门禁未运行、未新增commit。

实际加载新8a876a4ab770edcfebc1 app模块后，桌面/窄屏×浅深4张键盘画面均已复查，Styled?获得焦点并显示完整回答。桌面先实际点击目录对齐，再观察Right后的焦点，才按Enter；上轮未成功的桌面场景在本轮得到确认。[浏览器记录](browser/review.json)。动态重排自身由真实原生Compose UI三项回归验证，不把静态浏览器截图冒称为Web动态重排测试。390是桌面窄视口，不等同触摸真机。

剩余：RTL外置工具栏/精确Select、其余扩展API/Style/动态slot/辅助技术矩阵、Card换行/深色字形、全局列宽/scrollbar与代码区细节。手动主题仅remember、整页重载丢失的问题仍独立保留；这项结论未因锚点更正撤回。Accordion仍未勾选。
