package com.toutiaoyan.compose.ui.components.card

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class CardNovaParityTest : ScreenshotTest() {
    private fun matrix(dark: Boolean) =
        snapshot("card_nova_parity", darkTheme = dark, stylePreset = StylePreset.Nova) {
            Column(Modifier.width(384.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Card {
                    CardHeader {
                        CardTitle("Login to your account")
                        CardDescription("Enter your email below to login to your account")
                        CardAction { Text { "Sign Up" } }
                    }
                    CardContent("Email and password")
                    CardFooter { Button("Login") }
                }
                Card(size = CardSize.Sm) {
                    CardHeader("Small card", "Compact title and spacing")
                    CardFooter { Text { "Footer" } }
                }
            }
        }

    @Test fun lightMatrix() = matrix(false)

    @Test fun darkMatrix() = matrix(true)

    @Test fun footerBleedsToRootEdgesAndActionSharesTheTitleRow() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Card(Modifier.width(320.dp).testTag("card")) {
                    CardHeader {
                        CardTitle("Title", Modifier.testTag("title"))
                        CardDescription("Description", Modifier.testTag("description"))
                        CardAction(Modifier.testTag("action")) { Text { "Edit" } }
                    }
                    CardFooter(Modifier.testTag("footer")) { Text { "Footer" } }
                }
            }
        }

        fun bounds(tag: String) = composeRule.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot
        val card = bounds("card")
        val footer = bounds("footer")
        assertEquals(card.left, footer.left)
        assertEquals(card.right, footer.right)
        assertEquals(card.bottom, footer.bottom)
        assertEquals(bounds("title").top, bounds("action").top)
        assertTrue(bounds("description").right < bounds("action").left)
        val text = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithTag("title").performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(text) }
        assertEquals(16.sp, text.single().layoutInput.style.fontSize)
        assertEquals(FontWeight.Medium, text.single().layoutInput.style.fontWeight)
    }

    @Test fun nestedSmallCardDoesNotChangeOuterTypography() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                Card(Modifier.width(320.dp)) {
                    Card(size = CardSize.Sm) { CardTitle("Inner", Modifier.testTag("inner")) }
                    CardTitle("Outer", Modifier.testTag("outer"))
                }
            }
        }
        listOf("inner" to 14.sp, "outer" to 16.sp).forEach { (tag, expected) ->
            val results = mutableListOf<TextLayoutResult>()
            composeRule.onNodeWithTag(tag).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
            assertEquals(expected, results.single().layoutInput.style.fontSize)
        }
    }

    @Test fun headerCanMeasureInAnUnboundedHorizontalContainer() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                Box(Modifier.width(320.dp).horizontalScroll(rememberScrollState())) {
                    Card(Modifier.testTag("card")) {
                        CardHeader {
                            CardTitle("Title")
                            CardAction { Text { "Edit" } }
                        }
                    }
                }
            }
        }
        val width = composeRule.onNodeWithTag("card").fetchSemanticsNode().boundsInRoot.width
        assertTrue(width > 0 && width < 320)
    }
}
