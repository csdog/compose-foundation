# Marker Web 对齐验收

固定 shadcn/ui 官方修订 `968d8cb9dc42ae5d52e8fd356cf84596ec832a26`。12 份文档、组件、配方和样式来源均匹配既有哈希。以项目的 Nova Neutral 和 10 px 圆角验收，官方示例使用 Rhea，双方样式均归档。[来源](sources.json)

- Revert 行的图标及文字随指针进入、移开做 150 ms 过渡。链接和按钮均有整行键盘轮廓。点击链接文字、图标或空白都执行当前页锚点导航。复制示例接收调用方的同页回调。
- Revert 提示固定在视口顶部居中，桌面和手机分别保留 24 px 和 16 px 外边距。验证指针、Space、Enter 和重复显示。提示配置四秒停留，浏览器验证退出后消失。真实主题切换、Shimmer、Spinner 和复制剪贴板均有浏览器证据。
- 37 项 JVM、基准图和所属 lint 通过。暗色分隔线基准只更新原分隔线所在的一行，当前像素与两份历史实际渲染逐字节一致。精确构建生成 JS、Wasm 和 Desktop，当前浏览器通过 64 个配方视图、20 次导航检查及 8 组动画采样。

[证据](../../../.agent-runs/marker/goal-20261118/acceptance-evidence.json) · [构建](../../../.agent-runs/marker/goal-20261118/settled-build/artifact-handoff.json) · [独立终审](independent-review.md)

## 平台边界

Compose 用 URL 注解、整行指针激活和键盘焦点提供链接等价行为。浏览器原生锚点属性、上下文菜单及修饰键行为属于平台差异。状态用 Compose 礼貌实时区域。官方八个示例没有 RTL 专例，JVM 另验证阿拉伯语文本方向、逻辑起始图标和居中分隔线。多个提示按共享 Kotlin Toaster 的纵向次序显示，Sonner 的折叠层叠属于共享 Toast 的后续范围。

实现提交 `d66a61df`。构建与运行回执绑定的是验收时的完整工作树。
