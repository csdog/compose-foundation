# Message Scroller：后续对齐计划

当前状态：基础滚动控制器及六个官网配方已实现并完成定向回归，整个组件仍未完成。来源固定 revision 与42个文件hash见 [sources.json](sources.json)，已知差异和验证边界见 [parity-matrix.json](parity-matrix.json)。Editor排除，Attachment内部遗留继续暂缓。

## 先迁移真实配方

下表每行都必须补真实预览、可复制的公共API代码和必要交互回归；不能用现有 Provider controls 或旧聊天示例替代。共用消息渲染器可以抽取，但预览及导出代码必须展示实际用法。

| 顺序 | 官网源码 | 关键结果与当前进度 |
| --- | --- | --- |
| 1 | message-scroller-opening-position.tsx | **已移植/原生验证，浏览器待验**。原四条open消息与560高卡片；start/end/last-anchor三个Tabs；重复选择后的重新定位，保留64前文 |
| 2 | message-scroller-commands.tsx | **已移植/原生验证，浏览器待验**。原8条消息和4条用户锚点；Jump to…下拉、42字符摘要、选中后平滑对齐start |
| 3 | message-scroller-scrollable.tsx | **已移植/原生验证，浏览器待验**。按源码的实际内容/控件展示start和end状态；尾部占位不能伪造可滚动标记 |
| 4 | message-scroller-visibility.tsx | **已移植/原生验证，浏览器待验**。原4用户锚点、margin12与当前目录跳转；窄布局外侧触发器裁剪未关闭 |
| 5 | message-scroller-load-history.tsx | **已移植/原生验证，浏览器待验**。后5→全部10条的一次加载，真实toast/禁用状态及Root重置，前插保持当前行位置 |
| 6 | message-scroller-previous-context.tsx | **已移植/原生验证，浏览器待验**。原8消息/初始2条，peek64–128，1000ms等待+35ms逐词，3次发送后耗尽及reset |
| 7 | message-scroller-anchoring.tsx | 原回合内容、提交和流式回复；新回合锚定、占位消耗和用户释放 |
| 8 | message-scroller-streaming.tsx | 源码流式数据与状态、取消/重来；自由阅读保持，回到末端恢复 |
| 9 | message-scroller-group-chat.tsx | 原人物/头像/分组和内容；同步滚动行为与响应式布局 |
| 10 | message-scroller-animation.tsx | 按源码移植真实行进退场和流式文本；动画期间补偿、打断与重排 |
| 11 | message-scroller-demo.tsx | 替换页面首屏为官网完整聊天卡片，输入栏/菜单/empty/loading及原始内容按源码逐项对照 |

本轮新增三组后还剩5个官网配方：Default、Anchoring、Group Chat、Streaming、Animation。当前页首屏临时为Context，实际Default/chat仍待迁移。全部六组均未完成浏览器四象限验收，见[第三轮矩阵](../message-scroller-r3/parity-matrix.json)。按用户优先尚未开始组件的要求，下一项推进Navigation Menu；本表五项、原语及浏览器缺口继续保留，不以原生测试通过销项。

## 原语遗留并行记录

补官网零消息时待执行ID的命令及取消/替换规则（源码仅在itemCount===0时接受并排队，已有消息时未知ID仍返回false）；补完整键盘输入、辅助技术和多Viewport支持边界。其余原语遗留逐项以矩阵为准，不因已有测试通过而销项。

## 验收门槛

- 恢复原浏览器工作空间或获得重建答复后，完成桌面/手机 × 浅/深的全部官网配方对照，检查真实提交、加载、滚动、手势和键盘。
- 新图先定向record、查看、再独立verify；旧图只在来源变化明确并检查差异后定向更新。
- 执行受影响的模块测试/质量检查及分别运行JS、Wasm构建，完整记录通过范围和未验证部分。
- 所有11个配方与剩余原语差异有结论、浏览器矩阵完整后，才允许将主清单中的Message Scroller勾选完成。实际pre-commit门禁可执行后提交，不跳过hook。
