package com.toutiaoyan.compose.ui.components.menubar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.browser.window
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.Event
import org.w3c.dom.pointerevents.PointerEvent
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.get

/** A focusable popup can receive pointer events aimed outside its own panel on Web. */
internal actual class MenubarPointerSurface actual constructor() {
    private var rootCanvas: HTMLCanvasElement? = null

    actual fun observeRoot(modifier: Modifier): Modifier =
        modifier.pointerInput(this) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    (event.nativeEvent as? Event)?.canvas()?.let { rootCanvas = it }
                }
            }
        }

    @Composable
    actual fun ObserveOpen(onMove: (Offset) -> Unit) {
        val latest = rememberUpdatedState(onMove)
        DisposableEffect(this) {
            val pointer: (Event) -> Unit = listener@{ event ->
                val move = event as? PointerEvent ?: return@listener
                if (move.pointerType != "mouse" || move.buttons.toInt() != 0) return@listener
                val canvas = rootCanvas ?: move.canvas() ?: return@listener
                val rect = canvas.getBoundingClientRect()
                if (rect.width <= 0.0 || rect.height <= 0.0) return@listener
                latest.value(
                    Offset(
                        ((move.clientX - rect.left) * canvas.width / rect.width).toFloat(),
                        ((move.clientY - rect.top) * canvas.height / rect.height).toFloat(),
                    ),
                )
            }
            window.addEventListener("pointermove", pointer, true)
            onDispose { window.removeEventListener("pointermove", pointer, true) }
        }
    }
}

// Window listeners see the Compose shadow host as the target.
@OptIn(ExperimentalWasmJsInterop::class)
private fun Event.canvas(): HTMLCanvasElement? = composedPath()[0] as? HTMLCanvasElement
