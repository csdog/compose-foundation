package com.toutiaoyan.compose.ui.components.dropdownmenu

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.overlay.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.*

@RunWith(Parameterized::class)
class DropdownMenuMotionTest(private val override: String) {
    @get:Rule val rule = createComposeRule()
    private var open by mutableStateOf(false)
    private var mounted = 0
    private var disposed = 0
    private val identities = mutableSetOf<Any>()

    private val enter: EnterTransition
        get() =
            when (override) {
                "none" -> EnterTransition.None
                "short" -> fadeIn(tween(20))
                "shared" -> PopupTransitions.Enter
                else -> scaleIn(tween(240), initialScale = .7f)
            }
    private val exit: ExitTransition
        get() =
            when (override) {
                "none" -> ExitTransition.None
                "short" -> fadeOut(tween(20))
                "shared" -> PopupTransitions.Exit
                else -> scaleOut(tween(240), targetScale = .7f)
            }

    private fun show(
        explicitEnter: Boolean,
        explicitExit: Boolean,
    ) {
        rule.mainClock.autoAdvance = false
        val callerEnter = if (explicitEnter) enter else DropdownDefaultEnter
        val callerExit = if (explicitExit) exit else DropdownDefaultExit
        rule.setContent {
            Box(Modifier.size(400.dp)) {
                DropdownMenuPopup(
                    open, {}, PopupPlacement.Bottom, 4.dp, PopupAlign.Start, 0.dp,
                    dismissOnClickOutside = true, focusable = false,
                    enter = callerEnter, exit = callerExit, owningNova = true,
                ) {
                    val identity = remember { Any() }
                    DisposableEffect(Unit) {
                        identities += identity
                        mounted++
                        onDispose { disposed++ }
                    }
                    Box(Modifier.size(100.dp, 60.dp).background(Color.Black).testTag("panel"))
                }
            }
        }
        rule.runOnIdle { open = true }
        rule.mainClock.advanceTimeByFrame()
        rule.waitForIdle()
    }

    private fun width() = rule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow.width

    @Test fun explicitExitOwnsTransformAndDisposalEvenWhenEnterIsInterrupted() {
        show(explicitEnter = false, explicitExit = true)
        rule.mainClock.advanceTimeBy(32)
        rule.onNodeWithTag("panel").assertExists()
        rule.runOnIdle { open = false }
        rule.mainClock.advanceTimeByFrame()
        rule.waitForIdle()
        if (override == "none") {
            rule.mainClock.advanceTimeByFrame()
            rule.onNodeWithTag("panel").assertDoesNotExist()
        } else {
            if (override != "custom") assertEquals(100f, width(), .1f, "No residual Nova scale on explicit exit")
            rule.mainClock.advanceTimeBy(if (override == "short") 48 else 32)
            if (override == "short") {
                rule.onNodeWithTag("panel").assertDoesNotExist()
            } else {
                rule.onNodeWithTag("panel").assertExists()
                rule.runOnIdle { open = true }
                rule.mainClock.advanceTimeByFrame()
                rule.mainClock.advanceTimeBy(1000)
                assertEquals(100f, width(), .1f)
                assertEquals(1, identities.size, "Reversal must preserve content identity")
                rule.runOnIdle { open = false }
                rule.mainClock.advanceTimeBy(1000)
            }
        }
        rule.onNodeWithTag("panel").assertDoesNotExist()
        rule.runOnIdle { assertEquals(mounted, disposed) }
    }

    @Test fun explicitEnterRemovesDefaultExitTransformOnReopen() {
        show(explicitEnter = true, explicitExit = false)
        rule.mainClock.advanceTimeBy(1000)
        assertEquals(100f, width(), .1f)
        rule.runOnIdle { open = false }
        rule.mainClock.advanceTimeByFrame()
        rule.mainClock.advanceTimeBy(32)
        assertTrue(width() < 100f, "Default exit must scale before disposal")
        rule.runOnIdle { open = true }
        rule.mainClock.advanceTimeByFrame()
        if (override != "custom") assertEquals(100f, width(), .1f, "Explicit fade/None entry has no Nova scale")
        rule.mainClock.advanceTimeBy(1000)
        assertEquals(100f, width(), .1f)
        assertEquals(1, identities.size)
        assertEquals(0, disposed)
        rule.runOnIdle { open = false }
        rule.mainClock.advanceTimeBy(1000)
        rule.onNodeWithTag("panel").assertDoesNotExist()
        assertEquals(1, disposed)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "override={0}")
        fun cases() = listOf("none", "short", "shared", "custom")
    }
}

class DropdownMenuInterruptedDismissalTest {
    @get:Rule val rule = createComposeRule()

    @Test fun closeDuringInitialEnterRetainsOneContentUntilExitCompletes() {
        var open by mutableStateOf(false)
        var mounts = 0
        var disposals = 0
        rule.mainClock.autoAdvance = false
        rule.setContent {
            Box(Modifier.size(400.dp)) {
                DropdownMenuPopup(
                    open, {}, PopupPlacement.Bottom, 4.dp, PopupAlign.Start, 0.dp,
                    true, false, DropdownDefaultEnter, DropdownDefaultExit, true,
                ) {
                    DisposableEffect(Unit) {
                        mounts++
                        onDispose { disposals++ }
                    }
                    Box(Modifier.size(100.dp, 60.dp).testTag("panel"))
                }
            }
        }
        rule.runOnIdle { open = true }
        rule.mainClock.advanceTimeByFrame()
        rule.mainClock.advanceTimeBy(32)
        rule.onNodeWithTag("panel").assertExists()
        rule.runOnIdle { open = false }
        rule.mainClock.advanceTimeByFrame()
        rule.onNodeWithTag("panel").assertExists()
        assertEquals(0, disposals, "Interrupted entry must retain the named exit")
        // The request frame composes the exit; the next frame starts its animation clock.
        rule.mainClock.advanceTimeByFrame()
        rule.mainClock.advanceTimeBy(96)
        rule.onNodeWithTag("panel").assertExists()
        rule.mainClock.advanceTimeByFrame() // First frame past the 100ms endpoint.
        rule.mainClock.advanceTimeByFrame() // Apply retention removal and disposal.
        rule.waitForIdle()
        rule.onNodeWithTag("panel").assertDoesNotExist()
        assertEquals(1, mounts)
        assertEquals(1, disposals)
    }
}

/** Both-default Nova effects restart; caller-owned direction tests above remain unchanged. */
@RunWith(Parameterized::class)
class DropdownMenuNovaRestartTest(private val child: Boolean) {
    @get:Rule val rule = createComposeRule()
    private var open by mutableStateOf(false)
    private var mounts = 0
    private var disposals = 0
    private val identities = mutableSetOf<Any>()

    @Composable
    private fun Target() {
        DropdownMenuPopup(
            open, {}, if (child) PopupPlacement.End else PopupPlacement.Bottom,
            4.dp, PopupAlign.Start, 0.dp,
            true, false, DropdownDefaultEnter, DropdownDefaultExit, true,
        ) {
            val identity = remember { Any() }
            DisposableEffect(Unit) {
                mounts++
                identities += identity
                onDispose { disposals++ }
            }
            Box(Modifier.size(100.dp, 60.dp).background(Color.Black).testTag("restart-panel"))
        }
    }

    private fun show() {
        rule.mainClock.autoAdvance = false
        rule.setContent {
            Box(Modifier.size(600.dp).background(Color.White).testTag("backing")) {
                Box(Modifier.size(24.dp)) {
                    if (child) {
                        DropdownMenuPopup(
                            true, {}, PopupPlacement.Bottom, 4.dp, PopupAlign.Start, 0.dp,
                            false, false, DropdownDefaultEnter, DropdownDefaultExit, true,
                        ) {
                            Box(Modifier.size(140.dp, 100.dp).background(Color.White)) { Target() }
                        }
                    } else {
                        Target()
                    }
                }
            }
        }
        rule.mainClock.advanceTimeBy(160)
        rule.runOnIdle { open = true }
    }

    private fun width() = rule.onNodeWithTag("restart-panel").fetchSemanticsNode().boundsInWindow.width

    private fun centerRed(): Float {
        val bounds = rule.onNodeWithTag("restart-panel").fetchSemanticsNode().boundsInWindow
        val pixels = rule.onNodeWithTag("backing").captureToImage().toPixelMap()
        return pixels[bounds.center.x.toInt(), bounds.center.y.toInt()].red
    }

    @Test fun earlyCloseRestartsAtFullVisibilityAndDisposesAfterOneNamedExit() {
        show()
        rule.mainClock.advanceTimeByFrame()
        rule.mainClock.advanceTimeBy(32)
        assertTrue(width() < 100f)
        rule.runOnIdle { open = false }
        rule.mainClock.advanceTimeByFrame() // Compose the requested exit, before animation playtime.
        rule.waitForIdle()
        assertEquals(100f, width(), .1f, "Exit starts at scale 1, not interrupted enter scale")
        assertEquals(0f, centerRed(), .01f, "Exit starts opaque")
        rule.mainClock.advanceTimeByFrame()
        rule.mainClock.advanceTimeBy(48)
        assertTrue(centerRed() in 0.1f..0.99f, "Exit has an intermediate composited frame")
        rule.mainClock.advanceTimeBy(48)
        rule.onNodeWithTag("restart-panel").assertExists() // 96ms is before the named endpoint.
        rule.mainClock.advanceTimeByFrame() // First frame past 100ms.
        rule.mainClock.advanceTimeByFrame() // Apply retention removal and disposal.
        rule.waitForIdle()
        rule.onNodeWithTag("restart-panel").assertDoesNotExist()
        assertEquals(1, mounts)
        assertEquals(1, disposals)
    }

    @Test fun retainedReopenRestartsEnterAndCancelsTheOldExitRemoval() {
        show()
        rule.mainClock.advanceTimeBy(160)
        rule.runOnIdle { open = false }
        rule.mainClock.advanceTimeByFrame()
        rule.mainClock.advanceTimeBy(32)
        rule.runOnIdle { open = true }
        rule.mainClock.advanceTimeByFrame() // Compose reentry before its first animation frame.
        rule.waitForIdle()
        assertEquals(95f, width(), .1f, "Reentry starts at declared scale .95")
        assertEquals(1f, centerRed(), .01f, "Reentry starts transparent over white")
        rule.mainClock.advanceTimeByFrame()
        rule.mainClock.advanceTimeBy(48)
        assertTrue(centerRed() in 0.01f..0.9f)
        rule.mainClock.advanceTimeBy(64)
        assertEquals(100f, width(), .1f)
        assertEquals(0f, centerRed(), .01f, "Named enter reaches opaque in 100ms of animation frames")
        rule.mainClock.advanceTimeBy(160)
        rule.onNodeWithTag("restart-panel").assertExists()
        assertEquals(1, mounts)
        assertEquals(1, identities.size)
        assertEquals(0, disposals, "Cancelled exit must not remove reopened content")
        rule.runOnIdle { open = false }
        rule.mainClock.advanceTimeBy(160)
        rule.onNodeWithTag("restart-panel").assertDoesNotExist()
        assertEquals(1, disposals)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "child={0}")
        fun cases() = listOf(false, true)
    }
}
