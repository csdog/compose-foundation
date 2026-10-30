package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.ApiProperty
import com.toutiaoyan.compose.ui.catalog.docs.ComponentDoc
import com.toutiaoyan.compose.ui.catalog.docs.ComponentExample
import com.toutiaoyan.compose.ui.navigation.RouteKey
import com.toutiaoyan.compose.ui.theme.Theme
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CatalogNavigationTest {
    @get:Rule val rule = createComposeRule()

    @Test
    fun searchFocusesInputAndNavigatesToAFilteredResult() {
        var selected: RouteKey? = null
        var open by mutableStateOf(true)
        rule.setContent {
            Theme(minTouchTarget = null) {
                if (open) {
                    CatalogSearchDialog(onDismiss = { open = false }, onNavigate = {
                        selected = it
                        open = false
                    })
                }
            }
        }
        rule.onNodeWithTag("catalog-search-input").assertIsFocused().performTextInput("calendar")
        rule.onNodeWithText("Calendar").performClick()
        assertEquals(RouteKey.ComponentDetail("calendar"), selected)
        rule.onNodeWithTag("catalog-search-input").assertDoesNotExist()
    }

    @Test
    fun searchSupportsArrowKeysAndEnter() {
        var selected: RouteKey? = null
        rule.setContent {
            Theme(minTouchTarget = null) {
                CatalogSearchDialog(onDismiss = {}, onNavigate = { selected = it })
            }
        }
        rule.onNodeWithTag("catalog-search-input").performTextInput("calendar")
        rule.onNodeWithTag("catalog-search-input").performKeyInput {
            pressKey(Key.DirectionDown)
            pressKey(Key.Enter)
        }
        assertEquals(RouteKey.ComponentDetail("calendar"), selected)
    }

    @Test
    fun emptySearchHasNoEmptyGroupHeadings() {
        rule.setContent { Theme { CatalogSearchDialog(onDismiss = {}, onNavigate = {}) } }
        rule.onNodeWithTag("catalog-search-input").performTextInput("no-such-component-xyz")
        rule.onNodeWithText("No results found.").assertExists()
        rule.onNodeWithText("Components").assertDoesNotExist()
        rule.onNodeWithText("Getting Started").assertDoesNotExist()
    }

    @Test
    fun searchDismissesWithEscape() {
        var open by mutableStateOf(true)
        rule.setContent {
            Theme { if (open) CatalogSearchDialog(onDismiss = { open = false }, onNavigate = {}) }
        }
        rule.onNodeWithTag("catalog-search-input").performKeyInput { pressKey(Key.Escape) }
        rule.onNodeWithTag("catalog-search-input").assertDoesNotExist()
    }

    @Test
    fun navigationIncludesEveryEntryOnceAndKeepsExtensionsSeparate() {
        val all = catalogNavigationSections.flatMap { it.entries }
        assertEquals(catalogEntries.size, all.size)
        assertEquals(catalogEntries.toSet(), all.toSet())
        val components = catalogNavigationSections.single { it.title == "Components" }.entries
        assertEquals(listOf("Accordion", "Alert", "Alert Dialog", "Aspect Ratio"), components.take(4).map { it.title })
        assertEquals(
            listOf("Chip", "Stepper", "Text"),
            catalogNavigationSections.single {
                it.title == "Extensions"
            }.entries.map { it.title },
        )
    }

    @Test
    fun tableOfContentsScrollsToTheActualSection() {
        val scroll = ScrollState(0)
        val example = ComponentExample("Default", "example()") { Box(Modifier.size(200.dp)) }
        val doc =
            ComponentDoc(
                id = "toc-test",
                title = "Anchor test",
                description = "Scroll to the API section",
                usageCode = "usage()",
                examples = listOf(example, example.copy(title = "Second"), example.copy(title = "Third")),
                apiProperties = listOf(ApiProperty("enabled", "Boolean", "true")),
            )
        rule.setContent {
            Theme(minTouchTarget = null) {
                Box(Modifier.requiredSize(1440.dp, 1000.dp)) {
                    ComponentDetailContent(doc = doc, scrollState = scroll)
                }
            }
        }
        rule.onNodeWithTag("toc-api-reference").performSemanticsAction(SemanticsActions.OnClick) { it() }
        rule.waitForIdle()
        assertTrue(scroll.value > 0)
        val heading = rule.onNodeWithTag("heading-api-reference").fetchSemanticsNode().boundsInRoot
        assertTrue(heading.top < 1000f && heading.bottom > 0f)
    }
}
