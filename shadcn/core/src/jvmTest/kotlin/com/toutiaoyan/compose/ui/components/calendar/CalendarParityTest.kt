@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.theme.Theme
import kotlin.test.*

class CalendarParityTest : ScreenshotTest() {
    @Test fun multiMonthDropdownUsesTheDisplayedMonthOffsetAcrossTheYearBoundary() {
        var year by mutableStateOf(2025)
        var month by mutableStateOf(12)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Calendar(
                year,
                month,
                { y, m ->
                    year = y
                    month = m
                },
                selected = null,
                onSelectedChange = {},
                today = CalendarDate(2026, 1, 1),
                numberOfMonths = 2,
                reverseMonths = true,
                captionLayout = CalendarCaptionLayout.Dropdown,
            )
        }
        composeRule.onNodeWithText("Dec").assertExists()
        composeRule.onNodeWithText("Jan").performClick()
        composeRule.onNodeWithText("Feb").performClick()
        composeRule.runOnIdle {
            assertEquals(2026, year)
            assertEquals(1, month)
        }
        composeRule.onNodeWithText("Jan").assertExists()
        composeRule.onNodeWithText("Feb").assertExists()
    }

    @Test fun novaHasVariableWeeksAndCorrectHeaderTableGeometry() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Calendar(
                2026,
                10,
                { _, _ -> },
                selected = null,
                onSelectedChange = {},
                modifier = Modifier.testTag("calendar"),
            )
        }
        composeRule.onNodeWithContentDescription("2026-11-7").assertDoesNotExist()
        composeRule.onNodeWithTag("calendar").assertWidthIsEqualTo(212.dp)
        composeRule.onNodeWithTag("calendar").assertHeightIsEqualTo(262.dp)
        val results = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithText("Su").performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
        assertEquals(12.8f, results.single().layoutInput.style.fontSize.value)
    }

    @Test fun configuredCellSizeAppliesToHeaderDaysAndWeekGutter() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Calendar(2026, 1, { _, _ -> }, selected = null, onSelectedChange = {
            }, modifier = Modifier.testTag("calendar"), cellSize = 40.dp, showWeekNumber = true)
        }
        composeRule.onNodeWithTag("calendar").assertWidthIsEqualTo(336.dp)
        composeRule.onNodeWithContentDescription("2026-1-15").assertWidthIsEqualTo(40.dp).assertHeightIsEqualTo(40.dp)
        composeRule.onNodeWithContentDescription("Next month").assertWidthIsEqualTo(40.dp)
    }

    @Test fun navigationButtonsKeepEnterInsteadOfSelectingTheOldDay() {
        var selected: CalendarDate? = null
        var month by mutableStateOf(10)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Calendar(2026, month, { _, m -> month = m }, CalendarDate(2026, 10, 15), { selected = it })
        }
        composeRule.onNodeWithContentDescription("Next month").requestFocus().performKeyInput { pressKey(Key.Enter) }
        composeRule.onNodeWithText("November 2026").assertIsDisplayed()
        composeRule.runOnIdle { assertNull(selected) }
        composeRule.onNodeWithContentDescription("2026-11-1").requestFocus().assertIsFocused()
    }

    @Test fun cursorSkipsDisabledDatesAndMaintainsRealFocusAcrossMonths() {
        var year by mutableStateOf(2026)
        var month by mutableStateOf(1)
        var date by mutableStateOf<CalendarDate?>(CalendarDate(2026, 1, 30))
        setThemedContent(stylePreset = StylePreset.Nova) {
            Calendar(
                year,
                month,
                { y, m ->
                    year = y
                    month = m
                },
                date,
                { date = it },
                disabled = { it.dayOfWeek() in listOf(0, 6) },
            )
        }
        composeRule.onNodeWithContentDescription(
            "2026-1-30",
        ).requestFocus().performKeyInput { pressKey(Key.DirectionRight) }
        composeRule.onNodeWithContentDescription("2026-2-2").assertIsFocused().performKeyInput { pressKey(Key.Enter) }
        composeRule.runOnIdle { assertEquals(CalendarDate(2026, 2, 2), date) }
    }

    @Test fun customDayContentRetainsTheNativeDisabledActionAndSelection() {
        var selected by mutableStateOf<CalendarDate?>(null)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Calendar(
                2026,
                12,
                { _, _ -> },
                selected,
                { selected = it },
                disabled = { it.day == 12 },
                cellSize = 48.dp,
            ) {
                CalendarGrid(dayStyle = { Style }, dayContent = { cell -> Text { "Price ${cell.date.day}" } })
            }
        }
        composeRule.onNodeWithContentDescription("2026-12-12").assertIsNotEnabled()
        composeRule.onNodeWithContentDescription("2026-12-15").performClick().assertIsSelected()
        composeRule.onNodeWithText("Price 15", useUnmergedTree = true).assertExists()
        composeRule.runOnIdle { assertEquals(CalendarDate(2026, 12, 15), selected) }
    }

    @Test fun weekNumbersFollowTheLocaleYearBoundary() {
        assertEquals(1, calendarWeekNumber(CalendarDate(2025, 12, 28), 0))
        assertEquals(2, calendarWeekNumber(CalendarDate(2026, 1, 4), 0))
        assertEquals(1, calendarWeekNumber(CalendarDate(2024, 12, 30), 1))
        assertEquals(2, calendarWeekNumber(CalendarDate(2025, 1, 6), 1))
    }

    @Test fun narrowContainersDoNotWrapDayNumbersOntoTwoLines() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.width(180.dp)) {
                Calendar(2026, 10, { _, _ -> }, selected = null, onSelectedChange = {}, cellSize = 38.dp)
            }
        }
        val layouts = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithText("17").performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(1, layouts.single().lineCount)
    }

    @Test fun dropdownUsesTheOfficialHundredYearWindowAndDoesNotNavigateBeyondIt() {
        var year by mutableStateOf(2026)
        var month by mutableStateOf(12)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Calendar(
                year,
                month,
                { y, m ->
                    year = y
                    month = m
                },
                CalendarDate(2026, 12, 31),
                {},
                today = CalendarDate(2026, 10, 30),
                captionLayout = CalendarCaptionLayout.Dropdown,
            )
        }
        composeRule.onNodeWithContentDescription("Next month").assertIsNotEnabled()
        composeRule.onNodeWithContentDescription(
            "2026-12-31",
        ).requestFocus().performKeyInput { pressKey(Key.DirectionRight) }
        composeRule.onNodeWithContentDescription("2026-12-31").assertIsFocused()
        composeRule.runOnIdle {
            assertEquals(12, month)
            assertEquals(2026, year)
        }
        composeRule.onNodeWithText("2026").performClick()
        composeRule.onNodeWithText("1926").assertExists()
        composeRule.onNodeWithText("2027").assertDoesNotExist()
    }

    @Test fun dropdownBoundsStayAnchoredWhenTodayIsNotProvided() {
        var year by mutableStateOf(2026)
        lateinit var state: CalendarState
        setThemedContent(stylePreset = StylePreset.Nova) {
            Calendar(
                year,
                10,
                { y, _ -> year = y },
                selected = null,
                onSelectedChange = {},
                captionLayout = CalendarCaptionLayout.Dropdown,
            ) {
                val current = calendarState()
                SideEffect { state = current }
                CalendarDefaultContent()
            }
        }
        composeRule.runOnIdle { year = 2016 }
        composeRule.runOnIdle {
            assertEquals(1926, state.fromYear)
            assertEquals(2026, state.toYear)
        }
    }

    @Test fun persianBoundaryNavigationAndKeyboardStayInRange() {
        var year by mutableStateOf(1)
        var month by mutableStateOf(1)
        var state: CalendarState? = null
        setThemedContent {
            CompositionLocalProvider(LocalCalendarLocale provides CalendarLocale.Persian) {
                Calendar(year, month, { y, m ->
                    year = y
                    month = m
                }, selected = null, onSelectedChange = {}, showWeekNumber = true) {
                    state = calendarState()
                    CalendarDefaultContent()
                }
            }
        }
        composeRule.runOnIdle {
            state!!.goPrevious()
            state!!.moveFocusByDays(-1)
            state!!.moveFocusByMonths(-1)
            assertEquals(1, year)
            assertEquals(1, month)
            year = 3176
            month = 12
        }
        composeRule.waitForIdle()
        composeRule.runOnIdle {
            state!!.goNext()
            state!!.moveFocusByMonths(1)
            assertEquals(3176, year)
            assertEquals(12, month)
        }
    }

    @Test fun persianMultiMonthFocusClampsWindowAndRetainsTarget() {
        var year by mutableStateOf(3176)
        var month by mutableStateOf(10)
        var state: CalendarState? = null
        setThemedContent {
            CompositionLocalProvider(LocalCalendarLocale provides CalendarLocale.Persian) {
                Calendar(year, month, { y, m ->
                    year = y
                    month = m
                }, selected = null, onSelectedChange = {}, numberOfMonths = 2) {
                    state = calendarState()
                    CalendarDefaultContent()
                }
            }
        }
        composeRule.runOnIdle { state!!.moveFocusTo(CalendarDate(3176, 11, 30)) }
        composeRule.runOnIdle { state!!.moveFocusByDays(1) }
        composeRule.runOnIdle {
            assertEquals(3176, year)
            assertEquals(11, month)
            assertEquals(CalendarDate(3176, 12, 1), state!!.focusedDate)
        }
    }

    @Test fun callerShapeUpdatesLiveAndThemeKeepsSelection() {
        var radius by mutableStateOf(0.dp)
        var dark by mutableStateOf(false)
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                Box(Modifier.background(Color.Red)) {
                    Calendar(
                        2026,
                        11,
                        { _, _ -> },
                        CalendarDate(2026, 11, 15),
                        {},
                        modifier = Modifier.testTag("live-calendar"),
                        style =
                            Style {
                                background(Color.Green)
                                shape(RoundedCornerShape(radius))
                            },
                    )
                }
            }
        }

        fun cornerGreen(): Float {
            val image = composeRule.onNodeWithTag("live-calendar").captureToImage().toPixelMap()
            val point = with(composeRule.density) { 2.dp.roundToPx() }
            return image[point, point].green
        }
        assertTrue(cornerGreen() > .9f, "Square caller shape must paint the corner")
        composeRule.runOnIdle { radius = 20.dp }
        assertTrue(cornerGreen() < .4f, "Live rounded caller shape must remove the same corner pixel")
        composeRule.runOnIdle { dark = true }
        assertTrue(cornerGreen() < .4f, "Theme change must retain the caller shape")
        composeRule.onNodeWithContentDescription("2026-11-15").assertIsSelected()
    }

    @Test fun novaSelectedDayKeepsPrimaryFillWhileHoveredInLightTheme() {
        var expected = Color.Unspecified
        setThemedContent(stylePreset = StylePreset.Nova) {
            expected = com.toutiaoyan.compose.ui.theme.currentTheme.colors.primary
            Calendar(2026, 11, { _, _ -> }, CalendarDate(2026, 11, 15), {})
        }
        val day = composeRule.onNodeWithContentDescription("2026-11-15")
        day.performMouseInput { moveTo(center) }
        composeRule.waitForIdle()
        day.assertIsSelected()
        val pixels = day.captureToImage().toPixelMap()
        val fill = pixels[with(composeRule.density) { 5.dp.roundToPx() }, pixels.height / 2]
        assertEquals(expected.red, fill.red, .005f)
        assertEquals(expected.green, fill.green, .005f)
        assertEquals(expected.blue, fill.blue, .005f)
    }
}
