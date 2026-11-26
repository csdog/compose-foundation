@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.dialog

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.Button
import kotlin.test.Test

class DialogScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("dialog_states", darkTheme = darkTheme) {
            Dialog(open = true, onOpenChange = {}) {
                DialogContent(
                    "Edit profile",
                    description = "Make changes to your profile here. Click save when you're done.",
                ) {
                    DialogFooter {
                        Button("Save changes")
                    }
                }
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)

    private fun closeButtonFocused(darkTheme: Boolean) {
        snapshotFocused("dialog_close_button_focused", focusTag = "dialog", darkTheme = darkTheme) {
            Dialog(open = true, onOpenChange = {}) {
                DialogContent(modifier = Modifier.testTag("dialog")) {
                    DialogHeader("Edit profile")
                }
            }
        }
    }

    @Test
    fun close_button_focused_light() = closeButtonFocused(darkTheme = false)
}
