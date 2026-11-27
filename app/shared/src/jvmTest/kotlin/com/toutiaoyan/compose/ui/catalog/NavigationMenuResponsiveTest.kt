package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.catalog.docs.navigationMenuDoc
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.*

class NavigationMenuResponsiveTest {
    @get:Rule val rule = createComposeRule()

    @Test fun officialPreviewsPrecedeKotlinSupplementsAndCopyCodeIncludesItsDependencies() {
        assertEquals(listOf("Default", "RTL"), navigationMenuDoc.examples.take(2).map { it.title })
        assertTrue(navigationMenuDoc.examples.any { it.title == "Kotlin extension: Vertical" })
        assertTrue(navigationMenuDoc.examples.any { it.title == "Kotlin extension: Controlled" })
        assertTrue(navigationMenuDoc.examples.any { it.title == "Kotlin extension: Delay" })
        assertTrue(
            navigationMenuDoc.examples.single { it.title == "RTL" }
                .descriptionMarkdown.orEmpty()
                .startsWith("To enable RTL support in shadcn/ui"),
        )
        assertTrue(navigationMenuDoc.compositionCode.orEmpty().contains("NavigationMenuIndicator"))
        assertEquals("Link Component", navigationMenuDoc.beforeExamples.single().title)
        navigationMenuDoc.examples.take(2).forEach {
            assertFalse(it.code.contains("RtlPreview("))
            assertTrue(it.code.contains("private fun NavigationMenuRecipeBody"))
            assertTrue(it.code.contains("import com.toutiaoyan.compose.ui.components.navigationmenu.*"))
        }
    }

    @Test fun componentsAppearAt768AndTheirGridGrowsAt1024WhileOpen() {
        var width by mutableIntStateOf(767)
        rule.mainClock.autoAdvance = false
        rule.setContent {
            val original = LocalWindowInfo.current
            val density = LocalDensity.current
            val currentWidth = width
            val window =
                remember(original, density, currentWidth) {
                    object : WindowInfo by original {
                        override val containerSize =
                            with(density) {
                                IntSize(
                                    currentWidth.dp.roundToPx(),
                                    900.dp.roundToPx(),
                                )
                            }
                    }
                }
            CompositionLocalProvider(LocalWindowInfo provides window) {
                Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                    Box(Modifier.width(800.dp).height(600.dp)) { navigationMenuDoc.examples.first().RenderPreview() }
                }
            }
        }

        fun advance() {
            rule.mainClock.advanceTimeBy(400)
            rule.waitForIdle()
        }
        advance()
        rule.onNodeWithText("Components").assertDoesNotExist()
        rule.runOnIdle { width = 768 }
        advance()
        rule.onNodeWithText("Components").performClick()
        advance()
        rule.onNodeWithTag("navigation-components-panel").assertWidthIsEqualTo(508.dp)
        rule.runOnIdle { width = 1023 }
        advance()
        rule.onNodeWithTag("navigation-components-panel").assertWidthIsEqualTo(508.dp)
        rule.runOnIdle { width = 1024 }
        advance()
        rule.onNodeWithTag("navigation-components-panel").assertWidthIsEqualTo(608.dp)
    }
}
