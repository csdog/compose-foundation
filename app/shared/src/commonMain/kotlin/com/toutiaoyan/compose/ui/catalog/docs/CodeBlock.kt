@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.hovered
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.LineHeightStyle
import com.composables.icons.lucide.FileCode
import com.composables.icons.lucide.Lucide
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.toutiaoyan.compose.ui.catalog.catalogMonoFontFamily
import androidx.compose.ui.text.withStyle
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.utils.clipboard.rememberCopyToClipboard

private val KOTLIN_KEYWORDS =
    setOf(
        "package", "import", "class", "interface", "object", "fun", "val", "var",
        "if", "else", "when", "for", "while", "do", "return", "true", "false", "null",
        "is", "as", "in", "this", "super", "override", "private", "internal", "public",
        "protected", "companion", "data", "sealed", "enum", "open", "abstract", "final",
        "by", "try", "catch", "finally", "throw", "suspend", "inline", "noinline",
        "crossinline", "reified", "out", "vararg", "typealias", "annotation", "const",
        "init", "constructor", "where", "actual", "expect", "get", "set",
    )

// Capture groups: 1 line comment, 2 double-quoted string, 3 annotation, 4 number, 5 identifier/keyword.
private val TOKEN_REGEX =
    Regex(
        "(//[^\\n]*)" +
            "|(\"(?:\\\\.|[^\"\\\\])*\")" +
            "|(@\\w+)" +
            "|\\b(\\d+(?:\\.\\d+)?[fFlL]?)\\b" +
            "|\\b([a-zA-Z_][a-zA-Z0-9_]*)\\b",
    )

/**
 * Minimal Kotlin syntax highlighter -- regex-tokenized, no external dependency.
 * Good enough for short catalog code samples; not a general-purpose lexer.
 */
private fun highlightKotlin(
    code: String,
    isLight: Boolean,
): AnnotatedString {
    // Same semantic colors as the site's github-light-default / vesper themes.
    val keywordColor = if (isLight) Color(0xFFCF222E) else Color(0xFFA0A0A0)
    val stringColor = if (isLight) Color(0xFF0A3069) else Color(0xFF99FFE4)
    val commentColor = if (isLight) Color(0xFF6E7781) else Color(0x948B8B8B)
    val annotationColor = if (isLight) Color(0xFF953800) else Color(0xFFFFC799)
    val numberColor = if (isLight) Color(0xFF0550AE) else Color(0xFFFFC799)

    return buildAnnotatedString {
        var lastIndex = 0
        for (match in TOKEN_REGEX.findAll(code)) {
            if (match.range.first > lastIndex) {
                append(code.substring(lastIndex, match.range.first))
            }
            val text = match.value
            val style =
                when {
                    match.groups[1] != null -> SpanStyle(color = commentColor)
                    match.groups[2] != null -> SpanStyle(color = stringColor)
                    match.groups[3] != null -> SpanStyle(color = annotationColor)
                    match.groups[4] != null -> SpanStyle(color = numberColor)
                    match.groups[5] != null && text in KOTLIN_KEYWORDS -> SpanStyle(color = keywordColor)
                    else -> null
                }
            if (style != null) {
                withStyle(style) { append(text) }
            } else {
                append(text)
            }
            lastIndex = match.range.last + 1
        }
        if (lastIndex < code.length) {
            append(code.substring(lastIndex))
        }
    }
}

/** Cross-fades between [label] and "Copied!" as [justCopied] flips -- a plain instant text
 *  swap read as a glitch, not a confirmation. */
@Composable
fun CopyLabel(
    justCopied: Boolean,
    label: String,
    typography: TextVariant = TextVariant.BodyMedium,
) {
    AnimatedContent(
        targetState = justCopied,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "copyLabel",
    ) { copied ->
        Text(variant = typography) { if (copied) "Copied!" else label }
    }
}

/** Neutral documentation chrome, separate from the component preview's palette. */
internal fun docCodeBackground(isLight: Boolean): Color = if (isLight) Color(0xFFF8F8F8) else Color(0xFF161616)

/** A copyable, horizontally scrollable snippet; language chooses highlighting, not a badge. */
@Composable
fun CodeBlock(
    code: String,
    modifier: Modifier = Modifier,
    showCopyButton: Boolean = true,
    lineNumbers: Boolean = false,
    language: String? = "Kotlin",
    title: String? = null,
    maxHeight: Dp? = null,
    shape: Shape = RoundedCornerShape(18.dp),
) {
    val light = currentTheme.isLight
    val background = docCodeBackground(light)
    val foreground = if (light) Color(0xFF1F2328) else Color.White
    val muted = if (light) Color(0xFF747474) else Color(0xFFA4A4A4)
    val hover = currentTheme.colors.accent.copy(alpha = if (light) 1f else .5f)
    val copyState = rememberCopyToClipboard(code)
    val highlighted = if (language == "Kotlin") highlightKotlin(code, light) else AnnotatedString(code)
    val lineHeight = if (lineNumbers) 26.5.sp else 24.5.sp
    val textStyle =
        TextStyle(
            fontFamily = catalogMonoFontFamily(),
            fontSize = 14.sp,
            lineHeight = lineHeight,
            fontWeight = FontWeight.Normal,
            color = foreground,
            lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
        )
    Box(modifier.clip(shape).background(background)) {
        Column(
            if (maxHeight != null) {
                Modifier.heightIn(
                    max = maxHeight,
                ).verticalScroll(rememberScrollState()).testTag("code-vertical-scroll")
            } else {
                Modifier
            },
        ) {
            if (title != null) {
                Row(
                    Modifier.fillMaxWidth().height(44.dp).padding(start = 12.dp, end = 44.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    DocIcon(Lucide.FileCode, tint = muted)
                    BasicText(
                        title,
                        style = TextStyle(fontFamily = catalogMonoFontFamily(), fontSize = 12.sp, color = muted),
                    )
                }
                Box(Modifier.fillMaxWidth().height(1.dp).background(currentTheme.colors.border.copy(alpha = .5f)))
            }
            Row(Modifier.fillMaxWidth().padding(vertical = 14.dp)) {
                if (lineNumbers) {
                    BasicText(
                        text = (1..code.lines().size).joinToString("\n"),
                        modifier = Modifier.width(64.dp).padding(end = 24.dp),
                        style = textStyle.copy(color = muted, textAlign = TextAlign.End),
                    )
                }
                Box(
                    Modifier.weight(
                        1f,
                    ).horizontalScroll(
                        rememberScrollState(),
                    ).testTag("code-scroll").padding(start = if (lineNumbers) 0.dp else 16.dp, end = 16.dp),
                ) {
                    SelectionContainer { BasicText(text = highlighted, style = textStyle) }
                }
            }
        }
        if (showCopyButton) {
            Button(
                onClick = copyState.copy,
                label = if (copyState.justCopied) "Copied" else "Copy",
                modifier =
                    Modifier.align(Alignment.TopEnd).padding(top = 12.dp, end = 8.dp)
                        .size(28.dp).testTag("code-copy").semantics { liveRegion = LiveRegionMode.Polite },
                variant = ButtonVariant.Ghost,
                size = ButtonSize.IconSm,
                style =
                    Style {
                        background(background)
                        hovered { background(hover) }
                    },
            ) {
                AnimatedContent(
                    targetState = copyState.justCopied,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "codeCopyIcon",
                ) { copied ->
                    DocIcon(
                        if (copied) CopiedCodeIcon else CopyCodeIcon,
                        tint = if (light) Color.Black else Color(0xFFA1A1A1),
                    )
                }
            }
        }
    }
}
