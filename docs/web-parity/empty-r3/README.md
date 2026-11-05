# Empty 页面复核（2026-11-05）

按固定上游 `8a3536678a528cc012b9e62484290e79d0be410f` 重读 Empty MDX、`empty.tsx`、七个配方与 `style-nova.css`，并实时读取 [ui.shadcn.com Base UI Empty](https://ui.shadcn.com/docs/components/base/empty) 的标题/段落。来源与 SHA-256 见 [sources.json](sources.json)。

## 发现

- 官方顺序仍为 demo、Outline、Background、Avatar、Avatar Group、InputGroup、RTL，七个配方不变；每节有一句说明，Usage 代码里 `EmptyHeader` 含 `EmptyMedia variant="icon"`。
- 本地此前六段说明与官网不一致（Outline/Background 仍按旧文案、Avatar/Avatar Group/InputGroup 缩写、RTL 改为“设置语言方向”），Usage 代码缺 `EmptyMedia`；页面结构、Outline 自然高度、RTL 工具栏与正文约束已是上一轮状态。

## 修改

- 按官网改写六段 copy（保留 Compose API 名称并标注对应 CSS 工具类）：Outline → `EmptyVariant.Outline`（border utility）；Background → `background` style（bg-*/bg-gradient-*）；Avatar/Avatar Group → `EmptyMedia`；InputGroup → “You can add an `InputGroup` component to the `EmptyContent` component.”；RTL → RTL 配置指南链接 + 工具栏三语说明。
- Usage 代码补 `EmptyMedia(variant = EmptyMediaVariant.Icon) { Icon(Lucide.FolderCode) }`，与官方 Usage 结构一致。
- 组件/预览未改：Outline 虚线框由预览承担 384dp 与桌面 40/窄屏 24dp inset、Empty 自身自然高度；RTL 仍为 64dp 工具栏 + 288dp 正文 + 40dp inset + 打包 Noto；Avatar 48dp 灰度、Avatar Group 40dp 叠放、InputGroup 搜索/清空/支持链接保持 r2 状态。

## 浏览器验证

`browser/` 记录 Outline（自然高度、虚线框未撑满预览）、Background、Avatar Group、InputGroup 与 RTL 阿拉伯语实际画面；四象限（1440/390 × 浅深）由 capture.py 生成，`sheet-all.png` 已逐象限目视复查。Empty 页没有 toast，任务提到的共享 toast 位置问题不适用于本页。

## 自动验证

`tests/`：EmptyExamplesTest **7 项通过**（含七配方顺序、官方 copy、Usage `EmptyMedia`、Learn More 导航、48dp 头像/灰度、Avatar Group 40dp 步进、InputGroup 编辑/清空/链接、RTL 三语顺序）。最终应用 `17aa61fed5cd3f56a86f.wasm`，SHA256 `7f45661cba22c83438633b3dd71bca5bfadb43c773ab974a905c067eaeed9fb0`。r2 记录的完整 25 项矩阵（core EmptyParityTest 7 + 目录 18）本轮未重跑；本轮只重跑目录 EmptyExamplesTest 与浏览器矩阵，不能据旧记录声称这些套件在最终构建上通过。

## 未关闭的差异

- `text-balance`/`text-pretty` 未实现：桌面 Default/Outline/Avatar/InputGroup 与窄屏 Avatar Group/RTL 换行仍与官网不同，不能全部归因于抗锯齿。
- 深色正文/标题观感偏粗，部分图形/阴影/ring/圆角与虚线细节仍按实际字重与绘制逐项核对；头像缓存/缩放采样差异保留（同 URL 文件逐字节一致）。
- 章节说明之外仍缺官网 Composition 前的 “Use the following composition …” 引导句（页面壳 Composition 区只渲染代码树），共享标题/垂直节奏、代码底栏与 API 表排版不同。
- 原生 HTML 链接语义（状态栏、新标签页、浏览器上下文菜单）、读屏与完整键盘/hover/focus、任意 Style/shape 覆盖、极端约束与大内容未关闭；历史旧 Vega 截图矩阵仍未重验。
