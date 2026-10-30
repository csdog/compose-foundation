@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.attachment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AttachmentScopeTest : ScreenshotTest() {
    private fun corner(tag: String) = composeRule.onNodeWithTag(tag).captureToImage().toPixelMap()[2, 3].green

    @Test fun mediaCornerTracksRootSizeChanges() {
        var size by mutableStateOf(AttachmentSize.Default)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Attachment(size = size) {
                AttachmentMedia(Modifier.testTag("media"), style = Style { background(Color.Red) }) { }
                AttachmentContent("file.pdf")
            }
        }
        for (value in listOf(AttachmentSize.Default, AttachmentSize.Sm, AttachmentSize.Xs, AttachmentSize.Default)) {
            composeRule.runOnIdle { size = value }
            if (value == AttachmentSize.Xs) {
                assertTrue(corner("media") < .25f, "Xs rounded-md8dp corner fills this pixel")
            } else {
                assertTrue(corner("media") > .4f, "Default/Sm rounded-lg10dp corner leaves this pixel clear")
            }
        }
    }

    @Test fun nestedRootsDoNotBorrowOuterSize() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Attachment(size = AttachmentSize.Xs) {
                Column {
                    AttachmentMedia(Modifier.testTag("outer"), style = Style { background(Color.Red) }) { }
                    Attachment {
                        AttachmentMedia(Modifier.testTag("inner"), style = Style { background(Color.Red) }) { }
                    }
                }
            }
        }
        assertTrue(corner("outer") < .25f)
        assertTrue(corner("inner") > .4f)
    }

    @Test fun actionsTrackOrientationAndKeepPhysicalRightInRtl() {
        var orientation by mutableStateOf(AttachmentOrientation.Horizontal)
        var direction by mutableStateOf(LayoutDirection.Ltr)
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                Attachment(Modifier.testTag("card"), orientation = orientation, actions = {
                    AttachmentActions(Modifier.testTag("actions")) {
                        Box(Modifier.size(16.dp).testTag("a"))
                        Box(Modifier.size(16.dp).testTag("b"))
                    }
                }) {
                    AttachmentMedia { }
                    AttachmentContent("file.pdf")
                }
            }
        }
        for (dir in listOf(
            LayoutDirection.Ltr,
            LayoutDirection.Rtl,
        )) for (o in listOf(
            AttachmentOrientation.Horizontal,
            AttachmentOrientation.Vertical,
            AttachmentOrientation.Horizontal,
        )) {
            composeRule.runOnIdle {
                direction = dir
                orientation = o
            }
            val a = composeRule.onNodeWithTag("a").getUnclippedBoundsInRoot()
            val b = composeRule.onNodeWithTag("b").getUnclippedBoundsInRoot()
            val gap = if (dir == LayoutDirection.Ltr)b.left - a.right else a.left - b.right
            assertEquals(if (o == AttachmentOrientation.Vertical)4f else 0f, gap.value, .1f)
            if (o == AttachmentOrientation.Vertical) {
                val card = composeRule.onNodeWithTag("card").getUnclippedBoundsInRoot()
                val actions = composeRule.onNodeWithTag("actions").getUnclippedBoundsInRoot()
                assertEquals(13f, (card.right - actions.right).value, .1f)
            }
        }
    }

    @Test fun extractedScopeHelpersKeepRootConfiguration() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Attachment(size = AttachmentSize.Xs) { ScopedMedia() }
        }
        val image = composeRule.onNodeWithTag("extracted").captureToImage()
        assertEquals(28, image.width)
        assertTrue(corner("extracted") < .25f)
    }

    @Test fun oldAndScopedJvmEntryPointsBothExist() {
        val signatures =
            Class.forName("com.toutiaoyan.compose.ui.components.attachment.AttachmentKt")
                .declaredMethods.filter { it.name == "Attachment" }.map {
                        method ->
                    method.parameterTypes.map { it.name }
                }
        assertTrue(
            signatures.any { "kotlin.jvm.functions.Function2" in it },
            "Legacy composable-slot binary entry must remain",
        )
        assertTrue(signatures.any { "kotlin.jvm.functions.Function3" in it }, "Scoped composable-slot entry must exist")
    }
}

@Composable
private fun AttachmentScope.ScopedMedia() {
    AttachmentMedia(Modifier.testTag("extracted"), style = Style { background(Color.Red) }) { }
}
