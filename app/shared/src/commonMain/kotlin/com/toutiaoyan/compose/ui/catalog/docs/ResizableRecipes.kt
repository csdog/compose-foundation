@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.components.resizable.*
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.utils.rememberTheme

// region shared
@Composable
private fun resizableRecipeFrameStyle(): Style =
    rememberTheme {
        Style {
            shape(RoundedCornerShape(radius.lg))
            border(colors.border)
        }
    }

@Composable
private fun ResizableSourcePane(label: String) {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        // CSS flex-center keeps the glyph visible even when the25% pane's content box is shorter than one line.
        Text(
            modifier = Modifier.wrapContentSize(unbounded = true),
            textStyle =
                TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                ),
        ) { label }
    }
}

@Composable
private fun ResizableSourceNested(labels: List<String>) {
    ResizablePanelGroup(
        Modifier.widthIn(max = 384.dp).fillMaxWidth().height(202.dp).testTag("resizable-example"),
        style = resizableRecipeFrameStyle(),
    ) { first, second, _ ->
        ResizablePanel(first.testTag("resizable-one")) { ResizableSourcePane(labels[0]) }
        ResizableHandle(withHandle = true, modifier = Modifier.testTag("resizable-outer-handle"))
        ResizablePanel(second) {
            ResizablePanelGroup(orientation = Orientation.Vertical, initialFraction = 0.25f) { top, bottom, _ ->
                ResizablePanel(top.testTag("resizable-two")) { ResizableSourcePane(labels[1]) }
                ResizableHandle(withHandle = true, modifier = Modifier.testTag("resizable-inner-handle"))
                ResizablePanel(bottom.testTag("resizable-three")) { ResizableSourcePane(labels[2]) }
            }
        }
    }
}
// endregion shared

// region default
@Composable
internal fun ResizableDefaultRecipe() {
    ResizableSourceNested(listOf("One", "Two", "Three"))
}
// endregion default

// region vertical
@Composable
internal fun ResizableVerticalRecipe() {
    ResizablePanelGroup(
        Modifier.widthIn(max = 384.dp).fillMaxWidth().height(200.dp).testTag("resizable-example"),
        style =
            resizableRecipeFrameStyle(),
        orientation =
            Orientation.Vertical,
        initialFraction =
        0.25f,
    ) { first,
        second,
        _,
        ->
        ResizablePanel(first.testTag("resizable-one")) { ResizableSourcePane("Header") }
        ResizableHandle(modifier = Modifier.testTag("resizable-outer-handle"))
        ResizablePanel(second.testTag("resizable-two")) { ResizableSourcePane("Content") }
    }
}
// endregion vertical

// region handle
@Composable
internal fun ResizableHandleRecipe() {
    ResizablePanelGroup(
        Modifier.widthIn(max = 384.dp).fillMaxWidth().height(200.dp).testTag("resizable-example"),
        style = resizableRecipeFrameStyle(),
        initialFraction = 0.25f,
    ) { first, second, _ ->
        ResizablePanel(first.testTag("resizable-one")) { ResizableSourcePane("Sidebar") }
        ResizableHandle(withHandle = true, modifier = Modifier.testTag("resizable-outer-handle"))
        ResizablePanel(second.testTag("resizable-two")) { ResizableSourcePane("Content") }
    }
}
// endregion handle

// region rtl
@Composable
internal fun ResizableRtlRecipe(language: Int = 1) {
    val labels =
        listOf(
            listOf(
                "One",
                "Two",
                "Three",
            ),
            listOf(
                "واحد",
                "اثنان",
                "ثلاثة",
            ),
            listOf(
                "אחד",
                "שניים",
                "שלושה",
            ),
        )
    CompositionLocalProvider(
        LocalLayoutDirection provides
            if (language == 0) {
                LayoutDirection.Ltr
            } else {
                LayoutDirection.Rtl
            },
    ) {
        ResizableSourceNested(labels[language])
    }
}
// endregion rtl

internal val resizableOfficialExamples =
    listOf(
        ComponentExample(
            "Default",
            code = resizableDefaultRecipeCode,
            minPreviewHeight = 240.dp,
            preview = { ResizableDefaultRecipe() },
        ),
        ComponentExample(
            "Vertical",
            code = resizableVerticalRecipeCode,
            minPreviewHeight = 208.dp,
            descriptionMarkdown = "Use `orientation = Orientation.Vertical` for vertical resizing.",
            preview = { ResizableVerticalRecipe() },
        ),
        ComponentExample(
            "Handle",
            code = resizableHandleRecipeCode,
            minPreviewHeight = 208.dp,
            descriptionMarkdown =
                "Use `withHandle = true` on `ResizableHandle` to show a visible handle.",
            preview = { ResizableHandleRecipe() },
        ),
        ComponentExample(
            "RTL",
            code = resizableRtlRecipeCode,
            minPreviewHeight = 208.dp,
            previewPadding = 0.dp,
            descriptionMarkdown =
                "To enable RTL support in shadcn/ui, see the RTL configuration guide. The " +
                    "component mirrors in RTL: the first pane starts on the right and dragging " +
                    "moves the split in the opposite physical direction.",
            preview = {
                var language by remember { mutableIntStateOf(1) }
                RtlPreview(
                    language,
                    listOf(
                        "English",
                        "Arabic (العربية)",
                        "Hebrew (עברית)",
                    ),
                    {
                        language =
                            it
                    },
                ) { ResizableRtlRecipe(language) }
            },
        ),
    )
