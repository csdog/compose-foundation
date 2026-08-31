package com.toutiaoyan.compose.ui.navigation

import kotlin.test.Test
import kotlin.test.assertEquals

class DeepLinksTest {
    @Test
    fun scheme_introduction() {
        assertEquals(RouteKey.Introduction, DeepLinks.resolve("shadcncompose://app"))
    }

    @Test
    fun scheme_create() {
        assertEquals(RouteKey.Create, DeepLinks.resolve("shadcncompose://app/create"))
    }

    @Test
    fun scheme_component_detail() {
        assertEquals(
            RouteKey.ComponentDetail("button"),
            DeepLinks.resolve("shadcncompose://app/button"),
        )
    }

    @Test
    fun create_is_not_swallowed_by_component_pattern() {
        assertEquals(RouteKey.Create, DeepLinks.resolve("shadcncompose://app/create"))
        assertEquals(
            RouteKey.ComponentDetail("accordion"),
            DeepLinks.resolve("shadcncompose://app/accordion"),
        )
    }

    @Test
    fun unknown_returns_null() {
        assertEquals(null, DeepLinks.resolve("shadcncompose://app/nested/too-deep"))
        assertEquals(null, DeepLinks.resolve("other://app/create"))
    }

    @Test
    fun native_uri_round_trip() {
        RouteKey.deepLinkSamples.forEach { sample ->
            val route =
                if (sample is RouteKey.ComponentDetail) {
                    RouteKey.ComponentDetail("button")
                } else {
                    sample
                }
            assertEquals(route, DeepLinks.resolve(DeepLinks.nativeUri(route)), "$route")
        }
    }

    @Test
    fun web_falls_back_to_deep_links() {
        assertEquals(
            RouteKey.Create,
            WebLinks.resolve("shadcncompose://app/create"),
        )
    }

    @Test
    fun web_spa_paths() {
        assertEquals(RouteKey.Create, WebLinks.resolve("http://localhost:8080/create"))
        assertEquals(RouteKey.Introduction, WebLinks.resolve("/"))
        assertEquals(
            RouteKey.ComponentDetail("button"),
            WebLinks.resolve("http://localhost:8080/button"),
        )
    }
}
