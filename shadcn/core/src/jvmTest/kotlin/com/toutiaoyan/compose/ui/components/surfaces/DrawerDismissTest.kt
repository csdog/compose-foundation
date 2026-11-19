package com.toutiaoyan.compose.ui.components.surfaces

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.drawer.*
import com.toutiaoyan.compose.ui.components.dialog.Dialog
import com.toutiaoyan.compose.ui.components.dialog.DialogContent
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class DrawerDismissTest : ScreenshotTest() {
    @Test fun snapDrawerReceivesDragLeftOverAtTheContentBoundary() {
        val peek = DrawerSnapPoint.Absolute(300.dp)
        val full = DrawerSnapPoint.Fraction(1f)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Drawer(defaultOpen = true, snapPoints = listOf(peek, full), snapPoint = peek) {
                DrawerContent {
                    DrawerHeader("Scrollable snaps")
                    Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).testTag("snap scroll body")) {
                        Spacer(Modifier.fillMaxWidth().height(1200.dp))
                    }
                }
            }
        }
        composeRule.waitForIdle()
        val header = composeRule.onNodeWithText("Scrollable snaps")
        val start = header.getUnclippedBoundsInRoot().top
        val body = composeRule.onNodeWithTag("snap scroll body")
        body.performTouchInput {
            down(Offset(center.x, 16f))
            moveBy(Offset(0f, 42f))
        }
        assertTrue(header.getUnclippedBoundsInRoot().top > start)
        body.performTouchInput { up() }
        composeRule.waitForIdle()
        assertEquals(start, header.getUnclippedBoundsInRoot().top)
    }

    @Test fun closingBeforeEnterFinishesKeepsTheExitAnimationMounted() {
        var open by mutableStateOf(false)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Drawer(open = open, onOpenChange = { open = it }) {
                DrawerContent { DrawerHeader("Interrupted enter") }
            }
        }
        composeRule.waitForIdle()
        composeRule.mainClock.autoAdvance = false
        composeRule.runOnIdle { open = true }
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeBy(96)
        composeRule.onNodeWithText("Interrupted enter").assertExists()
        composeRule.runOnIdle { open = false }
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Interrupted enter").assertExists()
        composeRule.mainClock.advanceTimeBy(650)
        composeRule.onNodeWithText("Interrupted enter").assertDoesNotExist()
        composeRule.mainClock.autoAdvance = true
    }

    @Test fun ordinaryDialogDoesNotInheritDrawerExitHandoff() {
        var outerOpen by mutableStateOf(true)
        var nestedOpen by mutableStateOf(true)
        var dialogOpen by mutableStateOf(true)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Drawer(open = outerOpen, onOpenChange = { outerOpen = it }, swipeDirection = DrawerSwipeDirection.Right) {
                DrawerContent {
                    Drawer(
                        open = nestedOpen,
                        onOpenChange = { nestedOpen = it },
                        swipeDirection = DrawerSwipeDirection.Right,
                    ) {
                        DrawerContent {
                            Dialog(open = dialogOpen, onOpenChange = { dialogOpen = it }) { DialogContent("Dialog") {} }
                        }
                    }
                }
            }
        }
        composeRule.waitForIdle()
        composeRule.mainClock.autoAdvance = false
        repeat(2) {
            composeRule.onAllNodes(isRoot()).onLast().performTouchInput { click(Offset(20f, 30f)) }
            composeRule.waitForIdle()
        }
        assertFalse(dialogOpen)
        assertTrue(nestedOpen)
        assertTrue(outerOpen)
        composeRule.mainClock.autoAdvance = true
    }

    private fun exerciseRapidOutsideClicks(
        preventParent: Boolean = false,
        disableParent: Boolean = false,
    ) {
        val open = mutableStateListOf(true, true, true)
        val requests = mutableListOf<Int>()
        setThemedContent(stylePreset = StylePreset.Nova) {
            @Composable fun Level(index: Int) {
                Drawer(
                    open = open[index],
                    onOpenChange = {
                        if (!it) {
                            requests += index
                            open[index] = false
                        }
                    },
                    swipeDirection = DrawerSwipeDirection.Right,
                    disablePointerDismissal = disableParent && index == 1,
                ) {
                    DrawerContent(onPointerDownOutside = if (preventParent && index == 1) ({ true }) else null) {
                        DrawerHeader("Level $index")
                        if (index < 2) Level(index + 1)
                    }
                }
            }
            Level(0)
        }
        composeRule.waitForIdle()
        composeRule.mainClock.autoAdvance = false
        repeat(3) {
            // Hit the actual topmost Popup, including during its paused exit animation.
            composeRule.onAllNodes(isRoot()).onLast().performTouchInput { click(Offset(20f, 30f)) }
            composeRule.waitForIdle()
            if (it == 0) assertEquals(listOf(2), requests, "One click closes only one layer")
        }
        if (preventParent || disableParent) {
            assertEquals(listOf(2), requests)
            assertEquals(listOf(true, true, false), open.toList(), "A blocking parent must not be bypassed")
        } else {
            assertEquals(listOf(2, 1, 0), requests, "Repeated clicks must not wait for exit animations")
            assertEquals(listOf(false, false, false), open.toList())
        }
        composeRule.mainClock.autoAdvance = true
    }

    @Test fun outsideClicksCloseSuccessiveDrawersBeforeExitCompletes() = exerciseRapidOutsideClicks()

    @Test fun exitingChildHonorsParentOutsidePrevention() = exerciseRapidOutsideClicks(preventParent = true)

    @Test fun exitingChildHonorsParentDisablePointerDismissal() = exerciseRapidOutsideClicks(disableParent = true)
}
