@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.test.junit4.DesktopComposeTestRule
import com.toutiaoyan.compose.ui.catalog.docs.CodeBlock
import com.toutiaoyan.compose.ui.catalog.docs.alertDoc
import com.toutiaoyan.compose.ui.catalog.docs.attachmentDoc
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ComponentDocumentationTest {
    @get:Rule val rule = createComposeRule()

    @Test fun attachmentGuidanceExportAndTocKeepSourceOrderAndReadableFragments() {
        val markdown = componentPageMarkdown(attachmentDoc)
        val headings =
            listOf(
                "## Composition", "## Features", "## Image", "## States", "## Sizes", "## Group", "## Trigger",
                "## Accessibility",
                "### Label icon-only actions",
                "### Label the trigger",
                "### Keyboard scrolling",
                "### Meaning beyond color",
                "## API Reference",
            )
        val positions = headings.map { markdown.indexOf(it).also { index -> assertTrue(index >= 0, "$it missing") } }
        assertEquals(positions.sorted(), positions)
        assertContains(markdown, "- Icon and image previews through `AttachmentMedia`.")
        assertContains(markdown, "label = \"Remove sales-dashboard.pdf\"")
        assertContains(markdown, "Arrow keys scroll the row")
        assertContains(markdown, "failure reason in `AttachmentDescription`")
        assertFalse(markdown.contains("aria-label"))
        val sections = docSections(attachmentDoc)
        assertEquals(
            listOf(
                "features",
                "accessibility",
                "label-icon-only-actions",
                "label-the-trigger",
                "keyboard-scrolling",
                "meaning-beyond-color",
            ),
            sections.filter { "guide" in it.id }.map { it.fragment },
        )
        assertTrue(sections.filter { it.id.startsWith("after-guide-") && it.id != "after-guide-0" }.all { it.nested })
        assertEquals(sections.size, sections.map { it.fragment }.distinct().size)
    }

    @OptIn(androidx.compose.ui.InternalComposeUiApi::class, androidx.compose.ui.test.InternalTestApi::class)
    @Test
    fun attachmentGuidanceLoadsByFragmentCopiesAndNavigatesThroughAllSections() {
        // Resize the real test scene; requiredSize alone would center a larger
        // viewport outside the default scene and give negative screen bounds.
        rule.runOnUiThread { (rule as DesktopComposeTestRule).scene.size = IntSize(1440, 900) }
        var copied: String? = null
        val clipboard =
            object : Clipboard {
                override suspend fun getClipEntry(): ClipEntry? = null

                override suspend fun setClipEntry(clipEntry: ClipEntry?) {
                    copied =
                        (clipEntry?.nativeClipEntry as? Transferable)
                            ?.getTransferData(DataFlavor.stringFlavor) as? String
                }
            }
        val scroll = ScrollState(0)
        val navigation =
            object : DocNavigation {
                override var location by mutableStateOf(DocLocation("attachment", "features"))

                override fun navigate(fragment: String) {
                    location = location.copy(fragment = fragment, revision = location.revision + 1, animate = true)
                }
            }
        rule.setContent {
            CompositionLocalProvider(
                LocalClipboard provides clipboard,
                LocalDocNavigation provides navigation,
                LocalCatalogViewportWidth provides 1440.dp,
            ) {
                Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                    Box(
                        Modifier.requiredSize(1440.dp, 900.dp),
                    ) { ComponentDetailContent(attachmentDoc, scrollState = scroll) }
                }
            }
        }

        fun assertVisible(id: String) {
            val bounds = rule.onNodeWithTag("heading-$id").fetchSemanticsNode().boundsInRoot
            assertTrue(
                bounds.left >= 0f && bounds.right <= 1440f && bounds.top >= 0f && bounds.bottom < 900f,
                "$id must actually be visible: $bounds",
            )
        }
        assertVisible("before-guide-0")
        rule.runOnIdle { scroll.dispatchRawDelta(-scroll.value.toFloat()) }
        rule.onNodeWithText("Copy Page").performClick()
        rule.waitForIdle()
        assertEquals(componentPageMarkdown(attachmentDoc), copied)
        val guides = docSections(attachmentDoc).filter { "guide" in it.id }
        for (section in guides) {
            rule.onNodeWithTag("toc-${section.id}").performSemanticsAction(SemanticsActions.OnClick) { it() }
            rule.waitForIdle()
            assertVisible(section.id)
            assertEquals(section.fragment, navigation.location.fragment)
        }
        rule.onNodeWithTag("toc-before-guide-0").performSemanticsAction(SemanticsActions.OnClick) { it() }
        rule.waitForIdle()
        rule.onNode(hasText("Features") and isHeading()).performTouchInput { click(center) }
        rule.waitForIdle()
        assertEquals("features", navigation.location.fragment)
        assertEquals(8, navigation.location.revision, "TOC links and the heading share the browser bridge")
    }

    @Test fun alertExportIncludesDescriptionsCompositionAndKotlinApiInPageOrder() {
        val markdown = componentPageMarkdown(alertDoc)
        assertTrue(markdown.indexOf("## Default") < markdown.indexOf("## Installation"))
        assertTrue(markdown.indexOf("## Usage") < markdown.indexOf("## Composition"))
        assertTrue(markdown.indexOf("## Composition") < markdown.indexOf("## Basic"))
        assertTrue(markdown.indexOf("## RTL") < markdown.indexOf("## API Reference"))
        assertContains(markdown, "Set `variant = AlertVariant.Destructive`")
        assertContains(markdown, "[RTL configuration guide](https://ui.shadcn.com/docs/rtl)")
        assertContains(markdown, "### AlertAction")
        assertContains(markdown, alertDoc.usageImportsCode!!)
        assertContains(markdown, alertDoc.usageCode)
        assertContains(markdown, "@Composable AlertScope.() -> Unit")
        assertFalse(markdown.contains("className"))
        assertFalse(docSections(alertDoc).any { it.id == "examples" })
    }

    @Test fun configuredExamplesHeadingIsIncludedInCopiedMarkdown() {
        val markdown = componentPageMarkdown(alertDoc.copy(examplesHeading = "Examples"))
        assertTrue(markdown.indexOf("## Composition") < markdown.indexOf("## Examples"))
        assertTrue(markdown.indexOf("## Examples") < markdown.indexOf("## Basic"))
    }

    @Test fun plainCompositionBlockCopiesItsOwnCodeAndShowsFeedback() {
        var copied: String? = null
        val code = "Alert\n└── AlertTitle"
        val clipboard =
            object : Clipboard {
                override suspend fun getClipEntry(): ClipEntry? = null

                override suspend fun setClipEntry(clipEntry: ClipEntry?) {
                    copied =
                        (clipEntry?.nativeClipEntry as? Transferable)
                            ?.getTransferData(DataFlavor.stringFlavor) as? String
                }
            }
        rule.setContent {
            CompositionLocalProvider(LocalClipboard provides clipboard) {
                Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                    Box(Modifier.requiredSize(600.dp, 300.dp)) { CodeBlock(code, language = null) }
                }
            }
        }
        rule.onNodeWithContentDescription("Copy").performClick()
        rule.waitForIdle()
        assertEquals(code, copied)
        rule.onNodeWithContentDescription("Copied").assertExists()
    }

    @Test fun actualCopyPageExportsDescriptionsAndNewTocAnchorsScrollIntoView() {
        var copied: String? = null
        val clipboard =
            object : Clipboard {
                override suspend fun getClipEntry(): ClipEntry? = null

                override suspend fun setClipEntry(clipEntry: ClipEntry?) {
                    copied =
                        (clipEntry?.nativeClipEntry as? Transferable)
                            ?.getTransferData(DataFlavor.stringFlavor) as? String
                }
            }
        val scroll = ScrollState(0)
        rule.setContent {
            CompositionLocalProvider(LocalClipboard provides clipboard, LocalCatalogViewportWidth provides 1440.dp) {
                Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                    Box(Modifier.requiredSize(1440.dp, 768.dp)) {
                        ComponentDetailContent(alertDoc, scrollState = scroll)
                    }
                }
            }
        }
        rule.onNodeWithText("Copy Page").performClick()
        rule.waitForIdle()
        assertEquals(componentPageMarkdown(alertDoc), copied)
        assertContains(copied.orEmpty(), "`contentColor`")
        for (id in listOf("composition", "api-3")) {
            rule.onNodeWithTag("toc-$id").performSemanticsAction(SemanticsActions.OnClick) { it() }
            rule.waitForIdle()
            val heading = rule.onNodeWithTag("heading-$id").fetchSemanticsNode().boundsInRoot
            assertTrue(heading.top < 768f && heading.bottom > 0f, "$id must scroll into view: $heading")
        }
    }

    @Test fun fragmentNavigationLoadsRestoresAndRepeatsRealSectionScroll() {
        val scroll = ScrollState(0)
        val navigation =
            object : DocNavigation {
                override var location by mutableStateOf(DocLocation("alert", "composition"))

                override fun navigate(fragment: String) {
                    location = location.copy(fragment = fragment, revision = location.revision + 1, animate = true)
                }
            }
        rule.setContent {
            CompositionLocalProvider(
                LocalDocNavigation provides navigation,
                LocalCatalogViewportWidth provides 1440.dp,
            ) {
                Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                    Box(Modifier.requiredSize(1440.dp, 768.dp)) {
                        ComponentDetailContent(alertDoc, scrollState = scroll)
                    }
                }
            }
        }

        fun assertHeadingVisible(id: String) {
            val heading = rule.onNodeWithTag("heading-$id").fetchSemanticsNode().boundsInRoot
            assertTrue(heading.top < 768f && heading.bottom > 0f, "$id must be visible: $heading")
        }
        assertHeadingVisible("composition")
        rule.onNode(hasText("Composition") and isHeading()).performTouchInput { click(center) }
        rule.waitForIdle()
        assertEquals("composition", navigation.location.fragment)
        assertEquals(1, navigation.location.revision, "The real heading must use the browser navigation bridge")
        rule.onNodeWithTag("toc-api-3").performSemanticsAction(SemanticsActions.OnClick) { it() }
        rule.waitForIdle()
        assertEquals("alertaction", navigation.location.fragment)
        assertHeadingVisible("api-3")
        rule.runOnIdle { scroll.dispatchRawDelta(-scroll.value.toFloat()) }
        rule.onNodeWithTag("toc-api-3").performSemanticsAction(SemanticsActions.OnClick) { it() }
        rule.waitForIdle()
        assertHeadingVisible("api-3")
        val previous = scroll.value
        rule.runOnIdle { navigation.location = DocLocation("alert", "unknown-section") }
        rule.waitForIdle()
        assertEquals(previous, scroll.value, "Unknown fragments leave scroll alone")
        rule.runOnIdle { navigation.location = DocLocation("button", "usage") }
        rule.waitForIdle()
        assertEquals(previous, scroll.value, "A different route must not move this document")
        rule.runOnIdle { navigation.location = DocLocation("alert", "") }
        rule.waitForIdle()
        assertEquals(0, scroll.value, "Returning to the URL without a fragment restores page top")
    }

    @Test fun generatedSectionFragmentsAreReadableAndUnique() {
        val example = alertDoc.examples.first()
        val doc =
            alertDoc.copy(
                examples =
                    listOf(
                        example,
                        example.copy(title = "Usage"),
                        example.copy(title = "Usage"),
                        example.copy(title = "usage-1"),
                    ),
            )
        val sections = docSections(doc)
        assertEquals(
            listOf("usage", "usage-1", "usage-2", "usage-1-1"),
            sections.filter {
                it.title.lowercase().startsWith("usage")
            }.map { it.fragment },
        )
        assertEquals(sections.size, sections.map { it.fragment }.toSet().size)
        assertEquals("custom-colors", docSections(alertDoc).single { it.title == "Custom Colors" }.fragment)
    }
}
