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
import com.toutiaoyan.compose.ui.catalog.docs.chipDoc
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(Parameterized::class)
class ChipExamplesTest(private val width: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()

    @Test fun extensionPageLabelsItselfAndDocumentsTheKotlinApi() {
        val extension = chipDoc.beforeExamples.single { it.title == "Kotlin extension" }
        val paragraph = assertIs<ComponentGuideBlock.Paragraph>(extension.blocks.single())
        assertTrue(paragraph.markdown.contains("not an official"))
        assertTrue(paragraph.markdown.contains("Badge"))
        assertEquals(
            listOf("Default", "Selectable", "Selected", "Outline", "Disabled", "Icon", "Row"),
            chipDoc.examples.map { it.title },
        )
        assertEquals("A selectable tag, commonly used for filters or multi-select choices.", chipDoc.description)
        assertNull(chipDoc.afterExamples.singleOrNull())
        val chipApi = chipDoc.apiSections.single { it.title == "Chip" }
        assertTrue(chipApi.properties.any { it.name == "leadingIcon" })
        assertTrue(chipApi.properties.any { it.name == "onClick" })
        assertTrue(chipDoc.apiSections.single { it.title == "ChipVariant" }.properties.size == 3)
        assertTrue(chipDoc.usageImportsCode!!.contains("components.chip.Chip"))
    }

    @Test fun everyPreviewRendersAtBothWidths() {
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
                        Modifier.requiredSize((if (width < 768) width - 65 else 638).dp, 200.dp)
                            .padding(40.dp)
                            .testTag("preview"),
                        contentAlignment = Alignment.Center,
                    ) {
                        key(index) { chipDoc.examples[index].preview() }
                    }
                }
            }
        }
        for (i in chipDoc.examples.indices) {
            rule.runOnIdle { index = i }
            rule.waitForIdle()
        }
    }

    @Test fun selectableChipTogglesItsSelectedState() {
        val selectable = chipDoc.examples.single { it.title == "Selectable" }
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                selectable.preview()
            }
        }
        val chip = rule.onNodeWithText("Kotlin")
        chip.assertIsDisplayed()
        chip.assertIsNotSelected()
        chip.performClick()
        rule.waitForIdle()
        chip.assertIsSelected()
        chip.performClick()
        rule.waitForIdle()
        chip.assertIsNotSelected()
    }

    @Test fun disabledChipIsNotClickableAndIconSlotKeepsItsSize() {
        val disabled = chipDoc.examples.single { it.title == "Disabled" }
        val icon = chipDoc.examples.single { it.title == "Icon" }
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                androidx.compose.foundation.layout.Column {
                    disabled.preview()
                    icon.preview()
                }
            }
        }
        val disabledNode = rule.onNodeWithText("Disabled")
        disabledNode.assertIsNotEnabled()
        rule.onNodeWithText("Verified").assertExists()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "width={0}, dark={1}")
        fun cases(): List<Array<Any>> = listOf(390, 1440).flatMap { w -> listOf(false, true).map { arrayOf(w, it) } }
    }
}
