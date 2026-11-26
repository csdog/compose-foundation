package com.toutiaoyan.compose.ui.components.overlay

import androidx.compose.foundation.focusGroup
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged

internal class OverlayFocusState {
    var presented by mutableStateOf(false)
    var returnTo: FocusRequester? = null
}

internal val LocalOverlayFocus = staticCompositionLocalOf<OverlayFocusState?> { null }
internal val LocalOverlayModal = staticCompositionLocalOf { true }

@Composable
internal fun OverlayFocusOwner(
    open: Boolean,
    modal: Boolean,
    content: @Composable () -> Unit,
) {
    val state = remember { OverlayFocusState() }
    var wasPresented by remember { mutableStateOf(false) }
    LaunchedEffect(state.presented, open) {
        if (state.presented) wasPresented = true
        if (!state.presented && !open && wasPresented) {
            withFrameNanos { }
            state.returnTo?.requestFocus()
            wasPresented = false
        }
    }
    CompositionLocalProvider(LocalOverlayFocus provides state, LocalOverlayModal provides modal, content = content)
}

@Composable
internal fun Modifier.overlayTriggerFocus(): Modifier {
    val state = LocalOverlayFocus.current ?: return this
    val requester = remember { FocusRequester() }
    DisposableEffect(state, requester) { onDispose { if (state.returnTo === requester) state.returnTo = null } }
    return focusRequester(requester).onFocusChanged { if (it.hasFocus) state.returnTo = requester }.focusGroup()
}
