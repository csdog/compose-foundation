package com.toutiaoyan.compose.ui.components.overlay

import androidx.compose.ui.input.pointer.PointerEvent

internal actual fun captureTouchCanvasFocus(event: PointerEvent): (() -> Unit)? = null
