@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.composables.icons.lucide.*
import compose_foundation.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import com.toutiaoyan.compose.ui.components.avatar.*
import com.toutiaoyan.compose.ui.components.attachment.*
import com.toutiaoyan.compose.ui.components.bubble.*
import com.toutiaoyan.compose.ui.components.button.*
import com.toutiaoyan.compose.ui.components.emojitext.EmojiText
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.marker.*
import com.toutiaoyan.compose.ui.components.message.*
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.styles.rememberShimmerStyle
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.Accent
import com.toutiaoyan.compose.ui.tokens.StylePreset

/** The official page names these six; everything after is a Compose-only extension. */
private val messageOfficialTitles =
    setOf("Default", "Avatar", "Group", "Header and Footer", "Actions", "Attachment")

private fun messagePageExample(example: ComponentExample): ComponentExample =
    if (example.title in messageOfficialTitles) example
    else example.copy(title = "Kotlin extension: " + example.title)

private val messageFeatureSections =
    listOf(
        ComponentGuideSection(
            title = "Features",
            blocks =
                listOf(
                    ComponentGuideBlock.Bullets(
                        listOf(
                            "Start and end alignment for sender and receiver rows via `align`.",
                            "Avatar slot that anchors to the bottom of the message and stays clear of the footer.",
                            "Header and footer slots for sender names, status, and message actions.",
                            "Footer follows the message side; actions stay aligned on `MessageAlign.End` rows.",
                            "`MessageGroup` stacks consecutive messages from the same sender.",
                        ),
                    ),
                ),
        ),
    )

private val messageAccessibilitySections =
    listOf(
        ComponentGuideSection(
            title = "Accessibility",
            blocks =
                listOf(
                    ComponentGuideBlock.Paragraph(
                        (
                            "`Message` is a presentational layout wrapper. Accessibility comes " +
                                "from the content you place inside it."
                        ),
                    ),
                ),
        ),
        ComponentGuideSection(
            title = "Label icon-only actions",
            level = 3,
            blocks =
                listOf(
                    ComponentGuideBlock.Paragraph(
                        (
                            "Action buttons in `MessageFooter` are usually icon-only, so give " +
                                "each one a content description (`aria-label` on the web)."
                        ),
                    ),
                    ComponentGuideBlock.Code(
                        """
                        MessageFooter {
                            Button(
                                onClick = { },
                                variant = ButtonVariant.Ghost,
                                size = ButtonSize.IconSm,
                                label = "Copy",
                            ) { Icon(Lucide.Copy) }
                        }
                        """.trimIndent(),
                    ),
                ),
        ),
        ComponentGuideSection(
            title = "Status updates",
            level = 3,
            blocks =
                listOf(
                    ComponentGuideBlock.Paragraph(
                        (
                            "For in-progress messages, use a `Marker` with `role = \"status\"` " +
                                "so assistive tech announces the update as it appears."
                        ),
                    ),
                    ComponentGuideBlock.Code(
                        """
                        Message {
                            Marker(role = "status") {
                                MarkerIcon { Spinner() }
                                MarkerContent { "Checking the logs..." }
                            }
                        }
                        """.trimIndent(),
                    ),
                ),
        ),
    )

val messageDoc =
    ComponentDoc(
        id = "message",
        title = "Message",
        description = "Displays a message in a conversation, with optional avatar, header, footer, and alignment.",
        examplesHeading = null,
        usageImportsCode =
            """
            import androidx.compose.foundation.Image
            import androidx.compose.foundation.layout.*
            import androidx.compose.runtime.*
            import androidx.compose.ui.Alignment
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.layout.ContentScale
            import androidx.compose.ui.semantics.*
            import androidx.compose.ui.text.*
            import androidx.compose.ui.text.font.FontWeight
            import androidx.compose.ui.unit.*
            import com.composables.icons.lucide.*
            import compose_foundation.app.shared.generated.resources.*
            import org.jetbrains.compose.resources.painterResource
            import com.toutiaoyan.compose.ui.components.avatar.*
            import com.toutiaoyan.compose.ui.components.attachment.*
            import com.toutiaoyan.compose.ui.components.bubble.*
            import com.toutiaoyan.compose.ui.components.button.*
            import com.toutiaoyan.compose.ui.components.emojitext.EmojiText
            import com.toutiaoyan.compose.ui.components.icon.Icon
            import com.toutiaoyan.compose.ui.components.marker.*
            import com.toutiaoyan.compose.ui.components.message.*
            import com.toutiaoyan.compose.ui.components.text.Text
            import com.toutiaoyan.compose.ui.components.text.TextVariant
            import com.toutiaoyan.compose.ui.components.text.TextColors
            import com.toutiaoyan.compose.ui.styles.rememberShimmerStyle
            import com.toutiaoyan.compose.ui.theme.currentTheme
            import com.toutiaoyan.compose.ui.theme.Theme
            import com.toutiaoyan.compose.ui.tokens.Accent
            import com.toutiaoyan.compose.ui.tokens.StylePreset
            """.trimIndent(),
        usageCode =
            """
            Message(avatar = {
                MessageAvatar {
                    Avatar {
                        AvatarImage(painter = painterResource(Res.drawable.message_avatar_02),
                            contentDescription = "@avatar")
                        AvatarFallback("CN")
                    }
                }
            }) {
                Bubble("How can I help you today?")
            }
            """.trimIndent(),
        compositionCode =
            """
            Message
            ├── avatar: MessageAvatar
            └── content: MessageContent (owned by Message)
                ├── MessageHeader
                ├── Bubble
                └── MessageFooter
            MessageGroup
            ├── Message
            └── Message
            """.trimIndent(),
        apiSections =
            listOf(
                ComponentApiSection(
                    "Message",
                    (
                        "Owns the row and inner content column. An omitted avatar " +
                            "emits no slot or gap."
                    ),
                    listOf(
                        ApiProperty("align", "MessageAlign", "Start"),
                        ApiProperty("avatar", "(@Composable () -> Unit)?", "null"),
                        ApiProperty("content", "@Composable ColumnScope.() -> Unit", "Required"),
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                    ),
                ),
                ComponentApiSection(
                    "MessageGroup",
                    "Stacks consecutive messages with an 8dp Nova gap.",
                    listOf(
                        ApiProperty("content", "@Composable ColumnScope.() -> Unit", "Required"),
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                    ),
                ),
                ComponentApiSection(
                    "MessageAvatar",
                    (
                        "Intrinsic avatar slot, min-width32dp. Empty content reserves " +
                            "only the column width."
                    ),
                    listOf(
                        ApiProperty("text / content", "String / @Composable () -> Unit", "Empty content"),
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                    ),
                ),
                ComponentApiSection(
                    "MessageContent",
                    (
                        "Standalone column with modifier/style; the existing RowScope " +
                            "bridge remains available."
                    ),
                    listOf(
                        ApiProperty("content", "@Composable ColumnScope.() -> Unit", "Required"),
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                    ),
                ),
                ComponentApiSection(
                    "MessageHeader",
                    (
                        "Metadata above a bubble; Nova follows the documentation " +
                            "start-alignment rule."
                    ),
                    listOf(
                        ApiProperty("text / content", "String / @Composable RowScope.() -> Unit", "Required"),
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                    ),
                ),
                ComponentApiSection(
                    "MessageFooter",
                    (
                        "Metadata or actions aligned to the message side. Presence " +
                            "shifts avatar up32dp in Nova."
                    ),
                    listOf(
                        ApiProperty("text / content", "String / @Composable RowScope.() -> Unit", "Required"),
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                    ),
                ),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        val inherited = currentTheme
                        Theme(
                            preset = StylePreset.Nova, baseColor = inherited.activeBaseColor,
                            accent = Accent.Blue, isDark = inherited.isDark,
                            baseRadius = inherited.baseRadius, fontFamily = inherited.fontFamily, minTouchTarget = null,
                        ) {
                            Column(
                                Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 48.dp),
                                verticalArrangement = Arrangement.spacedBy(24.dp),
                            ) {
                                Message(align = MessageAlign.End, avatar = {
                                    MessageAvatar {
                                        Avatar {
                                            AvatarImage(painter = painterResource(Res.drawable.message_avatar_10),
                                                contentDescription = "@me")
                                            AvatarFallback("ME")
                                        }
                                    }
                                }) {
                                    Bubble("Deploying to prod real quick.")
                                }
                                Message(avatar = {
                                    MessageAvatar {
                                        Avatar {
                                            AvatarImage(painter = painterResource(Res.drawable.message_avatar_02),
                                                contentDescription = "@rabbit")
                                            AvatarFallback("R")
                                        }
                                    }
                                }) {
                                    Bubble("It's 4:55 PM. On a Friday.", variant = BubbleVariant.Muted)
                                }
                                Message(align = MessageAlign.End, avatar = {
                                    MessageAvatar {
                                        Avatar {
                                            AvatarImage(painter = painterResource(Res.drawable.message_avatar_10),
                                                contentDescription = "@me")
                                            AvatarFallback("ME")
                                        }
                                    }
                                }) {
                                    Bubble("It's a one-line change.")
                                    MessageFooter("Delivered")
                                }
                                Message(avatar = {
                                    MessageAvatar {
                                        Avatar {
                                            AvatarImage(painter = painterResource(Res.drawable.message_avatar_02),
                                                contentDescription = "@rabbit")
                                            AvatarFallback("R")
                                        }
                                    }
                                }) {
                                    BubbleGroup {
                                        Bubble("It's always a one-line change 😭.", variant = BubbleVariant.Muted)
                                        Bubble(variant = BubbleVariant.Muted) {
                                            BubbleContent("Alright, let me take a look.")
                                            BubbleReactions(
                                                modifier = Modifier.semantics { contentDescription = "Reactions: thumbs up" },
                                            ) {
                                                EmojiText("👍")
                                            }
                                        }
                                    }
                                }
                                Marker(modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }) {
                                    MarkerContent {
                                        Text(style = rememberShimmerStyle()) {
                                            buildAnnotatedString {
                                                withStyle(SpanStyle(fontWeight = FontWeight.Medium)) {
                                                    append("Oliver")
                                                }
                                                append(" is typing...")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        val inherited = currentTheme
                        Theme(
                            preset = StylePreset.Nova,
                            baseColor = inherited.activeBaseColor,
                            accent = Accent.Blue,
                            isDark = inherited.isDark,
                            baseRadius = inherited.baseRadius,
                            fontFamily = inherited.fontFamily,
                            minTouchTarget = null,
                        ) {
                            Column(
                                Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 48.dp),
                                verticalArrangement = Arrangement.spacedBy(24.dp),
                            ) {
                                Message(align = MessageAlign.End, avatar = {
                                    MessageAvatar {
                                        Avatar {
                                            AvatarImage(
                                                painter =
                                                    painterResource(Res.drawable.message_avatar_10),
                                                contentDescription =
                                                    "@me",
                                            )
                                            AvatarFallback("ME")
                                        }
                                    }
                                }) {
                                    Bubble("Deploying to prod real quick.")
                                }
                                Message(avatar = {
                                    MessageAvatar {
                                        Avatar {
                                            AvatarImage(
                                                painter =
                                                    painterResource(Res.drawable.message_avatar_02),
                                                contentDescription =
                                                    "@rabbit",
                                            )
                                            AvatarFallback("R")
                                        }
                                    }
                                }) {
                                    Bubble("It's 4:55 PM. On a Friday.", variant = BubbleVariant.Muted)
                                }
                                Message(align = MessageAlign.End, avatar = {
                                    MessageAvatar {
                                        Avatar {
                                            AvatarImage(
                                                painter =
                                                    painterResource(Res.drawable.message_avatar_10),
                                                contentDescription =
                                                    "@me",
                                            )
                                            AvatarFallback("ME")
                                        }
                                    }
                                }) {
                                    Bubble("It's a one-line change.")
                                    MessageFooter("Delivered")
                                }
                                Message(avatar = {
                                    MessageAvatar {
                                        Avatar {
                                            AvatarImage(
                                                painter =
                                                    painterResource(Res.drawable.message_avatar_02),
                                                contentDescription =
                                                    "@rabbit",
                                            )
                                            AvatarFallback("R")
                                        }
                                    }
                                }) {
                                    BubbleGroup {
                                        Bubble("It's always a one-line change 😭.", variant = BubbleVariant.Muted)
                                        Bubble(variant = BubbleVariant.Muted) {
                                            BubbleContent("Alright, let me take a look.")
                                            BubbleReactions(
                                                modifier =
                                                    Modifier.semantics {
                                                        contentDescription =
                                                            "Reactions: thumbs up"
                                                    },
                                            ) {
                                                EmojiText("👍")
                                            }
                                        }
                                    }
                                }
                                Marker(modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }) {
                                    MarkerContent {
                                        Text(style = rememberShimmerStyle()) {
                                            buildAnnotatedString {
                                                withStyle(
                                                    SpanStyle(fontWeight = FontWeight.Medium),
                                                ) { append("Oliver") }
                                                append(" is typing...")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Avatar",
                    code =
                        """
                        val inherited = currentTheme
                        Theme(
                            preset = StylePreset.Nova, baseColor = inherited.activeBaseColor,
                            accent = Accent.Blue, isDark = inherited.isDark,
                            baseRadius = inherited.baseRadius, fontFamily = inherited.fontFamily, minTouchTarget = null,
                        ) {
                            Column(
                                Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 48.dp),
                                verticalArrangement = Arrangement.spacedBy(24.dp),
                            ) {
                                Message(avatar = {
                                    MessageAvatar {
                                        Avatar {
                                            AvatarImage(painter = painterResource(Res.drawable.message_avatar_03),
                                                contentDescription = "@avatar")
                                            AvatarFallback("R")
                                        }
                                    }
                                }) {
                                    Bubble(
                                    "The build failed during dependency installation.",
                                    variant = BubbleVariant.Muted,
                                )
                                }
                                Message(align = MessageAlign.End, avatar = {
                                    MessageAvatar {
                                        Avatar {
                                            AvatarImage(painter = painterResource(Res.drawable.message_avatar_10),
                                                contentDescription = "@avatar")
                                            AvatarFallback("R")
                                        }
                                    }
                                }) {
                                    Bubble("Can you share the exact error?")
                                }
                                Message(avatar = {
                                    MessageAvatar {
                                        Avatar {
                                            AvatarImage(painter = painterResource(Res.drawable.message_avatar_03),
                                                contentDescription = "@avatar")
                                            AvatarFallback("R")
                                        }
                                    }
                                }) {
                                    BubbleGroup {
                                        Bubble("Here's the error from the logs", variant = BubbleVariant.Muted)
                                        Bubble((
                                            "Something went wrong with the build. The libraries are not " +
                                            "installed correctly. Try running the build again."
                                        ), variant = BubbleVariant.Muted)
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        val inherited = currentTheme
                        Theme(
                            preset = StylePreset.Nova,
                            baseColor = inherited.activeBaseColor,
                            accent = Accent.Blue,
                            isDark = inherited.isDark,
                            baseRadius = inherited.baseRadius,
                            fontFamily = inherited.fontFamily,
                            minTouchTarget = null,
                        ) {
                            Column(
                                Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 48.dp),
                                verticalArrangement = Arrangement.spacedBy(24.dp),
                            ) {
                                Message(avatar = {
                                    MessageAvatar {
                                        Avatar {
                                            AvatarImage(
                                                painter =
                                                    painterResource(Res.drawable.message_avatar_03),
                                                contentDescription =
                                                    "@avatar",
                                            )
                                            AvatarFallback("R")
                                        }
                                    }
                                }) {
                                    Bubble(
                                        "The build failed during dependency installation.",
                                        variant = BubbleVariant.Muted,
                                    )
                                }
                                Message(align = MessageAlign.End, avatar = {
                                    MessageAvatar {
                                        Avatar {
                                            AvatarImage(
                                                painter =
                                                    painterResource(Res.drawable.message_avatar_10),
                                                contentDescription =
                                                    "@avatar",
                                            )
                                            AvatarFallback("R")
                                        }
                                    }
                                }) {
                                    Bubble("Can you share the exact error?")
                                }
                                Message(avatar = {
                                    MessageAvatar {
                                        Avatar {
                                            AvatarImage(
                                                painter =
                                                    painterResource(Res.drawable.message_avatar_03),
                                                contentDescription =
                                                    "@avatar",
                                            )
                                            AvatarFallback("R")
                                        }
                                    }
                                }) {
                                    BubbleGroup {
                                        Bubble("Here's the error from the logs", variant = BubbleVariant.Muted)
                                        Bubble(
                                            (
                                                "Something went wrong with the build. The libraries are not " +
                                                    "installed correctly. Try running the build again."
                                            ),
                                            variant = BubbleVariant.Muted,
                                        )
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Group",
                    code =
                        """
                        val inherited = currentTheme
                        Theme(
                            preset = StylePreset.Nova, baseColor = inherited.activeBaseColor,
                            accent = Accent.Blue, isDark = inherited.isDark,
                            baseRadius = inherited.baseRadius, fontFamily = inherited.fontFamily, minTouchTarget = null,
                        ) {
                            Column(
                                Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 48.dp),
                                verticalArrangement = Arrangement.spacedBy(24.dp),
                            ) {
                                MessageGroup {
                                    Message(avatar = {
                                        MessageAvatar()
                                    }) {
                                        Bubble("I checked the registry addresses.", variant = BubbleVariant.Muted)
                                    }
                                    Message(avatar = {
                                        MessageAvatar {
                                            Avatar {
                                                AvatarImage(painter = painterResource(Res.drawable.message_avatar_02),
                                                    contentDescription = "@avatar")
                                                AvatarFallback("CN")
                                            }
                                        }
                                    }) {
                                        Bubble("The component and example JSON now live under the UI registry.",
                                            variant = BubbleVariant.Muted)
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        val inherited = currentTheme
                        Theme(
                            preset = StylePreset.Nova,
                            baseColor = inherited.activeBaseColor,
                            accent = Accent.Blue,
                            isDark = inherited.isDark,
                            baseRadius = inherited.baseRadius,
                            fontFamily = inherited.fontFamily,
                            minTouchTarget = null,
                        ) {
                            Column(
                                Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 48.dp),
                                verticalArrangement = Arrangement.spacedBy(24.dp),
                            ) {
                                MessageGroup {
                                    Message(avatar = {
                                        MessageAvatar()
                                    }) {
                                        Bubble("I checked the registry addresses.", variant = BubbleVariant.Muted)
                                    }
                                    Message(avatar = {
                                        MessageAvatar {
                                            Avatar {
                                                AvatarImage(
                                                    painter =
                                                        painterResource(Res.drawable.message_avatar_02),
                                                    contentDescription =
                                                        "@avatar",
                                                )
                                                AvatarFallback("CN")
                                            }
                                        }
                                    }) {
                                        Bubble(
                                            "The component and example JSON now live under the UI registry.",
                                            variant =
                                                BubbleVariant.Muted,
                                        )
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Header and Footer",
                    code =
                        """
                        val inherited = currentTheme
                        Theme(
                            preset = StylePreset.Nova, baseColor = inherited.activeBaseColor,
                            accent = Accent.Blue, isDark = inherited.isDark,
                            baseRadius = inherited.baseRadius, fontFamily = inherited.fontFamily, minTouchTarget = null,
                        ) {
                            Column(
                                Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 48.dp),
                                verticalArrangement = Arrangement.spacedBy(32.dp),
                            ) {
                                Message() {
                                    MessageHeader("Olivia")
                                    Bubble("I already checked the logs.", variant = BubbleVariant.Muted)
                                }
                                Message(align = MessageAlign.End) {
                                    Bubble("Send the report to the team. Ping @shadcn if you need help.")
                                    MessageFooter {
                                        Text {
                                            buildAnnotatedString {
                                                append("Read ")
                                                withStyle(SpanStyle(fontWeight = FontWeight.Normal)) {
                                                    append("Yesterday")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        val inherited = currentTheme
                        Theme(
                            preset = StylePreset.Nova,
                            baseColor = inherited.activeBaseColor,
                            accent = Accent.Blue,
                            isDark = inherited.isDark,
                            baseRadius = inherited.baseRadius,
                            fontFamily = inherited.fontFamily,
                            minTouchTarget = null,
                        ) {
                            Column(
                                Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 48.dp),
                                verticalArrangement = Arrangement.spacedBy(32.dp),
                            ) {
                                Message {
                                    MessageHeader("Olivia")
                                    Bubble("I already checked the logs.", variant = BubbleVariant.Muted)
                                }
                                Message(align = MessageAlign.End) {
                                    Bubble("Send the report to the team. Ping @shadcn if you need help.")
                                    MessageFooter {
                                        Text {
                                            buildAnnotatedString {
                                                append("Read ")
                                                withStyle(
                                                    SpanStyle(fontWeight = FontWeight.Normal),
                                                ) { append("Yesterday") }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Actions",
                    code =
                        """
                        val inherited = currentTheme
                        Theme(
                            preset = StylePreset.Nova, baseColor = inherited.activeBaseColor,
                            accent = Accent.Blue, isDark = inherited.isDark,
                            baseRadius = inherited.baseRadius, fontFamily = inherited.fontFamily, minTouchTarget = null,
                        ) {
                            Column(
                                Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 48.dp),
                                verticalArrangement = Arrangement.spacedBy(32.dp),
                            ) {
                                Message() {
                                    Bubble("The install failure is coming from the workspace package.",
                                        variant = BubbleVariant.Muted)
                                    MessageFooter {
                                        Button(onClick = {},
                                            variant = ButtonVariant.Ghost,
                                            size = ButtonSize.Icon,
                                            label = "Copy") { Icon(Lucide.Copy,
                                            contentDescription = null) }
                                        Button(onClick = {},
                                            variant = ButtonVariant.Ghost,
                                            size = ButtonSize.Icon,
                                            label = "Like") { Icon(Lucide.ThumbsUp,
                                            contentDescription = null) }
                                        Button(onClick = {},
                                            variant = ButtonVariant.Ghost,
                                            size = ButtonSize.Icon,
                                            label = "Dislike") { Icon(Lucide.ThumbsDown,
                                            contentDescription = null) }
                                    }
                                }
                                Message(align = MessageAlign.End) {
                                    Bubble("Okay drop me a link. Taking a look...")
                                    MessageFooter {
                                        Row(verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Text(textStyle = TextStyle(color = currentTheme.colors.destructive,
                                                fontWeight = FontWeight.Normal)) { "Failed to send" }
                                            Button(onClick = {},
                                                variant = ButtonVariant.Ghost,
                                                size = ButtonSize.IconXs,
                                                label = "Retry") { Icon(Lucide.RefreshCcw,
                                                contentDescription = null) }
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        val inherited = currentTheme
                        Theme(
                            preset = StylePreset.Nova,
                            baseColor = inherited.activeBaseColor,
                            accent = Accent.Blue,
                            isDark = inherited.isDark,
                            baseRadius = inherited.baseRadius,
                            fontFamily = inherited.fontFamily,
                            minTouchTarget = null,
                        ) {
                            Column(
                                Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 48.dp),
                                verticalArrangement = Arrangement.spacedBy(32.dp),
                            ) {
                                Message {
                                    Bubble(
                                        "The install failure is coming from the workspace package.",
                                        variant =
                                            BubbleVariant.Muted,
                                    )
                                    MessageFooter {
                                        Button(
                                            onClick =
                                                {},
                                            variant =
                                                ButtonVariant.Ghost,
                                            size =
                                                ButtonSize.Icon,
                                            label =
                                                "Copy",
                                        ) {
                                            Icon(
                                                Lucide.Copy,
                                                contentDescription =
                                                null,
                                            )
                                        }
                                        Button(
                                            onClick =
                                                {},
                                            variant =
                                                ButtonVariant.Ghost,
                                            size =
                                                ButtonSize.Icon,
                                            label =
                                                "Like",
                                        ) {
                                            Icon(
                                                Lucide.ThumbsUp,
                                                contentDescription =
                                                null,
                                            )
                                        }
                                        Button(
                                            onClick =
                                                {},
                                            variant =
                                                ButtonVariant.Ghost,
                                            size =
                                                ButtonSize.Icon,
                                            label =
                                                "Dislike",
                                        ) {
                                            Icon(
                                                Lucide.ThumbsDown,
                                                contentDescription =
                                                null,
                                            )
                                        }
                                    }
                                }
                                Message(align = MessageAlign.End) {
                                    Bubble("Okay drop me a link. Taking a look...")
                                    MessageFooter {
                                        Row(
                                            verticalAlignment =
                                                Alignment.CenterVertically,
                                            horizontalArrangement =
                                                Arrangement.spacedBy(8.dp),
                                        ) {
                                            Text(
                                                textStyle =
                                                    TextStyle(
                                                        color =
                                                            currentTheme.colors.destructive,
                                                        fontWeight =
                                                            FontWeight.Normal,
                                                    ),
                                            ) { "Failed to send" }
                                            Button(
                                                onClick =
                                                    {},
                                                variant =
                                                    ButtonVariant.Ghost,
                                                size =
                                                    ButtonSize.IconXs,
                                                label =
                                                    "Retry",
                                            ) {
                                                Icon(
                                                    Lucide.RefreshCcw,
                                                    contentDescription =
                                                    null,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Attachment",
                    code =
                        """
                        val inherited = currentTheme
                        Theme(
                            preset = StylePreset.Nova, baseColor = inherited.activeBaseColor,
                            accent = Accent.Blue, isDark = inherited.isDark,
                            baseRadius = inherited.baseRadius, fontFamily = inherited.fontFamily, minTouchTarget = null,
                        ) {
                            Column(
                                Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 48.dp),
                                verticalArrangement = Arrangement.spacedBy(32.dp),
                            ) {
                                Message(align = MessageAlign.End) {
                                    Attachment(orientation = AttachmentOrientation.Vertical) {
                                        AttachmentMedia(variant = AttachmentMediaVariant.Image) {
                                            Image(painterResource(Res.drawable.attachment_workspace),
                                                contentDescription = "Workspace",

                                                modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                        }
                                    }
                                    Bubble("Here's the image. Can you add it to the PDF? Use it for the cover page.")
                                }
                                Message() {
                                    Bubble("Done. Here's the PDF with the image added as the cover page.",
                                        variant = BubbleVariant.Muted)
                                    Attachment {
                                        AttachmentMedia { Icon(Lucide.FileText, contentDescription = null) }
                                        AttachmentContent {
                                            AttachmentTitle("sales-dashboard.pdf")
                                            AttachmentDescription("PDF · 2.4 MB")
                                        }
                                        AttachmentActions {
                                            AttachmentAction(onClick = {},
                                                variant = ButtonVariant.Secondary,
                                                size = ButtonSize.IconSm,
                                                label = "Download") {
                                                Icon(Lucide.Download, contentDescription = null)
                                            }
                                        }
                                    }
                                }
                                Message(align = MessageAlign.End) {
                                    Bubble("Thanks. Looks good.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        val inherited = currentTheme
                        Theme(
                            preset = StylePreset.Nova,
                            baseColor = inherited.activeBaseColor,
                            accent = Accent.Blue,
                            isDark = inherited.isDark,
                            baseRadius = inherited.baseRadius,
                            fontFamily = inherited.fontFamily,
                            minTouchTarget = null,
                        ) {
                            Column(
                                Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 48.dp),
                                verticalArrangement = Arrangement.spacedBy(32.dp),
                            ) {
                                Message(align = MessageAlign.End) {
                                    Attachment(orientation = AttachmentOrientation.Vertical) {
                                        AttachmentMedia(variant = AttachmentMediaVariant.Image) {
                                            Image(
                                                painterResource(Res.drawable.attachment_workspace),
                                                contentDescription =
                                                    "Workspace",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop,
                                            )
                                        }
                                    }
                                    Bubble("Here's the image. Can you add it to the PDF? Use it for the cover page.")
                                }
                                Message {
                                    Bubble(
                                        "Done. Here's the PDF with the image added as the cover page.",
                                        variant =
                                            BubbleVariant.Muted,
                                    )
                                    Attachment {
                                        AttachmentMedia { Icon(Lucide.FileText, contentDescription = null) }
                                        AttachmentContent {
                                            AttachmentTitle("sales-dashboard.pdf")
                                            AttachmentDescription("PDF · 2.4 MB")
                                        }
                                        AttachmentActions {
                                            AttachmentAction(
                                                onClick =
                                                    {},
                                                variant =
                                                    ButtonVariant.Secondary,
                                                size =
                                                    ButtonSize.IconSm,
                                                label =
                                                    "Download",
                                            ) {
                                                Icon(Lucide.Download, contentDescription = null)
                                            }
                                        }
                                    }
                                }
                                Message(align = MessageAlign.End) {
                                    Bubble("Thanks. Looks good.")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Start aligned",
                    code =
                        """
                        Message(
                            avatar = { MessageAvatar("AI") },
                        ) {
                            MessageHeader("Assistant")
                            Text { "Hello! How can I help you today?" }
                        }
                        """.trimIndent(),
                    preview = {
                        Message(
                            avatar = { MessageAvatar("AI") },
                        ) {
                            MessageHeader("Assistant")
                            Text { "Hello! How can I help you today?" }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default align",
                    code =
                        """
                        Message(
                            align = MessageAlign.Start,
                            avatar = { MessageAvatar("AI") },
                        ) {
                            MessageHeader("Assistant")
                            Text { "Hello! How can I help you today?" }
                        }
                        """.trimIndent(),
                    preview = {
                        Message(
                            align = MessageAlign.Start,
                            avatar = { MessageAvatar("AI") },
                        ) {
                            MessageHeader("Assistant")
                            Text { "Hello! How can I help you today?" }
                        }
                    },
                ),
                ComponentExample(
                    title = "End aligned",
                    code =
                        """
                        Message(
                            align = MessageAlign.End,
                            avatar = { MessageAvatar("Me") },
                            text = "Can you summarize this document?",
                        )
                        """.trimIndent(),
                    preview = {
                        Message(
                            align = MessageAlign.End,
                            avatar = { MessageAvatar("Me") },
                            text = "Can you summarize this document?",
                        )
                    },
                ),
                ComponentExample(
                    title = "Conversation with bubbles",
                    code =
                        """
                        MessageGroup {
                            Message(
                                align = MessageAlign.End,
                                avatar = { MessageAvatar { EmojiText("🙂") } },
                            ) {
                                    Bubble("Deploying to prod real quick.", align = MessageAlign.End)
                            }
                            Message(avatar = { MessageAvatar { EmojiText("👨") } }) {
                                Bubble {
                                        BubbleContent("It's 4:55 PM. On a Friday.", variant = BubbleVariant.Muted)
                                }
                            }
                            Message(
                                align = MessageAlign.End,
                                avatar = { MessageAvatar { EmojiText("🙂") } },
                            ) {
                                    Bubble("It's a one-line change.", align = MessageAlign.End)
                                MessageFooter("Delivered")
                            }
                            Message(avatar = { MessageAvatar { EmojiText("👨") } }) {
                                Bubble {
                                    BubbleContent(variant = BubbleVariant.Muted) {
                                        EmojiText("It's always a one-line change 😭.")
                                    }
                                }
                            }
                            Message(avatar = { MessageAvatar { EmojiText("👨") } }) {
                                Bubble {
                                    BubbleContent("Alright, let me take a look.", variant = BubbleVariant.Muted)
                                    BubbleReactions { EmojiText("👍", typography = TextTypography.LabelSmall) }
                                }
                            }
                        }
                        Text(typography = TextTypography.LabelSmall,
                            textStyle = TextStyles.Muted) { "Oliver is typing…" }
                        """.trimIndent(),
                    preview = {
                        Column(
                            modifier = Modifier.width(280.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            MessageGroup {
                                Message(
                                    align = MessageAlign.End,
                                    avatar = { MessageAvatar { EmojiText("🙂") } },
                                ) {
                                    Bubble("Deploying to prod real quick.", align = MessageAlign.End)
                                }
                                Message(avatar = { MessageAvatar { EmojiText("👨") } }) {
                                    Bubble("It's 4:55 PM. On a Friday.", variant = BubbleVariant.Muted)
                                }
                                Message(
                                    align = MessageAlign.End,
                                    avatar = { MessageAvatar { EmojiText("🙂") } },
                                ) {
                                    Bubble("It's a one-line change.", align = MessageAlign.End)
                                    MessageFooter("Delivered")
                                }
                                Message(avatar = { MessageAvatar { EmojiText("👨") } }) {
                                    Bubble {
                                        BubbleContent(variant = BubbleVariant.Muted) {
                                            EmojiText("It's always a one-line change 😭.")
                                        }
                                    }
                                }
                                Message(avatar = { MessageAvatar { EmojiText("👨") } }) {
                                    Bubble {
                                        BubbleContent("Alright, let me take a look.", variant = BubbleVariant.Muted)
                                        BubbleReactions {
                                            EmojiText("👍", typography = TextVariant.LabelSmall)
                                        }
                                    }
                                }
                            }
                            Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) { "Oliver is typing…" }
                        }
                    },
                ),
                ComponentExample(
                    title = "Header",
                    code =
                        """
                        Message(avatar = { MessageAvatar("AI") }) {
                            MessageHeader("Assistant")
                            Text { "Hello! How can I help you today?" }
                        }
                        """.trimIndent(),
                    preview = {
                        Message(avatar = { MessageAvatar("AI") }) {
                            MessageHeader("Assistant")
                            Text { "Hello! How can I help you today?" }
                        }
                    },
                ),
                ComponentExample(
                    title = "Footer",
                    code =
                        """
                        Message(avatar = { MessageAvatar("AI") }) {
                            Text { "Hello! How can I help you today?" }
                            MessageFooter("Delivered")
                        }
                        """.trimIndent(),
                    preview = {
                        Message(avatar = { MessageAvatar("AI") }) {
                            Text { "Hello! How can I help you today?" }
                            MessageFooter("Delivered")
                        }
                    },
                ),
                ComponentExample(
                    title = "Content",
                    code =
                        """
                        Message(avatar = { MessageAvatar("AI") }) {
                            Text { "Hello! How can I help you today?" }
                        }
                        """.trimIndent(),
                    preview = {
                        Message(avatar = { MessageAvatar("AI") }) {
                            Text { "Hello! How can I help you today?" }
                        }
                    },
                ),
            ).map(::messagePageExample),
        beforeExamples = messageFeatureSections,
        afterExamples = messageAccessibilitySections,
    )
