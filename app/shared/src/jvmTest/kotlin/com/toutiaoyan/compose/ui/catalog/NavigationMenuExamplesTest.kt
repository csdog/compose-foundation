package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.platform.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.catalog.docs.navigationMenuDoc
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.*

@RunWith(Parameterized::class)
class NavigationMenuExamplesTest(private val viewport: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()
    private val visited = mutableListOf<String>()

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "viewport={0},dark={1}")
        fun modes() =
            listOf(
                arrayOf<Any>(
                    1440,
                    false,
                ),
                arrayOf<Any>(
                    1440,
                    true,
                ),
                arrayOf<Any>(
                    390,
                    false,
                ),
                arrayOf<Any>(
                    390,
                    true,
                ),
            )
    }

    private fun advance(ms: Long = 400) {
        rule.mainClock.advanceTimeBy(ms)
        rule.waitForIdle()
    }

    private fun show(title: String) {
        rule.mainClock.autoAdvance = false
        rule.setContent {
            val original = LocalWindowInfo.current
            val density = LocalDensity.current
            val window =
                remember(original, density) {
                    object : WindowInfo by original {
                        override val containerSize =
                            with(density) { IntSize(viewport.dp.roundToPx(), 900.dp.roundToPx()) }
                    }
                }
            CompositionLocalProvider(
                LocalWindowInfo provides window,
                LocalUriHandler provides
                    object : UriHandler {
                        override fun openUri(uri: String) {
                            visited.add(uri)
                        }
                    },
            ) {
                Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                    Box(
                        Modifier.testTag("navigation-capture").width(
                            (
                                if (viewport >= 768) {
                                    720
                                } else {
                                    viewport
                                }
                            ).dp,
                        ).height(560.dp)
                            .background(currentTheme.colors.background).padding(24.dp),
                        contentAlignment =
                            Alignment.TopCenter,
                    ) {
                        navigationMenuDoc.examples.single { it.title == title }.RenderPreview()
                    }
                }
            }
        }
        advance()
    }

    private fun capture(
        name: String,
        tag: String,
    ) {
        val out = File("build/reports/navigation-menu-recipes/$name-$viewport-${if (dark) "dark" else "light"}.png")
        out.parentFile.mkdirs()
        ImageIO.write(rule.onNodeWithTag(tag).captureToImage().toAwtImage(), "png", out)
    }

    private fun open(label: String) = select(label)

    private fun select(label: String) {
        val target = rule.onNodeWithText(label)
        target.performMouseInput { moveTo(center, delayMillis = 0) }
        advance(32)
        target.performClick()
        advance()
    }

    @Test fun delaySupplementActuallyDelaysOpeningAPanel() {
        show("Kotlin extension: Delay")
        rule.onNodeWithText("Getting started").performMouseInput {
            enter(center, delayMillis = 0)
            moveTo(center, delayMillis = 0)
        }
        advance(100)
        rule.onNodeWithText("Delayed navigation links").assertDoesNotExist()
        advance(150)
        rule.onNodeWithText("Delayed navigation links").assertIsDisplayed()
    }

    @Test fun defaultHasSourceLinksAndActionsCloseThePanel() {
        show("Default")
        rule.onNodeWithText("Getting started").assertIsDisplayed()
        rule.onNodeWithText("With Icon").assertIsDisplayed()
        capture("default-idle", "navigation-capture")
        open("Getting started")
        rule.onNodeWithTag("navigation-start-panel").assertWidthIsEqualTo(392.dp)
        listOf(
            "Introduction",
            "Installation",
            "Typography",
        ).forEach {
            rule.onNodeWithText(
                it,
                useUnmergedTree =
                true,
            ).assertIsDisplayed()
        }
        capture("default-start", "navigation-start-panel")
        listOf(
            "Typography" to "https://ui.shadcn.com/docs/primitives/typography",
            "Introduction" to "https://ui.shadcn.com/docs",
            "Installation" to "https://ui.shadcn.com/docs/installation",
        ).forEachIndexed { index, (label, uri) ->
            if (index > 0) open("Getting started")
            select(label)
            advance()
            assertEquals(uri, visited.last())
            rule.onNodeWithTag("navigation-start-panel").assertDoesNotExist()
        }
        rule.onNodeWithText("Docs").performClick()
        advance()
        assertEquals("https://ui.shadcn.com/docs", visited.last())
    }

    @Test fun iconLinksKeepTheirOriginalPlaceholderBehaviorAndClose() {
        show("Default")
        open("With Icon")
        rule.onNodeWithTag("navigation-icons-panel").assertWidthIsEqualTo(208.dp)
        listOf("Backlog", "To Do", "Done").forEach { rule.onNodeWithText(it).assertIsDisplayed() }
        capture("default-icons", "navigation-icons-panel")
        select("Done")
        advance()
        rule.onNodeWithTag("navigation-icons-panel").assertDoesNotExist()
        assertTrue(visited.isEmpty())
    }

    @Test fun componentsUseSourceBreakpointGridAndAllSixRealLinks() {
        show("Default")
        if (viewport < 768) {
            rule.onNodeWithText("Components").assertDoesNotExist()
            return
        }
        val links =
            listOf(
                "Alert Dialog" to "alert-dialog",
                "Hover Card" to "hover-card",
                "Progress" to "progress",
                "Scroll-area" to "scroll-area",
                "Tabs" to "tabs",
                "Tooltip" to "tooltip",
            )
        open("Components")
        rule.onNodeWithTag("navigation-components-panel").assertWidthIsEqualTo(608.dp)
        links.forEach { rule.onNodeWithText(it.first, useUnmergedTree = true).assertIsDisplayed() }
        val alert = rule.onNodeWithText("Alert Dialog", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val hover = rule.onNodeWithText("Hover Card", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertEquals(alert.top.value, hover.top.value, 0.5f)
        assertTrue(hover.left > alert.left)
        val layout = mutableListOf<TextLayoutResult>()
        rule.onNodeWithText(
            (
                "A modal dialog that interrupts the user with important " +
                    "content and expects a response."
            ),
            useUnmergedTree = true,
        )
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layout) }
        assertEquals(2, layout.single().lineCount)
        capture("default-components", "navigation-components-panel")
        links.forEachIndexed { index, (label, path) ->
            if (index > 0) open("Components")
            select(label)
            advance()
            assertEquals("https://ui.shadcn.com/docs/primitives/$path", visited.last())
            rule.onNodeWithTag("navigation-components-panel").assertDoesNotExist()
        }
    }

    @Test fun rtlLanguageSelectorUpdatesRealContentAndDirection() {
        show("RTL")
        capture("rtl-arabic-idle", "navigation-capture")
        val first = rule.onNodeWithText("البدء").getUnclippedBoundsInRoot()
        val docs = rule.onNodeWithText("الوثائق").getUnclippedBoundsInRoot()
        assertTrue(first.left > docs.left)
        open("البدء")
        listOf(
            "مقدمة",
            "التثبيت",
            "الطباعة",
        ).forEach {
            rule.onNodeWithText(
                it,
                useUnmergedTree =
                true,
            ).assertIsDisplayed()
        }
        capture("rtl-arabic-start", "navigation-start-panel")
        select("مقدمة")
        advance()
        assertEquals("https://ui.shadcn.com/docs", visited.last())
        rule.onNodeWithContentDescription("Preview language").performClick()
        advance()
        rule.onNodeWithText("English").performClick()
        advance()
        val englishFirst = rule.onNodeWithText("Getting started").getUnclippedBoundsInRoot()
        val englishDocs = rule.onNodeWithText("Docs").getUnclippedBoundsInRoot()
        assertTrue(englishFirst.left < englishDocs.left)
        rule.onNodeWithContentDescription("Preview language").performClick()
        advance()
        rule.onNodeWithText("Hebrew (עברית)").performClick()
        advance()
        val hebrewFirst = rule.onNodeWithText("התחלה").getUnclippedBoundsInRoot()
        val hebrewDocs = rule.onNodeWithText("תיעוד").getUnclippedBoundsInRoot()
        assertTrue(hebrewFirst.left > hebrewDocs.left)
        open("התחלה")
        listOf(
            "הקדמה",
            "התקנה",
            "טיפוגרפיה",
        ).forEach {
            rule.onNodeWithText(
                it,
                useUnmergedTree =
                true,
            ).assertIsDisplayed()
        }
        capture("rtl-hebrew-start", "navigation-start-panel")
    }
}
