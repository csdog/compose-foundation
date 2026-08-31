@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.bubble
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.message.MessageAlign
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.tokens.Colors
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.tailwind.core.twSpacing

/** Part of shadcn's "AI Elements" family: the visual treatment of a [BubbleContent] box. */
sealed interface BubbleVariant {
    data object Default : BubbleVariant

    data object Secondary : BubbleVariant

    data object Muted : BubbleVariant

    data object Outline : BubbleVariant

    data object Ghost : BubbleVariant

    data object Destructive : BubbleVariant

    /** Official `tinted`: washed primary fill + foreground text. */
    data object Tinted : BubbleVariant
}

/** Official `BubbleReactions` `side`. Default [Bottom] keeps the pill peeking under the bubble. */
enum class BubbleReactionsSide { Top, Bottom }

/**
 * Same color each variant passes to Style `contentColor(...)`, factored out so
 * [BubbleContent] can also provide [LocalContentColor] for [Text] / [Icon]
 * (Style inheritance is skipped inside SelectionContainer).
 */
fun BubbleVariant.contentColor(colors: Colors): Color =
    when (this) {
        BubbleVariant.Default -> colors.primaryForeground
        BubbleVariant.Secondary -> colors.secondaryForeground
        BubbleVariant.Muted -> colors.mutedForeground
        BubbleVariant.Outline -> colors.foreground
        BubbleVariant.Ghost -> colors.foreground
        BubbleVariant.Destructive -> colors.destructive
        BubbleVariant.Tinted -> colors.foreground
    }

@Composable
private fun BubbleVariant.rememberStyle(): Style {
    val theme = currentTheme
    val colors = theme.colors
    val shapes = twRadius
    val foreground = contentColor(colors)

    return remember(this, colors, shapes, foreground, theme.isDark) {
        when (this) {
            BubbleVariant.Default ->
                Style {
                    background(colors.primary)
                    contentColor(foreground)
                    shape(RoundedCornerShape(shapes.xl))
                }

            BubbleVariant.Secondary ->
                Style {
                    background(colors.secondary)
                    contentColor(foreground)
                    shape(RoundedCornerShape(shapes.xl))
                }

            BubbleVariant.Muted ->
                Style {
                    background(colors.muted)
                    contentColor(foreground)
                    shape(RoundedCornerShape(shapes.xl))
                }

            BubbleVariant.Outline ->
                Style {
                    background(colors.background)
                    contentColor(foreground)
                    borderWidth(1.dp)
                    borderColor(colors.border)
                    shape(RoundedCornerShape(shapes.xl))
                }

            BubbleVariant.Ghost ->
                Style {
                    contentColor(foreground)
                }

            BubbleVariant.Destructive ->
                Style {
                    background(colors.destructive.copy(alpha = 0.1f))
                    contentColor(foreground)
                    shape(RoundedCornerShape(shapes.xl))
                }

            // Official `oklch(from var(--primary) 0.93 …)` / dark `0.3` — alpha wash, not a
            // full oklch chroma rewrite (Compose has no `oklch(from …)` primitive).
            BubbleVariant.Tinted ->
                Style {
                    background(
                        colors.primary.copy(alpha = if (theme.isDark) 0.3f else 0.12f),
                    )
                    contentColor(foreground)
                    shape(RoundedCornerShape(shapes.xl))
                }
        }
    }
}

/**
 * A single chat message bubble, part of shadcn's "AI Elements" family. Matches real
 * shadcn/ui's `bubble.tsx` (`max-w-[80%]`, self-aligned per [align]).
 *
 * A [ColumnScope] extension (not a bare `@Composable`) specifically so it can call
 * `Modifier.align(Alignment.End)` on itself for [MessageAlign.End] -- that's a
 * `ColumnScope`-only modifier, and [Bubble] is always used inside one (directly
 * inside [BubbleGroup], or inside [Message]'s content column).
 *
 * [content] receives [BoxScope], not `ColumnScope` -- real shadcn's `BubbleReactions` is
 * an absolutely-positioned *sibling* of `BubbleContent` inside `Bubble` (CSS
 * `position: relative` on the bubble, `position: absolute` on the reactions pill), not
 * nested inside the content box. [BubbleReactions] is itself a `BoxScope`
 * extension for exactly this reason -- it needs to render layered on top of
 * [BubbleContent], not stacked below it in normal flow.
 *
 * Usage:
 * ```
 * BubbleGroup {
 *     Bubble("Hey, how's it going?", align = MessageAlign.End)
 * }
 * ```
 */
@Composable
fun ColumnScope.Bubble(
    modifier: Modifier = Modifier,
    style: Style = Style,
    align: MessageAlign = MessageAlign.Start,
    content: @Composable BoxScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Box(
        modifier =
            modifier
                .align(if (align == MessageAlign.End) Alignment.End else Alignment.Start)
                .fillMaxWidth(0.8f)
                .styleable(styleState, style),
        contentAlignment = if (align == MessageAlign.End) Alignment.TopEnd else Alignment.TopStart,
        content = content,
    )
}

/** String-body overload of [Bubble]. */
@Composable
fun ColumnScope.Bubble(
    text: String,
    modifier: Modifier = Modifier,
    variant: BubbleVariant = BubbleVariant.Default,
    style: Style = Style,
    align: MessageAlign = MessageAlign.Start,
) {
    Bubble(modifier = modifier, style = style, align = align) {
        BubbleContent(text, variant = variant)
    }
}

/** Vertically stacks a transcript of [Bubble]s. */
@Composable
fun BubbleGroup(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Column(
        modifier = modifier.styleable(styleState, style),
        verticalArrangement = Arrangement.spacedBy(twSpacing.sm),
        content = content,
    )
}

/** The actual colored bubble box inside a [Bubble]. */
@Composable
fun BubbleContent(
    modifier: Modifier = Modifier,
    variant: BubbleVariant = BubbleVariant.Default,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Box(
        modifier =
            modifier
                .styleable(styleState, variant.rememberStyle(), style)
                .padding(horizontal = twSpacing.md, vertical = twSpacing.sm),
    ) {
        CompositionLocalProvider(
            LocalContentColor provides variant.contentColor(currentTheme.colors),
        ) {
            content()
        }
    }
}

/** String-body overload of [BubbleContent]. */
@Composable
fun BubbleContent(
    text: String,
    modifier: Modifier = Modifier,
    variant: BubbleVariant = BubbleVariant.Default,
    style: Style = Style,
) {
    BubbleContent(modifier = modifier, variant = variant, style = style) {
        Text { text }
    }
}

/**
 * A small pill of reaction icons/counts, overlaid so it peeks past a [Bubble]'s edge —
 * matches real shadcn's `BubbleReactions` (`translate-y-3/4` / `-translate-y-3/4`).
 *
 * [side] / [align] match the official props (`bottom`/`end` by default). Pass [alignment]
 * only to override the Compose [Alignment] directly; default `null` derives it from
 * [side] + [align].
 */
@Composable
fun BoxScope.BubbleReactions(
    modifier: Modifier = Modifier,
    style: Style = Style,
    side: BubbleReactionsSide = BubbleReactionsSide.Bottom,
    align: MessageAlign = MessageAlign.End,
    alignment: Alignment? = null,
    content: @Composable () -> Unit,
) {
    val theme = currentTheme
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val reactionsStyle =
        remember(theme) {
            Style {
                background(theme.colors.muted)
                shape(RoundedCornerShape(twRadius.full))
            }
        }
    val resolvedAlignment =
        alignment
            ?: when {
                side == BubbleReactionsSide.Top && align == MessageAlign.Start -> Alignment.TopStart
                side == BubbleReactionsSide.Top -> Alignment.TopEnd
                align == MessageAlign.Start -> Alignment.BottomStart
                else -> Alignment.BottomEnd
            }
    val yOffset = if (side == BubbleReactionsSide.Top) -twSpacing.sm else twSpacing.sm
    Row(
        modifier =
            modifier
                .align(resolvedAlignment)
                .offset(y = yOffset)
                .styleable(styleState, reactionsStyle then style)
                .padding(horizontal = twSpacing.xs, vertical = twSpacing.xs2),
        horizontalArrangement = Arrangement.spacedBy(twSpacing.xs2),
    ) {
        content()
    }
}
