package com.toutiaoyan.compose.ui.gesture

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp

/**
 * Shared Open/Closed anchors for swipe-to-dismiss panels (drawer, sheet, …).
 * Offset is **closing progress**: `0` = [Open], panel extent = [Closed].
 */
enum class AnchoredSwipeValue {
    Open,
    Closed,
}

/**
 * WeChat-like swipe defaults: velocity commits the target, then settle uses
 * decay inertia (or a soft spring when the fling cannot reach the anchor).
 *
 * Pair with [rememberDecayAnchoredDraggableState] + [anchoredSwipe] — the
 * newer [androidx.compose.foundation.gestures.AnchoredDraggableDefaults.flingBehavior]
 * intentionally disables decay; this stack keeps `animateToWithDecay`.
 */
object SwipeGesture {
    /** Fraction of travel; crossed → dismiss when velocity is low. */
    const val DefaultThresholdFraction = 0.28f

    /**
     * End-velocity that commits the next anchor even below the positional
     * threshold (~WeChat-sensitive; Compose default fling is 125.dp).
     */
    val FlingVelocityThreshold: Dp = 110.dp

    /** Soft spring for snap-back / settle when decay cannot reach the target. */
    val SettleSpring: AnimationSpec<Float> =
        spring(
            dampingRatio = 0.92f,
            stiffness = Spring.StiffnessMedium,
        )

    /**
     * Inertia after release. Slightly under default friction so a flick keeps
     * coasting toward the target instead of stopping short.
     */
    val Decay: DecayAnimationSpec<Float> =
        exponentialDecay(frictionMultiplier = 0.85f)

    /**
     * Positional dismiss check (unit-test helper). Runtime settle uses the same
     * fraction via [rememberDecayAnchoredDraggableState]'s positional threshold,
     * plus [FlingVelocityThreshold].
     */
    fun shouldDismiss(
        dragOffsetPx: Float,
        contentExtentPx: Float,
        thresholdFraction: Float = DefaultThresholdFraction,
    ): Boolean {
        if (contentExtentPx <= 0f) return false
        return dragOffsetPx / contentExtentPx >= thresholdFraction
    }
}

/**
 * [AnchoredDraggableState] configured for velocity + decay settle
 * (`usePreModifierChangeBehavior`). Use with [anchoredSwipe] (or pass
 * `flingBehavior = null` to [anchoredDraggable]).
 */
@Composable
fun <T : Any> rememberDecayAnchoredDraggableState(
    initialValue: T,
    positionalThresholdFraction: Float = SwipeGesture.DefaultThresholdFraction,
    velocityThreshold: Dp = SwipeGesture.FlingVelocityThreshold,
    snapAnimationSpec: AnimationSpec<Float> = SwipeGesture.SettleSpring,
    decayAnimationSpec: DecayAnimationSpec<Float> = SwipeGesture.Decay,
): AnchoredDraggableState<T> {
    val density = LocalDensity.current
    @Suppress("DEPRECATION")
    return remember(
        initialValue,
        density,
        positionalThresholdFraction,
        velocityThreshold,
        snapAnimationSpec,
        decayAnimationSpec,
    ) {
        decayAnchoredDraggableState(
            initialValue = initialValue,
            density = density,
            positionalThresholdFraction = positionalThresholdFraction,
            velocityThreshold = velocityThreshold,
            snapAnimationSpec = snapAnimationSpec,
            decayAnimationSpec = decayAnimationSpec,
        )
    }
}

/**
 * Non-composable factory matching [rememberDecayAnchoredDraggableState].
 * Prefer the remember helper from UI code.
 */
@Suppress("DEPRECATION")
fun <T : Any> decayAnchoredDraggableState(
    initialValue: T,
    density: Density,
    positionalThresholdFraction: Float = SwipeGesture.DefaultThresholdFraction,
    velocityThreshold: Dp = SwipeGesture.FlingVelocityThreshold,
    snapAnimationSpec: AnimationSpec<Float> = SwipeGesture.SettleSpring,
    decayAnimationSpec: DecayAnimationSpec<Float> = SwipeGesture.Decay,
): AnchoredDraggableState<T> =
    AnchoredDraggableState(
        initialValue = initialValue,
        positionalThreshold = { distance -> distance * positionalThresholdFraction },
        velocityThreshold = { with(density) { velocityThreshold.toPx() } },
        snapAnimationSpec = snapAnimationSpec,
        decayAnimationSpec = decayAnimationSpec,
    )

/** Open@0 / Closed@extent anchors for [AnchoredSwipeValue] swipe-to-dismiss. */
fun AnchoredDraggableState<AnchoredSwipeValue>.updateSwipeDismissAnchors(extentPx: Float) {
    if (extentPx <= 0f) return
    updateAnchors(
        DraggableAnchors {
            AnchoredSwipeValue.Open at 0f
            AnchoredSwipeValue.Closed at extentPx
        },
    )
}

/**
 * Nested-scroll bridge so swipe-to-dismiss still works when children own the
 * pointer (e.g. `verticalScroll` inside an End drawer — horizontal “swipe right
 * to close” never reaches [anchoredDraggable] otherwise).
 *
 * Screen-axis deltas are mapped to closing progress with [reverseDirection]
 * (same convention as [anchoredDraggable]).
 */
fun anchoredSwipeNestedScrollConnection(
    state: AnchoredDraggableState<AnchoredSwipeValue>,
    orientation: Orientation,
    reverseDirection: Boolean = false,
): NestedScrollConnection =
    object : NestedScrollConnection {
        private fun offsetAxis(offset: Offset): Float =
            if (orientation == Orientation.Horizontal) offset.x else offset.y

        private fun velocityAxis(velocity: Velocity): Float =
            if (orientation == Orientation.Horizontal) velocity.x else velocity.y

        private fun Float.toClosing(): Float = if (reverseDirection) -this else this

        private fun Float.toScreen(): Float = if (reverseDirection) -this else this

        private fun Float.asAxisOffset(): Offset =
            if (orientation == Orientation.Horizontal) {
                Offset(x = this, y = 0f)
            } else {
                Offset(x = 0f, y = this)
            }

        private fun Float.asAxisVelocity(): Velocity =
            if (orientation == Orientation.Horizontal) {
                Velocity(x = this, y = 0f)
            } else {
                Velocity(x = 0f, y = this)
            }

        private fun dispatchClosing(closingDelta: Float): Float {
            if (closingDelta == 0f || state.offset.isNaN()) return 0f
            return state.dispatchRawDelta(closingDelta)
        }

        override fun onPreScroll(
            available: Offset,
            source: NestedScrollSource,
        ): Offset {
            if (source != NestedScrollSource.UserInput) return Offset.Zero
            val offset = state.offset
            // Already dragging closed: keep owning the gesture so nested lists
            // don't steal the return swipe.
            if (offset.isNaN() || offset <= 0f) return Offset.Zero
            val consumed = dispatchClosing(offsetAxis(available).toClosing())
            return consumed.toScreen().asAxisOffset()
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset {
            if (source != NestedScrollSource.UserInput) return Offset.Zero
            // Leftover axis (e.g. horizontal over a verticalScroll child) drives dismiss.
            val consumedClosing = dispatchClosing(offsetAxis(available).toClosing())
            return consumedClosing.toScreen().asAxisOffset()
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            val offset = state.offset
            val axis = velocityAxis(available)
            val closingVelocity = axis.toClosing()
            // Flinging further closed while partially dismissed — settle ourselves.
            if (!offset.isNaN() && offset > 0f && closingVelocity > 0f) {
                settle(closingVelocity)
                return axis.asAxisVelocity()
            }
            return Velocity.Zero
        }

        override suspend fun onPostFling(
            consumed: Velocity,
            available: Velocity,
        ): Velocity {
            val axis = velocityAxis(available)
            val closingVelocity = axis.toClosing()
            if (closingVelocity != 0f || (!state.offset.isNaN() && state.offset > 0f)) {
                settle(closingVelocity)
                return axis.asAxisVelocity()
            }
            return Velocity.Zero
        }

        @Suppress("DEPRECATION")
        private suspend fun settle(closingVelocity: Float) {
            state.settle(closingVelocity)
        }
    }

/**
 * Wire size → anchors, closing progress → offset, nested-scroll dismiss, and
 * decay fling (`flingBehavior = null` so [AnchoredDraggableState.settle] uses decay).
 *
 * Nested scroll is required for WeChat-style **swipe-right to close** on an End
 * drawer whose body is a vertical scroller — without it the child eats the pointer.
 *
 * @param closingOffset maps closing progress in px (`0` open → extent closed)
 *   to a layout [IntOffset] (e.g. bottom sheet: `IntOffset(0, px)`; End drawer:
 *   `IntOffset(px, 0)` for swipe-right dismiss).
 * @param extentPx derives dismiss travel from the measured size
 *   (default: height if vertical, width if horizontal).
 */
@Composable
fun Modifier.anchoredSwipe(
    state: AnchoredDraggableState<AnchoredSwipeValue>,
    orientation: Orientation,
    reverseDirection: Boolean = false,
    extentPx: (IntSize) -> Float = { size ->
        if (orientation == Orientation.Vertical) {
            size.height.toFloat()
        } else {
            size.width.toFloat()
        }
    },
    closingOffset: (closingPx: Int) -> IntOffset,
): Modifier {
    val nestedScrollConnection =
        remember(state, orientation, reverseDirection) {
            anchoredSwipeNestedScrollConnection(
                state = state,
                orientation = orientation,
                reverseDirection = reverseDirection,
            )
        }
    return this
        .nestedScroll(nestedScrollConnection)
        .onSizeChanged { size -> state.updateSwipeDismissAnchors(extentPx(size)) }
        .offset {
            val closingPx = state.offset.let { if (it.isNaN()) 0f else it }.toInt()
            closingOffset(closingPx)
        }
        .anchoredDraggable(
            state = state,
            reverseDirection = reverseDirection,
            orientation = orientation,
            flingBehavior = null,
        )
}
