@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.label

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.field.FieldLabel
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test

class LabelScreenshotTest : ScreenshotTest() {
    @Test
    fun zeroLineHeightUsesTheParagraphMeasurement() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            FieldLabel("Zero line height", TextStyle(lineHeight = 0.sp))
        }
        composeRule.onNodeWithText("Zero line height").assertIsDisplayed()
    }

    @Test
    fun relativeLineHeightUsesTheParagraphMeasurement() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            FieldLabel("Relative line height", TextStyle(lineHeight = 1.5.em))
        }
        composeRule.onNodeWithText("Relative line height").assertIsDisplayed()
    }

    private fun states(darkTheme: Boolean) {
        snapshot("label_states", darkTheme = darkTheme) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Label(text = "Default label")
                Label(text = "Required label", required = true)
                Label(text = "Disabled label", disabled = true)
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)
}
