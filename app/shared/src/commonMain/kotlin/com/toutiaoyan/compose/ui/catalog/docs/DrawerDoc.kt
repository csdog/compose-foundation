@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import com.toutiaoyan.compose.ui.components.drawer.DrawerFooterColumn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import com.toutiaoyan.compose.ui.catalog.catalogViewportWidth
import com.toutiaoyan.compose.ui.components.dialog.Dialog
import com.toutiaoyan.compose.ui.components.dialog.DialogContent
import com.toutiaoyan.compose.ui.components.dialog.DialogTrigger
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.currentTheme
import androidx.compose.foundation.layout.Arrangement
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

private val drawerLargeContentCode =
    """
    @Composable
    fun DrawerLargeContentExample() {
        Drawer(swipeDirection = DrawerSwipeDirection.Down, showSwipeHandle = true) {
            DrawerTrigger("Open large bottom drawer", variant = ButtonVariant.Outline)
            DrawerContent {
                DrawerHeader("Activity history", "Review your daily activity before setting a new goal.")
                Column(
                    Modifier.weight(1f, fill = false).clipToBounds()
                        .verticalScroll(rememberScrollState()).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    repeat(24) { index ->
                        Column(
                            Modifier.fillMaxWidth()
                                .background(currentTheme.colors.muted, RoundedCornerShape(16.dp))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            SurfaceText("Day ${'$'}{index + 1}", medium = true)
                            SurfaceText("${'$'}{6200 + index * 180} steps · ${'$'}{24 + index} active minutes", muted = true)
                        }
                    }
                }
                DrawerFooterColumn(Modifier.fillMaxWidth().background(currentTheme.colors.popover)) {
                    Button("Done", onClick = close, modifier = Modifier.fillMaxWidth(), style = Style { width(1f) })
                }
            }
        }
    }

    """.trimIndent()

private val drawerNestedCode =
    """
    @Composable
    fun DrawerNestedParityExample() {
        val mobile = LocalWindowInfo.current.containerSize.width < with(LocalDensity.current) { 768.dp.roundToPx() }
        DrawerNestedLevel(1, mobile)
    }

    @Composable
    private fun DrawerNestedLevel(level: Int, mobile: Boolean) {
        Drawer(showSwipeHandle = mobile, swipeDirection = if (mobile) DrawerSwipeDirection.Down else DrawerSwipeDirection.Right) {
            DrawerTrigger(
                when (level) { 1 -> "Open outer"; 2 -> "Open nested"; 3 -> "Open third"; else -> "Open fourth" },
                modifier = if (level > 1) Modifier.fillMaxWidth() else Modifier,
                variant = ButtonVariant.Outline,
            )
            DrawerContent {
                DrawerHeader(
                    when (level) { 1 -> "Drawer"; 2 -> "Nested Drawer"; 3 -> "Third Drawer"; else -> "Fourth Drawer" },
                    "Open another drawer from the same direction.",
                )
                Box(Modifier.weight(1f, fill = !mobile).padding(16.dp)) {
                    Box(
                        Modifier.fillMaxWidth().then(if (mobile) Modifier.aspectRatio(16f / 9f) else Modifier.fillMaxHeight())
                            .background(currentTheme.colors.muted, RoundedCornerShape(16.dp)),
                    )
                }
                DrawerFooterColumn(Modifier.fillMaxWidth()) {
                    if (level < 4) DrawerNestedLevel(level + 1, mobile)
                    Button("Close", onClick = close, modifier = Modifier.fillMaxWidth(), style = Style { width(1f) })
                }
            }
        }
    }

    """.trimIndent()

private val drawerExtensionExamples =
    listOf(
        ComponentExample(
            title = "Edit profile (bottom edge, drag to dismiss)",
            code =
                """
                @Composable
                fun DrawerParityExample() {
                    var open by remember { mutableStateOf(false) }
                    var selected by remember { mutableStateOf("asap") }
                    var confirmed by remember { mutableStateOf("") }
                    val mobile = LocalWindowInfo.current.containerSize.width < with(LocalDensity.current) { 768.dp.roundToPx() }
                    Column {
                        Drawer(open = open, onOpenChange = { open = it }, showSwipeHandle = mobile, swipeDirection = if (mobile) DrawerSwipeDirection.Down else DrawerSwipeDirection.Right) {
                            DrawerTrigger("Open Drawer", variant = ButtonVariant.Secondary)
                            DrawerContent {
                                DrawerHeader("Pick a delivery time", "We'll prepare your order as soon as possible.")
                                Column(Modifier.weight(1f, fill = !mobile).clipToBounds().verticalScroll(rememberScrollState()).padding(16.dp)) {
                                    RadioGroup(value = selected, onValueChange = { selected = it }) {
                                        deliveryTimes.forEach { (value, label, description) ->
                                            Row(Modifier.fillMaxWidth().pointerInput(value) { detectTapGestures(onTap = { selected = value }) }.background(if (selected == value) currentTheme.colors.muted.copy(alpha = 0.3f) else androidx.compose.ui.graphics.Color.Transparent, RoundedCornerShape(twRadius.lg)).border(1.dp, currentTheme.colors.border, RoundedCornerShape(twRadius.lg)).padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                                Column(Modifier.weight(1f)) {
                                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                        SurfaceText(label, medium = true)
                                                        if (value == "asap") Badge("Fastest", variant = BadgeVariant.Secondary)
                                                    }
                                                    SurfaceText(description, muted = true)
                                                }
                                                RadioGroupItem(value = value, label = label)
                                            }
                                        }
                                    }
                                }
                                DrawerFooterColumn(modifier = Modifier.fillMaxWidth().background(currentTheme.colors.popover)) {
                                    Button("Confirm Delivery Time", style = Style { width(1f); height(34.dp) }, modifier = Modifier.fillMaxWidth(), onClick = {
                                        confirmed = deliveryTimes.first { it.first == selected }.second; open = false
                                    })
                                    Button("Cancel", style = Style { width(1f) }, modifier = Modifier.fillMaxWidth(), variant = ButtonVariant.Outline, onClick = { open = false })
                                }
                            }
                        }
                        if (confirmed.isNotEmpty()) SurfaceText("Delivery time confirmed: ${'$'}confirmed", muted = true)
                    }
                }

                @Composable
                private fun SurfaceText(text: String, muted: Boolean = false, medium: Boolean = false) {
                    val theme = currentTheme
                    Text(textStyle = theme.typography.bodyMedium.copy(fontFamily = theme.fontFamily, fontSize = 14.sp, lineHeight = 20.sp,
                        fontWeight = if (medium) FontWeight.Medium else FontWeight.Normal,
                        color = if (muted) theme.colors.mutedForeground else theme.colors.foreground)) { text }
                }

                private val deliveryTimes = listOf(
                    Triple("asap", "Standard delivery", "25–35 min · Driver assigned now"),
                    Triple("5-00", "5:00 PM – 5:15 PM", "Prep starts at 4:45 PM"),
                    Triple("5-30", "5:30 PM – 5:45 PM", "Good if you're heading home"),
                    Triple("6-00", "6:00 PM – 6:15 PM", "Most popular · High demand"),
                    Triple("6-30", "6:30 PM – 6:45 PM", "Last slot before kitchen closes"),
                )
                """.trimIndent(),
            preview = { DrawerParityExample() },
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
            code = drawerLargeContentCode,
            preview = { DrawerLargeContentExample() },
        ),
        ComponentExample(
            title = "Scrollable content",
            code = drawerLargeContentCode,
            preview = { DrawerLargeContentExample() },
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
                    disablePointerDismissal = true,
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
                    disablePointerDismissal = true,
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
                            description =
                                "Outside presses do not close (Base disablePointerDismissal). " +
                                    "Swipe still works.",
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
                            description =
                                "Outside presses do not close (Base disablePointerDismissal). " +
                                    "Swipe still works.",
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
                    DrawerSnapPoint.Absolute(496.dp),
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
                            description =
                                "Drag the drawer to snap between a compact peek and a near full-height view.",
                        )
                        Box(Modifier.weight(1f).padding(16.dp)) {
                            Box(Modifier.fillMaxWidth().height(320.dp).background(currentTheme.colors.muted, RoundedCornerShape(16.dp)))
                        }
                        DrawerFooterColumn(Modifier.fillMaxWidth()) {
                            DrawerClose { Button("Close", onClick = close, modifier = Modifier.fillMaxWidth()) }
                        }
                    }
                }
                """.trimIndent(),
            preview = {
                val snaps =
                    listOf(
                        DrawerSnapPoint.Absolute(496.dp),
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
                            description =
                                "Drag the drawer to snap between a compact peek and a near full-height view.",
                        )
                        Box(Modifier.weight(1f).padding(16.dp)) {
                            Box(
                                Modifier.fillMaxWidth().height(
                                    320.dp,
                                ).background(currentTheme.colors.muted, RoundedCornerShape(16.dp)),
                            )
                        }
                        DrawerFooterColumn(Modifier.fillMaxWidth()) {
                            DrawerClose { Button("Close", onClick = close, modifier = Modifier.fillMaxWidth()) }
                        }
                    }
                }
            },
        ),
        ComponentExample(
            title = "Indent effect",
            code =
                """
                // At the app root: OverlayHost { DrawerIndentHost { AppContent() } }
                Drawer(indentBackground = true, showSwipeHandle = true) {
                    DrawerTrigger("Open indent drawer")
                    DrawerContent {
                        DrawerHeader("Indent effect", "Drag the drawer to restore the page scale and corners. Release to settle together.")
                        DrawerFooter { DrawerClose { Button("Close", onClick = close, modifier = Modifier.fillMaxWidth()) } }
                    }
                }
                """.trimIndent(),
            preview = {
                Drawer(indentBackground = true, showSwipeHandle = true) {
                    DrawerTrigger("Open indent drawer")
                    DrawerContent {
                        DrawerHeader(
                            "Indent effect",
                            (
                                "Drag the drawer to restore the page scale and corners. " +
                                    "Release to settle together."
                            ),
                        )
                        DrawerFooter {
                            DrawerClose {
                                Button(
                                    "Close",
                                    onClick = close,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }
                }
            },
        ),
        ComponentExample(
            title = "Nested drawers",
            code = drawerNestedCode,
            preview = { DrawerNestedParityExample() },
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
                            Button("Close", onClick = close)
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
                            Button("Close", onClick = close)
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
    )

/** Official recipes promoted into [drawerOfficialExamples]; not repeated as extensions. */
private val drawerPromotedExamples =
    setOf(
        "Edit profile (bottom edge, drag to dismiss)",
        "Non-modal",
        "Modal",
        "Default modal",
        "Swipe handle",
        "Nested drawers",
        "Snap points",
    )

private val drawerOfficialExamples =
    listOf(
        ComponentExample(
            title = "Delivery time",
            code =
                """
                @Composable
                fun DrawerDemo() {
                    var open by remember { mutableStateOf(false) }
                    var deliveryTime by remember { mutableStateOf("asap") }
                    val toasts = rememberToastState()
                    val mobile = LocalWindowInfo.current.containerSize.width <
                        with(LocalDensity.current) { 768.dp.roundToPx() }
                    Box {
                        Drawer(
                            open = open,
                            onOpenChange = { open = it },
                            showSwipeHandle = mobile,
                            swipeDirection = if (mobile) DrawerSwipeDirection.Down else DrawerSwipeDirection.Right,
                        ) {
                            DrawerTrigger("Open Drawer", variant = ButtonVariant.Secondary)
                            DrawerContent {
                                DrawerHeader("Pick a delivery time", "We'll prepare your order as soon as possible.")
                                Column(Modifier.weight(1f).clipToBounds().verticalScroll(rememberScrollState()).padding(16.dp)) {
                                    RadioGroup(value = deliveryTime, onValueChange = { deliveryTime = it }) {
                                        deliveryTimes.forEach { time ->
                                            FieldLabel {
                                                Field(orientation = FieldOrientation.Horizontal) {
                                                    FieldContent {
                                                        FieldTitle { time.label }
                                                        FieldDescription(time.description)
                                                    }
                                                    RadioGroupItem(time.value)
                                                }
                                            }
                                        }
                                    }
                                }
                                DrawerFooterColumn(Modifier.fillMaxWidth()) {
                                    Button("Confirm Delivery Time", onClick = {
                                        val selected = deliveryTimes.first { it.value == deliveryTime }
                                        open = false
                                        toasts.show("Delivery time confirmed", description = selected.label)
                                    }, modifier = Modifier.fillMaxWidth(), style = Style { height(34.dp) })
                                    DrawerClose { Button("Cancel", onClick = close, variant = ButtonVariant.Outline, modifier = Modifier.fillMaxWidth()) }
                                }
                            }
                        }
                        Toaster(state = toasts)
                    }
                }
                """.trimIndent(),
            preview = { DrawerParityExample() },
        ),
        ComponentExample(
            title = "Position",
            code =
                """
                Drawer(swipeDirection = DrawerSwipeDirection.Left) {
                    DrawerTrigger("Open Left Drawer", variant = ButtonVariant.Secondary)
                    DrawerContent {
                        DrawerHeader("Move Goal", "Set your daily activity goal.")
                        Box(Modifier.weight(1f).padding(16.dp)) {
                            Box(Modifier.fillMaxSize().background(currentTheme.colors.muted, RoundedCornerShape(16.dp)))
                        }
                        DrawerFooterColumn(Modifier.fillMaxWidth()) {
                            DrawerClose { Button("Close", onClick = close, modifier = Modifier.fillMaxWidth()) }
                        }
                    }
                }
                """.trimIndent(),
            preview = { DrawerPositionRecipe() },
        ),
        ComponentExample(
            title = "Swipe Handle",
            code =
                """
                Drawer(showSwipeHandle = true) {
                    DrawerTrigger("Open Drawer", variant = ButtonVariant.Secondary)
                    DrawerContent {
                        DrawerHeader("Drawer", "Drawer with a swipe handle.")
                        Box(Modifier.weight(1f).padding(16.dp)) {
                            Box(Modifier.fillMaxWidth().height(320.dp).background(currentTheme.colors.muted, RoundedCornerShape(16.dp)))
                        }
                        DrawerFooterColumn(Modifier.fillMaxWidth()) {
                            DrawerClose { Button("Close", onClick = close, modifier = Modifier.fillMaxWidth()) }
                        }
                    }
                }
                """.trimIndent(),
            preview = { DrawerSwipeHandleRecipe() },
        ),
        ComponentExample(
            title = "Nested",
            code = drawerNestedCode,
            preview = { DrawerNestedParityExample() },
        ),
        ComponentExample(
            title = "Non Modal",
            code =
                """
                Drawer(modal = false, disablePointerDismissal = true, swipeDirection = DrawerSwipeDirection.Right) {
                    DrawerTrigger("Non Modal", variant = ButtonVariant.Outline)
                    DrawerContent {
                        DrawerHeader("Non Modal Drawer")
                        Box(Modifier.weight(1f).padding(16.dp)) {
                            Box(Modifier.fillMaxSize().background(currentTheme.colors.muted, RoundedCornerShape(16.dp)))
                        }
                        DrawerFooterColumn(Modifier.fillMaxWidth()) {
                            DrawerClose { Button("Close", onClick = close, modifier = Modifier.fillMaxWidth()) }
                        }
                    }
                }
                """.trimIndent(),
            preview = { DrawerNonModalRecipe() },
        ),
        ComponentExample(
            title = "Snap Points",
            code =
                """
                val snaps = listOf(DrawerSnapPoint.Absolute(496.dp), DrawerSnapPoint.Fraction(1f))
                var snap by remember { mutableStateOf<DrawerSnapPoint?>(snaps.first()) }
                Drawer(snapPoints = snaps, snapPoint = snap, onSnapPointChange = { snap = it }, showSwipeHandle = true) {
                    DrawerTrigger("Open Snap Drawer", variant = ButtonVariant.Outline)
                    DrawerContent {
                        DrawerHeader(
                            "Snap points",
                            "Drag the drawer to snap between a compact peek and a near full-height view.",
                        )
                        Box(Modifier.weight(1f).padding(16.dp)) {
                            Box(Modifier.fillMaxWidth().height(320.dp).background(currentTheme.colors.muted, RoundedCornerShape(16.dp)))
                        }
                        DrawerFooterColumn(Modifier.fillMaxWidth()) {
                            DrawerClose { Button("Close", onClick = close, modifier = Modifier.fillMaxWidth()) }
                        }
                    }
                }
                """.trimIndent(),
            preview = { DrawerSnapPointsRecipe() },
        ),
        ComponentExample(
            title = "Responsive",
            code =
                """
                val desktop = catalogViewportWidth() >= 768.dp
                if (desktop) {
                    Dialog {
                        DialogTrigger("Edit Profile", variant = ButtonVariant.Outline)
                        DialogContent("Edit profile", description = "Make changes to your profile here. Click save when you're done.") {
                            ProfileForm()
                        }
                    }
                } else {
                    Drawer {
                        DrawerTrigger("Edit Profile", variant = ButtonVariant.Outline)
                        DrawerContent("Edit profile", "Make changes to your profile here. Click save when you're done.") {
                            ProfileForm(Modifier.padding(16.dp))
                        }
                    }
                }
                """.trimIndent(),
            preview = { DrawerResponsiveRecipe() },
        ),
    )

@Composable
private fun DrawerPositionRecipe() {
    Drawer(swipeDirection = DrawerSwipeDirection.Left) {
        DrawerTrigger("Open Left Drawer", variant = ButtonVariant.Secondary)
        DrawerContent {
            DrawerHeader("Move Goal", "Set your daily activity goal.")
            Box(Modifier.weight(1f).padding(16.dp)) {
                Box(Modifier.fillMaxSize().background(currentTheme.colors.muted, RoundedCornerShape(16.dp)))
            }
            DrawerFooterColumn(Modifier.fillMaxWidth()) {
                DrawerClose { Button("Close", onClick = close, modifier = Modifier.fillMaxWidth()) }
            }
        }
    }
}

@Composable
private fun DrawerSwipeHandleRecipe() {
    Drawer(showSwipeHandle = true) {
        DrawerTrigger("Open Drawer", variant = ButtonVariant.Secondary)
        DrawerContent {
            DrawerHeader("Drawer", "Drawer with a swipe handle.")
            Box(Modifier.padding(16.dp)) {
                Box(
                    Modifier.fillMaxWidth().height(
                        320.dp,
                    ).background(currentTheme.colors.muted, RoundedCornerShape(16.dp)),
                )
            }
            DrawerFooterColumn(Modifier.fillMaxWidth()) {
                DrawerClose { Button("Close", onClick = close, modifier = Modifier.fillMaxWidth()) }
            }
        }
    }
}

@Composable
private fun DrawerNonModalRecipe() {
    Drawer(modal = false, disablePointerDismissal = true, swipeDirection = DrawerSwipeDirection.Right) {
        DrawerTrigger("Non Modal", variant = ButtonVariant.Outline)
        DrawerContent {
            DrawerHeader("Non Modal Drawer")
            Box(Modifier.weight(1f).padding(16.dp)) {
                Box(Modifier.fillMaxSize().background(currentTheme.colors.muted, RoundedCornerShape(16.dp)))
            }
            DrawerFooterColumn(Modifier.fillMaxWidth()) {
                DrawerClose { Button("Close", onClick = close, modifier = Modifier.fillMaxWidth()) }
            }
        }
    }
}

@Composable
private fun DrawerSnapPointsRecipe() {
    val snaps = listOf(DrawerSnapPoint.Absolute(496.dp), DrawerSnapPoint.Fraction(1f))
    var snap by remember { mutableStateOf<DrawerSnapPoint?>(snaps.first()) }
    Drawer(
        snapPoints = snaps,
        snapPoint = snap,
        onSnapPointChange = { snap = it },
        showSwipeHandle = true,
    ) {
        DrawerTrigger("Open Snap Drawer", variant = ButtonVariant.Outline)
        DrawerContent {
            DrawerHeader(
                "Snap points",
                "Drag the drawer to snap between a compact peek and a near full-height view.",
            )
            Box(Modifier.padding(16.dp)) {
                Box(
                    Modifier.fillMaxWidth().height(
                        320.dp,
                    ).background(currentTheme.colors.muted, RoundedCornerShape(16.dp)),
                )
            }
            DrawerFooterColumn(Modifier.fillMaxWidth()) {
                DrawerClose { Button("Close", onClick = close, modifier = Modifier.fillMaxWidth()) }
            }
        }
    }
}

@Composable
private fun DrawerResponsiveRecipe() {
    val desktop = catalogViewportWidth() >= 768.dp
    if (desktop) {
        Dialog {
            DialogTrigger("Edit Profile", variant = ButtonVariant.Outline)
            DialogContent(
                "Edit profile",
                description = "Make changes to your profile here. Click save when you're done.",
            ) {
                DrawerProfileForm()
            }
        }
    } else {
        var open by remember { mutableStateOf(false) }
        Drawer(open = open, onOpenChange = { open = it }) {
            DrawerTrigger("Edit Profile", variant = ButtonVariant.Outline)
            DrawerContent(
                "Edit profile",
                "Make changes to your profile here. Click save when you're done.",
            ) {
                DrawerProfileForm(Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
private fun DrawerProfileForm(modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf("shadcn@example.com") }
    var username by remember { mutableStateOf("@shadcn") }
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text { "Email" }
            Input(value = email, onValueChange = { email = it })
        }
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text { "Username" }
            Input(value = username, onValueChange = { username = it })
        }
        Button("Save changes", onClick = {})
    }
}

private val drawerGuideSections =
    listOf(
        ComponentGuideSection(
            title = "Custom Sizes",
            blocks =
                listOf(
                    ComponentGuideBlock.Paragraph(
                        (
                            "A vertical drawer sizes itself to its content and is capped at 96dp less " +
                                "than the viewport height (`calc(100dvh - 6rem)`). A side drawer spans " +
                                "`75%` of the viewport width, or 384dp on larger screens."
                        ),
                    ),
                    ComponentGuideBlock.Paragraph(
                        "To customize the panel, pass a `Modifier` or `Style` to `DrawerContent`.",
                    ),
                    ComponentGuideBlock.Code(
                        """
                        DrawerContent(style = Style { height(400.dp) }) { /* ... */ }
                        """.trimIndent(),
                    ),
                    ComponentGuideBlock.Code(
                        """
                        // Side drawer width
                        Drawer(swipeDirection = DrawerSwipeDirection.Right) {
                            DrawerContent(style = Style { width(384.dp) }) { /* ... */ }
                        }
                        """.trimIndent(),
                    ),
                    ComponentGuideBlock.Paragraph(
                        (
                            "To make a region of the drawer scrollable, make the scroll container a " +
                                "flex item — `Modifier.weight(1f)` inside `DrawerContent`'s column. Avoid " +
                                "`fillMaxHeight`, which does not resolve inside a content-sized drawer."
                        ),
                    ),
                    ComponentGuideBlock.Code(
                        """
                        DrawerContent {
                            DrawerHeader("Notice", "Scrollable body")
                            Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) { /* ... */ }
                            DrawerFooter { /* ... */ }
                        }
                        """.trimIndent(),
                    ),
                ),
        ),
        ComponentGuideSection(
            title = "Styling",
            blocks =
                listOf(
                    ComponentGuideBlock.Paragraph(
                        (
                            "The web drawer exposes CSS variables and `data-*` attributes for " +
                                "style-level customization. Compose has no CSS cascade, so the same " +
                                "knobs are component parameters."
                        ),
                    ),
                    ComponentGuideBlock.Paragraph(
                        "Nova uses directional rounded-xl corners and an interior-edge border. The official " +
                            "page previews use Rhea; their floating chrome is not the Nova specification. " +
                            "Backdrop blur records the underlying content through `OverlayHost` and requires " +
                            "platform RenderEffect support. Without the host, " +
                            "the popup fallback has no backdrop capture.",
                    ),
                    ComponentGuideBlock.Table(
                        headers = listOf("Web variable", "Default", "Compose behavior"),
                        rows =
                            listOf(
                                listOf(
                                    "`--drawer-inset`",
                                    "`0px`",
                                    "Right Nova drawers leave an 8dp gap. Other directions attach to the viewport.",
                                ),
                                listOf(
                                    "`--drawer-bleed-background`",
                                    "`var(--color-popover)`",
                                    "Panel uses popover fill; anchored drags clamp at the open edge, " +
                                        "so no overshoot gap is exposed.",
                                ),
                                listOf(
                                    "`--drawer-overlay-min-opacity`",
                                    "`0` (`0.5` with snap points)",
                                    "Swipe fades the 10% black scrim and captured backdrop together; " +
                                        "snap points keep an opacity floor of 0.5.",
                                ),
                            ),
                    ),
                    ComponentGuideBlock.Table(
                        headers = listOf("Web attribute", "Values / when set", "Compose mapping"),
                        rows =
                            listOf(
                                listOf(
                                    "`data-swipe-direction`",
                                    "up / right / down / left",
                                    "`Drawer.swipeDirection`",
                                ),
                                listOf(
                                    "`data-swipe-axis`",
                                    "x / y",
                                    "Derived from the supplied `swipeDirection`.",
                                ),
                                listOf(
                                    "`data-snap-points`",
                                    "Snap points configured",
                                    "`Drawer.snapPoints`",
                                ),
                                listOf(
                                    "`data-expanded`",
                                    "At the full snap point",
                                    "Compare `DrawerScope.snapPoint`.",
                                ),
                                listOf(
                                    "`data-swiping`",
                                    "During a swipe",
                                    "No public swipe-progress state.",
                                ),
                                listOf(
                                    "`data-nested-drawer-open`",
                                    "Nested drawer open",
                                    "`DrawerScope.nestedOpenCount > 0`",
                                ),
                            ),
                    ),
                ),
        ),
    )

private val drawerMigrationSections =
    listOf(
        ComponentGuideSection(
            title = "Migrating from Vaul",
            blocks =
                listOf(
                    ComponentGuideBlock.Paragraph(
                        (
                            "The Base drawer replaces Vaul. This library follows the Base UI API: " +
                                "`swipeDirection` replaces `direction`, the slot overloads replace " +
                                "`asChild`/`render`, and snap points use the controlled " +
                                "`snapPoint` / `onSnapPointChange` pair."
                        ),
                    ),
                    ComponentGuideBlock.Steps(
                        listOf(
                            ComponentGuideBlock.Step(
                                "Update the dependency.",
                                "Compose uses the UI artifact shown in Installation; no Vaul dependency is required.",
                            ),
                            ComponentGuideBlock.Step(
                                "Replace direction with swipeDirection.",
                                "Use `DrawerSwipeDirection.Down` for bottom, `Up` for top, and `Left` / `Right`.",
                            ),
                            ComponentGuideBlock.Step(
                                "Replace asChild with render.",
                                "In Compose, use `DrawerTrigger { Button(...) }` and `DrawerClose { Button(...) }` " +
                                    "and wire `toggle` / `close` explicitly.",
                            ),
                            ComponentGuideBlock.Step(
                                "Update snap point props.",
                                "Use `snapPoint` / `onSnapPointChange` and `snapToSequentialPoints`.",
                            ),
                            ComponentGuideBlock.Step(
                                "Update animation and focus props.",
                                "`onOpenChangeComplete` is not exposed. Initial focus is owned by the overlay host.",
                            ),
                            ComponentGuideBlock.Step(
                                "Review Vaul-only props.",
                                "There are no one-to-one replacements for `handleOnly`, `repositionInputs` or " +
                                    "`shouldScaleBackground`. Outside presses use `disablePointerDismissal`; " +
                                    "the Compose `dismissible` flag controls swipe dismissal separately.",
                            ),
                            ComponentGuideBlock.Step(
                                "Update custom data attribute selectors.",
                                "Compose has no CSS selectors. See the Styling tables for available state and " +
                                    "the web transition and swipe-state limitations.",
                            ),
                        ),
                    ),
                    ComponentGuideBlock.Code(
                        """
                        Drawer(swipeDirection = DrawerSwipeDirection.Down) {
                            DrawerTrigger { Button("Open", onClick = toggle, variant = ButtonVariant.Outline) }
                            DrawerContent { /* ... */ }
                        }
                        """.trimIndent(),
                    ),
                ),
        ),
    )

private val drawerApiSections =
    listOf(
        ComponentApiSection(
            "Drawer",
            (
                "Controlled (`open` + `onOpenChange`) or uncontrolled (`defaultOpen`). " +
                    "`swipeDirection` picks the edge; `showSwipeHandle` paints the grabber. " +
                    "`modal = false` leaves the page interactive and `disablePointerDismissal` " +
                    "keeps outside presses from closing while swipe still works. " +
                    "`dismissible = false` and `indentBackground` are Compose extensions."
            ),
            listOf(
                ApiProperty("open", "Boolean?", "null"),
                ApiProperty("onOpenChange", "((Boolean) -> Unit)?", "null"),
                ApiProperty("defaultOpen", "Boolean", "false"),
                ApiProperty("swipeDirection", "DrawerSwipeDirection", "DrawerSwipeDirection.Down"),
                ApiProperty("showSwipeHandle", "Boolean", "false"),
                ApiProperty("snapPoints", "List<DrawerSnapPoint>?", "null"),
                ApiProperty(
                    "snapPoint / onSnapPointChange",
                    "DrawerSnapPoint? / ((DrawerSnapPoint?) -> Unit)?",
                    "null",
                ),
                ApiProperty("modal", "Boolean", "true"),
                ApiProperty("disablePointerDismissal", "Boolean", "false"),
                ApiProperty("dismissible", "Boolean", "true"),
                ApiProperty("indentBackground", "Boolean", "false"),
            ),
        ),
        ComponentApiSection(
            "DrawerTrigger",
            (
                "Host slot for the open control. The string overload renders an outline " +
                    "`Button`; the slot overload leaves chrome to the caller and does not add " +
                    "its own clickable layer."
            ),
            listOf(
                ApiProperty("text", "String", "Required (string overload)"),
                ApiProperty("variant", "ButtonVariant", "ButtonVariant.Outline"),
                ApiProperty("content", "@Composable () -> Unit", "Required (slot overload)"),
            ),
        ),
        ComponentApiSection(
            "DrawerContent",
            (
                "Portal + overlay + panel. Vertical drawers size to content with the " +
                    "`calc(100dvh - 6rem)` cap; side drawers use 75% of the viewport width " +
                    "384dp on larger screens with no default viewport inset. `onPointerDownOutside` / " +
                    "`onInteractOutside` return true to veto dismissal."
            ),
            listOf(
                ApiProperty("title / description", "String / String?", "Required / null"),
                ApiProperty("onPointerDownOutside", "(() -> Boolean)?", "null"),
                ApiProperty("onInteractOutside", "(() -> Boolean)?", "null"),
                ApiProperty("content", "@Composable ColumnScope.() -> Unit", "Required"),
            ),
        ),
        ComponentApiSection(
            "DrawerHeader / DrawerTitle / DrawerDescription",
            (
                "Header is `p-4 pb-0`; title/description follow the official typography. " +
                    "The title + description overload is a Compose convenience for the slots."
            ),
            listOf(
                ApiProperty("title / description", "String / String?", "Required / null"),
                ApiProperty("content", "@Composable ColumnScope.() -> Unit", "Required (slot)"),
            ),
        ),
        ComponentApiSection(
            "DrawerFooter",
            (
                "`p-4 pt-0`. `DrawerFooter` keeps the original `RowScope` action row; " +
                    "`DrawerFooterColumn` stacks actions vertically like the Base UI footer."
            ),
            listOf(ApiProperty("content", "@Composable RowScope.() -> Unit", "Required")),
        ),
        ComponentApiSection(
            "DrawerClose",
            (
                "Dismiss control host. The string overload renders an outline `Button` wired " +
                    "to `DrawerScope.close`; `DrawerCloseIcon` renders the Nova X glyph."
            ),
            listOf(
                ApiProperty("text", "String", "Required (string overload)"),
                ApiProperty("disabled", "Boolean", "false"),
                ApiProperty("content", "@Composable () -> Unit", "Required (slot overload)"),
            ),
        ),
        ComponentApiSection(
            "DrawerOverlay / DrawerPortal / DrawerSwipeHandle",
            (
                "Lower-level pieces [DrawerContent] composes: the `bg-black/10` " +
                    "overlay, the window-level teleport host and the grabber pill. Use them " +
                    "directly only when replacing the default chrome."
            ),
            listOf(
                ApiProperty("content", "@Composable () -> Unit", "Required"),
            ),
        ),
    )

val drawerDoc =
    ComponentDoc(
        id = "drawer",
        title = "Drawer",
        description = "A drawer component for React.",
        usageImportsCode =
            """
            import com.toutiaoyan.compose.ui.components.button.Button
            import com.toutiaoyan.compose.ui.components.button.ButtonVariant
            import com.toutiaoyan.compose.ui.components.drawer.Drawer
            import com.toutiaoyan.compose.ui.components.drawer.DrawerClose
            import com.toutiaoyan.compose.ui.components.drawer.DrawerContent
            import com.toutiaoyan.compose.ui.components.drawer.DrawerDescription
            import com.toutiaoyan.compose.ui.components.drawer.DrawerFooter
            import com.toutiaoyan.compose.ui.components.drawer.DrawerHeader
            import com.toutiaoyan.compose.ui.components.drawer.DrawerTitle
            import com.toutiaoyan.compose.ui.components.drawer.DrawerTrigger
            """.trimIndent(),
        usageCode =
            """
            var open by remember { mutableStateOf(false) }
            Drawer(open = open, onOpenChange = { open = it }) {
                DrawerTrigger {
                    Button("Open", onClick = toggle, variant = ButtonVariant.Outline)
                }
                DrawerContent {
                    DrawerHeader {
                        DrawerTitle("Are you absolutely sure?")
                        DrawerDescription("This action cannot be undone.")
                    }
                    DrawerFooter {
                        Button("Submit", onClick = close)
                        DrawerClose {
                            Button("Cancel", onClick = close, variant = ButtonVariant.Outline)
                        }
                    }
                }
            }
            """.trimIndent(),
        compositionCode =
            """
            Drawer
            ├── DrawerTrigger
            └── DrawerContent
                ├── DrawerHeader
                │   ├── DrawerTitle
                │   └── DrawerDescription
                └── DrawerFooter
            """.trimIndent(),
        introMarkdown =
            "The drawer now follows [Base UI](https://base-ui.com/react/components/drawer) instead of Vaul. " +
                "See [Migrating from Vaul](#migrating-from-vaul) for the corresponding Kotlin API changes.",
        compositionMarkdown =
            "`DrawerContent` composes the portal, overlay and popup panel. " +
                "`DrawerPortal`, `DrawerOverlay` and `DrawerSwipeHandle` are also exported for lower-level control.",
        beforeExamples = drawerGuideSections,
        afterExamples = drawerMigrationSections,
        examplesHeading = null,
        examples =
            drawerOfficialExamples +
                drawerExtensionExamples
                    .filterNot { it.title in drawerPromotedExamples }
                    .map { it.copy(title = "Kotlin extension: " + it.title) },
        apiSections = drawerApiSections,
    )
