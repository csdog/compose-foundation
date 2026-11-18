@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.sidebar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.click
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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

    @Test fun edgeToggleOverlapsTheDesktopPanelWithoutConsumingInsetWidth() {
        setThemedContent {
            SidebarProvider(
                collapsible = SidebarCollapsible.Icon,
                mobile = false,
                modifier = Modifier.size(600.dp, 200.dp),
            ) {
                Sidebar(Modifier.testTag("panel")) {}
                SidebarRail(Modifier.testTag("edge-toggle"))
                SidebarInset(Modifier.testTag("inset")) {}
            }
        }
        val panel = bounds("panel")
        val rail = bounds("edge-toggle")
        val inset = bounds("inset")
        assertEquals(16f, rail.width, .1f)
        assertEquals(200f, rail.height, .1f)
        assertEquals(panel.right - 9f, rail.left, .1f)
        assertEquals(panel.right, inset.left, .1f)
        assertEquals(344f, inset.width, .1f)

        composeRule.onNodeWithTag("edge-toggle").performClick()
        composeRule.mainClock.advanceTimeBy(260)
        val collapsed = bounds("panel")
        assertEquals(48f, collapsed.width, .1f)
        assertEquals(collapsed.right - 9f, bounds("edge-toggle").left, .1f)
        assertEquals(collapsed.right, bounds("inset").left, .1f)
    }

    @Test fun edgeToggleUsesTheLogicalOutsideForAnEndPanelInRtl() {
        setThemedContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                SidebarProvider(mobile = false, modifier = Modifier.size(600.dp, 200.dp)) {
                    SidebarInset(Modifier.testTag("inset")) {}
                    Sidebar(side = SidebarSide.End, modifier = Modifier.testTag("panel")) {}
                    SidebarRail(Modifier.testTag("edge-toggle"))
                }
            }
        }
        val panel = bounds("panel")
        val rail = bounds("edge-toggle")
        assertEquals(panel.right - 9f, rail.left, .1f)
        assertEquals(panel.right, bounds("inset").left, .1f)
        assertEquals(16f, rail.width, .1f)
    }

    @Test fun nestedSidebarRailRetainsAnInlineClickTarget() {
        var open = true
        setThemedContent {
            SidebarProvider(mobile = false, onOpenChange = { open = it }, modifier = Modifier.size(600.dp, 200.dp)) {
                Sidebar {
                    Box { SidebarRail(Modifier.testTag("inline-toggle")) }
                }
                SidebarInset {}
            }
        }

        val inlineRail = bounds("inline-toggle")
        assertTrue(inlineRail.width > 0f && inlineRail.height > 0f)
        composeRule.onNodeWithTag("inline-toggle").performMouseInput { click() }
        composeRule.runOnIdle { assertFalse(open) }
    }

    @Test fun unpairedProviderRailRetainsAnInlineClickTarget() {
        var open = true
        setThemedContent {
            SidebarProvider(mobile = false, onOpenChange = { open = it }, modifier = Modifier.size(600.dp, 200.dp)) {
                SidebarRail(Modifier.testTag("inline-toggle"))
                SidebarInset(Modifier.testTag("inset")) {}
            }
        }

        val inlineRail = bounds("inline-toggle")
        assertTrue(inlineRail.width > 0f && inlineRail.height > 0f)
        assertEquals(inlineRail.right, bounds("inset").left, .1f)
        composeRule.onNodeWithTag("inline-toggle").performMouseInput { click() }
        composeRule.runOnIdle { assertFalse(open) }
    }

    private fun bounds(tag: String) = composeRule.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot
}
