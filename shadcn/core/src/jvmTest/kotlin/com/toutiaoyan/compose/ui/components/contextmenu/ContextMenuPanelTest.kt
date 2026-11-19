package com.toutiaoyan.compose.ui.components.contextmenu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test

/** Base UI `cn-context-menu-content` is `min-w-36 p-1`; recipes override it with `w-48`/`w-44`. */
class ContextMenuPanelTest {
    @get:Rule val rule = createComposeRule()

    @Test fun keyboardTriggerRingIsTransparentThemeAwareAndRestoredAfterEscape() {
        val dark = mutableStateOf(false)
        val disabled = mutableStateOf(false)
        val direction = mutableStateOf(LayoutDirection.Ltr)
        val surface = Color(0xFFCFD8FF)
        lateinit var inputMode: InputModeManager
        var expectedRing = Color.Unspecified
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark.value, minTouchTarget = null) {
                inputMode = LocalInputModeManager.current
                val theme = currentTheme
                expectedRing = theme.colors.ring.copy(alpha = theme.ring.opacity).compositeOver(surface)
                CompositionLocalProvider(LocalLayoutDirection provides direction.value) {
                    Column {
                        Button(onClick = {}) { Text { "Before" } }
                        Box(Modifier.background(surface).testTag("focus-surface").padding(16.dp)) {
                            ContextMenu {
                                ContextMenuTrigger(Modifier.testTag("focus-trigger"), disabled = disabled.value) {
                                    Box(Modifier.size(80.dp, 40.dp), contentAlignment = Alignment.Center) {
                                        Text { "Area" }
                                    }
                                }
                                ContextMenuContent { ContextMenuItem("Back", onClick = {}) }
                            }
                        }
                    }
                }
            }
        }

        fun checkColor(
            actual: Color,
            expected: Color,
        ) {
            assertTrue(
                abs(actual.red - expected.red) < .02f &&
                    abs(actual.green - expected.green) < .02f &&
                    abs(actual.blue - expected.blue) < .02f,
                "Expected $expected, got $actual",
            )
        }

        fun checkRing(expected: Color) {
            val trigger = rule.onNodeWithTag("focus-trigger").fetchSemanticsNode().boundsInRoot
            val parent = rule.onNodeWithTag("focus-surface").fetchSemanticsNode().boundsInRoot
            val pixels = rule.onNodeWithTag("focus-surface").captureToImage().toPixelMap()
            val x = (trigger.center.x - parent.left).toInt()
            val y = (trigger.top - parent.top).toInt()
            checkColor(pixels[x, y - 1], expected)
            checkColor(pixels[(trigger.left - parent.left).toInt() + 4, y + 4], surface)
        }
        val idleBounds = rule.onNodeWithTag("focus-trigger").fetchSemanticsNode().boundsInRoot
        checkRing(surface)
        rule.runOnIdle { inputMode.requestInputMode(InputMode.Keyboard) }
        rule.onNodeWithText("Before").requestFocus().performKeyInput { pressKey(Key.Tab) }
        rule.onNodeWithTag("focus-trigger").assertIsFocused().assertTextEquals("Area")
        for (isDark in listOf(false, true, false)) {
            rule.runOnIdle {
                dark.value = isDark
                direction.value = if (isDark) LayoutDirection.Rtl else LayoutDirection.Ltr
            }
            rule.onNodeWithTag("focus-trigger").assertIsFocused()
            checkRing(expectedRing)
            assertEquals(idleBounds.size, rule.onNodeWithTag("focus-trigger").fetchSemanticsNode().boundsInRoot.size)
        }
        rule.onNodeWithTag("focus-trigger").performKeyInput { pressKey(Key.Menu) }
        rule.onNodeWithText("Back").assertIsFocused().performKeyInput { pressKey(Key.Escape) }
        rule.onNodeWithTag("focus-trigger").assertIsFocused()
        checkRing(expectedRing)
        rule.runOnIdle { inputMode.requestInputMode(InputMode.Touch) }
        checkRing(surface)
        rule.runOnIdle { inputMode.requestInputMode(InputMode.Keyboard) }
        rule.onNodeWithTag("focus-trigger").requestFocus().assertIsFocused()
        rule.runOnIdle { disabled.value = true }
        rule.onNodeWithTag("focus-trigger").assert(isFocused().not())
        checkRing(surface)
        assertEquals(idleBounds.size, rule.onNodeWithTag("focus-trigger").fetchSemanticsNode().boundsInRoot.size)
    }

    @Test fun ltrSubmenuOpensRightWithSpaceOnBothSides() = checkSubmenuPlacement(LayoutDirection.Ltr, atEdge = false)

    @Test fun rtlSubmenuOpensLeftWithSpaceOnBothSides() = checkSubmenuPlacement(LayoutDirection.Rtl, atEdge = false)

    @Test fun ltrSubmenuFlipsLeftAtRightEdge() = checkSubmenuPlacement(LayoutDirection.Ltr, atEdge = true)

    @Test fun rtlSubmenuFlipsRightAtLeftEdge() = checkSubmenuPlacement(LayoutDirection.Rtl, atEdge = true)

    private fun checkSubmenuPlacement(
        direction: LayoutDirection,
        atEdge: Boolean,
    ) {
        var anchor = Rect.Zero
        var panel = Rect.Zero
        var windowWidth = 0
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                windowWidth = LocalWindowInfo.current.containerSize.width
                CompositionLocalProvider(LocalLayoutDirection provides direction) {
                    val alignment =
                        when {
                            !atEdge -> Alignment.Center
                            direction == LayoutDirection.Rtl -> AbsoluteAlignment.CenterLeft
                            else -> AbsoluteAlignment.CenterRight
                        }
                    // Absolute alignment places RTL's collision case at the physical left edge.
                    Box(Modifier.fillMaxSize(), contentAlignment = alignment) {
                        ContextMenu {
                            ContextMenuTrigger("Area")
                            ContextMenuContent {
                                ContextMenuSub {
                                    ContextMenuSubTrigger(
                                        "Navigation",
                                        Modifier.onGloballyPositioned { anchor = it.boundsInWindow() },
                                    )
                                    ContextMenuSubContent(
                                        Modifier.onGloballyPositioned { panel = it.boundsInWindow() },
                                    ) {
                                        ContextMenuItem("Back", onClick = {})
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        rule.onNodeWithText("Area").requestFocus().performKeyInput { pressKey(Key.Menu) }
        rule.onNodeWithText("Navigation").assertIsFocused().performKeyInput {
            pressKey(if (direction == LayoutDirection.Rtl) Key.DirectionLeft else Key.DirectionRight)
        }
        rule.onNodeWithText("Back").assertIsFocused()
        rule.runOnIdle { assertSubmenuPlacement(direction, atEdge, anchor, panel, windowWidth) }
    }

    private fun assertSubmenuPlacement(
        direction: LayoutDirection,
        atEdge: Boolean,
        anchor: Rect,
        panel: Rect,
        windowWidth: Int,
    ) {
        assertTrue(anchor.width > 0 && panel.width > 0)
        if (!atEdge) {
            assertTrue(
                anchor.left >= panel.width && windowWidth - anchor.right >= panel.width,
                "Both sides must have room",
            )
        } else {
            val preferredSpace = if (direction == LayoutDirection.Rtl) anchor.left else windowWidth - anchor.right
            assertTrue(preferredSpace < panel.width, "The preferred side must collide")
        }
        assertTrue(panel.left >= 0 && panel.right <= windowWidth)
        val opensRight = (direction == LayoutDirection.Ltr) != atEdge
        if (opensRight) {
            assertTrue(panel.left >= anchor.right - 1f, "$direction edge=$atEdge: $anchor -> $panel")
        } else {
            assertTrue(panel.right <= anchor.left + 1f, "$direction edge=$atEdge: $anchor -> $panel")
        }
    }

    @Test fun openPanelUsesTheOfficialMinWidthAndOneDpInset() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.size(320.dp, 200.dp), contentAlignment = Alignment.Center) {
                        ContextMenu {
                            ContextMenuTrigger("Area")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {}, modifier = Modifier.testTag("cm-item"))
                            }
                        }
                    }
                }
            }
        }
        rule.onNodeWithText("Area").requestFocus().performKeyInput {
            keyDown(Key.ShiftLeft)
            pressKey(Key.F10)
            keyUp(Key.ShiftLeft)
        }
        rule.waitForIdle()
        // 144dp panel minus 4dp padding on each side.
        rule.onNodeWithTag("cm-item").assertWidthIsEqualTo(136.dp)
        rule.onNodeWithText("Back").assertExists()
    }

    @Test fun tabFocusedTriggerOpensNavigatesRestoresAndReopens() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Column {
                        Button(onClick = {}) { Text { "Before" } }
                        ContextMenu {
                            ContextMenuTrigger("Area")
                            ContextMenuContent {
                                ContextMenuItem("Unavailable", onClick = {}, disabled = true)
                                ContextMenuItem("Back", onClick = {})
                                ContextMenuItem("Forward", onClick = {}, disabled = true)
                                ContextMenuSub {
                                    ContextMenuSubTrigger("More Tools")
                                    ContextMenuSubContent {
                                        ContextMenuItem("Copy", onClick = {})
                                    }
                                }
                            }
                        }
                        Button(onClick = {}) { Text { "After" } }
                    }
                }
            }
        }
        rule.onNodeWithText("Before").requestFocus().assertIsFocused().performKeyInput {
            pressKey(Key.Tab)
        }
        // Key.Menu is the component contract, not the browser's native code mapping.
        rule.onNodeWithText("Area").assertIsFocused().performKeyInput { pressKey(Key.Menu) }
        rule.onNodeWithText("Unavailable").assertIsNotEnabled().assert(isFocused().not())
        rule.onNodeWithText("Back").assertIsFocused().performKeyInput { pressKey(Key.DirectionDown) }
        rule.onNodeWithText("Forward").assertIsNotEnabled().assert(isFocused().not())
        rule.onNodeWithText("More Tools").assertIsFocused().performKeyInput { pressKey(Key.DirectionRight) }
        rule.onNodeWithText("Copy").assertIsFocused().performKeyInput { pressKey(Key.Escape) }
        rule.onNodeWithText("Copy").assertDoesNotExist()
        rule.onNodeWithText("More Tools").assertIsFocused().performKeyInput { pressKey(Key.Escape) }
        rule.onNodeWithText("Back").assertDoesNotExist()
        rule.onNodeWithText("Area").assertIsFocused().performKeyInput {
            keyDown(Key.ShiftLeft)
            pressKey(Key.F10)
            keyUp(Key.ShiftLeft)
        }
        rule.onNodeWithText("Back").assertIsFocused().performKeyInput { pressKey(Key.Escape) }
        rule.onNodeWithText("Back").assertDoesNotExist()
        rule.onNodeWithText("Area").assertIsFocused()
    }

    @Test fun wideRecipePanelKeepsTheSameFourDpInset() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.size(320.dp, 200.dp), contentAlignment = Alignment.Center) {
                        ContextMenu {
                            ContextMenuTrigger("Area")
                            ContextMenuContent(panelWidth = 192.dp) {
                                ContextMenuItem("Back", onClick = {}, modifier = Modifier.testTag("cm-item"))
                            }
                        }
                    }
                }
            }
        }
        rule.onNodeWithText("Area").requestFocus().performKeyInput {
            keyDown(Key.ShiftLeft)
            pressKey(Key.F10)
            keyUp(Key.ShiftLeft)
        }
        rule.waitForIdle()
        rule.onNodeWithTag("cm-item").assertWidthIsEqualTo(184.dp)
    }

    @Test fun constrainedRootMenuCanScrollToItsLastAction() {
        var selected = false
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                ContextMenu {
                    ContextMenuTrigger("Area")
                    ContextMenuContent(modifier = Modifier.heightIn(max = 120.dp).testTag("scroll-panel")) {
                        repeat(30) { index ->
                            ContextMenuItem("Action $index", onClick = { selected = true })
                        }
                    }
                }
            }
        }
        rule.onNodeWithText("Area").requestFocus().performKeyInput {
            keyDown(Key.ShiftLeft)
            pressKey(Key.F10)
            keyUp(Key.ShiftLeft)
        }
        rule.waitForIdle()
        rule.onNodeWithTag("scroll-panel").assertHeightIsEqualTo(120.dp)
        rule.onNodeWithText("Action 29").performScrollTo().assertIsDisplayed().performClick()
        rule.runOnIdle { kotlin.test.assertTrue(selected) }
    }

    @Test fun destructiveFocusUsesTenPercentInLightAndTwentyInDarkAfterThemeSwitch() {
        val dark = mutableStateOf(false)
        var expected = Color.Unspecified
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark.value, minTouchTarget = null) {
                val colors = currentTheme.colors
                expected = colors.destructive.copy(alpha = if (dark.value) 0.2f else 0.1f).compositeOver(colors.popover)
                ContextMenu {
                    ContextMenuTrigger("Area")
                    ContextMenuContent {
                        ContextMenuItem(
                            "Delete",
                            onClick = {},
                            destructive = true,
                            modifier = Modifier.testTag("delete"),
                        )
                    }
                }
            }
        }
        rule.onNodeWithText("Area").requestFocus().performKeyInput {
            keyDown(Key.ShiftLeft)
            pressKey(Key.F10)
            keyUp(Key.ShiftLeft)
        }
        for (isDark in listOf(false, true, false)) {
            rule.runOnIdle { dark.value = isDark }
            rule.onNodeWithTag("delete").requestFocus().assertIsFocused()
            val pixels = rule.onNodeWithTag("delete").captureToImage().toPixelMap()
            val actual = pixels[pixels.width - 10, pixels.height / 2]
            kotlin.test.assertTrue(
                kotlin.math.abs(expected.red - actual.red) < 0.01f &&
                    kotlin.math.abs(expected.green - actual.green) < 0.01f &&
                    kotlin.math.abs(expected.blue - actual.blue) < 0.01f,
                "dark=$isDark expected $expected, got $actual",
            )
        }
    }
}
