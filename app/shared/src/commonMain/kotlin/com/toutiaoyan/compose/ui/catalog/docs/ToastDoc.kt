package com.toutiaoyan.compose.ui.catalog.docs

val toastDoc =
    ComponentDoc(
        id = "toast",
        title = "Toast",
        description = "A succinct message that is displayed temporarily.",
        installationBlocks =
            listOf(
                ComponentGuideBlock.Paragraph("Add `Toaster` once at the root of your app."),
                ComponentGuideBlock.Code(toastInstallationRecipeCode),
            ),
        usageCode = toastUsageRecipeCode,
        examplesHeading = null,
        examples = toastOfficialExamples + toastKotlinExamples,
        beforeExampleSections =
            mapOf(
                "Promise" to
                    listOf(
                        ComponentGuideSection(
                            title = "Action",
                            blocks =
                                listOf(
                                    ComponentGuideBlock.Paragraph(
                                        "Set `actionLabel` and `onAction` to render a button. " +
                                            "Use `close(id)` to dismiss that toast when the action runs.",
                                    ),
                                    ComponentGuideBlock.Code(toastActionRecipeCode),
                                ),
                        ),
                    ),
                "Kotlin extension: Duration" to
                    listOf(
                        ComponentGuideSection(
                            title = "API Reference",
                            blocks =
                                listOf(
                                    ComponentGuideBlock.Paragraph(
                                        "See the [Base UI Toast documentation]" +
                                            "(https://base-ui.com/react/components/toast) for manager options, " +
                                            "stacking, swipe dismissal, and the primitive API.",
                                    ),
                                ),
                        ),
                    ),
            ),
    )
