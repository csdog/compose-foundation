@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.bubble

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.message.TrackMessagePart
import com.toutiaoyan.compose.ui.components.message.MessageAlign
import com.toutiaoyan.compose.ui.components.message.LocalMessageAlign
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.LocalDefaultTextStyle
import com.toutiaoyan.compose.ui.tokens.Colors
import com.toutiaoyan.compose.ui.styles.cssStyleable
import com.toutiaoyan.compose.ui.styles.focusVisible
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.tailwind.core.twRadius
import kotlin.math.roundToInt

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
        BubbleVariant.Muted -> colors.foreground
        BubbleVariant.Outline -> colors.foreground
        BubbleVariant.Ghost -> colors.foreground
        BubbleVariant.Destructive -> colors.destructive
        BubbleVariant.Tinted -> colors.foreground
    }

private val LocalBubbleVariant = compositionLocalOf<BubbleVariant> { BubbleVariant.Default }

@Composable
private fun BubbleVariant.rememberStyle(interactive: Boolean): Style =
    rememberTheme(this, interactive) {
        val variant = this@rememberStyle
        Style {
            fontSize(14.sp)
            lineHeight(22.75.sp)
            shape(if (variant == BubbleVariant.Ghost) RectangleShape else RoundedCornerShape(twRadius.xl))
            borderWidth(1.dp)
            borderColor(if (variant == BubbleVariant.Outline) colors.border else Color.Transparent)
            contentPadding(
                horizontal = if (variant == BubbleVariant.Ghost) 0.dp else 12.dp,
                vertical = if (variant == BubbleVariant.Ghost) 0.dp else 8.dp,
            )
            contentColor(variant.contentColor(colors))
            background(
                when (variant) {
                    BubbleVariant.Default -> colors.primary
                    BubbleVariant.Secondary -> colors.secondary
                    BubbleVariant.Muted -> colors.muted
                    BubbleVariant.Outline -> colors.background
                    BubbleVariant.Ghost -> Color.Transparent
                    BubbleVariant.Destructive -> colors.destructive.copy(alpha = if (isDark) 0.2f else 0.1f)
                    BubbleVariant.Tinted -> bubbleTintedColor(colors.primary, isDark)
                },
            )
            if (interactive) {
                animate(
                    toSpec = tween(150, easing = FastOutSlowInEasing),
                    fromSpec = tween(150, easing = FastOutSlowInEasing),
                ) {
                    hovered {
                        background(
                            when (variant) {
                                BubbleVariant.Default -> colors.primary.copy(alpha = 0.8f)
                                BubbleVariant.Secondary -> bubbleHoverMix(colors.secondary, colors.foreground)
                                BubbleVariant.Muted -> bubbleHoverMix(colors.muted, colors.foreground)
                                BubbleVariant.Outline ->
                                    if (isDark) {
                                        colors.input.copy(
                                            alpha = colors.input.alpha * 0.3f,
                                        )
                                    } else {
                                        colors.muted
                                    }
                                BubbleVariant.Ghost -> colors.muted.copy(alpha = if (isDark) 0.5f else 1f)
                                BubbleVariant.Destructive -> colors.destructive.copy(alpha = if (isDark) 0.3f else 0.2f)
                                BubbleVariant.Tinted -> bubbleTintedColor(colors.primary, isDark, hovered = true)
                            },
                        )
                    }
                }
                focusVisible {
                    borderColor(colors.ring)
                    dropShadow(Shadow(radius = 0.dp, spread = 3.dp, color = colors.ring.copy(alpha = 0.5f)))
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
    variant: BubbleVariant = BubbleVariant.Default,
    content: @Composable BoxScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    if (variant == BubbleVariant.Ghost) TrackMessagePart(ghost = true)
    val end = align == MessageAlign.End || LocalMessageAlign.current == MessageAlign.End
    CompositionLocalProvider(LocalBubbleVariant provides variant) {
        Box(
            modifier =
                modifier.align(if (end) Alignment.End else Alignment.Start)
                    .layout { measurable, constraints ->
                        val maximum =
                            if (!constraints.hasBoundedWidth || variant == BubbleVariant.Ghost) {
                                constraints.maxWidth
                            } else {
                                (constraints.maxWidth * 0.8f).toInt()
                            }
                        val child = measurable.measure(constraints.copy(minWidth = 0, maxWidth = maximum))
                        layout(child.width, child.height) { child.placeRelative(0, 0) }
                    }.styleable(styleState, style),
            contentAlignment = if (end) Alignment.TopEnd else Alignment.TopStart,
            content = content,
        )
    }
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
    Bubble(modifier = modifier, style = style, align = align, variant = variant) {
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
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = content,
    )
}

/** The actual colored bubble box inside a [Bubble]. */
@Composable
fun BubbleContent(
    modifier: Modifier = Modifier,
    variant: BubbleVariant? = null,
    style: Style = Style,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val resolved = variant ?: LocalBubbleVariant.current
    val interaction = remember { MutableInteractionSource() }
    val styleState = remember { MutableStyleState(interaction) }
    val textStyle =
        TextStyle(
            fontSize = 14.sp,
            lineHeight = 22.75.sp,
            letterSpacing = 0.sp,
            lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
        )
    val interactiveModifier =
        if (onClick == null) {
            Modifier
        } else {
            Modifier
                .hoverable(interaction, enabled)
                .clickable(
                    interactionSource = interaction,
                    indication = null,
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick,
                )
        }
    Box(
        modifier =
            modifier.then(interactiveModifier)
                .cssStyleable(styleState, resolved.rememberStyle(onClick != null && enabled) then style),
    ) {
        CompositionLocalProvider(
            LocalContentColor provides resolved.contentColor(currentTheme.colors),
            LocalDefaultTextStyle provides textStyle,
        ) { content() }
    }
}

/** String-body overload of [BubbleContent]. */
@Composable
fun BubbleContent(
    text: String,
    modifier: Modifier = Modifier,
    variant: BubbleVariant? = null,
    style: Style = Style,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
) {
    BubbleContent(modifier = modifier, variant = variant, style = style, onClick = onClick, enabled = enabled) {
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
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val reactionsStyle =
        rememberTheme {
            Style {
                shape(RoundedCornerShape(50))
                background(colors.muted)
                contentColor(colors.foreground)
                contentPadding(horizontal = 6.dp, vertical = 2.dp)
                dropShadow(Shadow(radius = 0.dp, spread = 3.dp, color = colors.card))
            }
        }
    val resolvedAlignment =
        alignment ?: when {
            side == BubbleReactionsSide.Top && align == MessageAlign.Start -> Alignment.TopStart
            side == BubbleReactionsSide.Top -> Alignment.TopEnd
            align == MessageAlign.Start -> Alignment.BottomStart
            else -> Alignment.BottomEnd
        }
    Box(Modifier.matchParentSize()) {
        Row(
            modifier =
                modifier.align(resolvedAlignment)
                    .wrapContentSize(align = resolvedAlignment, unbounded = true)
                    .layout { measurable, constraints ->
                        val child = measurable.measure(constraints)
                        val y = (child.height * 0.75f).roundToInt() * if (side == BubbleReactionsSide.Top) -1 else 1
                        val x = (if (align == MessageAlign.Start) 12.dp else (-12).dp).roundToPx()
                        layout(child.width, child.height) { child.placeRelative(x, y) }
                    }.cssStyleable(styleState, reactionsStyle then style),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            CompositionLocalProvider(
                LocalContentColor provides currentTheme.colors.foreground,
                LocalDefaultTextStyle provides
                    TextStyle(
                        fontSize = 14.sp, lineHeight = 20.sp,
                        lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                    ),
            ) { content() }
        }
    }
}
