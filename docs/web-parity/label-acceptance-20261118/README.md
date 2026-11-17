# Label Web 对齐验收

验收基准为 shadcn/ui Base UI、Nova、Neutral 和 10 px 圆角。固定官方修订 `968d8cb9dc42ae5d52e8fd356cf84596ec832a26`。六份来源覆盖文档、Label 实现、三个配方及 Nova 样式，哈希与此前归档一致。[来源](sources.json)

- Default、付款表单和 RTL 的实际复选框有对应的可访问名称。可见标签转发指针激活，Space 在获得焦点的复选框切换状态。付款文字标签可聚焦并编辑 Name on Card、Card Number、CVV 和 Comments。月份和年份标签打开下拉框，触发器在选择后继续提供字段名称及其当前值。
- 付款配方的说明和独立代码块保留在 Label in Field 锚点下。RTL 工具栏置于预览顶部，英语、阿拉伯语和希伯来语的标签与复选框组合居中。语言和主题变化时保留复选框状态；付款地址标签采用官方的普通字重。
- 两张原有截图基准、12 项 JVM 用例及所属 lint 通过。JS、Wasm 和 Desktop 从当前工作树构建。浏览器覆盖 40 个配方视图、20 次路由检查、6 个交互上下文、24 次标签点击后编辑，以及 12 个下拉值。另验证下拉框贴近视口底边时的可视范围、末项选择、外部点击、Escape 和焦点恢复。扩展的必填红色标记、禁用半透明文字和实时主题变化均以实际像素复核。

[证据](../../../.agent-runs/label/goal-20261117/acceptance-evidence.json) · [构建](../../../.agent-runs/label/goal-20261117/settled-build/artifact-handoff.json) · [独立终审](independent-review.md)

## 平台边界

公共 Kotlin Label 提供文本与样式。配方用 FocusRequester、控件语义与指针动作对应 HTML 标签关联，控件拥有实际焦点和键盘操作。Compose 将自定义选择器呈现为自身的可访问菜单节点。付款数据仅在当前示例中编辑与重置。

实现提交 `838d302d`。构建与运行回执绑定的是验收时的完整工作树。
