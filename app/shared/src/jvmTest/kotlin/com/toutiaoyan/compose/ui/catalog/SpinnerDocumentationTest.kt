package com.toutiaoyan.compose.ui.catalog

import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.catalog.docs.spinnerDoc
import kotlin.test.Test
import kotlin.test.assertTrue

class SpinnerDocumentationTest {
    @Test
    fun copyPageKeepsLoadingPlacementAndRtlGuidanceInTheirSections() {
        val page = componentPageMarkdown(spinnerDoc)

        fun section(
            title: String,
            next: String,
        ) = page.substringAfter("## $title\n").substringBefore("## $next\n")
        val button = section("Button", "Badge").substringBefore("```kotlin")
        val badge = section("Badge", "Input Group").substringBefore("```kotlin")
        val rtl = section("RTL", "API Reference").substringBefore("```kotlin")
        assertTrue(button.contains("ButtonIconPlacement.Start") && button.contains("ButtonIconPlacement.End"))
        assertTrue(badge.contains("BadgeIconPlacement.Start") && badge.contains("BadgeIconPlacement.End"))
        assertTrue(rtl.contains("LocalLayoutDirection") && rtl.contains("https://ui.shadcn.com/docs/rtl"))
    }
}
