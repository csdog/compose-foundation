package com.toutiaoyan.compose.ui.components.popover

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.PopupProperties
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.browser.window
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent

private val openPopovers = mutableListOf<() -> Unit>()

private val escapeListener: (Event) -> Unit = { event ->
    val keyEvent = event as KeyboardEvent
    if (keyEvent.key == "Escape" && !keyEvent.repeat && !keyEvent.defaultPrevented) {
        openPopovers.lastOrNull()?.let { dismiss ->
            keyEvent.preventDefault()
            dismiss()
        }
    }
}

// Browsers can leave focus on the document body after opening a Compose popup by touch.
@Composable
internal actual fun PopoverWindowEscape(onEscape: () -> Unit) {
    val latestDismiss = rememberUpdatedState(onEscape)
    DisposableEffect(Unit) {
        val dismiss = { latestDismiss.value() }
        if (openPopovers.isEmpty()) window.addEventListener("keydown", escapeListener)
        openPopovers.add(dismiss)
        onDispose {
            openPopovers.remove(dismiss)
            if (openPopovers.isEmpty()) window.removeEventListener("keydown", escapeListener)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
internal actual fun popoverPopupProperties(
    modal: Boolean,
    open: Boolean,
    dismissOnClickOutside: Boolean,
    focusPanel: Boolean,
): PopupProperties =
    PopupProperties(
        focusable = focusPanel,
        dismissOnBackPress = focusPanel,
        dismissOnClickOutside = open && dismissOnClickOutside,
        consumePointerInputOutside = open && modal,
    )
