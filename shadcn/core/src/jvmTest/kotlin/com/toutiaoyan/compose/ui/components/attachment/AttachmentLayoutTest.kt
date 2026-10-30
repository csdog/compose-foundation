@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.attachment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AttachmentLayoutTest : ScreenshotTest() {
    private fun bounds(tag: String): Rect =
        composeRule.onNodeWithTag(tag, useUnmergedTree = true).fetchSemanticsNode().boundsInRoot

    @Test fun longTitleReservesActionsAndTracksWidthAndDirection() {
        var width by mutableStateOf(384.dp)
        var direction by mutableStateOf(LayoutDirection.Ltr)
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                Attachment(Modifier.width(width).testTag("card"), actions = {
                    AttachmentActions(Modifier.testTag("actions")) {
                        AttachmentAction({}, label = "Remove file") { Text { "×" } }
                        AttachmentAction({}, label = "Retry file") { Text { "↻" } }
                    }
                }) {
                    AttachmentMedia(Modifier.testTag("media")) { }
                    AttachmentContent(Modifier.testTag("content")) {
                        AttachmentTitle(
                            "a-very-long-market-research-document-with-a-long-filename.pdf",
                            Modifier.testTag("title"),
                        )
                        AttachmentDescription("PDF · 2.4 MB")
                    }
                }
            }
        }
        for (w in listOf(384.dp, 260.dp, 384.dp)) for (dir in listOf(LayoutDirection.Ltr, LayoutDirection.Rtl)) {
            composeRule.runOnIdle {
                width = w
                direction = dir
            }
            val card = bounds("card")
            val media = bounds("media")
            val content = bounds("content")
            val actions = bounds("actions")
            assertEquals(w.value, card.width, .1f)
            assertEquals(58f, card.height, .1f)
            assertEquals(40f, media.width, .1f)
            if (dir == LayoutDirection.Ltr) {
                assertEquals(card.left + 9f, media.left, .1f)
                assertEquals(media.right + 8f, content.left, .1f)
                assertEquals(content.right + 8f, actions.left, .1f)
                assertEquals(card.right - 9f, actions.right, .1f)
            } else {
                assertEquals(card.right - 9f, media.right, .1f)
                assertEquals(media.left - 8f, content.right, .1f)
                assertEquals(content.left - 8f, actions.right, .1f)
                assertEquals(card.left + 9f, actions.left, .1f)
            }
            val layouts = mutableListOf<TextLayoutResult>()
            composeRule.onNodeWithTag(
                "title",
            ).performSemanticsAction(androidx.compose.ui.semantics.SemanticsActions.GetTextLayoutResult) {
                it(layouts)
            }
            val result = layouts.single()
            assertTrue(result.hasVisualOverflow, "Long title must exceed its allocated line")
            assertEquals(1, result.lineCount)
            assertEquals(content.width, bounds("title").width, .1f)
            assertEquals(androidx.compose.ui.text.style.TextOverflow.Ellipsis, result.layoutInput.overflow)
        }
    }

    @Test fun mediaAndGapsUseNovaSizeAndRootWidthIsAtLeast160() {
        var size by mutableStateOf(AttachmentSize.Default)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Attachment(Modifier.testTag("card"), size = size) {
                AttachmentMedia(Modifier.testTag("media")) { }
                AttachmentContent(Modifier.testTag("content")) { AttachmentTitle("x", Modifier.testTag("title")) }
            }
        }
        for ((s, mediaWidth, gap) in listOf(
            Triple(AttachmentSize.Default, 40f, 8f),
            Triple(AttachmentSize.Sm, 32f, 10f),
            Triple(AttachmentSize.Xs, 28f, 6f),
        )) {
            composeRule.runOnIdle { size = s }
            assertEquals(160f, bounds("card").width, .1f)
            assertEquals(mediaWidth, bounds("media").width, .1f)
            assertEquals(mediaWidth, bounds("media").height, .1f)
            assertEquals(gap, bounds("content").left - bounds("media").right, .1f)
            val layouts = mutableListOf<TextLayoutResult>()
            composeRule.onNodeWithTag(
                "title",
            ).performSemanticsAction(androidx.compose.ui.semantics.SemanticsActions.GetTextLayoutResult) {
                it(layouts)
            }
            assertEquals(
                if (s == AttachmentSize.Default) 14f else 12f,
                layouts.single().layoutInput.style.fontSize.value,
                .1f,
            )
        }
    }

    @Test fun styleWidthAndBackgroundOverrideTheActualCard() {
        val red = Color(0xffcc2255)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Attachment(
                Modifier.testTag("card"),
                style =
                    Style {
                        width(240.dp)
                        background(red)
                    },
            ) {
                AttachmentContent { AttachmentTitle("report.pdf") }
            }
        }
        assertEquals(240f, bounds("card").width, .1f)
        val image = composeRule.onNodeWithTag("card").captureToImage().toPixelMap()
        assertEquals(red, image[4, image.height / 2])
    }

    @Test fun verticalMediaFillsCardAndActionsDoNotConsumeColumnSpace() {
        var actions by mutableStateOf(true)
        var width by mutableStateOf(120.dp)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Attachment(
                Modifier.width(width).testTag("card"),
                orientation = AttachmentOrientation.Vertical,
                actions =
                    if (actions) {
                        (
                            {
                                AttachmentActions(Modifier.testTag("actions")) {
                                    AttachmentAction({}, label = "Remove image") { Text { "×" } }
                                }
                            }
                        )
                    } else {
                        null
                    },
            ) {
                AttachmentMedia(Modifier.testTag("media")) { }
                AttachmentContent(Modifier.testTag("content")) {
                    AttachmentTitle("workspace.png")
                    AttachmentDescription("PNG · 820 KB")
                }
            }
        }
        val height = bounds("card").height
        assertEquals(102f, bounds("media").width, .1f)
        assertEquals(102f, bounds("media").height, .1f)
        assertEquals(bounds("card").left + 13f, bounds("content").left, .1f)
        assertEquals(bounds("card").top + 13f, bounds("actions").top, .1f)
        assertEquals(bounds("card").right - 13f, bounds("actions").right, .1f)
        composeRule.runOnIdle { actions = false }
        assertEquals(height, bounds("card").height, .1f)
        composeRule.runOnIdle { width = 160.dp }
        assertEquals(142f, bounds("media").width, .1f)
        assertEquals(142f, bounds("media").height, .1f)
    }

    @Test fun actionsRemainIndependentlyClickableOnAnInteractiveCard() {
        var opened = 0
        var removed = 0
        setThemedContent(stylePreset = StylePreset.Nova) {
            Attachment(Modifier.width(300.dp).testTag("card"), onClick = { opened++ }, actions = {
                AttachmentActions { AttachmentAction({ removed++ }, label = "Remove report") { Text { "×" } } }
            }) {
                AttachmentMedia { }
                AttachmentContent { AttachmentTitle("report.pdf") }
            }
        }
        composeRule.onNodeWithContentDescription("Remove report").performTouchInput { click(center) }
        assertEquals(1, removed)
        assertEquals(0, opened)
        composeRule.onNodeWithTag("card").performTouchInput { click(center) }
        assertEquals(1, opened)
        assertEquals(1, removed)
        composeRule.onNodeWithContentDescription("Remove report").assertIsDisplayed()
    }

    @Test fun mediaSpinnerUsesRootSizeWhileActionsKeepTheirOwnIconSize() {
        var size by mutableStateOf(AttachmentSize.Default)
        var orientation by mutableStateOf(AttachmentOrientation.Horizontal)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Attachment(size = size, orientation = orientation, actions = {
                AttachmentActions {
                    AttachmentAction({}, label = "Cancel") {
                        com.toutiaoyan.compose.ui.components.spinner.Spinner(Modifier.testTag("action spinner"))
                    }
                }
            }) {
                AttachmentMedia {
                    com.toutiaoyan.compose.ui.components.spinner.Spinner(
                        Modifier.testTag("media spinner"),
                    )
                }
                AttachmentContent { AttachmentTitle("Uploading") }
            }
        }
        for ((s, expected) in listOf(
            AttachmentSize.Default to 16f,
            AttachmentSize.Sm to 16f,
            AttachmentSize.Xs to 14f,
        )) {
            composeRule.runOnIdle { size = s }
            assertEquals(expected, bounds("media spinner").width, .1f)
            assertEquals(
                expected.toInt(),
                composeRule.onNodeWithTag("media spinner", useUnmergedTree = true).captureToImage().width,
            )
            assertEquals(12f, bounds("action spinner").width, .1f)
            assertEquals(12, composeRule.onNodeWithTag("action spinner", useUnmergedTree = true).captureToImage().width)
        }
        composeRule.runOnIdle { orientation = AttachmentOrientation.Vertical }
        assertEquals(24f, bounds("media spinner").width, .1f)
        assertEquals(12f, bounds("action spinner").width, .1f)
        assertEquals(12, composeRule.onNodeWithTag("action spinner", useUnmergedTree = true).captureToImage().width)
    }

    @Test fun novaLayoutLight() = layoutSnapshot(false)

    @Test fun novaLayoutDark() = layoutSnapshot(true)

    private fun layoutSnapshot(dark: Boolean) {
        snapshot("attachment_nova_layout", darkTheme = dark, stylePreset = StylePreset.Nova) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Attachment(Modifier.width(260.dp), actions = {
                    AttachmentActions { AttachmentAction({}, label = "Remove file") { Text { "×" } } }
                }) {
                    AttachmentMedia { Text { "F" } }
                    AttachmentContent {
                        AttachmentTitle("a-very-long-market-research-document.pdf")
                        AttachmentDescription("PDF · 2.4 MB")
                    }
                }
                Attachment(
                    orientation = AttachmentOrientation.Vertical,
                    actions = { AttachmentActions { AttachmentAction({}, label = "Remove photo") { Text { "×" } } } },
                ) {
                    AttachmentMedia(AttachmentMediaVariant.Image) {
                        Box(Modifier.fillMaxSize().background(Color(0xff3366ff)))
                    }
                    AttachmentContent {
                        AttachmentTitle("workspace.png")
                        AttachmentDescription("PNG · 820 KB")
                    }
                }
            }
        }
    }

    @Test fun legacyPresetStillAllowsCallerStyleOverrides() {
        setThemedContent(stylePreset = StylePreset.Vega) {
            Attachment(Modifier.testTag("card"), style = Style { width(250.dp) }) {
                AttachmentContent { AttachmentTitle("report.pdf") }
            }
        }
        assertEquals(250f, bounds("card").width, .1f)
    }
}
