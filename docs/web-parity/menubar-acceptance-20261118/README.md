# Menubar Web 对齐验收

固定 shadcn/ui 官方修订 `968d8cb9dc42ae5d52e8fd356cf84596ec832a26`，核对组件、Nova 样式和六个官方示例。目录另有 39 个标注为 `Kotlin extension` 的示例。[来源](sources.json)

- 菜单栏有一个 Tab 停靠点。顶层和二级菜单分别验证正反向 Tab、Escape、方向键、禁用和焦点恢复。受控调用方延迟确认关闭时，焦点随后继续移动。展开面板时，鼠标悬停可切换顶层菜单。触屏可打开二级菜单，DPR 2 下另验证触屏转鼠标。
- 根面板最小宽度 144dp，二级面板最小宽度 128dp，并按内容扩展。二级面板使用 Nova 的较重阴影。两种面板都采到了 100ms 过渡的中间帧，另实测视口底部翻转、外部关闭、勾选和单选、主题切换及英阿希三语。
- 33 项 Menubar JVM 检查通过。共享 Dropdown Menu 和 Context Menu 聚焦集成、Menubar 截图基准和所属 lint 同时通过。当前 JS/Wasm 开发分发覆盖桌面和手机、浅色和深色，共核对 360 个示例位置。Desktop 验证到编译。

[证据汇总](../../../.agent-runs/menubar/goal-20261118/acceptance-evidence.json) · [构建](../../../.agent-runs/menubar/goal-20261118/closure-build/artifact-handoff.json) · [独立终审](independent-review.md)

## 平台边界

Compose 通过按钮角色、展开状态、复选框和单选按钮的选择状态以及键盘焦点提供对应操作。Canvas Web 的辅助技术映射与官方 HTML 的 `menubar`、`menuitem` 原生角色存在差异。浏览器运行验证覆盖 Chromium。目录使用 Nova 的固定圆角配置，主题切换通过实际交互和渲染验证。
