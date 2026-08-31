@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.dropdownmenu

import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test

class DropdownMenuScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("dropdown_menu_states", darkTheme = darkTheme) {
            DropdownMenu(open = true, onOpenChange = {}) {
                DropdownMenuTrigger("Open")
                DropdownMenuContent {
                    DropdownMenuLabel("My Account")
                    DropdownMenuSeparator()
                    DropdownMenuItem("Edit", onClick = {})
                    DropdownMenuItem("Duplicate", onClick = {})
                    DropdownMenuSeparator()
                    DropdownMenuItem("Delete", onClick = {}, destructive = true)
                }
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)
}
