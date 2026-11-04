@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.toast

import androidx.compose.runtime.*
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
}
