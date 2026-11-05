@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.*
import com.toutiaoyan.compose.ui.components.command.Command
import com.toutiaoyan.compose.ui.components.command.CommandEmpty
import com.toutiaoyan.compose.ui.components.command.CommandGroup
import com.toutiaoyan.compose.ui.components.command.CommandInput
import com.toutiaoyan.compose.ui.components.command.CommandItem
import com.toutiaoyan.compose.ui.components.command.CommandList
import com.toutiaoyan.compose.ui.components.command.CommandSeparator
import com.toutiaoyan.compose.ui.components.command.CommandShortcut
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.currentTheme

/** One icon + label + optional shortcut row shared by the page previews and their code. */
@Composable
private fun CommandExampleItem(
    label: String,
    icon: ImageVector,
    shortcut: String? = null,
    disabled: Boolean = false,
    onSelect: () -> Unit = {},
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
                        if (disabled) {
                            currentTheme.colors.mutedForeground
                        } else {
                            currentTheme.colors.foreground
                        },
                    ),
            )
            Text(Modifier.weight(1f)) { label }
            if (shortcut != null) CommandShortcut(shortcut)
        }
    }
}

@Composable
private fun SurfaceText(
    text: String,
    muted: Boolean = false,
) {
    val theme = currentTheme
    Text(
        textStyle =
            theme.typography.bodyMedium.copy(
                fontFamily = theme.fontFamily,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Normal,
                color = if (muted) theme.colors.mutedForeground else theme.colors.foreground,
            ),
    ) { text }
}

private val commandDemoPreview: @Composable () -> Unit = {
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

private val commandDemoCode =
    """
    @Composable
    fun CommandDemo() {
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
            if (selection.isNotEmpty()) SurfaceText("Selected: ${'$'}selection", muted = true)
        }
    }

    @Composable
    private fun CommandExampleItem(
        label: String,
        icon: ImageVector,
        shortcut: String? = null,
        disabled: Boolean = false,
        onSelect: () -> Unit = {},
    ) {
        CommandItem(label, onSelect, disabled = disabled) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Image(icon, null, Modifier.size(16.dp))
                Text(Modifier.weight(1f)) { label }
                if (shortcut != null) CommandShortcut(shortcut)
            }
        }
    }
    """.trimIndent()

val commandDoc =
    ComponentDoc(
        id = "command",
        title = "Command",
        description = "Command menu for search and quick actions.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.command.*

            Command {
                CommandInput(placeholder = "Type a command or search...")
                CommandList {
                    CommandEmpty()
                    CommandGroup(heading = "Suggestions") {
                        CommandItem("Calendar", onSelect = {})
                        CommandItem("Search Emoji", onSelect = {})
                        CommandItem("Calculator", onSelect = {})
                    }
                    CommandSeparator()
                    CommandGroup(heading = "Settings") {
                        CommandItem("Profile", onSelect = {})
                        CommandItem("Billing", onSelect = {})
                        CommandItem("Settings", onSelect = {})
                    }
                }
            }
            """.trimIndent(),
        compositionCode =
            """
            Command
            ├── CommandInput
            └── CommandList
                ├── CommandEmpty
                ├── CommandGroup
                │   ├── CommandItem
                │   └── CommandItem
                ├── CommandSeparator
                └── CommandGroup
                    ├── CommandItem
                    └── CommandItem
            """.trimIndent(),
        examplesHeading = null,
        beforeExamples =
            listOf(
                ComponentGuideSection(
                    "About",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            "The `Command` component is modelled on the upstream cmdk command menu: " +
                                "a searchable list with groups, separators and keyboard selection.",
                        ),
                    ),
                ),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code = commandDemoCode,
                    preview = commandDemoPreview,
                ),
            ) + commandOfficialRecipes + listOf(
                ComponentExample(
                    title = "Kotlin extension: Controlled value",
                    code =
                        """
                        var value by remember { mutableStateOf("") }
                        Command(value = value, onValueChange = { value = it }) {
                            CommandInput()
                            CommandList {
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = {})
                                    CommandItem("Search Emoji", onSelect = {})
                                }
                            }
                        }
                        // value is the highlighted item's value; rejected writes keep the old value.
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf("") }
                        Column(Modifier.widthIn(max = 384.dp)) {
                            Command(value = value, onValueChange = { value = it }) {
                                CommandInput()
                                CommandList {
                                    CommandGroup(heading = "Suggestions") {
                                        CommandItem("Calendar", onSelect = {})
                                        CommandItem("Search Emoji", onSelect = {})
                                    }
                                }
                            }
                            SurfaceText("Active: " + value.ifEmpty { "(none)" }, muted = true)
                        }
                    },
                ),
                ComponentExample(
                    title = "Kotlin extension: Custom filter",
                    code =
                        """
                        Command(filter = { query, item -> item.contains(query, ignoreCase = true) }) {
                            CommandInput()
                            CommandList {
                                CommandEmpty(text = "No results found.")
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = {})
                                    CommandItem("Search Emoji", onSelect = {})
                                    CommandItem("Calculator", onSelect = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command(filter = { query, item -> item.contains(query, ignoreCase = true) }) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = {})
                                    CommandItem("Search Emoji", onSelect = {})
                                    CommandItem("Calculator", onSelect = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Kotlin extension: Long list keyboard navigation",
                    code =
                        """
                        Command {
                            CommandInput()
                            CommandList {
                                repeat(80) { index ->
                                    CommandItem("Action ${'$'}index", onSelect = {})
                                }
                            }
                        }
                        // Arrow keys move the selection; Home/End jump to the first/last item
                        // and the list scrolls the highlighted row into view.
                        """.trimIndent(),
                    preview = {
                        Command {
                            CommandInput()
                            CommandList {
                                repeat(80) { index ->
                                    CommandItem("Action $index", onSelect = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Kotlin extension: Keywords",
                    code =
                        """
                        Command {
                            CommandInput()
                            CommandList {
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = {}, keywords = listOf("date", "event"))
                                    CommandItem("Search Emoji", onSelect = {}, keywords = listOf("smile", "icon"))
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command {
                            CommandInput()
                            CommandList {
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = {}, keywords = listOf("date", "event"))
                                    CommandItem("Search Emoji", onSelect = {}, keywords = listOf("smile", "icon"))
                                }
                            }
                        }
                    },
                ),
            ),
        apiSections =
            listOf(
                ComponentApiSection(
                    "Command / CommandDialog",
                    "Search state, filtering and dialog wrapper.",
                    listOf(
                        ApiProperty("defaultQuery", "String", "\"\""),
                        ApiProperty("shouldFilter / filter", "Boolean / (String, String) -> Boolean", "true / substring match"),
                        ApiProperty("loop", "Boolean", "true"),
                        ApiProperty("value / onValueChange", "String? / ((String) -> Unit)?", "null"),
                        ApiProperty("open / onOpenChange (dialog)", "Boolean? / ((Boolean) -> Unit)?", "null"),
                    ),
                ),
                ComponentApiSection(
                    "CommandInput / CommandList / CommandGroup",
                    "Search field, viewport and grouped rows.",
                    listOf(
                        ApiProperty("placeholder", "String", "\"Type a command or search...\""),
                        ApiProperty("disabled", "Boolean", "false"),
                        ApiProperty("heading", "String?", "null on group"),
                    ),
                ),
                ComponentApiSection(
                    "CommandItem / CommandShortcut",
                    "Selectable row with label, icon slot, keywords and shortcut.",
                    listOf(
                        ApiProperty("label / onSelect", "String / () -> Unit", "Required"),
                        ApiProperty("shortcut", "String?", "null"),
                        ApiProperty("keywords", "List<String>", "emptyList()"),
                        ApiProperty("enabled / disabled", "Boolean", "true / false"),
                    ),
                ),
            ),
    )
