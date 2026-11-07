package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.*
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.*

@RunWith(Parameterized::class)
class ButtonFollowupRecipesTest(private val width: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()

    private fun show(content: @Composable () -> Unit) {
        rule.setContent {
            CompositionLocalProvider(LocalCatalogViewportWidth provides width.dp) {
                Theme(
                    preset = StylePreset.Nova,
                    isDark = dark,
                    minTouchTarget = null,
                    fontFamily = catalogFontFamily(),
                ) {
                    OverlayHost {
                        Box(
                            Modifier.requiredSize((if (width < 768) 325 else 638).dp, 600.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            content()
                        }
                    }
                }
            }
        }
    }

    @Test fun styledLinkHasUrlAnnotationAndClickingLabelOrPaddingOpensExactlyOnce() {
        var opens = 0
        show { ButtonAsLinkRecipe(onOpen = { opens++ }) }
        val layouts = mutableListOf<TextLayoutResult>()
        rule.onNodeWithText("Login").performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        val text = layouts.single().layoutInput.text
        val link = text.getLinkAnnotations(0, text.length).single().item as LinkAnnotation.Url
        assertEquals("#", link.url)
        assertNotEquals(
            Role.Button,
            rule.onNodeWithTag("button-as-link").fetchSemanticsNode().config.getOrNull(SemanticsProperties.Role),
        )
        rule.onNodeWithTag("button-as-link").assertHeightIsEqualTo(28.dp)
        rule.onNodeWithText("Login").performTouchInput { click() }
        assertEquals(1, opens)
        rule.onNodeWithTag("button-as-link").performTouchInput { click(Offset(3f, center.y)) }
        assertEquals(2, opens, "Padding is part of the link target, without a duplicate label click")
    }

    @Test fun linkHasOneKeyboardStopAndEnterActivatesIt() {
        var opens = 0
        show {
            Column {
                ButtonAsLinkRecipe(onOpen = { opens++ })
                Button("After")
            }
        }
        rule.onRoot().performKeyInput {
            pressKey(Key.Tab)
            pressKey(Key.Enter)
        }
        assertEquals(1, opens)
        rule.onRoot().performKeyInput { pressKey(Key.Tab) }
        rule.onNodeWithContentDescription("After").assertIsFocused()
    }

    @Test fun messageActionsMenuHasAllActionsAndKeepsRadioSelectionAfterReopening() {
        show { ButtonGroupDefaultRecipe() }
        if (width < 640) {
            rule.onNodeWithContentDescription("Go Back").assertDoesNotExist()
        } else {
            rule.onNodeWithContentDescription("Go Back").assertIsDisplayed()
        }
        rule.onNodeWithContentDescription("More Options").performClick()
        listOf("Mark as Read", "Add to Calendar", "Add to List", "Trash").forEach {
            rule.onNodeWithText(it).assertIsDisplayed()
        }
        rule.onAllNodesWithTag("message-action-icon", useUnmergedTree = true).assertCountEquals(6)
        rule.onAllNodesWithTag("message-action-icon", useUnmergedTree = true).fetchSemanticsNodes().forEach {
            assertEquals(16f, it.boundsInRoot.width, .1f)
        }
        rule.onNodeWithText("Label As...").performClick()
        rule.onNodeWithTag("button-group-label-submenu").assertWidthIsEqualTo(96.dp)
        rule.onNodeWithText("Personal").assertIsSelected()
        rule.onNodeWithText("Other").performClick().assertIsSelected()
        rule.onNodeWithText("Mark as Read").assertIsDisplayed()
        rule.onNodeWithText("Other").performKeyInput { pressKey(Key.Escape) }
        rule.onNodeWithText("Label As...").performKeyInput { pressKey(Key.Escape) }
        rule.onNodeWithContentDescription("More Options").assertIsFocused().performClick()
        rule.onNodeWithText("Label As...").performClick()
        rule.onNodeWithText("Other").assertIsSelected()
        rule.onNodeWithText("Other").performKeyInput { pressKey(Key.Escape) }
        rule.onNodeWithText("Trash").performClick()
        rule.onNodeWithText("Mark as Read").assertDoesNotExist()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "width={0}, dark={1}")
        fun cases(): List<Array<Any>> = listOf(390, 1440).flatMap { w -> listOf(false, true).map { arrayOf(w, it) } }
    }
}
