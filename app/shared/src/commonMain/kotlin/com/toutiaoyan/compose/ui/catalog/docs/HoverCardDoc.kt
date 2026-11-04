@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.components.button.*
import com.toutiaoyan.compose.ui.components.hovercard.*
import com.toutiaoyan.compose.ui.components.overlay.*
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.currentTheme

val hoverCardDoc =
    ComponentDoc(
        id = "hover-card",
        title = "Hover Card",
        description = "For sighted users to preview content available behind a link.",
        examplesHeading = null,
        usageImportsCode =
            """
            import androidx.compose.foundation.layout.*
            import androidx.compose.foundation.style.Style
            import androidx.compose.runtime.*
            import androidx.compose.ui.Alignment
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.platform.LocalLayoutDirection
            import androidx.compose.ui.text.TextStyle
            import androidx.compose.ui.text.font.FontWeight
            import androidx.compose.ui.unit.*
            import com.toutiaoyan.compose.ui.components.button.*
            import com.toutiaoyan.compose.ui.components.hovercard.*
            import com.toutiaoyan.compose.ui.components.overlay.*
            import com.toutiaoyan.compose.ui.components.text.Text
            import com.toutiaoyan.compose.ui.theme.currentTheme
            """.trimIndent(),
        usageCode =
            """
            HoverCard {
                HoverCardTrigger { Button("Hover", onClick = {}, variant = ButtonVariant.Link) }
                HoverCardContent { Text { "The React Framework – created and maintained by @vercel." } }
            }
            """.trimIndent(),
        compositionCode =
            """
            HoverCard
            ├── HoverCardTrigger
            └── HoverCardContent
            """.trimIndent(),
        beforeExamples =
            listOf(
                ComponentGuideSection(
                    "Trigger Delays",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            (
                                "Set delay and closeDelay on HoverCardTrigger. Base UI " +
                                    "defaults are 600/300ms; the demo explicitly uses 10/100ms."
                            ),
                        ),
                        ComponentGuideBlock.Code(
                            (
                                "HoverCardTrigger(delay = 100, closeDelay = 200) { " +
                                    "Button(\"Hover\", onClick = {}) }"
                            ),
                        ),
                    ),
                ),
                ComponentGuideSection(
                    "Positioning",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            (
                                "HoverCardContent supports four physical sides and " +
                                    "InlineStart/InlineEnd, resolved against LocalLayoutDirection."
                            ),
                        ),
                        ComponentGuideBlock.Code(
                            (
                                "HoverCardContent(side = HoverCardSide.Top, align = " +
                                    "PopupAlign.Start) { Text { \"Content\" } }"
                            ),
                        ),
                    ),
                ),
            ),
        apiSections =
            listOf(
                ComponentApiSection(
                    "HoverCard",
                    (
                        "Controlled or uncontrolled root. Provider/root timing aliases " +
                            "remain available for Kotlin callers."
                    ),
                    listOf(
                        ApiProperty("open / defaultOpen", "Boolean? / Boolean", "null / false"),
                        ApiProperty("onOpenChange", "((Boolean) -> Unit)?", "null"),
                        ApiProperty("disabled", "Boolean", "false"),
                        ApiProperty("openDelay / closeDelay", "Int?", "null (provider defaults)"),
                        ApiProperty("disableHoverableContent", "Boolean", "false"),
                    ),
                ),
                ComponentApiSection(
                    "HoverCardTrigger",
                    (
                        "Use a focusable control in the content slot to support " +
                            "keyboard focus. Trigger timing takes precedence over " +
                            "root/provider aliases."
                    ),
                    listOf(
                        ApiProperty("delay / closeDelay", "Int?", "null (600 / 300ms defaults)"),
                        ApiProperty("disabled", "Boolean", "false"),
                        ApiProperty("modifier", "Modifier", "Modifier"),
                    ),
                ),
                ComponentApiSection(
                    "HoverCardContent",
                    (
                        "Nova panel uses 256dp width, 10dp padding, an outer ring and " +
                            "100ms enter/exit transitions."
                    ),
                    listOf(
                        ApiProperty("side", "HoverCardSide / PopupPlacement?", "Bottom"),
                        ApiProperty("align", "PopupAlign", "Center"),
                        ApiProperty("sideOffset / alignOffset", "Dp", "4.dp / 4.dp"),
                        ApiProperty("dismissOnClickOutside", "Boolean", "true"),
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                        ApiProperty("enter / exit", "EnterTransition / ExitTransition", "Preset defaults"),
                    ),
                ),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    minPreviewHeight = 320.dp,
                    mobileMinPreviewHeight = 320.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        HoverCard {
                            HoverCardTrigger(delay = 10, closeDelay = 100) {
                                Button("Hover Here", onClick = {}, variant = ButtonVariant.Link)
                            }
                            HoverCardContent(style = Style { width(256.dp) }) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(textStyle = TextStyle(fontWeight = FontWeight.SemiBold)) { "@nextjs" }
                                    Text { "The React Framework – created and maintained by @vercel." }
                                    Text(modifier = Modifier.padding(top = 4.dp),
                                        textStyle = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, color = currentTheme.colors.mutedForeground)) {
                                        "Joined December 2021"
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCard {
                            HoverCardTrigger(delay = 10, closeDelay = 100) {
                                Button("Hover Here", onClick = {}, variant = ButtonVariant.Link)
                            }
                            HoverCardContent(style = Style { width(256.dp) }) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(textStyle = TextStyle(fontWeight = FontWeight.SemiBold)) { "@nextjs" }
                                    Text { "The React Framework – created and maintained by @vercel." }
                                    Text(
                                        modifier = Modifier.padding(top = 4.dp),
                                        textStyle =
                                            TextStyle(
                                                fontSize =
                                                    12.sp,
                                                lineHeight =
                                                    16.sp,
                                                color =
                                                    currentTheme.colors.mutedForeground,
                                            ),
                                    ) {
                                        "Joined December 2021"
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Basic",
                    minPreviewHeight = 320.dp,
                    mobileMinPreviewHeight = 320.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        HoverCard {
                            HoverCardTrigger(delay = 10, closeDelay = 100) {
                                Button("Hover Here", onClick = {}, variant = ButtonVariant.Link)
                            }
                            HoverCardContent(style = Style { width(256.dp) }) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(textStyle = TextStyle(fontWeight = FontWeight.SemiBold)) { "@nextjs" }
                                    Text { "The React Framework – created and maintained by @vercel." }
                                    Text(modifier = Modifier.padding(top = 4.dp),
                                        textStyle = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, color = currentTheme.colors.mutedForeground)) {
                                        "Joined December 2021"
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCard {
                            HoverCardTrigger(delay = 10, closeDelay = 100) {
                                Button("Hover Here", onClick = {}, variant = ButtonVariant.Link)
                            }
                            HoverCardContent(style = Style { width(256.dp) }) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(textStyle = TextStyle(fontWeight = FontWeight.SemiBold)) { "@nextjs" }
                                    Text { "The React Framework – created and maintained by @vercel." }
                                    Text(
                                        modifier = Modifier.padding(top = 4.dp),
                                        textStyle =
                                            TextStyle(
                                                fontSize =
                                                    12.sp,
                                                lineHeight =
                                                    16.sp,
                                                color =
                                                    currentTheme.colors.mutedForeground,
                                            ),
                                    ) {
                                        "Joined December 2021"
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Sides",
                    descriptionMarkdown = "Use physical sides to position the card.",
                    minPreviewHeight = 352.dp,
                    mobileMinPreviewHeight = 352.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                            verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(HoverCardSide.Left, HoverCardSide.Top, HoverCardSide.Bottom, HoverCardSide.Right).forEach { side ->
                                HoverCard {
                                    HoverCardTrigger(delay = 100, closeDelay = 100) {
                                        Button(side.name, onClick = {}, variant = ButtonVariant.Outline)
                                    }
                                    HoverCardContent(side = side) {
                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(textStyle = TextStyle(fontWeight = FontWeight.Medium)) { "Hover Card" }
                                            Text { "This hover card appears on the " + side.name.lowercase() + " side of the trigger." }
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            listOf(
                                HoverCardSide.Left,
                                HoverCardSide.Top,
                                HoverCardSide.Bottom,
                                HoverCardSide.Right,
                            ).forEach { side ->
                                HoverCard {
                                    HoverCardTrigger(delay = 100, closeDelay = 100) {
                                        Button(side.name, onClick = {}, variant = ButtonVariant.Outline)
                                    }
                                    HoverCardContent(side = side) {
                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(textStyle = TextStyle(fontWeight = FontWeight.Medium)) { "Hover Card" }
                                            Text {
                                                "This hover card appears on the " +
                                                    side.name.lowercase() +
                                                    " side of the trigger."
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "RTL",
                    descriptionMarkdown = "Use physical or logical inline sides for the selected language.",
                    minPreviewHeight = 384.dp,
                    mobileMinPreviewHeight = 384.dp,
                    previewPadding = 0.dp,
                    code =
                        """
                        // 0 English, 1 Arabic, 2 Hebrew. Connect this state to your language picker.
                        var language by remember { mutableStateOf(1) }
                        val t = hoverCardTranslations[language]
                        val direction = if (language == 0) LayoutDirection.Ltr else LayoutDirection.Rtl
                        CompositionLocalProvider(LocalLayoutDirection provides direction) {
                            Column(Modifier.fillMaxWidth().heightIn(min = 320.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)) {
                                listOf(
                                    listOf(HoverCardSide.Left to "left", HoverCardSide.Top to "top", HoverCardSide.Bottom to "bottom", HoverCardSide.Right to "right"),
                                    listOf(HoverCardSide.InlineStart to "inline-start", HoverCardSide.InlineEnd to "inline-end"),
                                ).forEach { sides ->
                                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        sides.forEach { (side, label) ->
                                            HoverCard {
                                                HoverCardTrigger(delay = 10, closeDelay = 100) {
                                                    Button(t.getValue(label), onClick = {}, variant = ButtonVariant.Outline)
                                                }
                                                HoverCardContent(side = side) {
                                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                        Text(textStyle = TextStyle(fontWeight = FontWeight.SemiBold)) { t.getValue("name") }
                                                        Text(textStyle = TextStyle(color = currentTheme.colors.mutedForeground)) { t.getValue("price") }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        private val hoverCardTranslations = listOf(
                            mapOf(
                                "trigger" to "Wireless Headphones",
                                "name" to "Wireless Headphones",
                                "price" to "$99.99",
                                "inline-start" to "Inline Start",
                                "left" to "Left",
                                "top" to "Top",
                                "bottom" to "Bottom",
                                "right" to "Right",
                                "inline-end" to "Inline End"
                            ),
                            mapOf(
                                "trigger" to "سماعات لاسلكية",
                                "name" to "سماعات لاسلكية",
                                "price" to "٩٩.٩٩ $",
                                "inline-start" to "بداية السطر",
                                "left" to "يسار",
                                "top" to "أعلى",
                                "bottom" to "أسفل",
                                "right" to "يمين",
                                "inline-end" to "نهاية السطر"
                            ),
                            mapOf(
                                "trigger" to "אוזניות אלחוטיות",
                                "name" to "אוזניות אלחוטיות",
                                "price" to "99.99 $",
                                "inline-start" to "תחילת השורה",
                                "left" to "שמאל",
                                "top" to "למעלה",
                                "bottom" to "למטה",
                                "right" to "ימין",
                                "inline-end" to "סוף השורה"
                            )
                        )
                        """.trimIndent(),
                    preview = {
                        var language by remember { mutableStateOf(1) }
                        RtlPreview(language, listOf("English", "العربية", "עברית"), { language = it }) {
                            val t = hoverCardTranslations[language]
                            val direction = if (language == 0) LayoutDirection.Ltr else LayoutDirection.Rtl
                            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                                Column(
                                    Modifier.fillMaxWidth().heightIn(min = 320.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                                ) {
                                    listOf(
                                        listOf(
                                            HoverCardSide.Left to "left",
                                            HoverCardSide.Top to "top",
                                            HoverCardSide.Bottom to "bottom",
                                            HoverCardSide.Right to "right",
                                        ),
                                        listOf(
                                            HoverCardSide.InlineStart to "inline-start",
                                            HoverCardSide.InlineEnd to "inline-end",
                                        ),
                                    ).forEach { sides ->
                                        FlowRow(
                                            horizontalArrangement =
                                                Arrangement.spacedBy(
                                                    8.dp,
                                                    Alignment.CenterHorizontally,
                                                ),
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                        ) {
                                            sides.forEach { (side, label) ->
                                                HoverCard {
                                                    HoverCardTrigger(delay = 10, closeDelay = 100) {
                                                        Button(
                                                            t.getValue(label),
                                                            onClick = {},
                                                            variant = ButtonVariant.Outline,
                                                        )
                                                    }
                                                    HoverCardContent(side = side) {
                                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                            Text(
                                                                textStyle =
                                                                    TextStyle(
                                                                        fontWeight =
                                                                            FontWeight.SemiBold,
                                                                    ),
                                                            ) { t.getValue("name") }
                                                            Text(
                                                                textStyle =
                                                                    TextStyle(
                                                                        color =
                                                                            currentTheme.colors.mutedForeground,
                                                                    ),
                                                            ) { t.getValue("price") }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Controlled",
                    descriptionMarkdown = "The parent can observe and control open state.",
                    minPreviewHeight = 320.dp,
                    mobileMinPreviewHeight = 320.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text { if (open) "Open" else "Closed" }
                            HoverCard(open = open, onOpenChange = { open = it }) {
                                HoverCardTrigger(delay = 100, closeDelay = 200) {
                                    Button("Controlled preview", onClick = {}, variant = ButtonVariant.Outline)
                                }
                                HoverCardContent { Text { "The parent owns the open state." } }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text { if (open) "Open" else "Closed" }
                            HoverCard(open = open, onOpenChange = { open = it }) {
                                HoverCardTrigger(delay = 100, closeDelay = 200) {
                                    Button("Controlled preview", onClick = {}, variant = ButtonVariant.Outline)
                                }
                                HoverCardContent { Text { "The parent owns the open state." } }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Provider timing",
                    descriptionMarkdown = "Share timing defaults between cards.",
                    minPreviewHeight = 320.dp,
                    mobileMinPreviewHeight = 320.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        HoverCardProvider(openDelay = 250, closeDelay = 300) {
                            HoverCard {
                                HoverCardTrigger { Button("Provider timing", onClick = {}, variant = ButtonVariant.Outline) }
                                HoverCardContent { Text { "Provider defaults can be overridden by each trigger." } }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCardProvider(openDelay = 250, closeDelay = 300) {
                            HoverCard {
                                HoverCardTrigger {
                                    Button(
                                        "Provider timing",
                                        onClick =
                                            {},
                                        variant =
                                            ButtonVariant.Outline,
                                    )
                                }
                                HoverCardContent { Text { "Provider defaults can be overridden by each trigger." } }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    descriptionMarkdown = "Disable the trigger and preview.",
                    minPreviewHeight = 320.dp,
                    mobileMinPreviewHeight = 320.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        HoverCard(disabled = true) {
                            HoverCardTrigger(disabled = true) {
                                Button("Disabled preview", onClick = {}, disabled = true, variant = ButtonVariant.Outline)
                            }
                            HoverCardContent { Text { "This disabled preview does not open." } }
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCard(disabled = true) {
                            HoverCardTrigger(disabled = true) {
                                Button(
                                    "Disabled preview",
                                    onClick = {},
                                    disabled = true,
                                    variant = ButtonVariant.Outline,
                                )
                            }
                            HoverCardContent { Text { "This disabled preview does not open." } }
                        }
                    },
                ),
            ),
    )

private val hoverCardTranslations =
    listOf(
        mapOf(
            "trigger" to "Wireless Headphones",
            "name" to "Wireless Headphones",
            "price" to "$99.99",
            "inline-start" to "Inline Start",
            "left" to "Left",
            "top" to "Top",
            "bottom" to "Bottom",
            "right" to "Right",
            "inline-end" to "Inline End",
        ),
        mapOf(
            "trigger" to "سماعات لاسلكية",
            "name" to "سماعات لاسلكية",
            "price" to "٩٩.٩٩ $",
            "inline-start" to "بداية السطر",
            "left" to "يسار",
            "top" to "أعلى",
            "bottom" to "أسفل",
            "right" to "يمين",
            "inline-end" to "نهاية السطر",
        ),
        mapOf(
            "trigger" to "אוזניות אלחוטיות",
            "name" to "אוזניות אלחוטיות",
            "price" to "99.99 $",
            "inline-start" to "תחילת השורה",
            "left" to "שמאל",
            "top" to "למעלה",
            "bottom" to "למטה",
            "right" to "ימין",
            "inline-end" to "סוף השורה",
        ),
    )
