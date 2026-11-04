# Questionnaire 第一轮（2026-10-31）

固定96da9cc，已归档14配方、组件、MDX、Nova CSS及headless组件/类型/四个hook，共24份来源。

- [x] 核心Nova选项：默认indicator，16dp radio/checkbox、8dp dot/14dp check，44dp最小行、12/10padding、10gap、选中/hover/focus/invalid样式。
- [x] 12/16sp进度、16/22sp标题、14/20sp说明与错误、14/19.25sp选项、20dp快捷键；低于640dp按钮/输入44dp。
- [x] 首题Previous与必填Skip隐藏；末题optional Skip提交；未答optional必须明确Skip；提交验证所有题。
- [x] 单选固定项/自由输入互斥、保留草稿；多选提交按集合顺序；输入和选项快捷键聚焦；Enter确认已选答案。
- [x] 控制器渲染字段用SnapshotState，初始/变更快捷键与受控item反应。
- [x] 官网Default三题与真实Toast payload；可复制代码从编译源码生成。
- [x] 原生38项、两项ktlint、JS/Wasm最终验证通过；36张目录原生图与4张新基准已查看；旧4图混合差异保留。
- [ ] 其余13个官网配方：multiple/freeform/skip/shortcuts/validation/controlled/resume/conditional/navigation-state/progress/animated/card/dialog。
- [ ] 官网与本地浏览器desktop/mobile×light/dark；原空间18丢失，恢复申请待回复。
- [ ] 完整键盘/IME/重复按键/读屏/字体缩放/动画/动态集合矩阵；native输入与HTML约束验证差异；Toast布局依赖后续Toast复核。

版本0.3.20。迁移：Choice现在自己画indicator，原手动ChoiceInput/ChoiceShortcut需要移除；requiredMessage可空，默认文案按required决定。原扩展示例保留，但不当作其余官网13配方已迁移。

组件保持未勾选，Editor排除，Attachment遗留暂缓。
