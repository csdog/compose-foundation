# Checkbox 首轮完整配方对照（2026-11-04）

本轮优先选择尚未做完整页面对照的Checkbox；Editor排除，Attachment继续后置。沿用用户授权的专用验收空间1，读取[官网Base UI Checkbox](https://ui.shadcn.com/docs/components/base/checkbox)及固定上游`96da9cc7f1aa10580a3741664e2a77c721206c00`的组件、Field、Nova样式与八个配方；[来源及哈希](sources.json)、原文在source。截图和实际DOM优先于旧文档推断。

## 发现与实现

- 原目录只有一项Default及大量“Default disabled/invalid/required”等反向状态重复，缺少官网完整默认组合、Group、Table和三语RTL。现按官网顺序组织Default、Invalid State、Basic、Description、Disabled、Group、Table、RTL，恢复Checked State说明及公共Kotlin导出代码。
- Default现在是四个独立控件：普通条款、默认已选且带说明的条款、禁用通知、整卡通知。说明和禁用配方分别使用288dp/224dp宽度，Group初始只选前两项。
- 横向Field原先将Checkbox居中在整个说明段落旁，回归中距正文顶部21dp。Nova现在在测量阶段识别FieldContent和Checkbox，按真实触发控件宽度为正文留出空间，正文顶对齐、Checkbox仅下移1dp；普通单行仍居中，RTL使用逻辑起点。Vega继续原Row路径。没有通过示例padding掩盖根布局缺陷。
- DOM与固定Field源码均为`mt-px`，不是最初误读的2dp。官网FieldDescription为14/21，FieldTitle为14/20；Nova指定这些行高并使用Tight+Trim.None，保留实际行框。FieldLegend标签变体也保留完整20dp行框，解决原18dp的裁切。
- 增加公共FieldLabel可组合整卡重载：调用者提供内容、选择状态与激活动作，组件负责边框、选中/hover底色和键盘焦点环。点击内边距或内部控件各只触发一次，标签回调负责聚焦所标记控件。Nova卡片内部Checkbox不重复绘制焦点环；鼠标聚焦不画键盘环。已选卡片hover底色优先，官网实测为muted/50，见[样式证据](card-hover-style.json)。
- FieldGroup增加可选spacing参数，官网Group使用12dp。公共FieldSet带标签legend的重载将legend置于正文flex gap之外，避免多出16dp；示例按自然宽度居中。默认legend及Vega原路径保持，不能据本轮宣称Field所有任意槽位布局已完成。
- Table使用官网四个人物及初始Sarah选中状态，支持全选和逐行切换。**部分选中时官网表头仍未选中**，本地没有擅自改成半选。
- RTL使用完整语言标签、已打包Noto Arabic/Hebrew和逻辑布局；切换语言保留各控件状态。Kotlin导出使用公共组件/字体资源，不依赖私有目录工具栏。
- 公共版本与安装说明为0.3.36。

## 自动验证

[最终测试XML](tests)共20项通过：核心CheckboxParityTest5、目录Examples3、Viewport12。涵盖说明布局/16dp控件、纯横向RTL居中、Space/受控拒绝、禁用祖先、整卡内边距与控件各单次激活和焦点、八配方/四控件、全选与逐行独立、四种显示组合下42px说明行框、Group间距/legend完整行框及三语选择状态保留。RED记录在tests/red，后续Group行框RED记录单独保留。

两模块ktlint与JS/Wasm development distribution通过。最终应用`c1c6449440a65ce996e1.wasm`，43,484,569字节，SHA256 `529bc0c5fba200d16d2d6316d9fc82386a31a5d7f5450789a7371c0f0f038ea5`；源码输入和检查汇总分别在source-inputs.json、validation.json。保留既有Text INVISIBLE_REFERENCE、AGP/Gradle与webpack os/path诊断；默认detekt的commonMain sourceCount=0限制不作全覆盖声明。

### 旧截图隔离

未录制或刷新任何既有基准。显式verify的旧Vega八图中，Checkbox浅色两图通过，深色focused变化52像素、states变化88像素；Field四图尺寸失配（profile_form实际368×344，旧图368×368；states实际1024×269，旧图1024×289）。

把Field.kt和Checkbox.kt暂时恢复HEAD后，8图结果完全重现；6组actual/compare共12张PNG与修改后逐字节一致。[legacy-differences](legacy-differences)保留两组结果、比较哈希和输入隔离记录，新源码已恢复。它们是已证明的预存差异，未伪装成像素通过；后续新增Card/Legend样式只作用于Nova，不声称完整旧矩阵已关闭。

## 浏览器范围

[browser](browser)采用1440/390×1000、DPR1、mouse、mobile:false，浅深各一组。390是窄屏鼠标模拟，不是触摸真机。官网八配方32张、标签/整卡/Space/表格全选及取消单行20张、英语/希伯来语8张，共60张；状态和矩形在official-matrix.json，语言在official-languages.json。本地也保存对应60张，并另补双方桌面浅色整卡键盘焦点各一张（主对照各61张），以及本地手机Space前后8张诊断过程。实际加载wasm、交互坐标和选取的状态帧在local系列JSON。官网动作截图已等颜色过渡结束后重拍，避免把过渡中的灰色当作最终颜色。

手机Space批次最初截到未选中状态，不能按文件名判通过。已逐步记录初态、标签点击和两次Space；浅色Space-2、深色Space-1实际已选中，分别替换对应主对照图，原错误帧隔离在diagnostics/space-initial-state。两种主题的连续Space均显示选中/未选中反转，页面位置记录在local-space-trace.json。深色第一张初态还出现热态滚动/画面未稳定，作为诊断保留，不声称该初态通过。

8张静态对照拼图、4张动作拼图、4张语言拼图均已目视复查；整卡键盘另直接查看原图。字重、共享说明排版、列宽及RTL说明换行仍有下面列出的差异。不会把原生测试通过或截图数量当作全部状态通过。

## 明确剩余差异

- Table公共组件的自动列宽与可见原生横向滚动条仍有差异；本配方使用公共Table及明确列宽保证真实数据可读、手机可横向滚动，未声称复制HTML自动列算法。
- 部分字重观感、换行、Group单行整数行框、RTL卡片说明行数与共享页面的标题/描述宽度、章节纵向节奏、代码底栏/API排版仍不同；八配方存在并不代表整页像素一致。
- CSS伪元素将官网控件命中区扩为40×32且不改16px布局；当前目录关闭minTouchTarget后尚未完整复制这层浏览器命中行为。
- 整卡键盘图中，本地按CSS规则只显示外环，官网实拍还可见控件内层灰亮，焦点环/绘制的像素对照仍需细查，不能声称焦点像素一致。
- 颜色过渡、pressed完整矩阵、动态theme/shape过程、原生HTML表单name/value/required校验与读屏关联、触摸设备及任意组合仍待核对。Field的通用slot legend、其他控件及响应式分支也需在Field专轮继续覆盖。
- 旧Vega六项已隔离但未修，保持开放；不会因本轮20项通过将整个Checkbox打勾。

进度保持未勾选，后续优先继续尚未开始完整页面对照的组件，并保留这里可定位的差异。
