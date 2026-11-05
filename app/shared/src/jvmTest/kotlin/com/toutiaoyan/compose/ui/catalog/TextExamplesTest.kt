package com.toutiaoyan.compose.ui.catalog

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.toutiaoyan.compose.ui.catalog.docs.ComponentGuideBlock
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.catalog.docs.textDoc
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class TextExamplesTest {
    @get:Rule val rule = createComposeRule()

    @Test fun pageIsLabeledAsKotlinExtensionAndUsesCurrentApiNames() {
        assertEquals("Text", textDoc.title)
        assertTrue(textDoc.isKotlinExtension)
        assertTrue(textDoc.beforeExamples.single { it.title == "Kotlin extension" }
            .blocks.filterIsInstance<ComponentGuideBlock.Paragraph>().isNotEmpty())
        assertTrue(textDoc.usageCode.contains("TextVariant.TitleLarge"))
        assertTrue(textDoc.usageCode.contains("TextColors.Muted"))
        assertFalse(textDoc.usageCode.contains("TextTypography"))
        assertFalse(textDoc.usageCode.contains("TextStyles"))
        assertEquals("import com.toutiaoyan.compose.ui.components.text.*", textDoc.usageImportsCode)
        assertEquals(null, textDoc.examplesHeading)
        assertEquals("Text", textDoc.apiSections.single().title)
    }

    @Test fun recipeOrderAndCopyAreConsistent() {
        assertEquals(
            listOf("Type scale", "Variants", "Muted", "Kotlin extension: Style font-size"),
            textDoc.examples.map { it.title },
        )
        assertFalse(textDoc.examples.first().showHeading)
        textDoc.examples.forEach { example ->
            assertFalse(example.code.contains("TextTypography"), example.title)
            assertFalse(example.code.contains("TextStyles"), example.title)
        }
    }

    @Test fun variantsPreviewRendersEveryTypeScaleEntry() {
        val example = textDoc.examples.single { it.title == "Variants" }
        rule.setContent { Theme(preset = StylePreset.Nova, minTouchTarget = null) { example.preview() } }
        listOf("DisplayLarge", "DisplayMedium", "TitleLarge", "TitleMedium", "TitleSmall",
            "BodyLarge", "BodyMedium", "BodySmall", "LabelLarge", "LabelSmall").forEach { name ->
            rule.onNodeWithText(name).assertExists()
        }
    }

    @Test fun mutedPreviewKeepsMutedCopy() {
        val example = textDoc.examples.single { it.title == "Muted" }
        rule.setContent { Theme(preset = StylePreset.Nova, minTouchTarget = null) { example.preview() } }
        rule.onNodeWithText("Muted body").assertIsDisplayed()
    }

    @Test fun copyPageMarkdownHasNoRemovedApiNames() {
        val markdown = componentPageMarkdown(textDoc)
        assertFalse(markdown.contains("TextTypography"))
        assertFalse(markdown.contains("TextStyles"))
        assertTrue(markdown.contains("## Variants"))
        assertTrue(markdown.contains("### Kotlin extension: Style font-size") || markdown.contains("## Kotlin extension: Style font-size"))
        val guide = textDoc.beforeExamples.single { it.title == "Kotlin extension" }
        assertIs<ComponentGuideBlock.Paragraph>(guide.blocks.single())
    }
}
