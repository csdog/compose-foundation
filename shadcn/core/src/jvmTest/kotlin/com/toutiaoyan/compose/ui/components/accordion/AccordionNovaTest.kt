package com.toutiaoyan.compose.ui.components.accordion

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import com.toutiaoyan.compose.ui.components.input.Input
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import com.toutiaoyan.compose.ui.components.card.Card
import com.toutiaoyan.compose.ui.components.card.CardContent
import com.toutiaoyan.compose.ui.theme.currentTheme
import kotlin.test.assertEquals
import com.toutiaoyan.compose.ui.components.button.Button
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test

class AccordionNovaTest : ScreenshotTest() {
    @Test fun navigationSkipsTriggerDisabledStateAndTracksReenabling() {
        var triggerDisabled by mutableStateOf(true)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Accordion {
                AccordionItem("a") { AccordionTrigger("First", Modifier.testTag("first")) }
                AccordionItem("b") {
                    AccordionTrigger("Middle", Modifier.testTag("middle"), disabled = triggerDisabled)
                }
                AccordionItem("c") { AccordionTrigger("Last", Modifier.testTag("last")) }
            }
        }
        composeRule.onNodeWithTag("first").requestFocus()
        composeRule.onNodeWithTag("first").performKeyInput { pressKey(Key.DirectionDown) }
        composeRule.onNodeWithTag("last").assertIsFocused()
        composeRule.runOnIdle { triggerDisabled = false }
        composeRule.onNodeWithTag("last").performKeyInput { pressKey(Key.DirectionUp) }
        composeRule.onNodeWithTag("middle").assertIsFocused()
        composeRule.runOnIdle { triggerDisabled = true }
        composeRule.onNodeWithTag("first").requestFocus()
        composeRule.onNodeWithTag("first").performKeyInput { pressKey(Key.DirectionDown) }
        composeRule.onNodeWithTag("last").assertIsFocused()
    }

    @Test fun novaTriggerUsesCompactPaddingAndToggles() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Accordion {
                AccordionItem("shipping") {
                    AccordionTrigger("Shipping", Modifier.testTag("trigger"))
                    AccordionContent("Delivery information")
                }
            }
        }
        composeRule.onNodeWithTag("trigger").assertHeightIsEqualTo(42.dp)
        composeRule.onNodeWithText("Delivery information").assertDoesNotExist()
        composeRule.onNodeWithTag("trigger").performClick()
        composeRule.onNodeWithText("Delivery information").assertIsDisplayed()
        composeRule.onNodeWithTag("trigger").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Delivery information").assertDoesNotExist()
    }

    @Test fun verticalNavigationSkipsDisabledAndSupportsHomeEnd() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Accordion {
                AccordionItem("a") { AccordionTrigger("First", Modifier.testTag("first")) }
                AccordionItem("b", disabled = true) { AccordionTrigger("Disabled") }
                AccordionItem("c") { AccordionTrigger("Last", Modifier.testTag("last")) }
            }
        }
        composeRule.onNodeWithTag("first").performClick()
        composeRule.onNodeWithTag("first").performKeyInput { pressKey(Key.DirectionDown) }
        composeRule.onNodeWithTag("last").assertIsFocused()
        composeRule.onNodeWithTag("last").performKeyInput { pressKey(Key.MoveHome) }
        composeRule.onNodeWithTag("first").assertIsFocused()
        composeRule.onNodeWithTag("first").performKeyInput { pressKey(Key.MoveEnd) }
        composeRule.onNodeWithTag("last").assertIsFocused()
    }

    @Test fun horizontalRtlUsesLeftForForwardAndRightForBackward() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Accordion(orientation = AccordionOrientation.Horizontal) {
                    AccordionItem("a") { AccordionTrigger("First", Modifier.testTag("first")) }
                    AccordionItem("b") { AccordionTrigger("Second", Modifier.testTag("second")) }
                }
            }
        }
        composeRule.onNodeWithTag("first").performClick()
        composeRule.onNodeWithTag("first").performKeyInput { pressKey(Key.DirectionLeft) }
        composeRule.onNodeWithTag("second").assertIsFocused()
        composeRule.onNodeWithTag("second").performKeyInput { pressKey(Key.DirectionRight) }
        composeRule.onNodeWithTag("first").assertIsFocused()
    }

    @Test fun externalCollapseReturnsFocusAndHidesKeptContent() {
        var selected by mutableStateOf(setOf("a"))
        setThemedContent(stylePreset = StylePreset.Nova) {
            Accordion(value = selected, onValueChange = { selected = it }) {
                AccordionItem("a") {
                    AccordionTrigger("Heading", Modifier.testTag("trigger"))
                    AccordionContent(keepMounted = true) {
                        Button("Inside", onClick = {}, modifier = Modifier.testTag("inside"))
                    }
                }
            }
        }
        composeRule.onNodeWithTag("inside").requestFocus()
        composeRule.onNodeWithTag("inside").assertIsFocused()
        composeRule.runOnIdle { selected = emptySet() }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("trigger").assertIsFocused()
        composeRule.onNodeWithTag("inside").assertDoesNotExist()
        composeRule.onNodeWithTag("trigger").performClick()
        composeRule.onNodeWithTag("inside").assertIsDisplayed()
    }

    @Test fun keyboardUsesVisualOrderAfterKeyedReordering() {
        var order by mutableStateOf(listOf("a", "b", "c"))
        setThemedContent(stylePreset = StylePreset.Nova) {
            Accordion {
                order.forEach { value ->
                    key(value) {
                        AccordionItem(value) { AccordionTrigger(value, Modifier.testTag(value)) }
                    }
                }
            }
        }
        composeRule.runOnIdle { order = listOf("c", "a", "b") }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("c").requestFocus()
        composeRule.onNodeWithTag("c").performKeyInput { pressKey(Key.DirectionDown) }
        composeRule.onNodeWithTag("a").assertIsFocused()
        composeRule.onNodeWithTag("a").performKeyInput { pressKey(Key.MoveEnd) }
        composeRule.onNodeWithTag("b").assertIsFocused()
    }

    @Test fun horizontalLtrReorderFollowsTheCurrentStack() {
        assertHorizontalReorder(LayoutDirection.Ltr)
    }

    @Test fun horizontalRtlReorderFollowsTheCurrentStack() {
        assertHorizontalReorder(LayoutDirection.Rtl)
    }

    @Test fun horizontalRtlUnequalWidthsUseStackOrder() {
        assertHorizontalReorder(LayoutDirection.Rtl, unequalWidths = true)
    }

    private fun assertHorizontalReorder(
        direction: LayoutDirection,
        unequalWidths: Boolean = false,
    ) {
        var order by mutableStateOf(listOf("a", "b", "c"))
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                Accordion(orientation = AccordionOrientation.Horizontal) {
                    order.forEach { value ->
                        key(value) {
                            AccordionItem(
                                value,
                                modifier =
                                    if (unequalWidths) {
                                        Modifier.width(
                                            when (value) {
                                                "a" ->
                                                    160.dp
                                                "b" ->
                                                    240.dp
                                                else
                                                -> 200.dp
                                            },
                                        )
                                    } else {
                                        Modifier
                                    },
                            ) { AccordionTrigger(value, Modifier.testTag(value)) }
                        }
                    }
                }
            }
        }
        composeRule.runOnIdle { order = listOf("c", "b", "a") }
        composeRule.onNodeWithTag("c").requestFocus()
        composeRule.onNodeWithTag("c").performKeyInput {
            pressKey(if (direction == LayoutDirection.Rtl) Key.DirectionLeft else Key.DirectionRight)
        }
        composeRule.onNodeWithTag("b").assertIsFocused()
        composeRule.onNodeWithTag("b").performKeyInput { pressKey(Key.MoveHome) }
        composeRule.onNodeWithTag("c").assertIsFocused()
        composeRule.onNodeWithTag("c").performKeyInput { pressKey(Key.MoveEnd) }
        composeRule.onNodeWithTag("a").assertIsFocused()
    }

    @Test fun cardNestedAccordionUsesThemeForeground() {
        var foreground = Color.Unspecified
        setThemedContent(stylePreset = StylePreset.Nova, darkTheme = true) {
            foreground = currentTheme.colors.foreground
            Card {
                CardContent {
                    Accordion(defaultValue = setOf("a")) {
                        AccordionItem("a", "Question", "Answer")
                    }
                }
            }
        }
        listOf("Question", "Answer").forEach { text ->
            val results = mutableListOf<TextLayoutResult>()
            composeRule.onNodeWithText(
                text,
            ).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
            assertEquals(foreground, results.single().layoutInput.style.color)
        }
    }

    @Test fun lastDividerFollowsKeyedReordering() {
        var order by mutableStateOf(listOf("a", "b", "c"))
        setThemedContent(stylePreset = StylePreset.Nova) {
            Accordion {
                order.forEach { value ->
                    key(value) {
                        AccordionItem(value, Modifier.testTag("item-$value")) { AccordionTrigger(value) }
                    }
                }
            }
        }
        composeRule.onNodeWithTag("item-c").assertHeightIsEqualTo(42.dp)
        composeRule.onNodeWithTag("item-b").assertHeightIsEqualTo(43.dp)
        composeRule.runOnIdle { order = listOf("c", "a", "b") }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("item-b").assertHeightIsEqualTo(42.dp)
        composeRule.onNodeWithTag("item-c").assertHeightIsEqualTo(43.dp)
    }

    @Test fun keptInputRetainsStateAndTabSkipsItWhileHidden() {
        var selected by mutableStateOf(setOf("a"))
        setThemedContent(stylePreset = StylePreset.Nova) {
            Column {
                Accordion(value = selected, onValueChange = { selected = it }) {
                    AccordionItem("a") {
                        AccordionTrigger("Heading", Modifier.testTag("heading"))
                        AccordionContent(keepMounted = true) {
                            var text by remember { mutableStateOf("") }
                            Input(text, onValueChange = { text = it }, modifier = Modifier.testTag("input"))
                        }
                    }
                }
                Button("After", onClick = {}, modifier = Modifier.testTag("after"))
            }
        }
        composeRule.onNodeWithTag("input").performTextInput("retained")
        composeRule.runOnIdle { selected = emptySet() }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("heading").assertIsFocused()
        composeRule.onNodeWithTag("heading").performKeyInput { pressKey(Key.Tab) }
        composeRule.onNodeWithTag("after").assertIsFocused()
        composeRule.runOnIdle { selected = setOf("a") }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("input").assertTextContains("retained")
    }

    @Test fun animationReversesFromItsCurrentHeight() {
        var expanded by mutableStateOf(false)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Accordion(value = if (expanded) setOf("a") else emptySet()) {
                AccordionItem("a", Modifier.testTag("panel")) {
                    AccordionTrigger("Heading")
                    AccordionContent { Box(Modifier.height(160.dp)) }
                }
            }
        }

        fun height() = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow.height
        val closed = height()
        composeRule.mainClock.autoAdvance = false
        composeRule.runOnIdle { expanded = true }
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeBy(80)
        val middle = height()
        kotlin.test.assertTrue(middle > closed && middle < closed + 170f)
        composeRule.runOnIdle { expanded = false }
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeBy(32)
        kotlin.test.assertTrue(height() < middle && height() > closed)
        composeRule.mainClock.advanceTimeBy(300)
        kotlin.test.assertTrue(kotlin.math.abs(height() - closed) < 1f)
        composeRule.mainClock.autoAdvance = true
    }

    @Test fun focusRingDoesNotFillTransparentTriggerInterior() {
        var mode: InputModeManager? = null
        setThemedContent(stylePreset = StylePreset.Nova) {
            mode = LocalInputModeManager.current
            Box(Modifier.background(Color.White).testTag("white surface")) {
                Accordion {
                    AccordionItem("a") { AccordionTrigger("Heading", Modifier.testTag("focus trigger")) }
                }
            }
        }
        composeRule.runOnIdle { mode!!.requestInputMode(InputMode.Keyboard) }
        composeRule.onNodeWithTag("focus trigger").requestFocus()
        composeRule.waitForIdle()
        val pixels = composeRule.onNodeWithTag("white surface").captureToImage().toPixelMap()
        val interior = pixels[pixels.width / 2, 7]
        kotlin.test.assertTrue(
            interior.red > .99f && interior.green > .99f && interior.blue > .99f,
            "Focus must not paint the transparent interior grey: $interior",
        )
    }

    @Test fun controlledRootCanRejectChangeAndDisabledItemNeverRequestsIt() {
        var requests = 0
        setThemedContent(stylePreset = StylePreset.Nova) {
            Accordion(value = setOf("a"), onValueChange = { requests++ }) {
                AccordionItem("a", "Open", "Existing answer")
                AccordionItem("b", "Other", "Rejected answer")
                AccordionItem("c", "Disabled", "Disabled answer", disabled = true)
            }
        }
        composeRule.onNodeWithText("Other").performClick()
        composeRule.waitForIdle()
        assertEquals(1, requests)
        composeRule.onNodeWithText("Existing answer").assertIsDisplayed()
        composeRule.onNodeWithText("Rejected answer").assertDoesNotExist()
        composeRule.onNodeWithText("Disabled").assertIsNotEnabled().performClick()
        assertEquals(1, requests)
        composeRule.onNodeWithText("Disabled answer").assertDoesNotExist()
    }

    @Test fun forceMountHidesSemanticsAndRestoresFocusWithoutDiscardingState() {
        var expanded by mutableStateOf(true)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Accordion(value = if (expanded) setOf("a") else emptySet()) {
                AccordionItem("a") {
                    AccordionTrigger("Heading", Modifier.testTag("force heading"))
                    AccordionContent(forceMount = true) {
                        var text by remember { mutableStateOf("") }
                        Input(text, onValueChange = { text = it }, modifier = Modifier.testTag("force input"))
                    }
                }
            }
        }
        composeRule.onNodeWithTag("force input").performTextInput("saved")
        composeRule.runOnIdle { expanded = false }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("force heading").assertIsFocused()
        composeRule.onNodeWithTag("force input").assertDoesNotExist()
        composeRule.runOnIdle { expanded = true }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("force input").assertTextContains("saved")
    }
}
