@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.*

@RunWith(Parameterized::class)
class TabsChromeParityTest(private val dark: Boolean) : ScreenshotTest() {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "dark={0}")
        fun modes() = listOf(arrayOf(false), arrayOf(true))
    }

    @Test fun selectedChromeFillsWeightedTriggerOnItsFirstFrame() {
        var expected = Color.Unspecified
        composeRule.mainClock.autoAdvance = false
        setThemedContent(darkTheme = dark, stylePreset = StylePreset.Nova) {
            val colors = currentTheme.colors
            expected =
                (if (dark) colors.input.copy(alpha = colors.input.alpha * .3f) else colors.background)
                    .compositeOver(colors.muted)
            Tabs(value = "a") {
                TabsList(Modifier.width(280.dp)) {
                    Row(Modifier.fillMaxWidth()) {
                        TabsTrigger("a", "Alpha", Modifier.weight(1f).testTag("a"))
                        TabsTrigger("b", "Beta", Modifier.weight(1f))
                    }
                }
            }
        }
        composeRule.mainClock.advanceTimeByFrame()
        val p = composeRule.onNodeWithTag("a").captureToImage().toPixelMap()
        val actual = p[p.width - 12, p.height / 2]
        assertEquals(expected.red, actual.red, .005f)
        assertEquals(expected.green, actual.green, .005f)
        assertEquals(expected.blue, actual.blue, .005f)
    }

    @Test fun rootHasEightPixelGapAndFourteenTwentyMediumText() {
        setThemedContent(darkTheme = dark, stylePreset = StylePreset.Nova) {
            Tabs(value = "a", modifier = Modifier.testTag("root")) {
                TabsList(Modifier.testTag("list")) { TabsTrigger("a", "Alpha") }
                TabsContent("a", Modifier.size(100.dp, 20.dp)) {}
            }
        }
        composeRule.onNodeWithTag("list").assertHeightIsEqualTo(32.dp)
        composeRule.onNodeWithTag("root").assertHeightIsEqualTo(60.dp)
        val layouts = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithText(
            "Alpha",
            useUnmergedTree =
            true,
        ).performSemanticsAction(SemanticsActions.GetTextLayoutResult) {
            it(layouts)
        }
        assertEquals(14f, layouts.single().layoutInput.style.fontSize.value)
        assertEquals(20f, layouts.single().layoutInput.style.lineHeight.value)
        assertEquals(FontWeight.Medium, layouts.single().layoutInput.style.fontWeight)
    }

    @Test fun lineUnderlineIsOutsideTheTriggerAtTheSourceOffset() {
        var foreground = Color.Unspecified
        setThemedContent(darkTheme = dark, stylePreset = StylePreset.Nova) {
            foreground = currentTheme.colors.foreground
            Box(Modifier.size(300.dp, 60.dp).background(currentTheme.colors.background).testTag("canvas")) {
                Tabs(value = "a") {
                    TabsList(variant = TabsListVariant.Line) {
                        TabsTrigger("a", "Alpha", Modifier.testTag("a"))
                        TabsTrigger("b", "Beta")
                    }
                }
            }
        }
        val canvas = composeRule.onNodeWithTag("canvas")
        val origin = canvas.fetchSemanticsNode().boundsInRoot
        val trigger = composeRule.onNodeWithTag("a").fetchSemanticsNode().boundsInRoot
        val p = canvas.captureToImage().toPixelMap()
        val x = ((trigger.left + trigger.right) / 2 - origin.left).toInt()
        val y = (trigger.bottom - origin.top + 3f).toInt()
        val actual = p[x, y]
        assertEquals(foreground.red, actual.red, .005f)
        assertEquals(foreground.green, actual.green, .005f)
        assertEquals(foreground.blue, actual.blue, .005f)
        assertEquals(actual, p[x, y + 1])
    }

    @Test fun keyboardFocusDoesNotFillTransparentLineTrigger() {
        var background = Color.Unspecified
        setThemedContent(darkTheme = dark, stylePreset = StylePreset.Nova) {
            background = currentTheme.colors.background
            Tabs(value = "a") {
                TabsList(variant = TabsListVariant.Line) {
                    TabsTrigger("a", "Alpha", Modifier.testTag("a"))
                    TabsTrigger("b", "Beta")
                }
            }
        }
        val tab = composeRule.onNodeWithTag("a")
        tab.requestFocus()
        tab.performKeyInput { pressKey(Key.MoveHome) }
        val actual = tab.captureToImage().toPixelMap()[5, 12]
        assertEquals(background.red, actual.red, .005f)
        assertEquals(background.green, actual.green, .005f)
        assertEquals(background.blue, actual.blue, .005f)
    }
}
