@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.catalog.docs.chartDoc
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
class ChartExamplesTest(private val viewport: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()
    private lateinit var inputMode: InputModeManager

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

    private fun show(title: String) {
        rule.setContent {
            val original = LocalWindowInfo.current
            val window =
                remember(original) {
                    object : WindowInfo by original {
                        override val containerSize =
                            IntSize(
                                viewport,
                                800,
                            )
                    }
                }
            inputMode = LocalInputModeManager.current
            CompositionLocalProvider(LocalWindowInfo provides window) {
                Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                    Box(
                        Modifier.fillMaxSize().background(currentTheme.colors.background),
                        contentAlignment =
                            Alignment.Center,
                    ) {
                        Box(
                            Modifier.width(
                                minOf(
                                    viewport,
                                    640,
                                ).dp,
                            ).padding(24.dp).testTag("chart-scene"),
                            contentAlignment =
                                Alignment.Center,
                        ) {
                            chartDoc.examples.single { it.title == title }.preview()
                        }
                    }
                }
            }
        }
        rule.runOnIdle { inputMode.requestInputMode(InputMode.Keyboard) }
    }

    private fun chart() = rule.onNodeWithContentDescription("Chart")

    private fun capture(name: String) {
        val file = File("build/reports/chart-recipes/$name-$viewport-${if (dark) "dark" else "light"}.png")
        file.parentFile.mkdirs()
        ImageIO.write(rule.onNodeWithTag("chart-scene").captureToImage().toAwtImage(), "png", file)
    }

    @Test fun firstStepUsesTwoSeriesAndHasNoAxisLabelsYet() {
        assertEquals(
            listOf(
                "Default",
                "Your First Chart",
                "Add a Grid",
                "Add an Axis",
                "Add Tooltip",
                "Add Legend",
                "Tooltip",
                "RTL",
            ),
            chartDoc.examples.take(8).map { it.title },
        )
        show("Your First Chart")
        chart().assertExists()
        rule.onNodeWithText("Jan").assertDoesNotExist()
        chart().requestFocus()
        chart().assertIsFocused()
        capture("first")
    }

    @Test fun gridStepStillOmitsTheXAxis() {
        show("Add a Grid")
        chart().assertExists()
        rule.onNodeWithText("Jan").assertDoesNotExist()
        capture("grid")
    }

    @Test fun axisStepContainsAllSixSourceMonths() {
        show("Add an Axis")
        listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun").forEach { rule.onNodeWithText(it).assertIsDisplayed() }
        capture("axis")
    }

    @Test fun tooltipFollowsKeyboardNavigationThroughActualMonthData() {
        show("Add Tooltip")
        chart().requestFocus()
        rule.onNodeWithText("January").assertIsDisplayed()
        rule.onNodeWithText("186").assertIsDisplayed()
        rule.onNodeWithText("80").assertIsDisplayed()
        chart().performKeyInput { pressKey(Key.DirectionRight) }
        rule.onNodeWithText("February").assertIsDisplayed()
        rule.onNodeWithText("305").assertIsDisplayed()
        capture("tooltip")
        chart().performKeyInput { pressKey(Key.Escape) }
        rule.onNodeWithText("February").assertDoesNotExist()
        chart().assertIsFocused()
    }

    @Test fun legendSharesConfigAndKeepsJuneData() {
        show("Add Legend")
        rule.onNodeWithText("Desktop").assertIsDisplayed()
        rule.onNodeWithText("Mobile").assertIsDisplayed()
        chart().requestFocus()
        chart().performKeyInput { pressKey(Key.MoveEnd) }
        rule.onNodeWithText("June").assertIsDisplayed()
        rule.onNodeWithText("214").assertIsDisplayed()
        rule.onNodeWithText("140").assertIsDisplayed()
        capture("legend")
    }

    @Test fun interactiveDemoSwitchesTheActualSeriesAndKeepsPinnedTotals() {
        show("Default")
        rule.onNodeWithText("Bar Chart - Interactive").assertIsDisplayed()
        rule.onNodeWithText("7,324").assertIsDisplayed()
        rule.onNodeWithText("7,250").assertIsDisplayed()
        rule.onNodeWithText("Apr 30").assertIsDisplayed()
        capture("interactive-desktop")
        rule.onNodeWithText("Mobile").performClick()
        chart().requestFocus()
        rule.onNodeWithText("Apr 1, 2024").assertIsDisplayed()
        rule.onNodeWithText("Page Views").assertIsDisplayed()
        rule.onNodeWithText("150").assertIsDisplayed()
        chart().performKeyInput { pressKey(Key.DirectionRight) }
        rule.onNodeWithText("180").assertIsDisplayed()
        capture("interactive-mobile")
    }

    @Test fun anatomyUsesGroupedNumbersAndTheResponsiveLineExample() {
        show("Tooltip")
        listOf("Label", "Name", "Indicator").forEach { rule.onNodeWithText(it).assertIsDisplayed() }
        rule.onAllNodesWithText("1,286").assertCountEquals(2)
        if (viewport >= 768) {
            rule.onNodeWithText("12,486").assertIsDisplayed()
        } else {
            rule.onNodeWithText("12,486").assertDoesNotExist()
        }
        capture("anatomy")
    }

    @Test fun rtlSwitchesThreeLanguagesAndReversesKeyboardDataOrder() {
        show("RTL")
        chart().requestFocus()
        chart().performKeyInput { pressKey(Key.MoveHome) }
        rule.onNodeWithText("يناير").assertIsDisplayed()
        chart().performKeyInput { pressKey(Key.DirectionLeft) }
        rule.onNodeWithText("فبراير").assertIsDisplayed()
        capture("rtl-arabic")
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("English").performClick()
        chart().requestFocus()
        chart().performKeyInput { pressKey(Key.MoveHome) }
        rule.onNodeWithText("January").assertIsDisplayed()
        chart().performKeyInput { pressKey(Key.DirectionRight) }
        rule.onNodeWithText("February").assertIsDisplayed()
        capture("rtl-english")
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("Hebrew (עברית)").performClick()
        chart().requestFocus()
        chart().performKeyInput { pressKey(Key.MoveHome) }
        rule.onNodeWithText("ינואר").assertIsDisplayed()
        chart().performKeyInput { pressKey(Key.DirectionLeft) }
        rule.onNodeWithText("פברואר").assertIsDisplayed()
        capture("rtl-hebrew")
    }
}
