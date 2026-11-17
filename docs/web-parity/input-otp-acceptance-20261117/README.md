# Input OTP Web 对齐验收

验收基准为 shadcn/ui Base UI、Nova、Neutral 和 10 px 圆角。官方修订固定在 `968d8cb9dc42ae5d52e8fd356cf84596ec832a26`。13 份源文件覆盖文档、组件、Nova CSS 和十个配方，均与既有来源归档的 SHA-256 一致。[来源](sources.json)

- Form 在 1440、390、320 px 的 JS 和 Wasm 中均可点击标签聚焦并输入完整六码。输入区域分别为 304×48、304×48 和 238×48，较窄时缩小插槽宽度。Separator 和 Invalid 在手机上为三组分配相同插槽宽度。Pattern 与 Form 的实际浏览器文本框带有可访问名称。
- Nova 插槽在 150 ms 中过渡颜色及焦点环。JVM 验证进入和退出中间像素，四个桌面和手机主题组合在 JS/Wasm 中均通过可控时钟的浏览器中间帧采样。空位光标按 Nova CSS 的一秒关键帧显隐，浏览器另以真实时间采样。
- 13 项核心和 36 项目录 JVM、截图校验及所属 lint 通过。三张已有深色基准用旧版和新版 OTP 各重渲一次，确认像素文件逐字节一致后更新。Shape 切换使用 JVM 像素检查。浏览器 live theme 验证已输入值保留，并复核插槽填充颜色。
- 最新工作树已生成 JS、Wasm 和 Desktop 编译结果。双主题、双视口通过全部 80 个配方视图。浏览器实测剪贴板过滤截断、删除、非法字符、禁用和错误状态、语言切换及在 RTL 中直接点击插槽替换字符。朝向和实际阿拉伯语、希伯来语标签有截图证据。深链接、刷新及前进后退通过 20 项检查。七组浏览器复验共绑定 1,000 条实际响应，均与构建文件一致。

[浏览器证据清单](../../../.agent-runs/input-otp/goal-20261117/acceptance-evidence.json) · [构建](../../../.agent-runs/input-otp/goal-20261117/motion-build/artifact-handoff.json) · [JVM 和 lint](../../../.agent-runs/input-otp/goal-20261117/review-fix-checks) · [基准图对照](../../../.agent-runs/input-otp/goal-20261117/motion-golden-control/refresh.json) · [独立终审](independent-review.md)

## 平台边界

Compose 用一个真实可编辑语义节点接收键盘和剪贴板事件，插槽负责显示。浏览器 DOM props、逐槽辅助技术、IME 组合及原生短信验证码自动填充各有平台边界。Form 中的提交、重新发送和示例链接用于本地演示。支持链接显示对应文案，预览没有服务端验证或邮件发送流程。浏览器 CSS 与 Compose 的半透明颜色处在各自的渲染管线中，验收覆盖可见中间帧、时长和终态。
