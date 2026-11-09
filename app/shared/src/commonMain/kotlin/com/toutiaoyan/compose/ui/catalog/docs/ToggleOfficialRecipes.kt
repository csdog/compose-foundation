@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Bookmark
import com.composables.icons.lucide.Lucide
import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
import com.toutiaoyan.compose.ui.catalog.catalogArabicFontFamily
import com.toutiaoyan.compose.ui.catalog.catalogHebrewFontFamily
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.toggle.Toggle
import com.toutiaoyan.compose.ui.components.toggle.ToggleIconPlacement
import com.toutiaoyan.compose.ui.components.toggle.ToggleSize
import com.toutiaoyan.compose.ui.components.toggle.ToggleVariant
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme

private val toggleRtlLabels =
    listOf(
        listOf("Bookmark", "Toggle bookmark"),
        listOf("إشارة مرجعية", "تبديل الإشارة المرجعية"),
        listOf("סימנייה", "החלף סימנייה"),
    )

private val toggleRtlExample =
    ComponentExample(
        title = "RTL",
        descriptionMarkdown =
            (
                "To enable RTL support in shadcn/ui, see the [RTL configuration guide](https://ui.shadcn.com/docs/rtl)."
            ),
        previewPadding = 0.dp,
        code =
            """
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Theme(fontFamily = catalogArabicFontFamily()) {
                    var bookmarked by remember { mutableStateOf(false) }
                    Toggle(
                        pressed = bookmarked,
                        onPressedChange = { bookmarked = it },
                        label = "تبديل الإشارة المرجعية",
                        variant = ToggleVariant.Outline,
                        size = ToggleSize.Sm,
                        iconPlacement = ToggleIconPlacement.Start,
                    ) {
                        Icon(Lucide.Bookmark)
                        Text { "إشارة مرجعية" }
                    }
                }
            }
            """.trimIndent(),
        preview = { ToggleRtlRecipe() },
    )

@Composable
private fun ToggleRtlRecipe() {
    var language by remember { mutableIntStateOf(1) }
    val inherited = currentTheme
    RtlPreview(language, listOf("English", "Arabic (العربية)", "Hebrew (עברית)"), { language = it }) {
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
            ) {
                val labels = toggleRtlLabels[language]
                var bookmarked by remember(language) { mutableStateOf(false) }
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Toggle(
                        pressed = bookmarked,
                        onPressedChange = { bookmarked = it },
                        label = labels[1],
                        variant = ToggleVariant.Outline,
                        size = ToggleSize.Sm,
                        iconPlacement = ToggleIconPlacement.Start,
                    ) {
                        Icon(Lucide.Bookmark)
                        Text { labels[0] }
                    }
                }
            }
        }
    }
}

/** Official six sections first, then the existing Compose-only examples. */
internal fun togglePageExamples(native: List<ComponentExample>): List<ComponentExample> {
    val byTitle = native.associateBy { it.title }
    val promoted = setOf("Default", "Outline", "With text", "Sizes", "Disabled")
    return listOf(
        byTitle.getValue("Default"),
        byTitle.getValue("Outline").copy(
            descriptionMarkdown = "Use `variant = ToggleVariant.Outline` for an outline style.",
        ),
        byTitle.getValue("With text").copy(title = "With Text"),
        byTitle.getValue("Sizes").copy(
            title = "Size",
            descriptionMarkdown = "Use the `size` prop to change the size of the toggle.",
        ),
        byTitle.getValue("Disabled"),
        toggleRtlExample,
    ) +
        native
            .filterNot { it.title in promoted }
            .map { it.copy(title = "Kotlin extension: " + it.title) }
}
