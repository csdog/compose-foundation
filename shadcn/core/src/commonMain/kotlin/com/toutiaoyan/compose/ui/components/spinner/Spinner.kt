@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.spinner

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.apply
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.isSpecified
import com.toutiaoyan.compose.ui.components.icon.LocalInlineIconSize
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import com.toutiaoyan.compose.tailwind.modifiers.animateSpin
import com.toutiaoyan.compose.ui.theme.currentTheme

private const val LOADER_VIEW_BOX_UNITS = 24f
private const val LOADER_DIAMETER_UNITS = 18f

/**
 * A spinning loading indicator. Real shadcn/ui's `spinner.tsx` renders Lucide's
 * `Loader2Icon` (a circle with one open gap) with `animate-spin` via
 * [Modifier.animateSpin] -- this library has no bundled icon set, so the same
 * silhouette is drawn directly with [Canvas]/[Stroke] instead of depending on
 * one just for this.
 *
 * Default size follows an inline Badge/Button when present, otherwise 16.dp.
 * [size] Sm/Lg and an explicit modifier override that; color follows ambient content.
 *
 * Usage:
 * ```
 * Spinner()
 * Spinner(size = SpinnerSize.Lg)
 * Spinner(modifier = Modifier.size(24.dp))
 * ```
 */
@Composable
fun Spinner(
    modifier: Modifier = Modifier,
    size: SpinnerSize = SpinnerSize.Default,
    label: String = "Loading",
    style: Style = Style,
) {
    val color = LocalContentColor.current.takeIf { it.isSpecified } ?: currentTheme.colors.foreground
    Spinner(modifier, size, label, style) {
        Canvas(Modifier.fillMaxSize()) {
            // Lucide LoaderCircle: 24-unit viewBox, r=9, stroke=2, 288° sweep.
            // Scale the path and stroke together, keeping its ink inside the viewport.
            val unit = this.size.minDimension / LOADER_VIEW_BOX_UNITS
            val diameter = LOADER_DIAMETER_UNITS * unit
            drawArc(
                color = color,
                startAngle = 0f,
                sweepAngle = 288f,
                useCenter = false,
                topLeft = Offset((this.size.width - diameter) / 2f, (this.size.height - diameter) / 2f),
                size = Size(diameter, diameter),
                style = Stroke(width = 2f * unit, cap = StrokeCap.Round),
            )
        }
    }
}

/** Customize the visual while retaining spinner sizing, animation and loading announcements. */
@Composable
fun Spinner(
    modifier: Modifier = Modifier,
    size: SpinnerSize = SpinnerSize.Default,
    label: String = "Loading",
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    val inlineSize = LocalInlineIconSize.current
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val preferredSize =
        when (size) {
            SpinnerSize.Default -> inlineSize
            SpinnerSize.Sm -> 12.dp
            SpinnerSize.Lg -> 24.dp
        }
    val sizeStyle =
        remember(preferredSize) {
            if (preferredSize == null) {
                Style
            } else {
                Style {
                    width(preferredSize)
                    height(preferredSize)
                }
            }
        }
    val dimensions = remember { SpinnerIntrinsicDimensions() }
    val mergedStyle = rememberSpinnerStyle() then sizeStyle then style
    val measuredStyle =
        remember(mergedStyle, preferredSize) {
            Style {
                dimensions.reset(preferredSize ?: DefaultSpinnerSize)
                SpinnerDimensionScope(this, dimensions).apply(mergedStyle)
            }
        }
    Box(
        modifier =
            modifier
                .semantics {
                    contentDescription = label
                    liveRegion = LiveRegionMode.Polite
                    progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate
                }
                .animateSpin()
                .styleable(
                    styleState,
                    measuredStyle,
                )
                .then(SpinnerIntrinsicSizeHint(dimensions)),
        contentAlignment = Alignment.Center,
    ) { content() }
}

/** Declared Style dimensions are cached during Style evaluation, without snapshot state writes. */
private class SpinnerIntrinsicDimensions {
    var fallback = DefaultSpinnerSize
    var width = DefaultSpinnerSize
    var height = DefaultSpinnerSize
    var minWidth = 0.dp
    var minHeight = 0.dp
    var maxWidth = Dp.Infinity
    var maxHeight = Dp.Infinity

    fun reset(side: Dp) {
        fallback = side
        width = side
        height = side
        minWidth = 0.dp
        minHeight = 0.dp
        maxWidth = Dp.Infinity
        maxHeight = Dp.Infinity
    }

    fun width(): Dp {
        val limit = maxWidth.coerceAtLeast(0.dp)
        return width.coerceIn(minWidth.coerceIn(0.dp, limit), limit)
    }

    fun height(): Dp {
        val limit = maxHeight.coerceAtLeast(0.dp)
        return height.coerceIn(minHeight.coerceIn(0.dp, limit), limit)
    }
}

/** Delegate all behavior to Foundation; only mirror dimension declarations for Canvas intrinsics. */
private class SpinnerDimensionScope(
    private val delegate: StyleScope,
    private val dimensions: SpinnerIntrinsicDimensions,
) : StyleScope by delegate {
    override fun width(value: Dp) {
        dimensions.width = value
        delegate.width(value)
    }

    override fun height(value: Dp) {
        dimensions.height = value
        delegate.height(value)
    }

    override fun width(fraction: Float) {
        dimensions.width = dimensions.fallback
        delegate.width(fraction)
    }

    override fun height(fraction: Float) {
        dimensions.height = dimensions.fallback
        delegate.height(fraction)
    }

    override fun minWidth(value: Dp) {
        dimensions.minWidth = value
        delegate.minWidth(value)
    }

    override fun minHeight(value: Dp) {
        dimensions.minHeight = value
        delegate.minHeight(value)
    }

    override fun maxWidth(value: Dp) {
        dimensions.maxWidth = value
        delegate.maxWidth(value)
    }

    override fun maxHeight(value: Dp) {
        dimensions.maxHeight = value
        delegate.maxHeight(value)
    }
}

/** Real measurement stays under Style/Modifier control; only the four intrinsic queries differ. */
private data class SpinnerIntrinsicSizeHint(val dimensions: SpinnerIntrinsicDimensions) : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val child = measurable.measure(constraints)
        return layout(child.width, child.height) { child.place(0, 0) }
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int,
    ): Int = dimensions.width().roundToPx()

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int,
    ): Int = dimensions.width().roundToPx()

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int,
    ): Int = dimensions.height().roundToPx()

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int,
    ): Int = dimensions.height().roundToPx()
}
