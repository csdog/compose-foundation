@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.BaseColor
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import kotlin.test.*

class ButtonInvalidStateTest {
    @get:Rule val rule = createComposeRule()

    @Test fun invalidBorderAndRingAreVisibleWithoutFocusAndClearWithoutResizing() {
        var dark by mutableStateOf(false)
        var invalid by mutableStateOf(false)
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                Box(Modifier.background(currentTheme.colors.background).testTag("surface").padding(24.dp)) {
                    Button("Invalid action", modifier = Modifier.testTag("button"), invalid = invalid)
                }
            }
        }
        val initial = rule.onNodeWithTag("button").getUnclippedBoundsInRoot()
        for (isDark in listOf(false, true)) {
            rule.runOnIdle {
                dark = isDark
                invalid = true
            }
            val colors = if (isDark) BaseColor.Neutral.dark else BaseColor.Neutral.light
            val pixels = rule.onNodeWithTag("surface").captureToImage().toPixelMap()
            val y = pixels.height / 2
            val ring = colors.destructive.copy(alpha = if (isDark) .4f else .2f).compositeOver(colors.background)
            checkColor(ring, pixels[22, y])
            // CSS bg-clip-padding leaves the page surface beneath a translucent border.
            val border = colors.destructive.copy(alpha = if (isDark) .5f else 1f).compositeOver(colors.background)
            checkColor(border, pixels[24, y])
            rule.onNodeWithText("Invalid action").assertIsNotFocused()
            assertEquals(
                "Invalid button",
                rule.onNodeWithTag("button").fetchSemanticsNode().config.getOrNull(SemanticsProperties.Error),
            )
            assertEquals(initial, rule.onNodeWithTag("button").getUnclippedBoundsInRoot())
            rule.runOnIdle { invalid = false }
            assertNull(rule.onNodeWithTag("button").fetchSemanticsNode().config.getOrNull(SemanticsProperties.Error))
            val cleared = rule.onNodeWithTag("surface").captureToImage().toPixelMap()
            checkColor(colors.background, cleared[22, y])
        }
    }

    @Test fun explicitStyleCanOverrideTheInvalidBorder() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Box(Modifier.testTag("surface").padding(24.dp)) {
                    Button("Override", invalid = true, style = Style { border(Color.Green) })
                }
            }
        }
        val pixels = rule.onNodeWithTag("surface").captureToImage().toPixelMap()
        checkColor(Color.Green, pixels[24, pixels.height / 2])
        rule.onNodeWithText("Override").assertExists()
    }

    @Test fun clippedBackgroundKeepsBrushAlphaShapeAndCallerDimensions() {
        var dimmed by mutableStateOf(false)
        var circle by mutableStateOf(false)
        var wide by mutableStateOf(false)
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Box(Modifier.background(Color.White).testTag("surface").padding(24.dp)) {
                    Button(
                        modifier = Modifier.testTag("button"),
                        size = ButtonSize.Icon,
                        disabled = dimmed,
                        style =
                            Style {
                                background(Brush.linearGradient(listOf(Color.Blue, Color.Blue)))
                                if (circle) shape(CircleShape)
                                if (wide) width(64.dp)
                            },
                    ) { Box(Modifier.size(8.dp)) }
                }
            }
        }

        fun checkFill(color: Color) {
            val pixels = rule.onNodeWithTag("surface").captureToImage().toPixelMap()
            val y = pixels.height / 2
            checkColor(Color.White, pixels[24, y])
            checkColor(color, pixels[26, y])
        }
        rule.onNodeWithTag("button").assertWidthIsEqualTo(32.dp).assertHeightIsEqualTo(32.dp)
        checkFill(Color.Blue)
        rule.runOnIdle { dimmed = true }
        checkFill(Color.Blue.copy(alpha = .5f).compositeOver(Color.White))
        rule.runOnIdle {
            dimmed = false
            circle = true
            wide = true
        }
        rule.onNodeWithTag("button").assertWidthIsEqualTo(64.dp).assertHeightIsEqualTo(32.dp)
        checkFill(Color.Blue)
        val pixels = rule.onNodeWithTag("surface").captureToImage().toPixelMap()
        checkColor(Color.White, pixels[25, 25])
    }

    @Test fun fractionalDensityUsesTheSameRoundedPaddingAndCeiledBorderAsStyleMeasurement() {
        rule.setContent {
            CompositionLocalProvider(LocalDensity provides Density(1.25f)) {
                Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                    Box(Modifier.background(Color.White).testTag("surface").padding(24.dp)) {
                        Button(
                            size = ButtonSize.Sm,
                            style =
                                Style {
                                    background(Color.Blue)
                                    shape(RectangleShape)
                                },
                        ) {
                            Box(Modifier.size(10.dp))
                        }
                    }
                }
            }
        }
        val pixels = rule.onNodeWithTag("surface").captureToImage().toPixelMap()
        checkColor(Color.White, pixels[31, pixels.height / 2])
        checkColor(Color.Blue, pixels[32, pixels.height / 2])
    }

    private fun checkColor(
        expected: Color,
        actual: Color,
    ) {
        assertEquals(expected.red, actual.red, .025f)
        assertEquals(expected.green, actual.green, .025f)
        assertEquals(expected.blue, actual.blue, .025f)
    }
}
