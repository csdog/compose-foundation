@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.contextmenu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertTrue

class ContextMenuMotionTest : ScreenshotTest() {
    @Test fun platformPopupFadesItsOuterShadowWithThePanel() {
        assertShadowDuringEntry(modal = true)
    }

    @Test fun hostedPanelFadesItsOuterShadowWithThePanel() {
        assertShadowDuringEntry(modal = false)
    }

    private fun assertShadowDuringEntry(modal: Boolean) {
        var open by mutableStateOf(false)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.size(400.dp, 240.dp).background(Color.White).testTag("backing")) {
                OverlayHost {
                    ContextMenu(open = open, modal = modal, modifier = Modifier.offset(x = 120.dp, y = 40.dp)) {
                        ContextMenuTrigger("Area")
                        ContextMenuContent(
                            modifier = Modifier.testTag("panel"),
                            style =
                                Style {
                                    background(Color.Black)
                                    dropShadow(Shadow(radius = 0.dp, spread = 12.dp, color = Color.Red))
                                },
                        ) {
                            Box(Modifier.size(48.dp))
                        }
                    }
                }
            }
        }

        composeRule.mainClock.autoAdvance = false
        try {
            composeRule.runOnIdle { open = true }
            composeRule.mainClock.advanceTimeByFrame()
            composeRule.mainClock.advanceTimeBy(64)
            val backing = composeRule.onNodeWithTag("backing")
            val origin = backing.fetchSemanticsNode().boundsInWindow.topLeft
            val bounds = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow.translate(-origin)
            val pixels = backing.captureToImage().toPixelMap()
            val y = bounds.center.y.toInt()
            val panel = pixels[bounds.center.x.toInt(), y]
            val shadow = pixels[bounds.right.toInt() + 3, y]
            assertTrue(panel.red in 0.02f..0.98f, "Expected a partially visible menu, observed $panel")
            assertTrue(shadow.green < 0.98f, "The panel appeared without its outer shadow, observed $shadow")
        } finally {
            composeRule.mainClock.autoAdvance = true
        }
    }
}
