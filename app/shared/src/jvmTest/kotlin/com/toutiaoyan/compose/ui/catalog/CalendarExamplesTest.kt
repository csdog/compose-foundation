package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.calendarDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CalendarExamplesTest {
    @get:Rule val rule = createComposeRule()

    private fun show(title: String) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(
                        Modifier.width(384.dp),
                    ) { calendarDoc.examples.single { it.title == title }.preview() }
                }
            }
        }
    }

    @Test fun bookedDatesCannotBeSelectedButUnbookedDaysRemainInteractive() {
        show("Booked dates")
        val year = java.time.LocalDate.now().year
        rule.onNodeWithContentDescription("$year-1-12").assertIsNotEnabled()
        rule.onNodeWithContentDescription("$year-1-6").assertIsSelected()
        rule.onNodeWithContentDescription("$year-1-8").performClick().assertIsSelected()
    }

    @Test fun presetsSelectTheCivilDateAndMoveTheVisibleMonth() {
        show("Presets")
        val target = java.time.LocalDate.now().plusDays(14)
        rule.onNodeWithText("In 2 weeks").performClick()
        rule.onNodeWithContentDescription("${target.year}-${target.monthValue}-${target.dayOfMonth}").assertIsSelected()
    }

    @Test fun timeFieldsKeepTheirIndependentEdits() {
        show("Date and Time Picker")
        rule.onNodeWithText("10:30:00").performTextReplacement("09:15:30")
        rule.onNodeWithText("09:15:30").assertIsDisplayed()
        rule.onNodeWithText("12:30:00").assertIsDisplayed()
    }

    @Test fun priceContentSelectsTheActualRangeAndRetainsThePrice() {
        show("Custom Cell Size")
        val year = java.time.LocalDate.now().year
        rule.onNodeWithContentDescription("$year-12-20").performClick().assertIsSelected()
        rule.onNodeWithContentDescription("$year-12-23").performClick().assertIsSelected()
        rule.onAllNodesWithText("$120", useUnmergedTree = true).assertCountEquals(8)
    }

    @Test fun catalogHasOfficialRecipesAndDocumentsTheCalendarSystemGap() {
        assertEquals(15, calendarDoc.examples.size)
        assertTrue(
            calendarDoc.examples.map { it.title }.containsAll(
                listOf(
                    "Basic",
                    "Range Calendar",
                    "Month and Year Selector",
                    "Presets",
                    "Date and Time Picker",
                    "Week Numbers",
                    "RTL",
                ),
            ),
        )
        assertTrue(calendarDoc.beforeExamples.any { it.title == "Persian / Hijri / Jalali Calendar" })
        assertTrue(calendarDoc.apiSections.any { it.title == "CalendarGrid" })
    }

    @Test fun exportedRecipesIncludeTheirStateAndCustomInteractions() {
        fun code(title: String) = calendarDoc.examples.single { it.title == title }.code
        assertTrue(code("Presets").contains("date = next; year = next.year; month = next.month"))
        assertTrue(code("Date and Time Picker").contains("var startTime by remember"))
        assertTrue(code("Date and Time Picker").contains("InputGroupInput(endTime"))
        assertTrue(code("Custom Cell Size").contains("CalendarGrid(dayContent"))
        assertTrue(code("Booked dates").contains("TextDecoration.LineThrough"))
        assertTrue(code("RTL").contains("LocalCalendarLocale provides calendarRtlLocale(language)"))
        assertTrue(code("RTL").contains("fun calendarRtlLocale"))
        assertTrue(calendarDoc.examples.none { it.code.contains("see the catalog recipe") })
    }
}
