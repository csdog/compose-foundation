package com.toutiaoyan.compose.ui.catalog.docs

import com.toutiaoyan.compose.ui.catalog.catalogViewportWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.style.TextDecoration
import com.toutiaoyan.compose.ui.catalog.LocalDocHeadingLinks
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.currentTheme

@Composable
private fun mobileDocs() = catalogViewportWidth() < 640.dp

@Composable
internal fun DocHeading(
    text: String,
    modifier: Modifier = Modifier,
    level: Int = 2,
    anchorId: String? = null,
) {
    val mobile = mobileDocs()
    val typography =
        TextStyle(
            fontSize =
                if (level == 3) {
                    if (mobile) 21.2625.sp else 16.875.sp
                } else {
                    if (mobile) 23.625.sp else 18.75.sp
                },
            lineHeight =
                if (level == 3) {
                    if (mobile) 30.8306.sp else 24.4688.sp
                } else {
                    if (mobile) 33.075.sp else 26.25.sp
                },
            fontWeight = FontWeight.SemiBold,
            lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
        )
    val link = LocalDocHeadingLinks.current[anchorId]
    if (link == null) {
        Text(modifier = modifier.semantics { heading() }, textStyle = typography) { text }
        return
    }
    val interactions = remember { MutableInteractionSource() }
    val hovered by interactions.collectIsHoveredAsState()
    var focused by remember { mutableStateOf(false) }
    Row(
        modifier.hoverable(interactions).onFocusChanged {
            focused = it.hasFocus
        },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            modifier =
                Modifier.weight(1f, fill = false).semantics {
                    heading()
                },
            textStyle =
                typography.copy(
                    textDecoration = if (hovered) TextDecoration.Underline else TextDecoration.None,
                ),
        ) {
            buildAnnotatedString {
                withLink(
                    LinkAnnotation.Url(
                        "#${link.fragment}",
                        styles = TextLinkStyles(focusedStyle = SpanStyle(textDecoration = TextDecoration.Underline)),
                    ) {
                        link.onClick()
                    },
                ) { append(text) }
            }
        }
        Text(
            modifier =
                Modifier.alpha(if (hovered || focused) 1f else 0f)
                    .focusProperties { canFocus = false }.clickable(onClick = link.onClick).clearAndSetSemantics {},
            textStyle = typography.copy(color = currentTheme.colors.mutedForeground),
        ) { "#" }
    }
}

/** Paragraphs support inline code and links; the unchanged Markdown is also exported. */
@Composable
internal fun DocParagraph(markdown: String) {
    val mobile = mobileDocs()
    val fontSize = if (mobile) 18.9.sp else 15.sp
    InlineDocParagraph(markdown, fontSize, if (mobile) 33.075.sp else 26.25.sp)
}
