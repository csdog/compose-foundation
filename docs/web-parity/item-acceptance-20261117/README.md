# Item Web 对齐验收

验收基准为 shadcn/ui Base UI、Nova、Neutral 和 10 px 圆角。固定官方修订 `968d8cb9dc42ae5d52e8fd356cf84596ec832a26`，14 份来源覆盖组件、文档、Nova CSS 与 11 个配方，均与既有来源归档哈希一致。[来源](sources.json)

- Link 示例验证当前页回到顶部、重复激活不增加历史条目，以及浏览器返回原锚点。外部示例打开新标签页，浏览器实测无 opener 和 referrer。Default、Image 和 RTL 的可复制代码展示同页激活所用的回调。
- Header 在 390 px 保留三列且卡片底部对齐，在 320 px 以两列分行。Dropdown 的单一触发器支持键盘重新打开、选择、Escape 恢复焦点、外部点击关闭和空间不足时向上翻转。
- Nova 焦点边框采用 100 ms 过渡，焦点环立即切换。JVM 检查获得与失去焦点的中间颜色，以及主题与圆角变化时的重绘。浏览器采样验证悬停进入和退出时的中间颜色，浅深主题下均无越过目标颜色的暗闪。
- 19 项核心和 25 项目录 JVM、6 张现有基准图及所属 lint 通过。当前工作树编译了 JS、Wasm 和 Desktop。浏览器通过 88 个配方视图、20 项深链接与历史检查、4 项菜单碰撞和 8 组动画采样。另验证实际复制的 Kotlin 代码、英语／阿拉伯语／希伯来语切换，以及切换主题时保留希伯来语与 RTL 布局。

[浏览器与测试证据](../../../.agent-runs/item/goal-20261117/acceptance-evidence.json) · [构建](../../../.agent-runs/item/goal-20261117/settled-build/artifact-handoff.json) · [独立终审](independent-review.md)

## 平台边界

Compose 用可聚焦的整行点击节点提供键盘与指针激活，当前 Web 辅助树将其报告为按钮。原生锚点的上下文菜单、修饰键、多标签页命令及 HTML 属性由浏览器提供，属于公共 Kotlin Item API 的平台差异。目录示例匹配普通激活的两种导航结果。Header 的 320 px 分行是为保持内容可读采用的响应式调整，官方页面在该宽度继续使用三列并产生溢出。

实现提交 `f0cc41d3`。构建与运行回执绑定的是验收时的完整工作树。
