package com.toutiaoyan.compose.ui.components.codeblock

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.components.text.LocalDefaultTextStyle
import com.toutiaoyan.compose.ui.styles.ScrollFadeOrientation
import com.toutiaoyan.compose.ui.styles.scrollFade
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.currentTheme
import kotlinx.coroutines.launch

sealed interface CodeBlockLineVariant {
    data object Default : CodeBlockLineVariant

    data object Added : CodeBlockLineVariant

    data object Removed : CodeBlockLineVariant

    data object Highlighted : CodeBlockLineVariant
}

// Measurement-only state shared by the content grid and its line-number cells. It is derived
// from every current label before any row is measured, with no snapshot/layout feedback.
internal class CodeBlockGutter {
    var width: Int = 0
}

class CodeBlockContentScope internal constructor(
    context: CodeBlockContext,
    internal val numbered: Boolean,
    internal val startLine: Int,
    internal val wrap: Boolean,
    internal val textStyle: TextStyle,
    internal val gutter: CodeBlockGutter,
) : CodeBlockPartsScope(context)

@Composable
fun CodeBlockScope.CodeBlockContent(
    code: String,
    modifier: Modifier = Modifier,
    wrap: Boolean = false,
    lineNumbers: Boolean = false,
    startLine: Int = 1,
    maxHeight: androidx.compose.ui.unit.Dp? = null,
    scrollable: Boolean = true,
    textStyle: TextStyle = TextStyle.Default,
) {
    CodeBlockContent(modifier, wrap, lineNumbers, startLine, maxHeight, scrollable, textStyle) {
        code.removeSuffix("\n").split('\n').forEachIndexed { index, line ->
            CodeBlockLine(text = line, lineNumber = startLine + index)
        }
    }
}

/**
 * Rich/diff lines specify their line numbers explicitly, avoiding a composition-time counter.
 *
 * [maxHeight] mirrors an official `max-h-*` class; [scrollable = false] mirrors
 * `overflow-y-hidden` (the Show More collapsed state), where the cap clips instead of scrolling.
 */
@Composable
fun CodeBlockScope.CodeBlockContent(
    modifier: Modifier = Modifier,
    wrap: Boolean = false,
    lineNumbers: Boolean = false,
    startLine: Int = 1,
    maxHeight: androidx.compose.ui.unit.Dp? = null,
    scrollable: Boolean = true,
    textStyle: TextStyle = TextStyle.Default,
    content: @Composable CodeBlockContentScope.() -> Unit,
) {
    val theme = currentTheme
    val tint = LocalContentColor.current
    val mono =
        theme.typography.bodyMedium.copy(
            color = tint,
            fontFamily = FontFamily.Monospace,
            fontSize = if (context.size == CodeBlockSize.Sm) 12.sp else 14.sp,
            lineHeight = if (context.size == CodeBlockSize.Sm) 19.5.sp else 22.75.sp,
        ).merge(textStyle)
    val gutter = remember { CodeBlockGutter() }
    val textMeasurer = rememberTextMeasurer()
    val scope = CodeBlockContentScope(context, lineNumbers, startLine, wrap, mono, gutter)
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    val scopeCoroutine = rememberCoroutineScope()
    val lineStep = with(LocalDensity.current) { (if (context.size == CodeBlockSize.Sm) 20.dp else 24.dp).toPx() }
    val scrollsVertically = maxHeight != null && scrollable
    val keyboardScroll =
        Modifier.onPreviewKeyEvent { event ->
            if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
            val verticalDelta =
                when (event.key) {
                    Key.DirectionUp -> -lineStep
                    Key.DirectionDown -> lineStep
                    Key.PageUp -> -verticalScrollState.viewportSize.toFloat()
                    Key.PageDown -> verticalScrollState.viewportSize.toFloat()
                    Key.MoveHome -> -verticalScrollState.value.toFloat()
                    Key.MoveEnd -> verticalScrollState.maxValue.toFloat()
                    else -> 0f
                }
            val horizontalDelta =
                when (event.key) {
                    Key.DirectionLeft -> -lineStep
                    Key.DirectionRight -> lineStep
                    else -> 0f
                }
            when {
                scrollsVertically && verticalDelta != 0f && verticalScrollState.maxValue > 0 -> {
                    scopeCoroutine.launch { verticalScrollState.scrollBy(verticalDelta) }
                    true
                }
                !wrap && horizontalDelta != 0f && horizontalScrollState.maxValue > 0 -> {
                    scopeCoroutine.launch { horizontalScrollState.scrollBy(horizontalDelta) }
                    true
                }
                else -> false
            }
        }
    // The viewport modifiers must share the code's LTR direction, including horizontal fades.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        BoxWithConstraints(
            modifier =
                modifier.fillMaxWidth().codeSlot(CodeSlot.Content)
                    .then(
                        when {
                            maxHeight == null -> Modifier
                            scrollable ->
                                Modifier.heightIn(max = maxHeight)
                                    .scrollFade(
                                        verticalScrollState,
                                        orientation = ScrollFadeOrientation.Vertical,
                                    )
                                    .verticalScroll(verticalScrollState)
                            else ->
                                Modifier.heightIn(max = maxHeight)
                                    .clipToBounds()
                                    // Official `max-h-* + scroll-fade-y + overflow-y-hidden`: the clipped
                                    // block keeps the trailing fade even though it cannot scroll.
                                    .clippedScrollFade()
                        },
                    )
                    .then(
                        if (wrap) {
                            Modifier
                        } else {
                            // Official `scroll-fade-x` on the content: horizontal edges fade while scrolling.
                            Modifier.scrollFade(horizontalScrollState, orientation = ScrollFadeOrientation.Horizontal)
                        },
                    )
                    .onFocusChanged { context.onContentFocus(it.isFocused) }
                    .then(keyboardScroll)
                    .focusable(),
        ) {
            val viewportWidth = maxWidth
            SelectionContainer {
                val scrolling = if (wrap) Modifier else Modifier.horizontalScroll(horizontalScrollState)
                Column(scrolling) {
                    Layout(
                        modifier =
                            if (wrap) {
                                Modifier.fillMaxWidth()
                            } else {
                                Modifier.widthIn(
                                    min = viewportWidth,
                                ).width(IntrinsicSize.Max)
                            },
                        content = { scope.content() },
                    ) { lines, constraints ->
                        gutter.width =
                            if (lineNumbers) {
                                maxOf(
                                    24.dp.roundToPx(),
                                    lines.maxOfOrNull { line ->
                                        val label = (line.layoutId as? Int)?.toString().orEmpty()
                                        textMeasurer.measure(label, style = mono, softWrap = false).size.width
                                    } ?: 0,
                                )
                            } else {
                                0
                            }
                        val children = lines.map { it.measure(constraints.copy(minHeight = 0)) }
                        val width = constraints.constrainWidth(children.maxOfOrNull { it.width } ?: 0)
                        val height = constraints.constrainHeight(children.sumOf { it.height })
                        layout(width, height) {
                            var y = 0
                            children.forEach { child ->
                                child.placeRelative(0, y)
                                y += child.height
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CodeBlockContentScope.CodeBlockLine(
    text: String,
    modifier: Modifier = Modifier,
    variant: CodeBlockLineVariant = CodeBlockLineVariant.Default,
    lineNumber: Int? = null,
) {
    CodeBlockLine(modifier, variant, lineNumber) {
        BasicText(
            text = if (text.isEmpty()) "\n" else text,
            style = textStyle,
            maxLines = if (text.isEmpty()) 1 else Int.MAX_VALUE,
            softWrap = wrap,
        )
    }
}

@Composable
fun CodeBlockContentScope.CodeBlockLine(
    modifier: Modifier = Modifier,
    variant: CodeBlockLineVariant = CodeBlockLineVariant.Default,
    lineNumber: Int? = null,
    content: @Composable () -> Unit,
) {
    val theme = currentTheme
    val inset = context.inset
    val background =
        when (variant) {
            CodeBlockLineVariant.Added, CodeBlockLineVariant.Highlighted -> theme.colors.muted
            CodeBlockLineVariant.Removed -> theme.colors.destructive.copy(alpha = 0.1f)
            else -> androidx.compose.ui.graphics.Color.Transparent
        }
    Box(modifier = modifier.layoutId(lineNumber ?: "").fillMaxWidth().drawBehind { drawRect(background) }) {
        Row(
            modifier =
                Modifier.fillMaxWidth().padding(
                    horizontal = inset,
                ).heightIn(min = if (context.size == CodeBlockSize.Sm) 19.5.dp else 22.75.dp),
            verticalAlignment = Alignment.Top,
        ) {
            DisableSelection {
                if (numbered) {
                    BasicText(
                        text = lineNumber?.toString().orEmpty(),
                        // Official line numbers are hidden from screen readers.
                        modifier =
                            Modifier.layout { measurable, constraints ->
                                val cell =
                                    measurable.measure(
                                        constraints.copy(minWidth = gutter.width, maxWidth = gutter.width),
                                    )
                                layout(cell.width, cell.height) { cell.placeRelative(0, 0) }
                            }.semantics { hideFromAccessibility() },
                        maxLines = 1,
                        softWrap = false,
                        style =
                            textStyle.copy(
                                color = theme.colors.mutedForeground,
                                textAlign = androidx.compose.ui.text.style.TextAlign.End,
                            ),
                    )
                }
            }
            if (numbered) androidx.compose.foundation.layout.Spacer(Modifier.width(12.dp))
            CompositionLocalProvider(LocalDefaultTextStyle provides textStyle) {
                androidx.compose.foundation.layout.Box(if (wrap) Modifier.weight(1f) else Modifier) { content() }
            }
        }
        val marker =
            when (variant) {
                CodeBlockLineVariant.Added -> "+"
                CodeBlockLineVariant.Removed -> "−"
                else -> ""
            }
        if (marker.isNotEmpty()) {
            DisableSelection {
                BasicText(
                    marker,
                    modifier = Modifier.offset(x = 2.dp),
                    style =
                        textStyle.copy(
                            color =
                                if (variant == CodeBlockLineVariant.Removed) {
                                    theme.colors.destructive
                                } else {
                                    theme.colors.mutedForeground
                                },
                        ),
                )
            }
        }
    }
}

private const val CLIPPED_SCROLL_FADE_HEIGHT_FRACTION = 0.12f
private val ClippedScrollFadeMaxHeight = 40.dp

/**
 * Static `scroll-fade` trailing mask for a clipped, non-scrollable block. Matches the official
 * `max-h-* scroll-fade-y overflow-y-hidden` combination: the bottom edge fades even though the
 * content itself cannot scroll.
 */
private fun Modifier.clippedScrollFade(): Modifier =
    graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }.drawWithContent {
        drawContent()
        val depth = minOf(size.height * CLIPPED_SCROLL_FADE_HEIGHT_FRACTION, ClippedScrollFadeMaxHeight.toPx())
        if (depth <= 0f || depth >= size.height) return@drawWithContent
        drawRect(
            brush =
                Brush.verticalGradient(
                    0f to Color.Black,
                    (size.height - depth) / size.height to Color.Black,
                    1f to Color.Transparent,
                ),
            blendMode = BlendMode.DstIn,
        )
    }
