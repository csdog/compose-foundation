package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.WindowInfo
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

    @Test fun pageKeepsOfficialTitleAndDescriptionAndKotlinExtensionTitles() {
        assertEquals("Sidebar", sidebarDoc.title)
        assertEquals("A composable, themeable and customizable sidebar component.", sidebarDoc.description)
        assertTrue(sidebarDoc.examples.isNotEmpty())
        assertTrue(
            sidebarDoc.examples.all { it.title.startsWith("Kotlin extension: ") },
            "The official app-shell demo is not migrated yet, so every recipe is a Kotlin extension",
        )
        assertEquals("Kotlin extension: App shell", sidebarDoc.examples.first().title)
        assertTrue(sidebarDoc.examples.any { it.title == "Kotlin extension: Mobile sheet" })
        assertTrue(sidebarDoc.examples.any { it.title == "Kotlin extension: Inset" })
        assertTrue(sidebarDoc.examples.any { it.title == "Kotlin extension: Inset primitives" })
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
        // Offcanvas keeps its composed children (state retention) even while clipped away.
        rule.onNodeWithText("Application").assertExists()
        rule.onNodeWithContentDescription("Toggle Sidebar").performClick()
        rule.mainClock.advanceTimeBy(260)
        rule.waitForIdle()
        rule.onNodeWithText("Application").assertIsDisplayed()
    }
}
