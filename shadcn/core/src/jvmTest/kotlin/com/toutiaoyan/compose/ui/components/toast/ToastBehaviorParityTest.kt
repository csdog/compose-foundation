@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.toast

import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.*
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class ToastBehaviorParityTest : ScreenshotTest() {
    @Test fun managerUsesFiveSecondDefaultTimeout() {
        val state = ToastState()
        state.show("Default")
        assertEquals(5000L, state.toasts.single().durationMillis)
    }

    @Test fun zeroTimeoutIsPersistentAndVisible() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            val state = remember { ToastState().apply { show("Persistent", duration = 0) } }
            Toaster(state)
        }
        composeRule.mainClock.advanceTimeBy(1000)
        composeRule.onNodeWithText("Persistent").assertIsDisplayed()
    }

    @Test fun defaultRendererHasCloseToastControl() {
        val state = ToastState().apply { show("Closable", duration = 60000) }
        setThemedContent(stylePreset = StylePreset.Nova) { Toaster(state) }
        composeRule.onNodeWithContentDescription("Close toast").assertIsDisplayed().performClick()
        composeRule.runOnIdle { assertTrue(state.toasts.isEmpty()) }
    }

    @Test fun actionDoesNotImplicitlyCloseTheToast() {
        var clicked = 0
        val state =
            ToastState().apply {
                show("Action", duration = 60000, actionLabel = "Run", onAction = { clicked++ })
            }
        setThemedContent(stylePreset = StylePreset.Nova) { Toaster(state) }
        composeRule.onNodeWithText("Run").performClick()
        composeRule.runOnIdle {
            assertEquals(1, clicked)
            assertEquals(1, state.toasts.size)
        }
    }

    @Test fun explicitActionCanCloseItsOwnId() {
        val state = ToastState()
        var id = -1L
        id = state.show("Undoable", duration = 60000, actionLabel = "Undo", onAction = { state.dismiss(id) })
        setThemedContent(stylePreset = StylePreset.Nova) { Toaster(state) }
        composeRule.onNodeWithText("Undo").performClick()
        composeRule.runOnIdle { assertTrue(state.toasts.isEmpty()) }
    }

    @Test fun updatingAMessageRestartsItsTimeout() {
        composeRule.mainClock.autoAdvance = false
        val state = ToastState()
        val id = state.show("Original", duration = 1000)
        setThemedContent(stylePreset = StylePreset.Nova) { Toaster(state) }
        composeRule.mainClock.advanceTimeBy(800)
        composeRule.runOnIdle { state.update(id) { it.copy(title = "Updated") } }
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeBy(800)
        composeRule.onNodeWithText("Updated").assertIsDisplayed()
        composeRule.mainClock.advanceTimeBy(2000)
        composeRule.runOnIdle { assertTrue(state.toasts.isEmpty()) }
    }

    @Test fun limitedMessagesStillRunTheirTimeouts() {
        composeRule.mainClock.autoAdvance = false
        val state =
            ToastState().apply {
                show("Old", duration = 1000)
                show("New", duration = 0)
            }
        setThemedContent(stylePreset = StylePreset.Nova) { Toaster(state, visibleToasts = 1) }
        composeRule.mainClock.advanceTimeBy(2000)
        composeRule.runOnIdle { assertEquals(listOf("New"), state.toasts.map { it.title }) }
    }

    @Test fun replacingTheManagerDoesNotReuseAnOldIdsDeadline() {
        composeRule.mainClock.autoAdvance = false
        var state by mutableStateOf(ToastState().apply { show("A", duration = 1000) })
        setThemedContent(stylePreset = StylePreset.Nova) { Toaster(state) }
        composeRule.mainClock.advanceTimeBy(800)
        composeRule.runOnIdle { state = ToastState().apply { show("B", duration = 1000) } }
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeBy(400)
        composeRule.onNodeWithText("B").assertIsDisplayed()
        assertEquals(1, state.toasts.size)
    }

    @Test fun depthLimitsInteractionAndHoverPausesTheRemainingTimeout() {
        composeRule.mainClock.autoAdvance = false
        val state =
            ToastState().apply {
                show("Limited", duration = 0)
                show("Behind", duration = 0)
                show("Front", "Front description", duration = 4000)
            }
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.size(440.dp, 300.dp), contentAlignment = Alignment.BottomCenter) {
                Toaster(state, visibleToasts = 2)
            }
        }
        composeRule.mainClock.advanceTimeBy(1000)
        val viewport = composeRule.onNodeWithContentDescription("Notifications")
        composeRule.onNodeWithText("Front").assertIsDisplayed()
        composeRule.onNodeWithText("Behind").assertDoesNotExist()
        composeRule.onNodeWithText("Limited").assertDoesNotExist()

        viewport.performMouseInput { enter(center) }
        composeRule.mainClock.advanceTimeBy(5000)
        val front = composeRule.onNodeWithText("Front").assertIsDisplayed()
        val behind = composeRule.onNodeWithText("Behind").assertIsDisplayed()
        assertTrue(behind.fetchSemanticsNode().boundsInRoot.bottom < front.fetchSemanticsNode().boundsInRoot.top)
        composeRule.onNodeWithText("Limited").assertDoesNotExist()

        viewport.performMouseInput { exit() }
        composeRule.mainClock.advanceTimeBy(1000)
        composeRule.onNodeWithText("Behind").assertDoesNotExist()
        composeRule.runOnIdle { assertEquals(3, state.toasts.size) }
        composeRule.mainClock.advanceTimeBy(3200)
        composeRule.runOnIdle { assertEquals(listOf("Limited", "Behind"), state.toasts.map { it.title }) }
    }

    @Test fun leavingActionDoesNotBlockTheActiveAction() {
        composeRule.mainClock.autoAdvance = false
        val state = ToastState()
        var frontClicks = 0
        var behindClicks = 0
        state.show("Older", duration = 0, actionLabel = "Older action", onAction = { behindClicks++ })
        var frontId = -1L
        frontId =
            state.show("Newest", duration = 0, actionLabel = "Newer action", onAction = {
                frontClicks++
                state.dismiss(frontId)
            })
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.size(480.dp, 300.dp), contentAlignment = Alignment.BottomCenter) {
                Toaster(state)
            }
        }
        composeRule.mainClock.advanceTimeBy(550)
        composeRule.onNodeWithText("Newer action").performTouchInput { click(center) }
        composeRule.mainClock.advanceTimeBy(48)
        composeRule.onNodeWithText("Older action").performTouchInput { click(center) }
        composeRule.runOnIdle {
            assertEquals(1, frontClicks)
            assertEquals(1, behindClicks)
        }
    }

    @Test fun escapeDismissesTheFocusedCardAndMovesFocusToTheNextCard() {
        val state =
            ToastState().apply {
                show("Older", duration = 0)
                show("Newest", duration = 0, actionLabel = "Focus action")
            }
        setThemedContent(stylePreset = StylePreset.Nova) { Toaster(state) }
        composeRule.onNodeWithText("Focus action")
            .performSemanticsAction(SemanticsActions.RequestFocus) { assertTrue(it()) }
            .assertIsFocused()
        composeRule.onRoot().performKeyInput { pressKey(Key.Escape) }
        composeRule.runOnIdle { assertEquals(listOf("Older"), state.toasts.map { it.title }) }
        composeRule.onRoot().performKeyInput { pressKey(Key.Escape) }
        composeRule.runOnIdle { assertTrue(state.toasts.isEmpty()) }
    }

    @Test fun undoThatDismissesItsCardMovesKeyboardFocus() {
        val state = ToastState()
        state.show("Older", duration = 0)
        var id = -1L
        var actions = 0
        id =
            state.show("Undoable", duration = 0, actionLabel = "Undo", onAction = {
                actions++
                state.dismiss(id)
            })
        setThemedContent(stylePreset = StylePreset.Nova) { Toaster(state) }
        composeRule
            .onNodeWithText("Undo")
            .performSemanticsAction(SemanticsActions.RequestFocus) { assertTrue(it()) }
            .assertIsFocused()
        composeRule.onRoot().performKeyInput { pressKey(Key.Enter) }
        composeRule.runOnIdle {
            assertEquals(1, actions)
            assertEquals(listOf("Older"), state.toasts.map { it.title })
        }
        composeRule.onRoot().performKeyInput { pressKey(Key.Escape) }
        composeRule.runOnIdle { assertTrue(state.toasts.isEmpty()) }
    }

    @Test fun holdingAnActionBeforeSwipeSlopPausesTheTimeout() {
        composeRule.mainClock.autoAdvance = false
        var taps = 0
        val state =
            ToastState().apply {
                show("Held", duration = 2000, actionLabel = "Hold action", onAction = { taps++ })
            }
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.size(480.dp, 300.dp), contentAlignment = Alignment.BottomCenter) {
                Toaster(state)
            }
        }
        composeRule.mainClock.advanceTimeBy(750)
        val action = composeRule.onNodeWithText("Hold action")
        action.performTouchInput { down(center) }
        composeRule.mainClock.advanceTimeBy(2500)
        composeRule.runOnIdle { assertEquals(listOf("Held"), state.toasts.map { it.title }) }
        action.performTouchInput { up() }
        composeRule.runOnIdle { assertEquals(1, taps) }
        composeRule.mainClock.advanceTimeBy(2500)
        composeRule.runOnIdle { assertTrue(state.toasts.isEmpty()) }
    }

    @Test fun touchSwipeDismissesOnlyTheFrontMessage() {
        composeRule.mainClock.autoAdvance = false
        val state =
            ToastState().apply {
                show("First", duration = 0)
                show("Second", duration = 0)
                show("Newest", duration = 0)
            }
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.size(480.dp, 300.dp), contentAlignment = Alignment.BottomCenter) {
                Toaster(state)
            }
        }
        composeRule.mainClock.advanceTimeBy(550)
        composeRule.onNodeWithContentDescription("Notifications").performTouchInput {
            swipe(center, center + Offset(70f, 0f), durationMillis = 240)
        }
        composeRule.mainClock.advanceTimeBy(550)
        composeRule.runOnIdle {
            assertEquals(listOf("First", "Second"), state.toasts.map { it.title })
        }
        composeRule.onNodeWithText("Second").assertIsDisplayed()
        composeRule.onNodeWithText("Newest").assertDoesNotExist()
    }

    @Test fun customTransitionToastReturnsOnShortDragAndDismissesOnLongSwipe() {
        val state = ToastState().apply { show("Swipe me", duration = 0) }
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.size(480.dp, 300.dp), contentAlignment = Alignment.BottomCenter) {
                Toaster(state, enter = fadeIn())
            }
        }
        val card = composeRule.onNodeWithText("Swipe me")
        card.performTouchInput {
            down(center)
            moveBy(Offset(25f, 0f))
            up()
        }
        card.assertIsDisplayed()
        card.performTouchInput { swipe(center, center + Offset(70f, 0f), durationMillis = 240) }
        composeRule.mainClock.advanceTimeBy(550)
        composeRule.runOnIdle { assertTrue(state.toasts.isEmpty()) }
    }
}
