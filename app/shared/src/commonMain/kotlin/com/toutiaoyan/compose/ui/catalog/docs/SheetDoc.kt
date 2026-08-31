@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.field.FieldGroup
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.scrollarea.ScrollArea
import com.toutiaoyan.compose.ui.components.sheet.Sheet
import com.toutiaoyan.compose.ui.components.sheet.SheetClose
import com.toutiaoyan.compose.ui.components.sheet.SheetContent
import com.toutiaoyan.compose.ui.components.sheet.SheetDescription
import com.toutiaoyan.compose.ui.components.sheet.SheetFooter
import com.toutiaoyan.compose.ui.components.sheet.SheetHeader
import com.toutiaoyan.compose.ui.components.sheet.SheetSide
import com.toutiaoyan.compose.ui.components.sheet.SheetTitle
import com.toutiaoyan.compose.ui.components.sheet.SheetTrigger
import com.toutiaoyan.compose.ui.components.text.Text

val sheetDoc =
    ComponentDoc(
        id = "sheet",
        title = "Sheet",
        description = "A modal panel that slides in from a screen edge.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.sheet.Sheet
            import com.toutiaoyan.compose.ui.components.sheet.SheetContent
            import com.toutiaoyan.compose.ui.components.sheet.SheetTrigger

            var open by remember { mutableStateOf(false) }
            Sheet(open = open, onOpenChange = { open = it }) {
                SheetTrigger("Open")
                SheetContent {
                    SheetClose()
                    SheetHeader("Edit profile", description = "Make changes to your profile here.")
                }
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Edit profile",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var name by remember { mutableStateOf("Pedro Duarte") }
                        var username by remember { mutableStateOf("@peduarte") }
                        Sheet(open = open, onOpenChange = { open = it }) {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetClose()
                                SheetHeader(
                                    "Edit profile",
                                    description = "Make changes to your profile here. Click save when you're done.",
                                )
                                FieldGroup(modifier = Modifier.padding(top = 16.dp)) {
                                    Field("Name") {
                                        Input(value = name, onValueChange = { name = it })
                                    }
                                    Field("Username") {
                                        Input(value = username, onValueChange = { username = it })
                                    }
                                }
                                SheetFooter(modifier = Modifier.padding(top = 16.dp)) {
                                    Button("Save changes", onClick = { open = false })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var name by remember { mutableStateOf("Pedro Duarte") }
                        var username by remember { mutableStateOf("@peduarte") }
                        Sheet(open = open, onOpenChange = { open = it }) {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetClose()
                                SheetHeader(
                                    "Edit profile",
                                    description = "Make changes to your profile here. Click save when you're done.",
                                )
                                FieldGroup(modifier = Modifier.padding(top = 16.dp)) {
                                    Field("Name") {
                                        Input(value = name, onValueChange = { name = it })
                                    }
                                    Field("Username") {
                                        Input(value = username, onValueChange = { username = it })
                                    }
                                }
                                SheetFooter(modifier = Modifier.padding(top = 16.dp)) {
                                    Button("Save changes", onClick = { open = false })
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Side",
                    code =
                        """
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            for ((side, label) in listOf(
                                SheetSide.Top to "top",
                                SheetSide.End to "right",
                                SheetSide.Bottom to "bottom",
                                SheetSide.Start to "left",
                            )) {
                                Sheet {
                                    SheetTrigger(label)
                                    SheetContent(side = side) {
                                        SheetClose()
                                        SheetHeader(
                                            "Edit profile",
                                            description = "Make changes to your profile here.",
                                        )
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            for ((side, label) in listOf(
                                SheetSide.Top to "top",
                                SheetSide.End to "right",
                                SheetSide.Bottom to "bottom",
                                SheetSide.Start to "left",
                            )) {
                                Sheet {
                                    SheetTrigger(label)
                                    SheetContent(side = side) {
                                        SheetClose()
                                        SheetHeader(
                                            "Edit profile",
                                            description = "Make changes to your profile here.",
                                        )
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default side",
                    code =
                        """
                        Sheet {
                            SheetTrigger("Open")
                            SheetContent(side = SheetSide.End) {
                                SheetClose()
                                SheetHeader(
                                    "Edit profile",
                                    description = "Make changes to your profile here.",
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Sheet {
                            SheetTrigger("Open")
                            SheetContent(side = SheetSide.End) {
                                SheetClose()
                                SheetHeader(
                                    "Edit profile",
                                    description = "Make changes to your profile here.",
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Scrollable content",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Sheet(open = open, onOpenChange = { open = it }) {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetClose()
                                SheetHeader("Notice", description = "A list that scrolls under the header.")
                                ScrollArea(modifier = Modifier.height(160.dp).padding(horizontal = 16.dp)) {
                                    Column {
                                        repeat(12) { index ->
                                            Text(modifier = Modifier.padding(vertical = 6.dp)) { "Item ${'$'}index" }
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Sheet(open = open, onOpenChange = { open = it }) {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetClose()
                                SheetHeader("Notice", description = "A list that scrolls under the header.")
                                ScrollArea(modifier = Modifier.height(160.dp).padding(horizontal = 16.dp)) {
                                    Column {
                                        repeat(12) { index ->
                                            Text(modifier = Modifier.padding(vertical = 6.dp)) { "Item $index" }
                                        }
                                    }
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
                        Sheet(open = open, onOpenChange = { open = it }, modal = false) {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetClose()
                                SheetHeader("Notice", description = "The page behind stays interactive.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Sheet(open = open, onOpenChange = { open = it }, modal = false) {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetClose()
                                SheetHeader("Notice", description = "The page behind stays interactive.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Modal",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Sheet(open = open, onOpenChange = { open = it }, modal = true) {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetClose()
                                SheetHeader("Notice", description = "The page behind is inert.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Sheet(open = open, onOpenChange = { open = it }, modal = true) {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetClose()
                                SheetHeader("Notice", description = "The page behind is inert.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default modal",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Sheet(open = open, onOpenChange = { open = it }, modal = true) {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetClose()
                                SheetHeader("Notice", description = "The page behind is inert.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Sheet(open = open, onOpenChange = { open = it }, modal = true) {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetClose()
                                SheetHeader("Notice", description = "The page behind is inert.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Non-dismissible",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Sheet(open = open, onOpenChange = { open = it }) {
                            SheetTrigger("Open")
                            SheetContent(dismissOnClickOutside = false) {
                                SheetClose()
                                SheetHeader("Notice", description = "Clicking outside will not close this sheet.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Sheet(open = open, onOpenChange = { open = it }) {
                            SheetTrigger("Open")
                            SheetContent(dismissOnClickOutside = false) {
                                SheetClose()
                                SheetHeader("Notice", description = "Clicking outside will not close this sheet.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default dismiss",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Sheet(open = open, onOpenChange = { open = it }) {
                            SheetTrigger("Open")
                            SheetContent(dismissOnClickOutside = true) {
                                SheetHeader("Notice", description = "Clicking outside will close this sheet.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Sheet(open = open, onOpenChange = { open = it }) {
                            SheetTrigger("Open")
                            SheetContent(dismissOnClickOutside = true) {
                                SheetHeader("Notice", description = "Clicking outside will close this sheet.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Close button",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Sheet(open = open, onOpenChange = { open = it }) {
                            SheetTrigger("Open")
                            SheetContent(showCloseButton = true) {
                                SheetHeader("Notice", description = "The panel includes a close button.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Sheet(open = open, onOpenChange = { open = it }) {
                            SheetTrigger("Open")
                            SheetContent(showCloseButton = true) {
                                SheetHeader("Notice", description = "The panel includes a close button.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default close button",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Sheet(open = open, onOpenChange = { open = it }) {
                            SheetTrigger("Open")
                            SheetContent(showCloseButton = false) {
                                SheetHeader("Notice", description = "The panel has no close button by default.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Sheet(open = open, onOpenChange = { open = it }) {
                            SheetTrigger("Open")
                            SheetContent(showCloseButton = false) {
                                SheetHeader("Notice", description = "The panel has no close button by default.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled close",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Sheet(open = open, onOpenChange = { open = it }) {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetClose(disabled = true)
                                SheetHeader("Notice", description = "The close control is disabled.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Sheet(open = open, onOpenChange = { open = it }) {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetClose(disabled = true)
                                SheetHeader("Notice", description = "The close control is disabled.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        Sheet(defaultOpen = false) {
                            SheetTrigger("Open", disabled = false)
                            SheetContent {
                                SheetHeader("Notice", description = "Starts closed.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Sheet(defaultOpen = false) {
                            SheetTrigger("Open", disabled = false)
                            SheetContent {
                                SheetHeader("Notice", description = "Starts closed.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Prevent pointer dismiss",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Sheet(open = open, onOpenChange = { open = it }) {
                            SheetTrigger("Open")
                            SheetContent(
                                onPointerDownOutside = { true },
                            ) {
                                SheetClose()
                                SheetHeader("Notice", description = "Clicking the overlay will not close this sheet.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Sheet(open = open, onOpenChange = { open = it }) {
                            SheetTrigger("Open")
                            SheetContent(
                                onPointerDownOutside = { true },
                            ) {
                                SheetClose()
                                SheetHeader("Notice", description = "Clicking the overlay will not close this sheet.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Prevent interact outside",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Sheet(open = open, onOpenChange = { open = it }) {
                            SheetTrigger("Open")
                            SheetContent(
                                onInteractOutside = { true },
                            ) {
                                SheetClose()
                                SheetHeader("Notice", description = "Interacting outside will not close this sheet.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Sheet(open = open, onOpenChange = { open = it }) {
                            SheetTrigger("Open")
                            SheetContent(
                                onInteractOutside = { true },
                            ) {
                                SheetClose()
                                SheetHeader("Notice", description = "Interacting outside will not close this sheet.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Accessible close label",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Sheet(open = open, onOpenChange = { open = it }) {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetClose(ariaLabel = "Dismiss sheet")
                                SheetHeader("Notice", description = "The close button has a custom accessible name.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Sheet(open = open, onOpenChange = { open = it }) {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetClose(ariaLabel = "Dismiss sheet")
                                SheetHeader("Notice", description = "The close button has a custom accessible name.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Content close label",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Sheet(open = open, onOpenChange = { open = it }) {
                            SheetTrigger("Open")
                            SheetContent(
                                showCloseButton = true,
                                closeAriaLabel = "Dismiss sheet",
                            ) {
                                SheetHeader("Notice", description = "The built-in close button has a custom accessible name.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Sheet(open = open, onOpenChange = { open = it }) {
                            SheetTrigger("Open")
                            SheetContent(
                                showCloseButton = true,
                                closeAriaLabel = "Dismiss sheet",
                            ) {
                                SheetHeader("Notice", description = "The built-in close button has a custom accessible name.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default close label",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Sheet(open = open, onOpenChange = { open = it }) {
                            SheetTrigger("Open")
                            SheetContent(
                                showCloseButton = true,
                                closeAriaLabel = "Close",
                            ) {
                                SheetHeader("Notice", description = "The built-in close button uses the default accessible name.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Sheet(open = open, onOpenChange = { open = it }) {
                            SheetTrigger("Open")
                            SheetContent(
                                showCloseButton = true,
                                closeAriaLabel = "Close",
                            ) {
                                SheetHeader("Notice", description = "The built-in close button uses the default accessible name.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Uncontrolled",
                    code =
                        """
                        Sheet {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetClose()
                                SheetHeader(
                                    "Notice",
                                    description = "Omit open and onOpenChange. Optional defaultOpen starts closed.",
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Sheet {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetClose()
                                SheetHeader(
                                    "Notice",
                                    description = "Omit open and onOpenChange. Optional defaultOpen starts closed.",
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default open",
                    code =
                        """
                        Sheet(defaultOpen = false) {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetClose()
                                SheetHeader("Notice", description = "Starts closed.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Sheet(defaultOpen = false) {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetClose()
                                SheetHeader("Notice", description = "Starts closed.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Title and description",
                    code =
                        """
                        Sheet {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetClose()
                                SheetHeader {
                                    SheetTitle("Notice")
                                    SheetDescription("Compose Title and Description inside Header.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Sheet {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetClose()
                                SheetHeader {
                                    SheetTitle("Notice")
                                    SheetDescription("Compose Title and Description inside Header.")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Footer",
                    code =
                        """
                        Sheet {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetClose()
                                SheetHeader {
                                    SheetTitle("Notice")
                                    SheetDescription("Footer holds actions, matching shadcn SheetFooter.")
                                }
                                SheetFooter {
                                    Button("Close", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Sheet {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetClose()
                                SheetHeader {
                                    SheetTitle("Notice")
                                    SheetDescription("Footer holds actions, matching shadcn SheetFooter.")
                                }
                                SheetFooter {
                                    Button("Close", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Header",
                    code =
                        """
                        Sheet {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetClose()
                                SheetHeader {
                                    SheetTitle("Notice")
                                    SheetDescription("Compose Title and Description inside Header.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Sheet {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetClose()
                                SheetHeader {
                                    SheetTitle("Notice")
                                    SheetDescription("Compose Title and Description inside Header.")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Trigger",
                    code =
                        """
                        Sheet {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetClose()
                                SheetHeader {
                                    SheetTitle("Notice")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Sheet {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetClose()
                                SheetHeader {
                                    SheetTitle("Notice")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Content",
                    code =
                        """
                        Sheet {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetClose()
                                SheetHeader {
                                    SheetTitle("Notice")
                                    SheetDescription("SheetContent hosts Header, Title, and Description.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Sheet {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetClose()
                                SheetHeader {
                                    SheetTitle("Notice")
                                    SheetDescription("SheetContent hosts Header, Title, and Description.")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Close",
                    code =
                        """
                        Sheet {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetClose()
                                SheetHeader {
                                    SheetTitle("Notice")
                                    SheetDescription("SheetClose matches shadcn's close slot.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Sheet {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetClose()
                                SheetHeader {
                                    SheetTitle("Notice")
                                    SheetDescription("SheetClose matches shadcn's close slot.")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Description",
                    code =
                        """
                        Sheet {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetHeader {
                                    SheetTitle("Notice")
                                    SheetDescription("SheetDescription matches shadcn's description slot.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Sheet {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetHeader {
                                    SheetTitle("Notice")
                                    SheetDescription("SheetDescription matches shadcn's description slot.")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Title",
                    code =
                        """
                        Sheet {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetHeader {
                                    SheetTitle("Notice")
                                    SheetDescription("Compose Title and Description inside Header.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Sheet {
                            SheetTrigger("Open")
                            SheetContent {
                                SheetHeader {
                                    SheetTitle("Notice")
                                    SheetDescription("Compose Title and Description inside Header.")
                                }
                            }
                        }
                    },
                ),
            ),
    )
