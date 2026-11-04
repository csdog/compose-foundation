# Separator 第一轮（2026-10-31）

固定shadcn96da9cc，五组base-nova配方Default/Vertical/Menu/List/RTL、组件、MDX和Base UI1.6.0实现/锁文件摘录，共9份来源已归档并校验。

- [x] 核对1dp横线/竖线、border颜色、无内建margin及style尺寸/颜色覆盖；原绘制尺寸正确，未作无依据改动。
- [x] 默认decorative=false，与Base UI默认role separator意图一致；native提供description和方向state，调用方可覆盖本地化描述。
- [x] 五官网配方、14/20文本/14sp leading-none标题、精确三语文案、List8dp gap与Menu768dp隐藏第三列/16→8dp间隔。
- [x] 明确竖线需要父高度有界，自适应Row用IntrinsicSize.Min；Menu真实40dp行高和竖线拉伸已验证。
- [x] 核心8、目录20通过；新Nova4已查看。32项最终独立验证和两模块ktlint通过。
- [x] 28张目录原生图已复核。旧Vega浅色通过，深色20px RGB38→34；替换本轮前Separator.kt后复现完全相同actual，证明预存于本轮，未重录旧图；当前源码已恢复。
- [x] JS27.607s/Wasm19.961s均退出0；JS仍有os/path诊断，Wasm一条webpack警告。11份源码/配置/生成器、4张新基准、28张目录图哈希匹配；证据已归档，工作流结束且原始日志清理。
- [ ] 官网/本地浏览器桌面/手机浅深、真实辅助技术和完整动态样式/字体/方向/密度矩阵。

平台差异：Compose1.12.2无Role.Separator，description/stateDescription不是HTML role/aria-orientation的等价映射。HTML dl/dt/dd也未在native中逐项映射。自动高度Row需调用方提供intrinsic测量；不声称任意无界父级自动self-stretch。

版本0.3.24。decorative默认true改false；需要装饰线的现有调用方请显式true。颜色/尺寸默认未变。组件不勾选，Editor排除、Attachment遗留暂缓，下一项Sheet。
