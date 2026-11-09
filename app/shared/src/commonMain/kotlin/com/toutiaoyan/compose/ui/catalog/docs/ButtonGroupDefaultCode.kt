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
    import androidx.compose.ui.graphics.graphicsLayer
    import androidx.compose.ui.platform.LocalLayoutDirection
    import androidx.compose.ui.unit.LayoutDirection
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
    fun ButtonGroupDefaultRecipe(language: Int = 1) {
        val translations = buttonGroupTranslations[language].orEmpty()

        fun translate(label: String): String = translations[label] ?: label
        val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
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
                    ) { Icon(Lucide.ArrowLeft, modifier = Modifier.graphicsLayer { rotationZ = if (rtl) 180f else 0f }) }
                }
            }
            ButtonGroup {
                ButtonGroupItem(translate("Archive"), onClick = {}, variant = ButtonVariant.Outline)
                ButtonGroupItem(translate("Report"), onClick = {}, variant = ButtonVariant.Outline)
            }
            ButtonGroup {
                ButtonGroupItem(translate("Snooze"), onClick = {}, variant = ButtonVariant.Outline)
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
                    DropdownMenuContent(
                        align = if (rtl) PopupAlign.Start else PopupAlign.End,
                        style = Style { width(160.dp) },
                    ) {
                        DropdownMenuGroup {
                            ActionMenuItem(translate("Mark as Read"), Lucide.MailCheck)
                            ActionMenuItem(translate("Archive"), Lucide.Archive)
                        }
                        DropdownMenuSeparator()
                        DropdownMenuGroup {
                            ActionMenuItem(translate("Snooze"), Lucide.Clock)
                            ActionMenuItem(translate("Add to Calendar"), Lucide.CalendarPlus)
                            ActionMenuItem(translate("Add to List"), Lucide.ListFilter)
                            DropdownMenuSub {
                                DropdownMenuSubTrigger(translate("Label As..."), leadingContent = { Icon(Lucide.Tag) })
                                DropdownMenuSubContent(
                                    style = Style { width(96.dp) },
                                ) {
                                    DropdownMenuRadioGroup(value = label, onValueChange = { label = it }) {
                                        DropdownMenuRadioItem(
                                            value = "personal",
                                            label = translate("Personal"),
                                            style = Style { contentPadding(horizontal = 4.dp, vertical = 4.dp) },
                                        )
                                        DropdownMenuRadioItem(
                                            value = "work",
                                            label = translate("Work"),
                                            style = Style { contentPadding(horizontal = 4.dp, vertical = 4.dp) },
                                        )
                                        DropdownMenuRadioItem(
                                            value = "other",
                                            label = translate("Other"),
                                            style = Style { contentPadding(horizontal = 4.dp, vertical = 4.dp) },
                                        )
                                    }
                                }
                            }
                        }
                        DropdownMenuSeparator()
                        DropdownMenuGroup { ActionMenuItem(translate("Trash"), Lucide.Trash2, destructive = true) }
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

    private val buttonGroupTranslations =
        mapOf(
            0 to
                mapOf(
                    "Archive" to "أرشفة",
                    "Report" to "تقرير",
                    "Snooze" to "تأجيل",
                    "Mark as Read" to "وضع علامة كمقروء",
                    "Add to Calendar" to "إضافة إلى التقويم",
                    "Add to List" to "إضافة إلى القائمة",
                    "Label As..." to "تصنيف كـ...",
                    "Personal" to "شخصي",
                    "Work" to "عمل",
                    "Other" to "آخر",
                    "Trash" to "سلة المهملات",
                ),
            2 to
                mapOf(
                    "Archive" to "ארכיון",
                    "Report" to "דוח",
                    "Snooze" to "דחה",
                    "Mark as Read" to "סמן כנקרא",
                    "Add to Calendar" to "הוסף ליומן",
                    "Add to List" to "הוסף לרשימה",
                    "Label As..." to "תייג כ...",
                    "Personal" to "אישי",
                    "Work" to "עבודה",
                    "Other" to "אחר",
                    "Trash" to "פח",
                ),
        )
    """.trimIndent()
