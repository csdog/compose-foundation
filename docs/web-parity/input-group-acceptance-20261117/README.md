# Input Group Web 对齐验收

基准采用 shadcn/ui Base UI + Nova + Neutral，基础圆角 10 px。官方 main 固定于 `968d8cb9dc42ae5d52e8fd356cf84596ec832a26`。归档 17 份官方文件，涵盖文档、核心组件、Nova 样式和 14 个配方。逐项核对配方顺序及 API 说明。[官方来源](sources.json)

- More 和 Search In 菜单各使用单一可聚焦触发器。桌面和手机的 JS/Wasm 均通过指针打开、菜单选择、外部点击、Tab、Enter、Space 和 Escape。Info 气泡可通过实际指针打开和 Escape 关闭。Search In 使用 12 px 字体和图标，Info 两段文字间距为 4 px。
- Nova 容器边框和背景支持 150 ms 颜色过渡，焦点环即时呈现。JVM 分别验证聚焦和失焦的中间色、即时环、动态主题和圆角及保值。八组 Chromium 可控时钟捕获到边框中间色和即时焦点环。
- 21 项核心和 80 项目录 JVM 测试、共享菜单回归、截图校验及所属 lint 通过。12 张过时基准图经旧版和新版 Input Group 源码分别重渲，两次结果逐字节一致后更新。
- 当前工作树构建生成 49 个 JS 和 45 个 Wasm 文件，Desktop 编译通过。双视口、双主题的 112 个配方视图通过，Canvas 原点、根容器尺寸和 DPR 1 bitmap 一致。真实语言菜单切换通过 12 项方向检查，深链接、刷新及前进后退通过 20 项路由检查。已对照阿拉伯语和希伯来语保存状态的实际字形。

[构建](../../../.agent-runs/input-group/goal-20261117/build/artifact-handoff.json) · [测试](../../../.agent-runs/input-group/goal-20261117/checks/jvm-counts.json) · [截图对照](../../../.agent-runs/input-group/goal-20261117/goldens/refresh.json) · [配方矩阵](../../../.agent-runs/input-group/goal-20261117/audit-matrix/group-matrix.json) · [实际操作](../../../.agent-runs/input-group/goal-20261117/audit-overlays/overlays.json) · [三语](../../../.agent-runs/input-group/goal-20261117/audit-rtl-glyphs/rtl.json) · [路由](../../../.agent-runs/input-group/goal-20261117/audit-routing/routing.json) · [动画](../../../.agent-runs/input-group/goal-20261117/audit-motion/motion.json) · [独立终审](independent-review.md)

- 浏览器专项复核了实际剪贴板写入、确认图标恢复、收藏颜色和前缀边界、圆角、主题切换后保值、控制框与附加按钮焦点环、编辑器高度与分隔线。两个菜单和 Info 气泡完成 12 项底部空间不足时的翻转、焦点恢复和外部关闭检查。八组浏览器证据共绑定 522 条实际响应。 [专项检查](../../../.agent-runs/input-group/goal-20261117/audit-detail-settled/detail.json) · [More 和 Info 翻转](../../../.agent-runs/input-group/goal-20261117/audit-boundary-short/boundary.json) · [Search In 翻转](../../../.agent-runs/input-group/goal-20261117/audit-boundary-search/boundary.json) · [证据清单](../../../.agent-runs/input-group/goal-20261117/acceptance-evidence.json)

## 平台边界

Compose 通过附加内容插槽、`Modifier`、语义和焦点请求表达 React render、className、DOM data-slot 和控件关联。Custom Input 使用随内容增长的 `InputGroupTextarea`，剪贴板按钮使用系统剪贴板并给出临时反馈。浏览器原生 DOM API、屏幕阅读器组合及 CSS 透明色逐帧插值属于平台差异，验收覆盖可见动画、实际输入操作和布局方向。
