@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Menu
import com.composables.icons.lucide.Plus
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.breadcrumb.Breadcrumb
import com.toutiaoyan.compose.ui.components.breadcrumb.BreadcrumbLink
import com.toutiaoyan.compose.ui.components.breadcrumb.BreadcrumbPage
import com.toutiaoyan.compose.ui.components.breadcrumb.BreadcrumbSeparator
import com.toutiaoyan.compose.ui.components.card.Card
import com.toutiaoyan.compose.ui.components.sidebar.Sidebar
import com.toutiaoyan.compose.ui.components.sidebar.SidebarCollapsible
import com.toutiaoyan.compose.ui.components.sidebar.SidebarDefaults
import com.toutiaoyan.compose.ui.components.sidebar.SidebarGroup
import com.toutiaoyan.compose.ui.components.sidebar.SidebarGroupAction
import com.toutiaoyan.compose.ui.components.sidebar.SidebarGroupContent
import com.toutiaoyan.compose.ui.components.sidebar.SidebarGroupLabel
import com.toutiaoyan.compose.ui.components.sidebar.SidebarHeader
import com.toutiaoyan.compose.ui.components.sidebar.SidebarFooter
import com.toutiaoyan.compose.ui.components.sidebar.SidebarInset
import com.toutiaoyan.compose.ui.components.sidebar.SidebarInput
import com.toutiaoyan.compose.ui.components.sidebar.SidebarMenu
import com.toutiaoyan.compose.ui.components.sidebar.SidebarMenuAction
import com.toutiaoyan.compose.ui.components.sidebar.SidebarMenuButton
import com.toutiaoyan.compose.ui.components.sidebar.SidebarMenuButtonSize
import com.toutiaoyan.compose.ui.components.sidebar.SidebarMenuButtonVariant
import com.toutiaoyan.compose.ui.components.sidebar.SidebarMenuItem
import com.toutiaoyan.compose.ui.components.sidebar.SidebarMenuSkeleton
import com.toutiaoyan.compose.ui.components.sidebar.SidebarMenuSub
import com.toutiaoyan.compose.ui.components.sidebar.SidebarMenuSubButton
import com.toutiaoyan.compose.ui.components.sidebar.SidebarMenuSubItem
import com.toutiaoyan.compose.ui.components.sidebar.SidebarProvider
import com.toutiaoyan.compose.ui.components.sidebar.SidebarRail
import com.toutiaoyan.compose.ui.components.sidebar.SidebarSeparator
import com.toutiaoyan.compose.ui.components.sidebar.SidebarSide
import com.toutiaoyan.compose.ui.components.sidebar.SidebarTrigger
import com.toutiaoyan.compose.ui.components.sidebar.SidebarVariant
import com.toutiaoyan.compose.ui.components.kbd.Kbd
import com.toutiaoyan.compose.ui.components.kbd.KbdGroup
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.card.CardSize

val sidebarDoc =
    ComponentDoc(
        id = "sidebar",
        title = "Sidebar",
        description = "A collapsible side navigation rail with grouped menu sections and a main content inset.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.*

            var open by remember { mutableStateOf(true) }
            SidebarProvider(open = open, onOpenChange = { open = it }) {
                Sidebar {
                    SidebarGroup {
                        SidebarGroupLabel("Application")
                        SidebarGroupContent {
                            SidebarMenu {
                                SidebarMenuItem("Home", onClick = {}, isActive = true)
                                SidebarMenuItem("Inbox", onClick = {})
                                SidebarMenuItem("Settings", onClick = {})
                            }
                        }
                    }
                }
                SidebarInset {
                    SidebarTrigger()
                    Text { "Main content" }
                }
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "App shell",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                        SidebarMenuItem("settings", "Settings")
                                    }
                                }
                            }
                            SidebarInset {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    SidebarTrigger(icon = { DocIcon(Lucide.Menu) })
                                    Breadcrumb {
                                        BreadcrumbLink("Building Your App", onClick = {})
                                        BreadcrumbSeparator()
                                        BreadcrumbPage("Data Fetching")
                                    }
                                }
                                Card(
                                    size = CardSize.Sm,
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                ) {
                                    Text { "Page content goes here." }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                        SidebarMenuItem("settings", "Settings")
                                    }
                                }
                            }
                            SidebarInset {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    SidebarTrigger(icon = { DocIcon(Lucide.Menu) })
                                    Breadcrumb {
                                        BreadcrumbLink("Building Your App", onClick = {})
                                        BreadcrumbSeparator()
                                        BreadcrumbPage("Data Fetching")
                                    }
                                }
                                Card(
                                    size = CardSize.Sm,
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                ) {
                                    Text { "Page content goes here." }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Right",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            SidebarInset {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    SidebarTrigger(icon = { DocIcon(Lucide.Menu) })
                                    Text { "Main content" }
                                }
                            }
                            Sidebar(side = SidebarSide.End) {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                        SidebarMenuItem("settings", "Settings")
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            SidebarInset {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    SidebarTrigger(icon = { DocIcon(Lucide.Menu) })
                                    Text { "Main content" }
                                }
                            }
                            Sidebar(side = SidebarSide.End) {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                        SidebarMenuItem("settings", "Settings")
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default side",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar(side = SidebarSide.Start) {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                        SidebarMenuItem("settings", "Settings")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar(side = SidebarSide.Start) {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                        SidebarMenuItem("settings", "Settings")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default width",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar(width = 240.dp) {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                        SidebarMenuItem("settings", "Settings")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar(width = 240.dp) {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                        SidebarMenuItem("settings", "Settings")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Not collapsible",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            collapsible = SidebarCollapsible.None,
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                        SidebarMenuItem("settings", "Settings")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            collapsible = SidebarCollapsible.None,
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                        SidebarMenuItem("settings", "Settings")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default collapsible",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            collapsible = SidebarCollapsible.Offcanvas,
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                        SidebarMenuItem("settings", "Settings")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            collapsible = SidebarCollapsible.Offcanvas,
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                        SidebarMenuItem("settings", "Settings")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Keyboard shortcut",
                    code =
                        """
                        SidebarProvider {
                            Sidebar { /* … */ }
                            SidebarInset {
                                SidebarTrigger()
                                KbdGroup { Kbd("⌘"); Kbd("B") }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                        SidebarMenuItem("settings", "Settings")
                                    }
                                }
                            }
                            SidebarInset {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    SidebarTrigger(icon = { DocIcon(Lucide.Menu) })
                                    Text { "Toggle with" }
                                    KbdGroup {
                                        Kbd("⌘")
                                        Kbd("B")
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Floating",
                    code =
                        """
                        SidebarProvider {
                            Sidebar(variant = SidebarVariant.Floating) { /* … */ }
                            SidebarInset { SidebarTrigger() }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar(variant = SidebarVariant.Floating) {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                        SidebarMenuItem("settings", "Settings")
                                    }
                                }
                            }
                            SidebarInset {
                                SidebarTrigger(icon = { DocIcon(Lucide.Menu) })
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Inset",
                    code =
                        """
                        SidebarProvider {
                            Sidebar(variant = SidebarVariant.Inset) { /* … */ }
                            SidebarInset { SidebarTrigger() }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar(variant = SidebarVariant.Inset) {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                        SidebarMenuItem("settings", "Settings")
                                    }
                                }
                            }
                            SidebarInset {
                                SidebarTrigger(icon = { DocIcon(Lucide.Menu) })
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Mobile sheet",
                    code =
                        """
                        SidebarProvider(mobile = true, open = true) {
                            Sidebar { /* … */ }
                            SidebarInset { SidebarTrigger() }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            mobile = true,
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                        SidebarMenuItem("settings", "Settings")
                                    }
                                }
                            }
                            SidebarInset {
                                SidebarTrigger(icon = { DocIcon(Lucide.Menu) })
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Official width",
                    code =
                        """
                        Sidebar(width = SidebarDefaults.Width) { /* 16rem */ }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar(width = SidebarDefaults.Width) {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                        SidebarMenuItem("settings", "Settings")
                                    }
                                }
                            }
                            SidebarInset {
                                SidebarTrigger(icon = { DocIcon(Lucide.Menu) })
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Icon collapse",
                    code =
                        """
                        SidebarProvider(
                            open = false,
                            collapsible = SidebarCollapsible.Icon,
                        ) {
                            Sidebar { /* … */ }
                            SidebarInset { SidebarTrigger() }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            collapsible = SidebarCollapsible.Icon,
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                        SidebarMenuItem("settings", "Settings")
                                    }
                                }
                            }
                            SidebarInset {
                                SidebarTrigger(icon = { DocIcon(Lucide.Menu) })
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox", disabled = true)
                                        SidebarMenuItem("settings", "Settings")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox", disabled = true)
                                        SidebarMenuItem("settings", "Settings")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home", disabled = false)
                                        SidebarMenuItem("inbox", "Inbox", disabled = false)
                                        SidebarMenuItem("settings", "Settings")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home", disabled = false)
                                        SidebarMenuItem("inbox", "Inbox", disabled = false)
                                        SidebarMenuItem("settings", "Settings")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default open",
                    code =
                        """
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(modifier = Modifier.height(260.dp)) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                        SidebarMenuItem("settings", "Settings")
                                    }
                                }
                            }
                            SidebarInset {
                                SidebarTrigger()
                                Text { "Uncontrolled; starts open." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(modifier = Modifier.height(260.dp)) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                        SidebarMenuItem("settings", "Settings")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Uncontrolled; starts open." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Menu button outline",
                    code =
                        """
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(modifier = Modifier.height(260.dp)) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem(
                                            "home",
                                            "Home",
                                            variant = SidebarMenuButtonVariant.Outline,
                                        )
                                        SidebarMenuItem("inbox", "Inbox")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Outline menu button." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(modifier = Modifier.height(260.dp)) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem(
                                            "home",
                                            "Home",
                                            variant = SidebarMenuButtonVariant.Outline,
                                        )
                                        SidebarMenuItem("inbox", "Inbox")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Outline menu button." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Active sub button",
                    code =
                        """
                        var active by remember { mutableStateOf("overview") }
                        SidebarProvider(modifier = Modifier.height(260.dp)) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("projects", "Projects")
                                        SidebarMenuSub {
                                            SidebarMenuSubButton("Overview", onClick = { active = "overview" }, isActive = active == "overview")
                                            SidebarMenuSubButton("Settings", onClick = { active = "settings" }, isActive = active == "settings")
                                        }
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Nested destination with isActive." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var active by remember { mutableStateOf("overview") }
                        SidebarProvider(modifier = Modifier.height(260.dp)) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("projects", "Projects")
                                        SidebarMenuSub {
                                            SidebarMenuSubButton(
                                                "Overview",
                                                onClick = { active = "overview" },
                                                isActive = active == "overview",
                                            )
                                            SidebarMenuSubButton(
                                                "Settings",
                                                onClick = { active = "settings" },
                                                isActive = active == "settings",
                                            )
                                        }
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Nested destination with isActive." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Forced active item",
                    code =
                        """
                        var active by remember { mutableStateOf("inbox") }
                        SidebarProvider(modifier = Modifier.height(260.dp)) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home", isActive = true)
                                        SidebarMenuItem("inbox", "Inbox")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Home stays visually active via isActive." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var active by remember { mutableStateOf("inbox") }
                        SidebarProvider(modifier = Modifier.height(260.dp)) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home", isActive = true)
                                        SidebarMenuItem("inbox", "Inbox")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Home stays visually active via isActive." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Accessible label",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar(ariaLabel = "Application") {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar(ariaLabel = "Application") {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default aria label",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar(ariaLabel = null) {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar(ariaLabel = null) {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Rail",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                    }
                                }
                            }
                            SidebarRail()
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                    }
                                }
                            }
                            SidebarRail()
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Header",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarHeader {
                                    Text { "Acme Inc" }
                                }
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarHeader {
                                    Text { "Acme Inc" }
                                }
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Footer",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                    }
                                }
                                SidebarFooter {
                                    Text { "v1.0.0" }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                    }
                                }
                                SidebarFooter {
                                    Text { "v1.0.0" }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Separator",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                    }
                                }
                                SidebarSeparator()
                                SidebarGroup(label = "Settings") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("settings", "Settings")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                    }
                                }
                                SidebarSeparator()
                                SidebarGroup(label = "Settings") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("settings", "Settings")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Group label",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup {
                                    SidebarGroupLabel("Application")
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup {
                                    SidebarGroupLabel("Application")
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Inset",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = "home", onItemClick = {}) {
                                        SidebarMenuItem("home", "Home")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = "home", onItemClick = {}) {
                                        SidebarMenuItem("home", "Home")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Menu",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Trigger",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = "home", onItemClick = {}) {
                                        SidebarMenuItem("home", "Home")
                                    }
                                }
                            }
                            SidebarInset {
                                SidebarTrigger()
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = "home", onItemClick = {}) {
                                        SidebarMenuItem("home", "Home")
                                    }
                                }
                            }
                            SidebarInset {
                                SidebarTrigger()
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Menu badge",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = "inbox", onItemClick = {}) {
                                        SidebarMenuItem("inbox", "Inbox", badge = "24")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = "inbox", onItemClick = {}) {
                                        SidebarMenuItem("inbox", "Inbox", badge = "24")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Input",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        var query by remember { mutableStateOf("") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarHeader {
                                    SidebarInput(value = query, onValueChange = { query = it }, placeholder = "Search")
                                }
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = "home", onItemClick = {}) {
                                        SidebarMenuItem("home", "Home")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        var query by remember { mutableStateOf("") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarHeader {
                                    SidebarInput(value = query, onValueChange = { query = it }, placeholder = "Search")
                                }
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = "home", onItemClick = {}) {
                                        SidebarMenuItem("home", "Home")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Menu skeleton",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = null, onItemClick = {}) {
                                        SidebarMenuSkeleton()
                                        SidebarMenuSkeleton(showIcon = true)
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = null, onItemClick = {}) {
                                        SidebarMenuSkeleton()
                                        SidebarMenuSkeleton(showIcon = true)
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default skeleton icon",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = null, onItemClick = {}) {
                                        SidebarMenuSkeleton(showIcon = false)
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = null, onItemClick = {}) {
                                        SidebarMenuSkeleton(showIcon = false)
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Group content",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup {
                                    SidebarGroupLabel("Application")
                                    SidebarGroupContent {
                                        SidebarMenu(activeId = "home", onItemClick = {}) {
                                            SidebarMenuItem("home", "Home")
                                        }
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup {
                                    SidebarGroupLabel("Application")
                                    SidebarGroupContent {
                                        SidebarMenu(activeId = "home", onItemClick = {}) {
                                            SidebarMenuItem("home", "Home")
                                        }
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Menu button",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = "home", onItemClick = {}) {
                                        SidebarMenuButton("Home", onClick = {})
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = "home", onItemClick = {}) {
                                        SidebarMenuButton("Home", onClick = {})
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default menu button variant",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = "home", onItemClick = {}) {
                                        SidebarMenuButton(
                                            "Home",
                                            onClick = {},
                                            variant = SidebarMenuButtonVariant.Default,
                                        )
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = "home", onItemClick = {}) {
                                        SidebarMenuButton(
                                            "Home",
                                            onClick = {},
                                            variant = SidebarMenuButtonVariant.Default,
                                        )
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default menu button size",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = "home", onItemClick = {}) {
                                        SidebarMenuButton(
                                            "Home",
                                            onClick = {},
                                            size = SidebarMenuButtonSize.Default,
                                        )
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = "home", onItemClick = {}) {
                                        SidebarMenuButton(
                                            "Home",
                                            onClick = {},
                                            size = SidebarMenuButtonSize.Default,
                                        )
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default active",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = "home", onItemClick = {}) {
                                        SidebarMenuButton("Home", onClick = {}, isActive = false)
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = "home", onItemClick = {}) {
                                        SidebarMenuButton("Home", onClick = {}, isActive = false)
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default tooltip",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = "home", onItemClick = {}) {
                                        SidebarMenuButton("Home", onClick = {}, tooltip = null)
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = "home", onItemClick = {}) {
                                        SidebarMenuButton("Home", onClick = {}, tooltip = null)
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Group",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = "home", onItemClick = {}) {
                                        SidebarMenuItem("home", "Home")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = "home", onItemClick = {}) {
                                        SidebarMenuItem("home", "Home")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Menu sub",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = "overview", onItemClick = {}) {
                                        SidebarMenuItem("projects", "Projects")
                                        SidebarMenuSub {
                                            SidebarMenuSubButton("Overview", onClick = {}, isActive = true)
                                            SidebarMenuSubButton("Settings", onClick = {})
                                        }
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = "overview", onItemClick = {}) {
                                        SidebarMenuItem("projects", "Projects")
                                        SidebarMenuSub {
                                            SidebarMenuSubButton("Overview", onClick = {}, isActive = true)
                                            SidebarMenuSubButton("Settings", onClick = {})
                                        }
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Menu action",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = "home", onItemClick = {}) {
                                        SidebarMenuButton(onClick = {}, label = "Home") {
                                            Text { "Home" }
                                            SidebarMenuAction(onClick = {}) { DocIcon(Lucide.Plus) }
                                        }
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = "home", onItemClick = {}) {
                                        SidebarMenuButton(onClick = {}, label = "Home") {
                                            Text { "Home" }
                                            SidebarMenuAction(onClick = {}) { DocIcon(Lucide.Plus) }
                                        }
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Group action",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        SidebarGroupLabel("Application")
                                        SidebarGroupAction(onClick = {}) { DocIcon(Lucide.Plus) }
                                    }
                                    SidebarGroupContent {
                                        SidebarMenu(activeId = "home", onItemClick = {}) {
                                            SidebarMenuItem("home", "Home")
                                        }
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        SidebarGroupLabel("Application")
                                        SidebarGroupAction(onClick = {}) { DocIcon(Lucide.Plus) }
                                    }
                                    SidebarGroupContent {
                                        SidebarMenu(activeId = "home", onItemClick = {}) {
                                            SidebarMenuItem("home", "Home")
                                        }
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Provider",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                        SidebarMenuItem("inbox", "Inbox")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Menu item",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("home") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("home", "Home")
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Menu sub button",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("overview") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("projects", "Projects")
                                        SidebarMenuSub {
                                            SidebarMenuSubButton("Overview", onClick = { active = "overview" }, isActive = active == "overview")
                                            SidebarMenuSubButton("Settings", onClick = { active = "settings" }, isActive = active == "settings")
                                        }
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("overview") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("projects", "Projects")
                                        SidebarMenuSub {
                                            SidebarMenuSubButton(
                                                "Overview",
                                                onClick = { active = "overview" },
                                                isActive = active == "overview",
                                            )
                                            SidebarMenuSubButton(
                                                "Settings",
                                                onClick = { active = "settings" },
                                                isActive = active == "settings",
                                            )
                                        }
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default sub button size",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("overview") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("projects", "Projects")
                                        SidebarMenuSub {
                                            SidebarMenuSubButton(
                                                "Overview",
                                                onClick = { active = "overview" },
                                                isActive = active == "overview",
                                                size = SidebarMenuButtonSize.Default,
                                            )
                                            SidebarMenuSubButton(
                                                "Settings",
                                                onClick = { active = "settings" },
                                                isActive = active == "settings",
                                                size = SidebarMenuButtonSize.Default,
                                            )
                                        }
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        var active by remember { mutableStateOf("overview") }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = active, onItemClick = { active = it }) {
                                        SidebarMenuItem("projects", "Projects")
                                        SidebarMenuSub {
                                            SidebarMenuSubButton(
                                                "Overview",
                                                onClick = { active = "overview" },
                                                isActive = active == "overview",
                                                size = SidebarMenuButtonSize.Default,
                                            )
                                            SidebarMenuSubButton(
                                                "Settings",
                                                onClick = { active = "settings" },
                                                isActive = active == "settings",
                                                size = SidebarMenuButtonSize.Default,
                                            )
                                        }
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default sub active",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = "projects", onItemClick = {}) {
                                        SidebarMenuItem("projects", "Projects")
                                        SidebarMenuSub {
                                            SidebarMenuSubButton("Overview", onClick = {}, isActive = false)
                                        }
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = "projects", onItemClick = {}) {
                                        SidebarMenuItem("projects", "Projects")
                                        SidebarMenuSub {
                                            SidebarMenuSubButton("Overview", onClick = {}, isActive = false)
                                        }
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Menu sub item",
                    code =
                        """
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = "overview", onItemClick = {}) {
                                        SidebarMenuItem("projects", "Projects")
                                        SidebarMenuSub {
                                            SidebarMenuSubItem("overview", "Overview")
                                            SidebarMenuSubItem("settings", "Settings")
                                        }
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(true) }
                        SidebarProvider(
                            open = open,
                            onOpenChange = { open = it },
                            modifier = Modifier.height(260.dp),
                        ) {
                            Sidebar {
                                SidebarGroup(label = "Application") {
                                    SidebarMenu(activeId = "overview", onItemClick = {}) {
                                        SidebarMenuItem("projects", "Projects")
                                        SidebarMenuSub {
                                            SidebarMenuSubItem("overview", "Overview")
                                            SidebarMenuSubItem("settings", "Settings")
                                        }
                                    }
                                }
                            }
                            SidebarInset {
                                Text { "Page content goes here." }
                            }
                        }
                    },
                ),
            ),
    )
