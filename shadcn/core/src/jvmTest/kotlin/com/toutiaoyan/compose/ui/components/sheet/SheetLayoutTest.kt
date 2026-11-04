@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.sheet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.contentPadding
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.*

class SheetLayoutTest : ScreenshotTest() {
    private var border = Color.Unspecified
    private var background = Color.Unspecified

    private fun node(tag: String) = composeRule.onNodeWithTag(tag, useUnmergedTree = true)

    private fun bounds(tag: String) = node(tag).getUnclippedBoundsInRoot()

    private fun show(
        weighted: Boolean = false,
        fill: Boolean = true,
        side: SheetSide = SheetSide.Right,
        dark: Boolean = false,
        rtl: Boolean = false,
    ) {
        setThemedContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                border = currentTheme.colors.border
                background = currentTheme.colors.popover
                CompositionLocalProvider(
                    LocalLayoutDirection provides
                        if (rtl) {
                            LayoutDirection.Rtl
                        } else {
                            LayoutDirection.Ltr
                        },
                ) {
                    Sheet(defaultOpen = true) {
                        SheetContent(Modifier.testTag("panel"), side = side) {
                            SheetHeader(Modifier.testTag("header")) {
                                SheetTitle(Modifier.testTag("title")) { "Profile" }
                                SheetDescription(Modifier.testTag("description")) { "Description" }
                            }
                            Box(
                                (if (weighted) Modifier.weight(1f, fill) else Modifier)
                                    .height(if (weighted && fill) 10000.dp else 20.dp).testTag("body"),
                            )
                            SheetFooter(Modifier.testTag("footer")) {
                                Button("Save changes", modifier = Modifier.testTag("save"))
                                Button("Cancel", modifier = Modifier.testTag("cancel"), variant = ButtonVariant.Outline)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun capture(name: String) {
        val file = File("build/reports/sheet-layout-r1/$name.png")
        file.parentFile.mkdirs()
        ImageIO.write(node("panel").captureToImage().toAwtImage(), "png", file)
    }

    @Test fun headerMatchesSixteenPaddingAndTwoGapWithFullLineHeights() {
        show()
        node("header").assertHeightIsEqualTo(78.dp)
        node("title").assertHeightIsEqualTo(24.dp)
        node("description").assertHeightIsEqualTo(20.dp)
        assertEquals(16.dp, bounds("title").top - bounds("header").top)
        assertEquals(16.dp, bounds("title").left - bounds("header").left)
        assertEquals(2.dp, bounds("description").top - bounds("title").bottom)
        assertEquals(16.dp, bounds("header").bottom - bounds("description").bottom)
        capture("header-footer-light")
    }

    @Test fun footerStacksFullWidthActionsAndUsesRemainingTopMargin() {
        show()
        node("footer").assertHeightIsEqualTo(104.dp)
        node("save").assertHeightIsEqualTo(32.dp)
        assertEquals(bounds("panel").bottom, bounds("footer").bottom)
        assertEquals(16.dp, bounds("save").left - bounds("footer").left)
        assertEquals(16.dp, bounds("footer").right - bounds("save").right)
        assertEquals(8.dp, bounds("cancel").top - bounds("save").bottom)
        assertTrue(bounds("footer").top - bounds("body").bottom > 100.dp)
    }

    @Test fun weightedBodyReceivesAllAvailableHeightBeforeFooterAutoMargin() {
        show(weighted = true)
        node("body").assertHeightIsEqualTo(554.dp)
        assertEquals(16.dp, bounds("body").top - bounds("header").bottom)
        assertEquals(16.dp, bounds("footer").top - bounds("body").bottom)
        assertEquals(bounds("panel").bottom, bounds("footer").bottom)
        capture("weighted-light")
    }

    @Test fun unfilledWeightedBodyLeavesOnlyItsUnusedSpaceForAutoMargin() {
        show(weighted = true, fill = false)
        node("body").assertHeightIsEqualTo(20.dp)
        assertEquals(bounds("panel").bottom, bounds("footer").bottom)
        assertTrue(bounds("footer").top - bounds("body").bottom > 100.dp)
    }

    @Test fun topPanelWrapsContentAndHasOnlyItsBottomBorder() {
        show(side = SheetSide.Top)
        node("panel").assertWidthIsEqualTo(1024.dp).assertHeightIsEqualTo(235.dp)
        assertEquals(bounds("panel").bottom - 1.dp, bounds("footer").bottom)
        val pixels = node("panel").captureToImage().toPixelMap()
        assertEquals(border, pixels[500, pixels.height - 1])
        assertEquals(background, pixels[500, 0])
    }

    @Test fun bottomPanelWrapsContentAndHasOnlyItsTopBorder() {
        show(side = SheetSide.Bottom)
        node("panel").assertWidthIsEqualTo(1024.dp).assertHeightIsEqualTo(235.dp)
        assertEquals(bounds("panel").top + 1.dp, bounds("header").top)
        val pixels = node("panel").captureToImage().toPixelMap()
        assertEquals(border, pixels[500, 0])
        assertEquals(background, pixels[500, pixels.height - 1])
    }

    @Test fun rightPanelHasOnlyItsInwardBorder() {
        show()
        val pixels = node("panel").captureToImage().toPixelMap()
        val expectedBorder = border.compositeOver(background)
        val actualBorder = pixels[0, 300]
        // The native renderer can round translucent channels by one 8-bit step.
        assertEquals(expectedBorder.red, actualBorder.red, 1f / 255f + .0001f)
        assertEquals(expectedBorder.green, actualBorder.green, 1f / 255f + .0001f)
        assertEquals(expectedBorder.blue, actualBorder.blue, 1f / 255f + .0001f)
        assertEquals(1f, actualBorder.alpha)
        assertEquals(background, pixels[pixels.width - 1, 300])
        assertEquals(background, pixels[150, 0])
    }

    @Test fun leftPanelHasOnlyItsInwardBorder() {
        show(side = SheetSide.Left)
        val pixels = node("panel").captureToImage().toPixelMap()
        assertEquals(border, pixels[pixels.width - 1, 300])
        assertEquals(background, pixels[0, 300])
        assertEquals(background, pixels[150, 0])
    }

    @Test fun closeIsTwentyEightAtPhysicalTopRightTwelve() {
        show()
        val close = composeRule.onNodeWithContentDescription("Close")
        close.assertWidthIsEqualTo(28.dp).assertHeightIsEqualTo(28.dp)
        val box = close.getUnclippedBoundsInRoot()
        assertEquals(12.dp, bounds("panel").right - box.right)
        assertEquals(12.dp, box.top - bounds("panel").top)
    }

    @Test fun rtlCloseUsesLogicalEndAndFooterStillStretches() {
        show(rtl = true, dark = true, side = SheetSide.Left)
        val close = composeRule.onNodeWithContentDescription("Close").getUnclippedBoundsInRoot()
        assertEquals(12.dp, close.left - bounds("panel").left)
        assertEquals(bounds("save").left, bounds("cancel").left)
        assertEquals(bounds("save").right, bounds("cancel").right)
        capture("rtl-dark")
    }

    @Test fun darkWeightedPanelKeepsSourceMetricsAndChrome() {
        show(weighted = true, dark = true)
        node("header").assertHeightIsEqualTo(78.dp)
        node("body").assertHeightIsEqualTo(554.dp)
        node("footer").assertHeightIsEqualTo(104.dp)
        val pixels = node("panel").captureToImage().toPixelMap()
        val expectedBorder = border.compositeOver(background)
        val actualBorder = pixels[0, 300]
        // The native renderer can round translucent channels by one 8-bit step.
        assertEquals(expectedBorder.red, actualBorder.red, 1f / 255f + .0001f)
        assertEquals(expectedBorder.green, actualBorder.green, 1f / 255f + .0001f)
        assertEquals(expectedBorder.blue, actualBorder.blue, 1f / 255f + .0001f)
        assertEquals(1f, actualBorder.alpha)
        assertEquals(background, pixels[pixels.width - 1, 300])
        capture("weighted-dark")
    }

    @Test fun headerAndFooterStylesCanOverrideDefaultPadding() {
        setThemedContent {
            Sheet(defaultOpen = true) {
                SheetContent(showCloseButton = false) {
                    SheetHeader(Modifier.testTag("header"), style = Style { contentPadding(0.dp) }) {
                        Box(Modifier.size(10.dp))
                    }
                    SheetFooter(Modifier.testTag("footer"), style = Style { contentPadding(0.dp) }) {
                        Box(Modifier.size(10.dp))
                    }
                }
            }
        }
        node("header").assertHeightIsEqualTo(10.dp)
        node("footer").assertHeightIsEqualTo(10.dp)
    }
}
