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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.icon.LocalInlineIconSize
import com.toutiaoyan.compose.ui.components.text.LocalDefaultTextStyle
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.clearAndSetSemantics
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
    val preset = currentTheme.activePreset
    val isNovaOrRhea = preset == StylePreset.Nova || preset == StylePreset.Rhea
    val borderColor = currentTheme.colors.border
    val contentColor = if (isNovaOrRhea) currentTheme.colors.mutedForeground else LocalContentColor.current
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val bottomBorderModifier =
        if (variant == MarkerVariant.Border) {
            Modifier.drawBehind {
                drawLine(
                    color = borderColor,
                    start = Offset(0f, size.height - if (isNovaOrRhea) 0.5.dp.toPx() else 0f),
                    end = Offset(size.width, size.height - if (isNovaOrRhea) 0.5.dp.toPx() else 0f),
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
        horizontalArrangement = Arrangement.spacedBy(if (isNovaOrRhea) 8.dp else twSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (variant == MarkerVariant.Separator) MarkerLine(isNovaOrRhea, leading = true)
        CompositionLocalProvider(
            LocalContentColor provides contentColor,
            LocalInlineIconSize provides if (isNovaOrRhea) 16.dp else LocalInlineIconSize.current,
            LocalDefaultTextStyle provides
                if (isNovaOrRhea) {
                    TextStyle(
                        fontFamily = currentTheme.fontFamily,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                    )
                } else {
                    LocalDefaultTextStyle.current
                },
        ) { content() }
        if (variant == MarkerVariant.Separator) MarkerLine(isNovaOrRhea)
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
private fun RowScope.MarkerLine(
    hasInnerMargin: Boolean,
    leading: Boolean = false,
) {
    Box(
        modifier =
            Modifier.weight(1f)
                .then(
                    if (hasInnerMargin) {
                        Modifier.padding(
                            start =
                                if (leading) {
                                    0.dp
                                } else {
                                    4.dp
                                },
                            end =
                                if (leading) {
                                    4.dp
                                } else {
                                    0.dp
                                },
                        )
                    } else {
                        Modifier
                    },
                )
                .height(1.dp).background(currentTheme.colors.border),
    )
}

/** An optional leading icon slot in a [Marker]. */
@Composable
fun MarkerIcon(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val preset = currentTheme.activePreset
    val isNovaOrRhea = preset == StylePreset.Nova || preset == StylePreset.Rhea
    val iconStyle =
        remember(isNovaOrRhea) {
            if (isNovaOrRhea) {
                Style {
                    width(16.dp)
                    height(16.dp)
                }
            } else {
                Style
            }
        }
    Box(
        modifier =
            modifier
                .then(if (isNovaOrRhea) Modifier.clearAndSetSemantics {} else Modifier.width(16.dp))
                .styleable(styleState, iconStyle then style),
        contentAlignment = if (isNovaOrRhea) Alignment.Center else Alignment.TopStart,
    ) { content() }
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
