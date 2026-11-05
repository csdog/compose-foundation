@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.*
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.command.Command
import com.toutiaoyan.compose.ui.components.command.CommandDialog
import com.toutiaoyan.compose.ui.components.command.CommandEmpty
import com.toutiaoyan.compose.ui.components.command.CommandGroup
import com.toutiaoyan.compose.ui.components.command.CommandInput
import com.toutiaoyan.compose.ui.components.command.CommandItem
import com.toutiaoyan.compose.ui.components.command.CommandList
import com.toutiaoyan.compose.ui.components.command.CommandSeparator
import com.toutiaoyan.compose.ui.components.command.CommandShortcut
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.currentTheme

/**
 * The five recipes after the first preview on the current official Base UI Command page
 * (upstream `8a3536678a...`): Basic, Shortcuts, Groups, Scrollable and RTL.
 *
 * Recipes are data-driven so the exported code stays in sync with the preview.
 */

private class CommandRecipeItem(
    val label: String,
    val iconName: String,
    val icon: ImageVector,
    val shortcut: String? = null,
    val disabled: Boolean = false,
)

private fun commandItemsCode(items: List<CommandRecipeItem>): String =
    items.joinToString(separator = "\n") { item ->
        val shortcut = item.shortcut?.let { ", shortcut = \"$it\"" } ?: ""
        val disabled = if (item.disabled) ", disabled = true" else ""
        "            CommandExampleItem(\"${item.label}\", Lucide.${item.iconName}$shortcut$disabled)"
    }

private const val commandItemHelperCode =
    """
    @Composable
    private fun CommandExampleItem(
        label: String,
        icon: ImageVector,
        shortcut: String? = null,
        disabled: Boolean = false,
    ) {
        CommandItem(label, onSelect = {}, disabled = disabled) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Image(icon, null, Modifier.size(16.dp))
                Text(Modifier.weight(1f)) { label }
                if (shortcut != null) CommandShortcut(shortcut)
            }
        }
    }
    """

private val suggestions =
    listOf(
        CommandRecipeItem("Calendar", "Calendar", Lucide.Calendar),
        CommandRecipeItem("Search Emoji", "Smile", Lucide.Smile),
        CommandRecipeItem("Calculator", "Calculator", Lucide.Calculator),
    )

private val settingsItems =
    listOf(
        CommandRecipeItem("Profile", "User", Lucide.User, shortcut = "⌘P"),
        CommandRecipeItem("Billing", "CreditCard", Lucide.CreditCard, shortcut = "⌘B"),
        CommandRecipeItem("Settings", "Settings", Lucide.Settings, shortcut = "⌘S"),
    )

private val scrollableGroups =
    listOf(
        "Navigation" to
            listOf(
                CommandRecipeItem("Home", "House", Lucide.House, shortcut = "⌘H"),
                CommandRecipeItem("Inbox", "Inbox", Lucide.Inbox, shortcut = "⌘I"),
                CommandRecipeItem("Documents", "FileText", Lucide.FileText, shortcut = "⌘D"),
                CommandRecipeItem("Folders", "Folder", Lucide.Folder, shortcut = "⌘F"),
            ),
        "Actions" to
            listOf(
                CommandRecipeItem("New File", "Plus", Lucide.Plus, shortcut = "⌘N"),
                CommandRecipeItem("New Folder", "FolderPlus", Lucide.FolderPlus, shortcut = "⇧⌘N"),
                CommandRecipeItem("Copy", "Copy", Lucide.Copy, shortcut = "⌘C"),
                CommandRecipeItem("Cut", "Scissors", Lucide.Scissors, shortcut = "⌘X"),
                CommandRecipeItem("Paste", "ClipboardPaste", Lucide.ClipboardPaste, shortcut = "⌘V"),
                CommandRecipeItem("Delete", "Trash", Lucide.Trash, shortcut = "⌘⌫"),
            ),
        "View" to
            listOf(
                CommandRecipeItem("Grid View", "LayoutGrid", Lucide.LayoutGrid, shortcut = "⌘G"),
                CommandRecipeItem("List View", "List", Lucide.List, shortcut = "⌘L"),
                CommandRecipeItem("Zoom In", "ZoomIn", Lucide.ZoomIn, shortcut = "⌘+"),
                CommandRecipeItem("Zoom Out", "ZoomOut", Lucide.ZoomOut, shortcut = "⌘-"),
            ),
        "Account" to
            listOf(
                CommandRecipeItem("Profile", "User", Lucide.User, shortcut = "⌘P"),
                CommandRecipeItem("Billing", "CreditCard", Lucide.CreditCard, shortcut = "⌘B"),
                CommandRecipeItem("Settings", "Settings", Lucide.Settings, shortcut = "⌘S"),
                CommandRecipeItem("Notifications", "Bell", Lucide.Bell, shortcut = "⌘N"),
                CommandRecipeItem("Help & Support", "CircleQuestionMark", Lucide.CircleQuestionMark, shortcut = "⌘?"),
            ),
        "Tools" to
            listOf(
                CommandRecipeItem("Calculator", "Calculator", Lucide.Calculator),
                CommandRecipeItem("Calendar", "Calendar", Lucide.Calendar),
                CommandRecipeItem("Image Editor", "Image", Lucide.Image),
                CommandRecipeItem("Code Editor", "Code", Lucide.Code),
            ),
    )

private val rtlSuggestions =
    listOf(
        CommandRecipeItem("التقويم", "Calendar", Lucide.Calendar),
        CommandRecipeItem("البحث عن الرموز التعبيرية", "Smile", Lucide.Smile),
        CommandRecipeItem("الآلة الحاسبة", "Calculator", Lucide.Calculator, disabled = true),
    )

private val rtlSettings =
    listOf(
        CommandRecipeItem("الملف الشخصي", "User", Lucide.User, shortcut = "⌘P"),
        CommandRecipeItem("الفوترة", "CreditCard", Lucide.CreditCard, shortcut = "⌘B"),
        CommandRecipeItem("الإعدادات", "Settings", Lucide.Settings, shortcut = "⌘S"),
    )

private fun commandDialogCode(
    group: String,
    items: List<CommandRecipeItem>,
    extraGroup: Pair<String, List<CommandRecipeItem>>? = null,
): String {
    val secondGroup =
        extraGroup?.let {
            "\n            CommandSeparator()\n" +
                "            CommandGroup(heading = \"${it.first}\") {\n" +
                commandItemsCode(it.second) +
                "\n            }"
        } ?: ""
    return """
        var open by remember { mutableStateOf(false) }
        Button("Open Menu", onClick = { open = true }, variant = ButtonVariant.Outline)
        CommandDialog(open = open, onOpenChange = { open = it }) {
            CommandInput(placeholder = "Type a command or search...")
            CommandList {
                CommandEmpty()
                CommandGroup(heading = "$group") {
${commandItemsCode(items)}
                }$secondGroup
            }
        }

        $commandItemHelperCode
    """.trimIndent()
}

private val commandBasicCode =
    """
    var open by remember { mutableStateOf(false) }
    Button("Open Menu", onClick = { open = true }, variant = ButtonVariant.Outline)
    CommandDialog(open = open, onOpenChange = { open = it }) {
        CommandInput(placeholder = "Type a command or search...")
        CommandList {
            CommandEmpty()
            CommandGroup(heading = "Suggestions") {
                CommandItem("Calendar", onSelect = {})
                CommandItem("Search Emoji", onSelect = {})
                CommandItem("Calculator", onSelect = {})
            }
        }
    }
    """.trimIndent()

private val commandShortcutsCode = commandDialogCode("Settings", settingsItems)

private val commandGroupsCode =
    commandDialogCode("Suggestions", suggestions, extraGroup = "Settings" to settingsItems)

private val commandScrollableCode =
    buildString {
        appendLine("var open by remember { mutableStateOf(false) }")
        appendLine("Button(\"Open Menu\", onClick = { open = true }, variant = ButtonVariant.Outline)")
        appendLine("CommandDialog(open = open, onOpenChange = { open = it }) {")
        appendLine("    CommandInput(placeholder = \"Type a command or search...\")")
        appendLine("    CommandList {")
        appendLine("        CommandEmpty()")
        scrollableGroups.forEachIndexed { index, (heading, items) ->
            if (index > 0) appendLine("        CommandSeparator()")
            appendLine("        CommandGroup(heading = \"$heading\") {")
            items.forEach { item ->
                val shortcut = item.shortcut?.let { ", shortcut = \"$it\"" } ?: ""
                appendLine("            CommandExampleItem(\"${item.label}\", Lucide.${item.iconName}$shortcut)")
            }
            appendLine("        }")
        }
        appendLine("    }")
        appendLine("}")
        appendLine()
        append(commandItemHelperCode)
    }.trimEnd()

private val commandRtlCode =
    """
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Command(modifier = Modifier.widthIn(max = 384.dp)) {
            CommandInput(placeholder = "اكتب أمرًا أو ابحث...")
            CommandList {
                CommandEmpty(text = "لم يتم العثور على نتائج.")
                CommandGroup(heading = "اقتراحات") {
${commandItemsCode(rtlSuggestions)}
                }
                CommandSeparator()
                CommandGroup(heading = "الإعدادات") {
${commandItemsCode(rtlSettings)}
                }
            }
        }
    }

    $commandItemHelperCode
    """.trimIndent()

@Composable
private fun CommandRecipeRow(item: CommandRecipeItem) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Image(
            item.icon,
            null,
            Modifier.size(16.dp),
            colorFilter =
                ColorFilter.tint(
                    if (item.disabled) {
                        currentTheme.colors.mutedForeground
                    } else {
                        currentTheme.colors.foreground
                    },
                ),
        )
        Text(Modifier.weight(1f)) { item.label }
        item.shortcut?.let { CommandShortcut(it) }
    }
}

@Composable
private fun CommandRecipeItemRow(item: CommandRecipeItem) {
    CommandItem(item.label, onSelect = {}, disabled = item.disabled) {
        CommandRecipeRow(item)
    }
}

@Composable
private fun CommandRecipeGroup(
    heading: String,
    items: List<CommandRecipeItem>,
) {
    CommandGroup(heading = heading) {
        items.forEach { CommandRecipeItemRow(it) }
    }
}

@Composable
private fun CommandRecipeDialog(
    groups: List<Pair<String, List<CommandRecipeItem>>>,
) {
    var open by remember { mutableStateOf(false) }
    Button("Open Menu", onClick = { open = true }, variant = ButtonVariant.Outline)
    CommandDialog(open = open, onOpenChange = { open = it }) {
        CommandInput(placeholder = "Type a command or search...")
        CommandList {
            CommandEmpty()
            groups.forEachIndexed { index, (heading, items) ->
                if (index > 0) CommandSeparator()
                CommandRecipeGroup(heading, items)
            }
        }
    }
}

internal val commandOfficialRecipes =
    listOf(
        ComponentExample(
            title = "Basic",
            descriptionMarkdown = "A simple command menu in a dialog.",
            code = commandBasicCode,
            preview = {
                CommandRecipeDialog(listOf("Suggestions" to suggestions))
            },
        ),
        ComponentExample(
            title = "Shortcuts",
            code = commandShortcutsCode,
            preview = {
                CommandRecipeDialog(listOf("Settings" to settingsItems))
            },
        ),
        ComponentExample(
            title = "Groups",
            descriptionMarkdown = "A command menu with groups, icons and separators.",
            code = commandGroupsCode,
            preview = {
                CommandRecipeDialog(
                    listOf("Suggestions" to suggestions, "Settings" to settingsItems),
                )
            },
        ),
        ComponentExample(
            title = "Scrollable",
            descriptionMarkdown = "Scrollable command menu with multiple items.",
            code = commandScrollableCode,
            preview = {
                CommandRecipeDialog(scrollableGroups)
            },
        ),
        ComponentExample(
            title = "RTL",
            descriptionMarkdown = "To enable RTL support in shadcn/ui, see the RTL configuration guide.",
            code = commandRtlCode,
            preview = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Command(modifier = Modifier.widthIn(max = 384.dp)) {
                        CommandInput(placeholder = "اكتب أمرًا أو ابحث...")
                        CommandList {
                            CommandEmpty(text = "لم يتم العثور على نتائج.")
                            CommandRecipeGroup("اقتراحات", rtlSuggestions)
                            CommandSeparator()
                            CommandRecipeGroup("الإعدادات", rtlSettings)
                        }
                    }
                }
            },
        ),
    )
