package com.toutiaoyan.compose.ui.components.hovercard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntRect
import kotlinx.browser.window
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import kotlin.time.TimeSource

internal actual class HoverCardPointerSurface actual constructor() {
    private var canvas by mutableStateOf<HTMLCanvasElement?>(null)
    private var triggerPoint: Offset? = null

    actual fun observeTrigger(modifier: Modifier): Modifier =
        modifier.pointerInput(this) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    if (event.type != PointerEventType.Enter && event.type != PointerEventType.Move) continue
                    if (event.changes.none { it.type == PointerType.Mouse }) continue
                    val native = event.nativeEvent as? MouseEvent ?: continue
                    canvas = native.target as? HTMLCanvasElement ?: continue
                    triggerPoint = native.canvasPoint(density)
                }
            }
        }

    @Composable
    actual fun ObserveTransit(
        anchor: HoverCardAnchor,
        triggerHovered: Boolean,
        contentHovered: Boolean,
        onTransit: (Boolean) -> Unit,
    ) {
        val density = LocalDensity.current.density
        val surface = canvas ?: return
        val latestTriggerHover = rememberUpdatedState(triggerHovered)
        val latestContentHover = rememberUpdatedState(contentHovered)
        val latestOnTransit = rememberUpdatedState(onTransit)
        val scope = rememberCoroutineScope()
        DisposableEffect(anchor, density, surface) {
            var departure = triggerPoint.takeIf { latestTriggerHover.value }
            var fromPanel = false
            var lastPoint: Offset? = null
            var lastMove = TimeSource.Monotonic.markNow()
            var timeout: Job? = null

            fun clearTimeout() {
                timeout?.cancel()
                timeout = null
            }

            fun reset() {
                clearTimeout()
                departure = null
                fromPanel = false
                lastPoint = null
                latestOnTransit.value(false)
            }

            fun move(event: MouseEvent) {
                val trigger = anchor.trigger
                val panel = anchor.popup
                if (event.buttons.toInt() != 0 || trigger == null || panel == null) {
                    reset()
                    return
                }
                val point = event.canvasPoint(density)
                val previous = lastPoint
                val elapsed = lastMove.elapsedNow().inWholeMilliseconds
                lastPoint = point
                lastMove = TimeSource.Monotonic.markNow()
                clearTimeout()
                when {
                    trigger.hasPoint(point) -> {
                        departure = point.takeIf { latestTriggerHover.value }
                        fromPanel = false
                        latestOnTransit.value(false)
                    }
                    panel.bounds.hasPoint(point) -> {
                        departure = null
                        fromPanel = latestContentHover.value
                        latestOnTransit.value(false)
                    }
                    else -> {
                        // Canvas receives the move before Compose publishes the hover exit. An unrelated
                        // Compose overlay on the same canvas cannot seed this card's traversal.
                        if (latestTriggerHover.value && previous != null && trigger.hasPoint(previous)) {
                            departure = previous
                        }
                        if (latestContentHover.value && previous != null && panel.bounds.hasPoint(previous)) {
                            fromPanel = true
                        }
                        if (departure == null && !fromPanel) return
                        val region = hoverCardTransitRegion(point, departure, trigger, panel, density)
                        val distance = previous?.let { (point - it).getDistance() }
                        val moving = distance == null || distance >= elapsed * 0.1f * density
                        when {
                            region == HoverCardTransitRegion.SharedGap -> latestOnTransit.value(true)
                            region == HoverCardTransitRegion.Approach && moving -> {
                                latestOnTransit.value(true)
                                timeout =
                                    scope.launch {
                                        delay(40)
                                        timeout = null
                                        reset()
                                    }
                            }
                            else -> reset()
                        }
                    }
                }
            }

            val geometry =
                scope.launch {
                    snapshotFlow { anchor.trigger to anchor.popup }.collect { (trigger, _) ->
                        reset()
                        departure = triggerPoint?.takeIf { latestTriggerHover.value && trigger?.hasPoint(it) == true }
                    }
                }
            val pointer: (Event) -> Unit = { move(it as MouseEvent) }
            val cancel: (Event) -> Unit = { reset() }
            surface.addEventListener("mousemove", pointer, true)
            surface.addEventListener("mouseleave", cancel)
            surface.addEventListener("pointerdown", cancel, true)
            surface.addEventListener("pointercancel", cancel)
            window.addEventListener("scroll", cancel, true)
            window.addEventListener("blur", cancel)
            window.addEventListener("resize", cancel)
            onDispose {
                geometry.cancel()
                surface.removeEventListener("mousemove", pointer, true)
                surface.removeEventListener("mouseleave", cancel)
                surface.removeEventListener("pointerdown", cancel, true)
                surface.removeEventListener("pointercancel", cancel)
                window.removeEventListener("scroll", cancel, true)
                window.removeEventListener("blur", cancel)
                window.removeEventListener("resize", cancel)
                reset()
            }
        }
    }
}

private fun MouseEvent.canvasPoint(density: Float) = Offset(offsetX.toFloat(), offsetY.toFloat()) * density

private fun IntRect.hasPoint(point: Offset) = point.x >= left && point.x < right && point.y >= top && point.y < bottom
