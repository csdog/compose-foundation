# Shared Web theme persistence

修复r7–r11反复记录的整页重载丢失手动主题：App过去仅remember内存override。官网[实测](browser/official-reload.json)的theme=dark及root.dark在不同timeOrigin的整页刷新后保留；本地原失效证据仍留[r11](../accordion-r11/browser/review.json)。

App增加可选启动主题和显式切换回调，仍是UI状态唯一所有者。Web入口在挂载前读取独立键compose-foundation.theme，只接受dark/light；没有或无效记录继续跟随系统。点击切换才写入，不在重组或恢复时重复写。读取/写入异常被隔离，页面仍能以内存状态切换。其他宿主继续使用原默认参数，没有引入原生偏好存储。

[验证](verification/summary.json)：新应用重建/像素/回调测试1 + 导航6 + 目录预览5 = 12项独立通过，shared/webApp两个ktlint通过（22.384s）；JS编译与Wasm开发distribution通过（18.809s），保留既有Text.kt、AGP/Gradle及webpack warning。5项输入SHA一致，工作流已正常结束并清理日志；未跑旧golden或全仓提交门禁，未新增commit。

新app42ea70f6b20f651c3bc8.wasm实际加载；六个常规浏览器状态逐个保存timeOrigin、实际存储值、系统主题、视口和三处页面颜色。无记录随系统深色且不写入、手动浅色覆盖系统深色并在重载/窄屏保留、手动深色覆盖系统浅色、Accordion→Chart→Accordion整页导航保留均通过。桌面1440/窄屏390的浅深四象限已查看；390为桌面Chromium，不是真机触摸。

另在p2临时用preload getter模拟SecurityError，验证读取失败使用系统浅色、写入失败仍能手动切深、重载后继续按系统；最后移除hook并重载，原有dark记录未变且恢复成功。未清除任何存储键。四个降级状态与六个常规状态共10张截图全部已目视复查，见[浏览器记录](browser/review.json)。JS目标仅编译验证，不冒称独立JS运行时验收；不扩大为所有组件完整视觉通过。

本轮关闭共享的Web手动主题持久化缺口。其余组件清单、列宽/深色字形/代码面板及扩展交互等差异继续按主进度追踪；Editor排除、Attachment后置。
