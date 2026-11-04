# Marker：页面、自然高度与交互复查

2026-11-04，对照[官方Marker](https://ui.shadcn.com/docs/components/base/marker)。固定上游 `96da9cc7f1aa10580a3741664e2a77c721206c00` 的文档、组件和八配方见[source](source)，URL/摘要见[sources.json](sources.json)，当前DOM元素、状态、链接和尺寸见[live-dom.json](live-dom.json)。官网使用base-rhea；r1已核实Marker的Nova/Rhea样式相同，本轮继续按实际页面和DOM复查，没有切换整个目录主题。

## 本轮修改

- 复核Default、Variants、Status、Shimmer、Separator、Border、With Icon、Links and Buttons八配方，补Features、六组行为说明及Accessibility。明确状态更新、装饰图标和交互链接的Compose适配，不把liveRegion称为HTML role=status。
- 官网八个预览都是h-auto。移除目录默认288dp最小高度，两行Status/Shimmer/Links示例随内容自然展开，保留40dp外层inset和配方原有48dp垂直padding。
- 链接由打开外部官网改为本页`#links-and-buttons`。真实URL annotation负责链接键盘/语义，周围Marker扩展指针目标到图标与空白区域；焦点转给唯一链接，点击不重复激活。Web目录通过现有DocNavigation桥接，公共复制代码通过UriHandler保留相同href。
- 链接和按钮hover时，文字及图标都从mutedForeground变为foreground。按钮保留button角色和键盘行为，触发真实Toast消息，移除不属于官网演示的点击波纹。公共Marker仍是可组合的呈现容器，未新增核心参数或更改发布版本。
- Links示例与完整可复制代码从同一编译源生成；生成器去掉目录内部导航和测试标签，保留回调、URL、图标与Toast依赖，输出与格式化后的构建输入一致。

## 自动验证

[常规测试](tests)30项通过：核心13、既有目录5、新增四种显示组合交互12。新增覆盖URL annotation、本地链接、整行空白点击和Enter各只激活一次、按钮键盘反馈，以及hover文字色和真实图标墨迹对比；文字span和装饰图标点击也各只激活一次。浅色图标断言最初把透明像素当黑色参与对比，已改为先与背景合成；未放宽阈值，初次RED留在tests/red。

七张既有基准显式verify中六张通过，旧marker_separator_dark仍有906像素变化（1024×64），actual与r1记录逐字节相同。[legacy-differences](legacy-differences)保留失败XML、actual/compare与比较摘要，没有刷新基准。普通任务通过与显式像素结果分开记录。

目录ktlint及JS/Wasm development构建通过。最终主应用 `3337221b4d395fb838fd.wasm`，43,584,607字节，SHA256 `a2cc8adb755838f2a34d9178b821ae6194c8b5d10d320205cba4869b6828d056`。输入/验证摘要见[validation.json](validation.json)。保留既有AGP/Gradle/Text及webpack os/path诊断；默认detekt未覆盖commonMain的限制仍存在。

## 浏览器

沿用授权专用空间1，1440/390×1000、DPR1、mouse、mobile:false，390是窄屏鼠标模拟。官网/最终本地各32静态及20交互图，包含link hover、button hover、鼠标Toast、Enter Toast、链接结果。旋转和Shimmer没有冻结，任意采样相位不用于声称逐像素一致。[before](before)保存修改前本地32图；最终对照在[browser](browser)，拼图在[review-sheets](review-sheets)，16张最终拼图已全部目视复查。

第一批链接结果用history替换设置了旧hash而没有自然滚动，不作为真实用户路径的充分证据，保存在[diagnostics-history-setup](diagnostics-history-setup)。最终使用真实滚轮从With Icon带着原hash滚到链接，记录点击前/后的URL；滚轮实际位移与请求delta不一致，按实际scrollY缩小步长，轨迹保留，不将它当作滚动桥接完全通过的证据。

## 明确剩余差异

- 共享Toaster仍按示例父布局绘制，显示在预览内部；官网Sonner反馈位于窗口顶部。关闭按钮、宽度、持续时间、布局及堆叠/手势等也不能据消息出现就视为对齐，移交Toast专轮；没有用私有弹层掩盖公共Toaster问题。
- 官网原生链接/按钮的focus outline、Space/修饰键/新标签页等完整原生能力、颜色过渡与指针/选字过程尚未关闭；URL annotation不是浏览器真实anchor DOM。本轮证明Enter与整行指针行为，不扩张为完整辅助技术通过。
- Variants表格、额外代码面板、Accessibility各小节与共享页面节奏/代码栏仍不同；字形/换行、Shimmer/Spinner完整动态、长内容/任意复杂slot及动态Style矩阵仍开放。
- 旧暗色分隔线差异保留；真实触屏/读屏未验。Marker保持未勾选，Editor排除、Attachment后置，整站目标未完成。
