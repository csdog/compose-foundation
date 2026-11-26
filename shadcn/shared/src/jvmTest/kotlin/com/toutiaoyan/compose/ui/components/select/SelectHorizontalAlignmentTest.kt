@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.select

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals

@RunWith(Parameterized::class)
class SelectHorizontalAlignmentTest(
    private val direction: LayoutDirection,
    private val align: PopupAlign,
    private val densityScale: Float,
) : ScreenshotTest() {
    @Test fun alignedModeUsesValueAndItemTextEdgesInsteadOfPopupAlign() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.absolutePadding(left = 320.dp, top = 200.dp)) {
                val density = LocalDensity.current
                CompositionLocalProvider(
                    LocalLayoutDirection provides direction,
                    LocalDensity provides Density(density.density * densityScale, density.fontScale),
                ) {
                    Select<String>("Alpha", {}, defaultOpen = true) {
                        SelectTrigger(
                            Modifier.width(240.dp),
                            style = Style { contentPadding(PaddingValues(start = 23.dp, end = 11.dp)) },
                        ) { SelectValue(Modifier.testTag("value")) }
                        SelectContent(
                            Modifier.testTag("panel"),
                            style = Style { width(320.dp) },
                            align = align,
                            alignOffset = 37.dp,
                        ) {
                            SelectGroup { SelectItem("Alpha") }
                        }
                    }
                }
            }
        }
        val value = composeRule.onNodeWithTag("value", useUnmergedTree = true).fetchSemanticsNode().boundsInWindow
        val item =
            composeRule.onAllNodes(
                hasText("Alpha") and hasAnyAncestor(hasTestTag("panel")),
                useUnmergedTree = true,
            ).fetchSemanticsNodes().single { !it.config.contains(SemanticsProperties.Role) }.boundsInWindow
        if (direction == LayoutDirection.Ltr) {
            assertEquals(value.left, item.left, .5f)
        } else {
            assertEquals(value.right, item.right, .5f)
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}-{1}-density{2}")
        fun cases() =
            LayoutDirection.entries.flatMap { direction ->
                PopupAlign.entries.flatMap { align -> listOf(1f, 2f).map { arrayOf<Any>(direction, align, it) } }
            }
    }
}

class SelectHorizontalCollisionTest : ScreenshotTest() {
    private fun edge(right: Boolean) {
        var windowWidth = 0
        setThemedContent(stylePreset = StylePreset.Nova) {
            windowWidth = LocalWindowInfo.current.containerSize.width
            Box(Modifier.fillMaxWidth().height(400.dp).padding(top = 200.dp)) {
                CompositionLocalProvider(
                    LocalLayoutDirection provides if (right) LayoutDirection.Rtl else LayoutDirection.Ltr,
                ) {
                    Select<String>(
                        "Alpha",
                        {},
                        defaultOpen = true,
                        modifier =
                            Modifier.align(
                                if (right) Alignment.TopEnd else Alignment.TopStart,
                            )
                                .absoluteOffset(x = if (right) 24.dp else (-24).dp),
                    ) {
                        SelectTrigger(Modifier.width(240.dp))
                        SelectContent(Modifier.testTag("panel"), style = Style { width(320.dp) }) {
                            SelectGroup { SelectItem("Alpha") }
                        }
                    }
                }
            }
        }
        val panel = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow
        if (right) assertEquals(windowWidth - 5f, panel.right, .5f) else assertEquals(5f, panel.left, .5f)
    }

    @Test fun leftEdgeKeepsFivePixelCollisionPadding() = edge(right = false)

    @Test fun rightEdgeKeepsFivePixelCollisionPaddingInRtl() = edge(right = true)
}

class SelectReferenceFallbackTest : ScreenshotTest() {
    private fun fallback(customTrigger: Boolean) {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.absolutePadding(left = 160.dp, top = 200.dp)) {
                Select<String>("Not in the options", {}, defaultOpen = true) {
                    if (customTrigger) {
                        SelectTrigger(Modifier.width(240.dp).testTag("trigger")) {
                            com.toutiaoyan.compose.ui.components.text.Text { "Custom trigger" }
                        }
                    } else {
                        SelectTrigger(Modifier.width(240.dp).testTag("trigger"))
                    }
                    SelectContent(
                        Modifier.testTag("panel"),
                        style = Style { width(320.dp) },
                        align = PopupAlign.End,
                        alignOffset = 37.dp,
                    ) {
                        SelectGroup { SelectItem("Alpha") }
                    }
                }
            }
        }
        val trigger = composeRule.onNodeWithTag("trigger").fetchSemanticsNode().boundsInWindow
        val panel = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow
        assertEquals(trigger.left, panel.left, .5f)
        assertEquals(trigger.top, panel.top, .5f)
    }

    @Test fun valueVerticalOffsetAlignsTheActualTextCenter() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.absolutePadding(left = 160.dp, top = 200.dp)) {
                Select<String>("Alpha", {}, defaultOpen = true) {
                    SelectTrigger(Modifier.width(240.dp)) {
                        SelectValue(Modifier.offset(y = 4.dp).testTag("value"))
                    }
                    SelectContent(Modifier.testTag("panel"), style = Style { width(320.dp) }) {
                        SelectGroup { SelectItem("Alpha") }
                    }
                }
            }
        }
        val value = composeRule.onNodeWithTag("value", useUnmergedTree = true).fetchSemanticsNode().boundsInWindow
        val item =
            composeRule.onAllNodes(
                hasText("Alpha") and hasAnyAncestor(hasTestTag("panel")),
                useUnmergedTree = true,
            ).fetchSemanticsNodes().single { !it.config.contains(SemanticsProperties.Role) }.boundsInWindow
        assertEquals(value.center.y, item.center.y, .5f)
    }

    @Test fun missingValueElementUsesTriggerEdge() = fallback(customTrigger = true)

    @Test fun unknownSelectedValueDoesNotAlignTheFirstUnselectedItem() = fallback(customTrigger = false)
}
