package com.toutiaoyan.compose.ui.components.badge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.spinner.Spinner
import com.toutiaoyan.compose.ui.icons.Check
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.BaseColor
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BadgeNovaParityTest : ScreenshotTest() {
    private fun matrix(dark: Boolean) =
        snapshot("badge_nova_parity", darkTheme = dark, stylePreset = StylePreset.Nova) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        BadgeVariant.Default,
                        BadgeVariant.Secondary,
                        BadgeVariant.Outline,
                        BadgeVariant.Destructive,
                        BadgeVariant.Ghost,
                        BadgeVariant.Link,
                    ).forEach {
                        Badge(it::class.simpleName.orEmpty(), variant = it)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Badge {
                        Icon(Check)
                        Text { "Verified" }
                    }
                    Badge(variant = BadgeVariant.Destructive) {
                        Icon(Check)
                        Text { "Destructive" }
                    }
                }
            }
        }

    @Test fun lightMatrix() = matrix(false)

    @Test fun darkMatrix() = matrix(true)

    @Test
    fun badgeUsesInlineDimensionsAndDoesNotOverlayItsSlots() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Badge(modifier = Modifier.testTag("badge")) {
                    Icon(Check, modifier = Modifier.testTag("icon"))
                    Text(modifier = Modifier.testTag("label")) { "Verified" }
                }
            }
        }
        composeRule.onNodeWithTag("badge").assertHeightIsEqualTo(20.dp)
        composeRule.onNodeWithTag("icon").assertWidthIsEqualTo(12.dp).assertHeightIsEqualTo(12.dp)
        val icon = composeRule.onNodeWithTag("icon").fetchSemanticsNode().boundsInRoot
        val label = composeRule.onNodeWithTag("label").fetchSemanticsNode().boundsInRoot
        assertEquals(4f, label.left - icon.right)
        val results = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithTag("label").performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
        assertEquals(12.sp, results.single().layoutInput.style.fontSize)
        assertEquals(FontWeight.Medium, results.single().layoutInput.style.fontWeight)
    }

    @Test
    fun destructiveTintUpdatesAcrossThemes() {
        var dark by mutableStateOf(false)
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark) {
                val colors = if (dark) BaseColor.Neutral.dark else BaseColor.Neutral.light
                Box(Modifier.background(colors.background)) {
                    Badge("Error", variant = BadgeVariant.Destructive, modifier = Modifier.testTag("badge"))
                }
            }
        }

        fun check(darkMode: Boolean) {
            val colors = if (darkMode) BaseColor.Neutral.dark else BaseColor.Neutral.light
            val pixels = composeRule.onNodeWithTag("badge").captureToImage().toPixelMap()
            val expected =
                colors.destructive.copy(
                    alpha = if (darkMode) 0.2f else 0.1f,
                ).compositeOver(colors.background)
            val actual = pixels[pixels.width / 2, 2]
            assertTrue(
                abs(expected.red - actual.red) < 0.015f &&
                    abs(expected.green - actual.green) < 0.015f &&
                    abs(expected.blue - actual.blue) < 0.015f,
            )
        }
        check(false)
        composeRule.runOnIdle { dark = true }
        check(true)
    }

    @Test
    fun spinnerInheritsBadgeSizeAndContentColor() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                Badge { Spinner(modifier = Modifier.testTag("spinner")) }
            }
        }
        composeRule.onNodeWithTag("spinner").assertWidthIsEqualTo(12.dp).assertHeightIsEqualTo(12.dp)
        val pixels = composeRule.onNodeWithTag("spinner").captureToImage().toPixelMap()
        val hasForegroundStroke =
            (0 until pixels.width).any { x ->
                (0 until pixels.height).any { y ->
                    pixels[x, y].let { it.red > 0.9f && it.green > 0.9f && it.blue > 0.9f }
                }
            }
        assertTrue(
            hasForegroundStroke,
            "Spinner should use the badge's light foreground on its dark primary background",
        )
    }

    @Test
    fun ghostDoesNotPaintAPermanentMutedBackground() {
        val background = Color(0xFF447788)
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                Box(Modifier.background(background)) {
                    Badge("Ghost", variant = BadgeVariant.Ghost, modifier = Modifier.testTag("badge"))
                }
            }
        }
        val pixels = composeRule.onNodeWithTag("badge").captureToImage().toPixelMap()
        assertEquals(background, pixels[pixels.width / 2, 2])
    }
}
