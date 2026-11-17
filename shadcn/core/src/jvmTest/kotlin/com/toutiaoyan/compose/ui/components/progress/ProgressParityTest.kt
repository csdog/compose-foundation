@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.progress

import androidx.compose.runtime.*
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.Radius
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class ProgressParityTest : ScreenshotTest() {
    @Test fun labelAndValueShareOneRowWithAnAutomaticTrackTwelvePixelsBelow() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Progress(56f, max = 100f, modifier = Modifier.width(320.dp).testTag("root")) {
                ProgressLabel("Upload progress")
                ProgressValue()
            }
        }
        composeRule.onNodeWithTag("root").assertWidthIsEqualTo(320.dp).assertHeightIsEqualTo(36.dp)
        val root = composeRule.onNodeWithTag("root").getUnclippedBoundsInRoot()
        val label = composeRule.onNodeWithText("Upload progress", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val value = composeRule.onNodeWithText("56%", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertEquals(root.left, label.left)
        assertEquals(root.right, value.right)
        assertEquals(label.top, value.top)
        assertEquals(20f, label.height.value, 0.5f)
        val pixels = composeRule.onNodeWithTag("root").captureToImage().toPixelMap()
        // The filled end is square; only the outer track's two ends are rounded.
        assertEquals(pixels[170, 34], pixels[178, 32])
        assertNotEquals(pixels[178, 32], pixels[180, 32])
    }

    @Test fun rtlMirrorsTheAutoMarginAndFillsFromTheRight() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Progress(0.56f, Modifier.width(320.dp).testTag("root")) {
                    ProgressLabel("تقدم الرفع")
                    ProgressValue("٥٦%")
                }
            }
        }
        val root = composeRule.onNodeWithTag("root").getUnclippedBoundsInRoot()
        val label = composeRule.onNodeWithText("تقدم الرفع", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val value = composeRule.onNodeWithText("٥٦%", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertEquals(root.right, label.right)
        assertEquals(root.left, value.left)
        val pixels = composeRule.onNodeWithTag("root").captureToImage().toPixelMap()
        assertNotEquals(pixels[20, 34], pixels[300, 34])
        assertEquals(pixels[300, 34], pixels[180, 34])
    }

    @Test fun rootStyleControlsBothTheLabelRowAndTrack() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Progress(0.56f, Modifier.testTag("root"), style = Style { width(200.dp) }) {
                ProgressLabel("Upload")
                ProgressValue()
            }
        }
        composeRule.onNodeWithTag("root").assertWidthIsEqualTo(200.dp).assertHeightIsEqualTo(36.dp)
    }

    @Test fun accessibilityAndDisplayedValueUpdateWithoutWaitingForTheFillAnimation() {
        var progress by mutableFloatStateOf(0f)
        composeRule.mainClock.autoAdvance = false
        setThemedContent(stylePreset = StylePreset.Nova) {
            Progress(progress, Modifier.width(200.dp).testTag("root")) { ProgressValue() }
        }
        composeRule.runOnIdle { progress = 1f }
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.onNodeWithTag("root").assert(
            SemanticsMatcher.expectValue(
                SemanticsProperties.ProgressBarRangeInfo,
                ProgressBarRangeInfo(1f, 0f..1f, 0),
            ),
        )
        composeRule.onNodeWithText("100%", useUnmergedTree = true).assertExists()
        composeRule.mainClock.advanceTimeBy(200)
        val pixels = composeRule.onNodeWithTag("root").captureToImage().toPixelMap()
        assertEquals(pixels[10, pixels.height - 2], pixels[190, pixels.height - 2])
    }

    @Test fun indeterminateHasNoFabricatedPercentage() {
        composeRule.mainClock.autoAdvance = false
        setThemedContent(stylePreset = StylePreset.Nova) {
            Progress(0.42f, Modifier.width(200.dp).testTag("root"), indeterminate = true) { ProgressValue() }
        }
        val root = composeRule.onNodeWithTag("root")
        root.assert(
            SemanticsMatcher.expectValue(
                SemanticsProperties.ProgressBarRangeInfo,
                ProgressBarRangeInfo.Indeterminate,
            ),
        )
        root.assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.StateDescription))
        composeRule.onNodeWithText("42%", useUnmergedTree = true).assertDoesNotExist()

        fun firstFilledPixel(): Int {
            val pixels = root.captureToImage().toPixelMap()
            return (2 until pixels.width - 2).first { pixels[it, pixels.height - 2].red < 0.3f }
        }

        composeRule.mainClock.advanceTimeBy(380)
        val first = firstFilledPixel()
        composeRule.mainClock.advanceTimeBy(250)
        assertTrue(firstFilledPixel() > first + 30)
    }

    @Test fun theTrackRepaintsAndKeepsItsPillShapeAcrossThemeChanges() {
        var dark by mutableStateOf(false)
        var radius by mutableStateOf(Radius(10.dp))
        var primary = Color.Unspecified
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, baseRadius = radius) {
                primary = currentTheme.colors.primary
                Progress(1f, Modifier.width(200.dp).testTag("root"))
            }
        }
        val root = composeRule.onNodeWithTag("root")
        val light = root.captureToImage().toPixelMap()
        val lightFill = light[50, 2]
        assertEquals(primary.toArgb(), lightFill.toArgb())
        composeRule.runOnIdle {
            dark = true
            radius = Radius(0.dp)
        }
        val darkPixels = root.captureToImage().toPixelMap()
        val darkFill = darkPixels[50, 2]
        assertEquals(primary.toArgb(), darkFill.toArgb())
        assertNotEquals(lightFill, darkFill)
        assertNotEquals(darkFill, darkPixels[0, 0])
        root.assertHeightIsEqualTo(4.dp)
    }

    @Test fun invalidAndOutOfRangeNumbersCannotProduceInvalidRangeSemantics() {
        var progress by mutableFloatStateOf(Float.NaN)
        var maximum by mutableFloatStateOf(1f)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Progress(progress, Modifier.width(200.dp).testTag("root"), max = maximum)
        }

        fun check(value: Float) {
            composeRule.onNodeWithTag("root").assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.ProgressBarRangeInfo,
                    ProgressBarRangeInfo(value, 0f..1f, 0),
                ),
            )
        }
        check(0f)
        composeRule.runOnIdle { progress = Float.POSITIVE_INFINITY }
        check(1f)
        composeRule.runOnIdle { progress = -1f }
        check(0f)
        composeRule.runOnIdle {
            maximum = 0f
            progress = 50f
        }
        check(0f)
    }

    @Test fun customValueDescriptionDoesNotReplaceTheAccessibleLabel() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Progress(0.5f, Modifier.testTag("root"), getValueLabel = { "Half complete" }) {
                ProgressLabel("Upload")
            }
        }
        composeRule.onNodeWithTag("root").assert(
            SemanticsMatcher.expectValue(
                SemanticsProperties.StateDescription,
                "Half complete",
            ),
        )
        composeRule.onNodeWithTag("root").assertContentDescriptionEquals("Upload")
        composeRule.onNodeWithTag("root").assertTextContains("Upload")
    }

    @Test fun accessibleLabelFollowsTheRenderedLabelAndRespectsAnExplicitName() {
        var label by mutableStateOf<String?>("Upload")
        setThemedContent(stylePreset = StylePreset.Nova) {
            Progress(56f, max = 100f, modifier = Modifier.testTag("root")) {
                label?.let { ProgressLabel(it) }
            }
            Progress(
                0.5f,
                Modifier.testTag("explicit").semantics { contentDescription = "Explicit name" },
            ) {
                ProgressLabel("Visible label")
            }
        }
        val root = composeRule.onNodeWithTag("root")
        root.assertContentDescriptionEquals("Upload")
        composeRule.runOnIdle { label = "تقدم الرفع" }
        root.assertContentDescriptionEquals("تقدم الرفع")
        composeRule.runOnIdle { label = null }
        root.assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.ContentDescription))
        composeRule.onNodeWithTag("explicit").assertContentDescriptionEquals("Explicit name")
    }

    @Test fun narrowHeadersWrapAndKeepTheValueAtTheLogicalEnd() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Progress(0.56f, Modifier.width(100.dp).testTag("root")) {
                ProgressLabel("Upload progress")
                ProgressValue()
            }
        }
        val label = composeRule.onNodeWithText("Upload progress", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val value = composeRule.onNodeWithText("56%", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val root = composeRule.onNodeWithTag("root").getUnclippedBoundsInRoot()
        assertTrue(value.top >= label.bottom + 12.dp)
        assertEquals(root.right, value.right)
    }

    @Test fun novaValueRequestsTabularNumeralsFromTheTextShaper() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Progress(0.11f, Modifier.width(320.dp)) { ProgressValue() }
        }
        val results = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithText("11%", useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
        assertEquals("tnum", results.single().layoutInput.style.fontFeatureSettings)
    }

    @Test fun visibleValueIsHiddenFromAccessibilityLikeBaseUiValue() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Progress(0.56f, Modifier.width(320.dp).testTag("root")) {
                ProgressLabel("Upload progress")
                ProgressValue()
            }
        }
        composeRule.onNodeWithText("56%", useUnmergedTree = true)
            .assert(hasAnyAncestor(SemanticsMatcher.keyIsDefined(SemanticsProperties.HideFromAccessibility)))
        composeRule.onNodeWithTag("root").assertTextEquals("Upload progress")
    }
}
