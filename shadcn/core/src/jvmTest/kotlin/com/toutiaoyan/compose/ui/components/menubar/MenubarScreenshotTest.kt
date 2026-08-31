@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.menubar

import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test

/** The idle (no menu open) state -- open-panel appearance reuses DropdownMenu's already-proven rendering. */
class MenubarScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("menubar_idle", darkTheme = darkTheme) {
            Menubar {
                MenubarMenu(value = "file") {
                    MenubarTrigger("File")
                    MenubarContent { MenubarItem("New Tab", onClick = {}) }
                }
                MenubarMenu(value = "edit") {
                    MenubarTrigger("Edit")
                    MenubarContent { MenubarItem("Undo", onClick = {}) }
                }
                MenubarMenu(value = "view") {
                    MenubarTrigger("View")
                    MenubarContent { MenubarItem("Zoom In", onClick = {}) }
                }
            }
        }
    }

    @Test
    fun idle_light() = states(darkTheme = false)

    @Test
    fun idle_dark() = states(darkTheme = true)
}
