# Compose Foundation 代码审查进度

[审查计划与验收标准](code-review-plan.md)

审查基线为 `95c932b6`。两轮审查已结束，组件修复已分批提交。目录导航和 Detekt 构建配置各有一项延后事项。审查开始前已有的代码改动和暂存内容均已保留。

## 第一轮

- [x] 盘点本地改动和基线验证结果。
- [x] 审查 Select 的弹层几何、滚动和键盘状态。
- [x] 审查 Toast 的队列、计时和焦点恢复。
- [x] 审查共享弹层与 Web 事件适配。

## 第二轮

- [x] 审查 Slider 的输入、焦点和平台语义。
- [x] 审查 Sheet、Toggle 的触摸焦点和平台适配。
- [x] 审查目录应用的浏览器滚动和导航，登记有重叠改动的待处理项。
- [x] 核对两轮的发现、验证结果和提交。

## 审查发现

每条记录写明文件与行号、影响、处理状态，以及对应的验证结果和提交。验收通过后勾选上方条目。

| 位置 | 发现及影响 | 处理状态 | 验证与提交 |
| --- | --- | --- | --- |
| `SelectPopupGeometry.kt:14`、`SelectPopupGrowth.kt:17`、`Select.kt:198` | 测量、增长和面板最大高度重复表达同一个窗口边距，容易在更新对齐参数时发生分歧。搜索超时和最小宽度也各有多个使用点。 | 已收拢规则 | `3effb8f4`。114 个 Select JVM 用例通过，包含分数密度、空间上限与输入模式。 |
| `Toast.kt:253` | 错误格式化回调抛异常时，原始加载提示永久驻留。三处默认超时也各自定义。 | 已修复 | `d1616329`。修复前测试实际得到 2 条消息，预期为 1 条。修复后两条新增回归均通过，覆盖原异常、无关消息和回调改写的新版本。 |
| `MenuPopup.kt:30`、`PopupPositionMetadataTest.kt:10` | 两条 Nova 路径重复定义过渡时间与滑动距离。弹层元数据测试也有既有 ktlint 格式问题。 | 已收拢参数并清理测试格式 | `1a2f20d6`。16 个共享弹层测试通过，保留各路径的挂载、取消和显式转换处理。 |
| `NovaToastViewport.kt:116` | 可见项先按消息排序，再逐项扫描视口条目查找相同 ID。 | 已复用有序列表 | `d1616329`。32 个 Toast 测试通过，覆盖可见上限、离场顺序、计时和焦点恢复。 |
| `Slider.kt:81`、`:104` | 控制器和根节点无障碍动作分别定义滑块间最小距离。键盘大步进使用了几个语义不同的数字。 | 已共享间距规则并命名步长 | `50f33ddb`。23 个 Slider 测试通过，保留 Web 绝对大步进及其他平台的步幅倍数。 |
| `TouchCanvasFocus.kt:15`、`Sheet.kt:252`、`Toggle.kt:147` | 两处都在指针释放的 Main 阶段向点击处理器暂存触摸信息，且各自负责取消清理。 | 已共享触摸释放生命周期 | `cd15c448`。37 个 Sheet、15 个 Toggle 测试通过。Sheet 保留原生触摸识别，Toggle 仅在捕获到恢复动作时恢复焦点。Slider 拖动与 Popover 非模态交互保留独立路径。 |
| `app/webApp/src/webMain/kotlin/com/toutiaoyan/compose/ui/main.kt:106`、`:122` | 路由绑定直接写入和清空 `window.onpopstate`，覆盖宿主已有处理器。 | 暂缓，文件有用户的导航改动 | Chromium 在 JS/Wasm 中均复现。可正常执行锚点导航、后退和前进，但浏览器派发了 `popstate`，预先注册的宿主处理器调用为 0。整合导航改动后改用成对注册与移除。[导航复现记录](../.agent-runs/code-review-95c932b6/catalog-history.json) |
| `build.gradle.kts:39` 的 Detekt 配置 | 核心模块默认 Gradle Detekt 任务解析依赖后以 `NO-SOURCE` 跳过，任务成功不代表扫描了 KMP 源码。 | 独立 CLI 已覆盖本轮检查，Gradle 配置待单独处理 | 任务输入为 0，构建脚本已有本地改动。直接修复 CI 输入还需处理现有告警。[任务证据](../.agent-runs/code-review-95c932b6/gradle-detekt-coverage.json) |

## 基线检查

- 首轮工作区基线为 160 个 JVM 测试通过。Select 114 个，Toast 30 个，共享弹层 16 个。
- 全模块 ktlint 基线报出 `PopupPositionMetadataTest.kt` 已有 9 项格式告警。Detekt 首次离线运行缺少 `detekt-formatting:1.23.7` 依赖。
- 目录测试首次编译遇到 JVM 堆不足，改用隔离输出与更大的堆后通过。最终 294 个 JVM 测试通过，包含 237 个组件及 57 个目录相关用例。核心模块 ktlint、目录共享源码与 JVM 测试 ktlint、Web 源码 ktlint 通过。JS 与 Wasm 分别生成 49 和 45 个产物文件。
- Detekt 1.23.7 CLI 使用仓库配置和同版本格式插件。以 `95c932b6` 对比本轮所改的 12 个文件，告警从 604 条降到 573 条，无新增的按文件和规则统计项。全模块报告仍有既有告警，包括 Select、Toast 的长函数。保留现有组合生命周期与测试覆盖，避免仅为降低行数而拆分状态所有者。[本地 Detekt 记录](../.agent-runs/code-review-95c932b6/detekt-summary.json)
- [本地 JVM、ktlint 和构建证据](../.agent-runs/code-review-95c932b6/native-and-distributions.json)

## Chromium 实测

- Select 在最终的 JS/Wasm 构建中验证 DPR 2 下的弹层高度、外部点击和滚轮锁定。Toast 在两种运行时验证桌面和触屏交互，覆盖队列、计时与焦点。
- Slider 验证键盘、触屏和滑块值语义。Sheet 验证鼠标与触屏打开时的初始焦点及 Tab 后编辑。Toggle 验证键盘、触屏取消、RTL 和 `aria-pressed`。目录测试验证锚点与前进后退，并复现上表的宿主监听器问题。
- 22 个浏览器配置的断言全部通过，其中 2 个用于复现已登记的目录缺陷。服务响应与最终分发产物哈希一致，Wasm 用例实际加载了应用 Wasm。[浏览器结果](../.agent-runs/code-review-95c932b6/browser-summary.json) · [产物响应核对](../.agent-runs/code-review-95c932b6/browser-provenance.json)
