@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.components.separator.Separator
import com.toutiaoyan.compose.ui.components.separator.SeparatorOrientation
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
import com.toutiaoyan.compose.ui.catalog.catalogViewportWidth
import com.toutiaoyan.compose.ui.catalog.catalogArabicFontFamily
import com.toutiaoyan.compose.ui.catalog.catalogHebrewFontFamily

// region shared
@Composable
private fun SeparatorRecipeText(
    value: String,
    modifier: Modifier = Modifier,
    muted: Boolean = false,
    small: Boolean = false,
    medium: Boolean = false,
) {
    Text(
        modifier = modifier,
        textStyle =
            TextStyle(
                fontSize = if (small)12.sp else 14.sp,
                lineHeight =
                    if (small) {
                        16.sp
                    } else {
                        20.sp
                    },
                color =
                    if (muted) {
                        currentTheme.colors.mutedForeground
                    } else {
                        currentTheme.colors.foreground
                    },
                fontWeight = if (medium)FontWeight.Medium else FontWeight.Normal,
                lineHeightStyle =
                    LineHeightStyle(
                        LineHeightStyle.Alignment.Center,
                        LineHeightStyle.Trim.None,
                        LineHeightStyle.Mode.Tight,
                    ),
            ),
    ) { value }
}

@Composable
private fun SeparatorIntro(
    subtitle: String,
    description: String,
) {
    Column(
        Modifier.widthIn(max = 384.dp).fillMaxWidth().testTag("separator-example"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SeparatorRecipeText(
                "shadcn/ui",
                medium = true,
                modifier =
                    Modifier.layout { measurable, constraints ->
                        val glyph = measurable.measure(constraints.copy(minHeight = 0))
                        val height = constraints.constrainHeight(14.sp.roundToPx())
                        layout(glyph.width, height) { glyph.placeRelative(0, (height - glyph.height) / 2) }
                    },
            )
            SeparatorRecipeText(subtitle, muted = true)
        }
        Separator(modifier = Modifier.testTag("separator-line"))
        SeparatorRecipeText(description)
    }
}
// endregion shared

// region default
@Composable
internal fun SeparatorDefaultRecipe() =
    SeparatorIntro(
        "The Foundation for your Design System",
        "A set of beautifully designed components that you can customize, extend, and build on.",
    )
// endregion default

// region vertical
@Composable
internal fun SeparatorVerticalRecipe() {
    Row(
        Modifier.height(20.dp).testTag("separator-example"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SeparatorRecipeText("Blog")
        Separator(orientation = SeparatorOrientation.Vertical, modifier = Modifier.testTag("separator-first"))
        SeparatorRecipeText("Docs")
        Separator(orientation = SeparatorOrientation.Vertical, modifier = Modifier.testTag("separator-second"))
        SeparatorRecipeText("Source")
    }
}
// endregion vertical

// region menu
@Composable
internal fun SeparatorMenuRecipe(
    viewportWidth: Dp = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width.toDp() },
) {
    val desktop = viewportWidth >= 768.dp
    Row(
        Modifier.height(IntrinsicSize.Min).testTag("separator-example"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(if (desktop)16.dp else 8.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            SeparatorRecipeText("Settings", medium = true)
            SeparatorRecipeText("Manage preferences", muted = true, small = true)
        }
        Separator(orientation = SeparatorOrientation.Vertical, modifier = Modifier.testTag("separator-first"))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            SeparatorRecipeText("Account", medium = true)
            SeparatorRecipeText("Profile & security", muted = true, small = true)
        }
        if (desktop) {
            Separator(orientation = SeparatorOrientation.Vertical, modifier = Modifier.testTag("separator-second"))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                SeparatorRecipeText("Help", medium = true)
                SeparatorRecipeText("Support & docs", muted = true, small = true)
            }
        }
    }
}
// endregion menu

// region list
@Composable
internal fun SeparatorListRecipe() {
    Column(
        Modifier.widthIn(max = 384.dp).fillMaxWidth().testTag("separator-example"),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        (1..3).forEach { index ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SeparatorRecipeText("Item $index")
                SeparatorRecipeText("Value $index", muted = true)
            }
            if (index < 3)Separator(modifier = Modifier.testTag("separator-list-$index"))
        }
    }
}
// endregion list

// region rtl
@Composable
internal fun SeparatorRtlRecipe(language: Int = 1) {
    val text =
        listOf(
            "The Foundation for your Design System" to (
                "A set of beautifully designed components that you can " +
                    "customize, extend, and build on."
            ),
            "الأساس لنظام التصميم الخاص بك" to (
                "مجموعة من المكونات المصممة بشكل جميل يمكنك تخصيصها وتوسيعها " +
                    "والبناء عليها."
            ),
            "הבסיס למערכת העיצוב שלך" to "סט של רכיבים מעוצבים בצורה יפה שאתה יכול להתאים אישית, להרחיב ולבנות עליהם.",
        )
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
            preset = inherited.activePreset, baseColor = inherited.activeBaseColor,
            accent = inherited.activeAccent, isDark = inherited.isDark, baseRadius = inherited.baseRadius,
            ring = inherited.ring, unitSpacing = LocalUnitSpacing.current,
            minTouchTarget = inherited.minTouchTarget, fontFamily = font,
        ) {
            SeparatorIntro(text[language].first, text[language].second)
        }
    }
}
// endregion rtl

internal val separatorOfficialExamples =
    listOf(
        ComponentExample("Default", code = separatorDefaultRecipeCode, preview = { SeparatorDefaultRecipe() }),
        ComponentExample(
            "Vertical",
            code = separatorVerticalRecipeCode,
            descriptionMarkdown = "Set orientation to Vertical to create a vertical separator.",
            preview = { SeparatorVerticalRecipe() },
        ),
        ComponentExample(
            "Menu",
            code = separatorMenuRecipeCode,
            descriptionMarkdown = "Use vertical separators to divide groups of content in a menu.",
            preview = { SeparatorMenuRecipe(catalogViewportWidth()) },
        ),
        ComponentExample(
            "List",
            code = separatorListRecipeCode,
            descriptionMarkdown = "Use separators to divide items in a list.",
            preview = { SeparatorListRecipe() },
        ),
        ComponentExample(
            "RTL",
            code = separatorRtlRecipeCode,
            descriptionMarkdown =
                "To enable RTL support in shadcn/ui, see the " +
                    "[RTL configuration guide](https://ui.shadcn.com/docs/rtl).",
            previewPadding = 0.dp,
            preview = {
                var language by remember { mutableIntStateOf(1) }
                Column(Modifier.fillMaxWidth()) {
                    RtlPreviewToolbar(
                        language,
                        listOf("English", "Arabic (العربية)", "Hebrew (עברית)"),
                        { language = it },
                    )
                    Box(
                        Modifier.testTag("separator-rtl-body").fillMaxWidth().heightIn(min = 288.dp).padding(40.dp),
                        contentAlignment = Alignment.Center,
                    ) { SeparatorRtlRecipe(language) }
                }
            },
        ),
    )
