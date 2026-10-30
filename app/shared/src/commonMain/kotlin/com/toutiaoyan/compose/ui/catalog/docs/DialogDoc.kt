@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.X
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.dialog.Dialog
import com.toutiaoyan.compose.ui.components.dialog.DialogClose
import com.toutiaoyan.compose.ui.components.dialog.DialogContent
import com.toutiaoyan.compose.ui.components.dialog.DialogDescription
import com.toutiaoyan.compose.ui.components.dialog.DialogFooter
import com.toutiaoyan.compose.ui.components.dialog.DialogHeader
import com.toutiaoyan.compose.ui.components.dialog.DialogSize
import com.toutiaoyan.compose.ui.components.dialog.DialogTitle
import com.toutiaoyan.compose.ui.components.dialog.DialogTrigger
import com.toutiaoyan.compose.ui.components.scrollarea.ScrollArea
import com.toutiaoyan.compose.ui.components.text.Text

val dialogDoc =
    ComponentDoc(
        id = "dialog",
        title = "Dialog",
        description = (
            "A window overlaid on either the primary window or another " +
                "dialog window, rendering the content underneath inert."
        ),
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.dialog.Dialog
            import com.toutiaoyan.compose.ui.components.dialog.DialogClose
            import com.toutiaoyan.compose.ui.components.dialog.DialogContent
            import com.toutiaoyan.compose.ui.components.dialog.DialogTrigger

            var open by remember { mutableStateOf(false) }
            Dialog(open = open, onOpenChange = { open = it }) {
                DialogTrigger("Edit profile")
                DialogContent("Edit profile", description = "Make changes to your profile here.") {
                    DialogFooter {
                        Button("Save changes", onClick = { open = false })
                    }
                }
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Edit profile",
                    code =
                        """
                        @Composable
                        fun DialogParityExample() {
                            var open by remember { mutableStateOf(false) }
                            var name by remember { mutableStateOf("Pedro Duarte") }
                            var username by remember { mutableStateOf("@peduarte") }
                            val mobile = LocalWindowInfo.current.containerSize.width < with(LocalDensity.current) { 640.dp.roundToPx() }
                            Dialog(open = open, onOpenChange = { open = it }) {
                                DialogTrigger("Open Dialog", variant = ButtonVariant.Outline)
                                DialogContent("Edit profile", description = "Make changes to your profile here. Click save when you're done.") {
                                    FieldGroup {
                                        Field("Name") { Input(value = name, onValueChange = { name = it }) }
                                        Field("Username") { Input(value = username, onValueChange = { username = it }) }
                                    }
                                    DialogFooter {
                                        if (mobile) Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button("Save changes", onClick = { open = false }, modifier = Modifier.fillMaxWidth(), style = Style { width(1f) })
                                            Button("Cancel", onClick = { open = false }, variant = ButtonVariant.Outline, modifier = Modifier.fillMaxWidth(), style = Style { width(1f) })
                                        } else {
                                            Button("Cancel", onClick = { open = false }, variant = ButtonVariant.Outline)
                                            Button("Save changes", onClick = { open = false })
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { DialogParityExample() },
                ),
                ComponentExample(
                    title = "Custom close icon",
                    code =
                        """
                        // Dialog's close button is a plain glyph by default (this
                        // library takes no icon-library dependency) -- override the closeIcon
                        // slot with a real vector from any icon set, e.g. Lucide.
                        var open by remember { mutableStateOf(false) }
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Open dialog")
                            DialogContent(
                                "Edit profile",
                                description = "Make changes to your profile here.",
                                closeIcon = { DocIcon(Lucide.X) },
                            ) { }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Open dialog")
                            DialogContent(
                                "Edit profile",
                                description = "Make changes to your profile here.",
                                closeIcon = { DocIcon(Lucide.X) },
                            ) { }
                        }
                    },
                ),
                ComponentExample(
                    title = "Without close button",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Open dialog")
                            DialogContent(
                                "Edit profile",
                                description = "Make changes to your profile here.",
                                showCloseButton = false,
                            ) {
                                DialogFooter {
                                    Button("Done", onClick = { open = false })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Open dialog")
                            DialogContent(
                                "Edit profile",
                                description = "Make changes to your profile here.",
                                showCloseButton = false,
                            ) {
                                DialogFooter {
                                    Button("Done", onClick = { open = false })
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
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Open dialog")
                            DialogContent(
                                "Edit profile",
                                description = "Make changes to your profile here.",
                                showCloseButton = true,
                            ) {
                                DialogFooter {
                                    Button("Done", onClick = { open = false })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Open dialog")
                            DialogContent(
                                "Edit profile",
                                description = "Make changes to your profile here.",
                                showCloseButton = true,
                            ) {
                                DialogFooter {
                                    Button("Done", onClick = { open = false })
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Scrollable content",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Open dialog")
                            DialogContent("Notice", description = "A list that scrolls under the header.") {
                                ScrollArea(modifier = Modifier.height(160.dp)) {
                                    Column {
                                        repeat(12) { index ->
                                            Text(modifier = Modifier.padding(vertical = 6.dp)) { "Item ${'$'}index" }
                                        }
                                    }
                                }
                                DialogFooter {
                                    Button("Close", onClick = { open = false })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Open dialog")
                            DialogContent("Notice", description = "A list that scrolls under the header.") {
                                ScrollArea(modifier = Modifier.height(160.dp)) {
                                    Column {
                                        repeat(12) { index ->
                                            Text(modifier = Modifier.padding(vertical = 6.dp)) { "Item $index" }
                                        }
                                    }
                                }
                                DialogFooter {
                                    Button("Close", onClick = { open = false })
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Sticky footer",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Open dialog")
                            DialogContent("Notice", description = "Header and footer stay put while the list scrolls.") {
                                ScrollArea(modifier = Modifier.height(160.dp)) {
                                    Column {
                                        repeat(12) { index ->
                                            Text(modifier = Modifier.padding(vertical = 6.dp)) { "Item ${'$'}index" }
                                        }
                                    }
                                }
                                DialogFooter {
                                    Button("Close", onClick = { open = false })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Open dialog")
                            DialogContent(
                                "Notice",
                                description = "Header and footer stay put while the list scrolls.",
                            ) {
                                ScrollArea(modifier = Modifier.height(160.dp)) {
                                    Column {
                                        repeat(12) { index ->
                                            Text(modifier = Modifier.padding(vertical = 6.dp)) { "Item $index" }
                                        }
                                    }
                                }
                                DialogFooter {
                                    Button("Close", onClick = { open = false })
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
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Small dialog")
                            DialogContent(
                                "Small",
                                description = "DialogSize.Sm is 320dp; Default stays 400dp.",
                                size = DialogSize.Sm,
                            ) {
                                DialogFooter {
                                    Button("Close", onClick = { open = false })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Small dialog")
                            DialogContent(
                                "Small",
                                description = "DialogSize.Sm is 320dp; Default stays 400dp.",
                                size = DialogSize.Sm,
                            ) {
                                DialogFooter {
                                    Button("Close", onClick = { open = false })
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
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Open dialog")
                            DialogContent(
                                "Default size",
                                description = "DialogSize.Default stays 400dp.",
                                size = DialogSize.Default,
                            ) {
                                DialogFooter {
                                    Button("Close", onClick = { open = false })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Open dialog")
                            DialogContent(
                                "Default size",
                                description = "DialogSize.Default stays 400dp.",
                                size = DialogSize.Default,
                            ) {
                                DialogFooter {
                                    Button("Close", onClick = { open = false })
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
                        Dialog(open = open, onOpenChange = { open = it }, modal = false) {
                            DialogTrigger("Open dialog")
                            DialogContent("Non-modal", description = "The page behind stays interactive.") {
                                DialogFooter {
                                    Button("Close", onClick = { open = false })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Dialog(open = open, onOpenChange = { open = it }, modal = false) {
                            DialogTrigger("Open dialog")
                            DialogContent("Non-modal", description = "The page behind stays interactive.") {
                                DialogFooter {
                                    Button("Close", onClick = { open = false })
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
                        Dialog(open = open, onOpenChange = { open = it }, modal = true) {
                            DialogTrigger("Open dialog")
                            DialogContent("Modal", description = "The page behind is inert.") {
                                DialogFooter {
                                    Button("Close", onClick = { open = false })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Dialog(open = open, onOpenChange = { open = it }, modal = true) {
                            DialogTrigger("Open dialog")
                            DialogContent("Modal", description = "The page behind is inert.") {
                                DialogFooter {
                                    Button("Close", onClick = { open = false })
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
                        Dialog(open = open, onOpenChange = { open = it }, modal = true) {
                            DialogTrigger("Open dialog")
                            DialogContent("Modal", description = "The page behind is inert.") {
                                DialogFooter {
                                    Button("Close", onClick = { open = false })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Dialog(open = open, onOpenChange = { open = it }, modal = true) {
                            DialogTrigger("Open dialog")
                            DialogContent("Modal", description = "The page behind is inert.") {
                                DialogFooter {
                                    Button("Close", onClick = { open = false })
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Non-dismissible",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Open dialog")
                            DialogContent(
                                "Notice",
                                description = "Clicking outside will not close this dialog.",
                                dismissOnClickOutside = false,
                            ) {
                                DialogFooter {
                                    Button("Close", onClick = { open = false })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Open dialog")
                            DialogContent(
                                "Notice",
                                description = "Clicking outside will not close this dialog.",
                                dismissOnClickOutside = false,
                            ) {
                                DialogFooter {
                                    Button("Close", onClick = { open = false })
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
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Open dialog")
                            DialogContent(
                                "Notice",
                                description = "Clicking outside closes this dialog.",
                                dismissOnClickOutside = true,
                            ) {
                                DialogFooter {
                                    Button("Close", onClick = { open = false })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Open dialog")
                            DialogContent(
                                "Notice",
                                description = "Clicking outside closes this dialog.",
                                dismissOnClickOutside = true,
                            ) {
                                DialogFooter {
                                    Button("Close", onClick = { open = false })
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled close",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Open dialog")
                            DialogContent(
                                "Notice",
                                description = "The close control is disabled.",
                                showCloseButton = false,
                            ) {
                                DialogClose(disabled = true)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Open dialog")
                            DialogContent(
                                "Notice",
                                description = "The close control is disabled.",
                                showCloseButton = false,
                            ) {
                                DialogClose(disabled = true)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        Dialog(defaultOpen = false) {
                            DialogTrigger("Open dialog", disabled = false)
                            DialogContent("Notice", description = "Starts closed.") {}
                        }
                        """.trimIndent(),
                    preview = {
                        Dialog(defaultOpen = false) {
                            DialogTrigger("Open dialog", disabled = false)
                            DialogContent("Notice", description = "Starts closed.") {}
                        }
                    },
                ),
                ComponentExample(
                    title = "Prevent pointer dismiss",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Open dialog")
                            DialogContent(
                                "Notice",
                                description = "Clicking the overlay will not close this dialog.",
                                onPointerDownOutside = { true },
                            ) {
                                DialogFooter {
                                    Button("Close", onClick = { open = false })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Open dialog")
                            DialogContent(
                                "Notice",
                                description = "Clicking the overlay will not close this dialog.",
                                onPointerDownOutside = { true },
                            ) {
                                DialogFooter {
                                    Button("Close", onClick = { open = false })
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
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Open dialog")
                            DialogContent(
                                "Notice",
                                description = "Interacting outside will not close this dialog.",
                                onInteractOutside = { true },
                            ) {
                                DialogFooter {
                                    Button("Close", onClick = { open = false })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Open dialog")
                            DialogContent(
                                "Notice",
                                description = "Interacting outside will not close this dialog.",
                                onInteractOutside = { true },
                            ) {
                                DialogFooter {
                                    Button("Close", onClick = { open = false })
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Accessible close label",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Open dialog")
                            DialogContent {
                                DialogClose(ariaLabel = "Dismiss dialog")
                                DialogHeader("Notice", description = "The close button has a custom accessible name.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Open dialog")
                            DialogContent {
                                DialogClose(ariaLabel = "Dismiss dialog")
                                DialogHeader("Notice", description = "The close button has a custom accessible name.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Content close label",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Open dialog")
                            DialogContent(
                                "Notice",
                                description = "The built-in close button has a custom accessible name.",
                                closeAriaLabel = "Dismiss dialog",
                            ) {
                                DialogFooter {
                                    Button("Close", onClick = { open = false })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Open dialog")
                            DialogContent(
                                "Notice",
                                description = "The built-in close button has a custom accessible name.",
                                closeAriaLabel = "Dismiss dialog",
                            ) {
                                DialogFooter {
                                    Button("Close", onClick = { open = false })
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
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Open dialog")
                            DialogContent(
                                "Notice",
                                description = "The built-in close button uses the default accessible name.",
                                closeAriaLabel = "Close",
                            ) {
                                DialogFooter {
                                    Button("Close", onClick = { open = false })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Open dialog")
                            DialogContent(
                                "Notice",
                                description = "The built-in close button uses the default accessible name.",
                                closeAriaLabel = "Close",
                            ) {
                                DialogFooter {
                                    Button("Close", onClick = { open = false })
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Uncontrolled",
                    code =
                        """
                        Dialog {
                            DialogTrigger("Open dialog")
                            DialogContent(
                                "Notice",
                                description = "Omit open and onOpenChange. Optional defaultOpen starts closed.",
                            ) {}
                        }
                        """.trimIndent(),
                    preview = {
                        Dialog {
                            DialogTrigger("Open dialog")
                            DialogContent(
                                "Notice",
                                description = "Omit open and onOpenChange. Optional defaultOpen starts closed.",
                            ) {}
                        }
                    },
                ),
                ComponentExample(
                    title = "Default open",
                    code =
                        """
                        Dialog(defaultOpen = false) {
                            DialogTrigger("Open dialog")
                            DialogContent("Notice", description = "Starts closed.") {}
                        }
                        """.trimIndent(),
                    preview = {
                        Dialog(defaultOpen = false) {
                            DialogTrigger("Open dialog")
                            DialogContent("Notice", description = "Starts closed.") {}
                        }
                    },
                ),
                ComponentExample(
                    title = "Title and description",
                    code =
                        """
                        Dialog {
                            DialogTrigger("Open dialog")
                            DialogContent {
                                DialogHeader {
                                    DialogTitle("Notice")
                                    DialogDescription("Compose Title and Description inside Header.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Dialog {
                            DialogTrigger("Open dialog")
                            DialogContent {
                                DialogHeader {
                                    DialogTitle("Notice")
                                    DialogDescription("Compose Title and Description inside Header.")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Footer",
                    code =
                        """
                        Dialog {
                            DialogTrigger("Open dialog")
                            DialogContent {
                                DialogHeader {
                                    DialogTitle("Notice")
                                    DialogDescription("Footer holds actions, matching shadcn DialogFooter.")
                                }
                                DialogFooter {
                                    Button("Close", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Dialog {
                            DialogTrigger("Open dialog")
                            DialogContent {
                                DialogHeader {
                                    DialogTitle("Notice")
                                    DialogDescription("Footer holds actions, matching shadcn DialogFooter.")
                                }
                                DialogFooter {
                                    Button("Close", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Footer close button",
                    code =
                        """
                        DialogFooter(showCloseButton = true) {
                            Button("Save")
                        }
                        """.trimIndent(),
                    preview = {
                        Dialog {
                            DialogTrigger("Open dialog")
                            DialogContent {
                                DialogHeader {
                                    DialogTitle("Notice")
                                    DialogDescription("Footer can render an official Close button.")
                                }
                                DialogFooter(showCloseButton = true) {
                                    Button("Save")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Header",
                    code =
                        """
                        Dialog {
                            DialogTrigger("Open dialog")
                            DialogContent {
                                DialogHeader {
                                    DialogTitle("Notice")
                                    DialogDescription("Compose Title and Description inside Header.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Dialog {
                            DialogTrigger("Open dialog")
                            DialogContent {
                                DialogHeader {
                                    DialogTitle("Notice")
                                    DialogDescription("Compose Title and Description inside Header.")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Trigger",
                    code =
                        """
                        Dialog {
                            DialogTrigger("Open dialog")
                            DialogContent {
                                DialogHeader {
                                    DialogTitle("Notice")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Dialog {
                            DialogTrigger("Open dialog")
                            DialogContent {
                                DialogHeader {
                                    DialogTitle("Notice")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Content",
                    code =
                        """
                        Dialog {
                            DialogTrigger("Open dialog")
                            DialogContent {
                                DialogHeader {
                                    DialogTitle("Notice")
                                    DialogDescription("DialogContent hosts Header, Title, and Description.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Dialog {
                            DialogTrigger("Open dialog")
                            DialogContent {
                                DialogHeader {
                                    DialogTitle("Notice")
                                    DialogDescription("DialogContent hosts Header, Title, and Description.")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Title",
                    code =
                        """
                        Dialog {
                            DialogTrigger("Open dialog")
                            DialogContent {
                                DialogHeader {
                                    DialogTitle("Notice")
                                    DialogDescription("Compose Title and Description inside Header.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Dialog {
                            DialogTrigger("Open dialog")
                            DialogContent {
                                DialogHeader {
                                    DialogTitle("Notice")
                                    DialogDescription("Compose Title and Description inside Header.")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Close",
                    code =
                        """
                        Dialog {
                            DialogTrigger("Open dialog")
                            DialogContent(showCloseButton = false) {
                                DialogClose()
                                DialogHeader {
                                    DialogTitle("Notice")
                                    DialogDescription("DialogClose matches shadcn's close slot.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Dialog {
                            DialogTrigger("Open dialog")
                            DialogContent(showCloseButton = false) {
                                DialogClose()
                                DialogHeader {
                                    DialogTitle("Notice")
                                    DialogDescription("DialogClose matches shadcn's close slot.")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Description",
                    code =
                        """
                        Dialog {
                            DialogTrigger("Open dialog")
                            DialogContent {
                                DialogHeader {
                                    DialogTitle("Notice")
                                    DialogDescription("DialogDescription matches shadcn's description slot.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Dialog {
                            DialogTrigger("Open dialog")
                            DialogContent {
                                DialogHeader {
                                    DialogTitle("Notice")
                                    DialogDescription("DialogDescription matches shadcn's description slot.")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Overlay",
                    code =
                        """
                        Dialog {
                            DialogTrigger("Open dialog")
                            DialogContent {
                                DialogHeader {
                                    DialogTitle("Notice")
                                    DialogDescription("DialogOverlay is the scrim composed by DialogContent.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Dialog {
                            DialogTrigger("Open dialog")
                            DialogContent {
                                DialogHeader {
                                    DialogTitle("Notice")
                                    DialogDescription("DialogOverlay is the scrim composed by DialogContent.")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Portal",
                    code =
                        """
                        Dialog {
                            DialogTrigger("Open dialog")
                            DialogContent {
                                DialogHeader {
                                    DialogTitle("Notice")
                                    DialogDescription("DialogPortal is the teleport host composed by DialogContent.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Dialog {
                            DialogTrigger("Open dialog")
                            DialogContent {
                                DialogHeader {
                                    DialogTitle("Notice")
                                    DialogDescription("DialogPortal is the teleport host composed by DialogContent.")
                                }
                            }
                        }
                    },
                ),
            ),
    )
