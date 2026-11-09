@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.*
import com.composables.icons.lucide.*
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuItemVariant
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.menubar.*
import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.currentTheme

private val menubarTranslations =
    listOf(
        mapOf(
            "file" to "File",
            "newTab" to "New Tab",
            "newWindow" to "New Window",
            "newIncognitoWindow" to "New Incognito Window",
            "share" to "Share",
            "emailLink" to "Email link",
            "messages" to "Messages",
            "notes" to "Notes",
            "print" to "Print...",
            "edit" to "Edit",
            "undo" to "Undo",
            "redo" to "Redo",
            "find" to "Find",
            "searchTheWeb" to "Search the web",
            "findItem" to "Find...",
            "findNext" to "Find Next",
            "findPrevious" to "Find Previous",
            "cut" to "Cut",
            "copy" to "Copy",
            "paste" to "Paste",
            "view" to "View",
            "bookmarksBar" to "Bookmarks Bar",
            "fullUrls" to "Full URLs",
            "reload" to "Reload",
            "forceReload" to "Force Reload",
            "toggleFullscreen" to "Toggle Fullscreen",
            "hideSidebar" to "Hide Sidebar",
            "profiles" to "Profiles",
            "andy" to "Andy",
            "benoit" to "Benoit",
            "luis" to "Luis",
            "editProfile" to "Edit...",
            "addProfile" to "Add Profile...",
        ),
        mapOf(
            "file" to "ملف",
            "newTab" to "علامة تبويب جديدة",
            "newWindow" to "نافذة جديدة",
            "newIncognitoWindow" to "نافذة التصفح المتخفي الجديدة",
            "share" to "مشاركة",
            "emailLink" to "رابط البريد الإلكتروني",
            "messages" to "الرسائل",
            "notes" to "الملاحظات",
            "print" to "طباعة...",
            "edit" to "تعديل",
            "undo" to "تراجع",
            "redo" to "إعادة",
            "find" to "بحث",
            "searchTheWeb" to "البحث على الويب",
            "findItem" to "بحث...",
            "findNext" to "البحث التالي",
            "findPrevious" to "البحث السابق",
            "cut" to "قص",
            "copy" to "نسخ",
            "paste" to "لصق",
            "view" to "عرض",
            "bookmarksBar" to "شريط الإشارات المرجعية",
            "fullUrls" to "عناوين URL الكاملة",
            "reload" to "إعادة تحميل",
            "forceReload" to "إعادة تحميل قسري",
            "toggleFullscreen" to "تبديل وضع ملء الشاشة",
            "hideSidebar" to "إخفاء الشريط الجانبي",
            "profiles" to "الملفات الشخصية",
            "andy" to "Andy",
            "benoit" to "Benoit",
            "luis" to "Luis",
            "editProfile" to "تعديل...",
            "addProfile" to "إضافة ملف شخصي...",
        ),
        mapOf(
            "file" to "קובץ",
            "newTab" to "כרטיסייה חדשה",
            "newWindow" to "חלון חדש",
            "newIncognitoWindow" to "חלון גלישה בסתר חדש",
            "share" to "שתף",
            "emailLink" to "קישור אימייל",
            "messages" to "הודעות",
            "notes" to "הערות",
            "print" to "הדפס...",
            "edit" to "ערוך",
            "undo" to "בטל",
            "redo" to "בצע שוב",
            "find" to "מצא",
            "searchTheWeb" to "חפש באינטרנט",
            "findItem" to "מצא...",
            "findNext" to "מצא הבא",
            "findPrevious" to "מצא הקודם",
            "cut" to "גזור",
            "copy" to "העתק",
            "paste" to "הדבק",
            "view" to "תצוגה",
            "bookmarksBar" to "סרגל סימניות",
            "fullUrls" to "כתובות URL מלאות",
            "reload" to "רענן",
            "forceReload" to "רענן בכוח",
            "toggleFullscreen" to "החלף מסך מלא",
            "hideSidebar" to "הסתר סרגל צד",
            "profiles" to "פרופילים",
            "andy" to "Andy",
            "benoit" to "Benoit",
            "luis" to "Luis",
            "editProfile" to "ערוך...",
            "addProfile" to "הוסף פרופיל...",
        ),
    )

/** The official page names these six; everything after is a Compose-only extension. */
private val menubarOfficialTitles = setOf("Default", "Checkbox", "Radio", "Submenu", "With Icons", "RTL")

private fun menubarPageExample(example: ComponentExample): ComponentExample =
    if (example.title in menubarOfficialTitles) {
        example
    } else {
        example.copy(title = "Kotlin extension: " + example.title)
    }

private val menubarApiSections =
    listOf(
        ComponentApiSection(
            "Menubar",
            (
                "A visually persistent menu bar. Each `MenubarMenu` owns a trigger and a " +
                    "popup; open menus share the bar's overlay/pointer context so moving " +
                    "between them keeps one menu open (Base UI behaviour). Arrow keys wrap " +
                    "inside a menu and Left/Right switch menus. See the [Base UI Menubar " +
                    "API](https://base-ui.com/react/components/menubar#api-reference) for " +
                    "the full prop list; the Kotlin surface below covers the same slots."
            ),
            listOf(
                ApiProperty("content", "@Composable MenubarScope.() -> Unit", "Required"),
            ),
        ),
        ComponentApiSection(
            "MenubarMenu / MenubarTrigger / MenubarContent",
            (
                "`MenubarMenu(value = …)` is the identity used for controlled state. " +
                    "`MenubarTrigger` is the persistent bar button; `MenubarContent` is " +
                    "the anchored panel (Nova: popover surface, 1dp ring, shadow) with " +
                    "`sideOffset`/`align` overrides."
            ),
            listOf(
                ApiProperty("value", "String", "Required"),
                ApiProperty("sideOffset", "Dp", "4.dp"),
                ApiProperty("align", "PopupAlign", "PopupAlign.Start"),
            ),
        ),
        ComponentApiSection(
            "Items, groups and separators",
            (
                "`MenubarItem` accepts an optional `shortcut`; `MenubarCheckboxItem` and " +
                    "`MenubarRadioGroup`/`MenubarRadioItem` keep their state across close/" +
                    "reopen like the upstream examples, and `MenubarSeparator` matches the " +
                    "1px rule. `MenubarSub`/`MenubarSubTrigger`/`MenubarSubContent` add the " +
                    "nested panel."
            ),
            listOf(
                ApiProperty("shortcut", "String?", "null"),
                ApiProperty("inset", "Boolean", "false"),
                ApiProperty("disabled", "Boolean", "false"),
                ApiProperty("variant", "DropdownMenuItemVariant", "Default"),
            ),
        ),
    )

val menubarDoc =
    ComponentDoc(
        id = "menubar",
        title = "Menubar",
        description = (
            "A visually persistent menu common in desktop applications " +
                "that provides quick access to a consistent set of commands."
        ),
        examplesHeading = null,
        usageImportsCode =
            """
            import androidx.compose.foundation.layout.*
            import androidx.compose.runtime.*
            import androidx.compose.ui.Alignment
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.platform.LocalLayoutDirection
            import androidx.compose.ui.text.TextStyle
            import androidx.compose.ui.unit.*
            import com.composables.icons.lucide.*
            import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuItemVariant
            import com.toutiaoyan.compose.ui.components.icon.Icon
            import com.toutiaoyan.compose.ui.components.menubar.*
            import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
            import com.toutiaoyan.compose.ui.components.text.Text
            import com.toutiaoyan.compose.ui.theme.currentTheme
            """.trimIndent(),
        usageCode =
            """
            Menubar {
                MenubarMenu(value = "file") {
                    MenubarTrigger("File")
                    MenubarContent {
                        MenubarItem("New Tab", onClick = {}, shortcut = "⌘T")
                        MenubarSeparator()
                        MenubarItem("Print", onClick = {})
                    }
                }
            }
            """.trimIndent(),
        compositionCode =
            """
            Menubar
            └── MenubarMenu
                ├── MenubarTrigger
                └── MenubarContent
                    ├── MenubarGroup
                    │   ├── MenubarItem
                    │   ├── MenubarCheckboxItem
                    │   └── MenubarRadioGroup
                    │       └── MenubarRadioItem
                    ├── MenubarSeparator
                    └── MenubarSub
                        ├── MenubarSubTrigger
                        └── MenubarSubContent
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        var option0 by remember { mutableStateOf(false) }
                        Menubar(modifier = Modifier.width(288.dp)) {
                            MenubarMenu(value = "menu1") {
                                MenubarTrigger("File")
                                MenubarContent() {
                                    MenubarGroup {
                                        MenubarItem("New Tab", onClick = {}, shortcut = "⌘T")
                                        MenubarItem("New Window", onClick = {}, shortcut = "⌘N")
                                        MenubarItem("New Incognito Window", onClick = {}, disabled = true)
                                    }
                                    MenubarSeparator()
                                    MenubarGroup {
                                        MenubarSub {
                                            MenubarSubTrigger("Share")
                                            MenubarSubContent {
                                                MenubarGroup {
                                                    MenubarItem("Email link", onClick = {})
                                                    MenubarItem("Messages", onClick = {})
                                                    MenubarItem("Notes", onClick = {})
                                                }
                                            }
                                        }
                                    }
                                    MenubarSeparator()
                                    MenubarGroup {
                                        MenubarItem("Print...", onClick = {}, shortcut = "⌘P")
                                    }
                                }
                            }
                            MenubarMenu(value = "menu2") {
                                MenubarTrigger("Edit")
                                MenubarContent() {
                                    MenubarGroup {
                                        MenubarItem("Undo", onClick = {}, shortcut = "⌘Z")
                                        MenubarItem("Redo", onClick = {}, shortcut = "⇧⌘Z")
                                    }
                                    MenubarSeparator()
                                    MenubarGroup {
                                        MenubarSub {
                                            MenubarSubTrigger("Find")
                                            MenubarSubContent {
                                                MenubarGroup {
                                                    MenubarItem("Search the web", onClick = {})
                                                }
                                                MenubarSeparator()
                                                MenubarGroup {
                                                    MenubarItem("Find...", onClick = {})
                                                    MenubarItem("Find Next", onClick = {})
                                                    MenubarItem("Find Previous", onClick = {})
                                                }
                                            }
                                        }
                                    }
                                    MenubarSeparator()
                                    MenubarGroup {
                                        MenubarItem("Cut", onClick = {})
                                        MenubarItem("Copy", onClick = {})
                                        MenubarItem("Paste", onClick = {})
                                    }
                                }
                            }
                            MenubarMenu(value = "menu3") {
                                MenubarTrigger("View")
                                MenubarContent(modifier = Modifier.width(176.dp)) {
                                    MenubarGroup {
                                        MenubarCheckboxItem("Bookmarks Bar", checked = option0) { option0 = it }
                                        MenubarCheckboxItem("Full URLs", checked = true) {}
                                    }
                                    MenubarSeparator()
                                    MenubarGroup {
                                        MenubarItem("Reload", onClick = {}, inset = true, shortcut = "⌘R")
                                        MenubarItem("Force Reload",
                                            onClick = {},
                                            disabled = true,
                                            inset = true,
                                            shortcut = "⇧⌘R")
                                    }
                                    MenubarSeparator()
                                    MenubarGroup {
                                        MenubarItem("Toggle Fullscreen", onClick = {}, inset = true)
                                    }
                                    MenubarSeparator()
                                    MenubarGroup {
                                        MenubarItem("Hide Sidebar", onClick = {}, inset = true)
                                    }
                                }
                            }
                            MenubarMenu(value = "menu4") {
                                MenubarTrigger("Profiles")
                                MenubarContent() {
                                    MenubarRadioGroup(value = "benoit", onValueChange = {}) {
                                        MenubarRadioItem(value = "andy", label = "Andy")
                                        MenubarRadioItem(value = "benoit", label = "Benoit")
                                        MenubarRadioItem(value = "Luis", label = "Luis")
                                    }
                                    MenubarSeparator()
                                    MenubarGroup {
                                        MenubarItem("Edit...", onClick = {}, inset = true)
                                    }
                                    MenubarSeparator()
                                    MenubarGroup {
                                        MenubarItem("Add Profile...", onClick = {}, inset = true)
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var option0 by remember { mutableStateOf(false) }
                        Menubar(modifier = Modifier.width(288.dp)) {
                            MenubarMenu(value = "menu1") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarGroup {
                                        MenubarItem("New Tab", onClick = {}, shortcut = "⌘T")
                                        MenubarItem("New Window", onClick = {}, shortcut = "⌘N")
                                        MenubarItem("New Incognito Window", onClick = {}, disabled = true)
                                    }
                                    MenubarSeparator()
                                    MenubarGroup {
                                        MenubarSub {
                                            MenubarSubTrigger("Share")
                                            MenubarSubContent {
                                                MenubarGroup {
                                                    MenubarItem("Email link", onClick = {})
                                                    MenubarItem("Messages", onClick = {})
                                                    MenubarItem("Notes", onClick = {})
                                                }
                                            }
                                        }
                                    }
                                    MenubarSeparator()
                                    MenubarGroup {
                                        MenubarItem("Print...", onClick = {}, shortcut = "⌘P")
                                    }
                                }
                            }
                            MenubarMenu(value = "menu2") {
                                MenubarTrigger("Edit")
                                MenubarContent {
                                    MenubarGroup {
                                        MenubarItem("Undo", onClick = {}, shortcut = "⌘Z")
                                        MenubarItem("Redo", onClick = {}, shortcut = "⇧⌘Z")
                                    }
                                    MenubarSeparator()
                                    MenubarGroup {
                                        MenubarSub {
                                            MenubarSubTrigger("Find")
                                            MenubarSubContent {
                                                MenubarGroup {
                                                    MenubarItem("Search the web", onClick = {})
                                                }
                                                MenubarSeparator()
                                                MenubarGroup {
                                                    MenubarItem("Find...", onClick = {})
                                                    MenubarItem("Find Next", onClick = {})
                                                    MenubarItem("Find Previous", onClick = {})
                                                }
                                            }
                                        }
                                    }
                                    MenubarSeparator()
                                    MenubarGroup {
                                        MenubarItem("Cut", onClick = {})
                                        MenubarItem("Copy", onClick = {})
                                        MenubarItem("Paste", onClick = {})
                                    }
                                }
                            }
                            MenubarMenu(value = "menu3") {
                                MenubarTrigger("View")
                                MenubarContent(modifier = Modifier.width(176.dp)) {
                                    MenubarGroup {
                                        MenubarCheckboxItem("Bookmarks Bar", checked = option0) { option0 = it }
                                        MenubarCheckboxItem("Full URLs", checked = true) {}
                                    }
                                    MenubarSeparator()
                                    MenubarGroup {
                                        MenubarItem("Reload", onClick = {}, inset = true, shortcut = "⌘R")
                                        MenubarItem(
                                            "Force Reload",
                                            onClick =
                                                {},
                                            disabled =
                                            true,
                                            inset =
                                            true,
                                            shortcut =
                                                "⇧⌘R",
                                        )
                                    }
                                    MenubarSeparator()
                                    MenubarGroup {
                                        MenubarItem("Toggle Fullscreen", onClick = {}, inset = true)
                                    }
                                    MenubarSeparator()
                                    MenubarGroup {
                                        MenubarItem("Hide Sidebar", onClick = {}, inset = true)
                                    }
                                }
                            }
                            MenubarMenu(value = "menu4") {
                                MenubarTrigger("Profiles")
                                MenubarContent {
                                    MenubarRadioGroup(value = "benoit", onValueChange = {}) {
                                        MenubarRadioItem(value = "andy", label = "Andy")
                                        MenubarRadioItem(value = "benoit", label = "Benoit")
                                        MenubarRadioItem(value = "Luis", label = "Luis")
                                    }
                                    MenubarSeparator()
                                    MenubarGroup {
                                        MenubarItem("Edit...", onClick = {}, inset = true)
                                    }
                                    MenubarSeparator()
                                    MenubarGroup {
                                        MenubarItem("Add Profile...", onClick = {}, inset = true)
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Checkbox",
                    code =
                        """
                        var option0 by remember { mutableStateOf(false) }
                        var option3 by remember { mutableStateOf(false) }
                        var option4 by remember { mutableStateOf(false) }
                        Menubar(modifier = Modifier.width(288.dp)) {
                            MenubarMenu(value = "menu1") {
                                MenubarTrigger("View")
                                MenubarContent(modifier = Modifier.width(256.dp)) {
                                    MenubarCheckboxItem("Always Show Bookmarks Bar", checked = option0) { option0 = it }
                                    MenubarCheckboxItem("Always Show Full URLs", checked = true) {}
                                    MenubarSeparator()
                                    MenubarItem("Reload", onClick = {}, inset = true, shortcut = "⌘R")
                                    MenubarItem("Force Reload",
                                        onClick = {},
                                        disabled = true,
                                        inset = true,
                                        shortcut = "⇧⌘R")
                                }
                            }
                            MenubarMenu(value = "menu2") {
                                MenubarTrigger("Format")
                                MenubarContent() {
                                    MenubarCheckboxItem("Strikethrough", checked = true) {}
                                    MenubarCheckboxItem("Code", checked = option3) { option3 = it }
                                    MenubarCheckboxItem("Superscript", checked = option4) { option4 = it }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var option0 by remember { mutableStateOf(false) }
                        var option3 by remember { mutableStateOf(false) }
                        var option4 by remember { mutableStateOf(false) }
                        Menubar(modifier = Modifier.width(288.dp)) {
                            MenubarMenu(value = "menu1") {
                                MenubarTrigger("View")
                                MenubarContent(modifier = Modifier.width(256.dp)) {
                                    MenubarCheckboxItem("Always Show Bookmarks Bar", checked = option0) { option0 = it }
                                    MenubarCheckboxItem("Always Show Full URLs", checked = true) {}
                                    MenubarSeparator()
                                    MenubarItem("Reload", onClick = {}, inset = true, shortcut = "⌘R")
                                    MenubarItem(
                                        "Force Reload",
                                        onClick = {},
                                        disabled = true,
                                        inset = true,
                                        shortcut = "⇧⌘R",
                                    )
                                }
                            }
                            MenubarMenu(value = "menu2") {
                                MenubarTrigger("Format")
                                MenubarContent {
                                    MenubarCheckboxItem("Strikethrough", checked = true) {}
                                    MenubarCheckboxItem("Code", checked = option3) { option3 = it }
                                    MenubarCheckboxItem("Superscript", checked = option4) { option4 = it }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Radio",
                    code =
                        """
                        var user by remember { mutableStateOf("benoit") }
                        var theme by remember { mutableStateOf("system") }
                        Menubar(modifier = Modifier.width(288.dp)) {
                            MenubarMenu(value = "menu1") {
                                MenubarTrigger("Profiles")
                                MenubarContent() {
                                    MenubarRadioGroup(value = user, onValueChange = { user = it }) {
                                        MenubarRadioItem(value = "andy", label = "Andy")
                                        MenubarRadioItem(value = "benoit", label = "Benoit")
                                        MenubarRadioItem(value = "luis", label = "Luis")
                                    }
                                    MenubarSeparator()
                                    MenubarItem("Edit...", onClick = {}, inset = true)
                                    MenubarItem("Add Profile...", onClick = {}, inset = true)
                                }
                            }
                            MenubarMenu(value = "menu2") {
                                MenubarTrigger("Theme")
                                MenubarContent() {
                                    MenubarRadioGroup(value = theme, onValueChange = { theme = it }) {
                                        MenubarRadioItem(value = "light", label = "Light")
                                        MenubarRadioItem(value = "dark", label = "Dark")
                                        MenubarRadioItem(value = "system", label = "System")
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var user by remember { mutableStateOf("benoit") }
                        var theme by remember { mutableStateOf("system") }
                        Menubar(modifier = Modifier.width(288.dp)) {
                            MenubarMenu(value = "menu1") {
                                MenubarTrigger("Profiles")
                                MenubarContent {
                                    MenubarRadioGroup(value = user, onValueChange = { user = it }) {
                                        MenubarRadioItem(value = "andy", label = "Andy")
                                        MenubarRadioItem(value = "benoit", label = "Benoit")
                                        MenubarRadioItem(value = "luis", label = "Luis")
                                    }
                                    MenubarSeparator()
                                    MenubarItem("Edit...", onClick = {}, inset = true)
                                    MenubarItem("Add Profile...", onClick = {}, inset = true)
                                }
                            }
                            MenubarMenu(value = "menu2") {
                                MenubarTrigger("Theme")
                                MenubarContent {
                                    MenubarRadioGroup(value = theme, onValueChange = { theme = it }) {
                                        MenubarRadioItem(value = "light", label = "Light")
                                        MenubarRadioItem(value = "dark", label = "Dark")
                                        MenubarRadioItem(value = "system", label = "System")
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Submenu",
                    code =
                        """
                        Menubar(modifier = Modifier.width(288.dp)) {
                            MenubarMenu(value = "menu1") {
                                MenubarTrigger("File")
                                MenubarContent() {
                                    MenubarSub {
                                        MenubarSubTrigger("Share")
                                        MenubarSubContent {
                                            MenubarItem("Email link", onClick = {})
                                            MenubarItem("Messages", onClick = {})
                                            MenubarItem("Notes", onClick = {})
                                        }
                                    }
                                    MenubarSeparator()
                                    MenubarItem("Print...", onClick = {}, shortcut = "⌘P")
                                }
                            }
                            MenubarMenu(value = "menu2") {
                                MenubarTrigger("Edit")
                                MenubarContent() {
                                    MenubarItem("Undo", onClick = {}, shortcut = "⌘Z")
                                    MenubarItem("Redo", onClick = {}, shortcut = "⇧⌘Z")
                                    MenubarSeparator()
                                    MenubarSub {
                                        MenubarSubTrigger("Find")
                                        MenubarSubContent {
                                            MenubarItem("Find...", onClick = {})
                                            MenubarItem("Find Next", onClick = {})
                                            MenubarItem("Find Previous", onClick = {})
                                        }
                                    }
                                    MenubarSeparator()
                                    MenubarItem("Cut", onClick = {})
                                    MenubarItem("Copy", onClick = {})
                                    MenubarItem("Paste", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar(modifier = Modifier.width(288.dp)) {
                            MenubarMenu(value = "menu1") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarSub {
                                        MenubarSubTrigger("Share")
                                        MenubarSubContent {
                                            MenubarItem("Email link", onClick = {})
                                            MenubarItem("Messages", onClick = {})
                                            MenubarItem("Notes", onClick = {})
                                        }
                                    }
                                    MenubarSeparator()
                                    MenubarItem("Print...", onClick = {}, shortcut = "⌘P")
                                }
                            }
                            MenubarMenu(value = "menu2") {
                                MenubarTrigger("Edit")
                                MenubarContent {
                                    MenubarItem("Undo", onClick = {}, shortcut = "⌘Z")
                                    MenubarItem("Redo", onClick = {}, shortcut = "⇧⌘Z")
                                    MenubarSeparator()
                                    MenubarSub {
                                        MenubarSubTrigger("Find")
                                        MenubarSubContent {
                                            MenubarItem("Find...", onClick = {})
                                            MenubarItem("Find Next", onClick = {})
                                            MenubarItem("Find Previous", onClick = {})
                                        }
                                    }
                                    MenubarSeparator()
                                    MenubarItem("Cut", onClick = {})
                                    MenubarItem("Copy", onClick = {})
                                    MenubarItem("Paste", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "With Icons",
                    code =
                        """
                        Menubar(modifier = Modifier.width(288.dp)) {
                            MenubarMenu(value = "menu1") {
                                MenubarTrigger("File")
                                MenubarContent() {
                                    MenubarItem("New File", onClick = {}) {
                                        Row(Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Icon(Lucide.File,
                                                modifier = Modifier.size(16.dp),
                                                tint = currentTheme.colors.popoverForeground,
                                                contentDescription = null)
                                            Text(modifier = Modifier.weight(1f),
                                                textStyle = TextStyle(fontSize = 14.sp,
                                                lineHeight = 20.sp,
                                                color = currentTheme.colors.popoverForeground)) { "New File" }
                                            MenubarShortcut("⌘N")
                                        }
                                    }
                                    MenubarItem("Open Folder", onClick = {}) {
                                        Row(Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Icon(Lucide.Folder,
                                                modifier = Modifier.size(16.dp),
                                                tint = currentTheme.colors.popoverForeground,
                                                contentDescription = null)
                                            Text(modifier = Modifier.weight(1f),
                                                textStyle = TextStyle(fontSize = 14.sp,
                                                lineHeight = 20.sp,
                                                color = currentTheme.colors.popoverForeground)) { "Open Folder" }
                                        }
                                    }
                                    MenubarSeparator()
                                    MenubarItem("Save", onClick = {}) {
                                        Row(Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Icon(Lucide.Save,
                                                modifier = Modifier.size(16.dp),
                                                tint = currentTheme.colors.popoverForeground,
                                                contentDescription = null)
                                            Text(modifier = Modifier.weight(1f),
                                                textStyle = TextStyle(fontSize = 14.sp,
                                                lineHeight = 20.sp,
                                                color = currentTheme.colors.popoverForeground)) { "Save" }
                                            MenubarShortcut("⌘S")
                                        }
                                    }
                                }
                            }
                            MenubarMenu(value = "menu2") {
                                MenubarTrigger("More")
                                MenubarContent() {
                                    MenubarGroup {
                                        MenubarItem("Settings", onClick = {}) {
                                            Row(Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Icon(Lucide.Settings,
                                                    modifier = Modifier.size(16.dp),
                                                    tint = currentTheme.colors.popoverForeground,
                                                    contentDescription = null)
                                                Text(modifier = Modifier.weight(1f),
                                                    textStyle = TextStyle(fontSize = 14.sp,
                                                    lineHeight = 20.sp,
                                                    color = currentTheme.colors.popoverForeground)) { "Settings" }
                                            }
                                        }
                                        MenubarItem("Help", onClick = {}) {
                                            Row(Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Icon(Lucide.CircleQuestionMark,
                                                    modifier = Modifier.size(16.dp),
                                                    tint = currentTheme.colors.popoverForeground,
                                                    contentDescription = null)
                                                Text(modifier = Modifier.weight(1f),
                                                    textStyle = TextStyle(fontSize = 14.sp,
                                                    lineHeight = 20.sp,
                                                    color = currentTheme.colors.popoverForeground)) { "Help" }
                                            }
                                        }
                                        MenubarSeparator()
                                        MenubarItem("Delete",
                                            onClick = {},
                                            variant = DropdownMenuItemVariant.Destructive) {
                                            Row(Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Icon(Lucide.Trash,
                                                    modifier = Modifier.size(16.dp),
                                                    tint = currentTheme.colors.destructive,
                                                    contentDescription = null)
                                                Text(modifier = Modifier.weight(1f),
                                                    textStyle = TextStyle(fontSize = 14.sp,
                                                    lineHeight = 20.sp,
                                                    color = currentTheme.colors.destructive)) { "Delete" }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar(modifier = Modifier.width(288.dp)) {
                            MenubarMenu(value = "menu1") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New File", onClick = {}) {
                                        Row(
                                            Modifier.fillMaxWidth(),
                                            verticalAlignment =
                                                Alignment.CenterVertically,
                                            horizontalArrangement =
                                                Arrangement.spacedBy(6.dp),
                                        ) {
                                            Icon(
                                                Lucide.File,
                                                modifier =
                                                    Modifier.size(16.dp),
                                                tint =
                                                    currentTheme.colors.popoverForeground,
                                                contentDescription =
                                                null,
                                            )
                                            Text(
                                                modifier =
                                                    Modifier.weight(1f),
                                                textStyle =
                                                    TextStyle(
                                                        fontSize =
                                                            14.sp,
                                                        lineHeight =
                                                            20.sp,
                                                        color =
                                                            currentTheme.colors.popoverForeground,
                                                    ),
                                            ) { "New File" }
                                            MenubarShortcut("⌘N")
                                        }
                                    }
                                    MenubarItem("Open Folder", onClick = {}) {
                                        Row(
                                            Modifier.fillMaxWidth(),
                                            verticalAlignment =
                                                Alignment.CenterVertically,
                                            horizontalArrangement =
                                                Arrangement.spacedBy(6.dp),
                                        ) {
                                            Icon(
                                                Lucide.Folder,
                                                modifier =
                                                    Modifier.size(16.dp),
                                                tint =
                                                    currentTheme.colors.popoverForeground,
                                                contentDescription =
                                                null,
                                            )
                                            Text(
                                                modifier =
                                                    Modifier.weight(1f),
                                                textStyle =
                                                    TextStyle(
                                                        fontSize =
                                                            14.sp,
                                                        lineHeight =
                                                            20.sp,
                                                        color =
                                                            currentTheme.colors.popoverForeground,
                                                    ),
                                            ) { "Open Folder" }
                                        }
                                    }
                                    MenubarSeparator()
                                    MenubarItem("Save", onClick = {}) {
                                        Row(
                                            Modifier.fillMaxWidth(),
                                            verticalAlignment =
                                                Alignment.CenterVertically,
                                            horizontalArrangement =
                                                Arrangement.spacedBy(6.dp),
                                        ) {
                                            Icon(
                                                Lucide.Save,
                                                modifier =
                                                    Modifier.size(16.dp),
                                                tint =
                                                    currentTheme.colors.popoverForeground,
                                                contentDescription =
                                                null,
                                            )
                                            Text(
                                                modifier =
                                                    Modifier.weight(1f),
                                                textStyle =
                                                    TextStyle(
                                                        fontSize =
                                                            14.sp,
                                                        lineHeight =
                                                            20.sp,
                                                        color =
                                                            currentTheme.colors.popoverForeground,
                                                    ),
                                            ) { "Save" }
                                            MenubarShortcut("⌘S")
                                        }
                                    }
                                }
                            }
                            MenubarMenu(value = "menu2") {
                                MenubarTrigger("More")
                                MenubarContent {
                                    MenubarGroup {
                                        MenubarItem("Settings", onClick = {}) {
                                            Row(
                                                Modifier.fillMaxWidth(),
                                                verticalAlignment =
                                                    Alignment.CenterVertically,
                                                horizontalArrangement =
                                                    Arrangement.spacedBy(6.dp),
                                            ) {
                                                Icon(
                                                    Lucide.Settings,
                                                    modifier =
                                                        Modifier.size(16.dp),
                                                    tint =
                                                        currentTheme.colors.popoverForeground,
                                                    contentDescription =
                                                    null,
                                                )
                                                Text(
                                                    modifier =
                                                        Modifier.weight(1f),
                                                    textStyle =
                                                        TextStyle(
                                                            fontSize =
                                                                14.sp,
                                                            lineHeight =
                                                                20.sp,
                                                            color =
                                                                currentTheme.colors.popoverForeground,
                                                        ),
                                                ) { "Settings" }
                                            }
                                        }
                                        MenubarItem("Help", onClick = {}) {
                                            Row(
                                                Modifier.fillMaxWidth(),
                                                verticalAlignment =
                                                    Alignment.CenterVertically,
                                                horizontalArrangement =
                                                    Arrangement.spacedBy(6.dp),
                                            ) {
                                                Icon(
                                                    Lucide.CircleQuestionMark,
                                                    modifier =
                                                        Modifier.size(16.dp),
                                                    tint =
                                                        currentTheme.colors.popoverForeground,
                                                    contentDescription =
                                                    null,
                                                )
                                                Text(
                                                    modifier =
                                                        Modifier.weight(1f),
                                                    textStyle =
                                                        TextStyle(
                                                            fontSize =
                                                                14.sp,
                                                            lineHeight =
                                                                20.sp,
                                                            color =
                                                                currentTheme.colors.popoverForeground,
                                                        ),
                                                ) { "Help" }
                                            }
                                        }
                                        MenubarSeparator()
                                        MenubarItem(
                                            "Delete",
                                            onClick = {},
                                            variant = DropdownMenuItemVariant.Destructive,
                                        ) {
                                            Row(
                                                Modifier.fillMaxWidth(),
                                                verticalAlignment =
                                                    Alignment.CenterVertically,
                                                horizontalArrangement =
                                                    Arrangement.spacedBy(6.dp),
                                            ) {
                                                Icon(
                                                    Lucide.Trash,
                                                    modifier =
                                                        Modifier.size(16.dp),
                                                    tint =
                                                        currentTheme.colors.destructive,
                                                    contentDescription =
                                                    null,
                                                )
                                                Text(
                                                    modifier =
                                                        Modifier.weight(1f),
                                                    textStyle =
                                                        TextStyle(
                                                            fontSize =
                                                                14.sp,
                                                            lineHeight =
                                                                20.sp,
                                                            color =
                                                                currentTheme.colors.destructive,
                                                        ),
                                                ) { "Delete" }
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
                    previewPadding = 0.dp,
                    minPreviewHeight = 304.dp,
                    code =
                        """
                        // Connect language to your language picker: 0 English, 1 Arabic, 2 Hebrew.
                        var language by remember { mutableStateOf(1) }
                        val translations = listOf(
                            mapOf(
                                "file" to "File",
                                "newTab" to "New Tab",
                                "newWindow" to "New Window",
                                "newIncognitoWindow" to "New Incognito Window",
                                "share" to "Share",
                                "emailLink" to "Email link",
                                "messages" to "Messages",
                                "notes" to "Notes",
                                "print" to "Print...",
                                "edit" to "Edit",
                                "undo" to "Undo",
                                "redo" to "Redo",
                                "find" to "Find",
                                "searchTheWeb" to "Search the web",
                                "findItem" to "Find...",
                                "findNext" to "Find Next",
                                "findPrevious" to "Find Previous",
                                "cut" to "Cut",
                                "copy" to "Copy",
                                "paste" to "Paste",
                                "view" to "View",
                                "bookmarksBar" to "Bookmarks Bar",
                                "fullUrls" to "Full URLs",
                                "reload" to "Reload",
                                "forceReload" to "Force Reload",
                                "toggleFullscreen" to "Toggle Fullscreen",
                                "hideSidebar" to "Hide Sidebar",
                                "profiles" to "Profiles",
                                "andy" to "Andy",
                                "benoit" to "Benoit",
                                "luis" to "Luis",
                                "editProfile" to "Edit...",
                                "addProfile" to "Add Profile..."
                            ),
                            mapOf(
                                "file" to "ملف",
                                "newTab" to "علامة تبويب جديدة",
                                "newWindow" to "نافذة جديدة",
                                "newIncognitoWindow" to "نافذة التصفح المتخفي الجديدة",
                                "share" to "مشاركة",
                                "emailLink" to "رابط البريد الإلكتروني",
                                "messages" to "الرسائل",
                                "notes" to "الملاحظات",
                                "print" to "طباعة...",
                                "edit" to "تعديل",
                                "undo" to "تراجع",
                                "redo" to "إعادة",
                                "find" to "بحث",
                                "searchTheWeb" to "البحث على الويب",
                                "findItem" to "بحث...",
                                "findNext" to "البحث التالي",
                                "findPrevious" to "البحث السابق",
                                "cut" to "قص",
                                "copy" to "نسخ",
                                "paste" to "لصق",
                                "view" to "عرض",
                                "bookmarksBar" to "شريط الإشارات المرجعية",
                                "fullUrls" to "عناوين URL الكاملة",
                                "reload" to "إعادة تحميل",
                                "forceReload" to "إعادة تحميل قسري",
                                "toggleFullscreen" to "تبديل وضع ملء الشاشة",
                                "hideSidebar" to "إخفاء الشريط الجانبي",
                                "profiles" to "الملفات الشخصية",
                                "andy" to "Andy",
                                "benoit" to "Benoit",
                                "luis" to "Luis",
                                "editProfile" to "تعديل...",
                                "addProfile" to "إضافة ملف شخصي..."
                            ),
                            mapOf(
                                "file" to "קובץ",
                                "newTab" to "כרטיסייה חדשה",
                                "newWindow" to "חלון חדש",
                                "newIncognitoWindow" to "חלון גלישה בסתר חדש",
                                "share" to "שתף",
                                "emailLink" to "קישור אימייל",
                                "messages" to "הודעות",
                                "notes" to "הערות",
                                "print" to "הדפס...",
                                "edit" to "ערוך",
                                "undo" to "בטל",
                                "redo" to "בצע שוב",
                                "find" to "מצא",
                                "searchTheWeb" to "חפש באינטרנט",
                                "findItem" to "מצא...",
                                "findNext" to "מצא הבא",
                                "findPrevious" to "מצא הקודם",
                                "cut" to "גזור",
                                "copy" to "העתק",
                                "paste" to "הדבק",
                                "view" to "תצוגה",
                                "bookmarksBar" to "סרגל סימניות",
                                "fullUrls" to "כתובות URL מלאות",
                                "reload" to "רענן",
                                "forceReload" to "רענן בכוח",
                                "toggleFullscreen" to "החלף מסך מלא",
                                "hideSidebar" to "הסתר סרגל צד",
                                "profiles" to "פרופילים",
                                "andy" to "Andy",
                                "benoit" to "Benoit",
                                "luis" to "Luis",
                                "editProfile" to "ערוך...",
                                "addProfile" to "הוסף פרופיל..."
                            )
                        )
                        val labels = translations[language]
                        var option0 by remember { mutableStateOf(false) }
                        var profile by remember { mutableStateOf("benoit") }
                        CompositionLocalProvider(LocalLayoutDirection provides
                            if (language == 0) LayoutDirection.Ltr else
                            LayoutDirection.Rtl) {
                            Menubar(modifier = Modifier.width(288.dp)) {
                                MenubarMenu(value = "menu1") {
                                    MenubarTrigger(labels.getValue("file"))
                                    MenubarContent(align = if (language == 0) PopupAlign.Start else PopupAlign.End) {
                                        MenubarGroup {
                                            MenubarItem(labels.getValue("newTab"), onClick = {}, shortcut = "⌘T")
                                            MenubarItem(labels.getValue("newWindow"), onClick = {}, shortcut = "⌘N")
                                            MenubarItem(labels.getValue("newIncognitoWindow"),
                                                onClick = {},
                                                disabled = true)
                                        }
                                        MenubarSeparator()
                                        MenubarGroup {
                                            MenubarSub {
                                                MenubarSubTrigger(labels.getValue("share"))
                                                MenubarSubContent {
                                                    MenubarGroup {
                                                        MenubarItem(labels.getValue("emailLink"), onClick = {})
                                                        MenubarItem(labels.getValue("messages"), onClick = {})
                                                        MenubarItem(labels.getValue("notes"), onClick = {})
                                                    }
                                                }
                                            }
                                        }
                                        MenubarSeparator()
                                        MenubarGroup {
                                            MenubarItem(labels.getValue("print"), onClick = {}, shortcut = "⌘P")
                                        }
                                    }
                                }
                                MenubarMenu(value = "menu2") {
                                    MenubarTrigger(labels.getValue("edit"))
                                    MenubarContent(align = if (language == 0) PopupAlign.Start else PopupAlign.End) {
                                        MenubarGroup {
                                            MenubarItem(labels.getValue("undo"), onClick = {}, shortcut = "⌘Z")
                                            MenubarItem(labels.getValue("redo"), onClick = {}, shortcut = "⇧⌘Z")
                                        }
                                        MenubarSeparator()
                                        MenubarGroup {
                                            MenubarSub {
                                                MenubarSubTrigger(labels.getValue("find"))
                                                MenubarSubContent {
                                                    MenubarGroup {
                                                        MenubarItem(labels.getValue("searchTheWeb"), onClick = {})
                                                    }
                                                    MenubarSeparator()
                                                    MenubarGroup {
                                                        MenubarItem(labels.getValue("findItem"), onClick = {})
                                                        MenubarItem(labels.getValue("findNext"), onClick = {})
                                                        MenubarItem(labels.getValue("findPrevious"), onClick = {})
                                                    }
                                                }
                                            }
                                        }
                                        MenubarSeparator()
                                        MenubarGroup {
                                            MenubarItem(labels.getValue("cut"), onClick = {})
                                            MenubarItem(labels.getValue("copy"), onClick = {})
                                            MenubarItem(labels.getValue("paste"), onClick = {})
                                        }
                                    }
                                }
                                MenubarMenu(value = "menu3") {
                                    MenubarTrigger(labels.getValue("view"))
                                    MenubarContent(modifier = Modifier.width(176.dp),
                                        align = if (language == 0) PopupAlign.Start else PopupAlign.End) {
                                        MenubarGroup {
                                            MenubarCheckboxItem(labels.getValue("bookmarksBar"),
                                                checked = option0) { option0 = it }
                                            MenubarCheckboxItem(labels.getValue("fullUrls"),
                                                checked = true) {}
                                        }
                                        MenubarSeparator()
                                        MenubarGroup {
                                            MenubarItem(labels.getValue("reload"),
                                                onClick = {},
                                                inset = true,
                                                shortcut = "⌘R")
                                            MenubarItem(labels.getValue("forceReload"),
                                                onClick = {},
                                                disabled = true,
                                                inset = true,
                                                shortcut = "⇧⌘R")
                                        }
                                        MenubarSeparator()
                                        MenubarGroup {
                                            MenubarItem(labels.getValue("toggleFullscreen"), onClick = {}, inset = true)
                                        }
                                        MenubarSeparator()
                                        MenubarGroup {
                                            MenubarItem(labels.getValue("hideSidebar"), onClick = {}, inset = true)
                                        }
                                    }
                                }
                                MenubarMenu(value = "menu4") {
                                    MenubarTrigger(labels.getValue("profiles"))
                                    MenubarContent(align = if (language == 0) PopupAlign.Start else PopupAlign.End) {
                                        MenubarRadioGroup(value = profile, onValueChange = { profile = it }) {
                                            MenubarRadioItem(value = "andy", label = labels.getValue("andy"))
                                            MenubarRadioItem(value = "benoit", label = labels.getValue("benoit"))
                                            MenubarRadioItem(value = "Luis", label = labels.getValue("luis"))
                                        }
                                        MenubarSeparator()
                                        MenubarGroup {
                                            MenubarItem(labels.getValue("editProfile"), onClick = {}, inset = true)
                                        }
                                        MenubarSeparator()
                                        MenubarGroup {
                                            MenubarItem(labels.getValue("addProfile"), onClick = {}, inset = true)
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var language by remember { mutableStateOf(1) }
                        val labels = menubarTranslations[language]
                        var option0 by remember { mutableStateOf(false) }
                        var profile by remember { mutableStateOf("benoit") }
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
                                    Menubar(modifier = Modifier.width(288.dp)) {
                                        MenubarMenu(value = "menu1") {
                                            MenubarTrigger(labels.getValue("file"))
                                            MenubarContent(
                                                align = if (language == 0) PopupAlign.Start else PopupAlign.End,
                                            ) {
                                                MenubarGroup {
                                                    MenubarItem(
                                                        labels.getValue("newTab"),
                                                        onClick = {},
                                                        shortcut = "⌘T",
                                                    )
                                                    MenubarItem(
                                                        labels.getValue("newWindow"),
                                                        onClick = {},
                                                        shortcut = "⌘N",
                                                    )
                                                    MenubarItem(
                                                        labels.getValue("newIncognitoWindow"),
                                                        onClick =
                                                            {},
                                                        disabled =
                                                        true,
                                                    )
                                                }
                                                MenubarSeparator()
                                                MenubarGroup {
                                                    MenubarSub {
                                                        MenubarSubTrigger(labels.getValue("share"))
                                                        MenubarSubContent {
                                                            MenubarGroup {
                                                                MenubarItem(labels.getValue("emailLink"), onClick = {})
                                                                MenubarItem(labels.getValue("messages"), onClick = {})
                                                                MenubarItem(labels.getValue("notes"), onClick = {})
                                                            }
                                                        }
                                                    }
                                                }
                                                MenubarSeparator()
                                                MenubarGroup {
                                                    MenubarItem(labels.getValue("print"), onClick = {}, shortcut = "⌘P")
                                                }
                                            }
                                        }
                                        MenubarMenu(value = "menu2") {
                                            MenubarTrigger(labels.getValue("edit"))
                                            MenubarContent(
                                                align = if (language == 0) PopupAlign.Start else PopupAlign.End,
                                            ) {
                                                MenubarGroup {
                                                    MenubarItem(labels.getValue("undo"), onClick = {}, shortcut = "⌘Z")
                                                    MenubarItem(labels.getValue("redo"), onClick = {}, shortcut = "⇧⌘Z")
                                                }
                                                MenubarSeparator()
                                                MenubarGroup {
                                                    MenubarSub {
                                                        MenubarSubTrigger(labels.getValue("find"))
                                                        MenubarSubContent {
                                                            MenubarGroup {
                                                                MenubarItem(
                                                                    labels.getValue("searchTheWeb"),
                                                                    onClick = {},
                                                                )
                                                            }
                                                            MenubarSeparator()
                                                            MenubarGroup {
                                                                MenubarItem(labels.getValue("findItem"), onClick = {})
                                                                MenubarItem(labels.getValue("findNext"), onClick = {})
                                                                MenubarItem(
                                                                    labels.getValue("findPrevious"),
                                                                    onClick = {},
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                                MenubarSeparator()
                                                MenubarGroup {
                                                    MenubarItem(labels.getValue("cut"), onClick = {})
                                                    MenubarItem(labels.getValue("copy"), onClick = {})
                                                    MenubarItem(labels.getValue("paste"), onClick = {})
                                                }
                                            }
                                        }
                                        MenubarMenu(value = "menu3") {
                                            MenubarTrigger(labels.getValue("view"))
                                            MenubarContent(
                                                modifier =
                                                    Modifier.width(176.dp),
                                                align =
                                                    if (language == 0) {
                                                        PopupAlign.Start
                                                    } else {
                                                        PopupAlign.End
                                                    },
                                            ) {
                                                MenubarGroup {
                                                    MenubarCheckboxItem(
                                                        labels.getValue("bookmarksBar"),
                                                        checked =
                                                        option0,
                                                    ) {
                                                        option0 =
                                                            it
                                                    }
                                                    MenubarCheckboxItem(
                                                        labels.getValue("fullUrls"),
                                                        checked = true,
                                                    ) {}
                                                }
                                                MenubarSeparator()
                                                MenubarGroup {
                                                    MenubarItem(
                                                        labels.getValue("reload"),
                                                        onClick =
                                                            {},
                                                        inset =
                                                        true,
                                                        shortcut =
                                                            "⌘R",
                                                    )
                                                    MenubarItem(
                                                        labels.getValue("forceReload"),
                                                        onClick =
                                                            {},
                                                        disabled =
                                                        true,
                                                        inset =
                                                        true,
                                                        shortcut =
                                                            "⇧⌘R",
                                                    )
                                                }
                                                MenubarSeparator()
                                                MenubarGroup {
                                                    MenubarItem(
                                                        labels.getValue("toggleFullscreen"),
                                                        onClick = {},
                                                        inset = true,
                                                    )
                                                }
                                                MenubarSeparator()
                                                MenubarGroup {
                                                    MenubarItem(
                                                        labels.getValue("hideSidebar"),
                                                        onClick = {},
                                                        inset = true,
                                                    )
                                                }
                                            }
                                        }
                                        MenubarMenu(value = "menu4") {
                                            MenubarTrigger(labels.getValue("profiles"))
                                            MenubarContent(
                                                align = if (language == 0) PopupAlign.Start else PopupAlign.End,
                                            ) {
                                                MenubarRadioGroup(value = profile, onValueChange = { profile = it }) {
                                                    MenubarRadioItem(value = "andy", label = labels.getValue("andy"))
                                                    MenubarRadioItem(
                                                        value = "benoit",
                                                        label = labels.getValue("benoit"),
                                                    )
                                                    MenubarRadioItem(value = "Luis", label = labels.getValue("luis"))
                                                }
                                                MenubarSeparator()
                                                MenubarGroup {
                                                    MenubarItem(
                                                        labels.getValue("editProfile"),
                                                        onClick = {},
                                                        inset = true,
                                                    )
                                                }
                                                MenubarSeparator()
                                                MenubarGroup {
                                                    MenubarItem(
                                                        labels.getValue("addProfile"),
                                                        onClick = {},
                                                        inset = true,
                                                    )
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
                    title = "Checkboxes and radio",
                    code =
                        """
                        var showStatus by remember { mutableStateOf(true) }
                        var panel by remember { mutableStateOf("bottom") }
                        Menubar {
                            MenubarMenu(value = "view") {
                                MenubarTrigger("View")
                                MenubarContent {
                                    MenubarCheckboxItem("Status Bar", checked = showStatus) {
                                        showStatus = it
                                    }
                                    MenubarSeparator()
                                    MenubarRadioGroup(value = panel, onValueChange = { panel = it }) {
                                        MenubarRadioItem(value = "top", label = "Top")
                                        MenubarRadioItem(value = "bottom", label = "Bottom")
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var showStatus by remember { mutableStateOf(true) }
                        var panel by remember { mutableStateOf("bottom") }
                        Menubar {
                            MenubarMenu(value = "view") {
                                MenubarTrigger("View")
                                MenubarContent {
                                    MenubarCheckboxItem("Status Bar", checked = showStatus) {
                                        showStatus = it
                                    }
                                    MenubarSeparator()
                                    MenubarRadioGroup(value = panel, onValueChange = { panel = it }) {
                                        MenubarRadioItem(value = "top", label = "Top")
                                        MenubarRadioItem(value = "bottom", label = "Bottom")
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Shortcuts",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {}, shortcut = "⌘T")
                                    MenubarItem("New Window", onClick = {}, shortcut = "⌘N")
                                    MenubarItem("Print", onClick = {}, shortcut = "⌘P")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {}, shortcut = "⌘T")
                                    MenubarItem("New Window", onClick = {}, shortcut = "⌘N")
                                    MenubarItem("Print", onClick = {}, shortcut = "⌘P")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {}, shortcut = "⌘T")
                                    MenubarItem("New Window", onClick = {}, disabled = true)
                                    MenubarItem("Print", onClick = {}, shortcut = "⌘P")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {}, shortcut = "⌘T")
                                    MenubarItem("New Window", onClick = {}, disabled = true)
                                    MenubarItem("Print", onClick = {}, shortcut = "⌘P")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {}, shortcut = "⌘T")
                                    MenubarItem("New Window", onClick = {}, disabled = false)
                                    MenubarItem("Print", onClick = {}, shortcut = "⌘P")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {}, shortcut = "⌘T")
                                    MenubarItem("New Window", onClick = {}, disabled = false)
                                    MenubarItem("Print", onClick = {}, shortcut = "⌘P")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default destructive",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {}, destructive = false)
                                    MenubarItem("Print", onClick = {}, destructive = false)
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {}, destructive = false)
                                    MenubarItem("Print", onClick = {}, destructive = false)
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default variant",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {}, variant = DropdownMenuItemVariant.Default)
                                    MenubarItem("Print", onClick = {}, variant = DropdownMenuItemVariant.Default)
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {}, variant = DropdownMenuItemVariant.Default)
                                    MenubarItem("Print", onClick = {}, variant = DropdownMenuItemVariant.Default)
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Inset",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "view") {
                                MenubarTrigger("View")
                                MenubarContent {
                                    MenubarLabel("Appearance", inset = true)
                                    MenubarSeparator()
                                    MenubarItem("Reset", onClick = {}, inset = true)
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "view") {
                                MenubarTrigger("View")
                                MenubarContent {
                                    MenubarLabel("Appearance", inset = true)
                                    MenubarSeparator()
                                    MenubarItem("Reset", onClick = {}, inset = true)
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default inset",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "view") {
                                MenubarTrigger("View")
                                MenubarContent {
                                    MenubarLabel("Appearance", inset = false)
                                    MenubarItem("Reset", onClick = {}, inset = false)
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "view") {
                                MenubarTrigger("View")
                                MenubarContent {
                                    MenubarLabel("Appearance", inset = false)
                                    MenubarItem("Reset", onClick = {}, inset = false)
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Non-modal",
                    code =
                        """
                        Menubar(modal = false) {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                    MenubarItem("New Window", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar(modal = false) {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                    MenubarItem("New Window", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Modal",
                    code =
                        """
                        Menubar(modal = true) {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar(modal = true) {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default modal",
                    code =
                        """
                        Menubar(modal = true) {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar(modal = true) {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Controlled",
                    code =
                        """
                        var open by remember { mutableStateOf<String?>(null) }
                        Menubar(value = open, onValueChange = { open = it }) {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                    MenubarItem("New Window", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf<String?>(null) }
                        Menubar(value = open, onValueChange = { open = it }) {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                    MenubarItem("New Window", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Undismissible",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent(dismissOnClickOutside = false) {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent(dismissOnClickOutside = false) {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default dismiss",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent(dismissOnClickOutside = true) {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent(dismissOnClickOutside = true) {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Keep open on select",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("Stay open", onClick = {}, onSelect = { true })
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("Stay open", onClick = {}, onSelect = { true })
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Accessible label",
                    code =
                        """
                        Menubar(ariaLabel = "Application") {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar(ariaLabel = "Application") {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default aria label",
                    code =
                        """
                        Menubar(ariaLabel = null) {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar(ariaLabel = null) {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Custom text value",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("sm", onClick = {}, textValue = "Small")
                                    MenubarItem("md", onClick = {}, textValue = "Medium")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("sm", onClick = {}, textValue = "Small")
                                    MenubarItem("md", onClick = {}, textValue = "Medium")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Portal",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarPortal {
                                    MenubarContent {
                                        MenubarItem("New Tab", onClick = {})
                                        MenubarItem("New Window", onClick = {})
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarPortal {
                                    MenubarContent {
                                        MenubarItem("New Tab", onClick = {})
                                        MenubarItem("New Window", onClick = {})
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Group heading",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarGroup(heading = "File") {
                                        MenubarItem("New Tab", onClick = {})
                                        MenubarItem("New Window", onClick = {})
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarGroup(heading = "File") {
                                        MenubarItem("New Tab", onClick = {})
                                        MenubarItem("New Window", onClick = {})
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Label",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarLabel("File")
                                    MenubarItem("New Tab", onClick = {})
                                    MenubarItem("New Window", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarLabel("File")
                                    MenubarItem("New Tab", onClick = {})
                                    MenubarItem("New Window", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Separator",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                    MenubarSeparator()
                                    MenubarItem("New Window", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                    MenubarSeparator()
                                    MenubarItem("New Window", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Shortcut",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("Print", onClick = {}, shortcut = "⌘P")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("Print", onClick = {}, shortcut = "⌘P")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Menu",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Trigger",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Content",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                    MenubarItem("New Window", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                    MenubarItem("New Window", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Side offset",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent(sideOffset = 8.dp) {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent(sideOffset = 8.dp) {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default side offset",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent(sideOffset = 8.dp) {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent(sideOffset = 8.dp) {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Group",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarGroup {
                                        MenubarItem("New Tab", onClick = {})
                                        MenubarItem("New Window", onClick = {})
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarGroup {
                                        MenubarItem("New Tab", onClick = {})
                                        MenubarItem("New Window", onClick = {})
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Item",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarItem("New Tab", onClick = {})
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Checkbox item",
                    code =
                        """
                        var showStatus by remember { mutableStateOf(true) }
                        Menubar {
                            MenubarMenu(value = "view") {
                                MenubarTrigger("View")
                                MenubarContent {
                                    MenubarCheckboxItem("Status Bar", checked = showStatus) {
                                        showStatus = it
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var showStatus by remember { mutableStateOf(true) }
                        Menubar {
                            MenubarMenu(value = "view") {
                                MenubarTrigger("View")
                                MenubarContent {
                                    MenubarCheckboxItem("Status Bar", checked = showStatus) {
                                        showStatus = it
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default indeterminate",
                    code =
                        """
                        var showStatus by remember { mutableStateOf(true) }
                        Menubar {
                            MenubarMenu(value = "view") {
                                MenubarTrigger("View")
                                MenubarContent {
                                    MenubarCheckboxItem(
                                        "Status Bar",
                                        checked = showStatus,
                                        indeterminate = false,
                                    ) {
                                        showStatus = it
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var showStatus by remember { mutableStateOf(true) }
                        Menubar {
                            MenubarMenu(value = "view") {
                                MenubarTrigger("View")
                                MenubarContent {
                                    MenubarCheckboxItem(
                                        "Status Bar",
                                        checked = showStatus,
                                        indeterminate = false,
                                    ) {
                                        showStatus = it
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Radio group",
                    code =
                        """
                        var panel by remember { mutableStateOf("bottom") }
                        Menubar {
                            MenubarMenu(value = "view") {
                                MenubarTrigger("View")
                                MenubarContent {
                                    MenubarRadioGroup(value = panel, onValueChange = { panel = it }) {
                                        MenubarRadioItem(value = "top", label = "Top")
                                        MenubarRadioItem(value = "bottom", label = "Bottom")
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var panel by remember { mutableStateOf("bottom") }
                        Menubar {
                            MenubarMenu(value = "view") {
                                MenubarTrigger("View")
                                MenubarContent {
                                    MenubarRadioGroup(value = panel, onValueChange = { panel = it }) {
                                        MenubarRadioItem(value = "top", label = "Top")
                                        MenubarRadioItem(value = "bottom", label = "Bottom")
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Radio item",
                    code =
                        """
                        var panel by remember { mutableStateOf("bottom") }
                        Menubar {
                            MenubarMenu(value = "view") {
                                MenubarTrigger("View")
                                MenubarContent {
                                    MenubarRadioGroup(value = panel, onValueChange = { panel = it }) {
                                        MenubarRadioItem(value = "bottom", label = "Bottom")
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var panel by remember { mutableStateOf("bottom") }
                        Menubar {
                            MenubarMenu(value = "view") {
                                MenubarTrigger("View")
                                MenubarContent {
                                    MenubarRadioGroup(value = panel, onValueChange = { panel = it }) {
                                        MenubarRadioItem(value = "bottom", label = "Bottom")
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Sub",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarSub {
                                        MenubarSubTrigger("Share")
                                        MenubarSubContent {
                                            MenubarItem("Email", onClick = {})
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarSub {
                                        MenubarSubTrigger("Share")
                                        MenubarSubContent {
                                            MenubarItem("Email", onClick = {})
                                        }
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Sub trigger",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarSub {
                                        MenubarSubTrigger("Share")
                                        MenubarSubContent {
                                            MenubarItem("Email", onClick = {})
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarSub {
                                        MenubarSubTrigger("Share")
                                        MenubarSubContent {
                                            MenubarItem("Email", onClick = {})
                                        }
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Sub content",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarSub {
                                        MenubarSubTrigger("Share")
                                        MenubarSubContent {
                                            MenubarItem("Email", onClick = {})
                                            MenubarItem("Messages", onClick = {})
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarSub {
                                        MenubarSubTrigger("Share")
                                        MenubarSubContent {
                                            MenubarItem("Email", onClick = {})
                                            MenubarItem("Messages", onClick = {})
                                        }
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Sub side offset",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarSub {
                                        MenubarSubTrigger("Share")
                                        MenubarSubContent(sideOffset = 0.dp) {
                                            MenubarItem("Email", onClick = {})
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarSub {
                                        MenubarSubTrigger("Share")
                                        MenubarSubContent(sideOffset = 0.dp) {
                                            MenubarItem("Email", onClick = {})
                                        }
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default sub side offset",
                    code =
                        """
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarSub {
                                        MenubarSubTrigger("Share")
                                        MenubarSubContent(sideOffset = 0.dp) {
                                            MenubarItem("Email", onClick = {})
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Menubar {
                            MenubarMenu(value = "file") {
                                MenubarTrigger("File")
                                MenubarContent {
                                    MenubarSub {
                                        MenubarSubTrigger("Share")
                                        MenubarSubContent(sideOffset = 0.dp) {
                                            MenubarItem("Email", onClick = {})
                                        }
                                    }
                                }
                            }
                        }
                    },
                ),
            ).map(::menubarPageExample),
        apiSections = menubarApiSections,
    )
