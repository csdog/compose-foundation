@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.calendar

import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.popover.Popover
import com.toutiaoyan.compose.ui.components.popover.PopoverContent
import com.toutiaoyan.compose.ui.components.popover.PopoverTrigger
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import kotlin.test.Test

/**
 * Not a standalone registry component in real shadcn/ui either -- the catalog's own
 * `DatePickerDoc` documents it as a Popover+Calendar composition, so this captures that same
 * recipe's rendered result rather than a dedicated `DatePicker` composable (there isn't
 * one). No calendar icon on the button here (unlike the catalog doc's version) -- `:shadcn:core`
 * takes zero icon-library dependencies, and Lucide is `app:shared`-only.
 */
class DatePickerScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("date_picker_states", darkTheme = darkTheme) {
            Popover(open = true, onOpenChange = {}) {
                PopoverTrigger {
                    Button(onClick = toggle, variant = ButtonVariant.Outline) {
                        Text(textStyle = TextColors.Muted) { "Pick a date" }
                    }
                }
                PopoverContent {
                    Calendar(
                        year = 2026,
                        month = 1,
                        onMonthChange = { _, _ -> },
                        selected = CalendarDate(2026, 1, 15),
                        onSelectedChange = {},
                    )
                }
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)
}
