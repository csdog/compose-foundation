package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import com.toutiaoyan.compose.ui.catalog.catalogMonoFontFamily
import com.toutiaoyan.compose.ui.theme.currentTheme
import kotlin.math.floor
import kotlin.math.ceil
import kotlin.math.roundToInt

private data class CodePiece(
    val text: String,
    val group: Int,
    val first: Boolean,
    val last: Boolean,
    val width: Float,
    val height: Float,
)

/** Inline code keeps original text as alternate content, including across wrapped fragments. */
@Composable
internal fun InlineDocParagraph(
    markdown: String,
    fontSize: TextUnit,
    lineHeight: TextUnit,
) {
    val theme = currentTheme
    val density = LocalDensity.current
    val codeSize = fontSize * .85f
    val codeStyle =
        TextStyle(
            fontFamily = catalogMonoFontFamily(),
            fontSize = codeSize,
            fontWeight = FontWeight.Normal,
            color = theme.colors.foreground,
        )
    val measurer = rememberTextMeasurer()
    val padding = with(density) { codeSize.toPx() * .3f }
    val verticalPadding = with(density) { codeSize.toPx() * .125f }
    val radius = with(density) { codeSize.toPx() * .35f }
    val background = theme.colors.muted
    BoxWithConstraints {
        val width = with(density) { maxWidth.toPx() }
        val pieces = mutableListOf<CodePiece>()
        val text =
            buildAnnotatedString {
                var end = 0
                var group = 0
                inlineMarkdown.findAll(markdown).forEach { match ->
                    append(markdown.substring(end, match.range.first))
                    if (match.groups[1] != null) {
                        val code = match.groupValues[1]
                        // Spaces and hyphens remain legal break opportunities. A very
                        // long identifier is split at text-layout line boundaries, so
                        // an oversized placeholder can never hide the entire token.
                        val chunks =
                            codeWords.findAll(code).flatMap { word ->
                                val result =
                                    measurer.measure(
                                        word.value,
                                        codeStyle,
                                        constraints =
                                            Constraints(
                                                maxWidth = floor(width - padding * 2).toInt().coerceAtLeast(1),
                                            ),
                                    )
                                (0 until result.lineCount).map {
                                        line ->
                                    word.value.substring(result.getLineStart(line), result.getLineEnd(line))
                                }.asSequence()
                            }.filter { it.isNotEmpty() }.toList()
                        chunks.forEachIndexed { index, chunk ->
                            val measured = measurer.measure(chunk, codeStyle, softWrap = false)
                            val first = index == 0
                            val last = index == chunks.lastIndex
                            val id = "code-${pieces.size}"
                            pieces.add(
                                CodePiece(
                                    chunk,
                                    group,
                                    first,
                                    last,
                                    (
                                        measured.size.width +
                                            (
                                                if (first) {
                                                    padding.roundToInt()
                                                } else {
                                                    0
                                                }
                                            ) +
                                            (
                                                if (last) {
                                                    padding.roundToInt()
                                                } else {
                                                    0
                                                }
                                            )
                                    ).toFloat(),
                                    ceil(measured.size.height + verticalPadding * 2),
                                ),
                            )
                            appendInlineContent(id, chunk)
                        }
                        group++
                    } else {
                        withLink(
                            LinkAnnotation.Url(
                                match.groupValues[3],
                                styles = TextLinkStyles(SpanStyle(textDecoration = TextDecoration.Underline)),
                            ),
                        ) {
                            append(match.groupValues[2])
                        }
                    }
                    end = match.range.last + 1
                }
                append(markdown.substring(end))
            }
        val content =
            pieces.mapIndexed { index, piece ->
                "code-$index" to
                    InlineTextContent(
                        Placeholder(
                            width = with(density) { piece.width.toSp() },
                            height = with(density) { piece.height.toSp() },
                            placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter,
                        ),
                    ) {
                        Box(Modifier.fillMaxSize().clearAndSetSemantics {}, contentAlignment = Alignment.Center) {
                            // Padding belongs only to the two outer edges of the whole
                            // inline run; text stays centered within its measured slot.
                            val leading = if (piece.first) padding else 0f
                            val trailing = if (piece.last) padding else 0f
                            Box(
                                Modifier.fillMaxSize().padding(
                                    start =
                                        with(density) {
                                            leading.toDp()
                                        },
                                    end = with(density) { trailing.toDp() },
                                ),
                                contentAlignment = Alignment.Center,
                            ) {
                                BasicText(piece.text, style = codeStyle, softWrap = false)
                            }
                        }
                    }
            }.toMap()
        val layout = remember(text) { mutableStateOf<TextLayoutResult?>(null) }
        SelectionContainer {
            BasicText(
                text = text,
                modifier =
                    Modifier.drawBehind {
                        // Only draw reads the layout result; measuring does not feed
                        // snapshot state back into composition or remeasure text.
                        val result = layout.value ?: return@drawBehind
                        val fragments =
                            pieces.indices.groupBy { pieces[it].group to result.placeholderRects.getOrNull(it)?.top }
                        fragments.values.forEach { indices ->
                            val rects = indices.mapNotNull { result.placeholderRects.getOrNull(it) }
                            if (rects.isEmpty()) return@forEach
                            val rect =
                                Rect(
                                    rects.minOf { it.left },
                                    rects.minOf { it.top },
                                    rects.maxOf { it.right },
                                    rects.maxOf { it.bottom },
                                )
                            val left = if (indices.any { pieces[it].first }) CornerRadius(radius) else CornerRadius.Zero
                            val right = if (indices.any { pieces[it].last }) CornerRadius(radius) else CornerRadius.Zero
                            drawPath(
                                Path().apply { addRoundRect(RoundRect(rect, left, right, right, left)) },
                                background,
                            )
                        }
                    },
                style =
                    theme.typography.bodyMedium.copy(
                        fontSize = fontSize,
                        lineHeight = lineHeight,
                        color = theme.colors.foreground,
                        fontWeight = FontWeight.Normal,
                        lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                    ),
                inlineContent = content,
                onTextLayout = { layout.value = it },
            )
        }
    }
}

private val inlineMarkdown = Regex("""`([^`]+)`|\[([^\]]+)\]\(([^)]+)\)""")
private val codeWords = Regex("""[^\s-]+-?|\s+""")
