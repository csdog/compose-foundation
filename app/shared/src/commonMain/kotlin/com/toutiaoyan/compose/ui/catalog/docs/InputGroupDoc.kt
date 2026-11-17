@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.Copy
import com.composables.icons.lucide.CreditCard
import com.composables.icons.lucide.EyeOff
import com.composables.icons.lucide.FileCode
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Mail
import com.composables.icons.lucide.RefreshCw
import com.composables.icons.lucide.Ellipsis
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.Star
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenu
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuContent
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuItem
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuTrigger
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.field.FieldDescription
import com.toutiaoyan.compose.ui.components.field.FieldGroup
import com.toutiaoyan.compose.ui.components.field.FieldLabel
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroup
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupAddon
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupAddonAlign
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupButton
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupButtonSize
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupInput
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupText
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupTextarea
import com.toutiaoyan.compose.ui.components.kbd.Kbd
import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
import com.toutiaoyan.compose.ui.components.popover.Popover
import com.toutiaoyan.compose.ui.components.popover.PopoverContent
import com.toutiaoyan.compose.ui.components.popover.PopoverTrigger
import com.toutiaoyan.compose.ui.components.spinner.Spinner
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
import com.toutiaoyan.compose.tailwind.core.TwColors
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.ui.catalog.catalogArabicFontFamily
import com.toutiaoyan.compose.ui.catalog.catalogHebrewFontFamily
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.utils.clipboard.rememberCopyToClipboard

/**
 * Current official page (revision 8a35366):
 * demo, Installation, Usage, Composition, Align (inline-start / inline-end / block-start /
 * block-end), Icon, Text, Button, Kbd, Dropdown, Spinner, Textarea, Custom Input, RTL,
 * API Reference (InputGroup / InputGroupAddon / InputGroupButton / InputGroupInput /
 * InputGroupTextarea).
 *
 * The previous local page was an API dump (Default disabled, Default invalid, Addon,
 * Default align, Default button variant, Default button size, Input, ...); this round
 * replaces it with the official page structure.
 */
val inputGroupDoc =
    ComponentDoc(
        id = "input-group",
        title = "Input Group",
        description = "Add addons, buttons, and helper content to inputs.",
        usageImportsCode =
            """
            import com.toutiaoyan.compose.ui.components.inputgroup.InputGroup
            import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupAddon
            import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupButton
            import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupInput
            import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupText
            import com.toutiaoyan.compose.ui.components.icon.Icon
            import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupTextarea
            import com.toutiaoyan.compose.ui.utils.clipboard.rememberCopyToClipboard
            import com.composables.icons.lucide.Lucide
            import com.composables.icons.lucide.Search
            """.trimIndent(),
        usageCode =
            """
            var query by remember { mutableStateOf("") }
            InputGroup {
                InputGroupInput(value = query, onValueChange = { query = it }, placeholder = "Search...")
                InputGroupAddon { Icon(Lucide.Search) }
            }
            """.trimIndent(),
        compositionCode =
            """
            InputGroup
            ├── InputGroupInput or InputGroupTextarea
            ├── InputGroupAddon
            ├── InputGroupButton
            └── InputGroupText
            """.trimIndent(),
        beforeExamples =
            listOf(
                ComponentGuideSection(
                    title = "Align",
                    blocks =
                        listOf(
                            ComponentGuideBlock.Paragraph(
                                "Use the `align` prop on `InputGroupAddon` to position the addon relative " +
                                    "to the input.",
                            ),
                            ComponentGuideBlock.Paragraph(
                                "For proper focus management, `InputGroupAddon` should always be placed " +
                                    "after `InputGroupInput` or `InputGroupTextarea` in the composition. " +
                                    "Use the `align` prop to visually position the addon.",
                            ),
                        ),
                ),
            ),
        examplesHeading = null,
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        var query by remember { mutableStateOf("") }
                        InputGroup(modifier = Modifier.widthIn(max = 320.dp), contentDescription = "Search") {
                            InputGroupInput(value = query, onValueChange = { query = it }, placeholder = "Search...")
                            InputGroupAddon { Icon(Lucide.Search) }
                            InputGroupAddon("12 results", align = InputGroupAddonAlign.InlineEnd)
                        }
                        """.trimIndent(),
                    preview = {
                        var query by remember { mutableStateOf("") }
                        InputGroup(modifier = Modifier.widthIn(max = 320.dp), contentDescription = "Search") {
                            InputGroupInput(value = query, onValueChange = { query = it }, placeholder = "Search...")
                            InputGroupAddon { Icon(Lucide.Search) }
                            InputGroupAddon("12 results", align = InputGroupAddonAlign.InlineEnd)
                        }
                    },
                ),
                ComponentExample(
                    title = "inline-start",
                    headingLevel = 3,
                    descriptionMarkdown =
                        "Use `align=\"inline-start\"` to position the addon at the start of " +
                            "the input. This is the default.",
                    code =
                        """
                        var query by remember { mutableStateOf("") }
                        Column(Modifier.widthIn(max = 384.dp)) {
                            Field {
                                FieldLabel("Input")
                                InputGroup {
                                    InputGroupInput(value = query, onValueChange = { query = it }, placeholder = "Search...")
                                    InputGroupAddon(align = InputGroupAddonAlign.InlineStart) {
                                        Icon(Lucide.Search)
                                    }
                                }
                                FieldDescription("Icon positioned at the start.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var query by remember { mutableStateOf("") }
                        Column(Modifier.widthIn(max = 384.dp)) {
                            Field {
                                FieldLabel("Input")
                                InputGroup {
                                    InputGroupInput(
                                        value = query,
                                        onValueChange = { query = it },
                                        placeholder = "Search...",
                                    )
                                    InputGroupAddon(align = InputGroupAddonAlign.InlineStart) {
                                        Icon(Lucide.Search)
                                    }
                                }
                                FieldDescription("Icon positioned at the start.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "inline-end",
                    headingLevel = 3,
                    descriptionMarkdown = "Use `align=\"inline-end\"` to position the addon at the end of the input.",
                    code =
                        """
                        var password by remember { mutableStateOf("") }
                        Column(Modifier.widthIn(max = 384.dp)) {
                            Field {
                                FieldLabel("Input")
                                InputGroup {
                                    InputGroupInput(
                                        value = password,
                                        onValueChange = { password = it },
                                        placeholder = "Enter password",
                                    )
                                    InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) {
                                        Icon(Lucide.EyeOff)
                                    }
                                }
                                FieldDescription("Icon positioned at the end.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var password by remember { mutableStateOf("") }
                        Column(Modifier.widthIn(max = 384.dp)) {
                            Field {
                                FieldLabel("Input")
                                InputGroup {
                                    InputGroupInput(
                                        value = password,
                                        onValueChange = { password = it },
                                        placeholder = "Enter password",
                                    )
                                    InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) {
                                        Icon(Lucide.EyeOff)
                                    }
                                }
                                FieldDescription("Icon positioned at the end.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "block-start",
                    headingLevel = 3,
                    descriptionMarkdown = "Use `align=\"block-start\"` to position the addon above the input.",
                    code =
                        """
                        var name by remember { mutableStateOf("") }
                        var script by remember { mutableStateOf("") }
                        Column(Modifier.widthIn(max = 384.dp)) {
                            FieldGroup {
                                Field {
                                    FieldLabel("Input")
                                    InputGroup {
                                        InputGroupInput(value = name, onValueChange = { name = it }, placeholder = "Enter your name")
                                        InputGroupAddon(align = InputGroupAddonAlign.BlockStart) {
                                            InputGroupText("Full Name")
                                        }
                                    }
                                    FieldDescription("Header positioned above the input.")
                                }
                                Field {
                                    FieldLabel("Textarea")
                                    InputGroup {
                                        InputGroupTextarea(
                                            value = script,
                                            onValueChange = { script = it },
                                            placeholder = "console.log('Hello, world!');",
                                        )
                                        InputGroupAddon(align = InputGroupAddonAlign.BlockStart) {
                                            InputGroupText {
                                                DocIcon(Lucide.FileCode)
                                                Text(
                                                    textStyle =
                                                        TextStyle(
                                                            fontFamily = FontFamily.Monospace,
                                                            fontWeight = FontWeight.Medium,
                                                            color = currentTheme.colors.mutedForeground,
                                                        ),
                                                ) { "script.js" }
                                            }
                                            Spacer(Modifier.weight(1f))
                                            InputGroupButton(onClick = {}, size = InputGroupButtonSize.IconXs, label = "Copy") {
                                                DocIcon(Lucide.Copy)
                                            }
                                        }
                                    }
                                    FieldDescription("Header positioned above the textarea.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var name by remember { mutableStateOf("") }
                        var script by remember { mutableStateOf("") }
                        Column(Modifier.widthIn(max = 384.dp)) {
                            FieldGroup {
                                Field {
                                    FieldLabel("Input")
                                    InputGroup {
                                        InputGroupInput(
                                            value = name,
                                            onValueChange = { name = it },
                                            placeholder = "Enter your name",
                                        )
                                        InputGroupAddon(align = InputGroupAddonAlign.BlockStart) {
                                            InputGroupText("Full Name")
                                        }
                                    }
                                    FieldDescription("Header positioned above the input.")
                                }
                                Field {
                                    FieldLabel("Textarea")
                                    InputGroup {
                                        InputGroupTextarea(
                                            value = script,
                                            onValueChange = { script = it },
                                            placeholder = "console.log('Hello, world!');",
                                        )
                                        InputGroupAddon(align = InputGroupAddonAlign.BlockStart) {
                                            InputGroupText {
                                                DocIcon(Lucide.FileCode)
                                                Text(
                                                    textStyle =
                                                        TextStyle(
                                                            fontFamily = FontFamily.Monospace,
                                                            fontWeight = FontWeight.Medium,
                                                            color = currentTheme.colors.mutedForeground,
                                                        ),
                                                ) { "script.js" }
                                            }
                                            Spacer(Modifier.weight(1f))
                                            InputGroupButton(
                                                onClick = {},
                                                size = InputGroupButtonSize.IconXs,
                                                label = "Copy",
                                            ) {
                                                DocIcon(Lucide.Copy)
                                            }
                                        }
                                    }
                                    FieldDescription("Header positioned above the textarea.")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "block-end",
                    headingLevel = 3,
                    descriptionMarkdown = "Use `align=\"block-end\"` to position the addon below the input.",
                    code =
                        """
                        var amount by remember { mutableStateOf("") }
                        var comment by remember { mutableStateOf("") }
                        Column(Modifier.widthIn(max = 384.dp)) {
                            FieldGroup {
                                Field {
                                    FieldLabel("Input")
                                    InputGroup {
                                        InputGroupInput(value = amount, onValueChange = { amount = it }, placeholder = "Enter amount")
                                        InputGroupAddon(align = InputGroupAddonAlign.BlockEnd) {
                                            InputGroupText("USD")
                                        }
                                    }
                                    FieldDescription("Footer positioned below the input.")
                                }
                                Field {
                                    FieldLabel("Textarea")
                                    InputGroup {
                                        InputGroupTextarea(
                                            value = comment,
                                            onValueChange = { comment = it },
                                            placeholder = "Write a comment...",
                                        )
                                        InputGroupAddon(align = InputGroupAddonAlign.BlockEnd) {
                                            InputGroupText("0/280")
                                            Spacer(Modifier.weight(1f))
                                            InputGroupButton(
                                                onClick = {},
                                                size = InputGroupButtonSize.Sm,
                                                variant = ButtonVariant.Default,
                                            ) { Text { "Post" } }
                                        }
                                    }
                                    FieldDescription("Footer positioned below the textarea.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var amount by remember { mutableStateOf("") }
                        var comment by remember { mutableStateOf("") }
                        Column(Modifier.widthIn(max = 384.dp)) {
                            FieldGroup {
                                Field {
                                    FieldLabel("Input")
                                    InputGroup {
                                        InputGroupInput(
                                            value = amount,
                                            onValueChange = { amount = it },
                                            placeholder = "Enter amount",
                                        )
                                        InputGroupAddon(align = InputGroupAddonAlign.BlockEnd) {
                                            InputGroupText("USD")
                                        }
                                    }
                                    FieldDescription("Footer positioned below the input.")
                                }
                                Field {
                                    FieldLabel("Textarea")
                                    InputGroup {
                                        InputGroupTextarea(
                                            value = comment,
                                            onValueChange = { comment = it },
                                            placeholder = "Write a comment...",
                                        )
                                        InputGroupAddon(align = InputGroupAddonAlign.BlockEnd) {
                                            InputGroupText("0/280")
                                            Spacer(Modifier.weight(1f))
                                            InputGroupButton(
                                                onClick = {},
                                                size = InputGroupButtonSize.Sm,
                                                variant = ButtonVariant.Default,
                                            ) { Text { "Post" } }
                                        }
                                    }
                                    FieldDescription("Footer positioned below the textarea.")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Icon",
                    code =
                        """
                        var search by remember { mutableStateOf("") }
                        var email by remember { mutableStateOf("") }
                        var card by remember { mutableStateOf("") }
                        var cardEnd by remember { mutableStateOf("") }
                        Column(
                            modifier = Modifier.widthIn(max = 384.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp),
                        ) {
                            InputGroup {
                                InputGroupInput(value = search, onValueChange = { search = it }, placeholder = "Search...")
                                InputGroupAddon { DocIcon(Lucide.Search) }
                            }
                            InputGroup {
                                InputGroupInput(value = email, onValueChange = { email = it }, placeholder = "Enter your email")
                                InputGroupAddon { DocIcon(Lucide.Mail) }
                            }
                            InputGroup {
                                InputGroupInput(value = card, onValueChange = { card = it }, placeholder = "Card number")
                                InputGroupAddon { DocIcon(Lucide.CreditCard) }
                                InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) { DocIcon(Lucide.Check) }
                            }
                            InputGroup {
                                InputGroupInput(value = cardEnd, onValueChange = { cardEnd = it }, placeholder = "Card number")
                                InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) {
                                    DocIcon(Lucide.Star)
                                    DocIcon(Lucide.Info)
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { InputGroupIconsPreview() },
                ),
                ComponentExample(
                    title = "Text",
                    code =
                        """
                        Column(
                            modifier = Modifier.widthIn(max = 384.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp),
                        ) {
                            InputGroup {
                                InputGroupAddon { InputGroupText("${'$'}") }
                                InputGroupInput(value = amount, onValueChange = {}, placeholder = "0.00")
                                InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) { InputGroupText("USD") }
                            }
                            InputGroup {
                                InputGroupAddon { InputGroupText("https://") }
                                InputGroupInput(value = domain, onValueChange = {}, placeholder = "example.com")
                                InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) { InputGroupText(".com") }
                            }
                            InputGroup {
                                InputGroupInput(value = username, onValueChange = {}, placeholder = "Enter your username")
                                InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) { InputGroupText("@company.com") }
                            }
                            InputGroup {
                                InputGroupTextarea(value = message, onValueChange = {}, placeholder = "Enter your message")
                                InputGroupAddon(align = InputGroupAddonAlign.BlockEnd) {
                                    InputGroupText("120 characters left")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { InputGroupTextPreview() },
                ),
                ComponentExample(
                    title = "Button",
                    code =
                        """
                        val copy = rememberCopyToClipboard("https://x.com/shadcn")
                        var favorite by remember { mutableStateOf(false) }
                        var url by remember { mutableStateOf("") }
                        var search by remember { mutableStateOf("") }
                        Column(
                            modifier = Modifier.widthIn(max = 384.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp),
                        ) {
                            InputGroup {
                                InputGroupInput(value = "https://x.com/shadcn", onValueChange = {}, readOnly = true)
                                InputGroupAddon(align = InputGroupAddonAlign.InlineEnd, style = Style { contentPaddingEnd(3.2.dp) }) {
                                    InputGroupButton(
                                        onClick = copy.copy,
                                        size = InputGroupButtonSize.IconXs,
                                        label = "Copy",
                                    ) { if (copy.justCopied) DocIcon(Lucide.Check) else DocIcon(Lucide.Copy) }
                                }
                            }
                            InputGroup(style = Style { shape(CircleShape) }) {
                                InputGroupAddon(style = Style { contentPaddingStart(3.2.dp) }) {
                                    Popover {
                                        PopoverTrigger {
                                            InputGroupButton(
                                                onClick = toggle,
                                                size = InputGroupButtonSize.IconXs,
                                                variant = ButtonVariant.Secondary,
                                                style = Style { shape(CircleShape) },
                                                label = "Info",
                                            ) {
                                                DocIcon(Lucide.Info)
                                            }
                                        }
                                        val popupRadius = twRadius.xl
                                        PopoverContent(
                                            align = PopupAlign.Start,
                                            style = Style { shape(RoundedCornerShape(popupRadius)) },
                                        ) {
                                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Text(textStyle = TextStyle(fontWeight = FontWeight.Medium)) {
                                                    "Your connection is not secure."
                                                }
                                                Text { "You should not enter any sensitive information on this site." }
                                            }
                                        }
                                    }
                                }
                                InputGroupAddon(style = Style { contentPaddingStart(6.dp) }) { InputGroupText("https://") }
                                InputGroupInput(value = url, onValueChange = { url = it })
                                InputGroupAddon(align = InputGroupAddonAlign.InlineEnd, style = Style { contentPaddingEnd(3.2.dp) }) {
                                    InputGroupButton(
                                        onClick = { favorite = !favorite },
                                        size = InputGroupButtonSize.IconXs,
                                        label = "Favorite",
                                        style = Style { shape(CircleShape) },
                                    ) {
                                        DocIcon(Lucide.Star, tint = if (favorite) TwColors.blue600 else currentTheme.colors.mutedForeground)
                                    }
                                }
                            }
                            InputGroup {
                                InputGroupInput(value = search, onValueChange = { search = it }, placeholder = "Type to search...")
                                InputGroupAddon(align = InputGroupAddonAlign.InlineEnd, style = Style { contentPaddingEnd(3.2.dp) }) {
                                    InputGroupButton("Search", onClick = {}, variant = ButtonVariant.Secondary)
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { InputGroupButtonsPreview() },
                ),
                ComponentExample(
                    title = "Kbd",
                    code =
                        """
                        var query by remember { mutableStateOf("") }
                        val keyRadius = (currentTheme.baseRadius.value - 5.dp).coerceAtLeast(0.dp)
                        InputGroup(modifier = Modifier.widthIn(max = 384.dp)) {
                            InputGroupInput(value = query, onValueChange = { query = it }, placeholder = "Search...")
                            InputGroupAddon { DocIcon(Lucide.Search) }
                            InputGroupAddon(
                                align = InputGroupAddonAlign.InlineEnd,
                                style = Style { contentPaddingEnd(5.6.dp) },
                            ) {
                                Kbd("⌘K", style = Style { shape(RoundedCornerShape(keyRadius)) })
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var query by remember { mutableStateOf("") }
                        val keyRadius = (currentTheme.baseRadius.value - 5.dp).coerceAtLeast(0.dp)
                        InputGroup(modifier = Modifier.widthIn(max = 384.dp)) {
                            InputGroupInput(value = query, onValueChange = { query = it }, placeholder = "Search...")
                            InputGroupAddon { DocIcon(Lucide.Search) }
                            InputGroupAddon(
                                align = InputGroupAddonAlign.InlineEnd,
                                style = Style { contentPaddingEnd(5.6.dp) },
                            ) {
                                Kbd("⌘K", style = Style { shape(RoundedCornerShape(keyRadius)) })
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Dropdown",
                    code =
                        """
                        var fileName by remember { mutableStateOf("") }
                        var search by remember { mutableStateOf("") }
                        Column(
                            modifier = Modifier.widthIn(max = 384.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            InputGroup {
                                InputGroupInput(value = fileName, onValueChange = { fileName = it }, placeholder = "Enter file name")
                                InputGroupAddon(align = InputGroupAddonAlign.InlineEnd, style = Style { contentPaddingEnd(3.2.dp) }) {
                                    DropdownMenu {
                                        DropdownMenuTrigger(label = "More") { trigger, onClick ->
                                            InputGroupButton(onClick, trigger, size = InputGroupButtonSize.IconXs, label = "More") {
                                                DocIcon(Lucide.Ellipsis)
                                            }
                                        }
                                        DropdownMenuContent(align = PopupAlign.End, sideOffset = 8.dp, alignOffset = (-4).dp) {
                                            DropdownMenuItem("Settings", onClick = {})
                                            DropdownMenuItem("Copy path", onClick = {})
                                            DropdownMenuItem("Open location", onClick = {})
                                        }
                                    }
                                }
                            }
                            InputGroup {
                                InputGroupInput(value = search, onValueChange = { search = it }, placeholder = "Enter search query")
                                InputGroupAddon(align = InputGroupAddonAlign.InlineEnd, style = Style { contentPaddingEnd(3.2.dp) }) {
                                    DropdownMenu {
                                        DropdownMenuTrigger(label = "Search In...") { trigger, onClick ->
                                            InputGroupButton(onClick, trigger) {
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    Text(
                                                        textStyle =
                                                            TextStyle(
                                                                fontSize = 12.sp,
                                                                lineHeight = 16.sp,
                                                                fontWeight = FontWeight.Medium,
                                                            ),
                                                    ) {
                                                        "Search In..."
                                                    }
                                                    DocIcon(Lucide.ChevronDown, Modifier.size(12.dp))
                                                }
                                            }
                                        }
                                        DropdownMenuContent(align = PopupAlign.End, sideOffset = 8.dp, alignOffset = (-4).dp) {
                                            DropdownMenuItem("Documentation", onClick = {})
                                            DropdownMenuItem("Blog Posts", onClick = {})
                                            DropdownMenuItem("Changelog", onClick = {})
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { InputGroupDropdownPreview() },
                ),
                ComponentExample(
                    title = "Spinner",
                    code =
                        """
                        Column(
                            modifier = Modifier.widthIn(max = 384.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            InputGroup {
                                InputGroupInput(value = searching, onValueChange = {}, placeholder = "Searching...")
                                InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) { Spinner() }
                            }
                            InputGroup {
                                InputGroupInput(value = processing, onValueChange = {}, placeholder = "Processing...")
                                InputGroupAddon { Spinner() }
                            }
                            InputGroup {
                                InputGroupInput(value = saving, onValueChange = {}, placeholder = "Saving changes...")
                                InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) {
                                    InputGroupText("Saving...")
                                    Spinner()
                                }
                            }
                            InputGroup {
                                InputGroupInput(value = refreshing, onValueChange = {}, placeholder = "Refreshing data...")
                                InputGroupAddon { Spinner() }
                                InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) {
                                    InputGroupText("Please wait...")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { InputGroupSpinnerPreview() },
                ),
                ComponentExample(
                    title = "Textarea",
                    code =
                        """
                        var code by remember { mutableStateOf("") }
                        val border = currentTheme.colors.border
                        Column(Modifier.widthIn(max = 448.dp)) {
                            InputGroup {
                                InputGroupTextarea(
                                    value = code,
                                    onValueChange = { code = it },
                                    modifier = Modifier.heightIn(min = 200.dp),
                                    placeholder = "console.log('Hello, world!');",
                                )
                                InputGroupAddon(
                                    align = InputGroupAddonAlign.BlockStart,
                                    modifier = Modifier.drawBehind {
                                        val y = size.height - .5.dp.toPx()
                                        drawLine(border, Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
                                    },
                                    style = Style { contentPaddingBottom(8.dp) },
                                ) {
                                    InputGroupText {
                                        DocIcon(Lucide.FileCode)
                                        Text(
                                            textStyle =
                                                TextStyle(
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Medium,
                                                    color = currentTheme.colors.mutedForeground,
                                                ),
                                        ) { "script.js" }
                                    }
                                    Spacer(Modifier.weight(1f))
                                    InputGroupButton(onClick = {}, size = InputGroupButtonSize.IconXs, label = "Refresh") {
                                        DocIcon(Lucide.RefreshCw)
                                    }
                                    InputGroupButton(onClick = {}, size = InputGroupButtonSize.IconXs, label = "Copy") {
                                        DocIcon(Lucide.Copy)
                                    }
                                }
                                InputGroupAddon(
                                    align = InputGroupAddonAlign.BlockEnd,
                                    modifier = Modifier.drawBehind {
                                        val y = .5.dp.toPx()
                                        drawLine(border, Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
                                    },
                                    style = Style { contentPaddingTop(8.dp) },
                                ) {
                                    InputGroupText("Line 1, Column 1")
                                    Spacer(Modifier.weight(1f))
                                    InputGroupButton(onClick = {}, size = InputGroupButtonSize.Sm, variant = ButtonVariant.Default) {
                                        Text { "Run" }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { InputGroupTextareaPreview() },
                ),
                ComponentExample(
                    title = "Custom Input",
                    descriptionMarkdown =
                        "Use `InputGroupTextarea` for a growing multiline control inside the group. " +
                            "It registers focus automatically and grows with its content; `minLines` sets " +
                            "the minimum. Arbitrary custom controls do not register through a DOM slot in Compose.",
                    code =
                        """
                        var text by remember { mutableStateOf("") }
                        Column(Modifier.widthIn(max = 384.dp)) {
                            InputGroup {
                                InputGroupTextarea(value = text, onValueChange = { text = it }, minLines = 1, placeholder = "Autoresize textarea...")
                                InputGroupAddon(align = InputGroupAddonAlign.BlockEnd) {
                                    Spacer(Modifier.weight(1f))
                                    InputGroupButton(onClick = {}, size = InputGroupButtonSize.Sm, variant = ButtonVariant.Default) {
                                        Text { "Submit" }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var text by remember { mutableStateOf("") }
                        Column(Modifier.widthIn(max = 384.dp)) {
                            InputGroup {
                                InputGroupTextarea(
                                    value = text,
                                    onValueChange = { text = it },
                                    minLines = 1,
                                    placeholder = "Autoresize textarea...",
                                )
                                InputGroupAddon(align = InputGroupAddonAlign.BlockEnd) {
                                    Spacer(Modifier.weight(1f))
                                    InputGroupButton(
                                        onClick = {},
                                        size = InputGroupButtonSize.Sm,
                                        variant = ButtonVariant.Default,
                                    ) { Text { "Submit" } }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "RTL",
                    descriptionMarkdown =
                        "To enable RTL support in shadcn/ui, see the " +
                            "[RTL configuration guide](https://ui.shadcn.com/docs/rtl).",
                    code =
                        """
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Column(
                                modifier = Modifier.widthIn(max = 384.dp),
                                verticalArrangement = Arrangement.spacedBy(24.dp),
                            ) {
                                InputGroup(modifier = Modifier.widthIn(max = 320.dp)) {
                                    InputGroupInput(value = query, onValueChange = {}, placeholder = "بحث...")
                                    InputGroupAddon { DocIcon(Lucide.Search) }
                                    InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) { InputGroupText("١٢ نتيجة") }
                                }
                                InputGroup {
                                    InputGroupInput(value = searching, onValueChange = {}, placeholder = "جاري البحث...")
                                    InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) { Spinner() }
                                }
                                InputGroup {
                                    InputGroupInput(value = saving, onValueChange = {}, placeholder = "جاري حفظ التغييرات...")
                                    InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) {
                                        InputGroupText("جاري الحفظ...")
                                        Spinner()
                                    }
                                }
                                FieldGroup {
                                    Field {
                                        FieldLabel("منطقة النص")
                                        InputGroup {
                                            InputGroupTextarea(value = comment, onValueChange = {}, placeholder = "اكتب تعليقًا...")
                                            InputGroupAddon(align = InputGroupAddonAlign.BlockEnd) {
                                                InputGroupText("٠/٢٨٠")
                                                Spacer(Modifier.weight(1f))
                                                InputGroupButton(onClick = {}, size = InputGroupButtonSize.Sm, variant = ButtonVariant.Default) {
                                                    Text { "نشر" }
                                                }
                                            }
                                        }
                                        FieldDescription("تذييل موضع أسفل منطقة النص.")
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { InputGroupRtlPreview() },
                ),
            ),
        apiSections =
            listOf(
                ComponentApiSection(
                    "InputGroup",
                    "The main component that wraps inputs and addons.",
                    listOf(
                        ApiProperty("invalid", "Boolean", "false"),
                        ApiProperty("disabled", "Boolean", "false"),
                        ApiProperty("contentDescription", "String?", "null"),
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                        ApiProperty("content", "@Composable () -> Unit", "Required"),
                    ),
                ),
                ComponentApiSection(
                    "InputGroupAddon",
                    "Displays icons, text, buttons, or other content alongside inputs. Place it after " +
                        "the input and set `align` to position it.",
                    listOf(
                        ApiProperty("align", "InputGroupAddonAlign", "InlineStart"),
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                        ApiProperty("content", "@Composable RowScope.() -> Unit", "Required (slot overload)"),
                        ApiProperty("text", "String", "Required (string overload)"),
                    ),
                ),
                ComponentApiSection(
                    "InputGroupButton",
                    "Displays buttons within input groups.",
                    listOf(
                        ApiProperty("onClick", "() -> Unit", "Required"),
                        ApiProperty("size", "InputGroupButtonSize", "Xs (Xs, Sm, IconXs, IconSm)"),
                        ApiProperty("variant", "ButtonVariant", "Ghost"),
                        ApiProperty("label", "String?", "null (accessibility label for icon buttons)"),
                        ApiProperty("enabled / disabled", "Boolean", "true / false"),
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                    ),
                ),
                ComponentApiSection(
                    "InputGroupInput",
                    "An `Input` inside a group with automatic group focus registration. " +
                        "The properties listed here are supported by the wrapper.",
                    listOf(
                        ApiProperty("value / onValueChange", "String / (String) -> Unit", "Required"),
                        ApiProperty("placeholder", "String?", "null"),
                        ApiProperty("disabled / invalid / readOnly", "Boolean", "false"),
                        ApiProperty("type", "InputType", "Text"),
                    ),
                ),
                ComponentApiSection(
                    "InputGroupTextarea",
                    "A growing multiline input inside a group with automatic group focus registration. " +
                        "The properties listed here are supported by the wrapper.",
                    listOf(
                        ApiProperty("value / onValueChange", "String / (String) -> Unit", "Required"),
                        ApiProperty("placeholder", "String?", "null"),
                        ApiProperty(
                            "enabled / disabled / invalid / isError / readOnly",
                            "Boolean",
                            "true / false / false / false / false",
                        ),
                        ApiProperty("rows", "Int?", "null"),
                        ApiProperty("minLines / maxLines", "Int", "1 / Int.MAX_VALUE"),
                        ApiProperty("modifier", "Modifier", "Modifier"),
                    ),
                ),
                ComponentApiSection(
                    "InputGroupText",
                    "Plain text inside an addon (currency, domain suffix, character count).",
                    listOf(
                        ApiProperty("text", "String", "Required (string overload)"),
                        ApiProperty("content", "@Composable RowScope.() -> Unit", "Required (slot overload)"),
                        ApiProperty("modifier", "Modifier", "Modifier"),
                    ),
                ),
            ),
    )

@Composable
private fun InputGroupIconsPreview() {
    var search by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var card by remember { mutableStateOf("") }
    var cardEnd by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.widthIn(max = 384.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        InputGroup {
            InputGroupInput(value = search, onValueChange = { search = it }, placeholder = "Search...")
            InputGroupAddon { DocIcon(Lucide.Search) }
        }
        InputGroup {
            InputGroupInput(value = email, onValueChange = { email = it }, placeholder = "Enter your email")
            InputGroupAddon { DocIcon(Lucide.Mail) }
        }
        InputGroup {
            InputGroupInput(value = card, onValueChange = { card = it }, placeholder = "Card number")
            InputGroupAddon { DocIcon(Lucide.CreditCard) }
            InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) { DocIcon(Lucide.Check) }
        }
        InputGroup {
            InputGroupInput(value = cardEnd, onValueChange = { cardEnd = it }, placeholder = "Card number")
            InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) {
                DocIcon(Lucide.Star)
                DocIcon(Lucide.Info)
            }
        }
    }
}

@Composable
private fun InputGroupTextPreview() {
    var amount by remember { mutableStateOf("") }
    var domain by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.widthIn(max = 384.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        InputGroup {
            InputGroupAddon { InputGroupText("$") }
            InputGroupInput(value = amount, onValueChange = { amount = it }, placeholder = "0.00")
            InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) { InputGroupText("USD") }
        }
        InputGroup {
            InputGroupAddon { InputGroupText("https://") }
            InputGroupInput(value = domain, onValueChange = { domain = it }, placeholder = "example.com")
            InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) { InputGroupText(".com") }
        }
        InputGroup {
            InputGroupInput(value = username, onValueChange = { username = it }, placeholder = "Enter your username")
            InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) { InputGroupText("@company.com") }
        }
        InputGroup {
            InputGroupTextarea(value = message, onValueChange = { message = it }, placeholder = "Enter your message")
            InputGroupAddon(align = InputGroupAddonAlign.BlockEnd) {
                InputGroupText("120 characters left")
            }
        }
    }
}

@Composable
private fun InputGroupButtonsPreview() {
    val copy = rememberCopyToClipboard("https://x.com/shadcn")
    var favorite by remember { mutableStateOf(false) }
    var url by remember { mutableStateOf("") }
    var search by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.widthIn(max = 384.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        InputGroup {
            InputGroupInput(value = "https://x.com/shadcn", onValueChange = {}, readOnly = true)
            InputGroupAddon(align = InputGroupAddonAlign.InlineEnd, style = Style { contentPaddingEnd(3.2.dp) }) {
                InputGroupButton(
                    onClick = copy.copy,
                    size = InputGroupButtonSize.IconXs,
                    label = "Copy",
                ) { if (copy.justCopied) DocIcon(Lucide.Check) else DocIcon(Lucide.Copy) }
            }
        }
        InputGroup(style = Style { shape(CircleShape) }) {
            InputGroupAddon(style = Style { contentPaddingStart(3.2.dp) }) {
                Popover {
                    PopoverTrigger {
                        InputGroupButton(
                            onClick = toggle,
                            size = InputGroupButtonSize.IconXs,
                            variant = ButtonVariant.Secondary,
                            style = Style { shape(CircleShape) },
                            label = "Info",
                        ) {
                            DocIcon(Lucide.Info)
                        }
                    }
                    val popupRadius = twRadius.xl
                    PopoverContent(
                        align = PopupAlign.Start,
                        style = Style { shape(RoundedCornerShape(popupRadius)) },
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(textStyle = TextStyle(fontWeight = FontWeight.Medium)) {
                                "Your connection is not secure."
                            }
                            Text { "You should not enter any sensitive information on this site." }
                        }
                    }
                }
            }
            InputGroupAddon(style = Style { contentPaddingStart(6.dp) }) { InputGroupText("https://") }
            InputGroupInput(value = url, onValueChange = { url = it })
            InputGroupAddon(align = InputGroupAddonAlign.InlineEnd, style = Style { contentPaddingEnd(3.2.dp) }) {
                InputGroupButton(
                    onClick = { favorite = !favorite },
                    size = InputGroupButtonSize.IconXs,
                    label = "Favorite",
                    style = Style { shape(CircleShape) },
                ) {
                    DocIcon(
                        Lucide.Star,
                        tint = if (favorite) TwColors.blue600 else currentTheme.colors.mutedForeground,
                    )
                }
            }
        }
        InputGroup {
            InputGroupInput(value = search, onValueChange = { search = it }, placeholder = "Type to search...")
            InputGroupAddon(align = InputGroupAddonAlign.InlineEnd, style = Style { contentPaddingEnd(3.2.dp) }) {
                InputGroupButton("Search", onClick = {}, variant = ButtonVariant.Secondary)
            }
        }
    }
}

@Composable
private fun InputGroupDropdownPreview() {
    var fileName by remember { mutableStateOf("") }
    var search by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.widthIn(max = 384.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        InputGroup {
            InputGroupInput(value = fileName, onValueChange = { fileName = it }, placeholder = "Enter file name")
            InputGroupAddon(align = InputGroupAddonAlign.InlineEnd, style = Style { contentPaddingEnd(3.2.dp) }) {
                DropdownMenu {
                    DropdownMenuTrigger(label = "More") { trigger, onClick ->
                        InputGroupButton(onClick, trigger, size = InputGroupButtonSize.IconXs, label = "More") {
                            DocIcon(Lucide.Ellipsis)
                        }
                    }
                    DropdownMenuContent(align = PopupAlign.End, sideOffset = 8.dp, alignOffset = (-4).dp) {
                        DropdownMenuItem("Settings", onClick = {})
                        DropdownMenuItem("Copy path", onClick = {})
                        DropdownMenuItem("Open location", onClick = {})
                    }
                }
            }
        }
        InputGroup {
            InputGroupInput(value = search, onValueChange = { search = it }, placeholder = "Enter search query")
            InputGroupAddon(align = InputGroupAddonAlign.InlineEnd, style = Style { contentPaddingEnd(3.2.dp) }) {
                DropdownMenu {
                    DropdownMenuTrigger(label = "Search In...") { trigger, onClick ->
                        InputGroupButton(onClick, trigger) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    textStyle =
                                        TextStyle(
                                            fontSize = 12.sp,
                                            lineHeight = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                        ),
                                ) {
                                    "Search In..."
                                }
                                DocIcon(Lucide.ChevronDown, Modifier.size(12.dp))
                            }
                        }
                    }
                    DropdownMenuContent(align = PopupAlign.End, sideOffset = 8.dp, alignOffset = (-4).dp) {
                        DropdownMenuItem("Documentation", onClick = {})
                        DropdownMenuItem("Blog Posts", onClick = {})
                        DropdownMenuItem("Changelog", onClick = {})
                    }
                }
            }
        }
    }
}

@Composable
private fun InputGroupSpinnerPreview() {
    var searching by remember { mutableStateOf("") }
    var processing by remember { mutableStateOf("") }
    var saving by remember { mutableStateOf("") }
    var refreshing by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.widthIn(max = 384.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        InputGroup {
            InputGroupInput(value = searching, onValueChange = { searching = it }, placeholder = "Searching...")
            InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) { Spinner() }
        }
        InputGroup {
            InputGroupInput(value = processing, onValueChange = { processing = it }, placeholder = "Processing...")
            InputGroupAddon { Spinner() }
        }
        InputGroup {
            InputGroupInput(value = saving, onValueChange = { saving = it }, placeholder = "Saving changes...")
            InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) {
                InputGroupText("Saving...")
                Spinner()
            }
        }
        InputGroup {
            InputGroupInput(value = refreshing, onValueChange = { refreshing = it }, placeholder = "Refreshing data...")
            InputGroupAddon { Spinner() }
            InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) {
                InputGroupText("Please wait...")
            }
        }
    }
}

@Composable
private fun InputGroupTextareaPreview() {
    var code by remember { mutableStateOf("") }
    val border = currentTheme.colors.border
    Column(Modifier.widthIn(max = 448.dp)) {
        InputGroup {
            InputGroupTextarea(
                value = code,
                onValueChange = { code = it },
                modifier = Modifier.heightIn(min = 200.dp),
                placeholder = "console.log('Hello, world!');",
            )
            InputGroupAddon(
                align = InputGroupAddonAlign.BlockStart,
                modifier =
                    Modifier.testTag("input-group-code-header").drawBehind {
                        val y = size.height - .5.dp.toPx()
                        drawLine(border, Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
                    },
                style = Style { contentPaddingBottom(8.dp) },
            ) {
                InputGroupText {
                    DocIcon(Lucide.FileCode)
                    Text(
                        textStyle =
                            TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Medium,
                                color = currentTheme.colors.mutedForeground,
                            ),
                    ) { "script.js" }
                }
                Spacer(Modifier.weight(1f))
                InputGroupButton(onClick = {}, size = InputGroupButtonSize.IconXs, label = "Refresh") {
                    DocIcon(Lucide.RefreshCw)
                }
                InputGroupButton(onClick = {}, size = InputGroupButtonSize.IconXs, label = "Copy") {
                    DocIcon(Lucide.Copy)
                }
            }
            InputGroupAddon(
                align = InputGroupAddonAlign.BlockEnd,
                modifier =
                    Modifier.testTag("input-group-code-footer").drawBehind {
                        val y = .5.dp.toPx()
                        drawLine(border, Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
                    },
                style = Style { contentPaddingTop(8.dp) },
            ) {
                InputGroupText("Line 1, Column 1")
                Spacer(Modifier.weight(1f))
                InputGroupButton(onClick = {}, size = InputGroupButtonSize.Sm, variant = ButtonVariant.Default) {
                    Text { "Run" }
                }
            }
        }
    }
}

private data class InputGroupRtlText(
    val language: String,
    val direction: LayoutDirection,
    val placeholder: String,
    val results: String,
    val searching: String,
    val savingChanges: String,
    val saving: String,
    val textareaLabel: String,
    val textareaPlaceholder: String,
    val characterCount: String,
    val post: String,
    val textareaDescription: String,
)

private val inputGroupRtlTexts =
    listOf(
        InputGroupRtlText(
            "Arabic (العربية)",
            LayoutDirection.Rtl,
            "بحث...",
            "١٢ نتيجة",
            "جاري البحث...",
            "جاري حفظ التغييرات...",
            "جاري الحفظ...",
            "منطقة النص",
            "اكتب تعليقًا...",
            "٠/٢٨٠",
            "نشر",
            "تذييل موضع أسفل منطقة النص.",
        ),
        InputGroupRtlText(
            "English",
            LayoutDirection.Ltr,
            "Search...",
            "12 results",
            "Searching...",
            "Saving changes...",
            "Saving...",
            "Textarea",
            "Write a comment...",
            "0/280",
            "Post",
            "Footer positioned below the textarea.",
        ),
        InputGroupRtlText(
            "Hebrew (עברית)",
            LayoutDirection.Rtl,
            "חפש...",
            "12 תוצאות",
            "מחפש...",
            "שומר שינויים...",
            "שומר...",
            "אזור טקסט",
            "כתוב תגובה...",
            "0/280",
            "פרסם",
            "כותרת תחתונה ממוקמת מתחת לאזור הטקסט.",
        ),
    )

@Composable
private fun InputGroupRtlPreview() {
    var language by remember { mutableStateOf(0) }
    val text = inputGroupRtlTexts[language]
    val inherited = currentTheme
    var query by remember { mutableStateOf("") }
    var searching by remember { mutableStateOf("") }
    var saving by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    RtlPreview(language, inputGroupRtlTexts.map { it.language }, { language = it }) {
        Theme(
            preset = inherited.activePreset,
            baseColor = inherited.activeBaseColor,
            accent = inherited.activeAccent,
            isDark = inherited.isDark,
            baseRadius = inherited.baseRadius,
            ring = inherited.ring,
            unitSpacing = LocalUnitSpacing.current,
            minTouchTarget = inherited.minTouchTarget,
            fontFamily =
                when (language) {
                    0 -> catalogArabicFontFamily()
                    2 -> catalogHebrewFontFamily()
                    else -> inherited.fontFamily
                },
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides text.direction) {
                Column(
                    modifier = Modifier.widthIn(max = 384.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    InputGroup(modifier = Modifier.widthIn(max = 320.dp)) {
                        InputGroupInput(value = query, onValueChange = { query = it }, placeholder = text.placeholder)
                        InputGroupAddon { DocIcon(Lucide.Search) }
                        InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) { InputGroupText(text.results) }
                    }
                    InputGroup {
                        InputGroupInput(
                            value = searching,
                            onValueChange = { searching = it },
                            placeholder = text.searching,
                        )
                        InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) { Spinner() }
                    }
                    InputGroup {
                        InputGroupInput(
                            value = saving,
                            onValueChange = { saving = it },
                            placeholder = text.savingChanges,
                        )
                        InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) {
                            InputGroupText(text.saving)
                            Spinner()
                        }
                    }
                    FieldGroup {
                        Field {
                            FieldLabel(text.textareaLabel)
                            InputGroup {
                                InputGroupTextarea(
                                    value = comment,
                                    onValueChange = { comment = it },
                                    placeholder = text.textareaPlaceholder,
                                )
                                InputGroupAddon(align = InputGroupAddonAlign.BlockEnd) {
                                    InputGroupText(text.characterCount)
                                    Spacer(Modifier.weight(1f))
                                    InputGroupButton(
                                        onClick = {},
                                        size = InputGroupButtonSize.Sm,
                                        variant = ButtonVariant.Default,
                                    ) { Text { text.post } }
                                }
                            }
                            FieldDescription(text.textareaDescription)
                        }
                    }
                }
            }
        }
    }
}
