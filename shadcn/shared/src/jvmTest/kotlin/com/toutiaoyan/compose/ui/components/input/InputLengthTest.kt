package com.toutiaoyan.compose.ui.components.input

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
import androidx.compose.ui.text.input.TextFieldValue
import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test
import kotlin.test.assertEquals

class InputLengthTest : ScreenshotTest() {
    @Test fun insertionAtTheLimitKeepsUntouchedSuffix() {
        var value by mutableStateOf("abcd")
        setThemedContent {
            Input(value, { value = it }, Modifier.testTag("input"), maxLength = 4)
        }
        val input = composeRule.onNodeWithTag("input").requestFocus()
        input.performTextInputSelection(TextRange(1))
        input.performTextInput("X")
        composeRule.runOnIdle { assertEquals("abcd", value) }
        input.performTextInputSelection(TextRange(1, 3))
        input.performTextInput("XYZ")
        composeRule.runOnIdle { assertEquals("aXYd", value) }
    }

    @Test fun limitDoesNotSplitASupplementaryCharacter() {
        val value = TextFieldValue("abcd", TextRange(1))
        val paste = TextFieldValue("a😀bcd", TextRange(3))

        assertEquals(value, paste.limitInsertion(value, 5))
        assertEquals(paste, paste.limitInsertion(value, 6))
    }
}
