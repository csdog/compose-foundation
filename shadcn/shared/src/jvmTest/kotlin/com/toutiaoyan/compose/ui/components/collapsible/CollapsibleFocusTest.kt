package com.toutiaoyan.compose.ui.components.collapsible

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.requestFocus
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test

class CollapsibleFocusTest : ScreenshotTest() {
    @Test fun closingContentReturnsFocusToTheRemainingTrigger() {
        var open by mutableStateOf(true)
        var originalTrigger by mutableStateOf(true)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Collapsible(open = open) {
                if (originalTrigger) CollapsibleTrigger("Original", Modifier.testTag("original"))
                CollapsibleTrigger("Remaining", Modifier.testTag("remaining"))
                CollapsibleContent {
                    Button("Inside", modifier = Modifier.testTag("inside"), onClick = {})
                }
            }
        }
        composeRule.onNodeWithTag("original").requestFocus().assertIsFocused()
        composeRule.onNodeWithTag("inside").requestFocus().assertIsFocused()
        composeRule.runOnIdle { originalTrigger = false }
        composeRule.runOnIdle { open = false }
        composeRule.onNodeWithTag("remaining").assertIsFocused()
    }
}
