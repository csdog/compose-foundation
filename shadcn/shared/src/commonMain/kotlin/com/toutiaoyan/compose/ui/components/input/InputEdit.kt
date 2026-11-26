package com.toutiaoyan.compose.ui.components.input

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

/**
 * Trims only the inserted text. An externally supplied value above the limit can shrink
 * and keep its cursor without forcing a truncation of untouched text.
 */
internal fun TextFieldValue.limitInsertion(
    previous: TextFieldValue,
    maxLength: Int,
): TextFieldValue {
    val excess = text.length - maxOf(maxLength, previous.text.length)
    if (excess <= 0) return this

    // Normal edits are contiguous. Use the caret/composition to keep repeated characters
    // after the edit from being mistaken for part of the input.
    val editEnd = maxOf(selection.max, composition?.max ?: 0)
    val unchangedTail = text.commonSuffixWith(previous.text).length.coerceAtMost(text.length - editEnd)
    var end = text.length - unchangedTail
    if (text.splitsSurrogatePair(end)) end++
    var start = end - excess
    if (text.splitsSurrogatePair(start)) start--

    return copy(
        text = text.removeRange(start, end),
        selection = selection.afterRemoving(start, end),
        composition = composition?.afterRemoving(start, end)?.takeUnless { it.collapsed },
    )
}

private fun String.splitsSurrogatePair(index: Int): Boolean =
    getOrNull(index - 1)?.isHighSurrogate() == true && getOrNull(index)?.isLowSurrogate() == true

private fun TextRange.afterRemoving(
    start: Int,
    end: Int,
): TextRange {
    fun offset(position: Int): Int = position - (position - start).coerceIn(0, end - start)
    return TextRange(offset(this.start), offset(this.end))
}
