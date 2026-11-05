@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Search
import com.toutiaoyan.compose.ui.components.button.*
import com.toutiaoyan.compose.ui.components.buttongroup.*
import com.toutiaoyan.compose.ui.components.inputgroup.*
import com.toutiaoyan.compose.ui.components.kbd.*
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.tooltip.*
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset

val kbdDoc =
    ComponentDoc(
        id = "kbd",
        title = "Kbd",
        description = "Used to display textual user input from keyboard.",
        examplesHeading = null,
        usageImportsCode =
            """
                        import androidx.compose.foundation.Image
                        import androidx.compose.foundation.layout.*
                        import androidx.compose.foundation.text.InlineTextContent
                        import androidx.compose.foundation.text.appendInlineContent
                        import androidx.compose.runtime.*
                        import androidx.compose.ui.Alignment
                        import androidx.compose.ui.Modifier
                        import androidx.compose.ui.graphics.ColorFilter
                        import androidx.compose.ui.platform.testTag
            import androidx.compose.ui.platform.LocalDensity
                        import androidx.compose.ui.platform.LocalLayoutDirection
                        import androidx.compose.ui.text.*
                        import androidx.compose.ui.text.font.FontWeight
                        import androidx.compose.ui.unit.*
                        import com.composables.icons.lucide.Lucide
                        import com.composables.icons.lucide.Search
                        import com.toutiaoyan.compose.ui.components.button.*
                        import com.toutiaoyan.compose.ui.components.buttongroup.*
                        import com.toutiaoyan.compose.ui.components.inputgroup.*
                        import com.toutiaoyan.compose.ui.components.kbd.*
                        import com.toutiaoyan.compose.ui.components.text.Text
                        import com.toutiaoyan.compose.ui.components.tooltip.*
                        import com.toutiaoyan.compose.ui.theme.currentTheme
                        import com.toutiaoyan.compose.ui.tokens.StylePreset
            """.trimIndent(),
        usageCode = "Kbd(\"Ctrl\")",
        compositionCode =
            """
            Kbd
            KbdGroup
            ├── Kbd
            └── Kbd
            """.trimIndent(),
        apiSections =
            listOf(
                ComponentApiSection(
                    "Kbd",
                    (
                        "A passive keycap. Use the content slot for icons or richer " +
                            "labels; it does not register a keyboard shortcut."
                    ),
                    listOf(
                        ApiProperty("text / content", "String / @Composable () -> Unit", "Required"),
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                    ),
                ),
                ComponentApiSection(
                    "KbdGroup",
                    (
                        "Groups keycaps with a 4dp Nova gap and inherits the layout " +
                            "direction."
                    ),
                    listOf(
                        ApiProperty("content", "@Composable () -> Unit", "Required"),
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                    ),
                ),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    minPreviewHeight = 288.dp,
                    mobileMinPreviewHeight = 288.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            KbdGroup { Kbd("⌘"); Kbd("⇧"); Kbd("⌥"); Kbd("⌃") }
                            KbdGroup { Kbd("Ctrl"); Text { "+" }; Kbd("B") }
                        }
                        """.trimIndent(),
                    preview = {
                        Column(
                            horizontalAlignment =
                                Alignment.CenterHorizontally,
                            verticalArrangement =
                                Arrangement.spacedBy(16.dp),
                        ) {
                            KbdGroup {
                                Kbd("⌘")
                                Kbd("⇧")
                                Kbd("⌥")
                                Kbd("⌃")
                            }
                            KbdGroup {
                                Kbd("Ctrl")
                                Text { "+" }
                                Kbd("B")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Group",
                    descriptionMarkdown = "Use the `KbdGroup` component to group keyboard keys together.",
                    minPreviewHeight = 288.dp,
                    mobileMinPreviewHeight = 288.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        val theme = currentTheme
                        val density = LocalDensity.current
                        val measurer = rememberTextMeasurer()
                        val nova = theme.activePreset == StylePreset.Nova
                        val keys = listOf("Ctrl + B", "Ctrl + K")
                        val keyStyle = if (nova) TextStyle(fontFamily = theme.fontFamily, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            else theme.typography.labelSmall.copy(fontFamily = theme.fontFamily)
                        val padding = if (nova) 8.dp else 4.dp
                        val gap = if (nova) 4.dp else 2.dp
                        val width = keys.map { key ->
                            maxOf(20.dp, with(density) { measurer.measure(key, keyStyle, maxLines = 1).size.width.toDp() } + padding)
                        }.reduce { total, next -> total + next } + gap
                        val sentence = buildAnnotatedString {
                            append("Use ")
                            appendInlineContent("shortcuts", "Ctrl + B Ctrl + K")
                            append(" to open the command palette")
                        }
                        Text(textStyle = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, color = theme.colors.mutedForeground),
                            inlineContent = mapOf("shortcuts" to InlineTextContent(
                                Placeholder(with(density) { width.toSp() }, with(density) { 20.dp.toSp() }, PlaceholderVerticalAlign.TextCenter),
                            ) { KbdGroup { keys.forEach { Kbd(it) } } }),
                        ) { sentence }
                        """.trimIndent(),
                    preview = {
                        val theme = currentTheme
                        val density = LocalDensity.current
                        val measurer = rememberTextMeasurer()
                        val nova = theme.activePreset == StylePreset.Nova
                        val keys = listOf("Ctrl + B", "Ctrl + K")
                        val keyStyle =
                            if (nova) {
                                TextStyle(
                                    fontFamily =
                                        theme.fontFamily,
                                    fontSize =
                                        12.sp,
                                    fontWeight =
                                        FontWeight.Medium,
                                )
                            } else {
                                theme.typography.labelSmall.copy(fontFamily = theme.fontFamily)
                            }
                        val padding = if (nova) 8.dp else 4.dp
                        val gap = if (nova) 4.dp else 2.dp
                        val width =
                            keys.map { key ->
                                maxOf(
                                    20.dp,
                                    with(density) {
                                        measurer.measure(
                                            key,
                                            keyStyle,
                                            maxLines =
                                            1,
                                        ).size.width.toDp()
                                    } +
                                        padding,
                                )
                            }.reduce { total, next -> total + next } + gap
                        val sentence =
                            buildAnnotatedString {
                                append("Use ")
                                appendInlineContent("shortcuts", "Ctrl + B Ctrl + K")
                                append(" to open the command palette")
                            }
                        Text(
                            textStyle =
                                TextStyle(
                                    fontSize =
                                        14.sp,
                                    lineHeight =
                                        20.sp,
                                    color =
                                        theme.colors.mutedForeground,
                                ),
                            inlineContent =
                                mapOf(
                                    "shortcuts" to
                                        InlineTextContent(
                                            Placeholder(
                                                with(density) { width.toSp() },
                                                with(density) { 20.dp.toSp() },
                                                PlaceholderVerticalAlign.TextCenter,
                                            ),
                                        ) { KbdGroup { keys.forEach { Kbd(it) } } },
                                ),
                        ) { sentence }
                    },
                ),
                ComponentExample(
                    title = "Button",
                    descriptionMarkdown =
                        "Use the `Kbd` component inside a `Button` component to display a " +
                            "keyboard key inside a button.",
                    minPreviewHeight = 288.dp,
                    mobileMinPreviewHeight = 288.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        Button(onClick = {}, variant = ButtonVariant.Outline, iconPlacement = ButtonIconPlacement.End) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text { "Accept" }
                                Kbd("⏎", modifier = Modifier.offset(x = 2.dp))
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Button(onClick = {}, variant = ButtonVariant.Outline, iconPlacement = ButtonIconPlacement.End) {
                            Row(
                                verticalAlignment =
                                    Alignment.CenterVertically,
                                horizontalArrangement =
                                    Arrangement.spacedBy(6.dp),
                            ) {
                                Text { "Accept" }
                                Kbd("⏎", modifier = Modifier.offset(x = 2.dp))
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Tooltip",
                    descriptionMarkdown =
                        "You can use the `Kbd` component inside a `Tooltip` component to " +
                            "display a tooltip with a keyboard key.",
                    minPreviewHeight = 288.dp,
                    mobileMinPreviewHeight = 288.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        TooltipProvider {
                            ButtonGroup {
                                Tooltip {
                                    TooltipTrigger {
                                        Button("Save", onClick = {}, variant = ButtonVariant.Outline)
                                    }
                                    TooltipContent {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(textStyle = TextStyle(fontSize = 12.sp, color = currentTheme.colors.background)) { "Save Changes" }
                                            Kbd("S")
                                        }
                                    }
                                }
                                Tooltip {
                                    TooltipTrigger {
                                        Button("Print", onClick = {}, variant = ButtonVariant.Outline)
                                    }
                                    TooltipContent {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(textStyle = TextStyle(fontSize = 12.sp, color = currentTheme.colors.background)) { "Print Document" }
                                            KbdGroup { Kbd("Ctrl"); Kbd("P") }
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        TooltipProvider {
                            ButtonGroup {
                                Tooltip {
                                    TooltipTrigger {
                                        Button("Save", onClick = {}, variant = ButtonVariant.Outline)
                                    }
                                    TooltipContent {
                                        Row(
                                            verticalAlignment =
                                                Alignment.CenterVertically,
                                            horizontalArrangement =
                                                Arrangement.spacedBy(6.dp),
                                        ) {
                                            Text(
                                                textStyle =
                                                    TextStyle(
                                                        fontSize =
                                                            12.sp,
                                                        color =
                                                            currentTheme.colors.background,
                                                    ),
                                            ) { "Save Changes" }
                                            Kbd("S")
                                        }
                                    }
                                }
                                Tooltip {
                                    TooltipTrigger {
                                        Button("Print", onClick = {}, variant = ButtonVariant.Outline)
                                    }
                                    TooltipContent {
                                        Row(
                                            verticalAlignment =
                                                Alignment.CenterVertically,
                                            horizontalArrangement =
                                                Arrangement.spacedBy(6.dp),
                                        ) {
                                            Text(
                                                textStyle =
                                                    TextStyle(
                                                        fontSize =
                                                            12.sp,
                                                        color =
                                                            currentTheme.colors.background,
                                                    ),
                                            ) { "Print Document" }
                                            KbdGroup {
                                                Kbd("Ctrl")
                                                Kbd("P")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Input Group",
                    descriptionMarkdown =
                        "You can use the `Kbd` component inside a `InputGroupAddon` component " +
                            "to display a keyboard key inside an input group.",
                    minPreviewHeight = 288.dp,
                    mobileMinPreviewHeight = 288.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        var query by remember { mutableStateOf("") }
                        InputGroup(modifier = Modifier.widthIn(max = 320.dp).fillMaxWidth()) {
                            InputGroupInput(query, { query = it }, placeholder = "Search...")
                            InputGroupAddon {
                                Image(Lucide.Search, null, Modifier.size(16.dp), colorFilter = ColorFilter.tint(currentTheme.colors.mutedForeground))
                            }
                            InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) { Kbd("⌘"); Kbd("K") }
                        }
                        """.trimIndent(),
                    preview = {
                        var query by remember { mutableStateOf("") }
                        InputGroup(modifier = Modifier.widthIn(max = 320.dp).fillMaxWidth()) {
                            InputGroupInput(query, { query = it }, placeholder = "Search...")
                            InputGroupAddon {
                                Image(
                                    Lucide.Search,
                                    null,
                                    Modifier.size(16.dp),
                                    colorFilter =
                                        ColorFilter.tint(currentTheme.colors.mutedForeground),
                                )
                            }
                            InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) {
                                Kbd("⌘")
                                Kbd("K")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "RTL",
                    descriptionMarkdown =
                        "To enable RTL support in shadcn/ui, see the RTL configuration guide.",
                    minPreviewHeight = 304.dp,
                    mobileMinPreviewHeight = 304.dp,
                    previewPadding = 0.dp,
                    code =
                        """
                        // 0 English, 1 Arabic, 2 Hebrew. Connect this state to your language picker.
                        var language by remember { mutableStateOf(1) }
                        CompositionLocalProvider(LocalLayoutDirection provides if (language == 0) LayoutDirection.Ltr else LayoutDirection.Rtl) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                KbdGroup { Kbd("⌘"); Kbd("⇧"); Kbd("⌥"); Kbd("⌃") }
                                KbdGroup { Kbd("Ctrl"); Text { "+" }; Kbd("B") }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var language by remember { mutableStateOf(1) }
                        Column(Modifier.fillMaxWidth()) {
                            RtlPreviewToolbar(
                                language,
                                listOf("English", "Arabic (العربية)", "Hebrew (עברית)"),
                                { language = it },
                            )
                            Box(
                                Modifier.testTag("kbd-rtl-body").fillMaxWidth().heightIn(min = 288.dp).padding(40.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                CompositionLocalProvider(
                                    LocalLayoutDirection provides
                                        if (language == 0) {
                                            LayoutDirection.Ltr
                                        } else {
                                            LayoutDirection.Rtl
                                        },
                                ) {
                                    Column(
                                        horizontalAlignment =
                                            Alignment.CenterHorizontally,
                                        verticalArrangement =
                                            Arrangement.spacedBy(16.dp),
                                    ) {
                                        KbdGroup {
                                            Kbd("⌘")
                                            Kbd("⇧")
                                            Kbd("⌥")
                                            Kbd("⌃")
                                        }
                                        KbdGroup {
                                            Kbd("Ctrl")
                                            Text { "+" }
                                            Kbd("B")
                                        }
                                    }
                                }
                            }
                        }
                    },
                ),
            ),
    )
