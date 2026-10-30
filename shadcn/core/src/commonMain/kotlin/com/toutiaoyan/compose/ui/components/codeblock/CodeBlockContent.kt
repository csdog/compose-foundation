package com.toutiaoyan.compose.ui.components.codeblock

import androidx.compose.foundation.focusable
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
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.components.text.LocalDefaultTextStyle
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.currentTheme

sealed interface CodeBlockLineVariant {
    data object Default : CodeBlockLineVariant

    data object Added : CodeBlockLineVariant

    data object Removed : CodeBlockLineVariant

    data object Highlighted : CodeBlockLineVariant
}

class CodeBlockContentScope internal constructor(
    context: CodeBlockContext,
    internal val numbered: Boolean,
    internal val startLine: Int,
    internal val wrap: Boolean,
    internal val textStyle: TextStyle,
) : CodeBlockPartsScope(context)

@Composable
fun CodeBlockScope.CodeBlockContent(
    code: String,
    modifier: Modifier = Modifier,
    wrap: Boolean = false,
    lineNumbers: Boolean = false,
    startLine: Int = 1,
    textStyle: TextStyle = TextStyle.Default,
) {
    CodeBlockContent(modifier, wrap, lineNumbers, startLine, textStyle) {
        code.removeSuffix("\n").split('\n').forEachIndexed { index, line ->
            CodeBlockLine(text = line, lineNumber = startLine + index)
        }
    }
}

/** Rich/diff lines specify their line numbers explicitly, avoiding a composition-time counter. */
@Composable
fun CodeBlockScope.CodeBlockContent(
    modifier: Modifier = Modifier,
    wrap: Boolean = false,
    lineNumbers: Boolean = false,
    startLine: Int = 1,
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
    val scope = CodeBlockContentScope(context, lineNumbers, startLine, wrap, mono)
    BoxWithConstraints(
        modifier =
            modifier.fillMaxWidth().codeSlot(CodeSlot.Content)
                .onFocusChanged { context.onContentFocus(it.isFocused) }.focusable(),
    ) {
        val viewportWidth = maxWidth
        SelectionContainer {
            val scrolling = if (wrap) Modifier else Modifier.horizontalScroll(rememberScrollState())
            Column(scrolling) {
                Column(
                    if (wrap) {
                        Modifier.fillMaxWidth()
                    } else {
                        Modifier.widthIn(
                            min = viewportWidth,
                        ).width(IntrinsicSize.Max)
                    },
                ) {
                    scope.content()
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
    Box(modifier = modifier.fillMaxWidth().drawBehind { drawRect(background) }) {
        Row(
            modifier =
                Modifier.fillMaxWidth().drawBehind {
                    drawRect(background)
                    // The +/- markers are drawn outside the selectable code and numbering gutter.
                }.padding(
                    horizontal = inset,
                ).heightIn(min = if (context.size == CodeBlockSize.Sm) 19.5.dp else 22.75.dp),
            verticalAlignment = Alignment.Top,
        ) {
            DisableSelection {
                if (numbered) {
                    BasicText(
                        text = lineNumber?.toString().orEmpty(),
                        modifier = Modifier.width(24.dp),
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
