@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
import com.toutiaoyan.compose.ui.catalog.catalogArabicFontFamily
import com.toutiaoyan.compose.ui.catalog.catalogHebrewFontFamily
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme

internal val selectAlignItemExample =
    ComponentExample(
        title = "Align Item With Trigger",
        descriptionMarkdown =
            (
                "Use `alignItemWithTrigger` on `SelectContent` to control whether the selected " +
                    "item aligns with the trigger. When `true` (default), the popup positions so " +
                    "the selected item appears over the trigger. When `false`, the popup aligns " +
                    "to the trigger edge."
            ),
        code = selectRecipeCode.getValue("Align Item With Trigger"),
        preview = { SelectAlignItemPreview() },
    )

internal val selectGroupsExample =
    ComponentExample(
        title = "Groups",
        descriptionMarkdown = "Use `SelectGroup`, `SelectLabel`, and `SelectSeparator` to organize items.",
        code = selectRecipeCode.getValue("Groups"),
        preview = { SelectGroupsPreview() },
    )

internal val selectScrollableExample =
    ComponentExample(
        title = "Scrollable",
        descriptionMarkdown = "A select with many items that scrolls.",
        code = selectRecipeCode.getValue("Scrollable"),
        preview = { SelectScrollablePreview() },
    )

internal val selectDisabledExample =
    ComponentExample(
        title = "Disabled",
        code = selectRecipeCode.getValue("Disabled"),
        preview = { SelectDisabledPreview() },
    )

internal val selectInvalidExample =
    ComponentExample(
        title = "Invalid",
        descriptionMarkdown =
            (
                "Add the `data-invalid` attribute to the `Field` component and the `aria-invalid` " +
                    "attribute to the `SelectTrigger` component to show an error state. In Compose, " +
                    "`Field(invalid = true)` and `Select(invalid = true)` expose the same state."
            ),
        code = selectRecipeCode.getValue("Invalid"),
        preview = { SelectInvalidPreview() },
    )

internal val selectRtlExample =
    ComponentExample(
        title = "RTL",
        descriptionMarkdown =
            "To enable RTL support in shadcn/ui, see the " +
                "[RTL configuration guide](https://ui.shadcn.com/docs/rtl).",
        previewPadding = 0.dp,
        code = selectRecipeCode.getValue("RTL"),
        preview = { SelectRtlRecipe() },
    )

@Composable
private fun SelectRtlRecipe() {
    var language by remember { mutableIntStateOf(1) }
    val inherited = currentTheme
    RtlPreview(language, listOf("English", "Arabic (العربية)", "Hebrew (עברית)"), { language = it }) {
        val font =
            when (language) {
                1 -> catalogArabicFontFamily()
                2 -> catalogHebrewFontFamily()
                else -> inherited.fontFamily
            }
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
            SelectRtlContent(language)
        }
    }
}

/** Official seven sections first, then the existing Compose-only examples. */
internal fun selectPageExamples(native: List<ComponentExample>): List<ComponentExample> {
    val byTitle = native.associateBy { it.title }
    val promoted = setOf("Default")
    return listOf(
        byTitle.getValue("Default"),
        selectAlignItemExample,
        selectGroupsExample,
        selectScrollableExample,
        selectDisabledExample,
        selectInvalidExample,
        selectRtlExample,
    ) +
        native
            .filterNot { it.title in promoted }
            .map { it.copy(title = "Kotlin extension: " + it.title) }
}
