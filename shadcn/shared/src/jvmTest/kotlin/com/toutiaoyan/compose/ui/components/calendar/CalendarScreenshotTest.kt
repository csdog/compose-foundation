@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.calendar

import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.ui.unit.dp
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

    private fun nova(darkTheme: Boolean) {
        snapshot("calendar_nova", darkTheme = darkTheme, stylePreset = StylePreset.Nova) {
            Calendar(
                2026,
                10,
                { _, _ -> },
                CalendarDate(2026, 10, 30),
                {},
                today = CalendarDate(2026, 10, 30),
                captionLayout = CalendarCaptionLayout.Dropdown,
            )
        }
    }

    private fun novaRange(darkTheme: Boolean) {
        snapshot("calendar_nova_range", darkTheme = darkTheme, stylePreset = StylePreset.Nova) {
            Calendar(
                2026,
                1,
                { _, _ -> },
                CalendarDateRange(CalendarDate(2026, 1, 12), CalendarDate(2026, 2, 11)),
                {},
                numberOfMonths = 2,
            )
        }
    }

    private fun novaWeekNumbers(darkTheme: Boolean) {
        snapshot("calendar_nova_week_numbers", darkTheme = darkTheme, stylePreset = StylePreset.Nova) {
            Calendar(2026, 1, { _, _ -> }, CalendarDate(2026, 1, 12), {}, showWeekNumber = true, cellSize = 40.dp)
        }
    }

    @Test fun nova_light() = nova(false)

    @Test fun nova_dark() = nova(true)

    @Test fun nova_range_light() = novaRange(false)

    @Test fun nova_range_dark() = novaRange(true)

    @Test fun nova_week_numbers_light() = novaWeekNumbers(false)

    @Test fun nova_week_numbers_dark() = novaWeekNumbers(true)
}
