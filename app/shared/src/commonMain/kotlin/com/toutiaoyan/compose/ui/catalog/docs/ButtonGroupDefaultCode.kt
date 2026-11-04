package com.toutiaoyan.compose.ui.catalog.docs

internal val buttonGroupDefaultCode =
    """
    @file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)


    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.style.Style
    import androidx.compose.foundation.style.contentPadding
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.vector.ImageVector
    import androidx.compose.ui.platform.LocalDensity
    import androidx.compose.ui.platform.LocalWindowInfo
    import androidx.compose.ui.semantics.contentDescription
    import androidx.compose.ui.semantics.semantics
    import androidx.compose.ui.text.TextStyle
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import com.composables.icons.lucide.*
    import com.toutiaoyan.compose.ui.components.button.ButtonVariant
    import com.toutiaoyan.compose.ui.components.buttongroup.*
    import com.toutiaoyan.compose.ui.components.dropdownmenu.*
    import com.toutiaoyan.compose.ui.components.icon.Icon
    import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
    import com.toutiaoyan.compose.ui.components.text.Text
    import com.toutiaoyan.compose.ui.theme.currentTheme

    @Composable
    fun ButtonGroupDefaultRecipe() {
        var label by remember { mutableStateOf("personal") }
        val viewport =
            with(LocalDensity.current) {
                LocalWindowInfo.current.containerSize.width.toDp()
            }
        ButtonGroup(spacing = 8.dp, contentDescription = "Message actions") {
            if (viewport >= 640.dp) {
                ButtonGroup {
                    ButtonGroupItem(
                        onClick = {},
                        variant = ButtonVariant.Outline,
                        modifier = Modifier.semantics { contentDescription = "Go Back" },
                        style =
                            Style {
                                width(32.dp)
                                contentPadding(0.dp)
                            },
                    ) { Icon(Lucide.ArrowLeft) }
                }
            }
            ButtonGroup {
                ButtonGroupItem("Archive", onClick = {}, variant = ButtonVariant.Outline)
                ButtonGroupItem("Report", onClick = {}, variant = ButtonVariant.Outline)
            }
            ButtonGroup {
                ButtonGroupItem("Snooze", onClick = {}, variant = ButtonVariant.Outline)
                DropdownMenu {
                    DropdownMenuTrigger {
                        ButtonGroupItem(
                            onClick = toggle,
                            variant = ButtonVariant.Outline,
                            modifier = Modifier.semantics { contentDescription = "More Options" },
                            style =
                                Style {
                                    width(32.dp)
                                    contentPadding(0.dp)
                                },
                        ) { Icon(Lucide.Ellipsis) }
                    }
                    DropdownMenuContent(align = PopupAlign.End, style = Style { width(160.dp) }) {
                        DropdownMenuGroup {
                            ActionMenuItem("Mark as Read", Lucide.MailCheck)
                            ActionMenuItem("Archive", Lucide.Archive)
                        }
                        DropdownMenuSeparator()
                        DropdownMenuGroup {
                            ActionMenuItem("Snooze", Lucide.Clock)
                            ActionMenuItem("Add to Calendar", Lucide.CalendarPlus)
                            ActionMenuItem("Add to List", Lucide.ListFilter)
                            DropdownMenuSub {
                                DropdownMenuSubTrigger("Label As...", leadingContent = { Icon(Lucide.Tag) })
                                DropdownMenuSubContent {
                                    DropdownMenuRadioGroup(value = label, onValueChange = { label = it }) {
                                        DropdownMenuRadioItem(value = "personal", label = "Personal")
                                        DropdownMenuRadioItem(value = "work", label = "Work")
                                        DropdownMenuRadioItem(value = "other", label = "Other")
                                    }
                                }
                            }
                        }
                        DropdownMenuSeparator()
                        DropdownMenuGroup { ActionMenuItem("Trash", Lucide.Trash2, destructive = true) }
                    }
                }
            }
        }
    }

    @Composable
    private fun DropdownMenuScope.ActionMenuItem(
        label: String,
        icon: ImageVector,
        destructive: Boolean = false,
    ) {
        val color = if (destructive) currentTheme.colors.destructive else currentTheme.colors.popoverForeground
        DropdownMenuItem(label, onClick = {}, destructive = destructive) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(icon, modifier = Modifier.size(16.dp), tint = color)
                Text(textStyle = TextStyle(color = color, fontSize = 14.sp, lineHeight = 20.sp)) { label }
            }
        }
    }

    """.trimIndent()
