# Empty 浏览器跟进（2026-11-04）

沿用已授权的专用验收空间1，检查[官网 Base UI Empty](https://ui.shadcn.com/docs/components/base/empty)和本地/empty。七个实际配方及Nova源码沿用[固定上游来源](../empty-r1/sources.json)，本轮另读取实时DOM、尺寸、字体、角色和交互，不把历史勾选作为验收依据。

## 发现与修改

- Outline原先把384dp预览最小高施加到Empty自身，导致虚线框撑高。现在外层预览负责384dp和桌面40/窄屏24dp inset，Empty本身保持自然高度，仍由公开组件绘制边框。修改前四张画面在[before](before)，修改后见browser。
- RTL原先套通用24dp wrapper且Empty至少288dp，实际内容超出官网的布局。改为64dp工具栏+288dp正文，正文40dp inset、内容自然高度；窄屏按钮保留自然单行并以整体居中。选中语言使用已打包Noto Arabic/Hebrew，普通语言继承Geist；导出代码包含主题/字体设置和公共资源引用。
- Nova EmptyTitle按实际官网div去掉额外heading语义，Vega保留原行为。标题/描述使用Tight+Trim.None完整行框，避免Arabic安全leading撑高；四种显示组合实际Arabic标题20dp已断言。两张既有Nova像素基准保持一致。
- Learn More使用公开buttonStyle绘制，单个URL annotation拥有键盘焦点，外围扩展到内边距/图标的指针区域；pointer和Enter只触发一次。**官网实际为 `<a href="#" role="button">`**，本地保留button角色及语义点击动作，同时保留URL annotation。不是将官网角色误判为普通链接。Small图标14dp、gap4，RTL inline-end减少逻辑end inset到6dp；父区域hover时下划线覆盖文字。
- Contact support的#链接接入目录导航，保留默认UriHandler用于独立示例/测试。两种链接的导出代码使用公共API与UriHandler，不依赖私有目录导航。
- 公共版本与安装说明为0.3.34。

## 自动验证

最终XML在[tests](tests)，**25项通过**：核心EmptyParityTest7（包括两张Nova显式像素verify）、原有目录6、新增四种显示组合12。测试包括Outline宽度/自然高度、三次Tab+Enter、文字/内边距各单次激活、URL annotation与button角色、RTL工具栏/正文边界及Arabic实际行框；既有头像/灰度/重叠、输入编辑/清空、支持链接与语言/动作顺序继续通过。没有声称原生测试覆盖Space。

core/shared ktlint和JS/Wasm development distribution通过。最终应用 `7f97a9c3d53c9282b288.wasm`，43,288,817字节，SHA256 `d37503b57f0dc409892bb24697b75cf2a7d31a0ee59e7e02768834368108cc7c`；浏览器实际加载版本见local-matrix.json，11个最终构建输入见[source-inputs.json](source-inputs.json)。构建仅保留既有Text INVISIBLE_REFERENCE、AGP/Gradle和webpack os/path诊断；默认detekt不覆盖commonMain的限制继续保留。

未修改或批量录制截图基准。历史EmptyScreenshotTest十二changed/四missing为上一轮已经隔离复现的记录，本轮未重验其全套，不能据本次两张Nova通过声称该旧矩阵已通过。

## 浏览器证据

[browser](browser)记录1440/390×1000、DPR1、mouse、mobile:false，浅/深各一组。390是窄屏模拟，不是触摸真机。官网/本地各**48张**：七组静态28、搜索编辑/清空8、三种语言12。24张双方对照拼图已逐组目视复查；截图保留各页真实滚动位置，未强行对齐不同描述导致的绝对Y。语言菜单对齐当前选中项，窄屏浅色Hebrew初次自动化误选English，已单独按当前菜单截图重新选择并复查，错误结果不计为通过。

搜索的Buttons文字、清空后placeholder和聚焦环均已查看；四种显示组合Contact support实际导航到#且scrollY=0，JSON保存结果。另实测窄屏浅色Hebrew Learn More的内边距点击和焦点保留后的Enter导航，结果在local-learn-more-interactions.json。官网真实聚焦的Arabic Learn More：Enter跳#并回页首，Space仅滚动页面；不把这个官网观察说成本地Space全矩阵通过。

头像观感复查还确认：当前官网https://github.com/shadcn.png新下载内容与本地avatar_shadcn.jpg **逐字节一致**，31,007字节、SHA256 `513d06f542692036ff0bb17938f04b3efd60567ba2cae08049469d21f18e0524`。没有因为目视采样差异替换图片或改Avatar裁切；官网已加载缓存图与本地缩放/滤镜的完整采样对照仍待关闭。

## 未关闭的差异

- `text-balance`/`text-pretty`尚未实现：桌面Default、Outline、Avatar及InputGroup、窄屏Avatar Group/RTL等仍有不同换行，不能仅归因于抗锯齿。
- 深色文字观感偏粗、部分图形/阴影/ring及圆角/虚线细节仍需按实际字重和绘制逐项核对；同URL头像的缓存/缩放采样差异也保留。
- 章节说明仍有部分缩写和缺少官网内联代码/RTL指南链接；共享页面标题/垂直节奏、代码底栏和API表排版不同。不能将组件外框修复等同于整页像素一致。
- URL annotation不自动提供原生HTML锚点的状态栏、新标签页或浏览器上下文菜单；读屏、完整键盘/hover/focus/主题动态切换、触摸设备仍待补。
- 任意Style/shape覆盖、极端约束和大内容，以及历史Vega截图矩阵未关闭；未作本轮全平台或全部状态通过的声明。

Empty保持未勾选。Editor排除，Attachment仍后置。下一项继续复核清单中缺少浏览器证据的组件，同时保留这里的明确缺口。
