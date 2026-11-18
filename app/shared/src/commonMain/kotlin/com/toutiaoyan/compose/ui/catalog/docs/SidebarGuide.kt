package com.toutiaoyan.compose.ui.catalog.docs

internal val sidebarUsageCode =
    """
    import androidx.compose.runtime.Composable
    import com.toutiaoyan.compose.ui.components.sidebar.*
    import com.toutiaoyan.compose.ui.components.text.Text

    @Composable
    fun SidebarLayout() {
        SidebarProvider {
            Sidebar {
                SidebarHeader { Text { "Application" } }
                SidebarContent {
                    SidebarGroup { Text { "Home" } }
                    SidebarGroup { Text { "Projects" } }
                }
                SidebarFooter { Text { "Account" } }
            }
            SidebarInset {
                SidebarTrigger()
                Text { "Main content" }
            }
        }
    }
    """.trimIndent()

internal val sidebarCompositionCode =
    """
    SidebarProvider
    ├── Sidebar
    │   ├── SidebarHeader
    │   ├── SidebarContent
    │   │   └── SidebarGroup
    │   │       ├── SidebarGroupLabel
    │   │       ├── SidebarGroupAction
    │   │       └── SidebarGroupContent
    │   │           └── SidebarMenu
    │   │               └── SidebarMenuItem
    │   │                   ├── SidebarMenuButton
    │   │                   ├── SidebarMenuAction
    │   │                   ├── SidebarMenuBadge
    │   │                   └── SidebarMenuSub
    │   │                       └── SidebarMenuSubItem
    │   │                           └── SidebarMenuSubButton
    │   └── SidebarFooter
    ├── SidebarRail
    └── SidebarInset
        └── SidebarTrigger
    """.trimIndent()

internal val sidebarGuide =
    listOf(
        ComponentGuideSection(
            "Structure",
            listOf(
                ComponentGuideBlock.Bullets(
                    listOf(
                        "`SidebarProvider` owns visibility and shares it with the panel and its controls.",
                        "`SidebarHeader` and `SidebarFooter` frame the scrollable `SidebarContent`.",
                        "`SidebarGroup` collects related destinations and optional controls.",
                        "`SidebarMenuItem` holds a destination, its badge or action, and any nested menu.",
                        "`SidebarInset` fills the space beside the panel.",
                        "`SidebarTrigger` and `SidebarRail` toggle the same provider.",
                    ),
                ),
            ),
        ),
        ComponentGuideSection(
            "SidebarProvider",
            listOf(
                ComponentGuideBlock.Paragraph(
                    "Wrap the panel and its controls in a provider. Desktop and mobile visibility are independent.",
                ),
                ComponentGuideBlock.Table(
                    listOf("Parameter", "Type", "Purpose"),
                    listOf(
                        listOf("`defaultOpen`", "`Boolean`", "Initial desktop visibility. Defaults to true."),
                        listOf("`open`", "`Boolean?`", "Caller-owned desktop visibility."),
                        listOf("`onOpenChange`", "`((Boolean) -> Unit)?`", "Receives desktop visibility requests."),
                        listOf("`openMobile`", "`Boolean?`", "Caller-owned mobile drawer visibility."),
                        listOf(
                            "`onOpenMobileChange`",
                            "`((Boolean) -> Unit)?`",
                            "Receives mobile visibility requests.",
                        ),
                    ),
                ),
            ),
        ),
        ComponentGuideSection(
            "Width",
            listOf(
                ComponentGuideBlock.Paragraph(
                    "The panel is 256 dp on desktop, with a 48 dp icon rail and a 288 dp forced mobile drawer. " +
                        "Automatic mobile mode uses 75% of the viewport, capped at 384 dp from 640 dp. " +
                        "Use `width` and `iconWidth` to choose desktop dimensions or supply a Sheet width in `style`.",
                ),
                ComponentGuideBlock.Code("Sidebar(width = 320.dp, iconWidth = 48.dp) {\n    // Sidebar sections\n}"),
            ),
            level = 3,
        ),
        ComponentGuideSection(
            "Keyboard Shortcut",
            listOf(
                ComponentGuideBlock.Paragraph(
                    "Cmd+B or Ctrl+B toggles the panel when focus is inside the provider. " +
                        "Use `shortcutKey` to change the key or `shortcutEnabled = false` to turn it off.",
                ),
            ),
            level = 3,
        ),
        ComponentGuideSection(
            "Sidebar",
            listOf(
                ComponentGuideBlock.Table(
                    listOf("Parameter", "Where", "Values"),
                    listOf(
                        listOf("`side`", "`Sidebar`", "`Start` or `End`, relative to layout direction."),
                        listOf("`variant`", "`Sidebar`", "`Sidebar`, `Floating`, or `Inset`."),
                        listOf("`collapsible`", "`SidebarProvider`", "`Offcanvas`, `Icon`, or `None`."),
                    ),
                ),
                ComponentGuideBlock.Paragraph("Pair the `Inset` variant with `SidebarInset` for the main content."),
                ComponentGuideBlock.Code(
                    """
                    SidebarProvider {
                        Sidebar(variant = SidebarVariant.Inset) {
                            SidebarContent { SidebarGroup { Text { "Home" } } }
                        }
                        SidebarInset { Text { "Main content" } }
                    }
                    """.trimIndent(),
                ),
            ),
        ),
        ComponentGuideSection(
            "useSidebar",
            listOf(
                ComponentGuideBlock.Paragraph(
                    "Call `sidebarState()` inside the provider to read state or request a change.",
                ),
                ComponentGuideBlock.Table(
                    listOf("Member", "Purpose"),
                    listOf(
                        listOf("`open` / `setOpen`", "Read or update desktop visibility."),
                        listOf("`openMobile` / `setOpenMobile`", "Read or update mobile visibility."),
                        listOf("`isMobile`", "Read the active responsive mode."),
                        listOf("`iconCollapsed`", "Read whether the desktop panel currently shows icons only."),
                        listOf("`toggle`", "Toggle visibility for the active mode."),
                    ),
                ),
            ),
        ),
        sidebarPart(
            "SidebarHeader",
            "Place branding or a workspace control above the scrollable panel content.",
            """
            SidebarHeader {
                SidebarMenu {
                    SidebarMenuItem {
                        SidebarMenuButton("Select Workspace", onClick = onChooseWorkspace)
                    }
                }
            }
            """,
        ),
        sidebarPart(
            "SidebarFooter",
            "Use the footer for controls that stay visible while the panel scrolls.",
            """
            SidebarFooter {
                SidebarMenu {
                    SidebarMenuItem {
                        SidebarMenuButton("Username", onClick = onOpenAccount)
                    }
                }
            }
            """,
        ),
        sidebarPart(
            "SidebarContent",
            "Put navigation groups inside the scrollable area.",
            """
            SidebarContent {
                SidebarGroup { Text { "Application" } }
                SidebarGroup { Text { "Help" } }
            }
            """,
        ),
        sidebarPart(
            "SidebarGroup",
            "Group related destinations. Use the label, content and action slots for their respective roles.",
            """
            SidebarGroup {
                SidebarGroupLabel("Application")
                SidebarGroupAction(onClick = onAddProject, label = "Add Project") {
                    Text { "+" }
                }
                SidebarGroupContent { Text { "Project destinations" } }
            }
            """,
        ),
        sidebarPart(
            "SidebarMenu",
            "Build a list of destinations from menu item slots.",
            """
            SidebarMenu {
                projects.forEach { project ->
                    SidebarMenuItem {
                        SidebarMenuButton(project.name, onClick = { onOpenProject(project) })
                    }
                }
            }
            """,
        ),
        sidebarPart(
            "SidebarMenuButton",
            "Use `isActive` to indicate the current destination. Supply a caller-owned navigation action to `onClick`.",
            """
            SidebarMenuItem {
                SidebarMenuButton("Home", onClick = onOpenHome, isActive = true)
            }
            """,
        ),
        sidebarPart(
            "SidebarMenuAction",
            "Give an item a separate action beside its destination.",
            """
            SidebarMenuItem {
                SidebarMenuButton("Home", onClick = onOpenHome)
                SidebarMenuAction(onClick = onAddProject, label = "Add Project") {
                    Text { "+" }
                }
            }
            """,
        ),
        sidebarPart(
            "SidebarMenuSub",
            "Nest destinations under their parent menu item.",
            """
            SidebarMenuItem {
                SidebarMenuButton("Projects", onClick = onOpenProjects)
                SidebarMenuSub {
                    SidebarMenuSubItem {
                        SidebarMenuSubButton("Overview", onClick = onOpenOverview)
                    }
                }
            }
            """,
        ),
        sidebarPart(
            "SidebarMenuBadge",
            "Display supplemental information such as an unread count next to the destination.",
            """
            SidebarMenuItem {
                SidebarMenuButton("Inbox", onClick = onOpenInbox)
                SidebarMenuBadge("24")
            }
            """,
        ),
        sidebarPart(
            "SidebarMenuSkeleton",
            "Reserve space for destinations while they load.",
            """
            SidebarMenu {
                repeat(5) {
                    SidebarMenuItem { SidebarMenuSkeleton() }
                }
            }
            """,
        ),
        sidebarPart(
            "SidebarTrigger",
            "The built-in trigger uses the provider. A custom control can call the same toggle callback.",
            """
            val state = sidebarState()
            Button("Toggle Sidebar", onClick = state.toggle)
            """,
        ),
        sidebarPart(
            "SidebarRail",
            "Place the edge toggle next to its panel as a direct child of the provider.",
            """
            SidebarProvider {
                Sidebar { SidebarContent { SidebarGroup { Text { "Home" } } } }
                SidebarRail()
                SidebarInset { SidebarTrigger() }
            }
            """,
        ),
    )

internal val sidebarBeforeRtlGuide =
    listOf(
        ComponentGuideSection(
            "Theming",
            listOf(
                ComponentGuideBlock.Paragraph(
                    "Sidebar parts read the active theme's `sidebar`, `sidebarForeground`, `sidebarPrimary`, " +
                        "`sidebarPrimaryForeground`, `sidebarAccent`, `sidebarAccentForeground`, " +
                        "`sidebarBorder`, and `sidebarRing` color roles.",
                ),
            ),
        ),
        sidebarPart(
            "Styling",
            "Control presentation with `Modifier`, `Style` and provider state.",
            """
            SidebarContent {
                if (!sidebarState().iconCollapsed) {
                    SidebarGroup { Text { "Projects" } }
                }
            }
            """,
        ),
    )

internal val sidebarChangelogGuide =
    listOf(
        ComponentGuideSection(
            "Changelog",
            listOf(
                ComponentGuideBlock.Paragraph(
                    "Language direction determines the physical edge for `SidebarSide.Start` and `SidebarSide.End`.",
                ),
            ),
        ),
        ComponentGuideSection(
            "RTL Support",
            listOf(
                ComponentGuideBlock.Paragraph(
                    "Provide `LocalLayoutDirection` in your localized subtree to move the panel to the correct edge.",
                ),
            ),
            level = 3,
        ),
    )

private fun sidebarPart(
    title: String,
    description: String,
    code: String,
) = ComponentGuideSection(
    title,
    listOf(
        ComponentGuideBlock.Paragraph(description),
        ComponentGuideBlock.Code(code.trimIndent()),
    ),
)
