package com.toutiaoyan.compose.ui.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.icons.Check
import androidx.compose.ui.test.assertWidthIsEqualTo
import kotlin.test.assertEquals
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.BaseColor
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

class ButtonNovaParityTest : ScreenshotTest() {
    private fun matrix(dark: Boolean) =
        snapshot("button_nova_parity", darkTheme = dark, stylePreset = StylePreset.Nova) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        ButtonVariant.Default,
                        ButtonVariant.Outline,
                        ButtonVariant.Secondary,
                        ButtonVariant.Ghost,
                        ButtonVariant.Destructive,
                        ButtonVariant.Link,
                    ).forEach {
                        Button(it::class.simpleName.orEmpty(), variant = it)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(ButtonSize.Xs, ButtonSize.Sm, ButtonSize.Md, ButtonSize.Lg).forEach {
                        Button("Button", size = it, variant = ButtonVariant.Outline)
                    }
                    Button("Disabled", disabled = true, variant = ButtonVariant.Destructive)
                }
            }
        }

    @Test fun lightMatrix() = matrix(false)

    @Test fun darkMatrix() = matrix(true)

    @Test
    fun destructiveBackgroundAndContentFollowLiveTheme() {
        var dark by mutableStateOf(false)
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                val background = if (dark) BaseColor.Neutral.dark.background else BaseColor.Neutral.light.background
                Box(Modifier.background(background)) {
                    Button(variant = ButtonVariant.Destructive, modifier = Modifier.testTag("button")) {
                        Box(Modifier.size(8.dp).background(LocalContentColor.current).testTag("content"))
                    }
                }
            }
        }

        fun check(darkMode: Boolean) {
            val colors = if (darkMode) BaseColor.Neutral.dark else BaseColor.Neutral.light
            val pixels = composeRule.onNodeWithTag("button").captureToImage().toPixelMap()
            val expected =
                colors.destructive.copy(
                    alpha = if (darkMode) 0.2f else 0.1f,
                ).compositeOver(colors.background)
            assertColor(expected, pixels[pixels.width / 2, 4])
            val content = composeRule.onNodeWithTag("content", useUnmergedTree = true).captureToImage().toPixelMap()
            assertColor(colors.destructive, content[4, 4])
        }
        check(false)
        composeRule.runOnIdle { dark = true }
        check(true)
    }

    @Test
    fun smallLabelUsesNovaTypographyAndThemeFont() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, fontFamily = FontFamily.Monospace, minTouchTarget = null) {
                Button(size = ButtonSize.Xs) {
                    Text(modifier = Modifier.testTag("label")) { "Button" }
                }
            }
        }
        val results = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithTag("label", useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
        val style = results.single().layoutInput.style
        assertEquals(12.sp, style.fontSize)
        assertEquals(FontWeight.Medium, style.fontWeight)
        assertEquals(FontFamily.Monospace, style.fontFamily)
    }

    @Test
    fun vegaLabelColorChangesWithThemeWithoutResize() = checkLabelColorOnThemeChange(StylePreset.Vega)

    @Test
    fun novaLabelColorChangesWithThemeWithoutResize() = checkLabelColorOnThemeChange(StylePreset.Nova)

    private fun checkLabelColorOnThemeChange(preset: StylePreset) {
        var dark by mutableStateOf(false)
        composeRule.setContent {
            Theme(preset = preset, isDark = dark, minTouchTarget = null) {
                Button {
                    Text(modifier = Modifier.testTag("label")) { "█" }
                }
            }
        }

        fun check(expected: Color) {
            val pixels = composeRule.onNodeWithTag("label", useUnmergedTree = true).captureToImage().toPixelMap()
            assertColor(expected, pixels[pixels.width / 2, pixels.height / 2])
        }
        check(BaseColor.Neutral.light.primaryForeground)
        composeRule.runOnIdle { dark = true }
        check(BaseColor.Neutral.dark.primaryForeground)
    }

    @Test
    fun smallDefaultIconUsesItsButtonSize() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Button(size = ButtonSize.Xs) { Icon(Check, modifier = Modifier.testTag("icon")) }
            }
        }
        composeRule.onNodeWithTag("icon", useUnmergedTree = true).assertWidthIsEqualTo(12.dp)
    }

    private fun assertColor(
        expected: Color,
        actual: Color,
    ) {
        assertTrue(
            abs(expected.red - actual.red) < 0.015f &&
                abs(expected.green - actual.green) < 0.015f &&
                abs(expected.blue - actual.blue) < 0.015f,
            "Expected $expected, got $actual",
        )
    }
}
