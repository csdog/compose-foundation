# Android registry 逐项验收

使用 Pixel 10a 模拟器（Android 17，API 37，16 KB 页面）和当前工作树构建的 catalog 调试 APK。69 个目录条目均采集了浅色、深色截图与对应 Android 无障碍树。每组截图的页面背景通过深浅色像素采样。表中交互列记录已实际执行的用例。

- Popover 的默认面板在触发器下方，外观无箭头。非模态 Popover 从触控打开时曾无法用 Escape 关闭，修复后在 API 37 验证通过。新增的窗口级键盘回退适用于 API 28 起，API 24–27 的键盘行为待复验。
- 两轮连续浏览均在进入 Message Scroller 时达到 192 MiB Java 堆上限并发生 OOM。同一条目以冷进程打开时，两种外观均通过。验收脚本现在每 12 条重启 catalog 进程；连续导航场景的内存问题仍待查。
- Stepper 从第 3 步返回时曾在 Compose Styles 挂载文本颜色修饰器时崩溃。文本颜色覆盖现在保持同一修饰器结构。修正版通过 Android 模拟器的 Next、末步边界、Back 和状态语义验收，也通过 Stepper、文本颜色继承和画笔 JVM 回归。
- Pagination 的单行在可用宽度不足时可横向滚动。Pixel 10a 上实测 Controlled 示例从第 1 页点到第 2 页，左滑露出 Next 并进入第 3 页，右滑露出 Previous 并返回第 2 页。15 项 JVM 回归通过，其中包含窄屏滚动与嵌套水平滚动。6 张 Pagination 截图的修复前后像素相同，其中 5 张在两版中均与现有基线有差异。
- Carousel 示例原有的前后按钮在 `Slides` 无障碍容器外，TalkBack 复验曾跳过按钮。示例现将控制区纳入容器。Pixel 10a 的默认示例保持幻灯片视口坐标，两个按钮各向内移 9 px，深浅色布局坐标一致。新版通过一次完整往返及再次前进，并在重启 catalog 进程后通过首张换向复验。9 项 `CarouselExamplesTest` 通过，覆盖横排和竖排控制区边界。
- TalkBack 17.0.0 已实测 Tabs、Checkbox、Radio Group、Toggle 和 Pagination。开启 TalkBack 的语音文字显示后，Checkbox 出现名称和复选框角色，Radio Group 出现名称和单选状态，Toggle 出现按钮角色和勾选反馈。为 `RadioGroupItem` 提供显式集合位置后，Compact 的播报从 “1 of 3” 修正为 “3 of 3”，双击仍能选中。纵排、横排 RTL 和动态重排的 JVM 回归通过。
- Questionnaire 的旧版 TalkBack 探索落在题干容器，随后向右滑会跳到问卷之后的内容。给当前题目设置独立的无障碍遍历分组后，TalkBack 可直接聚焦并双击选项。修正版通过触控、硬件键盘和深浅色复验、Android 调试构建及 64 项 Questionnaire JVM 测试。[Compose 无障碍遍历顺序](https://developer.android.com/develop/ui/compose/accessibility/traversal)
- Resizable 的横向分隔线新增无障碍激活以聚焦分隔线。在 Pixel 10a 上，横向焦点边界从 3 px 扩为 53 px。TalkBack 触摸探索直接聚焦，向上和向下滑动将范围值从 0.50 调至 0.55 再调回 0.50。触控拖动、硬件键盘、深浅色、Android 调试构建和 36 项 JVM 测试均通过。深浅色的面板与分隔线截图裁切分别与修复前逐像素一致。
- Table 在横向溢出时允许聚焦视口，处理左右方向键、Home 和 End。修正版在 Pixel 10a 上通过 LTR 触控、LTR 和阿拉伯语 RTL 键盘操作以及深浅色复验。深浅色的静止表格截图裁切分别与修复前逐像素一致。24 项 Table JVM 测试通过。

浅色、深色列的“通过”表示验收脚本找到了预期目录页面或示例，采集了对应截图和无障碍树，并通过了背景采样。“冷启通过”对应单独重试。

“无自身操作”用于布局、视觉修饰或静态内容。子内容的无障碍名称和朗读仍按示例单独核对。

| Registry 条目 | 浅色 | 深色 | 触控 | 硬件键盘 | TalkBack |
| --- | --- | --- | --- | --- | --- |
| `accordion` | 通过 | 通过 | 展开及收起指定项 | Tab 聚焦退货政策，Enter 展开，Space 收起 | TalkBack 聚焦退货政策触发器，双击展开内容，再双击收起 |
| `alert` | 通过 | 通过 | Review changes 示例点 Save，说明更新为 Save selected. | 从页面冷启连续 Tab 到 Review changes 示例的 Save，Enter 更新为 Save selected.，再 Tab 到 Undo 并按 Space，更新为 Undo selected. | TalkBack 触摸探索直接聚焦默认标题 Payment successful。向右滑动时也捕获到破坏性示例标题 Payment failed 的焦点事件 |
| `alert-dialog` | 通过 | 通过 | 打开、点 Cancel 关闭 | Escape 关闭 | TalkBack 聚焦 Show Dialog 和 Cancel，双击可打开及关闭。关闭后约 1.5 秒的两次采样分别未找到焦点、确认焦点回到 Show Dialog |
| `aspect-ratio` | 通过 | 通过 | 无自身操作 | 无自身操作 | TalkBack 可聚焦子图片 Photo，具体朗读待验 |
| `attachment` | 通过 | 通过 | 点预览卡片打开 research-summary.pdf 示例弹窗 | 冷启后 Tab 到 Preview research-summary.pdf，Enter 打开，Escape 关闭并归还焦点 | 无障碍树有 Remove message-renderer.tsx。默认示例的移除按钮在触摸探索及连续向右滑中未获得 TalkBack 焦点 |
| `avatar` | 通过 | 通过 | 点 Open user menu 头像触发器，菜单出现 Profile 和 Billing | 冷启后 Tab 到 Open user menu，Enter 打开，Down 聚焦 Billing，Escape 关闭并归还触发器焦点 | TalkBack 可聚焦默认示例的 `@shadcn` 头像，字幕为 “@shadcn. Image” |
| `badge` | 通过 | 通过 | 点 Open Link 后原生目录滚到 Link 小节 | 冷启后 Tab 聚焦 Open Link，Enter 后原生目录滚到 Link 小节 | TalkBack 触摸探索直接聚焦默认示例的 Secondary 徽标 |
| `breadcrumb` | 通过 | 通过 | 点省略号打开，点 Themes 关闭 | Tab 聚焦 Toggle menu，Enter 打开并聚焦 Documentation，Down 聚焦 Themes，Escape 关闭及 Enter 选择后均回到触发器 | TalkBack 双击 Toggle menu 打开后聚焦 Documentation。触摸探索聚焦 Themes，双击选择后菜单关闭，焦点回到 Toggle menu |
| `bubble` | 通过 | 通过 | 点 I forgot my password 出现 You clicked forgot password | 冷启后 Tab 到 I forgot my password，Enter 出现 You clicked forgot password | TalkBack 触摸探索直接聚焦气泡正文 Hey there! what's up? |
| `button` | 通过 | 通过 | 按住 Submit 显示按压样式，松手恢复 | Tab 聚焦 Submit，按住 Space 显示按压样式，松手恢复。Stepper 的 Button 样例验证了 Enter 和 Space 能触发可见状态变化 | 冷启后向右滑 13 次聚焦 Submit，双击产生该按钮的点击事件 |
| `button-group` | 通过 | 通过 | 点 More Options 打开消息操作菜单 | Tab 聚焦 More Options，Enter 打开并聚焦 Mark as Read，Down 聚焦 Archive。Escape 关闭或 Enter 选择后均归还焦点 | TalkBack 双击 More Options 后出现 Mark as Read，聚焦并双击该项后菜单项消失 |
| `calendar` | 通过 | 通过 | 前后翻月、选中日期并核对勾选状态 | Tab 进入已选日期，右键只移动焦点，Enter 选中，Page Down 聚焦下个月对应日期 | TalkBack 聚焦并双击 Next month 翻月，再聚焦下个月 15 日并双击。日期由未选中变为已选中，焦点留在该日期 |
| `card` | 通过 | 通过 | 点邮件输入框后可编辑 | Tab 聚焦邮件输入框，输入并读取聚焦节点的值 | TalkBack 直接聚焦登录卡片标题 Login to your account。聚焦邮件输入框并双击进入编辑，读取聚焦节点核对输入值 |
| `carousel` | 通过 | 通过 | 下一张与上一张按钮、横向滑动切换页面 | Tab 聚焦 Next，Space 和 Enter 连续前进到第 5 张。Next 禁用时焦点移到 Previous，可直接按 Enter 退回首张。首张反向交接到 Next，Space 可再次前进 | TalkBack 触摸探索聚焦按钮，双击逐张前进到第 5 张，再逐张返回。边界按钮禁用后仍保留无障碍焦点，触摸探索可聚焦另一侧按钮并换向 |
| `chart` | 通过 | 通过 | 点击 Mobile 和 Desktop 切换柱状图的选中系列 | Tab 聚焦 Mobile 后按 Enter 切换，Shift+Tab 聚焦 Desktop 后按 Space 恢复，核对两系列选中状态 | 交互柱状图的 Mobile 文字在无障碍树中，触摸探索及连续向右滑未聚焦可激活的 Mobile 控件 |
| `checkbox` | 通过 | 通过 | 勾选状态 | Tab 聚焦首个同名复选框，Space 勾选，Enter 取消 | 探索聚焦、双击勾选，字幕确认名称和复选框角色 |
| `chip` | 通过 | 通过 | Selectable 示例连续点击 Kotlin，状态依次变为已选中和未选中 | 冷启后 Tab 到 Selectable 的 Kotlin，Space 选中，Enter 取消 | 在 Selectable 示例中，TalkBack 双击 Kotlin 后选中，再次双击取消，焦点保持在该控件 |
| `code-block` | 通过 | 通过 | 点 Wrap lines，状态变为 Pressed | Tab 聚焦 Wrap lines，Enter 取消换行，Space 恢复换行，核对 Pressed 状态 | TalkBack 聚焦 Wrap lines 并双击，状态从 Not pressed 变为 Pressed |
| `collapsible` | 通过 | 通过 | 显示及隐藏订单详情 | Tab 聚焦 Toggle details，Enter 显示详情，Space 隐藏 | TalkBack 聚焦 Toggle details 并双击显示和隐藏 Shipping address |
| `combobox` | 通过 | 通过 | 打开建议并点选 SvelteKit | 输入过滤、Down 和 Enter 选择建议 | TalkBack 聚焦并双击 Show suggestions，触摸探索聚焦 SvelteKit，双击后列表关闭，焦点回到值为 SvelteKit 的输入框 |
| `command` | 通过 | 通过 | 聚焦搜索框 | 输入 calc，保留 Calculator 并隐藏其他建议 | TalkBack 触摸探索直接聚焦 Calendar，双击后页面显示 Selected: Calendar，焦点保持在该菜单项 |
| `context-menu` | 通过 | 通过 | 长按打开上下文菜单 | Tab 聚焦 Right click here，Shift+F10 打开，Escape 关闭并归还焦点 | TalkBack 聚焦 Right click here 后双击长按打开菜单；聚焦并双击 Reload 关闭，焦点回到触发器。新增无障碍长按动作后通过 23 项 JVM 测试 |
| `dialog` | 通过 | 通过 | 打开、点 Cancel 关闭 | Enter 打开、默认聚焦 Name、Tab 到 Username、Escape 归还触发器焦点 | TalkBack 双击 Open Dialog 打开，双击右上角 Close 关闭并确认焦点回到 Open Dialog。默认打开后的采样未找到焦点，首轮也未找到底部 Cancel |
| `drawer` | 通过 | 通过 | 打开、点 Cancel 关闭 | Escape 关闭 | TalkBack 聚焦 Open Drawer 和 Cancel，双击可打开及关闭。各次激活后 1.5 秒采样时未找到无障碍焦点 |
| `dropdown-menu` | 通过 | 通过 | 打开、点选 Profile 后关闭 | Tab 聚焦 Open，Enter 打开并聚焦 Profile，Down 聚焦 Billing。Escape 关闭或 Enter 选择后均归还焦点 | TalkBack 双击 Open 后出现 Profile，聚焦并双击 Profile 后菜单项消失 |
| `emoji-text`（Bubble 的表情反应） | 通过 | 通过 | 无自身操作 | 无自身操作 | TalkBack 触摸探索直接聚焦 Reaction: thumbs up 图像节点 |
| `empty` | 通过 | 通过 | 点击 404 示例的搜索框后可以编辑 | 冷启后 Tab 聚焦 404 示例的搜索框，输入 audit 并清除 | TalkBack 触摸探索聚焦 Create Project，双击产生点击事件。示例回调为空 |
| `field` | 通过 | 通过 | 点 Name on Card 的占位文本聚焦输入框 | 输入并核对编辑值和字段标签 | TalkBack 聚焦带有 Evil Rabbit, required 语义的 Name on Card 输入框，双击进入编辑，读取聚焦节点确认新值 |
| `hover-card` | 通过 | 通过 | 默认示例长按未弹出面板 | Tab 聚焦 Hover Here 后打开，Escape 关闭 | TalkBack 触摸探索直接聚焦 Hover Here 触发器，面板内容待验 |
| `icon`（Input Group 的搜索图标） | 通过 | 通过 | 无自身操作 | 无自身操作 | 默认搜索图标为装饰性图像，复查无障碍树确认图标无独立节点，Search 名称位于所属容器 |
| `input` | 通过 | 通过 | 聚焦密码输入框 | 输入及无障碍值掩码 | TalkBack 聚焦 API Key 的密码输入框并双击进入编辑，输入 10 字符后无障碍值为掩码 |
| `input-group` | 通过 | 通过 | 聚焦搜索框 | 输入并核对编辑值 | 金额示例的 Enter amount 可用 TalkBack 聚焦、双击和编辑，聚焦节点读回 `42`。默认 Search... 仍无法通过触摸探索或连续向右滑聚焦 |
| `input-otp` | 通过 | 通过 | 聚焦验证码 | 退格、补位、六位上限 | TalkBack 聚焦并双击进入验证码编辑。将原值替换为七位数字，确认聚焦的输入节点只保留前六位 `246810` |
| `item` | 通过 | 通过 | 按住 Action 显示按压样式，松手恢复 | 冷启后 Tab 聚焦 Action，按住 Space 显示按压样式，松手恢复 | TalkBack 触摸探索直接聚焦 Action，双击产生点击事件。示例回调为空 |
| `kbd` | 通过 | 通过 | 无自身操作 | 无自身操作 | TalkBack 可聚焦示例中的 ⌘ 键标签，具体朗读待验 |
| `label` | 通过 | 通过 | 点标签文字勾选关联复选框 | Tab 聚焦关联复选框，Space 勾选，Enter 取消勾选 | TalkBack 触摸探索聚焦名为 Accept terms and conditions 的复选框，双击后从未勾选变为已勾选并保持焦点 |
| `marker` | 通过 | 通过 | 无自身操作 | 无自身操作 | 默认 Switched to a new branch 在无障碍树中。触摸探索及连续向右滑未聚焦该文本节点 |
| `menubar` | 通过 | 通过 | 打开 File、点选 New Tab 后关闭 | Tab 聚焦 File，Enter 打开并聚焦 New Tab，Down 聚焦 New Window。Escape 关闭或 Enter 选择后均归还焦点 | TalkBack 双击 File 后出现 New Tab，聚焦并双击 New Tab 后菜单项消失 |
| `message` | 通过 | 通过 | 按住 Actions 示例的 Like 显示按压样式，松手恢复 | 冷启后 Tab 聚焦 Like，按住 Space 显示按压样式，松手恢复 | 默认示例的 Deploying to prod real quick. 在无障碍树中可见。触摸探索及连续向右滑未取得该文本节点的独立焦点 |
| `message-scroller` | 冷启通过 | 冷启通过 | 点 Send 启动本地演示回复，读到完整末段；点 Reset conversation 恢复空状态 | Tab 聚焦 Send 后按 Enter 启动本地演示回复，读到完整末段 | TalkBack 触摸探索聚焦 Messages 滚动容器。Morning, shadcn! 在无障碍树中，独立正文焦点用例未通过 |
| `navigation-menu` | 通过 | 通过 | 点开 Getting started、点外部关闭 | Tab 聚焦触发器、Down 聚焦 Introduction、Escape 归还焦点 | TalkBack 聚焦并双击 Getting started 后出现 Introduction，采样焦点仍在触发器 |
| `pagination` | 通过 | 通过 | Controlled 数字切页、横向滑动后点 Next 和 Previous，核对选中页 | 在第 10 页按钮按 Tab 使 Next 滚入视口，Enter 切到第 2 页 | 聚焦已滑入视口的 Next，在控件外双击后第 2 页选中 |
| `popover` | 通过 | 通过 | 打开、点外部关闭 | Escape 关闭 | TalkBack 双击 Open popover 后出现 Dimensions，采样焦点仍在触发器。触摸探索可将焦点移到面板内的 Dimensions 标题 |
| `progress` | 通过 | 通过 | 无自身操作 | 无自身操作 | TalkBack 可聚焦默认进度条，朗读字幕为 “66%. Progress bar”。焦点节点当前值 66，范围 0–100 |
| `questionnaire` | 通过 | 通过 | 选答进入第二题，回到第一题后可用原答案继续 | Tab 聚焦首个答案，未作答时 Enter 留在首题。Space 勾选，Enter 进入第二题，聚焦 Previous 后按 Enter 返回且原答案仍选中 | TalkBack 触摸探索聚焦 Tool call timeline，双击选中。双击 Next 和 Previous 切题，焦点移到对应题干。返回首题后选项仍为 Selected |
| `radio-group` | 通过 | 通过 | 单选状态切换 | Tab 聚焦 Comfortable，Down 聚焦并选中 Compact，Up 回到 Comfortable | 聚焦并双击选中 Compact，字幕确认单选角色及第 3 项 |
| `resizable` | 通过 | 通过 | 向右拖分隔线扩大左侧面板 | 拖动聚焦分隔线后，左键缩小左侧面板 | TalkBack 直接聚焦 Resize panels horizontally 的 SeekBar 节点，向上和向下滑动可调整并恢复分隔比例 |
| `scroll-area` | 通过 | 通过 | 在列表内向上滑显示后续版本标签，向下滑恢复首项 | Tab 聚焦内层 Tags 视口，连续按 Down 显示 v1.2.0-beta.43 | TalkBack 触摸探索直接聚焦首个版本标签 v1.2.0-beta.50 |
| `scroll-fade` | 通过 | 通过 | 内层列表向上滑显示后续条目，向下滑回到 Item 1 | Tab 聚焦内层视口，Page Down 显示 Item 9，Page Up 回到 Item 1。连续 Down 用例未滚到目标项 | TalkBack 可独立聚焦渐隐滚动示例的 Item 1 |
| `select` | 通过 | 通过 | 选择水果并关闭菜单 | Down 移动焦点、Enter 提交选择 | TalkBack 双击 Select a fruit 打开列表。触摸探索聚焦 Banana，双击后选项列表关闭，焦点回到显示 Banana 的触发器。打开后的初始焦点在两次采样中分别为空和 Fruits 分组 |
| `separator` | 通过 | 通过 | 无自身操作 | 无自身操作 | TalkBack 可聚焦示例分隔线自身的 Separator 节点，方向朗读待验 |
| `sheet` | 通过 | 通过 | 打开、点 Close 关闭 | Escape 关闭 | TalkBack 聚焦 Open 和 Close，双击可打开及关闭。各次激活后 1.5 秒采样时未找到无障碍焦点 |
| `shimmer` | 通过 | 通过 | 无自身操作 | 无自身操作 | 树中有 Generating response…，本轮的触摸探索及向右滑动未聚焦该文字 |
| `sidebar` | 通过 | 通过 | 点 Toggle Sidebar 打开，Android 返回关闭 | Tab 聚焦 Toggle Sidebar，Enter 打开，Escape 关闭 | TalkBack 聚焦 Toggle Sidebar 并双击，面板显示 Playground。打开后约 1.5 秒采样未找到焦点，面板内部操作待验 |
| `skeleton` | 通过 | 通过 | 无自身操作 | 无自身操作 | 占位形状无自身朗读，真实导航跳过待验 |
| `slider` | 通过 | 通过 | 点中点设为 50，核对无障碍范围值 | Tab 聚焦滑块，Home 到 0，右键加 1，End 到 100，继续按右键不越界 | TalkBack 聚焦滑块，向上滑将范围值从 75 调到 76，向下滑恢复 75 |
| `spinner` | 通过 | 通过 | 无自身操作 | 无自身操作 | Loading 节点具有不确定进度条语义，Android 树中角色为 ProgressBar。旋转动画的 16 帧采样从 5 种无障碍边界修复为 1 种。触摸探索及向右滑动仍未聚焦该节点，朗读待验 |
| `stepper` | 通过 | 通过 | 前进到末步、不越界，返回第 2 步并核对步骤状态 | Tab 聚焦 Next，Enter 和 Space 依次前进到第 3 步，再按 Enter 不越界。Shift+Tab 聚焦 Back 后按 Enter 返回第 2 步，核对全部状态 | TalkBack 双击 Next 依次将第 2、3 步设为 Current，末步再次双击保持 Current。双击 Back 后第 2 步恢复为 Current |
| `switch` | 通过 | 通过 | 点关联标签切换 | Tab 聚焦 Airplane Mode，Space 开启，Enter 关闭 | 给默认示例的开关加上 Airplane Mode 名称后，TalkBack 可聚焦可切换节点，双击后从未勾选变为已勾选，焦点仍在开关 |
| `table` | 通过 | 通过 | 左滑显示 Amount 和 `$250.00`，右滑回到 Invoice | 溢出视口可通过 Tab 聚焦。LTR 按 Right 显示 Amount。阿拉伯语 RTL 按 Right 停在起点，按 Left 后金额表头向右移动 105 像素。两种方向均用 End 到达末端、Home 还原起点。24 项 Table JVM 测试通过 | TalkBack 可独立聚焦 Invoice 表头。捕获到包含 INV002、Pending 和 PayPal 的行焦点事件，指定该行的复验未稳定取得焦点 |
| `tabs` | 通过 | 通过 | 切换面板 | Tab 聚焦、右键切换 | Reports 焦点及面板切换 |
| `text` | 通过 | 通过 | 无自身操作 | 无自身操作 | TalkBack 触摸探索直接聚焦 Title Large 文本节点 |
| `textarea` | 通过 | 通过 | 聚焦文本框 | 输入并核对编辑值 | TalkBack 触摸探索直接聚焦 Type your message here.，双击进入编辑后，聚焦的输入节点提供实际输入值 |
| `theme`（Theming） | 通过 | 通过 | 点目录的 Toggle theme，页面背景由浅色变为深色 | Tab 聚焦 Toggle theme，Enter 将页面背景切回浅色 | TalkBack 直接聚焦目录的 Toggle theme，双击后页面背景采样由浅色切换至深色 |
| `toast` | 通过 | 通过 | 点 Show Toast，显示通知和操作按钮，超时消失 | Tab 聚焦 Show Toast，Enter 显示通知和 Undo，通知正常到期。默认时限内未用 Tab 聚焦 Undo | TalkBack 直接聚焦 Show Toast，双击后通知出现 Event created。通知朗读与 Undo 操作待验 |
| `toggle` | 通过 | 通过 | 切换并还原选中状态 | Tab 聚焦 Bookmark，Space 选中，Enter 还原 | 聚焦 Bookmark 并双击勾选，字幕确认按钮角色与勾选反馈 |
| `toggle-group` | 通过 | 通过 | 切换一项并核对其余项 | Tab 进入 Bold，右键聚焦 Italic，Space 选中，Enter 还原 | TalkBack 触摸探索直接聚焦 Toggle italic，双击选中，再次双击清除选中并保持焦点 |
| `tooltip` | 通过 | 通过 | 默认示例长按未显示提示 | Tab 聚焦 Hover 后显示 Add to library，Escape 关闭 | TalkBack 触摸探索直接聚焦 Hover 触发器，提示正文待验 |
| `trace` | 通过 | 通过 | 展开条目核对详情，再收起 | Tab 聚焦 Thought for 5s，Enter 展开详情，Space 收起 | 冷启后向右滑 13 次聚焦 Thought for 5s，双击产生该触发器的点击事件 |

## 复验

安装当前工作树的 catalog 调试 APK，到专用 Android 模拟器运行下面的检查。目录脚本恢复运行前的系统夜间模式。

```sh
python3 shadcn/registry/build.py --output /tmp/compose-android-registry
python3 shadcn/registry/validation/verify_android_catalog.py \
  --device emulator-5580 --registry /tmp/compose-android-registry/android/registry.json \
  --output /tmp/compose-android-catalog-results
python3 shadcn/registry/validation/verify_android_interactions.py \
  --device emulator-5580 --output /tmp/compose-android-interaction-results
```

交互脚本可用 `--only menus modals dialog-focus` 重跑菜单、模态弹层和 Dialog 的键盘焦点。使用 `--only carousel command combobox` 可重跑 Carousel 按钮和两个搜索输入用例。Combobox 的触控选择另用模拟器截图定位，因为非模态建议列表独立成窗。使用 `--only disclosures select text password otp toggles` 可重跑展开、选择和输入用例。

使用 `--only calendar context-menu toast navigation-menu` 可复验 Calendar 翻月和选择、Context Menu 长按、Toast 超时以及 Navigation Menu 焦点。

使用 `--only context-menu-keys` 可复验 Shift+F10、Escape 和触发器焦点。TalkBack 长按用 `--route context-menu --label 'Right click here' --role '' --long-press --shown-after 'Show Bookmarks'`，再用 `--current-screen --label Reload --role '' --require-clickable --hidden-after 'Show Bookmarks'` 核对菜单关闭及焦点归还。

使用 `--only scroll-area resizable` 可复验列表内滚动、拖动分隔线，以及分隔线聚焦后的方向键调整。

使用 `--only table table-keys` 可复验窄屏表格的触控及键盘横向滚动，使用 `--only card` 可复验触控与 Tab 聚焦邮件输入框并编辑。表头的 TalkBack 用例使用 `--route table --label Invoice --role android.widget.TextView --exact-focus --focus-only`。

使用 `--only chart` 可复验 Desktop 与 Mobile 系列的触控及键盘切换。

深页键盘用例从应用冷启开始连续发送 Tab，并读回真实输入焦点。在 Pixel 10a 上，Alert 的 Save、Avatar 的 Open user menu、Attachment 的 Preview research-summary.pdf 和 Selectable Chip 分别在第 66、60、77 和 35 次 Tab 获得焦点。操作结果在对应组件行记录。

Badge 的 Open Link 通过触控和 Enter 分别复验。两次都用无障碍树记录激活前后的 Link 标题位置，确认原生目录滚到了对应小节。12 项 Badge JVM 测试覆盖浏览器导航桥、原生页内锚点及不同宽度和配色。Button、Item 和 Message 的按压样式分别采集静止、按住和松手画面，比较按钮区域像素是否变化和恢复。2 项 Spinner JVM 测试通过，覆盖进度条语义、焦点边界稳定和动画绘制。

使用 `--only scroll-fade message-scroller` 可复验渐隐列表的触控滚动，以及聊天示例的触控发送、重置及 Enter 发送。

使用 `--only tooltip-keys hover-card-keys` 可复验 Tab 聚焦时打开、Escape 关闭。

使用 `--only sidebar` 可复验触控与 Enter 打开侧栏、Android 返回和 Escape 关闭侧栏。TalkBack 打开用 `--route sidebar --label 'Toggle Sidebar' --role '' --require-clickable --shown-after Playground`。

使用 `--only trace questionnaire label field button-group breadcrumb` 可复验详情展开、问卷保留答案、关联标签、字段输入、按钮组菜单和面包屑菜单。

使用 `--only accordion-keys collapsible-keys trace-keys switch-keys checkbox-keys radio-keys toggle-keys toggle-group-keys` 可复验键盘焦点、展开及勾选状态。

使用 `--only label-keys toast-keys questionnaire-keys` 可复验关联复选框的 Space 和 Enter 切换、键盘触发通知后的显示和超时，以及问卷的键盘选答、往返和状态保留。

使用 `--only dropdown-menu-keys menubar-keys button-group-keys breadcrumb-keys` 可复验菜单初始焦点、方向键、选择、Escape 及关闭时的焦点归还。

使用 `--only calendar-keys` 可复验日期焦点、选择与按月移动。

使用 `--only carousel-keys carousel` 可复验五张示例的双向连续翻页、两端禁用及无需额外 Tab 的焦点交接，以及触控按钮和横滑。两张的双向交接另由 `CarouselKeyboardTest` 覆盖。

在已开启 TalkBack 触摸探索的专用模拟器上，可复验焦点和激活。脚本保持 TalkBack 运行，先触摸探索，再按需向右滑动到目标。随后在控件外双击，观察器核对点击事件及勾选状态。开启 TalkBack 的语音文字显示，使用 `--output` 可保存聚焦和激活截图。

Button 使用 `--route button --label Submit --role '' --require-clickable` 匹配包含 Submit 标签的可点击节点，并核对该节点的点击事件。

复验 Alert Dialog 时，先用 `--route alert-dialog --label 'Show Dialog' --role android.widget.Button --shown-after 'Are you absolutely sure?'` 打开。保持当前弹层，再用 `--current-screen --route alert-dialog --label Cancel --role android.widget.Button --shown-after 'Show Dialog'` 关闭。`afterActivate` 记录双击激活约 1.5 秒后的无障碍焦点。

复验 Dialog 时，对 `Open Dialog` 和右上角 `Close` 使用同一流程，分别以 `Edit profile` 和 `Open Dialog` 作为 `--shown-after`。弹层用例核对真实 TalkBack 手势和无障碍焦点，具体朗读短语仍待验。

复验菜单时，可先用 `--shown-after` 检查菜单项出现，再用 `--current-screen` 聚焦并双击菜单项，使用 `--hidden-after` 核对该项消失。观察器也会检查 catalog 的独立弹出窗口。

面包屑使用 `--route breadcrumb --role '' --require-clickable`。先传 `--label 'Toggle menu' --shown-after Themes`，再传 `--current-screen --label Themes --hidden-after Themes`。

Select 使用 `--route select --role '' --require-clickable`。先传 `--label 'Select a fruit' --shown-after Apple`，再传 `--current-screen --label Banana --shown-after Banana --hidden-after Apple --closes-option-list`。后一步确认可选的 Banana 节点消失，同时选择值仍可见。

Combobox 使用 `--route combobox --role '' --require-clickable`。先传 `--label 'Show suggestions' --shown-after SvelteKit`，再传 `--current-screen --label SvelteKit --shown-after SvelteKit --hidden-after Next.js --closes-option-list` 核对写回的值及列表关闭。

Questionnaire 使用 `--route questionnaire --role ''`。用 `--label 'Tool call timeline' --require-checkable` 选答。之后每步携带 `--current-screen`，对 Next 和 Previous 分别用 `--shown-after 'Question 2 of 3'` 和 `--shown-after 'Question 1 of 3'` 验证切题。最后用 `--label 'Tool call timeline' --focus-only --focused-checked true` 验证答案保留。

复验 Carousel 时，用 `--route carousel --label 'Next slide' --role android.widget.Button --require-clickable --shown-after 'Slide 2 of 5' --hidden-after 'Slide 1 of 5'` 开始。后续用 `--current-screen` 保持当前页，更新新旧页标签。到达两端时分别加上 `--disabled-after 'Next slide'` 或 `--disabled-after 'Previous slide'`，再聚焦另一侧按钮换向。

复验 Calendar 时，先用 `--route calendar --label 'Next month' --role '' --require-clickable` 双击翻月。可用 `--shown-after` 和 `--hidden-after` 分别检查下个月及当月的 15 日。随后加 `--current-screen`，用下个月 15 日的 `年-月-日` 标签和 `--require-checkable` 核对双击后的选中状态。

静态内容使用 `--focus-only` 检查 TalkBack 手势是否能聚焦目标。加 `--exact-focus` 可要求标签和角色位于实际聚焦的节点。使用 `--role ''` 可只按名称匹配。分隔线示例使用 `--label Separator --role android.view.View` 定位分隔线自身。

Progress 默认示例可用 `--route progress --label '' --role android.widget.ProgressBar --focus-only --exact-focus` 复验。`focusedState` 和 `focusedRange` 记录实际焦点节点的状态文字和最小值、当前值、最大值。打开 TalkBack 的语音文字显示再传入 `--output` 可同时保存朗读字幕。

滑块可用 `--route slider --label '' --role android.widget.SeekBar --adjust-range` 复验。脚本通过 TalkBack 的上下滑手势调整值，并从当前聚焦节点读取调整前后的范围值。[TalkBack 手势说明](https://support.google.com/accessibility/android/answer/6151827)

Resizable 用 `--route resizable --label 'Resize panels horizontally' --role android.widget.SeekBar --adjust-range` 复验分隔线焦点和双向范围调节。

开关可用 `--route switch --label 'Airplane Mode' --role '' --require-checkable` 复验。脚本要求焦点落在可切换节点，允许名称位于其语义子节点，并核对双击后的勾选状态。

关联标签用 `--route label --label 'Accept terms and conditions' --role android.widget.CheckBox --require-checkable` 核对复选框名称、焦点及勾选变化。

Toggle Group 用 `--route toggle-group --label 'Toggle italic' --role '' --focused-checked false` 验证选中。保留当前页，传 `--current-screen --focused-checked true` 复验双击后取消选中。

Chip 先滚动到 Selectable 示例，使其 Kotlin 成为第一个可见的同名控件。用 `--route chip --current-screen --label Kotlin --role '' --focused-checked false` 验证选中，用 `--focused-checked true` 再运行一次验证取消。

Command 用 `--route command --label Calendar --role '' --require-clickable --shown-after 'Selected: Calendar'` 核对建议项执行结果。

Code Block 用 `--route code-block --label 'Wrap lines' --role '' --require-clickable --state-after 'Wrap lines' Pressed` 核对启用换行后的语义状态。

Empty 和 Item 可分别用 `--route empty --label 'Create Project' --role '' --require-clickable` 和 `--route item --label Action --role '' --require-clickable` 复验双击事件。Card 用 `--route card --label m@example.com --role android.widget.EditText --type-text auditcard` 复验编辑。

Stepper 用 `--route stepper --role '' --require-clickable --label Next --state-after 2 Current` 验证首次前进。后续保持当前页，第二次 Next 用 `--state-after 3 Current`，末步再次激活增加 `--state-unchanged`，最后用 `--label Back --state-after 2 Current` 复验回退。

编辑用例使用 `--role android.widget.EditText`。脚本先用 TalkBack 聚焦并双击，再发送键盘输入，最后从同一个输入节点读取值。密码框用 `--route input --label sk-... --type-text registry42 --masked`，验证码用 `--route input-otp --label 123456 --type-text 2468109 --expected-edit-text 246810`。

文本域用 `--route textarea --label 'Type your message here.' --type-text registrymessage`。卡片姓名用 `--route field --label 'Evil Rabbit' --type-text RegistryTester`。

Input Group 的金额用例先滚到 Enter amount 显示在视口内，再用 `--route input-group --current-screen --label 'Enter amount' --type-text 42`。默认搜索框可见于无障碍树，试验遍历分组及将标签直接附到编辑框均未解决 TalkBack 焦点问题，试改已撤回。

对于需要先调整视口的示例，可定位后传入 `--current-screen`。Pagination 的 TalkBack 验收先滑动 Controlled 示例的单行，使 Next 显示在视口内，再按 `--current-screen --route pagination --label 'Go to next page' --role android.widget.Button` 聚焦并激活。

```sh
python3 shadcn/registry/validation/verify_android_talkback.py \
  --sdk "$ANDROID_HOME" --device emulator-5580
python3 shadcn/registry/validation/verify_android_talkback.py \
  --sdk "$ANDROID_HOME" --device emulator-5580 \
  --route radio-group --label Compact --role android.widget.RadioButton --position 2 0
python3 shadcn/registry/validation/verify_android_talkback.py \
  --sdk "$ANDROID_HOME" --device emulator-5580 \
  --route aspect-ratio --label Photo --role android.widget.ImageView --focus-only
```
