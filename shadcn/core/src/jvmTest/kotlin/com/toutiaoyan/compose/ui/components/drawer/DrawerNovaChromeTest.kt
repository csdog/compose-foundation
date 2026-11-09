package com.toutiaoyan.compose.ui.components.drawer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class DrawerNovaChromeTest : ScreenshotTest() {
    private fun assertBottomChrome(dark: Boolean) {
        var fill = Color.Unspecified
        setThemedContent(darkTheme = dark, stylePreset = StylePreset.Nova) {
            fill = currentTheme.colors.popover
            Drawer(defaultOpen = true) {
                DrawerContent(Modifier.testTag("panel")) { Box(Modifier.height(120.dp)) }
            }
        }
        val pixels = composeRule.onNodeWithTag("panel").captureToImage().toPixelMap()
        assertEquals(fill, pixels[pixels.width / 2, pixels.height - 1], "No border on the attached bottom edge")
        assertEquals(fill, pixels[1, pixels.height - 2], "Attached bottom corners must remain square")
        assertNotEquals(fill, pixels[pixels.width / 2, 0], "Nova has a top border toward the interior")
        assertNotEquals(fill, pixels[1, 1], "Top corner must be rounded")
    }

    @Test fun bottomNovaLightUsesDirectionalChrome() = assertBottomChrome(false)

    @Test fun bottomNovaDarkUsesDirectionalChrome() = assertBottomChrome(true)
}
