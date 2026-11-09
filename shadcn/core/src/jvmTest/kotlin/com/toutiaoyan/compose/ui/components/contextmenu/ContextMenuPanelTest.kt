package com.toutiaoyan.compose.ui.components.contextmenu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test

/** Base UI `cn-context-menu-content` is `min-w-36 p-1`; recipes override it with `w-48`/`w-44`. */
class ContextMenuPanelTest {
    @get:Rule val rule = createComposeRule()

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
