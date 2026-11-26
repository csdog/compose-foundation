package com.toutiaoyan.compose.ui.components.hovercard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.browser.window
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent

// A hover preview stays non-focusable, so its root cannot see Escape when focus is elsewhere.
private class EscapeHandler(
    val pointerOverCard: () -> Boolean,
    val dismiss: () -> Unit,
)

private val escapeHandlers = mutableListOf<EscapeHandler>()

private fun Event.isEscape() = (this as KeyboardEvent).let { it.key == "Escape" && !it.repeat && !it.defaultPrevented }

// Give a card under the pointer priority over the focused ancestor, including a dialog.
private val hoverEscapeListener: (Event) -> Unit = { event ->
    if (event.isEscape()) {
        escapeHandlers.lastOrNull { it.pointerOverCard() }?.let {
            event.preventDefault()
            event.stopPropagation()
            it.dismiss()
        }
    }
}

// Outside the hovered card, let the focused layer handle Escape first.
private val outsideEscapeListener: (Event) -> Unit = { event ->
    if (event.isEscape()) escapeHandlers.lastOrNull()?.dismiss?.invoke()
}

@Composable
internal actual fun HoverCardWindowEscape(
    pointerOverCard: Boolean,
    onEscape: () -> Unit,
) {
    val latestHover = rememberUpdatedState(pointerOverCard)
    val latestDismiss = rememberUpdatedState(onEscape)
    DisposableEffect(Unit) {
        val handler = EscapeHandler({ latestHover.value }, { latestDismiss.value() })
        if (escapeHandlers.isEmpty()) {
            window.addEventListener("keydown", hoverEscapeListener, true)
            window.addEventListener("keydown", outsideEscapeListener)
        }
        escapeHandlers.add(handler)
        onDispose {
            escapeHandlers.remove(handler)
            if (escapeHandlers.isEmpty()) {
                window.removeEventListener("keydown", hoverEscapeListener, true)
                window.removeEventListener("keydown", outsideEscapeListener)
            }
        }
    }
}
