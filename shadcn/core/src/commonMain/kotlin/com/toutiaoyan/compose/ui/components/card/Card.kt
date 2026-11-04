@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.card

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.style.contentPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.styles.cssStyleable
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Dp
import com.toutiaoyan.compose.tailwind.modifiers.shadowMd
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.LocalDefaultTextStyle
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.currentTheme
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.tailwind.core.twSpacing

// Header, title and footer share the same root size/spacing, including nested cards.
@Immutable
private data class CardMetrics(
    val size: CardSize,
    val spacing: Dp,
    val footerLine: androidx.compose.ui.layout.HorizontalAlignmentLine,
    val imageLine: androidx.compose.ui.layout.HorizontalAlignmentLine,
)

private object CardActionData : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = this@CardActionData
}

private val LocalCardMetrics = staticCompositionLocalOf<CardMetrics?> { null }

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
 * Root uses official `--card-spacing` for section gap and vertical inset;
 * Header/Content own horizontal inset and Footer owns its four-sided inset:
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
    val footerLine = remember { androidx.compose.ui.layout.HorizontalAlignmentLine(::maxOf) }
    val imageLine = remember { androidx.compose.ui.layout.HorizontalAlignmentLine(::minOf) }
    val insets = remember { CardInsetOverrides() }
    val callerStyle = rememberCardCallerStyle(style, insets)

    val nova = currentTheme.activePreset == StylePreset.Nova
    val root = modifier.then(elevationModifier)
    val defaultStyle = variant.rememberStyle() then size.rememberSpacingStyle(resolvedSpacing)
    val styledRoot =
        if (nova) {
            root.cssStyleable(styleState, defaultStyle, callerStyle)
        } else {
            root.styleable(styleState, defaultStyle, callerStyle)
        }
    Column(
        modifier =
            styledRoot.then(
                if (nova) CardLayoutElement(resolvedSpacing, footerLine, imageLine, insets) else Modifier,
            ),
        verticalArrangement = Arrangement.spacedBy(resolvedSpacing),
    ) {
        CompositionLocalProvider(
            LocalContentColor provides variant.contentColor(currentTheme.colors),
            LocalCardMetrics provides CardMetrics(size, resolvedSpacing, footerLine, imageLine),
            LocalDefaultTextStyle provides
                if (nova) {
                    currentTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp, lineHeight = 20.sp,
                        lineHeightStyle =
                            LineHeightStyle(
                                LineHeightStyle.Alignment.Center,
                                LineHeightStyle.Trim.None,
                                LineHeightStyle.Mode.Tight,
                            ),
                    )
                } else {
                    LocalDefaultTextStyle.current
                },
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
    val inset = rememberCardSectionInset()
    Column(
        modifier = modifier.fillMaxWidth().styleable(styleState, inset, style),
        verticalArrangement =
            Arrangement.spacedBy(
                if (currentTheme.activePreset == StylePreset.Nova) 4.dp else twSpacing.xs,
            ),
    ) {
        if (currentTheme.activePreset != StylePreset.Nova) {
            content()
        } else {
            Layout(content = { content() }, modifier = Modifier.fillMaxWidth()) { children, constraints ->
                val gap = 4.dp.roundToPx()
                val actions =
                    children.filter {
                        it.parentData == CardActionData
                    }.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }
                val actionWidth = actions.maxOfOrNull { it.width } ?: 0
                val leadingWidth =
                    if (constraints.hasBoundedWidth) {
                        (
                            constraints.maxWidth - actionWidth -
                                if (actions.isEmpty()) {
                                    0
                                } else {
                                    4.dp.roundToPx()
                                }
                        ).coerceAtLeast(
                            0,
                        )
                    } else {
                        androidx.compose.ui.unit.Constraints.Infinity
                    }
                val leading =
                    children.filter {
                        it.parentData != CardActionData
                    }.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0, maxWidth = leadingWidth)) }
                val height =
                    maxOf(
                        leading.sumOf { it.height } + gap * (leading.size - 1).coerceAtLeast(0),
                        actions.maxOfOrNull { it.height } ?: 0,
                    )
                val width =
                    if (constraints.hasBoundedWidth) {
                        constraints.maxWidth
                    } else {
                        (
                            leading.maxOfOrNull { it.width }
                                ?: 0
                        ) +
                            actionWidth +
                            if (actions.isEmpty()) {
                                0
                            } else {
                                4.dp.roundToPx()
                            }
                    }
                layout(width, height) {
                    var y = 0
                    leading.forEach {
                        it.placeRelative(0, y)
                        y += it.height + gap
                    }
                    actions.forEach { it.placeRelative(width - it.width, 0) }
                }
            }
        }
    }
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
        textStyle = cardTitleTypography(),
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
        textStyle = cardTitleTypography(),
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
        textStyle = cardDescriptionTypography(),
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
        textStyle = cardDescriptionTypography(),
        style = style,
        text = text,
    )
}

/** An edge-to-edge image region. A first image automatically removes the root top inset. */
@Composable
fun CardImage(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val marker = if (currentTheme.activePreset == StylePreset.Nova) LocalCardMetrics.current?.imageLine else null
    Box(modifier.cardPartLine(marker), content = content)
}

@Composable
fun CardAction(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.then(CardActionData), contentAlignment = Alignment.Center) {
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
    val inset = rememberCardSectionInset()
    Column(
        modifier = modifier.fillMaxWidth().styleable(styleState, inset, style),
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
    val metrics = LocalCardMetrics.current
    val nova = currentTheme.activePreset == StylePreset.Nova
    val colors = currentTheme.colors
    val radius = twRadius.xl
    val spacing = metrics?.spacing ?: CardDefaults.Spacing
    val footerStyle =
        if (nova) {
            Style {
                background(colors.muted.copy(alpha = 0.5f))
                shape(RoundedCornerShape(bottomStart = radius, bottomEnd = radius))
                contentPadding(spacing)
            }
        } else {
            Style
        }
    val topBorder =
        if (nova) {
            Modifier.drawWithContent {
                drawContent()
                drawLine(
                    colors.border,
                    Offset(0f, 0.5.dp.toPx()),
                    Offset(size.width, 0.5.dp.toPx()),
                    strokeWidth = 1.dp.toPx(),
                )
            }.then(Modifier.padding(top = 1.dp))
        } else {
            Modifier
        }
    Row(
        modifier =
            modifier.fillMaxWidth().then(topBorder).styleable(styleState, footerStyle, style)
                .cardPartLine(if (nova) metrics?.footerLine else null),
        horizontalArrangement = if (nova) Arrangement.Start else Arrangement.spacedBy(twSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

@Composable
private fun rememberCardSectionInset(): Style {
    val nova = currentTheme.activePreset == StylePreset.Nova
    val spacing = LocalCardMetrics.current?.spacing ?: CardDefaults.Spacing
    return remember(nova, spacing) {
        if (nova) Style { contentPadding(horizontal = spacing, vertical = 0.dp) } else Style
    }
}

@Composable
private fun cardTitleTypography(): TextStyle {
    val theme = currentTheme
    return if (theme.activePreset == StylePreset.Nova) {
        theme.typography.bodyLarge.copy(
            fontSize = if (LocalCardMetrics.current?.size == CardSize.Sm) 14.sp else 16.sp,
            lineHeight = if (LocalCardMetrics.current?.size == CardSize.Sm) 19.25.sp else 22.sp,
            fontWeight = FontWeight.Medium,
            lineHeightStyle =
                LineHeightStyle(
                    LineHeightStyle.Alignment.Center,
                    LineHeightStyle.Trim.None,
                    LineHeightStyle.Mode.Tight,
                ),
        )
    } else {
        TextStyle.Default
    }
}

@Composable
private fun cardDescriptionTypography(): TextStyle =
    if (currentTheme.activePreset == StylePreset.Nova) {
        currentTheme.typography.bodyMedium.copy(
            color = currentTheme.colors.mutedForeground,
            lineHeightStyle =
                LineHeightStyle(
                    LineHeightStyle.Alignment.Center,
                    LineHeightStyle.Trim.None,
                    LineHeightStyle.Mode.Tight,
                ),
        )
    } else {
        TextColors.Muted
    }
