@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ArrowUpRight
import com.composables.icons.lucide.BadgeCheck
import com.composables.icons.lucide.Bookmark
import com.composables.icons.lucide.Lucide
import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
import com.toutiaoyan.compose.tailwind.core.TwColors
import com.toutiaoyan.compose.ui.catalog.LocalDocNavigation
import com.toutiaoyan.compose.ui.catalog.catalogArabicFontFamily
import com.toutiaoyan.compose.ui.catalog.catalogHebrewFontFamily
import com.toutiaoyan.compose.ui.components.badge.*
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.separator.Separator
import com.toutiaoyan.compose.ui.components.spinner.Spinner
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme

@Composable
internal fun BadgeRecipe(
    kind: Int,
    language: Int = 0,
) {
    val navigation = LocalDocNavigation.current
    val words =
        when (language) {
            1 -> listOf("شارة", "ثانوي", "مدمر", "مخطط", "متحقق", "إشارة مرجعية")
            2 -> listOf("תג", "משני", "הרסני", "קווי מתאר", "מאומת", "סימנייה")
            else -> listOf("Badge", "Secondary", "Destructive", "Outline", "Verified", "Bookmark")
        }
    FlowRow(
        modifier =
            Modifier.testTag(
                "badge-recipe-$kind",
            ).then(if (kind == 0 || kind == 6) Modifier.fillMaxWidth() else Modifier.width(IntrinsicSize.Max)),
        horizontalArrangement =
            Arrangement.spacedBy(
                8.dp,
                if (kind == 0 || kind == 6) Alignment.CenterHorizontally else Alignment.Start,
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        when (kind) {
            0, 1, 6 -> {
                Badge(if (kind == 1) "Default" else words[0])
                Badge(words[1], variant = BadgeVariant.Secondary)
                Badge(words[2], variant = BadgeVariant.Destructive)
                Badge(words[3], variant = BadgeVariant.Outline)
                if (kind == 1) Badge("Ghost", variant = BadgeVariant.Ghost)
                if (kind == 6) BadgeIconPair(words[4], words[5])
            }
            2 -> BadgeIconPair("Verified", "Bookmark")
            3 -> {
                Badge(variant = BadgeVariant.Destructive, iconPlacement = BadgeIconPlacement.Start) {
                    Spinner(Modifier.testTag("badge-deleting-spinner"))
                    Text(maxLines = 1) { "Deleting" }
                }
                Badge(variant = BadgeVariant.Secondary, iconPlacement = BadgeIconPlacement.End) {
                    Text(maxLines = 1) { "Generating" }
                    Spinner(Modifier.testTag("badge-generating-spinner"))
                }
            }
            4 ->
                Badge(
                    href = "#link",
                    onClick = { navigation?.navigate("link") },
                    iconPlacement = BadgeIconPlacement.End,
                    modifier = Modifier.testTag("badge-link"),
                ) {
                    Text(maxLines = 1) { "Open Link" }
                    Icon(Lucide.ArrowUpRight)
                }
            5 -> {
                val colors =
                    if (currentTheme.isDark) {
                        listOf(
                            TwColors.blue950 to TwColors.blue300,
                            TwColors.green950 to TwColors.green300,
                            TwColors.sky950 to TwColors.sky300,
                            TwColors.purple950 to TwColors.purple300,
                            TwColors.red950 to TwColors.red300,
                        )
                    } else {
                        listOf(
                            TwColors.blue50 to TwColors.blue700,
                            TwColors.green50 to TwColors.green700,
                            TwColors.sky50 to TwColors.sky700,
                            TwColors.purple50 to TwColors.purple700,
                            TwColors.red50 to TwColors.red700,
                        )
                    }
                listOf("Blue", "Green", "Sky", "Purple", "Red").forEachIndexed { index, label ->
                    Badge(
                        label,
                        modifier = Modifier.testTag("badge-color-$index"),
                        style =
                            Style {
                                background(colors[index].first)
                                contentColor(colors[index].second)
                            },
                    )
                }
            }
        }
    }
}

@Composable
private fun BadgeIconPair(
    verified: String,
    bookmark: String,
) {
    Badge(variant = BadgeVariant.Secondary, iconPlacement = BadgeIconPlacement.Start) {
        Icon(Lucide.BadgeCheck, modifier = Modifier.testTag("badge-leading-icon"))
        Text(modifier = Modifier.testTag("badge-verified-label"), maxLines = 1) { verified }
    }
    Badge(variant = BadgeVariant.Outline, iconPlacement = BadgeIconPlacement.End) {
        Text(maxLines = 1) { bookmark }
        Icon(Lucide.Bookmark, modifier = Modifier.testTag("badge-trailing-icon"))
    }
}

@Composable
internal fun BadgeRtlRecipe() {
    var language by remember { mutableIntStateOf(1) }
    Column(Modifier.fillMaxWidth()) {
        RtlPreviewToolbar(language, listOf("English", "Arabic (العربية)", "Hebrew (עברית)"), { language = it })
        Separator()
        Box(Modifier.fillMaxWidth().height(288.dp).padding(40.dp), contentAlignment = Alignment.Center) {
            val inherited = currentTheme
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
                    isDark = inherited.isDark,
                    baseRadius = inherited.baseRadius,
                    ring = inherited.ring,
                    unitSpacing = LocalUnitSpacing.current,
                    minTouchTarget = inherited.minTouchTarget,
                    fontFamily = font,
                ) { BadgeRecipe(6, language) }
            }
        }
    }
}
