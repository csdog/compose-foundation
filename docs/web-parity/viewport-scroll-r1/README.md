# Web 滚动条切换后的画布重测

2026-11-04，关闭 Progress r2 发现的初载尺寸失配：根容器1425×1000，Canvas bitmap仍为1440×1000，CSS因此显示为1425×989.578并向下居中5.211px。窄屏对应375×961.531和19.234px偏移。[修改前诊断](before)保留实际几何和图片；历史根图在[Progress r2](../progress-r2/diagnostics-initial-layout)。

## 根因与修改

重新读取本机依赖 `org.jetbrains.compose.ui:ui:1.12.2` 源码：[DefaultWindowState摘录](source/DefaultWindowState-excerpt.kt)仅监听window resize与像素密度变化，再读取viewport容器clientWidth/clientHeight；没有监听滚动条引起的容器宽度变化。[来源与摘要](source/source.json)固定到实际编译依赖。

BrowserDocumentScrollHost在初次绑定文档时将html overflow-y从hidden变为scroll，经典滚动条令可用宽度1440→1425，但没有window resize通知。CSS将旧bitmap按宽度缩小，并由现有grid居中。一次可撤销实验显式通知resize后，bitmap立即成为1425×1000，CSS边界归零且高度1000；因此没有采用依赖内部DOM层级的CSS强制高度方案。

最终只在既有host中集中设置overflow-y：比较修改前后的clientWidth，实际改变才dispatch resize。显示滚动条、Create隐藏滚动条两方向都处理；无新增监听器/定时器/每帧状态，保留原有滚动缓存、绑定生命周期与CSS媒体查询的innerWidth含义。公共组件和版本未变。

## 验证

- 目录既有CatalogDocumentScrollHostTest一项通过：theme不重绑、Create独立滚动、离开/返回缓存及空锚点清零。它不是浏览器bitmap验证的替代。XML在[tests](tests)。
- Web模块ktlint通过，JS/Wasm development distributions退出0，保留既有webpack os/path诊断和warning。源文件构建前后SHA一致，见[input-after](input-after.json)。默认detekt公共源码覆盖限制仍适用，未新增/刷新golden。
- 最终主Wasm `66220382052adb6ad60a.wasm`，43,567,954字节，SHA256 `bbbcfa850825910bb594da9ba5eaf14ed4251ecd15c9d6bab0e73c6e5e845417`。实际浏览器资源与构建摘要一致。
- 沿用授权TaskSpace1，1440/390×1000、浅/深色、DPR1、mouse/mobile:false。四组合各初载、Controlled及RTL跳转12次，加滚动中的1440→390→768→1024→1280→390→1440七次重排，共19个几何断言通过。[geometry](browser/geometry.json)逐项保留root、CSS Canvas、bitmap、theme、URL、scrollY及实际产物。没有额外注入CSS或手动resize事件绕过修复。
- 四组合真实点击New进入Create，再浏览器Back，共8个新增状态通过：隐藏滚动条时clientWidth恢复完整1440/390、scrollY=0；返回时1425/375、Canvas同步重测，并精确恢复进入前scrollY。[routes](browser/routes.json)保留前/中/后状态。
- DPR2桌面/窄屏各一次新初载通过：bitmap分别2850×2000、750×2000，CSS仍为1425/375×1000，原点一致。[DPR2](browser/dpr2.json)。这是浏览器像素密度模拟，不等于真实设备覆盖。

共29张最终浏览器图已全部目视复查：19几何、8导航及2高密度。8张对照/重排/导航拼图在[review-sheets](review-sheets)，两张DPR2原图另看；图像摘要在[browser/images.json](browser/images.json)。Default示例13→66没有冻结，初载图可能处在不同动画时刻，本轮只据几何判断缩放，不将填充差别称为组件回归。执行脚本也保存于browser，使用`ego-browser nodejs < 脚本`；复用空间1，路径按该工作区记录。

## 验收范围

本轮证明并修复滚动条宽度变化导致的Canvas缩放/居中偏移；这些路径中未再出现旧的顶部大块空白。不能将其扩大为所有绘制时序、字体、滚动锚定或组件状态均完成：普通resize保留像素滚动位置，不保证标题跨排版仍锚定同一位置。未进行逐帧录屏/性能、触屏或辅助技术全矩阵。

历史组件浏览器图保留原始事实，不因共享修复自动升级为最新产物的精确对齐结论。Progress其余排版/Composition顺序、原生语义、完整手势等仍开放；继续优先缺少本轮完整页面对照的组件。Editor排除、Attachment后置，整站复核未完成。
