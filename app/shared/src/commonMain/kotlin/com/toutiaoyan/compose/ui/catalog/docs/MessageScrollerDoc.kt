@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ArrowDown
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.RefreshCw
import com.composables.icons.lucide.Send
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.components.bubble.Bubble
import com.toutiaoyan.compose.ui.components.bubble.BubbleContent
import com.toutiaoyan.compose.ui.components.bubble.BubbleVariant
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.card.Card
import com.toutiaoyan.compose.ui.components.card.CardAction
import com.toutiaoyan.compose.ui.components.card.CardDescription
import com.toutiaoyan.compose.ui.components.card.CardHeader
import com.toutiaoyan.compose.ui.components.card.CardTitle
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroup
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupAddon
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupAddonAlign
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupButton
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupButtonSize
import com.toutiaoyan.compose.ui.components.message.Message
import com.toutiaoyan.compose.ui.components.message.MessageAlign
import com.toutiaoyan.compose.ui.components.message.MessageAvatar
import com.toutiaoyan.compose.ui.components.messagescroller.MessageScroller
import com.toutiaoyan.compose.ui.components.messagescroller.MessageScrollerButton
import com.toutiaoyan.compose.ui.components.messagescroller.MessageScrollerContent
import com.toutiaoyan.compose.ui.components.messagescroller.MessageScrollerDefaults
import com.toutiaoyan.compose.ui.components.messagescroller.MessageScrollerDirection
import com.toutiaoyan.compose.ui.components.messagescroller.MessageScrollerItem
import com.toutiaoyan.compose.ui.components.messagescroller.MessageScrollerProvider
import com.toutiaoyan.compose.ui.components.messagescroller.MessageScrollerViewport
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.styles.shimmer
import com.toutiaoyan.compose.ui.theme.currentTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Chat-composer key handling, matching every real chat app's convention: plain Enter
 * sends (consumed, so BasicTextField never sees it and can't insert a newline);
 * Shift+Enter is left unconsumed so BasicTextField's own default multiline behavior
 * inserts the newline; Tab/Shift+Tab move focus instead of doing nothing or typing a
 * literal tab character. Requires the field to be `singleLine = false` -- a truly
 * single-line field has nowhere to put a Shift+Enter newline in the first place.
 *
 * `onPreviewKeyEvent` (not `onKeyEvent`): the interception must win *before*
 * BasicTextField's own key handling runs, not after -- `preview` handlers are visited
 * root-to-focused-leaf, ahead of the focused node's own handling, which is exactly
 * what's needed to stop the default newline-on-Enter behavior from ever firing.
 */
private fun composerKeyHandler(
    focusManager: FocusManager,
    onSend: () -> Unit,
): (KeyEvent) -> Boolean =
    keyEvent@{ event ->
        if (event.type != KeyEventType.KeyDown) return@keyEvent false
        when {
            (event.key == Key.Enter || event.key == Key.NumPadEnter) && !event.isShiftPressed -> {
                onSend()
                true
            }

            event.key == Key.Tab && !event.isShiftPressed -> {
                focusManager.moveFocus(FocusDirection.Next)
                true
            }

            event.key == Key.Tab && event.isShiftPressed -> {
                focusManager.moveFocus(FocusDirection.Previous)
                true
            }

            else -> false
        }
    }

// Overrides MessageScroller's own text-glyph "↓" default with a real Lucide
// vector -- this library ships with no icon dependency of its own (see README), but the
// catalog app already depends on icons-lucide-cmp for every other icon, so its own demos
// shouldn't fall back to the library's placeholder glyph.
private val DocMessageScrollerIcon: @Composable (rotationDegrees: Float) -> Unit = { rotationDegrees ->
    DocIcon(Lucide.ArrowDown, modifier = Modifier.graphicsLayer(rotationZ = rotationDegrees))
}

/** One row in the interactive demo's transcript -- [text] grows in place while [isUser] is false and streaming. */
private data class ScrollerDemoMessage(
    val id: Int,
    val text: String,
    val isUser: Boolean,
)

// Cycled per reply so repeated sends in the same demo session don't all say the same thing.
private val demoReplies =
    listOf(
        "Watch this reply stream in character by character -- MessageScroller keeps you pinned to the " +
                "bottom the whole time, unless you scroll away to read something above.",
        "That's the auto-scroll in action. Try scrolling up mid-stream and it'll release -- it won't jump " +
                "back down until you press the button or send another message.",
        "A streaming reply is really just content height growing one character at a time. Nothing special " +
                "about it from MessageScroller's side -- same auto-scroll logic either way.",
    )

private const val STREAM_TICK_MILLIS = 18L
private const val THINKING_DELAY_MILLIS = 500L

val messageScrollerDoc =
    ComponentDoc(
        id = "message-scroller",
        title = "Message Scroller",
        description =
            "A chat-transcript scroll container that follows new messages to the bottom -- but only while " +
                    "the reader hasn't scrolled away -- with a floating jump-to-bottom button when they have.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.*

            // The default jump-to-bottom icon is a plain text glyph (this library ships no
            // icon dependency of its own) -- pass a real vector via `icon` to override it.
            MessageScroller(
                modifier = Modifier.fillMaxSize(),
                icon = { rotationDegrees -> DocIcon(Lucide.ArrowDown, modifier = Modifier.graphicsLayer(rotationZ = rotationDegrees)) },
            ) {
                messages.forEach { message ->
                    Message(avatar = { MessageAvatar("AI") }) {
                        Text { message.text }
                    }
                }
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        // modifier must constrain the main-axis size (here: a fixed height) --
                        // this composable fills whatever it's given.
                        MessageScroller(
                            modifier = Modifier.width(280.dp).height(200.dp),
                            icon = { rotationDegrees -> DocIcon(Lucide.ArrowDown, modifier = Modifier.graphicsLayer(rotationZ = rotationDegrees)) },
                        ) {
                            repeat(10) { index ->
                                Message(avatar = { MessageAvatar("AI") }) {
                                    Text { "Message number ${'$'}index" }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        MessageScroller(
                            modifier = Modifier.width(280.dp).height(200.dp),
                            icon = DocMessageScrollerIcon,
                        ) {
                            repeat(10) { index ->
                                Message(avatar = { MessageAvatar("AI") }) {
                                    Text { "Message number $index" }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Content items",
                    code =
                        """
                        MessageScroller(
                            modifier = Modifier.width(280.dp).height(200.dp),
                            icon = { rotationDegrees -> DocIcon(Lucide.ArrowDown, modifier = Modifier.graphicsLayer(rotationZ = rotationDegrees)) },
                        ) {
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
                        """.trimIndent(),
                    preview = {
                        MessageScroller(
                            modifier = Modifier.width(280.dp).height(200.dp),
                            icon = DocMessageScrollerIcon,
                        ) {
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
                    },
                ),
                ComponentExample(
                    title = "Official tree",
                    code =
                        """
                        MessageScrollerProvider {
                            MessageScroller(
                                modifier = Modifier.width(280.dp).height(200.dp),
                                icon = { rotationDegrees -> DocIcon(Lucide.ArrowDown, modifier = Modifier.graphicsLayer(rotationZ = rotationDegrees)) },
                            ) {
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
                        """.trimIndent(),
                    preview = {
                        MessageScrollerProvider {
                            MessageScroller(
                                modifier = Modifier.width(280.dp).height(200.dp),
                                icon = DocMessageScrollerIcon,
                            ) {
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
                    },
                ),
                ComponentExample(
                    title = "Default threshold",
                    code =
                        """
                        MessageScroller(
                            modifier = Modifier.width(280.dp).height(200.dp),
                            autoScrollThresholdPx = 64,
                            icon = { rotationDegrees -> DocIcon(Lucide.ArrowDown, modifier = Modifier.graphicsLayer(rotationZ = rotationDegrees)) },
                        ) {
                            repeat(10) { index ->
                                Message(avatar = { MessageAvatar("AI") }) {
                                    Text { "Message number ${'$'}index" }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        MessageScroller(
                            modifier = Modifier.width(280.dp).height(200.dp),
                            autoScrollThresholdPx = 64,
                            icon = DocMessageScrollerIcon,
                        ) {
                            repeat(10) { index ->
                                Message(avatar = { MessageAvatar("AI") }) {
                                    Text { "Message number $index" }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Button",
                    code =
                        """
                        MessageScroller(
                            modifier = Modifier.width(280.dp).height(200.dp),
                            icon = { rotationDegrees ->
                                DocIcon(Lucide.ArrowDown, modifier = Modifier.graphicsLayer(rotationZ = rotationDegrees))
                            },
                            button = { visible, onClick ->
                                MessageScrollerButton(
                                    visible = visible,
                                    onClick = onClick,
                                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = twSpacing.md),
                                    icon = { rotationDegrees ->
                                        DocIcon(Lucide.ArrowDown, modifier = Modifier.graphicsLayer(rotationZ = rotationDegrees))
                                    },
                                )
                            },
                        ) {
                            repeat(10) { index ->
                                Message(avatar = { MessageAvatar("AI") }) {
                                    Text { "Message number ${'$'}index" }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        MessageScroller(
                            modifier = Modifier.width(280.dp).height(200.dp),
                            icon = DocMessageScrollerIcon,
                            button = { visible, onClick ->
                                MessageScrollerButton(
                                    visible = visible,
                                    onClick = onClick,
                                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = twSpacing.md),
                                    icon = DocMessageScrollerIcon,
                                )
                            },
                        ) {
                            repeat(10) { index ->
                                Message(avatar = { MessageAvatar("AI") }) {
                                    Text { "Message number $index" }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Start button",
                    code =
                        """
                        MessageScrollerButton(
                            visible = true,
                            onClick = {},
                            direction = MessageScrollerDirection.Start,
                            icon = { rotationDegrees ->
                                DocIcon(Lucide.ArrowDown, modifier = Modifier.graphicsLayer(rotationZ = rotationDegrees))
                            },
                        )
                        """.trimIndent(),
                    preview = {
                        MessageScrollerButton(
                            visible = true,
                            onClick = {},
                            direction = MessageScrollerDirection.Start,
                            icon = DocMessageScrollerIcon,
                        )
                    },
                ),
                ComponentExample(
                    title = "Chat panel with streaming replies",
                    code =
                        """
                        // Real shadcn's own message-scroller demo streams AI replies in with a
                        // useChat()-driven typewriter effect -- growing message content is exactly
                        // what exercises auto-scroll for real, not just a fixed transcript. This
                        // reproduces that with a plain coroutine appending one character at a time.
                        var messages by remember { mutableStateOf(listOf(ScrollerDemoMessage(0, "Hi! Ask me anything.", false))) }
                        var nextId by remember { mutableIntStateOf(1) }
                        var composerText by remember { mutableStateOf("") }
                        var isStreaming by remember { mutableStateOf(false) }
                        val coroutineScope = rememberCoroutineScope()

                        fun send() {
                            val prompt = composerText.ifBlank { return }
                            composerText = ""
                            messages = messages + ScrollerDemoMessage(nextId++, prompt, isUser = true)
                            val replyId = nextId++
                            val reply = demoReplies[replyId % demoReplies.size]
                            messages = messages + ScrollerDemoMessage(replyId, "", isUser = false)
                            isStreaming = true
                            coroutineScope.launch {
                                for (charCount in 1..reply.length) {
                                    delay(18)
                                    messages = messages.map {
                                        if (it.id == replyId) it.copy(text = reply.take(charCount)) else it
                                    }
                                }
                                isStreaming = false
                            }
                        }

                        Card(modifier = Modifier.width(320.dp).height(420.dp)) {
                            CardHeader("New Chat", description = "How can I help you today?")
                            MessageScroller(
                                modifier = Modifier.weight(1f),
                                icon = { rotationDegrees -> DocIcon(Lucide.ArrowDown, modifier = Modifier.graphicsLayer(rotationZ = rotationDegrees)) },
                            ) {
                                messages.forEach { message ->
                                    Message(
                                        align = if (message.isUser) MessageAlign.End else MessageAlign.Start,
                                        avatar = { MessageAvatar(if (message.isUser) "Me" else "AI") },
                                    ) {
                                        Bubble(align = if (message.isUser) MessageAlign.End else MessageAlign.Start) {
                                            BubbleContent(
                                                variant = if (message.isUser) BubbleVariant.Default else BubbleVariant.Muted,
                                            ) {
                                                if (message.text.isEmpty() && !message.isUser) {
                                                    Text(textStyle = TextStyles.Muted, modifier = Modifier.shimmer()) { "Thinking…" }
                                                } else {
                                                    Text { message.text }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            // The group owns the single border/focus ring; the inner field drops its own.
                            InputGroup {
                                Input(
                                    value = composerText,
                                    onValueChange = { composerText = it },
                                    placeholder = "Message MessageScroller…",
                                    singleLine = false,
                                    modifier = Modifier.onPreviewKeyEvent { event ->
                                        if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                                        when {
                                            event.key == Key.Enter && !event.isShiftPressed -> {
                                                send(); true
                                            }
                                            event.key == Key.Tab && !event.isShiftPressed -> {
                                                focusManager.moveFocus(FocusDirection.Next); true
                                            }
                                            else -> false
                                        }
                                    },
                                )
                                InputGroupAddon(InputGroupAddonAlign.InlineEnd) {
                                    InputGroupButton(
                                        onClick = ::send,
                                        variant = ButtonVariant.Default,
                                        size = InputGroupButtonSize.IconSm,
                                        enabled = composerText.isNotBlank() && !isStreaming,
                                    ) {
                                        DocIcon(Lucide.Send, tint = currentTheme.colors.primaryForeground)
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { InteractiveChatPanel() },
                ),
            ),
    )

@Composable
@Preview
private fun InteractiveChatPanel() {
    var messages by
    remember {
        mutableStateOf(
            listOf(ScrollerDemoMessage(0, "Hi! Ask me anything about MessageScroller.", isUser = false)),
        )
    }
    var nextId by remember { mutableIntStateOf(1) }
    var composerText by remember { mutableStateOf("") }
    var isStreaming by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    fun send() {
        val prompt = composerText.ifBlank { return }
        composerText = ""
        messages = messages + ScrollerDemoMessage(nextId++, prompt, isUser = true)
        val replyId = nextId++
        val reply = demoReplies[replyId % demoReplies.size]
        messages = messages + ScrollerDemoMessage(replyId, "", isUser = false)
        isStreaming = true
        coroutineScope.launch {
            delay(THINKING_DELAY_MILLIS) // lets the shimmering "Thinking…" placeholder actually be seen.
            for (charCount in 1..reply.length) {
                delay(STREAM_TICK_MILLIS)
                messages = messages.map { if (it.id == replyId) it.copy(text = reply.take(charCount)) else it }
            }
            isStreaming = false
        }
    }

    fun sendIfEligible() {
        if (composerText.isNotBlank() && !isStreaming) send()
    }

    fun reset() {
        messages = listOf(ScrollerDemoMessage(0, "Hi! Ask me anything about MessageScroller.", isUser = false))
        nextId = 1
        composerText = ""
        isStreaming = false
    }

    Card(modifier = Modifier.width(320.dp).height(420.dp)) {
        CardHeader {
            Box(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(twSpacing.xs)) {
                    CardTitle { "New Chat" }
                    CardDescription { "How can I help you today?" }
                }
                CardAction(modifier = Modifier.align(Alignment.TopEnd)) {
                    Button(onClick = ::reset, variant = ButtonVariant.Outline, size = ButtonSize.Icon, style = {
                        shape(CircleShape)
                    }) {
                        DocIcon(Lucide.RefreshCw)
                    }
                }
            }
        }
        MessageScroller(modifier = Modifier.weight(1f), icon = DocMessageScrollerIcon) {
            messages.forEach { message ->
                Message(
                    align = if (message.isUser) MessageAlign.End else MessageAlign.Start,
                    avatar = {
                        MessageAvatar(if (message.isUser) "Me" else "AI")
                    },
                ) {
                    Bubble(align = if (message.isUser) MessageAlign.End else MessageAlign.Start) {
                        BubbleContent(
                            variant = if (message.isUser) BubbleVariant.Default else BubbleVariant.Muted,
                        ) {
                            if (message.text.isEmpty() && !message.isUser) {
                                Text(textStyle = TextColors.Muted, modifier = Modifier.shimmer()) { "Thinking…" }
                            } else {
                                Text { message.text }
                            }
                        }
                    }
                }
            }
        }
        // InputGroup, not a hand-rolled Row -- the group owns the single
        // border/focus ring and the inner Input automatically drops its
        // own (LocalInsideInputGroup), so focusing the composer highlights the
        // whole pill once instead of nesting a second focus-ring box inside it.
        InputGroup {
            InputGroupAddon(InputGroupAddonAlign.InlineStart) {
                InputGroupButton(onClick = {}, size = InputGroupButtonSize.IconSm) {
                    DocIcon(Lucide.Plus)
                }
            }
            Input(
                value = composerText,
                onValueChange = { composerText = it },
                placeholder = "Message MessageScroller…",
                singleLine = false,
                modifier = Modifier.onPreviewKeyEvent(composerKeyHandler(focusManager, ::sendIfEligible)),
            )
            InputGroupAddon(InputGroupAddonAlign.InlineEnd) {
                InputGroupButton(
                    onClick = ::send,
                    variant = ButtonVariant.Default,
                    size = InputGroupButtonSize.IconSm,
                    enabled = composerText.isNotBlank() && !isStreaming,
                ) {
                    DocIcon(Lucide.Send, tint = currentTheme.colors.primaryForeground)
                }
            }
        }
    }
}
