# Questionnaire Web 验收

2026-11-18。以官方 Base/Nova 与固定上游提交为范围。最终结论由本页的测试、当前产物、实际浏览器响应和独立终审共同确定。

## 来源和适用范围

固定 shadcn/ui 提交 `968d8cb9dc42ae5d52e8fd356cf84596ec832a26`，归档 26 份 MDX、组件、十四份配方、Nova/Rhea CSS 和 headless Questionnaire 源码。官方配方顺序为 Default、Multiple Selection、Freeform Answer、Explicit Skip、Shortcuts、Custom Validation、Controlled、Resume、Conditional Items、Navigation State、Custom Progress、Animated Items、Card、Dialog。四份本地补充保留 `Kotlin extension:` 前缀。[官方文档](https://ui.shadcn.com/docs/components/base/questionnaire) · [固定来源](sources.json)。

## 本次修复

- 预览框遵循官方桌面底部 32px、手机顶部 16px 内边距。为共享预览增加可选手机配置，固定高度调用和原有默认值均经回归核对。
- Animated Items 启用真实 300ms 淡入和 8dp 上移，Web 响应系统减少动态效果设置。动作区位置稳定。
- 步骤提示公开命名的只读 `progressbar`、真实 1..总步数和当前步骤文本。
- 活动题目的 Compose 焦点目标组合题目、说明和实际错误。外部校验回到错误题目时，提示和错误语义一致。
- 十四份复制代码均从实际预览提取。每份独立包均经 Kotlin/Compose 编译。Initial Composition 说明了 Kotlin 首次组合的行为和上游 React 服务端渲染之间的适用范围。
- 三十张旧截图先逐张归档尺寸、像素区域和哈希。再次实拍与修复前输出逐字节一致后刷新引用，保留旧图和差异记录。

## 验证

- Questionnaire 核心 61 项、目录 38 项，共 99 项。Accordion 预览和 Catalog 默认布局另有 29 项回归。十四份独立示例编译成功，34 张基准图校验和所属 lint 通过。
- 当前工作树构建 JS 49 个、Wasm 45 个文件及 Desktop compile。构建前后输入哈希一致，浏览器关闭缓存和 service worker，记录 422 条实际响应并逐字节匹配磁盘产物。
- JS/Wasm × 1440/390 × 浅/深色，共八组 144 个示例位置，其中十四份官方配方保存 112 张定视口截图。根容器、Canvas 原点、CSS 尺寸、DPR 位图和实际主题像素均已校验。
- 四组流程包含外部校验、回退错误题目、纠正提交、Resume 默认答案恢复、对话框重开和关闭、数字快捷键。手机 radio 从截图轮廓定位并发送可信触摸事件。另有四组默认问卷、步骤语义、Tab/Space 和实时主题切换。
- 四组路由验证包含直接锚点、刷新、目录、前进/后退、实际剪贴板中的配方和页面文本、桌面前后页及手机导航。
- 两种 Web 运行时分别验证正常动画的中间帧和减少动态效果后的静止位置。录制时钟减速只用于观察过程，系统偏好来自实际 matchMedia。
- 独立终审见下方链接。

## 平台和 API 边界

- 上游的 native HTML `form`、`fieldset`/`legend`、`FormData`、原生 submit/reset 事件及可取消 `onReset` 属于 React DOM 接口。Kotlin 提供题目状态、答案集合、提交回调和重置状态，Resume 由宿主显示恢复提示。页面上的对应 unstyled props 继承说明引用上游 React API。Kotlin 的接收器、modifier、style 和组合内容为实际公开接口。
- React `render`/DOM 替换由 Kotlin 组合内容和样式覆盖承接。传入 `items` 让首次组合获得完整顺序和进度。SSR HTML 与 hydration 留在上游接口范围。
- 官方 Shortcuts 的原生 select 由项目已有 `Select` 承接三种模式，独立代码、实际选择、数字键和提交反馈均有证据。
- Chromium 可读取命名 progressbar、radio/checkbox、错误状态和当前题目的名称。实际浏览器焦点仍报告为 Compose Canvas 的无名代理。Compose JVM 断言活动题目获得焦点，浏览器 Tab 和 Space 可选择下一题答案。操作系统屏幕阅读器的实际朗读未经验证。
- 当前共享 Dialog 具有可见标题、关闭控件和 Cancel。实际 Cancel、重开、Escape/外部触摸均记录。浏览器未导出原生 `dialog` 角色，DOM 焦点节点仍为 Canvas 代理。
- 手机窄卡片中的多行文字会出现辅助 DOM 代理子项位置落后于实际绘制的位置差异。像素边框定位后的实际触摸用于验证该卡片的选择、校验和提交。该坐标差异在原始 DOM、截图和操作坐标中保留。
- 官方 Questionnaire 未提供 RTL/多语言配方或独立 shape 切换器。通过现有组合设施补测阿拉伯语、英语和希伯来语，JVM 断言选择标签/快捷键镜像、选择后导航、下一题焦点和真实步骤范围。浏览器目录保留官方配方。动态浅深主题切换保持当前步骤，八组静态视图均记录实际背景像素。Android/iOS 运行和系统级 AT 覆盖另列为未执行范围，Desktop 为编译验证。

## 证据

最终凭证、构建输入、截图和传输哈希位于组件工作目录。[证据入口](../../../.agent-runs/questionnaire/goal-20261118/acceptance-evidence.json) · [独立终审](independent-review.md) · [交接](../../../.agent-runs/questionnaire/progress.md)。
