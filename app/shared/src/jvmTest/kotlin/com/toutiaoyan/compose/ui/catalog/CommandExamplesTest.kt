package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.commandDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CommandExamplesTest {
    @get:Rule val rule = createComposeRule()

    private fun show(title: String) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.width(720.dp).height(720.dp), contentAlignment = Alignment.TopCenter) {
                        commandDoc.examples.single { it.title == title }.preview()
                    }
                }
            }
        }
    }

    @Test fun officialRecipesAppearInTheOfficialOrderWithOfficialCopy() {
        assertEquals(
            listOf("Default", "Basic", "Shortcuts", "Groups", "Scrollable", "RTL"),
            commandDoc.examples.take(6).map { it.title },
        )
        assertEquals(
            "A simple command menu in a dialog.",
            commandDoc.examples.single { it.title == "Basic" }.descriptionMarkdown,
        )
        assertEquals(
            "A command menu with groups, icons and separators.",
            commandDoc.examples.single { it.title == "Groups" }.descriptionMarkdown,
        )
        assertEquals(
            "Scrollable command menu with multiple items.",
            commandDoc.examples.single { it.title == "Scrollable" }.descriptionMarkdown,
        )
        assertTrue(
            commandDoc.examples.single { it.title == "RTL" }
                .descriptionMarkdown.orEmpty()
                .startsWith("To enable RTL support"),
        )
        assertTrue(commandDoc.examples.drop(6).all { it.title.startsWith("Kotlin extension: ") })
        assertNotNull(commandDoc.compositionCode)
        assertEquals("About", commandDoc.beforeExamples.single().title)
    }

    @Test fun demoRecipeUsesTheOfficialDemoContent() {
        show("Default")
        rule.onNodeWithText("Suggestions").assertIsDisplayed()
        rule.onNodeWithText("Calendar").assertIsDisplayed()
        rule.onNodeWithText("Search Emoji").assertIsDisplayed()
        rule.onNodeWithText("Calculator").assertIsNotEnabled()
        rule.onAllNodesWithText("Settings").assertCountEquals(2)
    }

    @Test fun basicRecipeOpensItsDialog() {
        show("Basic")
        rule.onNodeWithText("Open Menu").performClick()
        rule.onNodeWithText("Suggestions").assertIsDisplayed()
        rule.onNodeWithText("Calendar").assertIsDisplayed()
        rule.onNodeWithText("Search Emoji").assertIsDisplayed()
    }

    @Test fun scrollableRecipeReachesTheLastGroup() {
        show("Scrollable")
        rule.onNodeWithText("Open Menu").performClick()
        rule.onNodeWithText("Code Editor").performScrollTo().assertIsDisplayed()
    }

    @Test fun rtlRecipeRendersArabicHeadings() {
        show("RTL")
        rule.onNodeWithText("اقتراحات").assertIsDisplayed()
        rule.onAllNodesWithText("الإعدادات").assertCountEquals(2)
        rule.onNodeWithText("التقويم").assertIsDisplayed()
    }

    @Test fun longListHomeAndEndScrollToTheEdges() {
        show("Kotlin extension: Long list keyboard navigation")
        rule.onNodeWithText("Action 0").assertIsDisplayed()
        rule.onNodeWithText("Action 0").requestFocus()
        rule.onNodeWithText("Action 0").performKeyInput { pressKey(Key.MoveEnd) }
        rule.onNodeWithText("Action 79").assertIsDisplayed()
        rule.onNodeWithText("Action 79").performKeyInput { pressKey(Key.MoveHome) }
        rule.onNodeWithText("Action 0").assertIsDisplayed()
    }
}
