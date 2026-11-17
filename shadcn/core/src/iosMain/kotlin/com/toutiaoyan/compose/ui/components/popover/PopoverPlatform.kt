package com.toutiaoyan.compose.ui.components.popover

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.window.PopupProperties

@Composable
internal actual fun PopoverWindowEscape(onEscape: () -> Unit) = Unit

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

internal actual fun popoverTouchHostFocus(event: PointerEvent): (() -> Unit)? = null
