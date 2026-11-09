@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuScope
import com.toutiaoyan.compose.ui.components.tooltip.Tooltip
import com.toutiaoyan.compose.ui.components.tooltip.TooltipTrigger
import com.toutiaoyan.compose.ui.components.tooltip.TooltipContent
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.graphics.Color
import com.toutiaoyan.compose.tailwind.core.TwColors
import com.toutiaoyan.compose.ui.theme.currentTheme
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ArrowRight
import com.composables.icons.lucide.AudioLines
import com.composables.icons.lucide.Bot
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Minus
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.VolumeOff
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.TriangleAlert
import com.composables.icons.lucide.UserRoundX
import com.composables.icons.lucide.Share
import com.composables.icons.lucide.Copy
import com.composables.icons.lucide.Trash
import com.composables.icons.lucide.Search
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.buttongroup.*
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenu
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuContent
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuGroup
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuItem
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuTrigger
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuSeparator
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.field.FieldDescription
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.inputgroup.*
import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
import com.toutiaoyan.compose.ui.components.popover.*
import com.toutiaoyan.compose.ui.components.select.*
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.textarea.Textarea

private val buttonGroupOrientationCode =
    """
    ButtonGroup(orientation = ButtonGroupOrientation.Vertical, contentDescription = "Media controls") {
        ButtonGroupItem(onClick = {}, variant = ButtonVariant.Outline, size = ButtonSize.Icon) { Icon(Lucide.Plus) }
        ButtonGroupItem(onClick = {}, variant = ButtonVariant.Outline, size = ButtonSize.Icon) { Icon(Lucide.Minus) }
    }
    """.trimIndent()

private val buttonGroupSizeCode =
    """
    Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {
        listOf(ButtonSize.Sm to "Small", ButtonSize.Md to "Default", ButtonSize.Lg to "Large").forEach { (size, label) ->
            ButtonGroup {
                ButtonGroupItem(label, onClick = {}, variant = ButtonVariant.Outline, size = size)
                ButtonGroupItem("Button", onClick = {}, variant = ButtonVariant.Outline, size = size)
                ButtonGroupItem("Group", onClick = {}, variant = ButtonVariant.Outline, size = size)
                ButtonGroupItem(onClick = {}, variant = ButtonVariant.Outline, size = size) { Icon(Lucide.Plus) }
            }
        }
    }
    """.trimIndent()

private val buttonGroupNestedCode =
    """
    var message by remember { mutableStateOf("") }
    ButtonGroup(spacing = 8.dp) {
        ButtonGroup {
            ButtonGroupItem(onClick = {}, variant = ButtonVariant.Outline, size = ButtonSize.Icon) {
                Icon(Lucide.Plus)
            }
        }
        ButtonGroup {
            InputGroup(Modifier.width(280.dp)) {
                InputGroupInput(message, { message = it }, placeholder = "Send a message...")
                InputGroupAddon(InputGroupAddonAlign.InlineEnd) {
                    Tooltip {
                        TooltipTrigger {
                            InputGroupButton(
                                onClick = {},
                                size = InputGroupButtonSize.IconXs,
                                label = "Voice Mode",
                            ) { Icon(Lucide.AudioLines) }
                        }
                        TooltipContent("Voice Mode")
                    }
                }
            }
        }
    }
    """.trimIndent()

private val buttonGroupSeparatorCode =
    """
    ButtonGroup {
        ButtonGroupItem("Copy", onClick = {}, variant = ButtonVariant.Secondary, size = ButtonSize.Sm)
        ButtonGroupSeparator()
        ButtonGroupItem("Paste", onClick = {}, variant = ButtonVariant.Secondary, size = ButtonSize.Sm)
    }
    """.trimIndent()

private val buttonGroupSplitCode =
    """
    ButtonGroup {
        ButtonGroupItem("Button", onClick = {}, variant = ButtonVariant.Secondary)
        ButtonGroupSeparator()
        ButtonGroupItem(onClick = {}, variant = ButtonVariant.Secondary, size = ButtonSize.Icon) { Icon(Lucide.Plus) }
    }
    """.trimIndent()

private val buttonGroupInputCode =
    """
    var query by remember { mutableStateOf("") }
    val radius = twRadius.lg
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val fieldWidth = (maxWidth - 32.dp).coerceAtLeast(0.dp)
        ButtonGroup {
            Input(
                query,
                { query = it },
                Modifier.width(fieldWidth),
                placeholder = "Search...",
                style = Style { shape(RoundedCornerShape(radius, 0.dp, 0.dp, radius)) },
            )
            ButtonGroupItem(
                onClick = {},
                variant = ButtonVariant.Outline,
                size = ButtonSize.Icon,
                modifier = Modifier.semantics { contentDescription = "Search" },
                style = Style { shape(RoundedCornerShape(0.dp, radius, radius, 0.dp)) },
            ) { Icon(Lucide.Search) }
        }
    }
    """.trimIndent()

private val buttonGroupInputGroupCode =
    """
    var message by remember { mutableStateOf("") }
    var voiceEnabled by remember { mutableStateOf(false) }
    val pill = RoundedCornerShape(percent = 50)
    val dark = currentTheme.isDark
    val voiceBackground = if (dark) TwColors.orange800 else TwColors.orange100
    val voiceForeground = if (dark) TwColors.orange100 else TwColors.orange700
    ButtonGroup(spacing = 8.dp) {
        ButtonGroup {
            ButtonGroupItem(
                onClick = {},
                variant = ButtonVariant.Outline,
                size = ButtonSize.Icon,
                style = Style { shape(pill) },
            ) { Icon(Lucide.Plus) }
        }
        ButtonGroup {
            InputGroup(Modifier.width(280.dp), style = Style { shape(pill) }) {
                InputGroupInput(
                    message,
                    { message = it },
                    enabled = !voiceEnabled,
                    placeholder = if (voiceEnabled) "Record and send audio..." else "Send a message...",
                )
                InputGroupAddon(InputGroupAddonAlign.InlineEnd) {
                    Tooltip {
                        TooltipTrigger {
                            InputGroupButton(
                                onClick = { voiceEnabled = !voiceEnabled },
                                size = InputGroupButtonSize.IconXs,
                                label = "Voice Mode",
                                modifier =
                                    Modifier.semantics {
                                        toggleableState = if (voiceEnabled) ToggleableState.On else ToggleableState.Off
                                    },
                                style =
                                    Style {
                                        shape(pill)
                                        if (voiceEnabled) background(voiceBackground)
                                    },
                            ) {
                                Icon(Lucide.AudioLines, tint = if (voiceEnabled) voiceForeground else Color.Unspecified)
                            }
                        }
                        TooltipContent("Voice Mode")
                    }
                }
            }
        }
    }
    """.trimIndent()

private val buttonGroupDropdownCode =
    """
    ButtonGroup {
        ButtonGroupItem("Follow", onClick = {}, variant = ButtonVariant.Outline)
        DropdownMenu {
            DropdownMenuTrigger {
                ButtonGroupItem(
                    onClick = toggle,
                    variant = ButtonVariant.Outline,
                    size = ButtonSize.Icon,
                    modifier = Modifier.semantics { contentDescription = "Open options" },
                ) {
                    Icon(Lucide.ChevronDown)
                }
            }
            DropdownMenuContent(align = PopupAlign.End, style = Style { width(176.dp) }) {
                DropdownMenuGroup {
                    ButtonGroupDropdownItem("Mute Conversation", Lucide.VolumeOff)
                    ButtonGroupDropdownItem("Mark as Read", Lucide.Check)
                    ButtonGroupDropdownItem("Report Conversation", Lucide.TriangleAlert)
                    ButtonGroupDropdownItem("Block User", Lucide.UserRoundX)
                    ButtonGroupDropdownItem("Share Conversation", Lucide.Share)
                    ButtonGroupDropdownItem("Copy Conversation", Lucide.Copy)
                }
                DropdownMenuSeparator()
                DropdownMenuGroup {
                    ButtonGroupDropdownItem("Delete Conversation", Lucide.Trash, destructive = true)
                }
            }
        }
    }

    @Composable
    private fun DropdownMenuScope.ButtonGroupDropdownItem(
        label: String,
        icon: ImageVector,
        destructive: Boolean = false,
    ) {
        val theme = currentTheme
        val textColor = if (destructive) theme.colors.destructive else theme.colors.popoverForeground
        val iconColor = if (destructive) theme.colors.destructive else theme.colors.mutedForeground
        DropdownMenuItem(label, onClick = {}, destructive = destructive) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(icon, modifier = Modifier.size(16.dp), tint = iconColor)
                Text(
                    textStyle = theme.typography.bodyMedium.copy(color = textColor, fontSize = 14.sp, lineHeight = 20.sp),
                ) {
                    label
                }
            }
        }
    }
    """.trimIndent()

private val buttonGroupSelectCode =
    """
    var currency by remember { mutableStateOf("$") }
    var amount by remember { mutableStateOf("") }
    ButtonGroup(spacing = 8.dp) {
        ButtonGroup {
            Select(value = currency, onValueChange = { currency = it }) {
                SelectTrigger(Modifier.width(72.dp)) { Text { currency } }
                SelectContent(alignItemWithTrigger = false, align = PopupAlign.Start) {
                    SelectGroup {
                        listOf("$" to "US Dollar", "€" to "Euro", "£" to "British Pound").forEach { (value, name) ->
                            SelectItem(value, textValue = "${'$'}value ${'$'}name") {
                                Text { value }
                                Text(
                                    textStyle =
                                        currentTheme.typography.bodyMedium.copy(
                                            color = currentTheme.colors.mutedForeground,
                                        ),
                                ) { name }
                            }
                        }
                    }
                }
            }
            Input(amount, { amount = it }, Modifier.width(120.dp), placeholder = "10.00")
        }
        ButtonGroup {
            ButtonGroupItem(
                onClick = {},
                variant = ButtonVariant.Outline,
                size = ButtonSize.Icon,
                modifier = Modifier.semantics { contentDescription = "Send" },
            ) { Icon(Lucide.ArrowRight) }
        }
    }
    """.trimIndent()

private val buttonGroupPopoverCode =
    """
    var description by remember { mutableStateOf("") }
    ButtonGroup {
        ButtonGroupItem(onClick = {}, variant = ButtonVariant.Outline) {
            Icon(Lucide.Bot)
            Text { "Copilot" }
        }
        Popover {
            PopoverTrigger(modifier = Modifier.semantics { contentDescription = "Open Popover" }) {
                ButtonGroupItem(
                    onClick = toggle,
                    variant = ButtonVariant.Outline,
                    size = ButtonSize.Icon,
                ) { Icon(Lucide.ChevronDown) }
            }
            PopoverContent(
                align = PopupAlign.End,
                style = Style { shape(RoundedCornerShape(12.dp)); contentPadding(16.dp) },
            ) {
                PopoverHeader(
                    "Start a new task with Copilot",
                    "Describe your task in natural language.",
                )
                Field {
                    Textarea(
                        description,
                        { description = it },
                        placeholder = "I need to...",
                        modifier = Modifier.semantics { contentDescription = "Task Description" },
                    )
                    FieldDescription("Copilot will open a pull request for review.")
                }
            }
        }
    }
    """.trimIndent()

private val buttonGroupRtlCode =
    """
    var language by remember { mutableStateOf(0) }
    RtlPreview(language, listOf("Arabic (العربية)", "English", "Hebrew (עברית)"), { language = it }) {
        CompositionLocalProvider(
            LocalLayoutDirection provides if (language == 1) LayoutDirection.Ltr else LayoutDirection.Rtl,
        ) {
            ButtonGroupDefaultRecipe(language)
        }
    }
    """.trimIndent()

val buttonGroupDoc =
    ComponentDoc(
        id = "button-group",
        title = "Button Group",
        description = "A container that groups related buttons together with consistent styling.",
        examplesHeading = null,
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.buttongroup.ButtonGroup
            import com.toutiaoyan.compose.ui.components.buttongroup.ButtonGroupItem

            ButtonGroup(contentDescription = "Button group") {
                ButtonGroupItem("Archive", onClick = {})
                ButtonGroupItem("Report", onClick = {})
                ButtonGroupItem("Snooze", onClick = {})
            }
            """.trimIndent(),
        compositionCode =
            """
            ButtonGroup
            ├── Button or Input
            ├── ButtonGroupSeparator
            └── ButtonGroupText
            """.trimIndent(),
        beforeExamples =
            listOf(
                ComponentGuideSection(
                    title = "Accessibility",
                    blocks =
                        listOf(
                            ComponentGuideBlock.Paragraph(
                                "Compose exposes the group as one traversal group with an optional " +
                                    "accessible label. Use `contentDescription` as the Kotlin equivalent of " +
                                    "`aria-label`, and keep Tab navigation between the group's buttons.",
                            ),
                            ComponentGuideBlock.Bullets(
                                listOf(
                                    "The web `role=\"group\"` has no direct Canvas role; use the traversal " +
                                        "group plus a descriptive label.",
                                    "Use `contentDescription` for the group label.",
                                    "Related actions should remain button semantics, not toggle semantics.",
                                ),
                            ),
                            ComponentGuideBlock.Code(
                                """
                                ButtonGroup(contentDescription = "Button group") {
                                    ButtonGroupItem("Button 1", onClick = {})
                                    ButtonGroupItem("Button 2", onClick = {})
                                }
                                """.trimIndent(),
                            ),
                        ),
                ),
                ComponentGuideSection(
                    title = "ButtonGroup vs ToggleGroup",
                    blocks =
                        listOf(
                            ComponentGuideBlock.Bullets(
                                listOf(
                                    "Use ButtonGroup when the controls perform an action.",
                                    "Use ToggleGroup when the controls toggle a state.",
                                ),
                            ),
                        ),
                ),
            ),
        examples =
            listOf(
                ComponentExample("Default", buttonGroupDefaultCode, preview = { ButtonGroupDefaultRecipe() }),
                ComponentExample(
                    "Orientation",
                    buttonGroupOrientationCode,
                    descriptionMarkdown = "Set the `orientation` parameter to change the button group layout.",
                    preview = { ButtonGroupOrientationRecipe() },
                ),
                ComponentExample(
                    "Size",
                    buttonGroupSizeCode,
                    descriptionMarkdown = "Control the size of buttons using the `size` parameter.",
                    preview = { ButtonGroupSizeRecipe() },
                ),
                ComponentExample(
                    "Nested",
                    buttonGroupNestedCode,
                    descriptionMarkdown = "Nest ButtonGroups to create groups with spacing.",
                    preview = { ButtonGroupNestedRecipe() },
                ),
                ComponentExample(
                    "Separator",
                    buttonGroupSeparatorCode,
                    descriptionMarkdown =
                        "ButtonGroupSeparator visually divides buttons within a group. " +
                            "Outline buttons already have a border; use a separator for other variants.",
                    preview = { ButtonGroupSeparatorRecipe() },
                ),
                ComponentExample(
                    "Split",
                    buttonGroupSplitCode,
                    descriptionMarkdown =
                        "Create a split button group with two buttons separated by ButtonGroupSeparator.",
                    preview = { ButtonGroupSplitRecipe() },
                ),
                ComponentExample(
                    "Input",
                    buttonGroupInputCode,
                    descriptionMarkdown = "Wrap an Input component with buttons.",
                    preview = { ButtonGroupInputRecipe() },
                ),
                ComponentExample(
                    "Input Group",
                    buttonGroupInputGroupCode,
                    descriptionMarkdown = "Wrap an InputGroup component to create complex input layouts.",
                    preview = { ButtonGroupInputGroupRecipe() },
                ),
                ComponentExample(
                    "Dropdown Menu",
                    buttonGroupDropdownCode,
                    descriptionMarkdown = "Create a split button group with a DropdownMenu component.",
                    preview = { ButtonGroupDropdownRecipe() },
                ),
                ComponentExample(
                    "Select",
                    buttonGroupSelectCode,
                    descriptionMarkdown = "Pair with a Select component.",
                    preview = { ButtonGroupSelectRecipe() },
                ),
                ComponentExample(
                    "Popover",
                    buttonGroupPopoverCode,
                    descriptionMarkdown = "Use with a Popover component.",
                    preview = { ButtonGroupPopoverRecipe() },
                ),
                ComponentExample(
                    "RTL",
                    buttonGroupRtlCode,
                    descriptionMarkdown =
                        "To enable RTL support in shadcn/ui, see the " +
                            "[RTL configuration guide](https://ui.shadcn.com/docs/rtl).",
                    previewPadding = 0.dp,
                    preview = { ButtonGroupRtlRecipe() },
                ),
            ),
        apiSections =
            listOf(
                ComponentApiSection(
                    title = "ButtonGroup",
                    descriptionMarkdown =
                        "Container that groups related buttons together with consistent styling.",
                    properties =
                        listOf(
                            ApiProperty("orientation", "ButtonGroupOrientation", "Horizontal"),
                            ApiProperty("contentDescription", "String?", "null"),
                            ApiProperty("spacing", "Dp", "0.dp"),
                            ApiProperty("disabled", "Boolean", "false"),
                            ApiProperty("style", "Style", "Style"),
                            ApiProperty("content", "@Composable ButtonGroupScope.() -> Unit", "Required"),
                        ),
                ),
                ComponentApiSection(
                    title = "ButtonGroupSeparator",
                    descriptionMarkdown =
                        "Visually divides buttons within a group. The separator is perpendicular to the " +
                            "enclosing ButtonGroup orientation; no separate orientation parameter is needed.",
                    properties =
                        listOf(
                            ApiProperty("modifier", "Modifier", "Modifier"),
                            ApiProperty("style", "Style", "Style"),
                        ),
                ),
                ComponentApiSection(
                    title = "ButtonGroupText",
                    descriptionMarkdown = "Displays text inside a button group.",
                    properties =
                        listOf(
                            ApiProperty("text", "String", "Required"),
                            ApiProperty("modifier", "Modifier", "Modifier"),
                            ApiProperty("style", "Style", "Style"),
                        ),
                ),
            ),
    )

@Composable
private fun ButtonGroupOrientationRecipe() {
    ButtonGroup(orientation = ButtonGroupOrientation.Vertical, contentDescription = "Media controls") {
        ButtonGroupItem(onClick = {}, variant = ButtonVariant.Outline, size = ButtonSize.Icon) { Icon(Lucide.Plus) }
        ButtonGroupItem(onClick = {}, variant = ButtonVariant.Outline, size = ButtonSize.Icon) { Icon(Lucide.Minus) }
    }
}

@Composable
private fun ButtonGroupSizeRecipe() {
    Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {
        listOf(ButtonSize.Sm to "Small", ButtonSize.Md to "Default", ButtonSize.Lg to "Large").forEach {
                (size, label) ->
            ButtonGroup {
                ButtonGroupItem(label, onClick = {}, variant = ButtonVariant.Outline, size = size)
                ButtonGroupItem("Button", onClick = {}, variant = ButtonVariant.Outline, size = size)
                ButtonGroupItem("Group", onClick = {}, variant = ButtonVariant.Outline, size = size)
                ButtonGroupItem(onClick = {}, variant = ButtonVariant.Outline, size = size) { Icon(Lucide.Plus) }
            }
        }
    }
}

@Composable
private fun ButtonGroupNestedRecipe() {
    var message by remember { mutableStateOf("") }
    ButtonGroup(spacing = 8.dp) {
        ButtonGroup(modifier = Modifier.testTag("button-group-leading")) {
            ButtonGroupItem(onClick = {}, variant = ButtonVariant.Outline, size = ButtonSize.Icon) {
                Icon(Lucide.Plus)
            }
        }
        ButtonGroup(modifier = Modifier.testTag("button-group-trailing")) {
            InputGroup(Modifier.width(280.dp)) {
                InputGroupInput(message, { message = it }, placeholder = "Send a message...")
                InputGroupAddon(InputGroupAddonAlign.InlineEnd) {
                    Tooltip {
                        TooltipTrigger {
                            InputGroupButton(
                                onClick = {},
                                size = InputGroupButtonSize.IconXs,
                                label = "Voice Mode",
                            ) { Icon(Lucide.AudioLines) }
                        }
                        TooltipContent("Voice Mode")
                    }
                }
            }
        }
    }
}

@Composable
private fun ButtonGroupSeparatorRecipe() {
    ButtonGroup {
        ButtonGroupItem("Copy", onClick = {}, variant = ButtonVariant.Secondary, size = ButtonSize.Sm)
        ButtonGroupSeparator()
        ButtonGroupItem("Paste", onClick = {}, variant = ButtonVariant.Secondary, size = ButtonSize.Sm)
    }
}

@Composable
private fun ButtonGroupSplitRecipe() {
    ButtonGroup {
        ButtonGroupItem("Button", onClick = {}, variant = ButtonVariant.Secondary)
        ButtonGroupSeparator()
        ButtonGroupItem(onClick = {}, variant = ButtonVariant.Secondary, size = ButtonSize.Icon) { Icon(Lucide.Plus) }
    }
}

@Composable
private fun ButtonGroupInputRecipe() {
    var query by remember { mutableStateOf("") }
    val radius = twRadius.lg
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val fieldWidth = (maxWidth - 32.dp).coerceAtLeast(0.dp)
        ButtonGroup {
            Input(
                query,
                { query = it },
                Modifier.width(fieldWidth),
                placeholder = "Search...",
                style = Style { shape(RoundedCornerShape(radius, 0.dp, 0.dp, radius)) },
            )
            ButtonGroupItem(
                onClick = {},
                variant = ButtonVariant.Outline,
                size = ButtonSize.Icon,
                modifier = Modifier.semantics { contentDescription = "Search" },
                style = Style { shape(RoundedCornerShape(0.dp, radius, radius, 0.dp)) },
            ) { Icon(Lucide.Search) }
        }
    }
}

@Composable
private fun ButtonGroupInputGroupRecipe() {
    var message by remember { mutableStateOf("") }
    var voiceEnabled by remember { mutableStateOf(false) }
    val pill = RoundedCornerShape(percent = 50)
    val dark = currentTheme.isDark
    val voiceBackground = if (dark) TwColors.orange800 else TwColors.orange100
    val voiceForeground = if (dark) TwColors.orange100 else TwColors.orange700
    ButtonGroup(spacing = 8.dp) {
        ButtonGroup(modifier = Modifier.testTag("button-group-leading")) {
            ButtonGroupItem(
                onClick = {},
                variant = ButtonVariant.Outline,
                size = ButtonSize.Icon,
                modifier = Modifier.testTag("button-group-pill-leading"),
                style = Style { shape(pill) },
            ) { Icon(Lucide.Plus) }
        }
        ButtonGroup(modifier = Modifier.testTag("button-group-trailing")) {
            InputGroup(Modifier.width(280.dp), style = Style { shape(pill) }) {
                InputGroupInput(
                    message,
                    { message = it },
                    enabled = !voiceEnabled,
                    placeholder = if (voiceEnabled) "Record and send audio..." else "Send a message...",
                )
                InputGroupAddon(InputGroupAddonAlign.InlineEnd) {
                    Tooltip {
                        TooltipTrigger {
                            InputGroupButton(
                                onClick = { voiceEnabled = !voiceEnabled },
                                size = InputGroupButtonSize.IconXs,
                                label = "Voice Mode",
                                modifier =
                                    Modifier.semantics {
                                        toggleableState = if (voiceEnabled) ToggleableState.On else ToggleableState.Off
                                    },
                                style =
                                    Style {
                                        shape(pill)
                                        if (voiceEnabled) background(voiceBackground)
                                    },
                            ) {
                                Icon(Lucide.AudioLines, tint = if (voiceEnabled) voiceForeground else Color.Unspecified)
                            }
                        }
                        TooltipContent("Voice Mode")
                    }
                }
            }
        }
    }
}

@Composable
private fun ButtonGroupDropdownRecipe() {
    ButtonGroup {
        ButtonGroupItem("Follow", onClick = {}, variant = ButtonVariant.Outline)
        DropdownMenu {
            DropdownMenuTrigger {
                ButtonGroupItem(
                    onClick = toggle,
                    variant = ButtonVariant.Outline,
                    size = ButtonSize.Icon,
                    modifier = Modifier.semantics { contentDescription = "Open options" },
                ) {
                    Icon(Lucide.ChevronDown)
                }
            }
            DropdownMenuContent(align = PopupAlign.End, style = Style { width(176.dp) }) {
                DropdownMenuGroup {
                    ButtonGroupDropdownItem("Mute Conversation", Lucide.VolumeOff)
                    ButtonGroupDropdownItem("Mark as Read", Lucide.Check)
                    ButtonGroupDropdownItem("Report Conversation", Lucide.TriangleAlert)
                    ButtonGroupDropdownItem("Block User", Lucide.UserRoundX)
                    ButtonGroupDropdownItem("Share Conversation", Lucide.Share)
                    ButtonGroupDropdownItem("Copy Conversation", Lucide.Copy)
                }
                DropdownMenuSeparator()
                DropdownMenuGroup {
                    ButtonGroupDropdownItem("Delete Conversation", Lucide.Trash, destructive = true)
                }
            }
        }
    }
}

@Composable
private fun ButtonGroupSelectRecipe() {
    var currency by remember { mutableStateOf("$") }
    var amount by remember { mutableStateOf("") }
    ButtonGroup(spacing = 8.dp) {
        ButtonGroup(modifier = Modifier.testTag("button-group-leading")) {
            Select(value = currency, onValueChange = { currency = it }) {
                SelectTrigger(Modifier.width(72.dp)) { Text { currency } }
                SelectContent(alignItemWithTrigger = false, align = PopupAlign.Start) {
                    SelectGroup {
                        listOf("$" to "US Dollar", "€" to "Euro", "£" to "British Pound").forEach { (value, name) ->
                            SelectItem(value, textValue = "$value $name") {
                                Text { value }
                                Text(
                                    textStyle =
                                        currentTheme.typography.bodyMedium.copy(
                                            color = currentTheme.colors.mutedForeground,
                                        ),
                                ) { name }
                            }
                        }
                    }
                }
            }
            Input(amount, { amount = it }, Modifier.width(120.dp), placeholder = "10.00")
        }
        ButtonGroup(modifier = Modifier.testTag("button-group-trailing")) {
            ButtonGroupItem(
                onClick = {},
                variant = ButtonVariant.Outline,
                size = ButtonSize.Icon,
                modifier = Modifier.semantics { contentDescription = "Send" },
            ) { Icon(Lucide.ArrowRight) }
        }
    }
}

@Composable
private fun ButtonGroupPopoverRecipe() {
    var description by remember { mutableStateOf("") }
    ButtonGroup {
        ButtonGroupItem(onClick = {}, variant = ButtonVariant.Outline) {
            Icon(Lucide.Bot)
            Text { "Copilot" }
        }
        Popover {
            PopoverTrigger(
                modifier = Modifier.semantics { contentDescription = "Open Popover" },
            ) {
                ButtonGroupItem(
                    onClick = toggle,
                    variant = ButtonVariant.Outline,
                    size = ButtonSize.Icon,
                ) { Icon(Lucide.ChevronDown) }
            }
            PopoverContent(
                align = PopupAlign.End,
                style =
                    Style {
                        shape(RoundedCornerShape(12.dp))
                        contentPadding(16.dp)
                    },
            ) {
                PopoverHeader(
                    "Start a new task with Copilot",
                    "Describe your task in natural language.",
                )
                Field {
                    Textarea(
                        description,
                        { description = it },
                        placeholder = "I need to...",
                        modifier = Modifier.semantics { contentDescription = "Task Description" },
                    )
                    FieldDescription("Copilot will open a pull request for review.")
                }
            }
        }
    }
}

@Composable
private fun ButtonGroupRtlRecipe() {
    var language by remember { mutableStateOf(0) }
    RtlPreview(
        language,
        listOf("Arabic (العربية)", "English", "Hebrew (עברית)"),
        { language = it },
    ) {
        CompositionLocalProvider(
            LocalLayoutDirection provides if (language == 1) LayoutDirection.Ltr else LayoutDirection.Rtl,
        ) {
            ButtonGroupDefaultRecipe(language)
        }
    }
}

@Composable
private fun DropdownMenuScope.ButtonGroupDropdownItem(
    label: String,
    icon: ImageVector,
    destructive: Boolean = false,
) {
    val theme = currentTheme
    val textColor = if (destructive) theme.colors.destructive else theme.colors.popoverForeground
    val iconColor = if (destructive) theme.colors.destructive else theme.colors.mutedForeground
    DropdownMenuItem(label, onClick = {}, destructive = destructive) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, modifier = Modifier.size(16.dp), tint = iconColor)
            Text(
                textStyle = theme.typography.bodyMedium.copy(color = textColor, fontSize = 14.sp, lineHeight = 20.sp),
            ) {
                label
            }
        }
    }
}
