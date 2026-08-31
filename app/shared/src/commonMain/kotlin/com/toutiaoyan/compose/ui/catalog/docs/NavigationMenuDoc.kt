@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.components.navigationmenu.NavigationMenu
import com.toutiaoyan.compose.ui.components.navigationmenu.NavigationMenuContent
import com.toutiaoyan.compose.ui.components.navigationmenu.NavigationMenuIndicator
import com.toutiaoyan.compose.ui.components.navigationmenu.NavigationMenuItem
import com.toutiaoyan.compose.ui.components.navigationmenu.NavigationMenuLink
import com.toutiaoyan.compose.ui.components.navigationmenu.NavigationMenuList
import com.toutiaoyan.compose.ui.components.navigationmenu.NavigationMenuOrientation
import com.toutiaoyan.compose.ui.components.navigationmenu.NavigationMenuTrigger
import com.toutiaoyan.compose.ui.components.navigationmenu.NavigationMenuPositioner
import com.toutiaoyan.compose.ui.components.overlay.PopupPlacement
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant

private data class NavLink(val title: String, val description: String)

private val gettingStartedLinks =
    listOf(
        NavLink("Introduction", "Re-usable components built with Radix UI and Tailwind CSS."),
        NavLink("Installation", "How to install dependencies and structure your app."),
        NavLink("Typography", "Styles for headings, paragraphs, lists, and more."),
    )

@Composable
private fun NavMenuPanel(links: List<NavLink>) {
    Column(verticalArrangement = Arrangement.spacedBy(twSpacing.md)) {
        links.forEach { link ->
            Column(verticalArrangement = Arrangement.spacedBy(twSpacing.xs)) {
                Text(variant = TextVariant.LabelSmall) { link.title }
                Text(variant = TextVariant.BodySmall, textStyle = TextColors.Muted) { link.description }
            }
        }
    }
}

val navigationMenuDoc =
    ComponentDoc(
        id = "navigation-menu",
        title = "Navigation Menu",
        description = "A horizontal top-level site navigation row where some items open a panel of related links.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.navigationmenu.NavigationMenu
            import com.toutiaoyan.compose.ui.components.navigationmenu.NavigationMenuContent
            import com.toutiaoyan.compose.ui.components.navigationmenu.NavigationMenuItem
            import com.toutiaoyan.compose.ui.components.navigationmenu.NavigationMenuLink
            import com.toutiaoyan.compose.ui.components.navigationmenu.NavigationMenuList
            import com.toutiaoyan.compose.ui.components.navigationmenu.NavigationMenuTrigger

            NavigationMenu {
                NavigationMenuList {
                    NavigationMenuItem(value = "home") {
                        NavigationMenuLink("Home", onClick = {})
                    }
                    NavigationMenuItem(value = "getting-started") {
                        NavigationMenuTrigger("Getting started")
                        NavigationMenuContent { /* titled list of links */ }
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
                        NavigationMenu {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {})
                                }
                                NavigationMenuItem(value = "getting-started") {
                                    NavigationMenuTrigger("Getting started")
                                    NavigationMenuContent {
                                        Column {
                                            Text(variant = TextVariant.LabelSmall) { "Introduction" }
                                            Text(variant = TextVariant.BodySmall, textStyle = TextColors.Muted) {
                                                "Re-usable components built with Radix UI and Tailwind CSS."
                                            }
                                        }
                                    }
                                }
                                NavigationMenuItem(value = "docs") {
                                    NavigationMenuLink("Docs", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        NavigationMenu {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {})
                                }
                                NavigationMenuItem(value = "getting-started") {
                                    NavigationMenuTrigger("Getting started")
                                    NavigationMenuContent { NavMenuPanel(gettingStartedLinks) }
                                    NavigationMenuIndicator()
                                }
                                NavigationMenuItem(value = "docs") {
                                    NavigationMenuLink("Docs", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Horizontal",
                    code =
                        """
                        NavigationMenu(orientation = NavigationMenuOrientation.Horizontal) {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {})
                                }
                                NavigationMenuItem(value = "docs") {
                                    NavigationMenuLink("Docs", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        NavigationMenu(orientation = NavigationMenuOrientation.Horizontal) {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {})
                                }
                                NavigationMenuItem(value = "docs") {
                                    NavigationMenuLink("Docs", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default orientation",
                    code =
                        """
                        NavigationMenu(orientation = NavigationMenuOrientation.Horizontal) {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {})
                                }
                                NavigationMenuItem(value = "docs") {
                                    NavigationMenuLink("Docs", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        NavigationMenu(orientation = NavigationMenuOrientation.Horizontal) {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {})
                                }
                                NavigationMenuItem(value = "docs") {
                                    NavigationMenuLink("Docs", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Vertical",
                    code =
                        """
                        NavigationMenu(orientation = NavigationMenuOrientation.Vertical) {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {})
                                }
                                NavigationMenuItem(value = "getting-started") {
                                    NavigationMenuTrigger("Getting started")
                                    NavigationMenuContent { NavMenuPanel(gettingStartedLinks) }
                                }
                                NavigationMenuItem(value = "docs") {
                                    NavigationMenuLink("Docs", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        NavigationMenu(orientation = NavigationMenuOrientation.Vertical) {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {})
                                }
                                NavigationMenuItem(value = "getting-started") {
                                    NavigationMenuTrigger("Getting started")
                                    NavigationMenuContent { NavMenuPanel(gettingStartedLinks) }
                                }
                                NavigationMenuItem(value = "docs") {
                                    NavigationMenuLink("Docs", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Delay",
                    code =
                        """
                        NavigationMenu(delayDuration = 200) {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {})
                                }
                                NavigationMenuItem(value = "docs") {
                                    NavigationMenuLink("Docs", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        NavigationMenu(delayDuration = 200) {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {})
                                }
                                NavigationMenuItem(value = "docs") {
                                    NavigationMenuLink("Docs", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default delay",
                    code =
                        """
                        NavigationMenu(delayDuration = 0) {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {})
                                }
                                NavigationMenuItem(value = "docs") {
                                    NavigationMenuLink("Docs", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        NavigationMenu(delayDuration = 0) {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {})
                                }
                                NavigationMenuItem(value = "docs") {
                                    NavigationMenuLink("Docs", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Skip delay",
                    code =
                        """
                        NavigationMenu(delayDuration = 200, skipDelayDuration = 300) {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {})
                                }
                                NavigationMenuItem(value = "docs") {
                                    NavigationMenuLink("Docs", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        NavigationMenu(delayDuration = 200, skipDelayDuration = 300) {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {})
                                }
                                NavigationMenuItem(value = "docs") {
                                    NavigationMenuLink("Docs", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default skip delay",
                    code =
                        """
                        NavigationMenu(skipDelayDuration = 300) {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {})
                                }
                                NavigationMenuItem(value = "docs") {
                                    NavigationMenuLink("Docs", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        NavigationMenu(skipDelayDuration = 300) {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {})
                                }
                                NavigationMenuItem(value = "docs") {
                                    NavigationMenuLink("Docs", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code =
                        """
                        NavigationMenu {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {})
                                }
                                NavigationMenuItem(value = "docs") {
                                    NavigationMenuLink("Docs", onClick = {}, disabled = true)
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        NavigationMenu {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {})
                                }
                                NavigationMenuItem(value = "docs") {
                                    NavigationMenuLink("Docs", onClick = {}, disabled = true)
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        NavigationMenu {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {}, disabled = false)
                                }
                                NavigationMenuItem(value = "docs") {
                                    NavigationMenuLink("Docs", onClick = {}, disabled = false)
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        NavigationMenu {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {}, disabled = false)
                                }
                                NavigationMenuItem(value = "docs") {
                                    NavigationMenuLink("Docs", onClick = {}, disabled = false)
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
                        NavigationMenu(value = open, onValueChange = { open = it }) {
                            NavigationMenuList {
                                NavigationMenuItem(value = "getting-started") {
                                    NavigationMenuTrigger("Getting started")
                                    NavigationMenuContent {
                                        Text { "Re-usable components." }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf<String?>(null) }
                        NavigationMenu(value = open, onValueChange = { open = it }) {
                            NavigationMenuList {
                                NavigationMenuItem(value = "getting-started") {
                                    NavigationMenuTrigger("Getting started")
                                    NavigationMenuContent { NavMenuPanel(gettingStartedLinks) }
                                    NavigationMenuIndicator()
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Active link",
                    code =
                        """
                        NavigationMenu {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {}, active = true)
                                }
                                NavigationMenuItem(value = "docs") {
                                    NavigationMenuLink("Docs", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        NavigationMenu {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {}, active = true)
                                }
                                NavigationMenuItem(value = "docs") {
                                    NavigationMenuLink("Docs", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Accessible label",
                    code =
                        """
                        NavigationMenu(ariaLabel = "Main") {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {})
                                }
                                NavigationMenuItem(value = "docs") {
                                    NavigationMenuLink("Docs", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        NavigationMenu(ariaLabel = "Main") {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {})
                                }
                                NavigationMenuItem(value = "docs") {
                                    NavigationMenuLink("Docs", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default aria label",
                    code =
                        """
                        NavigationMenu(ariaLabel = null) {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {})
                                }
                                NavigationMenuItem(value = "docs") {
                                    NavigationMenuLink("Docs", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        NavigationMenu(ariaLabel = null) {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {})
                                }
                                NavigationMenuItem(value = "docs") {
                                    NavigationMenuLink("Docs", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Viewport",
                    code =
                        """
                        NavigationMenu {
                            NavigationMenuList {
                                NavigationMenuItem(value = "getting-started") {
                                    NavigationMenuTrigger("Getting started")
                                    NavigationMenuPositioner {
                                        NavigationMenuContent {
                                            Text { "Re-usable components built with Radix UI and Tailwind CSS." }
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        NavigationMenu {
                            NavigationMenuList {
                                NavigationMenuItem(value = "getting-started") {
                                    NavigationMenuTrigger("Getting started")
                                    NavigationMenuPositioner {
                                        NavigationMenuContent { NavMenuPanel(gettingStartedLinks) }
                                    }
                                    NavigationMenuIndicator()
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "List",
                    code =
                        """
                        NavigationMenu {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {})
                                }
                                NavigationMenuItem(value = "docs") {
                                    NavigationMenuLink("Docs", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        NavigationMenu {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {})
                                }
                                NavigationMenuItem(value = "docs") {
                                    NavigationMenuLink("Docs", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Item",
                    code =
                        """
                        NavigationMenu {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        NavigationMenu {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Link",
                    code =
                        """
                        NavigationMenu {
                            NavigationMenuList {
                                NavigationMenuItem(value = "docs") {
                                    NavigationMenuLink("Docs", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        NavigationMenu {
                            NavigationMenuList {
                                NavigationMenuItem(value = "docs") {
                                    NavigationMenuLink("Docs", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Trigger",
                    code =
                        """
                        NavigationMenu {
                            NavigationMenuList {
                                NavigationMenuItem(value = "getting-started") {
                                    NavigationMenuTrigger("Getting started")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        NavigationMenu {
                            NavigationMenuList {
                                NavigationMenuItem(value = "getting-started") {
                                    NavigationMenuTrigger("Getting started")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Content",
                    code =
                        """
                        NavigationMenu {
                            NavigationMenuList {
                                NavigationMenuItem(value = "getting-started") {
                                    NavigationMenuTrigger("Getting started")
                                    NavigationMenuContent {
                                        Text { "Re-usable components built with Radix UI and Tailwind CSS." }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        NavigationMenu {
                            NavigationMenuList {
                                NavigationMenuItem(value = "getting-started") {
                                    NavigationMenuTrigger("Getting started")
                                    NavigationMenuContent {
                                        Text { "Re-usable components built with Radix UI and Tailwind CSS." }
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default dismiss",
                    code =
                        """
                        NavigationMenu {
                            NavigationMenuList {
                                NavigationMenuItem(value = "getting-started") {
                                    NavigationMenuTrigger("Getting started")
                                    NavigationMenuContent(dismissOnClickOutside = true) {
                                        Text { "Re-usable components built with Radix UI and Tailwind CSS." }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        NavigationMenu {
                            NavigationMenuList {
                                NavigationMenuItem(value = "getting-started") {
                                    NavigationMenuTrigger("Getting started")
                                    NavigationMenuContent(dismissOnClickOutside = true) {
                                        Text { "Re-usable components built with Radix UI and Tailwind CSS." }
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
                        NavigationMenu {
                            NavigationMenuList {
                                NavigationMenuItem(value = "getting-started") {
                                    NavigationMenuTrigger("Getting started")
                                    NavigationMenuContent(sideOffset = 8.dp) {
                                        Text { "Re-usable components built with Radix UI and Tailwind CSS." }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        NavigationMenu {
                            NavigationMenuList {
                                NavigationMenuItem(value = "getting-started") {
                                    NavigationMenuTrigger("Getting started")
                                    NavigationMenuContent(sideOffset = 8.dp) {
                                        Text { "Re-usable components built with Radix UI and Tailwind CSS." }
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
                        NavigationMenu {
                            NavigationMenuList {
                                NavigationMenuItem(value = "getting-started") {
                                    NavigationMenuTrigger("Getting started")
                                    NavigationMenuContent(sideOffset = 8.dp) {
                                        Text { "Re-usable components built with Radix UI and Tailwind CSS." }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        NavigationMenu {
                            NavigationMenuList {
                                NavigationMenuItem(value = "getting-started") {
                                    NavigationMenuTrigger("Getting started")
                                    NavigationMenuContent(sideOffset = 8.dp) {
                                        Text { "Re-usable components built with Radix UI and Tailwind CSS." }
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
                        NavigationMenu {
                            NavigationMenuList {
                                NavigationMenuItem(value = "getting-started") {
                                    NavigationMenuTrigger("Getting started")
                                    NavigationMenuContent(side = PopupPlacement.Bottom) {
                                        Text { "Re-usable components built with Radix UI and Tailwind CSS." }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        NavigationMenu {
                            NavigationMenuList {
                                NavigationMenuItem(value = "getting-started") {
                                    NavigationMenuTrigger("Getting started")
                                    NavigationMenuContent(side = PopupPlacement.Bottom) {
                                        Text { "Re-usable components built with Radix UI and Tailwind CSS." }
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Indicator",
                    code =
                        """
                        NavigationMenu {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {})
                                    NavigationMenuIndicator()
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        NavigationMenu {
                            NavigationMenuList {
                                NavigationMenuItem(value = "home") {
                                    NavigationMenuLink("Home", onClick = {})
                                    NavigationMenuIndicator()
                                }
                            }
                        }
                    },
                ),
            ),
    )
