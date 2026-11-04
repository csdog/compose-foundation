package com.toutiaoyan.compose.ui.components.chart

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class ChartInteractionParityTest : ScreenshotTest() {
    private val config = mapOf("desktop" to ChartSeries("Desktop", Color.Blue))
    private val data =
        listOf(
            ChartPoint(
                "January",
                mapOf("desktop" to 186f),
            ),
            ChartPoint(
                "February",
                mapOf("desktop" to 305f),
            ),
        )

    private fun graph(
        line: Boolean = false,
        reversed: Boolean = false,
    ) {
        lateinit var inputMode: InputModeManager
        setThemedContent(stylePreset = StylePreset.Nova) {
            inputMode = LocalInputModeManager.current
            ChartContainer(Modifier.width(400.dp).height(200.dp), config = config) {
                val content: @Composable (ChartPoint) -> Unit = { ChartTooltipContent(it.label, config, it.values) }
                if (line) {
                    LineChart(data, reversed = reversed, tooltip = content)
                } else {
                    BarChart(data, reversed = reversed, tooltip = content)
                }
            }
        }
        composeRule.runOnIdle { inputMode.requestInputMode(InputMode.Keyboard) }
    }

    @Test fun barKeyboardNavigationUpdatesTheRealTooltipAndEscapePreservesFocus() {
        graph()
        val chart = composeRule.onNodeWithContentDescription("Chart")
        chart.requestFocus()
        composeRule.onNodeWithText("186").assertIsDisplayed()
        chart.performKeyInput { pressKey(Key.DirectionRight) }
        composeRule.onNodeWithText("305").assertIsDisplayed()
        chart.performKeyInput { pressKey(Key.Escape) }
        composeRule.onNodeWithText("305").assertDoesNotExist()
        chart.assertIsFocused()
    }

    @Test fun lineUsesTheSameKeyboardAndTooltipContract() {
        graph(line = true)
        val chart = composeRule.onNodeWithContentDescription("Chart")
        chart.requestFocus()
        chart.performKeyInput { pressKey(Key.MoveEnd) }
        composeRule.onNodeWithText("305").assertIsDisplayed()
        chart.performKeyInput { pressKey(Key.MoveHome) }
        composeRule.onNodeWithText("186").assertIsDisplayed()
    }

    @Test fun reversedAxisMakesLeftMoveToTheNextDatum() {
        graph(reversed = true)
        val chart = composeRule.onNodeWithContentDescription("Chart")
        chart.requestFocus()
        chart.performKeyInput { pressKey(Key.DirectionLeft) }
        composeRule.onNodeWithText("305").assertIsDisplayed()
    }

    @Test fun defaultIndexIsInitialStateAndDoesNotResetUserSelection() {
        var initial by mutableStateOf<Int?>(1)
        setThemedContent(stylePreset = StylePreset.Nova) {
            ChartContainer(Modifier.width(400.dp).height(200.dp), config = config) {
                BarChart(data, defaultIndex = initial, tooltip = { ChartTooltipContent(it.label, config, it.values) })
            }
        }
        composeRule.onNodeWithText("305").assertIsDisplayed()
        composeRule.runOnIdle { initial = 0 }
        composeRule.onNodeWithText("305").assertIsDisplayed()
        composeRule.onNodeWithText("186").assertDoesNotExist()
    }

    @Test fun initialDimensionDoesNotFreezeMeasuredWidthAndAspect() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            ChartContainer(Modifier.width(500.dp).testTag("container"), initialDimension = DpSize(320.dp, 200.dp)) {}
        }
        composeRule.onNodeWithTag("container").assertWidthIsEqualTo(500.dp).assertHeightIsEqualTo(281.dp)
    }

    @Test fun explicitMeasuredHeightWinsOverInitialDimension() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            ChartContainer(
                Modifier.width(500.dp).height(240.dp).testTag("container"),
                initialDimension =
                    DpSize(
                        320.dp,
                        200.dp,
                    ),
            ) {}
        }
        composeRule.onNodeWithTag("container").assertWidthIsEqualTo(500.dp).assertHeightIsEqualTo(240.dp)
    }

    @Test fun hitTestingHandlesBoundariesAndReversedDataOrder() {
        assertNull(chartIndexAt(-1f, 400f, 4, false, false))
        assertNull(chartIndexAt(400f, 400f, 4, false, false))
        assertNull(chartIndexAt(0f, 0f, 4, false, false))
        assertEquals(0, chartIndexAt(1f, 400f, 4, false, false))
        assertEquals(1, chartIndexAt(150f, 400f, 4, false, false))
        assertEquals(2, chartIndexAt(150f, 400f, 4, true, false))
        assertEquals(3, chartIndexAt(399f, 400f, 4, false, true))
    }

    @Test fun shrinkingDataHidesAnOutOfRangeInitialTooltip() {
        var points by mutableStateOf(data)
        setThemedContent(stylePreset = StylePreset.Nova) {
            ChartContainer(Modifier.width(400.dp).height(200.dp), config = config) {
                BarChart(points, defaultIndex = 1, tooltip = { ChartTooltipContent(it.label, config, it.values) })
            }
        }
        composeRule.onNodeWithText("305").assertIsDisplayed()
        composeRule.runOnIdle { points = data.take(1) }
        composeRule.onNodeWithText("305").assertDoesNotExist()
    }
}
