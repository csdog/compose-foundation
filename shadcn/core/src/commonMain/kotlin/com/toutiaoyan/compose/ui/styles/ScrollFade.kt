package com.toutiaoyan.compose.ui.styles

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.ScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/** Physical edges stay fixed; Start/End and horizontal Both follow the reading direction. */
enum class ScrollFadeEdge { Both, Top, Bottom, Left, Right, Start, End }

enum class ScrollFadeOrientation { Vertical, Horizontal }

/** CSS default min(12%,40px), a fixed length, or a fraction of the viewport (0.15 means15%). */
sealed interface ScrollFadeSize {
    data object Default : ScrollFadeSize

    data class Length(val value: Dp) : ScrollFadeSize {
        init {
            require(value.value.isFinite() && value.value >= 0f)
        }
    }

    data class Fraction(val value: Float) : ScrollFadeSize {
        init {
            require(value.isFinite() && value >= 0f)
        }
    }
}

internal fun shouldFadeLeadingEdge(scrollValue: Int): Boolean = scrollValue > 0

internal fun shouldFadeTrailingEdge(
    scrollValue: Int,
    maxValue: Int,
): Boolean = scrollValue < maxValue

private val ScrollFadeEasing = CubicBezierEasing(.42f, 0f, .58f, 1f)

internal fun scrollFadeReveal(
    depth: Float,
    distance: Int,
    revealPx: Float,
): Float =
    when {
        depth <= 0f || distance <= 0 -> 0f
        revealPx <= 0f -> depth
        else -> depth * ScrollFadeEasing.transform((distance / revealPx).coerceIn(0f, 1f))
    }

internal fun scrollFadeDepth(
    size: ScrollFadeSize,
    extent: Float,
    density: Density,
): Float =
    with(density) {
        when (size) {
            ScrollFadeSize.Default -> minOf(extent * .12f, 40.dp.toPx())
            is ScrollFadeSize.Length -> size.value.toPx()
            is ScrollFadeSize.Fraction -> extent * size.value
        }
    }

/** CSS gradient stop fixup: overlapping black stops collapse to the later position, rather than multiplying ramps. */
internal fun scrollFadeStops(
    extent: Float,
    leading: Float,
    trailing: Float,
): List<Pair<Float, Color>> {
    if (extent <= 0f || !extent.isFinite()) return listOf(0f to Color.Black, 1f to Color.Black)
    val firstBlack = leading.coerceAtLeast(0f)
    val lastBlack = maxOf(firstBlack, extent - trailing.coerceAtLeast(0f))
    val stops = mutableListOf(0f to if (firstBlack > 0f) Color.Transparent else Color.Black)
    if (firstBlack > 0f && firstBlack < extent) stops += firstBlack / extent to Color.Black
    if (lastBlack > firstBlack && lastBlack < extent) stops += lastBlack / extent to Color.Black
    val endAlpha =
        when {
            firstBlack > extent -> extent / firstBlack
            trailing <= 0f -> 1f
            lastBlack >= extent -> 1f
            else -> 0f
        }
    stops += 1f to Color.Black.copy(alpha = endAlpha)
    return stops
}

/**
 * Scroll-aware alpha mask. Default depth is min(12% of viewport,40.dp), revealed over96.dp.
 * Place BEFORE verticalScroll/horizontalScroll so the layer measures the viewport, not the content:
 * `Modifier.height(288.dp).scrollFade(state).verticalScroll(state)`.
 * Background and border belong on an outer wrapper, outside the masked viewport.
 * An explicit [edgeSize] is a fixed depth; omit it for the CSS default.
 */
fun Modifier.scrollFade(
    scrollState: ScrollState,
    orientation: ScrollFadeOrientation = ScrollFadeOrientation.Vertical,
    edgeSize: Dp = Dp.Unspecified,
): Modifier =
    scrollFade(
        scrollState,
        edge = ScrollFadeEdge.Both,
        size = if (edgeSize.value.isNaN()) ScrollFadeSize.Default else ScrollFadeSize.Length(edgeSize),
        orientation = orientation,
    )

/**
 * Single edges choose their axis; [orientation] applies to Both. Per-edge sizes override [size].
 * [enabled] false is scroll-fade-none. Fractional sizes have no40.dp cap, matching arbitrary CSS percentages.
 */
fun Modifier.scrollFade(
    scrollState: ScrollState,
    edge: ScrollFadeEdge,
    size: ScrollFadeSize = ScrollFadeSize.Default,
    leadingSize: ScrollFadeSize = size,
    trailingSize: ScrollFadeSize = size,
    revealDistance: Dp = 96.dp,
    enabled: Boolean = true,
    orientation: ScrollFadeOrientation = ScrollFadeOrientation.Vertical,
): Modifier =
    scrollFadeMask(
        scrollState,
        edge,
        orientation,
        revealDistance,
        enabled,
        leading = { extent -> scrollFadeDepth(leadingSize, extent, this) },
        trailing = { extent -> scrollFadeDepth(trailingSize, extent, this) },
    )

/** Compatibility for the existing Attachment viewport's capped depth/reveal configuration. */
internal fun Modifier.scrollFade(
    scrollState: ScrollState,
    orientation: ScrollFadeOrientation,
    edgeSize: Dp,
    edgeFraction: Float,
    revealDistance: Dp,
): Modifier =
    scrollFadeMask(
        scrollState,
        ScrollFadeEdge.Both,
        orientation,
        revealDistance,
        true,
        leading = { extent -> minOf(edgeSize.toPx(), extent * edgeFraction) },
        trailing = { extent -> minOf(edgeSize.toPx(), extent * edgeFraction) },
    )

private fun Modifier.scrollFadeMask(
    scrollState: ScrollState,
    edge: ScrollFadeEdge,
    orientation: ScrollFadeOrientation,
    revealDistance: Dp,
    enabled: Boolean,
    leading: Density.(Float) -> Float,
    trailing: Density.(Float) -> Float,
): Modifier {
    require(revealDistance.value.isFinite() && revealDistance.value >= 0f)
    if (!enabled) return this
    return composed {
        val density = LocalDensity.current
        val revealPx = with(density) { revealDistance.toPx() }
        val vertical =
            when (edge) {
                ScrollFadeEdge.Top, ScrollFadeEdge.Bottom -> true
                ScrollFadeEdge.Left, ScrollFadeEdge.Right, ScrollFadeEdge.Start, ScrollFadeEdge.End -> false
                ScrollFadeEdge.Both -> orientation == ScrollFadeOrientation.Vertical
            }
        val rtl = !vertical && LocalLayoutDirection.current == LayoutDirection.Rtl
        graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }.drawWithCache {
            val extent = if (vertical) size.height else size.width
            val max = scrollState.maxValue
            val value = scrollState.value.coerceIn(0, max.coerceAtLeast(0))
            val startDepth =
                if (shouldFadeLeadingEdge(value)) {
                    scrollFadeReveal(density.leading(extent), value, revealPx)
                } else {
                    0f
                }
            val endDepth =
                if (shouldFadeTrailingEdge(value, max)) {
                    scrollFadeReveal(density.trailing(extent), max - value, revealPx)
                } else {
                    0f
                }
            // Keep CSS stop order in the logical gradient direction; swapping depths
            // before fixup would select the opposite stop when the ramps overlap in RTL.
            val reverse =
                rtl &&
                    (
                        edge == ScrollFadeEdge.Both ||
                            edge == ScrollFadeEdge.Start ||
                            edge == ScrollFadeEdge.End
                    )
            val depths =
                when (edge) {
                    ScrollFadeEdge.Both -> startDepth to endDepth
                    ScrollFadeEdge.Top -> startDepth to 0f
                    ScrollFadeEdge.Bottom -> 0f to endDepth
                    ScrollFadeEdge.Start -> startDepth to 0f
                    ScrollFadeEdge.End -> 0f to endDepth
                    ScrollFadeEdge.Left ->
                        scrollFadeReveal(
                            density.leading(extent),
                            if (rtl) {
                                max - value
                            } else {
                                value
                            },
                            revealPx,
                        ) to 0f
                    ScrollFadeEdge.Right ->
                        0f to
                            scrollFadeReveal(
                                density.trailing(extent),
                                if (rtl) {
                                    value
                                } else {
                                    max - value
                                },
                                revealPx,
                            )
                }
            val mask =
                if (max > 0 && extent > 0 && (depths.first > 0 || depths.second > 0)) {
                    Brush.linearGradient(
                        *scrollFadeStops(extent, depths.first, depths.second).toTypedArray(),
                        start = if (reverse) Offset(size.width, 0f) else Offset.Zero,
                        end =
                            when {
                                vertical -> Offset(0f, size.height)
                                reverse -> Offset.Zero
                                else -> Offset(size.width, 0f)
                            },
                    )
                } else {
                    null
                }
            onDrawWithContent {
                drawContent()
                if (mask != null) drawRect(mask, blendMode = BlendMode.DstIn)
            }
        }
    }
}
