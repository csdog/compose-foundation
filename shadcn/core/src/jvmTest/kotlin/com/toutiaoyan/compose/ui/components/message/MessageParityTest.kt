@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.message

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.bubble.*
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class MessageParityTest : ScreenshotTest() {
    private fun layout(text: String): TextLayoutResult {
        val values = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithText(text, useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(values) }
        return values.single()
    }

    @Test fun noAvatarHasNoPhantomGapAndContentGapIsTen() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Message(Modifier.width(320.dp).testTag("row")) {
                Text { "First" }
                Text { "Second" }
            }
        }
        val row = composeRule.onNodeWithTag("row").fetchSemanticsNode().boundsInRoot
        val first = composeRule.onNodeWithText("First").fetchSemanticsNode().boundsInRoot
        val second = composeRule.onNodeWithText("Second").fetchSemanticsNode().boundsInRoot
        assertEquals(row.left, first.left, 0.1f)
        assertEquals(10f, second.top - first.bottom, 0.1f)
        assertEquals(14f, layout("First").layoutInput.style.fontSize.value)
        assertEquals(20f, layout("First").layoutInput.style.lineHeight.value)
    }

    @Test fun avatarHasIntrinsicHeightAndEmptyAvatarOnlyReservesWidth() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Column {
                MessageAvatar(Modifier.testTag("empty"))
                MessageAvatar(Modifier.testTag("large")) { Box(Modifier.size(48.dp)) }
            }
        }
        composeRule.onNodeWithTag("empty").assertWidthIsEqualTo(32.dp).assertHeightIsEqualTo(0.dp)
        composeRule.onNodeWithTag("large").assertWidthIsEqualTo(48.dp).assertHeightIsEqualTo(48.dp)
    }

    @Test fun footerPresenceTranslatesAvatarExactlyThirtyTwoAndRemovalRestoresIt() {
        var footers by mutableStateOf(2)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Message(Modifier.width(320.dp).testTag("row"), avatar = {
                MessageAvatar(Modifier.testTag("avatar")) { Box(Modifier.size(32.dp)) }
            }) {
                Box(Modifier.height(80.dp))
                repeat(footers) { MessageFooter("Footer $it") }
            }
        }

        fun clearance(): Float {
            composeRule.waitForIdle()
            return composeRule.onNodeWithTag("row").fetchSemanticsNode().boundsInRoot.bottom -
                composeRule.onNodeWithTag("avatar").fetchSemanticsNode().boundsInRoot.bottom
        }
        assertEquals(32f, clearance(), 0.1f)
        composeRule.runOnIdle { footers = 1 }
        assertEquals(32f, clearance(), 0.1f)
        composeRule.runOnIdle { footers = 0 }
        assertEquals(0f, clearance(), 0.1f)
    }

    @Test fun ghostBubbleRemovesMetadataPaddingAndDisposalRestoresItWithoutLeakingToSibling() {
        var ghost by mutableStateOf(false)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Column(Modifier.width(320.dp)) {
                Message {
                    MessageHeader("Sender", Modifier.testTag("header"))
                    Bubble("Body", variant = if (ghost) BubbleVariant.Ghost else BubbleVariant.Muted)
                    MessageFooter("Delivered", Modifier.testTag("footer"))
                }
                Message { MessageHeader("Sibling", Modifier.testTag("sibling")) }
            }
        }

        fun inset(
            tag: String,
            text: String,
        ): Float {
            composeRule.waitForIdle()
            return composeRule.onNodeWithText(text).fetchSemanticsNode().boundsInRoot.left -
                composeRule.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot.left
        }
        assertEquals(12f, inset("header", "Sender"), 0.1f)
        composeRule.runOnIdle { ghost = true }
        assertEquals(0f, inset("header", "Sender"), 0.1f)
        assertEquals(0f, inset("footer", "Delivered"), 0.1f)
        assertEquals(12f, inset("sibling", "Sibling"), 0.1f)
        composeRule.runOnIdle { ghost = false }
        assertEquals(12f, inset("footer", "Delivered"), 0.1f)
    }

    @Test fun metadataUsesTwelveSixteenMediumAndUpdatesMutedColorWithTheme() {
        var dark by mutableStateOf(false)
        var expected = Color.Unspecified
        composeRule.setContent {
            Theme(isDark = dark, preset = StylePreset.Nova) {
                expected = currentTheme.colors.mutedForeground
                Message {
                    MessageHeader("Sender")
                    MessageFooter("Delivered")
                }
            }
        }

        fun check() {
            composeRule.waitForIdle()
            listOf("Sender", "Delivered").forEach {
                val style = layout(it).layoutInput.style
                assertEquals(12f, style.fontSize.value)
                assertEquals(16f, style.lineHeight.value)
                assertEquals(FontWeight.Medium, style.fontWeight)
                assertEquals(expected, style.color)
            }
        }
        check()
        composeRule.runOnIdle { dark = true }
        check()
    }

    @Test fun endMessageKeepsHeaderAtStartAndFooterAtEndAsDocumented() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Message(Modifier.width(320.dp).testTag("row"), align = MessageAlign.End) {
                MessageHeader("Sender", Modifier.testTag("header"))
                Bubble("Hello")
                MessageFooter("Delivered", Modifier.testTag("footer"))
            }
        }
        val row = composeRule.onNodeWithTag("row").fetchSemanticsNode().boundsInRoot
        assertEquals(row.left, composeRule.onNodeWithTag("header").fetchSemanticsNode().boundsInRoot.left, 0.1f)
        assertEquals(row.right, composeRule.onNodeWithTag("footer").fetchSemanticsNode().boundsInRoot.right, 0.1f)
    }

    @Test fun standaloneContentAndRootAcceptCallerModifierAndStyle() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Column(Modifier.width(320.dp)) {
                Message(style = Style { width(100.dp) }, modifier = Modifier.testTag("row")) { Text { "Body" } }
                MessageContent(
                    modifier =
                        Modifier.testTag("content"),
                    style =
                        Style { width(80.dp) },
                ) { Text { "Slot" } }
            }
        }
        composeRule.onNodeWithTag("row").assertWidthIsEqualTo(100.dp)
        composeRule.onNodeWithTag("content").assertWidthIsEqualTo(80.dp)
    }

    @Test fun endAvatarAndContentMirrorWhenLayoutDirectionChanges() {
        var rtl by mutableStateOf(false)
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(
                LocalLayoutDirection provides
                    if (rtl) {
                        LayoutDirection.Rtl
                    } else {
                        LayoutDirection.Ltr
                    },
            ) {
                Message(Modifier.width(320.dp).testTag("row"), align = MessageAlign.End, avatar = {
                    MessageAvatar(Modifier.testTag("avatar")) { Box(Modifier.size(32.dp)) }
                }) { Text { "Body" } }
            }
        }
        val row = composeRule.onNodeWithTag("row").fetchSemanticsNode().boundsInRoot
        assertEquals(row.right, composeRule.onNodeWithTag("avatar").fetchSemanticsNode().boundsInRoot.right, 0.1f)
        composeRule.runOnIdle { rtl = true }
        assertEquals(row.left, composeRule.onNodeWithTag("avatar").fetchSemanticsNode().boundsInRoot.left, 0.1f)
    }

    @Test fun avatarClipsOverflowUsingItsFinalCallerOverriddenShape() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Column {
                MessageAvatar(Modifier.testTag("circle")) { Box(Modifier.size(48.dp).background(Color.Red)) }
                MessageAvatar(Modifier.testTag("rectangle"), style = Style { shape(RectangleShape) }) {
                    Box(Modifier.size(48.dp).background(Color.Red))
                }
            }
        }
        val circle = composeRule.onNodeWithTag("circle").captureToImage().toPixelMap()
        val rectangle = composeRule.onNodeWithTag("rectangle").captureToImage().toPixelMap()
        assertNotEquals(Color.Red, circle[0, 0])
        assertEquals(Color.Red, circle[24, 24])
        assertEquals(Color.Red, rectangle[0, 0])
    }

    @Test fun legacyRowScopeContentCallStillUsesTheWeightedBridge() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Row(Modifier.width(240.dp).testTag("row")) {
                MessageContent { Text { "Bridge" } }
                Box(Modifier.size(32.dp).testTag("trailing"))
            }
        }
        val row = composeRule.onNodeWithTag("row").fetchSemanticsNode().boundsInRoot
        val trailing =
            composeRule.onNodeWithTag("trailing").assertWidthIsEqualTo(32.dp).fetchSemanticsNode().boundsInRoot
        assertEquals(row.right, trailing.right, 0.1f)
        assertEquals(row.left, composeRule.onNodeWithText("Bridge").fetchSemanticsNode().boundsInRoot.left, 0.1f)
    }

    private fun nova(dark: Boolean) =
        snapshot("message_nova", darkTheme = dark, stylePreset = StylePreset.Nova) {
            Column(Modifier.width(360.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Message(avatar = { MessageAvatar("AI") }) {
                    MessageHeader("Assistant")
                    Bubble("I checked the logs.", variant = BubbleVariant.Muted)
                    MessageFooter("Delivered")
                }
                Message(align = MessageAlign.End, avatar = { MessageAvatar("ME") }) {
                    Bubble("Send the report to the team.")
                    MessageFooter("Read Yesterday")
                }
            }
        }

    private fun ghostNova(dark: Boolean) =
        snapshot(
            "message_ghost_nova",
            darkTheme =
            dark,
            stylePreset =
                StylePreset.Nova,
        ) {
            Message(Modifier.width(320.dp)) {
                MessageHeader("Assistant")
                Bubble("I checked the logs.", variant = BubbleVariant.Ghost)
                MessageFooter("Delivered")
            }
        }

    @Test fun ghost_nova_light() = ghostNova(false)

    @Test fun ghost_nova_dark() = ghostNova(true)

    @Test fun nova_light() = nova(false)

    @Test fun nova_dark() = nova(true)
}
