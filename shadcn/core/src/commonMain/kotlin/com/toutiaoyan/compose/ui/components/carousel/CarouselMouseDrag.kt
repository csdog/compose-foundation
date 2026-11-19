package com.toutiaoyan.compose.ui.components.carousel

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sign

internal fun carouselMainAxis(
    delta: Offset,
    orientation: Orientation,
): Float = if (orientation == Orientation.Horizontal) delta.x else delta.y

internal fun carouselScrollDelta(
    pointerDelta: Float,
    orientation: Orientation,
    layoutDirection: LayoutDirection,
): Float =
    if (orientation == Orientation.Horizontal && layoutDirection == LayoutDirection.Rtl) {
        pointerDelta
    } else {
        -pointerDelta
    }

internal fun carouselOverSlop(
    distance: Float,
    touchSlop: Float,
): Float = if (abs(distance) > touchSlop) distance - sign(distance) * touchSlop else 0f

private class CarouselMouseDragSession {
    var job: Job? = null
}

/**
 * Mouse-only dragging; the pager retains its native touch, wheel and keyboard paths.
 * Keep takeover, consumption and queue closure in one loop so no event escapes ownership.
 */
@Suppress("CyclomaticComplexMethod", "LoopWithTooManyJumpStatements")
@Composable
internal fun Modifier.carouselMouseDrag(
    state: PagerState,
    orientation: Orientation,
    enabled: Boolean,
): Modifier {
    val layoutDirection = LocalLayoutDirection.current
    val hasPages = state.pageCount > 0
    // A pointer cancellation closes the queue but must not cancel its boundary snap.
    val mutationScope = rememberCoroutineScope()
    val session = remember(state) { CarouselMouseDragSession() }
    return pointerInput(state, orientation, layoutDirection, enabled, hasPages) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            if (down.type != PointerType.Mouse || !currentEvent.buttons.isPrimaryPressed) return@awaitEachGesture
            if (!enabled || !hasPages) return@awaitEachGesture
            var distance = 0f
            var deltas: Channel<Float>? = null
            try {
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                    if (!enabled || state.pageCount == 0 || change.isConsumed) break
                    val delta = carouselMainAxis(change.positionChange(), orientation)
                    val scrollDelta =
                        if (deltas == null) {
                            distance += delta
                            carouselOverSlop(distance, viewConfiguration.touchSlop)
                        } else {
                            delta
                        }
                    if (deltas == null && scrollDelta != 0f) {
                        // Cancel an earlier snap only once this press becomes a drag.
                        session.job?.cancel()
                        val channel = Channel<Float>(Channel.UNLIMITED)
                        deltas = channel
                        // Launch outside AwaitPointerEventScope's restricted suspension. One
                        // mutation owns every delta, including the final release event.
                        session.job =
                            mutationScope.launch {
                                state.scroll(MutatePriority.UserInput) {
                                    for (amount in channel) scrollBy(amount)
                                }
                                if (state.pageCount > 0) state.animateScrollToPage(state.currentPage)
                            }
                    }
                    deltas?.let { channel ->
                        change.consume()
                        channel.trySend(carouselScrollDelta(scrollDelta, orientation, layoutDirection))
                    }
                    if (!change.pressed || !event.buttons.isPrimaryPressed) break
                }
            } finally {
                // Closing drains the ordered queue before releasing UserInput and snapping.
                // Pointer cancellation also takes this path; a new press cancels that snap.
                deltas?.close()
            }
        }
    }
}
