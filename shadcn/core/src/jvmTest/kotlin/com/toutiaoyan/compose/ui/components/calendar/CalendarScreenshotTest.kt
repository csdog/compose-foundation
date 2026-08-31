@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.calendar

import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test

class CalendarScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("calendar_states", darkTheme = darkTheme) {
            Calendar(
                year = 2026,
                month = 3,
                onMonthChange = { _, _ -> },
                selected = CalendarDate(2026, 3, 10),
                onSelectedChange = {},
                today = CalendarDate(2026, 3, 15),
            )
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)

    private fun captionDropdown(darkTheme: Boolean) {
        snapshot("calendar_caption_dropdown", darkTheme = darkTheme) {
            Calendar(
                year = 2026,
                month = 3,
                onMonthChange = { _, _ -> },
                selected = CalendarDate(2026, 3, 10),
                onSelectedChange = {},
                today = CalendarDate(2026, 3, 15),
                captionLayout = CalendarCaptionLayout.Dropdown,
            )
        }
    }

    @Test
    fun caption_dropdown_light() = captionDropdown(darkTheme = false)

    @Test
    fun caption_dropdown_dark() = captionDropdown(darkTheme = true)
}
