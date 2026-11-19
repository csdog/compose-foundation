@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.style.Style
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenu
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuCheckboxItem
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuContent
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuGroup
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuItem
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuLabel
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuRadioGroup
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuRadioItem
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuSeparator
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuSub
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuSubContent
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuSubTrigger
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuTrigger

private val dropdownMenuExampleOrder =
    listOf(
        "Default",
        "Basic",
        "Submenu",
        "Shortcuts",
        "Icons",
        "Checkboxes",
        "Checkboxes Icons",
        "Radio Group",
        "Radio Icons",
        "Destructive",
        "Avatar",
        "Complex",
        "RTL",
    )

val dropdownMenuDoc =
    ComponentDoc(
        id = "dropdown-menu",
        title = "Dropdown Menu",
        description = "Displays a menu to the user — such as a set of actions or functions — triggered by a button.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.dropdownmenu.*
            import com.toutiaoyan.compose.ui.components.button.ButtonVariant
            import androidx.compose.foundation.style.Style

            var open by remember { mutableStateOf(false) }
            DropdownMenu(open = open, onOpenChange = { open = it }) {
                DropdownMenuTrigger("Open", variant = ButtonVariant.Outline)
                DropdownMenuContent(style = Style { width(128.dp) }) {
                    DropdownMenuGroup {
                        DropdownMenuLabel("My Account")
                        DropdownMenuItem("Profile", onClick = {})
                        DropdownMenuItem("Billing", onClick = {})
                    }
                    DropdownMenuSeparator()
                    DropdownMenuGroup {
                        DropdownMenuItem("Team", onClick = {})
                        DropdownMenuItem("Subscription", onClick = {})
                    }
                }
            }
            """.trimIndent(),
        usageImportsCode =
            """
            import androidx.compose.foundation.Image
            import androidx.compose.foundation.layout.*
            import androidx.compose.foundation.shape.CircleShape
            import androidx.compose.foundation.style.Style
            import androidx.compose.foundation.style.then
            import androidx.compose.runtime.*
            import androidx.compose.ui.*
            import androidx.compose.ui.graphics.*
            import androidx.compose.ui.graphics.vector.ImageVector
            import androidx.compose.ui.platform.LocalLayoutDirection
            import androidx.compose.ui.semantics.contentDescription
            import androidx.compose.ui.semantics.semantics
            import androidx.compose.ui.text.TextStyle
            import androidx.compose.ui.text.style.LineHeightStyle
            import androidx.compose.ui.unit.*
            import com.composables.icons.lucide.*
            import com.toutiaoyan.compose.ui.components.avatar.*
            import com.toutiaoyan.compose.ui.components.button.*
            import com.toutiaoyan.compose.ui.components.dropdownmenu.*
            import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
            import com.toutiaoyan.compose.ui.components.text.Text
            import com.toutiaoyan.compose.ui.theme.currentTheme
            import com.toutiaoyan.compose.ui.styles.focusRing
            import org.jetbrains.compose.resources.painterResource
            // For Avatar, add the photo to your drawable resources and import your generated Res.
            """.trimIndent(),
        examplesHeading = null,
        compositionCode =
            """
            DropdownMenu
            ├── DropdownMenuTrigger
            └── DropdownMenuContent
                ├── DropdownMenuGroup
                │   ├── DropdownMenuLabel
                │   ├── DropdownMenuItem
                │   └── DropdownMenuItem
                ├── DropdownMenuSeparator
                ├── DropdownMenuGroup
                │   ├── DropdownMenuLabel
                │   ├── DropdownMenuCheckboxItem
                │   └── DropdownMenuCheckboxItem
                ├── DropdownMenuSeparator
                ├── DropdownMenuGroup
                │   ├── DropdownMenuLabel
                │   └── DropdownMenuRadioGroup
                │       ├── DropdownMenuRadioItem
                │       └── DropdownMenuRadioItem
                └── DropdownMenuSub
                    ├── DropdownMenuSubTrigger
                    └── DropdownMenuSubContent
                        └── DropdownMenuGroup
                            ├── DropdownMenuLabel
                            ├── DropdownMenuItem
                            └── DropdownMenuItem
            """.trimIndent(),
        examples =
            (
                listOf(
                    ComponentExample(
                        title = "Default",
                        code =
                            """
                            var open by remember { mutableStateOf(false) }
                            DropdownMenu(open = open, onOpenChange = { open = it }) {
                                DropdownMenuTrigger("Open", variant = ButtonVariant.Outline)
                                DropdownMenuContent(style = Style { width(160.dp) }) {
                                    DropdownMenuGroup {
                                        DropdownMenuLabel("My Account")
                                        DropdownMenuItem("Profile", onClick = {}, shortcut = "⇧⌘P")
                                        DropdownMenuItem("Billing", onClick = {}, shortcut = "⌘B")
                                        DropdownMenuItem("Settings", onClick = {}, shortcut = "⌘S")
                                    }
                                    DropdownMenuSeparator()
                                    DropdownMenuGroup {
                                        DropdownMenuItem("Team", onClick = {})
                                        DropdownMenuSub {
                                            DropdownMenuSubTrigger("Invite users")
                                            DropdownMenuSubContent(style = Style { width(96.dp) }) {
                                                DropdownMenuItem("Email", onClick = {})
                                                DropdownMenuItem("Message", onClick = {})
                                                DropdownMenuSeparator()
                                                DropdownMenuItem("More...", onClick = {})
                                            }
                                        }
                                        DropdownMenuItem("New Team", onClick = {}, shortcut = "⌘+T")
                                    }
                                    DropdownMenuSeparator()
                                    DropdownMenuGroup {
                                        DropdownMenuItem("GitHub", onClick = {})
                                        DropdownMenuItem("Support", onClick = {})
                                        DropdownMenuItem("API", onClick = {}, disabled = true)
                                    }
                                    DropdownMenuSeparator()
                                    DropdownMenuGroup {
                                        DropdownMenuItem("Log out", onClick = {}, shortcut = "⇧⌘Q")
                                    }
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var open by remember { mutableStateOf(false) }
                            DropdownMenu(open = open, onOpenChange = { open = it }) {
                                DropdownMenuTrigger("Open", variant = ButtonVariant.Outline)
                                DropdownMenuContent(style = Style { width(160.dp) }) {
                                    DropdownMenuGroup {
                                        DropdownMenuLabel("My Account")
                                        DropdownMenuItem("Profile", onClick = {}, shortcut = "⇧⌘P")
                                        DropdownMenuItem("Billing", onClick = {}, shortcut = "⌘B")
                                        DropdownMenuItem("Settings", onClick = {}, shortcut = "⌘S")
                                    }
                                    DropdownMenuSeparator()
                                    DropdownMenuGroup {
                                        DropdownMenuItem("Team", onClick = {})
                                        DropdownMenuSub {
                                            DropdownMenuSubTrigger("Invite users")
                                            DropdownMenuSubContent(style = Style { width(96.dp) }) {
                                                DropdownMenuItem("Email", onClick = {})
                                                DropdownMenuItem("Message", onClick = {})
                                                DropdownMenuSeparator()
                                                DropdownMenuItem("More...", onClick = {})
                                            }
                                        }
                                        DropdownMenuItem("New Team", onClick = {}, shortcut = "⌘+T")
                                    }
                                    DropdownMenuSeparator()
                                    DropdownMenuGroup {
                                        DropdownMenuItem("GitHub", onClick = {})
                                        DropdownMenuItem("Support", onClick = {})
                                        DropdownMenuItem("API", onClick = {}, disabled = true)
                                    }
                                    DropdownMenuSeparator()
                                    DropdownMenuGroup {
                                        DropdownMenuItem("Log out", onClick = {}, shortcut = "⇧⌘Q")
                                    }
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Basic",
                        descriptionMarkdown = "A basic dropdown menu with labels and separators.",
                        code =
                            """
                            var open by remember { mutableStateOf(false) }
                            DropdownMenu(open = open, onOpenChange = { open = it }) {
                                DropdownMenuTrigger("Open", variant = ButtonVariant.Outline)
                                DropdownMenuContent(style = Style { width(128.dp) }) {
                                    DropdownMenuGroup {
                                        DropdownMenuLabel("My Account")
                                        DropdownMenuItem("Profile", onClick = {})
                                        DropdownMenuItem("Billing", onClick = {})
                                        DropdownMenuItem("Settings", onClick = {})
                                    }
                                    DropdownMenuSeparator()
                                    DropdownMenuItem("GitHub", onClick = {})
                                    DropdownMenuItem("Support", onClick = {})
                                    DropdownMenuItem("API", onClick = {}, disabled = true)
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var open by remember { mutableStateOf(false) }
                            DropdownMenu(open = open, onOpenChange = { open = it }) {
                                DropdownMenuTrigger("Open", variant = ButtonVariant.Outline)
                                DropdownMenuContent(style = Style { width(128.dp) }) {
                                    DropdownMenuGroup {
                                        DropdownMenuLabel("My Account")
                                        DropdownMenuItem("Profile", onClick = {})
                                        DropdownMenuItem("Billing", onClick = {})
                                        DropdownMenuItem("Settings", onClick = {})
                                    }
                                    DropdownMenuSeparator()
                                    DropdownMenuItem("GitHub", onClick = {})
                                    DropdownMenuItem("Support", onClick = {})
                                    DropdownMenuItem("API", onClick = {}, disabled = true)
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Submenu",
                        descriptionMarkdown = "Use DropdownMenuSub to nest secondary actions.",
                        code =
                            """
                            var open by remember { mutableStateOf(false) }
                            DropdownMenu(open = open, onOpenChange = { open = it }) {
                                DropdownMenuTrigger("Open", variant = ButtonVariant.Outline)
                                DropdownMenuContent(style = Style { width(128.dp) }) {
                                    DropdownMenuGroup {
                                        DropdownMenuItem("Team", onClick = {})
                                        DropdownMenuSub {
                                            DropdownMenuSubTrigger("Invite users")
                                            DropdownMenuSubContent(style = Style { width(128.dp) }) {
                                                DropdownMenuItem("Email", onClick = {})
                                                DropdownMenuItem("Message", onClick = {})
                                                DropdownMenuSub {
                                                    DropdownMenuSubTrigger("More options")
                                                    DropdownMenuSubContent(style = Style { width(96.dp) }) {
                                                        DropdownMenuItem("Calendly", onClick = {})
                                                        DropdownMenuItem("Slack", onClick = {})
                                                        DropdownMenuSeparator()
                                                        DropdownMenuItem("Webhook", onClick = {})
                                                    }
                                                }
                                                DropdownMenuSeparator()
                                                DropdownMenuItem("Advanced...", onClick = {})
                                            }
                                        }
                                        DropdownMenuItem("New Team", onClick = {}, shortcut = "⌘+T")
                                    }
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var open by remember { mutableStateOf(false) }
                            DropdownMenu(open = open, onOpenChange = { open = it }) {
                                DropdownMenuTrigger("Open", variant = ButtonVariant.Outline)
                                DropdownMenuContent(style = Style { width(128.dp) }) {
                                    DropdownMenuGroup {
                                        DropdownMenuItem("Team", onClick = {})
                                        DropdownMenuSub {
                                            DropdownMenuSubTrigger("Invite users")
                                            DropdownMenuSubContent(style = Style { width(128.dp) }) {
                                                DropdownMenuItem("Email", onClick = {})
                                                DropdownMenuItem("Message", onClick = {})
                                                DropdownMenuSub {
                                                    DropdownMenuSubTrigger("More options")
                                                    DropdownMenuSubContent(style = Style { width(96.dp) }) {
                                                        DropdownMenuItem("Calendly", onClick = {})
                                                        DropdownMenuItem("Slack", onClick = {})
                                                        DropdownMenuSeparator()
                                                        DropdownMenuItem("Webhook", onClick = {})
                                                    }
                                                }
                                                DropdownMenuSeparator()
                                                DropdownMenuItem("Advanced...", onClick = {})
                                            }
                                        }
                                        DropdownMenuItem("New Team", onClick = {}, shortcut = "⌘+T")
                                    }
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Shortcuts",
                        descriptionMarkdown = "Add DropdownMenuShortcut to show keyboard hints.",
                        code =
                            """
                            var open by remember { mutableStateOf(false) }
                            DropdownMenu(open = open, onOpenChange = { open = it }) {
                                DropdownMenuTrigger("Open", variant = ButtonVariant.Outline)
                                DropdownMenuContent(style = Style { width(128.dp) }) {
                                    DropdownMenuGroup {
                                        DropdownMenuLabel("My Account")
                                        DropdownMenuItem("Profile", onClick = {}, shortcut = "⇧⌘P")
                                        DropdownMenuItem("Billing", onClick = {}, shortcut = "⌘B")
                                        DropdownMenuItem("Settings", onClick = {}, shortcut = "⌘S")
                                    }
                                    DropdownMenuSeparator()
                                    DropdownMenuItem("Log out", onClick = {}, shortcut = "⇧⌘Q")
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var open by remember { mutableStateOf(false) }
                            DropdownMenu(open = open, onOpenChange = { open = it }) {
                                DropdownMenuTrigger("Open", variant = ButtonVariant.Outline)
                                DropdownMenuContent(style = Style { width(128.dp) }) {
                                    DropdownMenuGroup {
                                        DropdownMenuLabel("My Account")
                                        DropdownMenuItem("Profile", onClick = {}, shortcut = "⇧⌘P")
                                        DropdownMenuItem("Billing", onClick = {}, shortcut = "⌘B")
                                        DropdownMenuItem("Settings", onClick = {}, shortcut = "⌘S")
                                    }
                                    DropdownMenuSeparator()
                                    DropdownMenuItem("Log out", onClick = {}, shortcut = "⇧⌘Q")
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Checkboxes",
                        descriptionMarkdown = "Use DropdownMenuCheckboxItem for toggles.",
                        code =
                            """
                            var showStatus by remember { mutableStateOf(true) }
                            var showActivity by remember { mutableStateOf(false) }
                            var showPanel by remember { mutableStateOf(false) }
                            var open by remember { mutableStateOf(false) }
                            DropdownMenu(open = open, onOpenChange = { open = it }) {
                                DropdownMenuTrigger("Open", variant = ButtonVariant.Outline)
                                DropdownMenuContent(style = Style { width(160.dp) }) {
                                    DropdownMenuGroup {
                                        DropdownMenuLabel("Appearance")
                                        DropdownMenuCheckboxItem("Status Bar", checked = showStatus, onCheckedChange = { showStatus = it })
                                        DropdownMenuCheckboxItem("Activity Bar", checked = showActivity, disabled = true, onCheckedChange = { showActivity = it })
                                        DropdownMenuCheckboxItem("Panel", checked = showPanel, onCheckedChange = { showPanel = it })
                                    }
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var showStatus by remember { mutableStateOf(true) }
                            var showActivity by remember { mutableStateOf(false) }
                            var showPanel by remember { mutableStateOf(false) }
                            var open by remember { mutableStateOf(false) }
                            DropdownMenu(open = open, onOpenChange = { open = it }) {
                                DropdownMenuTrigger("Open", variant = ButtonVariant.Outline)
                                DropdownMenuContent(style = Style { width(160.dp) }) {
                                    DropdownMenuGroup {
                                        DropdownMenuLabel("Appearance")
                                        DropdownMenuCheckboxItem(
                                            "Status Bar",
                                            checked =
                                            showStatus,
                                            onCheckedChange =
                                                {
                                                    showStatus =
                                                        it
                                                },
                                        )
                                        DropdownMenuCheckboxItem(
                                            "Activity Bar",
                                            checked =
                                            showActivity,
                                            disabled =
                                            true,
                                            onCheckedChange =
                                                {
                                                    showActivity =
                                                        it
                                                },
                                        )
                                        DropdownMenuCheckboxItem(
                                            "Panel",
                                            checked =
                                            showPanel,
                                            onCheckedChange =
                                                {
                                                    showPanel =
                                                        it
                                                },
                                        )
                                    }
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Radio Group",
                        descriptionMarkdown = "Use DropdownMenuRadioGroup for exclusive choices.",
                        code =
                            """
                            var panel by remember { mutableStateOf("bottom") }
                            var open by remember { mutableStateOf(false) }
                            DropdownMenu(open = open, onOpenChange = { open = it }) {
                                DropdownMenuTrigger("Open", variant = ButtonVariant.Outline)
                                DropdownMenuContent(style = Style { width(128.dp) }) {
                                    DropdownMenuGroup {
                                        DropdownMenuLabel("Panel Position")
                                        DropdownMenuRadioGroup(value = panel, onValueChange = { panel = it }) {
                                            DropdownMenuRadioItem(value = "top", label = "Top")
                                            DropdownMenuRadioItem(value = "bottom", label = "Bottom")
                                            DropdownMenuRadioItem(value = "right", label = "Right")
                                        }
                                    }
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var panel by remember { mutableStateOf("bottom") }
                            var open by remember { mutableStateOf(false) }
                            DropdownMenu(open = open, onOpenChange = { open = it }) {
                                DropdownMenuTrigger("Open", variant = ButtonVariant.Outline)
                                DropdownMenuContent(style = Style { width(128.dp) }) {
                                    DropdownMenuGroup {
                                        DropdownMenuLabel("Panel Position")
                                        DropdownMenuRadioGroup(value = panel, onValueChange = { panel = it }) {
                                            DropdownMenuRadioItem(value = "top", label = "Top")
                                            DropdownMenuRadioItem(value = "bottom", label = "Bottom")
                                            DropdownMenuRadioItem(value = "right", label = "Right")
                                        }
                                    }
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Inset",
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
                        title = "Kotlin extension: Disabled",
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
                        title = "Kotlin extension: Non-modal",
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
                        title = "Kotlin extension: Non-dismissible",
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
                        title = "Kotlin extension: Indeterminate checkbox",
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
                        title = "Kotlin extension: Keep open on select",
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
                        title = "Kotlin extension: Prevent checkbox toggle",
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
                        title = "Kotlin extension: Uncontrolled",
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
                ) + dropdownMenuAdditionalExamples
            ).sortedBy {
                val index = dropdownMenuExampleOrder.indexOf(it.title)
                if (index < 0) dropdownMenuExampleOrder.size else index
            },
        apiSections =
            listOf(
                ComponentApiSection(
                    "DropdownMenu",
                    "Controlled or local open state and modal input policy.",
                    listOf(
                        ApiProperty("open / defaultOpen", "Boolean? / Boolean", "null / false"),
                        ApiProperty("onOpenChange", "((Boolean) -> Unit)?", "null"),
                        ApiProperty("modal", "Boolean", "true"),
                        ApiProperty("modifier", "Modifier", "Modifier"),
                    ),
                ),
                ComponentApiSection(
                    "DropdownMenuContent / SubContent",
                    "Positioned, height-bounded scrollable panels.",
                    listOf(
                        ApiProperty(
                            "placement / side",
                            "PopupPlacement / PopupPlacement?",
                            "Bottom / null (root); End / null (sub)",
                        ),
                        ApiProperty("sideOffset", "Dp", "4.dp (root); 0.dp (sub)"),
                        ApiProperty(
                            "align / alignOffset",
                            "PopupAlign / Dp",
                            "Start / 0.dp (root); Start / -3.dp (sub)",
                        ),
                        ApiProperty("dismissOnClickOutside", "Boolean", "true"),
                        ApiProperty("enter / exit", "EnterTransition / ExitTransition", "PopupTransitions"),
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                    ),
                ),
                ComponentApiSection(
                    "Items and submenu triggers",
                    (
                        "Labels own their semantics; optional leadingContent owns " +
                            "visuals."
                    ),
                    listOf(
                        ApiProperty("label / textValue", "String / String?", "Required / null"),
                        ApiProperty(
                            "leadingContent",
                            "(@Composable () -> Unit)?",
                            "null on checkbox, radio and sub trigger",
                        ),
                        ApiProperty("enabled / disabled", "Boolean / Boolean", "true / false"),
                        ApiProperty("checked / onCheckedChange", "Boolean / (Boolean) -> Unit", "Required on checkbox"),
                        ApiProperty("value / onValueChange", "String / (String) -> Unit", "Required on radio group"),
                        ApiProperty("onSelect", "(() -> Boolean)?", "null; return true to prevent selection"),
                        ApiProperty("inset / indeterminate", "Boolean", "false where supported"),
                        ApiProperty(
                            "shortcut / variant",
                            "String? / DropdownMenuItemVariant",
                            "null / Default on plain item",
                        ),
                        ApiProperty("style", "Style", "Style"),
                    ),
                ),
            ),
        afterExamples =
            listOf(
                ComponentGuideSection(
                    "Kotlin API notes",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            (
                                "The first thirteen recipes follow the official Base UI page. " +
                                    "The additional examples cover Kotlin state and selection " +
                                    "APIs. "
                            ) +
                                "leadingContent supplies optional visuals for checkbox, radio and submenu rows. " +
                                (
                                    "Long menu content scrolls within the available window space. " +
                                        "Avatar uses the bundled shadcn photo; replace the resource in " +
                                        "your app."
                                ),
                        ),
                    ),
                ),
            ),
    )
