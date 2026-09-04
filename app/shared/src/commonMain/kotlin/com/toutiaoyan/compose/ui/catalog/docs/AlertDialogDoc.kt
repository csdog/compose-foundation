@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.toutiaoyan.compose.ui.components.alertdialog.AlertDialog
import com.toutiaoyan.compose.ui.components.alertdialog.AlertDialogAction
import com.toutiaoyan.compose.ui.components.alertdialog.AlertDialogCancel
import com.toutiaoyan.compose.ui.components.alertdialog.AlertDialogContent
import com.toutiaoyan.compose.ui.components.alertdialog.AlertDialogDefaults
import com.toutiaoyan.compose.ui.components.alertdialog.AlertDialogDescription
import com.toutiaoyan.compose.ui.components.alertdialog.AlertDialogFooter
import com.toutiaoyan.compose.ui.components.alertdialog.AlertDialogHeader
import com.toutiaoyan.compose.ui.components.alertdialog.AlertDialogTitle
import com.toutiaoyan.compose.ui.components.alertdialog.AlertDialogTrigger
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.dialog.DialogSize
import com.toutiaoyan.compose.ui.components.emojitext.EmojiText

val alertDialogDoc =
    ComponentDoc(
        id = "alert-dialog",
        title = "Alert Dialog",
        description = "A Dialog that can't be dismissed by clicking outside -- requires an explicit choice.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.alertdialog.AlertDialog
            import com.toutiaoyan.compose.ui.components.alertdialog.AlertDialogAction
            import com.toutiaoyan.compose.ui.components.alertdialog.AlertDialogCancel
            import com.toutiaoyan.compose.ui.components.alertdialog.AlertDialogContent
            import com.toutiaoyan.compose.ui.components.alertdialog.AlertDialogTrigger

            var open by remember { mutableStateOf(false) }
            AlertDialog(open = open, onOpenChange = { open = it }) {
                AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                AlertDialogContent(
                    "Are you absolutely sure?",
                    description = "This action cannot be undone.",
                ) {
                    AlertDialogFooter {
                        AlertDialogCancel("Cancel")
                        AlertDialogAction("Continue", variant = ButtonVariant.Destructive)
                    }
                }
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        AlertDialog(open = open, onOpenChange = { open = it }) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description =
                                    "This action cannot be undone. This will permanently delete your account.",
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue", variant = ButtonVariant.Destructive)
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        AlertDialog(open = open, onOpenChange = { open = it }) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description =
                                    "This action cannot be undone. This will permanently delete your account.",
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue", variant = ButtonVariant.Destructive)
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "With icon",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        AlertDialog(open = open, onOpenChange = { open = it }) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent {
                                AlertDialogHeader {
                                    EmojiText("⚠️")
                                    AlertDialogTitle("Are you absolutely sure?")
                                    AlertDialogDescription("This action cannot be undone.")
                                }
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue", variant = ButtonVariant.Destructive)
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        AlertDialog(open = open, onOpenChange = { open = it }) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent {
                                AlertDialogHeader {
                                    EmojiText("⚠️")
                                    AlertDialogTitle("Are you absolutely sure?")
                                    AlertDialogDescription("This action cannot be undone.")
                                }
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue", variant = ButtonVariant.Destructive)
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Size",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        AlertDialog(open = open, onOpenChange = { open = it }) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "DialogSize.Sm is 320dp; Default stays 400dp.",
                                size = DialogSize.Sm,
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue", variant = ButtonVariant.Destructive)
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        AlertDialog(open = open, onOpenChange = { open = it }) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "DialogSize.Sm is 320dp; Default stays 400dp.",
                                size = DialogSize.Sm,
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue", variant = ButtonVariant.Destructive)
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default size",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        AlertDialog(open = open, onOpenChange = { open = it }) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "DialogSize.Default stays 400dp.",
                                size = DialogSize.Default,
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        AlertDialog(open = open, onOpenChange = { open = it }) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "DialogSize.Default stays 400dp.",
                                size = DialogSize.Default,
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Non-modal",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        AlertDialog(open = open, onOpenChange = { open = it }, modal = false) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "The page behind stays interactive.",
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        AlertDialog(open = open, onOpenChange = { open = it }, modal = false) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "The page behind stays interactive.",
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Modal",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        AlertDialog(open = open, onOpenChange = { open = it }, modal = true) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "The page behind is inert.",
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        AlertDialog(open = open, onOpenChange = { open = it }, modal = true) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "The page behind is inert.",
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default modal",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        AlertDialog(open = open, onOpenChange = { open = it }, modal = true) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "The page behind is inert.",
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        AlertDialog(open = open, onOpenChange = { open = it }, modal = true) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "The page behind is inert.",
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Dismissible",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        AlertDialog(open = open, onOpenChange = { open = it }) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "Clicking outside will close this dialog.",
                                dismissOnClickOutside = true,
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        AlertDialog(open = open, onOpenChange = { open = it }) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "Clicking outside will close this dialog.",
                                dismissOnClickOutside = true,
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default dismiss",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        AlertDialog(open = open, onOpenChange = { open = it }) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "Clicking outside does not close this dialog by default.",
                                dismissOnClickOutside = false,
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        AlertDialog(open = open, onOpenChange = { open = it }) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "Clicking outside does not close this dialog by default.",
                                dismissOnClickOutside = false,
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Close button",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        AlertDialog(open = open, onOpenChange = { open = it }) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "The panel includes a close button.",
                                showCloseButton = AlertDialogDefaults.ShowCloseButton,
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        AlertDialog(open = open, onOpenChange = { open = it }) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "The panel includes a close button.",
                                showCloseButton = AlertDialogDefaults.ShowCloseButton,
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default close button",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        AlertDialog(open = open, onOpenChange = { open = it }) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "There is no close button by default.",
                                showCloseButton = false,
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        AlertDialog(open = open, onOpenChange = { open = it }) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "There is no close button by default.",
                                showCloseButton = false,
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        AlertDialog(defaultOpen = false) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline, disabled = false)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "Starts closed.",
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel", disabled = false)
                                    AlertDialogAction("Continue", disabled = false)
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        AlertDialog(defaultOpen = false) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline, disabled = false)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "Starts closed.",
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel", disabled = false)
                                    AlertDialogAction("Continue", disabled = false)
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Prevent pointer dismiss",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        AlertDialog(open = open, onOpenChange = { open = it }) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "Clicking the overlay will not close this dialog.",
                                dismissOnClickOutside = true,
                                onPointerDownOutside = { true },
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        AlertDialog(open = open, onOpenChange = { open = it }) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "Clicking the overlay will not close this dialog.",
                                dismissOnClickOutside = true,
                                onPointerDownOutside = { true },
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Prevent interact outside",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        AlertDialog(open = open, onOpenChange = { open = it }) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "Interacting outside will not close this dialog.",
                                dismissOnClickOutside = true,
                                onInteractOutside = { true },
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        AlertDialog(open = open, onOpenChange = { open = it }) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "Interacting outside will not close this dialog.",
                                dismissOnClickOutside = true,
                                onInteractOutside = { true },
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Content close label",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        AlertDialog(open = open, onOpenChange = { open = it }) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "The built-in close button has a custom accessible name.",
                                showCloseButton = true,
                                closeAriaLabel = "Dismiss alert",
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        AlertDialog(open = open, onOpenChange = { open = it }) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "The built-in close button has a custom accessible name.",
                                showCloseButton = true,
                                closeAriaLabel = "Dismiss alert",
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default close label",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        AlertDialog(open = open, onOpenChange = { open = it }) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "The built-in close button uses the default accessible name.",
                                showCloseButton = true,
                                closeAriaLabel = "Close",
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        AlertDialog(open = open, onOpenChange = { open = it }) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "The built-in close button uses the default accessible name.",
                                showCloseButton = true,
                                closeAriaLabel = "Close",
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Uncontrolled",
                    code =
                        """
                        AlertDialog {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "Omit open and onOpenChange. Optional defaultOpen starts closed.",
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        AlertDialog {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "Omit open and onOpenChange. Optional defaultOpen starts closed.",
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default open",
                    code =
                        """
                        AlertDialog(defaultOpen = false) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "Starts closed.",
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        AlertDialog(defaultOpen = false) {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent(
                                "Are you absolutely sure?",
                                description = "Starts closed.",
                            ) {
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Title and description",
                    code =
                        """
                        AlertDialog {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent {
                                AlertDialogHeader {
                                    AlertDialogTitle("Are you absolutely sure?")
                                    AlertDialogDescription("Compose Title and Description inside Header.")
                                }
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        AlertDialog {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent {
                                AlertDialogHeader {
                                    AlertDialogTitle("Are you absolutely sure?")
                                    AlertDialogDescription("Compose Title and Description inside Header.")
                                }
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Footer",
                    code =
                        """
                        AlertDialog {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent {
                                AlertDialogHeader {
                                    AlertDialogTitle("Are you absolutely sure?")
                                    AlertDialogDescription("Footer holds Action and Cancel, matching shadcn AlertDialogFooter.")
                                }
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        AlertDialog {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent {
                                AlertDialogHeader {
                                    AlertDialogTitle("Are you absolutely sure?")
                                    AlertDialogDescription(
                                        "Footer holds Action and Cancel, matching shadcn AlertDialogFooter.",
                                    )
                                }
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Header",
                    code =
                        """
                        AlertDialog {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent {
                                AlertDialogHeader {
                                    AlertDialogTitle("Are you absolutely sure?")
                                    AlertDialogDescription("Compose Title and Description inside Header.")
                                }
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        AlertDialog {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent {
                                AlertDialogHeader {
                                    AlertDialogTitle("Are you absolutely sure?")
                                    AlertDialogDescription("Compose Title and Description inside Header.")
                                }
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Action",
                    code =
                        """
                        AlertDialog {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent {
                                AlertDialogHeader {
                                    AlertDialogTitle("Are you absolutely sure?")
                                    AlertDialogDescription("Action confirms; Cancel dismisses.")
                                }
                                AlertDialogFooter {
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        AlertDialog {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent {
                                AlertDialogHeader {
                                    AlertDialogTitle("Are you absolutely sure?")
                                    AlertDialogDescription("Action confirms; Cancel dismisses.")
                                }
                                AlertDialogFooter {
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Cancel",
                    code =
                        """
                        AlertDialog {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent {
                                AlertDialogHeader {
                                    AlertDialogTitle("Are you absolutely sure?")
                                    AlertDialogDescription("Action confirms; Cancel dismisses.")
                                }
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        AlertDialog {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent {
                                AlertDialogHeader {
                                    AlertDialogTitle("Are you absolutely sure?")
                                    AlertDialogDescription("Action confirms; Cancel dismisses.")
                                }
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Trigger",
                    code =
                        """
                        AlertDialog {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent {
                                AlertDialogHeader {
                                    AlertDialogTitle("Are you absolutely sure?")
                                }
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        AlertDialog {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent {
                                AlertDialogHeader {
                                    AlertDialogTitle("Are you absolutely sure?")
                                }
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Description",
                    code =
                        """
                        AlertDialog {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent {
                                AlertDialogHeader {
                                    AlertDialogTitle("Are you absolutely sure?")
                                    AlertDialogDescription("AlertDialogDescription matches shadcn's description slot.")
                                }
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        AlertDialog {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent {
                                AlertDialogHeader {
                                    AlertDialogTitle("Are you absolutely sure?")
                                    AlertDialogDescription("AlertDialogDescription matches shadcn's description slot.")
                                }
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Overlay",
                    code =
                        """
                        AlertDialog {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent {
                                AlertDialogHeader {
                                    AlertDialogTitle("Are you absolutely sure?")
                                    AlertDialogDescription("AlertDialogOverlay is the scrim composed by Content.")
                                }
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        AlertDialog {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent {
                                AlertDialogHeader {
                                    AlertDialogTitle("Are you absolutely sure?")
                                    AlertDialogDescription("AlertDialogOverlay is the scrim composed by Content.")
                                }
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Content",
                    code =
                        """
                        AlertDialog {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent {
                                AlertDialogHeader {
                                    AlertDialogTitle("Are you absolutely sure?")
                                    AlertDialogDescription("AlertDialogContent hosts Header, Title, and Description.")
                                }
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        AlertDialog {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent {
                                AlertDialogHeader {
                                    AlertDialogTitle("Are you absolutely sure?")
                                    AlertDialogDescription("AlertDialogContent hosts Header, Title, and Description.")
                                }
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Title",
                    code =
                        """
                        AlertDialog {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent {
                                AlertDialogHeader {
                                    AlertDialogTitle("Are you absolutely sure?")
                                    AlertDialogDescription("Compose Title and Description inside Header.")
                                }
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        AlertDialog {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent {
                                AlertDialogHeader {
                                    AlertDialogTitle("Are you absolutely sure?")
                                    AlertDialogDescription("Compose Title and Description inside Header.")
                                }
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Portal",
                    code =
                        """
                        AlertDialog {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent {
                                AlertDialogHeader {
                                    AlertDialogTitle("Are you absolutely sure?")
                                    AlertDialogDescription("AlertDialogPortal is the teleport host composed by Content.")
                                }
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        AlertDialog {
                            AlertDialogTrigger("Show Dialog", variant = ButtonVariant.Outline)
                            AlertDialogContent {
                                AlertDialogHeader {
                                    AlertDialogTitle("Are you absolutely sure?")
                                    AlertDialogDescription(
                                        "AlertDialogPortal is the teleport host composed by Content.",
                                    )
                                }
                                AlertDialogFooter {
                                    AlertDialogCancel("Cancel")
                                    AlertDialogAction("Continue")
                                }
                            }
                        }
                    },
                ),
            ),
    )
