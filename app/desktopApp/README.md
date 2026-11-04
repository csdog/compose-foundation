# Desktop catalog

The desktop launcher uses the web catalog’s Kotlin-gradient favicon for the window, Dock/taskbar, system tray and native packages. The master PNG is `src/main/resources/icons/app.png` (1024×1024 RGBA, rendered from `../webApp/src/webMain/resources/favicon.svg`). Packaging also requires `icons/app.icns` on macOS and `icons/app.ico` on Windows.

Use the native **设置** menu:

- **显示托盘图标** enables/disables the system tray immediately.
- **关闭窗口时最小化到托盘** hides the window while preserving its navigation and component state.
- **退出** quits the application, including while it is in the tray.

Both settings default to enabled and persist through Java Preferences under `com/toutiaoyan/compose/foundation/desktop`. Disabling the tray makes window close exit normally; unsupported platforms also exit normally and disable these menu controls. The tray provides Open, close-to-tray and Exit actions. Double-click/action behavior follows the platform’s Compose Tray implementation; on macOS use its menu to reopen. Dock reopening and inbound deep links also restore the window.

Implementation follows the official [tray guide](https://kotlinlang.org/docs/multiplatform/compose-desktop-tray.html), [menu guide](https://kotlinlang.org/docs/multiplatform/compose-desktop-menu-bar.html), and [native packaging guide](https://kotlinlang.org/docs/multiplatform/compose-native-distribution.html).

Validation: `:app:desktopApp:test` covers persistence across newly constructed settings instances and independence of the two options. `:app:desktopApp:createDistributable` builds the macOS application directory; the generated ICNS decodes through AppKit and the bundle references that icon. The generated application was launched on macOS without a Java/Compose startup exception. Full manual tray/close/reopen interaction and Windows/Linux runtime checks remain outstanding.

Confirmed scope recheck (2026-10-31): `:app:desktopApp:test` and `:app:desktopApp:ktlintCheck` pass. This recheck does not replace the pending manual tray/close/reopen interaction checks above.
