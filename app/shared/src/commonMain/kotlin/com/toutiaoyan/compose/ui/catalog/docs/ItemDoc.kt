@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.composables.icons.lucide.*
import com.toutiaoyan.compose.ui.components.avatar.*
import com.toutiaoyan.compose.ui.components.button.*
import com.toutiaoyan.compose.ui.components.dropdownmenu.*
import com.toutiaoyan.compose.ui.components.item.*
import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.currentTheme
import compose_foundation.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

val itemDoc =
    ComponentDoc(
        id = "item",
        title = "Item",
        description = "A versatile component for displaying content with media, title, description, and actions.",
        examplesHeading = null,
        usageImportsCode =
            """
            import androidx.compose.foundation.Image
            import androidx.compose.foundation.layout.*
            import androidx.compose.foundation.shape.CircleShape
            import androidx.compose.foundation.shape.RoundedCornerShape
            import androidx.compose.foundation.style.Style
            import androidx.compose.foundation.style.contentPadding
            import androidx.compose.runtime.*
            import androidx.compose.ui.Alignment
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.draw.clip
            import androidx.compose.ui.graphics.*
            import androidx.compose.ui.graphics.vector.ImageVector
            import androidx.compose.ui.layout.ContentScale
            import androidx.compose.ui.platform.LocalDensity
            import androidx.compose.ui.platform.LocalWindowInfo
            import androidx.compose.ui.platform.LocalLayoutDirection
            import androidx.compose.ui.text.*
            import androidx.compose.ui.text.style.TextAlign
            import androidx.compose.ui.unit.*
            import com.composables.icons.lucide.*
            import com.toutiaoyan.compose.ui.components.avatar.*
            import com.toutiaoyan.compose.ui.components.button.*
            import com.toutiaoyan.compose.ui.components.dropdownmenu.*
            import com.toutiaoyan.compose.ui.components.item.*
            import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
            import com.toutiaoyan.compose.ui.components.text.Text
            import com.toutiaoyan.compose.ui.theme.LocalContentColor
            import com.toutiaoyan.compose.ui.theme.currentTheme
            import compose_foundation.app.shared.generated.resources.*
            import org.jetbrains.compose.resources.painterResource
            """.trimIndent(),
        usageCode =
            """
            Item {
                ItemContent { ItemTitle("Title"); ItemDescription("Description") }
                ItemActions { Button("Action", onClick = {}) }
            }
            """.trimIndent(),
        compositionCode =
            """
            ItemGroup
            └── Item
                ├── ItemHeader
                ├── ItemMedia
                ├── ItemContent
                │   ├── ItemTitle
                │   └── ItemDescription
                ├── ItemActions
                └── ItemFooter
            """.trimIndent(),
        beforeExamples =
            listOf(
                ComponentGuideSection(
                    "Item vs Field",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            "Use `Field` if you need to display a form input such as a checkbox, input, " +
                                "radio, or select.",
                        ),
                        ComponentGuideBlock.Paragraph(
                            "If you only need to display content such as a title, description, and " +
                                "actions, use `Item`.",
                        ),
                    ),
                ),
            ),
        apiSections =
            listOf(
                ComponentApiSection(
                    "Item",
                    "Flexible row with full-width wrapping regions.",
                    listOf(
                        ApiProperty("variant", "ItemVariant", "Default"),
                        ApiProperty("size", "ItemSize", "Default"),
                        ApiProperty("href", "String?", "null"),
                        ApiProperty("onClick", "(() -> Unit)?", "null"),
                        ApiProperty("enabled / disabled", "Boolean", "true / false"),
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                    ),
                ),
                ComponentApiSection(
                    "ItemGroup",
                    "A list with automatic size-aware spacing.",
                    listOf(
                        ApiProperty("spacing", "Dp?", "null (16 / 10 / 8.dp)"),
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                    ),
                ),
                ComponentApiSection(
                    "ItemSeparator",
                    "A separator with 8dp vertical margins.",
                    listOf(
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                    ),
                ),
                ComponentApiSection(
                    "ItemMedia",
                    (
                        "Icon is unstyled; Image uses 40/32/24dp for Default/Sm/Xs. " +
                            "Media aligns to the top when a description is present."
                    ),
                    listOf(
                        ApiProperty("variant", "ItemMediaVariant", "Default"),
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                    ),
                ),
                ComponentApiSection(
                    "ItemContent",
                    (
                        "Flexible content column. Use fill=false for a fixed trailing " +
                            "column."
                    ),
                    listOf(
                        ApiProperty("fill", "Boolean", "true"),
                        ApiProperty("spacing", "Dp?", "null (4.dp; Xs 0.dp)"),
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                    ),
                ),
                ComponentApiSection(
                    "ItemTitle",
                    "One-line title with ellipsis in Nova.",
                    listOf(
                        ApiProperty("text", "String / AnnotatedString", "Required"),
                        ApiProperty("textStyle", "TextStyle", "Default"),
                        ApiProperty("maxLines", "Int", "Int.MAX_VALUE; use 1 for ellipsis"),
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                    ),
                ),
                ComponentApiSection(
                    "ItemDescription",
                    "Two-line description, with rich text and inline link support.",
                    listOf(
                        ApiProperty("text", "String / AnnotatedString", "Required"),
                        ApiProperty("textStyle", "TextStyle", "Default"),
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                    ),
                ),
                ComponentApiSection(
                    "ItemActions",
                    "Trailing action row.",
                    listOf(
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                    ),
                ),
                ComponentApiSection(
                    "ItemHeader",
                    "A full-width row before the body.",
                    listOf(
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                    ),
                ),
                ComponentApiSection(
                    "ItemFooter",
                    "A full-width row after the body.",
                    listOf(
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                    ),
                ),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    descriptionMarkdown =
                        "The `Item` component is a straightforward flex container that can house " +
                            "nearly any type of content. Use it to display a title, description, and " +
                            "actions. Group it with the `ItemGroup` component to create a list of items.",
                    minPreviewHeight = 288.dp,
                    mobileMinPreviewHeight = 288.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        Column(Modifier.widthIn(max = 448.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            Item(variant = ItemVariant.Outline) {
                                ItemContent { ItemTitle("Basic Item"); ItemDescription("A simple item with title and description.") }
                                ItemActions { Button("Action", onClick = {}, variant = ButtonVariant.Outline, size = ButtonSize.Sm) }
                            }
                            Item(variant = ItemVariant.Outline, size = ItemSize.Sm, href = "#") {
                                ItemMedia { ItemDemoGlyph(Lucide.BadgeCheck, 20.dp) }
                                ItemContent { ItemTitle("Your profile has been verified.") }
                                ItemActions { ItemDemoGlyph(Lucide.ChevronRight) }
                            }
                        }

                        @Composable
                        private fun ItemDemoGlyph(image: ImageVector, size: Dp = 16.dp) {
                            val color = LocalContentColor.current.takeIf { it.isSpecified } ?: currentTheme.colors.foreground
                            Image(image, null, Modifier.size(size), colorFilter = ColorFilter.tint(color))
                        }
                        """.trimIndent(),
                    preview = {
                        Column(
                            Modifier.widthIn(
                                max =
                                    448.dp,
                            ).fillMaxWidth(),
                            verticalArrangement =
                                Arrangement.spacedBy(24.dp),
                        ) {
                            Item(variant = ItemVariant.Outline) {
                                ItemContent {
                                    ItemTitle("Basic Item")
                                    ItemDescription("A simple item with title and description.")
                                }
                                ItemActions {
                                    Button(
                                        "Action",
                                        onClick =
                                            {},
                                        variant =
                                            ButtonVariant.Outline,
                                        size =
                                            ButtonSize.Sm,
                                    )
                                }
                            }
                            Item(variant = ItemVariant.Outline, size = ItemSize.Sm, href = "#") {
                                ItemMedia { ItemDemoGlyph(Lucide.BadgeCheck, 20.dp) }
                                ItemContent { ItemTitle("Your profile has been verified.") }
                                ItemActions { ItemDemoGlyph(Lucide.ChevronRight) }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Variant",
                    descriptionMarkdown = "Use the `variant` prop to change the visual style of the item.",
                    minPreviewHeight = 384.dp,
                    mobileMinPreviewHeight = 384.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        Column(Modifier.widthIn(max = 448.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            listOf(
                                Triple(ItemVariant.Default, "Default Variant", "Transparent background with no border."),
                                Triple(ItemVariant.Outline, "Outline Variant", "Outlined style with a visible border."),
                                Triple(ItemVariant.Muted, "Muted Variant", "Muted background for secondary content."),
                            ).forEach { (variant, title, description) ->
                                Item(variant = variant) {
                                    ItemMedia(variant = ItemMediaVariant.Icon) { ItemDemoGlyph(Lucide.Inbox) }
                                    ItemContent { ItemTitle(title); ItemDescription(description) }
                                }
                            }
                        }

                        @Composable
                        private fun ItemDemoGlyph(image: ImageVector, size: Dp = 16.dp) {
                            val color = LocalContentColor.current.takeIf { it.isSpecified } ?: currentTheme.colors.foreground
                            Image(image, null, Modifier.size(size), colorFilter = ColorFilter.tint(color))
                        }
                        """.trimIndent(),
                    preview = {
                        Column(
                            Modifier.widthIn(
                                max =
                                    448.dp,
                            ).fillMaxWidth(),
                            verticalArrangement =
                                Arrangement.spacedBy(24.dp),
                        ) {
                            listOf(
                                Triple(
                                    ItemVariant.Default,
                                    "Default Variant",
                                    "Transparent background with no border.",
                                ),
                                Triple(ItemVariant.Outline, "Outline Variant", "Outlined style with a visible border."),
                                Triple(ItemVariant.Muted, "Muted Variant", "Muted background for secondary content."),
                            ).forEach { (variant, title, description) ->
                                Item(variant = variant) {
                                    ItemMedia(variant = ItemMediaVariant.Icon) { ItemDemoGlyph(Lucide.Inbox) }
                                    ItemContent {
                                        ItemTitle(title)
                                        ItemDescription(description)
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Size",
                    descriptionMarkdown = "Use the `size` prop to change the size of the item. Available sizes are `default`, `sm`, and `xs`.",
                    minPreviewHeight = 384.dp,
                    mobileMinPreviewHeight = 384.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        Column(Modifier.widthIn(max = 448.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            listOf(
                                Triple(ItemSize.Default, "Default Size", "The standard size for most use cases."),
                                Triple(ItemSize.Sm, "Small Size", "A compact size for dense layouts."),
                                Triple(ItemSize.Xs, "Extra Small Size", "The most compact size available."),
                            ).forEach { (size, title, description) ->
                                Item(variant = ItemVariant.Outline, size = size) {
                                    ItemMedia(variant = ItemMediaVariant.Icon) { ItemDemoGlyph(Lucide.Inbox) }
                                    ItemContent { ItemTitle(title); ItemDescription(description) }
                                }
                            }
                        }

                        @Composable
                        private fun ItemDemoGlyph(image: ImageVector, size: Dp = 16.dp) {
                            val color = LocalContentColor.current.takeIf { it.isSpecified } ?: currentTheme.colors.foreground
                            Image(image, null, Modifier.size(size), colorFilter = ColorFilter.tint(color))
                        }
                        """.trimIndent(),
                    preview = {
                        Column(
                            Modifier.widthIn(
                                max =
                                    448.dp,
                            ).fillMaxWidth(),
                            verticalArrangement =
                                Arrangement.spacedBy(24.dp),
                        ) {
                            listOf(
                                Triple(ItemSize.Default, "Default Size", "The standard size for most use cases."),
                                Triple(ItemSize.Sm, "Small Size", "A compact size for dense layouts."),
                                Triple(ItemSize.Xs, "Extra Small Size", "The most compact size available."),
                            ).forEach { (size, title, description) ->
                                Item(variant = ItemVariant.Outline, size = size) {
                                    ItemMedia(variant = ItemMediaVariant.Icon) { ItemDemoGlyph(Lucide.Inbox) }
                                    ItemContent {
                                        ItemTitle(title)
                                        ItemDescription(description)
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Icon",
                    descriptionMarkdown = "Use `ItemMedia` with `variant=\"icon\"` to display an icon.",
                    minPreviewHeight = 288.dp,
                    mobileMinPreviewHeight = 288.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        Item(Modifier.widthIn(max = 512.dp).fillMaxWidth(), variant = ItemVariant.Outline) {
                            ItemMedia(variant = ItemMediaVariant.Icon) { ItemDemoGlyph(Lucide.ShieldAlert) }
                            ItemContent { ItemTitle("Security Alert"); ItemDescription("New login detected from unknown device.") }
                            ItemActions { Button("Review", onClick = {}, variant = ButtonVariant.Outline, size = ButtonSize.Sm) }
                        }

                        @Composable
                        private fun ItemDemoGlyph(image: ImageVector, size: Dp = 16.dp) {
                            val color = LocalContentColor.current.takeIf { it.isSpecified } ?: currentTheme.colors.foreground
                            Image(image, null, Modifier.size(size), colorFilter = ColorFilter.tint(color))
                        }
                        """.trimIndent(),
                    preview = {
                        Item(Modifier.widthIn(max = 512.dp).fillMaxWidth(), variant = ItemVariant.Outline) {
                            ItemMedia(variant = ItemMediaVariant.Icon) { ItemDemoGlyph(Lucide.ShieldAlert) }
                            ItemContent {
                                ItemTitle("Security Alert")
                                ItemDescription("New login detected from unknown device.")
                            }
                            ItemActions {
                                Button(
                                    "Review",
                                    onClick =
                                        {},
                                    variant =
                                        ButtonVariant.Outline,
                                    size =
                                        ButtonSize.Sm,
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Avatar",
                    descriptionMarkdown = "You can use `ItemMedia` with `variant=\"avatar\"` to display an avatar.",
                    minPreviewHeight = 288.dp,
                    mobileMinPreviewHeight = 288.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        val wide = LocalWindowInfo.current.containerSize.width >= with(LocalDensity.current) { 640.dp.roundToPx() }
                        Column(Modifier.widthIn(max = 512.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            Item(variant = ItemVariant.Outline) {
                                ItemMedia { ItemDemoPhoto("evilrabbit", size = 40.dp, grayscale = false) }
                                ItemContent { ItemTitle("Evil Rabbit"); ItemDescription("Last seen 5 months ago") }
                                ItemActions {
                                    Button(onClick = {}, variant = ButtonVariant.Outline, size = ButtonSize.IconSm,
                                        label = "Invite", style = Style { shape(CircleShape) }) { ItemDemoGlyph(Lucide.Plus, 14.dp) }
                                }
                            }
                            Item(variant = ItemVariant.Outline) {
                                ItemMedia {
                                    AvatarGroup {
                                        if (wide) { ItemDemoPhoto("shadcn"); ItemDemoPhoto("maxleiter") }
                                        ItemDemoPhoto("evilrabbit")
                                    }
                                }
                                ItemContent { ItemTitle("No Team Members"); ItemDescription("Invite your team to collaborate on this project.") }
                                ItemActions { Button("Invite", onClick = {}, size = ButtonSize.Sm, variant = ButtonVariant.Outline) }
                            }
                        }

                        @Composable
                        private fun ItemDemoPhoto(user: String, size: Dp = 32.dp, grayscale: Boolean = true) {
                            val resource = when (user) {
                                "maxleiter" -> Res.drawable.avatar_maxleiter
                                "evilrabbit" -> Res.drawable.avatar_evilrabbit
                                else -> Res.drawable.avatar_shadcn
                            }
                            Avatar(style = Style { width(size); height(size) }) {
                                AvatarImage(painterResource(resource), "@" + user,
                                    colorFilter = if (grayscale) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }) else null)
                                AvatarFallback(if (user == "evilrabbit") "ER" else user.take(1))
                            }
                        }

                        @Composable
                        private fun ItemDemoGlyph(image: ImageVector, size: Dp = 16.dp) {
                            val color = LocalContentColor.current.takeIf { it.isSpecified } ?: currentTheme.colors.foreground
                            Image(image, null, Modifier.size(size), colorFilter = ColorFilter.tint(color))
                        }
                        """.trimIndent(),
                    preview = {
                        val wide =
                            LocalWindowInfo.current.containerSize.width >=
                                with(LocalDensity.current) { 640.dp.roundToPx() }
                        Column(
                            Modifier.widthIn(
                                max =
                                    512.dp,
                            ).fillMaxWidth(),
                            verticalArrangement =
                                Arrangement.spacedBy(24.dp),
                        ) {
                            Item(variant = ItemVariant.Outline) {
                                ItemMedia { ItemDemoPhoto("evilrabbit", size = 40.dp, grayscale = false) }
                                ItemContent {
                                    ItemTitle("Evil Rabbit")
                                    ItemDescription("Last seen 5 months ago")
                                }
                                ItemActions {
                                    Button(
                                        onClick = {},
                                        variant = ButtonVariant.Outline,
                                        size = ButtonSize.IconSm,
                                        label =
                                            "Invite",
                                        style =
                                            Style { shape(CircleShape) },
                                    ) {
                                        ItemDemoGlyph(
                                            Lucide.Plus,
                                            14.dp,
                                        )
                                    }
                                }
                            }
                            Item(variant = ItemVariant.Outline) {
                                ItemMedia {
                                    AvatarGroup {
                                        if (wide) {
                                            ItemDemoPhoto("shadcn")
                                            ItemDemoPhoto("maxleiter")
                                        }
                                        ItemDemoPhoto("evilrabbit")
                                    }
                                }
                                ItemContent {
                                    ItemTitle("No Team Members")
                                    ItemDescription("Invite your team to collaborate on this project.")
                                }
                                ItemActions {
                                    Button(
                                        "Invite",
                                        onClick =
                                            {},
                                        size =
                                            ButtonSize.Sm,
                                        variant =
                                            ButtonVariant.Outline,
                                    )
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Image",
                    descriptionMarkdown = "Use `ItemMedia` with `variant=\"image\"` to display an image.",
                    minPreviewHeight = 288.dp,
                    mobileMinPreviewHeight = 288.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        val songs = listOf(
                            listOf("Midnight City Lights", "Neon Dreams", "Electric Nights", "3:45"),
                            listOf("Coffee Shop Conversations", "The Morning Brew", "Urban Stories", "4:05"),
                            listOf("Digital Rain", "Cyber Symphony", "Binary Beats", "3:30"),
                        )
                        val covers = listOf(Res.drawable.item_song_0, Res.drawable.item_song_1, Res.drawable.item_song_2)
                        val muted = currentTheme.colors.mutedForeground
                        ItemGroup(Modifier.widthIn(max = 448.dp).fillMaxWidth(), spacing = 16.dp) {
                            songs.forEachIndexed { index, song ->
                                Item(variant = ItemVariant.Outline, href = "#") {
                                    ItemMedia(variant = ItemMediaVariant.Image) {
                                        Image(painterResource(covers[index]), song[0], Modifier.fillMaxSize(), contentScale = ContentScale.Crop,
                                            colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }))
                                    }
                                    ItemContent {
                                        ItemTitle(buildAnnotatedString {
                                            append(song[0] + " - ")
                                            withStyle(SpanStyle(color = muted)) { append(song[2]) }
                                        }, maxLines = 1)
                                        ItemDescription(song[1])
                                    }
                                    ItemContent(fill = false) { ItemDescription(song[3], textStyle = TextStyle(textAlign = TextAlign.Center)) }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        val songs =
                            listOf(
                                listOf("Midnight City Lights", "Neon Dreams", "Electric Nights", "3:45"),
                                listOf("Coffee Shop Conversations", "The Morning Brew", "Urban Stories", "4:05"),
                                listOf("Digital Rain", "Cyber Symphony", "Binary Beats", "3:30"),
                            )
                        val covers =
                            listOf(Res.drawable.item_song_0, Res.drawable.item_song_1, Res.drawable.item_song_2)
                        val muted = currentTheme.colors.mutedForeground
                        ItemGroup(Modifier.widthIn(max = 448.dp).fillMaxWidth(), spacing = 16.dp) {
                            songs.forEachIndexed { index, song ->
                                Item(variant = ItemVariant.Outline, href = "#") {
                                    ItemMedia(variant = ItemMediaVariant.Image) {
                                        Image(
                                            painterResource(covers[index]),
                                            song[0],
                                            Modifier.fillMaxSize(),
                                            contentScale =
                                                ContentScale.Crop,
                                            colorFilter =
                                                ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }),
                                        )
                                    }
                                    ItemContent {
                                        ItemTitle(
                                            buildAnnotatedString {
                                                append(song[0] + " - ")
                                                withStyle(SpanStyle(color = muted)) { append(song[2]) }
                                            },
                                            maxLines = 1,
                                        )
                                        ItemDescription(song[1])
                                    }
                                    ItemContent(
                                        fill =
                                        false,
                                    ) {
                                        ItemDescription(
                                            song[3],
                                            textStyle =
                                                TextStyle(
                                                    textAlign =
                                                        TextAlign.Center,
                                                ),
                                        )
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Group",
                    descriptionMarkdown = "Use `ItemGroup` to group related items together.",
                    minPreviewHeight = 384.dp,
                    mobileMinPreviewHeight = 384.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        ItemGroup(Modifier.widthIn(max = 384.dp).fillMaxWidth()) {
                            listOf("shadcn", "maxleiter", "evilrabbit").forEach { user ->
                                Item(variant = ItemVariant.Outline) {
                                    ItemMedia { ItemDemoPhoto(user) }
                                    ItemContent { ItemTitle(user); ItemDescription(user + "@vercel.com") }
                                    ItemActions {
                                        Button(onClick = {}, variant = ButtonVariant.Ghost, size = ButtonSize.Icon,
                                            label = "Add " + user, style = Style { shape(CircleShape) }) { ItemDemoGlyph(Lucide.Plus) }
                                    }
                                }
                            }
                        }

                        @Composable
                        private fun ItemDemoPhoto(user: String, size: Dp = 32.dp, grayscale: Boolean = true) {
                            val resource = when (user) {
                                "maxleiter" -> Res.drawable.avatar_maxleiter
                                "evilrabbit" -> Res.drawable.avatar_evilrabbit
                                else -> Res.drawable.avatar_shadcn
                            }
                            Avatar(style = Style { width(size); height(size) }) {
                                AvatarImage(painterResource(resource), "@" + user,
                                    colorFilter = if (grayscale) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }) else null)
                                AvatarFallback(if (user == "evilrabbit") "ER" else user.take(1))
                            }
                        }

                        @Composable
                        private fun ItemDemoGlyph(image: ImageVector, size: Dp = 16.dp) {
                            val color = LocalContentColor.current.takeIf { it.isSpecified } ?: currentTheme.colors.foreground
                            Image(image, null, Modifier.size(size), colorFilter = ColorFilter.tint(color))
                        }
                        """.trimIndent(),
                    preview = {
                        ItemGroup(Modifier.widthIn(max = 384.dp).fillMaxWidth()) {
                            listOf("shadcn", "maxleiter", "evilrabbit").forEach { user ->
                                Item(variant = ItemVariant.Outline) {
                                    ItemMedia { ItemDemoPhoto(user) }
                                    ItemContent {
                                        ItemTitle(user)
                                        ItemDescription(user + "@vercel.com")
                                    }
                                    ItemActions {
                                        Button(
                                            onClick = {},
                                            variant = ButtonVariant.Ghost,
                                            size = ButtonSize.Icon,
                                            label =
                                                "Add " +
                                                    user,
                                            style =
                                                Style { shape(CircleShape) },
                                        ) { ItemDemoGlyph(Lucide.Plus) }
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Header",
                    descriptionMarkdown = "Use `ItemHeader` to add a header above the item content.",
                    minPreviewHeight = 384.dp,
                    mobileMinPreviewHeight = 384.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        val models = listOf(
                            "v0-1.5-sm" to "Everyday tasks and UI generation.",
                            "v0-1.5-lg" to "Advanced thinking or reasoning.",
                            "v0-2.0-mini" to "Open Source model for everyone.",
                        )
                        val photos = listOf(Res.drawable.item_model_0, Res.drawable.item_model_1, Res.drawable.item_model_2)
                        ItemGroup(Modifier.widthIn(max = 576.dp).fillMaxWidth()) {
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                models.forEachIndexed { index, model ->
                                    Item(Modifier.weight(1f), variant = ItemVariant.Outline) {
                                        ItemHeader {
                                            Image(painterResource(photos[index]), model.first,
                                                Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(2.dp)), contentScale = ContentScale.Crop)
                                        }
                                        ItemContent { ItemTitle(model.first); ItemDescription(model.second) }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        val models =
                            listOf(
                                "v0-1.5-sm" to "Everyday tasks and UI generation.",
                                "v0-1.5-lg" to "Advanced thinking or reasoning.",
                                "v0-2.0-mini" to "Open Source model for everyone.",
                            )
                        val photos =
                            listOf(Res.drawable.item_model_0, Res.drawable.item_model_1, Res.drawable.item_model_2)
                        ItemGroup(Modifier.widthIn(max = 576.dp).fillMaxWidth()) {
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                models.forEachIndexed { index, model ->
                                    Item(Modifier.weight(1f), variant = ItemVariant.Outline) {
                                        ItemHeader {
                                            Image(
                                                painterResource(photos[index]),
                                                model.first,
                                                Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(2.dp)),
                                                contentScale =
                                                    ContentScale.Crop,
                                            )
                                        }
                                        ItemContent {
                                            ItemTitle(model.first)
                                            ItemDescription(model.second)
                                        }
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Link",
                    descriptionMarkdown = "Use `href` to render the item as a link (the Kotlin equivalent of the `render` prop). The hover and focus states apply to the whole row.",
                    minPreviewHeight = 288.dp,
                    mobileMinPreviewHeight = 288.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        Column(Modifier.widthIn(max = 448.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Item(href = "#") {
                                ItemContent { ItemTitle("Visit our documentation"); ItemDescription("Learn how to get started with our components.") }
                                ItemActions { ItemDemoGlyph(Lucide.ChevronRight) }
                            }
                            Item(variant = ItemVariant.Outline, href = "#") {
                                ItemContent { ItemTitle("External resource"); ItemDescription("Opens in a new tab with security attributes.") }
                                ItemActions { ItemDemoGlyph(Lucide.ExternalLink) }
                            }
                        }

                        @Composable
                        private fun ItemDemoGlyph(image: ImageVector, size: Dp = 16.dp) {
                            val color = LocalContentColor.current.takeIf { it.isSpecified } ?: currentTheme.colors.foreground
                            Image(image, null, Modifier.size(size), colorFilter = ColorFilter.tint(color))
                        }
                        """.trimIndent(),
                    preview = {
                        Column(
                            Modifier.widthIn(
                                max =
                                    448.dp,
                            ).fillMaxWidth(),
                            verticalArrangement =
                                Arrangement.spacedBy(16.dp),
                        ) {
                            Item(href = "#") {
                                ItemContent {
                                    ItemTitle("Visit our documentation")
                                    ItemDescription("Learn how to get started with our components.")
                                }
                                ItemActions { ItemDemoGlyph(Lucide.ChevronRight) }
                            }
                            Item(variant = ItemVariant.Outline, href = "#") {
                                ItemContent {
                                    ItemTitle("External resource")
                                    ItemDescription("Opens in a new tab with security attributes.")
                                }
                                ItemActions { ItemDemoGlyph(Lucide.ExternalLink) }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Dropdown",
                    minPreviewHeight = 288.dp,
                    mobileMinPreviewHeight = 288.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        DropdownMenu {
                            DropdownMenuTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text { "Select" }
                                        ItemDemoGlyph(Lucide.ChevronDown)
                                    }
                                }
                            }
                            DropdownMenuContent(align = PopupAlign.End, style = Style { width(192.dp) }) {
                                DropdownMenuGroup {
                                    listOf("shadcn", "maxleiter", "evilrabbit").forEach { user ->
                                        DropdownMenuItem(label = user, onClick = {}) {
                                            Item(size = ItemSize.Xs) {
                                                ItemMedia { ItemDemoPhoto(user, size = 26.dp) }
                                                ItemContent(spacing = 0.dp) {
                                                    ItemTitle(user)
                                                    ItemDescription(user + "@vercel.com", textStyle = TextStyle(lineHeight = 12.sp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        @Composable
                        private fun ItemDemoPhoto(user: String, size: Dp = 32.dp, grayscale: Boolean = true) {
                            val resource = when (user) {
                                "maxleiter" -> Res.drawable.avatar_maxleiter
                                "evilrabbit" -> Res.drawable.avatar_evilrabbit
                                else -> Res.drawable.avatar_shadcn
                            }
                            Avatar(style = Style { width(size); height(size) }) {
                                AvatarImage(painterResource(resource), "@" + user,
                                    colorFilter = if (grayscale) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }) else null)
                                AvatarFallback(if (user == "evilrabbit") "ER" else user.take(1))
                            }
                        }

                        @Composable
                        private fun ItemDemoGlyph(image: ImageVector, size: Dp = 16.dp) {
                            val color = LocalContentColor.current.takeIf { it.isSpecified } ?: currentTheme.colors.foreground
                            Image(image, null, Modifier.size(size), colorFilter = ColorFilter.tint(color))
                        }
                        """.trimIndent(),
                    preview = {
                        DropdownMenu {
                            DropdownMenuTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Row(
                                        horizontalArrangement =
                                            Arrangement.spacedBy(6.dp),
                                        verticalAlignment =
                                            Alignment.CenterVertically,
                                    ) {
                                        Text { "Select" }
                                        ItemDemoGlyph(Lucide.ChevronDown)
                                    }
                                }
                            }
                            DropdownMenuContent(align = PopupAlign.End, style = Style { width(192.dp) }) {
                                DropdownMenuGroup {
                                    listOf("shadcn", "maxleiter", "evilrabbit").forEach { user ->
                                        DropdownMenuItem(label = user, onClick = {}) {
                                            Item(size = ItemSize.Xs) {
                                                ItemMedia { ItemDemoPhoto(user, size = 26.dp) }
                                                ItemContent(spacing = 0.dp) {
                                                    ItemTitle(user)
                                                    ItemDescription(
                                                        user +
                                                            "@vercel.com",
                                                        textStyle =
                                                            TextStyle(
                                                                lineHeight =
                                                                    12.sp,
                                                            ),
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
                    title = "RTL",
                    descriptionMarkdown = "To enable RTL support in shadcn/ui, see the [RTL configuration guide](https://ui.shadcn.com/docs/rtl).",
                    minPreviewHeight = 352.dp,
                    mobileMinPreviewHeight = 352.dp,
                    previewPadding = 0.dp,
                    code = itemRtlRecipeCode,
                    preview = {
                        var language by remember { mutableIntStateOf(1) }
                        Column(Modifier.fillMaxWidth()) {
                            RtlPreviewToolbar(
                                language,
                                listOf("English", "Arabic (العربية)", "Hebrew (עברית)"),
                                { language = it },
                            )
                            Box(
                                Modifier.testTag("item-rtl-body").fillMaxWidth().heightIn(min = 288.dp).padding(40.dp),
                                contentAlignment = Alignment.Center,
                            ) { ItemRtlRecipe(language) }
                        }
                    },
                ),
            ),
    )

@Composable
private fun ItemDemoGlyph(
    image: ImageVector,
    size: Dp = 16.dp,
) {
    val color = LocalContentColor.current.takeIf { it.isSpecified } ?: currentTheme.colors.foreground
    Image(image, null, Modifier.size(size), colorFilter = ColorFilter.tint(color))
}

@Composable
private fun ItemDemoPhoto(
    user: String,
    size: Dp = 32.dp,
    grayscale: Boolean = true,
) {
    val resource =
        when (user) {
            "maxleiter" -> Res.drawable.avatar_maxleiter
            "evilrabbit" -> Res.drawable.avatar_evilrabbit
            else -> Res.drawable.avatar_shadcn
        }
    Avatar(
        style =
            Style {
                width(size)
                height(size)
            },
    ) {
        AvatarImage(
            painterResource(resource),
            "@" + user,
            colorFilter = if (grayscale) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }) else null,
        )
        AvatarFallback(if (user == "evilrabbit") "ER" else user.take(1))
    }
}
