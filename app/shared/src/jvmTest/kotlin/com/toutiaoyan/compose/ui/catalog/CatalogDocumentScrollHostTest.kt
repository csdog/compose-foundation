package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.App
import com.toutiaoyan.compose.ui.navigation.AppRouter
import com.toutiaoyan.compose.ui.navigation.RouteKey
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import kotlin.test.*

class CatalogDocumentScrollHostTest {
    @get:Rule val rule = createComposeRule()

    @Test fun routeOwnsOneBindingAndRestoresItsCachedDocumentPosition() {
        val starts = mutableListOf<ScrollState?>()
        val stops = mutableListOf<ScrollState?>()
        val docs =
            object : DocNavigation {
                override var location by mutableStateOf(DocLocation(null, ""))

                override fun navigate(fragment: String) {
                    location =
                        location.copy(fragment = fragment, revision = location.revision + 1, restoreScroll = false)
                }
            }
        val host =
            object : DocumentScrollHost {
                override suspend fun bind(
                    state: ScrollState?,
                    pixelsPerCssPixel: Float,
                ): Nothing {
                    assertTrue(pixelsPerCssPixel > 0)
                    starts += state
                    try {
                        awaitCancellation()
                    } finally {
                        stops += state
                    }
                }
            }
        lateinit var router: AppRouter
        rule.setContent {
            CompositionLocalProvider(LocalDocumentScrollHost provides host, LocalDocNavigation provides docs) {
                Box(Modifier.requiredSize(1200.dp, 800.dp)) {
                    App(onNavHostReady = { router = it })
                }
            }
        }
        rule.waitForIdle()
        val intro = assertNotNull(starts.single())
        rule.runOnIdle {
            docs.location = DocLocation("accordion", "")
            router.replaceAll(RouteKey.ComponentDetail("accordion"))
        }
        rule.waitForIdle()
        val accordion = assertNotNull(starts.last())
        assertNotSame(intro, accordion)
        assertSame(intro, stops.single())
        rule.runOnIdle { runBlocking { accordion.scrollTo(180) } }
        rule.onNodeWithContentDescription("Toggle theme").performClick()
        rule.waitForIdle()
        assertEquals(2, starts.size, "A theme change must not rebind the scroll listener")
        rule.runOnIdle {
            docs.location = DocLocation(null, "")
            router.replaceAll(RouteKey.Create)
        }
        rule.waitForIdle()
        assertNull(starts.last(), "Create keeps its independent preview scrollers")
        rule.onNodeWithText("Build your own shadcn/ui").assertExists()
        rule.onNodeWithText("Get Started").performClick()
        rule.waitForIdle()
        rule.runOnIdle {
            docs.location = DocLocation("accordion", "", restoreScroll = true)
            router.replaceAll(RouteKey.ComponentDetail("accordion"))
        }
        rule.waitForIdle()
        assertSame(accordion, starts.last())
        assertEquals(180, accordion.value)
        assertEquals(listOf<ScrollState?>(intro, accordion, null), stops)
        rule.runOnIdle { docs.navigate("") }
        rule.waitForIdle()
        assertEquals(0, accordion.value, "A new empty-fragment navigation still starts at the top")
        assertEquals(4, starts.size)
    }
}
