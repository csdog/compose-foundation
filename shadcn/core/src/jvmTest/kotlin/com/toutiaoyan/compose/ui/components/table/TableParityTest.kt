@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.table

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.*

@RunWith(Parameterized::class)
class TableParityTest(private val dark: Boolean) : ScreenshotTest() {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "dark={0}")
        fun modes() = listOf(arrayOf(false), arrayOf(true))
    }

    private fun node(tag: String) = composeRule.onNodeWithTag(tag, useUnmergedTree = true)

    private fun bounds(tag: String) = node(tag).fetchSemanticsNode().boundsInRoot

    @Test fun captionDeclaredFirstRendersBelowRowsWithSixteenPixelMargin() {
        setThemedContent(darkTheme = dark, stylePreset = StylePreset.Nova) {
            Table(Modifier.width(300.dp)) {
                TableCaption("Recent invoices", Modifier.testTag("caption"))
                TableHeader { TableHead("Invoice") }
                TableBody { TableRow(Modifier.testTag("body"), isLast = true) { TableCell("INV001") } }
            }
        }
        assertTrue(bounds("caption").top >= bounds("body").bottom)
        val text = composeRule.onNodeWithText("Recent invoices").fetchSemanticsNode().boundsInRoot
        assertEquals(bounds("body").bottom + 16f, text.top, .1f)
    }

    @Test fun headerIsFortyAndBodyUsesFourteenTwentyWithEightPixelPadding() {
        setThemedContent(darkTheme = dark, stylePreset = StylePreset.Nova) {
            Table(Modifier.width(300.dp)) {
                TableHeader(Modifier.testTag("header")) { TableHead("Invoice") }
                TableBody { TableRow(Modifier.testTag("body"), isLast = true) { TableCell("INV001") } }
            }
        }
        node("header").assertHeightIsEqualTo(40.dp)
        node("body").assertHeightIsEqualTo(36.dp)
        val text = composeRule.onNodeWithText("INV001").fetchSemanticsNode().boundsInRoot
        assertEquals(bounds("body").left + 8f, text.left, .1f)
        assertEquals(bounds("body").top + 8f, text.top, .1f)
        assertEquals(20f, text.height, .1f)
    }

    @Test fun rowIsTransparentAndHoverUsesMutedAtHalfAlpha() {
        var hover = Color.Unspecified
        setThemedContent(darkTheme = dark, stylePreset = StylePreset.Nova) {
            hover = currentTheme.colors.muted.copy(alpha = .5f).compositeOver(Color.Green)
            Box(Modifier.background(Color.Green)) {
                Table(Modifier.width(300.dp)) {
                    TableRow(Modifier.testTag("row"), isLast = true) { TableCell("INV001") }
                }
            }
        }
        assertEquals(Color.Green, node("row").captureToImage().toPixelMap()[280, 5])
        node("row").performMouseInput { moveTo(center) }
        composeRule.waitForIdle()
        val p = node("row").captureToImage().toPixelMap()[280, 5]
        assertEquals(hover.red, p.red, .005f)
        assertEquals(hover.green, p.green, .005f)
        assertEquals(hover.blue, p.blue, .005f)
    }

    @Test fun selectedStateIsExposedToAccessibility() {
        var fill = Color.Unspecified
        setThemedContent(darkTheme = dark, stylePreset = StylePreset.Nova) {
            fill = currentTheme.colors.muted
            Table(Modifier.width(300.dp)) {
                TableRow(Modifier.testTag("selected"), selected = true, isLast = true) { TableCell("INV001") }
            }
        }
        node("selected").assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
        node("selected").performMouseInput { moveTo(center) }
        composeRule.waitForIdle()
        assertEquals(fill, node("selected").captureToImage().toPixelMap()[280, 5])
    }

    @Test fun footerIsHalfMutedWithATopRuleAndNoTrailingRule() {
        var fill = Color.Unspecified
        setThemedContent(darkTheme = dark, stylePreset = StylePreset.Nova) {
            fill = currentTheme.colors.muted.copy(alpha = .5f).compositeOver(currentTheme.colors.background)
            Table(Modifier.width(300.dp).testTag("table")) {
                TableFooter { TableRow(Modifier.testTag("footer"), isLast = true) { TableCell("Total") } }
            }
        }
        val p = node("footer").captureToImage().toPixelMap()[280, 5]
        assertEquals(fill.red, p.red, .005f)
        assertEquals(fill.green, p.green, .005f)
        assertEquals(fill.blue, p.blue, .005f)
        node("table").assertHeightIsEqualTo(37.dp)
        assertEquals(bounds("table").top + 1f, bounds("footer").top, .1f)
    }

    @Test fun overflowingColumnsExposeHorizontalScrollAndCanReachLastCell() {
        setThemedContent(darkTheme = dark, stylePreset = StylePreset.Nova) {
            Table(Modifier.width(160.dp)) {
                TableBody {
                    TableRow {
                        repeat(4) { TableCell("Cell $it", Modifier.width(160.dp)) }
                    }
                }
            }
        }
        val scroll =
            composeRule.onNode(
                SemanticsMatcher.keyIsDefined(SemanticsProperties.HorizontalScrollAxisRange),
                useUnmergedTree = true,
            ).fetchSemanticsNode().config[SemanticsProperties.HorizontalScrollAxisRange]
        assertTrue(scroll.maxValue() >= 480f)
        composeRule.onNodeWithText("Cell 3").assertIsNotDisplayed().performScrollTo().assertIsDisplayed()
        assertTrue(scroll.value() > 0f)
    }

    @Test fun physicalRightAndLogicalEndAreDistinctInRtl() {
        setThemedContent(darkTheme = dark, stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Table(Modifier.width(300.dp)) {
                    TableRow(Modifier.testTag("row"), isLast = true) {
                        TableCell("Right", Modifier.weight(1f), textStyle = TextStyle(textAlign = TextAlign.Right))
                        TableCell("End", Modifier.weight(1f), textStyle = TextStyle(textAlign = TextAlign.End))
                    }
                }
            }
        }
        val right = composeRule.onNodeWithText("Right").fetchSemanticsNode().boundsInRoot
        val end = composeRule.onNodeWithText("End").fetchSemanticsNode().boundsInRoot
        assertEquals(bounds("row").right - 8f, right.right, .1f)
        assertEquals(bounds("row").left + 8f, end.left, .1f)
    }

    @Test fun stringLambdaAndComposableCellSlotsBothWork() {
        var clicks = 0
        setThemedContent(darkTheme = dark, stylePreset = StylePreset.Nova) {
            Table(Modifier.width(300.dp)) {
                TableBody {
                    TableRow {
                        TableCell { "Lambda value" }
                        TableCell(content = { Button("Action", onClick = { clicks++ }) })
                    }
                }
            }
        }
        composeRule.onNodeWithText("Lambda value").assertIsDisplayed()
        composeRule.onNodeWithText("Action").performClick()
        assertEquals(1, clicks)
    }
}
