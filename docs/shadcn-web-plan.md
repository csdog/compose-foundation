# Web 页面与 shadcn/ui 对齐计划

更新：2026-11-04。执行进度见 [shadcn-web-progress.md](shadcn-web-progress.md)。

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

## P0：固定比较条件

- 保存官方与本地的同尺寸截图；记录 URL、日期、主题、视口、浏览器缩放和源码版本。
- 默认展示 Base UI / Nova / Neutral / 基础圆角 10，确保预览设置与上游一致。
- 基础视口：1440×1000、1095×687、390×844；浅色与深色各一轮。
- 使用当前工作树启动本地 Web，不能拿线上旧版本冒充修改后的验收结果。
- 初载、滚动条显示/隐藏及窗口重排后，先确认Canvas的CSS原点/尺寸与根容器一致，bitmap尺寸等于CSS尺寸×DPR，再采集组件尺寸/点击证据；[根因修复与29状态回归](web-parity/viewport-scroll-r1/README.md)。

## P1：全站文档骨架

1. 字体：使用 Geist；检查 30/36 页面标题、16/24 描述、14/20 正文和字重。
2. 顶栏：64 高，正确导航、搜索、GitHub 链接、主题切换、New/Create；窄屏导航可用。
3. 侧栏：宽度、背景、分类/排序、选中样式、滚动位置、移动端关闭及键盘操作。
4. 内容：限制正文宽度、48 顶部间距、标题操作、前后页、宽屏目录和锚点导航。
5. 预览：16 圆角边框、288 最小预览高度、居中、代码底栏及 View Code；
   展开代码保留组件当前状态；长示例与手机宽度单独校验。
6. 内容层次：默认示例、Installation/Usage/Examples/API 按实际组件组织，避免重复示例。
7. 浏览器：页面标题、深链接、刷新、前进/后退、滚动恢复及加载状态。

## P2：逐组件视觉与交互

每个组件均按“官方页面与 CSS → 本地截图 → 差异分类 → 修改 → 定向验证”执行。
完整检查清单在进度文档；没有本轮证据的组件一律标为待核对。

建议顺序：

1. 基础：新增 Code Block 公开组合组件；Button、Badge、Card、Input、Label、Textarea、Separator、Skeleton、Spinner、Kbd、Avatar。
2. 表单：Checkbox、Radio Group、Switch、Toggle、Toggle Group、Slider、Input Group、Button Group、Field、Input OTP、Select、Combobox。
3. 组合与展开：Accordion、Collapsible、Tabs、Breadcrumb、Item、Empty、Progress、Alert。
4. 弹层：Tooltip、Popover、Hover Card、Dropdown Menu、Context Menu、Dialog、Alert Dialog、Sheet、Drawer、Command、Menubar、Navigation Menu、Toast。
5. 布局与数据：Table、Pagination、Scroll Area、Resizable、Carousel、Calendar、Date Picker、Chart、Sidebar、Aspect Ratio。
6. 扩展：新增 Trace 公开组合组件；Questionnaire、Message、Bubble、Attachment、Marker、Message Scroller、Shimmer、Scroll Fade；
   Chip、Text、Stepper 单独标注项目扩展，不虚构官方同名基准。

每个组件检查：默认、hover、focus、pressed、disabled、selected/checked、invalid、loading、empty，
仅适用状态需要验收；弹层另检查定位、Escape、外部点击、焦点恢复及键盘导航。
主题和 shape 动态切换需过程验证，静态深浅截图不能替代。

第三批已实现 Card 的 Nova 基础修正，以及 Code Block / Trace 公开 API 和目录入口。
第四批已修正 Input / Field / Label / Textarea 的基础 Nova 样式及默认示例，详见进度文档；
第五批已修正 Checkbox / Radio Group / Switch / Toggle 的基础 Nova 样式、默认示例和键盘缺口；
第六批已处理 Toggle Group / Slider / Input Group / Button Group 的基础 Nova 样式、默认示例及关键键盘/布局缺口；
第七批已处理 Input OTP / Select / Combobox 的默认示例、基础 Nova 状态、编辑与键盘链路；
第八批按用户指定优先处理 Collapsible / Command / Context Menu / Date Picker / Dialog / Drawer；
基础实现与回归结果见进度文档，背景模糊、焦点和复杂状态矩阵明确保留。
第九批已补齐这六项的 Web 实现缺口，相关回归与浏览器证据见进度文档；原生设备验证单独保留。
下一批回到 Separator / Skeleton / Spinner / Kbd / Avatar，以及 Field 复杂布局；
Code Block 的受限高度滚动、下载示例与 Trace 的完整工具调用组合继续列为待办。

## P3：验收与维护

- 改动先通过 JVM、JS、Wasm 编译；共享代码涉及平台能力时补对应平台检查。
- 组件改动添加有意义的尺寸/像素/交互回归；按受影响测试录制和校验截图，禁止批量刷新掩盖差异。
- 浏览器同时检查桌面与窄屏、浅深色、展开代码、导航与主题切换。
- 截图差异不能笼统归因于 Compose：字体、布局、颜色和组件行为逐项解释。
  仅抗锯齿/栅格化等无法完全消除的差异作为明确残差保留。
- 文档分别记录“已实现”“自动验证通过”“浏览器视觉验收通过”；全部满足才标完成。
- 发布前接入显式截图校验 CI；本任务不自动发布线上或提交推送。

## 完成条件

全局骨架完成、逐组件清单关闭、适用交互回归通过，且两种主题与三个基准宽度均有
修改后本地页面证据。历史 Tick、源码注释和编译成功不能单独作为视觉完成证明。

2026-11-04 执行补充：组件文档七宽度水平几何、CSS 断点及文档/浏览器滚动基础桥接见 [page-layout-r1](web-parity/page-layout-r1/README.md)。P1/P3 仍需完成垂直节奏、描述换行、目录/代码细节、完整弹层滚动锁与触摸/辅助技术矩阵；不将水平布局通过等同全页完成。


### 2026-11-04 Badge 复查增量

七个官方 docs 示例及方向/字体/颜色已补齐；组件扩展 href/invalid/图标逻辑边距，修复 Spinner 固有尺寸造成的换行。23项定向测试与最终四种显示组合截图证据见 [badge-r1](web-parity/badge-r1/README.md)。后续仍需原生链接语义、完整交互与辅助技术、共享页面和代码区收尾；保持进度清单未勾选，不把当前覆盖等同于全站完成。


### 2026-11-04 Button 增量与 checkpoint

继续复查Button，补公开逻辑图标边距、Size/Rounded/Spinner/RTL配方及Arabic行框，17项定向测试和七组四象限对照见 [button-r1](web-parity/button-r1/README.md)。As Link、完整Group菜单、Tabler图形及状态矩阵继续留在同一目标内；本轮不标记整个组件或全站完成。提交hook接入可选compact wrapper，保留全部既有门禁，以便将跨轮累积改动落为checkpoint。

### 2026-11-04 Button 第二轮与菜单依赖复查

补第十五组As Link、公开buttonStyle/invalid、Nova背景裁剪与阴影alpha，完整默认Group菜单及共享嵌套Escape。51项独立验证与两配方四象限56图见[Button r2](web-parity/button-r2/README.md)。浏览器暴露子菜单固定宽度/手机碰撞、指针高亮与根焦点环差异，继续保留；Context Menu历史勾选已撤回，需重新核对完整页面。后续按清单继续，优先尚未开始本轮页面与完整状态对照项；Editor排除、Attachment后置，不再将单个配方验证视为组件完成。

### 2026-11-04 Card 首轮与记录维护

六组官网配方、公开CardImage、Nova分区inset/完整行框、Label等字号行框及Button长单行文字已补，93项联合回归与四象限浏览器证据见[Card r1](web-parity/card-r1/README.md)。Card保留未完成：滚动条/边框占位、字重和换行、Select浏览器键盘初始焦点、共享页面/代码及完整交互矩阵。后续继续按清单推进，Editor排除、Attachment后置。

进度文档的历史记录按创建日期倒序；同日沿用原追加顺序倒序并持久化，编辑记录不改变创建顺序。使用 `python3 scripts/sort_progress_records.py` 维护，不编造历史精确时刻。

### 2026-11-04 Empty 浏览器跟进

恢复授权后完成七配方四象限及搜索/三语实际浏览器对照；修复Outline过高、RTL布局/字体/完整行框和LearnMore带URL的button角色。25项回归、两张既有Nova像素校验及JS/Wasm通过，见[Empty r2](web-parity/empty-r2/README.md)。仍留平衡换行、字体/图像采样、共享文档说明与原生HTML/辅助技术完整矩阵；继续按清单推进缺少浏览器证据的组件，不据本轮覆盖勾选全组件。

### 2026-11-04 Hover Card 浏览器跟进

四官方配方和官网三语方向已读取，本地完整方向/三语Web矩阵及连续切换/重载稳定性仍待关闭；补Trigger锚点、焦点失焦关闭、Nova窗口避让、RTL行框与窄屏Sides亚像素换行。44项回归及四张显式像素verify、JS/Wasm通过。两张Nova基准仅因5dp边距更新，Vega未改；详见[hover-card-r2](web-parity/hover-card-r2/README.md)。仍保留safe polygon、选字保护、多Trigger/嵌套宿主、全局Escape、实际翻转动画及完整辅助技术差异，清单不提前勾选。Editor排除、Attachment后置。

### 2026-11-04 Checkbox 首轮页面与状态

八个官方配方和导出代码已补，Field说明对齐/完整行框、整卡FieldLabel、Group legend布局与Table联动已修。20项定向验证、两模块lint和JS/Wasm通过，官网/本地各61张主对照已复查；旧Vega6项失配已用HEAD隔离，未刷新。列宽/滚动条、字体/换行/焦点绘制、CSS命中区及原生表单和完整状态保留，详见[checkbox-r1](web-parity/checkbox-r1/README.md)。继续优先尚未完整对照的组件；Editor排除、Attachment后置，清单不提前勾选。
