@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.empty

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toPixelMap
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
import com.toutiaoyan.compose.ui.theme.Theme
import kotlin.test.*

class EmptyParityTest : ScreenshotTest() {
    @Test fun dashedOutlineFollowsDynamicCallerShapeAndRestores() =
        assertDynamicBorderShape(StylePreset.Nova, EmptyVariant.Outline)

    @Test
    fun legacyDefaultDashedBorderFollowsCallerShape() = assertDynamicBorderShape(StylePreset.Vega, EmptyVariant.Default)

    @Test
    fun legacyOutlineSolidBorderFollowsCallerShape() = assertDynamicBorderShape(StylePreset.Vega, EmptyVariant.Outline)

    private fun assertDynamicBorderShape(
        preset: StylePreset,
        variant: EmptyVariant,
    ) {
        var shapeIndex by mutableStateOf(0)
        val diamond =
            GenericShape { size, _ ->
                moveTo(size.width / 2, 0f)
                lineTo(size.width, size.height / 2)
                lineTo(size.width / 2, size.height)
                lineTo(0f, size.height / 2)
                close()
            }
        setThemedContent(stylePreset = preset) {
            Theme(preset = preset, isDark = shapeIndex == 1) {
                Empty(
                    Modifier.size(160.dp).testTag("root"),
                    variant = variant,
                    padding = if (preset == StylePreset.Nova) null else 0.dp,
                    style =
                        Style {
                            shape(RectangleShape)
                            if (Theme.LocalTheme.currentValue.isDark) shape(CircleShape)
                            if (shapeIndex == 2) shape(diamond)
                            background(Color.Red)
                        },
                ) {}
            }
        }

        fun capture(): PixelMap {
            composeRule.waitForIdle()
            return composeRule.onNodeWithTag("root").captureToImage().toPixelMap()
        }

        fun borderInk(
            pixels: PixelMap,
            xs: IntRange,
            ys: IntRange,
        ): Int =
            ys.sumOf { y ->
                xs.count { x ->
                    val color = pixels[x, y]
                    color.red < 0.99f || color.green in 0.01f..0.99f
                }
            }
        val rectangle = capture()
        assertTrue(rectangle[3, 3].red > 0.99f && rectangle[3, 3].green < 0.01f)
        assertTrue(borderInk(rectangle, 16..40, 0..0) > 0, "Rectangle has visible top-edge border ink")
        for (index in 1..2) {
            composeRule.runOnIdle { shapeIndex = index }
            val shaped = capture()
            assertTrue(shaped[3, 3].green > 0.9f, "Resolved shape clears the fill corner")
            assertEquals(
                0,
                borderInk(shaped, 16..40, 0..0),
                "Border follows the resolved shape away from the old top edge",
            )
            assertTrue(borderInk(shaped, 0..159, 0..159) > 20, "Resolved shape retains a visible border")
        }
        composeRule.runOnIdle { shapeIndex = 0 }
        val restored = capture()
        for (y in 0 until rectangle.height) {
            for (x in 0 until rectangle.width) {
                assertEquals(rectangle[x, y], restored[x, y], "Rectangle restores at ($x, $y)")
            }
        }
    }

    @Test fun defaultAndExplicitRadiusBordersRestore() {
        var radius by mutableStateOf<androidx.compose.ui.unit.Dp?>(null)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Empty(
                Modifier.size(160.dp).testTag("root"),
                variant = EmptyVariant.Outline,
                radius = radius,
                style = Style { background(Color.Red) },
            ) {}
        }

        fun capture(): PixelMap {
            composeRule.waitForIdle()
            return composeRule.onNodeWithTag("root").captureToImage().toPixelMap()
        }
        val default = capture()
        composeRule.runOnIdle { radius = 0.dp }
        val square = capture()
        assertTrue(square[3, 3].red > 0.99f && square[3, 3].green < 0.01f)
        composeRule.runOnIdle { radius = 40.dp }
        val rounded = capture()
        assertTrue(rounded[3, 3].green > 0.9f)
        assertTrue((16..25).all { rounded[it, 0].red > 0.99f }, "Rounded border clears the square top-edge region")
        composeRule.runOnIdle { radius = null }
        val restored = capture()
        for (y in 0 until default.height) {
            for (x in 0 until default.width) {
                assertEquals(default[x, y], restored[x, y], "Default radius restores at ($x, $y)")
            }
        }
    }

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
