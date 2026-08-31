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
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.drawer.Drawer
import com.toutiaoyan.compose.ui.components.drawer.DrawerClose
import com.toutiaoyan.compose.ui.components.drawer.DrawerCloseIcon
import com.toutiaoyan.compose.ui.components.drawer.DrawerContent
import com.toutiaoyan.compose.ui.components.drawer.DrawerDescription
import com.toutiaoyan.compose.ui.components.drawer.DrawerFooter
import com.toutiaoyan.compose.ui.components.drawer.DrawerHeader
import com.toutiaoyan.compose.ui.components.drawer.DrawerSnapPoint
import com.toutiaoyan.compose.ui.components.drawer.DrawerSwipeDirection
import com.toutiaoyan.compose.ui.components.drawer.DrawerTitle
import com.toutiaoyan.compose.ui.components.drawer.DrawerTrigger
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.field.FieldGroup
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.scrollarea.ScrollArea
import com.toutiaoyan.compose.ui.components.text.Text

val drawerDoc =
    ComponentDoc(
        id = "drawer",
        title = "Drawer",
        description =
            "Base UI / shadcn base-nova drawer (not Vaul/Radix): slides from an edge and " +
                "dismisses by swiping that way. Trigger/Close are children host slots " +
                "(wire Button onClick to toggle/close — Compose stand-in for Base render={Button}). " +
                "Distinct from Sheet.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.button.Button
            import com.toutiaoyan.compose.ui.components.button.ButtonVariant
            import com.toutiaoyan.compose.ui.components.drawer.Drawer
            import com.toutiaoyan.compose.ui.components.drawer.DrawerClose
            import com.toutiaoyan.compose.ui.components.drawer.DrawerContent
            import com.toutiaoyan.compose.ui.components.drawer.DrawerFooter
            import com.toutiaoyan.compose.ui.components.drawer.DrawerHeader
            import com.toutiaoyan.compose.ui.components.drawer.DrawerTrigger

            var open by remember { mutableStateOf(false) }
            Drawer(open = open, onOpenChange = { open = it }) {
                DrawerTrigger {
                    Button("Open drawer", onClick = toggle, variant = ButtonVariant.Outline)
                }
                DrawerContent {
                    DrawerHeader("Are you absolutely sure?", description = "This action cannot be undone.")
                    DrawerFooter {
                        Button("Submit", onClick = close)
                        DrawerClose {
                            Button("Cancel", onClick = close, variant = ButtonVariant.Outline)
                        }
                    }
                }
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Edit profile (bottom edge, drag to dismiss)",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var name by remember { mutableStateOf("Pedro Duarte") }
                        var username by remember { mutableStateOf("@peduarte") }
                        Drawer(open = open, onOpenChange = { open = it }) {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerCloseIcon()
                                DrawerHeader(
                                    "Edit profile",
                                    description = "Make changes to your profile here. Click save when you're done.",
                                )
                                FieldGroup(modifier = Modifier.padding(16.dp)) {
                                    Field("Name") {
                                        Input(value = name, onValueChange = { name = it })
                                    }
                                    Field("Username") {
                                        Input(value = username, onValueChange = { username = it })
                                    }
                                }
                                DrawerFooter {
                                    Button("Save changes", onClick = { open = false })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var name by remember { mutableStateOf("Pedro Duarte") }
                        var username by remember { mutableStateOf("@peduarte") }
                        Drawer(open = open, onOpenChange = { open = it }) {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerCloseIcon()
                                DrawerHeader(
                                    "Edit profile",
                                    description = "Make changes to your profile here. Click save when you're done.",
                                )
                                FieldGroup(modifier = Modifier.padding(16.dp)) {
                                    Field("Name") {
                                        Input(value = name, onValueChange = { name = it })
                                    }
                                    Field("Username") {
                                        Input(value = username, onValueChange = { username = it })
                                    }
                                }
                                DrawerFooter {
                                    Button("Save changes", onClick = { open = false })
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Swipe direction",
                    code =
                        """
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            for ((direction, label) in listOf(
                                DrawerSwipeDirection.Up to "up",
                                DrawerSwipeDirection.Right to "right",
                                DrawerSwipeDirection.Down to "down",
                                DrawerSwipeDirection.Left to "left",
                            )) {
                                Drawer(swipeDirection = direction) {
                                    DrawerTrigger(label)
                                    DrawerContent {
                                        DrawerHeader(
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
                            for ((direction, label) in listOf(
                                DrawerSwipeDirection.Up to "up",
                                DrawerSwipeDirection.Right to "right",
                                DrawerSwipeDirection.Down to "down",
                                DrawerSwipeDirection.Left to "left",
                            )) {
                                Drawer(swipeDirection = direction) {
                                    DrawerTrigger(label)
                                    DrawerContent {
                                        DrawerHeader(
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
                    title = "Default swipe direction (down)",
                    code =
                        """
                        Drawer(swipeDirection = DrawerSwipeDirection.Down) {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerHeader(
                                    "Edit profile",
                                    description = "Make changes to your profile here.",
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Drawer(swipeDirection = DrawerSwipeDirection.Down) {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerHeader(
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
                        Drawer(open = open, onOpenChange = { open = it }) {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerCloseIcon()
                                DrawerHeader("Notice", description = "A list that scrolls under the header.")
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
                        Drawer(open = open, onOpenChange = { open = it }) {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerCloseIcon()
                                DrawerHeader("Notice", description = "A list that scrolls under the header.")
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
                    title = "Non-dismissible",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Drawer(
                            open = open,
                            onOpenChange = { open = it },
                            dismissOnClickOutside = false,
                        ) {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerCloseIcon()
                                DrawerHeader(
                                    "Notice",
                                    description = "Clicking outside will not close this drawer.",
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Drawer(
                            open = open,
                            onOpenChange = { open = it },
                            dismissOnClickOutside = false,
                        ) {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerCloseIcon()
                                DrawerHeader(
                                    "Notice",
                                    description = "Clicking outside will not close this drawer.",
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default dismiss",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Drawer(
                            open = open,
                            onOpenChange = { open = it },
                            dismissOnClickOutside = true,
                        ) {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerHeader(
                                    "Notice",
                                    description = "Clicking outside will close this drawer.",
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Drawer(
                            open = open,
                            onOpenChange = { open = it },
                            dismissOnClickOutside = true,
                        ) {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerHeader(
                                    "Notice",
                                    description = "Clicking outside will close this drawer.",
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Non-modal",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Drawer(open = open, onOpenChange = { open = it }, modal = false) {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerCloseIcon()
                                DrawerHeader("Notice", description = "The page behind stays interactive.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Drawer(open = open, onOpenChange = { open = it }, modal = false) {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerCloseIcon()
                                DrawerHeader("Notice", description = "The page behind stays interactive.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Modal",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Drawer(open = open, onOpenChange = { open = it }, modal = true) {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerCloseIcon()
                                DrawerHeader("Notice", description = "The page behind is inert.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Drawer(open = open, onOpenChange = { open = it }, modal = true) {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerCloseIcon()
                                DrawerHeader("Notice", description = "The page behind is inert.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default modal",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Drawer(open = open, onOpenChange = { open = it }, modal = true) {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerCloseIcon()
                                DrawerHeader("Notice", description = "The page behind is inert.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Drawer(open = open, onOpenChange = { open = it }, modal = true) {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerCloseIcon()
                                DrawerHeader("Notice", description = "The page behind is inert.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Swipe handle",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Drawer(
                            open = open,
                            onOpenChange = { open = it },
                            showSwipeHandle = true,
                        ) {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerHeader(
                                    "Notice",
                                    description = "showSwipeHandle paints DrawerSwipeHandle (Base default is false).",
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Drawer(
                            open = open,
                            onOpenChange = { open = it },
                            showSwipeHandle = true,
                        ) {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerHeader(
                                    "Notice",
                                    description = "showSwipeHandle paints DrawerSwipeHandle (Base default is false).",
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Undismissible",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Drawer(
                            open = open,
                            onOpenChange = { open = it },
                            dismissible = false,
                        ) {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerCloseIcon()
                                DrawerHeader(
                                    "Notice",
                                    description = "Swipe and clicking outside will not close this drawer.",
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Drawer(
                            open = open,
                            onOpenChange = { open = it },
                            dismissible = false,
                        ) {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerCloseIcon()
                                DrawerHeader(
                                    "Notice",
                                    description = "Swipe and clicking outside will not close this drawer.",
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default dismissible",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Drawer(
                            open = open,
                            onOpenChange = { open = it },
                            dismissible = true,
                        ) {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerCloseIcon()
                                DrawerHeader("Notice", description = "Swipe or click outside to close.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Drawer(
                            open = open,
                            onOpenChange = { open = it },
                            dismissible = true,
                        ) {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerCloseIcon()
                                DrawerHeader("Notice", description = "Swipe or click outside to close.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Disable pointer dismissal",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Drawer(
                            open = open,
                            onOpenChange = { open = it },
                            disablePointerDismissal = true,
                        ) {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerHeader(
                                    "Notice",
                                    description = "Outside presses do not close (Base disablePointerDismissal). Swipe still works.",
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Drawer(
                            open = open,
                            onOpenChange = { open = it },
                            disablePointerDismissal = true,
                        ) {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerHeader(
                                    "Notice",
                                    description = "Outside presses do not close (Base disablePointerDismissal). Swipe still works.",
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Snap points",
                    code =
                        """
                        val snaps = listOf(
                            DrawerSnapPoint.Absolute(148.dp),
                            DrawerSnapPoint.Fraction(1f),
                        )
                        var open by remember { mutableStateOf(false) }
                        var snap by remember { mutableStateOf<DrawerSnapPoint?>(snaps[0]) }
                        Drawer(
                            open = open,
                            onOpenChange = { open = it },
                            showSwipeHandle = true,
                            snapPoints = snaps,
                            snapPoint = snap,
                            onSnapPointChange = { snap = it },
                        ) {
                            DrawerTrigger("Open snap drawer")
                            DrawerContent {
                                DrawerHeader(
                                    "Snap points",
                                    description = "Drag between 148.dp and full height; past the lowest snap dismisses.",
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        val snaps =
                            listOf(
                                DrawerSnapPoint.Absolute(148.dp),
                                DrawerSnapPoint.Fraction(1f),
                            )
                        var open by remember { mutableStateOf(false) }
                        var snap by remember { mutableStateOf<DrawerSnapPoint?>(snaps[0]) }
                        Drawer(
                            open = open,
                            onOpenChange = { open = it },
                            showSwipeHandle = true,
                            snapPoints = snaps,
                            snapPoint = snap,
                            onSnapPointChange = { snap = it },
                        ) {
                            DrawerTrigger("Open snap drawer")
                            DrawerContent {
                                DrawerHeader(
                                    "Snap points",
                                    description = "Drag between 148.dp and full height; past the lowest snap dismisses.",
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Nested drawers",
                    code =
                        """
                        var outer by remember { mutableStateOf(false) }
                        var inner by remember { mutableStateOf(false) }
                        Drawer(open = outer, onOpenChange = { outer = it }, showSwipeHandle = true) {
                            DrawerTrigger("Open outer")
                            DrawerContent {
                                DrawerHeader("Outer", description = "Open another drawer from here.")
                                Drawer(open = inner, onOpenChange = { inner = it }, showSwipeHandle = true) {
                                    DrawerTrigger("Open nested")
                                    DrawerContent {
                                        DrawerHeader("Nested", description = "Parent scales/peeks while this is open.")
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var outer by remember { mutableStateOf(false) }
                        var inner by remember { mutableStateOf(false) }
                        Drawer(open = outer, onOpenChange = { outer = it }, showSwipeHandle = true) {
                            DrawerTrigger("Open outer")
                            DrawerContent {
                                DrawerHeader("Outer", description = "Open another drawer from here.")
                                Drawer(open = inner, onOpenChange = { inner = it }, showSwipeHandle = true) {
                                    DrawerTrigger("Open nested")
                                    DrawerContent {
                                        DrawerHeader("Nested", description = "Parent scales/peeks while this is open.")
                                    }
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
                        Drawer(open = open, onOpenChange = { open = it }) {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerCloseIcon(disabled = true)
                                DrawerHeader("Notice", description = "The close control is disabled.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Drawer(open = open, onOpenChange = { open = it }) {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerCloseIcon(disabled = true)
                                DrawerHeader("Notice", description = "The close control is disabled.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        Drawer(defaultOpen = false) {
                            DrawerTrigger("Open drawer", disabled = false)
                            DrawerContent {
                                DrawerHeader("Notice", description = "Starts closed.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Drawer(defaultOpen = false) {
                            DrawerTrigger("Open drawer", disabled = false)
                            DrawerContent {
                                DrawerHeader("Notice", description = "Starts closed.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Prevent pointer dismiss",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Drawer(open = open, onOpenChange = { open = it }) {
                            DrawerTrigger("Open drawer")
                            DrawerContent(
                                onPointerDownOutside = { true },
                            ) {
                                DrawerCloseIcon()
                                DrawerHeader("Notice", description = "Clicking the overlay will not close this drawer.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Drawer(open = open, onOpenChange = { open = it }) {
                            DrawerTrigger("Open drawer")
                            DrawerContent(
                                onPointerDownOutside = { true },
                            ) {
                                DrawerCloseIcon()
                                DrawerHeader("Notice", description = "Clicking the overlay will not close this drawer.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Prevent interact outside",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Drawer(open = open, onOpenChange = { open = it }) {
                            DrawerTrigger("Open drawer")
                            DrawerContent(
                                onInteractOutside = { true },
                            ) {
                                DrawerCloseIcon()
                                DrawerHeader("Notice", description = "Interacting outside will not close this drawer.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Drawer(open = open, onOpenChange = { open = it }) {
                            DrawerTrigger("Open drawer")
                            DrawerContent(
                                onInteractOutside = { true },
                            ) {
                                DrawerCloseIcon()
                                DrawerHeader("Notice", description = "Interacting outside will not close this drawer.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Accessible close label",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Drawer(open = open, onOpenChange = { open = it }) {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerCloseIcon(ariaLabel = "Dismiss drawer")
                                DrawerHeader("Notice", description = "The close button has a custom accessible name.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Drawer(open = open, onOpenChange = { open = it }) {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerCloseIcon(ariaLabel = "Dismiss drawer")
                                DrawerHeader("Notice", description = "The close button has a custom accessible name.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Uncontrolled",
                    code =
                        """
                        Drawer {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerCloseIcon()
                                DrawerHeader(
                                    "Notice",
                                    description = "Omit open and onOpenChange. Optional defaultOpen starts closed.",
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Drawer {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerCloseIcon()
                                DrawerHeader(
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
                        Drawer(defaultOpen = false) {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerCloseIcon()
                                DrawerHeader("Notice", description = "Starts closed.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Drawer(defaultOpen = false) {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerCloseIcon()
                                DrawerHeader("Notice", description = "Starts closed.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Title and description",
                    code =
                        """
                        Drawer {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerCloseIcon()
                                DrawerHeader {
                                    DrawerTitle("Notice")
                                    DrawerDescription("Compose Title and Description inside Header.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Drawer {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerCloseIcon()
                                DrawerHeader {
                                    DrawerTitle("Notice")
                                    DrawerDescription("Compose Title and Description inside Header.")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Footer",
                    code =
                        """
                        Drawer {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerCloseIcon()
                                DrawerHeader {
                                    DrawerTitle("Notice")
                                    DrawerDescription("Footer holds actions, matching shadcn DrawerFooter.")
                                }
                                DrawerFooter {
                                    Button("Close", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Drawer {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerCloseIcon()
                                DrawerHeader {
                                    DrawerTitle("Notice")
                                    DrawerDescription("Footer holds actions, matching shadcn DrawerFooter.")
                                }
                                DrawerFooter {
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
                        Drawer {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerCloseIcon()
                                DrawerHeader {
                                    DrawerTitle("Notice")
                                    DrawerDescription("Compose Title and Description inside Header.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Drawer {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerCloseIcon()
                                DrawerHeader {
                                    DrawerTitle("Notice")
                                    DrawerDescription("Compose Title and Description inside Header.")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Close (children slot)",
                    code =
                        """
                        Drawer {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerHeader {
                                    DrawerTitle("Notice")
                                    DrawerDescription("Compose uses children instead of Base render={Button}.")
                                }
                                DrawerFooter {
                                    DrawerClose {
                                        Button("Cancel", onClick = close, variant = ButtonVariant.Outline)
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Drawer {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerHeader {
                                    DrawerTitle("Notice")
                                    DrawerDescription("Compose uses children instead of Base render={Button}.")
                                }
                                DrawerFooter {
                                    DrawerClose {
                                        Button("Cancel", onClick = close, variant = ButtonVariant.Outline)
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Trigger (children slot)",
                    code =
                        """
                        Drawer {
                            DrawerTrigger {
                                Button("Open drawer", onClick = toggle, variant = ButtonVariant.Outline)
                            }
                            DrawerContent {
                                DrawerHeader {
                                    DrawerTitle("Notice")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Drawer {
                            DrawerTrigger {
                                Button("Open drawer", onClick = toggle, variant = ButtonVariant.Outline)
                            }
                            DrawerContent {
                                DrawerHeader {
                                    DrawerTitle("Notice")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Description",
                    code =
                        """
                        Drawer {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerHeader {
                                    DrawerTitle("Notice")
                                    DrawerDescription("DrawerDescription matches shadcn's description slot.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Drawer {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerHeader {
                                    DrawerTitle("Notice")
                                    DrawerDescription("DrawerDescription matches shadcn's description slot.")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Overlay",
                    code =
                        """
                        Drawer {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerHeader {
                                    DrawerTitle("Notice")
                                    DrawerDescription("DrawerOverlay is the scrim composed by DrawerContent.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Drawer {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerHeader {
                                    DrawerTitle("Notice")
                                    DrawerDescription("DrawerOverlay is the scrim composed by DrawerContent.")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Content",
                    code =
                        """
                        Drawer {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerHeader {
                                    DrawerTitle("Notice")
                                    DrawerDescription("DrawerContent hosts Header, Title, and Description.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Drawer {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerHeader {
                                    DrawerTitle("Notice")
                                    DrawerDescription("DrawerContent hosts Header, Title, and Description.")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Title",
                    code =
                        """
                        Drawer {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerHeader {
                                    DrawerTitle("Notice")
                                    DrawerDescription("Compose Title and Description inside Header.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Drawer {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerHeader {
                                    DrawerTitle("Notice")
                                    DrawerDescription("Compose Title and Description inside Header.")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Portal",
                    code =
                        """
                        Drawer {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerHeader {
                                    DrawerTitle("Notice")
                                    DrawerDescription("DrawerPortal is the teleport host composed by DrawerContent.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Drawer {
                            DrawerTrigger("Open drawer")
                            DrawerContent {
                                DrawerHeader {
                                    DrawerTitle("Notice")
                                    DrawerDescription("DrawerPortal is the teleport host composed by DrawerContent.")
                                }
                            }
                        }
                    },
                ),
            ),
    )
