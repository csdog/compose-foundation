@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package com.toutiaoyan.compose.ui.components.inputotp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.theme.currentTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import java.awt.datatransfer.StringSelection
import kotlin.test.assertEquals

class InputOTPParityTest {
    @get:Rule val rule = createComposeRule()

    @Test fun reportsValueChangesWithoutReportingCursorMovesOrRejectedInput() {
        var value by mutableStateOf("12")
        val seen = mutableListOf<String>()
        rule.setContent {
            Theme(preset = StylePreset.Nova) {
                InputOTP(
                    value = value,
                    onValueChange = {
                        value = it
                        seen.add(it)
                    },
                    length = 6,
                )
            }
        }
        val input = rule.onNode(hasSetTextAction()).requestFocus()
        input.performTextInputSelection(TextRange(0))
        input.performTextInput("X")
        rule.runOnIdle { assertEquals(emptyList(), seen) }
        input.performTextInput("5")
        rule.runOnIdle { assertEquals(listOf("512"), seen) }
    }

    @Test fun invalidGroupDoesNotPaintRingsAcrossItsInternalSeams() {
        var invalid by mutableStateOf(true)
        var includeInvalidSlot by mutableStateOf(true)
        var dark by mutableStateOf(false)
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                Box(Modifier.testTag("canvas").background(currentTheme.colors.background).padding(12.dp)) {
                    InputOTP(value = "", onValueChange = {}, length = 2, modifier = Modifier.testTag("otp")) {
                        InputOTPGroup {
                            InputOTPSlot(0)
                            if (includeInvalidSlot) InputOTPSlot(1, invalid = invalid)
                        }
                    }
                }
            }
        }
        // The second slot begins at x=31. Its invalid ring must surround the group,
        // not extend three pixels into the first slot beside the internal border.

        fun interiorIsClear() {
            val pixels = rule.onNodeWithTag("otp").captureToImage().toPixelMap()
            assertEquals(pixels[20, 16], pixels[29, 16], "Ring leaked into the group interior")
        }

        fun outerRingIsVisible(visible: Boolean) {
            val pixels = rule.onNodeWithTag("canvas").captureToImage().toPixelMap()
            assertEquals(visible, pixels[28, 10] != pixels[1, 1], "Outer group ring visibility")
        }
        interiorIsClear()
        outerRingIsVisible(true)
        rule.runOnIdle { dark = true }
        interiorIsClear()
        outerRingIsVisible(true)
        rule.runOnIdle { invalid = false }
        interiorIsClear()
        outerRingIsVisible(false)
        rule.runOnIdle { invalid = true }
        outerRingIsVisible(true)
        rule.runOnIdle { includeInvalidSlot = false }
        outerRingIsVisible(false)
    }

    @Test fun largeFormSlotsKeepTheirGeometryAndOriginalOverloadRemainsCallable() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                InputOTP(value = "123", onValueChange = {}, length = 3) {
                    InputOTPGroup(Modifier.height(48.dp).testTag("group")) {
                        repeat(3) { index ->
                            InputOTPSlot(
                                index,
                                textStyle = TextStyle(fontSize = 20.sp, lineHeight = 28.sp),
                                modifier = Modifier.size(if (index == 0) 44.dp else 45.dp, 48.dp),
                            )
                        }
                    }
                    InputOTPGroup { InputOTPSlot(0, Modifier, false) }
                }
            }
        }
        rule.onNodeWithTag("group").assertWidthIsEqualTo(132.dp).assertHeightIsEqualTo(48.dp)
    }

    @Test fun tapWhereASlotWasRemovedTargetsAMountedSlot() {
        var value by mutableStateOf("12")
        var showSecondSlot by mutableStateOf(true)
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                InputOTP(
                    value = value,
                    onValueChange = { value = it },
                    length = 2,
                    modifier = Modifier.testTag("otp"),
                ) {
                    InputOTPGroup {
                        InputOTPSlot(0)
                        if (showSecondSlot) {
                            InputOTPSlot(1)
                        } else {
                            Box(Modifier.size(32.dp))
                        }
                    }
                }
            }
        }

        rule.runOnIdle { showSecondSlot = false }
        rule.onNodeWithTag("otp").performTouchInput {
            click(Offset(width * 0.75f, center.y))
        }
        rule.onNode(hasSetTextAction()).performTextInput("5")
        rule.runOnIdle { assertEquals("52", value) }
    }

    @Test fun clipboardPasteFiltersAndTruncatesThenBackspaceDeletesSelection() {
        var value by mutableStateOf("")
        val clipboard =
            object : Clipboard {
                override suspend fun getClipEntry(): ClipEntry = ClipEntry(StringSelection("a12b34567"))

                override suspend fun setClipEntry(clipEntry: ClipEntry?) = Unit
            }
        rule.setContent {
            CompositionLocalProvider(LocalClipboard provides clipboard) {
                Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                    InputOTP(value = value, onValueChange = { value = it }, length = 6)
                }
            }
        }
        val input = rule.onNode(hasSetTextAction())
        val primary = if (System.getProperty("os.name").startsWith("Mac")) Key.MetaLeft else Key.CtrlLeft
        input.requestFocus().performKeyInput {
            keyDown(primary)
            pressKey(Key.V)
            keyUp(primary)
        }
        rule.waitUntil { value == "123456" }
        input.performTextInputSelection(TextRange(2, 4))
        input.performKeyInput { pressKey(Key.Backspace) }
        rule.runOnIdle { assertEquals("1256", value) }
        input.performTextInputSelection(TextRange(value.length))
        input.performKeyInput { pressKey(Key.Backspace) }
        rule.runOnIdle { assertEquals("125", value) }
    }
}
