package com.toutiaoyan.compose.ui.components.navigationmenu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.browser.window
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent

// A pointer-opened viewport does not take focus, so it cannot receive Escape itself.
@Composable
internal actual fun NavigationMenuWindowEscape(onEscape: () -> Unit) {
    val latestDismiss = rememberUpdatedState(onEscape)
    DisposableEffect(Unit) {
        val listener: (Event) -> Unit = { event ->
            val keyEvent = event as KeyboardEvent
            if (keyEvent.key == "Escape" && !keyEvent.repeat && !keyEvent.defaultPrevented) {
                keyEvent.preventDefault()
                latestDismiss.value()
            }
        }
        window.addEventListener("keydown", listener)
        onDispose { window.removeEventListener("keydown", listener) }
    }
}
