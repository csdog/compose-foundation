# Message Web 对齐验收

固定 shadcn/ui 主分支修订 `968d8cb9dc42ae5d52e8fd356cf84596ec832a26`，核对十份来源、六个官方配方、七个 Kotlin 扩展和六个 API 部件。[来源](sources.json)

- 结束侧消息的 Header 跟随气泡与 Footer。JVM 验证了头像、Header、气泡和 Footer 在阿拉伯语布局中一起镜像。
- 六个官方示例及其 Kotlin 源码采用官网的 Rhea 配置。默认对话用内联矢量 emoji 渲染哭脸。动作按钮使用该配置的 3dp 键盘焦点环。
- 验证浅深主题切换、圆角切换、动作顺序、反应名称和打字实时区域、附件及真实剪贴板。当前 JS/Wasm 在桌面和手机核对 104 个示例位置、48 张官方配方对应截图和 20 项路由检查。39 项 JVM、截图基准和所属 lint 通过，Desktop 验证到编译。

[证据汇总](../../../.agent-runs/message/goal-20261118/acceptance-evidence.json) · [构建](../../../.agent-runs/message/goal-20261118/acceptance-build/artifact-handoff.json) · [独立终审](independent-review.md)

## 平台边界

Message 是布局容器。Compose 用实际按钮名称、键盘焦点、反应名称和礼貌实时区提供对应操作和提示。浏览器取证覆盖 Chromium Canvas 的辅助功能树与焦点像素。官网动作示例展示按钮布局与名称，业务回调由调用者提供。Canvas 与 HTML 的字体栅格、原生工具提示和屏幕阅读器映射各有差异。
