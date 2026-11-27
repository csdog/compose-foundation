package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.currentCalendarDate
import com.toutiaoyan.compose.ui.catalog.docs.datePickerDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DatePickerExamplesTest {
    @get:Rule val rule = createComposeRule()

    private fun show(title: String) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.width(720.dp).height(760.dp), contentAlignment = Alignment.TopCenter) {
                        datePickerDoc.examples.single { it.title == title }.RenderPreview()
                    }
                }
            }
        }
    }

    @Test fun officialRecipesAppearInTheOfficialOrderWithOfficialCopy() {
        assertEquals(
            listOf(
                "Default",
                "Basic",
                "Range Picker",
                "Date of Birth",
                "Input",
                "Time Picker",
                "Natural Language Picker",
                "RTL",
            ),
            datePickerDoc.examples.take(8).map { it.title },
        )
        assertEquals(
            "A basic date picker component.",
            datePickerDoc.examples.single { it.title == "Basic" }.descriptionMarkdown,
        )
        assertEquals(
            "A date picker component for selecting a range of dates.",
            datePickerDoc.examples.single { it.title == "Range Picker" }.descriptionMarkdown,
        )
        assertTrue(
            datePickerDoc.examples.single { it.title == "Date of Birth" }
                .descriptionMarkdown.orEmpty()
                .contains("dropdown caption layout"),
        )
        assertEquals(
            "A date picker component with an input field for selecting a date.",
            datePickerDoc.examples.single { it.title == "Input" }.descriptionMarkdown,
        )
        assertEquals(
            "A date picker component with a time input field for selecting a time.",
            datePickerDoc.examples.single { it.title == "Time Picker" }.descriptionMarkdown,
        )
        assertTrue(
            datePickerDoc.examples.single { it.title == "Natural Language Picker" }
                .descriptionMarkdown.orEmpty()
                .contains("chrono-node"),
        )
        assertTrue(datePickerDoc.examples.drop(8).all { it.title.startsWith("Kotlin extension: ") })
        assertNotNull(datePickerDoc.compositionCode)
    }

    @Test fun demoPicksADateAndShowsItOnTheTrigger() {
        show("Default")
        rule.onNodeWithText("Pick a date").performClick()
        val today = currentCalendarDate()
        rule.onNodeWithContentDescription("${today.year}-${today.month}-15")
            .performClick()
            .assert(
                SemanticsMatcher.expectValue(SemanticsProperties.Selected, true),
            )
    }

    @Test fun rangeRecipeStartsFromTheOfficialDefaultRange() {
        show("Range Picker")
        val today = currentCalendarDate()
        rule.onNodeWithText("Jan 20, ${today.year} - Feb 9, ${today.year}").assertIsDisplayed()
        rule.onNodeWithText("Jan 20, ${today.year} - Feb 9, ${today.year}").performClick()
        rule.onNodeWithContentDescription("Previous month").assertExists()
    }

    @Test fun dateOfBirthUsesDropdownCaptionLayout() {
        show("Date of Birth")
        rule.onNodeWithText("Select date").performClick()
        val today = currentCalendarDate()
        rule.onNodeWithContentDescription("Previous month").assertExists()
        rule.onNodeWithText(SHORT_MONTH_NAMES[today.month - 1]).assertIsDisplayed()
        rule.onNodeWithText(today.year.toString()).assertIsDisplayed()
    }

    @Test fun inputRecipeKeepsTheOfficialFormattedValue() {
        show("Input")
        rule.onNodeWithText("Subscription Date").assertIsDisplayed()
        rule.onNodeWithText("June 01, 2025").assertIsDisplayed()
        rule.onNodeWithContentDescription("Select date").assertIsDisplayed()
    }

    @Test fun timePickerShowsDateAndTimeFields() {
        show("Time Picker")
        rule.onNodeWithText("Date").assertIsDisplayed()
        rule.onNodeWithText("Time").assertIsDisplayed()
        rule.onNodeWithText("10:30:00").assertIsDisplayed()
    }

    @Test fun naturalLanguageRecipeParsesRelativePhrases() {
        show("Natural Language Picker")
        rule.onNodeWithText("Schedule Date").assertIsDisplayed()
        rule.onNodeWithText("In 2 days").assertIsDisplayed()
    }

    @Test fun rtlRecipeUsesArabicPlaceholder() {
        show("RTL")
        rule.onNodeWithText("اختر تاريخًا").assertIsDisplayed().assertWidthIsEqualTo(212.dp)
        rule.onNodeWithText("التاريخ").assertDoesNotExist()
    }

    @Test fun inputEditsSelectTheTypedDateAndArrowDownOpensTheCalendar() {
        show("Input")
        rule.onNodeWithText("June 01, 2025").performTextReplacement("July 04, 2025")
        rule.onNodeWithText("July 04, 2025").performKeyInput { pressKey(Key.DirectionDown) }
        rule.onNodeWithContentDescription("2025-7-4").assert(
            SemanticsMatcher.expectValue(SemanticsProperties.Selected, true),
        )
    }

    @Test fun invalidInputPreservesTheLastValidSelection() {
        show("Input")
        rule.onNodeWithText("June 01, 2025").performTextReplacement("February 30, 2025")
        rule.onNodeWithContentDescription("Select date").performClick()
        rule.onNodeWithContentDescription("2025-6-1").assert(
            SemanticsMatcher.expectValue(SemanticsProperties.Selected, true),
        )
    }

    @Test fun naturalLanguageSummaryTracksEditsAndIgnoresOversizedNumbers() {
        show("Natural Language Picker")
        rule.onNodeWithText("In 2 days").performTextReplacement("tomorrow")
        val today = currentCalendarDate()
        val tomorrow = java.time.LocalDate.of(today.year, today.month, today.day).plusDays(1)
        val summary =
            "Your post will be published on ${MONTH_NAMES[tomorrow.monthValue - 1]} " +
                "${tomorrow.dayOfMonth.toString().padStart(2, '0')}, ${tomorrow.year}."
        rule.onNodeWithText(summary).assertIsDisplayed()
        rule.onNodeWithText("tomorrow").performTextReplacement("in 999999999999999999999 days")
        rule.onNodeWithText("in 999999999999999999999 days").assertIsDisplayed()
        rule.onNodeWithText(summary).assertIsDisplayed()
    }

    @Test fun naturalLanguageArrowDownOpensDropdownCalendar() {
        show("Natural Language Picker")
        rule.onNodeWithText("In 2 days").performClick()
            .performKeyInput { pressKey(Key.DirectionDown) }
        val today = currentCalendarDate()
        val date = java.time.LocalDate.of(today.year, today.month, today.day).plusDays(2)
        rule.onNodeWithText(SHORT_MONTH_NAMES[date.monthValue - 1]).assertIsDisplayed()
    }

    @Test fun rtlSelectionFormatsTheTriggerInArabic() {
        show("RTL")
        rule.onNodeWithText("اختر تاريخًا").performClick()
        val today = currentCalendarDate()
        rule.onNodeWithContentDescription("${today.year}-${today.month}-15").performClick()
        rule.onNodeWithText("15 ${ARABIC_MONTH_NAMES[today.month - 1]} ${today.year}").assertExists()
    }

    @Test fun exportedRecipesUseValidCalendarMonths() {
        for (title in listOf("Basic", "RTL")) {
            assertTrue(!datePickerDoc.examples.single { it.title == title }.code.contains("mutableStateOf(0)"))
        }
    }

    @Test fun basicRecipeUsesTheOfficialFieldWidth() {
        show("Basic")
        rule.onNodeWithText("Pick a date").assertWidthIsEqualTo(176.dp)
    }

    @Test fun minMaxRecipeEnforcesExactBoundaryDays() {
        show("Kotlin extension: Min and max date")
        rule.onNodeWithText("Pick a date").performClick()
        rule.onNodeWithContentDescription("2026-3-4").assertIsNotEnabled()
        rule.onNodeWithContentDescription("2026-3-5").assertIsEnabled()
        repeat(3) { rule.onNodeWithContentDescription("Next month").performClick() }
        rule.onNodeWithContentDescription("2026-6-20").assertIsEnabled()
        rule.onNodeWithContentDescription("2026-6-21").assertIsNotEnabled()
    }

    private companion object {
        val ARABIC_MONTH_NAMES =
            listOf(
                "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
                "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر",
            )
        val MONTH_NAMES =
            listOf(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December",
            )
        val SHORT_MONTH_NAMES = MONTH_NAMES.map { it.take(3) }
    }
}
