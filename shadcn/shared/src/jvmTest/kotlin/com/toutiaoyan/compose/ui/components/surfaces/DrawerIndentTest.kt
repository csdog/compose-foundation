package com.toutiaoyan.compose.ui.components.surfaces

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.drawer.*
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DrawerIndentTest : ScreenshotTest() {
    @Test fun registrationKeepsIndentUntilLastDrawerCloses() {
        val state = DrawerIndentState()
        val first = Any()
        val second = Any()
        state.register(first)
        state.register(second)
        state.unregister(first)
        assertTrue(state.active)
        state.unregister(second)
        assertFalse(state.active)
    }

    @Test fun optInScalesSurfaceAndDisposalRestoresIt() {
        var open by mutableStateOf(false)
        var mounted by mutableStateOf(true)
        setThemedContent {
            DrawerIndentHost(Modifier.size(400.dp)) {
                Box(Modifier.size(400.dp).testTag("surface"))
                if (mounted) Drawer(open = open, indentBackground = true) { }
            }
        }

        fun width() = composeRule.onNodeWithTag("surface").fetchSemanticsNode().boundsInWindow.width
        val original = width()
        composeRule.runOnIdle { open = true }
        composeRule.waitForIdle()
        assertTrue(width() < original - 10f)
        composeRule.runOnIdle { mounted = false }
        composeRule.waitForIdle()
        assertTrue(kotlin.math.abs(width() - original) < 1f)
    }

    @Test fun ordinaryDrawerDoesNotIndentSurface() {
        var open by mutableStateOf(false)
        setThemedContent {
            DrawerIndentHost(Modifier.size(400.dp)) {
                Box(Modifier.size(400.dp).testTag("surface"))
                Drawer(open = open) { }
            }
        }

        fun width() = composeRule.onNodeWithTag("surface").fetchSemanticsNode().boundsInWindow.width
        val original = width()
        composeRule.runOnIdle { open = true }
        composeRule.waitForIdle()
        assertTrue(kotlin.math.abs(width() - original) < 1f)
    }

    @Test fun multipleDrawersUseTheMostExpandedGesture() {
        val state = DrawerIndentState()
        val first = Any()
        val second = Any()
        var progress = 0.7f
        state.register(first) { progress }
        state.register(second) { 0.4f }
        assertEquals(0.7f, state.progress)
        progress = 0.2f
        assertEquals(0.4f, state.progress)
        state.unregister(second)
        assertEquals(0.2f, state.progress)
    }

    @Test fun pageTracksGestureWithoutAdvancingAnimationClock() {
        lateinit var state: DrawerIndentState
        var amount by mutableStateOf(1f)
        val key = Any()
        setThemedContent {
            DrawerIndentHost(Modifier.size(400.dp)) {
                state = LocalDrawerIndentState.current!!
                Box(Modifier.size(400.dp).testTag("surface"))
            }
        }

        fun width() = composeRule.onNodeWithTag("surface").fetchSemanticsNode().boundsInWindow.width
        val original = width()
        composeRule.runOnIdle { state.register(key) { amount } }
        composeRule.waitForIdle()
        val fullyIndented = width()
        composeRule.mainClock.autoAdvance = false
        try {
            for (fraction in listOf(0.75f, 0.25f, 0.6f, 1f, 0f, 1f)) {
                composeRule.runOnIdle { amount = fraction }
                composeRule.waitForIdle()
                val expected = original + (fullyIndented - original) * fraction
                assertEquals(expected, width(), 0.1f, "Gesture $fraction must render without an animation tick")
            }
        } finally {
            composeRule.mainClock.autoAdvance = true
        }
        composeRule.runOnIdle { state.unregister(key) }
        composeRule.waitForIdle()
        assertEquals(original, width(), 0.1f)
    }

    @Test fun draggingRestoresBackgroundBeforeReleaseAndReleaseSpringsBack() =
        assertDragFollows(DrawerSwipeDirection.Down, Offset(0f, 1f))

    @Test fun rightDrawerFollowsHeldDragAndSpringsBack() = assertDragFollows(DrawerSwipeDirection.Right, Offset(1f, 0f))

    @Test fun leftDrawerFollowsHeldDragAndSpringsBack() = assertDragFollows(DrawerSwipeDirection.Left, Offset(-1f, 0f))

    @Test fun topDrawerFollowsHeldDragAndSpringsBack() = assertDragFollows(DrawerSwipeDirection.Up, Offset(0f, -1f))

    @Test fun snapDrawerFollowsHeldDragBelowLowestSnapAndSpringsBack() =
        assertDragFollows(
            DrawerSwipeDirection.Down,
            Offset(0f, 1f),
            listOf(DrawerSnapPoint.Absolute(200.dp), DrawerSnapPoint.Fraction(1f)),
        )

    @Test fun dismissingDragRestoresPageDuringSettleAndAfterRemoval() {
        var open by mutableStateOf(true)
        setThemedContent(stylePreset = StylePreset.Nova) {
            DrawerIndentHost(Modifier.size(400.dp)) {
                Box(Modifier.size(400.dp).testTag("surface"))
                Drawer(open = open, onOpenChange = { open = it }, indentBackground = true) {
                    DrawerContent(Modifier.testTag("drag panel")) {
                        Box(Modifier.size(300.dp, 220.dp).testTag("panel content"))
                    }
                }
            }
        }

        fun width() = composeRule.onNodeWithTag("surface").fetchSemanticsNode().boundsInWindow.width
        val indented = width()
        composeRule.mainClock.autoAdvance = false
        try {
            composeRule.onNodeWithTag("drag panel").performTouchInput {
                down(Offset(center.x, 30f))
                moveBy(Offset(0f, 20f))
                moveBy(Offset(0f, 130f))
                advanceEventTime(200)
            }
            composeRule.waitForIdle()
            val held = width()
            assertTrue(held > indented + 5f, "The held dismissing drag must already restore the page")
            composeRule.onNodeWithTag("drag panel").performTouchInput { up() }
            composeRule.mainClock.advanceTimeByFrame()
            composeRule.mainClock.advanceTimeBy(64)
            val settling = width()
            assertTrue(settling >= held, "The page must continue restoring during dismissal: $held -> $settling")
            composeRule.mainClock.advanceTimeBy(1_000)
            composeRule.waitForIdle()
            assertFalse(open, "Dragging past the threshold must dismiss")
            assertEquals(400f, width(), 0.1f, "Removing the panel must leave the page fully restored")
        } finally {
            composeRule.mainClock.autoAdvance = true
        }
    }

    private fun assertDragFollows(
        direction: DrawerSwipeDirection,
        closingDirection: Offset,
        snaps: List<DrawerSnapPoint>? = null,
    ) {
        setThemedContent(stylePreset = StylePreset.Nova) {
            DrawerIndentHost(Modifier.size(400.dp)) {
                Box(Modifier.size(400.dp).testTag("surface"))
                Drawer(defaultOpen = true, indentBackground = true, swipeDirection = direction, snapPoints = snaps) {
                    DrawerContent(Modifier.testTag("drag panel")) {
                        Box(Modifier.size(300.dp, 220.dp).testTag("panel content"))
                    }
                }
            }
        }

        fun width() = composeRule.onNodeWithTag("surface").fetchSemanticsNode().boundsInWindow.width
        val indented = width()
        val beforePanel = composeRule.onNodeWithTag("drag panel").fetchSemanticsNode().boundsInWindow

        fun panelPosition(): Float {
            // The caller's modifier precedes the panel's offset modifier, so
            // measure its child to include the actual translated position.
            val bounds = composeRule.onNodeWithTag("panel content").getUnclippedBoundsInRoot()
            return if (closingDirection.x != 0f) bounds.left.value else bounds.top.value
        }
        composeRule.onNodeWithTag("drag panel").performTouchInput {
            down(if (snaps == null) center else Offset(center.x, height - 60f))
            moveBy(closingDirection * 20f)
            moveBy(closingDirection * 30f)
        }
        composeRule.waitForIdle()
        assertTrue(
            width() > indented + 0.25f,
            "Page must restore during the held gesture: $indented -> ${width()}, " +
                "panel $beforePanel -> ${composeRule.onNodeWithTag(
                    "drag panel",
                ).fetchSemanticsNode().boundsInWindow}",
        )
        val firstWidth = width()
        val firstPosition = panelPosition()
        // Stop frame-based animation: held pointer changes must still render
        // directly, rather than waiting for a second animation to catch up.
        composeRule.mainClock.autoAdvance = false
        try {
            composeRule.onNodeWithTag("drag panel").performTouchInput {
                moveBy(closingDirection * 25f)
            }
            composeRule.waitForIdle()
            val fartherWidth = width()
            val fartherPosition = panelPosition()
            assertTrue(
                fartherWidth > firstWidth + 0.25f,
                "A held drag must keep restoring the page without animation ticks",
            )
            composeRule.onNodeWithTag("drag panel").performTouchInput {
                moveBy(closingDirection * -25f)
            }
            composeRule.waitForIdle()
            assertEquals(firstPosition, panelPosition(), 1f, "Reversing the held gesture must return the panel")
            assertEquals(firstWidth, width(), 0.1f, "Reversing the held gesture must return the page immediately")
            assertTrue(
                kotlin.math.abs(fartherPosition - firstPosition) > 20f,
                "The panel must move with the held pointer: $firstPosition -> $fartherPosition",
            )
        } finally {
            composeRule.mainClock.autoAdvance = true
        }
        composeRule.onNodeWithTag("drag panel").performTouchInput {
            advanceEventTime(200)
            up()
        }
        composeRule.waitForIdle()
        assertTrue(kotlin.math.abs(width() - indented) < 1f, "Below-threshold release must settle back")
    }
}
