@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.hovercard

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.toutiaoyan.compose.ui.theme.currentTheme
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.overlay.PopupPlacement
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class HoverCardParityTest : ScreenshotTest() {
    private fun advance(ms: Long) {
        composeRule.mainClock.advanceTimeBy(ms)
        composeRule.waitForIdle()
    }

    private fun enter() {
        composeRule.onNodeWithText("Trigger").performMouseInput {
            enter(center, delayMillis = 0)
            moveTo(center, delayMillis = 0)
        }
        advance(16)
    }

    @Composable
    private fun HoverCardScope.Trigger(
        delay: Int? = null,
        closeDelay: Int? = null,
    ) {
        HoverCardTrigger(delay = delay, closeDelay = closeDelay) { Button("Trigger", onClick = {}) }
        HoverCardContent(
            modifier =
                Modifier.testTag("panel"),
            enter =
                EnterTransition.None,
            exit =
                ExitTransition.None,
        ) {
            Text { "Preview" }
        }
    }

    @Test fun defaultOpenAndExternalOpenDoNotCloseOnIdleComposition() {
        val calls = mutableListOf<Boolean>()
        setThemedContent(stylePreset = StylePreset.Nova) {
            HoverCard(open = true, onOpenChange = { calls.add(it) }) { Trigger() }
        }
        composeRule.mainClock.autoAdvance = false
        advance(1500)
        composeRule.onNodeWithText("Preview").assertIsDisplayed()
        assertTrue(calls.isEmpty())
    }

    @Test fun baseDefaultsWaitSixHundredToOpenAndThreeHundredToClose() {
        setThemedContent(stylePreset = StylePreset.Nova) { HoverCard { Trigger() } }
        composeRule.mainClock.autoAdvance = false
        enter()
        advance(450)
        composeRule.onNodeWithText("Preview").assertDoesNotExist()
        advance(200)
        composeRule.onNodeWithText("Preview").assertIsDisplayed()
        composeRule.onNodeWithText("Trigger").performMouseInput { exit(delayMillis = 0) }
        advance(100)
        composeRule.onNodeWithText("Preview").assertIsDisplayed()
        advance(250)
        composeRule.onNodeWithText("Preview").assertDoesNotExist()
    }

    @Test fun triggerTimingOverridesTheRootAndProvider() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            HoverCardProvider(openDelay = 2000, closeDelay = 2000) {
                HoverCard(openDelay = 1500, closeDelay = 1500) { Trigger(delay = 10, closeDelay = 100) }
            }
        }
        composeRule.mainClock.autoAdvance = false
        enter()
        advance(50)
        composeRule.onNodeWithText("Preview").assertIsDisplayed()
        composeRule.onNodeWithText("Trigger").performMouseInput { exit(delayMillis = 0) }
        advance(150)
        composeRule.onNodeWithText("Preview").assertDoesNotExist()
    }

    @Test fun aPendingOpenUsesTheLatestCallback() {
        var revision by mutableStateOf("old")
        val calls = mutableListOf<String>()
        setThemedContent(stylePreset = StylePreset.Nova) {
            val current = revision
            HoverCard(onOpenChange = { calls.add(current) }) { Trigger(delay = 300) }
        }
        composeRule.mainClock.autoAdvance = false
        enter()
        advance(100)
        composeRule.runOnIdle { revision = "new" }
        advance(250)
        assertEquals(listOf("new"), calls)
    }

    @Test fun enteringTheCardCancelsTheCloseTimer() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            HoverCard { Trigger(delay = 0, closeDelay = 300) }
        }
        composeRule.mainClock.autoAdvance = false
        enter()
        advance(50)
        composeRule.onNodeWithText("Preview").assertIsDisplayed()
        composeRule.onNodeWithTag("panel").performMouseInput { moveTo(center, delayMillis = 0) }
        advance(500)
        composeRule.onNodeWithText("Preview").assertIsDisplayed()
        composeRule.onNodeWithTag("panel").performMouseInput { exit(delayMillis = 0) }
        advance(350)
        composeRule.onNodeWithText("Preview").assertDoesNotExist()
    }

    @Test fun hoveringASeparateRootChildDoesNotActivateTheTrigger() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            HoverCard {
                Column {
                    HoverCardTrigger { Button("Trigger", onClick = {}) }
                    Box(Modifier.width(200.dp).height(100.dp).testTag("other"))
                }
                HoverCardContent(enter = EnterTransition.None, exit = ExitTransition.None) { Text { "Preview" } }
            }
        }
        composeRule.mainClock.autoAdvance = false
        composeRule.onNodeWithTag("other").performMouseInput {
            enter(center, delayMillis = 0)
            moveTo(center, delayMillis = 0)
        }
        advance(1000)
        composeRule.onNodeWithText("Preview").assertDoesNotExist()
    }

    @Test fun keyboardFocusOpensAndEscapeClosesWithoutStealingFocus() {
        lateinit var mode: InputModeManager
        setThemedContent(stylePreset = StylePreset.Nova) {
            mode = LocalInputModeManager.current
            HoverCard { Trigger(delay = 0) }
        }
        composeRule.runOnIdle { mode.requestInputMode(InputMode.Keyboard) }
        composeRule.onNodeWithText("Trigger").requestFocus()
        composeRule.onNodeWithText("Preview").assertIsDisplayed()
        composeRule.onNodeWithText("Trigger").assertIsFocused().performKeyInput { pressKey(Key.Escape) }
        composeRule.onNodeWithText("Preview").assertDoesNotExist()
        composeRule.onNodeWithText("Trigger").assertIsFocused()
    }

    @Test fun controlledRejectionDoesNotOpenOrRetryContinuously() {
        val calls = mutableListOf<Boolean>()
        setThemedContent(stylePreset = StylePreset.Nova) {
            HoverCard(open = false, onOpenChange = { calls.add(it) }) { Trigger(delay = 0) }
        }
        composeRule.mainClock.autoAdvance = false
        enter()
        advance(2000)
        composeRule.onNodeWithText("Preview").assertDoesNotExist()
        assertEquals(listOf(true), calls)
    }

    @Test fun aTouchPressDoesNotOpenAPreview() {
        lateinit var mode: InputModeManager
        setThemedContent(stylePreset = StylePreset.Nova) {
            mode = LocalInputModeManager.current
            HoverCard { Trigger(delay = 0) }
        }
        composeRule.runOnIdle { mode.requestInputMode(InputMode.Touch) }
        composeRule.onNodeWithText("Trigger").performTouchInput { click(center) }
        composeRule.mainClock.advanceTimeBy(1000)
        composeRule.onNodeWithText("Preview").assertDoesNotExist()
    }

    @Test fun disabledRootIgnoresThePointer() {
        setThemedContent(stylePreset = StylePreset.Nova) { HoverCard(disabled = true) { Trigger(delay = 0) } }
        composeRule.mainClock.autoAdvance = false
        enter()
        advance(1000)
        composeRule.onNodeWithText("Preview").assertDoesNotExist()
    }

    @Test fun physicalAndLogicalSidesResolveSeparatelyInRtl() {
        assertEquals(PopupPlacement.Start, HoverCardSide.Left.placement(LayoutDirection.Rtl))
        assertEquals(PopupPlacement.End, HoverCardSide.Right.placement(LayoutDirection.Rtl))
        assertEquals(PopupPlacement.End, HoverCardSide.InlineStart.placement(LayoutDirection.Rtl))
        assertEquals(PopupPlacement.Start, HoverCardSide.InlineEnd.placement(LayoutDirection.Rtl))
        assertEquals(PopupPlacement.Start, HoverCardSide.InlineStart.placement(LayoutDirection.Ltr))
        assertEquals(PopupPlacement.End, HoverCardSide.InlineEnd.placement(LayoutDirection.Ltr))
    }

    @Test fun novaPanelHasNoLayoutBorderAndUsesTheOfficialTextLineBox() {
        setThemedContent(stylePreset = StylePreset.Nova) { HoverCard(defaultOpen = true) { Trigger() } }
        composeRule.onNodeWithTag("panel").assertWidthIsEqualTo(256.dp).assertHeightIsEqualTo(40.dp)
        val result = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithText(
            "Preview",
        ).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(result) }
        assertEquals(14f, result.single().layoutInput.style.fontSize.value)
        assertEquals(20f, result.single().layoutInput.style.lineHeight.value)
    }

    private fun nova(dark: Boolean) {
        snapshot("hover_card_nova", darkTheme = dark, stylePreset = StylePreset.Nova) {
            HoverCard(defaultOpen = true) {
                HoverCardTrigger { Button("Hover Here", onClick = {}, variant = ButtonVariant.Link) }
                HoverCardContent {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(textStyle = TextStyle(fontWeight = FontWeight.SemiBold)) { "@nextjs" }
                        Text { "The React Framework – created and maintained by @vercel." }
                        Text(
                            modifier = Modifier.padding(top = 4.dp),
                            textStyle =
                                TextStyle(
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    color = currentTheme.colors.mutedForeground,
                                ),
                        ) { "Joined December 2021" }
                    }
                }
            }
        }
    }

    @Test fun nova_light() = nova(false)

    @Test fun nova_dark() = nova(true)
}
