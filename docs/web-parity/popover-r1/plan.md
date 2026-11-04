# Popover 第一轮计划（2026-10-31）

固定上游 `96da9cc7f1aa10580a3741664e2a77c721206c00`，官网 Base / Nova。

- [x] 归档组件、MDX、五组示例和 Nova CSS，共 8 份来源。
- [x] 核心面板：288dp 默认宽、10dp padding/gap、lg 圆角、foreground/10 ring、shadow-md；Header gap 2dp，14/20sp 标题与说明。9项原生验证通过；浏览器仍待验收。
- [ ] 官网 Default（320dp / 四个尺寸输入）、Basic、Align、With Form、RTL 五组配方；现有 Dimensions form 只有两项，未替换完成。
- [ ] 核对 Base UI 默认 modal、初始焦点、关闭恢复、Escape、Tab、外部点击、受控拒绝、快速重开与嵌套。
- [ ] 定位：四物理边与两个逻辑边、三种 align、offset、翻转与手机碰撞。
- [ ] 动画：官网 duration-100、方向滑移/95%缩放；当前共享 PopupTransitions 仍待核对。
- [ ] 浏览器桌面/手机 × 浅/深色及三语 RTL。原空间 18 丢失，已向用户申请重建；未获答复前不替换空间。
- [x] 本轮核心原生验证9项、旧截图独立 verify与隔离、两项ktlint、JS/Wasm。完整交互和配方验证仍待后续。

组件保持未勾选。原生模拟布局不能替代浏览器验收；Editor 排除，Attachment 遗留暂缓。
