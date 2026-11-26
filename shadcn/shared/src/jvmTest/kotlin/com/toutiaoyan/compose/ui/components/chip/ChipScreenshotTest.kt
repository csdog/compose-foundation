@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.chip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test

class ChipScreenshotTest : ScreenshotTest() {
    @Test fun selectableChipPublishesItsCheckedState() {
        var selected by mutableStateOf(false)
        setThemedContent {
            Chip("Filter", onClick = { selected = !selected }, modifier = Modifier.testTag("chip"), selected = selected)
        }

        val chip = composeRule.onNodeWithTag("chip")
        chip.assertIsOff().performClick()
        chip.assertIsOn().assertIsSelected()
        chip.performClick()
        chip.assertIsOff()
    }

    private fun states(darkTheme: Boolean) {
        snapshot("chip_states", darkTheme = darkTheme) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Chip(label = "Default", selected = false)
                Chip(label = "Selected", selected = true)
                Chip(label = "Outline", variant = ChipVariant.Outline)
                Chip(label = "Disabled", enabled = false)
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)

    @Test
    fun focused_light() {
        snapshotFocused("chip_focused", focusTag = "chip", darkTheme = false) {
            Chip(label = "Focus me", onClick = {}, modifier = Modifier.testTag("chip"))
        }
    }

    @Test
    fun focused_dark() {
        snapshotFocused("chip_focused", focusTag = "chip", darkTheme = true) {
            Chip(label = "Focus me", onClick = {}, modifier = Modifier.testTag("chip"))
        }
    }
}
