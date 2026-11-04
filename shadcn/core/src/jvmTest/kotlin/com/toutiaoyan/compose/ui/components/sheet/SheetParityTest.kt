@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.sheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toPixelMap
import java.io.File
import javax.imageio.ImageIO
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.WindowInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class SheetParityTest : ScreenshotTest() {
    private fun panel() = composeRule.onNodeWithTag("panel", useUnmergedTree = true)

    private fun content(
        width: Int,
        side: SheetSide = SheetSide.Right,
        modifier: Modifier = Modifier,
        dark: Boolean = false,
    ) {
        setThemedContent(darkTheme = dark, stylePreset = StylePreset.Nova) {
            val original = LocalWindowInfo.current
            val window =
                remember(original, width) {
                    object : WindowInfo by original {
                        override val containerSize = IntSize(width, 768)
                    }
                }
            CompositionLocalProvider(LocalWindowInfo provides window) {
                Sheet(defaultOpen = true) {
                    SheetContent(modifier.testTag("panel"), side = side, showCloseButton = false) {
                        SheetHeader("Profile")
                    }
                }
            }
        }
    }

    @Test fun mobilePanelUsesThreeQuartersOfViewport() {
        content(390)
        panel().assertWidthIsEqualTo(292.5.dp)
        capture("mobile-light")
    }

    @Test fun desktopPanelCapsAt384() {
        content(1440)
        panel().assertWidthIsEqualTo(384.dp)
        capture("desktop-light")
    }

    private fun capture(name: String) {
        val file = File("build/reports/sheet-r1/$name.png")
        file.parentFile.mkdirs()
        ImageIO.write(panel().captureToImage().toAwtImage(), "png", file)
    }

    @Test fun mobileDarkUsesTheSameResponsiveWidth() {
        content(390, dark = true)
        panel().assertWidthIsEqualTo(292.5.dp)
        capture("mobile-dark")
    }

    @Test fun desktopDarkUsesTheSameMaximumWidth() {
        content(1440, dark = true)
        panel().assertWidthIsEqualTo(384.dp)
        capture("desktop-dark")
    }

    @Test fun smallBreakpointDoesNotCapWidthEarly() {
        content(639)
        panel().assertWidthIsEqualTo(479.25.dp)
    }

    @Test fun smallBreakpointAppliesCapAt640() {
        content(640)
        panel().assertWidthIsEqualTo(384.dp)
    }

    @Test fun sidebarCallerCanOverrideDefaultPanelWidth() {
        content(390, modifier = Modifier.width(336.dp))
        panel().assertWidthIsEqualTo(336.dp)
    }

    @Test fun defaultPhysicalRightStaysRightInRtl() {
        setThemedContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Sheet(defaultOpen = true) {
                    SheetContent(Modifier.testTag("panel"), showCloseButton = false) { SheetHeader("Profile") }
                }
            }
        }
        assertEquals(1024.dp, panel().getUnclippedBoundsInRoot().right)
    }

    @Test fun logicalEndMovesToLeftInRtl() {
        setThemedContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Sheet(defaultOpen = true) {
                    SheetContent(Modifier.testTag("panel"), side = SheetSide.End, showCloseButton = false) {
                        SheetHeader("Profile")
                    }
                }
            }
        }
        assertEquals(0.dp, panel().getUnclippedBoundsInRoot().left)
    }

    @Test fun keyboardTriggerRestoresFocusAfterClosing() {
        setThemedContent {
            Sheet {
                SheetTrigger("Open", modifier = Modifier.testTag("trigger"))
                SheetContent {
                    Button("Done", onClick = {})
                }
            }
        }
        val trigger = composeRule.onNodeWithTag("trigger")
        trigger.performSemanticsAction(androidx.compose.ui.semantics.SemanticsActions.RequestFocus) { it() }
        trigger.performKeyInput { pressKey(Key.Enter) }
        composeRule.onNodeWithContentDescription("Close").performClick()
        trigger.assertIsFocused()
    }

    @Test fun controlledOwnerCanRefuseDismissal() {
        var requested: Boolean? = null
        setThemedContent {
            Sheet(open = true, onOpenChange = { requested = it }) {
                SheetContent(Modifier.testTag("panel")) { SheetHeader("Profile") }
            }
        }
        composeRule.onNodeWithContentDescription("Close").performClick()
        composeRule.runOnIdle { assertEquals(false, requested) }
        panel().assertExists()
    }

    @Test fun exitMovesFortyPixelsOverTwoHundredMilliseconds() {
        var visible by mutableStateOf(true)
        setThemedContent {
            AnimatedVisibility(visible = visible, exit = SheetTransitions.exit(SheetSide.Left)) {
                Box(Modifier.size(300.dp, 100.dp).background(Color.Red).testTag("panel"))
            }
        }
        val resting = panel().getUnclippedBoundsInRoot().left
        composeRule.mainClock.autoAdvance = false
        composeRule.runOnIdle { visible = false }
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeBy(100)
        val halfway = panel().getUnclippedBoundsInRoot().left
        assertTrue(halfway < resting - 10.dp && halfway > resting - 35.dp, "midpoint: $halfway")
        val pixel = panel().captureToImage().toPixelMap()[100, 50]
        assertTrue(pixel.green in 0.2f..0.8f, "opacity midpoint: $pixel")
        composeRule.mainClock.advanceTimeBy(150)
        panel().assertDoesNotExist()
    }

    @Test fun noCloseButtonStillAllowsOutsideDismiss() {
        setThemedContent {
            Sheet(defaultOpen = true) {
                SheetContent(Modifier.testTag("panel"), showCloseButton = false) { SheetHeader("Profile") }
            }
        }
        composeRule.onNodeWithContentDescription("Close").assertDoesNotExist()
        val roots = composeRule.onAllNodes(isRoot())
        roots[roots.fetchSemanticsNodes().lastIndex].performMouseInput { click(Offset(10f, 400f)) }
        panel().assertDoesNotExist()
    }

    @Test fun outsidePolicyCanVetoDismiss() {
        var intercepted = false
        setThemedContent {
            Sheet(defaultOpen = true) {
                SheetContent(Modifier.testTag("panel"), onPointerDownOutside = {
                    intercepted = true
                    true
                }) {
                    SheetHeader("Profile")
                }
            }
        }
        val roots = composeRule.onAllNodes(isRoot())
        roots[roots.fetchSemanticsNodes().lastIndex].performMouseInput { click(Offset(10f, 400f)) }
        composeRule.runOnIdle { assertTrue(intercepted) }
        panel().assertExists()
    }

    @Test fun escapeDismissesAndReturnsKeyboardFocus() {
        setThemedContent {
            Sheet {
                SheetTrigger("Open", modifier = Modifier.testTag("trigger"))
                SheetContent(Modifier.testTag("panel")) { Button("Inside", onClick = {}) }
            }
        }
        val trigger = composeRule.onNodeWithTag("trigger")
        trigger.performSemanticsAction(androidx.compose.ui.semantics.SemanticsActions.RequestFocus) { it() }
        trigger.performKeyInput { pressKey(Key.Enter) }
        composeRule.onNodeWithText("Inside").assertIsFocused().performKeyInput { pressKey(Key.Escape) }
        panel().assertDoesNotExist()
        trigger.assertIsFocused()
    }
}
