package com.toutiaoyan.compose.ui.components.toast

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEvent

internal actual fun captureToastOutsideTouch(touch: PointerEvent): ToastOutsideTouch? = null

@Composable
internal actual fun Modifier.toastViewportShortcut(enabled: Boolean): Modifier = this
