# Menubar 独立终审

**PASS** · 2026-11-18

- 独立复核官方源码哈希和浏览器交互。官方子菜单实测最小宽度 128px，使用较重阴影。确认 Tab 离开整组菜单，Shift+Tab 从二级菜单返回 Share。
- 核对当前源码清单与构建前后哈希。复算 49 个 JS 和 45 个 Wasm 分发文件的哈希。33 项 Menubar JVM 检查、截图基准、所属 lint 和共享菜单集成通过。
- 核对 360 个配方位置、48 个交互检查、20 个路由检查、48 张官方配方对应的本地截图和两个 DPR 2 混合输入场景。根面板和子菜单的开合均采到中间帧。Wasm 手机浅色用例曾在截图解码超时，用同一分发重跑通过。
- 浏览器响应的磁盘文件、实际 HTTP 正文与分发清单一致，共 361 条匹配。完成范围包括单个 Tab 停靠点、受控关闭、RTL、真实主题切换和弹层碰撞。Canvas 角色映射、目录固定圆角和 Chromium 覆盖范围记录在验收说明。

[当前证据汇总](../../../.agent-runs/menubar/goal-20261118/acceptance-evidence.json) · [官方实时测量](../../../.agent-runs/menubar/goal-20261118/official-live/submenu-tab.json)
