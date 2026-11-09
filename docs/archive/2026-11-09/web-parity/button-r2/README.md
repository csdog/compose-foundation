# Button：链接、错误状态和默认菜单（2026-11-04）

继续对照 [官方 Base UI Button](https://ui.shadcn.com/docs/components/base/button)，固定源版本 `4aaea19d9cff3e0908e12a8c1d504b8c5ec34610`，原始 MDX、十五个 docs 示例、Button 和 Nova CSS 复用 [上一轮归档](../button-r1/sources.json)。本轮仅补充两个重点配方与相关底层行为，未关闭 Button 或全站复查。Editor 排除，Attachment 后置。

## 修改

- 本地补第十五组 As Link，增加公开 `Modifier.buttonStyle`，只复用 Button 外观，不附加 Role.Button 或点击行为。示例用 `LinkAnnotation.Url`，链接文字负责键盘和链接语义，外层只补指针填充区命中；只有一个 Tab 停靠点，文字和填充区每次均只导航一次。目录的 `#` 回到当前页顶部，导出代码使用公共 API 和 UriHandler。Canvas 并未因此成为 HTML `<a>`；浏览器原生新标签页/链接上下文菜单和读屏映射仍待处理。
- 增加 `Button.invalid`，错误边框与无需聚焦即出现的 ring 对齐 CSS；浅色边框 destructive、ring 20%，深色边框 50%、ring 40%，保留主题 ring 开关及调用者 Style 最后覆盖。增加错误语义；两代旧 JVM 入口仍在，见 [javap](binary-entry-points.txt)。版本更新到 0.3.32。
- Nova Button 背景按官网 `bg-clip-padding` 排除边框区域。公共 Style 仍负责实际测量、透明度、变换、边框和阴影；绘制节点只替代背景，并匹配 Foundation 的 padding 四舍五入和 border 向上取整/Hairline 规则。回归覆盖 Color/Brush、圆形、调用者宽度、disabled 和 1.25 倍密度；Vega 仍走原 cssStyleable 路径。
- 共享 CSS 外阴影原先忽略 Style.alpha；现在阴影颜色透明度同步乘上 alpha，回归验证内容和阴影的 0.5/0.25/1 状态。
- Button 与 Button Group 页面共用完整默认菜单：Ellipsis、六个装饰图标、三个分组/分隔、Personal/Work/Other 单选，选择后保持展开，关闭再开保留选择，640dp 以下隐藏 Go Back。两处导出代码同步。
- 修复共享 Dropdown/Context Menu 根层提前吞掉 Escape 的问题。逻辑子层注册关闭回调，先关闭最深层，再恢复对应触发器焦点；抑制尚未退出的 hover 重新开层。官网与本地均验证一次 Escape 关闭子菜单、再次关闭根菜单。两层 Dropdown、模态/非模态 Context 的逐层恢复另有定向回归。

## 验证与基准

[validation.json](validation.json) 的根级十份 `TEST-*.xml` 共 **51 项独立用例通过**：core 31、目录 20。`red-menu/`、`red-border/`、`red-opacity/` 保留修复前失败；`menu-only-green.xml` 为重复子集，不重复计数。core 最终 run10、目录 Examples run5/Followup run7；run11 两模块 ktlint、JS 编译和 Wasm distribution 通过。源码输入哈希见 [source-inputs.json](source-inputs.json)。真实 Git 提交仍经过原有 ktlintFormat/ktlintCheck/detekt 门禁，使用同一 compact wrapper 工作流；默认 detekt 的 commonMain sourceCount=0 限制仍在，不能据任务成功推断公共源码完整静态分析通过。

仅更新两张 ButtonNova 基准，已显式像素 verify；旧图、actual、compare 与失败 XML 均保留。修正背景裁剪后浅色 1114 像素变化，深色 9848，其中 7766 仅有单通道一级差值（主要为 Outline 内部图层舍入），并非只有边缘变化；透明边框下方改为页面底色，使暗色 Outline 边框由约 #373737 变为 #2e2e2e。几何与文字位置未变，完整统计见 [baseline-comparison.json](baseline-comparison.json)。只派生更新两张 docs/previews；没有刷新其他旧基准，也未声称 Vega 全矩阵通过。

最终 app `2b882580d2bca680a8c6.wasm`，42,551,064 bytes，SHA256 `0bf992c501521da505ae74b4e1c67b96067d44a4ba4489e56784ca6a24e8a508`。TaskSpace 1 为用户授权恢复空间，继续复用。1440×1000/390×1000 CSS px、DPR1，浅/深各轮；As Link、Group 关闭/根菜单/Personal/Other/子层 Escape/根层 Escape，官网/本地各 28 张，四张 [查看拼图](browser/contacts/) 均已目视检查。截图覆盖这两个配方，不代表十五组示例全状态完成。390px 使用鼠标模拟窄屏，并非触摸真机。

实际浏览器还确认桌面链接文字/填充区、手机浅色填充区/深色文字都导航到 `#` 且 scrollY=0，见 browser 中导航 JSON；单 Tab、Enter、无重复触发由目录测试覆盖。官方深色 invalid 静态探针为禁用过渡后取样的 `official-invalid-dark-static.*`；较早同名无 static 的结果取到过渡起点，仅供诊断。浅深 invalid 像素与动态清除目前由原生测试覆盖，未声称本地浏览器错误状态矩阵已验。

## 真实剩余差异

- 默认 Group 根菜单按官网显式 160dp；共享子菜单仍固定 192dp，官网实测自然宽 101.5px/min96px。手机官网向左展开，本地因过宽会夹在窗口边缘并覆盖父菜单，需一并修自然宽度与碰撞处理，不能用示例硬编码掩盖。
- Dropdown 根层默认 min128、子层 min96；Context 根层 min144、子层 min128。当前共享面板还混用布局 border、ring 和阴影规则，应按原语分别核对。
- 指针打开本地菜单时首行即被聚焦，悬停子触发器后还会同时高亮两行；官网无此表现。Escape 后逻辑焦点恢复有测试和 ArrowRight 再打开证据，但根触发器的可见焦点环仍与官网不同。
- Button expanded 样式、非弹层按钮 pressed 下移 1px、完整 hover/pressed/disabled/动态主题与形状、Style.animate/任意复杂 slot、固有布局及辅助技术/触摸仍待完成；With Icon 的 Tabler/Lucide 图形差异仍在。
- 共享页面描述/换行/垂直节奏、代码区与字形观感仍有差异。Button Group 本页其他示例未重新完整验收；Context Menu 本页也需跟随共享修复重新验收，历史勾选不再视为本轮完成。
