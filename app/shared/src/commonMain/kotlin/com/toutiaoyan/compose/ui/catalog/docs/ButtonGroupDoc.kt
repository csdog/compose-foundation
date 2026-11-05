@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ArrowRight
import com.composables.icons.lucide.AudioLines
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.Ellipsis
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Minus
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.Search
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.buttongroup.*
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenu
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuContent
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuGroup
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuItem
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuItemVariant
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuTrigger
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuSeparator
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.inputgroup.*
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
    ButtonGroup {
        ButtonGroup {
            ButtonGroupItem(onClick = {}, variant = ButtonVariant.Outline, size = ButtonSize.Icon) { Icon(Lucide.Plus) }
        }
        ButtonGroup {
            InputGroup(Modifier.width(280.dp)) {
                InputGroupInput(message, { message = it }, placeholder = "Send a message...")
                InputGroupAddon(InputGroupAddonAlign.InlineEnd) {
                    InputGroupButton(onClick = {}, size = InputGroupButtonSize.Xs, label = "Voice Mode") { Icon(Lucide.AudioLines) }
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
    ButtonGroup(style = rememberButtonGroupOutlineStyle()) {
        Input(query, { query = it }, placeholder = "Search")
        ButtonGroupSeparator()
        ButtonGroupItem(onClick = {}, variant = ButtonVariant.Outline, size = ButtonSize.Icon) { Icon(Lucide.Search) }
    }
    """.trimIndent()

private val buttonGroupInputGroupCode =
    """
    var message by remember { mutableStateOf("") }
    ButtonGroup {
        ButtonGroup {
            ButtonGroupItem(onClick = {}, variant = ButtonVariant.Outline, size = ButtonSize.Icon) { Icon(Lucide.Plus) }
        }
        ButtonGroup {
            InputGroup(Modifier.width(280.dp)) {
                InputGroupInput(message, { message = it }, placeholder = "Send a message...")
                InputGroupAddon(InputGroupAddonAlign.InlineEnd) {
                    InputGroupButton(onClick = {}, size = InputGroupButtonSize.Xs, label = "Voice Mode") {
                        Icon(Lucide.AudioLines)
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
                ) { Icon(Lucide.ChevronDown) }
            }
            DropdownMenuContent {
                DropdownMenuGroup {
                    DropdownMenuItem("Mute Conversation", onClick = {})
                    DropdownMenuItem("Mark as Read", onClick = {})
                    DropdownMenuItem("Report Conversation", onClick = {})
                    DropdownMenuItem("Block User", onClick = {})
                    DropdownMenuItem("Share Conversation", onClick = {})
                    DropdownMenuItem("Copy Conversation", onClick = {})
                }
                DropdownMenuSeparator()
                DropdownMenuGroup {
                    DropdownMenuItem(
                        "Delete Conversation",
                        onClick = {},
                        variant = DropdownMenuItemVariant.Destructive,
                    )
                }
            }
        }
    }
    """.trimIndent()

private val buttonGroupSelectCode =
    """
    var currency by remember { mutableStateOf("$") }
    var amount by remember { mutableStateOf("") }
    ButtonGroup {
        ButtonGroup {
            Select(value = currency, onValueChange = { currency = it }) {
                SelectTrigger(Modifier.width(72.dp))
                SelectContent {
                    SelectItem("$")
                    SelectItem("€")
                    SelectItem("£")
                }
            }
            Input(amount, { amount = it }, Modifier.width(120.dp), placeholder = "10.00")
        }
        ButtonGroup {
            ButtonGroupItem(onClick = {}, variant = ButtonVariant.Outline, size = ButtonSize.Icon) { Icon(Lucide.ArrowRight) }
        }
    }
    """.trimIndent()

private val buttonGroupPopoverCode =
    """
    var description by remember { mutableStateOf("") }
    ButtonGroup {
        Popover {
            PopoverTrigger {
                ButtonGroupItem("Copilot", onClick = toggle, variant = ButtonVariant.Outline)
            }
            PopoverContent {
                Text { "Start a new task with Copilot" }
                Textarea(description, { description = it }, placeholder = "Describe your task...")
            }
        }
        ButtonGroupSeparator()
        ButtonGroupItem(onClick = {}, variant = ButtonVariant.Outline, size = ButtonSize.Icon) { Icon(Lucide.ChevronDown) }
    }
    """.trimIndent()

private val buttonGroupRtlCode =
    """
    var language by remember { mutableStateOf(0) }
    RtlPreview(language, listOf("Arabic (العربية)", "English", "Hebrew (עברית)"), { language = it }) {
        CompositionLocalProvider(
            LocalLayoutDirection provides if (language == 1) LayoutDirection.Ltr else LayoutDirection.Rtl,
        ) {
            ButtonGroup {
                ButtonGroupItem(if (language == 0) "أرشفة" else "Archive", onClick = {}, variant = ButtonVariant.Outline)
                ButtonGroupItem(if (language == 0) "تقرير" else "Report", onClick = {}, variant = ButtonVariant.Outline)
                ButtonGroupItem(onClick = {}, variant = ButtonVariant.Outline, size = ButtonSize.Icon) { Icon(Lucide.Ellipsis) }
            }
        }
    }
    """.trimIndent()

val buttonGroupDoc =
    ComponentDoc(
        id = "button-group",
        title = "Button Group",
        description = "A container that groups related buttons together with consistent styling.",
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
        apiProperties =
            listOf(
                ApiProperty("orientation", "ButtonGroupOrientation", "Horizontal"),
                ApiProperty("contentDescription", "String?", "null"),
                ApiProperty("ButtonGroupItem.size", "ButtonSize", "Md"),
                ApiProperty("ButtonGroupItem.variant", "ButtonVariant", "Ghost"),
            ),
        examples =
            listOf(
                ComponentExample("Default", buttonGroupDefaultCode, preview = { ButtonGroupDefaultRecipe() }),
                ComponentExample(
                    "Orientation",
                    buttonGroupOrientationCode,
                    preview = { ButtonGroupOrientationRecipe() },
                ),
                ComponentExample("Size", buttonGroupSizeCode, preview = { ButtonGroupSizeRecipe() }),
                ComponentExample("Nested", buttonGroupNestedCode, preview = { ButtonGroupNestedRecipe() }),
                ComponentExample("Separator", buttonGroupSeparatorCode, preview = { ButtonGroupSeparatorRecipe() }),
                ComponentExample("Split", buttonGroupSplitCode, preview = { ButtonGroupSplitRecipe() }),
                ComponentExample("Input", buttonGroupInputCode, preview = { ButtonGroupInputRecipe() }),
                ComponentExample("Input Group", buttonGroupInputGroupCode, preview = { ButtonGroupInputGroupRecipe() }),
                ComponentExample("Dropdown Menu", buttonGroupDropdownCode, preview = { ButtonGroupDropdownRecipe() }),
                ComponentExample("Select", buttonGroupSelectCode, preview = { ButtonGroupSelectRecipe() }),
                ComponentExample("Popover", buttonGroupPopoverCode, preview = { ButtonGroupPopoverRecipe() }),
                ComponentExample(
                    "RTL",
                    buttonGroupRtlCode,
                    descriptionMarkdown =
                        "To enable RTL support in shadcn/ui, see the [RTL configuration guide](https://ui.shadcn.com/docs/rtl).",
                    previewPadding = 0.dp,
                    preview = { ButtonGroupRtlRecipe() },
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
    ButtonGroup {
        ButtonGroup {
            ButtonGroupItem(onClick = {}, variant = ButtonVariant.Outline, size = ButtonSize.Icon) {
                Icon(Lucide.Plus)
            }
        }
        ButtonGroup {
            InputGroup(Modifier.width(280.dp)) {
                InputGroupInput(message, { message = it }, placeholder = "Send a message...")
                InputGroupAddon(InputGroupAddonAlign.InlineEnd) {
                    InputGroupButton(
                        onClick = {},
                        size = InputGroupButtonSize.Xs,
                        label = "Voice Mode",
                    ) { Icon(Lucide.AudioLines) }
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
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val fieldWidth = (maxWidth - 41.dp).coerceAtLeast(160.dp)
        ButtonGroup(style = rememberButtonGroupOutlineStyle()) {
            Input(query, { query = it }, Modifier.width(fieldWidth), placeholder = "Search")
            ButtonGroupSeparator()
            ButtonGroupItem(
                onClick = {},
                variant = ButtonVariant.Outline,
                size = ButtonSize.Icon,
            ) { Icon(Lucide.Search) }
        }
    }
}

@Composable
private fun ButtonGroupInputGroupRecipe() {
    var message by remember { mutableStateOf("") }
    ButtonGroup {
        ButtonGroup {
            ButtonGroupItem(
                onClick = {},
                variant = ButtonVariant.Outline,
                size = ButtonSize.Icon,
            ) { Icon(Lucide.Plus) }
        }
        ButtonGroup {
            InputGroup(Modifier.width(280.dp)) {
                InputGroupInput(message, { message = it }, placeholder = "Send a message...")
                InputGroupAddon(InputGroupAddonAlign.InlineEnd) {
                    InputGroupButton(
                        onClick = {},
                        size = InputGroupButtonSize.Xs,
                        label = "Voice Mode",
                    ) {
                        Icon(Lucide.AudioLines)
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
            DropdownMenuContent {
                DropdownMenuGroup {
                    DropdownMenuItem("Mute Conversation", onClick = {})
                    DropdownMenuItem("Mark as Read", onClick = {})
                    DropdownMenuItem("Report Conversation", onClick = {})
                    DropdownMenuItem("Block User", onClick = {})
                    DropdownMenuItem("Share Conversation", onClick = {})
                    DropdownMenuItem("Copy Conversation", onClick = {})
                }
                DropdownMenuSeparator()
                DropdownMenuGroup {
                    DropdownMenuItem(
                        "Delete Conversation",
                        onClick = {},
                        variant = DropdownMenuItemVariant.Destructive,
                    )
                }
            }
        }
    }
}

@Composable
private fun ButtonGroupSelectRecipe() {
    var currency by remember { mutableStateOf("$") }
    var amount by remember { mutableStateOf("") }
    ButtonGroup {
        ButtonGroup {
            Select(value = currency, onValueChange = { currency = it }) {
                SelectTrigger(Modifier.width(72.dp))
                SelectContent {
                    SelectItem("$")
                    SelectItem("€")
                    SelectItem("£")
                }
            }
            Input(amount, { amount = it }, Modifier.width(120.dp), placeholder = "10.00")
        }
        ButtonGroup {
            ButtonGroupItem(
                onClick = {},
                variant = ButtonVariant.Outline,
                size = ButtonSize.Icon,
            ) { Icon(Lucide.ArrowRight) }
        }
    }
}

@Composable
private fun ButtonGroupPopoverRecipe() {
    var description by remember { mutableStateOf("") }
    ButtonGroup {
        Popover {
            PopoverTrigger {
                ButtonGroupItem("Copilot", onClick = toggle, variant = ButtonVariant.Outline)
            }
            PopoverContent {
                Text { "Start a new task with Copilot" }
                Textarea(description, { description = it }, placeholder = "Describe your task...")
            }
        }
        ButtonGroupSeparator()
        ButtonGroupItem(
            onClick = {},
            variant = ButtonVariant.Outline,
            size = ButtonSize.Icon,
        ) { Icon(Lucide.ChevronDown) }
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
            ButtonGroup {
                ButtonGroupItem(
                    if (language == 0) "أرشفة" else "Archive",
                    onClick = {},
                    variant = ButtonVariant.Outline,
                )
                ButtonGroupItem(if (language == 0) "تقرير" else "Report", onClick = {}, variant = ButtonVariant.Outline)
                ButtonGroupItem(onClick = {}, variant = ButtonVariant.Outline, size = ButtonSize.Icon) {
                    Icon(Lucide.Ellipsis)
                }
            }
        }
    }
}
