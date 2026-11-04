@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.field.FieldGroup
import com.toutiaoyan.compose.ui.components.field.FieldLabel
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.label.Label
import com.toutiaoyan.compose.ui.components.sheet.*
import com.toutiaoyan.compose.ui.components.text.Text

// region shared
private const val SheetProfileDescription = "Make changes to your profile here. Click save when you're done."

private fun Modifier.sheetLabelFocus(requester: FocusRequester): Modifier =
    pointerInput(requester) { detectTapGestures { requester.requestFocus() } }.semantics {
        onClick {
            requester.requestFocus()
            true
        }
    }

@Composable
private fun SheetProfileInput(
    label: String,
    initial: String,
    id: String,
    field: Boolean = false,
) {
    val requester = remember { FocusRequester() }
    var value by remember { mutableStateOf(initial) }
    val labelModifier = Modifier.sheetLabelFocus(requester)
    val input: @Composable () -> Unit = {
        Input(
            value,
            { value = it },
            Modifier.fillMaxWidth().focusRequester(requester).testTag("sheet-$id").semantics {
                contentDescription = label
            },
        )
    }
    if (field) {
        Field {
            FieldLabel(label, modifier = labelModifier)
            input()
        }
    } else {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Label(
                label,
                modifier =
                    labelModifier.layout { measurable, constraints ->
                        val glyph = measurable.measure(constraints.copy(minHeight = 0))
                        val height = constraints.constrainHeight(14.sp.roundToPx())
                        layout(glyph.width, height) { glyph.placeRelative(0, (height - glyph.height) / 2) }
                    },
            )
            input()
        }
    }
}
// endregion shared

// region default
@Composable
internal fun SheetDefaultRecipe() {
    var open by remember { mutableStateOf(false) }
    Sheet(open = open, onOpenChange = { open = it }) {
        SheetTrigger("Open", variant = ButtonVariant.Outline)
        SheetContent(Modifier.testTag("sheet-panel")) {
            SheetHeader("Edit profile", description = SheetProfileDescription)
            Column(
                Modifier.weight(1f).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                SheetProfileInput("Name", "Pedro Duarte", "name")
                SheetProfileInput("Username", "@peduarte", "username")
            }
            SheetFooter {
                Button("Save changes")
                Button("Close", variant = ButtonVariant.Outline, onClick = { open = false })
            }
        }
    }
}
// endregion default

// region side
private const val SheetLongParagraph =
    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et " +
        "dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut " +
        "aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse " +
        "cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in " +
        "culpa qui officia deserunt mollit anim id est laborum."

@Composable
internal fun SheetSideRecipe() {
    val viewportHeight = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.height.toDp() }
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        for ((side, label) in listOf(
            SheetSide.Top to "Top",
            SheetSide.Right to "Right",
            SheetSide.Bottom to "Bottom",
            SheetSide.Left to "Left",
        )) {
            key(side) {
                var open by remember { mutableStateOf(false) }
                Sheet(open = open, onOpenChange = { open = it }) {
                    SheetTrigger(label, variant = ButtonVariant.Outline)
                    SheetContent(
                        Modifier.then(
                            if (side == SheetSide.Top ||
                                side == SheetSide.Bottom
                            ) {
                                Modifier.heightIn(
                                    max =
                                        viewportHeight / 2,
                                )
                            } else {
                                Modifier
                            },
                        ).testTag("sheet-panel"),
                        side = side,
                    ) {
                        SheetHeader("Edit profile", description = SheetProfileDescription)
                        Column(
                            Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp).testTag("sheet-scroll"),
                        ) {
                            repeat(10) { index ->
                                Text(
                                    Modifier.fillMaxWidth().padding(bottom = 8.dp).testTag("sheet-paragraph-$index"),
                                    textStyle =
                                        TextStyle(
                                            fontSize = 14.sp,
                                            lineHeight = 22.75.sp,
                                            lineHeightStyle =
                                                LineHeightStyle(
                                                    LineHeightStyle.Alignment.Center,
                                                    LineHeightStyle.Trim.None,
                                                ),
                                        ),
                                ) { SheetLongParagraph }
                            }
                        }
                        SheetFooter {
                            Button("Save changes")
                            Button("Cancel", variant = ButtonVariant.Outline, onClick = { open = false })
                        }
                    }
                }
            }
        }
    }
}
// endregion side

// region noClose
@Composable
internal fun SheetNoCloseRecipe() {
    Sheet {
        SheetTrigger("Open Sheet", variant = ButtonVariant.Outline)
        SheetContent(Modifier.testTag("sheet-panel"), showCloseButton = false) {
            SheetHeader(
                "No Close Button",
                description = "This sheet doesn't have a close button in the top-right corner. Click outside to close.",
            )
        }
    }
}
// endregion noClose

// region rtl
@Composable
internal fun SheetRtlRecipe(language: Int = 1) {
    val copy =
        when (language) {
            1 ->
                listOf(
                    "فتح",
                    "تعديل الملف الشخصي",
                    "قم بإجراء تغييرات على ملفك الشخصي هنا. انقر حفظ عند الانتهاء.",
                    "الاسم",
                    "اسم المستخدم",
                    "حفظ التغييرات",
                    "إغلاق",
                )
            2 ->
                listOf(
                    "פתח",
                    "עריכת פרופיל",
                    "בצע שינויים בפרופיל שלך כאן. לחץ שמור כשתסיים.",
                    "שם",
                    "שם משתמש",
                    "שמור שינויים",
                    "סגור",
                )
            else -> listOf("Open", "Edit profile", SheetProfileDescription, "Name", "Username", "Save changes", "Close")
        }
    val direction = if (language == 0) LayoutDirection.Ltr else LayoutDirection.Rtl
    var open by remember { mutableStateOf(false) }
    CompositionLocalProvider(LocalLayoutDirection provides direction) {
        Sheet(open = open, onOpenChange = { open = it }) {
            SheetTrigger(copy[0], variant = ButtonVariant.Outline)
            SheetContent(
                Modifier.testTag("sheet-panel"),
                side = if (direction == LayoutDirection.Rtl) SheetSide.Left else SheetSide.Right,
            ) {
                SheetHeader(copy[1], description = copy[2])
                FieldGroup(Modifier.padding(horizontal = 16.dp)) {
                    SheetProfileInput(copy[3], "Pedro Duarte", "name", field = true)
                    SheetProfileInput(copy[4], "peduarte", "username", field = true)
                }
                SheetFooter {
                    Button(copy[5])
                    Button(copy[6], variant = ButtonVariant.Outline, onClick = { open = false })
                }
            }
        }
    }
}
// endregion rtl

internal val sheetOfficialExamples =
    listOf(
        ComponentExample("Default", code = sheetDefaultRecipeCode, preview = { SheetDefaultRecipe() }),
        ComponentExample("Side", code = sheetSideRecipeCode, preview = { SheetSideRecipe() }),
        ComponentExample("No Close Button", code = sheetNoCloseRecipeCode, preview = { SheetNoCloseRecipe() }),
        ComponentExample("RTL", code = sheetRtlRecipeCode, previewPadding = 0.dp, preview = {
            var language by remember { mutableIntStateOf(1) }
            RtlPreview(language, listOf("English", "Arabic (العربية)", "Hebrew (עברית)"), { language = it }) {
                SheetRtlRecipe(language)
            }
        }),
    )
