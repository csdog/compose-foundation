@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.checkbox

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import kotlin.math.abs
import kotlin.test.assertTrue

/** Pixel assertions cover live updates without replacing historical screenshot goldens. */
class CheckboxLiveStyleTest {
    @get:Rule val rule = createComposeRule()

    @Test fun callerShapeAndThemeUpdateOnTheSameCheckedControl() {
        var rounded by mutableStateOf(false)
        var dark by mutableStateOf(false)
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                Box(Modifier.testTag("surface").background(Color.Magenta).padding(16.dp)) {
                    Checkbox(
                        checked = true,
                        onCheckedChange = {},
                        modifier = Modifier.testTag("control"),
                        style = Style { shape(RoundedCornerShape(if (rounded) 8.dp else 0.dp)) },
                    )
                }
            }
        }

        fun corner() = rule.onNodeWithTag("surface").captureToImage().toPixelMap()[16, 16]

        fun fill() = rule.onNodeWithTag("surface").captureToImage().toPixelMap()[24, 19]
        val lightFill = fill()
        assertTrue(corner() != Color.Magenta, "Square corner must cover the parent")
        rule.runOnIdle { rounded = true }
        assertTrue(corner() == Color.Magenta, "Live round shape must reveal the parent corner")
        rule.onNodeWithTag("control").assertIsOn()
        rule.runOnIdle { dark = true }
        assertTrue(abs(fill().red - lightFill.red) > .5f, "Checked fill must follow the live theme")
        rule.runOnIdle {
            dark = false
            rounded = false
        }
        assertTrue(abs(fill().red - lightFill.red) < .02f, "Light fill must be restored")
        assertTrue(corner() != Color.Magenta, "Square shape must be restored")
        rule.onNodeWithTag("control").assertIsOn()
    }

    @Test fun checkedColorHasAnIntermediateFrameAndSettles() {
        var checked by mutableStateOf(false)
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = false, minTouchTarget = null) {
                Box(Modifier.testTag("surface").background(Color.White).padding(16.dp)) {
                    Checkbox(checked = checked, onCheckedChange = {})
                }
            }
        }

        fun red() = rule.onNodeWithTag("surface").captureToImage().toPixelMap()[24, 19].red
        val start = red()
        rule.mainClock.autoAdvance = false
        try {
            rule.runOnIdle { checked = true }
            rule.mainClock.advanceTimeByFrame()
            assertTrue(abs(red() - start) < .02f, "Checked fill must start from the unchecked color")
            rule.mainClock.advanceTimeBy(64)
            val middle = red()
            rule.mainClock.advanceTimeBy(200)
            val end = red()
            assertTrue(start - middle > .02f, "Color must have started transitioning")
            assertTrue(middle - end > .02f, "Mid-frame must differ from the settled checked fill")
            assertTrue(end < .3f, "Checked fill must settle to the dark primary")

            rule.runOnIdle { checked = false }
            rule.mainClock.advanceTimeByFrame()
            rule.mainClock.advanceTimeBy(64)
            val reverseMiddle = red()
            rule.mainClock.advanceTimeBy(200)
            assertTrue(reverseMiddle - end > .02f, "Unchecked fill must transition from the checked color")
            assertTrue(start - reverseMiddle > .02f, "Reverse mid-frame must differ from the unchecked fill")
            assertTrue(abs(red() - start) < .02f, "Unchecked fill must return to the starting color")
        } finally {
            rule.mainClock.autoAdvance = true
        }
    }
}
