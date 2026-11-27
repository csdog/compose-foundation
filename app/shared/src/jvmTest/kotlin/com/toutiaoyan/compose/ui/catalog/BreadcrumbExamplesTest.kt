package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.breadcrumbDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.*

class BreadcrumbExamplesTest {
    @get:Rule val rule = createComposeRule()

    @Test fun docsKeepOfficialRecipesFirstAndGroupMenuPaths() {
        assertEquals(
            listOf(
                "Default",
                "Basic",
                "Custom separator",
                "Dropdown",
                "Collapsed",
                "Link component",
                "RTL",
            ),
            breadcrumbDoc.examples.take(7).map { it.title },
        )
        assertEquals("Kotlin extension: Disabled", breadcrumbDoc.examples[7].title)
        assertNull(breadcrumbDoc.examplesHeading)
        for (title in listOf("Default", "Dropdown", "RTL")) {
            assertTrue(
                breadcrumbDoc.examples.single { it.title == title }.code.contains("DropdownMenuGroup {"),
            )
        }
        assertTrue(
            breadcrumbDoc.apiSections.single { it.title == "BreadcrumbLink" }
                .descriptionMarkdown.contains("keyboard focus ring"),
        )
        assertTrue(
            breadcrumbDoc.apiSections.single { it.title == "BreadcrumbPage" }
                .descriptionMarkdown.contains("current page"),
        )
    }

    @Test fun defaultHasWorkingCollapsedMenu() {
        rule.setContent {
            Theme(
                preset = StylePreset.Nova,
                minTouchTarget = null,
            ) { OverlayHost { breadcrumbDoc.examples.first().RenderPreview() } }
        }
        rule.onNodeWithContentDescription("Toggle menu").performClick()
        rule.onNodeWithText("Documentation").assertIsDisplayed()
        rule.onNodeWithText("Themes").assertIsDisplayed().performClick()
        rule.onNodeWithText("Documentation").assertDoesNotExist()
        rule.onNodeWithContentDescription("Toggle menu").performClick()
        rule.onNodeWithText("Documentation").performKeyInput { pressKey(Key.Escape) }
        rule.onNodeWithText("Documentation").assertDoesNotExist()
    }

    @Test fun linkComponentUsesCallerNavigation() {
        var selectedFragment = ""
        val nav =
            object : DocNavigation {
                override val location = DocLocation("breadcrumb", "")

                override fun navigate(fragment: String) {
                    selectedFragment = fragment
                }
            }
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                CompositionLocalProvider(LocalDocNavigation provides nav) {
                    breadcrumbDoc.examples.single { it.title == "Link component" }.RenderPreview()
                }
            }
        }
        rule.onNodeWithText("Home").performClick()
        assertEquals("link-component", selectedFragment)
        selectedFragment = ""
        rule.onNodeWithText("Components").performClick()
        assertEquals("link-component", selectedFragment)
    }

    @Test fun customSeparatorLinksHaveRealTargets() {
        val visited = mutableListOf<String>()
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                CompositionLocalProvider(
                    LocalUriHandler provides
                        object : UriHandler {
                            override fun openUri(uri: String) {
                                visited += uri
                            }
                        },
                ) {
                    breadcrumbDoc.examples.single { it.title == "Custom separator" }.RenderPreview()
                }
            }
        }
        rule.onNodeWithText("Home").performClick()
        rule.onNodeWithText("Components").performClick()
        assertEquals(listOf("/", "https://ui.shadcn.com/components"), visited)
        assertTrue(
            breadcrumbDoc.examples.single { it.title == "Custom separator" }.code
                .contains("uri.openUri(\"https://ui.shadcn.com/components\")"),
        )
    }

    @Test fun collapsedLinksUseOfficialHomeAndDocsTargetsInPreviewAndCode() {
        val visited = mutableListOf<String>()
        val example = breadcrumbDoc.examples.single { it.title == "Collapsed" }
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                CompositionLocalProvider(
                    LocalUriHandler provides
                        object : UriHandler {
                            override fun openUri(uri: String) {
                                visited += uri
                            }
                        },
                ) { example.RenderPreview() }
            }
        }
        rule.onNodeWithText("Home").performClick()
        rule.onNodeWithText("Components").performClick()
        assertEquals(listOf("/", "https://ui.shadcn.com/docs/components"), visited)
        assertTrue(example.code.contains("uri.openUri(\"/\")"))
        assertTrue(example.code.contains("uri.openUri(\"https://ui.shadcn.com/docs/components\")"))
    }

    @Test fun rtlLanguageAndTranslatedMenuSwitchTogether() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.width(340.dp)) { breadcrumbDoc.examples.single { it.title == "RTL" }.RenderPreview() }
                }
            }
        }
        rule.onNodeWithContentDescription("مسار التنقل, current page").assertIsDisplayed()
        rule.onNodeWithText("المكونات").performClick()
        rule.onNodeWithText("التوثيق").assertIsDisplayed().performClick()
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("English").performClick()
        rule.onNodeWithText("Components").performClick()
        rule.onNodeWithText("Documentation").assertIsDisplayed().performClick()
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("Hebrew (עברית)").performClick()
        rule.onNodeWithContentDescription("פירורי לחם, current page").assertIsDisplayed()
        rule.onNodeWithText("רכיבים").performClick()
        rule.onNodeWithText("תיעוד").assertIsDisplayed()
    }
}
