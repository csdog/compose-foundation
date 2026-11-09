package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
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

    @Test fun outlineRecipeKeepsTheLiteralAllSelection() {
        show("Outline")
        rule.onNodeWithText("All").assertIsOn()
        rule.onNodeWithText("Missed").assertIsOff()
        rule.onNodeWithText("Missed").performClick()
        rule.onNodeWithText("Missed").assertIsOn()
        rule.onNodeWithText("All").assertIsOn()
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
        rule.onNodeWithContentDescription("Toggle bold").assertIsOn()
        rule.onNodeWithContentDescription("Toggle bold").performClick()
        rule.onNodeWithContentDescription("Toggle bold").assertIsOn()
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
