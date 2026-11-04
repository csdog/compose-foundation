# Chart axis r2 — responsive RTL follow-up

复现前轮窄屏阿语末两月重叠：在1440打开First Chart，切390后进入RTL；首次窄屏进入和单独切主题不复现。旧版重叠帧与下一次稳定帧均保存在[browser](browser/review.json)。

`ChartXAxis`移除独立`rememberTextMeasurer`/记忆宽度与BoxWithConstraints。单个Layout测量实际Text子节点，使用同一组placeable宽度决定碰撞、保留末刻度并放置；未选中的节点不放置。内容按index/label保留key。避免预估宽度与最终字体/文本测量分离；底层为何仅在Web缩放路径触发尚未精确隔离，不声称已证明是某个字体缓存缺陷。

新增阿语/希伯来语真实RTL294dp的文字间距与完整glyph断言，以及592→294后的像素与全新294布局一致性断言。四项在旧实现JVM上也全部通过，**不是native RED/GREEN**。最终32核心+32目录=64项独立通过，core/shared ktlint通过；44张原生图与final60逐字节相同，复用已审核证据。Wasm开发distribution32.657s通过，保留1项webpack warning；18项输入哈希再次一致。见[验证摘要](verification/summary.json)。

浏览器确认新`75b3de24bd2874335b17.wasm`实际加载后，原缩放步骤已不再重叠。AR/HE/EN×1440/390×浅深12张以及交互主图四象限4张均已目视复查，原图和SHA见[review.json](browser/review.json)。第一次Hebrew坐标操作未选成功，被截图检查发现并替换；归档的四张Hebrew均已确认实际语言。390是桌面窄视口，不是真实手机/触摸验收。

另补官网键盘核对：之前立即截图早于Recharts异步更新，实际上方向键有效。保留SVG真实focus，逐键等待DOM可见Tooltip后，Apr2,2024 / Page Views /180已确认，见[官网截图](../chart-r1/browser/official-mobile-keyboard-confirmed-1440-dark.png)及[状态](../chart-r1/browser/official-mobile-keyboard-confirmed.json)。

剩余仍由[Chart r1清单](../chart-r1/README.md)跟踪。尤其主图窄屏日期刻度仍受实际绘图区/字体差异影响（本地Apr1/8/15/22/30，官网Apr7/14/22/30）；本轮不声称整页像素相同或完整交互/辅助技术完成。未验证/刷新旧golden，未运行全仓提交门禁或新增commit。
