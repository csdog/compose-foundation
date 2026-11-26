package com.toutiaoyan.compose.ui.components.contextmenu

import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.*
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test

class ContextMenuRowOrderTest : ScreenshotTest() {
    @Test fun keyedReorderChangesTheDirectionKeySequence() {
        var order by mutableStateOf(listOf("Alpha", "Beta", "Gamma"))
        setThemedContent(stylePreset = StylePreset.Nova) {
            ContextMenu {
                ContextMenuTrigger("Area")
                ContextMenuContent {
                    order.forEach { label -> key(label) { ContextMenuItem(label, onClick = {}) } }
                }
            }
        }

        composeRule.onNodeWithText("Area").requestFocus().performKeyInput {
            withKeyDown(Key.ShiftLeft) { pressKey(Key.F10) }
        }
        composeRule.onNodeWithText("Alpha").assertIsFocused().performKeyInput { pressKey(Key.DirectionDown) }
        val beta = composeRule.onNodeWithText("Beta").assertIsFocused()
        composeRule.runOnIdle { order = listOf("Alpha", "Gamma", "Beta") }
        beta.performKeyInput { pressKey(Key.DirectionUp) }
        composeRule.onNodeWithText("Gamma").assertIsFocused()
    }
}
