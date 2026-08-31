@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuItemVariant
import com.toutiaoyan.compose.ui.components.menubar.Menubar
import com.toutiaoyan.compose.ui.components.menubar.MenubarCheckboxItem
import com.toutiaoyan.compose.ui.components.menubar.MenubarContent
import com.toutiaoyan.compose.ui.components.menubar.MenubarGroup
import com.toutiaoyan.compose.ui.components.menubar.MenubarItem
import com.toutiaoyan.compose.ui.components.menubar.MenubarLabel
import com.toutiaoyan.compose.ui.components.menubar.MenubarMenu
import com.toutiaoyan.compose.ui.components.menubar.MenubarPortal
import com.toutiaoyan.compose.ui.components.menubar.MenubarRadioGroup
import com.toutiaoyan.compose.ui.components.menubar.MenubarRadioItem
import com.toutiaoyan.compose.ui.components.menubar.MenubarSeparator
import com.toutiaoyan.compose.ui.components.menubar.MenubarSub
import com.toutiaoyan.compose.ui.components.menubar.MenubarSubContent
import com.toutiaoyan.compose.ui.components.menubar.MenubarSubTrigger
import com.toutiaoyan.compose.ui.components.menubar.MenubarTrigger

val menubarDoc =
    ComponentDoc(
        id = "menubar",
        title = "Menubar",
        description = "A horizontal row of menu triggers, desktop-app style.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.menubar.Menubar
            import com.toutiaoyan.compose.ui.components.menubar.MenubarContent
            import com.toutiaoyan.compose.ui.components.menubar.MenubarItem
            import com.toutiaoyan.compose.ui.components.menubar.MenubarMenu
            import com.toutiaoyan.compose.ui.components.menubar.MenubarSeparator
            import com.toutiaoyan.compose.ui.components.menubar.MenubarTrigger

            Menubar {
                MenubarMenu(value = "file") {
                    MenubarTrigger("File")
                    MenubarContent {
                        MenubarItem("New Tab", onClick = {})
                        MenubarSeparator()
                        MenubarItem("Close Window", onClick = {}, destructive = true)
                    }
                }
                MenubarMenu(value = "edit") {
                    MenubarTrigger("Edit")
                    MenubarContent {
                        MenubarItem("Undo", onClick = {})
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
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                    MenubarItem("New Window", onClick = {})
                                    MenubarSeparator()
                                    MenubarItem("Close Window", onClick = {}, destructive = true)
                                }
                            }
                            MenubarMenu(value = "edit") {
                                MenubarTrigger("Edit")
                                MenubarContent {
                                    MenubarItem("Undo", onClick = {})
                                    MenubarItem("Redo", onClick = {})
                                }
                            }
                            MenubarMenu(value = "view") {
                                MenubarTrigger("View")
                                MenubarContent {
                                    MenubarItem("Zoom In", onClick = {})
                                    MenubarItem("Zoom Out", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                    MenubarItem("New Window", onClick = {})
                                    MenubarSeparator()
                                    MenubarItem("Close Window", onClick = {}, destructive = true)
                                }
                            }
                            MenubarMenu(value = "edit") {
                                MenubarTrigger("Edit")
                                MenubarContent {
                                    MenubarItem("Undo", onClick = {})
                                    MenubarItem("Redo", onClick = {})
                                }
                            }
                            MenubarMenu(value = "view") {
                                MenubarTrigger("View")
                                MenubarContent {
                                    MenubarItem("Zoom In", onClick = {})
                                    MenubarItem("Zoom Out", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Checkboxes and radio",
                    code =
                        """
                        var showStatus by remember { mutableStateOf(true) }
                        var panel by remember { mutableStateOf("bottom") }
                        Menubar {
                            MenubarMenu(value = "view") {
                                MenubarTrigger("View")
                                MenubarContent {
                                    MenubarCheckboxItem("Status Bar", checked = showStatus) {
                                        showStatus = it
                                    }
                                    MenubarSeparator()
                                    MenubarRadioGroup(value = panel, onValueChange = { panel = it }) {
                                        MenubarRadioItem(value = "top", label = "Top")
                                        MenubarRadioItem(value = "bottom", label = "Bottom")
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var showStatus by remember { mutableStateOf(true) }
                        var panel by remember { mutableStateOf("bottom") }
                        Menubar {
                            MenubarMenu(value = "view") {
                                MenubarTrigger("View")
                                MenubarContent {
                                    MenubarCheckboxItem("Status Bar", checked = showStatus) {
                                        showStatus = it
                                    }
                                    MenubarSeparator()
                                    MenubarRadioGroup(value = panel, onValueChange = { panel = it }) {
                                        MenubarRadioItem(value = "top", label = "Top")
                                        MenubarRadioItem(value = "bottom", label = "Bottom")
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Submenu",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                    MenubarSub {
                                        MenubarSubTrigger("Share")
                                        MenubarSubContent {
                                            MenubarItem("Email", onClick = {})
                                            MenubarItem("Messages", onClick = {})
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                    MenubarSub {
                                        MenubarSubTrigger("Share")
                                        MenubarSubContent {
                                            MenubarItem("Email", onClick = {})
                                            MenubarItem("Messages", onClick = {})
                                        }
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
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {}, shortcut = "⌘T")
                                    MenubarItem("New Window", onClick = {}, shortcut = "⌘N")
                                    MenubarItem("Print", onClick = {}, shortcut = "⌘P")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {}, shortcut = "⌘T")
                                    MenubarItem("New Window", onClick = {}, shortcut = "⌘N")
                                    MenubarItem("Print", onClick = {}, shortcut = "⌘P")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {}, shortcut = "⌘T")
                                    MenubarItem("New Window", onClick = {}, disabled = true)
                                    MenubarItem("Print", onClick = {}, shortcut = "⌘P")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {}, shortcut = "⌘T")
                                    MenubarItem("New Window", onClick = {}, disabled = true)
                                    MenubarItem("Print", onClick = {}, shortcut = "⌘P")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {}, shortcut = "⌘T")
                                    MenubarItem("New Window", onClick = {}, disabled = false)
                                    MenubarItem("Print", onClick = {}, shortcut = "⌘P")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {}, shortcut = "⌘T")
                                    MenubarItem("New Window", onClick = {}, disabled = false)
                                    MenubarItem("Print", onClick = {}, shortcut = "⌘P")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default destructive",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {}, destructive = false)
                                    MenubarItem("Print", onClick = {}, destructive = false)
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {}, destructive = false)
                                    MenubarItem("Print", onClick = {}, destructive = false)
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default variant",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {}, variant = DropdownMenuItemVariant.Default)
                                    MenubarItem("Print", onClick = {}, variant = DropdownMenuItemVariant.Default)
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {}, variant = DropdownMenuItemVariant.Default)
                                    MenubarItem("Print", onClick = {}, variant = DropdownMenuItemVariant.Default)
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Inset",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "view") {
                                MenubarTrigger("View")
                                MenubarContent {
                                    MenubarLabel("Appearance", inset = true)
                                    MenubarSeparator()
                                    MenubarItem("Reset", onClick = {}, inset = true)
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "view") {
                                MenubarTrigger("View")
                                MenubarContent {
                                    MenubarLabel("Appearance", inset = true)
                                    MenubarSeparator()
                                    MenubarItem("Reset", onClick = {}, inset = true)
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default inset",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "view") {
                                MenubarTrigger("View")
                                MenubarContent {
                                    MenubarLabel("Appearance", inset = false)
                                    MenubarItem("Reset", onClick = {}, inset = false)
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "view") {
                                MenubarTrigger("View")
                                MenubarContent {
                                    MenubarLabel("Appearance", inset = false)
                                    MenubarItem("Reset", onClick = {}, inset = false)
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Non-modal",
                    code =
                        """
                        Menubar(modal = false) {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                    MenubarItem("New Window", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar(modal = false) {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                    MenubarItem("New Window", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Modal",
                    code =
                        """
                        Menubar(modal = true) {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar(modal = true) {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default modal",
                    code =
                        """
                        Menubar(modal = true) {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar(modal = true) {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Controlled",
                    code =
                        """
                        var open by remember { mutableStateOf<String?>(null) }
                        Menubar(value = open, onValueChange = { open = it }) {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                    MenubarItem("New Window", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf<String?>(null) }
                        Menubar(value = open, onValueChange = { open = it }) {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                    MenubarItem("New Window", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Undismissible",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent(dismissOnClickOutside = false) {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent(dismissOnClickOutside = false) {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default dismiss",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent(dismissOnClickOutside = true) {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent(dismissOnClickOutside = true) {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Keep open on select",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("Stay open", onClick = {}, onSelect = { true })
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("Stay open", onClick = {}, onSelect = { true })
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Accessible label",
                    code =
                        """
                        Menubar(ariaLabel = "Application") {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar(ariaLabel = "Application") {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default aria label",
                    code =
                        """
                        Menubar(ariaLabel = null) {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar(ariaLabel = null) {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Custom text value",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("sm", onClick = {}, textValue = "Small")
                                    MenubarItem("md", onClick = {}, textValue = "Medium")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("sm", onClick = {}, textValue = "Small")
                                    MenubarItem("md", onClick = {}, textValue = "Medium")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Portal",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarPortal {
                                    MenubarContent {
                                        MenubarItem("New Tab", onClick = {})
                                        MenubarItem("New Window", onClick = {})
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarPortal {
                                    MenubarContent {
                                        MenubarItem("New Tab", onClick = {})
                                        MenubarItem("New Window", onClick = {})
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Group heading",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarGroup(heading = "File") {
                                        MenubarItem("New Tab", onClick = {})
                                        MenubarItem("New Window", onClick = {})
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarGroup(heading = "File") {
                                        MenubarItem("New Tab", onClick = {})
                                        MenubarItem("New Window", onClick = {})
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Label",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarLabel("File")
                                    MenubarItem("New Tab", onClick = {})
                                    MenubarItem("New Window", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarLabel("File")
                                    MenubarItem("New Tab", onClick = {})
                                    MenubarItem("New Window", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Separator",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                    MenubarSeparator()
                                    MenubarItem("New Window", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                    MenubarSeparator()
                                    MenubarItem("New Window", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Shortcut",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("Print", onClick = {}, shortcut = "⌘P")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("Print", onClick = {}, shortcut = "⌘P")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Menu",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Trigger",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Content",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                    MenubarItem("New Window", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                    MenubarItem("New Window", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Side offset",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent(sideOffset = 8.dp) {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent(sideOffset = 8.dp) {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default side offset",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent(sideOffset = 8.dp) {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent(sideOffset = 8.dp) {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Group",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarGroup {
                                        MenubarItem("New Tab", onClick = {})
                                        MenubarItem("New Window", onClick = {})
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarGroup {
                                        MenubarItem("New Tab", onClick = {})
                                        MenubarItem("New Window", onClick = {})
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Item",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Checkbox item",
                    code =
                        """
                        var showStatus by remember { mutableStateOf(true) }
                        Menubar {
                            MenubarMenu(value = "view") {
                                MenubarTrigger("View")
                                MenubarContent {
                                    MenubarCheckboxItem("Status Bar", checked = showStatus) {
                                        showStatus = it
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var showStatus by remember { mutableStateOf(true) }
                        Menubar {
                            MenubarMenu(value = "view") {
                                MenubarTrigger("View")
                                MenubarContent {
                                    MenubarCheckboxItem("Status Bar", checked = showStatus) {
                                        showStatus = it
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default indeterminate",
                    code =
                        """
                        var showStatus by remember { mutableStateOf(true) }
                        Menubar {
                            MenubarMenu(value = "view") {
                                MenubarTrigger("View")
                                MenubarContent {
                                    MenubarCheckboxItem(
                                        "Status Bar",
                                        checked = showStatus,
                                        indeterminate = false,
                                    ) {
                                        showStatus = it
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var showStatus by remember { mutableStateOf(true) }
                        Menubar {
                            MenubarMenu(value = "view") {
                                MenubarTrigger("View")
                                MenubarContent {
                                    MenubarCheckboxItem(
                                        "Status Bar",
                                        checked = showStatus,
                                        indeterminate = false,
                                    ) {
                                        showStatus = it
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Radio group",
                    code =
                        """
                        var panel by remember { mutableStateOf("bottom") }
                        Menubar {
                            MenubarMenu(value = "view") {
                                MenubarTrigger("View")
                                MenubarContent {
                                    MenubarRadioGroup(value = panel, onValueChange = { panel = it }) {
                                        MenubarRadioItem(value = "top", label = "Top")
                                        MenubarRadioItem(value = "bottom", label = "Bottom")
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var panel by remember { mutableStateOf("bottom") }
                        Menubar {
                            MenubarMenu(value = "view") {
                                MenubarTrigger("View")
                                MenubarContent {
                                    MenubarRadioGroup(value = panel, onValueChange = { panel = it }) {
                                        MenubarRadioItem(value = "top", label = "Top")
                                        MenubarRadioItem(value = "bottom", label = "Bottom")
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Radio item",
                    code =
                        """
                        var panel by remember { mutableStateOf("bottom") }
                        Menubar {
                            MenubarMenu(value = "view") {
                                MenubarTrigger("View")
                                MenubarContent {
                                    MenubarRadioGroup(value = panel, onValueChange = { panel = it }) {
                                        MenubarRadioItem(value = "bottom", label = "Bottom")
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var panel by remember { mutableStateOf("bottom") }
                        Menubar {
                            MenubarMenu(value = "view") {
                                MenubarTrigger("View")
                                MenubarContent {
                                    MenubarRadioGroup(value = panel, onValueChange = { panel = it }) {
                                        MenubarRadioItem(value = "bottom", label = "Bottom")
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Sub",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarSub {
                                        MenubarSubTrigger("Share")
                                        MenubarSubContent {
                                            MenubarItem("Email", onClick = {})
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarSub {
                                        MenubarSubTrigger("Share")
                                        MenubarSubContent {
                                            MenubarItem("Email", onClick = {})
                                        }
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
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarSub {
                                        MenubarSubTrigger("Share")
                                        MenubarSubContent {
                                            MenubarItem("Email", onClick = {})
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarSub {
                                        MenubarSubTrigger("Share")
                                        MenubarSubContent {
                                            MenubarItem("Email", onClick = {})
                                        }
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
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarSub {
                                        MenubarSubTrigger("Share")
                                        MenubarSubContent {
                                            MenubarItem("Email", onClick = {})
                                            MenubarItem("Messages", onClick = {})
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarSub {
                                        MenubarSubTrigger("Share")
                                        MenubarSubContent {
                                            MenubarItem("Email", onClick = {})
                                            MenubarItem("Messages", onClick = {})
                                        }
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Sub side offset",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarSub {
                                        MenubarSubTrigger("Share")
                                        MenubarSubContent(sideOffset = 0.dp) {
                                            MenubarItem("Email", onClick = {})
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarSub {
                                        MenubarSubTrigger("Share")
                                        MenubarSubContent(sideOffset = 0.dp) {
                                            MenubarItem("Email", onClick = {})
                                        }
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default sub side offset",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarSub {
                                        MenubarSubTrigger("Share")
                                        MenubarSubContent(sideOffset = 0.dp) {
                                            MenubarItem("Email", onClick = {})
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarSub {
                                        MenubarSubTrigger("Share")
                                        MenubarSubContent(sideOffset = 0.dp) {
                                            MenubarItem("Email", onClick = {})
                                        }
                                    }
                                }
                            }
                        }
                    },
                ),
            ),
    )
