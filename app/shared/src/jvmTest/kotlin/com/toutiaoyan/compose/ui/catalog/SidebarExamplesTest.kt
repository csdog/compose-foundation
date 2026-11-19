package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.WindowInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.IntSize
import com.toutiaoyan.compose.ui.catalog.docs.sidebarDoc
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SidebarExamplesTest {
    @get:Rule val rule = createComposeRule()

    private fun show(
        title: String,
        viewportPx: Int = 1440,
    ) {
        rule.setContent {
            val original = LocalWindowInfo.current
            val window =
                remember(original) {
                    object : WindowInfo by original {
                        override val containerSize: IntSize = IntSize(viewportPx, 900)
                    }
                }
            CompositionLocalProvider(LocalWindowInfo provides window) {
                Theme(preset = StylePreset.Nova, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                    Box(Modifier.fillMaxSize()) {
                        sidebarDoc.examples.single { it.title == title }.preview()
                    }
                }
            }
        }
    }

    @Test fun pagePreservesOfficialOrderAndAllExistingKotlinExamples() {
        assertEquals("Sidebar", sidebarDoc.title)
        assertEquals("A composable, themeable and customizable sidebar component.", sidebarDoc.description)
        assertEquals(listOf("Default", "Controlled Sidebar", "RTL"), sidebarDoc.examples.take(3).map { it.title })
        assertTrue(sidebarDoc.examples.drop(3).all { it.title.startsWith("Kotlin extension: ") })
        assertEquals(48, sidebarDoc.examples.drop(3).size)
        assertEquals("Kotlin extension: App shell", sidebarDoc.examples[3].title)
        val sections = docSections(sidebarDoc).map { it.title }
        val ordered =
            listOf(
                "Installation", "Usage", "Composition", "Structure", "SidebarProvider", "Sidebar", "useSidebar",
                "SidebarHeader", "SidebarFooter", "SidebarContent", "SidebarGroup", "SidebarMenu", "SidebarMenuButton",
                "SidebarMenuAction", "SidebarMenuSub", "SidebarMenuBadge", "SidebarMenuSkeleton", "SidebarTrigger",
                "SidebarRail", "Controlled Sidebar", "Theming", "Styling", "RTL", "Changelog", "RTL Support",
            )
        assertEquals(ordered, sections.filter { it in ordered })
    }

    @Test fun officialDefaultOpensTeamsAndRetainsThePlaygroundSubmenuAcrossIconCollapse() {
        show("Default")
        val playground = rule.onNodeWithContentDescription("Playground")
        playground.assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Expanded"))
        rule.onNodeWithText("History").assertIsDisplayed()
        rule.onNodeWithContentDescription("Acme Inc").performClick()
        rule.onNodeWithText("Acme Corp.").performClick()
        rule.onNodeWithContentDescription("Acme Corp.").assertIsDisplayed()
        rule.onNodeWithTag("sidebar-demo-trigger").performClick()
        rule.mainClock.advanceTimeBy(260)
        rule.onNodeWithText("History").assertDoesNotExist()
        rule.onNodeWithTag("sidebar-demo-trigger").performClick()
        rule.mainClock.advanceTimeBy(260)
        rule.onNodeWithText("History").assertIsDisplayed()
        playground.performClick()
        playground.assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Collapsed"))
        rule.onNodeWithText("History").assertDoesNotExist()
        playground.performClick()
        playground.assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Expanded"))
        rule.onNodeWithText("History").assertIsDisplayed()
    }

    @Test fun officialRtlChangesThePanelSideWhenTheLanguageChanges() {
        show("RTL")
        val arabicPanel = rule.onNodeWithTag("sidebar-demo-panel").getBoundsInRoot()
        val arabicInset = rule.onNodeWithTag("sidebar-demo-inset").getBoundsInRoot()
        val language = rule.onNodeWithContentDescription("Preview language")
        val arabicLanguage = language.getBoundsInRoot()
        assertTrue(arabicPanel.left >= arabicInset.right)
        assertEquals(arabicInset.left.value + 16f, arabicLanguage.left.value, 0.1f)
        assertEquals(arabicInset.top.value + 16f, arabicLanguage.top.value, 0.1f)
        rule.onNodeWithText("شركة أكمي").assertIsDisplayed()
        language.performClick()
        rule.onNodeWithText("English").performClick()
        val englishPanel = rule.onNodeWithTag("sidebar-demo-panel").getBoundsInRoot()
        val englishInset = rule.onNodeWithTag("sidebar-demo-inset").getBoundsInRoot()
        val englishLanguage = language.getBoundsInRoot()
        assertTrue(englishPanel.right <= englishInset.left)
        assertEquals(englishInset.right.value - 16f, englishLanguage.right.value, 0.1f)
        assertEquals(englishInset.top.value + 16f, englishLanguage.top.value, 0.1f)
        rule.onNodeWithText("Acme Inc").assertIsDisplayed()
    }

    @Test fun mobileSheetExampleStartsClosedAndTheTriggerOpensIt() {
        show("Kotlin extension: Mobile sheet", viewportPx = 1440)
        rule.onNodeWithText("Page content goes here.").assertIsDisplayed()
        rule.onNodeWithText("Application").assertDoesNotExist()
        rule.onNodeWithContentDescription("Toggle Sidebar").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Application").assertIsDisplayed()
        rule.onNodeWithText("Home").assertIsDisplayed()
    }

    @Test fun automaticMobileBelow768OpensASheetFromTheTrigger() {
        show("Kotlin extension: App shell", viewportPx = 390)
        rule.onNodeWithText("Application").assertDoesNotExist()
        rule.onNodeWithContentDescription("Toggle Sidebar").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Application").assertIsDisplayed()
    }

    @Test fun rightSidebarTakesTheFlexSpaceAfterTheInset() {
        show("Kotlin extension: Right", viewportPx = 1440)
        val rail = rule.onNodeWithText("Application").getBoundsInRoot()
        val main = rule.onNodeWithText("Main content").getBoundsInRoot()
        assertTrue((rail.right - rail.left).value > 0f, "right-side rail must not collapse to zero width")
        assertTrue(rail.left.value > main.left.value, "right-side rail must sit after the inset")
    }

    @Test fun desktopTriggerCollapsesOffcanvasButKeepsContentComposed() {
        show("Kotlin extension: App shell", viewportPx = 1440)
        rule.onNodeWithText("Application").assertIsDisplayed()
        rule.onNodeWithContentDescription("Toggle Sidebar").performClick()
        rule.mainClock.advanceTimeBy(260)
        rule.waitForIdle()
        // Offcanvas retains composed state and hides the closed rail from accessibility.
        rule.onNodeWithText("Application").assertDoesNotExist()
        rule.onNodeWithText("Application", useUnmergedTree = true).assertExists()
        rule.onNodeWithContentDescription("Toggle Sidebar").performClick()
        rule.mainClock.advanceTimeBy(260)
        rule.waitForIdle()
        rule.onNodeWithText("Application").assertIsDisplayed()
    }
}
