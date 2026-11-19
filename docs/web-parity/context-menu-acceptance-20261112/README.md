# Context Menu — 当前验收（2026-11-12）

仅本目录代表当前验收；[r1](../context-menu-r1/README.md) 及旧 30a/30c FAIL 产物保留为历史，不标为当前。
官方固定 `dd41d19e2f80ff6891951f7ced74c2c9a650b065`，Base UI / base-nova / Neutral。
14 份原样文件（MDX、wrapper、Nova CSS、11 配方）的真实路径、URL、日期与 SHA256 见 [sources.json](sources.json)。
参考图复用已校准官方归档（108 图、44 份配方 DOM）；本轮不是重新 live 抓取官方页面。未复制截图。

## 当前源码、产物与终结凭证

[30-verify 正式终结与 release](../../../.agent-runs/context-menu/verify-30/goal-20261112/30-verify-terminal-receipt.json) 已核验，之后才开始本次文档写入。

- [30a-final](../../../.agent-runs/context-menu/verify-30/goal-20261112/current-tree-30a-final/terminal-receipt.json)：core 20、catalog 20、额外 golden verify 2，零失败/跳过。
  两张 idle trigger golden 与当前 baseline/actual 逐字节一致、0 像素变化；不是 open/focus/动画 golden。
- 四项 owning source-set lint 正常复用 **UP-TO-DATE**，报告为空；不是 fresh lint。
  [完整输入绑定](../../../.agent-runs/context-menu/verify-30/goal-20261112/current-tree-30a-final/03-lint-scope-results.json) 覆盖 core commonMain/jvmTest、catalog commonMain/jvmTest，40 阶段重新核验哈希。
- [30b](../../../.agent-runs/context-menu/verify-30/goal-20261112/current-tree-30b/terminal-receipt.json)：正常 JS/Wasm developmentExecutable distribution（48/44 文件）、Desktop `compileKotlin` 通过。
  默认 configuration cache、`--no-build-cache`；旧输出保留后再生；Desktop 仅 compile，不是打包或运行验收。
  [产物清单](../../../.agent-runs/context-menu/verify-30/goal-20261112/current-tree-30b/manifest.json)、[生成器及语义桥](../../../.agent-runs/context-menu/verify-30/goal-20261112/current-tree-30b/reviewed-bridge-binding.json)。
- [30c](../../../.agent-runs/context-menu/verify-30/goal-20261112/current-tree-30c/terminal-receipt.json)：当前树构建与实际浏览器载入绑定。
  [152 response bodies](../../../.agent-runs/context-menu/verify-30/goal-20261112/current-tree-30c/loaded-body-binding.json) 的同 session durable CDP body = server = manifest = disk，无缓存/SW；不是 HEAD 自证。
  包括 94,248,783-byte JS 与 49,137,511-byte Wasm；[92 产物复核](../../../.agent-runs/context-menu/verify-30/goal-20261112/current-tree-30c/artifact-final-recheck.json)。
- [30d 独立审查](../../../.agent-runs/context-menu/verify-30/goal-20261112/current-tree-30d/review-report.md) 与[正式收据](../../../.agent-runs/context-menu/verify-30/goal-20261112/current-tree-30d/terminal-receipt.json)：新上下文、本地 Prime Agent 原生 `openai-codex/gpt-6.1-sol`，无 Codex CLI、无阻塞。
- [40 核验报告](../../../.agent-runs/context-menu/verify-30/goal-20261112/current-tree-40/report.md)：四轮正式终结/release、源与证据重算、清单计数/顺序/链接、保留性、CRLF-aware diff、validator。

## 浏览器范围（不混同计数）

headless Chromium 固定 DPR1，JS/Wasm × 1440×1000 / 390×844 × light/dark，共 8 视图；root/canvas/bitmap 完全绑定。
[112 项矩阵](../../../.agent-runs/context-menu/verify-30/goal-20261112/current-tree-30c/matrix-results.json) 是几何、标题、代表 action 打开/退出检查，不是 112 个完整语义合同。
[88 配方位置](../../../.agent-runs/context-menu/verify-30/goal-20261112/current-tree-30c/exact-recipe-comparison.json) 是 11 配方 × 8 视图的 label/order/visible-width 比较，不等于所有角色/交互完全相同。
另有 128 默认 checked-state 比较及 72 对 JS/Wasm 图像零像素差异；152 bodies 是载入绑定记录，不是额外行为测试。
30d 是落盘证据独立抽样复算：14 官方文件、10 源/测试/生成器、92 产物、152 body 链、74 代表证据；3 份官方 DOM 与局部像素/图检查，不是重新浏览器运行。

真实 Tab→ContextMenu、Shift+F10、跳 disabled Forward、submenu/root 两层 Escape 和恢复、5 秒清理、可信 CDP 长按及 82px 滚动取消均有原始记录。
390×240/220 tall Default 滚动到 Colm、保持打开并反映 checked；live destructive light→dark→light 和 focus ring live 主题验证通过。
Arabic/Hebrew ample-space 左开、English 右开；550px 六组真实碰撞翻转与 390px clamping 分开验证；Sides 四物理方向不变。
退出动画捕获窗口 12–60ms（150ms transition），与 settled 差 25,663 pixels；不声称精确 midpoint 或已证明固定开场时刻。
详见[浏览器报告](../../../.agent-runs/context-menu/verify-30/goal-20261112/current-tree-30c/final-report.json)、[独立样本](../../../.agent-runs/context-menu/verify-30/goal-20261112/current-tree-30d/sample-evidence.json)。

## I16：可核验的剩余差异与未测范围

- Chromium 原生 active element/AX focus 仍是**无名 generic 宿主**；合并的 `Right click here` 文本不是命名 focused trigger。
  本地等价物为真实 Compose focus/text 绑定（JVM Tab/assertIsFocused）与可感知 ring：浏览器六对外部 2472 changed pixels、内部 0；Escape 恢复。
  [焦点等价证据](../../../.agent-runs/context-menu/verify-30/goal-20261112/current-tree-30c/focus-platform-equivalence.md)、[30d 边界](../../../.agent-runs/context-menu/verify-30/goal-20261112/current-tree-30d/review-report.md)。
- 菜单 action/checkbox/radio 的可读语义实际为 button/checkbox/radio，而不是 HTML menuitem*。
  DOM/AX 中 disabled/checked 实际可读；Colm checked=true，People/Theme 各一项，radio 不再有 aria-selected。
  [radio 复核](../../../.agent-runs/context-menu/verify-30/goal-20261112/current-tree-30c/radio-group-validation.json)；不再用平台差异豁免历史 checked=false 缺陷。
- **未实测** VoiceOver/NVDA/TalkBack 朗读操作、原生宿主命名关联、任意深度 trigger/submenu focus。
  浏览器焦点范围是 catalog 默认 trigger/子菜单行；disabled trigger 仅 JVM 实测；任意深度只有源码推理，没有专门当前 JVM 测试。
- 显式 `panelWidth` 是 Kotlin API 表达（144 默认，按配方 192/188/176），不提供 CSS intrinsic min-width 等价；长内容仍受此 API 约束。
  catalog 固定 `Right click here`，不随 pointer-coarse 自动改文案；实际触摸路径已验证。图标采用项目字形，无 icon-set 依赖。
  Hebrew toolbar 字体 fallback、dp 与 CSS px 同数值约定是既有已说明差异；API Reference 与官方一样链接 Base UI，安装/import 保持本项目真实内容。
- 可信 CDP 事件证明浏览器交付，不证明物理 OS 键盘/触摸硬件；共享 Checkbox/Switch/Tabs/aria-invalid 只是桥层回归 smoke，不是重验收三个组件。

## 保留与停止边界

旧 FAIL、原始 harness 错误及临时证据越界恢复记录保留；不声称从未临时越界。
30a manifest 的 commands.log 当前整文件不匹配：封存 828-byte 前缀仍精确匹配，父协调器随后追加两条 validator（504 bytes）。
这是有界 post-seal audit-log 追加，不是源码/测试/结果变更；历史 validator PASS 只代表当时封存状态。
不声称当前 109/109 整文件匹配；其余 108/109 通过。[补充范围与三组 hash](../../../.agent-runs/context-menu/verify-30/goal-20261112/current-tree-40/30a-log-append-scope.json)。
40 只新增必要验收文档及更新组件 handoff/共享清单；未修改产品、测试、配置、常量、阈值或截图。
清单仅 Context Menu 从未勾选变为通过：41/67 → 42/67，26 → 25。Chart 保持未勾选并跳过；下一组件由主编排另启。
不提交、不推送、不发布。
