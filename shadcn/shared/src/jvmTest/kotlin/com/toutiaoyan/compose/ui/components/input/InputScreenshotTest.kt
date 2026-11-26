@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.input

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasRequestFocusAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test

class InputScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("input_states", darkTheme = darkTheme) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(InputVariant.Default, InputVariant.Filled, InputVariant.Ghost).forEach { variant ->
                    Input(
                        value = "",
                        onValueChange = {},
                        placeholder = variant::class.simpleName,
                        variant = variant,
                        modifier = Modifier.width(200.dp),
                    )
                }
                Input(
                    value = "Invalid value",
                    onValueChange = {},
                    isError = true,
                    modifier = Modifier.width(200.dp),
                )
                Input(
                    value = "Disabled",
                    onValueChange = {},
                    enabled = false,
                    modifier = Modifier.width(200.dp),
                )
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)

    @Test
    fun focused_light() {
        snapshotFocused("input_focused", focusTag = "tf", darkTheme = false) {
            Input(value = "", onValueChange = {}, modifier = Modifier.width(200.dp).testTag("tf"))
        }
    }

    @Test
    fun focused_dark() {
        snapshotFocused("input_focused", focusTag = "tf", darkTheme = true) {
            Input(value = "", onValueChange = {}, modifier = Modifier.width(200.dp).testTag("tf"))
        }
    }

    /**
     * [com.toutiaoyan.compose.ui.tokens.Ring.enabled] = false must NOT
     * silence the ring here -- unlike every other focusable component (which calls
     * `focusRing`), `InputStyles.kt` calls `focusRingAlways`, since the ring is the
     * primary visual cue that a field is the one currently receiving keystrokes.
     */
    @Test
    fun focused_with_ring_disabled_light() {
        composeRule.setContent {
            Theme(ring = StylePreset.Vega.ring.copy(enabled = false)) {
                Box(modifier = Modifier.background(currentTheme.colors.background).padding(24.dp)) {
                    Input(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier.width(200.dp).testTag("tf-ring-disabled"),
                    )
                }
            }
        }
        val focusTag = "tf-ring-disabled"
        val focusable = hasRequestFocusAction() and (hasTestTag(focusTag) or hasAnyAncestor(hasTestTag(focusTag)))
        composeRule.onNode(focusable, useUnmergedTree = true).requestFocus()
        composeRule.waitForIdle()
        captureNamed("input_focused_with_ring_disabled", darkTheme = false)
    }
}
