@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.scrollarea

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

/** The thumb's length as a fraction of the track, clamped so it never shrinks to invisible on very long content. */
internal fun scrollThumbFraction(
    viewportSize: Int,
    trackExtent: Int,
): Float = (viewportSize.toFloat() / trackExtent).coerceIn(0.05f, 1f)

/** The thumb's leading-edge position as a fraction of the track, for `Modifier.offset`. */
internal fun scrollThumbOffsetFraction(
    value: Int,
    trackExtent: Int,
): Float = value.toFloat() / trackExtent

/**
 * Converts a raw pointer-drag delta (px) on the thumb into a content-space scroll delta
 * (also px) -- the thumb only travels across `travelPx` (the track minus the thumb's own
 * length) while the content scrolls across the full `maxValue`, so a 1:1 pointer-to-thumb
 * px mapping would under-scroll by exactly that ratio without this conversion.
 */
internal fun scrollDragDeltaToContentDelta(
    dragDeltaPx: Float,
    maxValue: Int,
    travelPx: Float,
): Float = if (travelPx <= 0f) 0f else dragDeltaPx * maxValue / travelPx

/**
 * Slim custom scrollbar thumb shared by [ScrollArea] and [LazyScrollArea].
 * Callers supply scroll metrics + a drag sink so both `ScrollState` and `LazyListState`
 * can drive the same visual.
 *
 * [forceShowWhenEmpty] keeps a full-length thumb when content does not overflow
 * ([ScrollType.Always]). [visible] drives fade show/hide for scroll/hover policies.
 * [interactionSource] is shared so parents can treat thumb drag as "scrolling".
 */
/**
 * Public scrollbar matching shadcn's `ScrollBar`. Delegates to the shared thumb.
 */
@Composable
fun ScrollBar(
    value: Int,
    maxValue: Int,
    viewportSize: Int,
    onDragDelta: (Float) -> Unit,
    vertical: Boolean,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    forceShowWhenEmpty: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    ScrollThumb(
        value = value,
        maxValue = maxValue,
        viewportSize = viewportSize,
        onDragDelta = onDragDelta,
        vertical = vertical,
        modifier = modifier,
        visible = visible,
        forceShowWhenEmpty = forceShowWhenEmpty,
        interactionSource = interactionSource,
    )
}

@Composable
internal fun ScrollThumb(
    value: Int,
    maxValue: Int,
    viewportSize: Int,
    onDragDelta: (Float) -> Unit,
    vertical: Boolean,
    modifier: Modifier,
    visible: Boolean = true,
    forceShowWhenEmpty: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val overflowing = maxValue > 0
    if (!overflowing && !forceShowWhenEmpty) return

    val trackExtent = if (overflowing) maxValue + viewportSize else viewportSize.coerceAtLeast(1)
    val thumbFraction = if (overflowing) scrollThumbFraction(viewportSize, trackExtent) else 1f
    val offsetFraction = if (overflowing) scrollThumbOffsetFraction(value, trackExtent) else 0f

    val thickness = 6.dp
    val density = LocalDensity.current

    // fillMaxHeight/Width, not left to wrap-content: BoxWithConstraints otherwise
    // reports its own size as just its (smaller, offset-unaware) thumb child's size,
    // not the full track -- the outer Alignment.CenterEnd/BottomCenter would then
    // center that smaller box first, and the thumb's own internal offset would then
    // compound on top of *that* already-centered position instead of resolving
    // against the real, full track length. Filling the track first makes maxHeight/
    // maxWidth below actually equal the track, and the internal offset the only
    // positioning math in play.
    val trackFillModifier = if (vertical) Modifier.fillMaxHeight() else Modifier.fillMaxWidth()

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier.then(trackFillModifier),
    ) {
        BoxWithConstraints(modifier = Modifier.then(trackFillModifier)) {
            val trackLength = if (vertical) maxHeight else maxWidth
            val trackPx = with(density) { trackLength.toPx() }
            val thumbLengthPx = thumbFraction * trackPx
            val travelPx = (trackPx - thumbLengthPx).coerceAtLeast(1f)
            val dragState =
                rememberDraggableState { deltaPx ->
                    if (overflowing) {
                        onDragDelta(scrollDragDeltaToContentDelta(deltaPx, maxValue, travelPx))
                    }
                }
            val thumbModifier =
                if (vertical) {
                    Modifier
                        .offset(y = trackLength * offsetFraction)
                        .height(trackLength * thumbFraction)
                        .width(thickness)
                } else {
                    Modifier
                        .offset(x = trackLength * offsetFraction)
                        .width(trackLength * thumbFraction)
                        .height(thickness)
                }
            val styleState = remember { MutableStyleState(interactionSource) }
            Box(
                modifier =
                    thumbModifier
                        .styleable(styleState, rememberScrollThumbStyle())
                        .draggable(
                            state = dragState,
                            orientation = if (vertical) Orientation.Vertical else Orientation.Horizontal,
                            interactionSource = interactionSource,
                            enabled = overflowing,
                        ),
            )
        }
    }
}
