@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.alertdialog

import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.dialog.DialogFooter
import com.toutiaoyan.compose.ui.components.dialog.DialogHeader
import kotlin.test.Test

class AlertDialogScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("alert_dialog_states", darkTheme = darkTheme) {
            AlertDialog(open = true, onOpenChange = {}) {
                AlertDialogContent {
                    DialogHeader(
                        "Are you absolutely sure?",
                        description = "This action cannot be undone. This will permanently delete your account.",
                    )
                    DialogFooter {
                        AlertDialogCancel("Cancel")
                        AlertDialogAction("Continue", variant = ButtonVariant.Destructive)
                    }
                }
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)

    private fun closeButton(darkTheme: Boolean) {
        snapshot("alert_dialog_close_button", darkTheme = darkTheme) {
            AlertDialog(open = true, onOpenChange = {}) {
                AlertDialogContent(
                    showCloseButton = AlertDialogDefaults.ShowCloseButton,
                ) {
                    DialogHeader(
                        "Are you absolutely sure?",
                        description = "This action cannot be undone. This will permanently delete your account.",
                    )
                    DialogFooter {
                        AlertDialogCancel("Cancel")
                        AlertDialogAction("Continue", variant = ButtonVariant.Destructive)
                    }
                }
            }
        }
    }

    @Test
    fun close_button_light() = closeButton(darkTheme = false)

    @Test
    fun close_button_dark() = closeButton(darkTheme = true)
}
