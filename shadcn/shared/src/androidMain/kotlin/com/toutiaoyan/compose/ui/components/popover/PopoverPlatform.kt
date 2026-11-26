package com.toutiaoyan.compose.ui.components.popover

import android.os.Build
import android.view.KeyEvent
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.PopupProperties

@Composable
internal actual fun PopoverWindowEscape(onEscape: () -> Unit) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
    val owner = LocalView.current.rootView
    val dismiss = rememberUpdatedState(onEscape)
    DisposableEffect(owner) {
        // After a non-modal touch open, neither the trigger nor the popup may own keyboard focus.
        val listener =
            View.OnUnhandledKeyEventListener { _, event ->
                val escapeKey = event.keyCode == KeyEvent.KEYCODE_ESCAPE
                val firstEscape = escapeKey && event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0
                if (firstEscape) dismiss.value()
                firstEscape
            }
        owner.addOnUnhandledKeyEventListener(listener)
        onDispose { owner.removeOnUnhandledKeyEventListener(listener) }
    }
}

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
