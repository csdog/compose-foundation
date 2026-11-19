@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.buttongroup

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals

class ButtonGroupScreenshotTest : ScreenshotTest() {
    @Test fun keyedReorderMovesTheOutlineOverlapToTheNewTrailingItem() {
        var reversed by mutableStateOf(false)
        setThemedContent(stylePreset = StylePreset.Nova) {
            ButtonGroup(Modifier.testTag("group")) {
                val ids = if (reversed) listOf("second", "first") else listOf("first", "second")
                ids.forEach { id ->
                    key(id) {
                        ButtonGroupItem(
                            "Label",
                            onClick = {},
                            modifier = Modifier.testTag(id),
                            variant = ButtonVariant.Outline,
                        )
                    }
                }
            }
        }

        fun bounds(tag: String) = composeRule.onNodeWithTag(tag).getUnclippedBoundsInRoot()
        assertEquals(bounds("group").left, bounds("first").left)
        assertEquals(bounds("first").right - 1.dp, bounds("second").left)
        composeRule.runOnIdle { reversed = true }
        assertEquals(bounds("group").left, bounds("second").left)
        assertEquals(bounds("second").right - 1.dp, bounds("first").left)
    }

    private fun states(darkTheme: Boolean) {
        snapshot("button_group_states", darkTheme = darkTheme) {
            ButtonGroup {
                ButtonGroupItem("Copy", onClick = {})
                ButtonGroupItem("Share", onClick = {})
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)

    private fun withLabel(darkTheme: Boolean) {
        snapshot("button_group_with_label", darkTheme = darkTheme) {
            // Mirrors app:shared's ButtonGroupDoc.kt "With label" example exactly.
            val rounded = twRadius.md
            ButtonGroup(style = rememberButtonGroupOutlineStyle()) {
                ButtonGroupText("https://", topStart = rounded, bottomStart = rounded)
                ButtonGroupSeparator()
                Button(
                    "example.com",
                    onClick = {},
                    variant = ButtonVariant.Ghost,
                    style = Style { shape(RoundedCornerShape(0.dp, rounded, rounded, 0.dp)) },
                )
            }
        }
    }

    @Test
    fun with_label_light() = withLabel(darkTheme = false)

    @Test
    fun with_label_dark() = withLabel(darkTheme = true)

    private fun withLabelFocused(darkTheme: Boolean) {
        snapshotFocused("button_group_with_label_focused", focusTag = "group-button", darkTheme = darkTheme) {
            val rounded = twRadius.md
            ButtonGroup(style = rememberButtonGroupOutlineStyle()) {
                ButtonGroupText("https://", topStart = rounded, bottomStart = rounded)
                ButtonGroupSeparator()
                Button(
                    "example.com",
                    onClick = {},
                    variant = ButtonVariant.Ghost,
                    style = Style { shape(RoundedCornerShape(0.dp, rounded, rounded, 0.dp)) },
                    modifier = Modifier.testTag("group-button"),
                )
            }
        }
    }

    @Test
    fun with_label_focused_light() = withLabelFocused(darkTheme = false)

    @Test
    fun with_label_focused_dark() = withLabelFocused(darkTheme = true)
}
