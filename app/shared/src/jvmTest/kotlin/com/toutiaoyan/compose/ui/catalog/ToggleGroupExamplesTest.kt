package com.toutiaoyan.compose.ui.catalog

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.graphics.Color
import com.toutiaoyan.compose.ui.theme.currentTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.PreviewCodeSection
import com.toutiaoyan.compose.ui.catalog.docs.toggleGroupDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ToggleGroupExamplesTest {
    @get:Rule val rule = createComposeRule()

    private fun show(title: String) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.width(720.dp).height(720.dp), contentAlignment = Alignment.TopCenter) {
                        toggleGroupDoc.examples.single { it.title == title }.preview()
                    }
                }
            }
        }
    }

    @Test fun officialRecipesAndKotlinExtensionsAppearInOrder() {
        assertEquals(
            listOf(
                "Default", "Outline", "Size", "Spacing", "Vertical", "Disabled", "Custom", "RTL",
                "Kotlin extension: Single select", "Kotlin extension: Connected",
            ),
            toggleGroupDoc.examples.map { it.title },
        )
        assertEquals(
            "Use `variant=\"outline\"` for an outline style.",
            toggleGroupDoc.examples.single { it.title == "Outline" }.descriptionMarkdown,
        )
        assertTrue(
            toggleGroupDoc.examples.single { it.title == "Size" }
                .descriptionMarkdown.orEmpty()
                .contains("`size` prop"),
        )
        assertTrue(
            toggleGroupDoc.examples.single { it.title == "Spacing" }
                .descriptionMarkdown.orEmpty()
                .contains("spacing"),
        )
        assertEquals(
            "A custom toggle group example.",
            toggleGroupDoc.examples.single { it.title == "Custom" }.descriptionMarkdown,
        )
        assertTrue(
            toggleGroupDoc.examples.single { it.title == "RTL" }.descriptionMarkdown.orEmpty()
                .startsWith("To enable RTL support"),
        )
        assertTrue(toggleGroupDoc.compositionCode.orEmpty().contains("ToggleGroupItem"))
    }

    @Test fun outlineRecipeReplacesAndDeselectsTheSelection() {
        show("Outline")
        rule.onNodeWithText("All").assertIsOn()
        rule.onNodeWithText("Missed").assertIsOff()
        rule.onNodeWithText("Missed").performClick()
        rule.onNodeWithText("Missed").assertIsOn()
        rule.onNodeWithText("All").assertIsOff()
        rule.onNodeWithText("Missed").performClick().assertIsOff()
    }

    @Test fun sizeRowsReplaceAndDeselectIndependently() {
        show("Size")
        repeat(2) { index ->
            rule.onAllNodesWithText("Top")[index].assertIsOn()
            rule.onAllNodesWithText("Bottom")[index].performClick().assertIsOn()
            rule.onAllNodesWithText("Top")[index].assertIsOff()
            rule.onAllNodesWithText("Bottom")[index].performClick().assertIsOff()
        }
    }

    @Test fun spacingRecipeReplacesAndDeselects() {
        show("Spacing")
        rule.onNodeWithText("Top").assertIsOn()
        rule.onNodeWithText("Bottom").performClick().assertIsOn()
        rule.onNodeWithText("Top").assertIsOff()
        rule.onNodeWithText("Bottom").performClick().assertIsOff()
    }

    @Test fun rtlRecipeReplacesAndDeselects() {
        show("RTL")
        rule.onNodeWithText("قائمة").assertIsOn()
        rule.onNodeWithText("شبكة").performClick().assertIsOn()
        rule.onNodeWithText("قائمة").assertIsOff()
        rule.onNodeWithText("شبكة").performClick().assertIsOff()
    }

    @Test fun singleSelectionIsExplicitInOfficialRecipeCode() {
        listOf("Outline", "Size", "Spacing", "Disabled", "RTL").forEach { title ->
            val code = toggleGroupDoc.examples.single { it.title == title }.code
            val groups = code.lineSequence().filter { it.trimStart().startsWith("ToggleGroup(") }.toList()
            assertEquals(if (title == "Size") 2 else 1, groups.size)
            assertTrue(groups.all { "type = ToggleGroupType.Single" in it }, title)
        }
    }

    @Test fun singleSelectRecipeDeselectsTheActiveItem() {
        show("Kotlin extension: Single select")
        rule.onNodeWithText("I").assertIsOn()
        rule.onNodeWithText("I").performClick()
        rule.onNodeWithText("I").assertIsOff()
        rule.onNodeWithText("B").performClick()
        rule.onNodeWithText("B").assertIsOn()
        rule.onNodeWithText("I").assertIsOff()
    }

    @Test fun verticalRecipeStacksItsItems() {
        show("Vertical")
        rule.onNodeWithContentDescription("Toggle bold").assertIsOn()
        rule.onNodeWithContentDescription("Toggle italic").assertIsOn()
        rule.onNodeWithContentDescription("Toggle underline").assertIsOff()
        val bold = rule.onNodeWithContentDescription("Toggle bold").fetchSemanticsNode().boundsInRoot
        val italic = rule.onNodeWithContentDescription("Toggle italic").fetchSemanticsNode().boundsInRoot
        assertTrue(italic.top > bold.bottom, "vertical group should stack below")
    }

    @Test fun disabledRecipeDoesNotToggle() {
        show("Disabled")
        listOf("Toggle bold", "Toggle italic", "Toggle strikethrough").forEach { label ->
            rule.onNodeWithContentDescription(label).assertIsNotEnabled().assertIsOff()
                .performClick().assertIsOff()
        }
    }

    @Test fun customSurfaceAndDescriptionFollowSelection() {
        show("Custom")
        val light = rule.onNodeWithContentDescription("Light")
        val normal = rule.onNodeWithContentDescription("Normal")
        light.assertWidthIsEqualTo(64.dp).assertHeightIsEqualTo(64.dp)
        normal.assertWidthIsEqualTo(64.dp).assertHeightIsEqualTo(64.dp).assertIsOn()
        val gap = normal.fetchSemanticsNode().boundsInRoot.left - light.fetchSemanticsNode().boundsInRoot.right
        assertEquals(with(rule.density) { 8.dp.toPx() }, gap, 0.5f)
        light.performClick().assertIsOn()
        normal.assertIsOff()
        rule.onNodeWithText("Use font-light to set the font weight.").assertExists()
    }

    @Test fun customInlineCodePaintAndTypographyFollowThemeAndSelection() {
        val dark = mutableStateOf(false)
        var muted = Color.Unspecified
        var mono: androidx.compose.ui.text.font.FontFamily? = null
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark.value, minTouchTarget = null) {
                muted = currentTheme.colors.muted
                mono = catalogMonoFontFamily()
                toggleGroupDoc.examples.single { it.title == "Custom" }.preview()
            }
        }
        listOf(false, true, false).forEach { isDark ->
            rule.runOnIdle { dark.value = isDark }
            listOf("Light", "Normal", "Medium", "Bold").forEach { label ->
                rule.onNodeWithContentDescription(label).performClick()
                val code = "font-${label.lowercase()}"
                rule.onNodeWithText("Use $code to set the font weight.").assertExists()
                val chip = rule.onNodeWithTag("font-weight-code", useUnmergedTree = true)
                chip.assertHeightIsEqualTo(21.dp)
                rule.onNodeWithTag("font-weight-description").assertHeightIsEqualTo(21.dp)
                val layouts = mutableListOf<TextLayoutResult>()
                rule.onNodeWithText(code, useUnmergedTree = true)
                    .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
                val layout = layouts.single()
                assertEquals(mono, layout.layoutInput.style.fontFamily)
                assertEquals(14.sp, layout.layoutInput.style.fontSize)
                assertEquals(1, layout.lineCount)
                val pixels = chip.captureToImage().toPixelMap()
                assertEquals(muted, pixels[pixels.width / 2, 1], "muted chip fill, dark=$isDark")
                assertTrue(pixels[0, 0].alpha < 1f, "rounded chip corner")
            }
        }
        val code = toggleGroupDoc.examples.single { it.title == "Custom" }.code
        assertTrue(code.contains("FontWeightDescription(fontWeight)"))
        assertTrue(code.contains("Placeholder(slotWidth, 21.sp, PlaceholderVerticalAlign.TextCenter)"))
        assertTrue(code.contains("catalogMonoFontFamily()"))
    }

    @Test fun customTextLineBoxesHaveThePinnedGapAndCenteredStack() {
        show("Custom")
        val samples = rule.onAllNodesWithText("Aa", useUnmergedTree = true)
        listOf("Light", "Normal", "Medium", "Bold").forEachIndexed { index, label ->
            val card = rule.onNodeWithContentDescription(label).fetchSemanticsNode().boundsInRoot
            val layouts = mutableListOf<TextLayoutResult>()
            samples[index].performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
            val layout = layouts.single()
            assertEquals(24.sp, layout.layoutInput.style.fontSize)
            assertEquals(24.sp, layout.layoutInput.style.lineHeight)
            assertEquals(1, layout.lineCount)
            assertTrue(!layout.isLineEllipsized(0))
            assertEquals(2, layout.getLineEnd(0))
            val sample =
                rule.onNodeWithTag("font-sample-${label.lowercase()}", useUnmergedTree = true)
                    .fetchSemanticsNode().boundsInRoot
            val glyph = samples[index].fetchSemanticsNode().boundsInRoot
            assertEquals(sample.center.y, glyph.center.y, 0.5f, "$label unclipped glyph center")
            val caption =
                rule.onNodeWithTag("font-caption-${label.lowercase()}", useUnmergedTree = true)
                    .fetchSemanticsNode().boundsInRoot
            with(rule.density) {
                assertEquals(24.dp.toPx(), sample.height, 0.5f, "$label sample line box")
                assertEquals(16.dp.toPx(), caption.height, 0.5f, "$label caption line box")
                assertEquals(4.dp.toPx(), caption.top - sample.bottom, 0.5f, "$label text gap")
                assertEquals(44.dp.toPx(), caption.bottom - sample.top, 0.5f, "$label stack height")
            }
            assertEquals(card.center.y, (sample.top + caption.bottom) / 2, 0.5f, "$label centered stack")
        }
    }

    @Test fun customDisplayedCodePreservesThePinnedTextGap() {
        val code = toggleGroupDoc.examples.single { it.title == "Custom" }.code
        assertTrue(code.contains("verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)"))
    }

    @Test fun customLabelsRenderOnOneLineInsideTheirCards() {
        show("Custom")
        listOf("Light", "Normal", "Medium", "Bold").forEach { label ->
            val layouts = mutableListOf<TextLayoutResult>()
            val text = rule.onNodeWithText(label, useUnmergedTree = true)
            text.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
            assertEquals(1, layouts.single().lineCount, "$label must not wrap")
            val layout = layouts.single()
            assertEquals(label.length, layout.getLineEnd(0), "$label must be complete")
            assertTrue(!layout.isLineEllipsized(0), "$label must not be ellipsized")
            assertTrue(layout.getLineRight(0) <= layout.size.width, "$label must fit its text layout")
            val card = rule.onNodeWithContentDescription(label).fetchSemanticsNode().boundsInRoot
            val bounds = text.fetchSemanticsNode().boundsInRoot
            assertTrue(bounds.left >= card.left && bounds.right <= card.right, "$label must fit its card")
        }
    }

    @Test fun customPhonePreviewPreservesEveryCardAndGap() {
        val example = toggleGroupDoc.examples.single { it.title == "Custom" }
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    // Reproduce the 342dp preview frame measured in the 390dp browser capture.
                    Box(Modifier.width(342.dp).height(720.dp)) {
                        PreviewCodeSection(
                            code = example.code,
                            previewPadding = example.previewPadding,
                            hideCode = true,
                            preview = example.preview,
                        )
                    }
                }
            }
        }
        var previousRight: Float? = null
        listOf("Light", "Normal", "Medium", "Bold").forEach { label ->
            val card = rule.onNodeWithContentDescription(label)
            card.assertWidthIsEqualTo(64.dp).assertHeightIsEqualTo(64.dp)
            val bounds = card.fetchSemanticsNode().boundsInRoot
            assertTrue(bounds.left >= 0 && bounds.right <= with(rule.density) { 342.dp.toPx() })
            previousRight?.let { right ->
                assertEquals(with(rule.density) { 8.dp.toPx() }, bounds.left - right, 0.5f)
            }
            previousRight = bounds.right
            val layouts = mutableListOf<TextLayoutResult>()
            rule.onNodeWithText(label, useUnmergedTree = true)
                .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
            assertEquals(1, layouts.single().lineCount, "$label must not wrap on phones")
            val layout = layouts.single()
            assertEquals(label.length, layout.getLineEnd(0))
            assertTrue(!layout.isLineEllipsized(0), "$label must not be ellipsized on phones")
            assertTrue(layout.getLineRight(0) <= layout.size.width, "$label must fit its phone text layout")
            val textBounds = rule.onNodeWithText(label, useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
            assertTrue(
                textBounds.left >= bounds.left && textBounds.right <= bounds.right,
                "$label must fit its phone card",
            )
        }
    }

    @Test fun spacingUsesTwoTailwindUnits() {
        show("Spacing")
        val top = rule.onNodeWithText("Top").fetchSemanticsNode().boundsInRoot
        val bottom = rule.onNodeWithText("Bottom").fetchSemanticsNode().boundsInRoot
        assertEquals(with(rule.density) { 8.dp.toPx() }, bottom.left - top.right, 0.5f)
    }

    @Test fun changelogRecordsDefaultSpacingMigration() {
        assertEquals(
            listOf("Changelog", "2026-05-17 Default Spacing"),
            toggleGroupDoc.afterApiSections.map { it.title },
        )
    }
}
