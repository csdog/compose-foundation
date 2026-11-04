package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.kbdDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.*

class KbdExamplesTest {
    @get:Rule val rule = createComposeRule()

    private fun show(title: String) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.width(560.dp).height(500.dp), contentAlignment = Alignment.Center) {
                        kbdDoc.examples.single { it.title == title }.preview()
                    }
                }
            }
        }
    }

    @Test fun allSixOfficialRecipesArePresent() {
        assertEquals(
            listOf(
                "Default",
                "Group",
                "Button",
                "Tooltip",
                "Input Group",
                "RTL",
            ),
            kbdDoc.examples.map { it.title },
        )
        assertEquals(listOf("Kbd", "KbdGroup"), kbdDoc.apiSections.map { it.title })
        assertFalse(kbdDoc.examples.single { it.title == "RTL" }.code.contains("RtlPreview("))
    }

    @Test fun defaultRendersAllActualModifierAndControlKeys() {
        show("Default")
        listOf("⌘", "⇧", "⌥", "⌃", "Ctrl", "+", "B").forEach { rule.onNodeWithText(it).assertIsDisplayed() }
    }

    @Test fun inlineGroupUsesActualKeycapChildrenInsideTheSentence() {
        show("Group")
        rule.onNodeWithText("Ctrl + B", useUnmergedTree = true).assertIsDisplayed()
        rule.onNodeWithText("Ctrl + K", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test fun inputGroupPreservesTypingWithBothKeycaps() {
        show("Input Group")
        rule.onNodeWithText("Search...").performTextInput("Components")
        rule.onNodeWithText("Components").assertIsDisplayed()
        rule.onNodeWithText("⌘").assertIsDisplayed()
        rule.onNodeWithText("K").assertIsDisplayed()
    }

    @Test fun saveAndPrintShowTheirOwnKeyboardHints() {
        show("Tooltip")
        rule.onNodeWithText("Save").performMouseInput {
            enter(
                center,
                delayMillis =
                0,
            )
            ; moveTo(
                center,
                delayMillis =
                0,
            )
        }
        rule.waitUntil(3000) { rule.onAllNodesWithText("Save Changes").fetchSemanticsNodes().isNotEmpty() }
        rule.onNodeWithText("S").assertIsDisplayed()
        rule.onNodeWithText("Save").performMouseInput { exit(delayMillis = 0) }
        rule.mainClock.advanceTimeBy(400)
        rule.onNodeWithText("Print").performMouseInput { moveTo(center, delayMillis = 0) }
        rule.waitUntil(3000) { rule.onAllNodesWithText("Print Document").fetchSemanticsNodes().isNotEmpty() }
        rule.onNodeWithText("Ctrl").assertIsDisplayed()
        rule.onNodeWithText("P").assertIsDisplayed()
    }

    @Test fun threeLanguagePickerChangesTheActualKeyOrder() {
        show("RTL")

        fun left(key: String) = rule.onNodeWithText(key).fetchSemanticsNode().boundsInRoot.left
        assertTrue(left("⌘") > left("⌃"))
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("English").performClick()
        assertTrue(left("⌘") < left("⌃"))
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("עברית").performClick()
        assertTrue(left("⌘") > left("⌃"))
    }
}
