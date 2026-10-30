@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.alertdialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.WindowInfo
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import kotlin.test.assertTrue
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.dialog.DialogSize
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals

class AlertDialogNovaTest : ScreenshotTest() {
    @Test fun headerUsesAlertTypographyAndReactsToSmallSize() {
        var size by mutableStateOf(DialogSize.Default)
        var extra by mutableStateOf(false)
        setThemedContent(stylePreset = StylePreset.Nova) {
            AlertDialog(defaultOpen = true) {
                AlertDialogContent(size = size, modifier = Modifier.testTag("panel")) {
                    AlertDialogHeader {
                        AlertDialogTitle("Confirm changes")
                        AlertDialogDescription("Review before continuing.")
                    }
                    AlertDialogFooter {
                        AlertDialogCancel("Cancel")
                        AlertDialogAction("Continue")
                        if (extra) AlertDialogAction("Review")
                    }
                }
            }
        }

        fun check(centered: Boolean) {
            val title = composeRule.onNodeWithText("Confirm changes")
            val description = composeRule.onNodeWithText("Review before continuing.")
            val t = title.fetchSemanticsNode().boundsInRoot
            val d = description.fetchSemanticsNode().boundsInRoot
            val panel = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInRoot
            assertEquals(6f, d.top - t.bottom, .5f)
            val layouts = mutableListOf<TextLayoutResult>()
            title.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
            assertEquals(24.sp, layouts.single().layoutInput.style.lineHeight)
            title.assert(isHeading())
            if (centered) {
                assertEquals(panel.center.x, t.center.x, .5f)
                val cancel = composeRule.onNodeWithText("Cancel").fetchSemanticsNode().boundsInRoot
                val action = composeRule.onNodeWithText("Continue").fetchSemanticsNode().boundsInRoot
                assertEquals(140f, cancel.width, .5f)
                assertEquals(cancel.width, action.width, .5f)
                assertEquals(8f, action.left - cancel.right, .5f)
            } else {
                assertEquals(panel.left + 16f, t.left, .5f)
            }
        }
        check(false)
        composeRule.runOnIdle { size = DialogSize.Sm }
        check(true)
        composeRule.runOnIdle { extra = true }
        val first = composeRule.onNodeWithText("Cancel").fetchSemanticsNode().boundsInRoot
        val third = composeRule.onNodeWithText("Review").fetchSemanticsNode().boundsInRoot
        assertEquals(first.left, third.left, .5f)
        assertEquals(first.width, third.width, .5f)
        assertEquals(8f, third.top - first.bottom, .5f)
        composeRule.runOnIdle {
            extra = false
            size = DialogSize.Default
        }
        check(false)
    }

    @Test fun phoneFooterReversesVisualOrderAndButtonsRemainUsable() {
        var viewport by mutableStateOf(IntSize(390, 844))
        var closeRequests = 0
        val window =
            object : WindowInfo {
                override val isWindowFocused = true
                override val containerSize get() = viewport
            }
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalWindowInfo provides window) {
                AlertDialog(open = true, onOpenChange = { if (!it) closeRequests++ }) {
                    Column(Modifier.width(320.dp).padding(16.dp)) {
                        AlertDialogHeader("Confirm changes", "Review before continuing.")
                        AlertDialogFooter {
                            AlertDialogCancel("Cancel")
                            AlertDialogAction("Continue")
                        }
                    }
                }
            }
        }

        fun bounds(label: String) = composeRule.onNodeWithText(label).fetchSemanticsNode().boundsInRoot
        assertEquals(288f, bounds("Continue").width, .5f)
        assertEquals(8f, bounds("Cancel").top - bounds("Continue").bottom, .5f)
        val pixels = composeRule.onNodeWithText("Continue").captureToImage().toPixelMap()
        assertTrue(
            pixels[20, pixels.height / 2].red < .25f,
            "The primary background must stretch as well as the hit box",
        )
        composeRule.onNodeWithText("Continue").performClick()
        assertEquals(1, closeRequests)
        composeRule.onNodeWithText("Cancel").performClick()
        assertEquals(2, closeRequests)
        composeRule.runOnIdle { viewport = IntSize(1024, 768) }
        assertEquals(bounds("Cancel").top, bounds("Continue").top, .5f)
    }

    private fun defaultDialog(dark: Boolean) {
        setThemedContent(darkTheme = dark, stylePreset = StylePreset.Nova) {
            AlertDialog(defaultOpen = true) {
                AlertDialogContent(
                    "Are you absolutely sure?",
                    description =
                        "This action cannot be undone. " +
                            "This will permanently delete your account from our servers.",
                ) {
                    AlertDialogFooter {
                        AlertDialogCancel("Cancel")
                        AlertDialogAction("Continue")
                    }
                }
            }
        }
        captureNamed("alert_dialog_nova_default", dark)
    }

    @Test fun defaultLight() = defaultDialog(false)

    @Test fun defaultDark() = defaultDialog(true)
}
