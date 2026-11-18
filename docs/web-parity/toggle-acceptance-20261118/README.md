# Toggle Web 验收

2026-11-18。验收采用 shadcn/ui `968d8cb9dc42ae5d52e8fd356cf84596ec832a26` 和其 Base UI 1.6.0 依赖 `b34551d644f2e58ebf8fc1050d949f6654ceca6c`，共 13 个固定来源。Default、Outline、With Text、Size、Disabled 和 RTL 对应官网预览。目录包含 Installation、Usage 与 API Reference，保留七个 Kotlin 扩展示例并增加 Invalid 状态恢复示例。[官方网站](https://ui.shadcn.com/docs/components/base/toggle) · [固定来源](sources.json)。

- Nova 主示例为 28px 高的 Outline 收藏按钮，选中时切换填充图标。悬停与按住采用相同颜色，释放后切换选中状态。三个尺寸的外观高度为 28、32 和 36px。Outline 双控件独立切换，两个 Disabled 控件保持关闭。阿拉伯语和希伯来语从右侧显示图标，英语从左侧显示，切换语言保留当前状态。
- 手机触摸取消保留焦点与状态。成功轻触后焦点留在 Compose Canvas，可信 Space 和 Enter 切换同一个 Toggle 且页面保持原滚动位置。Nova 控件在 Chromium 辅助功能树以 button 角色公开 pressed 状态。统一适配层继续向 Checkbox、Radio Group 和 Switch 公开 checked 状态，普通按钮保留自身语义。
- `gradle/patches/compose-web-toggle-aria-pressed.patch` 保存可复用补丁。当前工作树的 `gradle/compose-web-ui/build.gradle.kts` 原先未跟踪，实际构建使用的是在该文件上应用补丁后的版本。此文件的已有内容与暂存状态由原工作区保留。生成的 Compose Web 辅助功能契约经过 SHA-256 和实际 JS/Wasm 同源响应验证。
- 85 项功能 JVM、9 项截图测试及 8 张参考图、9 项所属 lint、15 份独立 Kotlin 片段通过。两个 Invalid 参考经逐图审查后更新，实际图在改动前及两次候选中逐字节一致。Toggle Group 的两处历史截图差异在改动前后实际图逐字节一致，其原参考图保持不变。[逐图记录](golden-references.json)。
- 当次核对 15,380 个工作树文件，构建产出 49 个 JS、45 个 Wasm 文件，Desktop 编译通过。真实 Chromium 通过 8 个设备与主题组合和 8 个相邻组件上下文，逐字节核对 30 次单份复制、2 次整页复制和 219 条实际响应。9 个上下文加载应用 Wasm。

[当前验收凭证](../../../.agent-runs/toggle/goal-20261118/final-evidence.json) · [官方浏览器对照](../../../.agent-runs/toggle/goal-20261118/official-browser/official-toggle.json) · [独立审查](independent-review.md)。
