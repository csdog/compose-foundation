# Hover Card 浏览器跟进（2026-11-04）

沿用用户授权的专用验收空间1，对照[官网 Base UI Hover Card](https://ui.shadcn.com/docs/components/base/hover-card)和本地/hover-card。四个官方配方Default、Basic、Sides、RTL沿用[r1固定来源](../hover-card-r1/sources.json)，本轮补充读取实时DOM、尺寸、字体、实际避让方向，以及Base UI 1.6.0定位源码（[来源与哈希](source/sources.json)）。不把历史勾选或编译成功等同于浏览器验收。

## 发现与修改

- 原先Popup锚在整个Root，额外的Root内容或固定高度会推开弹层。现在Root与Trigger分别记录窗口坐标，在Popup测量阶段替换为实际Trigger几何，并保留平台inset归一化。测试覆盖Root额外内容、固定Root内Trigger移动；不把它推广为任意嵌套Content宿主或多Trigger已完成。
- 键盘焦点离开后原先等待hover closeDelay，现在使用独立立即关闭路径；重新进入Trigger会清除旧的焦点关闭标记。测试覆盖1000ms hover延迟时的48ms内失焦关闭、弹层内控件切换后离开。
- Nova采用官网默认5dp窗口避让边距；两侧放不下时尝试垂直方向，逻辑交叉轴对齐按RTL解析。七项纯定位测试覆盖手机左右不足、顶部/底部翻转、交叉轴限制、逻辑偏移与平台坐标修正。Vega继续使用原定位器。
- RTL改为64dp工具栏和320dp正文，正文40dp inset，语言使用完整标签和已打包Noto Arabic/Hebrew；正文Tight+Trim.None保留20dp行框。恢复RTL指南说明与链接，导出配方含公共字体资源和主题配置。
- Sides移除官网没有的额外说明。390px页面的预览内部245dp宽：官网四按钮总宽244.445px；Skia intrinsic逐项取整后达到246px，会错误换行。示例用字形cursor advance决定flex换行，仍以完整Button尺寸布局、居中并保留点击区域；更窄时继续换行。生产与导出示例同步，不缩小公共Button的字重、边框或内边距。
- 公共版本与安装说明更新为0.3.35。

## 自动验证与像素基准

[最终XML](tests)共**44项通过**：核心27、目录17；包含四张既有像素基准的显式verify。新增失焦/锚点4项、定位7项，目录四种显示组合12项检查完整行框、RTL正文/工具栏、三语及Sides手机同排；既有目录5项继续通过。RED证据保留在tests/red和tests/red-sides。

只更新两张Nova基准及对应发布预览：面板由x=0移动到官网5px窗口边距；平移后内容区域RGBA一致，触发器未变。顶部阴影比较的五个像素受固定触发器背景混合影响，详情及旧/新/差异图在[diagnostics/collision-baseline](diagnostics/collision-baseline)。两张Vega基准未改，独立verify通过。没有批量刷新其他基准。

core/shared ktlint、JS和Wasm development distribution通过。最终应用`49e2a7e4c25526ebc341.wasm`，43,374,418字节，SHA256 `7895ebc9270641c420bed9bb9011915096f8fbead852d80e79d23272ad5410d7`。构建输入在[source-inputs.json](source-inputs.json)，最终测试和构建汇总在[validation.json](validation.json)。仅保留既有Text INVISIBLE_REFERENCE、AGP/Gradle及webpack os/path诊断；默认detekt对commonMain sourceCount=0的限制仍开放。

## 浏览器证据

[browser](browser)保存1440/390×1000、DPR1、mouse、mobile:false，浅深各一组；390为窄屏鼠标模拟，并非触摸真机。官网80张记录四配方关闭/展开、四物理方向、Arabic六方向及Hebrew/English逻辑方向，实际DOM矩形/方向保存在official-matrix.json。本地最终构建保留16张四配方关闭、8张Default/Basic展开和3张单独确认的方向图，共27张。桌面浅色Left/Top与窄屏浅色Left已单独目视确认；窄屏Left实际改在下方且保留5px窗口边距。最终构建实际加载wasm见local-closed-matrix.json，范围见[capture-review.json](browser/capture-review.json)。

**完整浏览器矩阵尚未通过。** 连续批量移动时部分画面没有弹层，或后续方向仍显示前一个弹层；这类图已移到[diagnostics/browser-continuous-switch](diagnostics/browser-continuous-switch)，不能计作方向通过。底层鼠标事件的单独手机Left验证正常，但批量仍有异常，不能只归因于包装器。另一次逐例重载等待发现桌面Sides滚动位置2823与热态2799.5不同，保存实际画面，字体/锚点稳定过程还需定位；本轮没有据此改共享页面或字体。官网三语截图已保存，本地Hebrew/English完整Web矩阵尚待补，三语自动测试通过不能替代这些浏览器证据。

## 未关闭的差异

- 连续方向切换/移出关闭与热态、重载后的完整浏览器矩阵仍待定位；目前只有上述明确范围通过，不声称四象限全部方向通过。
- safe polygon跨Trigger/Content间隙、拖动选字时保护尚未实现；全局Escape（焦点在Root之外）仍有缺口。
- 多Trigger/handle、任意嵌套Content宿主、完整裁切祖先/滚动容器和超大内容定位仍待补；目前窗口避让测试不能代表完整Floating UI行为。
- 默认动画仍基于请求side，而非实际避让后的side；不能将静态位置对齐说成翻转动画原点已完成。
- 读屏、焦点可交互内容的完整Web链路、动态主题/shape过程、触摸设备和其他平台仍待核对。
- 深色字重观感、ring/阴影细节、文字排版，以及共享文档标题、纵向节奏、代码/API区域仍有差异，不声称整页像素一致。

Hover Card保持未勾选；Editor排除，Attachment后置。r1记录保留为历史，本轮结果以本目录为准。
