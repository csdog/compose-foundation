@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.sidebar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.card.CardSize
import kotlin.test.Test
import com.toutiaoyan.compose.ui.components.breadcrumb.Breadcrumb
import com.toutiaoyan.compose.ui.components.breadcrumb.BreadcrumbLink
import com.toutiaoyan.compose.ui.components.breadcrumb.BreadcrumbPage
import com.toutiaoyan.compose.ui.components.breadcrumb.BreadcrumbSeparator
import com.toutiaoyan.compose.ui.components.card.Card
import com.toutiaoyan.compose.ui.components.text.Text

class SidebarScreenshotTest : ScreenshotTest() {
    @Composable
    private fun ApplicationMenu() {
        SidebarGroup(label = "Application") {
            SidebarMenu(activeId = "home", onItemClick = {}) {
                SidebarMenuItem("home", "Home")
                SidebarMenuItem("inbox", "Inbox")
                SidebarMenuItem("settings", "Settings")
            }
        }
    }

    private fun states(
        darkTheme: Boolean,
        open: Boolean,
    ) {
        val suffix = if (open) "_expanded" else "_collapsed"
        snapshot("sidebar$suffix", darkTheme = darkTheme) {
            SidebarProvider(open = open, onOpenChange = {}, modifier = Modifier.height(200.dp)) {
                Sidebar {
                    ApplicationMenu()
                }
                SidebarInset {
                    SidebarTrigger()
                    Text { "Main content" }
                }
            }
        }
    }

    private fun appShell(darkTheme: Boolean) {
        snapshot("sidebar_app_shell", darkTheme = darkTheme) {
            SidebarProvider(open = true, onOpenChange = {}, modifier = Modifier.height(260.dp)) {
                Sidebar {
                    ApplicationMenu()
                }
                SidebarInset {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SidebarTrigger()
                        Breadcrumb {
                            BreadcrumbLink("Building Your App", onClick = {})
                            BreadcrumbSeparator()
                            BreadcrumbPage("Data Fetching")
                        }
                    }
                    Card(size = CardSize.Sm, modifier = Modifier.padding(horizontal = 8.dp)) {
                        Text { "Page content goes here." }
                    }
                }
            }
        }
    }

    @Test
    fun expanded_light() = states(darkTheme = false, open = true)

    @Test
    fun expanded_dark() = states(darkTheme = true, open = true)

    @Test
    fun collapsed_light() = states(darkTheme = false, open = false)

    @Test
    fun collapsed_dark() = states(darkTheme = true, open = false)

    private fun iconCollapsed(darkTheme: Boolean) {
        snapshot("sidebar_icon_collapsed", darkTheme = darkTheme) {
            SidebarProvider(
                open = false,
                onOpenChange = {},
                collapsible = SidebarCollapsible.Icon,
                modifier = Modifier.height(200.dp),
            ) {
                Sidebar {
                    ApplicationMenu()
                }
                SidebarInset {
                    SidebarTrigger()
                    Text { "Main content" }
                }
            }
        }
    }

    @Test
    fun icon_collapsed_light() = iconCollapsed(darkTheme = false)

    @Test
    fun icon_collapsed_dark() = iconCollapsed(darkTheme = true)

    @Test
    fun app_shell_light() = appShell(darkTheme = false)

    @Test
    fun app_shell_dark() = appShell(darkTheme = true)

    private fun triggerFocused(darkTheme: Boolean) {
        snapshotFocused("sidebar_trigger_focused", focusTag = "sidebar-trigger", darkTheme = darkTheme) {
            SidebarProvider(open = true, onOpenChange = {}, modifier = Modifier.height(200.dp)) {
                SidebarInset {
                    SidebarTrigger(modifier = Modifier.testTag("sidebar-trigger"))
                    Text { "Main content" }
                }
            }
        }
    }

    @Test
    fun trigger_focused_light() = triggerFocused(darkTheme = false)

    private fun floating(darkTheme: Boolean) {
        snapshot("sidebar_floating", darkTheme = darkTheme) {
            SidebarProvider(open = true, onOpenChange = {}, modifier = Modifier.height(200.dp)) {
                Sidebar(variant = SidebarVariant.Floating) {
                    ApplicationMenu()
                }
                SidebarInset {
                    SidebarTrigger()
                    Text { "Main content" }
                }
            }
        }
    }

    @Test
    fun floating_light() = floating(darkTheme = false)

    @Test
    fun floating_dark() = floating(darkTheme = true)

    private fun inset(darkTheme: Boolean) {
        snapshot("sidebar_inset", darkTheme = darkTheme) {
            SidebarProvider(open = true, onOpenChange = {}, modifier = Modifier.height(200.dp)) {
                Sidebar(variant = SidebarVariant.Inset) {
                    ApplicationMenu()
                }
                SidebarInset {
                    SidebarTrigger()
                    Text { "Main content" }
                }
            }
        }
    }

    @Test
    fun inset_light() = inset(darkTheme = false)

    @Test
    fun inset_dark() = inset(darkTheme = true)

    private fun mobile(darkTheme: Boolean) {
        snapshot("sidebar_mobile", darkTheme = darkTheme) {
            SidebarProvider(
                open = true,
                onOpenChange = {},
                mobile = true,
                modifier = Modifier.height(200.dp),
            ) {
                Sidebar {
                    ApplicationMenu()
                }
                SidebarInset {
                    SidebarTrigger()
                    Text { "Main content" }
                }
            }
        }
    }

    @Test
    fun mobile_light() = mobile(darkTheme = false)

    @Test
    fun mobile_dark() = mobile(darkTheme = true)

    private fun officialWidth(darkTheme: Boolean) {
        snapshot("sidebar_official_width", darkTheme = darkTheme) {
            SidebarProvider(open = true, onOpenChange = {}, modifier = Modifier.height(200.dp)) {
                Sidebar(width = SidebarDefaults.Width) {
                    ApplicationMenu()
                }
                SidebarInset {
                    SidebarTrigger()
                    Text { "Main content" }
                }
            }
        }
    }

    @Test
    fun official_width_light() = officialWidth(darkTheme = false)

    @Test
    fun official_width_dark() = officialWidth(darkTheme = true)
}
