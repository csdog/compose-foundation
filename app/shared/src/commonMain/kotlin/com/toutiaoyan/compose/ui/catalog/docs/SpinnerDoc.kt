@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.ui.unit.dp

val spinnerDoc =
    ComponentDoc(
        id = "spinner", title = "Spinner",
        description = "An indicator that can be used to show a loading state.",
        examplesHeading = null,
        usageImportsCode = spinnerImports,
        usageCode = "Spinner()",
        compositionCode = "Spinner()",
        apiSections =
            listOf(
                ComponentApiSection(
                    "Spinner",
                    "Animated loading indicator with an optional custom visual.",
                    listOf(
                        ApiProperty("size", "SpinnerSize", "Default (16.dp, or inline size)"),
                        ApiProperty("label", "String", "Loading"),
                        ApiProperty("modifier / style", "Modifier / Style", "default"),
                        ApiProperty("content", "@Composable () -> Unit", "circular loader"),
                    ),
                ),
            ),
        examples =
            listOf(
                ComponentExample(
                    "Default",
                    spinnerCode("default"),
                    minPreviewHeight = 320.dp,
                    mobileMinPreviewHeight = 320.dp,
                    preview = { SpinnerDefaultRecipe() },
                ),
                ComponentExample(
                    "Customization",
                    spinnerCode("customization"),
                    descriptionMarkdown = "Replace the default circular visual using the Spinner content slot.",
                    preview = { SpinnerCustomizationRecipe() },
                ),
                ComponentExample(
                    "Size",
                    spinnerCode(
                        "size",
                    ),
                    descriptionMarkdown = "Use Modifier.size to change the size of the spinner.",
                    preview = {
                        SpinnerSizeRecipe()
                    },
                ),
                ComponentExample(
                    "Button",
                    spinnerCode("button"),
                    descriptionMarkdown =
                        "Add a spinner to a button to indicate a loading state. " +
                            "Place Spinner before the label with ButtonIconPlacement.Start, " +
                            "or after the label with ButtonIconPlacement.End.",
                    preview = { SpinnerButtonRecipe() },
                ),
                ComponentExample(
                    "Badge",
                    spinnerCode("badge"),
                    descriptionMarkdown =
                        "Add a spinner to a badge to indicate a loading state. " +
                            "Place Spinner before the label with BadgeIconPlacement.Start, " +
                            "or after the label with BadgeIconPlacement.End.",
                    preview = { SpinnerBadgeRecipe() },
                ),
                ComponentExample("Input Group", spinnerCode("inputgroup"), preview = { SpinnerInputGroupRecipe() }),
                ComponentExample("Empty", spinnerCode("empty"), preview = { SpinnerEmptyRecipe() }),
                ComponentExample(
                    "RTL",
                    spinnerCode("rtl"),
                    descriptionMarkdown =
                        "To enable RTL support, provide LocalLayoutDirection. " +
                            "For shadcn/ui configuration, see the " +
                            "[RTL configuration guide](https://ui.shadcn.com/docs/rtl).",
                    previewPadding = 0.dp,
                    preview = { SpinnerRtlRecipe() },
                ),
            ),
    )
