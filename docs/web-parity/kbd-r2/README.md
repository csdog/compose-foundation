# Kbd：页面与浏览器复查

2026-11-04，继续优先覆盖尚无完整页面浏览器矩阵的组件。对照[官方Kbd](https://ui.shadcn.com/docs/components/base/kbd)，固定上游 `96da9cc7f1aa10580a3741664e2a77c721206c00` 的文档、组件、六配方及TooltipProvider源码见[source](source)，摘要见[sources.json](sources.json)。关键实时CSS/RTL正文高度见[live-dom.json](live-dom.json)。

## 发现与修改

- 六组配方已经存在，本轮核对Default、Group、Button、Tooltip、Input Group、RTL，保留原公开Kbd/KbdGroup API。Kbd为被动键盘提示，不增加键盘快捷键处理。
- RTL旧实现把240dp内容又放进共享RtlPreview的padding/最小高度中。改成独立工具栏和单一288dp正文、40dp inset，与官网实测h-72/p-10一致；语言标签改为English、Arabic (العربية)、Hebrew (עברית)。四种显示组合的288dp实际高度与三语方向由新测试验证。
- Button示例声明ButtonIconPlacement.End，保持尾部键帽2dp视觉偏移；预览和导出同步。Group/Button/Tooltip/Input Group说明补齐，并为Kotlin的RTL用法提供准确说明。
- 浏览器最初按500ms抓取Tooltip，后续帧常未打开。代码证实此示例缺少TooltipProvider，使用未配置时的600ms延迟；官网Provider默认0。为实际示例及导出代码补TooltipProvider，新增手动时钟32ms的可见语义断言。原采样放在[diagnostics-before-provider](diagnostics-before-provider)，不能把其文件名当作成功，也不能将过早采样直接认定为hover故障。

## 验证范围

[常规测试](tests)24项通过：core13、目录Examples7与四种显示组合Viewport4。包含既有尺寸/字体/slot、调用方Style、父按钮指针传递、Tooltip主题色、六配方/输入文字/两个提示/三语排序，以及新增实际预览高度和零延迟配方验证。目录ktlint、JS/Wasm development构建通过；本轮不修改公共核心或版本。

显式verify六张旧基准：普通Nova两张和旧Vega两张通过，Tooltip浅深两张各2468像素变化，1024×768。actual/compare与失败XML在[legacy-differences](legacy-differences)。核心、Tooltip、原测试和构建配置与本轮起始HEAD逐字节一致的记录在unchanged-inputs.json；本轮没有刷新任何基准。已查看比较图，变化涉及提示胶囊/文字宽度等，尚未完成历史根因归属，保留为已存在的未关闭差异，不宣称全部像素通过。

浏览器沿用授权专用空间1，1440/390×1000、DPR1、mouse、mobile:false，390为窄屏鼠标模拟。官网/最终本地各24静态、12 Tooltip/输入、8语言补充图；[before](before)保留旧本地24图。对照拼图在[review-sheets](review-sheets)，包括四张修改前拼图及最终16张拼图，均已目视复查。构建/输入摘要及最终计数见[validation.json](validation.json)，原生测试与浏览器不互相替代。

## 仍开放

- InputGroupAddon的CSS只对直接kbd子项施加`radius - 5px`和-0.15rem边缘margin，本地没有这一完整结构规则；默认radius10时官网附件键帽圆角5px，普通键帽6px。保留输入组专轮处理，未用全局Kbd圆角覆盖掩盖。
- Tooltip嵌套ButtonGroup的内侧圆角/边框合并不同，含键帽提示的特殊尾部padding、完整指针穿越/延迟/焦点/取消过程仍需共享组件专轮覆盖。
- Unicode键帽符号的系统回退字形、Group句内换行/字宽、页面标题/说明/代码栏和RTL说明篇幅仍不同；原生kbd/DOM映射、辅助技术、真实触屏、完整动态主题与shape矩阵未关闭。
- 两张旧Tooltip像素差异仍开放。Kbd保持未勾选，Editor排除、Attachment后置，整站目标未完成。
