@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.message

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.bubble.BubbleVariant
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import androidx.compose.ui.graphics.Color
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

    @Test fun rheaMetadataAndBodyFollowSourceAcrossThemeAndGhostChanges() {
        var dark by mutableStateOf(false)
        var ghost by mutableStateOf(false)
        var muted = Color.Unspecified
        composeRule.setContent {
            Theme(isDark = dark, preset = StylePreset.Rhea) {
                muted = currentTheme.colors.mutedForeground
                Message(Modifier.width(320.dp).testTag("row"), align = MessageAlign.End) {
                    MessageHeader("Sender", Modifier.testTag("header"))
                    Text { "Body" }
                    Bubble("Surface", variant = if (ghost) BubbleVariant.Ghost else BubbleVariant.Muted)
                    MessageFooter("Delivered", Modifier.testTag("footer"))
                }
            }
        }

        fun layout(text: String): TextLayoutResult {
            val values = mutableListOf<TextLayoutResult>()
            composeRule.onNodeWithText(text).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(values) }
            return values.single()
        }

        fun bounds(tag: String) = composeRule.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot

        fun check(inset: Float) {
            composeRule.waitForIdle()
            assertEquals(14f, layout("Body").layoutInput.style.fontSize.value)
            assertEquals(20f, layout("Body").layoutInput.style.lineHeight.value)
            listOf("Sender", "Delivered").forEach {
                val style = layout(it).layoutInput.style
                assertEquals(12f, style.fontSize.value)
                assertEquals(16f, style.lineHeight.value)
                assertEquals(FontWeight.Medium, style.fontWeight)
                assertEquals(muted, style.color)
            }
            assertEquals(bounds("row").left, bounds("header").left, .1f)
            assertEquals(bounds("row").right, bounds("footer").right, .1f)
            val sender = composeRule.onNodeWithText("Sender").fetchSemanticsNode().boundsInRoot
            assertEquals(inset, sender.left - bounds("header").left, .1f)
        }
        check(12f)
        composeRule.runOnIdle {
            dark = true
            ghost = true
        }
        check(0f)
        composeRule.runOnIdle { ghost = false }
        check(12f)
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
