@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.emojitext

import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.components.text.resolveTypography
import com.toutiaoyan.compose.ui.icons.emoji.CuratedEmoji
import com.toutiaoyan.compose.ui.theme.Theme

/**
 * Like [Text], but any substring matching [CuratedEmoji] renders as a real
 * [ImageVector] via Compose's inline-content mechanism
 * ([AnnotatedString.Builder.appendInlineContent] + [BasicText]'s `inlineContent` map) instead
 * of relying on the platform font to draw the emoji glyph -- WasmJS has no browser emoji-font
 * fallback for Skia-rendered text. Substrings *not* in the curated set are emitted as plain
 * text unchanged. This is **not** general emoji support for arbitrary text (that would need a
 * full bundled emoji font/graphic set) -- it's scoped to the small curated reaction set, for
 * spots like [BubbleReactions].
 *
 * Deliberately a separate composable from [Text] rather than a new parameter on it:
 * [Text] is called from ~70 places and this scans/rebuilds an [AnnotatedString] on every
 * recomposition when curated emoji are present, a cost only the reaction-picker use case
 * should pay.
 *
 * Usage:
 * ```
 * BubbleReactions {
 *     EmojiText("👍", typography = TextTypography.LabelSmall)
 *     EmojiText("🔥", typography = TextTypography.LabelSmall)
 *     Text(typography = TextTypography.LabelSmall, textStyle = TextStyles.Muted) { "+2" }
 * }
 * ```
 */
@Composable
fun EmojiText(
    text: String,
    modifier: Modifier = Modifier,
    typography: TextVariant = TextVariant.LabelSmall,
    textStyle: TextStyle = TextStyle.Default,
    style: Style = Style,
) {
    val hasCuratedEmoji = remember(text) { CuratedEmoji.keys.any { text.contains(it) } }
    if (!hasCuratedEmoji) {
        Text(modifier, typography, textStyle = textStyle, style = style) { text }
        return
    }

    val theme = Theme.LocalTheme.current
    val merged = resolveTypography(theme, typography).merge(textStyle)
    val resolvedStyle =
        if (merged.color.isSpecified) merged else merged.copy(color = theme.colors.foreground)

    val annotated = remember(text) { buildEmojiAnnotatedString(text) }
    val inlineContent = remember(resolvedStyle.fontSize) { emojiInlineContent(resolvedStyle.fontSize) }

    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    BasicText(
        text = annotated,
        modifier = modifier.styleable(styleState, style),
        style = resolvedStyle,
        inlineContent = inlineContent,
    )
}

private fun buildEmojiAnnotatedString(text: String): AnnotatedString {
    val keysByLengthDescending = CuratedEmoji.keys.sortedByDescending { it.length }
    return buildAnnotatedString {
        var i = 0
        while (i < text.length) {
            val match = keysByLengthDescending.firstOrNull { text.startsWith(it, i) }
            if (match != null) {
                appendInlineContent(match, alternateText = match)
                i += match.length
            } else {
                append(text[i])
                i++
            }
        }
    }
}

private fun emojiInlineContent(fontSize: androidx.compose.ui.unit.TextUnit): Map<String, InlineTextContent> =
    CuratedEmoji.mapValues { (_, vector) ->
        InlineTextContent(
            placeholder = Placeholder(fontSize, fontSize, PlaceholderVerticalAlign.TextCenter),
        ) {
            EmojiGlyph(vector)
        }
    }

@Composable
private fun EmojiGlyph(vector: ImageVector) {
    Image(imageVector = vector, contentDescription = null, modifier = Modifier.fillMaxSize())
}
