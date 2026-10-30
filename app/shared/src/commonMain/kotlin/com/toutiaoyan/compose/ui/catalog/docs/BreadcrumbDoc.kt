@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.then
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.Dot
import com.composables.icons.lucide.Lucide
import com.toutiaoyan.compose.ui.catalog.LocalDocNavigation
import com.toutiaoyan.compose.ui.components.breadcrumb.*
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.button.rememberStyle
import com.toutiaoyan.compose.ui.components.dropdownmenu.*
import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.currentTheme

private val crumbComposition =
    """
    Breadcrumb
    └── BreadcrumbList
        ├── BreadcrumbItem
        │   └── BreadcrumbLink
        ├── BreadcrumbSeparator
        ├── BreadcrumbItem
        │   └── BreadcrumbLink
        ├── BreadcrumbSeparator
        └── BreadcrumbItem
            └── BreadcrumbPage
    """.trimIndent()

private fun crumbMenuCode(ellipsis: Boolean) =
    """
    BreadcrumbItem {
        DropdownMenu {
            DropdownMenuTrigger(
                modifier = Modifier.semantics { contentDescription = "${if (ellipsis) "Toggle menu" else "Components"}" },
                style = ${if (ellipsis) "ButtonVariant.Ghost.rememberStyle() then ButtonSize.IconSm.rememberStyle()" else "Style { contentColor(colors.mutedForeground) }"},
            ) {
                ${if (ellipsis) "Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { BreadcrumbEllipsis() }" else "Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {\n                    Text { \"Components\" }\n                    Image(Lucide.ChevronDown, null, Modifier.size(14.dp), colorFilter = ColorFilter.tint(currentTheme.colors.mutedForeground))\n                }"}
            }
            DropdownMenuContent(align = PopupAlign.Start) {
                DropdownMenuItem("Documentation", onClick = {})
                DropdownMenuItem("Themes", onClick = {})
                DropdownMenuItem("GitHub", onClick = {})
            }
        }
    }
    """.trimIndent()

private fun crumbRecipe(kind: String): String {
    val linkFragment = if (kind == "Link component") "link-component" else ""
    val dot =
        if (kind in listOf("Custom separator", "Dropdown", "RTL")) {
            (
                " { Image(Lucide.Dot, null, Modifier.size(14.dp), colorFilter " +
                    "= ColorFilter.tint(currentTheme.colors.mutedForeground)) }"
            )
        } else {
            ""
        }
    val separator = "BreadcrumbSeparator()$dot"
    val middle =
        when (kind) {
            "Default" ->
                crumbMenuCode(true) + (
                    "\n$separator\nBreadcrumbItem(\"Components\", onClick = { " +
                        "navigation?.navigate(\"\") })"
                )
            "Collapsed" -> (
                "BreadcrumbItem { BreadcrumbEllipsis() " +
                    "}\n$separator\nBreadcrumbItem(\"Components\", onClick = { " +
                    "navigation?.navigate(\"\") })"
            )
            "Dropdown", "RTL" -> crumbMenuCode(false)
            else
            -> "BreadcrumbItem(\"Components\", onClick = { navigation?.navigate(\"$linkFragment\") })"
        }
    val recipe =
        (
            "val navigation = LocalDocNavigation.current\nval uri = " +
                "LocalUriHandler.current\nval colors = currentTheme.colors\n"
        ) +
            """
            Breadcrumb {
                BreadcrumbList {
                    BreadcrumbItem("Home", onClick = { navigation?.navigate("$linkFragment") })
                    $separator
            """.trimIndent() + "\n" +
            middle.prependIndent("        ") +
            "\n        $separator\n        BreadcrumbItem(\"Breadcrumb\")\n    }\n}"
    var result = recipe
    if (kind in listOf("Custom separator", "Dropdown", "RTL")) {
        result =
            result.replace(
                (
                    "BreadcrumbItem(\"Home\", onClick = { " +
                        "navigation?.navigate(\"\") })"
                ),
                "BreadcrumbItem(\"Home\", onClick = { uri.openUri(\"/\") })",
            )
    }
    if (kind in listOf("Custom separator", "Collapsed")) {
        result =
            result.replace(
                (
                    "BreadcrumbItem(\"Components\", onClick = { " +
                        "navigation?.navigate(\"\") })"
                ),
                (
                    "BreadcrumbItem(\"Components\", onClick = { " +
                        "uri.openUri(\"/components\") })"
                ),
            )
    }
    return result
}

val breadcrumbDoc =
    ComponentDoc(
        id = "breadcrumb",
        title = "Breadcrumb",
        description = "Displays the path to the current resource using a hierarchy of links.",
        usageImportsCode = "import com.toutiaoyan.compose.ui.components.breadcrumb.*",
        usageCode =
            """
            Breadcrumb {
                BreadcrumbList {
                    BreadcrumbItem("Home", onClick = { /* Navigate home */ })
                    BreadcrumbSeparator()
                    BreadcrumbItem("Components", onClick = { /* Navigate to components */ })
                    BreadcrumbSeparator()
                    BreadcrumbItem("Breadcrumb")
                }
            }
            """.trimIndent(),
        compositionCode = crumbComposition,
        examplesHeading = null,
        examples =
            listOf(
                ComponentExample(
                    "Default",
                    crumbRecipe("Default"),
                    previewPadding = 8.dp,
                    preview = { BreadcrumbExample("Default") },
                ),
                ComponentExample(
                    "Basic",
                    crumbRecipe(
                        "Basic",
                    ),
                    descriptionMarkdown = "A basic breadcrumb with a home link and a components link.",
                    preview = {
                        BreadcrumbExample("Basic")
                    },
                ),
                ComponentExample(
                    "Custom separator",
                    crumbRecipe(
                        "Custom separator",
                    ),
                    descriptionMarkdown = "Pass custom content to BreadcrumbSeparator to replace the default chevron.",
                    preview = {
                        BreadcrumbExample("Custom separator")
                    },
                ),
                ComponentExample(
                    "Dropdown",
                    crumbRecipe(
                        "Dropdown",
                    ),
                    descriptionMarkdown = "Compose BreadcrumbItem with DropdownMenu to show additional paths.",
                    preview = {
                        BreadcrumbExample("Dropdown")
                    },
                ),
                ComponentExample(
                    "Collapsed",
                    crumbRecipe(
                        "Collapsed",
                    ),
                    previewPadding = 8.dp,
                    descriptionMarkdown = (
                        "BreadcrumbEllipsis shows a collapsed run of crumbs. It is " +
                            "presentational; use a menu trigger to make it interactive."
                    ),
                    preview = {
                        BreadcrumbExample("Collapsed")
                    },
                ),
                ComponentExample(
                    "Link component",
                    crumbRecipe(
                        "Link component",
                    ),
                    descriptionMarkdown = (
                        "Keep routing in the caller's onClick handler, or place your " +
                            "own router link inside BreadcrumbItem."
                    ),
                    preview = {
                        BreadcrumbExample("Link component")
                    },
                ),
                ComponentExample(
                    "RTL",
                    """
                    var language by remember { mutableStateOf(0) }
                    RtlPreview(language, listOf("Arabic (العربية)", "English", "Hebrew (עברית)"), { language = it }) {
                        CompositionLocalProvider(LocalLayoutDirection provides if (language == 1) LayoutDirection.Ltr else LayoutDirection.Rtl) {
                            // Translate each label and menu item for the selected language.
                    """.trimIndent() + "\n" +
                        (
                            """
                            val labels = when(language) {
                                0 -> listOf("الرئيسية", "المكونات", "مسار التنقل", "التوثيق", "السمات", "جيت هاب")
                                2 -> listOf("בית", "רכיבים", "פירורי לחם", "תיעוד", "ערכות נושא", "גיטהאב")
                                else -> listOf("Home", "Components", "Breadcrumb", "Documentation", "Themes", "GitHub")
                            }
                            """.trimIndent() + "\n" +
                                crumbRecipe("RTL")
                                    .replace(
                                        "\"Home\"",
                                        "labels[0]",
                                    ).replace(
                                        "\"Components\"",
                                        "labels[1]",
                                    ).replace(
                                        "\"Breadcrumb\"",
                                        "labels[2]",
                                    )
                                    .replace(
                                        "\"Documentation\"",
                                        "labels[3]",
                                    ).replace(
                                        "\"Themes\"",
                                        "labels[4]",
                                    ).replace(
                                        "\"GitHub\"",
                                        "labels[5]",
                                    )
                                    .replace(
                                        "align = PopupAlign.Start",
                                        (
                                            "align = if (LocalLayoutDirection.current == " +
                                                "LayoutDirection.Rtl) PopupAlign.End else PopupAlign.Start"
                                        ),
                                    )
                        ).prependIndent("        ") + "\n    }\n}",
                    previewPadding = 0.dp,
                    descriptionMarkdown = (
                        "Switch Arabic, English and Hebrew to compare translated links " +
                            "and menu alignment."
                    ),
                    preview = {
                        var language by remember { mutableStateOf(0) }
                        RtlPreview(
                            language,
                            listOf("Arabic (العربية)", "English", "Hebrew (עברית)"),
                            { language = it },
                        ) {
                            CompositionLocalProvider(
                                LocalLayoutDirection provides
                                    if (language == 1) {
                                        LayoutDirection.Ltr
                                    } else {
                                        LayoutDirection.Rtl
                                    },
                            ) {
                                Box(
                                    Modifier.fillMaxWidth().heightIn(min = 288.dp).padding(8.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    BreadcrumbExample("RTL", language)
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    "Disabled",
                    """
                    Breadcrumb {
                        BreadcrumbList {
                            BreadcrumbItem("Home", onClick = {}, disabled = true)
                            BreadcrumbSeparator()
                            BreadcrumbItem("Breadcrumb")
                        }
                    }
                    """.trimIndent(),
                    preview = {
                        Breadcrumb {
                            BreadcrumbList {
                                BreadcrumbItem("Home", onClick = {}, disabled = true)
                                BreadcrumbSeparator()
                                BreadcrumbItem("Breadcrumb")
                            }
                        }
                    },
                ),
            ),
        apiSections =
            listOf(
                ComponentApiSection(
                    "Breadcrumb",
                    "Navigation container. Use ariaLabel to name the breadcrumb. Routing remains caller-owned.",
                    listOf(
                        ApiProperty("ariaLabel", "String", "breadcrumb"),
                        ApiProperty("content", "@Composable RowScope.() -> Unit", "Required"),
                    ),
                ),
                ComponentApiSection(
                    "BreadcrumbList",
                    "Wrapping list with 6dp horizontal and vertical gap. Crumbs remain atomic.",
                    listOf(ApiProperty("modifier", "Modifier", "Modifier"), ApiProperty("style", "Style", "Style")),
                ),
                ComponentApiSection(
                    "BreadcrumbItem",
                    (
                        "Inline row with 4dp gap for caller-provided icons or links. " +
                            "Text overload renders a link when onClick is supplied, " +
                            "otherwise the current page."
                    ),
                    listOf(
                        ApiProperty("onClick", "(() -> Unit)?", "null"),
                        ApiProperty("disabled", "Boolean", "false"),
                    ),
                ),
                ComponentApiSection(
                    "BreadcrumbLink",
                    "Muted, clickable link with foreground hover. Use onClick to integrate your router.",
                    listOf(
                        ApiProperty("text", "String", "Required"),
                        ApiProperty("onClick", "() -> Unit", "Required"),
                        ApiProperty("disabled", "Boolean", "false"),
                    ),
                ),
                ComponentApiSection(
                    "BreadcrumbPage",
                    "Selected, non-clickable current page.",
                    listOf(ApiProperty("text", "String", "Required")),
                ),
                ComponentApiSection(
                    "BreadcrumbSeparator",
                    "Presentational divider hidden from accessibility. Default chevron is 14dp and mirrors in RTL.",
                    listOf(ApiProperty("content", "@Composable () -> Unit", "Chevron")),
                ),
                ComponentApiSection(
                    "BreadcrumbEllipsis",
                    (
                        "Presentational 20dp collapsed marker with a 16dp icon. Give " +
                            "its interactive parent a label such as Toggle menu."
                    ),
                    listOf(ApiProperty("style", "Style", "Style")),
                ),
            ),
    )

@Composable
internal fun BreadcrumbExample(
    kind: String,
    language: Int = 1,
) {
    val navigation = LocalDocNavigation.current
    val uri = LocalUriHandler.current
    val labels =
        when (language) {
            0 -> listOf("الرئيسية", "المكونات", "مسار التنقل", "التوثيق", "السمات", "جيت هاب")
            2 -> listOf("בית", "רכיבים", "פירורי לחם", "תיעוד", "ערכות נושא", "גיטהאב")
            else -> listOf("Home", "Components", "Breadcrumb", "Documentation", "Themes", "GitHub")
        }
    val colors = currentTheme.colors
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val useDot = kind in listOf("Custom separator", "Dropdown", "RTL")
    val separator: @Composable () -> Unit = {
        if (useDot) {
            BreadcrumbSeparator {
                Image(Lucide.Dot, null, Modifier.size(14.dp), colorFilter = ColorFilter.tint(colors.mutedForeground))
            }
        } else {
            BreadcrumbSeparator()
        }
    }
    val home: () -> Unit = {
        when (kind) {
            "Link component" -> navigation?.navigate("link-component")
            "Custom separator", "Dropdown", "RTL" -> uri.openUri("/")
            else -> navigation?.navigate("")
        }
    }
    Breadcrumb {
        BreadcrumbList {
            BreadcrumbItem(labels[0], onClick = home)
            separator()
            if (kind in listOf("Default", "Dropdown", "RTL")) {
                BreadcrumbItem {
                    DropdownMenu {
                        DropdownMenuTrigger(
                            modifier =
                                Modifier.semantics {
                                    contentDescription =
                                        if (kind == "Default") {
                                            "Toggle menu"
                                        } else {
                                            labels[1]
                                        }
                                },
                            style =
                                if (kind == "Default") {
                                    ButtonVariant.Ghost.rememberStyle() then ButtonSize.IconSm.rememberStyle()
                                } else {
                                    Style { contentColor(colors.mutedForeground) }
                                },
                        ) {
                            if (kind == "Default") {
                                Box(
                                    Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center,
                                ) { BreadcrumbEllipsis() }
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text { labels[1] }
                                    Image(
                                        Lucide.ChevronDown,
                                        null,
                                        Modifier.size(14.dp),
                                        colorFilter = ColorFilter.tint(colors.mutedForeground),
                                    )
                                }
                            }
                        }
                        DropdownMenuContent(align = if (rtl) PopupAlign.End else PopupAlign.Start) {
                            labels.drop(3).forEach { DropdownMenuItem(it, onClick = {}) }
                        }
                    }
                }
                separator()
            } else if (kind == "Collapsed") {
                BreadcrumbItem { BreadcrumbEllipsis() }
                separator()
            }
            if (kind !in listOf("Dropdown", "RTL")) {
                BreadcrumbItem(labels[1], onClick = {
                    when (kind) {
                        "Link component" -> navigation?.navigate("link-component")
                        "Custom separator", "Collapsed" -> uri.openUri("/components")
                        else -> navigation?.navigate("")
                    }
                })
                separator()
            }
            BreadcrumbItem(labels[2])
        }
    }
}
