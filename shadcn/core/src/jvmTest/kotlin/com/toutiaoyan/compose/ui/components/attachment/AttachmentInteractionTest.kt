@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.attachment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AttachmentInteractionTest : ScreenshotTest() {
    @Test fun actualPointerHoverInterpolatesAndReversesInLightTheme() = assertHoverMotion(false)

    @Test fun actualPointerHoverInterpolatesAndReversesInDarkTheme() = assertHoverMotion(true)

    private fun assertHoverMotion(dark: Boolean) {
        var cardColor = Color.Unspecified
        var hoverColor = Color.Unspecified
        // A contrasting caller backdrop makes intermediate alpha observable in
        // both themes; the real pointer tests the component's interaction wiring.
        val backdrop = Color(0xff808080)
        setThemedContent(stylePreset = StylePreset.Nova, darkTheme = dark) {
            cardColor = currentTheme.colors.card
            hoverColor = currentTheme.colors.muted.copy(alpha = .5f).compositeOver(backdrop)
            Box(Modifier.size(320.dp, 160.dp).background(backdrop)) {
                Attachment(Modifier.size(240.dp, 80.dp).testTag("motion card"), onClick = {}) { }
            }
        }
        val card = composeRule.onNodeWithTag("motion card")

        fun sample(): Color {
            val pixels = card.captureToImage().toPixelMap()
            return pixels[3, pixels.height / 2]
        }

        fun between(
            value: Color,
            a: Color,
            b: Color,
        ) = value.red > minOf(a.red, b.red) + .005f && value.red < maxOf(a.red, b.red) - .005f
        assertColor(cardColor, sample())
        val originalBounds = card.fetchSemanticsNode().boundsInRoot
        composeRule.mainClock.autoAdvance = false
        try {
            card.performMouseInput {
                enter(center, delayMillis = 0)
                moveTo(center, delayMillis = 0)
            }
            composeRule.mainClock.advanceTimeByFrame()
            sample() // Resolve the animated property's first frame.
            composeRule.mainClock.advanceTimeBy(48)
            val entering = sample()
            assertTrue(between(entering, cardColor, hoverColor), "48ms must be an intermediate colour: $entering")
            card.performMouseInput { exit(delayMillis = 0) }
            assertColor(entering, sample())
            composeRule.mainClock.advanceTimeByFrame()
            sample()
            composeRule.mainClock.advanceTimeBy(48)
            val returning = sample()
            assertTrue(
                between(returning, entering, cardColor),
                "Reversal must interpolate from the current colour: $returning",
            )
            composeRule.mainClock.advanceTimeBy(200)
            assertColor(cardColor, sample())
            card.performMouseInput {
                enter(center, delayMillis = 0)
                moveTo(center, delayMillis = 0)
            }
            composeRule.mainClock.advanceTimeByFrame()
            sample()
            composeRule.mainClock.advanceTimeBy(200)
            assertColor(hoverColor, sample())
            assertEquals(originalBounds, card.fetchSemanticsNode().boundsInRoot)
        } finally {
            composeRule.mainClock.autoAdvance = true
        }
    }

    @Test fun descendantFocusRingsCardWithoutChangingLayoutInBothInputModesAndThemes() {
        var dark by mutableStateOf(false)
        lateinit var input: InputModeManager
        var ring = Color.Unspecified
        var background = Color.Unspecified
        setThemedContent(stylePreset = StylePreset.Nova) {
            Theme(isDark = dark, preset = StylePreset.Nova) {
                input = LocalInputModeManager.current
                ring = currentTheme.colors.ring
                background = currentTheme.colors.background
                Box(Modifier.size(400.dp, 180.dp).background(background).testTag("surface")) {
                    Attachment(Modifier.width(280.dp).align(Alignment.Center).testTag("card"), actions = {
                        AttachmentActions {
                            AttachmentAction({}, Modifier.testTag("action"), label = "Remove file") { Text { "x" } }
                        }
                    }) { AttachmentContent("report.pdf", "PDF") }
                    Box(Modifier.size(20.dp).testTag("outside").clickable {})
                }
            }
        }
        val original = composeRule.onNodeWithTag("card").fetchSemanticsNode().boundsInRoot
        val surface = composeRule.onNodeWithTag("surface").fetchSemanticsNode().boundsInRoot
        for (theme in listOf(false, true, false)) for (mode in listOf(InputMode.Touch, InputMode.Keyboard)) {
            composeRule.runOnIdle {
                dark = theme
                input.requestInputMode(mode)
            }
            composeRule.onNodeWithTag("action").requestFocus()
            composeRule.onNodeWithTag("action").assertIsFocused()
            val bounds = composeRule.onNodeWithTag("card").fetchSemanticsNode().boundsInRoot
            assertEquals(original, bounds, "focus-within must not alter layout")
            val pixels = composeRule.onNodeWithTag("surface").captureToImage().toPixelMap()
            val x = (bounds.left - surface.left).toInt() - 1
            val y = ((bounds.top + bounds.bottom) / 2 - surface.top).toInt()
            assertColor(ring.copy(alpha = .5f).compositeOver(background), pixels[x, y])
        }
        composeRule.onNodeWithTag("outside").requestFocus()
        composeRule.onNodeWithTag("outside").assertIsFocused()
        val pixels = composeRule.onNodeWithTag("surface").captureToImage().toPixelMap()
        assertColor(
            background,
            pixels[
                (original.left - surface.left).toInt() - 1,
                ((original.top + original.bottom) / 2 - surface.top).toInt(),
            ],
        )
    }

    @Test fun standaloneTriggerCoversParentAndRespectsZOrderRegardlessOfInsertionOrder() {
        var preview = 0
        var underlay = 0
        var action = 0
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.size(280.dp, 100.dp).testTag("host")) {
                AttachmentTrigger({ preview++ }, Modifier.testTag("trigger"), label = "Preview report.pdf")
                Box(Modifier.fillMaxSize().clickable { underlay++ })
                Box(Modifier.align(Alignment.TopEnd).zIndex(20f)) {
                    AttachmentAction(
                        { action++ },
                        Modifier.testTag("action"),
                        label = "Remove report.pdf",
                    ) { Text { "x" } }
                }
            }
        }
        assertEquals(
            composeRule.onNodeWithTag("host").fetchSemanticsNode().boundsInRoot,
            composeRule.onNodeWithTag("trigger").fetchSemanticsNode().boundsInRoot,
        )
        composeRule.onNodeWithContentDescription("Preview report.pdf").assertHasClickAction()
        composeRule.onNodeWithTag("host").performTouchInput { click(Offset(20f, 70f)) }
        composeRule.onNodeWithTag("action").performTouchInput { click() }
        assertEquals(1, preview)
        assertEquals(1, action)
        assertEquals(0, underlay)
    }

    @Test fun standaloneTriggerKeyboardAndBothDisableFlagsFollowLiveChanges() {
        var enabled by mutableStateOf(true)
        var disabled by mutableStateOf(false)
        var clicks = 0
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.size(240.dp, 80.dp)) {
                AttachmentTrigger({ clicks++ }, Modifier.testTag("trigger"), enabled, disabled, "Preview report.pdf")
            }
        }
        val node = composeRule.onNodeWithTag("trigger")
        node.requestFocus()
        node.performKeyInput {
            pressKey(Key.Enter)
            pressKey(Key.Spacebar)
        }
        assertEquals(2, clicks)
        for ((isEnabled, isDisabled) in listOf(false to false, true to true)) {
            composeRule.runOnIdle {
                enabled = isEnabled
                disabled = isDisabled
            }
            node.assertIsNotEnabled()
            node.performTouchInput { click() }
            node.performKeyInput {
                pressKey(Key.Enter)
                pressKey(Key.Spacebar)
            }
            assertEquals(2, clicks)
        }
        composeRule.runOnIdle {
            enabled = true
            disabled = false
        }
        node.requestFocus()
        node.performKeyInput { pressKey(Key.Enter) }
        assertEquals(3, clicks)
    }

    private fun assertColor(
        expected: Color,
        actual: Color,
    ) {
        assertTrue(
            listOf(expected.red - actual.red, expected.green - actual.green, expected.blue - actual.blue)
                .all { abs(it) <= 2f / 255f },
            "$expected != $actual",
        )
    }
}
