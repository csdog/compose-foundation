package com.toutiaoyan.compose.ui.components.radiobutton

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RadioGroupSingleItemTest : ScreenshotTest() {
    @Test fun arrowWrapToTheOnlySelectedOptionKeepsFocusWithoutReportingAChange() {
        val requests = mutableListOf<String>()
        setThemedContent {
            RadioGroup(defaultValue = "only", onValueChange = { requests += it }) {
                RadioGroupItem("only", modifier = Modifier.testTag("only"))
                RadioGroupItem("disabled", disabled = true)
            }
        }
        val only = composeRule.onNodeWithTag("only").requestFocus()
        only.performKeyInput { pressKey(Key.DirectionDown) }
        only.assertIsSelected().assertIsFocused()
        composeRule.runOnIdle { assertEquals(emptyList(), requests) }
    }
}
