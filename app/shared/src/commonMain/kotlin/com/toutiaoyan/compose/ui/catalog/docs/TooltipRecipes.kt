@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.*
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Save
import com.toutiaoyan.compose.ui.components.button.*
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.kbd.Kbd
import com.toutiaoyan.compose.ui.components.overlay.PopupPlacement
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.tooltip.*

// region shared
private val tooltipPhysicalSides =
    listOf(
        PopupPlacement.Start,
        PopupPlacement.Top,
        PopupPlacement.Bottom,
        PopupPlacement.End,
    )
// endregion shared

// region default
@Composable
internal fun TooltipDefaultRecipe() {
    TooltipProvider {
        Tooltip {
            TooltipTrigger("Hover", variant = ButtonVariant.Outline)
            TooltipContent("Add to library")
        }
    }
}
// endregion default

// region side
@Composable
internal fun TooltipSideRecipe() {
    TooltipProvider {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            tooltipPhysicalSides.zip(listOf("Left", "Top", "Bottom", "Right")).forEach { (side, label) ->
                Tooltip {
                    TooltipTrigger(label, variant = ButtonVariant.Outline)
                    TooltipContent("Add to library", side = side)
                }
            }
        }
    }
}
// endregion side

// region keyboard
@Composable
internal fun TooltipKeyboardRecipe() {
    TooltipProvider {
        Tooltip {
            TooltipTrigger {
                Button(
                    variant = ButtonVariant.Outline,
                    size = ButtonSize.IconSm,
                    modifier = Modifier.semantics { contentDescription = "Save" },
                ) {
                    Icon(Lucide.Save, Modifier.size(16.dp))
                }
            }
            TooltipContent(style = Style { contentPadding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 6.dp) }) {
                Row(
                    verticalAlignment =
                        Alignment.CenterVertically,
                    horizontalArrangement =
                        Arrangement.spacedBy(6.dp),
                ) {
                    Text { "Save Changes" }
                    Kbd("S")
                }
            }
        }
    }
}
// endregion keyboard

// region disabled
@Composable
internal fun TooltipDisabledRecipe(interactionSource: MutableInteractionSource? = null) {
    TooltipProvider {
        Tooltip(interactionSource = interactionSource) {
            TooltipTrigger(Modifier.testTag("disabled-wrapper")) {
                Button("Disabled", variant = ButtonVariant.Outline, disabled = true)
            }
            TooltipContent("This feature is currently unavailable")
        }
    }
}
// endregion disabled

// region rtl
@Composable
internal fun TooltipRtlRecipe(language: Int = 1) {
    val labels =
        listOf(
            listOf("Left", "Top", "Bottom", "Right", "Inline Start", "Inline End", "Add to library"),
            listOf("يسار", "أعلى", "أسفل", "يمين", "بداية السطر", "نهاية السطر", "إضافة إلى المكتبة"),
            listOf("שמאל", "למעלה", "למטה", "ימין", "תחילת השורה", "סוף השורה", "הוסף לספרייה"),
        )[language]
    val rtl = language != 0
    CompositionLocalProvider(LocalLayoutDirection provides if (rtl) LayoutDirection.Rtl else LayoutDirection.Ltr) {
        TooltipProvider {
            Column(
                verticalArrangement =
                    Arrangement.spacedBy(16.dp),
                horizontalAlignment =
                    Alignment.CenterHorizontally,
            ) {
                FlowRow(
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            8.dp,
                            Alignment.CenterHorizontally,
                        ),
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp),
                ) {
                    tooltipPhysicalSides.forEachIndexed { index, side ->
                        Tooltip {
                            TooltipTrigger(labels[index], variant = ButtonVariant.Outline)
                            TooltipContent(labels[6], side = side)
                        }
                    }
                }
                FlowRow(
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            8.dp,
                            Alignment.CenterHorizontally,
                        ),
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp),
                ) {
                    listOf(
                        if (rtl) {
                            PopupPlacement.End
                        } else {
                            PopupPlacement.Start
                        },
                        if (rtl) {
                            PopupPlacement.Start
                        } else {
                            PopupPlacement.End
                        },
                    )
                        .forEachIndexed { index, side ->
                            Tooltip {
                                TooltipTrigger(labels[4 + index], variant = ButtonVariant.Outline)
                                TooltipContent(labels[6], side = side)
                            }
                        }
                }
            }
        }
    }
}
// endregion rtl

internal val tooltipOfficialExamples =
    listOf(
        ComponentExample("Default", code = tooltipDefaultRecipeCode, preview = { TooltipDefaultRecipe() }),
        ComponentExample("Side", code = tooltipSideRecipeCode, preview = { TooltipSideRecipe() }),
        ComponentExample(
            "With Keyboard Shortcut",
            code = tooltipKeyboardRecipeCode,
            preview = { TooltipKeyboardRecipe() },
        ),
        ComponentExample("Disabled Button", code = tooltipDisabledRecipeCode, preview = { TooltipDisabledRecipe() }),
        ComponentExample("RTL", code = tooltipRtlRecipeCode, previewPadding = 0.dp, preview = {
            var language by remember { mutableIntStateOf(1) }
            RtlPreview(language, listOf("English", "Arabic (العربية)", "Hebrew (עברית)"), { language = it }) {
                TooltipRtlRecipe(language)
            }
        }),
    )
