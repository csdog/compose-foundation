@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.toutiaoyan.compose.ui.catalog.catalogArabicFontFamily
import com.toutiaoyan.compose.ui.catalog.catalogHebrewFontFamily
import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
import com.toutiaoyan.compose.ui.theme.Theme
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
            import androidx.compose.ui.text.font.FontFamily
            import compose_foundation.app.shared.generated.resources.*
            import org.jetbrains.compose.resources.Font
            import com.toutiaoyan.compose.ui.theme.Theme
            import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
            import androidx.compose.ui.layout.Layout
            import androidx.compose.ui.platform.LocalDensity
            import androidx.compose.ui.text.rememberTextMeasurer
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
        beforeExamples =
            listOf(
                ComponentGuideSection(
                    "Composition",
                    listOf(
                        ComponentGuideBlock.Paragraph("Use the following composition to build a `HoverCard`:"),
                        ComponentGuideBlock.Code(
                            """
                            HoverCard
                            ├── HoverCardTrigger
                            └── HoverCardContent
                            """.trimIndent(),
                            language = null,
                        ),
                    ),
                ),
                ComponentGuideSection(
                    "Trigger Delays",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            (
                                "Use `delay` and `closeDelay` on the trigger to control when the " +
                                    "card opens and closes. Base UI defaults are 600/300ms; the demo " +
                                    "explicitly uses 10/100ms."
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
                                "Use the `side` and `align` props on `HoverCardContent` to control " +
                                    "placement. Four physical sides and InlineStart/InlineEnd resolve " +
                                    "against `LocalLayoutDirection`."
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
                    minPreviewHeight = 352.dp,
                    mobileMinPreviewHeight = 352.dp,
                    previewPadding = 40.dp,
                    code = hoverCardSidesCode,
                    preview = { HoverCardSidesRecipe() },
                ),
                ComponentExample(
                    title = "RTL",
                    descriptionMarkdown =
                        "To enable RTL support in your app, see the " +
                            "[RTL configuration guide](https://ui.shadcn.com/docs/rtl).",
                    minPreviewHeight = 384.dp,
                    mobileMinPreviewHeight = 384.dp,
                    previewPadding = 0.dp,
                    code = hoverCardRtlCode,
                    preview = { HoverCardRtlRecipe() },
                ),
                ComponentExample(
                    title = "Kotlin extension: Controlled",
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
                    title = "Kotlin extension: Provider timing",
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
                    title = "Kotlin extension: Disabled",
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

@Composable
private fun HoverCardRtlRecipe() {
    var language by remember { mutableStateOf(1) }
    val inherited = currentTheme
    Column(Modifier.fillMaxWidth()) {
        RtlPreviewToolbar(language, listOf("English", "Arabic (العربية)", "Hebrew (עברית)"), { language = it })
        Box(
            Modifier.testTag("hover-rtl-body").fillMaxWidth().height(320.dp).padding(40.dp),
            contentAlignment = Alignment.Center,
        ) {
            val font =
                when (language) {
                    1 -> catalogArabicFontFamily()
                    2 -> catalogHebrewFontFamily()
                    else -> inherited.fontFamily
                }
            CompositionLocalProvider(
                LocalLayoutDirection provides if (language == 0) LayoutDirection.Ltr else LayoutDirection.Rtl,
            ) {
                Theme(
                    preset = inherited.activePreset,
                    baseColor = inherited.activeBaseColor,
                    accent = inherited.activeAccent,
                    isDark = inherited.isDark, baseRadius = inherited.baseRadius, ring = inherited.ring,
                    unitSpacing = LocalUnitSpacing.current,
                    minTouchTarget = inherited.minTouchTarget,
                    fontFamily = font,
                ) {
                    val t = hoverCardTranslations[language]
                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                sides.forEach { (side, label) ->
                                    HoverCard {
                                        HoverCardTrigger(delay = 10, closeDelay = 100) {
                                            Button(t.getValue(label), onClick = {}, variant = ButtonVariant.Outline)
                                        }
                                        HoverCardContent(side = side) {
                                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Text(
                                                    textStyle = TextStyle(fontWeight = FontWeight.SemiBold),
                                                ) { t.getValue("name") }
                                                Text(
                                                    textStyle = TextStyle(color = currentTheme.colors.mutedForeground),
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
        }
    }
}
