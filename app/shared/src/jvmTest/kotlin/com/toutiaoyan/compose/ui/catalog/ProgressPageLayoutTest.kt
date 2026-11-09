package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.WindowInfo
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.catalog.docs.progressDoc
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.*

@RunWith(Parameterized::class)
class ProgressPageLayoutTest(private val viewport: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "viewport={0},dark={1}")
        fun modes() =
            listOf(
                arrayOf<Any>(1440, false),
                arrayOf<Any>(1440, true),
                arrayOf<Any>(390, false),
                arrayOf<Any>(390, true),
            )
    }

    private fun show(title: String) {
        rule.setContent {
            val original = LocalWindowInfo.current
            val density = LocalDensity.current
            val window =
                remember(original, density) {
                    object : WindowInfo by original {
                        override val containerSize =
                            with(density) { IntSize(viewport.dp.roundToPx(), 1000.dp.roundToPx()) }
                    }
                }
            CompositionLocalProvider(LocalWindowInfo provides window) {
                Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                    val example = progressDoc.examples.single { it.title == title }
                    Box(
                        Modifier.width((if (viewport == 390) 325 else 638).dp).heightIn(min = example.minPreviewHeight)
                            .padding(example.previewPadding),
                        contentAlignment = Alignment.Center,
                    ) { example.preview() }
                }
            }
        }
        rule.waitForIdle()
    }

    @Test fun pageContainsOnlyOfficialRecipesAndExplainsKotlinExtensions() {
        assertEquals(listOf("Default", "Label", "Controlled", "RTL"), progressDoc.examples.map { it.title })
        assertNull(progressDoc.examplesHeading)
        assertNull(progressDoc.compositionCode)
        assertEquals(3, progressDoc.examples.count { !it.descriptionMarkdown.isNullOrBlank() })
        assertTrue(progressDoc.afterExamples.isNotEmpty())
        val rtlCode = progressDoc.examples.single { it.title == "RTL" }.code
        assertContains(rtlCode, "FontWeight.Normal")
        assertContains(rtlCode, "FontWeight.Medium")
        assertContains(rtlCode, "import androidx.compose.ui.text.font.FontWeight")
        progressDoc.examples.forEach {
            assertFalse(it.code.contains("ui.catalog"))
            assertFalse(it.code.contains("testTag"))
            assertTrue(it.code.trimEnd().endsWith("Recipe()"))
        }
    }

    @Test fun compositionGuidancePrecedesTheTreeInThePageAndCopiedMarkdown() {
        val markdown = componentPageMarkdown(progressDoc)
        val fragments =
            listOf(
                "## Composition",
                "### With label and value",
                "Use `ProgressLabel` and `ProgressValue` to add a label and value display.",
                "Progress(value = 56f",
                "└── ProgressIndicator",
                "## Label",
            )
        val offsets =
            fragments.map { fragment ->
                markdown.indexOf(fragment).also { assertTrue(it >= 0, "$fragment missing") }
            }
        assertEquals(offsets.sorted(), offsets)
        val sections = docSections(progressDoc)
        assertEquals(1, sections.count { it.title == "Composition" })
        assertEquals(1, sections.count { it.title == "With label and value" })
        assertTrue(sections.single { it.title == "With label and value" }.nested)
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                Box(Modifier.width(viewport.dp).height(1000.dp)) { ComponentDetailContent(progressDoc) }
            }
        }
        val heading = rule.onNodeWithTag("heading-before-guide-1").getUnclippedBoundsInRoot()
        val sample =
            rule.onNodeWithText("Progress(value = 56f", substring = true, useUnmergedTree = true)
                .getUnclippedBoundsInRoot()
        val tree =
            rule.onNodeWithText("└── ProgressIndicator", substring = true, useUnmergedTree = true)
                .getUnclippedBoundsInRoot()
        assertTrue(heading.bottom <= sample.top)
        assertTrue(sample.bottom <= tree.top)
    }

    @Test fun labelAndTrackUseTheOfficialAvailableWidth() {
        show("Label")
        rule.onNodeWithTag("progress-recipe").assertWidthIsEqualTo((if (viewport == 390) 245 else 384).dp)
            .assertHeightIsEqualTo(36.dp)
    }

    private data class LanguageCase(
        val current: String,
        val next: String,
        val label: String,
        val value: String,
        val rtl: Boolean,
    )

    @Test fun rtlUsesOne288BodyAndConsistentLabelRowsAcrossLanguages() {
        show("RTL")
        val body = rule.onNodeWithTag("progress-rtl-body")
        body.assertHeightIsEqualTo(288.dp)
        val progress = rule.onNodeWithTag("progress-recipe")
        progress.assertWidthIsEqualTo((if (viewport == 390) 245 else 384).dp)
        for ((current, next, label, value, rtl) in listOf(
            LanguageCase("Arabic (العربية)", "English", "Upload progress", "56%", false),
            LanguageCase("English", "Hebrew (עברית)", "התקדמות העלאה", "56%", true),
            LanguageCase("Hebrew (עברית)", "Arabic (العربية)", "تقدم الرفع", "٥٦%", true),
        )) {
            rule.onNodeWithText(current).performClick()
            rule.waitForIdle()
            rule.onNodeWithText(next).performClick()
            rule.waitForIdle()
            progress.assertHeightIsEqualTo(36.dp)
            val l = rule.onNodeWithText(label, useUnmergedTree = true).getUnclippedBoundsInRoot()
            val v = rule.onNodeWithText(value, useUnmergedTree = true).getUnclippedBoundsInRoot()
            assertEquals(20f, l.height.value, 0.6f)
            assertEquals(20f, v.height.value, 0.6f)
            assertEquals(l.top.value, v.top.value, 0.6f)
            assertEquals(rtl, l.left > v.left)
            body.assertHeightIsEqualTo(288.dp)
        }
    }
}
