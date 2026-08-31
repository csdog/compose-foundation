@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenu
import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuCheckboxItem
import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuContent
import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuGroup
import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuItem
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuItemVariant
import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuLabel
import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuPortal
import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuRadioGroup
import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuRadioItem
import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuSeparator
import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuSub
import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuSubContent
import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuSubTrigger
import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuTrigger
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.theme.currentTheme

val contextMenuDoc =
    ComponentDoc(
        id = "context-menu",
        title = "Context Menu",
        description = "A menu triggered by right-click, opened at the cursor.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenu
            import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuContent
            import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuTrigger

            ContextMenu {
                ContextMenuTrigger("Right-click me")
                ContextMenuContent {
                    ContextMenuItem("Copy", onClick = {})
                }
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger {
                                Box(
                                    modifier = Modifier.size(width = 240.dp, height = 80.dp)
                                        .background(currentTheme.colors.muted, RoundedCornerShape(TwRadius.md)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(textStyle = TextStyles.Muted) { "Right-click here" }
                                }
                            }
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {})
                                ContextMenuItem("Reload", onClick = {})
                                ContextMenuSeparator()
                                ContextMenuItem("Inspect", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger {
                                Box(
                                    modifier =
                                        Modifier.size(width = 240.dp, height = 80.dp)
                                            .background(
                                                currentTheme.colors.muted,
                                                RoundedCornerShape(twRadius.md),
                                            ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(textStyle = TextColors.Muted) { "Right-click here" }
                                }
                            }
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {})
                                ContextMenuItem("Reload", onClick = {})
                                ContextMenuSeparator()
                                ContextMenuItem("Inspect", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Checkboxes",
                    code =
                        """
                        var showStatus by remember { mutableStateOf(true) }
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuCheckboxItem("Status Bar", checked = showStatus) {
                                    showStatus = it
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var showStatus by remember { mutableStateOf(true) }
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuCheckboxItem(
                                    label = "Status Bar",
                                    checked = showStatus,
                                    onCheckedChange = { showStatus = it },
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Radio group",
                    code =
                        """
                        var panel by remember { mutableStateOf("bottom") }
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuRadioGroup(value = panel, onValueChange = { panel = it }) {
                                    ContextMenuRadioItem(value = "top", label = "Top")
                                    ContextMenuRadioItem(value = "bottom", label = "Bottom")
                                    ContextMenuRadioItem(value = "right", label = "Right")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var panel by remember { mutableStateOf("bottom") }
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuRadioGroup(value = panel, onValueChange = { panel = it }) {
                                    ContextMenuRadioItem(value = "top", label = "Top")
                                    ContextMenuRadioItem(value = "bottom", label = "Bottom")
                                    ContextMenuRadioItem(value = "right", label = "Right")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Submenu",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {})
                                ContextMenuSub {
                                    ContextMenuSubTrigger("More")
                                    ContextMenuSubContent {
                                        ContextMenuItem("Save", onClick = {})
                                        ContextMenuItem("Export", onClick = {})
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {})
                                ContextMenuSub {
                                    ContextMenuSubTrigger("More")
                                    ContextMenuSubContent {
                                        ContextMenuItem("Save", onClick = {})
                                        ContextMenuItem("Export", onClick = {})
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Shortcuts",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {}, shortcut = "⌘[")
                                ContextMenuItem("Forward", onClick = {}, shortcut = "⌘]")
                                ContextMenuItem("Reload", onClick = {}, shortcut = "⌘R")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {}, shortcut = "⌘[")
                                ContextMenuItem("Forward", onClick = {}, shortcut = "⌘]")
                                ContextMenuItem("Reload", onClick = {}, shortcut = "⌘R")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {})
                                ContextMenuItem("Forward", onClick = {}, disabled = true)
                                ContextMenuItem("Reload", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {})
                                ContextMenuItem("Forward", onClick = {}, disabled = true)
                                ContextMenuItem("Reload", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {})
                                ContextMenuItem("Forward", onClick = {}, disabled = false)
                                ContextMenuItem("Reload", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {})
                                ContextMenuItem("Forward", onClick = {}, disabled = false)
                                ContextMenuItem("Reload", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default destructive",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {}, destructive = false)
                                ContextMenuItem("Reload", onClick = {}, destructive = false)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {}, destructive = false)
                                ContextMenuItem("Reload", onClick = {}, destructive = false)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default variant",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {}, variant = DropdownMenuItemVariant.Default)
                                ContextMenuItem("Reload", onClick = {}, variant = DropdownMenuItemVariant.Default)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {}, variant = DropdownMenuItemVariant.Default)
                                ContextMenuItem("Reload", onClick = {}, variant = DropdownMenuItemVariant.Default)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Inset",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuLabel("Appearance", inset = true)
                                ContextMenuSeparator()
                                ContextMenuItem("Reset", onClick = {}, inset = true)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuLabel("Appearance", inset = true)
                                ContextMenuSeparator()
                                ContextMenuItem("Reset", onClick = {}, inset = true)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default inset",
                    code =
                        """
                        ContextMenu(defaultOpen = false) {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuLabel("Appearance", inset = false)
                                ContextMenuItem("Reset", onClick = {}, inset = false)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu(defaultOpen = false) {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuLabel("Appearance", inset = false)
                                ContextMenuItem("Reset", onClick = {}, inset = false)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Non-modal",
                    code =
                        """
                        ContextMenu(modal = false) {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {})
                                ContextMenuItem("Reload", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu(modal = false) {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {})
                                ContextMenuItem("Reload", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Modal",
                    code =
                        """
                        ContextMenu(modal = true, defaultOpen = false) {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu(modal = true, defaultOpen = false) {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default modal",
                    code =
                        """
                        ContextMenu(modal = true, defaultOpen = false) {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu(modal = true, defaultOpen = false) {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Undismissible",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent(dismissOnClickOutside = false) {
                                ContextMenuItem("Reload", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent(dismissOnClickOutside = false) {
                                ContextMenuItem("Reload", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default dismiss",
                    code =
                        """
                        ContextMenu(defaultOpen = false) {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent(dismissOnClickOutside = true) {
                                ContextMenuItem("Reload", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu(defaultOpen = false) {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent(dismissOnClickOutside = true) {
                                ContextMenuItem("Reload", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Keep open on select",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Stay open", onClick = {}, onSelect = { true })
                                ContextMenuItem("Reload", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Stay open", onClick = {}, onSelect = { true })
                                ContextMenuItem("Reload", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Custom text value",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("sm", onClick = {}, textValue = "Small")
                                ContextMenuItem("md", onClick = {}, textValue = "Medium")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("sm", onClick = {}, textValue = "Small")
                                ContextMenuItem("md", onClick = {}, textValue = "Medium")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Controlled",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        ContextMenu(open = open, onOpenChange = { open = it }) {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {})
                                ContextMenuItem("Reload", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        ContextMenu(open = open, onOpenChange = { open = it }) {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {})
                                ContextMenuItem("Reload", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Uncontrolled",
                    code =
                        """
                        ContextMenu(defaultOpen = false) {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {})
                                ContextMenuItem("Reload", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu(defaultOpen = false) {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {})
                                ContextMenuItem("Reload", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default open",
                    code =
                        """
                        ContextMenu(defaultOpen = false) {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {})
                                ContextMenuItem("Reload", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu(defaultOpen = false) {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {})
                                ContextMenuItem("Reload", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Portal",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuPortal {
                                ContextMenuContent {
                                    ContextMenuItem("Back", onClick = {})
                                    ContextMenuItem("Reload", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuPortal {
                                ContextMenuContent {
                                    ContextMenuItem("Back", onClick = {})
                                    ContextMenuItem("Reload", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Group heading",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuGroup(heading = "History") {
                                    ContextMenuItem("Back", onClick = {})
                                    ContextMenuItem("Reload", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuGroup(heading = "History") {
                                    ContextMenuItem("Back", onClick = {})
                                    ContextMenuItem("Reload", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Label",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuLabel("History")
                                ContextMenuItem("Back", onClick = {})
                                ContextMenuItem("Reload", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuLabel("History")
                                ContextMenuItem("Back", onClick = {})
                                ContextMenuItem("Reload", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Separator",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {})
                                ContextMenuSeparator()
                                ContextMenuItem("Reload", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {})
                                ContextMenuSeparator()
                                ContextMenuItem("Reload", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Shortcut",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Print", onClick = {}, shortcut = "⌘P")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Print", onClick = {}, shortcut = "⌘P")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Group",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuGroup {
                                    ContextMenuItem("Back", onClick = {})
                                    ContextMenuItem("Reload", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuGroup {
                                    ContextMenuItem("Back", onClick = {})
                                    ContextMenuItem("Reload", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Trigger",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Item",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Reload", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Reload", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Content",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {})
                                ContextMenuItem("Reload", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {})
                                ContextMenuItem("Reload", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Checkbox item",
                    code =
                        """
                        var showStatus by remember { mutableStateOf(true) }
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuCheckboxItem("Status Bar", checked = showStatus) {
                                    showStatus = it
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var showStatus by remember { mutableStateOf(true) }
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuCheckboxItem(
                                    label = "Status Bar",
                                    checked = showStatus,
                                    onCheckedChange = { showStatus = it },
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default indeterminate",
                    code =
                        """
                        var showStatus by remember { mutableStateOf(true) }
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuCheckboxItem(
                                    "Status Bar",
                                    checked = showStatus,
                                    indeterminate = false,
                                ) {
                                    showStatus = it
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var showStatus by remember { mutableStateOf(true) }
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuCheckboxItem(
                                    label = "Status Bar",
                                    checked = showStatus,
                                    indeterminate = false,
                                    onCheckedChange = { showStatus = it },
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Radio item",
                    code =
                        """
                        var panel by remember { mutableStateOf("bottom") }
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuRadioGroup(value = panel, onValueChange = { panel = it }) {
                                    ContextMenuRadioItem(value = "bottom", label = "Bottom")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var panel by remember { mutableStateOf("bottom") }
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuRadioGroup(value = panel, onValueChange = { panel = it }) {
                                    ContextMenuRadioItem(value = "bottom", label = "Bottom")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Sub",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuSub {
                                    ContextMenuSubTrigger("More")
                                    ContextMenuSubContent {
                                        ContextMenuItem("Save", onClick = {})
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuSub {
                                    ContextMenuSubTrigger("More")
                                    ContextMenuSubContent {
                                        ContextMenuItem("Save", onClick = {})
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Sub trigger",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuSub {
                                    ContextMenuSubTrigger("More")
                                    ContextMenuSubContent {
                                        ContextMenuItem("Save", onClick = {})
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuSub {
                                    ContextMenuSubTrigger("More")
                                    ContextMenuSubContent {
                                        ContextMenuItem("Save", onClick = {})
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Sub content",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuSub {
                                    ContextMenuSubTrigger("More")
                                    ContextMenuSubContent {
                                        ContextMenuItem("Save", onClick = {})
                                        ContextMenuItem("Export", onClick = {})
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuSub {
                                    ContextMenuSubTrigger("More")
                                    ContextMenuSubContent {
                                        ContextMenuItem("Save", onClick = {})
                                        ContextMenuItem("Export", onClick = {})
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Side offset",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuSub {
                                    ContextMenuSubTrigger("More")
                                    ContextMenuSubContent(sideOffset = 0.dp) {
                                        ContextMenuItem("Save", onClick = {})
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuSub {
                                    ContextMenuSubTrigger("More")
                                    ContextMenuSubContent(sideOffset = 0.dp) {
                                        ContextMenuItem("Save", onClick = {})
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default side offset",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuSub {
                                    ContextMenuSubTrigger("More")
                                    ContextMenuSubContent(sideOffset = 0.dp) {
                                        ContextMenuItem("Save", onClick = {})
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger("Right-click me")
                            ContextMenuContent {
                                ContextMenuSub {
                                    ContextMenuSubTrigger("More")
                                    ContextMenuSubContent(sideOffset = 0.dp) {
                                        ContextMenuItem("Save", onClick = {})
                                    }
                                }
                            }
                        }
                    },
                ),
            ),
    )
