@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.select

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.style.contentPadding
import androidx.compose.ui.input.key.Key
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals

class SelectPanelStyleTest : ScreenshotTest() {
    private fun panel(style: Style = Style) {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.padding(top = 200.dp)) {
                Select<String>(null, {}, defaultOpen = true) {
                    SelectTrigger(Modifier.width(192.dp).testTag("trigger"))
                    SelectContent(Modifier.testTag("panel"), style = style) {
                        SelectGroup(Modifier.testTag("group")) { SelectItem("Alpha") }
                    }
                }
            }
        }
    }

    @Test fun novaOuterRingDoesNotInsetGroupLayout() {
        panel()
        composeRule.onNodeWithTag("panel").assertWidthIsEqualTo(192.dp)
        composeRule.onNodeWithTag("group").assertWidthIsEqualTo(192.dp)
        val panel = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInRoot
        val group = composeRule.onNodeWithTag("group").fetchSemanticsNode().boundsInRoot
        assertEquals(panel.left, group.left, .1f)
        assertEquals(panel.top, group.top, .1f)
    }

    @Test fun callerBorderStillOwnsPanelLayout() {
        panel(
            Style {
                borderWidth(3.dp)
                borderColor(Color.Magenta)
            },
        )
        composeRule.onNodeWithTag("panel").assertWidthIsEqualTo(192.dp)
        composeRule.onNodeWithTag("group").assertWidthIsEqualTo(186.dp)
    }

    @Test fun callerBorderAddsToNaturalHeightWithoutFalseScrolling() {
        panel(Style { borderWidth(3.dp) })
        composeRule.onNodeWithTag("panel").assertHeightIsEqualTo(42.dp)
        composeRule.onNodeWithTag("group").assertHeightIsEqualTo(36.dp)
        composeRule.onAllNodes(hasAnyAncestor(hasTestTag("panel")) and hasClickAction()).assertCountEquals(1)
    }

    @Test fun callerPaddingAddsToNaturalHeightWithoutFalseScrolling() {
        panel(Style { contentPadding(8.dp) })
        composeRule.onNodeWithTag("panel").assertHeightIsEqualTo(52.dp)
        composeRule.onNodeWithTag("group").assertHeightIsEqualTo(36.dp)
    }

    @Test fun fixedPanelHeightStillOpensLastOptionFullyVisible() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Select<String>(null, {}) {
                SelectTrigger(Modifier.testTag("trigger"))
                SelectContent(Modifier.testTag("panel"), style = Style { height(100.dp) }) {
                    SelectGroup { repeat(30) { SelectItem("Option $it") } }
                }
            }
        }
        composeRule.onNodeWithTag("trigger").requestFocus().performKeyInput { pressKey(Key.DirectionUp) }
        composeRule.onNodeWithTag("panel").assertHeightIsEqualTo(100.dp)
        composeRule.onNodeWithText("Option 29").assertIsFocused().assertIsDisplayed()
        val panel = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow
        val last = composeRule.onNodeWithText("Option 29").fetchSemanticsNode().boundsInWindow
        assertEquals(panel.bottom - 4f, last.bottom, 1f)
    }

    @Test fun shortContentHonorsAndReleasesCallerHeight() {
        var tall by mutableStateOf(true)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Select<String>(null, {}, defaultOpen = true) {
                SelectTrigger()
                SelectContent(Modifier.testTag("panel"), style = if (tall) Style { height(100.dp) } else Style) {
                    SelectGroup { SelectItem("Alpha") }
                }
            }
        }
        composeRule.onNodeWithTag("panel").assertHeightIsEqualTo(100.dp)
        composeRule.runOnIdle { tall = false }
        composeRule.onNodeWithTag("panel").assertHeightIsEqualTo(36.dp)
    }

    @Test fun shortContentHonorsAndReleasesCallerMinimumHeight() {
        var tall by mutableStateOf(true)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Select<String>(null, {}, defaultOpen = true) {
                SelectTrigger()
                SelectContent(Modifier.testTag("panel"), style = if (tall) Style { minHeight(80.dp) } else Style) {
                    SelectGroup { SelectItem("Alpha") }
                }
            }
        }
        composeRule.onNodeWithTag("panel").assertHeightIsEqualTo(80.dp)
        composeRule.runOnIdle { tall = false }
        composeRule.onNodeWithTag("panel").assertHeightIsEqualTo(36.dp)
    }

    @Test fun callerCanOverrideTheDefaultMinimumWidth() {
        panel(
            Style {
                minWidth(0.dp)
                width(100.dp)
            },
        )
        composeRule.onNodeWithTag("panel").assertWidthIsEqualTo(100.dp)
    }

    @Test fun callerBorderAndPaddingBothContributeToNaturalHeight() {
        panel(
            Style {
                borderWidth(3.dp)
                contentPadding(8.dp)
            },
        )
        composeRule.onNodeWithTag("panel").assertHeightIsEqualTo(58.dp)
        composeRule.onNodeWithTag("group").assertHeightIsEqualTo(36.dp)
    }

    @Test fun fixedHeightPopperRevealsSelectedLastItemOnOpen() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Select("Option 29", {}, defaultOpen = true) {
                SelectTrigger()
                SelectContent(
                    Modifier.testTag("panel"),
                    style = Style { height(100.dp) },
                    alignItemWithTrigger = false,
                ) {
                    SelectGroup { repeat(30) { SelectItem("Option $it") } }
                }
            }
        }
        composeRule.onAllNodesWithText("Option 29").onLast().assertIsFocused().assertIsDisplayed()
    }

    @Test fun longCallerHeightOverridesAlignedNaturalHeight() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Select("Option 15", {}, defaultOpen = true) {
                SelectTrigger()
                SelectContent(Modifier.testTag("panel"), style = Style { height(600.dp) }) {
                    SelectGroup { repeat(30) { SelectItem("Option $it") } }
                }
            }
        }
        composeRule.onNodeWithTag("panel").assertHeightIsEqualTo(600.dp)
        composeRule.onAllNodesWithText("Option 15").onLast().assertIsFocused().assertIsDisplayed()
    }

    @Test fun callerMaximumHeightPreservesTheBottomEdgeAtTheLimit() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.padding(top = 200.dp)) {
                Select<String>(null, {}, defaultOpen = true) {
                    SelectTrigger()
                    SelectContent(Modifier.testTag("panel"), style = Style { maxHeight(600.dp) }) {
                        SelectGroup { repeat(30) { SelectItem("Option $it") } }
                    }
                }
            }
        }
        val arrows =
            hasAnyAncestor(hasTestTag("panel")) and
                SemanticsMatcher.keyIsDefined(SemanticsProperties.HideFromAccessibility)
        repeat(2) { composeRule.onAllNodes(arrows).onLast().performTouchInput { click() } }
        composeRule.onNodeWithTag("panel").assertHeightIsEqualTo(600.dp)
        val panel = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow
        assertEquals(758f, panel.bottom, .1f)
    }

    @Test fun removingCallerHeightAfterScrollingDoesNotTriggerFreshGrowth() {
        var fixed by mutableStateOf(true)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Select("Option 15", {}, defaultOpen = true) {
                SelectTrigger()
                SelectContent(Modifier.testTag("panel"), style = if (fixed) Style { height(600.dp) } else Style) {
                    SelectGroup { repeat(30) { SelectItem("Option $it") } }
                }
            }
        }
        val arrows =
            hasAnyAncestor(hasTestTag("panel")) and
                SemanticsMatcher.keyIsDefined(SemanticsProperties.HideFromAccessibility)
        composeRule.onAllNodes(arrows).onFirst().performTouchInput { click() }
        composeRule.onNodeWithTag("panel").assertHeightIsEqualTo(600.dp)
        composeRule.runOnIdle { fixed = false }
        composeRule.onNodeWithTag("panel").assertHeightIsEqualTo(440.dp)
    }

    @Test fun constrainedAlignedPanelRevealsSelectedLastItem() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.padding(top = 200.dp)) {
                Select("Option 29", {}, defaultOpen = true) {
                    SelectTrigger()
                    SelectContent(Modifier.testTag("panel"), style = Style { height(100.dp) }) {
                        SelectGroup { repeat(30) { SelectItem("Option $it") } }
                    }
                }
            }
        }
        composeRule.onNodeWithTag("panel").assertHeightIsEqualTo(100.dp)
        composeRule.onAllNodesWithText("Option 29").onLast().assertIsFocused().assertIsDisplayed()
    }

    @Test fun removingMaximumChangesTheNextGrowthStep() {
        var limited by mutableStateOf(true)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.padding(top = 200.dp)) {
                Select<String>(null, {}, defaultOpen = true) {
                    SelectTrigger()
                    SelectContent(
                        Modifier.testTag("panel"),
                        style = if (limited) Style { maxHeight(600.dp) } else Style,
                    ) {
                        SelectGroup { repeat(30) { SelectItem("Option $it") } }
                    }
                }
            }
        }
        val arrows =
            hasAnyAncestor(hasTestTag("panel")) and
                SemanticsMatcher.keyIsDefined(SemanticsProperties.HideFromAccessibility)
        composeRule.onAllNodes(arrows).onLast().performTouchInput { click() }
        composeRule.onNodeWithTag("panel").assertHeightIsEqualTo(584.dp)
        composeRule.runOnIdle { limited = false }
        composeRule.onAllNodes(arrows).onLast().performTouchInput { click() }
        composeRule.onNodeWithTag("panel").assertHeightIsEqualTo(632.dp)
    }
}
