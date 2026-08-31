@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.styles.focusRingShadow
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.Ring
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test

/**
 * Documented, repeatable proof that [StylePreset] actually drives per-style
 * shape/ring differences end to end -- this is the durable replacement for eyeballing
 * a live browser toggle, which can't be committed to git or diffed in a PR.
 *
 * Two kinds of capture per theme:
 * 1. `style_matrix_ring_swatch`: all 8 presets' ring token (width/offset/corner) drawn
 *    forced-on in one composite image, side by side -- each swatch renders inside its
 *    own `Theme(preset = ..., isDark = ...)` subtree so `theme.focusRingShadow()`
 *    resolves that preset's own ring, not a shared one.
 * 2. `style_matrix_button_focused_<preset>`: the *real* `Button`, focused via real
 *    `requestFocus()`, one capture per preset -- proves the wiring through the actual
 *    component, not just a swatch.
 */
class StylePresetMatrixTest : ScreenshotTest() {
    @Test
    fun ring_swatch_light() = ringSwatch(darkTheme = false)

    @Test
    fun ring_swatch_dark() = ringSwatch(darkTheme = true)

    @Test
    fun button_focused_light() = buttonFocusedPerPreset(darkTheme = false)

    @Test
    fun button_focused_dark() = buttonFocusedPerPreset(darkTheme = true)

    @Test
    fun ring_toggle_light() = ringToggle(darkTheme = false)

    @Test
    fun ring_toggle_dark() = ringToggle(darkTheme = true)

    /**
     * [Ring.enabled] defaults to `false` (see `Ring.kt`'s doc comment) --
     * `ring = ..copy(enabled = true)` must still turn it back on for a focused component,
     * and the plain default (no override) must stay ring-less. Focus is a single
     * tree-wide concept in Compose UI testing, not scoped per composable, so this
     * captures one theme's focused button per composition rather than two side by side
     * in the same tree.
     */
    private fun ringToggle(darkTheme: Boolean) {
        snapshotFocused("style_matrix_ring_enabled", focusTag = "ring_on", darkTheme = darkTheme) {
            Theme(isDark = darkTheme, ring = StylePreset.Vega.ring.copy(enabled = true)) {
                Button(onClick = {}, modifier = Modifier.testTag("ring_on")) { Text { "On" } }
            }
        }
        snapshotFocused("style_matrix_ring_disabled", focusTag = "ring_off", darkTheme = darkTheme) {
            Theme(isDark = darkTheme) {
                Button(onClick = {}, modifier = Modifier.testTag("ring_off")) { Text { "Off" } }
            }
        }
    }

    private fun ringSwatch(darkTheme: Boolean) {
        snapshot("style_matrix_ring_swatch", darkTheme = darkTheme) {
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                StylePreset.entries.forEach { preset -> PresetRingSwatch(preset, darkTheme) }
            }
        }
    }

    /** One composition holding all 8 presets' own real, independently-themed [Button]s. */
    private fun buttonFocusedPerPreset(darkTheme: Boolean) {
        composeRule.setContent {
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                StylePreset.entries.forEach { preset ->
                    ThemedPresetBox(preset, darkTheme) {
                        Button(
                            onClick = {},
                            modifier = Modifier.testTag("style_matrix_btn_${preset.name}"),
                        ) { Text { "Focus" } }
                    }
                }
            }
        }
        StylePreset.entries.forEach { preset ->
            composeRule.onNodeWithTag("style_matrix_btn_${preset.name}").requestFocus()
            composeRule.waitForIdle()
            captureNamed("style_matrix_button_focused_${preset.name.lowercase()}", darkTheme)
        }
    }
}

@Composable
private fun PresetRingSwatch(
    preset: StylePreset,
    darkTheme: Boolean,
) {
    Theme(preset = preset, isDark = darkTheme) {
        val theme = currentTheme
        val swatchStyle =
            Style {
                background(theme.colors.primary)
                shape(RoundedCornerShape(twRadius.md))
                dropShadow(theme.focusRingShadow())
            }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) { preset.label }
            Box(
                modifier =
                    Modifier
                        .padding(top = 8.dp)
                        .size(64.dp, 36.dp)
                        .styleable(remember { MutableStyleState(MutableInteractionSource()) }, swatchStyle),
            )
        }
    }
}

@Composable
private fun ThemedPresetBox(
    preset: StylePreset,
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    Theme(preset = preset, isDark = darkTheme) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) { preset.label }
            Box(modifier = Modifier.padding(top = 8.dp).width(90.dp)) { content() }
        }
    }
}
