@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.button

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class ButtonIconPlacementTest {
    @get:Rule val rule = createComposeRule()

    @Test fun allTextSizesUseLogicalIconInsetsAndRetainHeight() {
        var size by mutableStateOf<ButtonSize>(ButtonSize.Md)
        var placement by mutableStateOf<ButtonIconPlacement?>(null)
        var direction by mutableStateOf(LayoutDirection.Ltr)
        var override by mutableStateOf(false)
        rule.setContent {
            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                    Button(
                        modifier = Modifier.testTag("button"),
                        size = size,
                        iconPlacement = placement,
                        style = if (override) Style { contentPadding(horizontal = 17.dp, vertical = 0.dp) } else Style,
                    ) { Box(Modifier.size(10.dp).testTag("content")) }
                }
            }
        }
        for (dir in LayoutDirection.entries) {
            for ((sz, height) in listOf(
                ButtonSize.Xs to 24,
                ButtonSize.Sm to 28,
                ButtonSize.Md to 32,
                ButtonSize.Lg to 36,
            )) {
                for (edge in listOf(
                    null,
                    ButtonIconPlacement.Start,
                    ButtonIconPlacement.End,
                    ButtonIconPlacement.Both,
                )) {
                    rule.runOnIdle {
                        size = sz
                        direction = dir
                        placement = edge
                    }
                    rule.onNodeWithTag("button").assertHeightIsEqualTo(height.dp)
                    val outer = rule.onNodeWithTag("button").getUnclippedBoundsInRoot()
                    val inner = rule.onNodeWithTag("content", useUnmergedTree = true).getUnclippedBoundsInRoot()
                    val normal = if (sz == ButtonSize.Xs) 8f else 10f
                    val icon = if (sz == ButtonSize.Xs || sz == ButtonSize.Sm) 6f else 8f
                    val start = if (dir == LayoutDirection.Ltr) inner.left - outer.left else outer.right - inner.right
                    val end = if (dir == LayoutDirection.Ltr) outer.right - inner.right else inner.left - outer.left
                    assertEquals(
                        (if (edge == null || edge == ButtonIconPlacement.End) normal else icon) + 1,
                        start.value,
                        .1f,
                    )
                    assertEquals(
                        (if (edge == null || edge == ButtonIconPlacement.Start) normal else icon) + 1,
                        end.value,
                        .1f,
                    )
                }
            }
        }
        rule.runOnIdle { override = true }
        rule.onNodeWithTag("button").assertWidthIsEqualTo(46.dp)
    }

    @Test fun iconOnlyDimensionsAndVegaPaddingAreNotChangedByTheHint() {
        var size by mutableStateOf<ButtonSize>(ButtonSize.Icon)
        var preset by mutableStateOf(StylePreset.Nova)
        rule.setContent {
            Theme(preset = preset, minTouchTarget = null) {
                Button(modifier = Modifier.testTag("button"), size = size, iconPlacement = ButtonIconPlacement.Both) {
                    Box(Modifier.size(10.dp))
                }
            }
        }
        for ((sz, side) in listOf(
            ButtonSize.IconXs to 24,
            ButtonSize.IconSm to 28,
            ButtonSize.Icon to 32,
            ButtonSize.IconLg to 36,
        )) {
            rule.runOnIdle { size = sz }
            rule.onNodeWithTag("button").assertWidthIsEqualTo(side.dp).assertHeightIsEqualTo(side.dp)
        }
        rule.runOnIdle {
            preset = StylePreset.Vega
            size = ButtonSize.Md
        }
        val bounds = rule.onNodeWithTag("button").getUnclippedBoundsInRoot()
        val withHint = bounds.right - bounds.left
        assertEquals(30f, withHint.value, .1f)
    }
}
