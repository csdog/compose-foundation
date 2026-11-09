@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.empty

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.Style
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class EmptyParityTest : ScreenshotTest() {
    @Test fun originalPositionalSignaturesRemainCallable() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Column {
                EmptyTitle("String title", Modifier, EmptyTitleDefaults.Official, Style)
                EmptyTitle(Modifier, EmptyTitleDefaults.Official, Style, { "Lambda title" })
                EmptyDescription("String description", Modifier, Style)
                EmptyDescription(AnnotatedString("Rich description"), Modifier, Style)
                EmptyDescription(Modifier, Style, { "Lambda description" })
            }
        }
        listOf("String title", "Lambda title", "String description", "Rich description", "Lambda description")
            .forEach { composeRule.onNodeWithText(it).assertExists() }
    }

    @Test fun novaHeaderAndContentHaveTheOfficialMaximumWidth() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Empty(Modifier.width(600.dp)) {
                EmptyHeader(Modifier.testTag("header")) {
                    EmptyDescription(
                        (
                            "A description that is long enough to fill the whole available " +
                                "header width and wrap onto another line."
                        ),
                    )
                }
                EmptyContent(Modifier.testTag("content")) { Text { "Action" } }
            }
        }
        composeRule.onNodeWithTag("header").assertWidthIsEqualTo(384.dp)
        composeRule.onNodeWithTag("content").assertWidthIsEqualTo(384.dp)
    }

    @Test fun callerStyleCanOverrideTheDefaultContentWidth() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Empty(Modifier.width(600.dp)) {
                EmptyContent(Modifier.testTag("content"), style = Style { maxWidth(480.dp) }) { Text { "Action" } }
            }
        }
        composeRule.onNodeWithTag("content").assertWidthIsEqualTo(480.dp)
    }

    @Test fun novaKeepsItsFullLineBoxesAndUsesEmBasedTracking() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Empty {
                EmptyHeader {
                    EmptyTitle("No Projects Yet")
                    EmptyDescription("Description")
                }
            }
        }
        val title = mutableListOf<TextLayoutResult>()
        val description = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithText(
            "No Projects Yet",
        ).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(title) }
        composeRule.onNodeWithText("Description").performSemanticsAction(SemanticsActions.GetTextLayoutResult) {
            it(description)
        }
        assertEquals(-0.35f, title.single().layoutInput.style.letterSpacing.value)
        assertEquals(14f, title.single().layoutInput.style.fontSize.value)
        assertEquals(20f, title.single().layoutInput.style.lineHeight.value)
        assertEquals(LineBreak.Heading, title.single().layoutInput.style.lineBreak)
        assertEquals(14f, description.single().layoutInput.style.fontSize.value)
        assertEquals(22.75f, description.single().layoutInput.style.lineHeight.value)
        assertEquals(LineBreak.Heading, description.single().layoutInput.style.lineBreak)
    }

    @Test fun novaCentersContentInsideAnExplicitHeight() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Empty(Modifier.width(500.dp).height(400.dp).testTag("root")) {
                EmptyHeader { EmptyTitle("Centered") }
            }
        }
        val root = composeRule.onNodeWithTag("root").fetchSemanticsNode().boundsInRoot
        val title = composeRule.onNodeWithText("Centered").fetchSemanticsNode().boundsInRoot
        assertEquals(root.center.y, title.center.y, 1f)
    }

    @Test fun novaTitleDoesNotIntroduceAnUnrequestedHeading() {
        setThemedContent(stylePreset = StylePreset.Nova) { EmptyTitle("No Projects Yet") }
        composeRule.onNodeWithText(
            "No Projects Yet",
        ).assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Heading))
    }

    private fun nova(dark: Boolean) {
        snapshot("empty_nova", darkTheme = dark, stylePreset = StylePreset.Nova) {
            Column(Modifier.width(500.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Empty {
                    EmptyHeader {
                        EmptyMedia(variant = EmptyMediaVariant.Icon) { Text { "+" } }
                        EmptyTitle("No Projects Yet")
                        EmptyDescription(
                            (
                                "You haven't created any projects yet. Get started by creating " +
                                    "your first project."
                            ),
                        )
                    }
                }
                Empty(variant = EmptyVariant.Outline) {
                    EmptyHeader {
                        EmptyTitle("Cloud Storage Empty")
                        EmptyDescription("Upload files to your cloud storage to access them anywhere.")
                    }
                }
            }
        }
    }

    @Test fun nova_light() = nova(false)

    @Test fun nova_dark() = nova(true)
}
