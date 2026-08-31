@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.card

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Dp
import com.toutiaoyan.compose.tailwind.modifiers.shadowMd
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.currentTheme
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.tailwind.core.twSpacing

/**
 * Card shell matching real shadcn/ui's `card.tsx` composition:
 *
 * ```
 * Card
 * ├── CardHeader
 * │   ├── CardTitle
 * │   ├── CardDescription
 * │   └── CardAction
 * ├── CardContent
 * └── CardFooter
 * ```
 *
 * Compound usage:
 * ```
 * Card {
 *     CardHeader("Account", description = "Manage your settings")
 *     CardContent("Body")
 *     CardFooter { Button("Save") }
 * }
 * ```
 *
 * Root uses official `--card-spacing` for both section gap and inset:
 * `--spacing(4)` (16dp) by default, `--spacing(3)` (12dp) for [CardSize.Sm].
 * Pass [spacing] to override.
 */
@Composable
fun Card(
    modifier: Modifier = Modifier,
    variant: CardVariant = CardVariant.Default,
    size: CardSize = CardSize.Default,
    spacing: Dp? = null,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val elevationModifier =
        if (variant == CardVariant.Elevated) {
            Modifier.shadowMd(shape = RoundedCornerShape(twRadius.xl))
        } else {
            Modifier
        }

    val resolvedSpacing = spacing ?: size.headerSpacing()

    Column(
        modifier =
            modifier.then(elevationModifier).styleable(
                styleState,
                variant.rememberStyle() then size.rememberSpacingStyle(resolvedSpacing),
                style,
            ),
        verticalArrangement = Arrangement.spacedBy(resolvedSpacing),
    ) {
        CompositionLocalProvider(
            LocalContentColor provides variant.contentColor(currentTheme.colors),
        ) {
            content()
        }
    }
}

/** Convenience: [title] + optional [description] instead of slots. */
@Composable
fun Card(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    variant: CardVariant = CardVariant.Default,
    size: CardSize = CardSize.Default,
    spacing: Dp? = null,
    style: Style = Style,
) {
    Card(modifier = modifier, variant = variant, size = size, spacing = spacing, style = style) {
        CardHeader {
            CardTitle(title)
            if (description != null) {
                CardDescription(description)
            }
        }
    }
}

@Composable
fun CardHeader(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Column(
        modifier = modifier.fillMaxWidth().styleable(styleState, style),
        verticalArrangement = Arrangement.spacedBy(twSpacing.xs),
        content = content,
    )
}

/** Convenience: [title] + optional [description] instead of slots. */
@Composable
fun CardHeader(
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    CardHeader(modifier = modifier, style = style) {
        CardTitle(title)
        if (description != null) {
            CardDescription(description)
        }
    }
}

/** String overload of [CardTitle]. */
@Composable
fun CardTitle(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    CardTitle(modifier = modifier, style = style) { text }
}

@Composable
@JvmName("CardTitleString")
@OverloadResolutionByLambdaReturnType
fun CardTitle(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) {
    Text(
        modifier = modifier.semantics { heading() },
        variant = TextVariant.TitleSmall,
        style = style,
        text = text,
    )
}

@Composable
@JvmName("CardTitleAnnotatedString")
@OverloadResolutionByLambdaReturnType
fun CardTitle(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> AnnotatedString,
) {
    Text(
        modifier = modifier.semantics { heading() },
        variant = TextVariant.TitleSmall,
        style = style,
        text = text,
    )
}

/** String overload of [CardDescription]. */
@Composable
fun CardDescription(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    CardDescription(modifier = modifier, style = style) { text }
}

@Composable
@JvmName("CardDescriptionString")
@OverloadResolutionByLambdaReturnType
fun CardDescription(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) {
    Text(
        modifier = modifier,
        variant = TextVariant.BodySmall,
        textStyle = TextColors.Muted,
        style = style,
        text = text,
    )
}

@Composable
@JvmName("CardDescriptionAnnotatedString")
@OverloadResolutionByLambdaReturnType
fun CardDescription(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> AnnotatedString,
) {
    Text(
        modifier = modifier,
        variant = TextVariant.BodySmall,
        textStyle = TextColors.Muted,
        style = style,
        text = text,
    )
}


@Composable
fun CardAction(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        content()
    }
}

@Composable
fun CardContent(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Column(
        modifier = modifier.fillMaxWidth().styleable(styleState, style),
        content = content,
    )
}

/** String-body overload of [CardContent]. */
@Composable
fun CardContent(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    CardContent(modifier = modifier, style = style) {
        Text { text }
    }
}

@Composable
fun CardFooter(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable RowScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Row(
        modifier = modifier.fillMaxWidth().styleable(styleState, style),
        horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}
