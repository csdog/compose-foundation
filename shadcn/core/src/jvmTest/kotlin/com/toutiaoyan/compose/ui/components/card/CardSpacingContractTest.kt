@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import kotlin.test.*

class CardSpacingContractTest {
    @get:Rule val rule = createComposeRule()

    @Test fun rootHasOnlyVerticalInsetWhilePartsFollowDynamicSpacingInBothDirections() {
        var inset by mutableStateOf(16)
        var rtl by mutableStateOf(false)
        rule.setContent {
            CompositionLocalProvider(
                LocalLayoutDirection provides if (rtl) LayoutDirection.Rtl else LayoutDirection.Ltr,
            ) {
                Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                    Card(Modifier.width(320.dp).testTag("card"), spacing = inset.dp) {
                        Box(Modifier.fillMaxWidth().height(8.dp).testTag("edge"))
                        CardHeader { Box(Modifier.fillMaxWidth().height(8.dp).testTag("header-body")) }
                        CardContent { Box(Modifier.fillMaxWidth().height(8.dp).testTag("content-body")) }
                        CardFooter(
                            Modifier.testTag("footer"),
                        ) { Box(Modifier.weight(1f).height(8.dp).testTag("footer-body")) }
                    }
                }
            }
        }

        fun bounds(tag: String) = rule.onNodeWithTag(tag).getUnclippedBoundsInRoot()
        for (direction in listOf(false, true)) for (spacing in listOf(16, 12, 20, 24, 32)) {
            rule.runOnIdle {
                rtl = direction
                inset = spacing
            }
            val root = bounds("card")
            assertEquals(root.left, bounds("edge").left)
            assertEquals(root.right, bounds("edge").right)
            assertEquals(spacing.dp, bounds("edge").top - root.top)
            for (tag in listOf("header-body", "content-body", "footer-body")) {
                assertEquals(root.left + spacing.dp, bounds(tag).left, "$tag left")
                assertEquals(root.right - spacing.dp, bounds(tag).right, "$tag right")
            }
            assertEquals(root.left, bounds("footer").left)
            assertEquals(root.right, bounds("footer").right)
            assertEquals(root.bottom, bounds("footer").bottom)
            assertEquals(spacing.dp, bounds("header-body").top - bounds("edge").bottom)
        }
    }

    @Test fun sectionStyleOverridesInsetAndRootClipsOverflowAtItsRoundedCorners() {
        var expectedRing = Color.Unspecified
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                expectedRing = currentTheme.colors.foreground.copy(alpha = .1f).compositeOver(Color.White)
                Box(Modifier.background(Color.White).testTag("surface").padding(16.dp)) {
                    Card(Modifier.width(120.dp).testTag("card"), style = Style { contentPadding(0.dp) }) {
                        CardContent(style = Style { contentPadding(0.dp) }) {
                            Box(Modifier.fillMaxWidth().height(40.dp).background(Color.Red).testTag("image"))
                        }
                    }
                }
            }
        }
        rule.onNodeWithTag("image").assertWidthIsEqualTo(120.dp)
        val pixels = rule.onNodeWithTag("surface").captureToImage().toPixelMap()
        assertEquals(Color.White, pixels[16, 16])
        assertEquals(Color.Red, pixels[76, 17])
        assertEquals(expectedRing.red, pixels[15, 36].red, .02f)
        assertEquals(expectedRing.green, pixels[15, 36].green, .02f)
        assertEquals(expectedRing.blue, pixels[15, 36].blue, .02f)
    }

    @Test fun firstImageAndFooterKeepFullBoundsAndExplicitPaddingWinsEvenAtTheDefaultValue() {
        var overrideInsets by mutableStateOf(false)
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Card(
                    Modifier.width(160.dp).testTag("card"),
                    style =
                        Style {
                            if (overrideInsets) {
                                contentPaddingTop(16.dp)
                                contentPaddingBottom(16.dp)
                            }
                        },
                ) {
                    CardImage(Modifier.fillMaxWidth().height(40.dp).testTag("cover")) {}
                    CardHeader("Title")
                    CardFooter(Modifier.testTag("footer")) { Box(Modifier.height(8.dp)) }
                }
            }
        }

        fun bounds(tag: String) = rule.onNodeWithTag(tag).getUnclippedBoundsInRoot()
        for (explicit in listOf(false, true, false)) {
            rule.runOnIdle { overrideInsets = explicit }
            val inset = if (explicit) 16.dp else 0.dp
            assertEquals(bounds("card").top + inset, bounds("cover").top)
            assertEquals(bounds("card").bottom - inset, bounds("footer").bottom)
            rule.onNodeWithTag("cover").assertHeightIsEqualTo(40.dp).assertWidthIsEqualTo(160.dp)
        }
    }

    @Test fun nestedPartsDoNotRemoveOuterInsetAndCallerHeightIsRespected() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Column {
                    Card(Modifier.width(200.dp).testTag("outer")) {
                        Card(Modifier.fillMaxWidth().testTag("inner")) {
                            CardImage(Modifier.fillMaxWidth().height(24.dp)) {}
                            CardFooter { Box(Modifier.height(8.dp)) }
                        }
                    }
                    Card(Modifier.requiredSize(200.dp, 100.dp).testTag("fixed")) {
                        CardImage(Modifier.fillMaxWidth().height(20.dp)) {}
                        CardFooter { Box(Modifier.height(8.dp)) }
                    }
                }
            }
        }
        val outer = rule.onNodeWithTag("outer").getUnclippedBoundsInRoot()
        val inner = rule.onNodeWithTag("inner").getUnclippedBoundsInRoot()
        assertEquals(16.dp, inner.top - outer.top)
        assertEquals(16.dp, outer.bottom - inner.bottom)
        rule.onNodeWithTag("fixed").assertWidthIsEqualTo(200.dp).assertHeightIsEqualTo(100.dp)
    }

    @Test fun footerHasNoImplicitGapAndCallersCanComposeTheirOwnSpacing() {
        var rtl by mutableStateOf(false)
        rule.setContent {
            CompositionLocalProvider(
                LocalLayoutDirection provides if (rtl) LayoutDirection.Rtl else LayoutDirection.Ltr,
            ) {
                Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                    Card(Modifier.width(160.dp)) {
                        CardFooter {
                            Box(Modifier.size(16.dp).testTag("first"))
                            Box(Modifier.size(16.dp).testTag("second"))
                        }
                    }
                }
            }
        }
        for (direction in listOf(false, true)) {
            rule.runOnIdle { rtl = direction }
            val first = rule.onNodeWithTag("first").fetchSemanticsNode().boundsInRoot
            val second = rule.onNodeWithTag("second").fetchSemanticsNode().boundsInRoot
            if (direction) assertEquals(second.right, first.left) else assertEquals(first.right, second.left)
        }
    }
}
