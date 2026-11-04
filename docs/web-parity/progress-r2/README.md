# Progress：页面、RTL与浏览器交互复查

2026-11-04，对照[官网 Progress](https://ui.shadcn.com/docs/components/base/progress)。固定上游 `96da9cc7f1aa10580a3741664e2a77c721206c00` 的文档、组件、四配方及 Nova 样式、Base UI Value 共八份源码重新请求并逐一核对摘要，见 [sources.json](sources.json) 和 [source](source)。当前官网实际 DOM/尺寸/字体/150ms cubic-bezier 过渡保存在 [official-matrix](browser/official-matrix.json)，不凭过往测试继承浏览器完成状态。

后续：[滚动条触发画布重测](../viewport-scroll-r1/README.md)已修复本轮发现的初载缩放根因，原始诊断与本轮边界仍保留。

## 本轮修改

- 页面仅保留 Default、Label、Controlled、RTL 四个官网配方，删除末尾十二个重复参数预览；Kotlin 的 fraction/max、indeterminate、自定义值说明和独立 Track/Indicator 能力合并到指南及实际 API 表，不删除公共能力。
- 补 Composition、With label and value 层级/树状结构，补 Label、Controlled、RTL 行为说明及 Usage 的完整布局 imports。Kotlin API 与 HTML min/null/format/render props 的适配范围直接说明。
- RTL 改用单一 288dp 正文和 40dp inset，语言工具栏保持独立，修复旧 RtlPreview 在手机自然高度/24dp inset及外层居中的偏差。Arabic/Hebrew 使用项目已公开的 Noto 字体资源，保留主题/preset/spacing 与逻辑方向；标签和数字跟随语言更新。
- 四配方完整复制代码从编译源生成，补调用入口和公共字体资源，去除目录 helper 与 testTag。生成器对格式化后的源码幂等。未改公共 Progress 实现或版本；新字体下现有公共 20sp 行框/36dp 根高度无需额外核心修复。

## 自动验证

41 项常规测试通过：核心行为9、Nova截图入口4、既有目录16、新增页面/三语布局12。新增布局验证使用实际预览 padding：桌面384dp、窄屏245dp进度宽，RTL正文288dp、三语根36dp/文字20dp行框，以及标签/数字的逻辑左右关系。Default 13→500ms后66、Controlled鼠标及键盘已有定向测试保留。

显式像素校验独立执行：Nova四张全部通过，旧Vega四张仍失配；四份actual与r1的current记录逐字节相同。见 [legacy-differences](legacy-differences)，golden没有刷新。常规截图入口通过不代替显式verify。

目录ktlint通过；第一次format/check暴露长行及参数断行，修正后再验通过。JS/Wasm development distributions退出0；既有webpack os/path诊断和warning仍保留。最终主应用 `2d1272c1f071676161fc.wasm`，43,567,384字节，SHA256 `b57c44a1cd46b1a52cf3143ee4e3ce753961c6cd9fa09359e883225cff2cc3fd`。五项输入构建前后摘要相同，见 [input-hashes-after](input-hashes-after.json)。默认detekt未分析commonMain的历史限制继续适用。

## 浏览器证据及边界

沿用授权专用空间1，1440/390×1000、DPR1、mouse、mobile:false。390是窄视口鼠标模拟，不是真机触屏。官网与最终本地各44张主图：四配方×四显示组合16静态，另各28张指针/键盘/三语图。12张最终对照拼图全部目视复查，见 [review-sheets](review-sheets)。[before](before)保留修改前16图。

- 官网键盘实测Home=0、End=100、Left=99；本地相同顺序的真实按键使可见进度填充达到0/100/约99%。本地点在轨道四分之一附近，填充约24%，可见采样坐标和像素数见 [local-fill-samples](browser/local-fill-samples.json)。这不是浏览器语义树中的精确值断言；native测试另检查range语义。
- 官网四分之一轨道点击未改变当前值，截图明确命名 `pointer-quarter-attempt`，不计指针对齐通过。一次重载后进一步记录trusted=true、isPrimary=true、buttons=1的pointerdown，命中实际slider-range，值仍为50；[诊断](browser/official-pointer-diagnostic.json)保留，不猜测根因或据此削弱本地可用交互。真实拖动/完整Slider焦点及修饰键矩阵仍未关闭。
- 三语实际菜单选择均完成，Arabic数字/左右方向随语言更新，官方DOM记录与本地图像分开验证。初始Arabic静态图经更长稳定等待重拍；不把失败坐标或空白帧当作通过。
- 观察到初载/快速视口切换的共享渲染问题：根容器1425×1000时，其子表面曾为1425×989.578、top=5.211；390时曾为375×961.531、top=19.234。还出现过顶部大块空白偏移和一次RTL空白帧。真实重排/导航并等待后可恢复，但不能据最终图宣布初载/resize路径已修复。原图及阶段几何保存在 [diagnostics-initial-layout](diagnostics-initial-layout)、[local-actions](browser/local-actions.json)。滚动条改变可用宽度可能相关，这是推断，尚未证明根因。
- 桌面深色一次250ms截图仍处于宽度过渡，保留诊断帧；等待1000ms重拍后约24%，未拿中间帧冒充最终宽度，也未因此宣布150ms真实浏览器时序/性能完全通过。

## 继续保留的差异

共享初载/resize、标题/说明行框与换行、代码面板、inline-code外观和Composition说明/代码顺序仍有区别；本轮补齐内容不等于这些共享排版全通过。官网RTL说明链接与本地切换说明也仍不同。

Native HTML progressbar命名关联、min/null/format/render props、完整辅助技术/触屏，过渡中断/反向/reduced motion/颜色、任意长文本/复杂slot、root显式高度与自定义Track shape等沿用r1开放范围。Kotlin默认max=1及indeterminate显式布尔是已说明的API适配。四张旧Vega差异保持开放。

Progress继续未勾选；Editor排除、Attachment后置。整站复核目标仍进行中。
