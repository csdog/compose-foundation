@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.emojitext

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.em
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.icons.emoji.CuratedEmoji

/** Renders curated emoji as inline vectors while preserving other text and emoji sequences. */
@Composable
fun EmojiText(
    text: String,
    modifier: Modifier = Modifier,
    typography: TextVariant = TextVariant.LabelSmall,
    textStyle: TextStyle = TextStyle.Default,
    style: Style = Style,
) {
    val annotated = remember(text) { buildEmojiAnnotatedString(text) }
    if (annotated == null) {
        Text(modifier, typography, textStyle = textStyle, style = style) { text }
    } else {
        Text(
            modifier = modifier,
            variant = typography,
            textStyle = textStyle,
            style = style,
            inlineContent = emojiInlineContent,
        ) { annotated }
    }
}

private val curatedKeysByStart by lazy {
    CuratedEmoji.keys.sortedByDescending { it.length }.groupBy { it.first() }
}

private const val EMOJI_JOINER = '\u200D'

private fun buildEmojiAnnotatedString(text: String): AnnotatedString? {
    var builder: AnnotatedString.Builder? = null
    var plainStart = 0
    var index = 0
    while (index < text.length) {
        val match = curatedMatchAt(text, index)
        if (match == null) {
            index++
        } else {
            val target = builder ?: AnnotatedString.Builder().also { builder = it }
            target.append(text, plainStart, index)
            target.appendInlineContent(match, alternateText = match)
            index += match.length
            plainStart = index
        }
    }
    return builder?.apply { append(text, plainStart, text.length) }?.toAnnotatedString()
}

private fun curatedMatchAt(
    text: String,
    index: Int,
): String? {
    if (text.getOrNull(index - 1) == EMOJI_JOINER) return null
    return curatedKeysByStart[text[index]]?.firstOrNull { key ->
        text.startsWith(key, index) && !hasEmojiContinuation(text, index + key.length)
    }
}

// A longer curated key may include a presentation selector. An unsupported continuation
// leaves the original sequence together in the text renderer.
private fun hasEmojiContinuation(
    text: String,
    index: Int,
): Boolean =
    when (text.getOrNull(index)) {
        EMOJI_JOINER, '\uFE0E', '\uFE0F', '\u20E3' -> true
        '\uD83C' -> text.getOrNull(index + 1)?.let { it in '\uDFFB'..'\uDFFF' } == true // Skin tone modifiers
        '\uDB40' -> text.getOrNull(index + 1)?.let { it in '\uDC20'..'\uDC7F' } == true // Emoji tags
        else -> false
    }

private val emojiInlineContent by lazy {
    CuratedEmoji.mapValues { (_, vector) ->
        InlineTextContent(
            placeholder = Placeholder(1.em, 1.em, PlaceholderVerticalAlign.TextCenter),
        ) {
            Image(imageVector = vector, contentDescription = null, modifier = Modifier.fillMaxSize())
        }
    }
}
