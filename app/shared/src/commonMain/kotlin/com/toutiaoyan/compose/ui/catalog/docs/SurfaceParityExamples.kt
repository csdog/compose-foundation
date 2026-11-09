@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.background
import com.toutiaoyan.compose.tailwind.core.twRadius
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import com.composables.icons.lucide.*
import com.toutiaoyan.compose.ui.components.button.*
import com.toutiaoyan.compose.ui.components.collapsible.*
import com.toutiaoyan.compose.ui.components.command.*
import com.toutiaoyan.compose.ui.components.contextmenu.*
import com.toutiaoyan.compose.ui.components.dialog.*
import com.toutiaoyan.compose.ui.components.drawer.*
import com.toutiaoyan.compose.ui.components.field.*
import com.toutiaoyan.compose.ui.components.input.*
import com.toutiaoyan.compose.ui.components.radiobutton.*
import com.toutiaoyan.compose.ui.components.text.*
import com.toutiaoyan.compose.ui.components.toast.ToastPosition
import com.toutiaoyan.compose.ui.components.toast.Toaster
import com.toutiaoyan.compose.ui.components.toast.rememberToastState
import com.toutiaoyan.compose.ui.components.badge.*
import com.toutiaoyan.compose.ui.components.calendar.*
import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
import com.toutiaoyan.compose.ui.components.popover.*
import com.toutiaoyan.compose.ui.theme.currentTheme
import androidx.compose.foundation.style.then
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding

@Composable
private fun SurfaceText(
    text: String,
    muted: Boolean = false,
    medium: Boolean = false,
) {
    val theme = currentTheme
    Text(
        textStyle =
            theme.typography.bodyMedium.copy(
                fontFamily = theme.fontFamily,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = if (medium) FontWeight.Medium else FontWeight.Normal,
                color = if (muted) theme.colors.mutedForeground else theme.colors.foreground,
            ),
    ) { text }
}

@Composable
internal fun CollapsibleParityExample() {
    Collapsible(modifier = Modifier.widthIn(max = 350.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SurfaceText("Order #4189", medium = true)
            CollapsibleTrigger(
                modifier = Modifier.size(32.dp),
                style = ButtonVariant.Ghost.rememberStyle() then ButtonSize.Icon.rememberStyle(),
            ) {
                Image(
                    Lucide.ChevronsUpDown,
                    "Toggle details",
                    Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(currentTheme.colors.foreground),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(
            Modifier.fillMaxWidth().border(
                1.dp,
                currentTheme.colors.border,
                RoundedCornerShape(6.dp),
            ).padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            SurfaceText("Status", muted = true)
            SurfaceText("Shipped", medium = true)
        }
        CollapsibleContent {
            listOf("Shipping address" to "100 Market St, San Francisco", "Items" to "2x Studio Headphones").forEach {
                    (title, text) ->
                Column(
                    Modifier.fillMaxWidth().padding(
                        top = 8.dp,
                    ).border(
                        1.dp,
                        currentTheme.colors.border,
                        RoundedCornerShape(6.dp),
                    ).padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    SurfaceText(title, medium = true)
                    SurfaceText(text, muted = true)
                }
            }
        }
    }
}

@Composable
internal fun CommandParityExample() {
    var selection by remember { mutableStateOf("") }
    Column(Modifier.widthIn(max = 384.dp)) {
        Command(modifier = Modifier.fillMaxWidth()) {
            CommandInput()
            CommandList {
                CommandEmpty()
                CommandGroup(heading = "Suggestions") {
                    CommandExampleItem("Calendar", Lucide.Calendar, onSelect = { selection = "Calendar" })
                    CommandExampleItem("Search Emoji", Lucide.Smile, onSelect = { selection = "Search Emoji" })
                    CommandExampleItem("Calculator", Lucide.Calculator, disabled = true, onSelect = {})
                }
                CommandSeparator()
                CommandGroup(heading = "Settings") {
                    CommandExampleItem("Profile", Lucide.User, "⌘P", onSelect = { selection = "Profile" })
                    CommandExampleItem("Billing", Lucide.CreditCard, "⌘B", onSelect = { selection = "Billing" })
                    CommandExampleItem("Settings", Lucide.Settings, "⌘S", onSelect = { selection = "Settings" })
                }
            }
        }
        if (selection.isNotEmpty()) SurfaceText("Selected: $selection", muted = true)
    }
}

@Composable
private fun CommandExampleItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    shortcut: String? = null,
    disabled: Boolean = false,
    onSelect: () -> Unit,
) {
    CommandItem(label, onSelect, disabled = disabled) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Image(
                icon,
                null,
                Modifier.size(16.dp),
                colorFilter =
                    ColorFilter.tint(
                        if (disabled) currentTheme.colors.mutedForeground else currentTheme.colors.foreground,
                    ),
            )
            Text(Modifier.weight(1f)) { label }
            if (shortcut != null) CommandShortcut(shortcut)
        }
    }
}

@Composable
internal fun DialogParityExample() {
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
                if (mobile) {
                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            "Save changes",
                            onClick = { open = false },
                            modifier = Modifier.fillMaxWidth(),
                            style = Style { width(1f) },
                        )
                        Button(
                            "Cancel",
                            onClick = {
                                open = false
                            },
                            variant = ButtonVariant.Outline,
                            modifier = Modifier.fillMaxWidth(),
                            style =
                                Style {
                                    width(
                                        1f,
                                    )
                                },
                        )
                    }
                } else {
                    Button("Cancel", onClick = { open = false }, variant = ButtonVariant.Outline)
                    Button("Save changes", onClick = { open = false })
                }
            }
        }
    }
}

private val deliveryTimes =
    listOf(
        Triple("asap", "Standard delivery", "25–35 min · Driver assigned now"),
        Triple("5-00", "5:00 PM – 5:15 PM", "Prep starts at 4:45 PM"),
        Triple("5-30", "5:30 PM – 5:45 PM", "Good if you're heading home"),
        Triple("6-00", "6:00 PM – 6:15 PM", "Most popular · High demand"),
        Triple("6-30", "6:30 PM – 6:45 PM", "Last slot before kitchen closes"),
    )

@Composable
internal fun DrawerNestedParityExample() {
    val mobile = LocalWindowInfo.current.containerSize.width < with(LocalDensity.current) { 768.dp.roundToPx() }
    DrawerNestedLevel(1, mobile)
}

@Composable
private fun DrawerNestedLevel(
    level: Int,
    mobile: Boolean,
) {
    Drawer(
        showSwipeHandle = mobile,
        swipeDirection = if (mobile) DrawerSwipeDirection.Down else DrawerSwipeDirection.Right,
    ) {
        DrawerTrigger(
            when (level) {
                1 -> "Open Drawer"
                2 -> "Open Nested Drawer"
                3 -> "Open Third Drawer"
                else -> "Open Fourth Drawer"
            },
            modifier = if (level > 1) Modifier.fillMaxWidth() else Modifier,
            variant = ButtonVariant.Outline,
        )
        DrawerContent {
            DrawerHeader(
                when (level) {
                    1 -> "Drawer"
                    2 -> "Nested Drawer"
                    3 -> "Third Drawer"
                    else -> "Fourth Drawer"
                },
                "Open another drawer from the same direction.",
            )
            Box(Modifier.weight(1f, fill = !mobile).padding(16.dp)) {
                Box(
                    Modifier.fillMaxWidth().then(
                        if (mobile) Modifier.aspectRatio(16f / 9f) else Modifier.fillMaxHeight(),
                    )
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

@Composable
internal fun DrawerLargeContentExample() {
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
                        SurfaceText("Day ${index + 1}", medium = true)
                        SurfaceText("${6200 + index * 180} steps · ${24 + index} active minutes", muted = true)
                    }
                }
            }
            DrawerFooterColumn(Modifier.fillMaxWidth().background(currentTheme.colors.popover)) {
                Button("Done", onClick = close, modifier = Modifier.fillMaxWidth(), style = Style { width(1f) })
            }
        }
    }
}

@Composable
internal fun DrawerParityExample() {
    var open by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf("asap") }
    val toasts = rememberToastState()
    val mobile = LocalWindowInfo.current.containerSize.width < with(LocalDensity.current) { 768.dp.roundToPx() }
    Box(contentAlignment = Alignment.TopCenter) {
        Drawer(
            open = open,
            onOpenChange = {
                open = it
            },
            showSwipeHandle =
            mobile,
            swipeDirection =
                if (mobile) {
                    DrawerSwipeDirection.Down
                } else {
                    DrawerSwipeDirection.Right
                },
        ) {
            DrawerTrigger("Open Drawer", variant = ButtonVariant.Secondary)
            DrawerContent {
                DrawerHeader("Pick a delivery time", "We'll prepare your order as soon as possible.")
                Column(
                    Modifier.weight(
                        1f,
                        fill = !mobile,
                    ).clipToBounds().verticalScroll(rememberScrollState()).padding(16.dp),
                ) {
                    RadioGroup(value = selected, onValueChange = { selected = it }) {
                        deliveryTimes.forEach { (value, label, description) ->
                            Row(
                                Modifier.fillMaxWidth().pointerInput(value) {
                                    detectTapGestures(onTap = {
                                        selected = value
                                    })
                                }.background(
                                    if (selected == value) {
                                        currentTheme.colors.muted.copy(
                                            alpha = 0.3f,
                                        )
                                    } else {
                                        androidx.compose.ui.graphics.Color.Transparent
                                    },
                                    RoundedCornerShape(twRadius.lg),
                                ).border(
                                    1.dp,
                                    currentTheme.colors.border,
                                    RoundedCornerShape(twRadius.lg),
                                ).padding(
                                    16.dp,
                                ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement =
                                    Arrangement.spacedBy(
                                        12.dp,
                                    ),
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
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
                    Button(
                        "Confirm Delivery Time",
                        style =
                            Style {
                                width(1f)
                                height(34.dp)
                            },
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            val label = deliveryTimes.first { it.first == selected }.second
                            open = false
                            toasts.show("Delivery time confirmed", description = label)
                        },
                    )
                    Button(
                        "Cancel",
                        style =
                            Style {
                                width(1f)
                            },
                        modifier = Modifier.fillMaxWidth(),
                        variant = ButtonVariant.Outline,
                        onClick = { open = false },
                    )
                }
            }
        }
        Toaster(state = toasts, modifier = Modifier.align(Alignment.BottomEnd), position = ToastPosition.BottomEnd)
    }
}

@Composable
internal fun DatePickerParityExample() {
    var open by remember { mutableStateOf(false) }
    val locale = LocalCalendarLocale.current
    val today =
        remember {
            currentCalendarDate()
        }
    var year by remember { mutableStateOf(today.year) }
    var month by remember { mutableStateOf(today.month) }
    var selected by remember { mutableStateOf<CalendarDate?>(null) }
    Popover(open = open, onOpenChange = { open = it }) {
        PopoverTrigger {
            Button(onClick = toggle, variant = ButtonVariant.Outline, modifier = Modifier.width(212.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        Modifier.weight(1f),
                        textStyle =
                            if (selected == null) {
                                TextColors.Muted
                            } else {
                                androidx.compose.ui.text.TextStyle.Default
                            },
                    ) {
                        selected?.let(locale.formatDate) ?: locale.pickDate
                    }
                    Image(
                        Lucide.ChevronDown,
                        null,
                        Modifier.size(16.dp),
                        colorFilter = ColorFilter.tint(currentTheme.colors.mutedForeground),
                    )
                }
            }
        }
        PopoverContent(
            align = PopupAlign.Start,
            style =
                Style {
                    width(214.dp)
                    contentPadding(0.dp)
                },
        ) {
            CompositionLocalProvider(LocalCalendarAutoFocus provides true) {
                Calendar(
                    year = year,
                    month = month,
                    onMonthChange = { y, m ->
                        year = y
                        month = m
                    },
                    selected = selected,
                    today = today,
                    fixedWeeks = false,
                    onSelectedChange = { selected = it },
                )
            }
        }
    }
}

@Composable
internal fun ContextMenuParityExample() {
    var bookmarks by remember { mutableStateOf(true) }
    var urls by remember { mutableStateOf(false) }
    var person by remember { mutableStateOf("pedro") }
    ContextMenu {
        ContextMenuTrigger {
            Box(
                Modifier.widthIn(max = 320.dp).fillMaxWidth().aspectRatio(16f / 9f),
                contentAlignment = Alignment.Center,
            ) {
                val border = currentTheme.colors.border
                Canvas(Modifier.matchParentSize()) {
                    drawRoundRect(
                        border,
                        cornerRadius = CornerRadius(12.dp.toPx()),
                        style =
                            androidx.compose.ui.graphics.drawscope.Stroke(
                                1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx())),
                            ),
                    )
                }
                SurfaceText("Right click here")
            }
        }
        ContextMenuContent {
            ContextMenuItem("Back", onClick = {}, shortcut = "⌘[")
            ContextMenuItem("Forward", onClick = {}, disabled = true, shortcut = "⌘]")
            ContextMenuItem("Reload", onClick = {}, shortcut = "⌘R")
            ContextMenuSub {
                ContextMenuSubTrigger("More Tools")
                ContextMenuSubContent {
                    ContextMenuItem("Save Page...", onClick = {})
                    ContextMenuItem("Create Shortcut...", onClick = {})
                    ContextMenuItem("Name Window...", onClick = {})
                    ContextMenuSeparator()
                    ContextMenuItem("Developer Tools", onClick = {})
                    ContextMenuSeparator()
                    ContextMenuItem("Delete", onClick = {}, destructive = true)
                }
            }
            ContextMenuSeparator()
            ContextMenuCheckboxItem("Show Bookmarks", checked = bookmarks, onCheckedChange = { bookmarks = it })
            ContextMenuCheckboxItem("Show Full URLs", checked = urls, onCheckedChange = { urls = it })
            ContextMenuSeparator()
            ContextMenuLabel("People")
            ContextMenuRadioGroup(value = person, onValueChange = { person = it }) {
                ContextMenuRadioItem("pedro", "Pedro Duarte")
                ContextMenuRadioItem("colm", "Colm Tuite")
            }
        }
    }
}
