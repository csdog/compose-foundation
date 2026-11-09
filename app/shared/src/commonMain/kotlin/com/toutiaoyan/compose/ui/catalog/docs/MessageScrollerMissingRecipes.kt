@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.toutiaoyan.compose.ui.components.bubble.*
import com.toutiaoyan.compose.ui.components.button.*
import com.toutiaoyan.compose.ui.components.empty.*
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.marker.*
import com.toutiaoyan.compose.ui.components.message.*
import com.toutiaoyan.compose.ui.components.messagescroller.*
import com.toutiaoyan.compose.ui.components.select.*
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.togglegroup.*
import com.toutiaoyan.compose.ui.theme.currentTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// region demo -----------------------------------------------------------------
private val demoPrompt =
    "I'm building a chat for our app and the scroll behavior is driving me nuts. " +
        "Every time the AI streams a reply, the whole thread jumps around."

private const val demoReply =
    "That's the classic streaming scroll problem. Wrap your message list in " +
        "`MessageScroller` and turn on `autoScroll` — the viewport pins to the bottom as " +
        "tokens arrive, so users always see the latest text land in place.\n\n" +
        "The important part: it only auto-scrolls while the reader is already at the bottom. " +
        "The moment they scroll up to read something earlier, auto-scroll backs off and " +
        "their position is preserved."

@Composable
internal fun MessageScrollerDemoRecipe() {
    var messages by remember {
        mutableStateOf(listOf(ScrollerRecipeMessage("demo-1", true, demoPrompt)))
    }
    var streaming by remember { mutableStateOf(false) }
    var started by remember { mutableStateOf(false) }
    var streamJob by remember { mutableStateOf<Job?>(null) }
    val scope = rememberCoroutineScope()

    fun send() {
        if (streaming) return
        started = true
        val replyId = "demo-2"
        messages = messages + ScrollerRecipeMessage(replyId, false, "")
        streaming = true
        streamJob =
            scope.launch {
                for (count in 1..demoReply.length) {
                    delay(6)
                    messages =
                        messages.map {
                            if (it.id == replyId) it.copy(text = demoReply.take(count)) else it
                        }
                }
                streaming = false
            }
    }

    MessageScrollerProvider(autoScroll = true) {
        ScrollerRecipeFrame(
            title = "New Chat",
            description = "How can we help you today?",
            note = "Demo is read only. Press send to start messages.",
            action = {
                Button(
                    onClick = {
                        streamJob?.cancel()
                        streamJob = null
                        messages = listOf(ScrollerRecipeMessage("demo-1", true, demoPrompt))
                        streaming = false
                        started = false
                    },
                    variant = ButtonVariant.Outline,
                    size = ButtonSize.Icon,
                    label = "Reset conversation",
                ) { DocIcon(Lucide.RefreshCw) }
            },
            footer = {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(color = currentTheme.colors.mutedForeground, fontSize = 13.sp),
                    ) { if (started) demoPrompt.take(48) + "…" else demoPrompt }
                    Button(onClick = ::send, enabled = !streaming, label = "Send") {
                        DocIcon(Lucide.ArrowUp, tint = currentTheme.colors.primaryForeground)
                    }
                }
            },
        ) {
            MessageScroller {
                MessageScrollerViewport(Modifier.testTag("demo-viewport")) {
                    MessageScrollerContent(Modifier.padding(16.dp)) {
                        if (!started) {
                            RecipeEmptyState(
                                title = "Morning, shadcn!",
                                description = "How are we working on today? Press send to start a new conversation.",
                            )
                        } else {
                            ScrollerRecipeTranscript(messages)
                        }
                    }
                }
                MessageScrollerButton()
            }
        }
    }
}

@Composable
private fun RecipeEmptyState(
    title: String,
    description: String,
) {
    Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Empty {
            EmptyHeader {
                EmptyMedia(variant = EmptyMediaVariant.Icon) {
                    DocIcon(Lucide.MessageCircleDashed)
                }
                EmptyTitle(title)
                EmptyDescription(description)
            }
        }
    }
}
// endregion demo --------------------------------------------------------------

// region anchoring ------------------------------------------------------------
private val anchoringScript =
    listOf(
        ScrollerRecipeMessage(
            "anchor-1-user",
            true,
            "Can you show me how anchoring behaves when a new prompt starts the turn?",
        ),
        ScrollerRecipeMessage(
            "anchor-1-assistant",
            false,
            "Append the user prompt first, then append the assistant response. With User " +
                "selected, the prompt settles near the top and the assistant response fills " +
                "in below it.",
        ),
        ScrollerRecipeMessage(
            "anchor-2-user",
            true,
            "What changes when assistant messages are the anchor?",
        ),
        ScrollerRecipeMessage(
            "anchor-2-assistant",
            false,
            "Now each assistant response is the item MessageScroller keeps in view. This is " +
                "useful when the reply is the moment you want readers to land on after each turn.",
        ),
        ScrollerRecipeMessage(
            "anchor-3-user",
            true,
            "Can I switch roles and keep adding turns?",
        ),
        ScrollerRecipeMessage(
            "anchor-3-assistant",
            false,
            "Yes. The next appended message with the selected role becomes the anchor, so you " +
                "can compare user and assistant anchoring without resetting the demo.",
        ),
    )

@Composable
internal fun MessageScrollerAnchoringRecipe() {
    var anchorAssistant by remember { mutableStateOf(false) }
    var count by remember { mutableIntStateOf(0) }
    val shown = anchoringScript.take(count)
    MessageScrollerProvider {
        ScrollerRecipeFrame(
            title = "Anchoring Turns",
            description = "Choose which role settles near the top edge.",
            note = "Toggle the anchor role, then send messages to compare where turns settle.",
            action = {
                Button(
                    onClick = {
                        count = 0
                        anchorAssistant = false
                    },
                    enabled = count > 0,
                    variant = ButtonVariant.Outline,
                    size = ButtonSize.Icon,
                    label = "Reset anchored turns",
                ) { DocIcon(Lucide.RefreshCw) }
            },
            footer = {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ToggleGroup(
                        value = if (anchorAssistant) "assistant" else "user",
                        onValueChange = {
                            anchorAssistant = it == "assistant"
                            count = 0
                        },
                    ) {
                        ToggleGroupItem("user", "User")
                        ToggleGroupItem("assistant", "Assistant")
                    }
                    Spacer(Modifier.weight(1f))
                    Button(
                        onClick = { if (count < anchoringScript.size) count += 1 },
                        enabled = count < anchoringScript.size,
                        size = ButtonSize.Icon,
                        label = "Send Message",
                    ) { DocIcon(Lucide.ArrowUp, tint = currentTheme.colors.primaryForeground) }
                }
            },
        ) {
            if (shown.isEmpty()) {
                RecipeEmptyState(
                    title = "No anchored messages yet",
                    description = "Send the first message to see the selected role anchor.",
                )
            } else {
                MessageScroller {
                    MessageScrollerViewport(Modifier.testTag("anchoring-viewport")) {
                        MessageScrollerContent(Modifier.padding(16.dp)) {
                            shown.forEach { message ->
                                key(message.id) {
                                    val anchored = message.user != anchorAssistant
                                    MessageScrollerItem(
                                        Modifier.testTag(message.id),
                                        messageId = message.id,
                                        scrollAnchor = anchored,
                                    ) {
                                        Message(
                                            align = if (message.user) MessageAlign.End else MessageAlign.Start,
                                        ) {
                                            Bubble(
                                                variant =
                                                    if (message.user) BubbleVariant.Muted else BubbleVariant.Ghost,
                                            ) {
                                                BubbleContent { Text { message.text } }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    MessageScrollerButton()
                }
            }
        }
    }
}
// endregion anchoring ---------------------------------------------------------

// region group chat -----------------------------------------------------------
private data class GroupChatItem(
    val id: String,
    val sender: String?,
    val text: String,
    val assistant: Boolean = false,
    val event: Boolean = false,
    val anchor: Boolean = false,
)

private val groupChatInitial =
    listOf(
        GroupChatItem(
            "group-1",
            "Grace",
            "@mary, the astrophage line keeps matching Venus energy output. Can you check my math?",
        ),
        GroupChatItem(
            "group-2",
            "Mary (Agent)",
            "Yes. Confirmed. The curve points to a microorganism harvesting stellar energy and " +
                "breeding near carbon dioxide. If @rocky agrees, this is the clue we need.",
            assistant = true,
        ),
        GroupChatItem("group-3", "Grace", "ping @rocky", anchor = true),
    )

private val rockyMarker = GroupChatItem("group-4", null, "Rocky has joined the chat", event = true, anchor = true)

private val rockyMessage =
    GroupChatItem(
        "group-5",
        "Rocky",
        "Amaze. Astrophage eats light, makes heat, goes to carbon dioxide. Rocky has fuel " +
            "model. Grace is smart.",
    )

@Composable
internal fun MessageScrollerGroupChatRecipe() {
    var stage by remember { mutableIntStateOf(0) }
    val items =
        when (stage) {
            0 -> groupChatInitial
            1 -> groupChatInitial + rockyMarker
            else -> groupChatInitial + rockyMarker + rockyMessage
        }
    MessageScrollerProvider {
        ScrollerRecipeFrame(
            title = "Group Chat",
            description = "A group chat with several participants and an assistant. The Marker is marked as a turn.",
            note = "When a user joins, a marker is created. scrollAnchor on the marker marks it as the next turn.",
            action = {
                Button(
                    onClick = { stage = 0 },
                    enabled = stage > 0,
                    variant = ButtonVariant.Outline,
                    size = ButtonSize.Icon,
                    label = "Reset conversation",
                ) { DocIcon(Lucide.RefreshCw) }
            },
            footer = {
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = { if (stage < 2) stage += 1 },
                        enabled = stage < 2,
                        variant = ButtonVariant.Secondary,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text { if (stage == 0) "Add Rocky" else "Send Message as Rocky" }
                    }
                    Text(
                        textStyle = TextStyle(color = currentTheme.colors.mutedForeground, fontSize = 12.sp),
                    ) {
                        if (stage == 0) {
                            "This will create a marker and make it the anchor"
                        } else {
                            "Now send Rocky's reply into the conversation"
                        }
                    }
                }
            },
        ) {
            MessageScroller {
                MessageScrollerViewport(Modifier.testTag("group-chat-viewport")) {
                    MessageScrollerContent(Modifier.padding(16.dp)) {
                        items.forEach { item ->
                            key(item.id) {
                                MessageScrollerItem(
                                    Modifier.testTag(item.id),
                                    messageId = item.id,
                                    scrollAnchor = item.anchor,
                                ) {
                                    if (item.event) {
                                        Marker(item.text, variant = MarkerVariant.Separator)
                                    } else {
                                        val me = item.sender == "Grace"
                                        Message(align = if (me) MessageAlign.End else MessageAlign.Start) {
                                            MessageContent {
                                                if (!me && item.sender != null) {
                                                    MessageHeader(item.sender)
                                                }
                                                Bubble(
                                                    variant =
                                                        when {
                                                            me -> BubbleVariant.Muted
                                                            item.assistant -> BubbleVariant.Ghost
                                                            else -> BubbleVariant.Tinted
                                                        },
                                                ) {
                                                    BubbleContent { Text { item.text } }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                MessageScrollerButton()
            }
        }
    }
}
// endregion group chat --------------------------------------------------------

// region streaming ------------------------------------------------------------
private data class StreamingTurn(
    val prompt: String,
    val reply: String,
)

private val streamingScript =
    listOf(
        StreamingTurn(
            "Okay, but when someone sends a new message the view still feels jarring — " +
                "like the whole conversation reloads from the top.",
            "MessageScrollerItem fixes that with turn anchoring. Set scrollAnchor on the turn " +
                "that should settle near the top instead of blindly snapping to the document " +
                "bottom.\n\nIt also leaves a small peek of the previous exchange visible above " +
                "the anchor, so context isn't lost.",
        ),
        StreamingTurn(
            "And if they've scrolled up to re-read an older answer? I don't want to yank them back down.",
            "You won't. Auto-scroll only runs when the viewport is already pinned to the bottom, " +
                "so scrolling up is a deliberate opt-out — their place in the thread stays put " +
                "even as new tokens keep arriving below.",
        ),
        StreamingTurn(
            "Last one — does this work with assistive tech?",
            "MessageScrollerContent marks the transcript as a live region, so screen readers " +
                "announce new messages as they stream in. The scroll button is a real button " +
                "with a label and leaves the tab order when you are already at the bottom.",
        ),
    )

@Composable
internal fun MessageScrollerStreamingRecipe() {
    var turn by remember { mutableIntStateOf(0) }
    var messages by remember { mutableStateOf(listOf<ScrollerRecipeMessage>()) }
    var streaming by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val next = streamingScript.getOrNull(turn)

    fun send() {
        val current = next ?: return
        if (streaming) return
        val promptId = "stream-user-$turn"
        val replyId = "stream-reply-$turn"
        messages =
            messages +
            ScrollerRecipeMessage(promptId, true, current.prompt) +
            ScrollerRecipeMessage(replyId, false, "")
        streaming = true
        val reply = current.reply
        scope.launch {
            for (count in 1..reply.length) {
                delay(6)
                messages =
                    messages.map {
                        if (it.id == replyId) it.copy(text = reply.take(count)) else it
                    }
            }
            streaming = false
            turn += 1
        }
    }

    MessageScrollerProvider(autoScroll = true) {
        ScrollerRecipeFrame(
            title = "Streaming Messages",
            description = "Auto-scroll follows the live edge of the conversation.",
            note = "Streaming is simulated. autoScroll is enabled.",
            action = {
                Button(
                    onClick = {
                        turn = 0
                        messages = emptyList()
                        streaming = false
                    },
                    enabled = messages.isNotEmpty() && !streaming,
                    variant = ButtonVariant.Outline,
                    size = ButtonSize.Icon,
                    label = "Reset stream",
                ) { DocIcon(Lucide.RefreshCw) }
            },
            footer = {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(color = currentTheme.colors.mutedForeground, fontSize = 13.sp),
                    ) { next?.prompt ?: "No messages queued. Reset the stream." }
                    Button(
                        onClick = ::send,
                        enabled = next != null && !streaming,
                        size = ButtonSize.Icon,
                        label = "Send",
                    ) { DocIcon(Lucide.ArrowUp, tint = currentTheme.colors.primaryForeground) }
                }
            },
        ) {
            if (messages.isEmpty()) {
                RecipeEmptyState(
                    title = "Ready to Stream",
                    description = "Press send to stream a scripted launch summary.",
                )
            } else {
                MessageScroller {
                    MessageScrollerViewport(Modifier.testTag("streaming-viewport")) {
                        MessageScrollerContent(Modifier.padding(16.dp)) {
                            ScrollerRecipeTranscript(messages)
                        }
                    }
                    MessageScrollerButton()
                }
            }
        }
    }
}
// endregion streaming ---------------------------------------------------------

// region animation ------------------------------------------------------------
private val animationPresets = listOf("Fade", "Pop", "Slide")

private data class AnimatedMessage(
    val id: String,
    val user: Boolean,
    val text: String,
    val preset: String,
)

private val animationScript =
    listOf(
        "Can user messages pop in like iMessage without breaking anchoring?" to
            "Yes. Animate the user row with transform and opacity, and let the assistant " +
            "response stream normally below it.\n\nThat keeps the row measurement predictable " +
            "while still giving the newly sent bubble a more tactile entrance.",
        "What makes the animation feel more like iMessage?" to
            "Use a quick spring from the trailing edge: a little scale, a small upward move, " +
            "and no layout animation.\n\nThe bubble feels tactile, but the measured row stays " +
            "predictable, so anchoring and auto-scroll do not have to fight a changing layout.",
        "Can I switch between presets while testing the same thread?" to
            "Yes. Keep the conversation in place while you change the preset, then send the " +
            "next message to compare the new entrance against the same context.",
    )

@Composable
internal fun MessageScrollerAnimationRecipe() {
    var preset by remember { mutableStateOf(animationPresets.first()) }
    var turn by remember { mutableIntStateOf(0) }
    var messages by remember {
        mutableStateOf(
            listOf(
                AnimatedMessage("anim-0", true, animationScript[0].first, animationPresets.first()),
                AnimatedMessage("anim-1", false, animationScript[0].second, animationPresets.first()),
            ),
        )
    }
    var streaming by remember { mutableStateOf(false) }
    var idCounter by remember { mutableIntStateOf(2) }
    val scope = rememberCoroutineScope()
    val next = animationScript.getOrNull(turn + 1)

    fun send() {
        val current = next ?: return
        if (streaming) return
        val userId = "anim-${idCounter++}"
        val replyId = "anim-${idCounter++}"
        messages =
            messages +
            AnimatedMessage(userId, true, current.first, preset) +
            AnimatedMessage(replyId, false, "", preset)
        streaming = true
        val reply = current.second
        scope.launch {
            for (count in 1..reply.length) {
                delay(6)
                messages =
                    messages.map {
                        if (it.id == replyId) it.copy(text = reply.take(count)) else it
                    }
            }
            streaming = false
            turn += 1
        }
    }

    MessageScrollerProvider {
        ScrollerRecipeFrame(
            title = "Animation",
            description = "Choose how user messages are animated when they are added to the conversation.",
            note = "Select an animation then click send to see it in action.",
            action = {
                Button(
                    onClick = {
                        messages =
                            listOf(
                                AnimatedMessage("anim-0", true, animationScript[0].first, preset),
                                AnimatedMessage("anim-1", false, animationScript[0].second, preset),
                            )
                        turn = 0
                        idCounter = 2
                        streaming = false
                    },
                    enabled = messages.isNotEmpty() && !streaming,
                    variant = ButtonVariant.Outline,
                    size = ButtonSize.Icon,
                    label = "Reset animated messages",
                ) { DocIcon(Lucide.RefreshCw) }
            },
            footer = {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Select(value = preset, onValueChange = { preset = it }) {
                        SelectTrigger(
                            Modifier.width(144.dp),
                            size = SelectSize.Sm,
                            accessibilityLabel = "Animation preset",
                        )
                        SelectContent {
                            animationPresets.forEach { presetName ->
                                SelectItem(presetName) { Text { presetName } }
                            }
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    Button(
                        onClick = ::send,
                        enabled = next != null && !streaming,
                        size = ButtonSize.Icon,
                        label = "Send Message",
                    ) { DocIcon(Lucide.ArrowUp, tint = currentTheme.colors.primaryForeground) }
                }
            },
        ) {
            if (messages.isEmpty()) {
                RecipeEmptyState(
                    title = "No Messages Yet",
                    description = "Click the button below to send the first message.",
                )
            } else {
                MessageScroller {
                    MessageScrollerViewport(Modifier.testTag("animation-viewport")) {
                        MessageScrollerContent(Modifier.padding(16.dp)) {
                            messages.forEach { message ->
                                key(message.id) {
                                    AnimatedRow(message)
                                }
                            }
                        }
                    }
                    MessageScrollerButton()
                }
            }
        }
    }
}

@Composable
private fun AnimatedRow(message: AnimatedMessage) {
    var entered by remember(message.id) { mutableStateOf(false) }
    val progress =
        animateFloatAsState(
            if (!message.user || entered) 1f else 0f,
            tween(260),
            label = "message-entrance",
        )
    LaunchedEffect(message.id) { entered = true }
    val fromScale =
        when (message.preset) {
            "Pop" -> 0.9f
            "Slide" -> 1f
            else -> 1f
        }
    val fromTy = if (message.preset == "Fade") 0.dp else 10.dp
    MessageScrollerItem(Modifier.testTag(message.id), messageId = message.id) {
        Message(
            modifier =
                Modifier.graphicsLayer {
                    alpha = progress.value
                    scaleX = fromScale + (1f - fromScale) * progress.value
                    scaleY = fromScale + (1f - fromScale) * progress.value
                    translationY = fromTy.toPx() * (1f - progress.value)
                },
            align = if (message.user) MessageAlign.End else MessageAlign.Start,
        ) {
            Bubble(
                variant = if (message.user) BubbleVariant.Muted else BubbleVariant.Ghost,
            ) {
                BubbleContent { Text { message.text } }
            }
        }
    }
}
// endregion animation ---------------------------------------------------------

internal val messageScrollerPageGuideSections =
    listOf(
        ComponentGuideSection(
            title = "What Makes a Great Streaming Chat Experience",
            blocks =
                listOf(
                    ComponentGuideBlock.Paragraph(
                        (
                            "Building a chat interface used to be simple. You create an inverted " +
                                "list with an input. Type a message, it appends at the bottom. When " +
                                "a reply comes in, the list grows and scrolls. Streaming breaks " +
                                "that model: messages arrive in chunks while you may still be " +
                                "reading, scrolling, or looking somewhere else entirely."
                        ),
                    ),
                    ComponentGuideBlock.Paragraph(
                        (
                            "Now the challenge is preserving the reader's place while the " +
                                "conversation keeps changing. A great streaming chat should:"
                        ),
                    ),
                    ComponentGuideBlock.Bullets(
                        listOf(
                            "Move only when the reader asked to move; auto-scroll is never the default.",
                            "Follow only while they're following — scrolling away releases the view.",
                            "Treat every interaction as a signal: selection, keyboard, links " +
                                "and search all stop movement.",
                            "Start a new turn near the top of the viewport so it can be read from the beginning.",
                            "Stream the answer into the screen instead of pushing everything away.",
                            "Keep part of the previous conversation in context.",
                            "Let new content arrive offscreen without changing what the reader sees.",
                            "Show what's happening out of view (streaming, new messages).",
                            "Make it easy to return to the latest reply.",
                            "Let people jump anywhere in the conversation.",
                            "Reopen where the reader left off — often the last user message, not the absolute bottom.",
                            "Keep the reader's place when layout changes (images, markdown, history).",
                            "Handle interruptions without stealing position.",
                            "Stay responsive in long threads.",
                            "Be accessible without the noise.",
                        ),
                    ),
                    ComponentGuideBlock.Paragraph("**Never move the reader against their intent.**"),
                ),
        ),
        ComponentGuideSection(
            title = "MessageScroller",
            blocks =
                listOf(
                    ComponentGuideBlock.Paragraph(
                        (
                            "MessageScroller is a chat transcript scroller built for these " +
                                "behaviors. MessageScrollerProvider owns the scroll state and " +
                                "transcript-row behavior: opening position, streamed output, " +
                                "new-turn anchoring, prepended history, visibility and scroll " +
                                "controls. MessageScroller is the styled frame rendered inside it."
                        ),
                    ),
                    ComponentGuideBlock.Paragraph(
                        (
                            "MessageScroller is scoped to the scroll viewport. It does not own " +
                                "messages, AI state, transport, persistence, branching or model " +
                                "state; product code keeps composing messages, markers, tools, " +
                                "attachments and prompt inputs."
                        ),
                    ),
                ),
        ),
        ComponentGuideSection(
            title = "Core Concepts",
            blocks =
                listOf(
                    ComponentGuideBlock.Paragraph(
                        (
                            "A turn is the part of the conversation that starts a new exchange; " +
                                "an anchor is the row the viewport treats as the start of that " +
                                "turn. Mark it with `scrollAnchor`. When a new anchor is appended " +
                                "the viewport moves it near the top and keeps a peek of the " +
                                "previous item above it. Anchors are not tied to role — any row " +
                                "can be an anchor, including markers and event rows."
                        ),
                    ),
                ),
        ),
    )

internal val messageScrollerPageAfterSections =
    listOf(
        ComponentGuideSection(
            title = "Performance",
            blocks =
                listOf(
                    ComponentGuideBlock.Paragraph(
                        (
                            "The official web implementation keeps scroll work outside React " +
                                "state and uses content-visibility to skip offscreen painting. " +
                                "Compose caches measured row geometry and reconciles changes after " +
                                "a frame. Its visibility snapshot is derived on demand, but all " +
                                "transcript rows are eagerly composed and measured. Browser " +
                                "benchmark results do not establish a native transcript size limit."
                        ),
                    ),
                ),
        ),
        ComponentGuideSection(
            title = "Virtualization",
            blocks =
                listOf(
                    ComponentGuideBlock.Paragraph(
                        (
                            "The official web recipe gives TanStack Virtual ownership of rows " +
                                "inside the viewport. This Compose implementation uses a vertically " +
                                "scrolling Column and ScrollState; it has no lazy-layout adapter. " +
                                "Nesting LazyColumn in MessageScrollerContent does not implement " +
                                "that recipe: it receives unbounded vertical constraints and uses " +
                                "a different scroll state. For virtualized transcripts, use a " +
                                "separate lazy layout and implement its anchoring and commands."
                        ),
                    ),
                ),
        ),
        ComponentGuideSection(
            title = "Accessibility",
            blocks =
                listOf(
                    ComponentGuideBlock.Paragraph(
                        (
                            "The viewport is labelled and keyboard focusable. Content supplies " +
                                "a polite Compose live region; this does not implement the web " +
                                "role=log, aria-relevant=additions or aria-busy contract, so token " +
                                "announcement behavior requires platform screen-reader testing. " +
                                "Inactive scroll buttons cannot receive focus or clicks and are " +
                                "hidden from accessibility during their exit animation."
                        ),
                    ),
                ),
        ),
        ComponentGuideSection(
            title = "Unstyled",
            blocks =
                listOf(
                    ComponentGuideBlock.Paragraph(
                        (
                            "The official unstyled primitive is provided by @shadcn/react. " +
                                "Compose exposes provider state and style parameters instead of a " +
                                "separate unstyled package. Keep MessageScrollerProvider, the " +
                                "MessageScroller frame, Viewport, Content and Item in the " +
                                "composition and customize their styles and rows. The frame owns " +
                                "the controller lifecycle and is required for scroll commands."
                        ),
                    ),
                ),
        ),
    )
private val messageScrollerDemoCode =
    """
    MessageScrollerProvider(autoScroll = true) {
        Card(Modifier.width(320.dp).height(560.dp)) {
            CardHeader("New Chat", description = "How can we help you today?")
            MessageScroller(modifier = Modifier.weight(1f)) {
                MessageScrollerViewport {
                    MessageScrollerContent(Modifier.padding(16.dp)) {
                        messages.forEach { message -> MessageRow(message) }
                    }
                }
                MessageScrollerButton()
            }
            InputGroup {
                Input(
                    value = composerText,
                    onValueChange = { composerText = it },
                    placeholder = "Message MessageScroller…",
                )
                InputGroupAddon(InputGroupAddonAlign.InlineEnd) {
                    InputGroupButton(onClick = ::send, size = InputGroupButtonSize.IconSm) {
                        Icon(Lucide.ArrowUp, contentDescription = null)
                    }
                }
            }
        }
    }
    """.trimIndent()

private val messageScrollerAnchoringCode =
    """
    var anchorRole by remember { mutableStateOf("user") }
    var sent by remember { mutableIntStateOf(0) }
    MessageScrollerProvider {
        ScrollerCard("Anchoring Turns", "Choose which role settles near the top edge.") {
            MessageScroller {
                MessageScrollerViewport {
                    MessageScrollerContent {
                        anchoringScript.take(sent).forEach { message ->
                            MessageScrollerItem(
                                messageId = message.id,
                                // Role-independent: the selected role marks the turn.
                                scrollAnchor = message.role.name == anchorRole,
                            ) { MessageRow(message) }
                        }
                    }
                }
                MessageScrollerButton()
            }
        }
    }
    """.trimIndent()

private val messageScrollerGroupChatCode =
    """
    MessageScrollerProvider {
        MessageScroller {
            MessageScrollerViewport {
                MessageScrollerContent {
                    items.forEach { item ->
                        MessageScrollerItem(
                            messageId = item.id,
                            scrollAnchor = item.scrollAnchor,
                        ) {
                            when (item) {
                                is GroupChatMessage -> MessageRow(item)
                                is GroupChatMarker ->
                                    Marker(item.text, variant = MarkerVariant.Separator)
                            }
                        }
                    }
                }
            }
            MessageScrollerButton()
        }
    }
    """.trimIndent()

private val messageScrollerStreamingCode =
    """
    MessageScrollerProvider(autoScroll = true) {
        MessageScroller {
            MessageScrollerViewport {
                MessageScrollerContent {
                    messages.forEach { message -> MessageRow(message) }
                }
            }
            MessageScrollerButton()
        }
        // Stream one character at a time; autoScroll keeps the live edge in view
        // until the reader scrolls away (which releases following).
        LaunchedEffect(nextTurn) {
            val reply = nextTurn.reply
            for (count in 1..reply.length) {
                delay(6)
                messages = messages.updateLastReply(reply.take(count))
            }
        }
    }
    """.trimIndent()

private val messageScrollerAnimationCode =
    """
    var preset by remember { mutableStateOf("Fade") }
    MessageScrollerProvider {
        MessageScroller {
            MessageScrollerViewport {
                MessageScrollerContent {
                    messages.forEach { message ->
                        MessageScrollerItem(messageId = message.id) {
                            val progress = animateFloatAsState(if (message.user) 1f else 1f)
                            Message(
                                modifier = Modifier.graphicsLayer {
                                    // transform + opacity only; never animate row height.
                                    alpha = progress.value
                                    translationY = 10.dp.toPx() * (1f - progress.value)
                                },
                            ) { Bubble { BubbleContent { Text(message.text) } } }
                        }
                    }
                }
            }
            MessageScrollerButton()
        }
    }
    """.trimIndent()

@Composable
private fun ScrollerNoteTile() {
    Box(
        Modifier.widthIn(max = 384.dp).fillMaxWidth().height(128.dp)
            .background(currentTheme.colors.muted.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier.padding(24.dp),
            textStyle =
                TextStyle(
                    color = currentTheme.colors.mutedForeground,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                ),
        ) {
            "The viewport hides pending scroll until the saved position is applied — no flash of the oldest message."
        }
    }
}

/** Official page order: demo, then Core Concepts recipes, then extensions. */
internal val messageScrollerOfficialPageExamples =
    listOf(
        ComponentExample(
            title = "Default",
            code = messageScrollerDemoCode,
            minPreviewHeight = 700.dp,
            mobileMinPreviewHeight = 700.dp,
            previewPadding = 24.dp,
            preview = { MessageScrollerDemoRecipe() },
        ),
        ComponentExample(
            title = "Anchoring Turns",
            headingLevel = 3,
            code = messageScrollerAnchoringCode,
            minPreviewHeight = 700.dp,
            mobileMinPreviewHeight = 700.dp,
            previewPadding = 24.dp,
            preview = { MessageScrollerAnchoringRecipe() },
        ),
        ComponentExample(
            title = "Group Chat",
            headingLevel = 3,
            code = messageScrollerGroupChatCode,
            minPreviewHeight = 700.dp,
            mobileMinPreviewHeight = 700.dp,
            previewPadding = 24.dp,
            preview = { MessageScrollerGroupChatRecipe() },
        ),
        messageScrollerOfficialExamples.single { it.title == "Keeping Context Visible" },
        ComponentExample(
            title = "Following the Live Edge",
            headingLevel = 3,
            code = messageScrollerStreamingCode,
            minPreviewHeight = 700.dp,
            mobileMinPreviewHeight = 700.dp,
            previewPadding = 24.dp,
            preview = { MessageScrollerStreamingRecipe() },
        ),
        messageScrollerOfficialExamples.single { it.title == "Opening Saved Threads" },
        ComponentExample(
            title = "Avoiding a Flash on Reload",
            headingLevel = 3,
            descriptionMarkdown =
                (
                    "A scroll container always opens at the top. HTML has no way to set its scroll " +
                        "position before the first paint, so a transcript would show the oldest " +
                        "messages first and jump afterwards. When `defaultScrollPosition` is `end` or " +
                        "`last-anchor`, the styled viewport stays hidden until that position is " +
                        "applied, so you see the frame instead of the jump. `start` does not need this."
                ),
            code = "",
            hideCode = true,
            previewPadding = 24.dp,
            minPreviewHeight = 176.dp,
            preview = { ScrollerNoteTile() },
        ),
        messageScrollerOfficialExamples.single { it.title == "Loading Earlier Messages" },
        ComponentExample(
            title = "Animating New Messages",
            headingLevel = 3,
            code = messageScrollerAnimationCode,
            minPreviewHeight = 700.dp,
            mobileMinPreviewHeight = 700.dp,
            previewPadding = 24.dp,
            preview = { MessageScrollerAnimationRecipe() },
        ),
        messageScrollerOfficialExamples.single { it.title == "Jumping to Messages" },
        messageScrollerOfficialExamples.single { it.title == "Tracking the Reader's Position" },
        messageScrollerOfficialExamples.single { it.title == "Reading Scroll State" },
    )
