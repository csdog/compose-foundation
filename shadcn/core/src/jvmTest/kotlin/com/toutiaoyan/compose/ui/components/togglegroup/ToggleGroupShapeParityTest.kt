@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.togglegroup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.toggle.ToggleSize
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.Radius
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertTrue

class ToggleGroupShapeParityTest : ScreenshotTest() {
    @Test fun smallCornersRespectSpacingThemeAndCallerOverride() {
        var spacing by mutableStateOf(8.dp)
        var radius by mutableStateOf(10.dp)
        var preset by mutableStateOf(StylePreset.Nova)
        var customShape by mutableStateOf(false)
        composeRule.setContent {
            Theme(preset = preset, baseRadius = Radius(radius), isDark = false, minTouchTarget = null) {
                Box(Modifier.background(Color.Red)) {
                    ToggleGroup(value = setOf("a", "b"), size = ToggleSize.Sm, spacing = spacing) {
                        ToggleGroupItem(
                            "a",
                            modifier = Modifier.testTag("first"),
                            style = if (customShape) Style { shape(RoundedCornerShape(12.dp)) } else Style,
                        ) { Box(Modifier.size(8.dp)) }
                        ToggleGroupItem("b") { Box(Modifier.size(8.dp)) }
                    }
                }
            }
        }

        fun greenAt(
            x: Int,
            y: Int,
        ): Float {
            val image = composeRule.onNodeWithTag("first").captureToImage().toPixelMap()
            return image[
                with(composeRule.density) { x.dp.roundToPx() },
                with(composeRule.density) { y.dp.roundToPx() },
            ].green
        }
        // Source-defined 8dp small corner fills this pixel; lg10 leaves it outside.
        assertTrue(greenAt(4, 1) > .8f, "Spaced Nova Sm must keep its 8dp corner")
        composeRule.runOnIdle { spacing = 0.dp }
        assertTrue(greenAt(4, 1) < .6f, "Connected outer corner must remain lg10")
        composeRule.runOnIdle {
            spacing = 8.dp
            radius = 20.dp
        }
        assertTrue(greenAt(7, 1) > .8f, "Small corners must update and cap at 12dp")
        composeRule.runOnIdle {
            radius = 10.dp
            customShape = true
        }
        assertTrue(greenAt(4, 1) < .6f, "Caller's 12dp shape must win")
        composeRule.runOnIdle {
            customShape = false
            preset = StylePreset.Vega
        }
        assertTrue(greenAt(4, 1) > .8f, "Legacy spaced md8 corners must be preserved")
    }
}
