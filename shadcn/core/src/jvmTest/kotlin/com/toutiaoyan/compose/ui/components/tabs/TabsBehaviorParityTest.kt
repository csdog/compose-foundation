@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.LayoutDirection
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class TabsBehaviorParityTest : ScreenshotTest() {
    private fun tab(value: String) = composeRule.onNodeWithTag(value)

    @Test fun defaultKeyboardActivationSelectsOnArrow() {
        // Base UI / Radix `Tabs.Root` default activationMode is "automatic":
        // arrow keys both move focus and select the panel.
        setThemedContent(stylePreset = StylePreset.Nova) {
            Tabs(defaultValue = "a") {
                TabsList {
                    TabsTrigger("a", "Alpha", Modifier.testTag("a"))
                    TabsTrigger("b", "Beta", Modifier.testTag("b"))
                }
            }
        }
        tab("a").requestFocus()
        tab("a").performKeyInput { pressKey(Key.DirectionRight) }
        tab("b").assertIsFocused().assertIsSelected()
    }

    @Test fun rtlArrowDirectionIsReversed() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Tabs(defaultValue = "a", activationMode = TabsActivationMode.Automatic) {
                    TabsList {
                        TabsTrigger("a", "Alpha", Modifier.testTag("a"))
                        TabsTrigger("b", "Beta", Modifier.testTag("b"))
                        TabsTrigger("c", "Gamma", Modifier.testTag("c"))
                    }
                }
            }
        }
        tab("a").requestFocus()
        tab("a").performKeyInput { pressKey(Key.DirectionLeft) }
        tab("b").assertIsFocused().assertIsSelected()
    }

    @Test fun tabExitsTheListThroughOneRovingEntry() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Column {
                Tabs(defaultValue = "a") {
                    TabsList {
                        TabsTrigger("a", "Alpha", Modifier.testTag("a"))
                        TabsTrigger("b", "Beta", Modifier.testTag("b"))
                    }
                }
                Button("After", onClick = {}, modifier = Modifier.testTag("after"))
            }
        }
        tab("a").requestFocus()
        tab("a").performKeyInput { pressKey(Key.Tab) }
        tab("after").assertIsFocused()
    }

    @Test fun uncontrolledRootInitiallySelectsFirstEnabledTab() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Tabs {
                TabsList {
                    TabsTrigger("disabled", "Disabled", disabled = true)
                    TabsTrigger("a", "Alpha", Modifier.testTag("a"))
                    TabsTrigger("b", "Beta", Modifier.testTag("b"))
                }
            }
        }
        tab("a").assertIsSelected()
    }

    @Test fun missingOrDisabledUncontrolledSelectionFallsBack() {
        var disable by mutableStateOf(false)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Tabs(defaultValue = "a") {
                TabsList {
                    TabsTrigger("a", "Alpha", Modifier.testTag("a"), disabled = disable)
                    TabsTrigger("b", "Beta", Modifier.testTag("b"))
                }
            }
        }
        composeRule.runOnIdle { disable = true }
        tab("b").assertIsSelected()
    }

    @Test fun controlledRefusalKeepsSelectionWhileFocusMoves() {
        var requested = ""
        setThemedContent(stylePreset = StylePreset.Nova) {
            Tabs(value = "a", onValueChange = { requested = it }, activationMode = TabsActivationMode.Automatic) {
                TabsList {
                    TabsTrigger("a", "Alpha", Modifier.testTag("a"))
                    TabsTrigger("b", "Beta", Modifier.testTag("b"))
                }
            }
        }
        tab("a").requestFocus()
        tab("a").performKeyInput { pressKey(Key.DirectionRight) }
        tab("b").assertIsFocused().assertIsNotSelected()
        tab("a").assertIsSelected()
        assertEquals("b", requested)
    }

    @Test fun switchingPanelsRemovesOldContentWithoutAnImplicitFade() {
        composeRule.mainClock.autoAdvance = false
        setThemedContent(stylePreset = StylePreset.Nova) {
            Tabs(defaultValue = "a") {
                TabsList {
                    TabsTrigger("a", "Alpha", Modifier.testTag("a"))
                    TabsTrigger("b", "Beta", Modifier.testTag("b"))
                }
                TabsContent("a") { Text { "Old panel" } }
                TabsContent("b") { Text { "New panel" } }
            }
        }
        tab("b").performClick()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Old panel").assertDoesNotExist()
        composeRule.onNodeWithText("New panel").assertIsDisplayed()
    }

    @Test fun disabledTabIsSkippedAndManualActivationIsSeparate() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Tabs(defaultValue = "a", activationMode = TabsActivationMode.Manual) {
                TabsList {
                    TabsTrigger("a", "Alpha", Modifier.testTag("a"))
                    TabsTrigger("b", "Beta", Modifier.testTag("b"), disabled = true)
                    TabsTrigger("c", "Gamma", Modifier.testTag("c"))
                }
            }
        }
        tab("a").requestFocus()
        tab("a").performKeyInput { pressKey(Key.DirectionRight) }
        tab("c").assertIsFocused().assertIsNotSelected()
        tab("c").performKeyInput { pressKey(Key.Spacebar) }
        tab("c").assertIsSelected()
    }

    @Test fun keptPanelPreservesInputButTakesNoSpaceOrFocusWhenHidden() {
        val hiddenFocus = FocusRequester()
        setThemedContent(stylePreset = StylePreset.Nova) {
            Tabs(defaultValue = "a", modifier = Modifier.testTag("root")) {
                TabsList {
                    TabsTrigger("a", "Alpha", Modifier.testTag("a"))
                    TabsTrigger("b", "Beta", Modifier.testTag("b"))
                }
                TabsContent("a", keepMounted = true) {
                    var text by remember { mutableStateOf("") }
                    Input(
                        text,
                        {
                            text =
                                it
                        },
                        Modifier.requiredSize(
                            100.dp,
                            36.dp,
                        ).focusRequester(hiddenFocus).testTag("input"),
                    )
                }
                TabsContent("b", Modifier.height(20.dp)) { Text { "Other panel" } }
            }
        }
        tab("input").performTextInput("Ada")
        tab("b").performClick()
        tab("input").assertDoesNotExist()
        composeRule.onNodeWithTag("input", useUnmergedTree = true).assertIsNotDisplayed()
        tab("root").assertHeightIsEqualTo(60.dp)
        composeRule.runOnIdle {
            assertFalse(
                hiddenFocus.requestFocus(),
                "Hidden descendants must reject focus requests",
            )
        }
        tab("a").performClick()
        tab("input").assertTextEquals("Ada")
    }

    @Test fun savedSetterUsesLatestOwnerCallback() {
        var generation by mutableIntStateOf(0)
        var setter: ((String) -> Unit)? = null
        var received = -1
        setThemedContent(stylePreset = StylePreset.Nova) {
            val captured = generation
            Tabs(defaultValue = "a", onValueChange = { received = captured }) {
                val current = tabsState()
                SideEffect { if (setter == null) setter = current.onSelectedChange }
                TabsList {
                    TabsTrigger("a", "Alpha")
                    TabsTrigger("b", "Beta")
                }
            }
        }
        composeRule.runOnIdle { generation = 1 }
        composeRule.runOnIdle { setter!!("b") }
        assertEquals(1, received)
    }

    @Test fun keyedReorderUsesCurrentVisualOrderForArrows() {
        var order by mutableStateOf(listOf("a", "b", "c"))
        setThemedContent(stylePreset = StylePreset.Nova) {
            Tabs(defaultValue = "a", activationMode = TabsActivationMode.Automatic) {
                TabsList {
                    order.forEach { value ->
                        key(value) {
                            TabsTrigger(
                                value,
                                value,
                                Modifier.testTag(value),
                            )
                        }
                    }
                }
            }
        }
        tab("a").requestFocus()
        composeRule.runOnIdle { order = listOf("a", "c", "b") }
        tab("a").performKeyInput { pressKey(Key.DirectionRight) }
        tab("c").assertIsFocused().assertIsSelected()
    }

    @Test fun verticalHomeEndAndNonLoopingBoundaryWork() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Tabs(defaultValue = "a", orientation = TabsOrientation.Vertical, loop = false) {
                TabsList {
                    TabsTrigger("a", "Alpha", Modifier.testTag("a"))
                    TabsTrigger("b", "Beta", disabled = true)
                    TabsTrigger("c", "Gamma", Modifier.testTag("c"))
                }
            }
        }
        tab("a").requestFocus()
        tab("a").performKeyInput { pressKey(Key.MoveEnd) }
        tab("c").assertIsFocused().assertIsSelected()
        tab("c").performKeyInput { pressKey(Key.DirectionDown) }
        tab("c").assertIsFocused().assertIsSelected()
        tab("c").performKeyInput { pressKey(Key.Enter) }
        tab("c").assertIsSelected()
        tab("c").performKeyInput { pressKey(Key.MoveHome) }
        tab("a").assertIsFocused().assertIsSelected()
    }
}
