# Pagination 本轮计划与进度

目标为base-nova，官网及依赖8份固定源码见[sources.json](sources.json)。组件仍未完成，Editor排除、Attachment遗留暂缓。

- 已移植官网Default、Simple、Icons Only、三语RTL的实际内容和状态，原Kotlin可操作分页保留为扩展；四份导出代码从编译源码生成。
- 已修正Nova32dp/2dp间距、响应式默认文字、RTL箭头、禁用透明度、装饰省略号、根层宽度与对齐，提供Link内容slot和size/style覆盖。
- 已修复实际截图暴露的SelectValue窄触发器换行；旧菜单项测试不覆盖当前值。补逻辑与省略号截图验证，公共版本0.3.17。
- 核心24、目录16、所属ktlint两项通过；6张新基准逐张查看后独立verify。两张旧Vega图修改前同样失败，actual逐字节一致，未重录。
- 最终JS/Wasm分别133.971s/94.374s构建成功，最终ledger已归档。浏览器空间18丢失，异步重建询问重新呈现、尚未收到答复，不另开空间或接管用户标签。
- 仍需浏览器桌面/手机浅深、完整键盘/触摸/读屏/主题/Style及链接语义矩阵；[差异矩阵](parity-matrix.json)保持开放。下一项优先尚未开始的Popover，不能将本轮原生通过视为所有组件验收完成。
