package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.catalog.docs.itemDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.*

class ItemExamplesTest {
    @get:Rule val rule = createComposeRule()
    private val visited = mutableListOf<String>()

    private fun show(
        title: String,
        width: () -> Dp = { 680.dp },
        navigation: DocNavigation? = null,
        externalLink: () -> ((String) -> Unit)? = { null },
    ) {
        rule.setContent {
            CompositionLocalProvider(
                LocalDocNavigation provides navigation,
                LocalDocExternalLink provides externalLink(),
                LocalUriHandler provides
                    object : UriHandler {
                        override fun openUri(uri: String) {
                            visited += uri
                        }
                    },
            ) {
                Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                    OverlayHost {
                        Box(Modifier.width(width()).height(650.dp), contentAlignment = Alignment.Center) {
                            itemDoc.examples.single { it.title == title }.RenderPreview()
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
            itemDoc.introMarkdown!!.contains("straightforward flex container"),
        )
        val vsField = itemDoc.beforeExamples.single { it.title == "Item vs Field" }
        assertEquals(2, vsField.blocks.size)
        assertTrue(
            (vsField.blocks.first() as com.toutiaoyan.compose.ui.catalog.docs.ComponentGuideBlock.Paragraph)
                .markdown
                .contains("form input such as a checkbox"),
        )
    }

    @Test fun exportedPageKeepsIntroductionAndCompositionGuidanceAtTheirSourcePositions() {
        val markdown = componentPageMarkdown(itemDoc)
        val intro = assertNotNull(itemDoc.introMarkdown)
        val composition = assertNotNull(itemDoc.beforeCompositionMarkdown)
        val positions =
            listOf(
                itemDoc.examples.first().code,
                intro,
                "## Installation",
                "## Usage",
                "## Composition",
                composition,
                assertNotNull(itemDoc.compositionCode),
                "## Item vs Field",
                "## Variant",
            ).map { text -> markdown.indexOf(text).also { assertTrue(it >= 0, "Missing $text") } }
        assertEquals(positions.sorted(), positions)
        assertEquals(1, markdown.split(intro).size - 1)
        assertNull(itemDoc.examples.first().descriptionMarkdown)
        assertFalse(markdown.contains("## $composition"))
        assertTrue(itemDoc.usageCode.contains("ItemMedia(variant = ItemMediaVariant.Icon)"))
        val withoutLeadIn = componentPageMarkdown(itemDoc.copy(beforeCompositionMarkdown = null))
        assertEquals(withoutLeadIn, markdown.replace("$composition\n\n", ""))
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

    @Test fun headerKeepsThreeColumnsAtPhoneWidthAndWrapsOnNarrowerScreens() {
        val width = mutableStateOf(325.dp)
        show("Header", width = { width.value })

        fun photo(name: String) = rule.onNodeWithContentDescription(name).assertIsDisplayed()

        val first = photo("v0-1.5-sm")
        val second = photo("v0-1.5-lg")
        val third = photo("v0-2.0-mini")
        first.assertWidthIsAtLeast(60.dp)
        assertEquals(first.getUnclippedBoundsInRoot().top, second.getUnclippedBoundsInRoot().top)
        assertEquals(first.getUnclippedBoundsInRoot().top, third.getUnclippedBoundsInRoot().top)

        rule.runOnIdle { width.value = 255.dp }
        assertEquals(first.getUnclippedBoundsInRoot().top, second.getUnclippedBoundsInRoot().top)
        assertTrue(third.getUnclippedBoundsInRoot().top > first.getUnclippedBoundsInRoot().bottom)
    }

    @Test fun samePageAndExternalLinksUseTheirOwnNavigationActions() {
        val fragments = mutableListOf<String>()
        val externalUrls = mutableListOf<String>()
        val externalLink = mutableStateOf<((String) -> Unit)?>(null)
        val navigation =
            object : DocNavigation {
                override val location = DocLocation("item", "link")

                override fun navigate(fragment: String) {
                    fragments += fragment
                }
            }
        show("Link", navigation = navigation, externalLink = { externalLink.value })

        rule.onNodeWithText("Visit our documentation").performClick()
        assertEquals(listOf(""), fragments)
        assertTrue(visited.isEmpty())

        rule.onNodeWithText("External resource").performClick()
        assertEquals(listOf("#"), visited)
        assertEquals(listOf(""), fragments)

        rule.runOnIdle { externalLink.value = { externalUrls += it } }
        rule.onNodeWithText("External resource").performClick()
        assertEquals(listOf("#"), externalUrls)
        assertEquals(listOf("#"), visited)
    }

    @Test fun dropdownHasOneKeyboardTriggerAndEscapeRestoresFocus() {
        show("Dropdown")
        rule.onAllNodes(hasText("Select") and hasClickAction()).assertCountEquals(1)
        val trigger = rule.onNodeWithText("Select")
        trigger.requestFocus().assertIsFocused()
        trigger.performKeyInput { pressKey(Key.Enter) }
        val firstEntry = rule.onNodeWithText("shadcn@vercel.com").assertIsDisplayed()
        firstEntry.performKeyInput { pressKey(Key.Escape) }
        firstEntry.assertDoesNotExist()
        trigger.assertIsFocused()
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
