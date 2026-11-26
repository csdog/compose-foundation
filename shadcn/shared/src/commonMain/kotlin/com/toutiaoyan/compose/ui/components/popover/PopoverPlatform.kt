package com.toutiaoyan.compose.ui.components.popover

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.PopupProperties

@Composable
internal expect fun PopoverWindowEscape(onEscape: () -> Unit)

internal expect fun popoverPopupProperties(
    modal: Boolean,
    open: Boolean,
    dismissOnClickOutside: Boolean,
    focusPanel: Boolean,
): PopupProperties
