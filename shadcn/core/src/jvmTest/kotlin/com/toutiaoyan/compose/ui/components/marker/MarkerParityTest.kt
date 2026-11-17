@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.marker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.test.*
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.ResolvedTextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class MarkerParityTest : ScreenshotTest() {
    private fun layout(text: String): TextLayoutResult {
        val results = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithText(text).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
        return results.single()
    }

    @Test fun novaUsesFullWidthFourteenTwentyTextAndEightPixelGap() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.width(280.dp)) {
                Marker(Modifier.testTag("marker")) {
                    MarkerIcon(Modifier.testTag("icon")) { Box(Modifier.size(16.dp)) }
                    MarkerContent("Explored 4 files")
                }
            }
        }
        composeRule.onNodeWithTag("marker").assertWidthIsEqualTo(280.dp).assertHeightIsEqualTo(20.dp)
        val icon =
            composeRule.onNodeWithTag("icon").assertWidthIsEqualTo(16.dp).assertHeightIsEqualTo(16.dp)
                .fetchSemanticsNode().boundsInRoot
        val text = composeRule.onNodeWithText("Explored 4 files").fetchSemanticsNode().boundsInRoot
        assertEquals(8f, text.left - icon.right, 0.1f)
        assertEquals(14f, layout("Explored 4 files").layoutInput.style.fontSize.value)
        assertEquals(20f, layout("Explored 4 files").layoutInput.style.lineHeight.value)
    }

    @Test fun decorativeIconDoesNotExposeItsChildDescription() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Marker {
                MarkerIcon { Text(modifier = Modifier.semantics { contentDescription = "decorative" }) { "!" } }
                MarkerContent("Synced")
            }
        }
        composeRule.onNodeWithContentDescription("decorative").assertDoesNotExist()
        composeRule.onNodeWithText("Synced").assertIsDisplayed()
    }

    @Test fun arabicMarkerUsesLogicalStartAndKeepsSeparatorCentered() {
        val label = "تمت المزامنة"
        val divider = "اليوم"
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Column(Modifier.width(280.dp)) {
                    Marker {
                        MarkerIcon(Modifier.testTag("rtl-icon")) { Box(Modifier.size(16.dp)) }
                        MarkerContent(label)
                    }
                    Marker(divider, Modifier.testTag("rtl-separator"), MarkerVariant.Separator)
                }
            }
        }
        val iconBounds = composeRule.onNodeWithTag("rtl-icon").fetchSemanticsNode().boundsInRoot
        val labelBounds = composeRule.onNodeWithText(label).assertIsDisplayed().fetchSemanticsNode().boundsInRoot
        assertEquals(8f, iconBounds.left - labelBounds.right, 0.1f)
        assertEquals(ResolvedTextDirection.Rtl, layout(label).getParagraphDirection(0))
        val separator = composeRule.onNodeWithTag("rtl-separator").fetchSemanticsNode().boundsInRoot
        val text = composeRule.onNodeWithText(divider).assertIsDisplayed().fetchSemanticsNode().boundsInRoot
        assertEquals(separator.center.x, text.center.x, 0.5f)
    }

    @Test fun emptyMarkerHasSixteenPixelMinimumAndCallerStyleOverridesWidth() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.width(280.dp)) {
                Marker(modifier = Modifier.testTag("marker"), style = Style { width(80.dp) }) {}
            }
        }
        composeRule.onNodeWithTag("marker").assertWidthIsEqualTo(80.dp).assertHeightIsEqualTo(16.dp)
    }

    @Test fun liveThemeSwitchUpdatesMutedTextColor() {
        var dark by mutableStateOf(false)
        var expected = Color.Unspecified
        composeRule.setContent {
            Theme(isDark = dark, preset = StylePreset.Nova) {
                expected = currentTheme.colors.mutedForeground
                Marker("Today")
            }
        }
        assertEquals(expected, layout("Today").layoutInput.style.color)
        composeRule.runOnIdle { dark = true }
        composeRule.waitForIdle()
        assertEquals(expected, layout("Today").layoutInput.style.color)
    }

    @Test fun bottomBorderIsFullyInsideTheLastPixelAndPaddingIsEight() {
        var border = Color.Unspecified
        setThemedContent(stylePreset = StylePreset.Nova) {
            border = currentTheme.colors.border
            Marker("Today", Modifier.width(240.dp).testTag("border"), variant = MarkerVariant.Border)
        }
        composeRule.onNodeWithTag("border").assertHeightIsEqualTo(28.dp)
        val image = composeRule.onNodeWithTag("border").captureToImage().toPixelMap()
        assertEquals(border, image[0, image.height - 1])
    }

    @Test fun separatorCentersItsLabelWithTwelvePixelClearanceBeforeTheLine() {
        var border = Color.Unspecified
        setThemedContent(stylePreset = StylePreset.Nova) {
            border = currentTheme.colors.border
            Marker("Today", Modifier.width(240.dp).testTag("separator"), variant = MarkerVariant.Separator)
        }
        val root = composeRule.onNodeWithTag("separator").fetchSemanticsNode().boundsInRoot
        val label = composeRule.onNodeWithText("Today").fetchSemanticsNode().boundsInRoot
        assertEquals(root.center.x, label.center.x, 0.5f)
        val image = composeRule.onNodeWithTag("separator").captureToImage().toPixelMap()
        val end = (label.left - root.left - 12f).toInt()
        val lineRows = (0 until image.height).filter { image[0, it] == border }
        assertEquals(1, lineRows.size, "Hairline must occupy exactly one pixel row")
        val lineY = lineRows.single()
        assertEquals(image.height / 2f, lineY + 0.5f, 0.5f)
        assertEquals(border, image[end - 1, lineY])
        assertNotEquals(border, image[end + 2, lineY])
    }

    private fun nova(dark: Boolean) =
        snapshot("marker_nova", darkTheme = dark, stylePreset = StylePreset.Nova) {
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Marker("Switched to a new branch")
                Marker("Conversation compacted", variant = MarkerVariant.Separator)
                Marker("Opened implementation notes", variant = MarkerVariant.Border)
            }
        }

    @Test fun nova_light() = nova(false)

    @Test fun nova_dark() = nova(true)
}
