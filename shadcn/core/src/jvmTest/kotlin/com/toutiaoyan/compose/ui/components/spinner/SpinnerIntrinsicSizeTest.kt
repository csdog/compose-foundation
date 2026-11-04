@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.spinner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test

class SpinnerIntrinsicSizeTest {
    @get:Rule val rule = createComposeRule()

    @Test fun intrinsicRowIncludesEachPresetSize() {
        var size by mutableStateOf(SpinnerSize.Default)
        rule.setContent {
            Theme(preset = StylePreset.Nova) {
                Row(Modifier.width(IntrinsicSize.Max).testTag("row")) {
                    Spinner(Modifier.testTag("spinner"), size = size)
                    Box(Modifier.size(10.dp))
                }
            }
        }
        for ((value, width) in listOf(SpinnerSize.Default to 16, SpinnerSize.Sm to 12, SpinnerSize.Lg to 24)) {
            rule.runOnIdle { size = value }
            rule.onNodeWithTag("spinner").assertWidthIsEqualTo(width.dp).assertHeightIsEqualTo(width.dp)
            rule.onNodeWithTag("row").assertWidthIsEqualTo((width + 10).dp)
        }
    }

    @Test fun intrinsicHintStillHonorsStyleAndCallerModifierSizes() {
        var explicit by mutableStateOf(false)
        var expanded by mutableStateOf(false)
        rule.setContent {
            Theme(preset = StylePreset.Nova) {
                Row(Modifier.width(IntrinsicSize.Max).testTag("row")) {
                    Spinner(
                        Modifier.testTag("spinner").then(if (explicit) Modifier.size(32.dp) else Modifier),
                        style =
                            Style {
                                width(if (expanded) 28.dp else 24.dp)
                                height(18.dp)
                            },
                    )
                    Box(Modifier.size(10.dp))
                }
            }
        }
        rule.onNodeWithTag("spinner").assertWidthIsEqualTo(24.dp).assertHeightIsEqualTo(18.dp)
        rule.onNodeWithTag("row").assertWidthIsEqualTo(34.dp)
        rule.runOnIdle { expanded = true }
        rule.onNodeWithTag("spinner").assertWidthIsEqualTo(28.dp).assertHeightIsEqualTo(18.dp)
        rule.onNodeWithTag("row").assertWidthIsEqualTo(38.dp)
        rule.runOnIdle { explicit = true }
        rule.onNodeWithTag("spinner").assertWidthIsEqualTo(32.dp).assertHeightIsEqualTo(32.dp)
        rule.onNodeWithTag("row").assertWidthIsEqualTo(42.dp)
    }
}
