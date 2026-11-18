@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.select

import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.runtime.*
import com.toutiaoyan.compose.ui.theme.Theme
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals

class SelectAvailableHeightTest : ScreenshotTest() {
    @Test fun elevenRowsFitWithoutArtificial288DpViewportLimit() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Select<String>(null, {}, defaultOpen = true) {
                SelectTrigger(Modifier.width(192.dp))
                SelectContent(Modifier.testTag("panel")) {
                    SelectGroup { repeat(11) { SelectItem("Option $it") } }
                }
            }
        }
        composeRule.onNodeWithTag("panel").assertHeightIsEqualTo(316.dp)
        composeRule.onAllNodes(hasAnyAncestor(hasTestTag("panel")) and hasClickAction()).assertCountEquals(11)
    }

    @Test fun tallListUsesWindowSpaceAndStillScrollsToLastOption() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Select<String>(null, {}, defaultOpen = true) {
                SelectTrigger(Modifier.width(192.dp))
                SelectContent(Modifier.testTag("panel")) {
                    SelectGroup { repeat(30) { SelectItem("Option $it") } }
                }
            }
        }
        val panel = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow
        assertTrue(panel.height > 316f)
        assertTrue(panel.top >= 10f)
        assertTrue(panel.bottom <= 758f)
        composeRule.onNodeWithText("Option 29").performScrollTo().assertIsDisplayed()
    }

    @Test fun selectedMiddleRowAlignsWithTriggerWithinViewport() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Select("Option 15", {}, defaultOpen = true) {
                SelectTrigger(Modifier.width(192.dp).testTag("trigger"))
                SelectContent(Modifier.testTag("panel")) {
                    SelectGroup { repeat(30) { SelectItem("Option $it", Modifier.testTag("item $it")) } }
                }
            }
        }
        val trigger = composeRule.onNodeWithTag("trigger").fetchSemanticsNode().boundsInWindow
        val item = composeRule.onNodeWithTag("item 15").fetchSemanticsNode().boundsInWindow
        assertEquals(trigger.center.y, item.center.y, 1f)
        composeRule.onNodeWithTag("item 15").assertIsDisplayed()
    }

    @Test fun touchOpensBelowTriggerInsteadOfAligningSelectedRow() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Select("Beta", {}) {
                SelectTrigger(Modifier.width(192.dp).testTag("trigger"))
                SelectContent(Modifier.testTag("panel")) {
                    SelectGroup {
                        SelectItem("Alpha")
                        SelectItem("Beta")
                    }
                }
            }
        }
        composeRule.onNodeWithTag("trigger").performTouchInput { click() }
        val trigger = composeRule.onNodeWithTag("trigger").fetchSemanticsNode().boundsInWindow
        val panel = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow
        assertEquals(trigger.bottom + 4f, panel.top, 1f)
    }

    @Test fun arrowUpOpensWithLastOptionFocusedAndVisible() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Select<String>(null, {}) {
                SelectTrigger(Modifier.width(192.dp).testTag("trigger"))
                SelectContent(Modifier.testTag("panel")) {
                    SelectGroup { repeat(30) { SelectItem("Option $it") } }
                }
            }
        }
        composeRule.onNodeWithTag("trigger").requestFocus().performKeyInput { pressKey(Key.DirectionUp) }
        composeRule.onNodeWithText("Option 29").assertIsFocused().assertIsDisplayed()
        val panel = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow
        val last = composeRule.onNodeWithText("Option 29").fetchSemanticsNode().boundsInWindow
        assertTrue(last.height >= 28f)
        assertTrue(last.bottom <= panel.bottom - 4f)
    }

    @Test fun liveThemeChangeDoesNotResetUserScroll() {
        var dark by mutableStateOf(false)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                Select<String>(null, {}, defaultOpen = true) {
                    SelectTrigger(Modifier.width(192.dp))
                    SelectContent {
                        SelectGroup { repeat(30) { SelectItem("Option $it") } }
                    }
                }
            }
        }
        composeRule.onNodeWithText("Option 29").performScrollTo()
        val before = composeRule.onNodeWithText("Option 29").fetchSemanticsNode().boundsInWindow
        composeRule.runOnIdle { dark = true }
        composeRule.onNodeWithText("Option 29").assertIsDisplayed()
        val after = composeRule.onNodeWithText("Option 29").fetchSemanticsNode().boundsInWindow
        assertEquals(before.top, after.top, 1f)
    }

    @Test fun disabledOptionsRemainVisibleWhenNoItemCanReceiveFocus() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Select<String>(null, {}, defaultOpen = true) {
                SelectTrigger(Modifier.width(192.dp))
                SelectContent {
                    SelectGroup {
                        SelectItem("Alpha", disabled = true)
                        SelectItem("Beta", disabled = true)
                    }
                }
            }
        }
        composeRule.onNodeWithText("Alpha").assertIsNotEnabled().assertIsDisplayed()
        composeRule.onNodeWithText("Beta").assertIsNotEnabled().assertIsDisplayed()
    }
}
