@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.select

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SelectDensityTest : ScreenshotTest() {
    private fun show() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalDensity provides Density(2f)) {
                Box(Modifier.fillMaxSize().padding(top = 32.dp)) {
                    Select<String>(null, {}, defaultOpen = true) {
                        SelectTrigger()
                        SelectContent(Modifier.testTag("panel")) {
                            SelectGroup { repeat(30) { SelectItem("Option $it") } }
                        }
                    }
                }
            }
        }
    }

    @Test fun doubleDensityKeepsTenDpWindowMargins() {
        show()
        val panel = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow
        assertEquals(748f, panel.bottom, .1f)
    }

    @Test fun doubleDensityClickScrollUsesTheSameLogicalDistance() {
        show()
        val before = composeRule.onNodeWithText("Option 5").fetchSemanticsNode().boundsInWindow
        val arrows =
            hasAnyAncestor(hasTestTag("panel")) and
                SemanticsMatcher.keyIsDefined(SemanticsProperties.HideFromAccessibility)
        composeRule.onAllNodes(arrows).onLast().performTouchInput { click() }
        val after = composeRule.onNodeWithText("Option 5").fetchSemanticsNode().boundsInWindow
        assertEquals(before.top - 96f, after.top, .1f)
    }

    private fun geometry(
        scale: Float,
        center: Float = 18f,
        aligned: Boolean = true,
        content: Float = 848f,
        top: Float = 224f,
    ) = selectPopupGeometry(
        trigger = Rect(24f * scale, top * scale, 216f * scale, (top + 32f) * scale),
        windowHeight = 768f * scale,
        contentHeight = content * scale,
        selectedCenter = center * scale,
        alignItem = aligned,
        touch = false,
        sideOffset = 4f * scale,
        scale = scale,
    )

    @Test fun geometryKeepsLogicalPositionAcrossDensities() {
        val cases = listOf(Triple(18f, 848f, 224f), Triple(438f, 848f, 224f), Triple(214f, 736f, 220f))
        for ((center, content, top) in cases) {
            for (aligned in listOf(true, false)) {
                val baseline = geometry(1f, center, aligned, content, top)
                for (scale in listOf(1.25f, 2f, 3f)) {
                    val scaled = geometry(scale, center, aligned, content, top)
                    assertEquals(baseline.top * scale, scaled.top, .001f)
                    assertEquals(baseline.height * scale, scaled.height, .001f)
                    assertEquals(baseline.initialScroll * scale, scaled.initialScroll, .001f)
                    assertEquals(baseline.growthAnchor, scaled.growthAnchor)
                }
            }
        }
    }

    @Test fun growthKeepsLogicalPositionIncludingSubpixelRemainders() {
        for (scroll in listOf(.5f, 48f)) {
            val baseline =
                assertNotNull(
                    selectPopupGrowth(geometry(1f), 768f, scroll, 312f),
                )
            for (scale in listOf(1.25f, 2f, 3f)) {
                val scaled =
                    assertNotNull(
                        selectPopupGrowth(
                            geometry(scale),
                            768f * scale,
                            scroll * scale,
                            312f * scale,
                            scale = scale,
                        ),
                    )
                assertEquals(baseline.geometry.top * scale, scaled.geometry.top, .001f)
                assertEquals(baseline.geometry.height * scale, scaled.geometry.height, .001f)
                assertEquals(baseline.scroll * scale, scaled.scroll, .001f)
            }
        }
    }

    @Test fun scrollTargetsRevealTheSameMixedHeightRowAcrossDensities() {
        var top = 4f
        val items =
            List(24) { index ->
                val height = if (index % 3 == 0) 48f else 28f
                SelectScrollItemBounds(top, height).also { top += height }
            }
        val viewport = 316f
        val maxScroll = top + 4f - viewport
        for (up in listOf(false, true)) {
            val scroll = if (up) 220f else 20f
            val baseline = selectScrollTarget(items, up, scroll, viewport, maxScroll)
            for (scale in listOf(1.25f, 2f, 3f)) {
                val scaledItems = items.map { SelectScrollItemBounds(it.top * scale, it.height * scale) }
                val scaled =
                    selectScrollTarget(
                        scaledItems,
                        up,
                        scroll * scale,
                        viewport * scale,
                        maxScroll * scale,
                        scale,
                    )
                assertEquals(baseline * scale, scaled, .001f)
            }
        }
    }
}
