@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.item

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.test.*
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class ItemParityTest : ScreenshotTest() {
    @Test fun novaVariantsReserveTheSameTransparentBorder() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Column(Modifier.width(300.dp)) {
                ItemVariant.entries.forEach { variant ->
                    Item(variant = variant, modifier = Modifier.testTag(variant.name)) {
                        ItemContent { ItemTitle(variant.name) }
                    }
                }
            }
        }
        for (variant in ItemVariant.entries) {
            val row =
                composeRule.onNodeWithTag(variant.name).assertHeightIsEqualTo(42.dp).fetchSemanticsNode().boundsInRoot
            val text = composeRule.onNodeWithText(variant.name).fetchSemanticsNode().boundsInRoot
            assertEquals(13f, text.left - row.left, 0.1f)
            assertEquals(11f, text.top - row.top, 0.1f)
        }
    }

    @Test fun headerAndFooterOccupySeparateFullWidthLines() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Item(Modifier.width(320.dp)) {
                ItemHeader(Modifier.testTag("header")) { Text { "Header" } }
                ItemMedia { Box(Modifier.size(20.dp)) }
                ItemContent(Modifier.testTag("content")) {
                    ItemTitle("Title")
                    ItemDescription("Description")
                }
                ItemActions { Text { "Action" } }
                ItemFooter(Modifier.testTag("footer")) { Text { "Footer" } }
            }
        }
        val header = composeRule.onNodeWithTag("header").assertWidthIsEqualTo(294.dp).fetchSemanticsNode().boundsInRoot
        val content = composeRule.onNodeWithTag("content").fetchSemanticsNode().boundsInRoot
        val footer = composeRule.onNodeWithTag("footer").assertWidthIsEqualTo(294.dp).fetchSemanticsNode().boundsInRoot
        assertTrue(content.top >= header.bottom + 9f)
        assertTrue(footer.top >= content.bottom + 9f)
    }

    @Test fun imageMediaFollowsTheItemSizeAndIconHasNoForcedBadge() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Column(Modifier.width(320.dp)) {
                ItemSize.entries.forEach { size ->
                    Item(size = size) {
                        ItemMedia(Modifier.testTag(size.name), ItemMediaVariant.Image) { Box(Modifier.fillMaxSize()) }
                        ItemContent { ItemTitle(size.name) }
                    }
                }
                Item { ItemMedia(Modifier.testTag("icon"), ItemMediaVariant.Icon) { Box(Modifier.size(16.dp)) } }
            }
        }
        listOf(ItemSize.Default to 40.dp, ItemSize.Sm to 32.dp, ItemSize.Xs to 24.dp).forEach { (size, width) ->
            composeRule.onNodeWithTag(size.name).assertWidthIsEqualTo(width).assertHeightIsEqualTo(width)
        }
        composeRule.onNodeWithTag("icon").assertWidthIsEqualTo(16.dp)
    }

    @Test fun addingAndRemovingDescriptionUpdatesMediaAlignment() {
        var description by mutableStateOf(false)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Item(Modifier.width(320.dp)) {
                ItemMedia(variant = ItemMediaVariant.Image) { Box(Modifier.fillMaxSize().testTag("media")) }
                ItemContent {
                    ItemTitle("Title")
                    if (description) ItemDescription("Description")
                }
            }
        }

        fun offset(): Float =
            composeRule.onNodeWithTag("media").fetchSemanticsNode().boundsInRoot.top -
                composeRule.onNodeWithText("Title").fetchSemanticsNode().boundsInRoot.top
        val centered = offset()
        assertTrue(centered < 0f)
        composeRule.runOnIdle { description = true }
        assertEquals(2f, offset(), 0.1f)
        composeRule.runOnIdle { description = false }
        assertEquals(centered, offset(), 0.1f)
    }

    @Test fun extraSmallContentHasZeroGapAndTwelvePixelDescription() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Item(size = ItemSize.Xs) {
                ItemContent {
                    ItemTitle("Title")
                    ItemDescription("Description")
                }
            }
        }
        val title = composeRule.onNodeWithText("Title").fetchSemanticsNode().boundsInRoot
        val description = composeRule.onNodeWithText("Description").fetchSemanticsNode().boundsInRoot
        assertEquals(title.bottom, description.top, 0.1f)
        val results = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithText(
            "Description",
        ).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
        assertEquals(12f, results.single().layoutInput.style.fontSize.value)
        assertEquals(18f, results.single().layoutInput.style.lineHeight.value)
    }

    @Test fun groupGapTracksTheSmallestSizeAndExposesListSemantics() {
        var size by mutableStateOf(ItemSize.Default)
        setThemedContent(stylePreset = StylePreset.Nova) {
            ItemGroup(Modifier.width(320.dp).testTag("group")) {
                Item("First", Modifier.testTag("first"), size = size)
                Item("Second", Modifier.testTag("second"))
            }
        }

        fun gap() =
            composeRule.onNodeWithTag("second").fetchSemanticsNode().boundsInRoot.top -
                composeRule.onNodeWithTag("first").fetchSemanticsNode().boundsInRoot.bottom
        assertEquals(16f, gap(), 0.1f)
        composeRule.runOnIdle { size = ItemSize.Sm }
        assertEquals(10f, gap(), 0.1f)
        composeRule.runOnIdle { size = ItemSize.Xs }
        assertEquals(8f, gap(), 0.1f)
        val list = composeRule.onNodeWithTag("group").fetchSemanticsNode().config[SemanticsProperties.CollectionInfo]
        assertEquals(2, list.rowCount)
        assertEquals(
            0,
            composeRule.onNodeWithTag(
                "first",
            ).fetchSemanticsNode().config[SemanticsProperties.CollectionItemInfo].rowIndex,
        )
        assertEquals(
            1,
            composeRule.onNodeWithTag(
                "second",
            ).fetchSemanticsNode().config[SemanticsProperties.CollectionItemInfo].rowIndex,
        )
    }

    @Test fun aNonGrowingSecondContentKeepsItsIntrinsicWidth() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Item(Modifier.width(320.dp)) {
                ItemContent(Modifier.testTag("main")) {
                    ItemTitle("Song")
                    ItemDescription("Artist")
                }
                ItemContent(Modifier.testTag("duration"), fill = false) { ItemDescription("3:45") }
            }
        }
        val main = composeRule.onNodeWithTag("main").fetchSemanticsNode().boundsInRoot
        val duration = composeRule.onNodeWithTag("duration").fetchSemanticsNode().boundsInRoot
        assertTrue(main.width > duration.width * 3)
        assertEquals(main.top + main.height / 2, duration.top + duration.height / 2, 1f)
    }

    @Test fun hrefNavigationAndDisabledAreReal() {
        var opened = ""
        var disabled by mutableStateOf(false)
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(
                LocalUriHandler provides
                    object : UriHandler {
                        override fun openUri(uri: String) {
                            opened = uri
                        }
                    },
            ) {
                Item("Dashboard", href = "/dashboard", disabled = disabled)
            }
        }
        composeRule.onNodeWithText("Dashboard").assert(
            SemanticsMatcher.keyNotDefined(SemanticsProperties.Role),
        ).performClick()
        assertEquals("/dashboard", opened)
        composeRule.runOnIdle {
            opened = ""
            disabled = true
        }
        composeRule.onNodeWithText("Dashboard").assertIsNotEnabled().performTouchInput { click(center) }
        assertEquals("", opened)
    }

    @Test fun titleWrapsAndDescriptionKeepsTwoLines() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Item(Modifier.width(150.dp)) {
                ItemContent {
                    ItemTitle("A very long title that cannot fit in one line")
                    ItemDescription(
                        (
                            "A long description with several words that should take many " +
                                "more than the two allowed lines of available width."
                        ),
                    )
                }
            }
        }
        val title = mutableListOf<TextLayoutResult>()
        val description = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithText(
            "A very long title that cannot fit in one line",
        ).performSemanticsAction(SemanticsActions.GetTextLayoutResult) {
            it(title)
        }
        composeRule.onNodeWithText(
            (
                "A long description with several words that should take many " +
                    "more than the two allowed lines of available width."
            ),
        ).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(description) }
        assertTrue(title.single().lineCount > 1)
        assertEquals(Int.MAX_VALUE, title.single().layoutInput.maxLines)
        assertEquals(androidx.compose.ui.text.style.TextOverflow.Clip, title.single().layoutInput.overflow)
        assertEquals(124, title.single().layoutInput.constraints.maxWidth)
        assertFalse(title.single().hasVisualOverflow)
        assertEquals(2, description.single().lineCount)
        assertEquals(2, description.single().layoutInput.maxLines)
        assertEquals(androidx.compose.ui.text.style.TextOverflow.Ellipsis, description.single().layoutInput.overflow)
        assertTrue(description.single().hasVisualOverflow)
    }

    @Test fun explicitSingleLineTitleKeepsTheImageRecipeClamp() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Item(Modifier.width(150.dp)) {
                ItemContent { ItemTitle("A very long title that cannot fit in one line", maxLines = 1) }
            }
        }
        val results = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithText("A very long title that cannot fit in one line")
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
        assertEquals(1, results.single().lineCount)
        assertEquals(1, results.single().layoutInput.maxLines)
        assertEquals(androidx.compose.ui.text.style.TextOverflow.Ellipsis, results.single().layoutInput.overflow)
        assertTrue(results.single().hasVisualOverflow)
    }

    private fun nova(dark: Boolean) {
        snapshot("item_nova", darkTheme = dark, stylePreset = StylePreset.Nova) {
            Column(Modifier.width(440.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ItemSize.entries.forEach { size ->
                    Item(size = size, variant = ItemVariant.Outline) {
                        ItemMedia(variant = ItemMediaVariant.Image) { Box(Modifier.fillMaxSize()) }
                        ItemContent {
                            ItemTitle(
                                size.name +
                                    " Size",
                            )
                            ; ItemDescription("The standard size for most use cases.")
                        }
                        ItemActions { Text { "Action" } }
                    }
                }
                Item(Modifier.width(150.dp), variant = ItemVariant.Outline) {
                    ItemContent {
                        ItemTitle("A very long title that cannot fit in one line", maxLines = 1)
                        ItemDescription(
                            (
                                "A long description with several words that should take many " +
                                    "more than the two allowed lines of available width."
                            ),
                        )
                    }
                }
            }
        }
    }

    @Test fun nova_light() = nova(false)

    @Test fun nova_dark() = nova(true)
}
