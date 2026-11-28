# SwiftUI 与 shadcn 对齐计划

以固定提交的 shadcn Base UI / Nova 为默认参考，逐项核对 SwiftUI registry。保留 iOS 14、macOS 11 的最低版本和现有调用兼容性。组件清单见[逐项审计](swiftui-parity-audit.md)，当前状态和验证证据见[对齐进度](swiftui-parity-progress.md)。截图用例由 [Actions 采集配置](../shadcn/validation/plan.json)维护。

## 对齐与验收

| 阶段 | 验收内容 |
| --- | --- |
| 核对上游 | 固定 shadcn 版本，记录组件、变体、状态、示例、主题及视口。核对 slot、受控状态、关闭和复开规则、交互状态以及 token 所用的角色和计算公式。没有对应示例时在逐项审计中注明。 |
| 验证交互和语义 | 从真实入口覆盖适用的触控、硬件键盘、焦点进入和归还、禁用项跳过、选择及 VoiceOver。按平台记录哪些行为已测，并对需要更高系统版本的能力单独验证最低版本的兼容路径。 |
| 审查外观 | 在 iOS 模拟器采集适用状态的深浅色，按逻辑尺寸与 shadcn 对照。对应的 macOS 行为用原生窗口或测试程序验证。遇到平台渲染差异，记录来源、计算方式、实测结果和处理决定。 |
| 回归与交付 | 日常回归与同平台已发布基准做像素比对。Review 复拍并产出候选截图、shadcn 对照和差异图，合并后按已批准的范围发布。分别记录专项通过、PR 检查、合并和基准发布状态。 |

保持上游 token 的角色和公式，验证自定义配色和实时切换主题。禁止为了消除截图差异而写死颜色、透明度、尺寸或增加平台特例。记录的 Web 与 iOS 渲染差异不改变同平台的回归标准。

按现有优先级完成 Tabs、Menubar、Navigation Menu 与弹层焦点，再验收 Combobox、Select、Calendar、Toast、Carousel 和 Theme，之后继续覆盖其余 registry 条目。各阶段按实际证据更新进度，待测项保留到对应状态验证完成。 [组件验收 SOP](shadcn-component-sop.md)

## 平台渲染差异与处理决定

| 组件与状态 | 上游语义与实测 | 处理决定与回归 |
| --- | --- | --- |
| Popover 默认收起态，深色 Nova 描边触发器 | 上游用 `dark:bg-input/30`。默认 `input` 是 15% 白色，取 30% 后以 4.5% 白色叠到 `#0A0A0A`。Chromium 156 中等价的 RGBA 表达式和 shadcn 样式均输出 `#141414`，透明画布取样得到 8 位 alpha 为 11。旧 iOS 实拍输出 `#151515`。 [上游样式](https://github.com/shadcn-ui/ui/blob/0e5b2e621a9ee4cf6a604bbe6c5c0d5b70561eb4/apps/v4/registry/styles/style-nova.css#L157-L159) · [iOS 基准](https://github.com/csdog/compose-foundation-baselines/blob/2a915c196e0bf69ab65c00f736c96e3727655210/mobile/swiftui/popover/Index.md) | 接受跨平台渲染的一个灰阶差。SwiftUI 使用 `input.opacity(0.3)`，保留主题可配置性。iOS 仍与已发布的 iOS 基准比对。 [修正 PR](https://github.com/csdog/compose-foundation/pull/6) · [专项回归](https://github.com/csdog/compose-foundation/actions/runs/46134149600) |

新增记录时标明组件和状态、固定的参考来源、同一采样区域的实测值、已确认的原因和处理决定。复核时只在证据支持的范围内确认差异。
