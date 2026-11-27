package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.ComponentGuideBlock
import com.toutiaoyan.compose.ui.catalog.docs.comboboxDoc
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(Parameterized::class)
class ComboboxExamplesTest(private val width: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()

    @Test fun officialPageOrderAndCopyMatchCurrentOfficialPage() {
        assertEquals(
            listOf(
                "Default",
                "Basic",
                "Multiple",
                "Clear Button",
                "Groups",
                "Custom Items",
                "Invalid",
                "Disabled",
                "Auto Highlight",
                "Popup",
                "Input Group",
                "RTL",
            ),
            comboboxDoc.examples.map { it.title },
        )
        assertEquals("Autocomplete input with a list of suggestions.", comboboxDoc.description)
        assertEquals(null, comboboxDoc.examplesHeading)
        assertTrue(comboboxDoc.compositionCode!!.contains("With chips"))
        assertTrue(comboboxDoc.compositionCode!!.contains("With groups and collection"))
        assertEquals(
            "A single-line input and a flat list (see [Basic](#basic)).\n\n" +
                "Multi-select with `multiple`, chips, and a chips input (see [Multiple](#multiple)).\n\n" +
                "Nested items per group using `ComboboxCollection` inside each `ComboboxGroup`, " +
                "with a separator between groups (see [Groups](#groups)).",
            comboboxDoc.compositionMarkdown,
        )
        val markdown = componentPageMarkdown(comboboxDoc)
        val compositionGuidance = requireNotNull(comboboxDoc.compositionMarkdown)
        assertTrue(markdown.contains(compositionGuidance))
        assertTrue(markdown.indexOf(compositionGuidance) < markdown.indexOf("## Custom Items"))
        assertEquals(
            listOf("Custom Items", "Multiple Selection"),
            comboboxDoc.beforeExamples.map { it.title },
        )
        assertEquals(
            "Use `multiple` with chips for multi-select behavior.",
            (
                comboboxDoc.beforeExamples[1].blocks
                    .single { it is ComponentGuideBlock.Paragraph } as ComponentGuideBlock.Paragraph
            ).markdown,
        )
        assertEquals(
            "A simple combobox with a list of frameworks.",
            comboboxDoc.examples.single { it.title == "Basic" }.descriptionMarkdown,
        )
        assertEquals(
            "A combobox with multiple selection using `multiple` and `ComboboxChips`.",
            comboboxDoc.examples.single { it.title == "Multiple" }.descriptionMarkdown,
        )
        val api = comboboxDoc.apiSections.first().descriptionMarkdown
        assertTrue(api.contains("https://base-ui.com/react/components/combobox#api-reference"))
        assertTrue(comboboxDoc.usageImportsCode!!.contains("components.combobox.Combobox"))
    }

    @Test fun everyOfficialPreviewRendersAtBothWidths() {
        var index by mutableIntStateOf(0)
        rule.setContent {
            CompositionLocalProvider(LocalCatalogViewportWidth provides width.dp) {
                Theme(
                    preset = StylePreset.Nova,
                    isDark = dark,
                    minTouchTarget = null,
                    fontFamily = catalogFontFamily(),
                ) {
                    Box(
                        Modifier.requiredSize((if (width < 768) width - 65 else 638).dp, 320.dp)
                            .padding(40.dp)
                            .testTag("preview"),
                        contentAlignment = Alignment.Center,
                    ) {
                        key(index) { comboboxDoc.examples[index].RenderPreview() }
                    }
                }
            }
        }
        assertEquals(12, comboboxDoc.examples.size)
        for (i in comboboxDoc.examples.indices) {
            rule.runOnIdle { index = i }
            rule.waitForIdle()
        }
    }

    @Test fun editableInputOpensFiltersAndPicks() {
        val basic = comboboxDoc.examples.single { it.title == "Basic" }
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                basic.RenderPreview()
            }
        }
        rule.onNodeWithText("Select a framework").assertExists()
        rule.onNodeWithText("Next.js").assertDoesNotExist()
        rule.onNodeWithText("Select a framework").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Next.js").assertExists()
        rule.onNodeWithText("Astro").assertExists()
        rule.onNode(hasSetTextAction()).performTextInput("nu")
        rule.waitForIdle()
        rule.onNodeWithText("Nuxt.js").assertExists()
        rule.onNodeWithText("Next.js").assertDoesNotExist()
        rule.onNodeWithText("Nuxt.js").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Nuxt.js").assertExists()
        assertFalse(comboboxDoc.examples.single { it.title == "Basic" }.code.contains("ComboboxTrigger()"))
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "width={0}, dark={1}")
        fun cases(): List<Array<Any>> = listOf(390, 1440).flatMap { w -> listOf(false, true).map { arrayOf(w, it) } }
    }
}
