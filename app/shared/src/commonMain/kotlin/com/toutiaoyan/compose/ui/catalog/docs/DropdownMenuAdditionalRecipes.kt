@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.*
import com.composables.icons.lucide.*
import com.toutiaoyan.compose.ui.components.avatar.*
import com.toutiaoyan.compose.ui.components.button.*
import com.toutiaoyan.compose.ui.components.dropdownmenu.*
import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.currentTheme
import compose_foundation.app.shared.generated.resources.Res
import compose_foundation.app.shared.generated.resources.avatar_shadcn
import org.jetbrains.compose.resources.painterResource

internal val dropdownMenuAdditionalExamples =
    listOf(
        ComponentExample(
            title = "Icons",
            descriptionMarkdown = "Combine icons with labels for quick scanning.",
            code =
                """
                var open by remember { mutableStateOf(false) }
                DropdownMenu(open = open, onOpenChange = { open = it }) {
                    DropdownMenuTrigger("Open", variant = ButtonVariant.Outline)
                    DropdownMenuContent(style = Style { width(128.dp) }) {
                        IconMenuItem("Profile", Lucide.User)
                        IconMenuItem("Billing", Lucide.CreditCard)
                        IconMenuItem("Settings", Lucide.Settings)
                        DropdownMenuSeparator()
                        IconMenuItem("Log out", Lucide.LogOut, destructive = true)
                    }
                }

                @Composable
                private fun MenuGlyph(icon: ImageVector, destructive: Boolean = false) {
                    Image(icon, null, Modifier.size(16.dp), colorFilter = ColorFilter.tint(
                        if (destructive) currentTheme.colors.destructive else currentTheme.colors.popoverForeground,
                    ))
                }

                @Composable
                private fun DropdownMenuScope.IconMenuItem(
                    label: String,
                    icon: ImageVector,
                    shortcut: String? = null,
                    destructive: Boolean = false,
                ) {
                    DropdownMenuItem(label, onClick = {}, destructive = destructive) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            MenuGlyph(icon, destructive)
                            Text(modifier = Modifier.weight(1f), textStyle = TextStyle(
                                fontSize = 14.sp, lineHeight = 20.sp,
                                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                                color =
                            if (destructive) {
                                currentTheme.colors.destructive
                            } else {
                                currentTheme.colors.popoverForeground
                            },
                            )) { label }
                            shortcut?.let { DropdownMenuShortcut(it) }
                        }
                    }
                }
                """.trimIndent(),
            preview = {
                var open by remember { mutableStateOf(false) }
                DropdownMenu(open = open, onOpenChange = { open = it }) {
                    DropdownMenuTrigger("Open", variant = ButtonVariant.Outline)
                    DropdownMenuContent(style = Style { width(128.dp) }) {
                        IconMenuItem("Profile", Lucide.User)
                        IconMenuItem("Billing", Lucide.CreditCard)
                        IconMenuItem("Settings", Lucide.Settings)
                        DropdownMenuSeparator()
                        IconMenuItem("Log out", Lucide.LogOut, destructive = true)
                    }
                }
            },
        ),
        ComponentExample(
            title = "Checkboxes Icons",
            descriptionMarkdown = "Add icons to checkbox items.",
            code =
                """
                var email by remember { mutableStateOf(true) }
                var sms by remember { mutableStateOf(false) }
                var push by remember { mutableStateOf(true) }
                var open by remember { mutableStateOf(false) }
                DropdownMenu(open = open, onOpenChange = { open = it }) {
                    DropdownMenuTrigger("Notifications", variant = ButtonVariant.Outline)
                    DropdownMenuContent(style = Style { width(192.dp) }) {
                        DropdownMenuGroup {
                            DropdownMenuLabel("Notification Preferences")
                            DropdownMenuCheckboxItem("Email notifications", checked = email, leadingContent = { MenuGlyph(Lucide.Mail) }, onCheckedChange = { email = it })
                            DropdownMenuCheckboxItem("SMS notifications", checked = sms, leadingContent = { MenuGlyph(Lucide.MessageSquare) }, onCheckedChange = { sms = it })
                            DropdownMenuCheckboxItem("Push notifications", checked = push, leadingContent = { MenuGlyph(Lucide.Bell) }, onCheckedChange = { push = it })
                        }
                    }
                }

                @Composable
                private fun MenuGlyph(icon: ImageVector, destructive: Boolean = false) {
                    Image(icon, null, Modifier.size(16.dp), colorFilter = ColorFilter.tint(
                        if (destructive) currentTheme.colors.destructive else currentTheme.colors.popoverForeground,
                    ))
                }

                @Composable
                private fun DropdownMenuScope.IconMenuItem(
                    label: String,
                    icon: ImageVector,
                    shortcut: String? = null,
                    destructive: Boolean = false,
                ) {
                    DropdownMenuItem(label, onClick = {}, destructive = destructive) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            MenuGlyph(icon, destructive)
                            Text(modifier = Modifier.weight(1f), textStyle = TextStyle(
                                fontSize = 14.sp, lineHeight = 20.sp,
                                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                                color =
                            if (destructive) {
                                currentTheme.colors.destructive
                            } else {
                                currentTheme.colors.popoverForeground
                            },
                            )) { label }
                            shortcut?.let { DropdownMenuShortcut(it) }
                        }
                    }
                }
                """.trimIndent(),
            preview = {
                var email by remember { mutableStateOf(true) }
                var sms by remember { mutableStateOf(false) }
                var push by remember { mutableStateOf(true) }
                var open by remember { mutableStateOf(false) }
                DropdownMenu(open = open, onOpenChange = { open = it }) {
                    DropdownMenuTrigger("Notifications", variant = ButtonVariant.Outline)
                    DropdownMenuContent(style = Style { width(192.dp) }) {
                        DropdownMenuGroup {
                            DropdownMenuLabel("Notification Preferences")
                            DropdownMenuCheckboxItem(
                                "Email notifications",
                                checked =
                                email,
                                leadingContent =
                                    { MenuGlyph(Lucide.Mail) },
                                onCheckedChange =
                                    {
                                        email =
                                            it
                                    },
                            )
                            DropdownMenuCheckboxItem(
                                "SMS notifications",
                                checked =
                                sms,
                                leadingContent =
                                    { MenuGlyph(Lucide.MessageSquare) },
                                onCheckedChange =
                                    {
                                        sms =
                                            it
                                    },
                            )
                            DropdownMenuCheckboxItem(
                                "Push notifications",
                                checked =
                                push,
                                leadingContent =
                                    { MenuGlyph(Lucide.Bell) },
                                onCheckedChange =
                                    {
                                        push =
                                            it
                                    },
                            )
                        }
                    }
                }
            },
        ),
        ComponentExample(
            title = "Radio Icons",
            descriptionMarkdown = "Show radio options with icons.",
            code =
                """
                var payment by remember { mutableStateOf("card") }
                var open by remember { mutableStateOf(false) }
                DropdownMenu(open = open, onOpenChange = { open = it }) {
                    DropdownMenuTrigger("Payment Method", variant = ButtonVariant.Outline)
                    DropdownMenuContent(style = Style { width(224.dp) }) {
                        DropdownMenuGroup {
                            DropdownMenuLabel("Select Payment Method")
                            DropdownMenuRadioGroup(value = payment, onValueChange = { payment = it }) {
                                DropdownMenuRadioItem("card", "Credit Card", leadingContent = { MenuGlyph(Lucide.CreditCard) })
                                DropdownMenuRadioItem("paypal", "PayPal", leadingContent = { MenuGlyph(Lucide.Wallet) })
                                DropdownMenuRadioItem("bank", "Bank Transfer", leadingContent = { MenuGlyph(Lucide.Building2) })
                            }
                        }
                    }
                }

                @Composable
                private fun MenuGlyph(icon: ImageVector, destructive: Boolean = false) {
                    Image(icon, null, Modifier.size(16.dp), colorFilter = ColorFilter.tint(
                        if (destructive) currentTheme.colors.destructive else currentTheme.colors.popoverForeground,
                    ))
                }

                @Composable
                private fun DropdownMenuScope.IconMenuItem(
                    label: String,
                    icon: ImageVector,
                    shortcut: String? = null,
                    destructive: Boolean = false,
                ) {
                    DropdownMenuItem(label, onClick = {}, destructive = destructive) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            MenuGlyph(icon, destructive)
                            Text(modifier = Modifier.weight(1f), textStyle = TextStyle(
                                fontSize = 14.sp, lineHeight = 20.sp,
                                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                                color =
                            if (destructive) {
                                currentTheme.colors.destructive
                            } else {
                                currentTheme.colors.popoverForeground
                            },
                            )) { label }
                            shortcut?.let { DropdownMenuShortcut(it) }
                        }
                    }
                }
                """.trimIndent(),
            preview = {
                var payment by remember { mutableStateOf("card") }
                var open by remember { mutableStateOf(false) }
                DropdownMenu(open = open, onOpenChange = { open = it }) {
                    DropdownMenuTrigger("Payment Method", variant = ButtonVariant.Outline)
                    DropdownMenuContent(style = Style { width(224.dp) }) {
                        DropdownMenuGroup {
                            DropdownMenuLabel("Select Payment Method")
                            DropdownMenuRadioGroup(value = payment, onValueChange = { payment = it }) {
                                DropdownMenuRadioItem(
                                    "card",
                                    "Credit Card",
                                    leadingContent =
                                        { MenuGlyph(Lucide.CreditCard) },
                                )
                                DropdownMenuRadioItem(
                                    "paypal",
                                    "PayPal",
                                    leadingContent =
                                        { MenuGlyph(Lucide.Wallet) },
                                )
                                DropdownMenuRadioItem(
                                    "bank",
                                    "Bank Transfer",
                                    leadingContent =
                                        { MenuGlyph(Lucide.Building2) },
                                )
                            }
                        }
                    }
                }
            },
        ),
        ComponentExample(
            title = "Destructive",
            descriptionMarkdown = "Use DropdownMenuItemVariant.Destructive for irreversible actions.",
            code =
                """
                var open by remember { mutableStateOf(false) }
                DropdownMenu(open = open, onOpenChange = { open = it }) {
                    DropdownMenuTrigger("Actions", variant = ButtonVariant.Outline)
                    DropdownMenuContent(style = Style { width(128.dp) }) {
                        DropdownMenuGroup {
                            IconMenuItem("Edit", Lucide.Pencil)
                            IconMenuItem("Share", Lucide.Share)
                        }
                        DropdownMenuSeparator()
                        DropdownMenuGroup {
                            IconMenuItem("Delete", Lucide.Trash, destructive = true)
                        }
                    }
                }

                @Composable
                private fun MenuGlyph(icon: ImageVector, destructive: Boolean = false) {
                    Image(icon, null, Modifier.size(16.dp), colorFilter = ColorFilter.tint(
                        if (destructive) currentTheme.colors.destructive else currentTheme.colors.popoverForeground,
                    ))
                }

                @Composable
                private fun DropdownMenuScope.IconMenuItem(
                    label: String,
                    icon: ImageVector,
                    shortcut: String? = null,
                    destructive: Boolean = false,
                ) {
                    DropdownMenuItem(label, onClick = {}, destructive = destructive) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            MenuGlyph(icon, destructive)
                            Text(modifier = Modifier.weight(1f), textStyle = TextStyle(
                                fontSize = 14.sp, lineHeight = 20.sp,
                                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                                color =
                            if (destructive) {
                                currentTheme.colors.destructive
                            } else {
                                currentTheme.colors.popoverForeground
                            },
                            )) { label }
                            shortcut?.let { DropdownMenuShortcut(it) }
                        }
                    }
                }
                """.trimIndent(),
            preview = {
                var open by remember { mutableStateOf(false) }
                DropdownMenu(open = open, onOpenChange = { open = it }) {
                    DropdownMenuTrigger("Actions", variant = ButtonVariant.Outline)
                    DropdownMenuContent(style = Style { width(128.dp) }) {
                        DropdownMenuGroup {
                            IconMenuItem("Edit", Lucide.Pencil)
                            IconMenuItem("Share", Lucide.Share)
                        }
                        DropdownMenuSeparator()
                        DropdownMenuGroup {
                            IconMenuItem("Delete", Lucide.Trash, destructive = true)
                        }
                    }
                }
            },
        ),
        ComponentExample(
            title = "Avatar",
            descriptionMarkdown = "An account switcher dropdown triggered by an avatar.",
            code =
                """
                var open by remember { mutableStateOf(false) }
                DropdownMenu(open = open, onOpenChange = { open = it }) {
                    DropdownMenuTrigger {
                        Button(onClick = toggle, variant = ButtonVariant.Ghost, size = ButtonSize.Icon,
                            label = "Open account menu", style = Style { shape(CircleShape) }) {
                            Avatar {
                                AvatarImage(painterResource(Res.drawable.avatar_shadcn), "shadcn")
                                AvatarFallback("LR")
                            }
                        }
                    }
                    DropdownMenuContent(style = Style { width(128.dp) }, align = PopupAlign.End) {
                        DropdownMenuGroup {
                            IconMenuItem("Account", Lucide.BadgeCheck)
                            IconMenuItem("Billing", Lucide.CreditCard)
                            IconMenuItem("Notifications", Lucide.Bell)
                        }
                        DropdownMenuSeparator()
                        IconMenuItem("Sign Out", Lucide.LogOut)
                    }
                }

                @Composable
                private fun MenuGlyph(icon: ImageVector, destructive: Boolean = false) {
                    Image(icon, null, Modifier.size(16.dp), colorFilter = ColorFilter.tint(
                        if (destructive) currentTheme.colors.destructive else currentTheme.colors.popoverForeground,
                    ))
                }

                @Composable
                private fun DropdownMenuScope.IconMenuItem(
                    label: String,
                    icon: ImageVector,
                    shortcut: String? = null,
                    destructive: Boolean = false,
                ) {
                    DropdownMenuItem(label, onClick = {}, destructive = destructive) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            MenuGlyph(icon, destructive)
                            Text(modifier = Modifier.weight(1f), textStyle = TextStyle(
                                fontSize = 14.sp, lineHeight = 20.sp,
                                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                                color =
                            if (destructive) {
                                currentTheme.colors.destructive
                            } else {
                                currentTheme.colors.popoverForeground
                            },
                            )) { label }
                            shortcut?.let { DropdownMenuShortcut(it) }
                        }
                    }
                }
                """.trimIndent(),
            preview = {
                var open by remember { mutableStateOf(false) }
                DropdownMenu(open = open, onOpenChange = { open = it }) {
                    DropdownMenuTrigger {
                        Button(
                            onClick = toggle,
                            variant = ButtonVariant.Ghost,
                            size = ButtonSize.Icon,
                            label = "Open account menu",
                            style = Style { shape(CircleShape) },
                        ) {
                            Avatar {
                                AvatarImage(painterResource(Res.drawable.avatar_shadcn), "shadcn")
                                AvatarFallback("LR")
                            }
                        }
                    }
                    DropdownMenuContent(style = Style { width(128.dp) }, align = PopupAlign.End) {
                        DropdownMenuGroup {
                            IconMenuItem("Account", Lucide.BadgeCheck)
                            IconMenuItem("Billing", Lucide.CreditCard)
                            IconMenuItem("Notifications", Lucide.Bell)
                        }
                        DropdownMenuSeparator()
                        IconMenuItem("Sign Out", Lucide.LogOut)
                    }
                }
            },
        ),
        ComponentExample(
            title = "Complex",
            descriptionMarkdown = "A richer example combining groups, icons, and submenus.",
            code =
                """
                var email by remember { mutableStateOf(true) }
                var sms by remember { mutableStateOf(false) }
                var push by remember { mutableStateOf(true) }
                var theme by remember { mutableStateOf("light") }
                var open by remember { mutableStateOf(false) }
                DropdownMenu(open = open, onOpenChange = { open = it }) {
                    DropdownMenuTrigger("Complex Menu", variant = ButtonVariant.Outline)
                    DropdownMenuContent(style = Style { width(176.dp) }) {
                        DropdownMenuGroup {
                            DropdownMenuLabel("File")
                            IconMenuItem("New File", Lucide.File, shortcut = "⌘N")
                            IconMenuItem("New Folder", Lucide.Folder, shortcut = "⇧⌘N")
                            DropdownMenuSub {
                                DropdownMenuSubTrigger("Open Recent", leadingContent = { MenuGlyph(Lucide.FolderOpen) })
                                DropdownMenuSubContent(style = Style { width(176.dp) }) {
                                    DropdownMenuGroup {
                                        DropdownMenuLabel("Recent Projects")
                                        IconMenuItem("Project Alpha", Lucide.FileCode)
                                        IconMenuItem("Project Beta", Lucide.FileCode)
                                        DropdownMenuSub {
                                            DropdownMenuSubTrigger("More Projects", leadingContent = { MenuGlyph(Lucide.Ellipsis) })
                                            DropdownMenuSubContent(style = Style { width(176.dp) }) {
                                                IconMenuItem("Project Gamma", Lucide.FileCode)
                                                IconMenuItem("Project Delta", Lucide.FileCode)
                                            }
                                        }
                                    }
                                    DropdownMenuSeparator()
                                    DropdownMenuGroup {
                                        IconMenuItem("Browse...", Lucide.FolderSearch)
                                    }
                                }
                            }
                            DropdownMenuSeparator()
                            IconMenuItem("Save", Lucide.Save, shortcut = "⌘S")
                            IconMenuItem("Export", Lucide.Download, shortcut = "⇧⌘E")
                        }
                        DropdownMenuSeparator()
                        DropdownMenuGroup {
                            DropdownMenuLabel("View")
                            DropdownMenuCheckboxItem("Show Sidebar", checked = email, leadingContent = { MenuGlyph(Lucide.Eye) }, onCheckedChange = { email = it })
                            DropdownMenuCheckboxItem("Show Status Bar", checked = sms, leadingContent = { MenuGlyph(Lucide.PanelsTopLeft) }, onCheckedChange = { sms = it })
                            DropdownMenuSub {
                                DropdownMenuSubTrigger("Theme", leadingContent = { MenuGlyph(Lucide.Palette) })
                                DropdownMenuSubContent(style = Style { width(144.dp) }) {
                                    DropdownMenuGroup {
                                        DropdownMenuLabel("Appearance")
                                        DropdownMenuRadioGroup(value = theme, onValueChange = { theme = it }) {
                                            DropdownMenuRadioItem("light", "Light", leadingContent = { MenuGlyph(Lucide.Sun) })
                                            DropdownMenuRadioItem("dark", "Dark", leadingContent = { MenuGlyph(Lucide.Moon) })
                                            DropdownMenuRadioItem("system", "System", leadingContent = { MenuGlyph(Lucide.Monitor) })
                                        }
                                    }
                                }
                            }
                        }
                        DropdownMenuSeparator()
                        DropdownMenuGroup {
                            DropdownMenuLabel("Account")
                            IconMenuItem("Profile", Lucide.User, shortcut = "⇧⌘P")
                            IconMenuItem("Billing", Lucide.CreditCard)
                            DropdownMenuSub {
                                DropdownMenuSubTrigger("Settings", leadingContent = { MenuGlyph(Lucide.Settings) })
                                DropdownMenuSubContent(style = Style { width(192.dp) }) {
                                    DropdownMenuGroup {
                                        DropdownMenuLabel("Preferences")
                                        IconMenuItem("Keyboard Shortcuts", Lucide.Keyboard)
                                        IconMenuItem("Language", Lucide.Languages)
                                        DropdownMenuSub {
                                            DropdownMenuSubTrigger("Notifications", leadingContent = { MenuGlyph(Lucide.Bell) })
                                            DropdownMenuSubContent(style = Style { width(192.dp) }) {
                                                DropdownMenuGroup {
                                                    DropdownMenuLabel("Notification Types")
                                                    DropdownMenuCheckboxItem("Push Notifications", checked = push, leadingContent = { MenuGlyph(Lucide.Bell) }, onCheckedChange = { push = it })
                                                    DropdownMenuCheckboxItem("Email Notifications", checked = email, leadingContent = { MenuGlyph(Lucide.Mail) }, onCheckedChange = { email = it })
                                                }
                                            }
                                        }
                                    }
                                    DropdownMenuSeparator()
                                    DropdownMenuGroup {
                                        IconMenuItem("Privacy & Security", Lucide.Shield)
                                    }
                                }
                            }
                        }
                        DropdownMenuSeparator()
                        DropdownMenuGroup {
                            IconMenuItem("Help & Support", Lucide.CircleQuestionMark)
                            IconMenuItem("Documentation", Lucide.FileText)
                        }
                        DropdownMenuSeparator()
                        DropdownMenuGroup {
                            IconMenuItem("Sign Out", Lucide.LogOut, shortcut = "⇧⌘Q", destructive = true)
                        }
                    }
                }

                @Composable
                private fun MenuGlyph(icon: ImageVector, destructive: Boolean = false) {
                    Image(icon, null, Modifier.size(16.dp), colorFilter = ColorFilter.tint(
                        if (destructive) currentTheme.colors.destructive else currentTheme.colors.popoverForeground,
                    ))
                }

                @Composable
                private fun DropdownMenuScope.IconMenuItem(
                    label: String,
                    icon: ImageVector,
                    shortcut: String? = null,
                    destructive: Boolean = false,
                ) {
                    DropdownMenuItem(label, onClick = {}, destructive = destructive) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            MenuGlyph(icon, destructive)
                            Text(modifier = Modifier.weight(1f), textStyle = TextStyle(
                                fontSize = 14.sp, lineHeight = 20.sp,
                                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                                color =
                            if (destructive) {
                                currentTheme.colors.destructive
                            } else {
                                currentTheme.colors.popoverForeground
                            },
                            )) { label }
                            shortcut?.let { DropdownMenuShortcut(it) }
                        }
                    }
                }
                """.trimIndent(),
            preview = {
                var email by remember { mutableStateOf(true) }
                var sms by remember { mutableStateOf(false) }
                var push by remember { mutableStateOf(true) }
                var theme by remember { mutableStateOf("light") }
                var open by remember { mutableStateOf(false) }
                DropdownMenu(open = open, onOpenChange = { open = it }) {
                    DropdownMenuTrigger("Complex Menu", variant = ButtonVariant.Outline)
                    DropdownMenuContent(style = Style { width(176.dp) }) {
                        DropdownMenuGroup {
                            DropdownMenuLabel("File")
                            IconMenuItem("New File", Lucide.File, shortcut = "⌘N")
                            IconMenuItem("New Folder", Lucide.Folder, shortcut = "⇧⌘N")
                            DropdownMenuSub {
                                DropdownMenuSubTrigger(
                                    "Open Recent",
                                    leadingContent =
                                        { MenuGlyph(Lucide.FolderOpen) },
                                )
                                DropdownMenuSubContent(style = Style { width(176.dp) }) {
                                    DropdownMenuGroup {
                                        DropdownMenuLabel("Recent Projects")
                                        IconMenuItem("Project Alpha", Lucide.FileCode)
                                        IconMenuItem("Project Beta", Lucide.FileCode)
                                        DropdownMenuSub {
                                            DropdownMenuSubTrigger(
                                                "More Projects",
                                                leadingContent =
                                                    { MenuGlyph(Lucide.Ellipsis) },
                                            )
                                            DropdownMenuSubContent(style = Style { width(176.dp) }) {
                                                IconMenuItem("Project Gamma", Lucide.FileCode)
                                                IconMenuItem("Project Delta", Lucide.FileCode)
                                            }
                                        }
                                    }
                                    DropdownMenuSeparator()
                                    DropdownMenuGroup {
                                        IconMenuItem("Browse...", Lucide.FolderSearch)
                                    }
                                }
                            }
                            DropdownMenuSeparator()
                            IconMenuItem("Save", Lucide.Save, shortcut = "⌘S")
                            IconMenuItem("Export", Lucide.Download, shortcut = "⇧⌘E")
                        }
                        DropdownMenuSeparator()
                        DropdownMenuGroup {
                            DropdownMenuLabel("View")
                            DropdownMenuCheckboxItem(
                                "Show Sidebar",
                                checked =
                                email,
                                leadingContent =
                                    { MenuGlyph(Lucide.Eye) },
                                onCheckedChange =
                                    {
                                        email =
                                            it
                                    },
                            )
                            DropdownMenuCheckboxItem(
                                "Show Status Bar",
                                checked =
                                sms,
                                leadingContent =
                                    { MenuGlyph(Lucide.PanelsTopLeft) },
                                onCheckedChange =
                                    {
                                        sms =
                                            it
                                    },
                            )
                            DropdownMenuSub {
                                DropdownMenuSubTrigger("Theme", leadingContent = { MenuGlyph(Lucide.Palette) })
                                DropdownMenuSubContent(style = Style { width(144.dp) }) {
                                    DropdownMenuGroup {
                                        DropdownMenuLabel("Appearance")
                                        DropdownMenuRadioGroup(value = theme, onValueChange = { theme = it }) {
                                            DropdownMenuRadioItem(
                                                "light",
                                                "Light",
                                                leadingContent =
                                                    { MenuGlyph(Lucide.Sun) },
                                            )
                                            DropdownMenuRadioItem(
                                                "dark",
                                                "Dark",
                                                leadingContent =
                                                    { MenuGlyph(Lucide.Moon) },
                                            )
                                            DropdownMenuRadioItem(
                                                "system",
                                                "System",
                                                leadingContent =
                                                    { MenuGlyph(Lucide.Monitor) },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        DropdownMenuSeparator()
                        DropdownMenuGroup {
                            DropdownMenuLabel("Account")
                            IconMenuItem("Profile", Lucide.User, shortcut = "⇧⌘P")
                            IconMenuItem("Billing", Lucide.CreditCard)
                            DropdownMenuSub {
                                DropdownMenuSubTrigger(
                                    "Settings",
                                    leadingContent =
                                        { MenuGlyph(Lucide.Settings) },
                                )
                                DropdownMenuSubContent(style = Style { width(192.dp) }) {
                                    DropdownMenuGroup {
                                        DropdownMenuLabel("Preferences")
                                        IconMenuItem("Keyboard Shortcuts", Lucide.Keyboard)
                                        IconMenuItem("Language", Lucide.Languages)
                                        DropdownMenuSub {
                                            DropdownMenuSubTrigger(
                                                "Notifications",
                                                leadingContent =
                                                    { MenuGlyph(Lucide.Bell) },
                                            )
                                            DropdownMenuSubContent(style = Style { width(192.dp) }) {
                                                DropdownMenuGroup {
                                                    DropdownMenuLabel("Notification Types")
                                                    DropdownMenuCheckboxItem(
                                                        "Push Notifications",
                                                        checked =
                                                        push,
                                                        leadingContent =
                                                            { MenuGlyph(Lucide.Bell) },
                                                        onCheckedChange =
                                                            {
                                                                push =
                                                                    it
                                                            },
                                                    )
                                                    DropdownMenuCheckboxItem(
                                                        "Email Notifications",
                                                        checked =
                                                        email,
                                                        leadingContent =
                                                            { MenuGlyph(Lucide.Mail) },
                                                        onCheckedChange =
                                                            {
                                                                email =
                                                                    it
                                                            },
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    DropdownMenuSeparator()
                                    DropdownMenuGroup {
                                        IconMenuItem("Privacy & Security", Lucide.Shield)
                                    }
                                }
                            }
                        }
                        DropdownMenuSeparator()
                        DropdownMenuGroup {
                            IconMenuItem("Help & Support", Lucide.CircleQuestionMark)
                            IconMenuItem("Documentation", Lucide.FileText)
                        }
                        DropdownMenuSeparator()
                        DropdownMenuGroup {
                            IconMenuItem("Sign Out", Lucide.LogOut, shortcut = "⇧⌘Q", destructive = true)
                        }
                    }
                }
            },
        ),
        ComponentExample(
            title = "RTL",
            descriptionMarkdown = "Set LocalLayoutDirection for the selected language.",
            code =
                """
                // Connect language to your picker: 0 English, 1 Arabic, 2 Hebrew.
                var language by remember { mutableStateOf(1) }
                CompositionLocalProvider(LocalLayoutDirection provides if (language == 0) LayoutDirection.Ltr else LayoutDirection.Rtl) {
                    val t = dropdownMenuTranslations[language]
                    var status by remember { mutableStateOf(true) }
                    var activity by remember { mutableStateOf(false) }
                    var panel by remember { mutableStateOf(false) }
                    var position by remember { mutableStateOf("bottom") }
                    var open by remember { mutableStateOf(false) }
                    DropdownMenu(open = open, onOpenChange = { open = it }) {
                        DropdownMenuTrigger(t.getValue("open"), variant = ButtonVariant.Outline)
                        DropdownMenuContent(style = Style { width(144.dp) }, align = if (language == 0) PopupAlign.Start else PopupAlign.End) {
                            DropdownMenuGroup {
                                DropdownMenuSub {
                                    DropdownMenuSubTrigger(t.getValue("account"))
                                    DropdownMenuSubContent(style = Style { width(176.dp) }) {
                                        DropdownMenuGroup {
                                            IconMenuItem(t.getValue("profile"), Lucide.User)
                                            IconMenuItem(t.getValue("billing"), Lucide.CreditCard)
                                            IconMenuItem(t.getValue("settings"), Lucide.Settings)
                                        }
                                    }
                                }
                            }
                            DropdownMenuSeparator()
                            DropdownMenuGroup {
                                DropdownMenuLabel(t.getValue("team"))
                                DropdownMenuItem(t.getValue("team"), onClick = {})
                                DropdownMenuSub {
                                    DropdownMenuSubTrigger(t.getValue("inviteUsers"))
                                    DropdownMenuSubContent(style = Style { width(176.dp) }) {
                                        DropdownMenuItem(t.getValue("email"), onClick = {})
                                        DropdownMenuItem(t.getValue("message"), onClick = {})
                                        DropdownMenuSub {
                                            DropdownMenuSubTrigger(t.getValue("more"))
                                            DropdownMenuSubContent(style = Style { width(128.dp) }) {
                                                DropdownMenuItem(t.getValue("calendar"), onClick = {})
                                                DropdownMenuItem(t.getValue("chat"), onClick = {})
                                                DropdownMenuSeparator()
                                                DropdownMenuItem(t.getValue("webhook"), onClick = {})
                                            }
                                        }
                                        DropdownMenuSeparator()
                                        DropdownMenuItem(t.getValue("advanced"), onClick = {})
                                    }
                                }
                                DropdownMenuItem(t.getValue("newTeam"), onClick = {}, shortcut = "⌘+T")
                            }
                            DropdownMenuSeparator()
                            DropdownMenuGroup {
                                DropdownMenuLabel(t.getValue("view"))
                                DropdownMenuCheckboxItem(t.getValue("statusBar"), checked = status, onCheckedChange = { status = it })
                                DropdownMenuCheckboxItem(t.getValue("activityBar"), checked = activity, onCheckedChange = { activity = it })
                                DropdownMenuCheckboxItem(t.getValue("panel"), checked = panel, onCheckedChange = { panel = it })
                            }
                            DropdownMenuSeparator()
                            DropdownMenuGroup {
                                DropdownMenuLabel(t.getValue("position"))
                                DropdownMenuRadioGroup(value = position, onValueChange = { position = it }) {
                                    DropdownMenuRadioItem("top", t.getValue("top"))
                                    DropdownMenuRadioItem("bottom", t.getValue("bottom"))
                                    DropdownMenuRadioItem("right", t.getValue("right"))
                                    DropdownMenuRadioItem("left", t.getValue("left"))
                                }
                            }
                            DropdownMenuSeparator()
                            DropdownMenuGroup {
                                DropdownMenuItem(t.getValue("logout"), onClick = {}, destructive = true)
                            }
                        }
                    }
                }

                @Composable
                private fun MenuGlyph(icon: ImageVector, destructive: Boolean = false) {
                    Image(icon, null, Modifier.size(16.dp), colorFilter = ColorFilter.tint(
                        if (destructive) currentTheme.colors.destructive else currentTheme.colors.popoverForeground,
                    ))
                }

                @Composable
                private fun DropdownMenuScope.IconMenuItem(
                    label: String,
                    icon: ImageVector,
                    shortcut: String? = null,
                    destructive: Boolean = false,
                ) {
                    DropdownMenuItem(label, onClick = {}, destructive = destructive) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            MenuGlyph(icon, destructive)
                            Text(modifier = Modifier.weight(1f), textStyle = TextStyle(
                                fontSize = 14.sp, lineHeight = 20.sp,
                                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                                color =
                            if (destructive) {
                                currentTheme.colors.destructive
                            } else {
                                currentTheme.colors.popoverForeground
                            },
                            )) { label }
                            shortcut?.let { DropdownMenuShortcut(it) }
                        }
                    }
                }

                private val dropdownMenuTranslations = listOf(
                    mapOf(
                        "open" to "Open",
                        "account" to "Account",
                        "profile" to "Profile",
                        "billing" to "Billing",
                        "settings" to "Settings",
                        "logout" to "Log out",
                        "team" to "Team",
                        "inviteUsers" to "Invite users",
                        "email" to "Email",
                        "message" to "Message",
                        "more" to "More",
                        "calendar" to "Calendar",
                        "chat" to "Chat",
                        "webhook" to "Webhook",
                        "advanced" to "Advanced...",
                        "newTeam" to "New Team",
                        "view" to "View",
                        "statusBar" to "Status Bar",
                        "activityBar" to "Activity Bar",
                        "panel" to "Panel",
                        "position" to "Position",
                        "top" to "Top",
                        "bottom" to "Bottom",
                        "right" to "Right",
                        "left" to "Left"
                    ),
                    mapOf(
                        "open" to "افتح القائمة",
                        "account" to "الحساب",
                        "profile" to "الملف الشخصي",
                        "billing" to "الفوترة",
                        "settings" to "الإعدادات",
                        "logout" to "تسجيل الخروج",
                        "team" to "الفريق",
                        "inviteUsers" to "دعوة المستخدمين",
                        "email" to "البريد الإلكتروني",
                        "message" to "رسالة",
                        "more" to "المزيد",
                        "calendar" to "تقويم",
                        "chat" to "دردشة",
                        "webhook" to "خطاف ويب",
                        "advanced" to "متقدم...",
                        "newTeam" to "فريق جديد",
                        "view" to "عرض",
                        "statusBar" to "شريط الحالة",
                        "activityBar" to "شريط النشاط",
                        "panel" to "اللوحة",
                        "position" to "الموضع",
                        "top" to "أعلى",
                        "bottom" to "أسفل",
                        "right" to "يمين",
                        "left" to "يسار"
                    ),
                    mapOf(
                        "open" to "פתח תפריט",
                        "account" to "חשבון",
                        "profile" to "פרופיל",
                        "billing" to "חיוב",
                        "settings" to "הגדרות",
                        "logout" to "התנתק",
                        "team" to "הצוות",
                        "inviteUsers" to "הזמן משתמשים",
                        "email" to "אימייל",
                        "message" to "הודעה",
                        "more" to "עוד",
                        "calendar" to "יומן",
                        "chat" to "צ'אט",
                        "webhook" to "Webhook",
                        "advanced" to "מתקדם...",
                        "newTeam" to "צוות חדש",
                        "view" to "תצוגה",
                        "statusBar" to "שורת סטטוס",
                        "activityBar" to "שורת פעילות",
                        "panel" to "לוח",
                        "position" to "מיקום",
                        "top" to "למעלה",
                        "bottom" to "למטה",
                        "right" to "ימין",
                        "left" to "שמאל"
                    )
                )
                """.trimIndent(),
            minPreviewHeight = 352.dp,
            mobileMinPreviewHeight = 352.dp,
            previewPadding = 0.dp,
            preview = {
                var language by remember { mutableStateOf(1) }
                RtlPreview(language, listOf("English", "العربية", "עברית"), { language = it }) {
                    Box(Modifier.fillMaxWidth().height(240.dp), contentAlignment = Alignment.Center) {
                        CompositionLocalProvider(
                            LocalLayoutDirection provides
                                if (language == 0) {
                                    LayoutDirection.Ltr
                                } else {
                                    LayoutDirection.Rtl
                                },
                        ) {
                            val t = dropdownMenuTranslations[language]
                            var status by remember { mutableStateOf(true) }
                            var activity by remember { mutableStateOf(false) }
                            var panel by remember { mutableStateOf(false) }
                            var position by remember { mutableStateOf("bottom") }
                            var open by remember { mutableStateOf(false) }
                            DropdownMenu(open = open, onOpenChange = { open = it }) {
                                DropdownMenuTrigger(t.getValue("open"), variant = ButtonVariant.Outline)
                                DropdownMenuContent(
                                    style =
                                        Style { width(144.dp) },
                                    align =
                                        if (language == 0) {
                                            PopupAlign.Start
                                        } else {
                                            PopupAlign.End
                                        },
                                ) {
                                    DropdownMenuGroup {
                                        DropdownMenuSub {
                                            DropdownMenuSubTrigger(t.getValue("account"))
                                            DropdownMenuSubContent(style = Style { width(176.dp) }) {
                                                DropdownMenuGroup {
                                                    IconMenuItem(t.getValue("profile"), Lucide.User)
                                                    IconMenuItem(t.getValue("billing"), Lucide.CreditCard)
                                                    IconMenuItem(t.getValue("settings"), Lucide.Settings)
                                                }
                                            }
                                        }
                                    }
                                    DropdownMenuSeparator()
                                    DropdownMenuGroup {
                                        DropdownMenuLabel(t.getValue("team"))
                                        DropdownMenuItem(t.getValue("team"), onClick = {})
                                        DropdownMenuSub {
                                            DropdownMenuSubTrigger(t.getValue("inviteUsers"))
                                            DropdownMenuSubContent(style = Style { width(176.dp) }) {
                                                DropdownMenuItem(t.getValue("email"), onClick = {})
                                                DropdownMenuItem(t.getValue("message"), onClick = {})
                                                DropdownMenuSub {
                                                    DropdownMenuSubTrigger(t.getValue("more"))
                                                    DropdownMenuSubContent(style = Style { width(128.dp) }) {
                                                        DropdownMenuItem(t.getValue("calendar"), onClick = {})
                                                        DropdownMenuItem(t.getValue("chat"), onClick = {})
                                                        DropdownMenuSeparator()
                                                        DropdownMenuItem(t.getValue("webhook"), onClick = {})
                                                    }
                                                }
                                                DropdownMenuSeparator()
                                                DropdownMenuItem(t.getValue("advanced"), onClick = {})
                                            }
                                        }
                                        DropdownMenuItem(t.getValue("newTeam"), onClick = {}, shortcut = "⌘+T")
                                    }
                                    DropdownMenuSeparator()
                                    DropdownMenuGroup {
                                        DropdownMenuLabel(t.getValue("view"))
                                        DropdownMenuCheckboxItem(
                                            t.getValue("statusBar"),
                                            checked =
                                            status,
                                            onCheckedChange =
                                                {
                                                    status =
                                                        it
                                                },
                                        )
                                        DropdownMenuCheckboxItem(
                                            t.getValue("activityBar"),
                                            checked =
                                            activity,
                                            onCheckedChange =
                                                {
                                                    activity =
                                                        it
                                                },
                                        )
                                        DropdownMenuCheckboxItem(
                                            t.getValue("panel"),
                                            checked =
                                            panel,
                                            onCheckedChange =
                                                {
                                                    panel =
                                                        it
                                                },
                                        )
                                    }
                                    DropdownMenuSeparator()
                                    DropdownMenuGroup {
                                        DropdownMenuLabel(t.getValue("position"))
                                        DropdownMenuRadioGroup(
                                            value =
                                            position,
                                            onValueChange =
                                                {
                                                    position =
                                                        it
                                                },
                                        ) {
                                            DropdownMenuRadioItem("top", t.getValue("top"))
                                            DropdownMenuRadioItem("bottom", t.getValue("bottom"))
                                            DropdownMenuRadioItem("right", t.getValue("right"))
                                            DropdownMenuRadioItem("left", t.getValue("left"))
                                        }
                                    }
                                    DropdownMenuSeparator()
                                    DropdownMenuGroup {
                                        DropdownMenuItem(t.getValue("logout"), onClick = {}, destructive = true)
                                    }
                                }
                            }
                        }
                    }
                }
            },
        ),
    )

@Composable
private fun MenuGlyph(
    icon: ImageVector,
    destructive: Boolean = false,
) {
    Image(
        icon,
        null,
        Modifier.size(16.dp),
        colorFilter =
            ColorFilter.tint(
                if (destructive) currentTheme.colors.destructive else currentTheme.colors.popoverForeground,
            ),
    )
}

@Composable
private fun DropdownMenuScope.IconMenuItem(
    label: String,
    icon: ImageVector,
    shortcut: String? = null,
    destructive: Boolean = false,
) {
    DropdownMenuItem(label, onClick = {}, destructive = destructive) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            MenuGlyph(icon, destructive)
            Text(
                modifier = Modifier.weight(1f),
                textStyle =
                    TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                        color =
                            if (destructive) {
                                currentTheme.colors.destructive
                            } else {
                                currentTheme.colors.popoverForeground
                            },
                    ),
            ) { label }
            shortcut?.let { DropdownMenuShortcut(it) }
        }
    }
}

private val dropdownMenuTranslations =
    listOf(
        mapOf(
            "open" to "Open",
            "account" to "Account",
            "profile" to "Profile",
            "billing" to "Billing",
            "settings" to "Settings",
            "logout" to "Log out",
            "team" to "Team",
            "inviteUsers" to "Invite users",
            "email" to "Email",
            "message" to "Message",
            "more" to "More",
            "calendar" to "Calendar",
            "chat" to "Chat",
            "webhook" to "Webhook",
            "advanced" to "Advanced...",
            "newTeam" to "New Team",
            "view" to "View",
            "statusBar" to "Status Bar",
            "activityBar" to "Activity Bar",
            "panel" to "Panel",
            "position" to "Position",
            "top" to "Top",
            "bottom" to "Bottom",
            "right" to "Right",
            "left" to "Left",
        ),
        mapOf(
            "open" to "افتح القائمة",
            "account" to "الحساب",
            "profile" to "الملف الشخصي",
            "billing" to "الفوترة",
            "settings" to "الإعدادات",
            "logout" to "تسجيل الخروج",
            "team" to "الفريق",
            "inviteUsers" to "دعوة المستخدمين",
            "email" to "البريد الإلكتروني",
            "message" to "رسالة",
            "more" to "المزيد",
            "calendar" to "تقويم",
            "chat" to "دردشة",
            "webhook" to "خطاف ويب",
            "advanced" to "متقدم...",
            "newTeam" to "فريق جديد",
            "view" to "عرض",
            "statusBar" to "شريط الحالة",
            "activityBar" to "شريط النشاط",
            "panel" to "اللوحة",
            "position" to "الموضع",
            "top" to "أعلى",
            "bottom" to "أسفل",
            "right" to "يمين",
            "left" to "يسار",
        ),
        mapOf(
            "open" to "פתח תפריט",
            "account" to "חשבון",
            "profile" to "פרופיל",
            "billing" to "חיוב",
            "settings" to "הגדרות",
            "logout" to "התנתק",
            "team" to "הצוות",
            "inviteUsers" to "הזמן משתמשים",
            "email" to "אימייל",
            "message" to "הודעה",
            "more" to "עוד",
            "calendar" to "יומן",
            "chat" to "צ'אט",
            "webhook" to "Webhook",
            "advanced" to "מתקדם...",
            "newTeam" to "צוות חדש",
            "view" to "תצוגה",
            "statusBar" to "שורת סטטוס",
            "activityBar" to "שורת פעילות",
            "panel" to "לוח",
            "position" to "מיקום",
            "top" to "למעלה",
            "bottom" to "למטה",
            "right" to "ימין",
            "left" to "שמאל",
        ),
    )
