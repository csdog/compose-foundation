package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.TwColors
import com.toutiaoyan.compose.ui.catalog.create.*
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.Accent
import com.toutiaoyan.compose.ui.tokens.BaseColor
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FinancePreviewTest {
    @get:Rule val rule = createComposeRule()

    @Test fun financeGridUsesOfficialViewportBreakpointsAndSevenColumns() {
        for ((width, grid) in listOf(
            390 to 16 to 2400,
            767 to 16 to 2400,
            768 to 40 to 3000,
            1599 to 40 to 3000,
            1600 to 48 to 3000,
        )) {
            // Pair nesting keeps these independent source values simple.
            val result = financePreviewMetrics(width.first.dp, StylePreset.Nova)
            assertEquals(width.second.dp, result.gap)
            assertEquals(grid.toFloat(), (result.columnWidth * 7 + result.gap * 8).value, .01f)
        }
        for (preset in listOf(StylePreset.Lyra, StylePreset.Mira)) {
            val result = financePreviewMetrics(1200.dp, preset)
            assertEquals(24.dp, result.gap)
            assertEquals(2600f, (result.columnWidth * 7 + result.gap * 8).value, .01f)
        }
    }

    @Test fun contributionSummaryRespondsToPreviewWidthAndFooterBackgroundFills() {
        var viewport by mutableStateOf(390.dp)
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                CompositionLocalProvider(LocalCreatePreviewWidth provides viewport) {
                    val cardWidth = financePreviewMetrics(viewport, StylePreset.Nova).columnWidth - 8.dp
                    CreateContributionHistoryCard(Modifier.width(cardWidth).testTag("card"))
                }
            }
        }
        for (width in listOf(390.dp, 1176.dp, 390.dp)) {
            rule.runOnIdle { viewport = width }
            val first = rule.onNodeWithText("UPCOMING").fetchSemanticsNode().boundsInRoot
            val second = rule.onNodeWithText("AUTO-SAVE PLAN").fetchSemanticsNode().boundsInRoot
            if (width < 768.dp) {
                assertEquals(first.left, second.left, .1f)
                assertTrue(second.top > first.bottom + 50f)
            } else {
                assertEquals(first.top, second.top, .1f)
                assertTrue(second.left > first.right)
            }
            rule.onNodeWithText("May 25, 2024").assertIsDisplayed()
            rule.onNodeWithText("$1,000 scheduled").assertIsDisplayed()
            val cardWidth = financePreviewMetrics(width, StylePreset.Nova).columnWidth - 8.dp
            rule.onNodeWithText("View Full Report").assertWidthIsEqualTo(cardWidth - 32.dp)
            val button = rule.onNodeWithText("View Full Report").captureToImage().toPixelMap()
            assertColor(TwColors.neutral900, button[4, button.height / 2])
            val image = rule.onNodeWithTag("card").captureToImage().toPixelMap()
            val card = rule.onNodeWithTag("card").fetchSemanticsNode().boundsInRoot
            assertColor(
                TwColors.neutral100.copy(alpha = .5f).compositeOver(Color.White),
                image[(first.left - card.left).toInt() + 1, (first.top - card.top).toInt() - 3],
            )
        }
    }

    @Test fun payoutCurrencySliderAndNotesSurviveThemeChangesWithoutAmountWrapping() {
        var dark by mutableStateOf(false)
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                OverlayHost { CreatePayoutThresholdCard(Modifier.width(316.57.dp).testTag("payout")) }
            }
        }
        rule.onAllNodesWithText("USD — United States Dollar").onFirst().performClick()
        rule.onNodeWithText("EUR — Euro").performClick()
        rule.onNode(hasSetTextAction()).performTextInput("Keep this payout note")
        val thumb =
            rule.onNode(
                SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo) and hasRequestFocusAction(),
                useUnmergedTree = true,
            )
        thumb.performSemanticsAction(SemanticsActions.SetProgress) { it(5550f) }
        rule.onNodeWithText("$5550.00").assertIsDisplayed()
        thumb.requestFocus().performKeyInput { pressKey(Key.DirectionRight) }
        rule.onNodeWithText("$5600.00").assertIsDisplayed()
        thumb.performKeyInput { pressKey(Key.MoveHome) }
        rule.onNodeWithText("$50.00").assertIsDisplayed()
        thumb.performKeyInput { pressKey(Key.MoveEnd) }
        val layouts = mutableListOf<TextLayoutResult>()
        rule.onNodeWithText("$10000.00").performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(1, layouts.single().lineCount)
        val amount = rule.onNodeWithText("$10000.00").fetchSemanticsNode().boundsInRoot
        val label = rule.onNodeWithText("Minimum Payout Amount").fetchSemanticsNode().boundsInRoot
        assertTrue(label.right <= amount.left, "The label and amount must not overlap")
        rule.runOnIdle { dark = true }
        rule.onNodeWithText("EUR — Euro").assertIsDisplayed()
        rule.onNode(hasSetTextAction()).assertTextContains("Keep this payout note")
        rule.onNodeWithText("$10000.00").assertIsDisplayed()
        assertTrue(
            rule.onNodeWithContentDescription("Notes").fetchSemanticsNode().layoutInfo.height >= 100,
            "The measured textarea chrome includes padding excluded from inner editable semantics bounds",
        )
        rule.onNodeWithText("Save Threshold").assertWidthIsEqualTo(284.57.dp)
    }

    @Test fun financeGridScrollsColumnsTogether() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost { CreatePreviewCanvas("Neutral", Modifier.size(1000.dp, 700.dp)) }
            }
        }

        fun top(label: String) = rule.onNodeWithText(label).fetchSemanticsNode().boundsInRoot.top
        val first = top("Contribution History")
        val second = top("Payout Threshold")
        val grid =
            rule.onNode(
                SemanticsMatcher.keyIsDefined(SemanticsProperties.VerticalScrollAxisRange) and
                    hasAnyDescendant(hasText("Contribution History")),
            )
        grid.performSemanticsAction(SemanticsActions.ScrollBy) { it(0f, 20f) }
        rule.waitForIdle()
        assertEquals(20f, first - top("Contribution History"), 1f)
        assertEquals(20f, second - top("Payout Threshold"), 1f)
        grid.performTouchInput { swipe(Offset(150f, 450f), Offset(150f, 390f), durationMillis = 500) }
        rule.waitForIdle()
        val firstDelta = first - top("Contribution History")
        val secondDelta = second - top("Payout Threshold")
        assertTrue(
            firstDelta > 20f,
            "Shared held swipe should move both columns: first=$firstDelta second=$secondDelta",
        )
        assertEquals(firstDelta, secondDelta, .1f)
    }

    @Test fun financeCanvasUsesMutedLightAndBackgroundDark() {
        var dark by mutableStateOf(false)
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                OverlayHost { CreatePreviewCanvas("Neutral", Modifier.size(1000.dp, 700.dp).testTag("finance frame")) }
            }
        }
        for (mode in listOf(false, true, false)) {
            rule.runOnIdle { dark = mode }
            val pixels = rule.onNodeWithTag("finance frame").captureToImage().toPixelMap()
            assertColor(if (mode) TwColors.neutral950 else TwColors.neutral100, pixels[10, 10])
        }
    }

    @Test fun payoutInputsSurviveCreatePageDesktopMobileDesktopMove() {
        var width by mutableStateOf(1000.dp)
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.size(width, 760.dp)) {
                        CreatePage(StylePreset.Nova, {}, BaseColor.Neutral, {}, Accent.Base, {})
                    }
                }
            }
        }
        rule.onNodeWithText("Get Started").performClick()
        rule.onAllNodesWithText("USD — United States Dollar").onFirst().performClick()
        rule.onNodeWithText("EUR — Euro").performClick()
        rule.onNodeWithContentDescription("Minimum Payout Amount, $2500.00")
            .performSemanticsAction(SemanticsActions.SetProgress) { it(10000f) }
        rule.onNode(hasSetTextAction() and hasContentDescription("Notes")).performTextInput("Retained payout")
        for (size in listOf(390.dp, 1000.dp)) {
            rule.runOnIdle { width = size }
            rule.onNodeWithText("EUR — Euro").assertExists()
            rule.onNodeWithText("$10000.00").assertExists()
            rule.onNode(hasSetTextAction() and hasContentDescription("Notes")).assertTextContains("Retained payout")
        }
    }

    private fun assertColor(
        expected: Color,
        actual: Color,
    ) {
        assertTrue(
            abs(expected.red - actual.red) < .015f &&
                abs(expected.green - actual.green) < .015f &&
                abs(expected.blue - actual.blue) < .015f,
            "Expected $expected, actual $actual",
        )
    }
}
