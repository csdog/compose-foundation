@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.*
import com.composables.icons.lucide.*
import com.toutiaoyan.compose.ui.components.avatar.*
import com.toutiaoyan.compose.ui.components.button.*
import com.toutiaoyan.compose.ui.components.empty.*
import com.toutiaoyan.compose.ui.components.inputgroup.*
import com.toutiaoyan.compose.ui.components.kbd.Kbd
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
import com.toutiaoyan.compose.ui.catalog.LocalDocNavigation
import com.toutiaoyan.compose.ui.catalog.catalogArabicFontFamily
import com.toutiaoyan.compose.ui.catalog.catalogHebrewFontFamily
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import compose_foundation.app.shared.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

internal val emptyAdditionalExamples =
    listOf(
        ComponentExample(
            title = "Avatar",
            descriptionMarkdown = "Display an avatar in the empty state.",
            minPreviewHeight = 384.dp,
            mobileMinPreviewHeight = 384.dp,
            previewPadding = 0.dp,
            code =
                """
                Empty(modifier = Modifier.fillMaxWidth().heightIn(min = 384.dp)) {
                    EmptyHeader {
                        EmptyMedia { EmptyPhoto(Res.drawable.avatar_shadcn, "@shadcn", "LR") }
                        EmptyTitle("User Offline")
                        EmptyDescription("This user is currently offline. You can leave a message to notify them or try again later.")
                    }
                    EmptyContent { Button("Leave Message", onClick = {}, size = ButtonSize.Sm) }
                }

                @Composable
                private fun EmptyPhoto(resource: DrawableResource, name: String, fallback: String, modifier: Modifier = Modifier) {
                    Avatar(modifier, style = Style { width(48.dp); height(48.dp) }) {
                        AvatarImage(painterResource(resource), name,
                            colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }))
                        AvatarFallback(fallback)
                    }
                }
                """.trimIndent(),
            preview = {
                Empty(modifier = Modifier.fillMaxWidth().heightIn(min = 384.dp)) {
                    EmptyHeader {
                        EmptyMedia { EmptyPhoto(Res.drawable.avatar_shadcn, "@shadcn", "LR") }
                        EmptyTitle("User Offline")
                        EmptyDescription(
                            (
                                "This user is currently offline. You can leave a message to " +
                                    "notify them or try again later."
                            ),
                        )
                    }
                    EmptyContent { Button("Leave Message", onClick = {}, size = ButtonSize.Sm) }
                }
            },
        ),
        ComponentExample(
            title = "Avatar Group",
            descriptionMarkdown = "Display a group of avatars in the empty state.",
            minPreviewHeight = 384.dp,
            mobileMinPreviewHeight = 384.dp,
            previewPadding = 0.dp,
            code =
                """
                Empty(modifier = Modifier.fillMaxWidth().heightIn(min = 384.dp)) {
                    EmptyHeader {
                        EmptyMedia {
                            AvatarGroup {
                                EmptyPhoto(Res.drawable.avatar_shadcn, "@shadcn", "CN")
                                EmptyPhoto(Res.drawable.avatar_maxleiter, "@maxleiter", "LR")
                                EmptyPhoto(Res.drawable.avatar_evilrabbit, "@evilrabbit", "ER")
                            }
                        }
                        EmptyTitle("No Team Members")
                        EmptyDescription("Invite your team to collaborate on this project.")
                    }
                    EmptyContent {
                        Button(onClick = {}, size = ButtonSize.Sm) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                EmptyAdditionalGlyph(Lucide.Plus)
                                Text { "Invite Members" }
                            }
                        }
                    }
                }

                @Composable
                private fun EmptyPhoto(resource: DrawableResource, name: String, fallback: String, modifier: Modifier = Modifier) {
                    Avatar(modifier, style = Style { width(48.dp); height(48.dp) }) {
                        AvatarImage(painterResource(resource), name,
                            colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }))
                        AvatarFallback(fallback)
                    }
                }

                @Composable
                private fun EmptyAdditionalGlyph(image: ImageVector, modifier: Modifier = Modifier, tint: Color = Color.Unspecified) {
                    Image(image, null, modifier.size(16.dp), colorFilter = ColorFilter.tint(if (tint.isSpecified) tint else LocalContentColor.current.takeIf { it.isSpecified } ?: currentTheme.colors.foreground))
                }
                """.trimIndent(),
            preview = {
                Empty(modifier = Modifier.fillMaxWidth().heightIn(min = 384.dp)) {
                    EmptyHeader {
                        EmptyMedia {
                            AvatarGroup {
                                EmptyPhoto(Res.drawable.avatar_shadcn, "@shadcn", "CN")
                                EmptyPhoto(Res.drawable.avatar_maxleiter, "@maxleiter", "LR")
                                EmptyPhoto(Res.drawable.avatar_evilrabbit, "@evilrabbit", "ER")
                            }
                        }
                        EmptyTitle("No Team Members")
                        EmptyDescription("Invite your team to collaborate on this project.")
                    }
                    EmptyContent {
                        Button(onClick = {}, size = ButtonSize.Sm) {
                            Row(
                                horizontalArrangement =
                                    Arrangement.spacedBy(6.dp),
                                verticalAlignment =
                                    Alignment.CenterVertically,
                            ) {
                                EmptyAdditionalGlyph(Lucide.Plus)
                                Text { "Invite Members" }
                            }
                        }
                    }
                }
            },
        ),
        ComponentExample(
            title = "InputGroup",
            descriptionMarkdown = "Add an InputGroup to the EmptyContent.",
            minPreviewHeight = 384.dp,
            mobileMinPreviewHeight = 384.dp,
            previewPadding = 0.dp,
            code =
                """
                var query by remember { mutableStateOf("") }
                val width = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width.toDp() }
                val colors = currentTheme.colors
                Empty(modifier = Modifier.fillMaxWidth().heightIn(min = 384.dp)) {
                    EmptyHeader {
                        EmptyTitle("404 - Not Found")
                        EmptyDescription("The page you're looking for doesn't exist. Try searching for what you need below.")
                    }
                    EmptyContent {
                        InputGroup(modifier = Modifier.fillMaxWidth(if (width >= 640.dp) 0.75f else 1f)) {
                            InputGroupInput(query, { query = it }, placeholder = "Try searching for pages...")
                            InputGroupAddon { EmptyAdditionalGlyph(Lucide.Search) }
                            InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) { Kbd("/") }
                        }
                        EmptyDescription(buildAnnotatedString {
                            append("Need help? ")
                            withLink(LinkAnnotation.Url("#", TextLinkStyles(
                                style = SpanStyle(textDecoration = TextDecoration.Underline),
                                hoveredStyle = SpanStyle(color = colors.primary),
                            ))) { append("Contact support") }
                        })
                    }
                }

                @Composable
                private fun EmptyAdditionalGlyph(image: ImageVector, modifier: Modifier = Modifier, tint: Color = Color.Unspecified) {
                    Image(image, null, modifier.size(16.dp), colorFilter = ColorFilter.tint(if (tint.isSpecified) tint else LocalContentColor.current.takeIf { it.isSpecified } ?: currentTheme.colors.foreground))
                }
                """.trimIndent(),
            preview = {
                var query by remember { mutableStateOf("") }
                val width = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width.toDp() }
                val colors = currentTheme.colors
                val navigation = LocalDocNavigation.current
                val uri = LocalUriHandler.current
                Empty(modifier = Modifier.fillMaxWidth().heightIn(min = 384.dp)) {
                    EmptyHeader {
                        EmptyTitle("404 - Not Found")
                        EmptyDescription(
                            (
                                "The page you're looking for doesn't exist. Try searching for " +
                                    "what you need below."
                            ),
                        )
                    }
                    EmptyContent {
                        InputGroup(modifier = Modifier.fillMaxWidth(if (width >= 640.dp) 0.75f else 1f)) {
                            InputGroupInput(query, { query = it }, placeholder = "Try searching for pages...")
                            InputGroupAddon { EmptyAdditionalGlyph(Lucide.Search) }
                            InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) { Kbd("/") }
                        }
                        EmptyDescription(
                            buildAnnotatedString {
                                append("Need help? ")
                                withLink(
                                    LinkAnnotation.Url(
                                        "#",
                                        TextLinkStyles(
                                            style = SpanStyle(textDecoration = TextDecoration.Underline),
                                            hoveredStyle = SpanStyle(color = colors.primary),
                                        ),
                                        linkInteractionListener = { navigation?.navigate("") ?: uri.openUri("#") },
                                    ),
                                ) { append("Contact support") }
                            },
                        )
                    }
                }
            },
        ),
        ComponentExample(
            title = "RTL",
            descriptionMarkdown = "Set the layout direction for the selected language.",
            minPreviewHeight = 352.dp,
            mobileMinPreviewHeight = 352.dp,
            previewPadding = 0.dp,
            code = emptyRtlCode + "\n\n" + emptyMediaIconCode + "\n\n" + emptyLearnMoreCode,
            preview = { EmptyRtlRecipe() },
        ),
    )

@Composable
private fun EmptyPhoto(
    resource: DrawableResource,
    name: String,
    fallback: String,
    modifier: Modifier = Modifier,
) {
    Avatar(
        modifier,
        style =
            Style {
                width(48.dp)
                height(48.dp)
            },
    ) {
        AvatarImage(
            painterResource(resource),
            name,
            colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }),
        )
        AvatarFallback(fallback)
    }
}

@Composable
private fun EmptyAdditionalGlyph(
    image: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
) {
    Image(
        image,
        null,
        modifier.size(16.dp),
        colorFilter =
            ColorFilter.tint(
                if (tint.isSpecified) {
                    tint
                } else {
                    LocalContentColor.current.takeIf { it.isSpecified }
                        ?: currentTheme.colors.foreground
                },
            ),
    )
}

private val emptyTranslations =
    listOf(
        listOf(
            "No Projects Yet",
            "You haven't created any projects yet. Get started by creating your first project.",
            "Create Project",
            "Import Project",
            "Learn More",
        ),
        listOf(
            "لا توجد مشاريع بعد",
            "لم تقم بإنشاء أي مشاريع بعد. ابدأ بإنشاء مشروعك الأول.",
            "إنشاء مشروع",
            "استيراد مشروع",
            "تعرف على المزيد",
        ),
        listOf(
            "אין פרויקטים עדיין",
            "עדיין לא יצרת פרויקטים. התחל על ידי יצירת הפרויקט הראשון שלך.",
            "צור פרויקט",
            "ייבא פרויקט",
            "למד עוד",
        ),
    )

@Composable
private fun EmptyRtlRecipe() {
    var language by remember { mutableStateOf(1) }
    val inherited = currentTheme
    Column(Modifier.fillMaxWidth()) {
        RtlPreviewToolbar(language, listOf("English", "Arabic (العربية)", "Hebrew (עברית)"), { language = it })
        Box(
            Modifier.testTag(
                "empty-rtl-body",
            ).fillMaxWidth().height(288.dp).padding(40.dp).wrapContentHeight(unbounded = true),
            contentAlignment = Alignment.Center,
        ) {
            val font =
                when (language) {
                    1 -> catalogArabicFontFamily()
                    2 -> catalogHebrewFontFamily()
                    else -> inherited.fontFamily
                }
            CompositionLocalProvider(
                LocalLayoutDirection provides if (language == 0) LayoutDirection.Ltr else LayoutDirection.Rtl,
            ) {
                Theme(
                    preset = inherited.activePreset,
                    baseColor = inherited.activeBaseColor,
                    accent = inherited.activeAccent,
                    isDark = inherited.isDark, baseRadius = inherited.baseRadius, ring = inherited.ring,
                    unitSpacing = LocalUnitSpacing.current,
                    minTouchTarget = inherited.minTouchTarget,
                    fontFamily = font,
                ) {
                    val t = emptyTranslations[language]
                    Empty {
                        EmptyHeader {
                            EmptyMedia(variant = EmptyMediaVariant.Icon) { EmptyAdditionalGlyph(EmptyFolderCode) }
                            EmptyTitle(t[0])
                            EmptyDescription(t[1])
                        }
                        EmptyContent {
                            Row(
                                Modifier.wrapContentWidth(unbounded = true),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Button(t[2], onClick = {})
                                Button(t[3], onClick = {}, variant = ButtonVariant.Outline)
                            }
                        }
                        EmptyLearnMore(t[4], rtl = language != 0, inlineEnd = true)
                    }
                }
            }
        }
    }
}
