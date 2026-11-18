@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.select

import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.style.Style
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SelectScrollArrowGeometryTest : ScreenshotTest() {
    private fun showScrollable(
        varied: Boolean = false,
        onPick: (String) -> Unit = {},
    ) {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.fillMaxSize()) {
                Select<String>(null, onPick, defaultOpen = true) {
                    SelectTrigger()
                    SelectContent(Modifier.testTag("panel")) {
                        SelectGroup {
                            repeat(30) {
                                SelectItem(
                                    "Option $it",
                                    if (varied && it % 3 == 0) Modifier.height(48.dp) else Modifier,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private val arrow: SemanticsMatcher
        get() =
            hasAnyAncestor(hasTestTag("panel")) and
                SemanticsMatcher.keyIsDefined(SemanticsProperties.HideFromAccessibility) and
                SemanticsMatcher("scroll control without option text") { !it.config.contains(SemanticsProperties.Text) }

    @Test fun novaScrollArrowsHaveSourcePaddingBeforeAndAfterScrolling() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Select<String>(null, {}, defaultOpen = true) {
                SelectTrigger()
                SelectContent(Modifier.testTag("panel")) {
                    SelectGroup { repeat(30) { SelectItem("Option $it") } }
                }
            }
        }
        val arrow =
            hasAnyAncestor(hasTestTag("panel")) and
                SemanticsMatcher.keyIsDefined(SemanticsProperties.HideFromAccessibility) and
                SemanticsMatcher("scroll control without option text") { !it.config.contains(SemanticsProperties.Text) }
        composeRule.onAllNodes(arrow).assertCountEquals(1)
        composeRule.onNode(arrow).assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.HideFromAccessibility))
            .assertHeightIsEqualTo(24.dp).performTouchInput { click(Offset(center.x, 1f)) }
        composeRule.onAllNodes(arrow).assertCountEquals(2)
        composeRule.onAllNodes(arrow).fetchSemanticsNodes().indices.forEach { index ->
            composeRule.onAllNodes(arrow)[index].assertHeightIsEqualTo(24.dp)
        }
    }

    @Test fun touchOpeningUsesTheFullViewportAndKeepsGestureScrolling() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Select("Option 8", {}, modal = false) {
                SelectTrigger(Modifier.testTag("trigger"))
                SelectContent(Modifier.testTag("panel"), style = Style { height(160.dp) }) {
                    SelectGroup {
                        repeat(30) { SelectItem("Option $it", Modifier.testTag("option-$it")) }
                    }
                }
            }
        }
        composeRule.onNodeWithTag("trigger").performTouchInput { click() }
        val panel = composeRule.onNodeWithTag("panel")
        val selected = composeRule.onNodeWithTag("option-8").assertIsDisplayed()
        val bottomGap =
            panel.fetchSemanticsNode().boundsInWindow.bottom - selected.fetchSemanticsNode().boundsInWindow.bottom
        assertTrue(
            bottomGap < with(composeRule.density) { 12.dp.toPx() },
            "Selected row bottom gap was $bottomGap px",
        )
        composeRule.onAllNodes(arrow).assertCountEquals(0)

        selected.performKeyInput { pressKey(Key.DirectionDown) }
        composeRule.onNodeWithTag("option-9").assertIsFocused().assertIsDisplayed()
        composeRule.onAllNodes(arrow).assertCountEquals(0)

        val scrollNode =
            composeRule.onNode(
                hasAnyAncestor(hasTestTag("panel")) and
                    SemanticsMatcher.keyIsDefined(SemanticsProperties.VerticalScrollAxisRange),
            )
        val scroll = scrollNode.fetchSemanticsNode().config[SemanticsProperties.VerticalScrollAxisRange].value
        val beforeSwipe = scroll()
        panel.performTouchInput { swipeUp(durationMillis = 160) }
        assertTrue(scroll() > beforeSwipe)
        composeRule.onAllNodes(arrow).assertCountEquals(0)
    }

    @Test fun mouseHoverScrollsToEndAndBackWithoutSelecting() {
        var picked: String? = null
        showScrollable(onPick = { picked = it })
        val before = composeRule.onNodeWithText("Option 10").fetchSemanticsNode().boundsInWindow.top
        composeRule.mainClock.autoAdvance = false
        try {
            val down = composeRule.onNode(arrow).fetchSemanticsNode().boundsInWindow.center
            composeRule.onAllNodes(isRoot()).onFirst().performMouseInput { enter(down) }
            // Keep this check below the 40ms delay instead of rounding to full frames.
            composeRule.mainClock.advanceTimeBy(20, ignoreFrameDuration = true)
            assertEquals(before, composeRule.onNodeWithText("Option 10").fetchSemanticsNode().boundsInWindow.top, .1f)
            composeRule.mainClock.advanceTimeBy(400)
            composeRule.onNodeWithText("Option 29").assertIsDisplayed()
            val up = composeRule.onNode(arrow).fetchSemanticsNode().boundsInWindow.center
            composeRule.onAllNodes(isRoot()).onFirst().performMouseInput { moveTo(up) }
            composeRule.mainClock.advanceTimeByFrame()
            composeRule.mainClock.advanceTimeBy(400)
            composeRule.onNodeWithText("Option 0").assertIsDisplayed()
            composeRule.runOnIdle { assertEquals(null, picked) }
        } finally {
            composeRule.mainClock.autoAdvance = true
        }
    }

    @Test fun keyboardHomeAfterHoverRevealsFirstOptionAndStopsStationaryArrow() {
        var picked: String? = null
        showScrollable(onPick = { picked = it })
        composeRule.mainClock.autoAdvance = false
        try {
            val down = composeRule.onNode(arrow).fetchSemanticsNode().boundsInWindow.center
            composeRule.onAllNodes(isRoot()).onFirst().performMouseInput { enter(down) }
            composeRule.mainClock.advanceTimeBy(400)
            composeRule.onNodeWithText("Option 29").assertIsDisplayed()
            composeRule.onNodeWithText("Option 0").performKeyInput { pressKey(Key.MoveHome) }
            composeRule.mainClock.advanceTimeByFrame()
            composeRule.mainClock.advanceTimeBy(400)
            composeRule.onNodeWithText("Option 0").assertIsFocused().assertIsDisplayed()
            composeRule.onNodeWithText("Option 0").performKeyInput { pressKey(Key.Enter) }
            composeRule.runOnIdle { assertEquals("Option 0", picked) }
        } finally {
            composeRule.mainClock.autoAdvance = true
        }
    }

    @Test fun hoverUsesActualHeightsForMixedRows() {
        showScrollable(varied = true)
        composeRule.mainClock.autoAdvance = false
        try {
            val down = composeRule.onNode(arrow).fetchSemanticsNode().boundsInWindow.center
            composeRule.onAllNodes(isRoot()).onFirst().performMouseInput { enter(down) }
            composeRule.mainClock.advanceTimeBy(1500)
            composeRule.onNodeWithText("Option 29").assertIsDisplayed()
        } finally {
            composeRule.mainClock.autoAdvance = true
        }
    }
}
