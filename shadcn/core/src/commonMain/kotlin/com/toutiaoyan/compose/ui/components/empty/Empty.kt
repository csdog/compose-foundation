@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.empty

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.utils.rememberTheme
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

enum class EmptyVariant {
    /** base-nova root: 1dp `border-dashed`. */
    Default,

    /** Docs “Outline”: solid 1dp `border` (replaces dashed). */
    Outline,
}

/**
 * A centered placeholder for an empty list/search-result/state, matching base-nova
 * Empty (`gap-4 rounded-xl border-dashed p-6`, title `text-sm font-medium`).
 *
 * Outer padding is Style-driven — override with [style] (e.g. syncing card uses `p-4`).
 * [variant] [EmptyVariant.Default] draws a dashed border; [EmptyVariant.Outline] uses a
 * solid 1dp border instead.
 * [spacing] defaults to official `gap-4` ([EmptyDefaults.Spacing]).
 * Pass [padding] to overlay inset (`EmptyDefaults.MdPadding` = docs `md:p-12`);
 * default `null` keeps `p-6`.
 * [radius] defaults to official `rounded-xl` via root Style; pass a value to overlay.
 *
 * Usage:
 * ```
 * Empty {
 *     EmptyHeader {
 *         EmptyMedia { EmojiText("📭") }
 *         EmptyTitle("No results")
 *         EmptyDescription("Try a different search term.")
 *     }
 *     EmptyContent { Button("Clear search", onClick = {}) }
 * }
 * Empty(..., variant = EmptyVariant.Outline) { … }
 * Empty(padding = EmptyDefaults.MdPadding) { … }
 * ```
 */
@Composable
fun Empty(
    modifier: Modifier = Modifier,
    variant: EmptyVariant = EmptyVariant.Default,
    spacing: Dp = EmptyDefaults.Spacing,
    padding: Dp? = null,
    radius: Dp? = null,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    var composed = rememberEmptyStyle()
    if (padding != null) composed = composed then rememberEmptyPaddingStyle(padding)
    if (radius != null) composed = composed then rememberEmptyRadiusStyle(radius)
    if (variant == EmptyVariant.Outline) composed = composed then rememberEmptyOutlineStyle()
    val corner = radius ?: EmptyDefaults.Radius
    val borderColor = rememberTheme { colors.border }
    val dashed =
        if (variant == EmptyVariant.Default) {
            Modifier.emptyDashedBorder(color = borderColor, corner = corner)
        } else {
            Modifier
        }
    Column(
        modifier =
            modifier
                .styleable(
                    styleState,
                    composed,
                    style,
                )
                .then(dashed),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing),
        content = content,
    )
}

/** Convenience: [title] + optional [description] instead of slots. */
@Composable
fun Empty(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    variant: EmptyVariant = EmptyVariant.Default,
    spacing: Dp = EmptyDefaults.Spacing,
    padding: Dp? = null,
    radius: Dp? = null,
    style: Style = Style,
    media: (@Composable () -> Unit)? = null,
) {
    Empty(
        modifier = modifier,
        variant = variant,
        spacing = spacing,
        padding = padding,
        radius = radius,
        style = style,
    ) {
        EmptyHeader {
            if (media != null) {
                EmptyMedia { media() }
            }
            EmptyTitle(title)
            if (description != null) {
                EmptyDescription(description)
            }
        }
    }
}

@Composable
fun ColumnScope.EmptyHeader(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Column(
        modifier = modifier.styleable(styleState, style),
        horizontalAlignment = Alignment.CenterHorizontally,
        // Nova `cn-empty-header` `gap-2` = 8.dp
        verticalArrangement = Arrangement.spacedBy(twSpacing.sm),
        content = content,
    )
}

/** Convenience: [title] + optional [description] instead of slots. */
@Composable
fun ColumnScope.EmptyHeader(
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier,
    style: Style = Style,
    mediaVariant: EmptyMediaVariant = EmptyMediaVariant.Default,
    media: (@Composable () -> Unit)? = null,
) {
    EmptyHeader(modifier = modifier, style = style) {
        if (media != null) {
            EmptyMedia(variant = mediaVariant) { media() }
        }
        EmptyTitle(title)
        if (description != null) {
            EmptyDescription(description)
        }
    }
}

enum class EmptyMediaVariant {
    /** Unstyled slot — official shadcn `variant="default"`. */
    Default,

    /** Muted rounded badge — official `variant="icon"` (`size-8`). */
    Icon,
}

/** A circular icon/emoji badge above an [EmptyTitle], matching real shadcn's `EmptyMedia`.
 * Default [variant] is official `"default"` (unstyled). Pass [EmptyMediaVariant.Icon] for the badge.
 */
@Composable
fun EmptyMedia(
    modifier: Modifier = Modifier,
    variant: EmptyMediaVariant = EmptyMediaVariant.Default,
    size: Dp? = null,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val variantStyle =
        if (variant == EmptyMediaVariant.Icon) {
            rememberEmptyMediaStyle(size ?: EmptyMediaDefaults.IconSize)
        } else {
            Style
        }
    Box(
        modifier =
            modifier
                // Nova `cn-empty-media` `mb-2`
                .padding(bottom = twSpacing.sm)
                .styleable(
                    styleState,
                    variantStyle then style,
                ),
        contentAlignment = Alignment.Center,
        content = { content() },
    )
}

/** Matches nova `cn-empty-title`: `text-sm font-medium tracking-tight`. */
object EmptyTitleDefaults {
    /** Official `text-sm` ([TextVariant.BodySmall]). */
    val Official = TextVariant.BodySmall
}

@Composable
fun EmptyTitle(
    text: String,
    modifier: Modifier = Modifier,
    variant: TextVariant = EmptyTitleDefaults.Official,
    style: Style = Style,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Text(
        variant = variant,
        modifier = modifier.semantics { heading() }.styleable(styleState, style),
        textStyle =
            TextStyle(
                fontWeight = FontWeight.Medium,
                letterSpacing = (-0.025).sp,
                textAlign = TextAlign.Center,
            ),
    ) { text }
}

@Composable
@JvmName("EmptyTitleLambda")
@OverloadResolutionByLambdaReturnType
fun EmptyTitle(
    modifier: Modifier = Modifier,
    variant: TextVariant = EmptyTitleDefaults.Official,
    style: Style = Style,
    text: () -> String,
) {
    EmptyTitle(text = text(), modifier = modifier, variant = variant, style = style)
}

/** Matches Nova `cn-empty-description`: `text-sm/relaxed` + muted. */
@Composable
fun EmptyDescription(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Text(
        variant = TextVariant.BodyMedium,
        style = style,
        modifier = modifier.styleable(styleState, style),
        textStyle =
            TextColors.Muted.merge(
                TextStyle(
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center,
                ),
            ),
    ) { text }
}

@Composable
@JvmName("EmptyDescriptionLambda")
@OverloadResolutionByLambdaReturnType
fun EmptyDescription(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) {
    EmptyDescription(text = text(), modifier = modifier, style = style)
}

@Composable
fun ColumnScope.EmptyContent(
    modifier: Modifier = Modifier,
    spacing: Dp = EmptyContentDefaults.Spacing,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Column(
        modifier = modifier.styleable(styleState, style),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing),
        content = content,
    )
}

/** String-body overload of [EmptyContent]. */
@Composable
fun ColumnScope.EmptyContent(
    text: String,
    modifier: Modifier = Modifier,
    spacing: Dp = EmptyContentDefaults.Spacing,
    style: Style = Style,
) {
    EmptyContent(modifier = modifier, spacing = spacing, style = style) {
        Text { text }
    }
}
