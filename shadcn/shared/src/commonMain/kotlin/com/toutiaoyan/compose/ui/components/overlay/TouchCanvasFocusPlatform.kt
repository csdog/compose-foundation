package com.toutiaoyan.compose.ui.components.overlay

import androidx.compose.ui.input.pointer.PointerEvent

internal expect fun captureTouchCanvasFocus(event: PointerEvent): (() -> Unit)?
