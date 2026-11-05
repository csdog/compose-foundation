package com.toutiaoyan.compose.ui.catalog

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.catalog.docs.switchDoc
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SwitchExamplesTest {
    @get:Rule val rule = createComposeRule()

    @Test fun officialRecipeOrderAndDescriptionMatchCurrentPage() {
        assertEquals("A control that allows the user to toggle between checked and not checked.", switchDoc.description)
        assertEquals(null, switchDoc.examplesHeading)
        assertEquals(
            listOf("Default", "Description", "Choice Card", "Disabled", "Invalid", "Size", "RTL"),
            switchDoc.examples.map { it.title },
        )
        assertFalse(switchDoc.examples.first().showHeading)
    }

    @Test fun defaultRecipeTogglesFromTheLabel() {
        val example = switchDoc.examples.single { it.title == "Default" }
        rule.setContent { Theme(preset = StylePreset.Nova, minTouchTarget = null) { example.preview() } }
        rule.onAllNodes(isToggleable()).assertCountEquals(1)
        rule.onNode(isToggleable()).assertIsOff()
        rule.onNodeWithText("Airplane Mode").performClick()
        rule.waitForIdle()
        rule.onNode(isToggleable()).assertIsOn()
    }

    @Test fun descriptionRecipeRendersTheOfficialCopy() {
        val example = switchDoc.examples.single { it.title == "Description" }
        rule.setContent { Theme(preset = StylePreset.Nova, minTouchTarget = null) { example.preview() } }
        rule.onNodeWithText("Share across devices").assertIsDisplayed()
        rule.onNodeWithText("Focus is shared across devices", substring = true).assertIsDisplayed()
        rule.onAllNodes(isToggleable()).assertCountEquals(1)
    }

    @Test fun choiceCardRowTogglesItsSwitch() {
        val example = switchDoc.examples.single { it.title == "Choice Card" }
        rule.setContent { Theme(preset = StylePreset.Nova, minTouchTarget = null) { example.preview() } }
        rule.onAllNodes(isToggleable()).assertCountEquals(2)
        rule.onNodeWithText("Enable notifications").performClick()
        rule.waitForIdle()
        rule.onAllNodes(isToggleable())[1].assertIsOff()
    }

    @Test fun disabledRecipeCannotBeToggled() {
        val example = switchDoc.examples.single { it.title == "Disabled" }
        rule.setContent { Theme(preset = StylePreset.Nova, minTouchTarget = null) { example.preview() } }
        rule.onNode(isToggleable()).assertIsNotEnabled()
        rule.onNodeWithText("Disabled").assertIsDisplayed()
    }

    @Test fun invalidRecipeShowsTheTermsCopy() {
        val example = switchDoc.examples.single { it.title == "Invalid" }
        rule.setContent { Theme(preset = StylePreset.Nova, minTouchTarget = null) { example.preview() } }
        rule.onNodeWithText("Accept terms and conditions").assertIsDisplayed()
        rule.onNodeWithText("You must accept the terms and conditions to continue.").assertIsDisplayed()
    }

    @Test fun rtlRecipeStartsWithArabicCopy() {
        val example = switchDoc.examples.single { it.title == "RTL" }
        rule.setContent { Theme(preset = StylePreset.Nova, minTouchTarget = null) { example.preview() } }
        rule.onNodeWithText("المشاركة عبر الأجهزة").assertExists()
    }

    @Test fun copyPageMarkdownKeepsOfficialOrder() {
        val markdown = componentPageMarkdown(switchDoc)
        val headings = listOf("## Description", "## Choice Card", "## Disabled", "## Invalid", "## Size", "## RTL")
        val positions = headings.map { heading ->
            markdown.indexOf(heading).also { assertTrue(it >= 0, "missing $heading") }
        }
        assertEquals(positions.sorted(), positions)
        assertFalse(markdown.contains("## Examples"))
    }
}
