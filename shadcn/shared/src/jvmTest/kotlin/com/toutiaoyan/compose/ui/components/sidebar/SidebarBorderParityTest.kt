@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.sidebar

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class SidebarBorderParityTest : ScreenshotTest() {
    private fun check(
        dark: Boolean,
        rtl: Boolean = false,
        fixed: Boolean = false,
    ) {
        var border = Color.Unspecified
        var background = Color.Unspecified
        setThemedContent(darkTheme = dark, stylePreset = StylePreset.Nova) {
            border = currentTheme.colors.sidebarBorder
            background = currentTheme.colors.sidebar
            CompositionLocalProvider(
                LocalLayoutDirection provides
                    if (rtl) {
                        LayoutDirection.Rtl
                    } else {
                        LayoutDirection.Ltr
                    },
            ) {
                SidebarProvider(
                    mobile = false,
                    modifier = Modifier.height(120.dp),
                    collapsible = if (fixed) SidebarCollapsible.None else SidebarCollapsible.Offcanvas,
                ) { Sidebar(Modifier.testTag("rail")) {} }
            }
        }
        val pixels = composeRule.onNodeWithTag("rail").captureToImage().toPixelMap()
        val inward = if (rtl) 0 else pixels.width - 1
        val outer = if (rtl) pixels.width - 1 else 0
        val expected = if (fixed) background else border.compositeOver(background)
        val actual = pixels[inward, 60]
        assertEquals(expected.red, actual.red, 1f / 255f + .0001f)
        assertEquals(expected.green, actual.green, 1f / 255f + .0001f)
        assertEquals(expected.blue, actual.blue, 1f / 255f + .0001f)
        assertEquals(background, pixels[outer, 60])
    }

    @Test fun inwardBorderLight() = check(false)

    @Test fun inwardBorderDark() = check(true)

    @Test fun inwardBorderMirrorsWithLogicalStartInRtl() = check(true, rtl = true)

    @Test fun fixedSidebarHasNoDefaultBorder() = check(true, fixed = true)
}
