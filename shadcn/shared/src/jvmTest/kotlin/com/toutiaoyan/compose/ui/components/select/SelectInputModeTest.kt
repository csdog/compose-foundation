@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.select

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals

class SelectInputModeTest : ScreenshotTest() {
    @Test fun popupKeyboardSelectionSwitchesModeBeforeReturningFocus() {
        val manager =
            object : InputModeManager {
                var mode by mutableStateOf(InputMode.Touch)

                override val inputMode get() = mode

                override fun requestInputMode(inputMode: InputMode): Boolean {
                    mode = inputMode
                    return true
                }
            }
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalInputModeManager provides manager) {
                Select("Alpha", {}, defaultOpen = true) {
                    SelectTrigger(Modifier.testTag("trigger"))
                    SelectContent { SelectItem("Alpha", Modifier.testTag("alpha")) }
                }
            }
        }
        composeRule.runOnIdle { assertEquals(InputMode.Touch, manager.inputMode) }
        composeRule.onNodeWithTag("alpha").performKeyInput { pressKey(Key.Enter) }
        composeRule.onNodeWithTag("trigger").assertIsFocused()
        composeRule.runOnIdle { assertEquals(InputMode.Keyboard, manager.inputMode) }
    }
}
