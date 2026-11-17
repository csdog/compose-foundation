@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.item

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.styles.cssStyleable
import com.toutiaoyan.compose.ui.utils.keyboardInputMode
import com.toutiaoyan.compose.ui.utils.rememberTheme
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.field.LocalFieldSetDisabled
import com.toutiaoyan.compose.ui.components.separator.Separator
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.LocalDefaultTextStyle
import com.toutiaoyan.compose.ui.components.icon.LocalInlineIconSize
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.tailwind.core.twSpacing
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

/**
 * A single row in a list of interactive/informational rows (notification, contact,
 * settings row, ...), matching real shadcn/ui's `item.tsx`.
 *
 * Visuals (padding, shape, border, muted fill) are Style-driven — override with [style]
 * (`variant.rememberStyle() then style`). Optional [onClick] matches shadcn's
 * "Item as a link" — omit it for a static row (existing screenshots stay put).
 *
 * Usage:
 * ```
 * ItemGroup {
 *     Item(variant = ItemVariant.Outline) {
 *         ItemMedia { Avatar("JD") }
 *         ItemContent("Jane Doe", description = "jane@example.com")
 *         ItemActions("View")
 *     }
 * }
 * ```
 */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun Item(
    modifier: Modifier = Modifier,
    variant: ItemVariant = ItemVariant.Default,
    size: ItemSize = ItemSize.Default,
    onClick: (() -> Unit)? = null,
    href: String? = null,
    enabled: Boolean = true,
    disabled: Boolean = false,
    style: Style = Style,
    content: @Composable RowScope.() -> Unit,
) {
    val nova = currentTheme.activePreset == StylePreset.Nova
    val uri = LocalUriHandler.current
    val action = onClick ?: href?.let { { uri.openUri(it) } }
    val semanticRole = if (href != null) null else Role.Button
    val resolvedEnabled = enabled && !disabled && !LocalFieldSetDisabled.current
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = remember { MutableStyleState(interactionSource) }
    val context = remember(size) { ItemContext(size) }
    val group = LocalItemGroupContext.current
    val token = remember { Any() }
    SideEffect {
        group?.items?.set(token, size)
        if (group != null && token !in group.order) group.order.add(token)
    }
    DisposableEffect(group) {
        onDispose {
            group?.items?.remove(token)
            group?.order?.remove(token)
        }
    }
    val hovered = interactionSource.collectIsHoveredAsState().value
    val linkStyle =
        if (nova && href != null) {
            val muted = currentTheme.colors.muted
            val target =
                when {
                    hovered && resolvedEnabled -> muted
                    variant == ItemVariant.Muted -> muted.copy(alpha = 0.5f)
                    else -> muted.copy(alpha = 0f)
                }
            val animatedBackground = animateColorAsState(target, tween(NovaItemColorDurationMillis))
            rememberTheme(animatedBackground) { Style { background(animatedBackground.value) } }
        } else {
            Style
        }
    val clickableModifier =
        if (action != null) {
            Modifier.keyboardInputMode().clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = resolvedEnabled,
                role = semanticRole,
                onClick = action,
            ).then(if (resolvedEnabled) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier)
        } else if (!resolvedEnabled) {
            Modifier.semantics { disabled() }
        } else {
            Modifier
        }
    val rootModifier =
        modifier
            .then(if (resolvedEnabled) Modifier else Modifier.alpha(0.5f))
            .then(clickableModifier)
            .then(
                if (nova && group != null) {
                    Modifier.semantics {
                        val index = group.order.indexOf(token)
                        if (index >= 0) collectionItemInfo = CollectionItemInfo(index, 1, 0, 1)
                    }
                } else {
                    Modifier
                },
            )
            .cssStyleable(styleState, variant.rememberStyle(size), linkStyle, style)
    CompositionLocalProvider(
        LocalItemContext provides context,
        LocalDefaultTextStyle provides
            if (nova) {
                TextStyle(
                    fontSize = 14.sp, lineHeight = 20.sp,
                    lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                )
            } else {
                LocalDefaultTextStyle.current
            },
        LocalInlineIconSize provides if (nova) 16.dp else LocalInlineIconSize.current,
    ) {
        if (nova) {
            FlowRow(
                modifier = rootModifier,
                horizontalArrangement = Arrangement.spacedBy(size.itemGap()),
                verticalArrangement = Arrangement.spacedBy(size.itemGap()),
                itemVerticalAlignment = Alignment.CenterVertically,
            ) { content() }
        } else {
            Row(
                rootModifier,
                horizontalArrangement = Arrangement.spacedBy(size.itemGap()),
                verticalAlignment = Alignment.CenterVertically,
                content = content,
            )
        }
    }
}

/** Convenience: [title] + optional [description] instead of slots. */
@Composable
fun Item(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    variant: ItemVariant = ItemVariant.Default,
    size: ItemSize = ItemSize.Default,
    onClick: (() -> Unit)? = null,
    href: String? = null,
    enabled: Boolean = true,
    disabled: Boolean = false,
    style: Style = Style,
) {
    Item(
        modifier = modifier,
        variant = variant,
        size = size,
        onClick = onClick,
        href = href,
        enabled = enabled,
        disabled = disabled,
        style = style,
    ) {
        ItemContent {
            ItemTitle(title)
            if (description != null) {
                ItemDescription(description)
            }
        }
    }
}

/** Vertically stacks a list of [Item]s with a hairline separator between each. */
@Composable
fun ItemGroup(
    modifier: Modifier = Modifier,
    style: Style = Style,
    spacing: Dp? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val context = remember { ItemGroupContext() }
    val nova = currentTheme.activePreset == StylePreset.Nova
    val gap =
        spacing ?: if (nova) {
            when {
                ItemSize.Xs in context.items.values -> 8.dp
                ItemSize.Sm in context.items.values -> 10.dp
                else -> 16.dp
            }
        } else {
            0.dp
        }
    CompositionLocalProvider(LocalItemGroupContext provides context) {
        Column(
            modifier.fillMaxWidth().semantics { collectionInfo = CollectionInfo(context.order.size, 1) }
                .styleable(styleState, style),
            verticalArrangement = Arrangement.spacedBy(gap),
            content = content,
        )
    }
}

/** A hairline between two [Item]s inside a [ItemGroup]. */
@Composable
fun ItemSeparator(
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    Separator(
        modifier =
            modifier.then(
                if (currentTheme.activePreset == StylePreset.Nova) {
                    Modifier.padding(
                        vertical =
                            8.dp,
                    )
                } else {
                    Modifier
                },
            ),
        style =
        style,
    )
}

enum class ItemMediaVariant { Default, Icon, Image }

/** A leading icon/avatar/image slot in a [Item]. */
@Composable
fun RowScope.ItemMedia(
    modifier: Modifier = Modifier,
    variant: ItemMediaVariant = ItemMediaVariant.Default,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val nova = currentTheme.activePreset == StylePreset.Nova
    val context = LocalItemContext.current
    val mediaModifier =
        if (nova) {
            val base =
                if (context?.descriptions?.isNotEmpty() == true) {
                    modifier.align(Alignment.Top).offset(y = 2.dp)
                } else {
                    modifier
                }
            when (variant) {
                ItemMediaVariant.Default, ItemMediaVariant.Icon -> base
                ItemMediaVariant.Image ->
                    base.size(
                        when (context?.size) {
                            ItemSize.Sm -> 32.dp
                            ItemSize.Xs -> 24.dp
                            else -> 40.dp
                        },
                    ).clip(RoundedCornerShape(twRadius.sm))
            }
        } else {
            when (variant) {
                ItemMediaVariant.Icon ->
                    modifier.size(32.dp).background(
                        currentTheme.colors.muted,
                        RoundedCornerShape(twRadius.sm),
                    )
                ItemMediaVariant.Image -> modifier.size(40.dp).clip(RoundedCornerShape(twRadius.sm))
                ItemMediaVariant.Default -> modifier
            }
        }
    Box(
        modifier = mediaModifier.styleable(styleState, style),
        contentAlignment = Alignment.Center,
        content = { content() },
    )
}

/** The flexible title/description column in a [Item]. */
@Composable
fun RowScope.ItemContent(
    modifier: Modifier = Modifier,
    style: Style = Style,
    fill: Boolean = true,
    spacing: Dp? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Column(
        modifier = modifier.then(if (fill) Modifier.weight(1f) else Modifier).styleable(styleState, style),
        verticalArrangement =
            Arrangement.spacedBy(
                spacing ?: if (currentTheme.activePreset == StylePreset.Nova) {
                    if (LocalItemContext.current?.size == ItemSize.Xs) 0.dp else 4.dp
                } else {
                    twSpacing.xs2
                },
            ),
        content = content,
    )
}

/** Convenience: [title] + optional [description] instead of slots. */
@Composable
fun RowScope.ItemContent(
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    ItemContent(modifier = modifier, style = style) {
        ItemTitle(title)
        if (description != null) {
            ItemDescription(description)
        }
    }
}

@Composable
fun ItemTitle(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
    textStyle: TextStyle = TextStyle.Default,
    maxLines: Int = Int.MAX_VALUE,
) = ItemTitle(AnnotatedString(text), modifier, style, textStyle, maxLines)

@Composable
fun ItemTitle(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    style: Style = Style,
    textStyle: TextStyle = TextStyle.Default,
    maxLines: Int = Int.MAX_VALUE,
) {
    val nova = currentTheme.activePreset == StylePreset.Nova
    Text(
        modifier = if (nova) modifier else modifier.semantics { heading() },
        variant = TextVariant.LabelLarge,
        style = style,
        maxLines = maxLines,
        overflow = if (maxLines == Int.MAX_VALUE) TextOverflow.Clip else TextOverflow.Ellipsis,
        textStyle =
            (
                if (nova) {
                    TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 19.25.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                    )
                } else {
                    TextStyle.Default
                }
            ).merge(textStyle),
    ) { text }
}

@Composable
@JvmName("ItemTitleLambda")
@OverloadResolutionByLambdaReturnType
fun ItemTitle(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) = ItemTitle(text(), modifier, style)

@Composable
fun ItemDescription(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
    textStyle: TextStyle = TextStyle.Default,
) = ItemDescription(AnnotatedString(text), modifier, style, textStyle)

@Composable
fun ItemDescription(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    style: Style = Style,
    textStyle: TextStyle = TextStyle.Default,
) {
    val context = LocalItemContext.current
    val key = remember { Any() }
    DisposableEffect(context) {
        context?.descriptions?.set(key, Unit)
        onDispose { context?.descriptions?.remove(key) }
    }
    val nova = currentTheme.activePreset == StylePreset.Nova
    val small = context?.size == ItemSize.Xs
    Text(
        modifier = modifier,
        variant = TextVariant.BodySmall,
        style = style,
        maxLines = 2,
        overflow = if (nova) TextOverflow.Ellipsis else TextOverflow.Clip,
        textStyle =
            TextColors.Muted.merge(
                if (nova) {
                    TextStyle(
                        fontSize = if (small) 12.sp else 14.sp,
                        lineHeight = if (small) 18.sp else 21.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Left,
                        lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                    )
                } else {
                    TextStyle.Default
                },
            ).merge(textStyle),
    ) { text }
}

@Composable
@JvmName("ItemDescriptionLambda")
@OverloadResolutionByLambdaReturnType
fun ItemDescription(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) = ItemDescription(text(), modifier, style)

/** Trailing actions (buttons, icons) in a [Item]. */
@Composable
fun RowScope.ItemActions(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable RowScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Row(
        modifier = modifier.styleable(styleState, style),
        horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

/** String-label overload of [ItemActions]. */
@Composable
fun RowScope.ItemActions(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    ItemActions(modifier = modifier, style = style) {
        Text { text }
    }
}

// Reserve Nova's gap-2 while retaining justify-between placement for surplus space.
private val ItemSpreadArrangement =
    object : Arrangement.Horizontal {
        override val spacing: Dp = 8.dp

        override fun Density.arrange(
            totalSize: Int,
            sizes: IntArray,
            layoutDirection: LayoutDirection,
            outPositions: IntArray,
        ) {
            with(Arrangement.SpaceBetween) { arrange(totalSize, sizes, layoutDirection, outPositions) }
        }
    }

/** Full-width heading row inside an [Item]. Matches shadcn's `ItemHeader`. */
@Composable
fun ItemHeader(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable RowScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Row(
        modifier = modifier.fillMaxWidth().styleable(styleState, style),
        horizontalArrangement =
            if (currentTheme.activePreset == StylePreset.Nova) ItemSpreadArrangement else Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

/** String-label overload of [ItemHeader]. */
@Composable
fun ItemHeader(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    ItemHeader(modifier = modifier, style = style) {
        Text(variant = TextVariant.LabelLarge) { text }
    }
}

/** Full-width trailing row inside an [Item]. Matches shadcn's `ItemFooter`. */
@Composable
fun ItemFooter(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable RowScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Row(
        modifier = modifier.fillMaxWidth().styleable(styleState, style),
        horizontalArrangement =
            if (currentTheme.activePreset == StylePreset.Nova) ItemSpreadArrangement else Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

/** String-label overload of [ItemFooter]. */
@Composable
fun ItemFooter(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    ItemFooter(modifier = modifier, style = style) {
        Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) { text }
    }
}
