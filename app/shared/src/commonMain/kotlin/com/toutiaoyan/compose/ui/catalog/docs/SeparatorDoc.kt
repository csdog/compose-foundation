@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

val separatorDoc =
    ComponentDoc(
        id = "separator", title = "Separator",
        description = "Visually or semantically separates content.",
        examplesHeading = null,
        usageImportsCode = "import com.toutiaoyan.compose.ui.components.separator.*",
        usageCode = "Separator()",
        compositionCode = "Separator()",
        beforeExamples =
            listOf(
                ComponentGuideSection(
                    "Kotlin extension: Compose layout and semantics",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            "For a vertical divider in a content-sized row, use `Modifier.height(IntrinsicSize.Min)` " +
                                "on the row. `decorative = true` removes the default accessibility description. " +
                                "Compose exposes a description and orientation; " +
                                "it does not provide the native HTML separator role.",
                        ),
                    ),
                ),
            ),
        apiSections =
            listOf(
                ComponentApiSection(
                    "Separator",
                    "A horizontal or vertical dividing line. See the " +
                        "[Base UI Separator](https://base-ui.com/react/components/separator#api-reference) " +
                        "documentation for the upstream API.",
                    listOf(
                        ApiProperty("orientation", "SeparatorOrientation", "Horizontal"),
                        ApiProperty("decorative", "Boolean", "false"),
                        ApiProperty("modifier / style", "Modifier / Style", "default"),
                    ),
                ),
            ),
        examples = separatorOfficialExamples,
    )
