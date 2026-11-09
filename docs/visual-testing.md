# 截图与视觉测试

本页维护测试入口与结果口径。旧覆盖矩阵、历史缺口、工具版本和当时 CI 状态见
[整理前快照](archive/2026-11-09/visual-testing.md)。本次整理未执行 Gradle、重新截图或更新基准。

## JVM / Desktop

- 测试源码：[shadcn/core/src/jvmTest](../shadcn/core/src/jvmTest/)。
- 已提交基准：[shadcn/core/src/jvmTest/snapshots](../shadcn/core/src/jvmTest/snapshots/)。
- 任务定义：[shadcn/core/build.gradle.kts](../shadcn/core/build.gradle.kts)。

下列是 Gradle 任务示例；agent 执行时继续遵循环境中的 `gradle-run` 封装规则。

```sh
# 聚焦测试体；按具体任务附加 --tests 过滤器
./gradlew :shadcn:core:jvmTest

# 显式像素比对
./gradlew :shadcn:core:verifyRoborazziJvm

# 仅在有意修改外观并审核差异时录制新基准
./gradlew :shadcn:core:recordRoborazziJvm
```

普通 `jvmTest` 通过不能单独证明启用了像素比对；上传了 diff 文件也不能证明比较任务已执行。
当前 CI 任务见[ci.yml](../.github/workflows/ci.yml)，应以实际执行命令与报告为准。
不要通过批量更新 goldens 掩盖未解释的差异。

## Android Preview / Layoutlib

- 宿主：[app/androidApp](../app/androidApp/)。
- 测试源码：[screenshotTest](../app/androidApp/src/screenshotTest/)。
- 基准：[reference](../app/androidApp/src/screenshotTestDefaultDebug/reference/)。
- 依赖版本与配置以[构建文件](../app/androidApp/build.gradle.kts)为准。

```sh
./gradlew :app:androidApp:testDebugScreenshotTestDefaultTestSuite
# 仅在有意更新并审核差异时使用
./gradlew :app:androidApp:updateDebugScreenshotTestDefaultTestSuite
```

Android 与 JVM 的结果分开维护，不能互相证明另一平台渲染一致。

## Web 与交互证据

Web 验收见[计划](shadcn-web-plan.md)与[状态清单](shadcn-web-progress.md)。
截图需要绑定实际源码、构建产物、视口和主题；[历史预览图集](previews/README.md)
只用于浏览，不代表当前工作树已通过浏览器验收。

静态图之外，适用时还需验证实时主题/shape、焦点与输入方式、动画中间帧、
RTL、边界内容和辅助语义。只运行已有测试不能自动关闭尚未覆盖的要求。

## 证据保管

- 当前仍引用的取证材料见[web-parity](web-parity/README.md)。
- 旧轮次与未使用截图已移至[归档](archive/README.md)，保留原内容和路径映射。
- 组件截图、目录预览和 Web 浏览器证据用途不同；请在记录结果时注明具体任务和范围。
