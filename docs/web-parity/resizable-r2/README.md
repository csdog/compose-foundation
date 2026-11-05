# Resizable 页面 r2（2026-11-05）

对照官方 [Resizable 页面](https://ui.shadcn.com/docs/components/base/resizable) 与上游固定版本
`8a3536678a528cc012b9e62484290e79d0be410f`（MDX、4 个示例 TSX、`resizable.tsx`、`style-nova.css`）。
来源与哈希见 [sources.json](sources.json)。

## 官方结构

官方页面为：顶部 demo（`h-80`）、About、Installation、Usage、Composition、Vertical、Handle、
RTL、API Reference（仅链接上游）、Changelog（h3 "2025-02-02 react-resizable-panels v4" + v3→v4 对照表）。

## 本轮改动

- 页面描述/标题与官方一致；补 `About`、`Composition`、`Usage` 导入代码、`API Reference` 与
  `Changelog`（v3→v4 八条映射以列表呈现）；移除多余 Examples 标题；ToC 与官方顺序一致
  （About 受文档模型限制位于 Composition 之后）。
- 四个官方预览保持官方顺序与标题：Default / Vertical / Handle / RTL，补官方说明文案；
  Default 预览内高 320（官方 `h-80`），其余 288（官方默认预览高）；修复 Usage 代码块重复导入。
- Kotlin 专有配方裁剪为四条并统一 `Kotlin extension: ` 前缀：Custom pane content、Min fraction、
  Max fraction、Disabled handle；旧列表中与官方重复的 13 个变体不再渲染。
- 组件本身（`shadcn/core/.../resizable`）已具备 Nova 细分隔线/抓手、0..100% 分数钳制、真实
  鼠标/触摸命中区、方向键调整和嵌套比例；本轮未改组件行为。

## 验证

- 聚焦测试 **20 项通过**（4 种视口/主题参数化）：官方顺序/扩展前缀、默认嵌套比例 50/25/75、
  鼠标拖拽、键盘 ArrowRight/Down 调整、触摸拖拽、Vertical 细分隔线、Handle 25% 起点、RTL
  镜像拖拽与三语切换；XML 见 [tests/](tests/)。
- 最终 Wasm 构建：`/tmp/cf-parity/web-build.sh f 8776`，wasm sha256
  `52e9c17c2bd84f125bfefce884246b92aacd162fd2646a931fcefc243c3368c0`。
- 四象限（1440/390 × 浅/深）：[browser/](browser/)，`meta.json` 记录官方 ToC/预览数；桌面/手机
  顶部与官方一致。
- 实际浏览器状态：[browser/states/](browser/states/)：默认组拖拽后外侧比例改变；RTL 下反向
  拖拽改变比例。官方参考：[官方页面](https://ui.shadcn.com/docs/components/base/resizable)。

## 剩余差异（均已解释）

1. **About 位置**：官方位于 Installation 之前；本地文档模型只在 Composition 之后提供指南插槽，
   About 因此显示在 Composition 之后（ToC 顺序同）。
2. **Changelog 表格**：官方 v3→v4 为两张表，本地以八条列表代替；API Reference 为链接段落。
3. **扩展配方**：官方无 Kotlin 扩展；本地保留四条 `Kotlin extension:` 配方（分数上下限、禁用
   handle、自定义面板内容）并移除与官方重复的旧变体。
4. **平台差异**：Canvas 无真实 `role=separator`/`aria-orientation` DOM，用 Compose 语义与
   `ProgressBarRangeInfo` 近似；指针与键盘为真实交互，已在 JVM 与浏览器验证。
