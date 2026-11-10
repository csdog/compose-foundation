@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.togglegroup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.toggle.ToggleSize
import com.toutiaoyan.compose.ui.components.toggle.ToggleVariant
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertTrue

class ToggleGroupItemPresetsTest : ScreenshotTest() {
    @Test fun connectedItemOutlineHasSingleSeamsWithoutRootVariant() {
        var preset by mutableStateOf(StylePreset.Vega)
        var vertical by mutableStateOf(false)
        var dark by mutableStateOf(false)
        var rtl by mutableStateOf(false)
        var mixed by mutableStateOf(false)
        var explicitRoot by mutableStateOf(false)
        composeRule.setContent {
            Theme(preset = preset, isDark = dark, minTouchTarget = null) {
                CompositionLocalProvider(
                    LocalLayoutDirection provides if (rtl) LayoutDirection.Rtl else LayoutDirection.Ltr,
                ) {
                    ToggleGroup(
                        modifier = Modifier.background(Color.Red).testTag("group"),
                        spacing = 0.dp,
                        variant = if (explicitRoot) ToggleVariant.Outline else null,
                        orientation =
                            if (vertical) ToggleGroupOrientation.Vertical else ToggleGroupOrientation.Horizontal,
                    ) {
                        repeat(3) { index ->
                            ToggleGroupItem(
                                "$index",
                                "",
                                modifier = Modifier.testTag("item$index"),
                                variant = if (mixed && index == 0) ToggleVariant.Default else ToggleVariant.Outline,
                            )
                        }
                    }
                }
            }
        }
        for (nextExplicitRoot in listOf(false, true)) {
            composeRule.runOnIdle { explicitRoot = nextExplicitRoot }
            for (nextMixed in if (nextExplicitRoot) listOf(false) else listOf(false, true)) {
                composeRule.runOnIdle { mixed = nextMixed }
                val presets =
                    if (nextExplicitRoot) listOf(StylePreset.Nova) else listOf(StylePreset.Vega, StylePreset.Nova)
                for (nextPreset in presets) {
                    for (nextVertical in listOf(false, true)) {
                        for (nextDark in listOf(false, true)) {
                            for (nextRtl in listOf(false, true)) {
                                composeRule.runOnIdle {
                                    preset = nextPreset
                                    vertical = nextVertical
                                    dark = nextDark
                                    rtl = nextRtl
                                }
                                val root = composeRule.onNodeWithTag("group")
                                val bounds = root.fetchSemanticsNode().boundsInRoot
                                val pixels = root.captureToImage().toPixelMap()
                                for (index in 1..2) {
                                    val item = composeRule.onNodeWithTag("item$index").fetchSemanticsNode().boundsInRoot
                                    val seam =
                                        if (vertical) {
                                            (item.top - bounds.top).toInt()
                                        } else if (rtl) {
                                            (item.right - bounds.left).toInt() - 1
                                        } else {
                                            (item.left - bounds.left).toInt()
                                        }

                                    fun red(offset: Int): Boolean {
                                        val color =
                                            if (vertical) {
                                                pixels[pixels.width / 2, seam + offset]
                                            } else {
                                                pixels[seam + offset, pixels.height / 2]
                                            }
                                        return color.red > .99f && color.green < .01f && color.blue < .01f
                                    }
                                    // Both perpendicular edges must continue across the removed leading side.
                                    for (distance in 0..2) {
                                        val along = seam + if (!vertical && rtl) -distance else distance
                                        for (far in listOf(false, true)) {
                                            val pixel =
                                                if (vertical) {
                                                    pixels[if (far) pixels.width - 1 else 0, along]
                                                } else {
                                                    pixels[along, if (far) pixels.height - 1 else 0]
                                                }
                                            assertTrue(
                                                pixel.green > .01f || pixel.blue > .01f || pixel.red < .99f,
                                                "Perimeter gap at $distance far=$far mixed=$mixed " +
                                                    "($preset vertical=$vertical dark=$dark rtl=$rtl item=$index)",
                                            )
                                        }
                                    }
                                    val colored = (-2..2).count { !red(it) }
                                    val expected = if (mixed && index == 1) 0 else 1
                                    assertTrue(
                                        colored == expected,
                                        "Expected $expected border pixels, got $colored mixed=$mixed " +
                                            "($preset vertical=$vertical dark=$dark rtl=$rtl item=$index)",
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test fun itemFallbackAndExplicitRootPrecedenceUpdateLive() {
        var override by mutableStateOf(false)
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, isDark = false, minTouchTarget = null) {
                Column(Modifier.background(Color.Red)) {
                    ToggleGroup(
                        variant = if (override) ToggleVariant.Default else null,
                        size = if (override) ToggleSize.Lg else null,
                    ) {
                        ToggleGroupItem(
                            "a",
                            "Alpha",
                            modifier = Modifier.testTag("item"),
                            variant = ToggleVariant.Outline,
                            size = ToggleSize.Sm,
                        )
                        ToggleGroupItem("b", "Beta", modifier = Modifier.testTag("fallback"))
                    }
                    ToggleGroup {
                        ToggleGroupItem(
                            "c",
                            modifier = Modifier.testTag("content"),
                            size = ToggleSize.Sm,
                        ) { Box {} }
                    }
                }
            }
        }

        fun redAtEdge(): Float {
            val pixels = composeRule.onNodeWithTag("item").captureToImage().toPixelMap()
            return pixels[0, pixels.height / 2].red
        }
        composeRule.onNodeWithTag("item").assertHeightIsEqualTo(28.dp)
        composeRule.onNodeWithTag("fallback").assertHeightIsEqualTo(32.dp)
        composeRule.onNodeWithTag("content").assertHeightIsEqualTo(28.dp)
        assertTrue(redAtEdge() < .98f, "Item Outline must render its border when root variant is omitted")
        composeRule.runOnIdle { override = true }
        composeRule.onNodeWithTag("item").assertHeightIsEqualTo(36.dp)
        composeRule.onNodeWithTag("fallback").assertHeightIsEqualTo(36.dp)
        assertTrue(redAtEdge() > .99f, "Explicit root Default must override item Outline with a transparent edge")
        composeRule.runOnIdle { override = false }
        composeRule.onNodeWithTag("item").assertHeightIsEqualTo(28.dp)
        assertTrue(redAtEdge() < .98f, "Removing root override must restore item Outline")
    }
}
