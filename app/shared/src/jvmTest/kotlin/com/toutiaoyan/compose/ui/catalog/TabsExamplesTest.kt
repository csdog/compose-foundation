@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.platform.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.catalog.docs.tabsDoc
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
class TabsExamplesTest(private val viewport: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "viewport={0},dark={1}")
        fun modes() =
            listOf(
                arrayOf<Any>(1440, false),
                arrayOf<Any>(1440, true),
                arrayOf<Any>(390, false),
                arrayOf<Any>(390, true),
            )
    }

    private fun advance() {
        rule.waitForIdle()
        rule.mainClock.advanceTimeBy(320)
        rule.waitForIdle()
    }

    private fun show(title: String) {
        rule.mainClock.autoAdvance = false
        rule.setContent {
            val original = LocalWindowInfo.current
            val window =
                remember(original) {
                    object : WindowInfo by original {
                        override val containerSize = IntSize(viewport, 800)
                    }
                }
            CompositionLocalProvider(LocalWindowInfo provides window) {
                Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                    Box(
                        Modifier.width(minOf(viewport, 640).dp).height(640.dp)
                            .background(currentTheme.colors.background)
                            .padding(if (title == "RTL") 0.dp else 24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(Modifier.testTag("tabs-capture").padding(bottom = 8.dp)) {
                            tabsDoc.examples.single { it.title == title }.preview()
                        }
                    }
                }
            }
        }
        rule.mainClock.advanceTimeByFrame()
        rule.waitForIdle()
    }

    private fun node(tag: String) = rule.onNodeWithTag(tag, useUnmergedTree = true)

    private fun bounds(tag: String) = node(tag).fetchSemanticsNode().boundsInRoot

    private fun capture(name: String) {
        val file = File("build/reports/tabs-recipes/$name-$viewport-${if (dark) "dark" else "light"}.png")
        file.parentFile.mkdirs()
        ImageIO.write(node("tabs-capture").captureToImage().toAwtImage(), "png", file)
    }

    @Test fun defaultActivationExtensionUsesTheActualDefault() {
        show("Kotlin extension: Default activation")
        rule.onNodeWithText("Account").requestFocus()
        rule.onNodeWithText("Account").performKeyInput { pressKey(Key.DirectionRight) }
        advance()
        rule.onNodeWithText("Password").assertIsFocused().assertIsSelected()
    }

    @Test fun defaultHasFourSourceCardsAndShowsOneAtATime() {
        show("Default")
        node("tabs-example").assertWidthIsEqualTo((if (viewport == 390) 342 else 400).dp)
        val panels =
            listOf(
                "overview" to "You have 12 active projects and 3 pending tasks.",
                "analytics" to "Page views are up 25% compared to last month.",
                "reports" to "You have 5 reports ready and available to export.",
                "settings" to "Configure notifications, security, and themes.",
            )
        panels.forEach { (value, body) ->
            node("tabs-$value").performClick()
            advance()
            node("tabs-$value").assertIsSelected()
            rule.onNodeWithText(body).assertIsDisplayed()
            panels.filter { it.first != value }.forEach { rule.onNodeWithText(it.second).assertDoesNotExist() }
            capture("default-$value")
        }
    }

    @Test fun lineUsesManualActivationAndThirtyTwoPixelTrack() {
        show("Line")
        node("tabs-list").assertHeightIsEqualTo(32.dp)
        node("tabs-overview").requestFocus()
        node("tabs-overview").performKeyInput { pressKey(Key.DirectionRight) }
        advance()
        node("tabs-analytics").assertIsFocused().assertIsSelected()
        capture("line")
    }

    @Test fun verticalListUsesUniformWidthsAndDownArrowFocus() {
        show("Vertical")
        val account = bounds("tabs-account")
        val password = bounds("tabs-password")
        val notifications = bounds("tabs-notifications")
        assertEquals(account.width, notifications.width, .1f)
        assertEquals(account.bottom, password.top, .1f)
        node("tabs-account").requestFocus()
        node("tabs-account").performKeyInput { pressKey(Key.DirectionDown) }
        advance()
        node("tabs-password").assertIsFocused().assertIsSelected()
        capture("vertical")
    }

    @Test fun disabledCannotBeActivated() {
        show("Disabled")
        node("tabs-disabled").assertIsNotEnabled().performClick()
        advance()
        node("tabs-home").assertIsSelected()
        capture("disabled")
    }

    @Test fun iconsAreSeparateFromTextAndBothTriggersWork() {
        show("Icons")
        val label = rule.onNodeWithText("Preview", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        assertTrue(label.left > bounds("tabs-preview").left + 20f)
        node("tabs-code").performClick()
        advance()
        node("tabs-code").assertIsSelected()
        capture("icons")
    }

    private fun direction(
        rtl: Boolean,
        first: String,
        last: String,
    ) {
        val a = rule.onNodeWithTag("tabs-overview").fetchSemanticsNode().positionInRoot.x
        val b = rule.onNodeWithTag("tabs-settings").fetchSemanticsNode().positionInRoot.x
        assertEquals(rtl, a > b)
        rule.onAllNodesWithText(first).assertCountEquals(2)
        rule.onNodeWithText(last).assertExists()
    }

    @Test fun rtlArabicDefaultHasTranslatedCardAndMirroredTriggers() {
        show("RTL")
        direction(true, "نظرة عامة", "الإعدادات")
        rule.onNodeWithText("لديك ١٢ مشروعًا نشطًا و٣ مهام معلقة.").assertIsDisplayed()
        capture("rtl-arabic")
    }

    private fun language(label: String) {
        rule.onNodeWithContentDescription("Preview language").performClick()
        advance()
        rule.onNodeWithText(label).performClick()
        advance()
    }

    @Test fun rtlEnglishSelectorRestoresLtr() {
        show("RTL")
        language("English")
        direction(false, "Overview", "Settings")
        capture("rtl-english")
    }

    @Test fun rtlHebrewSelectorKeepsRtl() {
        show("RTL")
        language("Hebrew (עברית)")
        direction(true, "סקירה כללית", "הגדרות")
        rule.onNodeWithText("יש לך 12 פרויקטים פעילים ו-3 משימות ממתינות.").assertIsDisplayed()
        capture("rtl-hebrew")
    }

    @Test fun defaultDemoActivatesOnArrowKeys() {
        show("Default")
        node("tabs-overview").requestFocus()
        node("tabs-overview").performKeyInput { pressKey(Key.DirectionRight) }
        advance()
        node("tabs-analytics").assertIsSelected()
        rule.onNodeWithText("Page views are up 25% compared to last month.").assertIsDisplayed()
    }

    @Test fun pageKeepsOfficialRecipeOrderCompositionAndKotlinExtensions() {
        assertEquals(
            listOf("Default", "Line", "Vertical", "Disabled", "Icons", "RTL"),
            tabsDoc.examples.take(6).map { it.title },
        )
        assertTrue(tabsDoc.examples.drop(6).all { it.title.startsWith("Kotlin extension: ") })
        assertEquals(null, tabsDoc.examplesHeading)
        val markdown = componentPageMarkdown(tabsDoc)
        val order =
            listOf(
                "## Composition",
                "## Line",
                "## Vertical",
                "## Disabled",
                "## Icons",
                "## RTL",
                "## API Reference",
            ).map { markdown.indexOf(it) }
        assertTrue(order.all { it >= 0 }, "missing a section heading: $order")
        assertEquals(order.sorted(), order)
        assertContains(markdown, "Use the line variant on `TabsList` for a line style.")
        assertContains(markdown, "orientation = TabsOrientation.Vertical")
        assertFalse(markdown.contains("## Examples"))
    }
}
