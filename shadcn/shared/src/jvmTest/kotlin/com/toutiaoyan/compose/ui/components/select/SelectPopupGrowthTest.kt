@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.select

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals

class SelectPopupGrowthTest : ScreenshotTest() {
    private fun show(
        selected: String? = null,
        aligned: Boolean = true,
        topPadding: Dp = 200.dp,
    ) {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.fillMaxSize().padding(top = topPadding)) {
                Select(selected, {}, defaultOpen = true) {
                    SelectTrigger(Modifier.testTag("trigger"))
                    SelectContent(Modifier.testTag("panel"), alignItemWithTrigger = aligned) {
                        SelectGroup { repeat(30) { SelectItem("Option $it") } }
                    }
                }
            }
        }
    }

    private val arrows: SemanticsMatcher
        get() =
            hasAnyAncestor(hasTestTag("panel")) and
                SemanticsMatcher.keyIsDefined(SemanticsProperties.HideFromAccessibility)

    @Test fun bottomAnchoredPanelConsumesScrollByGrowingUp() {
        show()
        val before = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow
        val first = composeRule.onNodeWithText("Option 0").fetchSemanticsNode().boundsInWindow
        composeRule.onAllNodes(arrows).onLast().performTouchInput { click() }
        val after = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow
        val moved = composeRule.onNodeWithText("Option 0").fetchSemanticsNode().boundsInWindow
        assertEquals(before.height + 48f, after.height, 1f)
        assertEquals(before.top - 48f, after.top, 1f)
        assertEquals(before.bottom, after.bottom, 1f)
        assertEquals(first.top - 48f, moved.top, 1f)
    }

    @Test fun topAnchoredPanelConsumesReverseScrollByGrowingDown() {
        show("Option 15")
        val before = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow
        val selected = composeRule.onAllNodesWithText("Option 15").onLast().fetchSemanticsNode().boundsInWindow
        composeRule.onAllNodes(arrows).onFirst().performTouchInput { click() }
        val after = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow
        val moved = composeRule.onAllNodesWithText("Option 15").onLast().fetchSemanticsNode().boundsInWindow
        assertEquals(before.height + 48f, after.height, 1f)
        assertEquals(before.top, after.top, 1f)
        assertEquals(before.bottom + 48f, after.bottom, 1f)
        assertEquals(selected.top + 48f, moved.top, 1f)
    }

    @Test fun growthStopsAtWindowMarginsAndScrollingContinues() {
        show()
        repeat(7) { composeRule.onAllNodes(arrows).onLast().performTouchInput { click() } }
        val panel = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow
        assertEquals(10f, panel.top, 1f)
        assertEquals(748f, panel.height, 1f)
        assertEquals(758f, panel.bottom, 1f)
        composeRule.onNodeWithText("Option 29").assertIsDisplayed()
    }

    @Test fun topAnchoredGrowthKeepsScrollOvershootAtTheHeightLimit() {
        show("Option 15")
        val selected = composeRule.onAllNodesWithText("Option 15").onLast().fetchSemanticsNode().boundsInWindow
        repeat(3) { composeRule.onAllNodes(arrows).onFirst().performTouchInput { click() } }
        val panel = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow
        val moved = composeRule.onAllNodesWithText("Option 15").onLast().fetchSemanticsNode().boundsInWindow
        assertEquals(10f, panel.top, 1f)
        assertEquals(748f, panel.height, 1f)
        assertEquals(selected.top + 144f, moved.top, 1f)
    }

    @Test fun popperScrollDoesNotExpandThePanel() {
        show(aligned = false)
        val before = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow
        val row = composeRule.onNodeWithText("Option 3").fetchSemanticsNode().boundsInWindow
        composeRule.onAllNodes(arrows).onLast().performTouchInput { click() }
        val after = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow
        val moved = composeRule.onNodeWithText("Option 3").fetchSemanticsNode().boundsInWindow
        assertEquals(before.height, after.height, 1f)
        assertEquals(before.top, after.top, 1f)
        assertEquals(row.top - 48f, moved.top, 1f)
    }

    @Test fun reopeningStartsFromTheOriginalAlignedHeight() {
        show()
        val before = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow
        composeRule.onAllNodes(arrows).onLast().performTouchInput { click() }
        composeRule.onNodeWithText("Option 0").performKeyInput { pressKey(Key.Escape) }
        composeRule.onNodeWithTag("trigger").performKeyInput { pressKey(Key.Enter) }
        val reopened = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow
        assertEquals(before.height, reopened.height, 1f)
        assertEquals(before.top, reopened.top, 1f)
    }

    @Test fun nearlyMaximalBottomAnchorStillExpandsUpOnePixel() {
        show("Option 1", topPadding = 17.dp)
        val before = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow
        assertEquals(11f, before.top, .1f)
        composeRule.onAllNodes(arrows).onLast().performTouchInput { click() }
        val after = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow
        assertEquals(10f, after.top, .1f)
        assertEquals(758f, after.bottom, .1f)
    }

    @Test fun nearFullNaturalContentUsesTheSourceTopEdgeClamp() {
        val result =
            selectPopupGeometry(
                trigger = Rect(24f, 220f, 216f, 252f),
                windowHeight = 768f,
                contentHeight = 736f,
                selectedCenter = 214f,
                alignItem = true,
                touch = false,
                sideOffset = 4f,
            )
        assertEquals(10f, result.top, .1f)
        assertEquals(736f, result.height, .1f)
    }
}
