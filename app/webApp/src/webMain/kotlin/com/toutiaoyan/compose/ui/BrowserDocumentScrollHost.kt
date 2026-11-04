package com.toutiaoyan.compose.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.snapshotFlow
import com.toutiaoyan.compose.ui.catalog.DocumentScrollHost
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import kotlin.math.roundToInt

/** Browser chrome owns the scrollbar; Compose remains the owner of document position. */
internal class BrowserDocumentScrollHost : DocumentScrollHost {
    override suspend fun bind(
        state: ScrollState?,
        pixelsPerCssPixel: Float,
    ): Unit =
        coroutineScope {
            val root = document.documentElement as HTMLElement
            val body = checkNotNull(document.body)
            if (state == null) {
                root.style.overflowY = "hidden"
                body.style.height = "100%"
                window.scrollTo(0.0, 0.0)
                awaitCancellation()
            }
            root.style.overflowY = "scroll"
            // A fresh ScrollState has no measured extent yet. Do not overwrite a restored position
            // with the browser's previous route position while waiting for the first measurement.
            snapshotFlow { state.maxValue }.first { it != Int.MAX_VALUE }

            fun updateBrowser() {
                body.style.height = "calc(100vh + ${state.maxValue / pixelsPerCssPixel}px)"
                val desired = state.value / pixelsPerCssPixel.toDouble()
                if (kotlin.math.abs(window.scrollY - desired) > .5 / pixelsPerCssPixel) {
                    window.scrollTo(0.0, desired)
                }
            }
            val browserScrolls = Channel<Int>(Channel.CONFLATED)
            val listener: (Event) -> Unit = {
                browserScrolls.trySend((window.scrollY * pixelsPerCssPixel).roundToInt())
            }
            window.addEventListener("scroll", listener)
            try {
                // Install both directions before exposing the first browser scroll extent.
                launch(start = CoroutineStart.UNDISPATCHED) {
                    snapshotFlow { state.maxValue to state.value }.collect { updateBrowser() }
                }
                browserScrolls.receiveAsFlow().collectLatest { target ->
                    if (state.value != target) state.scrollTo(target.coerceIn(0, state.maxValue))
                }
            } finally {
                window.removeEventListener("scroll", listener)
                browserScrolls.close()
            }
        }
}
