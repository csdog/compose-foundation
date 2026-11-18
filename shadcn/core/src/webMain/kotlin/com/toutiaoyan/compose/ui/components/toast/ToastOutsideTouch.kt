package com.toutiaoyan.compose.ui.components.toast

import androidx.compose.foundation.focusable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.platform.LocalInputModeManager
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.pointerevents.PointerEvent as DomPointerEvent

internal actual fun captureToastOutsideTouch(touch: PointerEvent): ToastOutsideTouch? {
    val canvas = (touch.nativeEvent as? Event)?.target as? HTMLCanvasElement ?: return null
    return ToastOutsideTouch { bounds, onOutside ->
        val listener: (Event) -> Unit = pointerDown@{ event ->
            val press = event as? DomPointerEvent ?: return@pointerDown
            if (press.pointerType != "touch") return@pointerDown
            val viewport = bounds() ?: return@pointerDown
            val canvasBounds = canvas.getBoundingClientRect()
            if (canvasBounds.width <= 0.0 || canvasBounds.height <= 0.0) return@pointerDown
            val position =
                Offset(
                    ((press.clientX - canvasBounds.left) * canvas.width / canvasBounds.width).toFloat(),
                    ((press.clientY - canvasBounds.top) * canvas.height / canvasBounds.height).toFloat(),
                )
            if (!viewport.contains(position)) onOutside()
        }
        document.addEventListener("pointerdown", listener, true)
        return@ToastOutsideTouch { document.removeEventListener("pointerdown", listener, true) }
    }
}

@Composable
internal actual fun Modifier.toastViewportShortcut(enabled: Boolean): Modifier {
    if (!enabled) return this
    val viewportFocus = remember { FocusRequester() }
    val inputModeManager = LocalInputModeManager.current
    DisposableEffect(viewportFocus, inputModeManager) {
        val listener: (Event) -> Unit = keyDown@{ event ->
            val key = event as? KeyboardEvent ?: return@keyDown
            if (key.key != "F6" || key.repeat || key.defaultPrevented) return@keyDown
            if (key.altKey || key.ctrlKey || key.metaKey || key.shiftKey) return@keyDown
            if (viewportFocus.requestFocus()) {
                inputModeManager.requestInputMode(InputMode.Keyboard)
                key.preventDefault()
            }
        }
        document.addEventListener("keydown", listener, true)
        onDispose { document.removeEventListener("keydown", listener, true) }
    }
    return focusRequester(viewportFocus).focusable()
}
