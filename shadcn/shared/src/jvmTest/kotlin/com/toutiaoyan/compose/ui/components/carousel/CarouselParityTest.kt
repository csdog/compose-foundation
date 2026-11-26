@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.carousel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class CarouselParityTest : ScreenshotTest() {
    private lateinit var state: PagerState

    @Composable
    private fun fixture(
        count: Int = 5,
        partial: Boolean = false,
        rtl: Boolean = false,
    ) {
        val pager = rememberPagerState { count }
        SideEffect { state = pager }
        CompositionLocalProvider(LocalLayoutDirection provides if (rtl) LayoutDirection.Rtl else LayoutDirection.Ltr) {
            CarouselRoot(pager, Modifier.width(260.dp).height(120.dp), ariaLabel = "Gallery") {
                CarouselContent(
                    pageSize =
                        if (partial) {
                            PageSize.Fixed(80.dp)
                        } else {
                            PageSize.Fill
                        },
                    pageSpacing =
                        if (partial) {
                            10.dp
                        } else {
                            0.dp
                        },
                ) { page ->
                    Text { "Slide ${page + 1}" }
                }
                Row(Modifier.align(Alignment.BottomCenter)) {
                    CarouselPrevious()
                    CarouselNext()
                }
            }
        }
    }

    @Test fun partialSlidesDisableNextAtTheActualEndAndCanReturn() {
        setThemedContent(stylePreset = StylePreset.Nova) { fixture(partial = true) }
        composeRule.onNodeWithContentDescription("Previous slide").assertIsNotEnabled()
        repeat(2) {
            composeRule.onNodeWithContentDescription("Next slide").performClick()
            composeRule.waitForIdle()
        }
        composeRule.onNodeWithContentDescription("Next slide").assertIsNotEnabled()
        composeRule.runOnIdle { assertEquals(2, state.currentPage) }
        repeat(2) {
            composeRule.onNodeWithContentDescription("Previous slide").performClick()
            composeRule.waitForIdle()
        }
        composeRule.onNodeWithContentDescription("Previous slide").assertIsNotEnabled()
    }

    @Test fun rootKeysWorkWhenANavigationControlHasFocus() {
        setThemedContent(stylePreset = StylePreset.Nova) { fixture() }
        composeRule.onNodeWithContentDescription(
            "Next slide",
        ).requestFocus().performKeyInput { pressKey(Key.DirectionRight) }
        composeRule.waitForIdle()
        composeRule.runOnIdle { assertEquals(1, state.currentPage) }
        composeRule.onNodeWithContentDescription("Next slide").performKeyInput { pressKey(Key.DirectionLeft) }
        composeRule.waitForIdle()
        composeRule.runOnIdle { assertEquals(0, state.currentPage) }
        composeRule.onAllNodes(
            SemanticsMatcher.expectValue(
                SemanticsProperties.Role,
                Role.Carousel,
            ),
        ).assertCountEquals(1)
    }

    @Test fun emptyCarouselDisablesBothNavigationButtons() {
        setThemedContent(stylePreset = StylePreset.Nova) { fixture(count = 0) }
        composeRule.onNodeWithContentDescription("Next slide").assertIsNotEnabled()
        composeRule.onNodeWithContentDescription("Previous slide").assertIsNotEnabled()
        composeRule.runOnIdle { assertEquals(0, state.pageCount) }
    }

    @Test fun rtlRetainsLogicalNextAndPrevious() {
        setThemedContent(stylePreset = StylePreset.Nova) { fixture(rtl = true) }
        composeRule.onNodeWithContentDescription("Next slide").performClick()
        composeRule.waitForIdle()
        composeRule.runOnIdle { assertEquals(1, state.currentPage) }
        composeRule.onNodeWithContentDescription("Previous slide").performClick()
        composeRule.waitForIdle()
        composeRule.runOnIdle { assertEquals(0, state.currentPage) }
    }

    @Test fun shrinkingDataClampsThePageAndUpdatesBothControls() {
        var count by mutableStateOf(5)
        setThemedContent(stylePreset = StylePreset.Nova) { fixture(count = count) }
        repeat(3) {
            composeRule.onNodeWithContentDescription("Next slide").performClick()
            composeRule.waitForIdle()
        }
        composeRule.runOnIdle { count = 1 }
        composeRule.waitForIdle()
        composeRule.onNodeWithContentDescription("Next slide").assertIsNotEnabled()
        composeRule.onNodeWithContentDescription("Previous slide").assertIsNotEnabled()
        composeRule.runOnIdle { assertEquals(0, state.currentPage) }
    }
}
