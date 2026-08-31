@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Search
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.toutiaoyan.compose.ui.components.command.Command
import com.toutiaoyan.compose.ui.components.command.CommandDefaults
import com.toutiaoyan.compose.ui.components.command.CommandDialog
import com.toutiaoyan.compose.ui.components.command.CommandEmpty
import com.toutiaoyan.compose.ui.components.command.CommandFooter
import com.toutiaoyan.compose.ui.components.command.CommandGroup
import com.toutiaoyan.compose.ui.components.command.CommandGroupHeading
import com.toutiaoyan.compose.ui.components.command.CommandInput
import com.toutiaoyan.compose.ui.components.command.CommandItem
import com.toutiaoyan.compose.ui.components.command.CommandList
import com.toutiaoyan.compose.ui.components.command.CommandLoading
import com.toutiaoyan.compose.ui.components.command.CommandSeparator
import com.toutiaoyan.compose.ui.components.command.CommandShortcut
import com.toutiaoyan.compose.ui.components.button.Button

val commandDoc =
    ComponentDoc(
        id = "command",
        title = "Command",
        description = "A searchable/filterable action list -- the building block behind a command palette.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.command.Command
            import com.toutiaoyan.compose.ui.components.command.CommandEmpty
            import com.toutiaoyan.compose.ui.components.command.CommandGroup
            import com.toutiaoyan.compose.ui.components.command.CommandInput
            import com.toutiaoyan.compose.ui.components.command.CommandItem
            import com.toutiaoyan.compose.ui.components.command.CommandList
            import com.toutiaoyan.compose.ui.components.command.CommandSeparator

            Command {
                CommandInput(placeholder = "Type a command or search...")
                CommandList {
                    CommandEmpty()
                    CommandGroup(heading = "Suggestions") {
                        CommandItem("Calendar", onSelect = {})
                        CommandItem("Search Emoji", onSelect = {})
                    }
                    CommandSeparator()
                    CommandGroup(heading = "Settings") {
                        CommandItem("Profile", onSelect = {})
                    }
                }
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        Command {
                            CommandInput()
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
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = {})
                                    CommandItem("Search Emoji", onSelect = {})
                                    CommandItem("Calculator", onSelect = {})
                                }
                                CommandSeparator()
                                CommandGroup(heading = "Settings") {
                                    CommandItem("Profile", onSelect = {}, shortcut = "⌘P")
                                    CommandItem("Billing", onSelect = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default query",
                    code =
                        """
                        Command(defaultQuery = "") {
                            CommandInput()
                            CommandList {
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command(defaultQuery = "") {
                            CommandInput()
                            CommandList {
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Dialog",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Button("Open command", onClick = { open = true })
                        CommandDialog(open = open, onOpenChange = { open = it }) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = { open = false })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Button("Open command", onClick = { open = true })
                        CommandDialog(open = open, onOpenChange = { open = it }) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = { open = false })
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Dialog title",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Button("Open command", onClick = { open = true })
                        CommandDialog(
                            open = open,
                            onOpenChange = { open = it },
                            title = "Command Palette",
                        ) {
                            CommandInput()
                            CommandList {
                                CommandItem("Calendar", onSelect = { open = false })
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Button("Open command", onClick = { open = true })
                        CommandDialog(
                            open = open,
                            onOpenChange = { open = it },
                            title = "Command Palette",
                        ) {
                            CommandInput()
                            CommandList {
                                CommandItem("Calendar", onSelect = { open = false })
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default dialog title",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Button("Open command", onClick = { open = true })
                        CommandDialog(
                            open = open,
                            onOpenChange = { open = it },
                            title = "Command Palette",
                        ) {
                            CommandInput()
                            CommandList {
                                CommandItem("Calendar", onSelect = { open = false })
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Button("Open command", onClick = { open = true })
                        CommandDialog(
                            open = open,
                            onOpenChange = { open = it },
                            title = "Command Palette",
                        ) {
                            CommandInput()
                            CommandList {
                                CommandItem("Calendar", onSelect = { open = false })
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Dialog description",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Button("Open command", onClick = { open = true })
                        CommandDialog(
                            open = open,
                            onOpenChange = { open = it },
                            description = "Search for a command to run...",
                        ) {
                            CommandInput()
                            CommandList {
                                CommandItem("Calendar", onSelect = { open = false })
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Button("Open command", onClick = { open = true })
                        CommandDialog(
                            open = open,
                            onOpenChange = { open = it },
                            description = "Search for a command to run...",
                        ) {
                            CommandInput()
                            CommandList {
                                CommandItem("Calendar", onSelect = { open = false })
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default dialog description",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Button("Open command", onClick = { open = true })
                        CommandDialog(
                            open = open,
                            onOpenChange = { open = it },
                            description = "Search for a command to run...",
                        ) {
                            CommandInput()
                            CommandList {
                                CommandItem("Calendar", onSelect = { open = false })
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Button("Open command", onClick = { open = true })
                        CommandDialog(
                            open = open,
                            onOpenChange = { open = it },
                            description = "Search for a command to run...",
                        ) {
                            CommandInput()
                            CommandList {
                                CommandItem("Calendar", onSelect = { open = false })
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code =
                        """
                        Command {
                            CommandInput()
                            CommandList {
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = {})
                                    CommandItem("Search Emoji", onSelect = {}, disabled = true)
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command {
                            CommandInput()
                            CommandList {
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = {})
                                    CommandItem("Search Emoji", onSelect = {}, disabled = true)
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        Command {
                            CommandInput(disabled = false)
                            CommandList {
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = {})
                                    CommandItem("Search Emoji", onSelect = {}, disabled = false)
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command {
                            CommandInput(disabled = false)
                            CommandList {
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = {})
                                    CommandItem("Search Emoji", onSelect = {}, disabled = false)
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled input",
                    code =
                        """
                        Command {
                            CommandInput(disabled = true)
                            CommandList {
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command {
                            CommandInput(disabled = true)
                            CommandList {
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Loading",
                    code =
                        """
                        Command {
                            CommandInput()
                            CommandList {
                                CommandLoading()
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command {
                            CommandInput()
                            CommandList {
                                CommandLoading()
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Loading text",
                    code =
                        """
                        Command {
                            CommandInput()
                            CommandList {
                                CommandLoading(text = "Loading...")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command {
                            CommandInput()
                            CommandList {
                                CommandLoading(text = "Loading...")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default loading text",
                    code =
                        """
                        Command {
                            CommandInput()
                            CommandList {
                                CommandLoading(text = "Loading...")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command {
                            CommandInput()
                            CommandList {
                                CommandLoading(text = "Loading...")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Keywords",
                    code =
                        """
                        Command {
                            CommandInput()
                            CommandList {
                                CommandItem(
                                    "Calendar",
                                    onSelect = {},
                                    keywords = listOf("event", "date"),
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command {
                            CommandInput()
                            CommandList {
                                CommandItem(
                                    "Calendar",
                                    onSelect = {},
                                    keywords = listOf("event", "date"),
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Empty",
                    code =
                        """
                        Command(defaultQuery = "xyzzy") {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandItem("Calendar", onSelect = {})
                                CommandItem("Search Emoji", onSelect = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command(defaultQuery = "xyzzy") {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandItem("Calendar", onSelect = {})
                                CommandItem("Search Emoji", onSelect = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Empty text",
                    code =
                        """
                        Command(defaultQuery = "xyzzy") {
                            CommandInput()
                            CommandList {
                                CommandEmpty(text = "No results found.")
                                CommandItem("Calendar", onSelect = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command(defaultQuery = "xyzzy") {
                            CommandInput()
                            CommandList {
                                CommandEmpty(text = "No results found.")
                                CommandItem("Calendar", onSelect = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default empty text",
                    code =
                        """
                        Command(defaultQuery = "xyzzy") {
                            CommandInput()
                            CommandList {
                                CommandEmpty(text = "No results found.")
                                CommandItem("Calendar", onSelect = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command(defaultQuery = "xyzzy") {
                            CommandInput()
                            CommandList {
                                CommandEmpty(text = "No results found.")
                                CommandItem("Calendar", onSelect = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Custom filter",
                    code =
                        """
                        Command(
                            defaultQuery = "Cal",
                            filter = { query, value -> value.startsWith(query, ignoreCase = true) },
                        ) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandItem("Calendar", onSelect = {})
                                CommandItem("Search Emoji", onSelect = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command(
                            defaultQuery = "Cal",
                            filter = { query, value -> value.startsWith(query, ignoreCase = true) },
                        ) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandItem("Calendar", onSelect = {})
                                CommandItem("Search Emoji", onSelect = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default filter",
                    code =
                        """
                        Command(shouldFilter = true) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandItem("Calendar", onSelect = {})
                                CommandItem("Search Emoji", onSelect = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command(shouldFilter = true) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandItem("Calendar", onSelect = {})
                                CommandItem("Search Emoji", onSelect = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default should filter",
                    code =
                        """
                        Command(shouldFilter = true) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandItem("Calendar", onSelect = {})
                                CommandItem("Search Emoji", onSelect = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command(shouldFilter = true) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandItem("Calendar", onSelect = {})
                                CommandItem("Search Emoji", onSelect = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "No loop",
                    code =
                        """
                        Command(loop = false) {
                            CommandInput()
                            CommandList {
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = {})
                                    CommandItem("Search Emoji", onSelect = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command(loop = false) {
                            CommandInput()
                            CommandList {
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = {})
                                    CommandItem("Search Emoji", onSelect = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Loop",
                    code =
                        """
                        Command(loop = true) {
                            CommandInput()
                            CommandList {
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = {})
                                    CommandItem("Search Emoji", onSelect = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command(loop = true) {
                            CommandInput()
                            CommandList {
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = {})
                                    CommandItem("Search Emoji", onSelect = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default loop",
                    code =
                        """
                        Command(loop = true) {
                            CommandInput()
                            CommandList {
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = {})
                                    CommandItem("Search Emoji", onSelect = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command(loop = true) {
                            CommandInput()
                            CommandList {
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = {})
                                    CommandItem("Search Emoji", onSelect = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Controlled",
                    code =
                        """
                        var selected by remember { mutableStateOf("Calendar") }
                        Command(value = selected, onValueChange = { selected = it }) {
                            CommandInput()
                            CommandList {
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = {})
                                    CommandItem("Search Emoji", onSelect = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var selected by remember { mutableStateOf("Calendar") }
                        Command(value = selected, onValueChange = { selected = it }) {
                            CommandInput()
                            CommandList {
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = {})
                                    CommandItem("Search Emoji", onSelect = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Close button",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Button("Open command", onClick = { open = true })
                        CommandDialog(
                            open = open,
                            onOpenChange = { open = it },
                            showCloseButton = CommandDefaults.ShowCloseButton,
                        ) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = { open = false })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Button("Open command", onClick = { open = true })
                        CommandDialog(
                            open = open,
                            onOpenChange = { open = it },
                            showCloseButton = CommandDefaults.ShowCloseButton,
                        ) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = { open = false })
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
                        Button("Open command", onClick = { open = true })
                        CommandDialog(
                            open = open,
                            onOpenChange = { open = it },
                            showCloseButton = false,
                        ) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = { open = false })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Button("Open command", onClick = { open = true })
                        CommandDialog(
                            open = open,
                            onOpenChange = { open = it },
                            showCloseButton = false,
                        ) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = { open = false })
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
                        Button("Open command", onClick = { open = true })
                        CommandDialog(
                            open = open,
                            onOpenChange = { open = it },
                            modal = false,
                        ) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = { open = false })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Button("Open command", onClick = { open = true })
                        CommandDialog(
                            open = open,
                            onOpenChange = { open = it },
                            modal = false,
                        ) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = { open = false })
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
                        Button("Open command", onClick = { open = true })
                        CommandDialog(
                            open = open,
                            onOpenChange = { open = it },
                            modal = true,
                        ) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = { open = false })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Button("Open command", onClick = { open = true })
                        CommandDialog(
                            open = open,
                            onOpenChange = { open = it },
                            modal = true,
                        ) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = { open = false })
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
                        Button("Open command", onClick = { open = true })
                        CommandDialog(
                            open = open,
                            onOpenChange = { open = it },
                            modal = true,
                        ) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = { open = false })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Button("Open command", onClick = { open = true })
                        CommandDialog(
                            open = open,
                            onOpenChange = { open = it },
                            modal = true,
                        ) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = { open = false })
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Undismissible",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Button("Open command", onClick = { open = true })
                        CommandDialog(
                            open = open,
                            onOpenChange = { open = it },
                            dismissOnClickOutside = false,
                        ) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = { open = false })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Button("Open command", onClick = { open = true })
                        CommandDialog(
                            open = open,
                            onOpenChange = { open = it },
                            dismissOnClickOutside = false,
                        ) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = { open = false })
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
                        Button("Open command", onClick = { open = true })
                        CommandDialog(
                            open = open,
                            onOpenChange = { open = it },
                            dismissOnClickOutside = true,
                        ) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = { open = false })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Button("Open command", onClick = { open = true })
                        CommandDialog(
                            open = open,
                            onOpenChange = { open = it },
                            dismissOnClickOutside = true,
                        ) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = { open = false })
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Always visible item",
                    code =
                        """
                        Command {
                            CommandInput(placeholder = "Type a command or search...")
                            CommandList {
                                CommandEmpty()
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = {}, forceMount = true)
                                    CommandItem("Search Emoji", onSelect = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command {
                            CommandInput(placeholder = "Type a command or search...")
                            CommandList {
                                CommandEmpty()
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = {}, forceMount = true)
                                    CommandItem("Search Emoji", onSelect = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default force mount",
                    code =
                        """
                        Command {
                            CommandInput(placeholder = "Type a command or search...")
                            CommandList {
                                CommandEmpty()
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = {}, forceMount = false)
                                    CommandItem("Search Emoji", onSelect = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command {
                            CommandInput(placeholder = "Type a command or search...")
                            CommandList {
                                CommandEmpty()
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = {}, forceMount = false)
                                    CommandItem("Search Emoji", onSelect = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Prevent pointer dismiss",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Button("Open command", onClick = { open = true })
                        CommandDialog(
                            open = open,
                            onOpenChange = { open = it },
                            onPointerDownOutside = { true },
                        ) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = { open = false })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Button("Open command", onClick = { open = true })
                        CommandDialog(
                            open = open,
                            onOpenChange = { open = it },
                            onPointerDownOutside = { true },
                        ) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = { open = false })
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
                        Button("Open command", onClick = { open = true })
                        CommandDialog(
                            open = open,
                            onOpenChange = { open = it },
                            onInteractOutside = { true },
                        ) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = { open = false })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Button("Open command", onClick = { open = true })
                        CommandDialog(
                            open = open,
                            onOpenChange = { open = it },
                            onInteractOutside = { true },
                        ) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = { open = false })
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Content close label",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Button("Open command", onClick = { open = true })
                        CommandDialog(
                            open = open,
                            onOpenChange = { open = it },
                            showCloseButton = true,
                            closeAriaLabel = "Dismiss command",
                        ) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = { open = false })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Button("Open command", onClick = { open = true })
                        CommandDialog(
                            open = open,
                            onOpenChange = { open = it },
                            showCloseButton = true,
                            closeAriaLabel = "Dismiss command",
                        ) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = { open = false })
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
                        Button("Open command", onClick = { open = true })
                        CommandDialog(
                            open = open,
                            onOpenChange = { open = it },
                            showCloseButton = true,
                            closeAriaLabel = "Close",
                        ) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = { open = false })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Button("Open command", onClick = { open = true })
                        CommandDialog(
                            open = open,
                            onOpenChange = { open = it },
                            showCloseButton = true,
                            closeAriaLabel = "Close",
                        ) {
                            CommandInput()
                            CommandList {
                                CommandEmpty()
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = { open = false })
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Custom text value",
                    code =
                        """
                        Command {
                            CommandInput()
                            CommandList {
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("cal", onSelect = {}, textValue = "Calendar")
                                    CommandItem("mail", onSelect = {}, textValue = "Mail")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command {
                            CommandInput()
                            CommandList {
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("cal", onSelect = {}, textValue = "Calendar")
                                    CommandItem("mail", onSelect = {}, textValue = "Mail")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default value",
                    code =
                        """
                        Command(defaultValue = "Calendar") {
                            CommandInput()
                            CommandList {
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = {})
                                    CommandItem("Search Emoji", onSelect = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command(defaultValue = "Calendar") {
                            CommandInput()
                            CommandList {
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = {})
                                    CommandItem("Search Emoji", onSelect = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Group heading",
                    code =
                        """
                        Command {
                            CommandInput()
                            CommandList {
                                CommandGroup {
                                    CommandGroupHeading("Suggestions")
                                    CommandItem("Calendar", onSelect = {})
                                    CommandItem("Search Emoji", onSelect = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command {
                            CommandInput()
                            CommandList {
                                CommandGroup {
                                    CommandGroupHeading("Suggestions")
                                    CommandItem("Calendar", onSelect = {})
                                    CommandItem("Search Emoji", onSelect = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Shortcut",
                    code =
                        """
                        Command {
                            CommandInput()
                            CommandList {
                                CommandGroup(heading = "Settings") {
                                    CommandItem("Profile", onSelect = {}, shortcut = "⌘P")
                                    CommandItem("Billing", onSelect = {}, shortcut = "⌘B")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command {
                            CommandInput()
                            CommandList {
                                CommandGroup(heading = "Settings") {
                                    CommandItem("Profile", onSelect = {}, shortcut = "⌘P")
                                    CommandItem("Billing", onSelect = {}, shortcut = "⌘B")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Footer",
                    code =
                        """
                        Command {
                            CommandInput()
                            CommandList {
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = {})
                                }
                            }
                            CommandFooter {
                                CommandShortcut("⌘K")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command {
                            CommandInput()
                            CommandList {
                                CommandGroup(heading = "Suggestions") {
                                    CommandItem("Calendar", onSelect = {})
                                }
                            }
                            CommandFooter {
                                CommandShortcut("⌘K")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Separator",
                    code =
                        """
                        Command {
                            CommandInput()
                            CommandList {
                                CommandItem("Calendar", onSelect = {})
                                CommandSeparator()
                                CommandItem("Profile", onSelect = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command {
                            CommandInput()
                            CommandList {
                                CommandItem("Calendar", onSelect = {})
                                CommandSeparator()
                                CommandItem("Profile", onSelect = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Icon",
                    code =
                        """
                        Command {
                            CommandInput(icon = { DocIcon(Lucide.Search) })
                            CommandList {
                                CommandItem("Calendar", onSelect = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command {
                            CommandInput(icon = { DocIcon(Lucide.Search) })
                            CommandList {
                                CommandItem("Calendar", onSelect = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default icon",
                    code =
                        """
                        Command {
                            CommandInput(icon = null)
                            CommandList {
                                CommandItem("Calendar", onSelect = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command {
                            CommandInput(icon = null)
                            CommandList {
                                CommandItem("Calendar", onSelect = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Placeholder",
                    code =
                        """
                        Command {
                            CommandInput(placeholder = "Search...")
                            CommandList {
                                CommandItem("Calendar", onSelect = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command {
                            CommandInput(placeholder = "Search...")
                            CommandList {
                                CommandItem("Calendar", onSelect = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default placeholder",
                    code =
                        """
                        Command {
                            CommandInput(placeholder = "Type a command or search...")
                            CommandList {
                                CommandItem("Calendar", onSelect = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command {
                            CommandInput(placeholder = "Type a command or search...")
                            CommandList {
                                CommandItem("Calendar", onSelect = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Accessible label",
                    code =
                        """
                        Command(label = "Command Menu") {
                            CommandInput()
                            CommandList {
                                CommandItem("Calendar", onSelect = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command(label = "Command Menu") {
                            CommandInput()
                            CommandList {
                                CommandItem("Calendar", onSelect = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default label",
                    code =
                        """
                        Command(label = "Command Menu") {
                            CommandInput()
                            CommandList {
                                CommandItem("Calendar", onSelect = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command(label = "Command Menu") {
                            CommandInput()
                            CommandList {
                                CommandItem("Calendar", onSelect = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Input",
                    code =
                        """
                        Command {
                            CommandInput()
                            CommandList {
                                CommandItem("Calendar", onSelect = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command {
                            CommandInput()
                            CommandList {
                                CommandItem("Calendar", onSelect = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "List",
                    code =
                        """
                        Command {
                            CommandInput()
                            CommandList {
                                CommandItem("Calendar", onSelect = {})
                                CommandItem("Search Emoji", onSelect = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command {
                            CommandInput()
                            CommandList {
                                CommandItem("Calendar", onSelect = {})
                                CommandItem("Search Emoji", onSelect = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Group",
                    code =
                        """
                        Command {
                            CommandInput()
                            CommandList {
                                CommandGroup {
                                    CommandItem("Calendar", onSelect = {})
                                    CommandItem("Search Emoji", onSelect = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command {
                            CommandInput()
                            CommandList {
                                CommandGroup {
                                    CommandItem("Calendar", onSelect = {})
                                    CommandItem("Search Emoji", onSelect = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Item",
                    code =
                        """
                        Command {
                            CommandInput()
                            CommandList {
                                CommandItem("Calendar", onSelect = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Command {
                            CommandInput()
                            CommandList {
                                CommandItem("Calendar", onSelect = {})
                            }
                        }
                    },
                ),
            ),
    )
