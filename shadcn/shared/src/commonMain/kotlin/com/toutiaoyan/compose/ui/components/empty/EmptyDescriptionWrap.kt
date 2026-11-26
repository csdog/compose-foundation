package com.toutiaoyan.compose.ui.components.empty

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainWidth
import com.toutiaoyan.compose.ui.components.text.LocalTextParagraphModifier

private const val MAX_SEARCH_TEXT_LENGTH = 4096
private const val MAX_SEARCH_LINE_COUNT = 6
private const val MAX_SEARCH_MEASUREMENTS = 64

/** Native paths do not install the hook or resolve any additional typography. */
@Composable
internal fun EmptyDescriptionWrapping(
    text: AnnotatedString,
    enabled: Boolean = emptyDescriptionWebWrap,
    content: @Composable () -> Unit,
) {
    if (!enabled) {
        content()
        return
    }
    CompositionLocalProvider(
        LocalTextParagraphModifier provides { style -> Modifier.emptyDescriptionWrap(text, style) },
    ) {
        content()
    }
}

/** Runs inside Text's Style shell, with the exact style passed to its existing BasicText. */
@Composable
private fun Modifier.emptyDescriptionWrap(
    text: AnnotatedString,
    style: TextStyle,
): Modifier {
    if (style.lineBreak !in listOf(LineBreak.Heading, LineBreak.Paragraph)) return this
    // One bounded search fits here, so repeated identical layouts reuse engine paragraphs.
    // TextMeasurer keys typography, annotations, constraints, direction, density and font resolver,
    // and detects stale resolved fonts. No separate width cache can outlive those inputs.
    val measurer = rememberTextMeasurer(cacheSize = 128)
    return layout { measurable, constraints ->
        var width = constraints.maxWidth
        if (canSearchWidth(text, constraints)) {
            fun measure(candidateWidth: Int): TextLayoutResult =
                measurer.measure(
                    text = text,
                    style = style,
                    constraints = constraints.copy(minWidth = 0, maxWidth = candidateWidth),
                    layoutDirection = layoutDirection,
                )
            val original = measure(width)
            width = emptyDescriptionWidth(original, style.lineBreak == LineBreak.Paragraph, ::measure)
        }
        if (width == constraints.maxWidth) {
            // No improvement means a true pass-through, including exact width and height bounds.
            val child = measurable.measure(constraints)
            layout(child.width, child.height) { child.placeRelative(0, 0) }
        } else {
            val child = measurable.measure(constraints.copy(minWidth = 0, maxWidth = width))
            val outerWidth = constraints.constrainWidth(constraints.maxWidth)
            layout(outerWidth, child.height) { child.placeRelative((outerWidth - child.width) / 2, 0) }
        }
    }
}

private fun canSearchWidth(
    text: AnnotatedString,
    constraints: Constraints,
): Boolean =
    constraints.hasBoundedWidth && constraints.maxWidth > 0 && text.length <= MAX_SEARCH_TEXT_LENGTH &&
        text.text.none { it in "\n\r\u2028\u2029" }

/**
 * Search the same-line-count region, then the boundaries of identical engine line-end groups.
 * Only the widest width of each group can improve the result. This assumes contiguous greedy
 * break groups in a single short paragraph, not Chromium's general paragraph optimizer.
 * Observed non-monotone groups, overflow, unsupported paragraphs or budget exhaustion pass through.
 */
internal fun emptyDescriptionWidth(
    original: TextLayoutResult,
    pretty: Boolean,
    measure: (Int) -> TextLayoutResult,
): Int {
    val fullWidth = original.layoutInput.constraints.maxWidth
    if (original.lineCount !in 2..MAX_SEARCH_LINE_COUNT || original.hasVisualOverflow ||
        original.layoutInput.style.textAlign == TextAlign.Justify
    ) {
        return fullWidth
    }
    val candidate =
        if (pretty && !original.hasTrailingWidow()) {
            null
        } else {
            DescriptionWidthSearch(original, measure).select(pretty)
        }
    return candidate ?: fullWidth
}

private class DescriptionWidthSearch(
    private val original: TextLayoutResult,
    private val measure: (Int) -> TextLayoutResult,
) {
    private val fullWidth = original.layoutInput.constraints.maxWidth
    private val measured = mutableMapOf(fullWidth to original)

    private fun at(width: Int): TextLayoutResult? =
        measured[width] ?: if (measured.size < MAX_SEARCH_MEASUREMENTS) {
            measure(width).also { measured[width] = it }
        } else {
            null
        }

    private fun valid(result: TextLayoutResult): Boolean =
        result.lineCount == original.lineCount && !result.hasVisualOverflow

    private fun minimumWidth(): Int? = firstMatchingWidth(1, fullWidth) { at(it)?.let(::valid) }

    private fun firstWidthWithLineEnds(
        lower: Int,
        upper: Int,
        ends: List<Int>,
    ): Int? = firstMatchingWidth(lower, upper) { at(it)?.takeIf(::valid)?.let { result -> result.lineEnds() == ends } }

    private fun lineEndsAreMonotone(): Boolean =
        measured.entries.sortedBy { it.key }.map { it.value }.filter(::valid)
            .zipWithNext().none { (narrow, wide) ->
                narrow.lineEnds().zip(wide.lineEnds()).any { (a, b) -> a > b }
            }

    fun select(pretty: Boolean): Int? {
        val minimum = minimumWidth() ?: return null
        val selected = selectFromGroups(minimum, pretty)
        return selected?.takeIf { lineEndsAreMonotone() }
    }

    private fun selectFromGroups(
        minimum: Int,
        pretty: Boolean,
    ): Int? {
        var currentWidth: Int? = fullWidth
        var selectedWidth = fullWidth
        var best = original.lineVariance()
        while (currentWidth != null && currentWidth >= minimum) {
            val current = at(currentWidth)?.takeIf(::valid) ?: return null
            if (pretty) {
                if (!current.hasTrailingWidow()) {
                    selectedWidth = currentWidth
                    break
                }
            } else {
                val variance = current.lineVariance()
                if (variance < best) {
                    best = variance
                    selectedWidth = currentWidth
                }
            }
            currentWidth = firstWidthWithLineEnds(minimum, currentWidth, current.lineEnds())?.minus(1)
        }
        return selectedWidth.takeIf { currentWidth != null }
    }
}

private inline fun firstMatchingWidth(
    lower: Int,
    upper: Int,
    matches: (Int) -> Boolean?,
): Int? {
    var low = lower
    var high = upper
    while (low < high) {
        val middle = low + (high - low) / 2
        if (matches(middle) ?: return null) high = middle else low = middle + 1
    }
    return low
}

private fun TextLayoutResult.lineEnds(): List<Int> = (0 until lineCount).map { getLineEnd(it, visibleEnd = true) }

private fun TextLayoutResult.lineVariance(): Float {
    val widths = (0 until lineCount).map { getLineRight(it) - getLineLeft(it) }
    val mean = widths.average().toFloat()
    return widths.sumOf { ((it - mean) * (it - mean)).toDouble() }.toFloat()
}

private fun TextLayoutResult.hasTrailingWidow(): Boolean {
    val text = layoutInput.text.text
    val start = getLineStart(lineCount - 1)
    val end = getLineEnd(lineCount - 1, visibleEnd = true)
    var offset = start
    var words = 0
    while (offset < end) {
        val word = getWordBoundary(offset)
        val stop = minOf(end, maxOf(offset + 1, word.end))
        if (text.substring(offset, stop).any { it.isLetterOrDigit() }) words++
        if (words > 1) return false
        offset = stop
    }
    return words == 1
}
