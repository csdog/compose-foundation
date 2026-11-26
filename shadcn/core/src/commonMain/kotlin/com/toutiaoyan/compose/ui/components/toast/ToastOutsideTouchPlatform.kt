package com.toutiaoyan.compose.ui.components.toast

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEvent

internal expect fun captureToastOutsideTouch(touch: PointerEvent): ToastOutsideTouch?

internal expect fun preventToastTouchScroll(touch: PointerEvent): (() -> Unit)?

@Composable
internal expect fun Modifier.toastViewportShortcut(enabled: Boolean): Modifier
