# shadcn 组件开发与验收 SOP

适用于新增组件，以及修改样式、布局、动画、焦点和输入行为。一次验收应能从上游参考复现到本地结果，并能说明哪些状态和平台已经验证。

## 1. 确认参考和验收范围

1. 记录组件名、registry slug、变体、主题、目标平台和要验证的交互状态。默认使用 shadcn Base UI 与 Nova。记录上游 Git commit、示例路径、当前主题和状态。
2. 在 [vendor 说明](../vendor/README.md) 指定的 checkout 中，打开 `apps/v4/registry/bases/base/ui/<slug>.tsx`，再读 `apps/v4/examples/base/<slug>-demo.tsx` 或对应示例，以及 `apps/v4/registry/styles/style-nova.css` 或当前样式文件。核对 slot、依赖的 primitive、容器色、圆角、尺寸、状态选择器、进出场动画、键盘与关闭策略。可在 [shadcn Base 组件目录](https://ui.shadcn.com/docs/components/base) 运行同一示例。记录 checkout 的 `git -C vendor/shadcn-ui rev-parse HEAD`。
3. 有 Figma 设计时，记录文件与 frame 链接、版本或日期、component variant、主题和画板宽度。分别核对 auto layout、间距、字号与行高、颜色 token、边框、圆角和阴影，再查看状态与 prototype。以任务中明确批准的 Figma 差异为准，为改动注明覆盖了哪条上游默认值。遇到未确认的冲突，保留参考并标注待确认。没有设计稿时以固定版本的上游源码和示例为依据。
4. 对照前固定内容、字体、图标、语言、背景、视口、主题和输入方式。记下 Web 的 CSS px、缩放和设备像素比，以及 Android dp、Apple pt 和各自屏幕 scale。按逻辑尺寸裁切和对齐。保存原图，以便单独复核字体抗锯齿、平台原生控件、系统栏或滚动指示器的差异。

## 2. 设计组件与 slot

| 结构 | 实现要求 |
| --- | --- |
| Root | 管理展开、选择或拖动状态。为需要由调用端控制的状态提供值和回调。保证初始值、关闭、重开、禁用和动态数据变化行为清楚。 |
| Trigger | 适用于有展开或弹层行为的组件，独立负责触发、定位锚点、展开语义和关闭后的焦点归还。允许传入自定义按钮或标签，保留修饰符、自定义内容和可访问名称。 |
| Content | 负责内容布局、对齐、碰撞或翻转、焦点和滚动。弹层按行为明确外点、Escape、系统返回、点击选择和窗口失焦时的关闭策略。 |
| Item、Group、Label、Separator 等 | 按上游实际组合暴露所需 slot。每项支持适用的禁用、选中、分组标题、键盘跳过和辅助技术朗读。 |

独立 Button 等没有展开行为的控件直接暴露动作、样式与内容。Popover、Dialog、Menu、Select 优先保留 Root、Trigger、Content 的组合边界。复用现有 popup、焦点、主题和手势原语；新增共享能力前先检查已有调用。尽量维持已有签名、默认值和平台最低版本。

- Compose Kotlin 组件放在 `shadcn/shared/src/commonMain`，平台桥接放对应 source set，目录示例放在 `app/shared`。
- 原生 SwiftUI 放在 `shadcn/iosApp/Sources/ComposeFoundationSwiftUI`，验收 fixture 放在同模块的 `validation`。已有可用的原生语义和系统行为可以复用。
- 组件源码需单独安装时，更新 registry 的 Swift 支持文件或 Kotlin 依赖闭包。运行生成器，核对 slug、链接、安装路径和依赖文件。新增 catalog 条目要有可打开并可交互的示例。

## 3. 测试矩阵

| 类型 | 做法与验收 |
| --- | --- |
| 行为与语义 | 从真实入口打开组件。核对可访问名称、角色、状态、焦点顺序、禁用项跳过、回调次数及关闭后的焦点位置。测试典型内容、空值、长内容、窄屏溢出、RTL、系统字体缩放，以及同一实例实时切换主题。 |
| 像素回归 | 固定渲染环境和比较区域，覆盖相关状态，包括浅色、深色、焦点、按压、禁用、展开、选择、错误和加载。用已提交的基准比较当前输出，检查 expected、actual、diff。布局、边框、容器色和焦点环可直接像素核对。若现有测试允许容差，记录区域、阈值及原因。跨平台比较先核对逻辑尺寸、布局和颜色，平台文本差异单独记录。 |
| 动画 | 采集触发前、动画起点、中间帧、终点及稳定后的画面。Compose 对确定性动画使用 `mainClock.autoAdvance = false` 和 `advanceTimeBy`，也要测反向、取消、中断和减少动态效果。录屏比较以相同输入事件或明确的可见状态变化作为时间零点，保留原始帧时间。核对位移、透明度、持续时间、遮挡、状态回调以及手势期间的连续性。 |
| 移动端手势 | 在 Android 模拟器和适用的 iOS 模拟器验证真实触屏输入。根据组件覆盖短按、长按、横滑、纵向滚动、拖动、速度阈值、超出边界、取消、嵌套滚动、外点、系统返回和键盘弹出。检查窄屏、边缘锚点、系统安全区、RTL、键盘焦点与 TalkBack 或 VoiceOver。记录设备、系统、方向、按压时长和位移。 |
| 桌面与 Web | 根据组件验证鼠标悬停或移出、按键、Tab 和 Shift+Tab、方向键、Escape、焦点恢复、重新缩放，以及触控与鼠标入口差异。静态渲染只证明当前取样状态。 |

对拖动、碰撞和滚动定位，分别验证边界计算、给定状态的渲染和真实指针轨迹。只有产品确实支持的状态才需要相应测试，对不适用项写明原因。视觉变更先确认参考和变更范围，再更新相应基准。

## 4. 从仓库根目录运行

### 跨平台视觉回归试点

GitHub Actions 的 **Component parity** 在相关 PR 上采集 Button、Tabs、Popover 的初始状态和深浅色截图。CMP Web、CMP Android、SwiftUI iOS 与 SwiftUI macOS 使用各自的 Catalog。CMP iOS、CMP Desktop 和独立 Jetpack Compose 的平台执行入口待接入，报告会标为待验。报告同时收录 CMP JVM 的 Tabs 和 Popover 行为测试，以及 SwiftUI 的 Tabs 导航逻辑测试。设备交互、无障碍和动画仍需按下文的方法单独验收。

[`shadcn/validation/plan.json`](../shadcn/validation/plan.json) 记录每个组件的 shadcn Base UI / Nova 源码版本和示例。首次运行会采集两遍，按像素核对复现性。对照上游示例审核实际图，检查内容、排版、主题和各平台状态，并在 PR 上说明有意保留的平台差异。初始截图只覆盖当前 fixture 的状态。例如 Popover 展开态要单独验收。

审核通过的实际图提交到独立的 `csdog/compose-foundation-baselines` 仓库，路径格式为 `<platform>/<slug>/initial/<light|dark>.png`。基准提交应记录用于采集的源码提交、上游示例、渲染环境和审核记录。随后在主仓库更新 `plan.json` 中的 `baseline.revision` 为完整提交 SHA。私有基准仓库需要 Actions 能读它的 `COMPONENT_BASELINES_READ_TOKEN`。外部贡献者的 PR 无法读取该 secret，基准需要在允许读取它的工作流环境复验。

Actions 的 **Summary** 显示逐平台和主题的结果。附件提供实际图、第二次采集、已锁定基准、差异图和采集日志。缺基准的稳定截图显示待审核。确认基准发布后，可手动运行并启用 `require_baselines` 检查自动采集用例是否都有基准。

### Compose JVM 与 Android Preview 像素基线

```sh
./gradlew :shadcn:core:jvmTest --tests '*TableParityTest'
./gradlew :shadcn:core:verifyRoborazziJvm
./gradlew :app:androidApp:testScreenshotTestDefaultDebugTestSuite
```

确认视觉变化后，更新相应基准，复查新增、修改和删除的 PNG，再重跑校验。

```sh
./gradlew :shadcn:core:recordRoborazziJvm
./gradlew :shadcn:core:verifyRoborazziJvm
./gradlew :app:androidApp:updateScreenshotTestDefaultDebugTestSuite
./gradlew :app:androidApp:testScreenshotTestDefaultDebugTestSuite
```

### Android 模拟器

先启动专用模拟器，让 `ANDROID_SDK_ROOT` 指向本机 SDK，并将 `android_device` 设为 `adb devices` 显示的序列号。将下面的 `button` 换成待验组件，批量静态截图时去掉 `--only`。

```sh
audit=build/shadcn-audit/button
adb_bin="$ANDROID_SDK_ROOT/platform-tools/adb"
python3 shadcn/registry/build.py --output "$audit/registry"
./gradlew :app:androidApp:assembleDebug
"$adb_bin" -s "$android_device" install -r -t shadcn/androidApp/build/outputs/apk/debug/androidApp-debug.apk
python3 shadcn/androidApp/validation/verify_android_catalog.py \
  --adb "$adb_bin" --device "$android_device" --registry "$audit/registry/android/registry.json" \
  --output "$audit/android/catalog" --only button
python3 shadcn/androidApp/validation/verify_android_interactions.py \
  --adb "$adb_bin" --device "$android_device" --output "$audit/android/interaction" --only popover table-keys
```

已启用 TalkBack 的专用模拟器可以用 `verify_android_talkback.py` 运行真实手势并保存截图。完整选项通过 `--help` 查看。可复用的路由和判定条件见 [Android 逐项验收](android-registry-audit.md)。

### SwiftUI 静态截图

以下命令共用同一份 fixture。macOS 直接导出 PNG。iOS 编译独立测试应用，在已启动的模拟器中运行。把 `ios_device` 设为目标模拟器 UDID，只有一个设备启动时也可用 `booted`。

```sh
audit=build/shadcn-audit/swiftui
swiftui=shadcn/iosApp
python3 shadcn/registry/build.py --output "$audit/registry"
swift build --package-path "$swiftui"

xcrun swiftc -parse-as-library -swift-version 6 \
  "$swiftui"/Sources/ComposeFoundationSwiftUI/*.swift \
  "$swiftui"/validation/{AuditFixtures,RenderAudit}.swift -o "$audit/macos-render-audit"
"$audit/macos-render-audit" "$audit/registry/swiftui/registry.json" "$audit/macos"

audit_app="$audit/ComposeFoundationAudit.app"
bundle_id=com.toutiaoyan.compose.foundation.audit
sim_sdk="$(xcrun --sdk iphonesimulator --show-sdk-path)"
mkdir -p "$audit_app" "$audit/ios"
cp "$swiftui/validation/IOSAuditInfo.plist" "$audit_app/Info.plist"
cp "$audit/registry/swiftui/registry.json" "$audit_app/registry.json"
xcrun --sdk iphonesimulator swiftc -sdk "$sim_sdk" \
  -parse-as-library -swift-version 6 -target "$(uname -m)-apple-ios14.0-simulator" \
  "$swiftui"/Sources/ComposeFoundationSwiftUI/*.swift \
  "$swiftui"/validation/{AuditFixtures,IOSAuditApp}.swift -o "$audit_app/ComposeFoundationAudit"
xcrun simctl install "$ios_device" "$audit_app"
xcrun simctl launch --terminate-running-process "$ios_device" "$bundle_id"
```

等新一轮输出目录中的 `result.txt` 出现 `PASS`，再复制本轮结果。重复运行时核对摘要更新时间。复验弹层或菜单展开后状态时，使用 [SwiftUI 逐项验收](swiftui-parity-audit.md) 中的对应启动参数和验证脚本。

```sh
sim_data="$(xcrun simctl get_app_container "$ios_device" "$bundle_id" data)"
cp "$sim_data"/Documents/ParityAudit/*.png "$sim_data/Documents/ParityAudit/result.txt" "$audit/ios/"
```

### 动画逐帧对照

用测试框架的可控时钟生成确定性截图。真实手势的 SwiftUI 实录可以用 `xcrun simctl io "$ios_device" recordVideo "$audit/ios/gesture.mov"` 开始录屏，操作后按 Ctrl+C 停止。在同一操作路径采集 Web 基准。若使用外部输入驱动，记录实际注入时刻，保留按住和松手两段。以下命令逐帧导出并保存时间轴。Android 可以用 `adb shell screenrecord` 加 `adb pull` 取得同类实录。

```sh
mkdir -p "$audit/ios/frames"
ffmpeg -i "$audit/ios/gesture.mov" -fps_mode passthrough "$audit/ios/frames/%05d.png"
ffprobe -v error -select_streams v:0 -show_frames -show_entries frame=best_effort_timestamp_time \
  -of csv=p=0 "$audit/ios/gesture.mov" > "$audit/ios/frame-times.csv"
```

## 5. 图像与验收结论存放

| 内容 | 位置 |
| --- | --- |
| Compose JVM 测试源码与提交基准 | `shadcn/shared/src/jvmTest/`，基准放 `shadcn/shared/src/jvmTest/snapshots/` |
| Android Preview 测试源码与提交基准 | `shadcn/androidApp/src/screenshotTest/`，基准放 `shadcn/androidApp/src/screenshotTestDefaultDebug/reference/` |
| 工具生成的结果与差异报告 | 对应 Gradle 模块的 `build/reports/` |
| 本轮参考原图、实际截图、diff、视频、帧与无障碍树 | `build/shadcn-audit/<slug-or-run>/{reference,web,android,ios,macos}/`，按主题、变体、状态命名 |
| 可追溯的验收结论 | 更新 [Android 审计](android-registry-audit.md) 或 [SwiftUI 审计](swiftui-parity-audit.md)，写清命令、设备和系统、参考版本、已验证的状态及待验项 |

仓库提交可复现的测试和经审核的像素基准。录屏、模拟器临时截图、批量报告，以及带访问限制的 Figma 文件留在适用的本地产物目录或 CI artifact。
