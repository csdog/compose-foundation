@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.command

import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test

class CommandScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("command_states", darkTheme = darkTheme) {
            Command {
                CommandInput()
                CommandList {
                    CommandGroup {
                        CommandItem("Calendar", onSelect = {})
                        CommandItem("Search Emoji", onSelect = {})
                        CommandItem("Calculator", onSelect = {})
                    }
                }
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)

    private fun closeButton(darkTheme: Boolean) {
        snapshot("command_dialog_close_button", darkTheme = darkTheme) {
            CommandDialog(
                open = true,
                onOpenChange = {},
                showCloseButton = CommandDefaults.ShowCloseButton,
            ) {
                CommandInput()
                CommandList {
                    CommandGroup {
                        CommandItem("Calendar", onSelect = {})
                        CommandItem("Search Emoji", onSelect = {})
                        CommandItem("Calculator", onSelect = {})
                    }
                }
            }
        }
    }

    @Test
    fun dialog_close_button_light() = closeButton(darkTheme = false)

    @Test
    fun dialog_close_button_dark() = closeButton(darkTheme = true)
}
