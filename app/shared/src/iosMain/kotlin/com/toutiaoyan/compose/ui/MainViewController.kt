package com.toutiaoyan.compose.ui

import androidx.compose.ui.window.ComposeUIViewController
import com.toutiaoyan.compose.ui.navigation.InboundDeepLinks

private val iosInboundDeepLinks = InboundDeepLinks()

/** Called from Swift `onOpenURL` to hand the URI to [App]. */
fun offerIosInboundDeepLink(uri: String) {
    iosInboundDeepLinks.offer(uri)
}

@Suppress("ktlint:standard:function-naming")
fun MainViewController() =
    ComposeUIViewController {
        App(inboundDeepLinks = iosInboundDeepLinks.events)
    }
