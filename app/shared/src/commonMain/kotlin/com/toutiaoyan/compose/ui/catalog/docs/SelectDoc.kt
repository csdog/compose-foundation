@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

private val selectKotlinApi =
    listOf(
        ComponentApiSection(
            "Select",
            "Single-selection state and popup lifecycle.",
            properties =
                listOf(
                    ApiProperty("value", "T?", "required"),
                    ApiProperty("onValueChange", "(T) -> Unit", "required"),
                    ApiProperty("open", "Boolean?", "null"),
                    ApiProperty("onOpenChange", "((Boolean) -> Unit)?", "null"),
                    ApiProperty("defaultOpen", "Boolean", "false"),
                    ApiProperty("placeholder", "String", "Select an option"),
                    ApiProperty("label", "(T) -> String", "toString()"),
                    ApiProperty("disabled", "Boolean", "false"),
                    ApiProperty("required", "Boolean", "false"),
                    ApiProperty("invalid", "Boolean", "false"),
                    ApiProperty("modal", "Boolean", "true"),
                ),
        ),
        ComponentApiSection(
            "SelectTrigger",
            "Trigger appearance and accessible name.",
            properties =
                listOf(
                    ApiProperty("size", "SelectSize", "Default"),
                    ApiProperty("accessibilityLabel", "String", "empty; uses selected label or placeholder"),
                    ApiProperty("invalid", "Boolean", "false"),
                    ApiProperty("content", "@Composable RowScope.() -> Unit", "SelectValue()"),
                ),
        ),
        ComponentApiSection(
            "SelectContent",
            "Popup positioning, dismissal, and motion.",
            properties =
                listOf(
                    ApiProperty("alignItemWithTrigger", "Boolean?", "null; uses position"),
                    ApiProperty("position", "SelectContentPosition", "AlignItemWithTrigger"),
                    ApiProperty("align", "PopupAlign", "Center"),
                    ApiProperty("sideOffset", "Dp", "4.dp"),
                    ApiProperty("alignOffset", "Dp", "0.dp"),
                    ApiProperty("dismissOnClickOutside", "Boolean", "true"),
                    ApiProperty("enter", "EnterTransition", "MenuDefaultEnter"),
                    ApiProperty("exit", "ExitTransition", "MenuDefaultExit"),
                ),
        ),
        ComponentApiSection(
            "SelectItem",
            "An option; return true from onSelect to prevent selection.",
            properties =
                listOf(
                    ApiProperty("value", "T", "required"),
                    ApiProperty("enabled", "Boolean", "true"),
                    ApiProperty("disabled", "Boolean", "false"),
                    ApiProperty("textValue", "String?", "null; uses formatted value"),
                    ApiProperty("onSelect", "(() -> Boolean)?", "null"),
                ),
        ),
    )

val selectDoc =
    ComponentDoc(
        id = "select",
        title = "Select",
        description = "Displays a list of options for the user to pick from—triggered by a button.",
        usageImportsCode =
            """
            @file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

            import androidx.compose.runtime.*
            import androidx.compose.ui.Modifier
            import androidx.compose.foundation.layout.width
            import androidx.compose.ui.unit.dp
            import com.toutiaoyan.compose.ui.components.select.*
            """.trimIndent(),
        usageCode =
            """
            @Composable
            fun SelectUsage() {
                var theme by remember { mutableStateOf<String?>(null) }
                Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                    SelectTrigger(Modifier.width(192.dp))
                    SelectContent {
                        SelectItem("Light")
                        SelectItem("Dark")
                        SelectItem("System")
                    }
                }
            }
            """.trimIndent(),
        examplesHeading = null,
        beforeCompositionMarkdown = "Use the following composition to build a `Select`:",
        compositionCode =
            """
            Select
            ├── SelectTrigger
            │   └── SelectValue
            └── SelectContent
                ├── SelectGroup
                │   ├── SelectLabel
                │   ├── SelectItem
                │   └── SelectItem
                ├── SelectSeparator
                └── SelectGroup
                    ├── SelectLabel
                    ├── SelectItem
                    └── SelectItem
            """.trimIndent(),
        beforeExampleSections =
            mapOf(
                "Kotlin extension: Uncontrolled" to
                    listOf(
                        ComponentGuideSection(
                            "API Reference",
                            listOf(
                                ComponentGuideBlock.Paragraph(
                                    "See the [Base UI Select]" +
                                        "(https://base-ui.com/react/components/select#api-reference) documentation.",
                                ),
                            ),
                        ),
                    ),
            ),
        afterExamples =
            listOf(
                ComponentGuideSection(
                    "Kotlin extension: API",
                    selectKotlinApi.flatMap { section ->
                        listOf(
                            ComponentGuideBlock.Paragraph("**" + section.title + "** " + section.descriptionMarkdown),
                            ComponentGuideBlock.Table(
                                headers = listOf("Property", "Type", "Default"),
                                rows = section.properties.map { listOf(it.name, it.type, it.defaultValue) },
                            ),
                        )
                    },
                ),
            ),
        examples =
            selectPageExamples(
                listOf(
                    ComponentExample(
                        title = "Default",
                        showHeading = false,
                        code = selectRecipeCode.getValue("Default"),
                        preview = { SelectDefaultPreview() },
                    ),
                    ComponentExample(
                        title = "Uncontrolled",
                        code = selectRecipeCode.getValue("Uncontrolled"),
                        preview = { SelectUncontrolledPreview() },
                    ),
                    ComponentExample(
                        title = "Default open",
                        code = selectRecipeCode.getValue("Default open"),
                        preview = { SelectDefaultOpenPreview() },
                    ),
                    ComponentExample(
                        title = "Compound",
                        code = selectRecipeCode.getValue("Compound"),
                        preview = { SelectCompoundPreview() },
                    ),
                    ComponentExample(
                        title = "Default disabled",
                        code = selectRecipeCode.getValue("Default disabled"),
                        preview = { SelectDefaultDisabledPreview() },
                    ),
                    ComponentExample(
                        title = "Small",
                        code = selectRecipeCode.getValue("Small"),
                        preview = { SelectSmallPreview() },
                    ),
                    ComponentExample(
                        title = "Default size",
                        code = selectRecipeCode.getValue("Default size"),
                        preview = { SelectDefaultSizePreview() },
                    ),
                    ComponentExample(
                        title = "Scroll buttons",
                        descriptionMarkdown =
                            "Scroll controls appear automatically when the list overflows. " +
                                "Move over a control or click it to scroll.",
                        code = selectRecipeCode.getValue("Scroll buttons"),
                        preview = { SelectScrollButtonsPreview() },
                    ),
                    ComponentExample(
                        title = "Default invalid",
                        code = selectRecipeCode.getValue("Default invalid"),
                        preview = { SelectDefaultInvalidPreview() },
                    ),
                    ComponentExample(
                        title = "Required",
                        code = selectRecipeCode.getValue("Required"),
                        preview = { SelectRequiredPreview() },
                    ),
                    ComponentExample(
                        title = "Default required",
                        code = selectRecipeCode.getValue("Default required"),
                        preview = { SelectDefaultRequiredPreview() },
                    ),
                    ComponentExample(
                        title = "Default placeholder",
                        code = selectRecipeCode.getValue("Default placeholder"),
                        preview = { SelectDefaultPlaceholderPreview() },
                    ),
                    ComponentExample(
                        title = "Disabled select",
                        code = selectRecipeCode.getValue("Disabled select"),
                        preview = { SelectDisabledSelectPreview() },
                    ),
                    ComponentExample(
                        title = "Non-modal",
                        code = selectRecipeCode.getValue("Non-modal"),
                        preview = { SelectNonModalPreview() },
                    ),
                    ComponentExample(
                        title = "Modal",
                        code = selectRecipeCode.getValue("Modal"),
                        preview = { SelectModalPreview() },
                    ),
                    ComponentExample(
                        title = "Default modal",
                        code = selectRecipeCode.getValue("Default modal"),
                        preview = { SelectDefaultModalPreview() },
                    ),
                    ComponentExample(
                        title = "Group heading",
                        code = selectRecipeCode.getValue("Group heading"),
                        preview = { SelectGroupHeadingPreview() },
                    ),
                    ComponentExample(
                        title = "Prevent item select",
                        code = selectRecipeCode.getValue("Prevent item select"),
                        preview = { SelectPreventItemSelectPreview() },
                    ),
                    ComponentExample(
                        title = "Custom text value",
                        code = selectRecipeCode.getValue("Custom text value"),
                        preview = { SelectCustomTextValuePreview() },
                    ),
                    ComponentExample(
                        title = "Value",
                        code = selectRecipeCode.getValue("Value"),
                        preview = { SelectValuePreview() },
                    ),
                    ComponentExample(
                        title = "Separator",
                        code = selectRecipeCode.getValue("Separator"),
                        preview = { SelectSeparatorPreview() },
                    ),
                    ComponentExample(
                        title = "Label",
                        code = selectRecipeCode.getValue("Label"),
                        preview = { SelectLabelPreview() },
                    ),
                    ComponentExample(
                        title = "Group",
                        code = selectRecipeCode.getValue("Group"),
                        preview = { SelectGroupPreview() },
                    ),
                    ComponentExample(
                        title = "Item",
                        code = selectRecipeCode.getValue("Item"),
                        preview = { SelectItemPreview() },
                    ),
                    ComponentExample(
                        title = "Accessible label",
                        code = selectRecipeCode.getValue("Accessible label"),
                        preview = { SelectAccessibleLabelPreview() },
                    ),
                    ComponentExample(
                        title = "Default accessibility label",
                        code = selectRecipeCode.getValue("Default accessibility label"),
                        preview = { SelectDefaultAccessibilityLabelPreview() },
                    ),
                    ComponentExample(
                        title = "Trigger",
                        code = selectRecipeCode.getValue("Trigger"),
                        preview = { SelectTriggerPreview() },
                    ),
                    ComponentExample(
                        title = "Content",
                        code = selectRecipeCode.getValue("Content"),
                        preview = { SelectContentPreview() },
                    ),
                    ComponentExample(
                        title = "Default content variant",
                        code = selectRecipeCode.getValue("Default content variant"),
                        preview = { SelectDefaultContentVariantPreview() },
                    ),
                    ComponentExample(
                        title = "Default dismiss",
                        code = selectRecipeCode.getValue("Default dismiss"),
                        preview = { SelectDefaultDismissPreview() },
                    ),
                    ComponentExample(
                        title = "Side offset",
                        code = selectRecipeCode.getValue("Side offset"),
                        preview = { SelectSideOffsetPreview() },
                    ),
                    ComponentExample(
                        title = "Default side offset",
                        code = selectRecipeCode.getValue("Default side offset"),
                        preview = { SelectDefaultSideOffsetPreview() },
                    ),
                    ComponentExample(
                        title = "Viewport",
                        code = selectRecipeCode.getValue("Viewport"),
                        preview = { SelectViewportPreview() },
                    ),
                    ComponentExample(
                        title = "Scroll up button",
                        descriptionMarkdown =
                            "Scroll controls appear automatically when the list overflows. " +
                                "Move over a control or click it to scroll.",
                        code = selectRecipeCode.getValue("Scroll up button"),
                        preview = { SelectScrollUpButtonPreview() },
                    ),
                    ComponentExample(
                        title = "Scroll down button",
                        descriptionMarkdown =
                            "Scroll controls appear automatically when the list overflows. " +
                                "Move over a control or click it to scroll.",
                        code = selectRecipeCode.getValue("Scroll down button"),
                        preview = { SelectScrollDownButtonPreview() },
                    ),
                ),
            ),
    )
