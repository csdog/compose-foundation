# 文档入口

整理于 2026-11-09。日常文档、验收状态和历史材料分开查阅。

| 要查什么 | 入口 | 维护口径 |
|---|---|---|
| 有哪些组件、对应源码 | [组件目录](components.md) | 直接索引当前 CatalogRegistry，不维护重复参数表 |
| 机器导出与预览 | [元数据说明](component-metadata.md) · [JSON](component-metadata.json) | 静态导出，按需重新生成；不是验收结果 |
| 对齐约定 | [对齐约定与证据入口](shadcn-parity.md) | 当前约定与源码入口；旧色表/支持矩阵在归档 |
| 怎么验收 | [Web 验收计划](shadcn-web-plan.md) | 执行规则；不在这里维护进度 |
| 哪些验收已关闭 | [状态与断点](shadcn-web-progress.md) | 唯一组件勾选入口；交接结束不等于验收完成 |
| 怎么运行截图检查 | [视觉测试](visual-testing.md) | JVM、Android、Web 的不同结果口径 |
| 仍在引用的 Web 证据 | [web-parity](web-parity/README.md) | 保留当前来源、许可、交接和最新轮次依赖 |
| 静态预览图片 | [previews](previews/README.md) | 维持仍被导出使用的公开路径 |
| 旧进度、旧对照与截图 | [归档](archive/README.md) | 历史材料，不自动继承为当前通过证据 |

新增文档优先补到上述对应入口；长篇批次流水、旧轮次截图和被取代的矩阵进入归档，
不要继续堆入当前组件清单。保留证据时注明源码 revision、输入哈希、构建与验证范围。
