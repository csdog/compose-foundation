@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.styles

import androidx.compose.foundation.style.Style
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import kotlin.math.abs
import com.toutiaoyan.compose.ui.theme.currentTheme
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.select.*
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.checkbox.Checkbox
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FocusVisibleTest : ScreenshotTest() {
    @Test fun buttonRingTracksKeyboardModeWithoutMovingFocus() = checkRing(select = false)

    @Test fun selectRingTracksKeyboardModeWithoutMovingFocus() = checkRing(select = true)

    @Test fun destructiveButtonUsesTheSameFocusVisiblePolicy() =
        checkRing(
            select = false,
            variant = ButtonVariant.Destructive,
        )

    @Test fun spaceActivatesPointerFocusedCheckboxAndEnablesKeyboardIndicator() {
        lateinit var inputMode: InputModeManager
        setThemedContent(stylePreset = StylePreset.Nova) {
            inputMode = LocalInputModeManager.current
            Box(Modifier.size(80.dp).testTag("surface").background(Color.White).padding(12.dp)) {
                Checkbox(modifier = Modifier.testTag("target"))
            }
        }
        val target = composeRule.onNodeWithTag("target")
        val surface = composeRule.onNodeWithTag("surface")
        // The 16 dp indicator is centered in the larger touch target. Sample 2 dp outside its left edge.
        val ringOffset = with(composeRule.density) { 8.dp.roundToPx() + 2.dp.roundToPx() }

        fun ringPixel(): Color {
            val center = target.fetchSemanticsNode().boundsInRoot.center
            val origin = surface.fetchSemanticsNode().boundsInRoot.topLeft
            val pixels = surface.captureToImage().toPixelMap()
            return pixels[(center.x - origin.x).toInt() - ringOffset, (center.y - origin.y).toInt()]
        }

        target.requestFocus()
        composeRule.runOnIdle { inputMode.requestInputMode(InputMode.Touch) }
        target.assertIsFocused().assertIsOff()
        assertEquals(InputMode.Touch, inputMode.inputMode)
        assertEquals(Color.White, ringPixel())
        target.performKeyInput { pressKey(Key.Spacebar) }
        target.assertIsFocused().assertIsOn()
        assertEquals(InputMode.Keyboard, inputMode.inputMode)
        assertTrue(ringPixel() != Color.White, "Consumed activation key must reveal the ring")
        composeRule.runOnIdle { inputMode.requestInputMode(InputMode.Touch) }
        target.assertIsFocused().assertIsOn()
        assertEquals(InputMode.Touch, inputMode.inputMode)
        assertEquals(Color.White, ringPixel())
    }

    private fun checkRing(
        select: Boolean,
        variant: ButtonVariant = ButtonVariant.Outline,
    ) {
        lateinit var inputMode: InputModeManager
        setThemedContent(stylePreset = StylePreset.Nova) {
            inputMode = LocalInputModeManager.current
            Box(Modifier.size(210.dp, 64.dp).testTag("surface").background(Color.White).padding(12.dp)) {
                if (select) {
                    Select("Apple", {}) {
                        SelectTrigger(Modifier.width(160.dp).testTag("target"))
                        SelectContent { SelectItem("Apple") }
                    }
                } else {
                    Button("Target", {}, Modifier.testTag("target"), variant = variant, style = Style { width(160.dp) })
                }
            }
        }
        val target = composeRule.onNodeWithTag("target")
        target.requestFocus()
        composeRule.runOnIdle { assertTrue(inputMode.requestInputMode(InputMode.Touch)) }
        target.assertIsFocused()
        assertEquals(Color.White, outsidePixel(target), "Pointer-mode focus must not paint an outer ring")
        target.performKeyInput { pressKey(Key.DirectionRight) }
        target.assertIsFocused()
        assertEquals(InputMode.Keyboard, inputMode.inputMode)
        assertTrue(outsidePixel(target) != Color.White, "A real key must make the focused ring visible")
        composeRule.runOnIdle { inputMode.requestInputMode(InputMode.Touch) }
        target.assertIsFocused()
        assertEquals(Color.White, outsidePixel(target), "Returning to pointer mode must remove the ring")
    }

    @Test fun textInputRetainsItsFocusCueInPointerMode() {
        lateinit var inputMode: InputModeManager
        var expectedBorder = Color.Unspecified
        setThemedContent(stylePreset = StylePreset.Nova) {
            inputMode = LocalInputModeManager.current
            Box(Modifier.size(210.dp, 64.dp).testTag("surface").background(Color.White).padding(12.dp)) {
                expectedBorder = currentTheme.colors.ring
                Box(Modifier.width(160.dp).testTag("target")) { Input("Editable", {}) }
            }
        }
        val field =
            composeRule.onNode(
                hasRequestFocusAction() and (hasTestTag("target") or hasAnyAncestor(hasTestTag("target"))),
                useUnmergedTree = true,
            )
        field.requestFocus()
        composeRule.runOnIdle { inputMode.requestInputMode(InputMode.Touch) }
        field.assertIsFocused()
        val target = composeRule.onNodeWithTag("target")
        val box = composeRule.onNodeWithTag("surface")
        val a = target.fetchSemanticsNode().boundsInRoot
        val b = box.fetchSemanticsNode().boundsInRoot
        val pixels = box.captureToImage().toPixelMap()
        val border = pixels[(a.left - b.left).toInt(), (a.center.y - b.top).toInt()]
        assertTrue(
            abs(border.red - expectedBorder.red) < .01f &&
                abs(border.green - expectedBorder.green) < .01f &&
                abs(border.blue - expectedBorder.blue) < .01f,
            "Text-entry fields keep their focus border after pointer focus: $border vs $expectedBorder",
        )
    }

    private fun outsidePixel(target: SemanticsNodeInteraction): Color {
        val box = composeRule.onNodeWithTag("surface")
        val a = target.fetchSemanticsNode().boundsInRoot
        val b = box.fetchSemanticsNode().boundsInRoot
        val image = box.captureToImage().toPixelMap()
        return image[(a.left - b.left).toInt() - 2, (a.center.y - b.top).toInt()]
    }
}
