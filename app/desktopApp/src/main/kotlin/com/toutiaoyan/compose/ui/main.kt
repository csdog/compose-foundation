package com.toutiaoyan.compose.ui

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main(args: Array<String>) {
    registerDesktopOpenUriHandler()
    emitStartupDeepLinksFromArgs(args)
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "shadcn-compose",
        ) {
            App(inboundDeepLinks = desktopInboundDeepLinks.events)
        }
    }
}
