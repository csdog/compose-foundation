package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.catalog.docs.markerDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.*

class MarkerExamplesTest {
    @get:Rule val rule = createComposeRule()
    private var opened: String? = null

    private fun show(title: String) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                CompositionLocalProvider(
                    LocalUriHandler provides
                        object : UriHandler {
                            override fun openUri(uri: String) {
                                opened = uri
                            }
                        },
                ) {
                    OverlayHost {
                        Box(Modifier.width(560.dp).height(600.dp), contentAlignment = Alignment.Center) {
                            markerDoc.examples.single { it.title == title }.preview()
                        }
                    }
                }
            }
        }
    }

    @Test fun allEightOfficialRecipesAndAllThreeApiPartsArePresent() {
        assertEquals(
            listOf(
                "Default",
                "Variants",
                "Status",
                "Shimmer",
                "Separator",
                "Border",
                "With Icon",
                "Links and Buttons",
            ),
            markerDoc.examples.map { it.title },
        )
        assertEquals(listOf("Marker", "MarkerIcon", "MarkerContent"), markerDoc.apiSections.map { it.title })
        assertFalse(markerDoc.usageCode.contains("components.*"))
    }

    @Test fun copiedPageExplainsVariantsAndAccessibleKotlinCompositionInReadingOrder() {
        val markdown = componentPageMarkdown(markerDoc)
        val ordered =
            listOf(
                "[Message](/message)",
                "## Installation",
                "Use the following composition",
                "## Features",
                "| Variant | Description |",
                "## Status",
                "### Status and Progress",
                "### Labeled Separators",
                "### Bordered Markers",
                "### Decorative Icons",
                "### Interactive Markers",
                "## API Reference",
            )
        val positions =
            ordered.map { text ->
                markdown.indexOf(text).also { assertTrue(it >= 0, "Missing guidance: $text") }
            }
        assertEquals(positions.sorted(), positions)
        assertTrue(markdown.contains("liveRegion = LiveRegionMode.Polite"))
        assertTrue(markdown.contains("Role.Button"))
        assertTrue(markdown.contains("LinkAnnotation.Url"))
        assertFalse(markdown.contains("with the `render` prop"))
        assertFalse(markdown.contains("Set `role=\"status\"`"))
        assertFalse(markdown.contains("LocalDocNavigation"))
        assertFalse(markdown.contains("testTag"))
    }

    @Test fun copiedLinkRecipeUsesTheCallersSamePageNavigation() {
        val code = markerDoc.examples.single { it.title == "Links and Buttons" }.code
        assertTrue(code.contains("fun MarkerActionsRecipe(onOpenLink: () -> Unit)"))
        assertTrue(code.contains("MarkerLinkAction(onOpenLink)"))
        assertFalse(code.contains("LocalUriHandler"))
        assertFalse(code.contains("MarkerActionsRecipe()"))
    }

    @Test fun defaultContainsAllFourConversationMarkers() {
        show("Default")
        listOf("Switched to a new branch", "Thinking...", "Conversation compacted", "Explored 4 files").forEach {
            rule.onNodeWithText(it).assertIsDisplayed()
        }
    }

    @Test fun statusRowsAnnounceUpdatesWithoutExposingDecorativeLoadingLabels() {
        show("Status")
        rule.onAllNodes(SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Polite))
            .assertCountEquals(2)
        rule.onNodeWithContentDescription("Loading").assertDoesNotExist()
        rule.onNodeWithText("Compacting conversation").assertIsDisplayed()
        rule.onNodeWithText("Running tests").assertIsDisplayed()
    }

    @Test fun stackedIconExampleKeepsTheLabelBelowTheIconRow() {
        show("With Icon")
        rule.onNodeWithText("Syncing completed").assertIsDisplayed()
        rule.onNodeWithText("Explored 4 files").assertIsDisplayed()
    }

    @Test fun linksAndButtonsPerformTheirActualActions() {
        show("Links and Buttons")
        rule.onNodeWithTag("marker-link").performTouchInput { click(center) }
        assertEquals("#links-and-buttons", opened)
        rule.onNodeWithText("Revert this change").performClick()
        rule.onNodeWithText("You clicked the revert button").assertIsDisplayed()
    }
}
