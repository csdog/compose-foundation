# Field Web 验收证据

本轮以固定 revision `968d8cb9dc42ae5d52e8fd356cf84596ec832a26` 的 Base UI、Nova 和 Neutral 为基准。已复核页面配方顺序、十个 API 小节和阿拉伯语、希伯来语、英语内容。[官方 Field](https://ui.shadcn.com/docs/components/base/field) · [16 份来源及哈希](sources.json)

- 修复了 FieldSet legend 和说明的间距、直接子项的密集布局、Choice Card 和相关控件的可访问名称。Nova Select 内容保持触发器宽度。Default、RTL 与 Responsive Layout 对首个空的必填输入显示本地化错误并移入焦点。
- Web 多滑块分别向 Chromium 提供带名称和值的 slider 节点，键盘可逐个聚焦并调整。单滑块和原生平台沿用现有的聚合语义。[JS/Wasm 表单和双滑块交互](../../../.agent-runs/field/goal-20261113/browser-validation-focus-controls/field-controls-results.json)
- JVM 使用同一个已挂载的 Choice Card 依次验证圆角切换及键盘聚焦的像素变化。Field 路由本身没有圆角选择器。Field 的 Nova 样式没有开关过渡，Choice Card 的悬停、选中和焦点外观直接更新。Field 配方复用 Select 的条目对齐模式，该模式即时显示和关闭。非条目对齐的动画模式不在 Field 配方中。[圆角与焦点测试](../../../.agent-runs/field/goal-20261113/field-appearance-gradle.log)
- 1440×1000 和 390×844 中，合计 104 张本地配方截图核对了实际渲染主题和有界文案区域。手机 Responsive Layout 另滚动到表单本体，与同样范围的官方截图逐项比较。[截图测量](../../../.agent-runs/field/goal-20261113/browser-final-lintclean-matrix/bounded-comparison.json) · [官方手机表单基准](../../../.agent-runs/field/goal-20261113/official-responsive-matrix/official-matrix-results.json)
- Field 的截图基准共六张。四张旧 Vega 图的替换经过单独复核，归档保留了替换之前的参考图和哈希。另有两张 Nova FieldSet 基准。[基准及原因](../../../.agent-runs/field/goal-20261113/vega-baseline-refresh) · [截图校验日志](../../../.agent-runs/field/goal-20261113/legend-legacy-goldens-gradle.log)

Compose Canvas 提供字段和控件的可访问名称、独立滑块、无效状态与错误提示。必填提示由名称中的 required 和提交后的焦点及错误公告表达，浏览器的原生 required 属性没有映射。成功提交会清空演示表单。原生标签的 HTML 关联和表单提交由 Compose 的控件焦点和示例本地校验替代。量测的文字差异包括浏览器和 Canvas 的字形栅格位置差异，暗色的 Name on Card 标签裁剪已人工对照。

当前工作树通过 47 项核心 JVM、4 项 Slider 语义、16 项 Select、45 项目录、1 项 Choice Card 像素测试、6 张基准图，以及共享和平台源码集的 ktlint。最终重建的 JS 49 个文件和 Wasm 45 个文件与浏览器实际验证的分发产物逐文件一致。[完整证据索引](../../../.agent-runs/field/goal-20261113/field-final-evidence.json) · [构建核对](../../../.agent-runs/field/goal-20261113/build/browser-to-current.json)

- 40 项交互包含 Select 在视口底部的碰撞处理、Escape、外点关闭、焦点恢复和 Choice Card 的悬停状态。另有 4 项纯键盘 Select 进入弹层与选择验证。[交互矩阵](../../../.agent-runs/field/goal-20261113/browser-final-lintclean-controls/field-controls-results.json) · [纯键盘](../../../.agent-runs/field/goal-20261113/browser-final-select-keyboard/field-controls-results.json)
- 冷载、深链接、刷新、前进后退和滚动恢复均复核。桌面实际点击上一页和下一页，手机确认紧凑顶栏不显示这两个按钮。[路由复验](../../../.agent-runs/field/goal-20261113/browser-final-lintclean-route/field-route-results.json)

- 四个 JS/Wasm 视口组合通过 View Code 展开，两个手机组合同时通过导航抽屉的开关。[页面操作](../../../.agent-runs/field/goal-20261113/browser-final-shell-header/field-controls-results.json)

2026-11-17 独立终审通过。[终审记录](independent-review.md)
