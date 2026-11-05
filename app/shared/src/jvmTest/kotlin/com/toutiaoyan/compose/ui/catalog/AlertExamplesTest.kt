package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toPixelMap
import kotlin.math.abs
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.TwColors
import com.toutiaoyan.compose.ui.catalog.docs.alertDoc
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AlertExamplesTest {
    @get:Rule val rule = createComposeRule()

    @Test fun officialRecipesComeFirstAndKotlinExtensionsAreSeparated() {
        assertEquals(
            listOf("Default", "Basic", "Destructive", "Action", "Custom Colors", "RTL"),
            alertDoc.examples.take(6).map { it.title },
        )
        assertEquals(null, alertDoc.examplesHeading)
        assertEquals(
            listOf(
                "Kotlin extension: Slots",
                "Kotlin extension: Title",
                "Kotlin extension: Description",
                "Kotlin extension: Multiple actions",
            ),
            alertDoc.examples.drop(6).map { it.title },
        )
        assertEquals(
            "A basic alert with an icon, title and description.",
            alertDoc.examples.single { it.title == "Basic" }.descriptionMarkdown,
        )
        assertEquals(
            "Use `AlertAction` to add a button or other action element to the alert.",
            alertDoc.examples.single { it.title == "Action" }.descriptionMarkdown,
        )
    }

    @Test fun colorsExampleUpdatesActualTitleColorWhenThemeChanges() {
        var dark by mutableStateOf(false)
        val example = alertDoc.examples.single { it.title == "Custom Colors" }
        rule.setContent { Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) { example.preview() } }

        fun assertColor(expected: androidx.compose.ui.graphics.Color) {
            val pixels = rule.onNodeWithText("Your subscription will expire in 3 days.").captureToImage().toPixelMap()
            var matching = 0
            for (y in 0 until pixels.height) for (x in 0 until pixels.width) {
                val actual = pixels[x, y]
                if (abs(actual.red - expected.red) < .025f && abs(actual.green - expected.green) < .025f &&
                    abs(actual.blue - expected.blue) < .025f && actual.alpha > .9f
                ) {
                    matching++
                }
            }
            assertTrue(matching >= 3, "Expected actual painted title color $expected, got $matching matching pixels")
        }
        assertColor(TwColors.amber900)
        rule.runOnIdle { dark = true }
        rule.waitForIdle()
        assertColor(TwColors.amber50)
        rule.runOnIdle { dark = false }
        rule.waitForIdle()
        assertColor(TwColors.amber900)
    }

    @Test fun actionTitlePaintsLightTextInDarkTheme() {
        val example = alertDoc.examples.single { it.title == "Action" }
        var expected = androidx.compose.ui.graphics.Color.Unspecified
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = true, minTouchTarget = null) {
                expected = com.toutiaoyan.compose.ui.theme.currentTheme.colors.cardForeground
                example.preview()
            }
        }
        repeat(12) {
            val pixels = rule.onNodeWithText("Dark mode is now available").captureToImage().toPixelMap()
            var matching = 0
            for (y in 0 until pixels.height) for (x in 0 until pixels.width) {
                val actual = pixels[x, y]
                if (abs(actual.red - expected.red) < .025f &&
                    abs(actual.green - expected.green) < .025f &&
                    abs(actual.blue - expected.blue) < .025f
                ) {
                    matching++
                }
            }
            assertTrue(matching >= 3, "Dark Action title must paint cardForeground $expected; found $matching pixels")
        }
    }

    @Test fun rtlExampleSelectsEnglishAndUpdatesRealContent() {
        val example = alertDoc.examples.single { it.title == "RTL" }
        rule.setContent { Theme(preset = StylePreset.Nova, minTouchTarget = null) { example.preview() } }
        rule.onNodeWithText("تم الدفع بنجاح").assertIsDisplayed()
        rule.onNodeWithText("Arabic (العربية)").performClick()
        rule.onNodeWithText("English").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Payment successful").assertIsDisplayed()
        rule.onNodeWithText("New feature available").assertIsDisplayed()
        rule.onNodeWithText("تم الدفع بنجاح").assertDoesNotExist()
    }

    @Test fun rtlToolbarIsSeparateAndTranslationPopoverClosesWithEscape() {
        val example = alertDoc.examples.single { it.title == "RTL" }
        rule.setContent { Theme(preset = StylePreset.Nova, minTouchTarget = null) { example.preview() } }
        rule.onNodeWithContentDescription("Preview language").assertWidthIsEqualTo(144.dp).assertHeightIsEqualTo(28.dp)
        rule.onNodeWithTag("rtl-preview-toolbar").assertHeightIsEqualTo(63.dp)
        val toolbar = rule.onNodeWithTag("rtl-preview-toolbar").fetchSemanticsNode().boundsInRoot
        val body = rule.onNodeWithTag("rtl-preview-body").fetchSemanticsNode().boundsInRoot
        assertTrue(body.top >= toolbar.bottom + 1f, "The toolbar must sit above the bordered preview body")
        rule.onNodeWithContentDescription("Preview language").requestFocus()
        rule.onNodeWithContentDescription("Translation information").performMouseInput { click() }
        val notice = (
            "I used AI to translate the text for demonstration purposes. " +
                "It's not perfect and may contain errors."
        )
        rule.onNodeWithText(notice).assertIsDisplayed().performKeyInput { pressKey(Key.Escape) }
        rule.waitForIdle()
        rule.onNodeWithText(notice).assertDoesNotExist()
        rule.onNodeWithContentDescription("Translation information").assertIsFocused()
    }

    @Test fun multipleActionsKeepBodyClearAndUpdateTheActualExample() {
        val example = alertDoc.examples.single { it.title == "Kotlin extension: Multiple actions" }
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Box(Modifier.width(292.dp)) { example.preview() }
            }
        }

        fun check(message: String) {
            val save = rule.onNodeWithText("Save").fetchSemanticsNode().boundsInRoot
            for (label in listOf("Review changes", message)) {
                val body = rule.onNodeWithText(label).fetchSemanticsNode().boundsInRoot
                assertTrue(body.right <= save.left - 7.5f, "Text must not overlap either action")
            }
        }
        check("Choose an action for this draft.")
        rule.onNodeWithText("Save").performClick()
        check("Save selected.")
        rule.onNodeWithText("Undo").performClick()
        check("Undo selected.")
    }
}
