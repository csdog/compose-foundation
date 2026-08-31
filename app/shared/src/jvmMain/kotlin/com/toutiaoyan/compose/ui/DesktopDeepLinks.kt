package com.toutiaoyan.compose.ui

import com.toutiaoyan.compose.ui.navigation.DeepLinks
import com.toutiaoyan.compose.ui.navigation.InboundDeepLinks
import java.awt.Desktop

/** Passed into [App] from desktop `main`. */
val desktopInboundDeepLinks = InboundDeepLinks()

internal fun offerDesktopInboundDeepLink(uri: String) {
    desktopInboundDeepLinks.offer(uri)
}

fun registerDesktopOpenUriHandler() {
    if (!Desktop.isDesktopSupported()) return
    try {
        Desktop.getDesktop().setOpenURIHandler {
            offerDesktopInboundDeepLink(it.uri.toString())
        }
    } catch (_: UnsupportedOperationException) {
        // Some JDK / OS combinations do not support APP_OPEN_URI.
    }
}

fun emitStartupDeepLinksFromArgs(args: Array<out String>) {
    args.forEach { arg ->
        val trimmed = arg.trim()
        if (trimmed.startsWith("${DeepLinks.SCHEME}://")) {
            offerDesktopInboundDeepLink(trimmed)
        }
    }
}
