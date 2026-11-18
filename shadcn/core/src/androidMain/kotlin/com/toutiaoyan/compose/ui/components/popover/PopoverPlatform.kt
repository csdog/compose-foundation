package com.toutiaoyan.compose.ui.components.popover

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.PopupProperties

@Composable
internal actual fun PopoverWindowEscape(onEscape: () -> Unit) = Unit

internal actual fun popoverPopupProperties(
    modal: Boolean,
    open: Boolean,
    dismissOnClickOutside: Boolean,
    focusPanel: Boolean,
): PopupProperties =
    PopupProperties(
        focusable = modal && focusPanel,
        dismissOnBackPress = modal && focusPanel,
        dismissOnClickOutside = open && dismissOnClickOutside,
    )
