package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import com.toutiaoyan.compose.ui.catalog.DocumentScrollHost
import com.toutiaoyan.compose.ui.catalog.LocalDocumentScrollHost

@Composable
internal fun emptyLinkSpaceModifier(): Modifier {
    val host = LocalDocumentScrollHost.current
    val focus = remember { EmptyLinkSpaceFocus() }
    DisposableEffect(host) {
        focus.bind(host)
        onDispose { focus.bind(null) }
    }
    return Modifier.onFocusChanged { focus.onFocusChanged(it.hasFocus) }
}

private class EmptyLinkSpaceFocus {
    private var host: DocumentScrollHost? = null
    private var focused = false
    private var release: (() -> Unit)? = null

    fun bind(host: DocumentScrollHost?) {
        this.host = host
        updateCapture()
    }

    fun onFocusChanged(focused: Boolean) {
        if (this.focused == focused) return
        this.focused = focused
        updateCapture()
    }

    private fun updateCapture() {
        release?.invoke()
        release = if (focused) host?.captureNativeLinkSpace() else null
    }
}
