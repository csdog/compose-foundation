@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.message

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.bubble.Bubble
import com.toutiaoyan.compose.ui.components.bubble.BubbleContent
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class MessageRheaTest : ScreenshotTest() {
    @Test fun emptyAvatarAndContentSpacingMatchTheRheaConversation() {
        setThemedContent(stylePreset = StylePreset.Rhea) {
            MessageGroup(Modifier.width(400.dp)) {
                Message(Modifier.testTag("first"), avatar = { MessageAvatar(Modifier.testTag("empty")) }) {
                    Bubble("First")
                }
                Message(Modifier.testTag("second"), avatar = { MessageAvatar("AI", Modifier.testTag("avatar")) }) {
                    Bubble("Second", Modifier.testTag("surface"))
                    MessageFooter("Delivered", Modifier.testTag("footer"))
                }
            }
        }
        composeRule.waitForIdle()

        fun bounds(tag: String) = composeRule.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot
        composeRule.onNodeWithTag("empty").assertWidthIsEqualTo(32.dp).assertHeightIsEqualTo(0.dp)
        assertEquals(8f, bounds("second").top - bounds("first").bottom, .1f)
        assertEquals(10f, bounds("footer").top - bounds("surface").bottom, .1f)
        assertEquals(bounds("second").bottom - 32f, bounds("avatar").bottom, .1f)
    }

    @Test fun rheaBubbleHasTenPixelVerticalPaddingAndOnePixelBorder() {
        setThemedContent(stylePreset = StylePreset.Rhea) {
            Column(Modifier.width(400.dp)) {
                Bubble { BubbleContent("Message", Modifier.testTag("surface")) }
            }
        }
        val results = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithText(
            "Message",
        ).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
        val surface = composeRule.onNodeWithTag("surface").fetchSemanticsNode().boundsInRoot
        assertEquals(results.single().size.height + 22f, surface.height, .1f)
    }
}
