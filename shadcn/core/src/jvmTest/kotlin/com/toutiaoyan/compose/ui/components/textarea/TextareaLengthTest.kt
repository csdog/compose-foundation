package com.toutiaoyan.compose.ui.components.textarea

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextInputSelection
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.text.TextRange
import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TextareaLengthTest : ScreenshotTest() {
    @Test fun replacementAtTheLimitKeepsTheFollowingLine() {
        var value by mutableStateOf("ab\ncd")
        setThemedContent {
            Textarea(value, { value = it }, Modifier.testTag("textarea"), maxLength = 6)
        }
        val input = composeRule.onNodeWithTag("textarea").requestFocus()
        input.performTextInputSelection(TextRange(1, 2))
        input.performTextInput("XYZ")
        composeRule.runOnIdle { assertEquals("aXY\ncd", value) }
    }
}
