@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.tabs

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test

class TabsScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("tabs_states", darkTheme = darkTheme) {
            Tabs(selected = "account", onSelectedChange = {}) {
                TabsList {
                    TabsTrigger(value = "account", label = "Account")
                    TabsTrigger(value = "password", label = "Password")
                }
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)

    private fun line(darkTheme: Boolean) {
        snapshot("tabs_line", darkTheme = darkTheme) {
            Tabs(selected = "overview", onSelectedChange = {}) {
                TabsList(variant = TabsListVariant.Line) {
                    TabsTrigger(value = "overview", label = "Overview")
                    TabsTrigger(value = "analytics", label = "Analytics")
                    TabsTrigger(value = "reports", label = "Reports")
                }
            }
        }
    }

    @Test
    fun line_light() = line(darkTheme = false)

    @Test
    fun line_dark() = line(darkTheme = true)

    private fun itemFocused(darkTheme: Boolean) {
        snapshotFocused("tabs_item_focused", focusTag = "tabs", darkTheme = darkTheme) {
            Tabs(selected = "account", onSelectedChange = {}) {
                TabsList(modifier = Modifier.testTag("tabs")) {
                    TabsTrigger(value = "account", label = "Account")
                }
            }
        }
    }

    @Test
    fun item_focused_light() = itemFocused(darkTheme = false)
}
