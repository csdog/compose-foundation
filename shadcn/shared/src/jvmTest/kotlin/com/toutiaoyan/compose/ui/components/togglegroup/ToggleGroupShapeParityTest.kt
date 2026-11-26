@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.togglegroup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.toggle.ToggleVariant
import com.toutiaoyan.compose.ui.components.toggle.ToggleSize
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.Radius
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ToggleGroupShapeParityTest : ScreenshotTest() {
    @Test fun connectedDisabledMiddleKeepsPhysicalBorderInBothDirectionsAndPalettes() {
        var dark by mutableStateOf(false)
        var direction by mutableStateOf(LayoutDirection.Ltr)
        var orientation by mutableStateOf(ToggleGroupOrientation.Horizontal)
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                CompositionLocalProvider(LocalLayoutDirection provides direction) {
                    Box(Modifier.background(if (dark) Color.Black else Color.White)) {
                        ToggleGroup(
                            variant = ToggleVariant.Outline,
                            size = ToggleSize.Md,
                            spacing = 0.dp,
                            orientation = orientation,
                            modifier = Modifier.testTag("physical-group"),
                        ) {
                            ToggleGroupItem("a") {}
                            ToggleGroupItem("b", enabled = false, modifier = Modifier.testTag("middle")) {}
                            ToggleGroupItem("c") {}
                        }
                    }
                }
            }
        }
        for (isDark in listOf(false, true)) {
            for (layout in listOf(LayoutDirection.Ltr, LayoutDirection.Rtl)) {
                for (axis in ToggleGroupOrientation.entries) {
                    composeRule.runOnIdle {
                        dark = isDark
                        direction = layout
                        orientation = axis
                    }
                    val image = composeRule.onNodeWithTag("middle").captureToImage().toPixelMap()
                    val horizontal = axis == ToggleGroupOrientation.Horizontal
                    val center = image[image.width / 2, image.height / 2]

                    fun differs(
                        x: Int,
                        y: Int,
                    ): Boolean = image[x, y] != center
                    val context = "$isDark/$layout/$axis"
                    // Physical left/top is removed, even when RTL reverses item placement.
                    assertEquals(!horizontal, differs(0, image.height / 2), "left $context")
                    assertTrue(differs(image.width - 1, image.height / 2), "right $context")
                    assertEquals(horizontal, differs(image.width / 2, 0), "top $context")
                    assertTrue(differs(image.width / 2, image.height - 1), "bottom $context")
                    val group = composeRule.onNodeWithTag("physical-group").captureToImage()
                    assertEquals(image.width * if (horizontal) 3 else 1, group.width, "width $context")
                    assertEquals(image.height * if (horizontal) 1 else 3, group.height, "height $context")
                }
            }
        }
    }

    @Test fun legacyItemOutlineRemovesLogicalLeadingBorderInRtl() {
        composeRule.setContent {
            Theme(preset = StylePreset.Vega, isDark = false, minTouchTarget = null) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    ToggleGroup(spacing = 0.dp) {
                        ToggleGroupItem("a", variant = ToggleVariant.Outline) {}
                        ToggleGroupItem(
                            "b",
                            modifier = Modifier.testTag("legacy-middle"),
                            variant = ToggleVariant.Outline,
                        ) {}
                        ToggleGroupItem("c", variant = ToggleVariant.Outline) {}
                    }
                }
            }
        }

        val image = composeRule.onNodeWithTag("legacy-middle").captureToImage().toPixelMap()
        val y = image.height / 2
        val center = image[image.width / 2, y]
        assertTrue(image[0, y] != center, "Legacy RTL keeps its physical left border")
        assertEquals(center, image[image.width - 1, y], "Legacy RTL removes its physical right border")
    }

    @Test fun connectedRtlEndsKeepPhysicalCornersWithDisabledAndFocusedItems() {
        var dark by mutableStateOf(false)
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Box(Modifier.background(Color.Red)) {
                        ToggleGroup(variant = ToggleVariant.Outline, spacing = 0.dp) {
                            ToggleGroupItem(
                                "a",
                                modifier = Modifier.testTag("rtl-first"),
                                style = Style { background(Color.Green) },
                            ) {}
                            ToggleGroupItem("b") {}
                            ToggleGroupItem("c", disabled = true, modifier = Modifier.testTag("rtl-last")) {}
                        }
                    }
                }
            }
        }

        val first = composeRule.onNodeWithTag("rtl-first")
        val last = composeRule.onNodeWithTag("rtl-last")
        assertTrue(last.fetchSemanticsNode().boundsInRoot.right <= first.fetchSemanticsNode().boundsInRoot.left)
        val unfocused = first.captureToImage().toPixelMap()
        assertEquals(Color.Red, unfocused[0, 0], "First item rounds its physical left")
        assertTrue(unfocused[unfocused.width - 1, 0] != Color.Red, "First item keeps a square physical right")
        first.requestFocus().assertIsFocused()
        for (isDark in listOf(false, true)) {
            composeRule.runOnIdle { dark = isDark }
            first.assertIsFocused()
            val focused = first.captureToImage().toPixelMap()
            assertTrue(focused[1, 1] != Color.Green, "Focused first item rounds its physical left")
            assertEquals(Color.Green, focused[focused.width - 2, 1], "Focused first item keeps its square right")
            val disabled = last.captureToImage().toPixelMap()
            assertTrue(disabled[0, 0] != Color.Red, "Disabled last item keeps its square left")
            assertEquals(Color.Red, disabled[disabled.width - 1, 0], "Disabled last item rounds its physical right")
        }
    }

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
