package com.toutiaoyan.compose.ui.components.tooltip

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TooltipChromeParityTest : ScreenshotTest() {
    @Test fun novaChipUsesTwelveBySixPaddingAndNormalTwelveSixteenType() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.size(400.dp), contentAlignment = Alignment.Center) {
                Tooltip(open = true) {
                    TooltipTrigger("Hover")
                    TooltipContent("Add to library", Modifier.testTag("chip"), showArrow = false)
                }
            }
        }
        val text = composeRule.onNodeWithText("Add to library")
        val layouts = mutableListOf<TextLayoutResult>()
        text.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        val style = layouts.single().layoutInput.style
        assertEquals(12.sp, style.fontSize)
        assertEquals(16.sp, style.lineHeight)
        assertEquals(FontWeight.Normal, style.fontWeight)
        val chip = composeRule.onNodeWithTag("chip").fetchSemanticsNode().boundsInRoot
        val glyph = text.fetchSemanticsNode().boundsInRoot
        assertEquals(12f, glyph.left - chip.left, .1f)
        assertEquals(12f, chip.right - glyph.right, .1f)
        assertEquals(6f, glyph.top - chip.top, .1f)
        assertEquals(6f, chip.bottom - glyph.bottom, .1f)
        composeRule.onNodeWithTag("chip").assertHeightIsEqualTo(28.dp)
    }

    @Test fun arrowPointsTowardTheAnchorAfterThePopupFlips() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(
                Modifier.size(400.dp).background(Color.White).testTag("backing"),
                contentAlignment = Alignment.TopCenter,
            ) {
                Tooltip(open = true) {
                    TooltipTrigger(Modifier.testTag("anchor")) { Box(Modifier.size(30.dp)) }
                    TooltipContent(
                        modifier = Modifier.testTag("chip"),
                        enter = EnterTransition.None,
                        exit = ExitTransition.None,
                    ) { Box(Modifier.size(68.dp)) }
                }
            }
        }
        val anchor = composeRule.onNodeWithTag("anchor").fetchSemanticsNode().boundsInWindow
        val chip = composeRule.onNodeWithTag("chip").fetchSemanticsNode().boundsInWindow
        assertTrue(chip.top > anchor.bottom, "The requested top tooltip must flip below the anchor")
        val backing = composeRule.onNodeWithTag("backing")
        val origin = backing.fetchSemanticsNode().boundsInWindow.topLeft
        val panel = chip.translate(-origin)
        val pixels = backing.captureToImage().toPixelMap()
        val x = panel.center.x.toInt()
        assertTrue(pixels[x, panel.top.toInt() - 2].red < .5f, "The arrow must be above the flipped panel")
        assertEquals(1f, pixels[x, panel.bottom.toInt() + 2].red, .01f)
    }

    @Test fun arrowDoesNotAddSpaceToThePopupLayout() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.size(400.dp), contentAlignment = Alignment.Center) {
                Tooltip(open = true) {
                    TooltipTrigger("Hover")
                    TooltipContent("Add to library", Modifier.testTag("chip"))
                }
            }
        }
        composeRule.onNodeWithTag("chip").assertHeightIsEqualTo(28.dp)
        val anchor = composeRule.onNodeWithText("Hover").fetchSemanticsNode().boundsInRoot
        val chip = composeRule.onNodeWithTag("chip").fetchSemanticsNode().boundsInRoot
        assertEquals(4f, anchor.top - chip.bottom, .1f)
    }
}
