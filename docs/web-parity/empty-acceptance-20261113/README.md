# Empty Web 验收证据

2026-11-13 独立终审通过。[终审记录](independent-review.md)

固定上游 revision 为 `dd41d19e2f80ff6891951f7ced74c2c9a650b065`。官方 Base UI、Nova、Neutral、基础圆角 10 的比较使用当前页面与固定源码。受控 Neutral 参考覆盖 Empty 和相邻控件，官网页面原生 HTML 文本颜色保留独立记录。[官方 Empty](https://ui.shadcn.com/docs/components/base/empty) · [来源和哈希](sources.json)

当前工作树通过 67 项聚焦 JVM 测试、两张 Nova 截图基准、五项所属 lint、Desktop 编译和 JS/Wasm 开发分发构建。Web 滚动持久化修复后重跑 Web lint 和构建，其余测试输入按哈希复核。12 张存在差异、4 张缺少参考的旧截图基准仅供诊断。[测试与基准](../../../.agent-runs/empty/goal-20261113/continuation-f3-native-capture/formal-30a-reconciliation.json) · [当前构建与受影响范围](../../../.agent-runs/empty/goal-20261113/recipe-actions-30b/validation-scope.json)

- 1440×1000 和 390×844 浅深配方矩阵，1095×687 补充视口。浏览器隐藏滚动条时手机预览宽 340px。启用与官网相同的传统滚动条后，本地与官方手机预览宽均为 325px。两种运行时与受控 Neutral 的 56 个配对保留了各自的几何与像素差值。[浏览器证据索引](../../../.agent-runs/empty/goal-20261113/continuation-30c/current-browser-index.json)
- 两种运行时在两个视口都验证了冷加载、刷新、快速二次刷新和同页历史前进后退。滚轮实际位移在刷新后保留，第二次刷新前采样到的可滚动高度为 0。手机 Wasm 的完整历史序列从已验证的深链接进入，较早的触发点击诊断另有保留。[刷新](../../../.agent-runs/empty/goal-20261113/continuation-30c/reload-f3-calibrated/verified-summary.json) · [历史](../../../.agent-runs/empty/goal-20261113/continuation-30c/reload-traversal-mobile-deeplink/results.json)
- 英语、阿拉伯语、希伯来语及两种 RTL 按钮方向，语言选项关闭后的辅助功能树恢复，Escape 和外部点击后通过方向键重新打开。翻译说明弹层验证了三语正文、关闭行为和进出中间帧。[语言焦点](../../../.agent-runs/empty/goal-20261113/continuation-30c/reload-language-focus/language-results.json) · [过渡帧](../../../.agent-runs/empty/goal-20261113/continuation-30c/reload-popover-frames/popover-frame-audit.json)
- 两种运行时的手机浏览器都采集了六个可见 API 小节和真实代码横向滚动。已验证桌面前后页、移动导航抽屉、View Code、输入、清除、长文本和内联链接键盘行为。[API 和代码滚动](../../../.agent-runs/empty/goal-20261113/continuation-30c/reload-api-code/api-code-results.json)
- 独立补充样例验证了形状、圆角、实时深浅切换、无子内容高度、RTL 长文本和大内容边界。其 132 个稳定画面有 66 组逐字节相同的 JS/Wasm 配对。轮廓采样和文本墨迹数据在样例证据中。[补充样例像素](../../../.agent-runs/empty/goal-20261113/continuation-30c/g2-fixture/pixel-audit.json)

Compose Canvas 以语义代理暴露文案和控件。其辅助功能树中的焦点节点可能是无名 Canvas，语言触发器焦点通过真实键盘重新打开验证。浏览器排版实测英语标题为 Geist 14/20、500、字距 −0.35，描述为 Geist 14/22.75、400、字距 0。Invite Members 的 14dp 图标、4dp 间距及 Refresh 图标侧 8dp 内边距与上游匹配，两个按钮分别实测 133/95px，官方为 132.234/92.891px。这两处剩余宽差集中在 CSS 和 Canvas 的标签字形宽度，已按 I16 记录。Compose 语义代理保留可访问名称和已验证的键盘行为。[按钮实测](../../../.agent-runs/empty/goal-20261113/continuation-30c/actions-classic/recipe-action-geometry.json) · [弹层边缘](../../../.agent-runs/empty/goal-20261113/continuation-30c/actions-popup-edge-desktop/popup-edge-results.json)完整工作树的原始空白检查报告 `gradlew.bat` 的既有 CRLF 行，采用 CRLF 感知检查通过。
