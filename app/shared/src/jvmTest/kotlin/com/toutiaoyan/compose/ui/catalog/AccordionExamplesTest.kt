package com.toutiaoyan.compose.ui.catalog

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import com.toutiaoyan.compose.ui.catalog.docs.accordionDoc
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.Test

class AccordionExamplesTest {
    @get:Rule val rule = createComposeRule()

    @Test fun basicRendersActualQuestionsAndSwitchesAnswer() {
        val example = accordionDoc.examples.single { it.title == "Basic (official)" }
        rule.setContent { Theme(preset = StylePreset.Nova, minTouchTarget = null) { example.preview() } }
        rule.onNodeWithText("How do I reset my password?").assertIsDisplayed()
        rule.onNodeWithText("Can I change my subscription plan?").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Yes, you can upgrade", substring = true).assertIsDisplayed()
        rule.onNodeWithText("{item.trigger}").assertDoesNotExist()
    }

    @Test fun multipleKeepsBothRealAnswersOpen() {
        val example = accordionDoc.examples.single { it.title == "Multiple (official)" }
        rule.setContent { Theme(preset = StylePreset.Nova, minTouchTarget = null) { example.preview() } }
        rule.onNodeWithText("Manage how you receive", substring = true).assertIsDisplayed()
        rule.onNodeWithText("Privacy & Security").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Manage how you receive", substring = true).assertIsDisplayed()
        rule.onNodeWithText("Control your privacy", substring = true).assertIsDisplayed()
        rule.onNodeWithText("{item.content}").assertDoesNotExist()
    }
}
