@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.select

import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test

/** Raster coverage: String SimpleNode's diagnostic isLineEllipsized flag did not match its rendered glyphs. */
class SelectValueOverflowScreenshotTest : ScreenshotTest() {
    private fun overflow(dark: Boolean) =
        snapshot(
            "select_nova_value_ellipsis",
            darkTheme =
            dark,
            stylePreset =
                StylePreset.Nova,
        ) {
            Select(
                value = "A very long selected language value that cannot fit in a small trigger",
                onValueChange = {},
            ) {
                SelectTrigger(Modifier.width(120.dp), size = SelectSize.Sm)
            }
        }

    @Test fun light() = overflow(false)

    @Test fun dark() = overflow(true)
}
