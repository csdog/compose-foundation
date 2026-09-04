@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.item

import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    enabled: Boolean = true,
    disabled: Boolean = false,
    style: Style = Style,
    content: @Composable RowScope.() -> Unit,
) {
    val resolvedEnabled = enabled && !disabled && !LocalFieldSetDisabled.current
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = remember { MutableStyleState(interactionSource) }
    val clickableModifier =
        if (onClick != null) {
            Modifier
                .semantics {
                    role = Role.Button
                    if (!resolvedEnabled) disabled()
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = resolvedEnabled,
                    role = Role.Button,
                    onClick = onClick,
                )
                .then(if (resolvedEnabled) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier)
        } else {
            if (!resolvedEnabled) {
                Modifier.semantics { disabled() }
            } else {
                Modifier
            }
        }
    Row(
        modifier =
            modifier
                .then(if (resolvedEnabled) Modifier else Modifier.alpha(0.5f))
                .then(clickableModifier)
                .styleable(
                    styleState,
                    variant.rememberStyle(size) then style,
                ),
        horizontalArrangement = Arrangement.spacedBy(size.itemGap()),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
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
    enabled: Boolean = true,
    disabled: Boolean = false,
    style: Style = Style,
) {
    Item(
        modifier = modifier,
        variant = variant,
        size = size,
        onClick = onClick,
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
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Column(modifier = modifier.fillMaxWidth().styleable(styleState, style), content = content)
}

/** A hairline between two [Item]s inside a [ItemGroup]. */
@Composable
fun ItemSeparator(
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    Separator(modifier = modifier, style = style)
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
    val mediaModifier =
        when (variant) {
            ItemMediaVariant.Icon ->
                modifier
                    .size(32.dp)
                    .background(currentTheme.colors.muted, RoundedCornerShape(twRadius.sm))
            ItemMediaVariant.Image ->
                modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(twRadius.sm))
            ItemMediaVariant.Default -> modifier
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
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Column(
        modifier = Modifier.weight(1f).then(modifier).styleable(styleState, style),
        verticalArrangement = Arrangement.spacedBy(twSpacing.xs2),
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
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Text(
        variant = TextVariant.LabelLarge,
        modifier = modifier.semantics { heading() }.styleable(styleState, style),
    ) { text }
}

@Composable
@JvmName("ItemTitleLambda")
@OverloadResolutionByLambdaReturnType
fun ItemTitle(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) {
    ItemTitle(text = text(), modifier = modifier, style = style)
}

@Composable
fun ItemDescription(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Text(
        variant = TextVariant.BodySmall,
        textStyle = TextColors.Muted,
        style = style,
        maxLines = 2,
        modifier = modifier.styleable(styleState, style),
    ) { text }
}

@Composable
@JvmName("ItemDescriptionLambda")
@OverloadResolutionByLambdaReturnType
fun ItemDescription(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) {
    ItemDescription(text = text(), modifier = modifier, style = style)
}

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
        horizontalArrangement = Arrangement.SpaceBetween,
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
        horizontalArrangement = Arrangement.SpaceBetween,
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
