@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.sidebar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import org.junit.Test
import kotlin.test.assertEquals

class SidebarProviderLayoutTest : ScreenshotTest() {
    @Test fun ordinarySiblingReservesSpaceBeforeInset() {
        setThemedContent {
            SidebarProvider(mobile = false, modifier = Modifier.size(600.dp, 200.dp)) {
                SidebarInset(Modifier.testTag("inset")) {}
                Box(Modifier.size(40.dp).testTag("ordinary"))
                Sidebar(side = SidebarSide.End, modifier = Modifier.testTag("rail")) {}
            }
        }
        val inset = bounds("inset")
        val ordinary = bounds("ordinary")
        val rail = bounds("rail")
        assertEquals(304f, inset.width, .1f)
        assertEquals(inset.right, ordinary.left, .1f)
        assertEquals(ordinary.right, rail.left, .1f)
        assertEquals(inset.left + 600f, rail.right, .1f)
    }

    @Test fun multipleInsetsShareRemainingWidth() {
        setThemedContent {
            SidebarProvider(mobile = false, modifier = Modifier.size(601.dp, 200.dp)) {
                Sidebar(Modifier.testTag("rail")) {}
                SidebarInset(Modifier.testTag("first")) {}
                SidebarInset(Modifier.testTag("second")) {}
            }
        }
        val rail = bounds("rail")
        val first = bounds("first")
        val second = bounds("second")
        assertEquals(345f, first.width + second.width, .1f)
        assertEquals(rail.right, first.left, .1f)
        assertEquals(first.right, second.left, .1f)
        assertEquals(rail.left + 601f, second.right, .1f)
    }

    private fun bounds(tag: String) = composeRule.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot
}
