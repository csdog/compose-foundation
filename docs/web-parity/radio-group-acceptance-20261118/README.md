# Radio Group Web 验收

2026-11-18。以官方 Base/Nova、七个配方、当前工作树与独立终审为验收范围。

## 来源与配方

固定 shadcn/ui 提交 `968d8cb9dc42ae5d52e8fd356cf84596ec832a26`，归档14份 MDX、七个示例、组件、Nova/Rhea CSS、预览容器、依赖声明和主题定义。按官方依赖版本另归档4份 Base UI1.6.0 的 Radio/RadioGroup/Composite 源码。官方顺序为 Default、Description、Choice Card、Fieldset、Disabled、Invalid、RTL。Horizontal orientation、Required 与 Kotlin API 使用扩展标题。[官方文档](https://ui.shadcn.com/docs/components/base/radio-group) · [固定来源](sources.json)。

## 修复

- Nova 圆圈在16dp布局之外按源码扩展至40×32dp点击区域。官网与本地的圆圈外8px点击均可选中。主题显式触摸最小尺寸继续控制布局，禁用项保持不可选。
- Nova 默认采用源码静止的按压行为，显式动画参数可选择原有动画，其他样式预设默认保留 Scale。
- 方向键在执行导航前切换键盘模式，使鼠标选中之后继续按方向键时显示焦点环。回归使用独立 InputModeManager，从 Touch 开始，验证选中项、焦点和模式。
- Choice Card 的外层 FieldLabel 显示键盘焦点环，内部 radio 按官方规则取消第二层环，单独使用的 radio 继续显示自己的环。
- Description、Disabled、RTL 按官方 `w-fit` 使用内容宽度。
- Fieldset、Disabled、Invalid 的标签按源码使用正常字重。新增接受必需 TextStyle 参数的 FieldLabel 重载，并复用 LabelImpl 的文字渲染，保留既有公共重载。实际 TextLayoutResult 字重与既有 Label、Field 回归共同核对。
- 九个配方代码均由实际预览内容生成，RTL 包含三种语言数据和方向，语言参数对应预览工具栏的选择。Usage 使用实际可访问名称、标签激活和 roving focus 回调。
- 官方 API Reference 链接位于 RTL 后。Kotlin 参数表放入明确标注的扩展指南。四张旧基准经过尺寸、像素和重复输出哈希核对后刷新，旧图与差异保留。

## 验证

- 当前源码58项聚焦及共享 JVM 回归、十份独立编译的配方/Usage、八张截图比较和所属 lint 通过。Selection、Label 与 Field 卡片回归共同覆盖共享影响。
- JS49/Wasm45文件和Desktop编译通过，构建前后输入完全一致。453条实际浏览器响应逐字节匹配磁盘产物，缓存与service worker绕过已记录。
- 八组运行时、视口、浅深色矩阵覆盖72个配方位置，保存72张矩阵图。四组键盘/卡片/禁用/错误交互、四组三语/实时主题/弹层、四组路由/真实剪贴板和四组按压检查通过。
- 六组点击区域测试包括四种指针视口的圆圈边缘激活和两种真实触摸上下文，默认主题的48dp最小尺寸与正常选中经过实测。
- Description、Disabled、RTL在官网与本地的圆圈横向位置误差不超过2px，行距差不超过1px。源图、裁剪区域和SHA保存在几何比较清单中。

## 适用范围与边界

- 固定视口、指针配置采用 Nova/Neutral。官网live演示的主题覆盖另存原始参数，B1颜色比较使用固定提交themes.ts中的Neutral变量作为独立官方参考输入，角色值逐项断言，原始live截图继续保留。另有真实触摸上下文验证主题按设备派生的48dp最小布局和选中行为。主题公开的 minTouchTarget 是 Kotlin 配置项，16dp可见圆圈位于其内部。
- Chromium 导出 radio、选中、禁用、错误和实际标签名称。Compose 的 selectableGroup 与 roving registry 提供单选组和单 Tab 入口。浏览器没有原生 radiogroup/fieldset 角色，实际 DOM 焦点仍是 Canvas 代理，系统屏幕阅读器朗读未经验证。
- HTML name、隐藏 input、FormData、原生表单校验、inputRef、DOM render 替换属于上游 DOM 接口。Kotlin 使用 value/defaultValue、onValueChange、required、disabled、contentDescription 和组合内容承接支持的状态与行为。
- Home/End 是保留的 Kotlin 扩展行为，Base UI1.6.0 的 RadioGroup 明确关闭了对应功能。受控拒绝变更、键控重排与 RTL 导航有实际 Compose JVM 回归，页面的现有配方用于浏览器操作。
- 实际浏览器验证三语布局、语言菜单、翻译说明弹层的 Escape/外部关闭，以及实时主题切换后的选中状态。圆形为源码固定形状，官方配方没有独立 shape 切换器。外层卡片radius实时更新与焦点外观另有JVM像素回归。
- Desktop 提供编译结果。Android/iOS 运行和操作系统 AT 未执行。小数 CSS 行高与整数 Compose 放置产生的亚像素差异保留在几何记录中。圆形边缘和字形的栅格差异另保留裁剪与像素统计，固定 Neutral 的实色角色已核对。

## 证据

[验收凭证](../../../.agent-runs/radio-group/goal-20261118/acceptance-evidence.json) · [独立终审](independent-review.md) · [交接](../../../.agent-runs/radio-group/progress.md)。
