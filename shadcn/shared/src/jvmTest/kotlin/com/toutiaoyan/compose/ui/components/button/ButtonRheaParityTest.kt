package com.toutiaoyan.compose.ui.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.BaseColor
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ButtonRheaParityTest : ScreenshotTest() {
    @Test
    fun smallOutlineUsesSourcePaddingAndMediumLabel() {
        composeRule.setContent {
            Theme(preset = StylePreset.Rhea, fontFamily = FontFamily.Monospace, minTouchTarget = null) {
                Button(modifier = Modifier.testTag("button"), variant = ButtonVariant.Outline, size = ButtonSize.Sm) {
                    Text(modifier = Modifier.testTag("text")) { "Replay" }
                }
            }
        }
        val button = composeRule.onNodeWithTag("button").getUnclippedBoundsInRoot()
        val label = composeRule.onNodeWithTag("text", useUnmergedTree = true)
        val text = label.getUnclippedBoundsInRoot()
        val layouts = mutableListOf<TextLayoutResult>()
        label.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        val typography = layouts.single().layoutInput.style
        assertEquals(28.dp, button.bottom - button.top)
        assertEquals(13.dp, text.left - button.left)
        assertEquals(13.dp, button.right - text.right)
        assertEquals(14.sp, typography.fontSize)
        assertEquals(20.sp, typography.lineHeight)
        assertEquals(FontWeight.Medium, typography.fontWeight)
        assertEquals(FontFamily.Monospace, typography.fontFamily)
    }

    @Test
    fun outlineKeepsDarkRestingSurfaceTransparentAndUpdatesHoverAcrossThemes() {
        val surface = Color(0xff293349)
        var dark by mutableStateOf(false)
        composeRule.setContent {
            Theme(preset = StylePreset.Rhea, isDark = dark, minTouchTarget = null) {
                Box(Modifier.background(surface).padding(8.dp)) {
                    Button(
                        "Replay",
                        modifier = Modifier.testTag("button"),
                        variant = ButtonVariant.Outline,
                        size = ButtonSize.Sm,
                    )
                }
            }
        }
        val button = composeRule.onNodeWithTag("button")

        fun topFill(): Int {
            val pixels = button.captureToImage().toPixelMap()
            return pixels[pixels.width / 2, 3].toArgb()
        }
        assertEquals(BaseColor.Neutral.light.background.toArgb(), topFill())
        composeRule.runOnIdle { dark = true }
        assertEquals(surface.toArgb(), topFill())
        button.performMouseInput { moveTo(center) }
        val input = BaseColor.Neutral.dark.input
        val darkHover = input.copy(alpha = input.alpha * 0.3f).compositeOver(surface)
        val rendered = topFill()
        val expected = darkHover.toArgb()
        // Skia's blend can round a color channel to the neighboring 8-bit value.
        for (shift in listOf(16, 8, 0)) {
            val actualChannel = (rendered ushr shift) and 0xff
            val expectedChannel = (expected ushr shift) and 0xff
            assertTrue(actualChannel in expectedChannel - 1..expectedChannel + 1)
        }
        assertEquals(expected ushr 24, rendered ushr 24)
        composeRule.runOnIdle { dark = false }
        assertEquals(BaseColor.Neutral.light.muted.toArgb(), topFill())
    }
}
