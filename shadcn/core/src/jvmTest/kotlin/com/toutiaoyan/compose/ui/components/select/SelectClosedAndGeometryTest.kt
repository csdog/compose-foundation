@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.select

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class SelectClosedAndGeometryTest : ScreenshotTest() {
    @Test fun neverOpenedTriggerTypesSelectsAndKeepsPopupClosed() {
        var value by mutableStateOf("Alpha")
        var openChanges = 0
        setThemedContent(stylePreset = StylePreset.Nova) {
            Select(value, { value = it }, onOpenChange = { openChanges++ }) {
                SelectTrigger(Modifier.testTag("trigger"))
                SelectContent {
                    SelectItem("Alpha")
                    SelectItem("Banana", disabled = true)
                    SelectItem("Blueberry")
                    SelectItem("Blackberry")
                }
            }
        }
        composeRule.onNodeWithText("Blueberry").assertDoesNotExist()
        composeRule.onNodeWithTag("trigger").requestFocus().performKeyInput { pressKey(Key.B) }
        composeRule.runOnIdle {
            assertEquals("Blueberry", value)
            assertEquals(0, openChanges)
        }
        composeRule.onNodeWithTag("trigger").assertIsFocused().performKeyInput { pressKey(Key.B) }
        composeRule.runOnIdle { assertEquals("Blackberry", value) }
        composeRule.onNodeWithText("Banana").assertDoesNotExist()
    }

    @Test fun closedRegistryTracksLatestLabelsDisabledAndKeyedOrderWithoutOpening() {
        var reverse by mutableStateOf(false)
        var disabled by mutableStateOf(false)
        var targetLabel by mutableStateOf("Zebra")
        var picked: String? = null
        setThemedContent(stylePreset = StylePreset.Nova) {
            Select("Alpha", { picked = it }) {
                SelectTrigger(Modifier.testTag("trigger"))
                SelectContent {
                    val order = if (reverse) listOf("two", "one") else listOf("one", "two")
                    SelectItem("Alpha")
                    order.forEach { value ->
                        key(value) {
                            SelectItem(
                                value,
                                textValue = if (value == "one") targetLabel else "Zulu",
                                disabled = disabled && value == "one",
                            )
                        }
                    }
                }
            }
        }
        composeRule.runOnIdle { reverse = true }
        composeRule.onNodeWithTag("trigger").requestFocus().performKeyInput { pressKey(Key.Z) }
        composeRule.runOnIdle {
            assertEquals("two", picked)
            targetLabel = "Beta"
            disabled = true
        }
        Thread.sleep(800)
        composeRule.onNodeWithTag("trigger").performKeyInput { pressKey(Key.B) }
        composeRule.runOnIdle {
            assertEquals("two", picked)
            disabled = false
        }
        Thread.sleep(800)
        composeRule.onNodeWithTag("trigger").performKeyInput { pressKey(Key.B) }
        composeRule.runOnIdle { assertEquals("one", picked) }
    }

    @Test fun hiddenOptionsStayOutOfSemanticsAndFocusAcrossOpenClose() {
        var value by mutableStateOf("Alpha")
        setThemedContent(stylePreset = StylePreset.Nova) {
            androidx.compose.foundation.layout.Column {
                Select(value, { value = it }) {
                    SelectTrigger(Modifier.testTag("trigger"))
                    SelectContent {
                        SelectItem("Alpha", Modifier.testTag("alpha"))
                        SelectItem("Beta", Modifier.testTag("beta"))
                    }
                }
                Button(onClick = {}, modifier = Modifier.testTag("next")) { Text { "Next" } }
            }
        }
        composeRule.onNodeWithTag("beta", useUnmergedTree = true).assertDoesNotExist()
        composeRule.onNodeWithTag("trigger").requestFocus().performKeyInput { pressKey(Key.Tab) }
        composeRule.onNodeWithTag("next").assertIsFocused()
        composeRule.onNodeWithTag("trigger").requestFocus().performKeyInput { pressKey(Key.DirectionDown) }
        composeRule.onNodeWithTag("alpha").performKeyInput {
            pressKey(Key.DirectionDown)
            pressKey(Key.Enter)
        }
        composeRule.runOnIdle { assertEquals("Beta", value) }
        composeRule.onNodeWithTag("beta", useUnmergedTree = true).assertDoesNotExist()
        composeRule.onNodeWithTag("trigger").performKeyInput { pressKey(Key.A) }
        composeRule.runOnIdle { assertEquals("Alpha", value) }
    }

    @Test fun novaGroupsOwnPaddingAndSeparatorOwnsVerticalMargins() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Select<String>(null, {}, defaultOpen = true) {
                SelectTrigger(Modifier.width(192.dp))
                SelectContent(Modifier.testTag("panel")) {
                    SelectGroup(Modifier.testTag("first group")) { SelectItem("Alpha", Modifier.testTag("first row")) }
                    SelectSeparator(Modifier.testTag("separator"))
                    SelectGroup(Modifier.testTag("second group")) { SelectItem("Beta", Modifier.testTag("second row")) }
                }
            }
        }

        fun bounds(tag: String): Rect = composeRule.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot
        val panel = bounds("panel")
        val group = bounds("first group")
        val row = bounds("first row")
        val secondGroup = bounds("second group")
        assertEquals(panel.width - 2f, group.width, .1f)
        assertEquals(4f, row.left - group.left, .1f)
        assertEquals(4f, row.top - group.top, .1f)
        assertEquals(9f, secondGroup.top - group.bottom, .1f)
        composeRule.onNodeWithTag("separator").assertHeightIsEqualTo(9.dp)
    }
}
