package com.toutiaoyan.compose.ui.components.drawer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DrawerRtlTest : ScreenshotTest() {
    @Test fun physicalEdgesDoNotReverseWithTextDirection() {
        for (layout in LayoutDirection.entries) {
            assertEquals(
                IntOffset(0, 40),
                DrawerSwipeDirection.Left.contentAlignment().align(IntSize(20, 20), IntSize(100, 100), layout),
            )
            assertEquals(
                IntOffset(80, 40),
                DrawerSwipeDirection.Right.contentAlignment().align(IntSize(20, 20), IntSize(100, 100), layout),
            )
        }
    }

    private fun assertHandleOnInteriorEdge(direction: DrawerSwipeDirection) {
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Drawer(defaultOpen = true, swipeDirection = direction, showSwipeHandle = true) {
                    DrawerContent(Modifier.testTag("panel")) {
                        Box(Modifier.fillMaxWidth().height(80.dp).testTag("body"))
                    }
                }
            }
        }
        val panel = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow
        val body = composeRule.onNodeWithTag("body").fetchSemanticsNode().boundsInWindow
        val gap = if (direction == DrawerSwipeDirection.Right) body.left - panel.left else panel.right - body.right
        assertTrue(gap >= 11f, "The handle must reserve the interior edge in RTL: $panel, $body")
    }

    @Test fun rightHandleStaysOnTheLeftInRtl() = assertHandleOnInteriorEdge(DrawerSwipeDirection.Right)

    @Test fun leftHandleStaysOnTheRightInRtl() = assertHandleOnInteriorEdge(DrawerSwipeDirection.Left)
}
