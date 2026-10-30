@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.spinner

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.modifiers.animateSpin
import com.toutiaoyan.compose.ui.theme.currentTheme

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
enum class SpinnerSize { Default, Sm, Lg }

@Composable
fun Spinner(
    modifier: Modifier = Modifier,
    size: SpinnerSize = SpinnerSize.Default,
    label: String = "Loading",
    style: Style = Style,
) {
    val color = LocalContentColor.current.takeIf { it.isSpecified } ?: currentTheme.colors.foreground
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
    Canvas(
        modifier =
            modifier
                .animateSpin()
                .semantics {
                    contentDescription = label
                    liveRegion = LiveRegionMode.Polite
                }
                .styleable(
                    styleState,
                    rememberSpinnerStyle() then sizeStyle then style,
                ),
    ) {
        // Lucide LoaderCircle: 24-unit viewBox, r=9, stroke=2, 288° sweep.
        // Scale the path and stroke together, keeping its ink inside the viewport.
        val unit = this.size.minDimension / 24f
        val diameter = 18f * unit
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
