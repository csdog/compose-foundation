@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.scrollarea

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test

class ScrollAreaNovaScreenshotTest : ScreenshotTest() {
    @Composable private fun area(horizontal: Boolean = false) {
        val colors = currentTheme.colors
        val radius = twRadius.md
        ScrollArea(
            Modifier.size(192.dp, 288.dp),
            style =
                Style {
                    shape(RoundedCornerShape(radius))
                    border(colors.border)
                },
            viewportStyle = Style { shape(RoundedCornerShape(radius)) },
            orientation = if (horizontal) ScrollAreaOrientation.Horizontal else ScrollAreaOrientation.Vertical,
        ) {
            if (horizontal) {
                Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    repeat(3) { Text(Modifier.width(160.dp)) { "Artwork ${it + 1}" } }
                }
            } else {
                Column(Modifier.padding(16.dp)) {
                    repeat(30) { Text(Modifier.height(24.dp)) { "v1.2.0-beta.${30 - it}" } }
                }
            }
        }
    }

    @Test fun verticalLight() = snapshot("scroll_area_nova_vertical", stylePreset = StylePreset.Nova) { area() }

    @Test fun verticalDark() =
        snapshot("scroll_area_nova_vertical", darkTheme = true, stylePreset = StylePreset.Nova) {
            area()
        }

    @Test fun horizontalLight() = snapshot("scroll_area_nova_horizontal", stylePreset = StylePreset.Nova) { area(true) }

    @Test fun horizontalDark() =
        snapshot(
            "scroll_area_nova_horizontal",
            darkTheme =
            true,
            stylePreset =
                StylePreset.Nova,
        ) {
            area(true)
        }

    @Test fun focusLight() =
        snapshotFocused(
            "scroll_area_nova_focus",
            "scroll-area-viewport",
            stylePreset =
                StylePreset.Nova,
        ) {
            area()
        }

    @Test fun focusDark() =
        snapshotFocused(
            "scroll_area_nova_focus",
            "scroll-area-viewport",
            darkTheme = true,
            stylePreset = StylePreset.Nova,
        ) { area() }
}
