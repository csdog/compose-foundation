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
                        commandDoc.examples.single { it.title == title }.RenderPreview()
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
        assertEquals("About", commandDoc.introductionSections.single().title)
    }

    @Test fun generatedRecipeCodeContainsOnlyExpandedRowsAndBalancedScopes() {
        val expectedRows = mapOf("Shortcuts" to 3, "Groups" to 6, "RTL" to 18)
        expectedRows.forEach { (title, count) ->
            val code = commandDoc.examples.single { it.title == title }.code
            assertTrue(!code.contains("commandItemsCode"), "$title leaks a generator call into copied code")
            assertEquals(count, Regex("CommandExampleItem\\(\"").findAll(code).count(), title)
            var depth = 0
            code.forEach { char ->
                if (char == '{') depth++
                if (char == '}') depth--
                assertTrue(depth >= 0, "$title closes a scope before opening it")
            }
            assertEquals(0, depth, "$title has unbalanced scopes")
        }
    }

    @Test fun sourceAttributionAndScrollableShortcutsMatchPinnedExamples() {
        val about = commandDoc.introductionSections.single().blocks.single().toString()
        assertTrue(about.contains("https://github.com/dip/cmdk"))
        assertTrue(about.contains("https://www.dip.org/"))
        assertTrue(commandDoc.apiSections.first().descriptionMarkdown.contains("https://github.com/dip/cmdk"))
        val code = commandDoc.examples.single { it.title == "Scrollable" }.code
        assertTrue(code.contains("Lucide.Trash, shortcut = \"⌫\""))
        listOf("LayoutGrid", "List", "Bell", "CircleQuestionMark").forEach { icon ->
            assertTrue(!code.contains("Lucide.$icon, shortcut"), "Unexpected shortcut on $icon")
        }
    }

    @Test fun demoRecipeUsesTheOfficialDemoContent() {
        show("Default")
        rule.onNodeWithText("Suggestions").assertIsDisplayed()
        rule.onNodeWithText("Calendar").assertIsDisplayed()
        rule.onNodeWithText("Search Emoji").assertIsDisplayed()
        rule.onNodeWithText("Calculator").assertIsNotEnabled()
        rule.onAllNodesWithText("Settings").assertCountEquals(2)
    }

    @Test fun clearingSearchRestoresVisualKeyboardOrder() {
        show("Default")
        rule.onNode(hasSetTextAction()).performTextInput("Settings")
        rule.onNode(hasSetTextAction()).performTextClearance()
        rule.onNode(hasSetTextAction()).performKeyInput {
            pressKey(Key.MoveHome)
            pressKey(Key.Enter)
        }
        rule.onNodeWithText("Selected: Calendar").assertIsDisplayed()
        rule.onNode(hasSetTextAction()).performKeyInput {
            pressKey(Key.DirectionDown)
            pressKey(Key.DirectionDown)
            pressKey(Key.Enter)
        }
        rule.onNodeWithText("Selected: Profile").assertIsDisplayed()
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

    @Test fun rtlRecipeSwitchesToEnglishAndHebrew() {
        show("RTL")
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("English").performClick()
        rule.onNodeWithText("Calendar").assertIsDisplayed()
        rule.onNodeWithText("Calculator").assertIsNotEnabled()
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("Hebrew (עברית)").performClick()
        rule.onNodeWithText("לוח שנה").assertIsDisplayed()
        rule.onNodeWithText("מחשבון").assertIsNotEnabled()
        assertTrue(commandDoc.examples.single { it.title == "RTL" }.code.contains("לוח שנה"))
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
