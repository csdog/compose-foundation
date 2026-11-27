package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.catalog.docs.menubarDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.*

class MenubarExamplesTest {
    @get:Rule val rule = createComposeRule()

    private fun show(title: String) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.width(600.dp).height(700.dp), contentAlignment = Alignment.Center) {
                        menubarDoc.examples.single { it.title == title }.RenderPreview()
                    }
                }
            }
        }
    }

    @Test fun officialSixRecipesComeBeforeKotlinSupplements() {
        assertEquals(
            listOf(
                "Default",
                "Checkbox",
                "Radio",
                "Submenu",
                "With Icons",
                "RTL",
            ),
            menubarDoc.examples.take(6).map { it.title },
        )
        assertTrue(menubarDoc.examples.any { it.title == "Kotlin extension: Controlled" })
        assertTrue(
            menubarDoc.examples.drop(6).all { it.title.startsWith("Kotlin extension: ") },
        )
        val rtl = menubarDoc.examples.single { it.title == "RTL" }.code
        assertFalse(rtl.contains("RtlPreview("))
        assertFalse(rtl.contains("menubarTranslations"))
    }

    @Test fun defaultIncludesProfilesAndRealFileItems() {
        show("Default")
        rule.onNodeWithText("Profiles").assertIsDisplayed()
        rule.onNodeWithText("File").performClick()
        rule.onNodeWithText("New Incognito Window").assertIsNotEnabled()
        rule.onNodeWithText("⌘T", useUnmergedTree = true).assertIsDisplayed()
        rule.onNodeWithText("Share").performClick()
        rule.onNodeWithText("Email link").assertIsDisplayed()
    }

    @Test fun defaultLiteralCheckedAndProfileValuesStayReadonlyLikeUpstream() {
        show("Default")
        rule.onNodeWithText("View").performClick()
        rule.onNodeWithText("Full URLs").assertIsOn().performClick().assertIsOn()
        rule.onNodeWithText("View").performClick()
        rule.onNodeWithText("Profiles").performClick()
        rule.onNodeWithText("Benoit").assertIsSelected()
        rule.onNodeWithText("Andy").performClick()
        rule.onNodeWithText("Benoit").assertIsSelected()
        rule.onNodeWithText("Andy").assertIsNotSelected()
    }

    @Test fun checkboxToggleSurvivesClosingAndReopening() {
        show("Checkbox")
        rule.onNodeWithText("View").performClick()
        rule.onNodeWithText("Always Show Bookmarks Bar").assertIsOff().performClick()
        rule.onNodeWithText("Always Show Bookmarks Bar").assertIsOn()
        rule.onNodeWithText("View").performClick()
        rule.onNodeWithText("View").performClick()
        rule.onNodeWithText("Always Show Bookmarks Bar").assertIsOn()
        rule.onNodeWithText("Force Reload").assertIsNotEnabled()
    }

    @Test fun radioSelectionSurvivesClosingAndReopening() {
        show("Radio")
        rule.onNodeWithText("Profiles").performClick()
        rule.onNodeWithText("Benoit").assertIsSelected()
        rule.onNodeWithText("Andy").performClick()
        rule.onNodeWithText("Andy").assertIsSelected()
        rule.onNodeWithText("Profiles").performClick()
        rule.onNodeWithText("Profiles").performClick()
        rule.onNodeWithText("Andy").assertIsSelected()
    }

    @Test fun iconRecipeIncludesTheDestructiveActionAndShortcut() {
        show("With Icons")
        rule.onNodeWithText("File").performClick()
        rule.onNodeWithText("New File").assertIsDisplayed()
        rule.onNodeWithText("⌘N", useUnmergedTree = true).assertIsDisplayed()
        rule.onNodeWithText("New File").performClick()
        rule.onNodeWithText("More").performClick()
        rule.onNodeWithText("Delete").assertIsDisplayed()
    }

    @Test fun rtlStartsInArabicAndRendersTranslatedMenuItems() {
        show("RTL")
        val file = rule.onNodeWithText("ملف").fetchSemanticsNode().boundsInRoot
        val edit = rule.onNodeWithText("تعديل").fetchSemanticsNode().boundsInRoot
        assertTrue(file.left > edit.left)
        rule.onNodeWithText("ملف").performClick()
        rule.onNodeWithText("علامة تبويب جديدة").assertIsDisplayed()
    }

    @Test fun exportKeepsOfficialSectionsAndKotlinExtensions() {
        val markdown = componentPageMarkdown(menubarDoc)
        assertFalse(markdown.contains("## Examples"))
        val order =
            listOf(
                "## Checkbox",
                "## Radio",
                "## Submenu",
                "## With Icons",
                "## RTL",
                "## API Reference",
            ).map { markdown.indexOf(it) }
        assertTrue(order.all { it >= 0 }, "missing a section heading: $order")
        assertEquals(order.sorted(), order)
        assertContains(markdown, "## Controlled")
        assertContains(markdown, "base-ui.com/react/components/menubar#api-reference")
        assertFalse(markdown.contains("EmojiText"))
    }
}
