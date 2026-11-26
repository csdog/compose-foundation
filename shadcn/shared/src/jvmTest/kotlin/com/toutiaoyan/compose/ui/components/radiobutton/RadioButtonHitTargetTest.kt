package com.toutiaoyan.compose.ui.components.radiobutton

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class RadioButtonHitTargetTest {
    @get:Rule val rule = createComposeRule()

    @Test fun consumedArrowKeysEnterKeyboardModeBeforeMovingSelection() {
        var mode by mutableStateOf(InputMode.Touch)
        val manager =
            object : InputModeManager {
                override val inputMode get() = mode

                override fun requestInputMode(inputMode: InputMode): Boolean {
                    mode = inputMode
                    return true
                }
            }
        rule.setContent {
            CompositionLocalProvider(LocalInputModeManager provides manager) {
                Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                    RadioGroup(defaultValue = "a") {
                        RadioGroupItem("a", label = "A")
                        RadioGroupItem("b", label = "B")
                    }
                }
            }
        }
        rule.onNodeWithContentDescription("A").requestFocus()
        rule.onNodeWithContentDescription("A").performKeyInput { pressKey(Key.DirectionDown) }
        rule.onNodeWithContentDescription("B").assertIsSelected().assertIsFocused()
        assertEquals(InputMode.Keyboard, mode)
    }

    @Test fun novaExtendsPointerHitsWhileKeepingTheControlSixteenDp() {
        var clicks = 0
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Box(Modifier.size(120.dp).testTag("surface")) {
                    Box(Modifier.padding(40.dp)) {
                        RadioButton(false, { clicks++ }, Modifier.testTag("radio"))
                    }
                }
            }
        }
        val radio = rule.onNodeWithTag("radio")
        radio.assertWidthIsEqualTo(16.dp).assertHeightIsEqualTo(16.dp)
        val bounds = radio.fetchSemanticsNode().boundsInRoot
        val surface = rule.onNodeWithTag("surface")
        surface.performMouseInput { click(Offset(bounds.left - 8, bounds.center.y)) }
        assertEquals(1, clicks)
        surface.performMouseInput { click(Offset(bounds.center.x, bounds.top - 4)) }
        assertEquals(2, clicks)
        surface.performMouseInput { click(Offset(bounds.left - 14, bounds.center.y)) }
        assertEquals(2, clicks)
    }

    @Test fun anExplicitTouchMinimumStillControlsLayoutAndDisabledHitsStayInactive() {
        var clicks = 0
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = 48.dp) {
                RadioButton(false, { clicks++ }, Modifier.testTag("radio"), disabled = true)
            }
        }
        rule.onNodeWithTag("radio")
            .assertWidthIsEqualTo(48.dp)
            .assertHeightIsEqualTo(48.dp)
            .assertIsNotEnabled()
            .performMouseInput { click(center) }
        assertEquals(0, clicks)
    }
}
