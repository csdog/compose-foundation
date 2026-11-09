# Web 页面与 shadcn/ui 对齐计划

> 文档定位（2026-11-09 整理）：这是执行与验收规范；状态只在[组件清单](shadcn-web-progress.md)维护。
> 本轮上游快照段落是计划起始来源，具体组件以各自固定 revision 与输入哈希为准。
> 历史批次记录见[归档](archive/README.md)。本次整理不修改下方验收规则。

更新：2026-11-08。本文件只定义目标、执行策略和验收标准。组件状态、实施记录、
测试结果与剩余差异统一记录在 [shadcn-web-progress.md](shadcn-web-progress.md)。

## 目标与基准

以 [ui.shadcn.com](https://ui.shadcn.com/docs/components/base/button) 的实际页面和
`shadcn-ui/ui` 的 Base UI + `base-nova` 源码为基准，尽可能对齐布局、字体、色彩、
圆角、尺寸、示例内容和交互。dp 与 CSS px 沿用项目同数值约定。

- 官方组件逻辑：[registry/bases/base/ui](https://github.com/shadcn-ui/ui/tree/main/apps/v4/registry/bases/base/ui)。
- 官方组件样式：[style-nova.css](https://github.com/shadcn-ui/ui/blob/main/apps/v4/registry/styles/style-nova.css)。
- 官方预览框：[component-preview-tabs.tsx](https://github.com/shadcn-ui/ui/blob/main/apps/v4/components/component-preview-tabs.tsx)。
- 当前线上页面：[Compose Foundation](https://compose-foundation.devops.cn.toutiaoyan.com/button)。
- 本轮开始的参考截图见 [web-parity](web-parity/)。线上版本与本地未提交代码分开记录。

组件展示有差异，先判断来自组件、主题、示例或页面容器；组件自身的差异直接修改
`shadcn/core`。不通过示例专用 padding/color/shape 掩盖组件缺陷。

Kotlin 导入、Gradle 安装方式、项目品牌和链接保留真实内容。已有的平台范围决定
（native-select、direction、form 等）沿用，不为复制 React 文档增加无效按钮或伪实现。
旧文档认定为“可接受”的示例差异，本轮重新检查，不视为自动豁免。

本轮上游快照：`96da9cc7f1aa10580a3741664e2a77c721206c00`。
Geist 来源：`vercel/geist-font@b5d164a0c066c2c9bfcd5ab65dd8244551f0ac06`；
许可保留在 `app/shared/Geist-OFL.txt`。

## 1. 执行目标

以当前官方页面、组件源码、示例和 Nova CSS 为基准，逐组件完成页面视觉、交互、
语义和响应式验收。所有实现必须由当前工作区中的文件、测试结果和浏览器证据证明。

计划中的 `完成` 同时表示：

- 官方当前来源已固定并归档。
- 页面和组件要求已实现。
- 自动测试、lint 和构建通过。
- 桌面、手机、浅色和深色浏览器证据通过。
- 剩余差异已分类并有证据。
- `shadcn-web-progress.md` 已勾选并链接到完整证据。

## 2. 固定比较条件

- **B1**：默认使用 Base UI、Nova、Neutral、基础圆角 10。
- **B2**：基础视口为 1440×1000、1095×687、390×844；完整验收至少覆盖
  1440×1000 与 390×844 的浅色和深色组合。
- **B3**：使用当前工作树启动本地 Web；不得用线上旧版本、历史截图或 HEAD-only
  结果冒充当前修改的验收结果。
- **B4**：采图前确认 Canvas CSS 原点、尺寸和根容器一致，bitmap 尺寸等于
  CSS 尺寸 × DPR。
- **B5**：官方内容以 `ui.shadcn.com` 和 `shadcn-ui/ui` 当前 `main` 为准；
  每次新组件或重新打开组件时固定具体 revision。
- **B6**：记录来源 URL、commit SHA、本地路径、文件 SHA、日期、主题、视口、
  浏览器缩放和构建产物，保存到组件的 `sources.json` 或等价证据文件。

## 3. Stage Goal 规范

### 3.1 Goal 粒度和会话生命周期

- **G1**：一个 goal 只完成一个 Stage，不把整个组件或多个组件写成单个 goal。
- **G2**：一个 Stage 使用一个新的 Codex 线程；出现 `STAGE_DONE <STAGE_ID>` 后
  立即结束当前线程。
- **G3**：下一 Stage 从同一项目目录启动新线程，新线程创建新的 Stage goal，
  并从 `progress.md` 继续，不读取上一线程 transcript。
- **G4**：如果 Stage 因上下文保护提前停止，保留当前 Stage ID，在新线程中重建同一
  Stage goal。
- **G5**：每个 Stage goal 必须声明组件、slug、Stage ID、对应 card、工作目录、
  修改边界、验证命令和完成条件。

Stage 与 card 固定对应：

| Stage | Card | 退出标记 |
| --- | --- | --- |
| `00-scope` | `00-scope.md` | `STAGE_DONE 00-scope` |
| `10-baseline` | `10-baseline.md` | `STAGE_DONE 10-baseline` |
| `20-implement` | `20-implement.md` | `STAGE_DONE 20-implement` |
| `30-verify` | `30-verify.md` | `STAGE_DONE 30-verify` |
| `40-handoff` | `40-handoff.md` | `STAGE_DONE 40-handoff` |

### 3.2 必读上下文

- **C1**：读取
  [token-efficient-compose-workflow](../.agents/skills/token-efficient-compose-workflow/SKILL.md)。
- **C2**：只读取当前 Stage card、当前组件的 `progress.md`、card 明确指定的文件，
  以及 `shadcn-web-progress.md` 中当前组件的条目。
- **C3**：禁止预加载其他组件文件、完整历史 transcript、全部源码树或无关日志。
- **C4**：如果 Skill、card 或组件 handoff 无法读取，停止并报告，不凭记忆执行。

### 3.3 修改边界

- **M1**：只修改当前 card 明确要求的文件，不顺便处理其他组件。
- **M2**：保留用户和其他 agent 的既有改动；不得重写历史或执行破坏性 reset。
- **M3**：不提交、不推送、不发布线上版本。
- **M4**：共享代码变更必须列出受影响组件和验证范围。
- **M5**：不得通过截断内容、隐藏失败、缩小验收范围或批量更新 golden 让测试变绿。

### 3.4 工具输出和上下文预算

- **T1**：预计超过 80 行输出的命令必须通过 `scripts/agent_run.sh`：

  ```bash
  scripts/agent_run.sh .agent-runs/<component-slug> <command-name> -- <command>
  ```

- **T2**：完整日志保存到 `.agent-runs/<component-slug>/logs/`；模型上下文只保留
  摘要、失败指纹和相关尾部。
- **T3**：250k prompt 时主动准备 handoff，不继续扩大当前会话。
- **T4**：DSH 会话使用：

  ```bash
  python3 scripts/token_efficiency.py check-session \
    --session "$DSH_SESSION_ID" --max-prompt 300000
  ```

- **T4A**：Codex 会话使用仓库内 TAO/Codex driver。默认 dry-run，只有显式
  `--execute` 才启动模型：

  ```bash
  python3 scripts/codex_stage_orchestrator.py \
    --component <component-slug> --component-name "<Component Name>" \
    --execute --approve-for-me
  ```

  driver 从 `progress.log` 最后一个 `ok` 自动选择下一 Stage；每次 attempt 使用
  fresh thread；工具动作预算默认 scope/baseline 30、implement 50、verify 60、
  handoff 40；单 session 默认 600 秒 watchdog；同一 Stage 需要轮转时记录 `rotate`，真实失败
  记录 `FAIL` 且自动停止。详细 handoff 仍是 `progress.md`，运行审计写入
  `loop_log.jsonl` 和 `orchestrator/runs.json`。CLI 自动压缩默认使用
  `model_auto_compact_token_limit=250000` 和
  `model_auto_compact_token_limit_scope="body_after_prefix"`；`--compact-threshold 0`
  表示沿用宿主/模型默认值。

- **T5**：`check-session` 返回 2、上下文利用率超过 70%，或工具输出占比超过 50%
  时，更新 handoff 并由新线程继续。
- **T6**：300k 是会话切换 guardrail，不是 goal 的 token budget，也不允许牺牲验收。

- **T7**：截图、contact sheet、DOM、XML 和测试日志都是落盘证据。默认只把摘要、
  文件路径、hash、尺寸和失败指纹带入模型上下文；只有当前判断无法由这些信息完成
  时，才按需读取最小相关片段。

- **T8**：`view_image` 只用于查看预先切好的小块或局部裁剪。单张送模图片默认不超过
  200 万像素、长边不超过 2048 px，禁止对 contact sheet 或全页长图使用
  `detail: "original"`。原始证据可以保留在 1440×1000、390×844 等固定视口文件中。

- **T9**：图片和工具结果按路径与 SHA-256 去重。同一路径且同一 hash 不得在同一会话
  重复查看；只有内容变化、上一轮检查不完整或出现新的明确问题时，才重新查看对应
  局部图，并在 progress 中记录原因。

- **T10**：已解决的日志和图片观察在超过两个 turn 后只保留结论、路径、hash 和失败
  指纹；最近三个 turn 内仍在排查的错误详情、堆栈和相关截图不得过早遮罩。

- **T11**：最多同时运行两个 worker，且只并行相互独立的组件或验证任务。不得为了
  并行而把同一组件拆给多个 agent，也不得向 worker 传递完整 transcript、整份源码、
  全部日志或 contact sheet 原图。

- **T12**：效率观测目标为每组件 prompt 中位数不超过 150k、p95 不超过 250k、
  `view_image` 不超过 8 次、模型调用不超过 120 次。它们是发现退化的目标值，不是
  降低验收的 token budget；超限时在 handoff 记录原因，重复出现时交给 Rule Owner
  调整流程或批准例外。

- **T13**：连续两个 turn 没有新证据、反复重读同一文件，或上下文接近切换阈值时，
  停止当前线程的探索，更新 `progress.md` 并由新线程继续。不得用反复发送“继续”
  维持已过长的 Stage。

- **T13A**：`30-verify` 按 `30a-tests`、`30b-build`、`30c-browser`、
  `30d-audit` 四个 work unit 执行。单次 session 最多完成一个未完成 unit，记录证据后
  handoff；动作预算按 unit 计算，不对整个验证 Stage 反复提高上限。

- **T13B**：对重复机械任务，prompt 只要求优先使用一个 bounded batch script 或
  命令批次，把完整日志和截图落盘，只回传紧凑摘要。PTC 是宿主工具执行模式，不是
  prompt 模式；除非当前 runtime 明确暴露 `run_code` 和 PTC SDK，否则禁止请求或
  模拟 PTC。

- **T13C**：只读官方 revision 查询或被锁屏阻断的浏览器 batch 若仅在 sandbox 内
  失败，必须用完全相同命令升级重试一次；仍失败后才记录环境阻塞。GUI 浏览器不可用
  时优先切换到已安装的 headless Playwright，不反复启动 GUI。

- **T14**：发生 compaction 后，必须重新读取 `progress.md`、当前 git 状态和 card
  指定证据，确认摘要没有丢失路径、失败指纹、未完成项或用户约束，再从当前状态继续。

- **T15**：效率按每个完成组件衡量，至少记录完成项、模型调用数、prompt
  中位数/p95、缓存命中率、送模图片数、压缩次数和最终质量结果。高缓存命中率不能
  单独证明流程高效，也不能作为保留超大上下文的理由。

- **T16**：Codex `turn.completed.usage.input_tokens` 是整个 Stage 内所有请求的
  累计输入，不是最后一次 prompt 的大小。它只用于累计 Stage 成本告警；单次上下文
  使用 `contextPressure`、session projection 或等价逐请求数据判断。

### 3.5 Handoff 要求

- **H1**：唯一持久工作记忆是
  `.agent-runs/<component-slug>/progress.md`。
- **H2**：`progress.md` 不超过 150 行，保留精确 goal、当前 Stage、已完成项、
  修改路径、验证命令和结果、证据、剩余项、失败签名、排除方案和下一步 action。
- **H3**：每个 Stage 结束前运行：

  ```bash
  python3 scripts/token_efficiency.py validate \
    --run-dir .agent-runs/<component-slug>
  ```

- **H4**：同一失败签名出现两次后停止重复尝试，修复根因或记录真实阻塞。
- **H5**：不能只用完整 transcript、临时聊天摘要或模型记忆代替 `progress.md`。

### 3.6 Agent 角色和上下文分工

角色分工用于隔离责任和上下文，不只是给同一个 agent 分配不同标题。每个角色必须
有明确的读取范围、写入权限、命令权限、输出和停止条件。

| 编号 | 角色 | 读取 | 允许操作 | 输出 | 禁止 |
| --- | --- | --- | --- | --- | --- |
| `R1` | Orchestrator | plan、progress、当前 card、紧凑证据摘要 | 选择 Stage、检查 gate、维护 handoff | 下一 action、阻塞、汇总指标 | 不替代 implementer 写业务代码，不读取整份历史 |
| `R2` | Implementer | 当前 card、原需求、progress、指定源码和测试 | 修改当前 Stage 允许的文件，运行快速聚焦检查 | diff、修改路径、已知风险 | 不审查自己的实现，不运行完整 survey build |
| `R3` | Reviewer | 规则、原需求、diff、必要源码行 | 只读检查，提出带文件/行/规则依据的问题 | finding 列表和严重度 | 不修改文件，不看 implementer 完整推理 |
| `R4` | Fixer | confirmed findings、相关 diff 和最小源码片段 | 只修复已确认问题 | 精确修复和复验路径 | 不扩大范围，不自行接受或关闭 finding |
| `R5` | Verifier | 验收要求、changed paths、test command、失败证据 | 只运行机械验证并保存 XML/截图/日志 | pass/fail、证据路径、失败指纹 | 不实现功能，不降低验收标准 |
| `R6` | Build/Test Daemon | 当前工作树 hash 和固定构建/测试命令 | 作为唯一进程执行批量 survey build/test，生成编号结果 | `build-output-rN.txt` 或测试结果队列 | 不是 LLM agent，不做语义判断 |
| `R7` | Rule Owner | 重复失败、review finding、progress | 修改 plan/card/rulebook 或批准豁免 | 规则 amendment、重新生成范围 | 不逐实例手补同一类错误 |

角色执行要求：

- **R8**：Stage `00-scope`、`10-baseline` 和 `40-handoff` 默认由 Orchestrator
  完成；`20-implement` 使用 Implementer；`30-verify` 优先使用独立 Verifier。
- **R9**：独立组件的小范围修改可以采用单 agent 的 `solo` 模式，但实现者仍需用
  机械脚本验收，并在 handoff 中声明没有独立 reviewer。
- **R10**：涉及共享组件、公共 API、语义、布局系统、并发、平台差异或超过一个
  明确验收维度的 Stage，必须使用独立 Reviewer；Reviewer 和 Implementer 不得共享
  同一连续上下文。
- **R11**：Reviewer 提出 correctness、memory/semantics、公共行为或回归风险时必须
  由 Fixer 或新的修复会话处理；纯格式问题可以由原 Implementer 修复。
- **R12**：同一失败签名出现三次，或 Reviewer 在多处发现同一模式时，停止逐实例
  修复，交给 Rule Owner 修改 plan/card 并指定重新生成范围。
- **R13**：一个组件同一时间最多一个 Implementer。只有相互独立的组件可以并行；
  总 active workers 不超过 token-efficient workflow 规定的上限。
- **R14**：角色间只传递文件路径、diff、验收要求、失败指纹、证据路径和短摘要；
  不传完整 transcript、实现者完整思维链、全部源码或完整测试日志。
- **R15**：Reviewer 和 Verifier 必须拥有独立上下文。如果当前执行器无法创建独立
  session/subagent，应在 gate 停止并请求新线程，不能由同一 agent 自审后声称独立
  审查通过。
- **R16**：批量或并发验证必须由单一 Build/Test Daemon 执行，其他角色只消费编号
  结果文件。当前尚未实现项目级 daemon；在实现前使用 `agent_run.sh` 和现有锁串行
  执行，并明确记录这只是日志遮罩/互斥，不是完整 daemon。
- **R17**：Review finding、test failure 和 source evidence 都必须引用可复核的
  文件、行号、命令、截图或错误指纹；不得只写“看起来没问题”。

## 4. 组件实现要求

### 4.1 官方页面与配方

- **I1**：官方 MDX、组件源码、全部示例和 Nova CSS 必须按固定 revision 归档。
- **I2**：官方配方顺序、标题、说明、代码、Composition、Usage、API 和 RTL 文案
  必须逐项核对。
- **I3**：Compose-only 扩展统一使用 `Kotlin extension:` 前缀，不伪装成官方配方。
- **I4**：新增本地内容必须有用途说明，不得为了凑齐页面制造无效按钮或伪实现。

### 4.2 视觉与交互状态

- **I5**：按适用情况验证 default、hover、focus、pressed、disabled、
  selected/checked、invalid、loading 和 empty。
- **I6**：弹层另验证定位、碰撞翻转、Escape、外部点击、焦点进入和焦点恢复。
- **I7**：动画验证中间帧，静态首尾截图不能代替 transition 行为。
- **I8**：动态主题和 shape 切换必须验证过程，静态浅深截图不能证明状态没有冻结。
- **I9**：RTL 至少验证阿拉伯语布局和方向；组件要求支持时覆盖英语、希伯来语。

- **I18**：浏览器截图默认使用固定视口或明确 `clip`；不得把 `fullPage` 长截图作为
  常规验收图。确需全页图时只作为指定诊断证据落盘，不直接送入模型上下文。

- **I19**：contact sheet 可以作为人工复核或独立 reviewer 的归档证据，但必须由
  脚本生成并记录每个源图的 hash、顺序、视口和主题。模型审查使用按 section 或按行
  切分的小图，不能让完整长图取代局部验证。

- **I20**：视觉验收先运行确定性检查，包括尺寸、DOM/布局元数据、文件 hash 和像素
  差异；再用低分辨率索引定位异常，最后只查看异常 section 的局部裁剪。不得逐张打开
  全部图片而不先缩小候选范围。

- **I21**：Reviewer 和 Verifier 只接收紧凑证据 manifest、失败指纹和异常区域图片；
  不能把同一 contact sheet 或完整截图组重复加载到多个角色上下文。

### 4.3 全站文档骨架

- **I10**：Geist 字体、标题/描述/正文行高与字重符合基准。
- **I11**：顶栏、侧栏、正文宽度、标题操作、前后页、目录和锚点导航均可用。
- **I12**：预览卡、代码底栏、View Code、长示例和手机宽度符合基准。
- **I13**：页面标题、深链接、刷新、前进/后退、滚动恢复和加载状态通过验证。
- **I14**：默认预览、Installation、Usage、Examples 和 API 不重复、不缺失、
  不错序到无法接受。

### 4.4 平台差异

- **I15**：先判断差异来自组件、主题、示例、共享骨架或浏览器平台。
- **I16**：Compose 无法等价提供的 HTML/ARIA、原生控件、DOM、字体栅格或浏览器
  行为可以记录为 accepted platform/API difference，但必须给出本地语义等价物。
- **I17**：不能用“Compose 平台差异”笼统解释布局、颜色、间距或交互缺陷。

### 4.5 建议执行顺序

1. 基础：Code Block、Button、Badge、Card、Input、Label、Textarea、Separator、
   Skeleton、Spinner、Kbd、Avatar。
2. 表单：Checkbox、Radio Group、Switch、Toggle、Toggle Group、Slider、
   Input Group、Button Group、Field、Input OTP、Select、Combobox。
3. 组合与展开：Accordion、Collapsible、Tabs、Breadcrumb、Item、Empty、
   Progress、Alert。
4. 弹层：Tooltip、Popover、Hover Card、Dropdown Menu、Context Menu、Dialog、
   Alert Dialog、Sheet、Drawer、Command、Menubar、Navigation Menu、Toast。
5. 布局与数据：Table、Pagination、Scroll Area、Resizable、Carousel、Calendar、
   Date Picker、Chart、Sidebar、Aspect Ratio。
6. 扩展：Trace、Questionnaire、Message、Bubble、Attachment、Marker、
   Message Scroller、Shimmer、Scroll Fade、Chip、Text、Stepper。

建议顺序不是跨组件 goal 的授权；每次仍只选择一个组件和一个 Stage。

## 5. 验证要求

- **V1**：运行当前组件和页面最窄但充分的聚焦测试；窄测试不能支持更大范围结论。
- **V2**：运行 owning module/source-set 的 ktlint；失败必须区分新增问题和既有问题。
- **V3**：涉及共享或多平台代码时，运行当前 card 要求的 JVM、JS、Wasm 或平台编译。
- **V4**：使用当前 Wasm/本地构建完成浅深、桌面和手机浏览器检查。
- **V5**：组件改动增加有意义的尺寸、像素或行为回归；禁止批量刷新掩盖差异。
- **V6**：测试、lint、构建和浏览器证据分别记录，不能互相替代。
- **V7**：运行 `git diff --check`。
- **V8**：把 source、测试 XML、截图、浏览器 meta、构建 hash 和失败证据路径写入
  组件目录及 `progress.md`。

## 6. Stage 完成条件

只有以下条件全部成立，才能把当前 Stage goal 标记为 complete：

1. 当前 card 的全部要求已满足。
2. 本 Stage 要求的修改均已落盘。
3. 测试、lint、构建或浏览器证据覆盖对应验收范围。
4. 未解决问题已分类，并有准确文件路径和失败证据。
5. `progress.md` 已更新并通过 validator。
6. `git diff --check` 通过。
7. 没有修改当前 Stage 范围之外的文件。
8. 未把完整 contact sheet、全页长图、原始 DOM、完整源码树或整份日志作为聊天输入；
   如有必要例外，handoff 已记录原因、具体问题和最小证据路径。
9. 最终回复最后一行严格输出 `STAGE_DONE <STAGE_ID>`。

Stage 完成后立即停止，不进入下一 Stage。整个组件尚未完成不能作为扩展本轮范围的理由。

## 7. Stage Goal 启动模板

下面是唯一需要复制的 goal 入口。goal 不在模板里重复规则；执行时必须读取本文件
第 2–6 节，以其中的编号要求为准。

```text
/goal 在 <PROJECT_ROOT> 执行 <COMPONENT_NAME>（<component-slug>）
Web parity 的 Stage <STAGE_ID>（<STAGE_CARD_FILE>）。

本 goal 只完成这个 Stage。必须使用
$token-efficient-compose-workflow，并读取：
1. docs/shadcn-web-plan.md 第 2–6 节
2. .agents/skills/token-efficient-compose-workflow/SKILL.md
3. .agents/skills/token-efficient-compose-workflow/cards/<STAGE_CARD_FILE>
4. .agent-runs/<component-slug>/progress.md（如果存在）

严格遵守 B、G、C、M、T、H、I、V 编号要求，只修改当前 card 明确要求的文件。
严格执行 R1-R17：声明本轮角色；高风险 Stage 使用独立 Reviewer；需要独立验证时
不得由 Implementer 自审。角色间只传 diff、路径、失败指纹和紧凑证据。
截图、contact sheet、DOM、XML 和完整日志只落盘并传递路径与摘要；模型只看切好的
局部图或明确异常区域。不得对 contact sheet 使用 detail original，同一图片 hash
不得在本会话重复查看。最多并行两个独立 worker。重复机械检查优先使用一个 bounded
batch script，不把断言拆成多次工具调用。除非 runtime 明确暴露 PTC `run_code`
transport，否则禁止请求或模拟 PTC。
完成后更新 progress.md，运行 card 指定验证、handoff validator 和 git diff --check。
如果 Skill/card 无法读取，停止并报告。250k 时准备 handoff；300k 或 check-session
返回 2 时停止当前线程。连续两个 turn 无新证据或发生 compaction 后先重读
progress.md 和当前状态。完成当前 Stage 后立即停止，最后一行输出：
STAGE_DONE <STAGE_ID>
```

占位符：

| 占位符 | 值 |
| --- | --- |
| `<PROJECT_ROOT>` | `compose-foundation` 的绝对路径 |
| `<COMPONENT_NAME>` | 组件显示名 |
| `<component-slug>` | 组件目录和 handoff 使用的 slug |
| `<STAGE_ID>` | 第 3.1 节中的 Stage ID |
| `<STAGE_CARD_FILE>` | 与 Stage 同名的 card 文件 |

## 8. 组件完成与总计划完成

组件勾选条件：

- 当前官方来源已固定，所有官方配方和 API 已核对。
- 适用视觉、交互、响应式和 RTL 状态已通过。
- 自动测试、lint、构建和浏览器证据齐全。
- 剩余差异已解释并记录为 accepted platform/API difference。
- 组件 README、`sources.json` 和 `shadcn-web-progress.md` 已更新。

总计划完成条件：

- 全站文档骨架完成。
- 逐组件清单全部关闭。
- 适用交互回归通过。
- 桌面、手机、浅色和深色均有当前本地构建证据。
- 历史 Tick、源码注释、编译成功或单个窄测试不能单独作为完成证明。
