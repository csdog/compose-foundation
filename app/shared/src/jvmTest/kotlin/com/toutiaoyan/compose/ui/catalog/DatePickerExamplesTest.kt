package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
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
                        datePickerDoc.examples.single { it.title == title }.preview()
                    }
                }
            }
        }
    }

    @Test fun officialRecipesAppearInTheOfficialOrderWithOfficialCopy() {
        assertEquals(
            listOf(
                "Default", "Basic", "Range Picker", "Date of Birth", "Input",
                "Time Picker", "Natural Language Picker", "RTL",
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
        rule.onNodeWithText("اختر تاريخًا").assertIsDisplayed()
        rule.onNodeWithText("التاريخ").assertIsDisplayed()
    }

    private companion object {
        val MONTH_NAMES =
            listOf(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December",
            )
        val SHORT_MONTH_NAMES = MONTH_NAMES.map { it.take(3) }
    }
}
