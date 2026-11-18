@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.select

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals

class SelectRetainedInputTest : ScreenshotTest() {
    private val choices = mutableStateListOf("Alpha", "Beta")
    private var label by mutableStateOf("Beta")
    private var suffix by mutableStateOf("old")
    private var blocked by mutableStateOf(false)
    private var picked: String? = null
    private var selectionAttempts = 0
    private var expanded by mutableStateOf(false)

    private fun show() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.padding(top = 200.dp)) {
                Select("Alpha", { picked = it + suffix }, open = expanded, onOpenChange = { expanded = it }) {
                    SelectTrigger(Modifier.testTag("trigger"))
                    SelectContent(Modifier.testTag("panel"), alignItemWithTrigger = false) {
                        choices.forEach { value ->
                            key(value) {
                                SelectItem(
                                    value,
                                    Modifier.testTag(value),
                                    textValue = if (value == "Beta") label else value,
                                    onSelect = {
                                        selectionAttempts++
                                        blocked
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
        composeRule.onNodeWithTag("trigger").requestFocus().performKeyInput { pressKey(Key.DirectionDown) }
        composeRule.onNodeWithTag("Alpha").assertIsFocused()
    }

    private fun whileClosing(
        closeWithEscape: Boolean = true,
        action: () -> Unit,
    ) {
        composeRule.mainClock.autoAdvance = false
        try {
            if (closeWithEscape) {
                composeRule.onNodeWithTag("Alpha").performKeyInput { pressKey(Key.Escape) }
            } else {
                composeRule.runOnIdle { expanded = false }
            }
            composeRule.mainClock.advanceTimeByFrame()
            composeRule.onNodeWithTag("panel").assertExists()
            composeRule.onNodeWithTag("trigger").assertIsFocused()
            action()
        } finally {
            composeRule.mainClock.autoAdvance = true
        }
    }

    @Test fun closedTypeaheadUsesLatestLabelAndCallbackDuringExit() {
        show()
        composeRule.runOnIdle {
            label = "Zulu"
            suffix = "new"
        }
        whileClosing {
            composeRule.onNodeWithTag("trigger").performKeyInput { pressKey(Key.Z) }
            composeRule.runOnIdle {
                assertEquals("Betanew", picked)
                assertEquals(1, selectionAttempts)
            }
        }
    }

    @Test fun closedTypeaheadCannotSelectAnOptionRemovedWhileOpen() {
        show()
        composeRule.runOnIdle {
            choices.remove("Beta")
            choices.add("Blueberry")
        }
        whileClosing {
            composeRule.onNodeWithTag("trigger").performKeyInput { pressKey(Key.B) }
            composeRule.runOnIdle { assertEquals("Blueberryold", picked) }
        }
    }

    @Test fun closedTypeaheadPreservesTheLatestPreventSelectionCallback() {
        show()
        composeRule.runOnIdle {
            label = "Zulu"
            blocked = true
        }
        whileClosing {
            composeRule.onNodeWithTag("trigger").performKeyInput { pressKey(Key.Z) }
            composeRule.runOnIdle {
                assertEquals(null, picked)
                assertEquals(1, selectionAttempts)
            }
        }
    }

    @Test fun emptyMountedListCannotFallBackToRemovedOptionsDuringExit() {
        show()
        composeRule.runOnIdle { choices.clear() }
        whileClosing(closeWithEscape = false) {
            composeRule.onNodeWithTag("trigger").performKeyInput { pressKey(Key.B) }
            composeRule.runOnIdle {
                assertEquals(null, picked)
                assertEquals(0, selectionAttempts)
            }
        }
    }

    @Test fun disablingControlledOpenSelectMakesTheRetainedOptionsInert() {
        var disabled by mutableStateOf(false)
        var closeRequests = 0
        lateinit var controller: SelectController
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.padding(top = 200.dp)) {
                Select(
                    "Alpha",
                    { picked = it },
                    open = true,
                    onOpenChange = { if (!it) closeRequests++ },
                    disabled = disabled,
                ) {
                    val currentController = selectState()
                    SideEffect { controller = currentController }
                    SelectTrigger(Modifier.testTag("trigger"))
                    SelectContent(
                        Modifier.testTag("panel"),
                        alignItemWithTrigger = false,
                        exit = fadeOut(tween(600)),
                    ) {
                        SelectItem("Alpha")
                        SelectItem(
                            "Beta",
                            Modifier.testTag("Beta"),
                            onSelect = {
                                selectionAttempts++
                                false
                            },
                        )
                    }
                }
            }
        }
        val option = composeRule.onNodeWithTag("Beta")
        option.assertIsEnabled()
        composeRule.mainClock.autoAdvance = false
        try {
            composeRule.runOnIdle { disabled = true }
            composeRule.mainClock.advanceTimeBy(64)
            composeRule.onNodeWithTag("panel").assertExists()
            option.assertExists()
            if (option.fetchSemanticsNode().config.getOrNull(SemanticsActions.OnClick) != null) {
                option.performClick()
            }
            composeRule.runOnIdle {
                assertEquals(null, picked)
                assertEquals(0, selectionAttempts)
                assertEquals(0, closeRequests)

                controller.select("Beta")
                assertEquals(null, picked)
                assertEquals(0, closeRequests)
            }
            option.assertIsNotEnabled()
            composeRule.mainClock.advanceTimeBy(700)
            composeRule.onNodeWithTag("panel").assertDoesNotExist()

            composeRule.runOnIdle { disabled = false }
            composeRule.mainClock.advanceTimeBy(240)
            option.assertIsEnabled().performClick()
            composeRule.runOnIdle {
                assertEquals("Beta", picked)
                assertEquals(1, selectionAttempts)
                assertEquals(1, closeRequests)
            }
        } finally {
            composeRule.mainClock.autoAdvance = true
        }
    }

    @Test fun nullableOptionReportsNullThroughTheRootCallback() {
        var selected by mutableStateOf<String?>("Alpha")
        var calls = 0
        setThemedContent(stylePreset = StylePreset.Nova) {
            Select<String?>(selected, {
                selected = it
                calls++
            }, defaultOpen = true, placeholder = "Select a fruit") {
                SelectTrigger(Modifier.testTag("trigger"))
                SelectContent {
                    SelectItem(null, textValue = "Select a fruit")
                    SelectItem("Alpha")
                }
            }
        }
        composeRule.onNodeWithText("Select a fruit").performClick()
        composeRule.runOnIdle {
            assertEquals(null, selected)
            assertEquals(1, calls)
        }
        composeRule.onNodeWithTag("trigger").assertTextContains("Select a fruit")
    }
}
