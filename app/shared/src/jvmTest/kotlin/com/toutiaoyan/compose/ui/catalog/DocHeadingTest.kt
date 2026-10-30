package com.toutiaoyan.compose.ui.catalog

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import com.toutiaoyan.compose.ui.catalog.docs.DocHeading
import com.toutiaoyan.compose.ui.theme.Theme
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class DocHeadingTest {
    @get:Rule val rule = createComposeRule()

    @Test fun headingLinkHasReadableFragmentAndNavigatesOnPointerClick() {
        var clicks = 0
        rule.setContent {
            Theme {
                CompositionLocalProvider(
                    LocalDocHeadingLinks provides mapOf("api-3" to DocHeadingLink("alertaction") { clicks++ }),
                ) {
                    DocHeading("AlertAction", anchorId = "api-3")
                }
            }
        }
        val heading = rule.onNode(hasText("AlertAction") and isHeading())
        val layouts = mutableListOf<TextLayoutResult>()
        heading.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        val text = layouts.single().layoutInput.text
        assertEquals("#alertaction", (text.getLinkAnnotations(0, text.length).single().item as LinkAnnotation.Url).url)
        heading.performTouchInput { click(center) }
        assertEquals(1, clicks)
        rule.onNodeWithText("#").assertDoesNotExist()
    }

    @Test fun headingLinkCanBeReachedAndActivatedWithKeyboard() {
        var clicks = 0
        rule.setContent {
            Theme {
                CompositionLocalProvider(
                    LocalDocHeadingLinks provides mapOf("usage" to DocHeadingLink("usage") { clicks++ }),
                ) {
                    DocHeading("Usage", anchorId = "usage")
                }
            }
        }
        rule.onRoot().performKeyInput {
            pressKey(Key.Tab)
            pressKey(Key.Enter)
        }
        assertEquals(1, clicks)
    }
}
