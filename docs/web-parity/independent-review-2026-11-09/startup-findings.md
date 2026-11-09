# 启动对账发现

- 67 个 canonical 组件，37 个历史勾选；本轮独立复审均未自动继承。
- Chart：存在 chart-r6 浏览器产物，但 .agent-runs/chart/progress.md 写 baseline、Done None yet、Verification Not run yet；缺少 progress.log。不能直接认定可 handoff。
- Calendar：progress.md 写 30-verify complete，progress.log 尾部仍为 30-verify FAIL；需要对照具体失败事件与后来验证记录，不能批量恢复。
- Context Menu / Dropdown Menu / Field / Input / Input Group / Input OTP / Hover Card / Item / Label：progress.md 写 verify complete，state.json 仍为 scope；需核实证据后再同步，不能凭阶段文字推进。
- token_efficiency.py validate 只校验 state 字段、handoff 标题和行数，不校验测试/截图/source 哈希，不等同于质量验收。
- Sidebar 及通用浏览器脚本正在独立复审和定向修复。

本记录不修改历史 stage marks、DONE.marker 或 canonical 勾选。
