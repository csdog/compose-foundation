@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.scrollarea

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.ui.unit.dp

private const val LEGACY_MINIMUM_THUMB_TRACK_FRACTION = 0.05f
private val NovaMinimumThumbSize = 16.dp

internal fun scrollbarTrackThickness(nova: Boolean) = if (nova) 10.dp else 6.dp

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
): Float =
    if (!dragDeltaPx.isFinite() || !travelPx.isFinite() || travelPx <= 0f || maxValue <= 0) {
        0f
    } else {
        dragDeltaPx * maxValue / travelPx
    }

/** Track-space geometry, including the minimum thumb size; offset always stays inside travel. */
internal fun scrollThumbLengthPx(
    viewportSize: Int,
    maxValue: Int,
    trackPx: Float,
    minimumPx: Float,
): Float {
    if (!trackPx.isFinite() || trackPx <= 0f) return 0f
    val total = viewportSize.coerceAtLeast(0).toDouble() + maxValue.coerceAtLeast(0).toDouble()
    val ratio = if (total > 0) (viewportSize.coerceAtLeast(0) / total).toFloat() else 1f
    return (trackPx * ratio).coerceIn(minimumPx.coerceIn(0f, trackPx), trackPx)
}

internal fun scrollThumbOffsetPx(
    value: Int,
    maxValue: Int,
    travelPx: Float,
): Float =
    if (maxValue <= 0 || !travelPx.isFinite() || travelPx <= 0f) {
        0f
    } else {
        (value.toFloat() / maxValue).coerceIn(0f, 1f) * travelPx
    }

/**
 * Slim custom scrollbar thumb shared by [ScrollArea] and [LazyScrollArea].
 * Callers supply scroll metrics + a drag sink so both `ScrollState` and `LazyListState`
 * can drive the same visual.
 *
 * [forceShowWhenEmpty] keeps a full-length thumb when content does not overflow
 * ([ScrollType.Always]). [visible] drives fade show/hide for scroll/hover policies.
 * [interactionSource] is shared so parents can treat thumb drag as "scrolling".
 *
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

    val nova = currentTheme.activePreset == StylePreset.Nova
    val direction = LocalLayoutDirection.current
    val rtl = !vertical && direction == LayoutDirection.Rtl
    val orientation = if (vertical) Orientation.Vertical else Orientation.Horizontal
    val wheel =
        rememberScrollableState { delta ->
            val consumed = delta.coerceIn(-value.toFloat(), (maxValue - value).coerceAtLeast(0).toFloat())
            onDragDelta(consumed)
            consumed
        }
    val thickness = scrollbarTrackThickness(nova)
    val density = LocalDensity.current
    val trackFillModifier = if (vertical) Modifier.fillMaxHeight() else Modifier.fillMaxWidth()
    val trackSize = if (vertical) Modifier.width(thickness) else Modifier.height(thickness)
    val insets =
        if (!nova) {
            Modifier
        } else if (vertical) {
            Modifier.absolutePadding(left = 2.dp, top = 1.dp, right = 1.dp, bottom = 1.dp)
        } else {
            Modifier.absolutePadding(left = 1.dp, top = 2.dp, right = 1.dp, bottom = 1.dp)
        }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier.then(trackFillModifier).then(trackSize),
    ) {
        BoxWithConstraints(
            Modifier.then(trackFillModifier).then(trackSize)
                .testTag(if (vertical) "scroll-area-vertical-track" else "scroll-area-horizontal-track")
                .scrollable(
                    state = wheel,
                    orientation = orientation,
                    enabled = overflowing,
                    reverseDirection = ScrollableDefaults.reverseDirection(direction, orientation, false),
                )
                .then(insets),
        ) {
            val trackLength = if (vertical) maxHeight else maxWidth
            val trackPx = with(density) { trackLength.toPx() }
            val minimum =
                if (nova) {
                    with(density) { NovaMinimumThumbSize.toPx() }
                } else {
                    trackPx * LEGACY_MINIMUM_THUMB_TRACK_FRACTION
                }
            val thumbLengthPx = scrollThumbLengthPx(viewportSize, maxValue, trackPx, minimum)
            val travelPx = (trackPx - thumbLengthPx).coerceAtLeast(0f)
            val logicalOffset = scrollThumbOffsetPx(value, maxValue, travelPx)
            val physicalOffset = if (rtl) travelPx - logicalOffset else logicalOffset
            val extent = with(density) { thumbLengthPx.toDp() }
            val position = with(density) { logicalOffset.toDp() }
            val thumbModifier =
                if (vertical) {
                    Modifier.offset(y = position).height(extent).fillMaxWidth()
                } else {
                    Modifier.offset(x = position).width(extent).fillMaxHeight()
                }
            val styleState = remember { MutableStyleState(interactionSource) }
            Box(
                Modifier.fillMaxSize().scrollThumbPointer(
                    vertical = vertical,
                    enabled = overflowing,
                    offset = physicalOffset,
                    length = thumbLengthPx,
                    interactionSource = interactionSource,
                    onDelta = { physicalDelta ->
                        val delta = if (rtl) -physicalDelta else physicalDelta
                        onDragDelta(scrollDragDeltaToContentDelta(delta, maxValue, travelPx))
                    },
                ),
            ) {
                Box(
                    thumbModifier
                        .testTag(if (vertical) "scroll-area-vertical-thumb" else "scroll-area-horizontal-thumb")
                        .styleable(styleState, rememberScrollThumbStyle()),
                )
            }
        }
    }
}
