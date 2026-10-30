@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.styles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

class CssStyleableTest : ScreenshotTest() {
    @Test fun inputFocusAndInvalidRingsDoNotTintTransparentInterior() {
        val surface = Color(0xFFCFD8FF)
        var invalid by mutableStateOf(false)
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, isDark = false, minTouchTarget = null) {
                Box(Modifier.background(surface).padding(16.dp).testTag("surface")) {
                    Input("", {}, Modifier.width(180.dp).testTag("input"), invalid = invalid)
                }
            }
        }

        fun check() {
            val pixels = composeRule.onNodeWithTag("input").captureToImage().toPixelMap()
            val actual = pixels[pixels.width / 2, 4]
            assertTrue(
                abs(surface.red - actual.red) < .012f &&
                    abs(surface.green - actual.green) < .012f &&
                    abs(surface.blue - actual.blue) < .012f,
                "Focus/error shadow must not fill the transparent interior: $surface vs $actual",
            )
        }
        check()
        composeRule.onNodeWithTag("input").requestFocus()
        check()
        composeRule.runOnIdle { invalid = true }
        check()
        composeRule.runOnIdle { invalid = false }
        check()
    }

    @Test fun changingOnlyShadowColorRepaintsWithoutAnimationTicks() {
        val tint = mutableStateOf(Color.Red)
        composeRule.setContent {
            Box(Modifier.testTag("surface").background(Color.White).padding(16.dp)) {
                Box(
                    Modifier.cssStyleable(
                        null,
                        Style {
                            width(80.dp)
                            height(40.dp)
                            shape(RoundedCornerShape(8.dp))
                            dropShadow(Shadow(radius = 0.dp, spread = 3.dp, color = tint.value))
                        },
                    ).testTag("box"),
                )
            }
        }

        fun check(expected: Color) {
            val pixels = composeRule.onNodeWithTag("surface").captureToImage().toPixelMap()
            val actual = pixels[pixels.width / 2, 14]
            assertTrue(
                abs(actual.red - expected.red) < .02f &&
                    abs(actual.green - expected.green) < .02f &&
                    abs(actual.blue - expected.blue) < .02f,
                "A shadow-only Style change must invalidate its renderer: $expected vs $actual",
            )
        }
        check(Color.Red)
        composeRule.mainClock.autoAdvance = false
        try {
            composeRule.runOnIdle { tint.value = Color.Blue }
            check(Color.Blue)
        } finally {
            composeRule.mainClock.autoAdvance = true
        }
    }

    @Test fun nativeOuterShadowFollowsTheFinalCallerProvidedGenericShape() {
        val surface = Color(0xFFCFD8FF)
        lateinit var mode: InputModeManager
        val triangle =
            GenericShape { size, _ ->
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(0f, size.height)
                close()
            }
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, isDark = false, minTouchTarget = null) {
                mode = LocalInputModeManager.current
                Box(Modifier.background(surface).padding(16.dp).testTag("surface")) {
                    Button(
                        "",
                        modifier = Modifier.testTag("button"),
                        style =
                            Style {
                                width(100.dp)
                                height(40.dp)
                                shape(triangle)
                                background(Color.Transparent)
                            },
                    )
                }
            }
        }
        composeRule.runOnIdle { mode.requestInputMode(InputMode.Keyboard) }
        composeRule.onNodeWithTag("button").requestFocus()
        val button = composeRule.onNodeWithTag("button").fetchSemanticsNode().boundsInRoot
        val parent = composeRule.onNodeWithTag("surface").fetchSemanticsNode().boundsInRoot
        val pixels = composeRule.onNodeWithTag("surface").captureToImage().toPixelMap()

        fun pixel(
            x: Int,
            y: Int,
        ) = pixels[(button.left - parent.left).toInt() + x, (button.top - parent.top).toInt() + y]

        fun matches(
            a: Color,
            b: Color,
        ) = abs(a.red - b.red) < .02f && abs(a.green - b.green) < .02f && abs(a.blue - b.blue) < .02f
        assertTrue(matches(surface, pixel(10, 10)), "The triangle interior remains transparent")
        assertTrue(matches(surface, pixel(90, 35)), "The ring must not follow the old rounded-rectangle shape")
        assertTrue(!matches(surface, pixel(55, 20)), "The native shadow must follow the triangle's outer diagonal")
    }
}
