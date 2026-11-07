@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.then
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.toutiaoyan.compose.ui.components.avatar.*
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.button.rememberStyle
import com.toutiaoyan.compose.ui.components.dropdownmenu.*
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.tailwind.core.TwColors
import compose_foundation.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private fun avatarRecipe(
    user: String = "shadcn",
    initials: String = "CN",
    size: String = "Default",
    badge: String = "",
): String =
    """
    Avatar(size = AvatarSize.$size) {
        AvatarImage(
            painter = painterResource(Res.drawable.avatar_$user),
            contentDescription = "@$user",
            colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }),
        )
        AvatarFallback("$initials")
        $badge
    }
    """.trimIndent()

private fun groupRecipe(
    icon: Boolean = false,
    count: Boolean = true,
): String =
    "AvatarGroup {\n" +
        listOf(avatarRecipe(), avatarRecipe("maxleiter", "LR"), avatarRecipe("evilrabbit", "ER"))
            .joinToString("\n") { it.prependIndent("    ") } +
        (
            if (!count) {
                ""
            } else if (icon) {
                (
                    "\n    AvatarGroupCount { Image(Lucide.Plus, null, " +
                        "Modifier.size(16.dp), colorFilter = " +
                        "ColorFilter.tint(currentTheme.colors.mutedForeground)) }"
                )
            } else {
                "\n    AvatarGroupCount(\"+3\")"
            }
        ) + "\n}"

private val avatarDemoCode =
    """
    val onlineColor = if (currentTheme.isDark) TwColors.green800 else TwColors.green600
    val windowWidth = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width.toDp() }
    val gap = if (windowWidth >= 768.dp) 48.dp else 24.dp
    FlowRow(horizontalArrangement = Arrangement.spacedBy(gap), verticalArrangement = Arrangement.spacedBy(gap),
        itemVerticalAlignment = Alignment.CenterVertically) {
    """.trimIndent() + "\n" + avatarRecipe().prependIndent("        ") + "\n" +
        avatarRecipe("evilrabbit", "ER", badge = "AvatarBadge(style = Style { background(onlineColor) })")
            .replace("colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }),", "")
            .prependIndent("        ") + "\n" + groupRecipe().prependIndent("        ") + "\n}"

private val avatarDropdownCode =
    """
    DropdownMenu {
        DropdownMenuTrigger(
            modifier = Modifier.semantics { contentDescription = "Open user menu" },
            style = ButtonVariant.Ghost.rememberStyle() then ButtonSize.Icon.rememberStyle() then Style { shape(CircleShape) },
        ) {
    """.trimIndent() + "\n" + avatarRecipe().prependIndent("        ") + "\n" +
        """
            }
            DropdownMenuContent(style = Style { width(128.dp) }) {
                DropdownMenuGroup {
                    DropdownMenuItem("Profile", onClick = {})
                    DropdownMenuItem("Billing", onClick = {})
                    DropdownMenuItem("Settings", onClick = {})
                }
                DropdownMenuSeparator()
                DropdownMenuItem("Log out", onClick = {}, destructive = true)
            }
        }
        """.trimIndent()

val avatarDoc =
    ComponentDoc(
        id = "avatar",
        title = "Avatar",
        description = "An image element with a fallback for representing the user.",
        usageImportsCode =
            """
            import com.toutiaoyan.compose.ui.components.avatar.*
            import org.jetbrains.compose.resources.painterResource
            import compose_foundation.app.shared.generated.resources.*
            """.trimIndent(),
        usageCode = avatarRecipe().lines().filterNot { "colorFilter =" in it }.joinToString("\n"),
        examplesHeading = null,
        compositionCode =
            """
            Avatar
            ├── AvatarImage
            ├── AvatarFallback
            └── AvatarBadge

            AvatarGroup
            ├── Avatar
            │   ├── AvatarImage
            │   ├── AvatarFallback
            │   └── AvatarBadge
            └── AvatarGroupCount
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample("Default", avatarDemoCode, preview = { AvatarDemoPreview() }),
                ComponentExample(
                    "Basic",
                    avatarRecipe(),
                    descriptionMarkdown =
                        "A basic avatar component with an image and a fallback.",
                    preview =
                        {
                            AvatarPhoto()
                        },
                ),
                ComponentExample(
                    "Badge",
                    (
                        "val onlineColor = if (currentTheme.isDark) TwColors.green800 " +
                            "else TwColors.green600\n"
                    ) + avatarRecipe(badge = "AvatarBadge(style = Style { background(onlineColor) })"),
                    descriptionMarkdown =
                        "Use the `AvatarBadge` component to add a badge to the avatar. " +
                            "The badge is positioned at the bottom right of the avatar. " +
                            "Use `Style` to customize its color.",
                    preview = {
                        AvatarPhoto(grayscale = false, badge = { AvatarBadge(style = onlineStyle()) })
                    },
                ),
                ComponentExample(
                    "Badge with Icon",
                    avatarRecipe(
                        "pranathip",
                        "PP",
                        badge = (
                            "AvatarBadge { Image(Lucide.Plus, null, " +
                                "Modifier.fillMaxSize(), colorFilter = " +
                                "ColorFilter.tint(currentTheme.colors.primaryForeground)) }"
                        ),
                    ),
                    descriptionMarkdown = "You can also use an icon inside AvatarBadge.",
                    preview = {
                        AvatarPhoto("pranathip", badge = { AvatarBadge { AvatarPlus(Modifier.fillMaxSize()) } })
                    },
                ),
                ComponentExample(
                    "Avatar Group",
                    groupRecipe(
                        count = false,
                    ),
                    descriptionMarkdown = "Use the `AvatarGroup` component to add a group of avatars.",
                    preview = {
                        AvatarPeople(count = false)
                    },
                ),
                ComponentExample(
                    "Avatar Group Count",
                    groupRecipe(),
                    descriptionMarkdown =
                        "Use `AvatarGroupCount` to add a count to the group.",
                    preview =
                        {
                            AvatarPeople()
                        },
                ),
                ComponentExample(
                    "Avatar Group with Icon",
                    groupRecipe(
                        icon = true,
                    ),
                    descriptionMarkdown = "You can also use an icon inside AvatarGroupCount.",
                    preview = {
                        AvatarPeople(icon = true)
                    },
                ),
                ComponentExample(
                    "Sizes",
                    (
                        "Row(horizontalArrangement = Arrangement.spacedBy(8.dp), " +
                            "verticalAlignment = Alignment.CenterVertically) {\n"
                    ) +
                        listOf(
                            "Sm",
                            "Default",
                            "Lg",
                        ).joinToString("\n") {
                            avatarRecipe(
                                size =
                                it,
                            ).prependIndent("    ")
                        } +
                        "\n}",
                    descriptionMarkdown = "Use the `size` prop to change the size of the avatar.",
                    preview = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            AvatarSize.entries.forEach { AvatarPhoto(size = it) }
                        }
                    },
                ),
                ComponentExample(
                    "Dropdown",
                    avatarDropdownCode,
                    descriptionMarkdown =
                        "You can use the `Avatar` component as a trigger for a dropdown menu.",
                    preview =
                        {
                            AvatarDropdownPreview()
                        },
                ),
                ComponentExample(
                    "RTL",
                    """
                    var language by remember { mutableStateOf(0) }
                    RtlPreview(language, listOf("Arabic (العربية)", "English", "Hebrew (עברית)"), { language = it }) {
                        CompositionLocalProvider(LocalLayoutDirection provides if (language == 1) LayoutDirection.Ltr else LayoutDirection.Rtl) {
                    """.trimIndent() + "\n" +
                        avatarDemoCode.replace(
                            "AvatarGroupCount(\"+3\")",
                            "AvatarGroupCount(if (language == 0) \"+٣\" else \"+3\")",
                        ).prependIndent("        ") + "\n    }\n}",
                    previewPadding = 0.dp,
                    descriptionMarkdown = (
                        "To enable RTL support in shadcn/ui, see the [RTL configuration " +
                            "guide](https://ui.shadcn.com/docs/rtl)."
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
                                    Modifier.fillMaxWidth().height(288.dp).padding(24.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    AvatarDemoPreview(if (language == 0) "+٣" else "+3")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    "Kotlin extension: Fallback",
                    "Avatar(\"CN\")",
                    descriptionMarkdown = (
                        "Initials remain visible when an image is missing or fails to " +
                            "load."
                    ),
                    preview = {
                        Avatar("CN")
                    },
                ),
            ),
        apiSections =
            listOf(
                ComponentApiSection(
                    "Avatar",
                    "Root container. Style controls size and shape; image loading state is shared with the fallback.",
                    listOf(ApiProperty("size", "AvatarSize", "Default"), ApiProperty("style", "Style", "Style")),
                ),
                ComponentApiSection(
                    "AvatarImage",
                    (
                        "Use a model for asynchronous loading, a Painter for a decoded " +
                            "local image, or an ImageVector. Reports Idle, Loading, " +
                            "Success and Error through onLoadingStatusChange."
                    ),
                    listOf(
                        ApiProperty("model / painter / imageVector", "Any? / Painter / ImageVector", "Required"),
                        ApiProperty("contentDescription", "String?", "null"),
                        ApiProperty("onLoadingStatusChange", "((AvatarImageLoadState) -> Unit)?", "null"),
                    ),
                ),
                ComponentApiSection(
                    "AvatarFallback",
                    (
                        "Shown when no image is available. delayMs delays the loading " +
                            "fallback; errors and missing images show it immediately."
                    ),
                    listOf(
                        ApiProperty("delayMs", "Int?", "null (uses delayMillis = 0)"),
                        ApiProperty("content", "@Composable BoxScope.() -> Unit", "Required, or text overload"),
                    ),
                ),
                ComponentApiSection(
                    "AvatarBadge",
                    (
                        "Bottom-end dot or icon inside Avatar; mirrors in RTL. Small " +
                            "avatars hide icon content. Style can override the badge " +
                            "appearance."
                    ),
                    listOf(
                        ApiProperty("style", "Style", "Style"),
                        ApiProperty("content", "(@Composable () -> Unit)?", "null"),
                    ),
                ),
                ComponentApiSection(
                    "AvatarGroup",
                    (
                        "Row of avatars with background-colored rings. Spacing controls " +
                            "overlap; Compose layout direction controls ordering."
                    ),
                    listOf(
                        ApiProperty("spacing", "Dp", "(-8).dp"),
                        ApiProperty("content", "@Composable () -> Unit", "Required"),
                    ),
                ),
                ComponentApiSection(
                    "AvatarGroupCount",
                    (
                        "Muted count or caller-provided icon. In an AvatarGroup the " +
                            "default size follows CSS precedence: any sm member makes it sm, " +
                            "otherwise any lg member makes it lg; otherwise it is default. " +
                            "Pass size to override it."
                    ),
                    listOf(
                        ApiProperty("size", "AvatarSize?", "Follows group (Default outside a group)"),
                        ApiProperty("content", "@Composable BoxScope.() -> Unit", "Required, or text overload"),
                    ),
                ),
            ),
    )

@Composable
private fun onlineStyle(): Style {
    val color = if (currentTheme.isDark) TwColors.green800 else TwColors.green600
    return Style { background(color) }
}

@Composable
internal fun AvatarPhoto(
    user: String = "shadcn",
    size: AvatarSize = AvatarSize.Default,
    grayscale: Boolean = true,
    badge: @Composable BoxScope.() -> Unit = {},
) {
    val resource =
        when (user) {
            "maxleiter" -> Res.drawable.avatar_maxleiter
            "evilrabbit" -> Res.drawable.avatar_evilrabbit
            "pranathip" -> Res.drawable.avatar_pranathip
            else -> Res.drawable.avatar_shadcn
        }
    Avatar(size = size) {
        AvatarImage(
            painterResource(
                resource,
            ),
            "@$user",
            colorFilter =
                if (grayscale) {
                    ColorFilter.colorMatrix(
                        ColorMatrix().apply {
                            setToSaturation(0f)
                        },
                    )
                } else {
                    null
                },
        )
        AvatarFallback(
            when (user) {
                "maxleiter" -> "LR"
                "evilrabbit" -> "ER"
                "pranathip" -> "PP"
                else -> "CN"
            },
        )
        badge()
    }
}

@Composable
private fun AvatarPlus(modifier: Modifier = Modifier) {
    Image(Lucide.Plus, null, modifier, colorFilter = ColorFilter.tint(LocalContentColor.current))
}

@Composable
private fun AvatarPeople(
    count: Boolean = true,
    icon: Boolean = false,
    countText: String = "+3",
) {
    AvatarGroup {
        AvatarPhoto()
        AvatarPhoto("maxleiter")
        AvatarPhoto("evilrabbit")
        if (count) {
            if (icon) {
                AvatarGroupCount { AvatarPlus(Modifier.size(16.dp)) }
            } else {
                AvatarGroupCount(countText)
            }
        }
    }
}

@Composable
internal fun AvatarDemoPreview(countText: String = "+3") {
    val windowWidth = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width.toDp() }
    val gap = if (windowWidth >= 768.dp) 48.dp else 24.dp
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(gap),
        verticalArrangement = Arrangement.spacedBy(gap),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) {
        AvatarPhoto()
        AvatarPhoto("evilrabbit", grayscale = false, badge = { AvatarBadge(style = onlineStyle()) })
        AvatarPeople(countText = countText)
    }
}

@Composable
internal fun AvatarDropdownPreview() {
    DropdownMenu {
        DropdownMenuTrigger(
            modifier = Modifier.semantics { contentDescription = "Open user menu" },
            style =
                ButtonVariant.Ghost.rememberStyle() then
                    ButtonSize.Icon.rememberStyle() then
                    Style { shape(CircleShape) },
        ) { AvatarPhoto(grayscale = false) }
        DropdownMenuContent(style = Style { width(128.dp) }) {
            DropdownMenuGroup {
                DropdownMenuItem("Profile", onClick = {})
                DropdownMenuItem("Billing", onClick = {})
                DropdownMenuItem("Settings", onClick = {})
            }
            DropdownMenuSeparator()
            DropdownMenuItem("Log out", onClick = {}, destructive = true)
        }
    }
}
