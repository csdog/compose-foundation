package com.toutiaoyan.compose.ui.components.overlay

import androidx.compose.ui.input.pointer.PointerEvent
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.Event

internal actual fun captureTouchCanvasFocus(event: PointerEvent): (() -> Unit)? {
    val canvas = (event.nativeEvent as? Event)?.target as? HTMLCanvasElement ?: return null
    return {
        if (document.activeElement === document.body && canvas.isConnected) canvas.focus()
    }
}
