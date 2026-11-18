package com.toutiaoyan.compose.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.snapshotFlow
import com.toutiaoyan.compose.ui.catalog.DocumentScrollHost
import com.toutiaoyan.compose.ui.catalog.DocumentScrollBridge
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

/** Browser chrome owns the scrollbar; Compose remains the owner of document position. */
internal class BrowserDocumentScrollHost(
    private val history: BrowserDocHistory,
    private val positionToRestore: () -> Double?,
) : DocumentScrollHost {
    override suspend fun bind(
        state: ScrollState?,
        pixelsPerCssPixel: Float,
    ): Unit =
        coroutineScope {
            val entryId = history.entryId
            val restorePosition = positionToRestore()
            val root = document.documentElement as HTMLElement
            val body = checkNotNull(document.body)

            fun setDocumentOverflow(value: String) {
                val previousWidth = root.clientWidth
                root.style.overflowY = value
                // ComposeViewport observes window resize, not scrollbar-driven container resizing.
                if (root.clientWidth != previousWidth) window.dispatchEvent(Event("resize"))
            }
            if (state == null) {
                setDocumentOverflow("hidden")
                body.style.height = "100%"
                window.scrollTo(0.0, 0.0)
                awaitCancellation()
            }
            setDocumentOverflow("scroll")
            // A fresh ScrollState has no measured extent yet. Do not overwrite a restored position
            // with the browser's previous route position while waiting for the first measurement.
            snapshotFlow { state.maxValue }.first { it != Int.MAX_VALUE }
            val bridge = DocumentScrollBridge(state, pixelsPerCssPixel, restorePosition)
            bridge.updateCompose()
            history.bindPosition(entryId) {
                if (history.ownsCurrentEntry(entryId)) {
                    bridge.positionForCapture(window.scrollY)
                } else {
                    bridge.positionForDeparture()
                }
            }

            fun updateBrowser() {
                if (entryId != null && !history.ownsCurrentEntry(entryId)) return
                body.style.height = "calc(100vh + ${state.maxValue / pixelsPerCssPixel}px)"
                if (!bridge.canPublishCompose()) return
                val desired = state.value / pixelsPerCssPixel.toDouble()
                if (kotlin.math.abs(window.scrollY - desired) > .5 / pixelsPerCssPixel) {
                    window.scrollTo(0.0, desired)
                }
                bridge.publishedNative(window.scrollY)
            }
            val browserScrolls = Channel<Int>(Channel.CONFLATED)
            val listener: (Event) -> Unit = {
                if (entryId == null || history.ownsCurrentEntry(entryId)) {
                    bridge.nativeScroll(window.scrollY)?.let { browserScrolls.trySend(it) }
                }
            }
            val pageHideListener: (Event) -> Unit = { history.capturePosition(persistForReload = true) }
            window.addEventListener("scroll", listener)
            window.addEventListener("pagehide", pageHideListener)
            try {
                // Install both directions before exposing the first browser scroll extent.
                launch(start = CoroutineStart.UNDISPATCHED) {
                    snapshotFlow { Triple(state.maxValue, state.value, state.isScrollInProgress) }.collect {
                        bridge.updateCompose()
                        updateBrowser()
                    }
                }
                browserScrolls.receiveAsFlow().collectLatest { target ->
                    if (state.value != target) state.scrollTo(target.coerceIn(0, state.maxValue))
                }
            } finally {
                history.unbindPosition(entryId)
                window.removeEventListener("scroll", listener)
                window.removeEventListener("pagehide", pageHideListener)
                browserScrolls.close()
            }
        }
}
