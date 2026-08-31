package com.toutiaoyan.compose.ui.catalog.docs

val guidePagesById: Map<String, GuidePage> =
    listOf(
        introductionPage,
        installationPage,
        themingPage,
        darkModePage,
        typographyPage,
    ).associateBy { it.id }
