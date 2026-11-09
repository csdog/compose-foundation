@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.catalogViewportWidth

val checkboxDoc =
    ComponentDoc(
        id = "checkbox", title = "Checkbox",
        description = "A control that allows the user to toggle between checked and not checked.",
        examplesHeading = null,
        usageImportsCode = checkboxImports,
        usageCode = "Checkbox()",
        compositionCode = "Checkbox()",
        beforeExamples =
            listOf(
                ComponentGuideSection(
                    "Checked State",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            "Use `defaultChecked` for uncontrolled checkboxes, or `checked` " +
                                "and `onCheckedChange` to control the state.",
                        ),
                        ComponentGuideBlock.Code(
                            "var checked by remember { mutableStateOf(false) " +
                                "}\nCheckbox(checked = checked, onCheckedChange = { checked = it })",
                        ),
                    ),
                ),
            ),
        apiSections =
            listOf(
                ComponentApiSection(
                    "Checkbox",
                    "Controlled or uncontrolled checkbox; `indeterminate` represents a mixed state. " +
                        "Nova uses the official 150ms color transition without moving the control; " +
                        "callers can opt into `ButtonAnimation` press motion. See the " +
                        "[Base UI](https://base-ui.com/react/components/checkbox#api-reference) " +
                        "documentation for more information.",
                    listOf(
                        ApiProperty("checked / defaultChecked", "Boolean? / Boolean", "null / false"),
                        ApiProperty("onCheckedChange", "((Boolean) -> Unit)?", "null"),
                        ApiProperty("indeterminate / invalid / disabled / required", "Boolean", "false"),
                        ApiProperty("label", "String", "empty"),
                        ApiProperty("modifier / style", "Modifier / Style", "default"),
                    ),
                ),
            ),
        examples =
            listOf(
                ComponentExample(
                    "Default",
                    checkboxCode(
                        "default",
                    ),
                    minPreviewHeight = 320.dp,
                    mobileMinPreviewHeight = 320.dp,
                    preview = {
                        CheckboxDefaultRecipe()
                    },
                ),
                ComponentExample(
                    "Invalid State",
                    checkboxCode(
                        "invalid",
                    ),
                    descriptionMarkdown =
                        "Set `invalid` on both the checkbox and the field wrapper to show " +
                            "the invalid styles.",
                    preview = {
                        CheckboxInvalidRecipe()
                    },
                ),
                ComponentExample(
                    "Basic",
                    checkboxCode(
                        "basic",
                    ),
                    descriptionMarkdown =
                        "Pair the checkbox with `Field` and `FieldLabel` for proper " +
                            "layout and labeling.",
                    preview = {
                        CheckboxBasicRecipe()
                    },
                ),
                ComponentExample(
                    "Description",
                    checkboxCode(
                        "description",
                    ),
                    descriptionMarkdown = "Use `FieldContent` and `FieldDescription` for helper text.",
                    preview = {
                        CheckboxDescriptionRecipe()
                    },
                ),
                ComponentExample(
                    "Disabled",
                    checkboxCode(
                        "disabled",
                    ),
                    descriptionMarkdown =
                        "Use `disabled` on the checkbox and field to prevent interaction " +
                            "and show disabled styles.",
                    preview = {
                        CheckboxDisabledRecipe()
                    },
                ),
                ComponentExample(
                    "Group",
                    checkboxCode(
                        "group",
                    ),
                    descriptionMarkdown = "Use multiple fields to create a checkbox list.",
                    preview = {
                        CheckboxGroupRecipe()
                    },
                ),
                ComponentExample(
                    "Table",
                    checkboxCode("table"),
                    previewPadding = 0.dp,
                    preview = {
                        Box(Modifier.padding(if (catalogViewportWidth() < 768.dp) 16.dp else 32.dp)) {
                            CheckboxTableRecipe()
                        }
                    },
                ),
                ComponentExample(
                    "RTL",
                    checkboxCode(
                        "rtl",
                    ),
                    previewPadding = 0.dp,
                    descriptionMarkdown =
                        "To enable RTL support in shadcn/ui, see the [RTL configuration " +
                            "guide](https://ui.shadcn.com/docs/rtl).",
                    preview = {
                        CheckboxRtlRecipe()
                    },
                ),
            ),
    )
