# Dialog 页面复核（2026-11-05）

固定上游 `8a3536678a528cc012b9e62484290e79d0be410f` 的 Base UI Dialog MDX、`dialog.tsx`、六个官方配方与 `style-nova.css`，并实时对照 [ui.shadcn.com Base UI Dialog](https://ui.shadcn.com/docs/components/base/dialog)。十份来源及 SHA-256 见 [sources.json](sources.json)。

## 发现

- 官方页面顺序为：首个 `dialog-demo`（Edit profile）、Custom Close Button、No Close Button、Sticky Footer、Scrollable Content、RTL，然后 API Reference；正文没有 Examples 分组标题。
- 本地页面此前把 33 个示例放在 "Examples" 标题下，标题多为 Compose API 名，官方六个配方缺 RTL，Sticky Footer 与 Scrollable Content 顺序相反，Custom Close Button/No Close Button 的标题、copy 与预览内容均不是官网内容；页面也没有 Composition 和 API Reference。
- 预览内容差异：官网 Custom Close Button 是 Share link + 只读安装链接 + `sm:max-w-md`（448dp）；No Close Button 使用独立标题/描述；Sticky/Scrollable 是 10 段同一 lorem 文本；RTL 默认阿拉伯语并带语言切换。

## 修改

- 重建 `DialogDoc` 示例列表：`examplesHeading = null`，先放官方六个配方（标题/说明/预览与官网一致，包括 448dp 的 `Style { width(448.dp) }` 覆盖、lorem 滚动体、RTL 三语工具栏），其余 Compose-only 配方统一加 `Kotlin extension: ` 前缀放在官方配方之后；Filter 掉与官方重复的旧 demo/滚动/Sticky/无关闭按钮条目。
- 新增 Usage 导入代码块、Composition 树（`Dialog ├── DialogTrigger └── DialogContent …`）和七个 API 小节（Dialog / DialogTrigger / DialogContent / Header-Title-Description / DialogFooter / DialogClose / Overlay-Portal），并按当前 Base UI 源区分 Compose 扩展（`dismissOnClickOutside`、外部交互否决回调、`closeIcon`、`DialogSize.Lg`）。
- 组件本身未改：Nova 面板宽度/p-4/rounded-xl/bg-popover/ring、4dp 背景模糊、footer `bg-muted/50` + 顶边框、关闭按钮位置都与 `style-nova.css` 一致。

## 浏览器验证

`browser/` 保存实际打开/关闭画面：

- 浅色桌面：Edit profile 打开（输入框初始焦点）、Escape 关闭且焦点环回到触发器 `demo-escape-restore-light.png`，与 `official-demo-open-light.png` 对照；Custom Close Button 448dp、Sticky Footer 滚动后 header/footer 保持、Scrollable Content、No Close Button 无右上角关闭且 Escape 可关闭。
- 深色桌面：`demo-open-dark.png`、`rtl-open-dark.png`。
- RTL：`rtl-open-light.png` 阿拉伯语弹窗，文案/左右顺序/关闭按钮镜像与官网一致。
- 非模态：`non-modal-open-light.png`、`non-modal-page-click-light.png` 表明遮罩缺失但页面仍可交互（点击后弹窗仍在）。
- 禁用关闭：`Kotlin extension: Disabled close` 的禁用关闭控件不关闭弹窗由 JVM 测试覆盖；禁用态浏览器画面未重拍成功，作为残留注明。
- 四象限：`official-*.png` / `local-*.png` 为 1440/390 × 浅深，`sheet-*.png` 为逐象限拼图，全部目视复查。

## 自动验证

`tests/`：DialogExamplesTest **10 项通过**（官方顺序、Kotlin extension 前缀、各配方打开/关闭、Escape 焦点恢复、禁用关闭、RTL 三语、导出 markdown 顺序）。构建 `:app:webApp:wasmJsBrowserDevelopmentExecutableDistribution` 成功；最终应用 `17aa61fed5cd3f56a86f.wasm`，SHA256 `7f45661cba22c83438633b3dd71bca5bfadb43c773ab974a905c067eaeed9fb0`。

## 未关闭的差异

- Canvas 没有 DOM 覆盖层语义：无法提供原生 `inert`/`aria-modal`/`aria-labelledby`、浏览器自带的焦点陷阱与层叠顺序；当前用 Compose 焦点宿主、Tab 闭环与 Escape 处理近似。
- JVM 测试无法给“无任何可聚焦内容的弹窗”注入 Escape（无焦点节点，注入不进入 Popup）；该路径只在浏览器验证（`no-close-button-*`）。这是测试能力限制，不是浏览器行为差异。
- 官网 `sm:max-w-md` 448px 用 `Style { width(448.dp) }` 覆盖 `DialogSize`；`DialogSize` 本身仍只有 Sm/Default/Lg。Base UI 的 `modal="trap-focus"`、`initialFocus`/`finalFocus`、`onOpenChangeComplete` 未映射。
- 共享页面：标签页只有 Base UI（无 React Aria/Radix UI）、无 Vercel 广告、Geist 字体光栅化、手机端 clientWidth 375 对 390、代码面板为 Kotlin 而非 TSX；RTL 使用打包 Noto 回退字体，行框仍有细微差异。
