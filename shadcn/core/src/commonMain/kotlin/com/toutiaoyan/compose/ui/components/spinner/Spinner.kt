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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import com.toutiaoyan.compose.tailwind.modifiers.animateSpin
import com.toutiaoyan.compose.ui.theme.currentTheme

/**
 * A spinning loading indicator. Real shadcn/ui's `spinner.tsx` renders Lucide's
 * `Loader2Icon` (a circle with one open gap) with `animate-spin` via
 * [Modifier.animateSpin] -- this library has no bundled icon set, so the same
 * silhouette is drawn directly with [Canvas]/[Stroke] instead of depending on
 * one just for this.
 *
 * Default size is Style-driven (16.dp). [size] Sm/Lg override that; Default
 * keeps the existing chrome.
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
    val color = currentTheme.colors.foreground
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val sizeOverride =
        when (size) {
            SpinnerSize.Default -> Modifier
            SpinnerSize.Sm -> Modifier.size(12.dp)
            SpinnerSize.Lg -> Modifier.size(24.dp)
        }
    Canvas(
        modifier =
            modifier
                .then(sizeOverride)
                .animateSpin()
                .semantics {
                    contentDescription = label
                    liveRegion = LiveRegionMode.Polite
                }
                .styleable(
                    styleState,
                    rememberSpinnerStyle() then style,
                ),
    ) {
        // A ~270 degree arc (Loader2Icon's open-gap silhouette), not a full circle.
        drawArc(
            color = color,
            startAngle = 0f,
            sweepAngle = 270f,
            useCenter = false,
            style = Stroke(width = this.size.minDimension * 0.18f, cap = StrokeCap.Round),
        )
    }
}
