@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.ArrowRight
import com.composables.icons.lucide.ClipboardPaste
import com.composables.icons.lucide.Copy
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pencil
import com.composables.icons.lucide.RotateCw
import com.composables.icons.lucide.Scissors
import com.composables.icons.lucide.Share
import com.composables.icons.lucide.Trash2
import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenu
import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuCheckboxItem
import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuContent
import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuGroup
import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuItem
import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuLabel
import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuRadioGroup
import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuRadioItem
import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuSeparator
import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuSide
import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuSub
import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuSubContent
import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuSubTrigger
import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuTrigger
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuScope
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.currentTheme

/**
 * Current official page (revision 8a35366):
 * demo, Installation, Usage, Composition, then Basic, Submenu, Shortcuts, Groups, Icons,
 * Checkboxes, Radio, Destructive, Sides, RTL, API Reference.
 *
 * The previous local page was a Base UI API dump (39 recipes such as "Default disabled",
 * "Default variant", "Default modal", "Trigger" and "Side offset"); this round replaces it
 * with the official page structure.
 */
val contextMenuDoc =
    ComponentDoc(
        id = "context-menu",
        title = "Context Menu",
        description = "Displays a menu of actions triggered by a right click.",
        usageImportsCode =
            """
            import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenu
            import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuContent
            import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuItem
            import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuTrigger
            """.trimIndent(),
        usageCode =
            """
            ContextMenu {
                ContextMenuTrigger("Right click here")
                ContextMenuContent {
                    ContextMenuItem("Profile", onClick = {})
                    ContextMenuItem("Billing", onClick = {})
                    ContextMenuItem("Team", onClick = {})
                    ContextMenuItem("Subscription", onClick = {})
                }
            }
            """.trimIndent(),
        compositionCode =
            """
            ContextMenu
            ├── ContextMenuTrigger
            └── ContextMenuContent
                ├── ContextMenuGroup
                │   ├── ContextMenuLabel
                │   ├── ContextMenuItem
                │   └── ContextMenuItem
                ├── ContextMenuSeparator
                ├── ContextMenuGroup
                │   ├── ContextMenuLabel
                │   ├── ContextMenuCheckboxItem
                │   └── ContextMenuCheckboxItem
                ├── ContextMenuSeparator
                ├── ContextMenuGroup
                │   ├── ContextMenuLabel
                │   └── ContextMenuRadioGroup
                │       ├── ContextMenuRadioItem
                │       └── ContextMenuRadioItem
                └── ContextMenuSub
                    ├── ContextMenuSubTrigger
                    └── ContextMenuSubContent
                        └── ContextMenuGroup
                            ├── ContextMenuItem
                            └── ContextMenuItem
            """.trimIndent(),
        examplesHeading = null,
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        var bookmarks by remember { mutableStateOf(true) }
                        var urls by remember { mutableStateOf(false) }
                        var person by remember { mutableStateOf("pedro") }
                        ContextMenu {
                            ContextMenuTrigger { ContextMenuDemoTrigger() }
                            ContextMenuContent(panelWidth = 192.dp) {
                                ContextMenuGroup {
                                    ContextMenuItem("Back", onClick = {}, shortcut = "⌘[")
                                    ContextMenuItem("Forward", onClick = {}, shortcut = "⌘]", disabled = true)
                                    ContextMenuItem("Reload", onClick = {}, shortcut = "⌘R")
                                    ContextMenuSub {
                                        ContextMenuSubTrigger("More Tools")
                                        ContextMenuSubContent(style = Style { width(176.dp) }) {
                                            ContextMenuGroup {
                                                ContextMenuItem("Save Page...", onClick = {})
                                                ContextMenuItem("Create Shortcut...", onClick = {})
                                                ContextMenuItem("Name Window...", onClick = {})
                                            }
                                            ContextMenuSeparator()
                                            ContextMenuGroup {
                                                ContextMenuItem("Developer Tools", onClick = {})
                                            }
                                            ContextMenuSeparator()
                                            ContextMenuGroup {
                                                ContextMenuItem("Delete", onClick = {}, destructive = true)
                                            }
                                        }
                                    }
                                }
                                ContextMenuSeparator()
                                ContextMenuGroup {
                                    ContextMenuCheckboxItem("Show Bookmarks", checked = bookmarks, onCheckedChange = { bookmarks = it })
                                    ContextMenuCheckboxItem("Show Full URLs", checked = urls, onCheckedChange = { urls = it })
                                }
                                ContextMenuSeparator()
                                ContextMenuGroup {
                                    ContextMenuLabel("People")
                                    ContextMenuRadioGroup(value = person, onValueChange = { person = it }) {
                                        ContextMenuRadioItem("pedro", "Pedro Duarte")
                                        ContextMenuRadioItem("colm", "Colm Tuite")
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var bookmarks by remember { mutableStateOf(true) }
                        var urls by remember { mutableStateOf(false) }
                        var person by remember { mutableStateOf("pedro") }
                        ContextMenu {
                            ContextMenuTrigger { ContextMenuDemoTrigger() }
                            ContextMenuContent(panelWidth = 192.dp) {
                                ContextMenuGroup {
                                    ContextMenuItem("Back", onClick = {}, shortcut = "⌘[")
                                    ContextMenuItem("Forward", onClick = {}, shortcut = "⌘]", disabled = true)
                                    ContextMenuItem("Reload", onClick = {}, shortcut = "⌘R")
                                    ContextMenuSub {
                                        ContextMenuSubTrigger("More Tools")
                                        ContextMenuSubContent(style = Style { width(176.dp) }) {
                                            ContextMenuGroup {
                                                ContextMenuItem("Save Page...", onClick = {})
                                                ContextMenuItem("Create Shortcut...", onClick = {})
                                                ContextMenuItem("Name Window...", onClick = {})
                                            }
                                            ContextMenuSeparator()
                                            ContextMenuGroup {
                                                ContextMenuItem("Developer Tools", onClick = {})
                                            }
                                            ContextMenuSeparator()
                                            ContextMenuGroup {
                                                ContextMenuItem("Delete", onClick = {}, destructive = true)
                                            }
                                        }
                                    }
                                }
                                ContextMenuSeparator()
                                ContextMenuGroup {
                                    ContextMenuCheckboxItem(
                                        "Show Bookmarks",
                                        checked = bookmarks,
                                        onCheckedChange = { bookmarks = it },
                                    )
                                    ContextMenuCheckboxItem(
                                        "Show Full URLs",
                                        checked = urls,
                                        onCheckedChange = { urls = it },
                                    )
                                }
                                ContextMenuSeparator()
                                ContextMenuGroup {
                                    ContextMenuLabel("People")
                                    ContextMenuRadioGroup(value = person, onValueChange = { person = it }) {
                                        ContextMenuRadioItem("pedro", "Pedro Duarte")
                                        ContextMenuRadioItem("colm", "Colm Tuite")
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Basic",
                    descriptionMarkdown = "A simple context menu with a few actions.",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger { ContextMenuDemoTrigger() }
                            ContextMenuContent {
                                ContextMenuGroup {
                                    ContextMenuItem("Back", onClick = {})
                                    ContextMenuItem("Forward", onClick = {}, disabled = true)
                                    ContextMenuItem("Reload", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger { ContextMenuDemoTrigger() }
                            ContextMenuContent {
                                ContextMenuGroup {
                                    ContextMenuItem("Back", onClick = {})
                                    ContextMenuItem("Forward", onClick = {}, disabled = true)
                                    ContextMenuItem("Reload", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Submenu",
                    descriptionMarkdown = "Use `ContextMenuSub` to nest secondary actions.",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger { ContextMenuDemoTrigger() }
                            ContextMenuContent {
                                ContextMenuGroup {
                                    ContextMenuItem("Copy", onClick = {}, shortcut = "⌘C")
                                    ContextMenuItem("Cut", onClick = {}, shortcut = "⌘X")
                                }
                                ContextMenuSub {
                                    ContextMenuSubTrigger("More Tools")
                                    ContextMenuSubContent {
                                        ContextMenuGroup {
                                            ContextMenuItem("Save Page...", onClick = {})
                                            ContextMenuItem("Create Shortcut...", onClick = {})
                                            ContextMenuItem("Name Window...", onClick = {})
                                        }
                                        ContextMenuSeparator()
                                        ContextMenuGroup {
                                            ContextMenuItem("Developer Tools", onClick = {})
                                        }
                                        ContextMenuSeparator()
                                        ContextMenuGroup {
                                            ContextMenuItem("Delete", onClick = {}, destructive = true)
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger { ContextMenuDemoTrigger() }
                            ContextMenuContent {
                                ContextMenuGroup {
                                    ContextMenuItem("Copy", onClick = {}, shortcut = "⌘C")
                                    ContextMenuItem("Cut", onClick = {}, shortcut = "⌘X")
                                }
                                ContextMenuSub {
                                    ContextMenuSubTrigger("More Tools")
                                    ContextMenuSubContent {
                                        ContextMenuGroup {
                                            ContextMenuItem("Save Page...", onClick = {})
                                            ContextMenuItem("Create Shortcut...", onClick = {})
                                            ContextMenuItem("Name Window...", onClick = {})
                                        }
                                        ContextMenuSeparator()
                                        ContextMenuGroup {
                                            ContextMenuItem("Developer Tools", onClick = {})
                                        }
                                        ContextMenuSeparator()
                                        ContextMenuGroup {
                                            ContextMenuItem("Delete", onClick = {}, destructive = true)
                                        }
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Shortcuts",
                    descriptionMarkdown = "Add `ContextMenuShortcut` to show keyboard hints.",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger { ContextMenuDemoTrigger() }
                            ContextMenuContent {
                                ContextMenuGroup {
                                    ContextMenuItem("Back", onClick = {}, shortcut = "⌘[")
                                    ContextMenuItem("Forward", onClick = {}, shortcut = "⌘]", disabled = true)
                                    ContextMenuItem("Reload", onClick = {}, shortcut = "⌘R")
                                }
                                ContextMenuSeparator()
                                ContextMenuGroup {
                                    ContextMenuItem("Save", onClick = {}, shortcut = "⌘S")
                                    ContextMenuItem("Save As...", onClick = {}, shortcut = "⇧⌘S")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger { ContextMenuDemoTrigger() }
                            ContextMenuContent {
                                ContextMenuGroup {
                                    ContextMenuItem("Back", onClick = {}, shortcut = "⌘[")
                                    ContextMenuItem("Forward", onClick = {}, shortcut = "⌘]", disabled = true)
                                    ContextMenuItem("Reload", onClick = {}, shortcut = "⌘R")
                                }
                                ContextMenuSeparator()
                                ContextMenuGroup {
                                    ContextMenuItem("Save", onClick = {}, shortcut = "⌘S")
                                    ContextMenuItem("Save As...", onClick = {}, shortcut = "⇧⌘S")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Groups",
                    descriptionMarkdown = "Group related actions and separate them with dividers.",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger { ContextMenuDemoTrigger() }
                            ContextMenuContent {
                                ContextMenuGroup {
                                    ContextMenuLabel("File")
                                    ContextMenuItem("New File", onClick = {}, shortcut = "⌘N")
                                    ContextMenuItem("Open File", onClick = {}, shortcut = "⌘O")
                                    ContextMenuItem("Save", onClick = {}, shortcut = "⌘S")
                                }
                                ContextMenuSeparator()
                                ContextMenuGroup {
                                    ContextMenuLabel("Edit")
                                    ContextMenuItem("Undo", onClick = {}, shortcut = "⌘Z")
                                    ContextMenuItem("Redo", onClick = {}, shortcut = "⇧⌘Z")
                                }
                                ContextMenuSeparator()
                                ContextMenuGroup {
                                    ContextMenuItem("Cut", onClick = {}, shortcut = "⌘X")
                                    ContextMenuItem("Copy", onClick = {}, shortcut = "⌘C")
                                    ContextMenuItem("Paste", onClick = {}, shortcut = "⌘V")
                                }
                                ContextMenuSeparator()
                                ContextMenuGroup {
                                    ContextMenuItem("Delete", onClick = {}, destructive = true, shortcut = "⌫")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger { ContextMenuDemoTrigger() }
                            ContextMenuContent {
                                ContextMenuGroup {
                                    ContextMenuLabel("File")
                                    ContextMenuItem("New File", onClick = {}, shortcut = "⌘N")
                                    ContextMenuItem("Open File", onClick = {}, shortcut = "⌘O")
                                    ContextMenuItem("Save", onClick = {}, shortcut = "⌘S")
                                }
                                ContextMenuSeparator()
                                ContextMenuGroup {
                                    ContextMenuLabel("Edit")
                                    ContextMenuItem("Undo", onClick = {}, shortcut = "⌘Z")
                                    ContextMenuItem("Redo", onClick = {}, shortcut = "⇧⌘Z")
                                }
                                ContextMenuSeparator()
                                ContextMenuGroup {
                                    ContextMenuItem("Cut", onClick = {}, shortcut = "⌘X")
                                    ContextMenuItem("Copy", onClick = {}, shortcut = "⌘C")
                                    ContextMenuItem("Paste", onClick = {}, shortcut = "⌘V")
                                }
                                ContextMenuSeparator()
                                ContextMenuGroup {
                                    ContextMenuItem("Delete", onClick = {}, destructive = true, shortcut = "⌫")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Icons",
                    descriptionMarkdown = "Combine icons with labels for quick scanning.",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger { ContextMenuDemoTrigger() }
                            ContextMenuContent {
                                ContextMenuGroup {
                                    ContextMenuIconItem("Copy", Lucide.Copy)
                                    ContextMenuIconItem("Cut", Lucide.Scissors)
                                    ContextMenuIconItem("Paste", Lucide.ClipboardPaste)
                                }
                                ContextMenuSeparator()
                                ContextMenuGroup {
                                    ContextMenuIconItem("Delete", Lucide.Trash2, destructive = true)
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger { ContextMenuDemoTrigger() }
                            ContextMenuContent {
                                ContextMenuGroup {
                                    ContextMenuIconItem("Copy", Lucide.Copy)
                                    ContextMenuIconItem("Cut", Lucide.Scissors)
                                    ContextMenuIconItem("Paste", Lucide.ClipboardPaste)
                                }
                                ContextMenuSeparator()
                                ContextMenuGroup {
                                    ContextMenuIconItem("Delete", Lucide.Trash2, destructive = true)
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Checkboxes",
                    descriptionMarkdown = "Use `ContextMenuCheckboxItem` for toggles.",
                    code =
                        """
                        var bookmarks by remember { mutableStateOf(true) }
                        var urls by remember { mutableStateOf(false) }
                        var tools by remember { mutableStateOf(true) }
                        ContextMenu {
                            ContextMenuTrigger { ContextMenuDemoTrigger() }
                            ContextMenuContent(panelWidth = 188.dp) {
                                ContextMenuGroup {
                                    ContextMenuCheckboxItem(
                                        "Show Bookmarks Bar",
                                        checked = bookmarks,
                                        onCheckedChange = { bookmarks = it },
                                    )
                                    ContextMenuCheckboxItem(
                                        "Show Full URLs",
                                        checked = urls,
                                        onCheckedChange = { urls = it },
                                    )
                                    ContextMenuCheckboxItem(
                                        "Show Developer Tools",
                                        checked = tools,
                                        onCheckedChange = { tools = it },
                                    )
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var bookmarks by remember { mutableStateOf(true) }
                        var urls by remember { mutableStateOf(false) }
                        var tools by remember { mutableStateOf(true) }
                        ContextMenu {
                            ContextMenuTrigger { ContextMenuDemoTrigger() }
                            ContextMenuContent(panelWidth = 188.dp) {
                                ContextMenuGroup {
                                    ContextMenuCheckboxItem(
                                        "Show Bookmarks Bar",
                                        checked = bookmarks,
                                        onCheckedChange = { bookmarks = it },
                                    )
                                    ContextMenuCheckboxItem(
                                        "Show Full URLs",
                                        checked = urls,
                                        onCheckedChange = { urls = it },
                                    )
                                    ContextMenuCheckboxItem(
                                        "Show Developer Tools",
                                        checked = tools,
                                        onCheckedChange = { tools = it },
                                    )
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Radio",
                    descriptionMarkdown = "Use `ContextMenuRadioItem` for exclusive choices.",
                    code =
                        """
                        var user by remember { mutableStateOf("pedro") }
                        var theme by remember { mutableStateOf("light") }
                        ContextMenu {
                            ContextMenuTrigger { ContextMenuDemoTrigger() }
                            ContextMenuContent {
                                ContextMenuGroup {
                                    ContextMenuLabel("People")
                                    ContextMenuRadioGroup(value = user, onValueChange = { user = it }) {
                                        ContextMenuRadioItem("pedro", "Pedro Duarte")
                                        ContextMenuRadioItem("colm", "Colm Tuite")
                                    }
                                }
                                ContextMenuSeparator()
                                ContextMenuGroup {
                                    ContextMenuLabel("Theme")
                                    ContextMenuRadioGroup(value = theme, onValueChange = { theme = it }) {
                                        ContextMenuRadioItem("light", "Light")
                                        ContextMenuRadioItem("dark", "Dark")
                                        ContextMenuRadioItem("system", "System")
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var user by remember { mutableStateOf("pedro") }
                        var theme by remember { mutableStateOf("light") }
                        ContextMenu {
                            ContextMenuTrigger { ContextMenuDemoTrigger() }
                            ContextMenuContent {
                                ContextMenuGroup {
                                    ContextMenuLabel("People")
                                    ContextMenuRadioGroup(value = user, onValueChange = { user = it }) {
                                        ContextMenuRadioItem("pedro", "Pedro Duarte")
                                        ContextMenuRadioItem("colm", "Colm Tuite")
                                    }
                                }
                                ContextMenuSeparator()
                                ContextMenuGroup {
                                    ContextMenuLabel("Theme")
                                    ContextMenuRadioGroup(value = theme, onValueChange = { theme = it }) {
                                        ContextMenuRadioItem("light", "Light")
                                        ContextMenuRadioItem("dark", "Dark")
                                        ContextMenuRadioItem("system", "System")
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Destructive",
                    descriptionMarkdown = "Use `variant=\"destructive\"` to style the menu item as destructive.",
                    code =
                        """
                        ContextMenu {
                            ContextMenuTrigger { ContextMenuDemoTrigger() }
                            ContextMenuContent {
                                ContextMenuGroup {
                                    ContextMenuIconItem("Edit", Lucide.Pencil)
                                    ContextMenuIconItem("Share", Lucide.Share)
                                }
                                ContextMenuSeparator()
                                ContextMenuGroup {
                                    ContextMenuIconItem("Delete", Lucide.Trash2, destructive = true)
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ContextMenu {
                            ContextMenuTrigger { ContextMenuDemoTrigger() }
                            ContextMenuContent {
                                ContextMenuGroup {
                                    ContextMenuIconItem("Edit", Lucide.Pencil)
                                    ContextMenuIconItem("Share", Lucide.Share)
                                }
                                ContextMenuSeparator()
                                ContextMenuGroup {
                                    ContextMenuIconItem("Delete", Lucide.Trash2, destructive = true)
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Sides",
                    descriptionMarkdown = "Control submenu placement with side and align props.",
                    code =
                        """
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            ContextMenuSideRow(ContextMenuSide.Top, ContextMenuSide.Right, "Right click (top)", "Right click (right)")
                            ContextMenuSideRow(ContextMenuSide.Bottom, ContextMenuSide.Left, "Right click (bottom)", "Right click (left)")
                        }

                        @Composable
                        private fun ContextMenuSideRow(
                            top: ContextMenuSide,
                            right: ContextMenuSide,
                            topText: String,
                            rightText: String,
                        ) {
                            Row(
                                Modifier.widthIn(max = 384.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                SideTrigger(topText, top, Modifier.weight(1f))
                                SideTrigger(rightText, right, Modifier.weight(1f))
                            }
                        }

                        @Composable
                        private fun SideTrigger(text: String, side: ContextMenuSide, modifier: Modifier) {
                            ContextMenu(modifier = modifier) {
                                ContextMenuTrigger(Modifier.fillMaxWidth()) { ContextMenuDemoTrigger(text) }
                                ContextMenuContent(side = side) {
                                    ContextMenuGroup {
                                        ContextMenuItem("Back", onClick = {})
                                        ContextMenuItem("Forward", onClick = {})
                                        ContextMenuItem("Reload", onClick = {})
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { ContextMenuSidesPreview() },
                ),
                ComponentExample(
                    title = "RTL",
                    descriptionMarkdown =
                        "To enable RTL support in shadcn/ui, see the " +
                            "[RTL configuration guide](https://ui.shadcn.com/docs/rtl).",
                    code =
                        """
                        var people by remember { mutableStateOf("pedro") }
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            ContextMenu {
                                ContextMenuTrigger { ContextMenuDemoTrigger("انقر بزر الماوس الأيمن هنا") }
                                ContextMenuContent(panelWidth = 192.dp) {
                                    ContextMenuGroup {
                                        ContextMenuSub {
                                            ContextMenuSubTrigger("التنقل")
                                            ContextMenuSubContent(style = Style { width(176.dp) }) {
                                                ContextMenuItem("رجوع", onClick = {}, shortcut = "⌘[")
                                                ContextMenuItem("تقدم", onClick = {}, shortcut = "⌘]", disabled = true)
                                                ContextMenuItem("إعادة تحميل", onClick = {}, shortcut = "⌘R")
                                            }
                                        }
                                        ContextMenuSub {
                                            ContextMenuSubTrigger("المزيد من الأدوات")
                                            ContextMenuSubContent(style = Style { width(176.dp) }) {
                                                ContextMenuItem("حفظ الصفحة...", onClick = {})
                                                ContextMenuItem("إنشاء اختصار...", onClick = {})
                                                ContextMenuItem("تسمية النافذة...", onClick = {})
                                                ContextMenuSeparator()
                                                ContextMenuItem("أدوات المطور", onClick = {})
                                                ContextMenuSeparator()
                                                ContextMenuItem("حذف", onClick = {}, destructive = true)
                                            }
                                        }
                                    }
                                    ContextMenuSeparator()
                                    ContextMenuGroup {
                                        ContextMenuCheckboxItem("إظهار الإشارات المرجعية", checked = true, onCheckedChange = {})
                                        ContextMenuCheckboxItem("إظهار عناوين URL الكاملة", checked = false, onCheckedChange = {})
                                    }
                                    ContextMenuSeparator()
                                    ContextMenuGroup {
                                        ContextMenuLabel("الأشخاص")
                                        ContextMenuRadioGroup(value = people, onValueChange = { people = it }) {
                                            ContextMenuRadioItem("pedro", "Pedro Duarte")
                                            ContextMenuRadioItem("colm", "Colm Tuite")
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { ContextMenuRtlPreview() },
                ),
            ),
        afterExamples =
            listOf(
                ComponentGuideSection(
                    title = "API Reference",
                    blocks =
                        listOf(
                            ComponentGuideBlock.Paragraph(
                                "See the [Base UI](https://base-ui.com/react/components/context-menu#api-reference) " +
                                    "documentation for more information.",
                            ),
                        ),
                ),
            ),
    )

/** Dashed trigger surface used by every official context-menu recipe (`aspect-video max-w-xs`). */
@Composable
private fun ContextMenuDemoTrigger(
    text: String = "Right click here",
    modifier: Modifier = Modifier,
) {
    val border = currentTheme.colors.border
    Box(
        modifier = modifier.widthIn(max = 320.dp).fillMaxWidth().aspectRatio(16f / 9f),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.matchParentSize()) {
            drawRoundRect(
                color = border,
                cornerRadius = CornerRadius(12.dp.toPx()),
                style =
                    Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx())),
                    ),
            )
        }
        Text(
            textStyle =
                TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                    color = currentTheme.colors.foreground,
                ),
        ) { text }
    }
}

/** Icon + label row for slot [ContextMenuItem]s (official `Icons` / `Destructive` recipes). */
@Composable
private fun DropdownMenuScope.ContextMenuIconItem(
    label: String,
    icon: ImageVector,
    destructive: Boolean = false,
    shortcut: String? = null,
    onClick: () -> Unit = {},
) {
    ContextMenuItem(
        label = label,
        onClick = onClick,
        destructive = destructive,
        content = {
            ContextMenuIconLabel(label = label, icon = icon, shortcut = shortcut, destructive = destructive)
        },
    )
}

@Composable
private fun ContextMenuIconLabel(
    label: String,
    icon: ImageVector,
    shortcut: String? = null,
    destructive: Boolean = false,
) {
    val tint = if (destructive) currentTheme.colors.destructive else currentTheme.colors.popoverForeground
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Image(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            colorFilter = ColorFilter.tint(tint),
        )
        Text(
            modifier = Modifier.weight(1f),
            textStyle =
                TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                    color = tint,
                ),
        ) { label }
        shortcut?.let {
            Text(textStyle = TextStyle(color = currentTheme.colors.mutedForeground, fontSize = 12.sp)) { it }
        }
    }
}

@Composable
private fun ContextMenuSidesPreview() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ContextMenuSideRow(ContextMenuSide.Top, ContextMenuSide.Right, "Right click (top)", "Right click (right)")
        ContextMenuSideRow(ContextMenuSide.Bottom, ContextMenuSide.Left, "Right click (bottom)", "Right click (left)")
    }
}

@Composable
private fun ContextMenuSideRow(
    top: ContextMenuSide,
    right: ContextMenuSide,
    topText: String,
    rightText: String,
) {
    Row(
        modifier = Modifier.widthIn(max = 384.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ContextMenuSideTrigger(topText, top, Modifier.weight(1f))
        ContextMenuSideTrigger(rightText, right, Modifier.weight(1f))
    }
}

@Composable
private fun ContextMenuSideTrigger(
    text: String,
    side: ContextMenuSide,
    modifier: Modifier = Modifier,
) {
    ContextMenu(modifier = modifier) {
        ContextMenuTrigger(Modifier.fillMaxWidth()) { ContextMenuDemoTrigger(text) }
        ContextMenuContent(side = side) {
            ContextMenuGroup {
                ContextMenuItem("Back", onClick = {})
                ContextMenuItem("Forward", onClick = {})
                ContextMenuItem("Reload", onClick = {})
            }
        }
    }
}

private data class ContextMenuRtlText(
    val language: String,
    val direction: LayoutDirection,
    val rightClick: String,
    val navigation: String,
    val back: String,
    val forward: String,
    val reload: String,
    val moreTools: String,
    val savePage: String,
    val createShortcut: String,
    val nameWindow: String,
    val developerTools: String,
    val delete: String,
    val showBookmarks: String,
    val showFullUrls: String,
    val people: String,
)

private val contextMenuRtlTexts =
    listOf(
        ContextMenuRtlText(
            language = "Arabic (العربية)",
            direction = LayoutDirection.Rtl,
            rightClick = "انقر بزر الماوس الأيمن هنا",
            navigation = "التنقل",
            back = "رجوع",
            forward = "تقدم",
            reload = "إعادة تحميل",
            moreTools = "المزيد من الأدوات",
            savePage = "حفظ الصفحة...",
            createShortcut = "إنشاء اختصار...",
            nameWindow = "تسمية النافذة...",
            developerTools = "أدوات المطور",
            delete = "حذف",
            showBookmarks = "إظهار الإشارات المرجعية",
            showFullUrls = "إظهار عناوين URL الكاملة",
            people = "الأشخاص",
        ),
        ContextMenuRtlText(
            language = "English",
            direction = LayoutDirection.Ltr,
            rightClick = "Right click here",
            navigation = "Navigation",
            back = "Back",
            forward = "Forward",
            reload = "Reload",
            moreTools = "More Tools",
            savePage = "Save Page...",
            createShortcut = "Create Shortcut...",
            nameWindow = "Name Window...",
            developerTools = "Developer Tools",
            delete = "Delete",
            showBookmarks = "Show Bookmarks",
            showFullUrls = "Show Full URLs",
            people = "People",
        ),
        ContextMenuRtlText(
            language = "Hebrew (עברית)",
            direction = LayoutDirection.Rtl,
            rightClick = "לחץ לחיצה ימנית כאן",
            navigation = "ניווט",
            back = "חזור",
            forward = "קדימה",
            reload = "רענן",
            moreTools = "כלים נוספים",
            savePage = "שמור עמוד...",
            createShortcut = "צור קיצור דרך...",
            nameWindow = "שם חלון...",
            developerTools = "כלי מפתח",
            delete = "מחק",
            showBookmarks = "הצג סימניות",
            showFullUrls = "הצג כתובות URL מלאות",
            people = "אנשים",
        ),
    )

@Composable
private fun ContextMenuRtlPreview() {
    var language by remember { mutableStateOf(0) }
    val text = contextMenuRtlTexts[language]
    var people by remember { mutableStateOf("pedro") }
    RtlPreview(language, contextMenuRtlTexts.map { it.language }, { language = it }) {
        CompositionLocalProvider(LocalLayoutDirection provides text.direction) {
            ContextMenu {
                ContextMenuTrigger { ContextMenuDemoTrigger(text.rightClick) }
                ContextMenuContent(panelWidth = 192.dp) {
                    ContextMenuGroup {
                        ContextMenuSub {
                            ContextMenuSubTrigger(text.navigation)
                            ContextMenuSubContent(style = Style { width(176.dp) }) {
                                ContextMenuItem(
                                    text.back,
                                    onClick = {},
                                    shortcut = "⌘[",
                                    content = { ContextMenuIconLabel(text.back, Lucide.ArrowLeft, "⌘[") },
                                )
                                ContextMenuItem(
                                    text.forward,
                                    onClick = {},
                                    shortcut = "⌘]",
                                    disabled = true,
                                    content = { ContextMenuIconLabel(text.forward, Lucide.ArrowRight, "⌘]") },
                                )
                                ContextMenuItem(
                                    text.reload,
                                    onClick = {},
                                    shortcut = "⌘R",
                                    content = { ContextMenuIconLabel(text.reload, Lucide.RotateCw, "⌘R") },
                                )
                            }
                        }
                        ContextMenuSub {
                            ContextMenuSubTrigger(text.moreTools)
                            ContextMenuSubContent(style = Style { width(176.dp) }) {
                                ContextMenuItem(text.savePage, onClick = {})
                                ContextMenuItem(text.createShortcut, onClick = {})
                                ContextMenuItem(text.nameWindow, onClick = {})
                                ContextMenuSeparator()
                                ContextMenuItem(text.developerTools, onClick = {})
                                ContextMenuSeparator()
                                ContextMenuItem(text.delete, onClick = {}, destructive = true)
                            }
                        }
                    }
                    ContextMenuSeparator()
                    ContextMenuGroup {
                        ContextMenuCheckboxItem(text.showBookmarks, checked = true, onCheckedChange = {})
                        ContextMenuCheckboxItem(text.showFullUrls, checked = false, onCheckedChange = {})
                    }
                    ContextMenuSeparator()
                    ContextMenuGroup {
                        ContextMenuLabel(text.people)
                        ContextMenuRadioGroup(value = people, onValueChange = { people = it }) {
                            ContextMenuRadioItem("pedro", "Pedro Duarte")
                            ContextMenuRadioItem("colm", "Colm Tuite")
                        }
                    }
                }
            }
        }
    }
}
