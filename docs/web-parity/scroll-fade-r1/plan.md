# scroll-fade 第一轮（2026-10-31）

固定shadcn96da9cc，归档MDX、七配方、shadcn/tailwind.css、Rhea/Nova CSS及site globals共12份来源。前六配方指定Rhea，RTL指定Nova；普通div的rounded-lg/2xl依全局--radius推导，不能套用Rhea的.cn-*组件圆角。

- [x] 默认12%/40dp上限、96dp ease-in-out reveal；Length/Fraction、逐边、非对称大小和none API。
- [x] 单一alpha mask，CSS停点fixup；超半屏/超视口大小，RTL重叠停点方向回归先RED后GREEN。
- [x] draw/cache阶段读滚动；真实触摸不重组调用方；红内容/蓝背景像素证明确为透明遮罩。
- [x] 七组官网配方、精确文案/翻译/尺寸；复制代码从编译源生成；旧shadcnScrollFade名称和mask顺序修正。
- [x] MessageScroller视口顺序修复，Attachment五参配置像素回归通过，未改Attachment示例或遗留。
- [x] 92项独立验证、两个ktlint；64张原生目录图、6张新Nova和4张有意修正的旧图已查看。只更新已核对的四张旧图，保存before/hash证据。
- [x] 最终JS21.569s/Wasm18.178s均退出0；JS仍有os/path解析诊断、Wasm一条webpack警告，未声称干净构建。14份源码、10张新/定向更新基准、64张目录图哈希匹配；证据归档，工作流结束且原始日志清理。
- [ ] 浏览器桌面/手机浅深及完整状态；原空间18丢失，既有重建申请待回复。
- [ ] 不支持scroll-driven-animation浏览器的静态fallback、物理x时间线RTL、custom spacing/rem、字体/主题/密度/启用动态变化与完整辅助技术矩阵。

版本0.3.23。默认edgeSize从固定24dp改为未指定时CSS默认；显式传edgeSize仍为固定大小。需要旧瞬时24dp行为可用edge=Both,size=Length(24.dp),revealDistance=0.dp。mask应放在verticalScroll/horizontalScroll之前，背景/边框在外层wrapper。Mono字体可由调用方传入；目录注入GeistMono。

组件保留未勾选；Editor排除，Attachment遗留暂缓，下一项优先Separator。
