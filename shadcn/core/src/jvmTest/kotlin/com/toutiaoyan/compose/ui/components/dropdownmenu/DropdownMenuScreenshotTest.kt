@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.dropdownmenu

import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

    private fun nova(dark: Boolean) {
        snapshot("dropdown_menu_nova", darkTheme = dark, stylePreset = StylePreset.Nova) {
            Box(Modifier.size(400.dp), contentAlignment = Alignment.TopCenter) {
                DropdownMenu(open = true, onOpenChange = {}) {
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
            }
        }
    }

    @Test fun nova_light() = nova(false)

    @Test fun nova_dark() = nova(true)
}
