@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.navigationmenu

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import kotlin.test.Test

/** The idle (no panel open) state -- open-panel appearance reuses AnchoredPopup, already proven by Popover. */
class NavigationMenuScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("navigation_menu_idle", darkTheme = darkTheme) {
            NavigationMenu {
                NavigationMenuList {
                    NavigationMenuItem(value = "home") {
                        NavigationMenuLink("Home", onClick = {})
                    }
                    NavigationMenuItem(value = "getting-started") {
                        NavigationMenuTrigger("Getting started")
                        NavigationMenuContent {
                            Text { "Re-usable components built with Radix UI." }
                        }
                        NavigationMenuIndicator()
                    }
                    NavigationMenuItem(value = "docs") {
                        NavigationMenuLink("Docs", onClick = {})
                    }
                }
            }
        }
    }

    @Test
    fun idle_light() = states(darkTheme = false)

    @Test
    fun idle_dark() = states(darkTheme = true)

    private fun itemFocused(darkTheme: Boolean) {
        snapshotFocused("navigation_menu_item_focused", focusTag = "navmenu", darkTheme = darkTheme) {
            NavigationMenu(modifier = Modifier.testTag("navmenu")) {
                NavigationMenuList {
                    NavigationMenuItem(value = "home") {
                        NavigationMenuLink("Home", onClick = {})
                    }
                }
            }
        }
    }

    @Test
    fun item_focused_light() = itemFocused(darkTheme = false)
}
