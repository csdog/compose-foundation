@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.resizable

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test

class ResizableNovaScreenshotTest : ScreenshotTest() {
    @Composable private fun group(orientation: Orientation) {
        val colors = currentTheme.colors
        val corner = twRadius.lg
        ResizablePanelGroup(
            Modifier.size(320.dp, 200.dp),
            orientation = orientation,
            style =
                Style {
                    border(colors.border)
                    shape(RoundedCornerShape(corner))
                },
        ) { first, second, _ ->
            ResizablePanel(first) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(textStyle = TextStyle(fontWeight = FontWeight.SemiBold)) { "One" }
                }
            }
            ResizableHandle(withHandle = true, modifier = Modifier.testTag("separator"))
            ResizablePanel(second) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(textStyle = TextStyle(fontWeight = FontWeight.SemiBold)) { "Two" }
                }
            }
        }
    }

    private fun normal(
        dark: Boolean,
        vertical: Boolean,
    ) {
        snapshot(
            "resizable_nova_" + if (vertical) "vertical" else "horizontal",
            darkTheme = dark,
            stylePreset = StylePreset.Nova,
        ) {
            group(if (vertical) Orientation.Vertical else Orientation.Horizontal)
        }
    }

    private fun focused(dark: Boolean) {
        snapshotFocused("resizable_nova_focus", "separator", darkTheme = dark, stylePreset = StylePreset.Nova) {
            group(Orientation.Horizontal)
        }
    }

    @Test fun horizontalLight() = normal(false, false)

    @Test fun horizontalDark() = normal(true, false)

    @Test fun verticalLight() = normal(false, true)

    @Test fun verticalDark() = normal(true, true)

    @Test fun focusLight() = focused(false)

    @Test fun focusDark() = focused(true)
}
