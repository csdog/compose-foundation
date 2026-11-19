package com.toutiaoyan.compose.ui.components.overlay

import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MenuPopupReadinessTest {
    @get:Rule val rule = createComposeRule()
    private var open by mutableStateOf(true)
    private var ready by mutableStateOf(false)

    private fun show(exit: ExitTransition = MenuDefaultExit) {
        rule.mainClock.autoAdvance = false
        rule.setContent {
            Box(Modifier.size(400.dp).background(Color.White).testTag("backing")) {
                Box(Modifier.size(24.dp)) {
                    MenuPopup(
                        open, {}, PopupPlacement.Bottom, 4.dp, PopupAlign.Start, 0.dp,
                        true, false, MenuDefaultEnter, exit, true, ready,
                    ) {
                        Box(
                            Modifier.size(200.dp, 60.dp)
                                .dropShadow(RectangleShape, Shadow(radius = 0.dp, spread = 12.dp, color = Color.Red))
                                .background(Color.Black)
                                .testTag("panel"),
                        )
                    }
                }
            }
        }
        rule.mainClock.advanceTimeByFrame()
        rule.waitForIdle()
    }

    private fun width() = rule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow.width

    @Test fun entryWaitsForPositioningAndThenRunsItsFullDuration() {
        show()
        val identity = rule.onNodeWithTag("panel").fetchSemanticsNode().id
        rule.mainClock.advanceTimeBy(500)
        assertEquals(190f, width(), .2f)
        rule.runOnIdle { ready = true }
        rule.mainClock.advanceTimeByFrame()
        rule.mainClock.advanceTimeBy(32)
        assertTrue(width() > 190f && width() < 200f, "Entry must still be in progress after readiness")
        rule.mainClock.advanceTimeBy(120)
        assertEquals(200f, width(), .2f)
        assertEquals(identity, rule.onNodeWithTag("panel").fetchSemanticsNode().id)
    }

    @Test fun mixedDefaultEntryWaitsWithoutShorteningTheCallerExit() {
        show(exit = PopupTransitions.Exit)
        rule.mainClock.advanceTimeBy(500)
        assertEquals(190f, width(), .2f, "Default entry must wait even with an explicit exit")
        rule.runOnIdle { ready = true }
        rule.mainClock.advanceTimeByFrame()
        rule.mainClock.advanceTimeBy(32)
        assertTrue(width() > 190f && width() < 200f)
        rule.mainClock.advanceTimeBy(120)
        assertEquals(200f, width(), .2f)
        rule.runOnIdle { open = false }
        rule.mainClock.advanceTimeByFrame()
        rule.mainClock.advanceTimeBy(160)
        rule.onNodeWithTag("panel").assertExists()
        rule.mainClock.advanceTimeBy(160)
        rule.onNodeWithTag("panel").assertDoesNotExist()
    }

    @Test fun mixedEntryReachesFullOpacityAfterReversingAnExplicitExit() {
        show(exit = PopupTransitions.Exit)
        rule.runOnIdle { ready = true }
        rule.mainClock.advanceTimeBy(180)
        rule.runOnIdle { open = false }
        rule.mainClock.advanceTimeByFrame()
        rule.mainClock.advanceTimeBy(32)
        rule.runOnIdle { open = true }
        rule.mainClock.advanceTimeByFrame()
        rule.mainClock.advanceTimeBy(128)
        val bounds = rule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow
        val pixels = rule.onNodeWithTag("backing").captureToImage().toPixelMap()
        assertEquals(0f, pixels[bounds.center.x.toInt(), bounds.center.y.toInt()].red, .01f)
    }

    @Test fun defaultEntryPaintsTheOuterShadowInTheSameFrameAsThePanel() {
        show()
        rule.runOnIdle { ready = true }
        rule.mainClock.advanceTimeByFrame()
        rule.mainClock.advanceTimeBy(48)
        val bounds = rule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow
        val pixels = rule.onNodeWithTag("backing").captureToImage().toPixelMap()
        val y = bounds.center.y.toInt()
        val panel = pixels[bounds.center.x.toInt(), y]
        val shadow = pixels[bounds.right.toInt() + 3, y]
        assertTrue(panel.red in 0.02f..0.98f, "Expected a partially visible menu, observed $panel")
        assertTrue(shadow.green < 0.98f, "The menu appeared without its outer shadow, observed $shadow")
    }

    @Test fun closingBeforeReadyDoesNotWaitForAnInvisibleExit() {
        show()
        rule.runOnIdle { open = false }
        rule.mainClock.advanceTimeByFrame()
        rule.mainClock.advanceTimeByFrame()
        rule.onNodeWithTag("panel").assertDoesNotExist()
        rule.runOnIdle { ready = true }
        rule.mainClock.advanceTimeBy(500)
        rule.onNodeWithTag("panel").assertDoesNotExist()
    }
}
