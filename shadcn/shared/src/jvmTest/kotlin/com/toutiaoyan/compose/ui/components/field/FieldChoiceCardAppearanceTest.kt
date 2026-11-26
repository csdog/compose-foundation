package com.toutiaoyan.compose.ui.components.field

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.radiobutton.RadioGroup
import com.toutiaoyan.compose.ui.components.radiobutton.RadioGroupItem
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.Radius
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class FieldChoiceCardAppearanceTest {
    @get:Rule val rule = createComposeRule()

    @Test fun radiusAndKeyboardFocusUpdateTheRenderedChoiceCard() {
        var radius by mutableStateOf(Radius(0.dp))
        lateinit var inputMode: InputModeManager
        rule.setContent {
            inputMode = LocalInputModeManager.current
            Theme(preset = StylePreset.Nova, baseRadius = radius) {
                Box(Modifier.testTag("surface").background(Color.White).padding(8.dp)) {
                    RadioGroup(value = "choice", onValueChange = {}) {
                        FieldLabel(onClick = {}, modifier = Modifier.width(120.dp).height(48.dp), selected = true) {
                            RadioGroupItem("choice", modifier = Modifier.testTag("radio"))
                        }
                    }
                }
            }
        }
        val surface = rule.onNodeWithTag("surface")
        val square = surface.captureToImage().toPixelMap()
        rule.runOnIdle { radius = Radius(24.dp) }
        val round = surface.captureToImage().toPixelMap()
        assertNotEquals(square[10, 10], round[10, 10])
        assertEquals(square[60, 12], round[60, 12])

        rule.runOnIdle { inputMode.requestInputMode(InputMode.Keyboard) }
        rule.onNodeWithTag("radio").requestFocus().assertIsFocused()
        val focused = surface.captureToImage().toPixelMap()
        assertNotEquals(round[60, 6], focused[60, 6])
    }
}
