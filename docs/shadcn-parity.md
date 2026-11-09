# 对齐约定与证据入口

此页保留正在使用的对齐约定；旧数值表、组件支持矩阵及历次“通过”记录已移至
[2026-11-09 归档快照](archive/2026-11-09/shadcn-parity.md)。历史结果不替代当前源码、构建与浏览器证据。

## 1. Color tokens

默认比较条件为 Base UI / Nova / Neutral / 基础圆角 10。比较必须固定同一基础色、
强调色、浅深主题和状态；透明颜色应在实际父背景上验证。

当前定义见 [BaseColor.kt](../shadcn/core/src/commonMain/kotlin/com/toutiaoyan/compose/ui/tokens/BaseColor.kt)、
[Accent.kt](../shadcn/core/src/commonMain/kotlin/com/toutiaoyan/compose/ui/tokens/Accent.kt) 和
[Theme.kt](../shadcn/core/src/commonMain/kotlin/com/toutiaoyan/compose/ui/theme/Theme.kt)。
434 个历史角色值及色表对照见归档，未在本次整理中重新运行。

## 2. Radius tokens

沿用本项目约定：CSS 的 px 数值与 Compose 的 dp 数值相同；这是相对比例约定，
不是物理单位换算。不得对单个组件额外乘以屏幕密度系数。

## 3. Spacing tokens

尺寸、间距、边框与圆角应对照固定版本的组件源码和 Nova 样式，并检查真实布局。
具体历史测量值见归档；当前组件差异见[验收清单](shadcn-web-progress.md)。

## 4. Component support matrix

- [当前目录索引](components.md)：有哪些组件及对应源码。
- [验收状态](shadcn-web-progress.md)：完整验收是否关闭。
- [验收计划](shadcn-web-plan.md)：适用范围与证据要求。
- [保留的 Web 证据](web-parity/README.md)：当前引用与依赖仍在原位。

实现存在、参数表存在、截图存在分别说明不同事实，不能直接换算为完整验收通过。

## 5. Methodology notes

上游以 Base UI `apps/v4/registry/bases/base/ui/` 与 Nova 样式为目标；本地来源说明见
[vendor/README.md](../vendor/README.md)。每个组件以自己的已固定 revision 和输入哈希为准。

取证需记录比较条件、源码哈希、构建产物、测试和浏览器结果。旧通过或失败记录只有在
相关输入仍匹配时才能复用。历史 OKLCH 转换和逐项数值分析保存在归档，不在此重复维护。

## 6. `StylePreset` (Vega/Nova/...) vs. the old Default/New York split

StylePreset、基础色与强调色是独立维度。预览图集包含不同历史预设，不能根据目录应用
当前默认 Nova 推断每张预览的样式。当前定义见
[StylePreset.kt](../shadcn/core/src/commonMain/kotlin/com/toutiaoyan/compose/ui/tokens/StylePreset.kt)。

## 7. Focus ring implementation: `Modifier.shadcnFocusRing` retired in favor of `dropShadow`

焦点圈、透明背景与最终 shape 的具体实现以当前源码和回归测试为准。静态截图不能证明
实时主题、动态形状、指针/键盘输入模式和焦点恢复都正确。历史实现变更及测量见归档；
当前验证方式见[截图与视觉测试](visual-testing.md)。
