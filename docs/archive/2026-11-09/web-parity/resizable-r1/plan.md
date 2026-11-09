# Resizable 第一轮（2026-10-31）

固定 shadcn revision 96da9cc7f1aa10580a3741664e2a77c721206c00；实际依赖 react-resizable-panels 4.5.8，按该 tag 核对默认约束和键盘行为。

- [x] 归档组件、MDX、四个官网配方、Nova CSS、依赖版本及上游事件/布局实现；逐份校验 SHA-256。
- [x] Nova 1dp 分隔线、4×24dp grip（纵向组旋转）、1dp 键盘焦点环；保持可见宽度与命中宽度分离。
- [x] 默认面板范围 0..100%；按可分配内容尺寸计算拖动，排除边框与分隔线；零尺寸端点、无效输入、自定义范围、禁用及方向变更取消。
- [x] 方向键 5%、Home/End；可访问性 SetProgress；水平 RTL 拖动按物理方向改变逻辑首面板。
- [x] 鼠标 10dp / 触摸 20dp 命中范围以组级路由实现，真实鼠标/触摸测试已通过。Compose touchBoundsExpansion 不扩大普通鼠标/触摸命中，已移除该错误方案。
- [x] Default / Vertical / Handle / 三语 RTL 四个官网配方；嵌套比例独立；复制代码从编译源码生成；旧 Handle/Vertical 示例加 Kotlin 后缀避免重名。
- [x] 查看新增 Nova 六张浅深图；源/代码生成幂等检查。
- [x] 目录16项、新图6项独立验证，连同核心15项共37通过；两模块ktlint通过，36张原生目录图已查看。JS26.356s/Wasm18.278s均退出0，但JS仍有os/path解析诊断、Wasm有一条webpack警告，非全量无诊断构建。
- [ ] 官网与本地浏览器桌面/手机 × 浅深；原空间 18 丢失，重建申请待回复。原生 mockWindowInfo 不能替代浏览器验收。
- [ ] resize 指针、多于两面板、collapse/ref/布局持久化与双击还原、组间交叉命中、完整焦点/读屏与动态 Style/大字体矩阵；旧8图中5项通过、3项深色分隔线RGB38→34差异未归因，基线未重录。

版本 0.3.21。组件保留未勾选，Editor 排除，Attachment 遗留暂缓。
