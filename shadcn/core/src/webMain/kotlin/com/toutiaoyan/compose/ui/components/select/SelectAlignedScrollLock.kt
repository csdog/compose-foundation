package com.toutiaoyan.compose.ui.components.select

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalDensity
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent

@Composable
internal actual fun SelectAlignedScrollLock(popupBounds: () -> Rect?) {
    val latestBounds by rememberUpdatedState(popupBounds)
    val density = LocalDensity.current.density
    DisposableEffect(density) {
        // Keep the viewport width unchanged and leave the host's inline scroll styles intact.
        val lockStyle = document.createElement("style")
        lockStyle.textContent = ":root { overflow-y: hidden !important; scrollbar-gutter: stable; }"
        document.head?.appendChild(lockStyle)
        val wheelListener: (Event) -> Unit = { event ->
            val mouse = event as MouseEvent
            val point = Offset(mouse.offsetX.toFloat(), mouse.offsetY.toFloat()) * density
            // An outside wheel can also reach a Compose document scroller.
            if (latestBounds()?.contains(point) != true) event.stopPropagation()
        }
        window.addEventListener("wheel", wheelListener, true)
        onDispose {
            window.removeEventListener("wheel", wheelListener, true)
            lockStyle.parentNode?.removeChild(lockStyle)
        }
    }
}
