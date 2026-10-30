@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import com.toutiaoyan.compose.ui.components.drawer.DrawerFooterColumn
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

val drawerDoc =
    ComponentDoc(
        id = "drawer",
        title = "Drawer",
        description = "A drawer component for React.",
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
                                DrawerFooter { DrawerClose { Button("Close", onClick = close) } }
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
                                DrawerFooter { DrawerClose { Button("Close", onClick = close) } }
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
