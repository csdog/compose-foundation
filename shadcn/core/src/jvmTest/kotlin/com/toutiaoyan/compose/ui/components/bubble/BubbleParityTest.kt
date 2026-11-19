@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.bubble

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.message.MessageAlign
import com.toutiaoyan.compose.ui.components.message.Message
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.tailwind.core.Oklch
import com.toutiaoyan.compose.tailwind.core.TwColors
import com.toutiaoyan.compose.tailwind.core.toColor
import kotlin.test.*

class BubbleParityTest : ScreenshotTest() {
    @Test fun naturalWidthIsCappedAndGhostCanUseFullRow() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            BubbleGroup(Modifier.width(400.dp)) {
                Bubble("Hi", Modifier.testTag("short"))
                Bubble("Long content ".repeat(40), Modifier.testTag("long"))
                Bubble("Ghost content ".repeat(40), Modifier.testTag("ghost"), variant = BubbleVariant.Ghost)
            }
        }

        fun width(tag: String) = composeRule.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot.width
        assertTrue(width("short") < 100f)
        assertEquals(320f, width("long"), .1f)
        assertEquals(400f, width("ghost"), .1f)
        val results = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithText("Hi").performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
        assertEquals(14f, results.single().layoutInput.style.fontSize.value)
        assertEquals(22.75f, results.single().layoutInput.style.lineHeight.value)
        val actualHeight = composeRule.onNodeWithTag("short").fetchSemanticsNode().boundsInRoot.height
        assertEquals(results.single().size.height + 18f, actualHeight, .1f)
    }

    @Test fun shortReactionDoesNotGrowBubbleAndOverflowButtonReceivesRealPointer() {
        var clicked = false
        setThemedContent(stylePreset = StylePreset.Nova) {
            BubbleGroup(Modifier.width(300.dp)) {
                Bubble(Modifier.testTag("bubble")) {
                    BubbleContent("Hi", Modifier.testTag("content"))
                    BubbleReactions(style = Style { contentPadding(0.dp) }) {
                        Button("Yes, run it", onClick = { clicked = true }, size = ButtonSize.Xs)
                    }
                }
            }
        }
        val root = composeRule.onNodeWithTag("bubble").fetchSemanticsNode().boundsInRoot
        val content = composeRule.onNodeWithTag("content").fetchSemanticsNode().boundsInRoot
        assertEquals(content.width, root.width, .1f)
        assertEquals(content.height, root.height, .1f)
        composeRule.onNodeWithText("Yes, run it").performMouseInput { click(center) }
        assertTrue(clicked, "A reaction outside the visual content must receive actual pointer input")
    }

    @Test fun alignmentMirrorsWithLayoutDirection() {
        var rtl by mutableStateOf(false)
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(
                LocalLayoutDirection provides if (rtl) LayoutDirection.Rtl else LayoutDirection.Ltr,
            ) {
                BubbleGroup(Modifier.width(400.dp).testTag("group")) {
                    Bubble("Start", Modifier.testTag("start"))
                    Bubble("End", Modifier.testTag("end"), align = MessageAlign.End)
                }
            }
        }

        fun bounds(tag: String) = composeRule.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot
        assertEquals(bounds("group").left, bounds("start").left, .1f)
        assertEquals(bounds("group").right, bounds("end").right, .1f)
        composeRule.runOnIdle { rtl = true }
        assertEquals(bounds("group").right, bounds("start").right, .1f)
        assertEquals(bounds("group").left, bounds("end").left, .1f)
    }

    @Test fun interactiveContentIsDisabledAndKeyboardActivatesOnlyWhenEnabled() {
        var enabled by mutableStateOf(false)
        var clicks = 0
        setThemedContent(stylePreset = StylePreset.Nova) {
            BubbleContent("Reply", onClick = { clicks++ }, enabled = enabled)
        }
        composeRule.onNodeWithText("Reply").assertIsNotEnabled().performClick()
        assertEquals(0, clicks)
        composeRule.runOnIdle { enabled = true }
        composeRule.onNodeWithText("Reply").performMouseInput { click(center) }
        assertEquals(1, clicks)
        composeRule.onNodeWithText("Reply").requestFocus().performKeyInput { pressKey(Key.Enter) }
        assertEquals(2, clicks)
        composeRule.onNodeWithText("Reply").performKeyInput { pressKey(Key.Spacebar) }
        assertEquals(3, clicks)
    }

    @Test fun tintUsesOpaqueRelativeOklchInsteadOfAnAlphaWash() {
        for (dark in listOf(false, true)) {
            val expected = Oklch(if (dark) .3f else .93f, .245f * .4f, 262.881f).toColor()
            val actual = bubbleTintedColor(TwColors.blue600, dark)
            assertEquals(1f, actual.alpha)
            assertEquals(expected.red, actual.red, .003f)
            assertEquals(expected.green, actual.green, .003f)
            assertEquals(expected.blue, actual.blue, .003f)
        }
    }

    @Test fun messageAlignmentIsInheritedUnlessTheBubbleSpecifiesItsOwn() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Message(
                align = MessageAlign.End,
                modifier = Modifier.width(400.dp),
                avatar = { Box(Modifier.size(24.dp)) },
            ) {
                Column(Modifier.fillMaxWidth().testTag("body")) {
                    Bubble("Short", Modifier.testTag("inherited"))
                    Bubble("Explicit", Modifier.testTag("explicit"), align = MessageAlign.Start)
                }
            }
        }
        val parent = composeRule.onNodeWithTag("body").fetchSemanticsNode().boundsInRoot
        val bubble = composeRule.onNodeWithTag("inherited").fetchSemanticsNode().boundsInRoot
        assertEquals(parent.right, bubble.right, .1f)
        assertTrue(bubble.left > parent.left)
        val explicit = composeRule.onNodeWithTag("explicit").fetchSemanticsNode().boundsInRoot
        assertEquals(parent.left, explicit.left, .1f)
    }

    @Test fun hoverMixUsesOklchInterpolationForNeutralColors() {
        val expected = Oklch(.97f * .95f + .145f * .05f, 0f, 0f).toColor()
        val actual = bubbleHoverMix(Oklch(.97f, 0f, 0f).toColor(), Oklch(.145f, 0f, 0f).toColor())
        assertEquals(expected.red, actual.red, .003f)
        assertEquals(expected.green, actual.green, .003f)
        assertEquals(expected.blue, actual.blue, .003f)
    }
}
