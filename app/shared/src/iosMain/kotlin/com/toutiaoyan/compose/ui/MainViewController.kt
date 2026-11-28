package com.toutiaoyan.compose.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.toutiaoyan.compose.ui.components.sheet.Sheet
import com.toutiaoyan.compose.ui.components.sheet.SheetContent
import com.toutiaoyan.compose.ui.components.sheet.SheetHeader
import com.toutiaoyan.compose.ui.components.sheet.SheetSide
import com.toutiaoyan.compose.ui.components.sheet.SheetTrigger
import com.toutiaoyan.compose.ui.navigation.InboundDeepLinks
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlinx.coroutines.delay

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

fun sheetAnimationViewController(markTrigger: () -> Unit) =
    ComposeUIViewController {
        var opened by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            delay(6000)
            markTrigger()
            opened = true
        }
        Theme(preset = StylePreset.Nova, isDark = false) {
            Box(Modifier.fillMaxSize()) {
                Sheet(open = opened, onOpenChange = { opened = it }) {
                    SheetTrigger("Edit profile")
                    SheetContent(side = SheetSide.End) {
                        SheetHeader("Profile", description = "Update your profile details.")
                    }
                }
            }
        }
    }
