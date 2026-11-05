# Trace 页面 r3（2026-11-05）

对照官方 [Trace 页面](https://ui.shadcn.com/docs/components/base/trace) 与上游固定版本
`8a3536678a528cc012b9e62484290e79d0be410f`（MDX、16 个示例 TSX、`trace.tsx`、`style-nova.css`/`style-rhea.css`）。
抓取来源与哈希见 [sources.json](sources.json)。

## 官方结构与顺序

官方 MDX 的 16 个预览按页面顺序为：顶部 `trace-demo`、Trace Line、Status、Description、
Collapsible、Open State、Link、Dialog、Sheet、Group、Sources、Chain of Thought、Commit、
Whole Turn、Reasoning、Tool；其后是 Accessibility 与 API Reference。页面文案包括两段简介
（位于顶部预览之后）、Built with Trace 表格、Features 列表，以及每个配方前的说明。

## 本轮改动

- `TraceDoc.kt`：页面描述改为官方 "The building block for reasoning, tool calls, sources and more."；
  16 个配方全部按官方顺序与标题重建（顶部 demo 沿用首例，无标题），每例补官方说明文案；
  移除多余 Examples 标题（官方 ToC 直接列出配方）；新增 Built with Trace（11 条）与 Features（7 条）
  指南；Accessibility 7 条按官方；API Reference 拆成 Trace/TraceLine/TraceIcon/TraceTitle/
  TraceDescription/TraceCollapsible/TraceCollapsibleTrigger/TraceCollapsibleContent 以及
  标注为 Kotlin extension 的 TraceLink 共 9 个小节；Usage/Composition 与官方结构对齐。
- `TraceRecipes.kt`（新增）：全部 16 个预览的真实 Compose 组合，含顶部已结束 turn（reasoning、
  三次工具调用、失败输出、来源、Replay）、Link/Dialog/Sheet 行、Sources 默认展开、Chain of Thought
  嵌套、Commit 两文件与 Sheet diff、Whole Turn 三层、Reasoning/Tool 的点击式演示。
- `TraceRecipeCode.kt`（新增）：与预览同步的 16 段 Kotlin 导出代码，Copy Page 走同一数据。
- `shadcn/core/.../trace/Trace.kt`：补官方 `TraceCollapsibleContent(keepMounted =)`；标题之后的
  `TraceIcon` 按官方 `[[data-slot=trace-title]~&]:-ml-1` 收 4dp；`TraceDescription(mono =)` 支持
  官方的 `font-mono`；新增 Kotlin extension `TraceLink`（锚点等价物：hover 前景色、焦点环、
  Enter/Space 打开、`contentDescription` 承载 "(opens in a new tab)"）。
- 测试：`TraceExamplesTest` 19 项（顺序/标题/文案/默认 demo/状态/说明/折叠/受控打开/链接语义/
  Dialog/Sheet/Group/Sources/Chain of Thought/Commit/Whole Turn/Reasoning/Tool/Copy Page），
  `TraceParityTest` 11 项（原有 5 项 + keepMounted 保/不保嵌套状态、链接语义、extras 与 RTL
  像素矩阵）；新增 4 张 Nova 基准 `trace_parity_extras_{light,dark}`、`trace_parity_rtl_{light,dark}`。

## 验证

- 聚焦测试 **30 项通过**：页面 19、核心 11；XML 见 [tests/](tests/)。
- 最终 Wasm 构建：`/tmp/cf-parity/web-build.sh f 8776`，wasm sha256
  `89721bca784cc2dd66346c26a0de1fa9a445ecf2a0d7ea0d13e04fc658b8129d`。
- 四象限（1440/390 × 浅/深）在最终构建后重抓：[browser/](browser/)（`official-*`、`local-*`、
  `meta.json`、`sheet-*`）。桌面顶部与官方一致：标题/描述/Base UI/顶部 demo 首屏可见；
  16 个配方的预览几何（内宽 384、行高 20、8dp 行距、内容缩进 7/16/4；官方测量内高
  100/128/156/160/184/188/255/305/368，顶部 demo 515.75）与本地抽查值一致或相差 <10px；
  手机端顶部同样回到 demo 首屏。
- 实际浏览器状态（最终构建，[browser/states/](browser/states/)）：demo Replay 清空 1.2s 后恢复、
  Collapsible 展开、Open State 外部 Button 控制、Dialog 打开与 Escape 关闭、Sheet 打开、
  Chain of Thought 展开搜索行露出 Badge、Commit 文件行打开 diff Sheet、Whole Turn 展开失败命令、
  Reasoning/Tool 点击 Send message 生成完整转录并可 Reset；官方对应打开态参考见
  [browser/official-states/](browser/official-states/)。
- 组件 RTL 在 JVM 像素基准 `trace_parity_rtl_*` 中验证（内容左侧竖线翻到右侧）；
  `:shadcn:core:verifyRoborazziJvm --tests TraceParityTest` 通过（原有 2 张 + 新增 4 张 Nova 基准）。

## 剩余差异（均已解释）

1. **简介段位置**：官方两段简介位于顶部预览之后、Built with Trace 之前；本地文档模型在首例预览与
   Installation 之间没有插槽，简介合并进 Built with Trace 指南段（文案保留），因此 Built with Trace/
   Features 整体出现在 Usage/Composition 之后，ToC 顺序与官方不同。
2. **Built with Trace 表格**：本地指南只支持段落/列表/代码，官方 11 行表格改为同名条目列表；
   官方链接指向的 AI Elements 页面本地不存在，条目以纯文本呈现。
3. **Status 表**：官方 "Use for" 四行表在本地以第二段散文呈现，且位于预览之前；文档模型无表格且
   示例说明只在预览前渲染。
4. **配方后补充片段**：官方在 Open State、Link、Dialog 预览*之后*另有 TSX 片段
   （`open ?? running`、锚点新标签页、对话框 trigger）；本地不支持“示例后代码”，未收录，
   等价 Kotlin 组合已在该配方代码面板中。
5. **Reasoning/Tool 流式**：官方用 AI SDK 逐 token 流式；本地为点击后一次性渲染完成态转录
   （初始态与官方相同：空预览 + Send message），没有运行中的 shimmer/逐字增长。
6. **顶部 Replay**：官方清空并用 transport 重新流式；本地清空 1.2s 后恢复同一静态转录。
7. **触发行 hover**：官方 Dialog/Sheet trigger 包裹的 TraceLine 在 hover 时变前景色；本地共享
   DialogTrigger/SheetTrigger 不暴露 hover 状态，只有 TraceLink 行实现了该效果。
8. **样式目标差异**：官方该页所有预览显式使用 `styleName="base-rhea"`（对话框 `sm:max-w-md`
   即 448、圆角/内距更大），本地目录统一 Nova（384），因此 Dialog 面板宽度、圆角等 chrome
   呈 style 级差异，而非 Trace 配方差异。
9. **API 表列**：官方 API 表为 Property/Type/Default/Description 四列；本地 `ApiReference`
   只有三列，属性说明放入小节描述（`mono`、`keepMounted`、`enabled` 作为 Kotlin extension 说明）。
10. **平台范围**：本地 Canvas 无真实 DOM 锚点/`aria-expanded`，用 Compose 语义近似；
    官方 React Aria/Radix 站点级差异不适用。
