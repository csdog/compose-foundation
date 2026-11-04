# 文档水平布局与浏览器滚动（2026-11-04）

用户允许重新建立专用验收空间后，恢复 TaskSpace **1**（官网 p1、本地 p2）及 127.0.0.1:8765 服务。后续复用该空间。

## 官网依据与改动

[当前 Accordion 官网](https://ui.shadcn.com/docs/components/base/accordion) 的水平布局测量保存在 [official-layout.json](official-layout.json)。七宽度基线采集于 11 月 2 日；11 月 4 日重新核对 1440px，正文仍为 x=392.5、宽 640px，可用页面宽 1425px。

- 文档壳左右各 8dp，侧栏 288dp，在 CSS 视口 1024px 起显示；移除原有额外分隔线和全页 1536dp 上限。Create 保留独立上限。
- 正文居中、最大 640dp；768px 以下内边距 16dp。目录改为 288dp，1280px 起显示，内容左右各 48dp、顶部 32dp。
- Web 使用包含浏览器滚动条的 CSS 视口判断上述断点；正文、预览和 RTL 工具栏共用该判断。原生宿主保留窗口宽度回退。
- ComposeViewport 改挂固定的 compose-root；文档 ScrollState 与浏览器滚动位置、总高度双向同步。滚轮交给 Compose 内部滚动器处理，阻止浏览器重复消费；滚动监听随路由取消，Create 停用文档滚动条。
- 五个指南页复用路由缓存的滚动状态。首次安装两个同步方向后才发布浏览器高度，避免初始化期间丢失外部滚动。
- 在 HTML head 尽早设置 `history.scrollRestoration = "manual"`，由路由缓存统一恢复。进一步调用跟踪确认：即使停用浏览器自动恢复，原有空 fragment 锚点逻辑仍会在返回后调用 scrollTo(0)。新增跨路由 history 标志，返回缓存页面时跳过该逻辑，新导航及同页 fragment 跳转仍走原有锚点处理。瞬时 `scrollY=800` 不能当成稳定通过。[HTML 标准](https://html.spec.whatwg.org/dev/nav-history-apis.html)、[MDN](https://developer.mozilla.org/en-US/docs/Web/API/History/scrollRestoration)。

| CSS 视口 | 浏览器可用宽度 | 正文 x | 正文宽度 |
|---:|---:|---:|---:|
|390|375|24|327|
|768|753|56.5|640|
|900|885|122.5|640|
|1024|1009|328.5|640|
|1280|1265|312.5|640|
|1440|1425|392.5|640|
|1800|1785|572.5|640|

## Create 崩溃修复

完整导航测试和浏览器均复现了 Create finance preview 的 intrinsic 异常。隔离 welcome/customizer/finance 三个子树后，确认只有 finance 失败：KitchenSliderRow 中 Nova Item 的 FlowRow 查询滑块固有宽度和高度，而滑块内部 BoxWithConstraints 不支持此查询。

仅在该示例调用处提供 120dp 宽度回退和按当前 thumb/minTouchTarget 计算的高度；外层 fillMaxWidth 仍使用真实分配宽度。先补宽度时错误由 minIntrinsicWidth 变为 minIntrinsicHeight，补高度后三个子树均通过。临时诊断测试已移除，最终完整路由测试覆盖打开 Create、欢迎弹层、Get Started 和返回原文档缓存。

## 验证

- 共 28 项独立测试通过：几何 14 项（七宽度 × 浅深色）、完整生命周期 1 项、现有导航 6 项及文档导出/锚点 7 项。生命周期包含主题切换不重绑、Create null 绑定、欢迎弹层、返回保留 180px，以及随后新空 fragment 导航回顶；补入真实 DocNavigation 条件后，先红灯 expected180/actual0，再加 history guard 修复。另复验现有导航及文档导出/锚点测试。[JUnit XML](tests/) 及 [RED 证据](tests/red/history-cache-without-restore-guard.xml) 已归档。
- shared/webApp ktlint、JS 编译、Wasm 开发 distribution 通过。[构建和输入摘要](validation.json)、[22 个输入哈希](inputs.json)。保留既有 AGP/Gradle/webpack warning；不将默认 detekt 空源码成功当公共源码检查。
- [四象限截图索引](browser/matrix.json)：官网/本地各四张，1440/390 × 浅/深，全部目视复查，实际主题键与 clientWidth 同时记录。水平几何已对齐，其他视觉差异未关闭。
- 四象限截图使用 43f908… 构建；后续仅调整同步初始化、Create 示例尺寸及 HTML 历史恢复。最终 5f6f08116629f8f0f6d7 应用实际加载；DPR2 的 800 CSS px 原生滚动已在 e3f75… 阶段复查，最终构建另复查 Create 及稳定返回。Skiko 658820… 是图形运行时。
- 浏览器已验证 Compose 滚轮 → 浏览器位置、直接 window.scrollTo → 正文、目录 Multiple → `#multiple`、搜索弹层打开后背景滚轮不移动。原始滚轮诊断收到三次事件、delta 合计 400（工具请求 200）；三次均 defaultPrevented，不把工具拆分事件误认为重复消费。

最终无诊断构建的 [交互记录](browser/final-interactions.json) 确认：Accordion 800px → Create（y0、client1440、body1000）→ Back 800px，timeOrigin 相同、固定根容器 y0；[稳定返回画面](browser/layout-final-history-accepted.png) 已目视复查。临时打印已全部移除。

验收脚本必须先截图确认目标组件已显示，再操作。只等待 body 高度会读到初始 Introduction 过渡页，导致把滚动写入错误的 ScrollState；[事件诊断](browser/initialization-diagnosis.json) 已明确排除这些瞬态失败。最终测试从已目视确认的稳定 Accordion 开始。

## 仍需核对

描述的 80% 限宽和 text-balance、垂直间距（手机标题仍低于官网）、顶部导航及复制/前后页操作、目录字体/行距/广告区和扩展长标签、代码面板及深色字形仍有差异。指南页水平结构没有据组件页测试宣布全部完成。

原生滚动条模拟拖动两次均未产生 scrollY 变化，尚不能计为通过。完整弹层/嵌套滚动锁、同一路由不同 history entry 的独立位置快照、跨 DPR 迁移时的位置保持、触摸和辅助技术矩阵尚未完整关闭。390px 是桌面 Chromium 窄视口，不是真机触摸验证。早期字体未稳定画面、Create 修复前崩溃和 Back 自动恢复中途画面不计通过。

Accordion 及 P1/P3 继续保持未勾选；Editor 排除、Attachment 后置。本轮未运行旧 golden 或全仓提交门禁，未新增 commit。
