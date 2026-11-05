@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.X
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
import com.toutiaoyan.compose.ui.catalog.catalogArabicFontFamily
import com.toutiaoyan.compose.ui.catalog.catalogHebrewFontFamily
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.dialog.Dialog
import com.toutiaoyan.compose.ui.components.dialog.DialogClose
import com.toutiaoyan.compose.ui.components.dialog.DialogContent
import com.toutiaoyan.compose.ui.components.dialog.DialogDescription
import com.toutiaoyan.compose.ui.components.dialog.DialogFooter
import com.toutiaoyan.compose.ui.components.dialog.DialogHeader
import com.toutiaoyan.compose.ui.components.dialog.DialogSize
import com.toutiaoyan.compose.ui.components.dialog.DialogTitle
import com.toutiaoyan.compose.ui.components.dialog.DialogTrigger
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.field.FieldGroup
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.scrollarea.ScrollArea
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme

private val dialogExtensionExamples =
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
            )

/** Official recipes promoted into [dialogOfficialExamples]; not repeated as extensions. */
private val dialogPromotedExamples =
    setOf("Edit profile", "Without close button", "Scrollable content", "Sticky footer")

private val dialogOfficialExamples =
    listOf(
        ComponentExample(
            title = "Edit profile",
            code =
                """
                var open by remember { mutableStateOf(false) }
                var name by remember { mutableStateOf("Pedro Duarte") }
                var username by remember { mutableStateOf("@peduarte") }
                Dialog(open = open, onOpenChange = { open = it }) {
                    DialogTrigger("Open Dialog", variant = ButtonVariant.Outline)
                    DialogContent(
                        "Edit profile",
                        description = "Make changes to your profile here. Click save when you're done.",
                    ) {
                        FieldGroup {
                            Field("Name") { Input(value = name, onValueChange = { name = it }) }
                            Field("Username") { Input(value = username, onValueChange = { username = it }) }
                        }
                        DialogFooter {
                            Button("Cancel", onClick = { open = false }, variant = ButtonVariant.Outline)
                            Button("Save changes", onClick = { open = false })
                        }
                    }
                }
                """.trimIndent(),
            preview = { DialogEditProfileRecipe() },
        ),
        ComponentExample(
            title = "Custom Close Button",
            code =
                """
                var open by remember { mutableStateOf(false) }
                var link by remember { mutableStateOf("https://ui.shadcn.com/docs/installation") }
                Dialog(open = open, onOpenChange = { open = it }) {
                    DialogTrigger("Share", variant = ButtonVariant.Outline)
                    // The official example uses sm:max-w-md (448dp).
                    DialogContent(style = Style { width(448.dp) }) {
                        DialogHeader(
                            "Share link",
                            description = "Anyone who has this link will be able to view this.",
                        )
                        Input(value = link, onValueChange = { link = it }, readOnly = true)
                        DialogFooter { Button("Close", onClick = { open = false }) }
                    }
                }
                """.trimIndent(),
            preview = { DialogCustomCloseButtonRecipe() },
        ),
        ComponentExample(
            title = "No Close Button",
            code =
                """
                var open by remember { mutableStateOf(false) }
                Dialog(open = open, onOpenChange = { open = it }) {
                    DialogTrigger("No Close Button", variant = ButtonVariant.Outline)
                    DialogContent(
                        "No Close Button",
                        description = "This dialog doesn't have a close button in the top-right corner.",
                        showCloseButton = false,
                    ) { }
                }
                """.trimIndent(),
            preview = { DialogNoCloseButtonRecipe() },
        ),
        ComponentExample(
            title = "Sticky Footer",
            code =
                """
                var open by remember { mutableStateOf(false) }
                Dialog(open = open, onOpenChange = { open = it }) {
                    DialogTrigger("Sticky Footer", variant = ButtonVariant.Outline)
                    DialogContent(
                        "Sticky Footer",
                        description =
                            "This dialog has a sticky footer that stays visible while the content scrolls.",
                    ) {
                        Column(
                            Modifier.heightIn(max = 240.dp).clipToBounds()
                                .verticalScroll(rememberScrollState()),
                        ) {
                            repeat(10) {
                                Text(Modifier.padding(bottom = 16.dp)) { loremIpsum }
                            }
                        }
                        DialogFooter {
                            Button("Close", onClick = { open = false }, variant = ButtonVariant.Outline)
                        }
                    }
                }
                """.trimIndent(),
            minPreviewHeight = 288.dp,
            preview = { DialogStickyFooterRecipe() },
        ),
        ComponentExample(
            title = "Scrollable Content",
            code =
                """
                var open by remember { mutableStateOf(false) }
                Dialog(open = open, onOpenChange = { open = it }) {
                    DialogTrigger("Scrollable Content", variant = ButtonVariant.Outline)
                    DialogContent(
                        "Scrollable Content",
                        description = "This is a dialog with scrollable content.",
                    ) {
                        Column(
                            Modifier.heightIn(max = 240.dp).clipToBounds()
                                .verticalScroll(rememberScrollState()),
                        ) {
                            repeat(10) {
                                Text(Modifier.padding(bottom = 16.dp)) { loremIpsum }
                            }
                        }
                    }
                }
                """.trimIndent(),
            preview = { DialogScrollableContentRecipe() },
        ),
        ComponentExample(
            title = "RTL",
            descriptionMarkdown =
                (
                    "To enable RTL support in your app, see the [RTL configuration " +
                        "guide](https://ui.shadcn.com/docs/rtl). Source shows the Arabic pair; " +
                        "the catalog toolbar also previews English and Hebrew."
                ),
            previewPadding = 0.dp,
            minPreviewHeight = 288.dp,
            code =
                """
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Theme(fontFamily = catalogArabicFontFamily()) {
                        var open by remember { mutableStateOf(false) }
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("فتح الحوار", variant = ButtonVariant.Outline)
                            DialogContent(
                                "تعديل الملف الشخصي",
                                description =
                                    "قم بإجراء تغييرات على ملفك الشخصي هنا. انقر فوق حفظ عند الانتهاء.",
                            ) {
                                FieldGroup {
                                    Field("الاسم") { Input(value = "Pedro Duarte", onValueChange = {}) }
                                    Field("اسم المستخدم") { Input(value = "@peduarte", onValueChange = {}) }
                                }
                                DialogFooter {
                                    Button("إلغاء", onClick = { open = false }, variant = ButtonVariant.Outline)
                                    Button("حفظ التغييرات", onClick = { open = false })
                                }
                            }
                        }
                    }
                }
                """.trimIndent(),
            preview = { DialogRtlRecipe() },
        ),
    )

private const val loremIpsum =
    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor " +
        "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud " +
        "exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure " +
        "dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur."

@Composable
private fun DialogEditProfileRecipe() {
    var open by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("Pedro Duarte") }
    var username by remember { mutableStateOf("@peduarte") }
    Dialog(open = open, onOpenChange = { open = it }) {
        DialogTrigger("Open Dialog", variant = ButtonVariant.Outline)
        DialogContent(
            "Edit profile",
            description = "Make changes to your profile here. Click save when you're done.",
        ) {
            FieldGroup {
                Field("Name") { Input(value = name, onValueChange = { name = it }) }
                Field("Username") { Input(value = username, onValueChange = { username = it }) }
            }
            DialogFooter {
                Button("Cancel", onClick = { open = false }, variant = ButtonVariant.Outline)
                Button("Save changes", onClick = { open = false })
            }
        }
    }
}

@Composable
private fun DialogCustomCloseButtonRecipe() {
    var open by remember { mutableStateOf(false) }
    var link by remember { mutableStateOf("https://ui.shadcn.com/docs/installation") }
    Dialog(open = open, onOpenChange = { open = it }) {
        DialogTrigger("Share", variant = ButtonVariant.Outline)
        DialogContent(style = Style { width(448.dp) }) {
            DialogHeader("Share link", description = "Anyone who has this link will be able to view this.")
            Input(value = link, onValueChange = { link = it }, readOnly = true)
            DialogFooter { Button("Close", onClick = { open = false }) }
        }
    }
}

@Composable
private fun DialogNoCloseButtonRecipe() {
    var open by remember { mutableStateOf(false) }
    Dialog(open = open, onOpenChange = { open = it }) {
        DialogTrigger("No Close Button", variant = ButtonVariant.Outline)
        DialogContent(
            "No Close Button",
            description = "This dialog doesn't have a close button in the top-right corner.",
            showCloseButton = false,
        ) { }
    }
}

@Composable
private fun DialogStickyFooterRecipe() {
    var open by remember { mutableStateOf(false) }
    Dialog(open = open, onOpenChange = { open = it }) {
        DialogTrigger("Sticky Footer", variant = ButtonVariant.Outline)
        DialogContent(
            "Sticky Footer",
            description = "This dialog has a sticky footer that stays visible while the content scrolls.",
        ) {
            Column(Modifier.heightIn(max = 240.dp).clipToBounds().verticalScroll(rememberScrollState())) {
                repeat(10) { Text(Modifier.padding(bottom = 16.dp)) { loremIpsum } }
            }
            DialogFooter {
                Button("Close", onClick = { open = false }, variant = ButtonVariant.Outline)
            }
        }
    }
}

@Composable
private fun DialogScrollableContentRecipe() {
    var open by remember { mutableStateOf(false) }
    Dialog(open = open, onOpenChange = { open = it }) {
        DialogTrigger("Scrollable Content", variant = ButtonVariant.Outline)
        DialogContent("Scrollable Content", description = "This is a dialog with scrollable content.") {
            Column(Modifier.heightIn(max = 240.dp).clipToBounds().verticalScroll(rememberScrollState())) {
                repeat(10) { Text(Modifier.padding(bottom = 16.dp)) { loremIpsum } }
            }
        }
    }
}

private val dialogTranslations =
    listOf(
        listOf(
            "Open Dialog",
            "Edit profile",
            "Make changes to your profile here. Click save when you're done.",
            "Name",
            "Username",
            "Cancel",
            "Save changes",
        ),
        listOf(
            "فتح الحوار",
            "تعديل الملف الشخصي",
            "قم بإجراء تغييرات على ملفك الشخصي هنا. انقر فوق حفظ عند الانتهاء.",
            "الاسم",
            "اسم المستخدم",
            "إلغاء",
            "حفظ التغييرات",
        ),
        listOf(
            "פתח דיאלוג",
            "ערוך פרופיל",
            "בצע שינויים בפרופיל שלך כאן. לחץ על שמור כשתסיים.",
            "שם",
            "שם משתמש",
            "בטל",
            "שמור שינויים",
        ),
    )

@Composable
private fun DialogRtlRecipe() {
    var language by remember { mutableStateOf(1) }
    val inherited = currentTheme
    RtlPreview(language, listOf("English", "Arabic (العربية)", "Hebrew (עברית)"), { language = it }) {
        val font =
            when (language) {
                1 -> catalogArabicFontFamily()
                2 -> catalogHebrewFontFamily()
                else -> inherited.fontFamily
            }
        CompositionLocalProvider(
            LocalLayoutDirection provides if (language == 0) LayoutDirection.Ltr else LayoutDirection.Rtl,
        ) {
            Theme(
                preset = inherited.activePreset,
                baseColor = inherited.activeBaseColor,
                accent = inherited.activeAccent,
                isDark = inherited.isDark,
                baseRadius = inherited.baseRadius,
                ring = inherited.ring,
                unitSpacing = LocalUnitSpacing.current,
                minTouchTarget = inherited.minTouchTarget,
                fontFamily = font,
            ) {
                val t = dialogTranslations[language]
                var open by remember(language) { mutableStateOf(false) }
                Dialog(open = open, onOpenChange = { open = it }) {
                    DialogTrigger(t[0], variant = ButtonVariant.Outline)
                    DialogContent(t[1], description = t[2]) {
                        FieldGroup {
                            Field(t[3]) { Input(value = "Pedro Duarte", onValueChange = {}) }
                            Field(t[4]) { Input(value = "@peduarte", onValueChange = {}) }
                        }
                        DialogFooter {
                            Button(t[5], onClick = { open = false }, variant = ButtonVariant.Outline)
                            Button(t[6], onClick = { open = false })
                        }
                    }
                }
            }
        }
    }
}

private val dialogApiSections =
    listOf(
        ComponentApiSection(
            "Dialog",
            (
                "Controlled (`open` + `onOpenChange`) or uncontrolled (`defaultOpen`). " +
                    "Renders a window-level overlay; the catalog root wraps the app in " +
                    "`OverlayHost`. `modal = false` is a Compose extension that keeps the " +
                    "page interactive (Base UI's `modal` additionally accepts \"trap-focus\")."
            ),
            listOf(
                ApiProperty("open", "Boolean?", "null"),
                ApiProperty("onOpenChange", "((Boolean) -> Unit)?", "null"),
                ApiProperty("defaultOpen", "Boolean", "false"),
                ApiProperty("modal", "Boolean", "true"),
                ApiProperty("content", "@Composable DialogScope.() -> Unit", "Required"),
            ),
        ),
        ComponentApiSection(
            "DialogTrigger",
            (
                "Toggles the dialog. The string overload renders a `Button`; the slot " +
                    "overload leaves chrome to the caller, matching `DialogTrigger`'s " +
                    "unstyled primitive."
            ),
            listOf(
                ApiProperty("text", "String", "Required (string overload)"),
                ApiProperty("variant", "ButtonVariant", "ButtonVariant.Default"),
                ApiProperty("size", "ButtonSize", "ButtonSize.Md"),
                ApiProperty("enabled / disabled", "Boolean", "true / false"),
                ApiProperty("content", "@Composable () -> Unit", "Required (slot overload)"),
            ),
        ),
        ComponentApiSection(
            "DialogContent",
            (
                "Portal + overlay + panel. Nova defaults to 384dp (`DialogSize.Sm` 320dp, " +
                    "`DialogSize.Lg` 512dp is a Compose extension), `p-4`, `rounded-xl`, " +
                    "`bg-popover` with a foreground/10 ring and a 4dp backdrop blur. " +
                    "`dismissOnClickOutside`, the outside-interaction veto callbacks and the " +
                    "`closeIcon` slot are Compose extensions of Base UI's props."
            ),
            listOf(
                ApiProperty("size", "DialogSize", "DialogSize.Default"),
                ApiProperty("showCloseButton", "Boolean", "true"),
                ApiProperty("closeAriaLabel", "String", "Close"),
                ApiProperty("dismissOnClickOutside", "Boolean", "true"),
                ApiProperty("onPointerDownOutside", "(() -> Boolean)?", "null"),
                ApiProperty("onInteractOutside", "(() -> Boolean)?", "null"),
                ApiProperty("closeIcon", "@Composable () -> Unit", "Theme X icon"),
                ApiProperty("enter / exit", "EnterTransition / ExitTransition", "fade + zoom-95"),
            ),
        ),
        ComponentApiSection(
            "DialogHeader / DialogTitle / DialogDescription",
            (
                "Header stacks title and description with Nova's 8dp gap. Title is a " +
                    "semantic heading at 16sp/medium; description is 14sp muted. The " +
                    "title + description overloads are Compose conveniences for the slots."
            ),
            listOf(
                ApiProperty("title / description", "String", "Required / null"),
                ApiProperty("content", "@Composable ColumnScope.() -> Unit", "Required (slot)"),
            ),
        ),
        ComponentApiSection(
            "DialogFooter",
            (
                "Right-aligned action row; Nova paints `bg-muted/50` with a top border and " +
                    "bottom `rounded-xl` exactly like `.cn-dialog-footer`. " +
                    "`showCloseButton` appends the official outline Close button."
            ),
            listOf(
                ApiProperty("showCloseButton", "Boolean", "false"),
                ApiProperty("closeAriaLabel", "String", "Close"),
                ApiProperty("content", "@Composable RowScope.() -> Unit", "Required"),
            ),
        ),
        ComponentApiSection(
            "DialogClose",
            (
                "Closes the nearest dialog. Renders the Nova X glyph by default; " +
                    "`icon`, `ariaLabel` and `disabled` are the documented slots."
            ),
            listOf(
                ApiProperty("ariaLabel", "String", "Close"),
                ApiProperty("disabled", "Boolean", "false"),
                ApiProperty("icon", "@Composable () -> Unit", "Theme X icon"),
            ),
        ),
        ComponentApiSection(
            "DialogOverlay / DialogPortal",
            (
                "Lower-level pieces [DialogContent] composes: the `bg-black/10` blurred " +
                    "overlay and the window-level teleport host. Use them directly only " +
                    "when replacing the default panel chrome."
            ),
            listOf(
                ApiProperty("dismissOnClickOutside", "Boolean", "true (Portal)"),
                ApiProperty("content", "@Composable () -> Unit", "Required"),
            ),
        ),
    )


val dialogDoc =
    ComponentDoc(
        id = "dialog",
        title = "Dialog",
        description = (
            "A window overlaid on either the primary window or another " +
                "dialog window, rendering the content underneath inert."
        ),
        usageImportsCode =
            """
            import com.toutiaoyan.compose.ui.components.dialog.Dialog
            import com.toutiaoyan.compose.ui.components.dialog.DialogClose
            import com.toutiaoyan.compose.ui.components.dialog.DialogContent
            import com.toutiaoyan.compose.ui.components.dialog.DialogDescription
            import com.toutiaoyan.compose.ui.components.dialog.DialogFooter
            import com.toutiaoyan.compose.ui.components.dialog.DialogHeader
            import com.toutiaoyan.compose.ui.components.dialog.DialogTitle
            import com.toutiaoyan.compose.ui.components.dialog.DialogTrigger
            """.trimIndent(),
        usageCode =
            """
            var open by remember { mutableStateOf(false) }
            Dialog(open = open, onOpenChange = { open = it }) {
                DialogTrigger("Open")
                DialogContent {
                    DialogHeader {
                        DialogTitle("Are you absolutely sure?")
                        DialogDescription(
                            "This action cannot be undone. This will permanently delete your " +
                                "account and remove your data from our servers.",
                        )
                    }
                }
            }
            """.trimIndent(),
        compositionCode =
            """
            Dialog
            ├── DialogTrigger
            └── DialogContent
                ├── DialogHeader
                │   ├── DialogTitle
                │   └── DialogDescription
                └── DialogFooter
            """.trimIndent(),
        examplesHeading = null,
        examples =
            dialogOfficialExamples +
                dialogExtensionExamples
                    .filterNot { it.title in dialogPromotedExamples }
                    .map { it.copy(title = "Kotlin extension: " + it.title) },
        apiSections = dialogApiSections,
    )
