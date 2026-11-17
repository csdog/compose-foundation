@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.*
import com.composables.icons.lucide.*
import com.toutiaoyan.compose.ui.components.button.*
import com.toutiaoyan.compose.ui.components.item.*
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
import com.toutiaoyan.compose.ui.catalog.catalogArabicFontFamily
import com.toutiaoyan.compose.ui.catalog.catalogHebrewFontFamily

@Composable
internal fun ItemRtlRecipe(
    openOnSamePage: () -> Unit,
    language: Int = 1,
) {
    val inherited = currentTheme
    val font =
        when (language) {
            1 -> catalogArabicFontFamily()
            2 -> catalogHebrewFontFamily()
            else -> inherited.fontFamily
        }
    val t = itemTranslations[language]
    CompositionLocalProvider(
        LocalLayoutDirection provides if (language == 0) LayoutDirection.Ltr else LayoutDirection.Rtl,
    ) {
        Theme(
            preset = inherited.activePreset, baseColor = inherited.activeBaseColor,
            accent = inherited.activeAccent, isDark = inherited.isDark, baseRadius = inherited.baseRadius,
            ring = inherited.ring, unitSpacing = LocalUnitSpacing.current,
            minTouchTarget = inherited.minTouchTarget, fontFamily = font,
        ) {
            Column(
                Modifier.testTag("item-rtl-content").widthIn(max = 448.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Item(variant = ItemVariant.Outline) {
                    ItemContent {
                        ItemTitle(t.getValue("basicItem"))
                        ItemDescription(t.getValue("basicItemDesc"))
                    }
                    ItemActions {
                        Button(
                            t.getValue("action"),
                            onClick = {},
                            variant = ButtonVariant.Outline,
                            size = ButtonSize.Sm,
                        )
                    }
                }
                Item(variant = ItemVariant.Outline, size = ItemSize.Sm, href = "#", onClick = openOnSamePage) {
                    ItemMedia { ItemRtlGlyph(Lucide.BadgeCheck, 20.dp) }
                    ItemContent { ItemTitle(t.getValue("verifiedTitle")) }
                    ItemActions { ItemRtlGlyph(Lucide.ChevronRight) }
                }
            }
        }
    }
}

@Composable
private fun ItemRtlGlyph(
    image: ImageVector,
    size: Dp = 16.dp,
) {
    val color = LocalContentColor.current.takeIf { it.isSpecified } ?: currentTheme.colors.foreground
    Image(image, null, Modifier.size(size), colorFilter = ColorFilter.tint(color))
}

private val itemTranslations =
    listOf(
        mapOf(
            "basicItem" to "Basic Item",
            "basicItemDesc" to "A simple item with title and description.",
            "action" to "Action",
            "verifiedTitle" to "Your profile has been verified.",
        ),
        mapOf(
            "basicItem" to "عنصر أساسي",
            "basicItemDesc" to "عنصر بسيط يحتوي على عنوان ووصف.",
            "action" to "إجراء",
            "verifiedTitle" to "تم التحقق من ملفك الشخصي.",
        ),
        mapOf(
            "basicItem" to "פריט בסיסי",
            "basicItemDesc" to "פריט פשוט עם כותרת ותיאור.",
            "action" to "פעולה",
            "verifiedTitle" to "הפרופיל שלך אומת.",
        ),
    )
