package com.toutiaoyan.compose.ui.components.surfaces

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.drawer.*
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.math.abs

class DrawerSizingTest : ScreenshotTest() {
    private fun assertAttachedSide(direction: DrawerSwipeDirection) {
        var viewportWidth = 0f
        var viewportHeight = 0f
        var density = 1f
        setThemedContent(stylePreset = StylePreset.Nova) {
            density = LocalDensity.current.density
            viewportWidth = LocalWindowInfo.current.containerSize.width.toFloat()
            viewportHeight = LocalWindowInfo.current.containerSize.height.toFloat()
            Drawer(defaultOpen = true, swipeDirection = direction) {
                DrawerContent(Modifier.testTag("panel")) { DrawerHeader("Attached side") }
            }
        }
        val panel = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow
        val inset = 0f
        assertTrue(abs(panel.top - inset) < 2f, "Top gap: ${panel.top}")
        assertTrue(abs(viewportHeight - panel.bottom - inset) < 2f, "Bottom gap: ${viewportHeight - panel.bottom}")
        val sideGap = if (direction == DrawerSwipeDirection.Left) panel.left else viewportWidth - panel.right
        assertTrue(abs(sideGap - inset) < 2f, "Side gap: $sideGap")
        val expectedWidth = if (viewportWidth >= 640f * density) 384f * density else viewportWidth * 0.75f
        assertTrue(
            abs(panel.width - expectedWidth) < 2f,
            "Width should exclude the outer gap: ${panel.width} vs $expectedWidth",
        )
    }

    @Test fun leftDrawerAttachesToWindowEdges() = assertAttachedSide(DrawerSwipeDirection.Left)

    @Test fun rightDrawerAttachesToWindowEdges() = assertAttachedSide(DrawerSwipeDirection.Right)

    private fun assertAttachedVertical(direction: DrawerSwipeDirection) {
        var viewportWidth = 0f
        var viewportHeight = 0f
        var density = 1f
        setThemedContent(stylePreset = StylePreset.Nova) {
            density = LocalDensity.current.density
            viewportWidth = LocalWindowInfo.current.containerSize.width.toFloat()
            viewportHeight = LocalWindowInfo.current.containerSize.height.toFloat()
            Drawer(defaultOpen = true, swipeDirection = direction) {
                DrawerContent(Modifier.testTag("panel")) { Box(Modifier.height(180.dp)) }
            }
        }
        val panel = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow
        val inset = 0f
        assertTrue(abs(panel.left - inset) < 2f, "Left gap: ${panel.left}")
        assertTrue(abs(viewportWidth - panel.right - inset) < 2f, "Right gap: ${viewportWidth - panel.right}")
        val gap = if (direction == DrawerSwipeDirection.Down) viewportHeight - panel.bottom else panel.top
        assertTrue(abs(gap - inset) < 2f, "Attached edge gap: $gap")
    }

    @Test fun bottomDrawerAttachesToSideAndBottomEdges() = assertAttachedVertical(DrawerSwipeDirection.Down)

    @Test fun topDrawerAttachesToSideAndTopEdges() = assertAttachedVertical(DrawerSwipeDirection.Up)

    private fun assertSnapPosition(
        point: DrawerSnapPoint,
        full: Boolean = false,
    ) {
        var viewport = 0f
        var density = 1f
        setThemedContent(stylePreset = StylePreset.Nova) {
            viewport = LocalWindowInfo.current.containerSize.height.toFloat()
            density = LocalDensity.current.density
            Drawer(
                defaultOpen = true,
                snapPoints =
                    listOf(
                        DrawerSnapPoint.Absolute(148.dp),
                        DrawerSnapPoint.Fraction(0.5f),
                        DrawerSnapPoint.Fraction(1f),
                    ),
                snapPoint = point,
            ) {
                DrawerContent(Modifier.testTag("panel")) {
                    Box(Modifier.fillMaxWidth().height(1.dp).testTag("snap marker"))
                }
            }
        }
        val panel = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow
        val title =
            composeRule.onNodeWithTag("snap marker").fetchSemanticsNode().boundsInWindow
        val cap = viewport - 96f * density
        val exposed =
            if (full) {
                cap
            } else {
                when (point) {
                    is DrawerSnapPoint.Absolute -> point.size.value * density
                    is DrawerSnapPoint.Fraction -> viewport * point.value
                }
            }
        assertTrue(abs(panel.height - cap) < 2f, "Snap panel must retain vertical cap: ${panel.height} vs $cap")
        assertTrue(
            abs(title.top - (viewport - exposed)) < 2f,
            "Snap should expose $exposed px; title at ${title.top}",
        )
    }

    @Test fun fullSnapKeepsViewportInset() = assertSnapPosition(DrawerSnapPoint.Fraction(1f), full = true)

    @Test fun absoluteSnapExposesExactHeight() = assertSnapPosition(DrawerSnapPoint.Absolute(148.dp))

    @Test fun fractionSnapUsesViewportNotCappedPanel() = assertSnapPosition(DrawerSnapPoint.Fraction(0.5f))

    @Test fun bottomFollowsContentHeight() {
        var bodyHeight by mutableStateOf(80.dp)
        var expectedGrowth = 0f
        setThemedContent(stylePreset = StylePreset.Nova) {
            expectedGrowth = with(LocalDensity.current) { 160.dp.toPx() }
            Drawer(defaultOpen = true, swipeDirection = DrawerSwipeDirection.Down) {
                DrawerContent(Modifier.testTag("panel")) {
                    DrawerHeader("Short content")
                    Box(Modifier.height(bodyHeight))
                }
            }
        }
        val before = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow.height
        composeRule.runOnIdle { bodyHeight = 240.dp }
        val after = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow.height
        assertTrue(abs(after - before - expectedGrowth) < 2f, "Panel should grow with its content: $before -> $after")
    }

    @Test fun bottomLongBodyUsesViewportCapAndKeepsFooterVisible() {
        var expectedCap = 0f
        setThemedContent(stylePreset = StylePreset.Nova) {
            expectedCap = LocalWindowInfo.current.containerSize.height - with(LocalDensity.current) { 96.dp.toPx() }
            Drawer(defaultOpen = true, swipeDirection = DrawerSwipeDirection.Down) {
                DrawerContent(Modifier.testTag("panel")) {
                    DrawerHeader("Long content")
                    Box(Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()).testTag("body")) {
                        Box(Modifier.fillMaxWidth().height(2000.dp))
                    }
                    DrawerFooterColumn(Modifier.testTag("footer")) { Button("Confirm") }
                }
            }
        }
        val panel = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow
        val body = composeRule.onNodeWithTag("body").fetchSemanticsNode().boundsInWindow
        val footer = composeRule.onNodeWithTag("footer").fetchSemanticsNode().boundsInWindow
        assertTrue(
            abs(panel.height - expectedCap) < 2f,
            "Long panel should use viewport minus 96dp: ${panel.height} vs $expectedCap",
        )
        assertTrue(body.bottom <= footer.top + 1f, "Scrollable body must leave footer space")
        composeRule.onNodeWithText("Confirm").assertIsDisplayed()
    }
}
