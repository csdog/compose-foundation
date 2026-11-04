# Card 首轮对照（2026-11-04）

官网依据为 [Base UI Card](https://ui.shadcn.com/docs/components/base/card)，固定源码版本 `4aaea19d9cff3e0908e12a8c1d504b8c5ec34610`。MDX、六个示例、Card与Nova规则的地址和哈希见 [sources.json](sources.json)。原图直接来自官网示例的 avatar.vercel.sh/shadcn1，未重绘。

## 已实现

- 目录替换为 Default、Size、Spacing、Edge-to-edge、Image、RTL 六个实际预览；补完整示例导出、Composition及八个Kotlin API小节。Edge仍属于Spacing正文，不增加官网不存在的目录标题。
- Nova根只有垂直inset；Header/Content自己负责水平inset，Action逻辑末端间隔4dp，Footer保留完整测量/语义范围并铺满。默认Footer gap为0，条款配方显式gap8。显式调用方Style最后覆盖，Vega保留原行为。
- 新增公开CardImage。首个图像移除默认根顶部inset，Footer移除默认底部inset；每卡独立alignment line，显式top/bottom覆盖优先。外环与内容裁剪分离。
- Title/Description/Body使用完整CSS行框。Label在Skia equal font-size/line-height仍添加paragraph leading时，校正外层行框：Arabic单行14、两行28；内部TextLayoutResult段落仍可能30px，不能据外层14声称内部也14。
- Nova Button字符串按自然单行宽度显示；内容允许溢出，仅反馈层按shape裁剪。解决手机Arabic Google最后一词丢失；不改变交互命中范围，显式调用方clip仍生效。Vega保留原裁剪路径及JVM兼容入口。
- 表单输入本地保留，间距16/20/24/32不允许空选，切换语言保留值；标签点击聚焦正确字段。Forgot为真实LinkAnnotation.Url("#")，按钮的无业务回调与官方演示一致。条款为真实可滚动区域，图片16:9、灰度/亮度/遮罩按源码。

## 验证范围

最终15份根XML共 **93项通过**，见 [tests](tests)：core41、app52。包括Card布局契约/目录20、长文字首行完整性、调用方尺寸、图标逻辑边距、invalid、focus、shape反馈，以及Finance/Carousel消费者。RED证据与诊断分目录保留，不计入最终93。

**11张像素校验通过**：CardNova2、ButtonNova2、RippleShape5、Vega Label2。仅更新5张基准及对应文档预览；[统计](baseline-comparison.json)逐项列明原因：

- Card两图432×354→432×376，完整标题/描述/正文行框及布局改变，不是仅边缘变化。
- Button两图540×128尺寸不变，各2个边缘像素变化，来自允许内容溢出同时裁剪反馈。
- circle-ring旧基准为实蓝内部，与现有实际白底粉色反馈失配；暂时恢复HEAD `ef3b0e71` 的Button后实际文件与本轮逐字节一致，SHA `7908e228408fe51ef7d9158f192b8494e503d47bc0ea67ca95fcac23973b5452`，见[隔离记录](diagnostics/circle-ring-head-control/summary.json)。校准既有基准，未改变Vega实现。其余4张Ripple和2张Vega Label未更新。

最终core/shared ktlint及JS/Wasm development distribution通过。最后文案变动后补shared lint/JS/Wasm，未重复没有代码变化的93项测试。应用为 `c7a97b6fbec6511cd220.wasm`，43,282,455字节，SHA256 `669b3dfa98a1daee847c21213c697527c83771f54db78c539db8905853707180`；17份最终输入哈希见[source-inputs.json](source-inputs.json)。仅已有Text INVISIBLE_REFERENCE、AGP/deprecated/webpack诊断。默认detekt的commonMain sourceCount=0限制保留，不能声称完整公共源码静态分析通过。

浏览器使用已授权TaskSpace1，1440/390×1000、DPR1、mouse、mobile:false；390是窄屏模拟，非触摸真机。官网/本地各 **56张**：六配方24、Spacing四状态16、三语12、条款末尾4。最终原图及状态JSON在[browser](browser)，28张source/local对照拼图在[browser/contacts](browser/contacts)，已逐组目视复查。另补桌面/窄屏标签点击聚焦及Forgot链接：实际输入label@example.com，点击Forgot后URL为/card#且scrollY=0。

Spacing截图以insertText与两帧等待后再切换，避免自动化逐键输入末尾尚未落入状态就失焦；完整alice@example.com在切换后保留。RTL选项面板对齐当前选中项，选项坐标随选择变化，最终12图按实际语言复查；窄屏浅色Hebrew单独重新采集。初次错误语言及空白图不作为成功证据。滚轮可连带滚动外层文档，截图仅恢复外层位置，内层保持真实末尾；前后scrollY见JSON，不声称无滚动串联。

原生bounded harness早期裁掉Google的高度问题已修测试容器；通用Web预览原有自然高度，不能据此声称旧Web也存在相同高度缺陷。原生Style节点getUnclippedBounds曾与实际绘制坐标不同，最终用boundsInRoot验证；不把辅助查询差异当作已证明的绘制缺陷。

## 仍开放

- 条款缺少HTML原生滚动条外观；绘制边框未占源码1px布局，段落可用宽和换行不同，滚动末尾内容位置也有差异。
- 画面中仍见浅/深主题字重观感差异、Noto回退字形、标题/正文换行与细微尺寸。不能全部归因抗锯齿；应继续核对实际字体/权重和动态主题排版。
- 共享页面标题/描述、代码预览和垂直节奏不同，Kotlin说明较CSS说明长，截图未强制对齐为相同绝对Y；本轮不关闭全页像素验收。
- 浏览器指针打开语言菜单后Home/End曾滚动外层而未选中预期选项；需专项核对Select初始焦点/浏览器键盘桥接。原生测试通过与指针三语切换不覆盖此差异。
- HTML链接新标签/上下文菜单、辅助技术映射、完整键盘焦点/invalid/disabled/动态主题/触摸矩阵仍待补。仅验证示例真实回调，不实现演示无业务的认证流程。
- 任意Card Style/intrinsics、非直接嵌套slot、其他平台和Label任意字体/emoji/分数行高仍需扩展验证。Button r2菜单宽度、手机碰撞、指针焦点、Tabler图形及expanded/pressed缺口继续保留。

Card/Label/Button保持未勾选。Editor排除，Attachment继续后置。进度记录按创建日期/原追加顺序倒序；旧125条正文保留，新增本条后共126条，见[排序审计](progress-order-audit.json)。
