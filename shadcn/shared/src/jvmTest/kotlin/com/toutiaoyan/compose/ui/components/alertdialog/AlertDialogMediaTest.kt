@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.alertdialog

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.test.*
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.dialog.DialogSize
import com.toutiaoyan.compose.ui.components.icon.LocalInlineIconSize
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AlertDialogMediaTest : ScreenshotTest() {
    @Test fun compactFooterMeasuresAndPreservesActionOrderInUnboundedWidth() {
        val window =
            object : WindowInfo {
                override val isWindowFocused = true
                override val containerSize = IntSize(390, 844)
            }
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalWindowInfo provides window) {
                Row(Modifier.horizontalScroll(rememberScrollState())) {
                    AlertDialogFooter {
                        Box(Modifier.width(48.dp).height(24.dp).testTag("cancel"))
                        Box(Modifier.width(80.dp).height(24.dp).testTag("continue"))
                    }
                }
            }
        }
        val cancel = composeRule.onNodeWithTag("cancel").getUnclippedBoundsInRoot()
        val proceed = composeRule.onNodeWithTag("continue").getUnclippedBoundsInRoot()
        assertEquals(80.dp, cancel.width)
        assertEquals(80.dp, proceed.width)
        assertEquals(8.dp, cancel.top - proceed.bottom)
    }

    @Test fun mediaTracksDirectionBreakpointAndRemoval() {
        var viewport by mutableStateOf(IntSize(1024, 768))
        var direction by mutableStateOf(LayoutDirection.Ltr)
        var media by mutableStateOf(true)
        val window =
            object : WindowInfo {
                override val isWindowFocused = true
                override val containerSize get() = viewport
            }
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalWindowInfo provides window, LocalLayoutDirection provides direction) {
                Box(Modifier.width(352.dp)) {
                    AlertDialogHeader(Modifier.testTag("header")) {
                        if (media) {
                            AlertDialogMedia(Modifier.testTag("media")) {
                                Box(Modifier.size(LocalInlineIconSize.current!!).testTag("glyph"))
                            }
                        }
                        AlertDialogTitle("Share this project?")
                        AlertDialogDescription("Anyone with the link will be able to view and edit this project.")
                    }
                }
            }
        }

        fun node(tag: String) = composeRule.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot

        fun title() = composeRule.onNodeWithText("Share this project?").fetchSemanticsNode().boundsInRoot
        assertEquals(40f, node("media").width, .5f)
        assertEquals(24f, node("glyph").width, .5f)
        assertEquals(16f, title().left - node("media").right, .5f)
        assertEquals(node("media").top, title().top, .5f)
        composeRule.runOnIdle { direction = LayoutDirection.Rtl }
        assertEquals(16f, node("media").left - title().right, .5f)
        composeRule.runOnIdle { viewport = IntSize(390, 844) }
        assertEquals(node("header").center.x, title().center.x, .5f)
        assertEquals(node("header").center.x, node("media").center.x, .5f)
        assertEquals(14f, title().top - node("media").bottom, .5f)
        composeRule.runOnIdle { media = false }
        assertEquals(node("header").top, title().top, .5f)
        composeRule.onNodeWithTag("glyph").assertDoesNotExist()
    }

    @Test fun smallMediaStaysCenteredOnDesktop() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            AlertDialog(defaultOpen = true) {
                AlertDialogContent(size = DialogSize.Sm) {
                    AlertDialogHeader(Modifier.testTag("header")) {
                        AlertDialogMedia(Modifier.testTag("media")) { Box(Modifier.size(24.dp)) }
                        AlertDialogTitle("Allow accessory to connect?")
                        AlertDialogDescription("Do you want to allow the USB accessory to connect to this device?")
                    }
                }
            }
        }
        val title = composeRule.onNodeWithText("Allow accessory to connect?").fetchSemanticsNode().boundsInRoot
        val media = composeRule.onNodeWithTag("media").fetchSemanticsNode().boundsInRoot
        val header = composeRule.onNodeWithTag("header").fetchSemanticsNode().boundsInRoot
        assertEquals(header.center.x, media.center.x, .5f)
        assertEquals(header.center.x, title.center.x, .5f)
        assertEquals(14f, title.top - media.bottom, .5f)
    }

    @Test fun mediaHeaderStillMeasuresInUnboundedWidth() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Row(Modifier.horizontalScroll(rememberScrollState())) {
                AlertDialogHeader {
                    AlertDialogMedia { Box(Modifier.size(24.dp)) }
                    AlertDialogTitle("Visible title")
                    AlertDialogDescription("Visible description")
                }
            }
        }
        val bounds = composeRule.onNodeWithText("Visible title").fetchSemanticsNode().boundsInRoot
        assertTrue(bounds.width > 20f && bounds.height > 0f)
    }
}
