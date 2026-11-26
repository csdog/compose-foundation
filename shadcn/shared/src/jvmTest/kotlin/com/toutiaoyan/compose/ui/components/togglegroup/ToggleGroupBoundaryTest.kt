package com.toutiaoyan.compose.ui.components.togglegroup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.theme.Theme
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertFailsWith

class ToggleGroupBoundaryTest {
    @get:Rule val rule = createComposeRule()

    @Test
    fun emptyChildrenCanBecomeInteractiveAndEmptyAgain() {
        var populated by mutableStateOf(false)
        rule.setContent {
            Theme {
                ToggleGroup {
                    if (populated) ToggleGroupItem("a", "Alpha")
                }
            }
        }
        rule.onAllNodesWithText("Alpha").assertCountEquals(0)
        rule.runOnIdle { populated = true }
        rule.onNodeWithText("Alpha").performClick().assertIsOn()
        rule.runOnIdle { populated = false }
        rule.onAllNodesWithText("Alpha").assertCountEquals(0)
        rule.runOnIdle { populated = true }
        rule.onNodeWithText("Alpha").performClick().assertIsOff()
    }

    @Test
    fun emptyIdentityIsRejected() {
        assertFailsWith<IllegalArgumentException> {
            rule.setContent { Theme { ToggleGroup { ToggleGroupItem("", "Empty identity") } } }
            rule.waitForIdle()
        }
    }

    @Test
    fun duplicateIdentityIsRejected() {
        assertFailsWith<IllegalArgumentException> {
            rule.setContent {
                Theme {
                    ToggleGroup {
                        ToggleGroupItem("a", "Alpha")
                        ToggleGroupItem("a", "Another Alpha")
                    }
                }
            }
            rule.waitForIdle()
        }
    }

    @Test
    fun identityMayBeReusedAcrossGroups() {
        rule.setContent {
            Theme {
                Column {
                    ToggleGroup { ToggleGroupItem("a", "First") }
                    ToggleGroup { ToggleGroupItem("a", "Second") }
                }
            }
        }
        rule.onNodeWithText("First").performClick().assertIsOn()
        rule.onNodeWithText("Second").assertIsOff()
    }

    @Test
    fun emptyAndLongVisualLabelsRetainAccessibleNamesAndConstrainedBounds() {
        val longLabel = "A deliberately long formatting option that exceeds the constrained item"
        rule.setContent {
            Theme {
                ToggleGroup {
                    ToggleGroupItem("empty", modifier = Modifier.width(40.dp), label = "Empty visual") {}
                    ToggleGroupItem("long", longLabel, modifier = Modifier.width(80.dp))
                }
            }
        }
        rule.onNodeWithContentDescription("Empty visual")
            .assertWidthIsEqualTo(40.dp).performClick().assertIsOn()
        rule.onNodeWithContentDescription(longLabel)
            .assertWidthIsEqualTo(80.dp).performClick().assertIsOn()
        rule.onNodeWithContentDescription("Empty visual").assertIsOff()
    }

    @Test
    fun controlledCallerAcceptsAndClearsSelection() {
        rule.setContent {
            Theme {
                var selected by remember { mutableStateOf("") }
                ToggleGroup(value = selected, onValueChange = { selected = it }) {
                    ToggleGroupItem("a", "Alpha")
                    ToggleGroupItem("b", "Beta")
                }
            }
        }
        rule.onNodeWithText("Alpha").performClick().assertIsOn()
        rule.onNodeWithText("Beta").performClick().assertIsOn()
        rule.onNodeWithText("Alpha").assertIsOff()
        rule.onNodeWithText("Beta").performClick().assertIsOff()
    }
}
