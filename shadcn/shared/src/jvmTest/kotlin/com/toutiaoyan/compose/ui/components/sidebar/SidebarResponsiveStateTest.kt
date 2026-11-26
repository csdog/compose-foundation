@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.sidebar

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.*
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.IntSize
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class SidebarResponsiveStateTest : ScreenshotTest() {
    @Test fun automaticBreakpointAndEachModesOpenStateSurviveSwitching() {
        var width by mutableIntStateOf(767)
        lateinit var state: SidebarState
        lateinit var contentIdentity: Any
        setThemedContent {
            val original = LocalWindowInfo.current
            val window =
                remember(original) {
                    object : WindowInfo by original {
                        override val containerSize: IntSize get() = IntSize(width, 768)
                    }
                }
            CompositionLocalProvider(LocalWindowInfo provides window) {
                SidebarProvider {
                    val current = sidebarState()
                    val identity = remember { Any() }
                    SideEffect {
                        state = current
                        contentIdentity = identity
                    }
                }
            }
        }
        val initialContentIdentity = composeRule.runOnIdle { contentIdentity }
        composeRule.runOnIdle {
            assertTrue(state.isMobile)
            assertFalse(state.openMobile)
            width = 768
        }
        composeRule.runOnIdle {
            assertFalse(state.isMobile)
            state.toggle()
        }
        composeRule.runOnIdle {
            assertFalse(state.open)
            width = 390
        }
        composeRule.runOnIdle {
            assertTrue(state.isMobile)
            state.toggle()
        }
        composeRule.runOnIdle {
            assertTrue(state.openMobile)
            assertFalse(state.open)
            width = 1440
        }
        composeRule.runOnIdle {
            assertFalse(state.isMobile)
            assertFalse(state.open)
            width = 767
        }
        composeRule.runOnIdle {
            assertTrue(state.isMobile)
            assertTrue(state.openMobile)
            assertSame(initialContentIdentity, contentIdentity)
        }
    }

    @Test fun mobileTriggerSharesTouchAndKeyboardFocusWithItsDrawer() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            SidebarProvider(mobile = true) {
                Sidebar(Modifier.testTag("drawer")) {
                    Input(value = "Profile", onValueChange = {}, modifier = Modifier.testTag("drawer-input"))
                }
                SidebarInset { SidebarTrigger(Modifier.testTag("drawer-trigger")) }
            }
        }

        val trigger = composeRule.onNodeWithTag("drawer-trigger")
        val drawer = composeRule.onNodeWithTag("drawer", useUnmergedTree = true)
        val input = composeRule.onNodeWithTag("drawer-input")
        trigger.performTouchInput { click() }
        input.assertIsDisplayed().assertIsNotFocused()
        drawer.performKeyInput { pressKey(Key.Tab) }
        input.assertIsFocused().performKeyInput { pressKey(Key.Escape) }
        drawer.assertDoesNotExist()

        trigger.performSemanticsAction(SemanticsActions.RequestFocus) { it() }
        trigger.assertIsFocused().performKeyInput { pressKey(Key.Enter) }
        input.assertIsFocused().performKeyInput { pressKey(Key.Escape) }
        drawer.assertDoesNotExist()
        trigger.assertIsFocused()
    }

    @Test fun explicitMobileOverrideWinsOverWindowSize() {
        var mode by mutableStateOf<Boolean?>(false)
        lateinit var state: SidebarState
        setThemedContent {
            val original = LocalWindowInfo.current
            val window =
                remember(original) {
                    object : WindowInfo by original {
                        override val containerSize = IntSize(390, 768)
                    }
                }
            CompositionLocalProvider(LocalWindowInfo provides window) {
                SidebarProvider(mobile = mode) {
                    val current = sidebarState()
                    SideEffect { state = current }
                }
            }
        }
        composeRule.runOnIdle {
            assertFalse(state.mobile)
            mode = true
        }
        composeRule.runOnIdle {
            assertTrue(state.mobile)
            mode = null
        }
        composeRule.runOnIdle { assertTrue(state.mobile) }
    }

    @Test fun mobileChangesUseLatestCallbackAndNeverDesktopPersistence() {
        var revision by mutableIntStateOf(0)
        val events = mutableListOf<String>()
        lateinit var state: SidebarState
        setThemedContent {
            val captured = revision
            SidebarProvider(
                mobile = true,
                openMobile = false,
                onOpenMobileChange = { events += "mobile:$captured:$it" },
                onOpenChange = { events += "desktop:$captured:$it" },
                onPersistOpen = { events += "persist:$captured:$it" },
            ) {
                val current = sidebarState()
                SideEffect { state = current }
            }
        }
        composeRule.runOnIdle { revision = 1 }
        composeRule.runOnIdle { state.setOpenMobile(true) }
        composeRule.runOnIdle {
            assertEquals(listOf("mobile:1:true"), events)
            assertFalse(state.openMobile)
            assertTrue(state.open)
        }
    }

    @Test fun desktopAndMobileSettersAreIndependent() {
        val persisted = mutableListOf<Boolean>()
        lateinit var state: SidebarState
        setThemedContent {
            SidebarProvider(mobile = true, onPersistOpen = { persisted += it }) {
                val current = sidebarState()
                SideEffect { state = current }
            }
        }
        composeRule.runOnIdle {
            state.setOpen(false)
            state.setOpenMobile(true)
        }
        composeRule.runOnIdle {
            assertFalse(state.open)
            assertTrue(state.openMobile)
            assertEquals(
                listOf(false),
                persisted,
            )
        }
    }

    @Test fun twoUncontrolledMobileTogglesInOneFrameCancelEachOther() {
        lateinit var state: SidebarState
        setThemedContent {
            SidebarProvider(mobile = true) {
                val current = sidebarState()
                SideEffect { state = current }
            }
        }
        composeRule.runOnIdle {
            state.toggle()
            state.toggle()
        }
        composeRule.runOnIdle {
            assertFalse(state.openMobile)
            assertTrue(state.open)
        }
    }

    @Test fun previouslyStoredToggleUsesTheLatestResponsiveMode() {
        var mobile by mutableStateOf(false)
        lateinit var state: SidebarState
        setThemedContent {
            SidebarProvider(mobile = mobile) {
                val current = sidebarState()
                SideEffect { state = current }
            }
        }
        lateinit var toggle: () -> Unit
        composeRule.runOnIdle {
            toggle = state.toggle
            mobile = true
        }
        composeRule.runOnIdle { toggle() }
        composeRule.runOnIdle {
            assertTrue(state.openMobile)
            assertTrue(state.open)
        }
    }

    private fun shortcut(enabled: Boolean) {
        lateinit var state: SidebarState
        setThemedContent {
            SidebarProvider(mobile = true, shortcutEnabled = enabled) {
                val current = sidebarState()
                SideEffect { state = current }
                Button("Focus", modifier = Modifier.testTag("focus"))
            }
        }
        val focus = composeRule.onNodeWithTag("focus")
        focus.performSemanticsAction(SemanticsActions.RequestFocus) { it() }
        focus.performKeyInput {
            keyDown(Key.CtrlLeft)
            pressKey(Key.B)
            keyUp(Key.CtrlLeft)
        }
        composeRule.runOnIdle {
            assertEquals(enabled, state.openMobile)
            assertTrue(state.open)
        }
    }

    @Test fun ctrlBInsideProviderTogglesOnlyMobile() = shortcut(true)

    @Test fun disabledShortcutLeavesBothStatesUnchanged() = shortcut(false)
}
