@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenu
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuCheckboxItem
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuContent
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuGroup
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuItem
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuItemVariant
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuLabel
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuPortal
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuRadioGroup
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuRadioItem
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuSeparator
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuSub
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuSubContent
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuSubTrigger
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuTrigger
import com.toutiaoyan.compose.ui.components.overlay.PopupPlacement

val dropdownMenuDoc =
    ComponentDoc(
        id = "dropdown-menu",
        title = "Dropdown Menu",
        description = "An anchored list of actions, optionally grouped under labels and separators.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenu
            import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuContent
            import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuGroup
            import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuItemVariant
            import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuTrigger

            var open by remember { mutableStateOf(false) }
            DropdownMenu(open = open, onOpenChange = { open = it }) {
                DropdownMenuTrigger("Open")
                DropdownMenuContent {
                    DropdownMenuGroup {
                        DropdownMenuLabel("My Account")
                        DropdownMenuSeparator()
                        DropdownMenuItem("Profile", onClick = {})
                        DropdownMenuItem("Billing", onClick = {})
                    }
                    DropdownMenuSeparator()
                    DropdownMenuItem("Log out", onClick = {}, variant = DropdownMenuItemVariant.Destructive, shortcut = "⇧⌘Q")
                }
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuLabel("My Account")
                                DropdownMenuSeparator()
                                DropdownMenuItem("Profile", onClick = {})
                                DropdownMenuItem("Billing", onClick = {})
                                DropdownMenuItem("Team", onClick = {})
                                DropdownMenuSeparator()
                                DropdownMenuItem("Log out", onClick = {}, variant = DropdownMenuItemVariant.Destructive)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuLabel("My Account")
                                DropdownMenuSeparator()
                                DropdownMenuItem("Profile", onClick = {})
                                DropdownMenuItem("Billing", onClick = {})
                                DropdownMenuItem("Team", onClick = {})
                                DropdownMenuSeparator()
                                DropdownMenuItem("Log out", onClick = {}, variant = DropdownMenuItemVariant.Destructive)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Checkboxes",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var showStatus by remember { mutableStateOf(true) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("View")
                            DropdownMenuContent {
                                DropdownMenuLabel("Appearance")
                                DropdownMenuSeparator()
                                DropdownMenuCheckboxItem("Status Bar", checked = showStatus) {
                                    showStatus = it
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var showStatus by remember { mutableStateOf(true) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("View")
                            DropdownMenuContent {
                                DropdownMenuLabel("Appearance")
                                DropdownMenuSeparator()
                                DropdownMenuCheckboxItem(
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
                        var open by remember { mutableStateOf(false) }
                        var panel by remember { mutableStateOf("bottom") }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Panel")
                            DropdownMenuContent {
                                DropdownMenuLabel("Panel Position")
                                DropdownMenuSeparator()
                                DropdownMenuRadioGroup(value = panel, onValueChange = { panel = it }) {
                                    DropdownMenuRadioItem(value = "top", label = "Top")
                                    DropdownMenuRadioItem(value = "bottom", label = "Bottom")
                                    DropdownMenuRadioItem(value = "right", label = "Right")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var panel by remember { mutableStateOf("bottom") }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Panel")
                            DropdownMenuContent {
                                DropdownMenuLabel("Panel Position")
                                DropdownMenuSeparator()
                                DropdownMenuRadioGroup(value = panel, onValueChange = { panel = it }) {
                                    DropdownMenuRadioItem(value = "top", label = "Top")
                                    DropdownMenuRadioItem(value = "bottom", label = "Bottom")
                                    DropdownMenuRadioItem(value = "right", label = "Right")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Submenu",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Profile", onClick = {})
                                DropdownMenuSub {
                                    DropdownMenuSubTrigger("More")
                                    DropdownMenuSubContent {
                                        DropdownMenuItem("Save", onClick = {})
                                        DropdownMenuItem("Export", onClick = {})
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Profile", onClick = {})
                                DropdownMenuSub {
                                    DropdownMenuSubTrigger("More")
                                    DropdownMenuSubContent {
                                        DropdownMenuItem("Save", onClick = {})
                                        DropdownMenuItem("Export", onClick = {})
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Inset",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var showStatus by remember { mutableStateOf(true) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("View")
                            DropdownMenuContent {
                                DropdownMenuLabel("Appearance", inset = true)
                                DropdownMenuSeparator()
                                DropdownMenuCheckboxItem("Status Bar", checked = showStatus) {
                                    showStatus = it
                                }
                                DropdownMenuItem("Reset", onClick = {}, inset = true)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var showStatus by remember { mutableStateOf(true) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("View")
                            DropdownMenuContent {
                                DropdownMenuLabel("Appearance", inset = true)
                                DropdownMenuSeparator()
                                DropdownMenuCheckboxItem(
                                    label = "Status Bar",
                                    checked = showStatus,
                                    onCheckedChange = { showStatus = it },
                                )
                                DropdownMenuItem("Reset", onClick = {}, inset = true)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default inset",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("View")
                            DropdownMenuContent {
                                DropdownMenuLabel("Appearance", inset = false)
                                DropdownMenuItem("Reset", onClick = {}, inset = false)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("View")
                            DropdownMenuContent {
                                DropdownMenuLabel("Appearance", inset = false)
                                DropdownMenuItem("Reset", onClick = {}, inset = false)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Shortcuts",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Profile", onClick = {}, shortcut = "⇧⌘P")
                                DropdownMenuItem("Billing", onClick = {}, shortcut = "⌘B")
                                DropdownMenuSeparator()
                                DropdownMenuItem("Log out", onClick = {}, shortcut = "⇧⌘Q", destructive = true)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Profile", onClick = {}, shortcut = "⇧⌘P")
                                DropdownMenuItem("Billing", onClick = {}, shortcut = "⌘B")
                                DropdownMenuSeparator()
                                DropdownMenuItem("Log out", onClick = {}, shortcut = "⇧⌘Q", destructive = true)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default destructive",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Profile", onClick = {}, destructive = false)
                                DropdownMenuItem("Billing", onClick = {}, destructive = false)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Profile", onClick = {}, destructive = false)
                                DropdownMenuItem("Billing", onClick = {}, destructive = false)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default variant",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Profile", onClick = {}, variant = DropdownMenuItemVariant.Default)
                                DropdownMenuItem("Billing", onClick = {}, variant = DropdownMenuItemVariant.Default)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Profile", onClick = {}, variant = DropdownMenuItemVariant.Default)
                                DropdownMenuItem("Billing", onClick = {}, variant = DropdownMenuItemVariant.Default)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Profile", onClick = {})
                                    DropdownMenuItem("Billing", onClick = {}, disabled = true)
                                DropdownMenuItem("Team", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Profile", onClick = {})
                                DropdownMenuItem("Billing", onClick = {}, disabled = true)
                                DropdownMenuItem("Team", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Profile", onClick = {}, disabled = false)
                                DropdownMenuItem("Billing", onClick = {}, disabled = false)
                                DropdownMenuItem("Team", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Profile", onClick = {}, disabled = false)
                                DropdownMenuItem("Billing", onClick = {}, disabled = false)
                                DropdownMenuItem("Team", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Non-modal",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }, modal = false) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Profile", onClick = {})
                                DropdownMenuItem("Billing", onClick = {})
                                DropdownMenuItem("Team", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }, modal = false) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Profile", onClick = {})
                                DropdownMenuItem("Billing", onClick = {})
                                DropdownMenuItem("Team", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Modal",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }, modal = true) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Profile", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }, modal = true) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Profile", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default modal",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }, modal = true) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Profile", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }, modal = true) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Profile", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Non-dismissible",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent(dismissOnClickOutside = false) {
                                DropdownMenuItem("Profile", onClick = {})
                                DropdownMenuItem("Billing", onClick = {})
                                DropdownMenuItem("Team", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent(dismissOnClickOutside = false) {
                                DropdownMenuItem("Profile", onClick = {})
                                DropdownMenuItem("Billing", onClick = {})
                                DropdownMenuItem("Team", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default dismiss",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent(dismissOnClickOutside = true) {
                                DropdownMenuItem("Profile", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent(dismissOnClickOutside = true) {
                                DropdownMenuItem("Profile", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Group heading",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuGroup(heading = "My Account") {
                                    DropdownMenuItem("Profile", onClick = {})
                                    DropdownMenuItem("Billing", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuGroup(heading = "My Account") {
                                    DropdownMenuItem("Profile", onClick = {})
                                    DropdownMenuItem("Billing", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Content side",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent(side = PopupPlacement.Top) {
                                DropdownMenuItem("Profile", onClick = {})
                                DropdownMenuItem("Billing", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent(side = PopupPlacement.Top) {
                                DropdownMenuItem("Profile", onClick = {})
                                DropdownMenuItem("Billing", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Placement",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent(placement = PopupPlacement.Top) {
                                DropdownMenuItem("Profile", onClick = {})
                                DropdownMenuItem("Billing", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent(placement = PopupPlacement.Top) {
                                DropdownMenuItem("Profile", onClick = {})
                                DropdownMenuItem("Billing", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default side",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent(side = PopupPlacement.Bottom) {
                                DropdownMenuItem("Profile", onClick = {})
                                DropdownMenuItem("Billing", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent(side = PopupPlacement.Bottom) {
                                DropdownMenuItem("Profile", onClick = {})
                                DropdownMenuItem("Billing", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default placement",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent(placement = PopupPlacement.Bottom) {
                                DropdownMenuItem("Profile", onClick = {})
                                DropdownMenuItem("Billing", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent(placement = PopupPlacement.Bottom) {
                                DropdownMenuItem("Profile", onClick = {})
                                DropdownMenuItem("Billing", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Indeterminate checkbox",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("View")
                            DropdownMenuContent {
                                DropdownMenuCheckboxItem(
                                    "Select all",
                                    checked = false,
                                    indeterminate = true,
                                    onCheckedChange = {},
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("View")
                            DropdownMenuContent {
                                DropdownMenuCheckboxItem(
                                    "Select all",
                                    checked = false,
                                    indeterminate = true,
                                    onCheckedChange = {},
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default indeterminate",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("View")
                            DropdownMenuContent {
                                DropdownMenuCheckboxItem(
                                    "Status Bar",
                                    checked = false,
                                    indeterminate = false,
                                    onCheckedChange = {},
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("View")
                            DropdownMenuContent {
                                DropdownMenuCheckboxItem(
                                    "Status Bar",
                                    checked = false,
                                    indeterminate = false,
                                    onCheckedChange = {},
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Keep open on select",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Stay open", onClick = {}, onSelect = { true })
                                DropdownMenuItem("Close", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Stay open", onClick = {}, onSelect = { true })
                                DropdownMenuItem("Close", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Prevent checkbox toggle",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("View")
                            DropdownMenuContent {
                                DropdownMenuCheckboxItem(
                                    "Locked option",
                                    checked = true,
                                    onCheckedChange = {},
                                    onSelect = { true },
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("View")
                            DropdownMenuContent {
                                DropdownMenuCheckboxItem(
                                    "Locked option",
                                    checked = true,
                                    onCheckedChange = {},
                                    onSelect = { true },
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Custom text value",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("⌘N", onClick = {}, textValue = "New file")
                                DropdownMenuItem("⌘O", onClick = {}, textValue = "Open file")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("⌘N", onClick = {}, textValue = "New file")
                                DropdownMenuItem("⌘O", onClick = {}, textValue = "Open file")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Abbreviated text value",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("sm", onClick = {}, textValue = "Small")
                                DropdownMenuItem("md", onClick = {}, textValue = "Medium")
                                DropdownMenuItem("lg", onClick = {}, textValue = "Large")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("sm", onClick = {}, textValue = "Small")
                                DropdownMenuItem("md", onClick = {}, textValue = "Medium")
                                DropdownMenuItem("lg", onClick = {}, textValue = "Large")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Checkbox text value",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("View")
                            DropdownMenuContent {
                                DropdownMenuCheckboxItem(
                                    "sm",
                                    checked = true,
                                    onCheckedChange = {},
                                    textValue = "Small",
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        DropdownMenu(open = open, onOpenChange = { open = it }) {
                            DropdownMenuTrigger("View")
                            DropdownMenuContent {
                                DropdownMenuCheckboxItem(
                                    "sm",
                                    checked = true,
                                    onCheckedChange = {},
                                    textValue = "Small",
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Uncontrolled",
                    code =
                        """
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Profile", onClick = {})
                                DropdownMenuItem("Log out", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Profile", onClick = {})
                                DropdownMenuItem("Log out", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default open",
                    code =
                        """
                        DropdownMenu(defaultOpen = false) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Profile", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        DropdownMenu(defaultOpen = false) {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Profile", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Portal",
                    code =
                        """
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuPortal {
                                DropdownMenuContent {
                                    DropdownMenuItem("Profile", onClick = {})
                                    DropdownMenuItem("Log out", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuPortal {
                                DropdownMenuContent {
                                    DropdownMenuItem("Profile", onClick = {})
                                    DropdownMenuItem("Log out", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Label",
                    code =
                        """
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuLabel("My Account")
                                DropdownMenuItem("Profile", onClick = {})
                                DropdownMenuItem("Billing", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuLabel("My Account")
                                DropdownMenuItem("Profile", onClick = {})
                                DropdownMenuItem("Billing", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Separator",
                    code =
                        """
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Profile", onClick = {})
                                DropdownMenuSeparator()
                                DropdownMenuItem("Log out", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Profile", onClick = {})
                                DropdownMenuSeparator()
                                DropdownMenuItem("Log out", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Shortcut",
                    code =
                        """
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Print", onClick = {}, shortcut = "⌘P")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Print", onClick = {}, shortcut = "⌘P")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Group",
                    code =
                        """
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuGroup {
                                    DropdownMenuItem("Profile", onClick = {})
                                    DropdownMenuItem("Billing", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuGroup {
                                    DropdownMenuItem("Profile", onClick = {})
                                    DropdownMenuItem("Billing", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Trigger",
                    code =
                        """
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Profile", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Profile", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Content",
                    code =
                        """
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Profile", onClick = {})
                                DropdownMenuItem("Billing", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Profile", onClick = {})
                                DropdownMenuItem("Billing", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Side offset",
                    code =
                        """
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent(sideOffset = 4.dp) {
                                DropdownMenuItem("Profile", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent(sideOffset = 4.dp) {
                                DropdownMenuItem("Profile", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default side offset",
                    code =
                        """
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent(sideOffset = 4.dp) {
                                DropdownMenuItem("Profile", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent(sideOffset = 4.dp) {
                                DropdownMenuItem("Profile", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Item",
                    code =
                        """
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Profile", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("Profile", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Checkbox item",
                    code =
                        """
                        var showStatus by remember { mutableStateOf(true) }
                        DropdownMenu {
                            DropdownMenuTrigger("View")
                            DropdownMenuContent {
                                DropdownMenuCheckboxItem("Status Bar", checked = showStatus) {
                                    showStatus = it
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var showStatus by remember { mutableStateOf(true) }
                        DropdownMenu {
                            DropdownMenuTrigger("View")
                            DropdownMenuContent {
                                DropdownMenuCheckboxItem(
                                    label = "Status Bar",
                                    checked = showStatus,
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
                        DropdownMenu {
                            DropdownMenuTrigger("Panel")
                            DropdownMenuContent {
                                DropdownMenuRadioGroup(value = panel, onValueChange = { panel = it }) {
                                    DropdownMenuRadioItem(value = "bottom", label = "Bottom")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var panel by remember { mutableStateOf("bottom") }
                        DropdownMenu {
                            DropdownMenuTrigger("Panel")
                            DropdownMenuContent {
                                DropdownMenuRadioGroup(value = panel, onValueChange = { panel = it }) {
                                    DropdownMenuRadioItem(value = "bottom", label = "Bottom")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Sub",
                    code =
                        """
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuSub {
                                    DropdownMenuSubTrigger("More")
                                    DropdownMenuSubContent {
                                        DropdownMenuItem("Save", onClick = {})
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuSub {
                                    DropdownMenuSubTrigger("More")
                                    DropdownMenuSubContent {
                                        DropdownMenuItem("Save", onClick = {})
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
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuSub {
                                    DropdownMenuSubTrigger("More")
                                    DropdownMenuSubContent {
                                        DropdownMenuItem("Save", onClick = {})
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuSub {
                                    DropdownMenuSubTrigger("More")
                                    DropdownMenuSubContent {
                                        DropdownMenuItem("Save", onClick = {})
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
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuSub {
                                    DropdownMenuSubTrigger("More")
                                    DropdownMenuSubContent {
                                        DropdownMenuItem("Save", onClick = {})
                                        DropdownMenuItem("Export", onClick = {})
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuSub {
                                    DropdownMenuSubTrigger("More")
                                    DropdownMenuSubContent {
                                        DropdownMenuItem("Save", onClick = {})
                                        DropdownMenuItem("Export", onClick = {})
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
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuSub {
                                    DropdownMenuSubTrigger("More")
                                    DropdownMenuSubContent(sideOffset = 4.dp) {
                                        DropdownMenuItem("Save", onClick = {})
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuSub {
                                    DropdownMenuSubTrigger("More")
                                    DropdownMenuSubContent(sideOffset = 4.dp) {
                                        DropdownMenuItem("Save", onClick = {})
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
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuSub {
                                    DropdownMenuSubTrigger("More")
                                    DropdownMenuSubContent(sideOffset = 4.dp) {
                                        DropdownMenuItem("Save", onClick = {})
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuSub {
                                    DropdownMenuSubTrigger("More")
                                    DropdownMenuSubContent(sideOffset = 4.dp) {
                                        DropdownMenuItem("Save", onClick = {})
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default sub placement",
                    code =
                        """
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuSub {
                                    DropdownMenuSubTrigger("More")
                                    DropdownMenuSubContent(placement = PopupPlacement.End) {
                                        DropdownMenuItem("Save", onClick = {})
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuSub {
                                    DropdownMenuSubTrigger("More")
                                    DropdownMenuSubContent(placement = PopupPlacement.End) {
                                        DropdownMenuItem("Save", onClick = {})
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default sub side",
                    code =
                        """
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuSub {
                                    DropdownMenuSubTrigger("More")
                                    DropdownMenuSubContent(side = PopupPlacement.End) {
                                        DropdownMenuItem("Save", onClick = {})
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuSub {
                                    DropdownMenuSubTrigger("More")
                                    DropdownMenuSubContent(side = PopupPlacement.End) {
                                        DropdownMenuItem("Save", onClick = {})
                                    }
                                }
                            }
                        }
                    },
                ),
            ),
    )
