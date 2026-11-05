package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.itemDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.*

class ItemExamplesTest {
    @get:Rule val rule = createComposeRule()
    private val visited = mutableListOf<String>()

    private fun show(title: String) {
        rule.setContent {
            CompositionLocalProvider(
                LocalUriHandler provides
                    object : UriHandler {
                        override fun openUri(uri: String) {
                            visited += uri
                        }
                    },
            ) {
                Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                    OverlayHost {
                        Box(Modifier.width(680.dp).height(650.dp), contentAlignment = Alignment.Center) {
                            itemDoc.examples.single { it.title == title }.preview()
                        }
                    }
                }
            }
        }
    }

    @Test fun officialExamplesAndTenApiPartsAppearInOrder() {
        assertEquals(
            listOf(
                "Default",
                "Variant",
                "Size",
                "Icon",
                "Avatar",
                "Image",
                "Group",
                "Header",
                "Link",
                "Dropdown",
                "RTL",
            ),
            itemDoc.examples.map { it.title },
        )
        assertEquals(
            listOf(
                "Item",
                "ItemGroup",
                "ItemSeparator",
                "ItemMedia",
                "ItemContent",
                "ItemTitle",
                "ItemDescription",
                "ItemActions",
                "ItemHeader",
                "ItemFooter",
            ),
            itemDoc.apiSections.map { it.title },
        )
        assertFalse(itemDoc.examples.single { it.title == "RTL" }.code.contains("RtlPreview("))
    }

    @Test fun currentOfficialCopyIsUsedForEveryRecipeAndTheItemVsFieldGuide() {
        assertEquals(
            "Use the `variant` prop to change the visual style of the item.",
            itemDoc.examples.single { it.title == "Variant" }.descriptionMarkdown,
        )
        assertEquals(
            "Use the `size` prop to change the size of the item. Available sizes are `default`, `sm`, and `xs`.",
            itemDoc.examples.single { it.title == "Size" }.descriptionMarkdown,
        )
        assertEquals(
            "Use `ItemMedia` with `variant=\"icon\"` to display an icon.",
            itemDoc.examples.single { it.title == "Icon" }.descriptionMarkdown,
        )
        assertEquals(
            "You can use `ItemMedia` with `variant=\"avatar\"` to display an avatar.",
            itemDoc.examples.single { it.title == "Avatar" }.descriptionMarkdown,
        )
        assertEquals(
            "Use `ItemMedia` with `variant=\"image\"` to display an image.",
            itemDoc.examples.single { it.title == "Image" }.descriptionMarkdown,
        )
        assertEquals(
            "Use `ItemGroup` to group related items together.",
            itemDoc.examples.single { it.title == "Group" }.descriptionMarkdown,
        )
        assertEquals(
            "Use `ItemHeader` to add a header above the item content.",
            itemDoc.examples.single { it.title == "Header" }.descriptionMarkdown,
        )
        val link = itemDoc.examples.single { it.title == "Link" }
        assertTrue(link.descriptionMarkdown!!.contains("`href`") && link.descriptionMarkdown!!.contains("`render`"))
        assertTrue(
            itemDoc.examples
                .single { it.title == "RTL" }
                .descriptionMarkdown!!
                .contains("RTL configuration guide"),
        )
        assertTrue(
            itemDoc.examples
                .single { it.title == "Default" }
                .descriptionMarkdown!!
                .contains("straightforward flex container"),
        )
        val vsField = itemDoc.beforeExamples.single { it.title == "Item vs Field" }
        assertEquals(2, vsField.blocks.size)
        assertTrue(
            (vsField.blocks.first() as com.toutiaoyan.compose.ui.catalog.docs.ComponentGuideBlock.Paragraph)
                .markdown
                .contains("form input such as a checkbox"),
        )
    }

    @Test fun defaultIncludesTheActionAndTheVerifiedProfileLink() {
        show("Default")
        rule.onNodeWithText("Basic Item").assertIsDisplayed()
        rule.onNodeWithText("A simple item with title and description.").assertIsDisplayed()
        rule.onNodeWithText("Action").assertIsEnabled()
        rule.onNodeWithText("Your profile has been verified.").performClick()
        assertEquals(listOf("#"), visited)
    }

    @Test fun sizesUseTheExtraSmallDescriptionTypography() {
        show("Size")
        rule.onNodeWithText("Default Size").assertIsDisplayed()
        rule.onNodeWithText("Small Size").assertIsDisplayed()
        val results = mutableListOf<TextLayoutResult>()
        rule.onNodeWithText(
            "The most compact size available.",
        ).performSemanticsAction(SemanticsActions.GetTextLayoutResult) {
            it(results)
        }
        assertEquals(12f, results.single().layoutInput.style.fontSize.value)
    }

    @Test fun avatarAndTeamUseActualBundledPhotos() {
        show("Avatar")
        rule.onAllNodesWithContentDescription("@evilrabbit")[0].assertWidthIsEqualTo(40.dp).assertHeightIsEqualTo(40.dp)
        rule.onAllNodesWithContentDescription("@evilrabbit")[1].assertWidthIsEqualTo(32.dp).assertHeightIsEqualTo(32.dp)
        rule.onNodeWithText("Evil Rabbit").assertIsDisplayed()
        rule.onAllNodesWithContentDescription("Invite").assertCountEquals(2)
        rule.onAllNodesWithContentDescription("Invite")[0].assertIsEnabled()
        rule.onAllNodesWithContentDescription("Invite")[1].assertIsEnabled()
        rule.onNodeWithText("No Team Members").assertIsDisplayed()
    }

    @Test fun songsKeepTheirFixedDurationColumnAndNavigate() {
        show("Image")
        rule.onNodeWithText("3:45").assertIsDisplayed()
        rule.onNodeWithText("4:05").assertIsDisplayed()
        rule.onNodeWithText("3:30").assertIsDisplayed()
        rule.onNodeWithContentDescription("Midnight City Lights", useUnmergedTree = true).assertWidthIsEqualTo(40.dp)
        rule.onNodeWithText("Midnight City Lights - Electric Nights").performClick()
        assertEquals(listOf("#"), visited)
    }

    @Test fun headerPicturesSitAboveTheBodyAcrossThreeColumns() {
        show("Header")
        val pictures =
            listOf("v0-1.5-sm", "v0-1.5-lg", "v0-2.0-mini").map { name ->
                val photo =
                    rule.onNodeWithContentDescription(
                        name,
                    ).assertIsDisplayed().fetchSemanticsNode().boundsInRoot
                val title = rule.onNodeWithText(name).assertIsDisplayed().fetchSemanticsNode().boundsInRoot
                assertTrue(title.top >= photo.bottom + 9f)
                assertEquals(photo.width, photo.height, 1f)
                photo
            }
        assertTrue(pictures[0].left < pictures[1].left && pictures[1].left < pictures[2].left)
    }

    @Test fun dropdownRendersThreeRichItemsAndSelectionCloses() {
        show("Dropdown")
        rule.onNodeWithText("Select").performClick()
        rule.onNodeWithText("shadcn@vercel.com").assertIsDisplayed()
        rule.onNodeWithText("maxleiter@vercel.com").assertIsDisplayed()
        rule.onNodeWithText("evilrabbit@vercel.com").assertIsDisplayed()
        rule.onNodeWithText("maxleiter").performClick()
        rule.onNodeWithText("maxleiter@vercel.com").assertDoesNotExist()
    }

    @Test fun rtlChangesTheCopyAndMovesTheActionToTheOtherSide() {
        show("RTL")
        rule.onNodeWithText("عنصر أساسي").assertIsDisplayed()

        fun left(text: String) = rule.onNodeWithText(text).fetchSemanticsNode().boundsInRoot.left
        assertTrue(left("إجراء") < left("عنصر أساسي"))
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("English").performClick()
        assertTrue(left("Action") > left("Basic Item"))
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("Hebrew (עברית)").performClick()
        rule.onNodeWithText("פריט בסיסי").assertIsDisplayed()
        assertTrue(left("פעולה") < left("פריט בסיסי"))
        rule.onNodeWithText("הפרופיל שלך אומת.").performClick()
        assertEquals(listOf("#"), visited)
    }
}
