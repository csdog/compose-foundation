@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.toggle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.ButtonAnimation
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ToggleNovaParityTest : ScreenshotTest() {
    // Compare rendered pixels, since a graphics scale leaves semantics bounds unchanged.
    private fun assertPressMotion(
        preset: StylePreset,
        animation: ButtonAnimation?,
        scales: Boolean,
    ) {
        composeRule.setContent {
            Theme(preset = preset, minTouchTarget = null) {
                Box(Modifier.testTag("surface").background(Color.White).padding(8.dp)) {
                    val content: @androidx.compose.runtime.Composable () -> Unit = {
                        Box(Modifier.size(48.dp).background(Color.Black))
                    }
                    if (animation == null) {
                        Toggle(modifier = Modifier.testTag("toggle"), content = content)
                    } else {
                        Toggle(modifier = Modifier.testTag("toggle"), animation = animation, content = content)
                    }
                }
            }
        }

        fun blackPixels(): Int {
            val pixels = composeRule.onNodeWithTag("surface").captureToImage().toPixelMap()
            return (0 until pixels.height).sumOf { y ->
                (0 until pixels.width).count { x ->
                    val color = pixels[x, y]
                    color.red < .1f && color.green < .1f && color.blue < .1f
                }
            }
        }
        val before = blackPixels()
        composeRule.onNodeWithTag("toggle").performTouchInput { down(center) }
        composeRule.mainClock.advanceTimeBy(1000)
        val held = blackPixels()
        composeRule.onNodeWithTag("toggle").performTouchInput { cancel() }
        if (scales) {
            assertTrue(held < before, "Explicit/legacy scale must shrink rendered content: $before -> $held")
        } else {
            assertEquals(before, held, "Nova implicit motion must preserve rendered geometry")
        }
    }

    @Test fun novaImplicitPressPreservesPixels() = assertPressMotion(StylePreset.Nova, null, false)

    @Test fun novaExplicitScaleStillAnimates() = assertPressMotion(StylePreset.Nova, ButtonAnimation.Scale, true)

    @Test fun legacyImplicitScaleStillAnimates() = assertPressMotion(StylePreset.Vega, null, true)

    @Test
    fun transparentSurfaceAndSelectedColorSurvivePointerDownAndInvalidRing() {
        var dark by mutableStateOf(false)
        var selected by mutableStateOf(false)
        var invalid by mutableStateOf(false)
        var variant by mutableStateOf<ToggleVariant>(ToggleVariant.Default)
        var muted = Color.Unspecified
        val surface = Color(0xfff08050)
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                muted = currentTheme.colors.muted
                Box(Modifier.background(surface)) {
                    Toggle(
                        pressed = selected,
                        onPressedChange = {},
                        modifier = Modifier.testTag("toggle"),
                        variant = variant,
                        animation = ButtonAnimation.None,
                        invalid = invalid,
                    ) { Box(Modifier.size(10.dp)) }
                }
            }
        }

        fun assertInterior(expected: Color) {
            val pixels = composeRule.onNodeWithTag("toggle").captureToImage().toPixelMap()
            val actual = pixels[pixels.width / 2, pixels.height / 2]
            assertEquals(expected.red, actual.red, .01f)
            assertEquals(expected.green, actual.green, .01f)
            assertEquals(expected.blue, actual.blue, .01f)
        }
        for (isDark in listOf(false, true)) {
            for (v in listOf(ToggleVariant.Default, ToggleVariant.Outline)) {
                for (isInvalid in listOf(false, true)) {
                    composeRule.runOnIdle {
                        dark = isDark
                        variant = v
                        invalid = isInvalid
                        selected = false
                    }
                    assertInterior(surface)
                    composeRule.onNodeWithTag("toggle").performTouchInput { down(center) }
                    assertInterior(surface)
                    composeRule.onNodeWithTag("toggle").performTouchInput { cancel() }
                    composeRule.runOnIdle { selected = true }
                    assertInterior(muted)
                    composeRule.onNodeWithTag("toggle").performTouchInput { down(center) }
                    assertInterior(muted)
                    composeRule.onNodeWithTag("toggle").performTouchInput { cancel() }
                }
            }
        }
    }

    @Test
    fun hoverAndCheckedTransitionsStayWithinCompositedEndpoints() {
        var dark by mutableStateOf(false)
        var selected by mutableStateOf(false)
        var variant by mutableStateOf<ToggleVariant>(ToggleVariant.Default)
        var surface = Color.Unspecified
        var muted = Color.Unspecified
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                surface = currentTheme.colors.background
                muted = currentTheme.colors.muted
                Box(Modifier.background(surface)) {
                    Toggle(
                        pressed = selected,
                        onPressedChange = {},
                        modifier = Modifier.testTag("toggle"),
                        variant = variant,
                    ) { Box(Modifier.size(16.dp)) }
                }
            }
        }
        composeRule.mainClock.autoAdvance = false

        fun pixel(): Color {
            val pixels = composeRule.onNodeWithTag("toggle").captureToImage().toPixelMap()
            return pixels[pixels.width / 2, pixels.height / 2]
        }

        fun transition(
            requireIntermediate: Boolean = true,
            change: () -> Unit,
        ) {
            val initial = pixel()
            change()
            val samples = mutableListOf<Color>()
            repeat(20) {
                composeRule.mainClock.advanceTimeBy(16)
                val actual = pixel()
                samples += actual
                for ((value, start, end) in listOf(
                    Triple(actual.red, surface.red, muted.red),
                    Triple(actual.green, surface.green, muted.green),
                    Triple(actual.blue, surface.blue, muted.blue),
                )) {
                    assertTrue(
                        value >= minOf(start, end) - .005f && value <= maxOf(start, end) + .005f,
                        "Transition escaped endpoint envelope: $actual between $surface and $muted",
                    )
                }
            }
            // Allow two display frames for interaction delivery/recomposition.
            // Nova must settle by 192ms; the former default spring remains in flight.
            val destination = if (initial == surface) muted else surface
            assertEquals(destination, samples[11], "Nova color transition must settle within 150ms plus two frames")
            if (requireIntermediate) {
                assertTrue(
                    samples.any { sample ->
                        listOf(
                            Triple(sample.red, surface.red, muted.red),
                            Triple(sample.green, surface.green, muted.green),
                            Triple(sample.blue, surface.blue, muted.blue),
                        ).any { (value, start, end) ->
                            value > minOf(start, end) + .005f && value < maxOf(start, end) - .005f
                        }
                    },
                    "Must capture a pixel strictly between composited endpoints",
                )
            }
        }
        for (isDark in listOf(false, true)) {
            for (v in listOf(ToggleVariant.Default, ToggleVariant.Outline)) {
                composeRule.runOnIdle {
                    dark = isDark
                    variant = v
                }
                composeRule.mainClock.advanceTimeBy(1000)
                assertEquals(surface, pixel())
                transition { composeRule.onNodeWithTag("toggle").performMouseInput { enter(center) } }
                assertEquals(muted, pixel())
                transition { composeRule.onNodeWithTag("toggle").performMouseInput { exit() } }
                assertEquals(surface, pixel())
                // Checked changes must animate through recomposition as hover does.
                transition { composeRule.runOnIdle { selected = true } }
                assertEquals(muted, pixel())
                transition { composeRule.runOnIdle { selected = false } }
                assertEquals(surface, pixel())
            }
        }
    }

    @Test
    fun originalPositionalContentCallsCompileAndRetainUncontrolledState() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                // This is the public positional signature from before iconPlacement was added.
                Toggle(
                    null, null, Modifier.testTag("toggle"), true, false,
                    ToggleVariant.Default, ToggleSize.Md, ButtonAnimation.None,
                    "Legacy caller", false, Style, { Box(Modifier.size(10.dp)) },
                )
            }
        }
        composeRule.onNodeWithTag("toggle").assertIsOff().performClick().assertIsOn()
    }
}
