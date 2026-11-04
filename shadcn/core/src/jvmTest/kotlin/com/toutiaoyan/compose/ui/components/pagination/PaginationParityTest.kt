@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.pagination

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.foundation.style.Style
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.input.key.Key
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import androidx.compose.ui.platform.*
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class PaginationParityTest : ScreenshotTest() {
    @Test fun novaPageLinksAre32SquareWithTwoPixelGap() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Pagination {
                PaginationContent {
                    PaginationLink(1, onClick = {}, modifier = Modifier.testTag("one"))
                    PaginationLink(2, onClick = {}, modifier = Modifier.testTag("two"))
                }
            }
        }
        composeRule.onNodeWithTag("one").assertWidthIsEqualTo(32.dp).assertHeightIsEqualTo(32.dp)
        val one = composeRule.onNodeWithTag("one").getUnclippedBoundsInRoot()
        val two = composeRule.onNodeWithTag("two").getUnclippedBoundsInRoot()
        assertEquals(2f, (two.left - one.right).value, 0.5f)
    }

    @Test fun novaRootUsesAvailableWidthAndCentersItsContents() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.width(320.dp)) {
                Pagination(Modifier.testTag("root")) {
                    PaginationContent(Modifier.testTag("content")) { PaginationLink(1, onClick = {}) }
                }
            }
        }
        composeRule.onNodeWithTag("root").assertWidthIsEqualTo(320.dp)
        val root = composeRule.onNodeWithTag("root").getUnclippedBoundsInRoot()
        val content = composeRule.onNodeWithTag("content").getUnclippedBoundsInRoot()
        assertEquals((root.left.value + root.right.value) / 2, (content.left.value + content.right.value) / 2, 0.5f)
    }

    @Test fun novaPreviousAndNextShowDefaultLabelsOnDesktop() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Pagination {
                PaginationContent {
                    PaginationPrevious(onClick = {})
                    PaginationNext(onClick = {})
                }
            }
        }
        composeRule.onNodeWithText("Previous").assertIsDisplayed()
        composeRule.onNodeWithText("Next").assertIsDisplayed()
    }

    @Test fun novaEllipsisIs32SquareAndHiddenFromAccessibility() {
        setThemedContent(stylePreset = StylePreset.Nova) { PaginationEllipsis(Modifier.testTag("gap")) }
        composeRule.onNodeWithTag(
            "gap",
            useUnmergedTree =
            true,
        ).assertWidthIsEqualTo(32.dp).assertHeightIsEqualTo(32.dp)
        composeRule.onNodeWithTag("gap", useUnmergedTree = true)
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.HideFromAccessibility))
        composeRule.onNodeWithContentDescription("More pages").assertDoesNotExist()
    }

    @Test fun disabledPageLinkDimsItsActualTextAndCannotActivate() {
        var clicks = 0
        setThemedContent(stylePreset = StylePreset.Nova) {
            PaginationLink(1, enabled = false, onClick = { clicks++ }, modifier = Modifier.testTag("disabled"))
        }
        composeRule.onNodeWithTag("disabled").assertIsNotEnabled().performTouchInput { click() }
        assertEquals(0, clicks)
        val pixels = composeRule.onNodeWithTag("disabled").captureToImage().toPixelMap()
        val minimum =
            (0 until pixels.width).flatMap { x ->
                (0 until pixels.height).map { y ->
                    pixels[
                        x,
                        y,
                    ].red
                }
            }.min()
        assertTrue(minimum > 0.35f && minimum < 0.8f, "disabled foreground=$minimum")
    }

    @Test fun previousChevronMirrorsInRtl() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                PaginationPrevious(onClick = {}, text = "", modifier = Modifier.testTag("previous"))
            }
        }
        val pixels = composeRule.onNodeWithTag("previous").captureToImage().toPixelMap()
        val ink =
            (0 until pixels.width).flatMap { x ->
                (0 until pixels.height).filter { y ->
                    pixels[
                        x,
                        y,
                    ].red < 0.4f
                }.map { y ->
                    x to y
                }
            }
        val top = ink.minOf { it.second }
        val middle = (top + ink.maxOf { it.second }) / 2
        val midX = ink.filter { kotlin.math.abs(it.second - middle) <= 1 }.map { it.first }.average()
        val topX = ink.filter { it.second <= top + 1 }.map { it.first }.average()
        assertTrue(midX > topX, "RTL previous arrow points right: middle=$midX top=$topX")
    }

    @Test fun directionalLabelsSwitchAt640AndKeepSourcePadding() {
        var width by mutableIntStateOf(639)
        setThemedContent(stylePreset = StylePreset.Nova) {
            val original = LocalWindowInfo.current
            val density = LocalDensity.current
            val current = width
            val window =
                remember(original, current, density) {
                    object : WindowInfo by original {
                        override val containerSize =
                            with(density) { IntSize(current.dp.roundToPx(), 800.dp.roundToPx()) }
                    }
                }
            CompositionLocalProvider(LocalWindowInfo provides window) {
                PaginationPrevious(onClick = {}, modifier = Modifier.testTag("previous"))
            }
        }
        composeRule.onNodeWithText("Previous").assertDoesNotExist()
        composeRule.onNodeWithTag("previous").assertWidthIsEqualTo(34.dp).assertHeightIsEqualTo(32.dp)
        composeRule.runOnIdle { width = 640 }
        composeRule.onNodeWithText("Previous").assertIsDisplayed()
        val control = composeRule.onNodeWithTag("previous").getUnclippedBoundsInRoot()
        val label = composeRule.onNodeWithText("Previous", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertEquals(40f, ((control.right - control.left) - (label.right - label.left)).value, 0.6f)
    }

    @Test fun localizedSlotUsesVisibleTextAndCallerSizeOverrides() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            PaginationLink(
                2,
                isActive =
                true,
                onClick =
                    {},
                modifier =
                    Modifier.testTag("page"),
                style =
                    Style {
                        width(40.dp)
                        height(38.dp)
                    },
            ) {
                Text { "٢" }
            }
        }
        composeRule.onNodeWithTag("page").assertIsSelected().assertWidthIsEqualTo(40.dp).assertHeightIsEqualTo(38.dp)
        composeRule.onNodeWithText("٢").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Go to page 2").assertDoesNotExist()
    }

    @Test fun minimumTouchTargetKeeps32PixelPageChrome() {
        var background = 1f
        setThemedContent(stylePreset = StylePreset.Nova) {
            Theme(preset = StylePreset.Nova, minTouchTarget = 48.dp) {
                background = currentTheme.colors.background.red
                PaginationLink(2, isActive = true, onClick = {}, modifier = Modifier.testTag("page"))
            }
        }
        composeRule.onNodeWithTag("page").assertWidthIsEqualTo(48.dp).assertHeightIsEqualTo(48.dp)
        val pixels = composeRule.onNodeWithTag("page").captureToImage().toPixelMap()
        assertEquals(background, pixels[5, 24].red, 0.01f)
        assertTrue(pixels[8, 24].red < background - 0.02f)
    }

    @Test fun controlledExtensionTracksPageAndAllBoundaryControls() {
        var page by mutableIntStateOf(1)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Pagination(page = page, onPageChange = { page = it }, pageCount = 3, showEdges = true)
        }
        composeRule.onNodeWithContentDescription("Go to first page").assertIsNotEnabled()
        composeRule.onNodeWithContentDescription("Go to previous page").assertIsNotEnabled()
        composeRule.onNodeWithContentDescription("Go to next page").performClick()
        assertEquals(2, page)
        composeRule.onNodeWithText("2").assertIsSelected()
        composeRule.onNodeWithContentDescription("Go to last page").performClick()
        assertEquals(3, page)
        composeRule.onNodeWithContentDescription("Go to next page").assertIsNotEnabled()
        composeRule.onNodeWithContentDescription("Go to last page").assertIsNotEnabled()
    }

    @Test fun keyboardActivationRequestsPageAndControlledRejectionKeepsSelection() {
        val requests = mutableListOf<Int>()
        setThemedContent(stylePreset = StylePreset.Nova) {
            Pagination(page = 1, onPageChange = { requests.add(it) }, pageCount = 3) {
                PaginationContent {
                    PaginationLink(1)
                    PaginationLink(2, modifier = Modifier.testTag("two"))
                }
            }
        }
        composeRule.onNodeWithTag("two").performSemanticsAction(SemanticsActions.RequestFocus) { it() }
        composeRule.onNodeWithTag("two").performKeyInput { pressKey(Key.Enter) }
        assertEquals(listOf(2), requests)
        composeRule.onNodeWithText("1").assertIsSelected()
        composeRule.onNodeWithTag("two").assertIsNotSelected()
    }
}
