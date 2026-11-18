package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.ui.unit.dp

val toggleDoc =
    ComponentDoc(
        id = "toggle",
        title = "Toggle",
        description = "A two-state button that can be either on or off.",
        usageCode = toggleUsageRecipeCode,
        usageImportsCode = toggleUsageImportCode,
        examplesHeading = null,
        apiSections =
            listOf(
                ComponentApiSection(
                    title = "Toggle",
                    descriptionMarkdown =
                        "See the [Base UI Toggle](https://base-ui.com/react/components/toggle#api-reference) " +
                            "documentation.",
                    properties =
                        listOf(
                            ApiProperty("pressed", "Boolean?", "null"),
                            ApiProperty("defaultPressed", "Boolean", "false"),
                            ApiProperty("variant", "ToggleVariant", "Default"),
                            ApiProperty("size", "ToggleSize", "Md"),
                            ApiProperty("invalid", "Boolean", "explicit invalid overload"),
                            ApiProperty("disabled", "Boolean", "false"),
                        ),
                ),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    showHeading = false,
                    code = toggleDefaultRecipeCode,
                    preview = { ToggleDemoPreview() },
                ),
                ComponentExample(
                    title = "Outline",
                    descriptionMarkdown = "Use `variant = ToggleVariant.Outline` for an outline style.",
                    code = toggleOutlineRecipeCode,
                    preview = { ToggleOutlinePreview() },
                ),
                ComponentExample(
                    title = "With Text",
                    code = toggleTextRecipeCode,
                    preview = { ToggleWithTextPreview() },
                ),
                ComponentExample(
                    title = "Size",
                    descriptionMarkdown = "Use the `size` prop to change the size of the toggle.",
                    code = toggleSizeRecipeCode,
                    preview = { ToggleSizePreview() },
                ),
                ComponentExample(
                    title = "Disabled",
                    code = toggleDisabledRecipeCode,
                    preview = { ToggleDisabledPreview() },
                ),
                ComponentExample(
                    title = "RTL",
                    descriptionMarkdown =
                        "To enable RTL support in shadcn/ui, see the " +
                            "[RTL configuration guide](https://ui.shadcn.com/docs/rtl).",
                    previewPadding = 0.dp,
                    code = toggleRtlRecipeCode,
                    preview = { ToggleRtlCatalogPreview() },
                ),
                ComponentExample(
                    title = "Kotlin extension: Default variant",
                    code = toggleDefaultVariantRecipeCode,
                    preview = { ToggleDefaultVariantPreview() },
                ),
                ComponentExample(
                    title = "Kotlin extension: Default size",
                    code = toggleDefaultSizeRecipeCode,
                    preview = { ToggleDefaultSizePreview() },
                ),
                ComponentExample(
                    title = "Kotlin extension: Default disabled",
                    code = toggleDefaultDisabledRecipeCode,
                    preview = { ToggleDefaultDisabledPreview() },
                ),
                ComponentExample(
                    title = "Kotlin extension: Uncontrolled",
                    code = toggleUncontrolledRecipeCode,
                    preview = { ToggleUncontrolledPreview() },
                ),
                ComponentExample(
                    title = "Kotlin extension: Default pressed",
                    code = toggleDefaultPressedRecipeCode,
                    preview = { ToggleDefaultPressedPreview() },
                ),
                ComponentExample(
                    title = "Kotlin extension: Accessible label",
                    code = toggleAccessibleLabelRecipeCode,
                    preview = { ToggleAccessibleLabelPreview() },
                ),
                ComponentExample(
                    title = "Kotlin extension: Default label",
                    code = toggleDefaultLabelRecipeCode,
                    preview = { ToggleDefaultLabelPreview() },
                ),
                ComponentExample(
                    title = "Kotlin extension: Invalid",
                    descriptionMarkdown =
                        "Use `invalid` when the user must confirm a selection. Pressing the toggle clears the error.",
                    code = toggleInvalidRecipeCode,
                    preview = { ToggleInvalidPreview() },
                ),
            ),
    )
