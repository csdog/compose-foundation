# Slider Web 验收

2026-11-18。固定 shadcn/ui `968d8cb9dc42ae5d52e8fd356cf84596ec832a26` 和其 Base UI 1.6.0 依赖 `b34551d644f2e58ebf8fc1050d949f6654ceca6c`。20 个源文件，历史 11 个固定来源逐字节相同。官方 Default、Range、Multiple Thumbs、Vertical、Controlled、Disabled 和 RTL 均核对桌面、手机、浅色和深色。保留 Step、Inverted、Commit 和 Min steps 四个 Kotlin 示例。[官方文档](https://ui.shadcn.com/docs/components/base/slider) · [固定来源](sources.json)。

- 真实鼠标和触摸按在拇指中心旁 4px 时保留当前位置。点中两拇指的正中选中后一个。官方 Range 实测从 [25,50] 出发，PageUp 到 [35,50]，Shift+Left 回 [25,50]，下限拇指越过上限时到 [80,80]，同一次拖动退回后为 [40,80]。本地复制这组操作并用 End 检查键盘的邻居限制。
- 触摸后，滑块仍能接收键盘输入。Home 和 End 将滑块置于端点，并保留页面滚动位置。Controlled 显示实际被接受的值，Disabled 保持不可操作。RTL 中阿拉伯语和希伯来语的右键递减，英语的右键递增。单拇指与多拇指暴露各自可操作的滑块语义。
- Web 对大于 0 且不超过 10 的离散步长采用 Base UI 的十单位粗调。连续值、原生端和更大步长沿用现有规则。手势可在重新组合前发出连续推挤请求，提交回调仍使用调用方已应用的受控值。JVM 分别验证了接受、拒绝和转换请求的调用方。
- 54 项 JVM 测试，含 Groups Nova 键盘兼容测试。9 项所属 lint 通过。12 份 Kotlin 片段从实际预览生成并分别作为编译输入检查。改动前 Vega 的四张实际图均为空白，历史参考只有一条滑轨。修复后焦点图为 208×64，状态图为 208×160，四种状态分别绘制。逐张核对两次独立渲染的字节及最终参考。[逐图记录](golden-references.json)。
- 当前工作树逐一核对 15,363 个文件。49 个 JS、45 个 Wasm 产物文件和 Desktop 编译通过。8 个本地设备、主题和运行时组合覆盖 24 个语言场景。24 次从浏览器精确复制已编译片段，2 次整页复制验证顺序和完整示例。95 条实际响应与产物哈希相符，5 个上下文加载应用 Wasm。
- Compose 使用 Canvas 和浏览器辅助语义覆盖层。触摸区域随设备最小触控尺寸变化，Nova 浏览器画面在八组场景均测得 4px 水平及垂直滑轨、12px 拇指，以及键盘聚焦后外扩 3px 的焦点环。

[当前验收凭证](../../../.agent-runs/slider/goal-20261118/final-evidence.json) · [官方浏览器凭证](../../../.agent-runs/slider/goal-20261118/official-browser/slider-official.json) · [独立审查](independent-review.md)。
