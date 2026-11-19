package com.toutiaoyan.compose.ui.components.menubar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test

class MenubarTriggerOrderTest : ScreenshotTest() {
    @Test fun keyedReorderMovesAcrossTheCurrentTriggerPositions() {
        var order by mutableStateOf(listOf("Alpha", "Beta", "Gamma"))
        setThemedContent(stylePreset = StylePreset.Nova) {
            Menubar {
                order.forEach { label ->
                    key(label) {
                        MenubarMenu(label) { MenubarTrigger(label, Modifier.testTag(label)) }
                    }
                }
            }
        }
        composeRule.onNodeWithTag("Alpha").requestFocus().performKeyInput { pressKey(Key.DirectionRight) }
        val beta = composeRule.onNodeWithTag("Beta").assertIsFocused()
        composeRule.runOnIdle { order = listOf("Alpha", "Gamma", "Beta") }
        beta.performKeyInput { pressKey(Key.DirectionLeft) }
        composeRule.onNodeWithTag("Gamma").assertIsFocused()
    }
}
