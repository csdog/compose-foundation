package com.toutiaoyan.compose.ui.gesture

import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity

/**
 * One dismiss axis for [anchoredSwipe2D]: closing progress increases toward
 * the edge, with optional [reverseDirection] (same as [anchoredDraggable]).
 */
data class SwipeDismissAxis(
    val state: AnchoredDraggableState<AnchoredSwipeValue>,
    val reverseDirection: Boolean = false,
)

/**
 * Nested scroll for independent horizontal + vertical dismiss axes (e.g. swipe
 * right **or** swipe down to close). Leftover deltas on each axis feed the
 * matching state; either axis can settle closed.
 */
fun anchoredSwipe2DNestedScrollConnection(
    horizontal: SwipeDismissAxis?,
    vertical: SwipeDismissAxis?,
): NestedScrollConnection =
    object : NestedScrollConnection {
        private fun SwipeDismissAxis.toClosing(screenDelta: Float): Float =
            if (reverseDirection) -screenDelta else screenDelta

        private fun SwipeDismissAxis.toScreen(closingDelta: Float): Float =
            if (reverseDirection) -closingDelta else closingDelta

        private fun SwipeDismissAxis.dispatch(screenDelta: Float): Float {
            if (screenDelta == 0f || state.offset.isNaN()) return 0f
            val consumedClosing = state.dispatchRawDelta(toClosing(screenDelta))
            return toScreen(consumedClosing)
        }

        @Suppress("DEPRECATION")
        private suspend fun SwipeDismissAxis.settle(screenVelocity: Float) {
            state.settle(toClosing(screenVelocity))
        }

        override fun onPreScroll(
            available: Offset,
            source: NestedScrollSource,
        ): Offset {
            if (source != NestedScrollSource.UserInput) return Offset.Zero
            var x = 0f
            var y = 0f
            horizontal?.let { axis ->
                val offset = axis.state.offset
                if (!offset.isNaN() && offset > 0f) {
                    x = axis.dispatch(available.x)
                }
            }
            vertical?.let { axis ->
                val offset = axis.state.offset
                if (!offset.isNaN() && offset > 0f) {
                    y = axis.dispatch(available.y)
                }
            }
            return Offset(x, y)
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset {
            if (source != NestedScrollSource.UserInput) return Offset.Zero
            val x = horizontal?.dispatch(available.x) ?: 0f
            val y = vertical?.dispatch(available.y) ?: 0f
            return Offset(x, y)
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            var consumedX = 0f
            var consumedY = 0f
            horizontal?.let { axis ->
                val offset = axis.state.offset
                val closingV = axis.toClosing(available.x)
                if (!offset.isNaN() && offset > 0f && closingV > 0f) {
                    axis.settle(available.x)
                    consumedX = available.x
                }
            }
            vertical?.let { axis ->
                val offset = axis.state.offset
                val closingV = axis.toClosing(available.y)
                if (!offset.isNaN() && offset > 0f && closingV > 0f) {
                    axis.settle(available.y)
                    consumedY = available.y
                }
            }
            return Velocity(consumedX, consumedY)
        }

        override suspend fun onPostFling(
            consumed: Velocity,
            available: Velocity,
        ): Velocity {
            var consumedX = 0f
            var consumedY = 0f
            horizontal?.let { axis ->
                val offset = axis.state.offset
                if (available.x != 0f || (!offset.isNaN() && offset > 0f)) {
                    axis.settle(available.x)
                    consumedX = available.x
                }
            }
            vertical?.let { axis ->
                val offset = axis.state.offset
                if (available.y != 0f || (!offset.isNaN() && offset > 0f)) {
                    axis.settle(available.y)
                    consumedY = available.y
                }
            }
            return Velocity(consumedX, consumedY)
        }
    }

/**
 * Dual-axis swipe-to-dismiss: optional horizontal and/or vertical
 * [SwipeDismissAxis] (WeChat-style right **and** down).
 *
 * @param closingOffset maps each axis's closing progress (px) to a physical screen offset.
 */
@Composable
fun Modifier.anchoredSwipe2D(
    horizontal: SwipeDismissAxis? = null,
    vertical: SwipeDismissAxis? = null,
    closingOffset: (horizontalClosingPx: Int, verticalClosingPx: Int) -> IntOffset,
): Modifier {
    require(horizontal != null || vertical != null) {
        "anchoredSwipe2D needs at least one of horizontal / vertical"
    }
    val nestedScrollConnection =
        remember(horizontal, vertical) {
            anchoredSwipe2DNestedScrollConnection(horizontal, vertical)
        }
    var modifier =
        this
            .nestedScroll(nestedScrollConnection)
            .onSizeChanged { size ->
                horizontal?.state?.updateSwipeDismissAnchors(size.width.toFloat())
                vertical?.state?.updateSwipeDismissAnchors(size.height.toFloat())
            }
            .absoluteOffset {
                val h =
                    horizontal
                        ?.state
                        ?.offset
                        .let { if (it == null || it.isNaN()) 0f else it }
                        .toInt()
                val v =
                    vertical
                        ?.state
                        ?.offset
                        .let { if (it == null || it.isNaN()) 0f else it }
                        .toInt()
                closingOffset(h, v)
            }
    horizontal?.let { axis ->
        modifier =
            modifier.anchoredDraggable(
                state = axis.state,
                reverseDirection = axis.reverseDirection,
                orientation = Orientation.Horizontal,
                flingBehavior = null,
            )
    }
    vertical?.let { axis ->
        modifier =
            modifier.anchoredDraggable(
                state = axis.state,
                reverseDirection = axis.reverseDirection,
                orientation = Orientation.Vertical,
                flingBehavior = null,
            )
    }
    return modifier
}
