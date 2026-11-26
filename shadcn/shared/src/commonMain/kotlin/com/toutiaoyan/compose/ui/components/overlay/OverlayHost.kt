package com.toutiaoyan.compose.ui.components.overlay

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer

internal class OverlayHostState(val background: GraphicsLayer) {
    val portals = mutableStateMapOf<Any, @Composable () -> Unit>()
    val outside = mutableMapOf<Any, OverlayOutsideEntry>()
    val branches = mutableSetOf<OverlayHostState>()
}

internal val LocalOverlayHost = staticCompositionLocalOf<OverlayHostState?> { null }

/**
 * Place once at the window root. Records the underlying content for Nova's 4dp
 * backdrop blur and hosts non-modal portals without making the window inert.
 * Without a host, modal components still work using their platform Popup.
 */
@Composable
fun OverlayHost(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val layer = rememberGraphicsLayer()
    val host = remember(layer) { OverlayHostState(layer) }
    CompositionLocalProvider(LocalOverlayHost provides host) {
        Box(modifier.fillMaxSize().observeOverlayOutside(host)) {
            Box(
                Modifier.fillMaxSize().drawWithContent {
                    layer.record { this@drawWithContent.drawContent() }
                    drawLayer(layer)
                },
            ) { content() }
            host.portals.forEach { (id, portal) -> key(id) { portal() } }
        }
    }
}

@Composable
internal fun HostPortal(
    host: OverlayHostState,
    content: @Composable () -> Unit,
) {
    val id = remember { Any() }
    val latest by rememberUpdatedState(content)
    val locals = currentCompositionLocalContext
    val portal = remember(locals) { @Composable { CompositionLocalProvider(locals) { latest() } } }
    DisposableEffect(host, id) {
        host.portals[id] = portal
        onDispose { host.portals.remove(id) }
    }
    SideEffect { host.portals[id] = portal }
}

internal class OverlayOutsideEntry(var bounds: Rect? = null, val dismiss: () -> Unit)

internal fun Modifier.observeOverlayOutside(host: OverlayHostState?): Modifier =
    if (host == null) {
        this
    } else {
        pointerInput(host) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    if (event.type == PointerEventType.Press) {
                        val point = event.changes.firstOrNull()?.position ?: continue
                        host.dispatchOutside(point)
                    }
                }
            }
        }
    }

private fun OverlayHostState.containsPointer(point: Offset): Boolean =
    outside.values.any {
        it.bounds?.contains(point) == true
    } || branches.any { it.containsPointer(point) }

private fun OverlayHostState.dispatchOutside(point: Offset) {
    if (branches.none {
            it.containsPointer(point)
        }
    ) {
        outside.values.toList().forEach { if (it.bounds?.contains(point) == false) it.dismiss() }
    }
    branches.toList().forEach { it.dispatchOutside(point) }
}

@Composable
internal fun Modifier.overlayOutsideDismiss(
    host: OverlayHostState?,
    enabled: Boolean,
    dismiss: () -> Unit,
): Modifier {
    val latest by rememberUpdatedState(dismiss)
    val id = remember { Any() }
    val entry = remember { OverlayOutsideEntry { latest() } }
    DisposableEffect(host, enabled) {
        if (enabled) host?.outside?.set(id, entry)
        onDispose { host?.outside?.remove(id) }
    }
    return onGloballyPositioned { entry.bounds = it.boundsInWindow() }
}
