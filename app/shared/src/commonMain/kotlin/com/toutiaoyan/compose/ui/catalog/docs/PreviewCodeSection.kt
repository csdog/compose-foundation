@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.hovered
import com.toutiaoyan.compose.ui.catalog.catalogViewportWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.theme.currentTheme

/** Live preview stays mounted when its source is expanded, matching component-preview-tabs.tsx. */
@Composable
fun PreviewCodeSection(
    code: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    headingLevel: Int = 2,
    minPreviewHeight: Dp = 288.dp,
    mobileMinPreviewHeight: Dp? = null,
    previewPadding: Dp = 40.dp,
    descriptionMarkdown: String? = null,
    headingAnchor: String? = null,
    previewBackground: (@Composable () -> Color)? = null,
    hideCode: Boolean = false,
    previewHeight: Dp? = null,
    previewVerticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    desktopPreviewInsetStart: Dp = 0.dp,
    preview: @Composable () -> Unit,
) {
    val mobile = catalogViewportWidth() < 640.dp
    val effectiveMinHeight = if (mobile) mobileMinPreviewHeight ?: minPreviewHeight else minPreviewHeight
    var showCode by remember(code) { mutableStateOf(false) }
    val shape = RoundedCornerShape(16.dp)
    val colors = currentTheme.colors
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (title != null) {
            DocHeading(title, level = headingLevel, anchorId = headingAnchor)
        }
        descriptionMarkdown?.let { DocParagraph(it) }
        Column(
            Modifier.padding(start = if (mobile) 0.dp else desktopPreviewInsetStart)
                .fillMaxWidth().clip(shape)
                .border(1.dp, currentTheme.colors.border, shape)
                .background(currentTheme.colors.background)
                // CSS border-box reserves its border; Compose border only draws.
                .padding(1.dp),
        ) {
            Box(
                modifier =
                    Modifier.fillMaxWidth().background(previewBackground?.invoke() ?: colors.background)
                        .then(
                            if (previewHeight != null) {
                                Modifier.height(previewHeight)
                            } else {
                                Modifier.heightIn(
                                    min =
                                    effectiveMinHeight,
                                )
                            },
                        ).padding(previewPadding),
                contentAlignment = Alignment.Center,
            ) {
                if (previewHeight != null) {
                    // CSS flex centering allows the chart aspect ratio to exceed the padded
                    // content box while keeping the complete preview at its fixed height.
                    Box(
                        Modifier.fillMaxHeight().wrapContentHeight(
                            align =
                            previewVerticalAlignment,
                            unbounded =
                            true,
                        ),
                    ) { preview() }
                } else {
                    preview()
                }
            }
            if (!hideCode) {
                Box(Modifier.fillMaxWidth().height(1.dp).background(currentTheme.colors.border))
                if (showCode) {
                    CodeBlock(
                        code = code,
                        modifier = Modifier.fillMaxWidth(),
                        lineNumbers = true,
                        maxHeight = 288.dp,
                        shape = RectangleShape,
                    )
                } else {
                    Box(Modifier.fillMaxWidth().height(108.dp).clip(RoundedCornerShape(0.dp))) {
                        CodeBlock(
                            code = code.lineSequence().take(3).joinToString("\n"),
                            modifier = Modifier.fillMaxWidth(),
                            showCopyButton = false,
                            lineNumbers = true,
                            shape = RectangleShape,
                        )
                        Box(
                            Modifier.matchParentSize().background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        docCodeBackground(currentTheme.isLight).copy(alpha = 0.6f),
                                        docCodeBackground(currentTheme.isLight),
                                    ),
                                ),
                            ),
                        )
                        Button(
                            text = "View Code",
                            onClick = { showCode = true },
                            modifier = Modifier.align(Alignment.Center),
                            variant = ButtonVariant.Outline,
                            size = ButtonSize.Md,
                            style =
                                Style {
                                    background(colors.background)
                                    hovered { background(colors.muted) }
                                },
                        )
                    }
                }
            }
        }
    }
}
