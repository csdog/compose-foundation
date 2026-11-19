@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.navigationmenu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertTrue

class NavigationMenuMotionTest : ScreenshotTest() {
    @Test
    fun sharedViewportPaintsItsShadowDuringEntrance() {
        var selected by mutableStateOf<String?>(null)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.size(400.dp, 260.dp).background(Color.White).testTag("nav-backing")) {
                NavigationMenu(
                    modifier = Modifier.offset(x = 100.dp, y = 30.dp),
                    value = selected,
                    onValueChange = { selected = it },
                ) {
                    NavigationMenuList {
                        NavigationMenuItem("menu") {
                            NavigationMenuTrigger("Menu")
                            NavigationMenuContent { Box(Modifier.size(120.dp, 64.dp).background(Color.Black)) }
                        }
                    }
                }
            }
        }
        composeRule.mainClock.autoAdvance = false
        try {
            composeRule.runOnIdle { selected = "menu" }
            composeRule.mainClock.advanceTimeByFrame()
            composeRule.mainClock.advanceTimeBy(128)
            val backing = composeRule.onNodeWithTag("nav-backing")
            val origin = backing.fetchSemanticsNode().boundsInWindow.topLeft
            val panel =
                composeRule.onNodeWithTag(NavigationMenuViewportTag)
                    .fetchSemanticsNode().boundsInWindow.translate(-origin)
            val pixels = backing.captureToImage().toPixelMap()
            val x = panel.center.x.toInt()
            val body = pixels[x, panel.center.y.toInt()]
            val shadow = pixels[x, panel.bottom.toInt() + 2]
            assertTrue(body.red in 0.02f..0.98f, "Expected the viewport to be fading: $body")
            assertTrue(shadow.red < 0.995f, "Expected a shadow outside the fading viewport: $shadow")
        } finally {
            composeRule.mainClock.autoAdvance = true
        }
    }
}
