package com.toutiaoyan.compose.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.isTraySupported
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.flow.onEach
import java.awt.Desktop
import java.awt.Taskbar
import java.awt.desktop.AppReopenedListener
import javax.imageio.ImageIO
import javax.swing.SwingUtilities

fun main(args: Array<String>) {
    registerDesktopOpenUriHandler()
    emitStartupDeepLinksFromArgs(args)
    application { DesktopApplication() }
}

@Composable
private fun ApplicationScope.DesktopApplication() {
    val settings = remember { DesktopSettings() }
    val windowState = rememberWindowState()
    var windowVisible by remember { mutableStateOf(true) }
    var foregroundRequest by remember { mutableStateOf(0) }
    val icon =
        remember {
            BitmapPainter(
                ImageIO.read(checkNotNull(DesktopSettings::class.java.getResource("/icons/app.png")))
                    .toComposeImageBitmap(),
            )
        }

    fun showWindow() {
        SwingUtilities.invokeLater {
            windowVisible = true
            windowState.isMinimized = false
            foregroundRequest++
        }
    }
    // Keep a single deep-link collector in App; observing must not steal channel events.
    val inboundLinks = remember { desktopInboundDeepLinks.events.onEach { showWindow() } }
    DesktopLifecycleEffects(::showWindow)
    val trayVisible = isTraySupported && settings.showTray
    if (trayVisible) {
        Tray(icon = icon, tooltip = "Compose Foundation", onAction = ::showWindow) {
            Item("打开 Compose Foundation", onClick = ::showWindow)
            Separator()
            CheckboxItem(
                "关闭窗口时最小化到托盘",
                checked = settings.closeToTray,
                onCheckedChange = settings::updateCloseToTray,
            )
            Separator()
            Item("退出", onClick = ::exitApplication)
        }
    }
    Window(
        onCloseRequest = { if (trayVisible && settings.closeToTray) windowVisible = false else exitApplication() },
        state = windowState,
        visible = windowVisible,
        title = "Compose Foundation",
        icon = icon,
    ) {
        LaunchedEffect(foregroundRequest) {
            if (windowVisible) {
                window.toFront()
                window.requestFocus()
            }
        }
        DesktopSettingsMenu(settings, trayVisible, ::exitApplication)
        App(inboundDeepLinks = inboundLinks)
    }
}

@Composable
private fun FrameWindowScope.DesktopSettingsMenu(
    settings: DesktopSettings,
    trayVisible: Boolean,
    onExit: () -> Unit,
) {
    MenuBar {
        Menu("设置") {
            CheckboxItem(
                "显示托盘图标",
                checked = settings.showTray,
                onCheckedChange = settings::updateShowTray,
                enabled = isTraySupported,
            )
            CheckboxItem(
                "关闭窗口时最小化到托盘",
                checked = settings.closeToTray,
                onCheckedChange = settings::updateCloseToTray,
                enabled = trayVisible,
            )
            Separator()
            Item("退出", onClick = onExit)
        }
    }
}

@Composable
private fun DesktopLifecycleEffects(onShowWindow: () -> Unit) {
    val showWindow by rememberUpdatedState(onShowWindow)
    DisposableEffect(Unit) {
        val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
        val reopen = AppReopenedListener { showWindow() }
        val canReopen = desktop?.isSupported(Desktop.Action.APP_EVENT_REOPENED) == true
        if (canReopen) desktop.addAppEventListener(reopen)
        if (Taskbar.isTaskbarSupported()) {
            val taskbar = Taskbar.getTaskbar()
            if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                taskbar.iconImage = ImageIO.read(DesktopSettings::class.java.getResource("/icons/app.png"))
            }
        }
        onDispose { if (canReopen) desktop.removeAppEventListener(reopen) }
    }
}
