@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.sidebar

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.text.Text
import kotlin.test.*

class SidebarStateParityTest : ScreenshotTest() {
    @Test fun mobileStartsClosedEvenWhenDesktopDefaultsOpen() {
        setThemedContent {
            SidebarProvider(mobile = true) {
                Sidebar { Text(Modifier.testTag("mobile-content")) { "Mobile navigation" } }
                SidebarInset { SidebarTrigger() }
            }
        }
        composeRule.onNodeWithTag("mobile-content").assertDoesNotExist()
    }

    @Test fun togglingMobileDoesNotChangeDesktopOpen() {
        lateinit var state: SidebarState
        setThemedContent {
            SidebarProvider(mobile = true) {
                val current = sidebarState()
                SideEffect { state = current }
            }
        }
        composeRule.runOnIdle { state.toggle() }
        composeRule.runOnIdle { assertTrue(state.open, "Desktop open must remain independent of the phone drawer") }
    }

    @Test fun controlledCallbackAndPersistenceUseLatestClosures() {
        var revision by mutableIntStateOf(0)
        val events = mutableListOf<String>()
        lateinit var state: SidebarState
        setThemedContent {
            val captured = revision
            SidebarProvider(
                open = true,
                mobile = false,
                onOpenChange = { events += "change:$captured:$it" },
                onPersistOpen = { events += "persist:$captured:$it" },
            ) {
                val current = sidebarState()
                SideEffect { state = current }
            }
        }
        composeRule.runOnIdle { revision = 1 }
        composeRule.runOnIdle { state.toggle() }
        composeRule.runOnIdle {
            assertEquals(listOf("change:1:false", "persist:1:false"), events)
            assertTrue(state.open, "Controlled owner may refuse the request")
        }
    }

    @Test fun offcanvasKeepsRememberedContentWhileClosed() {
        setThemedContent {
            SidebarProvider(mobile = false, modifier = Modifier.height(240.dp)) {
                val state = sidebarState()
                Sidebar {
                    var value by remember { mutableStateOf("Original") }
                    Input(value, { value = it }, Modifier.testTag("field"))
                }
                SidebarInset { Button("Toggle", onClick = state.toggle) }
            }
        }
        composeRule.onNodeWithTag("field").performTextReplacement("Ada")
        composeRule.onNodeWithText("Toggle").performClick()
        composeRule.onNodeWithTag("field").assertExists().assertIsNotDisplayed()
        composeRule.onNodeWithText("Toggle").performClick()
        composeRule.onNodeWithTag("field").assertTextContains("Ada")
    }

    @Test fun offcanvasReachesZeroAfterTwoHundredMilliseconds() {
        var open by mutableStateOf(true)
        composeRule.mainClock.autoAdvance = false
        setThemedContent {
            SidebarProvider(open = open, mobile = false, modifier = Modifier.height(240.dp)) {
                Sidebar(Modifier.testTag("rail")) { Text { "Navigation" } }
            }
        }
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.onNodeWithTag("rail").assertWidthIsEqualTo(256.dp)
        composeRule.runOnIdle { open = false }
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeBy(96)
        val bounds = composeRule.onNodeWithTag("rail").getUnclippedBoundsInRoot()
        val midpoint = (bounds.right - bounds.left).value
        assertTrue(midpoint in 96f..160f, "Expected linear half-width near128dp, actual=$midpoint")
        composeRule.mainClock.advanceTimeBy(160)
        composeRule.onNodeWithTag("rail").assertWidthIsEqualTo(0.dp)
    }

    @Test fun nonCollapsiblePanelDoesNotForceContextDesktopOpenToTrue() {
        lateinit var state: SidebarState
        setThemedContent {
            SidebarProvider(open = false, mobile = false, collapsible = SidebarCollapsible.None) {
                val current = sidebarState()
                SideEffect { state = current }
                Sidebar(Modifier.testTag("rail")) { Text { "Always visible" } }
            }
        }
        composeRule.runOnIdle { assertFalse(state.open) }
        composeRule.onNodeWithTag("rail").assertWidthIsEqualTo(256.dp)
    }
}
