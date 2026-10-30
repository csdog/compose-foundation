package com.toutiaoyan.compose.ui.tokens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.*
import com.toutiaoyan.compose.ui.components.checkbox.Checkbox
import com.toutiaoyan.compose.ui.components.radiobutton.RadioButton
import com.toutiaoyan.compose.ui.components.switch.Switch
import com.toutiaoyan.compose.ui.components.inputgroup.*
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.select.*
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ThemeRoleRenderingTest : ScreenshotTest() {
    @Test fun controlsPaintTranslucentInputRoleAcrossBasesAndLiveThemeChanges() {
        var dark by mutableStateOf(false)
        var base by mutableStateOf(BaseColor.Neutral)
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, baseColor = base, isDark = dark, minTouchTarget = null) {
                Column(
                    Modifier.background(currentTheme.colors.popover).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Button("Outline", modifier = Modifier.testTag("button"), variant = ButtonVariant.Outline)
                    Input("", {}, Modifier.width(180.dp).testTag("input"))
                    Checkbox(false, {}, Modifier.testTag("checkbox"))
                    RadioButton(false, {}, Modifier.testTag("radio"))
                    Switch(false, {}, Modifier.testTag("switch"))
                    InputGroup(Modifier.width(180.dp).testTag("group")) { InputGroupInput("", {}) }
                    Select(value = "One", onValueChange = {}) {
                        SelectTrigger(Modifier.width(180.dp).testTag("select"))
                        SelectContent { SelectItem("One") }
                    }
                }
            }
        }
        for (palette in BaseColor.entries) {
            for (darkMode in listOf(false, true, false)) {
                composeRule.runOnIdle {
                    base = palette
                    dark = darkMode
                }
                val surface = if (darkMode) palette.dark.popover else palette.light.popover
                // The independent source-fixture test proves the palette; this checks consumers
                // paint the CSS input/30 role over the surface instead of flattening its alpha.
                val expected = if (darkMode) Color.White.copy(alpha = .045f).compositeOver(surface) else surface
                for (tag in listOf("button", "input", "checkbox", "select", "radio", "switch", "group")) {
                    val pixels = composeRule.onNodeWithTag(tag).captureToImage().toPixelMap()
                    val actual =
                        pixels[
                            if (tag == "switch") pixels.width - 5 else pixels.width / 2,
                            if (tag in listOf("checkbox", "radio", "switch")) pixels.height / 2 else 4,
                        ]
                    val target =
                        if (tag != "switch") {
                            expected
                        } else if (darkMode) {
                            Color.White.copy(alpha = .12f).compositeOver(surface)
                        } else {
                            palette.light.input
                        }
                    assertTrue(
                        abs(target.red - actual.red) < .018f &&
                            abs(target.green - actual.green) < .018f &&
                            abs(target.blue - actual.blue) < .018f,
                        "$palette dark=$darkMode $tag: expected $target over $surface, got $actual",
                    )
                }
            }
        }
    }

    @Test fun lightInputControlsPreserveTheActualTintedSurface() {
        val surface = Color(0xFFCFD8FF)
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, isDark = false, minTouchTarget = null) {
                Column(Modifier.background(surface).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Input("", {}, Modifier.width(180.dp).testTag("input"))
                    Checkbox(false, {}, Modifier.testTag("checkbox"))
                    RadioButton(false, {}, Modifier.testTag("radio"))
                    Select(value = "One", onValueChange = {}) {
                        SelectTrigger(Modifier.width(180.dp).testTag("select"))
                        SelectContent { SelectItem("One") }
                    }
                    InputGroup(Modifier.width(180.dp).testTag("group")) { InputGroupInput("", {}) }
                }
            }
        }
        for (tag in listOf("input", "checkbox", "radio", "select", "group")) {
            val pixels = composeRule.onNodeWithTag(tag).captureToImage().toPixelMap()
            val actual = pixels[pixels.width / 2, if (tag in listOf("checkbox", "radio")) pixels.height / 2 else 4]
            assertEquals(surface, actual, "$tag must remain transparent in light mode")
        }
    }

    @Test fun disabledInputBackgroundUsesItsOpacityOverTheCurrentSurface() {
        val surface = Color(0xFF382744)
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, isDark = true, minTouchTarget = null) {
                Box(Modifier.background(surface).padding(16.dp)) {
                    Input("", {}, Modifier.width(180.dp).testTag("input"), disabled = true)
                }
            }
        }
        val pixels = composeRule.onNodeWithTag("input").captureToImage().toPixelMap()
        val actual = pixels[pixels.width / 2, 4]
        val expected = Color.White.copy(alpha = .15f * .8f * .5f).compositeOver(surface)
        assertTrue(
            abs(actual.red - expected.red) < .018f &&
                abs(actual.green - expected.green) < .018f &&
                abs(actual.blue - expected.blue) < .018f,
            "Disabled fill must combine input/80 with opacity-50 over the parent: $expected vs $actual",
        )
    }

    @Test fun presetsAndAccentChangesPreserveTheSharedBorderAndInputRoles() {
        var preset by mutableStateOf(StylePreset.Nova)
        var accent by mutableStateOf(Accent.Base)
        var base by mutableStateOf(BaseColor.Neutral)
        var dark by mutableStateOf(true)
        var border = Color.Unspecified
        var input = Color.Unspecified
        composeRule.setContent {
            Theme(preset = preset, accent = accent, baseColor = base, isDark = dark) {
                border = currentTheme.colors.border
                input = currentTheme.colors.input
            }
        }
        for (p in listOf(
            StylePreset.Nova,
            StylePreset.Vega,
        )) for (a in listOf(Accent.Base, Accent.Blue)) for (b in BaseColor.entries) {
            composeRule.runOnIdle {
                preset = p
                accent = a
                base = b
                dark = true
            }
            composeRule.runOnIdle {
                assertEquals(Color.White.copy(alpha = .1f), border)
                assertEquals(Color.White.copy(alpha = .15f), input)
            }
            composeRule.runOnIdle { dark = false }
            composeRule.runOnIdle {
                assertEquals(b.light.border, border)
                assertEquals(b.light.input, input)
            }
        }
    }
}
