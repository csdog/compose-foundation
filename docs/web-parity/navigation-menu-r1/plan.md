# Navigation Menu 对照计划

本轮已开始，目标为base-nova。固定官网revision与Base UI 1.6.0原语源码见[sources.json](sources.json)。当前仍未完成，Editor排除，Attachment遗留暂缓。

1. 修复原语观察到的计时/禁用/回调/链接关闭缺口；按Nova补触发器36dp、12dp箭头、零列表间隔、半muted打开态、hover/focus与4dp内容留白。每项以真实交互/像素或几何回归验证。
2. 迁移官网Default与三语RTL完整配方，含Getting started三链接、响应式Components六链接、With Icon三链接与Docs。原生overview与扩展示例不能充作官方Default。导出代码必须独立且与实际预览一致。
3. 补root级共享视口、首尾对齐、跨面板宽高/位置及方向过渡；不可用当前逐项AnchoredPopup替代后勾选完成。对照安全指针通道、焦点/方向键/Tab/Escape返回/焦点移出、受控拒绝与卸载重排。
4. 恢复原浏览器空间18或收到重建答复后，完成官网与本地桌面/手机×浅深及完整交互矩阵；当前浏览器空间只读复查仍不存在，不接管用户标签或新建替代空间。
5. 运行定向核心与示例测试、拥有源码的格式检查和单独JS/Wasm构建。新黄金图先查看再独立verify，旧图不无理由刷新；实际commonMain Detekt已有全模块失败，不能按默认零源码任务退出成功计绿。
6. 逐项更新进度与差异。剩余所有缺口有明确结论且验收完整前保持未勾选。适时通过真实pre-commit门禁后提交，不绕过hook。


第一轮结果：基础Nova触发器/内容样式和实际开关事件已修复；13项行为、4张新增图、3张旧图独立verify共20项通过，两个所属ktlint、JS/Wasm构建成功。来源10份；原图已查看，旧基准未重录。见[本轮矩阵](parity-matrix.json)。后续仍按本计划补官网完整Default/RTL、共享视口及全部状态，不把上述局部验证扩大为组件完成。


第二轮：官网Default/三语RTL的实际内容、导航回调、图标与媒体查询已迁移，75条文案逐字核对，18目录+22核心独立校验通过；悬停关闭/重开与Popup导航依赖作用域问题已修正。浏览器及root共享视口/align仍未完成，详见[第二轮矩阵](../navigation-menu-r2/parity-matrix.json)。按用户“优先尚未开始”顺序，下一项Pagination；本计划其余条目继续开放，未以两组示例通过销项。
