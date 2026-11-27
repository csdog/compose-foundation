package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.buttonGroupDoc
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(Parameterized::class)
class ButtonGroupExamplesTest(
    private val width: Int,
    private val dark: Boolean,
) {
    @get:Rule val rule = createComposeRule()

    @Test fun currentOfficialRecipesRenderAtBothWidths() {
        val indexState = mutableIntStateOf(0)
        var index by indexState
        setContent(indexState)
        assertEquals(
            listOf(
                "Default",
                "Orientation",
                "Size",
                "Nested",
                "Separator",
                "Split",
                "Input",
                "Input Group",
                "Dropdown Menu",
                "Select",
                "Popover",
                "RTL",
            ),
            buttonGroupDoc.examples.map { it.title },
        )
        for (i in buttonGroupDoc.examples.indices) {
            rule.runOnIdle { index = i }
            rule.waitForIdle()
        }
    }

    @Test fun sizeRecipeUsesSmDefaultAndLgButtonHeights() {
        val indexState = mutableIntStateOf(2)
        setContent(indexState)
        rule.onNodeWithText("Small").assertHeightIsEqualTo(28.dp)
        rule.onNodeWithText("Default").assertHeightIsEqualTo(32.dp)
        rule.onNodeWithText("Large").assertHeightIsEqualTo(36.dp)
    }

    @Test fun dropdownAndPopoverRecipesOpenTheirRealContent() {
        val indexState = mutableIntStateOf(8)
        var index by indexState
        setContent(indexState)
        rule.onNodeWithText("Follow").assertIsDisplayed()
        rule.onNodeWithContentDescription("Open options").performClick()
        rule.onNodeWithText("Mute Conversation").assertIsDisplayed()
        rule.onNodeWithText("Copy Conversation").assertIsDisplayed()
        rule.onNodeWithText("Delete Conversation").assertIsDisplayed()
        rule.onNodeWithText("Mark as Read").performClick()
        rule.runOnIdle { index = 10 }
        rule.onNodeWithText("Copilot").performClick()
        rule.onNodeWithText("Start a new task with Copilot").assertDoesNotExist()
        rule.onNodeWithContentDescription("Open Popover").performClick()
        rule.onNodeWithText("Start a new task with Copilot").assertIsDisplayed()
        rule.onNodeWithText("Describe your task in natural language.").assertIsDisplayed()
        rule.onNodeWithText("I need to...").assertIsDisplayed()
        rule.onNodeWithText("Copilot will open a pull request for review.").assertIsDisplayed()
        rule.onNodeWithContentDescription("Task Description").assertIsDisplayed()
    }

    @Test fun groupExposesLabeledTraversalGroupSemantics() {
        setContent(mutableIntStateOf(0))
        val group =
            rule.onNodeWithContentDescription("Message actions", useUnmergedTree = true)
                .fetchSemanticsNode()
        assertTrue(group.config[SemanticsProperties.IsTraversalGroup])
    }

    @Test fun pageKeepsOfficialGuidanceAndRecipeOrder() {
        val titles = docSections(buttonGroupDoc).map { it.title }
        val order =
            listOf(
                "Installation",
                "Usage",
                "Composition",
                "Accessibility",
                "ButtonGroup vs ToggleGroup",
                "Orientation",
                "Size",
                "Nested",
                "Separator",
                "Split",
                "Input",
                "Input Group",
                "Dropdown Menu",
                "Select",
                "Popover",
                "RTL",
                "API Reference",
            ).map { titles.indexOf(it) }
        assertTrue(order.all { it >= 0 }, "missing section: $order")
        assertEquals(order.sorted(), order)

        val markdown = componentPageMarkdown(buttonGroupDoc)
        assertContains(markdown, "## Composition")
        assertContains(markdown, "## Accessibility")
        assertContains(markdown, "## ButtonGroup vs ToggleGroup")
        assertContains(markdown, "The web `role=\"group\"` has no direct Canvas role")
        assertContains(markdown, "### ButtonGroupSeparator")
        assertContains(markdown, "### ButtonGroupText")
        assertContains(markdown, "Use with a Popover component.")
    }

    @Test fun inputGroupVoiceModeDisablesTheMessageFieldAndUpdatesItsPrompt() {
        val indexState = mutableIntStateOf(7)
        setContent(indexState)
        rule.onNodeWithText("Send a message...").assertIsDisplayed()
        rule.onNodeWithContentDescription("Voice Mode").assertIsOff().performClick()
        rule.onNodeWithText("Record and send audio...").assertIsDisplayed().assertIsNotEnabled()
        rule.onNodeWithContentDescription("Voice Mode").assertIsOn().performClick()
        rule.onNodeWithText("Send a message...").assertIsDisplayed().assertIsEnabled()
        rule.onNodeWithContentDescription("Voice Mode").assertIsOff()
    }

    @Test fun rtlLanguageSelectorKeepsButtonGroupsLogical() {
        val indexState = mutableIntStateOf(11)
        setContent(indexState)
        rule.onNodeWithText("تقرير").assertIsDisplayed()
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("English").performClick()
        rule.onNodeWithText("Report").assertIsDisplayed()
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("Hebrew (עברית)").performClick()
        rule.onNodeWithText("דוח").assertIsDisplayed()
        rule.onNodeWithText("דחה").assertIsDisplayed()
        rule.onNodeWithContentDescription("More Options").performClick()
        rule.onNodeWithText("סמן כנקרא").assertIsDisplayed()
        rule.onNodeWithText("תייג כ...").performClick()
        rule.onNodeWithText("אישי").assertIsDisplayed()
        rule.onNodeWithText("עבודה").assertIsDisplayed()
        assertTrue(buttonGroupDoc.examples.last().code.contains("LocalLayoutDirection"))
    }

    @Test fun nestedRecipesKeepEightDpBetweenIndependentGroups() {
        val indexState = mutableIntStateOf(3)
        setContent(indexState)
        for (i in listOf(3, 7, 9)) {
            rule.runOnIdle { indexState.intValue = i }
            val first = rule.onNodeWithTag("button-group-leading").getUnclippedBoundsInRoot()
            val second = rule.onNodeWithTag("button-group-trailing").getUnclippedBoundsInRoot()
            assertEquals(8.dp, second.left - first.right, "${buttonGroupDoc.examples[i].title} group gap")
        }
    }

    @Test fun voiceRecipesExposeTheirTooltipOnKeyboardFocusAndPillShape() {
        val indexState = mutableIntStateOf(3)
        setContent(indexState)
        for (i in listOf(3, 7)) {
            rule.runOnIdle { indexState.intValue = i }
            rule.onNodeWithContentDescription("Voice Mode").requestFocus()
            rule.onNodeWithText("Voice Mode").assertIsDisplayed()
        }
        val pixels = rule.onNodeWithTag("button-group-pill-leading").captureToImage().toPixelMap()
        val corner = pixels[0, 0]
        val paintedTop = (0 until pixels.width).count { pixels[it, 0] != corner }
        assertTrue(paintedTop < pixels.width / 2, "Pill button top edge must curve across half its width")
    }

    @Test fun inputAndCurrencyRecipesKeepOfficialLabelsAndSelection() {
        val indexState = mutableIntStateOf(6)
        setContent(indexState)
        rule.onNodeWithText("Search...").assertIsDisplayed()
        rule.onNodeWithContentDescription("Search").assertHasClickAction()
        rule.onNode(hasSetTextAction()).performTextInput("Compose")
        rule.onNodeWithContentDescription("Search").performClick()
        rule.onNode(hasSetTextAction()).assertTextEquals("Compose")
        rule.runOnIdle { indexState.intValue = 9 }
        rule.onNodeWithText("$").performClick()
        rule.onNodeWithText("US Dollar").assertIsDisplayed()
        rule.onNodeWithText("Euro").assertIsDisplayed().performClick()
        rule.onNodeWithText("€").assertIsDisplayed()
        rule.onNodeWithText("$").assertDoesNotExist()
        rule.onNodeWithContentDescription("Send").assertHasClickAction()
    }

    private fun setContent(index: State<Int>) {
        rule.setContent {
            Theme(
                preset = StylePreset.Nova,
                isDark = dark,
                minTouchTarget = null,
                fontFamily = catalogFontFamily(),
            ) {
                OverlayHost {
                    Box(
                        Modifier.requiredSize((if (width < 768) 325 else 638).dp, 600.dp),
                    ) {
                        key(index.value) { buttonGroupDoc.examples[index.value].RenderPreview() }
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "width={0}, dark={1}")
        fun cases(): List<Array<Any>> =
            listOf(390, 1440).flatMap { width ->
                listOf(false, true).map { dark -> arrayOf<Any>(width, dark) }
            }
    }
}
