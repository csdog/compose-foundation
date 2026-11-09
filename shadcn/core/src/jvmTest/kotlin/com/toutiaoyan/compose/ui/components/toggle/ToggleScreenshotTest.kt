@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.toggle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals

class ToggleScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("toggle_states", darkTheme = darkTheme) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(ToggleVariant.Default, ToggleVariant.Outline).forEach { variant ->
                    Toggle("Off", pressed = false, onPressedChange = {}, variant = variant)
                    Toggle("On", pressed = true, onPressedChange = {}, variant = variant)
                    Toggle(
                        "Disabled",
                        pressed = false,
                        onPressedChange = {},
                        variant = variant,
                        enabled = false,
                    )
                }
            }
        }
    }

    private fun sizes(darkTheme: Boolean) {
        snapshot("toggle_sizes", darkTheme = darkTheme) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Toggle("Sm", pressed = true, onPressedChange = {}, size = ToggleSize.Sm)
                Toggle("Md", pressed = true, onPressedChange = {}, size = ToggleSize.Md)
                Toggle("Lg", pressed = true, onPressedChange = {}, size = ToggleSize.Lg)
            }
        }
    }

    private fun invalid(darkTheme: Boolean) {
        snapshot("toggle_invalid", darkTheme = darkTheme) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Toggle(pressed = false, onPressedChange = {}, invalid = true) {
                    Text { "Default invalid" }
                }
                Toggle(
                    pressed = false,
                    onPressedChange = {},
                    variant = ToggleVariant.Outline,
                    invalid = true,
                ) {
                    Text { "Outline invalid" }
                }
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)

    @Test
    fun sizes_light() = sizes(darkTheme = false)

    @Test
    fun sizes_dark() = sizes(darkTheme = true)

    @Test
    fun invalid_light() = invalid(darkTheme = false)

    @Test
    fun invalid_dark() = invalid(darkTheme = true)

    @Test
    fun focused_light() {
        snapshotFocused("toggle_focused", focusTag = "tg", darkTheme = false) {
            Toggle(
                "Focus me",
                pressed = false,
                onPressedChange = {},
                modifier = Modifier.testTag("tg"),
            )
        }
    }

    @Test
    fun focused_dark() {
        snapshotFocused("toggle_focused", focusTag = "tg", darkTheme = true) {
            Toggle(
                "Focus me",
                pressed = false,
                onPressedChange = {},
                modifier = Modifier.testTag("tg"),
            )
        }
    }

    @Test
    fun icon_placement_uses_logical_asymmetric_padding() {
        var placement by mutableStateOf<ToggleIconPlacement?>(null)
        var direction by mutableStateOf(LayoutDirection.Ltr)
        var size by mutableStateOf<ToggleSize>(ToggleSize.Md)
        composeRule.setContent {
            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                    Toggle(
                        modifier = Modifier.testTag("toggle"),
                        pressed = false,
                        onPressedChange = {},
                        size = size,
                        iconPlacement = placement,
                    ) {
                        if (placement == ToggleIconPlacement.Start || placement == ToggleIconPlacement.Both) {
                            Box(Modifier.size(10.dp).testTag("start"))
                        }
                        Box(Modifier.size(10.dp).testTag("label"))
                        if (placement == ToggleIconPlacement.End || placement == ToggleIconPlacement.Both) {
                            Box(Modifier.size(10.dp).testTag("end"))
                        }
                    }
                }
            }
        }

        val iconPlacements =
            listOf(
                null,
                ToggleIconPlacement.Start,
                ToggleIconPlacement.End,
                ToggleIconPlacement.Both,
            )
        for (dir in LayoutDirection.entries) {
            for ((toggleSize, height) in listOf(
                ToggleSize.Sm to 28,
                ToggleSize.Md to 32,
                ToggleSize.Lg to 36,
            )) {
                for (edge in iconPlacements) {
                    composeRule.runOnIdle {
                        direction = dir
                        size = toggleSize
                        placement = edge
                    }
                    composeRule.onNodeWithTag("toggle", useUnmergedTree = true).assertHeightIsEqualTo(height.dp)
                    val outer =
                        composeRule
                            .onNodeWithTag("toggle", useUnmergedTree = true)
                            .getUnclippedBoundsInRoot()
                    val hasLeadingIcon =
                        edge == ToggleIconPlacement.Start || edge == ToggleIconPlacement.Both
                    val hasTrailingIcon =
                        edge == ToggleIconPlacement.End || edge == ToggleIconPlacement.Both
                    val first =
                        composeRule.onNodeWithTag(
                            if (hasLeadingIcon) "start" else "label",
                            useUnmergedTree = true,
                        ).getUnclippedBoundsInRoot()
                    val last =
                        composeRule.onNodeWithTag(
                            if (hasTrailingIcon) "end" else "label",
                            useUnmergedTree = true,
                        ).getUnclippedBoundsInRoot()
                    val minimumWidth =
                        when (toggleSize) {
                            ToggleSize.Sm -> 28f
                            ToggleSize.Md -> 32f
                            ToggleSize.Lg -> 36f
                        }
                    val normal =
                        if (edge == null) {
                            // justify-center splits any surplus from min-w-* across both edges.
                            10f + maxOf(0f, (minimumWidth - 30f) / 2f)
                        } else {
                            10f
                        }
                    val icon = if (toggleSize == ToggleSize.Sm) 6f else 8f
                    val start =
                        if (dir == LayoutDirection.Ltr) {
                            first.left - outer.left
                        } else {
                            outer.right - first.right
                        }
                    val end =
                        if (dir == LayoutDirection.Ltr) {
                            outer.right - last.right
                        } else {
                            last.left - outer.left
                        }
                    assertEquals(
                        if (edge == ToggleIconPlacement.Start || edge == ToggleIconPlacement.Both) icon else normal,
                        start.value,
                        .1f,
                        "$dir/$toggleSize/$edge start",
                    )
                    assertEquals(
                        if (edge == ToggleIconPlacement.End || edge == ToggleIconPlacement.Both) icon else normal,
                        end.value,
                        .1f,
                        "$dir/$toggleSize/$edge end",
                    )
                }
            }
        }
    }
}
