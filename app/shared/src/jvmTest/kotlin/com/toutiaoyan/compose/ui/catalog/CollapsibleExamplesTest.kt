package com.toutiaoyan.compose.ui.catalog

import androidx.compose.ui.Alignment
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.ComponentGuideBlock
import com.toutiaoyan.compose.ui.catalog.docs.collapsibleDoc
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class CollapsibleExamplesTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun officialRecipeOrderMatchesCurrentPage() {
        assertEquals("Collapsible", collapsibleDoc.title)
        assertEquals("An interactive component which expands/collapses a panel.", collapsibleDoc.description)
        assertEquals(null, collapsibleDoc.examplesHeading)
        assertEquals(
            listOf("Default", "Basic", "Settings Panel", "File Tree", "RTL"),
            collapsibleDoc.examples.take(5).map { it.title },
        )
        val extensions = collapsibleDoc.examples.drop(5)
        assertEquals(4, extensions.size)
        assertTrue(extensions.all { it.title.startsWith("Kotlin extension: ") })

        val default = collapsibleDoc.examples.first()
        assertEquals(false, default.showHeading)
        assertEquals(288.dp, default.previewHeight)
        assertEquals(Alignment.Top, default.previewVerticalAlignment)
        assertEquals(576.dp, collapsibleDoc.examples.single { it.title == "File Tree" }.previewHeight)
        assertEquals(352.dp, collapsibleDoc.examples.single { it.title == "RTL" }.previewHeight)
    }

    @Test
    fun controlledStateAndApiReferenceMatchTheOfficialPage() {
        val controlled = collapsibleDoc.beforeExamples.single { it.title == "Controlled State" }
        assertTrue(controlled.blocks.any { it is ComponentGuideBlock.Paragraph })
        val code =
            assertIs<ComponentGuideBlock.Code>(controlled.blocks.filterIsInstance<ComponentGuideBlock.Code>().single())
        assertTrue(code.code.contains("open = open"))
        assertTrue(code.code.contains("onOpenChange = { open = it }"))

        assertEquals(
            "Collapsible\n├── CollapsibleTrigger\n└── CollapsibleContent",
            collapsibleDoc.compositionCode,
        )
        assertEquals("Use the following composition to build a `Collapsible`:", collapsibleDoc.compositionMarkdown)
        val api = collapsibleDoc.afterExamples.single { it.title == "API Reference" }
        val paragraph = assertIs<ComponentGuideBlock.Paragraph>(api.blocks.single())
        assertTrue(
            paragraph.markdown.contains(
                "https://base-ui.com/react/components/collapsible#api-reference",
            ),
        )
    }

    @Test
    fun copyPageMarkdownKeepsOfficialHeadingOrder() {
        val markdown = componentPageMarkdown(collapsibleDoc)
        val headings =
            listOf(
                "## Controlled State",
                "## Basic",
                "## Settings Panel",
                "## File Tree",
                "## RTL",
                "## API Reference",
            )
        val positions =
            headings.map {
                    heading ->
                markdown.indexOf(heading).also { assertTrue(it >= 0, "missing $heading") }
            }
        assertEquals(positions.sorted(), positions)
        assertTrue(markdown.contains("Use the following composition to build a `Collapsible`:"))
        assertFalse(markdown.contains("## Examples"))
        assertTrue(markdown.contains("## Kotlin extension: Disabled"))
    }

    @Test
    fun defaultPreviewOpensAndClosesTheOrderDetails() {
        val example = collapsibleDoc.examples.first()
        rule.setContent { Theme(preset = StylePreset.Nova, minTouchTarget = null) { example.preview() } }
        rule.onNodeWithText("Order #4189").assertIsDisplayed()
        rule.onNodeWithText("Status").assertIsDisplayed()
        rule.onNodeWithText("Shipping address").assertDoesNotExist()

        rule.onNodeWithTag("collapsible-default-toggle").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Shipping address").assertIsDisplayed()
        rule.onNodeWithText("100 Market St, San Francisco").assertIsDisplayed()
        rule.onNodeWithText("2x Studio Headphones").assertIsDisplayed()

        rule.onNodeWithTag("collapsible-default-toggle").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Shipping address").assertDoesNotExist()
    }

    @Test
    fun basicPreviewRevealsPanelContentAndLearnMoreButton() {
        val example = collapsibleDoc.examples.single { it.title == "Basic" }
        rule.setContent { Theme(preset = StylePreset.Nova, minTouchTarget = null) { example.preview() } }
        rule.onNodeWithText("Product details").assertIsDisplayed()
        rule.onNodeWithText("This panel can be expanded", substring = true).assertDoesNotExist()

        rule.onNodeWithTag("collapsible-basic-toggle").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("This panel can be expanded", substring = true).assertIsDisplayed()
        rule.onNodeWithText("Learn More").assertIsDisplayed()
    }

    @Test
    fun settingsPanelRevealsTheHiddenRadiusFields() {
        val example = collapsibleDoc.examples.single { it.title == "Settings Panel" }
        rule.setContent { Theme(preset = StylePreset.Nova, minTouchTarget = null) { example.preview() } }
        rule.onNodeWithText("Radius").assertIsDisplayed()
        rule.onNodeWithText("Set the corner radius of the element.").assertIsDisplayed()
        rule.onAllNodes(hasSetTextAction()).assertCountEquals(2)

        rule.onNodeWithTag("collapsible-settings-toggle").performClick()
        rule.waitForIdle()
        rule.onAllNodes(hasSetTextAction()).assertCountEquals(4)
    }

    @Test
    fun settingsRadiusFieldsKeepAccessibleNamesWhenExpanded() {
        val example = collapsibleDoc.examples.single { it.title == "Settings Panel" }
        rule.setContent { Theme(preset = StylePreset.Nova, minTouchTarget = null) { example.preview() } }
        rule.onAllNodes(hasSetTextAction() and hasContentDescription("Radius X")).assertCountEquals(1)
        rule.onAllNodes(hasSetTextAction() and hasContentDescription("Radius Y")).assertCountEquals(1)

        rule.onNodeWithTag("collapsible-settings-toggle").performClick()
        rule.waitForIdle()
        rule.onAllNodes(hasSetTextAction() and hasContentDescription("Radius X")).assertCountEquals(2)
        rule.onAllNodes(hasSetTextAction() and hasContentDescription("Radius Y")).assertCountEquals(2)
        rule.onAllNodes(hasSetTextAction() and hasContentDescription("Radius X"))[1].performTextReplacement("12")
        rule.onNodeWithTag("collapsible-settings-toggle").performClick()
        rule.waitForIdle()
        rule.onAllNodes(hasSetTextAction() and hasContentDescription("Radius X")).assertCountEquals(1)
    }

    @Test
    fun fileTreeExpandsNestedFolders() {
        val example = collapsibleDoc.examples.single { it.title == "File Tree" }
        rule.setContent { Theme(preset = StylePreset.Nova, minTouchTarget = null) { example.preview() } }
        rule.onNodeWithText("Explorer").assertExists()
        rule.onNodeWithText("components").assertExists()
        rule.onNodeWithText("login-form.tsx").assertDoesNotExist()

        rule.onNodeWithTag("file-tree-components").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("login-form.tsx").assertExists()
        rule.onNodeWithText("ui").assertExists()

        rule.onNodeWithTag("file-tree-ui").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("button.tsx").assertExists()
    }

    @Test
    fun rtlPreviewStartsWithArabicOrderCopy() {
        val example = collapsibleDoc.examples.single { it.title == "RTL" }
        rule.setContent { Theme(preset = StylePreset.Nova, minTouchTarget = null) { example.preview() } }
        rule.onNodeWithText("الطلب #4189").assertExists()
        rule.onNodeWithText("الحالة").assertExists()
        rule.onNodeWithText("تم الشحن").assertExists()
    }
}
