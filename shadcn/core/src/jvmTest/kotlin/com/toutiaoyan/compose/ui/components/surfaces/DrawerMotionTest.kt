package com.toutiaoyan.compose.ui.components.surfaces

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.drawer.*
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertTrue

class DrawerMotionTest : ScreenshotTest() {
    @Test fun parentFollowsShorterFrontDrawerAndRestoresNaturalHeight() {
        var childOpen by mutableStateOf(false)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Drawer(defaultOpen = true) {
                DrawerContent(Modifier.testTag("parent panel")) {
                    Box(Modifier.height(320.dp))
                    Drawer(open = childOpen, onOpenChange = { childOpen = it }) {
                        DrawerContent { Box(Modifier.height(140.dp)) }
                    }
                }
            }
        }

        fun height() = composeRule.onNodeWithTag("parent panel").fetchSemanticsNode().boundsInWindow.height
        val natural = height()
        composeRule.runOnIdle { childOpen = true }
        composeRule.waitForIdle()
        assertTrue(height() < natural - 100f, "Parent should use the front drawer's shorter height")
        composeRule.runOnIdle { childOpen = false }
        composeRule.waitForIdle()
        assertTrue(kotlin.math.abs(height() - natural) < 1f, "Closing should restore the parent's natural height")
    }

    @Test fun parentStackAnimatesAndReversesFromItsCurrentGeometry() {
        var childOpen by mutableStateOf(false)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Drawer(defaultOpen = true) {
                DrawerContent {
                    Box(Modifier.width(300.dp).height(320.dp).testTag("parent marker"))
                    Drawer(open = childOpen, onOpenChange = { childOpen = it }) {
                        DrawerContent { DrawerHeader("Child") }
                    }
                }
            }
        }

        fun width() = composeRule.onNodeWithTag("parent marker").fetchSemanticsNode().boundsInWindow.width
        val original = width()
        composeRule.mainClock.autoAdvance = false
        composeRule.runOnIdle { childOpen = true }
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeBy(32)
        val early = width()
        composeRule.mainClock.advanceTimeBy(80)
        val middle = width()
        composeRule.mainClock.advanceTimeBy(500)
        val stacked = width()
        assertTrue(
            original >= early && early > middle && middle > stacked,
            "Parent must interpolate instead of jumping: $original, $early, $middle, $stacked",
        )
        composeRule.runOnIdle { childOpen = false }
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        width() // Flush the first transition frame before sampling its motion.
        composeRule.mainClock.advanceTimeBy(96)
        val returning = width()
        assertTrue(returning > stacked && returning < original, "Closing should interpolate back: $returning")
        composeRule.runOnIdle { childOpen = true }
        composeRule.mainClock.advanceTimeByFrame()
        val reversed = width()
        assertTrue(reversed < original, "Reopening during return must not reset the layer")
        composeRule.mainClock.advanceTimeBy(550)
        assertTrue(kotlin.math.abs(width() - stacked) < 1f)
        composeRule.mainClock.autoAdvance = true
    }
}
