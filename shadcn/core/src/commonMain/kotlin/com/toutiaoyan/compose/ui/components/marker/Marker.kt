@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.marker
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.separator.Separator
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.tailwind.core.twSpacing

/**
 * A labeled divider/section marker. Matches real shadcn/ui's `marker.tsx`: plain by
 * default, flanked by two hairlines for [MarkerVariant.Separator] (e.g. "Today"
 * between two lines), or underlined for [MarkerVariant.Border].
 *
 * Variant padding lives in Style — override with [style]. Border underline stays
 * `drawBehind` (Style has no per-side border width).
 *
 * Usage:
 * ```
 * Marker("Today", variant = MarkerVariant.Separator)
 * ```
 */
@Composable
fun Marker(
    modifier: Modifier = Modifier,
    variant: MarkerVariant = MarkerVariant.Default,
    style: Style = Style,
    content: @Composable RowScope.() -> Unit,
) {
    val borderColor = currentTheme.colors.border
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val bottomBorderModifier =
        if (variant == MarkerVariant.Border) {
            Modifier.drawBehind {
                drawLine(
                    color = borderColor,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx(),
                )
            }
        } else {
            Modifier
        }

    Row(
        modifier =
            modifier
                .then(bottomBorderModifier)
                .styleable(
                    styleState,
                    variant.rememberStyle() then style,
                ),
        horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (variant == MarkerVariant.Separator) MarkerLine()
        content()
        if (variant == MarkerVariant.Separator) MarkerLine()
    }
}

/** Convenience: label text instead of [MarkerContent] slots. */
@Composable
fun Marker(
    text: String,
    modifier: Modifier = Modifier,
    variant: MarkerVariant = MarkerVariant.Default,
    style: Style = Style,
) {
    Marker(modifier = modifier, variant = variant, style = style) {
        MarkerContent { Text { text } }
    }
}

@Composable
private fun RowScope.MarkerLine() {
    Box(modifier = Modifier.weight(1f).height(1.dp).background(currentTheme.colors.border))
}

/** An optional leading icon slot in a [Marker]. */
@Composable
fun MarkerIcon(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Box(modifier = modifier.width(16.dp).styleable(styleState, style)) { content() }
}

/** The marker's label text/content. */
@Composable
fun MarkerContent(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Box(modifier = modifier.styleable(styleState, style)) { content() }
}

/** String-label overload of [MarkerContent]. */
@Composable
fun MarkerContent(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    MarkerContent(modifier = modifier, style = style) {
        Text { text }
    }
}

/** Alias for [MarkerContent] matching a `MarkerLabel` slot. */
@Composable
fun MarkerLabel(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    MarkerContent(modifier = modifier, style = style, content = content)
}

/** String-label overload of [MarkerLabel]. */
@Composable
fun MarkerLabel(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    MarkerLabel(modifier = modifier, style = style) {
        Text { text }
    }
}
