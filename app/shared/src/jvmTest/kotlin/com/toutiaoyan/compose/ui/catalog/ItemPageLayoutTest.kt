package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.itemDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.*

@RunWith(Parameterized::class)
class ItemPageLayoutTest(private val phone: Boolean, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "phone={0},dark={1}")
        fun modes() = listOf(arrayOf(false, false), arrayOf(false, true), arrayOf(true, false), arrayOf(true, true))
    }

    private fun show(title: String) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                OverlayHost {
                    val example = itemDoc.examples.single { it.title == title }
                    Box(
                        Modifier.width((if (phone) 325 else 638).dp).heightIn(min = example.minPreviewHeight)
                            .padding(example.previewPadding),
                        contentAlignment = Alignment.Center,
                    ) { example.preview() }
                }
            }
        }
    }

    private fun layout(text: String): TextLayoutResult {
        val results = mutableListOf<TextLayoutResult>()
        rule.onNodeWithText(text, useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
        return results.single()
    }

    @Test fun ordinaryTitleWrapsAtPhoneWidthInsteadOfDiscardingTheVerifiedMessage() {
        show("Default")
        val title = layout("Your profile has been verified.")
        assertEquals(if (phone) 2 else 1, title.lineCount)
        assertFalse(title.hasVisualOverflow)
        rule.onNodeWithText("Your profile has been verified.").assertIsDisplayed()
    }

    @Test fun richDropdownPeopleKeepAvatarBesideTheNameAndEmail() {
        show("Dropdown")
        rule.onNodeWithText("Select").performClick()
        for (name in listOf("shadcn", "maxleiter", "evilrabbit")) {
            val photo =
                rule.onNodeWithContentDescription("@" + name, useUnmergedTree = true)
                    .getUnclippedBoundsInRoot()
            val title = rule.onNodeWithText(name, useUnmergedTree = true).getUnclippedBoundsInRoot()
            assertTrue(photo.right <= title.left, "$name avatar must precede its text on the same row")
            assertTrue(kotlin.math.abs(photo.top.value - title.top.value) <= 5f)
        }
    }

    @Test fun rtlBodyHasOneInsetAndDirectionChangesWithAllThreeLanguages() {
        show("RTL")
        val body = rule.onNodeWithTag("item-rtl-body")
        body.assertHeightIsEqualTo(288.dp)
        rule.onNodeWithTag("item-rtl-content").assertWidthIsEqualTo((if (phone) 245 else 448).dp)

        fun direction(
            action: String,
            title: String,
            rtl: Boolean,
        ) {
            val a = rule.onNodeWithText(action).getUnclippedBoundsInRoot()
            val t = rule.onNodeWithText(title).getUnclippedBoundsInRoot()
            assertEquals(rtl, a.left < t.left)
            assertFalse(layout(title).hasVisualOverflow)
        }
        direction("إجراء", "عنصر أساسي", true)
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("English").performClick()
        direction("Action", "Basic Item", false)
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("Hebrew (עברית)").performClick()
        direction("פעולה", "פריט בסיסי", true)
        body.assertHeightIsEqualTo(288.dp)
        val code = itemDoc.examples.single { it.title == "RTL" }.code
        assertFalse(code.contains("ui.catalog"))
        assertFalse(code.contains("testTag"))
        assertTrue(code.trimEnd().endsWith("ItemRtlRecipe()"))
    }
}
