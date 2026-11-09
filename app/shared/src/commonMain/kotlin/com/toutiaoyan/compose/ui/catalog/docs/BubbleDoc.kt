@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.toutiaoyan.compose.ui.catalog.LocalCatalogToastState
import com.toutiaoyan.compose.ui.catalog.catalogMonoFontFamily
import com.toutiaoyan.compose.ui.components.bubble.*
import com.toutiaoyan.compose.ui.components.button.*
import com.toutiaoyan.compose.ui.components.collapsible.*
import com.toutiaoyan.compose.ui.components.emojitext.EmojiText
import com.toutiaoyan.compose.ui.components.message.MessageAlign
import com.toutiaoyan.compose.ui.components.popover.*
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.components.toast.*
import com.toutiaoyan.compose.ui.components.tooltip.*
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.Accent
import com.toutiaoyan.compose.ui.tokens.StylePreset

private data class BubbleLine(
    val text: String,
    val variant: BubbleVariant = BubbleVariant.Default,
    val align: MessageAlign = MessageAlign.Start,
    val emoji: List<String> = emptyList(),
    val reactionLabel: String = "",
    val side: BubbleReactionsSide = BubbleReactionsSide.Bottom,
    val reactionAlign: MessageAlign = MessageAlign.End,
)

private sealed interface BubbleNode {
    data class Line(val value: BubbleLine) : BubbleNode

    data class Group(val values: List<BubbleLine>) : BubbleNode
}

private fun line(
    text: String,
    variant: BubbleVariant = BubbleVariant.Default,
    align: MessageAlign = MessageAlign.Start,
    emoji: List<String> = emptyList(),
    label: String = "",
    side: BubbleReactionsSide = BubbleReactionsSide.Bottom,
    reactionAlign: MessageAlign = MessageAlign.End,
) = BubbleLine(text, variant, align, emoji, label, side, reactionAlign)

private fun node(value: BubbleLine) = BubbleNode.Line(value)

private val ghostText =
    """
    Ghost bubbles work for assistant text, markdown, and other content that should not be framed.

    This is perfect for assistant messages that should not have a frame and can take the full width of the container. You can also render code in it.

    Ghost bubbles are full width and can take the full width of the container.
    """.trimIndent()

private val bubbleNodes =
    mapOf(
        "Default" to
            listOf(
                node(line("Hey there! what's up?", align = MessageAlign.End)),
                BubbleNode.Group(
                    listOf(
                        line("Hey! Want to see chat bubbles?", BubbleVariant.Muted),
                        line(
                            "I can group messages, switch sides, and keep the whole thread easy to scan.",
                            BubbleVariant.Muted, emoji = listOf("👍"), label = "Reaction: thumbs up",
                        ),
                    ),
                ),
                node(line("Sure. Hit me with your best demo.", align = MessageAlign.End)),
                node(
                    line(
                        "Yes. You are reading a demo that is demoing itself. Very meta. Very on-brand.",
                        BubbleVariant.Muted, emoji = listOf("👍", "🔥", "👀", "+2"),
                        label = "Reactions: thumbs up, fire, eyes, and 2 more",
                    ),
                ),
            ),
        "Variants" to
            listOf(
                node(line("This is the default primary bubble.")),
                node(line("This is the secondary variant.", BubbleVariant.Secondary, MessageAlign.End)),
                node(
                    line(
                        "This one is muted. It uses a lower emphasis color for the chat bubble.",
                        BubbleVariant.Muted, emoji = listOf("👍"), label = "Reaction: thumbs up",
                    ),
                ),
                node(
                    line(
                        "This one is tinted. The tint is a softer color derived from the primary color.",
                        BubbleVariant.Tinted, MessageAlign.End,
                    ),
                ),
                node(line("We can also use an outlined variant.", BubbleVariant.Outline)),
                node(
                    line(
                        "Or a destructive variant with a reaction.", BubbleVariant.Destructive, MessageAlign.End,
                        listOf("🔥"), "Reaction: fire",
                    ),
                ),
                node(line(ghostText, BubbleVariant.Ghost)),
            ),
        "Alignment" to
            listOf(
                node(line("This bubble is aligned to the start. This is the default alignment.", BubbleVariant.Muted)),
                node(line("This bubble is aligned to the end. Use this for user messages.", align = MessageAlign.End)),
            ),
        "Bubble Group" to
            listOf(
                node(line("Can you tell me what's the issue?", BubbleVariant.Muted)),
                BubbleNode.Group(
                    listOf(
                        line("You tell me!", align = MessageAlign.End),
                        line("It worked yesterday. You broke it!", align = MessageAlign.End),
                        line(
                            "Find the bug and fix it.", align = MessageAlign.End, emoji = listOf("👀"),
                            label = "Reactions: eyes", reactionAlign = MessageAlign.Start,
                        ),
                    ),
                ),
                node(
                    line(
                        (
                            "Want me to diff yesterday's you against today's you? It's a " +
                                "bit embarrassing."
                        ),
                        BubbleVariant.Muted,
                    ),
                ),
            ),
        "Reactions" to
            listOf(
                node(
                    line(
                        "I don't need tests, I know my code works.", BubbleVariant.Muted, MessageAlign.End,
                        listOf("👍", "😮"), "Reactions: thumbs up, surprised", reactionAlign = MessageAlign.Start,
                    ),
                ),
                node(
                    line(
                        "Bold. Fine I'll add some tests. I'll let you know when they're done.", BubbleVariant.Muted,
                        emoji = listOf("👀", "🚀", "+2"), label = "Reactions: eyes, rocket, and 2 more",
                    ),
                ),
                node(
                    line(
                        "Tests passed on the first try. All 142 of them. Looking good!", align = MessageAlign.End,
                        emoji = listOf("🎉", "👏"), label = "Reactions: party popper, clapping hands",
                        side = BubbleReactionsSide.Top, reactionAlign = MessageAlign.Start,
                    ),
                ),
            ),
    )

private fun quoteBubble(value: String) =
    "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\""

private fun variantName(value: BubbleVariant) =
    when (value) {
        BubbleVariant.Default -> "Default"
        BubbleVariant.Secondary -> "Secondary"
        BubbleVariant.Muted -> "Muted"
        BubbleVariant.Tinted -> "Tinted"
        BubbleVariant.Outline -> "Outline"
        BubbleVariant.Ghost -> "Ghost"
        BubbleVariant.Destructive -> "Destructive"
    }

private val ghostContentCode =
    """
    BubbleContent {
        val theme = currentTheme
        val mono = catalogMonoFontFamily()
        val textStyle = TextStyle(fontFamily = theme.fontFamily, fontSize = 14.sp,
            lineHeight = 22.75.sp, color = theme.colors.foreground,
            lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None))
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(textStyle = textStyle) { buildAnnotatedString {
                append("Ghost bubbles work for assistant text, ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("markdown") }
                append(", and other content that should not be framed.")
            } }
            Text(textStyle = textStyle) { buildAnnotatedString {
                append("This is perfect for assistant messages that should not have a frame and can take the full width of the container. You can also render ")
                withStyle(SpanStyle(fontFamily = mono, background = theme.colors.muted)) { append("code") }
                append(" in it.")
            } }
            Text(textStyle = textStyle) { "Ghost bubbles are full width and can take the full width of the container." }
        }
    }
    """.trimIndent()

private fun lineCode(line: BubbleLine): String {
    val v = variantName(line.variant)
    val a = if (line.align == MessageAlign.End) "End" else "Start"
    val result = StringBuilder("Bubble(variant = BubbleVariant.$v, align = MessageAlign.$a) {\n")
    if (line.variant == BubbleVariant.Ghost) {
        result.append(ghostContentCode.prependIndent("    ") + "\n")
    } else {
        result.append("    BubbleContent(" + quoteBubble(line.text) + ")\n")
    }
    if (line.emoji.isNotEmpty()) {
        val side = if (line.side == BubbleReactionsSide.Top) "Top" else "Bottom"
        val align = if (line.reactionAlign == MessageAlign.Start) "Start" else "End"
        result.append("    BubbleReactions(\n        side = BubbleReactionsSide.$side, align = MessageAlign.$align,\n")
        result.append(
            "        modifier = Modifier.clearAndSetSemantics { role = Role.Image; contentDescription = " +
                quoteBubble(line.reactionLabel) + " },\n    ) {\n",
        )
        for (emoji in line.emoji) result.append(
            if (emoji.startsWith("+")) {
                "        Text { ${quoteBubble(emoji)} }\n"
            } else {
                "        EmojiText(${quoteBubble(emoji)}, typography = TextVariant.BodyMedium)\n"
            },
        )
        result.append("    }\n")
    }
    return result.append("}").toString()
}

private fun nodesCode(nodes: List<BubbleNode>) =
    nodes.joinToString("\n") { node ->
        when (node) {
            is BubbleNode.Line -> lineCode(node.value)
            is BubbleNode.Group ->
                "BubbleGroup(Modifier.fillMaxWidth()) {\n" +
                    node.values.joinToString("\n") { lineCode(it).prependIndent("    ") } + "\n}"
        }
    }

private fun frameCode(
    body: String,
    gap: Int = 32,
) = "Column(Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 48.dp),\n" +
    "    verticalArrangement = Arrangement.spacedBy($gap.dp)) {\n" + body.prependIndent("    ") + "\n}"

internal val bubbleLongText =
    """
    The accessibility review found two focus states that were visually too subtle in dark mode.

    I checked the dialog, menu, and drawer paths because each one renders focusable controls inside a layered surface.

    The dialog and drawer are fine. The menu needs the hover and focus tokens split so keyboard focus stays visible when the pointer is not involved.

    I also recommend keeping the change in the style file instead of the primitive so the other themes can choose their own focus treatment later.
    """.trimIndent()

private val linksCode =
    """
    val toasts = rememberToastState()
    Bubble(variant = BubbleVariant.Muted) { BubbleContent("How can I help you today?") }
    BubbleGroup(Modifier.fillMaxWidth()) {
        Bubble(variant = BubbleVariant.Tinted, align = MessageAlign.End) {
            BubbleContent("I forgot my password", onClick = { toasts.show("You clicked forgot password") })
        }
        Bubble(variant = BubbleVariant.Tinted, align = MessageAlign.End) {
            BubbleContent("I need help with my subscription", onClick = { toasts.show("You clicked help with subscription") })
        }
        Bubble(variant = BubbleVariant.Tinted, align = MessageAlign.End) {
            BubbleContent("Something else. Talk to a human.", onClick = { toasts.show("You clicked something else. Talk to a human.") })
        }
    }
    // Mount Toaster(toasts) once at the window root.
    """.trimIndent()
private val quotedBubbleLongText = quoteBubble(bubbleLongText)
private val collapsibleCode =
    """
    var open by remember { mutableStateOf(false) }
    val text = $quotedBubbleLongText
    Bubble(variant = BubbleVariant.Muted) { BubbleContent("How can I help you today?") }
    Bubble(variant = BubbleVariant.Muted, align = MessageAlign.End) {
        BubbleContent {
            Collapsible(open = open, onOpenChange = { open = it }) {
                Text { if (open) text else text.take(180) + "..." }
                CollapsibleTrigger {
                    val muted = currentTheme.colors.mutedForeground
                    Button(onClick = toggle, variant = ButtonVariant.Link,
                        style = Style { contentPadding(0.dp); contentColor(muted) }) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(textStyle = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, color = muted)) {
                                if (open) "Show less" else "Show more"
                            }
                            Image(Lucide.ChevronDown, null, Modifier.size(16.dp).rotate(if (open) 180f else 0f),
                                colorFilter = ColorFilter.tint(muted))
                        }
                    }
                }
            }
        }
    }
    """.trimIndent()
private val tooltipCode =
    """
    Bubble(variant = BubbleVariant.Secondary) { BubbleContent("Did you remove the stale route?") }
    Bubble(align = MessageAlign.End) {
        BubbleContent("Yes, removed it from the registry.")
        BubbleReactions(style = Style { contentPadding(0.dp) }) {
            Tooltip {
                TooltipTrigger {
                    Button(onClick = {}, variant = ButtonVariant.Ghost, size = ButtonSize.IconXs, label = "Read status") {
                        Image(Lucide.Check, null, Modifier.size(16.dp), colorFilter = ColorFilter.tint(currentTheme.colors.foreground))
                    }
                }
                TooltipContent("Read on Jan 5, 2026 at 4:32 PM")
            }
        }
    }
    """.trimIndent()
private val popoverCode =
    """
    Bubble(align = MessageAlign.End) { BubbleContent("Run the build script.") }
    Bubble(variant = BubbleVariant.Destructive) {
        BubbleContent("Failed to run the command.")
        BubbleReactions(style = Style { contentPadding(0.dp) }) {
            Popover {
                PopoverTrigger {
                    Button(onClick = toggle, variant = ButtonVariant.Ghost, size = ButtonSize.IconXs, label = "Show error details") {
                        Image(Lucide.Info, null, Modifier.size(16.dp), colorFilter = ColorFilter.tint(currentTheme.colors.foreground))
                    }
                }
                PopoverContent {
                    PopoverHeader {
                        Text(textStyle = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.Medium)) {
                            "Command failed with exit code 1"
                        }
                        Text(textStyle = TextStyle(fontSize = 14.sp, lineHeight = 20.sp,
                            color = currentTheme.colors.mutedForeground)) {
                            "ENOENT: no such file or directory, open pnpm-lock.yaml"
                        }
                    }
                }
            }
        }
    }
    """.trimIndent()

private val bubbleExampleTitles =
    listOf(
        "Default", "Variants", "Alignment", "Bubble Group", "Links and Buttons",
        "Reactions", "Show More / Collapsible", "Tooltip", "Popover",
    )
private val bubbleDescriptions =
    listOf<String?>(
        null, "Seven visual treatments, including full-row ghost content.",
        "Align a bubble to the start or end of the conversation. Message alignment is inherited.",
        "Group consecutive bubbles from the same sender. Set alignment on each bubble.",
        "Clickable BubbleContent supports keyboard activation and a visible focus ring.",
        "Reactions overlap the edge. Leave vertical space between rows; use Style to remove padding around buttons.",
        "Compose long content with Collapsible to show more or less text.",
        (
            "Use Tooltip to reveal read metadata on hover or keyboard " +
                "focus."
        ),
        "Open a Popover for detailed error information.",
    )

val bubbleDoc =
    ComponentDoc(
        id = "bubble", title = "Bubble",
        description = (
            "Displays conversational content in a message bubble. Supports " +
                "variants, alignment, grouping, reactions, and collapsible " +
                "content."
        ),
        introMarkdown =
            "The `Bubble` component displays framed conversational content. Use it for chat text, " +
                "short structured output, quoted replies, suggestions, and reactions.\n\n" +
                "For full-featured chat interfaces, use the `Message` component. `Bubble` is " +
                "intentionally scoped to the bubble surface. Place avatars, names, timestamps, " +
                "metadata, and message-level actions in `Message`.",
        usageImportsCode = "import com.toutiaoyan.compose.ui.components.bubble.*",
        usageCode =
            """
            BubbleGroup {
                Bubble {
                    BubbleContent("I checked the registry output and removed the stale route.")
                    BubbleReactions(modifier = Modifier.clearAndSetSemantics {
                        role = Role.Image; contentDescription = "Reaction: thumbs up"
                    }) { EmojiText("👍", typography = TextVariant.BodyMedium) }
                }
            }
            """.trimIndent(),
        compositionCode = (
            "Bubble\n├── BubbleContent\n└── " +
                "BubbleReactions\n\nBubbleGroup\n├── Bubble\n│   └── " +
                "BubbleContent\n└── Bubble\n    └── BubbleContent"
        ),
        examplesHeading = null,
        beforeExamples =
            listOf(
                ComponentGuideSection(
                    "Features",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            (
                                "Bubble owns the message surface. Keep avatars, timestamps, " +
                                    "and message-level actions in Message."
                            ),
                        ),
                        ComponentGuideBlock.Bullets(
                            listOf(
                                "Seven variants and start/end alignment",
                                "Content-sized surfaces up to 80%; ghost can use the full row",
                                "Overlapping reactions with configurable side and alignment",
                                "Interactive content with hover, keyboard activation, and focus indication",
                            ),
                        ),
                    ),
                ),
            ),
        examples =
            bubbleExampleTitles.mapIndexed { index, title ->
                val body =
                    when (title) {
                        "Links and Buttons" -> linksCode
                        "Show More / Collapsible" -> collapsibleCode
                        "Tooltip" -> tooltipCode
                        "Popover" -> popoverCode
                        else ->
                            (
                                if (title == "Reactions") {
                                    "val toasts = rememberToastState()\n"
                                } else {
                                    ""
                                }
                            ) +
                                nodesCode(bubbleNodes.getValue(title)) +
                                if (title == "Reactions") {
                                    "\n" +
                                        """
                                        Bubble(variant = BubbleVariant.Destructive) {
                                            BubbleContent("Are you sure I can run this command?")
                                            BubbleReactions(style = Style { contentPadding(0.dp) }) {
                                                Button("Yes, run it", onClick = { toasts.success("You clicked yes, running command...") },
                                                    variant = ButtonVariant.Ghost, size = ButtonSize.Xs)
                                            }
                                        }
                                        """.trimIndent()
                                } else {
                                    ""
                                }
                    }
                val gap =
                    if (title in listOf("Variants", "Reactions")) {
                        48
                    } else if (title in listOf("Tooltip", "Popover")) {
                        16
                    } else {
                        32
                    }
                val code =
                    if (title in listOf("Links and Buttons", "Reactions")) {
                        val content =
                            body.removePrefix("val toasts = rememberToastState()\n")
                                .replace("// Mount Toaster(toasts) once at the window root.", "")
                        (
                            "val toasts = " +
                                "rememberToastState()\nBox(Modifier.fillMaxSize(), " +
                                "contentAlignment = Alignment.Center) {\n"
                        ) +
                            frameCode(content, gap).prependIndent("    ") +
                            "\n    Toaster(toasts, Modifier.align(Alignment.BottomEnd).padding(24.dp))\n}"
                    } else {
                        frameCode(body, gap)
                    }
                ComponentExample(
                    title,
                    code,
                    minPreviewHeight = 0.dp,
                    previewPadding = 40.dp,
                    descriptionMarkdown = bubbleDescriptions[index],
                    preview = { BubbleExample(title) },
                )
            },
        afterExamples =
            listOf(
                ComponentGuideSection(
                    "Accessibility",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            (
                                "Bubble is the presentational message surface. Keep " +
                                    "conversation-level semantics on its container."
                            ),
                        ),
                    ),
                ),
                ComponentGuideSection(
                    "Labeling Reactions",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            (
                                "Describe a presentational reaction row as one image and hide " +
                                    "individual glyphs. Keep interactive reactions as real Buttons " +
                                    "with meaningful accessible names."
                            ),
                        ),
                        ComponentGuideBlock.Code(
                            """
                            BubbleReactions(modifier = Modifier.clearAndSetSemantics {
                                role = Role.Image
                                contentDescription = "Reactions: thumbs up, fire, and 8 more"
                            }) {
                                EmojiText("👍", typography = TextVariant.BodyMedium)
                                EmojiText("🔥", typography = TextVariant.BodyMedium)
                                Text { "+8" }
                            }
                            """.trimIndent(),
                        ),
                    ),
                    level = 3,
                ),
                ComponentGuideSection(
                    "Interactive Bubbles",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            (
                                "Use onClick on BubbleContent for a keyboard-focusable surface " +
                                    "with a visible focus ring. The accessible name comes from its " +
                                    "text."
                            ),
                        ),
                        ComponentGuideBlock.Code(
                            """
                            Bubble(variant = BubbleVariant.Muted, align = MessageAlign.End) {
                                BubbleContent("I forgot my password", onClick = onReply)
                            }
                            """.trimIndent(),
                        ),
                    ),
                    level = 3,
                ),
                ComponentGuideSection(
                    "Meaning Beyond Color",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            (
                                "Explain destructive states in text instead of relying on " +
                                    "color. Use text, alignment, or icons to convey meaning."
                            ),
                        ),
                    ),
                    level = 3,
                ),
            ),
        apiSections =
            listOf(
                ComponentApiSection(
                    "Bubble",
                    (
                        "Content-sized surface. Root variant is inherited by " +
                            "BubbleContent. Ghost has no 80% cap; parent Message end " +
                            "alignment is inherited."
                    ),
                    listOf(
                        ApiProperty("variant", "BubbleVariant", "Default"),
                        ApiProperty("align", "MessageAlign", "Start"),
                        ApiProperty("content", "@Composable BoxScope.() -> Unit", "Required"),
                    ),
                ),
                ComponentApiSection(
                    "BubbleContent",
                    (
                        "Surface or real interactive button. Explicit variant " +
                            "overrides remain supported; otherwise inherit the enclosing " +
                            "Bubble."
                    ),
                    listOf(
                        ApiProperty("variant", "BubbleVariant?", "null (inherit)"),
                        ApiProperty("onClick", "(() -> Unit)?", "null"),
                        ApiProperty("enabled", "Boolean", "true"),
                    ),
                ),
                ComponentApiSection(
                    "BubbleReactions",
                    (
                        "Absolute reaction row, inset 12dp from the chosen inline edge " +
                            "and translated by 75% of its own height. Does not change " +
                            "bubble size."
                    ),
                    listOf(
                        ApiProperty("side", "BubbleReactionsSide", "Bottom"),
                        ApiProperty("align", "MessageAlign", "End"),
                        ApiProperty("style", "Style", "Style"),
                    ),
                ),
                ComponentApiSection(
                    "BubbleGroup",
                    (
                        "Consecutive bubbles separated by 8dp. Give the group a width " +
                            "with modifier when it belongs to a full transcript row."
                    ),
                    listOf(
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("content", "@Composable ColumnScope.() -> Unit", "Required"),
                    ),
                ),
            ),
    )

@Composable
private fun ColumnScope.RenderBubbleLine(line: BubbleLine) {
    Bubble(variant = line.variant, align = line.align) {
        if (line.variant == BubbleVariant.Ghost) {
            BubbleContent { GhostBubbleText() }
        } else {
            BubbleContent(line.text)
        }
        if (line.emoji.isNotEmpty()) {
            BubbleReactions(
                side = line.side,
                align = line.reactionAlign,
                modifier =
                    Modifier.clearAndSetSemantics {
                        role = Role.Image
                        contentDescription = line.reactionLabel
                    },
            ) {
                line.emoji.forEach {
                    if (it.startsWith(
                            "+",
                        )
                    ) {
                        Text { it }
                    } else {
                        EmojiText(it, typography = TextVariant.BodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun GhostBubbleText() {
    val theme = currentTheme
    val mono = catalogMonoFontFamily()
    val style =
        TextStyle(
            fontFamily = theme.fontFamily,
            fontSize = 14.sp,
            lineHeight = 22.75.sp,
            color = theme.colors.foreground,
            lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
        )
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(textStyle = style) {
            buildAnnotatedString {
                append("Ghost bubbles work for assistant text, ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("markdown") }
                append(", and other content that should not be framed.")
            }
        }
        Text(textStyle = style) {
            buildAnnotatedString {
                append(
                    (
                        "This is perfect for assistant messages that should not have a " +
                            "frame and can take the full width of the container. You can " +
                            "also render "
                    ),
                )
                withStyle(SpanStyle(fontFamily = mono, background = theme.colors.muted)) { append("code") }
                append(" in it.")
            }
        }
        Text(textStyle = style) { "Ghost bubbles are full width and can take the full width of the container." }
    }
}

@Composable
internal fun BubbleExample(title: String) {
    val inherited = currentTheme
    val globalToasts = LocalCatalogToastState.current
    val localToasts = rememberToastState()
    val toasts = globalToasts ?: localToasts
    Theme(
        preset = StylePreset.Nova,
        baseColor = inherited.activeBaseColor,
        accent = Accent.Blue,
        isDark = inherited.isDark,
        baseRadius = inherited.baseRadius,
        fontFamily = inherited.fontFamily,
        minTouchTarget = null,
    ) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(
                Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 48.dp),
                verticalArrangement =
                    Arrangement.spacedBy(
                        if (title in listOf("Variants", "Reactions")) {
                            48.dp
                        } else if (title in listOf("Tooltip", "Popover")) {
                            16.dp
                        } else {
                            32.dp
                        },
                    ),
            ) {
                bubbleNodes[title]?.forEach { node ->
                    when (node) {
                        is BubbleNode.Line -> RenderBubbleLine(node.value)
                        is BubbleNode.Group ->
                            BubbleGroup(
                                Modifier.fillMaxWidth(),
                            ) { node.values.forEach { RenderBubbleLine(it) } }
                    }
                }
                when (title) {
                    "Links and Buttons" -> BubbleLinkButtons(toasts)
                    "Reactions" ->
                        Bubble(variant = BubbleVariant.Destructive) {
                            BubbleContent("Are you sure I can run this command?")
                            BubbleReactions(style = Style { contentPadding(0.dp) }) {
                                Button(
                                    "Yes, run it",
                                    onClick = { toasts.success("You clicked yes, running command...") },
                                    variant = ButtonVariant.Ghost,
                                    size = ButtonSize.Xs,
                                )
                            }
                        }
                    "Show More / Collapsible" -> BubbleCollapsible()
                    "Tooltip" -> BubbleTooltip()
                    "Popover" -> BubblePopover()
                }
            }
            if (globalToasts == null) Toaster(localToasts, Modifier.align(Alignment.BottomEnd))
        }
    }
}

@Composable
private fun ColumnScope.BubbleLinkButtons(toasts: ToastState) {
    Bubble(variant = BubbleVariant.Muted) { BubbleContent("How can I help you today?") }
    BubbleGroup(Modifier.fillMaxWidth()) {
        listOf(
            "I forgot my password" to "You clicked forgot password",
            "I need help with my subscription" to "You clicked help with subscription",
            "Something else. Talk to a human." to "You clicked something else. Talk to a human.",
        ).forEach {
                (text, notification) ->
            Bubble(variant = BubbleVariant.Tinted, align = MessageAlign.End) {
                BubbleContent(text, onClick = { toasts.show(notification) })
            }
        }
    }
}

@Composable
private fun ColumnScope.BubbleCollapsible() {
    var open by remember { mutableStateOf(false) }
    val muted = currentTheme.colors.mutedForeground
    Bubble(variant = BubbleVariant.Muted) { BubbleContent("How can I help you today?") }
    Bubble(variant = BubbleVariant.Muted, align = MessageAlign.End) {
        BubbleContent {
            Collapsible(open = open, onOpenChange = { open = it }) {
                Text { if (open) bubbleLongText else bubbleLongText.take(180) + "..." }
                CollapsibleTrigger {
                    Button(
                        onClick = toggle,
                        variant = ButtonVariant.Link,
                        style =
                            Style {
                                contentPadding(0.dp)
                                contentColor(muted)
                            },
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(textStyle = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, color = muted)) {
                                if (open) "Show less" else "Show more"
                            }
                            Image(
                                Lucide.ChevronDown,
                                null,
                                Modifier.size(16.dp).rotate(if (open)180f else 0f),
                                colorFilter = ColorFilter.tint(muted),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.BubbleTooltip() {
    Bubble(variant = BubbleVariant.Secondary) { BubbleContent("Did you remove the stale route?") }
    Bubble(align = MessageAlign.End) {
        BubbleContent("Yes, removed it from the registry.")
        BubbleReactions(style = Style { contentPadding(0.dp) }) {
            Tooltip {
                TooltipTrigger {
                    Button(
                        onClick = {},
                        variant = ButtonVariant.Ghost,
                        size = ButtonSize.IconXs,
                        label = "Read status",
                    ) {
                        BubbleIcon(Lucide.Check)
                    }
                }
                TooltipContent("Read on Jan 5, 2026 at 4:32 PM")
            }
        }
    }
}

@Composable
private fun ColumnScope.BubblePopover() {
    val colors = currentTheme.colors
    Bubble(align = MessageAlign.End) { BubbleContent("Run the build script.") }
    Bubble(variant = BubbleVariant.Destructive) {
        BubbleContent("Failed to run the command.")
        BubbleReactions(style = Style { contentPadding(0.dp) }) {
            Popover {
                PopoverTrigger {
                    Button(
                        onClick = toggle,
                        variant = ButtonVariant.Ghost,
                        size = ButtonSize.IconXs,
                        label = "Show error details",
                    ) {
                        BubbleIcon(Lucide.Info)
                    }
                }
                PopoverContent {
                    PopoverHeader {
                        Text(
                            textStyle = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.Medium),
                        ) {
                            "Command failed with exit code 1"
                        }
                        Text(
                            textStyle = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, color = colors.mutedForeground),
                        ) {
                            "ENOENT: no such file or directory, open pnpm-lock.yaml"
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BubbleIcon(image: androidx.compose.ui.graphics.vector.ImageVector) {
    Image(image, null, Modifier.size(16.dp), colorFilter = ColorFilter.tint(currentTheme.colors.foreground))
}
