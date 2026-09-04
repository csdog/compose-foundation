@file:OptIn(
    androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class,
    kotlin.experimental.ExperimentalTypeInference::class,
)

package com.toutiaoyan.compose.ui.components.text

import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.LocalTextDecoration
import com.toutiaoyan.compose.ui.theme.Theme
import kotlin.jvm.JvmName

/**
 * Usage:
 * ```
 * Text { "Hello world" }
 * Text(variant = TextVariant.BodySmall, textStyle = TextStyles.Muted) { "Subtitle" }
 * Text(style = Style { textXl2(); fontWeightBold() }) { "Large bold" }
 * Text(variant = TextVariant.BodySmall, textStyle = TextStyles.Muted) {
 *     buildAnnotatedString {
 *         append("Agree to our ")
 *         withLink(LinkAnnotation.Url("https://example.com")) { append("Terms") }
 *     }
 * }
 * ```
 *
 * Both [String] and [AnnotatedString] use trailing-lambda form. Resolution relies on
 * [OverloadResolutionByLambdaReturnType] (+ [JvmName] for JVM erasure of `Function0`).
 *
 * [textStyle] merges into [BasicText]'s layout style (alignment, weight, color, etc.). Prefer it
 * for tones like [TextColors.Muted] and for `textAlign` — Style `textAlign` / `textStyle` /
 * `contentColor` inheritance is skipped when the text is inside
 * [androidx.compose.foundation.text.selection.SelectionContainer].
 *
 * Style typography (`fontSize` / `lineHeight` / `fontWeight` / `textSm()` …) is inherited only on
 * the [String] overload (`BasicText(String)` → `TextStringSimpleNode`). The [AnnotatedString]
 * overload uses `TextAnnotatedStringNode`, which does **not** apply inherited Style typography in
 * Compose Foundation 1.11 — pass [textStyle] / [TwTextStyle][com.toutiaoyan.compose.tailwind.core.TwTextSize]
 * for rich text sizing instead.
 *
 * [inlineContent] maps ids from [androidx.compose.foundation.text.appendInlineContent] to
 * composables rendered inline (icons, images, etc.).
 *
 * Empty [textStyle] leaves ambient `LocalContentColor` from parents like Button/Badge intact;
 * set [textStyle] (e.g. [TextColors.Muted]) to override color. Use [style] for shell properties
 * (background, padding, shape).
 */
@Composable
@JvmName("TextString")
@OverloadResolutionByLambdaReturnType
fun Text(
    modifier: Modifier = Modifier,
    variant: TextVariant = TextVariant.BodyMedium,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    textStyle: TextStyle = TextStyle.Default,
    style: Style = Style,
    text: () -> String,
) {
    TextImpl(
        plainText = text(),
        annotatedText = null,
        modifier = modifier,
        variant = variant,
        maxLines = maxLines,
        overflow = overflow,
        textStyle = textStyle,
        style = style,
        inlineContent = emptyMap(),
    )
}

/**
 * Same typography / Style shell as the [String] overload, for rich text
 * (`withLink`, span styles, [inlineContent], etc.).
 *
 * Style `fontSize` / `textSm()` etc. do **not** inherit here — use [textStyle].
 */
@Composable
@JvmName("TextAnnotatedString")
@OverloadResolutionByLambdaReturnType
fun Text(
    modifier: Modifier = Modifier,
    variant: TextVariant = TextVariant.BodyMedium,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    textStyle: TextStyle = TextStyle.Default,
    style: Style = Style,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    text: () -> AnnotatedString,
) {
    TextImpl(
        plainText = null,
        annotatedText = text(),
        modifier = modifier,
        variant = variant,
        maxLines = maxLines,
        overflow = overflow,
        textStyle = textStyle,
        style = style,
        inlineContent = inlineContent,
    )
}

@Composable
private fun TextImpl(
    plainText: String?,
    annotatedText: AnnotatedString?,
    modifier: Modifier,
    variant: TextVariant,
    maxLines: Int,
    overflow: TextOverflow,
    textStyle: TextStyle,
    style: Style,
    inlineContent: Map<String, InlineTextContent>,
) {
    val theme = Theme.LocalTheme.current
    // When textStyle/typography leave color unspecified: LocalContentColor (e.g. Button) then
    // theme.foreground. A specified [textStyle] color is also pushed as a nearer Style
    // `contentColor` so it wins over ancestor Style (Card/Button) on the String path.
    // LocalTextDecoration covers Link-button hover underline where Style inheritance is skipped
    // (SelectionContainer).
    val merged = resolveTypography(theme, variant).merge(textStyle)
    val specifiedColor = merged.color.takeIf { it.isSpecified }
    val ambientColor = LocalContentColor.current
    val ambientDecoration = LocalTextDecoration.current
    val resolvedStyle =
        run {
            val colored =
                if (specifiedColor != null) {
                    merged
                } else {
                    merged.copy(
                        color =
                            if (ambientColor.isSpecified) ambientColor else theme.colors.foreground,
                    )
                }
            if (ambientDecoration != null && colored.textDecoration == null) {
                colored.copy(textDecoration = ambientDecoration)
            } else {
                colored
            }
        }
    val styleState = remember { MutableStyleState(interactionSource = null) }
    val colorStyle =
        if (specifiedColor != null) {
            Style { contentColor(specifiedColor) }
        } else {
            Style
        }
    val styledModifier = modifier.styleable(styleState, style then colorStyle)
    // Prefer BasicText(String): only TextStringSimpleNode applies Style-inherited typography
    // (fontSize / lineHeight / fontWeight). AnnotatedString always uses TextAnnotatedStringNode.
    if (plainText != null) {
        BasicText(
            text = plainText,
            modifier = styledModifier,
            style = resolvedStyle,
            maxLines = maxLines,
            overflow = overflow,
        )
    } else {
        BasicText(
            text = annotatedText!!,
            modifier = styledModifier,
            style = resolvedStyle,
            maxLines = maxLines,
            overflow = overflow,
            inlineContent = inlineContent,
        )
    }
}
