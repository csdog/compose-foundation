package com.toutiaoyan.compose.ui.catalog

import com.toutiaoyan.compose.ui.navigation.RouteKey
import kotlin.test.Test
import kotlin.test.assertEquals

class DocumentTitleTest {
    @Test
    fun titlesFollowResolvedRoutesAcrossHistoryAndRefresh() {
        // Resolve fresh URLs just as initial load and popstate do, including fragments.
        val paths = listOf("toggle-group", "toggle", "toggle-group", "toggle", "toggle", "toggle#examples")
        val expected = listOf("Toggle Group", "Toggle", "Toggle Group", "Toggle", "Toggle", "Toggle")
        paths.zip(expected).forEach { (path, title) ->
            val route = com.toutiaoyan.compose.ui.navigation.WebLinks.resolve("https://example.com/$path")
            assertEquals("$title — Compose Foundation", route?.documentTitle)
        }
    }

    @Test
    fun nonComponentAndUnknownRoutesHaveUsefulTitles() {
        assertEquals("Introduction — Compose Foundation", RouteKey.Introduction.documentTitle)
        assertEquals("Theme — Compose Foundation", RouteKey.Create.documentTitle)
        assertEquals("Compose Foundation — Components", RouteKey.ComponentDetail("missing").documentTitle)
    }
}
