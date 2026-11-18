@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.select

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals

class SelectTypeaheadExpiryTest : ScreenshotTest() {
    @Test fun spaceOpensClosedSelectAfterTypeaheadExpires() {
        var value by mutableStateOf("Alpha")
        setThemedContent(stylePreset = StylePreset.Nova) {
            Select(value, { value = it }) {
                SelectTrigger(Modifier.testTag("trigger"))
                SelectContent {
                    SelectItem("Alpha", Modifier.testTag("alpha"))
                    SelectItem("Blueberry", Modifier.testTag("blueberry"))
                }
            }
        }
        composeRule.onNodeWithTag("trigger").requestFocus().performKeyInput { pressKey(Key.B) }
        composeRule.runOnIdle { assertEquals("Blueberry", value) }
        composeRule.onNodeWithTag("alpha").assertDoesNotExist()
        Thread.sleep(850)
        composeRule.onNodeWithTag("trigger").performKeyInput { pressKey(Key.Spacebar) }
        composeRule.onNodeWithTag("alpha").assertExists()
        composeRule.onNodeWithTag("blueberry").assertIsFocused()
    }
}
