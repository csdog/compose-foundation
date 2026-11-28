# SwiftUI 与 shadcn 对齐进度

更新于 2026-11-28。目标、验收标准和平台渲染差异见[对齐计划](swiftui-parity-plan.md)。完整的 registry 清单及组件层面的历史证据见[逐项审计](swiftui-parity-audit.md)。下表记录当前工作项，专项通过只覆盖对应的环境、用例和提交。

| 工作项 | 当前证据 | 待验收或交付 |
| --- | --- | --- |
| Popover、Button / Tabs、Alert / Card 的深色描边 | 已移除默认页的固定 `#141414`，恢复 `input.opacity(0.3)`。一灰阶的跨平台差异已记入计划。 [Popover #6](https://github.com/csdog/compose-foundation/pull/6) · [Button / Tabs #7](https://github.com/csdog/compose-foundation/pull/7) · [Alert / Card #20](https://github.com/csdog/compose-foundation/pull/20) | 新提交的 PR 检查已触发。确认 SwiftUI Popover 专项日常回归与已发布 iOS 基准的结果。 [专项运行](https://github.com/csdog/compose-foundation/actions/runs/46134149600) |
| Calendar 键盘焦点 | iPad 普通键盘模式通过跨月焦点移动，Return 才提交选择。开启完全键盘访问后，对照按钮可激活，Calendar 日期选择未变化。 [PR #59](https://github.com/csdog/compose-foundation/pull/59) · [最近一次 iPad 诊断](https://github.com/csdog/compose-foundation/actions/runs/46131727819/job/140938193627) | 继续定位完全键盘访问下的日期激活，验证实际 VoiceOver。 |
| Toggle Group | iPad 键盘和触控通过横向 LTR、横向 RTL 和纵向用例。iPhone 六张深浅色实拍与复拍一致。 [PR #64](https://github.com/csdog/compose-foundation/pull/64) · [专项运行](https://github.com/csdog/compose-foundation/actions/runs/46127910706) | 沿用专项结果作为审核证据。实际 VoiceOver 待验。 |
| Resizable | macOS 拖动、方向键和禁用回归通过。iPhone 重新定位后通过横向 LTR、RTL、纵向拖动和禁用用例，截图采集通过。 [PR #66](https://github.com/csdog/compose-foundation/pull/66) · [macOS 日志](https://github.com/csdog/compose-foundation/actions/runs/46127853397/job/140926387148) | 保留专项结果供 PR 审核。 [iPhone 交互截图](https://github.com/csdog/compose-foundation/actions/runs/46131914196/artifacts/15257838597) |
| Progress | 六张 iPhone 实拍与复拍一致，已验证英文与阿拉伯语的填充方向。macOS 包构建和通用焦点规则通过。 [PR #71](https://github.com/csdog/compose-foundation/pull/71) · [专项运行](https://github.com/csdog/compose-foundation/actions/runs/46129844621) | 验证进度变化过程和实际 VoiceOver。 |
| Chart | 横轴、图例和图内选中提示已提交草稿。二月提示牌的两张 shadcn 深浅色参考图已复拍并提审。 [横轴 #73](https://github.com/csdog/compose-foundation/pull/73) · [图例 #74](https://github.com/csdog/compose-foundation/pull/74) · [提示牌 #75](https://github.com/csdog/compose-foundation/pull/75) · [参考图 PR](https://github.com/csdog/shadcn-baselines/pull/5) | 横轴和图例的 iOS 运行在审计应用的颜色初始化处编译失败。待修复后验证触控、无障碍摘要和深浅色实拍。参考图审核合入后再更新父仓库子模块指针。 [图例编译日志](https://github.com/csdog/compose-foundation/actions/runs/46131611805/job/140937867718) [最新提示牌 Review](https://github.com/csdog/compose-foundation/actions/runs/46132590909) |

更新进度时记录状态、验证范围、对应的源码提交或 PR，以及 Actions 日志或产物链接。若 Review 产物过期，链接仍可指向运行，重要结论和复现条件保留在逐项审计里。
