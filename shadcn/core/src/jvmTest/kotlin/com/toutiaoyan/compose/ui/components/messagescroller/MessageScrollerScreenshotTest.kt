@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.messagescroller

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.theme.currentTheme
import kotlin.test.Test
import com.toutiaoyan.compose.ui.components.bubble.Bubble
import com.toutiaoyan.compose.ui.components.bubble.BubbleContent
import com.toutiaoyan.compose.ui.components.bubble.BubbleVariant
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.card.Card
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import com.toutiaoyan.compose.ui.components.card.CardAction
import com.toutiaoyan.compose.ui.components.card.CardDescription
import com.toutiaoyan.compose.ui.components.card.CardHeader
import com.toutiaoyan.compose.ui.components.card.CardTitle
import com.toutiaoyan.compose.ui.components.message.Message
import com.toutiaoyan.compose.ui.components.message.MessageAlign
import com.toutiaoyan.compose.ui.components.message.MessageAvatar
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.tailwind.core.twSpacing

/**
 * The auto-scroll/button-visibility decisions ([shouldAutoScrollToBottom],
 * [isMessageScrollerNearBottom]) are unit tested directly in
 * `MessageScrollerTest`, per this project's rule against driving decision math
 * through a live gesture in a Compose UI Test. These screenshots instead cover fixed,
 * settled render states -- including the real (non-obvious) behavior that a freshly
 * composed scroller with overflowing content auto-scrolls to the bottom before the
 * first frame is captured, so no floating button appears on initial load.
 */
class MessageScrollerScreenshotTest : ScreenshotTest() {
    private fun shortContent(darkTheme: Boolean) {
        snapshot("message_scroller_short_content", darkTheme = darkTheme) {
            MessageScroller(modifier = Modifier.width(280.dp).height(160.dp)) {
                Message(avatar = { MessageAvatar("AI") }) {
                    Text { "Hi there!" }
                }
            }
        }
    }

    private fun overflowingContentSettlesAtBottom(darkTheme: Boolean) {
        snapshot("message_scroller_overflow_settles_at_bottom", darkTheme = darkTheme) {
            MessageScroller(modifier = Modifier.width(280.dp).height(160.dp)) {
                repeat(10) { index ->
                    Message(avatar = { MessageAvatar("AI") }) {
                        Text { "Message number $index" }
                    }
                }
            }
        }
    }

    private fun jumpToBottomButton(darkTheme: Boolean) {
        snapshot("message_scroller_button_visible", darkTheme = darkTheme) {
            MessageScrollerButton(visible = true, onClick = {})
        }
    }

    private fun chatPanel(darkTheme: Boolean) {
        snapshot("message_scroller_chat_panel", darkTheme = darkTheme) {
            Column {
                Card(modifier = Modifier.width(320.dp).height(420.dp)) {
                    CardHeader {
                        Box(Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(twSpacing.xs)) {
                                CardTitle { "New Chat" }
                                CardDescription { "How can I help you today?" }
                            }
                            CardAction(modifier = Modifier.align(Alignment.TopEnd)) {
                                Button(onClick = {}, variant = ButtonVariant.Outline, size = ButtonSize.Icon) {
                                    Text { "↻" }
                                }
                            }
                        }
                    }
                    MessageScroller(modifier = Modifier.weight(1f)) {
                        Message(avatar = { MessageAvatar("AI") }) {
                            Bubble {
                                BubbleContent(variant = BubbleVariant.Muted) {
                                    Text { "Hi! Ask me anything about MessageScroller." }
                                }
                            }
                        }
                        Message(
                            align = MessageAlign.End,
                            avatar = { MessageAvatar("Me") },
                        ) {
                            Bubble(align = MessageAlign.End) {
                                BubbleContent {
                                    Text { "Does it auto-scroll while I'm reading old messages?" }
                                }
                            }
                        }
                        Message(avatar = { MessageAvatar("AI") }) {
                            Bubble {
                                BubbleContent(variant = BubbleVariant.Muted) {
                                    Text {
                                        "Nope -- only when you're already near the bottom. " +
                                            "Scroll up and it leaves you alone."
                                    }
                                }
                            }
                        }
                        Message(
                            align = MessageAlign.End,
                            avatar = { MessageAvatar("Me") },
                        ) {
                            Bubble(align = MessageAlign.End) {
                                BubbleContent { Text { "And this message you're reading right now?" } }
                            }
                        }
                        Message(avatar = { MessageAvatar("AI") }) {
                            Bubble {
                                BubbleContent(variant = BubbleVariant.Muted) {
                                    Text { "Proof it works -- you're at the bottom, so it followed me here." }
                                }
                            }
                        }
                    }
                    Row(
                        modifier =
                            Modifier
                                .background(currentTheme.colors.muted, RoundedCornerShape(twRadius.full))
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Button(onClick = {}, variant = ButtonVariant.Ghost, size = ButtonSize.Icon) {
                            Text { "+" }
                        }
                        Text(
                            modifier = Modifier.weight(1f),
                            variant = TextVariant.BodySmall,
                            textStyle = TextColors.Muted,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        ) { "Okay, but when someone sends a new message the view still feels jarring…" }
                        Button(onClick = {}, variant = ButtonVariant.Default, size = ButtonSize.Icon) {
                            Text { "↑" }
                        }
                    }
                }
                Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) { "Demo is read only." }
            }
        }
    }

    @Test
    fun short_content_light() = shortContent(darkTheme = false)

    @Test
    fun short_content_dark() = shortContent(darkTheme = true)

    @Test
    fun overflowing_content_settles_at_bottom_light() = overflowingContentSettlesAtBottom(darkTheme = false)

    @Test
    fun overflowing_content_settles_at_bottom_dark() = overflowingContentSettlesAtBottom(darkTheme = true)

    @Test
    fun jump_to_bottom_button_light() = jumpToBottomButton(darkTheme = false)

    /**
     * [MessageScroller]'s own `icon` parameter forwards to the default [button]
     * slot's `MessageScrollerButton.icon` -- proves the wiring through a real swipe
     * away from the auto-settled bottom (not a forced `visible = true`, which only tests
     * [MessageScrollerButton] in isolation, not the forwarding).
     */
    @Test
    fun icon_override_light() {
        setThemedContent(darkTheme = false) {
            MessageScroller(
                modifier = Modifier.width(280.dp).height(160.dp).testTag("scroller"),
                icon = { rotationDegrees ->
                    Text(modifier = Modifier.graphicsLayer(rotationZ = rotationDegrees)) { "X" }
                },
            ) {
                repeat(10) { index ->
                    Message(avatar = { MessageAvatar("AI") }) {
                        Text { "Message number $index" }
                    }
                }
            }
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("scroller").performTouchInput { swipeDown() }
        composeRule.waitForIdle()
        captureNamed("message_scroller_icon_override", darkTheme = false)
    }

    @Test
    fun chat_panel_light() = chatPanel(darkTheme = false)

    @Test
    fun chat_panel_dark() = chatPanel(darkTheme = true)

    private fun contentItems(darkTheme: Boolean) {
        snapshot("message_scroller_content_items", darkTheme = darkTheme) {
            MessageScroller(modifier = Modifier.width(280.dp).height(200.dp)) {
                MessageScrollerContent(spacing = MessageScrollerDefaults.ContentSpacing) {
                    MessageScrollerItem {
                        Message(avatar = { MessageAvatar("AI") }) { Text { "First" } }
                    }
                    MessageScrollerItem {
                        Message(avatar = { MessageAvatar("AI") }) { Text { "Second" } }
                    }
                    MessageScrollerItem {
                        Message(avatar = { MessageAvatar("AI") }) { Text { "Third" } }
                    }
                }
            }
        }
    }

    @Test
    fun content_items_light() = contentItems(darkTheme = false)

    @Test
    fun content_items_dark() = contentItems(darkTheme = true)

    private fun startButton(darkTheme: Boolean) {
        snapshot("message_scroller_button_start", darkTheme = darkTheme) {
            MessageScrollerButton(
                visible = true,
                onClick = {},
                direction = MessageScrollerDirection.Start,
            )
        }
    }

    @Test
    fun button_start_light() = startButton(darkTheme = false)

    @Test
    fun button_start_dark() = startButton(darkTheme = true)

    private fun officialTree(darkTheme: Boolean) {
        snapshot("message_scroller_viewport", darkTheme = darkTheme) {
            MessageScrollerProvider {
                MessageScroller(modifier = Modifier.width(280.dp).height(200.dp)) {
                    MessageScrollerViewport(contentDescription = MessageScrollerDefaults.ViewportLabel) {
                        MessageScrollerContent(spacing = MessageScrollerDefaults.ContentSpacing) {
                            MessageScrollerItem {
                                Message(avatar = { MessageAvatar("AI") }) { Text { "First" } }
                            }
                            MessageScrollerItem {
                                Message(avatar = { MessageAvatar("AI") }) { Text { "Second" } }
                            }
                            MessageScrollerItem {
                                Message(avatar = { MessageAvatar("AI") }) { Text { "Third" } }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun viewport_light() = officialTree(darkTheme = false)

    @Test
    fun viewport_dark() = officialTree(darkTheme = true)
}
