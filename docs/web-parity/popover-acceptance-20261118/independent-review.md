# Popover 独立终审

**PASS** · 2026-11-18

- 独立复算 13 份官方来源、24 个 Popover 相关源码及截图基准、126 个证据文件的哈希。构建前后源码清单相同，304 个 Chromium HTTP 正文均匹配对应 JS/Wasm 分发文件。Desktop 编译通过。
- 复核 48 项聚焦 JVM、6 张截图基准和所属 lint。五张更新的旧基准均逐字节匹配实施前的采样。证据保存旧图、采样和差异位置。
- 两种运行时均实测 200ms 二次点击关闭、外部点击激活目标、触屏 Escape 后恢复触发器、反向 Tab 返回触发器以及再次点击编辑 Max. width。四个碰撞与动画场景均测到进出场中间像素。
- 并行运行时，JS 在最后一次 Tab 后约 240ms 的一次读取仍显示弹层。同一构建的隔离复测只按一次该键，JS/Wasm 均在 780ms 采样时已关闭。JVM 回归验证关闭后焦点到达页面的下一个控件。
- Kotlin 的 `modal=true` 默认值沿用既有 API。官方示例配置为非模态。Canvas 提供 Compose 生成的按钮、展开状态、标题和表单语义。浏览器证据覆盖 Chromium。

[证据清单](../../../.agent-runs/popover/goal-20261118/acceptance-evidence.json) · [官网 Tab 行为](../../../.agent-runs/popover/goal-20261118/official-live/tab.json)
