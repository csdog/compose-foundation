@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.navigationmenu

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class NavigationMenuParityTest : ScreenshotTest() {
    private fun advance(ms: Long) {
        composeRule.mainClock.advanceTimeBy(ms)
        composeRule.waitForIdle()
    }

    private fun hover(tag: String) {
        composeRule.onNodeWithTag(tag).performMouseInput {
            enter(center, delayMillis = 0)
            moveTo(center, delayMillis = 0)
        }
        advance(16)
    }

    @Composable
    private fun NavigationMenuListScope.first(enabled: Boolean = true) {
        NavigationMenuItem("first") {
            NavigationMenuTrigger("Getting started", modifier = Modifier.testTag("first"), enabled = enabled)
            NavigationMenuContent(enter = EnterTransition.None, exit = ExitTransition.None) {
                Text { "First panel" }
            }
        }
    }

    @Test fun novaTriggerHasSourceHeightAndDecorativeChevronSpace() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            NavigationMenu { NavigationMenuList { first() } }
        }
        composeRule.onNodeWithTag("first").assertHeightIsEqualTo(36.dp)
        val trigger = composeRule.onNodeWithTag("first").getUnclippedBoundsInRoot()
        val label = composeRule.onNodeWithText("Getting started", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertEquals(36f, ((trigger.right - trigger.left) - (label.right - label.left)).value, 0.5f)
    }

    @Test fun novaListDoesNotInsertGapsBetweenItems() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            NavigationMenu {
                NavigationMenuList {
                    NavigationMenuItem("one") { NavigationMenuLink("One", {}, Modifier.testTag("one")) }
                    NavigationMenuItem("two") { NavigationMenuLink("Two", {}, Modifier.testTag("two")) }
                }
            }
        }
        val one = composeRule.onNodeWithTag("one").getUnclippedBoundsInRoot()
        val two = composeRule.onNodeWithTag("two").getUnclippedBoundsInRoot()
        assertEquals(one.right.value, two.left.value, 0.5f)
    }

    @Test fun novaOpenTriggerUsesHalfMutedAndHoverUsesMuted() {
        var muted = Color.Unspecified
        var background = Color.Unspecified
        setThemedContent(stylePreset = StylePreset.Nova) {
            muted = currentTheme.colors.muted
            background = currentTheme.colors.background
            NavigationMenu(defaultValue = "first") { NavigationMenuList { first() } }
        }

        fun pixel() = composeRule.onNodeWithTag("first").captureToImage().toPixelMap()[6, 18]
        val expected =
            Color(
                red = muted.red * 0.5f + background.red * 0.5f,
                green = muted.green * 0.5f + background.green * 0.5f,
                blue = muted.blue * 0.5f + background.blue * 0.5f,
            )
        assertEquals(expected.red, pixel().red, 0.01f)
        hover("first")
        assertEquals(muted.red, pixel().red, 0.01f)
    }

    @Test fun pendingHoverOpenUsesLatestControlledCallback() {
        var revision by mutableStateOf("old")
        val changes = mutableListOf<String>()
        composeRule.mainClock.autoAdvance = false
        setThemedContent(stylePreset = StylePreset.Nova) {
            val captured = revision
            NavigationMenu(value = null, onValueChange = { changes.add("$captured:$it") }, delayDuration = 200) {
                NavigationMenuList { first() }
            }
        }
        hover("first")
        advance(60)
        composeRule.runOnIdle { revision = "new" }
        advance(200)
        assertEquals(listOf("new:first"), changes)
    }

    @Test fun disablingTriggerCancelsPendingHoverOpen() {
        var enabled by mutableStateOf(true)
        val changes = mutableListOf<String?>()
        composeRule.mainClock.autoAdvance = false
        setThemedContent(stylePreset = StylePreset.Nova) {
            NavigationMenu(value = null, onValueChange = { changes.add(it) }, delayDuration = 200) {
                NavigationMenuList { first(enabled) }
            }
        }
        hover("first")
        advance(60)
        composeRule.runOnIdle { enabled = false }
        advance(250)
        composeRule.onNodeWithTag("first").assertIsNotEnabled()
        assertTrue(changes.isEmpty(), changes.toString())
    }

    @Test fun anOldLeaveTimerCannotCloseANewerControlledItem() {
        var value by mutableStateOf<String?>(null)
        composeRule.mainClock.autoAdvance = false
        setThemedContent(stylePreset = StylePreset.Nova) {
            NavigationMenu(value = value, onValueChange = { value = it }, delayDuration = 0) {
                NavigationMenuList { first() }
            }
        }
        hover("first")
        advance(64)
        assertEquals("first", value)
        composeRule.onNodeWithTag("first").performMouseInput { moveTo(Offset(-20f, -20f), delayMillis = 0) }
        advance(16)
        composeRule.runOnIdle { value = "second" }
        advance(200)
        assertEquals("second", value)
    }

    @Test fun selectingAPanelLinkClosesMenuAndRunsItsActionOnce() {
        var clicks = 0
        composeRule.mainClock.autoAdvance = false
        setThemedContent(stylePreset = StylePreset.Nova) {
            NavigationMenu(defaultValue = "first") {
                NavigationMenuList {
                    NavigationMenuItem("first") {
                        NavigationMenuTrigger("Getting started", Modifier.testTag("first"))
                        NavigationMenuContent(enter = EnterTransition.None, exit = ExitTransition.None) {
                            NavigationMenuLink("Introduction", onClick = { clicks++ })
                        }
                    }
                }
            }
        }
        advance(64)
        composeRule.onNodeWithTag("first").performMouseInput { moveTo(Offset(-20f, -20f), delayMillis = 0) }
        advance(16)
        composeRule.onNodeWithText("Introduction").performClick()
        advance(64)
        composeRule.onNodeWithText("Introduction").assertDoesNotExist()
        assertEquals(1, clicks)
    }

    @Test fun touchActivationTogglesAndDisabledTriggerDoesNotOpen() {
        var open by mutableStateOf<String?>(null)
        val changes = mutableListOf<String?>()
        composeRule.mainClock.autoAdvance = false
        setThemedContent(stylePreset = StylePreset.Nova) {
            NavigationMenu(value = open, onValueChange = {
                open = it
                changes.add(it)
            }) {
                NavigationMenuList {
                    first()
                    NavigationMenuItem("disabled") {
                        NavigationMenuTrigger("Disabled", enabled = false)
                    }
                }
            }
        }
        advance(64)
        composeRule.onNodeWithTag("first").performMouseInput { moveTo(Offset(-20f, -20f), delayMillis = 0) }
        advance(16)
        composeRule.onNodeWithTag("first").performTouchInput { click() }
        advance(64)
        composeRule.onNodeWithText("First panel").assertIsDisplayed()
        composeRule.onNodeWithTag("first").performTouchInput { click() }
        advance(64)
        assertNull(open, "touch transitions=$changes")
        composeRule.onNodeWithText("First panel").assertDoesNotExist()
        composeRule.onNodeWithText("Disabled").assertIsNotEnabled()
    }

    @Test fun baseDefaultsWaitFiftyMillisecondsForOpenAndCloseWithoutStealingHover() {
        var value by mutableStateOf<String?>(null)
        val changes = mutableListOf<String?>()
        composeRule.mainClock.autoAdvance = false
        setThemedContent(stylePreset = StylePreset.Nova) {
            NavigationMenu(value = value, onValueChange = {
                value = it
                changes.add(it)
            }) {
                NavigationMenuList { first() }
            }
        }
        hover("first")
        advance(16)
        assertNull(value)
        advance(64)
        assertEquals("first", value)
        advance(1000)
        assertEquals(listOf<String?>("first"), changes)
        composeRule.onNodeWithTag("first").performMouseInput { moveTo(Offset(-20f, -20f), delayMillis = 0) }
        advance(16)
        assertEquals("first", value)
        advance(128)
        assertNull(value)
    }

    @Test fun triggerAccessibleNameComesFromTheVisibleLabelRatherThanTheInternalId() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            NavigationMenu { NavigationMenuList { first() } }
        }
        composeRule.onNodeWithTag("first").assertTextEquals("Getting started")
        composeRule.onNodeWithContentDescription("first").assertDoesNotExist()
    }

    @Test fun keyboardEnterOpensAndEscapeClosesTheKeyboardPopup() {
        composeRule.mainClock.autoAdvance = false
        setThemedContent(stylePreset = StylePreset.Nova) {
            NavigationMenu { NavigationMenuList { first() } }
        }
        advance(64)
        composeRule.onNodeWithTag("first").performMouseInput { moveTo(Offset(-20f, -20f), delayMillis = 0) }
        composeRule.onNodeWithTag("first").performSemanticsAction(SemanticsActions.RequestFocus) { it() }
        composeRule.onNodeWithTag("first").performKeyInput { pressKey(Key.Enter) }
        advance(64)
        composeRule.onNodeWithText("First panel").assertIsDisplayed()
        composeRule.onNodeWithText("First panel").performKeyInput { pressKey(Key.Escape) }
        advance(64)
        composeRule.onNodeWithText("First panel").assertDoesNotExist()
        composeRule.onNodeWithTag("first").assertIsFocused()
    }

    @Test fun callerStyleOverridesTriggerDimensionsAndNovaPanelHasIntrinsicWidth() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            NavigationMenu(defaultValue = "first") {
                NavigationMenuList {
                    NavigationMenuItem("first") {
                        NavigationMenuTrigger(
                            "Getting started",
                            Modifier.testTag("first"),
                            style =
                                Style {
                                    width(200.dp)
                                    height(44.dp)
                                },
                        )
                        NavigationMenuContent(
                            Modifier.testTag("panel"),
                            enter =
                                EnterTransition.None,
                            exit =
                                ExitTransition.None,
                        ) {
                            Box(Modifier.width(176.dp).height(32.dp))
                        }
                    }
                }
            }
        }
        composeRule.onNodeWithTag("first").assertWidthIsEqualTo(200.dp).assertHeightIsEqualTo(44.dp)
        composeRule.onNodeWithTag("panel").assertWidthIsEqualTo(184.dp).assertHeightIsEqualTo(40.dp)
    }

    @Test fun linkCanRetainTheOpenPanelWhenCloseOnClickIsFalse() {
        var clicks = 0
        composeRule.mainClock.autoAdvance = false
        setThemedContent(stylePreset = StylePreset.Nova) {
            NavigationMenu(defaultValue = "first") {
                NavigationMenuList {
                    NavigationMenuItem("first") {
                        NavigationMenuTrigger("Getting started")
                        NavigationMenuContent(enter = EnterTransition.None, exit = ExitTransition.None) {
                            NavigationMenuLink("Keep open", onClick = { clicks++ }, closeOnClick = false)
                        }
                    }
                }
            }
        }
        advance(64)
        composeRule.onNodeWithText("Keep open").performClick()
        advance(64)
        composeRule.onNodeWithText("Keep open").assertIsDisplayed()
        assertEquals(1, clicks)
    }

    @Test fun hoveringAPanelLinkKeepsItOpenUntilSelection() {
        var clicks = 0
        var open by mutableStateOf<String?>("first")
        val requests = mutableListOf<String?>()
        composeRule.mainClock.autoAdvance = false
        setThemedContent(stylePreset = StylePreset.Nova) {
            NavigationMenu(value = open, onValueChange = {
                open = it
                requests.add(it)
            }) {
                NavigationMenuList {
                    NavigationMenuItem("first") {
                        NavigationMenuTrigger("Getting started", Modifier.testTag("first"))
                        NavigationMenuContent(enter = EnterTransition.None, exit = ExitTransition.None) {
                            NavigationMenuLink("Introduction", onClick = { clicks++ })
                        }
                    }
                }
            }
        }
        advance(64)
        composeRule.onNodeWithText("Introduction").performMouseInput {
            enter(center, delayMillis = 0)
            moveTo(center, delayMillis = 0)
        }
        advance(128)
        composeRule.onNodeWithText("Introduction").assertIsDisplayed()
        assertTrue(null !in requests, "hover requests=$requests")
        assertEquals(0, clicks)
        composeRule.onNodeWithText("Introduction").performMouseInput { click() }
        advance(64)
        composeRule.onNodeWithText("Introduction").assertDoesNotExist()
        assertEquals(1, clicks)
    }

    @Test fun hoveringATopLevelLinkStillClosesTheOpenPanel() {
        var open by mutableStateOf<String?>("first")
        var visits = 0
        composeRule.mainClock.autoAdvance = false
        setThemedContent(stylePreset = StylePreset.Nova) {
            NavigationMenu(value = open, onValueChange = { open = it }) {
                NavigationMenuList {
                    first()
                    NavigationMenuItem("docs") { NavigationMenuLink("Docs", onClick = { visits++ }) }
                }
            }
        }
        advance(64)
        composeRule.onNodeWithText("Docs").performMouseInput {
            enter(center, delayMillis = 0)
            moveTo(center, delayMillis = 0)
        }
        advance(128)
        assertNull(open)
        composeRule.onNodeWithText("First panel").assertDoesNotExist()
        composeRule.onNodeWithText("Docs").performClick()
        advance(64)
        assertEquals(1, visits)
    }
}
