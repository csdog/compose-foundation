package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.checkboxDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(Parameterized::class)
class CheckboxViewportTest(private val width: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()
    private val checkboxes = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox)

    private fun show(name: String) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                OverlayHost {
                    CompositionLocalProvider(LocalCatalogViewportWidth provides width.dp) {
                        val example = checkboxDoc.examples.single { it.title == name }
                        Box(Modifier.width((if (width < 640) 325 else 638).dp).testTag("preview")) {
                            Box(Modifier.padding(example.previewPadding)) {
                                example.preview()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun textLayout(text: String): TextLayoutResult {
        val results = mutableListOf<TextLayoutResult>()
        rule.onNodeWithText(text).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
        return results.single()
    }

    @Test fun tablePreviewUsesTheOfficialResponsiveInset() {
        show("Table")
        val preview = rule.onNodeWithTag("preview").fetchSemanticsNode().boundsInRoot
        val table = rule.onNodeWithContentDescription("People").fetchSemanticsNode().boundsInRoot
        val inset = if (width < 768) 16f else 32f
        assertEquals(inset, table.left - preview.left, .5f)
        assertEquals(inset, preview.right - table.right, .5f)
    }

    @Test fun descriptionHasTwoCompleteTwentyOnePixelLines() {
        show("Description")
        assertEquals(42, textLayout("By clicking this checkbox, you agree to the terms and conditions.").size.height)
        rule.onAllNodes(checkboxes)[0].assertIsOn()
        rule.onNodeWithText("Accept terms and conditions").performClick()
        rule.onAllNodes(checkboxes)[0].assertIsOff().assertIsFocused()
    }

    @Test fun groupDefaultsAndLabelActivationAreIndependent() {
        show("Group")
        rule.onAllNodes(checkboxes).assertCountEquals(4)
        rule.onAllNodes(checkboxes)[0].assertIsOn()
        rule.onAllNodes(checkboxes)[1].assertIsOn()
        rule.onAllNodes(checkboxes)[2].assertIsOff()
        rule.onNodeWithText("Hard disks").performClick()
        rule.onAllNodes(checkboxes)[0].assertIsOff()
        rule.onAllNodes(checkboxes)[1].assertIsOn()
        val legend = rule.onNodeWithText("Show these items on the desktop:").fetchSemanticsNode().boundsInRoot
        val description =
            rule.onNodeWithText(
                "Select the items you want to show on the desktop.",
            ).fetchSemanticsNode().boundsInRoot
        assertEquals(22f, description.top - legend.top, 1f)
        val nodes = rule.onAllNodes(checkboxes).fetchSemanticsNodes()
        assertTrue(nodes[1].boundsInRoot.top - nodes[0].boundsInRoot.top in 30f..33f)
    }

    @Test fun rtlLanguagesKeepSelectionAndDisabledState() {
        show("RTL")
        rule.onAllNodes(checkboxes).assertCountEquals(4)
        rule.onAllNodes(checkboxes)[2].assertIsNotEnabled()
        rule.onAllNodes(checkboxes)[0].performClick()
        rule.onAllNodes(checkboxes)[0].assertIsOn()
        for ((language, title) in listOf(
            "English" to "Accept terms and conditions",
            "Hebrew (עברית)" to "קבל תנאים והגבלות",
        )) {
            rule.onNodeWithContentDescription("Preview language").performClick()
            rule.onNodeWithText(language).performClick()
            rule.onAllNodesWithText(title)[0].assertIsDisplayed()
            rule.onAllNodes(checkboxes)[0].assertIsOn()
            rule.onAllNodes(checkboxes)[2].assertIsNotEnabled()
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}-{1}")
        fun parameters() =
            listOf(
                arrayOf<Any>(1440, false),
                arrayOf<Any>(1440, true),
                arrayOf<Any>(390, false),
                arrayOf<Any>(390, true),
            )
    }
}
