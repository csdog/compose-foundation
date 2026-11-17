@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.field.*
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.label.Label
import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
import com.toutiaoyan.compose.ui.components.overlay.PopupPlacement
import com.toutiaoyan.compose.ui.components.popover.*
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors

// region shared
private const val popoverDescription = "Set the dimensions for the layer."

@Composable
private fun PopoverDimensionInput(
    label: String,
    initialValue: String,
    labelWidth: Dp,
    field: Boolean = false,
) {
    var value by remember { mutableStateOf(initialValue) }
    val focus = remember { FocusRequester() }
    val labelModifier =
        Modifier.width(labelWidth).focusProperties { canFocus = false }.clickable { focus.requestFocus() }
    if (field) FieldLabel(label, modifier = labelModifier) else Label(label, modifier = labelModifier)
    Input(
        value,
        onValueChange = { value = it },
        modifier = Modifier.focusRequester(focus).semantics { contentDescription = label },
        style = Style { height(32.dp) },
    )
}
// endregion shared

// region default
@Composable
internal fun PopoverDefaultRecipe() {
    Popover(modal = false) {
        PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
        PopoverContent(style = Style { width(320.dp) }, modifier = Modifier.testTag("popover-panel")) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        modifier =
                            Modifier.semantics { heading() }.layout { measurable, constraints ->
                                // CSS leading-none permits ink outside its14sp line box; keep the glyph uncut.
                                val glyph = measurable.measure(constraints.copy(minHeight = 0))
                                val lineHeight = constraints.constrainHeight(14.sp.roundToPx())
                                layout(glyph.width, lineHeight) {
                                    glyph.placeRelative(0, (lineHeight - glyph.height) / 2)
                                }
                            },
                        textStyle =
                            TextStyle(
                                fontSize = 14.sp,
                                lineHeight = 14.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeightStyle =
                                    LineHeightStyle(
                                        LineHeightStyle.Alignment.Center,
                                        LineHeightStyle.Trim.None,
                                    ),
                            ),
                    ) { "Dimensions" }
                    Text(textStyle = TextColors.Muted) { popoverDescription }
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Width" to "100%", "Max. width" to "300px", "Height" to "25px", "Max. height" to "none")
                        .forEach { (label, value) ->
                            BoxWithConstraints(Modifier.fillMaxWidth()) {
                                val labelWidth = (maxWidth - 32.dp) / 3
                                Row(
                                    modifier = Modifier.testTag("popover-row-" + label),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    // Three equal grid columns; the input spans two columns and the inner gap.
                                    PopoverDimensionInput(label, value, labelWidth)
                                }
                            }
                        }
                }
            }
        }
    }
}
// endregion default

// region basic
@Composable
internal fun PopoverBasicRecipe() {
    Popover(modal = false) {
        PopoverTrigger("Open Popover", variant = ButtonVariant.Outline)
        PopoverContent(align = PopupAlign.Start, modifier = Modifier.testTag("popover-panel")) {
            PopoverHeader("Dimensions", popoverDescription)
        }
    }
}
// endregion basic

// region align
@Composable
internal fun PopoverAlignRecipe() {
    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        listOf(
            "Start" to PopupAlign.Start,
            "Center" to PopupAlign.Center,
            "End" to PopupAlign.End,
        ).forEach {
                (
                    label,
                    align,
                ),
            ->
            Popover(modal = false) {
                PopoverTrigger(label, variant = ButtonVariant.Outline, size = ButtonSize.Sm)
                PopoverContent(
                    "Aligned to " + label.lowercase(),
                    align = align,
                    style = Style { width(160.dp) },
                    modifier = Modifier.testTag("popover-panel"),
                )
            }
        }
    }
}
// endregion align

// region form
@Composable
internal fun PopoverFormRecipe() {
    Popover(modal = false) {
        PopoverTrigger("Open Popover", variant = ButtonVariant.Outline)
        PopoverContent(
            align = PopupAlign.Start,
            style = Style { width(256.dp) },
            modifier = Modifier.testTag("popover-panel"),
        ) {
            PopoverHeader("Dimensions", popoverDescription)
            // FieldGroup's default gap is20dp; the source recipe explicitly overrides it to16dp.
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                listOf("Width" to "100%", "Height" to "25px").forEach { (label, value) ->
                    BoxWithConstraints(Modifier.fillMaxWidth()) {
                        Field(
                            modifier =
                                Modifier.testTag(
                                    "popover-row-" +
                                        label,
                                ),
                            orientation =
                                FieldOrientation.Horizontal,
                        ) {
                            PopoverDimensionInput(label, value, maxWidth / 2, field = true)
                        }
                    }
                }
            }
        }
    }
}
// endregion form

// region rtl
private val popoverTranslations =
    listOf(
        listOf(
            "Dimensions",
            "Set the dimensions for the layer.",
            "Left",
            "Top",
            "Bottom",
            "Right",
            "Inline Start",
            "Inline End",
        ),
        listOf("الأبعاد", "تعيين الأبعاد للطبقة.", "يسار", "أعلى", "أسفل", "يمين", "بداية السطر", "نهاية السطر"),
        listOf("מימדים", "הגדר את המימדים לשכבה.", "שמאל", "למעלה", "למטה", "ימין", "תחילת השורה", "סוף השורה"),
    )

@Composable
internal fun PopoverRtlRecipe(language: Int = 1) {
    val translations = popoverTranslations[language]
    val direction = if (language == 0) LayoutDirection.Ltr else LayoutDirection.Rtl
    val left = if (direction == LayoutDirection.Rtl) PopupPlacement.End else PopupPlacement.Start
    val right = if (direction == LayoutDirection.Rtl) PopupPlacement.Start else PopupPlacement.End
    CompositionLocalProvider(LocalLayoutDirection provides direction) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            listOf(
                listOf(left, PopupPlacement.Top, PopupPlacement.Bottom, right),
                listOf(PopupPlacement.Start, PopupPlacement.End),
            ).forEachIndexed { row, sides ->
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    sides.forEachIndexed { index, side ->
                        Popover(modal = false) {
                            PopoverTrigger(
                                translations[
                                    if (row == 0) {
                                        index +
                                            2
                                    } else {
                                        index +
                                            6
                                    },
                                ],
                                variant =
                                    ButtonVariant.Outline,
                            )
                            PopoverContent(side = side, modifier = Modifier.testTag("popover-panel")) {
                                PopoverHeader(translations[0], translations[1])
                            }
                        }
                    }
                }
            }
        }
    }
}
// endregion rtl

internal val popoverOfficialExamples =
    listOf(
        ComponentExample("Default", code = popoverDefaultRecipeCode, preview = { PopoverDefaultRecipe() }),
        ComponentExample(
            title = "Basic",
            descriptionMarkdown = "A simple popover with a header, title, and description.",
            code = popoverBasicRecipeCode,
            preview = { PopoverBasicRecipe() },
        ),
        ComponentExample(
            title = "Align",
            descriptionMarkdown =
                "Use the `align` prop on `PopoverContent` to control the horizontal alignment.",
            code = popoverAlignRecipeCode,
            preview = { PopoverAlignRecipe() },
        ),
        ComponentExample(
            title = "With Form",
            descriptionMarkdown = "A popover with form fields inside.",
            code = popoverFormRecipeCode,
            preview = { PopoverFormRecipe() },
        ),
        ComponentExample(
            title = "RTL",
            descriptionMarkdown = "To enable RTL support in shadcn/ui, see the RTL configuration guide.",
            code = popoverRtlRecipeCode,
            previewPadding = 0.dp,
            preview = {
                var language by remember { mutableIntStateOf(1) }
                RtlPreview(language, listOf("English", "Arabic (العربية)", "Hebrew (עברית)"), { language = it }) {
                    PopoverRtlRecipe(language)
                }
            },
        ),
    )
