package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.App
import com.toutiaoyan.compose.ui.navigation.RouteKey
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals

/** CSS viewport includes the 15px classic browser scrollbar; the Compose host excludes it. */
@RunWith(Parameterized::class)
class CatalogDocumentLayoutTest(private val width: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()

    @Test fun documentMatchesOfficialHorizontalGeometryAndBreakpoints() {
        rule.setContent {
            CompositionLocalProvider(LocalCatalogViewportWidth provides width.dp) {
                Box(Modifier.requiredSize((width - 15).dp, 1000.dp).testTag("host")) {
                    App(initialDarkModeOverride = dark, onNavHostReady = {
                        it.replaceAll(RouteKey.ComponentDetail("accordion"))
                    })
                }
            }
        }
        val host = rule.onNodeWithTag("host").getUnclippedBoundsInRoot()
        val content = rule.onNodeWithTag("doc-column").getUnclippedBoundsInRoot()
        val expected =
            when (width) {
                390 -> 24f to 327f
                768 -> 56.5f to 640f
                900 -> 122.5f to 640f
                1024 -> 328.5f to 640f
                1280 -> 312.5f to 640f
                1440 -> 392.5f to 640f
                1800 -> 572.5f to 640f
                else -> error("Missing official measurement")
            }
        assertEquals(expected.first, (content.left - host.left).value, .5f, "Content left")
        assertEquals(expected.second, (content.right - content.left).value, .5f, "Content width")
        if (width < 1024) {
            rule.onNodeWithContentDescription("Open navigation").assertExists()
        } else {
            rule.onNodeWithContentDescription("Open navigation").assertDoesNotExist()
        }
        if (width >= 1280) {
            rule.onNodeWithText("On This Page").assertExists()
        } else {
            rule.onNodeWithText("On This Page").assertDoesNotExist()
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "width={0}, dark={1}")
        fun cases(): List<Array<Any>> =
            listOf(390, 768, 900, 1024, 1280, 1440, 1800).flatMap { width ->
                listOf(false, true).map { arrayOf(width, it) }
            }
    }
}
